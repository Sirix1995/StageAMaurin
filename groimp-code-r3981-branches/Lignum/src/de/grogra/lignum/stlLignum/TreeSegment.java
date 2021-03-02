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

import static java.lang.Math.PI;
import static java.lang.Math.pow;

import java.util.Vector;

import javax.vecmath.Point3d;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */
public class TreeSegment extends TreeCompartment {

	/**
	 * Original sapwood area
	 */
	protected double LGAAs0 = 0;
	//enh:field getter setter
	
	/**
	 * Length of the tree segment (m)
	 */
	protected double LGAL = 0;
	//enh:field getter setter
	
	/**
	 * Rate of respiration (= amount of r. of the tree segment during the time step)
	 * (kgC dry weight)
	 */
	protected double LGAM = 0;
	//enh:field getter setter
	
	/**
	 * Gravelius order
	 */
	protected double LGAomega = 0;
	//enh:field getter setter
	
	/**
	 * Radius of segment including bark (m)
	 */
	protected double LGAR = 0;
	//enh:field getter setter
	
	/**
	 * Radius of segment at top (m)
	 */
	protected double LGARTop = 0;
	//enh:field getter setter
	
	/**
	 * Heartwood radius (m)
	 */
	protected double LGARh = 0;
	//enh:field getter setter
	
	/**
	 * Sapwood mass of the tree segment (kgC dry weight)
	 */
	protected double LGAWs = 0;
	//enh:field setter
	
	/**
	 * Heartwood mass of the segment (kgC dry weight)
	 */
	protected double LGAWh = 0;
	//enh:field setter
	
	/**
	 * Annual rings of the tree segment (m)
	 */
	protected Vector<Double> annual_rings;
	
	/**
	 * Vigour index
	 */
	protected double LGAvi = 0;
	//enh:field getter setter
	
	/**
	 * General type specifier, e.g. dominant, nondominant
	 */
	protected double LGAtype = 0;
	//enh:field getter setter
	
	
	public TreeSegment() {
		super();
	}
	
	public TreeSegment(Tree tree, double LGAomega, double LGAL) {
		super(tree);
		this.LGAL = LGAL;
		setLGAR(tree.getLGPlr() * LGAL);
		
		this.LGAomega = LGAomega;
		// the first annual ring
//		annual_rings.addElement(LGAR);
	}
	
	public Point3d getMidPoint() {
		Point3d point = new Point3d(getPoint());
		Point3d direction = new Point3d(getDirection());
		direction.scale(0.5 * getLGAL());
		point.add(direction);
		return point;
	}	
	
	public Point3d getEndPoint() {
		Point3d point = new Point3d(getPoint());
		Point3d direction = new Point3d(getDirection());
		direction.scale(getLGAL());
		point.add(direction);
		return point;
	}
	
	public double getLGAA() {
		return PI * pow(getLGAR(), 2.0);
	}
	
	public double getLGAAh() {
		return PI * pow(getLGARh(), 2.0);
	}
	
	@Override
	public double getLGAAs() {
		return getLGAA() - getLGAAh();
	}
	
	@Override
	public double getLGAage() {
		return LGAage;
	}
	
	public double getLGAH() {
		return point.z;
	}
	
	double getLGAHTop() {
		return point.z + direction.z * getLGAL();
	}
	
	public double getLGAV() {
		// Now calculates as frustum volume if Rtop present
		double rb = getLGAR();
		double rt = getLGARTop();
		
		// LGARTop is not set (default value present)
		if (rt == 0.0)
			return  PI * rb * rb * getLGAL();
		else
			return (PI * getLGAL() * (rb * rb + rb * rt + rt * rt)) / 3.0;
	}
	
	public double getLGAVfrustum() {
		double rb = getLGAR();
		double rt = getLGARTop();
		
		// LGARTop is not set (default value present), return segment cylinder volume
		if (rt == 0.0)
			return getLGAV();
		// LGARTop set, return the frustum volume
		else
			return (PI * getLGAL() * (rb * rb + rb * rt + rt * rt)) / 3.0;
	}
	
