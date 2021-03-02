package de.grogra.imp3d.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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

	public Object getObject () throws IOException
	{
		Reader in = ((ReaderSource) source).getReader ();
		// read luminance from in
		
		BufferedReader reader = new BufferedReader( in );
		
		int width = Integer.parseInt( reader.readLine() );
		int height = Integer.parseInt( reader.readLine() );
		
		double luminance[][] = new double[height][width];
		
		for( int y = 0 ; y < height; y++ )
		{
			StringTokenizer tokenizer = new StringTokenizer( reader.readLine() );
			for( int x = 0 ; x < width; x++ )
			{
				double v = Double.parseDouble( tokenizer.nextToken() );
				luminance[y][x] = v;
			}
		}
		
		return new LightDistributionResource( luminance );
	}
	
}
