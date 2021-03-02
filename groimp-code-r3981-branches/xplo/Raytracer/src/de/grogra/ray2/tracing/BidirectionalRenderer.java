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

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.ray2.antialiasing.Antialiasing;
import de.grogra.ray2.antialiasing.NoAntialiasing;
import de.grogra.ray2.tracing.PixelwiseRenderer.Result;
import de.grogra.ray2.tracing.modular.CausticMap;
import de.grogra.ray2.tracing.modular.CausticMap.CausticElement;
import de.grogra.xl.util.IntList;

public class BidirectionalRenderer extends PixelwiseRenderer {
	
	CausticMap globalCausticMap;
	
	
	public void render (Scene scene, Sensor camera,
			Matrix4d cameraTransformation, int width, int height,
			ImageObserver obs)
	{
		long startTime = System.currentTimeMillis ();
		this.originalScene = scene;
		this.camera = camera;
		this.cameraTransformation = cameraTransformation;

		antialiasing.initialize (this, scene);

		this.width = width;
		this.height = height;

		changedPixels=0;
		maxColor = new Color4f();
		minColor = new Color4f(10,10,10,10);
		globalCausticMap = new CausticMap(width,height);
		
		lineState = new int[height];

		if (hdr)
		{
			hdrPixels = new float[4][width * height];
			DataBufferFloat buffer = new DataBufferFloat (hdrPixels, hdrPixels[0].length);
			BandedSampleModel sampleModel = new BandedSampleModel (buffer.getDataType (), width, height, 4);
			WritableRaster raster = Raster.createWritableRaster (sampleModel, buffer, null);
			ColorSpace cs = ColorSpace.getInstance (ColorSpace.CS_sRGB);
			ComponentColorModel cm = new ComponentColorModel (cs, true, false, Transparency.TRANSLUCENT, buffer.getDataType ());
			image = new BufferedImage (cm, raster, false, null);
		}
		else
		{
			image = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
			rgbaPixels = ((DataBufferInt) image.getRaster ().getDataBuffer ()).getData ();
		}
		observer = obs;
		if (threadCount < 2)
		{
			addSolver (createLocalSolver (true));
		}
		else
		{
			for (int i = Math.min (32, threadCount); i > 0; i--)
			{
				addSolver (createLocalSolver (false));
			}
		}
		if (monitor != null)
		{
			monitor.setProgress (Resources.msg ("renderer.rendering",
				new Float (0)), 0);
		}
		solve ();
		removeSolvers ();
		if (isStopped ())
		{
			observer.imageUpdate (image, ImageObserver.ABORT
				| ImageObserver.ERROR, 0, 0, width, height);
			if (monitor != null)
			{
				monitor.setProgress (Resources.msg ("renderer.stopped"),
					ProgressMonitor.DONE_PROGRESS);
			}
		}
		else
		{
			mergeCaustic2Image();
			if (hdr)
			{
				if (removeOutliers)
				{
					removeOutliers ();
				}
				if (autoAdjust && (maxValue > 0))
				{
					float f = autoAdjustMaxValue / maxValue;
					for (int s = 0; s < 3; s++)
					{
						float[] a = hdrPixels[s];
						for (int i = a.length - 1; i >= 0; i--)
						{
							a[i] *= f;
						}
					}
				}
			}
			long time = System.currentTimeMillis () - startTime;
			observer.imageUpdate (image, ImageObserver.ALLBITS, 0, 0, width,
				height);
			if (monitor != null)
			{
				monitor.setProgress (Resources.msg ("renderer.done"),
					ProgressMonitor.DONE_PROGRESS);

				StringBuffer stats = new StringBuffer ("<html><pre>");
				stats.append (Resources.msg ("raytracer.statistics",
					new Object[] {width, height, threadCount,
							(int) (time / 60000),
							(time % 60000) * 0.001f}));

				stats.append ("    Count of changed Pixels: " +changedPixels+"\n");
				stats.append ("    maxColor " +maxColor+"\n");
				stats.append ("    minColor " +minColor+"\n");
				
				originalScene.appendStatistics (stats);

				antialiasing.appendStatistics (stats);
				monitor
					.showMessage (stats.append ("</pre></html>").toString ());
			}
		}
	}
	