	public double getLGAVh() {	
		// Calculated assuming same taper as the whole segment
		// if Rtop present
		double rt = getLGARTop();
		
		if (rt == 0)
		  return  PI * pow(getLGARh(), 2.0) * getLGAL();
		else {
		  double rh = getLGARh();
		  double rh_t = (rh / getLGAR()) * rt;
		  return (PI * getLGAL() * (rh * rh + rh * rh_t + rh_t * rh_t)) / 3.0; 
		}
	}
	
	public double getLGAVs() {
		return getLGAV() - getLGAVh();
	}
	
	public double getLGAWs() {
		// mass is density * volume
		return tree.getLGPrhoW() * getLGAVs();
	}
	
	public double getLGAWh() {
		return tree.getLGPrhoW() * getLGAVh();
	}
	
	public double getLGAWood() {
		return tree.getLGPrhoW() * getLGAVfrustum();
	}
	
	@Override
	public void setLGAage(double value) {
		LGAage = value;
	}
	
	public void setLGAdR(double value) {
		int size = annual_rings.size();
		
	    if (size > 1) {
			double rad = annual_rings.get(size-2);
			annual_rings.set(size-1, value + rad);
	    }
	}
	
	
//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field LGAAs0$FIELD;
	public static final NType.Field LGAL$FIELD;
	public static final NType.Field LGAM$FIELD;
	public static final NType.Field LGAomega$FIELD;
	public static final NType.Field LGAR$FIELD;
	public static final NType.Field LGARTop$FIELD;
	public static final NType.Field LGARh$FIELD;
	public static final NType.Field LGAWs$FIELD;
	public static final NType.Field LGAWh$FIELD;
	public static final NType.Field LGAvi$FIELD;
	public static final NType.Field LGAtype$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (TreeSegment.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 0:
					((TreeSegment) o).LGAAs0 = value;
					return;
				case 1:
					((TreeSegment) o).LGAL = value;
					return;
				case 2:
					((TreeSegment) o).LGAM = value;
					return;
				case 3:
					((TreeSegment) o).LGAomega = value;
					return;
				case 4:
					((TreeSegment) o).LGAR = value;
					return;
				case 5:
					((TreeSegment) o).LGARTop = value;
					return;
				case 6:
					((TreeSegment) o).LGARh = value;
					return;
				case 7:
					((TreeSegment) o).LGAWs = value;
					return;
				case 8:
					((TreeSegment) o).LGAWh = value;
					return;
				case 9:
					((TreeSegment) o).LGAvi = value;
					return;
				case 10:
					((TreeSegment) o).LGAtype = value;
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
					return ((TreeSegment) o).getLGAAs0 ();
				case 1:
					return ((TreeSegment) o).getLGAL ();
				case 2:
					return ((TreeSegment) o).getLGAM ();
				case 3:
					return ((TreeSegment) o).getLGAomega ();
				case 4:
					return ((TreeSegment) o).getLGAR ();
				case 5:
					return ((TreeSegment) o).getLGARTop ();
				case 6:
					return ((TreeSegment) o).getLGARh ();
				case 7:
					return ((TreeSegment) o).LGAWs;
				case 8:
					return ((TreeSegment) o).LGAWh;
				case 9:
					return ((TreeSegment) o).getLGAvi ();
				case 10:
					return ((TreeSegment) o).getLGAtype ();
			}
			return super.getDouble (o);
		}
	}

	static
	{
		$TYPE = new NType (new TreeSegment ());
		$TYPE.addManagedField (LGAAs0$FIELD = new _Field ("LGAAs0", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 0));
		$TYPE.addManagedField (LGAL$FIELD = new _Field ("LGAL", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 1));
		$TYPE.addManagedField (LGAM$FIELD = new _Field ("LGAM", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 2));
		$TYPE.addManagedField (LGAomega$FIELD = new _Field ("LGAomega", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 3));
		$TYPE.addManagedField (LGAR$FIELD = new _Field ("LGAR", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 4));
		$TYPE.addManagedField (LGARTop$FIELD = new _Field ("LGARTop", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 5));
		$TYPE.addManagedField (LGARh$FIELD = new _Field ("LGARh", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 6));
		$TYPE.addManagedField (LGAWs$FIELD = new _Field ("LGAWs", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 7));
		$TYPE.addManagedField (LGAWh$FIELD = new _Field ("LGAWh", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 8));
		$TYPE.addManagedField (LGAvi$FIELD = new _Field ("LGAvi", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 9));
		$TYPE.addManagedField (LGAtype$FIELD = new _Field ("LGAtype", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 10));
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
		return new TreeSegment ();
	}

	public double getLGAAs0 ()
	{
		return LGAAs0;
	}

	public void setLGAAs0 (double value)
	{
		this.LGAAs0 = value;
	}

	public double getLGAL ()
	{
		return LGAL;
	}

	public void setLGAL (double value)
	{
		this.LGAL = value;
	}

	public double getLGAM ()
	{
		return LGAM;
	}

	public void setLGAM (double value)
	{
		this.LGAM = value;
	}

	public double getLGAomega ()
	{
		return LGAomega;
	}

	public void setLGAomega (double value)
	{
		this.LGAomega = value;
	}

	public double getLGAR ()
	{
		return LGAR;
	}

	public void setLGAR (double value)
	{
		this.LGAR = value;
	}

	public double getLGARTop ()
	{
		return LGARTop;
	}

	public void setLGARTop (double value)
	{
		this.LGARTop = value;
	}

	public double getLGARh ()
	{
		return LGARh;
	}

	public void setLGARh (double value)
	{
		this.LGARh = value;
	}

	public void setLGAWs (double value)
	{
		this.LGAWs = value;
	}

	public void setLGAWh (double value)
	{
		this.LGAWh = value;
	}

	public double getLGAvi ()
	{
		return LGAvi;
	}

	public void setLGAvi (double value)
	{
		this.LGAvi = value;
	}

	public double getLGAtype ()
	{
		return LGAtype;
	}

	public void setLGAtype (double value)
	{
		this.LGAtype = value;
	}

