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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.logging.Level;

import de.grogra.http.Request;
import de.grogra.http.Server;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;

public class HttpResponse implements Command
{
	private final HttpServer server;
	private final Request request;
	private final Socket client;

	boolean cont;
	boolean done = false;
	IOException ex;

	private String mimeType;
	private byte[] content;

	public HttpResponse (HttpServer server, Request request, Socket client)
	{
		this.server = server;
		this.request = request;
		this.client = client;
	}

	public void setContent (String mimeType, byte[] content)
	{
		this.mimeType = mimeType;
		this.content = content;
	}

	public void setContent (String mimeType, String encoding, String content)
			throws UnsupportedEncodingException
	{
		this.mimeType = mimeType + "; charset=" + encoding;
		this.content = content.getBytes (encoding);
	}

	public String getCommandName ()
	{
		return null;
	}

	public void setException (IOException ex)
	{
		this.ex = ex;
	}

	public void send (boolean cont)
	{
		send (cont, HttpURLConnection.HTTP_OK, "OK");
	}

	public synchronized void send (boolean cont, int status, String reason)
	{
		try
		{
			if (content == null)
			{
				setContent ("text/plain", "US-ASCII",
					"The content has not been set.\n");
			}
			Server.writeResponse (status, reason, mimeType,
				content, true, client.getOutputStream ());
		}
		catch (IOException e)
		{
			server.getLogger ().log (Level.INFO, "Could not send OK", e);
		}
		finally
		{
			this.cont = cont;
			done = true;
			notifyAll ();
		}
	}

	public void run (Object info, Context context)
	{
		handleRequest ();
	}

	public void sendBadRequest ()
	{
		try
		{
			setContent ("text/plain", "UTF-8", "The request\n" + request
				+ "could not be handled by the server.\n");
			Server.writeResponse (HttpURLConnection.HTTP_BAD_REQUEST,
				"Bad Request", mimeType, content, true, client
					.getOutputStream ());
		}
		catch (IOException e)
		{
			server.getLogger ().log (Level.INFO, "Could not send BAD REQUEST",
				e);
		}
		finally
		{
			send (false);
		}
	}

	protected void handleRequest ()
	{
		try
		{
			String cmd = request.getPath ();
			while (cmd.endsWith ("/"))
			{
				cmd = cmd.substring (0, cmd.length () - 1);
			}
			if (cmd.length () == 0)
			{
				cmd = "/about";
			}
			cmd = "/http/commands" + cmd;
			Item c = Item.resolveItem (server.workbench, cmd);
			if ((c instanceof Command)
				&& c.getAbsoluteName ().equals (cmd)) 
			{
				server.getLogger ().log (server.getInfoLevel (), "Executing command " + c.getAbsoluteName ());
				((Command) c).run (this, server.workbench);
				return;
			}
		}
		catch (RuntimeException e)
		{
			server.getLogger ().log (server.getWarningLevel (), "Exception occured", e);
		}
		sendBadRequest ();
	}

	public Socket getClient ()
	{
		return client;
	}

	public Request getRequest ()
	{
		return request;
	}

	public HttpServer getServer ()
	{
		return server;
	}

	public static HttpResponse get (Context ctx)
	{
		return (HttpResponse) ctx.getWorkbench ().getProperty (
			HttpServer.HTTP_RESPONSE);
	}

}
