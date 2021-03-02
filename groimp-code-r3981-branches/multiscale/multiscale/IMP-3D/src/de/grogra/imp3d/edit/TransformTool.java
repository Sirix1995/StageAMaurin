package de.grogra.imp3d.edit;

import javax.vecmath.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.math.*;
import de.grogra.vecmath.*;

/**
 * This class is the base class for tool roots which manipulate
 * transformable objects (objects having the attribute
 * {@link de.grogra.imp3d.objects.Attributes#TRANSFORM}). 
 * The tool coordinate system (see {@link de.grogra.imp3d.edit.ToolRoot3D}
 * is adjusted such that tool nodes of size 1 have a size of about 150
 * pixels on a 3D view.
 * 
 * @author Ole Kniemeyer
 */
public abstract class TransformTool extends ToolRoot3D
{
	/**
	 * The local transformation matrix of the
	 * transformable object.
	 */
	protected Matrix4d local = new Matrix4d ();


	public TransformTool ()
	{
		local.setIdentity ();
	}


	/**
	 * This implementation sets the target coordinate system such that is
	 * is transformed to the parent coordinate system by the
	 * transformation of the transformable tool target.
	 * 
	 * @param t the computed matrix of the target coordinate system
	 * @param parent the provided matrix of the parent coordinate system
	 */
	@Override
	protected void computeTargetTransformation (Matrix4d t, Matrix4d parent)
	{
		local.setIdentity ();
		Transform3D x = (Transform3D) tool.getObjectOfObject (Attributes.TRANSFORM, null);
		if (x != null)
		{
			x.transform (local, local);
		}
		Math2.mulAffine (t, parent, local);
	}


	/**
	 * This utility method may be used by subclasses in order to set
	 * the local transformation of the transformable tool target
	 * to the given matrix <code>t</code>.
	 * 
	 * @param t the new local transformation of the target object
	 */
	protected void setTargetTransform (Matrix4d t)
	{
		tool.setObjectOfObject (Attributes.TRANSFORM, IMP3D.toTransform (t));
	}


	@Override
	protected void adjustToolTransformation (Matrix4d t, CameraBase c)
	{
		adjustScale (t, c, 150);
	}


	@Override
	protected boolean initialize (de.grogra.imp.edit.Tool tool)
	{
		return super.initialize (tool) && tool.isWritable (Attributes.TRANSFORM);
	}

}
