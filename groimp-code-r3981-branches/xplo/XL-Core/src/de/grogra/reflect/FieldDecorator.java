
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

public class FieldDecorator extends MemberDecorator implements Field
{
	public static Field undecorate (Field f)
	{
		while (f instanceof FieldDecorator)
		{
			f = (Field) ((FieldDecorator) f).getDecoratedMember ();
		}
		return f;
	}


	private final Type type;

	
	public FieldDecorator (Field field, Type type)
	{
		super (field);
		this.type = type;
	}

	
	public FieldDecorator (Field field)
	{
		this (field, null);
	}

	
	public Type getType ()
	{
		return (type != null) ? type : ((Field) getDecoratedMember ()).getType ();
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public $type get$pp.Type (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).get$pp.Type (object);
	}


	public void set$pp.Type (Object object, $type value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).set$pp.Type (object, value);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public boolean getBoolean (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getBoolean (object);
	}
// generated
// generated
	public void setBoolean (Object object, boolean value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setBoolean (object, value);
	}
// generated
// generated
// generated
	public byte getByte (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getByte (object);
	}
// generated
// generated
	public void setByte (Object object, byte value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setByte (object, value);
	}
// generated
// generated
// generated
	public short getShort (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getShort (object);
	}
// generated
// generated
	public void setShort (Object object, short value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setShort (object, value);
	}
// generated
// generated
// generated
	public char getChar (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getChar (object);
	}
// generated
// generated
	public void setChar (Object object, char value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setChar (object, value);
	}
// generated
// generated
// generated
	public int getInt (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getInt (object);
	}
// generated
// generated
	public void setInt (Object object, int value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setInt (object, value);
	}
// generated
// generated
// generated
	public long getLong (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getLong (object);
	}
// generated
// generated
	public void setLong (Object object, long value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setLong (object, value);
	}
// generated
// generated
// generated
	public float getFloat (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getFloat (object);
	}
// generated
// generated
	public void setFloat (Object object, float value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setFloat (object, value);
	}
// generated
// generated
// generated
	public double getDouble (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getDouble (object);
	}
// generated
// generated
	public void setDouble (Object object, double value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setDouble (object, value);
	}
// generated
// generated
// generated
	public Object getObject (Object object) throws IllegalAccessException
	{
		return ((Field) getDecoratedMember ()).getObject (object);
	}
// generated
// generated
	public void setObject (Object object, Object value) throws IllegalAccessException
	{
		((Field) getDecoratedMember ()).setObject (object, value);
	}
// generated
//!! *# End of generated code

}
