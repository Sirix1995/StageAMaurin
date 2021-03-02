package de.grogra.imp3d.msml;

import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.shading.Shader;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.*;
import de.grogra.msml.*;
import de.grogra.math.*;


import javax.vecmath.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.net.URL;
import de.grogra.pf.registry.Registry;
import de.grogra.vecmath.Math2;

public class Transform extends X3DMSMLDatatype{
	public static final CoordTransformer READER_COORD =
	new CoordTransformer(new Matrix3f(1, 0, 0,
									  0, 0, -1,
									  0, 1, 0),
			 			 new Matrix3f(1, 0, 0,
									  0, 1, 0,
									  0, 0, 1));


	public void export (Object o, Document doc, Element data, Node n){
		GraphState gs = GraphState.current(n.getGraph());
		Object t = gs.getObjectDefault(n, true, Attributes.TRANSFORM, null);
		if (t!=null){
			Transform3D transformation = (Transform3D)t;
			if (transformation!=null){
				Element transform=(Element) doc.createElementNS(GROIMPDATATYPE_NAMESPACE,"g:Transform");
				Element x3dtransform=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Transform");
				transform.appendChild(x3dtransform);
		 		data.appendChild(transform);
				
		 		
				if (transformation instanceof TMatrix4d){
			 		Matrix4d m4d=(Matrix4d)transformation;
					//Rotation
			 		Matrix3d rotation = new Matrix3d();
			 		Matrix3d scaleMat = new Matrix3d();

			 		m4d.getRotationScale (scaleMat);
			 		Math2.decomposeQR (scaleMat, rotation);

					AxisAngle4d aa4d = convertMatrix2AxisAngle(
						MSMLWriter.COORD.transform(rotation));
					if ((aa4d.angle!=0)&&(new Vector3d(aa4d.x,aa4d.y,aa4d.z).length()!=0)){
						x3dtransform.setAttribute("rotation",getStringFromAxisAngle4d(aa4d));
					}
					//nonuniformScale
					Vector3d scale = new Vector3d();

//					javax.media.j3d.Transform3D t3d=new javax.media.j3d.Transform3D(m4d);
//					t3d.getScale(scale);
					
					scale.x = scaleMat.m00;
					scale.y = scaleMat.m11;
					scale.z = scaleMat.m22;

					scale = MSMLWriter.COORD.transform(scale);
					//scale-values shall be greater than zero ! X3D-Convention!
					scale.absolute();
					if (!scale.equals(new Vector3d(1,1,1))){
						x3dtransform.setAttribute("scale",getStringFromVector3d(scale));
					}

					//Translation
					Vector3d translation = ((Null)n).getTranslation();
					if (translation.length() != 0){
						x3dtransform.setAttribute("translation",getStringFromVector3d(MSMLWriter.COORD.transform(translation)));
					}
				}else if(transformation instanceof UniformScale){
					//uniformScale
					float scale = ((UniformScale)transformation).getScale();
					if (scale != 1){
						x3dtransform.setAttribute("scale",getStringFromVector3f(new Vector3f(scale,scale,scale)));
					}
				}else if(transformation instanceof TVector3d){
					//Translation
					Vector3d translation = MSMLWriter.COORD.transform((Vector3d)transformation);
					if (translation.length() != 0){
						x3dtransform.setAttribute("translation",getStringFromVector3d(translation));
					}
				}
			}
		}
	}
	
	public Node export (Registry registry, org.w3c.dom.Node node, Node n, URL baseURL)
	{
		org.w3c.dom.Node subTransformNode=getSubTransformNode(node);
		Null transformationNode=null;
		if ((n==null)||!(n instanceof Null)){
			n=transformationNode=new Null();
		}
		if (subTransformNode==null){
			transformationNode=(Null)n;
			if (transformationNode.getTransform()==null){
				TMatrix4d tm4d=new TMatrix4d();
				tm4d.setIdentity();
				transformationNode.setTransform(tm4d);
			}
		}else{
			transformationNode=(Null)export(registry, subTransformNode,n,baseURL);
		}
		Vector3f c = new Vector3f(0, 0, 0),
				 s = new Vector3f(1, 1, 1),
				 t = new Vector3f(0, 0, 0);
		AxisAngle4f r = new AxisAngle4f(0, 0, 1, 0),
				   sr = new AxisAngle4f(0, 0, 1, 0);
		
		
		if (getAttributeContent(node,"translation")!=""){
			t=getVector3fFromString(getAttributeContent(node,"translation"));
		}
		if (getAttributeContent(node,"rotation")!=""){
			r=getAxisAngle4fFromString(getAttributeContent(node,"rotation"));
		}
		if (getAttributeContent(node,"scale")!=""){
			s=getVector3fFromString(getAttributeContent(node,"scale"));	
		}
		if (getAttributeContent(node,"scaleOrientation")!=""){
			sr=getAxisAngle4fFromString(getAttributeContent(node,"scaleOrientation"));
		}
		if (getAttributeContent(node,"center")!=""){
			c=getVector3fFromString(getAttributeContent(node,"center"));
		}
		//P'=T � C � R � SR � S � -SR � -C � P
	  	
	  	Matrix4d transformation = new Matrix4d();
	  	transformation.setIdentity();
	  	
	  	Matrix4d translation = new Matrix4d();
	  	translation.set(new Vector3d(READER_COORD.transform(t)));
	  	transformation.mul(translation);
	  	
	  	Matrix4d center = new Matrix4d();
	  	center.set(new Vector3d(READER_COORD.transform(c)));
	  	transformation.mul(center);
	  	
	  	Matrix4d rotation = new Matrix4d();
	  	rotation.set(READER_COORD.transform(r));
	  	transformation.mul(rotation);
	  	
	  	Matrix4d scaleOrientation = new Matrix4d();
	  	scaleOrientation.set(READER_COORD.transform(sr));
	  	transformation.mul(scaleOrientation);
	  	
	  	s=READER_COORD.transform(s);
	  	s.absolute();
	  	Matrix4d scale = new Matrix4d(s.x,  0,  0,0,
	  									0,s.y,  0,0,
	  									0,  0,s.z,0,
	  									0,  0,  0,1);
	  	transformation.mul(scale);
	  	
	  	scaleOrientation.invert();
	  	transformation.mul(scaleOrientation);
	  	
	  	center.invert();
	  	transformation.mul(center);
	  	
	  	TMatrix4d actual = (TMatrix4d)transformationNode.getTransform();
	  	
	  	actual.mul(transformation,actual);
	  	
	  	transformationNode.setTransform(actual);

		return transformationNode;
	}

	private org.w3c.dom.Node getSubTransformNode(org.w3c.dom.Node node){
		for (org.w3c.dom.Node i = node.getFirstChild (); i != null; i = i.getNextSibling ()){
			if ((i.getNodeType () == org.w3c.dom.Node.ELEMENT_NODE)&&(i.getNamespaceURI ().equals (X3D_NAMESPACE))){
				if (i.getLocalName().equals ("Transform")){
					return i;
				}
			}
		}
		return null;
	}
}