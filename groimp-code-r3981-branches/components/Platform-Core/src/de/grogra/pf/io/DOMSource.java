
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

import java.io.*;
import org.w3c.dom.*;

/**
 * This subinterface of <code>FilterSource</code> has to be implemented
 * by filter sources whose flavor supports DOM trees
 * ({@link de.grogra.pf.io.IOFlavor#DOM}). It represents the data
 * as a DOM tree rooted at its <code>Document</code>.
 * 
 * @author Ole Kniemeyer
 */
public interface DOMSource extends FilterSource
{
	/**
	 * Returns the data as a DOM document.
	 * 
	 * @return the data
	 */
	Document getDocument () throws IOException, DOMException;
}
