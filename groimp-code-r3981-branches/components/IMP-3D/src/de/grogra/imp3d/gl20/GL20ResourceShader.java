package de.grogra.imp3d.gl20;

import de.grogra.imp3d.shading.*;
import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20GLSLCode;
import de.grogra.imp3d.gl20.GL20ShaderServer;
import de.grogra.imp3d.shading.Shader;

abstract class GL20ResourceShader extends GL20Resource {
	/**
	 * shader can produce transparent fragments
	 */
	final public static int NON_OPAQUE = 0x1;
	
	/**
	 * shader need normals for calculation
	 */
	final public static int NEEDS_NORMAL = 0x2;
	
	/**
	 * shader need texture coordinats for calculation
	 */
	final public static int NEEDS_TEXCOORD = 0x4;
	
	/**
	 * shader need the world-to-view matrix
	 */
	final public static int NEEDS_WORLD_TO_VIEW_MATRIX = 0x8;
	
	/**
	 * shader need information about lights
	 */
	final public static int NEEDS_LIGHTS = 0x10;
	
	/**
	 * shader attribute bit
	 */
	final private static int SHADER = 0x1;
	
	/**
	 * all changes that was made, since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;
	
	/**
	 * shader attribute
	 */
	private Shader shader = null;
	
	/**
	 * last shader stamp
	 */
	private int shaderStamp = -1;
	
	/**
	 * if the shader is valid 
	 */
	private boolean isShaderValid = false;
				
	/**
	 * vertex shader index, will be assigned by <code>GL20GfxServer</code>
	 */
	private int vertexShaderIndex = 0;
	
	/**
	 * fragment shader index, will be assigned by <code>GL20GfxServer</code>
	 */
	private int fragmentShaderIndex = 0;
	
	/**
	 * shader program index, will be assigned by <code>GL20GfxServer</code>
	 */
	private int shaderProgramIndex = 0;
	
	private int userCount = 0;
	
	public GL20ResourceShader(int resourceClassType) {
		super(resourceClassType);
		
		assert((resourceClassType & GL20RESOURCE_CLASS_SHADER) != 0);
	}
	
	/**
	 * return the equivalent <code>Shader</code>
	 * 
	 * @return the equivalent <code>Shader</code>
	 */
	final public Shader getShader() {
		return shader;
	}
	
	/**
	 * get the environment dependencies for this <code>GL20ResourceShader</code>
	 * 
	 * @return
	 */
	abstract public int getEnvironmentDependencies();
	
	abstract public int getScalarFromShader(GL20GLSLCode code,int channel);
	
	abstract public int getVector3FromShader(GL20GLSLCode code,int startChannel);
		
	abstract public int getVector4FromShader(GL20GLSLCode code,int startChannel);
		
	public boolean setShader(Shader shader) {
		boolean returnValue = false;
		
		if (this.shader != shader) {
			this.shader = shader;
			
			changeMask |= SHADER;
		}
		else
			returnValue = true;
		
		return returnValue;
	}
	
	public void registerUser() {
		userCount++;
	}
	
	public void unregisterUser() {
		if (userCount == 1)
			destroy();
		
		userCount--;
	}
	
	public void applyShader(boolean shadeless) {
		if (isShaderValid == false) {
			// shader is invalid so (re)create it
			GL20GLSLCode code = new GL20GLSLCode();
			
			int vector4Index = getVector4FromShader(code,GL20ResourceShaderFragment.GL20CHANNEL_R);
			
			if (vector4Index != -1) {				
				String source = new String("gl_FragColor = " + code.getVector4Name(vector4Index) + ";\n");
				
				code.freeVector4(vector4Index);
				code.appendCode(source);
			}
			
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			gfxServer.createShader(code);
			
			//System.out.println(code.getShaderCodeVertex(false));
			//System.out.println(code.getShaderCodeFragment());
			
			isShaderValid = true;
		}
	}
	
	/**
	 * check if this <code>GL20ResourceShader</code> is up to date
	 * 
	 * @return <code>true</code> this <code>GL20ResourceShader</code> is up to date
	 * <code>false</code> this <code>GL20ResourceShader</code> is not up to date,
	 * need an <code>update()</code> call
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else if ((shader != null) &&
				 (((shader instanceof Material) && ((Material)shader).getStamp() != shaderStamp)) ||
			      ((shader instanceof RGBAShader) && ((RGBAShader)shader).getStamp() != shaderStamp))
			   return false;
		else 
			return super.isUpToDate();
	}
	
	/**
	 * update this <code>GL20ResourceShader</code>
	 */
	public void update() {
		if (shader != null) {
			int currentStamp = -1;
			if (shader instanceof Material)
				currentStamp = ((Material)shader).getStamp();
			else if (shader instanceof RGBAShader)
				currentStamp = ((RGBAShader)shader).getStamp();
			
			if (currentStamp != shaderStamp) {
				shaderStamp = currentStamp;
				isShaderValid = false;
			}
		}
		
		if (changeMask != 0) {
			
			changeMask = 0;
		}
		
		// apply update to base class
		super.update();
	}
	
	/**
	 * destroy this <code>GL20Shader</code>
	 */
	public void destroy() {
		GL20ShaderServer.removeShader(this);
	}
}