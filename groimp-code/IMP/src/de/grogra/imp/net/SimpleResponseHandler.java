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

package de.grogra.imp.net;

import java.io.IOException;

import de.grogra.util.IOWrapException;

public class SimpleResponseHandler implements ResponseHandler
{
	public static final int INT = 0;
	public static final int LONG = 1;
	public static final int STRING = 2;
	public static final int USER = 3;

	private final int type;

	private boolean haveResult;
	private int intResult;
	private long longResult;
	private String stringResult;

	public SimpleResponseHandler (int type)
	{
		this.type = type;
	}

	public synchronized void handleResponse (Connection cx, long requestId, long responseId) throws IOException
	{
		haveResult = true;
		notifyAll ();
		switch (type)
		{
			case INT:
				intResult = cx.getIn ().readInt ();
				break;
			case LONG:
				longResult = cx.getIn ().readLong ();
				break;
			case STRING:
				stringResult = cx.getIn ().readUTF ();
				break;
			case USER:
				handleUserResponse (cx, requestId, responseId);
				break;
			default:
				throw new IOException ("Illegal type " + type);
		}
	}

	protected void handleUserResponse (Connection cx, long requestId, long responseId) throws IOException
	{
	}

	public synchronized void waitForResult () throws InterruptedException
	{
		while (!haveResult)
		{
			wait ();
		}
	}

	public void waitForResultWrapInterruption () throws IOException
	{
		try
		{
			waitForResult ();
		}
		catch (InterruptedException e)
		{
			throw new IOWrapException (e);
		}
	}

	public int getInt () throws InterruptedException
	{
		if (type != INT)
		{
			throw new IllegalStateException ();
		}
		waitForResult ();
		return intResult;
	}

	public void consume (int value) throws InterruptedException, IOException
	{
		int i = getInt ();
		if (i != value)
		{
			throw new IOException ("Expected " + value + " instead of " + i);
		}
	}

	public long getLong () throws InterruptedException
	{
		if (type != LONG)
		{
			throw new IllegalStateException ();
		}
		waitForResult ();
		return longResult;
	}

	public String getString () throws InterruptedException
	{
		if (type != STRING)
		{
			throw new IllegalStateException ();
		}
		waitForResult ();
		return stringResult;
	}

}
