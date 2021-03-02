
package de.grogra.imp3d.shading;

import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.sunflow.image.ChromaticitySpectrum;
import org.sunflow.image.ConstantSpectralCurve;
import org.sunflow.image.IrregularSpectralCurve;
import org.sunflow.image.RGBSpace;
import org.sunflow.image.RegularSpectralCurve;
import org.sunflow.image.SpectralCurve;
import org.sunflow.math.MathUtils;

import de.grogra.math.Tuple3dType;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class SunSkyLight extends Material implements Light
{
	// parameters to the model

	final Vector3d sun = new Vector3d (0, 1, 0.4);
	//enh:field type=Tuple3dType.VECTOR set=set getter setter

	float turbidity = 6;
	//enh:field getter setter

	float radianceFactor = 1;
	//enh:field getter setter

	boolean disableLight = false;
	//enh:field getter setter

	boolean disableSun = false;
	//enh:field getter setter

	// sunflow parameters
	private final int numSkySamples = 64;
	// derived quantities

	private int derivedStamp = -1;

	private final Vector3d sunDir = new Vector3d ();
	
	private final Matrix3d sunBasis = new Matrix3d ();

	/**
	 * Spectral radiance of sun, unit is W/(m² nm sr)
	 */
	private SpectralCurve sunSpectralRadiance;

	/**
	 * Radiance of sun, unit is W/(m² sr). Irradiance (W/m²) is obtained by
	 * the multiplication with {@link #sunSolidAngle}.
	 */
	private Spectrum3f sunColor;

	private double sunTheta;
	private double cosSunTheta;
	private double zenithY, zenithx, zenithy;
	private final double[] perezY = new double[5];
	private final double[] perezx = new double[5];
	private final double[] perezy = new double[5];
	private double jacobian;
	private final Spectrum3f irradiance = new Spectrum3f ();
	private final Spectrum3f invIrradiance = new Spectrum3f ();
	private float sunFraction;

	private double[] lumColHistogram;
	private double[][] lumImageHistogram;

	private double[] colHistogram;
	private double[][] imageHistogram;

	// constant data
	// the unit of solAmplitudes is 100 W/(m² nm sr)
	private static final float[] solAmplitudes = {165.5f, 162.3f, 211.2f,
			258.8f, 258.2f, 242.3f, 267.6f, 296.6f, 305.4f, 300.6f, 306.6f,
			288.3f, 287.1f, 278.2f, 271.0f, 272.3f, 263.6f, 255.0f, 250.6f,
			253.1f, 253.5f, 251.3f, 246.3f, 241.7f, 236.8f, 232.1f, 228.2f,
			223.4f, 219.7f, 215.3f, 211.0f, 207.3f, 202.4f, 198.7f, 194.3f,
			190.7f, 186.3f, 182.6f};
	private static final RegularSpectralCurve solCurve = new RegularSpectralCurve (
		solAmplitudes, 380, 750);
	private static final float[] k_oWavelengths = {300, 305, 310, 315, 320,
			325, 330, 335, 340, 345, 350, 355, 445, 450, 455, 460, 465, 470,
			475, 480, 485, 490, 495, 500, 505, 510, 515, 520, 525, 530, 535,
			540, 545, 550, 555, 560, 565, 570, 575, 580, 585, 590, 595, 600,
			605, 610, 620, 630, 640, 650, 660, 670, 680, 690, 700, 710, 720,
			730, 740, 750, 760, 770, 780, 790,};
	private static final float[] k_oAmplitudes = {10.0f, 4.8f, 2.7f, 1.35f,
			.8f, .380f, .160f, .075f, .04f, .019f, .007f, .0f, .003f, .003f,
			.004f, .006f, .008f, .009f, .012f, .014f, .017f, .021f, .025f,
			.03f, .035f, .04f, .045f, .048f, .057f, .063f, .07f, .075f, .08f,
			.085f, .095f, .103f, .110f, .12f, .122f, .12f, .118f, .115f, .12f,
			.125f, .130f, .12f, .105f, .09f, .079f, .067f, .057f, .048f, .036f,
			.028f, .023f, .018f, .014f, .011f, .010f, .009f, .007f, .004f, .0f,
			.0f};
	private static final float[] k_gWavelengths = {759, 760, 770, 771};
	private static final float[] k_gAmplitudes = {0, 3.0f, 0.210f, 0};
	private static final float[] k_waWavelengths = {689, 690, 700, 710, 720,
			730, 740, 750, 760, 770, 780, 790, 800};
	private static final float[] k_waAmplitudes = {0f, 0.160e-1f, 0.240e-1f,
			0.125e-1f, 0.100e+1f, 0.870f, 0.610e-1f, 0.100e-2f, 0.100e-4f,
			0.100e-4f, 0.600e-3f, 0.175e-1f, 0.360e-1f};

	private static final IrregularSpectralCurve k_oCurve = new IrregularSpectralCurve (
		k_oWavelengths, k_oAmplitudes);
	private static final IrregularSpectralCurve k_gCurve = new IrregularSpectralCurve (
		k_gWavelengths, k_gAmplitudes);
	private static final IrregularSpectralCurve k_waCurve = new IrregularSpectralCurve (
		k_waWavelengths, k_waAmplitudes);

	private static final double sunAngle = Math.PI * 32 / (60 * 180);

	private static final double cosHalfSunAngle = 1 - sunAngle * sunAngle / 8;

	private static final double sunSolidAngle = 2 * Math.PI * (1 - cosHalfSunAngle);


	public SunSkyLight ()
	{
	}

	private void update ()
	{
		if (derivedStamp == getStamp ())
		{
			return;
		}
		// recompute model
		initSunSky ();
		derivedStamp = getStamp ();
	}

	private static SpectralCurve computeAttenuatedSunlight (double theta,
			double turbidity)
	{
		float[] data = new float[91]; // holds the sunsky curve data
		final double alpha = 1.3;
		final double lozone = 0.35;
		final double w = 2.0;
		double beta = 0.04608365822050 * turbidity - 0.04586025928522;
		// Relative optical mass
		double m = 1.0 / (Math.cos (theta) + 0.000940 * Math.pow (
			1.6386 - theta, -1.253));
		for (int i = 0, lambda = 350; lambda <= 800; i++, lambda += 5)
		{
			// Rayleigh scattering
			double tauR = Math.exp (-m * 0.008735
				* Math.pow (lambda / 1000.0, -4.08));
			// Aerosol (water + dust) attenuation
			double tauA = Math.exp (-m * beta
				* Math.pow (lambda / 1000.0, -alpha));
			// Attenuation due to ozone absorption
			double tauO = Math.exp (-m * k_oCurve.sample (lambda) * lozone);
			// Attenuation due to mixed gases absorption
			double tauG = Math.exp (-1.41 * k_gCurve.sample (lambda) * m
				/ Math.pow (1.0 + 118.93 * k_gCurve.sample (lambda) * m, 0.45));
			// Attenuation due to water vapor absorption
			double tauWA = Math.exp (-0.2385
				* k_waCurve.sample (lambda)
				* w
				* m
				/ Math.pow (1.0 + 20.07 * k_waCurve.sample (lambda) * w * m,
					0.45));
			// 100.0 comes from solAmplitudes begin in wrong units.
			double amp = 100.0 * solCurve.sample (lambda) * tauR * tauA
				* tauO * tauG * tauWA;
			data[i] = (float) amp;
		}
		return new RegularSpectralCurve (data, 350, 800);
	}

	private double perezFunction (final double[] lam, double cosTheta,
			double gamma, double cosGamma, double lvz)
	{
		double den = ((1.0 + lam[0] * Math.exp (lam[1])) * (1.0 + lam[2]
			* Math.exp (lam[3] * sunTheta) + lam[4] * cosSunTheta
			* cosSunTheta));
		double num = ((1.0 + lam[0] * Math.exp (lam[1] / cosTheta)) * (1.0
			+ lam[2] * Math.exp (lam[3] * gamma) + lam[4] * cosGamma
			* cosGamma));
		return lvz * num / den;
	}

	static float getLuminance (float r, float g, float b)
	{
		return 0.2989f * r + 0.5866f * g + 0.1145f * b;
	}

	private void initSunSky ()
	{
		// perform all the required initialization of constants
		sunDir.set (sun);
		sunDir.normalize ();
		Math2.getOrthogonalBasis (sunDir, sunBasis, true);
		cosSunTheta = MathUtils.clamp (sunDir.z, -1, 1);
		sunTheta = Math.acos (cosSunTheta);
		sunColor = new Spectrum3f ();
		if (sunDir.z > 0)
		{
			sunSpectralRadiance = computeAttenuatedSunlight (sunTheta,
				turbidity);
			// produce color suitable for rendering
			RGBSpace.SRGB.convertXYZtoRGB (sunSpectralRadiance.toXYZ (), sunColor);
			constrainRGB (sunColor);
		}
		else
		{
			sunSpectralRadiance = new ConstantSpectralCurve (0);
			sunColor.setZero ();
		}
		double theta2 = sunTheta * sunTheta;
		double theta3 = sunTheta * theta2;
		double T = turbidity;
		double T2 = turbidity * turbidity;
		double chi = (4.0 / 9.0 - T / 120.0) * (Math.PI - 2.0 * sunTheta);
		zenithY = (4.0453 * T - 4.9710) * Math.tan (chi) - 0.2155 * T + 2.4192;
		zenithY *= 1000; /* conversion from kcd/m^2 to cd/m^2 */
		zenithx = (0.00165 * theta3 - 0.00374 * theta2 + 0.00208 * sunTheta + 0)
			* T2
			+ (-0.02902 * theta3 + 0.06377 * theta2 - 0.03202 * sunTheta + 0.00394)
			* T
			+ (0.11693 * theta3 - 0.21196 * theta2 + 0.06052 * sunTheta + 0.25885);
		zenithy = (0.00275 * theta3 - 0.00610 * theta2 + 0.00316 * sunTheta + 0)
			* T2
			+ (-0.04212 * theta3 + 0.08970 * theta2 - 0.04153 * sunTheta + 0.00515)
			* T
			+ (0.15346 * theta3 - 0.26756 * theta2 + 0.06669 * sunTheta + 0.26688);

		perezY[0] = 0.17872 * T - 1.46303;
		perezY[1] = -0.35540 * T + 0.42749;
		perezY[2] = -0.02266 * T + 5.32505;
		perezY[3] = 0.12064 * T - 2.57705;
		perezY[4] = -0.06696 * T + 0.37027;

		perezx[0] = -0.01925 * T - 0.25922;
		perezx[1] = -0.06651 * T + 0.00081;
		perezx[2] = -0.00041 * T + 0.21247;
		perezx[3] = -0.06409 * T - 0.89887;
		perezx[4] = -0.00325 * T + 0.04517;

		perezy[0] = -0.01669 * T - 0.26078;
		perezy[1] = -0.09495 * T + 0.00921;
		perezy[2] = -0.00792 * T + 0.21023;
		perezy[3] = -0.04405 * T - 1.65369;
		perezy[4] = -0.01092 * T + 0.05291;

		final int w = 32, h = 32;
		lumImageHistogram = new double[w][h];
		lumColHistogram = new double[w];
		imageHistogram = new double[w][h];
		colHistogram = new double[w];
		double du = 1.0f / w;
		double dv = 1.0f / h;
		Color3f c = new Color3f ();
		Vector3d dir = new Vector3d ();
		Color3f sum = new Color3f ();
		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				double u = (x + 0.5f) * du;
				double v = (y + 0.5f) * dv;
				getDirection (u, v, dir);
				getSkyRGB (dir, c);
				double sin = Math.sin (Math.PI * v);
				imageHistogram[x][y] = (c.x + c.y + c.z) * sin;
				sum.scaleAdd ((float) sin, c, sum);
				lumImageHistogram[x][y] = getLuminance (c.x, c.y, c.z) * sin;
				if (y > 0)
				{
					imageHistogram[x][y] += imageHistogram[x][y - 1];
					lumImageHistogram[x][y] += lumImageHistogram[x][y - 1];
				}
			}
			colHistogram[x] = imageHistogram[x][h - 1];
			lumColHistogram[x] = lumImageHistogram[x][h - 1];
			if (x > 0)
			{
				colHistogram[x] += colHistogram[x - 1];
				lumColHistogram[x] += lumColHistogram[x - 1];
			}
			for (int y = 0; y < h; y++)
			{
				imageHistogram[x][y] /= imageHistogram[x][h - 1];
				lumImageHistogram[x][y] /= lumImageHistogram[x][h - 1];
			}
		}
		jacobian = (2 * Math.PI * Math.PI) / (w * h);
		irradiance.scale ((float) sunSolidAngle, sunColor);
		double sunIrradiance = irradiance.integrate ();
		irradiance.scaleAdd ((float) jacobian, sum, irradiance);
		invIrradiance.x = 1 / irradiance.x;
		invIrradiance.y = 1 / irradiance.y;
		invIrradiance.z = 1 / irradiance.z;
		sunFraction = (float) (sunIrradiance / irradiance.integrate ());
		for (int x = 0; x < w; x++)
		{
			colHistogram[x] /= colHistogram[w - 1];
			lumColHistogram[x] /= lumColHistogram[w - 1];
		}
	}

	private boolean getSkyRGB (Vector3d dir, Tuple3f color)
	{
		if (dir.z < 0)
		{
			color.set (0, 0, 0);
			return false;
		}
		if (dir.z < 0.001f)
			dir.z = 0.001f;
		dir.normalize ();
		double cosTheta = MathUtils.clamp (dir.z, -1, 1);
		double cosGamma = MathUtils.clamp (dir.dot (sunDir), -1, 1);
		double gamma = Math.acos (cosGamma);
		double x = perezFunction (perezx, cosTheta, gamma, cosGamma, zenithx);
		double y = perezFunction (perezy, cosTheta, gamma, cosGamma, zenithy);
		double Y = perezFunction (perezY, cosTheta, gamma, cosGamma, zenithY);
		ChromaticitySpectrum.get ((float) x, (float) y, color);

		// 683.002: conversion from cd/m² to W/m²sr
		color.scale ((float) Y / (color.y * 683.002f));

		RGBSpace.SRGB.convertXYZtoRGB (color, color);
		return cosGamma >= cosHalfSunAngle;
	}

	@Override
	public void generateRandomRays (Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random)
	{
		update ();
		double[] colHistogram;
		double[][] imageHistogram;
		if (env.type == Environment.RADIATION_MODEL)
		{
			colHistogram = this.colHistogram;
			imageHistogram = this.imageHistogram;
		}
		else
		{
			colHistogram = this.lumColHistogram;
			imageHistogram = this.lumImageHistogram;
		}
		Vector3d tmp = env.tmpVector0;
		Vector3f color = env.userVector;
		Matrix3d basis = env.tmpMatrix30;

		float sunRaysFloat = rays.size () * sunFraction;
		int sunRays = (int) sunRaysFloat;
		if (random.nextFloat () < sunRaysFloat - sunRays)
		{
			sunRays++;
		}
		int c = 0;
		for (int i = 0; i < rays.size (); i++)
		{
			Ray ray = rays.rays[i];
			c += sunRays;
			if (c >= rays.size ())
			{
				c -= rays.size ();
				int j = random.nextInt ();
				// choose a random direction to solar disk
				float cost = (float) ((1 - cosHalfSunAngle) / 0x20000) * (2 * (j >>> 16) + 1) + (float) cosHalfSunAngle,
					sint = (float) Math.sqrt (1 - cost * cost);
				char phi = (char) j;
				tmp.set (Math2.ccos (phi) * sint, Math2.csin (phi) * sint, -cost);
				sunBasis.transform (tmp);
				ray.direction.set (tmp);
				ray.spectrum.set (specOut);
				ray.spectrum.mul ((Tuple3f) sunColor);
				ray.spectrum.mul ((Tuple3f) invIrradiance);
				ray.spectrum.scale (sunSolidAngle / sunFraction);
				ray.directionDensity = (float) (sunFraction / sunSolidAngle);
			}
			else
			{
				double randX = random.nextDouble ();
				double randY = random.nextDouble ();

				int x = 0;
				while (randX >= colHistogram[x] && x < colHistogram.length - 1)
					x++;
				double[] rowHistogram = imageHistogram[x];
				int y = 0;
				while (randY >= rowHistogram[y] && y < rowHistogram.length - 1)
					y++;
				// sample from (x, y)
				double u = ((x == 0) ? (randX / colHistogram[0])
						: ((randX - colHistogram[x - 1]) / (colHistogram[x] - colHistogram[x - 1])));
				double v = ((y == 0) ? (randY / rowHistogram[0])
						: ((randY - rowHistogram[y - 1]) / (rowHistogram[y] - rowHistogram[y - 1])));

				double px = ((x == 0) ? colHistogram[0]
						: (colHistogram[x] - colHistogram[x - 1]));
				double py = ((y == 0) ? rowHistogram[0]
						: (rowHistogram[y] - rowHistogram[y - 1]));

				double su = (x + u) / colHistogram.length;
				double sv = (y + v) / rowHistogram.length;
				double sin = getDirection (su, sv, tmp);
				getSkyRGB (tmp, color);
				ray.directionDensity = (1 - sunFraction) * (float) (px * py / (sin * jacobian));
				Math2.mul (color, color, invIrradiance);
				color.scale (1 / ray.directionDensity);
				ray.spectrum.set (specOut);
				ray.spectrum.mul (color);
				ray.direction.set (tmp);
				ray.direction.negate ();
			}
			tmp.set (ray.direction);
			Math2.getOrthogonalBasis (tmp, basis, true);

			// generate a location which is uniformly distributed
			// on the circular area
			int j = random.nextInt ();
			double r = Math.sqrt ((j >>> 16) * (1d / 0x10000)) * env.boundsRadius;
			char phi = (char) j;
			tmp.x = Math2.ccos (phi) * r;
			tmp.y = Math2.csin (phi) * r;
			tmp.z = -env.boundsRadius;
			basis.transform (tmp);
			tmp.add (env.boundsCenter);
			ray.origin.set (tmp);
		}
	}

	private static void constrainRGB (Tuple3f c)
	{
		float m = -MathUtils.min (c.x, c.y, c.z);
		if (m > 0)
		{
			c.x += m;
			c.y += m;
			c.z += m;
		}
	}

	@Override
	public float computeBSDF (Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		update ();
		Vector3d dir = env.tmpVector0;
		dir.set (env.localPoint);
		Vector3f c = env.userVector;
		if (getSkyRGB (dir, c) && !disableSun)
		{
			c.add (sunColor);
		}
		Math2.mul (c, c, invIrradiance);
		constrainRGB (c);
		bsdf.set (specIn);
		bsdf.mul (c);
		return 0;
	}

	@Override
	public double completeRay (Environment env, Point3d vertex, Ray out)
	{
		throw new UnsupportedOperationException ();
	}

	private double getDirection (double u, double v, Vector3d out)
	{
		double phi = 0, theta = 0;
		theta = u * 2 * Math.PI;
		phi = v * Math.PI;
		double sin_phi = Math.sin (phi);
		out.x = -sin_phi * Math.cos (theta);
		out.y = Math.cos (phi);
		out.z = sin_phi * Math.sin (theta);
		return sin_phi;
	}

	@Override
	public int getAverageColor ()
	{
		return 0xff8080ff;
	}

	@Override
	public int getFlags ()
	{
		return NEEDS_POINT | RANDOM_RAYS_GENERATE_ORIGINS;
	}

	@Override
	public void computeMaxRays (Environment env, Vector3f in, Spectrum specIn,
			Ray reflected, Tuple3f refVariance, Ray transmitted,
			Tuple3f transVariance)
	{
		throw new UnsupportedOperationException ();
	}

	@Override
	public boolean isTransparent ()
	{
		return false;
	}

	@Override
	public void shade (Environment env, RayList in, Vector3f out,
			Spectrum specOut, Tuple3d color)
	{
		update ();
		Vector3d dir = env.tmpVector0;
		dir.set (env.localPoint);
		Vector3f c = env.userVector;
		getSkyRGB (dir, c);
		c.scale (10 / (float) irradiance.integrate ());
		float m = -MathUtils.min (c.x, c.y, c.z);
		if (m > 0)
		{
			c.x += m;
			c.y += m;
			c.z += m;
		}
		env.tmpSpectrum0.set (c);
		env.tmpSpectrum0.dot (specOut, color);
	}

	@Override
	public int getLightType ()
	{
		return SKY;
	}

	@Override
	public double getTotalPower (Environment env)
	{
		if (disableLight)
		{
			return 0;
		}
		update ();
		return env.boundsRadius * env.boundsRadius * Math.PI * irradiance.integrate () * radianceFactor;
	}

	@Override
	public boolean isShadowless ()
	{
		return false;
	}

	@Override
	public boolean isIgnoredWhenHit ()
	{
		return false;
	}

	@Override
	public double computeExitance (Environment env, Spectrum exitance)
	{
		update ();
		exitance.set ((Tuple3f) irradiance);
		exitance.scale (radianceFactor);
		return 1 / (env.boundsRadius * env.boundsRadius * Math.PI);
	}

	@Override
	public void generateRandomOrigins (Environment env, RayList out, Random random)
	{
		update ();
		double area = env.boundsRadius * env.boundsRadius * Math.PI;
		double areaInv = 1 / area;
		for (int i = 0; i < out.getSize (); i++)
		{
			Ray ray = out.rays[i];
			if (i == 0)
			{
				ray.spectrum.set ((Tuple3f) irradiance);
				ray.spectrum.scale (radianceFactor * area);
			}
			else
			{
				ray.spectrum.set (out.rays[0].spectrum);
			}
			ray.originDensity = (float) areaInv;
		}
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field sun$FIELD;
	public static final NType.Field turbidity$FIELD;
	public static final NType.Field radianceFactor$FIELD;
	public static final NType.Field disableLight$FIELD;
	public static final NType.Field disableSun$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SunSkyLight.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((SunSkyLight) o).disableLight = (boolean) value;
					return;
				case 4:
					((SunSkyLight) o).disableSun = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 3:
					return ((SunSkyLight) o).isDisableLight ();
				case 4:
					return ((SunSkyLight) o).isDisableSun ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((SunSkyLight) o).turbidity = (float) value;
					return;
				case 2:
					((SunSkyLight) o).radianceFactor = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((SunSkyLight) o).getTurbidity ();
				case 2:
					return ((SunSkyLight) o).getRadianceFactor ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((SunSkyLight) o).sun.set ((Vector3d) value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((SunSkyLight) o).getSun ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new SunSkyLight ());
		$TYPE.addManagedField (sun$FIELD = new _Field ("sun", _Field.FINAL  | _Field.SCO, Tuple3dType.VECTOR, null, 0));
		$TYPE.addManagedField (turbidity$FIELD = new _Field ("turbidity", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (radianceFactor$FIELD = new _Field ("radianceFactor", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (disableLight$FIELD = new _Field ("disableLight", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.addManagedField (disableSun$FIELD = new _Field ("disableSun", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 4));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new SunSkyLight ();
	}

	public boolean isDisableLight ()
	{
		return disableLight;
	}

	public void setDisableLight (boolean value)
	{
		this.disableLight = (boolean) value;
	}

	public boolean isDisableSun ()
	{
		return disableSun;
	}

	public void setDisableSun (boolean value)
	{
		this.disableSun = (boolean) value;
	}

	public float getTurbidity ()
	{
		return turbidity;
	}

	public void setTurbidity (float value)
	{
		this.turbidity = (float) value;
	}

	public float getRadianceFactor ()
	{
		return radianceFactor;
	}

	public void setRadianceFactor (float value)
	{
		this.radianceFactor = (float) value;
	}

	public Vector3d getSun ()
	{
		return sun;
	}

	public void setSun (Vector3d value)
	{
		sun$FIELD.setObject (this, value);
	}

//enh:end

	@Override
	public void accept(ShaderVisitor visitor) {
		visitor.visit( this );
	}
	
	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
	@Override
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}
	
	@Override
	public Light resolveLight() {
		return this;
	}
}
