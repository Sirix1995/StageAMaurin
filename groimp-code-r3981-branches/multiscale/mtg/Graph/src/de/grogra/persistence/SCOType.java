
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
import java.lang.reflect.InvocationTargetException;

public class SCOType extends ManageableType
{
	protected static final SCOType $TYPE
		= (SCOType) new SCOType (null, OBJECT, null, false).validate ();
	protected static final int FIELD_COUNT = 0;

	protected final Object representative;


	public class Field extends ManageableType.Field
	{
		public final int id;


		public Field (String name, int modifiers, Type type,
					  Type componentType, int id)
		{
			super (name, modifiers, type, componentType, null);
			this.id = id;
		}


		public Field (String name, int modifiers, Class cls,
					  Class componentType, int id)
		{
			this (name, modifiers, ClassAdapter.wrap (cls),
				  ClassAdapter.wrap (componentType), id);
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)
		@Override
		public $type get$pp.Type (Object object)
		{
			return SCOType.this.get$pp.Type (object, id);
		}

		@Override
#if ($pp.Object)
		protected void setObjectImpl (Object object, $type value)
#else
		public void set$pp.Type (Object object, $type value)
#end
		{
			SCOType.this.set$pp.Type (object, id, value);
		}
#end
!!*/
//!! #* Start of generated code
// generated
		@Override
		public boolean getBoolean (Object object)
		{
			return SCOType.this.getBoolean (object, id);
		}
// generated
		@Override
		public void setBoolean (Object object, boolean value)
		{
			SCOType.this.setBoolean (object, id, value);
		}
// generated
		@Override
		public byte getByte (Object object)
		{
			return SCOType.this.getByte (object, id);
		}
// generated
		@Override
		public void setByte (Object object, byte value)
		{
			SCOType.this.setByte (object, id, value);
		}
// generated
		@Override
		public short getShort (Object object)
		{
			return SCOType.this.getShort (object, id);
		}
// generated
		@Override
		public void setShort (Object object, short value)
		{
			SCOType.this.setShort (object, id, value);
		}
// generated
		@Override
		public char getChar (Object object)
		{
			return SCOType.this.getChar (object, id);
		}
// generated
		@Override
		public void setChar (Object object, char value)
		{
			SCOType.this.setChar (object, id, value);
		}
// generated
		@Override
		public int getInt (Object object)
		{
			return SCOType.this.getInt (object, id);
		}
// generated
		@Override
		public void setInt (Object object, int value)
		{
			SCOType.this.setInt (object, id, value);
		}
// generated
		@Override
		public long getLong (Object object)
		{
			return SCOType.this.getLong (object, id);
		}
// generated
		@Override
		public void setLong (Object object, long value)
		{
			SCOType.this.setLong (object, id, value);
		}
// generated
		@Override
		public float getFloat (Object object)
		{
			return SCOType.this.getFloat (object, id);
		}
// generated
		@Override
		public void setFloat (Object object, float value)
		{
			SCOType.this.setFloat (object, id, value);
		}
// generated
		@Override
		public double getDouble (Object object)
		{
			return SCOType.this.getDouble (object, id);
		}
// generated
		@Override
		public void setDouble (Object object, double value)
		{
			SCOType.this.setDouble (object, id, value);
		}
// generated
		@Override
		public Object getObject (Object object)
		{
			return SCOType.this.getObject (object, id);
		}
// generated
		@Override
		protected void setObjectImpl (Object object, Object value)
		{
			SCOType.this.setObject (object, id, value);
		}
//!! *# End of generated code

		@Override
		public boolean isWritable (Object object)
		{
			return SCOType.this.isWritable (object, this);
		}
	}


	private SCOType (Object representative, Type type, SCOType supertype,
					 boolean register)
	{
		super (type, (supertype == null) ? $TYPE : supertype, register);
		this.representative = representative;
	}


	public SCOType (Type type, SCOType supertype)
	{
		this (null, type, supertype, true);
	}


	public SCOType (Class type, SCOType supertype)
	{
		this (null, ClassAdapter.wrap (type), supertype, true);
	}


	public SCOType (Object representative, SCOType supertype)
	{
		this (representative, ClassAdapter.wrap (representative.getClass ()),
			  supertype, true);
	}


	protected final Field addManagedField (String name, int modifiers, Type type,
										   Type componentType, int id)
	{
		Field f = new Field (name, modifiers, type, componentType, id);
		addManagedField (f);
		return f;
	}


	protected final Field addManagedField (String name, int modifiers, Class type,
										   Class componentType, int id)
	{
		Field f = new Field (name, modifiers, type, componentType, id);
		addManagedField (f);
		return f;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	protected void set$pp.Type (Object object, int id, $type value)
	{
		throw new InvalidIdException (this, id);
	}


	protected $type get$pp.Type (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	protected void setBoolean (Object object, int id, boolean value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected boolean getBoolean (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setByte (Object object, int id, byte value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected byte getByte (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setShort (Object object, int id, short value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected short getShort (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setChar (Object object, int id, char value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected char getChar (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setInt (Object object, int id, int value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected int getInt (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setLong (Object object, int id, long value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected long getLong (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setFloat (Object object, int id, float value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected float getFloat (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setDouble (Object object, int id, double value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected double getDouble (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected void setObject (Object object, int id, Object value)
	{
		throw new InvalidIdException (this, id);
	}
// generated
// generated
	protected Object getObject (Object object, int id)
	{
		throw new InvalidIdException (this, id);
	}
//!! *# End of generated code

	protected boolean isWritable (Object object, Field field)
	{
		return true;
	}


	@Override
	public ManageableType validate ()
	{
		super.validate ();
		for (int i = managedFieldCount - 1; i >= 0; i--)
		{
			if (!((managedFields[i] instanceof Field)
				  && (((Field) managedFields[i]).id
					  == managedFields[i].fieldId)))
			{
				throw new AssertionError (managedFields[i]);
			}
		}
		return this;
	}


	@Override
	public Object newInstance () throws InvocationTargetException,
		InstantiationException, IllegalAccessException
	{
		return (representative != null) ? newInstance (representative)
			: super.newInstance ();
	}


	@Override
	public Object getRepresentative ()
	{
		return representative;
	}


	protected Object newInstance (Object repr) throws InvocationTargetException,
		InstantiationException, IllegalAccessException
	{
		ManageableType t = getManageableSupertype ();
		return (t == null) ? super.newInstance ()
			: ((SCOType) t).newInstance (repr);
	}

}

