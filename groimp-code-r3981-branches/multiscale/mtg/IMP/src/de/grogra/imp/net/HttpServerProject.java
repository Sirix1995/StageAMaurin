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

import de.grogra.imp.IMP;
import de.grogra.imp.NewProject;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.Utils;

public class HttpServerProject extends NewProject
{
	private static final String ITEM = "/http/server";

	@Override
	public void run (Object arg, Context ctx)
	{
		Item i = Item.resolveItem (ctx.getWorkbench (), ITEM);
		ServerSocket socket;
		try
		{
			int port = Integer.parseInt ((String) arg);
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
			socket = Commands.getServerSocket (ctx, Utils.getInt (i, "port", 58080));
		}
		if (socket == null)
		{
			return;
		}
		super.run (socket, ctx);
	}

	@Override
	protected void configure (Workbench wb, Object arg)
	{
		ServerSocket socket = (ServerSocket) arg;
		HttpServer s = new HttpServer (wb, socket);
		new Thread (s, s.toString ()).start ();
		wb.setProperty (Workbench.INITIAL_LAYOUT, "/ui/layouts/http");
		wb.setName (IMP.I18N.msg ("httpserver-name", Integer.toString (socket
			.getLocalPort ())));
		wb.ignoreIfModified ();
		wb.setProperty (ITEM, s);
	}

	public static void close (Context ctx) throws IOException
	{
		HttpServer s = (HttpServer) ctx.getWorkbench ().getProperty (ITEM);
		if (s != null)
		{
			s.close ();
		}
	}
}
