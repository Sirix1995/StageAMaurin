package de.grogra.ray2.tracing;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import sun.security.action.GetLongAction;

import com.sun.org.apache.bcel.internal.generic.LLOAD;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.ray2.antialiasing.Antialiasing;
import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.antialiasing.NoAntialiasing;
import de.grogra.ray2.tracing.PixelwiseRenderer.RenderTask;
import de.grogra.ray2.tracing.PixelwiseRenderer.Result;
import de.grogra.ray2.tracing.modular.TracingMediator;
import de.grogra.task.PartialTask;
import de.grogra.task.Solver;
import de.grogra.task.SolverInOwnThread;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Line;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class MetropolisRenderer extends PixelwiseRenderer {
	
	
	public static final String INITIAL_COUNT = "MetropolisPathTracer/initialCount";
	public static final String SEED_COUNT = "MetropolisPathTracer/seedCount";
	public static final String MUTATION_PP_COUNT = "MetropolisPathTracer/mutationPPCount";
//	public static final String TWOSTAGE_REF = "MetropolisPathTracer/Refinement/TwoStageRefinement";
//	public static final String DIRECTLIGHT_REF = "MetropolisPathTracer/Refinement/DirectLightingRefinement";
//	public static final String EXPVALUES_REF = "MetropolisPathTracer/Refinement/ExpectedValuesRefinement";
//	public static final String IMPORTANCE_REF = "MetropolisPathTracer/Refinement/ImportanceRefinement";
//	public static final String TESTIMAGE_MUTpPIX = "MetropolisPathTracer/Refinement/testimageMutationPerPixel";
//	public static final String TESTIMAGE_CORRECTION = "MetropolisPathTracer/Refinement/testimageCorrectionFactor";
	
	public static final int FIRST_STAGE =0;
	public static final int SECOND_STAGE =1;
	
	protected int[] pixelChangeArray;
	
	public static int initialCount = 20000;
	public static int seedPathCount = 100;
	public static int mutationPPCount = 1;
//	public static int mutationLoops =0;
//	public static int mutationProgress =0;
	
	public int mutatedPixPerProc;
	public int solverCount;
	public int notMutatedPixelsLeft;
	public static boolean twoStage_Ref;
	public static boolean directLightning_Ref;
	public static boolean expectedValues_Ref;
	public static boolean importanceSampling_Ref;
	
	public static int actRenderingStage;
//	public static int totalLoops;
//	
//	private static int subHeight;

	protected BufferedImage firstStageImage;
	protected int[] firstStageRgbaPixels;
	protected float[][] firstStageHdrPixels;
	protected Color4f firstStageBrightestValue;
	protected Color4f firstStageDarkestValue;
	protected Color4f firstStageAverageValue;
	protected static float testimageCorrectionFactor;
	public static int testimageMutperPix = 1;
	

	
	protected MTRandom rnd;
	
	int nextImageUpdateI;
	long nextImageUpdateTime;
	int maxY =0;
	float maxYFloat=0;
	
//	int renderedPixels;
	
	
	public static class MetropolisResult extends Result
	{
		public IntList cols = new IntList (500);
		public int lastIndex=0;

	}
	
	
	public static class MLTTask implements PartialTask
	{
		public int mutatedPixPerProc;
	}
	
	
	
	public synchronized void stop ()
	{
		super.stop();
		((MetropolisAntiAliasing)antialiasing).stopProcessor();
	}
	
	
	public void initialize (Options opts, ProgressMonitor progress)
	{
		super.initialize(opts, progress);
		
		
		initialCount = getNumericOption(INITIAL_COUNT, 100000).intValue();
		assert initialCount >=1;
		
		System.err.println(" MetroRenderer: initialize: initialCount=" + initialCount);
		
		seedPathCount = getNumericOption(SEED_COUNT, 10000).intValue();
		assert seedPathCount >=1;
		
		System.err.println(" MetroRenderer: initialize: seedPathCount=" + seedPathCount);
		
		
		mutationPPCount = getNumericOption(MUTATION_PP_COUNT, 10000).intValue();
		assert mutationPPCount >=1;
		
//		twoStage_Ref = getBooleanOption(TWOSTAGE_REF, true);
//		directLightning_Ref = getBooleanOption(DIRECTLIGHT_REF, true);
//		expectedValues_Ref = getBooleanOption(EXPVALUES_REF, true);
//		importanceSampling_Ref  = getBooleanOption(IMPORTANCE_REF, true);
		twoStage_Ref = false;
		directLightning_Ref = false;
		expectedValues_Ref = true;
		importanceSampling_Ref  = false;
		
//		testimageMutperPix = getNumericOption(TESTIMAGE_MUTpPIX, 10).intValue();
//		assert testimageMutperPix >=1;
//		
//		testimageCorrectionFactor = getNumericOption(TESTIMAGE_CORRECTION, 2).floatValue();
		
		System.err.println(" MetroRenderer: initialize: mutationPPCount=" + mutationPPCount);
		
		antialiasing = 	new MetropolisAntiAliasing ();
		//antialiasing.initialize(this, originalScene);

		rnd = new MTRandom(getSeed());
		
		changedPixels =0;
	}
	
	public void render (Scene scene, Sensor camera,
			Matrix4d cameraTransformation, int width, int height,
			ImageObserver obs)
	{
		
		changedPixels=0;
		
		pixelChangeArray = new int[width * height];
		
		int thC = (threadCount>=2) ? threadCount : 1;
		
		
		if(twoStage_Ref) {
		
			actRenderingStage = FIRST_STAGE;
			if (hdr)
			{
				firstStageHdrPixels = new float[4][width * height];
				DataBufferFloat buffer = new DataBufferFloat (firstStageHdrPixels, firstStageHdrPixels[0].length);
				BandedSampleModel sampleModel = new BandedSampleModel (buffer.getDataType (), width, height, 4);
				WritableRaster raster = Raster.createWritableRaster (sampleModel, buffer, null);
				ColorSpace cs = ColorSpace.getInstance (ColorSpace.CS_sRGB);
				ComponentColorModel cm = new ComponentColorModel (cs, true, false, Transparency.TRANSLUCENT, buffer.getDataType ());
				firstStageImage = new BufferedImage (cm, raster, false, null);
			}
			else
			{
				firstStageImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
				firstStageRgbaPixels = ((DataBufferInt) firstStageImage.getRaster ().getDataBuffer ()).getData ();
			}
			
	
			//low sampling rate
			mutationPPCount = testimageMutperPix;
			notMutatedPixelsLeft = mutationPPCount*width*height;
			
			mutatedPixPerProc = Math.round(((float)notMutatedPixelsLeft) / ((float)thC)) +1;
	
			System.err.println(" MetroRend: renderer: ************************************* First Stage!*********************");
			
			super.render(scene, camera, cameraTransformation, width, height, obs);
			
			prefilterTestImage3();
			
			//Full sampling rate
			mutationPPCount = getNumericOption(MUTATION_PP_COUNT, 10000).intValue();
			assert mutationPPCount >=1;
			
			pixelChangeArray = new int[width * height];
			changedPixels=0;
			
			System.err.println(" MetroRend: renderer: ************************************ Second Stage!*********************");
		}

		actRenderingStage = SECOND_STAGE;
		
		notMutatedPixelsLeft = mutationPPCount*width*height;
		
		mutatedPixPerProc = Math.round(((float)notMutatedPixelsLeft) / ((float)thC)) +1;
		
		super.render(scene, camera, cameraTransformation, width, height, obs);
	}
	
	
	
	public Solver createLocalSolver (final boolean sameThread)
	{
		final Antialiasing aa = antialiasing.dup (originalScene.dup ());
		solverCount++;
		
		return new SolverInOwnThread ()
		{
			
			private final MetropolisResult result = new MetropolisResult ();

			@Override
			protected void solveImpl (PartialTask task)
			{
				renderMLT (aa, ((MLTTask) task).mutatedPixPerProc, solverCount, result);
			}

			@Override
			protected Thread createThread ()
			{
				if (sameThread)
				{
					return null;
				}
				Thread t = new Thread (this, toString ());
				t.setPriority (Thread.MIN_PRIORITY);
				return t;
			}
		};
	}
	
	
	
	
	
	
	
	protected PartialTask nextPartialTask (int solverIndex)
	{
	
		MLTTask task = new MLTTask ();
		
		if(notMutatedPixelsLeft > mutatedPixPerProc){
			notMutatedPixelsLeft -= mutatedPixPerProc;
			task.mutatedPixPerProc = mutatedPixPerProc;
			return task;
			
		}else if (notMutatedPixelsLeft > 0){
			
			task.mutatedPixPerProc = notMutatedPixelsLeft;
			notMutatedPixelsLeft = 0;
			return task;
			
		}else return null;
		
	}
	
	@Override
	protected void dispose (PartialTask task)
	{
//		IntList list = ((RenderTask) task).lines;
//		for (int i = 0; i < list.size; i++)
//		{
//			lineState[list.elements[i]] &= ~RENDERING;
//		}
	}
	
	
	
	public synchronized void increaseNotMutatedPixels(int count){
		notMutatedPixelsLeft += count;
	}

	@Override
	protected synchronized boolean done ()
	{
		System.err.println("  MetroRender: done()    ret=" +((changedPixels >= (width*height*mutationPPCount))));
		System.err.println("  MetroRender: done()    changed= " +changedPixels +"  target= " +width*height*mutationPPCount);
//		return (notMutatedPixelsLeft ==0);
		return (changedPixels >= (width*height*mutationPPCount-10));
//		return ((((float)renderedPixels)/((float)(width*height))) > mutationPPCount);
	}
	
	
	protected void renderMLT (Antialiasing antialiasing, int mutatedPixPerProc, int solverNo,
			Result res)
	{

		System.err.println(" MetroRenderer: renderMLT:  \n " +
				"\n  SolverNo:" +solverNo +
				"\n  mutatedPixPerProc:" +mutatedPixPerProc +
				"\n  imWidth= " +width +"  imHeight= " +height+ "\n" +
				"\n  mutationPerPix= " +mutationPPCount +"  threads= " +threadCount +"  InitialPaths= " +seedPathCount);

		
		((MetropolisAntiAliasing)antialiasing).resetAll();
		
		((MetropolisAntiAliasing)antialiasing).signAntialiser2Processor();
//		((MetropolisAntiAliasing)antialiasing).initializeProccessorLoop
//			(getBrightness(), width, height, mutatedPixPerInitialPath, (MetropolisResult)res);
		
		double pixelWidth = 2.0 / width;
		double pixelHeight = 2.0 / height;

		Color4f color = new Color4f ();

		res.lines.setSize (0);
		res.data.setSize (0);
		MTRandom rnd = new MTRandom ();
		
		int mutatedCount =0;
		int targetMutationCount = mutatedPixPerProc;
		int loop=0;
		while(mutatedCount < targetMutationCount){
			
			int mutatedPixPerInitialPath = Math.round(((float)targetMutationCount) / ((float)seedPathCount)) +1;
			
			((MetropolisAntiAliasing)antialiasing).initializeProccessorLoop
			(getBrightness(), width, height, mutatedPixPerInitialPath, (MetropolisResult)res,solverNo);
			
			
			rnd.setSeed (seed + 0x5deece66dL * (seedPathCount+loop));
			loop++;
			
			System.err.println(" MetroRend: renderMLT: Before loop " +loop +"  target Mutation Count= " +targetMutationCount);
			for (int i = 0; i < seedPathCount; i++)
			{
			
				
				((MetropolisAntiAliasing)antialiasing).pathChanged();
				
				
//				System.err.println(" Metro-Renderer-loop: " +i);
				
				float y = rnd.nextInt(height) +0.5f;
				float x = rnd.nextInt(width) + +0.5f;
	//			int y = height/2; 
	//			int x = width/2; 
				
				
				//beginfor
				if (isStopped ())
					{
						return;
					}
				DEBUG_PIXEL = (((int)x) == DEBUG_X) && (((int)y) == DEBUG_Y);				
				DEBUG_SUBPIXEL = DEBUG_PIXEL;
					
				DEBUG_SUBPIXEL = (((int)y)== Math.round((height*DEBUG_LINE)));
	
				((MetropolisAntiAliasing)antialiasing).setPixelXY(x, y);
				
				
				
				antialiasing.getColorOfRectangle (x * pixelWidth - 1,
						(0.5 * height - (y + 1)) * pixelWidth, pixelWidth, pixelWidth,
						color, rnd);
	
 
				mutatedCount += ((MetropolisAntiAliasing)antialiasing).getProcessorMutatedPixels();
				
//				increaseNotMutatedPixels(notMutated);
			
			}
			System.err.println(" MetroRend: renderMLT: After loop " +loop +"  target Mutation Count= " +targetMutationCount +" reached mutation count=" +mutatedCount);
			targetMutationCount = targetMutationCount - mutatedCount;
			mutatedCount =0;
		}
	}
	
	

	
	
	
	
	
	@Override
	public synchronized void merge (Result res)
	{

		float[] data = res.data.elements;
		float maxValue = this.maxValue;
		
		for (int i = 0; i <= ((MetropolisResult)res).lastIndex; i++)
		{
			int y = res.lines.elements[i];
			int x = ((MetropolisResult)res).cols.elements[i];

			int f = i *  4;
			int j = x + y*width;
			
			
			if (autoAdjust){
				float t = data[f + 3];
				if (t * data[f] > maxValue)	{
					maxValue = t * data[f];
				}
				if (t * data[f + 1] > maxValue)
				{
					maxValue = t * data[f + 1];
				}
				if (t * data[f + 2] > maxValue)
				{
					maxValue = t * data[f + 2];
				}
			}

			
			int mult =  pixelChangeArray[j];
			if (hdr)
			{
				
				float last_bright = brightness / Math.max (hdrPixels[3][j], 1e-3f);

				hdrPixels[0][j] *=  1f/last_bright;
				hdrPixels[1][j] *=  1f/last_bright;
				hdrPixels[2][j] *= 	1f/last_bright;
					
				hdrPixels[3][j] =((hdrPixels[3][j]*mult)+ data[f+3]) / (mult+1);
				
				float bright = brightness / Math.max (hdrPixels[3][j], 1e-3f);
				
				hdrPixels[0][j] +=data[f] ;
				hdrPixels[1][j] +=data[f+1];
				hdrPixels[2][j] +=data[f+2];
				
				hdrPixels[0][j] *= bright;
				hdrPixels[1][j] *= bright;
				hdrPixels[2][j] *= bright;
				
				if(luminance(hdrPixels[0][j],hdrPixels[1][j],hdrPixels[2][j]) > 
					luminance(maxColor.x, maxColor.y, maxColor.z)){
					maxColor.x = hdrPixels[0][j];
					maxColor.y = hdrPixels[1][j];
					maxColor.z = hdrPixels[2][j];
					maxColor.w = hdrPixels[3][j];
				}
				if(luminance(hdrPixels[0][j],hdrPixels[1][j],hdrPixels[2][j]) < 
						luminance(minColor.x, minColor.y, minColor.z)){
					minColor.x = hdrPixels[0][j];
					minColor.y = hdrPixels[1][j];
					minColor.z = hdrPixels[2][j];
					minColor.w = hdrPixels[3][j];
				}
			}else
			{
				float[] cols = toRGBA(rgbaPixels[j]);
				float r = data[f] + cols[0];
				float g = data[f+1] + cols[1];
				float b = data[f+2] + cols[2];
				float a = (data[f+3] + cols[3]*mult)/(mult +1);
				rgbaPixels[j] = toIntColor (r,g,b,a);

			}
			
			
			
			if(twoStage_Ref && (actRenderingStage == FIRST_STAGE)){
				if (hdr)
				{
					
					float last_bright = brightness / Math.max (firstStageHdrPixels[3][j], 1e-3f);
					
					firstStageHdrPixels[0][j] *=  1f/last_bright;
					firstStageHdrPixels[1][j] *=  1f/last_bright;
					firstStageHdrPixels[2][j] *= 	1f/last_bright;
						
					firstStageHdrPixels[3][j] = ((firstStageHdrPixels[3][j]*mult)+ data[f+3]) / (mult+1);
					
					float bright = brightness / Math.max (hdrPixels[3][j], 1e-3f);
				
					firstStageHdrPixels[0][j] += data[f];
					firstStageHdrPixels[1][j] += data[f+1];
					firstStageHdrPixels[2][j] += data[f+2];
					firstStageHdrPixels[0][j] *= bright;
					firstStageHdrPixels[1][j] *= bright;
					firstStageHdrPixels[2][j] *= bright;

				}else
				{
					float[] cols = toRGBA(firstStageRgbaPixels[j]);
					float r = data[f] + cols[0];
					float g = data[f+1] + cols[1];
					float b = data[f+2] + cols[2];
					float a = (data[f+3] + cols[3]*mult)/(mult +1);
					firstStageRgbaPixels[j] = toIntColor (r,g,b,a);
				}
			}
			
			pixelChangeArray[j]++;
			changedPixels++;

		}//for
	
		this.maxValue = maxValue;

		observer.imageUpdate (image, ImageObserver.SOMEBITS, 0,0, width, height);
		
		if (monitor != null)
		{
			float progress = (((float)changedPixels)/((float)(width*height))) /((float)mutationPPCount) ;
			monitor.setProgress (Resources.msg ("renderer.rendering",
				new Float (progress)), progress);
		}
	}	
	
	
	
	public float [] getPixelsForLine2Vertex(Environment env, Point3d vertex){
		float[] ret = {-1,-1};
		
//		env.localToGlobal.set (getCameraTransformation ());
////		env.globalToLocal.m33 = 1;
//		Math2.invertAffine (env.localToGlobal, env.globalToLocal);

		double pixelWidth = 2.0 / width; 
		double pixelHeight = 2.0 / height;
		
		float[] res = getCamera().getUVForVertex(env, vertex);
//		System.err.println("MetroRend: getPixelForLine2Vert:  before ret= (" +res[0] +";"+res[1]+")  weidth=" +width +"   height="+height);
		if((res[0]==-10) || (res[1]==-10) ) return ret;


		
		ret[0] = Math.min((float)((res[0] +1)/pixelWidth),width-1);
//		ret[0] = Math.round(res[0]* ((width-1)/2f) +(width-1)/2f) +0.5f;
//		ret[1] = Math.round((height-1)/2f - res[1]* ((height-1)/2f) ) +0.5f;
		ret[1] = (float)(-res[1]/pixelWidth + 0.5 * height - 1);
//		System.err.println("MetroRend: getPixelForLine2Vert: after   ret= (" +ret[0] +";"+ret[1]+")");
		
		if((ret[0]<0)||(ret[0]>=width)) return new float[]{-1,-1};
		if((ret[1]<0)||(ret[1]>=height)) return new float[]{-1,-1};
		
		maxY = (int)Math.max(ret[1], maxY);
		maxYFloat = Math.min(res[1], maxYFloat);
//		System.err.println("MetroRend: getPixelForLine2Vert:  before ret= (" +res[0] +";"+res[1]+")  weidth=" +width +"   height="+height +"           maxY="+maxY +"   maxYFloat="+maxYFloat);
		
		return ret;
	}

	
	public float getFirstStageImageValue(int x, int y){
		int j = x + y*width;
		float ret=0;
		double r,g,b;
		if(hdr){
			r = firstStageHdrPixels[0][j];
			g = firstStageHdrPixels[1][j];
			b = firstStageHdrPixels[2][j];
			ret = luminance(r, g, b);
		}
		else {
			float[] rgb = toRGBA(firstStageRgbaPixels[j]);
			r = rgb[0];
			g = rgb[1];
			b = rgb[2];
			ret = luminance(r, g, b);
		}
		return ret;
	}
	
	
	private void prefilterTestImage1(){
		
		//Find brightest Value
		firstStageBrightestValue = new Color4f();
		firstStageAverageValue = new Color4f();
		int countValidPix =0;
		
		for(int i=0; i< width; i++){
			for(int j=0; j< height; j++){
				float ret = getFirstStageImageValue(i, j); 
				float fSBV = luminance(firstStageBrightestValue.x, firstStageBrightestValue.y, firstStageBrightestValue.z);
				int pos = i + j*width;
				if(ret > fSBV) {
					setExtremumValue(firstStageBrightestValue, pos);
				}//if
				if(ret>0){
					add2ExtremumValue(firstStageAverageValue, pos);
					countValidPix++;					
				}
			}//for
		}//for
		
		firstStageAverageValue.scale( 1f/((float)countValidPix));

		// displace Zero-Values with Random
		for(int i=0; i< width; i++){
			for(int j=0; j< height; j++){
				float ret = getFirstStageImageValue(i, j);
				int pos = i + j*width;
				if(ret == 0) {
					float avX = firstStageAverageValue.x ;
					float avY = firstStageAverageValue.y ;
					float avZ = firstStageAverageValue.z ;
					float avW = firstStageAverageValue.w ;
					if(hdr){
						firstStageHdrPixels[0][pos] = avX;
						firstStageHdrPixels[1][pos] = avY;
						firstStageHdrPixels[2][pos] = avZ;
						firstStageHdrPixels[3][pos] = avW;
					}
					else {
						int val = toIntColor(avX,avY,avZ,avW);
						firstStageRgbaPixels[pos] = val;
					}//else
				}
				
			}//for
		}//for
		
	}
	
	
	private void prefilterTestImage3(){
		
		//Find brightest Value
		firstStageBrightestValue = new Color4f();
		firstStageAverageValue = new Color4f();
		int countValidPix =0;
		
		
		for(int i=0; i< width; i++){
			for(int j=0; j< height; j++){
				float ret = getFirstStageImageValue(i, j); 
				float fSBV = luminance(firstStageBrightestValue.x, firstStageBrightestValue.y, firstStageBrightestValue.z);
				int pos = i + j*width;
				if(ret > fSBV) {
					setExtremumValue(firstStageBrightestValue, pos);
				}//if
				if(ret>0){
					add2ExtremumValue(firstStageAverageValue, pos);
					countValidPix++;					
				}
			}//for
		}//for
		
		
		
		if(testimageCorrectionFactor <=1){
			
			firstStageDarkestValue = new Color4f(firstStageBrightestValue);
			for(int i=0; i< width; i++){
				for(int j=0; j< height; j++){
					float ret = getFirstStageImageValue(i, j); 
					float fSDV = luminance(firstStageDarkestValue.x, firstStageDarkestValue.y, firstStageDarkestValue.z);
					if((ret>0) && (ret < fSDV)) {
						int pos = i + j*width;
						setExtremumValue(firstStageDarkestValue, pos);
					}//if
				}//for
			}//for
			
//			System.err.println(" MetroRend: prefilter:    brightestVal=" +firstStageBrightestValue);

			// displace Zero-Values with Random
			for(int i=0; i< width; i++){
				for(int j=0; j< height; j++){
					float ret = getFirstStageImageValue(i, j);
					int pos = i + j*width;
					if(ret == 0) {
						float minX = firstStageDarkestValue.x ;
						float minY = firstStageDarkestValue.y ;
						float minZ = firstStageDarkestValue.z ;
						float minW = firstStageDarkestValue.w ;
						if(hdr){
							firstStageHdrPixels[0][pos] = minX;
							firstStageHdrPixels[1][pos] = minY;
							firstStageHdrPixels[2][pos] = minZ;
							firstStageHdrPixels[3][pos] = minW;
						}
						else {
							int val = toIntColor(minX,minY,minZ,minW);
							firstStageRgbaPixels[pos] = val;
						}//else
					}
					
				}//for
			}//for
		}else{
		
			
			firstStageAverageValue.scale( 1f/((float)countValidPix));
			float mult = 1f + 1f/testimageCorrectionFactor;
			
			// displace Zero-Values with Random
			for(int i=0; i< width; i++){
				for(int j=0; j< height; j++){
					float ret = getFirstStageImageValue(i, j);
					float avV = luminance(firstStageAverageValue.x, firstStageAverageValue.y, firstStageAverageValue.z);
					int pos = i + j*width;
					if(ret >= avV*mult) {
						float v =  avV / ret * mult;
						if(hdr){
							firstStageHdrPixels[0][pos] *= v;
							firstStageHdrPixels[1][pos] *= v;
							firstStageHdrPixels[2][pos] *= v;
							firstStageHdrPixels[3][pos] *= v;
						}
						else {
							float[] rgb = toRGBA(firstStageRgbaPixels[pos]);
							rgb[0] *=v;
							rgb[1]*=v;
							rgb[2]*=v;
							rgb[3]*=v;
							int val = toIntColor(rgb[0],rgb[1],rgb[2],rgb[3]);
							firstStageRgbaPixels[pos] = val;
						}//else
					}
							
					
					if(hdr){
						firstStageHdrPixels[0][pos] = firstStageAverageValue.x- 
							(firstStageAverageValue.x - firstStageHdrPixels[0][pos])/testimageCorrectionFactor;
						firstStageHdrPixels[1][pos] = firstStageAverageValue.y- 
							(firstStageAverageValue.y - firstStageHdrPixels[1][pos])/testimageCorrectionFactor;
						firstStageHdrPixels[2][pos] = firstStageAverageValue.z- 
							(firstStageAverageValue.z - firstStageHdrPixels[2][pos])/testimageCorrectionFactor;
						firstStageHdrPixels[3][pos] = firstStageAverageValue.w- 
							(firstStageAverageValue.w - firstStageHdrPixels[3][pos])/testimageCorrectionFactor;
					}
					else {
						float[] rgb = toRGBA(firstStageRgbaPixels[pos]);
						rgb[0] = firstStageAverageValue.x - (firstStageAverageValue.x - rgb[0])/testimageCorrectionFactor;
						rgb[1] = firstStageAverageValue.y - (firstStageAverageValue.y - rgb[1])/testimageCorrectionFactor;
						rgb[2] = firstStageAverageValue.z - (firstStageAverageValue.z - rgb[2])/testimageCorrectionFactor;
						rgb[3] = firstStageAverageValue.w - (firstStageAverageValue.w - rgb[3])/testimageCorrectionFactor;
						int val = toIntColor(rgb[0],rgb[1],rgb[2],rgb[3]);
						firstStageRgbaPixels[pos] = val;
					}//else				
					
				}//for
			}//for
		}
	}
	
	
	
	
	
	private void prefilterTestImage2(){
		
		//Find brightest Value
		firstStageBrightestValue = new Color4f();

		
		for(int i=0; i< width; i++){
			for(int j=0; j< height; j++){
				float ret = getFirstStageImageValue(i, j); 
				float fSBV = luminance(firstStageBrightestValue.x, firstStageBrightestValue.y, firstStageBrightestValue.z);
				int pos = i + j*width;
				if(ret > fSBV) {
					setExtremumValue(firstStageBrightestValue, pos);
				}//if
			}//for
		}//for
		
		
		firstStageDarkestValue = new Color4f(firstStageBrightestValue);
		for(int i=0; i< width; i++){
			for(int j=0; j< height; j++){
				float ret = getFirstStageImageValue(i, j); 
				float fSDV = luminance(firstStageDarkestValue.x, firstStageDarkestValue.y, firstStageDarkestValue.z);
				if((ret>0) && (ret < fSDV)) {
					int pos = i + j*width;
					setExtremumValue(firstStageDarkestValue, pos);
				}//if
			}//for
		}//for
		
//		System.err.println(" MetroRend: prefilter:    brightestVal=" +firstStageBrightestValue);

		// displace Zero-Values with Random
		for(int i=0; i< width; i++){
			for(int j=0; j< height; j++){
				float ret = getFirstStageImageValue(i, j);
				int pos = i + j*width;
				if(ret == 0) {
					float minX = firstStageDarkestValue.x ;
					float minY = firstStageDarkestValue.y ;
					float minZ = firstStageDarkestValue.z ;
					float minW = firstStageDarkestValue.w ;
					if(hdr){
						firstStageHdrPixels[0][pos] = minX;
						firstStageHdrPixels[1][pos] = minY;
						firstStageHdrPixels[2][pos] = minZ;
						firstStageHdrPixels[3][pos] = minW;
					}
					else {
						int val = toIntColor(minX,minY,minZ,minW);
						firstStageRgbaPixels[pos] = val;
					}//else
				}
				
			}//for
		}//for

		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void setExtremumValue(Color4f extremum, int pos){
		if(hdr){
			extremum.x = firstStageHdrPixels[0][pos];
			extremum.y = firstStageHdrPixels[1][pos];
			extremum.z = firstStageHdrPixels[2][pos];
			extremum.w = firstStageHdrPixels[3][pos];
		}
		else {
			float[] rgb = toRGBA(firstStageRgbaPixels[pos]);
			extremum.x = rgb[0];
			extremum.y = rgb[1];
			extremum.z = rgb[2];
			extremum.w = rgb[3];
		}//else
	}
	
	
	private void add2ExtremumValue(Color4f extremum, int pos){
		if(hdr){
			extremum.x += firstStageHdrPixels[0][pos];
			extremum.y += firstStageHdrPixels[1][pos];
			extremum.z += firstStageHdrPixels[2][pos];
			extremum.w += firstStageHdrPixels[3][pos];
		}
		else {
			float[] rgb = toRGBA(firstStageRgbaPixels[pos]);
			extremum.x += rgb[0];
			extremum.y += rgb[1];
			extremum.z += rgb[2];
			extremum.w += rgb[3];
		}//else
	}
	
	
	
}
