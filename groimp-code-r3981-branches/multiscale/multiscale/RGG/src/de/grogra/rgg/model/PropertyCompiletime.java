
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

package de.grogra.rgg.model;

import java.util.HashMap;

import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeDecorator;
import de.grogra.reflect.XField;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.impl.property.CompiletimeModel;
import de.grogra.xl.impl.property.RuntimeModel;

public class PropertyCompiletime extends CompiletimeModel
{
	static final PropertyCompiletime INSTANCE = new PropertyCompiletime ();

	public class GraphProperty extends PropertyImpl
	{
		final ManageableType.Field field;


		GraphProperty (Type<?> type, ManageableType.Field field, String id)
		{
			super (type, id);
			this.field = field;
		}


		GraphProperty (PersistenceField field, String id)
		{
			super (resolveType (field.getType ()), id);
			this.field = field.getLastField ();
		}


		@Override
		protected PropertyImpl getSubProperty (String name, String id)
		{
			PersistenceField f;
			if ((getType () instanceof ManageableType)
				&& (((f = ((ManageableType) getType ()).getManagedField (name)) != null)
					|| ((f = ((ManageableType) getType ()).resolveAliasField (name)) != null)))
			{
				return new GraphProperty (f, id);
			}
			return null;
		}


		@Override
		protected PropertyImpl getComponentProperty (String id)
		{
			ManageableType.Field f = field.getArrayComponent ();
			if (f != null)
			{
				return new GraphProperty (f, id);
			}
			return null;
		}


		@Override
		protected PropertyImpl getTypeCastProperty (Type<?> type, String id)
		{
			return new GraphProperty (resolveType (type), field, id);
		}

		public Type<? extends de.grogra.xl.property.RuntimeModel.Property> getRuntimeType ()
		{
			if (Reflection.isPrimitive (getType ()))
			{
				return RuntimeModel.getInterface (getType ());
			}
			return RuntimeModel.getInterface (Type.OBJECT);
		}
		
		// TODO
		public Type getDeclaringType() {
			return field.getDeclaringType();
		}
	}


	public PropertyCompiletime (String runtimeConstant)
	{
		super (runtimeConstant);
	}

	
	public PropertyCompiletime ()
	{
		this (PropertyRuntime.class.getName ());
	}

	
	private HashMap<CClass,NType> ntypes = new HashMap<CClass,NType> ();
	

	Type<?> resolveType (Type<?> type)
	{
		type = TypeDecorator.undecorate (Reflection.getBinaryType (type));
		if (!(type instanceof CClass))
		{
			return PropertyRuntime.resolveType (type);
		}
		CClass t = (CClass) type;
		NType n = ntypes.get (t);
		if (n == null)
		{
			Type s = resolveType (t.getSupertype ());
			if (!(s instanceof NType))
			{
				return (s instanceof ManageableType) ? s : PropertyRuntime.resolveType (type);
			}
			n = NType.create (t, (NType) s);
			for (int i = 0; i < t.getDeclaredFieldCount (); i++)
			{
				XField f = (XField) t.getDeclaredField (i);
				int m = f.getModifiers ();
				if ((m & (Member.FINAL | Member.STATIC | Member.TRANSIENT)) == 0)
				{
					Type ct = f.getType ();
					while (Reflection.isArray (ct))
					{
						ct = ct.getComponentType ();
					}
					m |= Reflection.isSuperclassOrSame (Node.$TYPE, ct)
						? NType.Field.FCO : NType.Field.SCO;
					n.addManagedField (new NType.Field (n, f, m));
				}
			}
			n.validate ();
			ntypes.put (t, n);
		}
		return n;
	}


	@Override
	protected PropertyImpl getDirectProperty (Type type, String name, String id)
	{
		type = resolveType (type);
		PersistenceField f;
		if ((type instanceof Node.NType)
			&& (((f = ((Node.NType) type).getManagedField (name)) != null)
				|| ((f = ((Node.NType) type).resolveAliasField (name)) != null)))
		{
			return new GraphProperty (f, id);
		}
		return null;
	}


}
