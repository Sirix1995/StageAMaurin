/*
 * Copyright (C) 2012 GroIMP Developer Team
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

import de.grogra.reflect.Type;
import de.grogra.util.EnumerationType;
import de.grogra.util.I18NBundle;

/**
 * Provides a list of all supported languages by GroIMP.
 * 
 * To add an other language increment the number of languages in line 38 and add the  
 * property key "platform.language.<number of the new language>" into the Resources.properties.
 * 
 * Finally you have to provide the possibility to load the new language. Therefore you have 
 * to add the new language into the switch in "Main.getCurrentLocale()".
 * 
 * @author mhenke
 *
 */
public class Languages {
	public static final String OPTION_NAME_LANGUAGE_LIST = "current_language";

	public static final I18NBundle I18N = I18NBundle.getInstance (Languages.class);

	public static final Type LANGUAGE_LIST = new EnumerationType ("platform.language", Languages.I18N, 2);

}
