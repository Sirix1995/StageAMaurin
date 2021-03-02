package de.grogra.ext.sunshine;

import static javax.media.opengl.GL.GL_CLAMP;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_COMPLETE_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT;
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_RGBA32F_ARB;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_RECTANGLE_ARB;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.TimerTask;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.glu.GLU;
import net.goui.util.MTRandom;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.StreamUtil;
import de.grogra.ext.sunshine.acceleration.SunshineAccelerator;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.imp3d.gl.TextureManager;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.util.Debug;

/**
 * this class is the framework for the fragment shader
 * @author Thomas
 *
 */
public abstract class SunshineRaytracer implements GLEventListener
{
	// to get the parameters for the raytracer
	protected static final String GRID_SIZE 		= "grid";
	protected static final String REC_DEEP 			= "rec_deep";
	protected static final String ACCELERATOR		= "accelerator";
	protected static final String TILE_SIZE 		= "Additional_options/tile_size";
	protected static final String REFRESH_TIME 		= "Additional_options/refresh";
	protected static final String LOOP_STEPS		= "Additional_options/loopSteps";
	protected static final String SEP_SEC_RAY		= "SpecialOptions/sepSecRaySpawn"; 
	protected static final String SEP_UV_MAPPING	= "SpecialOptions/sepUVMapping"; 

	protected static final int STATE 		= 3;
	protected static final int ATTENUATION 	= 4;
	
	protected String RAYPROCESSOR;
	
	// This constant is the number of kernel calls until a glFinish has to be invoke
	// to ensure that all commands called prior are completed. The number of calls
	// is just tested and might be to high or two low in the future.
	protected static final int COMMANDS_UNTIL_SYNCH_CALL	= 5000;
	
	protected static final int MILLIS_UNTIL_SYNCH_CALL	= 4000;
	
	// set property grogra.debug.sunshine to enable debugging
	protected static final boolean DEBUG = Debug.debug("sunshine");
	
	// set property grogra.debug.sunshine.trace to enable tracing
	protected static final boolean TRACE = Debug.debug("sunshine.trace");
	
	// Data declaration
	protected boolean renderReady = false;
	
	protected final GLU glu = new GLU();
	
	// number of attachments to use for ping-pong
	protected static final int NUM_OF_ATTACHMENTS = 8;
	protected static final int NUM_OF_ATTACHMENTS_PER_FBO = 4;
	
	/**
	 * determine the current buffer for ping-pong
	 */ 
	protected int active;
	
	// the FBO
	protected int[] fbo = new int[1];
	
	/**
	 * Number of Ping-Pong states
	 */ 
	protected int pStates;
	
	/**
	 * the input output textures
	 */ 
	protected int[][] ioTextures;

	
	/**
	 * the scene texture
	 */
	protected int[] sceneTexture = new int[1];
	
	/**
	 * the texture atlas
	 */
	protected int[] texTexture = new int[1];
	
	/**
	 * the kd tree
	 */
	protected int[] treeTexture = new int[1];
	
	/**
	 * Tex ID for the seed texture
	 */
	protected int[] seedTex = new int[1];
	
	/**
	 * GL_COLOR_ATTACHMENT0_EXT;
	 * GL_COLOR_ATTACHMENT1_EXT;
	 */
	protected int[] attachmentpoints;

	/**
	 * GL_TEXTURE_RECTANGLE_ARB
	 * GL_TEXTURE_RECTANGLE_NV not needed
	 */
	protected int texTarget = GL_TEXTURE_RECTANGLE_ARB;
	protected int texInternalFormat = GL_RGBA32F_ARB;
	
	
	// the texture handler
	protected ObjectHandler oh;
	
	
	/**
	 * manage textures stored in OpenGL server side memory
	 */
	final TextureManager textureManager = new TextureManager ();
	
	protected SunshineAccelerator octree;
	
	// width and height of final image
	protected int imageWidth;
	protected int imageHeight;
	
	protected int outputMode = Image.OUTPUT_LOG; //Image.OUTPUT_RAW;
	
	// number of tiles in horizontal and vertical direction
	protected int partsHeight;
	protected int partsWidth;
	
	// the offscreen render buffer
	protected GLPbuffer pBuffer;
	
	// the size of one tile
	public int tileWidth = 256;
	public int tileHeight = 256;
	
	protected int loopStart = 0;
	protected int loopStop;
	protected int loopSteps;
	
	protected int lightCount;
	
