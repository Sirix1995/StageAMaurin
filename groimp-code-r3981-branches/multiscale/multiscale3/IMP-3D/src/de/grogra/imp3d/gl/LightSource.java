package de.grogra.imp3d.gl;

import javax.vecmath.Color4f;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

/**
 * Store all necessary data to describe an OpenGL light source. OpenGL supports
 * three type of lights:<br>
 * <ul>
 * <li>point light
 * <li>spot light
 * <li>directional light
 * </ul>
 * 
 * Every light has a position specified in 4D-coordinates. If the w-component of
 * the position is 0, then the light is infinitely far away. So the result is a
 * directional light. The direction the light is coming from then depends on the
 * x-, y- and z-components of the light sources position.
 * 
 * Every light also has an associated light direction and spot angle. The light
 * source will emit light from its position in the given direction. Every object
 * within the light cone will receive light. Any object outside the light cone
 * will not receive any light from this light source. The spot angle determines
 * the size of the spot. Spotlights use angles from 0 to 90 degrees. A
 * pointlight ?nd a directional light uses an angle of 180 degrees.
 * 
 * @author nmi
 * 
 */
class LightSource
{
	static final int LIGHT_TYPE_NONE = 0;

	static final int LIGHT_TYPE_POINT = 1;

	static final int LIGHT_TYPE_SPOT = 2;

	static final int LIGHT_TYPE_DIRECTIONAL = 3;

	int stamp; // used to track when light was last changed

	int lightType; // one of the LIGHT_TYPE_* constants

	Point4d lightPos; // location of light source in homogenous coordinates

	Vector3d lightDir; // direction for spotlights

	float spotExponent;

	float spotCutoff;

	float constantAttenuation;

	float linearAttenuation;

	float quadraticAttenuation;

	Color4f ambientColor;

	Color4f diffuseColor;

	Color4f specularColor;

	LightSource ()
	{
		lightType = LIGHT_TYPE_NONE;

		// set parameters to opengl default values
		ambientColor = new Color4f (0, 0, 0, 1);
		diffuseColor = new Color4f (1, 1, 1, 1);
		specularColor = new Color4f (1, 1, 1, 1);
		lightPos = new Point4d (0, 0, 1, 0);
		lightDir = new Vector3d (0, 0, -1);
		spotExponent = 0;
		spotCutoff = 180;
		constantAttenuation = 1;
		linearAttenuation = 0;
		quadraticAttenuation = 0;
	}

	public boolean equals (Object obj)
	{
		//		boolean result = super.equals (obj);
		boolean result = false;

		if (obj != null && obj instanceof LightSource)
		{
			result = true;
			LightSource that = (LightSource) obj;
			//			result &= stamp == that.stamp;
			result &= lightType == that.lightType;
			result &= lightPos.equals (that.lightPos);
			result &= lightDir.equals (that.lightDir);
			result &= spotExponent == that.spotExponent;
			result &= spotCutoff == that.spotCutoff;
			result &= constantAttenuation == that.constantAttenuation;
			result &= linearAttenuation == that.linearAttenuation;
			result &= quadraticAttenuation == that.quadraticAttenuation;
			result &= ambientColor.equals (that.ambientColor);
			result &= diffuseColor.equals (that.diffuseColor);
			result &= specularColor.equals (that.specularColor);
		}

		return result;
	}

	public int hashCode ()
	{
		//		int hash = super.hashCode ();
		int hash = 0;

		//		hash = (hash << 13) | (hash >>>= 19);
		//		hash ^= stamp;
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= lightType;
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= lightPos.hashCode ();
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= lightDir.hashCode ();
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= Float.floatToIntBits (spotExponent);
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= Float.floatToIntBits (spotCutoff);
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= Float.floatToIntBits (constantAttenuation);
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= Float.floatToIntBits (linearAttenuation);
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= Float.floatToIntBits (quadraticAttenuation);
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= ambientColor.hashCode ();
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= diffuseColor.hashCode ();
		hash = (hash << 13) | (hash >>>= 19);
		hash ^= specularColor.hashCode ();

		return hash;
	}
}
