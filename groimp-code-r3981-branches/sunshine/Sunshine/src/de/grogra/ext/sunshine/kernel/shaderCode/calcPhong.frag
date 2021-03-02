
// this is the old and now unused direct illumination calculation
vec3 calcPhong(Phong p, Light light, vec3 normal, TraceData td, int counter)
{
	vec4 cpri;
	
	vec3 vp = normalize(light.origin.xyz - td.iPoint.xyz);
	vec3 vc = normalize(primRay.origin.xyz - td.iPoint.xyz);
	
	// distance to light source
	float d = length(vp);
	
//	float fade_distance = light.distance;
//	float fade_power = light.exponent;

	vec4 dcl = light.color; 			//diffuse color of light
	float att = 1.0;//light.origin.w != 0.0 ? 2.0 / (1.0 + pow(d / fade_distance, fade_power)) : 1.0;
	float spot = 1.0;
	
	float f = max(dot(normal, vp), 0.0) != 0.0 ? 1.0 : 0.0;
	
	vec3 h = (vp + vc) / length(vp + vc); //Blinn-Phong-Model: Halfway vector
	vec4 scl = vec4(1.0); //specular intensity of light
	
	if(counter == 0)
		cpri = p.ecm;
	
	cpri += att * spot * ( p.acm
		+ max( dot(normal, vp ), 0.0) * p.dcm * dcl
//		+ f * pow(max(dot(normal, normalize(h) ), 0.0), p.srm.x*128.0) * p.scm * scl );
		+ f * pow(max(dot(normal, normalize(h) ), 0.0), pow(2.0, (p.srm.x-4.0)+(5.0*p.srm.x) ) *128.0) * p.scm * scl );
	
	return cpri.xyz;
}
