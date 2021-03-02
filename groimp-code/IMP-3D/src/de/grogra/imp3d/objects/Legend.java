
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

package de.grogra.imp3d.objects;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.WireframeCanvas;
import de.grogra.math.Tuple3fType;

public class Legend extends TextLabelBase {

	private static final long serialVersionUID = 13434343435L;

	protected String caption = "Label";
	// enh:field attr=Attributes.CAPTION getter setter

	// position in {N,E,S,W, NE, SE, SW, NW}
	protected String position = "NE";
	// enh:field attr=Attributes.POSITION getter

	private final static Color3f BACKGROUND = new Color3f(0.5f,0.58f,0.75f);
	
	// exact position, defined by the user (constructor)
	int userPX = -1;
	int userPY = -1;
	
	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern {
		public Pattern() {
			super(null, Legend.$TYPE, new NType.Field[] { caption$FIELD });
		}

		public static void signature(@In @Out Legend l, String c) {
		}
	}

	public Legend() {
		bits |= OUTLINED_MASK;
		bits |= FILLED_MASK;
		setFillColor(BACKGROUND);
		font = FontAdapter.getInstance(20, "Arial");
		userPX = -1;
		userPY = -1;
	}

	public Legend(String caption) {
		this();
		this.caption = caption;
	}

	public Legend(String caption, String position) {
		this();
		this.caption = caption;
		this.position = checkPosition(position);
	}

	public Legend(String caption, int px, int py) {
		this();
		this.caption = caption;
		if(px<0) px = 0; if(py<0) py = 0;
		userPX = px;
		userPY = py;
	}
	
