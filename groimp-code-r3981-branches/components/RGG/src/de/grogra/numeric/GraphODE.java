package de.grogra.numeric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;

import de.grogra.graph.impl.Extent;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.Field;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.rgg.Library;
import de.grogra.rgg.model.PropertyRuntime;
import de.grogra.rgg.model.PropertyRuntime.GraphProperty;
import de.grogra.xl.lang.VoidToDouble;
import de.grogra.xl.ode.RateAssignment;

/**
 * This abstract class represents an ODE on a graph.
 * The state will be automatically copied into the graph prior
 * evaluation of the rate function. The user must implement the
 * abstract function {@link #getRate()} to evaluate the rates, and can
 * make use of graph queries and rules. The class {@link de.grogra.rgg.RGG}
 * internally implements this class and provides helper functions that
 * forward to the same functions in this class.
 * 
 * @author Reinhard Hemmerling
 *
 */
public abstract class GraphODE implements ODE {

	HashMap<Class, RateEntry> rateTable;
	
//	Solver solver = new FirstOrderIntegratorAdapter(new GraggBulirschStoerIntegrator(0, 1, 1e-8, 1e-8));
//	Solver solver = new FirstOrderIntegratorAdapter(new GraggBulirschStoerIntegrator(0, 1, 1e-4, 1e-4));
//	Solver solver = new FirstOrderIntegratorAdapter(new EulerIntegrator(0.1));
//	Solver solver = new FirstOrderIntegratorAdapter(new ClassicalRungeKuttaIntegrator(0.01));
//	Solver solver = new FirstOrderIntegratorAdapter(new DormandPrince853Integrator(0, 1, 1e-8, 1e-8));
//	Solver solver = new FirstOrderIntegratorAdapter(new DormandPrince853Integrator(0, 1, 1e-4, 1e-4));
	Solver solver = new FirstOrderIntegratorAdapter(new DormandPrince54Integrator(0, 1, 1e-4, 1e-4));
	
//	double[] rate;
	double[] state;
	
	// value-specific allowed tolerances
	double[] absTol;
	double[] relTol;
	
	// indicates if any tolerance value in the repsective tolerance arrays was set
	boolean usesAbsTol;
	boolean usesRelTol;

	final ArrayList<MonitorEntry> monitors = new ArrayList<MonitorEntry>();


	/**
	 * Use graph queries to evaluate rate function.
	 */
	public abstract void getRate();

	// copy state to graph, then call getRate()
	public final void getRate(double[] out, double t, double[] state) {
		GraphManager graph = Library.graph();

		// prepare rate array
		Arrays.fill(out, 0);
		graph.rate = out;

		// copy state into graph
		copyStateToGraph(state, false);
		
		// call the actual rate function
		getRate();
	}

