#version 120
#extension GL_ARB_texture_rectangle : enable
//#extension GL_ARB_draw_buffers : enable
 #define SHADOW_FEELER
uniform sampler2DRect outputImage0;
uniform sampler2DRect outputImage1;
uniform sampler2DRect outputImage2;
uniform sampler2DRect outputImage3;

uniform sampler2DRect a0;
uniform sampler2DRect a1;
uniform sampler2DRect a2;
uniform sampler2DRect a3;
uniform sampler2DRect scene;

uniform sampler2DRect normalTex;

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


const int size 				= 7;
const int sphereCount 		= 0;
const int boxCount 			= 2;
const int cylinderCount 		= 0;
const int planeCount 		= 1;
const int meshCount 			= 0;
const int paraCount 			= 1;
const int lightCount 		= 1;
const int countObjects		= 5;
const float meshPart			= 21.0;
const float lightPart			= 29.0;
const int allTris 	 = 0;
int triCount[1]; 

 void setTriSet(void)
{ 
}

bool accelerate			= false;
float superSample 		= 100.0;

struct Ray 
{	
    vec4 origin;
    vec3 direction;
    int typ; // -1 primary ray, > 0 shadow feeler
    vec3 spectrum;
};


struct Box
{
	vec4 parameters[2];
	float width;
	float deep;
	float height;	
};


struct Sphere
{
	vec4 origin;
	float radius;
};


struct CylFruCo
{
	int isTopOpen;
	int isBaseOpen;
	float max;
	float typ;
};

struct Cone
{
	int isBaseOpen;
	float min;
	float max;
};

struct Frustum
{
	int isTopOpen;
	int isBaseOpen;
	float min;
	float max;
};


struct Cell
{
	int VOLUMELINK;
	int CHILDRENLINK;
};


struct Light
{
	vec4 origin;
	vec4 color;
	float typ;
	float innerAngle;
	float outerAngle;
	int shadowless;
	int isAlsoLight;
	float distance;
	float exponent;
};


struct Phong
{
	vec4 ecm;
	vec4 acm;
	vec4 dcm;
	vec4 scm;
	vec4 srm;
	vec4 trans;
	vec4 diffTrans;
};


struct TraceData
{
	bool hasIntersection;
	vec4 iPoint;
	vec3 normal;
	int id;
	int typ;
	bool invertNormal;
};


struct UV
{
	float u;
	float v;
};

int sizes[10];
float refDeep;
float rayLength;

//colors
vec3 red = vec3(1.0, 0.0, 0.0);
vec3 green = vec3(0.0, 1.0, 0.0);
vec3 blue = vec3(0.0, 0.0, 1.0);
vec3 yellow = red + green;
vec3 black = vec3(0.0);
vec3 test = black;


//rays
Ray primRay;
Ray secRay = Ray(vec4(0.0), vec3(0.0), -1, vec3(1.0));


const int SPHERE 	= 0;
const int BOX 		= 1;
const int CFC 		= 2;
const int PLANE 	= 3;
const int MESH		= 4;
const int PARA		= 5;
const int LIGHT 	= 7;
const int CELL 		= 8;

const int TRI		= 9;

// attached textures
uniform sampler2DRect kd_tree;

uniform int typeOfRay;

uniform int lastCycle;
uniform int loopStart;
uniform int loopStop;
int loopStep = loopStop - loopStart;
uniform int isShadowTest;
uniform int lightPass;

vec4 normalTest;

bool tree = false;
float debug = -123.0;

const float PI 	= 3.141592654;
const float HPI = 1.570796327;

const float LAMBERTIAN_VARIANCE = (PI * PI - 4.0) / 8.0;
const float MAX_VARIANCE = LAMBERTIAN_VARIANCE * 3.0 * 0.8;

float MAX_SHININESS = 2.0 * PI * 1e10 - 2.0;

float EPSILON = 0.0001;

// For debug information
vec3 testVec = vec3(0.0);
vec4 check	= vec4(0.0);
vec4 treeLookUp(int pos)
{
	int s = 100;
	int offset = pos / s;
	pos = int( mod( float(pos), float(s)) );
	
	return texture2DRect(kd_tree, vec2(pos, offset) );
}

