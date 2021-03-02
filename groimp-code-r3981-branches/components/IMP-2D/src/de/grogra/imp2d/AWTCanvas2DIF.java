
package de.grogra.imp2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;

import de.grogra.graph.GraphState;
import de.grogra.math.Pool;

public interface AWTCanvas2DIF
{

	public View2DIF getView2D ();

	public int getWidth ();

	public int getHeight ();

	public int getCurrentLayer ();

	public int getMinLayer ();

	public int getMaxLayer ();

	public boolean isCurrentLayer (int layer);

	public void setGraphicsTransform (Matrix3d t);

	public void draw (Shape s, Matrix3d t, Stroke stroke);

	public void setColor (Color color);
	
	/**
	 * @return render GraphState
	 */
	public GraphState getRenderGraphState ();
	
	public void setColor (Color3f value);
	
	public void setColor (Color color, int state, boolean showSel);
	
	public Graphics2D getGraphics ();
	
	public GraphState getGraphState ();
	
	public void resetGraphicsTransform ();
	
	public Pool getPool();
	
	public boolean isBendedCurve();
	
	public boolean isShowSlotEdges();
	
	public boolean isShowSlotLabels();

	public void setMENinPool(float i);

}
