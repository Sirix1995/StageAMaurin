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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.TraceGL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import com.sun.opengl.util.BufferUtil;
import de.grogra.ext.sunshine.acceleration.SunshineOctree;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.acceleration.OctreeInitKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.CombineInitKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.CombineKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.ComplementKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.GenEyeRayKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.GenLightRayKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.InterruptableIsVisibleKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.InterruptableKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.IsVisibleInitKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.LightInitKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.TraceEyeRayKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.TraceInitKernel;
import de.grogra.ext.sunshine.kernel.bidirPT.TraceLightRayKernel;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.ray2.tracing.modular.CausticMap;
import de.grogra.ray2.tracing.modular.CausticMap.CausticElement;

/**
 * @author mankmil
 *
 */
public class SunshineBidirPathtracer extends SunshineRaytracer
{
	private static final String EYEPATH_DEPTH 	= "BidirectionalPathTracer/eyeDepth";
	private static final String LIGHTPATH_DEPTH = "BidirectionalPathTracer/lightDepth";
	private static final String HEURISTIC_EXP	= "BidirectionalPathTracer/heuristicExp";
	
	private int eyeDepth;
	private int lightDepth;
	private int heuristicExp;
	
	
	private int[] eyePathTexture;
	private int[] lightPathTexture;
	private int[] radianceTexture;
	private int[] irradianceTexture;
	private int[] eVertexInfoTexture;
	private int[] lVertexInfoTexture;
	private int[] complementTextures;
	
	private int[][] outputTexture;
	
	private Kernel combineInit;
	private Kernel traceInit;
	private Kernel genEyeRayKernel;
	private Kernel lightInit;
	private Kernel genLightRayKernel;
	private Kernel isVisibleInit;
	private Kernel combineKernel;
	private Kernel traceComplementKernel;
	private Kernel octreeInit;
	private Kernel octreeKernel;
	private InterruptableKernel traceEyeRayKernel;
	private InterruptableKernel traceLightRayKernel;
	private InterruptableKernel isVisibleKernel;
	
	private int[][] combinePath;
	private int indexS;
	private int indexT;
	private static final int BEGIN2END = 0;
	private static final int END2BEGIN = 1;
	private static int DIRECTION = BEGIN2END;
	
	private int currentEyeVertices;
	private int currentLightVertices;
	
	private Indicator current = new Indicator(0);
	private boolean resume = false;
	
	// current state of state machine in processNextKernel()
	static final int STATE_COMBINE 				= 7;
	static final int STATE_GEN_EYERAY 			= 8;
	static final int STATE_GEN_LIGHTRAY 		= 9;
	static final int STATE_TRACE_LIGHTRAY		= 10;
	static final int STATE_DRAW					= 11;
	static final int STATE_INIT_LIGHT			= 12;
	static final int STATE_INIT_IS_VISIBLE_TEST	= 13;
	static final int STATE_IS_VISIBLE_TEST		= 14;
	static final int STATE_CALC_WEIGHT			= 15;
	static final int STATE_INIT_COMPLEMENT		= 16;
	static final int STATE_TRACE_RECURSIVLY		= 17;
	
	static final int STATE_INIT_OCTREE			= 18;
	static final int STATE_TRAVERSE_OCTREE		= 19;
	static final int STATE_TRACE_OCTREE			= 20;
	
	
	private static final int ORIG 	= 0;
	private static final int DIR 	= 1;
	private static final int SPEC 	= 2;
	private static final int INFO 	= 3;
	private static final int W_ST 	= 4;
	private static final int WEIGHT = 10;
	
	private int s;
	
	private boolean nonIntrusiv = false;
	
	
	private CausticMap localCausticMap;
	
	private ByteBuffer tmpBB;
	private ByteBuffer scatterBB;
	
	public SunshineBidirPathtracer()
	{
		RAYPROCESSOR = "pathRT.frag";
	}
	
