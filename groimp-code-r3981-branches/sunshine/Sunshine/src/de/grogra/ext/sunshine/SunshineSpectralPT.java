package de.grogra.ext.sunshine;

import static javax.media.opengl.GL.GL_FRAMEBUFFER_EXT;

import java.awt.EventQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.kernel.InitKernel;
import de.grogra.ext.sunshine.kernel.IntersectionKernel;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.ext.sunshine.shader.SunshineSunSky;
import de.grogra.ext.sunshine.spectral.MaterialHandler;
import de.grogra.ext.sunshine.spectral.RGB2Spectrum_Illum;
import de.grogra.ext.sunshine.spectral.RGB2Spectrum_Ref;
import de.grogra.ext.sunshine.spectral.SPDConversion;
import de.grogra.ext.sunshine.spectral.kernel.ConversionKernel;
import de.grogra.ext.sunshine.spectral.kernel.SpectralGenRayKernel;
import de.grogra.ext.sunshine.spectral.kernel.SpectralShadingKernel;
import de.grogra.ext.sunshine.spectral.kernel.SpectralTempMatKernel;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;

public class SunshineSpectralPT extends SunshinePathtracer {
	
	public static final int WAVELENGTH_MIN		= 360;
	public static final int WAVELENGTH_MAX		= 830;
	public static final int WAVELENGTH_STEP		= 1;
	public static final int STANDARD_TEMP		= 6500;
	
	protected static final String LAMBDA_MIN	= "SpectralPathTracer/lambdaMin"; 
	protected static final String LAMBDA_MAX	= "SpectralPathTracer/lambdaMax"; 
	protected static final String LAMBDA_STEP	= "SpectralPathTracer/lambdaStep"; 
	
	private Kernel conversionKernel;
	private Kernel tempMatKernel;
	
	private int lambdaMin 						= WAVELENGTH_MIN;
	private int lambdaMax 						= WAVELENGTH_MAX;
	private int lambdaStep						= WAVELENGTH_STEP;
	private int lambda							= 0;
	private int lambdaDelta						= 0;
	private int lambdaSamples					= 0;
	private float invLambdaSamples				= 0f;
	
	private int colorRGB2SPDFirstRow			= 0;
	private double colorRGB2SPDstep				= 0;
	private double colorRGB2SPDRow				= 0;
	
	private int matCols							= 0;	
	private int matRowOffset					= 0;
	private int matRowOffsetStep				= 0;
	
	private int[] colorRGB2SPDRefTex			= new int[1];
	private int[] colorRGB2SPDIlluTex			= new int[1];
	private int[] materialsTex					= new int[1];
	
	static final int STATE_CON					= 13;	
	
	private MaterialHandler mh;
	private boolean hasImageTex					= false;
	
	Timer timer;
	
