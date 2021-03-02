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

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.border.EmptyBorder;
import de.grogra.pf.registry.*;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileTypeItem;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.pf.ui.registry.*;
import de.grogra.pf.ui.awt.HideComponent;
import de.grogra.icon.*;
import de.grogra.util.*;
import de.grogra.xl.lang.ObjectToBoolean;
import de.grogra.xl.util.ObjectList;
import de.grogra.docking.*;

public final class WindowSupport extends PanelSupport implements Window, EventListener
{
	RootPane frame;
	SwingToolkit manager;
	Workbench workbench;
	DockManager dockManager;
	JFileChooser fileChooser;

	private volatile Dockable[] windowsToDispose;

	private Cursor transparentCursor = null;
	private boolean transparentNotCreated = true;
	private Robot robot = null;
	private boolean robotNotCreated = true;
	private Point framePosition = null;

	private final Command close;

	static final Dimension FILE_ICON_SIZE = new Dimension (16, 16);

	WindowSupport (SwingToolkit manager, Command close, Map params)
	{
		super (new RootPane ());
		this.close = close;
		this.frame = (RootPane) getComponent ();
		this.manager = manager;
		initialize (this, params);
		DockContentPane d = new DockContentPane ();
		frame.setContentPane (d);
		
		// determine new window position based on selected screen
		Integer screenId = (Integer) params.get(Main.SCREEN_PROPERTY, null);
		if (screenId != null) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice screen = ge.getDefaultScreenDevice();
			GraphicsDevice[] screens = ge.getScreenDevices();
			if (screens.length > screenId)
				screen = screens[screenId];
			this.framePosition = screen.getDefaultConfiguration().getBounds().getLocation();
		}
		
