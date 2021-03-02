vec3 computeBSDF(Phong phong, Light light, vec3 pointToLight, vec3 normal, TraceData td)
{
	vec3 col;
	vec3 outRay = normalize(pointToLight);
	vec3 inRay 	= normalize(-primRay.direction);
	float cos 	= dot(outRay, normal);
	float icos	= dot(inRay, normal);

	bool isIcos	= (icos > 0.0);
	bool isCos 	= (cos > 0.0);

	bool transmitted = (isIcos && !isCos) || (!isIcos && isCos);

	cos *= sign(cos);

	vec3 rdir = vec3(0.0);
	vec3 tdir = vec3(0.0);

	computeEnv(outRay, normal, rdir, tdir, phong, false);

	float shininess = phong.srm.x;;

	if (transmitted)
	{
		vec3 t 			= phong.diffTrans.rgb;
		phong.diffTrans	= phong.dcm;
		phong.dcm.rgb 	= t;
		t 				= phong.trans.rgb;
		phong.trans 	= phong.scm;
		phong.scm.rgb 	= t;
		shininess 		= phong.srm.y;
		rdir 			= tdir;
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
//	float p = kd * k * cos * _PI;
	col = _PI * cos * phong.dcm.rgb;
	if((c > 0.0) && (ks > 0.0))
	{
		c = shininessPow(c, shininess);
		icos = abs(icos);
		col += (shininess + 2.0) * c * _PI_ * ((icos > cos) ? cos / icos : 1.0) * phong.scm.rgb;
	}

	return light.color.rgb*col;
} //computeBSDF

vec3 calcRGB(vec3 color, Light light, float angle)
{
	return color * light.color.rgb * angle;
}

vec3 calcDirectIllumination(Light light, vec3 pointToLight, TraceData td,
		float angle, vec3 normal, float phong, int counter, float decay)
{
	vec3 result;
	if(phong < 0.0)
	{
		result = calcRGB(getObjectColor(td.id).rgb, light, angle);
	}
	else
	{
		result = computeBSDF(getPhong(phong,td), light, pointToLight, normal, td);
	}

	return decay*result;
}

