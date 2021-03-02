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

package de.grogra.mtg.nodes;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.WireframeCanvas;
import de.grogra.imp3d.gl.GLDisplay;
import de.grogra.imp3d.objects.PolygonMesh;
import de.grogra.imp3d.objects.Polygons;
import de.grogra.mtg.Attributes;
import de.grogra.mtg.MTGBranch;
import de.grogra.mtg.MTGBranchElement;
import de.grogra.mtg.MTGDressingDefaultValues;
import de.grogra.mtg.MTGDressingFile;
import de.grogra.mtg.MTGError;
import de.grogra.mtg.MTGError.MTGPlantFrameException;
import de.grogra.mtg.MTGKeys;
import de.grogra.mtg.MTGNode;
import de.grogra.mtg.MTGNodeData;
import de.grogra.mtg.MTGNodeDataClasses;
import de.grogra.mtg.MTGNodeDataFeature;
import de.grogra.mtg.MTGSquares;
import de.grogra.mtg.MTGVoxel;
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
public class MTGRoot extends MTGNode implements Renderable,Polygonizable
{
	//FOR DEBUG
	int debugIndex=0;
	//END DEBUG
	
	protected PolygonMesh polygons;
	protected int visibleSides = Attributes.VISIBLE_SIDES_BOTH;
	
	private float dist;
	
	public static final NType $TYPE;

