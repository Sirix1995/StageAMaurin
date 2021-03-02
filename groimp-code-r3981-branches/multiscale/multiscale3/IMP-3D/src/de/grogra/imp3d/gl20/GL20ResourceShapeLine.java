package de.grogra.imp3d.gl20;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20GfxServer;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceShape;

public class GL20ResourceShapeLine extends GL20ResourceShape {
	/**
	 * line coordinates attribute bit
	 */
	final private static int LINE_COORD = 0x1;
	
	/**
	 * color attribute bit
	 */
	final private static int COLOR = 0x2;
	
	/**
	 * all changed that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;
	
	/**
	 * line coordinates attribute
	 */
	private Vector3f start = new Vector3f(0.0f,0.0f,0.0f);
	private Vector3f end = new Vector3f(0.0f,0.0f,0.0f);
	
	/**
	 * color attribute
	 */
	private Vector4f color = new Vector4f(1.0f,1.0f,1.0f,1.0f);
	
	public GL20ResourceShapeLine() {
		super(GL20Resource.GL20RESOURCE_SHAPE_LINE);
	}
	
	final public void setColor(Vector4f color) {
		if (this.color.equals(color) == false) {
			this.color.set(color);
			changeMask |= COLOR;
		}
	}
	
	/**
	 * set the <code>start</code> and <code>end</code> coordinates
	 * 
	 * @param start the <code>start</code> coordinates of the line
	 * @param end the <code>end</code> coordinates of the line
	 */
	final public void setLineCoordinates(Vector3f start,Vector3f end) {
		if (this.start.equals(start) == false) {
			this.start.set(start);
			changeMask |= LINE_COORD;
		}
		if (this.end.equals(end) == false) {
			this.end.set(end);
			changeMask |= LINE_COORD;
		}
	}
	
	/**
	 * tell this <code>GL20ResourceShape</code> that it should apply the
	 * geometry to the <code>GL20GfxServer</code>
	 */	
	public void applyGeometry() {
		super.applyGeometry();
		draw();
	}
	
	/**
	 * draw the single line
	 */
	private void draw() {
		GL20GfxServer gfxServer = GL20GfxServer.getInstance();
		gfxServer.setCurrentColor(color);
		gfxServer.drawLine(start,end);
	}
	
	/**
	 * check if this <code>GL20ResourceShapeLine</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceShapeLine</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if ((changeMask != 0))
			return false;
		else
			return super.isUpToDate();
	}	
	
	/**
	 * update the state of this <code>GL20ResourceShapeLine</code>
	 * 
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		if (changeMask != 0) {
			if ((changeMask & LINE_COORD) != 0) {
				// nothing to do
			}
			
			changeMask = 0;
		}
		
		// apply changes to <code>GL20ResourceShape</code>		
		super.update();
	}
	
	/**
	 * destroy this <code>GL20ResourceShapeLine</code>
	 * 
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		super.destroy();
	}
}