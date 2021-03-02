package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.math.RGBColor;

public class FluxPointLight extends FluxLight {

	PointLight pointLight;
	
	public FluxPointLight(PointLight pointLight) {
		super( pointLight );
		this.pointLight = pointLight;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		
		RGBColor power3f = (RGBColor) pointLight.getColor().clone();
		power3f.scale (3 * pointLight.getPower() / (power3f.x + power3f.y + power3f.z));
		
		serializeLightBase( out, LIGHT_POINT, power3f, null );
	}
	
}
