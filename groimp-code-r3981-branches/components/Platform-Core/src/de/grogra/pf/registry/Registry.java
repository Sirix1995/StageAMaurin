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

package de.grogra.pf.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.ComponentNode;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.GraphTransaction;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.SharedObjectNode;
import de.grogra.persistence.FatalPersistenceException;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceBindings;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.PersistenceConnection;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.PersistenceInput;
import de.grogra.persistence.PersistenceOutput;
import de.grogra.persistence.ResolvableReference;
import de.grogra.persistence.SOReferenceImpl;
import de.grogra.persistence.ServerConnection;
import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.Transaction;
import de.grogra.persistence.XAListener;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileTypeItem;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.SAXSource;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeLoader;
import de.grogra.util.Int2ObjectMap;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.util.TreeDiff;
import de.grogra.util.TreeModelEventMulticaster;
import de.grogra.util.Utils;
import de.grogra.vfs.FSFile;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.JoinedFileSystem;
import de.grogra.xl.util.ObjectList;

/**
 * 
 * The registry consists of a tree of items. Each item has a name and can be referenced
 * like in a file system. The path for selecting an item is similar to a unix path
 * (i. e. /ui/commands).
 * 
 * 
 * @author nmi
 *
 */
public final class Registry implements Transaction.Consumer,
		GraphTransaction.Consumer, XAListener, SharedObjectProvider,
		SharedObjectProvider.Binding, TreeModel, RegistryContext, //Disposable,
		TreeDiff.NodeModel, TypeLoader
{
	public static final String NAMESPACE = "http://grogra.de/registry";
	public static final MimeType MIME_TYPE = new MimeType (
		"application/x-grogra-registry+xml");
	
	public static final String PROJECT_GRAPH = "Project";
	public static final String REGISTRY_GRAPH = "Registry";
	public static final String COMPONENT_GRAPH = "Component";

	private static int nextPropertyId = 0;
	
	private int mainRGGID = -1;

	public static synchronized int allocatePropertyId ()
	{
		return nextPropertyId++;
	}

	int stamp = 1;

	private int activationStamp = 0;

	final StringMap importAttributes = new StringMap ();
	private final StringMap itemMap = new StringMap ();
	private static final HashMap<String, ComponentDescriptor> componentMap = new HashMap<String, ComponentDescriptor>();
	private final Registry parentRegistry, rootRegistry;
	private final TreeModelEventMulticaster treeMulticaster = new TreeModelEventMulticaster ();
	private final Int2ObjectMap userProperties = new Int2ObjectMap ();
	private final ObjectList afterCommit = new ObjectList (10);

	private GraphManager regGraph;
	private GraphManager project;
	private GraphManager componentGraph;
	private Transaction.Reader reader;
	private Transaction.Reader componentReader;
	private Item root;

	private String projectName = null;
	private final Object lock, writeLock;
	private boolean active = false;
	private final Hashtable pluginTypes;

	private FileSystem fileSystem;
	private String fsName;
	private boolean fsAdded;
	private final ObjectList fileSystemListeners = new ObjectList (5, false);

	private final int id;

	private final Logger logger;

	public static final JoinedFileSystem PLUGIN_FILE_SYSTEMS = new JoinedFileSystem (
		"pluginfs", "pluginfs");

	public static final JoinedFileSystem ALL_FILE_SYSTEMS = new JoinedFileSystem (
		"project", "project");

	private static int nextId = -1;

	private final SortedMap<String, Set<String>> providedOutputSlots = new TreeMap<String, Set<String>>();
	private final SortedMap<String, Set<String>> requiredInputSlots = new TreeMap<String, Set<String>>();

	private static synchronized int nextId ()
	{
		return nextId++;
	}

	private final TreeModel oldTree = new TreeModel ()
	{
		@Override
		public Object getRoot ()
		{
			return Registry.this.getRoot ();
		}

		@Override
		public Object getChild (Object parent, int index)
		{
			return ((Item) parent).oldChildren.get (index);
		}

		@Override
		public int getChildCount (Object parent)
		{
			return ((Item) parent).oldChildren.size ();
		}

		@Override
		public boolean isLeaf (Object node)
		{
			return Registry.this.isLeaf (node);
		}

		@Override
		public int getIndexOfChild (Object parent, Object child)
		{
			return ((Item) parent).oldChildren.indexOf (child);
		}

		@Override
		public void valueForPathChanged (TreePath path, Object value)
		{
			throw new AssertionError ();
		}

		@Override
		public void addTreeModelListener (TreeModelListener e)
		{
			throw new AssertionError ();
		}

		@Override
		public void removeTreeModelListener (TreeModelListener e)
		{
			throw new AssertionError ();
		}
	};

	@Override
	public void addTreeModelListener (TreeModelListener l)
	{
		treeMulticaster.addTreeModelListener (l);
	}

	@Override
	public void removeTreeModelListener (TreeModelListener l)
	{
		treeMulticaster.removeTreeModelListener (l);
	}

	@Override
	public void valueForPathChanged (TreePath path, Object newValue)
	{
		throw new UnsupportedOperationException ();
	}

	@Override
	public Object getChild (Object parent, int index)
	{
		return ((Item) parent).getBranchNode (index);
	}

	@Override
	public int getChildCount (Object parent)
	{
		return ((Item) parent).getBranchLength ();
	}

	@Override
	public int getIndexOfChild (Object parent, Object child)
	{
		return ((Item) child).getIndex ();
	}

	@Override
	public boolean isLeaf (Object node)
	{
		return !((Item) node).isDirectory ();
	}

	@Override
	public Object getRoot ()
	{
		return root;
	}


	public static Registry create (Registry parentRegistry)
	{
		Registry reg = new Registry (parentRegistry);

		ServerConnection conn = new ServerConnection (
			new PersistenceBindings (reg, reg));

		reg.createGraphs (conn);

		Item root = new Root ();
		reg.regGraph.makePersistent (root, -1L, null);
		reg.regGraph.setRoot (GraphManager.MAIN_GRAPH, root);

		Item root2 = new Root ();
		reg.componentGraph.makePersistent (root2, -1L, null);
		reg.componentGraph.setRoot (GraphManager.COMPONENT_GRAPH, root2);
		
		reg.initialize ();
		return reg;
	}

	
	public void createGraphs (PersistenceConnection conn)
	{
		if (parentRegistry != null)
		{
			project = new GraphManager (conn, PROJECT_GRAPH, true, true);
		}
		else
		{
			project = null;
		}
		regGraph = new GraphManager (conn, REGISTRY_GRAPH, true, false);
		componentGraph = new GraphManager (conn, COMPONENT_GRAPH, true, false);
	}

	public Registry (Registry parentRegistry)
	{
		super ();
		id = nextId ();
		this.parentRegistry = parentRegistry;
		if (parentRegistry != null)
		{
			rootRegistry = parentRegistry.rootRegistry;
			lock = parentRegistry.lock;
			writeLock = parentRegistry.writeLock;
			pluginTypes = parentRegistry.pluginTypes;
			logger = Logger.getLogger ("de.grogra.pf.registry." + id);
		}
		else
		{
			rootRegistry = this;
			lock = new Object ();
			writeLock = new Object ();
			pluginTypes = new Hashtable ();
			logger = Logger.getLogger ("de.grogra.pf.registry");
		}
		logger.setLevel (null);
	}

	public void initialize ()
	{
		this.root = (Item) regGraph.getRoot (Graph.MAIN_GRAPH);
		this.root.initPluginDescriptor (null);
		this.root.setRegistry (this);
		reader = new GraphTransaction (regGraph, null).createReader ();
		regGraph.addXAListener (this);

		Item root = (Item) componentGraph.getRoot (Graph.COMPONENT_GRAPH);
		root.initPluginDescriptor (null);
		root.setRegistry (this);
		componentReader = new GraphTransaction (componentGraph, null).createReader ();
		componentGraph.addXAListener (this);
	}

	@Override
	public Registry getRegistry ()
	{
		return this;
	}

	public Logger getLogger ()
	{
		return logger;
	}

	public void addFileSystemListener (TreeModelListener l)
	{
		l.getClass ();
		fileSystemListeners.addIfNotContained (l);
		if (fileSystem != null)
		{
			fileSystem.addTreeModelListener (l);
		}
	}

	public void removeFileSystemListener (TreeModelListener l)
	{
		fileSystemListeners.remove (l);
		if (fileSystem != null)
		{
			fileSystem.removeTreeModelListener (l);
		}
	}

	public void initFileSystem (FileSystem fileSystem)
	{
		if (this.fileSystem != null)
		{
			throw new IllegalStateException ();
		}
		setFileSystem (fileSystem);
	}

	private void setFileSystem (FileSystem fileSystem)
	{
		fsAdded = false;
		if (this.fileSystem != null)
		{
			ALL_FILE_SYSTEMS.removeFileSystem (this.fileSystem);
			for (int i = fileSystemListeners.size () - 1; i >= 0; i--)
			{
				this.fileSystem
					.removeTreeModelListener ((TreeModelListener) fileSystemListeners
						.get (i));
			}
			this.fileSystem.setFileNameMap (null);
		}
		this.fileSystem = fileSystem;
		if (fileSystem != null)
		{
			for (int i = fileSystemListeners.size () - 1; i >= 0; i--)
			{
				fileSystem
					.addTreeModelListener ((TreeModelListener) fileSystemListeners
						.get (i));
			}
			fileSystem
				.setFileNameMap (new FileTypeItem.Map (getRootRegistry ()));
			addFileSystem ();
		}
	}

	private void addFileSystem ()
	{
		if (!fsAdded && (fileSystem != null)
			&& ((fsName != null) || (projectName != null)))
		{
			fsAdded = true;
			ALL_FILE_SYSTEMS.addFileSystem (fileSystem, getFileSystemName (), fileSystem.getRoot ());
		}
	}

	public void substituteFileSystem (FileSystem newfs) throws IOException
	{
		fileSystem.copyFilesTo (getFiles (), newfs);
		newfs.setManifest (fileSystem.getManifest ());
		setFileSystem (newfs);
	}

	private static final ItemVisitor GET_FILES = new ItemVisitor ()
	{
		@Override
		public void visit (Item item, Object info)
		{
			item.addRequiredFiles ((Collection) info);
		}
	};

	public Collection getFiles ()
	{
		Collection list = new java.util.HashSet ();
		forAll (null, null, GET_FILES, list, false);
		return list;
	}

	public FileSystem getFileSystem ()
	{
		return fileSystem;
	}

	public final Object getProjectFile (String systemId)
	{
		FSFile f = IO.toFile (this, systemId);
		return (f.fileSystem == fileSystem) ? f.file : null;
	}

	public final synchronized String getFileSystemName ()
	{
		if (fsName == null)
		{
			fsName = (projectName != null) ? projectName + '[' + id + ']'
					: Integer.toString (id);
		}
		return fsName;
	}

	public void setProjectName (String projectName)
	{
		this.projectName = projectName;
		Utils.setDisplayLoggerName (logger.getName (), projectName);
		addFileSystem ();
	}

	public String getProjectName ()
	{
		return projectName;
	}

	public Object getImportAttribute (String key)
	{
		return importAttributes.get (key);
	}

	public Item findFirst (ItemCriterion c, Object info, boolean resolve)
	{
		String r = c.getRootDirectory ();
		return (r == null) ? root.findFirst (c, info, resolve) : Item
			.findFirst (getItem (r), c, info, resolve);
	}

	public void forAll (ItemCriterion c, Object info, ItemVisitor cb,
			Object cbInfo, boolean resolve)
	{
		String r;
		if ((c == null) || ((r = c.getRootDirectory ()) == null))
		{
			root.forAll (c, info, cb, cbInfo, resolve);
		}
		else
		{
			Item.forAll (getItem (r), c, info, cb, cbInfo, resolve);
		}
	}

	public Item[] findAll (ItemCriterion c, Object info, boolean resolve)
	{
		String r = c.getRootDirectory ();
		return (r == null) ? root.findAll (c, info, resolve) : Item.findAll (
			getItem (r), c, info, resolve);
	}

	public Item findMax (ItemComparator c, Object info, boolean resolve)
	{
		return root.findMax (c, info, resolve);
	}

	public void setEmptyGraph ()
	{
		Node n = new Node ();
		n.setExtentIndex (Node.LAST_EXTENT_INDEX);	
		project.setRoot (GraphManager.MAIN_GRAPH, n);
		n = new Node ();
		n.setExtentIndex (Node.LAST_EXTENT_INDEX);
		project.setRoot (GraphManager.META_GRAPH, n);
		n = new ComponentNode ();
		n.setExtentIndex (Node.LAST_EXTENT_INDEX);
		project.setRoot (GraphManager.COMPONENT_GRAPH, n);
	}

	public GraphManager getProjectGraph ()
	{
		return project;
	}

	public GraphManager getRegistryGraph ()
	{
		return regGraph;
	}

	public GraphManager getComponentGraph ()
	{
		return componentGraph;
	}
	
	
	public Registry getParentRegistry ()
	{
		return parentRegistry;
	}

	public Registry getRootRegistry ()
	{
		return rootRegistry;
	}

	public final boolean isRootRegistry ()
	{
		return this == rootRegistry;
	}

	public Object getLock ()
	{
		return lock;
	}

	public Object getWriteLock ()
	{
		return writeLock;
	}

	private transient GraphTransaction currentXA = null;
	private transient int xaCount = 0;

	public final void beginXA ()
	{
		assert Thread.holdsLock (writeLock);
		if (xaCount++ > 0)
		{
			return;
		}
		GraphManager g = isActive () ? regGraph : null;
		GraphTransaction xa = (g == null) ? null : (GraphTransaction) g.getTransaction (true);
		if (xa != null)
		{
			xa.begin (false);
			currentXA = xa;
		}
	}

	public final Transaction getTransaction ()
	{
		return currentXA;
	}

	public final void invokeAfterCommit (Runnable r)
	{
		if (xaCount <= 0)
		{
			throw new IllegalStateException ();
		}
		afterCommit.add (0, r);
	}

	public final void commitXA (boolean activateItems)
	{
		assert Thread.holdsLock (writeLock);
		if (activateItems && (xaCount == 1) && (currentXA != null))
		{
			activateItems ();
		}
		if ((--xaCount == 0) && (currentXA != null))
		{
			currentXA.commit ();
			currentXA = null;
			runAfterCommit ();
		}
	}

	private void runAfterCommit ()
	{
		while (!afterCommit.isEmpty ())
		{
			((Runnable) afterCommit.pop ()).run ();
		}
	}

	/*
	 public void add (String key, Item item)
	 {
	 items.add (key, item);
	 }


	 public ItemNode getNode (String key)
	 {
	 ItemNode d = (ItemNode) root;
	 int pos = 0;
	 while (true)
	 {
	 if (!key.regionMatches (pos, d.key, 0, d.key.length ()))
	 {
	 return null;
	 }
	 pos += d.key.length ();
	 if (pos == key.length ())
	 {
	 return d;
	 }
	 ItemNode i;
	 for (i = (ItemNode) d.children; i != null; i = (ItemNode) i.next)
	 {
	 if (key.regionMatches (pos, i.key, 0, i.key.length ())
	 && ((pos + i.key.length () == key.length ())
	 || i.item.isDirectory ()))
	 {
	 d = i;
	 break;
	 }
	 }
	 if (i == null)
	 {
	 return null;
	 }
	 }
	 }
	 */

	public Item getItem (String key)
	{
		synchronized (lock)
		{
			Item i = (Item) itemMap.get (key);
			if (i == null)
			{
				i = getItem (key, null, false);
				if (i != null)
				{
					itemMap.put (key, i);
				}
			}
			return i;
		}
	}
	
	/**
	 * Return the item specified by its absolute <code>path</code>
	 * or create such an item if does not yet exist.
	 * <code>plugin</code> is stored as the plugin that was responsible for
	 * creating the item (only if an item was created).
	 * <code>plugin</code> may be null to signal
	 * that there is no responsible instance.
	 * 
	 * @param path absolute path
	 * @param plugin descriptor of responsible plugin
	 * @return item specified by path
	 */
	public Item getDirectory (String path, Object plugin)
	{
		Item dir;
		synchronized (lock)
		{
			dir = getItem (path);
		}
		if (dir != null)
		{
			return dir;
		}
		synchronized (writeLock)
		{
			dir = getItem (path);
			if (dir != null)
			{
				return dir;
			}
			beginXA ();
			try
			{
				synchronized (lock)
				{
					dir = getItem (path, plugin, true);
				}
			}
			finally
			{
				commitXA (false);
			}
			return dir;
		}
	}

	/**
	 * Adds a {@link SharedObjectNode} for <code>object</code> to the meta-graph
	 * (see {@link GraphManager#META_GRAPH}) of the
	 * project graph (see {@link #getProjectGraph()}), and creates
	 * a reference to this node in the specified <code>directory</code>
	 * of this registry. The name is chosen based on <code>name</code>
	 * in the same way as in
	 * {@link Item#addUserItemWithUniqueName(Item, String)}.
	 * 
	 * @param directory absolute path where to insert reference item 
	 * @param object object for which references shall be created
	 * @param name base name to use
	 * @param objDescribes <code>true</code> iff <code>object</code>
	 * shall be queried for descriptions (e.g., for the icon) 
	 * @return reference item to <code>object</code> in registry
	 */
	public SONodeReference addSharedObject (String directory, Shareable object,
			String name, boolean objDescribes)
	{
		SharedObjectNode n = new SharedObjectNode (object);
		n.setExtentIndex (Node.LAST_EXTENT_INDEX);
		project.addMetaNode (n, isActive () ? project.getActiveTransaction () : null);
		SONodeReference ref = new SONodeReference (n);
		ref.setObjDescribes (objDescribes);
		getDirectory (directory, null).addUserItemWithUniqueName (ref, name);
		return ref;
	}

	public final Item getPluginDirectory ()
	{
		return rootRegistry.root.getItem ("plugins");
	}

	public PluginDescriptor getPluginDescriptor (String pluginId)
	{
		return (PluginDescriptor) getPluginDirectory ().getItem (pluginId);
	}	
	
	private Item getItem (String key, Object forDirs,
			boolean createDirs)
	{
		if (key.equals ("/"))
		{
			return root;
		}
		if (!key.startsWith ("/"))
		{
			throw new IllegalArgumentException (key
				+ " does not start with '/'");
		}
		Item d = root;
		int pos = 0;
		search: while (true)
		{
			String dn = d.getName ();
			for (int i = 0; i < dn.length (); i++)
			{
				char c = key.charAt (pos++);
				if (c == '\\')
				{
					c = key.charAt (pos++);
				}
				if (c != dn.charAt (i))
				{
					throw new AssertionError ();
				}
			}
			if (pos == key.length ())
			{
				return d;
			}
			if (key.charAt (pos++) != '/')
			{
				throw new AssertionError ();
			}
			if (pos == key.length ())
			{
				throw new IllegalArgumentException (key + " ends with '/'");
			}
			Item i = d.getItem (key, pos, true);
			if (i != null)
			{
				d = i;
				continue search;
			}
			if (!createDirs)
			{
				return null;
			}
			StringBuffer b = new StringBuffer (key.length () - pos);
			char c;
			int n = pos;
			while ((n < key.length ()) && ((c = key.charAt (n++)) != '/'))
			{
				if (c == '\\')
				{
					if (n == key.length ())
					{
						throw new IllegalArgumentException (
							"Illegal escape in " + key);
					}
					c = key.charAt (n++);
				}
				b.append (c);
			}
			i = new Directory (b.toString (), true);
			if (forDirs != null)
			{
				if(forDirs instanceof ComponentDescriptor)
				{
					i.initComponentDescriptor ((ComponentDescriptor)forDirs);
				}
				if(forDirs instanceof PluginDescriptor)
				{
					i.initPluginDescriptor ((PluginDescriptor)forDirs);
				}
			}
			d.add (i);
			d = i;
		}
	}

	
	private static final ItemCriterion ACTIVATED = new ItemCriterion ()
	{
		@Override
		public boolean isFulfilled (Item item, Object info)
		{
			return item.isActivated () == Boolean.TRUE.equals (info);
		}

		@Override
		public String getRootDirectory ()
		{
			return null;
		}
	};

	public final boolean activateItems ()
	{
		boolean activated = false;
		synchronized (writeLock)
		{
			beginXA ();
			try
			{
				Item[] a;
				do
				{
					a = findAll (ACTIVATED, Boolean.FALSE, false);
					activationStamp++;
					for (int i = 0; i < a.length; i++)
					{
						activated = true;
						a[i].activate ();
					}
				}
				while (a.length > 0);
			}
			finally
			{
				commitXA (false);
			}
		}
		return activated;
	}

	public int getActivationStamp ()
	{
		return activationStamp;
	}

	public void startup ()
	{
		if (active)
		{
			throw new IllegalStateException (this + " is already active");
		}
		activateItems ();
		do
		{
			runAfterCommit ();
		}
		while (activateItems ());
		root.updateChildren (true);
		active = true;
	}

	public boolean isActive ()
	{
		return active;
	}

	public void dispose ()
	{
		if (project != null)
		{
			project.close ();
		}
		setFileSystem (null);
		Handler[] h = logger.getHandlers ();
		for (int i = 0; i < h.length; i++)
		{
			logger.removeHandler (h[i]);
		}
	}

	public Object setUserProperty (int id, Object value)
	{
		return userProperties.synchronizedPut (id, value);
	}

	public Object getUserProperty (int id)
	{
		return userProperties.synchronizedGet (id);
	}
	
	public void setMainRGG (int id)
	{
		mainRGGID = id;
	}

	public Object getMainRGG ()
	{
		return userProperties.synchronizedGet (mainRGGID);
	}

	
	
	/*
	 public void addItemFieldListener (String item, ItemFieldListener l)
	 {
	 Object o = itemListeners.get (item);
	 if (o instanceof XVector)
	 {
	 ((XVector) o).addIfNotContained (l);
	 }
	 else if (o != null)
	 {
	 XVector v = new XVector ();
	 v.add (o);
	 v.addIfNotContained (l);
	 itemListeners.put (item, v);
	 }
	 else
	 {
	 itemListeners.put (item, l);
	 }
	 }


	 public void removeItemFieldListener (String item, ItemFieldListener l)
	 {
	 Object o = itemListeners.get (item);
	 if (o instanceof XVector)
	 {
	 ((XVector) o).remove (l);
	 }
	 else if (o != null)
	 {
	 itemListeners.remove (item);
	 }
	 }


	 void fieldModified (Item item, PersistenceField field, XAThreadState t)
	 {
	 if (t != null)
	 {
	 ass ert t == item.getXAThreadState ();
	 Object l = itemListeners.get (item.getAbsoluteKey ());
	 if (l instanceof XVector)
	 {
	 XVector v = (XVector) l;
	 for (int i = v.size () - 1; i >= 0; i--)
	 {
	 ((ItemFieldListener) v.get (i))
	 .fieldModified (item, field, t);
	 }
	 }
	 else if (l != null)
	 {
	 ((ItemFieldListener) l).fieldModified (item, field, t);
	 }
	 }
	 }
	 */

	private void updateFields (Item item)
	{
		if (fieldChanged.contains (item))
		{
			fireTreeNodesChanged (item);
		}
		for (item = (Item) item.getBranch (); item != null; item = (Item) item
			.getSuccessor ())
		{
			updateFields (item);
		}
	}

	void fireTreeNodesChanged (Item item)
	{
		treeMulticaster.treeNodesChanged (((Item) item.getAxisParent ())
			.createTreeModelEvent (item));
	}

	private final ObjectList fieldChanged = new ObjectList (10, false),
			structureChanged = new ObjectList (10, false);

	@Override
	public void transactionApplied (Transaction.Data xa, boolean rollback)
	{
		assert Thread.holdsLock (writeLock);
		fieldChanged.clear ();
		structureChanged.clear ();
		reader.getQueue ().restore (xa);
		reader.resetCursor ();
		try
		{
			if (rollback)
			{
				reader.supplyInverse (this);
			}
			else
			{
				reader.supply (this);
			}
		}
		catch (java.io.IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		if (!structureChanged.isEmpty ())
		{
			for (int i = itemMap.size () - 1; i >= 0; i--)
			{
				itemMap.setValueAt (i, null);
			}
			stamp++;
			new TreeDiff ()
				.compare (oldTree, this, this, this, treeMulticaster);
			root.updateChildren (true);
		}
		if (!fieldChanged.isEmpty ())
		{
			updateFields (root);
		}
		/*		if (!inverse && !structureChanged.isEmpty ())
		 {
		 XVector v = new XVector (structureChanged.size () * 2);
		 for (int i = structureChanged.size () - 1; i >= 0; i--)
		 {
		 for (Item c = (Item) ((Item) structureChanged.get (i))
		 .getFirstChild ();
		 c != null; c = (Item) c.getNextSibling ())
		 {
		 if (!c.isInitialized ())
		 {
		 v.add (c);
		 }
		 }
		 }
		 boolean nested = initializingItem != null;
		 for (int i = v.size () - 1; i >= 0; i--)
		 {
		 Item item = (Item) v.get (i);
		 if (nested)
		 {
		 System.out.println (initializingItem + " creates "
		 + item);
		 }
		 else
		 {
		 initializingItem = item; 
		 }
		 item.initialize (nested);
		 }
		 if (!nested)
		 {
		 initializingItem = null; 
		 }
		 }
		 */
	}

	@Override
	public void setField (PersistenceCapable o, PersistenceField field,
			int[] indices, Transaction.Reader reader)
	{
		if (o instanceof Item)
		{
			fieldChanged.addIfNotContained (o);
		}
	}

	@Override
	public void insertComponent (PersistenceCapable o, PersistenceField field,
			int[] indices, Transaction.Reader reader)
	{
		setField (o, field, indices, reader);
	}

	@Override
	public void removeComponent (PersistenceCapable o, PersistenceField field,
			int[] indices, Transaction.Reader reader)
	{
		setField (o, field, indices, reader);
	}

	@Override
	public void addEdgeBits (Node source, Node target, int mask)
	{
		//		if (((mask ^= source.getEdgesTo (target)) & EdgeSet.BRANCH) != 0)
		if ((mask & Graph.SUCCESSOR_EDGE) != 0)
		{
			source = source.getAxisParent ();
			if (source == null)
			{
				source = target.getAxisParent ();
			}
		}
		else if ((mask & Graph.BRANCH_EDGE) == 0)
		{
			source = null;
		}
		if (source != null)
		{
			structureChanged.addIfNotContained (source);
		}
	}

	@Override
	public void removeEdgeBits (Node source, Node target, int mask)
	{
		addEdgeBits (source, target, mask);
	}

	@Override
	public void makePersistent (long id, ManageableType type)
	{
	}

	@Override
	public void makeTransient (PersistenceCapable o, ManageableType type)
	{
	}

	@Override
	public void readData (PersistenceCapable o, Transaction.Reader reader)
	{
	}

	@Override
	public void begin ()
	{
	}

	@Override
	public void end ()
	{
	}

	@Override
	public SharedObjectProvider lookup (String name)
	{
		return "/".equals (name) ? (SharedObjectProvider) this
				: GraphManager.META_GRAPH.equals (name) ? project : null;
	}

	@Override
	public String getProviderName ()
	{
		return "/";
	}

	private final SOReferenceImpl ref = new SOReferenceImpl ();

	@Override
	public ResolvableReference readReference (PersistenceInput in)
			throws java.io.IOException
	{
		final String item = in.readString ();
		Shareable s = getShareable (item);
		if (s != null)
		{
			ref.object = s;
			return ref;
		}
		return new ResolvableReference ()
		{
			@Override
			public Shareable resolve ()
			{
				return getShareable (item);
			}

			@Override
			public boolean isResolvable ()
			{
				return getShareable (item) != null;
			}
		};
	}

	@Override
	public void writeObject (Shareable object, PersistenceOutput out)
			throws java.io.IOException
	{
		object.getProvider ().writeObject (object, out);
	}

	Shareable getShareable (String key)
	{
		Item i;
		Object o;
		if (((i = Item.resolveItem (this, key)) instanceof ObjectItem)
			&& ((o = ((ObjectItem) i).getObject ()) instanceof Shareable))
		{
			return (Shareable) o;
		}
		return null;
	}

	public SAXSource createXMLSource (Collection requiredPlugins)
	{
		return new XMLSerializer (this, requiredPlugins);
	}

	public org.xml.sax.ContentHandler createXMLReader ()
	{
		return new XMLRegistryReader (this, null, regGraph.getBindings (),
			root, true);
	}

	@Override
	public boolean equals (Object a, Object b)
	{
		return a == b;
	}

	@Override
	public TreeDiff.DiffInfo getDiffInfo (Object node)
	{
		return ((Item) node).getDiffInfo ();
	}

	@Override
	public ClassLoader getClassLoader ()
	{
		return Main.getLoaderForAll ();
	}

	@Override
	public Class classForName (String name) throws ClassNotFoundException
	{
		return (Class) typeForName (name, true);
	}

	@Override
	public Type typeForName (String name) throws ClassNotFoundException
	{
		return (Type) typeForName (name, false);
	}

	private Object typeForName (String name, boolean cls) throws ClassNotFoundException
	{
		Object o = pluginTypes.get (name);
		if (o != null)
		{
			return cls ? ((Type) o).getImplementationClass () : o;
		}

		if (root != null)
		{
			o = getItem ("/classes/" + name);
			if ((o instanceof ObjectItem)
				&& ((ObjectItem) o).isInstance (Type.TYPE))
			{
				Type t = (Type) ((ObjectItem) o).getObject ();
				return cls ? t.getImplementationClass () : (Object) t;
			}
		}

		for (Item i = (Item) getRootRegistry ().getPluginDirectory ()
			.getBranch (); i != null; i = (Item) i.getSuccessor ())
		{
			if (i instanceof PluginDescriptor)
			{
				PluginClassLoader loader = ((PluginDescriptor) i)
					.getPluginClassLoader ();
				if (loader != null)
				{
					try
					{
						Type t = loader.typeForName (name);
						pluginTypes.put (name, t);
						return cls ? t.getImplementationClass () : (Object) t;
					}
					catch (ClassNotFoundException e)
					{
					}
				}
			}
		}
		Class c = Class.forName (name, false, getClassLoader ());
		return cls ? c : (Object) ClassAdapter.wrap (c, false);
	}

	private static final ThreadLocal REGISTRIES = new ThreadLocal ();

	public static void setCurrent (RegistryContext current)
	{
		REGISTRIES.set ((current != null) ? current.getRegistry () : null);
	}

	public static Registry current ()
	{
		return (Registry) REGISTRIES.get ();
	}
	
	private static ArrayList<String> methodNameList; 
	private static ArrayList<MethodDescriptionContent> methodList;
	
	@SuppressWarnings("unchecked")
	public void setMethodDescription (ArrayList<MethodDescriptionContent> methods) {
		Collections.sort(methods);
		methodNameList = new ArrayList<String>();
		Iterator<MethodDescriptionContent> iter = methods.iterator();
		while (iter.hasNext()) {
			MethodDescriptionContent method = iter.next();
			// check if:  
			if(//it a "public static" method
				method.isStatic () && method.getVisibility ().equals ("public") &&
				// and the name contains no "$" sign 
				!method.getName ().contains ("$")) {
				//extract name for the list of all method names
				methodNameList.add(method.getName ());
			} else {
				iter.remove ();
			}
		}
		methodList = methods;
	}

	public ArrayList<String> getMethodNameList() {
		return methodNameList;
	}
	
	public ArrayList<MethodDescriptionContent> getMethodList() {
		return methodList;
	}

	/***********************************
	 ** Components ********************* 
	 ***********************************/
	
	/**
	 * Adds a component to the component HashMap.
	 * 
	 * @param key
	 * @param cd
	 */
	public void addComponent(String key, ComponentDescriptor cd) {
		componentMap.put("/components"+key, cd);
	}

	/**
	 * Returns the component with the specified key.
	 * 
	 * @param key
	 * @return
	 */
	public ComponentDescriptor getComponent(String key) {
		return componentMap.get("/components"+key);
	}
	
	/**
	 * Returns the HashMap where all loaded components are stored.
	 * 
	 * @return
	 */
	public HashMap<String, ComponentDescriptor> getComponentMap() {
		return componentMap;
	}
	
	
	/**
	 * Returns a string containing the content of the coponentMap.
	 * 
	 * @return
	 */
	public String componentMapToString() {
		return "Size = "+componentMap.size()+"\n"+componentMap.toString();
	}

	public void removeComponent (String key) {
		providedOutputSlots.remove (key);
		requiredInputSlots.remove (key);
	}

	public void addOutputSlot (String key, String value) {
		Set<String> s = providedOutputSlots.get (key); 
		if(s==null) {
			s = new TreeSet<String>();
		}
		s.add (value);
		providedOutputSlots.put (key, s);
	}

	public void addInputSlot (String key, String value) {
		Set<String> s = requiredInputSlots.get (key); 
		if(s==null) {
			s = new TreeSet<String>();
		}
		s.add (value);
		requiredInputSlots.put (key, s);
	}

	public String getOutputSlots () {
		StringBuffer sb = new StringBuffer();
		for (String elem : providedOutputSlots.keySet() ) {
			Set<String> s = providedOutputSlots.get (elem);
			sb.append (elem+"\t"+s.toString ()+"\n");
		}
		return sb.toString ();
	}

	public String getInputSlots () {
		StringBuffer sb = new StringBuffer();
		for (String elem : requiredInputSlots.keySet() ) {
			Set<String> s = requiredInputSlots.get (elem);
			sb.append (elem+"\t"+s.toString ()+"\n");
		}
		return sb.toString ();
	}

	public SortedMap<String, Set<String>> getOutputSlotMap () {
		return providedOutputSlots;
	}

	public SortedMap<String, Set<String>> getInputSlotMap () {
		return requiredInputSlots;
	}
}
