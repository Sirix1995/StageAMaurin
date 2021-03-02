
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

public interface PersistenceOutput
{
	void writeBoolean (boolean value) throws IOException;

	void writeByte (int value) throws IOException;

	void writeShort (int value) throws IOException;

	void writeChar (int value) throws IOException;

	void writeInt (int value) throws IOException;

	void writeLong (long value) throws IOException;

	void writeFloat (float value) throws IOException;

	void writeDouble (double value) throws IOException;

	void writeName (String value) throws IOException;

	void writeString (String value) throws IOException;

	void writeNullObject () throws IOException;

	void writeStringObject (String value) throws IOException;

	void writeObject (Object object, Type type) throws IOException;

	void writeSharedObjectReference (Shareable o) throws IOException;

	void writePersistentObjectReference (PersistenceCapable o) throws IOException;

	void beginArray (int length, Type componentType) throws IOException;

	void endArray () throws IOException;

	boolean beginManaged (ManageableType type, boolean writeType) throws IOException;

	void beginFields () throws IOException;

	void beginField (ManageableType.Field field) throws IOException;

	void endField (ManageableType.Field field) throws IOException;

	void endFields () throws IOException;

	void endManaged (Object object, boolean writeType) throws IOException;

	void setNested (boolean nested);
}
