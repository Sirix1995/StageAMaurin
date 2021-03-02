

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


void traverseTree(Ray ray, inout Cell currentCell, vec2 state)
{
	Cell child;
	
	if(state[0] > 0.0 && state[1] > 0.0)
	{
		currentCell = RayCellIntersection(ray, currentCell.index);
		
		fillCell(currentCell);
		
		for(int k = 0; k < treeDepth; k++)
		{
			if(!currentCell.isLeaf) //isNode
			{
				child = getChild(currentCell, ray);
				
				currentCell = child;
				
				// is this really necessary?
				if(currentCell.index == nilCell.index) break;
			} //if		
		} //for treeDepth
	} //if
} //traverseTree


void main(void)
{
	vec4 origin 	= texture2DRect(a0, gl_FragCoord.xy);
	vec4 dir 		= texture2DRect(a1, gl_FragCoord.xy);

	vec4 state		= texture2DRect(stateTexture, gl_FragCoord.xy);
	
	init(dir.w);
	Ray ray = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, vec3(1.0), 1.0);
	
	Cell cell 	= nilCell;
	cell.index 	= int(state[2]);

	traverseTree(ray, cell, state.xy);

	
	gl_FragData[0] = vec4(state[0], state[1], cell.index, cell.volumeLink);
} //main


