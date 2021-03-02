package de.grogra.ext.x3d;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;
import javax.vecmath.Matrix4d;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.grogra.ext.x3d.io.X3DIndexedFaceSetIO;
import de.grogra.ext.x3d.io.X3DTransformIO;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.io.SceneGraphExport;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.pf.io.DOMSource;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.util.MimeType;

public class X3DExport extends SceneGraphExport implements DOMSource {
	
	/**
	 * Current export instance.
	 */
	protected static X3DExport theExport = null;
	
	/**
	 * This stack is used to remember the last written transform node.
	 * All new elements are children of the top element of this stack.
	 */
	private Stack<GroupToElement> groupToElementStack = null;
	
	/**
	 * Output file.
	 */
	protected File file = null;
	
	/**
	 * XML flavor.
	 */
	public static final IOFlavor FLAVOR
		= new IOFlavor (new MimeType ("model/vnd.x3d", null),
					IOFlavor.DOM, null);
	
	/**
	 * Current xml document.
	 */	
    protected Document doc = null;
    
    /**
     * Scene root element.
     */
    protected Element scene = null;
    
    /**
     * This collection is used to give every exported bitmap an unique name.
     */
    private HashSet<String> createdFiles = new HashSet<String>();
    
	/**
	 * Creates a new x3d export object.
	 * @param item
	 * @param source
	 */
	public X3DExport(FilterItem item, FilterSource source){
		super(item, source);
		theExport = this;
		setFlavor(FLAVOR);
	}

	@Override
	protected void beginGroup(InnerNode group) throws IOException {
//		System.out.println("beginGroup: " + group + ", isProperGroup: " + group.isProperGroup());
		Matrix4d transMatrix = new Matrix4d();
		group.get(transMatrix);
		Element transformElement = X3DTransformIO.handleTransformation(transMatrix);
		groupToElementStack.peek().getElement().appendChild(transformElement);
		groupToElementStack.push(new GroupToElement(group, transformElement));
	}

	@Override
	protected SceneTree createSceneTree(View3D scene) {
		SceneTree t = new SceneTreeWithShader (scene) {
			@Override
			protected boolean acceptLeaf (Object object, boolean asNode) {
				if (asNode)
					return getExportFor(object, asNode) != null;
				return false;
			}
			@Override
			protected Leaf createLeaf (Object object, boolean asNode, long id) {
				Leaf l = new Leaf (object, asNode, id);
				init (l);
				return l;
			}
		};
		t.createTree(false);
		return t;
	}

	@Override
	protected void endGroup(InnerNode group) throws IOException {
//		System.out.println("endGroup:" + group + ", isProperGroup: " + group.isProperGroup());
		groupToElementStack.pop();
	}

	public Document getDocument () throws IOException, DOMException {
		file = this.getMetaData(DESTINATION_FILE, null);
		DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		dbf.setValidating(false);
	    dbf.setIgnoringComments(true);
	    DocumentBuilder db;
	    try {
	    	db = dbf.newDocumentBuilder();
	    	doc = db.newDocument();
	    } catch (ParserConfigurationException e){
	    	System.out.println("Parser misconfigured: "+e);
	    	return null;
	    }

	    // set the header of the x3d file
	    Element x3delement = doc.createElement("X3D");
	    doc.appendChild(x3delement);
	    x3delement.setAttribute("profile", "Immersive");
	    x3delement.setAttribute("version", "3.0");
	    
		scene = doc.createElement("Scene");
		x3delement.appendChild(scene);
		
		// initialize stack to keep the last open group
		groupToElementStack = new Stack<GroupToElement>();
		
		// put x3dscene on stack
		groupToElementStack.add(new GroupToElement(null, scene));
		
		// call scene graph export (parse all nodes)
		write();
		
		return doc;
	}
	
	@Override
	public NodeExport getExportFor (Object object, boolean asNode) {
		Object s = getGraphState ().getObjectDefault(object, asNode, Attributes.SHAPE, null);
		if (s != null) {
			NodeExport ex = super.getExportFor (s, asNode);
			if (ex != null)	{
				return ex;
			}
			if (s instanceof Polygonizable)
			{
				return new X3DIndexedFaceSetIO();
			}
		}
		return null;
	}
	
	/**
	 * Returns the groupToElement stack.
	 * @return
	 */
	public Stack<GroupToElement> getGroupToElementStack() {
		return groupToElementStack;
	}

	/**
	 * Returns the document of the x3d exporter.
	 * @return
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * Returns the current x3d export instance.
	 * @return
	 */
	public static X3DExport getTheExport() {
		return theExport;
	}

	/**
	 * Returns the current output file.
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the current x3d scene root.
	 * @return
	 */
	public Element getScene() {
		return scene;
	}
	
	@Override
	public String getDirectory () {
		return file.getParent();
	}
	
	@Override
	public Object getFile (String name) throws IOException {
		int i = name.lastIndexOf ('.');
		String base = (i < 0) ? name : name.substring (0, i);
		String ext = (i < 0) ? "" : name.substring (i);
		i = 0;
		while (!createdFiles.add (name))		{
			name = base + ++i + ext;
		}
		String dir = getDirectory();
		File f = new File(dir + "\\" + name);
		return f;
	}
}
