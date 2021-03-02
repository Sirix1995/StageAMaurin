package de.grogra.imp3d.gl20;

import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.Shader;

public class GL20ResourceShaderPhong extends GL20ResourceShader { 
	/**
	 * diffuse attribute bit
	 */
	final private static int DIFFUSE_MAP = 0x1;
	
	/**
	 * speculare attribute bit
	 */
	final private static int SPECULAR_MAP = 0x2;
	
	/**
	 * transparency attribute bit
	 */
	final private static int TRANSPARENCY_MAP = 0x4;
	
	/**
	 * shininess transparency attribute bit
	 */
	final private static int TRANSPARENCY_SHININESS_MAP = 0x8;
	
	/**
	 * diffuse transparency attribute bit
	 */
	final private static int TRANSPARENCY_DIFFUSE_MAP = 0x10;
	
	/**
	 * ambient attribute bit
	 */
	final private static int AMBIENT_MAP = 0x20;
	
	/**
	 * emissive attribute bit
	 */
	final private static int EMISSIVE_MAP = 0x40;
	
	/**
	 * shininess attribute bit
	 */
	final private static int SHININESS_MAP = 0x80;
	
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;
		
	/**
	 * diffuse attribute
	 */
	GL20ResourceShaderFragment diffuseMap = null;
	
	/**
	 * specular attribute
	 */
	GL20ResourceShaderFragment specularMap = null;
	
	/**
	 * transparency attribute
	 */
	GL20ResourceShaderFragment transparencyMap = null;
	
	/**
	 * transparency shininess attribute
	 */
	GL20ResourceShaderFragment transparencyShininessMap = null;
	
	/**
	 * transparency diffuse attribute
	 */
	GL20ResourceShaderFragment transparencyDiffuseMap = null;
	
	/**
	 * ambient attribute
	 */
	GL20ResourceShaderFragment ambientMap = null;
	
	/**
	 * emissive attribute
	 */
	GL20ResourceShaderFragment emissiveMap = null;
	
	/**
	 * shininess attribute
	 */
	GL20ResourceShaderFragment shininessMap = null;
	
	/**
	 * the default diffse map
	 */
	static private GL20ResourceShaderFragment DEFAULT_DIFFUSE_MAP = new GL20ResourceShaderFragmentGraytone(0.5f);
	
	/**
	 * the default specular map
	 */
	static private GL20ResourceShaderFragment DEFAULT_SPECULAR_MAP = new GL20ResourceShaderFragmentGraytone(0.0f);
	
	/**
	 * the default transparency map
	 */
	static private GL20ResourceShaderFragment DEFAULT_TRANSPARENCY_MAP = new GL20ResourceShaderFragmentGraytone(0.0f);
	
	/**
	 * the default transparency shininess map
	 */
	static private GL20ResourceShaderFragment DEFAULT_TRANSPARENCY_SHININESS_MAP = new GL20ResourceShaderFragmentGraytone(0.0f);
	
	/**
	 * the default transparency diffuse map
	 */
	static private GL20ResourceShaderFragment DEFAULT_TRANSPARENCY_DIFFUSE_MAP = new GL20ResourceShaderFragmentGraytone(0.0f);
	
	/**
	 * the default ambient map
	 */
	static private GL20ResourceShaderFragment DEFAULT_AMBIENT_MAP = new GL20ResourceShaderFragmentGraytone(0.0f);
	
	/**
	 * the default emissive map
	 */
	static private GL20ResourceShaderFragment DEFAULT_EMISSIVE_MAP = new GL20ResourceShaderFragmentGraytone(0.0f);
	
	/**
	 * the default shininess map
	 */
	static private GL20ResourceShaderFragment DEFAULT_SHININESS_MAP = new GL20ResourceShaderFragmentGraytone(0.5f);
		
	public GL20ResourceShaderPhong() {
		super(GL20Resource.GL20RESOURCE_SHADER_PHONG);
	}
	
	public boolean fragmentAffectOnAlpha() {
		boolean returnValue = false;
		
		if (getDiffuseMap().fragmentAffectOnAlpha())
			returnValue = true;
		else if (getTransparencyMap().fragmentAffectOnAlpha())
			returnValue = true;
			
		return returnValue;
	}
	
