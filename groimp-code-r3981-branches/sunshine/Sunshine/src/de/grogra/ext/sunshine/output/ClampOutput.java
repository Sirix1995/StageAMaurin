package de.grogra.ext.sunshine.output;

public class ClampOutput implements SunshineOutputStrategy 
{
	float min = 0.0f;
	float max = 1.0f;
	
	float r, g, b, a;
	
//	@Override
	public void setMinMax(float min, float max) 
	{
		this.min = min;
		this.max = max;		
	}

//	@Override
	public void applyValues(float[] values, float scale) 
	{				
		r = values[0] * scale;
		g = values[1] * scale;
		b = values[2] * scale;
						
		values[0] = r > max ? max : r;
		values[1] = g > max ? max : g;
		values[2] = b > max ? max : b;
		
	}

	public void gatherValues(float[] values) {
		// TODO Auto-generated method stub
		
	}

	public boolean needPrePass() {
		return false;
	}
}
