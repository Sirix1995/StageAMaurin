
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.grogra;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.TreeIterator;
import de.grogra.turtle.Attributes;
import de.grogra.turtle.F;
import de.grogra.turtle.K;
import de.grogra.turtle.KAssignment;
import de.grogra.turtle.KL;
import de.grogra.turtle.Shoot;
import de.grogra.turtle.TurtleState;
import de.grogra.persistence.Transaction;
import de.grogra.pf.data.Dataseries;
import de.grogra.pf.data.Dataset;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.CommandItem;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.rgg.Library;
import de.grogra.rgg.model.RGGGraph;
import de.grogra.rgg.model.RGGProducer;
import de.grogra.rgg.model.Runtime;
import de.grogra.vecmath.CutConeParameter;
import de.grogra.vecmath.CutRay2Parameter;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Matrix34d;
import de.grogra.xl.lang.VoidConsumer;
import de.grogra.xl.util.Operators;

public class LSystem extends de.grogra.rgg.RGG
{
	public float angle = 90;
	//enh:field
	
	public int generation = 90;
	//enh:field
	

	/**
	 * Contains the value of GROGRA's register 0. 
	 */
	public float r0;

	/**
	 * Contains the value of GROGRA's register 1. 
	 */
	public float r1;

	/**
	 * Contains the value of GROGRA's register 2. 
	 */
	public float r2;

	/**
	 * Contains the value of GROGRA's register 3. 
	 */
	public float r3;

	/**
	 * Contains the value of GROGRA's register 4. 
	 */
	public float r4;

	/**
	 * Contains the value of GROGRA's register 5. 
	 */
	public float r5;

	/**
	 * Contains the value of GROGRA's register 6. 
	 */
	public float r6;

	/**
	 * Contains the value of GROGRA's register 7. 
	 */
	public float r7;

	/**
	 * Contains the value of GROGRA's register 8. 
	 */
	public float r8;

	/**
	 * Contains the value of GROGRA's register 9. 
	 */
	public float r9;
	
	// Holds the shoot population of a LSystem
	private Dataset shootPopulation = new Dataset();
	
	private int generationCounter;

	
	private TurtleState interpretationState = new TurtleState ();

	private Method derivation = null, interpretation = null;
	
	protected float[] defaultValuesForLocalRegisters;
	
	// Reference shoot for function 21 (is used in the local registers)
	private Shoot refShoot;
	
	/**
	 * Contains the current node which matched the left hand side of a rule,
	 * or which is the current node of an arithmetical-structural computation.
	 */
	public Node currentNode;
	
	private boolean derivationActive;

	private static final double D2R = Math.PI / 180, R2D = 1 / D2R;

	private static final int REG_ID = Registry.allocatePropertyId ();


	public class Apply extends de.grogra.rgg.RGG.Apply
	{
		private final int count;


		public Apply (boolean loop, boolean useRunCheckBox, int count)
		{
			super (null, loop, useRunCheckBox);
			this.count = count;
		}


		@Override
		protected boolean applyRules (Transaction t)
		{
			return LSystem.this.apply (count, t);
		}
	}


	@Override
	protected void reset ()
	{
		super.reset ();
		generation$FIELD.setInt (this, null, 0, getGraph ().getActiveTransaction ());
	}

	public LSystem() {
		super();
		shootPopulation.setTitle("shoot population");
		shootPopulation.setColumnKey (0, "Generation")
			.setColumnKey (1, "Shoots for color 1")
			.setColumnKey (2, "Shoots for color 2")
			.setColumnKey (3, "Shoots for color 3")
			.setColumnKey (4, "Shoots for color 4")
			.setColumnKey (5, "Shoots for color 5")
			.setColumnKey (6, "Shoots for color 6")
			.setColumnKey (7, "Shoots for color 7")
			.setColumnKey (8, "Shoots for color 8")
			.setColumnKey (9, "Shoots for color 9")
			.setColumnKey (10, "Shoots for color 10")
			.setColumnKey (11, "Shoots for color 11")
			.setColumnKey (12, "Shoots for color 12")
			.setColumnKey (13, "Shoots for color 13")
			.setColumnKey (14, "Shoots for color 14")
			.setColumnKey (15, "Shoots for color 15")
			.setColumnKey (16, "Shoots for color 16")
			.setColumnKey (17, "Shoots of other colors");
	}
	
	/**
	 * Returns a pseudo-random number which is uniformly distributed
	 * between <code>min</code> and <code>max</code>.
	 * 
	 * @param min minimum value 
	 * @param max maximum value
	 * @return pseudo-random number between <code>min</code> and <code>max</code>
	 */
	public static float uniform (float min, float max)
	{
		return Operators.getRandomGenerator ().nextFloat () * (max - min) + min;
	}

	/**
	 * Returns a pseudorandom number which is distributed according
	 * to a normal distribution with mean value <code>mu</code> and standard
	 * deviation <code>sigma</code>.
	 * 
	 * @param mu mean value
	 * @param sigma standard deviation
	 * @return normally distributed random number
	 */
	public static float normal (float mu, float sigma)
	{
		return mu + sigma * (float) Operators.getRandomGenerator ().nextGaussian ();
	}

	/**
	 * Returns a pseudo-random number which is uniformly distributed
	 * between 0 and 1.
	 * 
	 * @return pseudo-random number between 0 and 1
	 */
	public static float random ()
	{
		return Operators.getRandomGenerator ().nextFloat ();
	}


	/**
	 * This generator method yields <code>void</code> for every shoot
	 * of the subtree starting at the associated shoot of the
	 * {@link #currentNode}.  
	 * 
	 * @param c a consumer
	 */
	public void sumGenerator (VoidConsumer c)
	{
		Node cn = currentNode;
		if (cn == null)
		{
			return;
		}
		Shoot n = getAssociatedShoot (cn);
		TreeIterator it = new TreeIterator ((n != null) ? n : cn);
		try
		{
			while ((n = nextShoot (it)) != null)
			{
				currentNode = n;
				c.consume ();
			}
		}
		finally
		{
			currentNode = cn;
		}
	}


	/**
	 * This generator method yields <code>void</code> for every daughter shoot
	 * of the associated shoot of the {@link #currentNode}.  
	 * 
	 * @param c a consumer
	 */
	public void sumdGenerator (VoidConsumer c)
	{
		Node cn = currentNode;
		if (cn == null)
		{
			return;
		}
		try
		{
			sumdGenerator (getAssociatedShoot (cn), c);
		}
		finally
		{
			currentNode = cn;
		}
	}
	
	
	private void sumdGenerator (Node n, VoidConsumer c)
	{
		for (Edge e = n.getFirstEdge (); e != null; e = e.getNext (n))
		{
			Node m = e.getTarget ();
			if ((n != m) && e.testEdgeBits (Graph.BRANCH_EDGE
											| Graph.SUCCESSOR_EDGE))
			{
				if (m instanceof Shoot)
				{
					currentNode = m;
					c.consume ();
				}
				else
				{
					sumdGenerator (m, c);
				}
			}
		}
	}


