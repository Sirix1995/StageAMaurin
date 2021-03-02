package de.grogra.ext.sunshine;

import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0_EXT;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT1_EXT;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT2_EXT;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT3_EXT;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_FRAMEBUFFER_EXT;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_SMOOTH;

import java.awt.EventQueue;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.TraceGL;


import de.grogra.ext.sunshine.kernel.GenPathRayKernel;
import de.grogra.ext.sunshine.kernel.InitKernel;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.TraceKernel;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;

/**
 * @author Thomas
 *
 */
public class SunshinePathtracerOld extends SunshineRaytracer implements GLEventListener
{
	// declaration of the used kernel
	private Kernel initKernel;
	private Kernel genRayKernel;
	private Kernel traceKernel;
	
	
	public SunshinePathtracerOld()
	{
		RAYPROCESSOR = "pathRT.frag";
	}
		
	public void initialize(Options opts, ProgressMonitor progress, 
			ObjectHandler oh, int width, int height, int sceneSize, 
			int[] objects, int[] triangles)
	{
		super.initialize(opts, progress, oh, width, height, sceneSize, objects, 
				triangles);
		
		pStates = 2;
		
		GLCapabilities glcaps = new GLCapabilities();
		glcaps.setDoubleBuffered(false);
		
		// check if pbuffer support is available
		if( GLDrawableFactory.getFactory().canCreateGLPbuffer() ) 
		{
			pBuffer = GLDrawableFactory.getFactory().createGLPbuffer(glcaps, 
					null, imageWidth, imageHeight, null);
			
			pBuffer.addGLEventListener(this);
		} else
		{
			System.out.println("The graphic card has no pbuffer support.");
		}
	} //initialize

	
	public void init(GLAutoDrawable drawable)
	{ 
		// TODO check for bug, if init is called more than once (according to docs)
		// maybe move this code to processNextKernel()
		
		// if tracing is enabled insert TraceGL into composable pipeline
		if(TRACE) 
		{
			drawable.setGL(new TraceGL(drawable.getGL(), System.err));
		}
		
		// if debugging is enabled insert DebugGL into composable pipeline
		if(DEBUG) 
		{
			drawable.setGL(new DebugGL(drawable.getGL()));
		}
		
		GL gl = drawable.getGL();
		
		ioTextures = new int[pStates][];
		
		attachmentpoints = new int[NUM_OF_ATTACHMENTS_PER_FBO];
		
		// the color attachments for the framebuffer objects
		attachmentpoints[0] = GL_COLOR_ATTACHMENT0_EXT;
		attachmentpoints[1] = GL_COLOR_ATTACHMENT1_EXT;
		attachmentpoints[2] = GL_COLOR_ATTACHMENT2_EXT;
		attachmentpoints[3] = GL_COLOR_ATTACHMENT3_EXT;


		gl.glShadeModel(GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // clear the framebuffer
		gl.glViewport(0, 0, imageWidth, imageHeight); // set the size of the the viewport

		// Setup the FBOs
		gl.glGenFramebuffersEXT(fbo.length, fbo, 0);

		// generate the texture attachments
		for (int i = 0; i < pStates; i++) 
		{
			ioTextures[i] = new int[NUM_OF_ATTACHMENTS_PER_FBO];
			generateTexture(drawable, tileWidth, tileHeight, NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[i]);
		}
		
		// generate the scene texture
		generateTexture(drawable, sceneSize, sceneSize, 1, sceneTexture);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, sceneSize, sceneSize, oh.getSceneTex());
		
		// generate the texture atlas
		generateTexture(drawable, textureSizeX, textureSizeY, 1, texTexture);
		
		// fill the texture atlas with images, get by the texture handler
		if( oh.hasImages() )
		{
			for (int i = 0; i < oh.getImageCount(); i++)
			{
				gl.glTexSubImage2D(texTarget, 0, (i%x)*512, (i/x)*512, 512, 512, 
						GL.GL_BGRA,	GL.GL_UNSIGNED_BYTE, oh.getPixels(i) );
				
			} //for
		}
		
		// generate the tree texture
		generateTexture(drawable, 100, 100, 1, treeTexture);
		
		
		// calculate the size of the odd tiles
		// => isn't that the number of tiles horizontally and vertically instead ?
		// TODO find better name
		partsWidth	= getParts(imageWidth, tileWidth);
		partsHeight = getParts(imageHeight, tileHeight);
//		System.out.println("partsWidth = " + partsWidth + "   partsHeight = " + partsHeight);
		
		// create the kernels
		createKernels(drawable);
	} // init
	
		
	public void display(GLAutoDrawable drawable)
	{
		processNextKernel();
	}
	
	
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
	} // reshape
	

	
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		
	} // displayChanged
	
	
		
	public void processNextKernel() 
	{
		// obtain GL instance from the drawable
		GL gl = drawable.getGL();		
		
		switch(state) 
		{
			case STATE_STARTUP: 
			{
				// store the window viewport dimensions
				gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
	
				// The first active texture set
				active = 0;
				
				// At first we have to activate the attachments.
				renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
				
				// The first attach of the texture to the attachments
				attachTextureToFBO(drawable, NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[active]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				// set initial rendering buffer	
				state = STATE_INIT;
				break;
			}
			case STATE_INIT: 
			{
				// set the viewport to the dimensions of the tile
				gl.glViewport(0, 0, tileWidth, tileHeight);
			
				execute(initKernel, NUM_OF_ATTACHMENTS_PER_FBO, 0, ioTextures);
				
				currentSample = 0;
				state = STATE_GENRAY;
				break;
			}
			case STATE_GENRAY:
			{
				// give feedback about rendering progress
				if (monitor != null && (System.currentTimeMillis() - lastTimeMillis) > 1000L)
				{
					lastTimeMillis = System.currentTimeMillis();
					float progress = 0;
					progress = (progress + currentSample) / superSample;
					progress = (progress + currentTileX) / partsWidth;
					progress = (progress + currentTileY) / partsHeight;
					monitor.setProgress(Resources.msg("renderer.rendering",
							progress), progress);
				}
				
				execute(genRayKernel, NUM_OF_ATTACHMENTS_PER_FBO, 
						currentSample, ioTextures);
				
				currentRecursionDepth = 0;
				state = STATE_TRACE_EYERAY;
				break;
			}
			case STATE_TRACE_EYERAY: 
			{
				execute(traceKernel, NUM_OF_ATTACHMENTS_PER_FBO, 
						currentSample, ioTextures);
				
				// do recursive ray tracing
				currentRecursionDepth++;
				
				if(currentRecursionDepth >= recursionDepth) 
				{
					// do supersampling
					currentSample++;
					
					if(currentSample < superSample)
					{
						state = STATE_GENRAY;
					} 
					else 
					{
						// draw the rendered tile to the ByteBuffer
						draw(drawable, vp, tileWidth, tileHeight, currentTileX, currentTileY );
						// Refresh the shown image
						((SunshineSceneVisitor) monitor).storeImage(imageBB, outputMode);
						// rebind the actual FBO
						gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[0]);
						
						// render next tile
						currentTileX++;
						if (currentTileX >= partsWidth) 
						{
							currentTileX = 0;
							currentTileY++;
						}
						if (currentTileY >= partsHeight) 
						{
							state = STATE_FINAL;
						}
						else 
						{
							state = STATE_INIT;
						}
					}
				}
				break;
			}
			case STATE_FINAL:
			{
				// destroy resources
				shutDown(drawable);
				setRenderReady(true);
	
				// mark render job as finished and exit loop
				state = STATE_DONE;
				synchronized (sunshineAction)
				{
					sunshineAction.notify();
				}
				return;
			}
			default:
				// error ?
				System.err.println("called state machine with unknown state");
		}
			
		// schedule sunshineAction for execution again
		EventQueue.invokeLater(sunshineAction);
	} //processNextKernel
	

	
	// tidy up
	void shutDown(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		for (int i = 0; i < pStates; i++) {
			gl.glDeleteTextures(NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[i], 0);
		}
		gl.glDeleteTextures(1, sceneTexture, 0);
		gl.glDeleteTextures(1, texTexture, 0);
		gl.glDeleteTextures(1, treeTexture, 0);
		
		gl.glDeleteFramebuffersEXT(fbo.length, fbo, 0);
	} //shutDown
	
	

	// create the kernels
	void createKernels(GLAutoDrawable drawable)
	{
		// create the kernels
		initKernel 		= new InitKernel("initKernel", drawable, tileWidth);
		genRayKernel 	= new GenPathRayKernel("genRayKernel", drawable, tileWidth);
		traceKernel 	= new TraceKernel("traceRayKernel", drawable,
				sceneTexture, texTexture, treeTexture, oh.hasImages(), tileWidth);
		
		
		initKernel.setSource(drawable, new String[]
		{
				loadSource(Kernel.EXTENSIONS),
				loadSource(Kernel.RANDOM),
				loadSource("init.frag") 
		});
		
		
		genRayKernel.setSource(drawable, new String[]
		{
				loadSource(Kernel.EXTENSIONS),
				"vec3 rayOrigin 	= vec3("+oh.getPosString()+");", // the camera parameter
				"vec3 up 			= vec3("+oh.getUpString()+");",
				"vec3 right 		= vec3("+oh.getRightString()+");",
				"vec3 dir 			= vec3("+oh.getDirString()+");",
				"int partsHeight 	= " + partsHeight +";",
				"int partsWidth 	= " + partsWidth +";",
				"int texWidth 		= " + imageWidth + ";",
				"int texHeight 		= " + imageHeight + ";",
				"int gridSize 		= " + grid + ";",
				"int width			= " + tileWidth + ";",
   				"int height			= " + tileHeight + ";",
				loadSource(Kernel.RANDOM),
				loadSource("genPathRaySource.frag")
		});
		
		
		//the source for the tracing kernel
		traceKernel.setSource(drawable, new String[] 
		{			
				loadSource(Kernel.EXTENSIONS),
				loadSource("samplers.frag"),
				loadSource(Kernel.RANDOM),
				getIntermediates(),
				loadSource(Kernel.STRUCTS),
				loadSource(Kernel.LIGHT_CALC),
				loadSource(Kernel.TEXTURE_LOOKUP),
				loadSource(Kernel.CALC_NORMALS),
				loadSource(Kernel.INTERSECT_UTILS),
				loadSource("intersections.frag"),
				((SunshineSceneVisitor)monitor).getPhong(),
				loadSource(Kernel.COMPUTE_BSDF),
				loadSource(Kernel.DIRECT_ILLUM),
				//standard ray tracing or path tracing
				loadSource(RAYPROCESSOR),
				loadSource("intersectLoop.frag"),
				loadSource(Kernel.TEST_SHADOW).replaceFirst("shadowFeeler, td", "shadowFeeler"),
				loadSource("sunny_main.frag") 
		});		
		
		//compile and link the shader
		armKernel(drawable, initKernel);
		armKernel(drawable, genRayKernel);
		armKernel(drawable, traceKernel);
	} //createKernels

	
	void draw(GLAutoDrawable drawable, int[] vp, int width, int height, int px, int py)
	{
		GL gl = drawable.getGL();
		
		// restore the stored viewport dimensions and set rendering back to default frame buffer
//		gl.glViewport(vp[0], vp[1], vp[2], vp[3]);
//		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
//		gl.glMatrixMode (GL_PROJECTION);
//		gl.glLoadIdentity();
		
		// Obtaining the actual size of the current tile.
		int w = Math.min(width, imageWidth - px*width);		// w is lower equal then TILE_WIDTH
		int h = Math.min(height, imageHeight - py*height);	// h is lower equal then TILE_HEIGHT
				
		// Choose the first texture where the pixel data are stored.
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(texTarget, ioTextures[active][0]);
		gl.glEnable(texTarget);
			
		// Resetting the temporary ByteBuffer
		tileBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, tileBB);
		
		int onePixel		= ObjectHandler.RGBA;
		int oneLineInBB 	= imageWidth;
		int widthOffset 	= px * width;
		int heightOffset 	= imageWidth * py * height;
				
				
		for(int y = 0; y < h; y++)
		{			
			imageBB.position((y * oneLineInBB + widthOffset + heightOffset) * onePixel);
				
			// Here we have to skip the positions in tempBB when the width 
			// of the drawing part is shorter then a usual tile.
			if(w < width)
				tileBB.position(y*width*16);	// Jump to the beginning at the next line
			float f = 0;	
			// Copy the tempBB values to the global ByteBuffer
			for(int x = 0; x < w; x++)
			{				
				imageBB.putFloat(tileBB.getFloat());
				imageBB.putFloat(tileBB.getFloat());
				imageBB.putFloat(tileBB.getFloat());
				imageBB.putFloat(1);
				
				f = tileBB.getFloat();				
//				System.out.println(f);
										
			}
		}
		
		gl.glDisable(texTarget);
	} //draw
	
	protected String loadSource(String s)
	{	
		s = "kernel/shaderCode/" + s;
		
		return SunshineRaytracer.loadSourceRaw(s);
	}
	
} // Class