vec4 lookUp(float pos)
{
	vec4 result;
	
	//if(!tree)
	//{
		
		int offset = int(pos / size);
		pos 	= pos - size * offset;	// This is better and more numerical stable.
		
		//pos 	= int( mod( float(pos), float(size)) );
		
		result 	= texture2DRect(scene, vec2(pos, offset) );
	

	//testVec = vec3(pos, offset, 3.0);	
		
	//} else
	//{
	//	result = treeLookUp(pos);
	//}
	
	return result;
} //lookUp

vec4 lookUp(int pos)
{
	return lookUp(float(pos));
} //lookUp




Sphere getSphere(int pos)
{	
	return Sphere( vec4(0.0), 1.0);
} //getSphere


Box getBox(int pos)
{
	vec4 parameter[2];
	
	vec4 min = vec4(-0.5, -0.5, 0.0, 1.0);
	vec4 max = vec4(0.5, 0.5, 1.0, 1.0);
	
	parameter[0] = min;
	parameter[1] = max;
	
	return Box( parameter,
			distance(min.x, max.x) - 0.001,
			distance(min.y, max.y) - 0.001,
			distance(min.z, max.z) - 0.001 );
	
} //getBox


CylFruCo getCFC(int pos)
{
	vec4 values = lookUp(pos+7);
	return CylFruCo(int(values.x), int(values.y), values.z, values.w );
}

mat4 getMatrix(float pos)
{	
	mat4 m;
	
	vec4 tmp0 = lookUp(pos+0);
	vec4 tmp1 = lookUp(pos+1);
	vec4 tmp2 = lookUp(pos+2);
	
	m[0] = vec4(tmp0.xyz, 0.0);
	m[1] = vec4(tmp0.w, tmp1.xy, 0.0);
	m[2] = vec4(tmp1.zw, tmp2.x, 0.0);
	m[3] = vec4(tmp2.yzw, 1.0);
	
	return m;
} //getMatrix

mat4 getMatrix(int pos)
{
	return getMatrix(float(pos));
}

mat4 getInverseMatrix(float pos)
{
	return getMatrix(pos+3);
} //getInverseMatrix

mat4 getInverseMatrix(int pos)
{
	return getInverseMatrix(float(pos));
} //getInverseMatrix


//the color is always on the 6th position
vec4 getObjectColor(float pos)
{
	return lookUp(pos+6); 
} //getColor

vec4 getObjectColor(int pos)
{
	return getObjectColor(float(pos));
} //getColor

Light getLight(float pos)
{
	mat4 m = getMatrix(lightPart + pos);
	vec4 p0 = lookUp(lightPart + pos + 7);
	vec4 p1 = lookUp(lightPart + pos + 8);
	
	return Light( m[3], getObjectColor(lightPart + pos), p1.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z );
} //getLight

Light getLight(int pos)
{
	return getLight(float(pos));
} //getLight


//transform the ray into the coord system of the object
Ray getTransformRay(int pos, Ray ray)
{
	mat4 m4x4 = getInverseMatrix(pos);
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
			
	return Ray( m4x4 * ray.origin, m3x3 * ray.direction, -1, ray.spectrum );
} //getTransformRay

