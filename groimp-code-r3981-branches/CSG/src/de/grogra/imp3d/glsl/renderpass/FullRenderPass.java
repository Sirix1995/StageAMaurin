/**
 * 
 */
package de.grogra.imp3d.glsl.renderpass;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;

/**
 * @author Konni Hartmann
 *
 */
public abstract class FullRenderPass extends RenderPass {
	
	@Override
	public void process(GLSLDisplay disp, OpenGLState glState, Object data) {
		super.process(disp, glState, data);
		//XXX: Debug!
		glState.renderPass++;
		glState.currentPassName = this.getClass().getSimpleName();
		glState.presentDebugScreen(disp);
	}
}
