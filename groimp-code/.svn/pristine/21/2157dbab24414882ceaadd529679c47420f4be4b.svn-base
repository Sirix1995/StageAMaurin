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

import de.grogra.util.StringMap;
import de.grogra.xl.util.ObjectList;

public final class BindingsCache
{
	public static final int STRING = 0;
	public static final int TYPE = 1;
	public static final int SO_PROVIDER = 2;

	private static final int IO_BINDINGS_BEGIN = 0x77;
	private static final int IO_BINDINGS_END = 0x12;

	private final PersistenceBindings bindings;
	private final short cacheId;

	static class SingleCache
	{
		StringMap nameToHandle = new StringMap ();
		ObjectList handleToObject = new ObjectList ();
		ObjectList handleToName = new ObjectList ();
	}

	private final SingleCache[] caches = new SingleCache[] {new SingleCache (),
			new SingleCache (), new SingleCache ()};

	public BindingsCache (PersistenceBindings bindings, short cacheId)
	{
		this.bindings = bindings;
		this.cacheId = cacheId;
	}

	public short getCacheId ()
	{
		return cacheId;
	}

	public PersistenceBindings getBindings ()
	{
		return bindings;
	}

	synchronized void write (PersistenceOutputStream out) throws IOException
	{
		writeUnsync (out);
	}

	private void writeUnsync (PersistenceOutputStream out) throws IOException
	{
		out.write (IO_BINDINGS_BEGIN);
		int[] wh = out.getWrittenHandles (cacheId);
		for (int i = STRING; i <= SO_PROVIDER; i++)
		{
			SingleCache c = caches[i];
			int j = wh[i];
			int n = c.handleToName.size () - j;
			out.writeInt (n);
			if (n > 0)
			{
				out.writeInt (j);
				while (--n >= 0)
				{
					out.writeUTF ((String) c.handleToName.get (j++));
				}
				wh[i] = j;
			}
		}
		out.write (IO_BINDINGS_END);
	}

	void read (PersistenceInputStream in) throws IOException
	{
		in.check (IO_BINDINGS_BEGIN);
		for (int i = STRING; i <= SO_PROVIDER; i++)
		{
			SingleCache c = caches[i];
			int n = in.readInt ();
			if (n > 0)
			{
				int j = c.handleToName.size ();
				in.checkInt (j);
				while (--n >= 0)
				{
					String name = in.readUTF ();
					c.nameToHandle.put (name, Integer.valueOf (j));
					c.handleToName.set (j, name);
					Object object;
					switch (i)
					{
						case STRING:
							object = name;
							break;
						case TYPE:
							object = bindings.resolveType (name);
							break;
						case SO_PROVIDER:
							object = bindings.getSOBinding ().lookup (name);
							break;
						default:
							throw new AssertionError ();
					}
					assert object != null;
					c.handleToObject.set (j, object);
					j++;
				}
			}
		}
		in.check (IO_BINDINGS_END);
	}

	public int getHandle (ManageableType type)
	{
		return getHandle (caches[TYPE], type, type.getBinaryName ());
	}

	public int getHandle (SharedObjectProvider provider)
	{
		return getHandle (caches[SO_PROVIDER], provider, provider
			.getProviderName ());
	}

	private synchronized int getHandle (SingleCache cache, Object object,
			String name)
	{
		Object handle;
		if ((handle = cache.nameToHandle.get (name)) != null)
		{
			return ((Integer) handle).intValue ();
		}
		else
		{
			int i = cache.nameToHandle.size ();
			cache.nameToHandle.put (name, Integer.valueOf (i));
			cache.handleToName.set (i, name);
			cache.handleToObject.set (i, object);
			return i;
		}
	}

	private void getHandleAndWrite (SingleCache cache, Object object,
			String name, PersistenceOutputStream out) throws IOException
	{
		Object handle;
		if ((handle = cache.nameToHandle.get (name)) != null)
		{
			out.writeInt (((Integer) handle).intValue ());
		}
		else
		{
			int i = cache.nameToHandle.size ();
			cache.nameToHandle.put (name, Integer.valueOf (i));
			cache.handleToName.set (i, name);
			cache.handleToObject.set (i, object);
			out.writeInt (-1);
			writeUnsync (out);
			out.writeInt (i);
		}
	}

	public synchronized void write (ManageableType type,
			PersistenceOutputStream out) throws IOException
	{
		getHandleAndWrite (caches[TYPE], type, type.getBinaryName (), out);
	}

	public synchronized void write (SharedObjectProvider provider,
			PersistenceOutputStream out) throws IOException
	{
		getHandleAndWrite (caches[SO_PROVIDER], provider, provider
			.getProviderName (), out);
	}

	public String getString (int handle)
	{
		return (String) caches[STRING].handleToObject.get (handle);
	}

	public ManageableType getType (int handle)
	{
		return (ManageableType) caches[TYPE].handleToObject.get (handle);
	}

	public SharedObjectProvider getSOProvider (int handle)
	{
		return (SharedObjectProvider) caches[SO_PROVIDER].handleToObject
			.get (handle);
	}

	public ManageableType readType (PersistenceInputStream in)
			throws IOException
	{
		return (ManageableType) readHandle (caches[TYPE], in);
	}

	public SharedObjectProvider readSOProvider (PersistenceInputStream in)
			throws IOException
	{
		return (SharedObjectProvider) readHandle (caches[SO_PROVIDER], in);
	}

	private Object readHandle (SingleCache c, PersistenceInputStream in) throws IOException
	{
		int i = in.readInt ();
		if (i == -1)
		{
			read (in);
			i = in.readInt ();
		}
		Object o = c.handleToObject.get (i);
		if (o == null)
		{
			throw new IOException ("No object for handle " + i + " in " + this);
		}
		return o;
	}

}
