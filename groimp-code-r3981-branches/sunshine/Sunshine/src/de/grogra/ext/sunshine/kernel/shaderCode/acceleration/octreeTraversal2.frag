

void fillCell(inout Cell cell)
{
	vec4 pixel6 = treeLookUp(cell.index + 6);
	
	cell.isLeaf 		= pixel6[0] >= -1.0;
	cell.isFilled		= pixel6[0] > 0.0;
	cell.volumeLink		= int(pixel6[0]);
	cell.childrenLink 	= int(pixel6[1]);
}

Cell getChild(Cell cell, Ray ray)
{
	int qx = cell.p_entry.x < 0.0 ? 0 : 4;
	int qy = cell.p_entry.y > 0.0 ? 2 : 0;
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


TraceData intersectVolumeList(Ray ray, int pos)
{
	TraceData td = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, 1.0);
	bool run = true;
	
	vec4 idList 	= vec4(-1.0);
	float component = mod(float(pos), 16.0) / 4.0;
	

	for(int i = 0; i < maxVolumeCount; i++)
	{		
		idList = treeLookUp( pos * 0.0625 ); // aka 1/16
		
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
		
		pos += 4;
		component = 0.0;
	} //while


	return td;
}


TraceData traverseTree(Ray ray, int index, TraceData lastStateData)
{
	Cell child;
	TraceData td = nilTD;
	
	// first: test intersection with the root node
	Cell currentCell = RayCellIntersection(ray, index);
	
	int cells = int( pow(4.0, float(treeDepth)) );
	
	float delta_entry 	= currentCell.delta_entry;
	float delta_exit 	= currentCell.delta_exit;
	
	if(delta_entry < delta_exit)
	for(int i = 0; i < cells; i++)
	{
		fillCell(currentCell);
		
//		while(!currentCell.isLeaf) //isNode
		for(int k = 0; k < treeDepth; k++)
		{
			if(!currentCell.isLeaf) //isNode
			{
				child = getChild(currentCell, ray);
				
				currentCell = child;
				
				if(currentCell.index == nilCell.index) break;
			} //while		
		}
		
		
		if(currentCell.index == nilCell.index) break; 
		
		if(currentCell.isFilled)
		{
			td = intersectVolumeList(ray, currentCell.volumeLink);

			if(td.hasIntersection)
			{
				break;
				vec4 iPoint = convertLocal2Global(td.id, td.type, td.iPoint.xyz);
				
				// update the current best result
				delta_exit = distance(ray.origin.xyz, iPoint.xyz);
			}
		} //if
		
		
		delta_entry 	= currentCell.delta_exit;
		Cell nextCell 	= exitRope(currentCell, ray);
		
		if(nextCell.index != nilCell.index)
		{
			currentCell = nextCell;
		}
		else
		{
			break;
		}
	} //while
	
	
	//return best found result
	return td;
}