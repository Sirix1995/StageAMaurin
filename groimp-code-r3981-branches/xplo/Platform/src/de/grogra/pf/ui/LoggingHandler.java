
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

import java.util.logging.*;
import java.beans.*;
import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;

public class LoggingHandler extends Handler
{
	final Context ctx;
	final Object component;
	final Object viewer;
	final DefaultListModel recordList;
	final StringBuffer buffer;
	final Widget choice;

	private String mimeType;
	private int groupingCount;
	private ObjectList groupedRecords = new ObjectList ();
	private ObjectList pendingRecords = new ObjectList ();


	private final class Helper
		implements Command, PropertyChangeListener, Described
	{
		static final int HYPERLINK = 0;
		static final int PUBLISH = 1;

		private final int command;
		private LogRecord[] records;

		Helper (int command)
		{
			this.command = command;
		}
		
		private LogRecord[] getRecords ()
		{
			synchronized (LoggingHandler.this)
			{
				if (records == null)
				{
					records = new LogRecord[pendingRecords.size ()];
					pendingRecords.toArray (records);
					pendingRecords.clear ();
				}
			}
			return records;
		}

		public void run (Object info, Context cx)
		{
			switch (command)
			{
				case HYPERLINK:
					UI.executeHyperlinkURL ((String) info, cx);
					break;
				case PUBLISH:
					if (getRecords ().length > 0)
					{
						recordList.addElement (this);
						choice.updateValue (this);
						setViewerContent ();
					}
					break;
			}
		}
		
		private void setViewerContent ()
		{
			LoggingHandler.this.setViewerContent (getRecords ());
		}

		public String getCommandName ()
		{
			return null;
		}
		
		public void propertyChange (PropertyChangeEvent e)
		{
			((Helper) e.getNewValue ()).setViewerContent ();
		}

		public Object getDescription (String type)
		{
			if (Utils.isStringDescription (type))
			{
				LogRecord[] a = getRecords ();
				LogRecord r = a[a.length - 1];
				synchronized (buffer)
				{
					buffer.setLength (0);
					buffer.append ('[').append (a.length).append ("] ");
					Utils.formatDateAndName (r, buffer);
					buffer.append (' ').append (r.getLevel ().getLocalizedName ());
					Throwable t;
					if (r.getMessage ().length () > 0)
					{
						buffer.append (": ").append (r.getMessage ());
					}
					else if ((t = Utils.getMainException (r.getThrown ()))
							 != null)
					{
						buffer.append (": ").append
							(t.getLocalizedMessage ());
					}
					return buffer.toString ();
				}
			}
			return null;
		}
	}

	
	public LoggingHandler (Context ctx)
	{
		this.ctx = ctx;
		this.recordList = new DefaultListModel ();
		this.buffer = new StringBuffer ();
		this.mimeType = "text/plain";

		UIToolkit ui = ctx.getWorkbench ().getToolkit ();
		Helper h = new Helper (Helper.HYPERLINK);

		choice = ui.createChoiceWidget (recordList, false);
		choice.addPropertyChangeListener (h);

		viewer = ui.createTextViewer (null, mimeType, "", h, false);

		component = ui.createContainer (5);
		ui.addComponent (component, choice.getComponent (), BorderLayout.NORTH);
		ui.addComponent (component, ui.createScrollPane (viewer), BorderLayout.CENTER);
	}


	public synchronized void beginGrouping ()
	{
		groupingCount++;
	}

	
	public synchronized void endGrouping ()
	{
		if ((--groupingCount == 0) && (groupedRecords.size > 0))
		{
			pendingRecords.addAll (groupedRecords);
			groupedRecords.clear ();
			publish ();
		}
	}

	
	@Override
	public synchronized void publish (LogRecord record)
	{
		if (!isLoggable (record))
		{
			return;
		}
		if (groupingCount == 0)
		{
			pendingRecords.add (record);
			publish ();
		}
		else
		{
			groupedRecords.add (record);
		}
	}

	
	private void publish ()
	{
		ctx.getWorkbench ().getJobManager ().runLater
			(500, new Helper (Helper.PUBLISH), null, ctx);
	}


	void setViewerContent (LogRecord[] records)
	{
		Formatter f = getFormatter ();
		String s;
		synchronized (buffer)
		{
			buffer.setLength (0);
			buffer.append (f.getHead (this));
			for (int i = 0; i < records.length; i++)
			{
				buffer.append (f.format (records[i]));
			}
			s = buffer.append (f.getTail (this)).toString ();
		}
		ctx.getWorkbench ().getToolkit ().setContent (viewer, mimeType, s);
	}

	
	public void setMimeType (String mimeType)
	{
		this.mimeType = mimeType;
	}
	

	public Context getContext ()
	{
		return ctx;
	}


	public Object getComponent ()
	{
		return component;
	}


	@Override
	public void close ()
	{
	}


	@Override
	public void flush ()
	{
	}

}
