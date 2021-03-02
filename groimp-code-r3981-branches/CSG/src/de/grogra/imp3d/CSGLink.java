
package de.grogra.imp3d;

import javax.media.opengl.GL;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Point3f;

import com.sun.opengl.util.GLUT;

import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

public class CSGLink
{

	final static double EPSILON_d = 0.000001d;
	final static double EPSILON_D_VERT = 0.000001d;
	final static float EPSILON_f = 0.00001f;
	
	
	final static byte UNDEFINED = 0;
	final static byte BORDER = 1;
//	final static byte INSIDE = 2;
//	final static byte OUTSIDE = 3;
//	final static byte UPDATING = 4;

	IntList faces = new IntList ();
	public DoubleList vertices = new DoubleList ();
	
	int startOfBorder = 0;
	
	IntList triangleToShader = new IntList();
	/*
	 * stores one incoming halfEdge to this vertex*/
	IntList vertexToHalfEdge = new IntList ();
	DoubleList clusterNormal = new DoubleList ();

	/*
	 * The halfEdges array 
	 * 1	the destination vertex
	 * 2	the face-number
	 * 3	the position of the twin
	 * 4	the position of the previous HalfEdge
	 * 5	the position of the next HalfEdge
	 * */

	IntList halfEdges = new IntList ();
	ByteList status = new ByteList ();

	ByteList heStatus = new ByteList ();

	FloatList normals = new FloatList();
	public FloatList uv = new FloatList();
	
	public CSGLink (DoubleList vertices)
	{
		this.vertices = vertices;
		//		System.out.println("Sizes");
		//		System.out.println(vertices.size());
		//		System.out.println(this.vertices.size());
		for (int i = 0; i < vertices.size () / 3; i++)
		{
			vertexToHalfEdge.push (-1);
		}
		startOfBorder=vertices.size ()/3;
		//		System.out.println(verticesEdges.size());

	}

	public CSGLink (CSGLink link)
	{
		// TODO Auto-generated constructor stub
		halfEdges.addAll (link.halfEdges);
		vertices.addAll (link.vertices);
		clusterNormal.addAll (link.clusterNormal);
	}

	//method for adding the intersection segment to the Link

	public void addEdge (Tuple3d from, Tuple3d to)
	{
		int fromPos = -1;
		int toPos = -1;

		Tuple3d vert = new Point3d ();

		for (int i = 0; vertices.size () / 3 < i; i++)
		{
			vert.setX (vertices.get (i * 3));
			vert.setY (vertices.get (i * 3 + 1));
			vert.setZ (vertices.get (i * 3 + 2));

			if (fromPos < 0 && vert.epsilonEquals (from, EPSILON_D_VERT))
			{
				fromPos = i;
			}
			else if (toPos < 0 && vert.epsilonEquals (to, EPSILON_D_VERT))
			{
				toPos = i;
			}
			if (fromPos > -1 && toPos > -1)
				break;
		}

		if (fromPos < 0)
		{
			fromPos = vertices.size () / 3;
			vertices.add (from.x);
			vertices.add (from.y);
			vertices.add (from.z);
		}

		if (toPos < 0)
		{
			toPos = vertices.size () / 3;
			vertices.add (to.x);
			vertices.add (to.y);
			vertices.add (to.z);
		}

		int halfEdgePos = halfEdges.size () / 2;
		halfEdges.add (fromPos);
		halfEdges.add (toPos);

	}

	public void splitInMonotoneParts (int clusterNo)
	{

		int axes[] = getAxes (clusterNo);

		int x = axes[0];
		int y = axes[1];

		IntList verticesInCluster = getVerticesOfCluster (clusterNo);

		DoubleList verticesX = getVerticesCoordinates (verticesInCluster, x);
		DoubleList verticesY = getVerticesCoordinates (verticesInCluster, y);

		sortLists (verticesInCluster, verticesY, verticesX);

		splitBotToTop (verticesInCluster, verticesX, verticesY, x,
			y, clusterNo);

		reverseLists (verticesInCluster, verticesX, verticesY);

		splitTopToBot (verticesInCluster, verticesX, verticesY, x,
			y, clusterNo);

		createNewFace (clusterNo);

	}
	public void monotonate2 (int clusterNo)
	{

		int axes[] = getAxes (clusterNo);

		int x = axes[0];
		int y = axes[1];

		IntList verticesInCluster = getVerticesOfCluster (clusterNo);

		DoubleList verticesX = getVerticesCoordinates (verticesInCluster, x);
		DoubleList verticesY = getVerticesCoordinates (verticesInCluster, y);

		sortLists (verticesInCluster, verticesY, verticesX);

		splitBotToTop (verticesInCluster, verticesX, verticesY, x,
			y, clusterNo);

		reverseLists (verticesInCluster, verticesX, verticesY);

		splitTopToBot (verticesInCluster, verticesX, verticesY, x,
			y, clusterNo);

		createNewFace (clusterNo);

	}

	public void createNewFace (int clusterNo)
	{
		int firstHe = -1;

		for (int i = 0; i < heGetCount(); i++)
		{
			if (heGetFace (i) == clusterNo)
			{
				firstHe = i;
				heSetFace (i, -2);
				i = heGetNext (i);
				while (i != firstHe)
				{
//					if(i<0){ System.out.println("Error while creating new Faces");
//								this.printHalfEdges ();
//						break;}
					heSetFace (i, -2);
					i = heGetNext (i);
				}
				break;
			}
		}

		int newFace = clusterNormal.size () / 3;

		for (int i = firstHe + 1; i < heGetCount(); i++)
		{
			if (heGetFace (i) == clusterNo)
			{
				int start = i;
				heSetFace (i, newFace);
				clusterNormal.push (clusterNormal.get (clusterNo * 3),
					clusterNormal.get (clusterNo * 3 + 1),
					clusterNormal.get (clusterNo * 3 + 2));
				
				triangleToShader.push (triangleToShader.get (clusterNo));
				
				i = heGetNext (i);
				while (i != start)
				{
//					if(i<0){
//						System.out.println("error while adding faces");
//						this.printHalfEdges ();
//					}
					heSetFace (i, newFace);
					i = heGetNext (i);
				}
				newFace++;
			}
		}

		if (firstHe == -1)
		{
			//TODO:
			//Workaround why can it be -1
		}
		else
		{
			heSetFace (firstHe, clusterNo);
			int i = heGetNext (firstHe);
			while (i != firstHe)
			{
				heSetFace (i, clusterNo);
				i = heGetNext (i);
			}
		}

	}

	private void splitBotToTop (IntList verticesInCluster,
			DoubleList verticesX, DoubleList verticesY, int x, int y,
			int clusterNo)
	{

		for (int i = 1; i < verticesInCluster.size (); i++)
		{
			if (!isAdjecentToList (verticesInCluster,
				verticesInCluster.get (i), i))
			{
				
				
				double vertX = verticesX.get (i);
				double vertY = verticesY.get (i);

				double yLeftEdge = vertY;
				double xLeftEdgeTop=0.0d;
				double yLeftEdgeTop=0.0d;;
				double xLeftEdge = Double.NEGATIVE_INFINITY;
				double yRightEdge = vertY;
				double xRightEdgeTop=0.0d;
				double yRightEdgeTop=0.0d;
				double xRightEdge = Double.POSITIVE_INFINITY;
				
				int vertLeft = -1;
				int vertRight = -1;

				for (int j = 0; j < i; j++)
				{
					
					//for every checked vertex

					double sVertX = verticesX.get (j);
					double sVertY = verticesY.get (j);
					
					//alternative float sVertX = getVertexCoordinate(j,x, false);
					
					int outgoingHalfEdges [] = this.vertexGetOHeArray (verticesInCluster.get (j));
					
//					int neigh = vertexNeigborsCount (verticesInCluster.get (j));
					
					int neigh = outgoingHalfEdges.length;
					
//					int oHe = vertexGetOHe (verticesInCluster.get (j));
					for (int k = 0; k < neigh; k++)
					{
						int oHe = outgoingHalfEdges[k];

						int currentVert = heGetVertex (oHe);

						if ((heGetFace (oHe) == clusterNo || heGetFace (heGetTwin (oHe)) == clusterNo)
							&& getVertexCoordinate (currentVert, y, false) >= vertY)
						{
							
							double newX = calculateIntersection (sVertX, sVertY,
								getVertexCoordinate (currentVert, x, false),
								getVertexCoordinate (currentVert, y, false),
								vertY);
							if (newX < vertX)
							{
								
								//intersection Point lies left of the current Vertex
								if (newX > xLeftEdge)
								{	
									xLeftEdge = newX;
									vertLeft = verticesInCluster.get (j);
									yLeftEdgeTop= verticesY.get (j);//getVertexCoordinate (currentVert, x, false);
									xLeftEdgeTop= verticesX.get (j);
								}
							}
							else
							{
								//intersection Point lies right of the current Vertex
								if (newX < xRightEdge)
								{
									xRightEdge = newX;
									vertRight = verticesInCluster.get (j);
									yRightEdgeTop = verticesY.get (j);//getVertexCoordinate (currentVert, y, false);
									xRightEdgeTop = verticesX.get (j);
								}

							}
						}
					}
				}
				
				//yLeft=yLeft-vertY;
				//if(yLeft<1.0f){yLeft=-yLeft;}
				//yRight=yRight-vertY;
				//if(yRight<1.0f){yRight=-yRight;}
				
				if (vertLeft == -1 && vertRight >= 0)
				{
					
					addEdgeBetween (verticesInCluster.get (i), vertRight,
						clusterNo);
				}
				else if (vertRight == -1 && vertLeft >= 0)
				{
					addEdgeBetween (verticesInCluster.get (i), vertLeft,
						clusterNo);
				}
				else if (vertLeft == -1 && vertRight == -1)
				{
				}
				else
				{
					double maxEdgeVertexY;
					int maxVertex;
					
					if (yRightEdgeTop>yLeftEdgeTop)
					{
						//vertexLeft is the one with the smaller x-coordinate
						//addEdgeBetween (verticesInCluster.get (i), vertRight,
						//	clusterNo);
						maxEdgeVertexY=yRightEdgeTop;
						maxVertex=vertRight;
					}
					else
					{
						//addEdgeBetween (verticesInCluster.get (i), vertLeft,
						//	clusterNo);
						maxEdgeVertexY=yLeftEdgeTop;
						maxVertex=vertLeft;
					}
										
					if(maxEdgeVertexY-vertY < - EPSILON_d || maxEdgeVertexY-vertY > EPSILON_d){
					
					for (int j=0 ; j<i ; j++){

						if( j!= i && verticesY.get (j) < vertY - EPSILON_d && verticesY.get (j) > maxEdgeVertexY /*- EPSILON_d*/){
							if(IntersectionTests.leftOf(yLeftEdgeTop, xLeftEdgeTop, yLeftEdge, xLeftEdge , verticesY.get (j),verticesX.get (j)) &&
									IntersectionTests.rightOf( yRightEdgeTop, xRightEdgeTop, yRightEdge, xRightEdge , verticesY.get (j),verticesX.get (j) ) ){
								maxVertex=verticesInCluster.get(j);
								maxEdgeVertexY=verticesY.get (j);
							}
							
						}
					}
					}
					else {
						maxVertex=verticesInCluster.get (i-1);
					}
					
					addEdgeBetweenForce (verticesInCluster.get (i), maxVertex ,
						clusterNo);
				}

			}
		}

	}

