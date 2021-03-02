package de.grogra.imp3d.glsl.material.channel;

import java.awt.image.BufferedImage;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.vecmath.Math2;

/**
 * Static class that offers simplex noise function and a turbulence function
 * based on noise. Most work done by Stefan Gustavson
 * http://webstaff.itn.liu.se/~stegu/simplexnoise/ "Simplex noise demystified"
 * 
 * @author Konni Hartmann
 */
public class SimplexNoise {

	final static String simplexSig = "void simplex( const in vec3 P, out vec3 offset1, out vec3 offset2 )";
	final static String simplex =
	/*
	 * Author: Stefan Gustavson ITN-LiTH (stegu@itn.liu.se) 2004-12-05 Simplex
	 * indexing functions by Bill Licea-Kane, ATI (bill@ati.com)
	 */
	"			vec3 offset0;\n" +

	"			vec2 isX = step( P.yz, P.xx );\n" + // P.x >= P.y ? 1.0 : 0.0; P.x >=
											// P.z ? 1.0 : 0.0;
			"			offset0.x  = dot( isX, vec2( 1.0 ) );\n" + // Accumulate all P.x
															// >= other channels
															// in offset.x
			"			offset0.yz = 1.0 - isX;\n" + // Accumulate all P.x < other
												// channels in offset.yz

			"			float isY = step( P.z, P.y );\n" + // P.y >= P.z ? 1.0 : 0.0;
			"			offset0.y += isY;\n" + // Accumulate P.y >= P.z in offset.y
			"			offset0.z += 1.0 - isY;\n" + // Accumulate P.y < P.z in offset.z

			// offset0 now contains the unique values 0,1,2 in each channel
			// 2 for the channel greater than other channels
			// 1 for the channel that is less than one but greater than another
			// 0 for the channel less than other channels
			// Equality ties are broken in favor of first x, then y
			// (z always loses ties)

			"			offset2 = clamp(   offset0, 0.0, 1.0 );\n" +
			// offset2 contains 1 in each channel that was 1 or 2
			"			offset1 = clamp( --offset0, 0.0, 1.0 );";
	// offset1 contains 1 in the single channel that was 1

	// 3D version
	final static String noise3dSig = "float inoise(vec3 P)";
	final static String noise3dbody =
	// Scaled to fit GroImps noise function better
	// "P *= .5;\n"+
	/*
	 * To create offsets of one texel and one half texel in the texture lookup,
	 * we need to know the texture image size.
	 */
	"		#define ONE 0.00390625\n"
			+ "		#define ONEHALF 0.001953125\n"
			+
			// The numbers above are 1/256 and 0.5/256, change accordingly

			/*
			 * 3D simplex noise. Comparable in speed to classic noise, better
			 * looking.
			 */
			// The skewing and unskewing factors for the 3D case
			"		#define F3 0.333333333333\n"
			+ "		#define G3 0.166666666667\n"
			+

			// Skew the (x,y,z) space to determine which cell of 6 simplices
			// we're in
			"		  float s = (P.x + P.y + P.z) * F3;\n"
			+ // Factor for 3D skewing
			"		  vec3 Pi = floor(P + s);\n"
			+ "		  float t = (Pi.x + Pi.y + Pi.z) * G3;\n"
			+ "		  vec3 P0 = Pi - t;\n"
			+ // Unskew the cell origin back to (x,y,z) space
			"		  Pi = Pi * ONE + ONEHALF;\n"
			+ // Integer part, scaled and offset for texture lookup

			"		  vec3 Pf0 = P - P0;\n"
			+ // The x,y distances from the cell origin

			// For the 3D case, the simplex shape is a slightly irregular
			// tetrahedron.
			// To find out which of the six possible tetrahedra we're in, we
			// need to
			// determine the magnitude ordering of x, y and z components of Pf0.
			"		  vec3 o1;\n"
			+ "		  vec3 o2;\n"
			+ "		  simplex(Pf0, o1, o2);\n"
			+

