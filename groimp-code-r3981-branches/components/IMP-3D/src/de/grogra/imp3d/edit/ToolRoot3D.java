
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d.edit;

import java.beans.*;
import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.imp.edit.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.vecmath.*;

/**
 * This class is the base class for roots of tool graphs which
 * manipulate 3D objects (the latter are called tool targets).
 * It defines three coordinate systems:
 * <ol>
 * <li>The <i>parent coordinate system</i> is the coordinate system
 * of the parent object of the tool target.
 * <li>The <i>target coordinate system</i> is the coordinate system
 * of the tool target. It is computed by the method
 * {@link #computeTargetTransformation(Matrix4d, Matrix4d)} based
 * on the global transformation of the parent coordinate system.
 * <li>The <i>tool coordinate system</i> is the coordinate system
 * of the tool graph. Normally, the translational and rotational
 * components of its transformation matrix are equal to those of the
 * target coordinate system. However, the scaling factors differ to ensure
 * a constant size of the tool handles (independent of the size of the
 * tool target).
 * </ol>
 * This class implements <code>Transformation</code>
 * such that its local coordinates correspond to the tool coordinate system. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class ToolRoot3D extends ToolRoot
	implements Transformation, de.grogra.util.EventListener
{
	private Matrix4d parentTransformation = new Matrix4d ();
	private Matrix4d targetTransformation = new Matrix4d ();
	private Matrix4d toolTransformation = new Matrix4d ();
	private Matrix4d toolAdjust = new Matrix4d ();
	private boolean valid = false;


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (ToolRoot3D.class);
		initType ();
		$TYPE.validate ();
	}

//enh:end


	@Override
	protected boolean initialize (de.grogra.imp.edit.Tool tool)
	{
		boolean b = super.initialize (tool);
		if (b)
		{
			tool.getView ().addEventListener (this);
		}
		return b;
	}


	public void dispose ()
	{
		tool.getView ().removeEventListener (this);
	}


	/**
	 * This method implements the <code>EventListener</code>-interface.
	 * If this method is overriden, it has to be invoked by the
	 * overriding method.
	 * 
	 * @param e an event
	 */
	public void eventOccured (java.util.EventObject e)
	{
		if ((e instanceof PropertyChangeEvent)
			&& (e.getSource () == tool.getView ())
			&& "camera".equals (((PropertyChangeEvent) e).getPropertyName ()))
		{
			invalidate ();
		}
	}


	@Override
	protected void attributeChanged (Attribute[] a)
	{
		if (GlobalTransformation.ATTRIBUTE.isContained (a))
		{
			invalidate ();
		}
	}


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		Math2.mulAffine (out, in, getToolTransformation ());
	}


	public void postTransform
		(Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre, GraphState gs)
	{
		out.set (in);
	}


	/**
	 * This method returns the transformation matrix from parent
	 * coordinates to world coordinates.
	 * 
	 * @see ToolRoot3D
	 * @return the transformation matrix of the parent coordinate system
	 */
	protected final Matrix4d getParentTransformation ()
	{
		validate ();
		return parentTransformation;
	}


	/**
	 * This method returns the transformation matrix from target
	 * coordinates to world coordinates.
	 * 
	 * @see ToolRoot3D
	 * @return the transformation matrix of the target coordinate system
	 */
	protected final Matrix4d getTargetTransformation ()
	{
		validate ();
		return targetTransformation;
	}


	/**
	 * This method returns the transformation matrix from tool
	 * coordinates to world coordinates.
	 * 
	 * @see ToolRoot3D
	 * @return the transformation matrix of the tool coordinate system
	 */
	protected final Matrix4d getToolTransformation ()
	{
		validate ();
		return toolTransformation;
	}


	/**
	 * This method returns the adjustment matrix from tool coordinates
	 * to target coordinates.
	 * 
	 * @return the adjustment between tool and target coordinates
	 */
	protected final Matrix4d getAdjustment ()
	{
		validate ();
		return toolAdjust;
	}


	private void invalidate ()
	{
		valid = false;
//		tool.getGraphState ().fireAttributeChanged
//			(getToolTarget (), isTargetNode (), Attributes.TRANSFORMATION, null);
	}


	private void validate ()
	{
		if (valid)
		{
			return;
		}
		GlobalTransformation.get
			(getToolTarget (), isTargetNode (), tool.getGraphState (), true);
		GlobalTransformation.getParentValue
			(getToolTarget (), isTargetNode (), tool.getGraphState (), true)
			.get (parentTransformation);
		computeTargetTransformation
			(targetTransformation, parentTransformation);
		toolTransformation.set (targetTransformation);
		adjustToolTransformation
			(toolTransformation,
			 ((View3D) tool.getView ()).getCanvasCamera ());
		Math2.makeAffine (toolAdjust);
		Math2.invertAffine (targetTransformation, toolAdjust);
		Math2.mulAffine (toolAdjust, toolAdjust, toolTransformation);
		valid = true;
	}


	/**
	 * This method is invoked to compute the transformation matrix
	 * from target coordinates to world coordinates, based on the
	 * transformation matrix from parent coordinates to world
	 * coordinates.  
	 * 
	 * @param t the computed matrix has to be placed in here
	 * @param parent the global transformation of parent coordinates 
	 */
	protected abstract void computeTargetTransformation
		(Matrix4d t, Matrix4d parent);


	/**
	 * This method is invoked to compute an adjustment of the tool
	 * coordinates system. The invoker sets <code>t</code> to the
	 * transformation matrix from target coordinates to world
	 * coordinates. Implementations of this method may adjust
	 * <code>t</code>, e.g., they may set a certain scaling factor
	 * between tool coordinates and camera coordinates in order to
	 * ensure a fixed size of tool nodes on the 3D view.
	 * 
	 * @param t the tool transformation to be adjusted
	 * @param c the camera of the tool's 3D view
	 */
	protected void adjustToolTransformation (Matrix4d t, CameraBase c)
	{
	}


	protected static final void adjustScale (Matrix4d t, CameraBase c,
											 float scale)
	{
		Matrix3d mat = new Matrix3d ();
		Matrix3d rot = new Matrix3d ();
		t.getRotationScale (mat);
		Math2.decomposeQR (mat, rot);
		mat.set (scale / c.getScaleAt (t.m03, t.m13, t.m23));
		rot.mul (mat);
		t.setRotationScale (rot);
	}

}
