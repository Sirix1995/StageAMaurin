
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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import de.grogra.docking.LAFUpdateListener;
import de.grogra.icon.IconAdapter;
import de.grogra.icon.IconSource;
import de.grogra.persistence.Transaction;
import de.grogra.persistence.XAListener;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Option;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.pf.ui.ChartPanel;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.Console;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.TextEditor;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Widget;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.awt.AWTSynchronizer;
import de.grogra.pf.ui.awt.AWTToolkitBase;
import de.grogra.pf.ui.awt.AWTTree;
import de.grogra.pf.ui.awt.AWTWidgetSupport;
import de.grogra.pf.ui.awt.ContentPaneContainer;
import de.grogra.pf.ui.edit.EnumerationEditor;
import de.grogra.pf.ui.tree.SyncMappedList;
import de.grogra.pf.ui.tree.UINodeHandler;
import de.grogra.pf.ui.tree.UITree;
import de.grogra.pf.ui.util.ComponentWrapperImpl;
import de.grogra.pf.ui.util.LinearConversion;
import de.grogra.pf.ui.util.Numeric2String;
import de.grogra.pf.ui.util.WidgetList;
import de.grogra.reflect.BoundedType;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.Described;
import de.grogra.util.Disposable;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;
import de.grogra.util.ModifiableMap;
import de.grogra.util.Quantity;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.xl.util.ObjectList;

public class SwingToolkit extends AWTToolkitBase implements XAListener
{
	public static final I18NBundle I18N
		= I18NBundle.getInstance (SwingToolkit.class);


	private static final class Frame extends JFrame
	{
		Frame ()
		{
			setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
			
			// get icon and set it if found
			try {
				String bootPath = Main.getProperty("boot");
				Image image = ImageIO.read(new File(bootPath + "/groimp.png"));
				setIconImage(image);
			} catch(Exception ex) {
				Main.logWarning(ex);
			}

			new LAFUpdateListener (this);
		}

		void setRootPane (RootPane root)
		{
			if ((root != null)
				&& !Utils.equal (root.currentLAF,
								 javax.swing.UIManager.getLookAndFeel ()))
			{
				SwingUtilities.updateComponentTreeUI (root);
			}
			super.setRootPane (root);
		}
	}


	static final int TEXT = 1;
	static final int TOOLBAR_ICON = 2;
	static final int MENU_ICON = 4;

	static final IconAdapter MENU_ICON_DUMMY = new IconAdapter
		(null, null, MENU_ICON_SIZE.width, MENU_ICON_SIZE.height, true,
		 de.grogra.icon.Icon.DEFAULT);

