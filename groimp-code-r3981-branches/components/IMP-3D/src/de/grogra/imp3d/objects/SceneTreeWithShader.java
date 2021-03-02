
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

import de.grogra.graph.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.shading.*;
import de.grogra.xl.util.ObjectList;

/**
 * This class extends <code>SceneTree</code> and keeps track of the current
 * shader and interior. These additional parameters are stored in the
 * corresponding fields of the <code>Leaf</code> class.
 * 
 * @author Ole Kniemeyer
 */
public abstract class SceneTreeWithShader extends SceneTree
{
	/**
	 * This subclass of <code>SceneTree.Leaf</code> stores the shader
	 * and interior which are associated with the object of the leaf. 
	 * 
	 * @author Ole Kniemeyer
	 */
	public static class Leaf extends SceneTree.Leaf
	{
		/**
		 * The shader of the object of this leaf.
		 */
		public Shader shader;

		/**
		 * The interior of the object of this leaf.
		 */
		public Interior interior;


		public Leaf (Object object, boolean asNode, long pathId)
		{
			super (object, asNode, pathId);
		}
		
		
		public void setShader (Shader shader)
		{
			this.shader = shader;
		}
		
		
		public void setInterior (Interior interior)
		{
			this.interior = interior;
		}
	}


	private ObjectList shaderStack = new ObjectList ().push (RGBAShader.GRAY);
	private ObjectList interiorStack = new ObjectList ().push (null);


	public SceneTreeWithShader (GraphState gs, EdgePattern pattern)
	{
		super (gs, pattern);
	}


	public SceneTreeWithShader (View3D scene)
	{
		super (scene);
	}


	@Override
	protected void init (SceneTree.Leaf leaf)
	{
		super.init (leaf);
		((Leaf) leaf).shader = getCurrentShader ();
		((Leaf) leaf).interior = getCurrentInterior ();
	}


	protected Shader getCurrentShader ()
	{
		return (Shader) shaderStack.peek (1);
	}


	protected Interior getCurrentInterior ()
	{
		return (Interior) interiorStack.peek (1);
	}

	protected Shader resolveShader (Shader shader)
	{
		return (shader instanceof AlgorithmSwitchShader)
			? ((AlgorithmSwitchShader) shader).getRaytracerShader ()
			: (shader instanceof ShaderRef)
			? ((ShaderRef) shader).resolve ()
			: shader;
	}

	@Override
	Object visitEnter (Object object, boolean asNode, long id, Path path)
	{
		Shader sh = (Shader) shaderStack.peek (1);
		Shader s = (object == null) ? null : (Shader) state.getObjectDefault
			(object, asNode, de.grogra.imp3d.objects.Attributes.SHADER, sh);
		Shader s2;
		while ((s2 = resolveShader (s)) != s)
		{
			s = s2;
		}
		shaderStack.push ((s != null) ? s : sh);
		Interior oi = (Interior) interiorStack.peek (1);
		Interior i = (object == null) ? null : (Interior) state.getObjectDefault
			(object, asNode, de.grogra.imp3d.objects.Attributes.INTERIOR, oi);
		interiorStack.push ((i != null) ? i : oi);
		return super.visitEnter (object, asNode, id, path);
	}


	@Override
	public boolean visitLeave (Object o, Path path, boolean node)
	{
		boolean b = super.visitLeave (o, path, node);
		if (node)
		{
			shaderStack.pop ();
			interiorStack.pop ();
		}
		return b;
	}

}
