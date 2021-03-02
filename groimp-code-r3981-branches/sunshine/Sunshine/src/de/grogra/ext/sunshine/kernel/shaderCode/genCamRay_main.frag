
vec3 firstRay;
vec3 ray = vec3(0.0);
const float NO_INTERSECTION = 47.11;
const float _PI_ = 0.1591549431; 	// 1/(2PI)
const float DELTA_FACTOR = 1e10;


vec2 getSphericalCoordinates(vec3 dir)
{
	float theta = atan(dir.y, dir.x);
	float phi	= acos(dir.z);
	
	return vec2(theta, phi);
}

void main(void)
{
	// init RNG
	random = texture2DRect(a1, gl_FragCoord.xy).w;

	up	*= texHeight / texWidth;
	dir	*= 2.0; 
	
	// tileRight and tileUp have to be adjust to the size of the tile 	
	float w = texWidth / width;
	float h = texHeight / height;
	vec3 tileRight = (2.0*right) / w;
	vec3 tileUp = (2.0*up) / h;
	
	// dir nach unten links setzen 
	firstRay = dir - right - up;
	
	// je nach Kachel um currentTileX*right weiter rechts anfangen analog mit up verfahren
	firstRay += float(currentTileX)*tileRight + float(currentTileY)*tileUp;
	
	// partitioning the image plane in pixel
	float pixelX = 2.0*(length(right)/texWidth); 	//pixel width
 	float pixelY = 2.0*(length(up)/texHeight);		//pixel height

	
	int qx = int( mod( float(currentSample), float(gridSize) )  );
	int qy = currentSample/gridSize;
	
 	float qw = pixelX / float(gridSize); //quadrant width
 	float qh = pixelY / float(gridSize); //quadrant height
 	
 	
 	float randx = rand();
 	float randy = rand();
 	
 	
	right 	= pixelX*normalize(right); 	//vector with length of a pixel in direction of right 
	up 		= pixelY*normalize(up);		//vector with height of a pixel in direction of up
	ray		+= firstRay;
	ray 	+= (gl_FragCoord.x-0.5)*right 	+ (float(qx)*qw) + (randx*qw);
	ray		+= (gl_FragCoord.y-0.5)*up 		+ (float(qy)*qh) + (randy*qh);
 
	float icos 		= length(ray);
	float dirDens 	= 0.25*icos*icos*icos;
	vec2 angles		= getSphericalCoordinates(normalize(ray));
	
	gl_FragData[0] 	= vec4(rayOrigin, 1.0);
	gl_FragData[1] 	= vec4(angles, dirDens, random);
	gl_FragData[2] 	= vec4(1.0, 1.0, 1.0, NO_INTERSECTION);
	//					dirDensIn | dirDensOut | geomFac	
	gl_FragData[3] 	= vec4(_PI_ / 2.0, -1.0, 1.0, -1.0);
}