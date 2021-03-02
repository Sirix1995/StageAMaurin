
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

public interface Method extends Member, Signature
{
	int MODIFIERS = ACCESS_MODIFIERS | STATIC | FINAL | SYNCHRONIZED
		| NATIVE | ABSTRACT | STRICT | VARARGS;
	
	int CONSTRUCTOR_MODIFIERS = ACCESS_MODIFIERS | VARARGS;

	int INTERFACE_MODIFIERS = PUBLIC | ABSTRACT | VARARGS;

	Method[] METHOD_0 = new Method[0];

	
	Type getReturnType ();

	Object invoke (Object instance, Object[] arguments)
		throws java.lang.reflect.InvocationTargetException, IllegalAccessException;

	int getParameterAnnotationCount (int param);

	Annotation getParameterAnnotation (int param, int index);

	int getExceptionCount ();
	
	Type getExceptionType (int index);
}
