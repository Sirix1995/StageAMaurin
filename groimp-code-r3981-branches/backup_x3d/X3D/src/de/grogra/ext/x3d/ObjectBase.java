package de.grogra.ext.x3d;

import java.io.IOException;
import javax.vecmath.Matrix4d;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.io.X3DTransformIO;
import de.grogra.ext.x3d.objects.X3DAppearance;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.io.SceneGraphExport;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Implements export instructions for GroIMP nodes.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public abstract class ObjectBase implements SceneGraphExport.NodeExport {
	
	/**
	 * Does common import tasks.
	 * @param sceneChild
	 * @return
	 */
	public Node doImport(Attributes atts) {
		return doImportImpl(atts);
	}
	
	/**
	 * Import implementation for every x3d node class.
	 * @param sceneChild Element to import.
	 * @param appearance Appearance node of shape.
	 * @return GroIMP scene graph node.
	 */
	protected abstract Node doImportImpl(Attributes atts);
	
	public void export(Leaf node, InnerNode transform, SceneGraphExport sge) throws IOException {
		X3DExport export = (X3DExport) sge;
		
		Element parentElement = export.getGroupToElementStack().peek().getElement();
		
		// is transformation given create a x3d transform element only for this node (no inheritation)
		if (transform != null) {
			Matrix4d transMatrix = new Matrix4d();
			transform.get(transMatrix);
			Element transformElement = X3DTransformIO.handleTransformation(transMatrix);
			parentElement.appendChild(transformElement);
			parentElement = transformElement;				
		}	
		
		// if groimp node is an axis node then correct the center of it
		if (this instanceof AxisObjectBase) {
			Matrix4d transMatrix = new Matrix4d();
			transMatrix.setIdentity();
			Element transformElement = X3DTransformIO.handleTransformation(transMatrix,
					true, (Node) node.object, sge.getGraphState());
			parentElement.appendChild(transformElement);
			parentElement = transformElement;
		}
		
		if ((node instanceof SceneTreeWithShader.Leaf) &&
				(((SceneTreeWithShader.Leaf)node).shader != null) &&
				(!(node.object instanceof LightNode))) {
			// if node is a geometry node with shader
		
			// create shape
			Element shapeElement = export.getDoc().createElement("Shape");
			
			// create appearance and material
			Element appearanceElement = null;

			appearanceElement = X3DAppearance.handleAppearance(node.object,
					((SceneTreeWithShader.Leaf)node).shader, export, sge.getGraphState());

			shapeElement.appendChild(appearanceElement);
			
			parentElement.appendChild(shapeElement);
			
			exportImpl(node, export, shapeElement);
		}
		else {
			// node is something else (like light)
			exportImpl(node, export, parentElement);
		}
		
		
	}
	
	/**
	 * Export implementation for every node class.
	 * @param node Leaf nod to export.
	 * @param export Exporter.
	 * @param parentElement X3D element on which to add the new element. Already a shape element.
	 * @throws IOException
	 */
	protected abstract void exportImpl(Leaf node, X3DExport export, Element parentElement) throws IOException;

	
	//Hier k�nnte z.B. Implementierung zum Schreiben eines Polygons oder sowas als static-Methode eingebaut werden

}
