
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Manifest;

import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Registry;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;

public class GSReader extends FilterBase implements ObjectSource, ProjectLoader
{
	public GSReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.PROJECT_LOADER);
	}


	private LocalFileSystem fs;
	private File project;


	public Object getObject () throws IOException
	{
		project = ((FileSource) source).getInputFile ();
		fs = new LocalFileSystem (IO.PROJECT_FS, project.getParentFile ());
		return this;
	}


	public void loadRegistry (Registry r) throws IOException
	{
		r.initFileSystem (fs);
		File m = new File (new File (project.getParentFile (), "META-INF"),
						   "MANIFEST.MF");
		if (m.isFile ())
		{
			BufferedInputStream in = new BufferedInputStream
				(new FileInputStream (m));
			Manifest mf;
			try
			{
				mf = new Manifest (in);
			}
			finally
			{
				in.close ();
			}
			fs.setManifest (mf);
		}
		FilterSource f = new FileSource (fs, project, IO.toSystemId (fs, project), Registry.MIME_TYPE, r,
										 null);
		f = IO.createPipeline (f, IOFlavor.REGISTRY_LOADER);
		if (!(f instanceof ObjectSource))
		{
			throw new AssertionError ("gs-pipeline = " + f);
		}
		((RegistryLoader) ((ObjectSource) f).getObject ()).loadRegistry (r);
	}


	public void loadGraph (Registry r) throws IOException
	{
		loadGraph (fs, r);
	}


	public static void loadGraph (FileSystem fs, Registry r) throws IOException
	{
		String gfName = (String) r.getImportAttribute ("graph");
		if (gfName == null)
		{
			throw new IOException (IO.I18N.msg ("gs.graphfile-not-specified"));
		}
		Object gf = fs.getFile (gfName);
		if (gf == null)
		{
			throw new IOException
				(IO.I18N.msg ("gs.file-not-found", gfName));
		}
		FilterSource f = new FileSource (fs, gf, r, null);
		f = IO.createPipeline (f, IOFlavor.GRAPH_LOADER);
		if (!(f instanceof ObjectSource))
		{
			throw new IOException
				(IO.I18N.msg ("gs.invalid-file-type", gfName));
		}
		((GraphLoader) ((ObjectSource) f).getObject ()).loadGraph (r);
		r.forAll (null, null, new ItemVisitor ()
			{
				public void visit (Item item, Object info)
				{
					if (!item.validate ())
					{
						System.err.println ("Removed " + item);
						item.removeFromChain ();
					}
				}
			}, null, false);
	}

}