//enh:end
}



//public class TreeSegment extends TreeCompartment {
//
//	private TreeSegmentAttributes tsa = new TreeSegmentAttributes();
//	
//	public PointL getMidPoint(TreeSegment ts) {
//		
//		PointL point = new Point3d(ts.getPoint());
//		PointL direction = new PointL(ts.getDirection());
//		direction.mul(0.5*ts.GetValue(ts, LGMAD.LGAL));
//		point.add(direction);
//		return point;
//	}
//	
//	public double GetValue(TreeSegment ts, LGMAD name){
//		double rb,rt;
//		switch(name){
//		case LGAA:
//			return PI*pow(this.GetValue(ts,LGMAD.LGAR), 2.0);
//		case LGAAh:
//			return PI*pow(this.GetValue(ts,LGMAD.LGARh), 2.0);
//		case LGAAs:
//			return this.GetValue(ts,LGMAD.LGAA) - this.GetValue(ts,LGMAD.LGAAh);
//		case LGAAs0:
//			return ts.tsa.As0;
//		case LGAage:
//			return ts.tc_age; 
//		case LGAH:
//			 return ts.point.z;
//		case LGAHTop:
//			return ts.point.z + ts.direction.z * this.GetValue(ts,LGMAD.LGAL);
//		case LGAL:
//			return ts.tsa.L;
//		case LGAM:
//			return ts.tsa.M;
//		case LGAomega:
//			return ts.tsa.omega;
//		case LGAR:
//			return ts.tsa.R;
//		case LGARh:
//			 return ts.tsa.Rh;
//		case LGARTop:
//			return ts.tsa.Rtop;
//		case LGAtype:
//			return ts.tsa.type;
//		case LGAvi:
//			return ts.tsa.vigour;
//		case LGAV:	 //Now claculates as frustum volume if Rtop present
//			rb = this.GetValue(ts,LGMAD.LGAR);
//		    rt = this.GetValue(ts,LGMAD.LGARTop);
//		    if (rt == 0.0)//LGARTop is not set (default value present)
//		        return  PI*rb*rb *this.GetValue(ts,LGMAD.LGAL);
//		    else
//		        return (PI*this.GetValue(ts,LGMAD.LGAL)*(rb*rb+rb*rt+rt*rt))/3.0;    
//		case LGAVfrustum:
//			rb = this.GetValue(ts,LGMAD.LGAR);
//			rt = this.GetValue(ts,LGMAD.LGARTop);
//			if (rt == 0.0)//LGARTop is not set (default value present)
//			      return this.GetValue(ts,LGMAD.LGAV);//Return segment cylinder volume
//			else
//			      //LGARTop set, return the frustum volume
//			      return (PI*this.GetValue(ts,LGMAD.LGAL)*(rb*rb+rb*rt+rt*rt))/3.0;
//		case LGAVh:					//Calculated assuming same taper as the whole segment
//			rt = this.GetValue(ts,LGMAD.LGARTop); //if Rtop present 
//		    if(rt == 0)
//		      return  PI*pow(this.GetValue(ts,LGMAD.LGARh),2.0) * this.GetValue(ts,LGMAD.LGAL);
//		    else {
//		      double rh = this.GetValue(ts,LGMAD.LGARh);
//		      double rh_t = (rh/this.GetValue(ts,LGMAD.LGAR))*rt;
//		      return (PI*this.GetValue(ts,LGMAD.LGAL)*(rh*rh+rh*rh_t+rh_t*rh_t))/3.0; 
//		    }
//		case LGAVs:
//			return this.GetValue(ts,LGMAD.LGAV) - this.GetValue(ts,LGMAD.LGAVh);
//		case LGAWood:
//			double v1 = ts.GetValue(ts, LGMAD.LGAVfrustum);
//			return ts.getTree().getValue(ts.getTree(), LGMPD.LGPrhoW)*v1;
//		default: 
//			return 0;
//		/*case LGAvi:		//TODO: ADD later the rest
//			return ts.tsa.vigour;
//		case LGAvi:
//			return ts.tsa.vigour;
//		case LGAvi:
//			return ts.tsa.vigour;
//			
//		//Personally  I prefer minimum  set of  variables or  attributes and
//  //compute others  like Ws  or Wh (we  have to do  these computations
//  //anyway). These 3 lines should not be a computational cost.
//  else if (name == LGAWs){
//    LGMdouble v1 = GetValue(ts,LGAVs);   
//    //mass is density * volume
//    return GetValue(GetTree(ts),LGPrhoW) * v1;
//  }
//  else if (name == LGAWood) {
//    LGMdouble v1 = GetValue(ts, LGAVfrustum);
//    return GetValue(GetTree(ts), LGPrhoW) * v1;
//  }
//  else
//    return GetValue(dynamic_cast<const TreeCompartment<TS,BUD>&>(ts), name);
//			
//			
//		default:
//			return super.GetValue(ts, name);	*/
//		}
//		
//	}
//	
//	public double SetValue(TreeSegment ts, LGMAD name, double value){
//		
//		// double old_value = GetValue(ts,name);
//		
//		switch(name){
//		case LGAage:
//			ts.tc_age = value;
//			break;
//		case LGAAs0:
//			ts.tsa.As0 = value;
//			break;
//		case LGAL:
//			ts.tsa.L = value;
//			break;
//		case LGAdR:
//			int size = ts.tsa.annual_rings.size();
//		    if (size>1)
//		      {
//			double rad = ts.tsa.annual_rings.get(size-2);
//			ts.tsa.annual_rings.set(size-1, value+rad);
//		      }
//			break;
//		case LGAM:
//			ts.tsa.M = value;
//			break;
//		case LGAomega:
//			ts.tsa.omega = value;
//			break;
//		case LGAR:
//			ts.tsa.R = value;
//			break;
//		case LGARh:
//			ts.tsa.Rh = value;
//			break;
//		case LGARTop:
//			ts.tsa.Rtop = value;
//			break;
//		case LGAtype:
//			ts.tsa.type = value;
//			break;
//		case LGAvi:
//			ts.tsa.vigour = value;
//			break;
//		case LGAWs:
//			ts.tsa.Ws = value;
//			break;
//		case LGAWh:
//			ts.tsa.Wh = value;
//			break;
//	//	default:
//	//		old_value = SetValue(ts, name,value);
//			
//		
//		}
//	//	return old_value;
//		return 0;
//	}
//}
