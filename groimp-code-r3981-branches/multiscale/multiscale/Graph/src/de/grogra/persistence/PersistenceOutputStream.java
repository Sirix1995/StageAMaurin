
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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import de.grogra.reflect.Type;
import de.grogra.xl.util.ObjectList;

public class PersistenceOutputStream extends OutputStream
	implements PersistenceOutput, DataOutput
{
	public static final int MAGIC = 0xdadababe;
	static final int IO_BEGIN = 0x86427531;
	static final int IO_END = 0xfedcba98;
	static final int IO_PERSISTENT_DATA = 0x05;

	private final ObjectList writtenHandles = new ObjectList ();
	private final BindingsCache cache;
	private final DataOutputStream out;
	private final ObjectOutputStream objectOut;
	
	private PersistenceManager manager;
	private int writtenMark;


	public PersistenceOutputStream (BindingsCache cache,
									DataOutputStream out) throws IOException
	{
		super ();
		this.cache = cache;
		this.out = out;
		out.writeInt (MAGIC);
		objectOut = new ObjectOutputStream (out);
		objectOut.flush ();
	}

	int[] getWrittenHandles (short cacheId)
	{
		int[] a = (int[]) writtenHandles.get (cacheId);
		if (a == null)
		{
			a = new int[3];
			writtenHandles.set (cacheId, a);
		}
		return a;
	}

	@Override
	public void flush () throws IOException
	{
		out.flush ();
	}


	@Override
	public void close () throws IOException
	{
		objectOut.close ();
		out.flush ();
		out.close ();
	}


	public void write (BindingsCache cache) throws IOException
	{
		writeShort (cache.getCacheId ());
		cache.write (this);
	}

	public void beginExtent (PersistenceManager manager) throws IOException
	{
		writeInt (IO_BEGIN);
		write (cache);
		this.manager = manager;
		writtenMark = manager.allocateBitMark (false);
	}


	public void endExtent () throws IOException
	{
		writeInt (IO_END);
		manager.disposeBitMark (writtenMark, true);
		manager = null;
	}


	@Override
	public void write (byte[] v) throws IOException
	{
		out.write (v);
	}


	@Override
	public void write (byte[] v, int offset, int length) throws IOException
	{
		out.write (v, offset, length);
	}


	@Override
	public void write (int v) throws IOException
	{
		out.write (v);
	}


	public void writeBytes (String v) throws IOException
	{
		out.writeBytes (v);
	}


	public void writeChars (String v) throws IOException
	{
		out.writeChars (v);
	}


	public void writeUTF (String v) throws IOException
	{
		out.writeUTF (v);
	}


	public void writeName (String v) throws IOException
	{
		out.writeUTF (v);
	}


	public void writeString (String v) throws IOException
	{
		out.writeUTF (v);
	}


	public void writeBoolean (boolean v) throws IOException
	{
		out.writeBoolean (v);
	}


	public void writeByte (int v) throws IOException
	{
		out.writeByte (v);
	}


	public void writeChar (int v) throws IOException
	{
		out.writeChar (v);
	}


	public void writeShort (int v) throws IOException
	{
		out.writeShort (v);
	}


	public void writeInt (int v) throws IOException
	{
		while (true)
		{
			int b = v & 0x7f;
			if ((v >>>= 7) != 0)
			{
				out.write (b | 0x80);
			}
			else
			{
				out.write (b);
				return;
			}
		}
	}


	public void writeLong (long v) throws IOException
	{
		writeInt ((int) (v >> 32));
		writeInt ((int) v);
/*
		for (int i = 7; i >= 0; i--)
		{
			int b = ((int) v) & 0x7f;
			if ((v >>= 7) != 0)
			{
				out.write (b | 0x80);
			}
			else
			{
				out.write (b);
				return;
			}
		}
		out.write ((int) v);
*/
	}


	public void writeFloat (float v) throws IOException
	{
		out.writeFloat (v);
	}


	public void writeDouble (double v) throws IOException
	{
		out.writeDouble (v);
	}


	public void writeNullObject () throws IOException
	{
		out.writeByte (PersistenceInput.NULL_OBJECT);
	}


	public void writeStringObject (String value) throws IOException
	{
		out.writeByte (PersistenceInput.STRING_OBJECT);
		out.writeUTF (value);
	}


	public void writeObject (Object v, Type type) throws IOException
	{
		out.writeByte (PersistenceInput.SERIALIZED_OBJECT);
		objectOut.writeObject (v);
		objectOut.flush ();
	}


	public void writeSharedObjectReference (Shareable o) throws IOException
	{
		out.writeByte (PersistenceInput.SHARED_OBJECT_REFERENCE);
		cache.write (o.getProvider (), this);
		o.getProvider ().writeObject (o, this);
	}


	public void writePersistentObjectReference (PersistenceCapable o)
		throws IOException
	{
		out.writeByte (PersistenceInput.PERSISTENT_OBJECT_ID);
		writeLong (o.getId ());
		if (!o.setBitMark (writtenMark, true))
		{
			out.write (IO_PERSISTENT_DATA);
			boolean diff = beginManaged (o.getManageableType (), true);
			o.getManageableType ().write (o, this, diff);
			endManaged (o, true);
		}
	}


	public void beginArray (int length, Type ct) throws IOException
	{
		out.writeByte (PersistenceInput.ARRAY_OBJECT);
		writeInt (length);
	}


	public void endArray ()
	{
	}


	public boolean beginManaged (ManageableType type, boolean writeType) throws IOException
	{
		if (writeType)
		{
			out.writeByte (PersistenceInputStream.MANAGEABLE_WITH_TYPE);
			cache.write (type, this);
		}
		else
		{
			out.writeByte (PersistenceInput.MANAGEABLE_OBJECT);
		}
		return true;
	}


	public void beginFields ()
	{
	}


	public void beginField (ManageableType.Field field) throws IOException
	{
		writeShort (field.getFieldId ());
	}


	public void endField (ManageableType.Field field)
	{
	}


	public void endFields () throws IOException
	{
		writeShort (-1);
	}


	public void endManaged (Object object, boolean writeType)
	{
	}


	public void setNested (boolean nested)
	{
	}

}
