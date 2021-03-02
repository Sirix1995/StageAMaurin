
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

/**
 * This interface defines an enumeration of <code>int</code> ids which
 * are used to identify the kind of a type
 * (see {@link de.grogra.reflect.Type}). In addition, it defines some masks
 * which have to be used as follows:
 * <pre>
 *     if (((1 &lt;&lt; typeId) &amp; TypeId.INTEGRAL_MASK) != 0) {
 *         // typeId identifies a type which is integral
 *     }
 * </pre>
 * <b>IMPORTANT</b>: The numeric values of the ids must not be changed!
 * It is defined such that (for numeric types) it is a widening
 * conversion from s to t if the type id of s is less than the type id
 * of t, with one exception: The type <code>char</code>
 * has to be handled specially, because BYTE &lt; SHORT &lt; CHAR &lt; INT,
 * but there is no widening conversion from <code>byte</code> or
 * <code>short</code> to <code>char</code>. 
 *
 * @author Ole Kniemeyer
 */
public interface TypeId
{
	/**
	 * Type id for reference types.
	 */
	int OBJECT = 0;

	/**
	 * Type id for <code>void</code>.
	 */
	int VOID = 1;

	/**
	 * Type id for <code>boolean</code>.
	 */
	int BOOLEAN = 2;

	/**
	 * Type id for <code>byte</code>.
	 */
	int BYTE = 3;

	/**
	 * Type id for <code>short</code>.
	 */
	int SHORT = 4;

	/**
	 * Type id for <code>char</code>.
	 */
	int CHAR = 5;

	/**
	 * Type id for <code>int</code>.
	 */
	int INT = 6;

	/**
	 * Type id for <code>long</code>.
	 */
	int LONG = 7;

	/**
	 * Type id for <code>float</code>.
	 */
	int FLOAT = 8;

	/**
	 * Type id for <code>double</code>.
	 */
	int DOUBLE = 9;

	/**
	 * Type id for abstract primitive types, i.e.,
	 * {@link Type#NUMERIC}, {@link Type#INTEGRAL}.
	 * {@link Type#FLOATING_POINT}.
	 */
	int ABSTRACT_PRIMITIVE = 10;

	/**
	 * The number of defined types. Valid type ids range
	 * from 0 to <code>TYPE_COUNT-1</code>.
	 */
	int TYPE_COUNT = 10;

	int MIN_PRIMITIVE = BOOLEAN;
	
	int MAX_PRIMITIVE = DOUBLE;

	/**
	 * This is the mask for type ids of reference types.
	 */
	int OBJECT_MASK = 1 << OBJECT;

	/**
	 * This is the mask for type ids for <code>void</code>.
	 */
	int VOID_MASK = 1 << VOID;

	/**
	 * This is the mask for type ids for <code>boolean</code>.
	 */
	int BOOLEAN_MASK = 1 << BOOLEAN;

	/**
	 * This is the mask for type ids for <code>byte</code>.
	 */
	int BYTE_MASK = 1 << BYTE;

	/**
	 * This is the mask for type ids for <code>short</code>.
	 */
	int SHORT_MASK = 1 << SHORT;

	/**
	 * This is the mask for type ids for <code>char</code>.
	 */
	int CHAR_MASK = 1 << CHAR;

	/**
	 * This is the mask for type ids for <code>int</code>.
	 */
	int INT_MASK = 1 << INT;

	/**
	 * This is the mask for type ids for <code>long</code>.
	 */
	int LONG_MASK = 1 << LONG;

	/**
	 * This is the mask for type ids for <code>float</code>.
	 */
	int FLOAT_MASK = 1 << FLOAT;

	/**
	 * This is the mask for type ids for <code>double</code>.
	 */
	int DOUBLE_MASK = 1 << DOUBLE;

	/**
	 * This is the mask for type ids of types which as assignable
	 * to <code>int</code>.
	 */
	int INT_ASSIGNABLE = BYTE_MASK | SHORT_MASK | CHAR_MASK | INT_MASK;

	/**
	 * This is the mask for type ids of integral types.
	 */
	int INTEGRAL_MASK = INT_ASSIGNABLE | LONG_MASK;

	/**
	 * This is the mask for type ids of floating-point types.
	 */
	int FLOATING_POINT_MASK = FLOAT_MASK | DOUBLE_MASK;

	/**
	 * This is the mask for type ids of numeric types.
	 */
	int NUMERIC_MASK = INTEGRAL_MASK | FLOATING_POINT_MASK;

	/**
	 * This is the mask for type ids of numeric types, but not <code>char</code>.
	 */
	int NUMERIC_NONCHAR_MASK = NUMERIC_MASK & ~CHAR_MASK;

	/**
	 * This is the mask for type ids of primitive types.
	 */
	int PRIMITIVE_MASK = NUMERIC_MASK | BOOLEAN_MASK | (1 << ABSTRACT_PRIMITIVE);

	/**
	 * This is the mask for type ids of types whose values are represented
	 * by <code>int</code>s by the Java Virtual Machine.
	 */
	int I_VALUE = BOOLEAN_MASK | INT_ASSIGNABLE;

	/**
	 * This is the mask for type ids of types whose values are represented
	 * as <code>long</code>s by the Java Virtual Machine.
	 */
	int L_VALUE = LONG_MASK;

	/**
	 * This is the mask for type ids of types whose values are represented
	 * as <code>float</code>s by the Java Virtual Machine.
	 */
	int F_VALUE = FLOAT_MASK;

	/**
	 * This is the mask for type ids of types whose values are represented
	 * as <code>double</code>s by the Java Virtual Machine.
	 */
	int D_VALUE = DOUBLE_MASK;

	/**
	 * This is the mask for type ids of types whose values are represented
	 * as references by the Java Virtual Machine.
	 */
	int A_VALUE = OBJECT_MASK;
}
