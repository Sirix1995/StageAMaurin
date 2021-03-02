package de.grogra.imp3d.glsl.renderable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Box;
import de.grogra.imp3d.objects.Cylinder;
import de.grogra.imp3d.objects.MeshNode;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.imp3d.objects.NumericLabel;
import de.grogra.imp3d.objects.Patch;
import de.grogra.imp3d.objects.Polygon;
import de.grogra.imp3d.objects.Sphere;

public class RenderableCollection {

	/**
	 * Container for Drawables. Optimized to cache Instances of Drawables
	 * @author shi
	 * @param <T>
	 */
	private class CachedRessourceStack<T extends GLSLRenderable> extends Vector<T> {		
		private int elements = 0;
		private T base;
		
		public CachedRessourceStack(T base) {
			super();
			this.base = base;
		}
		
		public T add() {
			T ele;
			elements++;
			if(elements-1 >= elementCount) {
				super.add( (T) base.getInstance() );
				ele = lastElement();
			}
			else {
				ele = get(elements-1);
			}
			return ele;
		}
		
		@Override
		public synchronized int size() {
			return elements;
		}
		
		@Override
	    public synchronized T lastElement() {
	    	if (elementCount == 0) {
	    	    throw new NoSuchElementException();
	    	}
	    	return (T)elementData[elements - 1];
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
	
	private HashMap<Class, CachedRessourceStack<GLSLRenderable>> shapeReference = new HashMap<Class, CachedRessourceStack<GLSLRenderable>>();
	
	public RenderableCollection() {
		addToReferenceMap(new GLSLBox());
		addToReferenceMap(new GLSLSphere());
		addToReferenceMap(new GLSLCylinder());
		addToReferenceMap(new GLSLCone());
		addToReferenceMap(new GLSLFrustum());
		addToReferenceMap(new GLSLNullRenderable());
		addToReferenceMap(new GLSLPlane());
//		addToReferenceMap(new GLSLNumericLabel());
//		addToReferenceMap(new GLSLTextLabel());
		addToReferenceMap(new GLSLNURBSCurve());
		
		///////////////////////////////////////////////
		// Here all Polygonizable's are added
		
		// MeshNode
		addToReferenceMap(new GLSLPolygonizable() {
			@Override public Class<?> instanceFor() {return MeshNode.class;}});
		// NURBSSurface
		addToReferenceMap(new GLSLPolygonizable() {
			@Override public Class<?> instanceFor() {return NURBSSurface.class;}});
		// Patch
		addToReferenceMap(new GLSLPolygonizable() {
			@Override public Class<?> instanceFor() {return Patch.class;}});
		// Polygon
		addToReferenceMap(new GLSLPolygonizable() {
			@Override public Class<?> instanceFor() {return Polygon.class;}});
		
		///////////////////////////////////////////////
		
		init();
	}
	
	final private void addToReferenceMap (GLSLRenderable ref) {
		CachedRessourceStack<GLSLRenderable> newStack = new CachedRessourceStack<GLSLRenderable>(ref);
		shapeReference.put(ref.instanceFor(), newStack);
	} 
	
	public void init() {
		Iterator<CachedRessourceStack<GLSLRenderable>> it = shapeReference.values().iterator();
		while(it.hasNext())
			it.next().init();
	}

	public void cleanup() {
		Iterator<CachedRessourceStack<GLSLRenderable>> it = shapeReference.values().iterator();
		while(it.hasNext())
			it.next().cleanup();
	}
	
	public GLSLRenderable getInstance(Object shape, Object state, boolean asNode, GraphState gs) {
		if(shape == null) {
			System.err.println("NULL SHAPE REQUESTED!");
			return null;
		}
		CachedRessourceStack<GLSLRenderable> reference = shapeReference.get(shape.getClass());
		
		if(reference == null) {
			reference = shapeReference.get(null);
			if(reference == null) {
				System.err.println("NULLSHAPE NOT FOUND --- THIS IS REALLY BAD!");
				return null;
			}	
		}
		
		GLSLRenderable ref = reference.add();
		ref.updateInstance(shape, state, asNode, gs);
		 
		return ref;
	}

	public void removeUnused(OpenGLState glState) {
	}
}
