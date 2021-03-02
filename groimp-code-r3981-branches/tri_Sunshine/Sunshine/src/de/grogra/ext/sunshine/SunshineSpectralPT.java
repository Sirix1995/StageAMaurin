package de.grogra.ext.sunshine;

import java.awt.EventQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import java.nio.ByteBuffer;
import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.kernel.ConversionKernel;
import de.grogra.ext.sunshine.kernel.GenRayKernel;
import de.grogra.ext.sunshine.kernel.InitKernel;
import de.grogra.ext.sunshine.kernel.IntersectionKernel;
import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.kernel.ShadingKernel;
import de.grogra.ext.sunshine.kernel.Spectral2ColorConversionKernel;
import de.grogra.ext.sunshine.output.Image;

public class SunshineSpectralPT extends SunshinePathtracer {
	
	private Kernel conversionKernel;
	 
	/*				initKernel, 
					genRayKernel, 
					intersectionKernel, 
					shadeKernel;*/
	
	private ByteBuffer colorMatch;
	private int[] colorMatchTex	= new int[1];
	
	private int lamdaMin 		= 360;
	private int lamdaMax 		= 830;
	private int lamda			= 0;
	private int lamdaSteps		= 1;
	
	private int cMTexSize 		= 0;
	
	static final int STATE_CON	= 13;	
	
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		
		outputMode 	= Image.OUTPUT_RAW;
		lamda		= lamdaMin;
		
		
	}
	
	private void initColorMatch()
	{
		int numCol	= (int) Math.ceil( Math.sqrt( SPDConversion.NUM_OF_XYZ_ELEMENTS ) / 3d ) * 3;	
		int numRow	= (int) Math.ceil( SPDConversion.NUM_OF_XYZ_ELEMENTS / (3d * numCol));
		
		cMTexSize	= numCol;
		
		colorMatch 	= BufferUtil.newByteBuffer(numCol * numRow * ObjectHandler.RGB) ;
		
		SPDConversion.getScaledXYZMatchFunction(1.0f, colorMatch);
		
		// generate the scene texture
		generateTexture(drawable, numCol, numRow, 1, colorMatchTex, GL.GL_RGB);
		
		// fill the texture with content, get by the object handler
		transferToTexture(drawable, numCol, numRow, colorMatch, GL.GL_RGB);
		
		checkSentData(drawable, colorMatch.limit(), true, GL.GL_RGB);
	}
	
	@Override
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
			case STATE_CON: 
			{
				processCon(gl);
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
	
	protected void processShade(GL gl)
	{
		int cieIndex = (lamda - lamdaMin) * 3;
		
		shadeKernel.setParameter("lamda", lamda);
		shadeKernel.setParameter("X", SPDConversion.matchFunctionXYZ[cieIndex + 0]);
		shadeKernel.setParameter("Y", SPDConversion.matchFunctionXYZ[cieIndex + 1]);
		shadeKernel.setParameter("Z", SPDConversion.matchFunctionXYZ[cieIndex + 2]);
		
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
					
					
					state = STATE_CON;
				}
				
			} else {
				
				state			= STATE_INTERSECT;						
				lightCounter	= 0;
			}
		}				
		
		intersectLoopStart 	= 0;
		intersectLoopStop 	= Math.min(intersectionLoopStep, countObjects);	
	}
	
	protected void processCon(GL gl)
	{
		conversionKernel.setParameter("lamda", lamda);
		
		execute(conversionKernel, currentSample, true);	
		
		lamda++;
		
		if(lamda >= lamdaMax )
		{
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
				lamda = lamdaMin;
				state = STATE_INIT;
			}							
		} else {
			currentSample = 0;
			currentRecursionDepth = 0;
			state = STATE_GENRAY;	
		}
			

	}
	
	@Override
	void createKernels(GLAutoDrawable drawable) 
	{		
		initColorMatch();
		
		// create the kernels
		initKernel 			= new InitKernel("initKernel", drawable);
		genRayKernel 		= new GenRayKernel("genRayKernel", drawable);
		intersectionKernel 	= new IntersectionKernel("intersectionKernel", drawable, sceneTexture, kdTexture);		
		shadeKernel 		= new ShadingKernel("shadeKernel", drawable,
										sceneTexture, texTexture, kdTexture, oh.hasImages());
		conversionKernel 	= new ConversionKernel("conversionKernel", drawable, colorMatchTex);
		
		intersectionKernel.setDebug();	
		shadeKernel.setDebug();
		conversionKernel.setDebug();
				
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
				loadSource("samplers.frag") + "\n",
				"int lamdaMin			= " + lamdaMin + ";\n",		
				"int lamdaMax			= " + lamdaMax + ";\n",		
				"int lamdaSteps			= " + lamdaSteps + ";\n",
				"int colorMatchTexSize 	= " + cMTexSize + ";\n",						
				"int imageWidth			= " + imageWidth + ";\n",
				"int tileWidth 			= " + TILE_WIDTH + ";\n\n",
			/*	"#define SHADOW_FEELER\n",
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
				*/
				loadSource("/spectral/shadeSpectral.frag") 
		});		
		
		conversionKernel.setSource(drawable, new String[] {
				loadSource("extension.frag"),	
				loadSource("samplers.frag") + "\n",
				"int lamdaMin			= " + lamdaMin + ";\n",		
				"int lamdaMax			= " + lamdaMax + ";\n",		
				"int lamdaSteps			= " + lamdaSteps + ";\n",
				"int colorMatchTexSize 	= " + cMTexSize + ";\n",						
				"int imageWidth			= " + imageWidth + ";\n",
				"int tileWidth 			= " + TILE_WIDTH + ";\n\n",
				loadSource("/spectral/walkerConversion.frag"),	
				loadSource("/spectral/spectralConMain.frag")	
		});
		
		//compile and link the shader
		armKernel(drawable, initKernel);
		armKernel(drawable, genRayKernel);
		armKernel(drawable, intersectionKernel);
		armKernel(drawable, shadeKernel);
		armKernel(drawable, conversionKernel);
		
	}
	
	
	
}