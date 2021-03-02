
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

package de.grogra.ray.util;

import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;


/**
 * This class represents a list of rays.
 * 
 * @author Ole Kniemeyer
 */
public final class RayList
{
	public final Spectrum spectrumFactory;

	/**
	 * The number of light rays in the array {@link #rays}.
	 * This field should only be set via {@link #setSize}.
	 */
	private int size = 0;

	/**
	 * The array of light rays. The components with indices from 0 to
	 * {@link #size} - 1 are the valid light rays.
	 */
	public Ray[] rays = new Ray[0];


	public RayList ()
	{
		this (10);
	}
	
	
	public RayList (int size)
	{
		this (new Spectrum3f ());
		setSize (size);
	}

	public RayList (Spectrum factory)
	{
		this.spectrumFactory = factory;
	}

	/**
	 * Sets the size of the light ray array. The field {@link #size}
	 * is set to <code>size</code>, and the array {@link #rays}
	 * is enlarged if necessary so that it
	 * has length of at least <code>size</code>. If an enlargement
	 * is necessary, the existing array components are copied into the
	 * new array, and the newly added components are initialized with
	 * new instances of <code>Ray</code>.
	 * 
	 * @param size the new size of the light ray array
	 */
	public void setSize (int size)
	{
		if (size > rays.length)
		{
			int n = rays.length;
			System.arraycopy (rays, 0, rays = new Ray[size], 0, n);
			while (n < size)
			{
				rays[n] = new Ray (spectrumFactory);
				n++;
			}
		}
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}
	
	
	public int size() {
		return size;
	}
	
	
	public void clear(){
		size=0;
	}
	
	
	public Ray lastRay(){
		return rays[size-1];		
	}
	
	
	public Ray nextRay(){
//		setSize(size+1); //TODO <-Micha fragen warum!!!
//		return rays[size-1];
		
		ensureCapacity(size+1);
		size++;
		return rays[size-1];
	}
	
	
	public void appendRay(Ray lastRay){
		setSize(size+1);
		rays[size-1]= lastRay;
	}
	
	

	



	
	
	
	private void ensureCapacity(int minCapacity) {
		int old_capacity = rays.length;
		if (minCapacity > old_capacity) {
		    Ray[] old_rays = rays;
		    int new_capacity = old_capacity * 2;
	    	if (new_capacity < minCapacity) {
	    		new_capacity = minCapacity;
	    	}
		    rays = new Ray[new_capacity];
		    System.arraycopy(old_rays, 0, rays, 0, size);
		    for (int i=size; i<new_capacity;i++) {
		    	rays[i] = new Ray(spectrumFactory);
		    }
		}
	}
	
}
