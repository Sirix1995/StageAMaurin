
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

package de.grogra.pf.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.MissingResourceException;

import javax.swing.event.TreeModelEvent;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.grogra.graph.AccessorMap;
import de.grogra.graph.Attribute;
import de.grogra.graph.AttributeAccessor;
import de.grogra.graph.BooleanAttributeAccessor;
import de.grogra.graph.ByteAttributeAccessor;
import de.grogra.graph.CharAttributeAccessor;
import de.grogra.graph.DoubleAttributeAccessor;
import de.grogra.graph.FloatAttributeAccessor;
import de.grogra.graph.GraphState;
import de.grogra.graph.IntAttributeAccessor;
import de.grogra.graph.LongAttributeAccessor;
import de.grogra.graph.ObjectAttributeAccessor;
import de.grogra.graph.ShortAttributeAccessor;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceBindings;
import de.grogra.persistence.Transaction;
import de.grogra.persistence.XMLPersistenceWriter;
import de.grogra.reflect.Field;
import de.grogra.reflect.FieldChain;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.Described;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;
import de.grogra.util.StringMap;
import de.grogra.util.TreeDiff;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.xl.util.ObjectList;

public class Item extends Node implements Described, RegistryContext
{
	final static Item[] ITEM_0 = new Item[0];

	private static final ThreadLocal DERIVED_SOURCE = new ThreadLocal ();


	private static final int ST_PROGRAM = 0;
	private static final int ST_USER = 1;
	private static final int ST_DERIVED = 2;
	private static final int ST_PLUGIN = 3;

	private Registry registry = null;
	private String absoluteName = null;
	private boolean isDirectory = false, optionCategory = false;
	private int sourceType = ST_PROGRAM;
	private PluginDescriptor pluginDescriptor = null;

	private int stamp = 0;

	final ObjectList oldChildren = new ObjectList (4, false);
	private TreeDiff.DiffInfo diffInfo;

	private ObjectList derivedItems = null;

	private String[][] elements = null;

	private boolean activated = false;

	private StringMap descriptions = null;

	private AccessorMap attributes = null;

	private Described description, defaultDescription;


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Item ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new Item ();
	}

//enh:end

	private static final class AdditionalAccessor implements AttributeAccessor
/*!!
#foreach ($type in $types)
$pp.setType($type)
	, ${pp.Type}AttributeAccessor
#end
!!*/
//!! #* Start of generated code
// generated
	, BooleanAttributeAccessor
// generated
	, ByteAttributeAccessor
// generated
	, ShortAttributeAccessor
// generated
	, CharAttributeAccessor
// generated
	, IntAttributeAccessor
// generated
	, LongAttributeAccessor
// generated
	, FloatAttributeAccessor
// generated
	, DoubleAttributeAccessor
// generated
	, ObjectAttributeAccessor
