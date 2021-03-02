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

package de.grogra.xl.compiler;

import org.objectweb.asm.Opcodes;

/**
 * This class contains the set of all compiler options.
 * 
 * @author Ole Kniemeyer
 */
public final class CompilerOptions implements Cloneable
{
	public static final boolean DEFAULT_D2F_IS_WIDENING = false;
	public static final boolean DEFAULT_ALLOW_CONVERSION_WITH_STATIC_VALUEOF = true;
	public static final boolean DEFAULT_ALLOW_CONVERSION_WITH_STATIC_TOX = true;
	public static final boolean DEFAULT_ALLOW_CONVERSION_WITH_TOX = true;
	public static final boolean DEFAULT_ALLOW_CONVERSION_WITH_CTOR = true;
	public static final boolean DEFAULT_ENABLE_AUTOCONVERSION_ANNOTATION = false;

	/**
	 * Select the target JVM compatibility.
	 * A compiler switch allows to select another value for this switch,
	 * the default is JDK1.4 compatibility.
	 * 
	 * An example where this switch influences bytecode generation:
	 * In java.lang.Integer the conversion of an int to Integer is only
	 * possible by creating a new instance of Integer for Java 1.4.
	 * Since Java 1.5 the class java.lang.Integer also contains a static
	 * method valueOf(int), which is a better choice for that conversion.
	 */
	public int javaVersion = Opcodes.V1_4;


	/**
	 * Compiler option: if true any function of the type
	 * 'static B valueOf(A)' in the current scope is used
	 * to implicitly perform autoconversion from type A
	 * to type B.
	 * Note: since the return type is not part of a method's
	 * signature, having multiple valueOf-functions may be
	 * ambigous.
	 */
	public boolean allowConversionWithStaticValueOf = DEFAULT_ALLOW_CONVERSION_WITH_STATIC_VALUEOF;

	/**
	 * Compiler option: if true any function of the type
	 * 'static B toB(A)' in the current scope is used
	 * to implicitly perform autoconversion from type A
	 * to type B.
	 */
	public boolean allowConversionWithStaticToX = DEFAULT_ALLOW_CONVERSION_WITH_STATIC_TOX;

	/**
	 * Compiler option: if true any function of the type
	 * 'B A.valueOf()' in the current scope is used
	 * to implicitly perform autoconversion from type A
	 * to type B. The function is called on an instance of
	 * type A.
	 */
	public boolean allowConversionWithToX = DEFAULT_ALLOW_CONVERSION_WITH_TOX;

	/**
	 * Compiler option: if true any function of the type
	 * 'B(A)' in the current scope is used
	 * to implicitly perform autoconversion from type A
	 * to type B. This is equivalent to creating a new
	 * instance via 'new B(a)', where a is of type A.
	 */
	public boolean allowConversionWithCtor = DEFAULT_ALLOW_CONVERSION_WITH_CTOR;

	/**
	 * Compiler option: if true any conversion function
	 * needs to be annotated with @Autoconversion.
	 * 
	 * @see de.grogra.xl.lang.Autoconversion
	 */
	public boolean enableAutoconversionAnnotation = DEFAULT_ENABLE_AUTOCONVERSION_ANNOTATION;

	
	public boolean sourceInfo = false;

	
	public boolean lineNumberInfo = false;

	
	public boolean localInfo = false;

	
	public CompilerOptions ()
	{
	}

	
	/**
	 * Returns true if the specified version number designates a version
	 * of the JVM that is compatible with the target version currently
	 * set. Valid values for the version number are the constants
	 * in org.objectweb.asm.Opcodes.V*
	 * For instance a call supportsVersion(Opcodes.V1_4) with Opcodes.V1_4
	 * currently set as target would return true. The same call with
	 * Opcodes.V1_3 set as target would return false.
	 * @param ver version to compare
	 * @return <code>true</code> iff <code>ver</code> is supported
	 * by the currently set version
	 */
	public boolean supportsVersion (int ver)
	{
		return isVersionGE (javaVersion, ver);
	}


	static boolean isVersionGE (int targetVersion, int version)
	{
		char major = (char) targetVersion, minor = (char) (targetVersion >> 16);
		char vma = (char) version, vmi = (char) (version >> 16);
		return (major > vma) || ((major == vma) && (minor >= vmi));
	}

}
