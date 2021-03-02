
void main(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 origin 	= texture2DRect(a0, texCoord);
	vec4 dir 		= texture2DRect(a1, texCoord);
	vec4 spectrum	= texture2DRect(a2, texCoord);
	vec4 state		= texture2DRect(stateTexture, texCoord);
	
	init(dir.w);
	primRay = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, spectrum.rgb);
	
	State lastState = State(state.xyz, origin.w, spectrum.w, state.w);
	
	
	vec2 tdata = trace(primRay, lastState);
	
	
	gl_FragData[0] = vec4(secRay.origin.xyz, tdata.x);
	gl_FragData[1] = vec4(secRay.direction, random);
	gl_FragData[2] = vec4(secRay.spectrum, tdata.y);
	gl_FragData[3] = vec4(lastState.iPoint, lastState.depth);
	
} //main