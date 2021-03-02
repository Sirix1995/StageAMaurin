package de.grogra.animation;

import de.grogra.animation.util.Debug;
import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Workbench;
import de.grogra.vfs.FileSystem;

public class AnimCore implements TimeChangeListener {

	private GraphManager graph;
	private Workbench wb;
	private AnimManager animManager;
	
	public AnimCore(GraphManager graph, Workbench wb) {
		this.graph = graph;
		this.wb = wb;
		this.animManager = new AnimManager(graph, wb);
		animManager.getTimelineManager().addTimeChangeListener(this);
	}
	
	public GraphManager getGraph() {
		return this.graph;
	}
	
	public Workbench getWorkbench() {
		return this.wb;
	}
	
	public AnimManager getAnimManager() {
		return this.animManager;
	}
	
	public int getCurrentTime() {
		return animManager.getTimelineManager().getCurrentTime();
	}
	
	public void updateWorkbench(GraphManager graph, Workbench wb) {
		this.graph = graph;
		this.wb = wb;
	}

	public void timeChanged(int newTime, boolean fromJob) {
		animManager.updateAnimValues(newTime, fromJob);
	}
	
	public void saveAnimationData(FileSystem fs) {
		if (!animManager.saveAnimationData(fs)) {
			//TODO: print correct message, perhaps in a message box
			Debug.println("Couldn't save animation data.");
		}
	}
	
	public void restoreAnimationData(FileSystem fs) {
		if (!animManager.restoreAnimationData(fs)) {
			//TODO: print correct message, perhaps in a message box
			Debug.println("Couldn't restore animation data.");
		}
	}

}
