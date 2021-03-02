
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

package de.grogra.ext.jedit;

import java.io.*;
import java.awt.*;
import de.grogra.util.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.io.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.awt.AWTSynchronizer;
import de.grogra.pf.ui.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

public class JEditTextEditor extends PanelSupport
	implements TextEditor, ModifiableMap.Producer
{
	private static final String PFS_PREFIX = IO.PROJECT_FS + IO.SYSTEM_ID_SEPARATOR;

	private static boolean initialized;
	private static String VFS_ROOT;


	private static synchronized View createView ()
	{
		class Helper implements Runnable
		{
			boolean returnNewView;
			View view;

			public void run ()
			{
				if (returnNewView)
				{
					view = jEdit.newView (null, null, false);
				}
			}
		}

		Helper helper = new Helper ();

		if (!initialized)
		{
			initialized = true;
			PluginDescriptor pd
				= PluginDescriptor.getInstance (helper.getClass ());
			if (pd.getPluginDirectory () instanceof File)
			{
				System.setProperty
					("jedit.home", ((File) pd.getPluginDirectory ()).getAbsolutePath ());
			}
			
			final File c = pd.getConfigurationDirectory ();
			new Thread ("jEdit Startup")
			{
				@Override
				public void run ()
				{
					jEdit.main (new String[] {
						(c != null) ? "-settings=" + c.getAbsolutePath ()
						: "-nosettings",
						"-noserver", "-norestore"});
				}
			}.start ();
			while (!jEdit.isStartupDone ())
			{
				try
				{
					Thread.sleep (10);
					EventQueue.invokeAndWait (helper);
				}
				catch (InterruptedException e)
				{
				}
				catch (java.lang.reflect.InvocationTargetException e)
				{
				}
			}
			VFS_ROOT = VFSAdapter.forAllProjects ().getName () + ":/";
			return jEdit.getFirstView ();
		}
		helper.returnNewView = true;
		AWTSynchronizer.staticInvokeAndWait (helper);
		return helper.view;
	}


	public static void shutdown ()
	{
		if (initialized)
		{
			jEdit.exit (null, true);
		}
	}

	
	public JEditTextEditor ()
	{
		super (createView ());
		mapProducer = this;
	}


	@Override
	protected void configure (Map params)
	{
		super.configure (params);
		String[] docs = UI.getDocuments (params);
		for (int j = 0; j < docs.length; j++)
		{
			openDocument (docs[j], null);
		}
		String s = (String) params.get ("selected", null);
		if (s != null)
		{
			openDocument (s, null);
		}
	}


	private String getVFSPrefix ()
	{
		return VFS_ROOT + getRegistry ().getFileSystemName () + '/';
	}

	
	private String getVFSPath (String systemId)
	{
		if (systemId.startsWith (PFS_PREFIX))
		{
			return getVFSPrefix () + systemId.substring (PFS_PREFIX.length ());
		}
		return systemId;
	}


	private String getSystemId (String vfsPath)
	{
		if (vfsPath.startsWith (VFS_ROOT))
		{
			String p = getVFSPrefix ();
			return vfsPath.startsWith (p)
				? PFS_PREFIX + vfsPath.substring (p.length ())
				: null;
		}
		else
		{
			return vfsPath;
		}
	}

	
	private static final int OPEN_DOC = MIN_UNUSED_ACTION;
	private static final int CLOSE_DOC = MIN_UNUSED_ACTION + 1;
	private static final int GET_DOCS = MIN_UNUSED_ACTION + 2;
	private static final int CHECK_CLOSE = MIN_UNUSED_ACTION + 3;

	@Override
	public Object run (int action, int iarg, Object arg, Object arg2)
	{
		switch (action)
		{
			case OPEN_DOC:
			{
				String path = getVFSPath ((String) arg);
				Buffer b = jEdit._getBuffer (path);
				if (b == null)
				{
					b = jEdit.openFile ((View) getComponent (), path);
				}
				else
				{
					((View) getComponent ()).goToBuffer (b);
				}
				JEditTextArea editor = ((View) getComponent ()).getTextArea ();
				if ((b == null) || (editor == null)
					|| (editor.getBuffer () != b))
				{
					break;
				}
				Point[] range = UI.parsePlainTextRange ((String) arg2);
				if (range == null)
				{
					break;
				}
				try
				{
					Point p = range[0];
					int start = editor.getLineStartOffset (p.y);
					if (p.x >= 0)
					{
						start += p.x;
					}
					p = range[1];
					if (p == null)
					{
						editor.setCaretPosition (start);
					}
					else
					{
						editor.setSelection (new Selection.Range
							(start, (p.x >= 0)
							 ? editor.getLineStartOffset (p.y) + p.x
							 : editor.getLineEndOffset (p.y)));
						editor.moveCaretPosition (start, true);
					}
				}
				catch (RuntimeException e)
				{
				}
				break;
			}
			case CLOSE_DOC:
			{
				Buffer b = jEdit._getBuffer (getVFSPath ((String) arg));
				if (b != null)
				{
					jEdit._closeBuffer ((View) getComponent (), b);
				}
				break;
			}
			case GET_DOCS:
			{
				Buffer[] a = jEdit.getBuffers ();
				String[] d = new String[a.length];
				int n = 0;
				for (int i = 0; i < a.length; i++)
				{
					String sysId = getSystemId (a[i].getPath ());
					if (sysId != null)
					{
						d[n++] = sysId;
					}
				}
				if (n != d.length)
				{
					System.arraycopy (d, 0, d = new String[n], 0, n);
				}
				return d;
			}
			case CHECK_CLOSE:
			{
				Buffer[] a = jEdit.getBuffers ();
				boolean lastView = jEdit.getViewCount () == 1;
				for (int i = 0; i < a.length; i++)
				{
					if (lastView
						|| (a[i].getPath ().startsWith (getVFSPrefix ())))
					{
						if (!jEdit.closeBuffer ((View) getComponent (), a[i]))
						{
							return null;
						}
					}
				}
				return this;
			}
			default:
				return super.run (action, iarg, arg, arg2);
		}
		return null;
	}

	/**
	 * Close and clear buffer of document in JEditTextEditor. Used in SourceFile.java method show (Context ctx, String ref)
	 */
	public void closeDocument (String doc, String ref)
	{
		sync.invokeAndWait (CLOSE_DOC, 0, doc, ref);
	}

	public void openDocument (String doc, String ref)
	{
		sync.invokeAndWait (OPEN_DOC, 0, doc, ref);
	}


	@Override
	public void checkClose (Runnable ok)
	{
		executeCheckClose (ok);
	}


	@Override
	protected void disposeImpl ()
	{
		jEdit.closeView ((View) getComponent (), false);
		super.disposeImpl ();
	}


	@Override
	public void checkClose (Command ok)
	{
		if (sync.invokeAndWait (CHECK_CLOSE) != null)
		{
			ok.run (null, this);
		}
	}


	public String[] getDocuments ()
	{
		return (String[]) sync.invokeAndWait (GET_DOCS);
	}


	public void addMappings (ModifiableMap out)
	{
		UI.putDocuments (this, out);
		String sysId = getSystemId
			(((View) getComponent ()).getBuffer ().getPath ());
		if (sysId != null)
		{
			out.put ("selected", sysId);
		}
	}

}
