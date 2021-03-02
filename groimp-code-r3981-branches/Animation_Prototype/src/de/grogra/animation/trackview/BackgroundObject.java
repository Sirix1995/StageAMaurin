package de.grogra.animation.trackview;

import java.awt.Color;
import java.awt.geom.Point2D;

public class BackgroundObject implements CanvasObject {

	public void draw(CanvasContext ctx) {
		
		Point2D rangeStart = new Point2D.Double(ctx.timeContext.getStart(), 0);
		Point2D rangeEnd = new Point2D.Double(ctx.timeContext.getEnd(), 0);
		
		ctx.transform.transform(rangeStart, rangeStart);
		ctx.transform.transform(rangeEnd, rangeEnd);
		
		ctx.graphics.setColor(Color.GRAY);
		ctx.graphics.fillRect(0, 0,
				(int) rangeStart.getX(), ctx.height);
		
		ctx.graphics.setColor(Color.LIGHT_GRAY);
		ctx.graphics.fillRect((int) rangeStart.getX(), 0,
				(int) rangeEnd.getX() - (int) rangeStart.getX(), ctx.height);

		ctx.graphics.setColor(Color.GRAY);
		ctx.graphics.fillRect((int) rangeEnd.getX(), 0,
				ctx.width - (int) rangeEnd.getX(), ctx.height);
		
	}

}