int getType(float id)
{
	float part 		= 0;
	float nextPart 	= sphereCount*sizes[SPHERE];
	
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

UV getUVMapping(TraceData td)
{
	UV result;
	bool sm, pm, cm = false;
	pm = true;

	//spherical mapping
	if(sm && (td.typ == BOX || td.typ == CFC) )
		td.iPoint.z -= 0.5;
	
	
	//if(td.typ == SPHERE) // is a sphere -> sphere mapping	
	if(sm)
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );


		
	if( !sm && td.typ == SPHERE)
		td.iPoint.z = (td.iPoint.z + 1.0) / 2.0;
	
	if( pm && td.typ == SPHERE)
	{		
		float u = atan(td.iPoint.y, td.iPoint.x );
		float v = acos( td.iPoint.z );
		float z = td.iPoint.z;
		
		if( u > (0.25*PI) && u < (0.75*PI) &&  z <= 0.9 && z >= 0.1 )
		{
			td.iPoint.x = -0.5;
			
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );
		}
		
		if( u+PI > (0.75*PI) && u+PI < (1.25*PI) && z <= 0.9 && z >= 0.1 )
		{
			td.iPoint.y = 0.5;
			
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );
			
		}

		if( td.iPoint.z > 0.9 )
		{
			td.iPoint.z = 1.0;
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );
			
		}
		
		if( td.iPoint.z < 0.1 )
		{
			td.iPoint.z = 0.0;
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );
		}
		
	}
		
	//if(td.typ == BOX) //is a box
	if(pm && td.typ != SPHERE)
	{
	
		if( td.iPoint.z >= 0.999 ) //top
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );
		
		if( td.iPoint.z <= 0.001 ) //bottom
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );
			
		if( td.iPoint.y <= -0.499 )
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.y >= 0.499 )
			result = UV( abs(td.iPoint.x - 0.5), 1.0-td.iPoint.z );
		
		if( td.iPoint.x >= 0.499 )
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.x <= -0.499 )
			result = UV( abs(td.iPoint.y - 0.5), 1.0-td.iPoint.z );
			
	
	} //if
		
	
	if(td.typ == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );
	
	//if(td.typ == CFC)
	if(cm)
	{
		CylFruCo cfc = getCFC(td.id);
		
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);
		cfc.typ=0.0;
		if(cfc.typ == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		
		if(cfc.typ == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );
		
			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 ); 
		}
		
		if(cfc.typ == 2.0)
		{
			float zmin = cfc.max <= 0.0 ? -1.0 : 1.0;
			if(cfc.max <= 0.0) //zmin = -1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z)*2.0 -1.0 );
				
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < -0.999)
					result = UV( (td.iPoint.x/2.0)+0.5, 1.0-(td.iPoint.y/2.0)+0.5 );
				
			} else //zmin = 1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), (abs(td.iPoint.z)-1.0) / (cfc.max-1.0) );
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < 1.001	)
					result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
			}
		}
	}

	if(td.typ == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );
	
	return result;
}


UV getUV(TraceData td)
{
	UV result = UV(0.0, 0.0);
	
	if(td.typ == SPHERE) // is a sphere -> sphere mapping
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );
	
	if(td.typ == BOX) //is a box
	{
		if( td.iPoint.z >= 0.999 ) //top
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );
		
		if( td.iPoint.z <= 0.001 ) //bottom
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );
		
		if( td.iPoint.y <= -0.499 )
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.y >= 0.499 )
			result = UV( abs(td.iPoint.x - 0.5), 1.0-td.iPoint.z );
		
		if( td.iPoint.x >= 0.499 )
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.x <= -0.499 )
			result = UV( abs(td.iPoint.y - 0.5), 1.0-td.iPoint.z );
	} //if
	
	if(td.typ == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );
	
	if(td.typ == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );
	
	if(td.typ == CFC)
	{
		CylFruCo cfc = getCFC(td.id);
		
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);
		
		if(cfc.typ == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		
		if(cfc.typ == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );
		
			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 ); 
		}
		
		if(cfc.typ == 2.0)
		{
			float zmin = cfc.max <= 0.0 ? -1.0 : 1.0;
			if(cfc.max <= 0.0) //zmin = -1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z)*2.0 -1.0 );
				
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < -0.999)
					result = UV( (td.iPoint.x/2.0)+0.5, 1.0-(td.iPoint.y/2.0)+0.5 );
				
			} else //zmin = 1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), (abs(td.iPoint.z)-1.0) / (cfc.max-1.0) );
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < 1.001	)
					result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
			}
		}
	}
		
	return result;
}


float convertShininess(float x)
{
	float result;
	
	x = x * (2.0 - x);
	if (x <= 0.0)
	{
		result = 0.0;
	}
	else if( x >= 1.0)
	{
		result = MAX_SHININESS;
	}
	else
	{
		result = min(-2.0 / log(x), MAX_SHININESS);
	}
	
	return result;
}


vec3 calcRGB(vec3 color, Light light, vec3 normal, float angle)
{
	return color * light.color.xyz * angle;
}