	final StringMap lafInfos = new StringMap ();
	private static final LookAndFeelInfo[] lafsToCheck = {
		new LookAndFeelInfo ("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
	};

	final Item config;
	String currentLaf;
	final Object lafLock = new Object ();
	boolean lafUpdatePending;

	private final ObjectList frames = new ObjectList (5, false);


	public SwingToolkit (Item item)
	{
		this.config = item;
		LookAndFeelInfo[] infos = javax.swing.UIManager.getInstalledLookAndFeels ();
		Item lafs = item.getItem ("lafs");
		item = Option.get (item, "laf");
		if (item != null)
		{
			item = (Item) item.getBranch ();
			if (item instanceof EnumerationEditor)
			{
				for (int i = 0; i < infos.length; i++)
				{
					String s = infos[i].getName ();
					if (!lafInfos.containsKey (s))
					{
						lafInfos.put (s, infos[i]);
					}
				}
				for (int i = 0; i < lafsToCheck.length; i++)
				{
					try
					{
						Class.forName (lafsToCheck[i].getClassName (), false,
									   getClass ().getClassLoader ());
						String s = lafsToCheck[i].getName ();
						if (!lafInfos.containsKey (s))
						{
							lafInfos.put (s, lafsToCheck[i]);
						}
					}
					catch (ClassNotFoundException e)
					{
					}
				}
				if (lafs != null)
				{
					for (Item i = (Item) lafs.getBranch (); i != null;
						 i = (Item) i.getSuccessor ())
					{
						if (i instanceof LAF)
						{
							String s = i.getName ();
							if (!lafInfos.containsKey (s))
							{
								lafInfos.put (s, i);
							}
						}
					}
				}
				DefaultListModel list = new DefaultListModel ();
				for (int i = 0; i < lafInfos.size (); i++)
				{
					list.addElement (lafInfos.getKeyAt (i));
				}
				((EnumerationEditor) item).setList (list);
			}
		}
		updateLaf ();
		if (lafUpdatePending)
		{
			synchronized (lafLock)
			{
				try
				{
					lafLock.wait (2000);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
		config.getPersistenceManager ().addXAListener (this);
	}


	public void transactionApplied (Transaction.Data xa, boolean rollback)
	{
		updateLaf ();
	}


	private void updateLaf ()
	{
		final String name = (String) config.get ("laf", null);
		Boolean d = (Boolean) config.get ("windowDecorations", null);
		if (d == null)
		{
			d = Boolean.FALSE;
		}
		final Boolean decorated
			= (d.booleanValue () == JFrame.isDefaultLookAndFeelDecorated ())
			? null : d;
		final int lafIndex;
		if ((name != null) && !name.equals (currentLaf))
		{
			currentLaf = name;
			lafIndex = lafInfos.findIndex (name);
		}
		else
		{
			lafIndex = -1;
		}
		if (lafUpdatePending = (decorated != null) || (lafIndex >= 0))
		{
			EventQueue.invokeLater (new Runnable ()
			{
				public void run ()
				{
					if (decorated != null)
					{
						JFrame.setDefaultLookAndFeelDecorated
							(decorated.booleanValue ());
					}
					try
					{
						if (lafIndex >= 0)
						{
							Object laf = lafInfos.getValueAt (lafIndex);
							if (laf instanceof LAF)
							{
								LAFUpdateListener.setLookAndFeel
									(((LAF) laf).getLAF ());
							}
							else
							{
								LAFUpdateListener.setLookAndFeel
									(((LookAndFeelInfo) laf).getClassName ());
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					synchronized (lafLock)
					{
						lafUpdatePending = false;
						lafLock.notifyAll ();
					}
				}
			});
		}
	}


	void releaseFrame (RootPane root, Context ctx)
	{
		Frame f = (Frame) root.getParent ();
		ModifiableMap opt = UI.getOptions (ctx);
		opt.put ("windowwidth", f.getWidth ());
		opt.put ("windowheight", f.getHeight ());
		opt.put ("windowmaximized", (f.getExtendedState () & Frame.MAXIMIZED_BOTH) != 0);
		synchronized (frames)
		{
			if (frames.size > 1)
			{
				frames.remove (f);
				f.dispose ();
			}
			else
			{
				f.setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
				f.setRootPane (null);
			}
		}
	}


	void showInFrame (RootPane root, String title, Context ctx, Point framePosition)
	{
		boolean newFrame;
		Frame f = null;
		synchronized (frames)
		{
			if (frames.size == 1)
			{
				f = (Frame) frames.get (0);
				if (f.getRootPane () != null)
				{
					f = null;
				}
			}
			if (newFrame = (f == null))
			{
				f = new Frame ();
				f.addWindowListener (this);
				frames.add (f);
			}
		}
		root.frame = f;

		f.setTitle (title);
		f.setCursor (null);
		f.setRootPane (root);
		if (newFrame)
		{
			f.pack ();
			Map opt = UI.getOptions (ctx);
			f.setSize (Utils.getInt (opt, "windowwidth", 750),
					   Utils.getInt (opt, "windowheight", 550));
			if (framePosition != null)
				f.setLocation(framePosition);
			if (Utils.getBoolean (opt, "windowmaximized", false))
			{
				f.setExtendedState (Frame.MAXIMIZED_BOTH);
			}
			f.setVisible (true);
		}
		f.invalidate ();
		f.validate ();
		f.repaint ();
		f.toFront ();
	}


	@Override
	protected Container getContentPane (Object container)
	{
		return (container instanceof ContentPaneContainer)
			? ((ContentPaneContainer) container).getContentPane ()
			: (container instanceof RootPaneContainer)
			? ((RootPaneContainer) container).getContentPane ()
			: (Container) container;
	}


	static void initialize (JComponent c, UITree tree, Object source,
							int modifiers)
	{
		Object o, o2;
		if ((c instanceof AbstractButton) || (c instanceof JLabel))
		{
			boolean noIcon = true;
			if ((modifiers & (TOOLBAR_ICON | MENU_ICON)) != 0)
			{
				o  = tree.getDescription (source, Described.ICON);
				o2 = tree.getDescription (source, Described.SELECTED_ICON);

				javax.swing.Icon i = null;
				javax.swing.Icon i2 = null;
				if (o != null)
				{
					i = IconAdapter.create
						((IconSource) o,
						 ((modifiers & TOOLBAR_ICON) != 0) ? TOOLBAR_ICON_SIZE
						 : MENU_ICON_SIZE);
					noIcon = false;
				}
				if (o2 != null)
				{
					i2 = IconAdapter.create
						((IconSource) o2,
						 ((modifiers & TOOLBAR_ICON) != 0) ? TOOLBAR_ICON_SIZE
						 : MENU_ICON_SIZE);
				}				
				if (i != null)
				{
					if (c instanceof AbstractButton)
					{
						((AbstractButton) c).setIcon (i);
						if (i != null)
						{
							((AbstractButton) c).setSelectedIcon (i2);							
						}
					}
					else
					{
						((JLabel) c).setIcon (i);
					}
				}
			}
			if (noIcon || ((modifiers & TEXT) != 0))
			{
				o = tree.getDescription (source, Described.NAME);
				if (o != null)
				{
					if (c instanceof AbstractButton)
					{
						((AbstractButton) c).setText ((String) o);
					}
					else
					{
						((JLabel) c).setText ((String) o);
					}
				}
			}
		}
		if (c instanceof AbstractButton)
		{
			AbstractButton b = (AbstractButton) c;
			o = tree.getDescription (source, Described.MNEMONIC_KEY);
			if (o instanceof Number)
			{
				b.setMnemonic (((Number) o).intValue ());
			}
			else if (o instanceof Character)
			{
				b.setMnemonic (((Character) o).charValue ());
			}
			else if (o instanceof String)
			{
				try 
				{
					b.setMnemonic (Integer.parseInt ((String) o));
				}
				catch (NumberFormatException e)
				{
					b.setMnemonic (((String) o).charAt (0));
				}
			}
			setDisabledIcon (b);	
		}
		if ((c instanceof JMenuItem) && !(c instanceof JMenu))
		{
			KeyStroke k = getKeyStroke (tree, source);
			if (k != null)
			{
				((JMenuItem) c).setAccelerator (k);
			}
		}
		o = tree.getDescription (source, Described.SHORT_DESCRIPTION);
		if (o != null)
		{
			c.setToolTipText (o.toString ());
		}
	}

	static KeyStroke getKeyStroke (UINodeHandler h, Object node)
	{
		Object o = h.getDescription (node, Described.ACCELERATOR_KEY);
		if (o instanceof String)
		{
			return KeyStroke.getKeyStroke ((String) o);
		}
		else if (o instanceof KeyStroke)
		{
			return (KeyStroke) o;
		}
		else
		{
			return null;
		}
	}

	static void setDisabledIcon (AbstractButton b)
	{
		javax.swing.Icon i = b.getIcon ();
		if (i == null)
		{
			return;
		}
		if (i instanceof IconAdapter)
		{
			b.setDisabledIcon
				(((IconAdapter) i).toState (de.grogra.icon.Icon.DISABLED));
		}
	}


	static final String SOURCE = "de.grogra.pf.ui.swing.SOURCE";

	static Object getSource (Object targetNode)
	{
		return ((JComponent) targetNode).getClientProperty (SOURCE);
	}


	static final String EDITOR_PANE = "de.grogra.pf.ui.swing.EDITOR_PANE";
	
	static JEditorPane getEditorPane (JComponent c)
	{
		return (c instanceof JEditorPane) ? (JEditorPane) c
			: (JEditorPane) c.getClientProperty (EDITOR_PANE);
	}

	
	static void setEditorPage (JEditorPane editor, URL page) throws IOException
	{
		editor.setPage (page);
	}
	
	
	static void checkEditorKit (JEditorPane editor)
	{
		if (editor.getEditorKit () instanceof HTMLEditorKit)
		{
			((HTMLEditorKit) editor.getEditorKit ()).setDefaultCursor (null);
		}
	}

/*
	static boolean disposeListeners (Component c, Class lc)
	{
		boolean isContained = false;
		java.util.EventListener[] l = c.getListeners (lc);
		for (int i = l.length - 1; i >= 0; i--)
		{
			if (l[i] == c)
			{
				isContained = true;
			}
			if (l[i] instanceof Disposable)
			{
				((Disposable) l[i]).dispose ();
			}
		}
		return isContained;
	}
*/
/*
	static void disposeNode (Object o)
	{
/*		if (o instanceof Component)
		{
			Component c = (Component) o;
			if (disposeListeners (c, KeyListener.class)
				| disposeListeners (c, MouseListener.class)
				| disposeListeners (c, MouseMotionListener.class)
				| disposeListeners (c, MouseWheelListener.class)
				| disposeListeners (c, ActionListener.class)
				| disposeListeners (c, ItemListener.class))
			{
				o = null;
			}
		}
* /
		if (o instanceof Disposable)
		{
			((Disposable) o).dispose ();
		}
	}
* /

	static Frame getFrame (EditorFrame w)
	{
		Container c = (Container) w;
		while (c != null)
		{
			if (c instanceof Frame)
			{
				return (Frame) c;
			}
			c = c.getParent ();
		}
		return null;
	}
*/

	@Override
	public Object getParent (Object component)
	{
		Container c = ((Component) component).getParent ();
		if (c instanceof JComponent)
		{
			JRootPane p = ((JComponent) c).getRootPane ();
			if ((p != null) && (p.getContentPane () == c))
			{
				return p.getParent ();
			}
		}
		return c;
	}


	@Override
	public Widget createNumericWidget
		(Type type, Quantity quantity, Map params)
	{
		AWTWidgetSupport w = (AWTWidgetSupport) createStringWidget (params);
		((JTextField) w.getComponent ()).setHorizontalAlignment (JTextField.RIGHT);
		w.setConversion (new Numeric2String (type, quantity));
		if ((type instanceof BoundedType)
			|| Utils.getBoolean (params, "slider", false))
		{
			BoundedType b = (type instanceof BoundedType) ? (BoundedType) type
				: null;
			JSlider s = new JSlider ();
			AWTWidgetSupport ws = new SliderWidget ();
			if (Reflection.isIntegral (type))
			{
				if (b != null)
				{
					s.setMinimum (b.getMin ().intValue ());
					s.setMaximum (b.getMax ().intValue ());
				}
				else
				{
					s.setMinimum (Utils.getInt (params, "min", 0));
					s.setMaximum (Utils.getInt (params, "max", 100));
				}
			}
			else
			{
				s.setMinimum (0);
				s.setMaximum (1000);
				double min, max;
				if (b != null)
				{
					min = b.getMin ().doubleValue ();
					max = b.getMax ().doubleValue ();
				}
				else
				{
					min = Utils.getDouble (params, "min", 0);
					max = Utils.getDouble (params, "max", 1);
				}
				ws.setConversion (new LinearConversion (1000 / (max - min),
												-1000 * min / (max - min)));
			}
			ws.setComponent (s);
			Container p = (Container) createContainer (new float[] {0.4f, 0.6f}, 1);
			p.add ((Component) w.getComponent ());
			p.add (s);
			return new WidgetList (p, w, ws);
		}
		else
		{
			return w;
		}
	}


	@Override
	public Widget createStringWidget (Map params)
	{
		// set columns of f to small value
		// unequal to 0 to prevent expanding
		// textfield to right
		JTextField f = new JTextField ();
		AWTWidgetSupport w = new TextWidget ();
		w.setComponent (f);
		return w;
	}

	@Override
	public Widget createTextAreaWidget (Map params)
	{
		JTextArea f = new JTextArea ();
		AWTWidgetSupport w = new TextAreaWidget ();
		w.setComponent (f);
		return w;
	}
	
	@Override
	public Widget createColorWidget (Map params)
	{
		AWTWidgetSupport w = new ColorWidget ();
		w.setComponent (new ColorChooser ());
		return w;
	}


	@Override
	public Widget createTreeChoiceWidget (UITree tree)
	{
		return new TreeChoiceWidget (tree);
	}


	@Override
	public Widget createChoiceWidget (ListModel list, boolean forMenu)
	{
		class Model extends SyncMappedList implements ComboBoxModel
		{
			private Object selected;

			Model (ListModel src)
			{
				super (src, new AWTSynchronizer (null));
			}

			public Object getSelectedItem ()
			{
				return selected;
			}

			public void setSelectedItem (Object anItem)
			{
				selected = anItem;
				fireContentsChanged (-1, -1);
			}
		}

		AWTWidgetSupport w;
		Component c;
		if (forMenu)
		{
			JMenu m = new JMenu ("XXX");
			ButtonGroup g = new ButtonGroup ();
			for (int i = 0; i < list.getSize (); i++)
			{
				JRadioButtonMenuItem it = new JRadioButtonMenuItem (String.valueOf (list.getElementAt (i)));
				g.add (it);
				m.add (it);
			}
			w = new MenuChoiceWidget (g, new Model (list));
			c = m;
		}
		else
		{
			w = new ChoiceWidget ();
			c = new JComboBox (new Model (list));
		}
		w.setComponent (c);
		return w;
	}


	@Override
	public Widget createBooleanWidget (boolean forMenu, Map params)
	{
		AbstractButton c;
		if (forMenu)
		{
			c = new JCheckBoxMenuItem ();
		}
		else
		{
			c = new JCheckBox ();
			c.setOpaque (false);
		}
		AWTWidgetSupport w = new BooleanWidget ();
		w.setComponent (c);
		return w;
	}


	@Override
	public ChartPanel createChartPanel (Context ctx, Map params)
	{
		ChartSupport c = new ChartSupport ();
		c.initialize ((WindowSupport) ctx.getWindow (), params);
		return c;
	}

/*
	public static void dispose (Component component, JobManager t)
	{
		if (component.getParent () == null)
		{
			return;
		}
		if (component instanceof Disposable)
		{
			((Disposable) component).dispose (t);
		}
		else if (component instanceof Container)
		{
			disposeChildren ((Container) component, t);
		}
		component.getParent ().remove (component);
	}


	public static void disposeChildren (Container c, JobManager t)
	{
		for (int i = c.getComponentCount () - 1; i >= 0; i--)
		{
			dispose (c.getComponent (i), t);
		}
	}
*/

/*
	public void openRegistryWindow (final GUIEditor editor, Registry r,
									Item root)
	{
		ComponentModel cm = new ComponentModel ()
			{
				protected int getHiddenCount (Container c)
				{
					return (c instanceof SubtreePanel) ? 1 : 0;
				}
			};
		DisposableTreeUpdater u = new DisposableTreeUpdater (r, root, cm)
			{
				protected boolean filter (Object node)
				{
					return ((Item) node).isVisible (-1);
				}


				protected Object createNode (Object sourceNode,
											 Object targetParent)
				{
					Component t = null;
					if (targetParent == null)
					{
						t = new TreePanel (SubtreePanel.INDENTATION,
										   false, this);
						t.setBackground (Color.white);
					}
					else
					{
						t = new ItemPanel (editor, (Item) sourceNode);
						if (((Item) sourceNode).isDirectory ())
						{
							Container c = new SubtreePanel
								((Container) targetParent, false);
							c.add (t);
							t = c;
						}
					}
					t.setName (((Item) sourceNode).getKey ());
					return t;
				}


				protected void update (Object sourceNode, Object targetNode,
									   javax.swing.event.TreeModelEvent event)
				{
					((Component) targetNode).repaint ();
				}


				protected void targetUpdated ()
				{
					ComponentModel.revalidate (((ComponentModel) target).root);
				}


				public boolean isImage (Object sourceNode, Object targetNode)
				{
					return ((Item) sourceNode).getKey ()
						.equals (((Component) targetNode).getName ());
				}
			};
		r.addTreeModelListener (u);
		cm.root = (Container) u.createTree ();
		openEditorDialog (editor, "registry", false, cm.root);
	}
*/

	@Override
	public Window createWindow (Command close, Map params)
	{
		JPopupMenu.setDefaultLightWeightPopupEnabled (false);
		return new WindowSupport (this, close, params);
	}


	@Override
	public Panel createPanel (Context ctx, Disposable toDispose, Map params)
	{
		SwingDockable d = Boolean.TRUE.equals (params.get ("plain", Boolean.FALSE))
			? new PlainPanel (new GridLayout (1, 1), toDispose)
			: (SwingDockable) new SwingPanel (toDispose);
		return new PanelSupport (d)
			.initialize ((WindowSupport) ctx.getWindow (), params);
	}


	@Override
	public Console createConsole (Context ctx, Map params)
	{
		return (JConsole) new JConsole ().initialize ((WindowSupport) ctx.getWindow (), params);
	}


	@Override
	public TextEditor createTextEditor (Context ctx, Map params)
	{
		Item dir = config.getItem ("texteditors");
		PanelSupport te = null;
		Map opt = UI.getOptions (Workbench.current());
		boolean useIntegratedTextEditor = Utils.getBoolean(opt, "useIntegratedTextEditor", false);
		if (!useIntegratedTextEditor) {
			if (dir != null)
			{
				Item last = null;
				for (Item i = (Item) dir.getBranch (); i != null;
					 i = (Item) i.getSuccessor ())
				{
					last = i;
				}
				if (last instanceof Expression)
				{
					te = (PanelSupport) ((Expression) last)
						.evaluate (ctx.getWorkbench (),
								   UI.getArgs (ctx, params));
				}
			}
		}
		if (te == null)
		{
			te = new TextEditorSupport ();
		}
		te.initialize ((WindowSupport) ctx.getWindow (), params);
		return (TextEditor) te;
	}


	@Override
	protected Container createContainer (LayoutManager layout)
	{
		return new JPanel (layout);
	}

	
	@Override
	public Object createSplitContainer (int orientation)
	{
		JSplitPane sp = new JSplitPane (orientation);
		sp.setContinuousLayout (false);
//		sp.setResizeWeight (0.5);
		sp.setDividerLocation(-1);
		return sp;
	}


	@Override
	public Object setBorder (Object component, int gap)
	{
		JComponent c;
		if (component instanceof JComponent)
		{
			c = (JComponent) component;
		}
		else
		{
			c = new JPanel (new GridLayout (1, 1, 0, 0));
			c.add ((Component) component);
		}
		c.setBorder (new EmptyBorder (gap, gap, gap, gap));
		return c;
	}


	@Override
	public ComponentWrapper createTree (UITree tree)
	{
		return new SwingTree (tree);
	}

	@Override
	public ComponentWrapper createTreeInSplit(UITree tree, Object split) {
		SwingTree swTree = (SwingTree) createTree (tree);
		final JSplitPane splitPane = (JSplitPane) split;
		swTree.addComponentListener(new ComponentListener() {
			int setCount = 0;
			public void componentHidden(ComponentEvent e) {
			}
			public void componentMoved(ComponentEvent e) {
			}
			public void componentResized(ComponentEvent e) {
				JTree swTree = (JTree) e.getSource();
				// only at the 4th step position change has effect
				if (setCount < 4) {
					swTree.doLayout();
					Dimension treeDim = swTree.getPreferredSize();
					Dimension splitDim = splitPane.getSize();
					int pos = treeDim.width + 50;
					if (splitDim.width / 2 < pos)
						pos = splitDim.width / 2;
					splitPane.setDividerLocation(pos);
					setCount++;
				}
			}
			public void componentShown(ComponentEvent e) {
			}			
		});
		return swTree;
	}
	
	@Override
	public ComponentWrapper createTable (TableModel table, Context ctx)
	{
		SwingTable tab = new SwingTable (table, ctx);
		return new ComponentWrapperImpl (createScrollPane (tab), tab);
	}

	@Override
	public TableModel getTable (ComponentWrapper table)
	{
		if (!(table instanceof ComponentWrapperImpl))
		{
			return null;
		}
		Disposable t = ((ComponentWrapperImpl) table).toDispose;
		if (!(t instanceof SwingTable))
		{
			return null;
		}
		return ((SwingTable) t).srcTable;
	}

	@Override
	public int getSelectedRow (ComponentWrapper table)
	{
		return ((SwingTable) ((ComponentWrapperImpl) table).toDispose).getSelectedRow ();
	}

	
	private class ViewerHistory extends ObjectList
		implements HyperlinkListener, ActionListener, PropertyChangeListener
	{
		static final String KEY = "de.grogra.pf.ui.swing.VIEWER_HISTORY";

		JButton back = (JButton) createButton
			(UI.I18N, "browser.back", MEDIUM_ICON_SIZE, 0, null, null);
		JButton forward = (JButton) createButton
			(UI.I18N, "browser.forward", MEDIUM_ICON_SIZE, 0, null, null);
		JLabel title = new JLabel ();
		JEditorPane editor;

		int currentIndex = -1;
		
		
		ViewerHistory (JEditorPane editor)
		{
			this.editor = editor;
			editor.putClientProperty (KEY, this);
			title.setFont (getFont (FONT_DIALOG | FONT_BOLD | 18));
			title.setBorder (new EmptyBorder (2, 4, 2, 4));
		}

		public void hyperlinkUpdate (HyperlinkEvent e)
		{
			if (e.getEventType () == HyperlinkEvent.EventType.ACTIVATED)
			{
				if (e instanceof HTMLFrameHyperlinkEvent)
				{
					((HTMLDocument) editor.getDocument ())
						.processHTMLFrameHyperlinkEvent
						((HTMLFrameHyperlinkEvent) e);
				}
				else if (e.getURL () != null)
				{
					setPage (e.getURL ());
				}
				else
				{
					editor.setCursor (null);
					UI.executeHyperlinkURL
						(e.getDescription (), PanelSupport.get (editor));
				}
			}
		}

		public void actionPerformed (ActionEvent e)
		{
			try
			{
				if ((e.getSource () == back) && (currentIndex > 0))
				{
					setEditorPage (editor, (URL) get (--currentIndex));
				}
				else if ((e.getSource () == forward) && (currentIndex + 1 < size))
				{
					setEditorPage (editor, (URL) get (++currentIndex));
				}
			}
			catch (java.io.IOException io)
			{
				io.printStackTrace ();
			}
		}

		public void propertyChange (PropertyChangeEvent e)
		{
			if ("page".equals (e.getPropertyName ()))
			{
				update ();
			}
			else if ("editorKit".equals (e.getPropertyName ()))
			{
				checkEditorKit (editor);
			}
		}

		void update ()
		{
			back.setEnabled (currentIndex > 0);
			forward.setEnabled (currentIndex + 1 < size);
			Object t = editor.getDocument ().getProperty (Document.TitleProperty);
			if (t == null)
			{
				t = editor.getPage ();
			}
			title.setText ((t != null) ? t.toString () : null);
		}
		
		void setPage (URL url)
		{
			try
			{
				setSize (++currentIndex);
				add (url);
				setEditorPage (editor, url);
			}
			catch (java.io.IOException io)
			{
				io.printStackTrace ();
			}
		}
	}


	@Override
	public Object createTextViewer (URL url, String mimeType, String content,
									final Command hyperlink, boolean asBrowser)
	{
		final JEditorPane p;
		try
		{
			if ((mimeType != null) && (content != null))
			{
				p = new JEditorPane (mimeType, content);
				if (url != null)
				{
					p.getDocument ().putProperty
						(Document.StreamDescriptionProperty, url);
				}
			}
			else
			{
				p = new JEditorPane ();
				if (url != null)
				{
					setEditorPage (p, url);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace ();
			return null;
		}
		p.setEditable (false);
		if (hyperlink != null)
		{
			p.addHyperlinkListener (new HyperlinkListener ()
			{
				public void hyperlinkUpdate (HyperlinkEvent e)
				{
					if ((e.getEventType () == HyperlinkEvent.EventType.ACTIVATED)
						&& !(e instanceof HTMLFrameHyperlinkEvent))
					{
						PanelSupport s = PanelSupport.get (p);
						s.getWorkbench ().getJobManager ().runLater
							(hyperlink, (e.getURL () != null)
							 ? e.getURL ().toString ()
							 : e.getDescription (), s,
							 JobManager.UPDATE_FLAGS);
					}
				}
			});
		}
		if (!asBrowser)
		{
			return p;
		}

		ViewerHistory h = new ViewerHistory (p);
		Container comp = (Container) createScrollPane (p);

		GridBagConstraints gc = new GridBagConstraints ();
		JComponent root = new JPanel (new GridBagLayout ());
		gc.gridy = 0;
		root.add (h.back, gc);
		root.add (h.forward, gc);
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.weightx = 1;
		root.add (h.title, gc);
		gc.gridy = 1;
		gc.weighty = 1;
		gc.weightx = 0;
		gc.gridheight = GridBagConstraints.REMAINDER;
		root.add (comp, gc);
		if ((url != null) && (p.getDocument () instanceof HTMLDocument))
		{
			((HTMLDocument) p.getDocument ()).setBase (url);
		}

		if (url != null)
		{
			h.currentIndex = 0;
			h.add (url);
		}
		checkEditorKit (p);
		p.addHyperlinkListener (h);
		p.addPropertyChangeListener (h);
		h.back.addActionListener (h);
		h.forward.addActionListener (h);
		h.update ();
		root.putClientProperty (EDITOR_PANE, p);
		return root;
	}


	@Override
	public void setContent (final Object textViewer,
							final String mimeType, final String content)
	{
		AWTSynchronizer.invokeInEventQueue (new Runnable ()
		{
			private boolean scroll = false;

			public void run ()
			{
				JEditorPane p = getEditorPane ((JComponent) textViewer);
				if (scroll)
				{
				    p.scrollRectToVisible (new Rectangle (0, 0, 1, 1));
				}
				else
				{
					p.setContentType (mimeType);
					p.setDocument (p.getEditorKit ().createDefaultDocument ());
					p.setText (content);
					scroll = true;
					EventQueue.invokeLater (this);
				}
			}
		});
	}


	@Override
	public void setContent (final Object textViewer, final URL content)
	{
		AWTSynchronizer.invokeInEventQueue (new Runnable ()
		{
			public void run ()
			{
				try
				{
					JEditorPane p = getEditorPane ((JComponent) textViewer);
					ViewerHistory h = (ViewerHistory)
						p.getClientProperty (ViewerHistory.KEY);
					if (h != null)
					{
						h.setPage (content);
					}
					else
					{
						setEditorPage (p, content);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}
		});
	}


	@Override
	public Panel createViewerPanel (Context ctx, URL url, Map params)
	{
		JComponent comp = (JComponent) createTextViewer (url, null, null, null, true);
		if (comp == null)
		{
			return null;
		}
		params = UI.configureViewerParams
			(params, url, IO.getMimeType (url.getFile ()).getMediaType (),
			 ctx.getWorkbench ());
		final PanelSupport p = (PanelSupport) createPanel (ctx, null, params);
		p.textViewer = comp;
		p.setContent (new ComponentWrapperImpl (comp, null));
		final JEditorPane ep = getEditorPane (comp);
		p.mapProducer = new ModifiableMap.Producer ()
		{
			public void addMappings (ModifiableMap out)
			{
				out.put ("mimeType", ep.getContentType ());
				URL u = ep.getPage ();
				if (u != null)
				{
					out.put ("systemId", IO.toSystemId
							 (UI.getRegistry (p).getFileSystem (), u));
				}
			}
		};
		return p;
	}


	@Override
	public Object getTextViewerComponent (Panel viewerPanel)
	{
		return ((PanelSupport) viewerPanel.resolve ()).textViewer;
	}


	private static class TreeRoot extends JComponent
	{
		AWTTree tree;


		TreeRoot ()
		{
			setOpaque (false);
			enableEvents (AWTEvent.MOUSE_EVENT_MASK
						  | AWTEvent.MOUSE_MOTION_EVENT_MASK
						  | AWTEvent.KEY_EVENT_MASK);
			updateUI ();
		}


		@Override
		public void updateUI ()
		{
			LookAndFeel.installColorsAndFont (this, "Label.background",
											  "Label.foreground",
											  "Label.font");
		}


		@Override
		protected void paintComponent (Graphics g)
		{
			if (tree != null)
			{
				tree.paint (g);
			}
		}
	}


	@Override
	public ComponentWrapper createComponentTree (UITree componentTree)
	{
		TreeRoot r = new TreeRoot ();
		AWTTree t = new AWTTree (componentTree, componentTree, r);
		t.map (true);
		r.tree = t;
		return t;
	}


	@Override
	public ComponentWrapper createComponentMenu (UITree componentTree)
	{
		ComponentMenu t = new ComponentMenu (componentTree, componentTree);
		t.map (true);
		return t;
	}


	@Override
	public Panel createToolBar (Context ctx, Map params)
	{
		return new PanelSupport (new ToolBar ())
			.initialize ((WindowSupport) ctx.getWindow (), params);
	}


	@Override
	public Panel createStatusBar (Context ctx, Map params)
	{
		return new PanelSupport (new StatusBar ())
			.initialize ((WindowSupport) ctx.getWindow (), params);
	}


	private static final int[] ALIGNMENT
		= {ALIGNMENT_CENTER, ALIGNMENT_LEADING, ALIGNMENT_TRAILING};

	private static final int[] J_ALIGNMENT
		= {SwingConstants.CENTER, SwingConstants.LEADING,
		   SwingConstants.TRAILING};


	@Override
	protected Component createLabel (String text, javax.swing.Icon icon, int flags)
	{
		if ((text != null) && (text.indexOf ('\n') >= 0) && (text.indexOf ("<html>") < 0))
		{
			text = "<html><pre>" + text + "</pre></html>";
		}
		JLabel l = new JLabel
			(text, icon, getFirstMatching (flags, ALIGNMENT, J_ALIGNMENT,
										   SwingConstants.LEADING));
		l.setVerticalAlignment (SwingConstants.TOP);
		return l;
	}


	@Override
	protected void updateLabel (Component label, String text, javax.swing.Icon icon)
	{
		JLabel l = (JLabel) label;
		if (!l.getText ().equals (text))
		{
			((JLabel) label).setText (text);
		}
		if (l.getIcon () != icon)
		{
			l.setIcon (icon);
		}
	}


	@Override
	public Object createButton (String text, IconSource icon, Dimension size,
								int flags, final Command cmd, final Context ctx)
	{
		javax.swing.Icon i = IconAdapter.create (icon, size);
		AbstractButton b = ((flags & FOR_MENU) != 0) ? (AbstractButton) new JMenuItem (text, i) : new JButton (text, i);
		b.setHorizontalAlignment
			(getFirstMatching (flags, ALIGNMENT, J_ALIGNMENT,
							   SwingConstants.LEADING));
		b.setVerticalAlignment (SwingConstants.TOP);
		Font f = getFont (flags);
		if (f != null)
		{
			b.setFont (f);
		}
		if (cmd != null)
		{
			b.addActionListener (new ActionListener ()
			{
				public void actionPerformed (ActionEvent e)
				{
					UI.getJobManager (ctx).runLater
						(cmd, e, ctx, JobManager.ACTION_FLAGS);
				}
			});
		}
		return b;
	}


/*
	public URLPair chooseURL (Window frame, String title,
							  String container)
	{
		URLChooser dialog = new URLChooser 
			(getFrame (((GUIEditor) frame.getEditor ()).getFrame ()), title,
			 container);
		dialog.setVisible (true);
		return new URLPair (dialog.getURL (), dialog.getContainer ());
		return null;
	}
*/

	@Override
	public void showPopupMenu (UITree menu, Object component,
							   final int x, final int y)
	{
		showPopupMenu (menu, component, x, y, null);
	}


	void showPopupMenu (UITree menu, Object component,
						final int x, final int y, ActionListener listener)
	{
		if (menu == null)
		{
			return;
		}
		final Component c = (Component)
			((component == null) ? menu.getContext ().getComponent ()
			 : component);
		final MenuModel popup = new MenuModel (menu, listener, true);
		popup.map (false);
		final JPopupMenu m = (JPopupMenu) popup.getRoot ();
		m.addPopupMenuListener (new PopupMenuListener ()
			{
				boolean disposed = false;


				public void popupMenuCanceled (PopupMenuEvent e)
				{
					if (disposed)
					{
						return;
					}
					disposed = true;
					Context ctx = popup.getSourceTree ().getContext ();
					UI.getJobManager (ctx).runLater
						(Command.DISPOSE, popup, ctx, JobManager.UPDATE_FLAGS);
				}


				public void popupMenuWillBecomeVisible (PopupMenuEvent e)
				{
				}


				public void popupMenuWillBecomeInvisible (PopupMenuEvent e)
				{
					popupMenuCanceled (e);
				}
			});
		EventQueue.invokeLater (new Runnable ()
			{
				public void run ()
				{
					m.show (c, x, y);
				}
			});
	}


	public void windowClosing (WindowEvent e)
	{
		java.awt.Window w = e.getWindow ();
		if (w instanceof RootPaneContainer)
		{
			JRootPane rp = ((RootPaneContainer) w).getRootPane ();
			if (rp instanceof ISwingPanel)
			{
				PanelSupport p = ((ISwingPanel) rp).getSupport ();
				if (p instanceof WindowSupport)
				{
					((WindowSupport) p).closeRequested ();
				}
			}
		}
	}


	@Override
	public Object createScrollPane (Object view)
	{
		return new JScrollPane ((java.awt.Component) view);
	}

	@Override
	public Object createTabbedPane (String[] titles, Object[] components)
	{
		JTabbedPane t = new JTabbedPane (JTabbedPane.TOP);
		for (int i = 0; i < titles.length; i++)
		{
			t.addTab (titles[i], (Component) components[i]);
		}
		return t;
	}


/*
	public static void resetProjection (SubWindow w)
	{
		((WireframeCanvas) ((de.grogra.pf.ui.swing.SwingView3D) w).canvas)
			.resetProjection ();
	}
*/
}
