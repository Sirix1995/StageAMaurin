
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

package de.grogra.pf.ui.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.LookAndFeel;

import org.xml.sax.SAXException;

import de.grogra.pf.registry.Item;
import de.grogra.util.Utils;

public final class LAF extends Item
{
	String create;


	public static final NType $TYPE
		= (NType) new NType (new LAF ()).validate (); 



	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new LAF ();
	}


	LAF ()
	{
		super (null);
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws SAXException
	{
		if ("".equals (uri))
		{
			if ("create".equals (name))
			{
				create = value;
				return true;
			}
		}
		return super.readAttribute (uri, name, value);
	}


	LookAndFeel getLAF () throws ClassNotFoundException, IllegalAccessException,
		InstantiationException, InvocationTargetException,
		NoSuchMethodException
	{
		return (LookAndFeel) Utils.invoke (create, null, getClassLoader ());
	}

}
