
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

import de.grogra.xl.util.ObjectList;

public abstract class MemberBase implements Member
{
	protected Type declaringType;
	protected String name;
	protected int modifiers;
	protected String descriptor;
	protected ObjectList<Annotation> annots;


	public MemberBase (String name, String descriptor, int modifiers,
					   Type declaringType)
	{
		this.name = name;
		this.descriptor = descriptor;
		this.modifiers = modifiers;
		this.declaringType = declaringType;
	}
	
	
	public MemberBase ()
	{
	}


	public final int getModifiers ()
	{
		return modifiers;
	}


	public final Type getDeclaringType ()
	{
		return declaringType;
	}


	public String getName ()
	{
		return name;
	}


	public String getSimpleName ()
	{
		return name;
	}


	public String getDescriptor ()
	{
		return descriptor;
	}


	public int getDeclaredAnnotationCount ()
	{
		return (annots != null) ? annots.size () : 0;
	}


	public Annotation getDeclaredAnnotation (int index)
	{
		return annots.get (index);
	}
	
	
	@Override
	public String toString ()
	{
		return getClass ().getName () + '[' + getDeclaringType () + ','
			+ getDescriptor () + ','
			+ Reflection.modifiersToString (getModifiers ()) + ']';
	}

}
