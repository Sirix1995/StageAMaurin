
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d.shading;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.icon.Icon;
import de.grogra.icon.IconSource;
import de.grogra.persistence.Manageable;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.PersistenceInput;
import de.grogra.persistence.PersistenceOutput;
import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;
import de.grogra.persistence.Transaction;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.reflect.ClassAdapter;
import de.grogra.vecmath.Math2;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>RGBAShader</code> implements a simple lambertian
 * material with a single color and an alpha-value for the
 * transparency. Some methods which are related to the color
 * palette of the EGA graphics card are declared, they are used
 * in the context of the emulation of the GROGRA software.
 * 
 * @author Ole Kniemeyer
 */
public final class RGBAShader extends Color4f
	implements Shader, Shareable, Manageable, IconSource, Icon
{
	/**
	 * The color palette of the EGA graphics card.
	 */
	private static final int[] EGA =
		{0xff000000, 0xff0000aa, 0xff00aa00, 0xff00aaaa,
		 0xffaa0000, 0xffaa00aa, 0xffaa5500, 0xffaaaaaa,
		 0xff555555, 0xff5555ff, 0xff55ff55, 0xff55ffff,
		 0xffff5555, 0xffff55ff, 0xffffff55, 0xffffffff};


	/**
	 * Returns the index of the color in the palette of the EGA graphics
	 * card which is closest to the specified color.
	 * 
	 * @param r red value of color (between 0 and 1)
	 * @param g green value of color (between 0 and 1)
	 * @param b blue value of color (between 0 and 1)
	 * @return index of closest EGA color
	 */
	public static final int getEGAColorIndex (float r, float g, float b)
	{
		float opt = Float.MAX_VALUE;
		r *= 255;
		g *= 255;
		b *= 255;
		int optIndex = -1;
		for (int i = 0; i < 16; i++)
		{
			int rgb = EGA[i];
			float t;
			float dist = (t = ((rgb & 0xff0000) >> 16) - r) * t
				+ (t = ((rgb & 0x00ff00) >> 8) - g) * t
				+ (t = (rgb & 0x0000ff) - b) * t;
			if (dist < opt)
			{
				opt = dist;
				optIndex = i;
			}
		}
		return optIndex;
	}


	/**
	 * Returns the index of the color in the palette of the EGA graphics
	 * card which is closest to the specified color.
	 * 
	 * @param c a color
	 * @return index of closest EGA color
	 */
	public static final int getEGAColorIndex (Color3f c)
	{
		return getEGAColorIndex (c.x, c.y, c.z);
	}


	/**
	 * Returns the index of the color in the palette of the EGA graphics
	 * card which is closest to the specified color.
	 * 
	 * @param c a color
	 * @return index of closest EGA color
	 */
	public static final int getEGAColorIndex (Color4f c)
	{
		return getEGAColorIndex (c.x, c.y, c.z);
	}


	static Color4f getColor4f (Shader a)
	{
		int i = a.getAverageColor ();
		Color4f c = new Color4f ((i >> 16) & 255, (i >> 8) & 255, i & 255,
								 i >>> 24);
		c.scale (1 / 255f);
		return c;
	}


	/**
	 * <code>RGBAShader</code> whose color is {@link Color#BLACK}.
	 */
	public static final RGBAShader BLACK;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#BLUE}.
	 */
	public static final RGBAShader BLUE;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#CYAN}.
	 */
	public static final RGBAShader CYAN;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#DARK_GRAY}.
	 */
	public static final RGBAShader DARK_GRAY;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#GRAY}.
	 */
	public static final RGBAShader GRAY;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#GREEN}.
	 */
	public static final RGBAShader GREEN;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#LIGHT_GRAY}.
	 */
	public static final RGBAShader LIGHT_GRAY;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#MAGENTA}.
	 */
	public static final RGBAShader MAGENTA;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#ORANGE}.
	 */
	public static final RGBAShader ORANGE;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#PINK}.
	 */
	public static final RGBAShader PINK;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#RED}.
	 */
	public static final RGBAShader RED;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#YELLOW}.
	 */
	public static final RGBAShader YELLOW;

	/**
	 * <code>RGBAShader</code> whose color is {@link Color#WHITE}.
	 */
	public static final RGBAShader WHITE;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 0.
	 */
	public static final RGBAShader EGA_0;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 1.
	 */
	public static final RGBAShader EGA_1;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 2.
	 */
	public static final RGBAShader EGA_2;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 3.
	 */
	public static final RGBAShader EGA_3;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 4.
	 */
	public static final RGBAShader EGA_4;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 5.
	 */
	public static final RGBAShader EGA_5;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 6.
	 */
	public static final RGBAShader EGA_6;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 7.
	 */
	public static final RGBAShader EGA_7;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 8.
	 */
	public static final RGBAShader EGA_8;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 9.
	 */
	public static final RGBAShader EGA_9;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 10.
	 */
	public static final RGBAShader EGA_10;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 11.
	 */
	public static final RGBAShader EGA_11;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 12.
	 */
	public static final RGBAShader EGA_12;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 13.
	 */
	public static final RGBAShader EGA_13;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 14.
	 */
	public static final RGBAShader EGA_14;

	/**
	 * <code>RGBAShader</code> whose color is EGA color number 15.
	 */
	public static final RGBAShader EGA_15;

	static final RGBAShader[] STANDARD;

	public static final ManageableType $TYPE;

	static
	{
		$TYPE = new ManageableType (ClassAdapter.wrap (RGBAShader.class),
								   de.grogra.math.Tuple4fType.$TYPE,
								   true)
			{
				@Override
				protected Object readObject (PersistenceInput in,
											 Object placeIn,
											 boolean fieldsProvided)
					throws IOException
				{
					return in.readBoolean ()
						? STANDARD[in.readByte () % STANDARD.length]
						: super.readObject
							(in, !(placeIn instanceof RGBAShader)
							 || (((RGBAShader) placeIn).index >= 0)
							 ? new RGBAShader () : placeIn, fieldsProvided);
				}


				@Override
				protected void write (Object value, PersistenceOutput out,
									  boolean onlyDiff)
					throws IOException
				{
					RGBAShader s = (RGBAShader) value;
					if (s.index >= 0)
					{
						out.writeBoolean (true);
						out.writeByte (s.index);
					}
					else
					{
						out.writeBoolean (false);
						super.write (value, out, onlyDiff);
					}
				}

				@Override
				public Object getRepresentative ()
				{
					return null;
				}


				@Override
				public Object newInstance ()
				{
					return new RGBAShader ();
				}
			}.validate ();

		Color[] c = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
					 Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA,
					 Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW,
					 Color.WHITE};
		STANDARD = new RGBAShader[EGA.length + c.length];
		int i = 0;
		for (int j = 0; j < EGA.length; j++)
		{
			STANDARD[i] = new RGBAShader (EGA[j], i++);
		}
		for (int j = 0; j < c.length; j++)
		{
			STANDARD[i] = new RGBAShader (c[j].getRGB (), i++);
		}
		i = -1;
		EGA_0 = STANDARD[++i];
		EGA_1 = STANDARD[++i];
		EGA_2 = STANDARD[++i];
		EGA_3 = STANDARD[++i];
		EGA_4 = STANDARD[++i];
		EGA_5 = STANDARD[++i];
		EGA_6 = STANDARD[++i];
		EGA_7 = STANDARD[++i];
		EGA_8 = STANDARD[++i];
		EGA_9 = STANDARD[++i];
		EGA_10 = STANDARD[++i];
		EGA_11 = STANDARD[++i];
		EGA_12 = STANDARD[++i];
		EGA_13 = STANDARD[++i];
		EGA_14 = STANDARD[++i];
		EGA_15 = STANDARD[++i];
		BLACK = STANDARD[++i];
		BLUE = STANDARD[++i];
		CYAN = STANDARD[++i];
		DARK_GRAY = STANDARD[++i];
		GRAY = STANDARD[++i];
		GREEN = STANDARD[++i];
		LIGHT_GRAY = STANDARD[++i];
		MAGENTA = STANDARD[++i];
		ORANGE = STANDARD[++i];
		PINK = STANDARD[++i];
		RED = STANDARD[++i];
		YELLOW = STANDARD[++i];
		WHITE = STANDARD[++i];
	}


	/**
	 * Returns the <code>RGBAShader</code> whose color has the
	 * specified index in the palette of the EGA graphics card.
	 * 
	 * @param index palette index (only the lowest 4 bits are used)
	 * @return corresponding shader
	 */
	public static final RGBAShader forEGAColor (int index)
	{
		return STANDARD[index & 15];
	}


	SharedObjectProvider sop;
	final int index;
	private transient ObjectList refs = null;
	private transient int stamp = 0;


	public RGBAShader (float red, float green, float blue, float alpha)
	{
		super (red, green, blue, alpha);
		index = -1;
	}


	public RGBAShader (float red, float green, float blue)
	{
		this (red, green, blue, 1);
	}


	public RGBAShader ()
	{
		this (0.5f, 0.5f, 0.5f, 1);
	}


	public RGBAShader (Shader a)
	{
		this (getColor4f (a));
	}


	public RGBAShader (Color4f color)
	{
		this (color.x, color.y, color.z, color.w);
	}

	/**
	 * Specify colors as ARGB integer.
	 * This means the bits 31-24 are alpha, 23-16 are red, 15-8 are green and 7-0 are blue.
	 * @param rgba color as 0xAARRGGBB
	 */
	public RGBAShader (int rgba)
	{
		this (rgba, -1);
	}

	/**
	 * Specify colors as ARGB integer.
	 * This means the bits 31-24 are alpha, 23-16 are red, 15-8 are green and 7-0 are blue.
	 * @param rgba color as 0xAARRGGBB
	 */
	private RGBAShader (int rgba, int index)
	{
		super ((rgba >> 16) & 255, (rgba >> 8) & 255, rgba & 255,
			   (rgba >> 24) & 255);
		scale (1 / 255f);
		this.index = index;
	}


	public void scale (double r, Tuple3d color)
	{
		x = (float) (r * color.x);
		y = (float) (r * color.y);
		z = (float) (r * color.z);
	}


	@Override
	public Object clone ()
	{
		return (index >= 0) ? this : new RGBAShader ((Color4f) this);
	}


	public boolean isPredefined ()
	{
		return index >= 0; 
	}


	private static int f2i (float f)
	{
		int i = Math.round (f * 255);
		return (i < 0) ? 0 : (i > 255) ? 255 : i;
	}


	public int getAverageColor ()
	{
		return (f2i (x) << 16) + (f2i (y) << 8) + f2i (z) + (f2i (w) << 24);
	}

	
	public int getFlags ()
	{
		return (w < 1) ? IS_NON_OPAQUE | NEEDS_NORMAL : NEEDS_NORMAL;
	}


	public void initProvider (SharedObjectProvider provider)
	{
		if (index >= 0)
		{
			return;
		}
		if (sop != null)
		{
			throw new IllegalStateException ();
		}
		sop = provider;
	}


	public SharedObjectProvider getProvider ()
	{
		return sop;
	}


	public synchronized void addReference (SharedObjectReference ref)
	{
		if (index >= 0)
		{
			return;
		}
		if (refs == null)
		{
			refs = new ObjectList (4, false);
		}
		refs.add (ref);
	}

	
	public synchronized void removeReference (SharedObjectReference ref)
	{
		if (refs != null)
		{
			refs.remove (ref);
		}
	}

	
	public synchronized void appendReferencesTo (java.util.List out)
	{
		if (refs != null)
		{
			out.addAll (refs);
		}
	}


	public ManageableType getManageableType ()
	{
		return $TYPE;
	}


	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
		if ((t != null) && (sop != null) && (index < 0))
		{
			t.fireSharedObjectModified (this);
		}
	}

	
	public int getStamp ()
	{
		return stamp;
	}

	
	public Manageable manageableReadResolve ()
	{
		return this;
	}

	public Object manageableWriteReplace ()
	{
		return this;
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
		g.setColor (new Color (getAverageColor ()));
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
		return index < 0;
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


	public void shade (Environment env, RayList in, Vector3f out, Spectrum specOut, Tuple3d color)
	{
		Ray[] rays = in.rays;
		Tuple3f tmp = env.userVector;
		Tuple3f col = env.userVector2;
		col.set (0, 0, 0);
		for (int i = in.getSize() - 1; i >= 0; i--)
		{
			rays[i].spectrum.get (tmp);
			float f = rays[i].direction.dot (env.normal);
			if ((out.dot (env.normal) >= 0) == (f >= 0))
			{
				f = w * Math.abs (f);
				col.x += tmp.x * x * f;
				col.y += tmp.y * y * f;
				col.z += tmp.z * z * f;
			}
			else
			{
				f = 1 - w;
				col.x += tmp.x * f;
				col.y += tmp.y * f;
				col.z += tmp.z * f;
			}
		}
		env.tmpSpectrum0.set (col);
		env.tmpSpectrum0.dot (specOut, color);
	}


	public void computeMaxRays
		(Environment env, Vector3f in, Spectrum specIn,
		 Ray reflected, Tuple3f refVariance, Ray transmitted, Tuple3f transVariance)
	{
		float r = Math2.fresnel
			(env.normal, in, env.iorRatio, reflected.direction, transmitted.direction);

//		System.out.println("RGBAShader: computeMaxRays - r=" +r);
		
		if (env.normal.dot (in) < 0)
		{
			reflected.direction.negate (env.normal);
		}
		else
		{
			reflected.direction.set (env.normal);
		}

		Tuple3f tmp = env.userVector;
		tmp.x = w * x;
		tmp.y = w * y;
		tmp.z = w * z;
		reflected.spectrum.set (specIn);
		reflected.spectrum.mul (tmp);
	
		refVariance.x = LAMBERTIAN_VARIANCE;
		refVariance.y = LAMBERTIAN_VARIANCE;
		refVariance.z = LAMBERTIAN_VARIANCE;
	
		r = (1 - w) * (1 - r);
		tmp.x = tmp.y = tmp.z = r;
		transmitted.spectrum.set (specIn);
		transmitted.spectrum.mul (tmp);
	
		transVariance.x = 0;
		transVariance.y = 0;
		transVariance.z = 0;
	}


	public void generateRandomRays
		(Environment env, Vector3f out, Spectrum specOut, RayList rays, boolean adjoint, Random rnd)
	{
		Vector3f normal = env.userVector, tdir = env.userVector2, rdir = env.userVector3;
		Vector3f col = env.userVector4;
		float r = Math2.fresnel (env.normal, out, env.iorRatio, rdir, tdir);

		float ior = env.iorRatio;
		
		// compute normal such that it points to the same side as out
		if (env.normal.dot (out) < 0)
		{
			normal.negate (env.normal);
		}
		else
		{
			ior = 1 / ior;
			normal.set (env.normal);
		}

		// compute the transmission coefficient
		float trans = 1 - w;
		if (!adjoint)
		{
			// correction of BSDF as described by Eric Veach in his thesis
			trans *= ior * ior;
		}

		col.x = x;
		col.y = y;
		col.z = z;

		// compute the probabilities of diffuse reflection, perfect transmission and perfect reflection
		// based on color and trans
		float pd = x + y + z;
		float pt = trans * (1 - r);
		float pr = trans * r; // when the ior is != 1, Fresnel's formula computes a reflection coefficient != 0
		float p = pd + pt + pr;
		boolean absorbed = p < 1e-7f;
		if (!absorbed)
		{
			p = 1 / p;
			pd *= p;
			pt *= p;
			pr *= p;
			col.scale (1 / pd);
		}
	
		Matrix3f diffBasis = null; 
	
		for (int i = rays.getSize() - 1; i >= 0; i--)
		{
			Vector3f in = rays.rays[i].direction;
			rays.rays[i].valid = true;

			// determine randomly if the ray is transmitted or reflected
			float z = (2 * (rnd.nextInt () >>> 8) + 1) * (1f / (1 << 25));
			if (absorbed)
			{
				rays.rays[i].spectrum.setZero ();
				rays.rays[i].direction.set (1, 0, 0);
				rays.rays[i].directionDensity = DELTA_FACTOR;
				rays.rays[i].valid = false;
//				System.err.println("RGBASHader: generateRandomRays: ABSORBED!");
			}
			else if (z <= pt)
			{
				// this is a transmitted ray
				in.set (tdir);
				float t = trans / pt;
				rays.rays[i].spectrum.set (specOut);
				rays.rays[i].spectrum.scale (t);
				rays.rays[i].directionDensity = DELTA_FACTOR;
				rays.rays[i].reflected = false;
			}
			else if (z <= pt + pr)
			{
				// this is a perfectly reflected ray
				in.set (rdir);
				float t = trans / pr;
				rays.rays[i].spectrum.set (specOut);
				rays.rays[i].spectrum.scale (t);
				rays.rays[i].directionDensity = DELTA_FACTOR;
				rays.rays[i].reflected = true;
			}
			else
			{
				// this is a reflected ray
				Matrix3f m;
				float sint, cost;
				if ((m = diffBasis) == null)
				{
					// compute a local orthogonal basis with its z-axis
					// pointing in the direction of normal
					m = diffBasis = env.userMatrix;
					Math2.getOrthogonalBasis (normal, diffBasis, true);
				}
				
				// choose theta randomly according to the density cos(theta)/PI
				int j = rnd.nextInt ();
				z = (2 * (j >>> 16) + 1) * (1f / 0x20000);
				cost = (float) Math.sqrt (z);
				sint = (float) Math.sqrt (1 - z);
				
				// choose phi randomly between 0 and 2 PI
				char phi = (char) j;
				
				// compute the direction vector described by theta and phi
				// and transform it to coordinates of the environment
				in.set (Math2.ccos (phi) * sint,
						Math2.csin (phi) * sint, cost);
				m.transform (in);
						
				rays.rays[i].directionDensity = pd * cost * Math2.M_1_PI;
				rays.rays[i].spectrum.set (specOut);
				rays.rays[i].spectrum.mul (col);
				rays.rays[i].reflected = true;
			}
		}
	}


	public float computeBSDF
		(Environment env, Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		float cos = out.dot (env.normal);
		if ((in.dot (env.normal) > 0) != (cos > 0)) 
		{
			if(PixelwiseRenderer.DEBUG_SUBPIXEL)System.err.println("RGBA is ZEro!!!");
			bsdf.setZero ();
			return 0;
		}

		float r = Math2.fresnel (env.normal, out, env.iorRatio, env.userVector, env.userVector2);

		float ior = env.iorRatio;
		if (cos < 0)
		{
			cos = -cos;
		}
		else
		{
			ior = 1 / ior;
		}

		float trans = (1 - w) * (1 - r);
		if (!adjoint)
		{
			trans *= ior * ior;
		}
		float kd = x + y + z;
		float k = kd + trans;

		if (k < 1e-7f)
		{
			kd = 1;
		}
		else
		{
			kd /= k;
		}

		float t = Math2.M_1_PI * cos;
		Tuple3f c = env.userVector;
		c.x = t * x;
		c.y = t * y;
		c.z = t * z;
		bsdf.set (specIn);
		bsdf.mul (c);
		return kd * t;
	}


	public boolean isTransparent() {
		return false;
	}
	
	public void accept(ShaderVisitor visitor) {
		visitor.visit( this );
	}

}
