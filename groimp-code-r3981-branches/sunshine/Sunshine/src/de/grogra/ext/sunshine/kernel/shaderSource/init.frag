uniform int px;
uniform int py;
uniform int width;

void main()
{
	vec2 xyCoor		= gl_FragCoord.xy;

	vec4 tempRnd	= texture2DRect(RND_TEX, xyCoor);

	random2D		= vec2(tempRnd.xy);

	gl_FragData[0] 	= vec4(0.0);
	gl_FragData[1] 	= vec4(vec3(0.0), random2D.x);
	gl_FragData[2] 	= vec4(vec3(0.0), random2D.y);
	gl_FragData[3] 	= vec4(vec4(0.0));
}
