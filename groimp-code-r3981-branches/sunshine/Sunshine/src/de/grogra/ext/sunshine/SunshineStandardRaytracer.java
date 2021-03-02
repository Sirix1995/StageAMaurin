/**
 * 
 */
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
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import net.goui.util.MTRandom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.kernel.GenRayKernel;
import de.grogra.ext.sunshine.kernel.InitKernel;
import de.grogra.ext.sunshine.kernel.IntersectionKernel;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.ShadingKernel;
import de.grogra.ext.sunshine.kernel.TempMatKernel;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.imp3d.ray2.SceneVisitor;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.Octree;



/**
 * @author Stephan
 *
 */
public class SunshineStandardRaytracer extends SunshineRaytracer implements GLEventListener
{
	// declaration of the used kernel
	protected Kernel initKernel;
	protected Kernel genRayKernel;
	protected Kernel intersectionKernel;
	protected Kernel shadowKernel;
	protected Kernel shadeKernel;
	protected Kernel spawnKernel;	
	
	protected Kernel tempMatKernel;
	
	/**
	 * Determine the current buffer for the intersection computation
	 */
	protected int activeTraceData;
	
	/**
	 * Determine the current buffer for the final image output
	 */
	protected int activeImageTex;
	
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
	
	protected int[] tempMaterialTex = new int[NUM_OF_ATTACHMENTS_PER_FBO];
	
	
	// current state of state machine in processNextKernel()
	protected static final int STATE_INTERSECT 		= 6;
	protected static final int STATE_SHADOW_TEST	= 7;
	protected static final int STATE_SHADE			= 8;
	protected static final int STATE_SEC_SPAWN		= 9;	
	protected static final int STATE_GET_MATERIAL	= 10;
	
	/**
	 * Some graphics cards refused shader after compilation of 
	 * the complete shading kernel. To avoid this it might
	 * helps to separate the secondary ray spawning from shading.
	 */
	protected boolean sepSecRay 					= false;
	
	/**
	 * Some graphics cards refused shader after compilation of 
	 * the complete shading kernel. To avoid this it might
	 * helps to out-source the uv-mapping in extra kernel.
	 */
	protected boolean sepMaterialFetch 				= false;
	
	/**
	 * Uniform sampling one light strategy: We choose randomly one light source
	 * per sample.
	 */
	protected int choosenLightSource				= 0;
	
	MTRandom rand = new MTRandom(0);
	
	/**
	 * For debug output purpose.
	 */
	protected int DEBUG_MODE = 0;
	
	public SunshineStandardRaytracer()
	{
		RAYPROCESSOR = "standardRT.frag";
	}
	
	
	protected int objectCount = 0;
	
	public void initialize(Options opts, ProgressMonitor progress, 
			ObjectHandler oh, int width, int height, int sceneSize, 
			int[] objects, int[] trianglesCount)
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
		refreshInterval 		= (long) (getNumericOption(REFRESH_TIME, 30).intValue() * 1000);
		tileWidth 				= getNumericOption(TILE_SIZE, 256).intValue();			
		tileHeight 				= tileWidth;			
		loopSteps				= getNumericOption(LOOP_STEPS, 1000).intValue();	
		
		sepSecRay				= getBooleanOption(SEP_SEC_RAY, false) && recursionDepth > 1; 
		sepMaterialFetch		= getBooleanOption(SEP_UV_MAPPING, false) && oh.getImageCount() > 0; 
								
		pStates					= 4;
		
		
		for(int i = 0; i < trianglesCount.length; i++)
			this.allTriangles += trianglesCount[i];

		for(int i = 0; i < objects.length; i++) // Don't count the mesh itself
			objectCount += i != 4 ? objects[i] : this.allTriangles; 		
			
		lightCount		= objects[6];
		
		tileBB 			= BufferUtil.newByteBuffer( tileWidth * tileHeight * ObjectHandler.RGBA );		
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
		this.triangles = trianglesCount;
		
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
		
		ioTextures 			= new int[pStates][];
		attachmentpoints 	= new int[NUM_OF_ATTACHMENTS_PER_FBO];
		
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
		
