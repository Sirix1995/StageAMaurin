
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

package de.grogra.pf.ui.awt;

import java.awt.event.*;
import de.grogra.pf.ui.*;
import de.grogra.util.EventListener;

public class EventAdapter implements KeyListener, MouseListener,
	MouseMotionListener, MouseWheelListener, Command
{
	private final Context ctx;
	private final EventListener target;
	private final int jobFlags;


	public EventAdapter (Context ctx, EventListener target, int jobFlags)
	{
		ctx.getClass ();
		target.getClass ();
		this.ctx = ctx;
		this.target = target;
		this.jobFlags = jobFlags;
	}


	public void install (java.awt.Component c)
	{
		c.addKeyListener (this); 
		c.addMouseListener (this); 
		c.addMouseMotionListener (this); 
		c.addMouseWheelListener (this); 
	}


	public void uninstall (java.awt.Component c)
	{
		c.removeKeyListener (this); 
		c.removeMouseListener (this); 
		c.removeMouseMotionListener (this); 
		c.removeMouseWheelListener (this); 
	}


	private final InputEvent[] pending = new InputEvent[3];

	private void post (InputEvent e, int coalesce)
	{
		boolean run = true;
		Object info;
		if (coalesce >= 0)
		{
			synchronized (pending)
			{
				run = pending[coalesce] == null;
				pending[coalesce] = e;
			}
			info = Byte.valueOf ((byte) coalesce);
		}
		else
		{
			info = e;
		}
		if (run)
		{
			Workbench w = ctx.getWorkbench ();
			if (w != null)
			{
				w.getJobManager ().runLater (this, info, ctx, jobFlags);
			}
		}
	}


	public String getCommandName ()
	{
		return null;
	}


	public void run (Object info, Context c)
	{
		InputEvent e;
		if (info instanceof InputEvent)
		{
			e = (InputEvent) info;
		}
		else
		{
			int coalesce = ((Byte) info).intValue ();
			synchronized (pending)
			{
				e = pending[coalesce];
				pending[coalesce] = null;
			}
		}
		target.eventOccured (e);
	}


	public void keyPressed (KeyEvent e)
	{
		post (e, -1);
	}


	public void keyReleased (KeyEvent e)
	{
		post (e, -1);
	}


	public void keyTyped (KeyEvent e)
	{
		post (e, -1);
	}


	public void mouseClicked (MouseEvent e)
	{
		post (e, -1);
	}


	public void mouseEntered (MouseEvent e)
	{
		post (e, -1);
	}


	public void mouseExited (MouseEvent e)
	{
		post (e, -1);
	}


	public void mousePressed (MouseEvent e)
	{
		e.getComponent ().requestFocus ();
		post (e, -1);
	}


	public void mouseReleased (MouseEvent e)
	{
		post (e, -1);
	}


	public void mouseDragged (MouseEvent e)
	{
		post (e, 0);
	}


	public void mouseMoved (MouseEvent e)
	{
		post (e, 1);
	}


	public void mouseWheelMoved (MouseWheelEvent e)
	{
		post (e, 2);
	}

}