	protected int superSample = 4;
	protected int recursionDepth;
	protected int grid = 3;
	
	protected int sceneSize;
	protected int textureSizeX;
	protected int textureSizeY;
	protected int[] objects;
	protected int[] triangles;
	protected int allTriangles = 0;
	protected int x; // TODO what is x ?
	protected int y; // TODO what is y ?
	protected ProgressMonitor monitor;
	protected long lastRefreshTime;
	protected long refreshInterval;
	protected long lastTimeMillis;
	protected boolean accelerate = false;
	protected int[] vp = new int[4];
	
	// To decide when an glFinish() call has to be set we need to count the kernel counts.
	protected int kernelCallCounter 	= 0;
	// To decide when an glFinish() call has to be set we need to now the last time.
	protected long lastGlFinishCall		= 0;
	
	// current position of tile being rendered
	protected int currentTileX;
	protected int currentTileY;
	
	// current sample for supersampling, 0 <= currentSample < superSample
	protected int currentSample;
	
	// 0 <= currentRecursionDepth < recursionDepth
	protected int currentRecursionDepth;
	
	/**
	 * the drawable on which it is rendered
	 */ 
	protected GLAutoDrawable drawable;
	
	// This is the temp ByteBuffer for reading the pixels of the current tile.
	protected ByteBuffer tileBB;
	protected ByteBuffer imageBB;
	
	// current state of state machine in processNextKernel()
	protected int state = STATE_DONE;
	protected boolean stop;
	static final int STATE_DONE 		= 0;
	static final int STATE_STARTUP 		= 1;
	static final int STATE_INIT 		= 2;
	static final int STATE_GENRAY 		= 3;
	static final int STATE_TRACE_EYERAY = 4;
	static final int STATE_FINAL 		= 5;
	
	protected Options opts;
	
	
	protected void initialize(Options opts, ProgressMonitor progress,
			ObjectHandler oh, int width, int height, int scene, 
			int[] objects, int[] triangles)
	{
		this.opts 			= opts;
		this.oh 			= oh;
		monitor 			= progress;			
		imageWidth			= width;
		imageHeight 		= height;
		tileWidth 			= getNumericOption(TILE_SIZE, 256).intValue();			
		tileHeight 			= tileWidth;			
		
		grid 				= getNumericOption(GRID_SIZE, 2).intValue();
		superSample 		= grid*grid;
		recursionDepth		= getNumericOption(REC_DEEP, 1).intValue();
		sceneSize			= scene;
		accelerate			= getBooleanOption(ACCELERATOR, false);
		
		// Some additional options for control the render process
		loopSteps			= getNumericOption(LOOP_STEPS, 1000).intValue();
		
		tileBB 				= BufferUtil.newByteBuffer( tileWidth * tileHeight * ObjectHandler.RGBA );		
		imageBB				= BufferUtil.newByteBuffer( width * height * ObjectHandler.RGBA );
		
		lightCount			= oh.getLightCount();
		
		int imageCount 		= oh.getImageCount();
		x = imageCount;
		y = 1;
		
		// calculates the size of the texture atlas
		if(imageCount > 8)
		{
			y = imageCount % 8 == 0 ? imageCount / 8 : imageCount / 8 + 1;
			x = (int)Math.ceil(  (float)imageCount / (float)y );	
		}
		
		textureSizeX = x*512;
		textureSizeY = y*512;
		
		this.objects 	= objects;
		this.triangles	= triangles;
		
		// To ensure that glFinish is not called at the beginning.
		lastGlFinishCall	= MILLIS_UNTIL_SYNCH_CALL;
	}
	