		// It might happen that uv-mapping for texture can only enabled when
		// this will be done in a own kernel for that purpose.
		if(sepMaterialFetch)
			generateTexture(drawable, tileWidth, tileHeight, NUM_OF_ATTACHMENTS_PER_FBO, tempMaterialTex);
				
		// generate the scene texture
		generateTexture(drawable, sceneSize, sceneSize, 1, sceneTexture);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, sceneSize, sceneSize, oh.getSceneTex());
		
		checkSentData(drawable, oh.getSceneTex().limit(), true);
		
		// generate the texture atlas
		generateTexture(drawable, textureSizeX, textureSizeY, 1, texTexture);
		
		// fill the texture atlas with images, get by the texture handler
		if( oh.hasImages() )
		{
			for (int i = 0; i < oh.getImageCount(); i++)
			{
				gl.glTexSubImage2D(texTarget, 0, (i%x)*512, (i/x)*512, 512, 512, 
						GL.GL_BGRA,	GL.GL_UNSIGNED_BYTE, oh.getPixels(i) );
				
			//	checkSentData(drawable, 512*512*4*4, true, GL.GL_BGRA);
				
			} //for
		}
		
		// Transfer on CPU side generated seeds. 
		generateSeedTexture();
		
		// generate the tree texture
//		generateTexture(drawable, 100, 100, 1, kdTexture);
		
		// fill the tree texture with the tree cells
	//	transferToTexture(drawable, tree.getSize(), tree.getSize(), tree.getTreeTex());
		
		
		// calculate the size of the odd tiles
		// => isn't that the number of tiles horizontally and vertically instead ?
		// TODO find better name
		partsWidth	= getParts(imageWidth, tileWidth);
		partsHeight = getParts(imageHeight, tileHeight);
