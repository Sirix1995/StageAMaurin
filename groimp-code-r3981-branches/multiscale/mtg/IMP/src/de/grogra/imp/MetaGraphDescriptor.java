
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

package de.grogra.imp;

import de.grogra.graph.*;
import de.grogra.graph.impl.*;

public class MetaGraphDescriptor extends GraphDescriptor
{
	//enh:sco

	@Override
	public Graph getGraph (View view)
	{
		return new GraphFilter (view.getWorkbench ().getRegistry ().getProjectGraph ())
		{
			@Override
			public Object getRoot (String key)
			{
				if (MAIN_GRAPH.equals (key))
				{
					Object r = source.getRoot (GraphManager.META_GRAPH);
					if (r != null)
					{
						return r;
					}
				}
				return super.getRoot (key);
			}

			public void accept (Object startNode, final Visitor visitor,
								ArrayPath placeInPath)
			{
				accept (startNode, visitor, placeInPath, false);
			}
		};
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends GraphDescriptor.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (MetaGraphDescriptor representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, GraphDescriptor.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new MetaGraphDescriptor ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (MetaGraphDescriptor.class);
		$TYPE.validate ();
	}

//enh:end

}
