package de.grogra.gpuflux.scene.shading;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.shading.channel.FluxChannelMap;
import de.grogra.gpuflux.scene.shading.channel.FluxChannelMapBuilder;
import de.grogra.imp3d.shading.Phong;
import de.grogra.math.ColorMap;
import de.grogra.math.Graytone;

public class FluxPhongShader extends FluxShader {

	private Phong phong;
	private FluxChannelMap diffuse;
	private FluxChannelMap diffuseTransparancy;
	private FluxChannelMap shininess;
	private FluxChannelMap specular;
	private FluxChannelMap transparancy;
	private FluxChannelMap transparancyShininess;
	
	public static final ColorMap DEFAULT_DIFFUSE = new Graytone (0.5f);
	public static final ColorMap DEFAULT_TRANSPARENCY = new Graytone (0);
	public static final ColorMap DEFAULT_SPECULAR = null; //new Graytone (0);
	public static final ColorMap DEFAULT_DIFFUSE_TRANSPARENCY = new Graytone (0);
	public static final ColorMap DEFAULT_AMBIENT = new Graytone (0);
	public static final ColorMap DEFAULT_EMISSIVE = new Graytone (0);

	public static final ColorMap DEFAULT_SHININESS = new Graytone (0.37275f);
	public static final ColorMap DEFAULT_TRANSPARENCY_SHININESS = new Graytone (1.f);
	
	public FluxPhongShader(Phong phong, FluxChannelMapBuilder channelBuilder ) {
		this.phong = phong;
			
		diffuse = channelBuilder.buildChannelMap(phong.getDiffuse(), DEFAULT_DIFFUSE);
		diffuseTransparancy = channelBuilder.buildChannelMap(phong.getDiffuseTransparency(),DEFAULT_DIFFUSE_TRANSPARENCY);
		shininess = channelBuilder.buildChannelMap( phong.getShininess(), DEFAULT_SHININESS);
		specular = channelBuilder.buildChannelMap( phong.getSpecular(),DEFAULT_SPECULAR); 
		transparancy = channelBuilder.buildChannelMap( phong.getTransparency(),DEFAULT_TRANSPARENCY);
		transparancyShininess = channelBuilder.buildChannelMap( phong.getTransparencyShininess(),DEFAULT_TRANSPARENCY_SHININESS);
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		out.writeInt(SHADER_PHONG);
		
		serialize( out , diffuse );
		serialize( out , specular );
		serialize( out , shininess );
		
		serialize( out , diffuseTransparancy );
		serialize( out , transparancy );
		serialize( out , transparancyShininess );
		
		out.writeBoolean(phong.isInterpolatedTransparency());
	}

	private void serialize(ComputeByteBuffer out, FluxChannelMap channel) throws IOException {
		if( channel == null )
			out.writeInt( -1 );
		else
			out.writeInt( channel.getOffset() );
	}

}
