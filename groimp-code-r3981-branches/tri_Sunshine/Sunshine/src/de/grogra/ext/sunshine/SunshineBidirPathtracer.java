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
import java.util.HashMap;
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.TraceGL;
import com.sun.opengl.util.BufferUtil;
import de.grogra.ext.sunshine.kdTree.KdTree;
import de.grogra.ext.sunshine.kernel.CombineKernel;
import de.grogra.ext.sunshine.kernel.GenRayKernel;
import de.grogra.ext.sunshine.kernel.InitKernel;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.ShadingKernel;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;

/**
 * @author Thomas
 *
 */
public class SunshineBidirPathtracer extends SunshineRaytracer implements GLEventListener
{
	private static final String EYEPATH_DEPTH = "BidirectionalPathTracer/eyeDepth";
	private static final String LIGHTPATH_DEPTH = "BidirectionalPathTracer/lightDepth";
	
	private int eyeDepth;
	private int lightDepth;
	
	private int[] eyePathTexture;
	private int[] lightPathTexture;
	private int[] normalTexture;
	private int[] combineTextures;
	private int[][] outputTexture = new int[pStates][];
	
	private Kernel initKernel;
	private Kernel genEyeRayKernel;
	private Kernel traceEyeRayKernel;
	private Kernel genLightRayKernel;
	private Kernel traceLightRayKernel;
	
	private Kernel combineKernel;
	
	private int currentEyeVertices;
	private int currentLightVertices;
	
	private int actual = 0;
	
	
	final int STATE_TRACE = 15;
	
	public SunshineBidirPathtracer()
	{
		RAYPROCESSOR = "pathRT.frag";
	}
	
	
	public void initialize(Options opts, ProgressMonitor progress, 
			ObjectHandler oh, int width, int height, int sceneSize, int[] objects, int[] trianglesCount)
	{
		this.opts 		= opts;
		this.oh 		= oh;
		monitor			= progress;
		tree			= new KdTree(100, ((SunshineSceneVisitor)progress).getOctree());
		imageWidth 		= width;
		imageHeight		= height;
		
		
		tempBB 			= BufferUtil.newByteBuffer( TILE_WIDTH * TILE_HEIGHT * ObjectHandler.RGBA );		
		imageBB			= BufferUtil.newByteBuffer( width * height * ObjectHandler.RGBA );
		
		
		eyeDepth 		= getNumericOption(EYEPATH_DEPTH, 10).intValue();
		lightDepth 		= getNumericOption(LIGHTPATH_DEPTH, 10).intValue();
		accelerate		= getBooleanOption(ACCELERATOR, false);
		grid 			= getNumericOption(GRID_SIZE, 2).intValue();
		superSample 	= grid*grid;
		this.sceneSize	= sceneSize;
		
		
		
		currentEyeVertices	= 0;
		currentLightVertices = 0;
		
		combineTextures = new int[4];
		
		this.objects = objects;
		this.trianglesCount = trianglesCount;

		
		GLCapabilities glcaps = new GLCapabilities();
		glcaps.setDoubleBuffered(false);
		
		if( GLDrawableFactory.getFactory().canCreateGLPbuffer() ) 
		{
			pBuffer = GLDrawableFactory.getFactory().createGLPbuffer(glcaps, 
					null, imageWidth, imageHeight, null);
			
			pBuffer.addGLEventListener(this);
		} else
		{
			System.out.println("The graphic card has no pbuffer support.");
		}
	}
	

