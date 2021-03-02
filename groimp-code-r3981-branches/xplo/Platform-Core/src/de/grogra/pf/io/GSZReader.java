
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

import java.io.*;
import de.grogra.vfs.*;
import de.grogra.pf.registry.*;

public class GSZReader extends FilterBase implements ObjectSource, ProjectLoader
{
	public GSZReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.PROJECT_LOADER);
	}


	private FileSystem fs;

	public Object getObject () throws IOException
	{
		fs = new MemoryFileSystem (IO.PROJECT_FS);
		InputStream in = ((InputStreamSource) source).getInputStream ();
		fs.readJar (in, false);
		in.close ();
		return this;
	}


	public void loadRegistry (Registry r) throws IOException
	{
		r.initFileSystem (fs);
		Object proj = fs.getFile ("project.gs");
		if (proj == null)
		{
			throw new IOException
				(IO.I18N.msg ("gsz.file-not-found", "project.gs"));
		}
		FilterSource f = new FileSource (fs, proj, r, null);
		f = IO.createPipeline (f, IOFlavor.REGISTRY_LOADER);
		if (!(f instanceof ObjectSource))
		{
			throw new AssertionError ("gsz-pipeline = " + f);
		}
		((RegistryLoader) ((ObjectSource) f).getObject ()).loadRegistry (r);
	}


	public void loadGraph (Registry r) throws IOException
	{
		GSReader.loadGraph (fs, r);
	}

}