			// Noise contribution from simplex origin
			"		  float perm0 = perm(Pi.xy).a;\n"
			+ "		  vec3  grad0 = perm(vec2(perm0, Pi.z)).rgb * 4.0 - 1.0;\n"
			+ "		  float t0 = 0.6 - dot(Pf0, Pf0);\n"
			+ "		  float t20 = 0.0;\n"
			+ // added
			"		  float t40 = 0.0;\n"
			+ // added
			"		  float n0;\n"
			+ "		  if (t0 < 0.0) n0 = 0.0;\n"
			+ "		  else {\n"
			+ "		    t20 = t0 * t0;\n"
			+ "		    t40 = t20 * t20;\n"
			+ "		    n0 = t40 * dot(grad0, Pf0);\n"
			+ "		  }\n"
			+

			// Noise contribution from second corner
			"		  vec3 Pf1 = Pf0 - o1 + G3;\n"
			+ "		  float perm1 = perm(Pi.xy + o1.xy*ONE).a;\n"
			+ "		  vec3  grad1 = perm(vec2(perm1, Pi.z + o1.z*ONE)).rgb * 4.0 - 1.0;\n"
			+ "		  float t1 = 0.6 - dot(Pf1, Pf1);\n"
			+ "		  float t21 = 0.0;\n"
			+ // added
			"		  float t41 = 0.0;\n"
			+ // added
			"		  float n1;\n"
			+ "		  if (t1 < 0.0) n1 = 0.0;\n"
			+ "		  else {\n"
			+ "		    t21 = t1 * t1;\n"
			+ "		    t41 = t21 * t21;\n"
			+ "		    n1 = t41 * dot(grad1, Pf1);\n"
			+ "		  }\n"
			+

			// Noise contribution from third corner
			"		  vec3 Pf2 = Pf0 - o2 + 2.0 * G3;\n"
			+ "		  float perm2 = perm(Pi.xy + o2.xy*ONE).a;\n"
			+ "		  vec3  grad2 = perm(vec2(perm2, Pi.z + o2.z*ONE)).rgb * 4.0 - 1.0;\n"
			+ "		  float t2 = 0.6 - dot(Pf2, Pf2);\n"
			+ "		  float t22 = 0.0;\n"
			+ // added
			"		  float t42 = 0.0;\n"
			+ // added
			"		  float n2;\n"
			+ "		  if (t2 < 0.0) n2 = 0.0;\n"
			+ "		  else {\n"
			+ "		    t22 = t2 * t2;\n"
			+ "		    t42 = t22 * t22;\n"
			+ "		    n2 = t42 * dot(grad2, Pf2);\n"
			+ "		  }\n"
			+

			// Noise contribution from last corner
			"		  vec3 Pf3 = Pf0 - vec3(1.0-3.0*G3);\n"
			+ "		  float perm3 = perm(Pi.xy + vec2(ONE, ONE)).a;\n"
			+ "		  vec3  grad3 = perm(vec2(perm3, Pi.z + ONE)).rgb * 4.0 - 1.0;\n"
			+ "		  float t3 = 0.6 - dot(Pf3, Pf3);\n"
			+ "		  float t23 = 0.0;\n"
			+ // added
			"		  float t43 = 0.0;\n"
			+ // added
			"		  float n3;\n" + "		  if(t3 < 0.0) n3 = 0.0;\n" + "		  else {\n"
			+ "		    t23 = t3 * t3;\n" + "		    t43 = t23 * t23;\n"
			+ "		    n3 = t43 * dot(grad3, Pf3);\n" + "		  }\n";

	// Here we use a different return value than in the original glsl-code
	// thanks goes again to Stefan Gustavson
	// the return value and calculation of derivates are adopted from his
	// library at
	// http://mines.lumpylumpy.com/Electronics/Computers/Software/Cpp/Graphics/Bitmap/Textures/Noise/Simplex.php

