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

package de.grogra.http;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.grogra.util.Utils;

public final class Request
{
	private final String method;
	private final String uri;
	private final String version;
	private final Map headerFields;
	private final byte[] content;

	public Request (String method, String uri, String version, Map headerFields, byte[] content)
	{
		this.method = method;
		this.uri = uri;
		this.version = version;
		this.headerFields = headerFields;
		this.content = content;
	}

	public static Request parse (BufferedInputStream in) throws IOException
	{
		skipWS (in);
		String method;
		try
		{
			method = readToken (in, -1);
		}
		catch (EOFException e)
		{
			return null;
		}
		String uri = readToken (in, -1);
		String version = readToken (in, -1);
		consumeCRLF (in);

		HashMap fields = new HashMap ();

		parseFields: while (true)
		{
			in.mark (1);
			int ch = in.read ();
			in.reset ();
			switch (ch)
			{
				case '\n':
				case '\r':
					break parseFields;
			}
			parseField (in, fields);
		}

		consumeCRLF (in);

		int length = -1;
		try
		{
			length = Integer.parseInt ((String) fields.get (Server.CONTENT_LENGTH));
		}
		catch (RuntimeException e)
		{
		}
		byte[] content;
		if (length >= 0)
		{
			content = new byte[length];
			Utils.readFully (in, content);
		}
		else
		{
			content = null;
		}
		return new Request (method, uri, version, fields, content);
	}

	private static void consumeCRLF (BufferedInputStream in) throws IOException
	{
		int c = in.read ();
		switch (c)
		{
			case '\n':
				return;
			case '\r':
				in.mark (1);
				if (in.read () != '\n')
				{
					in.reset ();
				}
				return;
			default:
				throw new IOException ("Illegal request: #"
					+ Integer.toHexString (c) + " instead of CRLF");
		}
	}

	private static void skipWS (BufferedInputStream in) throws IOException
	{
		while (true)
		{
			in.mark (1);
			int c = in.read ();
			if ((c < 0) || !Character.isWhitespace ((char) c))
			{
				in.reset ();
				return;
			}
		}
	}

	private static String readToken (BufferedInputStream in, int stop)
			throws IOException
	{
		skipWS (in);
		StringBuffer buf = new StringBuffer ();
		while (true)
		{
			in.mark (1);
			int c = in.read ();
			if ((c < 0) || (c == stop) || Character.isWhitespace ((char) c))
			{
				in.reset ();
				if ((c < 0) && (buf.length () == 0))
				{
					throw new EOFException ();
				}
				return buf.toString ();
			}
			buf.append ((char) c);
		}
	}

	private static void parseField (BufferedInputStream in, HashMap fields)
			throws IOException
	{
		String name = readToken (in, ':').toLowerCase ();
		if (in.read () != ':')
		{
			throw new IOException ("Expected :");
		}
		StringBuffer buf = new StringBuffer ();
		boolean lws = true;
		while (true)
		{
			in.mark (1);
			int c = in.read ();
			switch (c)
			{
				case '\n':
				case '\r':
					in.reset ();
					consumeCRLF (in);
					in.mark (1);
					c = in.read ();
					in.reset ();
					if ((c == ' ') || (c == '\t'))
					{
						break;
					}
					String s = (String) fields.get (name);
					if (s != null)
					{
						buf.insert (0, ',').insert (0, s);
					}
					fields.put (name, buf.toString ().trim ());
					return;
				case -1:
					throw new EOFException ();
				default:
					if (Character.isWhitespace ((char) c))
					{
						if (!lws)
						{
							lws = true;
							buf.append (' ');
						}
					}
					else
					{
						lws = false;
						buf.append ((char) c);
					}
					break;
			}
		}
	}

	public String getMethod ()
	{
		return method;
	}

	public String getURI ()
	{
		return uri;
	}

	public String getPath ()
	{
		int i = uri.indexOf ('?');
		return (i >= 0) ? uri.substring (0, i) : uri;
	}

	public String getQuery ()
	{
		int i = uri.indexOf ('?');
		return (i >= 0) ? uri.substring (i + 1) : null;
	}

	public String getVersion ()
	{
		return version;
	}

	public String getHeaderField (String name)
	{
		return (String) headerFields.get (name);
	}

	public byte[] getContent ()
	{
		return content;
	}

	@Override
	public String toString ()
	{
		StringBuffer buf = new StringBuffer ();
		buf.append (method + " " + uri + " " + version);
		for (Iterator i = headerFields.entrySet ().iterator (); i.hasNext ();)
		{
			Map.Entry e = (Map.Entry) i.next ();
			buf.append ("\n  ").append (e.getKey ()).append (" = ").append (
				e.getValue ());
		}
		if (content != null)
		{
			buf.append ("\n  content = ");
			int n = Math.min (content.length, 500);
			for (int i = 0; i < n; i++)
			{
				String hex = Integer.toHexString ((char) content[i]);
				if (hex.length () == 1)
				{
					buf.append ('0');
				}
				buf.append (hex);
				buf.append (' ');
			}
			if (n < content.length)
			{
				buf.append ("...");
			}
			buf.append ("\n  content = ");
			for (int i = 0; i < n; i++)
			{
				buf.append ((char) content[i]);
			}
			if (n < content.length)
			{
				buf.append ("...");
			}
		}
		buf.append ('\n');
		return buf.toString ();
	}
}
