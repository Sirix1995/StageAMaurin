package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import javax.vecmath.Vector3f;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.shading.FluxShader;
import de.grogra.imp3d.objects.LightDistribution;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.math.RGBColor;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum3d;

public class FluxSkyLight extends FluxLight {

	private Sky light;
	private FluxShader fluxShader;
	
	private Spectrum3d spectrum_distribution [][];
	private double pdf[][];
	private LightDistribution lightDistribution;
	
	public FluxSkyLight(Sky light, FluxShader fluxShader) {
		super(light);
		this.light = light;
		this.fluxShader = fluxShader;
	}

	private void discritizeSky()
	{
		int skyResolution = FluxSettings.getSkyResolution();
		
		Environment env = new Environment();
		env.point.set(0,0,0);
		env.localPoint.set(0,0,0);
		env.normal.set(0,0,1);
		env.dpdu.set(1,0,0);
		env.dpdv.set(0,1,0);
		env.solid = false;
		
		Vector3f in = new Vector3f(0,0,-1);
		Vector3f out = new Vector3f(0,0,1);
		Spectrum3d spectrum = new Spectrum3d();
		
		spectrum_distribution = new Spectrum3d[skyResolution][skyResolution];
		pdf = new double[skyResolution][skyResolution];
		
		// discretize sky
		for(int y = 0 ; y < skyResolution; y++ )
		{
			for(int x = 0 ; x < skyResolution; x++ )
			{
				spectrum_distribution[y][x] = new Spectrum3d();
				
				float u = x / (float)skyResolution;
				float v = y / (float)skyResolution;
				
				env.uv.set(1.5f-v,-u);
				
				LightDistribution.map2cartesian(in,u,v);
				out.negate(in);
				
				env.normal.set(out);
				env.localPoint.set(in);
				env.userVector.set(1,1,1);
				
				light.computeExitance(env , spectrum);
				light.computeBSDF(env, in, spectrum, out, false, spectrum_distribution[y][x]);
				
				pdf[y][x] = spectrum_distribution[y][x].integrate() / 3.f;
			}
		}
		
		// compute corresponding light distribution over unit sphere
		lightDistribution = new LightDistribution( pdf );
	}
	
	public void serializeSkyRadiance(ComputeByteBuffer outStream) throws IOException
	{
		double power = lightDistribution.getPower();
		
		outStream.writeInt(spectrum_distribution.length);
		outStream.writeInt(spectrum_distribution[0].length);
		
		// serialize sky radiance
		for( int y = 0 ; y < spectrum_distribution.length ; y++ )
			for( int x = 0 ; x < spectrum_distribution[y].length ; x++ )
			{
				outStream.writeFloat( (float)(spectrum_distribution[y][x].x / power) );
				outStream.writeFloat( (float)(spectrum_distribution[y][x].y / power) );
				outStream.writeFloat( (float)(spectrum_distribution[y][x].z / power) );
			}
	}
	
	public void serializeSky(ComputeByteBuffer outStream) throws IOException
	{
		// serialize radiance distribution
		double [][] lipdf = lightDistribution.getDistribution();
		double [] licdf = lightDistribution.getLinearCDF();
		
		outStream.writeInt( lightDistribution.getWidth() );
		outStream.writeInt( lightDistribution.getHeight() );
		
		// serialize pdf
		for( int y = 0 ; y < lightDistribution.getHeight() ; y++ )
			for( int x = 0 ; x < lightDistribution.getWidth() ; x++ )
				outStream.writeFloat( (float)lipdf[y][x] );
				
		// serialize cdf
		for( int i = 0 ; i < licdf.length ; i++ )
			outStream.writeFloat( (float)licdf[i] );
	}
	
	@Override
	public void serialize(ComputeByteBuffer outStream)
			throws IOException {
		discritizeSky();
				
		double power = light.getPowerDensity();
		serializeLightBase( outStream, LIGHT_SKY, new RGBColor((float)power, (float)power, (float)power), null );
				
		if( fluxShader == null )
			outStream.writeInt(0);
		else
			outStream.writeInt(fluxShader.getOffset());
		
		// relative offset of radiance image
		outStream.writeInt((2 + 2 * lightDistribution.getHeight() * lightDistribution.getWidth()) * 4);
		
		// write sample distribution
		serializeSky(outStream);
		
		// write sky radiance image
		serializeSkyRadiance( outStream );
	}

	public int getSampleCount() 
	{
		Shader shader = light.getShader();
		
		if( shader instanceof SunSkyLight )
		{
			SunSkyLight sky = (SunSkyLight)shader;
			
			if( sky.isDisableLight() ) 
				return 0;
		}
		
		return 32;
	};
	
}
