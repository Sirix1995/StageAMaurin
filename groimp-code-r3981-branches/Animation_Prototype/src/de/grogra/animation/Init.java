package de.grogra.animation;

import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Workbench;
import de.grogra.vfs.FileSystem;

public class Init {

	public static String ANIMCORE = "AnimCore";
	
	public static void init(Workbench wb, FileSystem fs) {
		GraphManager graph = wb.getRegistry().getProjectGraph();
		AnimCore animCore = new AnimCore(graph, wb);
		wb.setProperty(ANIMCORE, animCore);
		((AnimCore) Workbench.current().getProperty(ANIMCORE)).restoreAnimationData(fs);
	}
	
	public static void save(FileSystem fs, String projectFile) {
		((AnimCore) Workbench.current().getProperty(ANIMCORE)).saveAnimationData(fs);
	}
	

}
