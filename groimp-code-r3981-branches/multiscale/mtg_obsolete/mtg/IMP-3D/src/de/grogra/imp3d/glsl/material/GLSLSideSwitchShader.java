package de.grogra.imp3d.glsl.material;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.SideSwitchShader;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation of Shader: SideSwitch. Will recompile on every property change!
 * 
 * @author Konni Hartmann
 */
public class GLSLSideSwitchShader extends GLSLMaterial{

	private GLSLMaterial front = null;
	private GLSLMaterial back = null;
	
	private Shader front_S = null;
	private Shader back_S = null;
	
	int Stamp = -1;
	
	private String returnBySide(Result defaultVal, Result sh1, Result sh2) {
		if(sh1 == null) {
			if(sh2 == null)
				return defaultVal.toString();
			return sh2.convert(defaultVal.getReturnType());
		} else {
			if(sh2 == null || sh1.equals(sh2))
				return sh1.convert(defaultVal.getReturnType());
			return "gl_FrontFacing?"+sh1.convert(defaultVal.getReturnType())+":"+sh2.convert(defaultVal.getReturnType());
		}
	} 
	
	@Override
	protected Result[] getAllChannels(Object sha) {
		assert (sha instanceof SideSwitchShader);
		SideSwitchShader sh = (SideSwitchShader) sha;
		Result[] input = { 
				null,
				null,
				new Result("4.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("", Result.ET_UNKNOWN), };
		
		GLSLChannelMap defInp = getMaterialConfig().getDefaultInputChannel();
		
		input[MaterialConfiguration.IT_POSITION] = defInp.generate(null, getMaterialConfig(), null, Channel.X);
		input[MaterialConfiguration.IT_NORMAL] = defInp.generate(null, getMaterialConfig(), null, Channel.NX);
		
		Result [] frontInput = front_S!=null?front.getAllChannels(front_S):null;
		Result [] backInput = back_S!=null?back.getAllChannels(back_S):null;
		
		// Remember Stamp to check lateron
		Stamp = sh.getStamp();

		if(frontInput == null && backInput == null)
			return input;
		if(frontInput == null)
			return backInput;
		if(backInput == null)
			return frontInput;
		
		for(int i = 0; i < input.length-1; i++) {
//			if(input[i] != null && frontInput[i] != null && backInput[i] != null)
				input[i] = new Result(returnBySide(input[i], frontInput[i], backInput[i]), input[i].getReturnType());
		}
		
		return input;
	}

	@Override
	protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNo) {
		super.setupDynamicUniforms(gl, disp, data, shaderNo);
		if(front!=null)
			front.setupDynamicUniforms(gl, disp, front_S, shaderNo);
		if(back!=null)
			back.setupDynamicUniforms(gl, disp, back_S, shaderNo);		
	}
	
	@Override
	protected void setupUniforms(GL gl, GLSLDisplay disp, Object s, int shaderProgramNumber) {
		super.setupUniforms(gl, disp, s, shaderProgramNumber);
		if(front!=null)
			front.setupUniforms(gl, disp, front_S, shaderProgramNumber);
		if(back!=null)
			back.setupUniforms(gl, disp, back_S, shaderProgramNumber);		
	}
		
	@Override
	public Class instanceFor() {
		return SideSwitchShader.class;
	}

	@Override
	public boolean needsRecompilation(Object s) {
		assert s instanceof SideSwitchShader;
		SideSwitchShader sss = (SideSwitchShader) s;
		updateShaderRefs(sss);
		if(Stamp != sss.getStamp()) {
			return true;
//			front = null; back = null;
//			if(front_S == null && back_S == null)
//				return true;
//			if(front_S == null)
//				return back.needsRecompilation(back_S);
//			if(back_S == null)
//				return front.needsRecompilation(front_S);
//			return ( front.needsRecompilation(sss.getFrontShader()) || getBack(sss.getBackShader()).needsRecompilation(sss.getBackShader()));
		}
		return false;
	}

	@Override
	public GLSLManagedShader getInstance() {
		return new GLSLSideSwitchShader();
	}

	@Override
	public boolean mayDiscard(Object s) {
		assert s instanceof SideSwitchShader;
		SideSwitchShader sss = (SideSwitchShader) s;
		updateShaderRefs(sss);		
		//if(Stamp != sss.getStamp())
		if(front_S == null && back_S == null)
			return true;
		if(sss.getFrontShader() == null)
			return back.mayDiscard(back_S);
		if(sss.getBackShader() == null)
			return front.mayDiscard(front_S);
		
		return ( front.mayDiscard(front_S) || front.mayDiscard(front_S) );
	}
	
	@Override
	public boolean isOpaque(Object s) {
		assert s instanceof SideSwitchShader;
		SideSwitchShader sss = (SideSwitchShader) s;
		updateShaderRefs(sss);		
		//if(Stamp != sss.getStamp())
		if(front_S == null && back_S == null)
			return true;
		if(sss.getFrontShader() == null)
			return back.isOpaque(back_S);
		if(sss.getBackShader() == null)
			return front.isOpaque(front_S);

		return ( front.isOpaque(front_S) || front.isOpaque(front_S) );
	}

	public void updateShaderRefs(SideSwitchShader s) {
		
		if(s.getFrontShader() == front_S && s.getBackShader() == back_S)
			return;
		
		front_S = s.getFrontShader();
		back_S = s.getBackShader();

		if(front_S != null) {
			front = (GLSLMaterial)getConfig().getShaderByDefaultCollection(null, front_S);
			front = (GLSLMaterial) front.getInstance();
			front.setConfig(getConfig());
		}
		
		if(back_S != null) {
			back = (GLSLMaterial)getConfig().getShaderByDefaultCollection(null, back_S);
			back = (GLSLMaterial) back.getInstance();
			back.setConfig(getConfig());
		}
		
	}
}
