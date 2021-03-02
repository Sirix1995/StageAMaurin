
void main()
{	
	gl_FragData[0] = vec4(0.0);
	gl_FragData[1] = vec4(vec3(0.0), -1.0);
	gl_FragData[2] = vec4(vec3(0.0), getSeed());
	gl_FragData[3] = vec4(0.0);
}