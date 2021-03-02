package de.grogra.ray.antialiasing;

import java.util.Random;

import javax.vecmath.Color4f;

import de.grogra.ray.RTCamera;
import de.grogra.ray.tracing.RayProcessor;
import de.grogra.ray.util.Ray;


/**
 * This class implements a stochastical antialiasing algorithm. 
 * For every pixel frustum there will be N<sup>2</sup> generated random rays
 * that are inside the frustum. For all random rays the color value is 
 * determined and will be combined as average. 
 * 
 * To have a better distribution the original pixel is first divided into N x N 
 * subpixels. For every subpixel one random ray is generated. 
 * 
 * @author Micha
 *
 */
public class StochasticSupersampling implements Antialiasing {

	private final static int   PIXEL_DIVISION      = 4;
	private final static float PIXEL_DIVISION_1_SQ = 1.0f /(PIXEL_DIVISION*PIXEL_DIVISION);
	
	private Ray          m_ray = new Ray();
	private RTCamera     m_camera;
	private RayProcessor m_processor;
	
	private double x_offset;
	private double y_offset;
	private double x_delta;
	private double y_delta;
	
	private Random m_random = new Random(System.currentTimeMillis());
	private Color4f m_curColor = new Color4f();
		
	
	public void initialize(RTCamera camera, RayProcessor processor) {
		m_camera = camera;
		m_processor = processor;
	}

	public void getColorFromFrustum(double x, double y, double width,
			double height, Color4f color) {
	
		
		x_delta = width/PIXEL_DIVISION;
		y_delta = height/PIXEL_DIVISION;
		y_offset = y;
		for (int j=0; j<PIXEL_DIVISION; j++) {
			x_offset = x;
			for (int i=0; i<PIXEL_DIVISION; i++) {
				m_camera.getRayFromCoordinates(
						x_offset+m_random.nextFloat()*x_delta,
						y_offset+m_random.nextFloat()*y_delta,m_ray);
				m_processor.getColorFromRay(m_ray,m_curColor);
				if ((i==0) && (j==0)) {
					color.set(m_curColor);
				} else {
					color.add(m_curColor);
				}
				x_offset += x_delta;
			}
			y_offset += y_delta;
		}
		color.scale(PIXEL_DIVISION_1_SQ);		
	}

}

