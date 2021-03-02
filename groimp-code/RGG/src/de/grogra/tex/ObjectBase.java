
package de.grogra.tex;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;

import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.io.SceneGraphExport;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.ChannelMap;
import de.grogra.math.RGBColor;

public abstract class ObjectBase implements SceneGraphExport.NodeExport {

	Matrix4d transformation;
	StringBuffer outV, outF;
	TEXExport export;
	
	int[] tmpIdx;

	String colString = "255 255 255 0";

	protected void getColorString(Leaf node) {
		colString = "255 255 255 0";
		// RGBA
		Shader shader = ((SceneTreeWithShader.Leaf)node).shader;
		if (shader instanceof RGBAShader) {
			RGBAShader rgba = (RGBAShader) shader;
			int i = rgba.getAverageColor();
			colString = ""+((i >> 16) & 255)+" "+((i >> 8) & 255)+" "+(i & 255)+" "+((((i >>> 24))/255f));
		}
		if (shader instanceof Phong) {
			Phong phong = (Phong) shader;
			ChannelMap diffuseMap = phong.getDiffuse();
			RGBColor rgbDiffuse = new RGBColor(1,1,1);
			if (diffuseMap instanceof RGBColor) {
				rgbDiffuse = (RGBColor) diffuseMap;
			}
			ChannelMap transparencyMap = phong.getTransparency();
			RGBColor rgbTransparency = new RGBColor(0f,0f,0f);
			if (transparencyMap instanceof RGBColor) {
				rgbTransparency = (RGBColor) transparencyMap;
			}
			int i = rgbDiffuse.getAverageColor();
			colString = ""+((i >> 16) & 255)+" "+((i >> 8) & 255)+" "+(i & 255);
			i = rgbTransparency.getAverageColor();
			int t = (int)((((i >> 16) & 255)+((i >> 8) & 255)+(i & 255))/3f /255f);
			colString = colString+t;
			
		}

	}
	
	protected void writeVertices(Point4d[] p) {
		tmpIdx = new int[p.length];
		for (int i = 0; i < p.length; i++) {
			outV.append("\\coordinate (c"+export.vertices+") at ("+round(p[i].x)+", "+round(p[i].y)+", "+round(p[i].z)+");\n");
			tmpIdx[i] = export.vertices;
			export.vertices++;
		}
	}

	protected void writeFacets(String[] f) {
		String[] s;
		for (int i = 0; i < f.length; i++) {
			s = f[i].split(" ");
			outF.append("\\draw[#1] (c"+tmpIdx[Integer.parseInt(s[0])]+") -- (c"+tmpIdx[Integer.parseInt(s[1])]+") -- (c"+tmpIdx[Integer.parseInt(s[2])]+") -- cycle;\n");
		}
		export.facets += f.length;
	}

	@Override
	public void export (Leaf node, InnerNode transform, SceneGraphExport sge) throws IOException {
		// convert to PLYExport
		export = (TEXExport) sge;

		// obtain output buffer
		outV = export.outV;
		outF = export.outF;

		// obtain transformation matrix for this node
		Matrix4d m = export.matrixStack.peek ();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		transformation = n;
		export.primitives++;

		// call implementation export function
		exportImpl (node, transform, export);
	}

	abstract void exportImpl (Leaf node, InnerNode transform, TEXExport export) throws IOException;

	Matrix4d getTransformation () {
		return transformation;
	}

	// round to 7 decimal digits
	static double round (double d) {
		return (double) Math.round (d * 10000000) / 10000000;
	}

	void mesh2 (PolygonArray p, Leaf node, boolean twoF) {

		//generate colour string
		getColorString(node);
		
		if (p.polygons.size == 0) return;
		int v1,v2,v3,v4;
		// output indices
		for (int i = 0; i < p.polygons.size (); i += p.edgeCount) {
			v1 = p.polygons.get (i + 0);
			v2 = p.polygons.get (i + 1);
			v3 = p.polygons.get (i + 2);
			v4 = p.polygons.get (i + 3);
			
			//first facet of the rectangle
			Point4d[] pA = new Point4d[] {
				new Point4d(p.vertices.get(v1*p.dimension+0),p.vertices.get(v1*p.dimension+1),p.vertices.get(v1*p.dimension+2),1),
				new Point4d(p.vertices.get(v2*p.dimension+0),p.vertices.get(v2*p.dimension+1),p.vertices.get(v2*p.dimension+2),1),
				new Point4d(p.vertices.get(v3*p.dimension+0),p.vertices.get(v3*p.dimension+1),p.vertices.get(v3*p.dimension+2),1)
			};
			writeVertices(pA);
			writeFacets(new String[] {"0 1 2"});
			
			if(twoF) {
				//second facet of the rectangle
				pA = new Point4d[] {
					new Point4d(p.vertices.get(v1*p.dimension+0),p.vertices.get(v1*p.dimension+1),p.vertices.get(v1*p.dimension+2),1),
					new Point4d(p.vertices.get(v3*p.dimension+0),p.vertices.get(v3*p.dimension+1),p.vertices.get(v3*p.dimension+2),1),
					new Point4d(p.vertices.get(v4*p.dimension+0),p.vertices.get(v4*p.dimension+1),p.vertices.get(v4*p.dimension+2),1)
				};
				writeVertices(pA);
				writeFacets(new String[] {"0 1 2"});
			}
		}
	}
}
