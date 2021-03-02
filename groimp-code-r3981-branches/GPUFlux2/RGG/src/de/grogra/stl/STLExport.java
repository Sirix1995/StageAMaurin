
package de.grogra.stl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import javax.vecmath.Matrix4d;

import de.grogra.imp3d.View3D;
import de.grogra.imp3d.io.SceneGraphExport;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.MeshNode;
import de.grogra.imp3d.objects.SceneTree;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.pf.io.FileWriterSource;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.ui.Workbench;


/**
 * A simple exporter for ASCII-STL files.
 * 
 * http://en.wikipedia.org/wiki/STL_(file_format)
 * 
 * MH 2015-04-28
 */

public class STLExport extends SceneGraphExport implements FileWriterSource {

	public static final MetaDataKey<Float> FLATNESS = new MetaDataKey<Float> ("flatness");

	private Workbench workbench = null;

	protected PrintWriter out;

	final Stack<Matrix4d> matrixStack = new Stack<Matrix4d>();


	public STLExport(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(item.getOutputFlavor()); // IOflavor retrieved from filter item each time
		
		// put an initial identity transform into the matrixStack
		Matrix4d m = new Matrix4d();
		m.setIdentity();
		matrixStack.push(m);
		
		workbench = Workbench.current();
	}


	@Override
	protected void beginGroup (InnerNode group) throws IOException {
		// push new transformation matrix onto matrix stack
		Matrix4d m = matrixStack.peek();
		Matrix4d n = new Matrix4d();
		group.transform(m, n);
		matrixStack.push(n);
	}

	@Override
	protected SceneTree createSceneTree (View3D scene) {
		SceneTree t = new SceneTreeWithShader (scene) {

			@Override
			protected boolean acceptLeaf(Object object, boolean asNode) {
				return getExportFor(object, asNode) != null;
			}

			@Override
			protected Leaf createLeaf(Object object, boolean asNode, long id) {
				Leaf l = new Leaf (object, asNode, id);
				init(l);
				return l;
			}
		};
		t.createTree(true);
		return t;
	}

	@Override
	protected void endGroup (InnerNode group) throws IOException {
		// remove transformation matrix from matrix stack
		matrixStack.pop();
	}

	@Override
	public void write (File file) throws IOException {
		workbench.beginStatus(this);
		workbench.setStatus(this, "Export STL", -1);
		
		FileWriter fw = new FileWriter (file);
		BufferedWriter br = new BufferedWriter (fw);
		out = new PrintWriter(br);

		// write contents of the graph
		write ();

		out.close ();
		br.close ();
		fw.close ();
		
		workbench.clearStatusAndProgress(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public NodeExport getExportFor (Object object, boolean asNode)
	{
		Object s = getGraphState().getObjectDefault(object, asNode, Attributes.SHAPE, null);
		if (s == null) return null;
		NodeExport ex = super.getExportFor (s, asNode);
		if (ex != null) return ex;
		if (s instanceof MeshNode) {
			return new IndexedFaceSetExport();
		}
		return null;
	}
	


	public void increaseProgress() {
		workbench.setIndeterminateProgress(this);
	}

}