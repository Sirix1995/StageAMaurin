

void main()
{
	vec2 texCoor	= gl_TexCoord[0].xy;

	vec4 oldColor	= texture2DRect(outputImage0, texCoor);
	vec3 xyz		= texture2DRect(outputImage3, texCoor).xyz;

	vec4 currentCol	= vec4( convert2RGB(xyz), 1.0 );

	gl_FragData[0] 	= texture2DRect(outputImage0, texCoor); //oldColor + currentCol;
}
