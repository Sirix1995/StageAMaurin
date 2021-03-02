
// http://www.gpgpu.org/forums/viewtopic.php?p=19531&sid=599148a60967e216f23ab95db23b53bc
// http://random.mat.sbg.ac.at/~charly/server/node3.html
// http://researchweb.watson.ibm.com/journal/rd/461/agarwal.html

vec4 seed;
vec4 n;

vec4 seed0;
vec4 seed1;
vec4 seed2;
vec4 seed3;
vec4 seed4;
vec4 seed5;
vec4 seed6;
vec4 seed7;

#if 0
float rand()
{
/*
	seed0.x = mod(seed1.x + seed1.w, 1);
	seed1 = vec4(seed0.w, seed1.xyz);
	seed0 = seed0.xxyz;
	random = seed0.x;
	return seed0.x;
*/
	random = mod(seed1.z + seed7.w, 1.0);
	seed7 = vec4(seed6.w, seed7.xyz);
	seed6 = vec4(seed5.w, seed6.xyz);
	seed5 = vec4(seed4.w, seed5.xyz);
	seed4 = vec4(seed3.w, seed4.xyz);
	seed3 = vec4(seed2.w, seed3.xyz);
	seed2 = vec4(seed1.w, seed2.xyz);
	seed1 = vec4(seed0.w, seed1.xyz);
	seed0 = vec4(random, seed0.xyz);
	return random;
}
#endif

/*
// Park & Miller RNG: x(n+1) = x(x) * g mod n, n=2^16+1, g=75
float rand()
{
	random = mod(random * 75.0, 65537.0);
	return random / 65537.0;
//	random = mod(random * 75.0 / 65537.0, 1);
//	return random;
}
*/

/*
float LCG(float y1)
{

	float a = 16807.0;
	float b = 0.0;
	float m = pow(2.0,32.0) - 1.0;// 2147483647.0; //pow(2.0, 31.0) - 1.0;
	random = mod(a*y1 + b, m);
	
	return random / m;
}
*/

/*
const float a = 3513381.0;
const float b = 0.0;
const float m = 8388608.0;
float LCG(float y1)
{
	random = mod(a * random + b, m);
	return random / m;
}


// modified version with prescaled random by 1/m
const float a = 3513381.0;
float LCG(float y1)
{
	random = mod(3513381.0 * random, 1);
	return random;
}
*/

/*
// http://www.gpgpu.org/forums/viewtopic.php?p=19531&sid=599148a60967e216f23ab95db23b53bc
const vec4 m = vec4(4194287.0, 4194277.0, 4194191.0, 4194167.0);
const vec4 a = vec4(3423.0, 2646.0, 1707.0, 1999.0);
const vec4 q = vec4(1225.0, 1585.0, 2457.0, 2098.0);
const vec4 r = vec4(1112.0, 367.0, 92.0, 265.0);
//vec4 n;
float LCG(float y1)
{
	vec4 beta = floor(n / q);
	vec4 n = a * (n - beta*q) - beta*r;
	n += step(n, vec4(0.0)) * m;
	return fract(dot(n, vec4(1.0, -1.0, 1.0, -1.0) / m));
}
*/


/*
const vec4 K = vec4(-7.0, 11.0, 31.0, -3.0);
float LCG(float y1)
{
	seed = vec4(fract(dot(seed, K)), seed.xyz);
	return seed.x;
}
*/
	

/*
float LCG(float y1)
{
//	seed.wxyz = vec4(seed.xyz, fract(dot(vec4(seed.xyw, random), seed.ywzx)));
	seed = vec4(fract(dot(vec4(cos(seed.xyw), random), seed.xyzw)), seed.xyz);
	random++;
	return seed.x;
}
*/
	

/*
float LCG(float y1)
{
	return rand();
}
*/
	