vec3 calcPhong(Phong p, Light light, vec3 normal, TraceData td, int counter)
{
	vec4 cpri;
	
	vec3 vp = normalize(light.origin.xyz - td.iPoint.xyz);
	vec3 vc = normalize(primRay.origin.xyz - td.iPoint.xyz);
	
	// distance to light source
	float d = length(vp);
	
	float fade_distance = light.distance;
	float fade_power = light.exponent;
	
//	vec4 acs = vec4(0.2, 0.2, 0.2, 1.0); //ambient color of scene
//	vec4 acl = vec4(0.0);				// ambient color of light
//	float att = 1.0 / ( 1.0 + 0.0*length(vp) + 0.0*length(vp)*length(vp) );

	vec4 dcl = light.color; 			//diffuse color of light
	float att = light.origin.w != 0.0 ? 2.0 / (1.0 + pow(d / fade_distance, fade_power)) : 1.0;
	float spot = 1.0;
	
	float f = max(dot(normal, vp), 0.0) != 0.0 ? 1.0 : 0.0;
	
	vec3 h = (vp + vc) / length(vp + vc); //Blinn-Phong-Model: Halfway vector
	vec4 scl = vec4(1.0); //specular intensity of light
	
	if(counter == 0)
		cpri = p.ecm;
	
	cpri += att * spot * ( p.acm
		+ max( dot(normal, vp ), 0.0) * p.dcm * dcl
//		+ f * pow(max(dot(normal, normalize(h) ), 0.0), p.srm.x*128.0) * p.scm * scl );
		+ f * pow(max(dot(normal, normalize(h) ), 0.0), pow(2.0, (p.srm.x-4.0)+(5.0*p.srm.x) ) *128.0) * p.scm * scl );
	
	return cpri.xyz;
}


void getOrthogonalBasis(vec3 vecIn, inout mat3 matOut, bool orthonormal)
{
	float s = 1.0 / sqrt(vecIn.x * vecIn.x + vecIn.y * vecIn.y + vecIn.z* vecIn.z);
	float f;
	
	if (orthonormal)
	{
		matOut[2][0] = vecIn.x * s;
		matOut[2][1] = vecIn.y * s;
		matOut[2][2] = vecIn.z * s;
	}
	else
	{
		matOut[2][0] = vecIn.x;
		matOut[2][1] = vecIn.y;
		matOut[2][2] = vecIn.z;
	}
	
	if( abs(vecIn.x) > abs(vecIn.y) )
	{
		f = 1.0 / sqrt(vecIn.x * vecIn.x + vecIn.z * vecIn.z);
		matOut[0][0] = vecIn.z * f;
		matOut[0][1] = 0.0;
		matOut[0][2] = -vecIn.x * f;
	}
	else
	{
		f = 1.0 / sqrt(vecIn.y * vecIn.y + vecIn.z * vecIn.z);
		matOut[0][0] = 0.0;
		matOut[0][1] = vecIn.z * f;
		matOut[0][2] = -vecIn.y * f;
	}
	
	if (orthonormal)
	{
		matOut[1][0] = matOut[2][1] * matOut[0][2] - matOut[2][2] * matOut[0][1];
		matOut[1][1] = matOut[2][2] * matOut[0][0] - matOut[2][0] * matOut[0][2];
		matOut[1][2] = matOut[2][0] * matOut[0][1] - matOut[2][1] * matOut[0][0];
	} else
	{
		matOut[1][0] = s * (matOut[2][1] * matOut[0][2] - matOut[2][2] * matOut[0][1]);
		matOut[1][1] = s * (matOut[2][2] * matOut[0][0] - matOut[2][0] * matOut[0][2]);
		matOut[1][2] = s * (matOut[2][0] * matOut[0][1] - matOut[2][1] * matOut[0][0]);
	}
}

Phong getPhong(float id, TraceData td)
{
	td.iPoint = getInverseMatrix(td.id) * td.iPoint;
	Phong result = Phong(vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0));

	return result;
}

vec3 calcDirectIllumination(Light light, TraceData td, float angle, vec3 normal, float phong, int counter)
{
	vec3 result;
	if(phong < 0.0)
	{
 		result = calcRGB(getObjectColor(td.id).xyz, light, normal, angle);
	}
	else
	{
		result = calcPhong(getPhong(phong, td), light, normal, td, counter);
	} return result;
}

float fresnel(vec3 normal, inout vec3 inRay, float iorRatio, inout vec3 reflectedOut, inout vec3 transmittedOut)
{
	float result;
	
	transmittedOut = -inRay;
	float cos = dot(normal, inRay);
	reflectedOut = 2.0*cos*normal + transmittedOut;
	int sign;
	if (cos < 0.0)
	{
		cos = -cos;
		sign = -1;
		iorRatio = 1.0 / iorRatio;
	}
	else
	{
		sign = 1;
	}
	
	float t = (1.0 - iorRatio * iorRatio) + (iorRatio * cos) * (iorRatio * cos);
	if (t <= 0.0)
	{
		result = 1.0;
	}
	else
	{
		transmittedOut = -iorRatio * inRay;
		float cost = sqrt(t);
		transmittedOut = float(sign) * (iorRatio * cos - cost) * normal + transmittedOut;
		
		result = ((t = (cost - iorRatio * cos) / (cost + iorRatio * cos)) * t + (t = (cos - iorRatio
				* cost)
				/ (cos + iorRatio * cost))
				* t) * 0.5;
	}
			
	return result;
} //fresnel

