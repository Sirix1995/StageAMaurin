
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

package de.grogra.ext.exchangegraph;

import java.util.HashMap;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.ScaleClass;
import de.grogra.xl.util.BidirectionalHashMap;
import de.grogra.imp3d.objects.*;
import de.grogra.ext.exchangegraph.graphnodes.*;
import de.grogra.ext.exchangegraph.helpnodes.*;
import de.grogra.rgg.Axiom;
import de.grogra.rgg.SRoot;
import de.grogra.rgg.TypeRoot;
import de.grogra.turtle.*;

/**
 * The IOContext contains informations about import and export the
 * exchange graph format.
 * <p>
 * If the same context is used for import <b>and</b> export you can
 * use the same data, eg. give the same GroIMP nodes the same XEG node id,
 * see {@link #nodeMap node map}. This is used for example in the
 * OpenAlea plugin.
 * 
 * @author Uwe Mannl
 *
 */
public class IOContext {
	
	/**
	 * Mapping of standard XEG node types to GroIMP node types.
	 */
	public static HashMap<String, String> importNodeTypes = new HashMap<String, String>();
	
	/**
	 * Mapping of GroIMP node types to standard XEG node types.
	 */
	public static HashMap<String, String> exportNodeTypes = new HashMap<String, String>();

	/**
	 * Mapping of GroIMP node types to XEG helper classes for import and export.
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap<Class, Class> xegNodeTypes = new HashMap<Class, Class>();
	
	static {
		importNodeTypes.put("Node", "de.grogra.imp3d.objects.ShadedNull");
		importNodeTypes.put("ShadedNull", "de.grogra.imp3d.objects.ShadedNull");
		importNodeTypes.put("Box", "de.grogra.imp3d.objects.Box");
		importNodeTypes.put("Cone", "de.grogra.imp3d.objects.Cone");
		importNodeTypes.put("Sphere", "de.grogra.imp3d.objects.Sphere");
		importNodeTypes.put("Cylinder", "de.grogra.imp3d.objects.Cylinder");
		importNodeTypes.put("Frustum", "de.grogra.imp3d.objects.Frustum");
		importNodeTypes.put("F", "de.grogra.turtle.F");
		importNodeTypes.put("F0", "de.grogra.turtle.F0");
		importNodeTypes.put("M", "de.grogra.turtle.M");
		importNodeTypes.put("M0", "de.grogra.turtle.M0");
		importNodeTypes.put("RL", "de.grogra.turtle.RL");
		importNodeTypes.put("RU", "de.grogra.turtle.RU");
		importNodeTypes.put("RH", "de.grogra.turtle.RH");
		importNodeTypes.put("V", "de.grogra.turtle.V");
		importNodeTypes.put("Vl", "de.grogra.turtle.Vl");
		importNodeTypes.put("VlAdd", "de.grogra.turtle.VlAdd");
		importNodeTypes.put("VlMul", "de.grogra.turtle.VlMul");
		importNodeTypes.put("VAdd", "de.grogra.turtle.VAdd");
		importNodeTypes.put("VMul", "de.grogra.turtle.VMul");
		importNodeTypes.put("RV", "de.grogra.turtle.RV");
		importNodeTypes.put("RV0", "de.grogra.turtle.RV0");
		importNodeTypes.put("RG", "de.grogra.turtle.RG");
		importNodeTypes.put("RD", "de.grogra.turtle.RD");
		importNodeTypes.put("RO", "de.grogra.turtle.RO");
		importNodeTypes.put("RP", "de.grogra.turtle.RP");
		importNodeTypes.put("RN", "de.grogra.turtle.RN");
		importNodeTypes.put("AdjustLU", "de.grogra.turtle.AdjustLU");
		importNodeTypes.put("L", "de.grogra.turtle.L");
		importNodeTypes.put("Ll", "de.grogra.turtle.Ll");
		importNodeTypes.put("LlAdd", "de.grogra.turtle.LlAdd");
		importNodeTypes.put("LlMul", "de.grogra.turtle.LlMul");
		importNodeTypes.put("LAdd", "de.grogra.turtle.LAdd");
		importNodeTypes.put("LMul", "de.grogra.turtle.LMul");
		importNodeTypes.put("D", "de.grogra.turtle.D");
		importNodeTypes.put("Dl", "de.grogra.turtle.Dl");
		importNodeTypes.put("DlAdd", "de.grogra.turtle.DlAdd");
		importNodeTypes.put("DlMul", "de.grogra.turtle.DlMul");
		importNodeTypes.put("DAdd", "de.grogra.turtle.DAdd");
		importNodeTypes.put("DMul", "de.grogra.turtle.DMul");
		importNodeTypes.put("P", "de.grogra.turtle.P");
		importNodeTypes.put("Translate", "de.grogra.turtle.Translate");
		importNodeTypes.put("Scale", "de.grogra.turtle.Scale");
		importNodeTypes.put("Rotate", "de.grogra.turtle.Rotate");
		importNodeTypes.put("Axiom", "de.grogra.rgg.Axiom");
		importNodeTypes.put("Mesh", "de.grogra.imp3d.objects.MeshNode");
		importNodeTypes.put("Parallelogram", "de.grogra.imp3d.objects.Parallelogram");
		importNodeTypes.put("TextLabel", "de.grogra.imp3d.objects.TextLabel");
		importNodeTypes.put("PointCloud", "de.grogra.imp3d.objects.PointCloud");
		importNodeTypes.put("Polygon", "de.grogra.imp3d.objects.Polygon");
		importNodeTypes.put("NURBSCurve", "de.grogra.imp3d.objects.NURBSCurve");
		importNodeTypes.put("BezierSurface", "de.grogra.imp3d.objects.NURBSSurface");
		importNodeTypes.put("NURBSSurface", "de.grogra.ext.exchangegraph.graphnodes.ExchangeNURBSSurface");
		
		importNodeTypes.put("MtgVertex", "de.grogra.ext.exchangegraph.graphnodes.PropertyNodeImpl");
		
		importNodeTypes.put("TypeRoot", "de.grogra.rgg.TypeRoot");
		importNodeTypes.put("SRoot", "de.grogra.rgg.SRoot");
		importNodeTypes.put("SSubMetamer", "de.grogra.graph.impl.ScaleClass");
		importNodeTypes.put("SMetamer", "de.grogra.graph.impl.ScaleClass");
		importNodeTypes.put("SInternode", "de.grogra.graph.impl.ScaleClass");
		importNodeTypes.put("SGrowthUnit", "de.grogra.graph.impl.ScaleClass");
		importNodeTypes.put("STree", "de.grogra.graph.impl.ScaleClass");
		
		importNodeTypes.put("Supershape", "de.grogra.imp3d.objects.Supershape");
		importNodeTypes.put("HeightField", "de.grogra.imp3d.objects.Patch");
		
		xegNodeTypes.put(Node.class, XEGNode.class);
		xegNodeTypes.put(Null.class, XEGNode.class);
		xegNodeTypes.put(ShadedNull.class, XEGNode.class);
		xegNodeTypes.put(Box.class, XEGBox.class);
		xegNodeTypes.put(Cone.class, XEGCone.class);
		xegNodeTypes.put(Sphere.class, XEGSphere.class);
		xegNodeTypes.put(Cylinder.class, XEGCylinder.class);
		xegNodeTypes.put(Frustum.class, XEGFrustum.class);
		xegNodeTypes.put(F.class, XEGF.class);
		xegNodeTypes.put(F0.class, XEGF0.class);
		xegNodeTypes.put(M.class, XEGM.class);
		xegNodeTypes.put(M0.class, XEGM0.class);
		xegNodeTypes.put(RL.class, XEGRL.class);
		xegNodeTypes.put(RU.class, XEGRU.class);
		xegNodeTypes.put(RH.class, XEGRH.class);
		xegNodeTypes.put(V.class, XEGV.class);
		xegNodeTypes.put(Vl.class, XEGVl.class);
		xegNodeTypes.put(VlAdd.class, XEGVlAdd.class);
		xegNodeTypes.put(VlMul.class, XEGVlMul.class);
		xegNodeTypes.put(VAdd.class, XEGVAdd.class);
		xegNodeTypes.put(VMul.class, XEGVMul.class);
		xegNodeTypes.put(RV.class, XEGRV.class);
		xegNodeTypes.put(RV0.class, XEGRV0.class);
		xegNodeTypes.put(RG.class, XEGRG.class);
		xegNodeTypes.put(RD.class, XEGRD.class);
		xegNodeTypes.put(RO.class, XEGRO.class);
		xegNodeTypes.put(RP.class, XEGRP.class);
		xegNodeTypes.put(RN.class, XEGRN.class);
		xegNodeTypes.put(AdjustLU.class, XEGAdjustLU.class);
		xegNodeTypes.put(L.class, XEGL.class);
		xegNodeTypes.put(Ll.class, XEGLl.class);
		xegNodeTypes.put(LlAdd.class, XEGLlAdd.class);
		xegNodeTypes.put(LlMul.class, XEGLlMul.class);
		xegNodeTypes.put(LAdd.class, XEGLAdd.class);
		xegNodeTypes.put(LMul.class, XEGLMul.class);
		xegNodeTypes.put(D.class, XEGD.class);
		xegNodeTypes.put(Dl.class, XEGDl.class);
		xegNodeTypes.put(DlAdd.class, XEGDlAdd.class);
		xegNodeTypes.put(DlMul.class, XEGDlMul.class);
		xegNodeTypes.put(DAdd.class, XEGDAdd.class);
		xegNodeTypes.put(DMul.class, XEGDMul.class);
		xegNodeTypes.put(P.class, XEGP.class);
		xegNodeTypes.put(Translate.class, XEGTranslate.class);
		xegNodeTypes.put(Scale.class, XEGScale.class);
		xegNodeTypes.put(Rotate.class, XEGRotate.class);
		xegNodeTypes.put(Axiom.class, XEGAxiom.class);
		xegNodeTypes.put(MeshNode.class, XEGMeshNode.class);
		xegNodeTypes.put(Parallelogram.class, XEGParallelogram.class);
		xegNodeTypes.put(TextLabel.class, XEGTextLabel.class);
		xegNodeTypes.put(PointCloud.class, XEGPointCloud.class);
		xegNodeTypes.put(Polygon.class, XEGPolygon.class);
		xegNodeTypes.put(NURBSCurve.class, XEGNURBSCurve.class);
		xegNodeTypes.put(NURBSSurface.class, XEGBezierSurface.class);
		xegNodeTypes.put(ExchangeNURBSSurface.class, XEGNURBSSurface.class);

		xegNodeTypes.put(PropertyNodeImpl.class, XEGPropertyNode.class);
		
		xegNodeTypes.put(TypeRoot.class, XEGTypeRoot.class);
		xegNodeTypes.put(SRoot.class, XEGSRoot.class);
		xegNodeTypes.put(ScaleClass.class, XEGScaleClass.class);
		
		xegNodeTypes.put(Supershape.class, XEGSupershape.class);
		xegNodeTypes.put(Patch.class, XEGHeightField.class);
				
		exportNodeTypes.put("de.grogra.graph.impl.Node", "Node");
		exportNodeTypes.put("de.grogra.imp3d.objects.Null", "Node");
		//exportNodeTypes.put("de.grogra.imp3d.objects.ShadedNull", "Node");
		exportNodeTypes.put("de.grogra.imp3d.objects.Box", "Box");
		exportNodeTypes.put("de.grogra.imp3d.objects.Cone", "Cone");
		exportNodeTypes.put("de.grogra.imp3d.objects.Sphere", "Sphere");
		exportNodeTypes.put("de.grogra.imp3d.objects.Cylinder", "Cylinder");
		exportNodeTypes.put("de.grogra.imp3d.objects.Frustum", "Frustum");
		exportNodeTypes.put("de.grogra.turtle.F", "F");
		exportNodeTypes.put("de.grogra.turtle.F0", "F0");
		exportNodeTypes.put("de.grogra.turtle.M", "M");
		exportNodeTypes.put("de.grogra.turtle.M0", "M0");
		exportNodeTypes.put("de.grogra.turtle.RL", "RL");
		exportNodeTypes.put("de.grogra.turtle.RU", "RU");
		exportNodeTypes.put("de.grogra.turtle.RH", "RH");
		exportNodeTypes.put("de.grogra.turtle.V", "V");
		exportNodeTypes.put("de.grogra.turtle.Vl", "Vl");
		exportNodeTypes.put("de.grogra.turtle.VlAdd", "VlAdd");
		exportNodeTypes.put("de.grogra.turtle.VlMul", "VlMul");
		exportNodeTypes.put("de.grogra.turtle.VAdd", "VAdd");
		exportNodeTypes.put("de.grogra.turtle.VMul", "VMul");
		exportNodeTypes.put("de.grogra.turtle.RV", "RV");
		exportNodeTypes.put("de.grogra.turtle.RV0", "RV0");
		exportNodeTypes.put("de.grogra.turtle.RG", "RG");
		exportNodeTypes.put("de.grogra.turtle.RD", "RD");
		exportNodeTypes.put("de.grogra.turtle.RO", "RO");
		exportNodeTypes.put("de.grogra.turtle.RP", "RP");
		exportNodeTypes.put("de.grogra.turtle.RN", "RN");
		exportNodeTypes.put("de.grogra.turtle.AdjustLU", "AdjustLU");
		exportNodeTypes.put("de.grogra.turtle.L", "L");
		exportNodeTypes.put("de.grogra.turtle.Ll", "Ll");
		exportNodeTypes.put("de.grogra.turtle.LlAdd", "LlAdd");
		exportNodeTypes.put("de.grogra.turtle.LlMul", "LlMul");
		exportNodeTypes.put("de.grogra.turtle.LAdd", "LAdd");
		exportNodeTypes.put("de.grogra.turtle.LMul", "LMul");
		exportNodeTypes.put("de.grogra.turtle.D", "D");
		exportNodeTypes.put("de.grogra.turtle.Dl", "Dl");
		exportNodeTypes.put("de.grogra.turtle.DlAdd", "DlAdd");
		exportNodeTypes.put("de.grogra.turtle.DlMul", "DlMul");
		exportNodeTypes.put("de.grogra.turtle.DAdd", "DAdd");
		exportNodeTypes.put("de.grogra.turtle.DMul", "DMul");
		exportNodeTypes.put("de.grogra.turtle.P", "P");
		exportNodeTypes.put("de.grogra.turtle.Translate", "Translate");
		exportNodeTypes.put("de.grogra.turtle.Scale", "Scale");
		exportNodeTypes.put("de.grogra.turtle.Rotate", "Rotate");
		exportNodeTypes.put("de.grogra.rgg.Axiom", "Axiom");
		exportNodeTypes.put("de.grogra.imp3d.objects.MeshNode", "Mesh");
		exportNodeTypes.put("de.grogra.imp3d.objects.Parallelogram", "Parallelogram");
		exportNodeTypes.put("de.grogra.imp3d.objects.TextLabel", "TextLabel");
		exportNodeTypes.put("de.grogra.imp3d.objects.PointCloud", "PointCloud");
		exportNodeTypes.put("de.grogra.imp3d.objects.Polygon", "Polygon");
		exportNodeTypes.put("de.grogra.imp3d.objects.NURBSCurve", "NURBSCurve");
		exportNodeTypes.put("de.grogra.imp3d.objects.ShadedNull", "ShadedNull");
		exportNodeTypes.put("de.grogra.imp3d.objects.NURBSSurface", "BezierSurface");
		exportNodeTypes.put("de.grogra.ext.exchangegraph.graphnodes.ExchangeNURBSSurface", "NURBSSurface");
		
		
		exportNodeTypes.put("de.grogra.ext.exchangegraph.graphnodes.PropertyNodeImpl", "MtgVertex");
		// not needed, because the type graph will be removed while exporting
		exportNodeTypes.put("de.grogra.rgg.TypeRoot", "TypeRoot");
		exportNodeTypes.put("de.grogra.rgg.SRoot", "SRoot");
		exportNodeTypes.put("de.grogra.graph.impl.ScaleClass", "SSubMetamer");
		exportNodeTypes.put("de.grogra.graph.impl.ScaleClass", "SMetamer");
		exportNodeTypes.put("de.grogra.graph.impl.ScaleClass", "SInternode");
		exportNodeTypes.put("de.grogra.graph.impl.ScaleClass", "SGrowthUnit");
		exportNodeTypes.put("de.grogra.graph.impl.ScaleClass", "STree");
		
		exportNodeTypes.put("de.grogra.imp3d.objects.Supershape", "Supershape");
		exportNodeTypes.put("de.grogra.imp3d.objects.Patch", "HeightField");
	}
	
	/**
	 * Map contains the link between XEG node ids and the corresponding GroIMP
	 * nodes. It is used to set the same ids for export like for import for same nodes. 
	 */
	private BidirectionalHashMap<Long, Node> nodeMap = new BidirectionalHashMap<Long, Node>();
	
