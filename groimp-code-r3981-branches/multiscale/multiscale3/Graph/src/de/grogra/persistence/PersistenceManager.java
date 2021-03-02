
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

import java.util.*;
import java.io.IOException;
import de.grogra.util.LockableImpl;
import de.grogra.util.ThreadContext;
import de.grogra.xl.util.IntHashMap;
import de.grogra.xl.util.ObjectList;

public abstract class PersistenceManager extends LockableImpl
{
	private LogStore sentLog = new LogStore (true);
	private LogStore localLog = new LogStore (false);
	private LogStore log = new LogStore (false);
	private long xaStamp = -1;
	private volatile int modificationStamp = 0;


	private static final class PendingEnd
	{
		static final int COMMIT = 0;
		static final int ROLLBACK_LOCAL = 1;
		static final int COMMIT_LOCAL = 2;

		final int type;
		final long stamp;
		final Transaction.Data xa;
		
		PendingEnd (int type, long stamp, Transaction.Data xa)
		{
			this.type = type;
			this.stamp = stamp;
			this.xa = xa;
		}
	}

	private ObjectList<PendingEnd> pendingEnds = new ObjectList<PendingEnd> ();

	private static final int RUNNING = 0;
	private static final int WAITING_FOR_XA_END_0 = 1;
	private static final int WAITING_FOR_XA_END = 2;
	private static final int WAITING_FOR_DEREGISTER = 3;
	private static final int CLOSED = 4;

	private transient int state = RUNNING;


	private void enqueue (int type, long stamp, Transaction.Data xa)
	{
		synchronized (pendingEnds)
		{
			pendingEnds.add (new PendingEnd (type, stamp, xa));
			pendingEnds.notifyAll ();
		}
	}
	

	private int pendingChecked = 0;
	private LogStore.Entry pendingSentLogEntry = null;
	private boolean fetchNewSentLogEntry = true;
	private final IntHashMap<Transaction.Reader> readers = new IntHashMap<Transaction.Reader> ();
	private Runnable xaNotifier;

	private void processPendingEnds (Transaction t)
	{
		assert Thread.holdsLock (pendingEnds);
		if (fetchNewSentLogEntry)
		{
			pendingSentLogEntry = sentLog.getFirstEntry ();
			if (pendingSentLogEntry != null)
			{
				fetchNewSentLogEntry = false;
			}
		}
		if (!pendingEnds.isEmpty ())
		{
			if (pendingSentLogEntry != null)
			{
				while (pendingChecked < pendingEnds.size ())
				{
					PendingEnd pe = pendingEnds.get (pendingChecked++);
					switch (pe.type)
					{
						case PendingEnd.COMMIT_LOCAL:
						case PendingEnd.ROLLBACK_LOCAL:
							Transaction.Key k = pendingSentLogEntry.getKey ();
							if (!k.equals (pe.xa.key))
							{
								throw new FatalPersistenceException
									("Unexpected order of commitment");
							}
							pendingSentLogEntry = pendingSentLogEntry.getNext ();
							break;
					}
				}
			}
			if ((activeTransactions == 0) && (pendingSentLogEntry == null))
			{
				ThreadContext tc = ThreadContext.current ();
				try
				{
					t.beginApply ();
					
					Transaction.Reader reader = readers.get (tc.getId ());
					if (reader == null)
					{
						reader = createTransaction (tc.getThread ()).createReader ();
						readers.put (tc.getId (), reader);
					}
					Transaction xa = reader.getTransaction ();

					PendingEnd pe = null;
					for (int i = pendingEnds.size () - 1; i >= 0; i--)
					{
						pe = pendingEnds.get (i);
						if (pe.type == PendingEnd.ROLLBACK_LOCAL)
						{
							xa.restore (pe.xa);
							reader.resetCursor ();
							reader.supplyInverse (t.xaApplier);
							transactionApplied (pe.xa, true, t);
						}
					}
					for (int i = 0; i < pendingEnds.size (); i++)
					{
						pe = pendingEnds.get (i);
						switch (pe.type)
						{
							case PendingEnd.COMMIT_LOCAL:
								localLog.add (pe.xa.key, pe.xa);
								log.add (pe.xa.key, pe.xa);
								break;
							case PendingEnd.COMMIT:
								xa.restore (pe.xa);
								reader.resetCursor ();
								reader.supply (t.xaApplier);
								transactionApplied (pe.xa, false, t);
								log.add (pe.xa.key, pe.xa);
								break;
						}
					}
					xaStamp = pe.stamp;
					pendingChecked = 0;
					fetchNewSentLogEntry = true;
					pendingSentLogEntry = null;
					sentLog.clear ();
					pendingEnds.clear ();
					pendingEnds.notifyAll ();
				}
				catch (IOException e)
				{
					exceptionThrown (e);
				}
				finally
				{
					t.endApply ();
				}
			}
		}
	}


