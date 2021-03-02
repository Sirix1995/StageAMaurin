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

	float flipZ = 1.0;
	
	if(x > 2.0)
	{
		flipZ = -1.0;
		x-=2.0;
	} else if(x < -2.0)
	{
		flipZ = -1.0;
		x+=2.0;
	}
	
	float z = sqrt(1.0 - ((x * x) + (y * y)) );	
	
	return vec3(x, y, flipZ * z);
}
