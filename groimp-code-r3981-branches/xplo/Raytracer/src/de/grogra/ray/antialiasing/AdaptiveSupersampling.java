 package de.grogra.ray.antialiasing;

import javax.vecmath.Color4f;

import de.grogra.ray.RTCamera;
import de.grogra.ray.memory.MemoryPool;
import de.grogra.ray.tracing.RayProcessor;
import de.grogra.ray.util.Ray;


/**
 * The adaptive supersampling algorithm is a nonuniform supersampling 
 * algorithm. Only if a supersampling for a pixel is reasonable the 
 * pixel is divided into subpixel.
 * 
 * The following algorithm is used to perform the division:
 * The color values of the four corners and the center of a pixel are 
 * determined. If the difference of two color values exceeds a given limit
 * the pixel is recursively divided into subpixels until a depth limit 
 * of the recursion is achieved.
 * 
 * @author Micha
 *
 */
public class AdaptiveSupersampling implements Antialiasing {

	private final static boolean USE_MEMORY_POOL = true;
	
	private final boolean m_showDivisions    = false;
	private final boolean m_cachingIsEnabled = true;
		
	private double m_limit = 1.0;
	private int    m_depthLimit = 3;
	
	private Ray          m_ray = new Ray();
	private RTCamera     m_camera;
	private RayProcessor m_processor;
	private MemoryPool   m_memoryPool;
	
	private int    m_calculations = 0;
	private int    m_maxCalculations = 0;
	
	private float[][]   m_memoX;
	private float[][]   m_memoY;
	private float[][]   m_memoZ;
	private float[][]   m_memoW;
	private boolean[][] m_memoIsSet;
	private int         m_memoWidth = 0;
	private int         m_memoHeight = 0;
	
