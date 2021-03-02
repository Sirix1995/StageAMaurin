float computeBSDF_Phong(Phong phong, LambdaLight light, TraceData td)
{
	float col;
	vec3 outRay = normalize(light.origin.xyz - td.iPoint.xyz);
	vec3 inRay 	= normalize(-primRay.direction);
	float cos 	= dot(outRay, td.normal);
	float icos	= dot(inRay, td.normal);

	bool isIcos	= (icos > 0.0);
	bool isCos 	= (cos > 0.0);

	bool transmitted = (isIcos && !isCos) || (!isIcos && isCos);

	cos *= sign(cos);

	vec3 rdir = vec3(0.0);
	vec3 tdir = vec3(0.0);

	computeEnv(outRay, td.normal, rdir, tdir, phong, false);

	float shininess = phong.srm.x;;

	if (transmitted)
	{
		float t 		= phong.diffTrans.r;
		phong.diffTrans	= phong.dcm;
		phong.dcm.rgb 	= vec3(t, 0.0, 0.0);
		t 				= phong.trans.r;
		phong.trans 	= phong.scm;
		phong.scm.rgb 	= vec3(t, 0.0, 0.0);
		shininess 		= phong.srm.y;
		rdir 			= tdir;
	}

	float kd 	= phong.dcm.r; 	// + phong.dcm.g 	+ phong.dcm.b;
	float ks 	= phong.scm.r; 	// + phong.scm.g 	+ phong.scm.b;
	float kt 	= phong.trans.r; 	// + phong.trans.g 	+ phong.trans.b;
	float k 	= kd + ks + kt;

	if(k > 0.0)
	{
		k = 1.0 / k;
	}

	float c = dot(rdir, inRay);

	col = _PI * cos * phong.dcm.r;

	if((c > 0.0) && (ks > 0.0))
	{
		c 		= shininessPow(c, shininess);
		icos 	= abs(icos);
		col    += (shininess + 2.0) * c * _PI_ * ((icos > cos) ? cos / icos : 1.0) * phong.scm.r;
	}

	return light.intensity*col;
} //computeBSDF