float computeEnv(vec3 outRay, inout vec3 normal, inout vec3 rdir, inout vec3 tdir,
		inout Phong p, bool adjoint)
{
	bool interpolatedTransparency = true;
	float iorRatio = 1.0;
	
	float f = p.dcm.w;
	p.trans.x = 1.0 + f * (p.trans.x - 1.0);
	p.trans.y = 1.0 + f * (p.trans.y - 1.0);
	p.trans.z = 1.0 + f * (p.trans.z - 1.0);
	
	if (interpolatedTransparency)
	{
		p.dcm.x *= 1.0 - p.trans.x;
		p.dcm.y *= 1.0 - p.trans.y;
		p.dcm.z *= 1.0 - p.trans.z;
	}
	

	
	p.srm.x = 4.0;//convertShininess( p.srm.x );
	p.srm.y = 0.0;//MAX_SHININESS;
	
	
	float r = fresnel(normal, outRay, iorRatio, rdir, tdir);
	
	if (interpolatedTransparency)
	{
		p.scm.x *= 1.0 - p.trans.x;
		p.scm.y *= 1.0 - p.trans.y;
		p.scm.z *= 1.0 - p.trans.z;
	}
	
	p.scm = r*p.trans + p.scm; //spec.scaleAdd (r, trans, spec);
	
		
	if (adjoint)
	{
		p.trans *= (1.0 - r); //trans.scale (1 - r);
	}
	else
	{
		float i = iorRatio;
		if( dot(normal, outRay) > 0.0)
		{
			i = 1.0 / i;
		}
		p.trans  *= i * i * (1.0 - r); //trans.scale(i * i * (1 - r));
		p.diffTrans *= i*i; //diffTrans.scale (i * i);
	}
	
	return r;	
} //compEnv


float shininessPow(float x, float shininess)
{
	return (x >= 1.0) ? 1.0 : pow(x, shininess);
}

vec3 getDiffuseReflectionRay(vec3 normal)
{		
	float u = LCG(random);
	float v = LCG(random);
	
	float theta = 2.0*PI*u;
	float phi = acos(2.0*v - 1.0);
	float sint = sin(phi);
	
	vec3 s = vec3( cos(theta)*sint, sin(theta)*sint, cos(phi) );
	if( dot(normal, s) < 0.0 )
		s = -s;
	
	return s;
}


float getRandomRgbaRay(vec3 outRay, vec3 spectrum, vec3 normal, vec4 rgba)
{
	float result = 0.0;
	vec3 col;
	float iorRatio = 1.0;
	vec3 rdir;
	vec3 tdir;
	bool adjoint = false;
	
	float r = fresnel( normal, outRay, iorRatio, rdir, tdir );
	
	if( dot(normal, outRay) < 0.0 )
	{
		normal = -normal;
	}
	else
	{
		iorRatio = 1.0 / iorRatio;
	}
	
	// compute the transmission coefficient
	float trans = (1.0 - rgba.w) * (1.0 - r);
	
	col.r = rgba.r;
	col.g = rgba.g;
	col.b = rgba.b;

	// compute the probabilities of diffuse reflection and transmission
	// based on color and trans
	float pd = rgba.x + rgba.y + rgba.z;
	float pt = trans;
	float p = pd + pt;
	bool absorbed = p < pow(10.0, -7.0);
	if (!absorbed)
	{
		p = 1.0 / p;
		pd *= p;
		pt *= p;
		col *= 1.0/pd;
	}
	
	mat3 diffBasis;
	
	float cost, sint;
	
	float z = LCG(random);
	
	float t;
	
	if(absorbed)
	{
		secRay.spectrum = vec3(0.0);
		secRay.direction = vec3(1.0, 0.0, 0.0);
		result = 0.0;
	} else if (z < pt)
	{
		result = pt,
		// this is a transmitted ray
		t = trans / pt;
		secRay.spectrum = t * spectrum;
		secRay.direction = refract(outRay, normal, iorRatio);
		
	} else
	{
		// this is a reflected ray


		getOrthogonalBasis (normal, diffBasis, true);		

		
		float j = LCG(random);
		
		cost = sqrt(j);
		
		sint = sqrt(1.0 - j);
		
		
		
		float u = LCG(random);
		float phi = 2.0*PI*u;
		
		vec3 dir = vec3( cos(phi)*sint, sin(phi)*sint, cost );
		secRay.direction = diffBasis * dir;
//		secRay.direction = getDiffuseReflectionRay(normal);
		secRay.spectrum = spectrum * col;

		result = pd*cost;//(pd * cost)/PI;
	}

	return result;
}

