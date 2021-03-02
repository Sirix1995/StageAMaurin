package de.grogra.ext.limelight.objects;

import javax.vecmath.Vector3f;

import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ray.physics.Spectrum3f;


public interface BSDF extends Cloneable {
	
	//Stores BSDF value for incoming direction wIn and outgoing direction wOut in f
	public abstract void f(Vector3f wOut, Vector3f wIn, Spectrum3f f);

	//Samples incoming direction wIn for outgoing direction wOut. stores BSDF value for wIn and wOut in factor
	public abstract float sampleDirection(Vector3f wOut, Vector3f wIn, MersenneTwister rand, Spectrum3f factor);
	
	//returns pdf value for wIn and wOut
	public abstract float pdf(Vector3f wOut, Vector3f wIn);

	//returns diffuse part of BSDF, needed for area lights
	public abstract Spectrum3f getDiffuseColor();

	//true for completely specular BSDFs
	public boolean isSpecular();
	
	//returns a deep copy of the BSDF
	public abstract BSDF clone();
	
	
	
	
	
	
}