bool testShadow(TraceData td, vec4 lightOrigin, float angle, int shadowless, int isAlsoLight)
{	
	bool result = false;
	
	
	if(angle > 0.0 && shadowless != 1)
	{
		Ray shadowFeeler = Ray(td.iPoint, lightOrigin.xyz - td.iPoint.xyz, isAlsoLight, vec3(1.0) );
		
		
		//shadowTraceData
		TraceData std = intersection( shadowFeeler );
		
		
		if(std.hasIntersection)
		{	
			vec4 iPoint = getMatrix(std.id) * std.iPoint;
			
			result = distance(td.iPoint, lightOrigin) > distance(td.iPoint, iPoint);
		} //if	
	} //if
	

	return result;
} //testShadow


float calculateAngle(vec3 a, vec3 b)
{	
	return dot( normalize(a), normalize(b) );
} //calculateAngle


vec4 epsilonEnvironment(vec4 point, vec3 direction)
{
	//float epsilon = 1.0/160.0;
	const float epsilon = 0.001;//0.00001;
	
	return point + (epsilon * vec4( normalize(direction), 0.0) );
} //epsilonEnvironment

//float decay = 1.0;

bool checkConeOfLight(TraceData td, int pos, Light light, out float decay)
{
	bool result = true;
	decay = 1.0;
	
	if(light.typ == 1.0)
	{
		vec4 pnt = getInverseMatrix(lightPart + pos) * td.iPoint;
		
		result = acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle;
	}
	
	if(light.typ == 2.0)
	{
		vec4 origin = getInverseMatrix(lightPart + pos) * light.origin;
		vec4 pnt = getInverseMatrix(lightPart + pos) * td.iPoint;
		pnt = pnt - origin;
		decay = calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
		result = decay > 0.0;// && pnt.x >= -1.0 && pnt.x <= 1.0;
	}
	
	return result;
}


vec4 randomOrigin(int pos)
{
	vec4 result;
	vec3 start = vec3(-1.0, 0.0, 0.0); //left edge of parallelogram in <0,0>
	
	float x = LCG(random);
	float z = LCG(random);
	
	start.x += 2.0*x;
	start.z += z;
	result = getMatrix(lightPart + pos*sizes[LIGHT]) * vec4(start, 1.0);
	return result;
}


vec3 shade(TraceData td, vec3 normal, vec3 spectrum)
{	
	float angle;
	float AREA_LIGHT = 2.0;
	vec3 color 	= black;
	vec3 col 	= black;
	float phong;// = -1.0;
	Light light;
	vec4 lightOrigin;
	
	td.iPoint = getMatrix(td.id) * td.iPoint;
	td.iPoint = epsilonEnvironment(td.iPoint, normal);
	
	secRay.origin = td.iPoint;
	
	//check used shading
	vec4 c = getObjectColor(td.id);

	phong = c.x == -1.0 ? c.y : -1.0;
	
	
	for(int i = 0; i < lightCount; i++)
	{
		light = getLight( i*sizes[LIGHT] );

		if(light.typ == AREA_LIGHT)
			light.origin = randomOrigin(i);
		
		
		float decay = 1.0;
		
		
		if( checkConeOfLight(td, i*sizes[LIGHT], light, decay) )
		{										
			angle = calculateAngle( light.origin.xyz - td.iPoint.xyz, normal );
			
			if( !testShadow(td, light.origin, angle, light.shadowless, light.isAlsoLight) )
			{
				color += spectrum*calcDirectIllumination(light, td, max(angle, 0.0), normal, phong, i );	
				color = light.typ == AREA_LIGHT ? color * decay : color;
			}
		} //if
		
	} //for
	
	return color;
} //shade


vec3 getDiffuseReflectionRay(vec3 normal)
{		
	float u = LCG(random);
	float v = LCG(random);
	
	float theta = 2.0*PI*u;
	float phi = acos(2.0*v - 1.0);
	float sint = sin(phi);
	
	vec3 s = vec3( cos(theta)*sint, sin(theta)*sint, cos(phi) );
	if( dot(normal, s) < 0.0 )
		s = -s;
	
	return s;
}