float myDot(vec3 a, vec3 b)
{
	return 0.2989 * a.x * b.x + 0.5866 * a.y * b.y + 0.1145 * a.z * b.z;
}


float getRandomPhongRay(vec3 outRay, vec3 spectrum, vec3 normal, Phong phong)
{
	float result;
	vec3 rdir;
	vec3 tdir;
	computeEnv(outRay, normal, rdir, tdir, phong, false);
	
	float ocos = abs( dot(outRay, normal) );
	vec3 col = spectrum;
	
	float pd 	= myDot(col, phong.dcm.xyz);
	float ps 	= myDot(col, phong.scm.xyz);
	float pdt 	= myDot(col, phong.diffTrans.xyz);
	float pst 	= myDot(col, phong.trans.xyz);
	float p 	= pd + ps + pst + pdt;
	
	if (p < pow(10.0, -7.0))
	{
		p = 0.0;
	}
	else
	{
		p = 1.0 / p;
	}
	
	pd *= p; 	// pd: diffusely reflected fraction
	ps *= p; 	// ps: specularly reflected fraction
	pst *= p; 	// pt: specularly transmitted fraction
	pdt *= p; 	// pt: diffusely transmitted fraction

	// pd, ps, pdt and pst sum up to 1, or all are 0 if ray is completely absorbed
	
	mat3 diffBasis, specBasis, transBasis, m;
	
	// determine randomly if the ray is diffusely or specularly reflected
	// or transmitted (according to probabilities pd, ps, pdt, pst)
	float z = LCG(random);
	// z is uniformly distributed between 0 and 1
	
	float sint, cost;
	if(p == 0.0)
	{
		// completely absorbed
		col = vec3(0.0);
		secRay.direction = vec3(1.0, 0.0, 0.0);
		result = 0.0;
	} else
	{
		bool transmitted = false;
		bool specular = false;
		float shininess = phong.srm.x;
		if(z <= pst)
		{
			// this is a specularly transmitted ray
			transmitted = true;
			specular = true;
			shininess = phong.srm.x;
			
			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of ideal refraction
			getOrthogonalBasis(tdir, transBasis, true);
			m = transBasis;
		}
		else if (z <= pst + ps)
		{
			// this is a specularly reflected ray
			transmitted = false;
			specular = true;
			shininess = phong.srm.x;
			
			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of ideal reflection
			getOrthogonalBasis(rdir, specBasis, true);
			m = specBasis;			
		} 
		else if(z > pst + ps)
		{
			// this is a diffusely transmitted or reflected ray
			transmitted = z <= pst + ps + pdt;
			specular = false;
			shininess = 0.0;			
			
			// compute a local orthogonal basis with its z-axis
			// pointing in the direction of normal			
			getOrthogonalBasis(normal, diffBasis, true);
			m = diffBasis;			
		}
		
		float j = LCG(random);
		float cosr;
		if (specular)
		{
			// choose theta randomly according to the density
			// (n+1) / 2PI * cos(theta)^n 
			cost = pow(j, 1.0 / (shininess + 1.0));
			sint = sqrt(1.0 - cost * cost);
			cosr = cost;
		}
		else
		{
			// choose theta randomly according to the density cos(theta)/PI
			z = j;
			cost = sqrt(z);
			sint = sqrt(1.0 - z);
			if(transmitted)
			{
				cost = -cost;
			}
			cosr = 0.0;
		}
		// choose phi randomly between 0 and 2 PI
		float u = LCG(random);
		float phi = 2.0*PI*u;

		vec3 dir = vec3( cos(phi)*sint, sin(phi)*sint, cost );		
		secRay.direction = m * dir;

		
		cost = dot(secRay.direction, normal);
		if (transmitted)
		{
			cost = -cost;
		}
		if (cost > 0.0)
		{
			// OK, randomly chosen direction points to the side
			// of the normal					
			col = (col/PI) + (transmitted ? phong.diffTrans.xyz : phong.dcm.xyz);

			// z: probability densitity of choosing in as diffusely
			// reflected direction
			z = (transmitted ? pdt : pd) * cost * (1.0/PI);


			if (!specular)
			{
				cosr = dot(secRay.direction, (transmitted ? tdir : rdir) );
			}

			float prob = transmitted ? pst : ps;
			if( (cosr > 0.0) && (prob > 0.0) )
			{
				// angle between in and ideally reflected/refracted direction
				// is less than 90 degress. Thus, this ray could
				// have been chosen as a specularly reflected/refracted ray, too
				cosr = shininessPow(cosr, shininess);
			
	//			col = (shininess + 2.0) * cosr * ( (1.0/(2.0*PI)) / max(cost, ocos) ) * (transmitted ? phong.trans.xyz : phong.scm.xyz) + col;
				
	//			z += prob * (shininess + 1.0) * cosr * (1.0/(2.0*PI));
			}
				
			if(z != 0.0)
			col *= cost/z;
			result = z;
		
		}
		else
		{
			// direction points to the back-side, reset ray.
			// This can only happen for Phong shaders. It reflects
			// the fact that the total reflectivity of Phong's
			// reflection model depends on the angle between out
			// and normal: For non-perpendicular rays, an additional
			// fraction is absorbed.
			col = vec3(0.0);
			result = 0.0;
		}
		
	} //if
	
	secRay.spectrum = spectrum * col;

	return result;
}

