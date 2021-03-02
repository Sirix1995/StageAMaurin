void getValueFromTex(TraceData td, float matID, int rowOffset, int matCols, inout mat4 container )
{
	float value		= 0.0;
	int row			= 0;

	for(int i = 0; i < 6; i++)
	{
		int line 		= int(matID / float(matCols));
		int pos			= int(matID - int(matCols * line));

		matID++;

		if(i > 3)
			row++;

		line		   += rowOffset;

		value 			= texture2DRect(materialTex, vec2(pos, line)).x;

		if(value >= 0.0 )
		{
			container[row][int(mod(i,4))]= value;

			continue;

		} else {

			UV uv		= getUV(td);

			value		= abs(value) - 1;

			int y		= int(value / 8.0);
			int x		= int(value - (y * 8));

			vec3 rgb	= texture2DRect(texTexture, vec2( (x + uv.u) * 512, (y + uv.v) * 512) ).xyz;
			value		= getReflectanceIntensity(rgb);

			container[row][int(mod(i,4))]= value;

		}
	}
}

void main()
{
	vec2 xy				= gl_TexCoord[0].xy;

	vec4 tmp0			= texture2DRect(a0, xy);
	vec4 tmp1			= texture2DRect(a1, xy);

	vec3 iPoint			= tmp0.xyz;
	float id			= tmp0.w;
	float type			= tmp1.w;

	float matID			= abs(getObjectColor(id).x) - 1.0;

	TraceData td		= TraceData(
								true,
								vec4(iPoint, 1.0),
								vec3(0.0),
								int(id),
								int(type),
								false
							);

	mat4 container;

	getValueFromTex(td, matID + 0, matRowOffset, matCols, container );

	gl_FragData[0]		= container[0];
	gl_FragData[1]		= container[1];
	//gl_FragData[2]		= vec4(1.0);
	//gl_FragData[3]		= vec4(1.0);
}

