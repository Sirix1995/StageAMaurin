
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

package de.grogra.imp3d;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.Path;
import de.grogra.graph.impl.SharedObjectNode;
import de.grogra.imp.NavigatorFactory;
import de.grogra.imp.PickList;
import de.grogra.imp.View;
import de.grogra.imp.ViewComponent;
import de.grogra.imp.ViewEventHandler;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp.registry.ViewComponentFactory;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.imp3d.shading.Light;
import de.grogra.persistence.IndirectField;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;
import de.grogra.persistence.Transaction;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemCriterion;
import de.grogra.pf.registry.Option;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.ObjectSelection;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.pf.ui.registry.PanelFactory;
import de.grogra.util.Map;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Matrix34d;

/**
 * A <code>View3D</code> is a <code>View</code> which displays a graph
 * as a 3D structure.
 *
 * @author Ole Kniemeyer
 */
public class View3D extends View implements SharedObjectReference, ViewConfig3D
{
	public static final String INITIAL_CAMERA = "initial-camera";

	//enh:sco

	public static final IOFlavor FLAVOR = IOFlavor.valueOf (View3D.class);

	static final String DISPLAY_PATH = "/ui/viewcomponent3dselection";
	static final String TOOL_PATH = "/ui/tools/3d";

	public static final UIProperty DISPLAY = UIProperty.getOrCreate (DISPLAY_PATH, UIProperty.PANEL);
	public static final UIProperty TOOL = UIProperty.getOrCreate (TOOL_PATH, UIProperty.WORKBENCH);


	Camera camera;
	//enh:field type=Camera.$TYPE getter setmethod=setCamera

	Navigator3DFactory navigator;
	//enh:field getter setter

	protected CanvasCamera canvasCam = new CanvasCamera ();

	private PickRayVisitor pickVisitor;

	private Vector3f extentCenter = new Vector3f ();
	private float extentRadius = 1;

	private final SceneListener sceneListener = new SceneListener ();


	public static Selectable getSelectableCamera (Context ctx)
	{
		final View3D v = (View3D) get (ctx);
		return new Selectable ()
		{
			public Selection toSelection (Context c)
			{
				return new ObjectSelection
					(c, v, new PersistenceField[] {camera$FIELD},
					 null, null, null, null)
				{
					@Override
					protected Object replaceValue (PersistenceField field, Object value)
					{
						SharedObjectProvider sop;
						if ((field == camera$FIELD)
							&& ((sop = ((Camera) value).getProvider ()) != null))
						{
							if (sop instanceof SharedObjectNode)
							{
								return value;
							}
							try
							{
								return ManageableType.cloneObject (null, value, true, true);
							}
							catch (CloneNotSupportedException e)
							{
								e.printStackTrace ();
							}
							return Camera.createPerspective ();
						}
						return value;
					}

					@Override
					protected void valueChanged (PersistenceField field, Object value)
					{
						if (field.overlaps (null, camera$FIELD, null))
						{
							v.fireCameraChanged ();
						}
					}
				};
			}
		};
	}


	public View3D ()
	{
		setCamera (Camera.createPerspective ());
	}


	@Override
	public IOFlavor getFlavor ()
	{
		return FLAVOR;
	}


	@Override
	protected UIProperty getToolProperty ()
	{
		return TOOL;
	}


	@Override
	protected ViewEventHandler createEventHandler ()
	{
		return new ViewEventHandler (this, false)
		{
			private final Point3d origin = new Point3d ();
			private final Vector3d direction = new Vector3d ();

			private void getPoint (MouseEvent event)
			{
				getCanvasCamera ().getRay (event.getX (), event.getY (), origin, direction);
			}

			@Override
			protected ClickEvent createClickEvent (MouseEvent e)
			{
				return new ClickEvent ();
			}

			@Override
			protected DragEvent createDragEvent (MouseEvent e)
			{
				getPoint (e);
				return new DragEvent3D (origin, direction);
			}

			protected NavigatorFactory getNavigatorFactory ()
			{
			    return navigator;
			}
		};
	}