		dockManager = new DockManager (frame, d);
	}

	@Override
	public Workbench getWorkbench ()
	{
		return workbench;
	}

	public void initializeWorkbench (Workbench workbench)
	{
		assert this.workbench == null;
		this.workbench = workbench;
		UIProperty.WORKBENCH_TITLE.addPropertyListener (workbench, this);
	}

	private static final int DISPOSE = MIN_UNUSED_ACTION + 0;
	private static final int SET_TITLE = MIN_UNUSED_ACTION + 1;
	private static final int CHOOSE_FILE = MIN_UNUSED_ACTION + 2;
	private static final int SET_LAYOUT = MIN_UNUSED_ACTION + 3;
	private static final int GET_LAYOUT = MIN_UNUSED_ACTION + 4;
	private static final int SHOW_DIALOG = MIN_UNUSED_ACTION + 5;
	private static final int SHOW_DIALOG_IMPL = MIN_UNUSED_ACTION + 6;
	private static final int SHOW_INPUT_DIALOG = MIN_UNUSED_ACTION + 7;
	private static final int GET_DOCKABLES = MIN_UNUSED_ACTION + 8;

	@Override
	public Object run (int action, int iarg, Object arg, Object arg2)
	{
		switch (action)
		{
			case DISPOSE:
				windowsToDispose = dockManager.dispose ();
				setVisibleSync (false, null, false);
				break;
			case SET_TITLE:
				if (frame.getParent () != null)
				{
					frame.frame.setTitle (String.valueOf (arg));
				}
				break;
			case CHOOSE_FILE:
				chooseFileSync (iarg, (FileChooserResult) arg,
						(FileFilter[]) arg2);
				break;
			case SET_LAYOUT:
				((Command) arg).run (dockManager.setLayout (), null);
				break;
			case GET_LAYOUT:
				return getLayoutSync ();
			case SHOW_DIALOG:
				return showDialogSync ((String) arg, arg2, iarg);
			case SHOW_DIALOG_IMPL:
				return showDialogSyncImpl ((JDialog) arg, (JButton) arg2) ? this
						: null;
			case SHOW_INPUT_DIALOG:
				return showInputDialogSync ((String[]) arg, arg2);
			case GET_DOCKABLES:
				return getDockablesSync (arg);
			default:
				return super.run (action, iarg, arg, arg2);
		}
		return null;
	}
	
	public boolean isVisible ()
	{
		return frame.getParent () != null;
	}

	@Override
	void setVisibleSync (boolean visible, Panel keepInFront, boolean moveToFront)
	{
		if (visible)
		{
			if (frame.getParent () != null)
			{
				frame.frame.toFront ();
				return;
			}
			manager.showInFrame (frame, String
					.valueOf (UIProperty.WORKBENCH_TITLE.getValue (workbench)),
					this, framePosition);
			dockManager.showFloatingWindows ();
		}
		else
		{
			if (frame.getParent () == null)
			{
				return;
			}
			dockManager.hideFloatingWindows ();
			manager.releaseFrame (frame, this);
		}
	}

	Cursor getTransparentCursor ()
	{
		if (transparentNotCreated)
		{
			/*			transparentNotCreated = false;
			 try 
			 {
			 transparentCursor = getToolkit ()
			 .createCustomCursor (de.grogra.util.icon
			 .IconResource.TRANSPARENT_2X2
			 .getImage (),
			 new Point (), "transparent");
			 }
			 catch (Exception e)
			 {
			 }
			 */
		}
		return transparentCursor;
	}

	Robot getRobot ()
	{
		if (robotNotCreated)
		{
			robotNotCreated = false;
			try
			{
				robot = new Robot (frame.frame.getGraphicsConfiguration ()
						.getDevice ());
			}
			catch (Exception e)
			{
			}
		}
		return robot;
	}

	public void setLayout (final Layout layout, final Map params)
	{
		Panel[] p = getPanels (null);
		final HashMap oldMap = new HashMap (p.length), newMap = new HashMap (
				p.length);
		for (int i = 0; i < p.length; i++)
		{
			oldMap.put (p[i].getPanelId (), p[i]);
		}
		Command s = new Command ()
		{
			public void run (Object info, Context ctx)
			{
				if (info != null)
				{
					LayoutConsumer lc = (LayoutConsumer) info;
					lc.startLayout ();
					supply (layout, lc);
					lc.endLayout ();
				}
				else
				{
					supply (layout, null);
				}
			}

			public String getCommandName ()
			{
				return null;
			}

			private void supply (Item o, LayoutConsumer lc)
			{
				Item i = o.resolveLink (getWorkbench ());
				if (i == null)
				{
					System.err.println (o + " cannot be resolved");
					return;
				}
				if (lc == null)
				{
					if (i instanceof PanelFactory)
					{
						String id = (String) o.get (Panel.PANEL_ID, null);
						if (id == null)
						{
							id = (String) i.get (Panel.PANEL_ID, null);
						}
						Panel p = (Panel) oldMap.remove (id);
						if (p != null)
						{
							((PanelSupport) p.resolve ()).configure (i);
						}
						else
						{
							p = ((PanelFactory) i).createPanel (
									WindowSupport.this, params);
						}
						if (p != null)
						{
							newMap.put (o, p);
						}
					}
					else
					{
						supplyChildren (i, lc);
					}
					return;
				}
				if (i instanceof MainWindow)
				{
					lc.startMainWindow ();
					supplyChildren (i, lc);
					setMenuVisibility(((MainWindow) i).isMenuVisible());
					lc.endMainWindow ();
				}
				else if (i instanceof de.grogra.pf.ui.registry.FloatingWindow)
				{
					de.grogra.pf.ui.registry.FloatingWindow fi = (de.grogra.pf.ui.registry.FloatingWindow) i;
					lc.startFloatingWindow (String.valueOf (fi
							.getDescription (Described.NAME)), fi.getWidth (),
							fi.getHeight ());
					supplyChildren (i, lc);
					lc.endFloatingWindow ();
				}
				else if (i instanceof Split)
				{
					Split si = (Split) i;
					lc.startSplit (si.getOrientation (), si.getLocation ());
					supplyChildren (i, lc);
					lc.endSplit ();
				}
				else if (i instanceof Tab)
				{
					lc.startTabbed (((Tab) i).getSelectedIndex ());
					supplyChildren (i, lc);
					lc.endTabbed ();
				}
				else if (i instanceof PanelFactory)
				{
					Panel p = (Panel) newMap.get (o);
					if (p != null)
					{
						lc.addDockable ((Dockable) p.getComponent ());
					}
				}
				else
				{
					supplyChildren (i, lc);
				}
			}

			private void supplyChildren (Item i, LayoutConsumer lc)
			{
				for (i = (Item) i.getBranch (); i != null; i = (Item) i
						.getSuccessor ())
				{
					supply (i, lc);
				}
			}
		};
		s.run (null, null);
		sync.invokeAndWait (SET_LAYOUT, s);
		UIProperty.WINDOW_LAYOUT.setValue (this, layout);
	}

	public Layout getLayout ()
	{
		return (Layout) sync.invokeAndWait (GET_LAYOUT);
	}

	private Layout getLayoutSync ()
	{
		final ObjectList stack = new ObjectList ().push (new Layout (null));
		dockManager.supply (new LayoutConsumer ()
		{
			private void start (Item child)
			{
				((Item) stack.peek (1)).appendBranchNode (child);
				stack.push (child);
			}

			private void end ()
			{
				stack.pop ();
			}

			public void startLayout ()
			{
			}

			public void endLayout ()
			{
			}

			public void startMainWindow ()
			{
				start (new MainWindow (null));
			}

			public void endMainWindow ()
			{
				end ();
			}

			public void startFloatingWindow (String title, int width, int height)
			{
				start (new de.grogra.pf.ui.registry.FloatingWindow (null,
						width, height));
			}

			public void endFloatingWindow ()
			{
				end ();
			}

			public void startSplit (int orientation, float location)
			{
				start (new Split (null, orientation, location));
			}

			public void endSplit ()
			{
				end ();
			}

			public void startTabbed (int selectedIndex)
			{
				start (new Tab (null, selectedIndex));
			}

			public void endTabbed ()
			{
				end ();
			}

			private final StringMap map = new StringMap ();

			public void addDockable (Dockable dockable)
			{
				if (dockable instanceof SwingDockable)
				{
					PanelSupport p = ((SwingDockable) dockable).getSupport ();
					map.clear ();
					p.addParameters (map);
					String id = p.getPanelId ();
					String title = (String) UIProperty.PANEL_TITLE.getValue (p);
					int i = id.indexOf ('?');
					if (i >= 0)
					{
						id = id.substring (0, i);
					}
					if ((id != p.getPanelId ()) || !map.isEmpty ())
					{
						map.put ("panelId", p.getPanelId ());
					}
					Item link;
					if (map.isEmpty ())
					{
						link = new Link (null, id);
						PanelFactory f = (PanelFactory) Item.resolveItem (getWorkbench (), id);
						if ((f == null)
							|| !Utils.equal (title, f.getDefaultTitle ()))
						{
							map.put (UIProperty.PANEL_TITLE.getName (), title);
						}
					}
					else
					{
						link = new PanelFactory (null, id);
						map.put (UIProperty.PANEL_TITLE.getName (), title);
						for (i = 0; i < map.size (); i++)
						{
							link.appendBranchNode (Option.createNoneditableOption (map
									.getKeyAt (i), map.getValueAt (i)));
						}
					}
					((Item) stack.peek (1)).appendBranchNode (link);
				}
			}
		});
		return (Layout) stack.get (0);
	}

	private Panel[] getDockablesSync (final Object filter)
	{
		final String panelId = (filter instanceof String) ? (String) filter
				: null;
		Dockable[] d = dockManager.getDockables ((filter == null) ? null
				: new DockableFilter ()
				{
					public boolean accept (Dockable o)
					{
						return (o instanceof SwingDockable)
								&& ((panelId != null) ? panelId
										.equals (((SwingDockable) o)
												.getSupport ().getPanelId ())
										: ((ObjectToBoolean) filter)
												.evaluateBoolean (((SwingDockable) o)
														.getSupport ()));
					}
				});
		Panel[] p = new Panel[d.length];
		for (int i = 0; i < d.length; i++)
		{
			p[i] = ((SwingDockable) d[i]).getSupport ().unresolve ();
		}
		return p;
	}

	public Panel[] getPanels (final ObjectToBoolean filter)
	{
		return (Panel[]) sync.invokeAndWait (GET_DOCKABLES, filter);
	}

	public Panel getPanel (final String panelId)
	{
		Panel[] p = (Panel[]) sync.invokeAndWait (GET_DOCKABLES, panelId);
		return (p != null && p.length > 0) ? p[0] : null;
	}

	@Override
	public void setContent (ComponentWrapper content)
	{
		if (content != null)
		{
			throw new UnsupportedOperationException ();
		}
	}

	@Override
	protected void disposeImpl ()
	{
		UIProperty.WORKBENCH_TITLE
				.removePropertyListener (this.workbench, this);
		sync.invokeAndWait (DISPOSE);
		for (int i = windowsToDispose.length - 1; i >= 0; i--)
		{
			((SwingDockable) windowsToDispose[i]).dockableClosed ();
		}
		windowsToDispose = null;
		fileChooser = null;
		workbench = null;
	}

	public void eventOccured (EventObject event)
	{
		if (event instanceof UIPropertyEditEvent)
		{
			sync.invokeAndWait (SET_TITLE, ((UIPropertyEditEvent) event)
					.getNewValue ());
		}
	}


	/**
	 * 
	 * @param title
	 * @param directory
	 * @param filters
	 * @param type
	 * @param mustExist
	 * @param selectedFilter - the selected file filter type
	 */
	public FileChooserResult chooseFile (String title, java.io.File directory,
			FileFilter[] filters, int type, boolean mustExist, FileFilter selectedFilter)
	{
		if ((directory != null) && !directory.isDirectory ())
		{
			directory = null;
		}
		JFileChooser c = fileChooser;
		if (c == null)
		{
			fileChooser = c = new JFileChooser ()
			{
				@Override
				public javax.swing.Icon getIcon (java.io.File f)
				{
					IconSource is = null;
					if (f.isDirectory ())
					{
						is = (IconSource) UI.I18N
								.getObject ("registry.directory.Icon");
					}
					else
					{
						FileTypeItem i = FileTypeItem.get (getWorkbench (), f
								.getName ());
						if (i != null)
						{
							is = (IconSource) i.getDescription (Described.ICON);
						}
						if (is == null)
						{
							is = (IconSource) UI.I18N
									.getObject ("registry.file.Icon");
						}
					}
					return (is == null) ? super.getIcon (f) : IconAdapter
							.create (is, FILE_ICON_SIZE);
				}
			};
			if (selectedFilter != null) {
				fileChooser.setFileFilter(selectedFilter);
			}
			new LAFUpdateListener (c);
		}
		c.setDialogTitle (title);
		FileChooserResult r = new FileChooserResult ();
		r.file = directory;
		sync.invokeAndWait (CHOOSE_FILE, type, r, filters);
		return r.validate (mustExist, type);
	}
	
	
	private void chooseFileSync (int type, FileChooserResult r,
			FileFilter[] filters)
	{
		JFileChooser c = fileChooser;
		FileFilter oldFilter = c.getFileFilter ();
		c.resetChoosableFileFilters ();
		if ((filters != null) && (filters.length > 0))
		{
			FileFilter f = filters[0];
			for (int i = 0; i < filters.length; i++)
			{
				c.addChoosableFileFilter (filters[i]);
				if (filters[i] == oldFilter)
				{
					f = oldFilter;
				}
			}
			c.setFileFilter (f);
		}
		else
		{
			c.setFileFilter (c.getAcceptAllFileFilter ());
		}
		c.setCurrentDirectory (r.file);
		r.file = null;
		c.rescanCurrentDirectory ();
		int res;
		switch (type)
		{
			case OPEN_FILE:
				res = c.showOpenDialog (frame.frame);
				break;
			case ADD_FILE:
				res = c.showDialog (frame.frame, UI.I18N
						.getString ("filedialog.addbutton"));
				break;
			case SAVE_FILE:
				res = c.showSaveDialog (frame.frame);
				break;
			default:
				throw new IllegalArgumentException ("Type = " + type);
		}
		if (res == JFileChooser.APPROVE_OPTION)
		{
			r.file = c.getSelectedFile ();
			r.filter = c.getFileFilter ();
		}
		c.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
		c.setFileSelectionMode (JFileChooser.FILES_ONLY);
	}

	public int showDialog (final String title, final Object component,
			final int type)
	{
		final int[] d = new int[1];
		workbench.getJobManager ().runBlocking (new Runnable ()
		{
			public void run ()
			{
				d[0] = ((Integer) sync.invokeAndWait (SHOW_DIALOG, type, title,
						component)).intValue ();
			}
		});
		return d[0];
	}

	private int showDialogSync (String title, Object component, int type)
	{
		switch (type)
		{
			case PLAIN_MESSAGE:
			case INFORMATION_MESSAGE:
			case WARNING_MESSAGE:
			case ERROR_MESSAGE:
				JOptionPane.showMessageDialog (frame.frame, component, title,
						type);
				return YES_OK_RESULT;
			case QUESTION_MESSAGE:
				return (JOptionPane.showConfirmDialog (frame.frame, component,
						title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) ? YES_OK_RESULT
						: NO_RESULT;
			case QUESTION_CANCEL_MESSAGE:
				switch (JOptionPane.showConfirmDialog (frame.frame, component,
						title, JOptionPane.YES_NO_CANCEL_OPTION))
				{
					case JOptionPane.YES_OPTION:
						return YES_OK_RESULT;
					case JOptionPane.NO_OPTION:
						return NO_RESULT;
					default:
						return CANCEL_RESULT;
				}
			case RESIZABLE_PLAIN_MESSAGE:
			case RESIZABLE_OK_CANCEL_MESSAGE:
				JDialog d = new JDialog (frame.frame, title, true);
				Container c = d.getContentPane ();
				c.setLayout (new BorderLayout (10, 10));
				c.add ((Component) component, BorderLayout.CENTER);
				c.add (c = new Container (), BorderLayout.SOUTH);
				JButton b;
				if (type == RESIZABLE_OK_CANCEL_MESSAGE)
				{
					c.setLayout (new GridBagLayout ());
					GridBagConstraints gc = new GridBagConstraints ();
					gc.weightx = 1;
					b = new JButton (UI.I18N.getString ("dialog.okbutton"));
					c.add (b, gc);
					JButton cancel = new JButton (UI.I18N
							.getString ("dialog.cancelbutton"));
					cancel.addActionListener (new HideComponent (d));
					c.add (cancel, gc);
				}
				else
				{
					c.setLayout (new BorderLayout ());
					b = new JButton (UI.I18N.getString ("dialog.closebutton"));
					c.add (b, BorderLayout.EAST);
				}
				return showDialogSyncImpl (d, b)
						|| (type != RESIZABLE_OK_CANCEL_MESSAGE) ? YES_OK_RESULT
						: CANCEL_RESULT;
			default:
				throw new IllegalArgumentException ("Dialog type " + type);
		}
	}

	public String showInputDialog (String title, Object component,
			String initial)
	{
		return (String) sync.invokeAndWait (SHOW_INPUT_DIALOG, 0, new String[] {
				title, initial}, component);
	}

	private String showInputDialogSync (String[] titleInitial, Object message)
	{
		return (String) JOptionPane.showInputDialog (frame.frame, message,
				titleInitial[0], JOptionPane.QUESTION_MESSAGE, null, null,
				titleInitial[1]);
	}

	public int showChoiceDialog (String title, I18NBundle bundle,
			String keyBase, String[] options)
	{
		Container c = new Container ();
		c.setLayout (new GridBagLayout ());
		GridBagConstraints gc = new GridBagConstraints ();
		gc.insets.top = 10;
		gc.insets.bottom = 10;
		gc.fill = GridBagConstraints.HORIZONTAL;
		String s = bundle.getString (keyBase + '.'
				+ Described.SHORT_DESCRIPTION, null);
		if (s != null)
		{
			gc.gridwidth = GridBagConstraints.REMAINDER;
			c.add (new JLabel (s), gc);
		}
		ButtonGroup bg = new ButtonGroup ();
		AbstractButton b;
		AbstractButton[] buttons = new AbstractButton[options.length];
		for (int i = 0; i < options.length; i++)
		{
			gc.gridwidth = 1;
			gc.weightx = 0;
			gc.anchor = GridBagConstraints.WEST;
			b = new JRadioButton ();
			bg.add (b);
			buttons[i] = b;
			if (i == 0)
			{
				bg.setSelected (b.getModel (), true);
			}
			c.add (b, gc);
			String base = keyBase + '.' + options[i] + '.';
			String n = bundle.getString (base + Described.NAME, null);
			s = bundle.getString (base + Described.SHORT_DESCRIPTION, null);
			if (n == null)
			{
				n = (s == null) ? options[i] : s;
				s = null;
			}
			IconSource is = (IconSource) bundle.getObject (base
					+ Described.ICON, null);
			JLabel l = (is != null) ? new JLabel (n, IconAdapter.create (is,
					UIToolkit.MENU_ICON_SIZE), JLabel.LEADING) : new JLabel (n);
			gc.anchor = GridBagConstraints.WEST;
			gc.gridwidth = GridBagConstraints.REMAINDER;
			gc.weightx = 1;
			c.add (l, gc);
			if (s != null)
			{
				gc.gridx = 1;
				gc.insets.top = 0;
				c.add (new JLabel (s), gc);
				gc.insets.top = 10;
				gc.gridx = GridBagConstraints.RELATIVE;
			}
		}

		JDialog d = new JDialog (frame.frame, (title != null) ? title : bundle
				.getString (keyBase + '.' + Described.TITLE, keyBase), true);
		d.getContentPane ().setLayout (new BorderLayout (10, 10));
		d.getContentPane ().add (c, BorderLayout.CENTER);
		c = new Container ();
		c.setLayout (new GridBagLayout ());

		gc = new GridBagConstraints ();
		gc.weightx = 1;
		JButton def = new JButton (UI.I18N.getString ("dialog.okbutton"));
		c.add (def, gc);
		b = new JButton (UI.I18N.getString ("dialog.cancelbutton"));
		b.addActionListener (new HideComponent (d));
		c.add (b, gc);
		d.getContentPane ().add (c, BorderLayout.SOUTH);
		if (sync.invokeAndWait (SHOW_DIALOG_IMPL, 0, d, def) != null)
		{
			for (int i = 0; i < options.length; i++)
			{
				if (bg.isSelected (buttons[i].getModel ()))
				{
					return i;
				}
			}
		}
		return -1;
	}

	private boolean showDialogSyncImpl (JDialog d, JButton defaultButton)
	{
		d.setDefaultCloseOperation (JDialog.HIDE_ON_CLOSE);
		HideComponent hide;
		if (d.isModal ())
		{
			if (d.getContentPane () instanceof JComponent)
			{
				JComponent j = (JComponent) d.getContentPane ();
				j.setBorder (new EmptyBorder (10, 10, 10, 10));
				ComponentInputMap m = new ComponentInputMap (j);
				m.put (KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), this);
				j.setInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW, m);
				ActionMap a = new ActionMap ();
				a.put (this, new HideComponent (d));
				j.setActionMap (a);
			}
			defaultButton.setDefaultCapable (true);
			hide = new HideComponent (d);
			defaultButton.addActionListener (hide);
			d.getRootPane ().setDefaultButton (defaultButton);
		}
		else
		{
			hide = null;
		}

		d.pack ();
		java.awt.Dimension s = d.getSize ();
		int w = Math.min (Math.max (s.width, 300), 700);
		int h = Math.min (Math.max (s.height, 200), 500);
		if ((w != s.width) || (h != s.height))
		{
			d.setSize (w, h);
			d.validate ();
		}
		d.setLocationRelativeTo (frame.frame);
		d.setVisible (true);
		if (d.isModal ())
		{
			d.dispose ();
		}
		return (hide != null) && (hide.lastEvent != null);
	}

	public Disposable showWaitMessage (String toComplete)
	{
		final JDialog d = new JDialog (frame.frame, UI.I18N
				.msg ("waitingmessage.title"), false);
		d.getContentPane ().add (
				new JLabel ((toComplete != null) ? UI.I18N.msg (
						"waitingmessage.text", toComplete) : UI.I18N
						.msg ("waitingmessage.text0")));
		JProgressBar b = new JProgressBar ();
		d.getContentPane ().add (b, BorderLayout.SOUTH);
		b.setIndeterminate (true);
		EventQueue.invokeLater (new Runnable ()
			{
				public void run ()
				{
					showDialogSyncImpl (d, null);
				}
			});
		return new de.grogra.pf.ui.awt.DisposeWindow (d);
	}

	void closeRequested ()
	{
		getWorkbench ().getJobManager ().runLater (close, null, this,
				JobManager.ACTION_FLAGS);
	}

	public void setMenuVisibility(boolean value) {
//		JMenuBar bar;
		((JMenuBar) menu.getRoot()).setVisible(value);
	    
	}

}
