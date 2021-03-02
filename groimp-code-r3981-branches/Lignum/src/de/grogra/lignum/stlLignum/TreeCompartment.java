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

import javax.vecmath.Point3d;

import de.grogra.graph.impl.Node;
import de.grogra.lignum.jadt.PositionVector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */
public class TreeCompartment extends Node {
	
	private static final long serialVersionUID = 8435494224531324565L;
	
	protected Point3d point = new Point3d();
	//enh:field getter setter
	
	// TODO replace PositionVector by Vector3d?!
	protected PositionVector direction = new PositionVector(0,0,1);
	//enh:field getter setter
	
	/**
	 * Age of an object
	 */
	protected double LGAage = 0;
	//enh:field getter setter
	
	protected Tree tree = null;			//in cLignum Pointer !
	
	/**
	 * Sapwood area
	 */
	// parameter Asu of TcData in LIGNUM
	protected double LGAAs = 0;
	//enh:field getter setter
	
	
	public TreeCompartment() {
	}
	
	public TreeCompartment(Tree tree) {
//		point = Library.location(this);
//		direction = Library.direction(this);
//		setLGAage(0.0);	
		this.tree = tree;
	}

	// old lignum version
//	public TreeCompartment(PointL p, PositionVector d, Tree t) {
//		point = p;
//		direction = d;
//		setLGAage(0.0);
//		direction.normalize();
//		tree = t;
//	}
	
	
	// TODO add parameters and methods corresponding to LIGNUM's DiameterGrowthData


//	protected Tree getTree(TreeCompartment tc) {
//		return tc.tree;
//	}
	
	public Tree getTree() {
		return tree;
	}
		
	// TODO make the following methods abstract? they are virtual in LIGNUM
//	void photosynthesis();
//	void respiration();
//	void aging();
//	void diameterGrowth();
	
	
//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field point$FIELD;
	public static final NType.Field direction$FIELD;
	public static final NType.Field LGAage$FIELD;
	public static final NType.Field LGAAs$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (TreeCompartment.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 2:
					((TreeCompartment) o).LGAage = (double) value;
					return;
				case 3:
					((TreeCompartment) o).LGAAs = (double) value;
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 2:
					return ((TreeCompartment) o).getLGAage ();
				case 3:
					return ((TreeCompartment) o).getLGAAs ();
			}
			return super.getDouble (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((TreeCompartment) o).point = (Point3d) value;
					return;
				case 1:
					((TreeCompartment) o).direction = (PositionVector) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((TreeCompartment) o).getPoint ();
				case 1:
					return ((TreeCompartment) o).getDirection ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new TreeCompartment ());
		$TYPE.addManagedField (point$FIELD = new _Field ("point", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Point3d.class), null, 0));
		$TYPE.addManagedField (direction$FIELD = new _Field ("direction", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (PositionVector.class), null, 1));
		$TYPE.addManagedField (LGAage$FIELD = new _Field ("LGAage", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 2));
		$TYPE.addManagedField (LGAAs$FIELD = new _Field ("LGAAs", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 3));
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
		return new TreeCompartment ();
	}

	public double getLGAage ()
	{
		return LGAage;
	}

	public void setLGAage (double value)
	{
		this.LGAage = (double) value;
	}

	public double getLGAAs ()
	{
		return LGAAs;
	}

	public void setLGAAs (double value)
	{
		this.LGAAs = (double) value;
	}

	public Point3d getPoint ()
	{
		return point;
	}

	public void setPoint (Point3d value)
	{
		point$FIELD.setObject (this, value);
	}

	public PositionVector getDirection ()
	{
		return direction;
	}

	public void setDirection (PositionVector value)
	{
		direction$FIELD.setObject (this, value);
	}

//enh:end
}

//public class TreeCompartment extends Node {
//
//	private static final long serialVersionUID = 8435494224531324565L;
//	
//	public double tc_age;
//	public Tree tree;			//in cLignum Pointer !
//	protected PointL point = new PointL();
//	protected PositionVector direction = new PositionVector();
//	
//	public PositionVector getDirection() {
//		return direction;
//	}
//
//	public void setDirection(PositionVector direction) {
//		this.direction.set(direction);
//	}
//
//	public PointL getPoint() {
//		return point;
//	}
//
//	public void setPoint(PointL point) {
//		this.point = point;
//	}
//
//	protected Tree GetTree(TreeCompartment tc){
//		return tc.tree;
//	}
//	
//	public Tree GetTree(){
//		return this.tree;
//	}
//	
//}
