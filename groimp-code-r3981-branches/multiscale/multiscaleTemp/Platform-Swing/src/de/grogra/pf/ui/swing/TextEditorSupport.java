
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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.MissingResourceException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.undo.*;
import javax.swing.event.*;
import de.grogra.util.*;
import de.grogra.icon.*;
import de.grogra.pf.io.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.Window;

public class TextEditorSupport extends PanelSupport
	implements TextEditor, ChangeListener, ModifiableMap.Producer
{
	class Doc extends javax.swing.text.PlainDocument implements Runnable
	{
		final String systemId;
		MimeType mimeType;
		final Workbench wb;
		final boolean editable;
		boolean modified;


		Doc (Workbench wb, String systemId)
		{
			this.systemId = systemId;
			this.wb = wb;
			FileSource file = null;
			mimeType = MimeType.TEXT_PLAIN;
			try
			{
				file = FileSource.createFileSource
					(systemId, IO.getMimeType (systemId), wb, null);
				this.mimeType = file.getFlavor ().getMimeType ();
				insertString (0, file.readContent ().toString (), null);
			}
			catch (javax.swing.text.BadLocationException e)
			{
				wb.logInfo (null, e);
			}
			catch (Exception e)
			{
				//TODO: Workaround for saved projects created with jEdit
				// TextEditor is then called with systemId=Untitled 
//				wb.logGUIInfo (IO.I18N.msg ("readfile.failed", systemId), e);
			}
			putProperty (TitleProperty, IO.toPath (systemId));
			editable = (file != null) && !file.isReadOnly ();
			modified = false;
		}


		void save ()
		{
			render (this);
		}


		public void run ()
		{
			FileSource file = FileSource.createFileSource
				(systemId, mimeType, wb, null);
			Writer out = null;
			try
			{
				out = file.getWriter (false);
				out.write (getText (0, getLength ()));
				out.flush ();
				modified = false;
			}
			catch (IOException e)
			{
				wb.logGUIInfo
					(IO.I18N.msg ("writefile.failed", file.getSystemId ()), e);
			}
			catch (javax.swing.text.BadLocationException e)
			{
				wb.logInfo (null, e);
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close ();
					}
					catch (IOException e)
					{
						wb.logGUIInfo
							(IO.I18N.msg ("closefile.failed", file.getSystemId ()), e);
					}
				}
			}
		}


		@Override
		protected void postRemoveUpdate (DefaultDocumentEvent e)
		{
			super.postRemoveUpdate (e);
			modified = true;
		}


		@Override
		protected void insertUpdate (DefaultDocumentEvent e,
									 javax.swing.text.AttributeSet a)
		{
			super.insertUpdate (e, a);
			modified = true;
		}


		@Override
		protected void fireRemoveUpdate (DocumentEvent e)
		{
			super.fireRemoveUpdate (e);
			modified = true;
		}


		@Override
		protected void fireInsertUpdate (DocumentEvent e)
		{
			super.fireInsertUpdate (e);
			modified = true;
		}


		@Override
		protected void fireChangedUpdate (DocumentEvent e)
		{
			super.fireChangedUpdate (e);
			modified = true;
		}

	}


	private static final String UNDO_MANAGER
		= "de.grogra.pf.ui.swing.TextEditorSupport.UndoManager";

	private static final String HANDLER
		= "de.grogra.pf.ui.swing.TextEditorSupport.Handler";

	private static final String DOCUMENT
		= "de.grogra.pf.ui.swing.TextEditorSupport.Document";

	private static final String EDITOR
		= "de.grogra.pf.ui.swing.TextEditorSupport.Editor";

	private static final String[] KEYS
		= {Action.NAME, Action.SHORT_DESCRIPTION, Action.ACCELERATOR_KEY};


	private class ActionImpl extends AbstractAction
	{
		ActionImpl (String name)
		{
			super (name);
			setEnabled (false);
			putValue (ACTION_COMMAND_KEY, name);
			I18NBundle b = UI.I18N;
			Object o;
			try
			{
				o = b.getObject ("text." + name + '.' + Described.ICON);
				if (o instanceof IconSource)
				{
					putValue (SMALL_ICON, IconAdapter.create
							  ((IconSource) o, SwingToolkit.MENU_ICON_SIZE));
				}
			}
			catch (MissingResourceException e)
			{
			}
			for (int i = 0; i < KEYS.length; i++)
			{
				putValue (KEYS[i],
						  b.getStringOrNull ("text." + name + '.' + KEYS[i]));
			}
		}

		public void actionPerformed (ActionEvent e)
		{
			TextEditorSupport.this.actionPerformed (e);
		}
	}


	ActionImpl undoAction, redoAction, saveAction, cutAction, copyAction,
		pasteAction, closeAction;
	Object doUndo, doRedo, doSave;
	JTabbedPane tab;

	public TextEditorSupport ()
	{
		super (new SwingPanel (null));
		Container c = ((SwingPanel) getComponent ()).getContentPane ();
		c.setLayout (new BorderLayout ());

		JToolBar tb = new JToolBar ();
		tb.setFloatable (false);
		tb.setRollover (true);
		SwingToolkit.setDisabledIcon
			(tb.add (saveAction = new ActionImpl ("save")));
		SwingToolkit.setDisabledIcon
			(tb.add (undoAction = new ActionImpl ("undo")));
		SwingToolkit.setDisabledIcon
			(tb.add (redoAction = new ActionImpl ("redo")));
		SwingToolkit.setDisabledIcon
			(tb.add (cutAction = new ActionImpl ("cut")));
		SwingToolkit.setDisabledIcon
			(tb.add (copyAction = new ActionImpl ("copy")));
		SwingToolkit.setDisabledIcon
			(tb.add (pasteAction = new ActionImpl ("paste")));
		SwingToolkit.setDisabledIcon
			(tb.add (closeAction = new ActionImpl ("close")));
		c.add (tb, BorderLayout.NORTH);
		
		doUndo = new Object();
		doRedo = new Object();
		doSave = new Object();
		
		tab = new JTabbedPane (JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
		c.add (tab, BorderLayout.CENTER);
		tab.addChangeListener (this);
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

	
	private void openDocumentSync (String systemId, String ref)
	{
		JTextArea editor;
	getEditor:
		{
			for (int i = 0; i < tab.getTabCount (); i++)
			{
				if (systemId.equals (((Doc) ((JComponent) tab.getComponentAt (i))
									 .getClientProperty (DOCUMENT)).systemId))
				{
					tab.setSelectedIndex (i);
					editor = (JTextArea) ((JComponent) tab.getComponentAt (i))
						.getClientProperty (EDITOR);
					break getEditor;
				}
			}

			final Doc doc = new Doc (getWorkbench (), systemId);
			editor = new JTextArea (doc);
			editor.setEditable (doc.editable);
			editor.setTabSize (4);
			editor.setFont (new Font ("Monospaced", Font.PLAIN, 12));
			editor.setDragEnabled (true);
			
			//hotkeys
			editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), doSave);
			editor.getActionMap().put(doSave, saveAction);
			editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), doUndo);
			editor.getActionMap().put(doUndo, undoAction);
			editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK), doRedo);
			editor.getActionMap().put(doRedo, redoAction);
			
			JScrollPane sp = new JScrollPane ();
			sp.getViewport ().add (editor);
			final UndoManager m = new UndoManager ();
			sp.putClientProperty (UNDO_MANAGER, m);
			sp.putClientProperty (DOCUMENT, doc);
			sp.putClientProperty (EDITOR, editor);
			final JTextArea ed = editor;

			class Handler implements UndoableEditListener, Disposable,
				DocumentListener
			{
				public void undoableEditHappened (UndoableEditEvent e)
				{
					m.addEdit (e.getEdit ());
					update (ed, m, doc);
				}

				public void dispose ()
				{
					doc.removeUndoableEditListener (this);
					doc.removeDocumentListener (this);
					try
					{
						ed.setDocument (null);
					}
					catch (NullPointerException e)
					{
					}
				}

				public void changedUpdate (DocumentEvent e)
				{
					update (ed, m, (Doc) e.getDocument ());
				}

				public void insertUpdate (DocumentEvent e)
				{
					changedUpdate (e);
				}

				public void removeUpdate (DocumentEvent e)
				{
					changedUpdate (e);
				}
			}

			Handler h = new Handler ();				
			sp.putClientProperty (HANDLER, h);
			doc.addUndoableEditListener (h);
			doc.addDocumentListener (h);
			tab.addTab (String.valueOf
						(doc.getProperty (Doc.TitleProperty)),
						IconAdapter.create
							(UI.getIcon (doc.systemId,
											  doc.mimeType.getMediaType (),
											  null, this, true),
							 SwingToolkit.MENU_ICON_SIZE), sp);
			tab.setSelectedComponent (sp);
			update (editor, m, doc);
		}
		editor.requestFocus ();
		Point[] range = UI.parsePlainTextRange (ref);
		if (range != null)
		{
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
					editor.select (start, (p.x >= 0)
								   ? editor.getLineStartOffset (p.y) + p.x
								   : editor.getLineEndOffset (p.y));
				}
			}
			catch (BadLocationException e)
			{
			}
		}
	}


	private static final int OPEN_DOC = MIN_UNUSED_ACTION;
	private static final int CLOSE_DOC = MIN_UNUSED_ACTION + 1;
	private static final int UPDATE = MIN_UNUSED_ACTION + 2;

	@Override
	public Object run (int action, int iarg, Object arg, Object arg2)
	{
		switch (action)
		{
			case OPEN_DOC:
			{
				openDocumentSync ((String) arg, (String) arg2);
				break;
			}
			case CLOSE_DOC:
			{
				for (int i = 0; i < tab.getTabCount (); i++)
				{
					if (((JComponent) tab.getComponentAt (i))
						.getClientProperty (DOCUMENT) == arg)
					{
						Disposable d = (Disposable)
							((JComponent) tab.getComponentAt (i))
							.getClientProperty (HANDLER);
						tab.removeTabAt (i);
						d.dispose ();
						break;
					}
				}
				break;
			}
			case UPDATE:
			{
				update ((JComponent) arg);
				break;
			}
			default:
				return super.run (action, iarg, arg, arg2);
		}
		return null;
	}


	public void openDocument (String doc, String ref)
	{
		sync.invokeAndWait (OPEN_DOC, 0, doc, ref);
	}


	void closeDocument (Doc doc)
	{
		sync.invokeAndWait (CLOSE_DOC, doc);
	}

