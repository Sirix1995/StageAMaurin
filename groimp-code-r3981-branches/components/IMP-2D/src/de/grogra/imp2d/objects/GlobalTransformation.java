
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

package de.grogra.imp2d.objects;

import javax.vecmath.Matrix3d;
import de.grogra.graph.*;
import de.grogra.imp.objects.Matrix3dAttribute;

public final class GlobalTransformation extends ObjectTreeAttribute
{
	public static final GlobalTransformation ATTRIBUTE
		= new GlobalTransformation ();


	private static final Matrix3d[] INITIAL_VALUE
		= {Matrix3dAttribute.IDENTITY, Matrix3dAttribute.IDENTITY};


	private GlobalTransformation ()
	{
		super (Matrix3d[].class, false, null);
		initializeName ("de.grogra.imp2d.globalTransformation");
	}


	@Override
	protected Object derive (Object object, boolean asNode, Object parentValue,
							 Object placeIn, GraphState gs)
	{
		Matrix3d[] m;
		if (placeIn == null)
		{
			m = new Matrix3d[] {new Matrix3d (), new Matrix3d ()};
			m[0].setIdentity ();
			m[1].setIdentity ();
		}
		else
		{
			m = (Matrix3d[]) placeIn;
		}
		int i = gs.getInstancingPathIndex ();
		Matrix3d p = ((Matrix3d[]) parentValue)
			[(i <= 0) || asNode || !gs.getInstancingPath ().isInstancingEdge (i)
			 ? 1 : 0];
		Transformation t;
		if ((object != null)
			&& ((t = (Transformation) gs.getObjectDefault
				 (object, asNode, Attributes.TRANSFORMATION, null)) != null))
		{
			t.preTransform (object, asNode, p, m[0], gs);
			t.postTransform (object, asNode, m[0], m[1], p, gs);
		}
		else
		{
			m[0].set (p);
			m[1].set (p);
		}
		return m;
	}


	@Override
	public boolean dependsOn (Attribute[] a)
	{
		return Attributes.TRANSFORMATION.isContained (a);
	}


	@Override
	protected Object getInitialValue (GraphState gs)
	{
		return INITIAL_VALUE;
	}


	public static Matrix3d get (Object object, boolean asNode, GraphState gs, boolean post)
	{
		return ((Matrix3d[]) ATTRIBUTE.getDerived (object, asNode, null, gs))
			[post ? 1 : 0];
	}


	public static Matrix3d getParentValue (Object object, boolean asNode, GraphState gs, boolean post)
	{
		return ((Matrix3d[]) ATTRIBUTE.getParentValue (object, asNode, null, gs))[post ? 1 : 0];
	}
}
