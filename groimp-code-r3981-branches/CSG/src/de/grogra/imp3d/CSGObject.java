package de.grogra.imp3d;

import javax.media.opengl.GL;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Matrix4d;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.IntList;

import de.grogra.imp3d.HalfEdgeStructCSG;
import de.grogra.imp3d.shading.Shader;

public class CSGObject extends HalfEdgeStructCSG {

	public static final int CSG_UNION = 0;
	public static final int CSG_INTERSECTION = 1;
	public static final int CSG_DIFFERENCE = 2;
	public static final int CSG_COMPLEMENT = 3;

	final static double EPSILON = 0.00001f;
	
	/**
	 * 
	 */

	HalfEdgeStructCSG object1;
	HalfEdgeStructCSG object2;

	/**
	 * 
	 */

	int type = 0;
	
	/*
	 * local Transformation for object 1 and 2
	 */
	
	Matrix4d t1 = new Matrix4d();
	Matrix4d t2 = new Matrix4d();

	/**
	 * 
	 */

	public CSGObject() {
	}

	/**
	 * 
	 */

	public CSGObject(HalfEdgeStructCSG ob1, HalfEdgeStructCSG ob2, int typ) {
		object1 = ob1;
		object2 = ob2;
		type = typ;
	}
	
	public void addPrimitive(HalfEdgeStructCSG obj, Matrix4d t, int typ){
		if(object1==null){
//			System.out.println("Add first Object");
			object1 = obj;
			t1.set (t);
			type=typ;
			//TODO: set Transformation
			this.set (obj);
		} else if(object2==null){
//			System.out.println("Add second Object");
			object2 = obj;
			t2.set (t);
//			System.out.println ("Intersecting");
			
			try
			{
				intersect();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			type=typ;
		} else{
//			System.out.println("Add third Object");
			object1.clear();
			object1.set (this);
			this.clear ();
			t1.setIdentity ();
			object2.clear ();
			object2.set (obj);
			t2.set (t);
			type=typ;
//			System.out.println ("Intersecting");
			try
			{
				intersect();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */

	public CSGObject(HalfEdgeStructCSG ob1, Matrix4d t1, HalfEdgeStructCSG ob2,
			Matrix4d t2, int typ) {
		object1 = ob1;
		object2 = ob2;
		type = typ;
		this.t1.set(t1);
		this.t2.set(t2);
	}

	/**
	 * 
	 */

	public void setTransformation(Matrix4d t1, Matrix4d t2) {

//		System.out.println("t1");
//		System.out.println(this.t1);
//		System.out.println(t1);
//		System.out.println(this.t1.equals(t1));
//
//		System.out.println("t2");
//		System.out.println(this.t2);
//		System.out.println(t2);
//		System.out.println(this.t2.equals(t2));

		if ((!this.t1.equals(t1)) || (!this.t2.equals(t2))) {
			hasUpdated = false;
			this.t1.set(t1);
			this.t2.set(t2);
		}
	}

	/**
	 * 
	 */

	public boolean isUpdated() {
		return hasUpdated;
	}

	/**
	 * 
	 */
	
	public void intersect(){
		
		// workaroudn for null objects
//		if( (object1==null || object1.getFacesCount ()==0) && object2!=null){
//			this.getObject (object2);
//			return;
//		}
//		if(object1!=null && ( object2==null || object2.getFacesCount ()==0)){
//			this.getObject (object1);
//			return;
//		}
//		if( (object1==null || object1.getFacesCount ()==0) && (object2==null || object2.getFacesCount ()==0)){
//			return;
//		}
		
		
		boolean debug = false;
		
		boolean bench=false;
		
		//for debuging only
		this.createLink ();
		
		if(debug){
			if(type==CSG_INTERSECTION)System.out.println("CSG_INTERSECTION");
			if(type==CSG_UNION)System.out.println("CSG_UNION");
			if(type==CSG_DIFFERENCE)System.out.println("CSG_DIFFERENCE");
		}
		
		long startTime = System.currentTimeMillis ();
		
		
		this.clear();
		
		//some tests
//		t1.mul (10.d);
//		t2.mul (10.d);
//		
//		
//		//apply the local transformation for booth objects
		object1.transform(t1);
		object2.transform(t2);
//		
//		t1.mul (1.d/10.d);
//		t2.mul (1.d/10.d);
		
		
		Tuple3d isectpt1 = new Point3d();
		Tuple3d isectpt2 = new Point3d();
		
		
		if(bench||debug){
			System.out.println ("--> Creating the Links");
		}
		
		//create the Links and add the exterior
		
		object1.createLink ();
		object1.addExteriorToLink ();
		if(!object1.getLink ().verifyLink ()){
			System.out.println ("Link of Object 1 problem");
			System.out.println(object1.getLink ());
		}

		object2.createLink ();
		object2.addExteriorToLink();
		if(!object2.getLink ().verifyLink ()){
			System.out.println ("Link of Object 2 problem");
			System.out.println(object2.getLink ());
		}
		
		long creatingLinksTime = System.currentTimeMillis ();
		
		
		//create the intersection Object
		IntersectionLine intersectionLineObj = new IntersectionLine();
		
		if(bench||debug){
			System.out.println("--> Checking for Intersections");
		}
		
		
		// find all the intersections
		for (int i = 0; i < object1.getFacesCount(); i++) {
			for (int j = 0; j < object2.getFacesCount(); j++) {
				int intersection = object1.triangleTriangleIntersection(i, object2.faceGetVertexW(j), object2.faceGetVertexU(j), object2.faceGetVertexV(j), isectpt1, isectpt2);
				
				if (intersection==1) {
					
					Tuple2f uvObj1isectpt1 = object1.computeUV (i, isectpt1);
					Tuple2f uvObj1isectpt2 = object1.computeUV (i, isectpt2);
					
					Tuple2f currentUV = object1.InterpolateTexData (i, uvObj1isectpt1);
					currentUV = object1.InterpolateTexData (i, uvObj1isectpt2);
					
					Tuple3f currentNormal = object1.InterpolateNormal (i, uvObj1isectpt1);
					currentNormal = object1.InterpolateNormal (i, uvObj1isectpt2);
					
					
					Tuple2f uvObj2isectpt1 = object2.computeUV (j, isectpt1);
					Tuple2f uvObj2isectpt2 = object2.computeUV (j, isectpt2);
					
					currentUV = object2.InterpolateTexData (j, uvObj2isectpt1);
					currentUV = object2.InterpolateTexData (j, uvObj2isectpt2);
					
					currentNormal = object2.InterpolateNormal (j, uvObj2isectpt1);
					currentNormal = object2.InterpolateNormal (j, uvObj2isectpt2);
					
					boolean startExt=false;
					boolean endExt=false;
					
					if(object1.liesOnExteriorEdge (i, uvObj1isectpt1) || object2.liesOnExteriorEdge (j, uvObj2isectpt1)){
						//the starting vertex lies on an exterior edge
						startExt=true;
					}
					if(object1.liesOnExteriorEdge (i, uvObj1isectpt2) || object2.liesOnExteriorEdge (j, uvObj2isectpt2)){
						//the ending vertex lies on an exterior edge
						endExt=true;
					}
					
					//add the current segment with all the load data to the intersection line
					intersectionLineObj.insertSegment (isectpt1, isectpt2, object1.getFaceCluster (i) , object2.getFaceCluster (j), 
						object1.InterpolateTexData (i, uvObj1isectpt1), object1.InterpolateTexData (i, uvObj1isectpt2), 
						object2.InterpolateTexData (j, uvObj2isectpt1), object2.InterpolateTexData (j, uvObj2isectpt2), 
						object1.InterpolateNormal (i, uvObj1isectpt1), object1.InterpolateNormal (i, uvObj1isectpt2), 
						object2.InterpolateNormal (j, uvObj2isectpt1), object2.InterpolateNormal (j, uvObj2isectpt2), 
						startExt, endExt);
				}
				else if(intersection==2){
					//The Triangles are co-planar
					if(debug){
						System.out.println("Face "+i+" of object1 and Face "+j+" of object 2 are coplanar");
					}
					//check if the to triangles are the same, opposite or just in the same plane
					
				}
			}
		}
		
		
		long intersectionTime = System.currentTimeMillis ();
		
		
		if(bench||debug){
			System.out.println ("--> Inserting the intersection Lines");
		}
		
		//after all segments of the intersection line has been found, remove the interior vertices
		
		intersectionLineObj.removeInteriorVertices ();
		
		if(bench||debug){
			System.out.println ("---> Adding the intersection Line to Object 1");
		}
		//for debugging only
		if (type == 4){
			object1.getLink().addIntersectionVertices(intersectionLineObj.getVertices ());
			System.out.println ("Object 1 state");
			System.out.println(object1.getLink());
			object1.getLink().addIntersectionVertices(intersectionLineObj.getVertices ());
			object1.addIntersectionLine2(intersectionLineObj.getIntersectionLine (), 
				intersectionLineObj.getClusters1 (), intersectionLineObj.getNormalData1 () , intersectionLineObj.getUVData1 ());
			System.out.println ("Object 1 state after adding");
//			object1.getLink().printHalfEdges ();
		}
			else{
				if(type!=CSG_COMPLEMENT){
				object1.getLink().addIntersectionVertices(intersectionLineObj.getVertices ());
				object1.addIntersectionLine	(intersectionLineObj.getIntersectionLine (), 
						intersectionLineObj.getClusters1 (), intersectionLineObj.getNormalData1 () , intersectionLineObj.getUVData1 () );
				object1.insertLink ();
				}
			}
			
			
			if (type == CSG_COMPLEMENT){
				System.out.println("Object 2 state");
				System.out.println(object2.getLink ());
				object2.getLink().addIntersectionVertices(intersectionLineObj.getVertices ());
				object2.addIntersectionLine2(intersectionLineObj.getIntersectionLine (), 
				intersectionLineObj.getClusters2 (), intersectionLineObj.getNormalData2 () , intersectionLineObj.getUVData2 ());
			}else{
				if (type!=4){
				object2.getLink().addIntersectionVertices(intersectionLineObj.getVertices ());
//			object2.addIntersectionLine(intersectionLine, intersectionClustersObj2 , normalsObj2 , uvObj2);
				object2.addIntersectionLine(intersectionLineObj.getIntersectionLine (), 
				intersectionLineObj.getClusters2 (), intersectionLineObj.getNormalData2 () , intersectionLineObj.getUVData2 () );
				object2.insertLink ();
				}
			}
//			System.out.println("Object1 after");
//			object1.link.printHalfEdges ();
		
			if(bench||debug){
				System.out.println ("---> Inserting the Link to Object 2");
			}
			
			
//			System.out.println("Object2 after");
//			object2.link.printHalfEdges ();
		
			long insertingLineTime = System.currentTimeMillis ();
		
		
		if(bench||debug){
			System.out.println ("--> Classifiing the triangles");
		}
		
//		if(didIntersect){
		
		//classify border triangles
		
//		classifyTriangles(object1,object2);
//		classifyTriangles(object2,object1);

		
		//		
//		if(debug){System.out.println("checking Object1 classification inside and outside");}
//		boolean classObj1 = checkClassificationForIO(object1);
//		if(debug){System.out.println("checking Object2 classification inside and outside");}
//		boolean classObj2 =checkClassificationForIO(object2);
		
		//check if there exist at least one triangle with status 
		//outside and one with status inside in booth objects
		
		
		
		//classify the rest of the triangles
//		neigborhoodClassification(object1);
//		neigborhoodClassification(object2);
		

		if(debug){System.out.println("checking Object1 classification updating and undefined");}
		boolean classObj1 = checkClassificationForU(object1);
		if(debug){System.out.println("checking Object2 classification updating and undefined");}
		boolean classObj2 = checkClassificationForU(object2);
		
		if(!classObj1){
			object1.classifyFacesByRayCasting (object2);
			if (debug)
			{
				System.out.println ("Object 1 has "
					+ object1.triangleStatus.size () + " faces");
				System.out.println (object1.triangleStatus);
			}
		}
		if(!classObj2){
			object2.classifyFacesByRayCasting (object1);
			if (debug)
			{
				System.out.println (object2.triangleStatus);
				System.out.println ("Object 2 has "
					+ object2.triangleStatus.size () + " faces");
			}
		}
		
//		}//end didIntersect
		
		long classificationTime = System.currentTimeMillis ();
		
		
		//get the triangles which should be displayed
		
		if(bench||debug){
			System.out.println ("--> Selecting the triangles to display");
		}
		
		if (type == CSG_UNION) {
			
			getOutsideTriangles(object1);
			getSameTriangles(object1);	
			getOutsideTriangles(object2);
		}

		if (type == CSG_DIFFERENCE) {

			getOutsideTriangles(object1);
			getOppositeTriangles(object1);
			getInsideReversedTriangles(object2);
		}

		if (type == CSG_INTERSECTION) {

			getInsideTriangles(object1);
			getSameTriangles(object2);
			getInsideTriangles(object2);
		}
		
		if (type == CSG_COMPLEMENT){
			this.link=object2.link;
			}
		
		if (type == 4){
			this.link=object1.link;		}
		
		
			long endTime = System.currentTimeMillis ();
		
		
		if(bench||debug){
			System.out.println ("--> Operation complete");
			
			endTime=endTime-classificationTime;
			classificationTime=classificationTime-insertingLineTime;
			insertingLineTime=insertingLineTime-intersectionTime;
			intersectionTime=intersectionTime-creatingLinksTime;
			creatingLinksTime=creatingLinksTime-startTime;
			
			System.out.println ("Time to create Link");
			System.out.println(creatingLinksTime+" milliseconds");
			
			System.out.println ("Time to calculate intersections");
			System.out.println(intersectionTime+" milliseconds");
			
			
			System.out.println ("Time to insert the intersection-line");
			System.out.println(insertingLineTime+" milliseconds");
			
			System.out.println ("Time to classify triangles");
			System.out.println(classificationTime+" milliseconds");
			
			System.out.println ("Time to select triangles");
			System.out.println(endTime+" milliseconds");
			
		}
//		if(!this.validateStatus ()){
//			System.err.println ("CSG after");
//			this.printState ();
//			}
		
//		System.out.println("CSG done");
		
		//test
		
//		Matrix4d back = new Matrix4d();
//		back.setIdentity ();
//		back.mul (1.d/10.d);
//		
//		this.transform (back);
		
//		this.printState ();
//		this.printState ();
		
		//for debug only
//		getObject(object1);
//		getObject(object2);
		
//		this.printState ();
		
//		link = new CSGLink(object2.link);
//		clusters = object2.clusters;
//		this.link=object1.link;
//		this.createLink ();
		
//		addIntersectionLine(intersectionLine, intersectionClustersObj1);
		
	}
	
	

	private void neigborhoodClassification(HalfEdgeStructCSG object){
		for (int i = 0 ; i<object.getFacesCount () ; i++){
			if(object.triangleStatus.get (i)==UNDEFINED){
				object.getNeigborhoodClassification (i);
			}
		}
	}
	
	private void classifyTriangles(HalfEdgeStructCSG object1, HalfEdgeStructCSG object2){
		
		for (int i=0 ; i<object1.getFacesCount () ; i++){
			
			boolean debug = false;
			
			boolean wu = false;
			boolean wv = false;
			boolean uv = false;
			
			if( object1.heGetStatus ( object1.faceGetHe ( i , 0) ) == HalfEdgeStructCSG.BORDER){
					//There could be an Edge between w and u
					wv=true;
					
			} 
			if ( object1.heGetStatus ( object1.faceGetHe ( i , 2) ) == HalfEdgeStructCSG.BORDER){
					//There could be an Edge between w and v
					uv=true;
					
			} 
			if ( object1.heGetStatus (object1.faceGetHe ( i , 1) ) == HalfEdgeStructCSG.BORDER ){
					//There could be an Edge between u and v
					wu = true;
			}
			if(debug){
			if(wu||wv||uv){
				System.out.println ("classifiing face "+i);
			}
			}
			if(wu){
				int he = object2.getHeFT( object2.getVertexPos ( object1.faceGetVertexW ( i ) ), 
					object2.getVertexPos ( object1.faceGetVertexU ( i ) ));
				if (he>-1){
					if (debug)
					{
						System.out.println ("wu");
						System.out.println ("w =" + object1.faceGetVertexW (i));
						System.out.println ("u =" + object1.faceGetVertexU (i));
					}
					object1.triangleStatus.set ( i , object2.getClassification (object1.faceGetVertexV ( i ),object1.faceCalculateNormal ( i ), he) );
				}
				
			}
			
			if(wv && object1.triangleStatus.get (i)==UNDEFINED){
				int he = object2.getHeFT( object2.getVertexPos ( object1.faceGetVertexV ( i ) ), 
					object2.getVertexPos ( object1.faceGetVertexW ( i ) ));
				if (he>-1){
					if (debug)
					{
						System.out.println ("wv");
						System.out.println ("w =" + object1.faceGetVertexW (i));
						System.out.println ("v =" + object1.faceGetVertexV (i));
					}
					object1.triangleStatus.set ( i , object2.getClassification (object1.faceGetVertexU ( i ),object1.faceCalculateNormal ( i ), he) );
				}
				
			}
			
			if(uv && object1.triangleStatus.get (i)==UNDEFINED){
				int he = object2.getHeFT( object2.getVertexPos ( object1.faceGetVertexU ( i ) ), 
					object2.getVertexPos ( object1.faceGetVertexV ( i ) ));
				if (he>-1){
					if (debug)
					{
						System.out.println ("uv");
						System.out.println ("u =" + object1.faceGetVertexU (i));
						System.out.println ("v =" + object1.faceGetVertexV (i));
					}
					object1.triangleStatus.set ( i , object2.getClassification (object1.faceGetVertexW ( i ),object1.faceCalculateNormal ( i ), he) );
				}
				
			}
			if(debug){
				if(wu||wv||uv){
					System.out.println ("face "+i+" classified as "+triangleStatus.get (i));
				}
				}
			
			
		}
		
	}
	
	public boolean checkClassificationForIO(HalfEdgeStructCSG heStruct){
		boolean debug = false;
		
		boolean inside = false;
		boolean outside = false;
		
		for (int i=0 ; i<heStruct.getFacesCount () ; i++){
			if(!inside && heStruct.triangleStatus.get (i)==INSIDE){
				inside=true;
			}
			if(!outside && heStruct.triangleStatus.get (i)==OUTSIDE){
				outside=true;
			}
			if(inside && outside){
				return true;
			}
		}
		
		System.out.println(heStruct.triangleStatus);
		
		return false;
		
	}
	
	public boolean checkClassificationForU(HalfEdgeStructCSG heStruct){
		boolean debug = false;
		
		for (int i=0 ; i<heStruct.getFacesCount () ; i++){
			if(heStruct.triangleStatus.get (i)==UNDEFINED || heStruct.triangleStatus.get(i)==UPDATING){
//				System.out.println ("Classification Check failed");
//				System.out.println(heStruct.triangleStatus);
				return false;
			}
		}
		return true;
	}
	
}
