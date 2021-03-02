package de.grogra.ray;

import de.grogra.ray.event.RTProgressListener;
import de.grogra.ray.quality.Quality;
import de.grogra.ray.quality.Timer;
import de.grogra.ray.tracing.PhotonMapping;

public class PhotonMapRaytracer extends Raytracer {
	private int m_imageUpdateDistance = 0;
	private int photonCount = 0;
	private int range = 0;
	private static int old_stamp = -1;
	private static int old_photonCount = -1;
	private static int old_raytracingDepth = -1;
	
	public PhotonMapRaytracer() {
		super();
	}

	protected void prepareRaytracing(RTScene scene, RTCamera camera) {
		m_imageUpdateDistance = photonCount / 100;
		Timer preparing_timer = Quality.getQualityKit().getTimer(Raytracer.PREPARING_TIMER);
		preparing_timer.reset();
		
		// prepare raytracing
		fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING, 0.0,
				RTResources.getString("de.grogra.ray.progress.raytracer.preprocessing.prepareIntersection"),
				0, 0, 0, 0);
		
		preparing_timer.start();
		m_intersectionProcessor.prepareProcessing(scene);
		preparing_timer.stop();
		
		fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING, 0.1,
				RTResources.getString("de.grogra.ray.progress.raytracer.preprocessing.prepareRaytracing"),
				0, 0, 0, 0);
		
		preparing_timer.start();
		//m_rayProcessor.setIntersectionProcessor(m_intersectionProcessor);
		if (!m_rayProcessor.hasFixedLightProcessor()) {
			m_rayProcessor.setLightProcessor(m_lightProcessor);
		}		
		m_rayProcessor.setRecursionDepth(m_raytracingDepth);
		((PhotonMapping)m_rayProcessor).setPhotonCount(photonCount);
		((PhotonMapping)m_rayProcessor).setRang(range);
		m_rayProcessor.prepareRayProcessor(scene, m_intersectionProcessor);
		
		// create Photonen Map if scene or photoncount changed 
		// FEHLER: beim laden einer neuen Szene wird keine neue Photonmap erzeugt
		if((old_stamp != scene.getStamp()) || (old_photonCount != photonCount) 
			|| (old_raytracingDepth != m_raytracingDepth))
		{
			((PhotonMapping)m_rayProcessor).clear();
			for(int i = 0; i < photonCount; i++) {
				if (Thread.interrupted ()) { return; }
				
				((PhotonMapping)m_rayProcessor).createPhotonMap();

				if(i % m_imageUpdateDistance == 0) {
					//System.out.println(super.percentToString((double)i/photonCount*100.0));
					fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING, (double)i / photonCount,
							RTResources.getString("de.grogra.ray.progress.raytracer.preprocessing.prepareRaytracing") +
							" - " + super.percentToString((double)i / photonCount * 100.0),
							0, 0, 0, 0);
				}
			}
			old_stamp = scene.getStamp();
			old_photonCount = photonCount;
			old_raytracingDepth = m_raytracingDepth;
		}
		
		m_antialising.initialize(camera, m_rayProcessor);
		preparing_timer.stop();
		
		fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING, 1.0,
				RTResources.getString("de.grogra.ray.progress.done"),
				0, 0, 0, 0);
	}
	
	
	public void setPhotonCount(int photonCount) {
		this.photonCount = photonCount;
	}
	
	public void setRange(int range) {
		this.range = range;
	}
}