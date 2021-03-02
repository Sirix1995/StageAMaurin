/*
 * Copyright (C) 2011 Abteilung Oekoinformatik, Biometrie und Waldwachstum, 
 * Buesgeninstitut, Georg-August-Universitaet Göttingen
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

package de.grogra.mtg;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.objects.PolygonMesh;
import de.grogra.math.TMatrix4d;
import de.grogra.mtg.MTGError.MTGPlantFrameException;
import de.grogra.rgg.Library;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * Class representing an MTG. 
 * Contents are in HashMap retrievable by keys specified in section "//MTGNode contents" of MTGKeys.java.
 * 
 * @see MTGKeys
 * @author yong
 * @since  2012-02-01
 */
public class MTGRoot extends MTGNode implements Renderable/*,Polygonizable*/
{
	protected PolygonMesh polygons;
	private double dist;
	protected int visibleSides = Attributes.VISIBLE_SIDES_BOTH;
	private MTGNodeData nodeData;
	
	public static final NType $TYPE;
	public static final NType.Field dist$FIELD;
	public static final NType.Field visibleSides$FIELD;
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MTGRoot.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}
		
		//test SVN server - yong 18 June 2012
		
		/*
		@Override
		public void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
			case 0:
				((MTGRoot) o).setPolygons((PolygonMesh)value);
				return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object object)
		{
			switch (id)
			{
			case 0:
				return ((MTGRoot) object).getPolygons();
			}
			return super.getObject (object);
		}
		*/
		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 0:
					((MTGRoot) o).dist = value;
					return;
			}
			super.setDouble(o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 0:
					return ((MTGRoot) o).dist;
			}
			return super.getDouble(o);
		}
		
		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((MTGRoot) o).visibleSides = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 1:
					return ((MTGRoot) o).visibleSides;
			}
			return super.getInt(o);
		}
	}

	static
	{
		$TYPE = new NType (MTGRoot.class);
		//$TYPE.addManagedField (polygons$FIELD = new _Field ("polygons", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 0));
		$TYPE.addManagedField (dist$FIELD = new _Field ("dist", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 0));
		$TYPE.addManagedField (visibleSides$FIELD = new _Field ("visibleSides", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		//$TYPE.declareFieldAttribute (polygons$FIELD, Attributes.MTG_POLYGONS);
		$TYPE.declareFieldAttribute (dist$FIELD, Attributes.MTG_DIST);
		$TYPE.declareFieldAttribute (visibleSides$FIELD, Attributes.MTG_VISIBLE_SIDES);
		//$TYPE.addDependency (polygons$FIELD.getAttribute (), Attributes.MTG_POLYGONS);
		initType();
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
		return new MTGRoot();
	}

	public MTGRoot()
	{
		super();
		this.polygons = null;
		this.nodeData = new MTGNodeData();
	}
	
	public void setPolygons(PolygonMesh mesh)
	{
		this.polygons = mesh;
	}
	
	public PolygonMesh getPolygons()
	{
		return this.polygons;
	}
	
	public Object getObject(String key)
	{
		return nodeData.getObject(key);
	}
	
	public void setObject(String key, Object obj)
	{
		if(nodeData==null)
			nodeData = new MTGNodeData();
		nodeData.setObject(key, obj);
	}
	
	public MTGNodeData getData()
	{
		return nodeData;
	}
	
	public void setData(MTGNodeData data)
	{
		this.nodeData = data;
	}

	/**
	 * Adds nodes to cache list. The cache list is stored in a hashmap and used by method plantFrame.
	 * @param node
	 * @throws MTGError.MTGPlantFrameException
	 */
	private void addNodeToNodeList(MTGNode node) throws MTGError.MTGPlantFrameException
	{
		Object nodeListObj = getObject(MTGKeys.MTG_NODE_NODELIST);
		if(nodeListObj==null)
			throw new MTGError.MTGPlantFrameException("Node list not instantiated before adding node to list.");
		
		ArrayList<MTGNode> nodeList = (ArrayList<MTGNode>)nodeListObj;			
		nodeList.add(node);
		node.mtgID = nodeList.size()-1;
	}
	
	public void deleteNodeList()
	{
		setObject(MTGKeys.MTG_NODE_NODELIST, null);
	}
	
	/**
	 * Refreshes cached node list. At the same time reassigns id to the nodes.
	 * The plant frame computation is dependent on the cached node list and id of the nodes.
	 * This cached list is cleared from memory at the end of method plantFrame.
	 * @throws MTGError.MTGPlantFrameException
	 */
	public void refreshNodeIndices() throws MTGError.MTGPlantFrameException
	{
		//obtain node list cache from hashmap
		Object nodeListObj = getObject(MTGKeys.MTG_NODE_NODELIST);
		
		//if cache list does not exist, instantiate one
		if(nodeListObj==null)
		{
			ArrayList<MTGNode> nodeList = new ArrayList<MTGNode>();
			setObject(MTGKeys.MTG_NODE_NODELIST, nodeList);
		}
		//if cache list exists, clear it.
		else
		{
			ArrayList<MTGNode> nodeList = (ArrayList<MTGNode>)nodeListObj;
			nodeList.clear();
		}
		
		//MTGRoot has id -1
		this.mtgID = -1;
		
		//call recursive method to traverse the whole MTG graph
		refreshNodeIndicesInternal(this);
	}
	
	/**
	 * Recursive method. Called by refreshNodeIndices to traverse MTG graph and add nodes to a cache list.
	 * At the same time re-assign id to the nodes.
	 * @param node
	 * @throws MTGError.MTGPlantFrameException
	 */
	private void refreshNodeIndicesInternal(MTGNode node) throws MTGError.MTGPlantFrameException
	{	
		//refinement connected nodes are added first
		for(Edge e = node.getFirstEdge(); e!=null; e=e.getNext(node) )
		{
			if((e.getSource() == node)&&(e.testEdgeBits(Graph.REFINEMENT_EDGE)))
			{
				addNodeToNodeList((MTGNode)e.getTarget()); 
				refreshNodeIndicesInternal((MTGNode)e.getTarget());
			}
		}
		
		//succ/branch connected nodes are added
		if(node!=this)
		{
			for(Edge e = node.getFirstEdge(); e!=null; e=e.getNext(node) )
			{
				if((e.getSource() == node)&&((e.testEdgeBits(Graph.SUCCESSOR_EDGE))||(e.testEdgeBits(Graph.BRANCH_EDGE))))
				{
					addNodeToNodeList((MTGNode)e.getTarget()); 
					refreshNodeIndicesInternal((MTGNode)e.getTarget());
				}
			}
		}
	}
	
	/**
	 * Removes inter-scale topological relations (i.e. successor and branching edges).
	 * @throws MTGPlantFrameException
	 */
	public void removeInterScaleTopoRelations() throws MTGError.MTGPlantFrameException
	{
		//get list of root nodes (nodes without topological father)
		int[] rootList = getRootIds(MTGKeys.MTG_ANY);

		//recursive comb of graph
		for(int i=0; i<rootList.length; ++i)
		{
			removeInterScaleTopoRelationsInternal(rootList[i]);
		}
	}

	/**
	 * Internal recursive function to remove inter-scale topological relations (i.e. successor and branching edges).
	 * @param originIndex
	 * @throws MTGPlantFrameException
	 */
	private void removeInterScaleTopoRelationsInternal(int originIndex) throws MTGError.MTGPlantFrameException
	{
		ArrayList<Integer> nodeQueue = new ArrayList<Integer>();
		int nodeIndex;

		boolean endOfLoop=false;
		//yongzhi 31 jan 2012 - note that this loop assumes that if a branch exists, a successor must exist.
		//                      if node only has topoSons connected with branching edges, infinite loop occurs.
		//                    - This is a direct translation of code in AMAP software.
		//                    - This is resolved by checking if nextNode==originIndex at end of loop. Loop is ended
		//                      if it is the same.
		while(!endOfLoop)
		{
			int[] topoSonsList = topoSons(originIndex, MTGKeys.MTG_ANY);

			if(topoSonsList.length == 0)
				endOfLoop=true;

			int nextNode = originIndex;

			for(int i=0; i<topoSonsList.length;++i)
			{
				nodeIndex=topoSonsList[i];

				int originScale = getScale(originIndex);
				int nodeScale = getScale(nodeIndex);

				//if topo relation between origin and node, remove topo relation
				if(originScale!=nodeScale)
				{
					MTGNode originNode = getNode(originIndex);
					MTGNode node = getNode(nodeIndex);
					originNode.removeEdgeBitsTo(node, edgeType(originIndex,nodeIndex), null);
				}
				//else continue traversal
				else
				{
					if(edgeType(originIndex,nodeIndex)==Graph.SUCCESSOR_EDGE)
					{
						nextNode=nodeIndex;
					}
					else
					{
						nodeQueue.add(new Integer(nodeIndex));
					}
				}
			}

			if(originIndex == nextNode)//this line and the next are additional and does not exist in original AMAP source.
				endOfLoop=true;
			else
				originIndex = nextNode;
		}

		while(!nodeQueue.isEmpty())
		{
			nodeIndex = nodeQueue.remove(0).intValue();
			removeInterScaleTopoRelationsInternal(nodeIndex);
		}
	}
	
	/**
	 * Updates cache of list of nodes in this MTG. Index of nodes in the cached list is required for plant frame computation.
	 * @throws MTGPlantFrameException 
	 */
	private void refreshNodeList() throws MTGPlantFrameException
	{
		this.refreshNodeIndices();
	}
	
	private Vector3d getSupportDirVector(int supportNodeIndex) throws MTGPlantFrameException
	{
		Vector3d supportDir=null;
		if(supportNodeIndex == MTGKeys.MTG_UNKNOWN_KEYCODE)
			supportDir = new Vector3d(0,0,1);//default direction vector is pointing in z-axis
		else
		{
			MTGBranchElement element = searchForElement(supportNodeIndex);
			if(element==null)
				throw new MTGPlantFrameException("Node index with no corresponding branch element.");
			supportDir = new Vector3d(element.getDirp());
		}
		return supportDir;
	}
	
	private Vector3d getSupportDirsVector(int supportNodeIndex) throws MTGPlantFrameException
	{
		Vector3d supportDirs=null;
		if(supportNodeIndex == MTGKeys.MTG_UNKNOWN_KEYCODE)
			supportDirs = new Vector3d(1,0,0);//default direction vector is pointing in z-axis
		else
		{
			MTGBranchElement element = searchForElement(supportNodeIndex);
			if(element==null)
				throw new MTGPlantFrameException("Node index with no corresponding branch element.");
			supportDirs = new Vector3d(element.getDirs());
		}
		return supportDirs;
	}
	
	private double angleAlongXAxis(Point3d vec)
	{
		double angleAlongX=0;
		if((vec.y <=0)&&(vec.z >= 0)) //1st quadrant
		{
			if(vec.y!=0)
			{
				if(vec.z!=0)
					angleAlongX = (Math.atan(Math.abs(vec.y)/vec.z));
				else
					angleAlongX = (Math.PI/2.0);
			}
		}
		else if((vec.y <=0)&&(vec.z < 0)) //2nd quadrant
		{
			if(vec.y == 0)
				angleAlongX = (Math.PI);
			else
			{
				angleAlongX = ((Math.PI/2.0) + Math.atan((Math.abs(vec.z))/(Math.abs(vec.y))));
			}
		}
		else if((vec.y >0)&&(vec.z < 0)) //3rd quadrant
		{
			angleAlongX = (Math.PI + (Math.atan(vec.y/(Math.abs(vec.z)))));
		}
		else if((vec.y >0)&&(vec.z >= 0)) //4th quadrant
		{
			if(vec.z==0)
				angleAlongX = (3.0*Math.PI/2.0);
			else
				angleAlongX = ((3.0*Math.PI/2.0)+Math.atan(vec.z/vec.y));
		}	
		angleAlongX *= -1.0; //anti-clockwise rotation
		
		return angleAlongX;
	}
	
	private double angleAlongYAxis(Point3d vec)
	{
		double angleAlongY=0;
		if((vec.x >=0)&&(vec.z >= 0)) //1st quadrant
		{
			if(vec.x!=0)
			{
				if(vec.z!=0)
					angleAlongY = (Math.atan(vec.x/vec.z));
				else
					angleAlongY = (Math.PI/2.0);
			}
		}
		else if((vec.x >=0)&&(vec.z < 0)) //2nd quadrant
		{
			if(vec.x == 0)
				angleAlongY = (Math.PI);
			else
			{
				angleAlongY = ((Math.PI/2.0) + Math.atan((Math.abs(vec.z))/vec.x));
			}
		}
		else if((vec.x <0)&&(vec.z < 0)) //3rd quadrant
		{
			angleAlongY = (Math.PI + (Math.atan(Math.abs(vec.x)/(Math.abs(vec.z)))));
		}
		else if((vec.x <0)&&(vec.z >= 0)) //4th quadrant
		{
			if(vec.z==0)
				angleAlongY = (3.0*Math.PI/2.0);
			else
				angleAlongY = ((3.0*Math.PI/2.0)+Math.atan(vec.z/Math.abs(vec.x)));
		}	
		angleAlongY *= -1.0; //anti-clockwise rotation
		
		return angleAlongY;
	}
	
	/**
	 * Computes the relative rotation angles from refDir to new Dir
	 * @param refDir
	 * @param newDir
	 * @return
	 * @throws MTGPlantFrameException
	 */
	private Vector3d computeInterNodeRotation(Vector3d refDir, Vector3d newDir) throws MTGPlantFrameException
	{
		Point3d refPt = new Point3d(refDir.x,refDir.y,refDir.z);
		Point3d newPt = new Point3d(newDir.x,newDir.y,newDir.z);
		try
		{
			//rotate refDir and newDir, so that refDir is on xz-plane
			double angleAlongX = angleAlongXAxis(refPt);
			Point3d refPtXZPlane = new Point3d();
			Point3d newPtXZPlane = new Point3d();
			Matrix4d rotateToXZPlane = new Matrix4d();
			rotateToXZPlane.setIdentity();
			rotateToXZPlane.rotX(angleAlongX);
			rotateToXZPlane.transform(refPt, refPtXZPlane);
			rotateToXZPlane.transform(newPt, newPtXZPlane);
			
			//rotate refDir and newDir, so that refDir is on z-axis
			double angleAlongY = angleAlongYAxis(refPtXZPlane);
			Point3d refPtZ = new Point3d();
			Point3d newPtZ = new Point3d();
			Matrix4d rotateToZAxis = new Matrix4d();
			rotateToZAxis.setIdentity();
			rotateToZAxis.rotY(angleAlongY);
			rotateToZAxis.transform(refPtXZPlane,refPtZ);
			rotateToZAxis.transform(newPtXZPlane,newPtZ);
			
			//Vector to contain result rotation angles in the axes
			Vector3d result = new Vector3d();
			
			//rotate along y-axis to place newDir on y-z plane
			double angleY = angleAlongYAxis(newPtZ);
			Point3d newPtYZPlane = new Point3d();
			Matrix4d rotateToYZPlane = new Matrix4d();
			rotateToYZPlane.setIdentity();
			rotateToYZPlane.rotY(angleY);
			rotateToYZPlane.transform(newPtZ,newPtYZPlane);
			
			//rotate along x-axis to place newDir on z-axis
			double angleX = angleAlongXAxis(newPtYZPlane);
			
			return new Vector3d(-angleX,-angleY,0);
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	/**
	 * Loops through MTGBranches (created and computed in plantFrame).
	 * Computes the transformation of the direction vector of the previous branch element
	 * to the direction vector of the next branch element.
	 * This transformation is stored in the 'transformation' variable in MTGNodes.
	 * @throws MTGPlantFrameException
	 */
	private void computeInterNodeTransformation() throws MTGPlantFrameException
	{
		ArrayList<MTGBranch> branches = getBranches();
	
		//if no branches exists, unable to perform inter-branch element transformation computation
		if(branches==null)
			return;

		//loop through the branches
		for(int i=0; i<branches.size();++i)
		{
			MTGBranch branch = branches.get(i);
			
			//get preceeding node that connects to the first element of this branch
			int supportNodeIndex = branch.getSupportNodeIndex();
			
			//direction vector of support node
			Vector3d supportDir = getSupportDirVector(supportNodeIndex);
			if(supportDir==null)
				throw new MTGPlantFrameException("No preceeding direction vector found.");
			//direction secondary vector of support node
			Vector3d supportDirs = getSupportDirsVector(supportNodeIndex);
			if(supportDirs==null)
				throw new MTGPlantFrameException("No preceeding secondary direction vector found.");

			//loop through elements in the branch - each element references a node in the graph
			for(int j=0; j<branch.getElementCount(); ++j)
			{
				MTGBranchElement element = branch.getElement(j);
				
				//index of node corresponding to this branch element
				int nodeIndex=element.getNodeIndex();
				
				//check if a valid node instance is referenced by this branch element
				if(nodeIndex==MTGKeys.MTG_UNKNOWN_KEYCODE)
					throw new MTGPlantFrameException("Branch element referencing no node instance.");
				MTGNode node = getNode(nodeIndex);
				if(node == null)
					throw new MTGPlantFrameException("Branch element referencing no node instance.");
				
				//direction vector for this branch element
				Vector3d dir = element.getDirp();
				
				//secondary direction vector for this branch element
				Vector3d dirs = element.getDirs();
				
				if(dir==null)
					throw new MTGPlantFrameException("No direction vector found for branch element.");
				if(dirs==null)
					throw new MTGPlantFrameException("No secondary direction vector found for branch element.");
				
				//vector for containing relative rotation transformation
				Vector3d rotTrans = null;
				
				//vector for containing relative secondary rotation transformation
				Vector3d rotSecTrans = null;
				//rotSecTrans = computeInterNodeRotation(new Vector3d(0,0,1), dirs);
				
				//compute relative rotation transformation
				if(j==0)
				{
					rotTrans = computeInterNodeRotation(supportDir, dir);
					rotSecTrans = computeInterNodeRotation(supportDirs, dirs);
				}
				else
				{
					MTGBranchElement prevElement = branch.getElement(j-1);
					
					//direction vector for previous branch element
					// no need to check if null here, since prev element would have already 
					//used its direction vector in prev computation step
					Vector3d prevDir = prevElement.getDirp(); 
					Vector3d prevDirs = prevElement.getDirs();
					
					rotTrans = computeInterNodeRotation(prevDir, dir);
					rotSecTrans = computeInterNodeRotation(prevDirs, dirs);
				}
				
				//check if inter node rotation transformation was successfully computed
				if(rotTrans==null)
					throw new MTGPlantFrameException("Fail to compute inter-node rotation transformation.");
				if(rotSecTrans==null)
					throw new MTGPlantFrameException("Fail to compute inter-node secondary rotation transformation.");
				
				Matrix4d combinedRotation = combinePriSecRotation(rotTrans.x,
						rotTrans.y,
						rotTrans.z,
						0,
						0,
						0);
				
				//set rotation into node
				//node.setRotation(rotTrans.x, rotTrans.y, 0);
				node.setTransform(combinedRotation);
				
				//set length into node. 
				//NOTE:this length member variable is used by turtle interpretation.
				//     NOT the same as member variable 'Length' which is a MTG standard attribute.
				node.length = (float)(element.getLength());
				node.diameter = (float)(element.getBotDia());
				if(node.diameter < 0.001f)
					node.diameter = 0.001f;
			}
		}
	}
	
	public Matrix4d combinePriSecRotation (double x, double y, double z, double x2, double y2, double z2)
	{
		//pri rotation matrix - into matrix t
		TMatrix4d t = new TMatrix4d ();
		t.rotZ (z);
		Matrix4d m = new Matrix4d ();
		m.rotY (y);
		t.mul (m);
		m.rotX (x);
		t.mul (m);
		
		//sec rotation matrix = into matrix t2
		TMatrix4d t2 = new TMatrix4d ();
		t2.rotZ (z2);
		Matrix4d m2 = new Matrix4d ();
		m2.rotY (y2);
		t2.mul (m2);
		m2.rotX (x2);
		t2.mul (m2);
		
		//combine rotations - into matrix t
		t.mul(t2);
		
		return t;
	}
	
	public void plantFrameDelete(int scale)
	{
		ArrayList<MTGNode> toDelete = new ArrayList<MTGNode>();
		for(Edge e=this.getFirstEdge(); e!=null; e=e.getNext(this))
		{
			if((e.getSource()==this)&&(e.testEdgeBits(Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE)))
			{
				MTGNode target = (MTGNode)(e.getTarget());
				if(target.mtgScale == scale)
				{
					toDelete.add(target);
				}
			}
		}
		
		for(int i=0; i<toDelete.size(); ++i)
		{
			this.removeEdgeBitsTo(toDelete.get(i), Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE, null);
		}
	}

	/**
	 * Computes the frame of the plant at the specified scale. For a static situation only.
	 * The frame of the plant is a collection of branches. The branch elements contain a top diameter, bottom diameter,
	 * length, positional coordinates and directional coordinates.
	 * 
	 * Root nodes are nodes without topological parents, i.e. no father node with successor or branching edge to them.
	 *  
	 * @param scale
	 * @throws MTGPlantFrameException
	 */
	public void plantFrame(int scale, double dist) throws MTGError.MTGPlantFrameException
	{
		try
		{
			this.dist = dist;
	
			//load dressing data - 
			//TODO: load actual file. now just use default values
			MTGDressingFile dressingFile = new MTGDressingFile();
			setObject(MTGKeys.MTG_NODE_DRESSING,dressingFile);
	
			//plant origin
			setObject(MTGKeys.MTG_NODE_PLANT_ORIGIN,new Vector3d(0,0,0));
	
			//plant min/max
			Object pMin = getObject(MTGKeys.MTG_NODE_PLANT_MIN);
			if(pMin==null)
				setObject(MTGKeys.MTG_NODE_PLANT_MIN, new Vector3d(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE));
			Object pMax = getObject(MTGKeys.MTG_NODE_PLANT_MAX);
			if(pMax==null)
				setObject(MTGKeys.MTG_NODE_PLANT_MAX, new Vector3d(Double.MIN_VALUE,Double.MIN_VALUE,Double.MIN_VALUE));
	
			//refresh node list cache
			refreshNodeList();

			//remove all successor and branch edges of selected scale from this root node
			removeAllSuccOrBranchEdges(scale);
			
			//get list of root nodes (nodes without topological father)
			int[] rootList = getRootIds(scale);
			
			//yong 23 apr 2012 - use only 1 root for each scale - according to MTG data structure
//			if(rootList.length>1)
//			{
//				int index = rootList[0];
//				rootList = new int[1];
//				rootList[0] = index;
//			}
	
			Object branchesObj = getObject(MTGKeys.MTG_NODE_BRANCHES);
			if(branchesObj==null)
			{
				ArrayList<MTGBranch> branches = new ArrayList<MTGBranch>();
				setObject(MTGKeys.MTG_NODE_BRANCHES,branches);				//branches for all plants in MTG
			}
			else
			{
				ArrayList<MTGBranch> branches = (ArrayList<MTGBranch>)branchesObj;
				branches.clear();
			}
	
			//Object plantCountObj = getObject(MTGKeys.MTG_NODE_PLANT_COUNT);
			//if(plantCountObj==null)
			//reset plant count
			{
				Integer plantCount = new Integer(0);
				setObject(MTGKeys.MTG_NODE_PLANT_COUNT,plantCount);			//number of plants in MTG
			}
	
			//compute branches
			for(int i=0; i<rootList.length; ++i)
			{
				lookForBranches(rootList[i],scale,0);
			}
	
			//mtg type
			int mtgType = mtgType();
	
			//geometry computation
			switch(mtgType)
			{
			case MTGKeys.MTG_TYPE_COORD_TRI_REF:
			case MTGKeys.MTG_TYPE_COORD_CARTESIAN:
				computeGeometry(mtgType, scale);
				break;
			case MTGKeys.MTG_TYPE_STANDARD:
			default:
				computeGeometryStandard(mtgType,scale);
				break;
			}
	
			//normalize branches
			downSizeBranches();
			
			//refresh mesh - mesh recomputation on demand. TODO: remove mesh
			polygons=null;
			
			//compute relative transformations from each node to the next and store them in the node
			computeInterNodeTransformation();
			
			//Based on the selected scale, 
			//connect this MTGRoot node to 'base' or 'first' node of the selected scale of 
			//visualization with successor edge. Rendering traversal does not traverse refinement edges.
			connectToVisibleScale(rootList);
			
			//clear cached node list
			//this.setData(null);
			clearNonPersistantData();
		}
		catch(Throwable t)
		{
			System.out.println("Unable to compute Plant Frame.");
		}
	}
	
	private void clearNonPersistantData()
	{
		Object classInfo = getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		Object mtgFeaturesObj = getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		MTGNodeData persistantInfo = new MTGNodeData();
		persistantInfo.setObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES, classInfo);
		persistantInfo.setObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES, mtgFeaturesObj);
		setData(persistantInfo);
	}

	/**
	 * Computes geometry information for each branch/branch element, given that the MTG file has
	 * no cartesian coordinates and no triangular reference coordinates specified.
	 * @param coordType
	 * @param scale
	 * @throws MTGPlantFrameException
	 */
	private void computeGeometryStandard(int coordType, int scale) throws MTGError.MTGPlantFrameException
	{
		if(coordType!= MTGKeys.MTG_TYPE_STANDARD)
			throw new MTGError.MTGPlantFrameException("Unable to compute standard MTG geometry.");

		//number of branches
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("No branch information available for geometry computation.");
		int branchesCount = branches.size();

		//calculate azimuth
		computeAzimuth(scale);
		
		setupCoordinateVariables();

		//Calculating category,alpha,length,diameter,coordinates for each branch
		for(int i=0; i<branchesCount; ++i)
		{	
			MTGBranch branch = branches.get(i);

			//create new voxel if branch is root
			if(branch.getSupportNodeIndex() == MTGKeys.MTG_UNKNOWN_KEYCODE)
				newTrunk(i);

			//calculate branch length
			computeBranchLengthsStandard(i);

			//calculate diameters
			computeDiameters(i, scale);
			
			//calculate category
			computeCategory(i);
			
			//calculate alpha
			computeAlpha(i);

			//calculate coordinates
			computeBranchCoordinatesStandard(i, scale);
		}
	}
	
	double lookForRatio(int father,int vertex) throws MTGError.MTGPlantFrameException
	{
		double result=MTGKeys.MTG_UNKNOWN_KEYCODE;
		double dec_ratio=0.1;

		int[] sons = topoSons(father,Graph.BRANCH_EDGE);
		int nb=(sons==null)?0:sons.length;

		if (nb!=0)
		{
			result=1+(nb+1)*dec_ratio;
		}
		else
		{
			result=1;
		}

		for (int i=0;i<nb;i++)
		{
			if (vertex!=sons[i])
			{
				result-=dec_ratio;
			}
		}

		if(result == MTGKeys.MTG_UNKNOWN_KEYCODE)
			throw new MTGError.MTGPlantFrameException("Error looking for ratio while computing coordinates");

		return result;
	}

	/**
	 * Find root(first node) of the compositional group node 'startv' belongs to.
	 * @param startv
	 * @param etype
	 * @return
	 */
	public int findLocalTopoRoot(int startv, int etype) throws MTGError.MTGPlantFrameException 
	{
		MTGNode node = getNode(startv);
		if(node==null)
			throw new MTGError.MTGPlantFrameException("Node does not exists for finding local topological root.");

		int father = startv;
		int cf = MTGKeys.MTG_UNKNOWN_KEYCODE;
		int cfstartv = MTGKeys.MTG_UNKNOWN_KEYCODE;

		do {
			startv = father;
			cfstartv = compoFather(startv); // can be UNDEF
			father = topoFather(startv, etype);
			if (father != MTGKeys.MTG_UNKNOWN_KEYCODE)
				cf = compoFather(father);
			else 
				cf = MTGKeys.MTG_UNKNOWN_KEYCODE;

		} while(father != MTGKeys.MTG_UNKNOWN_KEYCODE && cf == cfstartv);

		return startv;
	}
	
	private int pathLengthInternal(int v1, int v2, int filter)
	{
		int f = v2;
		int dist = 0;
		if (v1 == v2) return 0;

		while(f != MTGKeys.MTG_UNKNOWN_KEYCODE) {

			f = topoFather(f, filter);
			dist++;
			if (f == v1) return dist;
		}

		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}
	
	public int pathLength(int father, int vertex, int edge)
	{
	  int result=0;

	  //yong 23 apr 2012 - in AMAP, a default edge type is defined in the plant object instance.
	  //                 - in GroIMP, this MTGRoot node is equivalent to the plant object instance in AMAP.
	  //			     - however, this implement ignores this default edge specification.
//	  if (_defaultEdge==NONE)
//	  {
//	    result= _mtg->pathLength(father, vertex, edge);
//	  }
//	  else
	  {

	    //if (_level!=getScale(vertex))
	    //{
	      result=pathLengthInternal(father,vertex,edge);

//	      if (result==0)
//	      {
//	        vertex=compoFather(vertex,_level);
//	        father=compoFather(vertex,_level);
//	      }
//	    }
//
//	    if ((result==0) && (vertex!=UNDEF) && (father!=UNDEF))
//	    {
//	      VId tmp_vertex=vertex;
//	      result=0;
//	      Boolean end_of_loop=((tmp_vertex==UNDEF) || (tmp_vertex==father));
//	      while(!end_of_loop)
//	      {
//	        tmp_vertex=topoFather(vertex,edge);
//
//	        if ((tmp_vertex==UNDEF) || (tmp_vertex==father))
//	        {
//	          end_of_loop=TRUE;
//	        }
//	        else
//	        {
//	          result++;
//	        }
//	      }
//
//	      if (tmp_vertex==UNDEF)
//	      {
//	        result=0;
//	      }
//	    }
	  }

	  return result;
	}
	
	/**
	 * Compute ramification order ratio
	 * @param father
	 * @param vertex
	 * @return
	 * @throws MTGPlantFrameException 
	 */
	private double computeRatio( int father,int vertex) throws MTGError.MTGPlantFrameException
	{
		double result=MTGKeys.MTG_UNKNOWN_KEYCODE;

		int detail=location(vertex);

		if (getScale(vertex)>getScale(detail))
		{
			result=lookForRatio(father,vertex);
		}
		else
		{
			
			int[] list=compoSonsIds(father,getScale(detail));

			if(list==null)
				throw new MTGError.MTGPlantFrameException("Invalid null for compositional sons of given node");
			
			if (list.length>0)
			{
				int topo_root=findLocalTopoRoot(detail,MTGKeys.MTG_ANY);
				double r1=(double)pathLength(topo_root,detail,MTGKeys.MTG_ANY);
				if (r1==0)
				{
					r1=1;
				}
				double r2=(double)(list.length);
				result=r2/r1;
			}
			else
			{
				result=1;
			}
		}

		if(result==MTGKeys.MTG_UNKNOWN_KEYCODE)
			throw new MTGError.MTGPlantFrameException("Invalid ramification ratio while computing standard plant frame geometry.");

		return result;
	}

	/**
	 * Branch - Coordinates - Compute coordinates for a branch element
	 * @param element
	 * @param insertAngle
	 * @return
	 * @throws MTGError.MTGPlantFrameException
	 */
	private Vector3d computeElementCoordinatesStandard(MTGBranchElement element, double insertAngle, int scale) throws MTGError.MTGPlantFrameException
	{
		int nodeIndex = element.getNodeIndex();
		MTGNode node = getNode(nodeIndex);
		
		if(node==null)
			throw new MTGError.MTGPlantFrameException("Invalid node for branch element during coordinate computation.");
		
		int fatherIndex = topoFather(nodeIndex, MTGKeys.MTG_ANY);
		
		//if this node has no topological father - i.e. a root node
		if(fatherIndex==MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			double x0=0;
			double y0=0;
			double z0=0;
			
			this.updatePlantMinMax(new Vector3d(x0,y0,z0));
			
			Vector3d dirp = new Vector3d(0,0,1);
			Vector3d dirs = new Vector3d(1,0,0);
			
			//yong 22 apr - in AMAP, check if custom euler computation function exists
			//            - this implementation assumes no custom functions
			
			
			Vector3d origin = new Vector3d(x0,y0,z0);
		    double alpha=Math.PI/2.0;
		    double beta=0;
		    double length = element.getLength();

		    if (length!=0.)
		    {
		      alpha=computeElevationAlpha(dirp);
		      beta=computeAzimuthBeta(dirp);
		    }
		    
		    //add to origin
		    Vector3d _origin = (Vector3d)(getObject(MTGKeys.MTG_NODE_COORD_ORIGIN));
		    setObject(MTGKeys.MTG_NODE_COORD_ORIGIN,new Vector3d(origin.x + _origin.x,
		    													origin.y + _origin.y,
		    													origin.z + _origin.z));
		    
		    element.setDirp(dirp);
		    element.setDirs(dirs);
		    element.setOrigin(origin);
		    element.setAlpha(alpha);
		    element.setBeta(beta);
		    
		    return new Vector3d(dirp.x * length, dirp.y * length, dirp.z * length);
		}
		else
		{
			double beta=element.getBeta();
		    double alpha=0;
		    double ratio=1;

		    MTGBranchElement fatherElement=searchForElement(fatherIndex);
		    Vector3d v = null;
		    
		    if(edgeType(fatherIndex,nodeIndex)==Graph.BRANCH_EDGE)
			{
		    	alpha = insertAngle;
		    	ratio = computeRatio(fatherIndex,nodeIndex);
		    	beta = element.getBeta();
		    	
		    	
		    	MTGDressingFile dressing = getDressingFile();
		    	if(dressing==null)
		    		throw new MTGError.MTGPlantFrameException("Dressing not available for computing branch coordinates.");
		    	
		    	if(dressing.isAlphaRelative())
		    	{
		    		v=putOn(fatherElement,element, alpha, beta, ratio,scale);
		    	}
		    	else
		    	{
		    		//yong 23 apr - default dressing is always alpha relative
		    		//            - implementation here is delayed until dressing file is implemented
		    		//v=putOnAbs(fatherElement,element, alpha, beta, ratio );
		    	}
			}
		    else
		    {
		    	v=putOn(fatherElement,element,alpha,beta,ratio,scale);
		    }
		    
		    return v;
		}
	}
	
	/**
	 * Arbitrary secondary direction given the primary direction angles
	 * @param b
	 * @param a
	 * @param c
	 * @return
	 */
	private Vector3d findSecondaryDirection(double b, // second euler angle
											double a, // first euler angle
											double c  // third euler angle
			) 
	{
		return new Vector3d(-Math.sin(a)*Math.cos(c) + Math.cos(a)*Math.sin(b)*Math.sin(c),
				Math.cos(a)*Math.cos(c) + Math.sin(a)*Math.sin(b)*Math.sin(c),
				Math.cos(b)*Math.sin(c));
	}
	
	private double getDefaultPsi(int scale)
	{
		MTGDressingFile dressing = this.getDressingFile();
		
		//default psi value from dressing
		return ((double)dressing.getDefaultPsi())/((double)scale);
	}
	
	Vector3d putOn(MTGBranchElement base, MTGBranchElement element, double alpha0, double beta,double ratio,int scale) throws MTGError.MTGPlantFrameException
	{

		if(base.getLength()<0)
			throw new MTGError.MTGPlantFrameException("Invalid length for base element.");

		if(getNode(base.getNodeIndex())==null)
			throw new MTGError.MTGPlantFrameException("Invalid base element.");

		double alp=base.getAlpha();
		double bet=base.getBeta();
		double length_of_support=base.getLength();
		Vector3d origin_of_support=base.getOrigin();

		double alpha=Math.PI/2.0-alpha0;

		Vector3d w=base.getDirp();
		Vector3d v=base.getDirs();
		
		//yong 23 apr 2012
		//this branch if not reachable by the root node. 
		//the base element is therefore not properly initialized
		//although this does not conform to the data structure, it is better not to throw
		//an error so that the valid portion of the data structure is still shown
		if((w==null)||(v==null))
			return null;
		
		Vector3d u=base.getDirt();

		w.normalize();
		u.normalize();
		v.normalize();


		// (O, u, v, w) is the benchmark against which the coordinates
		// Have been calculated.

		// Calculate the coordinates of the vector relative to the reference (u, v, w)
		// The angles alpha and beta are assumed relative
		Vector3d direction;
		Vector3d direction_s;

		double length;

		//yong 23 apr 2012 - no custom euler computation function in this implementation
		// Calcul avec les angles d'euler
		//		if (geo->euler(element.getVertex())) {
		//
		//			computeEulerAngles(&element, geo, direction, direction_s);
		//
		//			// ESSAI: le texte suivant est inserre
		//			Measures* len = geo->_length;
		//			ValType result= len->lookForFeature(element.getVertex());
		//
		//			if (result!= LUNDEF) length = result;
		//			else {
		//				length = ((CoordLength *)len)->getMinLength(element.getVertex());
		//			}
		//			// FIN ESSAI
		//
		//		}
		//else { // If not using alpha and beta (azimuth and insertion / bearer)
		{
			//direction = new Vector3d(alpha,beta);
			direction = new Vector3d();
			setElevationAzimuth(direction, alpha, beta);

			// We bring back the coordinates to global orthonormal
			//direction.changeBenchmark(u,v,w); // We pass in the global coordinate
			changeBenchmark(direction, u, v, w);

			// End of changes to the calculation of the origin.
			direction.normalize();

			// Calculate the secondary direction.
			// This direction is arbitrary unless the roll angle is given

			double roulis= 0; // If the roll is not given, it is zero
			roulis = getDefaultPsi(scale);
			//yong 23 apr - no custom psi
			//if (geo->_psi) {
			//	roulis= geo->_psi->lookForFeature(element.getVertex());
			//	if (roulis == LUNDEF) 
			//		roulis = 0;
			//}
			
			direction_s = findSecondaryDirection(alpha, beta, roulis);
			// direction_s.multiply(-1.0);

			//direction_s.changeBenchmark(u,v,w);
			changeBenchmark(direction_s,u,v,w);
			direction_s.normalize();

			length = element.getLength();
		}

		Vector3d basedirp=base.getDirp();

		//basedirp.norm();

		// Heuristic: when nothing is known, the position of the door
		// Is a fraction of the length of the wearer dependent
		// Number of components of the carrier (see computeRatio)
		// basedirp.multiply(length_of_support/ratio);
		// basedirp.multiply(length_of_support);

		Vector3d new_origin = new Vector3d();
		new_origin.add(basedirp, origin_of_support);

		

		element.setDirp(direction);
		element.setDirs(direction_s);

		element.setOrigin(new_origin);
		double al=0,be=0;

		if (direction.length()!=0)
		{
			al=this.computeElevationAlpha(direction);
			be=this.computeAzimuthBeta(direction);
		}

		element.setAlpha(al);
		element.setBeta(be);

		Vector3d result = new Vector3d();
		result.scale(length, direction);
		// Update the fields
		result.add(result,element.getOrigin());

		return result;
	}
	
	/**
	 * Branch - Alpha - Computes the alpha for each branch element.
	 * @param branchIndex
	 * @throws MTGPlantFrameException 
	 */
	private void computeAlpha(int branchIndex) throws MTGError.MTGPlantFrameException
	{
		//get branch instance
		MTGBranch branch = getBranch(branchIndex);
		
		//get index of branch base element
		int branchBase = branch.baseOfBranch();
		
		//compute angle using branch base element. set value to this branch angle.
		branch.setAlpha(computeElementAngle(branchBase));
	}
	
	/**
	 * Branch - Alpha - Computes the alpha for each branch element.
	 * @param branchIndex
	 * @throws MTGPlantFrameException 
	 */
	private double computeElementAngle(int branchBaseIndex)
	{
		double result=-1;

		MTGNode node = getNode(branchBaseIndex);
		if(node.hasAlpha())
		{
			result = node.Alpha;
		}
		else
		{
			result = lookUpForAlpha(branchBaseIndex);
			if(result!=-1)
				return result;
			
			result = lookDownForAlpha(branchBaseIndex);
		}
		return result;
	}
	
	/**
	 * Branch - Alpha - Searches downwards in scale hierarchy for alpha attribute value for the specified node.
	 * @param nodeIndex
	 * @return diameter attribute value
	 */
	private double lookDownForAlpha(int nodeIndex)
	{
		MTGNode node = getNode(nodeIndex);
	
		if(node.hasAlpha())
			return node.Alpha;
		else
		{
			int[] compoSonsIndices = compoSonsIds(nodeIndex);
			if(compoSonsIndices!=null)
				if(compoSonsIndices.length>=1)
					return lookDownForAlpha(compoSonsIndices[0]);
		}
		return -1;
	}
	
	/**
	 * Branch - Alpha - Searches upwards in scale hierarchy for alpha attribute value for the specified node.
	 * @param nodeIndex
	 * @return alpha attribute value
	 */
	private double lookUpForAlpha(int nodeIndex)
	{
		MTGNode node = getNode(nodeIndex);
		
		if(node.hasAlpha())
			return node.TopDia;
		else
		{
			int compoFatherIndex = compoFather(nodeIndex);

			if(compoFatherIndex!=MTGKeys.MTG_ROOT_NODE)
			{
				int[] fathersSons = this.compoSonsIds(compoFatherIndex);
				if(fathersSons.length>=1)
				{
					//inherit alpha only if this node is the first compositional son of its compositional father.
					if(fathersSons[0]==nodeIndex)
						return lookUpForAlpha(compoFatherIndex);
				}
			}
		}
		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}

	
	/**
	 * Compute coordinates of branch elements. Given that MTG data does not contain cartesian 
	 * coordinates or triangle reference coordinates.
	 * @param branchIndex
	 * @throws MTGError.MTGPlantFrameException
	 */
	private void computeBranchCoordinatesStandard(int branchIndex, int scale) throws MTGError.MTGPlantFrameException
	{
		MTGBranch branch = getBranch(branchIndex);
		
		MTGDressingFile dressing = this.getDressingFile();
		boolean computeCoordinatesWithForm = false;
		if(dressing!=null)
		{
			//TODO: implement coordinate calculation with Form
			/*MTGForm form = */dressing.getBranchForm(branch.category);
			//if(form!=null)
			// .......
		}
		
		//compute coordinates with form obtained from dressing
		if(computeCoordinatesWithForm)
		{
			//TODO: implement coordinate calculation with Form
		}
		//compute coordinate without form
		else
		{
			double alpha = branch.getAlpha();
			
			int elementCount = branch.getElementCount();
			
			if(alpha==MTGKeys.MTG_UNKNOWN_KEYCODE)
			{
				//default alpha value from dressing, convert to radians from degree
				alpha = dressing.getDefaultAlpha()/dressing.getAlphaUnit();
			}
			
			for(int i=0; i<elementCount; ++i)
			{
				Vector3d limit = this.computeElementCoordinatesStandard(branch.getElement(i), alpha, scale);
				
				//set limit of coordinates into plant bounding box
				if(limit!=null)
					this.updatePlantMinMax(limit);
			}
		}
	}

	/**
	 * Down-scales the geometry in branches for rendering
	 * @throws MTGPlantFrameException
	 */
	private void downSizeBranches() throws MTGError.MTGPlantFrameException
	{
		//Get scale factor
		Vector3d max = (Vector3d) getObject(MTGKeys.MTG_NODE_PLANT_MAX);
		Vector3d min = (Vector3d) getObject(MTGKeys.MTG_NODE_PLANT_MIN);

		double deltaX = max.x - min.x;
		double deltaY = max.y - min.y;
		double deltaZ = max.z - min.z;
		double scaleFactor = 1.0f;

		if(deltaX>=deltaY)
		{
			if(deltaX>=deltaZ)
				scaleFactor = 1.0f/deltaX;
			else
				scaleFactor = 1.0f/deltaZ;
		}
		if(deltaY>deltaX)
		{
			if(deltaY>=deltaZ)
				scaleFactor = 1.0f/deltaY;
			else
				scaleFactor = 1.0f/deltaZ;
		}

		//fit model in a 10x10x10 space
		scaleFactor *= 10.0;

		//Perform scale
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("Branches do not exist for normalization.");

		for(int i=0; i<branches.size(); ++i)
		{
			MTGBranch branch = branches.get(i);

			for(int j=0; j<branch.getElementCount(); ++j)
			{
				MTGBranchElement element = branch.getElement(j);

				Vector3d origin = element.getOrigin();
				if(origin!=null)
				{
					origin.scale(scaleFactor);
					element.setOrigin(origin);
				}

				double length = element.getLength();
				if(length!=0)
					element.setLength(length*(double)scaleFactor);

				double topdia = element.getTopDia();
				if(topdia!=0)
					element.setTopDia(topdia*(double)scaleFactor);

				double botdia = element.getBotDia();
				if(botdia!=0)
					element.setBotDia(botdia*(double)scaleFactor);
			}
		}
	}

	/**
	 * Computes geometry information for each branch/branch element, given that the MTG file has
	 * Cartesian coordinates or triangular reference coordinates specified.
	 * @param coordType
	 * @param scale
	 * @throws MTGPlantFrameException
	 */
	private void computeGeometry(int coordType, int scale) throws MTGError.MTGPlantFrameException
	{
		if( (coordType!= MTGKeys.MTG_TYPE_COORD_TRI_REF) && (coordType!= MTGKeys.MTG_TYPE_COORD_CARTESIAN) )
			throw new MTGError.MTGPlantFrameException("Unable to compute coordinate MTG geometry.");

		//number of branches
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("No branch information available for geometry computation.");
		int branchesCount = branches.size();

		//calculate azimuth
		computeAzimuth(scale);

		//initialize some variables used in coordinate computation
		//i.e. origin point, bounding boxes (collection of bounding boxes is known as MTGSquares)
		setupCoordinateVariables();

		//Calculating category,alpha,length,diameter,coordinates for each branch
		for(int i=0; i<branchesCount; ++i)
		{	
			MTGBranch branch = branches.get(i);

			//create new voxel if branch is root
			if(branch.getSupportNodeIndex() == MTGKeys.MTG_UNKNOWN_KEYCODE)
				newTrunk(i);

			//calculate branch length
			computeBranchLengths(i,coordType);

			//calculate diameters
			computeDiameters(i, scale);

			//calculate coordinates
			computeBranchCoordinates(i, scale, coordType);
		}
	}

	/**
	 * Branch - Coordinate - Computes the coordinates for each branch element.
	 * @param 
	 * @throws MTGPlantFrameException 
	 */
	private void computeBranchCoordinates(int branchIndex, int scale, int coordType) throws MTGError.MTGPlantFrameException
	{	
		//Get branch
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("Branch information not available for plant frame computation.");
		if(branches.size()-1<branchIndex)
			throw new MTGError.MTGPlantFrameException("Invalid branch index for branch retrieval.");
		MTGBranch branch = branches.get(branchIndex);

		//Look for origin of first branch element
		Vector3d origin = lookForOrigin(branch.getElement(0),scale,coordType);

		if(origin==null)
			return;

		//update plant min/max bounding
		updatePlantMinMax(origin);

		boolean new_element=true;

		Vector3d dirp,dirs;

		LinkedList<MTGBranchElement> fifo = new LinkedList<MTGBranchElement>();

		for(int i=0; i<branch.getElementCount(); ++i)
		{
			MTGBranchElement element = branch.getElement(i);

			Vector3d elementCoords = lookForCoords(element.getNodeIndex(),scale,coordType);

			if(elementCoords!=null)
			{
				new_element=false;

				//update plant min/max bounding
				updatePlantMinMax(elementCoords);

				Vector3d direction;
				Vector3d direction_s;
				double length;

				//find if phi,psi,teta attributes exist
				Vector3d eulerValues = getEulerAttributes(getNode(element.getNodeIndex()));

				Vector3d dirVector = new Vector3d();
				dirVector.sub(elementCoords, origin);
				length = (double)dirVector.length();

				if(eulerValues!=null)
				{
					direction = computeEulerPriDirection(eulerValues);
					direction_s = computeEulerSecDirection(eulerValues);
				}
				else
				{
					direction = new Vector3d(dirVector);
					if (length <= 0.0000001) { // this point is equal to the previous one.
						int father=topoFather(element.getNodeIndex(),MTGKeys.MTG_ANY);
						if(father!=MTGKeys.MTG_UNKNOWN_KEYCODE)
						{
							MTGBranchElement father_element=searchForElement(father);
							direction = father_element.getDirp();
							direction_s = father_element.getDirs();
							
							//yong 17 april - current element and previous element are on the same spatial location.
							//              - must inherit the direction vector of preceeding element with a non-null 
							//                direction vector
							while(direction==null)
							{
								father = topoFather(father,MTGKeys.MTG_ANY);
								if(father!=MTGKeys.MTG_UNKNOWN_KEYCODE)
								{
									father_element=searchForElement(father);
									direction = father_element.getDirp();
									direction_s = father_element.getDirs();
								}
								else
								{
									throw new MTGError.MTGPlantFrameException("Unable to find preceeding node direction vector.");
								}
							}
						}
						else
						{
							if(direction.length()!=0)
								direction.normalize();
							//direction_s = computeAPerpendicularDirection(direction);
							direction_s = new Vector3d(direction);
						}
					}
					else
					{
						direction.normalize();
						direction_s = computeAPerpendicularDirection(direction);
						direction_s.normalize();
					}
				}

				if(direction.length()!=0)
					direction.normalize();
				if(direction_s.length()!=0)
					direction_s.normalize();

				dirs= new Vector3d(direction_s);
				dirp= new Vector3d(direction);

				double alpha=0;
				double beta=0;

				if (length!=0)
				{
					alpha=computeElevationAlpha(direction);
					beta=computeAzimuthBeta(direction);
				}

				int flag = 0;

				while(!fifo.isEmpty())
				{
					flag = 1; 

					MTGBranchElement br_s=fifo.remove();

					int index=branch.getElementIndex(br_s);

					if(index==MTGKeys.MTG_UNKNOWN_KEYCODE)
						throw new MTGError.MTGPlantFrameException("Unknown branch element.");

					double br_length=length/br_s.getLength();

					Vector3d eulerAttributes = getEulerAttributes(getNode(br_s.getNodeIndex()));
					if (eulerAttributes!=null) 
					{
						dirp = computeEulerPriDirection(eulerValues);
						direction_s = computeEulerSecDirection(eulerValues);
					}
					else 
					{
						dirp=direction;
					}

					dirp.normalize();
					dirs.normalize();
					direction.normalize();
					direction_s.normalize();

					br_s.setDirp(dirp);
					br_s.setDirs(direction_s);
					br_s.setOrigin(origin);
					br_s.setLength(br_length);
					br_s.setAlpha(alpha);
					br_s.setBeta(beta);
					setElementLengthDirectionOrder(br_s, br_length, dirp);

					Vector3d toAddOrigin = new Vector3d(dirp);
					toAddOrigin.scale(br_length);
					origin.add(toAddOrigin);
				}

				// We should apply the rule of division of the segment information that if
				// Two vertices whose coordinates are entered are separated
				// At least one non-vertex information (flag == 1)

				double br_length;
				if (flag == 1) 
					br_length = length/element.getLength();
				else br_length = length;

				element.setDirp(direction);
				element.setDirs(direction_s);
				element.setAlpha(alpha);
				element.setBeta(beta);
				element.setLength(br_length);
				element.setOrigin(origin);
				setElementLengthDirectionOrder(element, br_length, direction);

				Vector3d toAddOrigin = new Vector3d(direction);
				toAddOrigin.scale(br_length);
				origin.add(toAddOrigin);

				updatePlantMinMax(elementCoords);
			}
			else //element without coordinates
			{
				fifo.add(element);
			}
		}// End of loop over the elements of the branch

		// We must now deal with all elements that are found at the end of
		// Branch and who do not have coordinates (we have not found new vertex
		// Coordinates with the end of the branch. It is therefore clear the
		// Existing stack.

		if(!fifo.isEmpty())
		{
			if(new_element)
			{
				// This is a branch which does not have any evidence of coordinates.
				// In this case we call to the method of computeBranchCoordinates
				// Coordinates (which takes account of euler angles if necessary)
				//yongzhi 6 feb - TODO: implement 


				//fifo.clear();
				while(!fifo.isEmpty())
				{
					MTGBranchElement branch_element = fifo.remove();
					int topoFather = topoFather(branch_element.getNodeIndex(),MTGKeys.MTG_ANY);
					if(topoFather!=MTGKeys.MTG_UNKNOWN_KEYCODE)
					{
						MTGBranchElement father_element=searchForElement(topoFather);
						if(father_element!=null)
						{
							branch_element.setOrigin(origin);
							branch_element.setDirp(father_element.getDirp());
							branch_element.setDirs(father_element.getDirs());
							setElementLengthDirectionOrder(branch_element, -1, father_element.getDirp());
						}
					}
				}
			}
			else
			{
				while(!fifo.isEmpty())
				{
					MTGBranchElement branch_element = fifo.remove();
					int index = branch.getElementIndex(branch_element);

					if(index==MTGKeys.MTG_UNKNOWN_KEYCODE)
						throw new MTGError.MTGPlantFrameException("Unknown branch element");

					double length;

					if(getNode(branch_element.getNodeIndex()).hasLength())
						length = getNode(branch_element.getNodeIndex()).length;
					else
					{
						MTGDressingFile dressing = getDressingFile();
						if(dressing==null)
							length = MTGDressingDefaultValues.DEFAULT_MIN_LENGTH;
						else
							length = dressing.getMinLength(
									getNode(branch_element.getNodeIndex()).mtgClass
									//getNode(branch_element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)
									);
					}
					/*
					Object lengthObj = getNode(branch_element.getNodeIndex()).getObject(MTGKeys.ATT_LENGTH);
					if(lengthObj!=null)
						length = ((Float)lengthObj).floatValue();
					else
					{
						MTGDressingFile dressing = getDressingFile();
						if(dressing==null)
							length = MTGDressingDefaultValues.DEFAULT_MIN_LENGTH;
						else
							length = dressing.getMinLength(
									getNode(branch_element.getNodeIndex()).mtgClass
									//getNode(branch_element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)
									);
					}
					 */
					branch_element.setLength(length);

					Vector3d eulerValues = getEulerAttributes(getNode(branch_element.getNodeIndex()));

					if(eulerValues!=null)
					{
						dirp = computeEulerPriDirection(eulerValues);
						dirs = computeEulerSecDirection(eulerValues);
					}
					else // Otherwise, we continue in the same direction as the father topological
					{
						int topoFather = topoFather(branch_element.getNodeIndex(),MTGKeys.MTG_ANY);
						if(topoFather!=MTGKeys.MTG_UNKNOWN_KEYCODE)
						{
							MTGBranchElement father_element=searchForElement(topoFather);
							dirp = father_element.getDirp();
							dirs = father_element.getDirs(); //yongzhi 6 feb 2012 - this line was not present in amap source. is this a bug?
						}
						else
						{
							dirp = new Vector3d(0,0,0);
							dirs = new Vector3d(0,0,0);
						}
					}

					dirp.normalize();
					dirs.normalize();

					double alpha=0;
					double beta=0;

					if (length!=0)
					{
						alpha=computeElevationAlpha(dirp);
						beta=computeAzimuthBeta(dirp);
					}

					branch_element.setDirp(dirp);
					branch_element.setDirs(dirs);
					branch_element.setAlpha(alpha);
					branch_element.setBeta(beta);
					branch_element.setLength(length);
					branch_element.setOrigin(origin);
					setElementLengthDirectionOrder(branch_element, length, dirp);

					Vector3d originToAdd = new Vector3d(dirp);
					originToAdd.scale(length);
					origin.add(originToAdd);

					updatePlantMinMax(origin);

				}//end of while-loop clearing the fifo list
			}
		}

		if(!fifo.isEmpty())
			throw new MTGError.MTGPlantFrameException("Unable to compute all branch coordinate values.");
	}

	/**
	 * 
	 * @param element
	 * @param length
	 * @param dirp
	 */
	private void setElementLengthDirectionOrder(MTGBranchElement element, double length, Vector3d dirp)
	{
		int nodeIndex = element.getNodeIndex();
		MTGNode node = getNode(nodeIndex);
		if(node!=null)
		{
			if(length!=-1)
				node.Length=length;
				//node.setObject(MTGKeys.ATT_LENGTH, new Float(length));
			if(dirp!=null)
				node.setDirectoryPrimary(dirp);
				//node.setObject(MTGKeys.ATT_DIRECTION_PRI, new Vector3d(dirp));
				
			node.Order = element.getOrder();
			//node.setObject(MTGKeys.ATT_ORDER, new Integer(element.getOrder()));
		}
	}

	/**
	 * Computes perpendicular vector to the given direction
	 * @param priDirection
	 * @return perpendicular direction
	 */
	private Vector3d computeAPerpendicularDirection(Vector3d priDirection)
	{
		// the spherical coordinates of dir1 define two Euler angles:

		double azimuth = computeAzimuthBeta(priDirection);

		double elevation = -computeElevationAlpha(priDirection); // in Euler angles positive angles are oriented downwards

		// The last Euler angle is underdetermined : we fix it
		// at a value such that the secundary direction is correctly
		// oriented with respect to its definition in the Geom library:
		// should be oriented towards the positive x axis when the
		// principal direction is oriented towards the positive z axis.
		// This comes to defining the roling angle as 3*Pi/2:

		double roling = 3.0*(Math.PI)/2.0;

		double sa=Math.sin(azimuth);
		double sb=Math.sin(elevation);
		double sc=Math.sin(roling);
		double ca=Math.cos(azimuth);
		double cb=Math.cos(elevation);
		double cc=Math.cos(roling);

		double a = (-sa*cc)+(ca*sb*sc);
		double b = (ca*cc)+(sa*sb*sc);
		double c = cb*sc;

		return new Vector3d(a,b,c);
	}

	private double computeAzimuthBeta(Vector3d dir)
	{
		double length = dir.length();
		double xx = (length>0)? dir.x:0;
		double result;

		double alpha = computeElevationAlpha(dir);
		double cosA=Math.cos(alpha);

		if (cosA!=0)
		{
			double cosB=xx/cosA;

			if (cosB>1)
			{

				cosB=1;
			}

			if (cosB<-1)
			{

				cosB=-1;
			}

			result=Math.acos(cosB);

			if (dir.y<0)
			{
				result=2*Math.PI-result;;
			}

			if (dir.y==0)
			{
				if (dir.x>=0)
				{
					result=0;
				}
				else
				{
					result=Math.PI;
				}
			}
		}
		else
		{
			result=0;
		}


		if ((Math.abs(result)<Math.pow((double)10,(double)-4)))
		{
			result=0;
		}

		return (double)result;
	}

	private double computeElevationAlpha(Vector3d dir)
	{
		double l=dir.length();
		double sinA=(l > 0 ?dir.z/l: 0);
		double result;

		if (sinA!=0)
		{
			if (sinA>1)
			{
				sinA=1;
			}
			if (sinA<-1)
			{
				sinA=-1;
			}

			result=Math.asin(sinA);
		}
		else
		{
			result=0;
		}

		if ((Math.abs(result)<Math.pow((double)10,(double)-4)))
		{
			result=0;
		}

		return (double)result;
	}

	/**
	 * Computes euler primary direction from phi,psi and teta values.
	 * @param eulerValues
	 * @return euler primary direction vector
	 */
	private Vector3d computeEulerPriDirection(Vector3d eulerValues)
	{
		if(eulerValues==null)
			return null;

		return new Vector3d((double)(Math.cos(eulerValues.z)*Math.cos(eulerValues.x)),
				(double)(Math.sin(eulerValues.z)*Math.cos(eulerValues.x)),
				(double)(-Math.sin(eulerValues.z)));
	}

	/**
	 * Computes euler secondary direction from phi,psi and teta values.
	 * @param eulerValues
	 * @return euler secondary direction vector
	 */
	private Vector3d computeEulerSecDirection(Vector3d eulerValues)
	{
		if(eulerValues==null)
			return null;

		return new Vector3d((double)(-Math.sin(eulerValues.z)*Math.cos(eulerValues.y)+Math.cos(eulerValues.z)*Math.sin(eulerValues.x)*Math.sin(eulerValues.y)),
				(double)(Math.cos(eulerValues.z)*Math.cos(eulerValues.y)+Math.sin(eulerValues.z)*Math.sin(eulerValues.x)*Math.sin(eulerValues.y)),
				(double)(Math.sin(eulerValues.y)*Math.cos(eulerValues.x)));
	}

	/**
	 * Get Euler measurement attributes of node.
	 * @param node
	 * @return Vector3d containing phi, psi, teta values if they exist, else null
	 */
	private Vector3d getEulerAttributes(MTGNode node)
	{
		/*
		Object phi,psi,teta;
		phi=psi=teta=null;

		phi = node.getObject(MTGKeys.ATT_PHI);
		psi = node.getObject(MTGKeys.ATT_PSI);
		teta = node.getObject(MTGKeys.ATT_TETA);

		if((phi!=null)&&(psi!=null)&&(teta!=null))
		{
			return new Vector3d((((Integer)phi).doubleValue()),
					(((Integer)psi).doubleValue()),
					(((Integer)teta).doubleValue())
					);
		}
		*/
		if(node.hasAA() && node.hasBB() && node.hasCC())
			return new Vector3d(node.AA,node.BB,node.CC);
		
		return null;
	}

	/**
	 * Update plant's min and max vector using the specified vector.
	 * @param newVector
	 */
	private void updatePlantMinMax(Vector3d newVector)
	{
		Vector3d minPlant = (Vector3d)(getObject(MTGKeys.MTG_NODE_PLANT_MIN));
		Vector3d maxPlant = (Vector3d)(getObject(MTGKeys.MTG_NODE_PLANT_MAX));
		Vector3d min = minVector3f(newVector,minPlant);
		Vector3d max = maxVector3f(newVector,maxPlant);
		setObject(MTGKeys.MTG_NODE_PLANT_MIN,min);
		setObject(MTGKeys.MTG_NODE_PLANT_MAX,max);
	}

	/**
	 * Finds coordinate values for the specified node.
	 * @param node
	 * @return Vector3d containing coordinates if they exist in node, else null.
	 * @throws MTGPlantFrameException 
	 */
	private Vector3d lookForCoords(int nodeIndex, int scale, int coordType) throws MTGError.MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		if(node==null)
			return null;

		if(getScale(nodeIndex) >= scale)
		{
			Vector3d coord = getCoordAttributes(node,coordType);
			if(coord!=null)
				return coord; //yongzhi 5 feb 2012 - TODO: to implement triangle ref system here later. this is for cartesian ref sys.
		}

		return null;
	}

	/**
	 * Branch - Coordinate - Look for origin point to begin calculating coordinates,
	 * @param element
	 * @param scale
	 * @param coordType
	 * @return Vector3d specifying origin coordinates
	 * @throws MTGPlantFrameException
	 */
	private Vector3d lookForOrigin(MTGBranchElement element, int scale, int coordType) throws MTGError.MTGPlantFrameException
	{
		Vector3d result = new Vector3d(0,0,0);

		int nodeIndex = element.getNodeIndex();

		int nodeLocationSupport = location(nodeIndex);

		if(nodeLocationSupport == MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			Object originObj = getObject(MTGKeys.MTG_NODE_PLANT_ORIGIN);
			if(originObj!=null)
				return (Vector3d)originObj;
			else
			{
				/*
				nodeLocationSupport=compoFather(nodeLocationSupport);
				while( (getScale(nodeLocationSupport)>scale) && !hasCoordAttributes(getNode(nodeLocationSupport),coordType))
					nodeLocationSupport=compoFather(nodeLocationSupport);*/
				//throw new MTGError.MTGPlantFrameException("Unable to find origin.");
			}
		}
		else
		{
			MTGBranchElement supportElement = searchForElement(nodeLocationSupport);
			if(supportElement.getNodeIndex()!=MTGKeys.MTG_UNKNOWN_KEYCODE)
			{
				Vector3d supportDir = new Vector3d(supportElement.getDirp());
				if(supportDir!=null)
				{
					double supportLength = supportElement.getLength();
					//if(supportLength!=0)
					supportDir.scale(supportLength);
					result.add(supportDir,supportElement.getOrigin());
					return result;
				}
				else
					return null; //TODO: fix this part for scale 2
			}
			else
			{
				/*
				while( (getScale(nodeLocationSupport)>scale) && !hasCoordAttributes(getNode(nodeLocationSupport),coordType))
					nodeLocationSupport=compoFather(nodeLocationSupport);*/
				//throw new MTGError.MTGPlantFrameException("Unable to find origin.");
			}
		}

		return result;
	}

	/**
	 * Branch - Coordinate - Looks for topological(prioritized) or composition father of node.
	 * @param nodeIndex
	 * @return index of father node.
	 * @throws MTGPlantFrameException
	 */
	private int location(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		int nodeScale = getScale(nodeIndex);
		int locNode = getPrefix(nodeIndex);

		if(locNode==MTGKeys.MTG_UNKNOWN_KEYCODE)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		if(locNode==MTGKeys.MTG_ROOT_NODE)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;

		int locNodeScale = getScale(locNode);

		if(nodeScale <= locNodeScale)
			return locNode;
		else
			return location(locNode);
	}

	/**
	 * 
	 * Branch - Diameter - Look in branch for next branch element that has bottom or top diameter defined.
	 * @param branch
	 * @param startElement
	 * @param isTopDia
	 * @return branch element index
	 */
	private int nextDefinedDia(MTGBranch branch, int startElementIndex, boolean isTopDia)
	{
		int elementCount = branch.getElementCount();
		if((elementCount-1<startElementIndex)||(elementCount<=0))
			return -1;
		for(int i=startElementIndex; i<elementCount; ++i)
		{
			MTGBranchElement element = branch.getElement(i);
			if(isTopDia)
			{
				if(element.getTopDia()!=-1.0f)
					return i;
			}
			else
			{
				if(element.getBotDia()!=-1.0f)
					return i;
			}
		}
		return -1;
	}
	
	private MTGBranch getBranch(int branchIndex) throws MTGError.MTGPlantFrameException
	{
		//Get branch
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("Branch information not available for plant frame computation.");
		if(branches.size()-1<branchIndex)
			throw new MTGError.MTGPlantFrameException("Invalid branch index for branch retrieval.");
		return branches.get(branchIndex);
	}
	
	/**
	 * Branch - Category - Look up refinement hierarchy for branch's category
	 * @param nodeIndex
	 * @return
	 * @throws MTGError.MTGPlantFrameException
	 */
	private int lookUpCategory(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		if(node == null)
			throw new MTGError.MTGPlantFrameException("Invalid node index for computing branch category.");
		
		if(node.hasCategory())
		{
			return node.Category;
		}
		else
		{
			int compoFatherIndex = compoFather(nodeIndex);

			if(compoFatherIndex!=MTGKeys.MTG_ROOT_NODE)
			{
				return lookUpCategory(compoFatherIndex);
			}
			
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}
	
	/**
	 * Branch - Category - Look down refinement hierarchy for branch's category
	 * @param nodeIndex
	 * @return
	 * @throws MTGError.MTGPlantFrameException
	 */
	private int lookDownCategory(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		if(node == null)
			throw new MTGError.MTGPlantFrameException("Invalid node index for computing branch category.");
		
		if(node.hasCategory())
		{
			return node.Category;
		}
		else
		{
			int[] sons = compoSonsIds(nodeIndex);
			if(sons!=null)
			{
				if(sons.length>=1)
					return lookDownCategory(sons[0]);
			}
			
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}

	/**
	 * Branch - Category - Look for category of branch
	 * @param nodeIndex
	 * @return
	 * @throws MTGError.MTGPlantFrameException
	 */
	private int lookForCategory(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		int result = MTGKeys.MTG_UNKNOWN_KEYCODE;
		
		//yong 22 apr 2012 - This part is a little different from AMAP code.
		//                 - In AMAP code, the vscale of this node is checked to determine whether to look up
		//				     or look down hierarchy for category
		//				   - In this implementation, look up for category. If undefined then look down

		//look up hierarchy for category
		result = lookUpCategory(nodeIndex);		
		
		//look down hierarchy for category
		if(result==MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			result = lookDownCategory(nodeIndex);
		}
		
		if(result==MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			MTGDressingFile dressingFile = getDressingFile();
			if(dressingFile==null)
				throw new MTGError.MTGPlantFrameException("Dressing information not available for plant frame computation.");
			result = dressingFile.getDefaultCategory();
		}
		return result;
	}
	
	/**
	 * Branch - Category - Ramification order of the branch
	 * @param branchIndex
	 * @return
	 * @throws MTGError.MTGPlantFrameException
	 */
	private int ramOrder(int branchIndex) throws MTGError.MTGPlantFrameException
	{
		int result=0;
		MTGBranch branch = getBranch(branchIndex);
		
		int supportNode=branch.getSupportNodeIndex();

		if (supportNode==MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			// Tronc de la plante. //TODO: translate to english
			result=MTGKeys.MTG_TRUNK_ORDER;
		}
		else
		{
			//MTGBranch supportBranch = null;
			ArrayList<MTGBranch> branches = this.getBranches();
			//boolean foundSupportBranch=false;
			int supportBranchIndex = MTGKeys.MTG_UNKNOWN_KEYCODE;
			if(branches==null)
				throw new MTGError.MTGPlantFrameException("Branches data not available for branch category computation.");

			for(int i=0; i<branches.size(); ++i)
			{
				MTGBranch currBranch= branches.get(i);
				int currElementCount = currBranch.getElementCount();
				if(currElementCount>0)
				{
					for(int j=0; j<currElementCount; ++j)
					{
						if(currBranch.getElement(j).getNodeIndex() == supportNode)
						{
							//supportBranch = currBranch;
							supportBranchIndex = i;
							//foundSupportBranch=true;
							break;
						}
					}
				}

				if(supportBranchIndex!=MTGKeys.MTG_UNKNOWN_KEYCODE)
					break;
			}

			if(supportBranchIndex==MTGKeys.MTG_UNKNOWN_KEYCODE)
				throw new MTGError.MTGPlantFrameException("Unable to find branch for supporting branch element while computing branch category.");

			result = 1+ramOrder(supportBranchIndex);
		}
		return result;
	}
	
	/**
	 * Branch - Category - Computes category for each branch element
	 * @param branchIndex
	 * @throws MTGError.MTGPlantFrameException
	 */
	private void computeCategory(int branchIndex) throws MTGError.MTGPlantFrameException
	{
		//Get branch
		MTGBranch branch = getBranch(branchIndex);
		
		//get support node for branch
		int supportNode = branch.getSupportNodeIndex();
		if(supportNode == MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			MTGDressingFile dressingFile = getDressingFile();
			if(dressingFile==null)
				throw new MTGError.MTGPlantFrameException("Dressing information not available for plant frame computation.");
			branch.setCategory(dressingFile.getAxeDefaultCategory());
		}
		else
		{
			int cat = lookForCategory(branch.baseOfBranch());
			
			if(cat == MTGKeys.MTG_UNKNOWN_KEYCODE)
			{
				cat=ramOrder(branchIndex);
			}
			branch.setCategory(cat);
		}
	}

	/**
	 * Branch - Diameter - Computes the diameters for each branch element.
	 * @param 
	 * @throws MTGPlantFrameException 
	 */
	private void computeDiameters(int branchIndex, int scale) throws MTGError.MTGPlantFrameException
	{		
		//Get branch
//		ArrayList<MTGBranch> branches = getBranches();
//		if(branches==null)
//			throw new MTGError.MTGPlantFrameException("Branch information not available for plant frame computation.");
//		if(branches.size()-1<branchIndex)
//			throw new MTGError.MTGPlantFrameException("Invalid branch index for branch retrieval.");
//		MTGBranch branch = branches.get(branchIndex);
		MTGBranch branch = getBranch(branchIndex);

		//First, assign top diameter and bottom diameter values from respective nodes to their 
		//corresponding branch elements.
		assignGivenDiameters(branch);

		//Loop through each branch element to assign top and bottom diameter values
		double topDia=-1.0f;				//top diameter for this branch element
		double bottomDia=-1.0f;			//bottom diameter for this branch element
		double fatherNodeTopDia=-1.0f;	//top diameter of topological father node of this branch element
		double fatherNodeBotDia=-1.0f;   //bottom diameter of topological father node of this branch element

		boolean interpolateDia = true;  //flag to prevent interpolating diameter values twice

		int nextElementInterpolate=-1;

		for(int i=0; i<branch.getElementCount(); ++i)
		{
			MTGBranchElement element = branch.getElement(i);

			//For the first branch element, need to retrieve node topo father
			if(i==0)
			{
				int elementNodeIndex=element.getNodeIndex();
				int elementNodeFatherIndex = topoFather(elementNodeIndex, MTGKeys.MTG_ANY);

				//if first branch element has no topological father (no node with succ or branch edge pointing to it)
				if(elementNodeFatherIndex == MTGKeys.MTG_UNKNOWN_KEYCODE)
				{
					//get dressing file info
					MTGDressingFile dressingFile = getDressingFile();
					if(dressingFile==null)
						throw new MTGError.MTGPlantFrameException("Dressing information not available for plant frame computation.");

					int nextElementTopDefined = nextDefinedDia(branch,i,true);
					int nextElementBotDefined = nextDefinedDia(branch,i,false);

					//preferably use next bottom
					if( (nextElementBotDefined!=-1) &&
							(nextElementBotDefined <= nextElementTopDefined)
							)
					{
						fatherNodeTopDia=branch.getElement(nextElementBotDefined).getBotDia();
						fatherNodeBotDia=branch.getElement(nextElementBotDefined).getBotDia();
					}
					else
					{
						//otherwise use next top
						if(nextElementTopDefined!=-1)
						{
							fatherNodeTopDia=branch.getElement(nextElementTopDefined).getTopDia();
							fatherNodeBotDia=branch.getElement(nextElementTopDefined).getTopDia();
						}
						//else use min values from dressing file, divided by scale
						else
						{
							fatherNodeTopDia=dressingFile.getMinTopDia(getNode(element.getNodeIndex()).mtgClass)
									//(String)(getNode(element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)))
									/(double)scale;
							//fatherNodeBotDia=dressingFile.getMinBotDia((String)(getNode(element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)))
							fatherNodeBotDia=dressingFile.getMinBotDia((String)(getNode(element.getNodeIndex()).mtgClass))
									/(double)scale;
						}
					}
				}
				else
				{
					MTGBranchElement fatherBranchElement = searchForElement(elementNodeFatherIndex);
					fatherNodeTopDia = fatherBranchElement.getTopDia();
					fatherNodeBotDia = fatherBranchElement.getBotDia();
				}
			}
			else //not the first branch element
			{
				fatherNodeTopDia=branch.getElement(i-1).getTopDia();
				fatherNodeBotDia=branch.getElement(i-1).getBotDia();
			}

			//set bottom diameter for this branch element
			if(element.getBotDia()==-1)
				bottomDia = fatherNodeTopDia;
			else
				bottomDia = element.getBotDia();

			//set top diameter for this branch element
			if(element.getTopDia()==-1)
			{
				if(interpolateDia)
				{
					//Interpolate diameter
					int startElementIndex=i+1;
					if(i==branch.getElementCount()-1)
						startElementIndex=i;

					int nextElementTopDefined = nextDefinedDia(branch,startElementIndex,true);
					int nextElementBotDefined = nextDefinedDia(branch,startElementIndex,false);

					//if next element with bottom diameter found, interpolate with it
					if( (nextElementBotDefined!=-1) &&
							(nextElementBotDefined <= nextElementTopDefined)
							)
					{
						interpolateDia=false;
						nextElementInterpolate=nextElementBotDefined;
						int numOfElementsToInterpolate = nextElementBotDefined-i;

						double diaRatio = computeDiaRatio(numOfElementsToInterpolate,bottomDia,branch.getElement(nextElementBotDefined).getBotDia());
						topDia=bottomDia/diaRatio;
					}
					//else interpolate with next element with top diameter if one such element is found
					else if(nextElementTopDefined!=-1)
					{
						interpolateDia=false;
						nextElementInterpolate=nextElementTopDefined;
						int numOfElementsToInterpolate = nextElementTopDefined-i;

						if(branch.getElement(nextElementTopDefined).getTopDia()>0)
						{
							double diaRatio = computeDiaRatio(numOfElementsToInterpolate,bottomDia,branch.getElement(nextElementTopDefined).getTopDia());
							topDia=bottomDia/diaRatio;
						}
					}
				}
				if(topDia==-1)
				{
					// Same proportion
					topDia=bottomDia*(fatherNodeTopDia/fatherNodeBotDia);
				}
			}
			else
			{
				topDia = element.getTopDia();
			}

			element.setBotDia(bottomDia);
			element.setTopDia(topDia);

			if (i==nextElementInterpolate)
			{
				interpolateDia=true;
			}
		}//end of looping each branch element
	}

	/**
	 * Branch - Diameter - Calculates the ratio of the diameter for a branch element as an interpolated value between 2 
	 * other branch elements that are topologically preceding and following it.
	 * @param numOfElements
	 * @param botDia
	 * @param topDia
	 * @return ratio
	 */
	private double computeDiaRatio(int numOfElements, double botDia, double topDia)
	{
		double result=Math.pow(
				(double)(botDia/topDia),
				1.0/(double)numOfElements
				);
		return (double)result;
	}

	/**
	 * Branch - Diameter - Assign top diameter and bottom diameter values from respective nodes to their 
	 * corresponding branch elements.
	 */
	private void assignGivenDiameters(MTGBranch branch)
	{
		if(branch==null)
			return;

		for(int i=0;i<branch.getElementCount();i++)
		{
			MTGBranchElement element = branch.getElement(i);

			double top_dia=lookForTopDia(branch.getElement(i).getNodeIndex());
			double bot_dia=lookForBottomDia(branch.getElement(i).getNodeIndex());

			element.setTopDia(top_dia);
			element.setBotDia(bot_dia);
		}
	}

	/**
	 * Branch - Diameter - Searches in scale hierarchy for top diameter attribute values for the specified node.
	 * @param nodeIndex
	 * @return top diameter value
	 */
	private double lookForTopDia(int nodeIndex)
	{
		double result=-1;

		result = lookUpDia(nodeIndex,true);
		if(result!=-1)
			return result;
		result = lookDownDia(nodeIndex,true);

		return result;
	}

	/**
	 * Branch - Diameter - Searches in scale hierarchy for bottom diameter attribute values for the specified node.
	 * @param nodeIndex
	 * @return bottom diameter value
	 */
	private double lookForBottomDia(int nodeIndex)
	{
		double result=-1;

		result = lookUpDia(nodeIndex,false);
		if(result!=-1)
			return result;
		result = lookDownDia(nodeIndex,false);

		return result;
	}

	/**
	 * Branch - Diameter - Searches upwards in scale hierarchy for diameter attribute value for the specified node.
	 * @param nodeIndex
	 * @param isTopDia
	 * @return diameter attribute value
	 */
	private double lookUpDia(int nodeIndex, boolean isTopDia)
	{
		MTGNode node = getNode(nodeIndex);
		/*
		Object diaObj;
		if(isTopDia)
			diaObj = node.getObject(MTGKeys.ATT_TOPDIA);
		else
			diaObj = node.getObject(MTGKeys.ATT_BOTTOMDIA);
		
		if(diaObj!=null)
			return ((Integer)diaObj).floatValue();
			*/
		if(isTopDia && node.hasTopDia())
			return node.TopDia;
		else if(!isTopDia && node.hasBotDia())
			return node.BotDia;
		else
		{
			int compoFatherIndex = compoFather(nodeIndex);

			if(compoFatherIndex!=MTGKeys.MTG_ROOT_NODE)
			{
				int[] fathersSons = this.compoSonsIds(compoFatherIndex);
				if(fathersSons.length>=1)
				{
					//inherit diameter only if this node is the last compositional son of its compositional father.
					//e.g.
					// B1/E1<E2<E3, i.e. E1, E2 and E2 are compositional sons of B1. 
					// E1 and E3 have diameters specified. E2 should not inherit B1's diameters and should get the 
					// interpolated diameter values from E1 and E3.
					if(fathersSons[fathersSons.length-1]==nodeIndex)
						return lookUpDia(compoFatherIndex,isTopDia);
				}
			}
		}
		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}

	/**
	 * Branch - Diameter - Searches downwards in scale hierarchy for diameter attribute value for the specified node.
	 * @param nodeIndex
	 * @param isTopDia
	 * @return diameter attribute value
	 */
	private double lookDownDia(int nodeIndex, boolean isTopDia)
	{
		MTGNode node = getNode(nodeIndex);
		/*
		Object diaObj;
		if(isTopDia)
			diaObj = node.getObject(MTGKeys.ATT_TOPDIA);
		else
			diaObj = node.getObject(MTGKeys.ATT_BOTTOMDIA);

		if(diaObj!=null)
			return ((Integer)diaObj).floatValue();
			*/
		if(isTopDia && node.hasTopDia())
			return node.TopDia;
		else if(!isTopDia && node.hasBotDia())
			return node.BotDia;
		else
		{
			int[] compoSonsIndices = compoSonsIds(nodeIndex);
			if(compoSonsIndices!=null)
				if(compoSonsIndices.length>=1)
					return lookDownDia(compoSonsIndices[0],isTopDia);
		}
		return -1;
	}
	
	/**
	 * Branch - Length - Look up hierarchy of refinement for node with specified attribute length value.
	 *                 - If length value found in hierarchy above, value is interpolated for this current node
	 * @param nodeIndex
	 * @return
	 */
	private double lookUpLength(int nodeIndex)
	{
		MTGNode node = getNode(nodeIndex);
		double result = MTGKeys.MTG_UNKNOWN_KEYCODE;
		if(node.hasLength())
			result = node.getLength();
		else
		{
			int compoFatherIndex = compoFather(nodeIndex);
			if(compoFatherIndex!=MTGKeys.MTG_ROOT_NODE)
			{
				result = lookUpLength(compoFatherIndex);
				
				//if compositional father has length attribute value
				if(result!=MTGKeys.MTG_UNKNOWN_KEYCODE)
				{
					int[] fatherSons = this.compoSonsIds(compoFatherIndex);
					//length of this node is the interpolated value of compositional father's length value
					//i.e. divide length of father by number of father's sons
					result = result/fatherSons.length;
				}
			}
		}
		
		//return result
		return result;
	}
	
	/**
	 * Branch - Length - Look down hierarchy of refinement for node with specified length attribute value.
	 *                 - if compositional children has length value, length of this node is the summation
	 * @param nodeIndex
	 * @return
	 * @throws MTGPlantFrameException 
	 */
	private double lookDownLength(int nodeIndex) throws MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		
		if(node.hasLength())
			return node.getLength();
		else
		{
			int[] sons = this.compoSonsIds(nodeIndex);
			//if this node has refinement children
			if(sons!=null)
			{
				if(sons.length>0)
				{	
					double length = 0;
					//length of this node is the aggregate of its childrens' length
					for(int i=0; i<sons.length; ++i)
					{
						length += lookDownLength(sons[i]);
					}
					//if length exists from children
					if(length>0)
						return length;
				}
			}
			//else if this node has no refinement children
			MTGDressingFile dressingFile = getDressingFile();
			if(dressingFile==null)
				throw new MTGError.MTGPlantFrameException("Dressing information not found.");
			//return default min length from dressing file
			return dressingFile.getMinLength(node.mtgClass);
			
		}
	}
	
	/**
	 * Branch - Length - Computes the length for a branch element. Called by method computeBranchLengthsStandard
	 * @param element
	 * @throws MTGError.MTGPlantFrameException
	 */
	private void computeElementLengthStandard(MTGBranchElement element) throws MTGError.MTGPlantFrameException
	{
		int nodeIndex = element.getNodeIndex();
		double result = lookUpLength(nodeIndex);
		if(result == MTGKeys.MTG_UNKNOWN_KEYCODE)
		{
			result = lookDownLength(nodeIndex);
		}
		
		//if result is invalid length value
		if(result<0)
			throw new MTGPlantFrameException("Unable to compute branch element length.");
		
		//set length value into element
		element.setLength(result);
	}

	/**
	 *  Branch - Length - Computes the length for each branch element. For standard geometry, i.e. MTG without cartesian 
	 *  coordinates or triangular reference frame coordinates.
	 * @param branchIndex
	 * @throws MTGPlantFrameException
	 */
	private void computeBranchLengthsStandard(int branchIndex) throws MTGError.MTGPlantFrameException
	{
//		ArrayList<MTGBranch> branches = getBranches();
//		if(branches==null)
//			throw new MTGError.MTGPlantFrameException("Branch information does not exist for plant frame computation.");
//
//		if((branches.size()-1)<branchIndex)
//			throw new MTGError.MTGPlantFrameException("Branch index does not exist for plant frame computation.");
//		
//		MTGBranch branch = branches.get(branchIndex);
		MTGBranch branch = getBranch(branchIndex);
		int elementCount = branch.getElementCount();
		for(int i=0; i<elementCount; ++i)
		{
			MTGBranchElement element = branch.getElement(i);
			computeElementLengthStandard(element);
		}
	}

	/**
	 * Branch - Length - Computes the length for each branch element.
	 * @param branchIndex
	 * @throws MTGPlantFrameException 
	 */
	private void computeBranchLengths(int branchIndex, int coordType) throws MTGError.MTGPlantFrameException
	{
		boolean coordinatesExist = coordinatesExistInBranch(branchIndex,coordType);
		
		if(!coordinatesExist)
		{
			computeBranchLengthsStandard(branchIndex);
		}
		else
		{
			int initialIndex=0;

//			ArrayList<MTGBranch> branches = getBranches();
//			if(branches==null)
//				throw new MTGError.MTGPlantFrameException("Branch information does not exist for plant frame computation.");
//
//			if(branches.size()-1<branchIndex)
//				throw new MTGError.MTGPlantFrameException("Invalid branch index.");
//
//			MTGBranch branch = branches.get(branchIndex);
			MTGBranch branch = getBranch(branchIndex);

			//TODO:
			//if(length_algo.equals("UseIndexes"))
			{
				// Option to interpret the index of the first vertex of the branch
				// Request by the user in case the index of first vertex
				// Is above that of its bearer: act as if the length of
				// This vertex was proportional to the difference with the index
				// In the carrier segment between the carrier and the first vertex
				// Tell this axis

				int support_index =  -1;
				int first_index = -1;

				int firstElementNodeIndex = branch.getElement(0).getNodeIndex();
				int firstElementSupportNodeIndex = topoFather(firstElementNodeIndex, MTGKeys.MTG_ANY);

				if(firstElementNodeIndex != MTGKeys.MTG_UNKNOWN_KEYCODE)
					first_index = firstElementNodeIndex;
				if(firstElementSupportNodeIndex != MTGKeys.MTG_UNKNOWN_KEYCODE)
					support_index = firstElementSupportNodeIndex;

				// In case the index is growing from the wearer
				// At the door, we consider that the index corresponds to the door
				// Length relative to the index of the carrier
				// Otherwise, the index of the door is an absolute length from the carrier
				// (Initial_index == 0)
				//if (support_index != -1 && (support_index < first_index))
				//{
				//	initialIndex = support_index;
				//}
				//else
				//{
				initialIndex = first_index-1;
				//}

				// Else the index is reset at the beginning of each axis
				//  initial_index=0;

				// Calculate table index these combined
				// Index is a cumulative account for the fact that normally
				// Indexes should be growing. decrease when
				// Do as if they were growing
				// Example:
				// Indexes reels: 1 3 12 4 6 9 15 33 1 6
				// Indexes these combined: 1 3 12 16 18 21 27 45 46 52
			}
			int nb_elements = branch.getElementCount();

			//yongzhi 3 feb - this portion of node index sorting cannot be understood.
			//TODO: find out about it and add it later.
			/*
			int index;
			int previous_index = initial_index;
			int previous_cum_ref_index = 0;

			int vertex;

			for(int i1=0;i1<nb_elements;i1++)
			{         

				vertex = branch.getElement(i1).getNodeIndex();
				index = vertex;//_plant->vindex(vertex); 

				if (index < previous_index && i1 > 0) { 
					// Decay index
					// If i == 0 is that previous_index initial_index = ==
					// Support_index and we have a decay index of passing on
					// Carrier to carrier. And nothing should be done because
					// Previous_cum_ref_index = 0 and this is Ok

					previous_cum_ref_index = cum_index_array[i1-1];

				}

				cum_index_array[i1] = previous_cum_ref_index + index;

				previous_index = index;
			}
			 */

			//Collect branch element ids that have coordinate feature values
			ArrayList<Integer> elementIdWithCoords = new ArrayList<Integer>();

			int nextBranchElementWithCoord = nextElementIndexWithCoordinates(branch,0, coordType);

			//yongzhi 6 feb 2012 - there exist branches without coordinate values - see agraf.mtg line 66 - '+F1'
			if(nextBranchElementWithCoord==MTGKeys.MTG_UNKNOWN_KEYCODE)
				return;
			//	throw new MTGError.MTGPlantFrameException("No Branch element with coordinates found in branch.");

			elementIdWithCoords.add(new Integer(nextBranchElementWithCoord));

			while((nextBranchElementWithCoord+1)<=(nb_elements-1))
			{
				nextBranchElementWithCoord = nextElementIndexWithCoordinates(branch,
						nextBranchElementWithCoord+1,
						coordType);
				if(nextBranchElementWithCoord!=MTGKeys.MTG_UNKNOWN_KEYCODE)
					elementIdWithCoords.add(new Integer(nextBranchElementWithCoord));
				else
					break;
			}

			//calculate lengths (as ratio of node indices)
			for(int j=0; j<nb_elements;++j)
			{
				int nextLargerNodeIdWithCoords=0;
				int nextLargerElementIdWithCoords=0;
				for(int k=0; k<elementIdWithCoords.size(); ++k)
				{
					if(
							((branch.getElement(elementIdWithCoords.get(k).intValue()).getNodeIndex() > branch.getElement(j).getNodeIndex()) && (j==0)) ||
							((branch.getElement(elementIdWithCoords.get(k).intValue()).getNodeIndex() >= branch.getElement(j).getNodeIndex()) && (j!=0))
							/*((j==nb_elements-1)&&(branch.getElement(elementIdWithCoords.get(k).intValue()).getNodeIndex() >= branch.getElement(j).getNodeIndex()))*/
							)
					{
						nextLargerNodeIdWithCoords=branch.getElement(elementIdWithCoords.get(k).intValue()).getNodeIndex();
						nextLargerElementIdWithCoords=k;


						//yongzhi 6 feb 2012 - big delta index is modifed and different from original amap source.
						//						original does not take into account j==0
						int big_delta_index=0;
						if(nextLargerElementIdWithCoords!=0)
						{
							big_delta_index = nextLargerNodeIdWithCoords - 
									branch.getElement(elementIdWithCoords.get(nextLargerElementIdWithCoords-1)).getNodeIndex();
						}
						else
						{
							big_delta_index = nextLargerNodeIdWithCoords - initialIndex;
						}

						int small_delta_index=0;
						if(j!=0)
						{
							small_delta_index = branch.getElement(j).getNodeIndex() - (branch.getElement(j-1).getNodeIndex()); 
						}
						else
						{
							small_delta_index = branch.getElement(j).getNodeIndex() - initialIndex;
						}

						double length=0;
						if ((small_delta_index==0) || (big_delta_index==0))
							length= Double.MAX_VALUE;
						else
							length = (double)big_delta_index/(double)small_delta_index;


						if(length<1.0f)
						{
							MTGDressingFile dressingFile = getDressingFile();
							if(dressingFile==null)
								throw new MTGError.MTGPlantFrameException("Dressing information not found.");
							length=dressingFile.getMinLength(
									//(String)(getNode(branch.getElement(j).getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS))
									(String)(getNode(branch.getElement(j).getNodeIndex()).mtgClass)
									);
						}


						branch.getElement(j).setLength(length);
						break;
					}
				}
			}
		}
	}

	/**
	 * Branch - Length - Find the next branch element, beginning from an initial element index, that contains 
	 * coordinate attribute values.
	 * @param branch
	 * @param initialElementIndex
	 * @param coordType
	 * @return branch element index
	 * @throws MTGPlantFrameException
	 */
	private int nextElementIndexWithCoordinates(MTGBranch branch, int initialElementIndex, int coordType) throws MTGError.MTGPlantFrameException
	{
		int elementCount = branch.getElementCount();
		for(int i=initialElementIndex; i<elementCount; ++i)
		{
			MTGBranchElement element = branch.getElement(i);
			int nodeIndex = element.getNodeIndex();
			MTGNode node = getNode(nodeIndex);

			if(hasCoordAttributes(node,coordType))
				return i;
		}

		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}

	/**
	 * Branch - Length - Determine if x,y,z coordinates for the specified type of coordinate reference system exists in 1 of the 
	 * branch element related nodes.
	 * @param branchIndex
	 * @param coordType
	 * @return true if coordinates exists, else false.
	 * @throws MTGPlantFrameException
	 */
	private boolean coordinatesExistInBranch(int branchIndex, int coordType) throws MTGError.MTGPlantFrameException
	{
//		ArrayList<MTGBranch> branches = getBranches();
//		if(branches==null)
//			throw new MTGError.MTGPlantFrameException("Branch information does not exist for plant frame computation.");
//
//		if(branches.size()-1 < branchIndex)
//			throw new MTGError.MTGPlantFrameException("Invalid branch index.");
//
//		MTGBranch branch = branches.get(branchIndex);
		MTGBranch branch = getBranch(branchIndex);
		int elementCount = branch.getElementCount();
		for(int i=0; i<elementCount; ++i)
		{
			MTGBranchElement element = branch.getElement(i);
			int nodeIndex = element.getNodeIndex();
			MTGNode node = getNode(nodeIndex);

			if(node==null)
				throw new MTGError.MTGPlantFrameException("Invalid node index.");

			if(hasCoordAttributes(node,coordType))
				return true;
		}

		return false;
	}

	/**
	 * Dressing - Get dressing file information from the root node data.
	 * @return MTGDressingFile
	 * @throws MTGPlantFrameException
	 */
	private MTGDressingFile getDressingFile()
	{
		Object dressingObj = getObject(MTGKeys.MTG_NODE_DRESSING);
		if(dressingObj!=null)
		{
			return (MTGDressingFile)dressingObj;
		}
		else
			return null;
	}

	/**
	 * Coordinates - Adds a new voxel to squares
	 * @param branchIndex
	 * @throws MTGPlantFrameException
	 */
	private void newTrunk(int branchIndex) throws MTGError.MTGPlantFrameException
	{
		MTGSquares squares = getSquares();
		if(squares==null)
			throw new MTGError.MTGPlantFrameException("Squares information not available for plant frame constructions.");

		squares.addVoxel(new MTGVoxel(branchIndex));
	}

	/**
	 * Coordinates - Sets origin point of coordinate system and container for bounding boxes.
	 * @throws MTGPlantFrameException
	 */
	private void setupCoordinateVariables() throws MTGError.MTGPlantFrameException
	{
		//coordinate - origin
		Vector3d origin = new Vector3d(0,0,0);
		setObject(MTGKeys.MTG_NODE_COORD_ORIGIN,origin);
		//coordinate - squares (voxels)
		MTGDressingFile dressingFile = getDressingFile();
		double distance=0;
		if(this.dist==0)
			distance = dressingFile.getDefaultDistance();
		else
			distance = this.dist;
		MTGSquares squares = new MTGSquares(distance/dressingFile.getLengthUnit());
		setObject(MTGKeys.MTG_NODE_COORD_SQUARES,squares);
	}

	/**
	 * Coordinates - Get squares information from the root node data.
	 * @return MTGSquares
	 * @throws MTGPlantFrameException
	 */
	private MTGSquares getSquares()
	{
		Object squaresObj = getObject(MTGKeys.MTG_NODE_COORD_SQUARES);
		if(squaresObj!=null)
		{
			return (MTGSquares)squaresObj;
		}
		else
			return null;
	}

	/**
	 * Coordinates - Get the MTG geometric reference system type.
	 * @return MTG Type
	 * @throws MTGPlantFrameException
	 */
	public int mtgType() throws MTGError.MTGPlantFrameException
	{
		// Look for the type of MTG which will be treat.
		// Type=1 : Standard mtg (by default).
		// Type=2 : Mtg with coordinates (triangular reference system).
		// Type=3 : Mtg with cartesian coordinates.

		//default result - standard mtg
		int result=MTGKeys.MTG_TYPE_STANDARD;
		/*
		ArrayList<MTGNodeDataFeature> mtgFeatures = getFeaturesInfo();
		for(int i=0; i<mtgFeatures.size(); ++i)
		{
			MTGNodeDataFeature feature = mtgFeatures.get(i);
			if(feature.getFeatureName().equals(MTGKeys.TR_DAB))
			{
				if(verifyTriangleReference())
					result=MTGKeys.MTG_TYPE_COORD_TRI_REF;
			}
			if(feature.getFeatureName().equals(MTGKeys.TR_X))
			{
				if(verifyTriangleReference())
					result=MTGKeys.MTG_TYPE_COORD_TRI_REF;
			}
			if(feature.getFeatureName().equals(MTGKeys.CA_X))
			{
				if(verifyCartesianReference())
					result=MTGKeys.MTG_TYPE_COORD_CARTESIAN;
			}
		}
		 */
		
		if(this.hasDAB())
			result=MTGKeys.MTG_TYPE_COORD_TRI_REF;
		if(this.hasL1())
			result=MTGKeys.MTG_TYPE_COORD_TRI_REF;
		if(this.hasXX())
			result=MTGKeys.MTG_TYPE_COORD_CARTESIAN;
		
		return result;
	}

	/**
	 * Coordinates - Verify if feature or attributes for triangle reference coordinates exist in MTG.
	 * @return true if MTG contains all attributes required for triangle reference coordinate specifications, else false.
	 * @throws MTGPlantFrameException
	 */
	private boolean verifyTriangleReference() throws MTGError.MTGPlantFrameException
	{
		//boolean DAB,DBC,DAC,L1,L2,L3;
		//DAB=DAC=DBC=L1=L2=L3=false;
		/*
		ArrayList<MTGNodeDataFeature> mtgFeatures = getFeaturesInfo();
		for(int i=0; i<mtgFeatures.size(); ++i)
		{
			MTGNodeDataFeature feature = mtgFeatures.get(i);
			if(feature.getFeatureName().equals(MTGKeys.TR_DAB))
			{
				DAB=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.TR_DBC))
			{
				DBC=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.TR_DAC))
			{
				DAC=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.TR_X))
			{
				L1=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.TR_Y))
			{
				L2=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.TR_Z))
			{
				L3=true;
				continue;
			}
		}
*/
		return (this.hasDAB() &&
				this.hasDAC() && 
				this.hasDBC() &&
				this.hasL1() &&
				this.hasL2() &&
				this.hasL3()
				);
		//return (DAB && DBC && DAC && L1 && L2 && L3);
	}

	/**
	 * Coordinates - Verify if Cartesian x,y,z coordinates are included as feature attributes in the MTG.
	 * @return true if MTG contains all attributes required for cartesian coordinate specifications, else false.
	 * @throws MTGPlantFrameException
	 */
	private boolean verifyCartesianReference() throws MTGError.MTGPlantFrameException
	{
		//boolean x,y,z;
		//x=y=z=false;

		/*ArrayList<MTGNodeDataFeature> mtgFeatures = getFeaturesInfo();
		for(int i=0; i<mtgFeatures.size(); ++i)
		{
			MTGNodeDataFeature feature = mtgFeatures.get(i);
			if(feature.getFeatureName().equals(MTGKeys.CA_X))
			{
				x=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.CA_Y))
			{
				y=true;
				continue;
			}
			if(feature.getFeatureName().equals(MTGKeys.CA_Z))
			{
				z=true;
				continue;
			}
		}*/
		return (this.hasXX() && this.hasYY() && this.hasZZ());
	}

	/**
	 * Azimuth - Computes and/or propagates given azimuth values (phyllotaxy) to the branches. 
	 * @throws MTGPlantFrameException
	 */
	private void computeAzimuth(int scale) throws MTGError.MTGPlantFrameException
	{
		//branch info
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("No branch information available for geometry computation.");

		//dressing info
		MTGDressingFile dressingFile = getDressingFile();
		if(dressingFile==null)
			throw new MTGError.MTGPlantFrameException("No dressing information available for geometry computation.");

		double beta=0; //starting azimuth value
		double base=dressingFile.getPhillotaxy()/dressingFile.getAzimutUnit();

		int branchCount = branches.size();
		for(int i=0; i<branchCount; ++i)
		{
			MTGBranch branch = branches.get(i);
			int plantNumber = branch.getPlant();

			int branchElementCount = branch.getElementCount();			
			for(int j=0; j<branchElementCount; ++j)
			{
				MTGBranchElement branchElement = branch.getElement(j);

				MTGBranch thisBranch = new MTGBranch(branchElement.getNodeIndex(),plantNumber,scale);

				int occurrences = numberOfBranchOccurrences(thisBranch);
				int index = indexOfFirstBranchOccurrence(thisBranch);

				if(index != MTGKeys.MTG_UNKNOWN_KEYCODE)
				{
					for(int k=index; k<index+occurrences; ++k)
					{
						int firstElement = branches.get(k).baseOfBranch();

						Double azimuth = lookForAzimuth(firstElement);
						if(azimuth!=null)
						{
							branches.get(k).getElement(0).setBeta(azimuth.doubleValue());
						}
						else
						{
							branches.get(k).getElement(0).setBeta(beta);
							beta+=base;
						}
					}
				}
			}
		}
	}

	/**
	 * Azimuth - Find azimuth feature value in node or its refinement fathers.
	 * @param nodeIndex
	 * @return azimuth value is found, else null
	 * @throws MTGPlantFrameException
	 */
	private Double lookForAzimuth(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		return lookForAzimuthUp(nodeIndex);
	}

	/**
	 * Azimuth - Recursive method to find if azimuth value exists as feature/attribute in the given node or its 
	 * successive topological fathers.
	 * 
	 * @param nodeIndex
	 * @return true if azimuth value exists as attribute in node or its successive topological fathers, else false.
	 */
	private Double lookForAzimuthUp(int nodeIndex) throws MTGError.MTGPlantFrameException
	{		
		MTGNode node = getNode(nodeIndex);
		if(node==null)
			throw new MTGError.MTGPlantFrameException("Node for specified index not found.");

		/*
		Object azimuthObj = node.getObject(MTGKeys.ATT_AZIMUT);
		if(azimuthObj!=null)
		{
			try
			{
				Float azimuthValue = (Float)azimuthObj;
				return azimuthValue;
			}
			catch(Exception ex)
			{
				throw new MTGError.MTGPlantFrameException("Incorrect value saved as Azimuth attribute in node.");
			}
		}
		*/
		if(node.hasAzimut())
		{
			return new Double(node.Azimut);
		}
		else
		{
			int compoFather = compoFather(nodeIndex);
			if(compoFather!=MTGKeys.MTG_ROOT_NODE)
				return lookForAzimuthUp(compoFather);
		}
		return null;
	}

	/**
	 * Branches - Gets the index of first branch in ordered array list of branches with the same plant number and 
	 * support node index.
	 * @param branch
	 * @return index of branch
	 * @throws MTGPlantFrameException
	 */
	private int indexOfFirstBranchOccurrence(MTGBranch branch) throws MTGError.MTGPlantFrameException
	{
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("No branch information available for geometry computation.");

		for(int i=0; i<branches.size();++i)
		{
			if(MTGBranch.equal(branches.get(i), branch))
				return i;
		}
		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}

	/**
	 * Branches - Returns number of branches in the same plant that have the same support node as the given branch.
	 * @param branch
	 * @return number of branches
	 */
	private int numberOfBranchOccurrences(MTGBranch branch) throws MTGError.MTGPlantFrameException
	{
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("No branch information available for geometry computation.");

		int lower_bound = 0;
		int upper_bound = 0;

		//find lower bound - 	index of first branch of the same plant that has support node with
		//						same or greater index than the specified branch.
		boolean foundLowerBound=false;
		for(int i=0; i<branches.size();++i)
		{
			if(!(MTGBranch.lessThan(branches.get(i), branch)))
			{
				lower_bound=i;
				foundLowerBound=true;
				break;
			}
		}
		if(!foundLowerBound)
			lower_bound=branches.size();

		//find upper bound
		boolean foundUpperBound=false;
		for(int i=0; i<branches.size();++i)
		{
			if(MTGBranch.greaterThan(branches.get(i), branch))
			{
				upper_bound=i;
				foundUpperBound=true;
				break;
			}
		}
		if(!foundUpperBound)
			upper_bound=branches.size();

		return upper_bound-lower_bound;
	}

	/**
	 * Branches - Get number of branches.
	 * @return Number of branches
	 */
	public int getBranchesCount()
	{
		try
		{
			ArrayList<MTGBranch> branches = getBranches();
			if(branches!=null)
			{
				return branches.size();
			}
			else
				return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get Branch count.");
			return 0;
		}
	}

	/**
	 * Branches - Returns branch element that corresponds to the node of the given index.
	 * @param nodeIndex
	 * @return MTGBranchElement
	 */
	public MTGBranchElement searchForElement(int nodeIndex)
	{
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			return null;

		for(int i=0; i<branches.size(); ++i)
		{
			MTGBranch branch = branches.get(i);
			for(int j=0; j<branch.getElementCount(); ++j)
			{
				MTGBranchElement element = branch.getElement(j);
				if(element.getNodeIndex() == nodeIndex)
				{
					return element;
				}
			}
		}

		return null;
	}

	/**
	 * Branches - Get the branches or plant frame.
	 * @return List of branches.
	 */
	public ArrayList<MTGBranch> getBranches()
	{
		try
		{
			Object branchesObj = getObject(MTGKeys.MTG_NODE_BRANCHES);
			if(branchesObj!=null)
			{
				return (ArrayList<MTGBranch>)branchesObj;
			}
			else
				return null;
		}
		catch(Throwable t)
		{
			//System.out.println("Unable to get Branches.");
			return null;
		}
	}

	/**
	 * Branches - Searches graph and builds a set of branches beginning from the node specified by the input index.
	 * @param originIndex
	 * @throws MTGPlantFrameException
	 */
	private void lookForBranches(int originIndex,int scale, int order) throws MTGError.MTGPlantFrameException
	{	
		if(getNode(originIndex)==null)
			throw new MTGError.MTGPlantFrameException("Branch origin does not exist in node list.");

		ArrayList<Integer> nodeQueue = new ArrayList<Integer>();
		int nodeIndex;

		int topoFatherIndex = topoFather(originIndex,MTGKeys.MTG_ANY);

		//if origin is a root node (the first node of a plant with no topological father)
		//increment plant counter
		if(topoFatherIndex == MTGKeys.MTG_UNKNOWN_KEYCODE)
			incrementPlantCount();

		MTGBranch branch = new MTGBranch(topoFatherIndex,getPlantCount(),scale);
		MTGBranchElement branchElement = new MTGBranchElement(originIndex);
		branchElement.setOrder(order);
		branch.addElement(branchElement);		

		boolean endOfLoop=false;
		//yongzhi 31 jan 2012 - note that this loop assumes that if a branch exists, a successor must exist.
		//                      if node only has topoSons connected with branching edges, infinite loop occurs.
		//                    - This is a direct translation of code in AMAP software.
		//                    - This is resolved by checking if nextNode==originIndex at end of loop. Loop is ended
		//                      if it is the same.
		while(!endOfLoop)
		{			
			int[] topoSonsList = topoSons(originIndex, MTGKeys.MTG_ANY);

			if(topoSonsList.length == 0)
				endOfLoop=true;

			int nextNode = originIndex;

			for(int i=0; i<topoSonsList.length;++i)
			{
				nodeIndex=topoSonsList[i];

				if(edgeType(originIndex,nodeIndex)==Graph.SUCCESSOR_EDGE)
				{
					MTGBranchElement newElement = new MTGBranchElement(nodeIndex);
					newElement.setOrder(order);
					branch.addElement(newElement);
					nextNode=nodeIndex;
				}
				else
				{
					nodeQueue.add(new Integer(nodeIndex));
				}
			}

			if(originIndex == nextNode)//this line and the next are additional and does not exist in original AMAP source.
				endOfLoop=true;
			else
				originIndex = nextNode;
		}

		Object branchesObj = getObject(MTGKeys.MTG_NODE_BRANCHES);
		if(branchesObj==null)
			throw new MTGError.MTGPlantFrameException("No container for branches in root node.");
		else
		{
			ArrayList<MTGBranch> branches = (ArrayList<MTGBranch>)branchesObj;

			//add branch in order of support node index and plant number
			boolean addedInOrder=false;
			for(int i=0; i<branches.size();++i)
			{
				if(!(MTGBranch.lessThan(branches.get(i), branch)))
				{
					branches.add(i, branch);
					addedInOrder=true;
					break;
				}
			}
			if(!addedInOrder)
				branches.add(branch);

		}

		int nextOrder = order+1;
		while(!nodeQueue.isEmpty())
		{
			nodeIndex = nodeQueue.remove(0).intValue();
			lookForBranches(nodeIndex,scale,nextOrder);
		}
	}

	/**
	 * Plant - Increment the counter for the number of plants specified in this MTG.
	 * @throws MTGPlantFrameException
	 */
	public void incrementPlantCount() throws MTGError.MTGPlantFrameException
	{
		try
		{
			Object plantCountObj = getObject(MTGKeys.MTG_NODE_PLANT_COUNT);
			if(plantCountObj==null)
				throw new MTGError.MTGPlantFrameException("Unable to retrieve plant count from MTG root node.");
			Integer plantCount = (Integer)plantCountObj;
			setObject(MTGKeys.MTG_NODE_PLANT_COUNT,new Integer(plantCount.intValue()+1));
		}
		catch(Throwable t)
		{
			System.out.println("Unable to increment plant count.");
		}
	}

	/**
	 * Plant - Gets the number of plants recognized in this MTG so far.
	 * @return Number of plants recognized in this MTG so far.
	 * @throws MTGPlantFrameException
	 */
	public int getPlantCount() throws MTGError.MTGPlantFrameException
	{
		try
		{
			Object plantCountObj = getObject(MTGKeys.MTG_NODE_PLANT_COUNT);
			if(plantCountObj==null)
				throw new MTGError.MTGPlantFrameException("Unable to retrieve plant count from MTG root node.");
			return ((Integer)plantCountObj).intValue();
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get plant count.");
			return 0;
		}
	}
	
	/**
	 * Number of successor or branch edges from a start node to an end node.
	 * @param startNodeId
	 * @param endNodeId
	 * @return
	 * @throws MTGPlantFrameException 
	 */
	public int height(MTGNode startNode, MTGNode endNode) throws MTGPlantFrameException
	{
		if(startNode==null || endNode==null)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
			
		int result = heightInternal(startNode, endNode, 0);
		
		return result;
	}
	
	private int heightInternal(MTGNode currNode, MTGNode endNode, int currHeight)
	{
		if(currNode.equals(endNode))
		{
			return currHeight;
		}
		else
		{
			MTGNode[] currNodeSons = currNode.topoSonNodes(MTGKeys.MTG_ANY);
			if(currNodeSons!=null)
			{
				if(currNodeSons.length >0)
				{
					int result = MTGKeys.MTG_UNKNOWN_KEYCODE;
					for(int i=0; i<currNodeSons.length; ++i)
					{
						int res = heightInternal(currNodeSons[i],endNode, currHeight+1);
						if(res!=-1)
							result = res;
					}
					return result;
				}
			}
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}
	
	public MTGNode[] getRoots(int scale) throws MTGPlantFrameException
	{
		refreshNodeList();
		
		int[] rootIds = getRootIds(scale);
		MTGNode[] roots = new MTGNode[rootIds.length];
		for(int i=0; i<rootIds.length; ++i)
		{
			roots[i] = getNode(rootIds[i]);
		}
		
		deleteNodeList();
		
		return roots;
	}

	/**
	 * Obtain a list of indices of root nodes refined the specified node. 
	 * @param scale
	 * @return List if indices of root nodes.
	 * @throws MTGPlantFrameException
	 */
	public int[] getRootIds(/*int nodeId,*/ int scale) throws MTGError.MTGPlantFrameException
	{
		try
		{
			//refresh node list cache
			//refreshNodeList();
			
			ArrayList<Integer> rootList = new ArrayList<Integer>();
	
			Object nodeListObj = getObject(MTGKeys.MTG_NODE_NODELIST);
			ArrayList<MTGNode> nodeList;
	
			if((nodeListObj!=null) && (nodeListObj instanceof ArrayList<?>) )
			{
				nodeList = (ArrayList<MTGNode>)(nodeListObj);
	
				//find nodes without topo father , i.e. the root nodes
				for(int i=0; i<nodeList.size(); ++i)
				{
					int adjNodeIndex = topoFather(i, MTGKeys.MTG_ANY);
	
					if(adjNodeIndex==MTGKeys.MTG_UNKNOWN_KEYCODE)
					{
						if(scale!=MTGKeys.MTG_ANY)
						{
							//check scale of node
							if(getScale(i)==scale)
							{
								//flag to check if this root node is directly connected to MTGRoot for rendering loop access
								//boolean isRenderEdge=false;
								
								//loop through all nodes directly connected to MTGRoot.
								//if this root node is directly connected to MTGRoot, it was connected for the purpose of rendering
								//and therefore should not be a root
//								for(Edge e = this.getFirstEdge(); e!=null; e = e.getNext(this))
//								{
//									if((e.getSource() == this) && (e.testEdgeBits(Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE)))
//									{
//										if(e.getTarget() == nodeList.get(i))
//											isRenderEdge = true;
//									}
//								}
								
								//add to result list if not a rendering connected node
								//if(!isRenderEdge)
									rootList.add(new Integer(i));
							}
						}
						else
							rootList.add(new Integer(i));
					}
				}
			}
	
			//convert arraylist to int[]
			
			int[] resultList = new int[rootList.size()];
			for(int j=0; j<resultList.length; ++j)
			{
				resultList[j] = rootList.get(j).intValue();
			}
			
			/*
			int[] resultList = null;
			if(rootList.size()>=1)
			{
				resultList = new int[1];
				resultList[0] = rootList.get(0).intValue();
			}
			else
				resultList = new int[0];
			*/
			
			//delete node list cache
			//deleteNodeList();
			
			return resultList;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get root nodes.");
			return null;
		}
	}

	/**
	 * Classes - Returns information of MTG classes defined.
	 * @return List of class information
	 * @throws MTGPlantFrameException
	 */
	public ArrayList<MTGNodeDataClasses> getClassesInfo() throws MTGError.MTGPlantFrameException
	{
		try
		{
			Object mtgClassesObj = getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
			if(mtgClassesObj==null)
				throw new MTGError.MTGPlantFrameException("Expected class information in MTG root node not found.");
			return (ArrayList<MTGNodeDataClasses>)mtgClassesObj;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get class information.");
			return null;
		}
	}

	/**
	 * Classes - Get number of features/attributes defined in MTG.
	 * @return number of classes in MTG.
	 * @throws MTGPlantFrameException
	 */
	/*
	public int getClassesCount() throws MTGError.MTGPlantFrameException
	{
		try
		{
			ArrayList<MTGNodeDataClasses> mtgClasses = getClassesInfo();
			return mtgClasses.size();
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get class count.");
			return 0;
		}
	}
	 */
	/**
	 * Features - Returns information of MTG features defined.
	 * @return List of feature information.
	 * @throws MTGPlantFrameException
	 */
	public ArrayList<MTGNodeDataFeature> getFeaturesInfo() throws MTGError.MTGPlantFrameException
	{
		try
		{
			Object mtgFeaturesObj = getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
			if(mtgFeaturesObj==null)
				throw new MTGError.MTGPlantFrameException("Expected feature information in MTG root node not found.");
			return (ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get feature information.");
			return null;
		}
	}

	/**
	 * Features - Get number of features/attributes defined in MTG.
	 * @return number of features in MTG.
	 * @throws MTGPlantFrameException
	 */
//	public int getFeaturesCount() throws MTGError.MTGPlantFrameException
//	{
//		try
//		{
//			ArrayList<MTGNodeDataFeature> mtgFeatures = getFeaturesInfo();
//			return mtgFeatures.size();
//		}
//		catch(Throwable t)
//		{
//			System.out.println("Unable to get feature count.");
//			return 0;
//		}
//	}

	/**
	 * Nodes - Returns MTGNode instance given its index.
	 * @param nodeIndex
	 * @return MTGNode at specified index
	 */
	public MTGNode getNode(int nodeIndex)
	{
		try
		{
			Object nodeListObj = getObject(MTGKeys.MTG_NODE_NODELIST);
			ArrayList<MTGNode> nodeList;
	
			if((nodeListObj!=null) && (nodeListObj instanceof ArrayList<?>) )
			{
				nodeList = (ArrayList<MTGNode>)(nodeListObj);
				if(nodeList.size()-1<nodeIndex)
					return null;
				MTGNode node = nodeList.get(nodeIndex);
				return node;
			}
			return null;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get node.");
			return null;
		}
	}

	/**
	 * Nodes - Returns index of MTGNode given its instance.
	 * @param node
	 * @return index of node.
	 */
	public int getIndex(MTGNode node)
	{
		try
		{
			//Object indexObj = node.getObject(MTGKeys.MTG_NODE_LIST_INDEX);
			return node.mtgID;
			//if(indexObj!=null)
			//{
			//	Integer index = (Integer)indexObj;
			//	return index.intValue();
			//}
	
			//return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get node index.");
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}

	/**
	 * Nodes - Get the edge bits (int) between a source node and a target node.
	 * @param sourceIndex
	 * @param targetIndex
	 * @return edge type
	 */
	public int edgeType(int sourceIndex, int targetIndex)
	{
		try
		{
			MTGNode sourceNode = getNode(sourceIndex);
			MTGNode targetNode = getNode(targetIndex);
	
			if((sourceNode==null) || (targetNode==null))
				return MTGKeys.MTG_UNKNOWN_KEYCODE;
	
			int edgeBits = sourceNode.getEdgeBitsTo(targetNode);
			if(edgeBits==0)
				return MTGKeys.MTG_UNKNOWN_KEYCODE;
			else
				return edgeBits;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get edge between nodes.");
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}

	/**
	 * Nodes - Returns index of node that is topological father of node specified by input nodeIndex.
	 * @param nodeIndex
	 * @param edgeType
	 * @return index of topological father node
	 */
	public int topoFather(int nodeIndex, int edgeType)
	{
		try
		{
			MTGNode node = getNode(nodeIndex);
			if(node!=null)
			{
				MTGNode adjNode = null;
				if(edgeType==MTGKeys.MTG_ANY)
				{
					adjNode = (MTGNode)node.findAdjacent(true, false, Graph.BRANCH_EDGE);
					if(adjNode==null)
						adjNode = (MTGNode)node.findAdjacent(true, false, Graph.SUCCESSOR_EDGE);
				}
				else
					adjNode = (MTGNode)node.findAdjacent(true, false, edgeType);
	
				if(adjNode!=null)
					return getIndex(adjNode);
			}
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get topological father of node.");
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}

	/**
	 * Nodes - For usage by root MTG node. To find the topological sons of the node found in list of nodes at specified nodeIndex.
	 * @param nodeIndex
	 * @param edgeType
	 * @return indices of topological son nodes.
	 */
	public int[] topoSons(int nodeIndex, int edgeType)
	{
		try
		{
			MTGNode node = getNode(nodeIndex);
			if(node!=null)
				return node.topoSons(edgeType);
			else
				return null;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get topological sons of node.");
			return null;
		}
	}

	/**
	 * Nodes - Returns refinement father of the specified node.
	 * @param nodeIndex
	 * @return index of refinement father node.
	 */
	public int compoFather(int nodeIndex)
	{
		try
		{
			MTGNode node = getNode(nodeIndex);
			if(node!=null)
			{
				MTGNode adjNode = (MTGNode)node.findAdjacent(true, false, Graph.REFINEMENT_EDGE);
	
				if(adjNode==this)
					return MTGKeys.MTG_ROOT_NODE;
				else if(adjNode!=null)
					return getIndex(adjNode);
			}
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get compositional father of node.");
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}
	
	/**
	 * Nodes - Returns refinement father of the specified node.
	 * @param nodeIndex
	 * @return index of refinement father node.
	 */
	public MTGNode compoFather(MTGNode node)
	{
		try
		{
			if(node!=null)
			{
				MTGNode adjNode = (MTGNode)node.findAdjacent(true, false, Graph.REFINEMENT_EDGE);
	
				return adjNode;
			}
			return null;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get compositional father of node.");
			return null;
		}
	}

	/**
	 * Returns compositional sons of a node, 
	 * given that the compositional sons are of a lower scale than the given scale.
	 * @param nodeIndex
	 * @param scale
	 * @return
	 * @throws MTGError.MTGPlantFrameException
	 */
	public int[] compoSonsIds(int nodeIndex, int scale) throws MTGError.MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		if(node==null)
			throw new MTGError.MTGPlantFrameException("Invalid node index used for retrieving compositional sons.");
		
		int nodeScale = getScale(nodeIndex);
		
		if(scale <= nodeScale)
			return new int[0];
		
		ArrayList<Integer> resList = new ArrayList<Integer>();
		
		int[] sons = compoSonsIds(nodeIndex);
		
		if(sons!=null)
		{
			for(int i=0; i<sons.length; ++i)
			{
				if(scale == (nodeScale+1))
					resList.add(new Integer(sons[i]));
				else
				{
					int[] nextSons = compoSonsIds(sons[i],scale);
					if(nextSons==null)
						throw new MTGError.MTGPlantFrameException("Null exception compositional sons.");
					
					for(int j=0; j<nextSons.length; ++j)
					{
						resList.add(new Integer(nextSons[j]));
					}
				}
			}
		}
		
		int[] resultArray = new int[resList.size()];
		for(int k=0; k<resList.size();++k)
		{
			resultArray[k] = resList.get(k).intValue();
		}
		
		return resultArray;
	}
	
	/**
	 * Nodes - Returns refinement sons of the specified node.
	 * @param nodeIndex
	 * @return indices of refinement son nodes.
	 */
	public int[] compoSonsIds(int nodeIndex)
	{
		try
		{
			MTGNode node = getNode(nodeIndex);
			if(node!=null)
				return node.compoSonsIds();
			else
				return null;
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get compositional sons of node.");
			return null;
		}
	}

	/**
	 * Return topological father of node. If topological father does not exist, return compositional father.
	 * @param nodeIndex
	 * @return node index of topological or composition father
	 * @throws MTGPlantFrameException 
	 */
	private int getPrefix(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		if(node==null)
			throw new MTGError.MTGPlantFrameException("Node of specified index cannot be found.");
		else
		{
			int topoFatherIndex = topoFather(nodeIndex,MTGKeys.MTG_ANY);

			if(topoFatherIndex!=MTGKeys.MTG_UNKNOWN_KEYCODE)
			{
				//8 feb 2012 - yongzhi: this commented portion is only valid if inter-scale topo relations are allowed.
				/*
				int[] fatherCompoSons = compoSons(topoFatherIndex);
				if(fatherCompoSons==null)
					return topoFatherIndex;
				else
				{
					int[] compoSons = compoSons(nodeIndex);
					MTGNode compoNode=null;
					if(compoSons!=null)
						if(compoSons.length>=1)
							compoNode = getNode(compoSons[0]);
					if(compoNode==null)
						return topoFatherIndex;

					for(int i=0; i<fatherCompoSons.length; ++i)
					{
						MTGNode fatherCompoNode = getNode(fatherCompoSons[i]);

						Node adjNode = compoNode.findAdjacent(true, false, Graph.BRANCH_EDGE);
						if(adjNode!=null)
						{
							Integer adjIndex = (Integer)(((MTGNode)adjNode).getObject(MTGKeys.MTG_NODE_LIST_INDEX));
							if((adjIndex.intValue())==fatherCompoSons[i])
								return fatherCompoSons[i];
						}
					}
				}
				 */

				//check if topo father has origin and dirp values
				//if branching element of topo father has no origin and dirp values,
				//search its compositional fathers for one that has branching element with origin and dirp values
				/*
				MTGBranchElement fatherElement = searchForElement(topoFatherIndex);
				if(fatherElement!=null)
				{
					int nextTopoFatherIndex=topoFatherIndex;
					while((fatherElement.getDirp()==null) || (fatherElement.getOrigin()==null))
					{
						nextTopoFatherIndex = topoFather(nextTopoFatherIndex,MTGKeys.MTG_ANY);
						if(nextTopoFatherIndex!=MTGKeys.MTG_UNKNOWN_KEYCODE)
						{
							fatherElement = searchForElement(nextTopoFatherIndex);
							if(fatherElement==null)
								return topoFatherIndex;
						}
						else
						{
							return topoFatherIndex;
						}
					}
					return nextTopoFatherIndex;
				}
				 */
				return topoFatherIndex;
			}
			else
			{
				return compoFather(nodeIndex);
			}
		}
	}

	/**
	 * Nodes - Returns scale of node at index specified.
	 * @param nodeIndex
	 * @return Scale of node.
	 * @throws MTGPlantFrameException
	 */
	public int getScale(int nodeIndex) throws MTGError.MTGPlantFrameException
	{
		try
		{
			MTGNode node = getNode(nodeIndex);
			if(node==null)
				throw new MTGError.MTGPlantFrameException("Node of specified index cannot be found.");
			else
			{
				return node.mtgScale;
//				ArrayList<MTGNodeDataClasses> nodeDataClasses = getClassesInfo();
//				for(int i=0; i<nodeDataClasses.size(); ++i)
//				{
//					//Object entityClassObj = node.getObject(MTGKeys.MTG_NODE_ENTITY_CLASS);
//					//if(entityClassObj==null)
//					//	throw new MTGError.MTGPlantFrameException("Entity class of node cannot be found.");
//					//String entityClass = (String)entityClassObj;
//					String entityClass = node.mtgClass;
//					if( (nodeDataClasses.get(i).getSymbol()).equals(entityClass) )
//						return nodeDataClasses.get(i).getScale();
//				}
//				throw new MTGError.MTGPlantFrameException("Scale for specified node cannot be found.");
			}
		}
		catch(Throwable t)
		{
			System.out.println("Unable to get scale of node.");
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}

	/**
	 * Nodes - Checks if a node has coordinate attribute values
	 * @param node
	 * @param coordType
	 * @return true if coordinate attribute values exists, else false
	 */
	private boolean hasCoordAttributes(MTGNode node, int coordType)
	{
		if(node==null)
			return false;

		//Object x,y,z;
		//x=y=z=null;
		if(coordType== MTGKeys.MTG_TYPE_COORD_TRI_REF)
		{
			/*
			x=node.getObject(MTGKeys.TR_X);
			y=node.getObject(MTGKeys.TR_Y);
			z=node.getObject(MTGKeys.TR_Z);
			*/
			return (node.hasL1() && node.hasL2() && node.hasL3());
		}
		else if(coordType== MTGKeys.MTG_TYPE_COORD_CARTESIAN)
		{
			/*
			x=node.getObject(MTGKeys.CA_X);
			y=node.getObject(MTGKeys.CA_Y);
			z=node.getObject(MTGKeys.CA_Z);
			*/
			return (node.hasXX() && node.hasYY() && node.hasZZ());
		}
		/*
		if((x!=null)&&(y!=null)&&(z!=null))
			return true;
		 */
		return false;
	}

	/**
	 * Nodes - Get coordinate feature values of a node.
	 * @param node
	 * @param coordType
	 * @return coordinate attribute values as Vector3d if they exists, else null
	 */
	private Vector3d getCoordAttributes(MTGNode node, int coordType)
	{
		if(node==null)
			return null;

		Object x,y,z;
		x=y=z=null;
		if(coordType== MTGKeys.MTG_TYPE_COORD_TRI_REF)
		{/*
			x=node.getObject(MTGKeys.TR_X);
			y=node.getObject(MTGKeys.TR_Y);
			z=node.getObject(MTGKeys.TR_Z);*/
			if(node.hasL1() && node.hasL2() && node.hasL3())
				return new Vector3d(node.L1,node.L2,node.L3);
		}
		else if(coordType== MTGKeys.MTG_TYPE_COORD_CARTESIAN)
		{/*
			x=node.getObject(MTGKeys.CA_X);
			y=node.getObject(MTGKeys.CA_Y);
			z=node.getObject(MTGKeys.CA_Z);*/
			if (node.hasXX() && node.hasYY() && node.hasZZ())
				return new Vector3d(node.XX,node.YY,node.ZZ);
		}
		/*
		if((x!=null)&&(y!=null)&&(z!=null))
		{
			return new Vector3d((((Double)x).doubleValue()),
					(((Double)y).doubleValue()),
					(((Double)z).doubleValue())
					);
		}
		 */
		return null;
	}

	private Vector3d minVector3f(Vector3d a, Vector3d b)
	{
		double x = (a.x<=b.x)?a.x:b.x;
		double y = (a.y<=b.y)?a.y:b.y;
		double z = (a.z<=b.z)?a.z:b.z;

		return new Vector3d(x,y,z);
	}

	private Vector3d maxVector3f(Vector3d a, Vector3d b)
	{
		double x = (a.x>=b.x)?a.x:b.x;
		double y = (a.y>=b.y)?a.y:b.y;
		double z = (a.z>=b.z)?a.z:b.z;

		return new Vector3d(x,y,z);
	}

	private void computeMesh(boolean[] scalesVisible)
	{	
		ArrayList<MTGBranch> branches = getBranches();

		if(scalesVisible==null)
		{
			scalesVisible = new boolean[]{false,false,false,true,false};
		}
		
		if(branches!=null)
		{
			
			this.polygons = new PolygonMesh();
			FloatList vertexData;
			IntList indexData;
			vertexData = new FloatList();
			indexData = new IntList();
	
			for(int i=0; i<branches.size();++i)
			{
				MTGBranch branch = branches.get(i);
	
				if(scalesVisible!=null)
				{
					if(!scalesVisible[branch.getScale()])
						continue;
				}
	
				for(int j=0; j<branch.getElementCount(); ++j)
				{
					MTGBranchElement element = branch.getElement(j);
	
					MTGBranchElement elementNext;
					if(j<branch.getElementCount()-1)
						elementNext = branch.getElement(j+1);
					else
						elementNext = null;
					Point3d[] top = element.getTopSurface(elementNext);
					Point3d[] bot = element.getBotSurface(elementNext);
	
					//put vertices into array
					for(int k=0; k<top.length;++k)
					{
						vertexData.push((float)top[k].x, (float)top[k].y, (float)top[k].z);
						vertexData.push((float)bot[k].x, (float)bot[k].y, (float)bot[k].z);
					}
	
					int firstVertex = (vertexData.size()/3)-(top.length*2);
	
					boolean ccw=true;
					for(int k=firstVertex; k<(vertexData.size()/3)-2; ++k)
					{
						if(ccw)
						{
							indexData.push(k, k+1, k+2);
							ccw=false;
						}
						else
						{
							indexData.push(k+2, k+1, k);
							ccw=true;
						}
					}
	
					int secondLastV = (vertexData.size()/3)-2;
					int lastV = (vertexData.size()/3)-1;
					indexData.push(secondLastV, lastV, firstVertex);
					indexData.push(firstVertex+1,firstVertex,lastV);
				}
			}
	
			this.polygons.setIndexData(indexData);
			this.polygons.setVertexData(vertexData);
		}
	}
	/*
	@Override
	public void initXClass(XClass<? extends XObject> cls) {
		// TODO Auto-generated method stub

	}
	*/
	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}

	public int classSymbolToClassIndex(String classSymbol)
	{
		Object mtgClassesObj = nodeData.getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		if(mtgClassesObj==null)
			return -1;
		ArrayList<MTGNodeDataClasses> mtgClasses = (ArrayList<MTGNodeDataClasses>)getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		
		for(int i=0; i<mtgClasses.size(); ++i)
		{
			if((mtgClasses.get(i)).getSymbol().equals(classSymbol))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Returns scale of a given class
	 * @param classSymbol
	 * @return
	 */
	public int classScale(String classSymbol)
	{
		Object mtgClassesObj = nodeData.getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		if(mtgClassesObj==null)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		ArrayList<MTGNodeDataClasses> mtgClasses = (ArrayList<MTGNodeDataClasses>)getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		
		for(int i=0; i<mtgClasses.size(); ++i)
		{
			MTGNodeDataClasses mtgClass = mtgClasses.get(i);
			if(mtgClass.getSymbol().equals(classSymbol))
				return mtgClass.getScale();
		}
		
		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}
	
	public int[] classSymbolsToClassIndices(String[] classSymbols)
	{
		Object mtgClassesObj = nodeData.getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		if(mtgClassesObj==null)
			return null;
		ArrayList<MTGNodeDataClasses> mtgClasses = (ArrayList<MTGNodeDataClasses>)getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		
		int[] classIndices = new int[classSymbols.length];
		for(int j=0; j<classSymbols.length; ++j)
		{
			int classIndex = classSymbolToClassIndex(classSymbols[j]);
			if(classIndex==-1)
				return null;
			else
				classIndices[j]=classIndex;
		}
		return classIndices;
	}
	
	private ArrayList<MTGNode> findRefine(ArrayList<MTGNode> list)
	{
		if(list==null)
			return null;
		ArrayList<MTGNode> result = new ArrayList<MTGNode>();
		for(int i=0; i<list.size(); ++i)
		{
			MTGNode node = list.get(i);
			for(Edge e = node.getFirstEdge(); e!=null; e = e.getNext(node))
			{
				if((e.getSource() == node) && (e.testEdgeBits(Graph.REFINEMENT_EDGE)))
				{
					result.add((MTGNode)e.getTarget());
				}
			}
		}
		return result;
	}
	
	private void removeAllSuccOrBranchEdges()
	{
		//remove all succ/branch edges from the root
		for(Edge e = this.getFirstEdge(); e!=null; e = e.getNext(this))
		{
			if((e.getSource() == this) && (e.testEdgeBits(Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE)))
			{
				this.removeEdgeBitsTo(e.getTarget(), Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE, null);
			}
		}
	}
	
	private void removeAllSuccOrBranchEdges(int scale) throws MTGPlantFrameException
	{
		//remove all succ/branch edges from the root
		for(Edge e = this.getFirstEdge(); e!=null; e = e.getNext(this))
		{
			if((e.getSource() == this) && (e.testEdgeBits(Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE)))
			{
				Node target = e.getTarget();
				if(target instanceof MTGNode)
				{
					int index = getIndex((MTGNode)target);
					int nodeScale = getScale(index);
					if(nodeScale==scale)
						this.removeEdgeBitsTo(e.getTarget(), Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE, null);
				}
			}
		}
	}
	
	/**
	 * Connects this node to root nodes of the visible scales
	 * @param scalesVisible
	 * @throws MTGPlantFrameException 
	 */
	private void connectToVisibleScale(int[] rootList/*boolean[] scalesVisible*/) throws MTGPlantFrameException
	{

		//remove all succ/branch edges from the root
		//removeAllSuccOrBranchEdges();
		
		boolean[] scalesVisible=null;
		
		Object scaleVisibleObj = Library.workbench().getProperty(MTGKeys.MTG_SCALES_VISIBLE);
		if(scaleVisibleObj!=null)
			
		if((rootList!=null)&&(scaleVisibleObj!=null))
		{
			scalesVisible = (boolean[])scaleVisibleObj;
			for(int i=0; i<rootList.length; ++i)
			{
				MTGNode node = getNode(rootList[i]);
				if(scalesVisible[getScale(rootList[i])])
				{
					if(node!=null)
						this.addEdgeBitsTo(node, Graph.BRANCH_EDGE, null);
				}
			}
		}
	}
	
	@Override
	public void draw(Object object, boolean asNode, RenderState rs)
	{
		try
		{
			boolean[] scalesVisible=null;
			boolean scaleChanged=false;;
			
			Object scaleVisibleObj = Library.workbench().getProperty(MTGKeys.MTG_SCALES_VISIBLE);
			if(scaleVisibleObj!=null)
				scalesVisible = (boolean[])scaleVisibleObj;
			
			Object scaleChangedObj = Library.workbench().getProperty(MTGKeys.MTG_SCALES_CHANGED);
			if(scaleChangedObj!=null)
				scaleChanged = ((Boolean)scaleChangedObj).booleanValue();
			

			//user has changed the list of visible scales in GUI, compute plant frames for visible scales, delete plant 
			//frames for invisible scales
			if(scaleChanged)
			{
				for(int i=0; i<scalesVisible.length; ++i)
				{
					if(scalesVisible[i])
					{
						plantFrame(i,400);
					}
					else
					{
						plantFrameDelete(i);
					}
				}
				
				//turn flag off to indicate plant frame for necessary scales have already been computed.
				Library.workbench().setProperty(MTGKeys.MTG_SCALES_CHANGED, new Boolean(false));
			}
		}
		catch(Throwable t)
		{
			
		}
	}
/*
	@Override
	public ContextDependent getPolygonizableSource(GraphState gs) {
		return polygons;
	}

	public Polygonization getPolygonization ()
	{
		final class Poly implements Polygonization
		{
			final int visibleSides = this.visibleSides;

			public void polygonize (ContextDependent source, GraphState gs, PolygonArray out, int flags, float flatness)
			{
				polygonizeImpl (source, gs, out, flags, flatness);
			}

			public boolean equals (Object o)
			{
				if (!(o instanceof Poly))
				{
					return false;
				}
				Poly p = (Poly) o;
				return (p.visibleSides == visibleSides);
			}

			public int hashCode ()
			{
				return visibleSides;
			}
		}

		return new Poly ();
	}

	void polygonizeImpl (ContextDependent source, GraphState gs, PolygonArray out,
			int flags, float flatness)
	{
		if (polygons == null)
		{
			out.init (3);
		}
		else
		{
			polygons.polygonize (source, gs, out, flags, flatness);
			out.visibleSides = visibleSides;
		}
	}
	*/
	
	public void setElevationAzimuth(Vector3d vec, double ele, double azi)
	{
		vec.x=Math.cos(azi)*Math.cos(ele);
    	vec.y=Math.sin(azi)*Math.cos(ele);
    	vec.z=Math.sin(ele);
	}
	
	public void changeBenchmark(Vector3d ori, Vector3d u, Vector3d v, Vector3d w)
	{
		double X=ori.x;
		double Y=ori.y;
		double Z=ori.z;

		ori.x=X*u.x+Y*v.x+Z*w.x;
		ori.y=X*u.y+Y*v.y+Z*w.y;
		ori.z=X*u.z+Y*v.z+Z*w.z;
	}
}
