
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

package de.grogra.imp3d;

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.imp3d.shading.AlgorithmSwitchShader;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.ShaderRef;

/**
 * This abstract visitor is used as base class 
 * 
 * @author Ole Kniemeyer
 */
public abstract class DisplayVisitor extends Visitor3D
{
	protected Object lastEntered;
	protected boolean lastEnteredIsNode;

	private ObjectList shaderStack = new ObjectList ();
	private Shader shader;
	private ViewConfig3D view;
	private boolean checkLayer;


	public void init (GraphState gs, Matrix4d t, ViewConfig3D view, boolean checkLayer)
	{
		init (gs, gs.getGraph ().getTreePattern (), t);
		shaderStack.clear ();
		shader = RGBAShader.GRAY;
		this.view = view;
		this.checkLayer = checkLayer;
	}

	
	public Shader getCurrentShader ()
	{
		return shader;
	}

	protected Shader resolveShader (Shader shader)
	{
		return (shader instanceof AlgorithmSwitchShader)
			? ((AlgorithmSwitchShader) shader).getGUIShader ()
			: (shader instanceof ShaderRef)
			? ((ShaderRef) shader).resolve ()
			: shader;
	}

	protected boolean isInVisibleLayer (Object o, boolean asNode)
	{
		return !checkLayer || view.isInVisibleLayer (o, asNode, state);
	}

	@Override
	protected void visitEnterImpl (Object object, boolean asNode, Path path)
	{
		lastEntered = object;
		lastEnteredIsNode = asNode;
		shaderStack.push (shader);
		Shader s = (Shader) state.getObjectDefault
			(object, asNode, de.grogra.imp3d.objects.Attributes.SHADER, shader);
		Shader s2;
		while ((s2 = resolveShader (s)) != s)
		{
			s = s2;
		}
		if (s != null)
		{
			shader = s;
		}
		else
		{
			s = shader;
		}
		if (isInVisibleLayer (object, asNode))
		{
			visitImpl (object, asNode, s, path);
		}
	}

	
	protected abstract void visitImpl (Object object, boolean asNode, Shader s, Path path);
	
	
	@Override
	protected void visitLeaveImpl (Object object, boolean asNode, Path path)
	{
		shader = (Shader) shaderStack.pop ();
	}
}
