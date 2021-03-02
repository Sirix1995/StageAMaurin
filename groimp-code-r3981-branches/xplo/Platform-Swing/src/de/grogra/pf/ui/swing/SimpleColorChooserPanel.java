
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

import java.awt.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;

final class SimpleColorChooserPanel extends AbstractColorChooserPanel
	implements ChangeListener
{
	private JSlider red, green, blue;
	private JLabel redLabel, greenLabel, blueLabel;
	private boolean updating;


    @Override
	public String getDisplayName ()
    {
    	return SwingToolkit.I18N.getString ("simplecolorchooser.Name");
	}


    @Override
	public int getMnemonic ()
    {
	    return -1;
	}

    
    @Override
	public int getDisplayedMnemonicIndex ()
    {
	    return -1;
	}

	
    @Override
	public Icon getSmallDisplayIcon ()
    {
	    return null;
	}

	
    @Override
	public Icon getLargeDisplayIcon ()
    {
    	return null;
	}
	       

    @Override
	protected void buildChooser ()
    {
    	setLayout (new GridBagLayout ());
    	GridBagConstraints gcl = new GridBagConstraints ();
    	GridBagConstraints gcs = new GridBagConstraints ();
    	GridBagConstraints gcp = new GridBagConstraints ();
    	gcs.weightx = 1;
		gcs.fill = GridBagConstraints.HORIZONTAL;
    	gcl.gridx = 0;
    	gcs.gridx = 1;
    	gcp.gridx = 2;
    	gcl.gridy = 0;
    	gcs.gridy = 0;
    	gcp.gridy = 0;
    	add (new JLabel ("R"), gcl);
    	add (red = new JSlider (0, 255), gcs);
    	add (redLabel = new JLabel (), gcp);
    	gcl.gridy = 1;
    	gcs.gridy = 1;
    	gcp.gridy = 1;
    	add (new JLabel ("G"), gcl);
    	add (green = new JSlider (0, 255), gcs);
    	add (greenLabel = new JLabel (), gcp);
    	gcl.gridy = 2;
    	gcs.gridy = 2;
    	gcp.gridy = 2;
    	add (new JLabel ("B"), gcl);
    	add (blue = new JSlider (0, 255), gcs);
    	add (blueLabel = new JLabel (), gcp);
    	red.addChangeListener (this);
    	green.addChangeListener (this);
    	blue.addChangeListener (this);
    }


    private static void setText (JLabel label, int value)
    {
    	value = value * 1000 / 255;
    	String text;
    	if (value == 0)
    	{
    		text = "0%";
    	}
    	else if (value < 10)
    	{
    		text = "0." + value + "%";
    	}
    	else if ((value % 10) == 0)
    	{
    		text = (value / 10) + "%";
    	}
    	else
    	{
    		text = (value / 10) + "." + (value % 10) + "%";
    	}
    	label.setText (text);
    }

    @Override
	public void updateChooser ()
    {
    	if (!updating)
    	{
    		updating = true;
    		Color c = getColorFromModel ();
            int i;
            i = c.getRed ();
            if (red.getValue() != i)
            {
                red.setValue (i);
            }
            setText (redLabel, i);
            i = c.getGreen ();
            if (green.getValue() != i)
            {
                green.setValue (i);
            }
            setText (greenLabel, i);
            i = c.getBlue ();
            if (blue.getValue() != i)
            {
                blue.setValue (i);
            }
            setText (blueLabel, i);
    		updating = false;
    	}
    }


    public void stateChanged (ChangeEvent e)
    {
    	if (!updating)
    	{
            getColorSelectionModel ().setSelectedColor
				(new Color (red.getValue (), green.getValue (), blue.getValue ()));
	    }

	}

}
