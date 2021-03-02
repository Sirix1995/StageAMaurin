package de.grogra.ray.shader;

public interface RTMedium {

	public final static float REFRACTION_INDEX_DEFAULT = 1.0f;
	
	public final static float REFRACTION_INDEX_VACUUM  = 1.0f;
	public final static float REFRACTION_INDEX_AIR     = 1.00029f;
	public final static float REFRACTION_INDEX_WATER   = 1.333f;
	public final static float REFRACTION_INDEX_GLASS   = 1.5f;
	public final static float REFRACTION_INDEX_ICE     = 1.309f;
	public final static float REFRACTION_INDEX_DIAMOND = 2.417f;
	public final static float REFRACTION_INDEX_RUBY    = 1.77f;
	
	
	float getIndexOfRefraction ();
	
}
