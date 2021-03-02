
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

import javax.vecmath.Matrix4d;

import de.grogra.graph.ArrayPath;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Instantiator;
import de.grogra.graph.Path;
import de.grogra.graph.Visitor;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Transform3D;

public class Instance3D extends ShadedNull implements Instantiator
{
	public static final int MASTER = ShadedNull.MIN_UNUSED_SPECIAL_OF_SOURCE;
	public static final int MIN_UNUSED_SPECIAL_OF_SOURCE = MASTER + 1;


	public static final int LOCAL = 0;
	public static final int TERMINAL = 1;
	public static final int TERMINAL_TRANSLATION = 2;


	int exportedTransformation = LOCAL;
	//enh:field attr=Attributes.EXPORTED_TRANSFORMATION getter setter

	private static void initType ()
	{
		$TYPE.declareSpecialEdge (MASTER, "edge.master", new Node[0]);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field exportedTransformation$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Instance3D.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((Instance3D) o).exportedTransformation = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Instance3D) o).getExportedTransformation ();
			}
			return super.getInt (o);
		}
	}

	static
	{
		$TYPE = new NType (new Instance3D ());
		$TYPE.addManagedField (exportedTransformation$FIELD = new _Field ("exportedTransformation", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.declareFieldAttribute (exportedTransformation$FIELD, Attributes.EXPORTED_TRANSFORMATION);
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
		return new Instance3D ();
	}

	public int getExportedTransformation ()
	{
		return exportedTransformation;
	}

	public void setExportedTransformation (int value)
	{
		this.exportedTransformation = (int) value;
	}

//enh:end


	public Instance3D (Transform3D transform)
	{
		super (transform);
	}


	public Instance3D ()
	{
		this (null);
	}


	public Instance3D (Transform3D transform, Shader shader)
	{
		this (transform);
		this.shader = shader;
	}


	@Override
	public Instantiator getInstantiator ()
	{
		return this;
	}


	protected int getEdgesToInstance ()
	{
		return Graph.BRANCH_EDGE;
	}


	protected Node getInstanceRootSetAttributes (GraphState gs)
	{
		for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
		{
			Node t;
			if ((e.getEdgeBits () == MASTER) && ((t = e.getTarget ()) != this))
			{
				return t;
			}
		}
		return null;
	}


	public boolean instantiate (ArrayPath path, Visitor v)
	{
/*		if (master != null)
		{
			return master.instantiate (path, v);
		}
		else*/
		{
			Node n = getInstanceRootSetAttributes (v.getGraphState ());
			return (n == null)
				|| n.instantiateGraph (getEdgesToInstance (), path, v);
		}
	}


	private static final class TVisitor extends Matrix4d implements Visitor
	{
		private final Matrix4d out;
		private final GraphState gs;
		private boolean first = true;


		TVisitor (GraphState gs, Matrix4d out)
		{
			m33 = 1;
			this.gs = gs;
			this.out = out;
		}


		public GraphState getGraphState ()
		{
			return gs;
		}


		public Object visitEnter (Path path, boolean node)
		{
			if (node)
			{
				Object n = path.getObject (-1);
				Transformation t = (Transformation) gs.getObjectDefault
					(n, true, Attributes.TRANSFORMATION, null);
				if (t != null)
				{
					t.preTransform (n, true, out, this, gs);
					t.postTransform (n, true, this, out, out, gs);
				}
			}
			else
			{
				if (first)
				{
					first = false;
					return null;
				}
				return path.isInEdgeDirection (-2) && ((path.getEdgeBits (-2) & Graph.SUCCESSOR_EDGE) != 0) ? null : STOP;
			}
			return null;
		}


		public boolean visitLeave (Object o, Path path, boolean node)
		{
			return !node && (o == STOP);
		}


		public Object visitInstanceEnter ()
		{
			return null;
		}


		public boolean visitInstanceLeave (Object o)
		{
			return true;
		}
	}


	@Override
	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		boolean t;
		int et;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = (bits & TRANSFORMING_MASK) != 0;
				et = exportedTransformation;
			}
			else
			{
				t = gs.checkBoolean (this, true, Attributes.TRANSFORMING,
									 (bits & TRANSFORMING_MASK) != 0);
				et = t ? gs.checkInt (this, true, Attributes.EXPORTED_TRANSFORMATION,
									  exportedTransformation) : 0;
			}
		}
		else
		{
			t = gs.getBoolean (object, asNode, Attributes.TRANSFORMING);
			et = LOCAL;
		}
		if (t)
		{
			out.set (in);
			if (et != LOCAL)
			{
				if (et == TERMINAL_TRANSLATION)
				{
					double m00 = in.m00, m10 = in.m10, m20 = in.m20,
						m01 = in.m01, m11 = in.m11, m21 = in.m21,
						m02 = in.m02, m12 = in.m12, m22 = in.m22;
					try
					{
						gs.beginInstancing (this, getId ());
						getInstantiator ().instantiate (new ArrayPath (gs.getGraph ()), new TVisitor (gs, out));
					}
					finally
					{
						gs.endInstancing ();
					}
					out.m00 = m00; out.m10 = m10; out.m20 = m20;
					out.m01 = m01; out.m11 = m11; out.m21 = m21;
					out.m02 = m02; out.m12 = m12; out.m22 = m22;
				}
				else
				{
					try
					{
						gs.beginInstancing (this, getId ());
						getInstantiator ().instantiate (null, new TVisitor (gs, out));
					}
					finally
					{
						gs.endInstancing ();
					}
				}
			}
		}
		else
		{
			if (out != pre)
			{
				out.set (pre);
			}
		}
	}

}
