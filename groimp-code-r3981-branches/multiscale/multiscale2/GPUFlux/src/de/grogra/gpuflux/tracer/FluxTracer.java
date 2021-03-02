package de.grogra.gpuflux.tracer;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.jocl.Sizeof;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.jocl.compute.Buffer;
import de.grogra.gpuflux.scene.shading.FluxSpectrum;
import de.grogra.gpuflux.scene.shading.FluxSpectrum.SpectralDiscretization;
import de.grogra.imp.*;
import de.grogra.imp3d.spectral.ConstantSpectralCurve;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Options;

public abstract class FluxTracer implements ProgressMonitor
{
	protected static final int DEFAULT_TRACE_DEPTH = 5;
	public static final int DEFAULT_PREFERRED_DURATION = 500;
	protected static final float DEFAULT_MIN_POWER = 0.01f;
	public static final int INITIAL_SAMPLE_COUNT = 10000000;
	public static final int MAXIMUM_SAMPLE_COUNT = 1000000000;
	public static final int INITIAL_SPECTRAL_RESOLUTION = 1;
	private static final int SPECTRUM_VISIBLE_MIN = 380;
	private static final int SPECTRUM_VISIBLE_MAX = 720;
	public static boolean BATCH_LOGGING_ENABLED = false;
	public static double BATCH_BALANCE_SMOOTH = 0.9;
		
	protected class MsgBox extends Dialog implements ActionListener {
	    private Button ok;
	    /*
	     * @param frame   parent frame 
	     * @param msg     message to be displayed
	     * @param okcan   true : ok cancel buttons, false : ok button only 
	     */
	    MsgBox(Frame frame, String msg){
	        super(frame, "Message", true);
	        setLayout(new BorderLayout());
	        add("Center",new Label(msg));
	        addOKPanel();
	        createFrame();
	        pack();
	        setVisible(true);
	    }
	    
	    void addOKPanel() {
	        Panel p = new Panel();
	        p.setLayout(new FlowLayout());
	        createOKButton( p );
	        add("South",p);
	    }

	    void createOKButton(Panel p) {
	        p.add(ok = new Button("Terminate"));
	        ok.addActionListener(this); 
	    }

	    void createFrame() {
	        Dimension d = getToolkit().getScreenSize();
	        setLocation(d.width/3,d.height/3);
	    }

	    public void actionPerformed(ActionEvent ae){
	        if(ae != null && ae.getSource() == ok) {
	            setVisible(false);
	        }
	    }
	}

	protected int width;
	protected int height;
	protected ArrayList<ImageObserver> observers;
	protected View view;
	protected Options options;
	protected boolean useBih;
	
	protected BufferedImage image;
	float hdrPixels[][];

	public FluxTracer()
	{}
	
	public void render( View view, int width, int height,
			ArrayList<ImageObserver> obs, Options options ) throws IOException {
		
		this.view = view;
		this.width = width;
		this.height = height;
		this.observers = obs;
		this.options = options;
		
		view.getWorkbench ().beginStatus (this);
		
		try
		{
			// create destination image
			hdrPixels = new float[4][width * height];
			
			DataBufferFloat buffer = new DataBufferFloat (hdrPixels, hdrPixels[0].length);
			BandedSampleModel sampleModel = new BandedSampleModel (buffer.getDataType (), width, height, 4);
			WritableRaster raster = Raster.createWritableRaster (sampleModel, buffer, null);
			ColorSpace cs = ColorSpace.getInstance (ColorSpace.CS_sRGB);
			ComponentColorModel cm = new ComponentColorModel (cs, true, false, Transparency.TRANSLUCENT, buffer.getDataType ());
			image = new BufferedImage (cm, raster, false, null);
			
			int spectralLambdaStep = FluxSettings.getModelSpectralLambdaStep();
			useBih = false; //Utils.getBoolean(i, "BIH" , false);
			
			// set spectral discretization
			FluxSpectrum.setDiscretization( new FluxSpectrum.SpectralDiscretization(SPECTRUM_VISIBLE_MIN, SPECTRUM_VISIBLE_MAX, spectralLambdaStep) );
			// set spectral importance
			FluxSpectrum.setImportance(new ConstantSpectralCurve(1.f));
			
			trace();
		}
		finally
		{
			setProgress ("Done", ProgressMonitor.DONE_PROGRESS);
			
			// invoke garbage collector
			Runtime.getRuntime().gc();
		}
	}

	protected abstract void trace() throws IOException;

	protected void displayImage(Buffer imageBuffer, ByteOrder byteOrder)
	{
		setProgress ("Display image", ProgressMonitor.INDETERMINATE_PROGRESS);
		
		// load the hdr image data
        byte hdr_image[] = new byte[width*height*Sizeof.cl_float4];
        imageBuffer.readBuffer(hdr_image);
                
        ByteBuffer imageData = ByteBuffer.wrap(hdr_image);
        imageData.order(byteOrder);
        
        displayImage( imageData.asFloatBuffer() );
	}
	
