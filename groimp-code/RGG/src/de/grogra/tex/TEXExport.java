
package de.grogra.tex;

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
import de.grogra.imp3d.objects.SceneTree;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.pf.io.FileWriterSource;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.ui.Workbench;


/**
 * A simple exporter for LaTeX/ TikZ files.
 * 
 * http://en.wikipedia.org/wiki/PLY_(file_format)
 * 
 * MH 2020-01-22
 */

public class TEXExport extends SceneGraphExport implements FileWriterSource {

	public static final MetaDataKey<Float> FLATNESS = new MetaDataKey<Float> ("flatness");

	private Workbench workbench = null;

	protected StringBuffer outV = new StringBuffer();
	protected StringBuffer outF = new StringBuffer();

	final Stack<Matrix4d> matrixStack = new Stack<Matrix4d>();

	protected int primitives = 0;
	protected int vertices = 0;
	protected int facets = 0;

	public TEXExport(FilterItem item, FilterSource source) {
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
		workbench.setStatus(this, "Export TEX", -1);

		//collect data from the graph
		write ();

		FileWriter fw = new FileWriter (file);
		BufferedWriter br = new BufferedWriter (fw);
		PrintWriter out = new PrintWriter(br);
		
		// write header
		out.println("%LaTeX/TikZ export by GroIMP");
		out.println("%Vertices and facets of "+primitives+" primitives exported\n%");
		out.println("%compile with lualatex for small files. If you got memory problems, what will happen very fast, user:\n");
		out.println("%lualatex --enable-write18 --extra-mem-bot=15000000 --synctex=1 filename.tex");
		out.println("%where you may replace 15000000 by another ridiculous high number.\n%");
		out.println("\\documentclass[tikz,crop=true,border=5pt]{standalone}");
		out.println("\\usepackage{tikz-3dplot}");
		out.println("");
		out.println("%number of vertices: "+vertices);
		out.println("\\newcommand{\\vertices}{");
		out.print(outV.toString());
		out.println("}");
		out.println("");
		out.println("%number of facets: "+facets);
		out.println("\\newcommand{\\facets}[1]{");
		out.print(outF.toString());
		/*
		//not working in this way since the order of vertices is not linear
		out.println("\\foreach \\i in {0,3,...,"+(vertices-3)+"} {");
		out.println("  \\pgfmathsetmacro{\\ib}{\\i + 1}");
		out.println("  \\pgfmathsetmacro{\\ic}{\\i + 2}");
		out.println("  \\draw[#1] (c\\i) -- (c\\ib) -- (c\\ic);");
		out.println("}");
		*/
		out.println("}");
		out.println("");

		out.println("\\begin{document}");
		out.println("\\tdplotsetmaincoords{0}{0}");
		out.println("\\begin{tikzpicture}[tdplot_main_coords]");
		out.println("  \\vertices");
		out.println("  \\facets{line width=0.045pt, scale=15}");
		out.println("\\end{tikzpicture}");
		out.println("");
		out.println("\\tdplotsetmaincoords{70}{110}");
		out.println("\\begin{tikzpicture}[tdplot_main_coords]");
		out.println("  \\vertices");
		out.println("  \\facets{line width=0.045pt, blue, scale=20}");
		out.println("\\end{tikzpicture}");
		out.println("");
		out.println("\\end{document}");
		
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
		return null;
	}
	

	public void increaseProgress() {
		workbench.setIndeterminateProgress(this);
	}

}