	/*
	 * Simplex.cpp Adapted to C++ from original code sdnoise1234.c Copyright ©
	 * 2003-2008, Stefan Gustavson Contact: stefan.gustavson@gmail.com
	 * _____________________________________________________________ This
	 * library is free software; you can redistribute it and/or modify it under
	 * the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option)
	 * any later version.
	 * 
	 * This library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
	 * Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License along
	 * with this library; if not, write to the Free Software Foundation, Inc.,
	 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
	 * _________________________________________________________________________
	 * 
	 * This code was used to find the range-correction values (the return value
	 * equations): double mn= DBL_MAX; double mx=-DBL_MAX; for(WORD X=100; X--;)
	 * { // double d=Simplex::Noise1(Twister::GetDouble()*X); for(WORD Y=100;
	 * Y--;) { // double
	 * d=Simplex::Noise2(Twister::GetDouble()*X,Twister::GetDouble()*Y);
	 * for(WORD Z=100; Z--;) { // double
	 * d=Simplex::Noise3(Twister::GetDouble()*X
	 * ,Twister::GetDouble()*Y,Twister::GetDouble()*Z); for(WORD W=100; W--;) {
	 * double
	 * d=Simplex::Noise4(Twister::GetDouble()*X,Twister::GetDouble()*Y,Twister
	 * ::GetDouble()*Z,Twister::GetDouble()*W); if(mn>d) mn=d; if(mx<d) mx=d; }
	 * } } } TRACE("Noise*%g-%g\r\n[%g, %g]\r\n" ,2/(mx-mn),mn*2/(mx-mn)+1,
	 * mn*2/(mx-mn)-(mn*2/(mx-mn)+1), mx*2/(mx-mn)-(mn*2/(mx-mn)+1));
	 */

	// Sum up and scale the result to cover the range [-1,1]
	final static String noise3d = noise3dbody
			+ "return 0.00104006 + 32.741 * (n0 + n1 + n2 + n3);";
	// "		  return 32.0 * (n0 + n1 + n2 + n3);";

	final static String dnoise3dSig = "vec3 dinoise(vec3 P)";
	final static String dnoise3d = noise3dbody
			+ "vec3 D = -8. * t20 * t0 * Pf0 * dot(grad0, Pf0) + t40 * grad0;\n"
			+ "D += -8. * t21 * t1 * Pf1 * dot(grad1, Pf1) + t41 * grad1;\n"
			+ "D += -8. * t22 * t2 * Pf2 * dot(grad2, Pf2) + t42 * grad2;\n"
			+ "D += -8. * t23 * t3 * Pf3 * dot(grad3, Pf3) + t43 * grad3;\n" +
			// "return D * 16.;\n";
			"return D * 16.9446;\n";
	// "return D;\n";

	final static String turb3dSig = "float turbulence(vec3 f, int octaves, float lambda, float omega)";
	final static String turb3d = "float v = inoise (f);"
			+ "float l = lambda, o = omega;"
			+ "for (int i = octaves-1; i > 0; --i)" + "{"
			+ "v += o * inoise (f * l);" + "if (i > 1)" + "{" + "l *= lambda;"
			+ "o *= omega;" + "}" + "}" + "return v;";

	final static String dturb3dSig = "vec3 dturbulence(vec3 f, int octaves, float lambda, float omega)";
	final static String dturb3d = "vec3 v = dinoise (f);"
			+ "float l = lambda, o = omega;"
			+ "for (int i = octaves-1; i > 0; --i)" + "{"
			+ "v += o * dinoise (f * l);" + "if (i > 1)" + "{" + "l *= lambda;"
			+ "o *= omega;" + "}" + "}" + "return v;";

	static BufferedImage permImage, gradImage;
	static String permSampler;
	// static String gradSampler;
	static boolean init = false;

	static int PermutationTexture = 0;
	// static int GradientTexture = 0;

