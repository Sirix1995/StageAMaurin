
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

void getReflectionRay(Ray ray, vec3 normal, TraceData td)
{
	vec3 reflected = normalize( reflect(ray.direction, normal) );
	vec4 material = getObjectColor(td.id);
	float var = 0.0;
	
	
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

void getSecondaryRay(Ray ray, vec3 normal, TraceData td)
{		
	getReflectionRay(ray, normal, td);
}