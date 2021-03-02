
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

package de.grogra.reflect;

public interface Field extends Member
{
	int MODIFIERS = ACCESS_MODIFIERS | STATIC | FINAL | VOLATILE
		| TRANSIENT;
	int INTERFACE_MODIFIERS = PUBLIC | STATIC | FINAL;

	Field[] FIELD_0 = new Field[0];


	Type getType ();

/*!!
#foreach ($type in $types)
$pp.setType($type)

	$type get$pp.Type (Object object) throws IllegalAccessException;

	void set$pp.Type (Object object, $type value) throws IllegalAccessException;
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	boolean getBoolean (Object object) throws IllegalAccessException;
// generated
	void setBoolean (Object object, boolean value) throws IllegalAccessException;
// generated
// generated
	byte getByte (Object object) throws IllegalAccessException;
// generated
	void setByte (Object object, byte value) throws IllegalAccessException;
// generated
// generated
	short getShort (Object object) throws IllegalAccessException;
// generated
	void setShort (Object object, short value) throws IllegalAccessException;
// generated
// generated
	char getChar (Object object) throws IllegalAccessException;
// generated
	void setChar (Object object, char value) throws IllegalAccessException;
// generated
// generated
	int getInt (Object object) throws IllegalAccessException;
// generated
	void setInt (Object object, int value) throws IllegalAccessException;
// generated
// generated
	long getLong (Object object) throws IllegalAccessException;
// generated
	void setLong (Object object, long value) throws IllegalAccessException;
// generated
// generated
	float getFloat (Object object) throws IllegalAccessException;
// generated
	void setFloat (Object object, float value) throws IllegalAccessException;
// generated
// generated
	double getDouble (Object object) throws IllegalAccessException;
// generated
	void setDouble (Object object, double value) throws IllegalAccessException;
// generated
// generated
	Object getObject (Object object) throws IllegalAccessException;
// generated
	void setObject (Object object, Object value) throws IllegalAccessException;
//!! *# End of generated code
}