	public void display(GLAutoDrawable drawable)
	{
		processNextKernel();
	} //display
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height)
	{
		GL gl = drawable.getGL();

		if (height == 0)
			height = 1;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	} //reshape
	
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
	} // displayChanged
	
	
	protected class SunshineAction extends TimerTask
	{
		public void run() 
		{
			try 
			{
				// perform another rendering step
				drawable.display();
			} 
			catch (Exception e) 
			{
				System.err.println("state = " + state);
				System.err.println("currentSample = " + currentSample);
				System.err.println("currentRecursionDepth = " + currentRecursionDepth);
				System.err.println("currentTileX = " + currentTileX);
				System.err.println("currentTileY = " + currentTileY);
				e.printStackTrace();
				
				shutDown(drawable);
				setRenderReady(true);				

				// mark render job as finished and exit loop
				state = STATE_DONE;
				synchronized (sunshineAction) 
				{
					sunshineAction.notify();
				}
			}
		}
	}
	
	protected final SunshineAction sunshineAction = new SunshineAction();
	// note that sunshineAction is also used as a lock
	// to synchronize to the end of rendering
	
	
	
	// start the render passes
	public void startRender()
	{
		// status report
		if (monitor != null)
		{
			monitor.setProgress (Resources.msg ("renderer.rendering",
					new Float (0)), 0);
		}
		
		// set initial conditions for rendering
		state = STATE_STARTUP;
		currentTileX = 0;
		currentTileY = 0;
		drawable = pBuffer;
		lastTimeMillis = System.currentTimeMillis();
		
		// schedule sunshineAction for first execution
		// this will switch to the AWT-Thread (which also is the OpenGL-Thread)
		// and start execution of the rendering process from there
		EventQueue.invokeLater(sunshineAction);

		// wait for rendering to finish
		synchronized(sunshineAction) 
		{
			try 
			{
				sunshineAction.wait();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
		// destroy resources
		pBuffer.destroy();

		if(monitor != null)
		{
			monitor.setProgress (Resources.msg ("renderer.done"),
					ProgressMonitor.DONE_PROGRESS);
		}
	} //starRender
	
	/**
	 * This method count kernel calls and invoke a glFinish() after {@link: #COMMANDS_UNTIL_SYNCH_CALL}
	 * kernel calls were sent to GPU. glFinish ensure, that all commands prior called are completed.
	 * This method should be invoked at the end of state machine.
	 *  
	 * @param gl
	 */
	protected void pollingSynchronisation()
	{
		kernelCallCounter++;
		
		if(kernelCallCounter % COMMANDS_UNTIL_SYNCH_CALL == 0 || 
				(System.currentTimeMillis() - lastGlFinishCall) > MILLIS_UNTIL_SYNCH_CALL)
		{
			GL gl = GLU.getCurrentGL();
			
			gl.glFlush();
			gl.glFinish();
			
			kernelCallCounter 	= 0;
			lastGlFinishCall	= System.currentTimeMillis();
		}
	}
	
	/**
	 * This function computes a StringBuffer with usefully information about the
	 * rendering process. In order to apply the output of this statistic every
	 * renderer should overload this function in their own class.
	 * 
	 * @param time	- End of render process
	 * 
	 * @return StringBuffer - Complete statistics 
	 */
	public StringBuffer getStatistics(long time)
	{
		StringBuffer stats = new StringBuffer ("<html><pre>");
		
		stats.append (de.grogra.ext.sunshine.Resources.msg ("raytracer.statistics",
										new Object[] {imageWidth, imageHeight, 1,
														(int) (time / 60000),
														(time % 60000) * 0.001f
										}
									)
					);
		
		
		int complete	= imageWidth*imageHeight*grid*grid*recursionDepth;
		int primRay		= imageWidth*imageHeight*grid*grid;
		int secRay		= complete - primRay;
		int perSec		= (int) (complete / (((double) time) * 0.001d));
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("rayprocessor.default.statistics", new Object[]{complete, primRay, secRay, perSec}) ); 
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("raytracer.scene.statistics", new Object[]{oh.getObjectCount(), lightCount}) );
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("rayprocessor.pathtracer.statistics", new Object[]{recursionDepth}) );
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("antialiasing.stochastic.statistics", new Object[]{grid, grid}) );
		
		stats.append ("</pre></html>").toString ();
		
		return stats;
	}
	
	public int getGrid()
	{
		return grid;
	} //getGrid
	

	abstract void shutDown(GLAutoDrawable drawable);
	
	
	protected void fillBuffer(ByteBuffer in, ByteBuffer out)
	{
		// Obtaining the actual size of the current tile.
		int w = Math.min(tileWidth, imageWidth - currentTileX*tileWidth);		// w is lower equal then TILE_WIDTH
		int h = Math.min(tileHeight, imageHeight - currentTileY*tileHeight);	// h is lower equal then TILE_HEIGHT
		
		
		int onePixel		= ObjectHandler.RGBA;
		int oneLineInBB 	= imageWidth;
		int widthOffset 	= currentTileX * tileWidth;
		int heightOffset 	= imageWidth * currentTileY * tileHeight;
				

		for(int y = 0; y < h; y++)
		{			
			out.position((y * oneLineInBB + widthOffset + heightOffset) * onePixel);
				
			// Here we have to skip the positions in tileBB when the width 
			// of the drawing part is shorter then a usual tile.
			if(w < tileWidth)
				in.position(y*tileWidth*16);	// Jump to the beginning at the next line
	
			// Copy the tempBB values to the global ByteBuffer
			for(int x = 0; x < w; x++)
			{				
				in.putFloat(out.getFloat());
				in.putFloat(out.getFloat());
				in.putFloat(out.getFloat());
				in.putFloat(out.getFloat());
			} //for x
		} //for y
	}
	

	protected void draw(GLAutoDrawable drawable, int[] vp, int px, int py, int texture)
	{
		GL gl = drawable.getGL();
		
		// Obtaining the actual size of the current tile.
		int w = Math.min(tileWidth, imageWidth - px*tileWidth);		// w is lower equal then TILE_WIDTH
		int h = Math.min(tileHeight, imageHeight - py*tileHeight);	// h is lower equal then TILE_HEIGHT
		
		// Choose the first texture where the pixel data are stored.
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(texTarget, texture);
		gl.glEnable(texTarget);
		
		// Resetting the temporary ByteBuffer
		tileBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, tileBB);
		
		int onePixel		= ObjectHandler.RGBA;
		int oneLineInBB 	= imageWidth;
		int widthOffset 	= px * tileWidth;
		int heightOffset 	= imageWidth * py * tileHeight;
				
		int debug = 0;
		
		if(debug == 0)
		{
			for(int y = 0; y < h; y++)
			{			
				imageBB.position((y * oneLineInBB + widthOffset + heightOffset) * onePixel);
					
				// Here we have to skip the positions in tileBB when the width 
				// of the drawing part is shorter then a usual tile.
				if(w < tileWidth)
					tileBB.position(y*tileWidth*16);	// Jump to the beginning at the next line
				float f = 0;	
				// Copy the tempBB values to the global ByteBuffer
				for(int x = 0; x < w; x++)
				{				
					imageBB.putFloat(tileBB.getFloat());
					imageBB.putFloat(tileBB.getFloat());
					imageBB.putFloat(tileBB.getFloat());
					
					if(px == 0 && py == 0 && false)
					{
						imageBB.putFloat(1);
						f = tileBB.getFloat();
						
						System.out.println(f);
					}
					else
					{
						imageBB.putFloat(tileBB.getFloat());
					}
											
				} //for x
			} //for y
		} 
		else if(debug == 1) 
		{
			outputMode = Image.OUTPUT_RAW;
			
			if(px == 0 && py == 0)
			{	
				try
				{
					PrintWriter f = new PrintWriter(new BufferedWriter(
							new FileWriter("../Sunshine/tmp/output.txt")));
					
					for(int y = 0; y < h; y++)
					{	
						for(int x = 0; x < w; x++)
						{								
							float tmp1 = tileBB.getFloat();
							float tmp2 = tileBB.getFloat();
							float tmp3 = tileBB.getFloat();
							float tmp4 = tileBB.getFloat();
							
							imageBB.putFloat(tmp1);
							imageBB.putFloat(tmp2);
							imageBB.putFloat(tmp3);
							imageBB.putFloat(tmp4);
							
							f.printf("%f, %f, %f, %f\t\t\n", tmp1, tmp2, tmp3, tmp4);
	
							if((x + 1) % tileWidth == 0)
								f.println();						
						}
					}	
					
					f.close();
					
				} catch (IOException e) {
					System.err.println("Could not create file");
				}
			}
		} //debug
		
		gl.glDisable(texTarget);		
	}




	/**
	 * This function is called by the AWT event queue via sunshineAction.
	 * It is a state machine and executes one kernel each time it is called.
	 * If after execution of the kernel the rendering is complete, the flag
	 * renderReady will be set. Otherwise the next kernel will be scheduled
	 * for execution via EventQueue.invokeLater(sunshineAction)
	 */
	abstract void processNextKernel();


	// create the kernels
	abstract void createKernels(GLAutoDrawable drawable);

	
	protected void generateTexture(GLAutoDrawable drawable, int texWidth, int texHeight, int count, int[] target)
	{
		// Just a method overloading with a default format (RGBA) for the texture
		generateTexture(drawable, texWidth, texHeight, count, target, GL_RGBA);
	} //generateTexture
	
	protected void generateTexture(GLAutoDrawable drawable, int texWidth, int texHeight, int count, int[] target, int texFormat)
	{
		GL gl = drawable.getGL();
		gl.glGenTextures(count, target, 0);

		for (int i = 0; i < count; i++)
		{
			gl.glBindTexture(texTarget, target[i]);
			gl.glTexImage2D
			(
				texTarget, 0, texInternalFormat, 
				texWidth, texHeight, 0, texFormat, GL_FLOAT, null
			);
			gl.glTexParameterf(texTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
			gl.glTexParameterf(texTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);
			gl.glTexParameterf(texTarget, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			gl.glTexParameterf(texTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		} // for
	} //generateTexture
	
	protected void armKernel(GLAutoDrawable drawable, Kernel kernel, 
			SunshineSceneVisitor monitor, String intermediates, boolean debug)
	{
		kernel.setDebug(debug);
		kernel.loadSource(drawable, monitor, intermediates);
		
		// compile the kernels
		kernel.compile(drawable);
		
		// Link the shader into a complete GLSL program.
		kernel.linkProgram(drawable);
	} //armKernel
	
	protected void armKernel(GLAutoDrawable drawable, Kernel kernel) 
	{
		// compile the kernels
		kernel.compile(drawable);
		
		// Link the shader into a complete GLSL program.
		kernel.linkProgram(drawable);
	} //armKernel


	protected void setRenderReady(boolean state)
	{
		renderReady = state;
	} //setRenderReady



	/**
	 * divide the texture in #parts  
	 * @param texSize 
	 * @param div the size of the part
	 * @return the count of the parts
	 */
	protected int getParts(int texSize, int div)
	{
		return (texSize-1) / div + 1;
	}
	
	
	protected void renderToTexture(GLAutoDrawable drawable)
	{
		renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
	}
	
	/**
	 * Here we bind the given number of attachments to the buffer which has the graphics card render to.
	 * 
	 * @param drawable
	 */
	protected void renderToTexture(GLAutoDrawable drawable, int count, int[] attachments)
	{
		GL gl = drawable.getGL();
		
		// bind the FBO to render to it
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[0]);
		// set render destination
		gl.glDrawBuffers(count, attachments, 0);
		
		// Then render as normal
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// Clear Screen and Depth Buffer
		// TODO check: can glClear() clear the depth buffer if none was attached to the FBO ?
//		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
		gl.glLoadIdentity();
		
	} //renderToTexture
	
	/**
	 * Here we bind the given number of attachments to the buffer which has the graphics card render to.
	 * 
	 * @param drawable
	 */
	protected void renderToTexture(GLAutoDrawable drawable, int count)
	{
		renderToTexture(drawable, count, attachmentpoints);		
	} //renderToTexture
	
	/**
	 * One FBO can usually bind four attachments. So we have to swap for every calculation step
	 * the actual textures. This is done by this method.
	 * 
	 * @param end
	 */
	protected void attachTextureToFBO(GLAutoDrawable drawable, int end, int[] target)
	{		
		GL gl = drawable.getGL();
		
		for(int i = 0; i < end; i++)
		{
			// attach a given attachment sets to the given attachmentpoints of the single given FBO
			gl.glFramebufferTexture2DEXT
			(
					GL_FRAMEBUFFER_EXT, 
					attachmentpoints[i], 
					texTarget, target[i], 0
			);
		} //for
	} //attachTextureToFBOS

	// error checking
	protected void checkFBO(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		int status = gl.glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		
		if (status != GL_FRAMEBUFFER_COMPLETE_EXT)
		{
			System.out.println("Framebuffer initialization error! " + status);
//			shutDown(drawable);
		} // if
		
		switch(status)
		{
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			{
				System.out.println("INCOMPLETE_MISSING_ATTACHMENT! " + status);
				break;
			}
			case GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT: 
			{
				System.out.println("INCOMPLETE_DUPLICATE_ATTACHMENT! " + status);
				break;
			}
			case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT: 
			{
				System.out.println("INCOMPLETE_DIMENSIONS_EXT! " + status);
				break;
			}
			case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT: 
			{
				System.out.println("GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT! " + status);
				break;
			}
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT: 
			{
				System.out.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT! " + status);
				break;
			}
			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT: 
			{
				System.out.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT! " + status);
				break;
			}
			case GL_FRAMEBUFFER_UNSUPPORTED_EXT: 
			{
				System.out.println("GL_FRAMEBUFFER_UNSUPPORTED_EXT! " + status);
				break;
			}
		} //switch
		
	} //checkFBO
	
	
	protected int swap(int value)
	{		
		return value == 1 ? 0 : 1;
	}


	// load a String out of the file given by the path s
	protected static String loadSourceRaw(String s)
	{	
		String source = null;
		try {
			source = new String(StreamUtil.readAll(SunshineRaytracer.class.getResourceAsStream(s)));
		} catch (Exception e)
		{
			System.out.println(e + s);			
		}		
		return source;
	} //loadShader	



	protected void transferToTexture(GLAutoDrawable drawable,int width, int height, ByteBuffer data)
	{
		transferToTexture(drawable, width, height, data, GL_RGBA);
	}

	// store the given bytebuffer into the active texture
	protected void transferToTexture(GLAutoDrawable drawable,int width, int height, ByteBuffer data, int texFormat)
	{
		GL gl = drawable.getGL();
		data.rewind();
		gl.glTexSubImage2D(texTarget, 0, 0, 0, width, height, texFormat, GL.GL_FLOAT, data);
		data.rewind();
	} //transferToTexture
	
	
	protected void execute(Kernel kernel, int count, int i, int[][] attachment)
	{
		// Switch between the possible active textures..
		active = active == 1 ? 0 : 1;
		
		// attach the corresponding texture to the attachments..
		attachTextureToFBO(drawable, count, attachment[active]);

		kernel.setInputTextures(attachment[active == 1 ? 0 : 1], null);
		
		// and execute the current kernel with the new textures.
		kernel.execute(drawable, currentTileX, currentTileY, i);		
	}
	
	
	protected Number getNumericOption(String key, Number def)
	{
		return (opts != null) ? (Number) opts.get (key, def) : def;
	} //getNumericOption
	
	
	protected boolean getBooleanOption (String key, boolean def)
	{
		return (opts != null) ? ((Boolean) opts.get (key, Boolean.valueOf (def))).booleanValue () : def;
	} //getBooleanOption
	
	
	protected String getIntermediates()
	{
		int objectCount = oh.getObjectCount();
		int triCount 	= oh.getTriCount();
		return 
		"\nconst int sphereCount 	= " + objects[0] 	+ ";\n" +
		"const int boxCount 		= " + objects[1] 	+ ";\n" +
		"const int cylinderCount 	= " + objects[2] 	+ ";\n" +
		"const int planeCount 		= " + objects[3] 	+ ";\n" +
		"const int meshCount 		= " + objects[4] 	+ ";\n" +
		"const int paraCount 		= " + objects[5] 	+ ";\n" +
		"const int lightCount 		= " + objects[6] 	+ ";\n" +
		"const int objectCount		= " + objectCount	+ ";\n" +
		"const int triangleCount	= " + triCount 		+ ";\n" +
		"const int size 			= " + sceneSize 		+";\n" +
		"const float superSample	= " + superSample		+".0;\n" +
		"const int meshPart			= " + oh.getMeshPart()	+";\n" +
		"const int lightPart		= " + oh.getLightPart() +";\n" +
		"bool accelerate			= " + accelerate		+";\n";
	}
	
	
	/**
	 * CPU generated SEED Hack
	 */	
	protected void generateSeedTexture()
	{			
		ByteBuffer seedBB	= BufferUtil.newByteBuffer(tileWidth * tileHeight * ObjectHandler.RGBA);
		MTRandom mtRnd		= new MTRandom(10L);
		
		for(int i = 0; i < tileWidth * tileHeight; i++)
		{
			seedBB.putFloat(mtRnd.nextFloat());
			seedBB.putFloat(mtRnd.nextFloat());
			seedBB.putFloat(0f);
			seedBB.putFloat(0f);
		}
		
		seedBB.rewind();
		
		// generate the seed texture
		generateTexture(drawable, tileWidth, tileHeight, 1, seedTex, GL.GL_RGBA);
		
		// transfer the seed texture
		transferToTexture(drawable, tileWidth, tileHeight, seedBB, GL.GL_RGBA);
		
		//checkSentData(drawable, seedBB.limit(), false, GL.GL_RGBA);
	} //transferSeed
	
	
	protected void cancelPolling()
	{
		if(renderReady)
		{
			state = STATE_FINAL; 
		}
	}
	
	public void stop()
	{
		setRenderReady(true);
	} //stop
	
	protected class Indicator
	{
		private int value;
		
		public Indicator(int val)
		{
			value = val;
		}
		
		public void set(int val)
		{
			value = val;
		}
		
		public int get()
		{
			return value;
		}
		
		public void swap()
		{
			value = value == 1 ? 0 : 1;
		}
	}
	
} // Class

