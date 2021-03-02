float fresnel_slick(float cosi, float normal_incidence)
{
	float sign = sign(1.0 - cosi);
	return normal_incidence + (1.0f - normal_incidence) * sign * pow(abs(1.0 - cosi), 5.0f);
}