	private boolean m_allocated = false;
	private double  m_currentY = -1.0;
	private double  m_scaleX;
	private double  m_scaleY;
	
	
	public void initialize(RTCamera camera, RayProcessor processor) {
		m_camera = camera;
		m_processor = processor;
		m_memoryPool = MemoryPool.getPool();
		m_maxCalculations = 
			m_depthLimit*m_depthLimit + (m_depthLimit+1)*(m_depthLimit+1);
	}
	
	
	public void getColorFromFrustum(double x, double y, double width,
			double height, Color4f color) {
		
		if ((m_processor==null) || (m_camera==null)) { 
			color.set(0.0f,0.0f,0.0f,0.0f);
			return; 
		}
		
		if ((!m_allocated) && m_cachingIsEnabled) {
			allocateMemory(width,height);
		}
		
		if ((m_currentY!=y) && m_cachingIsEnabled) {
			refreshMemory(y);
		}		
				
		m_calculations = 0;
		
		getSubpixelColor(1,x,y,width,height,color);
		
		if (m_showDivisions) {
			float gray = (float)m_calculations/(float)m_maxCalculations;
			color.set(gray,gray,gray,1.0f);
		}
		
	}
	
	
	private void getSubpixelColor(int depth, double x, double y, double width, double height, Color4f color) {
		
		Color4f color_A;
		Color4f color_B;
		Color4f color_C;
		Color4f color_D;
		Color4f color_E;
		if (USE_MEMORY_POOL) {
			color_A = m_memoryPool.newColor4f();
			color_B = m_memoryPool.newColor4f();
			color_C = m_memoryPool.newColor4f();
			color_D = m_memoryPool.newColor4f();
			color_E = m_memoryPool.newColor4f();
		} else {
			color_A = new Color4f();
			color_B = new Color4f();
			color_C = new Color4f();
			color_D = new Color4f();
			color_E = new Color4f();
		}
		
		if (m_cachingIsEnabled) {
			getCachedColor(depth,x,y,color_A);
			getCachedColor(depth,x+width,y,color_B);
			getCachedColor(depth,x+width,y+height,color_C);
			getCachedColor(depth,x,y+height,color_D);
			getCachedColor(depth+1,x+width*0.5,y+height*0.5,color_E);
		} else {
			m_camera.getRayFromCoordinates(x,y,m_ray);
			m_processor.getColorFromRay(m_ray,color_A);
			m_camera.getRayFromCoordinates(x+width,y,m_ray);
			m_processor.getColorFromRay(m_ray,color_B);
			m_camera.getRayFromCoordinates(x+width,y+height,m_ray);
			m_processor.getColorFromRay(m_ray,color_C);
			m_camera.getRayFromCoordinates(x,y+height,m_ray);
			m_processor.getColorFromRay(m_ray,color_D);
			m_camera.getRayFromCoordinates(x+width*0.5,y+height*0.5,m_ray);
			m_processor.getColorFromRay(m_ray,color_E);
			m_calculations += 5;
		}
		
		if ((color_A.w==0.0f)&&(color_B.w==0.0f)&&
			(color_C.w==0.0f)&&(color_D.w==0.0f)&&
			(color_E.w==0.0f)) {
			color.set(0.0f,0.0f,0.0f,0.0f);
			if (USE_MEMORY_POOL) {
				m_memoryPool.freeColor4f(color_E);
				m_memoryPool.freeColor4f(color_D);
				m_memoryPool.freeColor4f(color_C);
				m_memoryPool.freeColor4f(color_B);
				m_memoryPool.freeColor4f(color_A);
			}
			return;
		}
				
		Color4f color_subA;
		Color4f color_subB;
		Color4f color_subC;
		Color4f color_subD;
		if (USE_MEMORY_POOL) {
			color_subA = m_memoryPool.newColor4f();
			color_subB = m_memoryPool.newColor4f();
			color_subC = m_memoryPool.newColor4f();
			color_subD = m_memoryPool.newColor4f();
		} else {
			color_subA = new Color4f();
			color_subB = new Color4f();
			color_subC = new Color4f();
			color_subD = new Color4f();
		}
				
		
		// subpixel A
		if ((getColorDifference(color_E,color_A)>m_limit) && (depth<m_depthLimit)) {
			getSubpixelColor(depth+1,x,y,width*0.5,height*0.5,color_subA);
		} else {
			// subA = (A+E)/2
			if ((color_A.w==0.0f)&&(color_E.w==0.0f)) {
				color_subA.set(0.0f,0.0f,0.0f,0.0f);
			} else {
				color_subA.set(color_E);
				color_subA.add(color_A);
				color_subA.scale(0.5f);
			}
		}
		
		// subpixel B
		if ((getColorDifference(color_E,color_B)>m_limit) && (depth<m_depthLimit)) {
			getSubpixelColor(depth+1,x+width*0.5,y,width*0.5,height*0.5,color_subB);
		} else {
			// subB = (B+E)/2
			if ((color_B.w==0.0f)&&(color_E.w==0.0f)) {
				color_subB.set(0.0f,0.0f,0.0f,0.0f);
			} else {
				color_subB.set(color_E);
				color_subB.add(color_B);
				color_subB.scale(0.5f);
			}
		}
		
		// subpixel C
		if ((getColorDifference(color_E,color_C)>m_limit) && (depth<m_depthLimit)) {
			getSubpixelColor(depth+1,x+width*0.5,y+height*0.5,width*0.5,height*0.5,color_subC);
		} else {
			// subC = (C+E)/2
			if ((color_C.w==0.0f)&&(color_E.w==0.0f)) {
				color_subC.set(0.0f,0.0f,0.0f,0.0f);
			} else {
				color_subC.set(color_E);
				color_subC.add(color_C);
				color_subC.scale(0.5f);
			}
		}
		
		// subpixel D
		if ((getColorDifference(color_E,color_D)>m_limit) && (depth<m_depthLimit)) {
			getSubpixelColor(depth+1,x,y+height*0.5,width*0.5,height*0.5,color_subD);
		} else {
			// subD = (D+E)/2
			if ((color_D.w==0.0f)&&(color_E.w==0.0f)) {
				color_subD.set(0.0f,0.0f,0.0f,0.0f);
			} else {
				color_subD.set(color_E);
				color_subD.add(color_D);
				color_subD.scale(0.5f);
			}
		}
		
		color.set(color_subA);	
		color.add(color_subB);
		color.add(color_subC);
		color.add(color_subD);
		color.scale(0.25f);
						
		// clean up
		if (USE_MEMORY_POOL) {
			m_memoryPool.freeColor4f(color_subD);
			m_memoryPool.freeColor4f(color_subC);
			m_memoryPool.freeColor4f(color_subB);
			m_memoryPool.freeColor4f(color_subA);
			m_memoryPool.freeColor4f(color_E);
			m_memoryPool.freeColor4f(color_D);
			m_memoryPool.freeColor4f(color_C);
			m_memoryPool.freeColor4f(color_B);
			m_memoryPool.freeColor4f(color_A);
		}

	}
	
	
	private double getColorDifference(Color4f color1, Color4f color2) {
		double diff = 0.0;
		diff += Math.abs(color1.x-color2.x);
		diff += Math.abs(color1.y-color2.y);
		diff += Math.abs(color1.z-color2.z);
		diff += 3.0*Math.abs(color1.w-color2.w);
		return diff;
	}
	
	
	private void allocateMemory(double pixel_width, double pixel_height) {
		
		int subpixel = (1 << (m_depthLimit-1));
		int width = (int)Math.round(2.0 / pixel_width);
		int height = (int)Math.round(2.0 / pixel_height);
		m_memoWidth  = width*subpixel+1;
		m_memoHeight = subpixel+1;
		
		m_memoX = new float[m_memoWidth][m_memoHeight];
		m_memoY = new float[m_memoWidth][m_memoHeight];
		m_memoZ = new float[m_memoWidth][m_memoHeight];
		m_memoW = new float[m_memoWidth][m_memoHeight];
		m_memoIsSet = new boolean[m_memoWidth][m_memoHeight];
		for (int y=0;y<m_memoHeight;y++) {
			for (int x=0;x<m_memoWidth;x++) {
				m_memoIsSet[x][y] = false;
			}
		}
		m_scaleX = (subpixel) / 2.0 * width;
		m_scaleY = (subpixel) / 2.0 * height;

		m_allocated = true;
	}
	
	
	private void refreshMemory(double yValue) {
		
		for (int y=1;y<m_memoHeight-1;y++) {
			for (int x=0;x<m_memoWidth;x++) {
				m_memoIsSet[x][y] = false;
			}
		}
		
		// copy values from bottom to top
		for (int x=0;x<m_memoWidth;x++) {
			if (m_memoIsSet[x][m_memoHeight-1]) {
				m_memoIsSet[x][0] = true;
				m_memoX[x][0] = m_memoX[x][m_memoHeight-1];
				m_memoY[x][0] = m_memoY[x][m_memoHeight-1];
				m_memoZ[x][0] = m_memoZ[x][m_memoHeight-1];
				m_memoW[x][0] = m_memoW[x][m_memoHeight-1];
				m_memoIsSet[x][m_memoHeight-1] = false;
			} else {
				m_memoIsSet[x][0] = false;
			}
		}
		
		m_currentY = yValue;
	}
	
	
	private void getCachedColor(int depth, double x, double y, Color4f color) {
		
		if (depth>m_depthLimit) {
			m_camera.getRayFromCoordinates(x,y,m_ray);
			m_processor.getColorFromRay(m_ray,color);
			return;
		}
		
		int x_index = (int)((x+1.0)*m_scaleX+0.0000001);
		int y_index = (int)((y-m_currentY)*m_scaleY+0.0000001);
		
		if (m_memoIsSet[x_index][y_index]) {
			color.x = m_memoX[x_index][y_index];
			color.y = m_memoY[x_index][y_index];
			color.z = m_memoZ[x_index][y_index];
			color.w = m_memoW[x_index][y_index];
		} else {
			m_camera.getRayFromCoordinates(x,y,m_ray);
			m_processor.getColorFromRay(m_ray,color);
			m_calculations++;
			m_memoX[x_index][y_index] = color.x;
			m_memoY[x_index][y_index] = color.y;
			m_memoZ[x_index][y_index] = color.z;
			m_memoW[x_index][y_index] = color.w;
			m_memoIsSet[x_index][y_index] = true;
		}
		
	}

}
