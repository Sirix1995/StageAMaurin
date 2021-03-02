package de.grogra.imp3d.objects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.vecmath.Point3f;

import org.sunflow.image.RGBSpace;
import org.sunflow.image.XYZColor;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.icon.Icon;
import de.grogra.icon.IconSource;
import de.grogra.imp3d.shading.ConstantSPD;
import de.grogra.imp3d.shading.SPD;
import de.grogra.imp3d.shading.SPDCurve;
import de.grogra.imp3d.spectral.ConstantSpectralCurve;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.persistence.ShareableBase;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.RenderedIcon;

public class SpectralLightMapNode  extends ShareableBase
	implements SpectralLightMap, IconSource, Icon, RenderedIcon {

	private static final int MIN_WAVELENGTH = 380;
	private static final int MAX_WAVELENGTH = 720;
		
	//enh:sco de.grogra.persistence.SCOType

	SPD spectraldistribution = new ConstantSPD();
	//enh:field getter setter
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field spectraldistribution$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SpectralLightMapNode representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SpectralLightMapNode) o).spectraldistribution = (SPD) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SpectralLightMapNode) o).getSpectraldistribution ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SpectralLightMapNode ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SpectralLightMapNode.class);
		spectraldistribution$FIELD = Type._addManagedField ($TYPE, "spectraldistribution", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SPD.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public SPD getSpectraldistribution ()
	{
		return spectraldistribution;
	}

	public void setSpectraldistribution (SPD value)
	{
		spectraldistribution$FIELD.setObject (this, value);
	}

//enh:end

	private transient Color awtColor;
	private transient Point3f whitecolor;
	private float maxIntensity;
		
	public Point3f getColor()
	{
		SpectralCurve curve = getSpectralCurve();
		XYZColor xyz = curve.toXYZ();
		Point3f color = new Point3f();
		RGBSpace.CIE.convertXYZtoRGB( xyz, color );
				
		color.x /= whitecolor.x;
		color.y /= whitecolor.y;
		color.z /= whitecolor.z;
		
		return color;
	}

	public SpectralLightMapNode (SpectralCurve curve)
	{
		this();
		setSpectraldistribution( new SPDCurve(curve) );
	}
	
	public SpectralLightMapNode ()
	{
		super();
		// normalize for white
		XYZColor whitexyz = (new ConstantSpectralCurve(1)).toXYZ();
		whitecolor = new Point3f();
		RGBSpace.CIE.convertXYZtoRGB( whitexyz, whitecolor );
	}

	public int getAverageColor ()
	{
		Point3f color = getColor();
		int r = Math.min( Math.max(0, (int)(color.x * 255)) , 0 );
		int g = Math.min( Math.max(0, (int)(color.y * 255)) , 0 );
		int b = Math.min( Math.max(0, (int)(color.z * 255)) , 0 );
		int rgba = r | (g << 8) | (b << 16) | 0xFF000000;
		return rgba;
	}

	public float getFloatValue (ChannelData data, int channel)
	{
		if ((channel >= Channel.MIN_DERIVATIVE)
				&& (channel <= Channel.MAX_DERIVATIVE))
		{
			return 0;
		}

		Point3f color = getColor();
				
		switch (channel & 3)
		{
			case 0:
				return color.x;
			case 1:
				return color.y;
			case 2:
				return color.z;
			case 3:
				return 1;
			default:
				throw new AssertionError ();
		}
	}


	public Object getObjectValue (ChannelData data, int channel)
	{
		return data.forwardGetObjectValue (data.getData (null));
	}


	public Icon getIcon (Dimension size, int state)
	{
		return this;
	}


	public Dimension getPreferredIconSize (boolean small)
	{
		return null;
	}


	public void paintIcon (Component c, Graphics2D g,
						   int x, int y, int w, int h, int state)
	{
		Color old = g.getColor ();
		g.setColor (Color.BLACK);
		g.drawRect (x, y, w - 1, h - 1);
		int i = getAverageColor ();
		if ((awtColor == null) || (i != awtColor.getRGB ()))
		{
			awtColor = new Color (i, true);
		}
		g.setColor (awtColor);
		g.fillRect (x + 1, y + 1, w - 2, h - 2);
		g.setColor (old);
	}


	public IconSource getIconSource ()
	{
		return this;
	}


	public void prepareIcon ()	
	{
	}


	public boolean isMutable ()
	{
		return true;
	}


	public Image getImage ()
	{
		return null;
	}


	public Image getImage (int w, int h)
	{
		return null;
	}


	public java.net.URL getImageSource ()
	{
		return null;
	}


	public Rectangle getIconBounds ()
	{
		return null;
	}

	public float getSizeRatio() {
		return 2.0f;
	}

	public int renderImage(JobManager jm, BufferedImage img, int y) {
		long time = System.currentTimeMillis () + 500;
		
		if( y == 0 )
		{
			SpectralCurve curve = getSpectralCurve();
			
			maxIntensity = 0;
			for( int wavelength = MIN_WAVELENGTH; wavelength < MAX_WAVELENGTH; wavelength++)
				maxIntensity = Math.max(maxIntensity , curve.sample(wavelength));
		}
				
		do
		{
			for (int i = 3; i > 0; i--)
			{				
				if (y == img.getHeight ())
				{
					return y;
				}
			
				renderLine( y, img );
								
				if (++y == img.getHeight ())
				{
					return y;
				}
			}
		} while ((System.currentTimeMillis () < time)
				 && !jm.hasJobQueued (jm.getThreadContext ().getPriority () + 1));
		return y;
	}
	
	SpectralCurve getSpectralCurve()
	{
		SpectralCurve curve = null;
		if( spectraldistribution != null )
			curve = spectraldistribution.getSpectralDistribution();
		if( curve == null )
			curve = new ConstantSpectralCurve(1.0f);
		return curve;
	}

	private void renderLine(int y, BufferedImage img) {
		
		SpectralCurve curve = getSpectralCurve();
		
		img.setRGB (0, y, 0xFF000000 );
		img.setRGB (1, y, 0xFFFFFFFF );
		img.setRGB (img.getWidth ()-2, y, 0xFFFFFFFF );
		img.setRGB (img.getWidth ()-1, y, 0xFF000000 );
		
		int spectralLambdaResolution = FluxSettings.getModelSpectralLambdaStep();
		float spectralPixelResolution = (MAX_WAVELENGTH-MIN_WAVELENGTH) / (float)(img.getWidth()-4);
		
		int currentWavelength = 0;
		for (int ix = 2; ix < img.getWidth () - 2; ix++)
		{
			float wavelength = (float)(ix-2) * spectralPixelResolution + MIN_WAVELENGTH; 
			
			XYZColor xyz = SpectralCurve.toXYZ(wavelength, 1.f);
			org.sunflow.image.Color color = RGBSpace.CIE.convertXYZtoRGB( xyz );
			
			int rgba = color.toRGB() | 0xFF000000;
			
			// if the screen resolution is higher than the spectral resulution, interpolate
			float intensity;
			if( spectralPixelResolution < spectralLambdaResolution )
			{
				intensity = curve.sample(wavelength);
			}
			else
			{
				// if the screen resolution is lower than the spectral resulution, integrate
				int range = 1;
				intensity = curve.sample(currentWavelength);
				
				for( ; currentWavelength < wavelength ; currentWavelength++, range++ )
					intensity += curve.sample(currentWavelength);
					
				intensity /= range;
			}
			
			intensity *= img.getHeight() / maxIntensity;
			int height = (int) intensity; 
			
			int iy = img.getHeight() - y; 
			
			if( curve != null )
			{
				if( iy < height )
				{
					
				}
				else if( iy == height )
				{
					rgba = 0xFFFFFFFF;
				}
				else if( iy == height + 1 )
				{
					rgba = 0xFF000000;
				}
				else
				{
					rgba = 0xFFFFFFFF;
				}
			}
			
			
			img.setRGB (ix, y, rgba );
		}
	}

	public SpectralCurve getSpectralDistribution() {
		return spectraldistribution.getSpectralDistribution();
	}
		
	
}
