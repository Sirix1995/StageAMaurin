package de.grogra.imp3d.glsl.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.shading.Shader;

/**
 * Interface for all GLSLShaders. Activates / compiles and deletes
 * shaderprograms. Holds a list of all GLSLShaders.
 * @author Konni Hartmann
 */
public class CachedShaderCollection {
	
	private int graph_stamp = 0;
		
	/**
	 * Cache for all compiled Shaders of the scenegraph
	 */
	private final HashMap<ShaderConfiguration, GLSLManagedShader> shaderCache = new HashMap<ShaderConfiguration, GLSLManagedShader> ();
	
	GLSLManagedShader currentShader = null;
	
	/**
	 * Retrieve the cached entry for a Configuration/Object-Pair. 
	 * If no entry exists method will generate and store a new one
	 * @param sc
	 * @param s
	 * @return the fetched managed Shader
	 */
	public GLSLManagedShader getCacheEntry(OpenGLState glState, GLSLDisplay disp, ShaderConfiguration sc, Object s) {
		sc.set(glState, disp, s);
		GLSLManagedShader manS = shaderCache.get(sc);
		if(manS == null) {
			// Not cached so generate a new one
			manS = sc.getShaderByDefaultCollection(disp, s);
			if(manS == null) {
				// Not found.. throw exception
				System.err.println("Found none for: "+sc+"/"+s);
				GLSLDisplay.printDebugInfoN("! Error retrieving valid shader for: " + s);
				return null;
			}
			
			assert (manS instanceof GLSLManagedShader);
			manS = (GLSLManagedShader)manS.getInstance();
			sc = sc.clone();		
			manS.setConfig(sc);
			GLSLDisplay.printDebugInfoN("Added new Shader: "+s+" with Config: "+sc);
			shaderCache.put(sc, manS);
		}
		
		if(manS.getMaterial_stamp() != graph_stamp) {
			shaderChanged = true;
			manS.setMaterial_stamp(graph_stamp);
		}

		return manS;
	}
	
	/**
	 * Will obtain and activate a GLSLShader to emulate the input shaders 
	 * behavior. Will cache instances of GLSLShaders inside <code>shaderCache</code>
	 * @param glState current GL instance
	 * @param disp active GLDisplay instance
	 * @param s shader that should be activated
	 */
	public void findAndActivateShader(OpenGLState glState, GLSLDisplay disp, ShaderConfiguration sc, Shader s) {
		GLSLManagedShader cs = getCacheEntry(glState, disp, sc, s);
		currentShader = cs;
		if(cs == null)
		{
			System.err.println("Shader not found:"+s);
			return;
		}
		cs.activateShader(glState, disp, s);
			
		shaderChanged = false;
	}
	
	/**
	 * Will obtain a GLSLShader to emulate the input shaders 
	 * behavior. Will cache instances of GLSLShaders inside <code>shaderCache</code>
	 * @param glState current GL instance
	 * @param disp active GLDisplay instance
	 * @param s shader that should be activated
	 */
	public GLSLManagedShader findShader(OpenGLState glState, GLSLDisplay disp, ShaderConfiguration sc, Shader s) {
		GLSLManagedShader cs = getCacheEntry(glState, disp, sc, s);
		if(cs == null)
		{
			System.err.println("Shader not found:"+s);
			return null;
		}			
		shaderChanged = false;
		return cs;
	}
	
	boolean shaderChanged = true;
	
	public void setCurrentStamp(int stamp) {
		graph_stamp = stamp;
	}
	
	/**
	 * Removes unused GLSLShaders from <code>shaderCache</code>
	 * @param glState
	 */
	public void removeUnusedShaders(OpenGLState glState) {
		if (!shaderChanged) return;
		Collection<GLSLManagedShader> c = shaderCache.values();   
		Iterator<GLSLManagedShader> i = c.iterator();   
		while(i.hasNext()) {
			GLSLManagedShader s = i.next();
			// Shader was not updated	
			if(s.getMaterial_stamp() != graph_stamp) {
				s.deleteShader(glState.getGL(), false);
				i.remove();
			}
		}   
		currentShader = null;
	}

	/**
	 * Get current active GLSLShader. Will only work for managed Shaders. 
	 * If a non managed Shader is active result is undefined.
	 * @return returns active shader as corresponding GLSLShaderManagedShader 
	 */
	public GLSLManagedShader getCurrentShader() {
		return currentShader;
	}
	
	/**
	 * Remove all compiled Shaders from <code>shaderCache</code> and <code>shaderMap</code>.
	 * @param glState
	 */
	public void deleteAll(OpenGLState glState, boolean javaonly) {
		Collection<GLSLManagedShader> c = shaderCache.values();   
		Iterator<GLSLManagedShader> i = c.iterator();   
		while(i.hasNext()) {
			GLSLManagedShader s = i.next();
			s.deleteShader(glState.getGL(), javaonly);
			i.remove();
		}

		graph_stamp = 0;
	}
}
