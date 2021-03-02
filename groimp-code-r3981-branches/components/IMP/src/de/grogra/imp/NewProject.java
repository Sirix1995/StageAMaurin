
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

package de.grogra.imp;

import java.io.IOException;

import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.*;
import de.grogra.vfs.MemoryFileSystem;

public class NewProject implements Command
{

	public String getCommandName ()
	{
		return null;
	}

	
	protected String getProjectName ()
	{
		return IMP.I18N.getString ("project.new");
	}
	
	
	protected void configure (Workbench wb, Object arg)
	{
	}


	public void run (Object arg, Context context)
	{
		IMP imp = IMP.getInstance ();
		Registry r = Registry.create (imp.getRegistry ());
		r.initFileSystem (new MemoryFileSystem (de.grogra.pf.io.IO.PROJECT_FS));
		IMPWorkbench w = new IMPWorkbench (r, null);
		try
		{
			Registry.setCurrent (r);
			IMP.loadRegistry (r, null, true);
			r.setEmptyGraph ();
			w.setName (getProjectName ());
			configure (w, arg); 
			imp.start (w, context.getWindow ());
		}
		catch (IOException e)
		{
			throw new AssertionError (e);
		}
		finally
		{
			Registry.setCurrent (context.getWorkbench ());
		}
	}
}
