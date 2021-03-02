int getChild(vec3 p_entry)
{
	int qx = p_entry.x < 0.0 ? 0 : 4;
	int qy = p_entry.y > 0.0 ? 2 : 0;
	int qz = p_entry.z < 0.5 ? 0 : 1;
		
	return (qx + qy + qz) * sizes[CELL];
}


TraceData intersectVolumeList(Ray ray, float pos)
{
	TraceData result = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, 1.0);
	bool run = true;
	
	vec4 v = vec4(-1.0);
	int component = int(mod(pos, 16.0) / 4.0);
	

	while(true)
	{		
		v = treeLookUp( int(pos) / 16);
		
		if(component == 0.0)
		{
			if(v[0] < 0.0) break;
			
			intersection(ray, int(v[0]), result);
			component++;
		}
		
		if(component == 1.0)
		{
			if(v[1] < 0.0) break;
			
			intersection(ray, int(v[1]), result);
			component++;
		}
		
		if(component == 2.0)
		{
			if(v[2] < 0.0) break;
			
			intersection(ray, int(v[2]), result);
			component++;
		}		
		
		if(component == 3.0)
		{
			if(v[3] < 0.0) break;
			
			intersection(ray, int(v[3]), result);
			component = 0.0;
		}
		
		component = 0.0;
		pos += 4.0;
	} //while
	
	return result;
}

float getFaceLink(int pos, vec3 point)
{
	float result = -1.0;
	bool ready = false;
	
	
	if(point.x >= 0.4999 && ready == false)
	{
		result = treeLookUp(pos + 7).w; //right
		ready = true;
	}
	
	if(point.x <= -0.4999 && ready == false)
	{
		result = treeLookUp(pos + 7).z; //left
		ready = true;
	}
	
	if(point.y >= 0.4999 && ready == false)
	{
		result = treeLookUp(pos + 6).w; //back
		ready = true;
	}
		
	if(point.y <= -0.4999 && ready == false)
	{
		result = treeLookUp(pos + 6).z; //front
		ready = true;
	}
		
	if(point.z >= 0.999 && ready == false)
	{
		result = treeLookUp(pos + 7).x; //top
		ready = true;
	}
	
	if(point.z <= 0.001 && ready == false)
	{
		result = treeLookUp(pos + 7).y; //bottom
		ready = true;
	}
	

	return result;
}


float exitDistance(Ray ray, int cell)
{
	float delta_exit  = 0.0;
	vec3 foo;
	
	bool hit = RayCellIntersection( ray, cell, delta_exit, foo);
	
	return hit ? delta_exit : -1.0;
//	return delta_exit;
}


vec3 getIntersectionPoint(int cell, float dist, Ray ray)
{
	tree = true;
	Ray tray = getTransformRay(cell, ray);
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


TraceData traverseTree(Ray ray, int index)
{
	TraceData td = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, 1.0);
	vec3 P_entry;
	int i = 0;
	int k = 0;
	float delta_exit = 0.0;
	float root_exit  = 0.0;
	bool hit = false;
	bool rootHit = false;
	

//	Cell index;
	
	
	int currentCell	= index;
	int nextCell;
	vec2 link 	= vec2(0.0);
	
	rootHit = RayCellIntersection(ray, index, root_exit, P_entry);

	
	while(rootHit)
	{
		while(true)
		{
			link = treeLookUp(currentCell + 6).xy;
			if(!isLeaf(link.x)) //isNode
			{
//				hit = RayCellIntersection( ray, currentCell, delta_exit, P_entry );
						
				tree = true;
				mat4 m = getMatrix(currentCell);				
				nextCell = int(link.y) + getChild(P_entry);
				
				P_entry = (getInverseMatrix(nextCell)*(m*vec4(P_entry,1.0))).xyz;
				tree = false;
				
				currentCell = nextCell;
				
			} else
			{
				break;
			} //if
		} //while
				

		// if the leaf is not empty do intersection test 
		if(link.x > 0.0)
		{			
			td = intersectVolumeList(ray, link.x);
			
			if(td.hasIntersection)			
				break;
		}
		

		delta_exit = exitDistance(ray, currentCell);
		nextCell = delta_exit > 0.0 ? exitRope(ray, delta_exit, currentCell) : -1;
		
		
		if(nextCell < 0)// || i == 6)
		{			
			rootHit = false;
			break;
		} 
		else
		{	
			currentCell = nextCell;
			P_entry = getIntersectionPoint(currentCell, delta_exit, ray);			
			i++;
		}
		
//		if(i > 20)
//			debug = delta_exit;
		
			
	} //while	


	//return best found result
	return td;
}