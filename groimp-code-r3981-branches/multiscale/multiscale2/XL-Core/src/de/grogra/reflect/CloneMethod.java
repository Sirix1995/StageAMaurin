
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

final class CloneMethod extends MemberBase implements Method
{
	CloneMethod (Type arrayType)
	{
		super ("clone", Reflection.getMethodDescriptor ("clone", Type.OBJECT,
														Type.TYPE_0),
			   PUBLIC, arrayType);
	}


	public Type getReturnType ()
	{
		return Type.OBJECT;
	}

	public int getParameterAnnotationCount (int param)
	{
		return 0;
	}

	public Annotation getParameterAnnotation (int param, int index)
	{
		return null;
	}

	public Object invoke (Object instance, Object[] arguments)
	{
		switch (declaringType.getComponentType ().getTypeId ())
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				return (($type[]) instance).clone ();
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				return ((boolean[]) instance).clone ();
// generated
			case TypeId.BYTE:
				return ((byte[]) instance).clone ();
// generated
			case TypeId.SHORT:
				return ((short[]) instance).clone ();
// generated
			case TypeId.CHAR:
				return ((char[]) instance).clone ();
// generated
			case TypeId.INT:
				return ((int[]) instance).clone ();
// generated
			case TypeId.LONG:
				return ((long[]) instance).clone ();
// generated
			case TypeId.FLOAT:
				return ((float[]) instance).clone ();
// generated
			case TypeId.DOUBLE:
				return ((double[]) instance).clone ();
// generated
			case TypeId.OBJECT:
				return ((Object[]) instance).clone ();
//!! *# End of generated code
		}
		throw new AssertionError ();
	}


	public int getExceptionCount ()
	{
		return 0;
	}


	public Type getExceptionType (int index)
	{
		return null;
	}


	public int getParameterCount ()
	{
		return 0;
	}


	public Type getParameterType (int index)
	{
		return null;
	}

}
