/*
 * Copyright (C) 2011 Abteilung Oekoinformatik, Biometrie und Waldwachstum, 
 * Buesgeninstitut, Georg-August-Universitaet Göttingen
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

package de.grogra.mtg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.imp.View;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.rgg.RGGRoot;
import de.grogra.rgg.model.Runtime;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGLoader extends FilterBase implements ObjectSource, Workbench.Loader
{

	Registry m_registry;
	SourceFile m_sourceFile;
	
	public MTGLoader(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor (IOFlavor.PROJECT_LOADER);
	}

	
	public Object getObject ()
	{
		return this;
	}	
	
	private Node rootNode = null;

	public void loadRegistry (Registry registry) throws IOException
	{
		this.m_registry = registry;
		
		File f = ((FileSource) source).getInputFile ();
		m_registry.initFileSystem (new LocalFileSystem (IO.PROJECT_FS, f.getParentFile ()));

		//create root nodes
		m_registry.setEmptyGraph ();
		
		m_sourceFile = new SourceFile (IO.toSystemId (m_registry.getFileSystem (),
				f), IO.getRoot (source).getFlavor ().getMimeType ());
		m_registry.getDirectory ("/project/objects/files", null).addUserItem (m_sourceFile);
		
		//MTG filter for parsing and loading MTG files
		MTGFilter filter = new MTGFilter(item, source);
		
		//Call to getObject() function loads the MTG data into graph structure
		rootNode = (Node)filter.getObject();
	}

	public void loadGraph (Registry r) throws IOException
	{
		//Link the loaded graph to the registry project graph a.k.a. main graph
		((Node) r.getProjectGraph ().getRoot (Graph.MAIN_GRAPH)).appendBranchNode (rootNode);
		
		//Some functionalities (e.g. the console) retrieve the graph via the context thread referenced to by the runtime instance.
		//Hence it is necessary to point the Runtime instance's "current graph" to the project graph in the registry loaded.
		Runtime.INSTANCE.setCurrentGraph (r.getProjectGraph ());
	}

	public void loadWorkbench (Workbench wb)
	{
		wb.setProperty (de.grogra.imp3d.View3D.INITIAL_CAMERA, null);
		wb.setProperty (Workbench.INITIAL_LAYOUT, "/ui/layouts/rgg");
		
		m_sourceFile.showLater(wb);
			
		//DEBUG - temporary code - to implement dynamic number of scales shown on GUI.
		wb.setProperty(new String("scale count"), new Integer(3));
		//END DEBUG
	}

}
