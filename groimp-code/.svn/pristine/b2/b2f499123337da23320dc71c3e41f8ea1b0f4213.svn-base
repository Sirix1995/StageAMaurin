/*
 * Copyright (C) 2020 GroIMP Developer Team
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

/**
 * @author      elsaromm
 * @version     1.0                                      
 * @since       2022.10.30
 */
package de.grogra.nurbseditor3d;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.math.BSplineSurfaceImpl;


public class InputListener implements MouseListener{
	GL gl = NURBSDisplay3D.gl;
	public static Point p;
	int size = 0;
	int pointIndex = -2;
	int curveIndex = -1;
	int surfaceIndex = -1;
	boolean rotation = false;
	int displayYRotation = NURBSDisplay3D.getRotation()[1];
	int displayZRotation = NURBSDisplay3D.getRotation()[2];

	
	@Override
	public void mouseClicked(MouseEvent click) {
		p = new Point(click.getX(), click.getY());
		Point3f point = NURBSDisplay3D.pixelToCoordinate(new Point3f(p.x, p.y, 0.0f));
		
		if(point != null){
			//Initialize new object or add controlpoint depending on RadioButton 
			String geometry = NURBSDisplay3D.getGeometry();
			if(geometry == "Curve") {
				//
				if(ObjectGeometry3D.getNURBSCurve(0) == null) {
					ObjectGeometry3D.initCurve();
				}
				ObjectGeometry3D.addCurvePoint(new Point4f(point.x, point.y, point.z, 1.0f));
			}
			else if (geometry == "Surface"){
				if(ObjectGeometry3D.getNURBSSurface(0) == null) {
					ObjectGeometry3D.initSurface();
				}
				ObjectGeometry3D.addSurfacePoint(new Point4f(point.x, point.y, point.z, 1.0f));
			}else {
				ObjectGeometry3D.initObject(NURBSDisplay3D.getGeometry(), point);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {		
	}

	@Override
	public void mousePressed(MouseEvent event) {
		ObjectGeometry3D.isMousePressed(true);
        Point press = new Point(event.getPoint());
		Point3f point = NURBSDisplay3D.pixelToCoordinate(new Point3f(press.x, press.y, 0.0f));
		displayYRotation = NURBSDisplay3D.getRotation()[1];
		displayZRotation = NURBSDisplay3D.getRotation()[2];
		Point4f tmp;
		
		
		for(int j = 0; j < ObjectGeometry3D.getCurveCounter(); j++) {
			NURBSCurve curve = ObjectGeometry3D.getNURBSCurve(j);
			if(curve != null) {
				size = ObjectGeometry3D.getCurveSize(curve);
				//compare mouse input with all curve control points
				for(int i = 0; i < size; i++){
					tmp = ObjectGeometry3D.getCurvePoint(curve, i);
					if(ObjectGeometry3D.comparePoints(point, new Point3f(tmp.x, tmp.y, tmp.z), displayYRotation, displayZRotation)) {
						pointIndex = i;
						curveIndex = j;
					}  
				}
				//check if mousepress was near center of curve
				if(size>0) {
					Point4f center = ObjectGeometry3D.getCurveCenter(curve);
					if(ObjectGeometry3D.comparePoints(point, new Point3f(center.x, center.y, center.z), displayYRotation, displayZRotation)) {
						pointIndex = -1;
						curveIndex = j;
					}
				}
			}		
		}
		
		
		for(int k = 0; k < ObjectGeometry3D.getSurfaceCounter(); k++) {
			BSplineSurfaceImpl surface = ObjectGeometry3D.getNURBSSurface(k);
			if(surface != null) {
				size = ObjectGeometry3D.getSurfaceSize(surface);
				for(int m = 0; m < size; m++){
					tmp = ObjectGeometry3D.getSurfacePoint(surface, m);
					if(ObjectGeometry3D.comparePoints(point, new Point3f(tmp.x, tmp.y, tmp.z), displayYRotation, displayZRotation)) {
						pointIndex = m;
						surfaceIndex = k;
					}  
				}
				//check if mousepress was near center of surface
				if(size>0) {
					Point4f center = ObjectGeometry3D.getSurfaceCenter(surface);
					if(ObjectGeometry3D.comparePoints(point, new Point3f(center.x, center.y, center.z), displayYRotation, displayZRotation)) {
						pointIndex = -1;
						surfaceIndex = k;
					}
				}
			}		
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		ObjectGeometry3D.isMousePressed(false);
		Point released = new Point(event.getPoint());
		Point3f point = NURBSDisplay3D.pixelToCoordinate(new Point3f(released.x, released.y, 0.0f));
		
		if(curveIndex != -1) {
			NURBSCurve curve = ObjectGeometry3D.getNURBSCurve(curveIndex);
	
			//whole curve is moved in direction of distance vector
			if(pointIndex == -1) {
		    	Point4f center = ObjectGeometry3D.getCurveCenter(curve);
		    	int size = ObjectGeometry3D.getCurveSize(curve);
		    	
				Vector3f distance = ObjectGeometry3D.calculateDistance(point, new Point3f(center.x, center.y, center.z), displayYRotation, displayZRotation);
				for(int i = 0; i < size; i++) {
					Point4f tmp = ObjectGeometry3D.getCurvePoint(curve, i);
					tmp.x += distance.x;
					tmp.y += distance.y;
					tmp.z += distance.z;
					ObjectGeometry3D.setCurvePoint(curve, tmp, i);
				}
				pointIndex = -2;
				curveIndex = -1;
			}
			
			//one control point is moved
			else if(pointIndex >= 0){
				Point4f old = ObjectGeometry3D.getCurvePoint(curve, pointIndex);
				Vector3f distance = ObjectGeometry3D.calculateDistance(point, new Point3f(old.x, old.y, old.z), displayYRotation, displayZRotation);
				Point4f newPosition = new Point4f(old.x + distance.x, old.y + distance.y, old.z + distance.z, old.w);
				ObjectGeometry3D.setCurvePoint(curve, newPosition, pointIndex);
				pointIndex = -2; 
				curveIndex = -1;
	        }
		}
		
		if(surfaceIndex != -1) {
			BSplineSurfaceImpl surface = ObjectGeometry3D.getNURBSSurface(surfaceIndex);
			
			//whole surface is moved in direction of distance vector
			if(pointIndex == -1) {
				Point4f center = ObjectGeometry3D.getSurfaceCenter(surface);

				Vector3f distance = ObjectGeometry3D.calculateDistance(point, new Point3f(center.x, center.y, center.z), displayYRotation, displayZRotation);
				for(int i = 0; i < size; i++) {
					Point4f tmp = ObjectGeometry3D.getSurfacePoint(surface, i);
					tmp.x += distance.x;
					tmp.y += distance.y;
					tmp.z += distance.z;
					ObjectGeometry3D.setSurfacePoint(surface, tmp, i);
				}
				pointIndex = -2;
				surfaceIndex = -1;
			}
			
			//one control point is moved
			else if(pointIndex >= 0){
				Point4f old = ObjectGeometry3D.getSurfacePoint(surface, pointIndex);
				Vector3f distance = ObjectGeometry3D.calculateDistance(point, new Point3f(old.x, old.y, old.z), displayYRotation, displayZRotation);
				Point4f newPosition = new Point4f(old.x + distance.x, old.y + distance.y, old.z + distance.z, old.w);
				ObjectGeometry3D.setSurfacePoint(surface, newPosition, pointIndex);
				pointIndex = -2; 
				curveIndex = -1;
			}
		}
	}
}
