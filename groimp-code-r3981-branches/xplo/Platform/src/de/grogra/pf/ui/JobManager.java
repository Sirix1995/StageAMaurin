
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

public interface JobManager extends Context
{
	interface ExecutionListener extends java.util.EventListener
	{
		void executionStarted (JobManager jm);
		
		void executionFinished (JobManager jm);
	}

	
	short RENDER_PRIORITY = 1000;
	short UPDATE_PRIORITY = 5000;
	short UI_PRIORITY = ThreadContext.NORMAL_PRIORITY;

	int PRIORITY_MASK = 0xffff;
	int QUIET = 0x10000;

	int RENDER_FLAGS = QUIET | RENDER_PRIORITY;
	int UPDATE_FLAGS = QUIET | UPDATE_PRIORITY;
	int ACTION_FLAGS = UI_PRIORITY;


	Thread getMainThread ();

	ThreadContext getThreadContext ();

	void execute (Command command, Object info, Context ctx, int flags);

	void runLater (Command command, Object info, Context ctx, int flags);

	void runLater (long delay, Command command, Object info,
				   Context ctx);

	void runAt (long time, Command command, Object info,
				Context ctx);

	void runBlocking (Runnable r);

	boolean hasJobQueued (int minPriority);

	void cancelQueuedJob (Command command);
	
	boolean hasTimedJobQueued ();

	void cancelTimedJob (Command command);

	void addExecutionListener (ExecutionListener listener);

	void removeExecutionListener (ExecutionListener listener);
}
