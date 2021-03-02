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

package de.grogra.xl.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XData;
import de.grogra.reflect.XObject;
import de.grogra.xl.util.XHashMap;

public abstract class UserDefinedPattern extends Pattern implements XObject
{
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface In
	{
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface Out
	{
	}

	public static Method findSignatureMethod (Type type)
	{
		while (type != null)
		{
			XHashMap.Entry e = type.getLookup ().getMethods ("signature");
			while (e != null)
			{
				Method m = (Method) e.getValue ();
				if (Reflection.isStatic (m)
					&& (m.getReturnType ().getTypeId () == TypeId.VOID))
				{
					return m;
				}
				e = e.next ();
			}
			type = type.getSupertype ();
		}
		return null;
	}

	public static Type[] getSignature (Method sig, int[] inOut)
	{
		if (sig == null)
		{
			return null;
		}
		Type[] types = new Type[sig.getParameterCount ()];
		int in = -1, out = -1;
		for (int i = 0; i < sig.getParameterCount (); i++)
		{
			types[i] = sig.getParameterType (i);
			int n = sig.getParameterAnnotationCount (i);
			while (--n >= 0)
			{
				Type a = sig.getParameterAnnotation (i, n).annotationType ();
				if (Reflection.equal (In.class, a))
				{
					if (in >= 0)
					{
						return null;
					}
					in = i;
				}
				else if (Reflection.equal (Out.class, a))
				{
					if (out >= 0)
					{
						return null;
					}
					out = i;
				}
			}
		}
		if ((in >= 0) != (out >= 0))
		{
			return null;
		}
		if (inOut != null)
		{
			inOut[0] = in;
			inOut[1] = out;
		}
		return types;
	}

	private transient XClass cls;
	private transient XData data;

	public final void initXClass (XClass cls)
	{
		if (this.cls != null)
		{
			throw new IllegalStateException ();
		}
		this.cls = cls;
		data = new XData ();
		data.init (cls);
	}

	public final XClass getXClass ()
	{
		return cls;
	}

	public final XData getXData ()
	{
		return data;
	}

	protected UserDefinedPattern ()
	{
		super (null);
	}

	protected UserDefinedPattern (Type type)
	{
		super (type);
	}

	public final void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this, 0));
		out.endMethod ();
	}

}
