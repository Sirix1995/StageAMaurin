
vec3 combine2Paths()
{
	vec3 combinedBSDFSpectrum = black;
	vec4 weight = texture2DRect(weightTexture, gl_FragCoord.xy);
	vec4 indexS	= texture2DRect(indexI_Texture, gl_FragCoord.xy);

	float decay = weight.w;
	float w_st 	= weight.z != 0.0 ? 1.0 / (1.0+weight.z) : 0.0;

	if(hasIntersection(indexS.w) && decay > 0.0)
	{
		vec4 pre_indexS		= texture2DRect(index1_Texture, gl_FragCoord.xy);
		vec4 pre_indexC		= texture2DRect(prev_index2_Texture, gl_FragCoord.xy);
		vec4 indexC			= texture2DRect(index2_Texture, gl_FragCoord.xy);

		TraceData eyeTD 	= getTraceData(indexC, index2_spec_Texture);
		TraceData lightTD	= getTraceData(indexS, indexI_spec_Texture);


		vec3 lightPathWeight 	= texture2DRect(index1_spec_Texture, gl_FragCoord.xy).rgb;
		vec3 eyePathWeight 		= texture2DRect(prev_index2_spec_Texture, gl_FragCoord.xy).rgb;


		//negated in vector on light path
		vec3 inLightRay = normalize(pre_indexS.xyz - indexS.xyz);
		//out vector on light path
		vec3 outLightRay = normalize(indexC.xyz - indexS.xyz);

		//negated in vector on eye path
		vec3 inEyeRay = normalize(pre_indexC.xyz - indexC.xyz);

		//out vector on eye path
		vec3 outEyeRay = normalize(indexS.xyz - indexC.xyz);

		float dist = distance(indexC.xyz, indexS.xyz);
		float combinedBSDFSpec_Distsq = max(1.0, dist);
		
		
		vec3 bsdfL_Spec;

		if(lightPathVertex > 0)
		{
			bsdfL_Spec = 
				calcDirectIllumination(lightPathWeight, inLightRay, outLightRay, lightTD, false, 1.0);
		} else
		{
			bsdfL_Spec = getObjectColor(lightTD.id).rgb;			
		}

		vec3 bsdfE_Spec =
			calcDirectIllumination(eyePathWeight, inEyeRay, outEyeRay, eyeTD, true, 1.0);

		
		//multiply both BSDFs
		combinedBSDFSpectrum = (bsdfL_Spec*bsdfE_Spec) / combinedBSDFSpec_Distsq;
	} //isVisible


//	w_st = 1.0;
	return w_st*combinedBSDFSpectrum*decay;
} //combine2Paths


void init()
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[MESH]		= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	sizes[TRI]		= 5;

	debug 			= 47.11;
} //init

float random;


vec4 scatterTex = vec4(0.0);

void scatter(float xy, vec3 color)
{
	if(xy > texWidth*texHeight-16.0 || xy < 0.0)
	{
		xy 		= 0.0;
		color 	= black;
	}
	
	scatterTex = vec4(xy, min(color, vec3(1.0)));
}

TraceData getTraceData(vec4 data, sampler2DRect texture, vec2 coord)
{
	int id 		= int(abs(data.w));
	int type	= int(texture2DRect(texture, coord).w);
	
	vec4 localiPoint	= convertGlobal2Local(id, type, data.xyz);
	TraceData td 		= TraceData(true, localiPoint, vec3(0.0), id, type, sign(data.w + EPSILON));
	
	td.normal = calculateNormal(td);
	td.iPoint = vec4(data.xyz, 1.0);
	
	return td;
} //getTraceData


void traceLightImage(vec3 cam)
{
	vec2 coord = gl_FragCoord.xy;
	coord.x	+= mod(currentSample, width);
	coord.y += mod(currentSample, height);
	
	vec4 weight	= texture2DRect(weightTexture, coord);
	
	float uv 	= 0.0;
	vec3 color 	= black;
	
	if(weight.w > 0.0)
	{
		vec3 camUp	= up*(texHeight/texWidth);
		vec3 camDir	= 2.0*dir; 
		
		// tileRight and tileUp have to be adjust to the size of the tile 	
		float w 		= texWidth / width;
		float h 		= texHeight / height;
		vec3 tileRight 	= (2.0*right) / w;
		vec3 tileUp 	= (2.0*camUp) / h;
		
		vec3 rayDirection = camDir - right - camUp;

		float pixelX 		= 2.0*(length(right)/texWidth); 	//pixel width
	 	float pixelY 		= 2.0*(length(camUp)/texHeight);	//pixel height
	 	
	 	vec3 pixRight		= pixelX*normalize(right); 	//vector with length of a pixel in direction of right 
		vec3 pixUp 			= pixelY*normalize(camUp);	//vector with height of a pixel in direction of camUp
		
		vec3 lightColor		= texture2DRect(indexI_spec_Texture, coord).rgb;
		vec4 light 			= texture2DRect(indexI_Texture, coord);
		vec3 pixDirection 	= light.xyz - cam;

		float cos_alpha = calculateAngle(camDir, pixDirection);
		float hypo 		= length(camDir) / cos_alpha; //length of vector to image plane
		pixDirection 	= normalize(pixDirection)*hypo;
		vec3 vec		= pixDirection - rayDirection; 
		
		
		float beta = acos(calculateAngle(vec, right));
		
//		float u = cos(beta) * length(vec) / pixelX;
//		float v = sin(beta) * length(vec) / pixelY;
		float u = 0.0;
		float v = 0.0;
		
		if(lightPathVertex > 0)
		{
			if(hasIntersection(light.w))
			{
				vec3 pre_indexS		= texture2DRect(index1_Texture, coord).xyz;
				vec3 outLightRay 	= normalize(pre_indexS - light.xyz);
				vec3 inLightRay 	= normalize(cam - light.xyz);
				vec3 lightPathWeight= texture2DRect(index1_spec_Texture, coord).rgb;
				
				TraceData lightTD	= getTraceData(light, indexI_spec_Texture, coord);
				
				color =	weight.w*calcDirectIllumination(lightPathWeight, inLightRay, outLightRay, lightTD, false, 1.0);
//				color *= 1.0 / (1.0+weight[2]);
				
				u = cos(beta) * length(vec) / pixelX;
				v = sin(beta) * length(vec) / pixelY;
			}	
		}
		else
		{
			int id = int(abs(light.w));
			color = getObjectColor(id).rgb;
			
			u = cos(beta) * length(vec) / pixelX;
			v = sin(beta) * length(vec) / pixelY;
		} //if
		
		
		if(u <= texWidth && v <= texHeight)
		{
			uv = floor(u) + floor(v)*texWidth;
		}
		else
		{
			color = black;
		}
		
		
	}
	
	// do scattering
	scatter(uv, color);	
}

void main(void)
{
	init();
	
	vec4 indexC 	= texture2DRect(index2_Texture, gl_FragCoord.xy);
	vec4 eyeImage	= texture2DRect(eyeImage_Texture, gl_FragCoord.xy);
	
	if(eyePathVertex == 0)
	{
		traceLightImage(indexC.xyz);
	}
	
	// if an intersection point exists do the shading
	if(hasIntersection(indexC.w) && eyePathVertex > 0)
	{
		eyeImage.rgb 	+= combine2Paths() / superSample;
		eyeImage.a 		= 1.0;
	} //if
	
	
	gl_FragData[0] = eyeImage;
	gl_FragData[1] = scatterTex;
} //main
