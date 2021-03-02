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
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.StreamUtil;

import de.grogra.ext.sunshine.kdTree.KdTree;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.imp3d.gl.TextureManager;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.util.Debug;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;

/**
 * this class is the framework for the fragment shader
 * @author Thomas
 *
 */
public abstract class SunshineRaytracer
{
	// to get the parameters for the raytracer
	protected static final String GRID_SIZE 		= "grid";
	protected static final String REC_DEEP 			= "rec_deep";
	protected static final String ACCELERATOR		= "accelerator";
	
	protected static final String ADD_OPTIONS		= "AdditionalOptions";
	protected static final String REFRESH_INTERVAL 	= ADD_OPTIONS + "/refresh";
	protected static final String TILE_SIZE 		= ADD_OPTIONS + "/tileSize";			
	protected static final String LOOP_STEPS 		= ADD_OPTIONS + "/loopStep";
	
	protected String RAYPROCESSOR;
	
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
		
	// the FBO
	protected int[] fbo = new int[1];
	
	/**
	 * Number of Ping-Pong states
	 */ 
	protected int pStates = 4;
	
	/**
	 * Determine the current buffer for an output destination
	 */ 
	protected int active;
	
	/**
	 * Determine the current buffer for the intersection computation
	 */
	protected int activeTraceData;
	
	/**
	 * Determine the current buffer for the final image output
	 */
	protected int activeImageTex;
	
	/**
	 * To decide which texture has to be involved in ping-pong
	 */
//	protected int ppInputTex;
//	protected int ppOutputTex;
	
	/**
	 * The id of texture which is used as input at first
	 */
	protected int A1Tex = 0;
	
	/**
	 * The id of texture which is used as output at first
	 */
	protected int A2Tex = 1;
	
	/**
	 * The id of texture which is used as the final output
	 */
	protected int Image1Tex = 2;
	
	/**
	 * The id of texture which is used as the final output
	 */
	protected int Image2Tex = 3;
	
	/**
	 * Determine whether a swap has been performed
	 */
	protected boolean isSwaped = false;
	
	/**
	 * the input output textures
	 */ 
	protected int[][] ioTextures = new int[pStates][];

	
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
	protected int[] kdTexture = new int[1];
	
	/**
	 * GL_COLOR_ATTACHMENT0_EXT;
	 * GL_COLOR_ATTACHMENT1_EXT;
	 */
	protected int[] attachmentpoints = new int[4];

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
	
	protected KdTree tree; 
	
	// width and height of final image
	protected int imageWidth;
	protected int imageHeight;
	
	protected int outputMode = Image.OUTPUT_CLAMP; //Image.OUTPUT_RAW;
	
	// number of tiles in horizontal and vertical direction
	protected int partsHeight;
	protected int partsWidth;
	
	// the offscreen render buffer
	protected GLPbuffer pBuffer;
	
	// the size of one tile
	public static int TILE_WIDTH 	= 64;
	public static int TILE_HEIGHT = 64;
	
	protected int countObjects = 0;
	protected int intersectionLoopStep = 200;
	protected int intersectLoopStart = 0;
	protected int intersectLoopStop = intersectionLoopStep;

	protected int lightCounter;
	protected int lightCount;
	
	protected int superSample = 4;
	protected int recursionDepth;
	protected int grid = 3;
	protected int sceneSize;
	protected int textureSizeX;
	protected int textureSizeY;
	protected int[] objects;
	protected int[] trianglesCount;
	protected int allTriangles = 0;
	protected int x; // TODO what is x ?
	protected int y; // TODO what is y ?
	protected ProgressMonitor monitor;
	protected long lastTimeMillis;
	protected long lastRefreshTime;
	protected long refreshInterval;
	protected boolean accelerate = false;
	protected int[] vp = new int[4];
	
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
	protected ByteBuffer tempBB;
	protected ByteBuffer imageBB;
	
