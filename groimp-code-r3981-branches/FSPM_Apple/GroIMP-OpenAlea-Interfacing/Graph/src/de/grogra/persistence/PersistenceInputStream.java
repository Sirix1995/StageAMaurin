
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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import de.grogra.persistence.ManageableType.ArrayComponent;
import de.grogra.persistence.ManageableType.Field;
import de.grogra.reflect.Type;
import de.grogra.util.IOWrapException;
import de.grogra.xl.util.ClassLoaderObjectInputStream;
import de.grogra.xl.util.ObjectList;

public class PersistenceInputStream extends FilterInputStream
	implements PersistenceInput, DataInput
{
	final static int MANAGEABLE_WITH_TYPE = MIN_UNUSED_KIND;

	private final PersistenceBindings bindings;
	private final DataInputStream dataIn;
	private final ObjectInputStream objectIn;

	private PersistenceManager manager;
	private BindingsCache cache;

	private ObjectList bindingsCaches = new ObjectList ();

	public PersistenceInputStream (PersistenceBindings bindings, DataInputStream in) throws IOException
	{
		super (in);
		this.bindings = bindings;
		this.dataIn = in;
		if (in.readInt () != PersistenceOutputStream.MAGIC)
		{
			throw new StreamCorruptedException ("Invalid MAGIC number");
		}
		objectIn = new ClassLoaderObjectInputStream (in, bindings.getTypeLoader ());
	}


	public BindingsCache getCache (short cacheId) throws IOException
	{
		BindingsCache c = (BindingsCache) bindingsCaches.get (cacheId);
		if (c == null)
		{
			c = new BindingsCache (bindings, cacheId);
			bindingsCaches.set (cacheId, c);
		}
		return c;
	}


	public BindingsCache readCache () throws IOException
	{
		cache = getCache (readShort ());
		cache.read (this);
		return cache;
	}

	public void beginExtent (PersistenceManager manager) throws IOException
	{
		checkInt (PersistenceOutputStream.IO_BEGIN);
		readCache ();
		this.manager = manager;
	}


	public PersistenceBindings getBindings ()
	{
		return bindings;
	}


	public void endExtent () throws IOException
	{
		checkInt (PersistenceOutputStream.IO_END);
		manager = null;
	}


/*
	public final PersistenceBindings getBindings ()
	{
		return bindings;
	}
*/

	public final void checkInt (int expected) throws IOException
	{
		int r;
		if ((r = readInt ()) != expected)
		{
			throw new StreamCorruptedException
				("Expected 0x" + Integer.toHexString (expected) + ", read 0x"
				 + Integer.toHexString (r));
		}
	}


	public final void check (int expected) throws IOException
	{
		int r;
		if ((r = dataIn.readUnsignedByte ()) != expected)
		{
			throw new StreamCorruptedException
				("Expected 0x" + Integer.toHexString (expected) + ", read 0x"
				 + Integer.toHexString (r));
		}
	}


	@Override
	public final boolean markSupported ()
	{
		return false;
	}


	@Override
	public final void reset () throws IOException
	{
		throw new IOException ("reset not supported");
	}


	public void readFully (byte[] b) throws IOException
	{
		dataIn.readFully (b);
	}


	public void readFully (byte[] b, int off, int len) throws IOException
	{
		dataIn.readFully (b, off, len);
	}


	public int skipBytes (int n) throws IOException
	{
		return dataIn.skipBytes (n);
	}


	public boolean readBoolean () throws IOException
	{
		return dataIn.readBoolean ();
	}


	public byte readByte () throws IOException
	{
		return dataIn.readByte ();
	}


	public int readUnsignedByte () throws IOException
	{
		return dataIn.readUnsignedByte ();
	}


	public short readShort () throws IOException
	{
		return dataIn.readShort ();
	}


	public int readUnsignedShort () throws IOException
	{
		return dataIn.readUnsignedShort ();
	}


	public char readChar () throws IOException
	{
		return dataIn.readChar ();
	}


	public int readInt () throws IOException
	{
		int v = 0, n = 0;
		while (true)
		{
			int b = dataIn.readUnsignedByte ();
			if ((b & 0x80) == 0)
			{
				return v | (b << n);
			}
			else
			{
				v |= (b & 0x7f) << n;
				n += 7;
			}
		}
	}


	public long readLong () throws IOException
	{
		return ((long) readInt () << 32) | (readInt () & 0xffffffffL);
	}


	public float readFloat () throws IOException
	{
		return dataIn.readFloat ();
	}


	public double readDouble () throws IOException
	{
		return dataIn.readDouble ();
	}


	public String readLine () throws IOException
	{
		return dataIn.readLine ();
	}


	public String readUTF () throws IOException
	{
		return dataIn.readUTF ();
	}


	public String readName () throws IOException
	{
		return dataIn.readUTF ();
	}


	public String readString () throws IOException
	{
		return dataIn.readUTF ();
	}


	private int objectType = -1;

	public int getNextObjectKind () throws IOException
	{
		int t = dataIn.readByte ();
		objectType = t;
		return (t == MANAGEABLE_WITH_TYPE) ? MANAGEABLE_OBJECT : t;
	}


	private int nextByte () throws IOException
	{
		int t;
		if ((t = objectType) >= 0)
		{
			objectType = -1;
			return t;
		}
		else
		{
			return dataIn.readByte ();
		}
	}


	private void consume (int expected) throws IOException
	{
		int t = nextByte ();
		if (t != expected)
		{
			throw new StreamCorruptedException ("Expected " + expected 
												+ ", read " + t);
		}
	}


	public Object readObject (int kind, Type type) throws IOException
	{
		switch (kind)
		{
			case NULL_OBJECT:
				consume (NULL_OBJECT);
				return null;
			case STRING_OBJECT:
				consume (STRING_OBJECT);
				return dataIn.readUTF ();
			case SERIALIZED_OBJECT:
				try
				{
					return objectIn.readObject ();
				}
				catch (ClassNotFoundException e)
				{
					throw new IOWrapException (e);
				}
			default:
				throw new AssertionError ();
		}
	}


	public Shareable readSharedObject () throws IOException
	{
		consume (SHARED_OBJECT_REFERENCE);
		ResolvableReference r
			= cache.readSOProvider (this).readReference (this);
		return r.resolve ();
	}


	public PersistenceCapable readPersistentObject () throws IOException
	{
		consume (PERSISTENT_OBJECT_ID);
		long id = readLong ();
		Object o = manager.getObject (id);
		if (o == null)
		{
			check (PersistenceOutputStream.IO_PERSISTENT_DATA);
			o = ManageableType.read (this, null);
			manager.makePersistentImpl ((PersistenceCapable) o, id, null);
		}
		return (PersistenceCapable) o;
	}


	public long readPersistentObjectId () throws IOException
	{
		return readPersistentObject ().getId ();
	}

	public int beginArray () throws IOException
	{
		consume (ARRAY_OBJECT);
		return readInt ();
	}


	public boolean beginComponent (ArrayComponent c, int index)
	{
		return true;
	}


	public void endComponent ()
	{
	}


	public void endArray ()
	{
	}


	public ManageableType beginManaged () throws IOException
	{
		switch (nextByte ())
		{
			case MANAGEABLE_WITH_TYPE:
				return cache.readType (this);
			case MANAGEABLE_OBJECT:
				return null;
			default:
				throw new AssertionError ();
		}
	}


	public boolean areFieldsProvided ()
	{
		return true;
	}


	public Field beginField (ManageableType type, Field field) throws IOException
	{
		short s = readShort ();
		return (s < 0) ? null : type.getManagedField (s);
	}


	public void endField ()
	{
	}


	public void endManaged ()
	{
	}


	public void setNested (boolean nested)
	{
	}

	public PersistenceCapable resolveId (long inputId)
	{
		return manager.getObject (inputId);
	}
}
