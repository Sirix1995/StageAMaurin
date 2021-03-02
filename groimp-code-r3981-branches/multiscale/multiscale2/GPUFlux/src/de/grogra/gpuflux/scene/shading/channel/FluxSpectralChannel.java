package de.grogra.gpuflux.scene.shading.channel;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.shading.FluxSpectrum;
import de.grogra.imp3d.shading.ChannelSPD;

public class FluxSpectralChannel extends FluxChannelMap {

	private FluxSpectrum spectrum;

	public FluxSpectralChannel(ChannelSPD sd) {
		spectrum = new FluxSpectrum( sd.getSpectraldistribution().getSpectralDistribution() );
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		//out.writeInt( CHANNEL_SPECTRAL );
		serialize( out, CHANNEL_SPECTRAL );
		spectrum.serialize(out);
	}

}
