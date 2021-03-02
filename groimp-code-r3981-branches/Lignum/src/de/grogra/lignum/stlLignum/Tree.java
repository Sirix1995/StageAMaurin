/*
 * Copyright (C) 2016 GroIMP Developer Team
 *
 * Department Ecoinformatics, Biometrics and Forest Growth,
 * University of Göttingen, Germany
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */ 

package de.grogra.lignum.stlLignum;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.sky.FirmamentWithMask;
import de.grogra.lignum.stlVoxelspace.DumpCFTreeFunctor;
import de.grogra.pf.ui.Workbench;
import de.grogra.rgg.Library;
import de.grogra.xl.util.ObjectList;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */
public class Tree extends TreeCompartment {

	// TODO rewrite the Tree class!
	// Firmament is no more inside the tree, axis (and lists) should not be used (but graph)
	protected FirmamentWithMask f = new FirmamentWithMask();
	private Axis axis;
	
	protected TreeParameters parameters = null;
	
	// TODO add field set, get methods
	
	/**
	 * Variable to balance carbon balance equation
	 */
	protected double Treelambda = 0;
	//enh:field getter setter
	
	/**
	 * Longest branch
	 */
	protected double Treelb = 0;
	
	/**
	 * Tree level photosynthesis
	 */
	protected double TreeP = 0;
	
	/**
	 * Tree level respiration
	 */
	protected double TreeM = 0;
	
	/**
	 * Root mass
	 */
	protected double TreeWr = 0;
	
	/**
	 * Max Qin of all segments in a tree
	 */
	protected double TreeQinMax = 0;
	
	/**
	 * Variable that is used as reference radiation 
	 * in calculations in tree (e.g. length growth)
	 */
	protected double TreeRefRadiation = 0;
	
	
	public Tree() {
		super();
	}
	
	public Tree(TreeParameters parameters) {
		this.parameters = parameters;
	}
	
//	public FirmamentWithMask GetFirmament() {
//		return this.f;
//	}
	
	//includes forAllTreeCompartments
	public void forEachDumpCFTreeFunctor(Tree tree, DumpCFTreeFunctor s_e){
		/*
		Axis axis = GetAxis(tree);
		
		//TreeCompartments need to be CfTreeSegments, otherwise this method does not work
		LinkedList<TreeCompartment> tc_ls = axis.GetTreeCompratmentList();
		
		ListIterator<TreeCompartment> iterator = tc_ls.listIterator();
		
		while(iterator.hasNext()){
			s_e.eval((CfTreeSegment)iterator.next());
		}
		*/
		Workbench w = Library.workbench ();
		if (w == null)
		{
			return;
		}
		GraphManager g = w.getRegistry ().getProjectGraph ();
		
		HashMap<Node, String> visited = new HashMap<Node, String> ();
		ObjectList<Node> toVisit = new ObjectList<Node> ();
		append (g.getRoot (), null, false, visited, toVisit);
		while (!toVisit.isEmpty ())
		{
			Node n = toVisit.pop ();
			s_e.eval((CfTreeSegment)n);
		}
	}
	
	//includes forAllTreeCompartments
	public void forEachShadingEffectOfCfTreeSegment(Tree tree, ShadingEffectOfCfTreeSegment s_e){
//		Axis axis = GetAxis(tree);
//		
//		//TreeCompartments need to be CfTreeSegments, otherwise this method does not work
//		LinkedList<TreeCompartment> tc_ls = axis.GetTreeCompratmentList();
//		
//		ListIterator<TreeCompartment> iterator = tc_ls.listIterator();
//		
//		while(iterator.hasNext()){
//			s_e.eval((CfTreeSegment)iterator.next());
//		}
		
		Workbench w = Library.workbench ();
		if (w == null)
		{
			return;
		}
		GraphManager g = w.getRegistry ().getProjectGraph ();
		
		HashMap<Node, String> visited = new HashMap<Node, String> ();
		ObjectList<Node> toVisit = new ObjectList<Node> ();
		append (g.getRoot (), null, false, visited, toVisit);
		while (!toVisit.isEmpty ())
		{
			Node n = toVisit.pop ();
			s_e.eval((CfTreeSegment)n);
		}
		
	}
		
