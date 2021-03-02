package de.grogra.ray.quality;

import java.util.HashMap;

public class Quality {

	private static Quality m_quality = null;
	
	private final HashMap m_timerMap = new HashMap();
	
	private Quality() {}
	
	
	public static Quality getQualityKit() {
		if (m_quality == null) {
			m_quality = new Quality();
		}
		return m_quality;
	}
	
	
	public Timer getTimer(Object key) {
		if (m_timerMap.containsKey(key)) {
			return (Timer)m_timerMap.get(key);
		} else {
			Timer new_timer = new Timer();
			m_timerMap.put(key,new_timer);
			return new_timer;
		}
	}
	
}
