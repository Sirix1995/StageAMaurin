
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

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.imp3d.*;

public class Point extends ColoredNull implements Renderable, Pickable
{

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public Point ()
	{
		super ();
		setLayer (4);
	}


	public Point (float x, float y, float z)
	{
		this ();
		setTransform (x, y, z);
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d transformation, de.grogra.imp.PickList list)
	{
		PickRayVisitor.pickPoint (origin, direction, transformation, list, 8);
	}

	
	private static Tuple3f ZERO = new Point3f ();

	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawPoint (ZERO, 10, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				rs.drawPoint (ZERO, 10, (Tuple3f) gs.checkObject (this, true, Attributes.COLOR, color),
							  RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			rs.drawPoint (ZERO, 10, (Tuple3f) gs.getObject (object, asNode, Attributes.COLOR),
						  RenderState.CURRENT_HIGHLIGHT, null);
		}
	}
//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Point ());
		initType ();
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
		return new Point ();
	}

//enh:end

}
