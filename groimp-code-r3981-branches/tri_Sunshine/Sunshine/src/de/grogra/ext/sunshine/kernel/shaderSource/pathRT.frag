
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
		secRay.direction = mulMat(diffBasis, dir);
//		secRay.direction = getDiffuseReflectionRay(normal);
		secRay.spectrum = spectrum * col;

		result = pd*cost;//(pd * cost)/PI;
		
	}
		//normalTest = vec4(result);
	
	
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
		secRay.direction = mulMat(m, dir);		
		
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
	return getRandomRay(ray, normal, td);
}
