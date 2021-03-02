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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.kdTree.KdTree;
import de.grogra.ext.sunshine.kernel.GenRayKernel;
import de.grogra.ext.sunshine.kernel.InitKernel;
import de.grogra.ext.sunshine.kernel.IntersectionKernel;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.NormalsKernel;
import de.grogra.ext.sunshine.kernel.ShadingKernel;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;

/**
 * @author Thomas
 *
 */
public class SunshineStandardRaytracer extends SunshineRaytracer implements GLEventListener
{
	// declaration of the used kernel
	protected Kernel initKernel;
	protected Kernel genRayKernel;
	protected Kernel intersectionKernel;
	protected Kernel shadeKernel;
	
	public SunshineStandardRaytracer()
	{
		RAYPROCESSOR = "standardRT.frag";
	}
		
	public void initialize(Options opts, ProgressMonitor progress, 
			ObjectHandler oh, int width, int height, int sceneSize, int[] objects, int[] trianglesCount)
	{
		this.opts 				= opts;
		this.oh 				= oh;
		monitor 				= progress;			
		//tree					= new KdTree(100, ((SunshineSceneVisitor)progress).getOctree());
		imageWidth				= width;
		imageHeight 			= height;
		
		grid 					= getNumericOption(GRID_SIZE, 2).intValue();
		superSample 			= grid*grid;
		recursionDepth			= getNumericOption(REC_DEEP, 1).intValue();
		this.sceneSize			= sceneSize;
		accelerate				= getBooleanOption(ACCELERATOR, false);
		
		// Some additional options for control the render process
		refreshInterval 		= (long) (getNumericOption(REFRESH_INTERVAL, 30).intValue() * 1000);
		TILE_WIDTH 				= getNumericOption(TILE_SIZE, 256).intValue();			
		TILE_HEIGHT 			= TILE_WIDTH;			
		intersectionLoopStep	= getNumericOption(LOOP_STEPS, 1000).intValue();		
		
		for(int i = 0; i < trianglesCount.length; i++)
			this.allTriangles += trianglesCount[i];

		for(int i = 0; i < objects.length; i++)
			countObjects += i != 4 ? objects[i] : this.allTriangles; 		
			
		lightCount		= objects[6];
		
		tempBB 			= BufferUtil.newByteBuffer( TILE_WIDTH * TILE_HEIGHT * ObjectHandler.RGBA );		
		imageBB			= BufferUtil.newByteBuffer( width * height * ObjectHandler.RGBA );
		
		int counter 	= oh.getImageCount();
		x = counter;
		y = 1;
		
		// calculates the size of the texture atlas
		if(counter > 8)
		{
			y = counter % 8 == 0 ? counter / 8 : counter / 8 + 1;
			x = (int)Math.ceil(  (float)counter / (float)y );	
		}
		
		textureSizeX = x*512;
		textureSizeY = y*512;
		
		this.objects = objects;
		this.trianglesCount = trianglesCount;
		
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
			generateTexture(drawable, TILE_WIDTH, TILE_HEIGHT, NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[i]);
		}
		
		// generate the scene texture
		generateTexture(drawable, sceneSize, sceneSize, 1, sceneTexture);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, sceneSize, sceneSize, oh.getSceneTex());
		
	//	checkSentData(drawable, oh.getSceneTex().limit(), true, GL.GL_RGB);
		
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
		generateTexture(drawable, 100, 100, 1, kdTexture);
		
		// fill the tree texture with the tree cells
	//	transferToTexture(drawable, tree.getSize(), tree.getSize(), tree.getTreeTex());
		
		
		// calculate the size of the odd tiles
		// => isn't that the number of tiles horizontally and vertically instead ?
		// TODO find better name
		partsWidth	= getParts(imageWidth, TILE_WIDTH);
		partsHeight = getParts(imageHeight, TILE_HEIGHT);
