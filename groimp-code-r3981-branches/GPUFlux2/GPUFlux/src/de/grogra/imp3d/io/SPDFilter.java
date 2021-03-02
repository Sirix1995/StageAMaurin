package de.grogra.imp3d.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import de.grogra.imp3d.shading.SPD;
import de.grogra.imp3d.shading.SPDResource;
import de.grogra.imp3d.spectral.IrregularSpectralCurve;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem ;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;

public class SPDFilter extends FilterBase implements ObjectSource 
{
	public static final IOFlavor FLAVOR = IOFlavor.valueOf (SPD.class);


	public SPDFilter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}

	class LambdaPower
	{
		public LambdaPower(float lambda, float power) {
			this.lambda = lambda;
			this.power = power;
		}

		float lambda, power;
	};
	
	public Object getObject () throws IOException
	{
		Reader in = ((ReaderSource) source).getReader ();
		
		BufferedReader reader = new BufferedReader( in );
		
		Vector<LambdaPower> spectrum = new Vector<LambdaPower>();
		String line = reader.readLine();
		
		while( line != null )
		{
			StringTokenizer tokenizer = new StringTokenizer( line.replace(',', '.') );
			
			try
			{
				int lambda = Integer.parseInt( tokenizer.nextToken() );
				float power = Float.parseFloat( tokenizer.nextToken() );
			
				spectrum.add(new LambdaPower( lambda, power ));
			}catch( NoSuchElementException ex )
			{
				throw new IOException
					("Illegal string for wavelength-power tuple: " + line);
			}
			
			line = reader.readLine();
		}
		
		float[] wavelengths = new float[spectrum.size()];
		float[] amplitudes = new float[spectrum.size()];
		
		for( int i = 0 ; i < spectrum.size(); i++ )
		{
			wavelengths[i] = spectrum.get(i).lambda;
			amplitudes[i] = spectrum.get(i).power;
		}		
		
		return new SPDResource( new IrregularSpectralCurve( wavelengths , amplitudes ) );
	}
	
}
