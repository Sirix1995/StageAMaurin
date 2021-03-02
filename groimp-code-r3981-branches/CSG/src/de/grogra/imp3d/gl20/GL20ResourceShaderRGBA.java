package de.grogra.imp3d.gl20;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import de.grogra.imp3d.gl20.GL20ResourceShaderFragment;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.RGBAShader;

public class GL20ResourceShaderRGBA extends GL20ResourceShader {
	/**
	 * color attribute bit
	 */
	final private static int COLOR = 0x1;
	
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;
	
	/**
	 * color attribute
	 */
	private Vector4f color = new Vector4f();
		
	public GL20ResourceShaderRGBA() {
		super(GL20Resource.GL20RESOURCE_SHADER_RGBA);
		color.set(0.5f,0.5f,0.5f,1.0f);
	}
	
	public GL20ResourceShaderRGBA(float red,float green,float blue) {
		super(GL20Resource.GL20RESOURCE_SHADER_RGBA);
		color.set(red,green,blue,1.0f);
	}
	
	public int getEnvironmentDependencies() {
		return (color.w < 1.0f) ? NON_OPAQUE : 0;
	}
	
	public boolean setShader(Shader shader) {
		boolean returnValue = false;
		
		if (shader instanceof RGBAShader) {
			RGBAShader rgbaShader = (RGBAShader)shader;
			setColor(new Vector4f(rgbaShader.x,rgbaShader.y,rgbaShader.z,rgbaShader.w));
			
			returnValue = super.setShader(shader);
		}
		
		return returnValue;
	}
	
	public void setColor(Vector4f color) {
		if (this.color.equals(color) == false) {
			this.color.set(color);
			
			changeMask |= COLOR;
		}
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public int getScalarFromShader(GL20GLSLCode code,int channel) {
		int returnValue = -1;
		
		if ((channel < GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MIN) ||
			(channel > GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MAX)) {
			int constIndex = -1;
			switch (channel & 0x3) {
			case 0:
				constIndex = code.createConstScalar(color.x);
				break;
			case 1:
				constIndex = code.createConstScalar(color.y);
				break;
			case 2:
				constIndex = code.createConstScalar(color.z);
				break;
			case 3:
				constIndex = code.createConstScalar(color.w);
				break;
			}
			
			returnValue = constIndex | GL20GLSLCode.CONST_BIT;
		}
		
		return returnValue;
	}
	
	public int getVector3FromShader(GL20GLSLCode code,int startChannel) {
		int returnValue = -1;
		
		if ((startChannel + 2 < GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MIN) ||
			(startChannel > GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MAX)) {
			returnValue = code.allocateTemporaryVector3();
			
			if (returnValue != -1) {
				String source = new String(code.getTemporaryVector3Name(returnValue) + " = vec3(");
				
				for (int i=0;i < 3;i++) {
					switch ((startChannel + i) & 0x3) {
					case 0:
						source += ((Float)color.x).toString();
						break;
					case 1:
						source += ((Float)color.y).toString();
						break;
					case 2:
						source += ((Float)color.z).toString();
						break;
					case 3:
						source += ((Float)color.w).toString();
						break;
					}
					
					if (i < 2)
						source += ",";
					else
						source += ");\n";
				}
				
				code.appendCode(source);
			}
		}
		
		return returnValue;
	}
	
	public int getVector4FromShader(GL20GLSLCode code,int startChannel) {
		int returnValue = -1;
		
		if ((startChannel + 3 < GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MIN) ||
			(startChannel > GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MAX)) {
			returnValue = code.allocateTemporaryVector4();
			
			if (returnValue != -1) {
				String source = new String(code.getTemporaryVector4Name(returnValue) + " = vec4(");
				
				for (int i=0;i < 4;i++) {
					switch ((startChannel + i) & 0x3) {
					case 0:
						source += ((Float)color.x).toString();
						break;
					case 1:
						source += ((Float)color.y).toString();
						break;
					case 2:
						source += ((Float)color.z).toString();
						break;
					case 3:
						source += ((Float)color.w).toString();
						break;
					}
					
					if (i < 3)
						source += ",";
					else
						source += ");\n";					
				}
				
				code.appendCode(source);
			}
		}
		
		return returnValue;
	}
	
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();
	}
	
	public void update() {		
		RGBAShader rgbaShader = (RGBAShader)getShader();
		setColor(new Vector4f(rgbaShader.x,rgbaShader.y,rgbaShader.z,rgbaShader.w));
		
		if (changeMask != 0) {
			changeMask = 0;
		}		
		
		super.update();
	}
	
	public void destroy() {
		super.destroy();
	}
}