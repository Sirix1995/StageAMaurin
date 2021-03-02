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

package de.grogra.rgg.model;

import java.io.IOException;
import java.io.InputStream;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.InputStreamSource;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;
import de.grogra.xl.compiler.CompilationUnit;
import de.grogra.xl.util.ByteList;

/**
 * An <code>CompiledRGGFilter</code> reads a byte stream representing a JAR file
 * into a {@link CompilationUnit}.
 * 
 * @author Ole Kniemeyer
 */
public class CompiledRGGFilter extends FilterBase implements ObjectSource
{
	/**
	 * Constructs a new filter which transforms the input <code>source</code>
	 * into an instance of {@link CompilationUnit} which can be compiled afterwards.
	 * 
	 * @param item the defining item for this filter within the registry, may be <code>null</code> 
	 * @param source the source from which the input stream of characters will be read
	 * 
	 * @see #getObject
	 * @see #compile()
	 */
	public CompiledRGGFilter (FilterItem item, ReaderSource source)
	{
		super (item, source);
		setFlavor (CompilationFilter.CUNIT_FLAVOR);
	}

	public CompilationUnit getObject () throws IOException
	{
		String s = source.getSystemId ();
		s = s.substring (Math.max (s.lastIndexOf ('/'), s.lastIndexOf ('\\')) + 1);
		InputStream in = ((InputStreamSource) source).getInputStream();
		ByteList bytes = new ByteList();
		byte[] buf = new byte[1024];
		while (true)
		{
			int b = in.read(buf);
			if (b <= 0)
			{
				break;
			}
			bytes.addAll(buf, 0, b);
		}
		return new CompilationUnit (s, bytes.toArray());
	}
}
