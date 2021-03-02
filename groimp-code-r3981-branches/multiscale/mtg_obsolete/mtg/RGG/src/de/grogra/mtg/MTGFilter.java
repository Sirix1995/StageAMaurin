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
import java.io.IOException;
import java.io.Reader;

import javax.swing.JOptionPane;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;
import de.grogra.pf.io.VirtualFileReaderSource;
import de.grogra.turtle.F;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGFilter extends FilterBase implements ObjectSource 
{
	MTGTranslator mtgTranslator;

	public MTGFilter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(IOFlavor.NODE);
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
			
			//Return root node of graph generated from MTG data
			//If MTG data is empty, returned node will be null object
			return mtgTranslator.getMTGRootNode();
			//return mtgTranslator.getRootNode();
			//return new F();
			//return mtgTranslator.getSphere();
			
		} catch (Throwable t) {
			return null;
		} 
	}

}