	public void init(GLAutoDrawable drawable)
	{
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

		// Setup the FBO
		gl.glGenFramebuffersEXT(fbo.length, fbo, 0);
		
		
		eyePathTexture 		= new int[eyeDepth];
		generateTexture(drawable, TILE_WIDTH, TILE_HEIGHT, eyeDepth, eyePathTexture);
		
		lightPathTexture 	= new int[lightDepth];
		generateTexture(drawable, TILE_WIDTH, TILE_HEIGHT, lightDepth, lightPathTexture);
		
		normalTexture 	= new int[eyeDepth];
		generateTexture(drawable, TILE_WIDTH, TILE_HEIGHT, lightDepth, normalTexture);
		
		// generate the texture attachments
		for(int i = 0; i < pStates; i++) 
		{
			ioTextures[i] = new int[NUM_OF_ATTACHMENTS_PER_FBO];
			generateTexture(drawable, TILE_WIDTH, TILE_HEIGHT, NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[i]);

			// generate the output texture for the combine kernel
			outputTexture[i] = new int[1];
			generateTexture(drawable, TILE_WIDTH, TILE_HEIGHT, 1, outputTexture[i]);
		} //for
		
		
		combineTextures[3] = outputTexture[0][0];
		
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
		generateTexture(drawable, 100, 100, 1, kdTexture);
		
		// fill the tree texture with the tree cells
		transferToTexture(drawable, tree.getSize(), tree.getSize(), tree.getTreeTex());
		
		
		// calculate the size of the odd tiles
		// => isn't that the number of tiles horizontally and vertically instead ?
		// TODO find better name
		partsWidth	= getParts(imageWidth, TILE_WIDTH);
		partsHeight = getParts(imageHeight, TILE_HEIGHT);
//		System.out.println("partsWidth = " + partsWidth + "   partsHeight = " + partsHeight);
		
		// create the kernels
		createKernels(drawable);
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
				renderToTexture(drawable);
				
				// The first attach of the texture to the attachments
				attachTextureToFBO(drawable, 0, 4, ioTextures[active]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				// set initial rendering buffer	
				state = STATE_INIT;
				break;
			} //case STATE_STARTUP
			
			case STATE_INIT:
			{
				// set the viewport to the dimensions of the tile
				gl.glViewport(0, 0, TILE_WIDTH, TILE_HEIGHT);
			
				super.execute(initKernel, 0);
				
				currentSample = 0;
				state = STATE_GENRAY;
				break;
			} //case STATE_INIT
			
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
				
				super.execute(genEyeRayKernel, currentSample);
				
				currentRecursionDepth = 0;
				ioTextures[active == 1 ? 0 : 1][0] = normalTexture[0];
				ioTextures[active == 1 ? 0 : 1][1] = eyePathTexture[0];
				
				state = STATE_TRACE;
				break;
			} //case STATE_GENRAY
			
			case STATE_TRACE:
			{
				super.execute(traceEyeRayKernel, currentSample);
				
				// origin dem traceRayKernel geben
//				ioTextures[active == 1 ? 0 : 1][1] = eyePathTexture[currentRecursionDepth];
				
				// do recursive ray tracing
				currentRecursionDepth++;
				
//				ioTextures[active == 1 ? 0 : 1][1] = eyePathTexture[currentRecursionDepth];
				
				if(currentRecursionDepth >= eyeDepth)
				{
					state = STATE_COMBINE;
				}
				break;
			} //case STATE_TRACE
			
