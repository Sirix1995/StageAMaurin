package de.grogra.imp3d.glsl.material;

import javax.vecmath.Vector3d;

import org.sunflow.math.MathUtils;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.math.Channel;
import de.grogra.vecmath.Matrix34d;

public class GLSLSunSky extends GLSLMaterial {
	
	@Override
	public Class instanceFor() {
		return SunSkyLight.class;
	}

	@Override
	protected Result[] getAllChannels(Object sha) {
		// TODO Auto-generated method stub
		assert(sha instanceof SunSkyLight);
		SunSkyLight ssl = (SunSkyLight) sha;
		
		config.setVersion(110);
		
//		#version 120
//
//		const float PI = ;
//		// turbidity
//		uniform float T;
		double T = ssl.getTurbidity();
		GLSLDisplay.printDebugInfoN(" - SunSky: Turbidity: "+ssl.getTurbidity());
		
//		// solar angle from zenith
//		uniform float theta_s;
		Vector3d sun = new Vector3d (ssl.getSun());
		sun.normalize ();
		GLSLDisplay.printDebugInfoN(" - SunSky: SunDir: "+sun);//		Math2.getOrthogonalBasis (sunDir, sunBasis, true);
		double cosSunTheta = MathUtils.clamp (sun.z, -1, 1);
		double theta_s = Math.acos (cosSunTheta);

//		double sinSunPhi = MathUtils.clamp (sun.x / cosSunTheta, -1, 1);
//		double phi_s = Math.asin (sinSunPhi);
		
//		// direction vector to sun in local coordinates
//		//uniform vec3 sun;
//		const vec3 sun = vec3(
//			cos(phi_s) * sin(theta_s),
//			sin(phi_s) * sin(theta_s),
//			cos(theta_s)
//		);
//		String sun = cs.registerGlobalConst(Result.ET_VEC3, "vec3(" +
//				"cos("+phi_s+")*sin("+theta_s+")," +
//				"sin("+phi_s+")*sin("+theta_s+")," +
//				"cos("+theta_s+")" +
//			")");

//		// relative optical mass
//		const float m = 1.0 / (
//			cos(theta_s) + 
//			0.15 * pow(93.885 - theta_s * 180.0 / PI, -1.253)
//		);
//		double m = 1.0 / (Math.cos(theta_s) + 0.15 * Math.pow(93.885 - theta_s * 180.0 / Math.PI, -1.253));
		
//		const float chi = (4.0/9.0 - T/120.0) * (PI - 2*theta_s);
		double chi = (4.0/9.0 - T / 120.0) * (Math.PI - 2.0 * theta_s);
		
//		// zenith luminance coefficients
//		const float AY =  0.1787 * T - 1.4630;
		double AY = 0.1787 * T - 1.4630;
//		const float BY = -0.3554 * T + 0.4275;
		double BY = -0.3554 * T + 0.4275;
//		const float CY = -0.0227 * T + 5.3251;
		double CY = -0.0227 * T + 5.3251;
//		const float DY =  0.1206 * T - 2.5771;
		double DY = 0.1206 * T - 2.5771;
//		const float EY = -0.0670 * T + 0.3703;
		double EY = -0.0670 * T + 0.3703;
//
//		// zenith luminance
//		const float Y_Z = (4.0453 * T - 4.9710) * tan(chi) - 0.2155 * T + 2.4192;
		double Y_Z = (4.0453 * T - 4.9710) * Math.tan(chi) - 0.2155 * T + 2.4192;
		
//		// zenith chrominance coefficients x
//		const float Ax = -0.0193 * T - 0.2592;
		double Ax = -0.0193 * T - 0.2592;
//		const float Bx = -0.0665 * T + 0.0008;
		double Bx = -0.0665 * T + 0.0008;
//		const float Cx = -0.0004 * T + 0.2125;
		double Cx = -0.0004 * T + 0.2125;
//		const float Dx = -0.0641 * T - 0.8989;
		double Dx = -0.0641 * T - 0.8989;
//		const float Ex = -0.0033 * T + 0.0452;
		double Ex = -0.0033 * T + 0.0452;

		//		// zenith crominance x
//		const float x_Z = dot(vec3(T*T, T, 1), mat4x3(
//		0.0017, -0.0290,  0.1169,
//		-0.0037,  0.0638, -0.2120,
//		0.0021, -0.0320,  0.0605,
//		0.0000,  0.0039,  0.2589
//		) * vec4(theta_s * theta_s * theta_s, theta_s * theta_s, theta_s, 1));
//		String x_Z = config.registerGlobalConst(Result.ET_FLOAT, 
//				"dot(vec3("+T+"*"+T+", "+T+", 1.0), mat4x3(" +
//						" 0.0017, -0.0290,  0.1169, " +
//						"-0.0037,  0.0638, -0.2120, " +
//						" 0.0021, -0.0320,  0.0605, " +
//						" 0.0000,  0.0039,  0.2589" +
//						") * vec4("+theta_s+" * "+theta_s+" * "+theta_s+", "+theta_s+" * "+theta_s+", "+theta_s+", 1.0)"+
//						")");
		double x_Z = (new Vector3d(T*T,T,1)).dot(new Vector3d(
				 0.0017*theta_s*theta_s*theta_s - 0.0037*theta_s*theta_s + 0.0021*theta_s,
				-0.0290*theta_s*theta_s*theta_s + 0.0638*theta_s*theta_s - 0.0320*theta_s + 0.0039,
				 0.1169*theta_s*theta_s*theta_s - 0.2120*theta_s*theta_s + 0.0605*theta_s + 0.2589		
		));
//		// zenith chrominance coefficients y
//		const float Ay = -0.0167 * T - 0.2608;
		double Ay = -0.0167 * T - 0.2608;
//		const float By = -0.0950 * T + 0.0092;
		double By = -0.0950 * T + 0.0092;
//		const float Cy = -0.0079 * T + 0.2102;
		double Cy = -0.0079 * T + 0.2102;
//		const float Dy = -0.0441 * T - 1.6537;
		double Dy = -0.0441 * T - 1.6537;
//		const float Ey = -0.0109 * T + 0.0529;
		double Ey = -0.0109 * T + 0.0529;
//		// zenith chrominance y
//		const float y_Z = dot(vec3(T*T, T, 1), mat4x3(
//		0.0028, -0.0421,  0.1535,
//		-0.0061,  0.0897, -0.2676,
//		0.0032, -0.0415,  0.0667,
//		0.0000,  0.0052,  0.2669
//		) * vec4(theta_s * theta_s * theta_s, theta_s * theta_s, theta_s, 1));
		double y_Z = (new Vector3d(T*T,T,1)).dot(new Vector3d(
				 0.0028*theta_s*theta_s*theta_s - 0.0061*theta_s*theta_s + 0.0032*theta_s,
				-0.0421*theta_s*theta_s*theta_s + 0.0897*theta_s*theta_s - 0.0415*theta_s + 0.0052,
				 0.1535*theta_s*theta_s*theta_s - 0.2676*theta_s*theta_s + 0.0667*theta_s + 0.2669		
		));		

//		protected static final String shaderFuncCapY =
		config.registerFunc("float F_Y(float theta, float gamma)", 
				"	return (1.0 + "+AY+" * exp("+BY+" / cos(theta))) *"+
						" (1.0 + "+CY+" * exp("+DY+" * gamma) + "+EY+" * cos(gamma) * cos(gamma));");
	
//			protected static final String shaderFuncx =
		config.registerFunc("float F_x(float theta, float gamma)", 
				"	return (1.0 + "+Ax+" * exp("+Bx+" / cos(theta))) *"+
						" (1.0 + "+Cx+" * exp("+Dx+" * gamma) + "+Ex+" * cos(gamma) * cos(gamma));");

//			protected static final String shaderFuncy =
		config.registerFunc("float F_y(float theta, float gamma)", 
				"	return (1.0 + "+Ay+" * exp("+By+" / cos(theta))) *"+
						" (1.0 + "+Cy+" * exp("+Dy+" * gamma) + "+Ey+" * cos(gamma) * cos(gamma));");

		
		String shaderFunc = 
			//sun = normalize(sun);
			//theta_s = atan(sun.z, length(sun.xy));

			// transform texture coordinates to longitude/lattitude
			// u is angle between viewing direction and zenith
			// Already calculated inside shader
//		"	vec2 uv = vec2(0.5 * "+PI+" * (1.0 - gl_TexCoord[0].y)," +
//				"2.0 * "+PI+" * gl_TexCoord[0].x);\n"+

//		"	uv.s += "+theta_s+";"+
//		"	uv.t += "+phi_s+";"+

			//gl_FragColor = gl_Color;
			//gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
			//gl_FragColor = vec4(gl_Normal, 1.0);
			
			// calculate viewing vector from texture coordinates
			// z points upwards
//		"	vec3 view = vec3(\n"+
//		"		cos(uv.t) * sin(uv.s),\n"+
//		"		sin(uv.s) * sin(uv.s),\n"+
//		"		cos(uv.s)\n"+
//		"	);\n"+
		"	if(view.z < 0.0) " +
		"		return vec4(0.0, 0.0, 0.0, 1.0);\n"+
		
//		"	float theta = uv.t;\n"+
		"	float theta = acos(view.z);\n"+
		"	float gamma = acos(dot(vec3"+sun+", view));\n"+
			
		"	float Y = "+Y_Z+" * F_Y(theta, gamma) / F_Y(0.0, "+theta_s+");\n"+
		"	float x = "+x_Z+" * F_x(theta, gamma) / F_x(0.0, "+theta_s+");\n"+
		"	float y = "+y_Z+" * F_y(theta, gamma) / F_y(0.0, "+theta_s+");\n"+
			
		"	Y /= 10.0;\n"+
//		"	Y /= y * 683.002;\n"+
		
//			Y = clamp(Y, 0.0, 1.0);
//			x = clamp(x, 0.0, 1.0);
//			y = clamp(y, 0.0, 1.0);
			
		"	float M1 = (-1.3515 - 1.7703*x + 5.9114*y) /\n"+
		"		(0.0241 + 0.2562*x - 0.7341*y);\n"+
		"	float M2 = (0.0300 - 31.4424*x + 30.0717*y) /\n"+
		"		(0.0241 + 0.2562*x - 0.7341*y);\n"+

		"	vec3 c = mat3(\n"+
		"		10246.121, 10676.695, 12372.502,\n"+ 
		"		187.75537, 192.59651, 3482.8762,\n"+ 
		"		213.14803, 76.29493, -235.71611\n"+
//		"		10246.121, 187.75537, 213.14803,\n"+
//		"		10676.695, 192.59651, 76.29493,\n"+
//		"		12372.502, 3482.8762, -235.71611\n"+
		"	) * vec3(1, M1, M2);\n"+

		"	float X = c.x * Y / c.y;\n"+
		"	float Z = c.z * Y / c.y;\n"+
		
		/*
			float X = x * (Y / y);
			float Z = (1 - x - y) / (Y / y);
		*/	
		/*
			X = clamp(X, 0.0, 1.0);
			Z = clamp(Z, 0.0, 1.0);
			
			if (y <= 0.0)
				X = Y = Z = 0.0;
			
			vec3 XYZ = vec3(X, Y, Z);
			vec3 RGB = mat3(
				3.2406, -0.9689, 0.0557,
				-1.5372, 1.8758, -0.2040,
				-0.4986, 0.0415, 1.0570
			) * XYZ;
		*/
		"	vec3 XYZ = vec3(X, Y, Z);\n"+
		"	vec3 RGB = mat3(\n"+
		"		3.2410, -0.9692, 0.0556,\n"+
		"		-1.5374, 1.8760, -0.2040,\n"+
		"		-0.4986, 0.0416, 1.0571\n"+
		"	) * XYZ;\n" +
//		"	vec3 RGB = mat3(\n"+
//		"		3.2410, -1.5374, -0.4986,\n"+
//		"		-0.9692, 1.8759,  0.0416,\n"+
//		"		0.0556, -0.2040,  1.0571\n"+
//		"	) * XYZ;\n" +
			 
//		"	RGB *= vec3("+mo.irradiance.x+","+mo.irradiance.y+","+mo.irradiance.z +") *"+ ssl.getRadianceFactor()+";\n"+
		// XXX: Magic number to sync this with GroIMPs Implementation
		"	RGB *= "+Math.PI*ssl.getRadianceFactor()+";\n"+
//		"	RGB *= "+10. / (float) mo.irradiance.integrate () + ";\n"+
		"	float m = -min(RGB.r, min(RGB.g, RGB.b));"+
		"	if(m > 0.0) RGB += m;"+
		
//		"	RGB = clamp(RGB, vec3(0.0), vec3(1.0));\n"+
			//RGB = RGB * 1.0 / (1.0 + Y);
		
//			gl_FragColor = m * vec4(gl_TexCoord[0].xy, 0.0, 1.0);
		"	return vec4(RGB, 1.0);";
		/*
		 * 
			gl_FragColor = vec4( vec3(
				cos(u) * sin(v),
				sin(u) * sin(v),
				cos(v)
			), 1.0);
		*/
		config.registerFunc("vec4 sunsky(vec3 view, vec2 uv)", shaderFunc);
		
		GLSLChannelMap defInp = getMaterialConfig().getDefaultInputChannel();

		Result view = defInp.generate(ssl.getInput(), getMaterialConfig(), null, Channel.X); 
		Result uv = defInp.generate(ssl.getInput(), getMaterialConfig(), null, Channel.U); 
		
		Result[] input = { new Result("normal", Result.ET_VEC3),
				new Result("pos", Result.ET_VEC3),
				new Result("4.0", Result.ET_FLOAT),
				new Result("sunsky("+view+", "+uv+").rgb", Result.ET_VEC3),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("vec3(0.0)", Result.ET_VEC3),
				new Result("vec3(0.0)", Result.ET_VEC3),
				new Result("0.0", Result.ET_FLOAT),
				new Result("0.0", Result.ET_FLOAT),
				new Result("", Result.ET_UNKNOWN), };
		
		oldStamp = ssl.getStamp();
		return input;
	}

	@Override
	public GLSLShader getInstance() {
		return new GLSLSunSky();
	}

	int oldStamp = -1;

	@Override
	public boolean needsRecompilation(Object s) {
		if (s instanceof SunSkyLight) {
			int newStamp = ((SunSkyLight) s).getStamp();
			if (newStamp == oldStamp)
				return false;
		}
		return true;
	}
}