//		System.out.println("partsWidth = " + partsWidth + "   partsHeight = " + partsHeight);
		
		// Setting texture that has to be used for ping pong
		//setActiveTextures(A1Tex, A2Tex);
	} // init
	
	protected int chooseLightSource()
	{
		return (int) Math.min(lightCount * rand.nextFloat(), lightCount-1);
	}
	
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
	
	protected void processStartup()
	{
		// create the kernels
		createKernels(drawable);
		
		// store the window viewport dimensions
		GLU.getCurrentGL().glGetIntegerv(GL.GL_VIEWPORT, vp, 0);

		// First texture destination for output during intersection computation
		activeTraceData = A1Tex;
		
		// The first destination for the final image
		activeImageTex = Image1Tex;
		
		// The first active texture set
		active = activeTraceData;
		
		// At first we have to activate the attachments.
		renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
		
		// The first attach of the texture to the attachments... output texture is A1Tex
		attachTextureToFBO(drawable, NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[active]);
		
		// Check the FBO whether everything is fine.
		checkFBO(drawable);
		
		lastRefreshTime = System.currentTimeMillis();
		
		// set initial rendering buffer	
		state = STATE_INIT;
	}
	
	protected void processInit()
	{
		// set the viewport to the dimensions of the tile
		GLU.getCurrentGL().glViewport(0, 0, tileWidth, tileHeight);
		
		((InitKernel) initKernel).setSeedTex(seedTex);
	
		// Clearing the texture for trace data
		execute(initKernel, 0);
		
		// Clearing the texture for final output..				
		execute(initKernel, 0, true);
		
		currentSample = 0;
		
		state = STATE_GENRAY;
	}
	
	protected void processGenRay()
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
		
		currentRecursionDepth 	= 0;
		state 					= STATE_INTERSECT;					
		
		loopStart 				= 0;
		loopStop 				= Math.min(loopSteps, objectCount);	
	}
	
	protected void processIntersect()
	{
		intersectionKernel.setUniform("loopStart", loopStart);
		intersectionKernel.setUniform("loopStop", Math.min(loopStop, objectCount + 1));
		intersectionKernel.setUniform("lastCycle", loopStop >= objectCount);
		intersectionKernel.setUniform("sample", currentSample);
		intersectionKernel.setUniform("lightPass", 	choosenLightSource);	

		execute(intersectionKernel, currentSample);					

		if(loopStop < objectCount)
		{					
			loopStart = loopStop;
			loopStop += loopSteps;
			
		} else {
		
			state = STATE_SHADOW_TEST;		
			loopStart = 0;
			loopStop = Math.min(loopSteps, objectCount);	
		}				
	}
		
	protected void processShadowTest()
	{
		shadowKernel.setUniform("loopStart", 	loopStart);
		shadowKernel.setUniform("loopStop", 	Math.min(loopStop, objectCount + 1));
		shadowKernel.setUniform("lastCycle", 	loopStop >= objectCount);
		shadowKernel.setUniform("lightPass", 	choosenLightSource);			
		
		execute(shadowKernel, currentSample);					
						
		if(loopStop < objectCount)
		{
			loopStart = loopStop;
			loopStop += loopSteps;
			
		} else 	{							
			
			state = STATE_SHADE;
			
			// When uv-mapping has to be computed in an extra step,
			// ignore at first shading kernel and compute UV-coors
			// for image-textures before shading.
			if(sepMaterialFetch)
				state = STATE_GET_MATERIAL;
			
			loopStart = 0;
			loopStop = Math.min(loopSteps, objectCount);						
		}
	}
	
	protected void processTempMatFetchCalc()
	{
		// setting the new textures..
		tempMatKernel.setInputTextures(ioTextures[activeTraceData], ioTextures[activeImageTex]);
		
		attachTextureToFBO(drawable, NUM_OF_ATTACHMENTS_PER_FBO, tempMaterialTex);
		
		tempMatKernel.execute(drawable, currentTileX, currentTileY, 0);
		
		state = STATE_SHADE;			
	}
	
	protected void processShade()
	{
		shadeKernel.setUniform("recursionDepth", currentRecursionDepth);
		shadeKernel.setUniform("lightPass", choosenLightSource);	

		execute(shadeKernel, currentSample, true);		
		
		if(sepSecRay)
			state = STATE_SEC_SPAWN;
		else
			finalizeLightPass();
	}
	
	protected void processSecSpawn()
	{
		spawnKernel.setUniform("lightPass", choosenLightSource);	

		execute(spawnKernel, currentSample, true);	
		
		finalizeLightPass();
	}
	
	/**
	 * This method is invoked when all necessary calculation relating to a certain
	 * light source is done. What happens when this light pass is complete depends on
	 * the number of light sources in the scene and the actual progress of Sunshine.
	 * If the intersection test for a pixel in a tile was successful each of these 
	 * pixels holds an intersection point. Every intersection point has to consider
	 * every light source for shading and secondary ray spawning. The cases are:
	 * 
	 * Case 1: 	There are still some light sources in the scene which were not yet
	 * 			considered. So we have to check, if the intersection is shadowed to
	 * 			the light source or not. Go to {@link #STATE_SHADOW_TEST}.
	 * Case 2:	All light sources for the intersection point were considered. Now 
	 * 			compute the next recursion depth. Go to the {@value #STATE_INTERSECT}
	 * Case 3:	Maximum recursion depth is reached. Compute the next sampling step.
	 * 			Go to {@link #STATE_GENRAY}.
	 * Case 4:	All super sampling for the current tile is done. So shift the tile to
	 * 			the next position of the image plane. The go to {@link #STATE_INIT} and
	 * 			repeat all steps of the pipeline.
	 * Case 5:	All tiles are finished. So there is nothing to do anymore. Go to 
	 * 			{@link #STATE_FINAL}.
	 */
	protected void finalizeLightPass()
	{
		choosenLightSource = chooseLightSource();
		
		// Proceed next recursion depth
		currentRecursionDepth++;

		if(currentRecursionDepth >= recursionDepth)
		{				
			// Case 3
			currentSample++;
			
			if(currentSample < superSample)
			{				
				// Case 3
				state = STATE_GENRAY;						
				
			} else {
				
				// Case 4
				lastRefreshTime = 0;
				
				// render next tile
				currentTileX++;
				if (currentTileX >= partsWidth) 
				{
					// Case 4
					currentTileX = 0;
					currentTileY++;
				}
				if (currentTileY >= partsHeight) 
				{
					// Case 5
					state = STATE_FINAL;
				} 
				else 
				{
					// Case 4
					state = STATE_INIT;
				}							
			}
		} else {
			state = STATE_INTERSECT;
		}
		
		// Ensure that the interruption for the intersection loop is reset.
		loopStart 	= 0;
		loopStop 	= Math.min(loopSteps, objectCount);
	}
	
	
	
	protected void processFinal()
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
	
	public void processNextKernel() 
	{
		// setting params for actual tile which has to be draw
		int drawXTile = currentTileX;
		int drawYTile = currentTileY;
		
		cancelPolling();
		
		switch(state) 
		{
			case STATE_STARTUP: 
			{
				processStartup();
				break;
			}
			case STATE_INIT: 		// Computation with: input A2Tex output A1Tex 
			{
				processInit();
				break;
			}
			case STATE_GENRAY: 		// Computation with: input A1Tex output A2Tex 
			{
				processGenRay();				
				break;
			}
			case STATE_INTERSECT: 	// Computation with: input A2Tex output A1Tex 
			{									
				processIntersect();								
				break;
			}
			case STATE_SHADOW_TEST:
			{	
				processShadowTest();				
				break;
			} 
			case STATE_GET_MATERIAL:
			{
				processTempMatFetchCalc();
				break;
			}
			case STATE_SHADE: 
			{	
				processShade();	
				break;				
			}
			case STATE_SEC_SPAWN: 
			{	
				processSecSpawn();	
				break;				
			}
			case STATE_FINAL: 
			{				
				processFinal();
				return;
			}
			default:
				// error ?
				System.err.println("called state machine with unknown state");
		}
		
		// Count kernel calls and may invoke glFinish() to synch the GPU
		pollingSynchronisation();		
		
		if(System.currentTimeMillis() - lastRefreshTime > refreshInterval)
		{
			// draw the rendered tile to the ByteBuffer
			drawRenderedTile(drawable, drawXTile, drawYTile);						
			
			lastRefreshTime = System.currentTimeMillis();
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
		gl.glDeleteTextures(1, seedTex, 0);
		
		if(sepMaterialFetch)
			gl.glDeleteTextures(NUM_OF_ATTACHMENTS_PER_FBO, tempMaterialTex, 0);
			
		gl.glDeleteFramebuffersEXT(fbo.length, fbo, 0);
	} //shutDown
	
	void createUVKernel(GLAutoDrawable drawable)
	{
		tempMatKernel			= new TempMatKernel("UVKernel", drawable, sceneTexture, tileWidth);
		
		tempMatKernel.setDebug();
		
		tempMatKernel.setSource(drawable, new String[]{
				
				loadSource("extension.frag"),
				loadSource("structs.frag"),
				loadSource("samplers.frag") + "\n",
				"const int size 				= " + sceneSize +";\n",	
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				loadSource("mathCalc.frag"),
				loadSource("getObjects.frag"),
				loadSource("uvMapping.frag"),
				loadSource("computeMaterialFetchMain.frag"),
		});
		
		armKernel(drawable, tempMatKernel);
	}

	// create the kernels
	void createKernels(GLAutoDrawable drawable)
	{
		// create the kernels
		initKernel 			= new InitKernel("initKernel", drawable, tileWidth);
		genRayKernel 		= new GenRayKernel("genRayKernel", drawable, tileWidth);
		intersectionKernel 	= new IntersectionKernel("intersectionKernel", 
									drawable, sceneTexture, treeTexture, tileWidth);		
		shadowKernel		= new IntersectionKernel("shadowKernel", 
									drawable, sceneTexture, treeTexture, tileWidth);		
		shadeKernel 		= new ShadingKernel("shadeKernel", drawable, 
									sceneTexture, texTexture, treeTexture, oh.hasImages(), tileWidth, tempMaterialTex);
		spawnKernel 		= new ShadingKernel("spawnKernel", drawable, 
									sceneTexture, texTexture, treeTexture, oh.hasImages(), tileWidth, tempMaterialTex);
		
		initKernel.setDebug();	
		genRayKernel.setDebug();	
		intersectionKernel.setDebug();	
		shadowKernel.setDebug();
		shadeKernel.setDebug();
		spawnKernel.setDebug();
		
		String tris = "int triCount["; // Default
		int triCount = 0;		
		
		if(triangles.length>0)
		{
			tris = "int triCount[" + triangles.length + "]; \n\r\n void setTriSet(void)\n{ \n";
			for(int i = 0; i < triangles.length; i++)
			{
				tris += "\ttriCount[" + i + "] = " + triangles[i] + "; \n";
				triCount += triangles[i];
			}
		}
		else
			tris = "int triCount[1]; \n\r\n void setTriSet(void)\n{ \n";		
		
		tris += "}\n\n";
		
		tris = "const int allTris \t = " + triCount + ";\n" + tris;
		
		
		initKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				"uniform sampler2DRect RND_TEX; \n",
				loadSource("random.frag"),
				loadSource("init.frag") 
		});
		
		genRayKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				"vec3 rayOrigin 	= vec3("+oh.getPosString()+");\n", // the camera parameter
				"vec3 up 			= vec3("+oh.getUpString()+");\n",
				"vec3 right 		= vec3("+oh.getRightString()+");\n",
				"vec3 dir 			= vec3("+oh.getDirString()+");\n",
				"int partsHeight 	= " + partsHeight +";\n",
				"int partsWidth 	= " + partsWidth +";\n",
				"int texWidth 		= " + imageWidth + ";\n",
				"int texHeight 		= " + imageHeight + ";\n",
				"int gridSize 		= " + grid + ";\n",
				"int width 			= " + tileWidth + ";\n",
				"int height			= " + tileHeight + ";\n",
				loadSource("random.frag"),
				loadSource("genRaySource.frag")
		});
		
		String[] intersectionKernelSource = {
				loadSource("extension.frag"),
				loadSource("samplers.frag") + "\n",
				loadSource("random.frag") + "\n",
				"const int size 				= " + sceneSize +";\n",						
				"const int sphereCount 	= " + (objects[0]) + ";\n",
				"const int boxCount 			= " + objects[1] + ";\n",
				"const int cylinderCount 		= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int meshCount 			= " + objects[4] + ";\n",
				"const int paraCount 			= " + objects[5] + ";\n",
				"const int lightCount 		= " + objects[6] + ";\n",
				"const int countObjects		= " + objectCount + ";\n",
				"const int boundingSphere	= " + oh.getBSphereID() + ";\n",
				tris,
				"const float meshPart			= " + oh.getMeshPart() +".0;\n",
				"const float paraPart 			= " + oh.getParaPart() + ".0;\n",
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				"bool accelerate			= " + accelerate +";\n",
				"float superSample 		= " + superSample+".0;\n",
				oh.hasNormals() ? "#define HAS_NORMALS\n" : "",
				loadSource("structs.frag"),
				loadSource("mathCalc.frag"),
				loadSource("getObjects.frag"),
				loadSource("normals.frag"),
				loadSource("intersections.frag"),
				loadSource("intersectionLoop.frag"),
				loadSource("intersectUtils.frag"),
				loadSource("initValues.frag"),
				loadSource("intersectionTestMain.frag")
		};
		
		String[] shadowKernelSource = intersectionKernelSource.clone();
		
		shadowKernelSource[shadowKernelSource.length - 1] = loadSource("shadowTestMain.frag");
		
		intersectionKernel.setSource(drawable, intersectionKernelSource);	
		shadowKernel.setSource(drawable, shadowKernelSource);	
		
		String[] shaderKernelSource = {
				loadSource("extension.frag"),
				loadSource("samplers.frag") + "\n",
				sepMaterialFetch ? "uniform sampler2DRect tempMatTex;\n" : "",
				loadSource("random.frag") + "\n",
				"const int size 				= " + sceneSize +";\n",	
				"const int sphereCount 		= " + objects[0] + ";\n",
				"const int boxCount 			= " + objects[1] + ";\n",
				"const int cylinderCount 		= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int meshCount 			= " + objects[4] + ";\n",
				"const int paraCount 			= " + objects[5] + ";\n",
				"const int lightCount 		= " + objects[6] + ";\n",
				"const int countObjects		= " + objectCount + ";\n",
				"const float meshPart			= " + oh.getMeshPart() +".0;\n",
				"const float paraPart 			= " + oh.getParaPart() + ".0;\n",
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				"const int boundingSphere	= " + oh.getBSphereID() + ";\n",
				tris,
				"bool accelerate			= " + accelerate +";\n",
				"float superSample 		= " + superSample+".0;\n",
				(((SunshineSceneVisitor) monitor).hasSunSky() ? "#define USE_SUNSKY 1 \n" : " \n"), //34
				((SunshineSceneVisitor) monitor).getSunSkyParas() + "\n",
				loadSource("sunsky.frag"),
				loadSource("structs.frag"),
				loadSource("mathCalc.frag"),
				loadSource("lightCalculations.frag"),
				loadSource("getObjects.frag"),
				!sepMaterialFetch ? loadSource("uvMapping.frag") : loadSource("fetchUVMap.frag"),
				((SunshineSceneVisitor)monitor).getPhong(),
				loadSource("directIllum.frag"),
				//standard ray tracing or path tracing
				loadSource(RAYPROCESSOR),
				loadSource("intersectUtils.frag"),
				loadSource("shadeUtils.frag"),
				loadSource("initValues.frag"),
				"//PLACEHOLDER: Modular shading and/or spawning"
		};
		
		String SHADE_MODE 				= "#define SHADE_MODE 1\n";
		String SPAWN_MODE 				= "#define SPAWN_MODE 1\n\n";
		
		if(!sepSecRay)
		{
			String ALL_MODE				= SHADE_MODE + SPAWN_MODE;
			
			if(recursionDepth > 1)
				shaderKernelSource[shaderKernelSource.length - 1] 	= ALL_MODE + loadSource("shadeSpawnMain.frag");
			else
				shaderKernelSource[shaderKernelSource.length - 1] 	= SHADE_MODE + loadSource("shadeSpawnMain.frag");
			
			shadeKernel.setSource(drawable, shaderKernelSource);
			
		} else {
			
			String[] spawnKernelSource 	= shaderKernelSource.clone();
			
			shaderKernelSource[shaderKernelSource.length - 1] 	= SHADE_MODE + loadSource("shadeSpawnMain.frag");
			spawnKernelSource[spawnKernelSource.length - 1] 	= SPAWN_MODE + loadSource("shadeSpawnMain.frag");
			
			shadeKernel.setSource(drawable, shaderKernelSource);
			spawnKernel.setSource(drawable, spawnKernelSource);
		}
		
		//compile and link the shader
		armKernel(drawable, initKernel);
		armKernel(drawable, genRayKernel);
		armKernel(drawable, intersectionKernel);
		armKernel(drawable, shadowKernel);
		armKernel(drawable, shadeKernel);
		
		if(sepSecRay)
			armKernel(drawable, spawnKernel);
		
		if(sepMaterialFetch)
			createUVKernel(drawable);
		
	} //createKernels
	
	
	protected void drawRenderedTile(GLAutoDrawable drawable, int tileX, int tileY)
	{
		GL gl = drawable.getGL();
		
		// draw the rendered tile to the ByteBuffer
		draw(drawable, vp, tileWidth, tileHeight, tileX, tileY );
		// Refresh the shown image
		((SunshineSceneVisitor) monitor).storeImage(imageBB, outputMode);
		// rebind the actual FBO
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[0]);
	}
	
	
	protected String loadSource(String s)
	{	
		s = "kernel/shaderSource/" + s;
		
		return SunshineRaytracer.loadSourceRaw(s);
	}

	
	public void draw(GLAutoDrawable drawable, int[] vp, int width, int height, int px, int py)
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
		gl.glBindTexture(texTarget, ioTextures[activeImageTex /*active*/][0]);
		gl.glEnable(texTarget);
			
		// Resetting the temporary ByteBuffer
		tileBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, tileBB);
		
		int onePixel		= ObjectHandler.RGBA;
		int oneLineInBB 	= imageWidth;
		int widthOffset 	= px * width;
		int heightOffset 	= imageWidth * py * height;
				
		if(DEBUG_MODE == 0)
		{
			for(int y = 0; y < h; y++)
			{		
				imageBB.position((y * oneLineInBB + widthOffset + heightOffset) * onePixel);
				
				// Here we have to skip the positions in tempBB when the width 
				// of the drawing part is shorter then a usual tile.
				if(w < width)
					tileBB.position(y*width*16);	// Jump to the beginning at the next line
				// Copy the tempBB values to the global ByteBuffer
				for(int x = 0; x < w; x++)
				{						
					imageBB.putFloat(tileBB.getFloat());
					imageBB.putFloat(tileBB.getFloat());
					imageBB.putFloat(tileBB.getFloat());
					imageBB.putFloat(1);
					
					tileBB.getFloat();
				}
			}
		} 
		else if(DEBUG_MODE == 1)
		{
			outputMode = Image.OUTPUT_RAW;
			
			if(px == 0 && py == 0)
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
							
							f.printf("%f, %f, %f, %f\t\t", tileBB.getFloat(), tileBB.getFloat(), tileBB.getFloat(), tileBB.getFloat());
	
							if((x + 1) % 128 == 0)
								f.println();						
						}
					}	
					
					f.close();
					
				} catch (IOException e) {
					System.err.println("Could not create file");
				}
			}
				
		} else if (DEBUG_MODE == 2){
			if(currentTileX == 0 && currentTileY == 0)
			{
				float result0, result1, result2;
				
				float result0Old, result1Old, result2Old;
				
				result0Old	= 0f;
				result1Old	= 0f;
				result2Old	= 0f;
				
				for(int x = 0; x < w * h; x++)
				{
					result0 = tileBB.getFloat();
					result1 = tileBB.getFloat();
					result2 = tileBB.getFloat();
					
					tileBB.getFloat();
					
					if(!(result0 == result1 || result1 == result2 || result0 == 0.0) && (result0Old != result0 || result1Old != result1 || result2Old != result2))
					{
						System.out.print(result0 + ", " + result1 + ", " + result2 + "\t" + (x % 6 == 0 ? "\n" : ""));	
						
						result0Old	= result0;
						result1Old	= result1;
						result2Old	= result2;
					}
				}
			}
		}
		gl.glDisable(texTarget);
	} //draw
	
	protected void execute(Kernel kernel, int i)
	{
		execute(kernel, i, false);
	}
	
	protected void execute(Kernel kernel, int i, boolean isImageOutPutBuffer)
	{		
		
		int traceInput		= activeTraceData;
		int imageInput		= activeImageTex;
		
		// Switch between the possible active textures..
		if(!isImageOutPutBuffer)
			activeTraceData = swap(false);
		else
			activeImageTex 	= swap(true);
		
		
		active = isImageOutPutBuffer ? activeImageTex : activeTraceData;
		
		// attach the corresponding texture to the attachments..
		attachTextureToFBO(drawable, NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[active]);
		
		// setting the new textures..
		kernel.setInputTextures(ioTextures[traceInput], ioTextures[imageInput]);

		// and execute the current kernel with these new textures.
		kernel.execute(drawable, currentTileX, currentTileY, i);
		
	/*	System.out.println(kernel.name + " \t traceInputTex: \t" + traceInput);
		System.out.println(kernel.name + " \t imageInputTex: \t" + imageInput);
		System.out.println(kernel.name + " \t OutputTex: \t\t" + active);
		System.out.println("#####");*/
		
	}
	
	private int swap(boolean imagePP)
	{
		if(imagePP)
			return activeImageTex == Image1Tex ? Image2Tex : Image1Tex;
		else
			return activeTraceData == A1Tex ? A2Tex : A1Tex;
	}
	
	protected void checkSentData(GLAutoDrawable drawable, int sizeBB, boolean writeToDisk)
	{
		checkSentData(drawable, sizeBB, writeToDisk, GL_RGBA);
	}
	
	protected void checkSentData(GLAutoDrawable drawable, int sizeBB, boolean writeToDisk, int texFormat)
	{
		GL gl = drawable.getGL();
		
		ByteBuffer bb1 = BufferUtil.newByteBuffer(sizeBB);
		
		gl.glGetTexImage(texTarget, 0, texFormat, GL_FLOAT, bb1);
		int i = 0;
		String out = "";
		try
		{
			PrintWriter f = new PrintWriter(new BufferedWriter(
					new FileWriter("../Sunshine/scene.txt")));
			
			while(bb1.position() < bb1.limit())
			{
				out = bb1.getFloat() + " ";
				
				if((i + 1) % 4 == 0)
					if((i + 1) % 512 == 0)
						out +=  "\n";
					else
						out +=  "\t";
				
				i++;
				
				if(writeToDisk)					
				{
					
					f.print(out);					
				
				} else {					
					System.out.print(out);
				}
				
			}
			f.close();
		} catch (IOException e) {
			System.err.println("Could not create file");
		}		
	}
	
	
}
