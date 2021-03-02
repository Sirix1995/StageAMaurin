
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
} //fresnel

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
