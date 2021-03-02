package de.grogra.ext.x3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3d;

/**
 * This class provides usefull static methods for recurring tasks.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class Util {
	
	/**
	 * Regular expression which is used to split single values in strings of x3d attributes.
	 */
	private static String splitExpr = "[ ,\n]+";
	
	/**
	 * Load a BufferedImage by the given url-string.
	 * 
	 * @param filepath String to image
	 * @return BufferedImage texture
	 */
	public static BufferedImage loadTexture(String filepath) {
//		System.err.println("Util::loadTexture: actual = " + filepath);
		BufferedImage texture = null;
		
		try {
			File file = new File(filepath);			
			if(file.exists()) {
				texture = ImageIO.read(file);
			} else {
				System.err.println("Util::loadTexture: file does not exist: " + filepath);
			}			
//			System.err.println("loadTexture exists? " + file.exists());
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

//		System.err.println("loadTexture erfolgreich?: " + texture);
		return texture;
	}	
	
	
	/**
	 * Grep the first URL in the string and return it.
	 * 
	 * @author Marcel Petrick
	 * @param firstURL String input
	 * @return output String output
	 */
	public static String getFirstURL(String input) {
		if(input != null) {
			if(input.contains(";")) {
				int startIndex = input.indexOf(';');			
				return (input.substring(startIndex + 1, input.indexOf(';', startIndex + 1) - 5));			
			} else {
				return input;
			}
		} else {
//			Stream.err.println("Util::getFirstURL: input == null");
			return null;
		}
	}
	
	/**
	 * Grep the first URL in the string and return it.
	 * 
	 * @author Marcel Petrick
	 * @param firstURL String input
	 * @return output String output
	 */
	public static String grepFirstURL(String input, String filename) {
		String output = "";
		if(input.contains(";")) {
			//more than one url in the string -> extract the first one
			int startIndex = input.indexOf(';');
			int endIndex = input.indexOf(';', startIndex + 1);
			
			//get the first URL
			// + 1: offset, because indexOf delivers (position - 1)
			// -5 : offset, because the "&quot" before the second ';' should be ignored			
			output = input.substring(startIndex + 1, endIndex - 5);			
		} else {
			//do nothing - everything okay
			output = input;
		}
		
//		Stream.err.println("static shit: " + output);
		
		String cache_path = "";
		try {
			URL url_obj = new URL("file:///"+filename);
			cache_path = url_obj.getPath().substring(1, url_obj.getPath().lastIndexOf("/")+1);
			
			File f = new File(output);
			if(!f.exists()) {
				f = new File(cache_path + output);
				if(f.exists()) {
					output = cache_path + output;
				}
			}
		} catch(Exception e) {
//			Stream.err.println("staticHelpers::grepFirstURL:");
			e.printStackTrace();			
		}
		
		return output;
	}
		
	
	/**
	 * Remove the last part of the path, means: remove the filename.
	 * 
	 * @author Marcel Petrick
	 * @param String path with filename
	 * @return String path wihtout filename
	 */
	public static String getPathWithoutLastEntry(String input) {
		String  output = "";
		try {
			File f = new File(input);
			final String cache = f.getPath();
			output = cache.substring(0, cache.lastIndexOf(File.separator));
		} catch(Exception e) {
//			Stream.err.println("Error in Util.getPathWithoutLastEntry"+e.getMessage());
			output = "."+File.separator;
		}
		return output;
	}

	/**
	 * Merge both strings.
	 * 
	 * @author Marcel Petrick
	 */
	public static String resolveURL(String path, String imageUrl) {
		
//		Stream.err.println("\nutil:resolve: path before = " + path);
		path = createAbsolutePath(path);
//		Stream.err.println("\nutil:resolve: path after = " + path);
//		Stream.err.println("util:resolve: imageUrl = " + imageUrl);
		
		String temp;
		while(((temp = imageUrl.substring(0,2)) != null) && temp.equalsIgnoreCase("..")) {
			imageUrl = imageUrl.substring(3, imageUrl.length());

			path = getPathWithoutLastEntry(path);
		}
		
		String output = imageUrl;
		try {
			File f = new File(output);
//			Stream.err.println("util:resolve: anfang output = " + output);

			if(!f.exists()) {
				output = path + File.separator + output;
//				Stream.err.println("util:resolve: nachbeabeitung output = " + output);
				f = new File(output);
				
				if(!f.exists()) {
//					Stream.err.println("Util::resolveURL: error - filepath-handling somehow incorrect. output = " + output);
				}
			}
			
//			Stream.err.println("util:resolve: vor abgabe output = " + output);
			output = f.toURI().toString();
			
		} catch(Exception e) {
//			Stream.err.println("Util::resolveURL: exception");
			e.printStackTrace();			
		}

		return output;
	}
	

	/**
	 * If the filepath of the X3D was already relative,
	 * then try to create an absolute version.
	 * 
	 * @author Marcel Petrick
	 * 
	 * @param String input-path
	 * @return String output-path
	 */
	public static String createAbsolutePath(String path) {
//		Stream.err.println("createAbsolutePath: in = " + path);
		while(path.contains("..")) {
			int index = path.indexOf("..");
			String subString = path.substring(0, index - 1);
//			Stream.err.println("substring = " + subString);
			int indexFromFirstSlash = subString.lastIndexOf('/');
			path = path.substring(0, indexFromFirstSlash) + path.substring(index + 2); 
		}
		
//		Stream.err.println("createAbsolutePath: out = " + path);
		return path;
	}

	
	/**
	 * Creates a wellformed URL from a given String.
	 * 
	 * @author Marcel Petrick
	 * 
	 * @param String filename
	 * @return URL url
	 */
	public static URL stringToCanonicalUrl(String filename) {
		URI x3dURI = null;
		try {
			x3dURI = new URI(filename).normalize();
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
		File file = new File(x3dURI.getPath());
		File fileCanonicalPath = null;
		try {
			fileCanonicalPath = new File(file.getCanonicalPath());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		URL url = null;
		try {
			url = fileCanonicalPath.toURI().toURL();
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}


	/**
	 * Concatenate both, if relative.
	 * 
	 * @param x3dFilename
	 * @param imageUrl
	 * @return
	 */
	public static String createPath(String x3dFilename, String imageUrl) {
		/**
		 * absolute file+path are forbidden until a solution is found!
		 */
		/*
		try {
			File imageUrlFile = new File(new URI(imageUrl));
			
			if(imageUrlFile.exists()) {
				Stream.err.println("XXXXXXXXXXXX erster Test schon erfolgreich: " + imageUrl);
				return imageUrl;
			}
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		*/
		
//		Stream.err.println("\n================ createPath =============");
//		Stream.err.println("x3dFilename: " + x3dFilename);
//		Stream.err.println("imageUrl: " + imageUrl);
		
		String pathOnly = getPathWithoutLastEntry(x3dFilename);
//		Stream.err.println("pathOnly: " + pathOnly);
		
//		File file = new File(pathOnly);
//		Stream.err.println("exists: " + file.exists());
		
		String bothAdded = pathOnly + "/" + imageUrl;
//		Stream.err.println("bothAdded: " + bothAdded);
		File f2 = new File(bothAdded);
//		Stream.err.println("f2 exists: " + f2.exists());
		
		String output = null;
		try {
//			Stream.err.println("creatpath: f2 = " + f2);
			output = f2.getCanonicalPath();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
//		Stream.err.println("output: " + output);
//		File f3 = new File(output);
//		Stream.err.println("output exists: " + f3.exists());
		
		return output;
	}
	
	/**
	 * @author Uwe Mannl
	 * @param value
	 * @return
	 */
	public static float[] splitStringToArray2f(String value) {
		return splitStringToArray2f(value, 0.0f, 0.0f);
	}
	
	/**
	 * @author Uwe Mannl
	 * @param value String value
	 * @param default1
	 * @param default2
	 * @return
	 */
	public static float[] splitStringToArray2f(String value, float default1, float default2) {
		float[] returnValue = { default1, default2 };

		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			for (int i = 0; (i < 2) && (i < results.length); i++) {
				returnValue[i] = Float.valueOf(results[i]);
			}
		}
		return returnValue;
	}
	
	/**
	 * @author Volkmar Kantor, Marcel Petrick
	 * @param value
	 * @return
	 */
	public static double[] splitStringToArray3d(String value) {
		return splitStringToArray3d(value, 0.0d, 0.0d, 0.0d);
	}
	
	/**
	 * @author Uwe Mannl
	 * @param value
	 * @return
	 */
	public static float[] splitStringToArray3f(String value) {
		return splitStringToArray3f(value, 0.0f, 0.0f, 0.0f);
	}	
	
	/**
	 * @author Volkmar Kantor, Marcel Petrick
	 * @param value String value
	 * @param default1
	 * @param default2
	 * @param default3
	 * @return
	 */
	public static double[] splitStringToArray3d(String value, double default1, double default2, double default3) {
		double[] returnValue = { default1, default2, default3 };

		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			for (int i = 0; (i < 3) && (i < results.length); i++) {
				returnValue[i] = Double.valueOf(results[i]);
			}
		}
		return returnValue;
	}

	/**
	 * @author Uwe Mannl
	 * @param value String value
	 * @param default1
	 * @param default2
	 * @param default3
	 * @return
	 */
	public static float[] splitStringToArray3f(String value, float default1, float default2, float default3) {
		float[] returnValue = { default1, default2, default3 };

		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			for (int i = 0; (i < 3) && (i < results.length); i++) {
				returnValue[i] = Float.valueOf(results[i]);
			}
		}
		return returnValue;
	}
	
	/**
	 * @author Uwe Mannl
	 * @param result Tuple2f result
	 * @param value String value (must not be null!)
	 * @return
	 */
	public static Tuple2f splitStringToTuple2f(Tuple2f result, String value) {
		value = value.trim();
		String[] results = value.split(splitExpr);
		result.x = Float.valueOf(results[0]);
		result.y = Float.valueOf(results[1]);
		return result;
	}
	
	/**
	 * @author Uwe Mannl
	 * @param result Tuple3f result
	 * @param value String value (must not be null!)
	 * @return
	 */
	public static Tuple3f splitStringToTuple3f(Tuple3f result, String value) {
		value = value.trim();
		String[] results = value.split(splitExpr);
		result.x = Float.valueOf(results[0]);
		result.y = Float.valueOf(results[1]);
		result.z = Float.valueOf(results[2]);
		return result;
	}
	
	/**
	 * @author Uwe Mannl
	 * @param result Tuple4f result
	 * @param value String value (must not be null!)
	 * @return
	 */
	public static Tuple4f splitStringToTuple4f(Tuple4f result, String value) {
		value = value.trim();
		String[] results = value.split(splitExpr);
		result.x = Float.valueOf(results[0]);
		result.y = Float.valueOf(results[1]);
		result.z = Float.valueOf(results[2]);
		result.w = Float.valueOf(results[3]);
		return result;
	}
	
	/**
	 * 
	 * @param value String with int values
	 * @param def default values
	 * @return
	 */
	public static int[] splitStringToArrayOfInt(String value) {
		value = value.trim();
		String[] results = value.split(splitExpr);

		int[] returnValue = new int[results.length];
		
		for (int i = 0; i < results.length; i++) {
			returnValue[i] = Integer.valueOf(results[i]);
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @param value String with int values
	 * @param def default values
	 * @return
	 */
	public static int[] splitStringToArrayOfInt(String value, int[] def) {
		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			int[] returnValue = new int[results.length];
			
			for (int i = 0; i < results.length; i++) {
				returnValue[i] = Integer.valueOf(results[i]);
			}
			return returnValue;
		}
		return def;
	}
	
	/**
	 * 
	 * @param value String with float values
	 * @param def default values
	 * @return
	 */
	public static float[] splitStringToArrayOfFloat(String value) {
		value = value.trim();
		String[] results = value.split(splitExpr);

		float[] returnValue = new float[results.length];
		
		for (int i = 0; i < results.length; i++) {
			returnValue[i] = Float.valueOf(results[i]);
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @param value String with float values
	 * @param def default values
	 * @return
	 */
	public static float[] splitStringToArrayOfFloat(String value, float[] def) {
		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			float[] returnValue = new float[results.length];
			
			for (int i = 0; i < results.length; i++) {
				returnValue[i] = Float.valueOf(results[i]);
			}
			return returnValue;
		}
		return def;
	}
	
	/**
	 * 
	 * @param value String with double values
	 * @param def default values
	 * @return
	 */
	public static double[] splitStringToArrayOfDouble(String value, double[] def) {
		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			double[] returnValue = new double[results.length];
			
			for (int i = 0; i < results.length; i++) {
				returnValue[i] = Double.valueOf(results[i]);
			}
			return returnValue;
		}
		return def;
	}

	/**
	 * Splits a string containing multiple strings to an array of strings.
	 * The original string can have one of the following forms:
	 * <ul>
	 * <li>'"bla" "ble"'
	 * <li>'"bla","ble"'
	 * <li>'"bla"'
	 * <li>'bla'
	 * <li>"bla"
	 * </ul>
	 * @param value String with String values
	 * @param def default values
	 * @return
	 */
	public static String[] splitStringToArrayOfString(String value) {
		value = value.trim();
		String[] returnValue = value.split("\"[, ]+\"");
		returnValue[0] = returnValue[0].replace("\"", "");
		returnValue[returnValue.length-1] = returnValue[returnValue.length-1].replace("\"", "");
		return returnValue;
	}
	
	/**
	 * Splits a string containing multiple strings to an array of strings.
	 * The original string can have one of the following forms:
	 * <ul>
	 * <li>'"bla" "ble"'
	 * <li>'"bla","ble"'
	 * <li>'"bla"'
	 * <li>'bla'
	 * <li>"bla"
	 * </ul>
	 * @param value String with String values
	 * @param def default values
	 * @return
	 */
	public static String[] splitStringToArrayOfString(String value, String[] def) {
		if (value != null) {
			value = value.trim();
			String[] returnValue = value.split("\"[, ]+\"");
			returnValue[0] = returnValue[0].replace("\"", "");
			returnValue[returnValue.length-1] = returnValue[returnValue.length-1].replace("\"", "");
			return returnValue;
		}
		return def;
	}
	
	/**
	 * Split String on Whitespaces and return values as double-Array
	 * @author Volkmar Kantor, Marcel Petrick
	 * @param value
	 * @return
	 */
	public static double[] splitStringToArray4d(String value) {
		return splitStringToArray4d(value, 0.0d, 0.0d, 0.0d, 0.0d);
	}
	
	/**
	 * Split String on Whitespaces and return values as double-Array
	 * @author Volkmar Kantor, Marcel Petrick
	 * @param value
	 * @return
	 */
	public static float[] splitStringToArray4f(String value) {
		return splitStringToArray4f(value, 0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	/**
	 * Split String on Whitespaces and return values as double-Array
	 * @author Volkmar Kantor, Marcel Petrick
	 * @param value String value
	 * @param default1
	 * @param default2
	 * @param default3
	 * @param default4
	 * @return
	 */
	public static double[] splitStringToArray4d(String value, double default1, double default2, double default3, double default4) {
		double[] returnValue = { default1, default2, default3, default4 };

		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			for (int i = 0; (i < 4) && (i < results.length); i++) {
				returnValue[i] = Double.valueOf(results[i]);
			}
		}
		return returnValue;
	}
	
	/**
	 * Split String on Whitespaces and return values as double-Array
	 * @author Volkmar Kantor, Marcel Petrick
	 * @param value String value
	 * @param default1
	 * @param default2
	 * @param default3
	 * @param default4
	 * @return
	 */
	public static float[] splitStringToArray4f(String value, float default1, float default2, float default3, float default4) {
		float[] returnValue = { default1, default2, default3, default4 };

		if (value != null) {
			value = value.trim();
			String[] results = value.split(splitExpr);

			for (int i = 0; (i < 4) && (i < results.length); i++) {
				returnValue[i] = Float.valueOf(results[i]);
			}
		}
		return returnValue;
	}
	
	/**
	 * Extract the path-part as a String from the URL.
	 * 
	 * @param URL url
	 * @return String path without the filename
	 */
	public static String getRealPath(URL url) {
		String cache = url.getPath();
		int beginIndex = 0;
		if (cache.startsWith("/"))
			beginIndex = 1;
		int endIndex = cache.lastIndexOf("/")+1;
		String returnValue = cache.substring(beginIndex, endIndex);
		returnValue = returnValue.replace("%20", " ");
		return returnValue;
	}
	
	/**
	 * convert a string into an array of Vector3d
	 * used for IndexedFaceSet
	 * 
	 * @author Marcel Petrick
	 * @param String input
	 * @return Vector3d[]
	 * 
	 * @deprecated DON'T USE - UNTESTED, use splitStringToArray3d(String input) instead
	 */
	static public Vector3d[] StringToVector3dArray(String input) {		
		//split into the different values
		String[] temp = input.split(",");
		
		//convert
		String[] results;
		Vector3d[] returnvalue = new Vector3d[temp.length];
		
		for(int i = 0; i < temp.length; i++) {
			results = temp[i].split(" +");
			
			String[] resultsWithoutEmpyString = new String[3];
			int b = 0;
			for(int a = 0; a < 3;) {
				if (results[b].length() != 0) {
					resultsWithoutEmpyString[a] = results[b];
					a++;
				}
				b++;
			}
			results = resultsWithoutEmpyString;

			returnvalue[i] = new Vector3d(Double.valueOf(results[0]), Double.valueOf(results[1]), Double.valueOf(results[2]));			
		}
		
		return returnvalue;
	}
	
	
//	public static String createName(Element sceneChild, X3DTransform parentNode){
//		String nameString = sceneChild.getAttributeValue("DEF");
//		if ((nameString == null || nameString == "") && parentNode != null){
//			nameString = parentNode.getName();
//			return nameString;
//		}
//		return nameString;
//	}
	
	/**
	 * This methode returns a transformation matrix with a rotational component.
	 * The rotation transforms the vec1 to vec2. 
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	public static Matrix4d vectorsToTransMatrix(Vector3d vec1, Vector3d vec2) {
		Vector3d rotVec = new Vector3d();
		rotVec.cross(vec1, vec2);
		AxisAngle4d rot = null;
		// vec1 and vec2 are parallel
		if (rotVec.equals(new Vector3d(0, 0, 0))) {
//			// vec1 looks straight z or y direction
//			if (((vec1.x == 0) && (vec1.y == 0)) || ((vec1.x == 0) && (vec1.z == 0))) 
//				rotVec.x = 1;
//			// vec1 looks straight z direction
//			else if ((vec1.y == 0) && (vec1.z == 0))
//				rotVec.y = 1;
//			// two coordinates of vec1 are not zero
//			else {
//				if (vec1.x != 0) {
//					rotVec.x = -vec1.x;
//					rotVec.y = vec1.y;
//					rotVec.z = vec1.z;
//				}
//				else {
//					rotVec.x = vec1.x;
//					rotVec.y = -vec1.y;
//					rotVec.z = vec1.z;
//					
//				}
//			}
			rotVec = findOrthogonalVector(vec1);
			rot = new AxisAngle4d(rotVec, vec1.angle(vec2) + Math.PI);
		}
		else
			rot = new AxisAngle4d(rotVec, vec1.angle(vec2));
		
		Matrix4d transMat = new Matrix4d();
		transMat.setIdentity();
		transMat.setRotation(rot);
		return transMat;
	}
	
	/**
	 * Returns an orthogonal vector to given vector. This is undefined
	 * in any direction and not normalized.
	 * @param vec1
	 * @return
	 */
	public static Vector3d findOrthogonalVector(Vector3d vec1) {
		Vector3d returnVector = new Vector3d(0, 0, 0);
		// vec1 looks straight z or y direction
		if (((vec1.x == 0) && (vec1.y == 0)) || ((vec1.x == 0) && (vec1.z == 0))) 
			returnVector.x = 1;
		// vec1 looks straight z direction
		else if ((vec1.y == 0) && (vec1.z == 0))
			returnVector.y = 1;
		// two coordinates of vec1 are not zero
		else {
			if (vec1.x != 0) {
				returnVector.x = -vec1.x;
				returnVector.y = vec1.y;
				returnVector.z = vec1.z;
			}
			else {
				returnVector.x = vec1.x;
				returnVector.y = -vec1.y;
				returnVector.z = vec1.z;
				
			}
		}	
		return returnVector;
	}
	
	/**
	 * Checks if given points are on a straight line.
	 * @param vectors
	 * @return
	 */
	public static boolean pointsOnLine(ArrayList<Point3d> points) {
		
		if (points.size() <= 2)
			return true;
		
		Vector3d refVec = new Vector3d(points.get(0).x - points.get(1).x,
				points.get(0).y - points.get(1).y,
				points.get(0).z - points.get(1).z);
		refVec.normalize();
		
		for (int i = 2; i < points.size(); i++) {
			Vector3d newVec = new Vector3d(points.get(0).x - points.get(i).x,
					points.get(0).y - points.get(i).y,
					points.get(0).z - points.get(i).z);
			newVec.normalize();
			if (!newVec.equals(refVec))
				return false;
		}
				
		return true;
	}
	
}