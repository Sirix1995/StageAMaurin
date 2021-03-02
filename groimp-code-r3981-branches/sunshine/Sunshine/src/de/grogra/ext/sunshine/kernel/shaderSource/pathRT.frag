void getRandomRgbaRay(vec3 outRay, vec3 spectrum, vec3 normal, vec4 color, float ior)
{
	vec3 rdir;
	vec3 tdir;
	bool adjoint = false;

	float r = fresnel(normal, outRay, ior, rdir, tdir);

	if (dot(normal, outRay) < 0.0)
	{
		normal = -normal;
	}
	else
	{
		ior = 1.0 / ior;
	}

	// compute the transmission coefficient
	float trans = (1.0 - color.a) * (1.0 - r);

	// compute the probabilities of diffuse reflection and transmission
	// based on color and trans
	float pd = color.r + color.g + color.b;
	float pt = trans;
	float p = pd + pt;
	bool absorbed = p < pow(10.0, -7.0);
	if (!absorbed)
	{
		p = 1.0 / p;
		pd *= p;
		pt *= p;
		color.rgb *= 1.0/pd;
	}

	mat3 diffBasis;

	float cost, sint;

	float z = rand();

	float t;

	if (absorbed)
	{
		secRay.spectrum = vec3(0.0);
		secRay.direction = vec3(1.0, 0.0, 0.0);
	}
	else if (z < pt)
	{
		// this is a transmitted ray
		t = trans / pt;
		secRay.spectrum = t * spectrum;

		secRay.direction = tdir;

	}
	else
	{
		// this is a reflected ray
		diffBasis = getOrthonormalBasis(normal);

		float j = rand();

		cost = sqrt(j);

		sint = sqrt(1.0 - j);

		float u = rand();
		float phi = 2.0*PI*u;

		vec3 dir = vec3(cos(phi)*sint, sin(phi)*sint, cost);
		secRay.direction = diffBasis * dir;
		secRay.spectrum = spectrum * color.rgb;
	}
} //getRandomRgbaRay

float myDot(vec3 a, vec3 b)
{
	return 0.2989 * a.x * b.x + 0.5866 * a.y * b.y + 0.1145 * a.z * b.z;
}

void getRandomPhongRay(vec3 outRay, vec3 spectrum, vec3 normal, Phong phong)
{
	vec3 rdir;
	vec3 tdir;
	computeEnv(outRay, normal, rdir, tdir, phong, false);

	float ocos = abs(dot(outRay, normal) );
	vec3 col = spectrum;

	float pd = myDot(col, phong.dcm.rgb);
	float ps = myDot(col, phong.scm.rgb);
	float pdt = myDot(col, phong.diffTrans.rgb);
	float pst = myDot(col, phong.trans.rgb);
	float p = pd + ps + pst + pdt;

	if (p < 1e-7)
	{
		p = 0.0;
	}
	else
	{
		p = 1.0 / p;
	}

	pd 	*= p; // pd: diffusely reflected fraction
	ps 	*= p; // ps: specularly reflected fraction
	pst *= p; // pt: specularly transmitted fraction
	pdt *= p; // pt: diffusely transmitted fraction
	// pd, ps, pdt and pst sum up to 1, or all are 0 if ray is completely absorbed


	mat3 m = getOrthonormalBasis(normal);
	vec3 dir;

	// determine randomly if the ray is diffusely or specularly reflected
	// or transmitted (according to probabilities pd, ps, pdt, pst)

	float z = rand();
	// z is uniformly distributed between 0 and 1

	float sint, cost;
	if (p == 0.0)
	{
		// completely absorbed
		col = vec3(0.0);
		secRay.direction = vec3(1.0, 0.0, 0.0);
	}
	else
	{
		bool transmitted	= false;
		bool specular 		= false;
		float shininess 	= phong.srm.x;
		if (z <= pst)
		{
			// this is a specularly transmitted ray
			transmitted = true;
			specular 	= true;
			shininess 	= phong.srm.y;

			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of ideal refraction
			m = getOrthonormalBasis(tdir);
		}
		else if (z <= pst + ps)
		{
			// this is a specularly reflected ray
			transmitted = false;
			specular 	= true;
			shininess 	= phong.srm.x;

			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of ideal reflection
			m = getOrthonormalBasis(rdir);
		}
		else if (z > pst + ps)
		{
			// this is a diffusely transmitted or reflected ray
			transmitted = z <= pst + ps + pdt;
			specular 	= false;
			shininess 	= 0.0;

			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of normal
			m = getOrthonormalBasis(normal);
		}

		float j = rand();
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
			if (transmitted)
			{
				cost = -cost;
			}
			cosr = 0.0;
		}

		// choose phi randomly between 0 and 2 PI
		float phi = 2.0*PI*rand();

		secRay.direction = m * vec3(cos(phi)*sint, sin(phi)*sint, cost);

		cost = dot(secRay.direction, normal);
		if (transmitted)
		{
			cost = -cost;
		}
		if (cost > 0.0)
		{
			// OK, randomly chosen direction points to the side
			// of the normal
			col = _PI * (transmitted ? phong.diffTrans.rgb : phong.dcm.rgb);

			// z: probability densitity of choosing in as diffusely
			// reflected direction
			z = (transmitted ? pdt : pd) * cost * _PI;

			if (!specular)
			{
				cosr = dot(secRay.direction, transmitted ? tdir : rdir);
			}

			float prob = transmitted ? pst : ps;
			if ( (cosr > 0.0) && (prob > 0.0))
			{
				// angle between in and ideally reflected/refracted direction
				// is less than 90 degress. Thus, this ray could
				// have been chosen as a specularly reflected/refracted ray, too
				cosr = shininessPow(cosr, shininess);
				cosr = cosr > 0.0 ? cosr : 1.0;
				col += (shininess + 2.0) * cosr * _PI_ / max(cost, ocos)
						* (transmitted ? phong.trans.rgb : phong.scm.rgb);

				z += prob * (shininess + 1.0) * cosr * _PI_;
			}

			if (z != 0.0)
				col *= cost/z;
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
		}

	} //if

	secRay.spectrum = spectrum * col;
}

float getRandomRay(Ray outRay, vec3 normal, TraceData td)
{
	float var;
	float ior = 1.0;
	vec4 material = getObjectColor(td.type == TRI ? meshPart : float(td.id), ior);

	if (material.x == -1.0) //phong
	{
		Phong p = getPhong(material.y, td);
		getRandomPhongRay(-outRay.direction, outRay.spectrum, normal, p);
	}
	else
	{
		getRandomRgbaRay(-outRay.direction, outRay.spectrum, normal, material, ior);
	}

	return var;
}

void getSecondaryRay(Ray ray, vec3 normal, TraceData td)
{
	getRandomRay(ray, normal, td);
}
