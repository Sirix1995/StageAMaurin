
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.imp.objects.FixedImageAdapter;
import de.grogra.imp.objects.ImageAdapter;
import de.grogra.pf.registry.ItemReference;
import de.grogra.pf.registry.Registry;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class ShaderRef extends ItemReference<Shader> implements Shader
{
	//enh:sco
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends ItemReference.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ShaderRef representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ItemReference.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ShaderRef ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ShaderRef.class);
		$TYPE.validate ();
	}

//enh:end

	
	ShaderRef ()
	{
		super (null);
	}

	
	public ShaderRef (String name)
	{
		super (name);
	}

	public synchronized Shader resolve ()
	{
		Shader s = objectResolved ? object : resolveObject ("/objects/3d/shaders", Registry.current ());
		return (s == null) ? RGBAShader.GRAY : s;
	}
	
	// commented out by Uwe Mannl, 2009-10-05
	// look in Library.createImageShaderFromURL(...)
//	public static ShaderRef shaderFromURL (String url)
//	{
//		ImageAdapter ia = getImageForURL(url);
//		ImageMap im = new ImageMap();
//		im.setImageAdapter(ia);
//		Phong phong = new Phong();
//		phong.setDiffuse(im);		
//		
//		ShaderRef shader = new ShaderRef("tmpShader");
//		shader.object = phong;
//		
//		return shader;
//	}
//	
//	public static ImageAdapter getImageForURL(String imgUrl) {
//		ImageAdapter ia = null;
//		File f = null;
//		URL url = null;
//
//		try {
//			String imgpath = imgUrl;
//			if (imgpath.contains("://")) {
//				url = new URL(imgUrl);
//			} else {
//				// image path is a file with absolute path
//				f = new File(imgpath);
//				url = f.toURI().toURL();
//				
//				// to test if url is correct (image file exists)
//				Object testContent = url.getContent();
//				if (testContent == null)
//					return null;
//			}			
//			ia = new FixedImageAdapter(ImageIO.read(url));
//		} catch (MalformedURLException e) {} catch (IOException e) {}
//		return ia;
//	}
	

	public float computeBSDF (Environment env, Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		return resolve ().computeBSDF (env, in, specIn, out, adjoint, bsdf);
	}


	public void computeMaxRays (Environment env, Vector3f in, Spectrum specIn, Ray reflected, Tuple3f refVariance, Ray transmitted, Tuple3f transVariance)
	{
		resolve ().computeMaxRays (env, in, specIn, reflected, refVariance, transmitted, transVariance);
	}


	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays, boolean adjoint, Random random)
	{
		resolve ().generateRandomRays (env, out, specOut, rays, adjoint, random);
	}


	public int getAverageColor ()
	{
		return resolve ().getAverageColor ();
	}


	public int getFlags ()
	{
		return resolve ().getFlags ();
	}


	public boolean isTransparent ()
	{
		return resolve ().isTransparent ();
	}


	public void shade (Environment env, RayList in, Vector3f out, Spectrum specOut, Tuple3d color)
	{
		resolve ().shade (env, in, out, specOut, color);
	}

	@Override
	public Object manageableWriteReplace ()
	{
		return resolve ();
	}


	public void accept(ShaderVisitor visitor) {
		visitor.visit( this );
	}

}
