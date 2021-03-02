
vec3 calcRGB(vec4 color, vec3 lightColor, float angle)
{
	return color.rgb * lightColor * max(angle, 0.0);
}

vec3 calcDirectIllumination(vec3 specIn, vec3 inRay, vec3 outRay, TraceData td, 
		bool adjoint, float decay)
{
	vec3 bsdf;
	
	//check used shading
	vec4 color 	= getObjectColor(td.type == TRI ? meshPart : td.id);
	float id 	= color.x == -1.0 ? color.y : -1.0;
	
	if(id < 0.0)
	{
		computeBSDF(color, td.normal, inRay, specIn, outRay, adjoint, td.id, bsdf);
	}
	else
	{
		computeBSDF(getPhong(id, td), td.normal, inRay, specIn, outRay, adjoint, bsdf);
	}
		
	return decay*bsdf;
} //calcDirectIllumination
		