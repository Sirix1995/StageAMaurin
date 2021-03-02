
struct Ray 
{	
    vec4 origin;
    vec3 direction;
    int type; // -1 primary ray, > 0 shadow feeler
    vec3 spectrum;
    float density;
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
	float invertNormal;
};


struct UV
{
	float u;
	float v;
};


struct State
{
	vec3 iPoint;
	float id;
	float type;
	float depth;
};

struct VertexInfos
{
	float dirDensIn;
	float dirDensOut;
	float geomFac;
};

struct Cell
{
	int index;
	bool isLeaf;
	bool isFilled;
	vec3 p_entry; // in local coordinate system
	vec3 p_exit;  // in local coordinate system
	float delta_entry;
	float delta_exit;
	int childrenLink;
	float volumeLink;
};

struct Direction
{
	vec3 dir;
	float density;
};


int sizes[10];
float refDepth;

//colors
const vec3 red = vec3(1.0, 0.0, 0.0);
const vec3 green = vec3(0.0, 1.0, 0.0);
const vec3 blue = vec3(0.0, 0.0, 1.0);
const vec3 yellow = red + green;
const vec3 black = vec3(0.0);
vec4 test = vec4(0.0);


//rays
Ray primRay;
Ray secRay;


//td.type
const int SPHERE 	= 0;
const int BOX 		= 1;
const int CFC 		= 2;
const int PLANE 	= 3;
const int MESH		= 4;
const int PARA		= 5;
const int LIGHT 	= 7;
const int CELL 		= 8;
const int TRI		= 9;

int loopStep;

bool tree = false;
uniform sampler2DRect treeTexture;

float debug;

//light types
const float PONT_LIGHT = 0.0;
const float SPOT_LIGHT = 1.0;
const float AREA_LIGHT = 2.0;

const float EPSILON = 0.001;
const float PI 		= 3.141592654;		// PI
const float HPI 	= 1.570796327; 		// PI/2
const float _PI 	= 0.3183098862; 	// 1/PI
const float _PI_	= 0.1591549431; 	// 1/(2PI)

const float NO_INTERSECTION = 47.11;

const int BEGIN2END = 0;
const int END2BEGIN = 1;

const float DELTA_FACTOR = 1e10;

const float LAMBERTIAN_VARIANCE = (PI * PI - 4.0) / 8.0;
const float MAX_VARIANCE = LAMBERTIAN_VARIANCE * 3.0 * 0.8;

const float DEFAULT_SHININESS = 4.0;
const float MAX_SHININESS = 2.0 * PI * DELTA_FACTOR - 2.0;

const Cell nilCell 		= Cell(-1, false, false, vec3(0.0), vec3(0.0), 0.0, 0.0, 0, 0.0);
const TraceData nilTD 	= TraceData(false, vec4(NO_INTERSECTION), vec3(0.0),-1,-1, 1.0);