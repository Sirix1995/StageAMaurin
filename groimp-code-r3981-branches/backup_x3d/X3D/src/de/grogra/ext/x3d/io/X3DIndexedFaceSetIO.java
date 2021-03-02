package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DIndexedFaceSet;
import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.pf.io.FilterSource.MetaDataKey;
import de.grogra.util.NoSuchKeyException;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * Used to import and export an indexed face set.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DIndexedFaceSetIO extends ObjectBase {

	public final PolygonArray polygons = new PolygonArray ();
	
	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DIndexedFaceSet newIndexedFaceSet = new X3DIndexedFaceSet();
		
		String valueString;
		
		valueString = atts.getValue("ccw");
		if (valueString != null)
			newIndexedFaceSet.setCcw(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("colorIndex");
		if (valueString != null)
			newIndexedFaceSet.setColorIndex(Util.splitStringToArrayOfInt(valueString));

		valueString = atts.getValue("colorPerVertex");
		if (valueString != null)
			newIndexedFaceSet.setColorPerVertex(Boolean.valueOf(valueString));

		valueString = atts.getValue("convex");
		if (valueString != null)
			newIndexedFaceSet.setConvex(Boolean.valueOf(valueString));

		valueString = atts.getValue("coordIndex");
		if (valueString != null)
			newIndexedFaceSet.setCoordIndex(Util.splitStringToArrayOfInt(valueString));
		
		valueString = atts.getValue("creaseAngle");
		if (valueString != null)
			newIndexedFaceSet.setCreaseAngle(Float.valueOf(valueString));
		
		valueString = atts.getValue("normalIndex");
		if (valueString != null)
			newIndexedFaceSet.setNormalIndex(Util.splitStringToArrayOfInt(valueString));

		valueString = atts.getValue("normalPerVertex");
		if (valueString != null)
			newIndexedFaceSet.setNormalPerVertex(Boolean.valueOf(valueString));

		valueString = atts.getValue("solid");
		if (valueString != null)
			newIndexedFaceSet.setSolid(Boolean.valueOf(valueString));

		valueString = atts.getValue("texCoordIndex");
		if (valueString != null)
			newIndexedFaceSet.setTexCoordIndex(Util.splitStringToArrayOfInt(valueString));
		
		valueString = atts.getValue("DEF");
		newIndexedFaceSet.setDef(valueString);
		
		valueString = atts.getValue("USE");
		newIndexedFaceSet.setUse(valueString);
		
		return newIndexedFaceSet;
	}
	
	@Override
	protected void exportImpl (Leaf node, X3DExport export, Element parentElement)
		throws IOException
	{
		GraphState gs = export.getGraphState ();
		
		gs.setObjectContext (node.object, node.asNode);
		ContextDependent c = ((Polygonizable) node.getObject (de.grogra.imp3d.objects.Attributes.SHAPE))
			.getPolygonizableSource (gs);
		
		gs.setObjectContext (node.object, node.asNode);
		((Polygonizable) node.getObject
		 (de.grogra.imp3d.objects.Attributes.SHAPE)).getPolygonization ().polygonize
			(c, gs, polygons,
			 Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV,
			 export.getMetaData (new MetaDataKey<Float> ("flatness"), 1f));
		
		Element ifsElement = export.getDoc().createElement("IndexedFaceSet");
	
		// how many dimensions has a vertex, should be always 3
		int dim = polygons.dimension;
		// how many vertices has a polyon
		int ec = polygons.edgeCount;
		
		// index list for vertices, separated at ec
		IntList polyList = polygons.polygons;
		// list of float values for vertices, separated at dim
		FloatList vertList = polygons.vertices;
		// list of float values for texture coordinates, separated at 2
		FloatList uvList = polygons.uv;
		// list of float values for normals, separated at 3
		ByteList normalList = polygons.normals;
		
		// attribute for coordIndex
		StringBuilder coordIndex = new StringBuilder();
		// attribute for vertices
		StringBuilder coordPoint = new StringBuilder();
		// attribute for uv coordinates
		StringBuilder uvPoint = new StringBuilder();
		// attribute for normals
		StringBuilder normalVector = new StringBuilder();
		
		// node for vertices
		Element coordElement = export.getDoc().createElement("Coordinate");
		// vertex coordinates
		float[] coordPoints = vertList.elements;
		
		// node for texture coordinates
		Element textureCoordElement = export.getDoc().createElement("TextureCoordinate");
		// uv coordinates
		float[] uvPoints = uvList.elements;
		
		// node for normal vectors
		Element normalVectorElement = export.getDoc().createElement("Normal");
		// normal vectors
		byte[] normalVectors = normalList.elements;
		
		// if ec == 4 transfer polyList to newPolyList with ec = 3
		if (ec == 4) {
			IntList newPolyList = new IntList((polyList.size - 2) * 3); // (polyList.size - 2) * 3
			for (int i = 0; i < polyList.size; i = i + ec) {
				newPolyList.add(polyList.get(i+0));
				newPolyList.add(polyList.get(i+1));
				newPolyList.add(polyList.get(i+2));
				newPolyList.add(polyList.get(i+0));
				newPolyList.add(polyList.get(i+2));
				newPolyList.add(polyList.get(i+3));
			}
			polyList = newPolyList;
		}
		
		// identify maximum coord index value, write coord index values
		int maxIndex = 0;
		for (int i = 0; i < polyList.size; i++) {
			int ci = polyList.get(i);
			maxIndex = Math.max(ci, maxIndex);
			coordIndex.append(ci + " ");
			if ((i+1) % 3 == 0) {
				coordIndex.append("-1 ");
			}
		}
		
		// loop over polygons (polygon indices for other array lists)
		for (int i = 0; i <= maxIndex; i++) {
			// write vertices
			coordPoint.append(coordPoints[i*dim + 0] + " ");
			coordPoint.append(coordPoints[i*dim + 2] + " ");
			coordPoint.append(-coordPoints[i*dim + 1] + " ");
			
			// write uv coordinates
			uvPoint.append(uvPoints[i*2 + 0] + " ");
			uvPoint.append(uvPoints[i*2 + 1] + " ");
			
			// write normal vectors
			normalVector.append(normalVectors[i*3 + 0]/127.0f + " ");
			normalVector.append(normalVectors[i*3 + 2]/127.0f + " ");
			normalVector.append(-normalVectors[i*3 + 1]/127.0f + " ");
		}
		
		// write coordIndex
		ifsElement.setAttribute("coordIndex", coordIndex.toString());
		
		// write coordPoint
		coordElement.setAttribute("point", coordPoint.toString());
		ifsElement.appendChild(coordElement);
		
		// write uvPoint
		textureCoordElement.setAttribute("point", uvPoint.toString());
		ifsElement.appendChild(textureCoordElement);
		
		// write normalVector
		normalVectorElement.setAttribute("vector", normalVector.toString());
		ifsElement.appendChild(normalVectorElement);
		
		// write other default attributes
		ifsElement.setAttribute("colorPerVertex", "false");
		try  {
			if (node.getInt (de.grogra.imp3d.objects.Attributes.VISIBLE_SIDES) == 2)
				ifsElement.setAttribute("solid", "false");
		}
		catch (NoSuchKeyException e) {}
		
		parentElement.appendChild(ifsElement);
	}

}
