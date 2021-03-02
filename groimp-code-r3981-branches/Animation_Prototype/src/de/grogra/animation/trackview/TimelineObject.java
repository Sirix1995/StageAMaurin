package de.grogra.animation.trackview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class TimelineObject implements CanvasObject {

	public void draw(CanvasContext ctx) {
		Graphics2D g = ctx.graphics;
		AffineTransform transform = ctx.transform;
		
		g.setColor(Color.BLUE);
		Point2D curTime = new Point2D.Double(ctx.timeContext.getCurrentTime(), 0);
		
		transform.transform(curTime, curTime);
		
		g.drawLine((int) curTime.getX() - 2, 0, (int) curTime.getX() - 2, -ctx.height);
		g.drawLine((int) curTime.getX() + 2, 0, (int) curTime.getX() + 2, -ctx.height);

	}

}
