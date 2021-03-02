package de.grogra.imp3d.edit;

import java.util.EventObject;
import javax.vecmath.*;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.shading.*;
import de.grogra.math.*;
import de.grogra.vecmath.*;
import de.grogra.pf.ui.*;

/**
 * This class implements a tool for the translation of 3D objects.
 * It consists of three arrows (a line and a cone).
 * 
 * @author Ole Kniemeyer
 */
public class Translate extends TransformTool
{
	TranslateCone[] cones = new TranslateCone[3];

	private static final float DIV = 0.85f;
	private static final float AXIS_LENGTH = 1 - DIV;


	private class TranslateCone extends Cone
		implements de.grogra.util.EventListener, Command
	{
		final int id;
		private final RGBAShader color;
		private final Line line;
		
		/**
		 * Stores the closest point on the cone axis to the
		 * first pick ray of a dragging sequence. It is specified in
		 * target coordinates and computed when a dragging sequence is started.
		 */
		private final Vector3d contactPoint = new Vector3d ();
		
		/**
		 * Stores the component of the translational part of the
		 * local object which is not modified by the tool. If the tool
		 * translates the object along an axis, this fixed component of the
		 * translational part is the component of the original translation
		 * which perpendioriginal to the axis. Otherwise, if the tool
		 * translates in a place, the fixed component is the component of the
		 * original translation which is normal to the plane. The vector
		 * is specified in target coordinates
		 * and computed when a dragging sequence is started.  
		 */
		private final Vector3d fixed = new Vector3d ();
		
		/**
		 * <code>true</code> if the translation is in the plane
		 * perpendicular to the selected arrow, <code>false</code>
		 * if the translation is along the selected arrow.
		 */
		private boolean usePlane;
		
		/**
		 * <code>true</code> iff this tool has been disabled as a
		 * consequence of a mathematically problematic user input (e.g.,
		 * the attempt to drag along an arrow which is "too perpendicular"
		 * to the view plane).
		 */
		private boolean disabled;


		// some temporarily used variables, allocated once to reduce garbage
		private final Point3d p = new Point3d ();
		private final Vector3d v = new Vector3d (), w = new Vector3d (),
			axisTmp = new Vector3d ();
		private final Matrix3d m3d = new Matrix3d ();
		private final double[] lambda = new double[2];
		
		private final Vector3d handleAxis = new Vector3d ();
		private final Vector3f axis = new Vector3f ();


		TranslateCone (int id, int x, int y, int z, Line line, RGBAShader shader)
		{
			super ();
			TMatrix4d m = new TMatrix4d ();
			m.setIdentity ();
			if (x != 0)
			{
				m.rotY (Math.PI / 2);
			}
			else if (y != 0)
			{
				m.rotX (Math.PI / -2);
			}
			handleAxis.set (x * DIV, y * DIV, z * DIV);
			m.setTranslation (handleAxis);	
			setTransform (m);
			setLength (AXIS_LENGTH);
			axis.set (x * AXIS_LENGTH, y * AXIS_LENGTH, z * AXIS_LENGTH);
			setRadius (0.3f * AXIS_LENGTH);
			setShader (shader);
			color = shader;
			this.id = id;
			this.line = line;
			cones[id] = this;
		}


		@Override
		public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
						  Matrix4d t, de.grogra.imp.PickList list)
		{
			list.p3d0.set (origin);
			list.p3d1.set (0, 0, getLength ());
			origin.add (list.p3d1);
			pick (3 * getLength (), 3 * getRadius (), 2 * getRadius (), true, true,
				  origin, direction, list);
			origin.set (list.p3d0);
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
			// the implementation of this method is able to work with
			// arbitrary axes of arrows, i.e., the axes are not required to
			// form an orthonormal basis
			DragEvent3D d = (DragEvent3D) ctx;
			if (d.draggingFinished ())
			{
				Translate.this.setHighlight (-1, false);
			}
			else if (d.draggingStarted ())
			{
				usePlane = d.isAltDown ();
				Translate.this.setHighlight (id, usePlane);
				disabled = false;
				Matrix4d g = getToolTransformation ();
				
				// set (p, v) to the pick ray in tool coordinates
				p.set (d.origin);
				Math2.invTransformPoint (g, p);
				v.set (d.direction);
				Math2.invTransformVector (g, v);
				v.normalize ();
				axisTmp.set (axis);
				
				// check if pick ray and cone axis are nearly parallel
				if (Math.abs (Math.abs (v.dot (axisTmp) / AXIS_LENGTH) - 1)
					< 0.02)
				{
					// in this case, the computation would be instable,
					// disable the tool
					disabled = true;
					return;
				}
				
				// compute the shortest connection between the cone axis
				// and the pick ray
				Math2.shortestConnection (p, v, handleAxis, axisTmp, lambda);
				
				// set v to the closest point on the cone axis to the
				// pick ray
				v.scaleAdd (lambda[1], axisTmp, handleAxis);
				
				// transform v to target coordinates and store the result in contactPoint
				Math2.transformPoint (getAdjustment (), v);
				contactPoint.set (v);
				
				// retrieve the translational component of the local
				// coordinate transformation of the target
				local.get (v);
				
				// convert it to object coordinates
				Math2.invTransformVector (local, v);
				// ... and finally to tool coordinates
				Math2.invTransformVector (getAdjustment (), v);
				
				// project v onto the unit vector in axis-direction,
				// store the result in w
				w.scale (v.dot (axisTmp) / axisTmp.lengthSquared (), axisTmp);
				if (!usePlane)
				{
					// if the translation is along this cone's axis,
					// compute the component of v which is perpendicular
					// to the axis
					w.sub (v, w);
				}
				// transform the vector to target coordinates. The result
				// is the component of the translation which is fixed during
				// the dragging sequence.
				getAdjustment ().transform (w, fixed);
			}
			else
			{
				if (disabled)
				{
					return;
				}
 
				p.add (contactPoint, fixed);
				Math2.transformVector (local, p);
				getParentTransformation ().transform (p);
				p.scaleAdd (-1, d.origin);
				Matrix4d g = getToolTransformation ();
				Math2.invTransformVector (g, p);

				v.set (d.direction);
				Math2.invTransformVector (g, v);
				v.normalize ();

				if (usePlane)
				{
					m3d.setColumn (0, v);
					axisTmp.set (cones[(id + 1) % 3].axis);
					m3d.setColumn (1, axisTmp);
					axisTmp.set (cones[(id + 2) % 3].axis);
					m3d.setColumn (2, axisTmp);
					if (Math.abs (m3d.determinant ())
						< 0.02 * AXIS_LENGTH * AXIS_LENGTH)
					{
						disabled = true;
						return;
					}

					Math2.invMul (m3d, p);
					axisTmp.set (cones[(id + 1) % 3].axis);
					v.scale (p.y, axisTmp);
					axisTmp.set (cones[(id + 2) % 3].axis);
					v.scaleAdd (p.z, axisTmp, v);
				}
				else
				{
					axisTmp.set (axis);
					if (Math.abs (Math.abs (v.dot (axisTmp) / AXIS_LENGTH) - 1)
						< 0.02)
					{
						disabled = true;
						return;
					}
					Math2.shortestConnection (p, v, handleAxis,
											  axisTmp, lambda);
					v.scaleAdd (lambda[1], axisTmp, handleAxis);
				}
				getAdjustment ().transform (v);
				v.add (fixed);
				local.transform (v);
				g = new Matrix4d (local);
				g.setTranslation (v);
				setTargetTransform (g);
			}
			d.consume ();
		}


