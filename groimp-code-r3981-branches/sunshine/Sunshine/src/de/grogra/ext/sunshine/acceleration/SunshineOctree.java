/**
 * 
 */
package de.grogra.ext.sunshine.acceleration;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import com.sun.opengl.util.BufferUtil;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.acceleration.OctreeKernel;
import de.grogra.vecmath.geom.MeshVolume;
import de.grogra.vecmath.geom.Octree;
import de.grogra.vecmath.geom.OctreeUnion;
import de.grogra.vecmath.geom.Octree.Cell;
import de.grogra.vecmath.geom.Octree.State;

/**
 * @author mankmil
 * An octree cell looks like this
 * |			|inverse	|link2Volumes	|face2|
 * |transform	|transform	|link2Children	|face3|
 * |matrix		|matrix		|face0			|face4|
 * |size=3		|size=3		|face1			|face5|
 * 
 * the size of an octree cell is 8 pixel 
 */
public class SunshineOctree implements SunshineAccelerator
{
	private final int						RGBA			= 4 * BufferUtil.SIZEOF_FLOAT;
	private final int						SIZEOF_CELL		= 128;
	private final int						FACES			= 104;
	private final int						VOLUMELINK		= 96;
	private final int						CHILDRENLINK	= 100;

	private ByteBuffer						data;
	private int								cellCount		= 0;
	private int								volumeCount		= 0;
	private int								size			= 0;
	private int 							depth 			= 0;
	private int 							maxVolumeCount	= 0;

	final Hashtable<Octree.Cell, Integer>	cellCache;

	private Vector3d						tmp				= new Vector3d();
	private Point3d							min				= new Point3d();
	private Point3d							max				= new Point3d();
	
	private State state;

	public SunshineOctree(OctreeUnion octree)
	{
		size 	= 500;
		depth 	= octree.getOctree().getDepth();
		
		data = BufferUtil.newByteBuffer(size * size * RGBA);
		cellCache = new Hashtable<Cell, Integer>();

		Octree t = octree.getOctree();
		if((t.getRoot().getVolumeCount() > 0)
				&& (t.getRoot().getVolume(0, null) instanceof MeshVolume))
		{
			t = ((MeshVolume) t.getRoot().getVolume(0, null)).getOctree();
			state = t.createState();
		}

		Cell root = t.getRoot();

		// visit and store the root cell
		visitCell(t, root);
		storeFaceLinks(root, 0);
		
//		invertMatrixTest(root, t);
		
		data.putFloat(CHILDRENLINK, 8);
		if(root.getVolumeCount() > 0)
		{
			storeVolumes(root, 0);
		}
		else
		{
			data.putFloat(VOLUMELINK, -2);
		}

		/*
		 * visit and store the Children of the root cell the second run sets the
		 * neighborhood links
		 */
		for(int i = 0; i < 2; i++)
		{
			visitChildren(t, root);
		} // for i
		
		
		// fill the rest of the texture with nil values
		data.position(cellCount * SIZEOF_CELL + volumeCount * 4);
		while(data.remaining() > 0)
		{
			data.putFloat(-1f);
		} //for
		
		
		
//		System.out.println("front: " + root.children[0].front);
//		System.out.println("left: " + root.children[0].left);
//		System.out.println("right: " + root.children[0].right);
//		System.out.println("back: " + root.children[0].back);
//		System.out.println("top: " + root.children[0].top);
//		System.out.println("bottom: " + root.children[0].bottom);
		

	} // Constructor

	
	private void storeTransformMatrix(Matrix4f m)
	{
		for(int i = 0; i < 2; i++)
		{
			data.putFloat(m.m00);
			data.putFloat(m.m10);
			data.putFloat(m.m20);

			data.putFloat(m.m01);
			data.putFloat(m.m11);
			data.putFloat(m.m21);

			data.putFloat(m.m02);
			data.putFloat(m.m12);
			data.putFloat(m.m22);

			data.putFloat(m.m03);
			data.putFloat(m.m13);
			data.putFloat(m.m23);

			m.invert();
		}
	} // storeTreeCells


	private void storeCell(Cell cell, Matrix4f m)
	{
		data.position(cellCount * SIZEOF_CELL);
		cellCache.put(cell, data.position());

		storeTransformMatrix(m);

		cellCount++;
	} // storeCell


	private void storeChildrenLink(Cell cell)
	{
		if(cell.children != null)
		{
			data.putFloat(cellCache.get(cell) + CHILDRENLINK, cellCache
					.get(cell.children[0]) / 16);
//			 System.out.println("CHILDRENLINK = " +
//			 cellCache.get(cell.children[0]) / 16 );
		}
	}


