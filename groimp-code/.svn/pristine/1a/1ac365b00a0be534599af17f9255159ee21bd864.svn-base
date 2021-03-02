
package de.grogra.tex;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Polygonizable extends ObjectBase {

	@Override
	void exportImpl (Leaf node, InnerNode transform, TEXExport export)
			throws IOException
	{
		Point3d pos = new Point3d ();
		Matrix4d m = getTransformation ();
//		m.transform (pos);
		float flatness = export.getMetaData (TEXExport.FLATNESS, 1f);
		int flags = 0;

		PolygonArray p = new PolygonArray ();
		
		// TODO why this line ?
		export.getGraphState ().setObjectContext (node.object, node.asNode);
		
		GraphState gs = export.getGraphState ();
		de.grogra.imp3d.Polygonizable surface = (de.grogra.imp3d.Polygonizable) node.getObject (Attributes.SHAPE);
		ContextDependent source = surface.getPolygonizableSource (gs);
		surface.getPolygonization ().polygonize (source, gs, p, flags, flatness);

		// transform the mesh
		for (int i = 0; i < p.vertices.size(); i+=3) {
			pos.x = p.vertices.get(i+0);
			pos.y = p.vertices.get(i+1);
			pos.z = p.vertices.get(i+2);
			m.transform(pos);
			p.vertices.set(i+0, (float)pos.x);
			p.vertices.set(i+1, (float)pos.y);
			p.vertices.set(i+2, (float)pos.z);
		}

		// write object
		mesh2(p, node, false);
	}

}
