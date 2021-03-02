
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

package de.grogra.persistence;

import java.io.IOException;
import de.grogra.reflect.Type;
import de.grogra.persistence.ManageableType.*;

public interface PersistenceInput
{
	int PLAIN_OBJECT = 100;
	int NULL_OBJECT = 101;
	int STRING_OBJECT = 102;
	int SERIALIZED_OBJECT = 103;
	int MANAGEABLE_OBJECT = 104;
	int SHARED_OBJECT_REFERENCE = 105;
	int PERSISTENT_OBJECT_ID = 106;
	int ARRAY_OBJECT = 107;

	int MIN_UNUSED_KIND = 108;


	PersistenceBindings getBindings ();

	boolean readBoolean () throws IOException;

	byte readByte () throws IOException;

	int readUnsignedByte () throws IOException;

	short readShort () throws IOException;

	int readUnsignedShort () throws IOException;

	char readChar () throws IOException;

	int readInt () throws IOException;

	long readLong () throws IOException;

	float readFloat () throws IOException;

	double readDouble () throws IOException;

	String readName () throws IOException;

	String readString () throws IOException;

	int getNextObjectKind () throws IOException;

	Object readObject (int kind, Type type) throws IOException;

	Shareable readSharedObject () throws IOException;

	long readPersistentObjectId () throws IOException;

	PersistenceCapable readPersistentObject () throws IOException;

	int beginArray () throws IOException;

	boolean beginComponent (ArrayComponent c, int index) throws IOException;

	void endComponent () throws IOException;

	void endArray () throws IOException;

	ManageableType beginManaged () throws IOException;
	
	boolean areFieldsProvided ();

	Field beginField (ManageableType type, Field field) throws IOException;

	void endField () throws IOException;

	void endManaged () throws IOException;

	void setNested (boolean nested);

	PersistenceCapable resolveId (long inputId);
}
