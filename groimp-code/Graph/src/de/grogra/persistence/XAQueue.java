
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

import de.grogra.reflect.Type;
import de.grogra.util.*;
import de.grogra.persistence.ManageableType.*;

public abstract class XAQueue extends HierarchicalQueue
	implements PersistenceOutput
{
	protected final PersistenceManager manager;
	protected BindingsCache cache;
	private boolean nested = false;

	static final int MANAGEABLE_WITH_TYPE = PersistenceInput.MIN_UNUSED_KIND;


	public static class Data extends HierarchicalQueue.Data
	{
		final boolean isRemote;


		Data (boolean isRemote)
		{
			super ();
			this.isRemote = isRemote;
		}


		public final boolean isRemote ()
		{
			return isRemote;
		}
		
	}


	public class Reader extends HierarchicalQueue.Reader
		implements PersistenceInput
	{
		private boolean nested = false;
//	private final int[][] indexArrays = new int[64][];


		protected Reader ()
		{
			super (manager.getBindings ().getTypeLoader ());
		}


		public final PersistenceBindings getBindings ()
		{
			return manager.getBindings();
		}


		public void open ()
		{
		}


		@Override
		public void close ()
		{
			super.close ();
		}


		public XAQueue getQueue ()
		{
			return XAQueue.this;
		}

/*
		public final int[] readIndices (int length)
		{
			int[] a = indexArrays[length];
			if (a == null)
			{
				a = new int[length];
				indexArrays[length] = a;
			}
			for (int i = 0; i < length; i++)
			{
				a[i] = readInt ();
			}
			return a;
		}
*/


		public String readName () throws UTFDataFormatException
		{
			return readUTF ();
		}


		public String readString () throws UTFDataFormatException
		{
			return readUTF ();
		}


		public int getNextObjectKind ()
		{
			byte b = peekByte ();
			return (b == MANAGEABLE_WITH_TYPE) ? MANAGEABLE_OBJECT : b;
		}

		
		ManageableType readType ()
		{
			return cache.getType (readInt ());
		}


		private void consume (int expected) throws IOException
		{
			if (expected != readByte ())
			{
				throw new StreamCorruptedException ("Expected " + expected);
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
					return readUTF ();
				case SERIALIZED_OBJECT:
					consume (SERIALIZED_OBJECT);
					if (!nested)
					{
						readSkipBlock ();
					}
					try
					{
						return readObjectInStream ();
					}
					catch (ClassNotFoundException e)
					{
						throw new IOWrapException (e);
					}
				default:
					throw new AssertionError ();
			}
		}


		public final Shareable readSharedObject () throws IOException
		{
			consume (SHARED_OBJECT_REFERENCE);
			if (!nested)
			{
				readSkipBlock ();
			}
			return cache.getSOProvider (readInt ()).readReference (this)
				.resolve ();
		}


		public final long readPersistentObjectId ()
			throws IOException
		{
			consume (PERSISTENT_OBJECT_ID);
			return readLong ();
		}


		public final PersistenceCapable readPersistentObject ()
			throws IOException
		{
			consume (PERSISTENT_OBJECT_ID);
			return manager.getObject (readLong ());
		}


		public int beginArray () throws IOException
		{
			consume (ARRAY_OBJECT);
			if (!nested)
			{
				readSkipBlock ();
			}
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
			int i = readByte ();
			ManageableType t;
			if (!nested)
			{
				readSkipBlock ();
			}
			switch (i)
			{
				case MANAGEABLE_OBJECT:
					t = null;
					break;
				case MANAGEABLE_WITH_TYPE:
					t = readType ();
					break;
				default:
					throw new StreamCorruptedException (Integer.toString (i));
			}
			return t;
		}
		
		
		public boolean areFieldsProvided ()
		{
			return true;
		}


		public Field beginField (ManageableType type, Field field)
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
			this.nested = nested;
		}


		public PersistenceCapable resolveId (long inputId)
		{
			return manager.getObject (inputId);
		}


		final void skipObject () throws IOException
		{
			switch (readByte ())
			{
				case NULL_OBJECT:
					break;
				case STRING_OBJECT:
					skipUTF ();
					break;
				case SERIALIZED_OBJECT:
				case MANAGEABLE_OBJECT:
				case MANAGEABLE_WITH_TYPE:
				case SHARED_OBJECT_REFERENCE:
				case ARRAY_OBJECT:
					skipBlock ();
					break;
				case PERSISTENT_OBJECT_ID:
					skipLong ();
					break;
				default:
					throw new StreamCorruptedException ();
			}
		}

	}


	public XAQueue (PersistenceManager manager, boolean createBackLinks)
	{
		super (false, createBackLinks);
		this.manager = manager;
		cache = manager.getConnection ().getLocalCache ();
	}

	@Override
	protected Data createData ()
	{
		return new Data (false);
	}

	@Override
	public void clear ()
	{
		super.clear ();
		nested = false;
	}


	public final PersistenceManager getPersistenceManager ()
	{
		return manager;
	}


	public void writeName (String value)
	{
		writeUTF (value);
	}


	public void writeString (String value)
	{
		writeUTF (value);
	}


	public void writeNullObject ()
	{
		writeByte (PersistenceInput.NULL_OBJECT);
	}


	public void writeStringObject (String value)
	{
		writeByte (PersistenceInput.STRING_OBJECT);
		writeUTF (value);
	}


	public void writeObject (Object object, Type type) throws IOException
	{
		writeByte (PersistenceInput.SERIALIZED_OBJECT);
		int i = nested ? -1 : beginSkipBlock ();
		writeObjectInStream (object);
		if (i >= 0)
		{
			endSkipBlock (i);
		}
	}


	public final void writeSharedObjectReference (Shareable o)
	{
		writeByte (PersistenceInput.SHARED_OBJECT_REFERENCE);
		int i = nested ? -1 : beginSkipBlock ();
		writeInt (cache.getHandle (o.getProvider ()));
		try
		{
			o.getProvider ().writeObject (o, this);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		if (i >= 0)
		{
			endSkipBlock (i);
		}
	}


	public final void writePersistentObjectReference (PersistenceCapable o)
	{
		writeByte (PersistenceInput.PERSISTENT_OBJECT_ID);
		writeLong (o.getId ());
	}


	private int skipBlockIndex;

	public void beginArray (int length, Type type) throws IOException
	{
		writeByte (PersistenceInput.ARRAY_OBJECT);
		if (!nested)
		{
			skipBlockIndex = beginSkipBlock ();
		}
		writeInt (length);
	}


	public void writeComponentSeparator ()
	{
	}


	public void endArray ()
	{
		if (!nested)
		{
			endSkipBlock (skipBlockIndex);
		}
	}

	
	void writeType (ManageableType type)
	{
		writeInt (cache.getHandle (type));
	}


	public boolean beginManaged (ManageableType type, boolean writeType)
	{
		if (writeType)
		{
			writeByte (MANAGEABLE_WITH_TYPE);
			if (!nested)
			{
				skipBlockIndex = beginSkipBlock ();
			}
			writeType (type);
		}
		else
		{
			writeByte (PersistenceInput.MANAGEABLE_OBJECT);
			if (!nested)
			{
				skipBlockIndex = beginSkipBlock ();
			}
		}
		return true;
	}


	public void beginFields ()
	{
	}


	public void beginField (ManageableType.Field field)
	{
		writeShort (field.getFieldId ());
	}


	public void endField (ManageableType.Field field)
	{
	}


	public void endFields ()
	{
		writeShort (-1);
	}


	public void endManaged (Object object, boolean writeType)
	{
		if (!nested)
		{
			endSkipBlock (skipBlockIndex);
		}
	}


	public void setNested (boolean nested)
	{
		this.nested = nested;
	}

}
