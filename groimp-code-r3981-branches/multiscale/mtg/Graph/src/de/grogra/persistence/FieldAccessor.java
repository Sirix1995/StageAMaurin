
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

package de.grogra.persistence;

import de.grogra.reflect.*;

public class FieldAccessor
{
	protected final Field field;
	private final int typeId;


	public FieldAccessor (Field field)
	{
		this.field = field;
		typeId = field.getType ().getTypeId ();
	}


	public Type getType ()
	{
		return field.getType ();
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public $type get$pp.Type (Object object) throws IllegalAccessException
	{
#if ($pp.numeric_char)
		switch (typeId)
		{
#foreach ($t in $numeric_char)
$pp.setType($t)
			case TypeId.$pp.TYPE:
				return ($type) ((field instanceof PersistenceField)
								? ((PersistenceField) field).get$pp.Type
									(object, null)
								: field.get$pp.Type (object));
#end
$pp.setType($type)
		}
#end
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).get$pp.Type
				(object, null)
			: field.get$pp.Type (object);
	}


	public $type set$pp.Type (Object object, $type value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
#if ($pp.numeric_char)
			switch (typeId)
			{
#foreach ($t in $numeric_char)
$pp.setType($t)
				case TypeId.$pp.TYPE:
					f.set$pp.Type (object, null, ($t) value, t);
					return ($type) value;
#end
$pp.setType($type)
			}
#end
			f.set$pp.Type (object, null, value, t);
		}
		else
		{
#if ($pp.numeric_char)
			switch (typeId)
			{
#foreach ($t in $numeric_char)
$pp.setType($t)
				case TypeId.$pp.TYPE:
					field.set$pp.Type (object, ($t) value);
					return ($type) ($t) value;
#end
$pp.setType($type)
			}
#end
			field.set$pp.Type (object, value);
		}
		return value;
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public boolean getBoolean (Object object) throws IllegalAccessException
	{
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getBoolean
				(object, null)
			: field.getBoolean (object);
	}
// generated
// generated
	public boolean setBoolean (Object object, boolean value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			f.setBoolean (object, null, value, t);
		}
		else
		{
			field.setBoolean (object, value);
		}
		return value;
	}
// generated
// generated
	public byte getByte (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (byte) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getByte
				(object, null)
			: field.getByte (object);
	}
// generated
// generated
	public byte setByte (Object object, byte value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (byte) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (byte) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (byte) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (byte) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (byte) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (byte) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (byte) value;
// generated
			}
			f.setByte (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (byte) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (byte) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (byte) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (byte) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (byte) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (byte) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (byte) (double) value;
// generated
			}
			field.setByte (object, value);
		}
		return value;
	}
// generated
// generated
	public short getShort (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (short) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getShort
				(object, null)
			: field.getShort (object);
	}
// generated
// generated
	public short setShort (Object object, short value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (short) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (short) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (short) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (short) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (short) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (short) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (short) value;
// generated
			}
			f.setShort (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (short) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (short) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (short) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (short) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (short) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (short) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (short) (double) value;
// generated
			}
			field.setShort (object, value);
		}
		return value;
	}
// generated
// generated
	public char getChar (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (char) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getChar
				(object, null)
			: field.getChar (object);
	}
// generated
// generated
	public char setChar (Object object, char value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (char) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (char) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (char) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (char) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (char) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (char) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (char) value;
// generated
			}
			f.setChar (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (char) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (char) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (char) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (char) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (char) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (char) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (char) (double) value;
// generated
			}
			field.setChar (object, value);
		}
		return value;
	}
// generated
// generated
	public int getInt (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (int) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getInt
				(object, null)
			: field.getInt (object);
	}
// generated
// generated
	public int setInt (Object object, int value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (int) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (int) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (int) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (int) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (int) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (int) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (int) value;
// generated
			}
			f.setInt (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (int) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (int) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (int) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (int) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (int) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (int) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (int) (double) value;
// generated
			}
			field.setInt (object, value);
		}
		return value;
	}
// generated
// generated
	public long getLong (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (long) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getLong
				(object, null)
			: field.getLong (object);
	}
// generated
// generated
	public long setLong (Object object, long value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (long) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (long) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (long) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (long) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (long) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (long) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (long) value;
// generated
			}
			f.setLong (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (long) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (long) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (long) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (long) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (long) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (long) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (long) (double) value;
// generated
			}
			field.setLong (object, value);
		}
		return value;
	}
