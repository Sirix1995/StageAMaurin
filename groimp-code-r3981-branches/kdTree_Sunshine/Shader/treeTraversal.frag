
int getChild(vec3 p_entry)
{
	int qx = p_entry.x < 0.0 ? 0 : 4;
	int qy = p_entry.y > 0.0 ? 2 : 0;
	int qz = p_entry.z < 0.5 ? 0 : 1;
		
	return (qx + qy + qz) * sizes[CELL];
}


TraceData intersectVolumeList(Ray ray, float pos)
{
	TraceData result = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);
	bool run = true;
	
	vec4 v;
	float f = mod(pos, 16.0) / 16.0;

	v = treeLookUp( int(pos) / 16);
	tree = false;
	
//	intersection( ray, 42, result );
//	run = false;
	
	while(run)
	{		
		v = treeLookUp( int(pos) / 16);
		if( f == 0.0 && v.x != -1.0)
		{
			intersection( ray, int(v.x), result );
			f = 0.25;
			run = true;
		} 
		else if(v.x == -1.0) 
			run = false;
		
		
		if( f == 0.25 && v.y != -1.0)
		{
			intersection( ray, int(v.y), result );
			f = 0.5;
			run = true;
		}
		else if(v.y == -1.0) 
			run = false;
		
		if( f == 0.5 && v.z != -1.0)
		{
			intersection( ray, int(v.z), result );
			f = 0.75;
			run = true;
		}		
		else if(v.z == -1.0) 
			run = false;
		
		if( f == 0.75 && v.w != -1.0)
		{
			intersection( ray, int(v.w), result );
			f = 0.0;
			run = true;
		}
		else if(v.w == -1.0) 
			run = false;
		
		
		f = 0.0;
		pos += 4.0;
	} //while
	
	return result;
}

float getFaceLink(int pos, vec3 point)
{
	float result = -1.0;
		
	
	if(point.x >= 0.4999)
		result = treeLookUp(pos + 7).w; //right
	
	if(point.x <= -0.4999)
		result = treeLookUp(pos + 7).z; //left
	
	if(point.y >= 0.4999)
		result = treeLookUp(pos + 6).w; //back
		
	if(point.y <= -0.4999)
		result = treeLookUp(pos + 6).z; //front
		
	if(point.z >= 0.999)
		result = treeLookUp(pos + 7).x; //top
	
	if(point.z <= 0.001)
		result = treeLookUp(pos + 7).y; //bottom
	
	
	return result;
}


float exitDistance(Ray ray, int cell)
{
	float delta_exit  = 0.0;
	vec3 foo;
	
	bool hit = RayCellIntersection( ray, cell, delta_exit, foo);
	
//	return hit ? delta_exit : 0.0;
	return delta_exit;
}


vec3 getIntersectionPoint(int cell, float dist, Ray ray)
{
	tree = true;
	Ray tray = getTransformRay( cell, ray );
	tree = false;
	
	
	return tray.origin.xyz + (dist * normalize(tray.direction));
}


int exitRope(Ray ray, float delta_exit, int cell)
{
	return int(getFaceLink(cell, getIntersectionPoint(cell, delta_exit, ray)));
}


bool isLeaf(float link)
{
	return link >= -1.0;
}


TraceData traverseTree(Ray ray, int root)
{
	TraceData td = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);
	vec3 P_entry;
	int i = 0;
	float delta_exit  = 0.0;
	float root_exit  = 0.0;
	bool hit = false;
	bool rootHit = false;
	
	
	int cell 	= root;
	vec2 link 	= vec2(0.0);
	
	rootHit 	= RayCellIntersection( ray, root, root_exit, P_entry );


	while(rootHit)
	{
		while(true)
		{
			link = treeLookUp(cell + 6).xy;
			if( !isLeaf(link.x) ) //isNode
			{
//				hit = RayCellIntersection( ray, cell, delta_exit, P_entry );
						
				tree = true;
				mat4 m = getMatrix(cell);				
				cell = (int(link.y) / 16) + getChild(P_entry);
				
				P_entry = ( getInverseMatrix(cell)*(m*vec4(P_entry,1.0)) ).xyz;
				tree = false;
			} else
			{
				break;
			} //if
		} //while
				

		// if the leaf is not empty do intersection test
		if( link.x > 0.0 )
		{			
			test = red;
			td = intersectVolumeList(ray, link.x );
					
			
			if(td.hasIntersection)
				break;
		}
				
		delta_exit = exitDistance(ray, cell);
		cell = exitRope(ray, delta_exit, cell);
		
		if(cell < 0)// || i == 6)
		{
			break;
		}
				
		P_entry = getIntersectionPoint(cell, delta_exit, ray);
		i++;	
	} //while	
	

	//return best found result
	return td;
}



