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

package de.grogra.imp.net;

import java.io.DataInputStream;
import java.io.IOException;

import de.grogra.persistence.BindingsCache;
import de.grogra.persistence.PersistenceBindings;
import de.grogra.persistence.PersistenceConnection;
import de.grogra.persistence.PersistenceException;
import de.grogra.persistence.PersistenceInputStream;
import de.grogra.persistence.PersistenceManager;
import de.grogra.persistence.PersistenceOutputStream;
import de.grogra.persistence.Transaction;
import de.grogra.xl.util.IntHashMap;

public class ClientConnection extends PersistenceConnection implements
		MessageHandler
{
	static final int REGISTER_ID = 0;
	static final int INITIALIZE_ID = 1;
	static final int COMMIT_LOCAL_ID = 2;
	static final int COMMIT_ID = 3;
	static final int ROLLBACK_LOCAL_ID = 4;

	static final String REGISTER = REGISTER_ID + "persistence" + REGISTER_ID;
	static final String INITIALIZE = INITIALIZE_ID + "persistence"
		+ INITIALIZE_ID;
	static final String COMMIT_LOCAL = COMMIT_LOCAL_ID + "persistence"
		+ COMMIT_LOCAL_ID;
	static final String COMMIT = COMMIT_ID + "persistence" + COMMIT_ID;
	static final String ROLLBACK_LOCAL = ROLLBACK_LOCAL_ID + "persistence"
		+ ROLLBACK_LOCAL_ID;

	static final int CONNECT_MAGIC = 0xd4c3b2a1;

	static final int INITIALIZE_MAGIC = 0xc03311ab;

	final Connection connection;
	PersistenceInputStream pin;
	PersistenceOutputStream pout;
	String name;

	final IntHashMap managers = new IntHashMap ();

	public ClientConnection (final PersistenceBindings bindings,
			Connection connection) throws IOException
	{
		super (bindings);
		this.connection = connection;
		connection.addMessageHandler (this);
		SimpleResponseHandler rh = new SimpleResponseHandler (
			SimpleResponseHandler.USER)
		{
			@Override
			protected void handleUserResponse (Connection cx, long requestId,
					long responseId) throws IOException
			{
				DataInputStream in = ClientConnection.this.connection.getIn ();
				pin = new PersistenceInputStream (bindings, in);
				ClientConnection.this.connection.consumeInt (CONNECT_MAGIC);
				initializeCache (new BindingsCache (bindings, in.readShort ()));
				name = in.readUTF ();
			}
		};
		long msg = connection.beginMessage ("connectremote", rh);
		this.pout = new PersistenceOutputStream (getLocalCache (), connection
			.getOut ());
		this.pout.flush ();
		connection.getOut ().writeInt (CONNECT_MAGIC);
		connection.end (msg);
		rh.waitForResultWrapInterruption ();
	}

	public String getName ()
	{
		return name;
	}

	@Override
	public synchronized void commit (Transaction.Data xa, long stamp)
	{
		try
		{
			long msg = connection.beginMessage (COMMIT_LOCAL, null);
			xa.getKey ().write (connection.getOut ());
			connection.getOut ().writeLong (stamp);
			pout.write (getLocalCache ());
			xa.write (pout);
			pout.flush ();
			connection.end (msg);
		}
		catch (IOException e)
		{
			throw new PersistenceException (e);
		}
	}

	@Override
	public short registerManager (PersistenceManager manager, String key)
	{
		try
		{
			SimpleResponseHandler rh = new SimpleResponseHandler (
				SimpleResponseHandler.INT);
			long msg = connection.beginMessage (REGISTER, rh);
			connection.getOut ().writeUTF (key);
			connection.end (msg);
			short mid = (short) rh.getInt ();
			synchronized (managers)
			{
				managers.put (mid, manager);
			}
			return mid;
		}
		catch (Exception e)
		{
			throw new PersistenceException (e);
		}
	}

	@Override
	public void deregisterManager (PersistenceManager manager)
	{
	}

	public void initialize (final PersistenceManager manager)
			throws IOException
	{
		SimpleResponseHandler rh = new SimpleResponseHandler (
			SimpleResponseHandler.USER)
		{
			@Override
			protected void handleUserResponse (Connection cx, long requestId,
					long responseId) throws IOException
			{
				manager.readExtent (pin);
			}
		};
		long msg = connection.beginMessage (INITIALIZE, rh);
		connection.getOut ().writeShort (manager.getId ());
		connection.end (msg);
		rh.waitForResultWrapInterruption ();
	}

	public boolean handleMessage (Connection cx, long messageId,
			String message, boolean checkAvailability) throws IOException
	{
		int type;
		if (COMMIT_LOCAL.equals (message))
		{
			type = COMMIT_LOCAL_ID;
		}
		else if (COMMIT.equals (message))
		{
			type = COMMIT_ID;
		}
		else if (ROLLBACK_LOCAL.equals (message))
		{
			type = ROLLBACK_LOCAL_ID;
		}
		else
		{
			return false;
		}
		if (checkAvailability)
		{
			return true;
		}
		DataInputStream in = cx.getIn ();
		short mid = in.readShort ();
		PersistenceManager m;
		synchronized (managers)
		{
			m = (PersistenceManager) managers.get (mid);
		}
		Transaction.Key k = Transaction.Key.read (in);
		long s = in.readLong ();
		switch (type)
		{
			case COMMIT_LOCAL_ID:
				m.localTransactionCommitted (k, s);
				break;
			case ROLLBACK_LOCAL_ID:
				m.localTransactionRolledBack (k, s);
				break;
			case COMMIT_ID:
				Transaction.Data d = new Transaction.Data (true, k, pin
					.readCache ());
				d.read (pin);
				m.transactionCommitted (d, s);
				break;
		}
		return true;
	}

}
