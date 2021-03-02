
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

package de.grogra.ext.openalea;

import java.io.IOException;

import de.grogra.imp.IMP;
import de.grogra.imp.IMPWorkbench;
import de.grogra.imp.NewProject;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.Utils;
import de.grogra.vfs.MemoryFileSystem;

public class ServerProject implements Command{
//extends NewProject {

	private static final String ITEM = "/openalea/server";
	
	public void runag (Object arg, Context context)
	{
		/*IMP imp = IMP.getInstance ();
		Registry r = Registry.create (imp.getRegistry ());
		r.initFileSystem (new MemoryFileSystem (de.grogra.pf.io.IO.PROJECT_FS));
		IMPWorkbench w = new IMPWorkbench (r, null);*/
		IMPWorkbench w = de.grogra.rgg.Library.workbench();
		Registry r = w.getRegistry();
		try
		{
			/*Registry.setCurrent (r);
			IMP.loadRegistry (r, null, true);
			r.setEmptyGraph ();
			w.setName (getProjectName ());*/
			configure (w, arg); 
			//imp.start (w, context.getWindow ());
		}
		finally
		{
			Registry.setCurrent (context.getWorkbench ());
		}
		r.getLogger().log(w.GUI_INFO, OpenAleaPlugin.I18N.msg("openaleaserver-name", Integer
				.toString((Integer)arg))); 
	}

	//@Override
	public void run(Object arg, Context ctx) {
		Item i = Item.resolveItem(ctx.getWorkbench(), ITEM);
		int port;
		try {
			port = Integer.parseInt((String) arg);
		} catch (RuntimeException e) {
			port = getPort(ctx, Utils.getInt(i, "port", 58070));
		}
		if (port == 0) {
			return;
		}
		runag(port, ctx);
	}

	//@Override
	protected void configure(Workbench wb, Object arg) {
		int port = (Integer) arg;
		Server s = new Server(wb, port);
		if (s.isServerReady())
			s.start();
		else
			return;
		/*wb.setProperty(Workbench.INITIAL_LAYOUT, "/ui/layouts/http");
		wb.setName(OpenAleaPlugin.I18N.msg("openaleaserver-name", Integer
				.toString(port)));*/
		wb.ignoreIfModified();
		wb.setProperty(ITEM, port);
	}

	public int getPort(Context ctx, int defaultPort) {
		while (true) {
			String input = String.valueOf(defaultPort);
			if (ctx.getWindow() != null) {
				input = ctx.getWindow()
						.showInputDialog(
								OpenAleaPlugin.I18N
										.msg("startopenaleaserver.title"),
								OpenAleaPlugin.I18N
										.msg("startopenaleaserver.msg-port"),
								input);
			}
			if (input == null) {
				return 0;
			}

			return Integer.valueOf(input);
		}
	}

	
	public String getCommandName() {
		// TODO Auto-generated method stub
		return null;
	}

}
