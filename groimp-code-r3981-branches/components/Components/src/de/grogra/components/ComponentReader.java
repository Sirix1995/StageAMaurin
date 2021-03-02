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
import java.io.InputStream;

import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.GSReader;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.InputStreamSource;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.io.RegistryLoader;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;

public class ComponentReader extends FilterBase implements ObjectSource, ProjectLoader
{
	public ComponentReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.PROJECT_LOADER);
	}


	private FileSystem fileSystem;

	@Override
	public Object getObject () throws IOException
	{
		fileSystem = new MemoryFileSystem (IO.PROJECT_FS);
		InputStream in = ((InputStreamSource) source).getInputStream ();
		fileSystem.readJar (in, false);
		in.close ();
		return this;
	}


	@Override
	public void loadRegistry (Registry registry) throws IOException
	{
		registry.initFileSystem (fileSystem);
//		Object proje = fs.getFile ("description.txt");
		Object proj = fileSystem.getFile ("project.gs");
		if (proj == null)
		{
			throw new IOException
				(IO.I18N.msg ("zip.file-not-found", "project.gs"));
		}
		FilterSource f = new FileSource (fileSystem, proj, registry, null);
		f = IO.createPipeline (f, IOFlavor.REGISTRY_LOADER);
		if (!(f instanceof ObjectSource))
		{
			throw new AssertionError ("gsz-pipeline = " + f);
		}
		((RegistryLoader) ((ObjectSource) f).getObject ()).loadRegistry (registry);
		
		Workbench wb = Workbench.get (registry);
		wb.setProperty (Workbench.INITIAL_LAYOUT,"/ui/layouts/componentdesigner");
	}


	@Override
	public void loadGraph (Registry registry) throws IOException
	{
		GSReader.loadGraph (fileSystem, registry);
	}

}
