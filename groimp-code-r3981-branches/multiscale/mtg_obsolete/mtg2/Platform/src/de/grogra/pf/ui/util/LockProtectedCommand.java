
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

import de.grogra.util.*;
import de.grogra.pf.ui.*;

public abstract class LockProtectedCommand implements Command
{
	protected Lockable resource;
	protected boolean write;
	protected int flags;


	public LockProtectedCommand (Lockable resource, boolean write, int flags)
	{
		this.resource = resource;
		this.write = write;
		this.flags = flags;
	}


	public void run (final Object object, final Context ctx)
	{
		UI.executeLockedly (resource, write, 	
			new Command ()
				{
					public String getCommandName ()
					{
						return LockProtectedCommand.this.getCommandName ();
					}
					
					public void run (Object arg, Context c)
					{
						runImpl (object, c, (Lock) arg);
						done(c);
					}
					
					@Override
					public String toString ()
					{
						return "[" + LockProtectedCommand.this + ']';
					}
				}, null, ctx, flags, true);
	}

	
	public String getCommandName ()
	{
		return null;
	}

	protected abstract void runImpl (Object arg, Context ctx, Lock lock);

	protected void done(Context c) {};
}
