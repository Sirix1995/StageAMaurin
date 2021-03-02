
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

import java.io.IOException;
import de.grogra.reflect.*;

public class ModificationQueue extends XAQueue
{
	public static final Type TYPE
		= ClassAdapter.wrap (ModificationQueue.class);


	public ModificationQueue (PersistenceManager manager)
	{
		super (manager, false);
		setItemSize (SET_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (SET_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (SET_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (SET_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (SET_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (SET_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (SET_FLOAT, 0, -1, 1, 1, 0, 0);
		setItemSize (SET_DOUBLE, 0, -1, 1, 0, 1, 0);
		setItemSize (SET_OBJECT, -1, -1, -1, -1, -1, 0);

		setItemSize (ADD_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (ADD_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (ADD_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (ADD_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (ADD_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (ADD_FLOAT, 0, -1, 1, 1, 0, 0);
		setItemSize (ADD_DOUBLE, 0, -1, 1, 0, 1, 0);

		setItemSize (MUL_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (MUL_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (MUL_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (MUL_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (MUL_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (MUL_FLOAT, 0, -1, 1, 1, 0, 0);
		setItemSize (MUL_DOUBLE, 0, -1, 1, 0, 1, 0);

		setItemSize (DIV_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (DIV_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (DIV_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (DIV_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (DIV_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (DIV_FLOAT, 0, -1, 1, 1, 0, 0);
		setItemSize (DIV_DOUBLE, 0, -1, 1, 0, 1, 0);
/*!!
#foreach ($t in ["OR", "AND", "XOR"])
		setItemSize (${t}_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (${t}_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (${t}_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (${t}_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (${t}_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (${t}_LONG, 0, -1, 2, 0, 0, 0);
#end
!!*/
//!! #* Start of generated code
		setItemSize (OR_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (OR_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (OR_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (OR_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (OR_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (OR_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (AND_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (AND_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (AND_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (AND_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (AND_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (AND_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (XOR_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (XOR_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (XOR_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (XOR_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (XOR_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (XOR_LONG, 0, -1, 2, 0, 0, 0);
//!! *# End of generated code
		setItemSize (MAKE_PERSISTENT, -1, -1, -1, -1, -1, 0);
	}

	@Override
	public boolean beginManaged (ManageableType type, boolean writeType)
	{
		super.beginManaged (type, writeType);
		return false;
	}

	public void makePersistent (PersistenceCapable pc, long id)
	{
		writeItem (MAKE_PERSISTENT);
		writeLong (id);
		boolean diff = beginManaged (pc.getManageableType (), true);
		try
		{
			pc.getManageableType ().write (pc, this, diff);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		endManaged (pc, true);
	}

/*!!

#set ($i = 0)
#foreach ($type in $types)
$pp.setType($type)

	public static final int SET_$pp.TYPE = XAQueue.MIN_UNUSED_ITEM + $i;

	public void set$pp.Type (PersistenceCapable o, PersistenceField field,
							 int[] indices, $type value)
	{
		writeItem (SET_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
#if ($pp.Object)
		try
		{
			field.writeObject (value, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
#else
		write$pp.Type (value);
#end
	}

	#set ($i = $i + 1)
#end


#foreach ($type in $numeric_char)
$pp.setType($type)

	public static final int ADD_$pp.TYPE
		= XAQueue.MIN_UNUSED_ITEM + $i;

	public void add$pp.Type (PersistenceCapable o, PersistenceField field,
							 int[] indices, $type value)
	{
		writeItem (ADD_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
		write$pp.Type (value);
	}

	#set ($i = $i + 1)
#end


#foreach ($type in $numeric_char)
$pp.setType($type)

	public static final int MUL_$pp.TYPE = XAQueue.MIN_UNUSED_ITEM + $i;

	public void mul$pp.Type (PersistenceCapable o, PersistenceField field,
							 int[] indices, $type value)
	{
		writeItem (MUL_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
		write$pp.Type (value);
	}

	#set ($i = $i + 1)
#end


#foreach ($type in $numeric_char)
$pp.setType($type)

	public static final int DIV_$pp.TYPE = XAQueue.MIN_UNUSED_ITEM + $i;

	public void div$pp.Type (PersistenceCapable o, PersistenceField field,
							 int[] indices, $type value)
	{
		writeItem (DIV_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
		write$pp.Type (value);
	}

	#set ($i = $i + 1)
#end

#foreach ($t in ["OR", "AND", "XOR"])

#foreach ($type in $bittypes)
$pp.setType($type)

	public static final int ${t}_$pp.TYPE
		= XAQueue.MIN_UNUSED_ITEM + $i;

	public void ${t.toLowerCase()}$pp.Type
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, $type value)
	{
		writeItem (${t}_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
		write$pp.Type (value);
	}

	#set ($i = $i + 1)
#end

#end

	public static final int MAKE_PERSISTENT
		= XAQueue.MIN_UNUSED_ITEM + $i;

	public static final int MIN_UNUSED_ITEM = MAKE_PERSISTENT + 1;

!!*/
//!! #* Start of generated code
// generated
// generated
// generated
	public static final int SET_BOOLEAN = XAQueue.MIN_UNUSED_ITEM + 0;
// generated
	public void setBoolean (PersistenceCapable o, PersistenceField field,
							 int[] indices, boolean value)
	{
		writeItem (SET_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (value);
	}
// generated
	
// generated
	public static final int SET_BYTE = XAQueue.MIN_UNUSED_ITEM + 1;
// generated
	public void setByte (PersistenceCapable o, PersistenceField field,
							 int[] indices, byte value)
	{
		writeItem (SET_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int SET_SHORT = XAQueue.MIN_UNUSED_ITEM + 2;
// generated
	public void setShort (PersistenceCapable o, PersistenceField field,
							 int[] indices, short value)
	{
		writeItem (SET_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int SET_CHAR = XAQueue.MIN_UNUSED_ITEM + 3;
// generated
	public void setChar (PersistenceCapable o, PersistenceField field,
							 int[] indices, char value)
	{
		writeItem (SET_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int SET_INT = XAQueue.MIN_UNUSED_ITEM + 4;
// generated
	public void setInt (PersistenceCapable o, PersistenceField field,
							 int[] indices, int value)
	{
		writeItem (SET_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int SET_LONG = XAQueue.MIN_UNUSED_ITEM + 5;
// generated
	public void setLong (PersistenceCapable o, PersistenceField field,
							 int[] indices, long value)
	{
		writeItem (SET_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
	public static final int SET_FLOAT = XAQueue.MIN_UNUSED_ITEM + 6;
// generated
	public void setFloat (PersistenceCapable o, PersistenceField field,
							 int[] indices, float value)
	{
		writeItem (SET_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (value);
	}
// generated
	
// generated
	public static final int SET_DOUBLE = XAQueue.MIN_UNUSED_ITEM + 7;
// generated
	public void setDouble (PersistenceCapable o, PersistenceField field,
							 int[] indices, double value)
	{
		writeItem (SET_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (value);
	}
// generated
	
// generated
	public static final int SET_OBJECT = XAQueue.MIN_UNUSED_ITEM + 8;
// generated
	public void setObject (PersistenceCapable o, PersistenceField field,
							 int[] indices, Object value)
	{
		writeItem (SET_OBJECT);
		writeLong (o.getId ());
		field.write (indices, this);
		try
		{
			field.writeObject (value, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}
// generated
	
// generated
// generated
// generated
	public static final int ADD_BYTE
		= XAQueue.MIN_UNUSED_ITEM + 9;
// generated
	public void addByte (PersistenceCapable o, PersistenceField field,
							 int[] indices, byte value)
	{
		writeItem (ADD_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int ADD_SHORT
		= XAQueue.MIN_UNUSED_ITEM + 10;
// generated
	public void addShort (PersistenceCapable o, PersistenceField field,
							 int[] indices, short value)
	{
		writeItem (ADD_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int ADD_CHAR
		= XAQueue.MIN_UNUSED_ITEM + 11;
// generated
	public void addChar (PersistenceCapable o, PersistenceField field,
							 int[] indices, char value)
	{
		writeItem (ADD_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int ADD_INT
		= XAQueue.MIN_UNUSED_ITEM + 12;
// generated
	public void addInt (PersistenceCapable o, PersistenceField field,
							 int[] indices, int value)
	{
		writeItem (ADD_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int ADD_LONG
		= XAQueue.MIN_UNUSED_ITEM + 13;
// generated
	public void addLong (PersistenceCapable o, PersistenceField field,
							 int[] indices, long value)
	{
		writeItem (ADD_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
	public static final int ADD_FLOAT
		= XAQueue.MIN_UNUSED_ITEM + 14;
// generated
	public void addFloat (PersistenceCapable o, PersistenceField field,
							 int[] indices, float value)
	{
		writeItem (ADD_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (value);
	}
// generated
	
// generated
	public static final int ADD_DOUBLE
		= XAQueue.MIN_UNUSED_ITEM + 15;
// generated
	public void addDouble (PersistenceCapable o, PersistenceField field,
							 int[] indices, double value)
	{
		writeItem (ADD_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (value);
	}
// generated
	
// generated
// generated
// generated
	public static final int MUL_BYTE = XAQueue.MIN_UNUSED_ITEM + 16;
// generated
	public void mulByte (PersistenceCapable o, PersistenceField field,
							 int[] indices, byte value)
	{
		writeItem (MUL_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int MUL_SHORT = XAQueue.MIN_UNUSED_ITEM + 17;
// generated
	public void mulShort (PersistenceCapable o, PersistenceField field,
							 int[] indices, short value)
	{
		writeItem (MUL_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int MUL_CHAR = XAQueue.MIN_UNUSED_ITEM + 18;
// generated
	public void mulChar (PersistenceCapable o, PersistenceField field,
							 int[] indices, char value)
	{
		writeItem (MUL_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int MUL_INT = XAQueue.MIN_UNUSED_ITEM + 19;
// generated
	public void mulInt (PersistenceCapable o, PersistenceField field,
							 int[] indices, int value)
	{
		writeItem (MUL_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int MUL_LONG = XAQueue.MIN_UNUSED_ITEM + 20;
// generated
	public void mulLong (PersistenceCapable o, PersistenceField field,
							 int[] indices, long value)
	{
		writeItem (MUL_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
	public static final int MUL_FLOAT = XAQueue.MIN_UNUSED_ITEM + 21;
// generated
	public void mulFloat (PersistenceCapable o, PersistenceField field,
							 int[] indices, float value)
	{
		writeItem (MUL_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (value);
	}
// generated
	
// generated
	public static final int MUL_DOUBLE = XAQueue.MIN_UNUSED_ITEM + 22;
// generated
	public void mulDouble (PersistenceCapable o, PersistenceField field,
							 int[] indices, double value)
	{
		writeItem (MUL_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (value);
	}
// generated
	
// generated
// generated
// generated
	public static final int DIV_BYTE = XAQueue.MIN_UNUSED_ITEM + 23;
// generated
	public void divByte (PersistenceCapable o, PersistenceField field,
							 int[] indices, byte value)
	{
		writeItem (DIV_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int DIV_SHORT = XAQueue.MIN_UNUSED_ITEM + 24;
// generated
	public void divShort (PersistenceCapable o, PersistenceField field,
							 int[] indices, short value)
	{
		writeItem (DIV_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int DIV_CHAR = XAQueue.MIN_UNUSED_ITEM + 25;
// generated
	public void divChar (PersistenceCapable o, PersistenceField field,
							 int[] indices, char value)
	{
		writeItem (DIV_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int DIV_INT = XAQueue.MIN_UNUSED_ITEM + 26;
// generated
	public void divInt (PersistenceCapable o, PersistenceField field,
							 int[] indices, int value)
	{
		writeItem (DIV_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int DIV_LONG = XAQueue.MIN_UNUSED_ITEM + 27;
// generated
	public void divLong (PersistenceCapable o, PersistenceField field,
							 int[] indices, long value)
	{
		writeItem (DIV_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
	public static final int DIV_FLOAT = XAQueue.MIN_UNUSED_ITEM + 28;
// generated
	public void divFloat (PersistenceCapable o, PersistenceField field,
							 int[] indices, float value)
	{
		writeItem (DIV_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (value);
	}
// generated
	
// generated
	public static final int DIV_DOUBLE = XAQueue.MIN_UNUSED_ITEM + 29;
// generated
	public void divDouble (PersistenceCapable o, PersistenceField field,
							 int[] indices, double value)
	{
		writeItem (DIV_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (value);
	}
// generated
	
// generated
// generated
// generated
	public static final int OR_BOOLEAN
		= XAQueue.MIN_UNUSED_ITEM + 30;
// generated
	public void orBoolean
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, boolean value)
	{
		writeItem (OR_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (value);
	}
// generated
	
// generated
	public static final int OR_BYTE
		= XAQueue.MIN_UNUSED_ITEM + 31;
// generated
	public void orByte
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, byte value)
	{
		writeItem (OR_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int OR_SHORT
		= XAQueue.MIN_UNUSED_ITEM + 32;
// generated
	public void orShort
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, short value)
	{
		writeItem (OR_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int OR_CHAR
		= XAQueue.MIN_UNUSED_ITEM + 33;
// generated
	public void orChar
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, char value)
	{
		writeItem (OR_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int OR_INT
		= XAQueue.MIN_UNUSED_ITEM + 34;
// generated
	public void orInt
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, int value)
	{
		writeItem (OR_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int OR_LONG
		= XAQueue.MIN_UNUSED_ITEM + 35;
// generated
	public void orLong
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, long value)
	{
		writeItem (OR_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
// generated
// generated
	public static final int AND_BOOLEAN
		= XAQueue.MIN_UNUSED_ITEM + 36;
// generated
	public void andBoolean
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, boolean value)
	{
		writeItem (AND_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (value);
	}
// generated
	
// generated
	public static final int AND_BYTE
		= XAQueue.MIN_UNUSED_ITEM + 37;
// generated
	public void andByte
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, byte value)
	{
		writeItem (AND_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int AND_SHORT
		= XAQueue.MIN_UNUSED_ITEM + 38;
// generated
	public void andShort
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, short value)
	{
		writeItem (AND_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int AND_CHAR
		= XAQueue.MIN_UNUSED_ITEM + 39;
// generated
	public void andChar
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, char value)
	{
		writeItem (AND_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int AND_INT
		= XAQueue.MIN_UNUSED_ITEM + 40;
// generated
	public void andInt
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, int value)
	{
		writeItem (AND_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int AND_LONG
		= XAQueue.MIN_UNUSED_ITEM + 41;
// generated
	public void andLong
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, long value)
	{
		writeItem (AND_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
// generated
// generated
	public static final int XOR_BOOLEAN
		= XAQueue.MIN_UNUSED_ITEM + 42;
// generated
	public void xorBoolean
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, boolean value)
	{
		writeItem (XOR_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (value);
	}
// generated
	
// generated
	public static final int XOR_BYTE
		= XAQueue.MIN_UNUSED_ITEM + 43;
// generated
	public void xorByte
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, byte value)
	{
		writeItem (XOR_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (value);
	}
// generated
	
// generated
	public static final int XOR_SHORT
		= XAQueue.MIN_UNUSED_ITEM + 44;
// generated
	public void xorShort
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, short value)
	{
		writeItem (XOR_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (value);
	}
// generated
	
// generated
	public static final int XOR_CHAR
		= XAQueue.MIN_UNUSED_ITEM + 45;
// generated
	public void xorChar
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, char value)
	{
		writeItem (XOR_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (value);
	}
// generated
	
// generated
	public static final int XOR_INT
		= XAQueue.MIN_UNUSED_ITEM + 46;
// generated
	public void xorInt
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, int value)
	{
		writeItem (XOR_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (value);
	}
// generated
	
// generated
	public static final int XOR_LONG
		= XAQueue.MIN_UNUSED_ITEM + 47;
// generated
	public void xorLong
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, long value)
	{
		writeItem (XOR_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (value);
	}
// generated
	
// generated
	public static final int MAKE_PERSISTENT
		= XAQueue.MIN_UNUSED_ITEM + 48;
// generated
	public static final int MIN_UNUSED_ITEM = MAKE_PERSISTENT + 1;
// generated
//!! *# End of generated code

	protected class Applier extends Reader
	{
		protected final IndirectField indirectField = new IndirectField ();


		protected Applier ()
		{
			super ();
		}


		protected boolean apply (int item, Transaction t) throws IOException
		{
			IndirectField f;
			switch (item & ITEM_MASK)
			{
				case BEGIN_LEVEL:
					enter (false);
					boolean b = apply (t);
					leave ();
					return b;
/*!!
#foreach ($type in $types)
$pp.setType($type)

				case SET_$pp.TYPE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSet$pp.Type (o, f, a);
					t.finishSet (this);
					return true;
				}
#end
#foreach ($type in $numeric_char)
$pp.setType($type)
				case ADD_$pp.TYPE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.set$pp.Type (o, a, ($type) (f.get$pp.Type (o, a)
												  + read$pp.Type ()), t);
					return true;
				}
#end
#foreach ($type in $numeric_char)
$pp.setType($type)
				case MUL_$pp.TYPE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.set$pp.Type (o, a, ($type) (f.get$pp.Type (o, a)
												  * read$pp.Type ()), t);
					return true;
				}
#end
#foreach ($type in $numeric_char)
$pp.setType($type)
				case DIV_$pp.TYPE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.set$pp.Type (o, a, ($type) (f.get$pp.Type (o, a)
												  / read$pp.Type ()), t);
					return true;
				}
#end

#foreach ($t in ["OR", "AND", "XOR"])

#foreach ($type in $bittypes)
$pp.setType($type)
				case ${t}_$pp.TYPE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.set$pp.Type (o, a, ($type) (f.get$pp.Type (o, a)
	#if ($t == "OR")
		|
	#elseif ($t == "AND")
		&
	#elseif ($t == "XOR")
		^
	#end
											   read$pp.Type ()), t);
					return true;
				}
#end

#end
!!*/
//!! #* Start of generated code
// generated
// generated
				case SET_BOOLEAN:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetBoolean (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetByte (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetShort (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetChar (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetInt (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetLong (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_FLOAT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetFloat (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_DOUBLE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetDouble (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
// generated
				case SET_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					t.prepareSetObject (o, f, a);
					t.finishSet (this);
					return true;
				}
// generated
				case ADD_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setByte (o, a, (byte) (f.getByte (o, a)
												  + readByte ()), t);
					return true;
				}
// generated
				case ADD_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setShort (o, a, (short) (f.getShort (o, a)
												  + readShort ()), t);
					return true;
				}
// generated
				case ADD_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setChar (o, a, (char) (f.getChar (o, a)
												  + readChar ()), t);
					return true;
				}
// generated
				case ADD_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setInt (o, a, (int) (f.getInt (o, a)
												  + readInt ()), t);
					return true;
				}
// generated
				case ADD_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setLong (o, a, (long) (f.getLong (o, a)
												  + readLong ()), t);
					return true;
				}
// generated
				case ADD_FLOAT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setFloat (o, a, (float) (f.getFloat (o, a)
												  + readFloat ()), t);
					return true;
				}
// generated
				case ADD_DOUBLE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setDouble (o, a, (double) (f.getDouble (o, a)
												  + readDouble ()), t);
					return true;
				}
// generated
				case MUL_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setByte (o, a, (byte) (f.getByte (o, a)
												  * readByte ()), t);
					return true;
				}
// generated
				case MUL_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setShort (o, a, (short) (f.getShort (o, a)
												  * readShort ()), t);
					return true;
				}
// generated
				case MUL_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setChar (o, a, (char) (f.getChar (o, a)
												  * readChar ()), t);
					return true;
				}
// generated
				case MUL_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setInt (o, a, (int) (f.getInt (o, a)
												  * readInt ()), t);
					return true;
				}
// generated
				case MUL_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setLong (o, a, (long) (f.getLong (o, a)
												  * readLong ()), t);
					return true;
				}
// generated
				case MUL_FLOAT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setFloat (o, a, (float) (f.getFloat (o, a)
												  * readFloat ()), t);
					return true;
				}
// generated
				case MUL_DOUBLE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setDouble (o, a, (double) (f.getDouble (o, a)
												  * readDouble ()), t);
					return true;
				}
// generated
				case DIV_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setByte (o, a, (byte) (f.getByte (o, a)
												  / readByte ()), t);
					return true;
				}
// generated
				case DIV_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setShort (o, a, (short) (f.getShort (o, a)
												  / readShort ()), t);
					return true;
				}
// generated
				case DIV_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setChar (o, a, (char) (f.getChar (o, a)
												  / readChar ()), t);
					return true;
				}
// generated
				case DIV_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setInt (o, a, (int) (f.getInt (o, a)
												  / readInt ()), t);
					return true;
				}
// generated
				case DIV_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setLong (o, a, (long) (f.getLong (o, a)
												  / readLong ()), t);
					return true;
				}
// generated
				case DIV_FLOAT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setFloat (o, a, (float) (f.getFloat (o, a)
												  / readFloat ()), t);
					return true;
				}
// generated
				case DIV_DOUBLE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setDouble (o, a, (double) (f.getDouble (o, a)
												  / readDouble ()), t);
					return true;
				}
// generated
// generated
// generated
				case OR_BOOLEAN:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setBoolean (o, a, (boolean) (f.getBoolean (o, a)
			|
												   readBoolean ()), t);
					return true;
				}
// generated
				case OR_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setByte (o, a, (byte) (f.getByte (o, a)
			|
												   readByte ()), t);
					return true;
				}
// generated
				case OR_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setShort (o, a, (short) (f.getShort (o, a)
			|
												   readShort ()), t);
					return true;
				}
// generated
				case OR_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setChar (o, a, (char) (f.getChar (o, a)
			|
												   readChar ()), t);
					return true;
				}
// generated
				case OR_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setInt (o, a, (int) (f.getInt (o, a)
			|
												   readInt ()), t);
					return true;
				}
// generated
				case OR_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setLong (o, a, (long) (f.getLong (o, a)
			|
												   readLong ()), t);
					return true;
				}
// generated
// generated
// generated
				case AND_BOOLEAN:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setBoolean (o, a, (boolean) (f.getBoolean (o, a)
			&
												   readBoolean ()), t);
					return true;
				}
// generated
				case AND_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setByte (o, a, (byte) (f.getByte (o, a)
			&
												   readByte ()), t);
					return true;
				}
// generated
				case AND_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setShort (o, a, (short) (f.getShort (o, a)
			&
												   readShort ()), t);
					return true;
				}
// generated
				case AND_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setChar (o, a, (char) (f.getChar (o, a)
			&
												   readChar ()), t);
					return true;
				}
// generated
				case AND_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setInt (o, a, (int) (f.getInt (o, a)
			&
												   readInt ()), t);
					return true;
				}
// generated
				case AND_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setLong (o, a, (long) (f.getLong (o, a)
			&
												   readLong ()), t);
					return true;
				}
// generated
// generated
// generated
				case XOR_BOOLEAN:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setBoolean (o, a, (boolean) (f.getBoolean (o, a)
			^
												   readBoolean ()), t);
					return true;
				}
// generated
				case XOR_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setByte (o, a, (byte) (f.getByte (o, a)
			^
												   readByte ()), t);
					return true;
				}
// generated
				case XOR_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setShort (o, a, (short) (f.getShort (o, a)
			^
												   readShort ()), t);
					return true;
				}
// generated
				case XOR_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setChar (o, a, (char) (f.getChar (o, a)
			^
												   readChar ()), t);
					return true;
				}
// generated
				case XOR_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setInt (o, a, (int) (f.getInt (o, a)
			^
												   readInt ()), t);
					return true;
				}
// generated
				case XOR_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = (f = indirectField).read (o, this);
					f.setLong (o, a, (long) (f.getLong (o, a)
			^
												   readLong ()), t);
					return true;
				}
// generated
//!! *# End of generated code
				default:
					throw new IOException ();
			}
		}


		public final boolean apply (Transaction t) throws IOException
		{
			boolean modified = false;
			int item = readItem ();
			while (item >= 0)
			{
				modified |= apply (item, t);
				item = next ();
			}
			return modified;
		}
	}


	protected Applier createApplier ()
	{
		return new Applier ();
	}


	private final Applier applier = createApplier ();

	public boolean apply (Transaction t) throws IOException
	{
		applier.resetCursor ();
		return applier.apply (t);
	}

}
