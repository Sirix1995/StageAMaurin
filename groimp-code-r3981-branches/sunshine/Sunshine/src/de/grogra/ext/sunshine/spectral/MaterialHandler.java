package de.grogra.ext.sunshine.spectral;

import java.awt.Image;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.vecmath.Color4f;

import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.spectral.shader.SunshineChannel;
import de.grogra.ext.sunshine.spectral.shader.SunshineSpectralShader;
import de.grogra.ext.sunshine.spectral.shader.MaterialCollector;
import de.grogra.xl.util.ObjectList;

/**
 * This class is to manage all the different materials for Sunshines Spectral Raytracer.
 * At first all shaders from a scene are collected and stored into HashMap. Before the
 * data can upload to the GPU a ByteBuffer is constructed. During the constructing of 
 * the ByteBuffer all data is for a certain wavelength combined to a square.
 * 
 * @author adgen
 *
 */
public class MaterialHandler {
	
	/** The supported types. **/
	public static enum BxDFTypes 
	{
		LAMBERTIAN,
		COOK_TORRANCE,
		MICROFACET,
		GLASS,
		METAL,
		PHONG
	}
	
	/** Sizes of types which is needed to calculate right addresses. **/
	public final static int[] BxDFSizes = 
	{
		1, 	// LAMBERTIAN
		4,	// COOK_TORRANCE
		5,	// MICROFACET
		4,	// GLASS
		4,	// METAL
		8	// PHONG
	};
	
	/** To decide between two types of container **/
	public static enum MatElemTypeCon {
		SINGLE_VALUE,
		SPECTRAL_CURVE
	}
	
	/** 
	 * A container class to store single value or a spectral curve. 
	 **/
	public class MatElemContainer
	{				
		/** Holds the actual type of this instance. **/
		public MatElemTypeCon type;
		
		/** Float value if this is SINGLE_VALUE container. **/
		private float value;
		
		/** Float value if this is SPECTRAL_CURVE container. **/
		private SunshineRegularSpectralCurve curve;
		
		/** 
		 * According to the channel curve or value is set.
		 * 
		 * @param channel	Part of shaders content. 
		 */
		public void setData(SunshineChannel channel)
		{
			// Obtain the content of that shader.
			Object obj = channel.getContent();
			
			// Decide what has to happens next.
			if(obj instanceof SunshineRegularSpectralCurve)
			{
				// The channel holds spectral curve so this
				// container is a SPECTRAL_CURVE.
				this.type 	= MatElemTypeCon.SPECTRAL_CURVE;
				this.curve 	= (SunshineRegularSpectralCurve) obj;
				
			} else if(obj instanceof Image) {
				
				Image image = (Image) obj;

				if(image != null)
				{
					//TODO: Don't forget the UV-coors;
					
					images.put(imageCount, image);
					
					this.type 	= MatElemTypeCon.SINGLE_VALUE;
					this.value 	= (float) -(imageCount + 1);
					
					imageCount++;
				}
				
			} else if(obj instanceof Float) {
				
				// The channel holds a single value so this
				// container is a SINGLE_VALUE
				this.type 	= MatElemTypeCon.SINGLE_VALUE;
				this.value 	= ((Float) obj).floatValue();
			} else {
				System.out.println("ERROR: Not supported Material Element: " + obj);
			}
		}	
		
		/**
		 * If there is no channel as input, a single value
		 * can be directly set via this method.
		 */
		public void setData(float value)
		{
			this.type = MatElemTypeCon.SINGLE_VALUE;
			this.value = value;			
		}	
		
		public float getValue(float lambda)
		{
			if(type == MatElemTypeCon.SPECTRAL_CURVE)
				return this.curve.sample(lambda);
			else
				return this.value;
		}
	}
	
	/** 
	 * Holds the complete collected data: The internal ObjectList
	 * stores each single channel of a material. 
	 **/
	private HashMap<Integer, ObjectList<MatElemContainer>> materials;
	
	/** Holds hash number to distinguish shader and recognize identically shader **/
	private HashMap<Integer, Integer[]> shaderIDs;
	
	/** Holds images for textures **/
	private HashMap<Integer, Image> images;
	
	private int imageCount = 0;
	
	/** This member counts all elements stored in materials **/
	private int materialElements 		= 0;
	
	/** This member counts all materials stored in texture **/
	private int materialsCount 			= 0;
	
	/**	For upload on GPU memory we need to pack all information in a ByteBuffer **/
	ByteBuffer bbMaterials;
	
	public MaterialHandler() 
	{	
		materials 	= new HashMap<Integer, ObjectList<MatElemContainer>>();
		shaderIDs 	= new HashMap<Integer, Integer[]>();
		images		= new HashMap<Integer, Image>();
	}
	