/*
	void resetUndoManager ()
	{
		undo.discardAllEdits ();
		update ();
	}
  */  

	void update (JTextArea editor, UndoManager m, Doc doc)
	{
		for (int i = 0; i < tab.getTabCount (); i++)
		{
			if (((JComponent) tab.getComponentAt (i))
				.getClientProperty (DOCUMENT) == doc)
			{
				String s = String.valueOf
					(doc.getProperty (Doc.TitleProperty));
				if (doc.modified)
				{
					s = "* " + s + " *";
				}
				if (!s.equals (tab.getTitleAt (i)))
				{
					tab.setTitleAt (i, s);
				}
				break;
			}
		}
		undoAction.setEnabled ((m != null) && m.canUndo ());
		redoAction.setEnabled ((m != null) && m.canRedo ());
		saveAction.setEnabled ((doc != null) && doc.modified);
		cutAction.setEnabled (editor != null);
		copyAction.setEnabled (editor != null);
		pasteAction.setEnabled (editor != null);
		closeAction.setEnabled (editor != null);
	}


	void update (JComponent c)
	{
		if (c == null)
		{
			update (null, null, null);
		}
		else
		{
			update ((JTextArea) c.getClientProperty (EDITOR),
					(UndoManager) c.getClientProperty (UNDO_MANAGER),
					(Doc) c.getClientProperty (DOCUMENT));
		}
	}


	void actionPerformed (ActionEvent a)
	{
		final JComponent c = (JComponent) tab.getSelectedComponent ();
		if (c == null)
		{
			return;
		}
		UndoManager m = (UndoManager) c.getClientProperty (UNDO_MANAGER);
		JTextArea editor = (JTextArea) c.getClientProperty (EDITOR);
		Doc doc = (Doc) c.getClientProperty (DOCUMENT);
		String cmd = a.getActionCommand ();
		if ("undo".equals (cmd))
		{
			try
			{
				m.undo ();
			}
			catch (CannotUndoException e)
			{
				e.printStackTrace ();
			}
			update (editor, m, doc);
		}
		else if ("redo".equals (cmd))
		{
			try
			{
				m.redo ();
			}
			catch (CannotRedoException e)
			{
				e.printStackTrace ();
			}
			update (editor, m, doc);
		}
		else if ("save".equals (cmd))
		{
			getWorkbench ().getJobManager ().execute
				(new Command ()
					{
						public String getCommandName ()
						{
							return null;
						}

						public void run (Object info, Context context)
						{
							((Doc) info).save ();
							sync.invokeAndWait (UPDATE, c);
						}
					},
				 doc, this, JobManager.ACTION_FLAGS);
		}
		else if ("cut".equals (cmd))
		{
			editor.cut ();
		}
		else if ("copy".equals (cmd))
		{
			editor.copy ();
		}
		else if ("paste".equals (cmd))
		{
			editor.paste ();
		}
		else if ("close".equals (cmd))
		{
			getWorkbench ().getJobManager ().execute
				(new Command ()
					{
						public void run (Object info, Context context)
						{
							if (canClose ((Doc) info))
							{
								closeDocument ((Doc) info);
							}
						}

						public String getCommandName ()
						{
							return null;
						}
					},
				 doc, this, JobManager.ACTION_FLAGS);
		}
	}


	public void stateChanged (ChangeEvent e)
	{
		update ((JComponent) tab.getSelectedComponent ());
	}


	@Override
	protected void disposeImpl ()
	{
		for (int i = 0; i < tab.getTabCount (); i++)
		{
			((Disposable) ((JComponent) tab.getComponentAt (i))
			 .getClientProperty (HANDLER)).dispose ();
		}
		super.disposeImpl ();
	}


	@Override
	public void checkClose (Runnable ok)
	{
		executeCheckClose (ok);
	}


	boolean canClose (Doc d)
	{
		return !d.modified
			|| (getWindow ().showDialog
				(UI.I18N.msg ("text.savequestion.title"),
				 UI.I18N.msg ("text.savequestion.msg",
								   d.getProperty (Doc.TitleProperty)),
				 Window.QUESTION_MESSAGE) == Window.YES_OK_RESULT);
	}


	@Override
	public void checkClose (Command ok)
	{
		for (int i = 0; i < tab.getTabCount (); i++)
		{
			Doc d = (Doc) ((JComponent) tab.getComponentAt (i))
				.getClientProperty (DOCUMENT);
			if (!canClose (d))
			{
				return;
			}
		}
		ok.run (null, this);
	}


	public String[] getDocuments ()
	{
		int n = tab.getTabCount ();
		String[] d = new String[n];
		for (int i = 0; i < n; i++)
		{
			d[i] = ((Doc) ((JComponent) tab.getComponentAt (i))
					.getClientProperty (DOCUMENT)).systemId;
		}
		return d;
	}


	public void addMappings (ModifiableMap out)
	{
		UI.putDocuments (this, out);
		Component c = tab.getSelectedComponent ();
		if (c != null)
		{
			out.put ("selected",
					 ((Doc) ((JComponent) c).getClientProperty (DOCUMENT))
					 .systemId);
		}
	}

}
