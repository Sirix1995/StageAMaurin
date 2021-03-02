package de.grogra.imp3d.glsl.material.channel;

import java.util.HashMap;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.Transform3D;

public class Transform3DCollection {
	static HashMap<Class, GLSLTransform3D> ftf= new HashMap<Class, GLSLTransform3D>();
	
	private static void insertIntoHashMap(GLSLTransform3D in) {
		ftf.put(in.instanceFor(), in);
	}
	
	public static void initMap() {
		insertIntoHashMap(new GLSLUniformScale());
		insertIntoHashMap(new GLSLTVector3d());
		insertIntoHashMap(new GLSLTMatrix4d());
		insertIntoHashMap(new GLSLComponentTransform());
	}
	
	public static Result transform(Result in, Transform3D fkt, ShaderConfiguration sc) {
		GLSLTransform3D ftfw = fkt != null ? ftf.get(fkt.getClass()) : null;
		return ftfw != null ? ftfw.process(in, fkt, sc) : in;
	} 
}
