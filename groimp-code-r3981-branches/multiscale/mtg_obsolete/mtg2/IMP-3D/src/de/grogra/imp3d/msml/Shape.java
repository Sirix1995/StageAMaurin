package de.grogra.imp3d.msml;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import java.net.URL;
import de.grogra.imp3d.objects.*;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import de.grogra.pf.registry.Registry;

public class Shape extends X3DMSMLDatatype{

	public void export (Object o, Document doc, Element data, Node n){
		Element shapeElem=(Element) doc.createElementNS(GROIMPDATATYPE_NAMESPACE,"g:Shape");
		GraphState gs = GraphState.current(n.getGraph());
		Object shape = gs.getObjectDefault(n, true, Attributes.SHAPE, null);
		if (shape instanceof Cylinder){
			Element cylinder=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Cylinder");
			cylinder.setAttribute("radius",String.valueOf(gs.getFloat(n, true, Attributes.RADIUS)));
			cylinder.setAttribute("height",String.valueOf(gs.getDouble(n, true, Attributes.LENGTH)));
			cylinder.setAttribute("top",String.valueOf(!gs.getBoolean(n, true, Attributes.TOP_OPEN)));
			cylinder.setAttribute("bottom",String.valueOf(!gs.getBoolean(n, true, Attributes.BASE_OPEN)));
			shapeElem.setAttribute("startpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.START_POSITION,0f)));
			shapeElem.setAttribute("endpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.END_POSITION,1f)));		
			shapeElem.appendChild(cylinder);
			data.appendChild(shapeElem);
			new Transform().export(o,doc,data,n);
			new Appearance().export(o,doc,data,n);
		}
		else if (shape instanceof Cone){
			Element cone=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Cone");
			cone.setAttribute("bottomRadius",String.valueOf(gs.getFloat(n, true, Attributes.RADIUS)));
			cone.setAttribute("height",String.valueOf(gs.getDouble(n, true, Attributes.LENGTH)));
			cone.setAttribute("bottom",String.valueOf(!gs.getBoolean(n, true, Attributes.BASE_OPEN)));
			shapeElem.setAttribute("startpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.START_POSITION,0f)));
			shapeElem.setAttribute("endpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.END_POSITION,1f)));
			shapeElem.appendChild(cone);
			data.appendChild(shapeElem);
			new Transform().export(o,doc,data,n);
			new Appearance().export(o,doc,data,n);
		}
		else if (shape instanceof Sphere){
			Element sphere=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Sphere");
			sphere.setAttribute("radius",String.valueOf(gs.getFloat(n, true, Attributes.RADIUS)));
			shapeElem.appendChild(sphere);
			data.appendChild(shapeElem);
			new Transform().export(o,doc,data,n);
			new Appearance().export(o,doc,data,n);
		}
		else if (shape instanceof Box){
			Element box=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Box");
			box.setAttribute("size",getStringFromVector3d(new Vector3d(gs.getDouble(n, true, Attributes.WIDTH),gs.getDouble(n, true, Attributes.LENGTH),gs.getDouble(n, true, Attributes.HEIGHT))));
			shapeElem.setAttribute("startpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.START_POSITION,0f)));
			shapeElem.setAttribute("endpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.END_POSITION,1f)));
			shapeElem.appendChild(box);
			data.appendChild(shapeElem);
			new Transform().export(o,doc,data,n);
			new Appearance().export(o,doc,data,n);
		}
		else if (shape instanceof Frustum){
			Element extrusion=(Element) doc.createElementNS(X3D_NAMESPACE,"x3d:Extrusion");
			extrusion.setAttribute("height",String.valueOf(gs.getDouble(n, true, Attributes.LENGTH)));
			extrusion.setAttribute("endCap",String.valueOf(!gs.getBoolean(n, true, Attributes.TOP_OPEN)));
			extrusion.setAttribute("beginCap",String.valueOf(!gs.getBoolean(n, true, Attributes.BASE_OPEN)));
			extrusion.setAttribute("solid","false");
			extrusion.setAttribute("creaseAngle","90");
			extrusion.setAttribute("crossSection",
				"1 0 0.866 0.5 0.5 0.866 0 1 -0.5 0.866 -0.866 0.5 -1 0 -0.866 -0.5 -0.5 -0.866 0 -1 0.5 -0.866 0.866 -0.5 1 0");
			extrusion.setAttribute("spine","0 0 0 "+ getStringFromVector3d(MSMLWriter.COORD.transform(new Vector3d(0, 0, gs.getDouble(n, true, Attributes.LENGTH)))));
			extrusion.setAttribute("scale",gs.getFloat(n, true, Attributes.BASE_RADIUS)+" "+
				gs.getFloat(n, true, Attributes.BASE_RADIUS)+" "+
				gs.getFloat(n, true, Attributes.TOP_RADIUS)+" "+
				gs.getFloat(n, true, Attributes.TOP_RADIUS));
			shapeElem.setAttribute("startpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.START_POSITION,0f)));
			shapeElem.setAttribute("endpos",String.valueOf(gs.getFloatDefault(n, true, Attributes.END_POSITION,1f)));
			shapeElem.appendChild(extrusion);
			data.appendChild(shapeElem);
			new Transform().export(o,doc,data,n);
			new Appearance().export(o,doc,data,n);
		}
	}
	
	public Node export (Registry registry, org.w3c.dom.Node node, Node n, URL baseURL)
	{
		Node result=n;
		if (node.getNamespaceURI ().equals (GROIMPDATATYPE_NAMESPACE)){
			for (org.w3c.dom.Node i = node.getFirstChild (); i != null; i = i.getNextSibling ()){
				if ((i.getNodeType () == 1)&&(i.getNamespaceURI ().equals (X3D_NAMESPACE))){
					if (i.getLocalName().equals ("Cylinder")){
						Cylinder c=new Cylinder(2.0f, 1.0f);
						String s=getAttributeContent(i,"radius");
						if (s!=""){
							c.setRadius(Float.valueOf(s).floatValue());
						}
						s=getAttributeContent(i,"height");
						if (s!=""){
							c.setLength(Float.valueOf(s).floatValue());
						}
						s=getAttributeContent(i,"top");
						if (s!=""){
							c.setTopOpen(!Boolean.valueOf(s).booleanValue());
						}
						s=getAttributeContent(i,"bottom");
						if (s!=""){
							c.setBaseOpen(!Boolean.valueOf(s).booleanValue());
						}
						s=getAttributeContent(node,"startpos");
						if (s!=""){
							c.setStartPosition(Float.valueOf(s).floatValue());
						}else{
							c.setStartPosition(-0.5f);
						}
						s=getAttributeContent(node,"endpos");
						if (s!=""){
							c.setEndPosition(Float.valueOf(s).floatValue());
						}else{
							c.setEndPosition(0);
						}
						result=c;
					}
					else if (i.getLocalName().equals ("Cone")){
						Cone c=new Cone();
						c.setLength(2.0f);
						String s=getAttributeContent(i,"bottomRadius");
						if (s!=""){
							c.setRadius(Float.valueOf(s).floatValue());
						}
						s=getAttributeContent(i,"height");
						if (s!=""){
							c.setLength(Float.valueOf(s).floatValue());
						}
						s=getAttributeContent(i,"bottom");
						if (s!=""){
							c.setOpen(!Boolean.valueOf(s).booleanValue());
						}
						s=getAttributeContent(node,"startpos");
						if (s!=""){
							c.setStartPosition(Float.valueOf(s).floatValue());
						}else{
							c.setStartPosition(-0.5f);
						}
						s=getAttributeContent(node,"endpos");
						if (s!=""){
							c.setEndPosition(Float.valueOf(s).floatValue());
						}else{
							c.setEndPosition(0);
						}
						result=c;
					}
					else if (i.getLocalName().equals ("Sphere")){
						Sphere c=new Sphere(1.0f);
						String s=getAttributeContent(i,"radius");
						if (s!=""){
							c.setRadius(Float.valueOf(s).floatValue());
						}
						result=c;
					}
					else if (i.getLocalName().equals ("Box")){
						Box b=new Box();
							b.setLength(2.0f);
							b.setWidth(2.0f);
							b.setHeight(2.0f);
						String s=getAttributeContent(i,"size");
						if (s!=""){
							Vector3f size=getVector3fFromString(s);	
							b.setLength(size.y);
							b.setWidth(size.x);
							b.setHeight(size.z);
						}
						s=getAttributeContent(node,"startpos");
						if (s!=""){
							b.setStartPosition(Float.valueOf(s).floatValue());
						}else{
							b.setStartPosition(-0.5f);
						}
						s=getAttributeContent(node,"endpos");
						if (s!=""){
							b.setEndPosition(Float.valueOf(s).floatValue());
						}else{
							b.setEndPosition(0);
						}
						result=b;
					}
				}
			}
		}
		return result;
	}
}
