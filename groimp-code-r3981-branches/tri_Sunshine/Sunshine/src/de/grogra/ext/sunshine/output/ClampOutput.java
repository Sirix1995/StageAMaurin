package de.grogra.ext.sunshine.output;

public class ClampOutput implements SunshineOutputStrategy 
{
	float min = 0.0f;
	float max = 1.0f;
	
	float r, g, b, a;
	
	@Override
	public void setMinMax(float min, float max) {
		this.min = min;
		this.max = max;		
	}

	@Override
	public void applyValues(float[] values) 
	{		
		r = values[0];
		g = values[1];
		b = values[2];
		a = values[3];
						
		values[0] = r > max ? max : r;
		values[1] = g > max ? max : g;
		values[2] = b > max ? max : b;
		values[3] = a > max ? max : a;
		
	}
}
