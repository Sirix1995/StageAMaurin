
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

package de.grogra.pf.ui.swing;

import java.awt.EventQueue;

import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.util.Lockable;

public abstract class Job extends LockProtectedCommand implements Runnable
{
	private final Context ctx;
	private final int flags;

	private boolean started = false;
	private boolean done = false;

	public Job (Lockable resource, boolean write, int flags, Context ctx)
	{
		super (resource, write, flags);
		this.ctx = ctx;
		this.flags = flags;
	}

	public Job (Context ctx)
	{
		this (ctx.getWorkbench ().getRegistry ().getProjectGraph (), true, JobManager.ACTION_FLAGS, ctx);
	}

	public void execute ()
	{
		if (started)
		{
			throw new IllegalStateException (this + " may only be executed once");
		}
		started = true;
		JobManager jm = ctx.getWorkbench ().getJobManager ();
		jm.runLater (this, null, ctx, flags);
	}

	public void run ()
	{
		done ();
	}
	
	@Override
	protected void done (Context c)
	{
		if (done) {
			return;
		}
		done = true;
		EventQueue.invokeLater (this);
		synchronized (this) {
			notifyAll();
		}
	}
	
	public boolean isDone()
	{
		return done;
	}

	protected void done ()
	{
	}
}
