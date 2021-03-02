
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

import java.io.*;
import java.util.List;
import java.util.Hashtable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import de.grogra.reflect.*;
import de.grogra.util.*;
import de.grogra.xl.util.BooleanList;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.CharList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.ShortList;
import de.grogra.xl.util.ObjectList;

public abstract class ManageableType extends TypeDecorator
{
	public abstract class Field extends PersistenceField
	{
		public static final int SCO = 1 * MIN_UNUSED_MODIFIER;
		public static final int FCO = 2 * MIN_UNUSED_MODIFIER;
		public static final int UNMANAGED = 3 * MIN_UNUSED_MODIFIER;

		public static final int OBJECT_CLASS_MASK = 3 * MIN_UNUSED_MODIFIER;

		public static final int GETS_COPY = MIN_UNUSED_MODIFIER << 2;
		public static final int DEFINES_SHARED = MIN_UNUSED_MODIFIER << 3;
		public static final int HIDDEN = MIN_UNUSED_MODIFIER << 4;


		final boolean sco, managed, isArray, isArrayComponent, usesList;
		final ArrayComponent arrayComponent;
		Field arrayField;
		int fieldId = -1;
		ManageableType manageableType;

		private Type type;
		private int serializationMethod = -1;
		private Quantity quantity;
		private Number minValue;
		private Number maxValue;


		protected Field (String name, int modifiers, Type type, Type componentType)
		{
			this (name, modifiers, type, componentType, null);
		}


