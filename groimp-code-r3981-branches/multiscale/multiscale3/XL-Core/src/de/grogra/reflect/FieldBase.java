
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

public abstract class FieldBase extends MemberBase implements Field
{
	protected final Type type;


	public FieldBase (String name, int modifiers, Type declaringType,
					  Type type)
	{
		super (name, null, modifiers, declaringType);
		this.type = type;
	}


	@Override
	public String getDescriptor ()
	{
		if (descriptor == null)
		{
			descriptor = Reflection.getFieldDescriptor (getSimpleName (), getType ());
		}
		return descriptor;
	}


	public final Type getType ()
	{
		return type;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public $type get$pp.Type (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}


	public void set$pp.Type (Object object, $type value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public boolean getBoolean (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setBoolean (Object object, boolean value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public byte getByte (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setByte (Object object, byte value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public short getShort (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setShort (Object object, short value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public char getChar (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setChar (Object object, char value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public int getInt (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setInt (Object object, int value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public long getLong (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setLong (Object object, long value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public float getFloat (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setFloat (Object object, float value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public double getDouble (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setDouble (Object object, double value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
// generated
	public Object getObject (Object object) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
// generated
	public void setObject (Object object, Object value) throws IllegalAccessException
	{
		throw new AssertionError (getClass ());
	}
// generated
//!! *# End of generated code
	
	@Override
	public String toString ()
	{
		return Reflection.modifiersToString (getModifiers ()) + ' '
			+ type.getSimpleName ()  + ' ' + declaringType.getSimpleName ()
			+ '.' + name;
	}

}