float fresnel(vec3 normal, inout vec3 inRay, float iorRatio, inout vec3 reflectedOut, inout vec3 transmittedOut)
{
	float result;
	
	transmittedOut = -inRay;
	float cos = dot(normal, inRay);
	reflectedOut = 2.0*cos*normal + transmittedOut;
	int sign;
	if (cos < 0.0)
	{
		cos = -cos;
		sign = -1;
		iorRatio = 1.0 / iorRatio;
	}
	else
	{
		sign = 1;
	}
	
	float t = (1.0 - iorRatio * iorRatio) + (iorRatio * cos) * (iorRatio * cos);
	if (t <= 0.0)
	{
		result = 1.0;
	}
	else
	{
		transmittedOut = -iorRatio * inRay;
		float cost = sqrt(t);
		transmittedOut = float(sign) * (iorRatio * cos - cost) * normal + transmittedOut;
		
		result = ((t = (cost - iorRatio * cos) / (cost + iorRatio * cos)) * t + (t = (cos - iorRatio
				* cost)
				/ (cos + iorRatio * cost))
				* t) * 0.5;
	}
			
	return result;
}


vec3 computeRGBA_BSDF(vec3 inRay, vec3 outRay, vec3 spectrum, vec3 normal, vec4 rgba )
{
	float iorRatio = 1.0;
	float cos = dot (outRay, normal);
	vec3 rdir;
	vec3 tdir;
	
	if( (dot(inRay, normal) > 0.0) != (cos > 0.0)) 
	{
		spectrum *= 0.0;
	}
	
	float r = fresnel(normal, outRay, iorRatio, rdir, tdir);

	if (cos < 0.0)
	{
		cos = -cos;
	}
	else
	{
		iorRatio = 1.0 / iorRatio;
	}
	
	float trans = (1.0 - rgba.w) * (1.0 - r);
	
	float kd = rgba.x + rgba.y + rgba.z;
	float k = kd + trans;
	
	if( k < pow(10.0, -7.0) )
	{
		kd = 1.0;
	}
	else
	{
		kd /= k;
	}
	
	float t = (1.0/PI) * cos;
	vec3 c = t*rgba.xyz;
	
	
	return spectrum *= c;
	
}


float computeEnv(vec3 outRay, inout vec3 normal, inout vec3 rdir, inout vec3 tdir,
		inout Phong p, bool adjoint)
{
	bool interpolatedTransparency = true;
	float iorRatio = 1.0;
	
	float f = p.dcm.w;
	p.trans.x = 1.0 + f * (p.trans.x - 1.0);
	p.trans.y = 1.0 + f * (p.trans.y - 1.0);
	p.trans.z = 1.0 + f * (p.trans.z - 1.0);
	
	if (interpolatedTransparency)
	{
		p.dcm.x *= 1.0 - p.trans.x;
		p.dcm.y *= 1.0 - p.trans.y;
		p.dcm.z *= 1.0 - p.trans.z;
	}
	

	
	p.srm.x = 4.0;//convertShininess( p.srm.x );
	p.srm.y = 0.0;//MAX_SHININESS;
	
	
	float r = fresnel(normal, outRay, iorRatio, rdir, tdir);
	
	if (interpolatedTransparency)
	{
		p.scm.x *= 1.0 - p.trans.x;
		p.scm.y *= 1.0 - p.trans.y;
		p.scm.z *= 1.0 - p.trans.z;
	}
	
	p.scm = r*p.trans + p.scm; //spec.scaleAdd (r, trans, spec);
	
		
	if (adjoint)
	{
		p.trans *= (1.0 - r); //trans.scale (1 - r);
	}
	else
	{
		float i = iorRatio;
		if( dot(normal, outRay) > 0.0)
		{
			i = 1.0 / i;
		}
		p.trans  *= i * i * (1.0 - r); //trans.scale(i * i * (1 - r));
		p.diffTrans *= i*i; //diffTrans.scale (i * i);
	}
	
	return r;	
} //compEnv


