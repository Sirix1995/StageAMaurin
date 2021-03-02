package de.grogra.imp3d.glsl.renderable;

import java.awt.Font;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;

import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Null;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.imp3d.objects.TextLabel;
import de.grogra.imp3d.objects.TextLabelBase;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.graph.GraphState;

public class GLSLTextLabel extends GLSLTextLabelBase {
	
	
	@Override
	public Class<?> instanceFor() {
		return TextLabel.class;
	}
	
	@Override
	public void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		super.updateInstanceIndirect(state, asNode, gs);
		caption = (String) gs.getObject (state, asNode, Attributes.CAPTION);
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		super.updateInstanceByInstancing(reference, gs);
		assert (reference instanceof TextLabel);
		TextLabel ref = (TextLabel) reference;
		caption = (String) gs.checkObject (ref, true, Attributes.CAPTION, ref.getCaption ());
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		super.updateInstanceDirect(reference);
		assert (reference instanceof TextLabel);
		TextLabel ref = (TextLabel) reference;
		caption = ref.getCaption ();
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLTextLabel();
	}

}
