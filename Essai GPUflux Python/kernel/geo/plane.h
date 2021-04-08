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

#ifndef _PLANE_H
#define _PLANE_H

#include "geo/prim.h"

typedef struct
{
	Prim base;
}Plane;

bool computePlaneIntersect( Intc *intc, const Ray *r, const __global Plane *plane )
{ 
	if (r->d.z != 0)
	{
		intc->t = -r->o.z / r->d.z;
		return true;
	}
	return false; 
}

void computePlaneNormalUV( Vec3 *norm, Vec2 *uv, const Intc *intc, const Vec3 *intp, const __global Plane *plane )
{
	v2init( uv, intp->x, intp->y );
	v3init( norm, 0.f, 0.f, 1.f );
}

#endif