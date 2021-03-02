uniform sampler2DRect outputImage0;
uniform sampler2DRect outputImage2;
uniform int px;
uniform int py;
uniform int sample;

vec3 firstRay;
vec3 ray = vec3(0.0);

void main(void)
{
	// init RNG
	vec4 pixelColor = texture2DRect(outputImage0, gl_FragCoord.xy);
	random 			= texture2DRect(outputImage2, gl_FragCoord.xy).w;

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
	
	//pixelColor = vec4(random);
	
	gl_FragData[0]	= vec4(pixelColor				); 	//outputImage0
	gl_FragData[1]	= vec4(rayOrigin, 		-1		);	//outputImage1
	gl_FragData[2]	= vec4(normalize(ray),	random	);	//outputImage2
	gl_FragData[3]	= vec4(vec3(1.0),		1.0		); 	//outputImage3	
	
	/*
	gl_FragData[0]		= pixelColor;
	gl_FragData[1]		= vec4(rayOrigin, 	-1.0	);
	gl_FragData[2]		= vec4(point, 		-1.0	);
	gl_FragData[3]		= vec4(vec3(1.0), 		); */

}