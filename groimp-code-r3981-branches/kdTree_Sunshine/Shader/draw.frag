#extension GL_ARB_texture_rectangle : enable
uniform sampler2DRect draw;


void main()
{
	vec2 texCoord = gl_TexCoord[0].xy;
	vec4 color = texture2DRect(draw, texCoord);
	
	gl_FragColor = color;	
}