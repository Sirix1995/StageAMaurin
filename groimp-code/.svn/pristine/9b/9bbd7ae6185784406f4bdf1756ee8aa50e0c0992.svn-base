
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.xml.sax.helpers.AttributesImpl;

import de.grogra.util.MimeType;
import de.grogra.pf.io.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.vfs.*;
import de.grogra.xl.lang.ObjectToBoolean;

public class GSZCWriter extends FilterBase implements OutputStreamSource
{
	public static final IOFlavor FLAVOR
		= new IOFlavor (new MimeType ("application/x-grogra-project+zip", null),
						IOFlavor.OUTPUT_STREAM, null);


	public GSZCWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}


	public void write (OutputStream out) throws IOException
	{
		Registry r = ((CompiledProject) ((ObjectSource) source).getObject ()).registry;
		byte[] compiled = null;
		Item i = r.getItem("/compiled/rggc");
		if (i instanceof Value)
		{
			compiled = (byte[]) ((Value) i).getObject();
		}
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
		final HashSet<FSFile> removedSourceFiles = new HashSet<FSFile>();
		GSWriter.write (r, fs, "project.gs", new XMLSerializer.Filter() {
			@Override
			public AttributesImpl filter(Item item, AttributesImpl attr)
			{
				if (item instanceof SourceFile)
				{
					SourceFile sf = (SourceFile) item;
					String mime = sf.getMimeType().getMediaType();
					boolean remove = false;
					if (mime.equals("text/x-java") || mime.equals("text/x-grogra-xl") || mime.equals("text/x-grogra-rgg"))
					{
						remove = true;
					}
					else if (mime.equals("application/x-grogra-rgg-compiled"))
					{
						remove = true;
					}
					if (remove)
					{
						boolean first = removedSourceFiles.isEmpty();
						if (first)
						{
							attr.addAttribute ("", "name", "name", "CDATA", "pfs:compiled.rggc");
							attr.addAttribute ("", "mimeType", "mimeType", "CDATA", "application/x-grogra-rgg-compiled");
						}
						removedSourceFiles.add(IO.toFile(item, sf.getName()));
						return first ? attr : null;
					}
				}
				return attr;
			}
		});
		final FileSystem fs2 = fs;
		JarOutputStream j = new JarOutputStream (out, fs.getManifest ());
		fs.writeJar (j, new byte[0x40000], fs.getRoot (), new ObjectToBoolean<Object>() {
			@Override
			public boolean evaluateBoolean(Object x)
			{
				return !removedSourceFiles.contains(new FSFile(fs2, x));
			}
		});
		if (compiled != null)
		{
			ZipEntry z = new ZipEntry ("compiled.rggc");
			z.setSize (compiled.length);
			z.setTime (System.currentTimeMillis());
			j.putNextEntry (z);
			j.write (compiled);
			j.closeEntry ();
		}
		j.flush ();
		j.close ();
	}

}
