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

package de.grogra.vecmath.geom;

import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Abstract base class for octrees.
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public abstract class Octree
{
	/**
	 * The maximum depth which can be handled by this octree
	 * implementation. No subdivision corresponds to a depth
	 * of 0, a single subdivision in eight cells to a depth
	 * of 1 etc.
	 */
	public static final int MAX_DEPTH = 9;

	/**
	 * The grid size (number of cells in a single direction)
	 * which corresponds to the maximum depth,
	 * <code>1 << MAX_DEPTH</code>. 
	 */
	public static final int MAX_GRID_SIZE = 1 << MAX_DEPTH;

	/**
	 * The minimum coordinates of this octree.
	 */
	final Point3d min = new Point3d ();

	/**
	 * The maximum coordinates of this octree.
	 */
	final Point3d max = new Point3d ();

	/**
	 * The size of cells of depth {@link #MAX_DEPTH}.
	 * This is the size of this octree (<code>max - min</code>),
	 * scaled by 1 / {@link #MAX_GRID_SIZE}.
	 */
	final Vector3d minCellSize = new Vector3d ();

	/**
	 * Root cell of the octree.
	 */
	private Cell root;

	/**
	 * Actual depth of the octree. A depth of 0 indicates that
	 * only the root cell is present etc.
	 */
	private int depth;

	/**
	 * Number of cells in the octree.
	 */
	private int cellCount;

	/**
	 * Maximum depth, used internally during octree construction.
	 */
	private int maxDepth;

	/**
	 * Minimum number of objects per cell, used internally during
	 * octree construction.
	 */
	private int minObjects;

	/**
	 * A <code>Cell</code> represents a cell of an {@link Octree}.
	 * The array {@link #children} contains
	 * the eight children of this cell, if it is an inner node
	 * of the octree, otherwise it is <code>null</code>. The children
	 * are equal in size and represent a decomposition of their parent.
	 * <p>
	 * A leaf <code>Cell</code> also includes a list of volumes whose
	 * boundaries are contained in the cell.
	 * 
	 * @author Michael Tauer
	 * @author Ole Kniemeyer
	 */
	public static abstract class Cell
	{
		public static final int ROOT_POSITION = MAX_DEPTH;

		static final int WIDTH_MASK = 15;
		static final int X_BIT = 5;
		static final int Y_BIT = X_BIT + MAX_DEPTH;
		static final int Z_BIT = Y_BIT + MAX_DEPTH;
		static final int POS_MASK = MAX_GRID_SIZE - 1;

		/**
		 * The spatial position of the cell in the octree cube.
		 * This <code>int</code>-value encodes the full positional
		 * information in the following way:
		 * <p>
		 * <code>1 << (position & {@link #WIDTH_MASK})</code>:
		 * width of cell
		 * <p>
		 * <code>(position >> {@link #X_BIT}) & {@link #POS_MASK}</code>:
		 * minimum x-coordinate of cell 
		 * <p>
		 * <code>(position >> {@link #Y_BIT}) & {@link #POS_MASK}</code>:
		 * minimum y-coordinate of cell 
		 * <p>
		 * <code>(position >> {@link #Z_BIT}) & {@link #POS_MASK}</code>:
		 * minimum z-coordinate of cell
		 * <p>
		 * Width and coordinates are specified in integer grid
		 * coordinates ranging from 0 to {@link Octree#MAX_GRID_SIZE}.
		 * E.g., the root cell has (0, 0, 0) as coordinates and
		 * <code>{@link Octree#MAX_GRID_SIZE}
		 * = 1 << {@link Octree#MAX_DEPTH}</code> as width.
		 * <p>
		 * The actual 3D-coordinates are obtained through the method
		 * {@link #getExtent(Octree, Tuple3d, Tuple3d)}. 
		 */
		int position;

		/**
		 * The eight child nodes of this cell, or <code>null</code>
		 * if this cell is a leaf of the octree.
		 */
		public Cell[] children = null;

		public Cell front;
		public Cell back;
		public Cell top;
		public Cell bottom;
		public Cell left;
		public Cell right;

		/**
		 * Returns the number of volumes in this cell.
		 * Volumes contained in child cells are not counted. 
		 * 
		 * @return number of volumes
		 */
		public abstract int getVolumeCount ();

		/**
		 * Clears the list of volumes of this cell.
		 */
		public abstract void clearVolumes ();

		/**
		 * Returns the <code>index</code>th volume of the list
		 * of volumes of this cell. The returned volume may only
		 * be used up to the next invocation of this method with
		 * the same <code>state</code>, so that the state is allowed
		 * to store information about the volume. 
		 * 
		 * @param index index of volume in list
		 * @param state instance of <code>State</code> as returned by {@link Octree#createState()}
		 * @return volume at <code>index</code>
		 */
		public abstract Volume getVolume (int index, State state);

		/**
		 * Adds a volume to this cell. <code>v</code> must be a volume
		 * which has been returned previously by the invocation of
		 * {@link #getVolume} on the parent cell.
		 * 
		 * @param v volume of parent cell which has to be added to this cell
		 */
		public abstract void addVolume (Volume v);

		/**
		 * This factory method creates a new cell of the same class
		 * as this cell. It will be used as child of this cell.
		 * 
		 * @param position value for {@link #position}
		 * @return newly created cell
		 */
		protected abstract Cell createChild (int position);

		public Cell (int position)
		{
			this.position = position;
		}

		Cell getChild (int index)
		{
			if ((children == null) || (index < 0) || (index > 7))
			{
				return null;
			}
			return children[index];
		}

		/**
		 * Determines the spatial extent of this cell.
		 * 
		 * @param tree the octree to which this cell belongs
		 * @param min the minimum coordinates will be placed in here
		 * @param max the maximum coordinates will be placed in here
		 */
		public void getExtent (Octree tree, Tuple3d min, Tuple3d max)
		{
			int x = (position >> X_BIT) & POS_MASK;
			int y = (position >> Y_BIT) & POS_MASK;
			int z = (position >> Z_BIT) & POS_MASK;
			Vector3d s = tree.minCellSize;
			min.x = tree.min.x + s.x * x;
			min.y = tree.min.y + s.y * y;
			min.z = tree.min.z + s.z * z;
			int w = 1 << (position & WIDTH_MASK);
			max.x = tree.min.x + s.x * (x + w);
			max.y = tree.min.y + s.y * (y + w);
			max.z = tree.min.z + s.z * (z + w);
		}

		void divide ()
		{
			int base = position - 1;
			int width = 1 << (base & WIDTH_MASK);

			children = new Cell[8];

			// child 0 (x:0,y:0,z:0)
			children[0] = createChild (base);
			// child 1 (x:0,y:0,z:1)
			children[1] = createChild (base + (width << Z_BIT));
			// child 2 (x:0,y:1,z:0)
			children[2] = createChild (base + (width << Y_BIT));
			// child 3 (x:0,y:1,z:1)
			children[3] = createChild (base + (width << Y_BIT)
				+ (width << Z_BIT));
			// child 4 (x:1,y:0,z:0)
			children[4] = createChild (base + (width << X_BIT));
			// child 5 (x:1,y:0,z:1)
			children[5] = createChild (base + (width << X_BIT)
				+ (width << Z_BIT));
			// child 6 (x:1,y:1,z:0)
			children[6] = createChild (base + (width << X_BIT)
				+ (width << Y_BIT));
			// child 7 (x:1,y:1,z:1)
			children[7] = createChild (base + (width << X_BIT)
				+ (width << Y_BIT) + (width << Z_BIT));
		}

		void finish (Octree tree)
		{
			if (children == null)
			{
				return;
			}

			for (int childIndex = 0; childIndex < 8; childIndex++)
			{
				Cell child = children[childIndex];
				// link child neighbourhood

				if ((childIndex & 3) < 2)
				{
					// childIndex = 0, 1, 4, 5
					child.front = front;
					if ((front != null) && (front.children != null))
					{
						child.front = front.getChild (childIndex + 2);
					}
					child.back = getChild (childIndex + 2);
				}
				else
				{
					// childIndex = 2, 3, 6, 7
					child.back = back;
					if ((back != null) && (back.children != null))
					{
						child.back = back.getChild (childIndex - 2);
					}
					child.front = getChild (childIndex - 2);
				}

				if (childIndex < 4)
				{
					// childIndex = 0, 1, 2, 3
					child.left = left;
					if ((left != null) && (left.children != null))
					{
						child.left = left.getChild (childIndex + 4);
					}
					child.right = getChild (childIndex + 4);
				}
				else
				{
					// childIndex = 4, 5, 6, 7
					child.right = right;
					if ((right != null) && (right.children != null))
					{
						child.right = right.getChild (childIndex - 4);
					}
					child.left = getChild (childIndex - 4);
				}

				if ((childIndex & 1) == 0)
				{
					// childIndex = 0, 2, 4, 6
					child.bottom = bottom;
					if ((bottom != null) && (bottom.children != null))
					{
						child.bottom = bottom.getChild (childIndex + 1);
					}
					child.top = getChild (childIndex + 1);
				}
				else
				{
					// childIndex = 1, 3, 5, 7
					child.top = top;
					if ((top != null) && (top.children != null))
					{
						child.top = top.getChild (childIndex - 1);
					}
					child.bottom = getChild (childIndex - 1);
				}

				child.finish (tree);
			}
		}

		String toString (Octree octree)
		{
			Vector3d a = new Vector3d ();
			Vector3d b = new Vector3d ();
			getExtent (octree, a, b);
			return getClass ().getName () + '(' + a + " - " + b + ')';
		}
	}

	/**
	 * An instance of <code>State</code> represents the state which
	 * is needed for octree algorithms, namely for the methods
	 * {@link Octree#computeIntersections} and {@link Cell#getVolume}.
	 * A suitable instance for a specific octree implementation
	 * is obtained by {@link Octree#createState()}. 
	 * 
	 * @author Ole Kniemeyer
	 */
	public static abstract class State extends Variables
	{
		/**
		 * The cell iterator which is used to traverse the octree
		 * along a line.
		 */
		protected CellIterator cellIterator;

		/**
		 * Marks the volume <code>id</code> within this state,
		 * returns <code>true</code> iff the volume
		 * has not yet been marked before within this state.
		 * 
		 * @param id id of the volume
		 * @return <code>true</code> iff the volume <code>id</code>
		 * is marked for the first time
		 * 
		 * @see #clear(int)
		 */
		public abstract boolean mark (int id);

		/**
		 * Clears any mark of volume <code>id</code> which has been
		 * set previously by {@link #mark(int)} within this state.
		 * 
		 * @param id of the volume
		 * 
		 * @see #mark(int)
		 */
		public abstract void clear (int id);
	}

	/**
	 * Initializes the octree. The <code>root</code> cell has to contain
	 * all finite volumes, it will be subdivided by this method.
	 * 
	 * @param maxDepth maximum allowed depth of octree
	 * @param minObjects minimum volumes per cell. Only cells having
	 * more then <code>minObjects</code> volumes are checked for
	 * subdivision
	 * @param min minimum coordinates of octree box
	 * @param max maximum coordinates of octree box
	 * @param root root cell of the octree
	 */
	protected void initialize (int maxDepth, int minObjects, Tuple3d min,
			Tuple3d max, Cell root)
	{
		if (maxDepth < 0)
		{
			throw new IllegalArgumentException ("maxDepth negative");
		}
		if (maxDepth > MAX_DEPTH)
		{
			throw new IllegalArgumentException ("maxDepth greater than MAX_DEPTH");
		}
		this.maxDepth = maxDepth;
		this.minObjects = minObjects;
		this.root = root;

		this.min.set (min);
		this.max.set (max);
		this.minCellSize.sub (max, min);
		this.minCellSize.scale (1d / MAX_GRID_SIZE);

		depth = 0;
		cellCount = 1;

		recursivelyDivideNode (0, root, createState ());
		finishCells ();
	}

	
	protected void finishCells ()
	{
		root.finish (this);
	}

	private final Vector3d center = new Vector3d ();
	private final BoundingBox cellBounds = new BoundingBox (new Point3d (),
		new Point3d ());

	private void recursivelyDivideNode (int curDepth, Cell node, State state)
	{
		int size = node.getVolumeCount ();
		if ((curDepth < maxDepth) && (size > minObjects))
		{
			node.divide ();
			boolean keepDivision = false;
			for (int i = 0; i < 8; i++)
			{
				// add finite objects
				Cell curNode = node.children[i];
				curNode.getExtent (this, cellBounds.min, cellBounds.max);
				center.sub (cellBounds.max, cellBounds.min);
				double radius = 0.5 * center.length ();
				center.add (cellBounds.min, cellBounds.max);
				center.scale (0.5);
				for (int j = 0; j < size; j++)
				{
					Volume v = node.getVolume (j, state);
					if (v.boxContainsBoundary (cellBounds, center, radius,
						state))
					{
						curNode.addVolume (v);
					}
				}
				recursivelyDivideNode (curDepth + 1, curNode, state);
				keepDivision |= curNode.getVolumeCount () < size;
			}
			if (keepDivision)
			{
				depth = Math.max (depth, curDepth + 1);
				cellCount += 8;
				node.clearVolumes ();
			}
			else
			{
				node.children = null;
			}
		}
	}

	/**
	 * Returns the minimum coordinates of the octree box.
	 * All finite objects are contained in this box. The returned
	 * value must not be modified.
	 * 
	 * @return minimum coordinates of octree box
	 */
	public Point3d getMin ()
	{
		return min;
	}

	/**
	 * Returns the maximum coordinates of the octree box.
	 * All finite objects are contained in this box. The returned
	 * value must not be modified.
	 * 
	 * @return maximum coordinates of octree box
	 */
	public Point3d getMax ()
	{
		return max;
	}

	/**
	 * Returns the depth of this octree. A depth of 0 indicates that
	 * only the root cell is present etc.
	 * 
	 * @return depth of octree
	 */
	public int getDepth ()
	{
		return depth;
	}

	/**
	 * Returns the number of octree cells of this octree.
	 * 
	 * @return number of cells
	 */
	public int getCellCount ()
	{
		return cellCount;
	}

	/**
	 * Returns the root cell of the octree.
	 * The cell hierarchy contains all finite volumes.
	 *
	 * @return root cell of octree
	 */
	public Cell getRoot ()
	{
		return root;
	}

	/**
	 * This factory methods creates a new instance of
	 * {@link State} which is suitable as state for this octree.
	 * State information is needed in the methods
	 * {@link #computeIntersections} and {@link Cell#getVolume}.
	 * 
	 * @return new instance of suitable <code>State</code> subclass
	 */
	public abstract State createState ();

	/**
	 * Returns a list of infinite volumes which shall be treated
	 * as part of the octree, though they cannot be included
	 * in the hierarchy of octree cells.
	 * 
	 * @return list of infinite volumes, or <code>null</code>
	 */
	protected abstract ArrayList getInfiniteVolumes ();

	/**
	 * Computes intersections between the boundary surfaces of the
	 * volumes of this union and the specified <code>line</code>.
	 * For the parameters, see {@link Volume#computeIntersections}.
	 * The additional parameter <code>state</code> has to be preallocated
	 * by invocation of {@link #createState()} and may be used for
	 * multiple invocations of this method. This is preferable to
	 * the ordinary <code>computeIntersections</code>-method of the
	 * interface <code>Volume</code> for performance reasons.
	 *
	 * @param line a line
	 * @param which one of {@link Intersection#ALL},
	 * {@link Intersection#CLOSEST}, {@link Intersection#ANY}, this
	 * determines which intersections have to be added to <code>list</code>
	 * @param list the intersections are added to this list
	 * @param excludeStart intersection at start point which shall be excluded, or <code>null</code>
	 * @param excludeEnd intersection at end point which shall be excluded, or <code>null</code>
	 * @param state instance of <code>State</code> as returned by {@link #createState()}
	 * @return <code>true</code> iff the beginning of the line lies
	 * within the volume
	 */
	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd, State state)
	{
		// initial size of list
		int rs = list.size;

		int index = list.getISize ();

		// list.iarray[index + i] : index of next unprocessed intersection of volume i
		// when this index reaches list.iarray{end + i], volume i has been processed completely

		state.cellIterator.setLine (line);

		double lineEnd = line.end;

		boolean inside = false;

		ArrayList<Volume> infiniteObjects = getInfiniteVolumes ();

		if (infiniteObjects != null)
		{
			for (int i = 0; i < infiniteObjects.size (); i++)
			{
				Volume v = infiniteObjects.get (i);
				int begin = list.size;
				inside |= v.computeIntersections (line, which, list,
					excludeStart, excludeEnd);
				if (list.size > begin)
				{
					if (which == Intersection.ANY)
					{
						return inside;
					}
					list.ipush (0);
					list.ipush (begin);
					if (which == Intersection.CLOSEST)
					{
						line.end = list.elements[begin].parameter;
					}
				}
			}
		}

		findIntersections: while (state.cellIterator.hasNext ())
		{
			Cell nextCell = state.cellIterator.next ();
			if ((which != Intersection.ALL)
				&& (state.cellIterator.getEnteringParameter () > line.end))
			{
				break;
			}

			for (int i = nextCell.getVolumeCount () - 1; i >= 0; i--)
			{
				Volume v = nextCell.getVolume (i, state);
				int id = v.getId ();
				if (state.mark (id))
				{
					list.ipush (id);
					int begin = list.size;
					inside |= v.computeIntersections (line, which, list,
						excludeStart, excludeEnd);
					if (list.size > begin)
					{
						list.ipush (begin);
						if (which == Intersection.ANY)
						{
							break findIntersections;
						}
						if (which == Intersection.CLOSEST)
						{
							line.end = list.elements[begin].parameter;
						}
					}
					else
					{
						list.ipush (-1);
					}
				}
			}
		}

		line.end = lineEnd;

		int s = (list.getISize () - index) >> 1;
		if (s > 0)
		{
			int p = index;
			for (int i = 0; i < s; i++)
			{
				int b = index + (i << 1);
				state.clear (list.istack[b]);

				b = list.istack[b + 1];
				if (b >= 0)
				{
					list.istack[p++] = b;
				}
			}

			s = p - index;

			if (s > 1)
			{
				int end = p;
				for (int i = 1; i < s; i++)
				{
					list.istack[end + i - 1] = list.istack[index + i];
				}
				list.istack[end + s - 1] = list.size;

				CompoundVolume.sortIntersections (which == Intersection.ALL,
					list, rs, s, index, end);
			}
			else
			{
				list.setISize (index);
			}
		}
		return inside;
	}


	/**
	 * Suggests a maximal octree depth for the given
	 * <code>numberOfVolumes</code>. The computed value minimizes
	 * the heuristic cost function
	 * <center>
	 * c(d) = N / 4<sup>d</sup> + r 2<sup>d</sup> 
	 * </center>
	 * where N is <code>numberOfVolumes</code> and r the relative cost
	 * of passing an octree cell compared to intersecting a volume. The
	 * first part of this function estimates the number of
	 * ray/volume intersections for a single ray, the second part
	 * estimates the number of passed octree cells.
	 * <p>
	 * Based on experimental data, r = 0.1 has been chosen.
	 * 
	 * @param numberOfVolumes number of volumes in the scene
	 * @return suggested value for maximal octree depth
	 */
	public static int suggestDepth (int numberOfVolumes)
	{
		final double costsForCell = 0.1;
		
		// compute optimal depth and round down
		int n = (int) (Math.log (2 * numberOfVolumes / costsForCell) / Math.log (8));
		if (n < 4)
		{
			return 4;
		}
		if (n >= MAX_DEPTH)
		{
			return MAX_DEPTH;
		}

		// estimate costs for n
		double pow2n = 1 << n;
		double costn = numberOfVolumes / (pow2n * pow2n) + costsForCell * pow2n;
		// estimate costs for n + 1
		pow2n = 1 << (n + 1);
		double costn1 = numberOfVolumes / (pow2n * pow2n) + costsForCell * pow2n;

		return (costn < costn1) ? n : n + 1;
	}

}
