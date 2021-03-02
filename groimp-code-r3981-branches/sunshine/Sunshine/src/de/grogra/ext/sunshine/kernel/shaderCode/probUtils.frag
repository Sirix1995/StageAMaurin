
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

float calculateGeometryFactor(vec3 lastNormal, vec3 currNormal, vec3 vector)
{
	vec3 oi = length(vector) != 0.0 ? normalize(vector) : vector;

	float last_length = length(lastNormal);
	float curr_length = length(currNormal);
	
	if(last_length != 0.0)
		lastNormal = normalize(lastNormal);
	
	if(curr_length != 0.0)
		currNormal = normalize(currNormal);
	
	float cosOut 	= curr_length != 0.0 ? dot(currNormal,  oi) : 1.0;
	float cosIn		= last_length != 0.0 ? dot(lastNormal, -oi) : 1.0;
	
	float len 		= length(vector);
	float dist_sq 	= max(1.0, len*len);


	return abs(cosIn * cosOut) / dist_sq;
	
} //calculateGeometryfactor