//		System.out.println("partsWidth = " + partsWidth + "   partsHeight = " + partsHeight);
		
		// create the kernels
		createKernels(drawable);
		
		// Setting texture that has to be used for ping pong
		//setActiveTextures(A1Tex, A2Tex);
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
		GL gl 			= drawable.getGL();	
		
		// setting params for actual tile which has to be draw
		int drawXTile 	= currentTileX;
		int drawYTile	= currentTileY;
		
		switch(state) 
		{
			case STATE_STARTUP: 
			{
				processStartup(gl);
				break;
			}
			case STATE_INIT: 		// Computation with: input A2Tex output A1Tex 
			{
				processInit(gl);
				break;
			}
			case STATE_GENRAY: 		// Computation with: input A1Tex output A2Tex 
			{
				processGenRay(gl);
				break;
			}
			case STATE_INTERSECT: 	// Computation with: input A2Tex output A1Tex 
			{						
				processIntersection(gl);				
				break;
			}
			case STATE_SHADOW_TEST:
			{						
				processShadowTest(gl);			
				break;
			}
			case STATE_SHADE: 
			{					
				processShade(gl);
				break;				
			}
			case STATE_FINAL: 
			{				
				processFinal(gl);
				return;
			}
			default:
				// error ?
				System.err.println("called state machine with unknown state");
		}
		
		
		if(System.currentTimeMillis() - lastRefreshTime > refreshInterval)
		{
			// draw the rendered tile to the ByteBuffer
			drawRenderedTile(drawable, drawXTile, drawYTile);						
			
			lastRefreshTime = System.currentTimeMillis();
		}
					
		// schedule sunshineAction for execution again
		EventQueue.invokeLater(sunshineAction);
	} //processNextKernel
	
	protected void processStartup(GL gl)
	{
		// store the window viewport dimensions
		gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);

		// First texture destination for output during intersection computation
		activeTraceData = A1Tex;
		
		// The first destination for the final image
		activeImageTex = Image1Tex;
		
		// The first active texture set
		active = activeTraceData;
		
		// At first we have to activate the attachments.
		renderToTexture(drawable);
		
		// The first attach of the texture to the attachments... output texture is A1Tex
		attachTextureToFBO(drawable, 0, 4, ioTextures[active]);
		
		// Check the FBO whether everything is fine.
		checkFBO(drawable);
		
		lastRefreshTime = System.currentTimeMillis();
		
		// set initial rendering buffer	
		state = STATE_INIT;
	}	
	
	protected void processInit(GL gl)
	{
		// set the viewport to the dimensions of the tile
		gl.glViewport(0, 0, TILE_WIDTH, TILE_HEIGHT);
		
		//setActiveTextures(A1Tex, A2Tex);
	
		// Clearing the texture for trace data
		execute(initKernel, 0);
		
		// Clearing the texture for final output..				
		execute(initKernel, 0, true);
		
		currentSample = 0;
		
		state = STATE_GENRAY;
	}
	
	protected void processGenRay(GL gl)
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
						
		execute(genRayKernel, currentSample, true);				
		
		currentRecursionDepth = 0;
		state = STATE_INTERSECT;						
		
		lightCounter		= 0;
		intersectLoopStart 	= 0;
		intersectLoopStop 	= Math.min(intersectionLoopStep, countObjects);		
	}
	
	protected void processIntersection(GL gl)
	{
		intersectionKernel.setParameter("loopStart", intersectLoopStart);
		intersectionKernel.setParameter("loopStop", Math.min(intersectLoopStop, countObjects + 1));
		intersectionKernel.setParameter("lastCycle", intersectLoopStop >= countObjects);
		intersectionKernel.setParameter("isShadowTest", false);
		intersectionKernel.setParameter("lightPass", 0);
					
		execute(intersectionKernel, currentSample);					

		if(intersectLoopStop < countObjects)
		{					
			intersectLoopStart = intersectLoopStop;
			intersectLoopStop += intersectionLoopStep;
			
		} else {
		
			state = STATE_SHADOW_TEST;		
			intersectLoopStart = 0;
			intersectLoopStop = Math.min(intersectionLoopStep, countObjects);	
		}				
	}
	
	protected void processShadowTest(GL gl)
	{
		intersectionKernel.setParameter("loopStart", 	intersectLoopStart);
		intersectionKernel.setParameter("loopStop", 	Math.min(intersectLoopStop, countObjects + 1));
		intersectionKernel.setParameter("lastCycle", 	intersectLoopStop >= countObjects);
		intersectionKernel.setParameter("isShadowTest",	true);
		intersectionKernel.setParameter("lightPass", 	lightCounter);									
		
		execute(intersectionKernel, currentSample);					
						
		if(intersectLoopStop < countObjects)
		{
			intersectLoopStart = intersectLoopStop;
			intersectLoopStop += intersectionLoopStep;
			
		} else 	{							
			
			state = STATE_SHADE;	
			
			intersectLoopStart = 0;
			intersectLoopStop = Math.min(intersectionLoopStep, countObjects);						
		}
	}
	
	protected void processShade(GL gl)
	{
		shadeKernel.setParameter("lastCycle", 	lightCounter + 1 >= lightCount);
		shadeKernel.setParameter("lightPass", 	lightCounter);	

		execute(shadeKernel, currentSample, true);				
		
		lightCounter++;								
					
		if(lightCounter < lightCount)	
		{
			state = STATE_SHADOW_TEST;
			
		} else {					
			
			currentRecursionDepth++;
			
			if(currentRecursionDepth >= recursionDepth)
			{							
				currentSample++;
				
				if(currentSample < superSample)
				{						
					state = STATE_GENRAY;						
					
				} else {
					lastRefreshTime = 0;
					
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
				
			} else {
				
				state			= STATE_INTERSECT;						
				lightCounter	= 0;
			}
		}				
		
		intersectLoopStart 	= 0;
		intersectLoopStop 	= Math.min(intersectionLoopStep, countObjects);	
	}
	
	protected void processFinal(GL gl)
	{
		// Refresh the image one last time
		lastRefreshTime = 0;
		
		// destroy resources
		shutDown(drawable);
		setRenderReady(true);				

		// mark render job as finished and exit loop
		state = STATE_DONE;
		synchronized (sunshineAction) 
		{
			sunshineAction.notify();
		}
	}
	
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
		gl.glDeleteTextures(1, kdTexture, 0);
		
		gl.glDeleteFramebuffersEXT(fbo.length, fbo, 0);
	} //shutDown
	
	

	// create the kernels
	void createKernels(GLAutoDrawable drawable)
	{
		// create the kernels
		initKernel 			= new InitKernel("initKernel", drawable);
		genRayKernel 		= new GenRayKernel("genRayKernel", drawable);
		intersectionKernel 	= new IntersectionKernel("intersectionKernel", drawable, sceneTexture, kdTexture);		
		shadeKernel 		= new ShadingKernel("shadeKernel", drawable,
								sceneTexture, texTexture, kdTexture, oh.hasImages());
		
		intersectionKernel.setDebug();	
		shadeKernel.setDebug();
				
		String tris = "int triCount["; // Default
		int triCount = 0;		
		
		if(trianglesCount.length>0)
		{
			tris = "int triCount[" + trianglesCount.length + "]; \n\r\n void setTriSet(void)\n{ \n";
			for(int i = 0; i < trianglesCount.length; i++)
			{
				tris += "\ttriCount[" + i + "] = " + trianglesCount[i] + "; \n";
				triCount += trianglesCount[i];
			}
		}
		else
			tris = "int triCount[1]; \n\r\n void setTriSet(void)\n{ \n";		
		
		tris += "}\n\n";
		
		tris = "const int allTris \t = " + triCount + ";\n" + tris;
		
		
		initKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				loadSource("random.fs"),
				loadSource("init.frag") 
		});
		
		
		genRayKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				"vec3 rayOrigin 	= vec3("+oh.getCamPosString()+");\n", // the camera parameter
				"vec3 up 			= vec3("+oh.getUpString()+");\n",
				"vec3 right 		= vec3("+oh.getRightString()+");\n",
				"vec3 dir 			= vec3("+oh.getDirString()+");\n",
				"int partsHeight 	= " + partsHeight +";\n",
				"int partsWidth 	= " + partsWidth +";\n",
				"int texWidth 		= " + imageWidth + ";\n",
				"int texHeight 		= " + imageHeight + ";\n",
				"int gridSize 		= " + grid + ";\n",
				"int width 			= " + SunshineRaytracer.TILE_WIDTH + ";\n",
				"int height			= " + SunshineRaytracer.TILE_HEIGHT + ";\n",
				loadSource("random.fs"),
				loadSource("normalStoring.frag"),
				loadSource("genRaySource.frag")
		});
		
		intersectionKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				loadSource("samplers.frag") + "\n",
				loadSource("random.fs") + "\n",
				"const int size 				= " + sceneSize +";\n",						
				"const int sphereCount 	= " + (objects[0]) + ";\n",
				"const int boxCount 			= " + objects[1] + ";\n",
				"const int cylinderCount 		= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int meshCount 			= " + objects[4] + ";\n",
				"const int paraCount 			= " + objects[5] + ";\n",
				"const int lightCount 		= " + objects[6] + ";\n",
				"const int countObjects		= " + countObjects + ";\n",
				tris,
				"const float meshPart			= " + oh.getMeshPart() +".0;\n",
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				"bool accelerate			= " + accelerate +";\n",
				"float superSample 		= " + superSample+".0;\n",
				loadSource("structs.frag"),
				loadSource("mathCalc.frag"),
				loadSource("getObjects.frag"),
				loadSource("normals.frag"),
				loadSource("intersections.frag"),
				loadSource("intersectionLoop.frag"),
				loadSource("intersectUtils.frag"),
				loadSource("initValues.frag"),
				loadSource("intersectionMain.frag")
		});		
		
		shadeKernel.setSource(drawable, new String[] 
		{			
				loadSource("extension.frag"),
				"#define SHADOW_FEELER\n",
				loadSource("samplers.frag") + "\n",
				"uniform sampler2DRect normalTex;\n",
				loadSource("random.fs") + "\n",
				"const int size 				= " + sceneSize +";\n",	
				"const int sphereCount 		= " + objects[0] + ";\n",
				"const int boxCount 			= " + objects[1] + ";\n",
				"const int cylinderCount 		= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int meshCount 			= " + objects[4] + ";\n",
				"const int paraCount 			= " + objects[5] + ";\n",
				"const int lightCount 		= " + objects[6] + ";\n",
				"const int countObjects		= " + countObjects + ";\n",
				"const float meshPart			= " + oh.getMeshPart() +".0;\n",
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				tris,
				"bool accelerate			= " + accelerate +";\n",
				"float superSample 		= " + superSample+".0;\n",
				loadSource("structs.frag"),
				loadSource("mathCalc.frag"),
				loadSource("getObjects.frag"),
				((SunshineSceneVisitor)monitor).getPhong(),
				loadSource("lightCalculations.frag"),
				//standard ray tracing or path tracing
				loadSource(RAYPROCESSOR),
				loadSource("intersectUtils.frag"),
				loadSource("shadeUtils.frag"),
				loadSource("initValues.frag"), 
				loadSource("shadeMain.frag") 
		});		
		
		//compile and link the shader
		armKernel(drawable, initKernel);
		armKernel(drawable, genRayKernel);
		armKernel(drawable, intersectionKernel);
		armKernel(drawable, shadeKernel);
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
		gl.glBindTexture(texTarget, ioTextures[ activeImageTex /*active*/][0]);
		gl.glEnable(texTarget);
			
		// Resetting the temporary ByteBuffer
		tempBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, tempBB);
		
		int onePixel		= ObjectHandler.RGBA;
		int oneLineInBB 	= imageWidth;
		int widthOffset 	= px * width;
		int heightOffset 	= imageWidth * py * height;
		float scale			= (superSample + 1f)/ ( currentSample + 1f);		
		scale				= 1.0f;		
		
		int debug = 0;
		
		if(debug == 0)
		{
			for(int y = 0; y < h; y++)
			{			
				imageBB.position((y * oneLineInBB + widthOffset + heightOffset) * onePixel);
				
				// Here we have to skip the positions in tempBB when the width 
				// of the drawing part is shorter then a usual tile.
				if(w < width)
					tempBB.position(y*width*16);	// Jump to the beginning at the next line
				float f = 0;	
				// Copy the tempBB values to the global ByteBuffer
				for(int x = 0; x < w; x++)
				{				
					imageBB.putFloat(tempBB.getFloat() * scale);
					imageBB.putFloat(tempBB.getFloat() * scale);
					imageBB.putFloat(tempBB.getFloat() * scale);
					imageBB.putFloat(1);
					
					f = tempBB.getFloat();				
//					System.out.println(f);					
				}
			}
		} else if (debug == 1){
			
			outputMode = Image.OUTPUT_RAW;
			
			if(currentTileX == 0 && currentTileY == 0)
			{		
				try
				{
					PrintWriter f = new PrintWriter(new BufferedWriter(
							new FileWriter("../Sunshine/output.txt")));
					
					for(int y = 0; y < h; y++)
					{	
						for(int x = 0; x < w; x++)
						{	
						/*	System.out.print(tempBB.getFloat() + ", ");
							System.out.print(tempBB.getFloat() + ", ");
							System.out.print(tempBB.getFloat() + ", ");
							System.out.print(tempBB.getFloat() + );	*/	
							
							if(x % 128 == 0 && !(x == 0 && y == 0))
								f.println();
							
							f.printf("%G, %G, %G\t\t", tempBB.getFloat(), tempBB.getFloat(), tempBB.getFloat() );
							
							tempBB.getFloat();
							
													
						}
					}	
					
					f.close();
					
				} catch (IOException e) {
					System.err.println("Could not create file");
				}
			}
				
		} else if (debug == 2){
			if(currentTileX == 0 && currentTileY == 0)
			{		
				try
				{
					PrintWriter f = new PrintWriter(new BufferedWriter(
							new FileWriter("../Sunshine/output.txt")));
					
					for(int y = 0; y < h; y++)
					{	
						for(int x = 0; x < w; x++)
						{	
						/*	System.out.print(tempBB.getFloat() + ", ");
							System.out.print(tempBB.getFloat() + ", ");
							System.out.print(tempBB.getFloat() + ", ");
							System.out.print(tempBB.getFloat() + );	*/	
							
							
							
							f.printf("%G, %G, %G, %G\t\t", tempBB.getFloat(), tempBB.getFloat(), tempBB.getFloat(), tempBB.getFloat() );
							f.println();
							
													
						}
					}	
					
					f.close();
					
				} catch (IOException e) {
					System.err.println("Could not create file");
				}
			}
		}
		gl.glDisable(texTarget);
	} //draw
	
	protected void checkSentData(GLAutoDrawable drawable, int sizeBB, boolean writeToDisk, int tex_format)
	{
		GL gl = drawable.getGL();
		
		ByteBuffer bb1 = BufferUtil.newByteBuffer(sizeBB);
		
		gl.glGetTexImage(texTarget, 0, tex_format, GL_FLOAT, bb1);
		int i = 0;
		String out = "";
		
		int elemPerPixel 	= tex_format == GL.GL_RGBA ? 4 : 3; 	// RGB
		
		try
		{
			PrintWriter f = new PrintWriter(new BufferedWriter(
					new FileWriter("../Sunshine/scene.txt")));
			
			while(bb1.position() < bb1.limit())
			{
				i++;			
				
				if(writeToDisk)					
				{					
					float temp = bb1.getFloat();
					f.printf("%G" + (i % elemPerPixel == 0 ? "\t\t\t" : ",\t"), (new Double(temp)).doubleValue());			
					if((i % 39 == 0))
						f.println();
				
				} else {					
					System.out.print(bb1.getFloat() + (++i % elemPerPixel == 0 ? "\t\t\t" : ",\t\t"));
				}
				
			}
			f.close();
		} catch (IOException e) {
			System.err.println("Could not create file");
		}		
	}
	
} // Class

