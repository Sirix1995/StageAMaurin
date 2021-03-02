uniform sampler2DRect matchTable;
uniform int tileX;

int lamdaMin 			= 360;					// D65: 300	// RGB: 390	// XYZ: 360
int lamdaMax 			= 830;			
int lamdaDelta 			= lamdaMax - lamdaMin; 	// D65: 530	// RGB: 440 // XYZ: 470
int lamdaSteps			= 1;

int colorMatchTexSize 	= 39;					// D65:	42	// RGB: 18	// XYZ: 39
int coloMatchVaules		= lamdaDelta * 3; 					// RGB: 267 // XYZ: 1413 

vec3 getXYZMatch(int lamda)
{
	if(lamda < lamdaMin)
		lamda = lamdaMin;
		
	if(lamda > lamdaMax)	
		lamda = lamdaMax;
	
	int pos 		= int((lamda - lamdaMin) / float(lamdaSteps));	

	float line 		= int(float(pos) / float(colorMatchTexSize));
	float linePos 	= float(pos) - float(colorMatchTexSize * line);	
		
	return texture2DRect(matchTable, vec2(linePos, line) ).xyz;
}

vec3 getMatchFunc(int lamda)
{		
	vec3 XYZ 		= getXYZMatch(lamda);
	
	//return vec3(xyz);
	
	//return xyz;
	
	//float k = 100.0 / xyz.y;
	
	/*float x = XYZ.x / (XYZ.x + XYZ.y + XYZ.z);
	float y = XYZ.y / (XYZ.x + XYZ.y + XYZ.z);
   	float z = XYZ.z / (XYZ.x + XYZ.y + XYZ.z);
    
   vec3 xyz = vec3(x, y, z);*/
   
   vec3 xyz = XYZ;
   
   //xyz *= k;
	
	mat3 m;
	vec3 rgb = vec3(0.0);
	
	//xyz *= 1e-2;
	
	//sRGB
	m[0] = vec3(3.2410, -0.9692, 0.0556);
	m[1] = vec3(-1.5374, 1.8760, -0.2040);
	m[2] = vec3(-0.4986, 0.0416, 1.0570);
	
	// Wide Garmut
	/*m[0] = vec3(1.46281, -0.521793, 0.0349342); 
	m[1] = vec3(-0.184062, 1.44724, -0.0968931);  
	m[2] = vec3(-0.274361, 0.0677228, 1.28841); */
	
/*	 2.04148    -0.969258    0.0134455  
-0.564977    1.87599    -0.118373   
-0.344713    0.0415557   1.01527 */

	//Pal
 	/*m[0] = vec3(3.06313,    -0.969258,    0.0678674 );
	m[1] = vec3(-1.39328,     1.87599,    -0.228821   );
	m[2] = vec3(-0.475788,    0.0415557,   1.06919);*/
	
	m = transpose(m);
	
	rgb.x = m[0].x * xyz.x + m[0].y * xyz.y + m[0].z * xyz.z;
	rgb.y = m[1].x * xyz.x + m[1].y * xyz.y + m[1].z * xyz.z;
	rgb.z = m[2].x * xyz.x + m[2].y * xyz.y + m[2].z * xyz.z;
	
		
	//rgb = xyz;
	//rgb *= 1e6;	
	float minR 	= min(0, rgb.x);
	float minG 	= min(0, rgb.y);
	float minB 	= min(0, rgb.z);
	
	float minV 	= min(minR, minG);
	minV 		= -min(minV, minB);	
		
	if(minV > 0.0)
		rgb += minV;
	
	rgb.x = rgb.x > 1.0 ? 1.0 : rgb.x;
	rgb.y = rgb.y > 1.0 ? 1.0 : rgb.y;
	rgb.z = rgb.z > 1.0 ? 1.0 : rgb.z;

	
/*	vec3 xWeight = vec3(0.412453f, 0.357580f, 0.180423f);
	vec3 yWeight = vec3(0.212671f, 0.715160f, 0.072169f);
	vec3 zWeight = vec3(0.019334f, 0.119193f, 0.950227f);
*/
	return rgb;
	
	//return vec3(1.0, 0.0, 1.7975241 / 6.407417);
	//return vec3(6.407417E-11, 0.0, 1.7975241E-11);
	
	//return vec3(0.2989f * rgb.x, 0.5866f * rgb.y, 0.1145f * rgb.z);
	
	//return vec3(linePos, lamda, pos);
	
}

void main()
{
	vec2 texCoor	= gl_TexCoord[0].xy;
	float xPos		= float(tileX * tileWidth) + texCoor.x; 
	float relPos	= xPos / float(imageWidth);
	
	int mPos		= int( int(relPos  * (float(lamdaDelta))) + lamdaMin);
	
	//gl_FragData[0] 	= vec4(xPos); 
	//gl_FragData[0] 	= vec4(mPos); 
	gl_FragData[0] 	= vec4( getMatchFunc(mPos), 1.0 ); 
}