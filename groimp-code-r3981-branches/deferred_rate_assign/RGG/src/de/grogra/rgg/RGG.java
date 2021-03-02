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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import javax.swing.KeyStroke;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator;

import sun.misc.Cleaner;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Extent;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp.IMP;
import de.grogra.imp.IMPWorkbench;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.Shader;
import de.grogra.turtle.TurtleState;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;
import de.grogra.numeric.BasicODE;
import de.grogra.numeric.Euler;
import de.grogra.numeric.FirstOrderIntegratorAdapter;
import de.grogra.numeric.Midpoint;
import de.grogra.numeric.MonitorFunction;
import de.grogra.numeric.ODE;
import de.grogra.numeric.RK4;
import de.grogra.numeric.Solver;
import de.grogra.persistence.PersistenceField;
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
import de.grogra.rgg.model.PropertyRuntime;
import de.grogra.rgg.model.RGGGraph;
import de.grogra.rgg.model.Runtime;
import de.grogra.rgg.model.PropertyRuntime.GraphProperty;
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
import de.grogra.xl.ode.RateAssignment;
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
 * of its souce code and after loading a project containing an RGG.
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
public class RGG extends Node implements Selectable, RegistryContext, ODE
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
	private transient ObjectList items;

	protected TurtleState initialTurtleState = new TurtleState ();
	//enh:field type=TurtleState.$TYPE

	private static final Class[] CLASS_0 = {};

	private transient ConcurrentTasks tasks = new ConcurrentTasks ();

	public static final UIProperty STEPRUN = UIProperty.getOrCreate ("/workbench/rgg/toolbar/steprun", UIProperty.WORKBENCH);
	
	public static final EnumerationType GROUP_TYPE = new EnumerationType (
			"group_type", RGG.I18N, 2);
	
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
		
		public Apply (Method method, boolean loop, boolean useRunCheckBox)
		{
			super (RGG.this.getGraph (), true, JobManager.ACTION_FLAGS);
			this.method = method;
			this.loop = loop;
			this.useRunCheckBox = useRunCheckBox;
		}

		@Override
		protected void runImpl (Object info, Context ctx)
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
			boolean modified = applyRules (w.getRegistry ().getProjectGraph ().getActiveTransaction ());
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
	}

	public RGG ()
	{
		super ();
		if (getNType () != null)
		{
			registry = Registry.current ();
		}
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

	private static boolean isTransformationMethod (Method m)
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
			protected void runImpl (Object info, Context ctx)
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
	 * graph by a branch edge, then all edges
	 * starting at this instance are removed.
	 * Otherwise, a new instance of <code>RGGRoot</code>
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
	///// ODE                                                      /////
	////////////////////////////////////////////////////////////////////
	
	private static class ClsEntry
	{
		Class cls;
		ClsEntry parent;
		ClsEntry child;
		ClsEntry next;
		int size;
		final ArrayList<GraphProperty> props = new ArrayList<GraphProperty>();
	}
	
	private HashMap<Class, RateEntry> calculateOffsets(List<RateAssignment> list)
	{
		final HashMap<Class, ClsEntry> map = new HashMap<Class, ClsEntry>();
		
		// create dummy for root of tree
		ClsEntry root = new ClsEntry();
		root.cls = Object.class;
		map.put(Object.class, root);
		
		// enter rate assignments into map
		// remove duplicates while doing that
		for (RateAssignment ra : list)
		{
			// obtain property and reset offset
			PropertyRuntime.GraphProperty prop = (PropertyRuntime.GraphProperty) ra.property;
			prop.offset = Integer.MIN_VALUE;
			// obtain entry or create new one
			ClsEntry entry = map.get(ra.cls);
			if (entry == null)
			{
				entry = new ClsEntry();
				entry.cls = ra.cls;
				map.put(ra.cls, entry);
			}
			// add property to entry if not done yet
			if (!entry.props.contains(prop))
			{
				entry.props.add(prop);
			}
		}

		// create tree dependency structure
		for (ClsEntry entry : map.values())
		{
			// skip the root entry
			if (entry != root)
			{
				// search parent and connect to tree
				Class cls = entry.cls.getSuperclass();
				ClsEntry e = map.get(cls);
				while (e == null)
				{
					cls = cls.getSuperclass();
					e = map.get(cls);
				}
				entry.parent = e;
				entry.next = e.child;
				e.child = entry;
			}
		}
		
		final HashMap<PersistenceField, GraphProperty> lookup = new HashMap<PersistenceField, GraphProperty>();

		// walk tree to calculate offsets
		ClsEntry entry = root.child;
		while (entry != null)
		{
			// assign property offsets and 
			// enter new properties into lookup table
			entry.size = entry.parent.size;
			for (GraphProperty prop : entry.props)
			{
				PersistenceField field = prop.getField();
				GraphProperty p = lookup.get(field);
				if (p != null) {
					prop.offset = p.offset;
				} else {
					lookup.put(field, prop);
					prop.offset = entry.size++;
				}
			}

			// search next node to handle
			if (entry.child != null)
			{
				// go one level down
				entry = entry.child;
			} else {
				// remove properties from lookup table
				for (Iterator<GraphProperty> it = entry.props.iterator(); it.hasNext(); )
				{
					GraphProperty prop = it.next();
					PersistenceField field = prop.getField();
					GraphProperty p = lookup.get(field);
					if (p == prop) {
						lookup.remove(field);
					} else {
						it.remove();
					}
				}
				// search next node, climbing up if necessary
				while (entry != root && entry.next == null)
				{
					entry = entry.parent;
					// remove properties from lookup table
					for (Iterator<GraphProperty> it = entry.props.iterator(); it.hasNext(); )
					{
						GraphProperty prop = it.next();
						PersistenceField field = prop.getField();
						GraphProperty p = lookup.get(field);
						if (p == prop) {
							lookup.remove(field);
						} else {
							it.remove();
						}
					}
				}
				entry = entry.next;
			}
		}
		
		final HashMap<Class, RateEntry> result = new HashMap<Class, RateEntry>();
		
		// extract result
		for (ClsEntry e : map.values())
		{
			// extract only those that provide additional properties
			if (e.props.size() > 0)
			{
				RateEntry re = new RateEntry();
				re.cls = e.cls;
				re.m = e.size;
				re.props = e.props.toArray(new GraphProperty[e.props.size()]);
				result.put(re.cls, re);
			}
		}
		
		return result;
	}
	
	double[] rate;
	double[] state;
	
	static class RateEntry {
		Class cls;
		int m;		// #props for this type and its supertypes
		GraphProperty[] props;
	}
	
	HashMap<Class, RateEntry> rateTable;
	
	private void copyStateToGraph(double[] state)
	{
		GraphManager graph = Library.graph();
		// copy state from y to graph
		final LinkedList<Extent> extents = new LinkedList<Extent>();
		for (RateEntry re : rateTable.values())
		{
			Extent extent = graph.getExtent(re.cls);
			while (extent != null) {
				// loop over all nodes of this extent to copy the value
				for (int i = 0; i < Node.LAST_EXTENT_INDEX; i++) {
					Node node = extent.getFirstNode(i);
					while (node != null) {
						long id = node.getId();
						int index = graph.baseMap.get(id);

						for (GraphProperty p : re.props)
						{
							double value = state[index + p.offset];
							p.setDouble(node, null, value);
						}
						
						node = extent.getNextNode(node);
					}
				}
				
				// loop over subextents
				if (extent.totalSize() > extent.size())
				{
					extent.getSubExtents(extents);
				}
				extent = extents.poll();
			}
		}
	}
	
//	Solver solver = new FirstOrderIntegratorAdapter(new GraggBulirschStoerIntegrator(0, 1, 1e-8, 1e-8));
//	Solver solver = new FirstOrderIntegratorAdapter(new EulerIntegrator(0.1));
	Solver solver = new FirstOrderIntegratorAdapter(new DormandPrince853Integrator(0, 1, 1e-8, 1e-8));
	
	public Solver getSolver() {
		return solver;
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public void setSolver(FirstOrderIntegrator integrator) {
		this.solver = new FirstOrderIntegratorAdapter(integrator);
	}
	
	protected void monitor(final VoidToDouble g) {
		solver.addMonitorFunction(new MonitorFunction() {
			public double g(double t, double[] y) {
				copyStateToGraph(y);
				return g.evaluateDouble();
			}
		});
	}

	protected void integrate() {
//		integrate(Double.POSITIVE_INFINITY);
		integrate(Double.MAX_VALUE);
	}
	
	protected void integrate(double step) {
		GraphManager graph = Library.graph();
		graph.baseMap.clear();
		
		// obtain list of rate assignments and build rate table
		if (rateTable == null) {
			List<RateAssignment> list = BasicODE.getRateAssignments(Library.workbench().getRegistry());
			rateTable = calculateOffsets(list);
		}
		
		// calculate the size of the state vector
		int size = 0;
		for (RateEntry re : rateTable.values())
		{
			Extent extent = graph.getExtent(re.cls);
			int totalSize = extent.totalSize();
			size += totalSize * re.props.length;
		}
		
		// allocate rate and state vector
		if (state == null || state.length != size) {
			rate = new double[size];
			state = new double[size];
		}
		
		// copy state from graph to state vector
		// and build up hash table for fast lookup of the index
		int base = 0;
		final LinkedList<Extent> extents = new LinkedList<Extent>();
		for (RateEntry re : rateTable.values())
		{
			Extent extent = graph.getExtent(re.cls);
			while (extent != null) {
				// loop over all nodes of this extent to assign
				// an index into the state vector and copy the value
				for (int i = 0; i < Node.LAST_EXTENT_INDEX; i++) {
					Node node = extent.getFirstNode(i);
					while (node != null) {
						long id = node.getId();
						// check if node has already memory assigned in state vector
						if (!graph.baseMap.containsKey(id))
						{
							// TODO move search up into extent loop, since type is known already there
							
							// allocate space now
							Class cls = node.getClass();
							RateEntry re2 = rateTable.get(cls);
							while (re2 == null)
							{
								cls = cls.getSuperclass();
								re2 = rateTable.get(cls);
							}
							graph.baseMap.put(id, base);
							base += re2.m;
						}
						
						int index = graph.baseMap.get(id);

						for (GraphProperty p : re.props)
						{
							double value = p.getDouble(node, null);
							state[index + p.offset] = value;
						}
						
						node = extent.getNextNode(node);
					}
				}
				
				// loop over subextents
				if (extent.totalSize() > extent.size())
				{
					extent.getSubExtents(extents);
				}
				extent = extents.poll();
			}
		}
		
		assert base == size;

		// call the ODE solver
		solver.init(this);
		solver.step(step);
		
//		final Solver solver = new RK4();
//		final Solver solver = new Midpoint();
//		final Solver solver = new Euler();
//		solver.init(this);
//		final float h = 0.1f;
//		while (step > h) {
//			solver.step(h);
//			step -= h;
//		}
//		solver.step(step);
		
		// copy state from state vector to graph
		for (RateEntry re : rateTable.values())
		{
			Extent extent = graph.getExtent(re.cls);
			while (extent != null) {
				// loop over all nodes of this extent to copy the value
				for (int i = 0; i < Node.LAST_EXTENT_INDEX; i++) {
					Node node = extent.getFirstNode(i);
					while (node != null) {
						long id = node.getId();
						int index = graph.baseMap.get(id);

						for (GraphProperty p : re.props)
						{
							double value = state[index + p.offset];
							p.setDouble(node, null, value);
						}
						
						node = extent.getNextNode(node);
					}
				}
				
				// loop over subextents, skip if they are empty
				if (extent.totalSize() > extent.size())
				{
					extent.getSubExtents(extents);
				}
				extent = extents.poll();
			}
		}

	}
	
	public double getInitialTime() {
		return 0;
	}
	
	public double[] getInitialState() {
		return state;
	}
	
	public void getRate(double[] out, double t, double[] state) {
//		System.err.println("getRate() was called with t = " + t);
		GraphManager graph = Library.graph();

		// prepare rate array
		Arrays.fill(out, 0);
		graph.rate = out;

		// copy state from state vector to graph
		LinkedList<Extent> extents = new LinkedList<Extent>();
		for (RateEntry re : rateTable.values())
		{
			Extent extent = graph.getExtent(re.cls);
			while (extent != null) {
				// loop over all nodes of this extent to copy the value
				for (int i = 0; i < Node.LAST_EXTENT_INDEX; i++) {
					Node node = extent.getFirstNode(i);
					while (node != null) {
						long id = node.getId();
						int index = graph.baseMap.get(id);

						for (GraphProperty p : re.props)
						{
							double value = state[index + p.offset];
							p.setDouble(node, null, value);
						}
						
						node = extent.getNextNode(node);
					}
				}
				
				// loop over subextents
				if (extent.totalSize() > extent.size())
				{
					extent.getSubExtents(extents);
				}
				extent = extents.poll();
			}
		}
		
		// call the actual rate function
		getRate();
	}
	
	protected void getRate()
	{
	}

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
		protected void runImpl (Object a, Context ctx)
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
