package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.shading.FluxSpectrum;
import de.grogra.imp3d.objects.SpectralLight;
import de.grogra.imp3d.spectral.SpectralCurve;

public class FluxSpectralLight extends FluxLight {

	SpectralLight spectralLight;
	FluxLight input;
	
	public FluxSpectralLight(SpectralLight spectralLight, FluxLight input) {
		super(spectralLight);
		
		this.spectralLight = spectralLight;
		this.input = input;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {

		SpectralCurve SPD = spectralLight.getSpectrum().getSpectralDistribution();
		
		// serialize spectral base
		serializeLightBase( out, LIGHT_SPECTRAL, new Vector3f(), SPD );
		
		// serialize spectrum
		FluxSpectrum spectrum = new FluxSpectrum(SPD);
		
		// get power
		float power = spectralLight.getPower();
		
		// write color as rgb distribution
		Point3f color = spectrum.getRGBDistribution();
		color.scale(3.f * power / (color.x + color.y + color.z)); 
		out.write(color);
		
		// serialize SPD
		FluxSpectrum.serializeNormalizedSPD(out,SPD,power);
		
		// serialize input light
		input.serialize(out);
	}

}
