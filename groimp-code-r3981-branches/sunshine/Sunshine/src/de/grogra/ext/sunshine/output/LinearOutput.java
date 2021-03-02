package de.grogra.ext.sunshine.output;


public class LinearOutput implements SunshineOutputStrategy {

	float min = 0.0f;
	float max = 1.0f;
	
	public float tempMin = 1.0f;
	public float tempMax = 0.0f;
	
	public float tempMin2 = 1.0f;
	public float tempMax2 = 0.0f;
	
	public void applyValues(float[] values, float scale) 
	{
		//System.out.print("Apply: " + tempMax + ", " + tempMin);
		
		values[0] = ((values[0] / tempMax) - tempMin) * scale; 
		values[1] = ((values[1] / tempMax) - tempMin) * scale; 
		values[2] = ((values[2] / tempMax) - tempMin) * scale;
	}

	public void setMinMax(float min, float max) 
	{
		this.min = min;
		this.max = max;
	}

	public void gatherValues(float[] values) 
	{
		tempMin = Math.min(values[0], tempMin);
		tempMin = Math.min(values[1], tempMin);
		tempMin = Math.min(values[2], tempMin);
		
		tempMax = Math.max(values[0], tempMax);
		tempMax = Math.max(values[1], tempMax);
		tempMax = Math.max(values[2], tempMax);
	}

	public boolean needPrePass() {
		return true;
	}

}
