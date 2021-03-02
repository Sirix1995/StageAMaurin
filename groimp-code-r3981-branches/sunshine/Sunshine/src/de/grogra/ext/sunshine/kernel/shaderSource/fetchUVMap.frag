UV getUV(TraceData td)
{
	vec2 uv	= texture2DRect(tempMatTex, gl_TexCoord[0].xy).xy;

	return UV(uv.x, uv.y);
}
