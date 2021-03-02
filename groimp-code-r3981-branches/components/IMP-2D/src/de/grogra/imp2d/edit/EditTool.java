package de.grogra.imp2d.edit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EventObject;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;

import de.grogra.imp.PickList;
import de.grogra.imp.ViewEventHandlerIF;
import de.grogra.imp.edit.Tool;
import de.grogra.imp2d.AWTCanvas2D;
import de.grogra.imp2d.AWTCanvas2DIF;
import de.grogra.imp2d.AWTDrawable;
import de.grogra.imp2d.DragEvent2D;
import de.grogra.imp2d.Pickable;
import de.grogra.imp2d.View2D;
import de.grogra.imp2d.objects.Attributes;
import de.grogra.imp2d.objects.Transformation;
import de.grogra.math.Pool;
import de.grogra.math.TMatrix3d;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.event.EditEvent;
import de.grogra.vecmath.Math2;

public class EditTool extends ToolRoot2D
	implements AWTDrawable, Pickable, Command
{
	public static final int PICK_NOTHING = -2;
	public static final int PICK_CENTER = -1;
	
	public static final String PATH = "/ui/tools/2d/edit";

	public final Pool pool = new Pool ();
	private int pickId = PICK_NOTHING;
	private Object object;


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new EditTool ());
		initType ();
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
		return new EditTool ();
	}

//enh:end


	public Editable getEditable ()
	{
		Object o = tool.getObjectOfObject (Editable.ATTRIBUTE, null);
		return (o instanceof Editable) ? (Editable) o : null;
	}

	
	public de.grogra.imp.edit.Tool getTool ()
	{
		return tool;
	}


	public de.grogra.graph.GraphState getGraphState ()
	{
		return tool.getGraphState ();
	}


	@Override
	protected boolean initialize (Tool tool)
	{
		boolean b = super.initialize (tool) && (getEditable () != null);
		if (b)
		{
			ViewEventHandlerIF h = tool.getView ().getEventHandler ();
			if (h != null)
			{
				h.updateHighlight ();
			}
		}
		return b;
	}


	@Override
	protected void calculateTargetTransformation (Matrix3d t, Matrix3d parent)
	{
		Transformation x = (Transformation) tool.getObjectOfObject
			(Attributes.TRANSFORMATION, null);
		if (x != null)
		{
			x.preTransform (getToolTarget (), isTargetNode (), parent, t, tool.getGraphState ());
		}
		else
		{
			t.set (parent);
		}
	}


	@Override
	public void eventOccured (EventObject e)
	{
		super.eventOccured (e);
		if (e instanceof EditEvent)
		{
			if(e instanceof DragEvent2D && object==null) {
				DragEvent2D d = (DragEvent2D) e;
				Point2d p = new Point2d (d.point);
				Math2.invTransformPoint (getTargetTransformation (), p);
				setObject (p);
				setPickId (-1);
			}
			
			executeWithWriteLock (this, (EditEvent) e);
		}
	}

	
	@Override
	public String getCommandName ()
	{
		return null;
	}

	
	@Override
	public void run (Object o, Context c)
	{
		getEditable ().toolEventOccured ((EditEvent) c, this);
	}


	@Override
	public void pick (Object object, boolean asNode, Point2d point,
					  Matrix3d transformation, PickList list)
	{
		pickId = PICK_NOTHING;
		getEditable ().pickTool (point, transformation, list, this);
	}


	public void setPickId (int pickId)
	{
		this.pickId = pickId;
	}


	public int getPickId ()
	{
		return pickId;
	}


	public void setObject (Object object)
	{
		this.object = object;
	}


	public Object getObject ()
	{
		return object;
	}


	@Override
	public void draw (Object object, boolean asNode, AWTCanvas2DIF canvas,
					  Matrix3d transformation, int state)
	{
		getEditable ().drawTool (canvas, transformation, this);
	}


	public boolean pickHandle (Matrix3d transformation,
							   PickList list, double x, double y, int id)
	{
		if (list.getView () == null)
		{
			return false;
		}
		Point2d p = list.p2d0, q = list.p2d1;
		p.set (x, y);
		Math2.transformPoint (transformation, p);
		Math2.transformPoint
			(((View2D) list.getView ()).getCanvasTransformation (), p);
		q.set (list.getViewX (), list.getViewY ());
		if (q.distanceSquared (p) < 50)
		{
			list.add (Short.MIN_VALUE);
			setPickId (id);
			return true;
		}
		return false;
	}


	public static void drawHandle (AWTCanvas2DIF c, Matrix3d t, double x, double y)
	{
		int hx = Math.round ((float) (t.m00 * x + t.m01 * y + t.m02));
		int hy = Math.round ((float) (t.m10 * x + t.m11 * y + t.m12));
		c.resetGraphicsTransform ();
		Graphics2D g = c.getGraphics ();
		g.setStroke (AWTCanvas2D.STROKE_1);
		g.setColor (Color.BLACK);
		g.drawRect (hx - 3, hy - 3, 7, 7);
		g.setColor (Color.WHITE);
		g.fillRect (hx - 2, hy - 2, 6, 6);
	}


	public void setTransform (Matrix3d t)
	{
		tool.setObjectOfObject
			(Attributes.TRANSFORM, TMatrix3d.createTransform (t));
	}

}
