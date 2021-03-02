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

package de.grogra.rgg;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TimerTask;

import javax.swing.KeyStroke;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.apache.commons.math.ode.FirstOrderIntegrator;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.ScaleClass;
import de.grogra.imp.IMPWorkbench;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;
import de.grogra.numeric.FirstOrderIntegratorAdapter;
import de.grogra.numeric.GraphODE;
import de.grogra.numeric.NumericException;
import de.grogra.numeric.Solver;
import de.grogra.persistence.Transaction;
import de.grogra.pf.registry.Directory;
import de.grogra.pf.registry.Executable;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.NodeReference;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.GraphSelectionImpl;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.registry.CheckBoxItem;
import de.grogra.pf.ui.registry.CommandItem;
import de.grogra.pf.ui.registry.Group;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.rgg.model.RGGGraph;
import de.grogra.rgg.model.Runtime;
import de.grogra.turtle.TurtleState;
import de.grogra.util.Described;
import de.grogra.util.EnumerationType;
import de.grogra.util.I18NBundle;
import de.grogra.util.Lock;
import de.grogra.util.LockProtectedRunnable;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.vecmath.Math2;
import de.grogra.xl.lang.DisposableIterator;
import de.grogra.xl.lang.VoidToDouble;
import de.grogra.xl.util.ObjectList;

/**
 * This class is the base class of all instances of relational
 * growth grammars within GroIMP. The main class of an
 * rgg-file automatically extends this class.
 * <br>
 * This class declares some methods which may be overriden
 * by subclasses in order to respond to specific events:
 * <ul>
 * <li>
 * {@link #init()} is invoked to initialize an RGG
 * instance. This happens after the compilation
 * of its source code and after a reset.
 * <li>
 * {@link #startup()} is invoked when an RGG instance
 * is loaded within GroIMP. This happens after the compilation
 * of its source code and after loading a project containing an RGG.
 * <li>
 * {@link #shutdown()} is invoked when an RGG instance
 * is unloaded within GroIMP. This happens to an old RGG instance
 * after compilation when this old instance is to be replaced
 * by the newly compiled RGG instance.
 * <li>
 * {@link #run(Object)} is invoked as a response to the invocation
 * of {@link #runLater(Object)} or {@link #runLater(long, Object)}.
 * The argument is the
 * argument which has been passed to <code>runLater</code>. The
 * invocation of <code>run</code> is queued in the
 * {@link de.grogra.pf.ui.JobManager} of the current
 * workbench and executed in its thread.
 * </ul>
 * A newly compiled RGG induces an initialization of the graph
 * as defined by {@link #reset()}.
 * 
 * @author Ole Kniemeyer
 */
