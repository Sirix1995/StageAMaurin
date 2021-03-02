
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

package de.grogra.imp3d.ray;

import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.shading.*;

public class Tree extends SceneTreeWithShader
{

	public Tree (View3D scene)
	{
		super (scene);
	}


	protected boolean acceptLeaf(Object object, boolean asNode)
	{
		//System.out.println("Tree::acceptLeaf("+object+","+asNode+")");
		Object shape = getGraphState().getObjectDefault
			(object, asNode, Attributes.SHAPE, null);
		//System.out.println("shape:"+shape);
		if ((shape instanceof Raytraceable) || (shape instanceof Sky))
		{
			return true;
		}
		Light light = (Light) getGraphState ().getObjectDefault
			(object, asNode, Attributes.LIGHT, null);
		//System.out.println("light:"+light);
		if (light != null)
		{
			return true;
		}
		return false;
		//return true;
	}


	protected SceneTree.Leaf createLeaf(Object object, boolean asNode, long id)
	{
		Object shape = getGraphState ().getObjectDefault
			(object, asNode, Attributes.SHAPE, null);
		Light light = (Light) getGraphState ().getObjectDefault
			(object, asNode, Attributes.LIGHT, null);
		RaytracerLeaf leaf;
		if ((light!=null) && (light instanceof Raytraceable)) {
			leaf = ((Raytraceable) light).
				createRaytracerLeaf(object, asNode, id, getGraphState ());
		} else if (shape instanceof Raytraceable) {
			leaf = ((Raytraceable) shape).
				createRaytracerLeaf(object, asNode, id, getGraphState ());
		} else {
			leaf = new RaytracerLeaf(object, asNode, id);
		}
		init(leaf);
		return leaf;
	}


	protected InnerNode createInnerNode()
	{
		return new InnerNode();
	}

}
