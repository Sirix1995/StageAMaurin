
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
import java.util.NoSuchElementException;
import de.grogra.persistence.*;

public class Rhombus extends RectangularShape
{
	//enh:sco SCOType

	float centerX = 0;
	float centerY = 0;
		
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

		public Type (Rhombus representative, de.grogra.persistence.SCOType supertype)
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
					((Rhombus) o).width = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((Rhombus) o).height = (float) value;
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
					return ((Rhombus) o).width;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Rhombus) o).height;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Rhombus ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Rhombus.class);
		width$FIELD = Type._addManagedField ($TYPE, "width", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		height$FIELD = Type._addManagedField ($TYPE, "height", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end


	@Override
	public double getX ()
	{
		return centerX - 0.5f * width;
	}


	@Override
	public double getY ()
	{
		return centerY - 0.5f * height;
	}


	@Override
	public double getWidth ()
	{
		return width;
	}


	@Override
	public double getHeight ()
	{
		return height;
	}


	@Override
	public boolean isEmpty ()
	{
	    return (width <= 0.0f) || (height <= 0.0f);
	}


	@Override
	public void setFrame (double x, double y, double w, double h)
	{
		this.width = (float) w;
		this.height = (float) h;
		this.centerX = (float) (x + 0.5 * w);
		this.centerY = (float) (y + 0.5 * h);
	}


	public Rectangle2D getBounds2D ()
	{
		return new Rectangle2D.Float (centerX - 0.5f * width,
									  centerY - 0.5f * height, width, height);
	}

	
	public boolean contains (double x, double y) 
	{
		return Math.abs (x - centerX) * height + Math.abs (y - centerY) * width
			<= 0.5f * width * height;
	}


	public void setDeltaHeight (float h)
	{
		this.centerY =  h;
	}


	public boolean intersects (double x, double y, double w, double h)
	{
		return false;
	}


	public boolean contains (double x, double y, double w, double h)
	{
		return contains (x, y) && contains (x + w, y)
			&& contains (x, y + h) && contains (x + w, y + h);
	}


	@Override
	public PathIterator getPathIterator (AffineTransform at, double flatness)
	{
		return new Iterator (this, at);
	}


	public PathIterator getPathIterator (AffineTransform at)
	{
		return new Iterator (this, at);
	}

	
	private static class Iterator implements PathIterator
	{
	    float cx, cy, w2, h2;
	    AffineTransform affine;
	    int index;

	    Iterator (Rhombus r, AffineTransform at)
	    {
	    	cx = r.centerX;
	    	cy = r.centerY;
	    	w2 = 0.5f * r.width;
	    	h2 = 0.5f * r.height;
	    	affine = at;
	    	index = r.isEmpty () ? 6 : 0;
	    }


	    public int getWindingRule ()
	    {
	    	return WIND_NON_ZERO;
	    }


	    public boolean isDone ()
	    {
	    	return index > 5;
	    }


	    public void next ()
	    {
	    	index++;
	    }


	    public int currentSegment (float[] coords)
	    {
	    	switch (index)
	    	{
	    		case 0:
	    			coords[0] = cx + w2;
	    			coords[1] = cy;
	    			if (affine != null)
	    			{
	    			    affine.transform (coords, 0, coords, 0, 1);
	    			}
	    			return SEG_MOVETO;
	    		case 1:
	    			coords[0] = cx;
	    			coords[1] = cy + h2;
	    			break;
	    		case 2:
	    			coords[0] = cx - w2;
	    			coords[1] = cy;
	    			break;
	    		case 3:
	    			coords[0] = cx;
	    			coords[1] = cy - h2;
	    			break;
	    		case 4:
	    			coords[0] = cx + w2;
	    			coords[1] = cy;
	    			break;
	    		case 5:
	    			return SEG_CLOSE;
	    		default:
		    		throw new NoSuchElementException ("iterator out of bounds");
	    	}
	    	if (affine != null)
	    	{
	    	    affine.transform (coords, 0, coords, 0, 1);
	    	}
			return SEG_LINETO;
	    }


	    public int currentSegment (double[] coords)
	    {
	    	switch (index)
	    	{
	    		case 0:
	    			coords[0] = cx + w2;
	    			coords[1] = cy;
	    			if (affine != null)
	    			{
	    			    affine.transform (coords, 0, coords, 0, 1);
	    			}
	    			return SEG_MOVETO;
	    		case 1:
	    			coords[0] = cx;
	    			coords[1] = cy + h2;
	    			break;
	    		case 2:
	    			coords[0] = cx - w2;
	    			coords[1] = cy;
	    			break;
	    		case 3:
	    			coords[0] = cx;
	    			coords[1] = cy - h2;
	    			break;
	    		case 4:
	    			coords[0] = cx + w2;
	    			coords[1] = cy;
	    			break;
	    		case 5:
	    			return SEG_CLOSE;
	    		default:
		    		throw new NoSuchElementException ("iterator out of bounds");
	    	}
	    	if (affine != null)
	    	{
	    	    affine.transform (coords, 0, coords, 0, 1);
	    	}
			return SEG_LINETO;
	    }
	}

}