	@Override
	protected void initialize (Panel panel, Map params)
	{
		super.initialize (panel, params);
		Object ic = params.get (INITIAL_CAMERA, null);
		if (ic instanceof Camera)
		{
			setCamera ((Camera) ic);
		}
	}


	@Override
	protected void installImpl ()
	{
		pickVisitor = new PickRayVisitor ();
		sceneListener.install (getGraph ());
		if (getViewComponent () == null)
		{
			ViewComponentFactory f = ViewComponentFactory.get (this, DISPLAY_PATH);
			if (f != null)
			{
				setViewComponent (f.createViewComponent (this));
			}
			DISPLAY.setValue (this, f);
			DISPLAY.addPropertyListener (this, this);
		}
		else
		{
			repaint (ViewComponent.ALL | ViewComponent.CHANGED);
		}
	}


	@Override
	protected void uninstallImpl ()
	{
		DISPLAY.removePropertyListener (this, this);
		setCamera (null);
		sceneListener.remove (getGraph ());
	}


	private final Point3d pickOrigin = new Point3d ();
	private final Vector3d pickDirection = new Vector3d ();

	@Override
	public final void pick (int x, int y, PickList list)
	{
		canvasCam.getRay (x, y, pickOrigin, pickDirection);
		pickVisitor.pick (this, x, y, pickOrigin, pickDirection, list);
	}


	@Override
	public boolean isToolGraph (Graph graph)
	{
		return graph == de.grogra.graph.impl.GraphManager.STATIC;
	}


	public float estimateScaleAt (float x, float y, float z, Matrix4d t)
	{
		return canvasCam.getScaleAt
			((float) t.m20 * x + (float) t.m21 * y + (float) t.m22 * z
			 + (float) t.m23)
			* (float) Math.sqrt (Math2.estimateScaleSquared (t));
	}


	public float estimateScaleAt (Tuple3d p, Matrix4d t)
	{
		return canvasCam.getScaleAt
			((float) (t.m20 * p.x + t.m21 * p.y + t.m22 * p.z + t.m23))
			* (float) Math.sqrt (Math2.estimateScaleSquared (t));
	}


	public float estimateScaleAt (Tuple3f p, Matrix4d t)
	{
		return canvasCam.getScaleAt
			((float) t.m20 * p.x + (float) t.m21 * p.y + (float) t.m22 * p.z
			 + (float) t.m23)
			* (float) Math.sqrt (Math2.estimateScaleSquared (t));
	}


	public CanvasCamera getCanvasCamera ()
	{
		return canvasCam;
	}


	@Override
	public void eventOccured (java.util.EventObject e)
	{
		if (e instanceof UIPropertyEditEvent)
		{
			UIPropertyEditEvent pe = (UIPropertyEditEvent) e;
			Object o = pe.getNewValue ();
/*			if (pe.getProperty () == CAMERA)
			{
				if (o instanceof ObjectItem)
				{
					CameraBase c = (CameraBase) ((ObjectItem) o).getObject ();
					if (c != null)
					{
						if ((((ObjectItem) o).getRegistry ()
							 == getWorkbench ().getRegistry ())
							&& (c instanceof Camera))
						{
							setCamera ((Camera) c, true);
						}
						else
						{
							Camera ci = new Camera ();
							ci.configure (c, null);
							setCamera (ci, true);
							CAMERA.setValue
								(this, Item.resolveItem
								 (getWorkbench (), CAMERA_PATH + "/view"));
						}
						return;
					}
				}
				Camera ci = new Camera ();
				ci.configure (camera, null);
				setCamera (ci, true);
				return;
			}
			else*/ if (pe.getProperty () == DISPLAY)
			{
				if (o instanceof ViewComponentFactory)
				{
					setViewComponent (((ViewComponentFactory) o).createViewComponent (this));
				}
				return;
			}
		}
		super.eventOccured (e);
	}