	private static void append (Node node, Edge edge, boolean branch,
			HashMap<Node, String> visited, ObjectList<Node> toVisit)
	{
		String s = visited.get (node);
		if (s == null)
		{
			int inCount = 0;
			int outCount = 0;
			for (Edge e = node.getFirstEdge (); e != null; e = e.getNext (node))
			{
				if (e.isSource (node))
				{
					outCount++;
				}
				else if (e != edge)
				{
					inCount++;
				}
			}
			if (inCount == 0)
			{
				s = "";
			}
			else
			{
				s = "n" + visited.size ();
			}
			visited.put (node, s);
			for (int i = 0; i < 2; i++)
			{
				for (Edge e = node.getFirstEdge (); e != null; e = e.getNext (node))
				{
					if ((i == 0) == (e.getEdgeBits () == Graph.BRANCH_EDGE))
					{
						if (e.isSource (node))
						{
							if(node instanceof CfTreeSegment) toVisit.add (node);
							append (e.getTarget (), e, outCount > 0, visited, toVisit);
						}
//						else if ((e != edge) && (visited.get (e.getSource ()) == null))
//						{
//							toVisit.add (e.getSource ());
//						}
					}
				}
			}
		}
	}
	
	
	
	//includes forAllTreeCompartments
	public void forEachEvaluateRadiationForCfTreeSegmentForest(Tree tree, EvaluateRadiationForCfTreeSegmentForest f_e){
		
		Axis axis = GetAxis(tree);
		
		//TreeCompartments need to be CfTreeSegments, otherwise this method does not work
		LinkedList<TreeCompartment> tc_ls = axis.GetTreeCompratmentList();
		
		ListIterator<TreeCompartment> iterator = tc_ls.listIterator();
		
		while(iterator.hasNext()){
			f_e.eval((CfTreeSegment)iterator.next());
		}		
		
	}

	private Axis GetAxis(Tree tree) {
		return tree.axis;
	}
	
	
	
	public double getLGPaf() {
		return parameters.LGPaf;
	}
	
	public double getLGPaleafmax() {
		return parameters.LGPaleafmax;
	}
	
	public double getLGPapical() {
		return parameters.LGPapical;
	}
	
	public double getLGPar() {
		return parameters.LGPar;
	}
	
	public double getLGPdof() {
		return parameters.LGPdof;
	}
	
	public double getLGPLmin() {
		return parameters.LGPLmin;
	}
	
	public double getLGPlenRandom() {
		return parameters.LGPlen_random;
	}
	
	public double getLGPlr() {
		return parameters.LGPlr;
	}
	
	public double getLGPmf() {
		return parameters.LGPmf;
	}
	
	public double getLGPmr() {
		return parameters.LGPmr;
	}
	
	public double getLGPms() {
		return parameters.LGPms;
	}
	
	public double getLGPna() {
		return parameters.LGPna;
	}
	
	public double getLGPnl() {
		return parameters.LGPnl;
	}
	
	public double getLGPpr() {
		return parameters.LGPpr;
	}
	
	public double getLGPq() {
		return parameters.LGPq;
	}
	
	public double getLGPrhoW() {
		return parameters.LGPrhoW;
	}
	
	public double getLGPrho_root() {
		return parameters.LGPrho_root;
	}
	
	public double getLGPrho_hair() {
		return parameters.LGPrho_hair;
	}
	
	public double getLGPsf() {
		return parameters.LGPsf;
	}
	
	public double getLGPsr() {
		return parameters.LGPsr;
	}
	
	public double getLGPss() {
		return parameters.LGPss;
	}
	
	public double getLGPtauL() {
		return parameters.LGPtauL;
	}
	
	public double getLGPxi() {
		return parameters.LGPxi;
	}
	
	public double getLGPyc() {
		return parameters.LGPyc;
	}
	
	public double getLGPzbrentEpsilon() {
		return parameters.LGPzbrentEpsilon;
	}

	public void setLGPna (double value) {
		parameters.LGPna = value;
	}
	
	public void setLGPnl (double value) {
		parameters.LGPnl = value;
	}

	
	
	public ParametricCurve getLGMAL() {
		return parameters.LGMAL;
	}
	
	public ParametricCurve getLGMFM() {
		return parameters.LGMFM;
	}
	
	public ParametricCurve getLGMIP() {
		return parameters.LGMIP;
	}
	
	public ParametricCurve getLGMNB() {
		return parameters.LGMNB;
	}
	
	public ParametricCurve getLGMLONB() {
		return parameters.LGMLONB;
	}
	
