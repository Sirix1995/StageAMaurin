
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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.ByteAttribute;
import de.grogra.graph.CharAttribute;
import de.grogra.graph.DoubleAttribute;
import de.grogra.graph.EdgePattern;
import de.grogra.graph.FloatAttribute;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.LongAttribute;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.Path;
import de.grogra.graph.ShortAttribute;
import de.grogra.imp.View;
import de.grogra.imp3d.View3D;
import de.grogra.vecmath.Math2;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>SceneTree</code> is a simplified image of a
 * {@link de.grogra.graph.Graph} designed for usage in the context
 * of 3D analysis (e.g., export). The nodes of such a tree are created
 * by invocation of {@link #createTree}, this in turn uses the factory
 * methods {@link #createInnerNode()}
 * and {@link #createLeaf(Object, boolean, long)}.
 * 
 * @author Ole Kniemeyer
 */
public abstract class SceneTree implements de.grogra.graph.Visitor, TreeModel
{
	/**
	 * The abstract base class for nodes of a <code>SceneTree</code>.
	 * 
	 * @author Ole Kniemeyer
	 */
	public static abstract class Node
	{
		/**
		 * The next node in the linked list of siblings (same level of hierarchy).
		 */
		public Node next;
		
		public abstract void accept (Visitor visitor);

		abstract boolean hasContent ();
	}


	/**
	 * This represents a leaf in a <code>SceneTree</code>. A leaf contains
	 * a reference to an object of the graph for which the method
	 * {@link SceneTree#acceptLeaf(Object, boolean)} has returned
	 * <code>true</code>. E.g., think of all nodes having a 3D shape.
	 * 
	 * @author Ole Kniemeyer
	 */
	public static class Leaf extends Node
	{
		/**
		 * The object of the original graph for which this leaf has
		 * been created. 
		 */
		public final Object object;

		/**
		 * Is <code>object</code> a node or an edge?
		 */
		public final boolean asNode;

		public final long pathId;
		
		GraphState state;
		

		public Leaf (Object object, boolean asNode, long pathId)
		{
			this.object = object;
			this.asNode = asNode;
			this.pathId = pathId;
		}

		
		@Override
		public final void accept (Visitor visitor)
		{
			visitor.visit (this);
		}


		@Override
		boolean hasContent ()
		{
			return true;
		}


		@Override
		public String toString ()
		{
			return super.toString () + '[' + object + ']';
		}
/*!!
#foreach ($type in $types)
$pp.setType($type)

		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 $C
		public $type get$pp.Type (${pp.Type}Attribute a)
		{
			return state.get$pp.Type (object, asNode, a);
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public boolean getBoolean (BooleanAttribute a)
		{
			return state.getBoolean (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public byte getByte (ByteAttribute a)
		{
			return state.getByte (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public short getShort (ShortAttribute a)
		{
			return state.getShort (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public char getChar (CharAttribute a)
		{
			return state.getChar (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public int getInt (IntAttribute a)
		{
			return state.getInt (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public long getLong (LongAttribute a)
		{
			return state.getLong (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public float getFloat (FloatAttribute a)
		{
			return state.getFloat (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public double getDouble (DoubleAttribute a)
		{
			return state.getDouble (object, asNode, a);
		}
// generated
// generated
		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param a the attribute to read
		 */
		public Object getObject (ObjectAttribute a)
		{
			return state.getObject (object, asNode, a);
		}
//!! *# End of generated code

		/**
		 * Returns the value of attribute <code>a</code> for the
		 * <code>object</code> of this leaf.
		 * 
		 * @param placeIn an instance for the result may be provided by the caller 
		 * @param a the attribute to read
		 */
		public Object getObject (Object placeIn, ObjectAttribute a)
		{
			return state.getObject (object, asNode, placeIn, a);
		}
	}


	/**
	 * This class represents an inner node of a scene tree. Inner nodes
	 * have a set of {@link #children}, stored as a linked list of
	 * {@link Node}s, and specify a coordinate transformation by the
	 * fields {@link #m00} to {@link #m23}. The coordinate transformation
	 * transforms from the children's coordinate systems to the coordinate
	 * system of this inner node. 
	 * 
	 * @author Ole Kniemeyer
	 */
	public static class InnerNode extends Node
	{
		/**
		 * The first node of the linked list of children.
		 */
		public Node children;

		public static final int CSG_MASK = 7;
		public static final int IS_GROUP = 8;
		public static final int HAS_MORE_THAN_ONE_CHILD = 16;

		/**
		 * Stores flags {@link #IS_GROUP},
		 * {@link #HAS_MORE_THAN_ONE_CHILD} as well as the
		 * the CSG operation performed by this node, one
		 * of the <code>CSG</code> constants in {@link Attributes}.
		 */
		public int flags = Attributes.CSG_NONE & CSG_MASK;

		/**
		 * 00-component of the local transformation matrix.
		 */
		public float m00;

		/**
		 * 01-component of the local transformation matrix.
		 */
		public float m01;

		/**
		 * 02-component of the local transformation matrix.
		 */
		public float m02;

		/**
		 * 03-component of the local transformation matrix.
		 */
		public float m03;

		/**
		 * 10-component of the local transformation matrix.
		 */
		public float m10;

		/**
		 * 11-component of the local transformation matrix.
		 */
		public float m11;

		/**
		 * 12-component of the local transformation matrix.
		 */
		public float m12;

		/**
		 * 13-component of the local transformation matrix.
		 */
		public float m13;

		/**
		 * 20-component of the local transformation matrix.
		 */
		public float m20;

		/**
		 * 21-component of the local transformation matrix.
		 */
		public float m21;

		/**
		 * 22-component of the local transformation matrix.
		 */
		public float m22;

		/**
		 * 23-component of the local transformation matrix.
		 */
		public float m23;


		public InnerNode ()
		{
			m00 = m11 = m22 = 1;
		}

		@Override
		public final void accept (Visitor visitor)
		{
			visitor.visitEnter (this);
			for (Node n = children; n != null; n = n.next)
			{
				n.accept (visitor);
			}
			visitor.visitLeave (this);
		}


		/**
		 * Sets the transformation of this inner node to <code>m</code>.
		 * 
		 * @param m affine coordinate transformation
		 */
		public void set (Matrix4d m)
		{
			m00 = (float) m.m00;
			m01 = (float) m.m01;
			m02 = (float) m.m02;
			m03 = (float) m.m03;
			m10 = (float) m.m10;
			m11 = (float) m.m11;
			m12 = (float) m.m12;
			m13 = (float) m.m13;
			m20 = (float) m.m20;
			m21 = (float) m.m21;
			m22 = (float) m.m22;
			m23 = (float) m.m23;
		}
		
		
		/**
		 * Gets the transformation of this inner node and writes
		 * it to <code>out</code>.
		 * 
		 * @param out the transformation is written to this instance 
		 */
		public void get (Matrix4d out)
		{
			out.m00 = m00;
			out.m01 = m01;
			out.m02 = m02;
			out.m03 = m03;
			out.m10 = m10;
			out.m11 = m11;
			out.m12 = m12;
			out.m13 = m13;
			out.m20 = m20;
			out.m21 = m21;
			out.m22 = m22;
			out.m23 = m23;
			out.m30 = 0;
			out.m31 = 0;
			out.m32 = 0;
			out.m33 = 1;
		}


		/**
		 * Multiplies <code>in</code> from right by the transformation of
		 * this node and stores the result in <code>out</code>.
		 * 
		 * @param in input transformation
		 * @param out output transformation (may be <code>in</code>)
		 */
		public void transform (Matrix4d in, Matrix4d out)
		{
			double d00, d01, d02, d10, d11, d12, d20, d21, d22, v0, v1, v2;
			out.m03 = (d00 = in.m00) * (v0 = m03) + (d01 = in.m01) * (v1 = m13)
				+ (d02 = in.m02) * (v2 = m23) + in.m03;
			out.m13 = (d10 = in.m10) * v0 + (d11 = in.m11) * v1
				+ (d12 = in.m12) * v2 + in.m13;
			out.m23 = (d20 = in.m20) * v0 + (d21 = in.m21) * v1
				+ (d22 = in.m22) * v2 + in.m23;

			out.m00 = d00 * (v0 = m00) + d01 * (v1 = m10) + d02 * (v2 = m20);
			out.m10 = d10 * v0 + d11 * v1 + d12 * v2;
			out.m20 = d20 * v0 + d21 * v1 + d22 * v2;

			out.m01 = d00 * (v0 = m01) + d01 * (v1 = m11) + d02 * (v2 = m21);
			out.m11 = d10 * v0 + d11 * v1 + d12 * v2;
			out.m21 = d20 * v0 + d21 * v1 + d22 * v2;

			out.m02 = d00 * (v0 = m02) + d01 * (v1 = m12) + d02 * (v2 = m22);
			out.m12 = d10 * v0 + d11 * v1 + d12 * v2;
			out.m22 = d20 * v0 + d21 * v1 + d22 * v2;
		}


		/**
		 * Transforms the vector <code>t</code> by the rotational
		 * component of this node's transformation.
		 * 
		 * @param t a vector to be transformed
		 */
		public void transformVector (Tuple3f t)
		{
			float x = t.x, y = t.y, z = t.z;
			t.x = m00 * x + m01 * y + m02 * z;
			t.y = m10 * x + m11 * y + m12 * z;
			t.z = m20 * x + m21 * y + m22 * z;
		}


		/**
		 * Transforms the vector <code>t</code> by the rotational
		 * component of this node's transformation.
		 * 
		 * @param t a vector to be transformed
		 */
		public void transformVector (Tuple3d t)
		{
			double x = t.x, y = t.y, z = t.z;
			t.x = m00 * x + m01 * y + m02 * z;
			t.y = m10 * x + m11 * y + m12 * z;
			t.z = m20 * x + m21 * y + m22 * z;
		}


		/**
		 * Transforms the point <code>t</code> by this
		 * node's affine transformation.
		 * 
		 * @param t a point to be transformed
		 */
		public void transformPoint (Tuple3f t)
		{
			float x = t.x, y = t.y, z = t.z;
			t.x = m00 * x + m01 * y + m02 * z + m03;
			t.y = m10 * x + m11 * y + m12 * z + m13;
			t.z = m20 * x + m21 * y + m22 * z + m23;
		}


		/**
		 * Transforms the point <code>t</code> by this
		 * node's affine transformation.
		 * 
		 * @param t a point to be transformed
		 */
		public void transformPoint (Tuple3d t)
		{
			double x = t.x, y = t.y, z = t.z;
			t.x = m00 * x + m01 * y + m02 * z + m03;
			t.y = m10 * x + m11 * y + m12 * z + m13;
			t.z = m20 * x + m21 * y + m22 * z + m23;
		}


		void add (Node node)
		{
			node.next = children;
			children = node;
		}


		void simplify ()
		{
			Node p = null;
			for (Node n = children; n != null; n = n.next)
			{
				if (n.hasContent ())
				{
					p = n;
				}
				else
				{
					if (p == null)
					{
						children = n.next;
					}
					else
					{
						p.next = n.next;
					}
				}
			}
			p = children;
			if (p != null)
			{
				if (p.next != null)
				{
					if ((flags & IS_GROUP) != 0)
					{
						flags |= HAS_MORE_THAN_ONE_CHILD;
					}
				}
				else if ((p instanceof InnerNode) && !((InnerNode) p).isSpecial ())
				{
					InnerNode i = (InnerNode) p;
					children = i.children;
					flags |= (i.flags & (IS_GROUP | HAS_MORE_THAN_ONE_CHILD)); 
		
					float x00, x01, x02, x10, x11, x12, x20, x21, x22, v0, v1, v2;
					m03 = (x00 = m00) * (v0 = i.m03) + (x01 = m01) * (v1 = i.m13)
						+ (x02 = m02) * (v2 = i.m23) + m03;
					m13 = (x10 = m10) * v0 + (x11 = m11) * v1
						+ (x12 = m12) * v2 + m13;
					m23 = (x20 = m20) * v0 + (x21 = m21) * v1
						+ (x22 = m22) * v2 + m23;
		
					m00 = x00 * (v0 = i.m00) + x01 * (v1 = i.m10) + x02 * (v2 = i.m20);
					m10 = x10 * v0 + x11 * v1 + x12 * v2;
					m20 = x20 * v0 + x21 * v1 + x22 * v2;
		
					m01 = x00 * (v0 = i.m01) + x01 * (v1 = i.m11) + x02 * (v2 = i.m21);
					m11 = x10 * v0 + x11 * v1 + x12 * v2;
					m21 = x20 * v0 + x21 * v1 + x22 * v2;
		
					m02 = x00 * (v0 = i.m02) + x01 * (v1 = i.m12) + x02 * (v2 = i.m22);
					m12 = x10 * v0 + x11 * v1 + x12 * v2;
					m22 = x20 * v0 + x21 * v1 + x22 * v2;
				}
			}
		}


		@Override
		boolean hasContent ()
		{
			return children != null;
		}

		
		protected boolean isSpecial ()
		{
			return (flags & CSG_MASK) != (Attributes.CSG_NONE & CSG_MASK);
		}

		
		public boolean isProperGroup ()
		{
			return (flags & (IS_GROUP | HAS_MORE_THAN_ONE_CHILD))
				== (IS_GROUP | HAS_MORE_THAN_ONE_CHILD);
		}

		@Override
		public String toString ()
		{
			return "InnerNode[" + flags + ']';
		}
	}


	/**
	 * A <code>Visitor</code> is used to visit a <code>SceneTree</code>.
	 * On invocation of {@link SceneTree#accept(Visitor)}, the complete
	 * tree is passed to the specified visitor. This interface
	 * follows the design pattern "Hierarchical Visitor".
	 * 
	 * @author Ole Kniemeyer
	 */
	public interface Visitor
	{
		/**
		 * Informs the visitor that an inner node has been entered.
		 * 
		 * @param node the node being entered
		 */
		void visitEnter (InnerNode node);

		/**
		 * Informs the visitor that an inner node has been left.
		 * 
		 * @param node the node being left
		 */
 		void visitLeave (InnerNode node);

		/**
		 * Informs the visitor that a leaf is visited.
		 * 
		 * @param node the leaf being visited
		 */
		void visit (Leaf node);
	}


	protected GraphState state;
	private final EdgePattern pattern;
	private View view;

	private final Matrix4d identity = new Matrix4d (), out = new Matrix4d (),
		pre = new Matrix4d (), tmp = new Matrix4d ();
	private boolean preEqOut;
	private boolean simplifyHierarchy;

	/**
	 * Creates a new scene tree instance. The nodes are not created
	 * by the constructor, this is done in {@link #createTree}.
	 * 
	 * @param gs the graph state to use for attribute queries
	 * @param pattern the pattern which defines which edges span
	 * the tree as subgraph of the complete graph
	 */
	public SceneTree (GraphState gs, EdgePattern pattern)
	{
		this.state = gs;
		this.pattern = pattern;
		identity.setIdentity ();
		pre.setIdentity ();
		out.setIdentity ();
	}


	/**
	 * Creates a new scene tree instance. This delegates to
	 * {@link #SceneTree(GraphState, EdgePattern)}, where the parameters
	 * are obtained from the <code>view</code>.
	 * 
	 * @param view view for which the new scene tree is configured
	 */
	public SceneTree (View3D view)
	{
		this (view.getWorkbenchGraphState (),
			  view.getGraph ().getTreePattern ());
		this.view = view;
	}


	public GraphState getGraphState ()
	{
		return state;
	}


	/**
	 * This method has to be invoked whenever a new {@link Leaf} is created
	 * as a leaf of this tree. The tree may perform some initialization tasks
	 * on the <code>leaf</code>.
	 * 
	 * @param leaf a newly created <code>Leaf</code> to be initialized
	 */
	protected void init (Leaf leaf)
	{
		leaf.state = state;
	}

	public Node createTree (boolean simplify)
	{
		return createTree (simplify, false);
	}

	/**
	 * This method creates the nodes of this tree by traversing
	 * the original graph using the edge pattern which has been specified
	 * in the constructor. It collects all objects of the original
	 * graph as leaves for which the method
	 * {@link #acceptLeaf(Object, boolean)} returns <code>true</code>.
	 * The leaves are inserted into a hierarchy of
	 * {@link InnerNode}s corresponding to the hierarchy of the original
	 * graph.
	 * 
	 * @param simplify simplify structure (may destroy hierarchy)? 
	 * @return root node of the created tree
	 * 
	 * @see #createInnerNode()
	 * @see #createLeaf(Object, boolean, long)
	 */
	public Node createTree (boolean simplify, boolean checkLayer)
	{
		simplifyHierarchy = simplify;
		state.getGraph ().accept (null, this, null);
		stack.clear ();
		stack.push (root);
		while (!stack.isEmpty ())
		{
			Node n = stack.pop ();
			if (n instanceof InnerNode)
			{
				InnerNode p = (InnerNode) n;
				int k = stack.size ();
				for (n = p.children; n != null; n = n.next)
				{
					stack.push (n);
				}
				p.children = null;
				for (int i = k; i < stack.size (); i++)
				{
					Node c = stack.get (i);
					if (checkLayer && c instanceof Leaf)
					{
						if (!view.isInVisibleLayer (((Leaf) c).object, true, state))
							continue;
					}
					c.next = p.children;
					p.children = c;
				}
			}
		}
		return root;
	}


	private InnerNode root = createInnerNode (), parent = root;

	public Object getRoot ()
	{
		return root;
	}


	public Object visitEnter (Path path, boolean node)
	{
		if (node)
		{
			return visitEnter (path.getObject (-1), true, path.getObjectId (-1), path);
		}
		else
		{
			if (!GraphUtils.matchesTerminalEdge (pattern, path))
			{
				return STOP;
			}
			return visitEnter (path.getObject (-2), false, path.getObjectId (-2), path);
		}
	}


	Object visitEnter (Object object, boolean asNode, long id, Path path)
	{	
		if (visitor != null)
		{
			if ((id == nextPathId) && (asNode == nextAsNode))
			{
				visitor.visit (nextLeaf);
				findNext ();
			}
			return null;
		}
		Transformation t = (object == null) ? null
			: (Transformation) state.getObjectDefault
			(object, asNode, Attributes.TRANSFORMATION, null);
		boolean f = (object != null) && acceptLeaf (object, asNode);
		boolean preId, postId, totalId;
		if (t == null)
		{
			preId = true;
			postId = true;
			totalId = true;
			preEqOut = true;
		}
		else
		{
			t.preTransform (object, asNode, identity, pre, state);
			t.postTransform (object, asNode, pre, out, identity, state);
			preEqOut = pre.epsilonEquals (out, 0d);
			if (f)
			{
				preId = identity.epsilonEquals (pre, 0d);
				postId = preEqOut;
				totalId = (preId && postId)
					|| (!preId && !postId && identity.epsilonEquals (out, 0d));
			}
			else
			{
				preId = true;
				postId = totalId = identity.epsilonEquals (out, 0d);
			}
		}
		InnerNode nodeParent = null;
		if (f)
		{
			Leaf l = createLeaf (object, asNode, id);
			if (preId && simplifyHierarchy)
			{
				nodeParent = parent;
			}
			else
			{
				nodeParent = createInnerNode ();
				if (!preId)
				{
					nodeParent.set (pre);
				}
				parent.add (nodeParent);
			}
			nodeParent.add (l);
		}
		int csg = (object == null) ? Attributes.CSG_NONE
				: state.getIntDefault (object, asNode, Attributes.CSG_OPERATION, Attributes.CSG_NONE);
		if (simplifyHierarchy && (csg == Attributes.CSG_NONE))
		{
			if (totalId)
			{
				return null;
			}
			else if (postId)
			{
				assert f && !preId;
				InnerNode p = parent;
				parent = nodeParent;
				return p;
			}
		}
		InnerNode n = createInnerNode ();
		if (f && !simplifyHierarchy)
		{
			if (!postId)
			{
				if (preId)
				{
					n.set (out);
				}
				else
				{
					Math2.invertAffine (pre, tmp);
					Math2.mulAffine (tmp, tmp, out);
					n.set (tmp);
				}
			}
			nodeParent.add (n);
		}
		else
		{
			if (!totalId)
			{
				n.set (out);
			}
			parent.add (n);
		}
		n.flags = (csg & InnerNode.CSG_MASK) | InnerNode.IS_GROUP;
		InnerNode p = parent;
		parent = n;
		return p;
	}


	public boolean visitLeave (Object o, Path path, boolean node)
	{
		if (node)
		{
			if (visitor != null)
			{
				return nextNode != null;
			}
			if (o != null)
			{
				parent.simplify ();
				InnerNode p = (InnerNode) o;
				for (Node n = p.children; ; n = n.next)
				{
					if (n == parent)
					{
						break;
					}
					else if ((n instanceof InnerNode)
							 && (((InnerNode) n).children == parent))
					{
						((InnerNode) n).simplify ();
						break;
					}
				}
				parent = (InnerNode) o;
			}
		}
		else
		{
			return (o == STOP) ? ((visitor == null) || (nextNode != null))
				: visitLeave (o, path, true);
		}
		return true;
	}


	public Object visitInstanceEnter ()
	{
		if ((visitor != null) || preEqOut)
		{
			return null;
		}
		InnerNode n = createInnerNode ();
		Math2.invertAffine (out, tmp);
		Math2.mulAffine (tmp, tmp, pre);
		n.set (tmp);
		parent.add (n);
		InnerNode p = parent;
		parent = n;
		return p;
	}


	public boolean visitInstanceLeave (Object o)
	{
		if (visitor != null)
		{
			return nextNode != null;
		}
		if (o != null)
		{
			parent.simplify ();
			parent = (InnerNode) o;
		}
		return true;
	}


	private final ObjectList<Node> stack = new ObjectList<Node> (32);
	private Node cursor;

	private Leaf nextLeaf;
	private Object nextNode;
	private boolean nextAsNode;
	private long nextPathId;
	private Visitor visitor;


	private void findNext ()
	{
		Node n = cursor;
		while (true)
		{
			if (n == null)
			{
				if (stack.isEmpty ())
				{
					nextNode = null;
					cursor = null;
					return;
				}
				n = stack.pop ();
				visitor.visitLeave ((InnerNode) n);
				n = n.next;
			}
			else if (n instanceof InnerNode)
			{
				visitor.visitEnter ((InnerNode) n);
				stack.push (n);
				n = ((InnerNode) n).children;
			}
			else if (n instanceof Leaf)
			{
				nextLeaf = (Leaf) n;
				nextNode = nextLeaf.object;
				nextAsNode = nextLeaf.asNode;
				nextPathId = nextLeaf.pathId;
				cursor = n.next;
				return;
			}
		}
	}


	/**
	 * This method is invoked if one wants to inform the <code>visitor</code>
	 * of the complete set of nodes of this tree. 
	 * 
	 * @param visitor a visitor to visit the nodes of this tree
	 */
	public void accept (Visitor visitor)
	{
		this.visitor = visitor;
		try
		{
			cursor = root;
			findNext ();
			if (nextNode != null)
			{
				state.getGraph ().accept (null, this, null);
			}
		}
		finally
		{
			this.visitor = null;
		}
	}


	public boolean isLeaf (Object node)
	{
		return node instanceof Leaf;
	}


	public Object getChild (Object parent, int index)
	{
		Node c = ((InnerNode) parent).children;
		while (--index >= 0)
		{
			c = c.next;
		}
		return c;
	}


	public int getChildCount (Object parent)
	{
		Node c = ((InnerNode) parent).children;
		int count = 0;
		while (c != null)
		{
			count++;
			c = c.next;
		}
		return count;
	}


	public int getIndexOfChild (Object parent, Object child)
	{
		Node c = ((InnerNode) parent).children;
		if (c == null)
		{
			return -1;
		}
		int index = 0;
		while (c != child)
		{
			index++;
			c = c.next;
			if (c == null)
			{
				return -1;
			}
		}
		return index;
	}


	public void valueForPathChanged (TreePath path, Object newValue)
	{
	}


	public void addTreeModelListener (TreeModelListener l)
	{
		throw new UnsupportedOperationException ();
	}


	public void removeTreeModelListener (TreeModelListener l)
	{
		throw new UnsupportedOperationException ();
	}


	/**
	 * This factory method is used by {@link #createTree} to create
	 * an inner node. 
	 * 
	 * @return newly created inner node
	 */
	protected InnerNode createInnerNode ()
	{
		return new InnerNode ();
	}


	/**
	 * This method defines for which objects of the graph a
	 * {@link Leaf} node shall be created.
	 * 
	 * @param object an object of the graph
	 * @param asNode is object a node or an edge?
	 * @return <code>true</code> iff a leaf shall be created for the object
	 */
	protected abstract boolean acceptLeaf (Object object, boolean asNode);


	/**
	 * This factory method is used by {@link #createTree} to create a leaf.
	 * Note that the implementation has to invoke {@link #init(Leaf)} on the
	 * created leaf.
	 * 
	 * @param object object of leaf
	 * @param asNode represents leaf a node or an edge?
	 * @param id id to pass to the leaf constructor
	 * @return newly create leaf
	 */
	protected abstract Leaf createLeaf (Object object, boolean asNode, long id);
}