	public boolean setShader(Shader shader) {
		boolean returnValue = false;
		
		if (shader instanceof Phong) {
			Phong phongShader = (Phong)shader;
			setDiffuseMap(GL20ShaderServer.getShaderFragment(phongShader.getDiffuse()));
			setSpecularMap(GL20ShaderServer.getShaderFragment(phongShader.getSpecular()));
			setTransparencyMap(GL20ShaderServer.getShaderFragment(phongShader.getTransparency()));
			setTransparencyShininessMap(GL20ShaderServer.getShaderFragment(phongShader.getTransparencyShininess()));
			setTransparencyDiffuseMap(GL20ShaderServer.getShaderFragment(phongShader.getDiffuseTransparency()));
			setAmbientMap(GL20ShaderServer.getShaderFragment(phongShader.getAmbient()));
			setEmissiveMap(GL20ShaderServer.getShaderFragment(phongShader.getEmissive()));
			setShininessMap(GL20ShaderServer.getShaderFragment(phongShader.getShininess()));
			
			returnValue = super.setShader(shader);
		}
					
		return returnValue;
	}
	
	public int getEnvironmentDependencies() {
		return NEEDS_NORMAL | NEEDS_WORLD_TO_VIEW_MATRIX | NEEDS_LIGHTS;
	}
	