	class ThisTimerTask extends TimerTask
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			drawable.getGL().glFinish();
		}
		
	}
	
	@Override
	public void init(GLAutoDrawable drawable) 
	{		
		super.init(drawable);
		
		outputMode 		= Image.OUTPUT_LOG;		
		
		lambdaMin		= getNumericOption(LAMBDA_MIN, WAVELENGTH_MIN).intValue();
		lambdaMax		= getNumericOption(LAMBDA_MAX, WAVELENGTH_MAX).intValue();
		lambdaStep		= getNumericOption(LAMBDA_STEP, WAVELENGTH_STEP).intValue();
		
		if(lambdaMin < WAVELENGTH_MIN)		
			throw new IllegalArgumentException("Lambda min is lower than minimum wavelength (" + WAVELENGTH_MIN + "nm).");
		
		if(lambdaMax > WAVELENGTH_MAX)		
			throw new IllegalArgumentException("Lambda max is greater than maximum wavelength (" + WAVELENGTH_MAX + "nm).");
		
		if(lambdaMin > lambdaMax)		
			throw new IllegalArgumentException("Lambda min has to be smaller then lambda max.");
		
		if(lambdaStep <= 0)		
			throw new IllegalArgumentException("We need to go from lower to upper wavelength. Step value has to be positive.");
		
		lambdaDelta		= lambdaMax - lambdaMin;
		lambdaSamples	= (int) Math.round(((double) lambdaDelta) / ((double) lambdaStep));
		
		// Special case 1: We do only one lambda step
		if(lambdaMin == lambdaMax)
		{
			lambdaSamples 	= 1;
			lambdaStep		= 1;
			lambdaDelta		= 1;
		}
		
		// Special case 2: Step is bigger then range
		if(lambdaDelta <= lambdaStep)
		{
			lambdaSamples 	= 2;
			lambdaStep		= lambdaDelta - 1;
		}
		
		lambda			= lambdaMin;
		invLambdaSamples= 1.0f / ((float) lambdaSamples);
		
		// Initialize table for RGB to Spectrum conversion on GPU side
		initRGB2SPDTable();
		
		// Initialize table for all materials which has to be upload to GPU
		initMaterialTable();
		
		//DEBUG_MODE	= 1;
		
		hasImageTex		=  mh != null && mh.hasImages();
		
		// fill the texture atlas with images, get by the texture handler
		if( hasImageTex )
			prepareImageTexture(drawable);
		
		timer = new Timer();
	}
	
	protected void prepareImageTexture(GLAutoDrawable drawable)
	{
		int imgPerRow	= 8;
		
		GL gl			= drawable.getGL();
		int counter 	= mh.getImageCount();
		
		int x 			= counter;
		int y 			= 1;
		
		// calculates the size of the texture atlas
		if(counter > imgPerRow)
		{
			y 			= counter % imgPerRow == 0 ? counter / imgPerRow : counter / imgPerRow + 1;
			x 			= (int)Math.ceil(  (float)counter / (float) y );	
		}
		
		int imgTexX 	= x*512;
		int imgTexY 	= y*512;
		
		// generate the texture atlas
		generateTexture(drawable, imgTexX, imgTexY, 1, texTexture);
		
		IntBuffer tmp;
		
		// fill the texture atlas with images, get by the texture handler
		for (int i = 0; i < mh.getImageCount(); i++)
		{
			tmp			= ObjectHandler.getPixels(mh.getImage(i));
			
			int xPos	= (i%x) * 512;
			int yPos	= (i/x) * 512;
			
			gl.glTexSubImage2D(texTarget, 0, xPos, yPos, 512, 512, 
					GL.GL_BGRA,	GL.GL_UNSIGNED_BYTE, tmp );
			
			//checkSentData(drawable, 512*512*4*4, true, GL.GL_BGRA);
		} //for		
	}
	
	@Override
	public void initialize(Options opts, ProgressMonitor progress, 
			ObjectHandler oh, int width, int height, int sceneSize, 
			int[] objects, int[] trianglesCount)
	{
		
		super.initialize(opts, progress, oh, width, height, sceneSize, objects, trianglesCount);
		
		
		// We have to check again, whether we do extra uv-mapping or not. The reason for this is
		// that not the ObjectHandler but the MaterialHandler manage image data.
		sepMaterialFetch		= getBooleanOption(SEP_UV_MAPPING, false); 
		
		choosenLightSource 		= chooseLightSource();
		
	}
	
	private void initRGB2SPDTable()
	{
		int numCol				= 2;	
		int numRow				= RGB2Spectrum_Ref.refrgb2spect_bins;
		
		ByteBuffer colorRGB2SPD = BufferUtil.newByteBuffer(numCol * numRow * ObjectHandler.RGBA) ;
		
		/* For reflectance conversion */
		RGB2Spectrum_Ref.getAllSPDs(colorRGB2SPD);
		
		// generate the scene texture
		generateTexture(drawable, numCol, numRow, 1, colorRGB2SPDRefTex, GL.GL_RGBA);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, numCol, numRow, colorRGB2SPD, GL.GL_RGBA);
		/* Done - reflectance */
		
		/* For illuminance conversion */
		colorRGB2SPD.rewind();
		
		RGB2Spectrum_Illum.getAllSPDs(colorRGB2SPD);
		
		// generate the scene texture
		generateTexture(drawable, numCol, numRow, 1, colorRGB2SPDIlluTex, GL.GL_RGBA);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, numCol, numRow, colorRGB2SPD, GL.GL_RGBA);
		/* Done - illuminance */
		
		//checkSentData(drawable, colorRGB2SPD.limit(), false, GL.GL_RGBA);
		
		double invDelta		= 1.0 / (RGB2Spectrum_Ref.refrgb2spect_end - RGB2Spectrum_Ref.refrgb2spect_start);
		
		double firstRow		= 0.0;
		
		firstRow 			= lambdaMin - RGB2Spectrum_Ref.refrgb2spect_start;
		firstRow   		   *= invDelta;	
		firstRow			= firstRow > 1.0 ? 1.0 : firstRow;
		firstRow		   *= RGB2Spectrum_Ref.refrgb2spect_bins;
		
		double temp			= (((double) lambdaStep) * invDelta);
		
		if(temp > 1.0) temp	= 1.0;		
		if(temp < 0.0) temp	= 0.0;
		
		colorRGB2SPDstep	= temp * RGB2Spectrum_Ref.refrgb2spect_bins;
		
		colorRGB2SPDFirstRow= (int) Math.round(firstRow);
		colorRGB2SPDRow		= colorRGB2SPDFirstRow;
	}
	
	private void initMaterialTable()
	{
		mh					= ((SunshineSceneVisitor) monitor).getMatHandler();
		
		// When there is no material handler we have to exit the material table 
		// texture prepartaion
		if(mh == null)
			return;
		
		int numCols			= mh.prepareData(lambdaMin, lambdaMax, lambdaStep, lambdaSamples);		
		int numRows			= numCols * lambdaSamples;
		
		matCols				= numCols;		
		matRowOffsetStep	= numCols;
		
		// generate the scene texture
		generateTexture(drawable, numCols, numRows, 1, materialsTex, GL.GL_LUMINANCE);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, numCols, numRows, mh.getMaterials(), GL.GL_LUMINANCE);
	}
	
	protected void setNextLambda()
	{
		lambda 			= Math.min(lambda + lambdaStep, lambdaMax);
		matRowOffset	= matRowOffset + matRowOffsetStep;
		
		colorRGB2SPDRow	= Math.min(colorRGB2SPDRow + colorRGB2SPDstep, RGB2Spectrum_Ref.refrgb2spect_bins);		
	}
	
	@Override
	public void processNextKernel() 
	{
		// obtain GL instance from the drawable
		
		// setting params for actual tile which has to be draw
		int drawXTile 	= currentTileX;
		int drawYTile	= currentTileY;
		
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
				
				//timer.schedule(new ThisTimerTask(), 4000L);
				
				break;
			}
			case STATE_SHADOW_TEST:
			{						
				processShadowTest();			
				break;
			}
			case STATE_GET_MATERIAL:
			{
				processTempMat();
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
			case STATE_CON: 
			{
				processCon();
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
	
	protected void processGenRay()
	{
		// give feedback about rendering progress
		if (monitor != null && (System.currentTimeMillis() - lastTimeMillis) > 1000L)
		{
			lastTimeMillis = System.currentTimeMillis();
			float progress = 0;
			
			if(lambdaSamples > 1)
				progress = (progress + ( ((float) (lambda -lambdaMin)) / ((float) lambdaDelta)));
			else
				progress = (progress + currentSample) / superSample;
			
			progress = (progress + currentTileX) / partsWidth;
			progress = (progress + currentTileY) / partsHeight;
			
			monitor.setProgress(Resources.msg("renderer.rendering",
					progress), progress);
		}				
		
		//genRayKernel.setUniform("VIEWER_LIGHT_SOURCE", SPDConversion.D65[(lambda - lambdaMin) + 30]);
						
		execute(genRayKernel, currentSample, true);				
		
		currentRecursionDepth 	= 0;
		state 					= STATE_INTERSECT;					
		
		loopStart 				= 0;
		loopStop 				= Math.min(loopSteps, objectCount);	
	}
	
	protected void deliverParameters(Kernel kernel)
	{
		kernel.setUniform("recursionDepth", currentRecursionDepth);
		kernel.setUniform("lambda", lambda);
		kernel.setUniform("colorRGB2SPDRow", ((int) colorRGB2SPDRow));
		kernel.setUniform("matRowOffset", matRowOffset);
		
		kernel.setUniform("CIE_X", SPDConversion.getXMatch(lambda));
		kernel.setUniform("CIE_Y", SPDConversion.getYMatch(lambda));
		kernel.setUniform("CIE_Z", SPDConversion.getZMatch(lambda));
		
		kernel.setUniform("S0", SunshineSunSky.kS0Spectrum.sample(lambda));
		kernel.setUniform("S1", SunshineSunSky.kS1Spectrum.sample(lambda));
		kernel.setUniform("S2", SunshineSunSky.kS2Spectrum.sample(lambda));
		
		kernel.setUniform("lightPass", choosenLightSource);	
		
	}
	
	protected void checkForNextState()
	{
		choosenLightSource = chooseLightSource();

		currentRecursionDepth++;

		if(currentRecursionDepth >= recursionDepth)
		{							
			currentSample++;

			if(currentSample < superSample)
			{						
				state = STATE_GENRAY;						

			} else {					

				setNextLambda();		

				if(lambda >= lambdaMax )
					state = STATE_CON;
				else
					state = STATE_GENRAY;

				currentSample = 0;
				currentRecursionDepth = 0;

			}

		} else {

			state			= STATE_INTERSECT;						
		}


		loopStart 	= 0;
		loopStop 	= Math.min(loopSteps, objectCount);	
	}
	
	protected void processShade()
	{		
		deliverParameters(shadeKernel);
		
		execute(shadeKernel, currentSample, true);		
		
		if(sepSecRay)		
			state = STATE_SEC_SPAWN;	
		else
			checkForNextState();
	}
	
	protected void processSecSpawn()
	{
		deliverParameters(spawnKernel);
		
		execute(spawnKernel, currentSample, true);		
		
		checkForNextState();
	}
	
	protected void processCon()
	{
		execute(conversionKernel, currentSample, true);	
		
		// render next tile
		currentTileX++;
		
		if (currentTileX >= partsWidth) 
		{
			currentTileX = 0;
			currentTileY++;
		}
		if (currentTileY >= partsHeight) 
		{
			lastRefreshTime = 0;
			state = STATE_FINAL;
		} 
		else 
		{
			lambda 			= lambdaMin;
			colorRGB2SPDRow	= colorRGB2SPDFirstRow;
			matRowOffset	= 0;
			
			state = STATE_INIT;
		}								
		
		lastRefreshTime = 0;
	}
	
	@Override
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
		long lambdaSamples	= this.lambdaSamples; 
		
		if(lambdaSamples == 0)
			lambdaSamples	= 1;
		
		long res			= (long) (imageWidth * imageHeight);
		long res_supsam		= res * ((long) (grid * grid));
		
		long primRay		= res_supsam * lambdaSamples;
		long complete		= primRay * ((long) recursionDepth);
		long secRay			= complete - primRay;
		long perSec			= (long) (complete / (((double) time) * 0.001d));
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("rayprocessor.default.statistics", new Object[]{complete, primRay, secRay, perSec}) ); 
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("raytracer.scene.statistics", new Object[]{objectCount, lightCount}) );

		stats.append(de.grogra.ext.sunshine.Resources.msg("rayprocessor.spectral.statistics", new Object[]{lambdaMin, lambdaMax, lambdaStep, lambdaSamples}) );
		
		stats.append(de.grogra.ext.sunshine.Resources.msg("rayprocessor.pathtracer.statistics", new Object[]{recursionDepth}) );
				
		stats.append(de.grogra.ext.sunshine.Resources.msg("antialiasing.stochastic.statistics", new Object[]{grid, grid}) );
		
		stats.append ("</pre></html>").toString ();
		
		return stats;
	}
	
	@Override
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
		gl.glDeleteTextures(1, materialsTex, 0);
		gl.glDeleteTextures(1, colorRGB2SPDRefTex, 0);
		gl.glDeleteTextures(1, colorRGB2SPDIlluTex, 0);
		
		if(sepMaterialFetch)	
			gl.glDeleteTextures(NUM_OF_ATTACHMENTS_PER_FBO, tempMaterialTex, 0);
		
		gl.glDeleteFramebuffersEXT(fbo.length, fbo, 0);
	} //shutDown
	
	@Override
	void createKernels(GLAutoDrawable drawable) 
	{		
		// create the kernels
		initKernel 			= new InitKernel("initKernel", drawable, tileWidth);
		genRayKernel 		= new SpectralGenRayKernel("genRayKernel", drawable,  tileWidth);
		intersectionKernel 	= new IntersectionKernel("intersectionKernel", drawable, sceneTexture, treeTexture,  tileWidth);		
		shadowKernel		= new IntersectionKernel("shadowKernel", drawable, sceneTexture, treeTexture,  tileWidth);
		shadeKernel 		= new SpectralShadingKernel("shadeKernel", drawable,
										sceneTexture, texTexture, treeTexture, hasImageTex, materialsTex, colorRGB2SPDRefTex, colorRGB2SPDIlluTex, tileWidth, tempMaterialTex);
		spawnKernel 		= new SpectralShadingKernel("spawnKernel", drawable, 
										sceneTexture, texTexture, treeTexture, hasImageTex, materialsTex, colorRGB2SPDRefTex, colorRGB2SPDIlluTex, tileWidth, tempMaterialTex);
		
		conversionKernel 	= new ConversionKernel("conversionKernel", drawable, tileWidth);
		
		intersectionKernel.setDebug();	
		shadeKernel.setDebug();
		shadowKernel.setDebug();
		spawnKernel.setDebug();
		conversionKernel.setDebug();
				
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
		
		int lightCount = objects[6];
		
		//if(((SunshineSceneVisitor) monitor).hasSunSky())
		//	lightCount++;
		
		initKernel.setSource(drawable, new String[]
		{
				loadSource("extension.frag"),
				"uniform sampler2DRect RND_TEX; \n",
				loadSource("random.frag"),
				loadSpectralSource("init.frag") 
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
				"const int lightCount 		= " + lightCount + ";\n",
				loadSource("random.frag"),
				loadSpectralSource("genRaySource.frag")
		});
		
		// Most of the part is the same for intersectionKernel and shadowKernel
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
				"const int lightCount 		= " + lightCount + ";\n",
				"const int countObjects		= " + objectCount + ";\n",
				"const bool hasSunSky			= " + (((SunshineSceneVisitor) monitor).hasSunSky() ? "true" : "false") + ";\n",
				tris,
				"const float meshPart			= " + oh.getMeshPart() +".0;\n",
				"const float paraPart			= " + oh.getParaPart() +".0;\n",
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				"bool accelerate			= " + accelerate +";\n",
				"float superSample 		= " + superSample+".0;\n",
				oh.hasNormals() ? "#define HAS_NORMALS\n" : "",
				loadSource("structs.frag"),
				loadSpectralSource("reflection/reflectUtils.frag"), 
				loadSpectralSource("structs.frag"),
				loadSource("mathCalc.frag"),
				loadSource("getObjects.frag"),
				loadSource("normals.frag"),
				loadSource("intersections.frag"),
				loadSource("intersectionLoop.frag"),
				loadSource("intersectUtils.frag"),
				loadSource("initValues.frag"),
				loadSpectralSource("intersectionTestMain.frag")
		};
		
		// .. so we duplicate the source code..
		String[] shadowKernelSourec = intersectionKernelSource.clone();
		
		// .. and substitute the main for the shadowKernel.
		shadowKernelSourec[shadowKernelSourec.length - 1] = loadSpectralSource("shadowTestMain.frag");
		
		// Finally setting the source code to the according kernels.
		intersectionKernel.setSource(drawable, intersectionKernelSource);	
		shadowKernel.setSource(drawable, shadowKernelSourec);	
		
		String[] shadeKernelSource = new String[] 
		{			
				loadSource("extension.frag"),
				loadSource("samplers.frag") + "\n",		
				sepMaterialFetch ? "uniform sampler2DRect tempMatTex;\n" : "",
				hasImageTex ? "#define HAS_IMG_TEX 1\n" : "\n",
				loadSource("random.frag") + "\n", 
				loadSource("structs.frag"),
				loadSpectralSource("structs.frag"),
				/* Some needed constants */
				"const int size 				= " + sceneSize +";\n",	
				"const int sphereCount 		= " + objects[0] + ";\n",
				"const int boxCount 			= " + objects[1] + ";\n",
				"const int cylinderCount 		= " + objects[2] + ";\n",
				"const int planeCount 		= " + objects[3] + ";\n",
				"const int meshCount 			= " + objects[4] + ";\n",
				"const int paraCount 			= " + objects[5] + ";\n",
				"const int lightCount 		= " + lightCount + ";\n",
				"const int countObjects		= " + objectCount + ";\n",
				"const float meshPart			= " + oh.getMeshPart() +".0;\n",
				"const float paraPart			= " + oh.getParaPart() +".0;\n",
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				tris,
				"const bool accelerate			= " + accelerate +";\n",
				"const float superSample 		= " + superSample + ".0;\n", 			
				"const float invLambdaSamples	= " + invLambdaSamples + ";\n",
				"const int lambdaMin			= " + lambdaMin + ";\n",		
				"const int lambdaMax			= " + lambdaMax + ";\n",		
				"const int lambdaSteps			= " + lambdaStep + ";\n",
				"const int matCols				= " + matCols + ";\n",
				"const int imageWidth			= " + imageWidth + ";\n",
				"const int tileWidth 			= " + tileWidth + ";\n\n",
				"const bool hasSunSky			= " + (((SunshineSceneVisitor) monitor).hasSunSky() ? "true" : "false") + ";\n", //30
				/* Some needed constants: DONE */
				loadSource("mathCalc.frag"),													//31
				loadSource("getObjects.frag"),													//32
				((SunshineSceneVisitor) monitor).getSunSkyParas() + " \n",						//33
				(((SunshineSceneVisitor) monitor).hasSunSky() ? "#define USE_SUNSKY 1 \n" : " \n"), //34
				loadSpectralSource("sunsky.frag"), 												//35
				loadSpectralSource("rgb2SPD.frag"), 											//36
				loadSpectralSource("getObjects.frag"),											//37
				loadSource("intersectUtils.frag"),
				!sepMaterialFetch ? loadSource("uvMapping.frag") : loadSource("fetchUVMap.frag"),	//38
				!sepMaterialFetch ? loadSpectralSource("material.frag") : loadSpectralSource("tempMaterialFetch.frag"),											//31
				loadSpectralSource("shaderTransforamtions.frag"), 								//39
				loadSpectralSource("reflection/reflectUtils.frag"), 							//40
				loadSpectralSource("reflection/microdistribution/beckmannDistribution.frag"), 	//41
				loadSpectralSource("reflection/microdistribution/blinnDistribution.frag"), 		//42
				loadSpectralSource("reflection/fresnel/fresnelslick.frag"), 					//42
				loadSpectralSource("reflection/fresnel/fresnelDielectric.frag"), 				//43
				loadSpectralSource("reflection/BSDF_Microfacet.frag"), 							//44
				loadSpectralSource("reflection/BRDF_SpecularReflection.frag"), 					//45
				loadSpectralSource("reflection/BTDF_SpecularTransmission.frag"), 				//46
				loadSpectralSource("reflection/BRDF_CookTorrance.frag"), 						//46
				loadSpectralSource("reflection/BRDF_Lambertian.frag"), 							//47
				loadSpectralSource("reflection/Sampler.frag"), 									//48
				loadSource("initValues.frag"),													//49
				loadSpectralSource("shadeSpectral.frag") 										//50
		};
		
		if(!sepSecRay)
		{
			// No separation is needed so we can do shading and secray-spawning at once.
			shadeKernelSource[shadeKernelSource.length - 2] 	+= "\n#define SHADE_MODE_BxDF 1 \n";
			
			if(recursionDepth > 1)
				shadeKernelSource[shadeKernelSource.length - 2] += "#define SHADE_MODE_SPAWN 1 \n\n";
			
			shadeKernel.setSource(drawable, shadeKernelSource);	
		}
		else
		{
			// Copy shaderKernel source for the spwanKernel
			String[] spawnKernelSource = shadeKernelSource.clone();
			
			// Setup to ensure that shading kernel is only doing BxDF computation
			shadeKernelSource[shadeKernelSource.length - 2] += "\n#define SHADE_MODE_BxDF 1 \n\n";
			
			// Shading kernel is only computing directions
			shadeKernel.setSource(drawable, shadeKernelSource);	
			
			if(recursionDepth > 1)
			{			
				// Setup for spawnKernel to compute secondary rays
				spawnKernelSource[shadeKernelSource.length - 2] += "\n#define SHADE_MODE_SPAWN 1 \n\n";
				
				// Spawning kernel spawns only secondary rays
				spawnKernel.setSource(drawable, spawnKernelSource);	
			}
		}
			
		conversionKernel.setSource(drawable, new String[] {
				loadSource("extension.frag"),	
				loadSource("samplers.frag") + "\n",
				loadSpectralSource("walkerConversion.frag"),	
				loadSpectralSource("spectralConMain.frag")	
		});
		
		//compile and link the shader
		armKernel(drawable, initKernel);
		armKernel(drawable, genRayKernel);
		armKernel(drawable, intersectionKernel);
		armKernel(drawable, shadowKernel);
		armKernel(drawable, shadeKernel);
		
		if(sepSecRay)
			armKernel(drawable, spawnKernel);
		
		armKernel(drawable, conversionKernel);
		
		if(sepMaterialFetch)
			createTempMatKernel(drawable);
		
	}
	
	protected void processTempMat()
	{
		tempMatKernel.setUniform("lambda", lambda);
		tempMatKernel.setUniform("colorRGB2SPDRow", ((int) colorRGB2SPDRow));
		tempMatKernel.setUniform("matRowOffset", matRowOffset);
		
		// setting the new textures..
		tempMatKernel.setInputTextures(ioTextures[activeTraceData], ioTextures[activeImageTex]);
		
		attachTextureToFBO(drawable, NUM_OF_ATTACHMENTS_PER_FBO, tempMaterialTex);
		
		tempMatKernel.execute(drawable, currentTileX, currentTileY, 0);
		
		state = STATE_SHADE;	
	}
	
	protected void createTempMatKernel(GLAutoDrawable drawable)
	{
		((SpectralShadingKernel) shadeKernel).setExtraMaterialFetch(true);
		((SpectralShadingKernel) spawnKernel).setExtraMaterialFetch(true);
		
		tempMatKernel = new SpectralTempMatKernel("tempMatKernel", drawable, sceneTexture, texTexture, materialsTex, 
				colorRGB2SPDRefTex, colorRGB2SPDIlluTex, tileWidth); 
		
		tempMatKernel.setDebug();
		
		tempMatKernel.setSource(drawable, new String[]{
				loadSource("extension.frag"),
				loadSource("structs.frag"),
				loadSpectralSource("structs.frag"),
				loadSource("samplers.frag") + "\n",
				"const int size 				= " + sceneSize +";\n",	
				"const float lightPart			= " + oh.getLightPart() +".0;\n",
				"const int matCols				= " + matCols + ";\n",
				loadSource("mathCalc.frag"),
				loadSpectralSource("rgb2SPD.frag"), 
				loadSource("getObjects.frag"),
				loadSource("uvMapping.frag"),
				loadSpectralSource("generateTempMatMain.frag")
		});
		
		armKernel(drawable, tempMatKernel);
	}
	
	protected String loadSpectralSource(String s)
	{
		s = "spectral/kernel/shaderSource/" + s;
			
		return SunshineRaytracer.loadSourceRaw(s);
	}
	
}