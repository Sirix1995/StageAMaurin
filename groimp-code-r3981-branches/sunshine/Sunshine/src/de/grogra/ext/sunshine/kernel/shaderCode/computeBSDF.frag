
float computeBSDF(vec4 color, vec3 normal, vec3 inRay, vec3 specIn, vec3 outRay, 
		bool adjoint, int id, inout vec3 bsdf)
{
	float dirDens = 0.0;
	float cos = dot(outRay, normal);
	if( (dot(inRay, normal) > 0.0) != (cos > 0.0) )
	{			
		bsdf = vec3(0.0);
	} 
	else
	{
		float ior = getIOR(id);
		
		vec3 tmpVec1;
		vec3 tmpVec2;
		float r = fresnel(normal, outRay, ior, tmpVec1, tmpVec2);
		
		if(cos < 0.0)
		{
			cos = -cos;
		}
		else
		{
			ior = 1.0 / ior;
		}

		float trans = (1.0 - color.a) * (1.0 - r);
		if(!adjoint)
		{
			trans *= ior * ior;
		}
		float kd = color.r + color.g + color.b;
		float k = kd + trans;
		
		if(k < 1e-7)
		{
			kd = 1.0;
		}
		else
		{
			kd /= k;
		}

		float t = _PI * cos;
		
//		bsdf = t*tmpVec1*color.rgb*specIn;
		bsdf = cos*color.rgb*specIn;
		
		dirDens = kd * t;
	}
	
	return dirDens;

} //computeBSDF


float computeBSDF(Phong phong, vec3 normal, vec3 inRay, vec3 specIn, vec3 outRay, 
		bool adjoint, inout vec3 bsdf)
{
	vec3 col;
	float cos 	= dot(outRay, normal);
	float icos 	= dot(inRay, normal);
	bool transmitted = (icos > 0.0) != (cos > 0.0);
	if (cos < 0.0)
	{
		cos = -cos;
	}
	
	vec3 rdir;
	vec3 tdir;
	computeEnv(outRay, normal, rdir, tdir, phong, adjoint);

	cos 	= dot(outRay, normal);
	icos 	= dot(inRay, normal);
	if(cos < 0.0)
	{
		cos = -cos;
	}
	
	float shininess;
	if (transmitted)
	{
		vec3 t = phong.diffTrans.rgb; 
		phong.diffTrans = phong.dcm; 
		phong.dcm.rgb = t;
		t = phong.trans.rgb; 
		phong.trans = phong.scm; 
		phong.scm.rgb = t;
		shininess = phong.srm.y;
		rdir = tdir;
	}
	else
	{
		shininess = phong.srm.x;
	}
		
	float kd = phong.dcm.r + phong.dcm.g + phong.dcm.b;
	float ks = phong.scm.r + phong.scm.g + phong.scm.b;
	float kt = phong.trans.r + phong.trans.g + phong.trans.b;
	
	float k = kd + ks + kt;
	if(k > 0.0)
	{
		k = 1.0 / k;
	}

	
	float c = dot(rdir, inRay);
	float p = kd * k * cos * _PI;
	col = _PI * cos * phong.dcm.rgb;
	if((c > 0.0) && (ks > 0.0))
	{
		c = shininessPow(c, shininess);
		icos = abs(icos);
		col += (shininess + 2.0) * c * _PI_ * ((icos > cos) ? cos / icos : 1.0) * phong.scm.rgb;
		p += ks * k * (shininess + 1.0) * c * _PI_;
	}
	
	
	bsdf = specIn*col;
		
	return p;
} //computeBSDF