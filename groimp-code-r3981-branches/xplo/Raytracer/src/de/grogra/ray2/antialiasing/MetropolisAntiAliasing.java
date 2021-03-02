package de.grogra.ray2.antialiasing;

import java.util.ArrayList;
import java.util.Random;

import net.goui.util.MTRandom;

import de.grogra.ray.util.Ray;
import de.grogra.ray2.Scene;
import de.grogra.ray2.metropolis.MetropolisStrategy;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.MetropolisRenderer;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.RayProcessorBase;
import de.grogra.ray2.tracing.MetropolisRenderer.MetropolisResult;
import de.grogra.ray2.tracing.PixelwiseRenderer.Result;
import de.grogra.vecmath.geom.Line;

public class MetropolisAntiAliasing extends NoAntialiasing {
	
	public static final String SEED_COUNT = "MetropolisPathTracer/seedCount";
	public static final String MUTATION_PP_COUNT = "MetropolisPathTracer/mutationPPCount";
	
	int width;
//	int subHeight;
	int height;
//	int seedPathCount;
	int pixelCount;
	MTRandom pathRand;
	float r1,r2;
	int maxMutationPPCount;
	int maxY =0;
	float maxYFloat =0;
	
	ArrayList<float[]> freePixelList;
	int[][] stratifiedCounterField;
	
	public void initialize (PixelwiseRenderer renderer, Scene scene)
	{
		this.renderer = renderer;
		this.scene = scene;
		initLocals ();
		
		
		pathRand = new MTRandom(renderer.getSeed());
		
//		seedPathCount = renderer.getNumericOption(SEED_COUNT, 2).intValue();
//		assert seedPathCount >=0;
//		
		processor = new MetropolisProcessor();
		((MetropolisProcessor)processor).initialize (renderer, scene);
		
		maxMutationPPCount = ((MetropolisRenderer)renderer).mutationPPCount;

	}
	
	
	public Antialiasing dup (Scene scene)
	{
//		MetropolisAntiAliasing a = (MetropolisAntiAliasing)super.dup(scene);
		
		MetropolisAntiAliasing a = new MetropolisAntiAliasing();
		a.processor = processor.dup (scene);
		a.scene = scene;
		a.renderer = renderer;
		a.initLocals ();
		
		a.width = width;
		a.height = height;
//		a.subHeight = subHeight;
//		a.seedPathCount = seedPathCount;
		a.pixelCount = pixelCount;
		a.pathRand = pathRand;
		a.maxMutationPPCount = maxMutationPPCount;
		return a;
	}
	
	
	public void resetAll(){
		((MetropolisProcessor)processor).resetAll();
	}
	
	public void setImageValues(int width, int height){
		this.width= width;
		this.height = height;
//		this.subHeight = subHeight;
		
		freePixelList = new ArrayList<float[]>();
		stratifiedCounterField = new int[width][height];
		for(int i=0; i< width; i++){
			for(int j=0; j<height; j++){
				stratifiedCounterField[i][j] =0;
				float[] newPix = {i +0.5f,j+0.5f};
				freePixelList.add(newPix);
			}
		}
		
		float pixelSize = 2.0f / width;
		
		r1= 0.1f*pixelSize;
		r2 = (float)Math.sqrt(0.05f * width*height/Math.PI);
		
		System.err.println("Metro-AA: setImgVal: width=" +this.width + " height=" +this.height/* +"  subHieght=" +this.subHeight*/);
	}
	
	
	public void initializeProccessorLoop(float brightness, int imageWidth,int imageHeight, int mutatedPixPerProc, MetropolisResult result, int procId){
		
		setImageValues(imageWidth, imageHeight);
		((MetropolisProcessor)processor).initializeLoop(brightness, imageWidth, imageHeight, mutatedPixPerProc, result, procId);
		
	}
	
	public int getProcessorMutatedPixels(){
		return ((MetropolisProcessor)processor).getMutatedPixels();
	}
	
	public void signAntialiser2Processor(){
		((MetropolisProcessor)processor).tracingMediator.setAntialiser(this);
	}


	
	
	public void stopProcessor(){
		((MetropolisProcessor)processor).stopped = true;
	}
	
	
	public Line getNewLensEdge(){
		
		
		float x = pixelCount % width +0.5f; 
		float y = pixelCount / width +0.5f;
		pixelCount++;
		if(pixelCount>=(height*width)) pixelCount=0;
//		System.err.println(x +"," +y);

		
		double pixelWidth = 2.0 / width;
		double pixelHeight = 2.0 / height;
		
		setPixelXY(x, y);
		
		return getRayByCoordinates(x * pixelWidth - 1,
					(0.5 * height - (y + 1)) * pixelWidth, pixelWidth, pixelWidth, pathRand);
	}
	
