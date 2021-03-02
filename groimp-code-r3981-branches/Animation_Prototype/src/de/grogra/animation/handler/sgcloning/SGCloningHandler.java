package de.grogra.animation.handler.sgcloning;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.grogra.animation.AnimJob;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.util.Debug;
import de.grogra.graph.EdgePatternImpl;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.XMLGraphReader;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.GraphLoader;
import de.grogra.pf.io.GraphXMLSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.PluginCollector;
import de.grogra.pf.io.SAXSource;
import de.grogra.pf.io.StreamAdapter;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.IOWrapException;
import de.grogra.vfs.FileSystem;

public class SGCloningHandler extends Handler {

	final private Map<Integer, Node> graphMap;
	
	/**
	 * Only used to determine a suitable key.
	 * May not be consistent with real keys.
	 */
	final private Set<Integer> sortedKeys; 
	
	public SGCloningHandler(GraphManager graph, Workbench wb) {
		super(graph, wb);
		graphMap = new HashMap<Integer, Node>();
		sortedKeys = new TreeSet<Integer>();
	}
	
	@Override
	public void getTimes(Object node, Set<Integer> times) {
		times.addAll(graphMap.keySet());
	}

	@Override
	public boolean putValue(int time, Object node, PersistenceField property, Object value) {
		boolean result = graphMap.containsKey(time);
		
		Node root = graph.getRoot();
		Node clonedRoot = null;
		try {
			clonedRoot = root.cloneGraph(EdgePatternImpl.FORWARD, true);
		} catch (CloneNotSupportedException e) {e.printStackTrace();}
		graphMap.put(time, clonedRoot);
	
		return result;
	}

	@Override
	public void update(int time) {
		Debug.println("SGCloningHandler: update");
		Integer key = getSuitableTime(time);
		
		if (key != null) {
			Node root = graphMap.get(key);
			Node clonedRoot = null;
			try {
				// clone all edge types
				clonedRoot = root.cloneGraph(EdgePatternImpl.FORWARD, true);
			} catch (CloneNotSupportedException e) {e.printStackTrace();}
			// instead of graph.setRoot(), transfer all edges vom root node
//			graph.setRoot(GraphManager.MAIN_GRAPH, clonedRoot);
			
			Transaction t = graph.getActiveTransaction();
			Node rootNode = graph.getRoot();
			rootNode.removeAll(t);
			for (Edge e = clonedRoot.getFirstEdge(); e != null; e = e.getNext(clonedRoot)) {
				// create a new edge to target similar to the existing edge
				rootNode.addEdgeBitsTo(e.getTarget(), e.getEdgeBits(), t);
				// remove existing edge
				e.remove(t);
			}
			t.commitAll();
		}
	}
	
	private Integer getSuitableTime(int time) {
		if (graphMap.containsKey(time))
			return time;
			
		sortedKeys.clear();
		sortedKeys.addAll(graphMap.keySet());
		Iterator<Integer> it = sortedKeys.iterator();
		
		Integer key = null;
		Integer previous = null;
		
		while (it.hasNext()) {
			key = it.next();
			if (key > time) {
				key = previous != null ? previous : key;
				break;
			}
			previous = key;
		}
		
		return key;
	}
	
	@Override
	public void addEdgeBits(int time, Object source, Object target, int mask) {
		putValue(time, null, null, null);
	}

	@Override
	public void removeEdgeBits(int time, Object source, Object target, int mask) {
		putValue(time, null, null, null);
	}

	@Override
	public void clearValues() {
		graphMap.clear();
	}
	
	@Override
	public boolean saveData(final FileSystem fs, final int currentTime) {
		// write every cloned sg to a file
		AnimJob job = new AnimJob(null, wb) {

			@Override
			protected void runImpl(Object arg, Context ctx) {
				Set<Integer> keys = graphMap.keySet();

				for (Integer key : keys) {
					// set time
					update(key);
					
					// write graph file
					Collection plugins = new HashSet();
					PluginCollector pc = new PluginCollector (plugins);
					try {
						OutputStream s = null;
						try {
							Object fsRoot = fs.getRoot();
							Object animDir = fs.create(fsRoot, "animation", true);
							// TODO: abfangen, wenn schon verzeichnis existiert, datein darin loeschen
							Object f = fs.create (animDir, "graph_" + key + ".xml", false);
							s = fs.getOutputStream (f, false);
							new StreamAdapter (new GraphXMLSource (graph, wb.getRegistry(), pc),
									new IOFlavor(GraphXMLSource.MIME_TYPE, IOFlavor.OUTPUT_STREAM, null))
								.write (s);
							s.flush ();
							fs.getAttributes (f, true).put
								(java.util.jar.Attributes.Name.CONTENT_TYPE,
										GraphXMLSource.MIME_TYPE.toString ());
						}
						finally {
							if (s != null) {
								s.close ();
							}
						}						
					} catch (IOException e) {e.printStackTrace();}
				}
				
				// restore current time
				update(currentTime);
			}
			
		};
		//job.execute();
		wb.getJobManager().execute(job, null, wb, JobManager.ACTION_FLAGS);
		
		return true;
	}

	@Override
	public boolean restoreData(final FileSystem fs) {
		if (fs == null)
			return false;

		final Object animDir = fs.getFile("animation");
		if (animDir == null)
			// auch davon abhaengig machen, ob korrekter Handler genutzt wird
			// ansonsten bedeutet es einfach, dass es eine Szene ohne Animation ist
			return true;
		
		AnimJob job = new AnimJob(null, wb) {

			@Override
			protected void runImpl(Object arg, Context ctx) {
		
				int graphCount = fs.getChildCount(animDir);
				for (int i = 0; i < graphCount; i++) {
					Object child = fs.getChild(animDir, i);
					
					Registry r = wb.getRegistry();
					FilterSource f = new FileSource (fs, child, r, null);
					f = IO.createPipeline (f, IOFlavor.GRAPH_LOADER);
					try {
						//TODO: geht nicht, da schon Dinge im graph existieren
						((GraphLoader) ((ObjectSource) f).getObject ()).loadGraph (r);
					} catch (IOException e) {e.printStackTrace();}
					r.forAll (null, null, new ItemVisitor () {
							public void visit (Item item, Object info) {
								if (!item.validate ()) {
									System.err.println ("Removed " + item);
									item.removeFromChain ();
								}
							}
						}, null, false);
				}
				
			}
			
		};
		//job.execute();
		wb.getJobManager().execute(job, null, wb, JobManager.ACTION_FLAGS);
		
		return true;
	}

	@Override
	public Object getValue(double time, Object node, PersistenceField property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeValue(int oldTime, int newTime, Object node,
			PersistenceField property, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getTimesForProperty(Object node, PersistenceField property,
			Set<Integer> times) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeTransient(int time, Object node, ManageableType type) {
		// TODO Auto-generated method stub
		
	}

}