//!! *# End of generated code
	{
		private final Attribute attribute;
		private Object value;


		AdditionalAccessor (Attribute attribute)
		{
			this.attribute = attribute;
		}


		@Override
		public Type getType ()
		{
			return attribute.getType ();
		}


		@Override
		public Attribute getAttribute ()
		{
			return attribute;
		}


		@Override
		public Field getField ()
		{
			return null;
		}


		@Override
		public boolean isWritable (Object object, GraphState gs)
		{
			return true;
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)

		public $type get$pp.Type (Object object, GraphState gs)
		{
#if ($pp.Object)
			return get$pp.Type (object, null, gs);
#else
			return (value != null) ? $pp.unwrap("value") : $pp.null;
#end
		}

#if ($pp.Object)

		public $type get$pp.Type (Object object, Object placeIn,
								  GraphState gs)
		{
			return value;
		}
#end

		public $type set$pp.Type (Object object, $type value,
								  GraphState gs)
		{
			this.value = $pp.wrap("value");
			return value;
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		@Override
		public boolean getBoolean (Object object, GraphState gs)
		{
			return (value != null) ? (((Boolean) (value)).booleanValue ()) : false;
		}
// generated
// generated
		@Override
		public boolean setBoolean (Object object, boolean value,
								  GraphState gs)
		{
			this.value = ((value) ? Boolean.TRUE : Boolean.FALSE);
			return value;
		}
// generated
// generated
		@Override
		public byte getByte (Object object, GraphState gs)
		{
			return (value != null) ? (((Number) (value)).byteValue ()) : ((byte) 0);
		}
// generated
// generated
		@Override
		public byte setByte (Object object, byte value,
								  GraphState gs)
		{
			this.value = Byte.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public short getShort (Object object, GraphState gs)
		{
			return (value != null) ? (((Number) (value)).shortValue ()) : ((short) 0);
		}
// generated
// generated
		@Override
		public short setShort (Object object, short value,
								  GraphState gs)
		{
			this.value = Short.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public char getChar (Object object, GraphState gs)
		{
			return (value != null) ? (((Character) (value)).charValue ()) : ((char) 0);
		}
// generated
// generated
		@Override
		public char setChar (Object object, char value,
								  GraphState gs)
		{
			this.value = Character.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public int getInt (Object object, GraphState gs)
		{
			return (value != null) ? (((Number) (value)).intValue ()) : ((int) 0);
		}
// generated
// generated
		@Override
		public int setInt (Object object, int value,
								  GraphState gs)
		{
			this.value = Integer.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public long getLong (Object object, GraphState gs)
		{
			return (value != null) ? (((Number) (value)).longValue ()) : ((long) 0);
		}
// generated
// generated
		@Override
		public long setLong (Object object, long value,
								  GraphState gs)
		{
			this.value = Long.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public float getFloat (Object object, GraphState gs)
		{
			return (value != null) ? (((Number) (value)).floatValue ()) : ((float) 0);
		}
// generated
// generated
		@Override
		public float setFloat (Object object, float value,
								  GraphState gs)
		{
			this.value = Float.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public double getDouble (Object object, GraphState gs)
		{
			return (value != null) ? (((Number) (value)).doubleValue ()) : ((double) 0);
		}
// generated
// generated
		@Override
		public double setDouble (Object object, double value,
								  GraphState gs)
		{
			this.value = Double.valueOf (value);
			return value;
		}
// generated
// generated
		@Override
		public Object getObject (Object object, GraphState gs)
		{
			return getObject (object, null, gs);
		}
// generated
// generated
		@Override
		public Object getObject (Object object, Object placeIn,
								  GraphState gs)
		{
			return value;
		}
// generated
		@Override
		public Object setObject (Object object, Object value,
								  GraphState gs)
		{
			this.value = (value);
			return value;
		}
//!! *# End of generated code

		@Override
		public Object setSubfield (Object object, FieldChain field,
								   int[] indices, Object value, GraphState gs)
		{
			Object o = this.value;
			int i;
			try
			{
				for (i = 0; i < field.length () - 1; i++)
				{
					o = field.getField (i).getObject (o);
				}
				Reflection.set (o, field.getField (i), value);
			}
			catch (IllegalAccessException e)
			{
				throw new WrapException (e);
			}
			return value;
		}
	}


	private Item ()
	{
		this (null, false);
	}


	public Item (String key, boolean isDirectory)
	{
		super ();
		setName (key);
		this.isDirectory = isDirectory;
		Item a = (Item) DERIVED_SOURCE.get ();
		if (a != null)
		{
			if (a.derivedItems == null)
			{
				a.derivedItems = new ObjectList (10);
			}
			a.derivedItems.add (this);
		}
	}


	public Item (String key)
	{
		this (key, false);
	}


	public static Item resolveItem (RegistryContext ctx, String key)
	{
		for (Registry r = ctx.getRegistry (); r != null;
			 r = r.getParentRegistry ())
		{
			Item i = r.getItem (key);
			if (i != null)
			{
				return i.resolveLink (ctx);
			}
		}
		return null;
	}


	public Item resolveLink (RegistryContext ctx)
	{
		return this;
	}


	public final Object getLock ()
	{
		return (getRegistry () == null) ? this : registry.getLock ();
	}


	public final Object getWriteLock ()
	{
		return (getRegistry () == null) ? this : registry.getWriteLock ();
	}


	TreeDiff.DiffInfo getDiffInfo ()
	{
		if (diffInfo == null)
		{
			diffInfo = new TreeDiff.DiffInfo (this);		
		}
		return diffInfo;
	}


	@Override
	protected void dupUnmanagedFields (Node original)
	{
		Item o = (Item) original;
		isDirectory = o.isDirectory;
	}


	public Object getDefaultValue (NType.Field field)
	{
		return field.get (getNType ().getRepresentative (), null);
	}


	public I18NBundle getI18NBundle ()
	{
		return (pluginDescriptor != null) ? pluginDescriptor.getI18NBundle ()
			: null;
	}


	public Object getFromResource (String key)
	{
		return getI18NBundle ().getObject (key, null);
	}


	public Class classForName (String name, boolean initialize)
		throws ClassNotFoundException
	{
		return Class.forName (name, initialize, pluginDescriptor.getClassLoader ());
	}

/*
	public void setUserObject (Object object)
	{
		userObject = object;
	}


	public Object getUserObject ()
	{
		return userObject;
	}
*/

	protected void setDirectory ()
	{
		isDirectory = true;
	}


	public boolean isDirectory ()
	{
		return isDirectory;
	}


	public final Item makeUserItem (boolean recursive)
	{
		sourceType = ST_USER;
		if (recursive)
		{
			for (Node n = getBranch(); n != null; n = n.getSuccessor ())
			{
				((Item) n).makeUserItem (true);
			}
		}
		return this;
	}


	public final boolean isUserItem ()
	{
		return sourceType == ST_USER;
	}


	@Override
	public void setName (String name)
	{
		boolean wasNotNull = getName () != null;
		super.setName (name);
		absoluteName = null;
		if (wasNotNull)
		{
			descriptions = null;
		}
	}


	protected static final void setNameIfNull (Item item, String name)
	{
		if (item.getName () == null)
		{
			item.setName (name);
		}
	}


	public final boolean isPluginItem (String pluginId, String name)
	{
		return hasName (name)
			&& ((pluginId == null)
				|| ((getPluginDescriptor () != null)
					&& pluginId.equals (getPluginDescriptor ().getName ())));
	}


	private void updateDerived ()
	{
		Item n = (Item) getAxisParent ();
		String q = getName ();
		if ((q.indexOf ('/') >= 0) || (q.indexOf ('\\') >= 0)) 
		{
			q = Utils.escape (q, "/\\");
		}
		absoluteName = (n == null) ? q : n.getAbsoluteName () + '/' + q;
		stamp = getRegistry ().stamp;
	}


	public final String getAbsoluteName ()
	{
		if ((absoluteName == null) || (stamp != getRegistry ().stamp))
		{
			updateDerived ();
		}
		return absoluteName;
	}


	public final Item getRoot ()
	{
		Item n = this;
		while (n.getAxisParent () != null)
		{
			n = (Item) n.getAxisParent ();
		}
		return n;
	}


	public void setDescription (Described description)
	{
		this.description = description;
	}


	public void setDefaultDescription (Described descr)
	{
		this.defaultDescription = descr;
	}


	public void setDescription (String type, Object value)
	{
		if (descriptions == null)
		{
			descriptions = new StringMap (8);
		}
		descriptions.put (type, value);
	}


	@Override
	public final Object getDescription (String type)
	{
		Object d;
		if ((descriptions != null) && ((d = descriptions.get (type)) != null))
		{
			return (d == this) ? null : d;
		}
		if ((d = getDerivedDescription (type)) != null)
		{
			return d;
		}
		if (descriptions == null)
		{
			descriptions = new StringMap (8);
		}
		d = getDescriptionImpl (type);
		if (d == null)
		{
			d = getDefaultDescription (type);
		}
		if (Described.NAME.equals (type))
		{
			if ((d != null) && (d != getName ()))
			{
				StringBuffer sb = new StringBuffer (d.toString ());
			lookForAccelerator:
				for (int i = 0; i < sb.length (); i++)
				{
					switch (sb.charAt (i))
					{
						case '\\':
							sb.delete (i, i + 1);
							break;
						case '&':
							sb.delete (i, i + 1);
							if (!descriptions.containsKey (MNEMONIC_KEY))
							{
								d = getDescriptionImpl (MNEMONIC_KEY);
								if (d != null)
								{
									descriptions.put (MNEMONIC_KEY, d);
								}
								else
								{
									char c = sb.charAt (i);
									if ((c >= 'a') && (c <= 'z'))
									{
										c -= 'a' - 'A';
									}
									if (((c >= '0') && (c <= '9'))
										|| ((c >= 'A') && (c <= 'Z')))
									{
										descriptions.put (MNEMONIC_KEY, Integer.valueOf (c));
									}
									else
									{
										descriptions.put (MNEMONIC_KEY, this);
									}
								}
							}
							break lookForAccelerator;
					}
				}
				d = sb.toString ();
			}
		}
		else if (MNEMONIC_KEY.equals (type))
		{
			if (d == null)
			{
				getDescription (Described.NAME);
				if ((d = descriptions.get (MNEMONIC_KEY)) != null)
				{
					return (d == this) ? null : d;
				}
			}
		}
		descriptions.put (type, (d == null) ? this : d);
		return d;
	}


	protected Object getDerivedDescription (String type)
	{
		return null;
	}


	protected Object getDefaultDescription (String type)
	{
		Object d;
		if ((defaultDescription != null)
			&& ((d = defaultDescription.getDescription (type)) != null))
		{
			return d;
		}
		return Described.NAME.equals (type) ? getName ()
			: Utils.isStringDescription (type)
			? getDescription (Described.NAME) : null;
	}


	protected Object getDescriptionImpl (String type)
	{
		if (description != null)
		{
			return description.getDescription (type);
		}
		I18NBundle b = getI18NBundle ();
		if (b != null)
		{
			try
			{
				return b.getObject (getAbsoluteName () + '.' + type);
			}
			catch (MissingResourceException e)
			{
			}
		}
		return null;
	}

	protected Map getParentMap ()
	{
		return null;
	}


	@Override
	public synchronized AttributeAccessor getAccessor (Attribute attribute)
	{
		AttributeAccessor a = super.getAccessor (attribute);
		if (a == null)
		{
			if (attributes != null)
			{
				if ((a = attributes.getAccessor (attribute)) != null)
				{
					return a;
				}
			}
			else
			{
				attributes = new AccessorMap (false);
			}
			a = new AdditionalAccessor (attribute);
			attributes.add (a);
		}
		return a;
	}


	@Override
	public synchronized AttributeAccessor getAccessor (String name)
	{
		AttributeAccessor a = super.getAccessor (name);
		if ((a == null) && (attributes != null))
		{
			a = attributes.find (name);
		}
		return a;
	}


	@Override
	public synchronized Attribute[] getAttributes ()
	{
		Attribute[] a = super.getAttributes ();
		if ((attributes != null) && (attributes.size () != 0))
		{
			a = attributes.getAttributes (a);
		}
		return a;
	}


	@Override
	public Object get (Object key, Object defaultValue)
	{
		if (key instanceof Attribute)
		{
			key = ((Attribute) key).getKey ();
		}
		Object o = super.get (key, DEFAULT_VALUE);
		if (o == DEFAULT_VALUE)
		{
			if (getParentMap () != null)
			{
				o = getParentMap ().get (key, DEFAULT_VALUE);
			}
			if ((o == DEFAULT_VALUE) && (key instanceof String))
			{
				Option opt = Option.get (this, (String) key);
				if (opt != null)
				{
					o = opt.getObject ();
					if (o == null)
					{
						o = DEFAULT_VALUE;
					}
				}
			}
			if (o == DEFAULT_VALUE)
			{
				if ((key instanceof String)
					&& (key.equals (NAME) || key.equals (SHORT_DESCRIPTION)
						|| key.equals (TITLE) || key.equals (ICON)))
				{
					return getDescription ((String) key);
				}
				return defaultValue;
			}
		}
		return o;
	}
	
	
	public void setOption (String key, Object value)
	{
		Option opt = Option.get (this, key);
		if (opt != null)
		{
			opt.setOptionValue (value);
		}
	}


	public Item initPluginDescriptor (PluginDescriptor plugin)
	{
		if (this.pluginDescriptor == null)
		{
			this.sourceType = ST_PLUGIN;
			this.pluginDescriptor = plugin;
		}
		return this;
	}


	public final PluginDescriptor getPluginDescriptor ()
	{
		return pluginDescriptor;
	}


	public ClassLoader getClassLoader ()
	{
		return (pluginDescriptor != null) ? pluginDescriptor.getClassLoader ()
			: de.grogra.pf.boot.Main.getLoaderForAll ();
	}


	public final boolean isActivated ()
	{
		return activated;
	}


	public final ObjectList deriveItems (ItemVisitor callback, Object info)
	{
		Object oldSrc = DERIVED_SOURCE.get ();
		DERIVED_SOURCE.set (this);
		try
		{
			callback.visit (this, info);
			if (derivedItems != null)
			{
				for (int i = derivedItems.size () - 1; i >= 0; i--)
				{
					Item d = (Item) derivedItems.get (i);
					if ((d == d.getNType ().getRepresentative ())
						|| ((d instanceof Directory)
							&& ((Directory) d).autoGenerated))
					{
						derivedItems.remove (i);
					}
					else
					{
						d.sourceType = ST_DERIVED;
					}
				}
			}
		}
		finally
		{
			DERIVED_SOURCE.set (oldSrc);
		}
		return derivedItems;
	}


	public static final void removeDerivedItems (Registry r, ObjectList items)
	{
		if ((items != null) && !items.isEmpty ())
		{
			synchronized (r.getWriteLock ())
			{
				r.beginXA ();
				try
				{
					synchronized (r.getLock ())
					{
						for (int i = items.size () - 1; i >= 0; i--)
						{
							Item d = (Item) items.get (i);
							Item p = (Item) d.getAxisParent ();
							if (p != null)
							{
								d.removeFromChain (r.getTransaction ());
							}
						}
					}
					while (!items.isEmpty ())
					{
						((Item) items.pop ()).deactivate ();
					}
				}
				finally
				{
					r.commitXA (false);
				}
			}
		}
	}


	private static final ItemVisitor ACTIVATOR = new ItemVisitor ()
	{
		@Override
		public void visit (Item item, Object info)
		{
			item.activateImpl ();
		}
	};


	public final void activate ()
	{
		assert !activated;
		activated = true;
		if (getName () == null)
		{
			setName (((Item) getAxisParent ()).getUniqueName ("_", false));
		}
		deriveItems (ACTIVATOR, null);
	}


	public final void deactivate ()
	{
		if (activated)
		{
			activated = false;
			deactivateImpl ();
		}
		removeDerivedItems (getRegistry (), derivedItems);
	}


	protected void activateImpl ()
	{
	}


	protected void deactivateImpl ()
	{
	}


	final void setRegistry (Registry r)
	{
		registry = r;
	}


	@Override
	public final Registry getRegistry ()
	{
		if (registry == null)
		{
			Node p = getAxisParent ();
			if (p != null)
			{
				registry = ((Item) p).getRegistry ();
			}
		}
		return registry;
	}


	public boolean validate ()
	{
		return true;
	}


	public final Item getItem (CharSequence key)
	{
		if (key != null)
		{
			for (Item n = (Item) getBranch (); n != null;
				 n = (Item) n.getSuccessor ())
			{
				if (Utils.contentEquals (key, n.getName ()))
				{
					return n;
				}
			}
		}
		return null;
	}


	final Item getItem (CharSequence key, int begin, boolean dir)
	{
	searchItem:
		for (Item n = (Item) getBranch (); n != null;
			 n = (Item) n.getSuccessor ())
		{
			if (n.getName () != null)
			{
				int pos = begin;
				String nn = n.getName ();
				for (int i = 0; i < nn.length (); i++)
				{
					if (pos >= key.length ())
					{
						continue searchItem;
					}
					char c = key.charAt (pos++);
					if (c == '\\')
					{
						if (pos == key.length ())
						{
							continue searchItem;
						}
						c = key.charAt (pos++);
					}
					if (c != nn.charAt (i))
					{
						continue searchItem;
					}
				}
				if ((pos == key.length ())
					|| (dir && (pos < key.length ())
						&& (key.charAt (pos) == '/')))
				{
					return n;
				}
			}
		}
		return null;
	}


	public boolean isEditable (Field field)
	{
		return false;
	}


	public boolean equals (ManageableType.Field field, Object o1, Object o2)
	{
		return (o1 == o2) || ((o1 != null) && o1.equals (o2));
	}


/*	public void fieldModified (PersistenceField field, XAThreadState t)
	{
		super.fieldModified (field, t);
		Registry r = getRegistry ();
		if (r != null)
		{
			r.fieldModified (this, field, t);
		}
	}
* /

	public void edgeSetModified (EdgeSet set, int old, GraphThreadState t)
	{
		super.edgeSetModified (set, old, t);
		Registry r = getRegistry ();
		if (r != null)
		{
			r.fieldModified (this, field, t);
		}
	}
* /

	final int updateTreeCount ()
	{
		treeCount = 1;
		for (Item n = (Item) getFirstChild (); n != null;
			 n = (Item) n.getNextSibling ())
		{
			treeCount += n.updateTreeCount ();
		}
		return treeCount;
	}
*/

	final void updateChildren (boolean recursive)
	{
		oldChildren.clear ();
		for (Item i = (Item) getBranch (); i != null;
			 i = (Item) i.getSuccessor ())
		{
			oldChildren.add (i);
			if (recursive)
			{
				i.updateChildren (true);
			}
		}
	}


	public final Item[] getPath ()
	{
		int n = 0;
		for (Node p = this; p != null; p = p.getAxisParent ())
		{
			n++;
		}
		Item[] a = new Item[n];
		for (Node p = this; p != null; p = p.getAxisParent ())
		{
			a[--n] = (Item) p;
		}
		return a;
	}


	final TreeModelEvent createTreeModelEvent (Item child)
	{
		return new TreeModelEvent (this, getPath (),
								   (child == null) ? null
								   : new int[] {child.getIndex ()},
								   (child == null) ? null
								   : new Object[] {child});
	}


	public final void beginXA ()
	{
		Registry r = getRegistry ();
		if (r != null)
		{
			r.beginXA ();
		}
	}


	public final Transaction getTransaction ()
	{
		Registry r = getRegistry ();
		return (r != null) ? r.getTransaction () : null;
	}


	public final void commitXA (boolean activateItems)
	{
		Registry r = getRegistry ();
		if (r != null)
		{
			r.commitXA (activateItems);
		}
	}


	public final Item addUserItem (Item item)
	{
		return add (item.makeUserItem (false));
	}


	public final Item addUserItemWithUniqueName (Item item, String name)
	{
		return addWithUniqueName (item.makeUserItem (false), name, true);
	}


	public final Item add (Item item)
	{
		return add (Integer.MAX_VALUE, item);
	}


	public final Item addWithUniqueName (Item item, String name, boolean human)
	{
		item.setName (getUniqueName ((item.getName () != null) ? item.getName () : name,
									 human));
		return add (item);
	}


	public final Item add (int index, Item item)
	{
		synchronized (getWriteLock ())
		{
			beginXA ();
			synchronized (getLock ())
			{
				insertBranchNode (index, item, getTransaction ());
			}
			commitXA (true);
		}
		return this;
	}


	public void substitute (Item prev)
	{
		synchronized (getWriteLock ())
		{
			beginXA ();
			synchronized (getLock ())
			{
				Edge f;
				for (Edge e = prev.getFirstEdge (); e != null; e = f)
				{
					f = e.getNext (prev);
					Node s = e.getSource (), t = e.getTarget ();
					int b = e.getEdgeBits ();
					e.remove (null);
					if (s == prev)
					{
						addEdgeBitsTo (t, b, getTransaction ());
					}
					else
					{
						s.addEdgeBitsTo (this, b, getTransaction ());
					}
				}
			}
			commitXA (true);
		}
	}


	public final void remove ()
	{
		synchronized (getWriteLock ())
		{
			beginXA ();
			synchronized (getLock ())
			{
				removeFromChain (getTransaction ());
			}
			commitXA (false);
		}
	}


	protected final void set (ManageableType.Field field, int[] indices, Object value)
	{
		synchronized (getWriteLock ())
		{
			beginXA ();
			synchronized (getLock ())
			{
				field.setObject (this, indices, value, getTransaction ());
			}
			commitXA (false);
		}
	}


	public Item findFirst (ItemCriterion c, Object info, boolean resolve)
	{
		if (c.isFulfilled (this, info))
		{
			return this;
		}
		Item r = resolve ? resolveLink (this) : this;
		if (r != this)
		{
			if (r == null)
			{
				return null;
			}
			if (c.isFulfilled (r, info))
			{
				return r;
			}
		}
		for (r = (Item) r.getBranch (); r != null;
			 r = (Item) r.getSuccessor ())
		{
			Item i = r.findFirst (c, info, resolve);
			if (i != null)
			{
				return i;
			}
		}
		return null;
	}


	public Item[] findAll (ItemCriterion c, Object info, boolean resolve)
	{
		ObjectList v = new ObjectList (20);
		findAll (c, info, v, resolve);
		if (v.size == 0)
		{
			return ITEM_0;
		}
		Item[] a = new Item[v.size];
		v.toArray (a);
		return a;
	}


	private void findAll (ItemCriterion c, Object info, ObjectList v, boolean resolve)
	{
		if (c.isFulfilled (this, info))
		{
			v.add (this);
		}
		Item r = resolve ? resolveLink (this) : this;
		if (r != this)
		{
			if (r == null)
			{
				return;
			}
			if (c.isFulfilled (r, info))
			{
				v.add (r);
			}
		}
		for (r = (Item) r.getBranch (); r != null;
			 r = (Item) r.getSuccessor ())
		{
			r.findAll (c, info, v, resolve);
		}
	}


	public void forAll (ItemCriterion c, Object info,
						ItemVisitor cb, Object cbInfo, boolean resolve)
	{
		if ((c == null) || c.isFulfilled (this, info))
		{
			cb.visit (this, cbInfo);
		}
		Item r = resolve ? resolveLink (this) : this;
		if (r != this)
		{
			if (r == null)
			{
				return;
			}
			if ((c == null) || c.isFulfilled (r, info))
			{
				cb.visit (r, cbInfo);
			}
		}
		Item next;
		for (r = (Item) r.getBranch (); r != null; r = next)
		{
			next = (Item) r.getSuccessor ();
			r.forAll (c, info, cb, cbInfo, resolve);
		}
	}


	public Item findMax (ItemComparator c, Object info, boolean resolve)
	{
		return findMax (c, null, info, resolve);
	}


	private Item findMax (ItemComparator c, Item i, Object info, boolean resolve)
	{
		if (c.compare (this, i, info) > 0)
		{
			i = this;
		}
		Item r = resolve ? resolveLink (this) : this;
		if (r != this)
		{
			if (r == null)
			{
				return i;
			}
			if (c.compare (r, i, info) > 0)
			{
				i = r;
			}
		}
		for (r = (Item) r.getBranch (); r != null;
			 r = (Item) r.getSuccessor ())
		{
			String n = r.getName ();
			if ((n == null) || (n.length () == 0) || (n.charAt (0) != '.'))
			{
				i = r.findMax (c, i, info, resolve);
			}
		}
		return i;
	}


	public static Item findFirst (Item root, ItemCriterion c, Object info, boolean resolve)
	{
		return (root == null) ? null : root.findFirst (c, info, resolve);
	}


	public static Item findFirst (RegistryContext ctx, String root,
								  ItemCriterion c, Object info, boolean resolve)
	{
		return findFirst (resolveItem (ctx, root), c, info, resolve);
	}


	public static Item[] findAll (Item root, ItemCriterion c, Object info, boolean resolve)
	{
		return (root == null) ? Item.ITEM_0 : root.findAll (c, info, resolve);
	}


	public static Item[] findAll (RegistryContext ctx, String root,
								  ItemCriterion c, Object info, boolean resolve)
	{
		return findAll (resolveItem (ctx, root), c, info, resolve);
	}


	public static void forAll (Item root, ItemCriterion c, Object info,
							   ItemVisitor cb, Object cbInfo, boolean resolve)
	{
		if (root != null)
		{
			root.forAll (c, info, cb, cbInfo, resolve);
		}
	}


	public static void forAll (RegistryContext ctx, String root,
							   ItemCriterion c, Object info,
							   ItemVisitor cb, Object cbInfo, boolean resolve)
	{
		forAll (resolveItem (ctx, root), c, info, cb, cbInfo, resolve);
	}


	public static Item findMax (Item root, ItemComparator c, Object info, boolean resolve)
	{
		return (root == null) ? null : root.findMax (c, info, resolve);
	}


	public static Item findMax (RegistryContext ctx, String root,
								ItemComparator c, Object info, boolean resolve)
	{
		return findMax (resolveItem (ctx, root), c, info, resolve);
	}


/*
	public final Item getItem (int index)
	{
		for (Node n = getFirstChild (); n != null; n = n.getNextSibling ())
		{
			Item i = ((Item) n).resolveLink ();
			if ((i != null) && (--index < 0))
			{
				return i;
			}
		}
		return null;
	}


	public final int getItemCount ()
	{
		int c = 0;
		for (Node n = getFirstChild (); n != null; n = n.getNextSibling ())
		{
			if (((Item) n).resolveLink () != null)
			{
				c++;
			}
		}
		return c;
	}


	public final int indexOf (Item child)
	{
		int index = 0;
		for (Node n = getFirstChild (); n != null; n = n.getNextSibling ())
		{
			Item i = ((Item) n).resolveLink ();
			if (i != null)
			{
				if (i == child)
				{
					return index;
				}
				index++;
			}
		}
		return -1;
	}
*/

	public String getUniqueName (String name, boolean human)
	{
		if (getItem (name) == null)
		{
			return name;
		}
		int i = human ? 2 : 0;
		StringBuffer b = new StringBuffer (name);
		if (human)
		{
			b.append (' ');
		}
		int len = b.length ();
		while (true)
		{
			b.append (i++);
			if (getItem (b) == null)
			{
				return b.toString ();
			}
			b.setLength (len);
		}
	}


	@Override
	protected String paramString ()
	{
		return getName ();
	}

/*
	public SharedObjectProvider getProvider (ItemReferenceable object)
	{
		return registry;
	}


	public void write (ItemReferenceable object, PersistenceOutput out)
		throws java.io.IOException
	{
		out.writeUTF (getAbsoluteKey ());
	}


	public Shareable clone (ItemReferenceable object,
							SharedObjectProvider.Binding soBinding)
	{
		if (soBinding == null)
		{
			return object.clone ((Item) null);
		}
		else
		{
			SharedObjectProvider p = soBinding.lookup ("/");
			if (!(p instanceof Registry))
			{
				throw new FatalPersistenceException ("No registry found in "
													 + soBinding);
			}
			else
			{
				Shareable s = ((Registry) p).getShareable (getAbsoluteKey ());
				if (s != null)
				{
					return s;
				}
				throw new AssertionError ((Object) "Not yet implemented");// !!
			}
		}
	}
*/


	String getXMLElementName ()
	{
		return getClass ().getName ();
	}


	protected void getAttributes (AttributesImpl attr, XMLPersistenceWriter w)
		throws SAXException
	{
		try
		{
			w.getAttributes (this, attr);
		}
		catch (java.io.IOException e)
		{
			throw new SAXException (e);
		}
	}


	void accept (XMLSerializer s) throws SAXException
	{
		AttributesImpl attr = s.startElement (this);
		if (attr != null)
		{
			if (sourceType == ST_USER)
			{
				if (attr.getLength() == 0)
				{
					getAttributes (attr, s.writer);
				}
				s.setAttributes (attr);
			}
			for (Item i = (Item) getBranch (); i != null;
				 i = (Item) i.getSuccessor ())
			{
				i.accept (s);
			}
			s.endElement ();
		}
	}


	protected boolean readAttribute (String uri, String name, String value)
		throws SAXException
	{
		if ("".equals (uri))
		{
			if ("name".equals (name))
			{
				setName (value);
				return true;
			}
			else if ("i18nkey".equals (name))
			{
				return true;
			}
			else if ("optionCategory".equals (name))
			{
				optionCategory = "true".equals (value);
				if (optionCategory)
				{
					isDirectory = true;
				}
				return true;
			}
			else if ("dir".equals (name))
			{
				isDirectory = "true".equals (value);
				return true;
			}
			else if ("elements".equals (name))
			{
				ObjectList v = new ObjectList ();
				int i = 0;
			getElements:
				while (true)
				{
					char c;
					while (Character.isWhitespace (c = value.charAt (i))
						   || ("{},".indexOf (c) >= 0))
					{
						if (++i == value.length ())
						{
							break getElements;
						}
					}
					int b = i;
					while (!(Character.isWhitespace (c = value.charAt (i))
							 || ("{},".indexOf (c) >= 0)))
					{
						i++;
					}
					v.add (value.substring (b, i));
				}
				elements = new String[i = v.size () / 2][];
				while (--i >= 0)
				{
					elements[i] = new String[] {(String) v.get (2 * i),
												(String) v.get (2 * i + 1)}; 
				}
				return true;
			}
		}
		return false;
	}


	protected Item createItem (PersistenceBindings pb, String name)
		throws InvocationTargetException, InstantiationException,
		IllegalAccessException, ClassNotFoundException
	{
		if (elements != null)
		{
			for (int i = elements.length - 1; i >= 0; i--)
			{
				if (elements[i][0].equals (name))
				{
					return (Item) pb.typeForName (elements[i][1], true).newInstance (); 
				}
			}
		}
		Item p = (Item) getAxisParent ();
		return (p == null) ? null : p.createItem (pb, name);
	}


	public void addRequiredFiles (Collection list)
	{
	}


	public void addPluginPrerequisites (Collection list)
	{
		addPluginPrerequisite (list, getClass ());
		if (getPluginDescriptor () != null)
		{
			list.add (getPluginDescriptor ());
		}
	}


	protected static void addPluginPrerequisite (Collection list, Class cls)
	{
		ClassLoader l = cls.getClassLoader ();
		if (l instanceof PluginClassLoader)
		{
			list.add (((PluginClassLoader) l).getPluginDescriptor ());
		}
	}


	public boolean hasEditableOptions ()
	{
		return Option.hasEditableOptions (this);
	}


	public boolean isOptionCategory ()
	{
		return optionCategory;
	}
}
