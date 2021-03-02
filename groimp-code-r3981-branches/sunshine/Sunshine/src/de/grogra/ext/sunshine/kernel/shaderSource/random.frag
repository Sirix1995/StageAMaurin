
vec2 random2D	= vec2(0.1);

float getSeed()
{
	float seed = round(mod(dot(gl_FragCoord.xy, vec2(17.0, 31.0)), 65537.0));
	return (seed) / 65537.0;
} 

vec2 rand2D()
{
	float x		= mod(random2D.x * 75.0, 1.0);
	float y		= mod(random2D.y * 75.0, 1.0);
	
	random2D 	= vec2(x, y);
	
	///random2D	= mod(random2D * 75.0, 1.0);
	
	return random2D;
}

// Park & Miller RNG: x(n+1) = x(x) * g mod n, n=2^16+1, g=75
float rand()
{	
	//random2D = vec2(mod(random2D.x * 75.0, 1.0), random2D.y);
	//return random2D.x;
	
	vec2 random = rand2D();
	
	return random.x;
}

// TODO: get rid of this LCG function and use rand() in the whole code instead !!
float LCG(float y1)
{
	return rand();
}

