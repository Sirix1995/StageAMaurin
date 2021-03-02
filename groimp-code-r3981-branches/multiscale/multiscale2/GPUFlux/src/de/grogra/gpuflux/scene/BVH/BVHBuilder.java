package de.grogra.gpuflux.scene.BVH;

import java.util.Vector;

import de.grogra.gpuflux.scene.volume.FluxVolume;

public interface BVHBuilder {
	
	public BVHTree construct( Vector<? extends FluxVolume> volumes );
	public String getLog(); 
}
