package de.grogra.ray.event;

public interface RTProgressListener {

	public final static int RENDERING_PREPROCESSING  = 0;
	public final static int RENDERING_PROCESSING     = 1;
	public final static int RENDERING_POSTPROCESSING = 2;
	public final static int LIGHTNING_PREPROCESSING  = 3;
	public final static int LIGHTNING_PROCESSING     = 4;
	public final static int LIGHTNING_POSTPROCESSING = 5;
	
	public void progressChanged(int type, double progress, String text, 
			int x, int y, int width, int height);
	
}
