
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

public abstract class MemberDecorator implements Member
{
	private final Member member;

	
	public MemberDecorator (Member member)
	{
		this.member = member;
	}

	
	public Member getDecoratedMember ()
	{
		return member;
	}


	public Annotation getDeclaredAnnotation (int index)
	{
		return getDecoratedMember ().getDeclaredAnnotation (index);
	}


	public int getDeclaredAnnotationCount ()
	{
		return getDecoratedMember ().getDeclaredAnnotationCount ();
	}

	
	public Type getDeclaringType ()
	{
		return getDecoratedMember ().getDeclaringType ();
	}

	
	public String getDescriptor ()
	{
		return getDecoratedMember ().getDescriptor ();
	}

	
	public int getModifiers ()
	{
		return getDecoratedMember ().getModifiers ();
	}

	
	public String getName ()
	{
		return getDecoratedMember ().getName ();
	}

	
	public String getSimpleName ()
	{
		return getDecoratedMember ().getSimpleName ();
	}

	@Override
	public String toString ()
	{
		return getClass ().getName () + '[' + getDecoratedMember () + ']';
	}
}
