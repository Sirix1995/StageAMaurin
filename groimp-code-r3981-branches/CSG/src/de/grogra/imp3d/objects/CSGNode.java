/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d.objects;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import de.grogra.graph.*;
import de.grogra.graph.impl.*;
import de.grogra.imp3d.CSGable;
import de.grogra.imp3d.HalfEdgeStructCSG;
import de.grogra.imp3d.CSGObject;
import de.grogra.imp3d.HalfEdgeUtil;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.shading.Shader;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XObject;


public class CSGNode extends ShadedNull implements CSGable {
	CSGObject csg;
	int oldStamp;
	
	boolean child =false;
	
	int operation;
	// enh:field attr=Attributes.CSG_OPERATION
	// type=Attributes.CSG_OPERATION_TYPE getter setter

	// enh:insert
	// enh:begin
	// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field operation$FIELD;

	private static final class _Field extends NType.Field {
		private final int id;

		_Field(String name, int modifiers, de.grogra.reflect.Type type,
				de.grogra.reflect.Type componentType, int id) {
			super(CSGNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt(Object o, int value) {
			switch (id) {
			case 0:
				((CSGNode) o).operation = (int) value;
				return;
			}
			super.setInt(o, value);
		}

		@Override
		public int getInt(Object o) {
			switch (id) {
			case 0:
				return ((CSGNode) o).getOperation();
			}
			return super.getInt(o);
		}
	}

	static {
		$TYPE = new NType(new CSGNode());
		$TYPE.addManagedField(operation$FIELD = new _Field("operation",
				0 | _Field.SCO, Attributes.CSG_OPERATION_TYPE, null, 0));
		$TYPE.declareFieldAttribute(operation$FIELD, Attributes.CSG_OPERATION);
		$TYPE.addIdentityAccessor(Attributes.SHAPE); // TODO Sven
		$TYPE.validate();
	}

	@Override
	protected NType getNTypeImpl() {
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance() {
		return new CSGNode();
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int value) {
		this.operation = (int) value;
	}

	// enh:end

	public CSGNode() {
		this(Attributes.CSG_UNION);
	}

	public CSGNode(int operation) {
		setOperation(operation);
	}

	public Node findChild(Node n, Matrix4d local) {
		
//		System.out.println(n.getName ());
//		System.out.println(n.getClass ());
		
		
		Attribute[] test = n.getAttributes();

		// This code comes from the Visitor 3D class
		// it gets the Transformation for the CSG-primitives
		
		GraphState state = this.getCurrentGraphState();
		
		Transformation t = (n == null) ? null
				: (Transformation) state.getObjectDefault
				(n, true, Attributes.TRANSFORMATION, null);
		
		Matrix4d pre = new Matrix4d();
		
		t.preTransform (n, true, local, pre, state);
//		System.out.println ("pre transform is "+pre);
		local.set (pre);
		
		Matrix4d x = new Matrix4d();
		Matrix4d m = new Matrix4d();
		
		if(!(n instanceof CSGable) ){
		t.postTransform (n, true, pre, x, m, state);
//		System.out.println ("post transform is "+x);
		local.set (x);
		}
//		
		
		//search the attributes for Shape
		for (int i = 0; i < test.length; i++) {
			
				if (n instanceof CSGNode) {
					( (CSGNode) n ).setAsChild();
				return n;
			}

			else if (test[i] == Attributes.SHAPE) {

				// System.out.println("f SHAPE");
				return n;
			} 

		} // end of attributes for loop
		
		// search for next Node
		for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)) {
			if (e.isSource(n)) {
				Node r = findChild(e.getTarget(), local);
				if (r != null)
					return r;
			}
		}

		
		return null;

	}

	private void setAsChild ()
	{
		child = true;
	}
	
