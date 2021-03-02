package de.grogra.animation.handler.sgnodereference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.grogra.animation.AnimJob;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.handler.PropertyHandler;
import de.grogra.animation.handler.animationnodes.AnimationNodesHandler;
import de.grogra.animation.handler.simpleproperty.SimplePropertyHandler;
import de.grogra.animation.util.Debug;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.GraphLoader;
import de.grogra.pf.io.GraphXMLSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.InputStreamSourceImpl;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.PluginCollector;
import de.grogra.pf.io.StreamAdapter;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Workbench;
import de.grogra.vfs.FileSystem;

public class SGNodeReferenceHandler extends Handler {

	final private Map<Integer,byte[]> graphMap;
	
	/**
	 * Only used to determine a suitable key.
	 * May not be consistent with real keys.
	 */
	final private Set<Integer> sortedKeys;
	
	/**
	 * Handler for property values.
	 */
	private PropertyHandler propertyHandler;
	
	/**
	 * Node to keep all not used graph nodes.
	 */
	private Node storageNode;
	private long storageId; 
	
	private int currentRestoredTime;
		
	public SGNodeReferenceHandler(GraphManager graph, Workbench wb) {
		super(graph, wb);
		this.propertyHandler = new SimplePropertyHandler(graph, wb);
//		this.propertyHandler = new AnimationNodesHandler(graph, wb);
		this.graphMap = new HashMap<Integer,byte[]>();
		this.sortedKeys = new TreeSet<Integer>();
		
		// put storage node at graph root
//		this.storageNode = new StorageNode();
//		Node rootNode = graph.getRoot();
//		Transaction t = graph.getActiveTransaction();
//		rootNode.addEdgeBitsTo(storageNode, StorageNode.ANIM_STORAGE_EDGE, t);
		
		this.currentRestoredTime = 0;
	}
	
	@Override
	public void addEdgeBits(int time, Object source, Object target, int mask) {
		saveSceneGraph(time);
	}

	@Override
	public void removeEdgeBits(int time, Object source, Object target, int mask) {
		saveSceneGraph(time);
		// TODO: put removed node to another place (add to special node at root or keep in memory)
	}
	
	private void saveSceneGraph(int time) {
		Collection plugins = new HashSet ();
		PluginCollector pc = new PluginCollector (plugins);
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();
			new StreamAdapter (new GraphXMLSource (graph, wb.getRegistry(), pc, true),
					new IOFlavor(GraphXMLSource.MIME_TYPE, IOFlavor.OUTPUT_STREAM, null))
				.write (stream);
			stream.flush ();
			graphMap.put(time, stream.toByteArray());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void clearValues() {
		graphMap.clear();
		propertyHandler.clearValues();
	}

	@Override
	public void getTimes(Object node, Set<Integer> times) {
		// every scene graph change is added as key even if node is not effected
		//TODO: gut so oder lieber nur keys zurueckgeben, wenn Knoten sich aendert
		//oder Kanten am Knoten
		times.addAll(graphMap.keySet());
		// add property changes of node
		propertyHandler.getTimes(node, times);
	}
	

	@Override
	public void getTimesForProperty(Object node, PersistenceField property,
			Set<Integer> times) {
		propertyHandler.getTimesForProperty(node, property, times);
	}

	@Override
	public boolean putValue(int time, Object node, PersistenceField property,
			Object value) {
		return propertyHandler.putValue(time, node, property, value);
	}

	@Override
	public void changeValue(int oldTime, int newTime, Object node,
			PersistenceField property, Object value) {
		propertyHandler.changeValue(oldTime, newTime, node, property, value);
	}

	@Override
	public Object getValue(double time, Object node, PersistenceField property) {
		return propertyHandler.getValue(time, node, property);
	}

	@Override
	public boolean restoreData(FileSystem fs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean saveData(FileSystem fs, int currentTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(int time) {
		
		// restore scene graph
		Integer key = getSuitableTime(time);
		if ((key != null) && (key != currentRestoredTime)) {
			Debug.println("Restored saved scene graph");
			final byte[] bytes = graphMap.get(key);
			Registry r = wb.getRegistry();
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			FilterSource f = new InputStreamSourceImpl(
					stream, "", GraphXMLSource.MIME_TYPE, r, null);
								
			f = IO.createPipeline (f, IOFlavor.GRAPH_LOADER);
			try {
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
			currentRestoredTime = key;
		}

		// TODO: restore only property values for visible nodes
		propertyHandler.update(time);
				
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
	public void makeTransient(final int time, final Object node, final ManageableType type) {
		Debug.println("Node: " + node + ", type: " + type);

		storageNode = (Node) node;
		storageId = storageNode.getId();
		
//		Transaction t = graph.getActiveTransaction();
		
//		if (node instanceof Node) {
//			Node n = (Node) node;
//			graph.makePersistent(n, t);
//					n.removeAll(t);
//					t.commitAll();
//					t = graph.getActiveTransaction();
//					storageNode.addEdgeBitsTo(n, StorageNode.ANIM_NODE_EDGE, t);
//			
//					storageNode.addEdgeBitsTo(n, GraphManager.BRANCH_EDGE, t);
//			graph.getRoot().addEdgeBitsTo(n, GraphManager.BRANCH_EDGE, t);
//
//		}

	}
	
	public void testRestoreNode() {
		
		new AnimJob(null, Workbench.current()) {

			@Override
			protected void runImpl(Object arg, Context ctx) {

				GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
				Transaction t = graph.getActiveTransaction();
				
				graph.makePersistent(storageNode, storageId, t);
				graph.getRoot().addEdgeBitsTo(storageNode, GraphManager.BRANCH_EDGE, t);
				
				t.commitAll();
				
			}
			
		}.execute();
		
	}

}
