
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

package de.grogra.reflect;

import java.util.List;

import de.grogra.xl.util.ObjectList;

public final class XField extends FieldBase
{
	int index;
	Object constantValue;
	final XClass xcls;


	XField (XClass<?> declaring, String name, int modifiers, Type type)
	{
		super (name, modifiers, declaring, type);
		this.xcls = declaring;
	}


	public void setConstant (Object value)
	{
		modifiers |= CONSTANT;
		constantValue = value;
	}

	public List<Annotation> getDeclaredAnnotations ()
	{
		if (annots == null)
		{
			annots = new ObjectList<Annotation> ();
		}
		return annots;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	public void set$pp.Type (Object object, $type value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.${pp.prefix}vals[index] = value $pp.type2vm;
		}
	}

	@Override
	public $type get$pp.Type (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return $pp.unwrap("constantValue");
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return ($type) (i.${pp.prefix}vals[index] $pp.vm2type);
		}
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	public void setBoolean (Object object, boolean value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.ivals[index] = value  ? 1 : 0;
		}
	}
// generated
	@Override
	public boolean getBoolean (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Boolean) (constantValue)).booleanValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (boolean) (i.ivals[index]  != 0);
		}
	}
// generated
// generated
	@Override
	public void setByte (Object object, byte value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.ivals[index] = value ;
		}
	}
// generated
	@Override
	public byte getByte (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Number) (constantValue)).byteValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (byte) (i.ivals[index] );
		}
	}
// generated
// generated
	@Override
	public void setShort (Object object, short value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.ivals[index] = value ;
		}
	}
// generated
	@Override
	public short getShort (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Number) (constantValue)).shortValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (short) (i.ivals[index] );
		}
	}
// generated
// generated
	@Override
	public void setChar (Object object, char value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.ivals[index] = value ;
		}
	}
// generated
	@Override
	public char getChar (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Character) (constantValue)).charValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (char) (i.ivals[index] );
		}
	}
// generated
// generated
	@Override
	public void setInt (Object object, int value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.ivals[index] = value ;
		}
	}
// generated
	@Override
	public int getInt (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Number) (constantValue)).intValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (int) (i.ivals[index] );
		}
	}
// generated
// generated
	@Override
	public void setLong (Object object, long value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.lvals[index] = value ;
		}
	}
// generated
	@Override
	public long getLong (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Number) (constantValue)).longValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (long) (i.lvals[index] );
		}
	}
// generated
// generated
	@Override
	public void setFloat (Object object, float value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.fvals[index] = value ;
		}
	}
// generated
	@Override
	public float getFloat (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Number) (constantValue)).floatValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (float) (i.fvals[index] );
		}
	}
// generated
// generated
	@Override
	public void setDouble (Object object, double value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.dvals[index] = value ;
		}
	}
// generated
	@Override
	public double getDouble (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (((Number) (constantValue)).doubleValue ());
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (double) (i.dvals[index] );
		}
	}
// generated
// generated
	@Override
	public void setObject (Object object, Object value)
	{
		assert (modifiers & CONSTANT) == 0 : toString ();
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			i.avals[index] = value ;
		}
	}
// generated
	@Override
	public Object getObject (Object object)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return (constantValue);
		}
		if ((modifiers & STATIC) != 0)
		{
			xcls.initialize ();
			object = xcls;
		}
		XData i;
		synchronized (i = ((XObject) object).getXData ())
		{
			return (Object) (i.avals[index] );
		}
	}
//!! *# End of generated code
}