float shininessPow(float x, float shininess)
{
	return (x >= 1.0) ? 1.0 : pow(x, shininess);
}


// inRay: negated direction unit vector of the incoming ray
// outRay: reflectef ray
float compRGBA_MaxRays(vec3 inRay, vec3 specIn, vec3 outRay, vec3 normal, vec4 rgba)
{
	float var;
	float iorRatio = 0.0;
	vec3 tdir;

	
	float r = fresnel( normal, inRay, iorRatio, outRay, tdir );
	
	if( dot(normal, inRay) < 0.0 )
	{
		outRay = -normal;
	}
	else
	{
		outRay = normal;
	}
	
	vec3 tmp;
	tmp.x = rgba.w * rgba.x;
	tmp.y = rgba.w * rgba.y;
	tmp.z = rgba.w * rgba.z;
	
	var = 3.0 * LAMBERTIAN_VARIANCE;
	
	secRay.spectrum = specIn * tmp;
	secRay.direction = outRay;
/*	
	r = (1.0 - rgba.w) * (1.0 - r);
	tmp.x = tmp.y = tmp.z = r;
	
	at this moment only reflected rays are followed
	secRay.spectrum = specIn * tmp;
	
	
	transVariance.x = 0;
	transVariance.y = 0;
	transVariance.z = 0;
*/	
	
	return var;
}


float compPHONG_MaxRays(vec3 inRay, vec3 specIn, vec3 outRay, vec3 normal, Phong p)
{
	float var = 0.0;
	vec3 tdir;
	
	computeEnv(inRay, normal, outRay, tdir, p, true);
	secRay.direction = outRay;
	
	/*
	var = 2.0 / (p.srm.y + 3);
	transVariance.x = var;
	transVariance.y = var;
	transVariance.z = var;
	*/
	
	if(p.scm.x + p.scm.y + p.scm.z <= 0.0)
	{
		secRay.direction = normal;
		var = LAMBERTIAN_VARIANCE;
	}
	else
	{
		var = 2.0 / (p.srm.x + 3.0);
		p.dcm = p.scm;
	}
	
	secRay.spectrum = specIn * p.dcm.xyz;

	/*
	secRay.spectrum = specIn * p.trans.xyz;
	*/
	
	return var;
}


float getReflectionRay(Ray ray, vec3 normal, TraceData td)
{
	vec3 reflected = normalize( reflect(ray.direction, normal) );
	vec4 material = getObjectColor(td.id);
	float var = 2.0;
	
	
	if(material.x == -1.0)
	{	
		Phong p = getPhong(material.y, td);
		var = compPHONG_MaxRays(-ray.direction, ray.spectrum, reflected, normal, p);
	} else
	{
	  	var = compRGBA_MaxRays(-ray.direction, ray.spectrum, reflected, normal, material);
	}

	return var;
}


