package de.grogra.imp3d;

import javax.media.opengl.GL;
import de.grogra.imp3d.HalfEdgeStructCSG;

public interface CSGable {
	
	public boolean usedInCSG();
	
	public HalfEdgeStructCSG getMesh();
	
	public boolean isActive();
	
}