public class RGG extends Node implements Selectable, RegistryContext
//multiscale begin
,Observer
//multiscale end
{
	public static final I18NBundle I18N = I18NBundle.getInstance (RGG.class);
	
	private static final int RGG_BY_TYPE_ID = Registry.allocatePropertyId ();
	private static final int MAIN_RGG_ID = Registry.allocatePropertyId ();

	private static final long MEMORY_LIMIT = 6000000;

	private transient volatile boolean stop = false;

	final transient Object cancelLock = new Object ();
	transient TimerTask cancelTask = null;
	transient RGGGraph toCancel = null;
	transient volatile boolean doCancel = false;

	static final RuntimeException CANCEL = new RuntimeException (
		"Cancel productions");

	private transient Registry registry;
	private transient Lock currentLock;
	private transient ObjectList items;

	protected TurtleState initialTurtleState = new TurtleState ();
	//enh:field type=TurtleState.$TYPE

	private static final Class[] CLASS_0 = {};

	private transient ConcurrentTasks tasks = new ConcurrentTasks ();

	public static final UIProperty STEPRUN = UIProperty.getOrCreate ("/workbench/rgg/toolbar/steprun", UIProperty.WORKBENCH);
	
	public static final EnumerationType GROUP_TYPE = new EnumerationType (
			"group_type", RGG.I18N, 2);
	
	//multiscale begin
	//Map from observable to argument and method name.
	//(In order to invoke a specified method, given an observable and argument) 
	private HashMap<Observable, HashMap<Object, String> > observableMethodMap;
	//multiscale end
	
	/**
	 * Contains the list of classes which are to be ignored or considered by
	 * {@link #left(Node)} and {@link #right(Node)}.
	 *
	 * @see #consider
	 * @see #ignore(Class[])
	 * @see #consider(Class[])
	 */
	private Class[] classesToIgnoreOrConsider = CLASS_0;
	//enh:field

	/**
	 * Defines whether the classes in {@link #classesToIgnoreOrConsider} shall
	 * be ignored or considered.
	 * 
	 * @see #classesToIgnoreOrConsider
	 */
	private boolean consider = false;
	//enh:field
	
	private boolean doRun = false;
	//enh:field

	public class Apply extends LockProtectedCommand
	{
		private final Method method;
		private boolean loop;
		private final boolean useRunCheckBox;
		private boolean running = false;
		private boolean done = false;
		
		public Apply (Method method, boolean loop, boolean useRunCheckBox)
		{
			super (RGG.this.getGraph (), true, JobManager.ACTION_FLAGS);
			this.method = method;
			this.loop = loop;
			this.useRunCheckBox = useRunCheckBox;
		}

		@Override
		protected void runImpl (Object info, Context ctx, Lock lock)
		{
			if (useRunCheckBox)
			{
				Boolean steprun = (Boolean) STEPRUN.getValue(ctx);
				if (steprun != null)
					loop = steprun;
				else
					loop = false;
			}
			
			boolean r = running;
			running = false;
			if (info != this)
			{
				RGG.this.stop = false;
				if (loop && r)
				{
					return;
				}
			}
			else if (RGG.this.stop)
			{
				return;
			}
			IMPWorkbench w = (IMPWorkbench) ctx.getWorkbench ();
			boolean modified = false;
			try
			{
				currentLock = lock;
				modified = applyRules (w.getRegistry ().getProjectGraph ().getActiveTransaction ());
			}
			finally
			{
				currentLock = null;
			}
			if (modified && loop && !RGG.this.stop)
			{
				running = true;
				w.getJobManager ().runLater (10, this, this, ctx);
			}
		}

		protected boolean applyRules (Transaction t)
		{
			return apply (method, true, t);
		}
		
		@Override
		protected void done (Context c)
		{
			super.done(c);
			done = true;
			synchronized (this) {
				notifyAll();
			}
		}
		
		public boolean isDone() {
			return done;
		}
	}

	public RGG ()
	{
		super ();
		if (getNType () != null)
		{
			registry = Registry.current ();
		}
		//multiscale begin
		observableMethodMap = new HashMap<Observable, HashMap<Object, String> >();
		//multiscale end
	}

	public Selection toSelection (Context ctx)
	{
		return new GraphSelectionImpl (ctx, GraphState.get (ctx.getWorkbench ()
			.getRegistry ().getProjectGraph (), UI.getThreadContext (ctx)),
			this, true);
	}

	public static void startup (final Registry r, final Type type)
	{
		class Task implements Runnable, LockProtectedRunnable
		{
			public void run ()
			{
				Utils.executeForcedlyAndUninterruptibly (r.getProjectGraph (),
					this, true);
			}

			public void run (boolean sameThread, Lock lock)
			{
				RGG rgg = null;
				boolean newRGG = true;
				Node n = (Node) r.getProjectGraph ().getRoot (
					GraphManager.META_GRAPH);
				if (n != null)
				{
					for (Edge e = n.getFirstEdge (); e != null; e = e
						.getNext (n))
					{
						Node t = e.getTarget ();
						if ((t instanceof RGG)
							&& Reflection.equal (((RGG) t).getNType (), type))
						{
							rgg = (RGG) t;
							newRGG = false;
							break;
						}
					}
				}
				if (newRGG)
				{
					try
					{
						rgg = (RGG) type.newInstance ();
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace ();
						return;
					}
					catch (InvocationTargetException e)
					{
						e.printStackTrace ();
						return;
					}
					catch (InstantiationException e)
					{
						e.printStackTrace ();
						return;
					}
					rgg.setExtentIndex (LAST_EXTENT_INDEX);
					r.getProjectGraph ().addMetaNode (rgg, null);
					r.getDirectory ("/project/objects/meta", null)
						.addUserItemWithUniqueName (new NodeReference (rgg),
							type.getSimpleName ());
				}
				Hashtable<String,RGG> s = (Hashtable<String,RGG>) r.getUserProperty (RGG_BY_TYPE_ID);
				if (s == null)
				{
					r.setUserProperty (RGG_BY_TYPE_ID, s = new Hashtable<String,RGG> (16));
				}
				s.put (type.getBinaryName (), rgg);
				rgg.startup ();
				if (newRGG)
				{
					rgg.reset ();
				}
			}
		}

		r.invokeAfterCommit (new Task ());
	}

	/**
	 * Determins whether this RGG should be used as the project's main RGG.
	 * The main RGG creates menu entries in the RGG toolbar. A project must
	 * not contain more than one main RGG.
	 * <p>
	 * This default implementation defines an RGG to be the main RGG iff
	 * its class declares a <code>public</code> <code>void</code> method
	 * with no arguments, or an <code>init</code> method. This method should
	 * be overwritten by non-main RGGs for which this simple definition
	 * is not sufficient.  
	 * 
	 * @return <code>true</code> iff this RGG is the main RGG of a project
	 */
	protected boolean isMainRGG ()
	{
		Type type = getNType ();
		for (int i = type.getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			Method m = type.getDeclaredMethod (i);
			if (m.getDescriptor ().equals ("minit;()V"))
			{
				return true;
			}
			else if (isTransformationMethod (m))
			{
				return true;
			}
		}
		return false;
	}

	public static RGG getMainRGG (RegistryContext r)
	{
		return (RGG) r.getRegistry ().getUserProperty (MAIN_RGG_ID);
	}


	public static void shutdown (final Registry r, final Type t)
	{
		Hashtable<String,RGG> s = (Hashtable<String,RGG>) r.getUserProperty (RGG_BY_TYPE_ID);
		RGG rgg;
		if ((s != null)
			&& ((rgg = s.remove (t.getBinaryName ())) != null))
		{
			rgg.shutdown ();
			NodeReference ref = NodeReference.get (r, "/project/objects/meta",
				rgg);
			if (ref != null)
			{
				ref.remove ();
			}
			r.getProjectGraph ().removeMetaNode (rgg, r.getProjectGraph ().getActiveTransaction ());
		}
	}

	public static Map<String,RGG> getRGGForTypeMap (RegistryContext r)
	{
		return (Hashtable<String,RGG>) r.getRegistry ().getUserProperty (RGG_BY_TYPE_ID);
	}

	public Registry getRegistry ()
	{
		return registry;
	}

	/**
	 * Returns the RGG root of the graph. This is not
	 * necessarily the real root of the graph: If there exists
	 * a branch edge from the real root to an instance of
	 * {@link RGGRoot}, then this instance is returned. Otherwise,
	 * the real root is returned. 
	 * 
	 * @return RGG root of graph
	 */
	public Node getRoot ()
	{
		return RGGRoot.getRoot (getRegistry ().getProjectGraph ());
	}

	static boolean lessMemory ()
	{
		// check available free memory
		java.lang.Runtime r = java.lang.Runtime.getRuntime ();
		return (r.freeMemory () < MEMORY_LIMIT)
		// check if the maximum memory limit is about to be reached
			&& (r.totalMemory () > r.maxMemory () - MEMORY_LIMIT);
	}

	boolean apply (Method method, final boolean interruptible, Transaction t)
	{
		DisposableIterator it = Library.apply (1);
		Throwable ex = null;
		RGGGraph g = Runtime.INSTANCE.currentGraph ();
		g.setProductionCallback (new Runnable ()
		{
			private boolean memoryOK = true;
			private int counter;

			public void run ()
			{
				// do the following every eighth invocation
				if ((counter++ & 7) == 0)
				{
					if (memoryOK)
					{
						if (lessMemory ())
						{
							System.gc ();
							if (java.lang.Runtime.getRuntime ().freeMemory () < MEMORY_LIMIT)
							{
								memoryOK = false;
								Workbench w = Workbench.current ();
								if ((w != null) && !w.isHeadless ())
								{
									if (w.getWindow ().showDialog (
										Library.I18N
											.msg ("rgg.lessmemory-title"),
										Library.I18N
											.msg ("rgg.lessmemory-confirm"),
										Window.QUESTION_MESSAGE) == Window.YES_OK_RESULT)
									{
										throw CANCEL;
									}
								}
							}
						}
					}
					else if (!lessMemory ())
					{
						memoryOK = true;
					}
				}

				if (doCancel)
				{
					Workbench w = Workbench.current ();
					if (w != null)
					{
						if (w.getWindow ().showDialog (
							Library.I18N.msg ("rgg.confirmcancel-title"),
							Library.I18N.msg ("rgg.confirmcancel"),
							Window.QUESTION_MESSAGE) != Window.YES_OK_RESULT)
						{
							doCancel = false;
							return;
						}
					}
					throw CANCEL;
				}
			}
		});
		synchronized (cancelLock)
		{
			toCancel = interruptible ? g : null;
		}
		int s = getGraph ().getStamp ();
		try
		{
			while (it.next ())
			{
				if (method != null)
				{
					method.invoke (this, null);
				}
				else
				{
					init ();
				}
			}
			// hook when rgg finished
			StringMap m = new StringMap();
			m.put("workbench", Workbench.current());
			m.put("methodName", method != null ? method.getName() : null);
			Executable.runExecutables (getRegistry().getRootRegistry(), "/hooks/rgg/finished", Workbench.current(), m);
		}
		catch (Throwable e)
		{
			if (e instanceof InvocationTargetException)
			{
				e = e.getCause ();
			}
			if (Utils.unwrapFully (e) == CANCEL)
			{
				stop ();
				Workbench.get (this).getLogger ().log (Workbench.GUI_INFO,
					Library.I18N.msg ("rgg.canceled"));
			}
			else
			{
				ex = e;
				Utils.rethrow (e);
			}
		}
		finally
		{
			synchronized (cancelLock)
			{
				if (cancelTask != null)
				{
					cancelTask.cancel ();
					cancelTask = null;
				}
				toCancel = null;
				doCancel = false;
			}
			g.setProductionCallback (null);
			it.dispose (ex);
		}
		return getGraph ().getStamp () != s;
	}

	protected void initializeApplyMenu (Item d, boolean flat, boolean useRunCheckBox)
	{
		Item applyGroup;
		if (flat)
		{
			applyGroup = null;
		}
		else
		{
			applyGroup = new Group ("apply", true);
			applyGroup.setDescription (Described.NAME, "Apply");
		}
		initialize (d, applyGroup, false, useRunCheckBox, "");
	}

	protected void initializeRunMenu (Item d, boolean flat, boolean useRunCheckBox)
	{
		Item runGroup;
		if (flat)
		{
			runGroup = null;
		}
		else
		{
			runGroup = new Group ("run", true);
			runGroup.setDescription (Described.NAME, "Run");
		}
		initialize (d, runGroup, true, useRunCheckBox, "Run ");
	}

	public static boolean isTransformationMethod (Method m)
	{
		return ((m.getModifiers () & (Member.STATIC | Member.PUBLIC)) == Member.PUBLIC)
			&& !Reflection.isCtor (m)
			&& (m.getParameterCount () == 0)
			&& (m.getReturnType ().getTypeId () == TypeId.VOID);
	}

	private void initialize (Item d, Item group, boolean loop, boolean useRunCheckBox, String prefix)
	{
		Item item = Item.resolveItem (Workbench.current(), "/rgg/toolbar");
		int numberToGroup = Utils.getInt(item, "numberToGroup");
		int groupType = Utils.getInt(item, "group_type");
		boolean groupAtMethodCount = groupType == 0;
		int count = 0;
		boolean needGroup = false;
		
		NType n = getNType ();
		int declaredMethodCount = n.getDeclaredMethodCount();
		
		for (int i = 0; i < declaredMethodCount; i++)
		{
			Method m = n.getDeclaredMethod (i);
			if (isTransformationMethod (m))
			{
				String methodName = m.getSimpleName ();
				if (!groupAtMethodCount)
					count += methodName.length();
				CommandItem ci = new CommandItem (prefix + methodName, new Apply (
					m, loop, useRunCheckBox));
				Annotation a = Reflection.getDeclaredAnnotation (m, Accelerator.class);
				if (a != null)
				{
					String s = (String) a.value ("value");
					if (loop)
					{
						s = "shift " + s;
					}
					if (KeyStroke.getKeyStroke (s) != null)
					{
						ci.setDescription (Described.ACCELERATOR_KEY, s);
					}
				}
				if ((group == null) || (count < numberToGroup))
					d.add (ci);
				else {
					group.add (ci);
					needGroup = true;
				}
				if (groupAtMethodCount)
					count++;
			}
		}
		
		if (needGroup)
			d.add (group);
	}

	private static final ItemVisitor STARTUP = new ItemVisitor ()
	{
		public void visit (Item item, Object info)
		{
			((RGG) info).createItemsCallback ();
		}
	};

	/**
	 * This method is invoked when an RGG instance
	 * is loaded within GroIMP. This happens after the compilation
	 * of its source code and after loading a project containing an RGG.
	 * <br>
	 * If this method is overridden, <code>super.startup();</code> should
	 * be invoked at first.
	 */
	protected void startup ()
	{
		if (!isMainRGG ())
		{
			return;
		}
		registry.setUserProperty (MAIN_RGG_ID, this);
		Runtime.INSTANCE.setCurrentGraph (registry.getProjectGraph ());
		items = new Item ("").deriveItems (STARTUP, this);
	}

	void startCancelTask ()
	{
		synchronized (cancelLock)
		{
			if ((cancelTask != null) || (toCancel == null))
			{
				return;
			}
			cancelTask = new TimerTask ()
			{
				@Override
				public void run ()
				{
					synchronized (cancelLock)
					{
						if ((cancelTask == this) && (toCancel != null))
						{
							cancelTask = null;
							doCancel = true;
						}
					}
				}
			};

			Workbench.TIMER.schedule (cancelTask, 3000);
		}
	}

	void createItemsCallback ()
	{
		Registry r = registry;
		Item rggDir = r.getDirectory ("/workbench/rgg", null);
		Item toolbar = rggDir.getItem ("toolbar");
		if (toolbar == null)
		{
			toolbar = new Group ("toolbar", false);
			rggDir.add (toolbar);
		}
		Item methods = rggDir.getItem ("methods");
		if (methods == null)
		{
			methods = new Directory ("methods");
			rggDir.add (methods);
		}

		final Workbench wb = Workbench.get (this);

		class Reset extends LockProtectedCommand implements Runnable
		{
			Reset ()
			{
				super (getGraph (), true, JobManager.ACTION_FLAGS);
			}

			public void run ()
			{
				if (wb != null)
				{
					wb.getJobManager ().runLater (this, null, wb,
						JobManager.ACTION_FLAGS);
				}
				startCancelTask ();
			}

			@Override
			protected void runImpl (Object info, Context ctx, Lock lock)
			{
				reset ();
			}

			@Override
			public String getCommandName ()
			{
				return null;
			}
		}

		Item i = new CommandItem ("reset", (Runnable) new Reset ());
		i.setDescription (Described.NAME, "Reset");
		toolbar.add (i);

		i = new CommandItem ("stop", new Runnable ()
		{
			public void run ()
			{
				stop ();
				startCancelTask ();
			}
		});
		i.setDescription (Described.NAME, "Stop");
		toolbar.add (i);

		i = Item.resolveItem (Workbench.current(), "/rgg/toolbar");
		boolean useRunCheckBox = Utils.getBoolean(i, "useRunCheckBox");
		initializeApplyMenu (toolbar, false, useRunCheckBox);
		initializeApplyMenu (methods, true, false);

		if (useRunCheckBox == false)
		{
			initializeRunMenu (toolbar, false, useRunCheckBox);
		}
		else
		{
			Item runCB = toolbar.getItem("steprun");
			runCB = new CheckBoxItem();
			runCB.setName("steprun");
			runCB.setDescription(Described.NAME, "Run");
			toolbar.add(runCB);
		}
		initializeRunMenu (methods, true, false);
	}

	/**
	 * This method is invoked when an RGG instance
	 * is unloaded within GroIMP. This happens to an old RGG instance
	 * after compilation when this old instance is to be replaced
	 * by the newly compiled RGG instance.
	 * <br>
	 * If this method is overriden, <code>super.shutdown();</code> should
	 * be invoked.
	 */
	protected void shutdown ()
	{
		stop ();
		Item.removeDerivedItems (registry, items);
		if (registry.getUserProperty (MAIN_RGG_ID) == this)
		{
			registry.setUserProperty (MAIN_RGG_ID, null);
		}
	}

	boolean isJavaObject ()
	{
		return Reflection.equal (getClass (), getNType ());
	}

	/**
	 * This method can be invoked to reset the RGG
	 * to its initial state. The following steps
	 * are performed:
	 * <ol>
	 * <li>
	 * If there is an instance of {@link RGGRoot}
	 * which is connected with the real root of the
	 * graph by a branch edge, then this instance is removed.
	 * A new instance of <code>RGGRoot</code>
	 * is created and connected with the real root
	 * by a branch edge.
	 * <li>
	 * A new instance of {@link Axiom} is created
	 * and inserted in the graph by a successor edge
	 * from the <code>RGGRoot</code> instance.
	 * The result of this step
	 * is a graph containing an <code>RGGRoot</code>
	 * which has a single <code>Axiom</code> as its
	 * successor. 
	 * <li>
	 * The method {@link #init()} is invoked. This invocation
	 * is surrounded by transformation boundaries
	 * (see {@link Library#apply()}).
	 * </ol>
	 * This method is also invoked when a
	 * newly compiled RGG is instantiated.
	 */
	protected void reset ()
	{
		if (!isMainRGG ())
		{
			return;
		}
		Transaction xa = getPersistenceManager ().getActiveTransaction ();
		Node root = registry.getProjectGraph ().getRoot ();
		for (Edge e = root.getFirstEdge (), f; e != null; e = f)
		{
			f = e.getNext (root);
			if (e.testEdgeBits (Graph.BRANCH_EDGE)
				&& (e.getTarget () instanceof RGGRoot))
			{
				e.remove (xa);
			}
		}
		RGGRoot rr = new RGGRoot ();
		root.addEdgeBitsTo (rr, Graph.BRANCH_EDGE, xa);
		rr.addEdgeBitsTo (new Axiom (), Graph.SUCCESSOR_EDGE, xa);
		Method m = Reflection.getDeclaredMethod (getNType (), "minit;()V");
		if (m != null)
		{
			xa.commitAll ();
			if (isJavaObject ())
			{
				m = null;
			}
			apply (m, false, xa);
		}
	}

	/**
	 * This method is invoked to initialize an RGG
	 * This happens after the compilation
	 * of its source code and after a reset.
	 * <br>
	 * The default implementation does nothing. It
	 * may be overridden by subclasses in order
	 * to perform some initialization. This may include
	 * the creation of an initial graph structure,
	 * or the initialization of field values.  
	 */
	protected void init ()
	{
	}
	
	/**
	 * This method is used to invoke the normally
	 * protected init-method when compiler is called
	 * from outside GroIMP.
	 */
	public void invokeInit()
	{
		init();
	}

	public void initializeTurtleState (TurtleState s)
	{
		TurtleState t = initialTurtleState;
		t.initialState = t;
		s.initialState = t;
		s.length = s.localLength = t.length;
		s.diameter = s.localDiameter = t.diameter;
		s.parameter = s.localParameter = t.parameter;
		s.carbon = s.localCarbon = t.carbon;
		s.tropism = s.localTropism = t.tropism;
		s.color = s.localColor = t.color;
		s.heartwood = s.localHeartwood = t.heartwood;
		s.internodeCount = s.localInternodeCount = t.internodeCount;
	}


	/**
	 * This method can be invoked in order to stop the repeated invocation
	 * of a method.
	 */
	public void stop ()
	{
		stop = true;
	}

	
	/**
	 * This method can be invoked if interpretive rules shall be applied
	 * to the current graph by the method {@link #interpret()}. The effect
	 * of <code>applyInterpretation</code> is the following:
	 * <ul>
	 * <li> Existing interpretive nodes are removed.
	 * <li> A transformation boundary is passed in order to apply pending
	 * transformations.
	 * <li> The current extent is set up such that following
	 * <code>==&gt;</code> rules are treated as interpretive rules. This
	 * action is undone at the end of this method.
	 * <li> The method {@link #interpret()} is invoked.
	 * </ul>
	 */
	protected void applyInterpretation ()
	{
		RGGGraph ex = Runtime.INSTANCE.currentGraph ();
		ex.removeInterpretiveNodesOnDerivation ();
		ex.derive ();
		int old = ex.getDerivationMode ();
		ex.setDerivationMode (old | RGGGraph.INTERPRETIVE_FLAG);
		try
		{
			interpret ();
		}
		finally
		{
			ex.setDerivationMode (old);
		}
	}

	/**
	 * 
	 */
	protected void interpret ()
	{
	}

	////////////////////////////////////////////////////////////////////
	///// ODE  (begin)                                             /////
	////////////////////////////////////////////////////////////////////
	
	boolean autoClearMonitors = true;
	
	final GraphODE ode = new GraphODE() {
		@Override
		public void getRate() {
			// redirect to RGG.getRate()
			RGG.this.getRate();
		}
	};

	public Solver getSolver() {
		return ode.getSolver();
	}

	public void setSolver(Solver solver) {
		ode.setSolver(solver);
	}
	
	public void setSolver(FirstOrderIntegrator integrator) {
		ode.setSolver(new FirstOrderIntegratorAdapter(integrator));
	}
	
	/**
	 * Install a monitor function that generates an event when the
	 * returned value changes its sign. The monitor must not modify
	 * the state of the graph.
	 * @param g monitor function
	 */
	protected void monitor(final VoidToDouble g) {
		ode.monitor(g);
	}
	
	/**
	 * Install a monitor function that generates an event when the
	 * returned value changes its sign. The monitor must not modify
	 * the state of the graph. When the event triggers, the event
	 * handler is called.
	 * @param g monitor function
	 * @param r event handler
	 */
	protected void monitor(final VoidToDouble g, final Runnable r) {
		ode.monitor(g, r);
	}
	
	/**
	 * Trigger an event in regular intervals and call the event handler.
	 * Additionally, the write lock on the graph (automatically obtained
	 * when executing rules) is temporarily released so that modifications
	 * to the attributes made in the event handler will become visible.
	 * @param g monitor function
	 * @param r event handler
	 */
	protected void monitorPeriodic(final double period, final Runnable r) {
		ode.monitorPeriodic(period, new Runnable() {
			public void run() {
				r.run();

				// allow other threads (like 3D view) read-access to the graph
				// in case the r was updating some node attributes
				if (currentLock != null) {
					try {
						currentLock.executeWithoutWriteLock(new Runnable() {
							public void run() {
								try {
									// other threads may execute reader tasks
									Thread.sleep(0, 1);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Remove all currently set monitor functions.
	 * This will be called implicitly after {@link #integrate(double)} if {@link #isAutoClearMonitors()}
	 * is true.
	 */
	protected void unmonitor() {
		ode.unmonitor();
	}
	
	/**
	 * Get whether if all monitors should be removed automatically after integration. 
	 * @return true if all monitors should be removed after integration automatically
	 */
	public boolean isAutoClearMonitors() {
		return autoClearMonitors;
	}

	/**
	 * Set whether if all monitors should be removed automatically after integration. 
	 * @param autoClearMonitors if true, all monitors should be removed after integration automatically
	 */
	public void setAutoClearMonitors(boolean autoClearMonitors) {
		this.autoClearMonitors = autoClearMonitors;
	}

	/**
	 * Integrate numerically using the ODE framework for an unlimited duration of time units.
	 * This is implemented by calling {@link #integrate(double)} with a duration of {@link java.lang.Double.MAX_VALUE}.
	 * To stop the integration a monitor must be set.
	 * @throws NumericException 
	 * @see #integrate(double)
	 * @see #monitor(VoidToDouble)
	 */
	protected void integrate() throws NumericException {
		integrate(Double.MAX_VALUE);
	}

	/**
	 * Integrate numerically using the ODE framework over a duration of <code>duration</code>
	 * time units. The user has to provide a rate function dy/dt=f(t,y) to calculate the first
	 * derivative of the state vector with respect to time for a given time and state pair.
	 * The rate function f(t,y) is implemented by overriding the function {@link #getRate()}.
	 * The user should not make any assumptions about how many times or with which parameters of
	 * time and state the function {@link #getRate()} will be called. Since laws of physics do not
	 * depend on the current time the parameter t of f(t,y) is hidden from the user. The state y
	 * is provided implicitly as the attributes of the nodes in the current graph. 
	 * 
	 * @param duration time duration over which to integrate
	 * @throws NumericException 
	 * @see #getRate()
	 */
	protected void integrate(double duration) throws NumericException {
		ode.integrate(duration);

		// automatically remove all monitors from solver
		if (autoClearMonitors) {
			ode.unmonitor();
		}
	}
	
	/**
	 * Override this to provide the rate function using the rate
	 * assignment operator :'=.
	 * The state is passed implicitly as the current graph, and
	 * graph queries can be used to calculate the rates.
	 */
	protected void getRate()
	{
	}

	////////////////////////////////////////////////////////////////////
	///// ODE  (end)                                               /////
	////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Instructs the XL compiler to treat the method
	 * {@link #ignore(Class[])} as a variable arity method.
	 */
	private static final String $VARARGS_ignore = "mignore;([Ljava/lang/Class;)V";

	/**
	 * Sets the node classes which shall be ignored by the methods
	 * {@link #left(Node)} and {@link #right(Node)}. This method
	 * may be invoked as a variable arity method, i.e., with
	 * a variable number of arguments of class <code>Class</code>.
	 * 
	 * @param ignore an array of classes to be ignored
	 */
	protected void ignore (Class... ignore)
	{
		classesToIgnoreOrConsider = (ignore != null) ? ignore : CLASS_0;
		this.consider = false;
		ignoreFilter = null;
	}

	/**
	 * Sets the node classes which shall be considered by the methods
	 * {@link #left(Node)} and {@link #right(Node)}. This method
	 * may be invoked as a variable arity method, i.e., with
	 * a variable number of arguments of class <code>Class</code>.
	 * 
	 * @param consider an array of classes to be considered
	 */
	protected void consider (Class... consider)
	{
		classesToIgnoreOrConsider = (consider != null) ? consider : CLASS_0;
		this.consider = true;
		ignoreFilter = null;
	}

	private transient NodeFilter ignoreFilter;

	/**
	 * Returns a function which returns <code>false</code> iff
	 * its argument is an instance of at least one of the classes
	 * in {@link #classesToIgnoreOrConsider}.
	 * 
	 * @return function corresponding to the classes set by {@link #ignore(Class[])}
	 */
	private NodeFilter getIgnoreFilter ()
	{
		if (ignoreFilter == null)
		{
			ignoreFilter = Library.filter (classesToIgnoreOrConsider, consider);
		}
		return ignoreFilter;
	}

	/**
	 * Computes the first neighbour to the left of <code>n</code> which
	 * is not an instance of one of the classes set by
	 * {@link #ignore(Class[])}. The neighbours to the left
	 * are those which are reachable by successor or branch edges
	 * in reverse direction. 
	 * 
	 * @param n where to start the search
	 * @return first neighbour to the left, excluding instances
	 * of ignored classes
	 */
	public Node left (Node n)
	{
		return Library.ancestor (n, getIgnoreFilter ());
	}

	/**
	 * Computes the first neighbour to the right of <code>n</code> which
	 * is not an instance of one of the classes set by
	 * {@link #ignore(Class[])}. The neighbours to the right
	 * are those which are reachable by successor edges
	 * in forward direction. 
	 * 
	 * @param n where to start the search
	 * @return first neighbour to the right, excluding instances
	 * of ignored classes
	 */
	public Node right (Node n)
	{
		return Library.successor (n, getIgnoreFilter ());
	}

	ChannelData tmpData = new ChannelData ();
	ChannelMap tmpDataMap = null;

	public float readChannelAt (int channel, Parallelogram plane, float x,
			float y, float z)
	{
		Shader s = plane.getShader ();
		if (!(s instanceof Phong))
		{
			return 0;
		}
		ChannelMap m = ((Phong) s).getDiffuse ();
		if (m == null)
		{
			return 0;
		}
		if (m != tmpDataMap)
		{
			tmpDataMap = m;
			tmpData.clear ();
		}
		ChannelData src = tmpData, sink = src.createSink (m);
		Vector3f a1 = new Vector3f (0, 0, plane.getLength ()), a2 = plane
			.getAxis ();
		float oa1 = x * a1.x + y * a1.y + z * a1.z;
		float oa2 = x * a2.x + y * a2.y + z * a2.z;
		float a1a2 = a1.dot (a2), a12 = a1.lengthSquared (), a22 = a2
			.lengthSquared ();
		float detm1 = 1 / (a12 * a22 - a1a2 * a1a2);
		src.setFloat (Channel.U, detm1 * (a22 * oa1 - a1a2 * oa2));
		src.setFloat (Channel.V, detm1 * (a12 * oa2 - a1a2 * oa1));
		src.setFloat (Channel.X, x);
		src.setFloat (Channel.Y, y);
		src.setFloat (Channel.Z, z);
		return sink.getFloatValue (null, channel);
	}

	public float readChannelAt (int channel, Parallelogram plane, Node node)
	{
		Point3d p = tmpData.p3d0;
		Library.location (p, node, false);
		Math2.invTransformPoint (Library.transformation (plane), p);
		return readChannelAt (channel, plane, (float) p.x, (float) p.y,
			(float) p.z);
	}

	public float readChannelAt (int channel, String plane, Node node)
	{
		Node pl = node.getGraph ().getNodeForName (plane);
		if (!(pl instanceof Parallelogram))
		{
			return 0;
		}
		Point3d p = tmpData.p3d0;
		Library.location (p, node, false);
		Math2.invTransformPoint (Library.transformation (pl), p);
		return readChannelAt (channel, (Parallelogram) pl, (float) p.x,
			(float) p.y, (float) p.z);
	}

	/**
	 * Returns an instance of <code>ConcurrentTasks</code> which is associated
	 * with this <code>RGG</code>.
	 * 
	 * @return instance of <code>ConcurrentTasks</code> for use within this
	 * <code>RGG</code>
	 */
	public ConcurrentTasks getTasks ()
	{
		return tasks;
	}

	/**
	 * Adds a single <code>task</code> to the set of tasks of this
	 * <code>RGG</code> (see {@link #getTasks()}). After all tasks
	 * have been added, the method {@link #solveTasks()} has to be
	 * invoked in order to solve the tasks concurrently using a number
	 * of threads or even distributed computers.
	 * 
	 * @param task task to add
	 */
	public void addTask (ConcurrentTask task)
	{
		tasks.add (task);
	}

	/**
	 * This method simply invokes {@link ConcurrentTasks#solve()} on
	 * the set of tasks of this <code>RGG</code> (see {@link #getTasks()}).
	 * This solves all added tasks (see {@link #addTask(ConcurrentTask)})
	 * concurrently using the available resources (multiple processors,
	 * distributed computers).
	 */
	public void solveTasks ()
	{
		tasks.solve ();
	}

	private class RunLater extends LockProtectedCommand
	{
		RunLater ()
		{
			super (getGraph (), true, JobManager.ACTION_FLAGS);
		}

		@Override
		public String getCommandName ()
		{
			return "RGG";
		}

		@Override
		protected void runImpl (Object a, Context ctx, Lock lock)
		{
			RGG.this.run (a);
		}
	}

	/**
	 * This method is used to induce an invocation
	 * of {@link #run(Object)} in the thread
	 * of the {@link JobManager} of the current workbench.
	 * The invocation is queued in the job manager, i.e.,
	 * it is performed after all pending jobs have been
	 * completed.
	 * 
	 * @param arg argument to pass to {@link #run(Object)}
	 */
	public void runLater (Object arg)
	{
		Workbench wb = Library.workbench ();
		wb.getJobManager ().runLater (new RunLater (), arg, wb,
			JobManager.ACTION_FLAGS);
	}

	/**
	 * This method is used to induce an invocation
	 * of {@link #run(Object)} in the thread
	 * of the {@link JobManager} of the current workbench
	 * after a delay of at least <code>delay</code>
	 * milliseconds.
	 * The invocation is queued in the job manager, i.e.,
	 * it is performed after all pending jobs have been
	 * completed and after the delay is elapsed.
	 * 
	 * @param delay delay in milliseconds
	 * @param arg argument to pass to {@link #run(Object)}
	 */
	public void runLater (long delay, Object arg)
	{
		Workbench wb = Library.workbench ();
		wb.getJobManager ().runLater (delay, new RunLater (), arg, wb);
	}

	/**
	 * This callback method is invoked as a response to the invocation
	 * of {@link #runLater(Object)} or {@link #runLater(long, Object)}.
	 * <code>arg</code> is the
	 * argument which has been passed to <code>runLater</code>.
	 * The invocation is executed in the thread of
	 * the {@link JobManager} of the current workbench. In addition,
	 * a write-lock on the graph is being aquired during execution.
	 * <br>
	 * The default implementation does nothing.
	 * 
	 * @param arg the argument which has been passed to <code>runLater</code>
	 */
	protected void run (Object arg)
	{
	}

	//multiscale begin
	/**
	 * Register the method (by its name) to invoke given the notification from an 
	 * observable and an argument object.
	 * @param s
	 * @param arg
	 * @param methodName
	 */
	public void observe(ScaleClass s, Object arg, String methodName)
	{
		HashMap<Object, String> argMethodMap = observableMethodMap.get(s);
		
		//lazy addition to hash map
		if(argMethodMap == null)
		{
			argMethodMap = new HashMap<Object,String>();
			observableMethodMap.put(s.getScaleObserver(), argMethodMap);
		}
		
		argMethodMap.put(arg, methodName);
		
		s.addObserver(this);
	}
	
	/**
	 * Register the method (by its name) to invoke given the notification from an 
	 * observable and an argument object.
	 * @param o
	 * @param arg
	 * @param methodName
	 */
	public void observe(ScaleClass s, int arg, String methodName)
	{
		Integer i = new Integer(arg);
		
		observe(s, i, methodName);
	}
	
	/**
	 * Method invoked when the observable object observed by this object invokes notify
	 */
	@Override
	public void update(Observable o, Object arg) {
		HashMap<Object, String> argMethodMap = observableMethodMap.get(o);
		
		//if no argument & method-name pair for given observable, return.
		if(argMethodMap == null)
			return;
		
		String methodName = argMethodMap.get(arg);
		
		//if no method name for given argument, return.
		if(methodName == null)
			return;
		
		//method name found
		try {
			//get reference to method
			java.lang.reflect.Method method = this.getClass().getDeclaredMethod(methodName);
			method.setAccessible(true);
			//java.lang.reflect.Method[] methods = this.getClass().getMethods();
			//invoke the mehod
			method.invoke(this);
		} catch (NoSuchMethodException e) {
			//Library.println("Method \"" + methodName + "\" not found in class \"" + this.getClass() + "\"");
			return;
		} catch (SecurityException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		} catch (IllegalArgumentException e) {
			return;
		} catch (InvocationTargetException e) {
			return;
		} catch (Exception e){
			return;
		}
	}
	//multiscale end

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field initialTurtleState$FIELD;
	public static final NType.Field classesToIgnoreOrConsider$FIELD;
	public static final NType.Field consider$FIELD;
	public static final NType.Field doRun$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (RGG.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 2:
					((RGG) o).consider = (boolean) value;
					return;
				case 3:
					((RGG) o).doRun = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 2:
					return ((RGG) o).consider;
				case 3:
					return ((RGG) o).doRun;
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((RGG) o).initialTurtleState = (TurtleState) TurtleState.$TYPE.setObject (((RGG) o).initialTurtleState, value);
					return;
				case 1:
					((RGG) o).classesToIgnoreOrConsider = (Class[]) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((RGG) o).initialTurtleState;
				case 1:
					return ((RGG) o).classesToIgnoreOrConsider;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new RGG ());
		$TYPE.addManagedField (initialTurtleState$FIELD = new _Field ("initialTurtleState", _Field.PROTECTED  | _Field.SCO, TurtleState.$TYPE, null, 0));
		$TYPE.addManagedField (classesToIgnoreOrConsider$FIELD = new _Field ("classesToIgnoreOrConsider", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Class[].class), null, 1));
		$TYPE.addManagedField (consider$FIELD = new _Field ("consider", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 2));
		$TYPE.addManagedField (doRun$FIELD = new _Field ("doRun", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new RGG ();
	}

//enh:end

}
