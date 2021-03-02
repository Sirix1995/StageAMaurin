
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
import java.io.DataOutput;
import java.io.IOException;

import de.grogra.reflect.TypeId;
import de.grogra.util.HierarchicalQueue;
import de.grogra.xl.util.ObjectList;

public abstract class Transaction extends XAQueue
{
	Thread thread;

	long id = -1;

	private int activeCount = 0;
	private long userId = 0;
	private int readOnlyBegin = -1;
	boolean readOnly = false;
	private boolean modified = false;

	private boolean applyXA = false;
	final TransactionApplier xaApplier;

	private final ObjectList<Object> modifiedFields = new ObjectList<Object> ();

	public static final class Key
	{
		private final short mid;
		private final long xid, userId;


		Key (short mid, long xid, long userId)
		{
			this.mid = mid;
			this.xid = xid;
			this.userId = userId;
		}


		Key (short mid, long xid)
		{
			this (mid, xid, 0);
		}


		public boolean equals (short mid, long xid)
		{
			return (this.mid == mid) && (this.xid == xid);
		}


		@Override
		public boolean equals (Object o)
		{
			return (o instanceof Key)
				&& (((Key) o).mid == mid) && (((Key) o).xid == xid);
		}


		@Override
		public int hashCode ()
		{
			return mid ^ (int) xid;
		}


		public boolean hasUserId (PersistenceManager manager, long userId)
		{
			return (mid == manager.getId ()) && (this.userId == userId);
		}


		public short getManagerId ()
		{
			return mid;
		}


		public long getUserId ()
		{
			return userId;
		}


		public long getXAId ()
		{
			return xid;
		}


		public void write (DataOutput out) throws IOException
		{
			out.writeShort (mid);
			out.writeLong (xid);
		}


		public static Key read (DataInput in) throws IOException
		{
			return new Key (in.readShort (), in.readLong ());
		}


		@Override
		public String toString ()
		{
			return "Transaction.Key[manager=" + mid + ", xa=" + xid
				+ ", user=" + userId + ']';
		}
	}


	public static final class Data extends XAQueue.Data
	{
		final Key key;
		final BindingsCache cache;


		public Data (boolean remote, Key key, BindingsCache cache)
		{
			super (remote);
			this.key = key;
			this.cache = cache;
		}


		public Key getKey ()
		{
			return key;
		}


		public boolean hasUserId (PersistenceManager manager, long userId)
		{
			return key.hasUserId (manager, userId);
		}


		public long getUserId ()
		{
			return key.getUserId ();
		}
	}


	@Override
	protected Data createData ()
	{
		return new Data (false, getKey (), manager.getConnection ().getLocalCache ());
	}


	public interface Consumer
	{
		void setField (PersistenceCapable o, PersistenceField field,
					   int[] indices, Transaction.Reader reader);

		void insertComponent (PersistenceCapable o, PersistenceField field,
							  int[] indices, Transaction.Reader reader);

		void removeComponent (PersistenceCapable o, PersistenceField field,
							  int[] indices, Transaction.Reader reader);

		void makePersistent (long id, ManageableType type);

		void makeTransient (PersistenceCapable o, ManageableType type);

		void readData (PersistenceCapable o, Transaction.Reader reader);

		void begin ();

		void end ();


		class Adapter implements Consumer
		{
			public void setField (PersistenceCapable o, PersistenceField field,
								  int[] indices, Transaction.Reader reader)
			{
			}

			public void insertComponent (PersistenceCapable o, PersistenceField field,
					  					 int[] indices, Transaction.Reader reader)
			{
			}

			public void removeComponent (PersistenceCapable o, PersistenceField field,
					  					 int[] indices, Transaction.Reader reader)
			{
			}

			public void makePersistent (long id, ManageableType type)
			{
			}

			public void makeTransient (PersistenceCapable o, ManageableType type)
			{
			}

			public void readData (PersistenceCapable o, Transaction.Reader reader)
			{
			}

			public void begin ()
			{
			}

			public void end ()
			{
			}
		}


		class Multicaster implements Consumer
		{
			private ObjectList consumers = new ObjectList (10, false);
			private volatile boolean dirty = true;
			private ObjectList consumersCopy = new ObjectList (10);


			public synchronized void addConsumer (Object c)
			{
				consumers.addIfNotContained (c);
				dirty = true;
			}


			public synchronized void removeConsumer (Object c)
			{
				consumers.remove (c);
				dirty = true;
			}


			public synchronized void removeAllConsumers ()
			{
				consumers.clear ();
				dirty = true;
			}


			protected ObjectList getConsumers ()
			{
				if (dirty)
				{
					consumersCopy.clear ();
					synchronized (this)
					{
						consumersCopy.addAll (consumers);
						dirty = false;
					}
				}
				return consumersCopy;
			}