float getRandomRay(Ray outRay, vec3 normal, TraceData td)
{
	float var;
	vec4 material = getObjectColor(td.id);
	
	if(material.x == -1.0) //phong
	{
		Phong p = getPhong(material.y, td);
		var = getRandomPhongRay(-outRay.direction, outRay.spectrum, normal, p);
	} else
	{
		var = getRandomRgbaRay(-outRay.direction, outRay.spectrum, normal, material);
	}
	
	return var;
}

float getSecondaryRay(Ray ray, vec3 normal, TraceData td)
{		
	return getRandomRay(ray, normal, td);
}

float calculateAngle(vec3 a, vec3 b)
{	
	return dot( normalize(a), normalize(b) );
} //calculateAngle


//float decay = 1.0;

bool checkConeOfLight(vec4 iPoint, int pos, Light light, out float decay)
{
	bool result = true;
	decay = 1.0;
	
	if(light.typ == 1.0)
	{
		vec4 pnt = getInverseMatrix(lightPart + pos) * iPoint;
		result = pnt.x > light.innerAngle;
		//result = acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle;
	}

	if(light.typ == 2.0)
	{
		vec4 origin = getInverseMatrix(lightPart + pos) * light.origin;
		vec4 pnt 	= getInverseMatrix(lightPart + pos) * iPoint;
		pnt 		= pnt - origin;
		decay 		= calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
		result 		= decay > EPSILON;// && pnt.x >= -1.0 && pnt.x <= 1.0;
	}
	
	return result;
}


vec4 randomOrigin(int pos)
{
	vec4 result;
	vec3 start = vec3(-1.0, 0.0, 0.0); //left edge of parallelogram in <0,0>
	
	float x = LCG(random);
	float z = LCG(random);
	
	start.x += 2.0*x;
	start.z += z;
	result = getMatrix(lightPart + pos*sizes[LIGHT]) * vec4(start, 1.0);
	return result;
}



vec3 shade(Ray ray, inout float var, TraceData td, vec4 lightOrigin)
{	
	vec3 color = black;	
	vec3 normal;
	
	//if(td.hasIntersection)
	//{		
		normal = td.normal;
		
		if(td.typ == TRI)
			td.id 		= int(meshPart);		
		
		
		float angle;
		float AREA_LIGHT = 2.0;


		float phong;// = -1.0;
		Light light;
		
		
		//check used shading
		vec4 c = getObjectColor(td.id);
	
		phong = c.x == -1.0 ? c.y : -1.0;
		
		int i = lightPass;
		//for(int i = 0; i < lightCount; i++)
		//{
			light = getLight(  i*sizes[LIGHT] );		
			
			if(light.typ == AREA_LIGHT)
				light.origin = lightOrigin;
							
			float decay = 1.0;
			
			if( checkConeOfLight(td.iPoint, i*sizes[LIGHT], light, decay) )
			{										
				angle = calculateAngle( light.origin.xyz - td.iPoint.xyz, normal );
								
			//	if( !testShadow(td, light.origin, angle, light.shadowless, light.isAlsoLight) )
			//	{
					color = ray.spectrum*calcDirectIllumination(light, td, max(angle, 0.0), normal, phong, 0 );	
					color *= light.typ == AREA_LIGHT ? decay : 1.0;
			//	} 
				
			} //if
			
		//} //for

	//	if(lastCycle == 1)
			
		
	//} else
	//{
	//	secRay.spectrum = vec3(0.0);
	//}
	
	return color;
} //trace


