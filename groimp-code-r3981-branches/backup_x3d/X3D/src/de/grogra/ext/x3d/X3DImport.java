package de.grogra.ext.x3d;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import de.grogra.ext.x3d.interfaces.Appearance;
import de.grogra.ext.x3d.interfaces.Color;
import de.grogra.ext.x3d.interfaces.Coordinate;
import de.grogra.ext.x3d.interfaces.FontStyle;
import de.grogra.ext.x3d.interfaces.Normal;
import de.grogra.ext.x3d.interfaces.TextureCoordinate;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.ext.x3d.io.*;
import de.grogra.ext.x3d.objects.*;
import de.grogra.graph.EdgePatternImpl;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.XMLReaderBase;

public class X3DImport extends XMLReaderBase {

	/**
	 * Current import instance.
	 */
	protected static X3DImport theImport = null;
	
	/**
	 */
	protected X3DScene sceneNode = null;
	
	/**
	 * Url of input file.
	 */
	protected URL url = null;

	/**
	 * X3D parser states.
	 */
	public final static int OUTSIDE_SCENE = 0;
	public final static int PARSE_SCENE = 1;
	public final static int PROTO_DECLARE = 2;
	
	protected int parserState = OUTSIDE_SCENE;
	
	protected int nodesToIgnore = 0;
		
	/**
	 * This list is used to remember the last written transform node,
	 * all new node are children of the top element
	 */
	protected LinkedList<Node> transformList = null;
	
	/**
	 * This list is used to remember the last written shape node.
	 * Geometry and appearance nodes are set to top element.
	 * After reading all elements shape nodes are handled.
	 */
	protected LinkedList<X3DShape> shapeList = null;
	
	/**
	 * This list is used to remember which nodes need an extra call
	 * of the setValues() method.
	 */
	protected LinkedList<Value> valueList = null;
	
	/**
	 * Class used for cloning sub graphs.
	 * ref: Name of USE attribute in current x3d node.
	 * parent: GroIMP node at which the cloned sub graph is to add.
	 *
	 */
	private class ReferenceToParent {
		public String ref = null;
		public Node parent = null;
		public ReferenceToParent(String ref, Node parent) {
			this.ref = ref;
			this.parent = parent;
		}
	}
	
	/**
	 * This list is used to remember the x3d nodes which are cloned.
	 * Used with DEF/USE and only for nodes, which can be cloned completely
	 * (like Lights, Shapes, Transforms, but not GeometryNodes).
	 */
	protected LinkedList<ReferenceToParent> duplicateList = null;
	
	/**
	 * This list is used to remember x3d nodes which shall be cloned.
	 * Used with DEF/USE and for all nodes which can not be cloned completely
	 * (like GeometryNodes, Material, Coordinates).
	 */
	protected HashMap<String, Object> referenceMap = null;
	
	protected HashMap<String, Object> protoMap = null;
	
