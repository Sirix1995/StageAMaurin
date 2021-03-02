
float random;

float getSeed()
{
	return round(mod(dot(gl_FragCoord.xy, vec2(17.0, 31.0)), 65537.0));
//	return mod(dot(gl_FragCoord.xy, vec2(17.0, 31.0)), 65537.0);		// Make some trouble on ATI
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

