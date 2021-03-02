
float computeDirDens(TraceData td, vec3 inRay, vec3 outRay, bool adjoint)
{
	float dirDens = 1.0;
	vec3 newWeight;

	//check used shading
	vec4 color 		= getObjectColor(td.type == TRI ? meshPart : td.id);
	float phongID 	= color.x == -1.0 ? color.y : -1.0;

	if(phongID < 0.0)
	{
		dirDens = computeBSDF(color, td.normal, inRay, vec3(0.0), outRay, adjoint, td.id, newWeight);
	}
	else
	{
		dirDens = computeBSDF(getPhong(phongID, td), td.normal, inRay, vec3(0.0), outRay, adjoint, newWeight);
	}

	return dirDens;
}

float calculateGeometryFactor(vec3 nextNormal, vec3 currNormal, vec3 vector)
{
	vec3 oi = length(vector) != 0.0 ? normalize(vector) : vector;

	float next_length = length(nextNormal);
	float curr_length = length(currNormal);
	
	if(next_length != 0.0)
		nextNormal = normalize(nextNormal);
	
	if(curr_length != 0.0)
		currNormal = normalize(currNormal);
	
	float cosOut 	= curr_length != 0.0 ? dot(currNormal,  oi) : 1.0;
	float cosIn		= next_length != 0.0 ? dot(nextNormal, -oi) : 1.0;
	
	float len 		= length(vector);
	float dist_sq 	= max(1.0, len*len);


	return abs(cosIn * cosOut) / dist_sq;
	
} //calculateGeometryfactor


float calcLightBSDF(int pos, vec3 outRay)
{
	Light light 	= getLight(pos - lightPart);
	float dirDens 	= 1.0;
	
	if(light.type == PONT_LIGHT)
	{
		dirDens = _PI_ / 2.0;
	}
	
	if(light.type == AREA_LIGHT)
	{
		mat4 m4x4 = getMatrix(pos);
		mat3 m3x3;
		m3x3[0] = m4x4[0].xyz;
		m3x3[1] = m4x4[1].xyz;
		m3x3[2] = m4x4[2].xyz;
		
		//length in global coordinate system
		vec3 v = m3x3 * vec3(0.0, 0.0, 1.0);
		//axis in global coordinate system
		vec3 w = m3x3 * vec3(1.0, 0.0, 0.0);
		
		vec3 q 	= cross(v, w);
		float b = dot(q, outRay);
		if(b <= 0.0)
		{
			dirDens = 0.0;
		}
		
		dirDens = (light.exponent + 2.0) * pow (b / length(q), light.exponent + 1.0)
			* (1.0 / (2.0 * PI));
	}
	
	return dirDens;
}


float getDenumenator()
{
	vec4 info = texture2DRect(infoTexture_I, gl_FragCoord.xy);

	return info[0] * info[2];
}

TraceData getCurrentTD()
{
	TraceData x_i_TD = nilTD; // the normal vector vec3(0.0) is imporatant
	
	vec4 x_i = texture2DRect(indexI_Texture, gl_FragCoord.xy); // x_{i}
	
	if(currentVertex != 0 && currentVertex != lastVertex)
	{
		x_i_TD = getTraceData(x_i, indexI_spec_Texture);
	}
	
	return x_i_TD;
}