	private void storeFaceLinks(Cell cell, int pos)
	{
		int[] faces = { -1, -1, -1, -1, -1, -1 };

		if(cell.front != null)
			faces[0] = cellCache.get(cell.front);

		if(cell.back != null)
			faces[1] = cellCache.get(cell.back);

		if(cell.top != null)
			faces[2] = cellCache.get(cell.top);

		if(cell.bottom != null)
			faces[3] = cellCache.get(cell.bottom);

		if(cell.left != null)
			faces[4] = cellCache.get(cell.left);

		if(cell.right != null)
			faces[5] = cellCache.get(cell.right);

		data.position(pos + FACES);

		for(int i = 0; i < 6; i++)
		{
			data.putFloat(faces[i] != -1 ? (float) faces[i] / 16f : -1f);
		} // for i

	}

	private void storeVolumes(Cell cell, int pos)
	{
		int p = cellCount * SIZEOF_CELL + volumeCount * 4;
		int volumes = cell.getVolumeCount();
		maxVolumeCount = Math.max(maxVolumeCount, volumes);
		int link = -2; // isNode

		link = cell.children == null ? p : -2;

		if(cell.children == null && volumes == 0) // empty leaf
		{
			link = -1;
		}
		
		
		data.putFloat(pos + VOLUMELINK, link);
		 
		
		if(volumes > 0)
		{
			data.position(p);

			for(int i = 0; i < volumes; i++)
			{
				if(!(cell.getVolume(i, state) instanceof MeshVolume))
				{
					data.putFloat(cell.getVolume(i, state).getId());
					volumeCount++;
				}
				
			} // for
//			System.out.println("-----------");
			
			data.putFloat(-1f);
			volumeCount++;
		}

	}


	// obtains the dimension of the cell and stores it
	private void visitCell(Octree t, Cell cell)
	{
		if(cellCache.size() != t.getCellCount())
		{
			cell.getExtent(t, min, max);
			storeCell(cell, getMatrix(min, max));
		}
		else
		{			
			int pos = cellCache.get(cell);
			storeFaceLinks(cell, pos);
			storeChildrenLink(cell);
			storeVolumes(cell, pos);
		}

	}


	private void visitChildren(Octree t, Cell cell)
	{
		if(cell.children != null)
		{
			for(int i = 0; i < 8; i++)
			{
				visitCell(t, cell.children[i]);
			}

			for(int i = 0; i < 8; i++)
			{
				visitChildren(t, cell.children[i]);
			}
		} // if
	} // visitChildren


	private Matrix4f getMatrix(Point3d min, Point3d max)
	{
		tmp.add(min, max);
		tmp.scale(0.5);

		Matrix4f m = new Matrix4f();
		m.setIdentity();

		m.m03 = (float) tmp.x;
		m.m13 = (float) tmp.y;
		m.m23 = (float) min.z;

		tmp.sub(max, min);

		m.m00 *= tmp.x;
		m.m11 *= tmp.y;
		m.m22 *= tmp.z;

		return m;
	}
	
	private void invertMatrixTest(Cell cell, Octree t)
	{
		cell.getExtent(t, min, max);
		System.out.println("OctreeDepth: " + t.getDepth());
		
		System.out.println("min: " + min + " | max: " + max);
		Matrix4f m = getMatrix(min, max);
		System.out.println("Matrix: " + m);
		
		m.invert();
		System.out.println("Java inverted Matrix: " + m);
		
		Point3d myMin = new Point3d();
		Point3d myMax = new Point3d();
		
		myMin.set(m.m00, m.m11, m.m22);
		myMax.scale(0.5);
		myMax.set(myMin);
		myMin.negate();
		
		System.out.println("myMin: " + myMin);
		System.out.println("myMax: " + myMax);
		
		System.out.println("self inverted Matrix: " + getMatrix(myMin, myMax));
	}
	

	public Kernel getKernel(GLAutoDrawable drawable, int[] data, int size,
			int objectCount, int steps)
	{
		return new OctreeKernel("OctreeKernel", drawable, data, size,
				objectCount, steps, getDepth());
	}


	public ByteBuffer getTreeData()
	{
		return data;
	}


	public int getSize()
	{
		return size;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public int getMaxVolumeCount()
	{
		return maxVolumeCount;
	}
	
	public String getShaderCode()
	{
		return "octreeTraversal2.frag";
	}

} // class