			public void setField (PersistenceCapable o, PersistenceField field,
								  int[] indices, Transaction.Reader reader)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = cs.get (i)) instanceof Consumer)
					{
						reader.pushCursor ();
						((Consumer) c).setField (o, field, indices, reader);
						reader.popCursor ();
					}
				}
			}


			public void insertComponent (PersistenceCapable o, PersistenceField field,
										 int[] indices, Transaction.Reader reader)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = cs.get (i)) instanceof Consumer)
					{
						reader.pushCursor ();
						((Consumer) c).insertComponent (o, field, indices, reader);
						reader.popCursor ();
					}
				}
			}


			public void removeComponent (PersistenceCapable o, PersistenceField field,
										 int[] indices, Transaction.Reader reader)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = cs.get (i)) instanceof Consumer)
					{
						reader.pushCursor ();
						((Consumer) c).removeComponent (o, field, indices, reader);
						reader.popCursor ();
					}
				}
			}


			public void makePersistent (long id, ManageableType type)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = cs.get (i)) instanceof Consumer)
					{
						((Consumer) c).makePersistent (id, type);
					}
				}
			}


			public void makeTransient (PersistenceCapable o, ManageableType type)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = cs.get (i)) instanceof Consumer)
					{
						((Consumer) c).makeTransient (o, type);
					}
				}
			}


			public void readData (PersistenceCapable o, Transaction.Reader reader)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = cs.get (i)) instanceof Consumer)
					{
						reader.pushCursor ();
						((Consumer) c).readData (o, reader);
						reader.popCursor ();
					}
				}
			}


			public void begin ()
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = consumers.get (i)) instanceof Consumer)
					{
						((Consumer) c).begin ();
					}
				}
			}


			public void end ()
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object c;
					if ((c = consumers.get (i)) instanceof Consumer)
					{
						((Consumer) c).end ();
					}
				}
			}
		}
	}


	public interface ExtendedConsumer extends Consumer
	{
		void setField (PersistenceCapable o, PersistenceField field,
					   int[] indices, Transaction.Reader reader, boolean inverse);
	}


	protected boolean isLoggingSuppressed ()
	{
		return false;
	}

/*!!
#set ($i = 0)
#foreach ($type in $types)
$pp.setType($type)

	public static final int SET_$pp.TYPE = XAQueue.MIN_UNUSED_ITEM
		+ $i;

	public void logSet$pp.Type (PersistenceCapable o, PersistenceField field,
								int[] indices, $type oldValue, $type newValue)
	{
		prepareLogSet$pp.Type (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
#if ($pp.Object)
		try
		{
			field.writeObject (newValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
#else
		write$pp.Type (newValue);
#end
	}

	public void prepareLogSet$pp.Type
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, $type oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
#if ($pp.Object)
		try
		{
			field.writeObject (oldValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
#else
		write$pp.Type (oldValue);
#end
	}

#set ($i = $i + 1)

	public static final int INSERT_$pp.TYPE = XAQueue.MIN_UNUSED_ITEM
		+ $i;

	public void logInsert$pp.Type (PersistenceCapable o, PersistenceField field,
								   int[] indices, $type newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
#if ($pp.Object)
		try
		{
			field.writeObject (newValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
#else
		write$pp.Type (newValue);
#end
	}

#set ($i = $i + 1)

	public static final int REMOVE_$pp.TYPE = XAQueue.MIN_UNUSED_ITEM
		+ $i;

	public void logRemove$pp.Type (PersistenceCapable o, PersistenceField field,
								   int[] indices, $type oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_$pp.TYPE);
		writeLong (o.getId ());
		field.write (indices, this);
#if ($pp.Object)
		try
		{
			field.writeObject (oldValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
#else
		write$pp.Type (oldValue);
#end
	}

#set ($i = $i + 1)

#end

	public static final int UNDO_TRANSACTION = XAQueue.MIN_UNUSED_ITEM
		+ $i;

!!*/
//!! #* Start of generated code
// generated
// generated
	public static final int SET_BOOLEAN = XAQueue.MIN_UNUSED_ITEM
		+ 0;
// generated
	public void logSetBoolean (PersistenceCapable o, PersistenceField field,
								int[] indices, boolean oldValue, boolean newValue)
	{
		prepareLogSetBoolean (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeBoolean (newValue);
	}
// generated
	public void prepareLogSetBoolean
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, boolean oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (oldValue);
	}
// generated
// generated
	public static final int INSERT_BOOLEAN = XAQueue.MIN_UNUSED_ITEM
		+ 1;
// generated
	public void logInsertBoolean (PersistenceCapable o, PersistenceField field,
								   int[] indices, boolean newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (newValue);
	}
// generated
// generated
	public static final int REMOVE_BOOLEAN = XAQueue.MIN_UNUSED_ITEM
		+ 2;
