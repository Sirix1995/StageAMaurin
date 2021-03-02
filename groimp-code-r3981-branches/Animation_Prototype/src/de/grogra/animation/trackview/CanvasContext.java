package de.grogra.animation.trackview;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import de.grogra.animation.AnimManager;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.timeline.TimeContext;

public class CanvasContext {

	final public static BasicStroke axesStroke = new BasicStroke(2);
	final public static BasicStroke helperLinesStroke = new BasicStroke(1);
	
	public Graphics2D graphics;
	public BufferedImage image;
	
	/**
	 * Transformation from real coordinates (time, value) to canvas coordinates.
	 */
	final public AffineTransform transform;
	
	public AnimManager animManager;
	public TimeContext timeContext;
	
	public int rasterSizeX = 40;
	public int rasterSizeY = 40;
	
	public int width;
	public int height;
	
	public CanvasContext() {
		this.transform = new AffineTransform();
	}
	
}
