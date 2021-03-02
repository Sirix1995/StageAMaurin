
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

package de.grogra.imp;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

import de.grogra.util.Disposable;

public abstract class Renderer implements Disposable, ImageObserver
{
	protected View view;
	protected ArrayList<ImageObserver> observers = new ArrayList<ImageObserver> ();
	protected int width, height;


	public abstract String getName ();


	public final void initialize (View view,
								  int width, int height)
	{
		this.view = view;
		this.width = width;
		this.height = height;
		initializeImpl ();
	}

	
	public void addImageObserver (ImageObserver obs)
	{
		obs.getClass ();
		observers.add (obs);
	}

	protected void initializeImpl ()
	{
	}


	public abstract void render () throws IOException;


	private final Object imageLock = new Object ();
	private boolean renderingDone = false;
	private Image finalImage = null;

	public boolean imageUpdate (Image img, int infoflags, int x, int y, int width, int height)
	{
		boolean ret = false;
		for (int i = 0; i < observers.size (); i++)
		{
			ret |= observers.get (i).imageUpdate (img, infoflags, x, y, width, height);
		}
		if ((infoflags & (ALLBITS | ABORT | ERROR)) != 0)
		{
			synchronized (imageLock)
			{
				renderingDone = true;
				if ((infoflags & (ABORT | ERROR)) == 0)
				{
					finalImage = img;
				}
				imageLock.notifyAll ();
			}
		}
		return ret;
	}


	public Image waitForImage () throws InterruptedException
	{
		synchronized (imageLock)
		{
			while (!renderingDone)
			{
				imageLock.wait ();
			}
		}
		return finalImage;
	}


	public Image computeImage () throws IOException, InterruptedException
	{
		render ();
		return waitForImage ();
	}
	
}
