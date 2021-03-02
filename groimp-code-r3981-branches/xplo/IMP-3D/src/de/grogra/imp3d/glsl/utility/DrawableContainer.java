package de.grogra.imp3d.glsl.utility;

import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Vector;

import javax.vecmath.Matrix4d;


import de.grogra.imp3d.glsl.material.GLSLMaterial;
import de.grogra.imp3d.glsl.renderable.GLSLRenderable;

import de.grogra.imp3d.shading.Shader;

/**
 * Container for Drawables. Optimized to cache Instances of Drawables
 * @author shi
 *
 */
public class DrawableContainer extends Vector<Drawable> {
	private static final long serialVersionUID = -4000295999794471113L;
	
	private int elements = 0;
	
	public void add(GLSLRenderable shape, Shader s, GLSLMaterial sh, boolean asNode, int layer, Matrix4d worldTransform, Matrix4d cachedWorldTransform) {
		Drawable ele;
		elements++;
		if(elements-1 >= elementCount)
			super.add( new Drawable(shape, s, sh, asNode, layer, worldTransform, cachedWorldTransform) );
		else {
			ele = get(elements-1);
			ele.init(shape, s, sh, asNode, layer, worldTransform, cachedWorldTransform);
		}
	}
	
	@Override
	public synchronized int size() {
		return elements;
	}
	
	@Override
    public synchronized Drawable lastElement() {
    	if (elementCount == 0) {
    	    throw new NoSuchElementException();
    	}
    	return (Drawable)elementData[elements - 1];
        }

	@Override
	public synchronized boolean isEmpty() {
		return elements == 0;
	}
	
	public void init() {
		elements = 0;
	}
	
	public void cleanup() {
		setSize(elements);
	}
	
	@Override
	public synchronized String toString() {
		return super.toString()+"#:"+ size() + "[" + "]";
	}
}
