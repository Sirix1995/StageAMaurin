
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

package de.grogra.xl.impl.property;

import de.grogra.reflect.*;
import de.grogra.xl.lang.*;
import de.grogra.xl.query.*;

public abstract class CompiletimeModel implements de.grogra.xl.property.CompiletimeModel
{
	public abstract class PropertyImpl implements Property
	{
		final Type<?> type;
		final String id;


		public PropertyImpl (Type<?> type, String id)
		{
			this.type = type;
			this.id = id;
		}


		public PropertyImpl (Class<?> type, String id)
		{
			this (ClassAdapter.wrap (type), id);
		}

		
		public de.grogra.xl.property.CompiletimeModel getModel ()
		{
			return CompiletimeModel.this;
		}

		public Type<?> getType ()
		{
			return type;
		}

		public Property getSubProperty (String name)
		{
			return getSubProperty (name, id + name + ';');
		}


		public Property getComponentProperty ()
		{
			return getComponentProperty (id + "[;");
		}


		public Property getTypeCastProperty (Type<?> type)
		{
			return getTypeCastProperty (type, id + '(' + type.getBinaryName () + ';');
		}

		
		public String getRuntimeName ()
		{
			return id;
		}


		protected abstract PropertyImpl getSubProperty (String name, String id);


		protected abstract PropertyImpl getComponentProperty (String id);


		protected abstract PropertyImpl getTypeCastProperty (Type<?> type, String id);

	}


	private final String runtimeConstant;


	public CompiletimeModel (String runtimeConstant)
	{
		this.runtimeConstant = runtimeConstant;
	}


	public Property getDirectProperty (Type<?> type, String name)
	{
		return getDirectProperty (type, name, type.getBinaryName () + ';' + name + ';');
	}


	protected PropertyImpl getDirectProperty (Type<?> type, String name, String id)
	{
		return null;
	}


	public String getRuntimeName ()
	{
		return runtimeConstant;
	}

}
