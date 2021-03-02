
package de.grogra.webgl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point3d;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.MeshNode;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.math.Arc;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BezierSurface;
import de.grogra.math.Circle;
import de.grogra.math.Ellipse;
import de.grogra.math.SwungSurface;
import de.grogra.stl.STLExport;

public class Polygonizable extends ObjectBase {

	private final int P_SEGMENTS = 8;
	
	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		PolygonArray p = new PolygonArray ();
		
		// TODO why this line ?
		export.getGraphState ().setObjectContext (node.object, node.asNode);
		
		GraphState gs = export.getGraphState ();
		de.grogra.imp3d.Polygonizable surface = (de.grogra.imp3d.Polygonizable) node.getObject (Attributes.SHAPE);
		ContextDependent source = surface.getPolygonizableSource (gs);
		surface.getPolygonization ().polygonize (source, gs, p, 0, export.getMetaData (STLExport.FLATNESS, 1f));

		if(surface instanceof MeshNode) {
			Set<Point3d> points = new HashSet<Point3d>();
			for (int i = 0; i < p.vertices.size(); i+=3) {
				points.add(new Point3d(p.vertices.get(i+0), p.vertices.get(i+1), p.vertices.get(i+2)));
				
			}
			List<Point3d> pointList = new ArrayList<Point3d>();
			pointList.addAll(points);
			
			// write object
			out.println("\tvar geometry = new THREE.Geometry();");
			out.println("\tgeometry.vertices = [");
			for(Point3d pp : points) {
				out.println("\t\t new THREE.Vector3("+pp.x+", "+pp.y+", "+pp.z+"),");
			}
			out.println("\t];");
			
			out.println("\tgeometry.faces = [");
			for (int i = 0; i < p.vertices.size(); i+=9) {
				Point3d p1 = new Point3d(p.vertices.get(i+0), p.vertices.get(i+1), p.vertices.get(i+2));
				Point3d p2 = new Point3d(p.vertices.get(i+3), p.vertices.get(i+4), p.vertices.get(i+5));
				Point3d p3 = new Point3d(p.vertices.get(i+6), p.vertices.get(i+7), p.vertices.get(i+8));
				out.println("\t\t new THREE.Face3("+pointList.indexOf(p1)+", "+pointList.indexOf(p2)+", "+pointList.indexOf(p3)+"),");
			}
			out.println("\t];");
			
			out.println("\tgeometry.computeFaceNormals();");
			out.println("\tgeometry.computeVertexNormals();");
			wirteBody(node, "meshnode"+node.pathId, 0, false);
			return true;
		}


		if(surface instanceof NURBSSurface) {
			if(source instanceof SwungSurface) {
				SwungSurface swungSF = (SwungSurface)source;
				BSplineCurve profile = swungSF.getProfile();
				BSplineCurve trajecotry = swungSF.getTrajectory();
				
				if(trajecotry instanceof Circle) {
					Circle c = (Circle)trajecotry;
					float radius = c.getRadius();
					out.println("\tvar uMin = 0.001, uMax = 6.283, uRange = uMax - uMin;");
					out.println("\tvar xFunc = Parser.parse(\"cos(u)*("+1+radius+" + 1*cos(v))\").toJSFunction( ['u','v'] );");

				}
				if(trajecotry instanceof Arc) {
					Arc c = (Arc)trajecotry;
					float startAngle = c.getStartAngle();
					float endAngle = c.getEndAngle();
					float radius = c.getRadius();
					out.println("\tvar uMin = "+startAngle+", uMax = "+endAngle+", uRange = uMax - uMin;");
					out.println("\tvar xFunc = Parser.parse(\"cos(u)*("+1+radius+" + 1*cos(v))\").toJSFunction( ['u','v'] );");

				}
				if(trajecotry instanceof Ellipse) {
					Ellipse c = (Ellipse)trajecotry;
					float firstRadius = c.getFirstRadius();
					float secondRadius = c.getSecondRadius();
					out.println("\tvar uMin = 0.001, uMax = 6.283, uRange = uMax - uMin;");
					out.println("\tvar xFunc = Parser.parse(\"cos(u)*("+firstRadius+" + "+secondRadius+"*cos(v))\").toJSFunction( ['u','v'] );");

				}
				
				
				
				if(profile instanceof Circle) {
					Circle c = (Circle)profile;
					float radius = c.getRadius();
					out.println("\tvar vMin = 0.001, vMax = 6.283, vRange = vMax - vMin;");
					out.println("\tvar yFunc = Parser.parse(\"sin(u)*("+1+radius+" + 1*cos(v))\").toJSFunction( ['u','v'] );");

				}
				if(profile instanceof Arc) {
					Arc c = (Arc)profile;
					float startAngle = c.getStartAngle();
					float endAngle = c.getEndAngle();
					float radius = c.getRadius();
					out.println("\tvar vMin = "+startAngle+", vMax = "+endAngle+", vRange = vMax - vMin;");
					out.println("\tvar yFunc = Parser.parse(\"sin(u)*("+1+radius+" + 1*cos(v))\").toJSFunction( ['u','v'] );");

				}
				if(profile instanceof Ellipse) {
					Ellipse c = (Ellipse)profile;
					float firstRadius = c.getFirstRadius();
					float secondRadius = c.getSecondRadius();
					out.println("\tvar vMin = 0.001, vMax = 6.283, vRange = vMax - vMin;");
					out.println("\tvar yFunc = Parser.parse(\"sin(u)*("+firstRadius+" + "+secondRadius+"*cos(v))\").toJSFunction( ['u','v'] );");
				}
				
				
				out.println("\tvar zFunc = Parser.parse(\"1*sin(v)\").toJSFunction( ['u','v'] );");
				out.println("\tvar geometry = new THREE.ParametricGeometry( meshFunction, "+P_SEGMENTS+", "+P_SEGMENTS+", true );");
				
				wirteBody(node, "swungsurface"+node.pathId, 0, false);
				return true;
			}
			
			if(source instanceof BezierSurface) {
				BezierSurface bezierSF = (BezierSurface)source;
				float[] data = bezierSF.getData();
				out.println("\tvar controlpoints = [");
				for(int i=0;i<data.length; i+=12) {
					out.println("\t\t["
							+ "["+data[i+0]+", "+data[i+2]+", "+data[i+2]+"], "
							+ "["+data[i+3]+", "+data[i+5]+", "+data[i+4]+"], "
							+ "["+data[i+6]+", "+data[i+8]+", "+data[i+7]+"], "
							+ "["+data[i+9]+", "+data[i+11]+", "+data[i+10]+"]]"+(((i+12)<data.length)?",":""));
				}
				out.println("\t];");
				out.println("\tvar geometry = new THREE.BezierSurfaceGeometry( controlpoints, "+P_SEGMENTS+", "+P_SEGMENTS+" );");
				
				wirteBody(node, "beziersurface"+node.pathId, 0, false);
				return true;
			}
		}
		
		//else
		return false;

	}

}
