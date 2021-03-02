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

package de.grogra.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Server implements Runnable
{
	public static final String CONTENT_LENGTH = "content-length";
	public static final String CONTENT_TYPE = "content-type";

	public static final String CRLF = "\r\n";

	private final ServerSocket socket;
	private final Logger logger;
	private final Level infoLevel;
	private final Level warningLevel;

	private volatile boolean running = true;
	private Vector clients = new Vector ();

	public Server (ServerSocket socket, Logger logger, Level infoLevel,
			Level warningLevel)
	{
		this.socket = socket;
		this.logger = logger;
		this.infoLevel = infoLevel;
		this.warningLevel = warningLevel;
	}

	public Logger getLogger ()
	{
		return logger;
	}

	public Level getInfoLevel ()
	{
		return infoLevel;
	}

	public Level getWarningLevel ()
	{
		return warningLevel;
	}

	public void run ()
	{
		try
		{
			while (running)
			{
				accept (socket.accept ());
			}
		}
		catch (IOException e)
		{
			if (running)
			{
				logger.log (warningLevel,
					"Error while waiting for connections", e);
			}
		}
	}

	protected void accept (final Socket client)
	{
		new Thread (new Runnable ()
		{
			public void run ()
			{
				clients.add (client);
				try
				{
					logger.log (infoLevel, "Accepting http client " + client);
					BufferedInputStream in = new BufferedInputStream (client
						.getInputStream ());
					Request req;
					while (running && (req = Request.parse (in)) != null)
					{
						logger.log (infoLevel, "Client " + client
							+ ": Handling request\n" + req);
						if (!handleRequest (req, client))
						{
							break;
						}
					}
				}
				catch (IOException e)
				{
					if (running)
					{
						logger
							.log (warningLevel,
								"Error in communication with http client "
									+ client, e);
					}
				}
				logger.log (Level.CONFIG, "Closing http client " + client);
				try
				{
					clients.remove (client);
					if (running)
					{
						client.close ();
					}
				}
				catch (IOException e)
				{
					if (running)
					{
						logger.log (warningLevel, "Error while closing "
							+ client, e);
					}
				}
			}
		}, client.toString ()).start ();
	}

	public static void writeResponse (int code, String reason, String mimeType,
			byte[] content, boolean closeConnection, OutputStream os)
			throws IOException
	{
		PrintStream out = new PrintStream (os);
		out.print ("HTTP/1.1 ");
		out.print (code);
		out.print (' ');
		out.print (reason);
		out.print (CRLF);

		if (closeConnection)
		{
			out.print ("Connection: close" + CRLF);
		}

		out.print (CONTENT_LENGTH + ": ");
		out.print (content.length);
		out.print (CRLF);

		out.print (CONTENT_TYPE + ": ");
		out.print (mimeType);
		out.print (CRLF);

		out.print (CRLF);

		out.write (content);
		out.flush ();

		os.flush ();
	}

	public boolean isClosed ()
	{
		return !running;
	}

	public void close () throws IOException
	{
		running = false;
		socket.close ();
		Socket[] c = (Socket[]) clients.toArray (new Socket[0]);
		for (int i = 0; i < c.length; i++)
		{
			try
			{
				c[i].close ();
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
		}
	}

	protected abstract boolean handleRequest (Request request, Socket client)
			throws IOException;

}
