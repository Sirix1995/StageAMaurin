
/*
 * Copyright (C) 2016 GroIMP Developer Team
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

package de.grogra.imp3d.objects;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.pf.boot.Main;
import de.grogra.pf.ui.Workbench;

public class TextBlock extends ShadedNull implements Pickable, Renderable, Raytraceable {

	private static final long serialVersionUID = 134567897654L;

	protected String caption = "Label";
	//enh:field attr=Attributes.CAPTION getter setter
	
	protected float depth = 3.5f;
	//enh:field attr=Attributes.DEPTH getter setter

	protected FontAdapter font = FontAdapter.getInstance(12, "Arial");
	//enh:field type=FontAdapter.$TYPE attr=Attributes.FONT getter setter


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, TextBlock.$TYPE, new NType.Field[] {caption$FIELD});
		}

		public static void signature (@In @Out TextBlock l, String c)
		{
		}
	}

	public TextBlock () {}

	public TextBlock (String caption)
	{
		super ();
		this.caption = caption;
	}

	public TextBlock (String caption, float depth)
	{
		super ();
		this.caption = caption;
		this.depth = depth;
		
	}
	
	public TextBlock (String caption, float depth, String fontName, int size)
	{
		super ();
		this.caption = caption;
		this.depth = depth;
		font = FontAdapter.getInstance(size, fontName);
	}

	public static void pick (String caption, Font font, Point3d origin, Vector3d direction,
			 Matrix4d transformation, PickList list) {
		Point3d b = list.p3d0;
		Tuple2f p = list.p2f0;
		b.set (0, 0, 0);
		transformation.transform (b);
		if (((View3D) list.getView ()).getCanvasCamera ().projectWorld (b, p) > 0)
		{
		FontMetrics fm = ((Component) list.getView ().getComponent ()).getFontMetrics (font);
		
		int lc, w;
		if (caption == null) {
			lc = 1;
			w = 10;
		} else {
			lc = 1;
			w = 0;
			char[] chars = caption.toCharArray ();
			int len = chars.length;
			int pos = 0;
			for (int i = 0; i <= len; i++)
			{
				if ((i == len) || (chars[i] == '\n'))
				{
					if (i > pos)
					{
						w = Math.max (w, fm.charsWidth (chars, pos, i - pos));
						pos = i + 1;
					}
					if (i < len - 1)
					{
						lc++;
					}
				}
			}
		}
		int dx, dy, a = fm.getAscent (), d = fm.getDescent (),
		s = fm.charWidth ('x') / 2, h = fm.getHeight ();
		dx = w / -2;
		dy = (1 - lc) * h / 2;
		
		int x1 = (int) p.x + dx - s, y1 = (int) p.y + dy - a - s;
		if ((x1 <= list.getViewX ()) && (y1 <= list.getViewY ())
			&& (list.getViewX () <= x1 + w + 2 * s)
			&& (list.getViewY () <= y1 + a + d + 2 * s + (lc - 1) * h))
			{
				b.set (0, 0, 0);
				list.add (de.grogra.vecmath.Math2.closestConnection (origin, direction, b));
			}
		}
	}

	
	@Override
	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
			  Matrix4d t, PickList list)
		{
		GraphState gs = list.getGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (getCaption (), FontAdapter.getFont (font), origin, direction, t, list);
			}
			else
			{
				pick ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()),
					  FontAdapter.getFont ((FontAdapter) gs.checkObject (this, true, Attributes.FONT, font)),
					  origin, direction, t, list);
			}
		}
		else
		{
			pick ((String) gs.getObject (object, asNode, Attributes.CAPTION),
				  FontAdapter.getFont ((FontAdapter) gs.getObject (object, asNode, Attributes.FONT)),
				  origin, direction, t, list);
		}
	}


	@Override
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawTextBlock (caption, FontAdapter.getFont (font), depth, 
						null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
			else
			{
				rs.drawTextBlock ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()), 
						FontAdapter.getFont (font), gs.checkFloat (this, true, Attributes.DEPTH, depth), 
						null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
		}
		else
		{
			rs.drawTextBlock ((String) gs.getObject (object, asNode, Attributes.CAPTION), 
					FontAdapter.getFont (font), gs.getFloat (object, asNode, Attributes.DEPTH), 
					null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
		}
	}


	@Override
	public RaytracerLeaf createRaytracerLeaf
		(Object object, boolean asNode, long pathId, GraphState gs)
	{	
		Main.getLogger ().warning("LM does not support TextBlock objects ... going to ignored it.");
		Workbench.current ().logGUIInfo("LM does not support TextBlock objects ... going to ignored it.");
		
		return null;
	}


//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field caption$FIELD;
	public static final NType.Field depth$FIELD;
	public static final NType.Field font$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (TextBlock.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((TextBlock) o).depth = value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((TextBlock) o).getDepth ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((TextBlock) o).caption = (String) value;
					return;
				case 2:
					((TextBlock) o).font = (FontAdapter) FontAdapter.$TYPE.setObject (((TextBlock) o).font, value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((TextBlock) o).getCaption ();
				case 2:
					return ((TextBlock) o).getFont ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new TextBlock ());
		$TYPE.addManagedField (caption$FIELD = new _Field ("caption", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (depth$FIELD = new _Field ("depth", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (font$FIELD = new _Field ("font", _Field.PROTECTED  | _Field.SCO, FontAdapter.$TYPE, null, 2));
		$TYPE.declareFieldAttribute (caption$FIELD, Attributes.CAPTION);
		$TYPE.declareFieldAttribute (depth$FIELD, Attributes.DEPTH);
		$TYPE.declareFieldAttribute (font$FIELD, Attributes.FONT);
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
		return new TextBlock ();
	}

	public float getDepth ()
	{
		return depth;
	}

	public void setDepth (float value)
	{
		this.depth = value;
	}

	public String getCaption ()
	{
		return caption;
	}

	public void setCaption (String value)
	{
		caption$FIELD.setObject (this, value);
	}

	public FontAdapter getFont ()
	{
		return font;
	}

	public void setFont (FontAdapter value)
	{
		font$FIELD.setObject (this, value);
	}

//enh:end


	/**
	 * Calculates the area of an object.
	 * 
	 * @return 0
	 */
	@Override
	public double getSurfaceArea() {
		return 0;
	}

	/**
	 * Calculates the volume.
	 * 
	 * @return 0
	 */
	@Override
	public double getVolume() {
		return 0;
	}
	
}