	final short id;
	private long nextId;

	Transaction[] transactions = new Transaction[32];
	final Object xaLock = new Object ();
	long nextXAId = 0;
	final PersistenceConnection connection;
	int activeTransactions = 0;
	
	final boolean checkLock;


	public PersistenceManager (PersistenceConnection cx, String key, boolean checkLock)
	{
		connection = cx;
		this.checkLock  = checkLock;
		this.id = cx.registerManager (this, key);
		nextId = (long) this.id << 48;
	}

	
	public void initNonlocalTransactionNotifier (Runnable notifier)
	{
		xaNotifier = notifier;
	}

	public void close ()
	{
		synchronized (xaLock)
		{
			state = WAITING_FOR_XA_END_0;
		}
		synchronized (pendingEnds)
		{
			state = WAITING_FOR_XA_END;
			while (activeTransactions > 0)
			{
				try
				{
					pendingEnds.wait ();
				}
				catch (InterruptedException e)
				{
				}
			}
			state = WAITING_FOR_DEREGISTER;
		}
		connection.deregisterManager (this);
		synchronized (pendingEnds)
		{
			state = CLOSED;
			pendingEnds.notifyAll ();
		}
	}


	void exceptionThrown (Throwable t)
	{
		t.printStackTrace ();
	}


	protected abstract TransactionApplier createXAApplier ();


	protected abstract Transaction createTransaction (Thread thread);


	public final void getTransactions (List<? super Transaction> list)
	{
		list.clear ();
		int i = 0;
		Transaction t;
		synchronized (xaLock)
		{
			while ((t = transactions[i++]) != null)
			{
				list.add (t);
			}
		}
	}

	public final Transaction getActiveTransaction ()
	{
		Transaction t = getTransaction (true);
		if (!t.isActive ())
		{
			t.begin (false);
		}
		return t;
	}

	public final Transaction getTransaction (boolean create)
	{
		Thread thread = Thread.currentThread ();
		int i = 0;
		Transaction t;
		synchronized (xaLock)
		{
			while ((t = transactions[i++]) != null)
			{
				if (t.thread == thread)
				{
					return t;
				}
			}
			if (create)
			{
				if (state != RUNNING)
				{
					throw new PersistenceException ("Already closing");
				}
				t = createTransaction (thread);
				transactions = t.add (transactions);
				return t;
			}
			return null;
		}
	}


	protected void beginTransaction (Transaction t)
	{
		synchronized (pendingEnds)
		{
			processPendingEnds (t);
			while (!pendingEnds.isEmpty ())
			{
				try
				{
					pendingEnds.wait ();
				}
				catch (InterruptedException e)
				{
				}
				processPendingEnds (t);
			}
			if (state >= WAITING_FOR_XA_END)
			{
				throw new PersistenceException ("Already closing");
			}
			activeTransactions++;
			t.id = t.readOnly ? 0 : nextXAId++;
		}
	}

	
	protected void prepareCompletion (Transaction t, boolean commit)
	{
	}


	protected void completeTransaction (Transaction t, boolean commit)
	{
		Transaction.Data d = null;
		synchronized (pendingEnds)
		{
			activeTransactions--;
			if (commit)
			{
				if (t.hasModified ())
				{
					d = (Transaction.Data) t.cloneData ();
					sentLog.add (d.key, d);
					connection.commit (d, xaStamp);
				}
			}
			processPendingEnds (t);
			pendingEnds.notifyAll ();
		}
		if (d != null)
		{
			transactionApplied (d, false, t);
		}
	}


	public final void transactionCommitted (Transaction.Data xa, long stamp)
	{
		enqueue (PendingEnd.COMMIT, stamp, xa);
		if (xaNotifier != null)
		{
			xaNotifier.run ();
		}
	}


	public final void localTransactionCommitted (Transaction.Key key, long stamp)
	{
		enqueue (PendingEnd.COMMIT_LOCAL, stamp, sentLog.get (key));
	}


	public final void localTransactionRolledBack (Transaction.Key key, long stamp)
	{
		enqueue (PendingEnd.ROLLBACK_LOCAL, stamp, sentLog.get (key));
	}


	public final Transaction.Data getTransactionData (Transaction.Key key)
	{
		return log.get (key);
	}


	public final LogStore getLocalLog ()
	{
		return localLog;
	}


	public final PersistenceConnection getConnection ()
	{
		return connection;
	}

	public final LogStore getLog ()
	{
		return log;
	}
	
