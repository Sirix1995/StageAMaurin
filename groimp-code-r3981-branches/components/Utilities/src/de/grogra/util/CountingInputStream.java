
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

public class CountingInputStream extends FilterInputStream
{
	public interface Monitor
	{
		void bytesRead (long bytes);

		void streamClosed ();
	}


	protected long bytesRead = 0;
	private long markRead;
	private long resolution, nextToMonitor;
	private Monitor monitor;


	public CountingInputStream (InputStream in)
	{
		super (in);
	}


	public void setMonitor (Monitor monitor, long resolution)
	{
		this.monitor = monitor;
		this.resolution = Math.max (resolution, 1);
	}


	@Override
	public void mark (int readlimit)
	{
		in.mark (readlimit);
		markRead = bytesRead;
	}


	@Override
	public void reset () throws IOException
	{
		in.reset ();
		bytesRead = markRead;
		if (monitor != null)
		{
			monitor.bytesRead (bytesRead);
			nextToMonitor = bytesRead + resolution;
		}
	}


	@Override
	public int read () throws IOException
	{
		int i = in.read ();
		if (i >= 0)
		{
			bytesRead++;
			if ((monitor != null) && (bytesRead >= nextToMonitor))
			{
				monitor.bytesRead (bytesRead);
				nextToMonitor = bytesRead + resolution;
			}
		}
		return i;
	}


	@Override
	public int read (byte[] b) throws IOException
	{
		int i = in.read (b);
		if (i >= 0)
		{
			bytesRead += i;
			if ((monitor != null) && (bytesRead >= nextToMonitor))
			{
				monitor.bytesRead (bytesRead);
				nextToMonitor = bytesRead + resolution;
			}
		}
		return i;
	}


	@Override
	public int read (byte[] b, int off, int len) throws IOException
	{
		int i = in.read (b, off, len);
		if (i >= 0)
		{
			bytesRead += i;
			if ((monitor != null) && (bytesRead >= nextToMonitor))
			{
				monitor.bytesRead (bytesRead);
				nextToMonitor = bytesRead + resolution;
			}
		}
		return i;
	}


	@Override
	public long skip (long n) throws IOException
	{
		long l = in.skip (n);
		bytesRead += l;
		if ((monitor != null) && (bytesRead >= nextToMonitor))
		{
			monitor.bytesRead (bytesRead);
			nextToMonitor = bytesRead + resolution;
		}
		return l;
	}


	public long getBytesRead ()
	{
		return bytesRead;
	}


	@Override
	public void close () throws IOException
	{
		in.close ();
		if (monitor != null)
		{
			monitor.streamClosed ();
		}
	}

}
