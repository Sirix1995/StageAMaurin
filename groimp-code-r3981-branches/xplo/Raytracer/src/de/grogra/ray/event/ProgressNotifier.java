package de.grogra.ray.event;

import java.util.Vector;

public class ProgressNotifier {

	private Vector m_listeners = new Vector();
	
	
	public void addProgressListener(RTProgressListener listener) { 
		m_listeners.add(listener); 
	}
	
	
	public void removeProgressListener(RTProgressListener listener) { 
		m_listeners.remove(listener); 
	}
	
	
	protected void fire_progressChanged(int type, double progress, String text, 
			int x, int y, int width, int height) {
		for (int i=0;i<m_listeners.size();i++) {
			((RTProgressListener)m_listeners.elementAt(i)).progressChanged(
					type,progress,text,x,y,width,height);
		}
	}

}
