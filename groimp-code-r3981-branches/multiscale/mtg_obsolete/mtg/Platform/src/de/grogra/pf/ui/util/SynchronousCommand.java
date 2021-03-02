
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

package de.grogra.pf.ui.util;

import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;

public class SynchronousCommand implements Command
{
	protected final Command command;
	private final Object monitor = new Object ();
	private boolean done = false;


	public SynchronousCommand (Command command)
	{
		this.command = command;
	}


	public void run (final Object object, final Context ctx)
	{
		command.run (object, ctx);
		synchronized (monitor)
		{
			done = true;
			monitor.notifyAll ();
		}
	}

	
	public String getCommandName ()
	{
		return command.getCommandName ();
	}


	public void runAndWait (Object info, Context ctx, int flags) throws InterruptedException
	{
		ctx.getWorkbench ().getJobManager ().runLater (this, info, ctx, flags);
		synchronized (monitor)
		{
			while (!done)
			{
				monitor.wait ();
			}
		}
	}
}
