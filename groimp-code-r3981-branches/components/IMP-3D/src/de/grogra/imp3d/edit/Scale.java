
package de.grogra.imp3d.edit;

import java.util.EventObject;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.CameraBase;
import de.grogra.imp3d.DragEvent3D;
import de.grogra.imp3d.objects.Box;
import de.grogra.imp3d.objects.Line;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.vecmath.Math2;

/**
 * Scaling tool.
 * 
 * There are three cubes for each axis. Clicking and dragging them allows
 * to resize the object in that direction. There is a fourth cube in the
 * center of the tool. Clicking and dragging the mouse allows to resize
 * the object in all directions by the same amount.
 * 
 * There is a minimum scale factor of 1% of the original size for resizing
 * an object with one drag operation. Shrinking an object even further
 * requires to finish the drag and start a new one. This prevents objects
 * from disappearing unvoluntarily.
 * 
 * @author Reinhard Hemmerling
 */
public class Scale extends TransformTool
{
	//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Scale ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new Scale ();
	}

//enh:end

	private static final float DIV = 0.95f;
	private static final float AXIS_LENGTH = 2 * (1 - DIV);

	private ScaleBox[] boxes = new ScaleBox[4];

	private Matrix4d adjustTransformation = null;

	public Scale ()
	{
		// create ScaleBox for x, y and z
		addEdgeBitsTo (createHandle (0, 1, 0, 0, RGBAShader.RED),
				Graph.BRANCH_EDGE, null);
		addEdgeBitsTo (createHandle (1, 0, 1, 0, RGBAShader.BLUE),
				Graph.BRANCH_EDGE, null);
		addEdgeBitsTo (createHandle (2, 0, 0, 1, RGBAShader.GREEN),
				Graph.BRANCH_EDGE, null);

		// create center ScaleBox
		addEdgeBitsTo (new CenterScaleBox (3, null, RGBAShader.LIGHT_GRAY),
				Graph.BRANCH_EDGE, null);
	}

	private Node createHandle (int id, int x, int y, int z, RGBAShader color)
	{
		Line l = new Line (x * DIV, y * DIV, z * DIV);
		l.setColor (color.x, color.y, color.z);
		l.addEdgeBitsTo (new ScaleBox (id, x, y, z, l, color),
				Graph.SUCCESSOR_EDGE, null);
		return l;
	}

	/**
	 * Highlight handle specified by id (if usePlane is false) or
	 * the other handles (if usePlane is true).
	 * @param id 
	 * @param usePlane
	 */
	void setHighlight (int id, boolean usePlane)
	{
		for (int i = 0; i < boxes.length; i++)
		{
			if (boxes[i] != null)
			{
				boxes[i].setHighlight ((i == id));
			}
		}
	}

	@Override
	protected void adjustToolTransformation (Matrix4d t, CameraBase c)
	{
		if (adjustTransformation == null)
		{
			adjustScale (t, c, 150);
			adjustTransformation = new Matrix4d (t);
		}
		else
		{
			t.set (adjustTransformation);
		}
	}

	/**
	 * A cube that can be used to resize an object in one direction.
	 * Clicking the cube and dragging the mouse along the corresponding
	 * axis will resize the object with a minimum possible resize factor
	 * of MIN_SCALE. 
	 * 
	 * @author Reinhard Hemmerling
	 */
	private class ScaleBox extends Box implements de.grogra.util.EventListener,
			Command
	{
		static final double MIN_SCALE = 0.01;

		final int id;
		protected final RGBAShader color;
		protected final Line line;

		protected final Point3d p = new Point3d ();
		protected final Vector3d v = new Vector3d (), axis = new Vector3d ();
		protected final double[] lambda = new double[2];
		protected boolean usePlane;
		protected boolean disabled; // is tool in use ?
		protected double initialScale;
		protected double currentScale;
		protected final Matrix4d originalLocal = new Matrix4d ();
		protected final Matrix4d toolTransformation = new Matrix4d ();

		ScaleBox (int id, int x, int y, int z, Line line, RGBAShader s)
		{
			super ();
			setStartPosition (-0.5f);
			// scaling box is placed at (x/y/z)
			setTransform (x, y, z);
			// and has a size of 2*AXIS_LENGTH into each direction
			setLength (AXIS_LENGTH);
			setWidth (AXIS_LENGTH);
			setHeight (AXIS_LENGTH);

			// remember the axis for this tool (i.e. 1/0/0, 0/1/0 or 0/0/1)
			axis.set (x, y, z);
			axis.normalize ();
			// make box the same color as its line
			color = s;
			setShader (color);
			this.id = id;
			this.line = line;
			boxes[id] = this;
		}

		public void eventOccured (EventObject e)
		{
			if (e instanceof DragEvent3D)
			{
				executeWithWriteLock (this, (DragEvent3D) e);
			}
		}

		public String getCommandName ()
		{
			return null;
		}

		public void run (Object o, Context ctx)
		{
			DragEvent3D d = (DragEvent3D) ctx;
			if (d.draggingFinished ())
			{
				draggingFinished (d);
			}
			else if (d.draggingStarted ())
			{
				draggingStarted (d);
			}
			else
			// just dragging along
			{
				if (!disabled)
				{
					dragging (d);
				}
			}
			d.consume ();
		}

		protected void draggingFinished (DragEvent3D d)
		{
			// remove highlight from tool
			Scale.this.setHighlight (-1, false);

			// remove adjust transformation
			adjustTransformation = null;
		}

		protected void draggingStarted (DragEvent3D d)
		{
			// highlight the handles used to scale (depending on usePlane)
			Scale.this.setHighlight (id, usePlane);

			// enable this ScaleBox
			disabled = false;

			// transform picking ray into tool coordinate system
			toolTransformation.set (getToolTransformation ());
			p.set (d.origin);
			Math2.invTransformPoint (toolTransformation, p);
			v.set (d.direction);
			Math2.invTransformVector (toolTransformation, v);
			v.normalize ();

			// check if tool is visible from the side (and not from front/back)
			// then disable tool if it cannot be properly used
			// note that only one of x,y and z is 1, the others are 0
			if (Math.abs (v.dot (axis)) > 0.95)
			{
				// disable the scaling tool
				disabled = true;
			}

			// calculate distance between picking ray and tool line
			Math2.shortestConnection (p, v, new Point3d (), axis, lambda);

			// remember initial scale factor
			initialScale = lambda[1];

			// make sure that initialScale is not zero
			// otherwise division by zero may happen
			initialScale = Math.max (initialScale, Double.MIN_VALUE);

			// remember original local transformation matrix
			originalLocal.set (local);
		}

		protected void dragging (DragEvent3D d)
		{
			// transform picking ray into tool coordinate system
			p.set (d.origin);
			Math2.invTransformPoint (toolTransformation, p);
			v.set (d.direction);
			Math2.invTransformVector (toolTransformation, v);
			v.normalize ();

			// calculate distance between picking ray and tool line
			Math2.shortestConnection (p, v, new Point3d (), axis, lambda);

			// remember current scale factor
			currentScale = lambda[1];

			// prevent the scale to become less than zero
			currentScale = Math.max (currentScale, Double.MIN_VALUE);

			// calculate rescaling factor
			double rescalingFactor = currentScale / initialScale;
			rescalingFactor = Math.max (rescalingFactor, MIN_SCALE);
			rescalingFactor -= 1;

			// rescale local coordinate system
			Matrix4d newLocal = new Matrix4d (local);
			newLocal.setIdentity ();
			newLocal.setElement (0, 0, 1 + axis.x * rescalingFactor);
			newLocal.setElement (1, 1, 1 + axis.y * rescalingFactor);
			newLocal.setElement (2, 2, 1 + axis.z * rescalingFactor);
			newLocal.mul (originalLocal, newLocal);

			// update object transformation
			setTargetTransform (newLocal);
		}

		void setHighlight (boolean highlight)
		{
			RGBAShader s = highlight ? RGBAShader.YELLOW : color;
			setShader (s);
			if (line != null)
			{
				line.setColor (s.x, s.y, s.z);
			}
		}

	}

	/**
	 * A cube that allows resizing an object in all three directions
	 * by the same amount.  
	 * 
	 * @author Reinhard Hemmerling
	 */
	private class CenterScaleBox extends ScaleBox
	{
		static final double SCALE_AMOUNT = 0.01;

		CenterScaleBox (int id, Line line, RGBAShader s)
		{
			super (id, 0, 0, 0, line, s);
		}

		@Override
		protected void draggingStarted (DragEvent3D d)
		{
			// enable this ScaleBox
			disabled = false;

			// start with a scale factor of one (i.e. no scaling applied)
			currentScale = 1.0;

			// remember original local transformation matrix
			originalLocal.set (local);
		}

		@Override
		protected void dragging (DragEvent3D d)
		{
			// get the amount of pixels the mouse moved
			int deltaX = d.getDeltaX ();
			int deltaY = d.getDeltaY ();

			// get the total amount of pixel the mouse moved
			// moving right and up increases the delta by one
			// moving the other directions decreases delta by one
			int delta = deltaX - deltaY;

			// modify the scale factor depending on the pixels the
			// mouse was moved and weighted by SCALE_AMOUNT
			currentScale += SCALE_AMOUNT * (double) delta;

			// make sure that the scale does not become less or
			// equal than zero (i.e. the smallest value is MIN_SCALE)
			currentScale = Math.max (currentScale, MIN_SCALE);

			// rescale local coordinate system
			Matrix4d newLocal = new Matrix4d ();
			newLocal.set (currentScale);
			newLocal.mul (originalLocal, newLocal);

			// update object transformation
			setTargetTransform (newLocal);
		}
	}
}
