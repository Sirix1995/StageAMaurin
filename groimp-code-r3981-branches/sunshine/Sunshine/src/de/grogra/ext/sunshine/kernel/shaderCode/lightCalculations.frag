float convertShininess(float x)
{
	float result;

	x = x * (2.0 - x);
	if (x <= 0.0)
	{
		result = 0.0;
	}
	else if (x >= 1.0)
	{
		result = MAX_SHININESS;
	}
	else
	{
		result = min(-2.0 / log(x), MAX_SHININESS);
	}

	return result;
} //convertShininess


float fresnel(vec3 normal, vec3 inRay, float iorRatio, inout vec3 reflectedOut, inout vec3 transmittedOut)
{
	float result;

	transmittedOut = -inRay;
	float cos = dot(normal, inRay);
	reflectedOut = 2.0 * cos * normal + transmittedOut;
	float sign;
	if(cos < 0.0)
	{
		cos = -cos;
		sign = -1.0;
		iorRatio = 1.0 / iorRatio;
	}
	else
	{
		sign = 1.0;
	}

	float t = (1.0 - iorRatio * iorRatio) + (iorRatio * cos) * (iorRatio * cos);

	if(t <= 0.0)
	{
		result = 1.0;
	}
	else
	{
		transmittedOut = -iorRatio * inRay;
		float cost = sqrt(t);
		transmittedOut = (sign * (iorRatio * cos - cost) * normal) + transmittedOut;

		result = ((t = (cost - iorRatio*cos) / (cost + iorRatio*cos))*t +
				(t = (cos - iorRatio* cost) / (cos + iorRatio * cost))*t)*0.5;
	}

	return result;
} //fresnel

float computeEnv(vec3 outRay, inout vec3 normal, inout vec3 rdir, inout vec3 tdir,
inout Phong p, bool adjoint)
{
	bool interpolatedTransparency = true;
	float iorRatio = 1.0 / p.srm.w;

	float f = p.dcm.a;
	p.trans.r = 1.0 + f * (p.trans.r - 1.0);
	p.trans.g = 1.0 + f * (p.trans.g - 1.0);
	p.trans.b = 1.0 + f * (p.trans.b - 1.0);

	if (interpolatedTransparency)
	{
		p.dcm.r *= 1.0 - p.trans.r;
		p.dcm.g *= 1.0 - p.trans.g;
		p.dcm.b *= 1.0 - p.trans.b;
	}

	if(p.srm.x >= 0.0)
	{
		p.srm.x = convertShininess(p.srm.x);
	}
	else
	{
		p.srm.x = DEFAULT_SHININESS;
	}
	
	// transparencyShininess
	if(p.srm.y >= 0.0)
	{
		p.srm.y = convertShininess(p.srm.y);
	}
	else
	{
		p.srm.y = MAX_SHININESS;
	}

	float r = fresnel(normal, outRay, iorRatio, rdir, tdir);

	if (interpolatedTransparency)
	{
		p.scm.r *= 1.0 - p.trans.r;
		p.scm.g *= 1.0 - p.trans.g;
		p.scm.b *= 1.0 - p.trans.b;
	}

	p.scm += r*p.trans; //spec.scaleAdd (r, trans, spec);


	if (adjoint)
	{
		p.trans *= (1.0 - r); //trans.scale (1 - r);
	}
	else
	{
		float i = iorRatio;
		if(dot(normal, outRay)> 0.0)
		{
			i = 1.0 / i;
		}
		p.trans *= i * i * (1.0 - r); //trans.scale(i * i * (1 - r));
		p.diffTrans *= i*i; //diffTrans.scale (i * i);
	}
	
	if(dot(normal, outRay) < 0.0)
	{
		normal = -normal;
	}

	return r;
} //compEnv


float shininessPow(float x, float shininess)
{
	return x >= 1.0 ? 1.0 : pow(x, shininess);
}
