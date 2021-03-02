
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

package de.grogra.ext.exchangegraph.helpnodes;

import java.util.List;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.persistence.ManageableType;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;

public class XEGUnknown {
	
	public static void handleImportProperties(Node node, List<Property> properties) throws NumberFormatException, IllegalAccessException {
		
		for (Property p : properties) {
			// assign own defined properties
			ManageableType.Field p$FIELD = node.getNType().getManagedField(
					p.getName());
			
			String fieldType = p$FIELD.getType().getName();
			if (fieldType.equals("boolean"))
				p$FIELD.setBoolean(node, Boolean.valueOf(p.getValue()));
			else if (fieldType.equals("char"))
				p$FIELD.setChar(node, p.getValue().charAt(0));
			else if (fieldType.equals("byte"))
				p$FIELD.setByte(node, Byte.valueOf(p.getValue()));
			else if (fieldType.equals("short"))
				p$FIELD.setShort(node, Short.valueOf(p.getValue()));
			else if ((fieldType.equals("int")) ||
					(fieldType.equals("integer")))
				p$FIELD.setInt(node, Integer.valueOf(p.getValue()));
			else if (fieldType.equals("long"))
				p$FIELD.setLong(node, Long.valueOf(p.getValue()));
			else if (fieldType.equals("float"))
				p$FIELD.setFloat(node, Float.valueOf(p.getValue()));
			else if (fieldType.equals("double"))
				p$FIELD.setDouble(node, Double.valueOf(p.getValue()));
			else if (fieldType.equals("java.lang.String"))
				p$FIELD.setObject(node, p.getValue());
			
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode, List<Class> unknownTypes) {
		NType nodeType = node.getNType();
		int fieldCount = nodeType.getManagedFieldCount();
		
		for (int i = 0; i < fieldCount; i++) {
			ManageableType.Field mf = nodeType.getManagedField(i);
			
			Type t = mf.getDeclaringType();
			Class c = t.getImplementationClass();
			boolean belongsToUnknownType = false;
			if (unknownTypes.contains(c))
				belongsToUnknownType = true;
			
			// filter out all fields which are not important
			if (belongsToUnknownType
					&& (Reflection.isPrimitiveOrString(mf.getType()))) {
				
				String fieldType = mf.getType().getName();
				String value = null;
				if (fieldType.equals("boolean"))
					value = String.valueOf(mf.getBoolean(node));
				else if (fieldType.equals("char"))
					value = String.valueOf(mf.getChar(node));
				else if (fieldType.equals("byte"))
					value = String.valueOf(mf.getByte(node));
				else if (fieldType.equals("short"))
					value = String.valueOf(mf.getShort(node));
				else if (fieldType.equals("int"))
					value = String.valueOf(mf.getInt(node));
				else if (fieldType.equals("long"))
					value = String.valueOf(mf.getLong(node));
				else if (fieldType.equals("float"))
					value = String.valueOf(mf.getFloat(node));
				else if (fieldType.equals("double"))
					value = String.valueOf(mf.getDouble(node));
				else if (fieldType.equals("java.lang.String"))
					value = (String) mf.getObject(node);
				
				// do not write the property if there is no value
				if (value != null) {
					Property xmlProperty = xmlNode.addNewProperty();
					xmlProperty.setName(mf.getSimpleName());
					xmlProperty.setValue(value);
				}
			}
		}
	}
}
