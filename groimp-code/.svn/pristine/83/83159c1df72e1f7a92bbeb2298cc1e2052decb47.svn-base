package de.grogra.imp3d.glsl.material.channel;

import java.awt.image.BufferedImage;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.vecmath.Math2;

/**
 * Static class that offers a basic noise function (improved perlin noise) and a
 * turbulence function based on noise.
 * 
 * @author Konni Hartmann
 */
public class OldNoise {

	final static String fadeSig = "vec3 fade(vec3 t)";
	final static String fade = "return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);";
	// return t * t * (3 - 2 * t); // old curve

	// 3D version
	final static String noise3dSig = "float inoise(vec3 p)";
	final static String noise3d = "vec3 intp = floor(p);"
			+ "vec3 P = mod(intp, 256.0);"
			+ "p -= intp;"
			+

			// vec3 f = p;

			"vec3 f = fade(p);"
			+

			// HASH COORDINATES FOR 6 OF THE 8 CUBE CORNERS
			"float A  = perm(P.x) + P.y;"
			+ "float AA = perm(A) + P.z;"
			+ "float AB = perm(A + 1.0) + P.z;"
			+ "float B  = perm(P.x + 1.0) + P.y;"
			+ "float BA = perm(B) + P.z;"
			+ "float BB = perm(B + 1.0) + P.z;"
			+

			// AND ADD BLENDED RESULTS FROM 8 CORNERS OF CUBE
			"return mix(" + "mix(mix(grad(perm(AA), p),"
			+ "grad(perm(BA), p + vec3(-1.0,  0.0, 0.0)), f.x),"
			+ "mix(grad(perm(AB), p + vec3( 0.0, -1.0, 0.0)),"
			+ "grad(perm(BB), p + vec3(-1.0, -1.0, 0.0)), f.x)," + "f.y),"
			+ "mix(mix(grad(perm(AA + 1.0), p + vec3( 0.0,  0.0, -1.0)),"
			+ "grad(perm(BA + 1.0), p + vec3(-1.0,  0.0, -1.0)), f.x),"
			+ "mix(grad(perm(AB + 1.0), p + vec3( 0.0, -1.0, -1.0)),"
			+ "grad(perm(BB + 1.0), p + vec3(-1.0, -1.0, -1.0)), f.x),"
			+ "f.y)," + "f.z);";

	final static String turb3dSig = "float turbulence(vec3 f, int octaves, float lambda, float omega)";
	final static String turb3d = "float v = inoise (f);"
			+ "float l = lambda, o = omega;"
			+ "for (int i = octaves-1; i > 0; --i)" + "{"
			+ "v += o * inoise (f * l);" + "if (i > 1)" + "{" + "l *= lambda;"
			+ "o *= omega;" + "}" + "}" + "return v;";

	// XXX: Expand, to support all

	static BufferedImage permImage, gradImage;
	static String permSampler, gradSampler;
	static boolean init = false;

	static int PermutationTexture = 0;
	static int GradientTexture = 0;

	public static BufferedImage generatePermutationTexture() {
		BufferedImage result = new BufferedImage(256, 1,
				BufferedImage.TYPE_INT_ARGB);
		for (char i = 0; i < 256; i++)
			result.setRGB(i, 0, ((0xFF000000) | (Math2.random(i) << 16)));
		return result;
	}

	public static BufferedImage generateGradientTexture() {
		int off[] = { 0, 127, 255 };
		int permArr[] = { 1, 1, 0, -1, 1, 0, 1, -1, 0, -1, -1, 0, 1, 0, 1, -1,
				0, 1, 1, 0, -1, -1, 0, -1, 0, 1, 1, 0, -1, 1, 0, 1, -1, 0, -1,
				-1, 1, 1, 0, 0, -1, 1, -1, 1, 0, 0, -1, -1, };
		BufferedImage result = new BufferedImage(16, 1,
				BufferedImage.TYPE_INT_ARGB);
		for (char i = 0; i < 16; i++)
			result
					.setRGB(
							i,
							0,
							((0xFF000000) | ((off[permArr[3 * i] + 1]) << 16)
									| ((off[permArr[3 * i + 1] + 1]) << 8) | (off[permArr[3 * i + 2] + 1])));
		return result;
	}

	static void regLookUpTexture(ShaderConfiguration p) {
		if (!init) {
			permImage = generatePermutationTexture();
			gradImage = generateGradientTexture();
			init = true;
		}
		permSampler = p.registerTexture(permImage);
		gradSampler = p.registerTexture(gradImage);
	}

	public static void registerNoiseFunctions(ShaderConfiguration phong) {
		OldNoise.regLookUpTexture(phong);

		phong.registerFunc("float perm(float x)", "return texture2D("
				+ permSampler
				+ ", vec2(x / 256.0 + 1.0/256.0, 0.5)).r * 256.0;");
		phong
				.registerFunc(
						"float grad(float x, vec3 p)",
						"return dot((texture2D("
								+ gradSampler
								+ ", vec2(x / 256.0 + 1.0/256.0, 0.5)).rgb * 2.0 - 1.0), p);");

		phong.registerFunc(fadeSig, fade);
		phong.registerFunc(noise3dSig, noise3d);
		phong.registerFunc(turb3dSig, turb3d);

	}
}
