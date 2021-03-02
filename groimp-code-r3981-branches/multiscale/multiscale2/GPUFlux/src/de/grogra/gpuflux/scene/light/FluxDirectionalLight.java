package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.math.RGBColor;

public class FluxDirectionalLight extends FluxLight {

	private DirectionalLight light;

	public FluxDirectionalLight( DirectionalLight light )
	{
		super( light );
		this.light = light;
	}
	
	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		RGBColor power3f = (RGBColor) light.getColor().clone();
		power3f.scale (3 * light.getPowerDensity() / (power3f.x + power3f.y + power3f.z));
		
		serializeLightBase( out, LIGHT_DIRECTIONAL, power3f, null );
	}
}
