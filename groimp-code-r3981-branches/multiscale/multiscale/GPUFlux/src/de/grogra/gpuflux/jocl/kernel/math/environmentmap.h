/*
Copyright (c) 2011 Dietger van Antwerpen (dietger@xs4all.nl)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

#ifndef _ENVIRONMENT_MAP_H
#define _ENVIRONMENT_MAP_H

#include "math/samplepiecewise.h"
#include "math/sample.h"

#define INTERPOLATE_ENVMAP
#define IMPORTANCE_SAMPLE_ENVMAP

typedef struct {
	int width, height;
	float pdfandcdf[1];
} EnvironmentMap;

inline const __global float* GetEnvironmentMapPDF( const __global EnvironmentMap* map )
{
	return map->pdfandcdf;
}

inline const __global float* GetEnvironmentMapCDF( const __global EnvironmentMap* map )
{
	return &map->pdfandcdf[map->width*map->height];
}

/*
pre:
	theta \in [ 0 , PI ]
	phi \in [ -PI , PI ]
post:
	u \in [0,1]
	v \in [0,1]
*/
inline void SphericalToMap(float *u, float *v, float theta, float phi )
{
	// map to 2D distribution
	*u = (theta * F_M_1_PI);
	*v = (phi+F_M_PI) / F_M_2PI;
}

/*
pre:
	u \in [0,1]
	v \in [0,1]
post:
	theta \in [ 0 , PI ]
	phi \in [ -PI , PI ]
*/
inline void MapToSpherical(float *theta, float *phi, float u, float v )
{
	// map to spherical coordinates
	*theta = u * F_M_PI;
	*phi = v * 2 * F_M_PI - F_M_PI;
}

/*
pre:
	|in| = 1
post:
	theta \in [ 0 , PI ]
	phi \in [ -PI , PI ]
*/
inline void CartasianToSpherical(float *theta, float *phi, const Vec3 *in )
{
	// calculate polar coordinates
	*theta = atan2(sqrt(in->x*in->x + in->y*in->y), in->z);
	*phi = atan2(in->y, in->x);
}

inline void SphericalToCartasian( Vec3 *out, float theta, float phi )
{
	// map to cartesian direction
	float sint = sin(theta);
	float cost = cos(theta);
	
	//v3init( out, cos (phi) * sint, sin (phi) * sint, cost );
	v3init( out, cos (phi) * sint, sin (phi) * sint, cost );
}

inline void CartasianToMap(float *u, float *v, const Vec3 *in )
{
	float theta, phi;
	CartasianToSpherical( &theta, &phi, in );
	SphericalToMap( u, v, theta, phi );
}

inline void MapToCartasian(Vec3 *out, float u, float v )
{
	float theta, phi;
	MapToSpherical( &theta, &phi, u, v );
	SphericalToCartasian( out, theta, phi );
}

// get the probability density of some direction on an environment map per solid angle
inline float GetEnvironmentMapDensityFromMap( const __global EnvironmentMap *map, float u, float v )
{
	float result = 0;
	
	const int width = map->width;
	const int height = map->height;
	
	const __global float* pdf = GetEnvironmentMapPDF( map );
	
	// calculate indices into arrays
	const float x = u * width;
	const float y = v * height;
	const int theta0 = max(0, min(width-1, (int) x));
	const int phi0 = max(0, min(height-1, (int) y));
	
	// calculate bilinear interpolated density for given direction
	const float d00 = pdf[phi0 * width + theta0];

#ifdef INTERPOLATE_ENVMAP
	const int theta1 = min(width-1, theta0+1);
	const int phi1 = min(height-1, phi0+1);
	
	const float d10 = pdf[phi0 * width + theta1];
	const float d01 = pdf[phi1 * width + theta0];
	const float d11 = pdf[phi1 * width + theta1];
	result = interpolate(d00, d01, d10, d11, x - floor(x), y - floor(y));
#else
	result = d00;
#endif
	
	return (float) result;
}

// get the probability density of some direction on an environment map per solid angle
inline float GetEnvironmentMapDensityFromSpherical( const __global EnvironmentMap *map, float theta, float phi )
{
	// calculate map coordinates
	float u, v;
	SphericalToMap( &u, &v, theta, phi );
		
	return GetEnvironmentMapDensityFromMap( map, u, v );
}

// get the probability density of some direction on an environment map per solid angle
inline float GetEnvironmentMapDensityFromCartasian( const __global EnvironmentMap *map, const Vec3 *dir )
{
	// calculate spherical coordinates
	float theta, phi;
	CartasianToSpherical( &theta, &phi, dir );
	
	return GetEnvironmentMapDensityFromSpherical( map, theta, phi );
}

// sample a random direction on an environment map in shperical coordinates and return the sampling probability per unit solid angle
inline float SampleEnvironmentMapSpherical( float *out_theta, float *out_phi, const __global EnvironmentMap *map, const float2 *in )
{
	#ifndef IMPORTANCE_SAMPLE_ENVMAP
		float density = SampleSphereSpherical( out_theta, out_phi, (*in).x, (*in).y );

		//Vec3 tmp;
		//SphericalToCartasian( &tmp, *out_theta, *out_phi );
		//CartasianToSpherical( out_theta, out_phi, &tmp );
	
		return density;
	#else
	
		int width = map->width;
		int height = map->height;
		
		int length = width*height;
		
		const __global float* pdf = GetEnvironmentMapPDF( map );
		const __global float* cdf = GetEnvironmentMapCDF( map );
		
		float r = (*in).x;
			
		// sample the piece wise distribution
		int idx = SamplePiecewiseDistributionInterval( cdf, length, r );

		// get the cdf interval
		float low_cdf = idx==0?0:cdf[idx - 1];
		float high_cdf = cdf[idx];
		
		// compute pdf for interval
		float pdf_interval = high_cdf - low_cdf;

		// compute the position within the interval
		float r1 = ((*in).x - low_cdf) / pdf_interval;
		float r2 = (*in).y;
		
		// compute the positions of the interval within the 2d distribution
		int ix = (idx % width);
		int iy = (idx / width);
		
		float x = (ix + r1);
		float y = (iy + r2);
		
		// compute the position within the 2d distribution
		float u = x / (float)width;
		float v = y / (float)height;
		
		// map to spherical coordinates
		float theta, phi;
		MapToSpherical( &theta, &phi, u, v );
		
		// map to cartesian direction
		float sint = sin(theta);
		
		*out_theta = theta;
		*out_phi = phi;
		
		return (length * pdf_interval * invSafe(2*F_M_PI*F_M_PI*sint));
	#endif
}

// sample a random direction on an environment map in cartasian coordinates and return the sampling probability per unit solid angle
inline float SampleEnvironmentMapCartasian( Vec3 *out, const __global EnvironmentMap *map, const float2 *in )
{
	float theta, phi;
	float prb = SampleEnvironmentMapSpherical( &theta, &phi, map, in );
	SphericalToCartasian( out, theta, phi );
	return prb;
}

#endif
