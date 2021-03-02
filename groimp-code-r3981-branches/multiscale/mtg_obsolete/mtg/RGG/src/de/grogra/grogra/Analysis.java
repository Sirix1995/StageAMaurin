
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.TreeIterator;
import de.grogra.turtle.Attributes;
import de.grogra.turtle.Shoot;
import de.grogra.pf.data.Dataseries;
import de.grogra.pf.data.Dataset;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.reflect.Type;
import de.grogra.rgg.Library;
import de.grogra.util.Configuration;
import de.grogra.util.ConfigurationSet;
import de.grogra.util.KeyDescription;
import de.grogra.util.KeyDescriptionImpl;
import de.grogra.util.Quantity;
import de.grogra.util.StringMap;
import de.grogra.vecmath.Math2;

/**
 * This class contains a set of static analysis functions which
 * are similar to the analysis functions of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public final class Analysis
{
	
	// Color-value for something to be invisible
	private static final byte INVISIBLE = 0;
	
	// Used by severalTreesAnalysis method --> indicated the maximal numbers of trees in a system to analyse
	private static final byte MAX_TREES = 100;
	
	// Used by labelDaughters method and some other methods
	private static final short MAX_DAUGHTERS = 500;
	
	// Used in distributionAnalysis --> maximum order
	private static final int MAX_ORDER = 5;
	
	// Used in distributionAnalysis --> maximum numbers of elementary units per compounend
	private static final int MAX_EUCU = 80;
	
	// Used in distributionAnalysis --> maximum numbers of elementary units per axis
	private static final int MAX_EUAX = 400;
	
	// Used in distributionAnalysis --> maximum numbers of compound units per axis
	private static final int MAX_CUAX = 200;
	
	// Used in createArraysForSplit --> minimum distance
	private static final int MIN_DISTANCE = 1;
	
	// Global variable for labelDaughters and some other methods
	private static int countDaughters = 0;
	
	private Analysis ()
	{
	}


	public static void performAndShow (Item item, Object info, Context context)
	{
		if (!(info instanceof ActionEditEvent))
		{
			return;
		}
		ActionEditEvent e = (ActionEditEvent) info;
		if (e.isConsumed () || !((info = e.getSource ()) instanceof Expression))
		{
			return;
		}
		e.consume ();
		final Workbench w = context.getWorkbench ();
		Object o = ((Expression) info).evaluate (w, UI.getArgs (context, (Expression) info));
		if (o instanceof Dataset)
		{
			((Dataset) o).showInPanel (context.getPanel ());
		}
	}

	
	/**
	 * Performs elementary analysis on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the result of the elementary analysis
	 */
	public static Dataset elementary (Node root)
	{
		Dataset ds = new Dataset ();
		ds.setTitle("elementary analysis");
		ds.setColumnKey (0, "Generation")
			.setColumnKey (1, "#Shoots")
			.setColumnKey (2, "min. X")
			.setColumnKey (3, "min. Y")
			.setColumnKey (4, "min. Z")
			.setColumnKey (5, "max. X")
			.setColumnKey (6, "max. Y")
			.setColumnKey (7, "max. Z")
			.setColumnKey (8, "#terminal shoots")
			.setColumnKey (9, "aver. no. of daughter shoots of nonterminal shoots")
			.setColumnKey (10, "global sum of all shootlengths")
			.setColumnKey (11, "sum of shoot volumes")
			.setColumnKey (12, "stem volume")
			.setColumnKey (13, "sum branch volumes")
			.setColumnKey (14, "sum of shoot surface area")
			.setColumnKey (15, "stem surface area")
			.setColumnKey (16, "sum of branch surface area")
			.setColumnKey (17, "total no. of internodes")
			.setColumnKey (18, "sum of values of parameter N")
			.setColumnKey (19, "max. radius projection xy-plane")
			.setColumnKey (20, "average branching angle")
			.setColumnKey (21, "average contraction factor")
			.setColumnKey (22, "maximal shoot diameter")
			.setColumnKey (23, "#leafs")
			.setColumnKey (24, "#fruits");


		// variables to aggregate the result
		int count = 0;
		Point3d min = new Point3d (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point3d max = new Point3d (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);


		// temporary variables
		int aate, aatn, aaordsp, aato, order;
		float llen, vvol, svol, bvol, hvol, ar, sar, bar, har, nnsu, aang, kkontr;
		long izasu, bza, fza, internodeCount;
		float mmaxrxy, mmaxd, length, lengthTemp;
		float radi, hwi, diameter;
		
		aate = aatn = aaordsp = 0;
	    llen = vvol = svol = bvol = nnsu = 0.0f;
	    ar = sar = bar = 0.0f;
	    izasu = bza = fza = 0;
	    mmaxrxy = mmaxd = 0.0f;
	    aang = kkontr = 0.0f;
		
		Vector3d trans = new Vector3d ();
		Vector3d shootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
		Vector3d beginShootS = new Vector3d();
		Vector3d endShootS = new Vector3d();
		Vector3d beginShootSTemp = new Vector3d();
		Vector3d endShootSTemp = new Vector3d();

		GraphState gs = root.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (root);

		Shoot s;
		while ((s = LSystem.nextShoot (it)) != null)
		{
			count++;
			aato = 0;
			length = (float) gs.getDouble(s, true, Attributes.LENGTH);
			internodeCount = gs.getInt(s, true, Attributes.INTERNODE_COUNT);
			diameter = gs.getFloat(s, true, Attributes.RADIUS) * 2;
			order = gs.getInt(s, true, Attributes.ORDER);

			Math2.getBeginAndEndOfShoot(LSystem.transformation(s), length, beginShootS, endShootS);
			endShootS.sub(beginShootS);
			
			TreeIterator itTemp = new TreeIterator (s);
			GraphState gsTemp = s.getCurrentGraphState();
			Shoot sTemp;
			while ((sTemp = LSystem.nextShoot (itTemp)) != null) {
				if(getAssociatedMotherShoot(sTemp) != s)
					continue;
				aato++;
				if(order != gsTemp.getInt(sTemp, true, Attributes.ORDER)) {
					
					lengthTemp = (float) gsTemp.getDouble(sTemp, true, Attributes.LENGTH);
					
					if(lengthTemp > Math2.EPSILON) {
						aaordsp++;

						Math2.getBeginAndEndOfShoot(LSystem.transformation(sTemp), lengthTemp, beginShootSTemp, endShootSTemp);
						endShootSTemp.sub(beginShootSTemp);
						
						hwi = (float) Math.toDegrees(endShootS.angle(endShootSTemp));
						if ((hwi < 0) || (hwi > 180))
							System.out.println("WARNING: Angle between two shoots = "+hwi);
						else
							aang += hwi;
						if(length != 0.0)
							kkontr += lengthTemp / length;
					}
				}
			}
			
			if(aato > 0)
				aatn += aato;
			else
				aate++;

			llen += length;
			hvol = (float) (Math2.M_PI * length * (Math.pow(diameter, 2) * 3) / 12.0f);
			har = Math2.M_PI * diameter * length;
			vvol += hvol;
			ar += har;
			
			if(order <= 0) {
				svol += hvol;
				sar += har;
			} else {
				bvol += hvol;
				bar += har;
			}
			
			if (internodeCount > 0)
				izasu += (long) internodeCount;
			if (internodeCount == -1)
				bza++;
			if (internodeCount == -2)
				fza++;
			
			nnsu += gs.getFloat(s, true, Attributes.PARAMETER);
			
			Matrix4d m = LSystem.transformation (s);

			m.get (trans);
			shootBegin.set(trans);
			// now trans contains the global coordinates of the base of s
			Math2.min (min, trans);
			Math2.max (max, trans);

			trans.set (0, 0, s.getLength (s, gs));
			Math2.transformPoint (m, trans);
			// now trans contains the global coordinates of the tip of s
			shootEnd.set(trans);
			Math2.min (min, trans);
			Math2.max (max, trans);
			
			radi = (float) Math.sqrt(Math.pow(shootBegin.x, 2) + Math.pow(shootBegin.y, 2));
			if(radi > mmaxrxy)
				mmaxrxy = radi;
			radi = (float) Math.sqrt(Math.pow(shootEnd.x, 2) + Math.pow(shootEnd.y, 2));
			if(radi > mmaxrxy)
				mmaxrxy = radi;
			if(diameter > mmaxd)
				mmaxd = diameter;
		}

		Dataseries row = ds.addRow ();
		row.set (1, new Integer (count))
			.set (2, new Double (min.x))
			.set (3, new Double (min.y))
			.set (4, new Double (min.z))
			.set (5, new Double (max.x))
			.set (6, new Double (max.y))
			.set (7, new Double (max.z))
			.set (8, new Integer (aate))
			.set (9, new Double ((count == aate ? 0.0 : aatn / (count - aate))))
			.set (10, new Double (llen))
			.set (11, new Double (vvol))
			.set (12, new Double (svol))
			.set (13, new Double (bvol))
			.set (14, new Double (ar))
			.set (15, new Double (sar))
			.set (16, new Double (bar))
			.set (17, new Long (izasu))
			.set (18, new Double (nnsu))
			.set (19, new Double (mmaxrxy))
			.set (20, new Double (aaordsp == 0 ? 0.0 : aang / aaordsp))
			.set (21, new Double (aaordsp == 0 ? 0.0 : kkontr / aaordsp))
			.set (22, new Double (mmaxd))
			.set (23, new Long (bza))
			.set (24, new Long (fza));
		LSystem lsy = LSystem.current();
		if (lsy != null)
		{
			row.set (0, new Integer (lsy.generation));
		}
		return ds;
	}

	/**
	 * Performs basic tree parameters analysis on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the result of the analysis for the basic tree parameters
	 */
	public static Dataset basicTreeParameters(Node root) {
		Dataset ds = new Dataset ();
		ds.setTitle("basic tree parameters");
		ds.setColumnKey (0, "lowest z value")
			.setColumnKey (1, "highest z value")
			.setColumnKey (2, "latest height increment")
			.setColumnKey (3, "height of crown base")
			.setColumnKey (4, "maximal crown radius")
			.setColumnKey (5, "stem diameter at tree basis")
			.setColumnKey (6, "stem diameter at breast ht.")
			.setColumnKey (7, "branch diam. at crown base")
			.setColumnKey (8, "maximal branch diameter")
			.setColumnKey (9, "total nb. of growth units")
			.setColumnKey (10, "sum of all leaf values");
		
		float tfp, hoh, hzw, krh, krr, bsd, bhd, akd, amd, sumn, rad;
		float agl, zwb, agm, zhilf, length, diameter;
		long anzwe;
		int nbwe, nbhilf;
		int order, orderTemp;
		
		tfp = 1E16f;
	    hoh = 0.0f;
	    hzw = 0.0f;
	    krh = 1E16f;
	    krr = 0.0f;
	    bsd = -1.0f;
	    bhd = -1.0f;
	    akd = -1.0f;
	    amd = -1.0f;
	    anzwe = 0;
	    sumn = 0.0f;
	    agm = 0.0f;
	    zwb = -1.0f;
	    nbwe = -1;
		
	    Vector3d shootBegin = new Vector3d();
	    Vector3d motherShootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
	    
	    GraphState gs = root.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (root);

		Shoot s, mother, helper, longest = null;
		while((s = LSystem.nextShoot (it)) != null) {
			anzwe++;
			sumn += gs.getFloat(s, true, Attributes.PARAMETER);
			order = gs.getInt(s, true, Attributes.ORDER);
			length = (float) gs.getDouble(s, true, Attributes.LENGTH);
			diameter = gs.getFloat(s, true, Attributes.RADIUS) * 2;
			mother = getAssociatedMotherShoot(s);
			Math2.getBeginAndEndOfShoot(LSystem.transformation(s), length, shootBegin, shootEnd);
			
			if(order == 0) {
				if(shootBegin.z < tfp)
					tfp = (float) shootBegin.z;
				if(shootEnd.z > hoh) {
					hoh = (float) shootEnd.z;
					hzw = length;
				}
				if(mother == null)
					bsd = diameter;
				if((shootBegin.z <= 1300.0) && (shootEnd.z > 1300.0))
					bhd = diameter;
			} else if(order == 1) {
				if(mother != null) {
					helper = getAssociatedMotherShoot(mother);
					GraphState motherGs = mother.getCurrentGraphState();
					orderTemp = motherGs.getInt(mother, true, Attributes.ORDER);
					if((helper != null) && (orderTemp == 1)) {
						Math2.getBeginOfShoot(LSystem.transformation(mother), motherShootBegin);
						if((orderTemp == 0) && (motherShootBegin.z <= krh)) {
							krh = (float) motherShootBegin.z;
							akd = motherGs.getFloat(mother, true, Attributes.HEARTWOOD);
						}
					}
				}
			
				if(diameter > amd)
					amd = diameter;
				
				agl = zhilf = 0.0f;
				nbhilf = 0;
				helper = s;
				orderTemp = order;
				while((orderTemp > 0) && (helper != null)) {
					nbhilf++;
					
//					agl += helper.getCurrentGraphState().getDouble(helper, true, Attributes.LENGTH);
//					orderTemp = helper.getCurrentGraphState().getInt(helper, true, Attributes.ORDER);
//					Math2.getBeginOfShoot(LSystem.transformation(helper), motherShootBegin);
//					zhilf = (float) motherShootBegin.z;
//					helper = getAssociatedMotherShoot(helper);
					
					agl += mother.getCurrentGraphState().getDouble(mother, true, Attributes.LENGTH);
					orderTemp = mother.getCurrentGraphState().getInt(mother, true, Attributes.ORDER);
					Math2.getBeginOfShoot(LSystem.transformation(mother), motherShootBegin);
					zhilf = (float) motherShootBegin.z;
					mother = getAssociatedMotherShoot(mother);
				}
				if(orderTemp == 0) {
					if(agl > agm) {
						agm = agl;
						longest = s;
						zwb = zhilf;
						nbwe = nbhilf;
					}
				}
			}
			
			rad = (float) Math.sqrt(Math.pow(shootEnd.x, 2) + Math.pow(shootEnd.y, 2));
			if(rad > krr)
				krr = rad;
		}
	    
		if (krh > 1E15)
			krh = -1.0f;
		if (tfp > 1E15)
			tfp = -1.0f;
		
		Dataseries row = ds.addRow ();
		row.set (0, new Double (tfp))
			.set (1, new Double (hoh))
			.set (2, new Double (hzw))
			.set (3, new Double (krh))
			.set (4, new Double (krr))
			.set (5, new Double (bsd))
			.set (6, new Double (bhd))
			.set (7, new Double (akd))
			.set (8, new Double (amd))
			.set (9, new Long (anzwe))
			.set (10, new Double (sumn));
		
		if(longest != null) {
			ds.setColumnKey (11, "max. S1 total length")
				.setColumnKey (12, "resp. z of S1 basis")
				.setColumnKey (13, "resp. nb. of WE");
			row.set (11, new Double (agm))
				.set (12, new Double (zwb))
				.set (13, new Integer (nbwe));
			
			mother = getAssociatedMotherShoot(longest);
			nbhilf = 14;
			while((longest.getCurrentGraphState().getInt(longest, true, Attributes.ORDER) > 0) && (mother != null)) {
				ds.setColumnKey (nbhilf, "increments from end to basis");
				row.set (nbhilf, new Double (longest.getCurrentGraphState().getDouble(longest, true, Attributes.LENGTH)));
				longest = mother;
				mother = getAssociatedMotherShoot(mother);
				nbhilf++;
			}
		}
		
		return ds;
	}
	
	/**
	 * Performs coordinates analysis of GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the result of the coordinates analysis
	 */
	public static Dataset coordinates(Node root) {
		Dataset ds = new Dataset ();
		ds.setTitle("coordinates");
		ds.setColumnKey (0, "ID of shoot")
			.setColumnKey (1, "x-coordinate")
			.setColumnKey (2, "y-coordinate");
		Dataseries row;
		
		GraphState gs = root.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (root);
		Vector3d shootBegin = new Vector3d();
		
		Shoot s;
		while((s = LSystem.nextShoot (it)) != null) {
			if(gs.getInt(s, true, Attributes.DTG_COLOR) != INVISIBLE) {
				row = ds.addRow ();
				Math2.getBeginOfShoot(LSystem.transformation(s), shootBegin);
				row.set(0, new Long(s.getId()));
				row.set(1, new Double(shootBegin.x));
				row.set(2, new Double(shootBegin.y));
			}
		}
		
		return ds;
	}
	
	/**
	 * Creates a diameter table based on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the diameter table
	 */
	public static Dataset diameterTable(Node root) {
		Dataset ds = new Dataset ();
		ds.setTitle("diameter table");
		ds.setColumnKey (0, "ID of shoot")
			.setColumnKey (1, "#daughter shoots")
			.setColumnKey (2, "diameter of shoot")
			.setColumnKey (3, "diameter of daughter 1")
			.setColumnKey (4, "diameter of daughter 2")
			.setColumnKey (5, "diameter of daughter 3")
			.setColumnKey (6, "diameter of daughter 4");
		
		float[] dia = new float[4];
		int counter, internodeCount;
		
		Shoot searcher, s, mother;
		Dataseries row;
		
		GraphState gs = root.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (root);
		
		while((s = LSystem.nextShoot (it)) != null) {
			internodeCount = gs.getInt(s, true, Attributes.INTERNODE_COUNT); 
			
			if((gs.getInt(s, true, Attributes.ORDER) >= 0) && (internodeCount >= 0)) {
				counter = 0;
				dia[0] = dia[1] = dia[2] = dia[3] = 0;
				
				GraphState gsTemp = root.getCurrentGraphState ();
				TreeIterator itTemp = new TreeIterator (root);
				
				while((searcher = LSystem.nextShoot (itTemp)) != null) {
					mother = getAssociatedMotherShoot(searcher);
					if((mother == s) && (internodeCount >= 0)) {
						counter++;
						if(counter < 5)
							dia[counter-1] = gsTemp.getFloat(searcher, true, Attributes.RADIUS) * 2;
					}
				}
				
				if((counter > 1) && (counter < 5)) {
					row = ds.addRow();
					row.set(0, new Long(s.getId()))
						.set(1, new Integer(counter))
						.set(2, new Double(gs.getFloat(s, true, Attributes.RADIUS) * 2))
						.set(3, new Double(dia[0]))
						.set(4, new Double(dia[1]))
						.set(5, new Double(dia[2]))
						.set(6, new Double(dia[3]));
				}
			}
		}
		
		return ds;
	}
	
	/**
	 * Performs elementary analysis of several trees on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the result of the elementary analysis of several trees
	 */
	public static Dataset severalTreesAnalysis(Node root) {
		Dataset ds = new Dataset ();
		ds.setTitle("elementary analysis of several trees");
		ds.setColumnKey (0, "Tree id number")
			.setColumnKey (1, "maximal z value (height)")
			.setColumnKey (2, "maximal diameter")
			.setColumnKey (3, "BHD")
			.setColumnKey (4, "length sum")
			.setColumnKey (5, "sum of leaf values")
			.setColumnKey (6, "number of internodes")
			.setColumnKey (7, "total volume")
			.setColumnKey (8, "total surface");
		
		float diameter, length;
		int anzba, jj, color;
		boolean found;
		anzba = 0;
		color = 0;
		
		Vector3d shootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
		
		TreeInformation[] trees = new TreeInformation[MAX_TREES];
		Shoot s;
		Dataseries row;
		
		GraphState gs = root.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (root);
		
		while((s = LSystem.nextShoot (it)) != null) {
			found = false;
			jj = 0;
			color = gs.getInt(s, true, Attributes.DTG_COLOR);
			while(!found && (jj < anzba)) {
				if((trees[jj] != null) && (trees[jj].getColor() == color)) {
					found = true;
				} else {
					jj++;
					if(jj >= MAX_TREES)
						System.out.println("Warning: Too many trees!");
				}
			}
			
			if(!found && (jj < MAX_TREES)) {
				if(jj != anzba)
					System.out.println("Warning: Severe inconsistency!");
				trees[jj] = new TreeInformation();
				trees[jj].setColor(color);
				anzba++;
				if(anzba >= MAX_TREES)
					System.out.println("Warning: Too many trees!");
			}
			
			length = (float) gs.getDouble(s, true, Attributes.LENGTH);
			diameter = gs.getFloat(s, true, Attributes.RADIUS) * 2;
			Math2.getBeginAndEndOfShoot(LSystem.transformation(s), length, shootBegin, shootEnd);
			
			if(shootEnd.z > trees[jj].getZval())
				trees[jj].setZval((float) shootEnd.z);
			if(diameter > trees[jj].getDiameter())
				trees[jj].setDiameter(diameter);
			if(gs.getInt(s, true, Attributes.ORDER) == 0) {
				if((shootBegin.z <= 1300.0) && (shootEnd.z > 1300.0))
					trees[jj].setBhd(diameter);
			}
			trees[jj].setLsum(length+trees[jj].getLsum());
			trees[jj].setNsum(gs.getFloat(s, true, Attributes.PARAMETER)+trees[jj].getNsum());
			trees[jj].setIsum(gs.getInt(s, true, Attributes.INTERNODE_COUNT)+trees[jj].getIsum());
			trees[jj].setVolsum((float) (trees[jj].getVolsum()+(Math2.M_PI * 0.25 * diameter * diameter * length)));
			trees[jj].setSursum((float) (trees[jj].getSursum()+(Math2.M_PI * diameter * length)));
		}
		
		for(int i = 0; i < trees.length; i++) {
			if(trees[i] != null) {
				row = ds.addRow();
				row.set(0, new Integer(trees[i].getColor()))
					.set(1, new Double(trees[i].getZval()))
					.set(2, new Double(trees[i].getDiameter()))
					.set(3, new Double(trees[i].getBhd()))
					.set(4, new Double(trees[i].getLsum()))
					.set(5, new Double(trees[i].getNsum()))
					.set(6, new Long(trees[i].getIsum()))
					.set(7, new Double(trees[i].getVolsum()))
					.set(8, new Double(trees[i].getSursum()));
			} else
				break;
		}
		
		return ds;
	}
	
	/**
	 * Create a list of all shoots on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the list of all shoots
	 */
	public static Dataset listOfAllShoots(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("list of all shoots");
		ds.setColumnKey(0, "shoot-ID")
			.setColumnKey(1, "predecessor-ID")
			.setColumnKey(2, "length")
			.setColumnKey(3, "heartwood diameter")
			.setColumnKey(4, "total diameter")
			.setColumnKey(5, "#internode")
			.setColumnKey(6, "color")
			.setColumnKey(7, "order")
			.setColumnKey(8, "generation no.")
			.setColumnKey(9, "generative-distance")
			.setColumnKey(10, "tropism")
			.setColumnKey(11, "n-value")
			.setColumnKey(12, "carbon")
			.setColumnKey(13, "rel. position on mother shoot")
			.setColumnKey(14, "x-value at begin of shoot")
			.setColumnKey(15, "y-value at begin of shoot")
			.setColumnKey(16, "z-value at begin of shoot")
			.setColumnKey(17, "x-value at end of shoot")
			.setColumnKey(18, "y-value at end of shoot")
			.setColumnKey(19, "z-value at end of shoot")
			.setColumnKey(20, "x-value of sl")
			.setColumnKey(21, "y-value of sl")
			.setColumnKey(22, "z-value of sl")
			.setColumnKey(23, "x-value of su")
			.setColumnKey(24, "y-value of su")
			.setColumnKey(25, "z-value of su")
			.setColumnKey(26, "x-value of sh")
			.setColumnKey(27, "y-value of sh")
			.setColumnKey(28, "z-value of sh");
		
		float length;
		
		Vector3d shootBegin = new Vector3d();
		Vector3d shootEnd = new Vector3d();
		Matrix4d matrix;
		
		Shoot s, mother;
		Dataseries row;
		
		GraphState gs = root.getCurrentGraphState ();
		TreeIterator it = new TreeIterator (root);
		
		int generationNo = 0;
		LSystem lsy = LSystem.current();
		if (lsy != null)
			generationNo = lsy.getGenerationNo();
		
		while((s = LSystem.nextShoot (it)) != null) {
			length = (float) gs.getDouble(s, true, Attributes.LENGTH);
			matrix = LSystem.transformation(s);
			Math2.getBeginAndEndOfShoot(matrix, length, shootBegin, shootEnd);
			mother = getAssociatedMotherShoot(s);
			
			row = ds.addRow();
			row.set(0, new Long(s.getId()))
				.set(1, new Long((mother != null ? mother.getId() : -1)))
				.set(2, new Double(length))
				.set(3, new Double(gs.getFloat(s, true, Attributes.HEARTWOOD)))
				.set(4, new Double(gs.getFloat(s, true, Attributes.RADIUS) * 2))
				.set(5, new Integer(gs.getInt(s, true, Attributes.INTERNODE_COUNT)))
				.set(6, new Integer(gs.getInt(s, true, Attributes.DTG_COLOR)))
				.set(7, new Integer(gs.getInt(s, true, Attributes.ORDER)))
				.set(8, new Integer(generationNo))
				.set(9, new Integer(gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE)))
				.set(10, new Double(gs.getFloat(s, true, Attributes.TROPISM_STRENGTH)))
				.set(11, new Double(gs.getFloat(s, true, Attributes.PARAMETER)))
				.set(12, new Double(gs.getFloat(s, true, Attributes.CARBON)))
				.set(13, new Double((gs.getFloat(s, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(s, true, Attributes.REL_POSITION))))
				.set(14, new Double(shootBegin.x))
				.set(15, new Double(shootBegin.y))
				.set(16, new Double(shootBegin.z))
				.set(17, new Double(shootEnd.x))
				.set(18, new Double(shootEnd.y))
				.set(19, new Double(shootEnd.z))
				.set(20, new Double(matrix.m00))
				.set(21, new Double(matrix.m10))
				.set(22, new Double(matrix.m20))
				.set(23, new Double(matrix.m01))
				.set(24, new Double(matrix.m11))
				.set(25, new Double(matrix.m21))
				.set(26, new Double(matrix.m02))
				.set(27, new Double(matrix.m12))
				.set(28, new Double(matrix.m22));
		}
		
		return ds;
	}
	
	/**
	 * Performs pathlength analysis on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the result of the pathlength analysis
	 */
	public static Dataset pathlengthAnalysis(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("pathlength analysis");
		ds.setColumnKey(0, "shoot-ID")
			.setColumnKey(1, "diameter")
			.setColumnKey(2, "av.length")
			.setColumnKey(3, "max.length")
			.setColumnKey(4, "no. of longest terminal shoot")
			.setColumnKey(5, "length sum")
			.setColumnKey(6, "volume sum")
			.setColumnKey(7, "n sum")
			.setColumnKey(8, "order");
		
		Shoot s, n, m, ttermax;
		Dataseries row;
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		
		int anzterm;
		boolean terspr;
		float qvor, lensum, mmaxlen, geslen, lsu, nsu, vsu, lengthS, lengthN, lengthM = 0.0f;
		
		while((s = LSystem.nextShoot (it)) != null) {
			anzterm = 0;
			mmaxlen = geslen = 0.0f;
			ttermax = s;
			lsu = vsu = nsu = 0.0f;
			
			GraphState gsN = root.getCurrentGraphState();
			TreeIterator itN = new TreeIterator(root);
			lengthS = (float) gs.getDouble(s, true, Attributes.LENGTH);
			
			while((n = LSystem.nextShoot (itN)) != null) {
				terspr = false;
				lengthN = (float) gsN.getDouble(n, true, Attributes.LENGTH);
				if(terminalShoot(n, root) && !((lengthN < Math2.EPSILON) && ((gsN.getFloat(n, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gsN.getFloat(n, true, Attributes.REL_POSITION)) > Math2.EPSILON)))
					terspr = true;
				lensum = 0.0f;
				qvor = 0.0f;
				
				m = n;
				GraphState gsM = m.getCurrentGraphState();
				while((m != null) && (m != s)) {
					lengthM = (float) gsM.getDouble(m, true, Attributes.LENGTH);
					
					lensum += (lengthM * (1. - qvor));
					qvor = (gsM.getFloat(m, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gsM.getFloat(m, true, Attributes.REL_POSITION));
					m = getAssociatedMotherShoot(m);
				}
				
				if(m == s) {
					lensum += (lengthS * (1. - qvor));
					lsu += lengthN;
					vsu += lengthN * 0.25f * (float) Math2.M_PI * Math.pow(gsN.getFloat(n, true, Attributes.RADIUS) * 2, 2);
					nsu += gsN.getFloat(n, true, Attributes.PARAMETER);
					if (terspr) {
						geslen += lensum;
						anzterm++;
						if (lensum > mmaxlen) {
							mmaxlen = lensum;
							ttermax = n;
						}
					}
				}
			}
			
			row = ds.addRow();
			row.set(0, new Long(s.getId()))
				.set(1, new Double(gs.getFloat(s, true, Attributes.RADIUS) * 2))
				.set(2, new Double(anzterm > 0 ? geslen / anzterm : 0.0))
				.set(3, new Double(mmaxlen))
				.set(4, new Long(ttermax.getId()))
				.set(5, new Double(lsu))
				.set(6, new Double(vsu))
				.set(7, new Double(nsu))
				.set(8, new Integer(gs.getInt(s, true, Attributes.ORDER)));
		}
		
		return ds;
	}
	
	/**
	 * Create a list of branching positions on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the list of branching positions
	 */
	public static Dataset branchingPositions(Node root) {
		ArrayList<double[]> data = new ArrayList<double[]>();
		double[] rowData;
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, n;
		
		int order, countDaughter, maxDaughterInData = 0;
		
		while((s = LSystem.nextShoot (it)) != null) {
			order = gs.getInt(s, true, Attributes.ORDER);
			if((order >= 0) && (gs.getInt(s, true, Attributes.INTERNODE_COUNT) >= 0)) {
				rowData = new double[42];
				Arrays.fill(rowData, -1.0);
				countDaughter = 0;
				rowData[0] = s.getId();
				
				GraphState gsN = root.getCurrentGraphState();
				TreeIterator itN = new TreeIterator(root);
				while((n = LSystem.nextShoot (itN)) != null) {
					if ((getAssociatedMotherShoot(n) == s) && (gsN.getInt(n, true, Attributes.INTERNODE_COUNT) >= 0)
							&& (gsN.getInt(n, true, Attributes.ORDER) > order)) {
						countDaughter++;
						if(countDaughter < 41) {
							rowData[countDaughter] = (gsN.getFloat(n, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gsN.getFloat(n, true, Attributes.REL_POSITION));
						}
					}
				}
				
				if(countDaughter > 0) {
					rowData[41] = countDaughter;
					if(countDaughter < 41)
						maxDaughterInData = Math.max(countDaughter, maxDaughterInData);
					else
						maxDaughterInData = 40;
					data.add(rowData);
				}
			}
		}
		
		Dataset ds = new Dataset();
		ds.setTitle("branching positions");
		ds.setColumnKey(0, "shoot-ID")
			.setColumnKey(1, "#daughters");
		
		String temp;
		for(int i = 2; i < maxDaughterInData+2; i++) {
			temp = "daughter-"+(i-1);
			ds.setColumnKey(i, temp);
		}
		
		Dataseries row;
		Iterator iter = data.iterator();
		while(iter.hasNext()) {
			rowData = (double[]) iter.next();
			row = ds.addRow();
			row.set(0, new Double(rowData[0]))
				.set(1, new Double(rowData[41]));
			
			for(int i = 2; i < maxDaughterInData+2; i++) {
				row.set(i, new Double(rowData[i-1]));
			}
		}
		
		return ds;
	}
	
	/**
	 * Create a list with number of daughter shoots on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the list with number of daughter shoots
	 */
	public static Dataset noOfDaughterShoots(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("number of daughter shoots");
		ds.setColumnKey(0, "shoot-ID")
			.setColumnKey(1, "length")
			.setColumnKey(2, "diameter")
			.setColumnKey(3, "order")
			.setColumnKey(4, "age")
			.setColumnKey(5, "#daughter shoots")
			.setColumnKey(6, "#subapical daughter shoots")
			.setColumnKey(7, "#medial daughter shoots")
			.setColumnKey(8, "#basal daughter shoots")
			.setColumnKey(9, "sum of diametersquare of daughter shoots")
			.setColumnKey(10, "sum of diameters of daughter shoots")
			.setColumnKey(11, "z-value on shoottip")
			.setColumnKey(12, "x-value of direction vector of shoot")
			.setColumnKey(13, "y-value of direction vector of shoot")
			.setColumnKey(14, "z-value of direction vector of shoot")
			.setColumnKey(15, "leaf");
		
		Dataseries row;
		float q, diameter, alen, adu, qsum, dsum, hoeh = 0.0f;
		int aor, agen, aal, atoc, asubt, amedt, abast, genmax;
		
		genmax = findGenerationMax(root);
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		Vector3d vec = new Vector3d();
		Matrix4d matrix;
		
		while((s = LSystem.nextShoot (it)) != null) {
			aor = gs.getInt(s, true, Attributes.ORDER);
			
			if(aor >= 0) {
				alen = (float) gs.getDouble(s, true, Attributes.LENGTH);
				adu = gs.getFloat(s, true, Attributes.RADIUS) * 2;
				agen = gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE);
				aal = genmax - agen;
				
				matrix = LSystem.transformation(s);
				Math2.getEndOfShoot(matrix, alen, vec);
				hoeh = (float) vec.z;
				
				atoc = asubt = amedt = abast = 0;
				qsum = dsum = 0.0f;
				
				GraphState gsN = root.getCurrentGraphState();
				TreeIterator itN = new TreeIterator(root);
				Shoot n;
				
				while((n = LSystem.nextShoot (itN)) != null) {
					if((getAssociatedMotherShoot(n) == s) && (gsN.getInt(n, true, Attributes.INTERNODE_COUNT) >= 0)) {
						atoc++;
						diameter = gsN.getFloat(n, true, Attributes.RADIUS) * 2;
						qsum += Math.pow(diameter, 2);
						dsum += diameter;
						if(gsN.getInt(n, true, Attributes.ORDER) != aor) {
							q = (gsN.getFloat(n, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gsN.getFloat(n, true, Attributes.REL_POSITION));
							if(q < 0.2)
								asubt++;
							else if((q >= 0.2) && (q < 0.8))
								amedt++;
							else if(q >= 0.8)
								abast++;
						}
					}
				}
				
				row = ds.addRow();
				row.set(0, new Long(s.getId()))
					.set(1, new Double(alen))
					.set(2, new Double(adu))
					.set(3, new Integer(aor))
					.set(4, new Integer(aal))
					.set(5, new Integer(atoc))
					.set(6, new Integer(asubt))
					.set(7, new Integer(amedt))
					.set(8, new Integer(abast))
					.set(9, new Double(qsum))
					.set(10, new Double(dsum))
					.set(11, new Double(hoeh))
					.set(12, new Double(matrix.m02))
					.set(13, new Double(matrix.m12))
					.set(14, new Double(matrix.m22))
					.set(15, new Integer((gs.getInt(s, true, Attributes.INTERNODE_COUNT) < 0 ? 1 : 0)));
			}
		}
		
		return ds;
	}
	
	/**
	 * Performs option3 analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the result of option3 from GROGRA
	 */
	public static Dataset option3(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("number of daughter shoots");
		ds.setColumnKey(0, "shoot-ID")
			.setColumnKey(1, "mother-ID")
			.setColumnKey(2, "order")
			.setColumnKey(3, "age")
			.setColumnKey(4, "relative position on mother shoot")
			.setColumnKey(5, "run of angle")
			.setColumnKey(6, "length")
			.setColumnKey(7, "diameter")
			.setColumnKey(8, "#internodes")
			.setColumnKey(9, "#daughters")
			.setColumnKey(10, "#sleeping daughter-shoots")
			.setColumnKey(11, "length of bigbrother")
			.setColumnKey(12, "length of mother")
			.setColumnKey(13, "color")
			.setColumnKey(14, "difference (gen - gen of mother)")
			.setColumnKey(15, "axisposition")
			.setColumnKey(16, "leaf");
		
		Dataseries row;
		float alen, wnkl, biglen, mlen, adu;
		int aor, aal, atoc, aprotoc, apo, genmax, gen;
		long difgen;
		boolean cont;
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, n, bigBrother, mother, motherTemp;
		Vector3d sSh = new Vector3d();
		Vector3d motherSh = new Vector3d();
		Matrix4d matrix;
		
		genmax = findGenerationMax(root);
		
		while((s = LSystem.nextShoot (it)) != null) {
			aor = gs.getInt(s, true, Attributes.ORDER);
			
			if(aor >= 0) {
				alen = (float) gs.getDouble(s, true, Attributes.LENGTH);
				adu = gs.getFloat(s, true, Attributes.RADIUS) * 2;
				gen = gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE);
				aal = genmax - gen;
				bigBrother = bigBrother(s, root, true, null);
				
				if(bigBrother != null) {
					GraphState gsBB = bigBrother.getCurrentGraphState();
					biglen = (float) gsBB.getDouble(bigBrother, true, Attributes.LENGTH);
				} else
					biglen = -1;
				
				mother = getAssociatedMotherShoot(s);
				if(mother != null) {
					GraphState gsM = mother.getCurrentGraphState();
					mlen = (float) gsM.getDouble(mother, true, Attributes.LENGTH);
					matrix = LSystem.transformation(s);
					sSh.x = matrix.m02;
					sSh.y = matrix.m12;
					sSh.z = matrix.m22;
					matrix = LSystem.transformation(mother);
					motherSh.x = matrix.m02;
					motherSh.y = matrix.m12;
					motherSh.z = matrix.m22;
					wnkl = (float) Math.toDegrees(sSh.angle(motherSh));
					difgen = gen - gsM.getInt(mother, true, Attributes.GENERATIVE_DISTANCE);
				} else {
					mlen = -1.0f;
					wnkl = 0.0f;
					difgen = -1;
				}
				atoc = 0;
				aprotoc = 0;
				apo = 0;
				
				if(gs.getInt(s, true, Attributes.INTERNODE_COUNT) >= 0) {
					GraphState gsN = root.getCurrentGraphState();
					TreeIterator itN = new TreeIterator(root);
					
					while((n = LSystem.nextShoot (itN)) != null) {
						motherTemp = getAssociatedMotherShoot(n);
						if((motherTemp == s) && (gsN.getInt(n, true, Attributes.INTERNODE_COUNT) >= 0)) {
							atoc++;
							GraphState gsM = motherTemp.getCurrentGraphState();
							if (gsM.getInt(motherTemp, true, Attributes.GENERATIVE_DISTANCE) < gsN.getInt(n, true, Attributes.GENERATIVE_DISTANCE)-1)
								aprotoc++;
						}
					}
					
					apo = 1;
					n = s;
					cont = true;
					
					while (cont) {
						n = getAssociatedMotherShoot(n);
						if (n == null)
							cont = false;
						else {
							GraphState gsNn = n.getCurrentGraphState();
							if (gsNn.getInt(n, true, Attributes.ORDER) != aor)
								cont = false;
							else
								apo++;
						}
					}
				}
				
				row = ds.addRow();
				row.set(0, new Long(s.getId()))
					.set(1, new Long((mother != null ? mother.getId() : -1)))
					.set(2, new Integer(aor))
					.set(3, new Integer(aal))
					.set(4, new Double((gs.getFloat(s, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(s, true, Attributes.REL_POSITION))))
					.set(5, new Double(wnkl))
					.set(6, new Double(alen))
					.set(7, new Double(adu))
					.set(8, new Integer(gs.getInt(s, true, Attributes.INTERNODE_COUNT)))
					.set(9, new Integer(atoc))
					.set(10, new Integer(aprotoc))
					.set(11, new Double(biglen))
					.set(12, new Double(mlen))
					.set(13, new Double(gs.getInt(s, true, Attributes.DTG_COLOR)))
					.set(14, new Long(difgen))
					.set(15, new Integer(apo))
					.set(16, new Integer((gs.getInt(s, true, Attributes.INTERNODE_COUNT) < 0 ? 1 : 0)));
			}
		}
		
		return ds;
	}
	
	/**
	 * Performs shoot population analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the shoot population
	 */
	public static Dataset shootPopulation(Node root) {
		LSystem lsy = LSystem.current();
		if (lsy != null)
			return lsy.getShootPopulation();
		return null;
	}
	
	/**
	 * Performs length and angles analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the length and angles of the shoots
	 */
	public static Dataset lengthAndAngles(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("length and angles");
		ds.setColumnKey(0, "shoot-ID")
			.setColumnKey(1, "mother-ID")
			.setColumnKey(2, "order")
			.setColumnKey(3, "age")
			.setColumnKey(4, "positionno of cluster")
			.setColumnKey(5, "relative position on mother shoot")
			.setColumnKey(6, "rel. position * length of mother")
			.setColumnKey(7, "run of angle")
			.setColumnKey(8, "length")
			.setColumnKey(9, "#cluster")
			.setColumnKey(10, "length of bigbrother")
			.setColumnKey(11, "length of cousine")
			.setColumnKey(12, "length of mother")
			.setColumnKey(13, "#internodes")
			.setColumnKey(14, "#internodes of mother")
			.setColumnKey(15, "#internodes of bigbrother")
			.setColumnKey(16, "leaf");
		
		float alen, sppos, wnkl, biglen, coulen, mlen, clusterDistance, genmax;
		int aor, aal, iz, izm, izb;
		
		HashMap<Long, Float> xkaList = new HashMap<Long, Float>();
		HashMap<Long, Float> ykaList = new HashMap<Long, Float>();
		Shoot[] daughters = new Shoot[MAX_DAUGHTERS];
		float[] avek = new float[MAX_DAUGHTERS];
		float[] mavek = new float[MAX_DAUGHTERS];
		float[] mivek = new float[MAX_DAUGHTERS];
		
		Vector3d sSh = new Vector3d();
		Vector3d motherSh = new Vector3d();
		
		ConfigurationSet cs = new ConfigurationSet ("Length And Angles");
		KeyDescription key1 = new KeyDescriptionImpl("clusterDistance", Library.I18N, "analysis.clusterDistance", Type.FLOAT, Quantity.LENGTH);
		Configuration c = new Configuration(new KeyDescription[] {key1}, new StringMap().putFloat("clusterDistance", 1));
		cs.add(c);
		Workbench.current().showConfigurationDialog(cs);
		clusterDistance = (Float) cs.get("clusterDistance", null);
		
		genmax = findGenerationMax(root);
		labelDaughters(root, xkaList, ykaList, clusterDistance, daughters, avek, mavek, mivek);
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, bigBrother, cousine, mother;
		
		while((s = LSystem.nextShoot(it)) != null) {
			aor = gs.getInt(s, true, Attributes.ORDER);
			if (aor >= 0) {
				aal = ((int) genmax) - gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE);
				alen = (float) gs.getDouble(s, true, Attributes.LENGTH);
				iz = gs.getInt(s, true, Attributes.INTERNODE_COUNT);
				if (iz >= 0)
					bigBrother = bigBrother(s, root, false, xkaList);
				else
					bigBrother = null;
				if (bigBrother != null) {
					biglen = (float) gs.getDouble(bigBrother, true, Attributes.LENGTH);
					izb = gs.getInt(bigBrother, true, Attributes.INTERNODE_COUNT);
				} else {
					biglen = -1.0f;
					izb = -1;
				}
				if (iz >= 0)
					cousine = cousine(s, root, xkaList);
				else
					cousine = null;
				if (cousine != null)
					coulen = (float) gs.getDouble(cousine, true, Attributes.LENGTH);
				else
					coulen = -1;
				mother = getAssociatedMotherShoot(s);
				if (mother != null) {
					mlen = (float) gs.getDouble(mother, true, Attributes.LENGTH);
					sppos = (gs.getFloat(s, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(s, true, Attributes.REL_POSITION)) * mlen;
					izm = gs.getInt(mother, true, Attributes.INTERNODE_COUNT);
					Matrix4d matrix = LSystem.transformation(s);
					sSh.x = matrix.m02;
					sSh.y = matrix.m12;
					sSh.z = matrix.m22;
					matrix = LSystem.transformation(mother);
					motherSh.x = matrix.m02;
					motherSh.y = matrix.m12;
					motherSh.z = matrix.m22;
					wnkl = (float) Math.toDegrees(sSh.angle(motherSh));
				} else {
					mlen = -1.0f;
					sppos = 0.0f;
					izm = -1;
					wnkl = 0.0f;
				}
				
				Dataseries row = ds.addRow();
				row.set(0, new Long(s.getId()))
					.set(1, new Long((mother != null ? mother.getId() : -1)))
					.set(2, new Integer(aor))
					.set(3, new Integer(aal))
					.set(4, new Integer(integ(xkaList.get(s.getId()))))
					.set(5, new Double((gs.getFloat(s, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(s, true, Attributes.REL_POSITION))))
					.set(6, new Double(sppos))
					.set(7, new Double(wnkl))
					.set(8, new Double(alen))
					.set(9, new Integer(integ(ykaList.get(s.getId()))))
					.set(10, new Double(biglen))
					.set(11, new Double(coulen))
					.set(12, new Double(mlen))
					.set(13, new Double(iz))
					.set(14, new Long(izm))
					.set(15, new Integer(izb))
					.set(16, new Integer((iz < 0 ? 1 : 0)));
		    }
		}
		
		return ds;
	}
	
	/**
	 * Performs crown layers analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the crown layers data
	 */
	public static Dataset crownLayers(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("crown layers");
		ds.setColumnKey(0, "layerno.")
			.setColumnKey(1, "height of the lower layerborder (in mm)")
			.setColumnKey(2, "sum of the length in the layer (in mm)")
			.setColumnKey(3, "sum of the n-value in the layer");
		
		boolean cask;
		int ij, ijz, equimod;
		float[] nadsumm = new float[20];
		float[] lensumm = new float[20];
		float[] layerbd = new float[20];
		float layerdist;
		
		layerbd[0] = 0.0f;
		
		ConfigurationSet cs = new ConfigurationSet("Crown Layers");
		KeyDescription key1 = new KeyDescriptionImpl("equidistantLayers", Library.I18N, "analysis.equidistantLayers", Type.BOOLEAN, null);
		Configuration c = new Configuration(new KeyDescription[] {key1}, new StringMap().putBoolean("equidistantLayers", false));
		cs.add(c);
		Workbench.current().showConfigurationDialog(cs);
		cask = (Boolean) cs.get("equidistantLayers", null);
		
		if(!cask) {
			equimod = 0;
			
			for (int i = 1; i <= 3; i++) {
				cs = new ConfigurationSet("Crown Layers");
				key1 = new KeyDescriptionImpl("layerBound", Library.I18N.msg("analysis.layerBound", i), Type.FLOAT, Quantity.LENGTH);
				c = new Configuration(new KeyDescription[] {key1}, new StringMap().putFloat("layerBound", 1));
				cs.add(c);
				Workbench.current().showConfigurationDialog(cs);
				layerbd[i] = ((Float) cs.get("layerBound", null)).floatValue();
				layerbd[i] *= 1000.0f;
			}
			
			for(int i = 4; i <= 19; i++)
				layerbd[i] = 1000000.0f;
		} else {
			equimod = 1;
			cs = new ConfigurationSet("Crown Layers");
			key1 = new KeyDescriptionImpl("layerDistance", Library.I18N, "analysis.layerDistance", Type.FLOAT, Quantity.LENGTH);
			c = new Configuration(new KeyDescription[] {key1}, new StringMap().putFloat("layerDistance", 1));
			cs.add(c);
			Workbench.current().showConfigurationDialog(cs);
			layerdist = ((Float) cs.get("layerDistance", null)).floatValue();
			
			if(layerdist <= 0) {
				layerdist = 100.0f;
				Workbench.current().getWindow().showDialog("Crown Layers", "Layer distance <= 0. Set to 100.", Window.WARNING_MESSAGE);
			}
			layerdist *= 1000.0f;
			for(int i = 1; i <= 19; i++)
				layerbd[i] = layerbd[i-1] + layerdist;
		}
		
		Vector3d beginOfShoot = new Vector3d();
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		
		ij = 3+equimod*16;
		while((s = LSystem.nextShoot(it)) != null) {
			if((gs.getInt(s, true, Attributes.ORDER) >= 0) && (gs.getInt(s, true, Attributes.INTERNODE_COUNT) >= 0)) {
				ijz = 0;
				for (int i = 0; i <= ij; i++) {
					Math2.getBeginOfShoot(LSystem.transformation(s), beginOfShoot);
					if (beginOfShoot.z >= layerbd[i])
						ijz = i;
				}
				lensumm[ijz] += gs.getDouble(s, true, Attributes.LENGTH);
				nadsumm[ijz] += gs.getFloat(s, true, Attributes.PARAMETER);
			}
		}
		
		for(int i = 0; i <= ij; i++) {
			Dataseries row = ds.addRow();
			row.set(0, new Integer(i))
				.set(1, new Double(layerbd[i]))
				.set(2, new Double(lensumm[i]))
				.set(3, new Double(nadsumm[i]));
		}
		
		return ds;
	}
	
	/**
	 * Performs stem analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the stem analysis
	 */
	public static Dataset stemAnalysis(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("stem analysis");
		ds.setColumnKey(0, "timestep")
			.setColumnKey(1, "z-coordinate (segmentcenter)")
			.setColumnKey(2, "radius");
		
		float oz, hmerk, lng;
		int hor, trnb, ilauf, timestep, change;
		boolean found;
		oz = 42.0f;
		
		Dataseries row;
		Vector3d beginOfShoot = new Vector3d();
		Vector3d endOfShoot = new Vector3d();
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		
		while((s = LSystem.nextShoot(it)) != null) {
			if(gs.getInt(s, true, Attributes.ORDER) == 0) {
				Math2.getBeginOfShoot(LSystem.transformation(s), beginOfShoot);
				oz = (float) beginOfShoot.z;
				break;
			}
		}
		
		if(root == null)
			oz = 0.0f;
		
		ConfigurationSet cs = new ConfigurationSet("Stem Analysis");
		KeyDescription key1 = new KeyDescriptionImpl("treeNumber", Library.I18N, "analysis.treeNumber", Type.INT, Quantity.LENGTH);
		Configuration c = new Configuration(new KeyDescription[] {key1}, new StringMap().putFloat("treeNumber", 1));
		cs.add(c);
		Workbench.current().showConfigurationDialog(cs);
		trnb = (Integer) cs.get("treeNumber", null);
		if(trnb <= 0)
			trnb = 1;
		
		ilauf = 0;
		found = false;
		change = -1;
		hmerk = 0.0f;
		
		LSystem lsy = LSystem.current();
		if (lsy != null)
			timestep = lsy.getGenerationNo();
		else
			timestep = 0;
		
		it = new TreeIterator(root);
		while((s = LSystem.nextShoot(it)) != null) {
			hor = gs.getInt(s, true, Attributes.ORDER);
			lng = (float) gs.getDouble(s, true, Attributes.LENGTH);
			if(getAssociatedMotherShoot(s) == null)
				ilauf++;
			if(change > -1 && change == ilauf-1) {
				break;
			}
			if ((hor == 0) && (lng > Math2.EPSILON) && (ilauf == trnb)) {
				change = ilauf;
				if (found == false) {
					found = true;
					row = ds.addRow();
					row.set(0, new Integer(timestep))
						.set(1, new Double(0.0))
						.set(2, new Double(gs.getFloat(s, true, Attributes.RADIUS)));
						//.set(2, new Double(gs.getFloat(s, true, Attributes.HEARTWOOD) / 2.0));
				}
				
				Math2.getBeginAndEndOfShoot(LSystem.transformation(s), lng, beginOfShoot, endOfShoot);
				
				row = ds.addRow();
				row.set(0, new Integer(timestep))
					.set(1, new Double((beginOfShoot.z + endOfShoot.z) / 2.0 - oz))
					.set(2, new Double(gs.getFloat(s, true, Attributes.RADIUS)));
					//.set(2, new Double((gs.getFloat(s, true, Attributes.HEARTWOOD) + gs.getFloat(s, true, Attributes.RADIUS) * 2) / 4.0));
				
				hmerk = (float) (endOfShoot.z - oz);
			}
		}
		
		row = ds.addRow();
		row.set(0, new Integer(timestep))
			.set(1, new Double(hmerk))
			.set(2, new Double(0.0));
		
		return ds;
	}
	
	/**
	 * Performs distribution analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the distribution analysis
	 */
	public static Dataset distributionAnalysis(Node root) {
		Dataset ds = new Dataset();
		ds.setTitle("distribution analysis");
		Dataseries row;
		
		int[] nbeu = new int[MAX_ORDER];				// number of elementary units of given order
		int[] nbcu = new int[MAX_ORDER];				// nb. of compound units of given order
		int[] nbax = new int[MAX_ORDER];				// nb. of axes of given order
		int[][] dseucu = new int[MAX_ORDER][MAX_EUCU];	// distribution of eu per cu
		int[][] dseuax = new int[MAX_ORDER][MAX_EUAX];	// distribution of eu per axis
		int[][] dscuax = new int[MAX_ORDER][MAX_CUAX];	// distribution of cu per axis
		int maxeucu, maxeuax, maxcuax;					// actual maximal values
		int aeucu, aeuax, acuax;						// counting variables
		int efor, aor;									// efor = effective order
		maxeucu = maxeuax = maxcuax = 0;
		
		String colorString;
		boolean[] colorExclud = new boolean[16];
		HashMap<Long, Boolean> ykeList = new HashMap<Long, Boolean>();
		
		ConfigurationSet cs = new ConfigurationSet("Distribution Analysis");
		KeyDescription key1 = new KeyDescriptionImpl("treeColor", Library.I18N, "analysis.treeColor", Type.STRING, null);
		Configuration c = new Configuration(new KeyDescription[] {key1}, new StringMap().putObject("treeColor", ""));
		cs.add(c);
		Workbench.current().showConfigurationDialog(cs);
		colorString = (String) cs.get("treeColor", null);
		
		if(colorString != null && colorString.trim().length() > 0) {
			String[] singleColor = colorString.split(",");
			int j = 0;
			for(int i = 0; i < singleColor.length && i < 16; i++) {
				j = Integer.parseInt(singleColor[i].trim());
				if(j > -1 && j < 16)
					colorExclud[j] = true;
			}
		}
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s, n, mother;
		
		while((s = LSystem.nextShoot(it)) != null)
			ykeList.put(s.getId(), !colorExclud[gs.getInt(s, true, Attributes.DTG_COLOR)]);
		
		it = new TreeIterator(root);
		while((s = LSystem.nextShoot(it)) != null) {
			mother = getAssociatedMotherShoot(s);
			if((mother != null) && (gs.getInt(s, true, Attributes.ORDER) == gs.getInt(mother, true, Attributes.ORDER)) && !colorExclud[gs.getInt(s, true, Attributes.DTG_COLOR)])
				ykeList.put(mother.getId(), false);
		}
		
		int sOrder;
		
		it = new TreeIterator(root);
		while((s = LSystem.nextShoot(it)) != null) {		// analysis loop
			sOrder = gs.getInt(s, true, Attributes.ORDER);
			if((sOrder >= 0) && ykeList.get(s.getId())) {  // end of an axis
				aor = efor = sOrder;
				if(efor >= MAX_ORDER)
					efor = MAX_ORDER-1;
				nbax[efor]++;
				aeuax = acuax = aeucu = 0;
				
				GraphState gsN = s.getCurrentGraphState();
				n = s;
				while((n != null) && (gsN.getInt(n, true, Attributes.ORDER) == aor)) { // basipetal run down the axis
					mother = getAssociatedMotherShoot(n);
					if(!colorExclud[gsN.getInt(n, true, Attributes.DTG_COLOR)]) {
						nbeu[efor]++;
						aeucu++; aeuax++;
						if((gsN.getInt(n, true, Attributes.LOCAL_SCALE) > 0) || (mother == null) || (gsN.getInt(mother, true, Attributes.ORDER) != aor)) { // compound unit has to be counted
							if(aeucu >= MAX_EUCU)
								aeucu = MAX_EUCU-1;
							dseucu[efor][aeucu]++;
							if(aeucu > maxeucu)
								maxeucu = aeucu;
							aeucu = 0;
							nbcu[efor]++;
							acuax++;
							if((mother == null) || (gsN.getInt(mother, true, Attributes.ORDER) != aor)) { // axis is finished, i.e. base of axis
								if(aeuax >= MAX_EUAX)
									aeuax = MAX_EUAX-1;
								dseuax[efor][aeuax]++;
								if(aeuax > maxeuax)
									maxeuax = aeuax;
								if(acuax >= MAX_CUAX)
									acuax = MAX_CUAX-1;
								dscuax[efor][acuax]++;
								if(acuax > maxcuax)
									maxcuax = acuax;
							}
						}
					}
					n = mother;
				}
			}
		}
		
		// output
		int counter = 1;
		int numberCounter = 0;
		int maxEntries, eucuEntries, euaxEntries, cuaxEntries;
		
		row = ds.addRow();
		ds.setColumnKey(0, "number of elementary units : total");
		row.set(0, sumArray(nbeu));
		for(int i = 0; i < MAX_ORDER; i++) {
			ds.setColumnKey(counter, "order "+i);
			row.set(counter++, nbeu[i]);
		}
		
		ds.setColumnKey(counter, "number of compound units : total");
		row.set(counter++, sumArray(nbcu));
		for(int i = 0; i < MAX_ORDER; i++) {
			ds.setColumnKey(counter, "order "+i);
			row.set(counter++, nbcu[i]);
		}
		
		ds.setColumnKey(counter, "number of axes : total");
		row.set(counter++, sumArray(nbax));
		for(int i = 0; i < MAX_ORDER; i++) {
			ds.setColumnKey(counter, "order "+i);
			row.set(counter++, nbax[i]);
		}
		
		ds.setColumnKey(counter, "Elementary units per compound unit : number");
		row.set(counter++, numberCounter);
		ds.setColumnKey(counter, "total");
		row.set(counter++, sumArray(dseucu, 0));
		for(int i = 0; i < MAX_ORDER; i++) {
			ds.setColumnKey(counter, "order "+i);
			row.set(counter++, dseucu[i][0]);
		}
		
		ds.setColumnKey(counter, "Elementary units per axis : number");
		row.set(counter++, numberCounter);
		ds.setColumnKey(counter, "total");
		row.set(counter++, sumArray(dseuax, 0));
		for(int i = 0; i < MAX_ORDER; i++) {
			ds.setColumnKey(counter, "order "+i);
			row.set(counter++, dseuax[i][0]);
		}
		
		ds.setColumnKey(counter, "Compound units per axis : number");
		row.set(counter++, numberCounter);
		ds.setColumnKey(counter, "total");
		row.set(counter++, sumArray(dscuax, 0));
		for(int i = 0; i < MAX_ORDER; i++) {
			ds.setColumnKey(counter, "order "+i);
			row.set(counter++, dscuax[i][0]);
		}
		
		numberCounter++;
		eucuEntries = lastEntryInArray(dseucu);
		euaxEntries = lastEntryInArray(dseuax);
		cuaxEntries = lastEntryInArray(dscuax);
		maxEntries = Math.max(Math.max(eucuEntries, euaxEntries), cuaxEntries);
		
		if(maxEntries > 0) {
			while(numberCounter < maxEntries) {
				counter = 0;
				row = ds.addRow();
				for(counter = 0; counter < (3*MAX_ORDER+3); counter++)
					row.set(counter, -1);
				
				counter = fillRow(row, dseucu, counter, numberCounter, eucuEntries);
				counter = fillRow(row, dseuax, counter, numberCounter, euaxEntries);
				counter = fillRow(row, dscuax, counter, numberCounter, cuaxEntries);
				
				numberCounter++;
			}
		}
		
		return ds;
	}

	/**
	 * Performs topological analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the topological analysis
	 */
	public static Dataset topologicalAnalysis(Node root) {
		Dataset ds = new Dataset ();
		ds.setTitle("topological analysis");
		ds.setColumnKey (0, "number of components")
			.setColumnKey (1, "number of links")
			.setColumnKey (2, "number of exterior links")
			.setColumnKey (3, "discrepancy")
			.setColumnKey (4, "altitude (max.top.depth)")
			.setColumnKey (5, "mean top. depth b")
			.setColumnKey (6, "top. index qa")
			.setColumnKey (7, "top. index qb")
			.setColumnKey (8, "mean exterior link length")
			.setColumnKey (9, "mean interior link length");
		
		int nbco;
		long nbl, nbexl, discre, alti, sdep, mdep;
		float bdep, qa, qb, mle, mli, sle, sli, lbv0;
		ShootStructure s = null;
		ShootStructure bb;
		
		ShootStructure ggRoot = createGROGRAShootStructure(root);
		fusaxes(ggRoot);
		
		while(s != null)
			split(s, ggRoot);
		// initialize
		nbco = 0;
		nbl = nbexl = discre = alti = sdep = mdep = (long) 0;
		bdep = qa = qb = sle = sli = mle = mli = 0.0f;
		// go through structure: count links and components, set gen to 0 in all elements
		for(s = ggRoot; s != null; s = s.getSuccessor()) {
			s.setGenerativeDistance(0);
			if((s.getColor() != INVISIBLE) || (s.getLength() > Math2.EPSILON)) {
				nbl++;
				s.setYke(0.0f);
			} else
				s.setYke(1.0f);  /* marker for "improper" elements */
			if(s.getMother() == null)
				nbco++;
		}
		/* go through structure: memorize outdegree in gen */
		for(s = ggRoot; s != null; s = s.getSuccessor()) {
			if(s.getMother() != null)
				s.getMother().setGenerativeDistance(s.getMother().getGenerativeDistance() + 1);
		}
		// go through structure: count links of different degrees
		for(s = ggRoot; s != null; s = s.getSuccessor()) {
			if(s.getGenerativeDistance() == 0) { /* exterior link */
				if(s.getYke() < Math2.EPSILON) {
					nbexl++;
					sle += s.getLength();
					mdep = (long) 0;
					bb = s;
					while(bb != null) {
						if(bb.getYke() < Math2.EPSILON)
							mdep++;
						bb = bb.getMother();
					}
					if(mdep > alti)
						alti = mdep;
					sdep += mdep;
				}
			} else {
				if(s.getYke() < Math2.EPSILON) {
					sli += s.getLength();
					discre += (2 - s.getGenerativeDistance());
				}
			}
		}
		/* final calculations */
		if (nbexl > 0)
			bdep = (float) sdep / (float) nbexl;
		else
			bdep = 0.0f;
		if (nbexl > 0)
			mle = sle / (float) nbexl;
		else
			mle = 0.0f;
		if (nbl - nbexl > 0)
			mli = sli / ((float) nbl - (float) nbexl);
		else
			mli = 0.0f;
		lbv0 = (float) (Math.log(nbexl) / Math.log(2.0));
		qa = ((float) alti - 1.0f - lbv0) / ((float) nbexl - 1.0f - lbv0);
		if (nbexl > 0)
			qb = (bdep - 1.0f - lbv0) / ( (((float) nbexl + 1.0f)/2.0f) - (1.0f/(float) nbexl) - lbv0);
		else
			qb = 0.0f;
		
		Dataseries row;
		row = ds.addRow();
		row.set(0, nbco).
			set(1, nbl).
			set(2, nbexl).
			set(3, discre).
			set(4, alti).
			set(5, bdep).
			set(6, qa).
			set(7, qb).
			set(8, mle).
			set(9, mli);
		
		return ds;
	}
	
	/**
	 * Performs axes analysis from GROGRA on the graph starting at <code>root</code>.
	 * 
	 * @param root start node
	 * @return dataset containing the axes analysis
	 */
	public static Dataset axesAnalysis(Node root) {
		Dataset ds = new Dataset ();
		ds.setTitle("topological analysis");
		ds.setColumnKey (0, "number of the axis")
			.setColumnKey (1, "branching order")
			.setColumnKey (2, "length of the axis")
			.setColumnKey (3, "diameter")
			.setColumnKey (4, "color")
			.setColumnKey (5, "number of daughter axes")
			.setColumnKey (6, "average interbranch distance (pos(n) - pos(1))/(n-1)")
			.setColumnKey (7, "std deviation of interbranch distance");
		
		long jj;
		float my, sigm;
		Dataseries row;
		ShootStructure s;
		ShootStructure ggRoot = createGROGRAShootStructure(root);
		ShootStructure[] daughters = new ShootStructure[MAX_DAUGHTERS];
		float[] mavek = new float[MAX_DAUGHTERS];
		float[] avek = new float[MAX_DAUGHTERS];
		
		fuse(ggRoot);
		jj = 0L;
		for(s = ggRoot; s != null; s = s.getSuccessor()) {
			jj++;
			createArraysForSplit(s, ggRoot, 2, mavek, avek, daughters);
			if(countDaughters < 2)
				my = -1.0f;
			else
				my = (avek[countDaughters]-avek[1])/(float)(countDaughters-1);
			sigm = 0.0f;
			if(countDaughters >= 3) {
				for(int i = 2; i <= countDaughters; i++)
					sigm += (mavek[i]-my)*(mavek[i]-my);
				sigm = (float) Math.sqrt((double) sigm / (double)(countDaughters-2));
			}
			
			row = ds.addRow();
			row.set(0, jj).
				set(1, s.getOrder()).
				set(2, s.getLength()).
				set(3, s.getHeartwoodDiameter()).
				set(4, s.getColor()).
				set(5, countDaughters).
				set(6, my).
				set(7, sigm);
		 }
		
		return ds;
	}
	
	private static void fuse(ShootStructure root) {
		ShootStructure shoota;
		ShootStructure shootb;
		ShootStructure shootc;
		if(root != null) {
			shoota = root;
			while(shoota != null) {
				shootb = shoota.getMother();
				shootc = shoota.getSuccessor();
				if(shootb != null) {
					if(((shoota.getOrder()) == (shootb.getOrder())) && ((shoota.getOrder()) >= 0))
						fusion(shootb, shoota, root);
				}
				shoota = shootc;
			}
		}
	}
	
	private static void split(ShootStructure s, ShootStructure root) {
		ShootStructure next = null;
		ShootStructure part = null;
		ShootStructure last = null;
		ShootStructure middle = null;
		ShootStructure pre = null;
		ShootStructure suc = null;
		ShootStructure[] daughters = new ShootStructure[MAX_DAUGHTERS];
		float[] mavek = new float[MAX_DAUGHTERS];
		float[] avek = new float[MAX_DAUGHTERS];
		boolean fini;
		int k;
		Vector3d hivek = new Vector3d();
		fini = false;
		next = s.getSuccessor();
		if ((s == null) || (root == null) || (s.getLength() < Math2.EPSILON)) {
			System.out.println("Exceptional situation in aufspalte (null or l.<epsilon)");
			s = next;
			fini = true;
		} else {
			if (s == root)  {
				pre = null;
				middle = null;
			} else {
				pre = root;
				suc = pre.getSuccessor();
				while ((suc != null) && (suc != s)) {
					pre = suc;
					suc = pre.getSuccessor();
				}
				middle = s.getMother();
			}
		}
		if(!fini) {
			createArraysForSplit(s, root, 0, mavek, avek, daughters);
			/* Aufspaltung */
			last = pre;
			for(int j = 1; j <= countDaughters+1; j++) {
				if((mavek[j] > (mavek[j-1] + Math2.EPSILON)) || ((j == countDaughters+1) && (mavek[j] <= mavek[0]+Math2.EPSILON))) {
					part = new ShootStructure();
					part.setMother(middle);
					part.setLength(mavek[j] - mavek[j-1]);
					part.setDiameter(s.getDiameter());
					part.setHeartwoodDiameter(s.getHeartwoodDiameter());
					if (s.getLength() > Math2.EPSILON)
						part.setN(part.getLength() * s.getN() / s.getLength());
					else
						part.setN(s.getN());
					part.setInternodeCount(0);
					if(s.getN() > Math2.EPSILON)
						part.setInternodeCount(integ((float)(part.getN() / s.getN() * s.getInternodeCount()))); /* proportional zu nad */
					//part->lr = (*s)->lr;
					part.setColor(s.getColor());
					part.setOrder(s.getOrder());
					part.setGenerativeDistance(s.getGenerativeDistance());
					part.setScale(0);
					part.setQ(0.0f);
					hivek.scale(mavek[j-1], s.getSh());
					part.getBeginOfShoot().add(s.getBeginOfShoot(), hivek);
					hivek.scale(mavek[j], s.getSh());
					part.getEndOfShoot().add(s.getBeginOfShoot(), hivek);
					part.setSh(s.getSh());
					part.setSl(s.getSl());
					part.setSu(s.getSu());
					part.setSuccessor(null);
					//part->ob = null;
					part.setXka(0.0f);
					part.setYka(0.0f);
					part.setXke(0.0f);
					part.setYke(0.0f);
					part.setForwardChain(s.getForwardChain());
					if(last == null) {
						root = part;
						part.setId(1);
					} else {
						last.setSuccessor(part);
						part.setId(last.getId() + 1);
					}
					k = j-1;
					while((mavek[k] == mavek[j-1]) && (k > 0)) {
						daughters[k].setMother(middle);
						daughters[k].setQ(0.0f);
						k--;
					}
					last = part;
					middle = part;
					if(mavek[j] > (s.getLength() - Math2.EPSILON)) {
						for(k = j; k <= countDaughters; k++) {
							daughters[k].setMother(part);
							daughters[k].setQ(0.0f);
						}
						part.setSuccessor(next);
					}
				}
			}
			s = null;
			s = next;
		}
	}
	
	private static void createArraysForSplit(ShootStructure s, ShootStructure root, int dropmain, float[] mavek, float[] avek, ShootStructure[] daughters) {
		float[] mivek = new float[MAX_DAUGHTERS];
		ShootStructure search;
		ShootStructure temp = null;
		float merka;
		int insj, k;
		if(s == null) {
			System.out.println("Exceptional situation in erztarray (null).");
			return;
		}
		
		countDaughters = 0;
		for(int j = 0; j < MAX_DAUGHTERS; j++) {
			avek[j] = 0.0f;
			mavek[j] = 0.0f;
			if(dropmain < 2) {
				daughters[j] = null;
				mivek[j] = MIN_DISTANCE;
			}
		}
		search = root;
		while(search != null) {
			if((search.getMother() == s) && ((dropmain==0) || (search.getOrder() != s.getOrder()))) {
				if(countDaughters < MAX_DAUGHTERS - 2)
					countDaughters++;
				else
					System.out.println("Overflow in erztarray (too many daughters)!"); 
				if(dropmain < 2)
					daughters[countDaughters] = search;
				avek[countDaughters] = (1.0f - search.getQ()) * s.getLength();
				search = search.getSuccessor();
			}
			for(int j = 2; j <= countDaughters; j++) {
				merka = avek[j];
				if(dropmain < 2)
					temp = daughters[j];
				insj = 1;
				while((avek[insj] <= merka) && (insj < j))
					insj++;
				if(insj < j) {
					for(k=j; k > insj; k--) {
						avek[k] = avek[k-1];
						if(dropmain < 2)
							daughters[k] = daughters[k-1];
					}
					avek[insj] = merka;
					if(dropmain < 2)
						daughters[insj] = temp;
				}
			}
			avek[countDaughters+1] = s.getLength();
			if(dropmain < 2) {
				for(int j = 0; j <= countDaughters+1; j++) {
					mivek[j] = localMinimalDistanceGROGRA(s, (avek[j] + avek[j+1]) / 2.0f);
				}
				for(int j = 0; j < MAX_DAUGHTERS; j++)
					mavek[j] = avek[j];
			} else {
				for(int j = 1; j <= countDaughters+1; j++)
					mavek[j] = avek[j] - avek[j-1];
			}
		}
	}
	
	private static float localMinimalDistanceGROGRA(ShootStructure s, float abpos) {
		float resu;
		resu = 0.0f;
		if(resu < MIN_DISTANCE)
			resu = MIN_DISTANCE;
		if(s.getInternodeCount() < 0)
			resu = 0.0f;
		if(resu > 1E9f) {
			System.out.println("Very large effective crit. distance found for axis nb. "+s.getId()+" (eff. CD: "+resu+")");
			System.out.println("   cannot be used in dissection. Maintaining former length.");
			resu = s.getLength() - Math2.EPSILON;
		}
		return resu;
	}
	
	private static void fusaxes(ShootStructure root) {
		ShootStructure s;
		ShootStructure n, mother, temp;
		if(root == null) {
			System.out.println("WARNING: Exceptional situation in fusaxes (NULL).");
			return;
		}
		for(s = root; s != null; s = s.getSuccessor()) {
			s.setYke(1.0f);
			s.setGenerativeDistance(0);
		}
			
		for(s = root; s != null; s = s.getSuccessor()) {
			mother = s.getMother();
			if(mother != null) {
				if(s.getOrder() == mother.getOrder()) {
					mother.setYke(0.0f);
				}
			}
		}
		
		n = root;
		while(n != null) {  /* wenn Fusion mit slauf->mutter erfolgreich moeglich, basipetaler Abstieg; sonst weiter ueber nachf-Liste */
			if((n.getOrder() >= 0) && (n.getYke() > 0.5)) {
				s = n.getMother();
				if(s != null) {
					temp = s.getForwardChain();
					if(temp != null) {
						if((temp.getForwardChain() == n.getForwardChain()) && (s.getInternodeCount() >= 0) && (n.getInternodeCount() >= 0)) {
							fusion(s, n, root);
							n = s;
							n.setYke(1.0f);
						} else
							n = n.getSuccessor();
					} else
						n = n.getSuccessor();
				} else
					n = n.getSuccessor();
			} else
				n = n.getSuccessor();
		}
		
		for(s = root; s != null; s = s.getSuccessor()) {
			mother = s.getMother();
			if(mother != null)
				mother.setGenerativeDistance(1);  /* wird in aufspalte gebraucht */
		}
	}
	
	private static void fusion(ShootStructure s, ShootStructure n, ShootStructure root) {
		float ala, bla, sla;
		ShootStructure search;
		ShootStructure temp;
		Vector3d vvh = new Vector3d();
		Vector3d tempSh;
		
		if((s == null) || (n == null)) {
			System.out.println("Error: NULL occurred in function  f u s i o n");
			return;
		}
		
		if((s.getInternodeCount() < 0) || (n.getInternodeCount() < 0))
			System.out.println("Warning: suspicious fusion (izahl < 0)!");
		
		ala = s.getLength();
		bla = n.getLength();
		sla = ala + bla;
		s.setLength(sla);
		s.setDiameter(n.getDiameter());
		s.setN(s.getN() + n.getN());
		s.setInternodeCount(s.getInternodeCount() + n.getInternodeCount());
		vvh.scale(sla, s.getSh());
		vvh.add(s.getBeginOfShoot());
		s.setEndOfShoot((Vector3d)vvh.clone());
		temp = null;
		for(search = root; search != null; search = search.getSuccessor()) {
			if((search.getMother() == s) && (search != n)) {
				if(sla > Math2.EPSILON)
					search.setQ(1.0f - (ala / sla) * (1.0f - search.getQ()));
				else
					search.setQ(1.0f);
			}
			if(search.getMother() == n) {
				if(sla > Math2.EPSILON)
					search.setQ(1.0f - ((ala / sla) + (1.0f - search.getQ()) * (bla / sla)));
				else
					search.setQ(0.f);
				search.setOrder(s.getOrder() + search.getOrder() - n.getOrder());
				tempSh = (Vector3d) s.getSh().clone();
				tempSh.sub(n.getSh());
				if(tempSh.length() >= Math2.EPS) { /* Verschiebung notwendig */
					vvh.scale(sla, s.getSh());
					vvh.scale(1.0 - search.getQ());
					vvh.add(s.getBeginOfShoot());
					/* neuer Anfangspunkt des Tochtersprosses suchto */
					vvh.sub(search.getBeginOfShoot());
					relocate(search, vvh, root);
				}
				search.setMother(s);
			}
			if(search.getSuccessor() == n)
				temp = search;
		}
		/* Entfernen von (*bspr) aus der Liste: */
		if(temp != null)
			temp.setSuccessor(n.getSuccessor());
		n = null;
	}

	private static void relocate(ShootStructure search, Vector3d vve, ShootStructure root) {
		ShootStructure s;
		ShootStructure n;
		boolean isOffspring;
		if(search == null) {
			System.out.println("Exceptional situation in verschiebe (NULL).");
			return;
		}

		for(s = root; s != null; s = s.getSuccessor()) {
			isOffspring = false;
			n = s;
			while((n != null) && (!isOffspring)) {
				if(n == search)
					isOffspring = true;
				n = n.getMother();
			}
			if(isOffspring) { /* eigentliche Verschiebung */
				s.getBeginOfShoot().add(vve);
				s.getEndOfShoot().add(vve);
			}
		}
	}
	
	/**
	 * Fill the row with the given data ... otherwise with -1
	 * @param row The row to be filled
	 * @param array The data-array
	 * @param counter The current counter in the row
	 * @param numberCounter Entry-number
	 * @param eucuEntries The last relevant index in the array
	 * @return
	 */
	private static int fillRow(Dataseries row, int[][] array, int counter, int numberCounter, int eucuEntries) {
		if(numberCounter < eucuEntries) {
			row.set(counter++, numberCounter);
			row.set(counter++, sumArray(array, numberCounter));
			for(int i = 0; i < 5; i++) {
				row.set(counter++, array[i][numberCounter]);
			}
		} else {
			row.set(counter++, -1);
			row.set(counter++, -1);
			for(int i = 0; i < MAX_ORDER; i++)
				row.set(counter++, -1);
		}
		
		return counter;
	}
	
	/**
	 * What is the last entry in the array?
	 * @param array Array with values
	 * @return Index of last entry (index +1 -> for loop iterations)
	 */
	private static int lastEntryInArray(int[][] array) {
		int i;
		for(i = array.length-1; i > -1; i--) {
			for(int n : array[i]) {
				if(n != 0)
					return i+1;
			}
		}
		return i;
	}
	
	/**
	 * Sum of all values in a int-array
	 * @param array the int-array with values
	 * @return the sum
	 */
	private static int sumArray(int[] array) {
		int sum = 0;
		for(int n : array)
			sum += n;
		return sum;
	}
	
	/**
	 * Sum of all values in a 2D int-array (sum of only one row)
	 * @param array the 2D int-array with values
	 * @param index index for the 2D array[][index]
	 * @return the sum
	 */
	private static int sumArray(int[][] array, int index) {
		int sum = 0;
		for(int i = 0; i < MAX_ORDER; i++) {
			sum += array[i][index];
		}
		return sum;
	}
	
	/**
	 * Searching for the cousine of current in the graph
	 * @param current The current shoot
	 * @param root The root of the graph
	 * @param xkaList Needs a list of special auxialiary variables (in GroGra xka)
	 * @return Bigbrother shoot
	 */
	private static Shoot cousine(Shoot current, Node root, HashMap<Long, Float> xkaList) {
		Shoot s, uroma, ahnfrau, res;
		int ahnz, clustind, jj;
		if((current == null) || (root == null)) {
			System.out.println("Warning: NULL in method COUSINE!");
			return null;
		}
		
		TreeIterator it = new TreeIterator(root);
		
		ahnz = 0;
		ahnfrau = current;
		while((ahnfrau != null) && (integ(xkaList.get(ahnfrau.getId())) == 0)) {
			ahnfrau = getAssociatedMotherShoot(ahnfrau);
			ahnz++;
		}
		if(ahnfrau == null)
			return null;
		clustind = integ(xkaList.get(ahnfrau.getId()));
		ahnfrau = getAssociatedMotherShoot(ahnfrau);
		if(ahnfrau == null)
			return null;
		if((integ(xkaList.get(ahnfrau.getId())) != 0) || (getAssociatedMotherShoot(ahnfrau) == null))
			return null;
		uroma = getAssociatedMotherShoot(ahnfrau);
		ahnz++;
		res = null;
		while(((s = LSystem.nextShoot(it)) != null) && (res == null)) {
			ahnfrau = s;
			jj = 0;
			while((ahnfrau != null) && (integ(xkaList.get(ahnfrau.getId())) == 0) && (jj < ahnz)) {
				ahnfrau = getAssociatedMotherShoot(ahnfrau);
				jj++;
			}
			if((ahnfrau != null) && (jj == ahnz) && (integ(xkaList.get(ahnfrau.getId())) == clustind) && (getAssociatedMotherShoot(ahnfrau) == uroma))
				res = s;
		}
		return res;
	}
	
	/**
	 * Labeling the graph with clusterindices
	 * @param root The root of the graph
	 * @param xkaList List of auxiliary variabels with the clusters after method invocation
	 * @param ykaList List of auxiliary variabels with the numbers of daughter-clusters after method invocation
	 */
	private static void labelDaughters(Node root, HashMap<Long, Float> xkaList, HashMap<Long, Float> ykaList,
			float clusterDistance, Shoot[] daughters, float[] avek, float[] mavek, float[] mivek) {
		float altp, neup, midis, madis;
		int tocind;
		midis = clusterDistance;
		madis = 1E9f;
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		
		// Initialize HashMap xkaList with all node-ID and 0
		while((s = LSystem.nextShoot(it)) != null) {
			xkaList.put(s.getId(), 0.0f);
		}
		
		it = new TreeIterator(root);
		while((s = LSystem.nextShoot(it)) != null) {
			createArraysForLabeling(s, root, 1, false, 0.0f, midis, madis, daughters, avek, mavek, mivek, xkaList);
			while(modifeinschr(0, mavek, mivek)) ;
			
			if(countDaughters > 0) {
				altp = mavek[countDaughters];
				tocind = 1;
				for(int i = countDaughters; i > 0; i--) {
					neup = mavek[i];
					if(neup < altp - Math2.EPSILON)
						tocind++;
					if(gs.getInt(s, true, Attributes.ORDER) >= 0)
						xkaList.put(daughters[i].getId(), (float) tocind);
					else
						xkaList.put(daughters[i].getId(), 0.0f);
					altp = neup;
				}
			} else
				tocind = 0;
			
			ykaList.put(s.getId(), (float) tocind);
		}
	}
	
	private static boolean modifeinschr(int mitrand, float[] mavek, float[] mivek) {
		int k, jmerk, untgr, obgr;
		float klv, lae, absta, val1, val2, valneu;
		boolean result;
		
		if(countDaughters == 0)
			return false;
		
		switch(mitrand) {
			case 2:		untgr = 0;
						obgr = countDaughters;
						break;
			case 1:		untgr = 1;
						obgr = countDaughters;
						break;
			default: 	untgr = 1;
						obgr = countDaughters-1;
		}
		lae = mavek[countDaughters + 1];
		klv = 100000;
		jmerk = 0;
		for(int j = obgr; j >= untgr; j--) {
			absta = mavek[j+1] - mavek[j];
			if((absta > Math2.EPSILON) && ((absta / mivek[j]) < klv)) {
				klv = absta / mivek[j];
				jmerk = j;
			}
		}
		if ((klv < 1.) && (lae > Math2.EPSILON)) {
			result = true;
			val1 = mavek[jmerk];
			val2 = mavek[jmerk+1];
			valneu = (val1 + val2) / 2.0f;
			if((mitrand == 2) && (val1 < Math2.EPSILON))
				valneu = 0.0f;
			if((mitrand > 0) && (val2 > lae - Math2.EPSILON))
				valneu = lae;
			if((mitrand == 2) && (val1 < Math2.EPSILON) && (val2 > lae - Math2.EPSILON))
				return false;
			else {
				k = jmerk;
				while((k >= untgr) && (mavek[k] > val1 - Math2.EPSILON)) {
					mavek[k] = valneu;
					k--;
				}
				k = jmerk+1;
				while((k <= obgr+1) && (mavek[k] < val2 + Math2.EPSILON)) {
					if((valneu < Math2.EPSILON) && (k == countDaughters+1))
						result = false;
					else
						mavek[k] = valneu;
					k++;
				}
			}
		} else
			return false;
		
		return result;
	}
	
	private static void createArraysForLabeling(Shoot s, Node root, int dropmain, boolean lokber, float thr,
			float midis, float madis, Shoot[] daughters, float[] avek, float[] mavek, float[] mivek, HashMap<Long, Float> xkaList) {
		float merka, sLength;
		int insj;
		
		if(s == null) {
			System.out.println("Given shoot s in createArraysForLabeling is null!");
			return;
		}
		
		countDaughters = 0;
		if(dropmain < 2) {
			for(int j = 0; j < MAX_DAUGHTERS; j++) {
				mivek[j] = midis;
			}
		}
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot n, temp = null;
		
		sLength = (float) gs.getDouble(s, true, Attributes.LENGTH);
		
		while((n = LSystem.nextShoot(it)) != null) {
			if ((getAssociatedMotherShoot(n) == s) && ((dropmain==0) || (gs.getInt(n, true, Attributes.ORDER) != gs.getInt(s, true, Attributes.ORDER)))) {
				if (countDaughters < MAX_DAUGHTERS - 2)
					countDaughters++;
				else {
					System.out.println("To many daughters in createArraysForLabeling");
				}
				if (dropmain < 2)
					daughters[countDaughters] = n;
				avek[countDaughters] = (1 - (gs.getFloat(n, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(n, true, Attributes.REL_POSITION))) * sLength;
			}
		}
		
		for (int j = 2; j <= countDaughters; j++) {
			merka = avek[j];
			if (dropmain < 2)
				temp = daughters[j];
			insj = 1;
			while((avek[insj] <= merka) && (insj < j))
				insj++;
			if(insj < j) {
				for(int k = j; k > insj; k--) {
					avek[k] = avek[k-1];
					if(dropmain < 2)
						daughters[k] = daughters[k-1];
				}
				avek[insj] = merka;
				if(dropmain < 2)
					daughters[insj] = temp;
			}
		}
		
		avek[countDaughters+1] = sLength;
		
		if(dropmain < 2) {
			for(int j = 0; j <= countDaughters+1; j++) {
				mivek[j] = localMinimalDistance(s, (avek[j] + avek[j+1]) / 2.0f, lokber, thr, gs, midis, madis, xkaList);
		    }
			for(int j = 0; j < MAX_DAUGHTERS; j++)
				mavek[j] = avek[j];
		} else {
			for(int j = 1; j <= countDaughters+1; j++)
				mavek[j] = avek[j] - avek[j-1];
		}
	}
	
	private static float localMinimalDistance(Shoot s, float abpos, boolean lokber, float thr, GraphState gs, float midis, float madis, HashMap<Long, Float> xkaList) {
		float resu;
		float[] diaNadu = {0.0f, 0.0f};
		
		Shoot temp = null;
		if(lokber) {
			durna(s, abpos, diaNadu, temp, gs);
			if(temp == null) {
				System.out.println("Warning: NULL occurred in localMinimalDistance");
				resu = 0.0f;
			} else {
				resu = thr * xkaList.get(s.getId()) * 1000.0f;
			}
		} else
			resu = 0.0f;
		if(resu < midis)
			resu = midis;
		if (gs.getInt(s, true, Attributes.INTERNODE_COUNT) < 0)
			resu = 0.0f;
		if (resu > madis) {
			System.out.println("Very large crit. distance found for axis nb. "+s.getId()+" cannot be used in dissection. Maintaining former length.");
			resu = (float) (gs.getDouble(s, true, Attributes.LENGTH) - Math2.EPSILON);
		}
		return resu;
	}
	
	// INFO: float[] diaNadu really needed? !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	private static void durna(Shoot s, float abpos, float[] diaNadu, Shoot temp, GraphState gs) {
		Shoot runShoot, oldShoot;
		float rpos, runLength;
		runShoot = getAssociatedMotherShoot(s);
		oldShoot = null;
		rpos = abpos;
		runLength = (float) gs.getDouble(runShoot, true, Attributes.LENGTH);
		
		while((runShoot != null) && (rpos > runLength)) {
			rpos -= runLength;
			oldShoot = runShoot;
			runShoot = getForwardChaining(runShoot);
			if(runShoot != null)
				runLength = (float) gs.getDouble(runShoot, true, Attributes.LENGTH);
		}
		
		if(runShoot == null) {
			if(oldShoot == null) {
				temp = getForwardChaining(s);
				diaNadu[0] = gs.getFloat(s, true, Attributes.RADIUS) * 2;
				diaNadu[1] = (float) (gs.getFloat(s, true, Attributes.PARAMETER) / (gs.getDouble(s, true, Attributes.LENGTH) + Math2.EPSILON));
			} else {
				temp = oldShoot;
				diaNadu[0] = gs.getFloat(oldShoot, true, Attributes.RADIUS) * 2;
				float oldLength = (float) gs.getDouble(oldShoot, true, Attributes.LENGTH);
				if(oldLength < Math2.EPSILON)
					diaNadu[1] = 0.0f;
				else
					diaNadu[1] = gs.getFloat(oldShoot, true, Attributes.PARAMETER) / oldLength;
			}
		} else {
			temp = runShoot;
			float runHeartwood = gs.getFloat(runShoot, true, Attributes.HEARTWOOD);
			if(runLength < Math2.EPSILON) {
				diaNadu[0] = runHeartwood;
				diaNadu[1] = 0.0f;
			} else {
				diaNadu[0] = runHeartwood + (rpos / runLength) * (gs.getFloat(runShoot, true, Attributes.RADIUS) * 2 - runHeartwood);
				diaNadu[1] = gs.getFloat(runShoot, true, Attributes.PARAMETER) / runLength;
			}
		}
	}
	
	/**
	 * Searching for the bigbrother of current in the graph
	 * @param current The brother shoot
	 * @param root The root of the graph
	 * @param opt3 Is method used by analysis "option 3"?
	 * @param xkaList Needs a list of special auxialiary variables (in GroGra xka)
	 * @return Bigbrother shoot
	 */
	private static Shoot bigBrother(Shoot current, Node root, boolean opt3, HashMap<Long, Float> xkaList) {
		Shoot result, s;
		
		if((current == null) || (root == null))
			return null;
		
		if(!opt3 && integ(xkaList.get(current.getId())) == 0)
			return current;
		
		result = null;
		
		TreeIterator it = new TreeIterator(root);
		
		while(((s = LSystem.nextShoot (it)) != null) && (result == null)) {
			if ((getAssociatedMotherShoot(s) == getAssociatedMotherShoot(current)) && ((opt3) || (integ(xkaList.get(s.getId())) == 0)))
				result = s;
		}
		
		return result;
	}
	
	private static void outerbox(Node root, Vector3d ba1, Vector3d ba2, Vector3d ba3, Vector3d nuv, Vector3d extv) {
		Vector3d exmin = new Vector3d();
		Vector3d exmax = new Vector3d();
		Vector3d hv = new Vector3d();
		Vector3d temp = new Vector3d();
		boolean erst;
		
		if(root == null)
			return;
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		erst = true;
		
		while(((s = LSystem.nextShoot (it)) != null)) {
			Math2.getBeginOfShoot(LSystem.transformation(s), temp);
			Math2.onbco(hv, temp, ba1, ba2, ba3);
			Math2.getEndOfShoot(LSystem.transformation(s), gs.getDouble(s, true, Attributes.LENGTH), temp);
			
			if (erst) {
				exmin.x = exmax.x = hv.x;
				exmin.y = exmax.y = hv.y;
				exmin.z = exmax.z = hv.z;
				erst = false;
				Math2.onbco(hv, temp, ba1, ba2, ba3);
				if(hv.x < exmin.x)
					exmin.x = hv.x;
				if(hv.y < exmin.y)
					exmin.y = hv.y;
				if(hv.z < exmin.z)
					exmin.z = hv.z;
				if(hv.x > exmax.x)
					exmax.x = hv.x;
				if(hv.y > exmax.y)
					exmax.y = hv.y;
				if(hv.z > exmax.z)
					exmax.z = hv.z;
			} else {
				if(hv.x < exmin.x)
					exmin.x = hv.x;
				if(hv.y < exmin.y)
					exmin.y = hv.y;
				if(hv.z < exmin.z)
					exmin.z = hv.z;
				if(hv.x > exmax.x)
					exmax.x = hv.x;
				if(hv.y > exmax.y)
					exmax.y = hv.y;
				if(hv.z > exmax.z)
					exmax.z = hv.z;
				Math2.onbco(hv, temp, ba1, ba2, ba3);
				if(hv.x < exmin.x)
					exmin.x = hv.x;
				if(hv.y < exmin.y)
					exmin.y = hv.y;
				if(hv.z < exmin.z)
					exmin.z = hv.z;
				if(hv.x > exmax.x)
					exmax.x = hv.x;
				if(hv.y > exmax.y)
					exmax.y = hv.y;
				if(hv.z > exmax.z)
					exmax.z = hv.z;
			}
		}
		
		extv = (Vector3d) exmax.clone();
		extv.sub(exmin);
		temp = (Vector3d) ba1.clone();
		temp.scale(exmin.x);
		hv = (Vector3d) ba2.clone();
		hv.scale(exmin.y);
		temp.add(hv);
		hv = (Vector3d) ba3.clone();
		hv.scale(exmin.z);
		temp.add(hv);
	    nuv = temp;
	}
	
	private static int integ(float ar) {
		if (ar > (float) Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else {
			if (ar < (float) Integer.MIN_VALUE)
				return Integer.MIN_VALUE;
			else
				return ((int) Math.floor(ar + Math2.EPSILON));
		}
	}
	
	/**
	 * Searching the graph starting at <code>n</code> for the greates generativeDistance-value
	 * 
	 * @param n start node
	 * @return Maximum value of generativeDistance
	 */
	private static int findGenerationMax(Node n) {
		int genmax, currentGen;
		
		GraphState gs = n.getCurrentGraphState();
		TreeIterator it = new TreeIterator(n);
		Shoot s;
		
		genmax = 0;
		
		while((s = LSystem.nextShoot (it)) != null) {
			currentGen = gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE);
			if(currentGen < 0)
				currentGen = -currentGen;
			if(currentGen > genmax)
				genmax = currentGen;
		}
		
		return genmax;
	}
	
	private static boolean terminalShoot(Shoot s, Node root) {
		boolean result = true;
		
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot n;
		
		while((n = LSystem.nextShoot (it)) != null && result) {
			if((getAssociatedMotherShoot(n) == s) && ((gs.getFloat(n, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(n, true, Attributes.REL_POSITION)) < Math2.EPSILON))
				result = false;
		}
		
		return result;
	}
	
	private static Shoot getForwardChaining(Node n) {
		do {
			n = n.findAdjacent (false, true, Graph.SUCCESSOR_EDGE);
		} while ((n != null) && !(n instanceof Shoot));
//		while ((n != null) && !(n instanceof Shoot)) {
//			n = n.findAdjacent(false, true, Graph.SUCCESSOR_EDGE);
//		}
		return (Shoot) n;
	}
	
	private static Shoot getAssociatedMotherShoot(Node n) {
		do {
			n = n.findAdjacent (true, false, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE);
		} while ((n != null) && !(n instanceof Shoot));
		return (Shoot) n;
	}
	
	/**
	 * Create a structure like the shoot-structure of GROGRA 
	 * @param root Root of the GroIMP-structure
	 * @return Root of the GROGRA-structure
	 */
	private static ShootStructure createGROGRAShootStructure(Node root) {
		GraphState gs = root.getCurrentGraphState();
		TreeIterator it = new TreeIterator(root);
		Shoot s;
		Matrix4d matrix;
		float length;
		ShootStructure ggRoot = null, current = null, mother = null;
		HashMap<Long, ShootStructure> map = new HashMap<Long, ShootStructure>();
		
		while((s = LSystem.nextShoot(it)) != null) {
			mother = current;
			current = new ShootStructure();
			if(ggRoot == null)
				ggRoot = current;
			
			Vector3d shootBegin = new Vector3d();
			Vector3d shootEnd = new Vector3d();
			length = (float) gs.getDouble(s, true, Attributes.LENGTH);
			matrix = LSystem.transformation(s);
			Math2.getBeginAndEndOfShoot(matrix, length, shootBegin, shootEnd);
			map.put(s.getId(), current);
			
			current.setBeginOfShoot(shootBegin);
			current.setColor(gs.getInt(s, true, Attributes.DTG_COLOR));
			current.setDiameter(gs.getFloat(s, true, Attributes.RADIUS) * 2);
			current.setEndOfShoot(shootEnd);
			current.setGenerativeDistance(gs.getInt(s, true, Attributes.GENERATIVE_DISTANCE));
			current.setHeartwoodDiameter(gs.getFloat(s, true, Attributes.HEARTWOOD));
			current.setId(s.getId());
			current.setInternodeCount(gs.getInt(s, true, Attributes.INTERNODE_COUNT));
			current.setLength(length);
			current.setMother(mother);
			current.setN(gs.getFloat(s, true, Attributes.PARAMETER));
			current.setOrder(gs.getInt(s, true, Attributes.ORDER));
			current.setQ((gs.getFloat(s, true, Attributes.REL_POSITION) == 0.0f ? 0.0f : 1 - gs.getFloat(s, true, Attributes.REL_POSITION)));
			current.setScale(gs.getInt(s, true, Attributes.LOCAL_SCALE));
			current.setSh(new Vector3d(matrix.m02, matrix.m12, matrix.m22));
			current.setSl(new Vector3d(matrix.m00, matrix.m10, matrix.m20));
			current.setSu(new Vector3d(matrix.m01, matrix.m11, matrix.m21));
			if(mother != null)
				mother.setSuccessor(current);
			current.setXka(0);
			current.setYka(0);
			current.setXke(0);
			current.setYke(0);
		}
		current.setSuccessor(null);
		
		Shoot helper;
		it = new TreeIterator(root);
		while((s = LSystem.nextShoot(it)) != null) {
			current = map.get(s.getId());
			helper = getForwardChaining(s);
			if(helper != null)
				current.setForwardChain(map.get(helper.getId()));
			else
				current.setForwardChain(null);
		}
		
		return ggRoot;
	}
	
}

/**
 * The equivalent to the shoot-structure of GROGRA.
 * Constraints: Not every attribute is used in GroIMP and the link to the local registers and to the shared objects are not include.
 * @author Jan
 *
 */
class ShootStructure {
	private long id;
	private ShootStructure mother;
	private float length;
	private float heartwoodDiameter;
	private float diameter;
	private float n;
	private int internodeCount;
	private int color;
	private int order;
	private int scale;
	private long generativeDistance;
	private float q;
	private Vector3d beginOfShoot;
	private Vector3d endOfShoot;
	private Vector3d sh, sl, su;
	private ShootStructure successor;
	private ShootStructure forwardChain;
	private float xka, yka;
	private float xke, yke;
	
	public Vector3d getBeginOfShoot() {
		return beginOfShoot;
	}
	public void setBeginOfShoot(Vector3d beginOfShoot) {
		this.beginOfShoot = beginOfShoot;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public float getDiameter() {
		return diameter;
	}
	public void setDiameter(float diameter) {
		this.diameter = diameter;
	}
	public Vector3d getEndOfShoot() {
		return endOfShoot;
	}
	public void setEndOfShoot(Vector3d endOfShoot) {
		this.endOfShoot = endOfShoot;
	}
	public ShootStructure getForwardChain() {
		return forwardChain;
	}
	public void setForwardChain(ShootStructure forwardChain) {
		this.forwardChain = forwardChain;
	}
	public long getGenerativeDistance() {
		return generativeDistance;
	}
	public void setGenerativeDistance(long generativeDistance) {
		this.generativeDistance = generativeDistance;
	}
	public float getHeartwoodDiameter() {
		return heartwoodDiameter;
	}
	public void setHeartwoodDiameter(float heartwoodDiameter) {
		this.heartwoodDiameter = heartwoodDiameter;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getInternodeCount() {
		return internodeCount;
	}
	public void setInternodeCount(int internodeCount) {
		this.internodeCount = internodeCount;
	}
	public float getLength() {
		return length;
	}
	public void setLength(float length) {
		this.length = length;
	}
	public ShootStructure getMother() {
		return mother;
	}
	public void setMother(ShootStructure mother) {
		this.mother = mother;
	}
	public float getN() {
		return n;
	}
	public void setN(float n) {
		this.n = n;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public float getQ() {
		return q;
	}
	public void setQ(float q) {
		this.q = q;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public Vector3d getSh() {
		return sh;
	}
	public void setSh(Vector3d sh) {
		this.sh = sh;
	}
	public Vector3d getSl() {
		return sl;
	}
	public void setSl(Vector3d sl) {
		this.sl = sl;
	}
	public Vector3d getSu() {
		return su;
	}
	public void setSu(Vector3d su) {
		this.su = su;
	}
	public ShootStructure getSuccessor() {
		return successor;
	}
	public void setSuccessor(ShootStructure successor) {
		this.successor = successor;
	}
	public float getXka() {
		return xka;
	}
	public void setXka(float xka) {
		this.xka = xka;
	}
	public float getXke() {
		return xke;
	}
	public void setXke(float xke) {
		this.xke = xke;
	}
	public float getYka() {
		return yka;
	}
	public void setYka(float yka) {
		this.yka = yka;
	}
	public float getYke() {
		return yke;
	}
	public void setYke(float yke) {
		this.yke = yke;
	}
	
}

/**
 * Store the elemental tree information for the elementary analysis of several trees
 * @author Jan Derer
 *
 */
class TreeInformation {
	
	private int color;		// Color is also the ID for a tree (one tree -> one color)
	private float zval;		// Max z value
	private float diameter;	// Max diameter
	private float bhd;		// BHD
	private float lsum;		// Length of all shoots
	private float nsum;		// Sum of all n values
	private long isum;		// Number of internodes
	private float volsum;	// Total volume
	private float sursum;	// Total surface
	
	// Getters & Setters
	public float getBhd() {
		return bhd;
	}
	public void setBhd(float bhd) {
		this.bhd = bhd;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public float getDiameter() {
		return diameter;
	}
	public void setDiameter(float diameter) {
		this.diameter = diameter;
	}
	public long getIsum() {
		return isum;
	}
	public void setIsum(long isum) {
		this.isum = isum;
	}
	public float getLsum() {
		return lsum;
	}
	public void setLsum(float lsum) {
		this.lsum = lsum;
	}
	public float getNsum() {
		return nsum;
	}
	public void setNsum(float nsum) {
		this.nsum = nsum;
	}
	public float getSursum() {
		return sursum;
	}
	public void setSursum(float sursum) {
		this.sursum = sursum;
	}
	public float getVolsum() {
		return volsum;
	}
	public void setVolsum(float volsum) {
		this.volsum = volsum;
	}
	public float getZval() {
		return zval;
	}
	public void setZval(float zval) {
		this.zval = zval;
	}
	
}