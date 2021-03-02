
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

package de.grogra.imp2d.graphs;

import javax.vecmath.Matrix3d;
import de.grogra.graph.*;
import de.grogra.imp2d.objects.*;
import de.grogra.math.TVector2d;

public final class ObjectData extends TVector2d implements Transformation
{
	//enh:sco de.grogra.math.Tuple2dType

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends de.grogra.math.Tuple2dType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ObjectData representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.math.Tuple2dType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ObjectData ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ObjectData.class);
		$TYPE.validate ();
	}

//enh:end

	public ObjectData ()
	{
		super (Math.random () * 5, Math.random () * 5);
	}

	
	public void preTransform (Object object, boolean asNode,
							  Matrix3d in, Matrix3d out, GraphState gs)
	{
		transform (in, out);
	}


	public void postTransform (Object object, boolean asNode,
							   Matrix3d in, Matrix3d out, Matrix3d pre,
							   GraphState gs)
	{
		out.set (pre);
	}
	
	
	@Override
	public String toString ()
	{
		return x + " " + y;
	}
	
	
	public static ObjectData valueOf (String s)
	{
		ObjectData d = new ObjectData ();
		int p = 0, n = s.length (), index = 0;
		while (p < n)
		{
			int i = s.indexOf (' ', p);
			if (i < 0)
			{
				i = n;
			}
			if (i > p)
			{
				switch (index++)
				{
					case 0:
						d.x = Float.parseFloat (s.substring (p, i));
						break;
					case 1:
						d.y = Float.parseFloat (s.substring (p, i));
						break;
				}
			}
			p = i + 1;
		}
		return d;
	}
}
