
void setVertexInfos(vec3 inRay, TraceData td, inout VertexInfos infos)
{
	TraceData lastTD 	= getTraceData(texture2DRect(preVertexTexture, gl_FragCoord.xy), preSpecTexture);
	lastTD.iPoint		= convertGlobal2Local(lastTD.id, lastTD.type, lastTD.iPoint.xyz);
	
	vec3 lastNormal		= currentVertex > 1 ? calculateNormal(lastTD) : vec3(0.0);
	vec3 vector			= lastTD.iPoint.xyz - td.iPoint.xyz;

	infos.dirDensOut	= computeDirDens(td, normalize(secRay.direction), -inRay, false);
	infos.geomFac		= calculateGeometryFactor(lastNormal, td.normal, vector);
}
