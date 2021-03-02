
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

public class PersistenceInputDecorator implements PersistenceInput
{
	protected final PersistenceInput in;


	public PersistenceInputDecorator (PersistenceInput in)
	{
		this.in = in;
	}


	public int getNextObjectKind () throws IOException
	{
		return in.getNextObjectKind ();
	}


	public PersistenceManager getPersistenceManager ()
	{
		return in.getPersistenceManager ();
	}


	public int beginArray () throws IOException
	{
		return in.beginArray ();
	}


	public boolean beginComponent (ArrayComponent c, int index) throws IOException
	{
		return in.beginComponent (c, index);
	}


	public void endComponent () throws IOException
	{
		in.endComponent ();
	}


	public void endArray () throws IOException
	{
		in.endArray ();
	}


	public boolean readBoolean () throws IOException
	{
		return in.readBoolean ();
	}


	public byte readByte () throws IOException
	{
		return in.readByte ();
	}


	public char readChar () throws IOException
	{
		return in.readChar ();
	}


	public double readDouble () throws IOException
	{
		return in.readDouble ();
	}


	public float readFloat () throws IOException
	{
		return in.readFloat ();
	}


	public int readInt () throws IOException
	{
		return in.readInt ();
	}


	public long readLong () throws IOException
	{
		return in.readLong ();
	}


	public ManageableType beginManaged () throws IOException
	{
		return in.beginManaged ();
	}


	public boolean areFieldsProvided ()
	{
		return in.areFieldsProvided ();
	}


	public Field beginField (ManageableType type, Field field) throws IOException
	{
		return in.beginField (type, field);
	}


	public void endField () throws IOException
	{
		in.endField ();
	}


	public void endManaged () throws IOException
	{
		in.endManaged ();
	}


	public String readName () throws IOException
	{
		return in.readName ();
	}


	public Object readObject (int kind, Type type) throws IOException
	{
		return in.readObject (kind, type);
	}


	public long readPersistentObjectId () throws IOException
	{
		return in.readPersistentObjectId ();
	}


	public PersistenceCapable readPersistentObject () throws IOException
	{
		return in.readPersistentObject ();
	}


	public Shareable readSharedObject () throws IOException
	{
		return in.readSharedObject ();
	}


	public short readShort () throws IOException
	{
		return in.readShort ();
	}


	public String readString () throws IOException
	{
		return in.readString ();
	}


	public int readUnsignedByte () throws IOException
	{
		return in.readUnsignedByte ();
	}


	public int readUnsignedShort () throws IOException
	{
		return in.readUnsignedShort ();
	}


	public void setNested (boolean nested)
	{
		in.setNested (nested);
	}

}
