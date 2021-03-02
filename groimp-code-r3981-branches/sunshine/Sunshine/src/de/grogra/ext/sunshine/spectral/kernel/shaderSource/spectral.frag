#define IlluminantC     0.3101, 0.3162	    	/* For NTSC television 			*/
#define IlluminantD65   0.3127, 0.3291	    	/* For EBU and SMPTE 			*/
#define IlluminantE 	0.33333333, 0.33333333	/* CIE equal-energy illuminant 	*/

#define GAMMA_REC709	0.0						/* Rec. 709 */

uniform sampler2DRect matchTable;
uniform int tileX;

int lamdaMin 			= 360;					// D65: 300	// RGB: 390	// XYZ: 360
int lamdaMax 			= 830;			
int lamdaDelta 			= lamdaMax - lamdaMin; 	// D65: 530	// RGB: 440 // XYZ: 470
int lamdaSteps			= 1;

int colorMatchTexSize 	= 39;					// D65:	42	// RGB: 18	// XYZ: 39
int coloMatchVaules		= lamdaDelta * 3; 					// RGB: 267 // XYZ: 1413 

struct colourSystem 
{
    float xRed, yRed,	    	    /* Red x, y 		*/
           xGreen, yGreen,  	    /* Green x, y 		*/
           xBlue, yBlue,    	    /* Blue x, y 		*/
           xWhite, yWhite,  	    /* White point x, y	*/
	   gamma;   	    	    	/* Gamma correction for system */
};

	/* Name                  	xRed    yRed    xGreen  yGreen  xBlue  yBlue    White point        Gamma   */
colourSystem                
    NTSCsystem  =  colourSystem(0.67,   0.33,   0.21,   0.71,   0.14,   0.08,   IlluminantC,    GAMMA_REC709),
    EBUsystem   =  colourSystem(0.64,   0.33,   0.29,   0.60,   0.15,   0.06,   IlluminantD65,  GAMMA_REC709),
    SMPTEsystem =  colourSystem(0.630,  0.340,  0.310,  0.595,  0.155,  0.070,  IlluminantD65,  GAMMA_REC709),
    HDTVsystem  =  colourSystem(0.670,  0.330,  0.210,  0.710,  0.150,  0.060,  IlluminantD65,  GAMMA_REC709),
    CIEsystem   =  colourSystem(0.7355, 0.2645, 0.2658, 0.7243, 0.1669, 0.0085, IlluminantE,    GAMMA_REC709),
    Rec709system = colourSystem(0.64,   0.33,   0.30,   0.60,   0.15,   0.06,   IlluminantD65,  GAMMA_REC709);


vec3 getXYZMatch(int lamda)
{
	if(lamda < lamdaMin)
		lamda = lamdaMin;
		
	if(lamda > lamdaMax)	
		lamda = lamdaMax;
	
	int pos 		= int((lamda - 360) / float(lamdaSteps));	

	float line 		= int(float(pos) / float(colorMatchTexSize));
	float linePos 	= float(pos) - float(colorMatchTexSize * line);	
		
	return texture2DRect(matchTable, vec2(linePos, line) ).xyz;
}

/*                            BB_SPECTRUM

    Calculate, by Planck's radiation law, the emittance of a black body
    of temperature bbTemp at the given wavelength (in metres).  */

float bbTemp = 10000;                 /* Hidden temperature argument
                                         to BB_SPECTRUM. */
float bb_spectrum(int wavelength)
{
    float wlm = wavelength * 1e-9;   /* Wavelength in meters */

    return (3.74183e-16 * pow(wlm, -5.0)) /
           (exp(1.4388e-2 / (wlm * bbTemp)) - 1.0);
}

vec3 xyz_to_rgb(colourSystem cs, vec3 xyzC)
{
	float xc = xyzC.x; float yc = xyzC.y; float zc = xyzC.z;
    float xr, yr, zr, xg, yg, zg, xb, yb, zb;
    float xw, yw, zw;
    float rx, ry, rz, gx, gy, gz, bx, by, bz;
    float rw, gw, bw;
    
    vec3 rgb = vec3(0.0);

    xr = cs.xRed;    yr = cs.yRed;    zr = 1 - (xr + yr);
    xg = cs.xGreen;  yg = cs.yGreen;  zg = 1 - (xg + yg);
    xb = cs.xBlue;   yb = cs.yBlue;   zb = 1 - (xb + yb);

    xw = cs.xWhite;  yw = cs.yWhite;  zw = 1 - (xw + yw);

    /* xyz -> rgb matrix, before scaling to white. */
    
    rx = (yg * zb) - (yb * zg);  ry = (xb * zg) - (xg * zb);  rz = (xg * yb) - (xb * yg);
    gx = (yb * zr) - (yr * zb);  gy = (xr * zb) - (xb * zr);  gz = (xb * yr) - (xr * yb);
    bx = (yr * zg) - (yg * zr);  by = (xg * zr) - (xr * zg);  bz = (xr * yg) - (xg * yr);

    /* White scaling factors.
       Dividing by yw scales the white luminance to unity, as conventional. */
       
    rw = ((rx * xw) + (ry * yw) + (rz * zw)) / yw;
    gw = ((gx * xw) + (gy * yw) + (gz * zw)) / yw;
    bw = ((bx * xw) + (by * yw) + (bz * zw)) / yw;

    /* xyz -> rgb matrix, correctly scaled to white. */
    
    rx = rx / rw;  ry = ry / rw;  rz = rz / rw;
    gx = gx / gw;  gy = gy / gw;  gz = gz / gw;
    bx = bx / bw;  by = by / bw;  bz = bz / bw;

    /* rgb of the desired point */
    
    rgb.x = (rx * xc) + (ry * yc) + (rz * zc);
    rgb.y = (gx * xc) + (gy * yc) + (gz * zc);
    rgb.z = (bx * xc) + (by * yc) + (bz * zc);
    
    return rgb;
}

