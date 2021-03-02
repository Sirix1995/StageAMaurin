
void main()
{
	vec2 xy				= gl_TexCoord[0].xy;

	vec4 tmp0			= texture2DRect(a0, xy);
	vec4 tmp1			= texture2DRect(a1, xy);

	vec3 iPoint			= tmp0.xyz;
	float id			= tmp0.w;
	float type			= tmp1.w;

	TraceData td		= TraceData(
								true,
								vec4(iPoint, 1.0),
								vec3(0.0),
								int(id),
								int(type),
								false
							);

	UV uv				= UV(0.0, 0.0);

	if(id >= 0)
		uv				= getUV(td);

	gl_FragData[0]		= vec4(uv.u, uv.v, vec2(1.0));
	gl_FragData[1]		= vec4(1.0);
	gl_FragData[2]		= vec4(1.0);
	gl_FragData[3]		= vec4(1.0);
}

