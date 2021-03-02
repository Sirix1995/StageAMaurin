
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

package de.grogra.util;

import java.io.*;

public class XByteArrayOutputStream extends ByteArrayOutputStream
{

	public XByteArrayOutputStream (int size)
	{
		super (size); 
	}


	public byte[] getBuffer ()
	{
		return buf;
	}


	public ByteArrayInputStream createInputStream ()
	{
		return new ByteArrayInputStream (buf, 0, count);
	}


	public void read (InputStream in) throws IOException
	{
		byte[] b = new byte[0x4000];
		int i;
		while ((i = in.read (b, 0, b.length)) >= 0)
		{
			write (b, 0, i);
		}
	}

}

