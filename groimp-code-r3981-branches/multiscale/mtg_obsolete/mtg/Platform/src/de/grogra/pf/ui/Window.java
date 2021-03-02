
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

package de.grogra.pf.ui;

import de.grogra.util.*;
import de.grogra.xl.lang.ObjectToBoolean;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import de.grogra.pf.ui.registry.Layout;

public interface Window extends Panel
{
	int OPEN_FILE = 0;
	int ADD_FILE = 1;
	int SAVE_FILE = 2;

	int PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;
	int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
	int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
	int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
	int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
	int QUESTION_CANCEL_MESSAGE = 100;
	int RESIZABLE_PLAIN_MESSAGE = 101;
	int RESIZABLE_OK_CANCEL_MESSAGE = 102;

	int YES_OK_RESULT = 0;
	int NO_RESULT = 1;
	int CANCEL_RESULT = 2;


	String MAIN_WINDOW_ID = "MainWindow";

	java.util.Map getUIPropertyMap ();

	Workbench getWorkbench ();

	void initializeWorkbench (Workbench wb);

	void hide ();

	boolean isVisible ();

	Panel[] getPanels (ObjectToBoolean<Panel> filter);

	Panel getPanel (String panelId);

	void setLayout (Layout layout, Map params);

	Layout getLayout ();

	/**
	 * 
	 * @param title
	 * @param directory
	 * @param filters
	 * @param type
	 * @param mustExist
	 * @param selectedFilter - the selected file filter type
	 * @return
	 */
	FileChooserResult chooseFile (String title, java.io.File directory,
			  javax.swing.filechooser.FileFilter[] filters,
			  int type, boolean mustExist, FileFilter selectedFilter);

	int showChoiceDialog (String title, I18NBundle bundle, String keyBase,
						  String[] options);

	int showDialog (String title, Object message, int type);

	String showInputDialog (String title, Object message, String initial);

	Disposable showWaitMessage (String toComplete);
	
	void setMenuVisibility(boolean value);
}