	private int createPhongCode(GL20GLSLCode code) {
		int returnValue = -1;
		
		int ambientColor4 = getAmbientMap().getVector4Index(code, GL20ResourceShaderFragment.GL20CHANNEL_R);
		int diffuseColor4 = getDiffuseMap().getVector4Index(code, GL20ResourceShaderFragment.GL20CHANNEL_R);
		int specularColor4 = getSpecularMap().getVector4Index(code, GL20ResourceShaderFragment.GL20CHANNEL_R);
		int shininess = getShininessMap().getScalarIndex(code, GL20ResourceShaderFragment.GL20CHANNEL_R);
		
		if ((ambientColor4 != -1) &&
			(diffuseColor4 != -1) &&
			(specularColor4 != -1)) {
			int resultColor = code.allocateTemporaryVector4();
			
			if (resultColor != -1) {
				String source = new String();
				code.setDependency(GL20GLSLCode.NORMAL_BIT | GL20GLSLCode.POSITION_WORLD_BIT | GL20GLSLCode.POSITION_EYE_BIT);
				
				// set ambient color
				source += code.getVector4Name(resultColor) + " = " + code.getVector4Name(ambientColor4) + ";\n";
				
				// calculate normalized vertex-view vector
				int tempPositionViewVector3 = code.allocateTemporaryVector3();
				source += code.getVector3Name(tempPositionViewVector3) + " = normalize(" + GL20GLSLCode.getDependencyName(GL20GLSLCode.POSITION_EYE_BIT) + ");\n";
				
				// get light position and attenuation attributes
				int tempLightPositionVector3 = code.allocateTemporaryVector3();
				int tempLightAttenuationVector3 = code.allocateTemporaryVector3();
				source += code.getVector3Name(tempLightPositionVector3) + " = vec3(gl_LightSource[0].position);\n";
				source += code.getVector3Name(tempLightAttenuationVector3) + " = vec3(gl_LightSource[0].constantAttenuation,gl_LightSource[0].linearAttenuation,gl_LightSource[0].quadraticAttenuation);\n";
				
				// calculate vertex-light vector
				int tempVertexLightVector3 = code.allocateTemporaryVector3();
				source += code.getVector3Name(tempVertexLightVector3) + " = " + code.getVector3Name(tempLightPositionVector3) + " - " + GL20GLSLCode.getDependencyName(GL20GLSLCode.POSITION_WORLD_BIT) + ";\n";
				
				// calculate length of vertex-light vector
				int tempVertexLightDistance = code.allocateTemporaryScalar();
				source += code.getScalarName(tempVertexLightDistance) + " = length(" + code.getVector3Name(tempVertexLightVector3) + ");\n";
				
				// normalize vertex-light vector
				source += code.getVector3Name(tempVertexLightVector3) + " /= " + code.getScalarName(tempVertexLightDistance) + ";\n";
				
				// calculate normal dot vertex-light vector
				int tempNdotL = code.allocateTemporaryScalar();
				source += code.getScalarName(tempNdotL) + " = max(dot(" + GL20GLSLCode.getDependencyName(GL20GLSLCode.NORMAL_BIT) + "," + code.getVector3Name(tempVertexLightVector3) + "),0.0);\n";
				
				source += "if (" + code.getScalarName(tempNdotL) + " > 0.0) {\n";
				int tempAttenuation = code.allocateTemporaryScalar();
				source += "\t" + code.getScalarName(tempAttenuation) + " = 1.0 / (" + code.getVector3Name(tempLightAttenuationVector3) + ".x + " + code.getVector3Name(tempLightAttenuationVector3) + ".y * " + code.getScalarName(tempVertexLightDistance) + " + " + code.getVector3Name(tempLightAttenuationVector3) + ".z * " + code.getScalarName(tempVertexLightDistance) + " * " + code.getScalarName(tempVertexLightDistance) + ");\n";
				source += "\t" + code.getVector4Name(resultColor) + " += " + code.getScalarName(tempAttenuation) + " * (" + code.getVector4Name(diffuseColor4) + " * " + code.getScalarName(tempNdotL) + ");\n";
				// calculate view vector dot vertex-light vector
				int tempVdotVL = tempVertexLightDistance;
				source += "\t" + code.getScalarName(tempVdotVL) + " = max(dot(" + GL20GLSLCode.getDependencyName(GL20GLSLCode.NORMAL_BIT) + ",normalize(" + code.getVector3Name(tempVertexLightVector3) + " + " + code.getVector3Name(tempPositionViewVector3) + ")),0.0);\n"; 
				source += "\t" + code.getVector4Name(resultColor) + " += " + code.getScalarName(tempAttenuation) + " * " + code.getVector4Name(specularColor4) + " * pow(" + code.getScalarName(tempVdotVL) + "," + code.getScalarName(shininess) + " * 128.0);\n"; 
				source += "}\n";
				
				code.freeVector3(tempLightAttenuationVector3);
				code.freeVector3(tempLightPositionVector3);
				
				code.freeVector3(tempPositionViewVector3);

				code.freeVector3(tempVertexLightVector3);
				code.freeScalar(tempVertexLightDistance);
				code.freeScalar(tempNdotL);
				
				code.appendCode(source);
				returnValue = resultColor;
			}
		}
		
		code.freeScalar(shininess);
		code.freeVector4(ambientColor4);
		code.freeVector4(diffuseColor4);
		code.freeVector4(specularColor4);
		
		return returnValue;
	}
	
	public int getScalarFromShader(GL20GLSLCode code,int channel) {
		int returnValue = -1;
		
		if ((channel < GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MIN) ||
			(channel > GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MAX)) {
			int resultColor = createPhongCode(code);
			if (resultColor != -1) {
				int resultIndex = code.allocateTemporaryScalar();
				if (resultIndex != -1) {
					String source = new String(code.getScalarName(resultIndex) + " = " + code.getVector4Name(resultColor) + ".");
					
					switch (channel & 0x3) {
					case 0:
						source += "x;\n";
						break;
					case 1:
						source += "y;\n";
						break;
					case 2:
						source += "z;\n";
						break;
					case 3:
						source += "w;\n";
						break;
					}
					
					code.appendCode(source);
					returnValue = resultIndex;
				}
				
				code.freeVector4(resultColor);
			}
		}
		
		return returnValue;
	}
	
