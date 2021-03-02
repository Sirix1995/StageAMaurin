
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
const int PARA		= 4;
const int LIGHT 	= 7;
const int CELL 		= 8;

// attached textures
uniform sampler2DRect tex;
uniform sampler2DRect kd_tree;


bool tree = false;

const float PI 	= 3.141592654;
const float HPI = 1.570796327;

const float LAMBERTIAN_VARIANCE = (PI * PI - 4.0) / 8.0;
const float MAX_VARIANCE = LAMBERTIAN_VARIANCE * 3.0 * 0.8;

float MAX_SHININESS = 2.0 * PI * 1e10 - 2.0;