// to compute dirDens with bsdf x_1 and x_2 must exist
float getEnumenator(sampler2DRect x_1_tex, sampler2DRect x_2_tex, 
			  		sampler2DRect x_1_spec, bool adjoint
			  		)
{
	float dirDens = 1.0;
	float geomFac = 1.0;
	
	// xi and its two predecessors
	vec4 x_2 = texture2DRect(x_2_tex, 		gl_FragCoord.xy); // x_{i-2}
	vec4 x_1 = texture2DRect(x_1_tex, 		gl_FragCoord.xy); // x_{i-1}
	vec4 x_i = texture2DRect(indexI_Texture,gl_FragCoord.xy); // x_{i}

	vec3 inRay 	= x_2.xyz - x_1.xyz;
	vec3 outRay = x_i.xyz - x_1.xyz;

	TraceData x_1_TD 	= getTraceData(x_1, x_1_spec);
	TraceData x_i_TD	= getCurrentTD();
		

	dirDens = computeDirDens(x_1_TD, normalize(inRay), normalize(outRay), adjoint);
	geomFac	= calculateGeometryFactor(x_i_TD.normal, x_1_TD.normal, outRay);
	
	
	return dirDens * geomFac;
}


float getLightEnum(sampler2DRect x_1_tex)
{
	vec4 x_1 	= texture2DRect(x_1_tex, 		gl_FragCoord.xy); // x_{i-1}
	vec4 x_i 	= texture2DRect(indexI_Texture,	gl_FragCoord.xy); // x_{i}
	
	vec3 outRay	= x_i.xyz - x_1.xyz;
	float pos	= x_1.w;
	
	TraceData x_i_TD = getCurrentTD();
	
	float dirDens = calcLightBSDF(int(pos), normalize(outRay));
	float geomFac = calculateGeometryFactor(x_i_TD.normal, vec3(0.0), outRay);
	
	return dirDens * geomFac;
}


float getCamEnum(sampler2DRect x_1_tex)
{
	vec4 x_1 	= texture2DRect(x_1_tex, 		gl_FragCoord.xy); // x_{i-1}
	vec4 x_i 	= texture2DRect(indexI_Texture,	gl_FragCoord.xy); // x_{i}
	
	vec3 outRay		= x_i.xyz - x_1.xyz;
	
	TraceData x_i_TD = getCurrentTD();
	
	float icos 		= dot(normalize(outRay), normalize(camDir));
	float dirDens 	= 0.25*icos*icos*icos;
	float geomFac	= calculateGeometryFactor(x_i_TD.normal, vec3(0.0), outRay);
	
	return dirDens * geomFac;
}


void calcWeighteningFactor(inout vec4 weight)
{
	float p_pre;
	float e 	= 1.0;
	float d 	= 1.0;
	float next 	= 1.0;
	bool threshold;
	
	// path direction from light to camera
	if(DIRECTION == BEGIN2END)
	{
		if(currentVertex == 1)
		{
			e = getLightEnum(index1_Texture);
		}
		
		if(currentVertex >= 2)
		{
			e = getEnumenator(index1_Texture, prev_index1_Texture, index1_spec_Texture, true);
		}
		
		
		d 			= getDenumenator();
		p_pre 		= weight[0];
		next		= p_pre * e / d;
		weight[0] 	= next;
	}
	else //DIRECTION == END2BEGIN
	{
		if(currentVertex == lastVertex-1)
		{
			e = getCamEnum(index2_Texture);
		}
		
		if(currentVertex <= lastVertex-2)
		{
			e = getEnumenator(index2_Texture, prev_index2_Texture, index2_spec_Texture, false);
		}
		
		
		d			= getDenumenator();
		p_pre 		= weight[1];
		next 		= p_pre * d / e;
		weight[1] 	= next;
	}
	
	if(next > 0.0)
		weight[2] += pow(next, heuristicExponent);
	
	
} //calcWeighteningFactor

void main(void)
{
	vec4 indexS	= texture2DRect(indexI_Texture, gl_FragCoord.xy);
	vec4 indexC	= texture2DRect(index2_Texture, gl_FragCoord.xy);
	vec4 weight = texture2DRect(weightTexture, gl_FragCoord.xy);

	
	if((hasIntersection(indexC.w) && hasIntersection(indexS.w) && weight.w > 0.0)
		|| (eyePathVertex == 0))
	{
		calcWeighteningFactor(weight);
	}
	
	gl_FragData[0] = weight;
	
} //main
