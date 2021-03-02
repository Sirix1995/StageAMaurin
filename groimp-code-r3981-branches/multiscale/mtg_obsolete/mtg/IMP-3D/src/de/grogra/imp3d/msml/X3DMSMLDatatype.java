package de.grogra.imp3d.msml;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.shading.ImageMap;
import de.grogra.math.ChannelMap;
import de.grogra.math.ColorMap;
import de.grogra.math.Tuple3fType;
import de.grogra.msml.MSMLDatatype;

public abstract class X3DMSMLDatatype implements MSMLDatatype
{
	static final String GROIMPDATATYPE_NAMESPACE = "http://grogra.de/msml/datatypes/groimp";
	static final String X3D_NAMESPACE = "http://www.web3d.org/specifications";
	
	protected static String getStringFromVector3f(Vector3f v3f){
		return String.valueOf(v3f.x)+" "+String.valueOf(v3f.y)+" "+String.valueOf(v3f.z);
	}
	
	protected static String getStringFromVector2f(Vector2f v2f){
		return String.valueOf(v2f.x)+" "+String.valueOf(v2f.y);
	}
	
	protected static String getStringFromVector3d(Vector3d v3d){
		return String.valueOf(v3d.x)+" "+String.valueOf(v3d.y)+" "+String.valueOf(v3d.z);
	}
	
	protected static String getStringFromAxisAngle4d(AxisAngle4d aa4d){
		return String.valueOf(aa4d.x)+" "+String.valueOf(aa4d.y)+" "+String.valueOf(aa4d.z)+" "+String.valueOf(aa4d.angle);
	}
	
	/**
	 * Converts a rotation matrix to an axis-angle representation.
	 * see Book:"Graphics Gems 1" by Andrew S. Glassner page 466
     * @return AxisAngle4d, contains rotMatrix in axis-angle representation
     * @param	rotMatrix - contains a rotationmatrix
     */
	public static AxisAngle4d convertMatrix2AxisAngle (Matrix3d rotMatrix) {
		AxisAngle4d aa4d = new AxisAngle4d();
		aa4d.angle = 
			Math.acos((rotMatrix.m00 + rotMatrix.m11 + rotMatrix.m22 - 1) / 2.0);
		if (Math.sin(aa4d.angle) != 0) {
			double denominator = 2.0 * Math.sin(aa4d.angle);
			aa4d.x = (rotMatrix.m21 - rotMatrix.m12) / denominator;
			aa4d.y = (rotMatrix.m02 - rotMatrix.m20) / denominator;
			aa4d.z = (rotMatrix.m10 - rotMatrix.m01) / denominator;
		}
		return aa4d;
	} // convertMatrix2AxisAngle

	protected static Vector3f getVector3fFromChannelMap(ChannelMap cm){
		Vector3f v3f=new Vector3f();
		if (cm instanceof ColorMap){
			Tuple3fType.setColor(v3f,((ColorMap)cm).getAverageColor());
		}
		else if (cm instanceof ImageMap){
			Tuple3fType.setColor(v3f,((ImageMap)cm).getAverageColor());
		}
		else{
			v3f=null;
		}
		return v3f;
	}
	
	protected static Vector2f getVector2fFromString(String s){
		StringTokenizer st = new StringTokenizer(s);
		return new Vector2f(Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()));
	}
	
	protected static Vector3f getVector3fFromString(String s){
		StringTokenizer st = new StringTokenizer(s);
		return new Vector3f(Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()));
	}
	
	protected static AxisAngle4f getAxisAngle4fFromString(String s){
		StringTokenizer st = new StringTokenizer(s);
		return new AxisAngle4f(Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()));
	}

	protected static String getStringFromBufferedImage(BufferedImage img){
		StringBuffer result=new StringBuffer("");
		if (!(img.equals(null))){
			WritableRaster wR = img.getRaster();
			int width = img.getWidth();
			int height = img.getHeight();
			int numBands = wR.getNumBands();
			result.append(""+width+" "+height+" "+numBands+" ");
			StringBuffer pixel = new StringBuffer("0x");
			int[] samplesPerPixel = new int[numBands];
			int temp = height-1;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
				    samplesPerPixel = wR.getPixel(x, Math.abs(temp-y), samplesPerPixel);
					switch (numBands){
						case 1:{
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[0])));
						    break;
						}
						case 2:{
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[0])));
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[1])));
						    break;
						}
						case 3:{
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[0])));
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[1])));
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[2])));
						    break;
						}
						case 4:{
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[0])));
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[1])));
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[2])));
						    pixel.append(checkLength(Integer.toHexString(samplesPerPixel[3])));
						    break;
						}
					}
					result.append(pixel+" ");
					pixel.setLength(0);
					pixel.append("0x");
				}
				result.append(" ");
			}

		}
		return result.toString();
	}
	
	//converts Stringrepresentation of a 1 digit hexnumber to 2digits
	//for instance A to 0A, 9 to 09
	private static String checkLength(String hexnum){
	    if (hexnum.length()==1){
	        return "0"+hexnum;
	    }
	    else{
	        return hexnum;
	    }
	}

	protected static BufferedImage getBufferedImageFromSFImage(String s){
		StringTokenizer st = new StringTokenizer(s);
		int width=0,height=0,numOfComponents=0,bIFormat=0,x=0,y=0;
		BufferedImage bi=null;
		width=Integer.parseInt(st.nextToken());
		height=Integer.parseInt(st.nextToken());
		numOfComponents=Integer.parseInt(st.nextToken());
		switch (numOfComponents){
			case 1:{bIFormat=BufferedImage.TYPE_BYTE_GRAY;
		 			break;}
			case 2:{bIFormat=BufferedImage.TYPE_BYTE_GRAY;
		 			break;}
			case 3:{bIFormat=BufferedImage.TYPE_INT_RGB;
		 			break;}
			case 4:{bIFormat=BufferedImage.TYPE_INT_ARGB;
		 			break;}
		}			
		bi=new BufferedImage(width,height,bIFormat);
		int height1=height-1;
		while(st.hasMoreTokens())
		{
			bi.setRGB(x%width,height1-y,makeColor(st.nextToken(),numOfComponents));
			x++;
			if (x%width==0) y++;
		}
		return bi;
	}

	private static int makeColor(String rgba, int numOfComponents) {
		if (numOfComponents==4){
			if (rgba.toLowerCase().startsWith("0x")){
				int l=rgba.length();
				String alpha="0x"+rgba.substring(l-2,l);
				String rgb=rgba.substring(0,l-2);
				return Integer.decode(alpha).intValue()<<24|Integer.decode(rgb).intValue();
			}else{
				int rgbavalue=Integer.parseInt(rgba);
				int rgb=rgbavalue>>8;
				int alpha=rgbavalue&255;
				return alpha<<24|rgb;
			}
		}else{
			return Integer.decode(rgba).intValue();
		}
    }
	
	protected static URL getURLfromString(URL baseURL, String s)throws IOException{
		return new URL(baseURL,s);
	}

	protected static String getAttributeContent(org.w3c.dom.Node node, String attrname)
	{
		String result = "";
		org.w3c.dom.Node n = node.getAttributes ().getNamedItem (attrname);
		if (n != null)
		{
			result = n.getNodeValue ();
		}
		return result;
	}
}