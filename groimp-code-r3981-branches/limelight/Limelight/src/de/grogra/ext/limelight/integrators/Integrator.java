package de.grogra.ext.limelight.integrators;

import de.grogra.ext.limelight.LimelightVisitor;
import de.grogra.ext.limelight.Ray;
import de.grogra.ray.physics.Spectrum3f;

public abstract class Integrator {
	LimelightVisitor scene;
	
	public Integrator(){};

	public abstract void getRadiance(Ray ray, Spectrum3f radiance);
	

		
	
}
