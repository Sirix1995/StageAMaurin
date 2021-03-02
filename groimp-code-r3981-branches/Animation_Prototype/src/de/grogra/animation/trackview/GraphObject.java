package de.grogra.animation.trackview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.grogra.animation.util.Util;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.IndirectField;
import de.grogra.persistence.PersistenceField;
import de.grogra.reflect.Type;

public class GraphObject implements CanvasObject, KeyChangeListener {

	final private Node node;
	final private PersistenceField field;
	
	final private Map<KeyObject, Integer> keys;
	final private Set<Integer> times;
	
	final private Color color;
	
	public GraphObject(Node node, PersistenceField field, Color color) {
		this.node = node;
		this.field = new IndirectField(field);
		this.keys = new HashMap<KeyObject, Integer>();
		this.times  = new HashSet<Integer>();
		this.color = color;
	}
	
	public void updateKeys(CanvasContext ctx) {
		keys.clear();
		times.clear();
		ctx.animManager.getValueHandler().getTimesForProperty(node, field, times);
		for (Integer i : times) {
			KeyObject ko = new KeyObject(ctx, i, node, field);
			ko.addKeyChangeListener(this);
			keys.put(ko, i);
		}		
	}
	
	public void draw(CanvasContext ctx) {
		Graphics2D g = ctx.graphics;
		BufferedImage img = ctx.image;
		AffineTransform transform = ctx.transform;
		
		g.setColor(color);
		
		// read values from node/property
		Point2D minTime = new Point2D.Double(ctx.timeContext.getStart(), 0);
		Point2D maxTime = new Point2D.Double(ctx.timeContext.getEnd(), 0);
				
		transform.transform(minTime, minTime);
		transform.transform(maxTime, maxTime);

		final Point2D valuePoint = new Point2D.Double();
		final Point2D oldValuePoint = new Point2D.Double();
		
		Set<Integer> times = new HashSet<Integer>();
		ctx.animManager.getValueHandler().getTimesForProperty(node, field, times);
		if (times.isEmpty()) {
			// no animation values saved for this node+property
			
			Number value = null;
			//TODO: Character, Boolean, Object
			de.grogra.reflect.Type<?> type = field.getType();
			if (type.equals(de.grogra.reflect.Type.BYTE))
				value = field.getByte(node, null);
			else if (type.equals(de.grogra.reflect.Type.SHORT))
				value = field.getShort(node, null);
			else if (type.equals(de.grogra.reflect.Type.INT))
				value = field.getInt(node, null);
			else if (type.equals(de.grogra.reflect.Type.LONG))
				value = field.getLong(node, null);
			else if (type.equals(de.grogra.reflect.Type.FLOAT))
				value = field.getFloat(node, null);
			else if (type.equals(de.grogra.reflect.Type.DOUBLE))
				value = field.getDouble(node, null);

			if (value != null) {
				valuePoint.setLocation(0, value.doubleValue());
				transform.transform(valuePoint, valuePoint);
				
				g.drawLine(Util.round(minTime.getX()), (int) valuePoint.getY(),
						Util.round(maxTime.getX()), (int) valuePoint.getY());
			}

		}
		else {
		
			final Point2D curTime = new Point2D.Double();
			Object value;
			
			// first calculation of old value so left-most pixel is drawn correctly
			curTime.setLocation((int) minTime.getX(), 0);
			try {
				transform.inverseTransform(curTime, curTime);
			} catch (NoninvertibleTransformException e) {e.printStackTrace(); return;}
			value = ctx.animManager.getValue(curTime.getX(), node, field);
			if (value instanceof Number) {
				oldValuePoint.setLocation(curTime.getX(), ((Number) value).doubleValue());
				transform.transform(oldValuePoint, oldValuePoint);
			}
			
//			int color = Util.rgbToInt(g.getColor());
			
			// loop over pixels of canvas
			for (int i = 0; i < ctx.width; i++) {
				
				if (i < minTime.getX() || i > maxTime.getX())
					continue;
				
				curTime.setLocation(i, 0);
				try {
					transform.inverseTransform(curTime, curTime);
				} catch (NoninvertibleTransformException e) {e.printStackTrace();}
				
				value = ctx.animManager.getValue(curTime.getX(), node, field);
				if (value instanceof Number) {
					valuePoint.setLocation(curTime.getX(), ((Number) value).doubleValue());
					transform.transform(valuePoint, valuePoint);
					
					//TODO: find out which way is better
					g.drawLine((int) oldValuePoint.getX(), (int) oldValuePoint.getY(),
							(int) valuePoint.getX(), (int) valuePoint.getY());
					oldValuePoint.setLocation(valuePoint);
					
//					int result = (int) -valuePoint.getY();
//					// draw first line
//					if (result >= 0 && result < ctx.height)
//						img.setRGB(i, result, color);
//					result--;
//					// draw second line for thicker value
//					if (result >= 0 && result < ctx.height)
//						img.setRGB(i, result, color);
				}	
				
			}
			
			for (KeyObject ko : keys.keySet()) {
				ko.draw(ctx);
			}
			
		}
	}

