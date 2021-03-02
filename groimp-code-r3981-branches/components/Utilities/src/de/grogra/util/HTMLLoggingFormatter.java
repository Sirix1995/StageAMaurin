
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

import java.awt.Dimension;
import java.util.logging.*;
import de.grogra.icon.*;

public class HTMLLoggingFormatter extends Formatter
{
	protected final Dimension iconSize;
	protected final I18NBundle i18n;

	
	public HTMLLoggingFormatter (I18NBundle i18n, Dimension iconSize)
	{
		this.i18n = i18n;
		this.iconSize = iconSize;
	}

	
	@Override
	public String format (LogRecord log)
	{
		StringBuffer buffer = new StringBuffer ("<br>");
		int p = buffer.length ();
		Utils.formatDateAndName (log, buffer);
		Utils.escapeForXML (buffer, p);
		buffer.append ("<h3>");
		Level l = log.getLevel ();
		int lv = l.intValue ();
		String key;
		if (lv < Level.WARNING.intValue ())
		{
			key = "log.info";
		}
		else if (lv < Level.SEVERE.intValue ())
		{
			key = "log.warning";
		}
		else
		{
			key = "log.severe";
		}
		IconSource is = (IconSource)
			((l instanceof Described)
			 ? ((Described) l).getDescription (Described.ICON)
			 : i18n.getObject (key + ('.' + Described.ICON)));
		if (is != null)
		{
			Icon i = is.getIcon (iconSize, 0);
			if ((i != null) && (i.getImageSource () != null))
			{
				buffer.append ("&nbsp;<img src=\"").append (i.getImageSource ())
					.append ("\" alt=\"\">&nbsp;&nbsp;&nbsp;");
			}
		}
		p = buffer.length ();
		buffer.append ((l instanceof Described)
					   ? ((Described) l).getDescription (Described.NAME)
					   : Utils.get (i18n, key, Described.NAME,
					   				l.getLocalizedName ()));
		Utils.escapeForXML (buffer, p);
		buffer.append ("</h3>");
		if (log.getMessage ().length () > 0)
		{
			p = buffer.length ();
			buffer.append (log.getMessage ());
			if (log.getMessage ().startsWith ("<html>"))
			{
				buffer.delete (p, p + 6);
				p = buffer.lastIndexOf ("</html>");
				if (p >= 0)
				{
					buffer.delete (p, p + 7);
				}
			}
			else
			{
				Utils.escapeForXML (buffer, p);
				for (int i = buffer.length () - 1; i >= p; i--)
				{
					if (buffer.charAt (i) == '\n')
					{
						buffer.replace (i, i + 1, "<br>");
					}
				}
			}
			buffer.append ("<br>");
		}
		Throwable t = Utils.getMainException (log.getThrown ());
		if (t instanceof DetailedException)
		{
			buffer.append (((DetailedException) t)
						   .getDetailedMessage (true));
		}
		else if (t instanceof UserException)
		{
			p = buffer.length ();
			buffer.append (t.getLocalizedMessage ());
			Utils.escapeForXML (buffer, p);
		}
		else if (t != null)
		{
			Throwable u = t;
			while (u != null)
			{
				p = buffer.length ();
				String s = u.getClass ().getName ();
				buffer.append (s.substring (s.lastIndexOf ('.') + 1));
				s = u.getLocalizedMessage ();
				if (s != null)
				{
					buffer.append (": ").append (s);
				}
				Utils.escapeForXML (buffer, p);
				buffer.append ("<br>");
				u = u.getCause ();
				if (u != null)
				{
					buffer.append ("Caused by ");
				}
			}
			buffer.append ("<br>Stack Trace:<br>");
			p = buffer.length ();
			buffer.append (Utils.getStackTrace (t));
			Utils.escapeForXML (buffer, p);
			int nextNewLine = buffer.length ();
			for (int i = buffer.length () - 1; i >= p; i--)
			{
				boolean newLine = buffer.charAt (i) == '\n';
				if (newLine || (i == p))
				{
					if (buffer.charAt (nextNewLine - 1) == ')')
					{
						int j = buffer.lastIndexOf ("(", nextNewLine - 1);
						if (j >= i)
						{
							int k = buffer.lastIndexOf (":", nextNewLine - 1);
							if (k >= j)
							{
								String url = buffer.substring (j + 1, k);
								String lineStr = buffer.substring (k + 1, nextNewLine - 1);
								try
								{
									int line = Math.max (Integer.valueOf (lineStr) - 1, 0);
									buffer.insert (nextNewLine - 1, "</a>");
									buffer.insert (j + 1, "<a href=\"" + url + '#' + line + "\">");
								}
								catch (NumberFormatException e)
								{
								}
							}
						}
					}
				}
				if (newLine)
				{
					buffer.replace (i, i + 1, "<br>&nbsp;&nbsp;&nbsp;&nbsp;");
					nextNewLine = i;
				}
			}
		}
		return buffer.append ("<br>").toString ();
	}


	@Override
	public String getHead (Handler h)
	{
		return "<html><body>";
    }


	@Override
	public String getTail (Handler h)
	{
		return "</body></html>";
	}

}
