
struct Ray
{
    vec4 origin;
    vec3 direction;
    int type; // -1 primary ray, > 0 shadow feeler
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
	float type;
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
	float type;
	float power;
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
	int type;
	bool invertNormal;
};


struct UV
{
	float u;
	float v;
};

int sizes[10];
float shortestDistance;
float rayLength;

//colors
vec3 red 						= vec3(1.0, 0.0, 0.0);
vec3 green 						= vec3(0.0, 1.0, 0.0);
vec3 blue 						= vec3(0.0, 0.0, 1.0);
vec3 yellow 					= red + green;
vec3 black 						= vec3(0.0);
vec3 test 						= black;


//rays
Ray primRay;
Ray secRay 						= Ray(vec4(0.0), vec3(0.0), -1, vec3(1.0));


const int SPHERE 				= 0;
const int BOX 					= 1;
const int CFC 					= 2;
const int PLANE 				= 3;
const int MESH					= 4;
const int PARA					= 5;
const int LIGHT 				= 7;
const int CELL 					= 8;

const int TRI					= 9;

// attached textures
uniform sampler2DRect kd_tree;

uniform int typeOfRay;

uniform int sample;
uniform int recursionDepth;
uniform int lastCycle;
uniform int loopStart;
uniform int loopStop;
uniform int isShadowTest;
uniform int lightPass;

int loopStep 					= loopStop - loopStart;

vec4 normalTest 				= vec4(-0.0);

bool tree 						= false;
float debug 					= -123.0;

const float PI 					= 3.141592654;
const float HPI 				= 1.570796327;
const float _PI 				= 0.3183098862;
const float _PI_ 				= 0.1591549431;

const float LAMBERTIAN_VARIANCE	= (PI * PI - 4.0) / 8.0;
const float MAX_VARIANCE 		= LAMBERTIAN_VARIANCE * 3.0 * 0.8;

const float DEFAULT_SHININESS 	= 4.0;
float MAX_SHININESS 			= 2.0 * PI * 1e10 - 2.0;

const float EPSILON 			= 0.000001;
const float EPSILON_RAY			= 0.001;

const float EULER				= 2.7182818284;
const float INFINITY			= 1e20;

// For debug information
vec3 testVec 					= vec3(0.0);
vec4 check						= vec4(0.0);

const float ZERO 				= -0.0;

//light types
const float POINT_LIGHT	= 0.0;
const float SPOT_LIGHT	= 1.0;
const float AREA_LIGHT	= 2.0;
const float SKY_LIGHT 	= 3.0;

bool isBlack(vec3 color)
{
	//return color.x <= 0.0 && color.y <= 0.0 && color.z <= 0.0;
	return all(lessThan(color, vec3(0.0)));
}

