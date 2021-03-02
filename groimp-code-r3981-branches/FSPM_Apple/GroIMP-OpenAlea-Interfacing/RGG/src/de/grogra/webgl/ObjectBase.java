
package de.grogra.webgl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

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

	//scale factor to convert from meter to millimeter
	protected final static int FACTOR = 1;
	//Number of segmented faces
	protected final static int SEGMENTS = 32;
	
	
	protected Matrix4d transformation;
	protected PrintWriter out;

	@Override
	public void export (Leaf node, InnerNode transform, SceneGraphExport sge) throws IOException {
		// convert to WebGLExport
		WebGLExport export = (WebGLExport) sge;

		// obtain output writer
		out = export.out;

		// obtain transformation matrix for this node
		Matrix4d m = export.matrixStack.peek ();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		transformation = n;
		out.println("");
		export.primitives++;
		
		// call implementation export function
		if(!exportImpl(node, export)) out.println("// not supported: "+node);
	}

	abstract boolean exportImpl (Leaf node, WebGLExport export) throws IOException;

	Matrix4d getTransformation () {
		return transformation;
	}

	// round to 5 decimal digits
	static double round (double d) {
		return (double) Math.round (d * 100000) / 100000;
	}
	
	void wirteBody(Leaf node, String name, float l, boolean translate) {
		Matrix3f m1 = new Matrix3f(); 
		Vector3d t1 = new Vector3d();
		double scale = transformation.get(m1, t1);
		t1.scale(-0.5);
		
		// translation
		Vector3d trans = new Vector3d();
		transformation.get(trans);

		// RGBA
		Shader shader = ((SceneTreeWithShader.Leaf)node).shader;
		if (shader instanceof RGBAShader) {
			RGBAShader rgba = (RGBAShader) shader;
			if(this instanceof Parallelogram || this instanceof Plane) {
				out.println("\tvar material = new THREE.MeshBasicMaterial( { "
					+ "color: "+rgba.getAverageColor()+", "
					+ "side: THREE.DoubleSide });");
			} else {
				out.println("\tvar material = new THREE.MeshLambertMaterial( { "
						+ "color: "+rgba.getAverageColor()+", "
						+ "side: THREE.DoubleSide });");
			}
		}
		
//		var moonTexture = THREE.ImageUtils.loadTexture( 'images/moon.jpg' );
//		var moonMaterial = new THREE.MeshLambertMaterial( { map: moonTexture, transparent: true, opacity: 0.75 } );

//		material = new THREE.MultiMaterial( [
//		                 					new THREE.MeshPhongMaterial( { color: 0xffffff, shading: THREE.FlatShading } ), // front
//		                 					new THREE.MeshPhongMaterial( { color: 0xffffff, shading: THREE.SmoothShading } ) // side
//		                 				] );

		if (shader instanceof Phong) {
			Phong phong = (Phong) shader;
			ChannelMap diffuseMap = phong.getDiffuse();
			RGBColor rgbDiffuse = new RGBColor(1,1,1);
			if (diffuseMap instanceof RGBColor) {
				rgbDiffuse = (RGBColor) diffuseMap;
			}
			ChannelMap specularMap = phong.getDiffuse();
			RGBColor rgbSpecular = new RGBColor(0.1f,0.1f,0.1f);
			if (specularMap instanceof RGBColor) {
				rgbSpecular = (RGBColor) specularMap;
			}
			out.println("\tvar material = new THREE.MeshPhongMaterial( { "
					+ "color: "+rgbDiffuse.getAverageColor()+", "
					+ "specular: "+rgbSpecular.getAverageColor()+", "
					+ "shininess: 30, "
					+ "side: THREE.DoubleSide, "
					+ "shading: THREE.SmoothShading"
					+ " });");

		}

		out.println("\tvar m = new THREE.Matrix4();");
		out.println("\tm.set("
		+round(transformation.m00)+", "+round(transformation.m02)+", "+round(transformation.m01)+", "+round(transformation.m03)+", "
		+round(transformation.m10)+", "+round(transformation.m12)+", "+round(transformation.m11)+", "+round(transformation.m13)+", "
		+round(transformation.m20)+", "+round(transformation.m22)+", "+round(transformation.m21)+", "+round(transformation.m23)+", "
		+round(transformation.m30)+", "+round(transformation.m32)+", "+round(transformation.m31)+", "+round(transformation.m33)+");");
		if(translate)out.println("geometry.applyMatrix( new THREE.Matrix4().makeTranslation( 0.0, "+round(l/2f)+", 0.0 ) );");
		out.println("\tgeometry.applyMatrix( m );");
		out.println("\tvar "+name+" = new THREE.Mesh( geometry, material );");
		out.println("\tgroup.add( "+name+" );");
	}
}