	public void dollyOrZoom (int dx, int dy)
	{
		Camera c = getCamera ();
		if (c.getProjection () instanceof ParallelProjection)
		{
			zoom (dx, dy);
		}
		else
		{
			dolly (dx, dy);
		}
	}


	public void dolly (int dx, int dy)
	{
		Camera c = getCamera ();
		Matrix4d m = new Matrix4d (c.getWorldToViewTransformation ());
		double delta = Math.max (2 * getVisualEpsilon (), 0.0005 * Math.abs (m.m23));
		m.m23 += (dx + dy) * delta;
		setCameraTransformation (m);
	}



	public void move (int dx, int dy)
	{
		Camera c = getCamera ();
		Matrix4d m = new Matrix4d (c.getWorldToViewTransformation ());
		double delta = Math.max (2 * getVisualEpsilon (), 0.0005 * Math.abs (m.m23));
		m.m03 += dx * delta;
		m.m13 -= dy * delta;
		setCameraTransformation (m);
	}


	public void rotate (int dx, int dy)
	{
		ViewSelection s = ViewSelection.get (this);
		Vector3d center = new Vector3d ();
		if (s != null)
		{
			ViewSelection.Entry[] e = s.getAll (ViewSelection.SELECTED);
			if ((e != null) && (e.length > 0))
			{
				Path p = e[0].getPath ();
				int l = GraphUtils.lastIndexOfTree (p, getWorkbenchGraphState ());
				if (l >= 0)
				{
					Matrix34d m = GlobalTransformation.get
						(p.getObject (l), (l & 1) == 0,
						 getWorkbenchGraphState (), false);
					m.get (center);
				}
			}
		}
		Camera c = getCamera ();
		Matrix4d m = new Matrix4d (c.getWorldToViewTransformation ());
		Matrix4d rot = new Matrix4d ();
		Point3d trans = new Point3d (center);
		m.transform (trans);
		rot.rotZ (dx * -0.005);
		m.mul (rot);
		rot.rotX (dy * -0.005);
		m.mul (rot, m);
		m.transform (center);
		center.sub (trans, center);
		m.setTranslation (center);
		setCameraTransformation (m);
	}


	void zoom (int dx, int dy)
	{
		Camera c = getCamera ();
		if (c instanceof Camera)
		{
			Projection p;
			try
			{
				p = (Projection) de.grogra.persistence.ManageableType
					.cloneObject (null, ((Camera) c).getProjection (), true, true);
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace ();
				return;
			}
			if (p instanceof ParallelProjection)
			{
				ParallelProjection pp = (ParallelProjection) p;
				pp.setWidth
					(pp.getWidth () * (float) Math.exp (-0.005f * (dy + dx)));
			}
			else if (p instanceof PerspectiveProjection)
			{
				PerspectiveProjection pp = (PerspectiveProjection) p;
				pp.setFieldOfView
					(Math.min (0.99f * (float) Math.PI, pp.getFieldOfView ()
					 * (float) Math.exp (-0.01f * (dy + dx))));
			}
			else
			{
				return;
			}
			setCameraField (Camera.projection$FIELD, p);
		}
	}


	void fitCamera ()
	{
		if (extentRadius != extentRadius)
		{
			WireframeCanvas.fitCamera (this);
		}
		else
		{
			fitCamera (extentCenter, extentRadius);
		}
	}


	void fitCamera (Vector3f extentCenter, float extentRadius)
	{
		if (!((extentRadius > 0) && (extentRadius < Float.MAX_VALUE / 2)))
		{
			return;
		}
		if (!(extentCenter.lengthSquared () < Float.MAX_VALUE / 2))
		{
			return;
		}
		Camera c = getCamera ();
		Projection p = c.getProjection ();
		Vector3d t = new Vector3d (extentCenter);
		if (p instanceof PerspectiveProjection)
		{
			setCameraField (Camera.projection$FIELD.concat (PerspectiveProjection.fieldOfView$FIELD), new Float ((float) (Math.PI / 3)));
			t.z += 2 * extentRadius;
		}
		else if (p instanceof ParallelProjection)
		{
			setCameraField (Camera.projection$FIELD.concat (ParallelProjection.width$FIELD), new Float (2 * extentRadius));
			t.z += 2 * extentRadius;
		}
		else
		{
			return;
		}
		t.negate ();
		Matrix4d m = new Matrix4d ();
		m.set (t);
		m.mul (c.getTransformation ());
		setCameraField (Camera.transformation$FIELD, m);
	}