	public Color4f addShader(SunshineSpectralShader shader)
	{
		Color4f color					= new Color4f();
		int shaderHash					= shader.hashCode();
		Integer[] shaderID 				= shaderIDs.get(shaderHash);
		
		// Check whether a shaderID exists.
		if(shaderID != null)
		{
			// Shader ID exist, so we just have to read the id of the shader 
			// which is already stored, put it into the color...
			color.x = shaderID[0];
			color.y = shaderID[1];
			
			// .. and return because there is nothing else to do.
			return color;
		}
		
		// Check and maybe set a list for this type of BxDF
		ObjectList<MatElemContainer> tmp = getNewMaterial(materialsCount);
		
		// Obtain the type of that shader
		BxDFTypes type				= ((MaterialCollector) shader).getBxDFType();
		
		// Obtain the ordinal number of LAMBERTIAN type
		int bxdfType 				= type.ordinal();
		
		// Setting material ID: to address a spectra instead of an RGB the value must be negative so we
		// start counting by -1 in descending order
		color.x						= -(getMaterialID() + 1);
		// Setting the type of the material
		color.y						= bxdfType;
		
		SunshineChannel[] container = ((MaterialCollector) shader).collectData();
		
		for(int i = 0; i < container.length; i++)
		{
			MatElemContainer elem 	= new MatElemContainer();
			
			elem.setData(container[i]);
			
			tmp.add(elem);
		}
		
		// Store hash code and corresponding id of that shader to recognize duplicates
		shaderIDs.put(shaderHash, new Integer[]{((int) color.x), ((int) color.y)});
		
		// Counting: Add the number of elements of the last added material
		materialElements	 	   += getSize(type);
		
		// Counting the number of collected materials 
		materialsCount++;

		return color;
	}
	
	/**
	 * This function check if there is any entry for a certain type of 
	 * BxDF. If this is the case it returns the FloatList of the BxDF type
	 * otherwise it creates a FloatList.
	 * 
	 * @param bxdfType
	 * @return
	 */
	private ObjectList<MatElemContainer> getNewMaterial(int id)
	{
		ObjectList<MatElemContainer> tmp = new ObjectList<MatElemContainer>();
		
		materials.put(id, tmp);
		
		return tmp;
	}
	
	/**
	 * All different types needs different space for storing in the texture
	 * on GPU-side. This function is a helper function to retrieve the
	 * size of a certain BxDF type
	 * 
	 * @param type
	 * @return
	 */
	public static int getSize(BxDFTypes type)
	{
		return BxDFSizes[type.ordinal()];
	}
	
	/**
	 * To determine the id of an element in the upcoming constructed ByteBuffer this
	 * function look into the list of a certain BxDF type and count the entries
	 * to multiply the number by a predefined size of this type. 
	 * 
	 * @param type
	 * @return
	 */
	private int getMaterialID()
	{
		return materialElements;
	}
	
	/**
	 * When all shaders are collected this method should be invoked, before the ByteBuffer bbMaterials
	 * is read by Sunshine. A ByteBuffer is constructed and filled with the data which has to be
	 * uploaded to GPU. Special care was taken on designing alignment of the material data in that ByteBuffer
	 * to make it easier to read from texture on GPU side. All data for a certain lambda is combined in one 
	 * squared block.
	 * 
	 * @param lambdaMin
	 * @param lambdaMax
	 * @param lambdaStep
	 */
	public int prepareData(int lambdaMin, int lambdaMax, int lambdaStep, int lambdaSamples)
	{
		if(bbMaterials != null)
		{
			System.err.println("MaterialHandler: ByteBuffer was already prepared");
			return 0;
		}
			
		if(lambdaSamples == 1)
			lambdaMax++;
		
		// Dimension for the material texture
		int numOfColumns	= (int) Math.ceil(Math.sqrt(materialElements));
		int numOlElements	= numOfColumns * numOfColumns;

		// Constructing the ByteBuffer with the right dimension
		bbMaterials 		= BufferUtil.newByteBuffer(numOlElements * BufferUtil.SIZEOF_FLOAT * lambdaSamples);
		
		
		/** Here we prepare the ByteBuffer which is later actual uploaded to GPU. **/
		
		// For each Lambda...
		for(int l = lambdaMin; l < lambdaMax; l+= lambdaStep)
		{
			// .. and for each material:
			for(int i = 0; i < materialsCount; i++)
			{
				// Look into the HashMap for a material...
				ObjectList<MatElemContainer> material = materials.get(i);
				
				// ..check if this container is not empty..
				if(material != null)
				{
					// .. and take every element of the material..
					for(int e = 0; e < material.size; e++)
					{
						// .. obtain a value for the actual lambda (l)..
						float value = material.get(e).getValue(l);
					
						// .. and put it into the ByteBuffer
						bbMaterials.putFloat(value);				
						
					}
				}
			}
			
			// In order to achieve a squared alignment for each lambda set, we need to
			// fill the rest of that square with zeros.
			for(int r = 0; r < (numOlElements - materialElements); r++)
				bbMaterials.putFloat(0.0f);
		}
		
		/** Preparation DONE! **/
		
		return numOfColumns;
	}
	
	public ByteBuffer getMaterials()
	{
		return bbMaterials;
	}
	
	public int getElementsCount()
	{
		return materialElements;
	}
	
	public boolean hasImages()
	{
		return imageCount > 0;
	}
	
	public int getImageCount()
	{
		return imageCount;
	}
	
	public Image getImage(int pos)
	{
		return images.get(pos);
	}
}