	// current state of state machine in processNextKernel()
	protected int state = STATE_DONE;
	static final int STATE_DONE 		= 0;
	static final int STATE_STARTUP 		= 1;
	static final int STATE_INIT 		= 2;
	static final int STATE_GENRAY 		= 3;
	static final int STATE_INTERSECT	= 4;
	static final int STATE_NORMALS 		= 5;
	static final int STATE_SHADOW_TEST	= 6;
	static final int STATE_SHADE 		= 7;
	static final int STATE_FINAL 		= 8;
	static final int STATE_COMBINE 		= 9;
	
	protected Options opts;
	
	abstract void initialize(Options opts, ProgressMonitor progress,
			ObjectHandler oh, int width, int height, int sceneSize, int[] objects, int[] trianglesCount);
	
	protected class SunshineAction implements Runnable 
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
	
	
	
	public int getGrid()
	{
		return grid;
	}



	public ByteBuffer getImage()
	{
		return imageBB;
	}

	public void stop()
	{
		state = STATE_FINAL;
	}

	abstract void shutDown(GLAutoDrawable drawable);
	

	abstract void draw(GLAutoDrawable drawable, int[] vp, int width, int height, int px, int py);



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

	
	/**
	 * 
	 * @param drawable
	 * @param texWidth
	 * @param texHeight
	 * @param count
	 * @param target
	 */
	protected void generateTexture(GLAutoDrawable drawable, int texWidth, int texHeight, int count, int[] target)
	{
		generateTexture(drawable, texWidth, texHeight, count, target, GL_RGBA);
	} //generateTexture
	
