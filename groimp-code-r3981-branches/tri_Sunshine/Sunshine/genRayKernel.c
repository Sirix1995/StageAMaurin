#version 120
#extension GL_ARB_texture_rectangle : enable
//#extension GL_ARB_draw_buffers : enable
 vec3 rayOrigin 	= vec3(-46.456676,25.711262,21.579725);
vec3 up 			= vec3(0.14300662,-0.19418156,1.1292368);
vec3 right 		= vec3(-0.92976874,-0.68473595,2.772271E-15);
vec3 dir 			= vec3(0.57992184,-0.7874469,-0.2088493);
int partsHeight 	= 8;
int partsWidth 	= 11;
int texWidth 		= 675;
int texHeight 		= 449;
int gridSize 		= 10;
int width 			= 64;
int height			= 64;

float random;

float getSeed()
{
	return mod(dot(gl_FragCoord.xy, vec2(17.0, 31.0)), 65537.0);
//	return mod(dot(gl_FragCoord.xy, vec2(17.0, 31.0)) / 65537.0, 1.0);
//	return 1.0 / 65537.0;
//	return 3.0;
} 
		
// Park & Miller RNG: x(n+1) = x(x) * g mod n, n=2^16+1, g=75
float rand()
{
	random = mod(random * 75.0, 65537.0);
	return random / 65537.0;
}



// TODO: get rid of this LCG function and use rand() in the whole code instead !!
float LCG(float y1)
{
	return rand();
}

  /**
  	* To safe space during storing, we reduce the 3-comp. vector
  	* to a 2-comp. vector. But this method only works with a
  	* normalized vector.
  	*/
vec2 reduceNormalizedVector(vec3 v)
{
	if(v.z < 0.0)	
		v.x		+= v.x < 0.0 ? -2.0 : 2.0;
		
	return vec2(v.x, v.y);
}

  /**
	* A stored vector which is normalized only needs two values.
	* The third can be constructed based on the first two components.
	*/	
vec3 rebuildNormalizedVector(vec2 v)
{
	// Considering, that a normal vector has a length by 1
	float x = v.x;
	float y = v.y;

	float flipZ = 1;
	
	if(x > 2.0)
	{
		flipZ = -1;
		x-=2.0;
	} else if(x < -2.0)
	{
		flipZ = -1;
		x+=2.0;
	}
	
	float z = sqrt(1.0 - ((x * x) + (y * y)) );	
	
	return vec3(x, y, flipZ * z);
}
uniform sampler2DRect outputImage0;
uniform sampler2DRect outputImage3;
uniform int px;
uniform int py;
uniform int sample;

vec3 firstRay;
vec3 ray = vec3(0.0);

void main(void)
{
	// init RNG
	vec4 pixelColor = texture2DRect(outputImage0, gl_FragCoord.xy);
	random 			= texture2DRect(outputImage3, gl_FragCoord.xy).w;

	//size of tiles is set outside of main
	//int width 	= 256;
	//int height 	= 256;

	up	*= float(texHeight) / float(texWidth);
	dir	*= 2.0; 
	
	// tileRight and tileUp have to be adjust to the size of the tile 	
	float w = float(texWidth) / float(width);
	float h = float(texHeight) / float(height);
	vec3 tileRight = (2.0*right) / w;
	vec3 tileUp = (2.0*up) / h;
	
	// dir nach unten links setzen 
	firstRay = dir - right - up;
	
	// je nach Kachel um px*right weiter rechts anfangen analog mit up verfahren
	firstRay = firstRay + px*tileRight + py*tileUp;
		 
	// partitioning the image plane in pixel
	float pixelX = 2.0*(length(right)/texWidth); 	//pixel width
 	float pixelY = 2.0*(length(up)/texHeight);		//pixel height

	
	int qx = int( mod( float(sample), float(gridSize) )  );
	int qy = sample/gridSize;
	
 	float qw = pixelX / float(gridSize); //quadrant width
 	float qh = pixelY / float(gridSize); //quadrant height
 	
 	//random number in [0,1]
  	float m 	= pow(2.0, 32.0) - 1.0; //2147483647.0;
 	
 	float randx 	= rand();
 	float randy 	= rand(); 	
 	
	right 	= normalize(right);
	up 		= normalize(up);
	right 	= pixelX*right; //vector with length of a pixel in direction of right 
	up 		= pixelY*up;	//vector with height of a pixel in direction of up
	ray		+= firstRay;
	ray 	+= (gl_FragCoord.x-0.5)*right + (qx*qw) + (randx*qw);
	ray		+= (gl_FragCoord.y-0.5)*up + (qy*qh) + (randy*qh);	

	// Later we need only a certain point where the ray points and not the direction.
	vec3 point			= rayOrigin + normalize(ray);	
	
	//copy a0 to b0		
	
	//pixelColor = vec4(normalize(ray), 1.0);
	
	gl_FragData[0]	= vec4(pixelColor				); 	//outputImage0
	gl_FragData[1]	= vec4(rayOrigin, 		-1		);	//outputImage1
	gl_FragData[2]	= vec4(normalize(ray),	-1.0	);	//outputImage2
	gl_FragData[3]	= vec4(vec3(1.0),		 random	); 	//outputImage3	
	
	/*
	gl_FragData[0]		= pixelColor;
	gl_FragData[1]		= vec4(rayOrigin, 	-1.0	);
	gl_FragData[2]		= vec4(point, 		-1.0	);
	gl_FragData[3]		= vec4(vec3(1.0), 	random	); */

}