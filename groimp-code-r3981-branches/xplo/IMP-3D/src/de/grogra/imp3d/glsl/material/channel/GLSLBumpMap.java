package de.grogra.imp3d.glsl.material.channel;

import java.awt.Image;

import javax.vecmath.Vector3f;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.BumpMap;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;

public class GLSLBumpMap extends GLSLChannelMapNode {
//	final String derive =
//	"vec3 derivate(sampler2D tex, float normalStrength)
//	{
//	 vec2 texelSize = vec2(1.0 / 1024., 1.0 / 512.);
//	 float b = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(0, 1)).r;
//	 float t = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(0, -1)).r;
//	 float r = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(1, 0)).r;
//	 float l = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(-1, 0)).r;
//	 float br = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(1, 1)).r;
//	 float tr = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(1, -1)).r;
//	 float bl = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(-1, 1)).r;
//	 float tl = texture2D(tex, gl_TexCoord[0].xy + texelSize * vec2(-1, -1)).r;
//
//	 float dX = tr + 2.0*r + br -tl - 2.0*l - bl;
//	 
//	    // Compute dy using Sobel:
//	    //           -1 -2 -1 
//	    //            0  0  0
//	    //            1  2  1
//	 float dY = bl + 2.0*b + br -tl - 2.0*t - tr;
//		
//	 return normalize(vec3(dX, dY, 1.0 / normalStrength));
//	}";

	
	
//	if (((channel >= Channel.NX) && (channel <= Channel.NZ))
//			|| ((channel >= Channel.DPXDU) && (channel <= Channel.DPZDU))
//			|| ((channel >= Channel.DPXDV) && (channel <= Channel.DPZDV)))
//		{
//			if (displacement == null)
//			{
//				return data.forwardGetFloatValue (in);
//			}
//			Vector3f n = data.v3f0, dpdu = data.v3f1, dpdv = data.v3f2;
//
//			in.getTuple3f (n, data, Channel.NX);
//			in.getTuple3f (dpdu, data, Channel.DPXDU);
//			in.getTuple3f (dpdv, data, Channel.DPXDV);
//
//			ChannelData d = data.getData (displacement);
//			float s = 0.02f * strength / n.length ();
//			dpdu.scaleAdd (s * d.getFloatValue (data, Channel.DPXDU), n, dpdu);
//			dpdv.scaleAdd (s * d.getFloatValue (data, Channel.DPXDV), n, dpdv);
//			data.setTuple3f (Channel.DPXDU, dpdu);
//			data.setTuple3f (Channel.DPXDV, dpdv);
//			n.cross (dpdu, dpdv);
//			data.setTuple3f (Channel.NX, n);
//			return data.getValidFloatValue (channel);
//		}
//		return data.forwardGetFloatValue (in);
	
	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof BumpMap);
		BumpMap map = (BumpMap) inp;

		
			String normal = generateResultWithChannelDefault(map.getInput(), cs,
				inpChan, Channel.NX).convert(Result.ET_VEC3);
			
			if(map.getDisplacement() == null)
				return new Result(normal, Result.ET_VEC3);

			String dpdu = generateResultWithChannelDefault(map.getInput(), cs,
					inpChan, Channel.DPXDU).convert(Result.ET_VEC3);
			String dpdv = generateResultWithChannelDefault(map.getInput(), cs,
					inpChan, Channel.DPXDV).convert(Result.ET_VEC3);
			
			String strength = generateResult(map.getDisplacement(), cs, inpChan, Channel.DPXDU).convert(Result.ET_VEC2);
			
//			String su = generateResultWithChannelDefault(map.getInput(), cs,
//					inpChan, Channel.DPXDU).convert(Result.ET_VEC3);
//			String sv = generateResultWithChannelDefault(map.getInput(), cs,
//					inpChan, Channel.DPXDV).convert(Result.ET_VEC3);
		
			String s = cs.registerNewTmpVar(Result.ET_VEC2, 0.02f*map.getStrength()+"*"+strength);
			
			dpdu = s+".x*("+normal+")+("+dpdu+")";
			dpdv = s+".y*("+normal+")+("+dpdv+")";
			normal = "cross("+dpdu+","+dpdv+")";
			
			return new Result(normal, Result.ET_VEC3);
	}

	@Override
	public Class<BumpMap> instanceFor() {
		return BumpMap.class;
	}

}
