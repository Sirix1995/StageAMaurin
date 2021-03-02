package de.grogra.imp3d.glsl.material;

import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.imp3d.shading.Phong;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation of Shader: Phong. Will recompile on every property change!
 * 
 * @author Konni Hartmann
 */
public class GLSLPhong extends GLSLMaterial{

	final static String MAX_SHININESS = "65504.0";

	final static String convertShininessSig = "float convertShininess(in float x)";
	final static String convertShininessSource = " x = x * (2.0 - x);\n"
			+ " if(x <= 0.0) x = 0.0;\n" + " else if(x >= 1.0) x = "
			+ MAX_SHININESS + ";\n" + " else x = min(-2.0 / log(x), "
			+ MAX_SHININESS + ");\n" + " return x;";

	@Override
	protected Result[] getAllChannels(Object sha) {
		assert (sha instanceof Phong);
		Phong sh = (Phong) sha;
		Result[] input = { null,
				null,
				new Result("4.0", Result.ET_FLOAT),
				new Result("vec3(0.75)", Result.ET_VEC3),
				new Result("0.5", Result.ET_FLOAT),
				new Result("1.0", Result.ET_FLOAT),
				new Result("vec3(0.0)", Result.ET_VEC3),
				new Result("vec3(0.0)", Result.ET_VEC3),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("", Result.ET_UNKNOWN), };
		
		ChannelMap chanDiffuse = sh.getDiffuse() != null ? sh.getDiffuse()
				: Phong.DEFAULT_DIFFUSE; 
		ChannelMap chanSpecular = sh.getSpecular() != null ? sh.getSpecular()
				: Phong.DEFAULT_SPECULAR;
		ChannelMap chanEmissive = sh.getEmissive() != null ? sh.getEmissive()
				: Phong.DEFAULT_EMISSIVE;
		ChannelMap chanShininess = sh.getShininess();
		ChannelMap chanTransparency = sh.getTransparency() != null ? sh
				.getTransparency() : Phong.DEFAULT_TRANSPARENCY; 
		
		GLSLChannelMap df = GLSLChannelMap
				.getGLSLObject(chanDiffuse);
		GLSLChannelMap sf = GLSLChannelMap
				.getGLSLObject(chanSpecular);
		GLSLChannelMap em = GLSLChannelMap
				.getGLSLObject(chanEmissive);
		GLSLChannelMap trans = GLSLChannelMap
				.getGLSLObject(chanTransparency);

		GLSLChannelMap defInp = getMaterialConfig().getDefaultInputChannel();
		
		GLSLChannelMap inGLSLChan = GLSLChannelMap
				.getGLSLObject(sh.getInput());
		if(inGLSLChan == null)
			inGLSLChan = defInp;
		
		input[MaterialConfiguration.IT_POSITION] = defInp.generate(null, getMaterialConfig(), null, Channel.X);
		input[MaterialConfiguration.IT_NORMAL] = inGLSLChan.generate(sh.getInput(), getMaterialConfig(), defInp, Channel.NX);
		
		Result diff = df != null ? df.generate(chanDiffuse, getMaterialConfig(), defInp, Channel.R) : null;
		Result spec = sf != null ? sf.generate(chanSpecular, getMaterialConfig(), defInp, Channel.R) : null;
		Result emis = em != null ? em.generate(chanEmissive, getMaterialConfig(), defInp, Channel.R) : null;

		Result shine;
		if (chanShininess != null) {
			// XXX: Shininess takes quite a few calculations.. try
			// optimizing!
			GLSLChannelMap ssf = GLSLChannelMap
			.getGLSLObject(chanShininess);

			config.registerFunc(convertShininessSig, convertShininessSource);
			shine = new Result("convertShininess("
					+ (ssf.generate(chanShininess, getMaterialConfig(), defInp, Channel.R))
							.convert(Result.ET_FLOAT) + ")", Result.ET_FLOAT);
		} else
			shine = new Result("" + Phong.DEFAULT_SHININESS, Result.ET_FLOAT);

		Result transp = trans != null ? trans.generate(chanTransparency,
				getMaterialConfig(), defInp, Channel.R) : null;

		if (diff != null) {
			if (diff.getReturnType() == Result.ET_VEC4) {
				String diffuse = config.registerNewTmpVar(diff.getReturnType(),
						diff.toString());
				input[MaterialConfiguration.IT_PROLOGUE] = new Result("if("
						+ diffuse + ".a < 0.5) discard;", Result.ET_UNKNOWN);
				input[MaterialConfiguration.IT_DIFFUSE] = new Result(diffuse,
						diff.getReturnType());
			} else
				input[MaterialConfiguration.IT_DIFFUSE] = diff;
		}
		if (spec != null)
			input[MaterialConfiguration.IT_SPECULAR] = spec;
		if (shine != null)
			input[MaterialConfiguration.IT_SHININESS] = shine;
		if (emis != null)
			input[MaterialConfiguration.IT_EMISSIVE] = emis;
		if (transp != null)
			input[MaterialConfiguration.IT_TRANSPERENCY] = transp;

		// Remember Stamp to check lateron
		oldStamp = sh.getStamp();
		return input;
	}

	@Override
	public Class instanceFor() {
		return Phong.class;
	}

	int oldStamp = -1;

	@Override
	public boolean needsRecompilation(Object s) {
		if (s instanceof Phong) {
			int newStamp = ((Phong) s).getStamp();
			if (newStamp == oldStamp)
				return false;
		}
		return true;
	}

	@Override
	public GLSLManagedShader getInstance() {
		return new GLSLPhong();
	}

	@Override
	public boolean mayDiscard(Object s) {
		if (s instanceof Phong) {
			Phong sh = (Phong) s;

			GLSLChannelMap df = GLSLChannelMap
					.getGLSLObject(sh.getDiffuse() != null ? sh.getDiffuse()
							: Phong.DEFAULT_DIFFUSE);

			if (df != null)
				return df.mayDiscard();
		}
		return false;
	}
	
	@Override
	public boolean isOpaque(Object s) {
		if (s instanceof Phong) {
			Phong sh = (Phong) s;
			return (sh.getTransparency() == null); 
		}
		return true;
	}
}