	protected void renderLines (Antialiasing antialiasing, IntList lines,
			Result res)
	{
			
		CausticMap localCausticMap = new CausticMap(this.width,this.height);
		((BiDirectionalProcessor)((NoAntialiasing)antialiasing).processor).loacalCausticMap = localCausticMap;
		
		
		super.renderLines(antialiasing, lines, res);
		
		mergeCausticMap(localCausticMap);
	}
	
	
	public synchronized void mergeCausticMap (CausticMap localMap)
	{
		for(int i=0; i< localMap.causticMap.length; i++){
			for(int j=0; j<localMap.causticMap[i].length; j++ ){
				
				globalCausticMap.causticMap[i][j].causticCounter += localMap.causticMap[i][j].causticCounter;
				globalCausticMap.causticMap[i][j].nonCausticCounter += localMap.causticMap[i][j].nonCausticCounter;
				if(localMap.causticMap[i][j].color == null) continue;
				if(globalCausticMap.causticMap[i][j].color==null) globalCausticMap.causticMap[i][j].color = new Color4f(localMap.causticMap[i][j].color);
				else globalCausticMap.causticMap[i][j].color.add(localMap.causticMap[i][j].color);
			}
		}
		
	}
	
	public void mergeCaustic2Image(){
		
		int pos =0;
		for(int i=0; i< globalCausticMap.causticMap.length; i++){
			for(int j=0; j<globalCausticMap.causticMap[i].length; j++ ){
				CausticElement caustic = globalCausticMap.causticMap[i][j];
//				System.err.println("  BidiRenderre: mergeCaustic2Image:   width= " +width +" height="+height +"  i="+i+ " j="+j  +"    --> pos=" +pos);
				if(caustic.color!=null){
					
					caustic.color.scale(1f/caustic.causticCounter);
					float w = caustic.color.w;
//					float mult = brightness / Math.max (w, 1e-3f);
//					caustic.color.scale(mult);
//					caustic.color.w = w;
					
					if (hdr)
					{
						
						
						float last_bright = brightness / Math.max (hdrPixels[3][pos], 1e-3f);

						hdrPixels[0][pos] *=  1f/last_bright;
						hdrPixels[1][pos] *=  1f/last_bright;
						hdrPixels[2][pos] *=  1f/last_bright;
							
						
						float bright = brightness / Math.max (hdrPixels[3][pos], 1e-3f);
						
						hdrPixels[0][pos] +=caustic.color.x ;
						hdrPixels[1][pos] +=caustic.color.y;
						hdrPixels[2][pos] +=caustic.color.z;
//						hdrPixels[3][pos] +=caustic.color.w;

						
//						hdrPixels[0][pos] = (hdrPixels[0][pos]*caustic.nonCausticCounter + caustic.color.x*caustic.causticCounter)
//											/((float)(caustic.causticCounter + caustic.nonCausticCounter));
//						hdrPixels[1][pos] = (hdrPixels[1][pos]*caustic.nonCausticCounter + caustic.color.y*caustic.causticCounter)
//											/((float)(caustic.causticCounter + caustic.nonCausticCounter));
//						hdrPixels[2][pos] = (hdrPixels[2][pos]*caustic.nonCausticCounter + caustic.color.z*caustic.causticCounter)
//											/((float)(caustic.causticCounter + caustic.nonCausticCounter));
//						
						hdrPixels[0][pos] *= bright;
						hdrPixels[1][pos] *= bright;
						hdrPixels[2][pos] *= bright;
						

////						hdrPixels[3][pos] = (hdrPixels[3][pos]*caustic.nonCausticCounter + caustic.color.w*caustic.causticCounter)
////											/((float)(caustic.causticCounter + caustic.nonCausticCounter));
//						hdrPixels[3][pos] = Math.min(hdrPixels[3][pos], w);
						
//						System.err.print("BidiRendere:  mergeCaustic2Image:  x="+j +" y="+i +"\n   hdr before =(" 
//								+hdrPixels[0][pos] +";" +hdrPixels[1][pos] +";" +hdrPixels[2][pos] +";" +hdrPixels[3][pos]
//								+"\n   caustic=" +caustic.color +"\n   noncausticCount=" +caustic.nonCausticCounter +"  caustCounter= " +caustic.causticCounter +"\n");
//						
						if(autoAdjust){
							float t = hdrPixels[3][pos];
							if (t * hdrPixels[0][pos] > maxValue)
							{
								maxValue = t * hdrPixels[0][pos];
							}
							if (t * hdrPixels[1][pos] > maxValue)
							{
								maxValue = t * hdrPixels[1][pos];
							}
							if (t * hdrPixels[2][pos] > maxValue)
							{
								maxValue = t * hdrPixels[2][pos];
							}
						}
						
						if(luminance(hdrPixels[0][pos],hdrPixels[1][pos],hdrPixels[2][pos]) > 
						luminance(maxColor.x, maxColor.y, maxColor.z)){
						maxColor.x = hdrPixels[0][pos];
						maxColor.y = hdrPixels[1][pos];
						maxColor.z = hdrPixels[2][pos];
						maxColor.w = hdrPixels[3][pos];
					}
					if(luminance(hdrPixels[0][pos],hdrPixels[1][pos],hdrPixels[2][pos]) < 
							luminance(minColor.x, minColor.y, minColor.z)){
						minColor.x = hdrPixels[0][pos];
						minColor.y = hdrPixels[1][pos];
						minColor.z = hdrPixels[2][pos];
						minColor.w = hdrPixels[3][pos];
					}
					}
					else
					{
						
						float[] cols = toRGBA(rgbaPixels[pos]);
						float r = (cols[0]*caustic.nonCausticCounter + caustic.color.x)
								/((float)(caustic.causticCounter + caustic.nonCausticCounter));
						float g = (cols[1]*caustic.nonCausticCounter + caustic.color.y)
								/((float)(caustic.causticCounter + caustic.nonCausticCounter));
						float b = (cols[2]*caustic.nonCausticCounter + caustic.color.z)
								/((float)(caustic.causticCounter + caustic.nonCausticCounter));
						float a = (cols[3]*caustic.nonCausticCounter + caustic.color.w)
								/((float)(caustic.causticCounter + caustic.nonCausticCounter));
						rgbaPixels[pos] = toIntColor (r,g,b,a);
					}
				}
				pos++;	
				
//				if(caustic.color==null) {
//					caustic.color = new Color4f(0,0,0,0);
//					caustic.causticCounter = 1;
//				}
//				if (hdr)
//					{
//						hdrPixels[0][pos] = caustic.color.x /((float)(caustic.causticCounter));
//						hdrPixels[1][pos] = caustic.color.y /((float)(caustic.causticCounter));
//						hdrPixels[2][pos] = caustic.color.z /((float)(caustic.causticCounter));
//						hdrPixels[3][pos] = caustic.color.w /((float)(caustic.causticCounter));
//											
//				}else{
//						
//						
//						float r = caustic.color.x /((float)(caustic.causticCounter));
//						float g = caustic.color.y /((float)(caustic.causticCounter));
//						float b = caustic.color.z /((float)(caustic.causticCounter));
//						float a = caustic.color.w /((float)(caustic.causticCounter));
//						rgbaPixels[pos] = toIntColor (r,g,b,a);
//				}
//				pos++;
				
			}
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
		
//		System.err.println("MetroRend: getPixelForLine2Vert: after   ret= (" +ret[0] +";"+ret[1]+")");
		return ret;
	}
	
}
