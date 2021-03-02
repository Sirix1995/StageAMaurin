


void fillCell(inout Cell cell)
{
	vec4 pixel6 = treeLookUp(cell.index + 6);
	
	cell.isLeaf 		= pixel6[0] >= -1.0;
	cell.isFilled		= pixel6[0] > 0.0;
	cell.volumeLink		= pixel6[0];
	cell.childrenLink 	= int(pixel6[1]);
}

Cell getChild(Cell cell, Ray ray)
{
	int qx = cell.p_entry.x < 0.0 ? 0 : 4;
	int qy = cell.p_entry.y < 0.0 ? 0 : 2;
	int qz = cell.p_entry.z < 0.5 ? 0 : 1;
	
	int index = cell.childrenLink + (qx + qy + qz) * sizes[CELL];
	
	Cell child = RayCellIntersection(ray, index);
	
	if(child.index != nilCell.index)
	{
		fillCell(child);
	}
	
	return child;
}


Cell exitRope(Cell cell, Ray ray)
{
	Cell nextCell = nilCell;
	bool ready = false;

	
	if(cell.p_exit.x >= 0.4999)
	{
		nextCell.index = int(treeLookUp(cell.index + 7)[3]); //right
		ready = true;
	}
	
	else if(cell.p_exit.x <= -0.4999)
	{
		nextCell.index = int(treeLookUp(cell.index + 7)[2]); //left
		ready = true;
	}
	
	else if(cell.p_exit.y >= 0.4999)
	{
		nextCell.index = int(treeLookUp(cell.index + 6)[3]); //back
		ready = true;
	}
	
	else if(cell.p_exit.y <= -0.4999)
	{
		nextCell.index = int(treeLookUp(cell.index + 6)[2]); //front
		ready = true;
	}
	
	else if(cell.p_exit.z >= 0.9999)
	{
		nextCell.index = int(treeLookUp(cell.index + 7)[0]); //top
		ready = true;
	}
	
	else if(cell.p_exit.z <= 0.0001)
	{
		nextCell.index = int(treeLookUp(cell.index + 7)[1]); //bottom
		ready = true;
	}
	
	
	if(nextCell.index != nilCell.index)
	{
		nextCell = RayCellIntersection(ray, nextCell.index);
	} //if
	
	
	return nextCell;
} //exitRope


TraceData intersectVolumeList(Ray ray, float link)
{
	TraceData td = nilTD;
	
	vec4 idList 	= vec4(-1.0);
	float component = mod(link, 16.0) / 4.0;
	

	for(int i = 0; i < maxVolumeCount; i++)
	{		
		idList = treeLookUp(link * 0.0625); // aka 1/16
		
		if(component == 0.0)
		{
			if(idList[0] < 0.0) break;
			
			intersection(ray, int(idList[0]), td);
			component++;
		}
		
		if(component == 1.0)
		{
			if(idList[1] < 0.0) break;
			
			intersection(ray, int(idList[1]), td);
			component++;
		}
		
		if(component == 2.0)
		{
			if(idList[2] < 0.0) break;
			
			intersection(ray, int(idList[2]), td);
			component++;
		}		
		
		if(component == 3.0)
		{
			if(idList[3] < 0.0) break;
			
			intersection(ray, int(idList[3]), td);
			component = 0.0;
		}
		
		link += 4.0;
		component = 0.0;
	} //for


	return td;
}


void traverseTree(Ray ray, inout float index, inout vec2 traceState)
{
	if(loopStart == 0)
	{
		traceState = vec2(NO_INTERSECTION);
	}
	
	if(traceState[0] == NO_INTERSECTION && int(index) != nilCell.index)
	{
		TraceData td = nilTD;
		
		Cell currentCell = RayCellIntersection(ray, int(index));
		
		if(currentCell.delta_entry != currentCell.delta_exit)
		{
			int cells 	= int( pow(4.0, float(treeDepth)) );
			int start 	= 0;
			int stop 	= 0;
			
			setLoopParam(cells, start, stop);
			
			for(int i = start; i < stop; i++)
			{
				fillCell(currentCell);
				
				for(int k = 0; k < treeDepth; k++)
				{
					if(!currentCell.isLeaf) //isNode
					{
						currentCell = getChild(currentCell, ray);
						
						if(currentCell.index == nilCell.index) break;
					} //if	
				} //for treeDepth
				
				
				if(currentCell.index == nilCell.index) break; 
				
				if(currentCell.isFilled)
				{
					td = intersectVolumeList(ray, currentCell.volumeLink);
		
					if(td.hasIntersection)
					{
						index 		= float(currentCell.index);
						
						traceState 	= vec2( float(td.id)*td.invertNormal, float(td.type) );
						vec3 normal = calculateNormal(td);
						
						secRay.origin = convertLocal2Global(td.id, td.type, td.iPoint.xyz);			
						setSecRayDirection(ray, normal, td);
						
						break;
					} //if
				} //if
				
				
				Cell nextCell = exitRope(currentCell, ray);
				index = float(nextCell.index);
				
				if(nextCell.index != nilCell.index)
				{
					currentCell = nextCell;
				}
				else
				{
					break;
				}
			} //for cells
		} //if

	} //if
} //traverseTree


void trace(Ray ray, inout vec4 treeState, inout vec2 traceState)
{	
	secRay = ray;
	ray.origin = epsilonEnvironment(ray.origin, ray.direction);
	
	traverseTree(ray, treeState[2], traceState);
} //trace


// intrusiv
void main(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 origin 	= texture2DRect(a0, texCoord);
	vec4 dir 		= texture2DRect(a1, texCoord);
	vec4 spectrum	= texture2DRect(a2, texCoord);
	vec4 treeState	= texture2DRect(stateTexture, texCoord);
	
	init(dir.w);
	primRay = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, spectrum.rgb);
	
	vec2 tdata = vec2(origin.w, spectrum.w);
	trace(primRay, treeState, tdata);
	
	gl_FragData[0] = vec4(secRay.origin.xyz, tdata[0]);
	gl_FragData[1] = vec4(secRay.direction, random);
	gl_FragData[2] = vec4(secRay.spectrum, tdata[1]);
	gl_FragData[3] = treeState;
	
} //main

