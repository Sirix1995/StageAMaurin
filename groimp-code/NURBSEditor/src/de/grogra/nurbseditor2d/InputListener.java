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
package de.grogra.nurbseditor2d;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLJPanel;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;

import de.grogra.imp3d.objects.NURBSCurve;


public class InputListener implements MouseListener{
	GL gl = NURBSDisplay2D.gl;
	public static Point p;
	int size = 0;
	int pointIndex = -2;
	int curveIndex = 0;
	boolean rotation = false;
	GLJPanel panel;	
	
	public InputListener(GLJPanel panel) {
		this.panel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent click) {
		p = new Point(click.getX(), click.getY());
		Point2f point = NURBSDisplay2D.pixelToCoordinate(p);
		
		//add new Object or control point depending on RadioButton
		if(point != null) {
			String geometry = NURBSDisplay2D.getGeometry();
			if(geometry == "Curve") {
				if(ObjectGeometry2D.getNURBSCurve(0) == null) {
					ObjectGeometry2D.initCurve();
				}
				ObjectGeometry2D.addPoint(new Point3f(point.x, point.y, 1));
			}else {
				ObjectGeometry2D.initObject(NURBSDisplay2D.getGeometry(), point);
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
		ObjectGeometry2D.isMousePressed(true);
		Point2f point = NURBSDisplay2D.pixelToCoordinate(new Point(event.getPoint()));
		Point3f tmp;
		float s = 0.2f; 
		
		//compare input with every point of every curve
		for(int j = 0; j < ObjectGeometry2D.getCurveCounter(); j++) {
			NURBSCurve curve = ObjectGeometry2D.getNURBSCurve(j);			
			if(curve != null) {
				size = ObjectGeometry2D.getSize(curve);
				
				//check if MouseEvent was near control point
				for(int i = 0; i < size; i++){
					tmp = ObjectGeometry2D.getPoint(curve, i);
				    if(point.x > (tmp.x - s) && point.x < (tmp.x + s)){
				    	if(point.y > (tmp.y - s) && point.y < (tmp.y + s)){
				    		pointIndex = i;
				    		curveIndex = j;
				        }
			    	}      
			    } 
				//check if MouseEvent was near center of curve
				if(size>0) {
					Point3f center = ObjectGeometry2D.getCenter(curve);
					if(point.x > (center.x - s) && point.x < (center.x + s)){
						if(point.y > (center.y - s) && point.y < (center.y + s)){
							pointIndex = -1;
							curveIndex = j;
						}
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		ObjectGeometry2D.isMousePressed(false);

		Point2f point = NURBSDisplay2D.pixelToCoordinate(new Point(event.getPoint()));
		NURBSCurve curve = ObjectGeometry2D.getNURBSCurve(curveIndex);
		
		//whole curve is moved in direction of distance vector
		if(pointIndex == -1) {
			Vector2f distance = new Vector2f(point.x - ObjectGeometry2D.getCenter(curve).x , point.y - ObjectGeometry2D.getCenter(curve).y);
			for(int i = 0; i < size; i++) {
				Point3f tmp = ObjectGeometry2D.getPoint(curve, i);
				tmp.x += distance.x;
				tmp.y += distance.y;
				ObjectGeometry2D.setPoint(curve, tmp, i);
			}
			pointIndex = -2;
		}
		//one control point is moved
		else if(pointIndex >= 0){        
			ObjectGeometry2D.setPoint(curve, new Point3f(point.x, point.y, (ObjectGeometry2D.getPoint(curve, pointIndex)).z), pointIndex);
            pointIndex = -2;       
        }
	}
}