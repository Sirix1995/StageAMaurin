
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

package de.grogra.pf.ui.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import de.grogra.docking.FixedSize;

class StatusBar extends PlainPanel implements PropertyChangeListener, FixedSize
{
	JProgressBar bar;
	JLabel label;
	int avgTime;


	StatusBar ()
	{
		super (new BorderLayout (4, 0), null);
		add (bar = new JProgressBar (0, 1000)
		{
			private final int[] time = new int[16];
			private int index, count, sum;

			@Override
			protected void paintComponent (Graphics g)
			{
				long t = System.currentTimeMillis ();
				super.paintComponent (g);
				sum += -time[index]
					+ (time[index] = (int) (System.currentTimeMillis () - t));
				index = (index + 1) & 15;
				if (count < 16)
				{
					count++;
				}
				avgTime = sum / count;
			}
		}, BorderLayout.WEST);
		add (label = new JLabel (" "), BorderLayout.CENTER);
		label.setBorder (new SoftBevelBorder (BevelBorder.LOWERED));
		bar.setVisible (false);
		bar.setString("");
		bar.setStringPainted(true);
	}


	@Override
	public void initialize (PanelSupport support, de.grogra.util.Map p)
	{
		super.initialize (support, p);
		support.getWorkbench ().addStatusChangeListener (this);
	}


	@Override
	public void dispose ()
	{
		getSupport ().getWorkbench ().removeStatusChangeListener (this);
	}


	public int getFixedSize ()
	{
		return FIXED_HEIGHT;
	}


	private long lastTime;
	
	
	private float lastProgressBarValue = 2f;
	private long startTime;

	public void propertyChange (final PropertyChangeEvent event)
	{
		if ("progress".equals (event.getPropertyName ()))
		{
			Float f = (Float) event.getNewValue ();
			if ((f != null) && (f.floatValue () >= 0))
			{
				long t = System.currentTimeMillis ();
				if ((t - lastTime) < 8 * avgTime)
				{
					return;
				}
				lastTime = t;
			}
		}
		Runnable r = new Runnable ()
		{
			public void run ()
			{
				if ("status".equals (event.getPropertyName ()))
				{
					String s = (String) event.getNewValue ();
					label.setText (((s == null) || s.equals (""))
								   ? " " : s);
				}
				else if ("progress".equals (event.getPropertyName ()))
				{
					Float f = (Float) event.getNewValue ();
					if (f != null)
					{
						if (f.floatValue () < 0)
						{
							bar.setIndeterminate (true);
						}
						else
						{
							
							// added by Ralf Kopsch
							// shows the remaining time inside the progress bar 
							if (lastProgressBarValue > f.floatValue()) {
								// a new process, reset the start time
								startTime = lastTime;
								lastProgressBarValue = f.floatValue();								
							} else {
								long remaining = (long) (((lastTime - startTime) / f.floatValue()) - (lastTime - startTime)) / 1000;
								if (remaining < 60 ) {
									bar.setString("Remaining: " + remaining + " sec");
								} else if (remaining < 3600) {
									bar.setString("Remaining: " + remaining / 60 + " min");
								} else {
									bar.setString("Remaining: " + remaining / 3600 + " h");
								}
								lastProgressBarValue = f.floatValue();
							}
							// end added by Ralf Kopsch
							
							
							bar.setIndeterminate (false);
							bar.setValue
								(Math.max (0, Math.min
										   (Math.round
											(f.floatValue () * 1000),
											1000)));
						}
						bar.setVisible (true);
					}
					else
					{
						bar.setString("");
						bar.setVisible (false);
					}
				}
			}
		};
		if (EventQueue.isDispatchThread ())
		{
			r.run ();
		}
		else
		{
			EventQueue.invokeLater (r);
		}
	}

}
