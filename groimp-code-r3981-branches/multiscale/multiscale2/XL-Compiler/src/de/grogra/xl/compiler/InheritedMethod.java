
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

import java.lang.reflect.InvocationTargetException;

import de.grogra.reflect.*;

public class InheritedMethod extends MemberDecorator implements Method
{
	public static Type getQualifyingType (Method m)
	{
		return (m instanceof InheritedMethod) ? ((InheritedMethod) m).inheriting
			: m.getDeclaringType ();
	}


	private final Type inheriting;

	public InheritedMethod (Method method, Type inheriting)
	{
		super (method);
		this.inheriting = inheriting;
	}

	private Method getDecoratedMethod ()
	{
		return (Method) getDecoratedMember ();
	}

	public Type getReturnType ()
	{
		return getDecoratedMethod ().getReturnType ();
	}

	public int getParameterAnnotationCount (int param)
	{
		return getDecoratedMethod ().getParameterAnnotationCount (param);
	}

	public Annotation getParameterAnnotation (int param, int index)
	{
		return getDecoratedMethod ().getParameterAnnotation (param, index);
	}

	public Object invoke (Object instance, Object[] arguments) throws InvocationTargetException, IllegalAccessException
	{
		return getDecoratedMethod ().invoke (instance, arguments);
	}

	public int getExceptionCount ()
	{
		return getDecoratedMethod ().getExceptionCount ();
	}

	public Type getExceptionType (int index)
	{
		return getDecoratedMethod ().getExceptionType (index);
	}

	public int getParameterCount ()
	{
		return getDecoratedMethod ().getParameterCount ();
	}

	public Type getParameterType (int index)
	{
		return getDecoratedMethod ().getParameterType (index);
	}
}
