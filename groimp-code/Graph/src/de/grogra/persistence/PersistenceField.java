
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

/**
 * A <code>PersistenceField</code> represents a field which can be handled
 * by classes of the <code>persistence</code> package. It is
 * an abstract base class; its methods imply that a <code>PersistenceField</code>
 * is composed of a chain of fields of class
 * {@link de.grogra.persistence.ManageableType.Field}. E.g., if an object
 * has a field <code>location</code> of type <code>javax.vecmath.Point3f</code>,
 * such a chain could be <code>(location, x)</code> (then it denotes the
 * <code>x</code> field of the <code>location</code> field of the object).
 * 
 * A chain may involve array components. An array component is either
 * an array component in the sense of the Java programming language,
 * e.g., <code>object.values[i]</code>, or an element of
 * <code>java.util.List</code> or one of the primitive list classes in
 * <code>de.grogra.util</code>, e.g., <code>object.values.get (i)</code>.
 * When a <code>PersistenceField</code> containing array components is
 * accessed by one of its methods, the <code>indices</code> parameter has
 * to provide one index for every component. E.g., a field access
 * <code>object.values[i].locations[j].x</code> is represented
 * by the chain <code>(values, [], locations, [], x)</code>, where
 * <code>[]</code> represents array components,
 * and <code>indices = {i, j}</code>. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class PersistenceField extends MemberBase
	implements Field, FieldChain
{
	static final int CHAIN_LENGTH_BIT = 24;

	boolean fco;
	int typeId;


	public static PersistenceField get (Field field)
	{
		if (field instanceof PersistenceField)
		{
			return (PersistenceField) field;
		}
		Type t = field.getDeclaringType ();
		if (!(t instanceof ManageableType))
		{
			t = ManageableType.forType (t);
		}
		return (t == null) ? null
			: ((ManageableType) t).getManagedField (field.getName ());
	}


	PersistenceField (String name, String descriptor, int modifiers,
					  Type declaringType)
	{
		super (name, descriptor, modifiers, declaringType);
	}


	public final Field getField (int index)
	{
		return getSubfield (index);
	}

	
	public abstract ManageableType.Field getSubfield (int index);


	public abstract ManageableType.Field getLastField ();
	
	
	public abstract PersistenceField getShallowSubchain (int begin);
	
	
	abstract PersistenceField getShallowSuperchain ();


	public abstract de.grogra.util.Quantity getQuantity ();
	
	
	public abstract Number getMinValue ();

	
	public abstract Number getMaxValue ();


	public boolean isWritable (Object object)
	{
		return true;
	}


	abstract void write (int[] indices, XAQueue out);


	abstract void writeObject (Object value, PersistenceOutput out)
		throws IOException;


	public final void set (Object o, int[] indices, Object value,
						   Transaction t)
	{
		switch (typeId)
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				set$pp.Type (o, indices, $pp.unwrap("value"), t);
				return;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				setBoolean (o, indices, (((Boolean) (value)).booleanValue ()), t);
				return;
// generated
			case TypeId.BYTE:
				setByte (o, indices, (((Number) (value)).byteValue ()), t);
				return;
// generated
			case TypeId.SHORT:
				setShort (o, indices, (((Number) (value)).shortValue ()), t);
				return;
// generated
			case TypeId.CHAR:
				setChar (o, indices, (((Character) (value)).charValue ()), t);
				return;
// generated
			case TypeId.INT:
				setInt (o, indices, (((Number) (value)).intValue ()), t);
				return;
// generated
			case TypeId.LONG:
				setLong (o, indices, (((Number) (value)).longValue ()), t);
				return;
// generated
			case TypeId.FLOAT:
				setFloat (o, indices, (((Number) (value)).floatValue ()), t);
				return;
// generated
			case TypeId.DOUBLE:
				setDouble (o, indices, (((Number) (value)).doubleValue ()), t);
				return;
// generated
			case TypeId.OBJECT:
				setObject (o, indices, (value), t);
				return;
//!! *# End of generated code
		}
	}


	public final Object get (Object object, int[] indices)
	{
		switch (typeId)
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				return $pp.wrap("get$pp.Type (object, indices)");
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				return ((getBoolean (object, indices)) ? Boolean.TRUE : Boolean.FALSE);
// generated
			case TypeId.BYTE:
				return Byte.valueOf (getByte (object, indices));
// generated
			case TypeId.SHORT:
				return Short.valueOf (getShort (object, indices));
// generated
			case TypeId.CHAR:
				return Character.valueOf (getChar (object, indices));
// generated
			case TypeId.INT:
				return Integer.valueOf (getInt (object, indices));
// generated
			case TypeId.LONG:
				return Long.valueOf (getLong (object, indices));
// generated
			case TypeId.FLOAT:
				return Float.valueOf (getFloat (object, indices));
// generated
			case TypeId.DOUBLE:
				return Double.valueOf (getDouble (object, indices));
// generated
			case TypeId.OBJECT:
				return (getObject (object, indices));
//!! *# End of generated code
			default:
				throw new AssertionError ();
		}
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 $C
	public abstract void set$pp.Type (Object o, int[] indices,
									  $type value, Transaction t);

	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 $C
	public abstract void insert$pp.Type (Object o, int[] indices,
										 $type value, Transaction t);

	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 $C
	public abstract void remove$pp.Type (Object o, int[] indices,
										 Transaction t);

	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 $C
	public abstract $type get$pp.Type (Object o, int[] indices);


	abstract $type readAndSet$pp.Type (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);

	abstract void readAndInsert$pp.Type (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setBoolean (Object o, int[] indices,
									  boolean value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertBoolean (Object o, int[] indices,
										 boolean value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeBoolean (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract boolean getBoolean (Object o, int[] indices);
// generated
// generated
	abstract boolean readAndSetBoolean (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertBoolean (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setByte (Object o, int[] indices,
									  byte value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertByte (Object o, int[] indices,
										 byte value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeByte (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract byte getByte (Object o, int[] indices);
// generated
// generated
	abstract byte readAndSetByte (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertByte (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setShort (Object o, int[] indices,
									  short value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertShort (Object o, int[] indices,
										 short value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeShort (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract short getShort (Object o, int[] indices);
// generated
// generated
	abstract short readAndSetShort (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertShort (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setChar (Object o, int[] indices,
									  char value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertChar (Object o, int[] indices,
										 char value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeChar (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract char getChar (Object o, int[] indices);
// generated
// generated
	abstract char readAndSetChar (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertChar (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setInt (Object o, int[] indices,
									  int value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertInt (Object o, int[] indices,
										 int value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeInt (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract int getInt (Object o, int[] indices);
// generated
// generated
	abstract int readAndSetInt (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertInt (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setLong (Object o, int[] indices,
									  long value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertLong (Object o, int[] indices,
										 long value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeLong (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract long getLong (Object o, int[] indices);
// generated
// generated
	abstract long readAndSetLong (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertLong (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setFloat (Object o, int[] indices,
									  float value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertFloat (Object o, int[] indices,
										 float value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeFloat (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract float getFloat (Object o, int[] indices);
// generated
// generated
	abstract float readAndSetFloat (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertFloat (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setDouble (Object o, int[] indices,
									  double value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertDouble (Object o, int[] indices,
										 double value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeDouble (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract double getDouble (Object o, int[] indices);
// generated
// generated
	abstract double readAndSetDouble (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertDouble (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
// generated
// generated
	/**
	 * Sets the value of this field on the instance <code>o</code>
	 * using the given <code>indices</code> and <code>t</code>.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the new value
	 * @param t transaction context
	 */
	public abstract void setObject (Object o, int[] indices,
									  Object value, Transaction t);
// generated
	/**
	 * Inserts the given <code>value</code> into an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The new <code>value</code> will be inserted at the place of the
	 * indexed component. 
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param value the value to be inserted
	 * @param t transaction context
	 */
	public abstract void insertObject (Object o, int[] indices,
										 Object value, Transaction t);
// generated
	/**
	 * Removes an element from an array.
	 * This field has to be an array component indexed by <code>indices</code>.
	 * The value at the indexed component will be removed.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field is modified
	 * @param indices the indices for array fields
	 * @param t transaction context
	 */
	public abstract void removeObject (Object o, int[] indices,
										 Transaction t);
// generated
	/**
	 * Returns the value of a field.
	 * 
	 * @see PersistenceField
	 * @param o the instance for which the field value is returned
	 * @param indices the indices for array fields
	 * @return the field's value for the instance <code>pc</code> 
	 */
	public abstract Object getObject (Object o, int[] indices);
// generated
// generated
	abstract Object readAndSetObject (PersistenceCapable pc, int[] indices,
									   XAQueue.Reader reader);
// generated
	abstract void readAndInsertObject (PersistenceCapable pc, int[] indices,
										 XAQueue.Reader reader);
//!! *# End of generated code

}
