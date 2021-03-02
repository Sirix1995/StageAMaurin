package de.grogra.imp3d.glsl.renderable;

import java.awt.Font;
import java.text.DecimalFormat;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;

import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Null;
import de.grogra.imp3d.objects.NumericLabel;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.imp3d.objects.TextLabelBase;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.graph.GraphState;

public class GLSLNumericLabel extends GLSLTextLabelBase {
	
	@Override
	public Class<?> instanceFor() {
		return NumericLabel.class;
	}

	/*


		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				draw (getCaption (), FontAdapter.getFont (font), getHorizontalAlignment (),
					  getVerticalAlignment (), color,
					  fillColor, (bits & FILLED_MASK) != 0,
					  (bits & OUTLINED_MASK) != 0, rs);
			}
			else
			{
				draw ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()),
					  FontAdapter.getFont ((FontAdapter) gs.checkObject (this, true, Attributes.FONT, font)),
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
				  gs.getInt (object, asNode, Attributes.HORIZONTAL_ALIGNMENT),
				  gs.getInt (object, asNode, Attributes.VERTICAL_ALIGNMENT),
				  (Tuple3f) gs.getObject (object, asNode, Attributes.COLOR), 
				  (Tuple3f) gs.getObject (object, asNode, Attributes.FILL_COLOR), 
				  gs.getBoolean (object, asNode, Attributes.FILLED),
				  gs.getBoolean (object, asNode, Attributes.OUTLINED),
				  rs);


	 */
	
	@Override
	public void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		assert (reference instanceof NumericLabel);
		NumericLabel ref = (NumericLabel) reference;
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		super.updateInstanceDirect(reference);
		assert (reference instanceof NumericLabel);
		NumericLabel ref = (NumericLabel) reference;
		caption = getCaption (ref);
	}
	
	DecimalFormat formatter = null;
	
	protected String getCaption (NumericLabel ref)
	{
		if (ref.getFormat() == null)
		{
			return Float.toString (ref.getValue());
		}
		else
		{
			if (formatter == null)
			{
				formatter = new DecimalFormat (ref.getFormat());
			}
			return formatter.format (ref.getValue());
		}
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		TextLabelBase.draw (caption, font, getHorizontalAlignment (),
				  getVerticalAlignment (), color,
				  fillColor, getBool(FILLED_MASK),
				  getBool(OUTLINED_MASK), rs);
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLNumericLabel();
	}

}
