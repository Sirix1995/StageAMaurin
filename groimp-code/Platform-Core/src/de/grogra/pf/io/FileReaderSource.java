
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

/**
 * This subinterface of <code>FilterSource</code> has to be implemented
 * by filter sources whose flavor supports reading from an underlying
 * <code>File</code> ({@link de.grogra.pf.io.IOFlavor#FILE_READER}).
 * It represents the data by a <code>File</code>.
 * 
 * @author Ole Kniemeyer
 */
public interface FileReaderSource extends FilterSource
{
	/**
	 * Returns the file from which the data can be obtained.
	 * 
	 * @return file representing the data
	 */
	File getInputFile ();
}