	/**
	 * This generator method yields <code>void</code> for every shoot
	 * in the path from the associated shoot of the {@link #currentNode}
	 * downwards to the root.  
	 * 
	 * @param c a consumer
	 */
	public void sumpGenerator (VoidConsumer c)
	{
		Node cn = currentNode;
		if (cn == null)
		{
			return;
		}
		Shoot n = getAssociatedShoot (cn);
		try
		{
			while (n != null)
			{
				currentNode = n;
				c.consume ();
				n = getAssociatedShoot
					(n.findAdjacent (true, false,
									 Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE));
			}
		}
		finally
		{
			currentNode = cn;
		}
	}

	
	/**
	 * Invoked after a pattern of a rule has matched,
	 * but before the right-hand side of the rule is executed.
	 * 
	 * @param prod the producer which is used for the right-hand side
	 */
	public void patternMatched (RGGProducer prod)
	{
		currentNode = prod.producer$getLeftmostMatch ();
	}
	
	
	/**
	 * Returns the shoot which is associated with node <code>n</code>.
	 * The associated shoot is the first node of class {@link Shoot}
	 * which can be reached from <code>n</code> by traversing edges of type
	 * {@link Graph#BRANCH_EDGE} or {@link Graph#SUCCESSOR_EDGE} backwards,
	 * possibly <code>n</code> itself. If no such shoot is found or
	 * <code>n</code> is <code>null</code>, <code>null</code> is returned.
	 * 
	 * @param n a node
	 * @return the associated shoot
	 */
	private static Shoot getAssociatedShoot (Node n)
	{
		while ((n != null) && !(n instanceof Shoot))
		{
			n = n.findAdjacent (true, false, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE);
		}
		return (Shoot) n;
	}
	
	
	private static Shoot getAssociatedMotherShoot(Node n) {
		do {
			n = n.findAdjacent (true, false, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE);
		} while ((n != null) && !(n instanceof Shoot));
		return (Shoot) n;
	}

	
	/**
	 * Returns the next shoot of iterator <code>i</code>. This method
	 * iterates over <code>i</code> until an instance of
	 * {@link Shoot} is obtained, this is the returned value. If no such
	 * instance can be found, <code>null</code> is returned.
	 * 
	 * @param i a tree iterator
	 * @return the next shoot returend by <code>i</code>
	 */
	static Shoot nextShoot (TreeIterator i)
	{
		while (i.hasNext ())
		{
			Node n = i.nextNode ();
			if (n instanceof Shoot)
			{
				return (Shoot) n;
			}
		}
		return null;
	}

	
	/**
	 * Returns the global x-coordinate of the tip of the
	 * associated shoot of the {@link #currentNode}.
	 * 
	 * @return x-coordinate of current shoot
	 */
	public float currentXcoordinate ()
	{
		Shoot n = getAssociatedShoot (currentNode);
		if (n == null)
		{
			return 0;
		}
		Matrix34d m = Library.transformation (n);
		return (float) (m.m02 * n.getLength (n, n.getCurrentGraphState ())
						+ m.m03);
	}

	
	/**
	 * Returns the global y-coordinate of the tip of the
	 * associated shoot of the {@link #currentNode}.
	 * 
	 * @return y-coordinate of current shoot
	 */
	public float currentYcoordinate ()
	{
		Shoot n = getAssociatedShoot (currentNode);
		if (n == null)
		{
			return 0;
		}
		Matrix34d m = Library.transformation (n);
		return (float) (m.m12 * n.getLength (n, n.getCurrentGraphState ())
						+ m.m13);
	}

	
	/**
	 * Returns the global z-coordinate of the tip of the
	 * associated shoot of the {@link #currentNode}.
	 * 
	 * @return z-coordinate of current shoot
	 */
	public float currentZcoordinate ()
	{
		Shoot n = getAssociatedShoot (currentNode);
		if (n == null)
		{
			return 0;
		}
		Matrix34d m = Library.transformation (n);
		return (float) (m.m22 * n.getLength (n, n.getCurrentGraphState ())
						+ m.m23);
	}

	
	/**
	 * The GROGRA variable of type "length" returns the length
	 * of the associated shoot of the {@link #currentNode} if
	 * generative rules are active, or the value of <code>length</code>
	 * of the current turtle state if interpretative rules are active.
	 * 
	 * @return current length
	 */
	public float currentLength ()
	{
		if (derivationActive)
		{
			Shoot n = getAssociatedShoot (currentNode);
			return (n == null) ? 0
				: n.getLength (n, n.getCurrentGraphState ());
		}
		else
		{
			return interpretationState.length;
		}
	}

	static Matrix4d transformation (Node node)
	{
		Matrix4d m = new Matrix4d ();
		Library.transformation (node).get (m);
		return m;
	}

	public float function1(double minLength) {
		float result = 180.0f;
		float length = 0.0f;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);
		if(currentShoot == null)
			return result;
		
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		Shoot motherShoot = getAssociatedMotherShoot(currentNode);
		CutRay2Parameter crp = new CutRay2Parameter();
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		
//		double len = gs.getDouble(n,true,Attributes.LENGTH);
		
		Math2.getEndOfShoot(transformation(currentShoot), length, endOfCurrentShoot);
		
		while((n = nextShoot(it)) != null) {
			length = (float) gs.getDouble(n, true, Attributes.LENGTH);
			if((n != currentShoot) && (length >= minLength) && (n != motherShoot)) {
				Math2.getBeginAndEndOfShoot(transformation(n), length, beginOfShoot, endOfShoot);
				Math2.cutRay2((float)endOfCurrentShoot.x, (float)endOfCurrentShoot.z, 0.0f, 1.0f, (float)beginOfShoot.x,
							(float)beginOfShoot.z, (float)endOfShoot.x, (float)endOfShoot.z, crp);
				if(crp.isCorrect() && crp.isExists()) {
					result = 0.0f;
				} else {
					Matrix4d m = transformation (n);
					float current_angle = (float) (Math.acos (m.m22 / Math.sqrt (m.m02 * m.m02 + m.m12 * m.m12 + m.m22 * m.m22)) * R2D);
					if(current_angle < result)
						result = current_angle;
				}
			}
		}
		
