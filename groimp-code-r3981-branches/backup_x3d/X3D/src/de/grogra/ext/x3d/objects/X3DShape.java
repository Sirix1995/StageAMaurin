package de.grogra.ext.x3d.objects;

import org.xml.sax.Attributes;

import de.grogra.ext.x3d.interfaces.Definable;
import de.grogra.graph.impl.Node;

/**
 * This class saves the geometry node and the appearance object to
 * a shape element. It is used to link appearance to a node during
 * the import.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DShape implements Definable {

	/**
	 * The appearance object.
	 */
	protected X3DAppearance x3dAppearance = null;
	
	/**
	 * The geometry node (a groimp node).
	 */
	protected Node x3dGeometry = null;
	
	protected String def = null;
	protected String use = null;
	
	/**
	 * Constructor.
	 */
	public X3DShape() {
		super();
	}
	
	public X3DAppearance getX3dAppearance() {
		return x3dAppearance;
	}
	
	public void setAppearance(X3DAppearance appearance) {
		x3dAppearance = appearance;
	}
	
	public Node getX3dGeometry() {
		return x3dGeometry;
	}
	
	public void setGeometryNode(Node geometry) {
		this.x3dGeometry = geometry;
	}

	public String getDef() {
		return def;
	}

	public String getUse() {
		return use;
	}
	
	public static X3DShape createInstance(Attributes atts) {
		X3DShape newShape = new X3DShape();
		
		String valueString;
		
		valueString = atts.getValue("DEF");
		newShape.def = valueString;
		
		valueString = atts.getValue("USE");
		newShape.use = valueString;
		
		return newShape;
	}
		
}