float getRandomRgbaRay(vec3 outRay, vec3 spectrum, vec3 normal, vec4 rgba)
{
	float result = 0.0;
	vec3 col;
	float iorRatio = 1.0;
	vec3 rdir;
	vec3 tdir;
	bool adjoint = false;
	
	float r = fresnel( normal, outRay, iorRatio, rdir, tdir );
	
	if( dot(normal, outRay) < 0.0 )
	{
		normal = -normal;
	}
	else
	{
		iorRatio = 1.0 / iorRatio;
	}
	
	// compute the transmission coefficient
	float trans = (1.0 - rgba.w) * (1.0 - r);
	
	col.r = rgba.r;
	col.g = rgba.g;
	col.b = rgba.b;

	// compute the probabilities of diffuse reflection and transmission
	// based on color and trans
	float pd = rgba.x + rgba.y + rgba.z;
	float pt = trans;
	float p = pd + pt;
	bool absorbed = p < pow(10.0, -7.0);
	if (!absorbed)
	{
		p = 1.0 / p;
		pd *= p;
		pt *= p;
		col *= 1.0/pd;
	}
	
	mat3 diffBasis;
	
	float cost, sint;
	
	float z = LCG(random);
	
	float t;
	
	if(absorbed)
	{
		secRay.spectrum = vec3(0.0);
		secRay.direction = vec3(1.0, 0.0, 0.0);
		result = 0.0;
	} else if (z < pt)
	{
		result = pt,
		// this is a transmitted ray
		t = trans / pt;
		secRay.spectrum = t * spectrum;
		secRay.direction = refract(outRay, normal, iorRatio);
		
	} else
	{
		// this is a reflected ray


		getOrthogonalBasis (normal, diffBasis, true);		

		
		float j = LCG(random);
		
		cost = sqrt(j);
		
		sint = sqrt(1.0 - j);
		
		
		
		float u = LCG(random);
		float phi = 2.0*PI*u;
		
		vec3 dir = vec3( cos(phi)*sint, sin(phi)*sint, cost );
		secRay.direction = diffBasis * dir;
//		secRay.direction = getDiffuseReflectionRay(normal);
		secRay.spectrum = spectrum * col;

		result = pd*cost;//(pd * cost)/PI;
	}

	return result;
}

float myDot(vec3 a, vec3 b)
{
	return 0.2989 * a.x * b.x + 0.5866 * a.y * b.y + 0.1145 * a.z * b.z;
}


