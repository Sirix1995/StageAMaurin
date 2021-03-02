package de.grogra.animation.trackview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;

import de.grogra.animation.AnimManager;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.timeline.TimeContext;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	final public static int TOOL_MOVE_KEYS = 0;
	final public static int TOOL_ADD_KEYS = 1;
	
	final private CanvasContext ctx;

	int mouseStartX = 0, mouseStartY = 0;
	
	private CanvasObject timeline;
	private CanvasObject coordinates;
	private CanvasObject background;
	private List<GraphObject> graphObjects;
	
	private Selectable selectedObject = null;
	
	private int keyTool = TOOL_MOVE_KEYS;
	
	public CanvasPanel(AnimManager animManager, TimeContext timeContext) {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		
		this.ctx = new CanvasContext();
		this.ctx.animManager = animManager;
		this.ctx.timeContext = timeContext;
		
		this.timeline = new TimelineObject();
		this.coordinates = new CoordinatesObject();
		this.background = new BackgroundObject();
		this.graphObjects = new ArrayList<GraphObject>();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		
		ctx.graphics = g2;
		ctx.image = img;
		ctx.width = this.getWidth();
		ctx.height = this.getHeight();
		
		// draw background
		background.draw(ctx);
		g2.scale(1, -1);

		// draw coordinates/axis lines
		coordinates.draw(ctx);
		
		// draw values
		for (GraphObject go : graphObjects) {
			go.draw(ctx);
		}
		
		// draw timeline
		timeline.draw(ctx);
		
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown()) {
		}
		else {
			// use tool with object under mouse
			switch (keyTool) {
				case TOOL_ADD_KEYS:
					// add key
					
					// find correct graph object
					for (GraphObject go : graphObjects) {
						Point2D valuePoint = go.isPointOnLine(ctx,
								new Point2D.Double(e.getX(), e.getY()));
						if (valuePoint != null) {
							go.addNewKey(ctx, valuePoint);
							break;
						}
					}
					break;
			}
		}
		this.repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.isControlDown()) {
			// prepare to move view
			mouseStartX = e.getX();
			mouseStartY = e.getY();
		}
		else {
			// prepare to use tool at object under mouse
			for (GraphObject go : graphObjects) {
				selectedObject = go.getSelection(ctx, e.getX(), e.getY());
			}
			repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		AffineTransform transform = ctx.transform;
		
		double distX = e.getX() - mouseStartX;
		double distY = e.getY() - mouseStartY;
		
		distX /= transform.getScaleX();
		distY /= transform.getScaleY();
		
		mouseStartX = e.getX();
		mouseStartY = e.getY();

		if (e.isControlDown()) {
			// move view
			transform.translate(distX, -distY);
		}
		else {
			// use tool with object under mouse
			switch (keyTool) {
				case TOOL_MOVE_KEYS:
					// move object
					if (selectedObject != null) {
						selectedObject.moveTo(ctx, e.getX(), e.getY());
					}
					break;
			}
		}
		this.repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		// zoom view
		AffineTransform transform = ctx.transform;
		
		double zoom = e.getWheelRotation();
		zoom = zoom > 0 ? Math.pow(0.9, zoom) : Math.pow(1.1, -zoom);	
		Point2D mouse = new Point2D.Double(e.getX(), -e.getY());

		try {
			transform.inverseTransform(mouse, mouse);
		} catch (NoninvertibleTransformException e1) {e1.printStackTrace();}
		
		scaleView(zoom, zoom, mouse.getX(), mouse.getY());
		repaint();
	}
	
	/**
	 * Set axes to left and down. Set scale to 1.
	 */
	private void resetView() {
		ctx.transform.setToTranslation(0, -this.getHeight());
	}
	
	/**
	 * Scale in canvas coordinates. TODO: Set new raster sizes.
	 * @param scaleX
	 * @param scaleY
	 */
	private void scaleView(double scaleX, double scaleY) {
		AffineTransform transform = ctx.transform;
		
		transform.scale(scaleX, scaleY);

		double rasterX = 0, rasterY = 0, oldRasterX = 0, oldRasterY = 0;
		
		// loop until no change of raster is done anymore
		do {
			oldRasterX = rasterX;
			oldRasterY = rasterY;
			
			rasterX = ctx.rasterSizeX * transform.getScaleX();
			rasterY = ctx.rasterSizeY * transform.getScaleY();
			
			if (rasterX < 40)
				ctx.rasterSizeX = ctx.rasterSizeX * 2;
			else if (rasterX > 80)
				ctx.rasterSizeX = Math.max(ctx.rasterSizeX / 2, 1);
			
			if (rasterY < 40)
				ctx.rasterSizeY = ctx.rasterSizeY * 2;
			else if (rasterY > 80)
				ctx.rasterSizeY = Math.max(ctx.rasterSizeY / 2, 1);
		} while ((oldRasterX != rasterX) || (oldRasterY != rasterY));
		
	}
	
	/**
	 * Scale in canvas coordinates centered at given pivot.
	 * @param scaleX
	 * @param scaleY
	 * @param pivotX
	 * @param pivotY
	 */
	private void scaleView(double scaleX, double scaleY, double pivotX, double pivotY) {
		AffineTransform transform = ctx.transform;
		transform.translate(pivotX, pivotY);
		scaleView(scaleX, scaleY);
		transform.translate(-pivotX, -pivotY);		
	}
	
	/**
	 * Center in world coordinates.
	 * @param centerX
	 * @param centerY
	 */
	public void centerView(double centerX, double centerY) {
		AffineTransform transform = ctx.transform;
		
		double scaleX = transform.getScaleX();
		double scaleY = transform.getScaleY();
		
		Point2D center = new Point2D.Double(centerX, centerY);
		transform.transform(center, center);
		transform.translate(-(center.getX() - (this.getWidth() / 2.0))/scaleX, (-(center.getY() - (this.getHeight() / 2.0)) - this.getHeight())/scaleY);
		repaint();
	}
	
	/**
	 * Fit current values line to canvas.
	 */
	public void fitView() {
		resetView();
		Point2D p = new Point2D.Double();
		int minX = ctx.timeContext.getStart(), maxX = ctx.timeContext.getEnd();
		double minY, maxY;
		
		if (graphObjects.isEmpty()) {
			minY = 0;
			maxY = 20;
		}
		else {
			// take minY and maxY from first object, compare other objects with this one
			Iterator<GraphObject> it = graphObjects.iterator();
			GraphObject go = it.next();
			go.getDimension(ctx, p);
			minY = p.getX();
			maxY = p.getY();
			while (it.hasNext()) {
				go = it.next();
				go.getDimension(ctx, p);
				if (p.getX() < minY)
					minY = getX();
				if (p.getY() > maxY)
					maxY = p.getY();				
			}
		}
		
		if (minY == maxY) {
			minY -= 5;
			maxY += 15;
		}
		
		scaleView(0.9 * this.getWidth() / (maxX - minX),
			0.9 * this.getHeight() / (maxY - minY));
		centerView((minX + maxX) / 2.0, (minY + maxY) / 2.0);
		
		repaint();
	}

	public void addGraphContext(Node node, PersistenceField field) {
		GraphObject go = new GraphObject(node, field, new Color(0, 192, 0));
		go.updateKeys(ctx);
		this.graphObjects.add(go);
	}

	public void clearGraphContext() {
		graphObjects.clear();
	}

	public int getKeyTool() {
		return keyTool;
	}

	public void setKeyTool(int keyTool) {
		this.keyTool = keyTool;
	}

}
