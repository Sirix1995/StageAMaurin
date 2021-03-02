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

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.mtg.MTGError.MTGPlantFrameException;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGNode extends ShadedNull{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6891957776951480685L;

	private MTGNodeData nodeData;
	
	public MTGNode()
	{
		this.nodeData = new MTGNodeData();
	}
	
	public MTGNode(Object nodeData)
	{
		this.nodeData = (MTGNodeData)nodeData;
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
	
	/**
	 * Get the list of topological sons with specified edge type connection of this MTGNode.
	 */
	public int[] topoSons(int edgeType)
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
						topoSonsIndices.add((Integer)targetNode.getObject(MTGKeys.MTG_NODE_LIST_INDEX));
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
	
	/**
	 * Get the list of composition sons of this MTGNode.
	 */
	public int[] compoSons()
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
						compoSonsIndices.add((Integer)targetNode.getObject(MTGKeys.MTG_NODE_LIST_INDEX));
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
					
						Object targetEntityClassObj = targetNode.getObject(MTGKeys.MTG_NODE_ENTITY_CLASS);
						String targetEntityClass=new String();
						if(targetEntityClassObj!=null)
							targetEntityClass = (String)targetEntityClassObj;
						
						Object targetEntityIndexObj = targetNode.getObject(MTGKeys.MTG_NODE_ENTITY_INDEX);
						Integer targetEntityIndex = new Integer(-1);
						if(targetEntityIndexObj!=null)
							targetEntityIndex = (Integer)targetEntityIndexObj;
						
						if( (targetEntityClass.equals(entityClass)) && (targetEntityIndex.equals(new Integer(entityIndex))) )
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
	
	public static final NType $TYPE;

	public static final NType.Field nodeData$FIELD;
	
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
					((MTGNode) o).setData((MTGNodeData)value);
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
					return ((MTGNode) object).getData();
			}
			return super.getObject (object);
		}
	}
	
	static
	{
		$TYPE = new NType (MTGNode.class);
		$TYPE.addManagedField (nodeData$FIELD = new _Field ("nodeData", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 0));
		$TYPE.declareFieldAttribute (nodeData$FIELD, Attributes.MTG_NODE_DATA);
		$TYPE.addDependency (nodeData$FIELD.getAttribute (), Attributes.MTG_NODE_DATA);
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
		return new MTGNode();
	}
}
