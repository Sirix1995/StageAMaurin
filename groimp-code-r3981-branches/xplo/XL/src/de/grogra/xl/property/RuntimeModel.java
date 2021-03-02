
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

package de.grogra.xl.property;

import de.grogra.reflect.TypeLoader;

/**
 * This <code>RuntimeModel</code> is used by the XL run-time system
 * as an interface to the access of properties.
 * It corresponds to a compile-time model which was used at
 * compile-time: The invocation of
 * {@link de.grogra.xl.property.CompiletimeModel#getRuntimeName()} returns a name,
 * which is passed to {@link RuntimeModelFactory#modelForName} in order to
 * obtain the corresponding <code>RuntimeModel</code>.
 * <p>
 * A comprehensive specification of the behaviour of <code>RuntimeModel</code>
 * is given by the specification of the XL programming language.
 *
 * @author Ole Kniemeyer
 */
public interface RuntimeModel
{

	/**
	 * Initializes this model. This method is invoked by
	 * {@link RuntimeModelFactory#modelForName} after a new <code>Model</code>
	 * instance has been created. The format of <code>params</code>
	 * depends on implementations of <code>Model</code>.
	 * 
	 * @param params initialization parameters, possibly <code>null</code>
	 */
	void initialize (String params);


	/**
	 * Returns the run-time <code>Property</code> for a given
	 * <code>name</code>. The <code>name</code> has to be a name
	 * which was returned by an
	 * invocation of {@link CompiletimeModel.Property#getRuntimeName()}
	 * for a compile-time property at compile-time. The implementation has
	 * to return the corresponding run-time property. If classes have to
	 * be loaded, the <code>loader</code> has to be used.
	 * 
	 * @param name the name of the property, as returned by the compile-time property
	 * @param loader a loader for loading of classes
	 * @return the run-time property corresponding to <code>name</code>
	 */
	Property propertyForName (String name, TypeLoader loader);


	Property propertyForName (String name, ClassLoader loader);


	interface Property
	{
		Class<?> getType ();

	/*!!
	#foreach ($type in $types)
	$pp.setType($type)
		$type get$pp.Type (Object object, int[] indices);
	
		void set$pp.Type (Object object, int[] indices, $type value);
	#end
	!!*/
//!! #* Start of generated code
		
		boolean getBoolean (Object object, int[] indices);
	
		void setBoolean (Object object, int[] indices, boolean value);
		
		byte getByte (Object object, int[] indices);
	
		void setByte (Object object, int[] indices, byte value);
		
		short getShort (Object object, int[] indices);
	
		void setShort (Object object, int[] indices, short value);
		
		char getChar (Object object, int[] indices);
	
		void setChar (Object object, int[] indices, char value);
		
		int getInt (Object object, int[] indices);
	
		void setInt (Object object, int[] indices, int value);
		
		long getLong (Object object, int[] indices);
	
		void setLong (Object object, int[] indices, long value);
		
		float getFloat (Object object, int[] indices);
	
		void setFloat (Object object, int[] indices, float value);
		
		double getDouble (Object object, int[] indices);
	
		void setDouble (Object object, int[] indices, double value);
		
		Object getObject (Object object, int[] indices);
	
		void setObject (Object object, int[] indices, Object value);
//!! *# End of generated code
	}
}
