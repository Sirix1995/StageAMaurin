package de.grogra.animation.trackview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class CoordinatesObject implements CanvasObject {

	public void draw(CanvasContext ctx) {
		Graphics2D g = ctx.graphics;
		AffineTransform transform = ctx.transform;
		
		Point2D zero = new Point2D.Double(0, 0);
		transform.transform(zero, zero);
		
		// helper lines
		g.setColor(Color.DARK_GRAY);
		g.setStroke(CanvasContext.helperLinesStroke);
	
		for (double i = zero.getY(); i < 0; i += ctx.rasterSizeY * transform.getScaleY()) {
			// horizontal lines above x main axis
			g.drawLine(0, (int) i, ctx.width, (int) i);
		}
		
		for (double i = zero.getY(); i > -ctx.height; i -= ctx.rasterSizeY * transform.getScaleY()) {
			// horizontal lines under x main axis
			g.drawLine(0, (int) i, ctx.width, (int) i);
		}
		
		for (double i = zero.getX(); i < ctx.width; i += ctx.rasterSizeX * transform.getScaleX()) {
			// vertical lines right of y main axis
			g.drawLine((int) i, 0, (int) i, -ctx.height);
		}
		
		for (double i = zero.getX(); i > 0; i -= ctx.rasterSizeX * transform.getScaleX()) {
			// vertical lines left of y main axis
			g.drawLine((int) i, 0, (int) i, -ctx.height);
		}
		
		// main axes
		g.setColor(Color.BLACK);
		g.setStroke(CanvasContext.axesStroke);
		g.drawLine(0, (int) zero.getY(), ctx.width, (int) zero.getY());
		g.drawLine((int) zero.getX(), 0, (int) zero.getX(), -ctx.height);
		
		// axes labellings
		g.setColor(Color.BLUE);
		g.scale(1, -1);
		for (double i = zero.getY(), j = 0; i < 0; i += ctx.rasterSizeY * transform.getScaleY(), j += ctx.rasterSizeY) {
			// vertical numbers in y direction above x axis
			g.drawString(String.valueOf(j), 0, (int) -i);
		}
		
		for (double i = zero.getY(), j = 0; i > -ctx.height; i -= ctx.rasterSizeY * transform.getScaleY(), j -= ctx.rasterSizeY) {
			// vertical numbers in y direction unter x axis
			g.drawString(String.valueOf(j), 0, (int) -i);
		}
		
		for (double i = zero.getX(), j = 0; i > 0; i -= ctx.rasterSizeX * transform.getScaleX(), j -= ctx.rasterSizeX) {
			// horizontal numbers in x direction left of y axis
			g.drawString(String.valueOf(j), (int) i, ctx.height);
		}
		
		for (double i = zero.getX(), j = 0; i < ctx.width; i += ctx.rasterSizeX * transform.getScaleX(), j += ctx.rasterSizeX) {
			// horizontal numbers in x direction right of y axis
			g.drawString(String.valueOf(j), (int) i, ctx.height);
		}
		g.scale(1, -1);
	}

}
