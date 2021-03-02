package de.grogra.stl;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.pf.io.FilterSource.MetaDataKey;

public class IndexedFaceSetExport extends ObjectBase {

	public final PolygonArray polygons = new PolygonArray ();

	@Override
	void exportImpl(Leaf node, InnerNode transform, STLExport export) throws IOException {
		GraphState gs = export.getGraphState ();
		
		gs.setObjectContext (node.object, node.asNode);
		ContextDependent c = ((Polygonizable) node.getObject (de.grogra.imp3d.objects.Attributes.SHAPE)).getPolygonizableSource (gs);
		
		gs.setObjectContext (node.object, node.asNode);
		((Polygonizable) node.getObject(de.grogra.imp3d.objects.Attributes.SHAPE)).getPolygonization().polygonize(c, gs, polygons, 
			Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV, export.getMetaData (new MetaDataKey<Float> ("flatness"), 1f));

		Matrix4d m = export.matrixStack.peek();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		m = n;
		
		Point4d[] p = new Point4d[polygons.vertices.size/polygons.dimension];
		for(int i=0; i<p.length; i++) {
			p[i] = new Point4d (
					polygons.vertices.get(i*polygons.dimension+0), 
					polygons.vertices.get(i*polygons.dimension+1), 
					polygons.vertices.get(i*polygons.dimension+2), 1);
		}
		
		for (int i = 0; i < p.length; i++) {
			m.transform (p[i]);
		}

		// write object
		out.println("solid Mesh"+node.pathId);
		for (int i = 0; i < p.length/polygons.dimension; i++) {
			writeFace(new Vector3d(p[i*polygons.dimension].x,p[i*polygons.dimension].y,p[i*polygons.dimension].z),
					  new Vector3d(p[i*polygons.dimension+1].x,p[i*polygons.dimension+1].y,p[i*polygons.dimension+1].z),
					  new Vector3d(p[i*polygons.dimension+2].x,p[i*polygons.dimension+2].y,p[i*polygons.dimension+2].z));
		}
		out.println("endsolid Mesh"+node.pathId);
	}


}