float getRandomPhongRay(vec3 outRay, vec3 spectrum, vec3 normal, Phong phong)
{
	float result;
	vec3 rdir;
	vec3 tdir;
	computeEnv(outRay, normal, rdir, tdir, phong, false);
	
	float ocos = abs( dot(outRay, normal) );
	vec3 col = spectrum;
	
	float pd 	= myDot(col, phong.dcm.xyz);
	float ps 	= myDot(col, phong.scm.xyz);
	float pdt 	= myDot(col, phong.diffTrans.xyz);
	float pst 	= myDot(col, phong.trans.xyz);
	float p 	= pd + ps + pst + pdt;
	
	if (p < pow(10.0, -7.0))
	{
		p = 0.0;
	}
	else
	{
		p = 1.0 / p;
	}
	
	pd *= p; 	// pd: diffusely reflected fraction
	ps *= p; 	// ps: specularly reflected fraction
	pst *= p; 	// pt: specularly transmitted fraction
	pdt *= p; 	// pt: diffusely transmitted fraction

	// pd, ps, pdt and pst sum up to 1, or all are 0 if ray is completely absorbed
	
	mat3 diffBasis, specBasis, transBasis, m;
	
	// determine randomly if the ray is diffusely or specularly reflected
	// or transmitted (according to probabilities pd, ps, pdt, pst)
	float z = LCG(random);
	// z is uniformly distributed between 0 and 1
	
	float sint, cost;
	if(p == 0.0)
	{
		// completely absorbed
		col = vec3(0.0);
		secRay.direction = vec3(1.0, 0.0, 0.0);
		result = 0.0;
	} else
	{
		bool transmitted = false;
		bool specular = false;
		float shininess = phong.srm.x;
		if(z <= pst)
		{
			// this is a specularly transmitted ray
			transmitted = true;
			specular = true;
			shininess = phong.srm.x;
			
			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of ideal refraction
			getOrthogonalBasis(tdir, transBasis, true);
			m = transBasis;
		}
		else if (z <= pst + ps)
		{
			// this is a specularly reflected ray
			transmitted = false;
			specular = true;
			shininess = phong.srm.x;
			
			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of ideal reflection
			getOrthogonalBasis(rdir, specBasis, true);
			m = specBasis;			
		} 
		else if(z > pst + ps)
		{
			// this is a diffusely transmitted or reflected ray
			transmitted = z <= pst + ps + pdt;
			specular = false;
			shininess = 0.0;			
			
			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of normal			
			getOrthogonalBasis(normal, diffBasis, true);
			m = diffBasis;			
		}
		
		float j = LCG(random);
		float cosr;
		if (specular)
		{
			// choose theta randomly according to the density
			// (n+1) / 2PI * cos(theta)^n 
			cost = pow(j, 1.0 / (shininess + 1.0));
			sint = sqrt(1.0 - cost * cost);
			cosr = cost;
		}
		else
		{
			// choose theta randomly according to the density cos(theta)/PI
			z = j;
			cost = sqrt(z);
			sint = sqrt(1.0 - z);
			if(transmitted)
			{
				cost = -cost;
			}
			cosr = 0.0;
		}
		// choose phi randomly between 0 and 2 PI
		float u = LCG(random);
		float phi = 2.0*PI*u;

		vec3 dir = vec3( cos(phi)*sint, sin(phi)*sint, cost );		
		secRay.direction = m * dir;

		
		cost = dot(secRay.direction, normal);
		if (transmitted)
		{
			cost = -cost;
		}
		if (cost > 0.0)
		{
			// OK, randomly chosen direction points to the side
			// of the normal					
			col = (col/PI) + (transmitted ? phong.diffTrans.xyz : phong.dcm.xyz);

			// z: probability densitity of choosing in as diffusely
			// reflected direction
			z = (transmitted ? pdt : pd) * cost * (1.0/PI);


			if (!specular)
			{
				cosr = dot(secRay.direction, (transmitted ? tdir : rdir) );
			}

			float prob = transmitted ? pst : ps;
			if( (cosr > 0.0) && (prob > 0.0) )
			{
				// angle between in and ideally reflected/refracted direction
				// is less than 90 degress. Thus, this ray could
				// have been chosen as a specularly reflected/refracted ray, too
				cosr = shininessPow(cosr, shininess);
			
	//			col = (shininess + 2.0) * cosr * ( (1.0/(2.0*PI)) / max(cost, ocos) ) * (transmitted ? phong.trans.xyz : phong.scm.xyz) + col;
				
	//			z += prob * (shininess + 1.0) * cosr * (1.0/(2.0*PI));
			}
				
			if(z != 0.0)
			col *= cost/z;
			result = z;
		
		}
		else
		{
			// direction points to the back-side, reset ray.
			// This can only happen for Phong shaders. It reflects
			// the fact that the total reflectivity of Phong's
			// reflection model depends on the angle between out
			// and normal: For non-perpendicular rays, an additional
			// fraction is absorbed.
			col = vec3(0.0);
			result = 0.0;
		}
		
	} //if
	
	secRay.spectrum = spectrum * col;

	return result;
}


float getRandomRay(Ray outRay, vec3 normal, TraceData td)
{
	float var;
	vec4 material = getObjectColor(td.id);
	
	if(material.x == -1.0) //phong
	{
		Phong p = getPhong(material.y, td);
		var = getRandomPhongRay(-outRay.direction, outRay.spectrum, normal, p);
	} else
	{
		var = getRandomRgbaRay(-outRay.direction, outRay.spectrum, normal, material);
	}
	
	return var;
}


float getSecondaryRay(Ray ray, vec3 normal, TraceData td)
{		
	return pt ? getRandomRay(ray, normal, td) : getReflectionRay(ray, normal, td);
}


vec3 trace(Ray ray, inout float var)
{	
	vec3 color = black;


	TraceData td = accelerate ? traverseTree(ray, 0) : intersection(ray);
	
	
	if(td.hasIntersection)
	{
		vec3 normal = calculateNormal(td);
		
		color = shade(td, normal, ray.spectrum);
//		color = test;
		
		var = getSecondaryRay(ray, normal, td);
	} else
	{
		secRay.spectrum = vec3(0.0);
	}
	
	
	
	return color;
} //trace	