	/*
	 * These are Ken Perlin's proposed gradients for 3D noise. I kept them for
	 * better consistency with the reference implementation, but there is really
	 * no need to pad this to 16 gradients for this particular implementation.
	 * If only the "proper" first 12 gradients are used, they can be extracted
	 * from the grad4[][] array: grad3[i][j] == grad4[i*2][j], 0<=i<=11, j=0,1,2
	 */
	static final int grad3[][] = { { 0, 1, 1 }, { 0, 1, -1 }, { 0, -1, 1 },
			{ 0, -1, -1 }, { 1, 0, 1 }, { 1, 0, -1 }, { -1, 0, 1 },
			{ -1, 0, -1 }, { 1, 1, 0 }, { 1, -1, 0 }, { -1, 1, 0 },
			{ -1, -1, 0 }, // 12 cube edges
			{ 1, 0, -1 }, { -1, 0, -1 }, { 0, -1, 1 }, { 0, 1, 1 } }; // 4 more
																		// to
																		// make
																		// 16

	public static BufferedImage generatePermutationTexture() {
		BufferedImage result = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_ARGB);
		int pixel[] = new int[4];
		for (char i = 0; i < 256; i++)
			for (char j = 0; j < 256; j++) {
				char value = Math2
						.random((char) ((j + Math2.random(i)) & 0xFF));
				pixel[0] = grad3[value & 0x0F][0] * 64 + 64; // Gradient x
				pixel[1] = grad3[value & 0x0F][1] * 64 + 64; // Gradient y
				pixel[2] = grad3[value & 0x0F][2] * 64 + 64; // Gradient z
				pixel[3] = value; // Permuted index
				result.setRGB(j, i, (pixel[3] << 24) | (pixel[0] << 16)
						| (pixel[1] << 8) | (pixel[2]));
			}
		return result;
	}

	static void regLookUpTexture(ShaderConfiguration p) {
		if (!init) {
			permImage = generatePermutationTexture();
			// gradImage = generateGradientTexture();
			init = true;
		}
		permSampler = p.registerTexture(permImage);
		// gradSampler = p.registerTexture(gradImage);
	}

	public static String registerTurbWithUnroll(ShaderConfiguration phong,
			int octaves) {
		String sig = "float turbulence_" + octaves
				+ "(vec3 f, float lambda, float omega)";
		String func = "float v = inoise (f);"
				+ "float l = lambda, o = omega;";
		for (int i = octaves - 1; i > 1; --i) {
			func += "v += o * inoise (f * l);" + "l *= lambda;" + "o *= omega;";
		}
		if (octaves - 1 > 1)
			func += "v += o * inoise (f * l);";
		func += "return v;";
		phong.registerFunc(sig, func);
		return "turbulence_" + octaves;
	}

	public static String registerDTurbWithUnroll(ShaderConfiguration phong,
			int octaves) {
		String sig = "vec3 dturbulence_" + octaves
				+ "(vec3 f, float lambda, float omega)";
		String func = "vec3 v = dinoise (f);"
				+ "float l = lambda, o = omega;";
		for (int i = octaves - 1; i > 1; --i) {
			func += "v += o * dinoise (f * l);" + "l *= lambda;" + "o *= omega;";
		}
		if (octaves - 1 > 1)
			func += "v += o * dinoise (f * l);"; 
		func += "return v;";
		phong.registerFunc(sig, func);
		return "dturbulence_" + octaves;
	}

	public static void registerNoiseFunctions(ShaderConfiguration phong) {
		SimplexNoise.regLookUpTexture(phong);

		phong.registerFunc("vec4 perm(vec2 x)", "return texture2D("
				+ permSampler + ", x);");

		phong.registerFunc(simplexSig, simplex);
		phong.registerFunc(noise3dSig, noise3d);
		phong.registerFunc(turb3dSig, turb3d);
	}

	public static void registerdNoiseFunctions(ShaderConfiguration phong) {
		SimplexNoise.regLookUpTexture(phong);

		phong.registerFunc("vec4 perm(vec2 x)", "return texture2D("
				+ permSampler + ", x);");

		phong.registerFunc(simplexSig, simplex);
		phong.registerFunc(dnoise3dSig, dnoise3d);
		phong.registerFunc(dturb3dSig, dturb3d);
	}
}
