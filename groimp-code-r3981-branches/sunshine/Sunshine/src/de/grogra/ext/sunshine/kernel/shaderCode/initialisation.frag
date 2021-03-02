
void init(float seed)
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[MESH]		= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	sizes[TRI]		= 5;
	
	// init RNG
	random 	 		= seed;
	refDepth 		= -1.0;
	debug 			= 47.11;
} //init