	private static String checkPosition(String p) {
		p = p.toUpperCase();
		if (p.equals("N") || p.equals("E") || p.equals("S") || p.equals("W") || 
			p.equals("NE") || p.equals("SE") || p.equals("SW") || p.equals("NW")) return p;
		return "NE";
	}

	
	private static void pick(String caption, Font font, String position, int userPX, int userPY, 
			int horizontal, int vertical, Point3d origin, Vector3d direction, Matrix4d transformation, PickList list) {
		Point3d b = list.p3d0;
		Tuple2f p = list.p2f0;
		b.set(0, 0, 0);
		transformation.transform(b);
		if (((View3D) list.getView()).getCanvasCamera().projectWorld(b, p) > 0) {
			FontMetrics fm = ((Component) list.getView().getComponent()).getFontMetrics(font);

			int lc, w;
			if (caption == null) {
				lc = 1;
				w = 10;
			} else {
				lc = 1;
				w = 0;
				char[] chars = caption.toCharArray();
				int len = chars.length;
				int pos = 0;
				for (int i = 0; i <= len; i++) {
					if ((i == len) || (chars[i] == '\n')) {
						if (i > pos) {
							w = Math.max(w, fm.charsWidth(chars, pos, i - pos));
							pos = i + 1;
						}
						if (i < len - 1) {
							lc++;
						}
					}
				}
			}
			int dx, dy, a = fm.getAscent(), d = fm.getDescent(), s = fm.charWidth('x') / 2, h = fm.getHeight();
			switch (horizontal) {
			case Attributes.H_ALIGN_LEFT:
				dx = 0;
				break;
			case Attributes.H_ALIGN_RIGHT:
				dx = -w;
				break;
			default:
				dx = w / -2;
				break;
			}
			switch (vertical) {
			case Attributes.V_ALIGN_TOP:
				dy = a;
				break;
			case Attributes.V_ALIGN_BOTTOM:
				dy = (1 - lc) * h - d;
				break;
			default:
				dy = (1 - lc) * h / 2;
				break;
			}
			
			int x1 = 20;
			int y1 = 10;
			int maxX = list.getView().getSize().width;
			int maxY = list.getView().getSize().height;
			int ww = w + 4 * s;
			int hh = a + d + 2 * s + (lc - 1) * h;
			
			if(position.equals("N"))  { x1 = maxX/2 + dx+ww/2; y1 = 10+hh/2; }
			if(position.equals("S"))  { x1 = maxX/2 + dx+ww/2; y1 = maxY -hh/2; }
			if(position.equals("E"))  { x1 = maxX -5-ww/2; y1 = maxY/2 +dy+hh/2; }
			if(position.equals("NE")) { x1 = maxX -5-ww/2; y1 = 10+hh/2; }
			if(position.equals("SE")) { x1 = maxX -5-ww/2; y1 = maxY -hh/2; }
			if(position.equals("W"))  { x1 = 3+ww/2; y1 = maxY/2 +dy+hh/2; }
			if(position.equals("SW")) { x1 = 3+ww/2; y1 = maxY -hh/2; }
			if(position.equals("NW")) { x1 = 3+ww/2; y1 = 10+hh/2; }
			
			if ((x1 - ww/2 <= list.getViewX()) && (y1 - hh <= list.getViewY()) && 
				(list.getViewX() <= x1 + ww/2) && (list.getViewY() <= y1 + hh/2)) {
				b.set(0, 0, 0);
				list.add(de.grogra.vecmath.Math2.closestConnection(origin, direction, b));
			}
		}
	}

	
	private static void draw(String caption, Font font, String position, int userPX, int userPY, 
			int horizontal, int vertical, Tuple3f c, Tuple3f b, boolean filled, boolean outlined,
			RenderState rs) {
		Tuple2f p = rs.getPool().q2f0;
		Tuple3f t = rs.getPool().q3f0;
		t.set(0, 0, 0);
		if (!rs.getWindowPos(t, p)) {
			return;
		}

		int highlight = rs.getCurrentHighlight();
		if ((highlight & (ViewSelection.MOUSE_OVER_SELECTED | ViewSelection.MOUSE_OVER)) != 0) {
			b = new Color3f (1f, 0.0f, 0.1f);;
			Tuple3fType.setColor(t, ViewSelection.getColor(Tuple3fType.colorToInt(c), highlight, false));
			c = t;
		}
		FontMetrics fm = rs.getFontMetrics(font);
		int lc, w, len;
		char[] chars;
		if (caption == null) {
			chars = null;
			len = 0;
			lc = 1;
			w = 10;
		} else {
			lc = 1;
			w = 0;
			chars = caption.toCharArray();
			len = chars.length;
			int pos = 0;
			for (int i = 0; i <= len; i++) {
				if ((i == len) || (chars[i] == '\n')) {
					if (i > pos) {
						w = Math.max(w, fm.charsWidth(chars, pos, i - pos));
					}
					if (i < len - 1) {
						lc++;
					}
					pos = i + 1;
				}
			}
		}
		
		
		int dx, dy, a = fm.getAscent(), d = fm.getDescent(), s = fm.charWidth('x') / 2, h = fm.getHeight();
		switch (horizontal) {
			case Attributes.H_ALIGN_LEFT:
				dx = 0;
				break;
			case Attributes.H_ALIGN_RIGHT:
				dx = -w;
				break;
			default:
				dx = w / -2;
				break;
		}
		switch (vertical) {
			case Attributes.V_ALIGN_TOP:
				dy = a;
				break;
			case Attributes.V_ALIGN_BOTTOM:
				dy = (1 - lc) * h - d;
				break;
			default:
				dy = (1 - lc) * h / 2;
				break;
		}
		
		int px = 20;
		int py = 10;
		int maxX = 100;
		int maxY = 100;
		int ww = w + 4 * s;
		int hh = a + d + 2 * s + (lc - 1) * h;
		
		if(rs instanceof de.grogra.imp3d.gl.GLDisplay) {
			maxX = ((de.grogra.imp3d.gl.GLDisplay)rs).getView3D().getSize().width;
			maxY = ((de.grogra.imp3d.gl.GLDisplay)rs).getView3D().getSize().height;
		}
		if(rs instanceof de.grogra.imp3d.glsl.GLDisplay) {
			maxX = ((de.grogra.imp3d.glsl.GLDisplay)rs).getView3D().getSize().width;
			maxY = ((de.grogra.imp3d.glsl.GLDisplay)rs).getView3D().getSize().height;
		}
		if(rs instanceof WireframeCanvas) {
			maxX = ((WireframeCanvas)rs).getView3D().getSize().width;
			maxY = ((WireframeCanvas)rs).getView3D().getSize().height;
		}
		
		if(position.equals("N"))  { px = maxX/2 + dx+ww/2; py = 10+hh/2; }
		if(position.equals("S"))  { px = maxX/2 + dx+ww/2; py = maxY -hh/2; }
		if(position.equals("E"))  { px = maxX -5-ww/2; py = maxY/2 +dy+hh/2; }
		if(position.equals("NE")) { px = maxX -5-ww/2; py = 10+hh/2; }
		if(position.equals("SE")) { px = maxX -5-ww/2; py = maxY -hh/2; }
		if(position.equals("W"))  { px = 3+ww/2; py = maxY/2 +dy+hh/2; }
		if(position.equals("SW")) { px = 3+ww/2; py = maxY -hh/2; }
		if(position.equals("NW")) { px = 3+ww/2; py = 10+hh/2; }
		
		if(userPX!=-1 && userPY!=-1) {
			px = userPX; 
			py = userPY;
		}
		
		if (filled) {
			rs.fillRectangle(px + dx - 2*s, py + dy - a - s, w + 4 * s, a + d + 2 * s + (lc - 1) * h, b);
		}
		if (outlined) {
			rs.drawRectangle(px + dx - 2*s, py + dy - a - s, w + 4 * s, a + d + 2 * s + (lc - 1) * h, c);
			rs.drawRectangle(px + dx - 2*s-2, py + dy - a - s-2, w + 4 * s+4, a + d + 2 * s + (lc - 1) * h+4, c);
		}
		if (chars != null) {
			int pos = 0;
			lc = 0;
			for (int i = 0; i <= len; i++) {
				if ((i == len) || (chars[i] == '\n')) {
					if (i > pos) {
						rs.drawString(px + dx, py + dy + lc * h, caption.substring(pos, i), font, c);
					}
					lc++;
					pos = i + 1;
				}
			}
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
				draw (getCaption (), FontAdapter.getFont (font), position, userPX, userPY, 
					getHorizontalAlignment (), getVerticalAlignment (), color, fillColor, 
					(bits & FILLED_MASK) != 0, (bits & OUTLINED_MASK) != 0, rs);
			}
			else
			{
				draw ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()),
					  FontAdapter.getFont ((FontAdapter) gs.checkObject (this, true, Attributes.FONT, font)),
					  (String) gs.checkObject (this, true, Attributes.POSITION, getPosition ()), userPX, userPY,
					  gs.checkInt (this, true, Attributes.HORIZONTAL_ALIGNMENT, getHorizontalAlignment ()),
					  gs.checkInt (this, true, Attributes.VERTICAL_ALIGNMENT, getVerticalAlignment ()),
					  (Tuple3f) gs.checkObject (this, true, Attributes.COLOR, color), 
					  (Tuple3f) gs.checkObject (this, true, Attributes.FILL_COLOR, fillColor), 
					  gs.checkBoolean (this, true, Attributes.FILLED, (bits & FILLED_MASK) != 0),
					  gs.checkBoolean (this, true, Attributes.OUTLINED, (bits & OUTLINED_MASK) != 0),
					  rs);
			}
		}
		else
		{
			draw ((String) gs.getObject (object, asNode, Attributes.CAPTION),
				  FontAdapter.getFont ((FontAdapter) gs.getObject (object, asNode, Attributes.FONT)),
				  (String) gs.getObject (object, asNode, Attributes.POSITION), userPX, userPY,
				  gs.getInt (object, asNode, Attributes.HORIZONTAL_ALIGNMENT),
				  gs.getInt (object, asNode, Attributes.VERTICAL_ALIGNMENT),
				  (Tuple3f) gs.getObject (object, asNode, Attributes.COLOR), 
				  (Tuple3f) gs.getObject (object, asNode, Attributes.FILL_COLOR), 
				  gs.getBoolean (object, asNode, Attributes.FILLED),
				  gs.getBoolean (object, asNode, Attributes.OUTLINED), rs);
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
				pick (getCaption (), FontAdapter.getFont (font), position, userPX, userPY, 
					getHorizontalAlignment (), getVerticalAlignment (), 
					origin, direction, t, list);
			}
			else
			{
				pick ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()),
					FontAdapter.getFont ((FontAdapter) gs.checkObject (this, true, Attributes.FONT, font)),
					(String) gs.checkObject (this, true, Attributes.POSITION, getPosition ()), userPX, userPY,
					gs.checkInt (this, true, Attributes.HORIZONTAL_ALIGNMENT, getHorizontalAlignment ()),
					gs.checkInt (this, true, Attributes.VERTICAL_ALIGNMENT, getVerticalAlignment ()),
					origin, direction, t, list);
			}
		}
		else
		{
			pick ((String) gs.getObject (object, asNode, Attributes.CAPTION),
				FontAdapter.getFont ((FontAdapter) gs.getObject (object, asNode, Attributes.FONT)),
				(String) gs.getObject (object, asNode, Attributes.POSITION), userPX, userPY,
				gs.getInt (object, asNode, Attributes.HORIZONTAL_ALIGNMENT),
				gs.getInt (object, asNode, Attributes.VERTICAL_ALIGNMENT),
				origin, direction, t, list);
		}
	}


	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field caption$FIELD;
	public static final NType.Field position$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Legend.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Legend) o).caption = (String) value;
					return;
				case 1:
					((Legend) o).position = (String) value;
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
					return ((Legend) o).getCaption ();
				case 1:
					return ((Legend) o).getPosition ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Legend ());
		$TYPE.addManagedField (caption$FIELD = new _Field ("caption", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (position$FIELD = new _Field ("position", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.declareFieldAttribute (caption$FIELD, Attributes.CAPTION);
		$TYPE.declareFieldAttribute (position$FIELD, Attributes.POSITION);
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
		return new Legend ();
	}

	public String getCaption ()
	{
		return caption;
	}

	public void setCaption (String value)
	{
		caption$FIELD.setObject (this, value);
	}

	public String getPosition ()
	{
		return position;
	}

//enh:end
	public void setPosition (String value)
	{
		position$FIELD.setObject (this, checkPosition(value));
	}
}
