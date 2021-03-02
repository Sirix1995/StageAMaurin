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
import java.net.ServerSocket;
import java.net.Socket;

import de.grogra.imp.IMPWorkbench;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.util.Utils;

public class ConnectionServer implements Runnable
{
	private final IMPWorkbench workbench;
	private final ServerSocket socket;

	public ConnectionServer (IMPWorkbench workbench, ServerSocket socket)
	{
		this.workbench = workbench;
		this.socket = socket;
	}

	public void run ()
	{
		try
		{
			while (true)
			{
				startClient (socket.accept ());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	private void startClient (Socket s)
	{
		try
		{
			Connection cx = new Connection (s);
			workbench.addConnection (cx);
			cx.start ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	public static void start (Item item, Object info, Context ctx)
	{
		ServerSocket socket;
		try
		{
			int port = Integer.parseInt ((String) info);
			try
			{
				socket = new ServerSocket (port);
			}		
			catch (Exception e)
			{
				socket = null;
				ctx.getWorkbench ().logInfo ("", e);
			}
		}
		catch (RuntimeException e)
		{
			socket = Commands.getServerSocket (ctx, Utils.getInt (item, "port", 58090));
		}
		if (socket == null)
		{
			return;
		}
		ConnectionServer s = new ConnectionServer ((IMPWorkbench) ctx.getWorkbench (), socket);
		new Thread (s, s.toString ()).start ();
	}

}