	public ParametricCurve getLGMVI() {
		return parameters.LGMVI;
	}
	
	public ParametricCurve getLGMVIONB() {
		return parameters.LGMVIONB;
	}
	
	public void setLGMAL (ParametricCurve value)
	{
		this.parameters.LGMAL = new ParametricCurve(value);
	}
	
	public void setLGMFM (ParametricCurve value)
	{
		this.parameters.LGMFM = new ParametricCurve(value);
	}
	
	public void setLGMIP (ParametricCurve value)
	{
		this.parameters.LGMIP = new ParametricCurve(value);
	}
	
	public void setLGMNB (ParametricCurve value)
	{
		this.parameters.LGMNB = new ParametricCurve(value);
	}
	
	public void setLGMLONB (ParametricCurve value)
	{
		this.parameters.LGMLONB = new ParametricCurve(value);
	}
	
	public void setLGMVI (ParametricCurve value)
	{
		this.parameters.LGMVI = new ParametricCurve(value);
	}
	
	public void setLGMVIONB (ParametricCurve value)
	{
		this.parameters.LGMVIONB = new ParametricCurve(value);
	}
	
	
	
	public double getTreeQinMax () {
		return TreeQinMax;
	}
	
	public double getTreeWr () {
		return TreeWr;
	}
	
	public double getTreeP () {
		return TreeP;
	}
	
	public double getTreeM () {
		return TreeM;
	}
	
	public void setTreeQinMax (double value)
	{
		this.TreeQinMax = value;
	}
	
	public void setTreeWr (double value) {
		this.TreeWr = value;
	}
	
	public void setTreeP (double value) {
		this.TreeP = value;
	}
	
	public void setTreeM (double value) {
		this.TreeM = value;
	}
	
//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field Treelambda$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Tree.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 0:
					((Tree) o).Treelambda = value;
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Tree) o).getTreelambda ();
			}
			return super.getDouble (o);
		}
	}

	static
	{
		$TYPE = new NType (new Tree ());
		$TYPE.addManagedField (Treelambda$FIELD = new _Field ("Treelambda", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 0));
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
		return new Tree ();
	}

	public double getTreelambda ()
	{
		return Treelambda;
	}

	public void setTreelambda (double value)
	{
		this.Treelambda = value;
	}

//enh:end
	
	
	//Get a parameter value 
//	double getValue(LGMPD name){
//		switch(name){
//		case LGPaf:
//			return parameters.LGPaf;
//		case LGPaleafmax:
//		    return parameters.LGPaleafmax;
//
//		case LGPapical:
//			return parameters.LGPapical;
//
//	    case LGPar:
//	    	return parameters.LGPar;
//
//	    case LGPdof:
//	    	return parameters.LGPdof;
//
//	    case LGPLmin:
//	    	return parameters.LGPLmin;
//	  
//	    case LGPlen_random:
//	    	return parameters.LGPlen_random;
//	 
//	    case LGPlr:
//	    	return parameters.LGPlr;		 
//	  
//	    case LGPmf:
//	    	return parameters.LGPmf;
//
//	    case LGPmr:
//	    	return parameters.LGPmr;
//
//	    case LGPms:
//	    	return parameters.LGPms;
//
//	    case LGPna:
//	    	return parameters.LGPna;
//
//	    case LGPnl: 
//	    	return parameters.LGPnl;
//
//	    case LGPpr:
//	    	return parameters.LGPpr;
//
//	    case LGPq:
//	    	return parameters.LGPq;
//
//	    case LGPrhoW:
//	    	return parameters.LGPrhoW;
//
//	    case LGPrho_root:
//	    	return parameters.LGPrho_root;
//	  
//	    case LGPrho_hair:
//	    	return parameters.LGPrho_hair;
//
//	    case LGPsf:
//	    	return parameters.LGPsf;
//
//	    case LGPsr:
//	    	return parameters.LGPsr;
//
//	    case LGPss:
//	    	return parameters.LGPss;
//
//	    case LGPtauL:
//	    	return parameters.LGPtauL;
//
//	    case LGPxi:
//	    	return parameters.LGPxi;
//
//	    case LGPyc:
//	    	return parameters.LGPyc;
//
//	    case LGPzbrentEpsilon:
//	    	return parameters.LGPzbrentEpsilon;
//
//	  	default:
//	  		return 0.0;
//		}
//	}
	
}
