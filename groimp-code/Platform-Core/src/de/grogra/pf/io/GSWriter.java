
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.grogra.pf.registry.Executable;
import de.grogra.pf.registry.ItemCriterion;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.XMLSerializer;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.vfs.MemoryFileSystem;

public class GSWriter extends FilterBase implements FileWriterSource
{
	public static final IOFlavor FLAVOR
		= new IOFlavor (new MimeType ("application/x-grogra-project", null),
						IOFlavor.FILE_WRITER, null);


	public GSWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}


	public static void write (Registry reg, FileSystem fs, String projectFile, XMLSerializer.Filter filter)
		throws IOException
	{
		Collection plugins = new HashSet ();
		PluginCollector pc = new PluginCollector (plugins);

		IO.writeXML (new GraphXMLSource (reg.getProjectGraph (), reg, pc),
					 fs, "graph.xml", GraphXMLSource.MIME_TYPE);

		// hook before writing fs to disk
		StringMap m = new StringMap ().putObject ("registry", reg);
		m.put("filesystem", fs);
		m.put("projectfile", projectFile);
		Executable.runExecutables (reg.getRootRegistry (), "/hooks/saving",
			reg, m);
		
		IOFlavor dom = new IOFlavor (Registry.MIME_TYPE, IOFlavor.DOM, null);
		DOMSource ds = new StreamAdapter (reg.createXMLSource (plugins, filter), dom);
		Document doc = ds.getDocument ();
		Element proj = doc.getDocumentElement ();
		proj.setAttribute ("graph", "graph.xml");
		Node r = proj.getFirstChild ();
		for (Iterator it = plugins.iterator (); it.hasNext (); )
		{
			PluginDescriptor pd = (PluginDescriptor) it.next ();
			Element importPlugin = doc.createElementNS (Registry.NAMESPACE, "import");
			importPlugin.setAttribute ("plugin", pd.getName ());
			importPlugin.setAttribute ("version", pd.getPluginVersion ());
			proj.insertBefore (importPlugin, r);
		}
		IO.writeXML (new DOMSourceImpl (doc, ds.getSystemId (), dom, reg, null),
				  	 fs, projectFile, Registry.MIME_TYPE);
	}


	public void write (File out) throws IOException
	{
		File root = out.getParentFile ();
		Registry r = (Registry) ((ObjectSource) source).getObject ();
		FileSystem fs = r.getFileSystem ();
		Collection files = r.getFiles ();
		if (fs instanceof MemoryFileSystem)
		{
			((MemoryFileSystem) fs).removeNonlistedFiles (files);
		}
		fs.removeNonlistedAttributes (files);
		if (!((fs instanceof LocalFileSystem)
			&& fs.equals (root, fs.getRoot ())))
		{
			r.substituteFileSystem (fs = new LocalFileSystem (IO.PROJECT_FS, root));
		}

		write (r, fs, out.getName (), null);

		File f = new File (root, "META-INF");
		if (!f.isDirectory ())
		{
			if (!f.mkdir ())
			{
				throw new IOException ();
			}
		}
		BufferedOutputStream mout = new BufferedOutputStream
			(new FileOutputStream (new File (f, "MANIFEST.MF")));
		try
		{
			fs.getManifest ().write (mout);
		}
		finally
		{
			mout.flush ();
			mout.close ();
		}
	}

}
