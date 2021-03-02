UV getUV(TraceData td)
{
	UV result = UV(0.0, 0.0);

	td.iPoint = getInverseMatrix(td.id) * vec4(td.iPoint.xyz, 1.0);

	if(td.type == SPHERE) // is a sphere -> sphere mapping
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );

	if(td.type == BOX) //is a box
	{
		if( td.iPoint.z >= 0.999 ) //top
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );

		if( td.iPoint.z <= 0.001 ) //bottom
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );

		if( td.iPoint.y <= -0.499 )
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );

		if( td.iPoint.y >= 0.499 )
			result = UV( abs(td.iPoint.x - 0.5), 1.0-td.iPoint.z );

		if( td.iPoint.x >= 0.499 )
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );

		if( td.iPoint.x <= -0.499 )
			result = UV( abs(td.iPoint.y - 0.5), 1.0-td.iPoint.z );
	} //if

	if(td.type == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );

	if(td.type == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );

	if(td.type == CFC)
	{
		CylFruCo cfc = getCFC(td.id);

		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);

		if(cfc.type == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );

		if(cfc.type == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );

			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		}

		if(cfc.type == 2.0)
		{
			float zmin = cfc.max <= 0.0 ? -1.0 : 1.0;
			if(cfc.max <= 0.0) //zmin = -1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z)*2.0 -1.0 );

				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );

				if( td.iPoint.z < -0.999)
					result = UV( (td.iPoint.x/2.0)+0.5, 1.0-(td.iPoint.y/2.0)+0.5 );

			} else //zmin = 1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), (abs(td.iPoint.z)-1.0) / (cfc.max-1.0) );
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );

				if( td.iPoint.z < 1.001	)
					result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
			}
		}
	}

	return result;
}

UV getUvFloat(UV uv, float scaleU, float scaleV, float angle)
{
	mat3 m 	= mat3(1.0);
	mat3 i 	= mat3(1.0);

	m[0][2]	= 0.0;
	m[1][2]	= 0.0;

	i[0][0]	= cos(-angle);
	i[1][0]	= -sin(-angle);
	i[0][1]	= sin(-angle);
	i[1][1]	= cos(-angle);

	i[2][2]	= 1.0;

	i 		= i*m;

	m[0][0]	= scaleU;
	m[1][1]	= scaleV;
	m[0][2]	= 0.0;
	m[1][2]	= 0.0;

	m 		= m*i;

	uv.u 	= m[0][0] * uv.u + m[0][1] * uv.v + m[0][2];
	uv.v 	= m[1][0] * uv.u + m[1][1] * uv.v + m[1][2];
	uv.u 	= mod(uv.u, 1.0);
	uv.v 	= mod(uv.v, 1.0);

	return uv;
}
