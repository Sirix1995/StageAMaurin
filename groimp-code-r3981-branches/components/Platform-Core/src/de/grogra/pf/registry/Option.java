
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

import java.util.*;
import java.util.prefs.*;
import org.xml.sax.SAXException;
import de.grogra.util.Quantity;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.*;

public class Option extends Value implements de.grogra.util.KeyDescription
{
	private boolean editable = true;
	private boolean transient_ = false;

	private Quantity quantity;

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Option ());
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
		return new Option ();
	}

//enh:end

	public Option ()
	{
		super (null, false);
	}


	@Override
	boolean storesAsString ()
	{
		return true;
	}

	@Override
	protected boolean getTypeFromObject ()
	{
		return false;
	}

	public static Option createNoneditableOption (String name, Object value)
	{
		Option o = new Option ();
		o.setName (name);
		o.editable = false;
		o.setOptionValue (value);
		return o;
	}


	public boolean isEditable ()
	{
		return editable;
	}


	public boolean isTransient ()
	{
		return transient_;
	}

	
	public static void setPreference (Item item, String value)
	{
		StringBuffer b = new StringBuffer ();
		Preferences p = getPreferences (item, b, true, false);
		if (p != null)
		{
			p.put (b.toString (), value);
		}
	}
	
	
	public static String getPreference (Item item)
	{
		StringBuffer b = new StringBuffer ();
		Preferences p = getPreferences (item, b, false, false);
		return (p != null) ? p.get (b.toString (), null) : null;
	}


	private static Preferences getPreferences
		(Item item, StringBuffer outKey, boolean force, boolean onlyOutKey)
	{
		outKey.setLength (0);
		do
		{
			if (outKey.length () > 0)
			{
				outKey.insert (0, '.');
			}
			outKey.insert (0, item.getName ());
			item = (Item) item.getAxisParent ();
		} while (item instanceof OptionGroup);
		if (onlyOutKey)
		{
			return null;
		}
		String path = "/de/grogra/options" + item.getAbsoluteName ();
		Preferences root = Preferences.userRoot ();
		try
		{
			return (force || root.nodeExists (path)) ? root.node (path) : null;
		}
		catch (BackingStoreException e)
		{
			e.printStackTrace ();
			return null;
		}
	}


	public boolean belongsToGroup (Item item)
	{
		return (item == getAxisParent ())
			|| (!(item instanceof OptionGroup)
				&& (get (item, getKey ()) == this));
	}


	public String getKey ()
	{
		StringBuffer n = new StringBuffer ();
		getPreferences (this, n, false, true);
		return n.toString ();
	}


	public de.grogra.reflect.Type getType ()
	{
		return getObjectType ();
	}


	public Quantity getQuantity ()
	{
		return quantity;
	}


	public void setOptionValue (Object value)
	{
		set (value$FIELD, null, value);
		if (editable)
		{
			setPreference (this, getValueAsString (new XMLPersistenceWriter (null, null)));
		}
	}


	@Override
	protected void activateImpl ()
	{
		if (!editable)
		{
			return;
		}
		String s = getPreference (this);
		if (s != null)
		{
			setValueAsString (s);
		}
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws SAXException
	{
		if ("".equals (uri))
		{ 
			if ("editable".equals (name))
			{
				editable = "true".equals (value);
				return true;
			}
			else if ("transient".equals (name))
			{
				transient_ = "true".equals (value);
				if (transient_)
				{
					editable = false;
				}
				return true;
			}
			else if ("quantity".equals (name))
			{
				quantity = Quantity.get (value);
				return true;
			}
		}
		return super.readAttribute (uri, name, value);
	}


	public static Option get (Item item, String option)
	{
		int begin = 0, end = option.indexOf ('/');
		if (end < 0)
		{
			end = option.length ();
		}
		while (item != null)
		{
			Item n = (Item) item.getBranch ();
			item = null;
			while (n != null)
			{
				if (n.getName ().regionMatches (0, option, begin, end - begin))
				{
					if (n instanceof Option)
					{
						return (Option) n;
					}
					else if (n instanceof OptionGroup)
					{
						n = (Item) n.getBranch ();
						begin = end + 1;
						if (begin >= option.length ())
						{
							return null;
						}
						end = option.indexOf ('/', begin);
						if (end < 0)
						{
							end = option.length ();
						}
						continue;
					}
				}
				if ((n instanceof Link)
					&& ((Link) n).hasName ("options"))
				{
					item = ((Link) n).resolveLink ((Link) n);
				}
				n = (Item) n.getSuccessor ();
			}
		}
		return null;
	}


	public static boolean hasEditableOptions (Item item)
	{
		for (Node n = item.getBranch (); n != null; n = n.getSuccessor ())
		{
			if (((n instanceof Option) && ((Option) n).isEditable ())
				|| ((n instanceof OptionGroup)
					&& ((OptionGroup) n).hasEditableOptions ())
				|| ((n instanceof Link)
					&& ((Link) n).hasName ("options")
					&& ((Link) n).resolveLink (item).hasEditableOptions ()))
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Returns all options in the subtree starting with <code>i</code>
	 * which are editable or transient (the latter only if
	 * <code>includeTransient</code> is <code>true</code>). Links
	 * having the name <code>&quot;option&quot;</code> are resolved. 
	 * 
	 * @param i the item to start with
	 * @param includeTransient include transient options?
	 * @return an array of all editable options in the subtree starting with <code>i</code>
	 */
	public static Option[] getEditableOptions
		(Item i, final boolean includeTransient)
	{
		final ArrayList list = new ArrayList ();
		i.forAll (null, null, new ItemVisitor ()
		{
			public void visit (Item i, Object info)
			{
				if ((i instanceof Link) && i.hasName ("options"))
				{
					i = i.resolveLink (i);
					if (i != null)
					{
						i.forAll (null, null, this, info, false);
					}
				}
				else if (i instanceof Option)
				{
					Option option = (Option) i;
					if (option.isEditable ()
						|| (includeTransient && option.isTransient ()))
					{
						list.add (option);
					}
				}
			}
		}, null, false);
		return (Option[]) list.toArray (new Option[list.size ()]);
	}

}
