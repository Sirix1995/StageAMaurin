/**
 * 
 */
package de.grogra.ext.sunshine.kdTree;

import java.nio.ByteBuffer;
import java.util.Hashtable;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.opengl.util.BufferUtil;
import de.grogra.vecmath.geom.MeshVolume;
import de.grogra.vecmath.geom.Octree;
import de.grogra.vecmath.geom.OctreeUnion;
import de.grogra.vecmath.geom.Octree.Cell;

/**
 * @author Thomas
 *
 */
public class KdTree
{
	private final int RGBA = 4 * BufferUtil.SIZEOF_FLOAT;
	private final int SIZEOF_CELL = 128;
	private final int FACES = 104;
	private final int VOLUMELINK = 96;
	private final int CHILDRENLINK = 100;
	
	
	private ByteBuffer treeTex;
	private int cellCount = 0;
	private int volumeCount = 0;
	private int size;

	final Hashtable<Octree.Cell, Integer> cellCache;
	
	private Vector3d tmp = new Vector3d ();
	private Point3d min = new Point3d ();
	private Point3d max = new Point3d ();
	
	
	

	public KdTree(int s, OctreeUnion octree)
	{
		size = s;
		treeTex = BufferUtil.newByteBuffer(size*size*RGBA);
		cellCache = new Hashtable<Cell, Integer>();
		
		Octree t = octree.getOctree ();
		if ((t.getRoot ().getVolumeCount () > 0)
			&& (t.getRoot ().getVolume (0, null) instanceof MeshVolume))
		{
			t = ((MeshVolume) t.getRoot ().getVolume (0, null)).getOctree();
		}
		
		
		Cell root = t.getRoot(); 
		
		//visit and store the root cell
		visitCell(t, root);
		storeFaceLinks(root, 0);
		treeTex.putFloat(CHILDRENLINK, 8);
		if(root.getVolumeCount() > 0)
		{
			storeVolumes(root, 0);
		} else
		{
			treeTex.putFloat(VOLUMELINK, -2);
		}
		
		
		/*
		 * visit and store the Children of the root cell
		 * the second run sets the neighborhood links
		 */
		for (int i = 0; i < 2; i++)
		{
			visitChildren(t, root);	
		} //for i
		
		
	} //Constructor
	
	
	private void storeTransformMatrix(Matrix4f m)
	{
		for(int i = 0; i < 2; i++)
		{
			treeTex.putFloat( m.m00 );
			treeTex.putFloat( m.m10 );
			treeTex.putFloat( m.m20 );
			
			treeTex.putFloat( m.m01 );
			treeTex.putFloat( m.m11 );
			treeTex.putFloat( m.m21 );
			
			treeTex.putFloat( m.m02 );
			treeTex.putFloat( m.m12 );
			treeTex.putFloat( m.m22 );
			
			treeTex.putFloat( m.m03 );
			treeTex.putFloat( m.m13 );
			treeTex.putFloat( m.m23 );
			
			m.invert();
		}
	} //storeTreeCells
	
	
	private void storeCell(Cell cell, Matrix4f m )
	{
		treeTex.position(cellCount * SIZEOF_CELL);
		cellCache.put( cell, treeTex.position() );
		
		storeTransformMatrix(m);
		
		cellCount++;
	} //storeCell
	
	
	private void storeChildrenLink(Cell cell)
	{
		if(cell.children != null)
		{
			treeTex.putFloat(cellCache.get(cell) + CHILDRENLINK, cellCache.get(cell.children[0]) / 16);
			System.out.println("CHILDRENLINK = " + cellCache.get(cell.children[0]) / 16 );
		}
	}
	
	
	private void storeFaceLinks(Cell cell, int pos)
	{
		int[] faces = {-1,-1,-1,-1,-1,-1};
		
		
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
		
		
		treeTex.position(pos+FACES);
		
		for (int i = 0; i < 6; i++)
		{
			treeTex.putFloat( faces[i] != -1 ? (float)faces[i] / 16f : -1);
		} //for i
		
	}
	
	
	private void storeVolumes(Cell cell, int pos)
	{
		int p = cellCount*SIZEOF_CELL + volumeCount*4;
		int volumes = cell.getVolumeCount();
		int link = -2; //isNode
		

		link = cell.children == null ? p : -2;
		
		if(cell.children == null && volumes == 0) //empty leaf
		{
			link = -1;
		}
		
		treeTex.putFloat(pos + VOLUMELINK, link);
		
		
		if(volumes > 0)
		{
			treeTex.position(p);
			
			for (int i = 0; i < volumes; i++)
			{
				treeTex.putFloat( cell.getVolume(i, null).getId() );
				volumeCount++;
			} //for
			
			treeTex.putFloat( -1f );
			volumeCount++;
		}
		
	}
	
	
	//obtains the dimension of the cell and stores it
	private void visitCell(Octree t, Cell cell)
	{
		if(cellCache.size() != t.getCellCount() )
		{
			cell.getExtent(t, min, max);
			storeCell( cell, getMatrix(min, max) );
		}
		else
		{
			int pos = cellCache.get(cell);
			storeFaceLinks(cell, pos);
			storeChildrenLink(cell);
			storeVolumes(cell, pos);	
		}
		
	}
	
	
	private void visitChildren(Octree t, Cell cell )
	{
		if (cell.children != null)
		{
			for (int i = 0; i < 8; i++)
			{
				visitCell(t, cell.children[i]);
			}
			
			for (int i = 0; i < 8; i++)
			{
				visitChildren(t, cell.children[i]);
			}
		} //if
	} //visitChildren
	
	
	
	private Matrix4f getMatrix(Point3d min, Point3d max)
	{
		tmp.add (min, max);
		tmp.scale (0.5);
		
		
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		
		m.m03 = (float)tmp.x;
		m.m13 = (float)tmp.y;
		m.m23 = (float)min.z;
		
		tmp.sub (max, min);
		
		m.m00 *= tmp.x;
		m.m11 *= tmp.y;
		m.m22 *= tmp.z;
		
		
		return m;
	}
	
	
	public ByteBuffer getTreeTex()
	{
		return treeTex;		
	}
	
	
	public int getSize()
	{
		return size;
	}
	
} //class