	/**
	 * Integrate over a time interval.
	 * The method takes care of memory allocation for the node attributes
	 * in the graph and copying from graph to state vector prior integration, and
	 * back from state vector to graph afterwards.
	 * @param duration time interval to integrate over
	 * @throws NumericException
	 */
	public void integrate(double duration) throws NumericException
	{
		GraphManager graph = Library.graph();
		graph.baseMap.clear();
		
		// obtain list of rate assignments and build rate table
		if (rateTable == null) {
			List<RateAssignment> list = GraphODE.getRateAssignments(Library.workbench().getRegistry());
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
			// prevent empty arrays, otherwise stepsize control does not work for 
			// some solvers like DormandPrince853Integrator or GraggBulirschStoerIntegrator
//			rate = new double[size > 0 ? size : 1];
			state = new double[size > 0 ? size : 1];
			// also allocate tolerance vectors of same size
			absTol = new double[size > 0 ? size : 1];
			relTol = new double[size > 0 ? size : 1];
		}
		
		// initialize tolerance arrays
		Arrays.fill(absTol, 0);
		Arrays.fill(relTol, 0);
		usesAbsTol = false;
		usesRelTol = false;
		
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
				for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++) {
					Node node = extent.getFirstNode(i);
					while (node != null) {
						long id = node.getId();
						// check if node has already memory assigned in state vector
						if (!graph.baseMap.containsKey(id))
						{
							// TODO move search up into extent loop, since type is known already there
							
							// if not, allocate space now
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
						
						// obtain index into state vector for node
						int index = graph.baseMap.get(id);

						// copy all (integrating) node properties into state vector
						for (GraphProperty p : re.props)
						{
							double value = p.getDouble(node, null);
							state[index + p.offset] = value;
							
							// retrieve tolerances set by @Tolerance annotation
							PersistenceField pf = p.getField();
							Field f = Reflection.getDeclaredField(pf.getDeclaringType (), pf.getDescriptor());
							if (f != null) {
								Annotation<Tolerance> a = Reflection.getDeclaredAnnotation(f, Tolerance.class);
								if (a != null) {
									// obtain tolerances
									double abs = (Double)a.value("absolute");
									double rel = (Double)a.value("relative");
									// store them at matching index
									absTol[index + p.offset] = abs;
									relTol[index + p.offset] = rel;
									// update uses flags
									usesAbsTol |= abs != 0;
									usesRelTol |= rel != 0;
								}
							}
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

		// set tolerance arrays if any was used
		solver.setTolerances(usesAbsTol ? absTol : null, usesRelTol ? relTol : null);
		
		// set monitor functions
		Monitor monitor = new Monitor() {
			@Override
			public void g(double[] out, double t, double[] y) {
				copyStateToGraph(y, false);
				for (int i = 0; i < monitors.size(); i++) {
					out[i] = monitors.get(i).g(t, y);
				}
			}
			@Override
			public boolean handleEvent(int i, double t, double[] y) {
				copyStateToGraph(y, false);
				return monitors.get(i).handleEvent(t, y);
			}
		};
		solver.setMonitor(monitors.size(), monitor);
		
		// call the ODE solver
		solver.integrate(this, 0, state, duration, state);
		
		// copy state from state vector to graph
		copyStateToGraph(state, true);
	}

	/**
	 * Get the currently set solver for numerical integration.
	 * @return
	 */
	public Solver getSolver() {
		return solver;
	}

	/**
	 * Set a new solver to use for numerical integration.
	 * @param solver
	 */
	public void setSolver(Solver solver) {
		this.solver = solver;
	}

	/**
	 * Install a monitor function that generates an event when the
	 * returned value changes its sign. The monitor must not modify
	 * the state of the graph. Integration will stop when the event
	 * triggers.
	 * @param g monitor function
	 */
	public void monitor(final VoidToDouble g) {
		monitor(g, null);
	}
	
	/**
	 * Install a monitor function that generates an event when the
	 * returned value changes its sign. The monitor must not modify
	 * the state of the graph. When the event triggers, the event
	 * handler is called. Integration will stop when the event
	 * triggers. The event handler may modify the graph.
	 * @param g monitor function
	 * @param r event handler
	 */
	public void monitor(final VoidToDouble g, final Runnable r) {
		assert g != null;
		monitors.add(new MonitorEntry() {
			@Override
			public boolean handleEvent(double t, double[] y) {
				if (r != null) {
					r.run();
				}
				return true;
			}
			@Override
			public double g(double t, double[] y) {
				return g.evaluateDouble();
			}
		});
	}
	
	/**
	 * Trigger an event in regular intervals and call the event handler.
	 * The event handler must not modify the graph.
	 * @param period interval between events
	 * @param r event handler
	 */
	public void monitorPeriodic(final double period, final Runnable r) {
		assert r != null;
		monitors.add(new MonitorEntry() {
			@Override
			public boolean handleEvent(double t, double[] y) {
				r.run();
				return false;
			}
			@Override
			public double g(double t, double[] y) {
				return Math.sin(Math.PI * t / period);
			}
		});
	}
	
	/**
	 * Remove all monitors.
	 */
	public void unmonitor() {
		monitors.clear();
	}

	

	/**
	 * Use the list of rate assignments to calculate offsets for all
	 * attributes of nodes. Also a list of rate entries is created
	 * that is used to compute the length of state vector and allocate
	 * memory for the nodes.
	 * @param list
	 * @return
	 */
	HashMap<Class, RateEntry> calculateOffsets(List<RateAssignment> list)
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
			prop.offset = -1;
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

	/**
	 * Copy the state into the graph. It uses the {@link #rateTable}, that
	 * must have been computed previously by {@link #calculateOffsets(List)}.
	 * Transactions should be used for the state to become visible globally
	 * (for instance, when integration is done), but not during rate calculation.
	 * @param useTransaction
	 */
	void copyStateToGraph(double[] state, boolean useTransaction)
	{
		GraphManager graph = Library.graph();
		// copy state from y to graph
		final LinkedList<Extent> extents = new LinkedList<Extent>();
		for (RateEntry re : rateTable.values())
		{
			Extent extent = graph.getExtent(re.cls);
			while (extent != null) {
				// loop over all nodes of this extent to copy the value
				for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++) {
					Node node = extent.getFirstNode(i);
					while (node != null) {
						long id = node.getId();
						int index = graph.baseMap.get(id);

						for (GraphProperty p : re.props)
						{
							double value = state[index + p.offset];
							if (useTransaction) {
								p.setDouble(node, null, value);
							} else {
								p.getField().setDouble(node, null, value, null);
							}
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

	/**
	 * Query the registry for all classes belonging to the current project and
	 * extract the rate assignments from all $ODEHelper classes.
	 * @param r
	 * @return
	 */
	public static List<RateAssignment> getRateAssignments(Registry r)
	{
		List<RateAssignment> list = new ArrayList<RateAssignment>();
		Item dir = r.getItem("/classes");
		for (Item c = (Item) dir.getBranch(); c != null; c = (Item) c.getSuccessor())
		{
			if (c.getName().endsWith("$ODEHelper"))
			{
				try {
					RateAssignment[] ra = (RateAssignment[]) Reflection.get(
							null, Reflection.getDeclaredField((Type) ((TypeItem) c).getObject(), "TABLE"));
					list.addAll(Arrays.asList(ra));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
