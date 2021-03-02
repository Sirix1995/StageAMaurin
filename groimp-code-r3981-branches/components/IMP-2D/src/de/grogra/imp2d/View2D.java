
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

package de.grogra.imp2d;

import java.awt.event.MouseEvent;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.NavigatorFactory;
import de.grogra.imp.PickList;
import de.grogra.imp.View;
import de.grogra.imp.ViewComponent;
import de.grogra.imp.ViewEventHandler;
import de.grogra.imp.ViewEventHandlerIF;
import de.grogra.imp.registry.ViewComponentFactory;
import de.grogra.imp2d.layout.GeneralPurposeLayout;
import de.grogra.imp2d.layout.Layout;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.edit.ObjectSelection;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;

public class View2D extends View2DIF
{
	//enh:sco

	public static final IOFlavor FLAVOR = IOFlavor.valueOf (View2D.class);

	static final String DISPLAY_PATH = "/ui/viewcomponent2dselection";
	static final String TOOL_PATH = "/ui/tools/2d";

	public static final UIProperty DISPLAY = UIProperty.getOrCreate (DISPLAY_PATH, UIProperty.PANEL);
	public static final UIProperty TOOL = UIProperty.getOrCreate (TOOL_PATH, UIProperty.PANEL);

	
	Matrix3d transformation;
	//enh:field type=de.grogra.math.Matrix3dType.$TYPE setmethod=setTransformation
	
	Layout layout;
	//enh:field


	Matrix3d canvasTransformation = new Matrix3d ();

	private PickRayVisitor pickVisitor;

	private final SceneListener sceneListener = new SceneListener ()
	{
		@Override
		public void endChange (GraphState gs)
		{
			super.endChange (gs);
			//if (hasChanged)
			//{
			//	layout ();
			//}
		}
	};

	private final Command layoutCommand = new Command ()
	{
		@Override
		public void run (Object info, Context c)
		{
			if (info == null)
			{
				if (layout != null)
				{
					UI.executeLockedly
						(getGraph (), true, this, layout, c, JobManager.UPDATE_FLAGS);
				}
				else
				{
					layouting = false;
				}
			}
			else if (info == this)
			{
				layouting = false;
			}
			else if (info instanceof Layout.Algorithm[])
			{
				UI.executeLockedly (getGraph (), true, this, ((Layout.Algorithm[]) info)[0],
									c, JobManager.UPDATE_FLAGS);
			}
			else
			{
					Layout.Algorithm a = (info instanceof Layout.Algorithm) ? (Layout.Algorithm) info : null;
					a = layout.invoke ((View2D) c, a, GraphManager.MAIN_GRAPH);
					
				
				if (a != null)
				{
					getWorkbench ().getJobManager ().runLater (200, this, new Layout.Algorithm[] {a}, c);
				}
				else
				{
					UI.getJobManager (c).runLater (this, this, c, JobManager.RENDER_FLAGS);
				}
			}
 		}
	
		@Override
		public String getCommandName ()
		{
			return null;
		}
	};


	void layout ()
	{
		if (!layouting)
		{
			layouting = true;
			getWorkbench ().getJobManager ().runLater
				(layoutCommand, null, View2D.this, JobManager.UPDATE_FLAGS);
		}
	}

	
	public View2D ()
	{
		transformation = new Matrix3d ();
		transformation.setIdentity ();
		transformation.m00 = 100;
		transformation.m11 = -100;
		transformation.m12 = 100;
		transformation.m22 = 1;
		layout = new GeneralPurposeLayout();
		//layout.setTransformationSteps(10);
		//layout.setRedraw (true);
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
	protected ViewEventHandlerIF createEventHandler ()
	{
		return new ViewEventHandler (this, true)
		{
			private final Point2d point = new Point2d ();

			private void getPoint (MouseEvent event)
			{
				point.set (event.getX (), event.getY ());
				de.grogra.vecmath.Math2.invTransformPoint
					(getCanvasTransformation (), point);
			}

			@Override
			public ClickEvent createClickEvent (MouseEvent e)
			{
				getPoint (e);
				return new ClickEvent2D (point);
			}

			@Override
			public DragEvent createDragEvent (MouseEvent e)
			{
				getPoint (e);
				return new DragEvent2D (point);
			}

			@Override
			public DragEvent createDragEvent (Point2d point)
			{
				return new DragEvent2D (point);
			}

			@Override
			protected NavigatorFactory getNavigatorFactory ()
			{
			    return new Navigator2DFactory ();
			}

		};
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
			
				layout ();
				repaint (ViewComponent.ALL | ViewComponent.CHANGED);
			
		}
		else
		{
			layout ();
		}
	}


	@Override
	protected void uninstallImpl ()
	{
		sceneListener.remove (getGraph ());
	}
	
	public static Selectable getSelectableLayout (Context ctx)
	{
		final View2D v = (View2D) get (ctx);
		return new Selectable ()
		{
			@Override
			public Selection toSelection (Context c)
			{
				return new ObjectSelection
					(c, v, new PersistenceField[] {layout$FIELD},
					 null, null, null, null)
				{
					@Override
					protected void valueChanged (PersistenceField field, Object value)
					{
						//if (field.overlaps (null, layout$FIELD, null))
						//{
						//	v.layout ();
						//}
					}
				};
			}
		};
	}
	

	@Override
	public void pick (int x, int y, PickList list)
	{
		pickVisitor.pick (this, x, y, list);
	}

	
	@Override
	public boolean isToolGraph (Graph graph)
	{
		return graph == de.grogra.graph.impl.GraphManager.STATIC;
	}


	@Override
	public void setTransformation (Matrix3d t)
	{
		transformation.set (t);
		repaint (ViewComponent.ALL | ViewComponent.CHANGED);
	}


	@Override
	public final Matrix3d getTransformation ()
	{
		return transformation;
	}


	@Override
	public Matrix3d getCanvasTransformation ()
	{
		canvasTransformation.set (transformation);
		UIToolkit ui = UIToolkit.get (this);
		canvasTransformation.m02 += ui.getWidth (getViewComponent ().getComponent ()) >> 1;
		canvasTransformation.m12 += ui.getHeight (getViewComponent ().getComponent ()) >> 1;
		return canvasTransformation;
	}
	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field transformation$FIELD;
	public static final Type.Field layout$FIELD;

	public static class Type extends View.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (View2D representative, de.grogra.persistence.SCOType supertype)
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
					((View2D) o).setTransformation ((Matrix3d) value);
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((View2D) o).layout = (Layout) value;
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
					return ((View2D) o).transformation;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((View2D) o).layout;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new View2D ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (View2D.class);
		transformation$FIELD = Type._addManagedField ($TYPE, "transformation", 0 | Type.Field.SCO, de.grogra.math.Matrix3dType.$TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		layout$FIELD = Type._addManagedField ($TYPE, "layout", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Layout.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end

}
