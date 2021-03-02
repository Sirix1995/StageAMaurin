
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

package de.grogra.rgg;

import de.grogra.pf.registry.*;
import de.grogra.graph.GraphState;
import de.grogra.graph.Cache.Entry;
import de.grogra.math.*;

public class SurfaceRef extends ItemReference
	implements BSplineSurface
{

	SurfaceRef ()
	{
		this (null);
	}


	public SurfaceRef (String name)
	{
		super (name);
	}


	public synchronized BSplineSurface resolve ()
	{
		return (BSplineSurface)
			(objectResolved ? object
			 : resolveObject ("/objects/math/surfaces", Registry.current ()));
	}


	public int getUDegree (GraphState gs)
	{
		return resolve ().getUDegree (gs);
	}


	public int getVDegree (GraphState gs)
	{
		return resolve ().getVDegree (gs);
	}


	public boolean isRational (GraphState gs)
	{
		return resolve ().isRational (gs);
	}


	public int getUSize (GraphState gs)
	{
		return resolve ().getUSize (gs);
	}


	public int getVSize (GraphState gs)
	{
		return resolve ().getVSize (gs);
	}


	public int getDimension (GraphState gs)
	{
		return resolve ().getDimension (gs);
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		return resolve ().getVertex (out, index, gs);
	}


	public boolean dependsOnContext ()
	{
		return resolve ().dependsOnContext ();
	}


	public void writeStamp (Entry cache, GraphState gs)
	{
		resolve ().writeStamp (cache, gs);
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return resolve ().getKnot (dim, index, gs);
	}


	public int getVertexIndex (int u, int v, GraphState gs)
	{
		return resolve ().getVertexIndex (u, v, gs);
	}

	@Override
	public Object manageableWriteReplace ()
	{
		return resolve ();
	}

	//enh:sco
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends ItemReference.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SurfaceRef representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ItemReference.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SurfaceRef ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SurfaceRef.class);
		$TYPE.validate ();
	}

//enh:end

}
