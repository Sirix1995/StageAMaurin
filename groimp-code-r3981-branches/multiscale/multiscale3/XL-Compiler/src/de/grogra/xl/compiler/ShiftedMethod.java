
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

import de.grogra.reflect.Annotation;
import de.grogra.reflect.MemberDecorator;
import de.grogra.reflect.Method;
import de.grogra.reflect.Type;

public class ShiftedMethod extends MemberDecorator implements Method
{
	private final Method method;

	public ShiftedMethod (Method method)
	{
		super (method);
		this.method = method;
	}

	public Method getMethod ()
	{
		return method;
	}

	public int getExceptionCount ()
	{
		return method.getExceptionCount ();
	}

	public Type getExceptionType (int index)
	{
		return method.getExceptionType (index);
	}

	public int getModifiers ()
	{
		return method.getModifiers () | STATIC;
	}

	public Annotation getParameterAnnotation (int param, int index)
	{
		return method.getParameterAnnotation (param - 1, index);
	}

	public int getParameterAnnotationCount (int param)
	{
		return (param == 0) ? 0 : method.getParameterAnnotationCount (param - 1);
	}

	public int getParameterCount ()
	{
		return method.getParameterCount () + 1;
	}

	public Type getParameterType (int index)
	{
		return (index == 0) ? method.getDeclaringType () : method.getParameterType (index - 1);
	}

	public Type getReturnType ()
	{
		return method.getReturnType ();
	}

	public Object invoke (Object instance, Object[] arguments) throws InvocationTargetException, IllegalAccessException
	{
		instance = arguments[0];
		System.arraycopy (arguments, 1, arguments = new Object[arguments.length - 1], 0, arguments.length);
		return method.invoke (instance, arguments);
	}
}
