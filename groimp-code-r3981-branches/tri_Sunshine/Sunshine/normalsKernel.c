#version 120
#extension GL_ARB_texture_rectangle : enable
//#extension GL_ARB_draw_buffers : enable
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
float superSample 		= 1.0;

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

vec3 calculateNormal(TraceData td)
{	
	vec3 normal = vec3(0.0);
	//normal = td.normal;

	if(td.typ == SPHERE)
	{ 
		normal = td.iPoint.xyz;
	} 
	
	if(td.typ == BOX)
	{
		Box b = getBox(td.id);
		
		//front
		if( td.iPoint.y <= b.parameters[1].y - b.deep )
			normal = vec3(0.0, -1.0, 0.0);
		
		//top
		if( td.iPoint.z >= b.parameters[0].z + b.height )
			normal = vec3(0.0, 0.0, 1.0);		
		
		//right
		if( td.iPoint.x >= b.parameters[0].x + b.width )
			normal = vec3(1.0, 0.0, 0.0);
		
		//bottom
		if( td.iPoint.z <= b.parameters[1].z - b.height )
			normal = vec3(0.0, 0.0, -1.0); 
				
		//back
		if( td.iPoint.y >= b.parameters[0].y + b.deep )
			normal = vec3(0.0, 1.0 ,0.0); 
		
		//left
		if( td.iPoint.x <= b.parameters[1].x - b.width )
			normal = vec3(-1.0, 0.0, 0.0);
		
	} //box

	
	if(td.typ == CFC) //CFC
	{
		CylFruCo cfc = getCFC(td.id);
		
		if(cfc.typ == 0.0)
		{
			normal = td.iPoint.xyz - vec3(0.0, 0.0, td.iPoint.z);
			
			//top
			if(td.iPoint.z > 0.9999) normal = vec3(0,0,1.0);
			//bottom
			if(td.iPoint.z < 0.0001) normal = vec3(0,0, -1.0);
		}
		else if(cfc.typ == 1.0)
		{
			normal = vec3(td.iPoint.xy, -td.iPoint.z);
			//bottom
			if(td.iPoint.z < -0.999) normal = vec3(0.0, 0.0, -1.0);
		}
		else if(cfc.typ == 2.0)
		{
			normal = vec3(td.iPoint.xy, -td.iPoint.z);
			
			//top
			if(td.iPoint.z > cfc.max-0.001)
				normal = vec3(0,0, 1.0);
				
			float min = cfc.max > 0.0 ? 1.0 : -1.0;
			
			//bottom
			if(td.iPoint.z < min+0.001)
				normal = vec3(0,0, -1.0);
		}

		
		if(td.invertNormal) normal *= -1.0;
	} //cfc
	
	
	if(td.typ == PLANE || td.typ == PARA)
	{
		normal = vec3(0.0, 0.0, 1.0);
		
		if(td.typ == PARA) normal = vec3(0.0, 1.0, 0.0);
			
		if(td.invertNormal) normal *= -1.0;
	}
	
	mat4 m4x4;
		
	if(td.typ == TRI)
	{
		vec3 vert0 = lookUp(td.id + 0).xyz;
		vec3 vert1 = lookUp(td.id + 1).xyz;
		vec3 vert2 = lookUp(td.id + 2).xyz;
			
		vec3 edge1 = vert1 - vert0;
		vec3 edge2 = vert2 - vert0;
			
		normal = cross(edge1, edge2);
		//normal = vec3(1.0, 1.0, 0.0);
		
		if(td.invertNormal) normal *= -1.0;
		
		m4x4 = getInverseMatrix(meshPart);
		
	} else {
		m4x4 =	getInverseMatrix(td.id);
	}
		
	m4x4 = transpose(m4x4);
	
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
	

	return normalize(m3x3 * normal);
	
	//return normalize(normal);
	
} //calculateNormal
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


void mainOld(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	vec4 oldColor		= texture2DRect(a0, texCoord);
	vec4 origin 		= texture2DRect(a1, texCoord);
	vec4 dir 			= texture2DRect(a2, texCoord);
	vec4 shine			= texture2DRect(a3, texCoord);

	init();
	primRay  = Ray(origin, dir.xyz, -1, shine.xyz);
	random 	 = dir.w;
	
	float variance	= 1.0;
	vec3 currentColor = black;
	
	//currentColor = trace(primRay, variance, TraceData());	
	
	
	gl_FragData[0] = vec4(oldColor.xyz + shine.w*currentColor/superSample, debug);
	gl_FragData[1] = secRay.origin;
	gl_FragData[2] = vec4(secRay.direction, random);
	gl_FragData[3] = vec4(secRay.spectrum, variance);
} //main


void main(void)
{
	vec2 texCoord 		= gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 tmp0			= texture2DRect(a0, texCoord);
	vec4 tmp1			= texture2DRect(a1, texCoord);
	vec4 tmp2			= texture2DRect(a2, texCoord);
	vec4 tmp3			= texture2DRect(a3, texCoord);
	
	init();

	vec4 pixelColor		= tmp0;
	vec4 origin			= vec4(tmp1.xyz, 1.0);
	vec3 iPoint			= tmp2.xyz;
	vec3 spectrum		= tmp3.xyz;
	
	int id				= int(tmp1.w); //tmp3.x < 0 ? -1*int(tmp3.x) : int(tmp3.x);
	int typ				= getType(float(id)); //int(tmp3.y);
	bool invertNormal	= false; // tmp3.x < 0 ? true : false;
	random 	 			= tmp3.w;
	
	primRay  			= Ray(origin, normalize(iPoint.xyz - origin.xyz), -1, spectrum.xyz);
	
	float variance		= 1.0;
	vec3 currentColor 	= black;
	
	TraceData td = TraceData(	id >= 0, 
								vec4(iPoint, 1.0),
								vec3(0.0),
								id,
								typ,
								invertNormal
							);
		

	if(td.hasIntersection)
	{
		td.iPoint		= getInverseMatrix(td.typ != TRI ? float(td.id) : meshPart) * td.iPoint;
		
		td.normal		= calculateNormal(td) ;			
			
	}
	
	//pixelColor			= td.hasIntersection ? vec3(1.0) : vec3(0.0);
	
	// Intersection point with a small distance to the original iPoint						
	iPoint 				= epsilonEnvironment(vec4(iPoint, 1.0), td.normal).xyz;
	
	//gl_FragData[0]		= vec4(pixelColor.xyz, 1.0);
	//gl_FragData[0]		= vec4(pixelColor.xyz, 		td.id);	
		
	gl_FragData[0]		= vec4(td.normal, 		td.id);
	gl_FragData[1]		= vec4(iPoint, 			td.typ);
	gl_FragData[2]		= vec4(0.0);
	gl_FragData[3]		= vec4(0.0);	
		
	/*	
	gl_FragData[0]		= vec4(pixelColor, origin.x);
	gl_FragData[1]		= vec4(origin.yz, iPoint.xy);
	gl_FragData[2]		= vec4(iPoint.z, spectrum);
	gl_FragData[3]		= vec4(id, normStore, random);
	*/
	
} //main

