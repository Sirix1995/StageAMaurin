
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

public final class Link extends Item
{
	protected String source;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field source$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Link.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Link) o).source = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Link) o).source;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Link ());
		$TYPE.addManagedField (source$FIELD = new _Field ("source", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new Link ();
	}

//enh:end

	Link ()
	{
		this (null, null);
	}


	public Link (String key, String link)
	{
		super (key);
		this.source = link;
	}


	public Link (Item source)
	{
		this (source.getName (), source.getAbsoluteName ());
	}


	@Override
	public Item resolveLink (RegistryContext ctx)
	{
		return resolveLink (this, source, ctx);
	}


	public static Item resolveLink (Item item, String ref, RegistryContext ctx)
	{
		if (!(ref.charAt (0) == '/'))
		{
			Item p = (Item) item.getAxisParent ();
			int i = 0;
			while (ref.regionMatches (i, "../", 0, 3))
			{
				p = (Item) p.getAxisParent ();
				if (p == null)
				{
					return null;
				}
				i += 3;
			}
			ref = p.getAbsoluteName () + '/' + ref.substring (i);
		}
		return resolveItem (ctx, ref);
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri) && "source".equals (name))
		{
			source = value;
			setNameIfNull (this, value.substring (value.lastIndexOf ('/') + 1));
			return true;
		}
		return super.readAttribute (uri, name, value);
	}


	@Override
	protected Object getDefaultDescription (String type)
	{
		Item i = resolveLink (this);
		return (i != null) ? i.getDescription (type)
			: super.getDefaultDescription (type);
	}


	@Override
	public void addPluginPrerequisites (java.util.Collection list)
	{
		super.addPluginPrerequisites (list);
		Item i = resolveLink (this);
		if (i != null)
		{
			i.addPluginPrerequisites (list);
		}
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",source=" + source;
	}

}