	protected void displayImage( FloatBuffer hdrBuffer )
	{
		tonemap( hdrBuffer );
        
		for( int i = 0 ; i < width*height; i++ )
        {
        	float r = hdrBuffer.get(i*4+0);
        	float g = hdrBuffer.get(i*4+1);
        	float b = hdrBuffer.get(i*4+2);
        	
        	hdrPixels[0][i] = r;
        	hdrPixels[1][i] = g;
        	hdrPixels[2][i] = b;
        	hdrPixels[3][i] = 1;
        }
		
		// display image
		showImage(image);
	}
	
	private void tonemap(FloatBuffer hdrBuffer) {
		
		int count = 0;
		double sum = 0.f;
		
		int n = 0;
		double mean = 0;
		double M2 = 0;
		double max = 0;
		
		// compute some global image quantities
        for( int i = 0 ; i < width*height; i++ )
        {
        	float r = hdrBuffer.get(i*4+0);
        	float g = hdrBuffer.get(i*4+1);
        	float b = hdrBuffer.get(i*4+2);
        	
        	float luminance = r + g + b; 
  
        	if( luminance > 0 && luminance < Float.MAX_VALUE && !Float.isInfinite(luminance) && !Float.isNaN(luminance) )
        	{
        		sum += luminance;
        		count++;
        		
        		n = n + 1;
        		double delta = luminance - mean;
        		mean = mean + delta/n;
        		M2 = M2 + delta*(luminance - mean); // This expression uses the new value of mean
        	}
        	
        	max = Math.max( max , luminance );
        }
        
        max /= 3;
        
        double variance = M2/(n - 1);
        double deviation = Math.sqrt(variance) / 3;
        double avr = (double)(sum / (count)) / 3;
		
		// tonemap the hdr data
        for( int i = 0 ; i < width*height; i++ )
        {
        	float r = hdrBuffer.get(i*4+0);
        	float g = hdrBuffer.get(i*4+1);
        	float b = hdrBuffer.get(i*4+2);
        	float a = hdrBuffer.get(i*4+3);
        	
        	// apply Mean Value Mapping
        	double fr = (r / avr) * 0.5;
        	double fg = (g / avr) * 0.5;
        	double fb = (b / avr) * 0.5;
        	
        	// apply Deviation Mapping
        	//double fr = (r / (avr + 3.0*deviation)) * 1.5;
        	//double fg = (g / (avr + 3.0*deviation)) * 1.5;
        	//double fb = (b / (avr + 3.0*deviation)) * 1.5;
        	
        	// clamp at zero and map [0,inf] to [0,1]
        	double sr = Math.max(0, fr / (1+fr));
        	double sg = Math.max(0, fg / (1+fg));
        	double sb = Math.max(0, fb / (1+fb));
        	
        	hdrBuffer.put(i*4+0, (float)sr);
        	hdrBuffer.put(i*4+1, (float)sg);
        	hdrBuffer.put(i*4+2, (float)sb);
        	hdrBuffer.put(i*4+3, a);
        }
	}

	private static int toByte (float f)
	{
		int b = (int) (f * 256);
		return (b < 0) ? 0 : (b > 255) ? 255 : b;
	}

	protected static int toIntColor (float r, float g, float b, float a)
	{
		return (toByte (a) << 24) + (toByte (r) << 16) + (toByte (g) << 8)
			+ toByte (b);
	}
	
	/**
	 * displays the rendered image
	 * @param image
	 */
	protected void showImage( BufferedImage image  )
	{
		// inform all observers of new image
		for(ImageObserver observer : observers)
		{
			observer.imageUpdate(image, ImageObserver.ALLBITS, 0, 0, image.getWidth(), image.getHeight());
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	} //showImage

	public void setProgress(String text, float progress) {
		view.getWorkbench().setStatus (this, text);
		if (progress < 0)
		{
			view.getWorkbench().setIndeterminateProgress (this);
		}
		else if (progress == DONE_PROGRESS)
		{
			view.getWorkbench().clearProgress (this);
		}
		else
		{
			view.getWorkbench().setProgress (this, progress);
		}
	}
	
	public void showMessage (String message)
	{
		view.getWorkbench().logGUIInfo(message);
	}
	
	protected String getKernelCompilationArguments( boolean spectral_enabled, boolean dispersion_enabled )
	{
		boolean spectral = FluxSettings.getRenderSpectral();
		boolean dispersion = FluxSettings.getRenderDispersion();
		
		String arguments = "";
		
		if( spectral && spectral_enabled )
		{
			arguments += " -D SPECTRAL";
			if( dispersion && dispersion_enabled )
				arguments += " -D SPECTRUM_DISPERSION";
		}
		
		SpectralDiscretization discrete = FluxSpectrum.getDiscretization();
		
		arguments += " -D SPECTRAL_WAVELENGTH_MIN=" + discrete.getLambdaMin();
		arguments += " -D SPECTRAL_WAVELENGTH_MAX=" + discrete.getLambdaMax();
		arguments += " -D SPECTRAL_WAVELENGTH_BINS=" + discrete.getLambdaBins();
		
		if( useBih )
			arguments += " -D BIH";
		else
			arguments += " -D BVH";
		
		return arguments;
	}
}