		Field (String name, int modifiers, Type type,
			   Type componentType, ArrayComponent ac)
		{
			super (name, de.grogra.reflect.Reflection.getFieldDescriptor (name, type),
				   modifiers, ManageableType.this);
			Type atype;
			if (Reflection.isSupertypeOrSame ("java.util.List", type))
			{
				atype = Type.OBJECT.getArrayType ();
			}
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
			else if (type.getBinaryName ().equals ("de.grogra.util.${pp.Type}List"))
			{
				atype = Type.${pp.TYPE}.getArrayType ();
			}
#end
!!*/
//!! #* Start of generated code
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.BooleanList"))
			{
				atype = Type.BOOLEAN.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.ByteList"))
			{
				atype = Type.BYTE.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.ShortList"))
			{
				atype = Type.SHORT.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.CharList"))
			{
				atype = Type.CHAR.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.IntList"))
			{
				atype = Type.INT.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.LongList"))
			{
				atype = Type.LONG.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.FloatList"))
			{
				atype = Type.FLOAT.getArrayType ();
			}
// generated
			else if (type.getBinaryName ().equals ("de.grogra.util.DoubleList"))
			{
				atype = Type.DOUBLE.getArrayType ();
			}
//!! *# End of generated code
			else
			{
				atype = type;
			}
			if (componentType == null)
			{
				componentType = atype;
			}
			if ((ac == null) && Reflection.isArray (componentType))
			{
				ac = new ArrayComponent
					(modifiers, componentType.getComponentType (), null);
			}
			int amods = modifiers;
			int dimensions = Reflection.getDimensionCount (atype)
				- Reflection.getDimensionCount (componentType);
			while (--dimensions >= 0)
			{
				ac = new ArrayComponent (amods, componentType, ac);
				amods = SCO;
				componentType = componentType.getArrayType ();
			}
			this.type = type;
			manageableType = (type instanceof ManageableType)
				? (ManageableType) type : null;
			typeId = type.getTypeId ();
			arrayComponent = ac;
			if (ac != null)
			{
				ac.arrayField = this;
			}
			if (type.getTypeId () != TypeId.OBJECT)
			{
				sco = false;
				fco = false;
				managed = false;
			}
			else
			{
				modifiers &= OBJECT_CLASS_MASK;
				sco = modifiers == SCO;
				fco = modifiers == FCO;
				managed = modifiers != UNMANAGED;
				assert sco || fco || !managed;
			}
			isArray = Reflection.isArray (componentType);
			usesList = isArray && !Reflection.isArray (type);
			isArrayComponent = this instanceof ArrayComponent;
		}


		public final Type getType ()
		{
			return type;
		}


		@Override
		public final Quantity getQuantity ()
		{
			return quantity;
		}


		public final void setQuantity (Quantity q)
		{
			this.quantity = q;
		}


		@Override
		public final Number getMinValue ()
		{
			return minValue;
		}


		public final void setMinValue (Number min)
		{
			minValue = min;
			updateType ();
		}


		@Override
		public final Number getMaxValue ()
		{
			return maxValue;
		}


		public final void setMaxValue (Number max)
		{
			maxValue = max;
			updateType ();
		}
		
		
		private void updateType ()
		{
			if ((minValue != null) && (maxValue != null))
			{
				replaceType (new BoundedType ("bounded", type.getTypeId (), minValue, maxValue));
			}
		}


		void replaceType (Type newType)
		{
			if (newType.getTypeId () != type.getTypeId ())
			{
				throw new IllegalArgumentException ();
			}
			this.type = newType;
			manageableType = (newType instanceof ManageableType)
				? (ManageableType) newType : null;
		}


		@Override
		public Field getLastField ()
		{
			return this;
		}


		public int length ()
		{
			return 1;
		}


		@Override
		public Field getSubfield (int index)
		{
			return this;
		}
		
		
		@Override
		PersistenceField getShallowSuperchain ()
		{
			return null;
		}

		
		@Override
		public PersistenceField getShallowSubchain (int begin)
		{
			return (begin == 0) ? this : null;
		}


		public IndirectField concat (PersistenceField field)
		{
			return new IndirectField (this).add (field);
		}


		public IndirectField cast (Type type)
		{
			return new IndirectField (this).cast (type);
		}


		public final int getFieldId ()
		{
			return fieldId;
		}


		public boolean isGetReturningCopy ()
		{
			return (modifiers & GETS_COPY) != 0;
		}


		public final boolean containsSCO ()
		{
			return sco;
		}


		public final ArrayComponent getArrayComponent ()
		{
			return arrayComponent;
		}


		/**
		 * Returns a field chain starting with this, followed
		 * by <code>dimensions</code> array components. 
		 * 
		 * @param dimensions the number of array components to append
		 * @return a field chain for an access of array components of this field
		 */
		public final IndirectField getArrayChain (int dimensions)
		{
			IndirectField i = new IndirectField (this);
			Field f = this;
			while (--dimensions >= 0)
			{
				f = f.arrayComponent;
				if (f == null)
				{
					throw new IllegalArgumentException ();
				}
				i.add (f);
			}
			return i;
		}

		
		public final boolean isArrayComponent ()
		{
			return isArrayComponent;
		}
		
		
		int getArrayLength (Object fieldValue)
		{
			if (usesList)
			{
				switch (arrayComponent.typeId)
				{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)

					case TypeId.$pp.TYPE:
						return (fieldValue instanceof $type[])
							? (($type[]) fieldValue).length
							: ((${pp.Type}List) fieldValue).size ();
#end
!!*/
//!! #* Start of generated code
// generated
// generated
					case TypeId.BOOLEAN:
						return (fieldValue instanceof boolean[])
							? ((boolean[]) fieldValue).length
							: ((BooleanList) fieldValue).size ();
// generated
// generated
					case TypeId.BYTE:
						return (fieldValue instanceof byte[])
							? ((byte[]) fieldValue).length
							: ((ByteList) fieldValue).size ();
// generated
// generated
					case TypeId.SHORT:
						return (fieldValue instanceof short[])
							? ((short[]) fieldValue).length
							: ((ShortList) fieldValue).size ();
// generated
// generated
					case TypeId.CHAR:
						return (fieldValue instanceof char[])
							? ((char[]) fieldValue).length
							: ((CharList) fieldValue).size ();
// generated
// generated
					case TypeId.INT:
						return (fieldValue instanceof int[])
							? ((int[]) fieldValue).length
							: ((IntList) fieldValue).size ();
// generated
// generated
					case TypeId.LONG:
						return (fieldValue instanceof long[])
							? ((long[]) fieldValue).length
							: ((LongList) fieldValue).size ();
// generated
// generated
					case TypeId.FLOAT:
						return (fieldValue instanceof float[])
							? ((float[]) fieldValue).length
							: ((FloatList) fieldValue).size ();
// generated
// generated
					case TypeId.DOUBLE:
						return (fieldValue instanceof double[])
							? ((double[]) fieldValue).length
							: ((DoubleList) fieldValue).size ();
//!! *# End of generated code
					case TypeId.OBJECT:
						return (fieldValue instanceof Object[])
							? ((Object[]) fieldValue).length
							: ((List) fieldValue).size ();
					default:
						throw new AssertionError ();
				}
			}
			else
			{
				return Array.getLength (fieldValue);
			}
		}
		
		
		Object getArray (Object fieldValue)
		{
			if (usesList)
			{
				switch (arrayComponent.typeId)
				{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)

					case TypeId.$pp.TYPE:
						return (fieldValue instanceof $type[]) ? fieldValue
							: ((${pp.Type}List) fieldValue).elements;
#end
!!*/
//!! #* Start of generated code
// generated
// generated
					case TypeId.BOOLEAN:
						return (fieldValue instanceof boolean[]) ? fieldValue
							: ((BooleanList) fieldValue).elements;
// generated
// generated
					case TypeId.BYTE:
						return (fieldValue instanceof byte[]) ? fieldValue
							: ((ByteList) fieldValue).elements;
// generated
// generated
					case TypeId.SHORT:
						return (fieldValue instanceof short[]) ? fieldValue
							: ((ShortList) fieldValue).elements;
// generated
// generated
					case TypeId.CHAR:
						return (fieldValue instanceof char[]) ? fieldValue
							: ((CharList) fieldValue).elements;
// generated
// generated
					case TypeId.INT:
						return (fieldValue instanceof int[]) ? fieldValue
							: ((IntList) fieldValue).elements;
// generated
// generated
					case TypeId.LONG:
						return (fieldValue instanceof long[]) ? fieldValue
							: ((LongList) fieldValue).elements;
// generated
// generated
					case TypeId.FLOAT:
						return (fieldValue instanceof float[]) ? fieldValue
							: ((FloatList) fieldValue).elements;
// generated
// generated
					case TypeId.DOUBLE:
						return (fieldValue instanceof double[]) ? fieldValue
							: ((DoubleList) fieldValue).elements;
//!! *# End of generated code
					default:
						throw new AssertionError ();
				}
			}
			else
			{
				return fieldValue;
			}
		}


		Object getArrayComponent (Object fieldValue, int index)
		{
			if (usesList && !(fieldValue instanceof Object[]))
			{
				List l = (List) fieldValue;
				return (index >= l.size ()) ? null : l.get (index);
			}
			else
			{
				return ((Object[]) fieldValue)[index];
			}
		}


		void setArrayComponent (Object fieldValue, int index, Object value)
		{
			if (usesList)
			{
				List l = (List) fieldValue;
				int n = l.size ();
				if (index < n)
				{
					l.set (index, value);
				}
				else
				{
					while (index >= ++n)
					{
						l.add (null);
					}
					l.add (value);
				}
			}
			else
			{
				((Object[]) fieldValue)[index] = value;
			}
		}


		public final int getSerializationMethod ()
		{
			if (serializationMethod < 0)
			{
				serializationMethod = (manageableType != null)
					? manageableType.getSerializationMethod ()
					: LIST_SERIALIZATION;
			}
			return serializationMethod;
		}


		public boolean overlaps (int[] tindices, FieldChain field,
								 int[] findices)
		{
			return (this == field)
				|| ((field.length () > 1) ? field.overlaps (findices, this, tindices)
					: ((field.getField (0) == this)
					   || (!(field instanceof IndirectField)
						   && Reflection.membersEqual (this, field.getField (0), false))));
		}


		@Override
		void write (int[] indices, XAQueue out)
		{
			out.writeInt (fieldId + (1 << CHAIN_LENGTH_BIT));
		}


		Object setList (Object list, Object value)
		{
			if (value == null)
			{
				return null;
			}
			switch (arrayComponent.typeId)
			{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)

				case TypeId.$pp.TYPE:
					if (value instanceof $type[])
					{
						if (list == null)
						{
							return new ${pp.Type}List (($type[]) value);
						}
						((${pp.Type}List) list).clear ();
						((${pp.Type}List) list).addAll (($type[]) value, 0, (($type[]) value).length);
						return list;
					}
					break;
#end
!!*/
//!! #* Start of generated code
// generated
// generated
				case TypeId.BOOLEAN:
					if (value instanceof boolean[])
					{
						if (list == null)
						{
							return new BooleanList ((boolean[]) value);
						}
						((BooleanList) list).clear ();
						((BooleanList) list).addAll ((boolean[]) value, 0, ((boolean[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.BYTE:
					if (value instanceof byte[])
					{
						if (list == null)
						{
							return new ByteList ((byte[]) value);
						}
						((ByteList) list).clear ();
						((ByteList) list).addAll ((byte[]) value, 0, ((byte[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.SHORT:
					if (value instanceof short[])
					{
						if (list == null)
						{
							return new ShortList ((short[]) value);
						}
						((ShortList) list).clear ();
						((ShortList) list).addAll ((short[]) value, 0, ((short[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.CHAR:
					if (value instanceof char[])
					{
						if (list == null)
						{
							return new CharList ((char[]) value);
						}
						((CharList) list).clear ();
						((CharList) list).addAll ((char[]) value, 0, ((char[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.INT:
					if (value instanceof int[])
					{
						if (list == null)
						{
							return new IntList ((int[]) value);
						}
						((IntList) list).clear ();
						((IntList) list).addAll ((int[]) value, 0, ((int[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.LONG:
					if (value instanceof long[])
					{
						if (list == null)
						{
							return new LongList ((long[]) value);
						}
						((LongList) list).clear ();
						((LongList) list).addAll ((long[]) value, 0, ((long[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.FLOAT:
					if (value instanceof float[])
					{
						if (list == null)
						{
							return new FloatList ((float[]) value);
						}
						((FloatList) list).clear ();
						((FloatList) list).addAll ((float[]) value, 0, ((float[]) value).length);
						return list;
					}
					break;
// generated
// generated
				case TypeId.DOUBLE:
					if (value instanceof double[])
					{
						if (list == null)
						{
							return new DoubleList ((double[]) value);
						}
						((DoubleList) list).clear ();
						((DoubleList) list).addAll ((double[]) value, 0, ((double[]) value).length);
						return list;
					}
					break;
//!! *# End of generated code
				case TypeId.OBJECT:
					if (value instanceof Object[])
					{
						List l = (List) list;
						if (list == null)
						{
							try
							{
								l = (List) getType ().newInstance ();
							}
							catch (Exception e)
							{
								throw new WrapException (e);
							}
						}
						else
						{
							l.clear ();
						}
						Object[] a = (Object[]) value;
						for (int i = 0; i < a.length; i++)
						{
							l.add (a[i]);
						}
						return l;
					}
					break;
			}
			return value;
		}

		
		public final void setObject (Object object, Object value)
		{
			boolean ref = !isGetReturningCopy () && (object instanceof SharedObjectReference);
			if (ref)
			{
				Object old = getObject (object);
				if (old == value)
				{
					return;
				}
				if ((old instanceof Shareable)
					&& (((getModifiers () & DEFINES_SHARED) != 0)
						|| (((Shareable) old).getProvider () != null)))
				{
					((Shareable) old).removeReference ((SharedObjectReference) object);
				}
			}
			setObjectImpl (object, value);
			if (ref && (value instanceof Shareable)
				&& (((getModifiers () & DEFINES_SHARED) != 0)
					|| (((Shareable) value).getProvider () != null)))
			{
				((Shareable) value).addReference ((SharedObjectReference) object);
			}
		}
		
/*!!
#foreach ($type in $types)
$pp.setType($type)

		@Override
		public void set$pp.Type (Object o, int[] indices,
								 $type value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
#if ($pp.Object)
			if ((pm != null) & fco)
			{
				pm.makePersistent (value, t);
			}
#end
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSet$pp.Type ((PersistenceCapable) o, this, indices, get$pp.Type (o, indices), value);
			}
		setField:
			{
#if ($pp.Object)
				if (usesList)
				{
					Object list = getObject (o);
					value = setList (list, value);
					if ((list == value) && !isGetReturningCopy ())
					{
						break setField;
					}
				}
#end
				set$pp.Type (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}


		@Override
		$type readAndSet$pp.Type (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
#if ($pp.Object)
			try
			{
				return readObject (o, -1, reader);
			}
			catch (IOException e)
			{
				throw new FatalPersistenceException (e);
			}
#else
			$type v = reader.read$pp.Type ();
			set$pp.Type (o, v);
			return v;
#end
		}


		@Override
		public $type get$pp.Type (Object o, int[] indices)
		{
			return get$pp.Type (o);
		}


		public $type get$pp.Type (Object object)
		{
			throw new AssertionError ();
		}


#if ($pp.Object)
		protected void setObjectImpl (Object object, $type value)
#else
		public void set$pp.Type (Object object, $type value)
#end
		{
			throw new AssertionError ();
		}


		@Override
		public void insert$pp.Type (Object o, int[] indices,
									$type value, Transaction t)
		{
			throw new AssertionError ();
		}


		@Override
		public void remove$pp.Type (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}


		@Override
		void readAndInsert$pp.Type (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
		@Override
		public void setBoolean (Object o, int[] indices,
								 boolean value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetBoolean ((PersistenceCapable) o, this, indices, getBoolean (o, indices), value);
			}
		setField:
			{
				setBoolean (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		boolean readAndSetBoolean (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			boolean v = reader.readBoolean ();
			setBoolean (o, v);
			return v;
		}
// generated
// generated
		@Override
		public boolean getBoolean (Object o, int[] indices)
		{
			return getBoolean (o);
		}
// generated
// generated
		public boolean getBoolean (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setBoolean (Object object, boolean value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertBoolean (Object o, int[] indices,
									boolean value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeBoolean (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertBoolean (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setByte (Object o, int[] indices,
								 byte value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetByte ((PersistenceCapable) o, this, indices, getByte (o, indices), value);
			}
		setField:
			{
				setByte (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		byte readAndSetByte (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			byte v = reader.readByte ();
			setByte (o, v);
			return v;
		}
// generated
// generated
		@Override
		public byte getByte (Object o, int[] indices)
		{
			return getByte (o);
		}
// generated
// generated
		public byte getByte (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setByte (Object object, byte value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertByte (Object o, int[] indices,
									byte value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeByte (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertByte (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setShort (Object o, int[] indices,
								 short value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetShort ((PersistenceCapable) o, this, indices, getShort (o, indices), value);
			}
		setField:
			{
				setShort (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		short readAndSetShort (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			short v = reader.readShort ();
			setShort (o, v);
			return v;
		}
// generated
// generated
		@Override
		public short getShort (Object o, int[] indices)
		{
			return getShort (o);
		}
// generated
// generated
		public short getShort (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setShort (Object object, short value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertShort (Object o, int[] indices,
									short value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeShort (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertShort (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setChar (Object o, int[] indices,
								 char value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetChar ((PersistenceCapable) o, this, indices, getChar (o, indices), value);
			}
		setField:
			{
				setChar (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		char readAndSetChar (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			char v = reader.readChar ();
			setChar (o, v);
			return v;
		}
// generated
// generated
		@Override
		public char getChar (Object o, int[] indices)
		{
			return getChar (o);
		}
// generated
// generated
		public char getChar (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setChar (Object object, char value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertChar (Object o, int[] indices,
									char value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeChar (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertChar (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setInt (Object o, int[] indices,
								 int value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetInt ((PersistenceCapable) o, this, indices, getInt (o, indices), value);
			}
		setField:
			{
				setInt (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		int readAndSetInt (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			int v = reader.readInt ();
			setInt (o, v);
			return v;
		}
// generated
// generated
		@Override
		public int getInt (Object o, int[] indices)
		{
			return getInt (o);
		}
// generated
// generated
		public int getInt (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setInt (Object object, int value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertInt (Object o, int[] indices,
									int value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeInt (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertInt (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setLong (Object o, int[] indices,
								 long value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetLong ((PersistenceCapable) o, this, indices, getLong (o, indices), value);
			}
		setField:
			{
				setLong (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		long readAndSetLong (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			long v = reader.readLong ();
			setLong (o, v);
			return v;
		}
// generated
// generated
		@Override
		public long getLong (Object o, int[] indices)
		{
			return getLong (o);
		}
// generated
// generated
		public long getLong (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setLong (Object object, long value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertLong (Object o, int[] indices,
									long value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeLong (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertLong (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setFloat (Object o, int[] indices,
								 float value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetFloat ((PersistenceCapable) o, this, indices, getFloat (o, indices), value);
			}
		setField:
			{
				setFloat (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		float readAndSetFloat (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			float v = reader.readFloat ();
			setFloat (o, v);
			return v;
		}
// generated
// generated
		@Override
		public float getFloat (Object o, int[] indices)
		{
			return getFloat (o);
		}
// generated
// generated
		public float getFloat (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setFloat (Object object, float value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertFloat (Object o, int[] indices,
									float value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeFloat (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertFloat (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setDouble (Object o, int[] indices,
								 double value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetDouble ((PersistenceCapable) o, this, indices, getDouble (o, indices), value);
			}
		setField:
			{
				setDouble (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		double readAndSetDouble (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			double v = reader.readDouble ();
			setDouble (o, v);
			return v;
		}
// generated
// generated
		@Override
		public double getDouble (Object o, int[] indices)
		{
			return getDouble (o);
		}
// generated
// generated
		public double getDouble (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		public void setDouble (Object object, double value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertDouble (Object o, int[] indices,
									double value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeDouble (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertDouble (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
// generated
// generated
		@Override
		public void setObject (Object o, int[] indices,
								 Object value, Transaction t)
		{
			PersistenceManager pm = (o instanceof PersistenceCapable)
				? ((PersistenceCapable) o).getPersistenceManager () : null;
			if ((pm != null) & fco)
			{
				pm.makePersistent (value, t);
			}
			if ((t != null) && (pm != null))
			{
				t.makeActive ()
					.logSetObject ((PersistenceCapable) o, this, indices, getObject (o, indices), value);
			}
		setField:
			{
				if (usesList)
				{
					Object list = getObject (o);
					value = setList (list, value);
					if ((list == value) && !isGetReturningCopy ())
					{
						break setField;
					}
				}
				setObject (o, value);
			}
			if (o instanceof Manageable)
			{
				((Manageable) o).fieldModified (this, indices, t);
			}
		}
// generated
// generated
		@Override
		Object readAndSetObject (PersistenceCapable o, int[] indices,
								  XAQueue.Reader reader)
		{
			try
			{
				return readObject (o, -1, reader);
			}
			catch (IOException e)
			{
				throw new FatalPersistenceException (e);
			}
		}
// generated
// generated
		@Override
		public Object getObject (Object o, int[] indices)
		{
			return getObject (o);
		}
// generated
// generated
		public Object getObject (Object object)
		{
			throw new AssertionError ();
		}
// generated
// generated
		protected void setObjectImpl (Object object, Object value)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void insertObject (Object o, int[] indices,
									Object value, Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		public void removeObject (Object o, int[] indices,
									Transaction t)
		{
			throw new AssertionError ();
		}
// generated
// generated
		@Override
		void readAndInsertObject (PersistenceCapable o, int[] indices,
									XAQueue.Reader reader)
		{
			throw new AssertionError ();
		}
// generated
//!! *# End of generated code


		final void mergeModifiers (Member m)
		{
			modifiers = (modifiers & ~JAVA_MODIFIERS)
				| (m.getModifiers () & JAVA_MODIFIERS);
		}


		@Override
		protected void writeObject (Object value, PersistenceOutput out)
			throws IOException
		{
			ManageableType.write (this, value, out, false);
		}


		protected Object readObject (Object container, int index,
									 PersistenceInput in)
			throws IOException
		{
			return ManageableType.read (this, container, index, in, false);
		}


		@Override
		public String toString ()
		{
			return de.grogra.reflect.Reflection.toString (this);
		}


		public final Object getCloned (Object object)
			throws CloneNotSupportedException
		{
			Object v = getObject (object);
			if (v != null)
			{
				if (v instanceof Object[])
				{
					v = ((Object[]) v).clone ();
					Object[] a = (Object[]) v;
					Type t = isArray ? arrayComponent.getType () : null;
					for (int j = a.length - 1; j >= 0; j--)
					{
						a[j] = cloneObject (t, a[j], true, false);
					}
				}
				else if (v instanceof List)
				{
					List v2;
					try
					{
						v2 = (List) v.getClass ().newInstance ();
					}
					catch (Exception e)
					{
						throw (CloneNotSupportedException)
							new CloneNotSupportedException ().initCause (e);
					}
					Type t = isArray ? arrayComponent.getType () : null;
					for (int j = 0, n = ((List) v).size (); j < n; j++)
					{
						v2.add (cloneObject (t, ((List) v).get (j), true, false));
					}
					v = v2;
				}
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
				else if (v instanceof $type[])
				{
					v = (($type[]) v).clone ();
				}
				else if (v instanceof ${pp.Type}List)
				{
					v = ((${pp.Type}List) v).clone ();
				}
#end
!!*/
//!! #* Start of generated code
// generated
				else if (v instanceof boolean[])
				{
					v = ((boolean[]) v).clone ();
				}
				else if (v instanceof BooleanList)
				{
					v = ((BooleanList) v).clone ();
				}
// generated
				else if (v instanceof byte[])
				{
					v = ((byte[]) v).clone ();
				}
				else if (v instanceof ByteList)
				{
					v = ((ByteList) v).clone ();
				}
// generated
				else if (v instanceof short[])
				{
					v = ((short[]) v).clone ();
				}
				else if (v instanceof ShortList)
				{
					v = ((ShortList) v).clone ();
				}
// generated
				else if (v instanceof char[])
				{
					v = ((char[]) v).clone ();
				}
				else if (v instanceof CharList)
				{
					v = ((CharList) v).clone ();
				}
// generated
				else if (v instanceof int[])
				{
					v = ((int[]) v).clone ();
				}
				else if (v instanceof IntList)
				{
					v = ((IntList) v).clone ();
				}
// generated
				else if (v instanceof long[])
				{
					v = ((long[]) v).clone ();
				}
				else if (v instanceof LongList)
				{
					v = ((LongList) v).clone ();
				}
// generated
				else if (v instanceof float[])
				{
					v = ((float[]) v).clone ();
				}
				else if (v instanceof FloatList)
				{
					v = ((FloatList) v).clone ();
				}
// generated
				else if (v instanceof double[])
				{
					v = ((double[]) v).clone ();
				}
				else if (v instanceof DoubleList)
				{
					v = ((DoubleList) v).clone ();
				}
//!! *# End of generated code
				else
				{
					v = cloneObject (getType (), v, true, false);
				}
			}
			return v;
		}
	}


	public final class ArrayComponent extends Field
	{
		ArrayComponent (int modifiers, Type type, ArrayComponent ac)
		{
			super ("[.]", modifiers, type, null, ac);
		}

		@Override
		public boolean overlaps (int[] tindices, FieldChain field,
								 int[] findices)
		{
			throw new AssertionError ();
		}

		@Override
		void write (int[] indices, XAQueue out)
		{
			throw new AssertionError ();
		}
	}


	public static final int LIST_SERIALIZATION = 0;
	public static final int FIELD_NAME_SERIALIZATION = 1;
	public static final int FIELD_NODE_SERIALIZATION = 2;
	
	static final Hashtable nameToType = new Hashtable ();

	protected boolean finished = false;

	private ObjectList declaredManagedFields = null;
	int managedFieldCount, fcoFieldCount;
	Field[] managedFields, fcoFields;

	private final ManageableType supertype;
	private final Type superclass;

	private int serializationMethod = -1;
	private final boolean writeOverridden;
	private boolean serializationMethodSet = false;
	private boolean serializable;

	private static final Class[] writeParameters
		= {Object.class, PersistenceOutput.class, boolean.class}; 


	protected ManageableType (Type type, ManageableType supertype,
							  boolean register)
	{
		super (type);
		this.supertype = supertype;
		superclass = ((type == null)
					  || Reflection.equal (type.getSupertype (), supertype))
			? supertype : type.getSupertype ();
		if (supertype == null)
		{
			managedFieldCount = 0;
			fcoFieldCount = 0;
			serializable = true;
		}
		else
		{
			managedFieldCount = supertype.managedFieldCount;
			fcoFieldCount = supertype.fcoFieldCount;
			serializable = supertype.serializable;
		}
		Class c = getWriteImplementingClass ();
		assert c != null;
		writeOverridden = c != ManageableType.class;
		if (register && (type != null))
		{
			nameToType.put (type.getBinaryName (), this);
		}
	}


	private Class getWriteImplementingClass ()
	{
		Class c;
		for (c = getClass (); c != null; c = c.getSuperclass ())
		{
			try
			{
				c.getDeclaredMethod ("write", writeParameters);
				break;
			}
			catch (NoSuchMethodException e)
			{
			}
		}
		return c;
	}


	@Override
	public final Type getSupertype ()
	{
		return superclass;
	}


	public final ManageableType getManageableSupertype ()
	{
		return supertype;
	}


	public final boolean isAssignableFrom (Type t)
	{
		return Reflection.isAssignableFrom (this, t);
	}


	public final void setSerializationMethod (int serializationMethod)
	{
		serializationMethodSet = true;
		this.serializationMethod = serializationMethod;
	}


	public final void setSerializable (boolean serializable)
	{
		if (finished)
		{
			throw new IllegalStateException ();
		}
		this.serializable = serializable;
	}


	public final boolean isSerializable ()
	{
		return serializable;
	}


	private boolean serMethodSet ()
	{
		return serializationMethodSet
			|| ((supertype != null) && supertype.serMethodSet ());
	}


	public final int getSerializationMethod ()
	{
		if (writeOverridden)
		{
			return LIST_SERIALIZATION;
		}
		if (serializationMethod < 0)
		{
			if ((supertype != null) && supertype.serMethodSet ())
			{
				serializationMethod = supertype.getSerializationMethod ();
			}
			else
			{
				serializationMethod = FIELD_NAME_SERIALIZATION;
				for (int i = managedFieldCount - 1; i >= 0; i--)
				{
					Type t = managedFields[i].getType ();
					if ((t instanceof ManageableType) || Reflection.isArray (t))
					{
						serializationMethod = FIELD_NODE_SERIALIZATION;
						break;
					}
				}
			}
		}
		return serializationMethod;
	}


	public final void addManagedField (Field field)
	{
		if (finished)
		{
			throw new IllegalStateException
				("Can't add a field to a class which has been validated.");
		}
		String d = field.getDescriptor ();
		if (field.fco)
		{
			fcoFieldCount++;
		}
		field.fieldId = managedFieldCount++;
		for (int i = getDeclaredFieldCount () - 1; i >= 0; i--)
		{
			de.grogra.reflect.Field f = getDeclaredField (i);
			if (d.equals (f.getDescriptor ()))
			{
				field.mergeModifiers (f);
				break;
			}
		}
		if (declaredManagedFields == null)
		{
			declaredManagedFields = new ObjectList ();
		}
		declaredManagedFields.add (field);
	}


	public final int getManagedFieldCount ()
	{
		return managedFieldCount;
	}


	public final Field getManagedField (int fieldId)
	{
		return (fieldId < managedFieldCount) ? managedFields[fieldId] : null;
	}


	public final Field getManagedField (String name)
	{
		for (int i = managedFieldCount - 1; i >= 0; i--)
		{
			if (managedFields[i] == null)
			{
				name.getClass ();
			}
			if (name.equals (managedFields[i].getSimpleName ()))
			{
				return managedFields[i];
			}
		}
		return null;
	}


	public PersistenceField resolveAliasField (String name)
	{
		return (supertype != null) ? supertype.resolveAliasField (name) : null;
	}


	public final Field[] getFCOFields ()
	{
		return fcoFields;
	}


	public Object setObject (Object placeIn, Object value)
	{
		return value;
	}

	
	public abstract Object getRepresentative ();

	public ManageableType validate ()
	{
		finished = true;
		managedFields = new Field[managedFieldCount];
		fcoFields = new Field[fcoFieldCount];
		int i, j;
		if (supertype != null)
		{
			Field[] a = supertype.managedFields;
			i = a.length;
			System.arraycopy (a, 0, managedFields, 0, i);
			a = supertype.fcoFields;
			j = a.length;
			System.arraycopy (a, 0, fcoFields, 0, j);
		}
		else
		{
			i = 0;
			j = 0;
		}
		if (declaredManagedFields != null)
		{
			int n = i;
			while (!declaredManagedFields.isEmpty ())
			{
				Field f = (Field) declaredManagedFields.pop ();
				assert (f.fieldId >= n)
					&& (managedFields[f.fieldId] == null);
				managedFields[f.fieldId] = f;
				i++;
				if (f.fco)
				{
					fcoFields[j++] = f;
				}
			}
			declaredManagedFields = null;
		}
		assert (i == managedFieldCount) && (j == fcoFieldCount);
		return this;
	}


	protected final void replaceType (Field field, Type newType)
	{
		if (field.getDeclaringType () != this)
		{
			throw new IllegalArgumentException ();
		}
		field.replaceType (newType);
	}


	static final void write (Field field, Object value,
							 PersistenceOutput out, boolean nested)
		throws IOException
	{
		if (value == null)
		{
			out.writeNullObject ();
		}
		else if (field.isArray)
		{
			int n = field.getArrayLength (value);
			out.beginArray (n, field.arrayComponent.getType ());
			if (!nested)
			{
				out.setNested (true);
			}
		writeComponents:
			{
				Type t;
				Field f = field.arrayComponent;
				if (f.isArray)
				{
					for (int i = 0; i < n; i++)
					{
						write (f, field.getArrayComponent (value, i), out, true);
					}
					break writeComponents;
				}
				t = f.getType ();
				switch (t.getTypeId ())
				{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)

					case TypeId.$pp.TYPE:
					{
						$type[] a = ($type[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.write$pp.Type (a[i]);
						}
						break;
					}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
					case TypeId.BOOLEAN:
					{
						boolean[] a = (boolean[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeBoolean (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.BYTE:
					{
						byte[] a = (byte[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeByte (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.SHORT:
					{
						short[] a = (short[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeShort (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.CHAR:
					{
						char[] a = (char[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeChar (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.INT:
					{
						int[] a = (int[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeInt (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.LONG:
					{
						long[] a = (long[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeLong (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.FLOAT:
					{
						float[] a = (float[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeFloat (a[i]);
						}
						break;
					}
// generated
// generated
					case TypeId.DOUBLE:
					{
						double[] a = (double[]) field.getArray (value);
						for (int i = 0; i < n; i++)
						{
							out.writeDouble (a[i]);
						}
						break;
					}
//!! *# End of generated code
					case TypeId.OBJECT:
					{
						for (int i = 0; i < n; i++)
						{
							write (t, field.getArrayComponent (value, i), out, true, f.managed);
						}
						break;
					}
				}
			}
			if (!nested)
			{
				out.setNested (false);
			}
			out.endArray ();
		}
		else if ((field.getModifiers () & Field.DEFINES_SHARED) != 0)
		{
			if ((value instanceof PersistenceCapable)
				&& (((PersistenceCapable) value).getPersistenceManager ()
					!= null))
			{
				out.writePersistentObjectReference ((PersistenceCapable) value);
			}
			else
			{
				ManageableType mt = ((Manageable) value).getManageableType ();
				writeManaged (value, mt, mt != field.getType (), out, nested);
			}
		}
		else
		{
			write (field.getType (), value, out, nested, field.managed);
		}
	}


	private static void writeManaged (Object o, ManageableType type, boolean writeType,
									  PersistenceOutput out, boolean nested) throws IOException
	{
		boolean diff = out.beginManaged (type, writeType);
		if (!nested)
		{
			out.setNested (true);
		}
		type.write (o, out, diff);
		if (!nested)
		{
			out.setNested (false);
		}
		out.endManaged (o, writeType);
	}


	private static void write (Type type, Object value, PersistenceOutput out,
							   boolean nested, boolean managed)
		throws IOException
	{
		if (value instanceof Manageable)
		{
			Object v = ((Manageable) value).manageableWriteReplace ();
			if ((v != value) && ((v == null) || type.isInstance (v)))
			{
				value = v;
			}
		}
		if (value == null)
		{
			out.writeNullObject ();
		}
		else if ((value instanceof Shareable)
				 && (((Shareable) value).getProvider () != null))
		{
			out.writeSharedObjectReference ((Shareable) value);
		}
		else if (value instanceof PersistenceCapable)
		{
			PersistenceCapable o = (PersistenceCapable) value;
			if (o.getPersistenceManager () != null)
			{
				out.writePersistentObjectReference (o);
			}
			else if (managed)
			{
				writeManaged (o, o.getManageableType (), true, out, nested);
			}
			else
			{
				out.writeObject (value, type);
			}
		}
		else if (value instanceof Manageable)
		{
			ManageableType mt = ((Manageable) value).getManageableType ();
			writeManaged (value, mt, !managed || (mt != type), out, nested);
		}
		else if (managed && (type instanceof ManageableType))
		{
			writeManaged (value, (ManageableType) type, !managed, out, nested);
		}
		else if (value instanceof String)
		{
			out.writeStringObject ((String) value);
		}
		else
		{
			ManageableType mt;
			if ((mt = forName (value.getClass ().getName ())) != null)
			{
				writeManaged (value, mt, true, out, nested);
			}
			else
			{
				out.writeObject (value, type);
			}
		}
	}


	protected void write (Object value, PersistenceOutput out,
						  boolean onlyDiff)
		throws IOException
	{
		out.beginFields ();
		Object diff = onlyDiff ? getRepresentative () : null;
		for (int i = 0; i < managedFieldCount; i++)
		{
			Field f = managedFields[i];
			switch (f.getType ().getTypeId ())
			{
/*!!
#foreach ($type in $types)
$pp.setType($type)
				case TypeId.$pp.TYPE:
				{
					$type v = f.get$pp.Type (value);
					if ((diff != null)
						&& (v == f.get$pp.Type (diff))) 
					{
						continue;
					}
					out.beginField (f);
#if ($pp.Object)
					write (f, v, out, true);
#else
					out.write$pp.Type (v);
#end
					break;
				}
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
				{
					boolean v = f.getBoolean (value);
					if ((diff != null)
						&& (v == f.getBoolean (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeBoolean (v);
					break;
				}
// generated
				case TypeId.BYTE:
				{
					byte v = f.getByte (value);
					if ((diff != null)
						&& (v == f.getByte (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeByte (v);
					break;
				}
// generated
				case TypeId.SHORT:
				{
					short v = f.getShort (value);
					if ((diff != null)
						&& (v == f.getShort (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeShort (v);
					break;
				}
// generated
				case TypeId.CHAR:
				{
					char v = f.getChar (value);
					if ((diff != null)
						&& (v == f.getChar (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeChar (v);
					break;
				}
// generated
				case TypeId.INT:
				{
					int v = f.getInt (value);
					if ((diff != null)
						&& (v == f.getInt (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeInt (v);
					break;
				}
// generated
				case TypeId.LONG:
				{
					long v = f.getLong (value);
					if ((diff != null)
						&& (v == f.getLong (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeLong (v);
					break;
				}
// generated
				case TypeId.FLOAT:
				{
					float v = f.getFloat (value);
					if ((diff != null)
						&& (v == f.getFloat (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeFloat (v);
					break;
				}
// generated
				case TypeId.DOUBLE:
				{
					double v = f.getDouble (value);
					if ((diff != null)
						&& (v == f.getDouble (diff))) 
					{
						continue;
					}
					out.beginField (f);
					out.writeDouble (v);
					break;
				}
// generated
				case TypeId.OBJECT:
				{
					Object v = f.getObject (value);
					if ((diff != null)
						&& (v == f.getObject (diff))) 
					{
						continue;
					}
					out.beginField (f);
					write (f, v, out, true);
					break;
				}
//!! *# End of generated code
			}
			out.endField (f);
		}
		out.endFields ();
	}


	protected Object readObject (PersistenceInput in, Object placeIn,
								 boolean fieldsProvided)
		throws IOException
	{
		if ((placeIn == null) || (getImplementationClass () != placeIn.getClass ())
			|| ((placeIn instanceof Shareable)
				&& (((Shareable) placeIn).getProvider () != null)))
		{
			try
			{
				placeIn = newInstance ();
			}
			catch (IllegalAccessException e)
			{
				throw new IOWrapException (Utils.unwrapFully (e));
			}
			catch (InstantiationException e)
			{
				throw new IOWrapException (Utils.unwrapFully (e));
			}
			catch (InvocationTargetException e)
			{
				throw new IOWrapException (Utils.unwrapFully (e));
			}
		}
		readManaged (this, placeIn, in, fieldsProvided);
		return (placeIn instanceof Manageable) ? ((Manageable) placeIn).manageableReadResolve ()
			: placeIn;
	}


	private static Object getPlaceIn (Field field, ManageableType type,
									  Object container, int index, int length)
		throws IOException
	{
		Object o = (container == null) ? null : field.isArrayComponent
			? field.arrayField.getArrayComponent (container, index) : field.getObject (container);
		Object before = o;
		if (field.usesList)
		{
			switch (field.arrayComponent.typeId)
			{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
				case TypeId.$pp.TYPE:
					if (o == null)
					{
						o = new ${pp.Type}List (length);
					}
					else
					{
						((${pp.Type}List) o).clear ();
					}
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					if (o == null)
					{
						o = new BooleanList (length);
					}
					else
					{
						((BooleanList) o).clear ();
					}
					break;
// generated
				case TypeId.BYTE:
					if (o == null)
					{
						o = new ByteList (length);
					}
					else
					{
						((ByteList) o).clear ();
					}
					break;
// generated
				case TypeId.SHORT:
					if (o == null)
					{
						o = new ShortList (length);
					}
					else
					{
						((ShortList) o).clear ();
					}
					break;
// generated
				case TypeId.CHAR:
					if (o == null)
					{
						o = new CharList (length);
					}
					else
					{
						((CharList) o).clear ();
					}
					break;
// generated
				case TypeId.INT:
					if (o == null)
					{
						o = new IntList (length);
					}
					else
					{
						((IntList) o).clear ();
					}
					break;
// generated
				case TypeId.LONG:
					if (o == null)
					{
						o = new LongList (length);
					}
					else
					{
						((LongList) o).clear ();
					}
					break;
// generated
				case TypeId.FLOAT:
					if (o == null)
					{
						o = new FloatList (length);
					}
					else
					{
						((FloatList) o).clear ();
					}
					break;
// generated
				case TypeId.DOUBLE:
					if (o == null)
					{
						o = new DoubleList (length);
					}
					else
					{
						((DoubleList) o).clear ();
					}
					break;
//!! *# End of generated code
				case TypeId.OBJECT:
					if (o == null)
					{
						try
						{
							o = field.getType ().newInstance ();
						}
						catch (Exception e)
						{
							throw new IOWrapException (Utils.unwrapFully (e));
						}
					}
					else
					{
						((List) o).clear ();
					}
					break;
			}
		}
		else if (field.isArray)
		{
			switch (field.arrayComponent.typeId)
			{
/*!!
#foreach ($type in $types)
$pp.setType($type)
				case TypeId.$pp.TYPE:
					if ((o == null) || ((length >= 0)
										&& ((($type[]) o).length != length)))
					{
#if ($pp.Object)
						o = field.getType ()
							.createArray ((length > 0) ? length : 0);
#else
						o = (length > 0) ? new $type[length]
							: Utils.${pp.TYPE}_0;
#end
					}
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					if ((o == null) || ((length >= 0)
										&& (((boolean[]) o).length != length)))
					{
						o = (length > 0) ? new boolean[length]
							: Utils.BOOLEAN_0;
					}
					break;
// generated
				case TypeId.BYTE:
					if ((o == null) || ((length >= 0)
										&& (((byte[]) o).length != length)))
					{
						o = (length > 0) ? new byte[length]
							: Utils.BYTE_0;
					}
					break;
// generated
				case TypeId.SHORT:
					if ((o == null) || ((length >= 0)
										&& (((short[]) o).length != length)))
					{
						o = (length > 0) ? new short[length]
							: Utils.SHORT_0;
					}
					break;
// generated
				case TypeId.CHAR:
					if ((o == null) || ((length >= 0)
										&& (((char[]) o).length != length)))
					{
						o = (length > 0) ? new char[length]
							: Utils.CHAR_0;
					}
					break;
// generated
				case TypeId.INT:
					if ((o == null) || ((length >= 0)
										&& (((int[]) o).length != length)))
					{
						o = (length > 0) ? new int[length]
							: Utils.INT_0;
					}
					break;
// generated
				case TypeId.LONG:
					if ((o == null) || ((length >= 0)
										&& (((long[]) o).length != length)))
					{
						o = (length > 0) ? new long[length]
							: Utils.LONG_0;
					}
					break;
// generated
				case TypeId.FLOAT:
					if ((o == null) || ((length >= 0)
										&& (((float[]) o).length != length)))
					{
						o = (length > 0) ? new float[length]
							: Utils.FLOAT_0;
					}
					break;
// generated
				case TypeId.DOUBLE:
					if ((o == null) || ((length >= 0)
										&& (((double[]) o).length != length)))
					{
						o = (length > 0) ? new double[length]
							: Utils.DOUBLE_0;
					}
					break;
// generated
				case TypeId.OBJECT:
					if ((o == null) || ((length >= 0)
										&& (((Object[]) o).length != length)))
					{
						o = field.getType ()
							.createArray ((length > 0) ? length : 0);
					}
					break;
//!! *# End of generated code
				default:
					throw new AssertionError ();
			}
		}
		else if ((o == null) || ((o instanceof Shareable)
								 && (((Shareable) o).getProvider () != null)))
		{
			try
			{
				o = ((type != null) ? type : field.getType ()).newInstance ();
			}
			catch (IllegalAccessException e)
			{
				throw new IOWrapException (Utils.unwrapFully (e), type + " " + field.toString ());
			}
			catch (InstantiationException e)
			{
				throw new IOWrapException (Utils.unwrapFully (e), type + " " + field.toString ());
			}
			catch (InvocationTargetException e)
			{
				throw new IOWrapException (Utils.unwrapFully (e), type + " " + field.toString ());
			}
		}
		if ((o != before) && (container != null))
		{
			if (field.isArrayComponent)
			{
				field.arrayField.setArrayComponent (container, index, o);
			}
			else
			{
				field.setObject (container, o);
			}
		}
		return o;
	}


	private static void readManaged (ManageableType type, Object value,
									 PersistenceInput in,
									 boolean fieldsProvided)
		throws IOException
	{
		if (fieldsProvided)
		{
			Field f;
			while ((f = in.beginField (type, null)) != null)
			{
				switch (f.getType ().getTypeId ())
				{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
					case TypeId.$pp.TYPE:
						f.set$pp.Type (value, in.read$pp.Type ());
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.BOOLEAN:
						f.setBoolean (value, in.readBoolean ());
						break;
// generated
					case TypeId.BYTE:
						f.setByte (value, in.readByte ());
						break;
// generated
					case TypeId.SHORT:
						f.setShort (value, in.readShort ());
						break;
// generated
					case TypeId.CHAR:
						f.setChar (value, in.readChar ());
						break;
// generated
					case TypeId.INT:
						f.setInt (value, in.readInt ());
						break;
// generated
					case TypeId.LONG:
						f.setLong (value, in.readLong ());
						break;
// generated
					case TypeId.FLOAT:
						f.setFloat (value, in.readFloat ());
						break;
// generated
					case TypeId.DOUBLE:
						f.setDouble (value, in.readDouble ());
						break;
//!! *# End of generated code
					case TypeId.OBJECT:
						read (f, value, 0, in, true);
						break;
				}
				in.endField ();
			}
		}
		else
		{
			for (int i = 0; i < type.managedFieldCount; i++)
			{
				Field f;
				in.beginField (type, f = type.managedFields[i]);
				switch (f.getType ().getTypeId ())
				{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
					case TypeId.$pp.TYPE:
						f.set$pp.Type (value, in.read$pp.Type ());
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.BOOLEAN:
						f.setBoolean (value, in.readBoolean ());
						break;
// generated
					case TypeId.BYTE:
						f.setByte (value, in.readByte ());
						break;
// generated
					case TypeId.SHORT:
						f.setShort (value, in.readShort ());
						break;
// generated
					case TypeId.CHAR:
						f.setChar (value, in.readChar ());
						break;
// generated
					case TypeId.INT:
						f.setInt (value, in.readInt ());
						break;
// generated
					case TypeId.LONG:
						f.setLong (value, in.readLong ());
						break;
// generated
					case TypeId.FLOAT:
						f.setFloat (value, in.readFloat ());
						break;
// generated
					case TypeId.DOUBLE:
						f.setDouble (value, in.readDouble ());
						break;
//!! *# End of generated code
					case TypeId.OBJECT:
						read (f, value, 0, in, true);
						break;
				}
				in.endField ();
			}
		}
	}


	static final Object read (PersistenceInput in, Object placeIn)
		throws IOException
	{
		ManageableType t = in.beginManaged ();
		placeIn = t.readObject (in, placeIn, in.areFieldsProvided ());
		in.endManaged ();
		return placeIn;
	}

	
	public static final Object read (Field field, PersistenceInput in) throws IOException
	{
		return read (field, null, -1, in, false);
	}

	static final Object read (Field field, Object container, int index,
							  PersistenceInput in, boolean nested)
		throws IOException
	{
		Object value;
		int n;
		switch (n = in.getNextObjectKind ())
		{
			case PersistenceInput.PLAIN_OBJECT:
			case PersistenceInput.NULL_OBJECT:
			case PersistenceInput.STRING_OBJECT:
			case PersistenceInput.SERIALIZED_OBJECT:
				value = in.readObject (n, field.getType ());
				break;
			case PersistenceInput.MANAGEABLE_OBJECT:
				ManageableType t = in.beginManaged ();
				if (t == null)
				{
					t = field.manageableType;
				}
				boolean fieldsProvided = in.areFieldsProvided ();
				if (!nested)
				{
					in.setNested (true);
				}
				Object o = getPlaceIn (field, t, container, index, -1);
				value = t.readObject (in, o, fieldsProvided);
				if (!nested)
				{
					in.setNested (false);
				}
				in.endManaged ();
				if (value == o)
				{
					return value;
				}
				break;
			case PersistenceInput.SHARED_OBJECT_REFERENCE:
				value = in.readSharedObject ();
				if (value == null)
				{
					return null;
				}
				break;
			case PersistenceInput.PERSISTENT_OBJECT_ID:
				value = in.readPersistentObject ();
				if (value == null)
				{
					return null;
				}
				break;
			case PersistenceInput.ARRAY_OBJECT:
				n = in.beginArray ();
				if (!nested)
				{
					in.setNested (true);
				}
				value = getPlaceIn (field, field.manageableType, container, index, n);
				Object oldValue = value;
				ArrayComponent f = field.arrayComponent;
				if (field.usesList)
				{
					switch (f.typeId)
					{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
						case TypeId.$pp.TYPE:
						{
							${pp.Type}List a = (${pp.Type}List) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.read$pp.Type ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.read$pp.Type ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
#end
!!*/
//!! #* Start of generated code
// generated
						case TypeId.BOOLEAN:
						{
							BooleanList a = (BooleanList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readBoolean ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readBoolean ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.BYTE:
						{
							ByteList a = (ByteList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readByte ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readByte ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.SHORT:
						{
							ShortList a = (ShortList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readShort ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readShort ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.CHAR:
						{
							CharList a = (CharList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readChar ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readChar ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.INT:
						{
							IntList a = (IntList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readInt ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readInt ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.LONG:
						{
							LongList a = (LongList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readLong ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readLong ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.FLOAT:
						{
							FloatList a = (FloatList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readFloat ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readFloat ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
// generated
						case TypeId.DOUBLE:
						{
							DoubleList a = (DoubleList) value;
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									a.add (in.readDouble ());
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									a.add (in.readDouble ());
									i++;
									in.endComponent ();
								}
							}
							break;
						}
//!! *# End of generated code
						case TypeId.OBJECT:
						{
							if (n >= 0)
							{
								for (int i = 0; i < n; i++)
								{
									in.beginComponent (f, i);
									read (f, value, i, in, true);
									in.endComponent ();
								}
							}
							else
							{
								int i = 0;
								while (in.beginComponent (f, i))
								{
									read (f, value, i++, in, true);
									in.endComponent ();
								}
							}
							break;
						}
					}
				}
				else if (n >= 0)
				{
					switch (f.typeId)
					{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
						case TypeId.$pp.TYPE:
						{
							$type[] a = ($type[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.read$pp.Type ();
								in.endComponent ();
							}
							break;
						}
#end
!!*/
//!! #* Start of generated code
// generated
						case TypeId.BOOLEAN:
						{
							boolean[] a = (boolean[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readBoolean ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.BYTE:
						{
							byte[] a = (byte[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readByte ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.SHORT:
						{
							short[] a = (short[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readShort ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.CHAR:
						{
							char[] a = (char[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readChar ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.INT:
						{
							int[] a = (int[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readInt ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.LONG:
						{
							long[] a = (long[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readLong ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.FLOAT:
						{
							float[] a = (float[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readFloat ();
								in.endComponent ();
							}
							break;
						}
// generated
						case TypeId.DOUBLE:
						{
							double[] a = (double[]) value;
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								a[i] = in.readDouble ();
								in.endComponent ();
							}
							break;
						}
//!! *# End of generated code
						case TypeId.OBJECT:
							for (int i = 0; i < n; i++)
							{
								in.beginComponent (f, i);
								read (f, value, i, in, true);
								in.endComponent ();
							}
							break;
					}
				}
				else
				{
					if (value == null)
					{
						value = field.getType ().createArray (0);
					}
					switch (f.typeId)
					{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
						case TypeId.$pp.TYPE:
						{
							$type[] a = ($type[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new $type[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new $type[n *= 2], 0,
													  i);
								}
								a[i++] = in.read$pp.Type ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.${pp.TYPE}_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new $type[i], 0, i);
							}
							break;
						}
#end
!!*/
//!! #* Start of generated code
// generated
						case TypeId.BOOLEAN:
						{
							boolean[] a = (boolean[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new boolean[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new boolean[n *= 2], 0,
													  i);
								}
								a[i++] = in.readBoolean ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.BOOLEAN_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new boolean[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.BYTE:
						{
							byte[] a = (byte[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new byte[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new byte[n *= 2], 0,
													  i);
								}
								a[i++] = in.readByte ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.BYTE_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new byte[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.SHORT:
						{
							short[] a = (short[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new short[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new short[n *= 2], 0,
													  i);
								}
								a[i++] = in.readShort ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.SHORT_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new short[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.CHAR:
						{
							char[] a = (char[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new char[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new char[n *= 2], 0,
													  i);
								}
								a[i++] = in.readChar ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.CHAR_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new char[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.INT:
						{
							int[] a = (int[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new int[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new int[n *= 2], 0,
													  i);
								}
								a[i++] = in.readInt ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.INT_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new int[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.LONG:
						{
							long[] a = (long[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new long[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new long[n *= 2], 0,
													  i);
								}
								a[i++] = in.readLong ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.LONG_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new long[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.FLOAT:
						{
							float[] a = (float[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new float[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new float[n *= 2], 0,
													  i);
								}
								a[i++] = in.readFloat ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.FLOAT_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new float[i], 0, i);
							}
							break;
						}
// generated
						case TypeId.DOUBLE:
						{
							double[] a = (double[]) value;
							int i = 0;
							n = a.length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									a = new double[n = 4];
								}
								else if (i == n)
								{
									System.arraycopy (a, 0,
													  a = new double[n *= 2], 0,
													  i);
								}
								a[i++] = in.readDouble ();
								in.endComponent ();
							}
							if (i == n)
							{
								value = a;
							}
							else if (i == 0)
							{
								value = Utils.DOUBLE_0;
							}
							else
							{
								System.arraycopy (a, 0,
												  value = new double[i], 0, i);
							}
							break;
						}
//!! *# End of generated code
						case TypeId.OBJECT:
						{
							Type type = field.getType ();
							int i = 0;
							n = ((Object[]) value).length;
							while (in.beginComponent (f, i))
							{
								if (n == 0)
								{
									value = type.createArray (n = 4);
								}
								else if (i == n)
								{
									System.arraycopy
										(value, 0,
										 value = type.createArray (n *= 2),
										 0, i);
								}
								read (f, value, i++, in, true);
								in.endComponent ();
							}
							if (i != n)
							{
								System.arraycopy
									(value, 0,
									 value = type.createArray (i), 0, i);
							}
							break;
						}
					}
				}
				if (!nested)
				{
					in.setNested (false);
				}
				in.endArray ();
				if (value == oldValue)
				{
					return value;
				}
				break;
			default:
				throw new StreamCorruptedException ("Illegal protocol data");
		}
		if (container != null)
		{
			if (field.isArrayComponent)
			{
				field.arrayField.setArrayComponent (container, index, value);
			}
			else
			{
				field.setObject (container, value);
			}
		}
		return value;
	}


	@Override
	public final Object cloneObject (Object o, boolean deep)
		throws CloneNotSupportedException
	{
		if ((o == null)
			|| ((o instanceof Shareable)
				&& (((Shareable) o).getProvider () != null)))
		{
			return o;
		}
		else
		{
			return cloneNonsharedObject (o, deep);
		}
	}


	protected Object cloneNonsharedObject (Object o, boolean deep)
		throws CloneNotSupportedException
	{
		if (o instanceof Manageable)
		{
			return ((Manageable) o).getManageableType ()
				.cloneManageable (o, deep);
		}
		else
		{
			return cloneManageable (o, deep);
		}
	}


	protected Object cloneManageable (Object o, boolean deep)
		throws CloneNotSupportedException
	{
		Object dup;
		try
		{
			dup = newInstance ();
		}
		catch (Exception e)
		{
			throw (CloneNotSupportedException)
				new CloneNotSupportedException ().initCause (Utils.unwrapFully (e));
		}
		for (int i = getManagedFieldCount () - 1; i >= 0; i--)
		{
			ManageableType.Field f = getManagedField (i);
			switch (f.getType ().getTypeId ())
			{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
				case TypeId.$pp.TYPE:
					f.set$pp.Type (dup, f.get$pp.Type (o));
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					f.setBoolean (dup, f.getBoolean (o));
					break;
// generated
				case TypeId.BYTE:
					f.setByte (dup, f.getByte (o));
					break;
// generated
				case TypeId.SHORT:
					f.setShort (dup, f.getShort (o));
					break;
// generated
				case TypeId.CHAR:
					f.setChar (dup, f.getChar (o));
					break;
// generated
				case TypeId.INT:
					f.setInt (dup, f.getInt (o));
					break;
// generated
				case TypeId.LONG:
					f.setLong (dup, f.getLong (o));
					break;
// generated
				case TypeId.FLOAT:
					f.setFloat (dup, f.getFloat (o));
					break;
// generated
				case TypeId.DOUBLE:
					f.setDouble (dup, f.getDouble (o));
					break;
//!! *# End of generated code
				case TypeId.OBJECT:
					f.setObject (dup, deep ? f.getCloned (o) : f.getObject (o));
					break;
			}
		}
		return (dup instanceof Manageable) ? ((Manageable) dup).manageableReadResolve () : dup;
	}


	public static Object cloneObject
		(Type t, Object o, boolean deep, boolean cloneShared)
		throws CloneNotSupportedException
	{
		if ((o == null)
			|| (!cloneShared && (o instanceof Shareable)
				&& (((Shareable) o).getProvider () != null)))
		{
			return o;
		}
		else if (o instanceof Manageable)
		{
			return ((Manageable) o).getManageableType ()
				.cloneNonsharedObject (o, deep);
		}
		else if (t != null)
		{
			return t.cloneObject (o, deep);
		}
		else
		{
			return o;
		}
	}

	
	public static ManageableType forName (String name)
	{
		return (ManageableType) nameToType.get (name);
	}


	public static ManageableType forType (Type type)
	{
		if (type instanceof ManageableType)
		{
			return (ManageableType) type;
		}
		de.grogra.reflect.Field f;
		if (Reflection.isPublic (type)
			&& ((f = Reflection.findFieldInClasses (type, "$TYPE")) != null)
			&& Reflection.isStatic (f) && Reflection.isPublic (f)
			&& Reflection.isSuperclassOrSame (ManageableType.class, f.getType ()))
		{
			try
			{
				return (ManageableType) f.getObject (null);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e);
			}
		}
		return forName (type.getBinaryName ());
	}


	public static ManageableType forClass (Class type)
	{
		try
		{
			java.lang.reflect.Field f = type.getField ("$TYPE");
			if (((f.getModifiers () & STATIC) != 0)
				&& ManageableType.class.isAssignableFrom (f.getType ()))
			{
				return (ManageableType) f.get (null);
			}
		}
		catch (NoSuchFieldException e)
		{
		}
		catch (IllegalAccessException e)
		{
			throw new AssertionError (e);
		}
		return forName (type.getName ());
	}
}
