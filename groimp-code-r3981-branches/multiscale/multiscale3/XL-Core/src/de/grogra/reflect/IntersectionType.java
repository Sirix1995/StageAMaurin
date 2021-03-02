
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

public class IntersectionType extends TypeImpl<Object>
{
	private final String pkg;
	private Type baseType;
	private ObjectList<Type<?>> interfaces = new ObjectList<Type<?>> ();


	public IntersectionType (String pkg)
	{
		super (TypeId.OBJECT, "intersection", null, PUBLIC, null, null, null);
		baseType = Type.OBJECT;
		this.pkg = pkg;
	}


	public IntersectionType (Type<?> base)
	{
		this (base.getPackage ());
		intersect (base);
	}

	
	@Override
	public String getPackage ()
	{
		return pkg;
	}


	@Override
	public Type<Object> getSupertype ()
	{
		return baseType;
	}


	@Override
	public Type<?> getComponentType ()
	{
		return baseType.getComponentType ();
	}


	@Override
	public boolean isInstance (Object o)
	{
		if ((o == null) || !baseType.isInstance (o))
		{
			return false;
		}
		for (int i = interfaces.size - 1; i >= 0; i--)
		{
			if (!((Type) interfaces.get (i)).isInstance (o))
			{
				return false;
			}
		}
		return true;
	}


	@Override
	public int getDeclaredInterfaceCount ()
	{
		return interfaces.size;
	}


	@Override
	public Type<?> getDeclaredInterface (int index)
	{
		return interfaces.get (index);
	}


	private void narrowAccess (Type<?> type)
	{
		// TODO
	}


	public void narrow (Type<?> newBase)
	{
		if (Reflection.isSuperclassOrSame (newBase, baseType))
		{
			return;
		}
		for (int i = interfaces.size - 1; i >= 0; i--)
		{
			if (Reflection.isSupertypeOrSame (interfaces.get (i), newBase))
			{
				interfaces.remove (i);
			}
		}
		baseType = newBase;
		modifiers |= newBase.getModifiers () & (FINAL | ARRAY);
		narrowAccess (newBase);
	}

	
	public void addInterface (Type<?> iface)
	{
		if (!Reflection.isSupertypeOrSame (iface, this))
		{
			interfaces.add (iface);
		}
		narrowAccess (iface);
	}

	
	public void intersect (Type<?> t)
	{
		if (t instanceof IntersectionType)
		{
			IntersectionType it = (IntersectionType) t;
			narrow (it.baseType);
			for (int i = 0; i < it.interfaces.size (); i++)
			{
				addInterface (it.interfaces.get (i));
			}
		}
		else if ((t.getModifiers () & INTERFACE) != 0)
		{
			addInterface (t);
		}
		else
		{
			narrow (t);
		}
	}

	
	public Type<?> simplify ()
	{
		switch (interfaces.size)
		{
			case 0:
				return baseType;
			case 1:
				return Reflection.equal (baseType, Type.OBJECT)
					? getDeclaredInterface (0) : this;
			default:
				return this;
		}
	}


	@Override
	public Class<Object> getImplementationClass ()
	{
		return (((interfaces.size () == 1) && (Reflection.equal (baseType, Type.OBJECT)))
				? getDeclaredInterface (0) : baseType).getImplementationClass ();
	}


	public Type<?> getBinaryType ()
	{
		return ((interfaces.size () == 1) && (Reflection.equal (baseType, Type.OBJECT)))
			? getDeclaredInterface (0) : baseType;
	}


	@Override
	public String getDescriptor ()
	{
		return 'L' + getBinaryName ().replace ('.', '/') + ';';
	}


	@Override
	public String getName ()
	{
		if (interfaces.isEmpty ())
		{
			return baseType.getName ();
		}
		StringBuffer b = new StringBuffer ().append ('(').append (baseType.getName ());
		for (int i = 0; i < interfaces.size; i++)
		{
			b.append (" & ").append (getDeclaredInterface (i).getName ());
		}
		return b.append (')').toString ();
	}


	@Override
	public String getSimpleName ()
	{
		if (interfaces.isEmpty ())
		{
			return baseType.getSimpleName ();
		}
		StringBuffer b = new StringBuffer ().append ('(').append (baseType.getSimpleName ());
		for (int i = 0; i < interfaces.size; i++)
		{
			b.append (" & ").append (getDeclaredInterface (i).getSimpleName ());
		}
		return b.append (')').toString ();
	}


	@Override
	public String getBinaryName ()
	{
		StringBuffer b = new StringBuffer (baseType.getBinaryName ());
		for (int i = 0; i < interfaces.size; i++)
		{
			b.append ('&').append (getDeclaredInterface (i).getBinaryName ());
		}
		return b.toString ();
	}

}
