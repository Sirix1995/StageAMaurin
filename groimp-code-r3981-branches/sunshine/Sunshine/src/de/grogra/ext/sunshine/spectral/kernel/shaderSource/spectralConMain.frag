

void main()
{
	vec2 texCoor		= gl_TexCoord[0].xy;

	vec4 resultPixel	= texture2DRect(outputImage0, texCoor);

	gl_FragData[0] 		= vec4( convert2RGB(resultPixel.xyz), 1.0 );
	//gl_FragData[0] 		= resultPixel;

}
