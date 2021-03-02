
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

import javax.vecmath.Tuple3d;

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.RGBColor;
import de.grogra.math.TVector3d;
import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>GRSVertex</code> is used in conjunction with a
 * <code>GRSMesh</code> to define a graph rotation system. It has
 * a cyclic list of neighbors stored in {@link #neighbors}.
 * 
 * @see de.grogra.imp3d.objects.GRSMesh
 * 
 * @author Ole Kniemeyer
 */
public class GRSVertex extends Point
{
	/**
	 * Cyclic neighborhood of this vertex. This contains all neighboring
	 * vertices (those that are connected with this vertex by an edge
	 * of the polygon mesh). The neighbors are sorted in a cyclic way,
	 * namely in counter-clockwise order when seen from above of the
	 * mesh.
	 */
	protected ObjectList<GRSVertex> neighbors = new ObjectList<GRSVertex> (5);
	//enh:field componenttype=$TYPE fco getter setter
	
	/**
	 * u coordinate of vertex
	 */
	protected float u;
	//enh:field getter setter

	/**
	 * v coordinate of vertex
	 */
	protected float v;
	//enh:field getter setter

	/**
	 * Temporarily needed by {@link GRSMesh#polygonize}: Index in list
	 * of all vertices of graph rotation system. 
	 */
	transient int meshIndex;


	/**
	 * Creates a new <code>GRSVertex</code> whose
	 * {@linkplain Null#transform transformation} is set to a
	 * {@link TVector3d} initialized with (0, 0, 0).
	 */
	public GRSVertex ()
	{
		this (0, 0, 0);
	}


	/**
	 * Creates a new <code>GRSVertex</code> whose
	 * {@linkplain Null#transform transformation} is set to a
	 * {@link TVector3d} initialized with <code>p</code>.
	 */
	public GRSVertex (Tuple3d p)
	{
		this (p.x, p.y, p.z);
	}


	/**
	 * Creates a new <code>GRSVertex</code> at the specifed location.
	 * The {@linkplain Null#transform transformation} is set to a
	 * {@link TVector3d} initialized with <code>(x, y, z)</code>.
	 * 
	 * @param x relative x coordinate
	 * @param y relative y coordinate
	 * @param z relative z coordinate
	 */
	public GRSVertex (double x, double y, double z)
	{
		super ();
		setTransform (x, y, z);
		color = RGBColor.BLUE;
	}


	/**
	 * Returns the index of <code>v</code> in the cyclic list
	 * of neighbors.
	 * 
	 * @param v a vertex
	 * @return index of <code>v</code> in neighbor list, or -1 if
	 * <code>v</code> is not contained
	 */
	public int getNeighborIndex (GRSVertex v)
	{
		return neighbors.indexOf (v);
	}

	/**
	 * Returns the number of neighbors in the cyclic list of neighbors.
	 * 
	 * @return number of neighbors
	 */
	public int valence ()
	{
		return neighbors.size ();
	}

	/**
	 * Yields all neighbors to <code>cons</code>. This may be used
	 * as an XL generator method.
	 * 
	 * @param cons consumer which receives the neighbors
	 * @return <code>null</code>
	 */
	public GRSVertex neighbors (ObjectConsumer<? super GRSVertex> cons)
	{
		neighbors.evaluateObject (cons);
		return null;
	}

	/**
	 * Returns the neighbor having index <code>i</code>.
	 * 
	 * @param i index of neighbor
	 * @return neighbor <code>i</code> in cyclic list
	 */
	public GRSVertex getNeighbor (int i)
	{
		return neighbors.get (i);
	}

	/**
	 * Returns the neighbor which follows <code>n</code> in the
	 * cyclic list of neighbors.
	 * 
	 * @param n a neighbor
	 * @return successor of <code>n</code>, or <code>null</code>
	 * if <code>n</code> is not a neighbor of this vertex
	 */
	public GRSVertex nextTo (GRSVertex n)
	{
		int i = neighbors.indexOf (n);
		if (i < 0)
		{
			return null;
		}
		return neighbors.get ((i + 1 == neighbors.size) ? 0 : i + 1);
	}

	/**
	 * Returns the neighbor which precedes <code>n</code> in the
	 * cyclic list of neighbors.
	 * 
	 * @param n a neighbor
	 * @return predecessor of <code>n</code>, or <code>null</code>
	 * if <code>n</code> is not a neighbor of this vertex
	 */
	public GRSVertex prevTo (GRSVertex n)
	{
		int i = neighbors.indexOf (n);
		if (i < 0)
		{
			return null;
		}
		return neighbors.get ((i == 0) ? neighbors.size - 1 : i - 1);
	}

	/**
	 * Returns the neighbor which is the <code>j</code>-th successor
	 * of <code>n</code> in the cyclic list of neighbors.
	 * 
	 * @param j distance from <code>n</code> in cyclic neighbor list
	 * @param n a neighbor
	 * @return <code>j</code>-th successor of <code>n</code>,
	 * or <code>null</code> if <code>n</code> is not a neighbor of this vertex
	 */
	public GRSVertex nextTo (int j, GRSVertex n)
	{
		int i = neighbors.indexOf (n);
		if (i < 0)
		{
			return null;
		}
		return neighbors.get ((i + neighbors.size + j) % neighbors.size);
	}

	/**
	 * Returns the neighbor which is the <code>j</code>-th predecessor
	 * of <code>n</code> in the cyclic list of neighbors.
	 * 
	 * @param j distance from <code>n</code> in cyclic neighbor list
	 * @param n a neighbor
	 * @return <code>j</code>-th predecessor of <code>n</code>,
	 * or <code>null</code> if <code>n</code> is not a neighbor of this vertex
	 */
	public GRSVertex prevTo (int j, GRSVertex n)
	{
		int i = neighbors.indexOf (n);
		if (i < 0)
		{
			return null;
		}
		return neighbors.get ((i + neighbors.size - j) % neighbors.size);
	}

	/**
	 * Returns the first neighbor of the cyclic list of neighbors.
	 * 
	 * @return first neighbor of this vertex, or <code>null</code>
	 * if it has no neighbors
	 */
	public GRSVertex first ()
	{
		return neighbors.isEmpty () ? null : neighbors.get (0);
	}

	/**
	 * Returns the vertex which follows this vertex in the neighbor list
	 * of <code>ref</code>. Same as <code>ref.nextTo (this)</code>.
	 * 
	 * @param ref vertex of which <code>this</code> is a neighbor
	 * @return successor of <code>this</code> in <code>ref</code>'s
	 * neighbor list, or <code>null</code> if <code>this</code> is not
	 * a neighbor of <code>ref</code>
	 */
	public GRSVertex next (GRSVertex ref)
	{
		return ref.nextTo (this);
	}
	
	/**
	 * Returns the vertex which precedes this vertex in the neighbor list
	 * of <code>ref</code>. Same as <code>ref.prevTo (this)</code>.
	 * 
	 * @param ref vertex of which <code>this</code> is a neighbor
	 * @return predecessor of <code>this</code> in <code>ref</code>'s
	 * neighbor list, or <code>null</code> if <code>this</code> is not
	 * a neighbor of <code>ref</code>
	 */
	public GRSVertex prev (GRSVertex ref)
	{
		return ref.prevTo (this);
	}

	/**
	 * Returns the vertex which is the <code>j</code>-th successor
	 * of this vertex in the neighbor list
	 * of <code>ref</code>. Same as <code>ref.nextTo (j, this)</code>.
	 * 
	 * @param ref vertex of which <code>this</code> is a neighbor
	 * @return <code>j</code>-th successor of <code>this</code>
	 * in <code>ref</code>'s neighbor list, or <code>null</code>
	 * if <code>this</code> is not a neighbor of <code>ref</code>.
	 */
	public GRSVertex next (int j, GRSVertex ref)
	{
		return ref.nextTo (j, this);
	}
	
	/**
	 * Returns the vertex which is the <code>j</code>-th predecessor
	 * of this vertex in the neighbor list
	 * of <code>ref</code>. Same as <code>ref.prevTo (j, this)</code>.
	 * 
	 * @param ref vertex of which <code>this</code> is a neighbor
	 * @return <code>j</code>-th predecessor of <code>this</code>
	 * in <code>ref</code>'s neighbor list, or <code>null</code>
	 * if <code>this</code> is not a neighbor of <code>ref</code>.
	 */
	public GRSVertex prev (int j, GRSVertex ref)
	{
		return ref.prevTo (j, this);
	}
	
//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field neighbors$FIELD;
	public static final NType.Field u$FIELD;
	public static final NType.Field v$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (GRSVertex.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((GRSVertex) o).u = (float) value;
					return;
				case 2:
					((GRSVertex) o).v = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((GRSVertex) o).getU ();
				case 2:
					return ((GRSVertex) o).getV ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((GRSVertex) o).neighbors = (ObjectList<GRSVertex>) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((GRSVertex) o).getNeighbors ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new GRSVertex ());
		$TYPE.addManagedField (neighbors$FIELD = new _Field ("neighbors", _Field.PROTECTED  | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ObjectList.class), $TYPE, 0));
		$TYPE.addManagedField (u$FIELD = new _Field ("u", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (v$FIELD = new _Field ("v", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
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
		return new GRSVertex ();
	}

	public float getU ()
	{
		return u;
	}

	public void setU (float value)
	{
		this.u = (float) value;
	}

	public float getV ()
	{
		return v;
	}

	public void setV (float value)
	{
		this.v = (float) value;
	}

	public ObjectList<GRSVertex> getNeighbors ()
	{
		return neighbors;
	}

	public void setNeighbors (ObjectList<GRSVertex> value)
	{
		neighbors$FIELD.setObject (this, value);
	}

//enh:end

}