	public int getVector3FromShader(GL20GLSLCode code,int startChannel) {
		int returnValue = -1;
		
		if ((startChannel + 2 < GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MIN) ||
			(startChannel > GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MAX)) {
			int resultColor = createPhongCode(code);
			if (resultColor != -1) {
				int resultIndex = code.allocateTemporaryVector3();
				if (resultIndex != -1) {
					String source = new String(code.getVector3Name(resultIndex) + " = " + code.getVector4Name(resultColor) + ".");
					
					for (int i=0;i < 3;i++) {
						switch ((startChannel + i) & 0x3) {
						case 0:
							source += "x";
							break;
						case 1:
							source += "y";
							break;
						case 2:
							source += "z";
							break;
						case 3:
							source += "w";
							break;
						}
					}
					
					source += ";\n";
					code.appendCode(source);
					returnValue = resultIndex;
				}
				
				code.freeVector4(resultIndex);
			}
		}
		
		return returnValue;
	}
	
	public int getVector4FromShader(GL20GLSLCode code,int startChannel) {
		int returnValue = -1;
		
		if ((startChannel + 3 < GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MIN) ||
			(startChannel > GL20ResourceShaderFragment.GL20CHANNEL_DERIVATE_MAX)) {
			int resultColor = createPhongCode(code);
			if (resultColor != -1) {
				int resultIndex = resultColor;
				if (code.isConstant(resultColor) == true)
					resultIndex = code.allocateTemporaryVector4();
				
				if (resultIndex != -1) {
					String source = new String(code.getVector4Name(resultIndex) + " = " + code.getVector4Name(resultColor) + ".");
					
					for (int i=0;i < 4;i++) {
						switch ((startChannel + i) & 0x3) {
						case 0:
							source += "x";
							break;
						case 1:
							source += "y";
							break;
						case 2:
							source += "z";
							break;
						case 3:
							source += "w";
							break;
						}
					}
					
					source += ";\n";
					code.appendCode(source);
					returnValue = resultIndex;
				}				
			}
		}
		
		return returnValue;
	}
	
	final public GL20ResourceShaderFragment getDiffuseMap() {
		return (diffuseMap != null) ? diffuseMap : DEFAULT_DIFFUSE_MAP;
	}
	
	final public GL20ResourceShaderFragment getSpecularMap() {
		return (specularMap != null) ? specularMap : DEFAULT_SPECULAR_MAP;
	}
	
	final public GL20ResourceShaderFragment getTransparencyMap() {
		return (transparencyMap != null) ? transparencyMap : DEFAULT_TRANSPARENCY_MAP;
	}
	
	final public GL20ResourceShaderFragment getTransparencyShininessMap() {
		return (transparencyShininessMap != null) ? transparencyShininessMap : DEFAULT_TRANSPARENCY_SHININESS_MAP;
	}
	
	final public GL20ResourceShaderFragment getTransparencyDiffuseMap() {
		return (transparencyDiffuseMap != null) ? transparencyDiffuseMap : DEFAULT_TRANSPARENCY_DIFFUSE_MAP;
	}
	
	final public GL20ResourceShaderFragment getAmbientMap() {
		return (ambientMap != null) ? ambientMap : DEFAULT_AMBIENT_MAP;
	}
	
	final public GL20ResourceShaderFragment getEmissiveMap() {
		return (emissiveMap != null) ? emissiveMap : DEFAULT_EMISSIVE_MAP;
	}
	
	final public GL20ResourceShaderFragment getShininessMap() {
		return (shininessMap != null) ? shininessMap : DEFAULT_SHININESS_MAP;
	}
	
	final public void setDiffuseMap(GL20ResourceShaderFragment diffuseMap) {
		if (this.diffuseMap != diffuseMap) {
			if (this.diffuseMap != null)
				this.diffuseMap.unregisterUser();
			
			this.diffuseMap = diffuseMap;
			
			if (this.diffuseMap != null)
				this.diffuseMap.registerUser();
			changeMask |= DIFFUSE_MAP;
		}
	}
	
	final public void setSpecularMap(GL20ResourceShaderFragment specularMap) {
		if (this.specularMap != specularMap) {
			if (this.specularMap != null)
				this.specularMap.unregisterUser();
			
			this.specularMap = specularMap;
			if (this.specularMap != null)
				this.specularMap.registerUser();
			changeMask |= SPECULAR_MAP;
		}
	}
	
