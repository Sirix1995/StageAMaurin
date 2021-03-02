package de.grogra.imp3d.gl20;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20GfxServer;
import de.grogra.vecmath.Math2;

class GL20ResourceTexture extends GL20Resource {
	/**
	 * image attribute bit
	 */
	final private static int IMAGE = 0x1;
	
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;
	
	/**
	 * image attribute
	 */
	private Image image = null;
	
	/**
	 * texture index, will be assigned by <code>GL20GfxServer</code> 
	 */
	private int textureIndex = 0;
	
	/**
	 * texture dimension
	 */
	private int textureDimensions = 0;
	
	/**
	 * opaque texture state
	 */
	private boolean opaqueTexture = true;
	
	public GL20ResourceTexture() {
		super(GL20Resource.GL20RESOURCE_TEXTURE);
	}
	
	/**
	 * check if this texture is opaque
	 * 
	 * @return <code>true</code> texture is opaque
	 * <code>false</code> texture is not opaque
	 */
	final public boolean isTextureOpaque() {
		return ((textureIndex != 0) &&
				(textureDimensions != 0) &&
				(image != null)) ? opaqueTexture : true;
	}
	
	/**
	 * get the number of dimensions of this <code>GL20ResourceTexture</code>
	 * 
	 * @return <code>0</code> this <code>GL20ResourceTexture</code> is not valid
	 * otherwise the number of dimensions of this <code>GL20ResourceTexture</code>
	 */
	final public int getTextureDimensions() {
		return ((textureIndex != 0) &&
				(image != null)) ? textureDimensions : 0;
	}
	
	/**
	 * set up an image to this <code>GL20ResourceTexture</code>
	 * 
	 * @param image the image that should be set to this <code>GL20ResourceTexture</code>
	 * @return <code>true</code> image was successful set to this <code>GL20ResourceTexture</code>
	 * <code>false</code> 
	 */
	public boolean setImage(Image image) {
		boolean returnValue = false;
		
		if (this.image != image) {
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			
			if (textureIndex != 0) {
				// destroy old texture
				gfxServer.deleteTextureIndex(textureDimensions,textureIndex);
				textureIndex = 0;
				textureDimensions = 0;
				opaqueTexture = true;
				image = null;
			}
			
			if (image != null) {
				int _TextureDimensionCreate = 1;
				
				final int imageWidth = Math2.roundUpNextPowerOfTwo(image.getWidth(null));
				final int imageHeight = Math2.roundUpNextPowerOfTwo(image.getHeight(null));
				final int imageDepth = 1;
				
				// FIXME image handling for 3 Dimensions
				Image imagePowerOf2 = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
				
				int pixelData[] = new int[imageWidth * imageHeight];
				PixelGrabber pixelGrabber = new PixelGrabber(imagePowerOf2,0,0,imageWidth,imageHeight,pixelData,0,imageWidth);
				try {
					pixelGrabber.grabPixels();
					returnValue = true;
				} catch(InterruptedException e) {
					// failure
				}
				
				if ((returnValue == true) && ((pixelGrabber.getStatus() & ImageObserver.ABORT) != 0)) {
					// failure
					returnValue = false;
				}
								
				if (returnValue == true) {
					// calculate dimensions of the texture
					
					if (imageHeight > 1) {
						_TextureDimensionCreate = 2;
						if (imageDepth > 1)
							_TextureDimensionCreate = 3;
					}
										
					// create texture index
					textureIndex = gfxServer.createTextureIndex(_TextureDimensionCreate);
					
					// set up image
					int _TextureDimensions = 0;
					if ((_TextureDimensions = gfxServer.setTextureImage(textureIndex, imageWidth, imageHeight, imageDepth, pixelData)) == 0)
						returnValue = false;
					else {
						if (_TextureDimensions == _TextureDimensionCreate)
							textureDimensions = _TextureDimensions;
						else
							returnValue = false;
					}
				}
				
				if (returnValue == false) {
					if (textureIndex != 0) {
						gfxServer.deleteTextureIndex(_TextureDimensionCreate, textureIndex);
						textureIndex = 0;
					}
					
					textureDimensions = 0;
					opaqueTexture = true;
				}
				else {
					this.image = image;
					
					// scan image for alpha values
					opaqueTexture = true;
					int offset = 0;
					for (int currentDepth=0;(currentDepth < imageDepth) && (opaqueTexture == true);currentDepth++) {
						for (int currentHeight=0;(currentHeight < imageHeight) && (opaqueTexture == true);currentHeight++) {
							for (int currentWidth=0;(currentWidth < imageWidth) && (opaqueTexture == true);currentWidth++) {
								if (((pixelData[offset] >> 24) & 0xFF) != 0xFF)
									opaqueTexture = false;
								offset++;
							}
						}
					}
				}
			}
			else
				returnValue = true;
						
			changeMask |= IMAGE;
		}
		else
			returnValue = true;
		
		return returnValue;
	}
	
	/**
	 * bind this <code>GL20ResourceTexture</code> to a given <code>textureStage</code>
	 * 
	 * @param textureStage the texture stage/unit where this <code>GL20ResourceTexture</code>
	 * bound
	 * @return <code>true</code> this <code>GL20ResourceTexture</code> was successful bound
	 * <code>false</code> this <code>GL20ResourceTexture</code> wasn't bound
	 * <code>textureStage</code> OR this <code>GL20ResourceTexture</code> isn't valid
	 */
	final public boolean bindTexture(int textureStage) {
		boolean returnValue = false;
		
		if ((textureIndex != 0) &&
			(textureDimensions != 0) &&
			(image != null)) {
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			returnValue = gfxServer.bindTextureIndex(textureDimensions, textureIndex, textureStage);
		}
		
		return returnValue;
	}
	
	/**
	 * unbind this <code>GL20ResourceTexture</code> from a given <code>textureStage</code>
	 * 
	 * @param textureStage the texture stage/unit where this <code>GL20ResourceTexture</code>
	 * should be unbound
	 */
	final public void unbindTexture(int textureStage) {
		if ((textureIndex != 0) &&
			(textureDimensions != 0) &&
			(image != null)) {
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			gfxServer.unbindTexture(textureDimensions, textureStage);
		}
	}
	
	/**
	 * check if this <code>GL20ResourceTexture</code> is up to date.
	 * 
	 * @return <code>true</code> this <code>GL20ResourceTexture</code> is up to date
	 * <code>false</code> this <code>GL20ResourceTexture</code> need an update via
	 * <code>update()</code>
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();
	}
	
	/**
	 * update the state of this <code>GL20ResourceTexture2D</code>
	 */	
	public void update() {
		if (changeMask != 0) {
			if ((changeMask & IMAGE) != 0) {
				// image was already updated syncronously with setImage()
			}
			
			changeMask = 0;
		}
		
		super.update();
	}
	
	/**
	 * destroy this <code>GL20ResourceTexture2D</code>
	 */
	public void destroy() {
		if ((textureIndex != 0) &&
			(textureDimensions != 0) &&
			(image != null)) {
			GL20GfxServer gfxServer = GL20GfxServer.getInstance();
			gfxServer.deleteTextureIndex(textureDimensions, textureIndex);
			textureIndex = 0;
		}
		
		super.destroy();
	}
}