	public final PersistenceBindings getBindings ()
	{
		return connection.getBindings ();
	}

/*
	public boolean isActive (XAThreadState thread)
	{
		return false;
	}
*/

	public final short getId ()
	{
		return id;
	}


	private final ObjectList<XAListener> listeners = new ObjectList<XAListener> (10, false);

	public final void addXAListener (XAListener l)
	{
		synchronized (listeners)
		{
			listeners.addIfNotContained (l);
		}
	}


	public final void removeXAListener (XAListener l)
	{
		synchronized (listeners)
		{
			listeners.remove (l);
		}
	}


	public int getStamp ()
	{
		return modificationStamp;
	}


	protected void transactionApplied (Transaction.Data xa, boolean rollback, Transaction t)
	{
		modificationStamp++;
		XAListener l0 = null, l1 = null, l2 = null, l3 = null;
		XAListener[] array = null;
		Object[] elements;
		int n;
		synchronized (listeners)
		{
			n = listeners.size ();
			elements = listeners.elements;
			switch (n)
			{
				default:
					array = new XAListener[n - 4];
					System.arraycopy (elements, 4, array, 0, n - 4);
				case 4:
					l3 = (XAListener) elements[3];
				case 3:
					l2 = (XAListener) elements[2];
				case 2:
					l1 = (XAListener) elements[1];
				case 1:
					l0 = (XAListener) elements[0];
				case 0:
			}
		}
		if (l0 != null)
		{
			l0.transactionApplied (xa, rollback);
			if (l1 != null)
			{
				l1.transactionApplied (xa, rollback);
				if (l2 != null)
				{
					l2.transactionApplied (xa, rollback);
					if (l3 != null)
					{
						l3.transactionApplied (xa, rollback);
						if (array != null)
						{
							for (int i = 0; i < n - 4; i++)
							{
								array[i].transactionApplied (xa, rollback);
							}
						}
					}
				}
			}
		}
	}

	public final void makePersistent (Object o, Transaction t)
	{
		if (o instanceof PersistenceCapable)
		{
			makePersistent ((PersistenceCapable) o, -1L, t);
		}
		else if (o instanceof Object[])
		{
			Object[] a = (Object[]) o;
			for (int i = a.length - 1; i >= 0; i--)
			{
				makePersistent (a[i], t);
			}
		}
		else if (o instanceof List)
		{
			List l = (List) o;
			if (o instanceof RandomAccess)
			{
				for (int i = l.size () - 1; i >= 0; i--)
				{
					makePersistent (l.get (i), t);
				}
			}
			else
			{
				for (Iterator i = l.iterator (); i.hasNext (); )
				{
					makePersistent (i.next (), t);
				}
			}
		}
	}


	public abstract long prepareId (PersistenceCapable pc);

	protected final Object makeLock = new Object ();

	public final void makePersistent (PersistenceCapable o, long id, Transaction t)
	{
		PersistenceManager m = o.getPersistenceManager ();
		if (m != null)
		{
			if (m != this)
			{
				throw new UserPersistenceException
					("The persistence manager has already been set to another "
					 + "manager");
			}
		}
		else
		{
			synchronized (makeLock)
			{
				makePersistentImpl (o, id, t);
			}
		}
	}


	public final void makeTransient (PersistenceCapable o, Transaction t)
	{
		if (o.getPersistenceManager () != this)
		{
			throw new UserPersistenceException
				("The persistence manager has not been set to this "
				 + "persistence manager for " + o);
		}
		synchronized (makeLock)
		{
			makeTransientImpl (o, t);
		}
	}


	protected final long nextId ()
	{
		return nextId++;
	}


	protected final void idUsed (long id)
	{
		if (((int) (id >>> 48) == this.id) && (nextId <= id))
		{
			nextId = id + 1;
		}
	}


	protected abstract void makePersistentImpl
		(PersistenceCapable o, long id, Transaction t);


	protected abstract void makeTransientImpl
		(PersistenceCapable o, Transaction t);


	public abstract PersistenceCapable getObject (long id);


	public abstract void writeExtent (PersistenceOutputStream out) throws IOException;


	public abstract void readExtent (PersistenceInputStream in)
		throws IOException;


	public abstract int allocateBitMark (boolean resetOnDispose);


	public abstract void disposeBitMark (int handle, boolean resetAll);


	public abstract int allocateObjectMark (boolean resetOnDispose);


	public abstract void disposeObjectMark (int handle, boolean resetAll);

	public boolean undo (Transaction t) throws IOException
	{
		LogStore.Entry e = log.getLastEntry ();
		if (e == null)
			return false;
		Transaction.Data d = (Transaction.Data) getTransactionData (e.getKey ());
		if (d == null)
			return false;
		t.undo (d);
		return true;
	}
}
