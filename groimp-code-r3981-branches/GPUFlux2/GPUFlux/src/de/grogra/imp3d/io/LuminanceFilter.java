package de.grogra.imp3d.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import de.grogra.imp3d.objects.LightDistribution;
import de.grogra.imp3d.objects.LightDistributionResource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;

public class LuminanceFilter extends FilterBase implements ObjectSource 
{
	public static final IOFlavor FLAVOR = IOFlavor.valueOf (LightDistribution.class);


	public LuminanceFilter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}

	@Override
	public Object getObject () throws IOException
	{
		Reader in = ((ReaderSource) source).getReader ();
		// read luminance from in
		
		BufferedReader reader = new BufferedReader( in );
		
		int width = Integer.parseInt( reader.readLine().trim() );
		int height = Integer.parseInt( reader.readLine().trim() );
		
		double luminance[][] = new double[height][width];
		
		for( int y = 0 ; y < height; y++ )
		{
			String line = reader.readLine();
			StringTokenizer tokenizer = new StringTokenizer( line.replace(',', '.') );
			for( int x = 0 ; x < width; x++ ) {
				try {
					double v = Double.parseDouble( tokenizer.nextToken() );
					luminance[y][x] = v;
				} catch( NoSuchElementException ex ) {
					throw new IOException
						("Illegal string for Luminance at("+y+","+x+") in this line: " + line);
				}
			}
		}
		
		return new LightDistributionResource( luminance );
	}
	
}
