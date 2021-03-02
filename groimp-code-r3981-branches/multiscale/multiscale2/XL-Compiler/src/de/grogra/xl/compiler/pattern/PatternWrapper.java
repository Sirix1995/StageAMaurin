
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

package de.grogra.xl.compiler.pattern;

import de.grogra.reflect.Annotation;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Signature;
import de.grogra.reflect.Type;
import de.grogra.xl.query.BytecodeSerialization;
import de.grogra.xl.query.Graph;
import de.grogra.xl.query.Pattern;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class PatternWrapper extends Pattern
	implements Member, Signature
{
	private final int inParameter;
	private final int outParameter;
	private final Type predType;
	private final Type nameType;
	private final String descr;
	
	private transient Pattern predicate;


	public PatternWrapper (Type predType, Type nameType, Type[] paramTypes, int in, int out)
	{
		super (paramTypes, paramTypes.length);
		this.inParameter = in;
		this.outParameter = out;
		this.predType = predType;
		this.nameType = nameType;
		this.descr = 'p' + predType.getBinaryName () + ';';
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Reflection.getDeclaredMethod (predType, "m<init>;()V"));
		out.endMethod ();
	}


	private synchronized Pattern getPattern ()
	{
		if (predicate == null)
		{
			try
			{
				predicate = (Pattern) predType.newInstance ();
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
			catch (InstantiationException e)
			{
				throw new AssertionError (e.getMessage ());
			}
			catch (java.lang.reflect.InvocationTargetException e)
			{
				throw new AssertionError (e.getCause ());
			}
		}
		return predicate;
	}


	public Type getPatternType ()
	{
		return predType;
	}


	public boolean isFirstInOut ()
	{
		return (inParameter == 0) && (outParameter == 0);
	}

	public int getInParameter ()
	{
		return inParameter;
	}

	public int getOutParameter ()
	{
		return outParameter;
	}

	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants, IntList neededConstantsOut)
	{
		return getPattern ().createMatcher (src, providedConstants, neededConstantsOut);
	}


	@Override
	public int getParameterKind (int index)
	{
		return getPattern ().getParameterKind (index);
	}
	
	
	@Override
	public boolean isDeleting ()
	{
		return getPattern ().isDeleting ();
	}


	public int getModifiers ()
	{
		return predType.getModifiers ();
	}


	public Type getDeclaringType ()
	{
		return nameType.getDeclaringType ();
	}


	public String getName ()
	{
		return nameType.getName ();
	}


	public String getSimpleName ()
	{
		return nameType.getSimpleName ();
	}


	public String getDescriptor ()
	{
		return descr;
	}


	public int getDeclaredAnnotationCount ()
	{
		return predType.getDeclaredAnnotationCount ();
	}


	public Annotation getDeclaredAnnotation (int index)
	{
		return predType.getDeclaredAnnotation (index);
	}

}
