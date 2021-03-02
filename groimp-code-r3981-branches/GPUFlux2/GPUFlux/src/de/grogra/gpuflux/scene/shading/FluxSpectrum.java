package de.grogra.gpuflux.scene.shading;

import java.io.IOException;

import javax.vecmath.Point3f;

import org.sunflow.image.RGBSpace;
import org.sunflow.image.XYZColor;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.utils.CIE;
import de.grogra.imp3d.spectral.ConstantSpectralCurve;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.ManageableType;

public class FluxSpectrum {

	public static class SpectralDiscretization
	{
		private static final float CONSERVATIVE_IMPORTANCE = 0.1f; // 10% conservative importance
		
		public SpectralDiscretization(int spectralLambdaMin, int spectralLambdaMax, int spectralLambdaStep)
		{
			if( spectralLambdaMin >= spectralLambdaMax )
				throw new IllegalArgumentException (
					"Range must be non-empty: (" + spectralLambdaMin + "," + spectralLambdaMax + ")");
			
			if( spectralLambdaStep <= 0 )
				throw new IllegalArgumentException (
					"Step size must be strictly positive");
			
			spectralLambdaMax += (spectralLambdaMax - spectralLambdaMin) % spectralLambdaStep;
			
			this.spectralLambdaMin = spectralLambdaMin;
			this.spectralLambdaMax = spectralLambdaMax;
			this.spectralLambdaStep = spectralLambdaStep;
		}
		
		public float [] getDiscreteSPD(SpectralCurve SPD)
		{
			int bins = discretization.getLambdaBins();
			float [] discreteSPD = new float[bins];
			for( int i = 0 ; i < bins ; i++ )
			{
				float intensity = SPD.sample( discretization.getLambdaMin() + i * discretization.getLambdaStep() );
				discreteSPD[i] = intensity; 
			}
			return discreteSPD;
		}
		
		public float [] getDiscreteNormalizedSPD(SpectralCurve SPD, float unit)
		{
			float PDF [] = getDiscreteSPD(SPD);
			float total = 0;
			for( int i = 0 ; i < PDF.length ; i++ )
				total += PDF[i];
			if( total > 0 )
			{
				for( int i = 0 ; i < PDF.length ; i++ )
					PDF[i] *= PDF.length * unit / total;
			}
			return PDF;
		}
		
		public float getSPDPower(SpectralCurve SPD)
		{
			float PDF [] = getDiscreteSPD(SPD);
			float total = 0;
			for( int i = 0 ; i < PDF.length ; i++ )
				total += PDF[i];
			return total;
		}
		
		// use conservative CPT
		public float [] getDiscreteCumulativeSPD(SpectralCurve SPD)
		{
			float PDF [] = getDiscreteSPD(SPD);
			
			int total_nonzero_buckets = 0;
			float total = 0;
			for( int i = 0 ; i < PDF.length ; i++ )
			{
				// compute total nonzero wavelength range
				if( PDF[i] > 0 )
					total_nonzero_buckets++; 
				
				// compute total power
				total += PDF[i];
			}

			float sum = 0;
			float [] CDF = new float[PDF.length];
			for( int i = 0 ; i < PDF.length ; i++ )
			{
				float importance = 0;
				if( PDF[i] > 0 )
				{
					// add conservative importance
					importance += CONSERVATIVE_IMPORTANCE * total * (1 / (float)total_nonzero_buckets);
					// add power importance
					importance += (1 - CONSERVATIVE_IMPORTANCE) * PDF[i];
				}
				
				sum += importance;
				CDF[i] = sum / total;
			}
			return CDF;
		}
		
		public int getLambdaMin() {
			return spectralLambdaMin;
		}
		
		public int getLambdaMax() {
			return spectralLambdaMax;
		}

		public int getLambdaStep() {
			return spectralLambdaStep;
		}

		public int getLambdaBins() {
			return ((spectralLambdaMax - spectralLambdaMin) / spectralLambdaStep);
		}
		
		int spectralLambdaMin = 360;
		int spectralLambdaMax = 830;
		int spectralLambdaStep = 1;
	}
	
	private SpectralCurve SPD;
	
	static private SpectralCurve ImportanceSPD = new ConstantSpectralCurve(1);
	static private SpectralDiscretization discretization;
	
	static public void setImportance(SpectralCurve ImportanceSPD)
	{
		FluxSpectrum.ImportanceSPD = ImportanceSPD;
	}
	
	static public void setDiscretization(SpectralDiscretization discretization)
	{
		FluxSpectrum.discretization = discretization;
	}
	static public SpectralDiscretization getDiscretization()
	{
		return discretization;
	}
	
	public FluxSpectrum(SpectralCurve SPD) {
		this.SPD = SPD;
	}
	
	public static void serializeNormalizedSPD(ComputeByteBuffer out, SpectralCurve SPD, float unit) throws IOException
	{
		float [] spd = discretization.getDiscreteNormalizedSPD(SPD, unit);
		for( int i = 0 ; i < spd.length ; i++ )
		{
			float intensity = spd[i];
			out.writeFloat(intensity);
		}
	}
	
	public static void serializeSPD(ComputeByteBuffer out, SpectralCurve SPD) throws IOException
	{
		float [] spd = discretization.getDiscreteSPD(SPD);
		for( int i = 0 ; i < spd.length ; i++ )
		{
			float intensity = spd[i];
			out.writeFloat(intensity);
		}
	}
	
	public static void serializeCumulativeSPD(ComputeByteBuffer out, SpectralCurve SPD) throws IOException
	{
		float [] cspd = discretization.getDiscreteCumulativeSPD(SPD);
		for( int i = 0 ; i < cspd.length ; i++ )
		{
			float intensity = cspd[i];
			out.writeFloat(intensity);
		}
	}
	
	public static void serialize(ComputeByteBuffer out,SpectralCurve SPD)
		throws IOException {
		Point3f color = getRGBDistribution(SPD);
		out.write(color);
		serializeSPD(out,SPD);
	}
	
	private static Point3f whiteColor = new Point3f();
	static {
		CIE.XYZtoRGB( whiteColor, (new ConstantSpectralCurve(1)).toXYZ());	
	}
	
	public static Point3f getRGBDistribution(SpectralCurve SPD)
	{
		XYZColor xyz = SPD.toXYZ();
		Point3f color = new Point3f();
		CIE.XYZtoRGB( color, xyz );
		// normalize color to distribution so that a constant unit spectrum translates to RGB(1,1,1)
		color.x /= whiteColor.x;
		color.y /= whiteColor.y;
		color.z /= whiteColor.z;
		
		color.x = Math.max(0, color.x);
		color.y = Math.max(0, color.y);
		color.z = Math.max(0, color.z);
		return color;
	}
	
	public Point3f getRGBDistribution()
	{
		return getRGBDistribution(SPD);
	}
	
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		serialize(out,SPD);
	}
	public static void serializeCorrectedCumulativeSPD(
			ComputeByteBuffer out, final SpectralCurve SPD) throws IOException {
		// serialize the curve, corrected for the spectral interest curve
		
		SpectralCurve correctedSPD = new SpectralCurve()
		{
			public float sample( float lambda )
			{
				return ImportanceSPD.sample(lambda) * SPD.sample( lambda );
			}

			public ManageableType getManageableType() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		serializeCumulativeSPD( out, correctedSPD );
	}


}
