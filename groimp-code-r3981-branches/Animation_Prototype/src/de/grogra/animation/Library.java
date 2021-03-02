package de.grogra.animation;

import de.grogra.pf.ui.Workbench;

public class Library {

	public static void timestep() {
		AnimCore animCore = (AnimCore) Workbench.current().getProperty(Init.ANIMCORE);
		animCore.getAnimManager().getTimelineManager().increaseCurrentTime();
	}

	
	public static void timestep(int difference) {
		AnimCore animCore = (AnimCore) Workbench.current().getProperty(Init.ANIMCORE);
		animCore.getAnimManager().getTimelineManager().increaseCurrentTime(difference);
	}
	
	
}