	public void initialize(Options opts, ProgressMonitor progress, 
			ObjectHandler oh, int width, int height, int sceneSize, 
			int[] objects, int[] triangles)
	{
		super.initialize(opts, progress, oh, width, height, sceneSize, objects, triangles);
		
		nonIntrusiv = accelerate & nonIntrusiv;
		
		octree			= new SunshineOctree(((SunshineSceneVisitor)progress).getOctree());
				
		eyeDepth 			= getNumericOption(EYEPATH_DEPTH, 10).intValue();
		lightDepth 			= getNumericOption(LIGHTPATH_DEPTH, 10).intValue();
		heuristicExp		= getNumericOption(HEURISTIC_EXP, 2).intValue();


 		scatterBB		 	= BufferUtil.newByteBuffer(tileWidth * tileHeight * ObjectHandler.RGBA);
 		tmpBB 				= BufferUtil.newByteBuffer(imageWidth * imageHeight * ObjectHandler.RGBA);
		
		pStates  			= 2;
		
		currentEyeVertices		= 0;
		currentLightVertices 	= 0;
		
		combinePath 			= new int[3][];
		
		localCausticMap 		= new CausticMap(width*height);
		
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
		
		outputMode = Image.OUTPUT_LOG;
	} //Initialize
	

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
		
		complementTextures	= new int[12];
		outputTexture 		= new int[pStates][];
		ioTextures 			= new int[pStates][];
		
		attachmentpoints = new int[NUM_OF_ATTACHMENTS_PER_FBO];
		
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
		
		
		eyePathTexture 		= new int[eyeDepth+1];
		generateTexture(drawable, tileWidth, tileHeight, eyeDepth+1, eyePathTexture);
		
		radianceTexture		= new int[eyeDepth+1];
		generateTexture(drawable, tileWidth, tileHeight, eyeDepth+1, radianceTexture);
		
		lightPathTexture 	= new int[lightDepth+1];
		generateTexture(drawable, tileWidth, tileHeight, lightDepth+1, lightPathTexture);
		
		irradianceTexture	= new int[lightDepth+1];
		generateTexture(drawable, tileWidth, tileHeight, lightDepth+1, irradianceTexture);

		eVertexInfoTexture	= new int[eyeDepth+1];
		generateTexture(drawable, tileWidth, tileHeight, eyeDepth+1, eVertexInfoTexture);
		
		lVertexInfoTexture	= new int[lightDepth+1];
		generateTexture(drawable, tileWidth, tileHeight, lightDepth+1, lVertexInfoTexture);
		
		// generate the texture attachments
		for(int i = 0; i < pStates; i++) 
		{
			ioTextures[i] = new int[NUM_OF_ATTACHMENTS_PER_FBO+1];
			generateTexture(drawable, tileWidth, tileHeight, NUM_OF_ATTACHMENTS_PER_FBO+1, ioTextures[i]);
			
			// generate the output texture for the combine kernel
			outputTexture[i] = new int[2];
			generateTexture(drawable, tileWidth, tileHeight, 2, outputTexture[i]);
		} //for
		
		
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
		generateTexture(drawable, octree.getSize(), octree.getSize(), 1, treeTexture);
		
		// fill the tree texture with the tree cells
		transferToTexture(drawable, octree.getSize(), octree.getSize(), octree.getTreeData());
		
		
		// calculate the size of the odd tiles
		// => isn't that the number of tiles horizontally and vertically instead ?
		partsWidth	= getParts(imageWidth, tileWidth);
		partsHeight = getParts(imageHeight, tileHeight);
		
