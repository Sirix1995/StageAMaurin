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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

import de.grogra.graph.impl.Node;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.VirtualFileReaderSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGFilter extends FilterBase implements ObjectSource 
{
	MTGTranslator mtgTranslator;
	String filename;

	public MTGFilter(FilterItem item, FilterSource source)
	{
		super(item, source);
		setFlavor(IOFlavor.NODE);
	}
	
	public MTGFilter(FileSource source)
	{
		super(null, source);
		setFlavor(IOFlavor.NODE);
	}

	public Object getObject(FileSource fs) throws IOException 
	{
		try {
			//Instantiate translator
			mtgTranslator = new MTGTranslator(fs.getFileSystem().getReader(fs.getFile()),fs.getSystemId());
			
			//Begin translation of headers
			if(mtgTranslator.translateMTGFileHeader()!=MTGError.MTG_TRANSLATOR_TRANSLATE_HEADER_SUCCESSFUL)
			{
				//get line number where error occurred
				int errorLine = mtgTranslator.getLineCounter();
				String errorMessage =mtgTranslator.getErrorMessage();
				
				//show error Message
				JOptionPane.showMessageDialog(null, "Unable to load MTG file headers. Please check syntax at line: " + String.valueOf(errorLine) + ". " + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				
				return null;
			}
			
			//Instantiate module builder
			MTGModuleBuilder mtgBuilder = new MTGModuleBuilder(mtgTranslator.getMTGRootNode(),this.getSystemId(),getGeneratedFileName(fs,false));
			
			//generate xl file and generate xl code and compile generated file
			//if one already exists, it is overwritten
			Object generatedFile = generateFileAndCompile(fs, mtgBuilder);
			if(generatedFile==null)
			{
				String errorMessage = mtgBuilder.getErrorMessage();
				JOptionPane.showMessageDialog(null, "Unable to generate XL modules." + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				return null;
			}

			//Begin translation of MTG file body
			if(mtgTranslator.translateMTGFileBody()!=MTGError.MTG_TRANSLATOR_TRANSLATE_SUCCESSFUL)
			{
				//get line number where error occurred
				int errorLine = mtgTranslator.getLineCounter();
				String errorMessage =mtgTranslator.getErrorMessage();
				
				//show error Message
				JOptionPane.showMessageDialog(null, "Unable to load MTG file headers. Please check syntax at line: " + String.valueOf(errorLine) + ". " + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				
				return null;
			}
			
			//initialize scales selection
			initSelectableScales();
			
			//Return root node of graph generated from MTG data
			return mtgTranslator.getMTGRootNode();
			
		} catch (Throwable t) {
			return null;
		} 
	}
	
	@Override
	public Object getObject()
	{
		VirtualFileReaderSource src = (VirtualFileReaderSource)getSource();

		try {
			//Instantiate translator
			mtgTranslator = new MTGTranslator(src.getFileSystem().getReader(src.getFile()),src.getSystemId());
			
			//Begin translation of headers
			if(mtgTranslator.translateMTGFileHeader()!=MTGError.MTG_TRANSLATOR_TRANSLATE_HEADER_SUCCESSFUL)
			{
				//get line number where error occurred
				int errorLine = mtgTranslator.getLineCounter();
				String errorMessage =mtgTranslator.getErrorMessage();
				
				//show error Message
				JOptionPane.showMessageDialog(null, "Unable to load MTG file headers. Please check syntax at line: " + String.valueOf(errorLine) + ". " + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				
				return null;
			}
			
			//Instantiate module builder
			MTGModuleBuilder mtgBuilder = new MTGModuleBuilder(mtgTranslator.getMTGRootNode(),this.getSystemId(),getGeneratedFileName(src,false));
			
			//generate xl file and generate xl code and compile generated file
			//if one already exists, it is overwritten
			Object generatedFile = generateFileAndCompile(src, mtgBuilder);
			if(generatedFile==null)
			{
				String errorMessage = mtgBuilder.getErrorMessage();
				JOptionPane.showMessageDialog(null, "Unable to generate XL modules." + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				return null;
			}

			//Begin translation of MTG file body
			if(mtgTranslator.translateMTGFileBody()!=MTGError.MTG_TRANSLATOR_TRANSLATE_SUCCESSFUL)
			{
				//get line number where error occurred
				int errorLine = mtgTranslator.getLineCounter();
				String errorMessage =mtgTranslator.getErrorMessage();
				
				//show error Message
				JOptionPane.showMessageDialog(null, "Unable to load MTG file headers. Please check syntax at line: " + String.valueOf(errorLine) + ". " + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				
				return null;
			}
			
			//initialize scales selection
			initSelectableScales();
			
			//Return root node of graph generated from MTG data
			return mtgTranslator.getMTGRootNode();
			
		} catch (Throwable t) {
			return null;
		} 
	}
	
	private void initSelectableScales()
	{
		boolean[] scaleVisible = new boolean[de.grogra.imp.View.SCALE_COUNT];;
		
		for(int i=0; i<de.grogra.imp.View.SCALE_COUNT;++i)
			scaleVisible[i] = false;
		
		Workbench.current().setProperty(MTGKeys.MTG_SCALES_VISIBLE, scaleVisible);
		Workbench.current().setProperty(MTGKeys.MTG_SCALES_CHANGED, new Boolean(false));
	}
	
	/**
	 * Get generated xl file name from mtg file
	 * @param src
	 * @param withExt
	 * @return
	 */
	private String getGeneratedFileName(VirtualFileReaderSource src, boolean withExt)
	{
		Object fileObj = src.getFile();
		String fName = null;
		
		if(src.getFileSystem() instanceof de.grogra.vfs.MemoryFileSystem)
		{
			fName = src.getFileSystem().getName(fileObj);
		}
		else if(src.getFileSystem() instanceof de.grogra.vfs.LocalFileSystem)
		{
			File file = (File)fileObj;
			fName = file.getName();
		}
		
		if(fName.endsWith(".mtg"))
			fName = fName.substring(0, fName.length()-4);
		if(withExt)
			return fName + "-generated.xl";
		else
			return fName + "-generated";
	}
	
	/**
	 * Deletes generated xl file from file system (local or memory file system)
	 * @param src
	 */
	private void deleteGeneratedFile(VirtualFileReaderSource src)
	{
		//get name of generated xl file given the File f
		String generatedFileName = getGeneratedFileName(src,true);
		try {
			
			//get file system - could be LocalFileSystem or MemoryFileSystem
			FileSystem fs = Registry.current().getFileSystem();
			
			//get parent path of file
			Object parent = fs.getParent(src.getFile());
			
			//attempt to get generated file from file system
			Object file = fs.getFile(parent, generatedFileName);
			
			//if generated file exists, delete it
			if(file!=null)
				fs.delete(file);
		} 
		catch (Throwable e) {
			//e.printStackTrace();
			return;
		}
		
	}
	
	/**
	 * Removes generated xl file from project file list (file explorer)
	 * @param src
	 */
	private void removeGeneratedFileFromFileExpl(VirtualFileReaderSource src)
	{
		//get name of generated xl file
		String generatedFileName = getGeneratedFileName(src,true);
		
		//get list of entries in GroIMP's file explorer
		Item dir = Workbench.current().getRegistry().getDirectory ("/project/objects/files", null);
		for(Node n = dir.getBranch(); n != null; n = n.getSuccessor()) {
			if(n instanceof SourceFile) {
				SourceFile sf = (SourceFile) n;
				
				//if generated file is found in file explorer, remove it from file explorer
				if(sf.getName().equals("pfs:"+generatedFileName)) {
					sf.remove();
				}
			}
		}
	}
	
	/**
	 * Generate xl file with the given mtg file. 
	 * Deletes any existing generated xl file from file system and project listing beforehand.
	 * @param src
	 * @param mtgBuilder
	 * @return
	 */
	private Object generateFileAndCompile(VirtualFileReaderSource src, MTGModuleBuilder mtgBuilder)
	{
		try
		{
			FileSystem fs = Registry.current().getFileSystem(); //use current registry's file system
			String generatedFileName = getGeneratedFileName(src,true);
			
			//if file already exists in registry, delete it and remove it from file explorer list
			deleteGeneratedFile(src);
			removeGeneratedFileFromFileExpl(src);
			
			//generate new file
			Object generatedFile = null;
			if(fs instanceof MemoryFileSystem)
				generatedFile = fs.create (fs.getRoot(), generatedFileName, false);
			else
				generatedFile = fs.create(fs.getParent(src.getFile()), generatedFileName, false);
			//Object generatedFile = fs.create (fs.getParent(src.getFile()), generatedFileName, false);
			FileSource fsrc = new FileSource(fs,generatedFile,IO.toSystemId (fs, generatedFile),new MimeType ("text/x-grogra-xl",null), Registry.current(),null);
			OutputStream fsrcOut = fsrc.getOutputStream(false);
			OutputStreamWriter fsrcOutWriter = new OutputStreamWriter(fsrcOut); 
			BufferedWriter generatedWriter = new BufferedWriter(fsrcOutWriter);
			
			//module builder generates xl code that specifies modules required to represent MTG classes
			mtgBuilder.writeTypes(generatedWriter);
			
			//flush and end process
			generatedWriter.flush();
			fsrcOut.flush();
			
			//if file system is memoryfilesystem, must close in order for data to be written in.
			if(fs instanceof MemoryFileSystem)
			{
				((MemoryFileSystem) fs).closeQuiet(generatedFile);
			}
			
			//add generated file to 'file explorer' in project (list of files for project)
			final SourceFile sf = new SourceFile (IO.toSystemId (Registry.current().getFileSystem(), generatedFile), new MimeType ("text/x-grogra-xl",null));
			Registry.current().getDirectory ("/project/objects/files", null).addUserItem (sf);	
			
			//compile generated file
			sf.activate(); 
			return generatedFile;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
}