	private void setAsRoot ()
	{
		child = false;
	}

//	public void preprocess() {
//		
//		System.out.println("Preprocess "+this);
//		
////		if (csg==null /*|| this.getStamp ()!= oldStamp*/) {
//			
//			oldStamp=this.getStamp ();
//
//			Matrix4d t1, t2;
//			t1 = new Matrix4d();
//			t2 = new Matrix4d();
//			t1.setIdentity();
//			t2.setIdentity();
//			
//			Node n1, n2;
//			n1 = null;
//			n2 = null;
//
//			for (Edge e = this.getFirstEdge(); e != null; e = e.getNext(this)) {
//				if (e.isSource(this)) {
//					if (n1 == null) {
//						n1 = findChild(e.getTarget(), t1);
//					} else {
//						n2 = findChild(e.getTarget(), t2);
//					}
//				}
//			}
//
//			if (n1 instanceof CSGable && n2 instanceof CSGable) {
//				
//				((CSGable) n1).preprocess();
//				((CSGable) n2).preprocess();
//				
////				System.out.println ("object 1 "+n1);
////				System.out.println ("object 2 "+n2);
//				
//				if(n1==null){
////					System.out.println("Objekt 1 ist null");
//				}
//				if(n2==null){
////					System.out.println("Objekt 2 ist null");
//				}
//				
//
//				HalfEdgeStructCSG  obj1Mesh = ((CSGable) n1).getMesh();
//				HalfEdgeStructCSG  obj2Mesh = ((CSGable) n2).getMesh();
//				
//				if(obj1Mesh==null){
////					System.out.println("The Mesh return from object 1 is null");
////					System.out.println (n1);
//				}
//				
//				if(obj2Mesh==null){
////					System.out.println("The Mesh return from object 2 is null");
////					System.out.println (n2);
//				}
//				
////				if(obj1Mesh!=null && obj2Mesh!=null){
//					csg = new CSGObject(obj1Mesh, t1,
//						obj2Mesh , t2, operation);
//					try{
//					csg.intersect();
//					} catch(Exception e){
//
////						System.out.println ("Exception caught");
//						if(n1 instanceof CSGNode){
//							((CSGNode)n1).setAsRoot();
//						}
//						if(n2 instanceof CSGNode){
//							((CSGNode)n2).setAsRoot();
//						}
//					}
////				}
////				else{
////					csg=new CSGObject();
////					HalfEdgeUtil.insertSphere (8, 8, csg, 1);
////				}
////			}
//		}
//			if(n1 instanceof CSGable && !(n2 instanceof CSGable)){
//				((CSGable) n1).preprocess();
//				
//				csg=new CSGObject();
//				
//				HalfEdgeStructCSG mesh = ((CSGable) n1).getMesh () ;
//				if(mesh.getFacesCount ()>0){
//				csg.getObject ( mesh );}
//			}
//			if (!(n1 instanceof CSGable) && n2 instanceof CSGable){
//				((CSGable) n2).preprocess();
//				
//				csg=new CSGObject();
//				
//				HalfEdgeStructCSG mesh = ((CSGable) n2).getMesh () ;
//				if(mesh.getFacesCount ()>0){
//				csg.getObject ( mesh );}
//			}
//
//	}
	
	public HalfEdgeStructCSG getMesh() {

		
//if (csg==null /*|| this.getStamp ()!= oldStamp*/) {
			
			if (true || csg == null || oldStamp!=this.getStamp ())
			{
				oldStamp = this.getStamp ();
				csg = new CSGObject ();
				Matrix4d t1, t2;
				t1 = new Matrix4d ();
				t2 = new Matrix4d ();
				t1.setIdentity ();
				t2.setIdentity ();
				Node n1, n2;
				n1 = null;
				n2 = null;
				for (Edge e = this.getFirstEdge (); e != null; e = e
					.getNext (this))
				{
					if (e.isSource (this))
					{
						t1.setIdentity ();
						n1 = findChild (e.getTarget (), t1);
						if (n1 instanceof CSGable)
						{
//							System.out.println ("Adding " + n1);
							csg.addPrimitive (((CSGable) n1).getMesh (), t1,
								operation);
						}

						//					if (n1 == null) {
						//						n1 = findChild(e.getTarget(), t1);
						//					} else {
						//						n2 = findChild(e.getTarget(), t2);
						//					}
					}
				}
			}
			//			if (n1 instanceof CSGable && n2 instanceof CSGable) {
//				
//				System.out.println ("Calculating CSG Node, with "+n1+" and "+n2);
//				
////				((CSGable) n1).preprocess();
////				((CSGable) n2).preprocess();
//				
//				
//					csg = new CSGObject(((CSGable) n1).getMesh(), t1,
//							((CSGable) n2).getMesh(), t2, operation);
////					try{
//					csg.intersect();
////					}
////					catch (Exception e){
////						System.err.println ("Exception caught");
////						System.err.println (e);
////						if(n1 instanceof CSGNode){
////							((CSGNode)n1).setAsRoot();
////						}
////						if(n2 instanceof CSGNode){
////							((CSGNode)n2).setAsRoot();
////						}
////					}
//
//			} else {
//				csg = new CSGObject();
//			}
			return csg;
	}

	public boolean isActive() {
		return false;
	}

	public void draw(GL gl, int s) {
		
//		System.out.println("Draw the CSG-Object with shader "+s);
			csg.draw(gl,s);
	}
	
	
	public void drawTwins(GL gl){
		csg.drawTwins(gl);
	}
	
	public int getShadersCount(){
		if (csg==null) return 0;
		else return csg.getShaderCount();
	}
	
	public Shader getShader (int shaderpos)
	{return csg.getShaderPos(shaderpos);}

	@Override
	public boolean usedInCSG() {
		// TODO Auto-generated method stub
		return false;
	}

	public void drawLink (GL gl)
	{
		// TODO Auto-generated method stub
		csg.drawLink (gl);
	}
	
	public boolean isCSGRoot(){
		return !child;
	}
}