	public static final NType.Field nodeData$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MTGRoot.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
			case 0:
				((MTGRoot) o).setData((MTGNodeData)value);
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
				return ((MTGRoot) object).getData();
			}
			return super.getObject (object);
		}
	}

	static
	{
		$TYPE = new NType (MTGRoot.class);
		$TYPE.addManagedField (nodeData$FIELD = new _Field ("nodeData", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 0));
		$TYPE.declareFieldAttribute (nodeData$FIELD, Attributes.MTG_NODE_DATA);
		$TYPE.addDependency (nodeData$FIELD.getAttribute (), Attributes.MTG_NODE_DATA);
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
	}

	/**
	 * Removes inter-scale topological relations (i.e. successor and branching edges).
	 * @throws MTGPlantFrameException
	 */
	public void removeInterScaleTopoRelations() throws MTGPlantFrameException
	{
		//get list of root nodes (nodes without topological father)
		int[] rootList = theRoots(MTGKeys.MTG_ANY);

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
	private void removeInterScaleTopoRelationsInternal(int originIndex) throws MTGPlantFrameException
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
	 * Computes the frame of the plant at the specified scale. For a static situation only.
	 * The frame of the plant is a collection of branches. The branch elements contain a top diameter, bottom diameter,
	 * length, positional coordinates and directional coordinates.
	 * 
	 * Root nodes are nodes without topological parents, i.e. no father node with successor or branching edge to them.
	 *  
	 * @param scale
	 * @throws MTGPlantFrameException
	 */
	public void plantFrame(int scale, float dist) throws MTGPlantFrameException
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
		
		//get list of root nodes (nodes without topological father)
		int[] rootList = theRoots(scale);

		Object branchesObj = getObject(MTGKeys.MTG_NODE_BRANCHES);
		if(branchesObj==null)
		{
			ArrayList<MTGBranch> branches = new ArrayList<MTGBranch>();
			setObject(MTGKeys.MTG_NODE_BRANCHES,branches);				//branches for all plants in MTG
		}
		
		Object plantCountObj = getObject(MTGKeys.MTG_NODE_PLANT_COUNT);
		if(plantCountObj==null)
		{
			Integer plantCount = new Integer(0);
			setObject(MTGKeys.MTG_NODE_PLANT_COUNT,plantCount);			//number of plants in MTG
		}

		//compute branches
		for(int i=0; i<rootList.length; ++i)
		{
			lookForBranches(rootList[i],scale);
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
			//TODO: compute standard geometry
			break;
		}
		
		//normalize branches
		downSizeBranches();
	}
	
	private void downSizeBranches() throws MTGPlantFrameException
	{
		//Get scale factor
		Vector3d max = (Vector3d) getObject(MTGKeys.MTG_NODE_PLANT_MAX);
		Vector3d min = (Vector3d) getObject(MTGKeys.MTG_NODE_PLANT_MIN);
		
		double deltaX = max.x - min.x;
		double deltaY = max.y - min.y;
		double deltaZ = max.z - min.z;
		double scaleFactor = 1.0;
		
		if(deltaX>=deltaY)
		{
			if(deltaX>=deltaZ)
				scaleFactor = 1.0/deltaX;
			else
				scaleFactor = 1.0/deltaZ;
		}
		if(deltaY>deltaX)
		{
			if(deltaY>=deltaZ)
				scaleFactor = 1.0/deltaY;
			else
				scaleFactor = 1.0/deltaZ;
		}
		
		scaleFactor *= 10.0;
		
		//FOR DEBUG
		scaleFactor = 0.001;
		//END DEBUG
		
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
				
				float length = element.getLength();
				if(length!=0)
					element.setLength(length*(float)scaleFactor);
				
				float topdia = element.getTopDia();
				if(topdia!=0)
					element.setTopDia(topdia*(float)scaleFactor*0.1f);
				
				float botdia = element.getBotDia();
				if(botdia!=0)
					element.setBotDia(botdia*(float)scaleFactor*0.1f);
			}
		}
	}
	
	//TODO: javadoc
	private void computeGeometry(int coordType, int scale) throws MTGPlantFrameException
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
	private void computeBranchCoordinates(int branchIndex, int scale, int coordType) throws MTGPlantFrameException
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
				float length;
				
				//find if phi,psi,teta attributes exist
				Vector3d eulerValues = getEulerAttributes(getNode(element.getNodeIndex()));
				
				Vector3d dirVector = new Vector3d();
				dirVector.sub(elementCoords, origin);
				length = (float)dirVector.length();
				
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

				float alpha=0;
				float beta=0;

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

					float br_length=length/br_s.getLength();

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

					Vector3d toAddOrigin = new Vector3d(dirp);
					toAddOrigin.scale(br_length);
					origin.add(toAddOrigin);
				}
				
				// We should apply the rule of division of the segment information that if
				// Two vertices whose coordinates are entered are separated
				// At least one non-vertex information (flag == 1)
				
				float br_length;
				if (flag == 1) 
					br_length = length/element.getLength();
				else br_length = length;

				element.setDirp(direction);
				element.setDirs(direction_s);
				element.setAlpha(alpha);
				element.setBeta(beta);
				element.setLength(br_length);
				element.setOrigin(origin);

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
					
					float length;
					
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
									(String)
									getNode(branch_element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)
									);
					}
					
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
					
					float alpha=0;
					float beta=0;

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
	 * Computes perpendicular vector to the given direction
	 * @param priDirection
	 * @return perpendicular direction
	 */
	private Vector3d computeAPerpendicularDirection(Vector3d priDirection)
	{
		// the spherical coordinates of dir1 define two Euler angles:

		float azimuth = computeAzimuthBeta(priDirection);

		float elevation = -computeElevationAlpha(priDirection); // in Euler angles positive angles are oriented downwards

		// The last Euler angle is underdetermined : we fix it
		// at a value such that the secundary direction is correctly
		// oriented with respect to its definition in the Geom library:
		// should be oriented towards the positive x axis when the
		// principal direction is oriented towards the positive z axis.
		// This comes to defining the roling angle as 3*Pi/2:

		double roling = 3.0*Math.PI/2.0;

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
	
	private float computeAzimuthBeta(Vector3d dir)
	{
		double length = dir.length();
		double xx = (length>0)? dir.x:0;
		double result;

		float alpha = computeElevationAlpha(dir);
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

		return (float)result;
	}
	
	private float computeElevationAlpha(Vector3d dir)
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

		return (float)result;
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
		
		return new Vector3d(Math.cos(eulerValues.z)*Math.cos(eulerValues.x),
				Math.sin(eulerValues.z)*Math.cos(eulerValues.x),
                  -Math.sin(eulerValues.z));
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
		
		return new Vector3d(-Math.sin(eulerValues.z)*Math.cos(eulerValues.y)+Math.cos(eulerValues.z)*Math.sin(eulerValues.x)*Math.sin(eulerValues.y),
				Math.cos(eulerValues.z)*Math.cos(eulerValues.y)+Math.sin(eulerValues.z)*Math.sin(eulerValues.x)*Math.sin(eulerValues.y),
                  Math.sin(eulerValues.y)*Math.cos(eulerValues.x));
	}
	
	/**
	 * Get Euler measurement attributes of node.
	 * @param node
	 * @return Vector3d containing phi, psi, teta values if they exist, else null
	 */
	private Vector3d getEulerAttributes(MTGNode node)
	{
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
		Vector3d min = minVector3d(newVector,minPlant);
		Vector3d max = maxVector3d(newVector,maxPlant);
		setObject(MTGKeys.MTG_NODE_PLANT_MIN,min);
		setObject(MTGKeys.MTG_NODE_PLANT_MAX,max);
	}
	
	/**
	 * Finds coordinate values for the specified node.
	 * @param node
	 * @return Vector3d containing coordinates if they exist in node, else null.
	 * @throws MTGPlantFrameException 
	 */
	private Vector3d lookForCoords(int nodeIndex, int scale, int coordType) throws MTGPlantFrameException
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
	private Vector3d lookForOrigin(MTGBranchElement element, int scale, int coordType) throws MTGPlantFrameException
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
					float supportLength = supportElement.getLength();
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
	private int location(int nodeIndex) throws MTGPlantFrameException
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
	
	/**
	 * Branch - Diameter - Computes the diameters for each branch element.
	 * @param 
	 * @throws MTGPlantFrameException 
	 */
	private void computeDiameters(int branchIndex, int scale) throws MTGPlantFrameException
	{		
		//Get branch
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("Branch information not available for plant frame computation.");
		if(branches.size()-1<branchIndex)
			throw new MTGError.MTGPlantFrameException("Invalid branch index for branch retrieval.");
		MTGBranch branch = branches.get(branchIndex);
		
		//First, assign top diameter and bottom diameter values from respective nodes to their 
		//corresponding branch elements.
		assignGivenDiameters(branch);
		
		//Loop through each branch element to assign top and bottom diameter values
		float topDia=-1.0f;				//top diameter for this branch element
		float bottomDia=-1.0f;			//bottom diameter for this branch element
		float fatherNodeTopDia=-1.0f;	//top diameter of topological father node of this branch element
		float fatherNodeBotDia=-1.0f;   //bottom diameter of topological father node of this branch element
		
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
			        		fatherNodeTopDia=dressingFile.getMinTopDia(
			        				(String)(getNode(element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)))
			        				/(float)scale;
			        		fatherNodeBotDia=dressingFile.getMinBotDia((String)(getNode(element.getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS)))
			        				/(float)scale;
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
						
						float diaRatio = computeDiaRatio(numOfElementsToInterpolate,bottomDia,branch.getElement(nextElementBotDefined).getBotDia());
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
							float diaRatio = computeDiaRatio(numOfElementsToInterpolate,bottomDia,branch.getElement(nextElementTopDefined).getTopDia());
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
	private float computeDiaRatio(int numOfElements, float botDia, float topDia)
	{
		double result=Math.pow(
				(double)(botDia/topDia),
				1.0/(double)numOfElements
				);
		return (float)result;
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
			
			float top_dia=lookForTopDia(branch.getElement(i).getNodeIndex());
			float bot_dia=lookForBottomDia(branch.getElement(i).getNodeIndex());

			element.setTopDia(top_dia);
			element.setBotDia(bot_dia);
		}
	}
	
	/**
	 * Branch - Diameter - Searches in scale hierarchy for top diameter attribute values for the specified node.
	 * @param nodeIndex
	 * @return top diameter value
	 */
	private float lookForTopDia(int nodeIndex)
	{
		float result=-1;
		
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
	private float lookForBottomDia(int nodeIndex)
	{
		float result=-1;
		
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
	private float lookUpDia(int nodeIndex, boolean isTopDia)
	{
		MTGNode node = getNode(nodeIndex);
		Object diaObj;
		if(isTopDia)
			diaObj = node.getObject(MTGKeys.ATT_TOPDIA);
		else
			diaObj = node.getObject(MTGKeys.ATT_BOTTOMDIA);
		
		if(diaObj!=null)
			return ((Integer)diaObj).floatValue();
		else
		{
			int compoFatherIndex = compoFather(nodeIndex);
			
			if(compoFatherIndex!=MTGKeys.MTG_ROOT_NODE)
			{
				int[] fathersSons = this.compoSons(compoFatherIndex);
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
		return -1;
	}
	
	/**
	 * Branch - Diameter - Searches downwards in scale hierarchy for diameter attribute value for the specified node.
	 * @param nodeIndex
	 * @param isTopDia
	 * @return diameter attribute value
	 */
	private float lookDownDia(int nodeIndex, boolean isTopDia)
	{
		MTGNode node = getNode(nodeIndex);
		Object diaObj;
		if(isTopDia)
			diaObj = node.getObject(MTGKeys.ATT_TOPDIA);
		else
			diaObj = node.getObject(MTGKeys.ATT_BOTTOMDIA);
		
		if(diaObj!=null)
			return ((Integer)diaObj).floatValue();
		else
		{
			int[] compoSonsIndices = compoSons(nodeIndex);
			if(compoSonsIndices!=null)
				if(compoSonsIndices.length>=1)
					return lookDownDia(compoSonsIndices[0],isTopDia);
		}
		return -1;
	}
	
	/**
	 * Branch - Length - Computes the length for each branch element.
	 * @param branchIndex
	 * @throws MTGPlantFrameException 
	 */
	private void computeBranchLengths(int branchIndex, int coordType) throws MTGPlantFrameException
	{
		boolean coordinatesExist = coordinatesExistInBranch(branchIndex,coordType);
		/* TODO
		if(!coordinatesExist)
		{
			
		}
		else
		*/
		{
			int initialIndex=0;
			
			ArrayList<MTGBranch> branches = getBranches();
			if(branches==null)
				throw new MTGError.MTGPlantFrameException("Branch information does not exist for plant frame computation.");
			
			if(branches.size()-1<branchIndex)
				throw new MTGError.MTGPlantFrameException("Invalid branch index.");
			
			MTGBranch branch = branches.get(branchIndex);
			
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
						
						float length=0;
						if ((small_delta_index==0) || (big_delta_index==0))
							length= Float.MAX_VALUE;
						else
							length = (float)big_delta_index/(float)small_delta_index;
						
						
						if(length<1.0f)
						{
							MTGDressingFile dressingFile = getDressingFile();
							if(dressingFile==null)
								throw new MTGError.MTGPlantFrameException("Dressing information not found.");
							length=dressingFile.getMinLength(
									(String)(getNode(branch.getElement(j).getNodeIndex()).getObject(MTGKeys.MTG_NODE_ENTITY_CLASS))
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
	private int nextElementIndexWithCoordinates(MTGBranch branch, int initialElementIndex, int coordType) throws MTGPlantFrameException
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
	private boolean coordinatesExistInBranch(int branchIndex, int coordType) throws MTGPlantFrameException
	{
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("Branch information does not exist for plant frame computation.");
		
		if(branches.size()-1 < branchIndex)
			throw new MTGError.MTGPlantFrameException("Invalid branch index.");
		
		MTGBranch branch = branches.get(branchIndex);
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
	private void newTrunk(int branchIndex) throws MTGPlantFrameException
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
	private void setupCoordinateVariables() throws MTGPlantFrameException
	{
		//coordinate - origin
		Vector3d origin = new Vector3d(0,0,0);
		setObject(MTGKeys.MTG_NODE_COORD_ORIGIN,origin);
		//coordinate - squares (voxels)
		MTGDressingFile dressingFile = getDressingFile();
		float distance=0;
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
	public int mtgType() throws MTGPlantFrameException
	{
		// Look for the type of MTG which will be treat.
		// Type=1 : Standard mtg (by default).
		// Type=2 : Mtg with coordinates (triangular reference system).
		// Type=3 : Mtg with cartesian coordinates.
		
		//default result - standard mtg
		int result=MTGKeys.MTG_TYPE_STANDARD;
		
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
		
		return result;
	}
	
	/**
	 * Coordinates - Verify if feature or attributes for triangle reference coordinates exist in MTG.
	 * @return true if MTG contains all attributes required for triangle reference coordinate specifications, else false.
	 * @throws MTGPlantFrameException
	 */
	public boolean verifyTriangleReference() throws MTGPlantFrameException
	{
		boolean DAB,DBC,DAC,L1,L2,L3;
		DAB=DAC=DBC=L1=L2=L3=false;
		
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
		
		return (DAB && DBC && DAC && L1 && L2 && L3);
	}
	
	/**
	 * Coordinates - Verify if Cartesian x,y,z coordinates are included as feature attributes in the MTG.
	 * @return true if MTG contains all attributes required for cartesian coordinate specifications, else false.
	 * @throws MTGPlantFrameException
	 */
	public boolean verifyCartesianReference() throws MTGPlantFrameException
	{
		boolean x,y,z;
		x=y=z=false;
		
		ArrayList<MTGNodeDataFeature> mtgFeatures = getFeaturesInfo();
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
		}
		
		return (x && y && z);
	}
	
	/**
	 * Azimuth - Computes and/or propagates given azimuth values (phyllotaxy) to the branches. 
	 * @throws MTGPlantFrameException
	 */
	private void computeAzimuth(int scale) throws MTGPlantFrameException
	{
		//branch info
		ArrayList<MTGBranch> branches = getBranches();
		if(branches==null)
			throw new MTGError.MTGPlantFrameException("No branch information available for geometry computation.");
		
		//dressing info
		MTGDressingFile dressingFile = getDressingFile();
		if(dressingFile==null)
			throw new MTGError.MTGPlantFrameException("No dressing information available for geometry computation.");
		
		float beta=0; //starting azimuth value
		float base=dressingFile.getPhillotaxy()/dressingFile.getAzimutUnit();
		
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
						
						Float azimuth = lookForAzimuth(firstElement);
						if(azimuth!=null)
						{
							branches.get(k).getElement(0).setBeta(azimuth.floatValue());
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
	private Float lookForAzimuth(int nodeIndex) throws MTGPlantFrameException
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
	private Float lookForAzimuthUp(int nodeIndex) throws MTGPlantFrameException
	{		
		MTGNode node = getNode(nodeIndex);
		if(node==null)
			throw new MTGPlantFrameException("Node for specified index not found.");
		
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
	private int indexOfFirstBranchOccurrence(MTGBranch branch) throws MTGPlantFrameException
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
	private int numberOfBranchOccurrences(MTGBranch branch) throws MTGPlantFrameException
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
 	private int getBranchesCount()
	{
		ArrayList<MTGBranch> branches = getBranches();
		if(branches!=null)
		{
			return branches.size();
		}
		else
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
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
		Object branchesObj = getObject(MTGKeys.MTG_NODE_BRANCHES);
		if(branchesObj!=null)
		{
			return (ArrayList<MTGBranch>)branchesObj;
		}
		else
			return null;
	}
	
	/**
	 * Branches - Searches graph and builds a set of branches beginning from the node specified by the input index.
	 * @param originIndex
	 * @throws MTGPlantFrameException
	 */
	public void lookForBranches(int originIndex,int scale) throws MTGPlantFrameException
	{	
		if(getNode(originIndex)==null)
			throw new MTGPlantFrameException("Branch origin does not exist in node list.");

		ArrayList<Integer> nodeQueue = new ArrayList<Integer>();
		int nodeIndex;

		int topoFatherIndex = topoFather(originIndex,MTGKeys.MTG_ANY);
		
		//if origin is a root node (the first node of a plant with no topological father)
		//increment plant counter
		if(topoFatherIndex == MTGKeys.MTG_UNKNOWN_KEYCODE)
			incrementPlantCount();

		MTGBranch branch = new MTGBranch(topoFatherIndex,getPlantCount(),scale);
		MTGBranchElement branchElement = new MTGBranchElement(originIndex);

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
					branch.addElement(new MTGBranchElement(nodeIndex));
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

		while(!nodeQueue.isEmpty())
		{
			nodeIndex = nodeQueue.remove(0).intValue();
			lookForBranches(nodeIndex,scale);
		}
	}

	/**
	 * Plant - Increment the counter for the number of plants specified in this MTG.
	 * @throws MTGPlantFrameException
	 */
	public void incrementPlantCount() throws MTGPlantFrameException
	{
		Object plantCountObj = getObject(MTGKeys.MTG_NODE_PLANT_COUNT);
		if(plantCountObj==null)
			throw new MTGError.MTGPlantFrameException("Unable to retrieve plant count from MTG root node.");
		Integer plantCount = (Integer)plantCountObj;
		setObject(MTGKeys.MTG_NODE_PLANT_COUNT,new Integer(plantCount.intValue()+1));
	}
	
	/**
	 * Plant - Gets the number of plants recognized in this MTG so far.
	 * @return Number of plants recognized in this MTG so far.
	 * @throws MTGPlantFrameException
	 */
	public int getPlantCount() throws MTGPlantFrameException
	{
		Object plantCountObj = getObject(MTGKeys.MTG_NODE_PLANT_COUNT);
		if(plantCountObj==null)
			throw new MTGError.MTGPlantFrameException("Unable to retrieve plant count from MTG root node.");
		return ((Integer)plantCountObj).intValue();
	}
	
	/**
	 * Obtain a list of indices of root nodes refined the specified node. 
	 * @param scale
	 * @return List if indices of root nodes.
	 * @throws MTGPlantFrameException
	 */
	public int[] theRoots(/*int nodeId,*/ int scale) throws MTGPlantFrameException
	{
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
							//add to result list
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

		return resultList;
	}

	/**
	 * Classes - Returns information of MTG classes defined.
	 * @return List of class information
	 * @throws MTGPlantFrameException
	 */
	public ArrayList<MTGNodeDataClasses> getClassesInfo() throws MTGPlantFrameException
	{
		Object mtgClassesObj = getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		if(mtgClassesObj==null)
			throw new MTGError.MTGPlantFrameException("Expected class information in MTG root node not found.");
		return (ArrayList<MTGNodeDataClasses>)mtgClassesObj;
	}

	/**
	 * Classes - Get number of features/attributes defined in MTG.
	 * @return number of classes in MTG.
	 * @throws MTGPlantFrameException
	 */
	public int getClassesCount() throws MTGPlantFrameException
	{
		ArrayList<MTGNodeDataClasses> mtgClasses = getClassesInfo();
		return mtgClasses.size();
	}

	/**
	 * Features - Returns information of MTG features defined.
	 * @return List of feature information.
	 * @throws MTGPlantFrameException
	 */
	public ArrayList<MTGNodeDataFeature> getFeaturesInfo() throws MTGPlantFrameException
	{
		Object mtgFeaturesObj = getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		if(mtgFeaturesObj==null)
			throw new MTGError.MTGPlantFrameException("Expected feature information in MTG root node not found.");
		return (ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
	}
	
	/**
	 * Features - Get number of features/attributes defined in MTG.
	 * @return number of features in MTG.
	 * @throws MTGPlantFrameException
	 */
	public int getFeaturesCount() throws MTGPlantFrameException
	{
		ArrayList<MTGNodeDataFeature> mtgFeatures = getFeaturesInfo();
		return mtgFeatures.size();
	}

	/**
	 * Nodes - Returns MTGNode instance given its index.
	 * @param nodeIndex
	 * @return MTGNode at specified index
	 */
	public MTGNode getNode(int nodeIndex)
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

	/**
	 * Nodes - Returns index of MTGNode given its instance.
	 * @param node
	 * @return index of node.
	 */
	public int getIndex(MTGNode node)
	{
		/*
		Object nodeListObj = getObject(MTGKeys.MTG_NODE_NODELIST);
		ArrayList<MTGNode> nodeList;

		if((nodeListObj!=null) && (nodeListObj instanceof ArrayList<?>) )
		{
			nodeList = (ArrayList<MTGNode>)(nodeListObj);
			for(int j=0; j<nodeList.size(); ++j)
			{
				MTGNode tempNode = nodeList.get(j);
				if(tempNode.equals(node))
					return j;
			}
		}
		 */
		Object indexObj = node.getObject(MTGKeys.MTG_NODE_LIST_INDEX);
		if(indexObj!=null)
		{
			Integer index = (Integer)indexObj;
			return index.intValue();
		}

		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}

	/**
	 * Nodes - Get the edge bits (int) between a source node and a target node.
	 * @param sourceIndex
	 * @param targetIndex
	 * @return edge type
	 */
	public int edgeType(int sourceIndex, int targetIndex)
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

	/**
	 * Nodes - Returns index of node that is topological father of node specified by input nodeIndex.
	 * @param nodeIndex
	 * @param edgeType
	 * @return index of topological father node
	 */
	public int topoFather(int nodeIndex, int edgeType)
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

	/**
	 * Nodes - For usage by root MTG node. To find the topological sons of the node found in list of nodes at specified nodeIndex.
	 * @param nodeIndex
	 * @param edgeType
	 * @return indices of topological son nodes.
	 */
	public int[] topoSons(int nodeIndex, int edgeType)
	{
		MTGNode node = getNode(nodeIndex);
		if(node!=null)
			return node.topoSons(edgeType);
		else
			return null;
	}

	/**
	 * Nodes - Returns refinement father of the specified node.
	 * @param nodeIndex
	 * @return index of refinement father node.
	 */
	public int compoFather(int nodeIndex)
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
	
	/**
	 * Nodes - Returns refinement sons of the specified node.
	 * @param nodeIndex
	 * @return indices of refinement son nodes.
	 */
	public int[] compoSons(int nodeIndex)
	{
		MTGNode node = getNode(nodeIndex);
		if(node!=null)
			return node.compoSons();
		else
			return null;
	}
	
	/**
	 * Return topological father of node. If topological father does not exist, return compositional father.
	 * @param nodeIndex
	 * @return node index of topological or composition father
	 * @throws MTGPlantFrameException 
	 */
	public int getPrefix(int nodeIndex) throws MTGPlantFrameException
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
	public int getScale(int nodeIndex) throws MTGPlantFrameException
	{
		MTGNode node = getNode(nodeIndex);
		if(node==null)
			throw new MTGError.MTGPlantFrameException("Node of specified index cannot be found.");
		else
		{
			ArrayList<MTGNodeDataClasses> nodeDataClasses = getClassesInfo();
			for(int i=0; i<nodeDataClasses.size(); ++i)
			{
				Object entityClassObj = node.getObject(MTGKeys.MTG_NODE_ENTITY_CLASS);
				if(entityClassObj==null)
					throw new MTGError.MTGPlantFrameException("Entity class of node cannot be found.");
				String entityClass = (String)entityClassObj;
				if( (nodeDataClasses.get(i).getSymbol()).equals(entityClass) )
					return nodeDataClasses.get(i).getScale();
			}
			throw new MTGError.MTGPlantFrameException("Scale for specified node cannot be found.");
		}
	}
	
	/**
	 * Nodes - Checks if a node has coordinate attribute values
	 * @param node
	 * @param coordType
	 * @return true if coordinate attribute values exists, else false
	 */
	public boolean hasCoordAttributes(MTGNode node, int coordType)
	{
		if(node==null)
			return false;
		
		Object x,y,z;
		x=y=z=null;
		if(coordType== MTGKeys.MTG_TYPE_COORD_TRI_REF)
		{
			x=node.getObject(MTGKeys.TR_X);
			y=node.getObject(MTGKeys.TR_Y);
			z=node.getObject(MTGKeys.TR_Z);
		}
		else if(coordType== MTGKeys.MTG_TYPE_COORD_CARTESIAN)
		{
			x=node.getObject(MTGKeys.CA_X);
			y=node.getObject(MTGKeys.CA_Y);
			z=node.getObject(MTGKeys.CA_Z);
		}
		
		if((x!=null)&&(y!=null)&&(z!=null))
			return true;
		
		return false;
	}
	
	/**
	 * Nodes - Get coordinate feature values of a node.
	 * @param node
	 * @param coordType
	 * @return coordinate attribute values as Vector3d if they exists, else null
	 */
	public Vector3d getCoordAttributes(MTGNode node, int coordType)
	{
		if(node==null)
			return null;
		
		Object x,y,z;
		x=y=z=null;
		if(coordType== MTGKeys.MTG_TYPE_COORD_TRI_REF)
		{
			x=node.getObject(MTGKeys.TR_X);
			y=node.getObject(MTGKeys.TR_Y);
			z=node.getObject(MTGKeys.TR_Z);
		}
		else if(coordType== MTGKeys.MTG_TYPE_COORD_CARTESIAN)
		{
			x=node.getObject(MTGKeys.CA_X);
			y=node.getObject(MTGKeys.CA_Y);
			z=node.getObject(MTGKeys.CA_Z);
		}
		
		if((x!=null)&&(y!=null)&&(z!=null))
		{
			return new Vector3d((((Double)x).doubleValue()),
					(((Double)y).doubleValue()),
					(((Double)z).doubleValue())
					);
		}
		
		return null;
	}
	
	public Vector3d minVector3d(Vector3d a, Vector3d b)
	{
		double x = (a.x<=b.x)?a.x:b.x;
		double y = (a.y<=b.y)?a.y:b.y;
		double z = (a.z<=b.z)?a.z:b.z;
		
		return new Vector3d(x,y,z);
	}
	
	public Vector3d maxVector3d(Vector3d a, Vector3d b)
	{
		double x = (a.x>=b.x)?a.x:b.x;
		double y = (a.y>=b.y)?a.y:b.y;
		double z = (a.z>=b.z)?a.z:b.z;
		
		return new Vector3d(x,y,z);
	}
	
	public int getDebugIndex()
	{
		return debugIndex;
	}
	
	private void computeMesh(boolean[] scalesVisible)
	{	
		ArrayList<MTGBranch> branches = getBranches();

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
				Point3f[] top = element.getTopSurface(elementNext);
				Point3f[] bot = element.getBotSurface(elementNext);
				
				//put vertices into array
				for(int k=0; k<top.length;++k)
				{
					vertexData.push(top[k].x, top[k].y, top[k].z);
					vertexData.push(bot[k].x, bot[k].y, bot[k].z);
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
	
	@Override
	public void draw(Object object, boolean asNode, RenderState rs) {
		
		ArrayList<MTGBranch> branches = getBranches();
		boolean alternateColor=false;
		Point3f blueLight = new Point3f(0,0,1.0f);
		Point3f blueDark = new Point3f(0,0,0.5f);
		Point3f redLight = new Point3f(1.0f,0,0);
		Point3f redDark = new Point3f(0.5f,0,0);
		Point3f greenLight = new Point3f(0,1.0f,0);
		Point3f greenDark = new Point3f(0,0.5f,0);
		/*
		Point3f pt = new Point3f(1.0f,-2.0f,-1.0f);
//		Point3f vec = new Point3f(0,1.0f,0);
//		vec.scale(this.length);
		Point3f pt2 = new Point3f(-1.0f,
				1.0f,
				1.0f);
		Point3f vec = new Point3f();
		vec.sub(pt2,pt);
		
		int numOfSurfacePoints=8;

		//step 1 translate
		Matrix4d transToOrigin = new Matrix4d();
		transToOrigin.setIdentity();
		transToOrigin.setTranslation(new Vector3d(-pt.x,-pt.y,-pt.z));

		Point3f pt_t = new Point3f();
		Point3f pt2_t = new Point3f();
		transToOrigin.transform(pt,pt_t);
		transToOrigin.transform(pt2,pt2_t);


		//step 2 rotation to yz plane
		vec.sub(pt2_t, pt_t);
		double rotateToYZPlaneAngle=0;

		if((vec.x==0)&&(vec.y>=0))
			rotateToYZPlaneAngle=0;
		else if((vec.x==0)&&(vec.y<0))
			rotateToYZPlaneAngle=Math.PI;
		else if((vec.x>0)&&(vec.y>0))
			rotateToYZPlaneAngle=Math.atan(vec.x/vec.y);
		else if((vec.x>0)&&(vec.y<0))
			rotateToYZPlaneAngle=Math.atan(Math.abs(vec.y)/vec.x)+(Math.PI/2.0);
		else if((vec.x<0)&&(vec.y<0))
			rotateToYZPlaneAngle=Math.atan(Math.abs(vec.x)/Math.abs(vec.y)) + Math.PI;
		else if((vec.x<0)&&(vec.y>0))
			rotateToYZPlaneAngle=Math.atan(vec.y/Math.abs(vec.x)) + (Math.PI/2.0*3.0);
		else if((vec.y==0)&&(vec.x>0))
			rotateToYZPlaneAngle=(Math.PI/2.0);
		else if((vec.y==0)&&(vec.x<0))
			rotateToYZPlaneAngle=(Math.PI/2.0*3.0);

		Matrix4d rotateToYZPlane = new Matrix4d();
		rotateToYZPlane.setIdentity();
		rotateToYZPlane.rotZ(rotateToYZPlaneAngle);

		Point3f pt_r1 = new Point3f();
		Point3f pt2_r1 = new Point3f();
		rotateToYZPlane.transform(pt_t,pt_r1);
		rotateToYZPlane.transform(pt2_t,pt2_r1);


		//step 3 rotation to y axis
		vec.sub(pt2_r1, pt_r1);
		double rotateToYAxisAngle=0;

		if((vec.z==0)&&(vec.y>=0))
			rotateToYAxisAngle=0;
		else if((vec.z==0)&&(vec.y<0))
			rotateToYAxisAngle=Math.PI;
		else if((vec.z>0)&&(vec.y>0))
			rotateToYAxisAngle=Math.atan(vec.y/vec.z) + (Math.PI/2.0*3.0);
		else if((vec.z>0)&&(vec.y<0))
			rotateToYAxisAngle=Math.atan(vec.z/Math.abs(vec.y))+(Math.PI);
		else if((vec.z<0)&&(vec.y<0))
			rotateToYAxisAngle=Math.atan(Math.abs(vec.y)/Math.abs(vec.z)) + (Math.PI/2.0);
		else if((vec.z<0)&&(vec.y>0))
			rotateToYAxisAngle=Math.atan(Math.abs(vec.z)/vec.y);
		else if((vec.y==0)&&(vec.z>0))
			rotateToYAxisAngle=(Math.PI/2.0*3.0);
		else if((vec.y==0)&&(vec.z<0))
			rotateToYAxisAngle=(Math.PI/2.0);

		Matrix4d rotateToYAxis = new Matrix4d();
		rotateToYAxis.setIdentity();
		rotateToYAxis.rotX(rotateToYAxisAngle);

		Point3f pt_r2 = new Point3f();
		Point3f pt2_r2 = new Point3f();
		rotateToYAxis.transform(pt_r1,pt_r2);
		rotateToYAxis.transform(pt2_r1,pt2_r2);

		//step 4 transform pt and pt2 to align with y-axis
		Point3f pt_a = new Point3f(pt_r2);
		Point3f pt2_a = new Point3f(pt2_r2);

		Point3f[] topSurface = new Point3f[numOfSurfacePoints];
		Point3f[] botSurface = new Point3f[numOfSurfacePoints];
		
		//generate surface points
		double angleIncrement = (Math.PI*2.0/numOfSurfacePoints);
		for(int i=0; i<numOfSurfacePoints; i++)
		{
			double x = Math.cos((double)(i+1)*angleIncrement)*1.0;
			double z = Math.sin((double)(i+1)*angleIncrement)*1.0;
			botSurface[i] = new Point3f((float)x,0,(float)z);
		}
		for(int i=0; i<numOfSurfacePoints; i++)
		{
			double x = Math.cos((double)(i+1)*angleIncrement)*1.0;
			double z = Math.sin((double)(i+1)*angleIncrement)*1.0;
			topSurface[i] = new Point3f((float)x,pt2_a.y,(float)z);
		}

		
		
		
		
		//inverse transformations
		rotateToYAxis.invert();
		rotateToYZPlane.invert();
		transToOrigin.invert();

		//inverse transform surface points
		for(int i=0; i<botSurface.length; ++i)
		{
			rotateToYAxis.transform(botSurface[i]);
			rotateToYZPlane.transform(botSurface[i]);
			transToOrigin.transform(botSurface[i]);
		}
		for(int i=0; i<topSurface.length; ++i)
		{
			rotateToYAxis.transform(topSurface[i]);
			rotateToYZPlane.transform(topSurface[i]);
			transToOrigin.transform(topSurface[i]);
		}
		
		for(int m=0; m<topSurface.length-1; ++m)
		{
			rs.drawLine(topSurface[m], topSurface[m+1], blueDark, 1, null);
			//rs.drawPoint(topSurface[m],2,blueDark,1,null);
			rs.drawLine(botSurface[m], botSurface[m+1], redDark, 1, null);
			rs.drawLine(pt,pt2,redLight,1,null);
		}
		*/
		
		boolean[] scalesVisible=null;
		boolean scaleChanged=false;
		boolean drawPoly=false;
		if(rs instanceof GLDisplay)
		{
			scalesVisible = ((GLDisplay)rs).getScales();
			scaleChanged = ((GLDisplay)rs).getScaleChanged();
			drawPoly=true;
		}
		else if(rs instanceof WireframeCanvas)
		{
			scalesVisible = ((WireframeCanvas)rs).getScales();
			scaleChanged = ((WireframeCanvas)rs).getScaleChanged();
			drawPoly=false;
		}
		
		if(!drawPoly)
		{
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
					
					Vector3d origin = element.getOrigin();
					if(origin!=null)
					{
						Point3f pt = new Point3f((float)origin.x,(float)origin.y,(float)origin.z);
						
						if(j!=branch.getElementCount()-1)
						{
							MTGBranchElement element2 = branch.getElement(j+1);
							Vector3d origin2 = element2.getOrigin();
							if(origin2!=null)
							{
								Point3f pt2 = new Point3f((float)origin2.x,(float)origin2.y,(float)origin2.z);
								if(branch.getScale()==2)
								{
//									if(alternateColor)
//									{
//										//rs.drawPoint(pt, 2, blueLight, 1, null);
//										//rs.drawPoint(pt2, 2, blueLight, 1, null);
//										rs.drawLine(pt, pt2, blueLight, 0, null);
//										alternateColor=false;
//									}
//									else
									{
										//rs.drawPoint(pt, 2, blueDark, 1, null);
										//rs.drawPoint(pt2, 2, blueDark, 1, null);
										rs.drawLine(pt, pt2, blueDark, 0, null);
										alternateColor=true;
									}
								}
								else
								{
//									if(alternateColor)
//									{
//										rs.drawLine(pt, pt2, greenLight, 0, null);
//										alternateColor=false;
//									}
//									else
									{
										rs.drawLine(pt, pt2, greenDark, 0, null);
										alternateColor=true;
									}
								}
							}
						}
						else //draw last element
						{
							Vector3d dirp = new Vector3d(element.getDirp());
							if(dirp!=null)
							{
								dirp.scale(element.getLength());
								Point3f pt2 = new Point3f((float)(pt.x+dirp.x),
										(float)(pt.y+dirp.y),
										(float)(pt.z+dirp.z));
								if(branch.getScale()==2)
								{
//									if(alternateColor)
//									{
//										//rs.drawPoint(pt, 2, blueLight, 1, null);
//										//rs.drawPoint(pt2, 2, blueLight, 1, null);
//										rs.drawLine(pt, pt2, blueLight, 0, null);
//										alternateColor=false;
//									}
//									else
									{
										//rs.drawPoint(pt, 2, blueDark, 1, null);
										//rs.drawPoint(pt2, 2, blueDark, 1, null);
										rs.drawLine(pt, pt2, blueDark, 0, null);
										alternateColor=true;
									}
								}
								else
								{
//									if(alternateColor)
//									{
//										rs.drawLine(pt, pt2, greenLight, 0, null);
//										alternateColor=false;
//									}
//									else
									{
										rs.drawLine(pt, pt2, greenDark, 0, null);
										alternateColor=true;
									}
								}
							}
						}
					}
				}
				
			}
		}
		else //draw polygons
		{
			if((this.polygons==null)||(scaleChanged))
			{
				computeMesh(scalesVisible);
				((GLDisplay)rs).setScaleChanged(false);
			}
			
			rs.drawPolygons (this, object, asNode, null, -1, null);
		}
	}

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
}
