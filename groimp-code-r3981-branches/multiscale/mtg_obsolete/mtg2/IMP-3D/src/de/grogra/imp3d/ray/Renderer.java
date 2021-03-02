
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

import java.util.Stack;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.imp3d.objects.SceneTree.Node;
import de.grogra.imp3d.objects.SceneTree;
import de.grogra.util.Map;


public class Renderer extends de.grogra.imp.Renderer
{
	private Tree tree;
	private Thread thread;
	private Map params;
	private SetTransformVisitor m_setTransformVisitor = 
		new SetTransformVisitor();


	public Renderer (Map params)
	{
		this.params = params;
	}

	
	public String getName ()
	{
		return "Raytracer";
	}


	protected void initializeImpl ()
	{
		tree = new Tree ((View3D) view);
	}


	public void render ()
	{
		Camera c = ((View3D) view).getCamera ();
		
		
		Node root = tree.createTree (true);
		root.accept(m_setTransformVisitor);

		thread = new Thread
			(new GroIMPRaytracer (view.getWorkbench (), params, tree, width, height, this, c),
			 getName ());
		thread.setPriority (Thread.MIN_PRIORITY);
		thread.start ();
	}
	
	
	public synchronized void dispose ()
	{
		if (thread != null)
		{
			Thread t = thread;
			thread = null;
			t.interrupt ();
		}
	}
	
	
	private class SetTransformVisitor implements SceneTree.Visitor {

		private Stack m_transformationStack = new Stack();

		
		public void visitEnter(InnerNode node) {
			
			if (!m_transformationStack.isEmpty()) {
				Matrix4d local = (Matrix4d)m_transformationStack.peek();
				Matrix4d global = new Matrix4d();
				node.transform(local,global);
				global.m30 = 0.0;
				global.m31 = 0.0;
				global.m32 = 0.0;
				global.m33 = 1.0;
				m_transformationStack.push(global);
			} else {
				Matrix4d local = new Matrix4d();
				node.get(local);
				m_transformationStack.push(local);
			}			
			
		}

		
		public void visitLeave(InnerNode node) {
			m_transformationStack.pop();
		}

		
		public void visit(Leaf node) {
			if (!(node instanceof RaytracerLeaf)) { return; }
			RaytracerLeaf rt_node = (RaytracerLeaf)node;
			Matrix4d mat = (Matrix4d)m_transformationStack.peek();
			rt_node.setTransformation(new Matrix4f(mat));
		}
		
	}

}
