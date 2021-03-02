
vec3 calculateNormal(TraceData td)
{	
	vec3 normal = vec3(0.0);

	if(td.type == SPHERE)
	{ 
		normal = td.iPoint.xyz;
	} 
	
	if(td.type == BOX)
	{
		Box b = getBox();
		
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
			if(td.iPoint.z > 0.9999) normal = vec3(0.0, 0.0, 1.0);
			//bottom
			if(td.iPoint.z < 0.0001) normal = vec3(0.0, 0.0, -1.0);
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
			if(td.iPoint.z > cfc.max - EPSILON)
				normal = vec3(0.0, 0.0, 1.0);
				
			float min = cfc.max > 0.0 ? 1.0 : -1.0;
			
			//bottom
			if(td.iPoint.z < min + EPSILON)
				normal = vec3(0.0, 0.0, -1.0);
		}

		
		normal *= td.invertNormal;
	} //cfc
	
	
	if(td.type == PLANE || td.type == PARA)
	{
		normal = vec3(0.0, 0.0, 1.0);
		
		if(td.type == PARA) normal = vec3(0.0, 1.0, 0.0);
			
		normal *= td.invertNormal;
	}


	if(td.type == TRI)
	{
		int triID 	= td.id;
		vec4 temp0 	= lookUp(triID + 0);
		vec4 temp1 	= lookUp(triID + 1);
		vec4 temp2 	= lookUp(triID + 2);
		vec4 temp3 	= lookUp(triID + 3);
		vec4 temp4 	= lookUp(triID + 4);
		
		vec3 vert0	= temp0.xyz;
		vec3 vert1	= vec3(temp1.zw, temp2.x);
		vec3 vert2	= temp3.xyz;
		
		vec3 vertN0	= vec3(temp0.w, temp1.xy);
		vec3 vertN1	= temp2.yzw;
		vec3 vertN2	= vec3(temp3.w, temp4.xy);
		
		normal = cross(vert2-vert1, vert0-vert1);
	}

		
	mat4 m4x4 	= getInverseMatrix(td.type == TRI ? meshPart : td.id);
	m4x4 		= transpose(m4x4);
	
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
	

	return normalize(m3x3 * normal);
	
} //calculateNormal
