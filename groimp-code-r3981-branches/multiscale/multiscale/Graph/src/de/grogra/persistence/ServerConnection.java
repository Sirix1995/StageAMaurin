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

import java.io.*;
import java.util.*;
import de.grogra.xl.util.ObjectList;

public class ServerConnection extends PersistenceConnection
{
	private long nextXAStamp;

	private static final class ClientData
	{
		final Extent extent;
		final RemoteClient client;
		final short managerId;
		boolean initialized = false;

		ClientData (Extent extent, RemoteClient client, short managerId)
		{
			this.extent = extent;
			this.client = client;
			this.managerId = managerId;
		}
	}

	private final class Extent
	{
		final PersistenceManager localManager;
		final ObjectList clients = new ObjectList ();

		Extent (PersistenceManager localManager)
		{
			this.localManager = localManager;
		}

		void commit (Transaction.Data xa, long stamp) throws IOException
		{
			long xaStamp = nextXAStamp++;
			if (xa.isRemote ())
			{
				localManager.transactionCommitted (xa, xaStamp);
			}
			else
			{
				localManager.localTransactionCommitted (xa.key, xaStamp);
			}
			for (int i = 0; i < clients.size (); i++)
			{
				ClientData c = (ClientData) clients.get (i);
				if (c.managerId == xa.getKey ().getManagerId ())
				{
					c.client.localTransactionCommitted (xa.getKey (), xaStamp);
				}
				else if (c.initialized)
				{
					c.client.transactionCommitted (xa, c.managerId, xaStamp);
				}
			}
		}
	}

	private final HashMap extentMap = new HashMap ();
	private final ObjectList clientList = new ObjectList ();

	private short nextManagerId = 0;
	private volatile short nextCacheId = 0;

	public ServerConnection (PersistenceBindings bindings)
	{
		super (bindings);
		initializeCache (new BindingsCache (bindings, nextCacheId ()));
	}

	short nextCacheId ()
	{
		return nextCacheId++;
	}

	@Override
	public synchronized short registerManager (PersistenceManager manager,
			String key)
	{
		Extent e = new Extent (manager);
		extentMap.put (key, e);
		short id = nextManagerId++;
		clientList.set (id, new ClientData (e, null, id));
		return id;
	}

	public synchronized short registerRemoteManager (RemoteClient c, String key)
	{
		Extent e = (Extent) extentMap.get (key);
		short id = nextManagerId++;
		ClientData d = new ClientData (e, c, id);
		clientList.set (id, d);
		e.clients.add (d);
		return id;
	}

	@Override
	public void deregisterManager (PersistenceManager manager)
	{
	}

	public synchronized PersistenceManager initialize (short remoteManagerId)
	{
		Extent e = ((ClientData) clientList.get (remoteManagerId)).extent;
		ObjectList c = e.clients;
		for (int i = c.size () - 1; i >= 0; i--)
		{
			ClientData data = (ClientData) c.get (i);
			if (data.managerId == remoteManagerId)
			{
				assert !data.initialized;
				data.initialized = true;
				return e.localManager;
			}
		}
		throw new AssertionError ();
	}

	public synchronized PersistenceManager getLocalManager (short managerId)
	{
		return ((ClientData) clientList.get (managerId)).extent.localManager;
	}

	public synchronized RemoteClient getClient (short managerId)
	{
		return ((ClientData) clientList.get (managerId)).client;
	}

	@Override
	public synchronized void commit (Transaction.Data xa, long stamp)
	{
		try
		{
			((ClientData) clientList.get (xa.getKey ().getManagerId ())).extent
				.commit (xa, stamp);
		}
		catch (IOException e)
		{
			throw new PersistenceException (e);
		}
	}

}
