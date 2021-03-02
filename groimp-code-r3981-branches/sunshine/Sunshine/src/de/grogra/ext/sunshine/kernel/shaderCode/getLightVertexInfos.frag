
void setVertexInfos(vec3 inRay, TraceData td, inout VertexInfos infos)
{
	TraceData lastTD 	= getTraceData(texture2DRect(preVertexTexture, gl_FragCoord.xy), preSpecTexture);
	vec3 lastNormal		= currentVertex > 1 ? calculateNormal(lastTD) : vec3(0.0);
	vec3 vector			= lastTD.iPoint.xyz - td.iPoint.xyz;

	infos.dirDensOut	= computeDirDens(td, normalize(secRay.direction), -inRay, true);
	infos.geomFac		= calculateGeometryFactor(lastNormal, td.normal, vector);
}