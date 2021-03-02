
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

package de.grogra.xl.impl.dom;

import org.w3c.dom.Element;

import de.grogra.reflect.TypeLoader;
import de.grogra.xl.impl.property.RuntimeModel;
import de.grogra.xl.util.Variant;

public class PropertyRuntimeModel extends RuntimeModel
{

	public static class ElementProperty implements Property
	{
		final String name;

		ElementProperty (String name, int componentCount)
		{
			this.name = name;
		}

		public Class<?> getType ()
		{
			return Variant.class;
		}

/*!!
#foreach ($type in $primitives)
$pp.setType($type)
		public $type get$pp.Type (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		

		public void set$pp.Type (Object object, int[] indices, $type value)
		{
			throw new UnsupportedOperationException ();
		}
#end
!!*/
//!! #* Start of generated code
// generated
		public boolean getBoolean (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setBoolean (Object object, int[] indices, boolean value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public byte getByte (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setByte (Object object, int[] indices, byte value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public short getShort (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setShort (Object object, int[] indices, short value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public char getChar (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setChar (Object object, int[] indices, char value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public int getInt (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setInt (Object object, int[] indices, int value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public long getLong (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setLong (Object object, int[] indices, long value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public float getFloat (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setFloat (Object object, int[] indices, float value)
		{
			throw new UnsupportedOperationException ();
		}
// generated
		public double getDouble (Object object, int[] indices)
		{
			throw new UnsupportedOperationException ();
		}
		
// generated
		public void setDouble (Object object, int[] indices, double value)
		{
			throw new UnsupportedOperationException ();
		}
//!! *# End of generated code

		public Object getObject (Object object, int[] indices)
		{
			return new Variant (((Element) object).getAttribute (name));
		}

		public void setObject (Object object, int[] indices, Object value)
		{
			((Element) object).setAttribute (name, ((Variant) value).toString ());
		}
	}


	public Property propertyForName (String name, TypeLoader loader)
	{
		int t = name.indexOf (';');
		int i = name.indexOf(';', t + 1);
		String attr = name.substring (t + 1, i);
		return new ElementProperty (attr, 0);
	}

}
