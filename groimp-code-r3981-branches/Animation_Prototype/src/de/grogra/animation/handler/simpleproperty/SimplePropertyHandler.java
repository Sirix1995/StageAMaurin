package de.grogra.animation.handler.simpleproperty;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Set;

import de.grogra.animation.handler.PropertyHandler;
import de.grogra.graph.impl.GraphManager;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ObjectSourceImpl;
import de.grogra.pf.io.StreamAdapter;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;

public class SimplePropertyHandler extends PropertyHandler {

	private AnimNodeMap nodeMap;	
	
	public SimplePropertyHandler(GraphManager graph, Workbench wb) {
		super(graph, wb);
		this.nodeMap = new AnimNodeMap();
	}
	
	@Override
	public boolean putValue(int time, Object node, PersistenceField property, Object value) {	
		return nodeMap.putValue(time, node, property, value);
	}
	
	@Override
	public void changeValue(int oldTime, int newTime, Object node,
			PersistenceField property, Object value) {
		nodeMap.changeValue(oldTime, newTime, node, property, value);
	}
	
	@Override
	public Object getValue(double time, Object node, PersistenceField property) {
		return nodeMap.getValue(time, node, property);
	}
	
	@Override
	public void getTimes(Object node, Set<Integer> times) {
		nodeMap.getTimes(node, times);
	}
	

	@Override
	public void getTimesForProperty(Object node, PersistenceField property,
			Set<Integer> times) {
		nodeMap.getTimesForProperty(node, property, times);
	}
	
	@Override
	public void update(int time) {
		// update all object values to current time
		Transaction t = wb.getRegistry().getProjectGraph().getActiveTransaction();
		nodeMap.update(time, t);
		t.commitAll();
	}
	
	@Override
	public String toString() {
		return nodeMap.toString();
	}

	@Override
	public void clearValues() {
		nodeMap.clearValues();
	}
	
	@Override
	public boolean saveData(FileSystem fs, int currentTime) {
		// TODO: save content of all hashmaps to a file

		// speichern ueber Java-Mechanismus
		// dazu muss alles von Serializable implementieren
//		OutputStream s = null; 
//		try 
//		{
//			Object fsRoot = fs.getRoot();
//			Object animDir = fs.create(fsRoot, "animation", true);
//			// TODO: abfangen, wenn schon verzeichnis existiert, datein darin loeschen
//			Object f = fs.create (animDir, "nodeMap.ser", false);
//			s = fs.getOutputStream (f, false);
//
//			ObjectOutputStream o = new ObjectOutputStream( s ); 
//			o.writeObject(nodeMap); 
//
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		} 
//		finally {
//			try {
//				s.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				return false;
//			}
//		}
		
		//TODO: speichern ueber GroIMP-Mechanismus
		// dazu muss alles von  ShareableBase ableiten
		OutputStream s = null; 

		Object fsRoot = fs.getRoot();
		Object animDir;
		try {
			animDir = fs.create(fsRoot, "animation", true);
	
			// TODO: abfangen, wenn schon verzeichnis existiert, datein darin loeschen
			Object f = fs.create (animDir, "nodeMap.ser", false);
			s = fs.getOutputStream (f, false);
	
			MimeType mt = new MimeType(MimeType.JAVA_OBJECT, null, AnimNodeMap.class);
			
			FilterSource filterSource = new ObjectSourceImpl(
					nodeMap, "nodeMap.ser", mt, wb.getRegistry(), null);
			
			new StreamAdapter(filterSource, new IOFlavor
								   (mt, IOFlavor.OUTPUT_STREAM, null))
				.write(s);
			s.flush ();
		
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (s != null)
			{
				try {
					s.close ();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}

	@Override
	public boolean restoreData(FileSystem fs) {
		// TODO Auto-generated method stub
		return false;
	}


}
