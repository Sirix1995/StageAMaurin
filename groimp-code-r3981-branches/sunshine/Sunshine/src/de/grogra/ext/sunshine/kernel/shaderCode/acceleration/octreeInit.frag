
void main(void)
{
	vec4 origin 	= texture2DRect(a0, gl_FragCoord.xy);
	vec4 dir 		= texture2DRect(a1, gl_FragCoord.xy);

	init(dir.w);
	
	Ray ray = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, vec3(1.0), 1.0);
	
	// first: test intersection with the root node = 0
	Cell root = RayCellIntersection(ray, 0);
	
	float rootHit = root.delta_entry < root.delta_exit ? 1.0 : 0.0;
	
	gl_FragData[0] = vec4(rootHit, 1.0, root.index, 0.0);

} //main
