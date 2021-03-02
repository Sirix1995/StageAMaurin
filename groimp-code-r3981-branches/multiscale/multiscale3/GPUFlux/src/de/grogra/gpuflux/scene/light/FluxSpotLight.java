package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.math.RGBColor;

public class FluxSpotLight extends FluxPointLight { 
	
	private SpotLight light;
	
	public FluxSpotLight( SpotLight light )
	{
		super( light );
		this.light = light;
	}
	
	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		RGBColor power3f = (RGBColor) pointLight.getColor().clone();
		power3f.scale (3 * pointLight.getPower() / (power3f.x + power3f.y + power3f.z));
		
		serializeLightBase( out, LIGHT_SPOT, power3f, null );
		out.writeFloat( light.getInnerAngle() );
		out.writeFloat( light.getOuterAngle() );
	}
}
