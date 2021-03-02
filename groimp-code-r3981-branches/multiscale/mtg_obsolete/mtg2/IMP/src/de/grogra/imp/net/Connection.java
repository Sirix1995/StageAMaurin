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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import de.grogra.util.IOWrapException;
import de.grogra.xl.util.LongHashMap;
import de.grogra.xl.util.ObjectList;

public class Connection implements Runnable
{
	static final int MAGIC = 0x8a61c12f;
	static final int MESSAGE = 0x12fe0712;
	static final int RESPONSE = 0x8e54025e;
	static final int CLOSE = 0xc105e123;
	static final int AVAILABLE = 0xaba17abe;

	static final byte[] END_MARK = {-108, 48, 16, 69, -11, -27, -25, 61, 29,
			-47, 72, -93, 113, 12, -14, -31};
	static final int END_MARK_LENGTH = 16;

	private final Socket socket;
	private final DataOutputStream out;
	private final DataInputStream in;

	private long currentMessage = -1;
	private long nextMessage = 0;

	private LongHashMap<ResponseHandler> responseHandlers = new LongHashMap<ResponseHandler> ();
	private ObjectList<MessageHandler> messageHandlers = new ObjectList<MessageHandler> ();

	private volatile boolean close;
	private boolean started;

	public Connection (Socket socket) throws IOException
	{
		this.socket = socket;
		this.out = new DataOutputStream (new BufferedOutputStream (socket
			.getOutputStream ()));
		this.out.writeInt (MAGIC);
		this.out.flush ();
		this.in = new DataInputStream (new BufferedInputStream (socket
			.getInputStream ()));
		consumeInt (MAGIC);
	}

	public Socket getSocket ()
	{
		return socket;
	}

	public void addMessageHandler (MessageHandler h)
	{
		synchronized (messageHandlers)
		{
			messageHandlers.add (h);
		}
	}

	public void removeMessageHandler (MessageHandler h)
	{
		synchronized (messageHandlers)
		{
			messageHandlers.remove (h);
		}
	}

	public void consumeInt (int value) throws IOException
	{
		int i = in.readInt ();
		if (i != value)
		{
			throw new IOException ("Expected " + value + " instead of " + i);
		}
	}

	public void consumeByte (int value) throws IOException
	{
		int i = in.readByte ();
		if (i != value)
		{
			throw new IOException ("Expected " + value + " instead of " + i);
		}
	}

	private void begin (int code) throws IOException
	{
		try
		{
			while (currentMessage >= 0)
			{
				wait ();
			}
		}
		catch (InterruptedException e)
		{
			throw new IOWrapException (e);
		}
		currentMessage = nextMessage++;
		out.writeInt (code);
		out.writeLong (currentMessage);
	}

	public synchronized long beginMessage (String message, ResponseHandler rh)
			throws IOException
	{
		begin (MESSAGE);
		out.writeUTF (message);
		if (rh != null)
		{
			responseHandlers.put (currentMessage, rh);
		}
		return currentMessage;
	}

	public synchronized SimpleResponseHandler isAvailable (String message)
			throws IOException
	{
		SimpleResponseHandler rh = new SimpleResponseHandler (
			SimpleResponseHandler.INT);
		begin (AVAILABLE);
		out.writeUTF (message);
		responseHandlers.put (currentMessage, rh);
		end (currentMessage);
		return rh;
	}

	public synchronized long beginResponse (long requestId, ResponseHandler rh)
			throws IOException
	{
		begin (RESPONSE);
		out.writeLong (requestId);
		if (rh != null)
		{
			responseHandlers.put (currentMessage, rh);
		}
		return currentMessage;
	}

	public synchronized void end (long messageId) throws IOException
	{
		if (messageId != currentMessage)
		{
			throw new IllegalStateException ();
		}
		currentMessage = -1;
		for (int i = END_MARK_LENGTH - 1; i >= 0; i--)
		{
			out.writeByte (END_MARK[i]);
		}
		out.flush ();
		notifyAll ();
	}

	public DataOutputStream getOut ()
	{
		return out;
	}

	public DataInputStream getIn ()
	{
		return in;
	}

	public void skipRest () throws IOException
	{
		int i = END_MARK_LENGTH - 1;
		while (i >= 0)
		{
			byte b = in.readByte ();
			if (b == END_MARK[i])
			{
				i--;
			}
			else
			{
				i = END_MARK_LENGTH - 1;
				if (b == END_MARK[i])
				{
					i--;
				}
			}
		}
	}

	public void run ()
	{
		try
		{
			while (!close)
			{
				int c = in.readInt ();
				switch (c)
				{
					case CLOSE:
						close = true;
						break;
					case MESSAGE:
					case AVAILABLE:
						long id = in.readLong ();
						String msg = in.readUTF ();
						boolean b = handleMessage (id, msg, c == AVAILABLE);
						if (c == AVAILABLE)
						{
							long m = beginResponse (id, null);
							out.writeInt (b ? 1 : 0);
							end (m);
						}
						else if (!b)
						{
							throw new IOException ("Message " + msg
								+ " could not be handled");
						}
						break;
					case RESPONSE:
						id = in.readLong ();
						long request = in.readLong ();
						ResponseHandler rh;
						synchronized (this)
						{
							rh = responseHandlers.remove (request);
						}
						if (rh == null)
						{
							throw new IOException (
								"No ResponseHandler for request");
						}
						rh.handleResponse (this, request, id);
						break;
					default:
						throw new IOException ("Illegal code " + c);
				}
				for (int i = END_MARK_LENGTH - 1; i >= 0; i--)
				{
					consumeByte (END_MARK[i]);
				}
			}
			out.close ();
			in.close ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	public synchronized void start ()
	{
		if (started)
		{
			return;
		}
		started = true;
		new Thread (this, toString ()).start ();
	}

	public void close ()
	{
		boolean c = close;
		close = true;
		if (!c)
		{
			try
			{
				begin (CLOSE);
				end (currentMessage);
				out.close ();
				socket.close ();
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
		}
	}

	public boolean isClosed ()
	{
		return close;
	}

	protected boolean handleMessage (long messageId, String message,
			boolean checkAvailability) throws IOException
	{
		synchronized (messageHandlers)
		{
			for (int i = 0, n = messageHandlers.size; i < n; i++)
			{
				if (messageHandlers.get (i).handleMessage (this, messageId,
					message, checkAvailability))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString ()
	{
		return "Connection@" + Integer.toHexString (hashCode ()) + '[' + socket
			+ ']';
	}

}
