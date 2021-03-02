
vec3 calculateNormal(TraceData td)
{
	vec3 normal = vec3(0.0);
	//normal = td.normal;

	if(td.type == SPHERE)
	{
		normal = td.iPoint.xyz;
	}

	if(td.type == BOX)
	{
		Box b = getBox(td.id);

		//front
		if( td.iPoint.y <= b.parameters[1].y - b.deep )
			normal = vec3(0.0, -1.0, 0.0);

		//top
		if( td.iPoint.z >= b.parameters[0].z + b.height )
			normal = vec3(0.0, 0.0, 1.0);

		//right
		if( td.iPoint.x >= b.parameters[0].x + b.width )
			normal = vec3(1.0, 0.0, 0.0);

		//bottom
		if( td.iPoint.z <= b.parameters[1].z - b.height )
			normal = vec3(0.0, 0.0, -1.0);

		//back
		if( td.iPoint.y >= b.parameters[0].y + b.deep )
			normal = vec3(0.0, 1.0 ,0.0);

		//left
		if( td.iPoint.x <= b.parameters[1].x - b.width )
			normal = vec3(-1.0, 0.0, 0.0);

	} //box


	if(td.type == CFC) //CFC
	{
		CylFruCo cfc = getCFC(td.id);

		if(cfc.type == 0.0)
		{
			normal = td.iPoint.xyz - vec3(0.0, 0.0, td.iPoint.z);

			//top
			if(td.iPoint.z > 0.9999) normal = vec3(0,0,1.0);
			//bottom
			if(td.iPoint.z < 0.0001) normal = vec3(0,0, -1.0);
		}
		else if(cfc.type == 1.0)
		{
			normal = vec3(td.iPoint.xy, -td.iPoint.z);
			//bottom
			if(td.iPoint.z < -0.999) normal = vec3(0.0, 0.0, -1.0);
		}
		else if(cfc.type == 2.0)
		{
			normal = vec3(td.iPoint.xy, -td.iPoint.z);

			//top
			if(td.iPoint.z > cfc.max-0.001)
				normal = vec3(0,0, 1.0);

			float min = cfc.max > 0.0 ? 1.0 : -1.0;

			//bottom
			if(td.iPoint.z < min+0.001)
				normal = vec3(0,0, -1.0);
		}


		if(td.invertNormal) normal *= -1.0;
	} //cfc


	if(td.type == PLANE || td.type == PARA)
	{
		normal = vec3(0.0, 0.0, 1.0);

		if(td.type == PARA) normal = vec3(0.0, 1.0, 0.0);

		if(td.invertNormal) normal *= -1.0;
	}

	mat4 m4x4;

	if(td.type == TRI)
	{
		vec3 vert0 = lookUp(td.id + 0).xyz;
		vec3 vert1 = lookUp(td.id + 1).xyz;
		vec3 vert2 = lookUp(td.id + 2).xyz;

		vec3 edge1 = vert1 - vert0;
		vec3 edge2 = vert2 - vert0;

		normal = cross(edge1, edge2);
		//normal = vec3(1.0, 1.0, 0.0);

		if(td.invertNormal) normal *= -1.0;

		m4x4 = getInverseMatrix(meshPart);

	} else {
		m4x4 =	getInverseMatrix(td.id);
	}

	m4x4 = transpose(m4x4);

	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;


	return normalize(m3x3 * normal);

	//return normalize(normal);

} //calculateNormal
