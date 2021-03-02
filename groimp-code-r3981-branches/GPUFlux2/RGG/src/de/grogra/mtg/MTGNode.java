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

import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.mtg.MTGError.MTGPlantFrameException;
import de.grogra.turtle.F;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGNode extends F{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6891957776951480685L;

	//private MTGNodeData nodeData;
	
	//General Attributes
	public String mtgClass;
	public int mtgClassID;
	public int mtgID;
	public int mtgScale;
	
	//Standard Attributes
		public double L1;//TR_X											
		public double L2;//TR_Y											
		public double L3;//TR_Z											
	
		//Distances between vertices
		public double DAB;					//TR_DAB										 // Distance between points A and B.
		public double DAC;					//TR_DAC										 // Distance between points A and C.
		public double DBC;					//TR_DBC										 // Distance between points B and C.
	
		// Attributes containing the coordinates in a Cartesian system of reference.
		public double XX;					//CA_X											
		public double YY;					//CA_Y											
		public double ZZ;					//CA_Z											
	
		//Attributes containing measures.
		public double Length;				//ATT_LENGTH      //NOTE: super class F already contains attribute length
		public double Azimut;				//ATT_AZIMUT								
		public double Alpha;					//ATT_ALPHA									
		public double AA;					//ATT_TETA										
		public double BB;					//ATT_PHI										
		public double CC;					//ATT_PSI										
		public double TopDia;				//ATT_TOPDIA								
		public double BotDia;				//ATT_BOTTOMDIA								
		public double Position;				//ATT_POSITION							
		public int Category;				//ATT_CATEGORY							
		public Vector3d DirectionPrimary;	//ATT_DIRECTION_PRI			
		public int Order;					//ATT_ORDER									
	
	//Standard Attribute flags - indicate if the value is specified in the MTG file
		public int stdAttFlag;
		/*
		public boolean hasL1;
		public boolean hasL2;
		public boolean hasL3;
		
		public boolean hasDAB;
		public boolean hasDAC;
		public boolean hasDBC;
		
		public boolean hasXX;
		public boolean hasYY;
		public boolean hasZZ;
		
		public boolean hasLength;
		public boolean hasAzimut;			//ATT_AZIMUT		
		public boolean hasAlpha;			//ATT_ALPHA			
		public boolean hasAA;				//ATT_TETA			
		public boolean hasBB;				//ATT_PHI			
		public boolean hasCC;				//ATT_PSI			
		public boolean hasTopDia;			//ATT_TOPDIA		
		public boolean hasBotDia;			//ATT_BOTTOMDIA		
		public boolean hasPosition;			//ATT_POSITION		
		public boolean hasCategory;			//ATT_CATEGORY		
		public boolean hasDirectionPrimary;	//ATT_DIRECTION_PRI	
		public boolean hasOrder;			//ATT_ORDER			
		*/
		
	//flag to indicate if node (imported from an MTG file) contains imported data
		public int dataFlag; //0 - no data; 1 - has data
	
	public MTGNode()
	{
		super();
		//this.nodeData = new MTGNodeData();
		initMTGAttributes();
		this.length = 0;//turtle interpretation length should be 0 by default for mtg nodes
		dataFlag = 0;
	}
	
	private void initMTGAttributes()
	{
		/*
		hasL1=false;
		hasL2=false;
		hasL3=false;

		hasDAB=false;
		hasDAC=false;
		hasDBC=false;

		hasXX=false;
		hasYY=false;
		hasZZ=false;

		hasLength=false;
		hasAzimut=false;
		hasAlpha=false;
		hasAA=false;
		hasBB=false;
		hasCC=false;
		hasTopDia=false;
		hasBotDia=false;
		hasPosition=false;
		hasCategory=false;
		hasDirectionPrimary=false;
		hasOrder=false;
		*/
		
		stdAttFlag=0;

		L1=0;//TR_X											
		L2=0;//TR_Y											
		L3=0;//TR_Z											
	
		//Distances between vertices
		DAB=0;					//TR_DAB										 // Distance between points A and B.
		DAC=0;					//TR_DAC										 // Distance between points A and C.
		DBC=0;					//TR_DBC										 // Distance between points B and C.
	
		// Attributes containing the coordinates in a Cartesian system of reference.
		XX=0;					//CA_X											
		YY=0;					//CA_Y											
		ZZ=0;					//CA_Z											
	
		//Attributes containing measures.
		//public double length;				//ATT_LENGTH      //super class F already contains attribute length
		Length=0;
		Azimut=0;				//ATT_AZIMUT								
		Alpha=0;					//ATT_ALPHA									
		AA=0;					//ATT_TETA										
		BB=0;					//ATT_PHI										
		CC=0;					//ATT_PSI										
		TopDia=0;				//ATT_TOPDIA								
		BotDia=0;				//ATT_BOTTOMDIA								
		Position=0;				//ATT_POSITION							
		Category=0;				//ATT_CATEGORY							
		DirectionPrimary=new Vector3d();	//ATT_DIRECTION_PRI			
		Order=-1;					//ATT_ORDER		
	}
	
	public void setDirectoryPrimary(Vector3d dirp)
	{
		if(dirp!=null)
			this.DirectionPrimary = new Vector3d(dirp);
	}
	
	public boolean hasStdAtt(int mask)
	{
		return ((this.stdAttFlag & mask)==mask);
	}
	
	public void setStdAttFlagOn(int mask)
	{
		this.stdAttFlag |= mask;
	}
	
	public void setStdAttFlagOff(int mask)
	{
		this.stdAttFlag &= ~mask;
	}
	
	public boolean hasL1(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.TR_X));}
	public boolean hasL2(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.TR_Y));}
	public boolean hasL3(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.TR_Z));}

	public boolean hasDAB(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.TR_DAB));}
	public boolean hasDAC(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.TR_DAC));}
	public boolean hasDBC(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.TR_DBC));}

	public boolean hasXX(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.CA_X));}
	public boolean hasYY(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.CA_Y));}
	public boolean hasZZ(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.CA_Z));}

	public boolean hasLength(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_LENGTH));}
	public boolean hasAzimut(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_AZIMUT));}			//ATT_AZIMUT		
	public boolean hasAlpha(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_ALPHA));}			//ATT_ALPHA			
	public boolean hasAA(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_TETA));}				//ATT_TETA			
	public boolean hasBB(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_PHI));}				//ATT_PHI			
	public boolean hasCC(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_PSI));}				//ATT_PSI			
	public boolean hasTopDia(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_TOPDIA));}			//ATT_TOPDIA		
	public boolean hasBotDia(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_BOTTOMDIA));}			//ATT_BOTTOMDIA		
	public boolean hasPosition(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_POSITION));}			//ATT_POSITION		
	public boolean hasCategory(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_CATEGORY));}			//ATT_CATEGORY		
	public boolean hasDirectionPrimary(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_DIRECTION_PRI));}	//ATT_DIRECTION_PRI	
	public boolean hasOrder(){return hasStdAtt(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_ORDER));}			//ATT_ORDER		
	
	/*
	public MTGNode(Object nodeData)
	{
		super();
		//this.nodeData = (MTGNodeData)nodeData;
	}
	
	public Object getObject(String key)
	{
		return nodeData.getObject(key);
	}
	
	public void setObject(String key, Object obj)
	{
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
	*/
	/**
	 * Get the list of topological sons with specified edge type connection of this MTGNode.
	 */
	public int[] topoSons(int edgeType)
	{
		try
		{
			ArrayList<Integer> topoSonsIndices = new ArrayList<Integer>();
			
			for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
			{
				boolean edgeTypeMatches=false;
				if(edgeType==MTGKeys.MTG_ANY)
					edgeTypeMatches = ((e.testEdgeBits(Graph.SUCCESSOR_EDGE)) || (e.testEdgeBits(Graph.BRANCH_EDGE)));
				else
					edgeTypeMatches = e.testEdgeBits(edgeType);
				
				if (edgeTypeMatches)
				{
					if(e.getSource () == this)
					{
						MTGNode targetNode;
						if(e.getTarget() instanceof MTGNode)
						{
							targetNode = (MTGNode)e.getTarget ();
							//topoSonsIndices.add((Integer)targetNode.getObject(MTGKeys.MTG_NODE_LIST_INDEX));
							topoSonsIndices.add(new Integer(targetNode.mtgID));
						}
					}
				}
			}
			
			int[] results = new int[topoSonsIndices.size()];
			for(int j=0; j<results.length; ++j)
			{
				results[j] = topoSonsIndices.get(j).intValue();
			}
			
			return results;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	/**
	 * Get the list of topological sons with specified edge type connection of this MTGNode.
	 */
	public MTGNode[] topoSonNodes(int edgeType)
	{
		try
		{
			ArrayList<MTGNode> topoSons= new ArrayList<MTGNode>();
			
			for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
			{
				boolean edgeTypeMatches=false;
				if(edgeType==MTGKeys.MTG_ANY)
					edgeTypeMatches = ((e.testEdgeBits(Graph.SUCCESSOR_EDGE)) || (e.testEdgeBits(Graph.BRANCH_EDGE)));
				else
					edgeTypeMatches = e.testEdgeBits(edgeType);
				
				if (edgeTypeMatches)
				{
					if(e.getSource () == this)
					{
						MTGNode targetNode;
						if(e.getTarget() instanceof MTGNode)
						{
							targetNode = (MTGNode)e.getTarget ();
							//topoSonsIndices.add((Integer)targetNode.getObject(MTGKeys.MTG_NODE_LIST_INDEX));
							topoSons.add(targetNode);
						}
					}
				}
			}
			
			MTGNode[] results = new MTGNode[topoSons.size()];
			for(int j=0; j<results.length; ++j)
			{
				results[j] = topoSons.get(j);
			}
			
			return results;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	/**
	 * Number of branch edges from this node to an end node.
	 * @param endNodeId
	 * @return
	 * @throws MTGPlantFrameException 
	 */
	public int order(MTGNode endNode) throws MTGPlantFrameException
	{
		if(endNode == null)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		
		int result = orderInternal(this, endNode, 0);
		
		return result;
	}
	
	private int orderInternal(MTGNode currNode, MTGNode endNode, int currOrder)
	{
		if(currNode.equals(endNode))
		{
			return currOrder;
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
						int newOrder = currOrder;
						if(currNode.getEdgeBitsTo(currNodeSons[i]) == Graph.BRANCH_EDGE)
							newOrder += 1;
						
						int res = orderInternal(currNodeSons[i],endNode, newOrder);
						if(res!=-1)
							result = res;
					}
					return result;
				}
			}
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		}
	}
	
	/**
	 * Number of successor or branch edges from this node to an end node.
	 * @param endNodeId
	 * @return
	 * @throws MTGPlantFrameException 
	 */
	public int height(MTGNode endNode) throws MTGPlantFrameException
	{
		if(endNode == null)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		
		int result = heightInternal(this, endNode, 0);
		
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
	
	/**
	 * Returns compositional father of this node
	 * @return MTGNode
	 */
	public MTGNode compoFather()
	{
		for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
		{
			if (e.testEdgeBits(Graph.REFINEMENT_EDGE))
			{
				if(e.getTarget () == this)
				{
					return (MTGNode)e.getSource();
				}
			}
		}
		return null;
	}
	
	/**
	 * Return compositional sons of this node
	 */
	public MTGNode[] compoSons()
	{
		ArrayList<MTGNode> compoSons= new ArrayList<MTGNode>();
		
		for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
		{
			if (e.testEdgeBits(Graph.REFINEMENT_EDGE))
			{
				if(e.getSource () == this)
				{
					if(e.getTarget() instanceof MTGNode)
					{
						compoSons.add((MTGNode)e.getTarget ());
					}
				}
			}
		}
		
		MTGNode[] results = new MTGNode[compoSons.size()];
		for(int j=0; j<results.length; ++j)
		{
			results[j] = compoSons.get(j);
		}
		
		return results;
	}
	
	/**
	 * Get the list of composition sons of this MTGNode.
	 */
	public int[] compoSonsIds()
	{
		try
		{
			ArrayList<Integer> compoSonsIndices = new ArrayList<Integer>();
			
			for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
			{
				if (e.testEdgeBits(Graph.REFINEMENT_EDGE))
				{
					if(e.getSource () == this)
					{
						MTGNode targetNode;
						if(e.getTarget() instanceof MTGNode)
						{
							targetNode = (MTGNode)e.getTarget ();
							//compoSonsIndices.add((Integer)targetNode.getObject(MTGKeys.MTG_NODE_LIST_INDEX));
							compoSonsIndices.add(new Integer(targetNode.mtgID));
						}
					}
				}
			}
			
			int[] results = new int[compoSonsIndices.size()];
			for(int j=0; j<results.length; ++j)
			{
				results[j] = compoSonsIndices.get(j).intValue();
			}
			
			return results;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	/**
	 * Get number of compositional sons.
	 * @return number of compositional sons.
	 */
	public Integer compoSonsCount()
	{
		try
		{
			int count=0;
			for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
			{
				if (e.testEdgeBits(Graph.REFINEMENT_EDGE))
				{
					if(e.getSource () == this)
					{
						MTGNode targetNode;
						if(e.getTarget() instanceof MTGNode)
						{
							targetNode = (MTGNode)e.getTarget ();
							count++;
						}
					}
				}
			}
			return new Integer(count);
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	/**
	 * Gets the order attribute value of the node.
	 * @return order
	 */
	public int getOrder()
	{
		try
		{
			if(hasOrder())
				return this.Order;
			return -1;
//			Object orderObj = getObject(MTGKeys.ATT_ORDER);
//			if(orderObj!=null)
//			{
//				return ((Integer)orderObj);
//			}
//			return new Integer(-1);
		}
		catch(Throwable t)
		{
			return -1;
		}
	}
	
	/**
	 * Gets the length attribute value of the node.
	 * @return length
	 */
	public double getLength()
	{
		try
		{
			if(hasLength())
				return this.Length; //return MTG 'Length'. NOTE: different from turtle interpretation variable 'length'
			else
				return -1.0;
//			Object orderObj = getObject(MTGKeys.ATT_LENGTH);
//			if(orderObj!=null)
//			{
//				return ((Float)orderObj);
//			}
//			return new Float(-1.0f);	
		}
		catch(Throwable t)
		{
			return -1.0;
		}
	}
		
	/**
	 * Find an adjacent node n to this one. The edgeBits are used to determine
	 * if a relation between those two nodes exists ({@link #testEdgeBits(int)}).
	 * If out is true and this node is the source or if in is true and this node
	 * is the target, n is returned. Thus, if in and out is true, any adjacent
	 * node where the connecting edge matches the edgeBits is considered.
	 * @param in true if edges incoming to this node should be considered
	 * @param out true if edges outgoing from this node should be considered
	 * @param edgeBits the type/types of edges to consider
	 * @return an adjacent node that matches the criteria or null if none found
	 */
	public MTGNode findAdjacentMTG (/*boolean in, */boolean out, int edgeBits, String entityClass, int entityIndex)
	{
		try
		{
			for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
			{
				if (e.testEdgeBits (edgeBits))
				{
					if (out && (e.getSource () == this))
					{
						MTGNode targetNode;
						if(e.getTarget() instanceof MTGNode)
						{
							targetNode = (MTGNode)e.getTarget ();
						
							String targetEntityClass = targetNode.mtgClass;
//							Object targetEntityClassObj = targetNode.getObject(MTGKeys.MTG_NODE_ENTITY_CLASS);
//							String targetEntityClass=new String();
//							if(targetEntityClassObj!=null)
//								targetEntityClass = (String)targetEntityClassObj;
							
							int targetEntityIndex = targetNode.mtgClassID;
//							Object targetEntityIndexObj = targetNode.getObject(MTGKeys.MTG_NODE_ENTITY_INDEX);
//							Integer targetEntityIndex = new Integer(-1);
//							if(targetEntityIndexObj!=null)
//								targetEntityIndex = (Integer)targetEntityIndexObj;
							
							//if( (targetEntityClass.equals(entityClass)) && (targetEntityIndex.equals(new Integer(entityIndex))) )
							if( (targetEntityClass.equals(entityClass)) && (targetEntityIndex == entityIndex) )
								return targetNode;
						}
						
					}
					//TODO: implement incoming adjacent
					//if (in && (e.getTarget () == this))
					//{
					//	return e.getSource ();
					//}
				}
			}
			return null;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	
	public static final NType $TYPE;

	//general attributes
	public static final NType.Field mtgClass$FIELD;
	public static final NType.Field mtgClassID$FIELD;
	public static final NType.Field mtgID$FIELD;
	public static final NType.Field mtgScale$FIELD;
	//standard attributes for MTG
	public static final NType.Field L1$FIELD;
	public static final NType.Field L2$FIELD;
	public static final NType.Field L3$FIELD;
	public static final NType.Field DAB$FIELD;
	public static final NType.Field DAC$FIELD;
	public static final NType.Field DBC$FIELD;
	public static final NType.Field XX$FIELD;
	public static final NType.Field YY$FIELD;
	public static final NType.Field ZZ$FIELD;
	public static final NType.Field Azimut$FIELD;
	public static final NType.Field Alpha$FIELD;
	public static final NType.Field AA$FIELD;
	public static final NType.Field BB$FIELD;
	public static final NType.Field CC$FIELD;
	public static final NType.Field TopDia$FIELD;
	public static final NType.Field BotDia$FIELD;
	public static final NType.Field Position$FIELD;
	public static final NType.Field Category$FIELD;
	public static final NType.Field DirectionPrimary$FIELD;		
	public static final NType.Field Order$FIELD;
	public static final NType.Field Length$FIELD;
	
	//flag indicators for standard attributes
	public static final NType.Field stdAttFlag$FIELD;
	
	//flag indicator for imported data
	public static final NType.Field dataFlag$FIELD;
	
	/*
	public static final NType.Field hasL1$FIELD;
	public static final NType.Field hasL2$FIELD;
	public static final NType.Field hasL3$FIELD;
	public static final NType.Field hasDAB$FIELD;
	public static final NType.Field hasDAC$FIELD;
	public static final NType.Field hasDBC$FIELD;
	public static final NType.Field hasXX$FIELD;
	public static final NType.Field hasYY$FIELD;
	public static final NType.Field hasZZ$FIELD;
	public static final NType.Field hasLength$FIELD;
	public static final NType.Field hasAzimut$FIELD;
	public static final NType.Field hasAlpha$FIELD;
	public static final NType.Field hasAA$FIELD;
	public static final NType.Field hasBB$FIELD;
	public static final NType.Field hasCC$FIELD;
	public static final NType.Field hasTopDia$FIELD;
	public static final NType.Field hasBotDia$FIELD;
	public static final NType.Field hasPosition$FIELD;
	public static final NType.Field hasCategory$FIELD;
	public static final NType.Field hasDirectionPrimary$FIELD;
	public static final NType.Field hasOrder$FIELD;
	*/
	
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MTGNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}
		
		@Override
		public void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((MTGNode) o).mtgClass = (String)value;
					return;
				case 21:
					((MTGNode) o).setDirectoryPrimary((Vector3d)value);
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
					return ((MTGNode) object).mtgClass;
				case 21:
					return ((MTGNode) object).DirectionPrimary;
			}
			return super.getObject (object);
		}
		
		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((MTGNode) o).mtgClassID = value;
					return;
				case 2:
					((MTGNode) o).mtgID = value;
					return;
				case 20:
					((MTGNode) o).Category = value;
					return;
				case 22:
					((MTGNode) o).Order = value;
					return;
				case 23:
					((MTGNode) o).stdAttFlag = value;
					return;
				case 25:
					((MTGNode) o).mtgScale = value;
					return;
				case 26:
					((MTGNode) o).dataFlag = value;
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
					return ((MTGNode) o).mtgClassID;
				case 2:
					return ((MTGNode) o).mtgID;
				case 20:
					return ((MTGNode) o).Category;
				case 22:
					return ((MTGNode) o).Order;
				case 23:
					return ((MTGNode) o).stdAttFlag;
				case 25:
					return ((MTGNode) o).mtgScale;
				case 26:
					return ((MTGNode) o).dataFlag;
			}
			return super.getInt (o);
		}
		
		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 3:
					((MTGNode) o).L1 = value;
					return;
				case 4:
					((MTGNode) o).L2 =  value;
					return;
				case 5:
					((MTGNode) o).L3 =  value;
					return;
				case 6:
					((MTGNode) o).DAB =  value;
					return;
				case 7:
					((MTGNode) o).DAC =  value;
					return;
				case 8:
					((MTGNode) o).DBC =  value;
					return;
				case 9:
					((MTGNode) o).XX =  value;
					return;
				case 10:
					((MTGNode) o).YY =  value;
					return;
				case 11:
					((MTGNode) o).ZZ = value;
					return;
				case 12:
					((MTGNode) o).Azimut = value;
					return;
				case 13:
					((MTGNode) o).Alpha = value;
					return;
				case 14:
					((MTGNode) o).AA = value;
					return;
				case 15:
					((MTGNode) o).BB = value;
					return;
				case 16:
					((MTGNode) o).CC = value;
					return;
				case 17:
					((MTGNode) o).TopDia = value;
					return;
				case 18:
					((MTGNode) o).BotDia = value;
					return;
				case 19:
					((MTGNode) o).Position = value;
					return;
				case 24:
					((MTGNode) o).Length = value;
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 3:
					return ((MTGNode) o).L1;
				case 4:
					return ((MTGNode) o).L2;
				case 5:
					return ((MTGNode) o).L3;
				case 6:
					return ((MTGNode) o).DAB;
				case 7:
					return ((MTGNode) o).DAC;
				case 8:
					return ((MTGNode) o).DBC;
				case 9:
					return ((MTGNode) o).XX;
				case 10:
					return ((MTGNode) o).YY;
				case 11:
					return ((MTGNode) o).ZZ;
				case 12:
					return ((MTGNode) o).Azimut;
				case 13:
					return ((MTGNode) o).Alpha;
				case 14:
					return ((MTGNode) o).AA;
				case 15:
					return ((MTGNode) o).BB;
				case 16:
					return ((MTGNode) o).CC;
				case 17:
					return ((MTGNode) o).TopDia;
				case 18:
					return ((MTGNode) o).BotDia;
				case 19:
					return ((MTGNode) o).Position;
				case 24:
					return ((MTGNode) o).Length;
			}
			return super.getDouble (o);
		}
	}
	
	static
	{
		$TYPE = new NType (MTGNode.class);
		
		$TYPE.addManagedField (mtgClass$FIELD = new _Field ("mtgClass", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.STRING, null, 0));
		$TYPE.addManagedField (mtgClassID$FIELD = new _Field ("mtgClassID", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		$TYPE.addManagedField (mtgID$FIELD = new _Field ("mtgID", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 2));
		
		
		$TYPE.addManagedField (L1$FIELD = new _Field ("L1", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 3));
		$TYPE.addManagedField (L2$FIELD = new _Field ("L2", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 4));
		$TYPE.addManagedField (L3$FIELD = new _Field ("L3", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 5));
		
		$TYPE.addManagedField (DAB$FIELD = new _Field ("DAB", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 6));
		$TYPE.addManagedField (DAC$FIELD = new _Field ("DAC", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 7));
		$TYPE.addManagedField (DBC$FIELD = new _Field ("DBC", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 8));
		
		$TYPE.addManagedField (XX$FIELD = new _Field ("XX", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 9));
		$TYPE.addManagedField (YY$FIELD = new _Field ("YY", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 10));
		$TYPE.addManagedField (ZZ$FIELD = new _Field ("ZZ", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 11));
		
		$TYPE.addManagedField (Azimut$FIELD = new _Field ("Azimut", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 12));
		$TYPE.addManagedField (Alpha$FIELD = new _Field ("Alpha", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 13));
		$TYPE.addManagedField (AA$FIELD = new _Field ("AA", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 14));
		$TYPE.addManagedField (BB$FIELD = new _Field ("BB", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 15));
		$TYPE.addManagedField (CC$FIELD = new _Field ("CC", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 16));
		$TYPE.addManagedField (TopDia$FIELD = new _Field ("TopDia", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 17));
		$TYPE.addManagedField (BotDia$FIELD = new _Field ("BotDia", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 18));
		$TYPE.addManagedField (Position$FIELD = new _Field ("Position", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 19));
		$TYPE.addManagedField (Category$FIELD = new _Field ("Category", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 20));
		$TYPE.addManagedField (DirectionPrimary$FIELD = new _Field ("DirectionPrimary", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 21));
		$TYPE.addManagedField (Order$FIELD = new _Field ("Order", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 22));
		$TYPE.addManagedField (stdAttFlag$FIELD = new _Field ("stdAttFlag", _Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.INT, null, 23));
		$TYPE.addManagedField (Length$FIELD = new _Field ("Length", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 24));
		
		$TYPE.addManagedField (mtgScale$FIELD = new _Field ("mtgScale", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 25));
		$TYPE.addManagedField (dataFlag$FIELD = new _Field ("dataFlag", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 26));
		/*
		$TYPE.addManagedField(hasL1$FIELD              = new _Field("hasL1"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 23));
		$TYPE.addManagedField(hasL2$FIELD              = new _Field("hasL2"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 24));  
		$TYPE.addManagedField(hasL3$FIELD              = new _Field("hasL3"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 25));  
		$TYPE.addManagedField(hasDAB$FIELD             = new _Field("hasDAB"               ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 26));  
		$TYPE.addManagedField(hasDAC$FIELD             = new _Field("hasDAC"               ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 27));     
		$TYPE.addManagedField(hasDBC$FIELD             = new _Field("hasDBC"               ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 28));       
		$TYPE.addManagedField(hasXX$FIELD              = new _Field("hasXX"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 29));    
		$TYPE.addManagedField(hasYY$FIELD              = new _Field("hasYY"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 30));    
		$TYPE.addManagedField(hasZZ$FIELD              = new _Field("hasZZ"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 31));    
		$TYPE.addManagedField(hasLength$FIELD          = new _Field("hasLength"            ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 32));           
		$TYPE.addManagedField(hasAzimut$FIELD          = new _Field("hasAzimut"            ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 33));            
		$TYPE.addManagedField(hasAlpha$FIELD           = new _Field("hasAlpha"             ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 34));         
		$TYPE.addManagedField(hasAA$FIELD              = new _Field("hasAA"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 35));    
		$TYPE.addManagedField(hasBB$FIELD              = new _Field("hasBB"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 36));   
		$TYPE.addManagedField(hasCC$FIELD              = new _Field("hasCC"                ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 37));    
		$TYPE.addManagedField(hasTopDia$FIELD          = new _Field("hasTopDia"            ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 38));           
		$TYPE.addManagedField(hasBotDia$FIELD          = new _Field("hasBotDia"            ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 39));       
		$TYPE.addManagedField(hasPosition$FIELD        = new _Field("hasPosition"          ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 40));                
		$TYPE.addManagedField(hasCategory$FIELD        = new _Field("hasCategory"          ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 41));                
		$TYPE.addManagedField(hasDirectionPrimary$FIELD= new _Field("hasDirectionPrimary"  ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 42));              
		$TYPE.addManagedField(hasOrder$FIELD           = new _Field("hasOrder"             ,_Field.PUBLIC | _Field.SCO, de.grogra.reflect.Type.BOOLEAN,null, 43));
		*/
		
		
		$TYPE.declareFieldAttribute (mtgClass$FIELD, de.grogra.mtg.Attributes.MTG_CLASS);
		$TYPE.declareFieldAttribute (mtgClassID$FIELD, de.grogra.mtg.Attributes.MTG_CLASS_ID);
		$TYPE.declareFieldAttribute (mtgID$FIELD, de.grogra.mtg.Attributes.MTG_ID);
		$TYPE.declareFieldAttribute (L1$FIELD, de.grogra.mtg.Attributes.L1);
		$TYPE.declareFieldAttribute (L2$FIELD, de.grogra.mtg.Attributes.L2);
		$TYPE.declareFieldAttribute (L3$FIELD, de.grogra.mtg.Attributes.L3);
		$TYPE.declareFieldAttribute (DAB$FIELD, de.grogra.mtg.Attributes.DAB);
		$TYPE.declareFieldAttribute (DAC$FIELD, de.grogra.mtg.Attributes.DAC);
		$TYPE.declareFieldAttribute (DBC$FIELD, de.grogra.mtg.Attributes.DBC);
		$TYPE.declareFieldAttribute (XX$FIELD, de.grogra.mtg.Attributes.XX);
		$TYPE.declareFieldAttribute (YY$FIELD, de.grogra.mtg.Attributes.YY);
		$TYPE.declareFieldAttribute (ZZ$FIELD, de.grogra.mtg.Attributes.ZZ);
		$TYPE.declareFieldAttribute (Azimut$FIELD, de.grogra.mtg.Attributes.Azimut);
		$TYPE.declareFieldAttribute (Alpha$FIELD, de.grogra.mtg.Attributes.Alpha);
		$TYPE.declareFieldAttribute (AA$FIELD, de.grogra.mtg.Attributes.AA);
		$TYPE.declareFieldAttribute (BB$FIELD, de.grogra.mtg.Attributes.BB);
		$TYPE.declareFieldAttribute (CC$FIELD, de.grogra.mtg.Attributes.CC);
		$TYPE.declareFieldAttribute (TopDia$FIELD, de.grogra.mtg.Attributes.TopDia);
		$TYPE.declareFieldAttribute (BotDia$FIELD, de.grogra.mtg.Attributes.BotDia);
		$TYPE.declareFieldAttribute (Position$FIELD, de.grogra.mtg.Attributes.Position);
		$TYPE.declareFieldAttribute (Category$FIELD, de.grogra.mtg.Attributes.Category);
		$TYPE.declareFieldAttribute (DirectionPrimary$FIELD, de.grogra.mtg.Attributes.DirectionPrimary);
		$TYPE.declareFieldAttribute (Order$FIELD, de.grogra.mtg.Attributes.Order);
		$TYPE.declareFieldAttribute (stdAttFlag$FIELD, de.grogra.mtg.Attributes.STD_ATT_FLAG);
		$TYPE.declareFieldAttribute (Length$FIELD, de.grogra.mtg.Attributes.Length);
		$TYPE.declareFieldAttribute (mtgScale$FIELD, de.grogra.mtg.Attributes.MTG_SCALE);
		$TYPE.declareFieldAttribute (dataFlag$FIELD, de.grogra.mtg.Attributes.DATA_FLAG);
		/*
		$TYPE.declareFieldAttribute(hasL1$FIELD              ,de.grogra.mtg.Attributes.hasL1                );
		$TYPE.declareFieldAttribute(hasL2$FIELD              ,de.grogra.mtg.Attributes.hasL2                );
		$TYPE.declareFieldAttribute(hasL3$FIELD              ,de.grogra.mtg.Attributes.hasL3                );
		$TYPE.declareFieldAttribute(hasDAB$FIELD             ,de.grogra.mtg.Attributes.hasDAB               );
		$TYPE.declareFieldAttribute(hasDAC$FIELD             ,de.grogra.mtg.Attributes.hasDAC               );
		$TYPE.declareFieldAttribute(hasDBC$FIELD             ,de.grogra.mtg.Attributes.hasDBC               );
		$TYPE.declareFieldAttribute(hasXX$FIELD              ,de.grogra.mtg.Attributes.hasXX                );
		$TYPE.declareFieldAttribute(hasYY$FIELD              ,de.grogra.mtg.Attributes.hasYY                );
		$TYPE.declareFieldAttribute(hasZZ$FIELD              ,de.grogra.mtg.Attributes.hasZZ                );
		$TYPE.declareFieldAttribute(hasLength$FIELD          ,de.grogra.mtg.Attributes.hasLength            );
		$TYPE.declareFieldAttribute(hasAzimut$FIELD          ,de.grogra.mtg.Attributes.hasAzimut            );
		$TYPE.declareFieldAttribute(hasAlpha$FIELD           ,de.grogra.mtg.Attributes.hasAlpha             );
		$TYPE.declareFieldAttribute(hasAA$FIELD              ,de.grogra.mtg.Attributes.hasAA                );
		$TYPE.declareFieldAttribute(hasBB$FIELD              ,de.grogra.mtg.Attributes.hasBB                );
		$TYPE.declareFieldAttribute(hasCC$FIELD              ,de.grogra.mtg.Attributes.hasCC                );
		$TYPE.declareFieldAttribute(hasTopDia$FIELD          ,de.grogra.mtg.Attributes.hasTopDia            );
		$TYPE.declareFieldAttribute(hasBotDia$FIELD          ,de.grogra.mtg.Attributes.hasBotDia            );
		$TYPE.declareFieldAttribute(hasPosition$FIELD        ,de.grogra.mtg.Attributes.hasPosition          );
		$TYPE.declareFieldAttribute(hasCategory$FIELD        ,de.grogra.mtg.Attributes.hasCategory          );
		$TYPE.declareFieldAttribute(hasDirectionPrimary$FIELD,de.grogra.mtg.Attributes.hasDirectionPrimary  );
		$TYPE.declareFieldAttribute(hasOrder$FIELD           ,de.grogra.mtg.Attributes.hasOrder             );
		*/
		$TYPE.validate ();
	}
	
	public NType getType()
	{
		return $TYPE;
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new MTGNode();
	}
	
	public static void copyStdAttributes(MTGNode oldNode, MTGNode newNode)
	{
		if((oldNode==null)||(newNode==null))
			return;
		
		newNode.mtgClass = oldNode.mtgClass;
		newNode.mtgID = oldNode.mtgID;
		newNode.mtgClassID = oldNode.mtgClassID;
		newNode.mtgScale = oldNode.mtgScale;
		newNode.L1 = oldNode.L1;
		newNode.L2 = oldNode.L2;
		newNode.L3 = oldNode.L3;
		newNode.DAB = oldNode.DAB;
		newNode.DAC = oldNode.DAC;
		newNode.DBC = oldNode.DBC;
		newNode.XX = oldNode.XX;
		newNode.YY = oldNode.YY;
		newNode.ZZ = oldNode.ZZ;
		newNode.Length = oldNode.Length;
		newNode.Azimut = oldNode.Azimut;
		newNode.Alpha = oldNode.Alpha;
		newNode.AA = oldNode.AA;
		newNode.BB = oldNode.BB;
		newNode.CC = oldNode.CC;
		newNode.TopDia = oldNode.TopDia;
		newNode.BotDia = oldNode.BotDia;
		newNode.Position = oldNode.Position;
		newNode.Category = oldNode.Category;
		newNode.setDirectoryPrimary(oldNode.DirectionPrimary);
		newNode.Order = oldNode.Order;
		newNode.stdAttFlag = oldNode.stdAttFlag;
	}
}
