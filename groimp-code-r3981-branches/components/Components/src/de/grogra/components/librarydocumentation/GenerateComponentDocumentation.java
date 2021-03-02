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

package de.grogra.components.librarydocumentation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parses the input stream and generates a latex style documentation  
 * out of it and writes it to the output stream. 
 * 
 * @author mh
 *
 */
public class GenerateComponentDocumentation {

	/**
	 * 
	 * @param inputStream
	 * @param outputStreamBody
	 */
	public static void generate (InputStream inputStream, BufferedWriter outputStreamBody) {
		try	{
			generateBody(inputStream, outputStreamBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @param outputStreamBody
	 * @throws IOException
	 */
	private static void generateBody (InputStream inputStream, BufferedWriter outputStreamBody) throws IOException {
		//TODO parse input stream and generate the latex documentation
		BufferedReader inputStreamR = new BufferedReader(new InputStreamReader(inputStream));
		String buf;
		while ((buf = inputStreamR.readLine()) != null) {
			if(buf.contains ("=")) {
				outputStreamBody.write(buf+"\n");
			}
		}
	}

	
}
