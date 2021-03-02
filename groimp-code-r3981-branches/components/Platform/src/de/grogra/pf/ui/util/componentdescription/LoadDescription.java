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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.
 */

package de.grogra.pf.ui.util.componentdescription;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JScrollPane;

import de.grogra.pf.io.IO;
import de.grogra.pf.registry.ComponentDescriptionContent;
import de.grogra.pf.registry.ComponentDescriptionXMLReader;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.util.ComponentWrapperImpl;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;

public class LoadDescription {

	private static final String COMPONENT_DESCRIPTION_PANEL_UI_PATH = "/ui/panels/componentdescription";
	
	
	
	/**
	 * 
	 * @param descriptionFile input file
	 * @param file system
	 */
	public static ArrayList<ComponentDescriptionContent> load (Object descriptionFile, FileSystem fs)
	{
		InputStream in = ((MemoryFileSystem) fs).getInputStream(descriptionFile);
		try
		{
			return ComponentDescriptionXMLReader.readXML(in);
		}
		catch (Throwable t)
		{
			System.out.println ("Error in LoadDescription");
			return null;
		}
		finally
		{
			try {
				in.close();
				((MemoryFileSystem) fs).closeQuiet (descriptionFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Object getDescriptionPanel(Context ctx)
	{
		Window w = ctx.getWindow ();
		Object panel = null;
		panel = w.getPanel (COMPONENT_DESCRIPTION_PANEL_UI_PATH);
		
		return panel;
	}
	
	private static ArrayList<ComponentDescriptionContent> getContents(Context ctx)
	{
		//some initialization of variables
		ArrayList<ComponentDescriptionContent> contents=null;
		Registry reg = ctx.getWorkbench().getRegistry();
		
		//load contents from xml
		if(IO.toFile (reg, "pfs:description.txt")!=null) {
			Object o = reg.getProjectFile ("pfs:description.txt");
			if(o!=null)  {			
				contents = load(o, reg.getFileSystem ());
			}
		}			
		
		return contents;
	}
	
	
//	private static JPanel createIndent()
//	{
//		//create indent panel
//		JPanel indent = new JPanel();
//		ComponentDescriptionGridLayout layoutIndent = new ComponentDescriptionGridLayout(1,2);
//		indent.setLayout(layoutIndent);
//		layoutIndent.setHgap(0);
//		layoutIndent.setVgap(0);
//		indent.add(Box.createRigidArea(new Dimension(SPACING_HORIZONTAL_MINOR,SPACING_VERTICAL_MINOR)));
//		
//		return indent;
//	}
	
	
	
	public static void loadPanel(Context ctx)
	{
		try
		{
			//get GUI panel
			Object panel = getDescriptionPanel(ctx);
			//if GUI panel does not exist, return
			if(panel==null)
				return;
			
			//get module contents
			ArrayList<ComponentDescriptionContent> contents = getContents(ctx); //content from ctx
			
			//if no contents loaded from xml and no preloaded content, return
			if(contents==null)
				return;
			
			JScrollPane scrollPane = (JScrollPane)ComponentDescriptor.getDescriptionPanel(contents);
			((Panel)panel).setContent(new ComponentWrapperImpl( scrollPane,null));
		}
		catch(Throwable t)
		{
			System.out.println("Error Loading Descriptions");
		}
	}
}
