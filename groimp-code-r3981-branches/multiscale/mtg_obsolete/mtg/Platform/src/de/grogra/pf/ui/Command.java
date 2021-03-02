
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

package de.grogra.pf.ui;

import de.grogra.util.*;

/**
 * A <code>Command</code> is an object which can be executed
 * by a set of methods of a {@link de.grogra.pf.ui.JobManager}.
 * 
 * @author Ole Kniemeyer
 */
public interface Command
{
	Command DISPOSE = new Command ()
	{
		public String getCommandName ()
		{
			return null;
		}

		public void run (Object info, Context context)
		{
			((Disposable) info).dispose ();
		}
	};


	/**
	 * Returns a name which can be used in the graphical user interface
	 * to represent this command.
	 * 
	 * @return this commands's name
	 */
	String getCommandName ();

	/**
	 * Performs the actions of this command. This method is invoked
	 * by the {@link JobManager} after the command has been submitted
	 * to the job manager by one of <code>execute</code>, <code>runAt</code>,
	 * or <code>runLater</code>-methods. The arguments <code>info</code>
	 * and <code>context</code> are the arguments which have been provided
	 * as arguments to the <code>JobManager</code>-methods.
	 * 
	 * @param info an argument
	 * @param context a context
	 */
	void run (Object info, Context context);
}
