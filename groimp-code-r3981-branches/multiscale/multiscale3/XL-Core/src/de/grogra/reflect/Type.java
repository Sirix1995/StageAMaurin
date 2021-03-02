
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

import javax.swing.JTextArea;

/**
 * The interface <code>Type</code> is similar in function to the class
 * {@link java.lang.Class}. It represents a type of the Java programming
 * language and provides access to its properties (e.g., name, members).
 * In contrast to {@link java.lang.Class}, an instance of <code>Type</code>
 * does not necessarily represent a compiled class (available as byte-code),
 * it may also represent an object which provides functionality similar
 * to real classes.  
 *
 * @author Ole Kniemeyer
 */
public interface Type<T> extends Member
{
	/**
	 * Mask representing the valid modifiers for top-level classes.
	 */
	int TOP_LEVEL_CLASS_MODIFIERS = PUBLIC | FINAL | ABSTRACT | STRICT;

	/**
	 * Mask representing the valid modifiers for member classes.
	 */
	int MEMBER_CLASS_MODIFIERS
		= TOP_LEVEL_CLASS_MODIFIERS | ACCESS_MODIFIERS | STATIC;

	/**
	 * Mask representing the valid modifiers for local classes.
	 */
	int LOCAL_CLASS_MODIFIERS = FINAL | ABSTRACT | STRICT;

	/**
	 * Mask representing the valid modifiers for top-level interfaces.
	 */
	int TOP_LEVEL_INTERFACE_MODIFIERS = PUBLIC | STRICT | ABSTRACT;

	/**
	 * Mask representing the valid modifiers for member interfaces.
	 */
	int MEMBER_INTERFACE_MODIFIERS
		= TOP_LEVEL_INTERFACE_MODIFIERS | ACCESS_MODIFIERS | STATIC;

	/**
	 * Immutable array of length 0.
	 */
	Type[] TYPE_0 = new Type[0];

	/**
	 * Base type for all primitive numeric types.
	 */
	Type<Number> NUMERIC = new TypeImpl (TypeId.ABSTRACT_PRIMITIVE, "numeric", "??",
			PUBLIC | ABSTRACT, null, null, null);

	/**
	 * Direct supertype for primitive integral types.
	 */
	Type<Number> INTEGRAL = new TypeImpl (TypeId.ABSTRACT_PRIMITIVE, "integral", "??",
			PUBLIC | ABSTRACT, null, NUMERIC, null);

	/**
	 * Direct supertype for primitive floating-point types.
	 */
	Type<Number> FLOATING_POINT = new TypeImpl (TypeId.ABSTRACT_PRIMITIVE, "floatingpoint", "??",
			PUBLIC | ABSTRACT, null, NUMERIC, null);

	/**
	 * Represents the class {@link Object}.
	 */
	Type<Object> OBJECT = ClassAdapter.wrap (Object.class);

	/**
	 * Represents the class {@link Class}.
	 */
	Type<Class> CLASS = ClassAdapter.wrap (Class.class);

	/**
	 * Represents the interface {@link Type}.
	 */
	Type<Type> TYPE = ClassAdapter.wrap (Type.class);

	/**
	 * Represents the null type of the Java programming language.
	 */
	Type<?> NULL = new TypeImpl (TypeId.OBJECT, "null", "null", PUBLIC | FINAL,
							  null, null, null);

	/**
	 * Represents the primitive type <code>void</code>.
	 */
	Type<Void> VOID = ClassAdapter.wrap (Void.TYPE);

	/**
	 * Represents the primitive type <code>boolean</code>.
	 */
	Type<Boolean> BOOLEAN = ClassAdapter.wrap (Boolean.TYPE);

	/**
	 * Represents the primitive type <code>byte</code>. The direct
	 * supertype is {@link #INTEGRAL}.
	 */
	Type<Byte> BYTE = ClassAdapter.wrap (Byte.TYPE);

	/**
	 * Represents the primitive type <code>short</code>. The direct
	 * supertype is {@link #INTEGRAL}.
	 */
	Type<Short> SHORT = ClassAdapter.wrap (Short.TYPE);

	/**
	 * Represents the primitive type <code>char</code>. The direct
	 * supertype is {@link #INTEGRAL}.
	 */
	Type<Character> CHAR = ClassAdapter.wrap (Character.TYPE);

	/**
	 * Represents the primitive type <code>int</code>. The direct
	 * supertype is {@link #INTEGRAL}.
	 */
	Type<Integer> INT = ClassAdapter.wrap (Integer.TYPE);

	/**
	 * Represents the primitive type <code>long</code>. The direct
	 * supertype is {@link #INTEGRAL}.
	 */
	Type<Long> LONG = ClassAdapter.wrap (Long.TYPE);

	/**
	 * Represents the primitive type <code>float</code>. The direct
	 * supertype is {@link #FLOATING_POINT}.
	 */
	Type<Float> FLOAT = ClassAdapter.wrap (Float.TYPE);

	/**
	 * Represents the primitive type <code>double</code>. The direct
	 * supertype is {@link #FLOATING_POINT}.
	 */
	Type<Double> DOUBLE = ClassAdapter.wrap (Double.TYPE);

	/**
	 * Represents an invalid type.
	 */
	Type INVALID = new TypeImpl (TypeId.OBJECT, "??", "??", PUBLIC | FINAL,
								 null, null, null);

	/**
	 * Represents the class {@link String}.
	 */
	Type<String> STRING = ClassAdapter.wrap (String.class);

	/**
	 * Represents the class {@link String}.
	 */
	Type<JTextArea> TEXTAREA = ClassAdapter.wrap (JTextArea.class);

	
	/**
	 * Represents the class {@link Number}.
	 */
	Type<Number> NUMBER = ClassAdapter.wrap (Number.class);


	/**
	 * Returns the type id of this type.
	 * 
	 * @return this type's type id
	 * @see TypeId
	 */
	int getTypeId ();
	
	String getPackage ();
	
	String getBinaryName ();

	boolean isInstance (Object object);

	Class<T> getImplementationClass ();

	TypeLoader getTypeLoader ();

	Type<? super T> getSupertype ();

	int getDeclaredInterfaceCount ();

	Type<?> getDeclaredInterface (int index);

	int getDeclaredFieldCount ();

	Field getDeclaredField (int index);

	int getDeclaredTypeCount ();

	Type<?> getDeclaredType (int index);

	int getDeclaredMethodCount ();

	Method getDeclaredMethod (int index);

	Type<?> getComponentType ();

	Type<?> getArrayType ();

	Object getDefaultElementValue (String name);

	Object createArray (int length);

	T cloneObject (T o, boolean deep) throws CloneNotSupportedException;

	boolean isStringSerializable ();

	T valueOf (String s);
	
	T newInstance () throws java.lang.reflect.InvocationTargetException,
		InstantiationException, IllegalAccessException;
	
	Lookup getLookup ();
}
