
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

package de.grogra.pf.ui.registry;

import de.grogra.pf.registry.*;
import de.grogra.pf.registry.expr.*;
import de.grogra.pf.ui.*;

public final class ResourceDirectory extends Item
{
	private static final int CREATE_EXPLORER_MASK = 1 << Item.USED_BITS;
	private static final int CREATE_PROJECT_DIRECTORY_MASK = 1 << Item.USED_BITS + 1;
	public static final int USED_BITS = Item.USED_BITS + 2;

	// boolean createExplorer
	//enh:field type=bits(CREATE_EXPLORER_MASK)

	// boolean createProjectDirectory
	//enh:field type=bits(CREATE_PROJECT_DIRECTORY_MASK)

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field createExplorer$FIELD;
	public static final NType.Field createProjectDirectory$FIELD;

	static
	{
		$TYPE = new NType (new ResourceDirectory ());
		$TYPE.addManagedField (createExplorer$FIELD = new NType.BitField ($TYPE, "createExplorer", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, CREATE_EXPLORER_MASK));
		$TYPE.addManagedField (createProjectDirectory$FIELD = new NType.BitField ($TYPE, "createProjectDirectory", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, CREATE_PROJECT_DIRECTORY_MASK));
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
		return new ResourceDirectory ();
	}

//enh:end


	private ResourceDirectory ()
	{
		this (null);
	}


	public ResourceDirectory (String key)
	{
		super (key);
		setDirectory ();
		bits |= CREATE_EXPLORER_MASK | CREATE_PROJECT_DIRECTORY_MASK;
	}


	public static void installExplorers (Registry reg)
	{
		installExplorers0 (resolveItem (reg, "/objects"));
	}


	private static Item getDirectory (Item root, Item source, PluginDescriptor pd)
	{
		Item p = (Item) source.getAxisParent ();
		if (p == null)
		{
			return root;
		}
		root = getDirectory (root, p, pd);
		p = root.getItem (source.getName ());
		if (p != null)
		{
			return p;
		}
		p = new Directory (source.getName (), true).initPluginDescriptor (pd);
		p.setDefaultDescription (source);
		root.add (p);
		return p;
	}


	private static void installExplorers0 (Item d)
	{
		if (d instanceof ResourceDirectory)
		{
			ResourceDirectory c = (ResourceDirectory) d;
			String abs = c.getAbsoluteName ();
			PluginDescriptor pd = c.getPluginDescriptor ();
			if ((c.bits & CREATE_EXPLORER_MASK) != 0)
			{
				Item i = new ExplorerFactory
					(c.getName (), c.getBaseName (), abs, "/project" + abs);
				i.initPluginDescriptor (pd).setDefaultDescription (c);
				getDirectory (c.getRegistry ().getDirectory ("/ui/panels", pd),
							  (Item) c.getAxisParent (), pd)
					.add (i.add (new Exists (".available", "/project")
								 .initPluginDescriptor (pd))
						  .add (new Link ("menu", "/ui/explorer/menu")
								.initPluginDescriptor (pd)));
			}
			if ((c.bits & CREATE_PROJECT_DIRECTORY_MASK) != 0)
			{
				c.insertBranchNode (0, new Link ("project", "/project" + abs));
			}
		}
		else
		{
			for (d = (Item) d.getBranch (); d != null;
				 d = (Item) d.getSuccessor ())
			{
				installExplorers0 (d);
			}
		}
	}


	public static void configure (Registry reg)
	{
		configure0 (reg, resolveItem (reg, "/objects"));
	}


	private static void configure0 (Registry reg, Item d)
	{
		if (d instanceof ResourceDirectory)
		{
			ResourceDirectory c = (ResourceDirectory) d;
			if ((c.bits & CREATE_PROJECT_DIRECTORY_MASK) != 0)
			{
				d = reg.getDirectory ("/project" + c.getAbsoluteName (),
									  c.getPluginDescriptor ());
				if (d.hasName ((String) d.getDescription (NAME)))
				{
					d.setDescription
						(NAME, UI.I18N.msg ("projectresource",
												 c.getDescription (NAME)));
				}
				d.setDefaultDescription (c);
			}
		}
		else
		{
			for (d = (Item) d.getBranch (); d != null;
				 d = (Item) d.getSuccessor ())
			{
				configure0 (reg, d);
			}
		}
	}


	public Item getProjectDirectory (RegistryContext ctx)
	{
		return resolveItem (ctx, "/project" + getAbsoluteName ());
	}


	public String getBaseName ()
	{
		String s = (String) getDescription ("Base");
		if (s == null)
		{
			s = getName ();
			if (s.endsWith ("s"))
			{
				s = s.substring (0, s.length () - 1);
			}
		}
		return s;
	}


	public static ResourceDirectory get (Item child)
	{
		while ((child != null) && !(child instanceof ResourceDirectory))
		{
			child = (Item) child.getAxisParent ();
		}
		return (ResourceDirectory) child;
	}

}
