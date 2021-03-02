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
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import de.grogra.lignum.jadt.Mathsym;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class CfTreeSegment extends TreeSegment implements Mathsym {
	
	/**
	 * Height (thickness) of cylindrical layer of foliage
	 * in a segment (m), e.g. Hf = Lneedle*sin(needle_angle) 
	 */
	protected double LGAHf = 0;
	//enh:field getter setter
	
	/**
	 * Photosynthetic rate (= amount of p. during time step) (kg C)
	 */
	protected double LGAP = 0;
	//enh:field getter setter
	
	/**
	 * Irradiance of incoming radiation (MJ/m2)
	 */
	protected double LGAQin = 0;
	//enh:field getter setter
	
	/**
	 * Amount of absorbed radiation (MJ) (during time step)
	 */
	protected double LGAQabs = 0;
	//enh:field getter setter
	
	/**
	 * Radius (m) of segment cylinder including also foliage (conifers)
	 */
	protected double LGARf = 0;
	//enh:field
	
	/**
	 * Specific  leaf area  (=leaf area / leaf weight) (m2/kgC)
	 */
	protected double LGAsf = 0;
	//enh:field getter setter
	
	/**
	 * Star mean for the coniferous segment
	 */
	protected double LGAstarm = 0;
	//enh:field getter setter
	
	/**
	 * Foliage mass of the tree segment (kgC dry weight)
	 */
	protected double LGAWf = 0;
	//enh:field getter setter
	
	/**
	 * Initial foliage mass of the tree segment (kgC dry weight)
	 */
	protected double LGAWf0 = 0;
	//enh:field getter setter
	
	
	public CfTreeSegment() {
		super();
	}

	public CfTreeSegment(Tree tree, double LGAomega, double LGAL) {
		super(tree, LGAomega, LGAL);
		// TODO use set, get to access the attributes or this?
	    setLGAsf(tree.getLGPsf());
	    setLGAHf(tree.getLGPnl() * sin(tree.getLGPna()));
	    setLGARf(getLGAR() + getLGAHf());
		setLGAWf(tree.getLGPaf() * getLGASa());
		setLGAAs0(getLGAAs());
		setLGAWf0(getLGAWf());
	}
	
	
	public double getLGAAf() {
		// given Wf use sf to compute foliage area: sf*Wf ((m2/kg)*kg) 
		// see also parameter af
		return getLGAsf() * getLGAWf();
	}
	
	
	public double getLGARf() {
		if (getLGAWf() > R_EPSILON)
			// who remembers to update radius to foliage limit after diameter growth
			// return segment cylinder radius + foliage height
			return getLGAR() + getLGAHf();
		else
			return getLGAR();
	}
	
	public double getLGASa() {
		// wrap out the segment cylinder
		return 2.0 * PI * getLGAR() * getLGAL();
	}
		
	// volume occupied by foliage
	public double getLGAVf() {
		if (getLGAWf() > R_EPSILON)
			return PI * pow(getLGARf(), 2.0) * getLGAL() - getLGAV();
		else
		  return 0.0;
	}
	
	public void setLGARf(double value) {
		LGARf = value;
		setLGAHf(max(LGARf - getLGAR(), 0.0));
	}
		
	// Calculate PHOTOSYNTHETIC rate of the segment as the function of absorbed radiation
	public void photosynthesis() {
		setLGAP(tree.getLGPpr() * getLGAQabs());
	}
	
	// RESPIRATION rate of the segment as the function of needle mass and sapwood mass
	public void respiration() {
		double resp = 0.0;
		// Rtot = Rfoliage + Rsapwood
		resp = tree.getLGPmf() * getLGAWf() + tree.getLGPms() * getLGAWs();
		setLGAM(resp);
	}
	
	public void aging() {
		// add age (see foliage senescence below)
		setLGAage(getLGAage() + 1.0);
		
		// sapwood senescence
		double dAs = tree.getLGPss() * getLGAAs();
		double Ah_new =  dAs + getLGAAh();
		double Rh_new = sqrt(Ah_new / PI);
		setLGARh(Rh_new);
		
		// foliage senescence
		// this implementation assumes declining function of age from 1 to 0.
		double Wf_new = tree.getLGMFM().eval(getLGAage()) * getLGAWf0();
		setLGAWf(Wf_new);
	}
	
	public void diameterGrowth() {
		// new segment (age == 0) is iteratively set. 
		if (getLGAage() > 0.0) {
			double Asu = getLGAAs();	// sapwood area from above
			double Ahown  = getLGAAh();	// own heartwood
			// sapwood requirement of remaining foliage, assume fm returns
			// proportion initial foliage present, declining function from 1 to 0.
			double Asr = tree.getLGMFM().eval(getLGAage()) * getLGAAs0();
			
			// possible new radius
			double Rnew = sqrt((Asu + Ahown + Asr) / PI);
			// compare Rnew to R, choose max
			Rnew = max(Rnew, getLGAR());
			// new wood radius
			setLGAR(Rnew);
		}
	}

//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field LGAHf$FIELD;
	public static final NType.Field LGAP$FIELD;
	public static final NType.Field LGAQin$FIELD;
	public static final NType.Field LGAQabs$FIELD;
	public static final NType.Field LGARf$FIELD;
	public static final NType.Field LGAsf$FIELD;
	public static final NType.Field LGAstarm$FIELD;
	public static final NType.Field LGAWf$FIELD;
	public static final NType.Field LGAWf0$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (CfTreeSegment.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 0:
					((CfTreeSegment) o).LGAHf = value;
					return;
				case 1:
					((CfTreeSegment) o).LGAP = value;
					return;
				case 2:
					((CfTreeSegment) o).LGAQin = value;
					return;
				case 3:
					((CfTreeSegment) o).LGAQabs = value;
					return;
				case 4:
					((CfTreeSegment) o).LGARf = value;
					return;
				case 5:
					((CfTreeSegment) o).LGAsf = value;
					return;
				case 6:
					((CfTreeSegment) o).LGAstarm = value;
					return;
				case 7:
					((CfTreeSegment) o).LGAWf = value;
					return;
				case 8:
					((CfTreeSegment) o).LGAWf0 = value;
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
					return ((CfTreeSegment) o).getLGAHf ();
				case 1:
					return ((CfTreeSegment) o).getLGAP ();
				case 2:
					return ((CfTreeSegment) o).getLGAQin ();
				case 3:
					return ((CfTreeSegment) o).getLGAQabs ();
				case 4:
					return ((CfTreeSegment) o).LGARf;
				case 5:
					return ((CfTreeSegment) o).getLGAsf ();
				case 6:
					return ((CfTreeSegment) o).getLGAstarm ();
				case 7:
					return ((CfTreeSegment) o).getLGAWf ();
				case 8:
					return ((CfTreeSegment) o).getLGAWf0 ();
			}
			return super.getDouble (o);
		}
	}

	static
	{
		$TYPE = new NType (new CfTreeSegment ());
		$TYPE.addManagedField (LGAHf$FIELD = new _Field ("LGAHf", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 0));
		$TYPE.addManagedField (LGAP$FIELD = new _Field ("LGAP", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 1));
		$TYPE.addManagedField (LGAQin$FIELD = new _Field ("LGAQin", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 2));
		$TYPE.addManagedField (LGAQabs$FIELD = new _Field ("LGAQabs", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 3));
		$TYPE.addManagedField (LGARf$FIELD = new _Field ("LGARf", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 4));
		$TYPE.addManagedField (LGAsf$FIELD = new _Field ("LGAsf", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 5));
		$TYPE.addManagedField (LGAstarm$FIELD = new _Field ("LGAstarm", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 6));
		$TYPE.addManagedField (LGAWf$FIELD = new _Field ("LGAWf", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 7));
		$TYPE.addManagedField (LGAWf0$FIELD = new _Field ("LGAWf0", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 8));
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
		return new CfTreeSegment ();
	}

	public double getLGAHf ()
	{
		return LGAHf;
	}

	public void setLGAHf (double value)
	{
		this.LGAHf = value;
	}

	public double getLGAP ()
	{
		return LGAP;
	}

	public void setLGAP (double value)
	{
		this.LGAP = value;
	}

	public double getLGAQin ()
	{
		return LGAQin;
	}

	public void setLGAQin (double value)
	{
		this.LGAQin = value;
	}

	public double getLGAQabs ()
	{
		return LGAQabs;
	}

	public void setLGAQabs (double value)
	{
		this.LGAQabs = value;
	}

	public double getLGAsf ()
	{
		return LGAsf;
	}

	public void setLGAsf (double value)
	{
		this.LGAsf = value;
	}

	public double getLGAstarm ()
	{
		return LGAstarm;
	}

	public void setLGAstarm (double value)
	{
		this.LGAstarm = value;
	}

	public double getLGAWf ()
	{
		return LGAWf;
	}

	public void setLGAWf (double value)
	{
		this.LGAWf = value;
	}

	public double getLGAWf0 ()
	{
		return LGAWf0;
	}

	public void setLGAWf0 (double value)
	{
		this.LGAWf0 = value;
	}