		void setHighlight (boolean highlight)
		{
			RGBAShader s = highlight ? RGBAShader.YELLOW : color;
			setShader (s);
			line.setColor (s.x, s.y, s.z);
		}
	}


	/**
	 * Highlights the cones. If <code>usePlane</code> is <code>false</code>,
	 * the specified cone is highlighted, otherwise the other cones.
	 * 
	 * @param id the cone index
	 * @param usePlane
	 */
	void setHighlight (int id, boolean usePlane)
	{
		for (int i = 0; i < 3; i++)
		{
			cones[i].setHighlight ((i == id) != usePlane);
		}
	}


	public Translate ()
	{
		// add the three arrows to this tool root
		addEdgeBitsTo (createArrow (0, 1, 0, 0, RGBAShader.RED), Graph.BRANCH_EDGE, null);
		addEdgeBitsTo (createArrow (1, 0, 1, 0, RGBAShader.BLUE), Graph.BRANCH_EDGE, null);
		addEdgeBitsTo (createArrow (2, 0, 0, 1, RGBAShader.GREEN), Graph.BRANCH_EDGE, null);
	}


	private Node createArrow (int id, int x, int y, int z, RGBAShader color)
	{
		// create a line in the specified direction
		Line l = new Line (x * DIV, y * DIV, z * DIV);
		l.setColor (color.x, color.y, color.z);
		// add a cone
		l.addEdgeBitsTo (new TranslateCone (id, x, y, z, l, color), Graph.SUCCESSOR_EDGE, null);
		return l;
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Translate ());
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
		return new Translate ();
	}

//enh:end

}
