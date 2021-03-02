int getType(float id)
{
	float part 		= 0;
	float nextPart 	= float(sphereCount * sizes[SPHERE]);
	
	if(id >= part && id < nextPart)
		return SPHERE;
		
	part 		 = nextPart;
	nextPart 	+= boxCount*sizes[BOX];
		
	if(id >= part && id < nextPart)
		return BOX;
		
	part 		 = nextPart;
	nextPart 	+= cylinderCount*sizes[CFC];
		
	if(id >= part && id < nextPart)
		return CFC;
		
	part 		 = nextPart;
	nextPart 	+= planeCount*sizes[PLANE];
		
	check = vec4(id, part, nextPart, 0.0);
	
	if(id >= part && id < nextPart)
		return PLANE;
		
	part 		 = nextPart;
	nextPart 	+= allTris*sizes[TRI] + sizes[MESH];
		
	if(id >= part && id < nextPart)
		return TRI;
		
	part 		 = nextPart;
	nextPart 	+= paraCount*sizes[PARA];
		
	if(id >= part && id < nextPart)
		return PARA;
		
	return LIGHT;
}
