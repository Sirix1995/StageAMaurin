package de.grogra.imp3d.edit;

import java.util.EventObject;
import javax.vecmath.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.vecmath.*;
import de.grogra.math.*;
import de.grogra.pf.ui.*;
import de.grogra.graph.Graph;

public class Rotate extends TransformTool
{
	RotateCircle[] circles = new RotateCircle[3];

	static final Color3f YELLOW = new Color3f (1, 1, 0);

	private static final float CIRCLE_RADIUS = 1;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Rotate ());
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
		return new Rotate ();
	}

//enh:end

	final LineArray lines = new LineArray ();

	private class RotateCircle extends NURBSCurve
		implements Pickable, de.grogra.util.EventListener, Command
	{
		final int id;
		private final Color3f normalColor;
		private final Vector3d rotateAxis = new Vector3d ();

		private final Point3d p = new Point3d ();
		private final Vector3d v = new Vector3d (), w = new Vector3d (),
			delta0 = new Vector3d ();
		private final AxisAngle4d axisAngle = new AxisAngle4d ();
		private final Matrix3d scaleShear = new Matrix3d (), rot = new Matrix3d (),
			newRot = new Matrix3d ();
		private boolean disabled;


		RotateCircle (int id, int x, int y, int z, Color3f color)
		{
			this.normalColor = color;
			setHighlight (false);
			this.id = id;
			circles[id] = this;

			rotateAxis.set (x, y, z);

			Circle c = new Circle (CIRCLE_RADIUS);
			c.setPlane (id);
			setCurve (c);
		}


		public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
						  Matrix4d transformation, de.grogra.imp.PickList list)
		{
			list.getGraphState ().setObjectContext (null, false);
			segmentize (getSegmentizableSource (list.getGraphState ()), list.getGraphState (), lines, 1);
			PickRayVisitor.pickLines (lines, origin, direction, transformation, list, 10);
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
				Rotate.this.setHighlight (-1);
			}
			else
			{
				if (d.draggingStarted ())
				{
					Rotate.this.setHighlight (id);
					disabled = false;
				}
				else if (disabled)
				{
					return;
				}

				Matrix4d g = getToolTransformation ();
				p.set (d.origin);
				Math2.invTransformPoint (g, p);
				v.set (d.direction);
				Math2.invTransformVector (g, v);
				v.normalize ();
				if (Math.abs (v.dot (rotateAxis)) < 0.05)
				{
					disabled = true;
					return;
				}
				w.set (p);
				v.scaleAdd (-rotateAxis.dot (w) / rotateAxis.dot (v), v, p);
				g = new Matrix4d ();
				local.getRotationScale (scaleShear);
				Math2.decomposeQR (scaleShear, rot);
				g.setRotationScale (scaleShear);
				g.m33 = 1;
				g.mul (getAdjustment ());
				Math2.transformPoint (g, v);

				if (d.draggingStarted ())
				{
					delta0.set (v);
				}
				else
				{
					g.transform (rotateAxis, w);
					w.normalize ();
					v.normalize ();
					v.scaleAdd (-v.dot (w), w, v);
					if (v.lengthSquared () < 0.0025)
					{
						disabled = true;
						return;
					}
					v.normalize ();
					axisAngle.set (w, 0);
					w.cross (w, v);
					axisAngle.angle
						= -Math.atan2 (w.dot (delta0), v.dot (delta0));
					newRot.set (axisAngle);
					rot.mul (newRot);
					rot.mul (scaleShear);
					g.set (local);
					g.setRotationScale (rot);
					setTargetTransform (g);
				}
			}
			d.consume ();
		}


		void setHighlight (boolean highlight)
		{
			setColor (highlight ? YELLOW : normalColor);
		}
	}


	void setHighlight (int id)
	{
		for (int i = 0; i < 3; i++)
		{
			circles[i].setHighlight (i == id);
		}
	}


	public Rotate ()
	{
		addEdgeBitsTo (new RotateCircle (2, 1, 0, 0, new Color3f (1, 0, 0)), Graph.BRANCH_EDGE, null);
		addEdgeBitsTo (new RotateCircle (1, 0, 1, 0, new Color3f (0, 0, 1)), Graph.BRANCH_EDGE, null);
		addEdgeBitsTo (new RotateCircle (0, 0, 0, 1, new Color3f (0, 1, 0)), Graph.BRANCH_EDGE, null);
	}

}
