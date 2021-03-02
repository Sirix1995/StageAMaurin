
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

package de.grogra.pf.io;

import java.io.IOException;
import java.io.OutputStream;
import de.grogra.util.MimeType;
import de.grogra.pf.registry.*;
import de.grogra.vfs.*;

public class GSZWriter extends FilterBase implements OutputStreamSource
{
	public static final IOFlavor FLAVOR
		= new IOFlavor (new MimeType ("application/x-grogra-project+zip", null),
						IOFlavor.OUTPUT_STREAM, null);


	public GSZWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}


	public void write (OutputStream out) throws IOException
	{
		Registry r = (Registry) ((ObjectSource) source).getObject ();
		FileSystem fs = r.getFileSystem ();
		java.util.Collection files = r.getFiles ();
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