	/**
	 * Constructor of the x3d importer. Makes some preprocessing tasks.
	 * @param item
	 * @param source
	 */
	public X3DImport(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(IOFlavor.valueOf(Node.class));
		
		theImport = this;
		
		sceneNode = new X3DScene();
		
		File file = new File(source.getSystemId());
		try {
			url = file.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		transformList = new LinkedList<Node>();
		transformList.add(sceneNode);
		shapeList = new LinkedList<X3DShape>();
		valueList = new LinkedList<Value>();
		duplicateList = new LinkedList<ReferenceToParent>();
		referenceMap = new HashMap<String, Object>();
	}
	
	@Override
	protected Object getObjectImpl() throws IOException {
		return sceneNode;
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		if (parserState == OUTSIDE_SCENE) {
			// not yet in body of x3d file
			if (name.equals("Scene")) {
				// found node "Scene", so at next node the graph has to be parsed
				parserState |= PARSE_SCENE;
			}
			return;
		}
		
		if (nodesToIgnore > 0) {
			// we are in an undefined node or in a USE declaration,
			// so don't handle the node and it's children
			nodesToIgnore++;
			return;
		}
		
		// parse x3d nodes
		if (name.equals("ProtoDeclare") || ((parserState & PROTO_DECLARE) == PROTO_DECLARE)) {
			parserState |= PROTO_DECLARE;
			handleProtoDeclare(uri, localName, name, atts);
		}		
		else if ((name.equals("Transform")) || (name.equals("Group"))) {
			if (atts.getValue("USE") == null) {
				// no USE, so create new node in ordinary way
				X3DTransform node = (X3DTransform) new X3DTransformIO().doImport(atts);
				transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
				transformList.add(node);
				valueList.add(node);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				addToDuplicateList(atts);
			}
		}
		else if (name.equals("Shape")) {
			if (atts.getValue("USE") == null) {
				X3DShape shape = X3DShape.createInstance(atts);
				shapeList.add(shape);
			}
			else {
				addToDuplicateList(atts);
			}
		}
		else if (name.equals("Appearance")) {
			X3DAppearance appearance = null;
			if (atts.getValue("USE") == null) {
				appearance = X3DAppearance.createInstance(atts);
			}
			else {
				appearance = (X3DAppearance) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			shapeList.getLast().setAppearance(appearance);
		}
		else if (name.equals("Material")) {
			X3DMaterial material = null;
			if (atts.getValue("USE") == null) {
				material = X3DMaterial.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), material);
				}
			}
			else {
				material = (X3DMaterial) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			shapeList.getLast().getX3dAppearance().setMaterial(material);
		}
		else if (name.equals("ImageTexture")) {
			X3DImageTexture imageTexture = null;
			if (atts.getValue("USE") == null) {
				imageTexture = X3DImageTexture.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), imageTexture);
				}
			}
			else {
				imageTexture = (X3DImageTexture) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			shapeList.getLast().getX3dAppearance().setTexture(imageTexture);
		}
		else if (name.equals("TextureTransform")) {
			X3DTextureTransform textureTransform = null;
			if (atts.getValue("USE") == null) {
				textureTransform = X3DTextureTransform.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), textureTransform);
				}
			}
			else {
				textureTransform = (X3DTextureTransform) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			shapeList.getLast().getX3dAppearance().setTextureTransform(textureTransform);
		}
		else if (name.equals("Box")) {
			X3DBox node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DBox) new X3DBoxIO().doImport(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DBox) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("Sphere")) {
			X3DSphere node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DSphere) new X3DSphereIO().doImport(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DSphere) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("Cylinder")) {
			X3DCylinder node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DCylinder) new X3DCylinderIO().doImport(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DCylinder) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("Cone")) {
			X3DCone node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DCone) new X3DConeIO().doImport(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DCone) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("IndexedFaceSet")) {
			X3DIndexedFaceSet node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DIndexedFaceSet) new X3DIndexedFaceSetIO().doImport(atts);
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DIndexedFaceSet) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("ElevationGrid")) {
			X3DElevationGrid node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DElevationGrid) new X3DElevationGridIO().doImport(atts);
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DElevationGrid) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("Extrusion")) {
			X3DExtrusion node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DExtrusion) new X3DExtrusionIO().doImport(atts);
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DExtrusion) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("PointSet")) {
			X3DPointSet node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DPointSet) new X3DPointSetIO().doImport(atts);
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DPointSet) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("IndexedLineSet")) {
			X3DIndexedLineSet node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DIndexedLineSet) new X3DIndexedLineSetIO().doImport(atts);
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DIndexedLineSet) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("Text")) {
			X3DText node = null;
			if (atts.getValue("USE") == null) {
				node = (X3DText) new X3DTextIO().doImport(atts);
			}
			else {
				try {
					Node oldNode = (Node) referenceMap.get(atts.getValue("USE"));
					node = (X3DText) oldNode.clone(true);
					nodesToIgnore++;
				} catch (CloneNotSupportedException e) {e.printStackTrace();}
			}
			shapeList.getLast().setGeometryNode(node);
			transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
			valueList.add(node);
		}
		else if (name.equals("FontStyle")) {
			X3DFontStyle fontStyle = null;
			if (atts.getValue("USE") == null) {
				fontStyle = X3DFontStyle.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), fontStyle);
				}
			}
			else {
				fontStyle = (X3DFontStyle) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			((FontStyle) shapeList.getLast().getX3dGeometry()).setFontStyle(fontStyle);
		}
		else if (name.equals("Color")) {
			X3DColor color = null;
			if (atts.getValue("USE") == null) {
				color = X3DColor.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), color);
				}
			}
			else {
				color = (X3DColor) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			((Color) shapeList.getLast().getX3dGeometry()).setColor(color);
		}
		else if (name.equals("Coordinate")) {
			X3DCoordinate coord = null;
			if (atts.getValue("USE") == null) {
				coord = X3DCoordinate.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), coord);
				}
			}
			else {
				coord = (X3DCoordinate) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			((Coordinate) shapeList.getLast().getX3dGeometry()).setCoord(coord);
		}
		else if (name.equals("Normal")) {
			X3DNormal normal = null;
			if (atts.getValue("USE") == null) {
				normal = X3DNormal.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), normal);
				}
			}
			else {
				normal = (X3DNormal) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			((Normal) shapeList.getLast().getX3dGeometry()).setNormal(normal);
		}
		else if (name.equals("TextureCoordinate")) {
			X3DTextureCoordinate texCoord = null;
			if (atts.getValue("USE") == null) {
				texCoord = X3DTextureCoordinate.createInstance(atts);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), texCoord);
				}
			}
			else {
				texCoord = (X3DTextureCoordinate) referenceMap.get(atts.getValue("USE"));
				nodesToIgnore++;
			}
			((TextureCoordinate) shapeList.getLast().getX3dGeometry()).setTexCoord(texCoord);
		}
		else if (name.equals("DirectionalLight")) {
			if (atts.getValue("USE") == null) {
				X3DDirectionalLight node = (X3DDirectionalLight) new X3DDirectionalLightIO().doImport(atts);
				transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
				valueList.add(node);
				valueList.add(node);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				addToDuplicateList(atts);
			}
		}
		else if (name.equals("PointLight")) {
			if (atts.getValue("USE") == null) {
				X3DPointLight node = (X3DPointLight) new X3DPointLightIO().doImport(atts);
				transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
				valueList.add(node);
				valueList.add(node);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				addToDuplicateList(atts);
			}
		}
		else if (name.equals("SpotLight")) {
			if (atts.getValue("USE") == null) {
				X3DSpotLight node = (X3DSpotLight) new X3DSpotLightIO().doImport(atts);
				transformList.getLast().addEdgeBitsTo(node, GraphManager.BRANCH_EDGE, null);
				valueList.add(node);
				valueList.add(node);
				if (atts.getValue("DEF") != null) {
					referenceMap.put(atts.getValue("DEF"), node);
				}
			}
			else {
				addToDuplicateList(atts);
			}
		}
		else {
			// found undefined node, so don't handle it and it's children
			nodesToIgnore++;
		}
	}
	
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if ((parserState & PARSE_SCENE) == PARSE_SCENE) {
			if (nodesToIgnore > 0) {
				// in a undefined node
				nodesToIgnore--;
				return;
			}
			
			// parse x3d nodes
			if ((parserState & PROTO_DECLARE) == PROTO_DECLARE) {
				if (name.equals("ProtoDeclare")) {
					parserState &= ~PROTO_DECLARE;
				}
				handleProtoDeclareEnd(uri, localName, name);
			}
			else if ((name.equals("Transform")) || (name.equals("Group"))) {
				transformList.removeLast();
			}
			else if (name.equals("Scene")) {
				parserState = OUTSIDE_SCENE;
			}
			else if (name.equals("Shape")) {
				if ((shapeList.getLast().getDef() != null) && (shapeList.getLast().getUse() == null))
					referenceMap.put(shapeList.getLast().getDef(), shapeList.getLast().getX3dGeometry());
			}
			else if (name.equals("Appearance")) {
				if ((((X3DAppearance) shapeList.getLast().getX3dAppearance()).getDef() != null)
						&& (((X3DAppearance) shapeList.getLast().getX3dAppearance()).getUse() == null)) {
					referenceMap.put(((X3DAppearance) shapeList.getLast().getX3dAppearance()).getDef(),
							shapeList.getLast().getX3dAppearance());
				}
			}
			else if (name.equals("PointSet")) {
				if ((((X3DPointSet) shapeList.getLast().getX3dGeometry()).getDef() != null)
						&& (((X3DPointSet) shapeList.getLast().getX3dGeometry()).getUse() == null)) {
					referenceMap.put(((X3DPointSet) shapeList.getLast().getX3dGeometry()).getDef(),
							shapeList.getLast().getX3dGeometry());
				}
			}
			else if (name.equals("IndexedLineSet")) {
				if ((((X3DIndexedLineSet) shapeList.getLast().getX3dGeometry()).getDef() != null)
						&& (((X3DIndexedLineSet) shapeList.getLast().getX3dGeometry()).getUse() == null)) {
					referenceMap.put(((X3DIndexedLineSet) shapeList.getLast().getX3dGeometry()).getDef(),
							shapeList.getLast().getX3dGeometry());
				}
			}
			else if (name.equals("Text")) {
				if ((((X3DText) shapeList.getLast().getX3dGeometry()).getDef() != null)
						&& (((X3DText) shapeList.getLast().getX3dGeometry()).getUse() == null)) {
					referenceMap.put(((X3DText) shapeList.getLast().getX3dGeometry()).getDef(),
							shapeList.getLast().getX3dGeometry());
				}
			}
			else if (name.equals("IndexedFaceSet")) {
				if ((((X3DIndexedFaceSet) shapeList.getLast().getX3dGeometry()).getDef() != null)
						&& (((X3DIndexedFaceSet) shapeList.getLast().getX3dGeometry()).getUse() == null)) {
					referenceMap.put(((X3DIndexedFaceSet) shapeList.getLast().getX3dGeometry()).getDef(),
							shapeList.getLast().getX3dGeometry());
				}
			}
			else if (name.equals("ElevationGrid")) {
				if ((((X3DElevationGrid) shapeList.getLast().getX3dGeometry()).getDef() != null)
						&& (((X3DElevationGrid) shapeList.getLast().getX3dGeometry()).getUse() == null)) {
					referenceMap.put(((X3DElevationGrid) shapeList.getLast().getX3dGeometry()).getDef(),
							shapeList.getLast().getX3dGeometry());
				}
			}
			else if (name.equals("Extrusion")) {
				if ((((X3DExtrusion) shapeList.getLast().getX3dGeometry()).getDef() != null)
						&& (((X3DExtrusion) shapeList.getLast().getX3dGeometry()).getUse() == null)) {
					referenceMap.put(((X3DExtrusion) shapeList.getLast().getX3dGeometry()).getDef(),
							shapeList.getLast().getX3dGeometry());
				}
			}
			
		}
		
	}
	
	public void startDocument() {
	}
	
	public void endDocument() {
		// apply materials
		X3DShape shape = null;
		while (shapeList.size() > 0) {
			shape = shapeList.removeFirst();

			X3DAppearance.applyMaterial((ShadedNull) shape.getX3dGeometry(), shape.getX3dAppearance());

			if (shape.getX3dGeometry() instanceof Appearance) {
				((Appearance) shape.getX3dGeometry()).setAppearance(shape.getX3dAppearance());
			}
		}
		
		// apply values to value nodes (AFTER material is assigned!)
		Value value = null;
		while (valueList.size() > 0) {
			value = valueList.removeLast();
			value.setValues();
		}
		
		// clone sub graphs
		while (duplicateList.size() > 0) {
			ReferenceToParent rtp = duplicateList.remove();
			String ref = rtp.ref;
			Node node = (Node) referenceMap.get(ref);
			Node parent = rtp.parent;
			try {
				Node newNode = node.cloneGraph(EdgePatternImpl.TREE, true);
				parent.addEdgeBitsTo(newNode, GraphManager.BRANCH_EDGE, null);
			} catch (CloneNotSupportedException e) {e.printStackTrace();}
		}
		
	}
	
	protected void addToDuplicateList(Attributes atts) {
		duplicateList.add(new ReferenceToParent(atts.getValue("USE"), transformList.getLast()));
		nodesToIgnore++;		
	}

	
	protected void handleProtoDeclare(String uri, String localName, String name, Attributes atts) {
		if (name.equals("ProtoDeclare")) {
			
//			protoMap.put(atts.getValue("name"), )
		}
	}
	
	protected void handleProtoDeclareEnd(String uri, String localName, String name) {
		
	}
	
	
	
	/**
	 * Returns the url of the x3d file to import.
	 * @return
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Returns the instance of the current x3d importer.
	 * @return
	 */
	public static X3DImport getTheImport() {
		return theImport;
	}
	
}