		// create the kernels
		createKernels(drawable);
	}
	
	
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
				current.set(1);
				currentSample 		= 0;
				
				// At first we have to activate the attachments.
				renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO-1);
				
				// The first attach of the texture to the attachments
				attachTextureToFBO(drawable, NUM_OF_ATTACHMENTS_PER_FBO-1, ioTextures[active]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				// set initial rendering buffer	
				state = STATE_INIT;
				break;
			} //case STATE_STARTUP
			 
			case STATE_INIT:
			{
				// set the viewport to the dimensions of the tile
				gl.glViewport(0, 0, tileWidth, tileHeight);

				renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
				super.execute(traceInit, NUM_OF_ATTACHMENTS_PER_FBO, 
						currentSample, ioTextures);
				
				currentSample = 0;
				state = STATE_GEN_EYERAY;
				
//				renderToTexture(drawable, 2);
				execute(combineInit, 2, 0, outputTexture);
								
				break;
			} //case STATE_INIT
			
			case STATE_GEN_EYERAY:
			{
				renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
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
				
								
				if(nonIntrusiv)
				{
					traceEyeRayKernel.setLoopParameter((int)Math.pow(4.0, octree.getDepth()), 1);
				}
				
				if(accelerate)
				{
					traceEyeRayKernel.setLoopParameter((int)Math.pow(4.0, octree.getDepth()), loopSteps);
				}
				
				traceEyeRayKernel.reset();
				

				render(genEyeRayKernel, eyePathTexture, radianceTexture, 
						eVertexInfoTexture, 0);
				
				// set the render destination for tracing the ray
				currentEyeVertices = 1;
				
				if(accelerate)
				{
					state = STATE_INIT_OCTREE;
				}
				else
				{
					state = STATE_TRACE_EYERAY;
				}
				
				break;
			} //case STATE_GENEYERAY
			
			
			case STATE_INIT_OCTREE:
			{
				renderToTexture(drawable, 1); 
				attachTextureToFBO(drawable, 0, ioTextures[active][3]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				octreeInit.setInputTextures(ioTextures[active], null);
				octreeInit.execute(drawable, currentTileX, currentTileY, 0);
				
				if(nonIntrusiv)
				{
					state = STATE_TRAVERSE_OCTREE;
				}
				else
				{
					state = STATE_TRACE_EYERAY;
					renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
				}
				
				break;
			}
			
			case STATE_TRAVERSE_OCTREE:
			{
				renderToTexture(drawable, 1); 
				attachTextureToFBO(drawable, 0, ioTextures[active == 1 ? 0 : 1][3]);
				checkFBO(drawable); 
				
				octreeKernel.setInputTextures(ioTextures[active], null);
				octreeKernel.execute(drawable, currentTileX, currentTileY, 0);
				
				int tmp = ioTextures[active][3];
				ioTextures[active][3] = ioTextures[active == 1 ? 0 : 1][3];
				ioTextures[active == 1 ? 0 : 1][3] = tmp;
				
				renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
				state = STATE_TRACE_EYERAY;
				break;
			}
			
			case STATE_TRACE_EYERAY:
			{
				if(currentEyeVertices > eyeDepth)
				{
					currentEyeVertices = 0;
					state = STATE_INIT_LIGHT;
					break;
				} //if
				
				resume = traceEyeRayKernel.resume();
				
				render(traceEyeRayKernel, eyePathTexture, radianceTexture, 
						eVertexInfoTexture, currentEyeVertices);
				
				gl.glFlush();
				
				// do recursive ray tracing
				if(resume)
				{
					currentEyeVertices++;					
					break;
				}
				
				if(nonIntrusiv)
				{
					state = STATE_TRAVERSE_OCTREE;
				}
				
				
				break;
			} //case STATE_TRACE
			
			
			case STATE_INIT_LIGHT:
			{
				renderToTexture(drawable, NUM_OF_ATTACHMENTS_PER_FBO);
				currentLightVertices = 0;
				
				ioTextures[active == 1 ? 0 : 1][ORIG] = lightPathTexture[currentLightVertices];
				ioTextures[active == 1 ? 0 : 1][SPEC] = irradianceTexture[currentLightVertices];
				ioTextures[active == 1 ? 0 : 1][INFO] = lVertexInfoTexture[currentLightVertices];
				 
				super.execute(lightInit, NUM_OF_ATTACHMENTS_PER_FBO, 
						currentSample, ioTextures);
				
				
				state = STATE_GEN_LIGHTRAY;				
				break;
			} //initLight
			
			case STATE_GEN_LIGHTRAY:
			{
				render(genLightRayKernel, lightPathTexture, irradianceTexture, 
						lVertexInfoTexture, 0);
				
				traceLightRayKernel.reset();
				
				currentLightVertices = 1;
				
				state = STATE_TRACE_LIGHTRAY;
				break;
			}
			
			case STATE_TRACE_LIGHTRAY:
			{
				if(currentLightVertices > lightDepth)
				{
					currentLightVertices = 0;
					state = STATE_INIT_COMPLEMENT;
					break;
				} //if
				
				resume = traceLightRayKernel.resume();
				
				render(traceLightRayKernel, lightPathTexture, irradianceTexture, 
						lVertexInfoTexture, currentLightVertices);
				gl.glFlush();
				
				// do recursive light ray tracing
				if(resume)
				{
					currentLightVertices++;
				}
				
				break;
			} //STATE_TRACE_LIGHTRAY
			
			// creates the combined path
			case STATE_INIT_COMPLEMENT:
			{
				gl.glFinish();
				
				indexS = currentLightVertices;
				indexT = currentEyeVertices;
				
				
				//create the path from light to camera
				createCombinePath(indexS, indexT, lightPathTexture, 
						irradianceTexture, lVertexInfoTexture, 
						eyePathTexture, radianceTexture, eVertexInfoTexture);

				fillComplementTexture(indexS);
				
				
				state = STATE_INIT_IS_VISIBLE_TEST;
				break;
			} //STATE_INIT_COMPLEMENT
			
			
			case STATE_INIT_IS_VISIBLE_TEST:
			{
				active = active == 1 ? 0 : 1;
				renderToTexture(drawable, 1);
				attachTextureToFBO(drawable, 0, ioTextures[active][W_ST]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				isVisibleInit.setInputTextures(ioTextures[active == 1 ? 0 : 1], null);
				isVisibleInit.setUniform(Kernel.CURRENT_EYE_VERTEX, currentEyeVertices);
				isVisibleInit.execute(drawable, currentTileX, currentTileY, 0);
				
				isVisibleKernel.reset();
				
				state = STATE_IS_VISIBLE_TEST;				
				break;
			}
			
			case STATE_IS_VISIBLE_TEST:
			{
				active = active == 1 ? 0 : 1;
				renderToTexture(drawable, 1);
				attachTextureToFBO(drawable, 0, ioTextures[active][W_ST]);
				
				// Check the FBO whether everything is fine.
				checkFBO(drawable);
				
				resume = isVisibleKernel.resume();
				
				complementTextures[WEIGHT] = ioTextures[active == 1 ? 0 : 1][W_ST];
				
				isVisibleKernel.setInputTextures(complementTextures, null);
				isVisibleKernel.execute(drawable, currentTileX, currentTileY, currentLightVertices);
				
				
				if(resume)
				{
					complementTextures[WEIGHT] = ioTextures[active][W_ST];
						
					s 		= currentLightVertices;
					state 	= STATE_CALC_WEIGHT;					
				}
				
				break;
			} //STATE_IS_VISIBLE_TEST
			

			case STATE_CALC_WEIGHT:
			{				
				// calculation of the probability density quotients 
				// conformable to Veach 10.9
				if(DIRECTION == BEGIN2END)
				{
					if(s < indexS+indexT)
					{
						traceComplement(BEGIN2END, s+1, indexS+indexT+1);
						s++;
					}
					else
					{
						s = currentLightVertices;
						DIRECTION = END2BEGIN;
						break;
					}
				}
				else //DIRECTION == END2BEGIN
				{
					if(s > 0)
					{
						traceComplement(END2BEGIN, s, indexS+indexT+1);
						s--;
					}
					else
					{
						s 			= currentLightVertices;
						state 		= STATE_COMBINE;
						DIRECTION 	= BEGIN2END;
						break;
					}
				}

				break;
					
			} //STATE_CALC_WEIGHT
			
			
			case STATE_COMBINE:
			{
				fillComplementTexture(currentLightVertices);
				
				combineKernel.setUniform(Kernel.LAST_VERTEX, indexS+indexT);
				combineKernel.setUniform(Kernel.CURRENT_EYE_VERTEX, currentEyeVertices);
				combineKernel.setUniform("currentSample", currentSample);
				
				execute(combineKernel, 2, currentLightVertices, outputTexture);
				
				if(currentEyeVertices == 0 && currentTileX == 0 
						&& currentTileY == 0 && currentSample == 0 )
				{
					createLightImage(drawable, outputTexture[current.get()]);
				}
				
				currentLightVertices++;
				state = STATE_INIT_COMPLEMENT;
				
				if(currentLightVertices > lightDepth)
				{
					currentEyeVertices++;
					currentLightVertices = 0;
				
					if(currentEyeVertices >= eyeDepth+1)
					{
						// do supersampling
						currentSample++;					
						
						if(currentSample < superSample)
						{
							state = STATE_GEN_EYERAY;
						} 
						else
						{
							state = STATE_DRAW;
						}
					} //if
				} //if
				
				break;
			} //case STATE_COMBINE
			
			case STATE_DRAW:
			{
				// draw the rendered tile to the ByteBuffer
				draw(drawable, vp, currentTileX, currentTileY, outputTexture[current.get()][0]);
				
				
				// Refresh the shown image
				((SunshineSceneVisitor) monitor).storeImage(mergeCaustic2Image(), outputMode);
				
				// rebind the actual FBO
				gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo[0]);
				
				// render next tile
				currentTileX++;
				if(currentTileX >= partsWidth)
				{
					currentTileX = 0;
					currentTileY++;
				}
				if(currentTileY >= partsHeight) 
				{
					state = STATE_FINAL;
				} 
				else 
				{
					state = STATE_INIT;
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
			} //case STATE_FINAL
			default:
				// error ?
				System.err.println("called state machine with unknown state");
		} //switch
		
		cancelPolling();

		// schedule sunshineAction for execution again
		EventQueue.invokeLater(sunshineAction);
	} //processNextKernel
	
	
	/**
	 * 
	 * @param indexS light index
	 * @param indexC eye index
	 * @param startPath
	 * @param startWeight
	 * @param startInfo
	 * @param complPath
	 * @param complWeight
	 * @param complInfo
	 */
	private void createCombinePath(int indexS, int indexC, 
			int[] startPath, int[] startWeight, int[] startInfo, 
			int[] complPath, int[] complWeight, int[] complInfo)
	{
		int pathVertices = indexS + indexC + 2;
		int k = indexS+1;

		combinePath[0] = new int[pathVertices];
		combinePath[1] = new int[pathVertices];
		combinePath[2] = new int[pathVertices];
		
		for(int i = 0; i < indexS+1; i++)
		{
			combinePath[0][i] = startPath[i];
			combinePath[1][i] = startWeight[i];
			combinePath[2][i] = startInfo[i];
		}

		for(int i = indexC; i >= 0; i--)
		{
			combinePath[0][k] = complPath[i];
			combinePath[1][k] = complWeight[i];
			combinePath[2][k] = complInfo[i];
			
			k++;
		}
	} //createCombinePath
	

	private void fillComplementTexture(int index)
	{
		int length = combinePath[0].length-1;
		
		complementTextures[0] = combinePath[0][Math.max(index-2, 0)];
		complementTextures[1] = combinePath[0][Math.max(index-1, 0)];
		complementTextures[2] = combinePath[0][index];
		complementTextures[3] = combinePath[0][Math.min(index+1, length)];
		complementTextures[4] = combinePath[0][Math.min(index+2, length)];
		
		complementTextures[5] = combinePath[1][Math.max(index-2, 0)];
		complementTextures[6] = combinePath[1][Math.max(index-1, 0)];
		complementTextures[7] = combinePath[1][index];
		complementTextures[8] = combinePath[1][Math.min(index+1, length)];
		complementTextures[9] = combinePath[1][Math.min(index+2, length)];
		
		
		complementTextures[11] = combinePath[2][index];
	}

	private void traceComplement(int direction, int index, int lastVertex)
	{
		fillComplementTexture(index);
		
		active = active == 1 ? 0 : 1;
		renderToTexture(drawable, 1);
		attachTextureToFBO(drawable, 0, ioTextures[active][W_ST]);
		
		// Check the FBO whether everything is ok.
		checkFBO(drawable);

		
		traceComplementKernel.setInputTextures(complementTextures, null);
		
		traceComplementKernel.setUniform("DIRECTION", direction);
		traceComplementKernel.setUniform(Kernel.CURRENT_EYE_VERTEX, currentEyeVertices);
		traceComplementKernel.setUniform(Kernel.CURRENT_VERTEX, index);
		traceComplementKernel.setUniform(Kernel.LAST_VERTEX, lastVertex);
		
		traceComplementKernel.execute(drawable, currentTileX, currentTileY, currentSample);
		
		complementTextures[WEIGHT] = ioTextures[active][W_ST];
		
	} //traceRecursively
	
	
	private void render(Kernel kernel, int[] vertexTexture, 
			int[] spectrumTexture, int[] infoTexture, int index)
	{
		int prevIndex = Math.max(index-1, 0);
		
		kernel.setTexture(vertexTexture[prevIndex], spectrumTexture[prevIndex]);
		kernel.setUniform(Kernel.CURRENT_VERTEX, index);
		
		//set render destination
		ioTextures[active == 1 ? 0 : 1][ORIG] = vertexTexture[index];
		ioTextures[active == 1 ? 0 : 1][SPEC] = spectrumTexture[index];
		ioTextures[active == 1 ? 0 : 1][INFO] = infoTexture[index];
		
		super.execute(kernel, NUM_OF_ATTACHMENTS_PER_FBO, currentSample, ioTextures);
		
		ioTextures[active][ORIG] = vertexTexture[index];
		ioTextures[active][SPEC] = spectrumTexture[index];
		ioTextures[active][INFO] = infoTexture[index];
	} //render
	
	
	@Override
	void createKernels(GLAutoDrawable drawable)
	{
		String intermediates = getIntermediates();
		
		traceInit 			= new TraceInitKernel("initKernel", drawable, seedTex, 
				tileWidth);
		
		genEyeRayKernel 	= new GenEyeRayKernel("genEyeRayKernel", drawable, 
				tileWidth, oh, imageWidth, imageHeight, grid);
		
		traceEyeRayKernel 	= new TraceEyeRayKernel("traceEyeRayKernel", drawable,
				sceneTexture, texTexture, treeTexture, oh, tileWidth, loopSteps, 
				octree, accelerate);
		

		lightInit			= new LightInitKernel("initLight", drawable, tileWidth);
		
		genLightRayKernel 	= new GenLightRayKernel("genLightRayKernel", drawable,
				sceneTexture, oh.getLightCount(), tileWidth);
		
		traceLightRayKernel = new TraceLightRayKernel("traceLightRayKernel", drawable,
				sceneTexture, texTexture, treeTexture, oh, tileWidth, loopSteps, 
				octree);
		
		
		isVisibleInit		= new IsVisibleInitKernel("isVisibleInit", drawable, tileWidth);
		
		isVisibleKernel		= new InterruptableIsVisibleKernel("isVisibleKernel", drawable,
				sceneTexture, tileWidth, texTexture, oh.getObjectCount(), loopSteps);
		
		combineInit 		= new CombineInitKernel("combineInit", drawable, tileWidth);
		
		combineKernel		= new CombineKernel("combineKernel", drawable,
				sceneTexture, texTexture, oh, tileWidth, imageWidth, imageHeight);
		
		
		traceComplementKernel = new ComplementKernel("complementKernel", 
				drawable, tileWidth, sceneTexture, texTexture, oh, heuristicExp);
		
		
		octreeInit			= new OctreeInitKernel("octreeInit", drawable, treeTexture, tileWidth);
		octreeKernel 		= octree.getKernel(drawable, treeTexture, tileWidth, 
				oh.getObjectCount(), loopSteps);
		
		
		//compile and link the kernel
		armKernel(drawable, octreeInit, null, intermediates, true);
		armKernel(drawable, octreeKernel, (SunshineSceneVisitor)monitor, intermediates, false);
		
		armKernel(drawable, traceInit, null, null, false);	
		armKernel(drawable, genEyeRayKernel, null, null, true);
		armKernel(drawable, traceEyeRayKernel, (SunshineSceneVisitor)monitor, intermediates, true);

		armKernel(drawable, lightInit, null, null, false);
		armKernel(drawable, genLightRayKernel, null, intermediates, true);
		armKernel(drawable, traceLightRayKernel,(SunshineSceneVisitor)monitor, intermediates, false);
		
		armKernel(drawable, isVisibleInit, null, null, false);
		armKernel(drawable, isVisibleKernel, (SunshineSceneVisitor)monitor, intermediates, true);

		
		armKernel(drawable, combineInit, null, null, true);
		armKernel(drawable, traceComplementKernel, (SunshineSceneVisitor)monitor, intermediates, true);
		armKernel(drawable, combineKernel, (SunshineSceneVisitor)monitor, intermediates, true);
	} //createKernels
	
	
	void shutDown(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
		gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		for (int i = 0; i < pStates; i++) {
			gl.glDeleteTextures(NUM_OF_ATTACHMENTS_PER_FBO, ioTextures[i], 0);
			gl.glDeleteTextures(2, outputTexture[i], 0);
		}
		
		gl.glDeleteTextures(eyePathTexture.length, eyePathTexture, 0);
		gl.glDeleteTextures(lightPathTexture.length, lightPathTexture, 0);
		gl.glDeleteTextures(complementTextures.length, complementTextures, 0);
		gl.glDeleteTextures(eVertexInfoTexture.length, eVertexInfoTexture, 0);
		gl.glDeleteTextures(lVertexInfoTexture.length, lVertexInfoTexture, 0);
		
		gl.glDeleteTextures(1, sceneTexture, 0);
		gl.glDeleteTextures(1, texTexture, 0);
		gl.glDeleteTextures(1, treeTexture, 0);		
		
		gl.glDeleteFramebuffersEXT(fbo.length, fbo, 0);
	}
	

	@Override
	protected void execute(Kernel kernel, int count, int sample, int[][] renderDest)
	{
		renderToTexture(drawable, count);
		kernel.setInputTextures(complementTextures, renderDest[current.get()]);
		current.swap();
		attachTextureToFBO(drawable, count, renderDest[current.get()]);
		
		kernel.execute(drawable, currentTileX, currentTileY, sample);
	}
	
	
	/**
	 * attach a given attachment sets to the given attachmentpoints of the single given FBO 
	 */ 
	private void attachTextureToFBO(GLAutoDrawable drawable, int attachmentpoint, 
			int target)
	{		
		GL gl = drawable.getGL();

		gl.glFramebufferTexture2DEXT
		(
			GL_FRAMEBUFFER_EXT, 
			attachmentpoints[attachmentpoint], 
			texTarget, target, 0
		);

	} //attachTextureToFBOs
	
	
	
	private void createLightImage(GLAutoDrawable drawable, int[] texture)
	{
		GL gl = drawable.getGL();
		
		// Choose the texture where the scatter data are stored.
		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glBindTexture(texTarget, texture[1]);
		gl.glEnable(texTarget);
		
		// Resetting the scatter buffer
		scatterBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, scatterBB);
		
		scatterBB.rewind();
		
		int pos;
		
		for(int i = 0; i < scatterBB.limit() / 16; i++)
		{
			Color4f causCol = new Color4f();
			pos = (int)scatterBB.getFloat();
			
			causCol.x = scatterBB.getFloat();
			causCol.y = scatterBB.getFloat();
			causCol.z = scatterBB.getFloat();
			causCol.w = 1f;
			
			localCausticMap.saveColor(causCol, pos);
		}
		
	}

	
	private ByteBuffer mergeCaustic2Image()
	{
		imageBB.rewind();
		tmpBB.rewind();
		
		for(int i = 0; i < imageBB.limit() / 16; i++)
		{
			imageBB.position(i*16);
			
			CausticElement caustic = localCausticMap.getCausticElement(i);
			if(caustic.color != null)
			{
				tmpBB.putFloat((superSample*imageBB.getFloat() + caustic.color.x / caustic.causticCounter)
						/ (float)(superSample + caustic.causticCounter));
				tmpBB.putFloat((superSample*imageBB.getFloat() + caustic.color.y / caustic.causticCounter) 
						/ (float)(superSample + caustic.causticCounter));
				tmpBB.putFloat((superSample*imageBB.getFloat() + caustic.color.z / caustic.causticCounter)
						/ (float)(superSample + caustic.causticCounter));
				
//				tmpBB.putFloat(imageBB.getFloat() + caustic.color.x / caustic.causticCounter);
//				tmpBB.putFloat(imageBB.getFloat() + caustic.color.y / caustic.causticCounter);
//				tmpBB.putFloat(imageBB.getFloat() + caustic.color.z / caustic.causticCounter);
				
			}
			else 
			{
				tmpBB.putFloat(imageBB.getFloat());
				tmpBB.putFloat(imageBB.getFloat());
				tmpBB.putFloat(imageBB.getFloat());
			}
			
			tmpBB.putFloat(1);
		}
		
		return tmpBB;
	}
	
	
	private void write(int texture)
	{
		GL gl = GLU.getCurrentGL();
		
		ByteBuffer tmpBB = BufferUtil.newByteBuffer(tileWidth * tileHeight * ObjectHandler.RGBA);
		
		// Choose the texture where the scatter data are stored.
		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glBindTexture(texTarget, texture);
		gl.glEnable(texTarget);
		
		// Resetting the temporary ByteBuffer
		tmpBB.rewind();
		
		// Copying the pixel data to the temporary ByteBuffer
		gl.glGetTexImage(texTarget, 0, GL_RGBA, GL_FLOAT, tmpBB);
		
		tmpBB.rewind();
		
		if(currentTileX == 0 && currentTileY == 0)
		{
			try
			{
				PrintWriter f = new PrintWriter(new BufferedWriter(
						new FileWriter("../Sunshine/tmp/weight.txt")));

				for(int y = 0; y < tileHeight; y++)
				{	
					for(int x = 0; x < tileWidth; x++)
					{								
						float tmpR = tmpBB.getFloat();
						float tmpG = tmpBB.getFloat();
						float tmpB = tmpBB.getFloat();
						float tmpA = tmpBB.getFloat();

						if(tmpA > 0f)
							f.printf("%f, %f, %f, %f\t\t\n", tmpR, tmpG, tmpB, tmpA);

						if((x + 1) % tileWidth == 0)
							f.println();						
					}
				}	

				f.close();

			} catch (IOException e) {
				System.err.println("Could not create file");
			}
		}
	}

}
