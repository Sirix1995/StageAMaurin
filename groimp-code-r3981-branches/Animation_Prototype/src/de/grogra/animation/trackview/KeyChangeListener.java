package de.grogra.animation.trackview;

import java.awt.geom.Point2D;

public interface KeyChangeListener {

	public void keyChanged(CanvasContext ctx, KeyObject keyObject, Point2D valuePoint);
	
}
