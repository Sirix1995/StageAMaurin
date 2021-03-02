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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import de.grogra.graph.impl.Node;
import de.grogra.http.Request;
import de.grogra.http.Server;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.Described;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;

public class HttpServer extends Server
{
	public static final String HTTP_RESPONSE = "http-response";

	protected final Workbench workbench;

	public HttpServer (Workbench workbench, ServerSocket socket)
	{
		super (socket, workbench.getLogger (), Workbench.GUI_INFO,
			Level.WARNING);
		this.workbench = workbench;
	}

	@Override
	protected boolean handleRequest (Request request, Socket client)
			throws IOException
	{
		HttpResponse r = new HttpResponse (this, request, client);
		workbench.getJobManager ().runLater (r, null, workbench,
			JobManager.ACTION_FLAGS);

		synchronized (r)
		{
			try
			{
				while (!r.done)
				{
					if (isClosed ())
					{
						return false;
					}
					r.wait (1000);
				}
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}
		if (r.ex != null)
		{
			throw r.ex;
		}
		return r.cont;
	}


	public static void openProject (Item item, Object info, Context ctx)
			throws UnsupportedEncodingException
	{
		HttpResponse res = (HttpResponse) info;
		String proj = res.getRequest ().getQuery ();
		if ("GET".equals (res.getRequest ().getMethod ()) && (proj != null))
		{
			int i = proj.indexOf ('&');
			if (i >= 0)
			{
				proj = proj.substring (0, i);
			}
			String base = (String) Utils.getObject (item, "directory");
			if (base == null)
			{
				base = System.getProperty ("user.home");
			}
			File dir = new File (base);
			File file = new File (dir, proj);
			if (file.exists ())
			{
				try
				{
					if (file.getCanonicalPath ().startsWith (
						dir.getCanonicalPath ()))
					{
						StringMap init = new StringMap ().putObject (HTTP_RESPONSE,
							res);
						ctx.getWorkbench ().open (
							FileSource.createFileSource (file.getAbsolutePath (),
								IO.getMimeType (proj), ctx.getWorkbench (), null),
							init);
						return;
					}
				}
				catch (IOException e)
				{
					res.getServer ().getLogger ()
						.log (res.getServer ().getWarningLevel (),
							"Exception occured", e);
				}
				res.setContent ("text/plain", "UTF-8", "Access forbidden for "
					+ proj + '\n');
				res.send (true, HttpURLConnection.HTTP_FORBIDDEN,
					"Access Forbidden");
				return;
			}
		}
		res.setContent ("text/plain", "UTF-8", "Project file " + proj
			+ " not found\n");
		res.send (true, HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
	}

	public static void about (Item item, Object info, Context ctx)
			throws IOException
	{
		HttpResponse res = (HttpResponse) info;
		StringBuffer buf = new StringBuffer (
			"<html><head><title>HTTP Server - GroIMP</title></head><body>");
		buf.append ("GroIMP version ").append (Main.getVersion ()).append (
			"<br>");
		buf
			.append ("<table border=\"1\"><caption>Installed Plugins</caption><tr><th>Name</th><th>Version</th><th>Plugin-Id</th></tr>");
		for (PluginDescriptor p = (PluginDescriptor) ctx.getWorkbench ()
			.getRegistry ().getPluginDirectory ().getBranch (); p != null; p = (PluginDescriptor) p
			.getSuccessor ())
		{
			buf.append ("<tr><td>").append (p.getPluginName ()).append (
				"</td><td>").append (p.getPluginVersion ())
				.append ("</td><td>").append (p.getName ()).append (
					"</td></tr>");
		}
		buf
			.append ("</table><br><table border=\"1\"><caption>Available commands</caption><tr><th>Command</th><th>Description</th></tr>");
		for (Node n = Item.resolveItem (ctx.getWorkbench (), "/http/commands")
			.getBranch (); n != null; n = n.getSuccessor ())
		{
			if (n instanceof Command)
			{
				buf.append ("<tr><td>").append (((Item) n).getName ()).append (
					"</td><td>").append (
					((Item) n).getDescription (Described.SHORT_DESCRIPTION))
					.append ("</td></tr>");
			}
		}
		buf.append ("</table><br>HTTP Request:<pre>")
			.append (res.getRequest ()).append ("</pre></body></html>");
		res.setContent ("text/html", "UTF-8", buf.toString ());
		res.send (true);
	}

}
