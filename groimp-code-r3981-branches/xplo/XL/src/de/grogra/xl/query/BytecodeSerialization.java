
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

package de.grogra.xl.query;

import java.io.IOException;

import de.grogra.reflect.Field;
import de.grogra.reflect.Method;
import de.grogra.reflect.Type;

public interface BytecodeSerialization
{

	interface Serializable
	{
		void write (BytecodeSerialization out) throws IOException;
	}


/*!!
#foreach ($type in $primitives)
$pp.setType($type)

	void visit$pp.Type ($type value) throws IOException;

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	void visitBoolean (boolean value) throws IOException;
// generated
// generated
// generated
	void visitByte (byte value) throws IOException;
// generated
// generated
// generated
	void visitShort (short value) throws IOException;
// generated
// generated
// generated
	void visitChar (char value) throws IOException;
// generated
// generated
// generated
	void visitInt (int value) throws IOException;
// generated
// generated
// generated
	void visitLong (long value) throws IOException;
// generated
// generated
// generated
	void visitFloat (float value) throws IOException;
// generated
// generated
// generated
	void visitDouble (double value) throws IOException;
// generated
//!! *# End of generated code

	void visitObject (Object value) throws IOException;

	void visitClass (Type cls) throws IOException;

	void visitType (Type type) throws IOException;
	
	void visitField (Field field) throws IOException;

	void beginArray (int length, Type type) throws IOException;
	
	void beginArrayComponent (int index) throws IOException;
	
	void endArrayComponent () throws IOException;

	void endArray () throws IOException;

	void beginMethod (Method method) throws IOException;

	void endMethod () throws IOException;
}
