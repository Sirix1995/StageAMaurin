package de.grogra.pf.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
 
/**
 * PropertyFileReader.java 
 * 
 * Provides functions to read properties from external files.
 *
 * @author mh
 * @date 2010-12-07
 */
public class PropertyFileReader {

	//TODO: replace hard coded strings by properties (and translate them)
	
	private Properties properties = null;
	private File inFile = null;
	
	private int counter = 0;
	
	public PropertyFileReader() {
		init();
	}
	
	public PropertyFileReader(File file) {
		init();
		inFile = file;
	}
	
	public PropertyFileReader(String file_name) {
		init();
		inFile = new File(file_name);
	}
	
	public PropertyFileReader(FileSource fs) {
		init();
		inFile = fs.getInputFile();
	}

	private void init() {
		counter = 0;
		properties = new Properties();
	}
	
	/**
	 * Loading of a property file. The file has to be set by using the constructor.
	 * 
	 * @return false, iff there was no error during loading property file. 
	 */
	public boolean load() {
		try {
			if(inFile==null) {
				System.out.println("Error in PropertyFileReader: No file specified!" );	
				return true;
			}
			return load(new FileInputStream(inFile));
		} catch (IOException exc) {
			System.out.println("Error during reading a property file: "+exc);
			return true;
		}
	}
	
	public boolean load(File file) {
		try {
			if(file==null) {
				System.out.println("Error in PropertyFileReader: No file specified!" );	
				return true;
			}
			return load(new FileInputStream(file));
		} catch (IOException exc) {
			System.out.println("Error during reading a property file: "+exc);
			return true;
		}
	}
	
	
	public boolean load(InputStream inFileStream) {
		try {
			if(inFileStream==null) {
				System.out.println("Error in PropertyFileReader: No file input stream specified!" );	
				return true;
			}
			InputStream in = clean(inFileStream);
			if(in!=null) properties.load(in); else {
				System.out.println("Error during reading a property file.");
				return true;
			}
			//System.out.println("out = " + properties.toString());
			inFileStream.close();
		} catch (IOException exc) {
			System.out.println("Error during reading a property file: "+exc);
			return true;
		}
		return false;
	}
	
	public boolean save(String path) {
		try {
			return save(new FileOutputStream(new File(path)));
		} catch (IOException exc) {
			System.out.println("Error during saving a property file: "+exc);
			return true;
		}
	}
	
	public boolean save(File file) {
		try {
			if(file==null) {
				System.out.println("Error in PropertyFileReader: No file specified!" );	
				return true;
			}
			return save(new FileOutputStream(file));
		} catch (IOException exc) {
			System.out.println("Error during saving a property file: "+exc);
			return true;
		}
	}

	public boolean save(OutputStream outFileStream) {
		try {
			if(outFileStream==null) {
				System.out.println("Error in PropertyFileReader: No file output stream specified!" );	
				return true;
			}
			DataOutputStream out = new DataOutputStream(outFileStream);
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(out));
			br.write(properties.toString());
			br.flush();
			br.close();
			out.close();
			outFileStream.close();
		} catch (IOException exc) {
			System.out.println("Error during saving a property file: "+exc);
			return true;
		}
		return false;
	}
	
	private InputStream clean(InputStream fstream) {
		StringBuffer sb = new StringBuffer();
		try{
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// remove commands
				if (strLine.indexOf("#")!=-1) strLine = strLine.substring(0, strLine.indexOf("#"));
				if (strLine.indexOf("/")!=-1) strLine = strLine.substring(0, strLine.indexOf("/"));
				
				strLine = strLine.replaceAll(";", "");
				
				// replace all whitespaces
				strLine = strLine.replaceAll(" ", "");
				// remove tabs
				strLine = strLine.replaceAll("\t", "");
				
				sb.append(strLine+"\n");
			}
			//Close the input stream
			in.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		try {
			return new ByteArrayInputStream(sb.toString().getBytes("iso-8859-1"));
		} catch (UnsupportedEncodingException e) {e.printStackTrace(); }
		return null;
	}
	
	/**
	 * Returns the number of read properties.
	 * 
	 * @return number of read properties
	 */
	public int getNumberOfReadProperties() {
		return counter;
	}
	
	private String getProperty(Properties properties, String propertyName) {
		String tmp = properties.getProperty(propertyName);
		if(tmp==null) {
			System.out.println("Could not find property: "+propertyName);
			System.out.println("Property file: "+properties);
			return "";
		}

		counter++;
		return tmp;
	}	
	
	/**
	 * Returns the specified property as boolean value.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public boolean getBoolean(String propertyString) {
		try {
			return (getProperty(properties, propertyString).toLowerCase()).equals("true");
		} catch(NumberFormatException exc) {
			System.out.println("Error: "+exc);
			return false;
		}
	}

	/**
	 * Returns the specified property as string value.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public String getString(String propertyString) {
		try {
			return getProperty(properties, propertyString);
		} catch(NumberFormatException exc) {
			System.out.println("Error: "+exc);
			return "";
		}
	}

	/**
	 * Returns the specified property as double value.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public double getDouble(String propertyString) {
		try {
			return Double.parseDouble(getProperty(properties, propertyString));
		} catch(NumberFormatException exc) {
			System.out.println("Error: "+exc);
			return 0;
		}
	}

	/**
	 * Returns the specified property as float value.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public float getFloat(String propertyString) {
		try {
			return (float)Double.parseDouble(getProperty(properties, propertyString));
		} catch(NumberFormatException exc) {
			System.out.println("Error: "+exc);
			return 0;
		}
	}

	/**
	 * Returns the specified property as integer value.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public int getInteger(String propertyString) {
		try {
			return Integer.parseInt(getProperty(properties, propertyString));
		} catch(NumberFormatException exc) {
			System.out.println("Error: "+exc);
			return 0;
		}
	}	
	
	/**
	 * Returns the specified property as array of float values.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */	
	public float[] getFloatArray(String propertyString) {
		String[] propertyStringArray =  getProperty(properties, propertyString).split(",");
		float[] tmpIntArray = new float[propertyStringArray.length];
		for(int i=0; i<propertyStringArray.length; i++) {
			tmpIntArray[i] = (float)Double.parseDouble(propertyStringArray[i]);
		}
		return tmpIntArray;
	}

	/**
	 * Returns the specified property as array of double values.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public double[] getDoubleArray(String propertyString) {
		String[] propertyStringArray = getProperty(properties, propertyString).split(",");
		double[] tmpIntArray = new double[propertyStringArray.length];
		for(int i=0; i<propertyStringArray.length; i++) {
			tmpIntArray[i] = Double.parseDouble(propertyStringArray[i]);
		}
		return tmpIntArray;
	}

	/**
	 * Returns the specified property as a array of integer values.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public int[] getIntArray(String propertyString) {
		String[] propertyStringArray = getProperty(properties, propertyString).split(",");
		int[] tmpIntArray = new int[propertyStringArray.length];
		for(int i=0; i<propertyStringArray.length; i++) {
			tmpIntArray[i] = Integer.parseInt(propertyStringArray[i]);
		}
		return tmpIntArray;
	}

	/**
	 * Returns the specified property as a array of strings.
	 * 
	 * @param propertyString name of the specified property
	 * @return value of this property
	 */
	public String[] getStringArray(String propertyString) {
		return getProperty(properties, propertyString).split(",");
	}
}
