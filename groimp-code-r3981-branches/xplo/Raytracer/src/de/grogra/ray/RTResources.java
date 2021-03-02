package de.grogra.ray;

import java.util.Locale;
import java.util.ResourceBundle;

public class RTResources {

	private static String m_language = "en";
	
	
	private static ResourceBundle getBundle() {
		if (m_language.equals("de")) {
			return ResourceBundle.getBundle("resources",Locale.GERMANY);
		} else
		if (m_language.equals("en")) {
			return ResourceBundle.getBundle("resources",Locale.US);
		} else
		return ResourceBundle.getBundle("resources");
	}
	
	
	public static String getString(String key) {
		String str = key;
		try {
			str = getBundle().getString(key);
		} catch(Exception e) {
			System.err.println("No string found for key:"+key);
		}
		return str;
	}

}
