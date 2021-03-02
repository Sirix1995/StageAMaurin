
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

public abstract class RemoteClient
{
	private final ServerConnection server;
	private final short cacheId;


	public RemoteClient (ServerConnection server)
	{
		this.server = server;
		this.cacheId = server.nextCacheId ();
	}

	public final short getCacheId ()
	{
		return cacheId;
	}
	
	public final ServerConnection getServer ()
	{
		return server;
	}

	public abstract BindingsCache getRemoteCache ();

	public abstract void transactionCommitted (Transaction.Data xa,
											   short remoteId, long stamp)
		throws IOException;


	public abstract void localTransactionCommitted (Transaction.Key key,
													long stamp)
		throws IOException;


//	public abstract void localTransactionRolledBack (Transaction.Key key,
//													 long stamp)
//		throws IOException;

}
/*
public class RemoteClient implements Runnable
{
	final PersistenceInputStream in;
	final PersistenceOutputStream out;
	final BindingsCache localCache, remoteCache;
	private final ServerConnection server;


	public RemoteClient (InputStream in, OutputStream out,
						 ServerConnection server) throws IOException
	{
		this.server = server;
		this.out = new PersistenceOutputStream
			(localCache = server.getLocalCache (),
			 new DataOutputStream (out));
		this.in = new PersistenceInputStream
			(remoteCache = new BindingsCache (server.getBindings ()),
			 new DataInputStream (in));
		new Thread (this, toString ()).start ();
	}


	public synchronized void transactionCommitted (Transaction.Data xa,
												   short remoteId, long stamp)
		throws IOException
	{
		out.writeInt (RemoteConnection.COMMIT);
		out.writeShort (remoteId);
		xa.getKey ().write (out);
		out.writeLong (stamp);
		localCache.write (out);
		xa.write (out);
	}


	public synchronized void localTransactionCommitted (Transaction.Key key,
														long stamp)
		throws IOException
	{
		out.writeInt (RemoteConnection.COMMIT_LOCAL);
		out.writeShort (key.getManagerId ());
		key.write (out);
		out.writeLong (stamp);
		localCache.write (out);
	}


	public synchronized void localTransactionRolledBack (Transaction.Key key,
														 long stamp)
		throws IOException
	{
		out.writeInt (RemoteConnection.ROLLBACK_LOCAL);
		out.writeShort (key.getManagerId ());
		key.write (out);
		out.writeLong (stamp);
		localCache.write (out);
	}


	public void run ()
	{
		try
		{
			while (true)
			{
				int action = in.readInt (), i;
				short m;
				switch (action)
				{
					case RemoteConnection.REGISTER:
						i = in.readInt ();
						String key = in.readUTF ();
						StringMap map = server.registerManager (this, key);
						synchronized (this)
						{
							out.writeInt (RemoteConnection.RESULT);
							out.writeInt (i);
							out.write (TypeId.OBJECT);
							out.writeObject (map, null);
						}
						break;
					case RemoteConnection.INITIALIZE:
						i = in.readInt ();
						m = in.readShort ();
						synchronized (this)
						{
							out.writeInt (RemoteConnection.RESULT);
							out.writeInt (i);
							out.write (TypeId.VOID);
							server.initialize (m, out);
						}
						break;
					case RemoteConnection.COMMIT_LOCAL:
						Transaction.Key k = Transaction.Key.read (in);
						long s = in.readLong ();
						remoteCache.read (in);
						Transaction.Data d = new Transaction.Data
							(k, server.getLocalManager (k.getManagerId ()),
							 remoteCache);
						d.read (in);
						server.commit (d, s);
						break;
					default:
						throw new FatalPersistenceException
							("Illegal action id " + action);
				}
			}
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}

}
*/