	private void splitTopToBot (IntList verticesInCluster,
			DoubleList verticesX, DoubleList verticesY, int x, int y,
			int clusterNo)
	{
		
		for (int i = 1; i < verticesInCluster.size (); i++)
		{
			if (!isAdjecentToList (verticesInCluster,
				verticesInCluster.get (i), i))
			{

				double vertX = verticesX.get (i);
				double vertY = verticesY.get (i);

				double yLeftEdge = vertY;
				double xLeftEdgeTop=0.0d;
				double yLeftEdgeTop=0.0d;;
				double xLeftEdge = Double.POSITIVE_INFINITY;
				double yRightEdge = vertY;
				double xRightEdgeTop=0.0d;;
				double yRightEdgeTop=0.0d;;
				double xRightEdge = Double.NEGATIVE_INFINITY;

				int vertLeft = -1;
				int vertRight = -1;

				for (int j = 0; j < i; j++)
				{
					//for every checked vertex

					double sVertX = verticesX.get (j);
					double sVertY = verticesY.get (j);
					//alternative float sVertX = getVertexCoordinate(j,x, false);
					
					int outgoingHalfEdges [] = this.vertexGetOHeArray (verticesInCluster.get (j));
					
//					int neigh = vertexNeigborsCount (verticesInCluster.get (j));
					
					int neigh = outgoingHalfEdges.length;
					
//					int oHe = vertexGetOHe (verticesInCluster.get (j));
					for (int k = 0; k < neigh; k++)
					{
						int oHe = outgoingHalfEdges[k];

						int currentVert = heGetVertex (oHe);

						if ((heGetFace (oHe) == clusterNo || heGetFace (heGetTwin (oHe)) == clusterNo)
							&& getVertexCoordinate (currentVert, y, false) <= vertY)
						{

							double newX = calculateIntersection (sVertX, sVertY,
								getVertexCoordinate (currentVert, x, false),
								getVertexCoordinate (currentVert, y, false),
								vertY);
							if (newX < vertX)
							{
								//edge lies right of vertex
								
								if (newX > xRightEdge)
								{
									xRightEdge = newX;
									vertRight = verticesInCluster.get (j);
									yRightEdgeTop = verticesY.get (j);//getVertexCoordinate (currentVert, y, false);
									xRightEdgeTop = verticesX.get (j);
								}
							}
							else
							{
								//edge lies left of vertex
								
								
								if (newX < xLeftEdge)
								{
									xLeftEdge = newX;
									vertLeft = verticesInCluster.get (j);
									yLeftEdgeTop = verticesY.get (j);//getVertexCoordinate (currentVert, y, false);
									xLeftEdgeTop = verticesX.get (j);
								}

							}
						}
//						oHe = getNextOHeCW (oHe);

					}
				}
				
//				yLeft=yLeft-vertY;
//				if(yLeft<1.0f){yLeft=-yLeft;}
//				yRight=yRight-vertY;
//				if(yRight<1.0f){yRight=-yRight;}
				
				if (vertLeft == -1 && vertRight >= 0)
				{
					addEdgeBetween (verticesInCluster.get (i), vertRight,
						clusterNo);
				}
				else if (vertRight == -1 && vertLeft >= 0)
				{
					addEdgeBetween (verticesInCluster.get (i), vertLeft,
						clusterNo);
				}
				else if (vertLeft == -1 && vertRight == -1)
				{
					// no intersection found
				}
				else
				{

					double minEdgeVertexY;
					int minVertex;
					
					if (yRightEdgeTop<yLeftEdgeTop)
					{
						//vertexLeft is the one with the smaller y-coordinate
						//addEdgeBetween (verticesInCluster.get (i), vertRight,
						//	clusterNo);
						minEdgeVertexY=yRightEdgeTop;
						minVertex=vertRight;
					}
					else
					{
						//addEdgeBetween (verticesInCluster.get (i), vertLeft,
						//	clusterNo);
						minEdgeVertexY=yLeftEdgeTop;
						minVertex=vertLeft;
					}
					//Planänderung 
					
					
					
					if (minEdgeVertexY-vertY < - EPSILON_d || minEdgeVertexY-vertY > EPSILON_d)
					{
						for (int j = 0; j < i/*verticesInCluster.size ()*/; j++)
						{
							if (j != i && verticesY.get (j) > vertY + EPSILON_d
								&& verticesY.get (j) < minEdgeVertexY)
							{

								if (IntersectionTests.leftOf (yLeftEdgeTop,
									xLeftEdgeTop, yLeftEdge, xLeftEdge,
									verticesY.get (j), verticesX.get (j))
									&& IntersectionTests.rightOf (
										yRightEdgeTop, xRightEdgeTop,
										yRightEdge, xRightEdge,
										verticesY.get (j), verticesX.get (j)))
								{
//									System.out
//										.println ("Found smaller Vertex TtB");
									minVertex = verticesInCluster.get (j);
									minEdgeVertexY = verticesY.get (j);
								}

							}
						}
					} else {
						minVertex=verticesInCluster.get (i-1);
					}
					
					addEdgeBetweenForce (verticesInCluster.get (i), minVertex ,
						clusterNo);
					
				}

			}
		}

	}
	
