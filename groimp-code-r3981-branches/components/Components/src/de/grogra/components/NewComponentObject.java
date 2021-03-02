/*
 * Copyright (C) 2013 GroIMP Developer Team
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

package de.grogra.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.rgg.Library;
import de.grogra.util.I18NBundle;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;

public class NewComponentObject extends NewComponent
{

	@Override
	public void run (Object arg, Context context)
	{
		I18NBundle I18N = I18NBundle.getInstance (this.getClass ());
		while (true)
		{
			String name = context.getWindow ().showInputDialog (
				I18N.getString ("newcomponent.title"),
				I18N.getString ("newcomponent.msg"), I18N.getString ("newcomponent.name"));
			if (name == null)
			{
				return;
			}
			name = name.trim ();
			String msg = null;
			String param = null;
			if (name.length () == 0)
			{
				msg = "newcomponent.empty-name";
			}
			else if (!Character.isJavaIdentifierStart (name.charAt (0)))
			{
				msg = "newcomponent.illegal-start";
				param = String.valueOf (name.charAt (0));
			}
			else
			{
				for (int i = 1; i < name.length (); i++)
				{
					if (!Character.isJavaIdentifierPart (name.charAt (i)))
					{
						msg = "newcomponent.illegal-part";
						param = String.valueOf (name.charAt (i));
						break;
					}
				}
			}
			if (msg == null)
			{
				super.run (name, context);
				return;
			}
			else
			{
				context.getWindow ().showDialog (
					I18N.getString ("newcomponent.illegal-name"),
					Library.I18N.msg (msg, name, param),
					Window.INFORMATION_MESSAGE);
			}
		}
	}

	@Override
	protected void configure (Workbench wb, Object arg)
	{
		wb.setProperty (Workbench.INITIAL_LAYOUT, "/ui/layouts/componentdesign");
		wb.setName (getComponentName ());
		Registry r = wb.getRegistry ();
		FileSystem fs = r.getFileSystem ();
		int n;
		char[] buf = new char[150];
		
		try {
			// empty description file as template
			Object file1 = fs.create (fs.getRoot (), "description.txt", false);
			Writer out1 = fs.getWriter (file1, false);
			Reader in1 = new BufferedReader (new InputStreamReader (getClass ()
				.getClassLoader ().getResourceAsStream ("de/grogra/components/description.txt"), "ISO-8859-1"));						
			while ((n = in1.read (buf)) >= 0) {
				out1.write (buf, 0, n);
			}
			out1.flush ();
			out1.close ();
			final SourceFile sf1 = new SourceFile (IO.toSystemId (r
				.getFileSystem (), file1), MimeType.TEXT_XML);
			
			// model
			Object file2 = fs.create (fs.getRoot (), arg + ".rgg", false);
			Writer out2 = fs.getWriter (file2, false);
			Reader in2 = new BufferedReader (new InputStreamReader (
				getClass ().getClassLoader ().getResourceAsStream (
					"de/grogra/components/NewComponent-Template.rgg"), "ISO-8859-1"));
			while ((n = in2.read (buf)) >= 0) {
				out2.write (buf, 0, n);
			}
			out2.flush ();
			out2.close ();
			final SourceFile sf2 = new SourceFile (IO.toSystemId (r
				.getFileSystem (), file2), new MimeType ("text/x-grogra-rgg", null));			
			
			r.getDirectory ("/project/objects/files", null).addUserItem (sf1);
			r.getDirectory ("/project/objects/files", null).addUserItem (sf2);			
			sf2.showLater (wb);
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

}
