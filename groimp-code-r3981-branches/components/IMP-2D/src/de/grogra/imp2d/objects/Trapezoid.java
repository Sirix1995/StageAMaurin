
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

package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;

public class Trapezoid extends RectangularShape
{
	
	
	
	//enh:sco SCOType

	float width = 0.5f;
	//enh:field
	
	float height = 0.5f;
	//enh:field
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field width$FIELD;
	public static final Type.Field height$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Trapezoid representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Trapezoid) o).width = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((Trapezoid) o).height = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Trapezoid) o).width;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Trapezoid) o).height;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Trapezoid ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Trapezoid.class);
		width$FIELD = Type._addManagedField ($TYPE, "width", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		height$FIELD = Type._addManagedField ($TYPE, "height", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end


	
	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return -width/2;
	}


	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return -height/2;
	}


	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return width;
	}


	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return height;
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setFrame(double x, double y, double w, double h) {
		this.width = (float) w;
		this.height = (float) h;
		// TODO Auto-generated method stub
		
	}


	public Rectangle2D getBounds2D() {
		
		return new Rectangle2D.Float(-width/2,-height/2,width,height);
	}

	
	
	public boolean contains(double x, double y) 
	{
	
		double x0 = this.getX();
		double y0 = this.getY();
		return (x >= x0 &&
			y >= y0 &&
			x < x0 + getWidth() &&
			y < y0 + getHeight());
	    
	}


	public boolean intersects(double x, double y, double w, double h) {
		
		
		return false;
	}


	public boolean contains(double x, double y, double w, double h) {
		
		
		return false;
	}

	public PathIterator getPathIterator(AffineTransform at) {
		//	1)	 ----   (2)
		//		/    \
		//     /      \
		//(4) ----------   (3)
		
		GeneralPath  path = new GeneralPath ();
		path.moveTo(-width/4,height/2);
		path.lineTo(width/4,height/2);
		path.lineTo(width/2,-height/2);
		path.lineTo(-width/2,-height/2);
		path.closePath();
		return path.getPathIterator(at);
		
	}
		
}