	public boolean isAdjecentToList (IntList vertices, int vertex,
			int maxElement)
	{
		for (int i = 0; i < maxElement; i++)
		{
			if (isAdjecentVV (vertex, vertices.get (i)))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isAdjecentToListSmaller (IntList vertices, FloatList verticesX, int vertex,
			int maxElement)
	{
		for (int i = 0; i < vertices.size; i++)
		{
			if (verticesX.get (i)>=verticesX.get (vertex) && isAdjecentVV (vertex, vertices.get (i)))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isAdjecentToListBigger (IntList vertices, FloatList verticesX, int vertex,
			int maxElement)
	{
		for (int i = 0; i < vertices.size; i++)
		{
			if (verticesX.get (i)>=verticesX.get (vertex) && isAdjecentVV (vertex, vertices.get (i)))
			{
				return true;
			}
		}
		return false;
	}

	public DoubleList getVerticesCoordinates (IntList vertices, int offset)
	{
		DoubleList out = new DoubleList ();

		for (int i = 0; i < vertices.size (); i++)
		{
			out.push (getVertexCoordinate (vertices.get (i), offset, false));
		}
		out.trimToSize ();
		return out;
	}

	/* 
	 * returns the axes for an given cluster such that every half edge is still clockwise ordered. 
	 * The first entry of the array is the x-axis und the second entry is the y-axes. The
	 * value represents the axis in object coordinates which should be used.
	 * x-axis = 0
	 * y-axis = 1
	 * z axis = 2*/

	public int[] getAxes (int clusterNo)
	{
		int out[] = new int[2];

		int biggest = getBiggestComponentOfClusterNormal (clusterNo);

		if (biggest == 0)
		{
			if (isComponentNegative (clusterNo, biggest))
			{
				out[0] = 2;
				out[1] = 1;
			}
			else
			{
				out[0] = 1;
				out[1] = 2;
			}
		}
		else if (biggest == 1)
		{
			if (isComponentNegative (clusterNo, biggest))
			{
				out[0] = 0;
				out[1] = 2;
			}
			else
			{
				out[0] = 2;
				out[1] = 0;
			}
		}
		else
		{
			if (isComponentNegative (clusterNo, biggest))
			{
				out[0] = 1;
				out[1] = 0;
			}
			else
			{
				out[0] = 0;
				out[1] = 1;
			}
		}

		return out;
	}

	public void monotonateCluster (int clusterNo)
	{
		double normal[] = getClusterNormal (clusterNo);

		int axes[] = getAxes (clusterNo);
		int x = axes[0];
		int y = axes[1];

		//		IntList halfEdges = getHalfEdgesOfCluster(clusterNo);
		//		
		////		for (int i=0 ; i< halfEdges.size () ; i++){
		////			int v1=heGetOrigin(halfEdges.get (i));
		////			int v2=heGetVertex(halfEdges.get (i));
		////			int v3=heGetVertex(heGetNext(halfEdges.get (i)));
		////			if(isClockwise( getVertexCoordinate(v1,x,false),getVertexCoordinate(v1,y,false) ,
		////							getVertexCoordinate(v2,x,false),getVertexCoordinate(v2,y,false) ,
		////							getVertexCoordinate(v3,x,false),getVertexCoordinate(v3,y,false) )){
		////				
		////			}
		////			
		////		}

		splitInMonotoneParts (clusterNo);

		//		int he = halfEdges.get (0);
		//		int v1 = heGetOrigin(he);
		//		int v2 = heGetDest(heGetNext(he));
		//		
		//		addEdgeBetween(v1,v2,clusterNo);

	}
	
	public void monotonateCluster2 (int clusterNo)
	{
		double normal[] = getClusterNormal (clusterNo);

		int axes[] = getAxes (clusterNo);
		int x = axes[0];
		int y = axes[1];

		//		IntList halfEdges = getHalfEdgesOfCluster(clusterNo);
		//		
		////		for (int i=0 ; i< halfEdges.size () ; i++){
		////			int v1=heGetOrigin(halfEdges.get (i));
		////			int v2=heGetVertex(halfEdges.get (i));
		////			int v3=heGetVertex(heGetNext(halfEdges.get (i)));
		////			if(isClockwise( getVertexCoordinate(v1,x,false),getVertexCoordinate(v1,y,false) ,
		////							getVertexCoordinate(v2,x,false),getVertexCoordinate(v2,y,false) ,
		////							getVertexCoordinate(v3,x,false),getVertexCoordinate(v3,y,false) )){
		////				
		////			}
		////			
		////		}

		monotonate2 (clusterNo);

		//		int he = halfEdges.get (0);
		//		int v1 = heGetOrigin(he);
		//		int v2 = heGetDest(heGetNext(he));
		//		
		//		addEdgeBetween(v1,v2,clusterNo);

	}

	public void reverseLists (IntList list1, DoubleList list2, DoubleList list3)
	{
		for (int j = 0; j < list2.size () / 2; j++)
		{
			double temp = list2.get (j);
			list2.set (j, list2.get (list2.size - 1 - j));
			list2.set (list2.size - 1 - j, temp);

			temp = list3.get (j);
			list3.set (j, list3.get (list3.size - 1 - j));
			list3.set (list3.size - 1 - j, temp);

			int tempI = list1.get (j);
			list1.set (j, list1.get (list1.size - 1 - j));
			list1.set (list1.size - 1 - j, tempI);

		}
	}
	

	public void triangulate ()
	{
		int stop = clusterNormal.size()/3;
		for (int i=0 ; i<stop ; i++){
			monotonateCluster (i);
		}
		stop = clusterNormal.size()/3;
		for (int i=0 ; i<stop ; i++){
			triangulate (i);
		}
		
	}
	
	public void triangulate (int clusterNo) throws RuntimeException
	{

		int axes[] = getAxes (clusterNo);

		int x = axes[0];
		int y = axes[1];

		IntList verticesInCluster = getVerticesOfCluster (clusterNo);

		if (verticesInCluster.size () == 0)
		{
			//Workaround again
			return;
		}

		DoubleList verticesX = getVerticesCoordinates (verticesInCluster, x);
		DoubleList verticesY = getVerticesCoordinates (verticesInCluster, y);

		sortLists (verticesInCluster, verticesY, verticesX);

		reverseLists (verticesInCluster, verticesY, verticesX);

		// create a chain for every side of the polygone
				
		int minElement = verticesInCluster.get (0);
		int maxElement = verticesInCluster.get (verticesInCluster.size () - 1);

		int he = vertexGetOHalfEdgeCluster (minElement, clusterNo);
		
		if(he==-1){
			for(int i=0 ; i<heGetCount () ; i++){
				if(heGetFace(i)==clusterNo){he=i;}
			}
		}
		//create Right side
		
		IntList rightSide = new IntList ();
		int heCount=0;
		while (heGetVertex (he) != maxElement)
		{
			heCount++;
			rightSide.push (heGetVertex (he));
			he = heGetNext (he);
			if(heCount>1000){
				throw new RuntimeException();
			}
		}
		rightSide.push (maxElement);
		//create left side
		he = vertexGetOHalfEdgeCluster (minElement, clusterNo);
		he = heGetPrev(he);
		
		IntList leftSide = new IntList ();
		heCount=0;
		while (heGetVertex (he) != rightSide.peek (1))
		{
			heCount++;
			leftSide.push (heGetVertex (he));
			he = heGetPrev (he);
			if(heCount>1000){
				throw new RuntimeException();
			}
		}
		
		int cLeft=0;
		int cRight=0;
		
		//merge the left and the right side
		verticesInCluster.clear ();
		
		for(int i=0 ; i <= rightSide.size+leftSide.size ; i++){
						
			if(cRight==rightSide.size()){

				while(cLeft<leftSide.size()){
					verticesInCluster.push (leftSide.get(cLeft));
					cLeft++;
				}
				break;
			}
			
			if(cLeft == leftSide.size()){

				while(cRight<rightSide.size()){
					verticesInCluster.push (rightSide.get(cRight));
					cRight++;
				}
				break;
			}
			
			if( getVertexCoordinate (rightSide.get(cRight), y , false) > getVertexCoordinate (leftSide.get(cLeft), y , false)){
				verticesInCluster.push (rightSide.get(cRight));
				cRight++;
			} else {
				verticesInCluster.push (leftSide.get(cLeft));
				cLeft++;
			}
		}
				
		//lists are sorted

		IntList stack = new IntList ();
		
		//fill the stack

		stack.push (verticesInCluster.pop ());
		stack.push (verticesInCluster.pop ());

		while (!verticesInCluster.isEmpty ())
		{
			//get the next vertex
						
			int currentVertex = verticesInCluster.pop ();

			//check if new vertex is adjacent to bottom and top

			boolean adjToTop = isAdjecentVV (currentVertex,
				stack.get (stack.size () - 1));
			boolean adjToBottom = isAdjecentVV (currentVertex, stack.get (0));

			//Case 1: Adjacent to bottom addEdges between top to the second one from the bottom from the current vertex
			//new stack is (top, currentVert)

			if (adjToBottom && !adjToTop)
			{
				boolean fail=false;
				int newBottom = stack.get (stack.size () - 1);
				while (stack.size () > 1)
				{
					int topStack = stack.pop ();

					if (!addEdgeBetween (currentVertex, topStack , clusterNo)){
						// could not add Edge between topStack and currentVertex
						// add topStack back to the stack and insert currentVert at the bottom
						int oldSize=stack.size;
						
						stack.push (topStack);
//						for(int i=0 ; i<oldSize ; i++){
//							stack.set ( oldSize-i , stack.get(oldSize-1-i));
//						}
						stack.add (0 , currentVertex);
												
						fail=true;
						break;
					}
				}
				if(!fail){
					stack.clear ();
					stack.push (newBottom);
					stack.push (currentVertex);
				}
			}

			//Case 2: only adjacent to the top
			//try to add lines between the vertices in the stack from top (second one) to bottom and the current vertex
			//if the angle between the current vertex the current top and the vertex the edge should be added is smaller than 180 degrees
			//counterclockwise or clockwise
			//to determine if you should you the counter - or the clockwise way
			//check if the current vertex is the next of the top to the current face
			//if it is next you are on the right side
			//if it is the previous it is the left side

			else if (adjToTop && !adjToBottom)
			{

				if (rightSide.contains (currentVertex))
				{
					//current vertex lies on the right side of the polygon
					while (stack.size () > 1
						&& isClockwise (
							getVertexCoordinate (stack.peek (2), x, false),
							getVertexCoordinate (stack.peek (2), y, false),
							getVertexCoordinate (stack.peek (1), x, false),
							getVertexCoordinate (stack.peek (1), y, false),
							getVertexCoordinate (currentVertex, x, false),
							getVertexCoordinate (currentVertex, y, false)))
					{
						
						if(! addEdgeBetween (currentVertex,
							stack.get (stack.size () - 2), clusterNo) ) {
							break;
						}
						stack.pop ();
					}

				}
				else
				{
					//current vertex lies on the left side of the polygon

					while (stack.size () > 1
						&& isCounterClockwise (
							getVertexCoordinate (stack.peek (2), x, false),
							getVertexCoordinate (stack.peek (2), y, false),
							getVertexCoordinate (stack.peek (1), x, false),
							getVertexCoordinate (stack.peek (1), y, false),
							getVertexCoordinate (currentVertex, x, false),
							getVertexCoordinate (currentVertex, y, false)))
					{
						
						if( !addEdgeBetween (currentVertex,
							stack.get (stack.size () - 2), clusterNo) ){
							break;
						}
						stack.pop ();
					}

				}

				stack.push (currentVertex);
			}

			//Case 3: if both adjacent to bottom and top add Edges between the second one from the top to the second one from the bottom

			else if (adjToTop && adjToBottom)
			{
				stack.pop ();
				while (stack.size () > 1)
				{
					int topStack = stack.pop ();
										
					
					if(!addEdgeBetweenForce (currentVertex, topStack, clusterNo)){
						
						System.out.println("Triangulation fail at vertex "+currentVertex+" TopStack "+topStack);
						System.out.println("Case 3");
						System.out.println ("Cluster No: "+clusterNo);
						System.out.println("Old Stack "+stack);
//						int oldSize=stack.size ();
//						for(int i=1 ; i<oldSize ; i++){
//							stack.add ( oldSize-i , stack.get(oldSize-1-i));
//						}
//						stack.add ( 0 , currentVertex);
//						currentVertex=topStack;
						
					}
				}
				stack.clear ();
				verticesInCluster.clear ();
			}

		}
		//get the next vertex

		//check if new vertex is adjacent to bottom and top

		//Case 1: Adjacent to bottom addEdges between top to the second one from the bottom
		//new stack is (top, currentVert)

		//Case 2: if both adjacent to bottom and top add Edges between the second one from the top to the second one from the bottom

		//Case 3: only adjacent to the top
		//try to add lines between the vertices in the stack from top (second one) to bottom and the current vertex
		//if the angle between the current vertex the current top and the vertex the edge should be added is smaller than 180 degrees
		//counterclockwise or clockwise
		//to determine if you should you the counter - or the clockwise way
		//check if the current vertex is the next of the top to the current face
		//if it is next you are on the right side
		//if it is the previous it is the left side
		createNewFace (clusterNo);
	}

	public double[] getClusterNormal (int clusterNo)
	{
		double clusterNormal[] = new double[3];
		clusterNormal[0] = this.clusterNormal.get (clusterNo * 3);
		clusterNormal[1] = this.clusterNormal.get (clusterNo * 3 + 1);
		clusterNormal[2] = this.clusterNormal.get (clusterNo * 3 + 2);
		return clusterNormal;
	}

	public int getBiggestComponentOfClusterNormal (int clusterNo)
	{
		double clusterNormal[] = getClusterNormal (clusterNo);
		if (clusterNormal[0] < 0.0f)
			clusterNormal[0] = -clusterNormal[0];
		if (clusterNormal[1] < 0.0f)
			clusterNormal[1] = -clusterNormal[1];
		if (clusterNormal[2] < 0.0f)
			clusterNormal[2] = -clusterNormal[2];

		if (clusterNormal[0] > clusterNormal[1])
		{
			//x is bigge then y
			if (clusterNormal[0] > clusterNormal[2])
			{
				//x is the biggest component
				return 0;
			}
			else
			{
				//z is the biggest component
				return 2;
			}
		}
		else
		{
			//y is bigger then x
			if (clusterNormal[1] > clusterNormal[2])
			{
				//y is the biggest component
				return 1;
			}
			else
			{
				//z is the biggest component
				return 2;
			}
		}
	}

	/*
	 * checks if the n-th component of the given cluster is negative
	 * the offset stand for
	 * x=0
	 * y=1
	 * z=2*/
	public boolean isComponentNegative (int clusterNo, int offset)
	{
		if (clusterNormal.get (clusterNo * 3 + offset) > 0.0f)
			return false;
		else
			return true;
	}

	public IntList getHalfEdgesOfCluster (int clusterNo)
	{
		IntList out = new IntList ();
		for (int i = 0; i < halfEdges.size () / 5; i++)
		{
			if (heGetFace (i) == clusterNo)
			{
				out.push (i);
			}
		}
		out.trimToSize ();
		return out;
	}

	public IntList getVerticesOfCluster (int clusterNo)
	{
		IntList verticesInCluster = new IntList ();
		for (int i = 0; i < halfEdges.size () / 5; i++)
		{
			int vertNew;
			if (heGetFace (i) == clusterNo)
			{
				vertNew = heGetDest (i);
				boolean inCluster = false;
				for (int j = 0; j < verticesInCluster.size (); j++)
				{
					if (verticesInCluster.get (j) == vertNew)
					{
						inCluster = true;
					}
				}
				if (!inCluster)
				{
					verticesInCluster.push (vertNew);
				}
			}
		}
		verticesInCluster.trimToSize ();
		return verticesInCluster;
	}

	public FloatList getCoordinates (IntList vertices, int offset)
	{
		FloatList verticesCoordinates = new FloatList ();
		for (int i = 0; i < vertices.size (); i++)
		{
			verticesCoordinates.push (vertices.get (vertices.get (i) * 3
				+ offset));
		}
		verticesCoordinates.trimToSize ();
		return verticesCoordinates;
	}

	public double getVertexCoordinate (int vertPos, int offset, boolean negate)
	{
		if (negate)
			return -vertices.get (vertPos * 3 + (offset % 3));
		else
			return vertices.get (vertPos * 3 + (offset % 3));

	}

	private double sumScalar (double x1, double y1, double x2, double y2, double x3,
			double y3)
	{
		return (x2 - x1) * (y2 + y1) + (x3 - x2) * (y3 + y2) + (x1 - x3)
			* (y1 + y3);
	}

	public boolean isAdjecentVV (int vert1Pos, int vert2Pos)
	{
		
		int outgoingHalfEdges [] = vertexGetOHeArray (vert1Pos);
		
//		int neigborsCount = vertexNeigborsCount (vert1Pos);
		int neigborsCount = outgoingHalfEdges.length;
		
		//TODO: Remove old code
//		if (neigborsCount == -1)
//		{
//			if( vertexGetOHe(vert1Pos) > -1 && heGetVertex( vertexGetOHe(vert1Pos) ) == vert2Pos ){
//				return true;
//			}
//			if( vertexGetOHe(vert2Pos) > -1 && heGetVertex( vertexGetOHe(vert2Pos) ) == vert1Pos ){
//				return true;
//			}
//			
//			return false;
//		}
//		int oHe = vertexGetOHe (vert1Pos);
		for (int i = 0; i < neigborsCount; i++)
		{
			int oHe=outgoingHalfEdges[i];
			
			if (heGetVertex (oHe) == vert2Pos)
			{
				return true;
			}
			else
			{
//				oHe = getNextOHeCW (oHe);
			}
		}
		return false;
	}

	private boolean isCounterClockwise (double x1, double y1, double x2, double y2,
			double x3, double y3)
	{
		if (sumScalar (x1, y1, x2, y2, x3, y3) < 0.0d)
			return true;
		else
			return false;
	}

	private boolean isClockwise (double x1, double y1, double x2, double y2,
			double x3, double y3)
	{
		if (sumScalar (x1, y1, x2, y2, x3, y3) > 0.0d)
			return true;
		else
			return false;
	}

	private void sortLists (IntList verticesInCluster, DoubleList verticesY,
			DoubleList verticesX)
	{
		for (int i = 0; i < verticesY.size (); i++)
		{
			for (int j = 0; j < verticesY.size () - 1; j++)
			{
				double epsilonTemp = 0.000001d;
				
				if (verticesY.get (j) > verticesY.get (j + 1)+epsilonTemp)
				{
					double tempY;
					int tempVert;
					tempY = verticesY.get (j);
					verticesY.set (j, verticesY.get (j + 1));
					verticesY.set (j + 1, tempY);

					tempY = verticesX.get (j);
					verticesX.set (j, verticesX.get (j + 1));
					verticesX.set (j + 1, tempY);

					tempVert = verticesInCluster.get (j);
					verticesInCluster.set (j, verticesInCluster.get (j + 1));
					verticesInCluster.set (j + 1, tempVert);
				}
				else if (verticesY.get (j) < verticesY.get (j + 1) +epsilonTemp && verticesY.get (j) > verticesY.get (j + 1) -epsilonTemp
					&& verticesX.get (j) > verticesX.get (j + 1))
				{
					double tempY;
					int tempVert;
					tempY = verticesY.get (j);
					verticesY.set (j, verticesY.get (j + 1));
					verticesY.set (j + 1, tempY);

					tempY = verticesX.get (j);
					verticesX.set (j, verticesX.get (j + 1));
					verticesX.set (j + 1, tempY);

					tempVert = verticesInCluster.get (j);
					verticesInCluster.set (j, verticesInCluster.get (j + 1));
					verticesInCluster.set (j + 1, tempVert);
				}
			}
		}
	}

	private boolean findInList (IntList list, int vert, int maxIndex)
	{
		for (int i = 0; i < maxIndex; i++)
		{
			if (list.get (i) == vert)
				return true;
		}
		return false;
	}

	/*
	 * adds an edge between the destination of the start half-edge and the origin of the ending half-edge.
	 * the boolean left determines if the position of the the destination of the ending half-edge is left or right of the starting vertex 
	 * */

	private void addEdgeBetween (int prevHe, int nextHe)
	{
		// TODO Auto-generated method stub
		
		int pHalfEdge;
		pHalfEdge = nextHe;

		int face = heGetFace (prevHe);
		int cHalfEdge = prevHe;
		int nextCHalfEdge = heGetNext (cHalfEdge);
		int nextPHalfEdge = heGetNext (pHalfEdge);
		int pVert = heGetVertex (pHalfEdge);
		int cVert = heGetVertex (cHalfEdge);

		//create new edge with cluster as face
		//STEP 1 and 2 add the new half-edges
		int newNextCHalfEdge = addHalfEdge (pVert, face, -1, cHalfEdge,
			nextPHalfEdge);
		
		int newNextPHalfEdge = addHalfEdge (cVert, face, -1, pHalfEdge,
			nextCHalfEdge);
		
		//add the normal and the tex-coordinates for newNextCHalfEdge
		normals.push ( normals.get (pHalfEdge*3 ) , normals.get (pHalfEdge*3 + 1) , normals.get (pHalfEdge*3 + 2));
		uv.push ( uv.get (pHalfEdge*2) , uv.get (pHalfEdge*2 + 1) );
		

		//add the normal and the tex-coordinates for newNextPHalfEdge
		normals.push ( normals.get (cHalfEdge*3 ) , normals.get (cHalfEdge*3 + 1) , normals.get (cHalfEdge*3 + 2));
		uv.push ( uv.get (cHalfEdge*2) , uv.get (cHalfEdge*2 + 1) );
		
		
		//STEP 3 update twins of the new half-edges
		heSetTwins (newNextCHalfEdge, newNextPHalfEdge);

		//STEP 4 update the successors

		heSetSuccessors (cHalfEdge, newNextCHalfEdge);
		heSetSuccessors (newNextCHalfEdge, nextPHalfEdge);
		heSetSuccessors (pHalfEdge, newNextPHalfEdge);
		heSetSuccessors (newNextPHalfEdge, nextCHalfEdge);
		
		
		
	}

	/* calculates the intersection between an edge and a given line
	 * 
	 * */

	public double calculateIntersection (double edgeX1, double edgeY1, double edgeX2, double edgeY2,
			double vertY)
	{			
		double dX = edgeX2 - edgeX1;
		double dY = edgeY2 - edgeY1;

		double dLineY = vertY - edgeY1;

		double t = dLineY / dY;

		return edgeX1 + dX * t;
	}
	
	//finds the Exterior Edge on which the vertex lies on
	//and splits it at the vertex

	public void splitExteriorEdge (int halfEdge, int nextHalfEdge)
	{
		boolean debug = false;
		DoubleList debugList = new DoubleList();
		
		
		int vertex = heGetDest (halfEdge);
		Tuple3d c = new Point3d (vertices.get (vertex * 3),
			vertices.get (vertex * 3 + 1), vertices.get (vertex * 3 + 2));
		Tuple3d a = new Point3d ();
		Tuple3d b = new Point3d ();
		int from, to;
		for (int i = 0; i < heGetCount(); i++)
		{
			if (heGetFace (i) == heGetFace (halfEdge))
			{
				from = heGetOrigin (i);
				to = heGetDest (i);
				if (from != vertex && to != vertex)
				{
					a.x = vertices.get (from * 3);
					a.y = vertices.get (from * 3 + 1);
					a.z = vertices.get (from * 3 + 2);

					b.x = vertices.get (to * 3);
					b.y = vertices.get (to * 3 + 1);
					b.z = vertices.get (to * 3 + 2);

					Tuple3d extend1 = new Point3d ();
					Tuple3d extend2 = new Point3d ();

					if (a.x < b.x)
					{
						extend1.x = a.x;
						extend2.x = b.x;
					}
					else
					{
						extend2.x = a.x;
						extend1.x = b.x;
					}
					if (a.y < b.y)
					{
						extend1.y = a.y;
						extend2.y = b.y;
					}
					else
					{
						extend2.y = a.y;
						extend1.y = b.y;
					}
					if (a.z < b.z)
					{
						extend1.z = a.z;
						extend2.z = b.z;
					}
					else
					{
						extend2.z = a.z;
						extend1.z = b.z;
					}

					if ( extend1.x - EPSILON_d <= c.x && extend1.y - EPSILON_d  <= c.y
						&& extend1.z - EPSILON_d  <= c.z && extend2.x + EPSILON_d >= c.x
						&& extend2.y + EPSILON_d  >= c.y && extend2.z + EPSILON_d >= c.z)
					{
						a = IntersectionTests.CROSS (a, b);
						double d = IntersectionTests.DOT (a, c);
						
						if(debug){
							debugList.add (d);
						}
						
						if (d < EPSILON_d && d > -EPSILON_d)
						{
							//intersection found
							//split the Edge

							//save some old references
							int twinI = heGetTwin (i);
							int twinHalfEdge = heGetTwin (halfEdge);
							int twinNextHalfEdge = heGetTwin (nextHalfEdge);
							int face1 = heGetFace (halfEdge);
							int face2 = heGetFace (nextHalfEdge);
							int isectVert = heGetDest (halfEdge);
							int oldPrevI = heGetPrev (i);
							int oldPrevTwinI = heGetPrev (twinI);

							//STEP 1 
							//add 2 new half-edges

							//add newTwinI and alter the data of oldPrevTwinI
							int newTwinI = addHalfEdge (isectVert, face2, i,
								oldPrevTwinI, nextHalfEdge);
							heSetNext (oldPrevTwinI, newTwinI);
							heSetTwin (i, newTwinI);
							
							//dummy normals
							normals.push(1.0f,1.0f,1.0f);
							uv.push(uv.get (twinNextHalfEdge*2) , uv.get (twinNextHalfEdge*2+1) );
							
							
							//TODO: add status BORDER
							//add twinTwinI and alter the data of oldPrevI
							int twinTwinI = addHalfEdge (isectVert, face1,
								twinI, oldPrevI, twinHalfEdge);
							heSetNext (oldPrevI, twinTwinI);
							heSetTwin (twinI, twinTwinI);
							
							//dummy normals
							normals.push(1.0f,1.0f,1.0f);
							uv.push(uv.get (halfEdge*2) , uv.get (halfEdge*2+1) );
							
							//STEP 2
							//update the relations of successing half-edges

							heSetSuccessors (twinTwinI, twinHalfEdge);
							heSetSuccessors (halfEdge, i);
							heSetSuccessors (newTwinI, nextHalfEdge);
							heSetSuccessors (twinNextHalfEdge, twinI);

							return;
						}
					}

				}
			}
		}
		if (debug)
		{
			System.out.println ("found no edge for splitting " + halfEdge + " "
				+ nextHalfEdge);
			System.out.println (debugList);
			
		}
	}

	public void splitExteriorEdge (Tuple3d vertex)
	{
		boolean debug = false;
		DoubleList debugList = new DoubleList();
		
		int count=0;
		
//		int vertex = vertPos;
		Tuple3d c = vertex;
		Tuple3d a = new Point3d ();
		Tuple3d b = new Point3d ();
		int vertPos=vertices.size()/3;
		int from, to;
		int end=heGetCount();
		for (int i = 0; i < end ; i++)
		{
			
				from = heGetOrigin (i);
				to = heGetDest (i);
				if (from != vertPos && to != vertPos && from > -1 && to>-1)
				{
					
					a.x = vertices.get (from * 3);
					a.y = vertices.get (from * 3 + 1);
					a.z = vertices.get (from * 3 + 2);

					b.x = vertices.get (to * 3);
					b.y = vertices.get (to * 3 + 1);
					b.z = vertices.get (to * 3 + 2);
					
					Tuple3d fromVert = new Point3d();
					Tuple3d toVert = new Point3d();
					fromVert.set (a);
					toVert.set (b);
					
					Tuple3d extend1 = new Point3d ();
					Tuple3d extend2 = new Point3d ();

					if (a.x < b.x)
					{
						extend1.x = a.x;
						extend2.x = b.x;
					}
					else
					{
						extend2.x = a.x;
						extend1.x = b.x;
					}
					if (a.y < b.y)
					{
						extend1.y = a.y;
						extend2.y = b.y;
					}
					else
					{
						extend2.y = a.y;
						extend1.y = b.y;
					}
					if (a.z < b.z)
					{
						extend1.z = a.z;
						extend2.z = b.z;
					}
					else
					{
						extend2.z = a.z;
						extend1.z = b.z;
					}

					if ( extend1.x - EPSILON_d <= c.x && extend1.y - EPSILON_d  <= c.y
						&& extend1.z - EPSILON_d  <= c.z && extend2.x + EPSILON_d >= c.x
						&& extend2.y + EPSILON_d  >= c.y && extend2.z + EPSILON_d >= c.z)
					{
						
						Tuple3d x0 = new Point3d();
						Tuple3d x1 = new Point3d();
						Tuple3d x2 = new Point3d();
						
						x0.set (c);
						x1.set (a);
						x2.set (b);
						
						Tuple3d x0x1 = new Point3d();
						Tuple3d x0x2 = new Point3d();
						Tuple3d x2x1 = new Point3d();
						
						x0x1.sub (x0, x1);
						x0x2.sub (x0, x2);
						
						x2x1.sub (x2 , x1);
						
						x0 = IntersectionTests.CROSS(x0x1,x0x2);
						
						
						
						a = IntersectionTests.CROSS (a, b);
						double d = IntersectionTests.DOT (a, c);
						
						d = IntersectionTests.ABS (x0)/ IntersectionTests.ABS (x2x1);
						
						if(debug){
							debugList.add (d);
						}
						
						if (d < EPSILON_d && d > -EPSILON_d)
						{
							count++;
							
							vertPos=getVertexPosition(vertex.x , vertex.y , vertex.z);
							
							if(vertPos<0){
								vertPos=vertices.size()/3;
								vertices.push (vertex.x , vertex.y , vertex.z);
							}
							
							//intersection found
							
							//calculate t
							

							toVert.sub (fromVert);
							
							Tuple3d vert = new Point3d();
							vert.set (c);
							vert.sub (fromVert);
							int off=IntersectionTests.getBiggestComponent (toVert);
							
							
							double biggestB;
							double biggestVert;
							
							if(off==0){
								biggestB=toVert.x;
								biggestVert=vert.x;
							} else if(off==1){
								biggestB=toVert.y;
								biggestVert=vert.y;
							} else{
								biggestB=toVert.z;
								biggestVert=vert.z;
							}
							
							double t=biggestVert/biggestB;
							
							//get biggest component
							
							
							//split the Edge

							//save some old references
							
							int twinI = heGetTwin (i);
							int oldPrevI = heGetPrev (i);
							int oldPrevTwinI = heGetPrev (twinI);
							
							int face1=heGetFace(i);
							int face2=heGetFace(twinI);
							
							Tuple2f texPrevI = interpolateTexData(i,(float)t);
							Tuple3f normalPrevI = interpolateNormalData(i,(float)t); 
							
							t=1.0d-t;
							
							Tuple2f texPrevTwinI =interpolateTexData(twinI,(float)t);
							Tuple3f normalPrevTwinI =interpolateNormalData(twinI,(float)t);
							
							//STEP 1 
							//add 2 new half-edges

							//add newTwinI and alter the data of oldPrevTwinI
							int newTwinI = addHalfEdge (vertPos, face2, i,
								oldPrevTwinI, twinI);
							heSetNext (oldPrevTwinI, newTwinI);
							heSetPrev ( twinI, newTwinI);
							heSetTwin (i, newTwinI);
							
							normals.push( normalPrevTwinI.x , normalPrevTwinI.y , normalPrevTwinI.z );
							uv.push(texPrevTwinI.x , texPrevTwinI.y );
							
							
							
							vertexToHalfEdge.set (vertPos, newTwinI);
							
							//TODO: add status BORDER
							//add twinTwinI and alter the data of oldPrevI
							int twinTwinI = addHalfEdge (vertPos, face1,
								twinI, oldPrevI, i);
							heSetNext (oldPrevI, twinTwinI);
							heSetPrev ( i, twinTwinI);
							heSetTwin (twinI, twinTwinI);
							
							normals.push( normalPrevI.x , normalPrevI.y , normalPrevI.z );
							uv.push( texPrevI.x , texPrevI.y );

						}
					}

				}
		}
		if (debug)
		{
			System.out.println ("found no edge for splitting " + vertPos);
			System.out.println (debugList);
			
		}
	}
	
	private Tuple2f interpolateTexData (int i, float t)
	{
		Tuple2f out = new Point2f();
		
		int prev = heGetPrev(i);
		
		float iU , iV;
		
		float prevU, prevV;
		
		iU=uv.get (2*i);
		iV=uv.get (2*i + 1);
		
		prevU=uv.get (2*prev);
		prevV=uv.get (2*prev + 1);
		
		iU=(iU-prevU)*t;
		iV=(iV-prevV)*t;
		
		out.x=prevU+iU;
		out.y=prevV+iV;
		
		return out;
	}

	private Tuple3f interpolateNormalData (int i, float t)
	{
		Tuple3f out = new Point3f();
		
		int prev = heGetPrev(i);
		
		float iX , iY , iZ;
		
		float prevX, prevY, prevZ;
		
		iX=normals.get (3*i);
		iY=normals.get (3*i + 1);
		iZ=normals.get (3*i + 2);
		
		prevX=normals.get (3*prev);
		prevY=normals.get (3*prev + 1);
		prevZ=normals.get (3*prev + 2);
		
		iX=(iX-prevX)*t;
		iY=(iY-prevY)*t;
		iZ=(iZ-prevZ)*t;
		
		out.x=prevX+iX;
		out.y=prevY+iY;
		out.z=prevZ+iZ;
		
		double length = out.x*out.x+out.y*out.y+out.z*out.z;
		length=Math.sqrt (length);
		
		out.scale ((float)(1.0f/length));
		
		return out;
	}

	public void heSetVertex (int hePos, int vertex)
	{
		halfEdges.set (hePos * 5, vertex);
	}

	public void heSetFace (int hePos, int face)
	{
		
		halfEdges.set (hePos * 5 + 1, face);
	}

	public void heSetTwin (int hePos, int twin)
	{
		halfEdges.set (hePos * 5 + 2, twin);
	}

	public void heSetPrev (int hePos, int prev)
	{
		halfEdges.set (hePos * 5 + 3, prev);
	}

	public void heSetNext (int hePos, int next)
	{
		halfEdges.set (hePos * 5 + 4, next);
	}

	/*
	 * updates the references on both sides*/
	public void heSetTwins (int hePos1, int hePos2)
	{
		heSetTwin (hePos1, hePos2);
		heSetTwin (hePos2, hePos1);
	}

	public void heSetSuccessors (int prev, int next)
	{
		heSetNext (prev, next);
		heSetPrev (next, prev);
	}

	private int addHalfEdge (int destinationVertex, int face, int twinHe,
			int prevHe, int nextHe)
	{
		halfEdges.push (destinationVertex); //set Destination
		halfEdges.push (face); //set Cluster
		halfEdges.push (twinHe); //set twin
		halfEdges.push (prevHe); //set prev
		halfEdges.push (nextHe); //set next
		heStatus.push (UNDEFINED);
		return halfEdges.size () / 5 - 1;
	}

	
	/*method for adding Vertices of the intersection Line to the Link.
	 * this method test if the new vertex lies on an Edge an will split it at this position
	 * it will also interpolate the tex- and normal-data on this edge
	*/
	
	public void addIntersectionVertices(DoubleList vertices){
		
		for(int i=0 ; i<vertices.size (); i++){
			int pos = getVertexPosition(vertices.get (3*i),vertices.get (3*i+1),vertices.get (3*i+2));
			if (pos<0){
				Tuple3d vertex= new Point3d();
				vertex.x=vertices.get (3*i);
				vertex.y=vertices.get (3*i + 1);
				vertex.z=vertices.get (3*i + 2);
				
				splitExteriorEdge (vertex);
			}
		}
		
		startOfBorder=this.vertices.size()/3;
		
	}
	
	public void addIntersectionLineOld (int fromPos, int toPos, int cluster, float fromU , float fromV , float toU , float toV , float frommNX, float frommNY, float frommNZ, float toNX, float toNY, float toNZ)
	{
		//		System.out.println ("intersection line from: "+fromPos+" to: "+toPos);
		int hePos = heGetCount();
		//add first edge
		halfEdges.push (toPos);
		halfEdges.push (cluster);
		halfEdges.push (hePos + 1);
		halfEdges.push (-1); //prev
		halfEdges.push (-1); //next
		if(toPos>=startOfBorder){vertexToHalfEdge.set (toPos, hePos);} //update the vertex To Half Edge array
		//add first edge
		halfEdges.push (fromPos);
		halfEdges.push (cluster);
		halfEdges.push (hePos);
		halfEdges.push (-1); //prev
		halfEdges.push (-1); //next
		if(fromPos>=startOfBorder){vertexToHalfEdge.set (fromPos, hePos + 1);}
		
		//dummy normal of he1
		normals.push(toNX,toNY,toNZ);
		uv.push(toU , toV);
		
		
		//dummy normal of he2
		normals.push(frommNX,frommNY,frommNZ);
		uv.push(fromU , fromV);
		
		
		heStatus.push (BORDER, BORDER);

		int prev = -1;
		int next = -1;

		for (int i = 0; i < hePos; i++)
		{
			if (heGetDest (i) == fromPos)
			{
				prev = i;
			}
			else if (heGetOrigin (i) == toPos)
			{
				next = i;
			}
			if (prev > -1 && next > -1)
			{
				break;
			}
		}

		if (prev > -1)
		{
			
			if (fromPos>=startOfBorder)
			{
				halfEdges.set (hePos * 5 + 3, prev);
				halfEdges.set ((hePos + 1) * 5 + 4, heGetTwin (prev));
				halfEdges.set (prev * 5 + 4, hePos);
				halfEdges.set (heGetTwin (prev) * 5 + 3, heGetTwin (hePos));
				if (heGetFace (prev) != cluster)
				{
					//origin vertex lies on exterior Edge
					splitExteriorEdge (prev, hePos);
				}
			} else{
				//fromPos is an vertex of the original object
				prev=getSector (toPos, fromPos, cluster);
				if(prev==-1){
					
				} else {
				//set the previous
				halfEdges.set (hePos * 5 + 3, heGetPrev(prev));
				//set the next of the twin
				halfEdges.set ((hePos + 1) * 5 + 4, prev);
				//update the next of the previous of the previous
				halfEdges.set (heGetPrev (prev) * 5 + 4, hePos);
				//update the prev of the previous
				halfEdges.set (prev * 5 + 3, heGetTwin(hePos) );
				}
			}
			
		}

		if (next > -1)
		{
			if (toPos>=startOfBorder)
			{
				halfEdges.set (hePos * 5 + 4, next);
				halfEdges.set ((hePos + 1) * 5 + 3, heGetTwin (next));
				halfEdges.set (next * 5 + 3, hePos);
				halfEdges.set (heGetTwin (next) * 5 + 4, heGetTwin (hePos));
				if (heGetFace (next) != cluster)
				{
					//destination vertex lies on exterior Edge
					splitExteriorEdge (hePos, next);
				}
			} else{
				
				//toPos is an vertex of the original object
				next=getSector (fromPos, toPos, cluster);
				if(next==-1){
					next=getSector (fromPos, toPos, cluster);
				} else{
				//set the next
				halfEdges.set (hePos * 5 + 4, next);
				//set the previous of the twin
				halfEdges.set ((hePos + 1) * 5 + 3, heGetPrev (next));
				//update the next of the previous of the next
				halfEdges.set (heGetPrev (next) * 5 + 4, heGetTwin (hePos));
				//update the previous of the next
				halfEdges.set (next * 5 + 3, hePos);
				}
			}
		}
		
		//idee überprüfe beim verbinden der schnittkante, ob beide schnittlinien im selben cluster liegen
		//liegen sie in unterschiedlichen clustern, müssen sie sich auf einer äusseren kante treffen
		//speicher für vertices auf äusseren kanten.
	}
	
	//method for adding Edges from the intersection line to the Link

	public void addIntersectionLine (int fromPos, int toPos, int cluster, float fromU , float fromV , float toU , float toV , float frommNX, float frommNY, float frommNZ, float toNX, float toNY, float toNZ)
	{
		int hePos = heGetCount();
		//add first edge
		halfEdges.push (toPos);
		halfEdges.push (cluster);
		halfEdges.push (hePos + 1);
		halfEdges.push (-1); //prev
		halfEdges.push (-1); //next
		if(toPos>=startOfBorder){vertexToHalfEdge.set (toPos, hePos);} //update the vertex To Half Edge array
		//add first edge
		halfEdges.push (fromPos);
		halfEdges.push (cluster);
		halfEdges.push (hePos);
		halfEdges.push (-1); //prev
		halfEdges.push (-1); //next
		if(fromPos>=startOfBorder){vertexToHalfEdge.set (fromPos, hePos + 1);}
		
		//dummy normal of he1
		normals.push(toNX,toNY,toNZ);
		uv.push(toU , toV);
		
		//dummy normal of he2
		normals.push(frommNX,frommNY,frommNZ);
		uv.push(fromU , fromV);
		
		heStatus.push (BORDER, BORDER);

		int prev = -1;
		int next = -1;

		for (int i = 0; i < hePos; i++)
		{
			if (heGetDest (i) == fromPos)
			{
				prev = i;
			}
			else if (heGetOrigin (i) == toPos)
			{
				next = i;
			}
			if (prev > -1 && next > -1)
			{
				break;
			}
		}

		if (prev > -1)
		{
			
			if (fromPos>=startOfBorder)
			{
				halfEdges.set (hePos * 5 + 3, prev);
				halfEdges.set ((hePos + 1) * 5 + 4, heGetTwin (prev));
				halfEdges.set (prev * 5 + 4, hePos);
				halfEdges.set (heGetTwin (prev) * 5 + 3, heGetTwin (hePos));
//				if (heGetFace (prev) != cluster)
//				{
//					//origin vertex lies on exterior Edge
//					splitExteriorEdge (prev, hePos);
//				}
			} else{
				//fromPos is a vertex of the original object
				prev=getSector (toPos, fromPos, cluster);
				if(prev==-1){
				} else {
				//set the previous
				halfEdges.set (hePos * 5 + 3, heGetPrev(prev));
				//set the next of the twin
				halfEdges.set ((hePos + 1) * 5 + 4, prev);
				//update the next of the previous of the previous
				halfEdges.set (heGetPrev (prev) * 5 + 4, hePos);
				//update the prev of the previous
				halfEdges.set (prev * 5 + 3, heGetTwin(hePos) );
				}
			}
			
		}

		if (next > -1)
		{
			if (toPos>=startOfBorder)
			{
				halfEdges.set (hePos * 5 + 4, next);
				halfEdges.set ((hePos + 1) * 5 + 3, heGetTwin (next));
				halfEdges.set (next * 5 + 3, hePos);
				halfEdges.set (heGetTwin (next) * 5 + 4, heGetTwin (hePos));
//				if (heGetFace (next) != cluster)
//				{
//					//destination vertex lies on exterior Edge
//					splitExteriorEdge (hePos, next);
//				}
			} else{
				
				//toPos is an vertex of the original object
				next=getSector (fromPos, toPos, cluster);
				if(next==-1){
				} else{
				//set the next
				halfEdges.set (hePos * 5 + 4, next);
				//set the previous of the twin
				halfEdges.set ((hePos + 1) * 5 + 3, heGetPrev (next));
				//update the next of the previous of the next
				halfEdges.set (heGetPrev (next) * 5 + 4, heGetTwin (hePos));
				//update the previous of the next
				halfEdges.set (next * 5 + 3, hePos);
				}
			}
		}
		
	}

	//method for adding existing Edges from an HalfEdge Object to the Link

	public void addExteriorEdge (int fromPos, int toPos, int face, float normalX, float normalY , float normalZ, float texU , float texV)
	{
		int hePos = -1;
		int prevPos = -1;
		int twinPos = -1;
		int nextPos = -1;
		
		
		
		
		//check if half-edge already exists in the array
		//also check if the previous to this halfEdge exists
		//also check for next
		//also check for a twin
		for (int i = 0; i < heGetCount (); i++)
		{
			if (heGetFace (i) == face)
			{
				if (heGetDest (i) == fromPos && heGetNext(i)==-1)
					prevPos = i;
				if (heGetDest (i) == toPos && (heGetPrev(i)==-1))
					hePos = i;
			}
			else if (heGetDest (i) == fromPos && heGetOrigin (i) == toPos)
			{
				twinPos = i;
			}
		}

		if (hePos > -1)
		{
			halfEdges.set (hePos * 5 + 2, twinPos);
			
			normals.set (hePos*3,normalX);
			normals.set (hePos*3+1,normalY);
			normals.set (hePos*3+2,normalZ);
			
			uv.set (hePos*2 , texU );
			uv.set (hePos*2+1, texV);
			
			//if previous Half Edge does not exist add it
			if (prevPos == -1)
			{
				prevPos = halfEdges.size () / 5;
				halfEdges.add (fromPos);
				halfEdges.add (face);
				halfEdges.add (twinPos);
				halfEdges.add (-1);
				halfEdges.add (hePos);
				vertexToHalfEdge.set (fromPos, prevPos);
				heStatus.add (UNDEFINED);
				
				//add some dummy normals
				
				normals.push(0.0f , 0.0f, 0.0f);
				uv.push(0.0f, 0.0f);
				
			}
			else
			{
				halfEdges.set (prevPos * 5 + 4, hePos);
				if (twinPos > -1)
				{
					halfEdges.set (twinPos * 5 + 2, hePos);
				}
			}
			
			halfEdges.set (hePos * 5 + 3, prevPos);

		}
		else
		{
			//half-edge doesn't exist
			hePos = heGetCount();
			halfEdges.add (toPos);
			halfEdges.add (face);
			halfEdges.add (twinPos);
			halfEdges.add (prevPos);
			halfEdges.add (nextPos);
			heStatus.add (UNDEFINED);
			vertexToHalfEdge.set (toPos, hePos);
			
			normals.push (normalX , normalY , normalZ);
			
			uv.push (texU , texV);
			
			if (prevPos == -1)
			{
				prevPos = heGetCount();
				halfEdges.add (fromPos);
				halfEdges.add (face);
				halfEdges.add (-1);
				halfEdges.add (-1);
				halfEdges.add (hePos);
				vertexToHalfEdge.set (fromPos, prevPos);
				heStatus.add (UNDEFINED);
				
				//add some dummy normals
				
				normals.push(0.0f , 0.0f, 0.0f);
				uv.push(0.0f, 0.0f);
			}
			if (nextPos > -1)
			{
				halfEdges.set (nextPos * 5 + 3, hePos);
			}
			halfEdges.set (hePos * 5 + 3, prevPos);
			halfEdges.set (prevPos * 5 + 4, hePos);
		}

		if (twinPos > -1)
		{
			halfEdges.set (twinPos * 5 + 2, hePos);
		}

	}

	private double angleBetween (double vertMainX, double vertMainY, double vertSectorX, double vertSectorY,
			double d, double e)
	{
		double angle1 = Math.atan2 (e - vertSectorY, d - vertSectorX);
		double angle2 = Math.atan2 (vertMainY - vertSectorY, vertMainX - vertSectorX);

		if (angle1 < 0.0f)
			angle1 = (angle1 + 2 * Math.PI);
		if (angle2 < 0.0f)
			angle2 = (angle2 + 2 * Math.PI);

		angle1 = angle1 - angle2;
		if (angle1 < 0.0f)
		{
			angle1 = (2 * Math.PI + angle1);
		}
		
		return angle1;
	}

	/*
	 * returns the angle between the edge between the points (x1,y1) and (x2,y2) and 
	 * the edge between the points (x2,y2) and (x3,y3)*/

	/*
	 * This method returns the outgoing half-edge of the Main vertex, which lies inside 
	 * the same sector as the Sector Vertex*/
	public int getSector (int vertMainPos, int vertSectorPos, int clusterNo)
	{

		int outgoingHalfEdges [] = vertexGetOHeArray (vertSectorPos);
		
		int neig = outgoingHalfEdges.length;
		int outHe = -1;
		double currentAngle = 0.0d;

		int axes[] = getAxes (clusterNo);

		double vertMainX = getVertexCoordinate (vertMainPos, axes[0], false);
		double vertMainY = getVertexCoordinate (vertMainPos, axes[1], false);

		double vertSectorX = getVertexCoordinate (vertSectorPos, axes[0], false);
		double vertSectorY = getVertexCoordinate (vertSectorPos, axes[1], false);

		for (int i = 0; i < neig; i++)
		{
			
			try
			{
				int oHe = outgoingHalfEdges[i];
				
				if (heGetFace (oHe) == clusterNo
					|| heGetFace (heGetTwin (oHe)) == clusterNo)
				{
					int newVertex = heGetVertex (oHe);
					double newAngle = angleBetween (vertMainX, vertMainY,
						vertSectorX, vertSectorY,
						getVertexCoordinate (newVertex, axes[0], false),
						getVertexCoordinate (newVertex, axes[1], false));
					if (newAngle > currentAngle)
					{
						currentAngle = newAngle;
						outHe = oHe;
					}
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				System.out.println("vertM "+vertMainPos+" vertS = "+vertSectorPos+" clusterN = " + clusterNo);
				for (int j=0 ; j<outgoingHalfEdges.length ; j++){System.out.println(outgoingHalfEdges[j]);}
				System.out.println("i = "+i);
				e.printStackTrace();
			}
		}
		if (outHe > -1 && heGetFace (outHe) == clusterNo)
		{
			return outHe;
		}
		else
		{
			return -1;
		}

	}

	/*
	 * returns the 2-dimensional dot product*/

	public float dot2d (float x1, float y1, float x2, float y2)
	{
		return x1 * x2 + y1 * y2;
	}

	private boolean addEdgeBetweenForce (int vert1Pos, int vert2Pos, int clusterNo)
	{
		int he1 = getSector (vert1Pos, vert2Pos, clusterNo);
		int he2 = getSector (vert2Pos, vert1Pos, clusterNo);
		if (he1 <= -1 || he2 <= -1)
		{
			return false;
		}
		
		if(heGetPrev (he1)>=0 && heGetPrev (he2)>=0){
			addEdgeBetween (heGetPrev (he1), heGetPrev (he2));
		}
		return true;
	}
	
	private boolean addEdgeBetween (int vert1Pos, int vert2Pos, int clusterNo)
	{
		
		int he1 = getSector (vert1Pos, vert2Pos, clusterNo);
		int he2 = getSector (vert2Pos, vert1Pos, clusterNo);
		if (he1 <= -1 || he2 <= -1)
		{
			return false;
		}
		
		int vertex1 = heGetVertex(he1);
		int vertex2 = heGetVertex(he2);
		int vertex3 = heGetVertex(heGetPrev(heGetPrev(he1)));
		int vertex4 = heGetVertex(heGetPrev(heGetPrev(he2)));
				
		Tuple3d vertex1T3d = vertexGetTuple3d (vertex1);
		Tuple3d vertex2T3d = vertexGetTuple3d (vertex2);
		Tuple3d vertex3T3d = vertexGetTuple3d (vertex3);
		Tuple3d vertex4T3d = vertexGetTuple3d (vertex4);
		Tuple3d vertex1PosT3d = vertexGetTuple3d (vert1Pos);
		Tuple3d vertex2PosT3d = vertexGetTuple3d (vert2Pos);
		
		if ( pointInBV(vertex1PosT3d , vertex2PosT3d , vertex1T3d))
		{
			double d = IntersectionTests.distanceToLine (vertex1PosT3d,
				vertex2PosT3d, vertex1T3d);
			if (EPSILON_d > d && -EPSILON_d < d)
			{
				return false;
			}
		}
		if (pointInBV(vertex1PosT3d , vertex2PosT3d , vertex2T3d))
		{
			double d = IntersectionTests.distanceToLine (vertex1PosT3d,
				vertex2PosT3d, vertex2T3d);
			if (EPSILON_d > d && -EPSILON_d < d)
			{
				return false;
			}
		}
		if (pointInBV(vertex1PosT3d , vertex2PosT3d , vertex3T3d))
		{
			double d = IntersectionTests.distanceToLine (vertex1PosT3d,
				vertex2PosT3d, vertex3T3d);
			if (EPSILON_d > d && -EPSILON_d < d)
			{
				return false;
			}
		}
		if (pointInBV(vertex1PosT3d , vertex2PosT3d , vertex4T3d))
		{
			double d = IntersectionTests.distanceToLine (vertex1PosT3d,
				vertex2PosT3d, vertex4T3d);
			if (EPSILON_d > d && -EPSILON_d < d)
			{
				return false;
			}
		}
		if(heGetPrev (he1)>=0 && heGetPrev (he2)>=0){	
			addEdgeBetween (heGetPrev (he1), heGetPrev (he2));
		} else{
			return false;
		}
		return true;
	}
	
	public boolean pointInBV(Tuple3d v1, Tuple3d v2, Tuple3d point){
		
		Tuple3d min = new Point3d();
		Tuple3d max = new Point3d();
		
		if(v1.x<v2.x){
			min.x=v1.x;
			max.x=v2.x;
		} else{
			min.x=v2.x;
			max.x=v1.x;
		}
		if(v1.y<v2.y){
			min.y=v1.y;
			max.y=v2.y;
		} else{
			min.y=v2.y;
			max.y=v1.y;
		}
		if(v1.z<v2.z){
			min.z=v1.z;
			max.z=v2.z;
		} else{
			min.z=v2.z;
			max.z=v1.z;
		}
		
		//add Epsilon
		min.x=min.x-EPSILON_d;
		min.y=min.y-EPSILON_d;
		min.z=min.z-EPSILON_d;
		
		max.x=max.x+EPSILON_d;
		max.y=max.y+EPSILON_d;
		max.z=max.z+EPSILON_d;
		
		//test if point is in volume
		
		if(point.x>= min.x && point.x<= max.x &&
				point.y>= min.y && point.y<= max.y &&
				point.z>= min.z && point.z<= max.z){
			return true;
		}
		return false;
	}
	
	public void addClusterNormal (double x, double y, double z)
	{
		clusterNormal.push (x, y, z);
	}

	public Tuple3d getVertexTuple3f (int vertexPos)
	{
		return new Point3d (vertices.get (3 * vertexPos),
			vertices.get (3 * vertexPos + 1), vertices.get (3 * vertexPos + 2));
	}

	public void draw (GL gl)
	{
		drawLines (gl);
		drawHalfEdges(gl);

	}
	
	public void drawShaded (GL gl) {
		
		System.out.println("uv "+uv.size);
		System.out.println(uv);
		System.out.println("normals "+normals.size);
		System.out.println(normals);
		System.out.println("halfedges "+halfEdges.size);
		System.out.println(halfEdges);
		
		
		boolean drawn[] = new boolean[heGetCount()];
		
		for (int i = 0 ; i < heGetCount() ; i++)
		{
			if(!drawn[i]){
				int heStart = i;
				drawn[i]=true;
				
				IntList heToDraw = new IntList();
				
				heToDraw.push (heStart);
				
				int heItter=heStart;
				
				while (heGetNext(heItter)!=heStart)
				{
					heItter=heGetNext(heItter);
					drawn[heItter]=true;
					heToDraw.push( heItter );
				}
				
				gl.glBegin (GL.GL_POLYGON);
				
				for (int j=0 ; j<heToDraw.size() ; j++){
					
					int currentHe = heToDraw.get (j);
					
					int vertex = heGetVertex(currentHe);
					
					gl.glNormal3f (normals.get (currentHe*3), normals.get (currentHe*3+1), normals.get (currentHe*3+2));
					
					gl.glTexCoord2f(uv.get (currentHe*2) , uv.get (currentHe*2+1));
					
					gl.glVertex3d ( vertices.get (vertex*3), vertices.get (vertex*3+1), vertices.get (vertex*3+2));
					
					
					
				}
				
				gl.glEnd ();
				
				
			}
			else{}
			
			
		}
		
	}

	public void drawLines (GL gl)
	{
		gl.glDisable (GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_LIGHTING);
		for (int i = 0; i < heGetCount (); i++)
		{
			
			gl.glLineWidth (3.0f);
			gl.glColor3f (0f, 1f, 0f);
			gl.glBegin (GL.GL_LINES);
			int fromPos = heGetOrigin (i);
			int toPos = heGetDest (i);

			if(fromPos > -1 && toPos>-1 ){
			gl.glVertex3d (vertices.get (fromPos * 3),
				vertices.get (fromPos * 3 + 1), vertices.get (fromPos * 3 + 2));
			gl.glVertex3d (vertices.get (toPos * 3),
				vertices.get (toPos * 3 + 1), vertices.get (toPos * 3 + 2));
			}
			gl.glEnd ();
		}

		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable (GL.GL_DEPTH_TEST);
		gl.glLineWidth (1.0f);
	}

	public void drawHalfEdges (GL gl)
	{
		for (int i = 0; i < clusterNormal.size () / 3; i++)
		{
			double length = clusterNormal.get (i * 3)
				* clusterNormal.get (i * 3);
			length = length + clusterNormal.get (i * 3 + 1)
				* clusterNormal.get (i * 3 + 1);
			length = length + clusterNormal.get (i * 3 + 2)
				* clusterNormal.get (i * 3 + 2);

			length = (float) Math.sqrt ((double) length);

			clusterNormal.set (i * 3, clusterNormal.get (i * 3) / length);
			clusterNormal.set (i * 3 + 1, clusterNormal.get (i * 3 + 1)
				/ length);
			clusterNormal.set (i * 3 + 2, clusterNormal.get (i * 3 + 2)
				/ length);

		}

		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc (GL.GL_LEQUAL);
		for (int i = 0; i < heGetCount (); i++)
		{

			gl.glLineWidth (2.0f);
			gl.glDisable (GL.GL_LIGHTING);
			gl.glBegin (GL.GL_LINES);
			
			
			if(heStatus.get (i)==BORDER){
				gl.glLineWidth (2.0f);
				
				gl.glColor3f (1f, 0f, 0f);
			} else {
				gl.glColor3f (0f, 1f, 0f);
			}
			

			int fromPos = heGetOrigin (i);
			int toPos = heGetDest (i);

			if (fromPos>-1 && toPos>-1){
			gl.glVertex3d (vertices.get (fromPos * 3),
				vertices.get (fromPos * 3 + 1), vertices.get (fromPos * 3 + 2));
			gl.glVertex3d (vertices.get (toPos * 3),
				vertices.get (toPos * 3 + 1), vertices.get (toPos * 3 + 2));
			}
			gl.glEnd ();

			gl.glLineWidth (3.0f);

			gl.glBegin (GL.GL_LINE_STRIP);
			if (fromPos>-1 && toPos>-1 /*&& heGetVertex(i)==4*/){
			gl.glColor3f (0f, 0f, 1f);

			Tuple3d a = new Point3d ();
			a.set (vertices.get (toPos * 3) - vertices.get (fromPos * 3),
				vertices.get (toPos * 3 + 1) - vertices.get (fromPos * 3 + 1),
				vertices.get (toPos * 3 + 2) - vertices.get (fromPos * 3 + 2));
			Tuple3d n = new Point3d ();
			n.set (clusterNormal.get (heGetFace (i) * 3),
				clusterNormal.get (heGetFace (i) * 3 + 1),
				clusterNormal.get (heGetFace (i) * 3 + 2));
			
			Tuple3d nd = new Point3d();
			nd.set(n);
			
			Tuple3d b = IntersectionTests.CROSS (nd, a);
			b.scale (0.05d);

			gl.glVertex3d (vertices.get (fromPos * 3) + b.x + a.x * 0.15f,
				vertices.get (fromPos * 3 + 1) + b.y + a.y * 0.15f,
				vertices.get (fromPos * 3 + 2) + b.z + a.z * 0.15f);
			gl.glVertex3d (vertices.get (toPos * 3) + b.x - a.x * 0.15f,
				vertices.get (toPos * 3 + 1) + b.y - a.y * 0.15f,
				vertices.get (toPos * 3 + 2) + b.z - a.z * 0.15f);
			gl.glVertex3d (vertices.get (toPos * 3) + b.x * 2 - a.x * 0.3f,
				vertices.get (toPos * 3 + 1) + b.y * 2 - a.y * 0.3f,
				vertices.get (toPos * 3 + 2) + b.z * 2 - a.z * 0.3f);

			
			}
			gl.glEnd ();
			gl.glDepthFunc (GL.GL_LESS);

		}
		gl.glLineWidth (1.0f);
		gl.glColor3f (1f, 1f, 1f);
		gl.glEnable (GL.GL_LIGHTING);
		gl.glEnable(GL.GL_DEPTH_TEST);

	}

	public void addEdges (IntList edges, FloatList normalData , FloatList texData)
	{
		for (int i = 0; i < edges.size / 3; i++)
		{
			addExteriorEdge (edges.get (3 * i), edges.get (3 * i + 1),
				edges.get (3 * i + 2), normalData.get (3*i) , normalData.get (3*i+1), normalData.get (3*i+2) ,
				texData.get (2*i) , texData.get (2*i+1) );
		}
	}

	//Getter

	public int heGetVertex (int hePos)
	{
		return heGetDest (hePos);
	}

	public int heGetDest (int hePos)
	{
		if (hePos > -1)
			return halfEdges.get (5 * hePos);
		else
			return -1;
	}

	public int heGetOrigin (int hePos)
	{
		if (heGetPrev (hePos) > -1 && hePos > -1)
			return heGetDest (heGetPrev (hePos));
		else if (heGetTwin (hePos) > -1 && hePos > -1)
			return heGetDest (heGetTwin (hePos));
		else
			return -1;
	}

	public int heGetFace (int hePos)
	{
		return halfEdges.get (5 * hePos + 1);
	}

	public int heGetTwin (int hePos)
	{
		if (hePos != -1)
		{
			return halfEdges.get (5 * hePos + 2);
		}
		else
		{
			return -1;
		}
	}

	public int heGetPrev (int hePos)
	{
		if (hePos != -1)
		{
			return halfEdges.get (5 * hePos + 3);
		}
		else
		{
			return -1;
		}
	}

	public int heGetNext (int hePos)
	{
		if (hePos > -1)
		{
			return halfEdges.get (5 * hePos + 4);
		}
		else
		{
			return -1;
		}
	}

	public int vertexSetHe (int verticePos, int hePos)
	{
		return vertexToHalfEdge.set (verticePos, hePos);
	}
	
	public int[] vertexGetIHeArray(int vertex){
		IntList temp = new IntList();
		
		for (int i=0 ; i<heGetCount() ; i++){
			if(heGetVertex(i)==vertex){
				temp.push (i);
			}
		}
		
		temp.trimToSize ();
		return temp.toArray ();
	}
	
	public int[] vertexGetOHeArray(int vertex){
		IntList temp = new IntList();
		
		for (int i=0 ; i<heGetCount() ; i++){
			if(heGetVertex(i)==vertex && heGetTwin(i)>-1){
				temp.push (heGetTwin(i));
			}
		}
		
		temp.trimToSize ();
		return temp.toArray ();
	}
	
	public Tuple3d vertexGetTuple3d(int vertPos){
		return new Point3d(vertices.get (vertPos*3) , vertices.get (vertPos*3+1) , vertices.get (vertPos*3+2) );
	}
	
	public int vertexGetHe (int verticePos)
	{
		return vertexToHalfEdge.get (verticePos);
	}

	public int heGetCount ()
	{
		return halfEdges.size () / 5;
	}
	
	public int getVertexPosition (double x, double y, double z)
	{
		for (int i = 0; i < vertices.size () / 3; i++)
		{
			if (vertices.get (i * 3) - EPSILON_D_VERT < x
				&& vertices.get (i * 3) + EPSILON_D_VERT > x
				&& vertices.get (i * 3 + 1) - EPSILON_D_VERT < y
				&& vertices.get (i * 3 + 1) + EPSILON_D_VERT > y
				&& vertices.get (i * 3 + 2) - EPSILON_D_VERT < z
				&& vertices.get (i * 3 + 2) + EPSILON_D_VERT > z)
				return i;
		}
		return -1;
	}
	
	public String toString ()
	{
		String out = new String();
		
		out+="Start of border "+startOfBorder+"\n";
		
		out+=triangleToShader+"\n";
		
		for(int i = 0 ; i < vertices.size()/3 ; i++){
			out+="( "+i+" ) [ "+vertices.get (3*i)+" ; "+vertices.get (3*i+1)+" ; "+vertices.get (3*i+2)+" ]"+"\n";
		}
		
		
		for (int i = 0; i < heGetCount(); i++)
		{
			out+="(" + i + ")" + "   {" + halfEdges.get (i * 5)
				+ " , " + halfEdges.get (i * 5 + 1) + " , "
				+ halfEdges.get (i * 5 + 2) + " , " + halfEdges.get (i * 5 + 3)
				+ " , " + halfEdges.get (i * 5 + 4) + " , s= " + heStatus.get (i)
				+ " } "+" uv { "+uv.get (i*2)+" ; "+uv.get (i*2+1)+" }"+"\n";
		}
		return out;
	}

	public void addIntersectionLine (DoubleList intersectionLine,
			IntList intersectionClusters, FloatList normalData, FloatList uv)
	{
		int count=0;

		for (int i = 0; i < intersectionLine.size () / 6; i++)
		{
			
			
			int from = getVertexPosition (intersectionLine.get (i * 6),
				intersectionLine.get (i * 6 + 1),
				intersectionLine.get (i * 6 + 2));
			int to = getVertexPosition (intersectionLine.get (i * 6 + 3),
				intersectionLine.get (i * 6 + 4),
				intersectionLine.get (i * 6 + 5));

			if (from < 0)
			{

				from = vertices.size () / 3;
				vertices.push (intersectionLine.get (i * 6),
					intersectionLine.get (i * 6 + 1),
					intersectionLine.get (i * 6 + 2));

				vertexToHalfEdge.set (from, -1);
				
			}
			if (to < 0)
			{
				to = vertices.size () / 3;
				vertices.push (intersectionLine.get (i * 6 + 3),
					intersectionLine.get (i * 6 + 4),
					intersectionLine.get (i * 6 + 5));
				vertexToHalfEdge.set (to, -1);
			}
						
			if ((from!=to) && !isAdjecentVV (from, to))
			{
				count++;
				addIntersectionLine (from, to, intersectionClusters.get (i), uv.get (i*4) , uv.get (i*4+1) , uv.get (i*4+2), uv.get (i*4+3) , normalData.get (6*i) , normalData.get (6*i+1) , normalData.get (6*i+2), normalData.get (6*i+3), normalData.get (6*i+4), normalData.get (6*i+5) );
			}

		}

	}	
	
	
	public int vertexGetOHalfEdgeCluster (int vertexPos, int clusterNo)
	{
		int oHe[]= vertexGetOHeArray(vertexPos);
		
		for (int i=0 ; i<oHe.length ; i++){
			if(heGetFace(oHe[i])==clusterNo){
				return oHe[i];
			}
		}
		
		return -1;
	}

	public int getNextIHeCW (int hePos)
	{
		return heGetTwin (heGetNext (hePos));
	}

	public int getNextOHeCW (int hePos)
	{
		return heGetNext (heGetTwin (hePos));
	}

	public void orderTriangles ()
	{

		boolean debug = false;

		if (debug)
		{
			System.out.println ("before");
			System.out.println(this);
		}

		for (int i = 0; i < heGetCount(); i += 3)
		{

			int next = heGetNext (i);

			heSwitchPos (next, i + 1);

			int prev = heGetPrev (i);
			heSwitchPos (prev, i + 2);
		}

		if (debug)
		{
			System.out.println ("after");
			System.out.println(this);
		}
	}

	public void heSwitchPos (int he1Pos, int he2Pos)
	{
		//update references
		updatePosition (he1Pos, he2Pos);

		int vert = heGetVertex (he1Pos);
		int face = heGetFace (he1Pos);
		int twin = heGetTwin (he1Pos);
		int prev = heGetPrev (he1Pos);
		int next = heGetNext (he1Pos);
		byte status = heGetStatus (he1Pos);

		heSetVertex (he1Pos, heGetVertex (he2Pos));
		heSetFace (he1Pos, heGetFace (he2Pos));
		heSetTwin (he1Pos, heGetTwin (he2Pos));
		heSetPrev (he1Pos, heGetPrev (he2Pos));
		heSetNext (he1Pos, heGetNext (he2Pos));
		heSetStatus (he1Pos, heGetStatus (he2Pos));

		heSetVertex (he2Pos, vert);
		heSetFace (he2Pos, face);
		heSetTwin (he2Pos, twin);
		heSetPrev (he2Pos, prev);
		heSetNext (he2Pos, next);
		heSetStatus (he2Pos, status);
		
		float normalX = normals.get (he1Pos*3);
		float normalY = normals.get (he1Pos*3 + 1);
		float normalZ = normals.get (he1Pos*3 + 2);
		
		normals.set (he1Pos*3 , normals.get (he2Pos*3) );
		normals.set (he1Pos*3 + 1 , normals.get (he2Pos*3 + 1) );
		normals.set (he1Pos*3 + 2 , normals.get(he2Pos*3 + 2) );
		
		normals.set (he2Pos*3 , normalX );
		normals.set (he2Pos*3 + 1 , normalY );
		normals.set (he2Pos*3 + 2 , normalZ );
		
		float texU = uv.get ( he1Pos*2 );
		float texV = uv.get ( he1Pos*2 + 1 );
		
		uv.set (he1Pos*2, uv.get (he2Pos*2));
		uv.set (he1Pos*2 + 1 , uv.get (he2Pos*2+1) );
		
		uv.set (he2Pos*2, texU);
		uv.set (he2Pos*2 + 1 , texV );
		
		updatePosition (he1Pos, he1Pos);

		if (vertexGetHe (heGetVertex (he1Pos)) == he2Pos)
		{
			vertexSetHe (heGetVertex (he1Pos), he1Pos);
		}
	}

	public void updatePosition (int heOldPos, int heNewPos)
	{
		//update the Twin
		heSetTwin (heGetTwin (heOldPos), heNewPos);
		//update the next
		heSetPrev (heGetNext (heOldPos), heNewPos);
		//update the prev
		heSetNext (heGetPrev (heOldPos), heNewPos);
		//update vertex reference if any
		if (vertexGetHe (heGetVertex (heOldPos)) == heOldPos)
		{
			vertexSetHe (heGetVertex (heOldPos), heNewPos);
		}
	}

	public byte heGetStatus (int hePos)
	{
		return heStatus.get (hePos);
	}

	public void heSetStatus (int hePos, byte status)
	{
		heStatus.set (hePos, status);
	}

	public void addShader (int i)
	{
		triangleToShader.add (i);
		
	}
	
	public void check(){
		
		if(uv.size/2<heGetCount()){
			System.out.println("Not enough uv coodinates");
			for (int i=uv.size/2 ; i<halfEdges.size/5 ; i++){
				uv.push (0.0f,0.0f);
			}
		} else{
			System.out.println("uv check passed");
			System.out.println ("he "+halfEdges.size);
			System.out.println ("uv "+uv.size);
			
		}
		
	}
	
	public boolean verifyLink(){
		for (int i=0 ; i<heGetCount () ; i++){
			if(heGetTwin(i)<0){
				return false;
			}
			if(heGetPrev(i)<0){
				return false;
			}
			if(heGetNext(i)<0){
				return false;
			}
			
			
		}
		return true;
	}
	

}