void init(void)
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	
	refDeep = -1.0;
} //init


void main(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	vec4 oldColor		= texture2DRect(a0, texCoord);
	vec4 origin 		= texture2DRect(a1, texCoord);
	vec4 dir 			= texture2DRect(a2, texCoord);
	vec4 shine			= texture2DRect(a3, texCoord);

	init();
	primRay  = Ray(origin, dir.xyz, -1, shine.xyz);
	random 	 = dir.w;
	
	// init rng	 
	//random = 1;
	//random = mod(dot(origin, dir), m) / m;
	//seed = fract(2 * origin + 3 * dir);
	//random = dot(origin - dir, vec4(1));
	//random = mod(dot(origin - dir, vec4(1)), 1);
	//random = mod(dot(origin, dir), 1);
	//random = floor(dot(gl_FragCoord.xy, vec2(3.0, sqrt(m))));
	//random = floor(dot(origin - dir, vec4(1)) + dot(gl_FragCoord.xy, vec2(3.0, sqrt(m))) + dir.w);
	//random = floor(mod(dir.w + dot(origin - dir, vec4(1)), m));
	//seed = fract(vec4(gl_FragCoord.xy, gl_FragCoord.xy));
	//random = mod(pow(2.0, gl_FragCoord.x + 1.0) * pow(3.0, gl_FragCoord.y + 1.0), 1.0);
	//random = mod(pow(2.0, gl_FragCoord.x + 1.0) * pow(3.0, gl_FragCoord.y + 1.0) / 65537.0, 1.0);
	seed = origin - dir;
	seed = fract(vec4(gl_FragCoord.xy * seed.xy, seed.wz));
	n = origin - dir;
	//n = gl_FragCoord;
	//n = vec4(gl_FragCoord.xy, dir.w, 2.0 / gl_FragCoord.x - 3.0 / gl_FragCoord.y);
	
	//seed0 = vec4(1.0, dir.w, gl_FragCoord.xy);
	//seed1 = 1.0 / vec4(2.0, 3.0, 5.0, 7.0);
	//seed1.w = mod(dot(gl_FragCoord.xy, dir.ww) / 65537.0, 1.0);
//	seed1.w = mod(3.0 / 65537.0, 1.0);
	//seed1.w = mod(dir.w, 1.0);
	seed1.z = mod(seed1.w * 75.0, 1.0);
	seed1.y = mod(seed1.z * 75.0, 1.0);
	seed1.x = mod(seed1.y * 75.0, 1.0);
	//seed0.w = mod(dot(gl_FragCoord.xy, dir.ww) / 65537.0, 1.0);
	seed0.w = mod(seed1.x * 75.0, 1.0);
	seed0.z = mod(seed0.w * 75.0, 1.0);
	seed0.y = mod(seed0.z * 75.0, 1.0);
	seed0.x = mod(seed0.y * 75.0, 1.0);
	
	seed7.xyzw = dir.wwww;
	seed7.xy *= seed7.xy;
	seed7.xz *= seed7.w;
	seed7 = mod(seed7, 1.0);
	seed6 = mod(seed7 * 75.0, 1.0);
	seed5 = mod(seed6 * 75.0, 1.0);
	seed4 = mod(seed5 * 75.0, 1.0);
	seed3 = mod(seed4 * 75.0, 1.0);
	seed2 = mod(seed3 * 75.0, 1.0);
	seed1 = mod(seed2 * 75.0, 1.0);
	seed0 = mod(seed1 * 75.0, 1.0);
			
	float variance	= 1.0;
	vec3 currentColor = black;
	
	
	
	currentColor = trace(primRay, variance);	
		
	
	gl_FragData[0] = vec4(oldColor.xyz + shine.w*currentColor/superSample, 1.0);
	gl_FragData[1] = secRay.origin;
	gl_FragData[2] = vec4(secRay.direction, random);
	gl_FragData[3] = vec4(secRay.spectrum, variance);
} //main

