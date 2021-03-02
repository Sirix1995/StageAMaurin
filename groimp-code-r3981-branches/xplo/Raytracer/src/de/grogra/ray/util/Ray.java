
package de.grogra.ray.util;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Interior;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.geom.Line;

/**
 * This class represents a light ray.
 * 
 * @author Ole Kniemeyer
 */
public class Ray //extends Line
{

	//	private static long m_maxId = 1;
	//	private long m_id = -1;

	/**
	 * The ray origin in global coordinates. This field is only used in
	 * the context of {@link Interior#attenuate(Interior.Input, Color3f)}
	 * and the methods of {@link Light} and its subinterfaces.
	 */
	public final Point3f origin = new Point3f ();

	/**
	 * The ray direction. This direction unit vector is defined as the direction
	 * pointing away from a surface position in global coordinates.
	 * I.e., for an outgoing ray,
	 * it equals its direction, whereas for an incident ray, the direction
	 * is its negated direction.
	 */
	public final Vector3f direction = new Vector3f ();

	/**
	 * The ray color. Depending on context, this may represent
	 * BRDF-related values for the color components R, G, B, rather
	 * than simply an RGB color.
	 */
	public final Spectrum3f color;

	/**
	 * The spectrum of the ray. Depending on context, this may represent
	 * the ray's spectral radiant power or other properties.
	 */
	public final Spectrum spectrum;

	public float m_importance = 1.0f;

	/**
	 * The probability density of direction for this ray.
	 * This field is set by
	 * {@link Shader#generateRandomRays Shader.generateRandomRays} and
	 * {@link Light#generateRandomRays Light.generateRandomRays}.
	 * If p<sub>&omega;<sup>+</sup></sub> is the probability density
	 * that has been used for generating the random ray direction
	 * (measured with respect to projected solid angle &omega;<sup>+</sup>, i.e.,
	 * d&omega;<sup>+</sup> = cos &theta; d&omega;),
	 * then this field is set to
	 * p<sub>&omega;<sup>+</sup></sub>(<code>direction</code>). It is not
	 * defined for rays emanating from a directional light source. It
	 * may be {@link Float#POSITIVE_INFINITY} as a result of
	 * {@link Shader#generateRandomRays Shader.generateRandomRays}, namely
	 * for ideal specular reflection or transmission.
	 */
	public float directionDensity = 1.0f;

	/**
	 * The probability density of origin for this ray.
	 * This field is set by
	 * {@link Light#generateRandomRays Light.generateRandomRays}.
	 * If p<sub>x</sub> is the probability density
	 * that has been used for generating the random ray origin,
	 * then this field is set to
	 * p<sub>x</sub>(<code>origin</code>). It is not
	 * defined for rays emanating from a point light source.
	 */
	public float originDensity = 1.0f;

	public boolean reflected = true;//false;

	public boolean ambient = false;
	
	
	public boolean valid = true;

	public Ray (Spectrum factory)
	{
		spectrum = factory.newInstance ();
		color = (spectrum instanceof Spectrum3f) ? (Spectrum3f) spectrum : null;
	}

	public Ray ()
	{
		this (new Spectrum3f ());
//		start = 0;
//		end = Double.POSITIVE_INFINITY;
		//		nextId();
		//		color = new Color3f();
		//		direction = new Vector3f();
		//		origin = new Point3f();
	}

	public Ray (Ray temp)
	{
		this ();
		origin.set (temp.origin);
		direction.set (temp.direction);
		color.set ((Color3f) temp.color);
		m_importance = temp.m_importance;
		directionDensity = temp.directionDensity;
		originDensity = temp.originDensity;
		ambient = temp.ambient;
	}

	public Ray (Point3f newOrigin, Vector3f newDirection, float probability)
	{
		this ();
		origin.set (newOrigin);
		direction.set (newDirection);
		m_importance = probability;
	}

	public Ray (Point3d newOrigin, Vector3d newDirection, float probability)
	{
		this ();
		origin.set (newOrigin);
		direction.set (newDirection);
		m_importance = probability;
	}

	public boolean equals (Object obj)
	{
		if (!(obj instanceof Ray))
		{
			return false;
		}

		Ray obj_ray = (Ray) obj;
		return ((this.getOrigin ().x == obj_ray.getOrigin ().x)
			&& (this.getOrigin ().y == obj_ray.getOrigin ().y)
			&& (this.getOrigin ().z == obj_ray.getOrigin ().z)
			&& (this.getDirection ().x == obj_ray.getDirection ().x)
			&& (this.getDirection ().y == obj_ray.getDirection ().y)
			&& (this.getDirection ().z == obj_ray.getDirection ().z) && (this
			.getImportance () == obj_ray.getImportance ()));
	}

	public int hashCode ()
	{
		return this.getOrigin ().hashCode () + this.getDirection ().hashCode ()
			+ (int) this.getImportance () * 10000;
	}

	public Point3f getOrigin ()
	{
		return origin;
	}

	public Vector3f getDirection ()
	{
		return direction;
	}

	public Color3f getColor ()
	{
		return color;
	}

	public float getImportance ()
	{
		return m_importance;
	}

	public void setImportance (float value)
	{
		m_importance = value;
	}

	//	public long getId() { return m_id; }

	public void setRay (Ray ray)
	{
		this.getOrigin ().set (ray.getOrigin ());
		this.getDirection ().set (ray.getDirection ());
		this.getColor ().set (ray.getColor ());
		this.setImportance (ray.getImportance ());
		this.directionDensity = ray.directionDensity;
		this.originDensity = ray.originDensity;
		this.ambient = ray.ambient;
	}

	public void transform (Matrix4f mat, Ray ray)
	{
		ray.getOrigin ().set (this.getOrigin ());
		ray.getDirection ().set (this.getDirection ());
		mat.transform (ray.getOrigin ());
		mat.transform (ray.getDirection ());
		/*
		 Point3d transformed_origin = new Point3d(getOrigin());
		 mat.transform(transformed_origin);
		 Vector3d transformed_direction = new Vector3d(getDirection());
		 mat.transform(transformed_direction);
		 return new Ray(transformed_origin,transformed_direction,getImportance());
		 */
	}

	public void transform (Matrix4d mat, Ray ray)
	{
		ray.getOrigin ().set (this.getOrigin ());
		ray.getDirection ().set (this.getDirection ());
		mat.transform (ray.getOrigin ());
		mat.transform (ray.getDirection ());
	}

	public String toString ()
	{
		return "Ray[origin=" + origin + ", direction=" + direction + ", spectrum=" + spectrum + "]";
	}
	
	public Line convert2Line(){
		Line l = new Line();
		
		l.setLineAttributes(0, Double.POSITIVE_INFINITY);
		l.direction.set( this.direction);
		l.origin.set(this.origin);
		l.directionDensity = this.directionDensity;
		l.originDensity = this.originDensity;
		this.spectrum.get(l.spectrum);
		l.reflected = this.reflected;
		l.valid = this.valid;
		
		return l;
	}

}