	final public void setTransparencyMap(GL20ResourceShaderFragment transparencyMap) {
		if (this.transparencyMap != transparencyMap) {
			if (this.transparencyMap != null)
				this.transparencyMap.unregisterUser();
			
			this.transparencyMap = transparencyMap;
			if (this.transparencyMap != null)
				this.transparencyMap.registerUser();
			changeMask |= TRANSPARENCY_MAP;
		}
	}
	
	final public void setTransparencyShininessMap(GL20ResourceShaderFragment transparencyShininessMap) {
		if (this.transparencyShininessMap != transparencyShininessMap) {
			if (this.transparencyShininessMap != null)
				this.transparencyShininessMap.unregisterUser();
			
			this.transparencyShininessMap = transparencyShininessMap;
			if (this.transparencyShininessMap != null)
				this.transparencyShininessMap.registerUser();
			changeMask |= TRANSPARENCY_SHININESS_MAP;
		}
	}
	
	final public void setTransparencyDiffuseMap(GL20ResourceShaderFragment transparencyDiffuseMap) {
		if (this.transparencyDiffuseMap != transparencyDiffuseMap) {
			if (this.transparencyDiffuseMap != null)
				this.transparencyDiffuseMap.unregisterUser();
			
			this.transparencyDiffuseMap = transparencyDiffuseMap;
			if (this.transparencyDiffuseMap != null)
				this.transparencyDiffuseMap.registerUser();
			
			changeMask |= TRANSPARENCY_DIFFUSE_MAP;
		}
	}
	
	final public void setAmbientMap(GL20ResourceShaderFragment ambientMap) {
		if (this.ambientMap != ambientMap) {
			if (this.ambientMap != null)
				this.ambientMap.unregisterUser();
			
			this.ambientMap = ambientMap;
			if (this.ambientMap != null)
				this.ambientMap.registerUser();
			
			changeMask |= AMBIENT_MAP;
		}
	}
	
	final public void setEmissiveMap(GL20ResourceShaderFragment emissiveMap) {
		if (this.emissiveMap != emissiveMap) {
			if (this.emissiveMap != null)
				this.emissiveMap.unregisterUser();
			
			this.emissiveMap = emissiveMap;
			if (this.emissiveMap != null)
				this.emissiveMap.registerUser();
			
			changeMask |= EMISSIVE_MAP;
		}
	}
	
	final public void setShininessMap(GL20ResourceShaderFragment shininessMap) {
		if (this.shininessMap != shininessMap) {
			if (this.shininessMap != null)
				this.shininessMap.unregisterUser();
			
			this.shininessMap = shininessMap;
			if (this.shininessMap != null)
				this.shininessMap.registerUser();
			
			changeMask |= SHININESS_MAP;
		}
	}
	
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else if (super.isUpToDate() == false)
			return false;
		else if (getDiffuseMap().isUpToDate() == false)
			return false;
		else if (getSpecularMap().isUpToDate() == false)
			return false;
		else if (getTransparencyMap().isUpToDate() == false)
			return false;
		else if (getTransparencyShininessMap().isUpToDate() == false)
			return false;
		else if (getTransparencyDiffuseMap().isUpToDate() == false)
			return false;
		else if (getAmbientMap().isUpToDate() == false)
			return false;
		else if (getEmissiveMap().isUpToDate() == false)
			return false;
		else if (getShininessMap().isUpToDate() == false)
			return false;
		
		return true;
	}
	
	public void update() {
		setShader(getShader());

		// update all maps
		getDiffuseMap().update();
		getSpecularMap().update();
		getTransparencyMap().update();
		getTransparencyShininessMap().update();
		getTransparencyDiffuseMap().update();
		getAmbientMap().update();
		getEmissiveMap().update();
		getShininessMap().update();

		if (changeMask != 0) {
			
			changeMask = 0;
		}
		
		
		super.update();
	}
	
	public void destroy() {
		// unregister all used maps
		setDiffuseMap(null);
		setSpecularMap(null);
		setTransparencyMap(null);
		setTransparencyShininessMap(null);
		setTransparencyDiffuseMap(null);
		setAmbientMap(null);
		setEmissiveMap(null);
		setShininessMap(null);
	}
}