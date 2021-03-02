
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

import java.lang.reflect.Modifier;

/**
 * <code>Member</code> is the superinterface for types, methods, and fields.
 * It describes the common functionality of these interfaces and defines
 * a set of <code>int</code> constants which are combined to encode
 * the modifiers of the member.
 *
 * @author Ole Kniemeyer
 */
public interface Member
{
	/**
	 * Modifier for <code>public</code> members.
	 */
	int PUBLIC = Modifier.PUBLIC;

	/**
	 * Modifier for <code>private</code> members.
	 */
	int PRIVATE = Modifier.PRIVATE;

	/**
	 * Modifier for <code>protected</code> members.
	 */
	int PROTECTED = Modifier.PROTECTED;

	/**
	 * Modifier for <code>static</code> members.
	 */
	int STATIC = Modifier.STATIC;

	/**
	 * Modifier for <code>final</code> members.
	 */
	int FINAL = Modifier.FINAL;

	/**
	 * Modifier for <code>synchronized</code> methods.
	 */
	int SYNCHRONIZED = Modifier.SYNCHRONIZED;

	/**
	 * Modifier for <code>volatile</code> fields.
	 */
	int VOLATILE = Modifier.VOLATILE;

	/**
	 * Modifier for <code>transient</code> fields.
	 */
	int TRANSIENT = Modifier.TRANSIENT;

	/**
	 * Modifier for <code>native</code> methods.
	 */
	int NATIVE = Modifier.NATIVE;

	/**
	 * Modifier indicating that a type is an interface.
	 */
	int INTERFACE = Modifier.INTERFACE;

	/**
	 * Modifier for <code>abstract</code> members.
	 */
	int ABSTRACT = Modifier.ABSTRACT;

	/**
	 * Modifier for <code>strictfp</code> members.
	 */
	int STRICT = Modifier.STRICT;

	/**
	 * Modifier for synthetic members.
	 */
	int SYNTHETIC = 0x1000;

	/**
	 * Modifier for bridge methods.
	 */
	int BRIDGE = 0x0040;

	/**
	 * Modifier for variable arity methods.
	 */
	int VARARGS = 0x0080;

	/**
	 * Mask for the access modifiers <code>public</code>,
	 * <code>private</code>, and <code>protected</code>.
	 */
	int ACCESS_MODIFIERS = PUBLIC | PRIVATE | PROTECTED;

	/**
	 * Mask for all modifiers which are valid in the byte-code
	 * of classes.
	 */
	int JAVA_MODIFIERS = PUBLIC | PRIVATE | PROTECTED | STATIC | FINAL
		| SYNCHRONIZED | VOLATILE | TRANSIENT | NATIVE | INTERFACE 
		| ABSTRACT | STRICT | SYNTHETIC;

	/**
	 * Modifier for fields which are compile-time constants. This modifier
	 * is not defined by the Java virtual machine.
	 */
	int CONSTANT = 1 << 18;

	/**
	 * Modifier for classes which are local classes. This modifier
	 * is not defined by the Java virtual machine.
	 */
	int LOCAL_CLASS = CONSTANT << 1;

	/**
	 * Modifier for classes which are arrays. This modifier
	 * is not defined by the Java virtual machine.
	 */
	int ARRAY = LOCAL_CLASS << 1;

	/**
	 * Minimal bit mask which is not defined by this interface.
	 */
	int MIN_UNUSED_MODIFIER = ARRAY << 1;


	/**
	 * Returns the declaring type of which this is a member. May be
	 * <code>null</code> if such a type does not exist.
	 * 
	 * @return this member's declaring type
	 */
	Type getDeclaringType ();

	/**
	 * Returns the modifiers of this member as a combination of
	 * the bit masks which are defined in this interface.
	 * 
	 * @return modifiers of this member
	 */
	int getModifiers ();

	/**
	 * Returns the name of this member. For types, this is the canonical name
	 * of the type, e.g, <code>java.lang.Object</code>,
	 * <code>java.util.Map.Entry</code>.
	 * Otherwise, it equals {@link #getSimpleName()}.
	 * 
	 * @return name of the member
	 */
	String getName ();

	/**
	 * Returns the simple name of this member. This is the single
	 * identifier with which the member is declared in source code,
	 * e.g., <code>Object</code>, <code>out</code>, <code>println</code>.
	 * 
	 * @return simple name of the member
	 */
	String getSimpleName ();

	/**
	 * Returns a descriptor for this member. Descriptors are constructed
	 * as follows:
	 * <ul>
	 * <li> For types, descriptors are defined as for the Java Virtual
	 * Machine. For primitive types these are the single characters
	 * 'Z', 'B', 'S', 'C', 'I', 'J', 'F', 'D', 'V'
	 * for <code>boolean</code>, <code>byte</code>, <code>short</code>,
	 * <code>char</code>, <code>int</code>, <code>long</code>,
	 * <code>float</code>, <code>double</code>, <code>void</code>.
	 * For array types, the descriptor is the character '[' plus
	 * the descriptor of its component type, e.g.,
	 * <code>[[I</code> for <code>int[][]</code>. For non-array
	 * reference types, the
	 * descriptor is the character 'L', followed by the binary name
	 * (see {@link Type#getBinaryName()}) of the type, where '.'
	 * has to be replaced by '/', followed by ';', 
	 * e.g., <code>Ljava/lang/Object;</code>.
	 * <li> For fields, the descriptor is the character 'f', followed by the
	 * simple name of the field, followed by the character ';', followed
	 * by the descriptor of the field's type. E.g.,
	 * <code>fout;Ljava/io/PrintStream;</code>
	 * for the field <code>System.out</code>.
	 * <li> For methods and constructors, the descriptor is the character
	 * 'm', followed by the simple name of the method (which is <code>
	 * &lt;init&gt;</code> for
	 * constructors), followed by the characters ';' and '(', followed
	 * by the descriptors of the method's parameter types, followed by
	 * ')', followed by the descriptor of the return type. E.g.
	 * <code>mprintln;(Ljava/lang/String;)V</code> for the method
	 * <code>PrintStream.println(String)</code>.
	 * </ul>
	 * 
	 * @return the member's descriptor
	 */
	String getDescriptor ();

	int getDeclaredAnnotationCount ();

	Annotation getDeclaredAnnotation (int index);
}
