
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

package de.grogra.pf.registry;

import de.grogra.pf.boot.Main;
import de.grogra.reflect.*;

public class TypeItem extends ObjectItem
{
	private Type type;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new TypeItem ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new TypeItem ();
	}

//enh:end

	private TypeItem ()
	{
		this (null, (Type) null); 
	}


	public TypeItem (String key, Type type)
	{
		super (key);
		this.type = type;
	}


	public TypeItem (Type type)
	{
		this (type.getBinaryName (), type);
	}


	public TypeItem (String key, Class cls)
	{
		this (key, ClassAdapter.wrap (cls));
	}


	@Override
	public Type getObjectType ()
	{
		return Type.TYPE;
	}


	@Override
	public Object getObject ()
	{
		return type;
	}


	@Override
	public boolean isObjectFetched ()
	{
		return true;
	}


	public boolean hasSupertype (String supertype)
	{
		return Reflection.isSupertypeOrSame (supertype, type);
	}


	@Override
	protected void activateImpl ()
	{
		Method m;
		if (((m = Reflection.findMethodWithPrefixInTypes
			  (type, "mstartup;(Lde/grogra/pf/registry/Registry;"
			   + "Lde/grogra/reflect/Type;)V", false, true)) != null)
			&& Reflection.isStatic (m))
		{
			try
			{
				m.invoke (null, new Object[] {getRegistry (), type});
			}
			catch (Exception e)
			{
				Main.logSevere (e.getCause ());
			}
		}
	}


	@Override
	protected void deactivateImpl ()
	{
		Method m;
		if (((m = Reflection.findMethodWithPrefixInTypes
			  (type, "mshutdown;(Lde/grogra/pf/registry/Registry;"
			   + "Lde/grogra/reflect/Type;)V", false, true)) != null)
			&& Reflection.isStatic (m))
		{
			try
			{
				m.invoke (null, new Object[] {getRegistry (), type});
			}
			catch (Exception e)
			{
				Main.logSevere (e.getCause ());
			}
		}
	}


	@Override
	public void addPluginPrerequisites (java.util.Collection list)
	{
		super.addPluginPrerequisites (list);
		addPluginPrerequisite (list, type.getImplementationClass ());
	}

}