/*                            INSIDE_GAMUT

     Test whether a requested colour is within the gamut
     achievable with the primaries of the current colour
     system.  This amounts simply to testing whether all the
     primary weights are non-negative. */

bool inside_gamut(float r, float g, float b)
{
    return (r >= 0) && (g >= 0) && (b >= 0);
}


/*                          CONSTRAIN_RGB

    If the requested RGB shade contains a negative weight for
    one of the primaries, it lies outside the colour gamut 
    accessible from the given triple of primaries.  Desaturate
    it by adding white, equal quantities of R, G, and B, enough
    to make RGB all positive.  The function returns 1 if the
    components were modified, zero otherwise.
    
*/

bool constrain_rgb(out vec3 rgb)
{
    float w;

    /* Amount of white needed is w = - min(0, r, g, b) */
    
    w = (0.0 < rgb.x) ? 0.0 : rgb.x;
    w = (w < rgb.y) ? w : rgb.y;
    w = (w < rgb.z) ? w : rgb.z;
    w = -w;

    /* Add just enough white to make r, g, b all positive. */
    
    if (w > 0) {
        rgb += w;
        return true;                     /* Colour modified to fit RGB gamut */
    }

    return false;                         /* Colour within RGB gamut */
}

/*                          GAMMA_CORRECT_RGB

    Transform linear RGB values to nonlinear RGB values. Rec.
    709 is ITU-R Recommendation BT. 709 (1990) ``Basic
    Parameter Values for the HDTV Standard for the Studio and
    for International Programme Exchange'', formerly CCIR Rec.
    709. For details see
    
       http://www.poynton.com/ColorFAQ.html
       http://www.poynton.com/GammaFAQ.html
*/

void gamma_correct(colourSystem cs, out float c)
{
    float gamma;

    gamma = cs.gamma;

    if (gamma == GAMMA_REC709) {
	/* Rec. 709 gamma correction. */
	float cc = 0.018;
	
	if (c < cc) {
	    c *= ((1.099 * pow(cc, 0.45)) - 0.099) / cc;
	} else {
	    c = (1.099 * pow(c, 0.45)) - 0.099;
	}
    } else {
	/* Nonlinear colour = (Linear colour)^(1/gamma) */
	c = pow(c, 1.0 / gamma);
    }
}

void gamma_correct_rgb(colourSystem cs, out vec3 rgb)
{
    gamma_correct(cs, rgb.x);
    gamma_correct(cs, rgb.y);
    gamma_correct(cs, rgb.z);
}

/*  	    	    	    NORM_RGB

    Normalise RGB components so the most intense (unless all
    are zero) has a value of 1.
    
*/

void norm_rgb(out vec3 rgb)
{

    float greatest = max(rgb.x, max(rgb.y, rgb.z));
    
    if (greatest > 0) {
    	rgb /= greatest;
    }

}

vec3 getMatchFunc(int lamda)
{		
	vec3 xyz, rgb;
	vec3 XYZ, RGB;
	
/*	NTSCsystem
    EBUsystem 
    SMPTEsystem
    HDTVsystem 
    CIEsystem  
    Rec709system */
	
	colourSystem cs	= CIEsystem ;
	
	XYZ 			= bb_spectrum(lamda) *  getXYZMatch(lamda);

	float sumXYZ 	= XYZ.x + XYZ.y + XYZ.z;
	
	xyz 			= vec3(0.0);
	
	xyz.x 			= XYZ.x / sumXYZ;
	xyz.y 			= XYZ.y / sumXYZ;
	xyz.z 			= XYZ.z / sumXYZ;	
	
	rgb 			= xyz_to_rgb(cs, xyz);
	
/*	rgb.x			*= 0.2989;
	rgb.y			*= 0.5866;
	rgb.z			*= 0.1145;*/
	
	//rgb				/= rgb.x * 0.2989 + rgb.y * 0.5866 + rgb.z * 0.1145;
	
	constrain_rgb(rgb);
	
	
	norm_rgb(rgb);
		
	//gamma_correct_rgb(cs, rgb);

	return rgb;
	
	
}

void main()
{
	vec2 texCoor	= gl_TexCoord[0].xy;
	float xPos		= float(tileX * tileWidth) + texCoor.x; 
	float relPos	= xPos / float(imageWidth);
	
	int lamda		= int( int(relPos  * (float(lamdaDelta))) + lamdaMin);
	
	//gl_FragData[0] 	= vec4(xPos); 
	//gl_FragData[0] 	= vec4(lamda); 
	gl_FragData[0] 	= vec4( getMatchFunc(lamda), lamda ); 
	//gl_FragData[0] 	= vec4( getMatchFunc(lamda), 1.0 ); 
}