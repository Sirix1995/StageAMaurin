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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import de.grogra.graph.impl.Node;
import de.grogra.grogra.DTDTokenizer;
import de.grogra.imp.IMP;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FileTypeItem;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.VirtualFileReaderSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.SourceFile;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGFilter extends FilterBase implements ObjectSource 
{
	MTGTranslator mtgTranslator;
	String filename;

	public MTGFilter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(IOFlavor.NODE);
	}
	
	public MTGFilter(FileSource source) {
		super(null, source);
		setFlavor(IOFlavor.NODE);
	}

	public Object getObject(FileSource fs) throws IOException {
		// tokenizer = new DTDTokenizer();
		//initDTDParameter();
		//tokenizer.setSource(fs.getReader(), fs.getSystemId());
		
		// Erstellen des String fÃ¼r die DTD-Datei (Pfad und Dateiname)
		//String dtdFile = System.getProperty("user.home") + System.getProperty ("file.separator") + fs.getSystemId ().substring (4);
		//File tmpFile = createTmpFile(dtdFile, ((ByteArrayOutputStream) fs.getFile ()).toString ());
		//Object o = getObjectImpl(tokenizer, dtdFile);
		//deleteTmpFile(tmpFile);
		try {
			mtgTranslator = new MTGTranslator(fs.getReader());
			
			//Begin translation
			if(mtgTranslator.translateMTGFile()!=MTGError.MTG_TRANSLATOR_TRANSLATE_SUCCESSFUL)
			{
				//get line number where error occurred
				int errorLine = mtgTranslator.getLineCounter();
				String errorMessage =mtgTranslator.getErrorMessage();
				
				//show error Message
				JOptionPane.showMessageDialog(null, "Unable to load MTG file. Please check syntax at line: " + String.valueOf(errorLine) + ". " + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				
				return null;
			}
			
			return mtgTranslator.getMTGRootNode();
		}
		catch (Throwable t) {
			return null;
		} 
	}
	
	@Override
	public Object getObject()
	{
		VirtualFileReaderSource src = (VirtualFileReaderSource)getSource();

		try {
			//Instantiate translator
			mtgTranslator = new MTGTranslator(src.getFileSystem().getReader(src.getFile()));
			
			//Begin translation
			if(mtgTranslator.translateMTGFile()!=MTGError.MTG_TRANSLATOR_TRANSLATE_SUCCESSFUL)
			{
				//get line number where error occurred
				int errorLine = mtgTranslator.getLineCounter();
				String errorMessage =mtgTranslator.getErrorMessage();
				
				//show error Message
				JOptionPane.showMessageDialog(null, "Unable to load MTG file. Please check syntax at line: " + String.valueOf(errorLine) + ". " + errorMessage, "MTG Loading", JOptionPane.WARNING_MESSAGE);
				
				return null;
			}
			
			//source file path
			String mtgFile = source.toString().substring(source.toString().indexOf("[")+1, source.toString().length()-1);
			if(mtgFile.charAt(0) == '~')
				mtgFile = System.getProperty("user.home") +  mtgFile.substring(1);
			
			//Return root node of graph generated from MTG data
			//If MTG data is empty, returned node will be null object
			return mtgTranslator.getMTGRootNode();
			
		} catch (Throwable t) {
			return null;
		} 
	}
}