		return result;
	}
	
	public float function2(double minLength, int color) {
		float result, inres;
		result = Float.MAX_VALUE;
		float length = 0.0f;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);
		if(currentShoot == null)
			return result;
		
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		Math2.getEndOfShoot(transformation(currentShoot), length, endOfCurrentShoot);
		Shoot motherShoot = getAssociatedMotherShoot(currentNode);
		
		while((n = nextShoot(it)) != null) {
			length = (float) gs.getDouble(n, true, Attributes.LENGTH);
			if((n != currentShoot) && (n != motherShoot) && (length >= minLength) && ((color < 0) || (gs.getInt(n, true, Attributes.DTG_COLOR) == color))) {
				Math2.getBeginAndEndOfShoot(transformation(n), length, beginOfShoot, endOfShoot);
				inres = Math2.abstpp(endOfCurrentShoot, endOfShoot);
				if(inres < result)
					result = inres;
				inres = Math2.abstpp(endOfCurrentShoot, beginOfShoot);
				if(inres < result)
					result = inres;
			}
		}
		return result;
	}
	

	/**
	 * GROGRA function 3 computes the sum of the <code>parameter</code>
	 * values of the turtle states of all <code>Shoot</code>s emerging
	 * from the {@link #currentNode}.
	 * 
	 * @return sum of <code>parameter</code> values
	 */
	public float function3 ()
	{
		Shoot n = getAssociatedShoot (currentNode);
		if (n == null)
		{
			return 0;
		}
		float sum = 0;
		GraphState gs = n.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (n);
		while ((n = nextShoot (it)) != null)
		{
			sum += gs.getFloat(n, true, Attributes.PARAMETER);
		}
		return sum;
	}

	public float function4(double alpha, double minLength) {
		float length = 0.0f;
		float result = 0.0f;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		if(currentShoot == null || (alpha < 0.0) || (alpha > 90.0) || (minLength < 0.0))
			return 0.0f;									// if there is no shoot
		
		if(minLength < Math2.EPS)
			minLength = Math2.EPS;
		
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		CutConeParameter ccp = new CutConeParameter();
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d beginOfShoot2 = new Vector3d();
		Matrix4d currentMatrix = transformation(currentShoot);
		
		while((n = nextShoot(it)) != null) {				// get next shoot by using the tree-iterator
			length = (float) gs.getDouble(n, true, Attributes.LENGTH);
			if(n != currentShoot && (length >= minLength)) {
				Math2.getBeginAndEndOfShoot(transformation(n), length, beginOfShoot, endOfShoot);
				Math2.getBeginOfShoot(currentMatrix, beginOfShoot2);
				Math2.cutCone(beginOfShoot2, (float) alpha, beginOfShoot, endOfShoot, ccp);
				if(ccp.isCorrect() && ccp.isExists())
					result += (ccp.getA() * gs.getFloat(n, true, Attributes.PARAMETER));
			}
		}
		return result;
	}
	
	public float function5(double alpha) {
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		Node root = getRoot();
		Shoot n, mother;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		float nad, dla, lp, currentLength;
		boolean found;
		dla = 0.0f;
		
		GraphState gsCurrent = currentShoot.getCurrentGraphState();
		currentLength = (float) gsCurrent.getDouble(currentShoot, true, Attributes.LENGTH);
		
		while((n = nextShoot(it)) != null) {
			nad = gs.getFloat(n, true, Attributes.PARAMETER);
			if(nad > 0.0f) {
				lp = 0.0f;
				found = false;
				mother = getAssociatedMotherShoot(n);
				while(mother != null) {
					lp += gs.getDouble(n, true, Attributes.LENGTH);
					if(mother == currentShoot)
						found = true;
					mother = getAssociatedMotherShoot(mother);
				}
				
				if(found && (lp > 0.0f)) {
					dla += ((float) alpha * nad * currentLength) / lp;
				}
			}
		}
		return dla;
	}
	
	/**
	 * GROGRA function 6 computes the sum of the <code>carbon</code>
	 * values of the turtle states of all <code>Shoot</code>s emerging
	 * from the {@link #currentNode}.
	 * 
	 * @return sum of <code>carbon</code> values
	 */
	public float function6() {
		Shoot n = getAssociatedShoot(currentNode);			// get associated shoot for the currentnode
		if(n == null)
			return 0.0f;									// if there is no shoot
		
		float sum = 0.0f;									// sum of all carbon-values from the currentnode and there childrens
		GraphState gs = GraphState.current(n.getGraph());	// get state of the graph for the currentnode
		TreeIterator it = new TreeIterator(n);				// associated tree-iterator with currentnode
		while((n = nextShoot(it)) != null)					// get next shoot by using the tree-iterator
			sum += TurtleState.getBefore(n, gs).carbon;		// get the turtle-state before the actualnode
		return sum;
	}
	
	/**
	 * GROGRA function 7 computes the angle (in degrees) between the
	 * global z-direction and the local
	 * z-direction of the {@link #currentNode}.
	 * 
	 * @return angle between local and global z-direction in degrees
	 */
	public float function7 ()
	{
		Shoot n = getAssociatedShoot (currentNode);
		if (n == null)
		{
			return 0;
		}
		Matrix4d m = transformation (n);
		return (float)
			(Math.acos (m.m22 / Math.sqrt (m.m02 * m.m02 + m.m12 * m.m12 + m.m22 * m.m22))
			 * R2D);
	}

	public float function8(int color) {
		short[] touchedsegs = new short[Math2.nbSkySegments];
		int segindx, nbtch, colorOfShoot;
		float currz, result;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		Math2.getEndOfShoot(transformation(currentShoot), currentShoot.getCurrentGraphState().getDouble(currentShoot, true, Attributes.LENGTH), endOfCurrentShoot);
		currz = (float) endOfCurrentShoot.z;
		
		while((n = nextShoot(it)) != null) {
			Math2.getBeginAndEndOfShoot(transformation(n), gs.getDouble(n, true, Attributes.LENGTH), beginOfShoot, endOfShoot);
			colorOfShoot = gs.getInt(n, true, Attributes.DTG_COLOR);
			
			if((n != currentShoot) && ((color > 0) || (colorOfShoot == color))) {
				if(beginOfShoot.z > currz) {
					beginOfShoot.sub(endOfCurrentShoot);
					segindx = Math2.skySegment(beginOfShoot);
					if ((segindx >= 0) && (segindx < Math2.nbSkySegments))
						touchedsegs[segindx] = 1;
				}
				if(endOfShoot.z > currz) {
					endOfShoot.sub(endOfCurrentShoot);
					segindx = Math2.skySegment(endOfShoot);
					if ((segindx >= 0) && (segindx < Math2.nbSkySegments))
						touchedsegs[segindx] = 1;
				}
			}
		}
		
		nbtch = 0;
		for (int i = 0; i < Math2.nbSkySegments; i++)
			nbtch += touchedsegs[i];
		result = ((float) nbtch) / (float) Math2.nbSkySegments;
		return result;
	}

	public float function9(double alpha, double minleng, int co) {
		float result, abstd, wink, length;
		result = Float.MAX_VALUE;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		
		if (currentShoot == null)
			return result;
		
		Vector3d temp;
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		Vector3d currentSh = new Vector3d();
		Matrix4d matrix = transformation(currentShoot);
		currentSh.x = matrix.m02;
		currentSh.y = matrix.m12;
		currentSh.z = matrix.m22;
		Math2.getEndOfShoot(matrix, currentShoot.getCurrentGraphState().getDouble(currentShoot, true, Attributes.LENGTH), endOfCurrentShoot);
		
		while((n = nextShoot(it)) != null) {
			length = (float) gs.getDouble(n, true, Attributes.LENGTH);
			Math2.getBeginAndEndOfShoot(transformation(n), length, beginOfShoot, endOfShoot);
			if((length >= minleng) && ((co < 0) || (gs.getInt(n, true, Attributes.DTG_COLOR) == co))) {
				temp = (Vector3d) beginOfShoot.clone();
				temp.sub(endOfCurrentShoot);
				wink = (float) Math.toDegrees(currentSh.angle(temp));
				if ((wink <= alpha) && (wink >= -alpha) && (n != currentNode)) {
					abstd = Math2.abstps(endOfCurrentShoot, beginOfShoot, endOfShoot);
					if (abstd < result)
				    	result = abstd;
				}
				
				temp = (Vector3d) endOfShoot.clone();
				temp.sub(endOfCurrentShoot);
				wink = (float) Math.toDegrees(currentSh.angle(temp));
				if ((wink <= alpha) && (wink >= -alpha) && (n != currentNode)) {
					abstd = Math2.abstps(endOfCurrentShoot, beginOfShoot, endOfShoot);
					if (abstd < result)
				    	result = abstd;
				}
			}
		}
		
		return result;
	}
	
	public static float function10 (float x)
	{
		return (int) (0.5 * x + 2.3 - Math.exp (0.022 * x + 0.06));
	}


	public static float function11 (float n, float k, float b, float c2,
									float d)
	{
		return ((d == 0) || (d == 1)) ? 0
			: (n < d * (n + k)) ? ((1 + 0.03f * k) * 1.1f * b * n / d)
			: ((1 + 0.03f * k) * 1.1f * ((b + c2 * d) * k / (1 - d) - c2 * n));
	}


	public static float function12 (float i, float age)
	{
		switch (Math.round (i))
		{
			case 1:
				return (age <= 20) ? 7 + 0.1f * age
					: (age <= 90) ? (3 + 0.3f * age)
					: (13f / 18) * age - 35;
			case 2:
				return (age <= 30) ? 7 + 0.1f * age
					: (age <= 90) ? 1.5f + (17f / 60) * age
					: (11f / 18) * age - 28;
			case 3:
				return (age <= 40) ? 7 + 0.1f * age
					: (age <= 90) ? (7f / 25) * age - 0.2f
					: 0.5f * age - 20;
			default:
				return 0;
		}
	}

	/**
	 * GROGRA function 13
	 * Calculation of number of lateral buds from shoot length
	 * @param xl the length of the shoot
	 * @param xf an (optional) conversion factor to be multiplied with xl^2
	 * @return
	 */
	public static float function13(double xl, double xf) {
		float wf;
		float lfactor = 8.17E-5f;
		
		if(xf < 0.0)
			wf = (float) xl * (float) xl * lfactor;
		else
			wf = (float) xl * (float) xl * (float) xf;
		
		if(wf < 0.0) {
			System.out.println("Warning: negative leaf mass in function 13 !");
			return 0.0f;
		}
		
		if(wf >= 9.0)
			return 7.0f;
		if(wf >= 7.0)
			return 6.0f;
		if(wf >= 5.0)
			return 5.0f;
		if(wf >= 3.0)
			return 4.0f;
		if(wf >= 2.0)
			return 3.0f;
		if(wf >= 1.0)
			return 2.0f;
		if(wf >= 0.5)
			return 1.0f;
		return 0.0f;
	}
	
	public float function15(int color) {
		short[] touchedsegs = new short[Math2.nbSkySegments];
		int segindx, colorOfShoot;
		float currz, weight, sumtouch, sumall, result;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		Math2.getEndOfShoot(transformation(currentShoot), currentShoot.getCurrentGraphState().getDouble(currentShoot, true, Attributes.LENGTH), endOfCurrentShoot);
		currz = (float) endOfCurrentShoot.z;
		
		while((n = nextShoot(it)) != null) {
			Math2.getBeginAndEndOfShoot(transformation(n), gs.getDouble(n, true, Attributes.LENGTH), beginOfShoot, endOfShoot);
			colorOfShoot = gs.getInt(n, true, Attributes.DTG_COLOR);
			
			if((n != currentShoot) && ((color > 0) || (colorOfShoot == color))) {
				if(beginOfShoot.z > currz) {
					beginOfShoot.sub(endOfCurrentShoot);
					segindx = Math2.skySegment(beginOfShoot);
					if ((segindx >= 0) && (segindx < Math2.nbSkySegments))
						touchedsegs[segindx] = 1;
				}
				if(endOfShoot.z > currz) {
					endOfShoot.sub(endOfCurrentShoot);
					segindx = Math2.skySegment(endOfShoot);
					if ((segindx >= 0) && (segindx < Math2.nbSkySegments))
						touchedsegs[segindx] = 1;
				}
			}
		}
		
		sumtouch = sumall = 0.0f;
		for(int i = 0; i < Math2.nbSkySegments; i++) {
			weight = (float) (Math2.turtsky[i].z / Math2.turtsky[i].length());
			sumall += weight;
			if(touchedsegs[i] > 0)
				sumtouch += weight;
		}
		result = sumtouch / sumall;
		
		return result;
	}
	
	public float function20(int color) {
		float phfakt_tak = 0.0002f;   /* preliminary value; 0.00018 */
		float costlmaint = 0.25f;  /* cost of leaf maintenance, 0.25 g/g/yr */
		float leaf_transm = 0.1f;  /* leaf transmittance */
		float leafcl_rad_f = 1.5f; /* leaf cluster radius factor */
		float lcapt;
		float[] dummy = new float[1];
		
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		Node root = getRoot();
		
		if((root == null) || (currentShoot == null))
			return 0.0f;
		
		lcapt = taklightcapt(root, currentShoot, leaf_transm, leafcl_rad_f, color, false, false, false, dummy);
		
		return phfakt_tak * lcapt - costlmaint * currentShoot.getCurrentGraphState().getFloat(currentShoot, true, Attributes.PARAMETER);
	}
	
	private float taklightcapt(Node root, Shoot currentShoot, float ltransm, float lclrad, int color, boolean flrel, boolean fllen, boolean flmt, float[] relcapt) {
		float sumcapt, csumc, labsorb, lrad, lproj, hval, tval, length;
		float leafar_p_w = 10000; /* leaf area per weight, in mm^2 / g */
		float lightintens = 1.0f;   /* light flux density, preliminary */
		int nbc;
		Shoot exs;
		relcapt[0] = 0.0f;
		GraphState gsC = currentShoot.getCurrentGraphState();
		
		if(currentShoot == null)
			return 0.0f;
		
		if (flmt)
			exs = getAssociatedMotherShoot(currentShoot);
		else
			exs = null;
		
		sumcapt = 0.0f;
		csumc = 0.0f;
		labsorb = (float) (1. - (1. - (1.-ltransm)/(lclrad*lclrad)) * (1. - (1.-ltransm)/(lclrad*lclrad)));
		lrad = (float) (0.5 * Math.sqrt((leafar_p_w * gsC.getFloat(currentShoot, true, Attributes.PARAMETER))/Math2.M_PI));
		hval = lrad * lclrad;
		hval = hval * hval * (float) Math2.M_PI * labsorb;
		
		length = (float) gsC.getDouble(currentShoot, true, Attributes.LENGTH);
		Vector3d currentSh = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		Matrix4d matrix = transformation(currentShoot);
		currentSh.x = matrix.m02;
		currentSh.y = matrix.m12;
		currentSh.z = matrix.m22;
		Math2.getEndOfShoot(matrix, length, endOfCurrentShoot);
		
		for(int i = 0; i <= 45; i++) {
			if(fllen) {
				lproj = length * (float) Math.sin(Math2.turtsky[i].angle(currentSh));
				hval += 2 * lproj * lrad * lclrad * labsorb;
			}
			nbc = nbcuts(root, endOfCurrentShoot, Math2.turtsky[i], color, currentShoot, exs);
			tval = (float) (lightintens * (1. + 2.*Math2.turtsky[i].z)/3.);
			if(flrel)
				csumc += (tval * hval);
			for(int j = 0; j < nbc; j++)
				tval *= (1. - labsorb);
			sumcapt += (tval * hval);
		}
		
		if (flrel && (csumc > 0.))
			relcapt[0] = sumcapt / csumc;
		
		return sumcapt;
	}
	
	private int nbcuts(Node root, Vector3d pos, Vector3d dir, int color, Shoot exsp1, Shoot exsp2) {
		int res = 0;
		float dist;
		
		if(Math2.isNullVector(dir))
			return 0;
		
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		
		while((n = nextShoot(it)) != null) {
			if(((color < 0) || (color == gs.getInt(n, true, Attributes.DTG_COLOR))) && (n != exsp1) && (n != exsp2)) {
				Math2.getBeginAndEndOfShoot(transformation(n), gs.getDouble(n, true, Attributes.LENGTH), beginOfShoot, endOfShoot);
				dist = Math2.absthgs(pos, dir, beginOfShoot, endOfShoot);
				if (dist <= 0.5 * TurtleState.getBefore(n, gs).diameter)
					res++;
			}
		}
		
		return res;
	}
	
	public float function21(int color) {
		float mlen, dist, length;
		mlen = 0.0f;
		
		Shoot currentShoot = getAssociatedShoot(currentNode);// get associated shoot for the currentnode
		Node root = getRoot();
		Shoot n;
		GraphState gs = GraphState.current(root.getGraph());// get state of the graph for the root
		TreeIterator it = new TreeIterator(root);			// associated tree-iterator with root
		
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		Vector3d endOfCurrentShoot = new Vector3d();
		Math2.getEndOfShoot(transformation(currentShoot), currentShoot.getCurrentGraphState().getDouble(currentShoot, true, Attributes.LENGTH), endOfCurrentShoot);
		
		while((n = nextShoot(it)) != null) {
			if((color < 0) || (gs.getInt(n, true, Attributes.DTG_COLOR) == color)) {
				length = (float) gs.getDouble(n, true, Attributes.LENGTH);
				Math2.getBeginAndEndOfShoot(transformation(n), length, beginOfShoot, endOfShoot);
				
				dist = Math2.abstps(endOfCurrentShoot, beginOfShoot, endOfShoot);
				if(dist < length) {
					if(length > mlen) {
						mlen = length;
						refShoot = n;
					}
				}
			}
		}
		
		return mlen;
	}
	
	/**
	 * Auxilliary function for control purposes. Write the arguments to the console.
	 * @param arg1 First argument
	 * @param arg2 Second argument
	 * @param arg3 third argument
	 * @return Constant 0.0f
	 */
	public float function30(float arg1, float arg2, float arg3) {
		System.out.println(" funct30    "+arg1+"    "+arg2+"    "+arg3);
		return 0.0f;
	}
	
	/*
	 * RESERVE FUNCTIONS:-------------------------------------------------
	 */
	
	public float function31(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function32(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function33(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function34(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function35(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function36(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function37(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function38(float arg1, float arg2, float arg3) {
		return 0.0f;
	}
	
	public float function39(float arg1, float arg2, float arg3) {
		return 0.0f;
	}

	/*
	 * -------------------------------------------------------------------
	 */
	
	/**
	 * Computated the distribution of assimilate in a simple plant-model.
	 * Use the global registers 1 - 4.
	 * (non-realistic example, only for demonstration)
	 */
	public void method1() {
		float maxInvest = 300.0f;
		float maxLength = 100.0f;
		float factor1 = 1.0f;
		float factor2 = 5.0f;
		
		float assimResult;
		
		if(r4 >= 0.0f) {
			System.out.println("r1: "+r1);
			System.out.println("r2: "+r2);
			System.out.println("r3: "+r3);
			System.out.println("r4: "+r4);
			
			/* Calculation of the actual available reserve: */
			
			assimResult = r3 - factor1 * r1 + factor2 * r2;
			
			if(assimResult <= 0.0f)
				r4 = -1.0f;
			else {
				if(assimResult > maxInvest) {
					r3 = assimResult - maxInvest;
					r4 = maxLength;
				} else {
					r3 = 0.0f;
					r4 = maxLength * (assimResult / maxInvest);
				}
			}
		}
		r1 = r2 = 0.0f;
	}
	
	/**
	 * Write the current-structure in a binary-file strutemp.dat.
	 * WARNING: The stored structured is using big endian for coding the bytes (highest byte first).
	 * The class RandomAccessFile does not support little endian coding for binary-files.
	 * Big endian is normally used on UNIX-systems.
	 * All int-values are stored as 16-bit-values, for the compatibility to system where int-values
	 * have a range of 16-bits and for the compatibility to GroGra-files.
	 * All long-values are stored as 32-bit-values.
	 * Additional ... there could be problems with the floating point values ... it's in the Java-format.
	 * The GroGra auxiliary-variables yka, xke and yke will saved with the value 0.
	 * The GroGra auxiliary-variable xka will saved with the carbon-value.
	 * The GroGra scale-counter-variable is currently saved with the value 0.
	 * The local registers of a shoot are not saved (now).
	 */
	public void method4() {
		Node root = getRoot();
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, mother;
		float length;
		
		Vector3d shootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
		Matrix4d matrix;
		
		RandomAccessFile out = null;
		
		try {
			out = new RandomAccessFile("strutemp.dat", "rw");
			
			while((s = LSystem.nextShoot (it)) != null) {
				length = (float) gs.getDouble(s, true, Attributes.LENGTH);
				matrix = transformation(s);
				Math2.getBeginAndEndOfShoot(matrix, length, shootBegin, shootEnd);
				
				out.writeShort(generationCounter+1);							// ib+1
				out.writeInt((int) s.getId());									// nr
				mother = getAssociatedMotherShoot(s);
				out.writeInt((int) (mother != null ? mother.getId() : -2));		// slauf->mutter->nr
				out.writeFloat(length);											// laenge
				out.writeFloat(TurtleState.getBefore(s, gs).heartwood);			// adur
				out.writeFloat(TurtleState.getBefore(s, gs).diameter);			// edur
				out.writeFloat(gs.getFloat(s, true, Attributes.PARAMETER));		// nad
				out.writeShort(TurtleState.getBefore(s, gs).internodeCount);	// izahl
				out.writeShort(gs.getInt(s, true, Attributes.DTG_COLOR));		// farbe
				out.writeShort(gs.getInt(s, true, Attributes.ORDER));			// or
				out.writeShort(0);	// SCALE INDEX --> GroGra sca
				out.writeInt(gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE)); // gen
				out.writeFloat(gs.getFloat(s, true, Attributes.REL_POSITION));	// q
				out.writeFloat((float) shootBegin.x); 							// panf
				out.writeFloat((float) shootBegin.y);
				out.writeFloat((float) shootBegin.z);
				out.writeFloat((float) shootEnd.x);								// pend
				out.writeFloat((float) shootEnd.y);
				out.writeFloat((float) shootEnd.z);
				out.writeFloat((float) matrix.m02);								// sh
				out.writeFloat((float) matrix.m12);
				out.writeFloat((float) matrix.m22);
				out.writeFloat((float) matrix.m00);								// sl
				out.writeFloat((float) matrix.m10);
				out.writeFloat((float) matrix.m20);
				out.writeFloat((float) matrix.m01);								// su
				out.writeFloat((float) matrix.m11);
				out.writeFloat((float) matrix.m21);
				out.writeInt(-2);												// vnr
				out.writeFloat(TurtleState.getBefore(s, gs).carbon);			// xka
				out.writeFloat(0);												// yka
				out.writeFloat(0);												// xke
				out.writeFloat(0);												// yke
				// local registers have to be stored here
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Interface to branch library of TRAGIC++ (BITOEK, XI/1998)
	 */
	public void method10() {
		float maxxv, sumle1, sumna, maxqf, maxy, miny, length, diameter;
		int exna, order;
		long nbspr;
		
		Node root = getRoot();
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		
		Vector3d shootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter("bt"+(new Date()).getTime()+".dtt", true)));
			
			maxxv = sumle1 = sumna = maxqf = maxy = miny = 0.0f;
			nbspr = 0;
			
			while((s = LSystem.nextShoot (it)) != null) {
				nbspr++;
				length = (float) gs.getDouble(s, true, Attributes.LENGTH);
				order = gs.getInt(s, true, Attributes.ORDER);
				diameter = TurtleState.getBefore(s, gs).diameter;
				Math2.getEndOfShoot(transformation(s), length, shootEnd);
				
				if(shootEnd.x > maxxv)
					maxxv = (float) shootEnd.x;
				if(order == 1)
					sumle1 += length;
				sumna += gs.getFloat(s, true, Attributes.PARAMETER);
				if((order == 1) && (diameter > maxqf))
					maxqf = diameter;
				if(shootEnd.y < miny)
					miny = (float) shootEnd.y;
				if(shootEnd.y > maxy)
					maxy = (float) shootEnd.y;
			}
			
			maxqf = (float) (Math2.M_PI * 0.25 * maxqf * maxqf);
			
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			DecimalFormat df12_3 = new DecimalFormat("############.000", dfs);
			DecimalFormat df12_5 = new DecimalFormat("############.00000", dfs);
			DecimalFormat df15_7 = new DecimalFormat("###############.0000000", dfs);
			DecimalFormat df15_3 = new DecimalFormat("###############.000", dfs);
			DecimalFormat df10 = new DecimalFormat("##########", dfs);
			DecimalFormat df5 = new DecimalFormat("#####", dfs);
			
			out.print(df12_3.format(maxxv)+"  "+df12_5.format(sumle1)+"  "+df15_7.format(sumna)+"  "+df15_3.format(maxqf)+"  "+df12_3.format(maxy-miny)+"  "+df10.format(nbspr)+"\n\n");
			
			it = new TreeIterator(root);
			while((s = LSystem.nextShoot (it)) != null) {
				Math2.getBeginAndEndOfShoot(transformation(s), gs.getDouble(s, true, Attributes.LENGTH), shootBegin, shootEnd);
				exna = 0;
				
				if(gs.getFloat(s, true, Attributes.PARAMETER) > Math2.EPSILON)
					exna = 1;
				
				out.print(df12_3.format(shootBegin.x)+"  "+df12_3.format(shootBegin.y)+"  "+df12_3.format(shootBegin.z)+"  "+df12_3.format(shootEnd.x)+"  "+df12_3.format(shootEnd.y)+"  "+df12_3.format(shootEnd.z)+"  "+df5.format(exna)+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
	
	/**
	 * Writes all carbon-values of shoots with the color 14 to method12.dat
	 */
	public void method12() {
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter("method12.dat", true)));
			
			Node root = getRoot();
			GraphState gs = root.getCurrentGraphState();
			TreeIterator it = new TreeIterator(root);
			Shoot s;
			
			while((s = LSystem.nextShoot (it)) != null) {
				if(gs.getInt(s, true, Attributes.DTG_COLOR) == 14) {
					out.print(TurtleState.getBefore(s, gs).carbon+" ");
				}
			}
			
			out.print("\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
	
	/**
	 * Auxiliary method for control purposes: Scans through the whole structure, finds trees, whorls and branches.
	 */
	public void method30() {
		Node root = getRoot();
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, mother;
		
		int order;
		
		while((s = LSystem.nextShoot (it)) != null) {
			if(gs.getInt(s, true, Attributes.ORDER) != 0)
				continue;
			
			order = gs.getInt(s, true, Attributes.ORDER);
			
			while(s != null && order == 0) {
				System.out.println("A shoot of a stem "+order);
				mother = s;
				s = LSystem.nextShoot(it);
				order = gs.getInt(s, true, Attributes.ORDER);
				
				while(s != null && ((order == 1) || (order == -20))) {
					System.out.println("A branch is found "+order);
					
					s = LSystem.nextShoot(it);
					while(s != null && getAssociatedMotherShoot(s) != mother) {
						/* do something with other shoots of a branch */
						
						s = LSystem.nextShoot(it);
					}
				}
			}
			System.out.println("Whole tree scanned");
			System.out.println();
		}
		System.out.println("Whole structure scanned");
	}
	
	/**
	 * Creates boxes for every branch and calculates different kind of
	 * information of shoots, which belong to different boxes.
	 * (These boxes are used, when shading, photosynthesis,
	 * formation of new shoots etc. is calculated)
	 */
	public void method32() {
		float minx, maxx, minz, maxz, miny, maxy;
	    int order;
	    float dimension;
	    boolean brfound;
	    
	    Node root = getRoot();
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, mother;
		
		Vector3d shootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
		
		while((s = LSystem.nextShoot (it)) != null) {
			if(gs.getInt(s, true, Attributes.ORDER) != 0)
				continue;
			
			order = gs.getInt(s, true, Attributes.ORDER);
			
			while(s != null && order == 0) {
				mother = s;
				s = LSystem.nextShoot (it);
				order = gs.getInt(s, true, Attributes.ORDER);
				
				while(s != null && ((order == 1) || (order == -20))) {
					minx = Float.MAX_VALUE;
					maxx = -Float.MAX_VALUE;
					minz = Float.MAX_VALUE;
					maxz = -Float.MAX_VALUE;
					miny = Float.MAX_VALUE;
					maxy = -Float.MAX_VALUE;
					brfound = false;
					
					if(order > 0) {
						brfound = true;
						Math2.getBeginAndEndOfShoot(transformation(s), gs.getDouble(s, true, Attributes.LENGTH), shootBegin, shootEnd);
						maxx = (float) Math.max(Math.max(maxx, shootBegin.x), shootEnd.x);
						maxy = (float) Math.max(Math.max(maxy, shootBegin.y), shootEnd.y);
						maxz = (float) Math.max(Math.max(maxz, shootBegin.z), shootEnd.z);
						minx = (float) Math.min(Math.min(minx, shootBegin.x), shootEnd.x);
						miny = (float) Math.min(Math.min(miny, shootBegin.y), shootEnd.y);
						minz = (float) Math.min(Math.min(minz, shootBegin.z), shootEnd.z);
					}
					
					s = LSystem.nextShoot(it);
					while(s != null && getAssociatedMotherShoot(s) != mother) {
						if(gs.getInt(s, true, Attributes.ORDER) > 0) {
							brfound = true;
							Math2.getBeginAndEndOfShoot(transformation(s), gs.getDouble(s, true, Attributes.LENGTH), shootBegin, shootEnd);
							maxx = (float) Math.max(Math.max(maxx, shootBegin.x), shootEnd.x);
							maxy = (float) Math.max(Math.max(maxy, shootBegin.y), shootEnd.y);
							maxz = (float) Math.max(Math.max(maxz, shootBegin.z), shootEnd.z);
							minx = (float) Math.min(Math.min(minx, shootBegin.x), shootEnd.x);
							miny = (float) Math.min(Math.min(miny, shootBegin.y), shootEnd.y);
							minz = (float) Math.min(Math.min(minz, shootBegin.z), shootEnd.z);
						}
						
						s = LSystem.nextShoot(it);
					}
					
					if (brfound) {
						dimension = Math.max(maxx-minx, Math.max(maxz-minz, maxy-miny));
						if (dimension != 0) {
							System.out.println("oksakohtainen: dimension="+dimension);
							//System.out.println("X: "+(maxx-minx)+"    Z: "+(maxz-minz)+"    Y: "+(maxy-miny));
						} else
							/* (self)pruned branch */
							System.out.println("oksa karsittu");
				    }
					
					order = gs.getInt(s, true, Attributes.ORDER);
				}
				System.out.println();
			}
		}
		System.out.println("Whole structure scanned");
	}
	
	/*
	 * RESERVE METHODS:---------------------------------------------------
	 */
	
	public void method31() {}
	public void method33() {}
	public void method34() {}
	public void method35() {}
	public void method36() {}
	public void method37() {}
	public void method38() {}
	public void method39() {}
	public void method40() {}
	public void method41() {}
	public void method42() {}
	public void method43() {}
	public void method44() {}
	
	/*
	 * -------------------------------------------------------------------
	 */

	public Dataset getShootPopulation() {
		return shootPopulation;
	}
	
	public Shoot getRefShoot() {
		return refShoot;
	}
	
	public int getGenerationNo() {
		return generationCounter -1;
	}
	
	public static float floor (float x)
	{
		return (float) Math.floor (x);
	}


	public static float exp (float x)
	{
		return (float) Math.exp (x);
	}


	public static float log (float x)
	{
		return (float) Math.log (x);
	}


	public static float sqr (float x)
	{
		return x * x;
	}


	public static float sqrt (float x)
	{
		return (float) Math.sqrt (x);
	}


	public static float atan (float x)
	{
		return (float) Math.atan (x);
	}


	public static float atg (float x)
	{
		return (float) (R2D * Math.atan (x));
	}


	public static int round (float x)
	{
		return Math.round (x);
	}


	public static float min (float a, float b)
	{
		return Math.min (a, b);
	}


	public static float max (float a, float b)
	{
		return Math.max (a, b);
	}


	public static LSystem get (Registry r)
	{
		return (LSystem) r.getUserProperty (REG_ID);
	}


	public static LSystem current ()
	{
		return get (Registry.current ());
	}


	@Override
	protected void startup ()
	{
		getRegistry ().setUserProperty (REG_ID, this);
		super.startup ();
	}


	@Override
	protected void shutdown ()
	{
		super.shutdown ();
		getRegistry ().setUserProperty (REG_ID, null);
	}


	public void derivation ()
	{
		if (derivation == null)
		{
			derivation = Reflection.getDeclaredMethod (getNType (),
													   "derivation");
		}
		try
		{
			derivation.invoke (this, null);
		}
		catch (IllegalAccessException e)
		{
			Workbench.log (e.getCause ());
		}
		catch (InvocationTargetException e)
		{
			Workbench.log (e.getCause ());
		}
	}


	public void interpretation ()
	{
		if (interpretation == null)
		{
			interpretation = Reflection.getDeclaredMethod
				(getNType (), "interpretation");
		}
		try
		{
			interpretation.invoke (this, null);
		}
		catch (IllegalAccessException e)
		{
			Workbench.log (e.getCause ());
		}
		catch (InvocationTargetException e)
		{
			Workbench.log (e.getCause ());
		}
	}

	@Override
	protected void initializeApplyMenu (Item d, boolean flat, boolean useRunCheckBox)
	{
		d.add (new CommandItem ("Rules", new Apply (false, useRunCheckBox, 1)));
	}


	@Override
	protected void initializeRunMenu (Item d, boolean flat, boolean useRunCheckBox)
	{
		d.add (new CommandItem ("Run Rules", new Apply (true, useRunCheckBox, 1)));
	}


	final boolean apply (int count, Transaction t)
	{
		return apply (((GraphManager) getPersistenceManager ()).getRoot (), count, t);
	}


	private boolean apply (Node root, int count, Transaction t)
	{
		RGGGraph ex = Runtime.INSTANCE.currentGraph ();
	
		boolean modified = false;
		while (count-- > 0)
		{
			long s = ex.derive ();

			derivationActive = true;
			currentNode = null;
			derivation ();
			ex.removeInterpretiveNodesOnDerivation ();
			modified = ex.derive () > s;

			derivationActive = false;
			currentNode = null;
			int old = ex.getDerivationMode ();
			ex.setDerivationMode (old | RGGGraph.INTERPRETIVE_FLAG);
			try
			{
				interpretation ();
			}
			finally
			{
				ex.setDerivationMode (old);
			}
			ex.derive ();
			// the complete step consisting of derivation and interpretation
			// has been performed

			// Saving the current shoot population in a Dataset-object
			int[] shootsCounter = new int[17];
			
			Node rootN = getRoot();
			GraphState gs = rootN.getCurrentGraphState();
			TreeIterator it = new TreeIterator(rootN);
			Shoot n;
			int color;
			Node node = getRoot();
			HashMap<KAssignment, float[]> savedReferences = new HashMap<KAssignment, float[]>();
			float[] localRegisters;
			
			// Save references of associated shoots of the K=-node
			while((node = node.findAdjacent(false, true, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE)) != null) {
				if(node instanceof KAssignment) {
					n = getAssociatedShoot(node);
					localRegisters = n.getLocalRegisters();
					if(localRegisters != null)
						savedReferences.put((KAssignment) node, localRegisters);
				}
			}
			
			// Count shoots for shoot-population and remove all local registers
			while((n = LSystem.nextShoot(it)) != null) {
				n.setLocalRegisters(null);
				color = gs.getInt(n, true, Attributes.DTG_COLOR);
				if(color > -1 && color < 16)
					shootsCounter[color]++;
				else
					shootsCounter[16]++;
			}
			
			Dataseries row = shootPopulation.addRow();
			row.set(0, generationCounter);
			for(int i = 1; i < 18; i++)
				row.set(i, shootsCounter[i-1]);
			
			generationCounter++;
			
			node = getRoot();
			
			// Do assignment of local registers
			while((node = node.findAdjacent(false, true, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE)) != null) {
				if(node instanceof K) {
					n = getAssociatedShoot(node);
					localRegisters = n.getLocalRegisters();
					if(localRegisters != null) {
						localRegisters[((K)node).getArgument()] = defaultValuesForLocalRegisters[((K)node).getArgument()];
					} else {
						localRegisters = new float[defaultValuesForLocalRegisters.length];
						for(int i = 0; i < localRegisters.length; i++)
							localRegisters[i] = Float.NaN;
						localRegisters[((K)node).getArgument()] = defaultValuesForLocalRegisters[((K)node).getArgument()];
					}
				} else if(node instanceof KL) {
					n = getAssociatedShoot(node);
					localRegisters = n.getLocalRegisters();
					GraphState gsN = n.getCurrentGraphState();
					if(localRegisters != null) {
						localRegisters[((KL)node).getArgument()] = (float) gsN.getDouble(n, true, Attributes.LENGTH);
					} else {
						localRegisters = new float[defaultValuesForLocalRegisters.length];
						for(int i = 0; i < localRegisters.length; i++)
							localRegisters[i] = Float.NaN;
						localRegisters[((KL)node).getArgument()] = (float) gsN.getDouble(n, true, Attributes.LENGTH);
					}
				} else if(node instanceof KAssignment) {
					n = getAssociatedShoot(node);
					localRegisters = n.getLocalRegisters();
					if(localRegisters != null) {
						localRegisters[((KAssignment)node).getArgument()] = savedReferences.get(node)[((KAssignment)node).getArgument()];
					} else {
						localRegisters = new float[defaultValuesForLocalRegisters.length];
						for(int i = 0; i < localRegisters.length; i++)
							localRegisters[i] = Float.NaN;
						localRegisters[((KAssignment)node).getArgument()] = savedReferences.get(node)[((KAssignment)node).getArgument()];
					}
				}
			}
			
			generation$FIELD.setInt (this, null, generation + 1, t);
		}
		ex.derive ();
		return modified;
	}

	/**
	 * Go back to the next shoot with the given localregisterno and assign the given value
	 * @param nr the localregisterno
	 * @param value the value
	 */
	protected void assignLocalRegister(int no, float value) {
		float[] localRegisters;
		Shoot mother = getAssociatedShoot(currentNode);
		
		while(mother != null) {
			if(mother instanceof F) {
				localRegisters = mother.getLocalRegisters();
				if(localRegisters[no] == localRegisters[no]) {		// if value is NOT NaN
					localRegisters[no] = value;
					break;
				}
			}
			mother = getAssociatedShoot(mother);
		}
	}
	
	/**
	 * Go back to the next shoot with the given localregisterno and add the given value
	 * @param nr the localregisterno
	 * @param value the value
	 */
	protected void assignLocalRegisterAdd(int no, float value) {
		float[] localRegisters;
		Shoot mother = getAssociatedShoot(currentNode);
		
		while(mother != null) {
			if(mother instanceof F) {
				localRegisters = mother.getLocalRegisters();
				if(localRegisters[no] == localRegisters[no]) {		// if value is NOT NaN
					localRegisters[no] += value;
					break;
				}
			}
			mother = getAssociatedShoot(mother);
		}
	}
	
	/**
	 * Go back to the next shoot with the given localregisterno and multiply the given value
	 * @param nr the localregisterno
	 * @param value the value
	 */
	protected void assignLocalRegisterMul(int no, float value) {
		float[] localRegisters;
		Shoot mother = getAssociatedShoot(currentNode);
		
		while(mother != null) {
			if(mother instanceof F) {
				localRegisters = mother.getLocalRegisters();
				if(localRegisters[no] == localRegisters[no]) {		// if value is NOT NaN
					localRegisters[no] *= value;
					break;
				}
			}
			mother = getAssociatedShoot(mother);
		}
	}
	
	/**
	 * Go to the reference shoot (select by function21) and assign the localregisterno with the given value
	 * @param nr the localregisterno
	 * @param value the value
	 */
	protected void assignReferenceShoot(int no, float value) {
		if(refShoot != null) {
			float[] localRegisters = refShoot.getLocalRegisters();
			
			if(localRegisters[no] == localRegisters[no]) {		// if value is NOT NaN
				localRegisters[no] = value;
			}
		}
	}
	
	/**
	 * Go to the reference shoot (select by function21) and add the localregisterno with the given value
	 * @param nr the localregisterno
	 * @param value the value
	 */
	protected void assignReferenceShootAdd(int no, float value) {
		if(refShoot != null) {
			float[] localRegisters = refShoot.getLocalRegisters();
			
			if(localRegisters[no] == localRegisters[no]) {		// if value is NOT NaN
				localRegisters[no] += value;
			}
		}
	}
	
	/**
	 * Go to the reference shoot (select by function21) and multiply the localregisterno with the given value
	 * @param nr the localregisterno
	 * @param value the value
	 */
	protected void assignReferenceShootMul(int no, float value) {
		if(refShoot != null) {
			float[] localRegisters = refShoot.getLocalRegisters();
			
			if(localRegisters[no] == localRegisters[no]) {		// if value is NOT NaN
				localRegisters[no] *= value;
			}
		}
	}
	
	/**
	 * Method is called, when the value of a localregister is needed
	 * @param no number of the localregister
	 * @return the value
	 */
	protected float getLocalRegisterValue (int no) {
		float[] localRegisters;
		Shoot s = getAssociatedShoot(currentNode);
		
		while(s != null) {
			localRegisters = s.getLocalRegisters();
			if(localRegisters != null) {
				if(localRegisters[no] == localRegisters[no])
					return localRegisters[no];
			}
			s = getAssociatedShoot(s);
		}
		return 0.0f;
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field angle$FIELD;
	public static final NType.Field generation$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (LSystem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((LSystem) o).generation = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 1:
					return ((LSystem) o).generation;
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((LSystem) o).angle = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((LSystem) o).angle;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new LSystem ());
		$TYPE.addManagedField (angle$FIELD = new _Field ("angle", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (generation$FIELD = new _Field ("generation", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new LSystem ();
	}

//enh:end

}