	/**
	 * Map contains the link between XEG edge ids and the corresponding GroIMP
	 * edges. It is used to set the same ids for export like for import for same edges. 
	 */
	private BidirectionalHashMap<Long, Edge> edgeMap = new BidirectionalHashMap<Long, Edge>();
	
	/**
	 * Map contains specific edge types read for export which were read
	 * during import. Can be empty if no import was done. 
	 */
	private BidirectionalHashMap<Integer, String> edgeTypes = new BidirectionalHashMap<Integer, String>();
	
	
	private HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge> fromRootDstIdEdgeMap = new HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge>();
	
	
	/**
	 * Returns the {@link #nodeMap node map}.
	 * @return
	 */
	public BidirectionalHashMap<Long, Node> getNodeMap() {
		return nodeMap;
	}

	/**
	 * Returns the {@link #edgeMap edge map}.
	 * @return edge map
	 */
	public BidirectionalHashMap<Long, Edge> getEdgeMap() {
		return edgeMap;
	}
	
	/**
	 * Returns the {@link #edgeTypes edge types map}.
	 * @return edge types map
	 */
	public BidirectionalHashMap<Integer, String> getEdgeTypes() {
		return edgeTypes;
	}
	
	public HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge> getFromRootDstIdEdgeMap() {
		return fromRootDstIdEdgeMap;
	}
    
}
