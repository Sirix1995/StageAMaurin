
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

package de.grogra.xl.impl.dom;

import org.w3c.dom.Element;

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.impl.property.CompiletimeModel;
import de.grogra.xl.util.Variant;

public class PropertyCompiletimeModel extends CompiletimeModel
{
	static final Type VARIANT = ClassAdapter.wrap (Variant.class);
	static final Type RUNTIME_TYPE = ClassAdapter.wrap (PropertyRuntimeModel.ElementProperty.class);

	class AttributeProperty extends PropertyImpl
	{
		AttributeProperty (Type type, String id)
		{
			super (type, id);
		}

		@Override
		protected PropertyImpl getSubProperty (String name, String id)
		{
			return null;
		}

		@Override
		protected PropertyImpl getComponentProperty (String id)
		{
			return null;
		}

		@Override
		protected PropertyImpl getTypeCastProperty (Type type, String id)
		{
			return new AttributeProperty (type, id);
		}

		public Type getRuntimeType ()
		{
			return RUNTIME_TYPE;
		}
	}

	public PropertyCompiletimeModel ()
	{
		super (PropertyRuntimeModel.class.getName ());
	}


	@Override
	protected PropertyImpl getDirectProperty (Type type, String name, String id)
	{
		if (Reflection.isSupertypeOrSame (Element.class, type))
		{
			return new AttributeProperty (VARIANT, id);
		}
		return null;
	}

}
