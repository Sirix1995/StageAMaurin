
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

import javax.vecmath.*;

import de.grogra.graph.*;
import de.grogra.imp3d.objects.*;

public abstract class TurtleStep extends AxisBase
{

	private static void initType ()
	{
		$TYPE.addAccessor (new AccessorBridge (Attributes.LENGTH));
		$TYPE.addDependency (Attributes.ARGUMENT, Attributes.TURTLE_MODIFIER);
		$TYPE.addDependency (Attributes.LENGTH, Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (TurtleStep.class);
		initType ();
		$TYPE.validate ();
	}

//enh:end

	public abstract float getLength (Object node, GraphState gs);


	@Override
	protected double getDouble (DoubleAttribute a, GraphState gs)
	{
		return (a == Attributes.LENGTH) ? getLength (this, gs)
			: super.getDouble (a, gs);
	}


	@Override
	public void postTransform (Object object, boolean asNode,
							   Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		transform:
		{
			if (object == this) 
			{
				if (gs.getInstancingPathIndex () <= 0)
				{
					if ((bits & TRANSFORMING_MASK) == 0)
					{
						break transform;
					}
				}
				else
				{
					if (!gs.checkBoolean (this, true, Attributes.TRANSFORMING, (bits & TRANSFORMING_MASK) != 0))
					{
						break transform;
					}
				}
			}
			else
			{
				if (!gs.getBoolean (object, asNode, Attributes.TRANSFORMING))
				{
					break transform;
				}
			}
			out.set (in);
			float l = getLength (object, gs);
			if (l != 0)
			{
				out.m03 += l * out.m02;
				out.m13 += l * out.m12;
				out.m23 += l * out.m22;
			}
			return;
		}
		if (out != pre)
		{
			out.set (pre);
		}
	}

}
