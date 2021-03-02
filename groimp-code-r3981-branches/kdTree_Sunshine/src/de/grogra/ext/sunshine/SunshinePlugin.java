package de.grogra.ext.sunshine;

import java.awt.Frame;
import de.grogra.pf.registry.*;


public class SunshinePlugin extends Plugin
{
	
	@Override
	public boolean initialize()
	{
		ExtensionCheck checkWindow = new ExtensionCheck(new Frame() );

		checkWindow.setSize(1, 1);
		checkWindow.setLocation(100, 100);
		checkWindow.setVisible(true);
		
		
		return checkWindow.isExecutable();
	}
}