	public Selectable getSelection(CanvasContext ctx, int canvasX, int canvasY) {
		Selectable result = null;
		for (KeyObject ko : keys.keySet()) {
			if (ko.isSelected(ctx, canvasX, canvasY)) {
				ko.setSelected(true);
				result = ko;
			}
			else {
				ko.setSelected(false);
			}
		}
		return result;
	}
	
	public void keyChanged(CanvasContext ctx, KeyObject keyObject, Point2D valuePoint) {
		
		int key = keys.get(keyObject);
		int newKey = (int) Math.round(valuePoint.getX());
		keys.put(keyObject, newKey);

		Object value = getCorrectObjectForType(valuePoint.getY());
		
		if (value != null) {
			ctx.animManager.changeValue(key, newKey, node, field, value);
		}
	}
	
	private Object getCorrectObjectForType(double value) {
		Object result = null;
		Type<?> type = field.getType();
		if (type.equals(Type.BYTE))
			result = (byte) Util.round(value);
		else if (type.equals(Type.SHORT))
			result = (short) Util.round(value);
		else if (type.equals(Type.INT))
			result = Util.round(value);
		else if (type.equals(Type.LONG))
			result = Util.round(value);
		else if (type.equals(Type.FLOAT))
			result = (float) value;
		else if (type.equals(Type.DOUBLE))
			result = value;
		return result;
	}
	
	/**
	 * Returns the dimension of this canvas object ONLY in y direction.<br>
	 * p.x is the lowest value in real coordinates.<br>
	 * p.y is the highest value in real coordinates.
	 * @param ctx
	 * @param p
	 */
	public void getDimension(CanvasContext ctx, Point2D p) {
		
		Point2D kp = new Point2D.Double();
		
		if (keys.isEmpty()) {
			p.setLocation(0, 0);
		}
		else {
			
			// take minY and maxY from first key, compare other keys with this one
			Iterator<KeyObject> it = keys.keySet().iterator();
			
			KeyObject ko = it.next();
			ko.getValue(ctx, kp);
			p.setLocation(kp.getY(), kp.getY());
			
			while (it.hasNext()) {
				ko = it.next();
				ko.getValue(ctx, kp);
				if (kp.getY() < p.getX())
					p.setLocation(kp.getY(), p.getY());
				if (kp.getY() > p.getY())
					p.setLocation(p.getX(), kp.getY());
			}
		}
	}
	
	/**
	 * Is a point in canvas coordinates in the near of the line.
	 * p is transformed to real coordinates. p.x is used to find the real value of
	 * the property. This value is compared to p.y and if it is in the near, the computed
	 * value is return, else null.
	 * @param ctx
	 * @param p
	 * @return
	 */
	public Point2D isPointOnLine(CanvasContext ctx, Point2D canvasPoint) {
		Point2D realPoint = new Point2D.Double();
		try {
			ctx.transform.inverseTransform(canvasPoint, realPoint);
		} catch (NoninvertibleTransformException e) {e.printStackTrace(); return null;}
		
		Number value = (Number) ctx.animManager.getValue(realPoint.getX(), node, field);
		
		Point2D realValue = new Point2D.Double(realPoint.getX(), value.doubleValue());
		Point2D canvasValue = new Point2D.Double();
		
		ctx.transform.transform(realValue, canvasValue);
		
		if (Util.inRange(-canvasValue.getY(), canvasPoint.getY() - 10, canvasPoint.getY() + 10))
			return realValue;
		
		return null;
	}
	
	public void addNewKey(CanvasContext ctx, Point2D valuePoint) {
		Object value = getCorrectObjectForType(valuePoint.getY());
		if (value != null) {
			ctx.animManager.putValue((int) valuePoint.getX(), node, field, value);
			ctx.animManager.updateAnimValues(ctx.timeContext.getCurrentTime(), false);
		}
	}

}
