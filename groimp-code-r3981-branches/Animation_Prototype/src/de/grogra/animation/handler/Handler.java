package de.grogra.animation.handler;

import java.util.Set;

import de.grogra.graph.impl.GraphManager;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.ui.Workbench;
import de.grogra.vfs.FileSystem;

public abstract class Handler {

	protected GraphManager graph;
	protected Workbench wb;
	
	public Handler(GraphManager graph, Workbench wb) {
		this.graph = graph;
		this.wb = wb;
	}
	
	/**
	 * Saves a value of a property of a node to a give time.
	 * Returns true, if there was already a value.
	 * @param time
	 * @param node
	 * @param property
	 * @param value
	 * @return
	 */
	public abstract boolean putValue(int time, Object node, PersistenceField property, Object value);

	/**
	 * Modifies a value of a property of a node. The time of the key can be changed too.
	 * @param oldTime
	 * @param newTime
	 * @param node
	 * @param property
	 * @param value
	 */
	public abstract void changeValue(int oldTime, int newTime, Object node, PersistenceField property, Object value);
	
	/**
	 * Returns the value of a property of a node to a given time.
	 * Can be null if no value exists.
	 * @param time
	 * @param node
	 * @param property
	 * @return
	 */
	public abstract Object getValue(double time, Object node, PersistenceField property);
	
	/**
	 * Returns all keyframes of all properties of a node. 
	 * @param node
	 * @param times
	 */
	public abstract void getTimes(Object node, Set<Integer> times);
	
	/**
	 * Returns all keyframes for the property of a node.
	 * @param node
	 * @param property
	 * @param times
	 */
	public abstract void getTimesForProperty(Object node, PersistenceField property, Set<Integer> times);
	
	/**
	 * Update scene graph to given point int time.
	 * It is guaranteed that this method is always called in a GroIMP job.
	 * @param time
	 */
	public abstract void update(int time);
	
	/**
	 * Adds an edge to a node with an edge mask.
	 * @param time
	 * @param source
	 * @param target
	 * @param mask
	 */
	public abstract void addEdgeBits(int time, Object source, Object target, int mask);

	/**
	 * Removes an edge to a node with an edge mask.
	 * @param time
	 * @param source
	 * @param target
	 * @param mask
	 */
	public abstract void removeEdgeBits(int time, Object source, Object target, int mask);
	
	public abstract void makeTransient(int time, Object node, ManageableType type);
	
	/**
	 * Removes all saved animation values.
	 */
	public abstract void clearValues();
	
	/**
	 * Every handler knows by itselfs wheter and how to save the animation data.
	 * @param fs
	 * @param currentTime
	 * @return
	 */
	public abstract boolean saveData(FileSystem fs, int currentTime);
	
	/**
	 * Called after project loaded, so the handler can restore the animaton data by itselfs.
	 * @param fs
	 * @return
	 */
	public abstract boolean restoreData(FileSystem fs);

}