			case STATE_COMBINE:
			{
				// set up the path textures for combining
				combineTextures[0] = eyePathTexture[currentEyeVertices];
				combineTextures[1] = lightPathTexture[currentLightVertices];
				combineTextures[2] = normalTexture[0];
				
				
				execute(combineKernel, currentSample);
				
//				currentEyeVertices++;
				
				// draw the rendered tile to the ByteBuffer
				draw(drawable, vp, TILE_WIDTH, TILE_HEIGHT, currentTileX, currentTileY );
				// Refresh the shown image
				((SunshineSceneVisitor) monitor).storeImage(imageBB);
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
				break;
			} //case STATE_COMBINE
			
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
			} //case STATE_FINAL
			default:
				// error ?
				System.err.println("called state machine with unknown state");
		} //switch
		
		// schedule sunshineAction for execution again
		EventQueue.invokeLater(sunshineAction);
	} //processNextKernel
	
	
	@Override
	void createKernels(GLAutoDrawable drawable)
	{
		initKernel 			= new InitKernel("initKernel", drawable);
		genEyeRayKernel 	= new GenRayKernel("genRayKernel", drawable);
		
		traceEyeRayKernel 	= new ShadingKernel("traceEyeRayKernel", drawable,
				sceneTexture, texTexture, kdTexture, oh.hasImages());
		
		combineKernel		= new CombineKernel("combineKernel", drawable,
				sceneTexture, texTexture, kdTexture, oh.hasImages());

		combineKernel.setDebug();
		traceEyeRayKernel.setDebug();
		
		initKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				loadSource("random.fs"),
				loadSource("init.frag") 
		});
		
		genEyeRayKernel.setSource(drawable, new String[]
   		{
   				loadSource("extension.frag"),
   				"vec3 rayOrigin 	= vec3("+oh.getCamPosString()+");", // the camera parameter
   				"vec3 up 			= vec3("+oh.getUpString()+");",
   				"vec3 right 		= vec3("+oh.getRightString()+");",
   				"vec3 dir 			= vec3("+oh.getDirString()+");",
   				"int partsHeight 	= " + partsHeight +";",
   				"int partsWidth 	= " + partsWidth +";",
   				"int texWidth 		= " + imageWidth + ";",
   				"int texHeight 		= " + imageHeight + ";",
   				"int gridSize 		= " + grid + ";",
   				loadSource("random.fs"),
   				loadSource("genRaySource.frag")
   		});
		
		
		//the source for the tracing kernel
		traceEyeRayKernel.setSource(drawable, new String[] 
		{			
				loadSource("extension.frag"),
				loadSource("samplers.frag"),
				loadSource("random.fs"),
				"\n const int sphereCount 	= " + objects[0] + ";\n",
				"const int boxCount 			= " + objects[1] + ";\n",
				"const int cylinderCount 		= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int paraCount 			= " + objects[4] + ";\n",
				"const int lightCount 		= " + objects[5] + ";\n",
				"const int size 				= " + sceneSize +";\n",
				"float superSample 		= " + superSample+".0;\n",
				"const int lightPart			= " + oh.getLightPart() +";\n",
				"bool accelerate			= " + accelerate +";\n",
				loadSource("structs.frag"),
				loadSource("getObjects.frag"),
				loadSource("normals.frag"),
				loadSource("intersections.frag"),
				((SunshineSceneVisitor)monitor).getPhong(),
				loadSource("lightCalculations.frag"),
				loadSource(RAYPROCESSOR),
				loadSource("intersectionLoop.frag"),
				loadSource("treeTraversal.frag"),
				loadSource("bidirect_main.frag")
		});
		
		combineKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				loadSource("samplers2.frag"),
				"\n const int lightPart			= " + oh.getLightPart() +";\n",
				"const int size 				= " + sceneSize +";\n",
				loadSource("structs.frag"),
				loadSource("getObjects.frag"),
				loadSource("combine_main.frag")
		});
		
		//compile and link the shader
		armKernel(drawable, initKernel);		
		armKernel(drawable, genEyeRayKernel);
		armKernel(drawable, traceEyeRayKernel);
		armKernel(drawable, combineKernel);
	}
	
	
	void draw(GLAutoDrawable drawable, int[] vp, int width, int height, int px, int py)
	{
		GL gl = drawable.getGL();
		
		// Obtaining the actual size of the current tile.
		int w = Math.min(width, imageWidth - px*width);		// w is lower equal then TILE_WIDTH
		int h = Math.min(height, imageHeight - py*height);	// h is lower equal then TILE_HEIGHT
		
		// Choose the first texture where the pixel data are stored.
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(texTarget, outputTexture[actual][0]);
		gl.glEnable(texTarget);
		
		// Resetting the temporary ByteBuffer
		tempBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, tempBB);
		
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
				tempBB.position(y*width*16);	// Jump to the beginning at the next line
			float f = 0;	
			// Copy the tempBB values to the global ByteBuffer
			for(int x = 0; x < w; x++)
			{				
				imageBB.putFloat(tempBB.getFloat());
				imageBB.putFloat(tempBB.getFloat());
				imageBB.putFloat(tempBB.getFloat());
				imageBB.putFloat(1);
				
				f = tempBB.getFloat();				
//				System.out.println(f);
										
			} //for x
		} //for y
		
		gl.glDisable(texTarget);
	}
	
	void shutDown(GLAutoDrawable drawable)
	{
		
	}
	

	@Override
	protected void execute(Kernel kernel, int i)
	{
		attachTextureToFBO(drawable, 0, 1, outputTexture[actual]);
		
		//kernel.execute(drawable, currentTileX, currentTileY, i, combineTextures);
	}
	
}
