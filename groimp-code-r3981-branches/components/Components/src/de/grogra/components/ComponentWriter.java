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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.GSWriter;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.OutputStreamSource;
import de.grogra.pf.registry.Registry;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;

public class ComponentWriter extends FilterBase implements OutputStreamSource
{
	public static final IOFlavor FLAVOR
		= new IOFlavor (new MimeType ("application/x-grogra-component+zip", null),
						IOFlavor.OUTPUT_STREAM, null);


	public ComponentWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}


	@Override
	public void write (OutputStream out) throws IOException
	{
		Registry r = (Registry) ((ObjectSource) source).getObject ();
		FileSystem fs = r.getFileSystem ();
		Collection files = r.getFiles ();
		fs.removeNonlistedAttributes (files);
		if (fs instanceof MemoryFileSystem)
		{
			((MemoryFileSystem) fs).removeNonlistedFiles (files);
		}
		else
		{
			r.substituteFileSystem (fs = new MemoryFileSystem (IO.PROJECT_FS));
		}
		GSWriter.write (r, fs, "project.gs");
		fs.writeJar (out);
	}

}
