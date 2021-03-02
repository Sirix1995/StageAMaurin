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
import java.io.Reader;

/**
 * @author yong
 * @since  2011-11-18
 */
class MTGLineReader
{
	private BufferedReader mtgBufferedReader;
	
	/**
	 * MTGLineReader constructor.
	 * @param reader Reader instance from the MTGTranslator, which obtained it from the MTGFilter. 
	 * File resources are loaded into FilterSource objects in GroIMP. 
	 * The MTGFilter is constructed with a member FilterSource variable. 
	 */
	public MTGLineReader(Reader reader) throws Throwable
	{
        this.mtgBufferedReader = new BufferedReader(reader);
	}
	
	/**
	 * Reads a line of the .mtg file.
	 * @return next line read from the .mtg file or null if there is nothing left in file to be read. 
	 */
	public String readLine()
	{
		String mtgFileString;
		try
		{
			mtgFileString = mtgBufferedReader.readLine();
			return mtgFileString;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	
	/**
	 * Closes the buffers and readers of the .mtg file.
	 * @return Code specifying success or error in closing file. 
	 */
	public int close()
	{
		try
		{
			mtgBufferedReader.close();
			return 0;
		} catch (Throwable t)
		{
			return MTGError.MTG_TRANSLATOR_ERROR_MTG_BUFFER_CLOSE;
		}
		
	}
}