	public Line getPerturbedLensEdge(Line oldRay){
		
		float U = pathRand.nextFloat();
		
		float r = r2 * (float)Math.exp( - Math.log(r2/r1)*U);
		float angle = (float)(2f*Math.PI*pathRand.nextFloat());
		
		
		
		float x = oldRay.x;
		float y = oldRay.y;
		
		x += Math.round((Math.sin(angle)*r));
		y += Math.round((Math.cos(angle)*r));
		
		if(x<0) x = x+width;
		if(y<0) y= y+height;
		
		if(x>=width) x = x - width;
		if(y>=height) y = y - height;
		
//		System.err.println("MetroAA: getPerturbLE: Perturbed from " +oldRay.x +"," +oldRay.y +" to "  +x +"," +y +" by r=" +r);
		
		double pixelWidth = 2.0 / width;
		double pixelHeight = 2.0 / height;
		
		setPixelXY(x,y);

		x = (float)(x * pixelWidth - 1);
		y = (float)((0.5 * height - (y + 1)) * pixelWidth);
		Ray ray = list.rays[0];
		env.uv.set (x,y);

		// generate a ray origin for env.uv
		renderer.getCamera ().generateRandomOrigins (env, list, pathRand);
		tmpSpectrum.set (ray.spectrum);

		// copy generated ray origin to env
		
		env.point.set (ray.origin);
		env.globalToLocal.transform (env.point, env.localPoint);

		// generate ray direction
		renderer.getCamera ().generateRandomRays (env, null, tmpSpectrum, list, false, pathRand);
		
		Line line = ray.convert2Line();
		line.x = xPixel;
		line.y = yPixel;
		
		return line;
	}
	


	private float[] rover(){
	
		while(freePixelList.size()>0){
//		for(int i=0; i< freePixelList.size();i++){
			int i = pathRand.nextInt(freePixelList.size());
			float[] pix = freePixelList.get(i);
			float x = pix[0];
			float y = pix[1];

//			if(stratifiedCounterField[x][y]>0)System.err.println(" MetroAA: rover:  Point(" +x +";" +y +") with Quote: " +stratifiedCounterField[x][y] +" ( maxQuote=" +maxMutationPPCount+")");
			if(stratifiedCounterField[(int)x][(int)y] >= (maxMutationPPCount/2f)) {
				freePixelList.remove(i);
//				System.err.println(" MetroAA: rover:  Point(" +x +";" +y +") was removed from list");
			}
//			System.err.println(" MetroAA: rover:  returned Point(" +x +";" +y +")!");
			return pix;
		}
		return null;
	}
	
	
	
	
	public Line getStratifiedLensEdge(){
//		int x = pathRand.nextInt(width);
//		int y = pathRand.nextInt(height);
		
		float[] pix = rover();
		if(pix==null){
			System.err.println(" MetroAA: getStratifiedLensEdge:  Unsuspected stop!");
//			System.exit(0);
			return null;
		}
		float x= pix[0];
		float y= pix[1];
		
		double pixelWidth = 2.0 / width;
		double pixelHeight = 2.0 / height;
		
		setPixelXY(x, y);
		
//		maxY = (int)Math.max(y, maxY);
//		maxYFloat = Math.min((float)((0.5 * height - (maxY + 1)) * pixelHeight), maxYFloat);
////		System.err.println(" MetroAA: getStratifiedLensEdge:  Pixel=("+x+";"+y+")    val=("+(x * pixelWidth - 1)+";"+((0.5 * height - (y + 1)) * pixelHeight)+")              maxY=" +maxY +"   maxYFloat=" +maxYFloat);
//		
//		return getRayByCoordinates(x * pixelWidth - 1,
//					(0.5 * height - (y + 1)) * pixelHeight, pixelWidth, pixelHeight, pathRand);
		
		maxY = (int)Math.max(y, maxY);
		maxYFloat = Math.min((float)((0.5 * height - (maxY + 1)) * pixelWidth), maxYFloat);
//		System.err.println(" MetroAA: getStratifiedLensEdge:  Pixel=("+x+";"+y+")    val=("+(x * pixelWidth - 1)+";"+((0.5 * height - (y + 1)) * pixelWidth)+")              maxY=" +maxY +"   maxYFloat=" +maxYFloat);
		
		return getRayByCoordinates(x * pixelWidth - 1,
					(0.5 * height - (y + 1)) * pixelWidth, pixelWidth, pixelWidth, pathRand);
	}
	
	
	
	
	public Line getRayByCoordinates(double x, double y, double width,
			double height, Random random){
		
		float xDiff = random.nextFloat()-0.5f;
		float yDiff = random.nextFloat()-0.5f;
		
		Ray ray = list.rays[0];
		env.uv.set ((float) (x + width * xDiff), (float) (y + height * yDiff));

		// generate a ray origin for env.uv
		renderer.getCamera ().generateRandomOrigins (env, list, random);
		tmpSpectrum.set (ray.spectrum);

		// copy generated ray origin to env
		
		env.point.set (ray.origin);
		env.globalToLocal.transform (env.point, env.localPoint);

		// generate ray direction
		renderer.getCamera ().generateRandomRays (env, null, tmpSpectrum, list, false, random);
		
		Line line = ray.convert2Line();
		line.x = xPixel;
		line.y = yPixel;
		
		return line;
		
	}
	
	public void setPixelXY(float x, float y){
		super.setPixelXY(x, y);
//		stratifiedCounterField[(int)x][(int)y] = stratifiedCounterField[(int)x][(int)y]+1;
		
//		System.err.println(" MetroAA: setPixel: ----- Point(" +x +";" +y );//+") with Quote: " +stratifiedCounterField[x][y]);
		
	}

	
	public void registerUsedLensEdge(float x, float y){
		stratifiedCounterField[(int)x][(int)y] = stratifiedCounterField[(int)x][(int)y]+1;
	}
	
	public void pathChanged(){
		((MetropolisProcessor)processor).pathChanged();
	}

}
