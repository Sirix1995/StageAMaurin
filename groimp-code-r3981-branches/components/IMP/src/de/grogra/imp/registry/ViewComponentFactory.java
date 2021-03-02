
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

package de.grogra.imp.registry;

import de.grogra.imp.View;
import de.grogra.imp.ViewComponent;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.registry.ChoiceGroup;
import de.grogra.pf.ui.registry.UIItem;
import de.grogra.pf.ui.tree.UINodeHandler;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Reflection;
import de.grogra.util.WrapException;

public class ViewComponentFactory extends Item implements UIItem
{
	private String cls;
	private String ui;
	
	private boolean neverAvailable = false;
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new ViewComponentFactory ());
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
		return new ViewComponentFactory ();
	}

//enh:end


	ViewComponentFactory ()
	{
		super (null);
	}

	public String getCLS() //shining
	{
		return cls;
	}

	public void setCLS(String a)
	{
		cls = a;
	}
	
	public ViewComponent createViewComponent (Context ctx)
	{
		try
		{
			ViewComponent c = (ViewComponent) classForName (cls, true).newInstance ();
			c.initFactory (this);
			return c;
		}
		catch (ClassNotFoundException e)
		{
			throw new WrapException (e);
		}
		catch (IllegalAccessException e)
		{
			throw new WrapException (e);
		}
		catch (InstantiationException e)
		{
			throw new WrapException (e);
		}
	}


	public int getUINodeType ()
	{
		return UINodeHandler.NT_ITEM;
	}


	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}


	public boolean isAvailable (Context ctx)
	{
		if (neverAvailable)
		{
			return false;
		}
		if (!Reflection.isSuperclassOrSame
			(ui, ClassAdapter.wrap (ctx.getWorkbench ().getToolkit ().getClass ())))
		{
			return false;
		}
		try
		{
			classForName (cls, true);
			return true;
		}
		catch (Throwable e)
		{
			neverAvailable = true;
			de.grogra.pf.boot.Main.getLogger ().config (e.getMessage ());
			if ((e instanceof Error) && !(e instanceof NoClassDefFoundError))
			{
				e.printStackTrace ();
			}
			return false;
		}
	}


	public boolean isEnabled (Context ctx)
	{
		return true;
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri))
		{
			if ("class".equals (name))
			{
				cls = value;
				return true;
			}
			else if ("ui".equals (name))
			{
				ui = value;
				return true;
			}
		}
		return super.readAttribute (uri, name, value);
	}

	
	public static ViewComponentFactory get (View view, String itemPath)
	{
		Item g = Item.resolveItem (view.getWorkbench (), itemPath);
		Item i = (g instanceof ChoiceGroup) ? ((ChoiceGroup) g).getPropertyValue (view)
			: null;
		if ((i instanceof ViewComponentFactory)
			&& ((ViewComponentFactory) i).isAvailable (view))
		{
			return (ViewComponentFactory) i;
		}
		else
		{
			for (i = (Item) g.getBranch (); i != null; i = (Item) i.getSuccessor ())
			{ 
				if ((i instanceof ViewComponentFactory)
					&& ((ViewComponentFactory) i).isAvailable (view))
				{
					return (ViewComponentFactory) i;
				}
			}
		}
		return null;
	}

}
