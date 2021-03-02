
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

package de.grogra.msml;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import de.grogra.graph.Attributes;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.util.EnumValue;

public class MSNode extends Node implements ListModel
{
	static final ObjectAttribute SCALES
		= Attributes.init(new ObjectAttribute (NodeEnum.class, false, null),
			"scale", null, "");

	public static int SCALE=Graph.STD_EDGE_5;
	protected static int SELECTED=Graph.SUCCESSOR_EDGE;
	public static int SELECTEDSCALE=SELECTED|SCALE;
	
	

	public MSNode ()
	{
		super();
	}

	public Node getChoosenScale(){
		return getBranch();
	}
	
	public void setScale (EnumValue scale)
	{
		setScale (((NodeEnum) scale).scale);
	}
	
	public void addScale (Node scale)
	{
		addEdgeBitsTo(scale,SCALE,null);
		if (getScale()==null){
			setScale(scale);
		}
	}
	
	public void setScale (Node scale)
	{
		Node choosenScale=getScale();
		if (choosenScale!=null){
			removeEdgeBitsTo(choosenScale,SELECTED,getTransaction(true));
		}
		addEdgeBitsTo(scale,SELECTEDSCALE,getTransaction(true));
	}
	
	public Node getScale ()
	{
		return this.getSuccessor();
	}
	
	public EnumValue getScaleAsEnum ()
	{
		return new NodeEnum (getScale ());
	}

	class NodeEnum implements EnumValue
	{
		final Node scale;
	
		NodeEnum (Node scale)
		{
			this.scale = scale;
		}
		
		public ListModel getList ()
		{
			return MSNode.this;
		}
		
		public boolean equals (Object o)
		{
			return (o instanceof NodeEnum) && ((NodeEnum) o).scale == scale;
		}
		
		public String toString (){
			return scale.getName();
		}
	}

	public int getSize ()
	{
		int s = 0;
		for(Edge e=getFirstEdge();e!= null; e=e.getNext(this))
		{
			if (e.isSource(this))
			{
				s++;
			}
		}
		return s;
	}

	public Object getElementAt (int index)
	{
		int s = 0;
		for(Edge e=getFirstEdge();e!= null; e=e.getNext(this))
		{
			if (e.isSource(this))
			{
				if (s==index){
					return new NodeEnum(e.getTarget());
				}
				s++;
			}
			
		}
		return null;
	}

	public void addListDataListener (ListDataListener l)
	{
		// TODO Auto-generated method stub
		
	}

	public void removeListDataListener (ListDataListener l)
	{
		// TODO Auto-generated method stub
		
	}

	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == SCALES)
		{
			return getScaleAsEnum();
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}

	private static void initType ()
	{
		$TYPE.addAccessor (new AccessorBridge (SCALES)
			{
				public boolean isWritable (Object object, GraphState gs)
				{
					return true;
				}

				public Object setObject (Object object, Object value,
										 GraphState gs)
				{
					((MSNode) object).setScale((EnumValue) value);
					return value;
				}
			});
	}

//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new MSNode ());
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
		return new MSNode ();
	}

//enh:end

}
