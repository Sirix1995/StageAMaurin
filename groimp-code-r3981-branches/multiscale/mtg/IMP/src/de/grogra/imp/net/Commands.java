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

import de.grogra.imp.IMP;
import de.grogra.imp.IMPWorkbench;
import de.grogra.pf.boot.Main;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.UIItem;

public final class Commands implements MessageHandler
{
	private final Workbench workbench;

	public Commands (Workbench workbench)
	{
		this.workbench = workbench;
	}

	public boolean handleMessage (Connection cx, long messageId,
			String message, boolean checkAvailability) throws IOException
	{
		String cmd = "/connection/commands/" + message;
		Item c = Item.resolveItem (workbench.getMainWorkbench (), cmd);
		if (c instanceof Command)
		{
			if (checkAvailability)
			{
				return (c instanceof UIItem) ? ((UIItem) c)
					.isAvailable (workbench) : UI.isAvailable (c, workbench);
			}
			workbench.getLogger ().log (
				Workbench.GUI_INFO,
				"Connection to " + cx.getSocket ().getRemoteSocketAddress ()
					+ " induces execution of command " + c.getAbsoluteName ());
			((Command) c).run (new MessageInfo (cx, messageId, message),
				workbench);
			return true;
		}
		return false;
	}

	public static void connectRemoteWorkbench (Item item, Object info,
			Context context) throws IOException
	{
		MessageInfo i = (MessageInfo) info;
		new RemoteClientImpl (context.getWorkbench (), i.connection,
			i.messageId);
	}

	public static Connection add (Context ctx) throws IOException
	{
		Socket s = getSocket (ctx, "localhost:58090");
		Connection cx = new Connection (s);
		cx.start ();
		((IMPWorkbench) ctx.getWorkbench ()).addConnection (cx);
		return cx;
	}

	public static void openClientWorkbench (Item item, Object info,
			Context context)
	{
		final MessageInfo i = (MessageInfo) info;
		UI.getJobManager (context).runLater (new Command ()
		{
			public String getCommandName ()
			{
				return null;
			}

			public void run (Object info, Context context)
			{
				try
				{
					IMP.openClientWorkbench (i.connection, context);
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}
		}, null, context, JobManager.ACTION_FLAGS);
	}

	public static void availableProcessors (Item item, Object info,
			Context context) throws IOException
	{
		MessageInfo i = (MessageInfo) info;
		long msg = i.connection.beginResponse (i.messageId, null);
		i.connection.getOut ().writeInt (
			Runtime.getRuntime ().availableProcessors ());
		i.connection.end (msg);
	}

	public static void version (Item item, Object info, Context context)
			throws IOException
	{
		MessageInfo i = (MessageInfo) info;
		long msg = i.connection.beginResponse (i.messageId, null);
		i.connection.getOut ().writeUTF (Main.getVersion ());
		i.connection.end (msg);
	}

	public static SimpleResponseHandler getAvailableProcessors (Connection cx)
			throws IOException
	{
		SimpleResponseHandler rh = new SimpleResponseHandler (
			SimpleResponseHandler.INT);
		long msg = cx.beginMessage ("processors", rh);
		cx.end (msg);
		return rh;
	}

	public static SimpleResponseHandler getVersion (Connection cx)
			throws IOException
	{
		SimpleResponseHandler rh = new SimpleResponseHandler (
			SimpleResponseHandler.STRING);
		long msg = cx.beginMessage ("version", rh);
		cx.end (msg);
		return rh;
	}

	public static void sendMessage (Connection cx, String msg)
			throws IOException
	{
		long id = cx.beginMessage (msg, null);
		cx.end (id);
	}

	private static int checkPort (String port, Context ctx)
	{
		String msg = null;
		try
		{
			int p = Integer.parseInt (port.trim ());
			if ((p > 0) && (p <= 65535))
			{
				return p;
			}
			msg = "socket.illegal-port-number";
		}
		catch (NumberFormatException e)
		{
			msg = "socket.port-not-numeric";
		}
		msg = IMP.I18N.msg (msg, port);
		illegalPort (msg, ctx);
		return -1;
	}

	private static void illegalPort (String msg, Context ctx)
	{
		ctx.getWindow ().showDialog (IMP.I18N.msg ("socket.illegal-port"), msg,
			Window.INFORMATION_MESSAGE);
	}

	private static String getSimpleName (Class cls)
	{
		String s = cls.getName ();
		int i = s.lastIndexOf ('.');
		return (i >= 0) ? s.substring (i + 1) : s;
	}

	public static Socket getSocket (Context ctx, String defaultAddress)
	{
		while (true)
		{
			String input = ctx.getWindow ().showInputDialog (
				IMP.I18N.msg ("chooseserver.title"),
				IMP.I18N.msg ("chooseserver.msg"), defaultAddress);
			if (input == null)
			{
				return null;
			}
			String msg;
			int i = input.indexOf (':');
			if (i < 0)
			{
				msg = IMP.I18N.msg ("socket.colon-missing", input);
			}
			else
			{
				int port = checkPort (input.substring (i + 1), ctx);
				if (port < 0)
				{
					continue;
				}
				try
				{
					return new Socket (input.substring (0, i).trim (), port);
				}
				catch (IOException e)
				{
					msg = getSimpleName (e.getClass ()) + ": "
						+ e.getMessage ();
				}
			}
			ctx.getWindow ().showDialog (
				IMP.I18N.msg ("socket.illegal-address"), msg,
				Window.INFORMATION_MESSAGE);
		}
	}

	public static ServerSocket getServerSocket (Context ctx, int defaultPort)
	{
		while (true)
		{
			String input = String.valueOf (defaultPort);
			if (ctx.getWindow () != null)
			{
				input = ctx.getWindow ().showInputDialog (
					IMP.I18N.msg ("startserver.title"),
					IMP.I18N.msg ("startserver.msg-port"), input);
			}
			if (input == null)
			{
				return null;
			}
			int port = checkPort (input, ctx);
			if (port > 0)
			{
				try
				{
					return new ServerSocket (port);
				}
				catch (IOException e)
				{
					illegalPort (getSimpleName (e.getClass ()) + ": "
						+ e.getMessage (), ctx);
				}
			}
		}
	}

}
