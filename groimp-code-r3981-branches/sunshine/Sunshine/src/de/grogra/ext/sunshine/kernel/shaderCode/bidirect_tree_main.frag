
TraceData intersectVolumeList(Ray ray, float pos)
{
	TraceData td = nilTD;
	bool run = true;
	
	vec4 idList 	= vec4(-1.0);
	float component = mod(pos, 16.0) / 4.0;
	

	for(int i = 0; i < maxVolumeCount; i++)
	{		
		idList = treeLookUp(pos * 0.0625); // aka 1/16
		
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
		
		pos += 4.0;
		component = 0.0;
	} //while


	return td;
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
	
	
//	if(nextCell.index != nilCell.index)
//	{
//		nextCell = RayCellIntersection(ray, nextCell.index);
//	} //if
	
	
	return nextCell;
} //exitRope


float getNextCellIndex(Ray ray, float cellIndex)
{
	Cell currentCell 	= RayCellIntersection(ray, int(cellIndex));
	Cell nextCell 		= exitRope(currentCell, ray);
	
	return nextCell.index;
} //getNextCellIndex


void trace(Ray ray, inout vec4 state, inout vec2 tdata)
{
	//if loopStop == 1 new ray for intersection test
	if(loopStop == 1)
	{
		tdata = vec2(NO_INTERSECTION);
		// flag setzen dass baum neu getestet wird
		state[1] = 1.0;
	}
	
	secRay = ray;
	
	// beeing inside the tree and intersection test needed
	if(state[0] > 0.0 && state[1] > 0.0)
	{
		TraceData td = intersectVolumeList(ray, state[3]);
	
		if(td.hasIntersection)
		{
			tdata 		= vec2( float(td.id)*td.invertNormal, float(td.type) );
			vec3 normal = calculateNormal(td);
			
			secRay.origin = convertLocal2Global(td.id, td.type, td.iPoint.xyz);			
			setSecRayDirection(ray, normal, td);
			
			state[1] = 0.0;
		} //if
		else
		{
			//get the next cell by following the rope
			state[2] = getNextCellIndex(ray, state[2]);
		}
		
		//to show the boxes of the octree
//		tdata.x = 1.0;
	} //if
} //trace


// non intrusiv
void main(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 origin 	= texture2DRect(a0, texCoord);
	vec4 dir 		= texture2DRect(a1, texCoord);
	vec4 spectrum	= texture2DRect(a2, texCoord);
	vec4 state		= texture2DRect(stateTexture, texCoord);
	
	init(dir.w);
	
	primRay = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, spectrum.rgb);
	
	vec2 tdata = vec2(origin[3], spectrum[3]);
	
	trace(primRay, state, tdata);
	
	
	gl_FragData[0] = vec4(secRay.origin.xyz, tdata[0]);
	gl_FragData[1] = vec4(secRay.direction, random);
	gl_FragData[2] = vec4(secRay.spectrum, tdata[1]);
	gl_FragData[3] = state;
	
} //main

