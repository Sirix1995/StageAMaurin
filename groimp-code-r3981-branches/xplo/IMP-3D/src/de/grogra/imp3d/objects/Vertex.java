
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

package de.grogra.imp3d.objects;

import de.grogra.graph.GraphState;
import de.grogra.math.UniformScale;
import de.grogra.vecmath.Matrix34d;

public class Vertex extends Point implements VertexSequence.Vertex
{

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (Vertex.$TYPE, transform$FIELD.concat (UniformScale.scale$FIELD));
		}

		public static void signature (@In @Out Vertex v, float s)
		{
		}
	}


	public Vertex ()
	{
		super ();
	}


	public Vertex (float scale)
	{
		this ();
		setTransform (new UniformScale (scale));
		setTransforming (false);
	}

	
	public Matrix34d getVertexTransformation (Object node, GraphState gs)
	{
		return GlobalTransformation.get (node, true, gs, false);
	}

//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Vertex ());
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
		return new Vertex ();
	}

//enh:end


}