// generated
// generated
	public float getFloat (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (float) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getFloat
				(object, null)
			: field.getFloat (object);
	}
// generated
// generated
	public float setFloat (Object object, float value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (float) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (float) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (float) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (float) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (float) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (float) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (float) value;
// generated
			}
			f.setFloat (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (float) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (float) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (float) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (float) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (float) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (float) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (float) (double) value;
// generated
			}
			field.setFloat (object, value);
		}
		return value;
	}
// generated
// generated
	public double getDouble (Object object) throws IllegalAccessException
	{
		switch (typeId)
		{
// generated
			case TypeId.BYTE:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getByte
									(object, null)
								: field.getByte (object));
// generated
			case TypeId.SHORT:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getShort
									(object, null)
								: field.getShort (object));
// generated
			case TypeId.CHAR:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getChar
									(object, null)
								: field.getChar (object));
// generated
			case TypeId.INT:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getInt
									(object, null)
								: field.getInt (object));
// generated
			case TypeId.LONG:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getLong
									(object, null)
								: field.getLong (object));
// generated
			case TypeId.FLOAT:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getFloat
									(object, null)
								: field.getFloat (object));
// generated
			case TypeId.DOUBLE:
				return (double) ((field instanceof PersistenceField)
								? ((PersistenceField) field).getDouble
									(object, null)
								: field.getDouble (object));
// generated
		}
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getDouble
				(object, null)
			: field.getDouble (object);
	}
// generated
// generated
	public double setDouble (Object object, double value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					f.setByte (object, null, (byte) value, t);
					return (double) value;
// generated
				case TypeId.SHORT:
					f.setShort (object, null, (short) value, t);
					return (double) value;
// generated
				case TypeId.CHAR:
					f.setChar (object, null, (char) value, t);
					return (double) value;
// generated
				case TypeId.INT:
					f.setInt (object, null, (int) value, t);
					return (double) value;
// generated
				case TypeId.LONG:
					f.setLong (object, null, (long) value, t);
					return (double) value;
// generated
				case TypeId.FLOAT:
					f.setFloat (object, null, (float) value, t);
					return (double) value;
// generated
				case TypeId.DOUBLE:
					f.setDouble (object, null, (double) value, t);
					return (double) value;
// generated
			}
			f.setDouble (object, null, value, t);
		}
		else
		{
			switch (typeId)
			{
// generated
				case TypeId.BYTE:
					field.setByte (object, (byte) value);
					return (double) (byte) value;
// generated
				case TypeId.SHORT:
					field.setShort (object, (short) value);
					return (double) (short) value;
// generated
				case TypeId.CHAR:
					field.setChar (object, (char) value);
					return (double) (char) value;
// generated
				case TypeId.INT:
					field.setInt (object, (int) value);
					return (double) (int) value;
// generated
				case TypeId.LONG:
					field.setLong (object, (long) value);
					return (double) (long) value;
// generated
				case TypeId.FLOAT:
					field.setFloat (object, (float) value);
					return (double) (float) value;
// generated
				case TypeId.DOUBLE:
					field.setDouble (object, (double) value);
					return (double) (double) value;
// generated
			}
			field.setDouble (object, value);
		}
		return value;
	}
// generated
// generated
	public Object getObject (Object object) throws IllegalAccessException
	{
		return (field instanceof PersistenceField)
			? ((PersistenceField) field).getObject
				(object, null)
			: field.getObject (object);
	}
// generated
// generated
	public Object setObject (Object object, Object value, Transaction t) throws IllegalAccessException
	{
		if (field instanceof PersistenceField)
		{
			PersistenceField f = (PersistenceField) field;
			f.setObject (object, null, value, t);
		}
		else
		{
			field.setObject (object, value);
		}
		return value;
	}
//!! *# End of generated code

	public Object setSubfield (Object object, FieldChain fields, int[] indices,
							   Object value, Transaction t)
	{
		if (field instanceof PersistenceField)
		{
			IndirectField i = new IndirectField ((PersistenceField) field);
			i.add (fields).set
				((PersistenceCapable) object, indices, value, t);
		}
		else
		{
			throw new UnsupportedOperationException ();
		}
		return value;
	}
}
