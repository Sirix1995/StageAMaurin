
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

package de.grogra.imp2d.edit;

import java.beans.*;
import javax.vecmath.Matrix3d;
import de.grogra.graph.*;
import de.grogra.imp.edit.*;
import de.grogra.imp2d.objects.*;
import de.grogra.imp2d.objects.Attributes;
import de.grogra.vecmath.*;

public abstract class ToolRoot2D extends ToolRoot
	implements Transformation, de.grogra.util.EventListener
{
	private Matrix3d parentTransformation = new Matrix3d ();
	private Matrix3d targetTransformation = new Matrix3d ();
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
		$TYPE = new NType (ToolRoot2D.class);
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


	public void eventOccured (java.util.EventObject e)
	{
		if ((e instanceof PropertyChangeEvent)
			&& (e.getSource () == tool.getView ())
			&& "projection".equals (((PropertyChangeEvent) e)
									.getPropertyName ()))
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


	public void preTransform (Object object, boolean asNode, Matrix3d in, Matrix3d out, GraphState gs)
	{
		Math2.mulAffine (out, in, getTargetTransformation ());
	}


	public void postTransform
		(Object object, boolean asNode, Matrix3d in, Matrix3d out, Matrix3d pre, GraphState gs)
	{
		out.set (in);
	}


	public final Matrix3d getParentTransformation ()
	{
		validate ();
		return parentTransformation;
	}


	public final Matrix3d getTargetTransformation ()
	{
		validate ();
		return targetTransformation;
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
		GlobalTransformation.get (getToolTarget (), isTargetNode (), tool.getGraphState (), true);
		parentTransformation.set
			(GlobalTransformation.getParentValue
			 (getToolTarget (), isTargetNode (), tool.getGraphState (), true));
		calculateTargetTransformation
			(targetTransformation, parentTransformation);
		valid = true;
	}


	protected abstract void calculateTargetTransformation
		(Matrix3d t, Matrix3d parent);

}
