package de.grogra.imp3d.ray;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import de.grogra.ray.RTResources;
import de.grogra.ray.event.RTProgressListener;
import de.grogra.pf.ui.Workbench;


public class GroIMPRTProgressListener implements RTProgressListener {

	private Workbench m_workbench;
	
	private BufferedImage m_image         = null;
	private ImageObserver m_imageObserver = null;
	
	public GroIMPRTProgressListener(Workbench workbench) {
		m_workbench = workbench;
	}
	
	
	public void setImage(BufferedImage image, ImageObserver observer) {
		m_image = image;
		m_imageObserver = observer;
	}
	
	
	public void beginProgress() {
		m_workbench.beginStatus(this);
	}
	
	
	public void endProgress() {
		m_workbench.clearStatusAndProgress(this);
	}
	
	
	public void progressChanged(int type, double progress, String text,
			int x, int y, int width, int height) {
		String status_text;
		switch (type) {
		case RTProgressListener.RENDERING_PREPROCESSING:
			status_text = RTResources.getString("de.grogra.ray.progress.raytracer.preprocessing")+
					" - " + text;
			m_workbench.setStatus (this, status_text, (float)progress);
			break;
		case RTProgressListener.RENDERING_PROCESSING:
			status_text = RTResources.getString("de.grogra.ray.progress.raytracer.processing")+
					" - " + text;
			if ((m_image!=null)&&(m_imageObserver!=null)) {
				m_imageObserver.imageUpdate(m_image, ImageObserver.SOMEBITS,
						x,y,width,height);
				if (progress==1.0) {
					m_imageObserver.imageUpdate(m_image, ImageObserver.ALLBITS,
							0,0,m_image.getWidth(),m_image.getHeight());
				}
			}
			m_workbench.setStatus (this, status_text, (float)progress);
			break;
		case RTProgressListener.RENDERING_POSTPROCESSING:
			status_text = RTResources.getString("de.grogra.ray.progress.raytracer.postprocessing")+
					" - " + text;
			m_workbench.setStatus (this, status_text, (float)progress);
			break;	
		}
		
	}

}
