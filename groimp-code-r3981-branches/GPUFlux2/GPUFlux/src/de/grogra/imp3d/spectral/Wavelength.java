/*
 * [Wavelength.java]
 *
 * Summary: Create a Color object given the wavelength of the colour in nanometers.
 *
 * Copyright: (c) 1998-2013 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.6+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.2 2007-08-13 convert to JDK 1.5, add pad, add ANT build script,
 *                 add icon.
 *  1.3 2007-11-27 correct slight error in speed of light.
 */
package de.grogra.imp3d.spectral;

import java.awt.Color;

/**
 * Create a Color object given the wavelength of the colour in nanometers.
 * <p/>
 * instead of: Color c = new Color(255, 0, 0); use the freqency in nanometers, and gamma 0.0. .. 1.0. Color c =
 * Wavelength.wlColor( 400.0f, 0.80f );
 * <p/>
 * or using frequency in Terahertz and gamma 0.0. .. 1.0. Color c = Wavelength.fColor( 500.0f, 1.0f );
 * <p/>
 * You might use it to draw a realistic rainbow, or to write educational Applets about the light spectrum.
 * <p/>
 * Based on a Fortran program by Dan Bruton (astro@tamu.edu) The original Fortran can be found at:
 * http://www.isc.tamu.edu/~astro/color.html It uses linear interpolation on ten spectral bands.
 * <p/>
 * This code goes from Waveleng or Terahertz to RGB. It does not currently support going the other way.
 * There is a theoretical problem with going the other way.  Most RGB colours are not pure spectrum colours.
 * You could go the other way by:
 * 1. creating an exhaustive table of RGB:terahertz pairs using terahertz to convert to RGB.
 * 2. Sort by RGB
 * 3. eliminate RGB duplicates keeping the median/average terahertz value.
 * 4. You do the above once to create the table.
 * 5. An run time, you do a binary search lookup on RGB to find the terahertz value.
 * If you don't find an exact RGB match, you announce the RGB is impure.
 * 6. You might then search for one-unit off values in RGB + or -, (14 possibilities).
 * <p/>
 * @noinspection WeakerAccess
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2007-11-27 correct slight error in speed of light.
 * @since 1998
 */
public final class Wavelength
    {
    // ------------------------------ CONSTANTS ------------------------------

    /**
     * true if want debugging code.
     */
    private static final boolean DEBUGGING = true;

    private static final int FIRST_COPYRIGHT_YEAR = 1998;

    /**
     * embedded copyright not displayed
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 1998-2013 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * date this verzsion released.
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private static final String RELEASE_DATE = "2007-11-27";

    /**
     * title.
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private static final String TITLE_STRING = "Wavelength";

    /**
     * version.
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private static final String VERSION_STRING = "1.3";

    // -------------------------- PUBLIC STATIC METHODS --------------------------

    /**
     * Creates a Color object given the frequency instead of the usual RGB or HSB values.
     *
     * @param freq  frequency of the light in Terahertz (10.0e12 Hz) Will show up black outside the range 384..789
     *              TeraHertz.
     * @param gamma 0.0 .. 1.0 intensity.
     *
     * @return Color object of that frequency and intensity.
     * @noinspection SameParameterValue
     */
    public static final Color fColor( float freq, float gamma )
        {
        // speed of light is 299,792,458 meters/sec
        // = 2.99792458e08 m/sec
        // = 2.99792458e17 nm/sec
        // = 2.9979e458e05 nm/picosecond
        // = 299,792.458 nm/picosecond
        // 1 Terahertz = 1 cycle per picosecond
        return wvColor( 299792.458f / freq, gamma );
        }// end fColor

    /**
     * Creates    a Color object given the wavelength instead of the usual RGB or HSB values.
     *
     * @param wl    wavelength of the light in nanometers. Will show up black outside the range 380..780 nanometers.
     * @param gamma 0.0 .. 1.0 intensity.
     *
     * @return Color object with tha tha wavelength and intensity
     * @noinspection StandardVariableNames
     */
    public static final Color wvColor( float wl, float gamma )
        {
        /**
         * red, green, blue component in range 0.0 .. 1.0.
         */
        float r = 0;
        float g = 0;
        float b = 0;
        /**
         * intensity 0.0 .. 1.0
         * based on drop off in vision at low/high wavelengths
         */
        float s = 1;
        /**
         * We use different linear interpolations on different bands.
         * These numbers mark the upper bound of each band.
         * Wavelengths of the various bandbs.
         */
        final float[] bands =
                { 380, 420, 440, 490, 510, 580, 645, 700, 780, Float.MAX_VALUE };
        /**
         * Figure out which band we fall in.  A point on the edge
         * is considered part of the lower band.
         */
        int band = bands.length - 1;
        for ( int i = 0; i < bands.length; i++ )
            {
            if ( wl <= bands[ i ] )
                {
                band = i;
                break;
                }
            }
        switch ( band )
            {
            case 0:
                /* invisible below 380 */
                // The code is a little redundant for clarity.
                // A smart optimiser can remove any r=0, g=0, b=0.
                r = 0;
                g = 0;
                b = 0;
                s = 0;
                break;
            case 1:
                /* 380 .. 420, intensity drop off. */
                r = ( 440 - wl ) / ( 440 - 380 );
                g = 0;
                b = 1;
                s = .3f + .7f * ( wl - 380 ) / ( 420 - 380 );
                break;
            case 2:
                /* 420 .. 440 */
                r = ( 440 - wl ) / ( 440 - 380 );
                g = 0;
                b = 1;
                break;
            case 3:
                /* 440 .. 490 */
                r = 0;
                g = ( wl - 440 ) / ( 490 - 440 );
                b = 1;
                break;
            case 4:
                /* 490 .. 510 */
                r = 0;
                g = 1;
                b = ( 510 - wl ) / ( 510 - 490 );
                break;
            case 5:
                /* 510 .. 580 */
                r = ( wl - 510 ) / ( 580 - 510 );
                g = 1;
                b = 0;
                break;
            case 6:
                /* 580 .. 645 */
                r = 1;
                g = ( 645 - wl ) / ( 645 - 580 );
                b = 0;
                break;
            case 7:
                /* 645 .. 700 */
                r = 1;
                g = 0;
                b = 0;
                break;
            case 8:
                /* 700 .. 780, intensity drop off */
                r = 1;
                g = 0;
                b = 0;
                s = .3f + .7f * ( 780 - wl ) / ( 780 - 700 );
                break;
            case 9:
                /* invisible above 780 */
                r = 0;
                g = 0;
                b = 0;
                s = 0;
                break;
            }// end switch
        // apply intensity and gamma corrections.
        s *= gamma;
        r *= s;
        g *= s;
        b *= s;
        return new Color( r, g, b );
        }// end wvColor


    /**
     * Converts a given color to its integer value.
     * 
     * @param c color 
     * @return color as integer 0xFF000000 for 100% Alpha
     */
	public static final int color2int(Color c){
		int red =  c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		red = (red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
		green = (green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
		blue = blue & 0x000000FF; //Mask out anything not blue.
		return 0xFF000000 | red | green | blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
	}

	/**
	 * Converts a given color represented by its RGB color values to its integer value.
	 * 
	 * @param red channel [0,255] of the default sRGB space 
	 * @param green channel [0,255] of the default sRGB space
	 * @param blue channel [0,255] of the default sRGB space
	 * @return color as integer 0xFF000000 for 100% Alpha
	 */
	public static final int color2int(int red, int green, int blue){
		red = (red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
		green = (green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
		blue = blue & 0x000000FF; //Mask out anything not blue.
		return 0xFF000000 | red | green | blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
	}

}
