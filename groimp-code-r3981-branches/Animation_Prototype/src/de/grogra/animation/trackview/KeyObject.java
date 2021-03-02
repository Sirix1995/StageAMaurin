package de.grogra.animation.trackview;

import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.grogra.animation.util.Util;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;

public class KeyObject implements CanvasObject, Selectable {

	final private Point2D valuePoint;
	final private Point2D drawValuePoint;
	final private List<KeyChangeListener> keyChangeListeners;
	private Color color;
	
	public KeyObject(CanvasContext ctx, int key, Node node, PersistenceField field) {
		valuePoint = new Point2D.Double();
		drawValuePoint = new Point2D.Double();
		keyChangeListeners = new ArrayList<KeyChangeListener>();
		color = Color.BLACK;
		
		Object value;
		value = ctx.animManager.getValue(key, node, field);
		if (value instanceof Number) {
			valuePoint.setLocation(key, ((Number) value).doubleValue());
		}
	}
	
	public void draw(CanvasContext ctx) {
		ctx.transform.transform(valuePoint, drawValuePoint);
		ctx.graphics.setColor(color);
		ctx.graphics.drawRect(Util.round(drawValuePoint.getX() - 3),
				Util.round(drawValuePoint.getY() - 4), 7, 7);
	}

	public boolean isSelected(CanvasContext ctx, int canvasX, int canvasY) {
		ctx.transform.transform(valuePoint, drawValuePoint);
		if (canvasX >= drawValuePoint.getX() - 5 &&
				canvasX <= drawValuePoint.getX() + 5 &&
				-canvasY >= drawValuePoint.getY() - 5 &&
				-canvasY <= drawValuePoint.getY() + 5)
			return true;
		return false;
	}
	
	public void setSelected(boolean selected) {
		color = selected ? Color.RED : Color.BLACK;
	}

	public void moveTo(CanvasContext ctx, double newX, double newY) {
		drawValuePoint.setLocation(newX, -newY);
		try {
			ctx.transform.inverseTransform(drawValuePoint, valuePoint);
		} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		for (KeyChangeListener listener : keyChangeListeners) {
			listener.keyChanged(ctx, this, valuePoint);
		}
	}
	
	public void addKeyChangeListener(KeyChangeListener listener) {
		keyChangeListeners.add(listener);
	}
	
	public void getValue(CanvasContext ctx, Point2D point) {
		point.setLocation(valuePoint.getX(), valuePoint.getY());
	}

}
