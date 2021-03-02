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

import de.grogra.mtg.Attributes;
import de.grogra.mtg.MTGNode;
import de.grogra.mtg.MTGNodeData;

/**
 * @author yong
 * @since  2011-11-24
 */
public class U extends MTGNode{

	/**
	 * 
	 */
	/*
	private static final long serialVersionUID = -6891957776951480685L;

	private MTGNodeData nodeData;
	
	public U()
	{
		this.nodeData = new MTGNodeData();
	}
	
	public U(Object nodeData)
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
	*/
	public static final NType $TYPE;

	public static final NType.Field nodeData$FIELD;
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (U.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((U) o).setData((MTGNodeData)value);
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
					return ((U) object).getData();
			}
			return super.getObject (object);
		}
	}
	
	static
	{
		$TYPE = new NType (U.class);
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
		return new U();
	}
}
