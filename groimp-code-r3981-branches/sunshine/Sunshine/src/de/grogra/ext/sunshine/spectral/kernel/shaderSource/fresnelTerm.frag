float fresnelTerm(vec3 v,			///< [in] Incoming vector
		vec3 n,						///< [in] Normal at point of reflectance
		float Ni,					///< [in] Index of refraction of outside (IOR refracting from)
		float Nt,					///< [in] Index of refraction of inside (IOR refracting to)
		float kt	)
{
	float cos = abs(dot(v, n));
	if( cos < EPSILON ) {
		cos = EPSILON;
	}

	float cos2 = cos*cos;
	float sin2 = 1 - cos2;

	if( sin2 < 0.0 ) {
		sin2 = 0.0;
	}

	float sin 	= sqrt(sin2);
	float tan 	= sin / cos;
	float tan2 	= tan*tan;

	float Ni2 	= Ni*Ni;
	float Nt2 	= Nt*Nt;
	float kt2 	= kt*kt;
	float Ns  	= Ni2*sin2;
	float Nk  	= 4*Nt2*kt2;
	float A2 	= Nt2 - kt2 - Ns;
	float Sq 	= sqrt(A2*A2 + Nk);

	float b2 	= Sq - Nt2 + kt2 + Ns;
	A2 			= Sq + Nt2 - kt2 - Ns;
	float dNi 	= (0.5 / Ni2);

	A2 			= A2 * dNi;
	b2 			= b2 * dNi;

	A2 			= max(A2, 0.0 );
	b2 			= max(b2, 0.0 );

	float a 	= sqrt(A2);
	float ab2 	= A2 + b2;
	float dcos 	= 2*a*cos;
	float astan	= 2*a*sin*tan;
	float stan 	=  sin2*tan2;
	float Rs 	= (ab2 - dcos  + cos2) / (ab2 + dcos + cos2);
	float Rp 	= Rs * (ab2 - astan + stan) / (ab2 + astan + stan);

	return 0.5*(Rp + Rs);
}
