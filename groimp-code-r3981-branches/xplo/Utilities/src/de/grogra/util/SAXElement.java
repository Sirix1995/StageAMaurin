
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

package de.grogra.util;

public class SAXElement extends org.xml.sax.helpers.AttributesImpl
{
	public String uri;
	public String name;
	public String qName;
	public SAXElement next, children;


	public void set (String uri, String name, String qName,
					 org.xml.sax.Attributes atts)
	{
		this.uri = uri;
		this.name = name;
		this.qName = qName;
		setAttributes (atts);
	}


	public boolean nameEquals (String uri, String name)
	{
		return name.equals (this.name) && uri.equals (this.uri);
	}


	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer ();
		b.append ('<').append(uri).append (':').append (name);
		for (int i = 0; i < getLength (); i++)
		{
			b.append (' ').append (getURI (i)).append (':')
				.append (getLocalName (i)).append ('=').append (getValue (i));
		}
		return b.append ("/>").toString ();
	}


	public void dumpTree ()
	{
		dumpTree ("");
	}
	
	
	/**
	 * Copy an entire Attributes object. This method overrides
	 * {@link org.xml.sax.helpers.AttributesImpl#setAttributes(org.xml.sax.Attributes)}
	 * in order to fix a bug in Java 1.4.0's implementation.
	 */
	@Override
	public void setAttributes (org.xml.sax.Attributes atts)
	{
		if (atts.getLength () > 0)
		{
			super.setAttributes (atts);
		}
		else
		{
			clear ();
		}
	}


	private void dumpTree (String indent)
	{
		System.out.print (indent);
		System.out.println (this);
		for (SAXElement e = children; e != null; e = e.next)
		{
			e.dumpTree (indent + ' ');
		}
	}
}