	/**
	 * 
	 * @param drawable
	 * @param texWidth
	 * @param texHeight
	 * @param count
	 * @param target
	 */
	protected void generateTexture(GLAutoDrawable drawable, int texWidth, int texHeight, int count, int[] target, int tex_format)
	{
		GL gl = drawable.getGL();
		gl.glGenTextures(count, target, 0);

		for (int i = 0; i < count; i++)
		{
			gl.glBindTexture(texTarget, target[i]);
			gl.glTexImage2D
			(
				texTarget, 0, texInternalFormat, 
				texWidth, texHeight, 0, tex_format, GL_FLOAT, null
			);
			gl.glTexParameterf(texTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
			gl.glTexParameterf(texTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);
			gl.glTexParameterf(texTarget, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			gl.glTexParameterf(texTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		} // for
	} //generateTexture
	
	protected void armKernel(GLAutoDrawable drawable, Kernel kernel)
	{
		// compile the kernels
		kernel.compile(drawable);
		
		// Link the shader into a complete GLSL program.
		kernel.linkProgram(drawable);
	} //armKernel


	protected void setRenderReady(boolean renderReady)
	{
		this.renderReady = renderReady;
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
	
	
	/**
	 * Here we bind the four attachments to the buffer which has the graphics card render to.
	 * 
	 * @param drawable
	 */
	protected void renderToTexture(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
		// bind the FBO to render to it
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[0]);
		// set render destination
		gl.glDrawBuffers(NUM_OF_ATTACHMENTS_PER_FBO, attachmentpoints, 0);
		
		// Then render as normal
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// Clear Screen and Depth Buffer
		// TODO check: can glClear() clear the depth buffer if none was attached to the FBO ?
//		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
		gl.glLoadIdentity();
		
	} //renderToTexture

	
	/**
	 * One FBO can usually bind four attachments. So we have to swap for every calculation step
	 * the actual textures. This is done by this method.
	 * 
	 * @param start first attachment point
	 * @param end
	 */
	protected void attachTextureToFBO(GLAutoDrawable drawable, int start, int end, int[] target)
	{		
		GL gl = drawable.getGL();
				
		for(int i = start; i < end; i++)
		{
			// attach a given attachment sets to the four attachmentpoints of the single given FBO
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
		
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT)
			System.out.println("INCOMPLETE_MISSING_ATTACHMENT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT) 
			System.out.println("INCOMPLETE_DUPLICATE_ATTACHMENT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT)
			System.out.println("INCOMPLETE_DIMENSIONS_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT)
			System.out.println("INCOMPLETE_FORMATS_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT)
			System.out.println("INCOMPLETE_DRAW_BUFFER_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT) 
			System.out.println("INCOMPLETE_READ_BUFFER_EXT! " + status);
		
		if (status == GL_FRAMEBUFFER_UNSUPPORTED_EXT)
			System.out.println("FRAMEBUFFER_UNSUPPORTED! " + status);
		
	} //checkFBO
	
	
	protected int swap(boolean imagePP)
	{
		if(imagePP)
			return activeImageTex == Image1Tex ? Image2Tex : Image1Tex;
		else
			return activeTraceData == A1Tex ? A2Tex : A1Tex;
	}

	protected void drawRenderedTile(GLAutoDrawable drawable, int tileX, int tileY)
	{
		GL gl = drawable.getGL();
		
		// draw the rendered tile to the ByteBuffer
		draw(drawable, vp, TILE_WIDTH, TILE_HEIGHT, tileX, tileY );
		// Refresh the shown image
		((SunshineSceneVisitor) monitor).storeImage(imageBB, outputMode);
		// rebind the actual FBO
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[0]);
	}

	// load a String out of the file given by the path s
	protected String loadSource(String s)
	{	
		String source 	= null;
		s 				= "kernel/shaderSource/" + s;
		
		try {
			source = new String(StreamUtil.readAll(this.getClass().getResourceAsStream(s)));
		} catch (Exception e)
		{
			System.out.println(e);			
		}		
		return source;
	} //loadShader	



	// store the given bytebuffer into the active texture
	protected void transferToTexture(GLAutoDrawable drawable,int width, int height, ByteBuffer data)
	{
		transferToTexture(drawable, width, height, data, GL.GL_RGBA);
	} //transferToTexture
	
	// store the given bytebuffer into the active texture
	protected void transferToTexture(GLAutoDrawable drawable,int width, int height, ByteBuffer data, int tex_format)
	{
		GL gl = drawable.getGL();
		data.rewind();
		gl.glTexSubImage2D(texTarget, 0, 0, 0, width, height, tex_format, GL.GL_FLOAT, data);
		data.rewind();
	} //transferToTexture
	
	protected void execute(Kernel kernel, int i)
	{
		execute(kernel, i, false);
	}
	
	protected void execute(Kernel kernel, int i, boolean finalOutput)
	{		
		
		int traceInput		= activeTraceData;
		int imageInput		= activeImageTex;
		
		// Switch between the possible active textures..
		if(!finalOutput)
			activeTraceData = swap(false);
		else
			activeImageTex 	= swap(true);
		
		
		active = finalOutput ? activeImageTex : activeTraceData;
		
		// attach the corresponding texture to the attachments..
		attachTextureToFBO(drawable, 0, 4, ioTextures[active]);
		
		// setting the new textures..
		kernel.setTextures(ioTextures[traceInput], ioTextures[imageInput]);

		// and execute the current kernel with these new textures.
		kernel.execute(drawable, currentTileX, currentTileY, i);
		
		/*System.out.println(kernel.name + " \t traceInputTex: \t" + traceInput);
		System.out.println(kernel.name + " \t imageInputTex: \t" + imageInput);
		System.out.println(kernel.name + " \t OutputTex: \t\t" + active);
		System.out.println("#####");*/
		
	}
		
	protected Number getNumericOption(String key, Number def)
	{
		return (opts != null) ? (Number) opts.get (key, def) : def;
	} //getNumericOption
	
	
	protected boolean getBooleanOption (String key, boolean def)
	{
		return (opts != null) ? ((Boolean) opts.get (key, Boolean.valueOf (def))).booleanValue () : def;
	} //getBooleanOption
	
} // Class

