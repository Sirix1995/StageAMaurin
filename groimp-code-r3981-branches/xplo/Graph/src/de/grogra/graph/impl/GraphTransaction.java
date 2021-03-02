
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

package de.grogra.graph.impl;

import java.io.IOException;
import java.io.PrintStream;

import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.reflect.TypeId;
import de.grogra.xl.util.ObjectList;

public class GraphTransaction extends Transaction
{
	public static final int ADD_EDGE_BITS = Transaction.MIN_UNUSED_ITEM;
	public static final int REMOVE_EDGE_BITS = ADD_EDGE_BITS + 1;
	public static final int MIN_UNUSED_ITEM = REMOVE_EDGE_BITS + 1;


	public interface Consumer
	{
		void addEdgeBits (Node source, Node target, int mask);

		void removeEdgeBits (Node source, Node target, int mask);


		class Adapter extends Transaction.Consumer.Adapter implements Consumer
		{
			public void addEdgeBits (Node source, Node target, int mask)
			{
			}

			public void removeEdgeBits (Node source, Node target, int mask)
			{
			}
		}


		class Multicaster extends Transaction.Consumer.Multicaster
			implements Consumer
		{
/*!!
#foreach ($t in ["add", "remove"])
			public void ${t}EdgeBits (Node source, Node target, int mask)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object o;
					if ((o = cs.get (i)) instanceof Consumer)
					{
						((Consumer) o).${t}EdgeBits (source, target, mask);
					}
				}
			}
#end
!!*/
//!! #* Start of generated code
			public void addEdgeBits (Node source, Node target, int mask)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object o;
					if ((o = cs.get (i)) instanceof Consumer)
					{
						((Consumer) o).addEdgeBits (source, target, mask);
					}
				}
			}
			public void removeEdgeBits (Node source, Node target, int mask)
			{
				ObjectList cs = getConsumers ();
				int n = cs.size ();
				for (int i = 0; i < n; i++)
				{
					Object o;
					if ((o = cs.get (i)) instanceof Consumer)
					{
						((Consumer) o).removeEdgeBits (source, target, mask);
					}
				}
			}
//!! *# End of generated code
		}
	}


	public static class Dump implements Transaction.Consumer, Consumer
	{
		protected PrintStream out;


		public Dump (PrintStream out)
		{
			this.out = out;
		}


		public void setField (PersistenceCapable o, PersistenceField field,
							  int[] indices, Transaction.Reader reader)
		{
			out.print (o);
			out.print ('.');
			out.print (field.getName ());
			if ((indices != null) && (indices.length > 0))
			{
				for (int i = 0; i < indices.length; i++)
				{
					out.print ('[');
					out.print (indices[i]);
					out.print (']');
				}
			}
			out.print (" = ");
			try
			{
				switch (field.getType ().getTypeId ())
				{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
					case TypeId.$pp.TYPE:
					{
						out.println (reader.read$pp.Type ());
						break;
					}
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.BOOLEAN:
					{
						out.println (reader.readBoolean ());
						break;
					}
// generated
					case TypeId.BYTE:
					{
						out.println (reader.readByte ());
						break;
					}
// generated
					case TypeId.SHORT:
					{
						out.println (reader.readShort ());
						break;
					}
// generated
					case TypeId.CHAR:
					{
						out.println (reader.readChar ());
						break;
					}
// generated
					case TypeId.INT:
					{
						out.println (reader.readInt ());
						break;
					}
// generated
					case TypeId.LONG:
					{
						out.println (reader.readLong ());
						break;
					}
// generated
					case TypeId.FLOAT:
					{
						out.println (reader.readFloat ());
						break;
					}
// generated
					case TypeId.DOUBLE:
					{
						out.println (reader.readDouble ());
						break;
					}
//!! *# End of generated code
					case TypeId.OBJECT:
					{
						out.println (ManageableType.read (field.getLastField (), reader));
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace (out);
			}
		}


		public void insertComponent (PersistenceCapable o, PersistenceField field,
									 int[] indices, Transaction.Reader reader)
		{
			out.print (o);
		}


		public void removeComponent (PersistenceCapable o, PersistenceField field,
									 int[] indices, Transaction.Reader reader)
		{
			out.print (o);
		}


		public void makePersistent (long id, ManageableType type)
		{
			out.print ("makePersitent, id = ");
			out.println (id);
		}


		public void makeTransient (PersistenceCapable o, ManageableType type)
		{
			out.print ("makeTransient (");
			out.print (o);
			out.print ("), id = ");
			out.println (o.getId ());
		}


		public void readData (PersistenceCapable o, Transaction.Reader reader)
		{
			out.print ("readData (");
			out.print (o);
			out.print ("), id = ");
			out.println (o.getId ());
		}


		public void begin ()
		{
			out.println ("begin");
		}


		public void end ()
		{
			out.println ("end");
		}


		public void addEdgeBits (Node source, Node target, int mask)
		{
			out.print ("addEdgeBits (");
			out.print (source);
			out.print (" -");
			out.print (Integer.toHexString (mask));
			out.print ("-> ");
			out.print (target);
			out.println (')');
		}


		public void removeEdgeBits (Node source, Node target, int mask)
		{
			out.print ("removeEdgeBits (");
			out.print (source);
			out.print (" -");
			out.print (Integer.toHexString (mask));
			out.print ("-> ");
			out.print (target);
			out.println (')');
		}
	}


	final ObjectList<Node> madePersistent = new ObjectList<Node> ();
	final ObjectList<Node> madeTransient = new ObjectList<Node> ();
	final ObjectList<Node> extentIndexChanged = new ObjectList<Node> ();


	public GraphTransaction (GraphManager manager, Thread thread)
	{
		super (manager, thread);
		setItemSize (ADD_EDGE_BITS, 0, 2, 2, 0, 0, 0);
		setItemSize (REMOVE_EDGE_BITS, 0, 2, 2, 0, 0, 0);
	}


	@Override
	protected boolean isLoggingSuppressed ()
	{
		return ((GraphManager) manager).loggingSuppressed;
	}


	public void logAddEdgeBits (Node source, Node target, int added, int mask)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (ADD_EDGE_BITS);
		writeLong (source.id);
		writeLong (target.id);
		writeInt (added);
		writeInt (mask);
	}


	public void logRemoveEdgeBits (Node source, Node target, int removed,
								int mask)
	{
		setModified ();
		if (isLoggingSuppressed ())
		{
			return;
		}
		writeItem (REMOVE_EDGE_BITS);
		writeLong (source.id);
		writeLong (target.id);
		writeInt (removed);
		writeInt (mask);
	}


	public class Reader extends Transaction.Reader
	{
		Reader ()
		{
			super ();
		}


		@Override
		protected void supply (int item, Transaction.Consumer c)
			throws IOException
		{
			switch (item & ITEM_MASK)
			{
				case ADD_EDGE_BITS:
					if (c instanceof Consumer)
					{
						((Consumer) c).addEdgeBits
							((Node) manager.getObject (readLong ()),
							 (Node) manager.getObject (readLong ()),
							 readInt ());
					}
					else
					{
						readLong ();
						readLong ();
						readInt ();
					}
					break;
				case REMOVE_EDGE_BITS:
					if (c instanceof Consumer)
					{
						((Consumer) c).removeEdgeBits
							((Node) manager.getObject (readLong ()),
							 (Node) manager.getObject (readLong ()),
							 readInt ());
					}
					else
					{
						readLong ();
						readLong ();
						readInt ();
					}
					break;
				default:
					super.supply (item, c);
			}
		}


		@Override
		protected void supplyInverse (int item, Transaction.Consumer c)
			throws IOException
		{
			switch (item & ITEM_MASK)
			{
				case ADD_EDGE_BITS:
					((Consumer) c).removeEdgeBits
						((Node) manager.getObject (readLong ()),
						 (Node) manager.getObject (readLong ()),
						 readInt ());
					break;
				case REMOVE_EDGE_BITS:
					((Consumer) c).addEdgeBits
						((Node) manager.getObject (readLong ()),
						 (Node) manager.getObject (readLong ()),
						 readInt ());
					break;
				default:
					super.supplyInverse (item, c);
			}
		}
	}


	@Override
	public Transaction.Reader createReader ()
	{
		return new Reader ();
	}

}
