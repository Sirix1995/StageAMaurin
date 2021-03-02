package de.grogra.imp3d.glsl.material.channel;

import java.util.HashMap;

import de.grogra.math.ChannelMap;

public class ChannelCollection {
	private void AddToHashMap(GLSLChannelMap sj) {
		map.put(sj.instanceFor(), sj);
	}

	public void initMap() {
		AddToHashMap(new GLSLRGBColor());
		AddToHashMap(new GLSLGraytone());
		AddToHashMap(new GLSLChecker());
		AddToHashMap(new GLSLVolumeChecker());
		AddToHashMap(new GLSLAffineUVTransformation());
		AddToHashMap(new GLSLXYZTransformation());
		AddToHashMap(new GLSLImageMap());
		AddToHashMap(new GLSLVolumeTurbulence());
		AddToHashMap(new GLSLTurbulence());
		AddToHashMap(new GLSLGradient());
		AddToHashMap(new GLSLLeopard());
		AddToHashMap(new GLSLWood());
		AddToHashMap(new GLSLGranite());
		AddToHashMap(new GLSLCarpenter());
		AddToHashMap(new GLSLMandel());
		AddToHashMap(new GLSLJulia());
		AddToHashMap(new GLSLChannelBlend());
		AddToHashMap(new GLSLBumpMap());
	}

	public GLSLChannelMap getGLSLObject(ChannelMap inp) {
		return inp != null ? map.get(inp.getClass()) : null;
	}

	private final HashMap<Class, GLSLChannelMap> map = new HashMap<Class, GLSLChannelMap>();

}
