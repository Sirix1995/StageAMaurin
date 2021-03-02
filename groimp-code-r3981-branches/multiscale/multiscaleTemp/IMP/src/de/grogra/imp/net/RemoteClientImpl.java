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

import java.io.DataOutputStream;
import java.io.IOException;

import de.grogra.graph.impl.GraphManager;
import de.grogra.persistence.BindingsCache;
import de.grogra.persistence.PersistenceInputStream;
import de.grogra.persistence.PersistenceManager;
import de.grogra.persistence.PersistenceOutputStream;
import de.grogra.persistence.RemoteClient;
import de.grogra.persistence.ServerConnection;
import de.grogra.persistence.Transaction;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;

public class RemoteClientImpl extends RemoteClient implements Command,
		MessageHandler
{
	private final Connection connection;
	private final PersistenceInputStream pin;
	private final PersistenceOutputStream pout;
	private final Workbench workbench;
	private final BindingsCache remoteCache;

	public RemoteClientImpl (Workbench workbench, Connection connection,
			long requestId) throws IOException
	{
		super ((ServerConnection) workbench.getRegistry ().getProjectGraph ()
			.getConnection ());
		connection.addMessageHandler (this);
		this.workbench = workbench;
		this.connection = connection;
		this.pin = new PersistenceInputStream (getServer ().getBindings (),
			connection.getIn ());
		remoteCache = pin.getCache (getCacheId ());
		connection.consumeInt (ClientConnection.CONNECT_MAGIC);
		long msg = connection.beginResponse (requestId, null);
		DataOutputStream out = connection.getOut ();
		this.pout = new PersistenceOutputStream (getServer ().getLocalCache (),
			out);
		this.pout.flush ();
		out.writeInt (ClientConnection.CONNECT_MAGIC);
		out.writeShort (getCacheId ());
		out.writeUTF (workbench.getName ());
		connection.end (msg);
	}

	@Override
	public BindingsCache getRemoteCache ()
	{
		return remoteCache;
	}

	@Override
	public synchronized void transactionCommitted (Transaction.Data xa,
			short remoteId, long stamp) throws IOException
	{
		long msg = connection.beginMessage (ClientConnection.COMMIT, null);
		DataOutputStream out = connection.getOut ();
		out.writeShort (remoteId);
		xa.getKey ().write (out);
		out.writeLong (stamp);
		pout.write (xa.isRemote () ? getServer ().getClient (
			xa.getKey ().getManagerId ()).getRemoteCache () : getServer ()
			.getLocalCache ());
		xa.write (pout);
		pout.flush ();
		connection.end (msg);
	}

	@Override
	public synchronized void localTransactionCommitted (Transaction.Key key,
			long stamp) throws IOException
	{
		long msg = connection
			.beginMessage (ClientConnection.COMMIT_LOCAL, null);
		DataOutputStream out = connection.getOut ();
		out.writeShort (key.getManagerId ());
		key.write (out);
		out.writeLong (stamp);
		connection.end (msg);
	}

	public boolean handleMessage (Connection cx, long messageId,
			String message, boolean checkAvailability) throws IOException
	{
		int type;
		Object arg;
		long larg = 0;
		short manager = 0;
		if (ClientConnection.REGISTER.equals (message))
		{
			if (checkAvailability)
			{
				return true;
			}
			short remoteId = getServer ().registerRemoteManager (this,
				cx.getIn ().readUTF ());
			long msg = cx.beginResponse (messageId, null);
			cx.getOut ().writeInt (remoteId);
			cx.end (msg);
			return true;
		}
		else if (ClientConnection.INITIALIZE.equals (message))
		{
			if (checkAvailability)
			{
				return true;
			}
			type = ClientConnection.INITIALIZE_ID;
			manager = connection.getIn ().readShort ();
			arg = new Short (manager);
		}
		else if (ClientConnection.COMMIT_LOCAL.equals (message))
		{
			if (checkAvailability)
			{
				return true;
			}
			type = ClientConnection.COMMIT_LOCAL_ID;
			Transaction.Key k = Transaction.Key.read (connection.getIn ());
			larg = connection.getIn ().readLong ();
			Transaction.Data d = new Transaction.Data (true, k, pin
				.readCache ());
			d.read (pin);
			arg = d;
		}
		else
		{
			return false;
		}
		Object[] args = {new long[] {messageId, type, larg}, arg};
		if (type == ClientConnection.INITIALIZE_ID)
		{
			UI.executeLockedly (getServer ().getLocalManager (manager), false,
				this, args, workbench, JobManager.ACTION_FLAGS);
		}
		else
		{
			workbench.getJobManager ().runLater (this, args, workbench,
				JobManager.ACTION_FLAGS);
		}
		return true;
	}

	public void run (Object info, Context ctx)
	{
		try
		{
			Object[] args = (Object[]) info;
			long[] ids = (long[]) args[0];
			switch ((int) ids[1])
			{
				case ClientConnection.INITIALIZE_ID:
					short m = ((Short) args[1]).shortValue ();
					PersistenceManager mgr = getServer ().initialize (m);
					long msg = connection.beginResponse (ids[0], null);
					Transaction t = mgr.getTransaction (true);
					t.begin (true);
					try
					{
						mgr.writeExtent (pout);
					}
					finally
					{
						t.commit ();
					}
					pout.flush ();
					connection.end (msg);
					break;
				case ClientConnection.COMMIT_LOCAL_ID:
					long s = ids[2];
					Transaction.Data d = (Transaction.Data) args[1];
					getServer ().commit (d, s);
					break;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	public String getCommandName ()
	{
		return null;
	}

}