// generated
	public void logRemoveBoolean (PersistenceCapable o, PersistenceField field,
								   int[] indices, boolean oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_BOOLEAN);
		writeLong (o.getId ());
		field.write (indices, this);
		writeBoolean (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_BYTE = XAQueue.MIN_UNUSED_ITEM
		+ 3;
// generated
	public void logSetByte (PersistenceCapable o, PersistenceField field,
								int[] indices, byte oldValue, byte newValue)
	{
		prepareLogSetByte (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeByte (newValue);
	}
// generated
	public void prepareLogSetByte
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, byte oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (oldValue);
	}
// generated
// generated
	public static final int INSERT_BYTE = XAQueue.MIN_UNUSED_ITEM
		+ 4;
// generated
	public void logInsertByte (PersistenceCapable o, PersistenceField field,
								   int[] indices, byte newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (newValue);
	}
// generated
// generated
	public static final int REMOVE_BYTE = XAQueue.MIN_UNUSED_ITEM
		+ 5;
// generated
	public void logRemoveByte (PersistenceCapable o, PersistenceField field,
								   int[] indices, byte oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_BYTE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeByte (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_SHORT = XAQueue.MIN_UNUSED_ITEM
		+ 6;
// generated
	public void logSetShort (PersistenceCapable o, PersistenceField field,
								int[] indices, short oldValue, short newValue)
	{
		prepareLogSetShort (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeShort (newValue);
	}
// generated
	public void prepareLogSetShort
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, short oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (oldValue);
	}
// generated
// generated
	public static final int INSERT_SHORT = XAQueue.MIN_UNUSED_ITEM
		+ 7;
// generated
	public void logInsertShort (PersistenceCapable o, PersistenceField field,
								   int[] indices, short newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (newValue);
	}
// generated
// generated
	public static final int REMOVE_SHORT = XAQueue.MIN_UNUSED_ITEM
		+ 8;
// generated
	public void logRemoveShort (PersistenceCapable o, PersistenceField field,
								   int[] indices, short oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_SHORT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeShort (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_CHAR = XAQueue.MIN_UNUSED_ITEM
		+ 9;
// generated
	public void logSetChar (PersistenceCapable o, PersistenceField field,
								int[] indices, char oldValue, char newValue)
	{
		prepareLogSetChar (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeChar (newValue);
	}
// generated
	public void prepareLogSetChar
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, char oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (oldValue);
	}
// generated
// generated
	public static final int INSERT_CHAR = XAQueue.MIN_UNUSED_ITEM
		+ 10;
// generated
	public void logInsertChar (PersistenceCapable o, PersistenceField field,
								   int[] indices, char newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (newValue);
	}
// generated
// generated
	public static final int REMOVE_CHAR = XAQueue.MIN_UNUSED_ITEM
		+ 11;
// generated
	public void logRemoveChar (PersistenceCapable o, PersistenceField field,
								   int[] indices, char oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_CHAR);
		writeLong (o.getId ());
		field.write (indices, this);
		writeChar (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_INT = XAQueue.MIN_UNUSED_ITEM
		+ 12;
// generated
	public void logSetInt (PersistenceCapable o, PersistenceField field,
								int[] indices, int oldValue, int newValue)
	{
		prepareLogSetInt (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeInt (newValue);
	}
// generated
	public void prepareLogSetInt
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, int oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (oldValue);
	}
// generated
// generated
	public static final int INSERT_INT = XAQueue.MIN_UNUSED_ITEM
		+ 13;
// generated
	public void logInsertInt (PersistenceCapable o, PersistenceField field,
								   int[] indices, int newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (newValue);
	}
// generated
// generated
	public static final int REMOVE_INT = XAQueue.MIN_UNUSED_ITEM
		+ 14;
// generated
	public void logRemoveInt (PersistenceCapable o, PersistenceField field,
								   int[] indices, int oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_INT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeInt (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_LONG = XAQueue.MIN_UNUSED_ITEM
		+ 15;
// generated
	public void logSetLong (PersistenceCapable o, PersistenceField field,
								int[] indices, long oldValue, long newValue)
	{
		prepareLogSetLong (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeLong (newValue);
	}
// generated
	public void prepareLogSetLong
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, long oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (oldValue);
	}
// generated
// generated
	public static final int INSERT_LONG = XAQueue.MIN_UNUSED_ITEM
		+ 16;
// generated
	public void logInsertLong (PersistenceCapable o, PersistenceField field,
								   int[] indices, long newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (newValue);
	}
// generated
// generated
	public static final int REMOVE_LONG = XAQueue.MIN_UNUSED_ITEM
		+ 17;
// generated
	public void logRemoveLong (PersistenceCapable o, PersistenceField field,
								   int[] indices, long oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_LONG);
		writeLong (o.getId ());
		field.write (indices, this);
		writeLong (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_FLOAT = XAQueue.MIN_UNUSED_ITEM
		+ 18;
// generated
	public void logSetFloat (PersistenceCapable o, PersistenceField field,
								int[] indices, float oldValue, float newValue)
	{
		prepareLogSetFloat (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeFloat (newValue);
	}
// generated
	public void prepareLogSetFloat
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, float oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (oldValue);
	}
// generated
// generated
	public static final int INSERT_FLOAT = XAQueue.MIN_UNUSED_ITEM
		+ 19;
// generated
	public void logInsertFloat (PersistenceCapable o, PersistenceField field,
								   int[] indices, float newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (newValue);
	}
// generated
// generated
	public static final int REMOVE_FLOAT = XAQueue.MIN_UNUSED_ITEM
		+ 20;
// generated
	public void logRemoveFloat (PersistenceCapable o, PersistenceField field,
								   int[] indices, float oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_FLOAT);
		writeLong (o.getId ());
		field.write (indices, this);
		writeFloat (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_DOUBLE = XAQueue.MIN_UNUSED_ITEM
		+ 21;
// generated
	public void logSetDouble (PersistenceCapable o, PersistenceField field,
								int[] indices, double oldValue, double newValue)
	{
		prepareLogSetDouble (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeDouble (newValue);
	}
// generated
	public void prepareLogSetDouble
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, double oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (oldValue);
	}
// generated
// generated
	public static final int INSERT_DOUBLE = XAQueue.MIN_UNUSED_ITEM
		+ 22;
// generated
	public void logInsertDouble (PersistenceCapable o, PersistenceField field,
								   int[] indices, double newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (newValue);
	}
// generated
// generated
	public static final int REMOVE_DOUBLE = XAQueue.MIN_UNUSED_ITEM
		+ 23;
// generated
	public void logRemoveDouble (PersistenceCapable o, PersistenceField field,
								   int[] indices, double oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_DOUBLE);
		writeLong (o.getId ());
		field.write (indices, this);
		writeDouble (oldValue);
	}
// generated
// generated
// generated
// generated
	public static final int SET_OBJECT = XAQueue.MIN_UNUSED_ITEM
		+ 24;
// generated
	public void logSetObject (PersistenceCapable o, PersistenceField field,
								int[] indices, Object oldValue, Object newValue)
	{
		prepareLogSetObject (o, field, indices, oldValue);
		if (isLoggingSuppressed ())
		{
			return;
		}
		try
		{
			field.writeObject (newValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}
// generated
	public void prepareLogSetObject
		(PersistenceCapable o, PersistenceField field,
		 int[] indices, Object oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (SET_OBJECT);
		writeLong (o.getId ());
		field.write (indices, this);
		try
		{
			field.writeObject (oldValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}
// generated
// generated
	public static final int INSERT_OBJECT = XAQueue.MIN_UNUSED_ITEM
		+ 25;
// generated
	public void logInsertObject (PersistenceCapable o, PersistenceField field,
								   int[] indices, Object newValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (INSERT_OBJECT);
		writeLong (o.getId ());
		field.write (indices, this);
		try
		{
			field.writeObject (newValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}
// generated
// generated
	public static final int REMOVE_OBJECT = XAQueue.MIN_UNUSED_ITEM
		+ 26;
// generated
	public void logRemoveObject (PersistenceCapable o, PersistenceField field,
								   int[] indices, Object oldValue)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_OBJECT);
		writeLong (o.getId ());
		field.write (indices, this);
		try
		{
			field.writeObject (oldValue, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}
// generated
// generated
// generated
	public static final int UNDO_TRANSACTION = XAQueue.MIN_UNUSED_ITEM
		+ 27;
// generated
//!! *# End of generated code


	public void logUndo (Transaction t)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (UNDO_TRANSACTION);
		writeShort (t.manager.getId ());
		writeLong (t.id);
	}


	public static final int MAKE_PERSISTENT = UNDO_TRANSACTION + 1;

	public void logMakePersistent (PersistenceCapable o)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (MAKE_PERSISTENT);
		writeLong (o.getId ());
		writeType (o.getManageableType ());
	}


	public static final int MAKE_TRANSIENT = MAKE_PERSISTENT + 1;

	public void logMakeTransient (PersistenceCapable o)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (MAKE_TRANSIENT);
		writeLong (o.getId ());
		writeType (o.getManageableType ());
	}


	public static final int READ_DATA = MAKE_TRANSIENT + 1;

	public void logReadData (PersistenceCapable o, boolean onRollback)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (READ_DATA);
		writeLong (o.getId ());
		writeBoolean (onRollback);
		boolean diff = beginManaged (o.getManageableType (), true);
		try
		{
			o.getManageableType ().write (o, this, diff);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		endManaged (o, true);
	}


	public static final int MIN_UNUSED_ITEM = READ_DATA + 1;


	public class Reader extends XAQueue.Reader
	{
		protected final IndirectField indirectField = new IndirectField ();

		
		public Transaction getTransaction ()
		{
			return Transaction.this;
		}

/*!!
#foreach ($type in $primitives)
$pp.setType($type)

		public final $type skipAndRead$pp.Type () throws IOException
		{
			skip$pp.Type ();
			return read$pp.Type ();
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		public final boolean skipAndReadBoolean () throws IOException
		{
			skipBoolean ();
			return readBoolean ();
		}
// generated
// generated
		public final byte skipAndReadByte () throws IOException
		{
			skipByte ();
			return readByte ();
		}
// generated
// generated
		public final short skipAndReadShort () throws IOException
		{
			skipShort ();
			return readShort ();
		}
// generated
// generated
		public final char skipAndReadChar () throws IOException
		{
			skipChar ();
			return readChar ();
		}
// generated
// generated
		public final int skipAndReadInt () throws IOException
		{
			skipInt ();
			return readInt ();
		}
// generated
// generated
		public final long skipAndReadLong () throws IOException
		{
			skipLong ();
			return readLong ();
		}
// generated
// generated
		public final float skipAndReadFloat () throws IOException
		{
			skipFloat ();
			return readFloat ();
		}
// generated
// generated
		public final double skipAndReadDouble () throws IOException
		{
			skipDouble ();
			return readDouble ();
		}
//!! *# End of generated code

		protected void supply (int item, Consumer c) throws IOException
		{
			switch (item & ITEM_MASK)
			{
				case BEGIN_LEVEL:
					enter (false);
					supplyImpl (c);
					leave ();
					break;
/*!!
#foreach ($type in $types)
$pp.setType($type)
				case SET_$pp.TYPE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skip$pp.Type ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
#end
!!*/
//!! #* Start of generated code
// generated
				case SET_BOOLEAN:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipBoolean ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_BYTE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipByte ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_SHORT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipShort ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_CHAR:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipChar ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_INT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipInt ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_LONG:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipLong ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_FLOAT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipFloat ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_DOUBLE:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipDouble ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
// generated
				case SET_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, a, this, false);
					}
					else
					{
						skipObject ();
						c.setField (o, indirectField, a, this);
					}
					break;
				}
//!! *# End of generated code
				case INSERT_BOOLEAN:
				case INSERT_BYTE:
				case INSERT_SHORT:
				case INSERT_CHAR:
				case INSERT_INT:
				case INSERT_LONG:
				case INSERT_FLOAT:
				case INSERT_DOUBLE:
				case INSERT_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					c.insertComponent (o, indirectField, a, this);
					break;
				}
				case REMOVE_BOOLEAN:
				case REMOVE_BYTE:
				case REMOVE_SHORT:
				case REMOVE_CHAR:
				case REMOVE_INT:
				case REMOVE_LONG:
				case REMOVE_FLOAT:
				case REMOVE_DOUBLE:
				case REMOVE_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					c.removeComponent (o, indirectField, a, this);
					break;
				}
				case UNDO_TRANSACTION:
				{
					Data s = (Data) getData ();
					pushCursor ();
					restore (manager.getTransactionData (Key.read (this)));
					supplyInverse (resetCursor (), c);
					restore (s);
					popCursor ();
					break;
				}
				case MAKE_PERSISTENT:
					c.makePersistent (readLong (), readType ());
					break;
				case MAKE_TRANSIENT:
					c.makeTransient (manager.getObject (readLong ()), readType ());
					break;
				case READ_DATA:
					long id = readLong ();
					if (!readBoolean ())
					{
						c.readData (manager.getObject (id), this);
					}
					break;
			}
		}


		protected void supplyInverse (int item, Consumer c) throws IOException
		{
			switch (item & ITEM_MASK)
			{
				case BEGIN_LEVEL:
					enter (true);
					supplyInverseImpl (c);
					leave ();
					break;
				case SET_BOOLEAN:
				case SET_BYTE:
				case SET_SHORT:
				case SET_CHAR:
				case SET_INT:
				case SET_LONG:
				case SET_FLOAT:
				case SET_DOUBLE:
				case SET_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					if (c instanceof ExtendedConsumer)
					{
						((ExtendedConsumer) c).setField (o, indirectField, indirectField.read (o, this), this, true);
					}
					else
					{
						c.setField (o, indirectField, indirectField.read (o, this), this);
					}
					break;
				}
				case INSERT_BOOLEAN:
				case INSERT_BYTE:
				case INSERT_SHORT:
				case INSERT_CHAR:
				case INSERT_INT:
				case INSERT_LONG:
				case INSERT_FLOAT:
				case INSERT_DOUBLE:
				case INSERT_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					c.removeComponent (o, indirectField, a, this);
					break;
				}
				case REMOVE_BOOLEAN:
				case REMOVE_BYTE:
				case REMOVE_SHORT:
				case REMOVE_CHAR:
				case REMOVE_INT:
				case REMOVE_LONG:
				case REMOVE_FLOAT:
				case REMOVE_DOUBLE:
				case REMOVE_OBJECT:
				{
					PersistenceCapable o = manager.getObject (readLong ());
					int[] a = indirectField.read (o, this);
					c.insertComponent (o, indirectField, a, this);
					break;
				}
				case UNDO_TRANSACTION:
				{
					Data s = (Data) getData ();
					pushCursor ();
					restore (manager.getTransactionData (Key.read (this)));
					supply (resetCursor (), c);
					restore (s);
					popCursor ();
					break;
				}
				case MAKE_PERSISTENT:
					c.makeTransient (manager.getObject (readLong ()), readType ());
					break;
				case MAKE_TRANSIENT:
					c.makePersistent (readLong (), readType ());
					break;
				case READ_DATA:
					long id = readLong ();
					if (readBoolean ())
					{
						c.readData (manager.getObject (id), this);
					}
					break;
			}
		}


		private void supplyImpl (Consumer c) throws IOException
		{
			int item = readItem ();
			while (item >= 0)
			{
				supply (item, c);
				item = next ();
			}
		}


		private void supplyInverseImpl (Consumer c) throws IOException
		{
			int item = readItem ();
			while (item >= 0)
			{
				supplyInverse (item, c);
				item = previous ();
			}
		}


		public final void supply (Consumer c) throws IOException
		{
			c.begin ();
			supplyImpl (c);
			c.end ();
		}


		public final void supplyInverse (Consumer c) throws IOException
		{
			c.begin ();
			supplyInverseImpl (c);
			c.end ();
		}
	}



	protected Transaction (PersistenceManager manager,
						   Thread thread)
	{
		super (manager, true);
		this.thread = thread;
		this.xaApplier = manager.createXAApplier ();
		this.xaApplier.transaction = this;
		setItemSize (SET_BOOLEAN, 2, -1, 1, 0, 0, 0);
		setItemSize (SET_BYTE, 2, -1, 1, 0, 0, 0);
		setItemSize (SET_SHORT, 4, -1, 1, 0, 0, 0);
		setItemSize (SET_CHAR, 4, -1, 1, 0, 0, 0);
		setItemSize (SET_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (SET_LONG, 0, -1, 3, 0, 0, 0);
		setItemSize (SET_FLOAT, 0, -1, 1, 2, 0, 0);
		setItemSize (SET_DOUBLE, 0, -1, 1, 0, 2, 0);
		setItemSize (SET_OBJECT, -1, -1, -1, -1, -1, 0);
		setItemSize (INSERT_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (INSERT_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (INSERT_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (INSERT_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (INSERT_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (INSERT_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (INSERT_FLOAT, 0, -1, 1, 1, 0, 0);
		setItemSize (INSERT_DOUBLE, 0, -1, 1, 0, 1, 0);
		setItemSize (INSERT_OBJECT, -1, -1, -1, -1, -1, 0);
		setItemSize (REMOVE_BOOLEAN, 1, -1, 1, 0, 0, 0);
		setItemSize (REMOVE_BYTE, 1, -1, 1, 0, 0, 0);
		setItemSize (REMOVE_SHORT, 2, -1, 1, 0, 0, 0);
		setItemSize (REMOVE_CHAR, 2, -1, 1, 0, 0, 0);
		setItemSize (REMOVE_INT, 0, -1, 1, 0, 0, 0);
		setItemSize (REMOVE_LONG, 0, -1, 2, 0, 0, 0);
		setItemSize (REMOVE_FLOAT, 0, -1, 1, 1, 0, 0);
		setItemSize (REMOVE_DOUBLE, 0, -1, 1, 0, 1, 0);
		setItemSize (REMOVE_OBJECT, -1, -1, -1, -1, -1, 0);
		setItemSize (UNDO_TRANSACTION, 2, 0, 1, 0, 0, 0);
		setItemSize (MAKE_PERSISTENT, 0, 1, 1, 0, 0, 0);
		setItemSize (MAKE_TRANSIENT, 0, 1, 1, 0, 0, 0);
		setItemSize (READ_DATA, -1, -1, -1, -1, -1, 0);
	}


	public final void setReadOnly ()
	{
		assert activeCount == 0;
		readOnlyBegin = -1;
		readOnly = true;
	}


	public final long getUserId ()
	{
		return userId;
	}


	public final void setUserId (long userId)
	{
		this.userId = userId;
	}


	public abstract Reader createReader ();

	
	protected void setModified ()
	{
		if (activeCount == 0)
		{
			throw new IllegalStateException ("Transaction not active");
		}
		if (readOnly)
		{
			throw new IllegalStateException ("Transaction is read-only");
		}
		modified = true;
	}

	
	public boolean hasModified ()
	{
		return modified;
	}


	public void begin (boolean readOnly)
	{
		if (manager.checkLock && !manager.isLocked (!readOnly))
		{
			throw new IllegalStateException (manager + " is not locked");
		}
//		System.err.println ("begin " + readOnly + " " + Thread.currentThread());
//		Thread.dumpStack ();
		if (readOnly && !this.readOnly)
		{
			this.readOnly = true;
			readOnlyBegin = activeCount;
		}
		if (activeCount++ == 0)
		{
			modified = false;
			manager.beginTransaction (this);
		}
		if (!this.readOnly)
		{
			beginLevel ();
		}
	}


	public void commit ()
	{
//		System.err.println ("commit " + readOnly + " " + Thread.currentThread());
//		Thread.dumpStack ();
		boolean c = !readOnly;
		if (activeCount == 1)
		{
			manager.prepareCompletion (this, c);
		}
		if (c)
		{
			endLevel ();
		}
		if (--activeCount == readOnlyBegin)
		{
			readOnly = false;
			readOnlyBegin = -1;
		}
		if (activeCount == 0)
		{
			manager.completeTransaction (this, c);
			clear ();
		}
	}


	public void rollback ()
	{
//		System.err.println ("rollback " + readOnly + " " + Thread.currentThread());
		try
		{
			if (!readOnly)
			{
				if (hasItemsInCurrentLevel ())
				{
					Reader r = createReader ();
					r.moveToCurrent ();
					beginApply ();
					r.supplyInverse (xaApplier);
				}
				discardLevel ();
			}
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		finally
		{
			if (--activeCount == 0)
			{
				manager.completeTransaction (this, false);
				clear ();
			}
			if (activeCount == readOnlyBegin)
			{
				readOnly = false;
				readOnlyBegin = -1;
			}
			endApply ();
		}
	}


	public final void commitAll ()
	{
		while (activeCount > 0)
		{
			commit ();
		}
	}


	public final void rollbackAll ()
	{
		while (activeCount > 0)
		{
			rollback ();
		}
	}


	@Override
	public void close ()
	{
		commitAll ();
		synchronized (manager.xaLock)
		{
			remove (manager.transactions);
		}
	}


	public boolean isActive ()
	{
		return activeCount > 0;
	}

	
	public int getActiveCount ()
	{
		return activeCount;
	}


	public final long getId ()
	{
		return id;
	}


	public final Key getKey ()
	{
		return new Key (manager.getId (), id, userId);
	}


	public static boolean isApplying (Transaction t)
	{
		return (t != null) && t.applyXA;
	}

	public static boolean isNotApplying (Transaction t)
	{
		return (t != null) && !t.applyXA;
	}
	
	public void beginApply ()
	{
		applyXA = true;
	}

	
	public void endApply ()
	{
		applyXA = false;
	}


/*
	public static final Key getKey (int managerId, long xaId)
	{
		return new Key (managerId, xaId);
	}
*/

	final Transaction[] add (Transaction[] t)
	{
		int n = t.length;
		for (int i = 0; i < n - 1; i++)
		{
			if (t[i] == null)
			{
				t[i] = this;
				return t;
			}
		}
		Transaction[] a = new Transaction[n * 2];
		System.arraycopy (t, 0, a, 0, n);
		a[n - 1] = this;
		return a;
	}


	final void remove (Transaction[] t)
	{
		int n = t.length;
		for (int i = 0; i < n; i++)
		{
			if (t[i] == this)
			{
				System.arraycopy (t, i + 1, t, i, n - i - 1);
				return;
			}
		}
	}


	@Override
	public void restore (HierarchicalQueue.Data data)
	{
		id = ((Data) data).getKey ().getXAId ();
		cache = ((Data) data).cache;
		super.restore (data);
	}

	private boolean writePrepared;
	private PersistenceCapable preparedPC = null;
	private PersistenceField preparedField = null;
	private int[] preparedIndices = null;
	private int[][] preparedIndicesPool = new int[16][];

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public void prepareSet$pp.Type (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSet$pp.Type (pc, field, indices, field.get$pp.Type (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public void prepareSetBoolean (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetBoolean (pc, field, indices, field.getBoolean (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetByte (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetByte (pc, field, indices, field.getByte (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetShort (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetShort (pc, field, indices, field.getShort (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetChar (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetChar (pc, field, indices, field.getChar (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetInt (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetInt (pc, field, indices, field.getInt (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetLong (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetLong (pc, field, indices, field.getLong (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetFloat (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetFloat (pc, field, indices, field.getFloat (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetDouble (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetDouble (pc, field, indices, field.getDouble (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
// generated
// generated
	public void prepareSetObject (PersistenceCapable pc,
									PersistenceField field, int[] indices)
	{
		PersistenceManager pm = pc.getPersistenceManager ();
		if ((pm != null) && !applyXA)
		{
			writePrepared = true;
			prepareLogSetObject (pc, field, indices, field.getObject (pc, indices));
		}
		else
		{
			writePrepared = false;
		}
		preparedPC = pc;
		preparedField = field;
		int n;
		if ((indices == null) || ((n = indices.length) == 0))
		{
			preparedIndices = null;
		}
		else
		{
			int[][] p = preparedIndicesPool;
			if (n >= p.length)
			{
				preparedIndicesPool = p = new int[n + 1][];
			}
			int[] a;
			if ((a = p[n]) == null)
			{
				a = p[n] = new int[n];
			}
			while (--n >= 0)
			{
				a[n] = indices[n];
			}
			preparedIndices = a;
		}
	}
// generated
//!! *# End of generated code

	public void finishSet ()
	{
		if (writePrepared && !isLoggingSuppressed ())
		{
			switch (preparedField.typeId)
			{
/*!!
#foreach ($type in $types)
$pp.setType($type)
				case TypeId.$pp.TYPE:
#if ($pp.Object)
					try
					{
						preparedField.writeObject
							(preparedField.getObject (preparedPC, preparedIndices), this);
					}
					catch (IOException e)
					{
						throw new FatalPersistenceException (e);
					}
#else
					write$pp.Type (preparedField.get$pp.Type (preparedPC, preparedIndices));
#end
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					writeBoolean (preparedField.getBoolean (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.BYTE:
					writeByte (preparedField.getByte (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.SHORT:
					writeShort (preparedField.getShort (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.CHAR:
					writeChar (preparedField.getChar (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.INT:
					writeInt (preparedField.getInt (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.LONG:
					writeLong (preparedField.getLong (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.FLOAT:
					writeFloat (preparedField.getFloat (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.DOUBLE:
					writeDouble (preparedField.getDouble (preparedPC, preparedIndices));
					break;
// generated
				case TypeId.OBJECT:
					try
					{
						preparedField.writeObject
							(preparedField.getObject (preparedPC, preparedIndices), this);
					}
					catch (IOException e)
					{
						throw new FatalPersistenceException (e);
					}
					break;
//!! *# End of generated code
			}
		}
		preparedPC.fieldModified (preparedField, preparedIndices, this);
		preparedPC = null;
		preparedField = null;
	}


	public void finishSet (XAQueue.Reader reader)
	{
		switch (preparedField.typeId)
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
			{
				$type value = preparedField.readAndSet$pp.Type
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
#if ($pp.Object)
					try
					{
						preparedField.writeObject (value, this);
					}
					catch (IOException e)
					{
						throw new FatalPersistenceException (e);
					}
#else
					write$pp.Type (value);
#end
				}
				break;
			}
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
			{
				boolean value = preparedField.readAndSetBoolean
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeBoolean (value);
				}
				break;
			}
// generated
			case TypeId.BYTE:
			{
				byte value = preparedField.readAndSetByte
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeByte (value);
				}
				break;
			}
// generated
			case TypeId.SHORT:
			{
				short value = preparedField.readAndSetShort
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeShort (value);
				}
				break;
			}
// generated
			case TypeId.CHAR:
			{
				char value = preparedField.readAndSetChar
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeChar (value);
				}
				break;
			}
// generated
			case TypeId.INT:
			{
				int value = preparedField.readAndSetInt
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeInt (value);
				}
				break;
			}
// generated
			case TypeId.LONG:
			{
				long value = preparedField.readAndSetLong
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeLong (value);
				}
				break;
			}
// generated
			case TypeId.FLOAT:
			{
				float value = preparedField.readAndSetFloat
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeFloat (value);
				}
				break;
			}
// generated
			case TypeId.DOUBLE:
			{
				double value = preparedField.readAndSetDouble
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					writeDouble (value);
				}
				break;
			}
// generated
			case TypeId.OBJECT:
			{
				Object value = preparedField.readAndSetObject
					(preparedPC, preparedIndices, reader);
				if (writePrepared && !isLoggingSuppressed ())
				{
					try
					{
						preparedField.writeObject (value, this);
					}
					catch (IOException e)
					{
						throw new FatalPersistenceException (e);
					}
				}
				break;
			}
//!! *# End of generated code
		}
		preparedPC.fieldModified (preparedField, preparedIndices, this);
		preparedPC = null;
		preparedField = null;
	}

	
	public Transaction makeActive ()
	{
		if (!isActive ())
		{
			begin (false);
		}
		return this;
	}
	

	private final ObjectList refs = new ObjectList ();
	
	public void fireSharedObjectModified (Shareable object)
	{
		int s = refs.size ();
		object.appendReferencesTo (refs);
		while (refs.size () > s)
		{
			((SharedObjectReference) refs.pop ()).sharedObjectModified (object, this);
		}
	}

	
	public void markModified (Object object, PersistenceField field, int[] indices)
	{
		modifiedFields.push (object, field, indices);
	}

	public void unmarkModified ()
	{
		modifiedFields.setSize (modifiedFields.size () - 3);
	}

	public boolean isModified (Object object, PersistenceField field, int[] indices)
	{
		for (int i = modifiedFields.size () - 3; i >= 0; i -= 3)
		{
			if ((object == modifiedFields.get (i))
				&& field.overlaps (indices, (PersistenceField) modifiedFields.get (i + 1), (int[]) modifiedFields.get (i + 2)))
			{
				return true;
			}
		}
		return false;
	}

}
