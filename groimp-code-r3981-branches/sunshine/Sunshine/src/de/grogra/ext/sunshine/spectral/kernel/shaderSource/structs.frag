
struct LRay
{
    vec4 origin;
    vec3 direction;
    int typ; // -1 primary ray, > 0 shadow feeler
    float intensity;
};

struct LLight
{
	vec4 origin;
	float intensity;
	float type;
	float power;
	float innerAngle;
	float outerAngle;
	int shadowless;
	int isAlsoLight;
	float distance;
	float exponent;
};

/**
 * This is a container for varius information.
 */
struct Material
{
	int bxdf_type;
	int lambda;
	float v1;
	float v2;
	float urough;
	float vrough;
	float ior;
	float cauchy;
};

/**
 * Shader-Coordinate-System
 */
struct ShaderCoor
{
	vec3 nn;
	vec3 sn;
	vec3 tn;
};

int T_BXDF_DIFFUSE		= 0;
int T_BXDF_REFLECTION	= 1;
int T_BXDF_TRANSMISSION	= 2;

int BXDF_LAMBERTIAN		= 0;
int BXDF_COOKTORRANCE	= 1;
int BXDF_MICROFACET		= 2;
int BXDF_GLASS			= 3;
int BXDF_METAL			= 4;

// Texture for all material data when a single RGBA is not enough
uniform sampler2DRect materialTex;
// Texture with 7 base spectra for convert reflectance from RGB
uniform sampler2DRect colorRGB2SPDRefTex;
// Texture with 7 base spectra for convert illuminance from RGB
uniform sampler2DRect colorRGB2SPDIlluTex;

uniform sampler2DRect tempMatTex0;
uniform sampler2DRect tempMatTex1;

// Counter for referencing the actual entry in colorRGB2SPD_Tex
uniform int colorRGB2SPDRow;
// Counter for referencing the actual row in material texture
uniform int matRowOffset;
// Counter for the actual lambda (wavelength)
uniform int lambda;

uniform float CIE_X;
uniform float CIE_Y;
uniform float CIE_Z;

const float ROUGHNESS_MIN	= 1E-9;
const float ROUGHNESS_SCALE = 0.01;

/*
 * DEFINED ON CPU-SIDE:
 * 	const int lambdaStep		// size of an step from lambda_n-1 to lambda_n
 * 	const int matCols		// number of columns in the material tex.
 */
