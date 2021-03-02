
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

package de.grogra.turtle;

import de.grogra.graph.DoubleAttribute;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.vecmath.Matrix34d;

/**
 * The turtle command <code>AdjustLU</code> performs
 * a rotation about the local z-axis (the turtle's head axis)
 * such that the angle between the rotated local y-axis
 * (the turtle's up axis) and the global z-axis becomes minimal.
 * As a consequence, the rotated local x-axis
 * (the turtle's left axis) is horizontal.
 * <br>
 * This corresponds to the turtle command <code>$</code>
 * of the GROGRA software.
 * 
 * @author Ole Kniemeyer
 */
public class AdjustLU extends de.grogra.graph.impl.Node
{

	private static void initType ()
	{
		$TYPE.addAccessor (new AccessorBridge (Attributes.ANGLE));
		$TYPE.addAccessor (new AccessorBridge (Attributes.TRANSFORMATION));
		$TYPE.addDependency (GlobalTransformation.ATTRIBUTE, Attributes.ANGLE);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new AdjustLU ());
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
		return new AdjustLU ();
	}

//enh:end


	public double getAngle (GraphState gs)
	{
		Matrix34d t = GlobalTransformation.getParentValue (this, true, gs, true);
		return Math.atan2 (-t.m20, t.m21);
	}


	@Override
	protected double getDouble (DoubleAttribute a, GraphState gs)
	{
		return (a == Attributes.ANGLE) ? getAngle (gs)
			: super.getDouble (a, gs);
	}


	@Override
	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == Attributes.TRANSFORMATION)
		{
			return RH.$TYPE.getRepresentative ();
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}

}