void init(void)
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[MESH]		= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	
	sizes[TRI]		= 3;

	refDeep = -1.0;
} //init

void main(void)
{
	vec2 xy				= gl_TexCoord[0].xy;
	
	vec4 tmpOI0			= texture2DRect(outputImage0, xy);
	vec4 tmpOI1			= texture2DRect(outputImage1, xy);
	vec4 tmpOI2			= texture2DRect(outputImage2, xy);
	vec4 tmpOI3			= texture2DRect(outputImage3, xy);
	
	vec4 tmp0			= texture2DRect(a0, xy);
	vec4 tmp1			= texture2DRect(a1, xy);
	vec4 tmp2			= texture2DRect(a2, xy);
	vec4 tmp3			= texture2DRect(a3, xy);
	
	init();

	vec4 pixelColor		= tmpOI0;	
	vec4 origin			= vec4(tmpOI1.xyz, 1.0);
	vec3 direction		= tmpOI2.xyz;
	vec3 spectrum		= tmpOI3.xyz;
	

	vec3 iPoint			= tmp0.xyz;
	vec3 normal			= tmp1.xyz;
	vec4 lightOrigin	= vec4(tmp3.xyz, 1.0);
	
	int id				= int(tmp0.w);
	int type			= int(tmp1.w);
	float shine			= !(tmp2.w > EPSILON) ? 1.0 : tmp2.w;
	random 	 			= tmp3.w;
	
	refDeep				= tmp2.x;
	bool inShadow		= tmp2.y > 0.0 ? true : false;
			
	bool invertNormal	= false; // tmp3.x < 0 ? true : false;

	float variance		= 1.0;
	primRay  			= Ray(origin, direction, -1, spectrum);
	

	
	TraceData td = TraceData(	id >= 0, 
								vec4(iPoint, 1.0),
								normal,
								id,
								type,
								invertNormal
							);								
	//pixelColor = vec4(0.0);
	if(td.hasIntersection)	
	{	
	//shine = 1.0;

		if(!inShadow)
			pixelColor += shine * vec4(shade(primRay, variance, td, lightOrigin) / superSample, 1.0);	
		
		//pixelColor += vec4(1.0 / superSample) ;
		//iPoint 			= secRay.origin.xyz;
		
		
		//pixelColor		= vec4(direction, 1.0);
		//pixelColor		= vec4(iPoint.xyz, 1.0);
		
	//	pixelColor 		= vec4(direction, 1.0);
	//	pixelColor 		= vec4(normal, 1.0);
	//	pixelColor		= vec4(iPoint, 1.0);
	
		secRay.origin 	= vec4(iPoint, 1.0);		
	//	td.iPoint		= getMatrix(id) * vec4(iPoint, 1.0);
	

		variance 		= getSecondaryRay(primRay, normal, td);
		
		direction		= normalize(secRay.direction);
	
	//	iPoint = origin.xyz;
	//	pixelColor		= vec4(iPoint, 1.0);
	//	pixelColor 		= vec4(direction, 1.0);
	//	pixelColor 		= vec4(normal, 1.0);
	} 
	else	
	{
		secRay.spectrum = vec3(0.0);	
		direction = vec3(0.0);
		iPoint = vec3(0.0);
	//	pixelColor = vec4(0.0);
	}	

	//pixelColor = vec4(direction, 1.0);;
	
	gl_FragData[0]	= vec4(pixelColor						);
	gl_FragData[1]	= vec4(iPoint			, 	-1			);
	gl_FragData[2]	= vec4(direction,			variance	);
	gl_FragData[3]	= vec4(secRay.spectrum.xyz, random		); 
}