//enh:end

}

//public class CfTreeSegment extends TreeSegment implements Mathsym{
//	
//	public CfTreeSegmentAttributes cftsa = new CfTreeSegmentAttributes();
//	
//
//	
//	public double GetValue(CfTreeSegment ts, LGMAD name){
//		
//		switch(name){
//		case LGAAf:
//			//Given Wf use sf to compute foliage area: sf*Wf ((m2/kg)*kg) 
//		    //see also parameter af
//			return GetValue(ts,LGMAD.LGAsf)*GetValue(ts,LGMAD.LGAWf);
//		case LGAHf:
//			return ts.cftsa.Hf;
//		case LGAP:
//			return ts.cftsa.P;
//		case LGAQin:
//			return ts.cftsa.Qin;
//		case LGAQabs:
//			return ts.cftsa.Qabs;
//		case LGARf:
//			if (this.GetValue(ts,LGMAD.LGAWf) > R_EPSILON)
//			      return GetValue(ts,LGMAD.LGAR) + GetValue(ts,LGMAD.LGAHf);
//			    //Who remebers to update radius to foliage limit after diameter growth
//			    //Return segment cylinder radius + foliage height.
//			    else
//			      return this.GetValue(ts,LGMAD.LGAR);
//	/*	case LGASA:
//			 //Wrap out the segment cylinder
//		    return 2.0*PI_VALUE*GetValue(ts,LGAR)*GetValue(ts,LGAL);*/
//		case LGAsf:
//			return ts.cftsa.sf;
//		case LGAstarm:
//			return ts.cftsa.starm;
///*		case LGAVf:
//			if (GetValue(ts,LGAWf) > R_EPSILON)
//			      return PI_VALUE*pow(GetValue(ts,LGARf),2.0)*GetValue(ts,LGAL)
//				     -GetValue(ts,LGAV);
//			    else
//			      return 0.0; */
//		case LGAWf:
//			return ts.cftsa.Wf;
//		case LGAWf0:
//			return ts.cftsa.Wf0;
//		default:
//			return super.GetValue(ts, name);
//			
//			
//		}
//		
//		
//	}
//	
//	public double SetValue(CfTreeSegment ts, LGMAD name, double value){
//		
//	//	double old_value = GetValue(ts,name);
//		
//		switch(name){
//		case LGAHf:
//			ts.cftsa.Hf = value;
//			break;
//		case LGAP:
//			ts.cftsa.P = value;
//			break;
//		case LGAQin:
//			ts.cftsa.Qin = value;
//			break;
//		case LGAQabs:
//			ts.cftsa.Qabs = value;
//			break;
//	/*	case LGARf:
//			ts.cftsa.Rf = value;
//		    SetValue(ts,LGAHf,max(ts.cftsa.Rf-GetValue(ts,LGAR),0.0));
//			break;*/
//		case LGAsf:
//			ts.cftsa.sf = value;
//			break;
//		case LGAstarm:
//			ts.cftsa.starm = value;
//			break;
//		case LGAWf:
//			ts.cftsa.Wf = value;
//			break;
//		case LGAWf0:
//			 ts.cftsa.Wf0 = value;
//			break;
//	/*	default:
//			old_value = SetValue(dynamic_cast<TreeSegment<TS,BUD>&>(ts), name,value); */
//			
//		}
//	//	return old_value;
//		return 0;
//	}
//
//}
