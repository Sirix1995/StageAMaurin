

/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d.ray;

import java.awt.image.*;
import de.grogra.util.*;
import de.grogra.pf.ui.*;
import de.grogra.imp3d.*;
import de.grogra.ray.PhotonMapRaytracer;
import de.grogra.ray.RTCamera;
import de.grogra.ray.Raytracer;
import de.grogra.reflect.Type;


public class GroIMPRaytracer implements Runnable //, SceneTree.Visitor
{

	public static final Type ANTIALIASING_MODE
		= new EnumerationType ("ray.antialiasing", IMP3D.I18N, 3);
	public static final Type INTERSECTION_MODE
		= new EnumerationType ("ray.intersection", IMP3D.I18N, 2);
	public static final Type RAYTRACING_MODE
		= new EnumerationType ("ray.raytracing", IMP3D.I18N, 5);
	public static final Type PRIORITY_MODE
		= new EnumerationType ("ray.priority", IMP3D.I18N, 3);
	
	
	private GroIMPSceneGraph       m_sceneGraph;
	private RTCamera               m_camera;
	private GroIMPRTProgressListener m_progressNotifier;
	private Raytracer              default_raytracer;
	private PhotonMapRaytracer     photon_raytracer;
	private Raytracer              m_raytracer = default_raytracer;
	private BufferedImage          m_image;
	private Map                    m_params;


	public GroIMPRaytracer (Workbench wb, Map params, Tree tree, int width, int height,
					  ImageObserver out, Camera camera)
	{	
		m_sceneGraph = new GroIMPSceneGraph(tree);
		m_params = params;
		m_camera = new GroIMPCamera(camera,width*1.0/height);
		m_image = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
		m_progressNotifier = new GroIMPRTProgressListener(wb);
		m_progressNotifier.setImage(m_image,out);
	}

	
	public void run ()
	{
		configurateRaytracer();
		
		m_progressNotifier.beginProgress();
		m_raytracer.renderScene(m_sceneGraph,m_camera,m_image);
		m_progressNotifier.endProgress();
		m_raytracer.printStatistics();
		
	}
	
	
	private void configurateRaytracer() {

		// raytracing
		switch (Utils.getInt(m_params, "raytracing")) {
		case 0:
			m_raytracer = new Raytracer();;
			m_raytracer.setRaytracingPolicy(Raytracer.CONVENTIONAL_RAYTRACING);
			break;
		case 1:
			m_raytracer = new Raytracer();
			m_raytracer.setRaytracingPolicy(Raytracer.PATHTRACING_MT);
			m_raytracer.setPathtracingPathCount(Utils.getInt(m_params, "pathtracing/count"));
			break;
		case 2:
			m_raytracer = new Raytracer();;
			m_raytracer.setRaytracingPolicy(Raytracer.PATHTRACING_HS);
			break;
		case 3:
			m_raytracer = new Raytracer();;
			m_raytracer.setRaytracingPolicy(Raytracer.BIDIRECTIONAL_PATHTRACING);
			break;
		case 4:
			photon_raytracer  = new PhotonMapRaytracer();
			photon_raytracer.setPhotonCount(Utils.getInt(m_params, "photon/count"));
			photon_raytracer.setRange(Utils.getInt(m_params, "photon/range"));
			m_raytracer = photon_raytracer;
			m_raytracer.setRaytracingPolicy(Raytracer.PHOTONMAPPING);
			break;
		default:
			m_raytracer = new Raytracer();
			m_raytracer.setRaytracingPolicy(Raytracer.CONVENTIONAL_RAYTRACING);
		}
		
		m_raytracer.addProgressListener(m_progressNotifier);
		
		if (Utils.getBoolean(m_params, "debug/debugRaytracer")) {
			m_raytracer.enableDebugPixel(
					Utils.getInt(m_params, "debug/x"),
					Utils.getInt(m_params, "debug/y"),
					Utils.getBoolean(m_params, "debug/markPixel"));
		} else {
			m_raytracer.disableDebugPixel();
		}
		
		m_raytracer.setBrightnessScaleFactor(
				Utils.getFloat(m_params, "brightness/scaleFactor"));

		// antialiasing
		switch (Utils.getInt(m_params, "antialiasing")) {
		case 0:
			m_raytracer.setAntialisingPolicy(Raytracer.NO_ANTIALISING);
			break;
		case 1:
			m_raytracer.setAntialisingPolicy(Raytracer.STOCHASTIC_SUPERSAMPLING);
			break;
		case 2:
			m_raytracer.setAntialisingPolicy(Raytracer.ADAPTIVE_SUPERSAMPLING);
			break;
		default:
			m_raytracer.setAntialisingPolicy(Raytracer.NO_ANTIALISING);
		}
		
		// intersection
		switch (Utils.getInt(m_params, "intersection")) {
		case 0:
			m_raytracer.setIntersectionPolicy(Raytracer.NAIVE_INTERSECTION);
			break;
		case 1:
			m_raytracer.setIntersectionPolicy(Raytracer.OCTREE_INTERSECTION);
			break;
		default:
			m_raytracer.setIntersectionPolicy(Raytracer.OCTREE_INTERSECTION);
		}
		
		// raytracing_depth
		m_raytracer.setRaytracingDepth(Utils.getInt(m_params, "raytracing_depth"));
		
		// priority
		switch (Utils.getInt(m_params, "priority")) {
		case 0:
			m_raytracer.setPriority(Raytracer.LOW_PRIORITY);
			break;
		case 1:
			m_raytracer.setPriority(Raytracer.MEDIUM_PRIORITY);
			break;
		case 2:
			m_raytracer.setPriority(Raytracer.HIGH_PRIORITY);
			break;
		default:
			m_raytracer.setPriority(Raytracer.HIGH_PRIORITY);
		}
		
	}
	
	
	
//	private void configurateRaytracer_Hack() {		
//		m_raytracer = new Raytracer();
//		m_raytracer.addProgressListener(m_progressNotifier);
//		m_raytracer.setRaytracingDepth(1);
//		
//		m_raytracer.setRaytracingPolicy(Raytracer.PATHTRACING_MT);
//		m_raytracer.setAntialisingPolicy(Raytracer.NO_ANTIALISING);
//		m_raytracer.setIntersectionPolicy(Raytracer.NAIVE_INTERSECTION);
//		m_raytracer.setPriority(Raytracer.HIGH_PRIORITY);
//	}


}
