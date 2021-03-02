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


package de.grogra.ext.exchangegraph.graphnodes;

import java.util.ArrayList;
import java.util.List;

import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Box;

public class PropertyBox extends Box implements PropertyNode {

	/**
	 * @author Qinqin Long
	 */
	private static final long serialVersionUID = 1L;
	List<Property> nodeProperties = new ArrayList<Property>();
	//enh:field getter setter
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field nodeProperties$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (PropertyBox.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((PropertyBox) o).nodeProperties = (List<Property>) value;
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
					return ((PropertyBox) o).getNodeProperties ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new PropertyBox ());
		$TYPE.addManagedField (nodeProperties$FIELD = new _Field ("nodeProperties", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (List.class), null, 0));
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
		return new PropertyBox ();
	}

	public List<Property> getNodeProperties ()
	{
		return nodeProperties;
	}

	public void setNodeProperties (List<Property> value)
	{
		nodeProperties$FIELD.setObject (this, value);
	}

//enh:end
	
	public void setNodePropertiesFromXEGNode(List<de.grogra.ext.exchangegraph.xmlbeans.Property> xegNodeProperties){
		for (de.grogra.ext.exchangegraph.xmlbeans.Property p : xegNodeProperties){
			Property np = new Property();
			np.setName(p.getName());
			np.setValue(p.getValue());
			nodeProperties.add(np);
		}
	}
	
	public void setNode(Node n){
		this.setLength(((Box) n).getLength());
		this.setWidth(((Box) n).getWidth());
		this.setHeight(((Box) n).getHeight());
	}
}