	private void setCamera (Camera c)
	{
		camera = c;
		canvasCam.camera = c;
	}


	public void repaint ()
	{
//		getViewComponent().repaint (ViewComponent.ALL | ViewComponent.CHANGED);
//		ViewComponent vc = getViewComponent();
//		if(vc instanceof GLDisplay) {
//			Object o = ((GLDisplay)vc).getComponent();
//			if(o instanceof JPanel) {
//				((JPanel)o).repaint();
//			}
//		}
		repaint (ViewComponent.ALL | ViewComponent.CHANGED);
	}
	
	void fireCameraChangedImpl ()
	{
		listeners.eventOccured
			(new PropertyChangeEvent (this, "camera", null, camera));
		repaint (ViewComponent.ALL | ViewComponent.CHANGED);
	}


	private static final Command CAMERA_CHANGED = new Command ()
	{
		public void run (Object info, Context context)
		{
			((View3D) context).fireCameraChangedImpl ();
		}

		public String getCommandName ()
		{
			return null;
		}
	};


	void fireCameraChanged ()
	{
		getWorkbench ().getJobManager ().runLater
			(CAMERA_CHANGED, null, this, JobManager.UPDATE_PRIORITY);
	}


	public void sharedObjectModified (Shareable object, Transaction t)
	{
		if (object == camera)
		{
			fireCameraChanged ();
		}
	}

	
	public void setCameraTransformation (Matrix4d transformation)
	{
		setCameraField (Camera.transformation$FIELD, transformation);
	}

	void setCameraField (final PersistenceField field, final Object value)
	{
		SharedObjectProvider p = camera.getProvider ();
		if (p == null)
		{
			field.set (camera, null, value, null);
			fireCameraChanged ();
		}
		else if (p instanceof SharedObjectNode)
		{
			final SharedObjectNode sn = (SharedObjectNode) p;
			UI.executeLockedly (sn.getGraph (), true, new Command ()
			{
				public String getCommandName ()
				{
					return null;
				}

				public void run (Object info, Context c)
				{
					IndirectField.concat (SharedObjectNode.object$FIELD, field).set
						(sn, null, value, sn.getGraph ().getActiveTransaction ());
				}
			}, null, this, JobManager.ACTION_FLAGS);
		}
	}


	public static View3D getDefaultView (Context ctx)
	{
		Window w = ctx.getWindow ();
		return (w != null) ? (View3D) w.getPanel ("/ui/panels/3d/defaultview") : null;
	}


	public static ViewConfig3D withCamera (final ViewConfig3D v, final Camera c)
	{
		return new ViewConfig3D ()
		{
			public Camera getCamera ()
			{
				return c;
			}

			public Light getDefaultLight (Matrix4d lightToWorld)
			{
				return v.getDefaultLight (lightToWorld);
			}

			public float getEpsilon ()
			{
				return v.getEpsilon ();
			}

			public Graph getGraph ()
			{
				return v.getGraph ();
			}

			public Workbench getWorkbench ()
			{
				return v.getWorkbench ();
			}

			public boolean isInVisibleLayer (Object o, boolean asNode, GraphState gs)
			{
				return v.isInVisibleLayer (o, asNode, gs);
			}
		};
	}

