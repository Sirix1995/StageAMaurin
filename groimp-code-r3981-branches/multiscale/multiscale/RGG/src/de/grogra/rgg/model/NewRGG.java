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

package de.grogra.rgg.model;

import java.io.*;

import de.grogra.imp.NewProject;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.rgg.Library;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;

public class NewRGG extends NewProject
{

	@Override
	public void run (Object arg, Context ctx)
	{
		while (true)
		{
			String name = ctx.getWindow ().showInputDialog (
				Library.I18N.msg ("newrgg.title"),
				Library.I18N.msg ("newrgg.msg"), "Model");
			if (name == null)
			{
				return;
			}
			name = name.trim ();
			String msg = null;
			String param = null;
			if (name.length () == 0)
			{
				msg = "newrgg.empty-name";
			}
			else if (!Character.isJavaIdentifierStart (name.charAt (0)))
			{
				msg = "newrgg.illegal-start";
				param = String.valueOf (name.charAt (0));
			}
			else
			{
				for (int i = 1; i < name.length (); i++)
				{
					if (!Character.isJavaIdentifierPart (name.charAt (i)))
					{
						msg = "newrgg.illegal-part";
						param = String.valueOf (name.charAt (i));
						break;
					}
				}
			}
			if (msg != null)
			{
				ctx.getWindow ().showDialog (
					Library.I18N.msg ("newrgg.illegal-name"),
					Library.I18N.msg (msg, name, param),
					Window.INFORMATION_MESSAGE);
			}
			else
			{
				super.run (name, ctx);
				return;
			}
		}
	}

	@Override
	protected void configure (Workbench wb, Object arg)
	{
		wb.setProperty (Workbench.INITIAL_LAYOUT, "/ui/layouts/rgg");
		wb.setName ((String) arg);
		Registry r = wb.getRegistry ();
		FileSystem fs = r.getFileSystem ();
		try
		{
			Object file = fs.create (fs.getRoot (), arg + ".rgg", false);
			Writer out = fs.getWriter (file, false);
			Reader in = new BufferedReader (new InputStreamReader (getClass ()
				.getClassLoader ().getResourceAsStream ("de/grogra/rgg/model/NewRGG-Template.rgg"), "ISO-8859-1"));
			int n;
			char[] buf = new char[1024];
			while ((n = in.read (buf)) >= 0)
			{
				out.write (buf, 0, n);
			}
			out.flush ();
			out.close ();
			final SourceFile sf = new SourceFile (IO.toSystemId (r
				.getFileSystem (), file), new MimeType ("text/x-grogra-rgg",
				null));
			r.getDirectory ("/project/objects/files", null).addUserItem (sf);
			sf.showLater (wb);
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

}
