
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

public class PersistenceOutputDecorator implements PersistenceOutput
{
	protected final PersistenceOutput out;


	public PersistenceOutputDecorator (PersistenceOutput out)
	{
		this.out = out;
	}


	public void beginArray (int length, Type ct) throws IOException
	{
		out.beginArray (length, ct);
	}


	public void beginFields () throws IOException
	{
		out.beginFields ();
	}


	public void beginField (ManageableType.Field field) throws IOException
	{
		out.beginField (field);
	}


	public boolean beginManaged (ManageableType type, boolean writeType) throws IOException
	{
		return out.beginManaged (type, writeType);
	}


	public void endArray () throws IOException
	{
		out.endArray ();
	}


	public void endField (ManageableType.Field field) throws IOException
	{
		out.endField (field);
	}


	public void endFields () throws IOException
	{
		out.endFields ();
	}


	public void endManaged (Object object, boolean writeType) throws IOException
	{
		out.endManaged (object, writeType);
	}


	public void setNested (boolean nested)
	{
		out.setNested (nested);
	}


	public void writeBoolean (boolean value) throws IOException
	{
		out.writeBoolean (value);
	}


	public void writeByte (int value) throws IOException
	{
		out.writeByte (value);
	}


	public void writeChar (int value) throws IOException
	{
		out.writeChar (value);
	}


	public void writeDouble (double value) throws IOException
	{
		out.writeDouble (value);
	}


	public void writeFloat (float value) throws IOException
	{
		out.writeFloat (value);
	}


	public void writeInt (int value) throws IOException
	{
		out.writeInt (value);
	}


	public void writeLong (long value) throws IOException
	{
		out.writeLong (value);
	}


	public void writeName (String value) throws IOException
	{
		out.writeName (value);
	}


	public void writeNullObject () throws IOException
	{
		out.writeNullObject ();
	}


	public void writeObject (Object object, Type type) throws IOException
	{
		out.writeObject (object, type);
	}


	public void writePersistentObjectReference (PersistenceCapable o)
		throws IOException
	{
		out.writePersistentObjectReference (o);
	}


	public void writeSharedObjectReference (Shareable o) throws IOException
	{
		out.writeSharedObjectReference (o);
	}


	public void writeShort (int value) throws IOException
	{
		out.writeShort (value);
	}


	public void writeString (String value) throws IOException
	{
		out.writeString (value);
	}


	public void writeStringObject (String value) throws IOException
	{
		out.writeStringObject (value);
	}

}