	public static ViewConfig3D getDefaultViewConfig (Context ctx)
	{
		ViewConfig3D v = getDefaultView (ctx);
		if (v != null)
		{
			return v;
		}

		final Workbench w = ctx.getWorkbench ();

		class Helper implements ViewConfig3D, ItemCriterion
		{
			private View3D view;

			public Camera getCamera ()
			{
				return view.getCamera ();
			}

			public Light getDefaultLight (Matrix4d lightToWorld)
			{
				return View3D.getDefaultLight (getCamera (), lightToWorld);
			}

			public float getEpsilon ()
			{
				return view.getEpsilon ();
			}

			public Graph getGraph ()
			{
				return w.getRegistry ().getProjectGraph ();
			}

			public Workbench getWorkbench ()
			{
				return w;
			}

			public boolean isInVisibleLayer (Object o, boolean asNode, GraphState gs)
			{
				return view.isInVisibleLayer (o, asNode, gs);
			}


			public String getRootDirectory ()
			{
				return null;
			}

			public boolean isFulfilled (Item item, Object info)
			{
				return (item instanceof Option) && item.hasName ("view")
					&& (item.getAxisParent () instanceof PanelFactory)
					&& "/ui/panels/3d/defaultview".equals (((PanelFactory) item.getAxisParent ()).getFactorySource ());
			}
		}

		Helper h = new Helper ();
		Item i = Item.findFirst (ctx.getWorkbench (), "/workbench/state", h, null, false);
		if (i == null)
		{
			return null;
		}
		h.view = (View3D) ((Option) i).getObject ();
		return h;
	}

	public Light getDefaultLight (Matrix4d lightToWorld)
	{
		return getDefaultLight (getCamera (), lightToWorld);
	}

	/**
	 * Computes a default light to use when a scene contains no lights.
	 *
	 * @param camera used camera
	 * @param lightToWorld the computed transformation for the light is placed in here
	 * @return a default light
	 */
	public static Light getDefaultLight (Camera camera, Matrix4d lightToWorld)
	{
		Math2.makeAffine (lightToWorld);
		Math2.invertAffine (camera.getWorldToViewTransformation (), lightToWorld);
		Point3d p = new Point3d (-3e3, 3e3, 1e4);
		Vector3d v = new Vector3d (p);
		v.negate ();
		lightToWorld.transform (p);
		lightToWorld.transform (v);
		Matrix3d m = new Matrix3d ();
		Math2.getOrthogonalBasis (v, m, true);
		lightToWorld.set (m);
		lightToWorld.m03 = p.x;
		lightToWorld.m13 = p.y;
		lightToWorld.m23 = p.z;
		DirectionalLight light = new DirectionalLight ();
		light.setShadowless (true);
		light.getColor ().set (1, 1, 1);
		return light;
	}

	/**
	 * This callback method should be invoked by the 3D-{@link ViewComponent}
	 * which displays the scene in order to inform this <code>View3D</code> about
	 * the geometrical extent of the whole scene. <code>center</code> and <code>radius</code>
	 * should define a bounding sphere (or at least an approximation thereof) of all
	 * visible finite objects in view coordinates, i.e., after
	 * {@link Camera#getWorldToViewTransformation()} has been applied.
	 *
	 * @param center center of bounding sphere
	 * @param radius radius of bounding sphere
	 */
	public void setExtent (Tuple3f center, float radius)
	{
		if (center != null)
		{
			extentCenter.set (center);
			extentRadius = radius;
		}
		else
		{
			extentRadius = Float.NaN;
		}
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field camera$FIELD;
	public static final Type.Field navigator$FIELD;

	public static class Type extends View.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (View3D representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, View.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = View.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = View.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((View3D) o).setCamera ((Camera) value);
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((View3D) o).navigator = (Navigator3DFactory) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((View3D) o).getCamera ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((View3D) o).getNavigator ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new View3D ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (View3D.class);
		camera$FIELD = Type._addManagedField ($TYPE, "camera", 0 | Type.Field.SCO, Camera.$TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		navigator$FIELD = Type._addManagedField ($TYPE, "navigator", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Navigator3DFactory.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public Camera getCamera ()
	{
		return camera;
	}

	public Navigator3DFactory getNavigator ()
	{
		return navigator;
	}

	public void setNavigator (Navigator3DFactory value)
	{
		navigator$FIELD.setObject (this, value);
	}

//enh:end

}
