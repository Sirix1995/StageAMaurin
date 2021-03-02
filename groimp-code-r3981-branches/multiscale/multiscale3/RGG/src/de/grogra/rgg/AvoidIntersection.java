package de.grogra.rgg;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Null;
import de.grogra.imp3d.ray2.SceneVisitor;
import de.grogra.imp3d.ray2.VolumeListener;
import de.grogra.math.RGBColor;
import de.grogra.persistence.Transaction;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray2.Options;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.MeshVolume;
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.LongToIntHashMap;
import de.grogra.xl.util.Operators;

/**
 * Instances of <code>AvoidIntersection</code> helps to detected 
 * potential intersection by using rays 
 * ({@link de.grogra.vecmath.geom.Line}). Before something grow in 
 * a certain direction, this helps to check if objects lies in 
 * the near environment of the growing-direction.<br><br>
 * 
 * Example to use: <br>
 * <code>AvoidIntersection ai = new AvoidIntersection(100);<br>
 * ai.look(node);<br>
 * </code><br>
 * The node gets a new direction.
 * 
 * @author Stephan Rogge
 *
 */
public class AvoidIntersection implements Options, VolumeListener
{
	/**
	 * How many rays has to be shoot.
	 */
	private int RayCount;	
	
	/**
	 * This is the original direction of the node.
	 */
	private Vector3d direction;
	
	/**
	 * This is the position of the current node and of all rays.
	 */
	private Point3d origin;
	
	/**
	 * Iff the predicted length of the next added object is not zero
	 * the origin of all rays is translateted to that point. Otherwise
	 * this point is equal to the origin of the actual node.
	 */
	private Point3d rayOrigin;
		
	/**
	 * All rays are stored in this ArrayList. 
	 */
	private ArrayList<Line> rays;
	
	/** 
	 * In this array are stored, whether a ray is
	 * intersect something.
	 */
	private int[] hitList;
	private double[] hitDistance;
	
	private double hitRatio = 0;
	
	private double hitRatioPoint = 0;
	
	private boolean prepareScene = true;
	
	private float rangeWidth = 1;
	private float rangeHeight = 1;
	private float predictedLength = 0.1f;
	
	public static final int NO_HIT = 0;
	public static final int FRIENDLY_HIT = 1; 
	public static final int UNFRIENDLY_HIT = 2;

	private Class<?> favorNodeType;
	private ArrayList<Node> favorNodes;
	private LongList favorVolumeID;
	private float disToNodeSurface = 0.1f;
	private double minParameter;
	private int minDistRay = -1;
	
	/**
	 * Vector for the sum of free directions.
	 */
	private Vector3d freeVec	 	= new Vector3d();
	/**
	 * Vector for the sum of un-free directions.
	 */
	private Vector3d unfreeVec  	= new Vector3d();
	
	boolean allLinesFree;
	boolean allLinesUnFree;
	
	private Node originNode;
	private long originVolumeID;
	private Volume originVolume;
	
	private transient boolean[] visibleLayers;
	private transient int currentGroupIndex;
	private transient int nextGroupIndex;
	private transient int grouping;
	private transient IntList groupToVolumeId;
	private transient LongToIntHashMap nodeToGroup;	
	private IntersectionList ilist;
	private Intersection is;
	private SceneVisitor scene;
	private Spectrum3d spec;
	
	private ArrayList<Line> friendlyRays;
	private ArrayList<Line> normals;
	
	private boolean allLinesFriendlyFree = true;
	
	
	/**
	 * Create an instance of <code>AvoidIntersection</code> with
	 * 100 rays.
	 */
	public AvoidIntersection()
	{
		this (100);
	}
	
		
	/**
	 * Create an instance of <code>AvoidIntersection</code> with
	 * {@link RayCount} rays.
	 * 
	 * @param RayCount Number of rays.
	 */
	public AvoidIntersection(int RayCount)
	{
		this.RayCount 	= RayCount;
		
		// Initialize the ray-ArrayList
		rays 			= new ArrayList<Line>(RayCount + 1);
		// Initialize the Intersection hitList
		hitList 		= new int[RayCount + 1];
		
		hitDistance		= new double[RayCount + 1];
		
		friendlyRays	= new ArrayList<Line>(RayCount + 1);
		normals			= new ArrayList<Line>(RayCount + 1);
		
		favorVolumeID	= new LongList();
		
		// data is out-of-date, recompute the scene
		
		if (nodeToGroup == null)
		{
			nodeToGroup = new LongToIntHashMap ();
			groupToVolumeId = new IntList ();
		}
		else
		{
			nodeToGroup.clear ();
			groupToVolumeId.clear ();
		}
		
		visibleLayers = new boolean[Attributes.LAYER_COUNT];
		for (int i = 0; i < Attributes.LAYER_COUNT; i++)
		{
			visibleLayers[i] = true;
		}
		
		grouping 			= 0;
		nextGroupIndex 		= 1;
		currentGroupIndex 	= -1;
				
		spec			= new Spectrum3d();			
		
		// Create RayCount empty rays
		for(int i = 0; i <= RayCount; i++)
		{
			rays.add(new Line());
			friendlyRays.add(new Line());
			normals.add(new Line());
		}	
		
		hitRatioPoint	= 1.0/((double) RayCount);
		minParameter = 0;
		
		favorNodes = new ArrayList<Node>();
	}
	
	/**
	 * Set layer # <code>id</code> to visible (true) or invisible (false).
	 * 
	 * @param id 		Layer id
	 * @param visible 	Layer visible or not.
	 */
	public void setLayerVisible(int id, boolean visible)
	{
		if(id > 0)
			visibleLayers[id] = visible;
	}	
	
	/**
	 * This method sets the <code>prepareScene</code> flag. When the method 
	 * {@link #prepareScene()} is invoked, an octree will re-computed.
	 */
	public void setPrepareScene()
	{
		prepareScene = true;
	}
	
	/**
	 * For explanation see {@link #setRange(float, float, float)}.
	 * 
	 * @param width				Width of the tested space.
	 * @param height			Height of the tested space.
	 */
	public void setRange(float width, float height)
	{
		setRange(width, height, 0);
	}	

	/**
	 * Set the width and height of the space which has to be test on intersection.
	 * With <code>predictedLength</code> the origin of all rays is shift along
	 * the direction line of the <code>originNode</code>.
	 * 
	 * @param width				Width of the tested space.
	 * @param height			Height of the tested space.
	 * @param predictedLength	Distance of shifting the ray origin.
	 */
	public void setRange(float width, float height, float predictedLength)
	{
		if(width >= 0)
			this.rangeWidth = width;
		
		if(height >= 0)
			this.rangeHeight = height;
		
		this.predictedLength = predictedLength;
	}
	
	/**
	 * To get more or less tangential directions to certain objects (called as
	 * favor nodes), we have mark them. This can be done by implementing an
	 * interface. This interface is <code>className</code>.
	 * 
	 * @param className Interface type which is implemented by some objects of the
	 * 					scene.
	 */
	public void setFavorNodeType(Class<?> className)
	{
		favorNodeType = className;
	}

	/**
	 * Adds <code>node</code> to the list of <code>favorNodes</code>.
	 * Ray intersection with this <code>node</code> is marked as friendly.
	 * So a direction is calculated which is approximate tangential to the
	 * surface at the point where the intersection was computed.
	 * 
	 * @param node 
	 */
	public void addFavorNode(Node node)
	{
		if(node != null)
			favorNodes.add(node);
	}
	
	/**
	 * This method sets the distance of tangential plane to
	 * all nodes in <code>favorNodes</code>.
	 * 
	 * @param distance Distance to surface
	 */
	public void setDistance2Surface(float distance)
	{
		this.disToNodeSurface = distance;
	}
	
	/**
	 * This method prepare the whole scene, may for a new 
	 * intersection-computation. This is necessary, when something
	 * in the scene has changed.
	 * The method {@link look(Null node, float size, float distance)}
	 * invoke this automatically.
	 */
	public void prepareScene()
	{		
		if(prepareScene || scene == null)
		{		
			// Free the memory of all members of the SceneVisitor
			if(scene != null)
				scene.dispose();
			
			// This is needed by the SceneVisitor
			grouping 			= 0;
			nextGroupIndex 		= 1;
			currentGroupIndex 	= -1;
			
			
			// Cleaning up some lists.
			favorVolumeID.clear();
			nodeToGroup.clear();
			groupToVolumeId.clear();
			
			// By creating a new SceneVisitor, an octree will constructed.
			scene = new SceneVisitor (Workbench.current(), 
										Workbench.current().getRegistry().getProjectGraph(), 
										1e-5f, this, null,
										visibleLayers, this, spec
									);
			
			// Setting this flag as false so that a following invocation of 
			// this method doesn't have the effect to instantiate a new SceneVistor and re-build
			// an octree.
			prepareScene 			= false;	
			
		} else {
			
			// May this method is invoked by several rules with different nodes. To obtain the
			// volume id for excluding the volume of current node during the intersection tests.
			originVolumeID 	= groupToVolumeId.get(nodeToGroup.get(originNode.getId()));
			originVolume	= scene.getOctree().volumes.get((int) originVolumeID);
		}

		minDistRay			= -1;
		
		// Setting all stored information of rays to zero. We don't need these information anymore, but
		// need the allocated memory for the next calculations.
		setListToZero(rays);
		setListToZero(friendlyRays);
		setListToZero(normals);
		
		// Another reseting.
		ilist 	= new IntersectionList();
		is 		= new Intersection(new IntersectionList());
	}
	
	/**
	 * This method works exactly like {@link #look(Null, float, float, boolean)}. Expect that the
	 * strength of changing the direction is set to 1 and no rays were visualized.
	 * 
	 * @param node			For this node the environment has to be checked and a new direction 
	 * 						could be the result.
	 * @param distance		The distance is used as length for the rays. Thats a limitation for
	 * 						the near environment.
	 * @return				Iff every ray has an unfriendly intersection with objects, this method
	 * 						return false otherwise true.
	 */
	public boolean look(Null node, float distance)
	{
		return look(node, distance, 1, false);
	}
	
	/**
	 * This methods compute based on a intersection test with rays a new direction for 
	 * <code>node</node>. The area in front of <code>node</code> is defined by 
	 * {@link #setRange(float, float, float)} and by <code>distance</code>.
	 * Iff a new direction is calculated a transformation node which approximate the new direction
	 * by the given value of <code>strength</code> is put into the graph.
	 * Depending on objects which is involved in intersections the new direction could be tangential
	 * to the surface of an favor node a try get away from this object.
	 * 
	 * @param node 			For this node the environment has to be checked and a new direction 
	 * 						could be the result.
	 * @param distance		The distance is used as length for the rays. Thats a limitation for
	 * 						the near environment.
	 * @param strength		Iff a new direction is calculated, this parameter sets the strength
	 * 						of changing a direction.
	 * @param showLines		To visualize what is going on in front of <code>node</code>, set this
	 * 						parameter true.
	 * @return				Iff every ray has an unfriendly intersection with objects, this method
	 * 						return false otherwise true.
	 */
	public boolean look(Null node, float distance, float strength, boolean showLines)
	{				
		// Storing the origin node. From that node, we check the near
		// environment.
		this.originNode  		= node;			
		
		// Constructing the octree with the <code>SceneVisitor</code> an initializing
		// or resetting some attributes.
		prepareScene();	
		
		// We need to know the constructed volume of the origin node. Thats necessary 
		// to exclude this volume during the intersection computation.
		is.volume 				= originVolume;		
		
		origin 					= Library.location(node);
		direction				= Library.direction (node);
		
		rayOrigin				= new Point3d(origin);
		Vector3d tempDir		= new Vector3d(direction);
		
		// If predictedLength is not 0 origin of all rays is not the 
		// location of <code>node</code>.
		if(this.predictedLength != 0)
		{
			// Set the length of the duplicated direction vector..
			tempDir.normalize();
			
			// .. to the length of <code>predictedLength</code>.
			tempDir.scale(this.predictedLength);
			
			// After obtaining the length, we shift the <code>rayOrigin</code>
			// from location of the <code>node</code> to the position after
			// putting a new object with the predicted length into the scene.
			rayOrigin.scaleAdd(predictedLength, tempDir, origin); 
		}
		
		// Setting up the first ray.
		Line ray 				= rays.get(0); 
			
		ray.origin.set(rayOrigin);		
		ray.direction.set(direction);
		
		ray.start 				= 0.0001;
		ray.end					= distance;
		
		hitList[0]				= NO_HIT;	
		hitDistance[0]			= 0;		
		
		hitRatio 				= 0;
		minParameter			= 0;		
		
		// Shoot all rays from rayOrigin into the near environment of 
		// node and look for intersection.
		shootRays(ray, rangeWidth, rangeHeight);
		
		// With information about which direction is free or which direction
		// is a favor node a new grow direction is computed.
		getNewDirection(0);
					
		// We create a transformation node with the new direction...
		Null tropism 	= Library.tropism(node, direction, strength);	
		
		// ... and put this node into the graph.
		Transaction xa 	= node.getGraph ().getActiveTransaction ();
		Node parent 	= node.findAdjacent (true, false, -1);
		if (parent != null)
			parent.getEdgeTo (node).remove (xa);
		
		parent.addEdgeBitsTo (tropism, Graph.SUCCESSOR_EDGE, xa);
		tropism.addEdgeBitsTo (node, Graph.SUCCESSOR_EDGE, xa);
		
		// Visualize all shoot rays.
		if(showLines)
			showLines();
			
		// Iff the calculations doesn't find a free direction, this method returns
		// false. Otherwise there is free direction for growing.
		return allLinesUnFree || !allLinesFriendlyFree;
	}
	
	/**
	 * This method trace every constructed line (<code>rays</code>) and decided, which type of 
	 * intersection the current ray computes.
	 * 
	 * @param index Which ray in <code>rays</code> is to checked.
	 */
	private void traceRay(int index)
	{		
		int ilistSize = ilist.size;	
		
		// Every ray has initially no intersection
		hitList[index] 		= NO_HIT;
		hitDistance[index] 	= 0;
		
		ilist.clear();
		Line ray 			= rays.get(index);
		
		// Ray of current index is send into the scene
		scene.computeIntersections (ray, Intersection.CLOSEST, ilist, is, null);		
		
		if (ilist.size > ilistSize)
		{			
			// There should be just one intersection	
			int i = 0;
			Intersection intersection;
			
			// Only intersections with an other node as <code>node</code>
			// has to be set as valid intersection
			do
			{				
				// Get the next intersection
				intersection = ilist.elements[i++];
				
				// The first volume has the volume originVolumeID,
				// and we don't want the intersection with the volume of this
				// <code>node</code>.
				if(intersection.volume.getId() != originVolumeID)			
					break;
				
			} while(i<ilist.size);
						
			// Check if no sky object was hit (parameter < INF)
			if (intersection.parameter < Double.POSITIVE_INFINITY && intersection.volume.getId() != originVolumeID)
			{
				// The intersection of this ray is valid. Now decide whether the object
				// is a favor node or not. Iff its not mark the ray as friendly and store
				// the information of the intersection like intersection point, direction
				// of the which computes this intersection and the normal vector.
				if( !belongsToObject(intersection.volume.getId()))
				{					
					// Mark this ray as an unfriendly hit.
					hitList[index] 				= UNFRIENDLY_HIT;
					// Store the distance of rayOrigin to intersection point
					hitDistance[index] 			= intersection.parameter;
					// Increment the hitRatio counter
					hitRatio 				   += hitRatioPoint;
					
				} else {
					
					// Mark this as friendly intersection
					hitList[index] 				= FRIENDLY_HIT;
					
					// Obtain the used line..
					Line line 					= intersection.line;
					
					// .. at set the end where the intersection point lies on that line.
					line.end 					= intersection.parameter;
				
					// Finally change the used ray (line).
					rays.set(index, line);						
					
					// Obtain the container-line for the normals for following calculation
					Line normalLine				= normals.get(index);
					
					// Obtain the container-line for the friendly line for following calculation
					Line friendlyLine			= friendlyRays.get(index);
					
					// Copy the information of the intersection to the normal-container.
					normalLine.origin.set(intersection.getPoint());						
					normalLine.direction.set(intersection.getNormal());
					
					normalLine.end 				= 1;
												
					// Copy the information of the intersection to the friendly-ray-container.
					friendlyLine.origin.set(rayOrigin);					
					friendlyLine.direction.set(line.direction);
					
					friendlyLine.end 			= 1;		
										
					// Later for decision which ray is the base for calculation we choose the
					// ray with the shortest parameter. That usually means, the shortest
					// distance to a certain intersection point.
					if (intersection.parameter < minParameter || minParameter == 0)
					{
						// Store the actual shortest parameter..
						minParameter = intersection.parameter;
						
						// .. and the according index of the ray.
						minDistRay = index;						
					}					
				}
			} 
			
			ilist.setSize (ilistSize);			
		} 
	}
	
	/**
	 * This method computes a new direction for <code>node</code> which is approximate
	 * tangential to the surface of an object, where an intersection was computed. 
	 * Iff <code>allLinesFriendlyFree</code> is false, which means, that at least one
	 * ray hits a favor node, the old direction is tried to keep, by mapping it on the
	 * tangential plane. Otherwise just a point on the tangential plane is used as a
	 * new direction.
	 * 
	 * @param index Index of the ray which is the base for calculation.
	 * @return The new direction approximate tangential to the surface.
	 */
	private Vector3d getTangentDirection(int index)
	{	
		// Obtain the line which is orthogonal on a certain intersection point
		Line normalLine		= normals.get(index);
		
		// Normal vector of surface at the intersection point
		Vector3d norm		= new Vector3d(normalLine.direction);
				
		// A Point on the tangential plane (handled as vector)...	
		Vector3d Pnew 		= new Vector3d();
		
		/*
		 * .. is calculated with the parameter form of the normal line.
		 * To keep the distance to the surface we scale this line to
		 * <code>disToNodeSurface</code>. This new Point lies on a 
		 * tangential plane which has a distance of 
		 * <code>disToNodeSurface</code>.
		 */
		Pnew.scaleAdd(disToNodeSurface, norm, normalLine.origin);		
		
		/*
		 * The next step is only computed iff at least one ray hits a
		 * favor node.
		 */
		if(!allLinesFriendlyFree)	
		{
			// This would a point...
			Point3d Pold 		= new Point3d();
			
			// ... which lies on the old direction line.
			Pold.add(direction, rayOrigin);
			
			/*
			 * Here we map the old direction on the tangential plane: The notation
			 * of a plane is: 
			 * 
			 * 			P: a*x^2 + b*y^2 + c*z^2 = d
			 * 
			 * The components a,b and c accord the components of the normal vector
			 * of the tangential plane. To obtain d we calculate the dot product
			 * of a point on the plane and its normal vector.
			 */	
			double dotProd = Pnew.x*norm.x + Pnew.y*norm.y + Pnew.z*norm.z;
			
			/*
			 * With a parameter form of a line which points towards on the tangential
			 * plane and origin is Pold:
			 * 
			 * 			L: Pold + parameter * (normal vector)
			 * 
			 * ... we can obtain an intersection point on the tangential plane by 
			 * calculating the parameter.
			 */			
			double parameter 	 = dotProd - norm.x*Pold.x - norm.y*Pold.y - norm.z*Pold.z;			
			parameter 			/= ((-norm.x)*norm.x + (-norm.y)*norm.y + (-norm.z)*norm.z);
			
			Pnew.normalize();
			
			/*
			 * Obtaining the point which mapped the old direction on the tangential plane.
			 */
			Pnew.scaleAdd(-parameter, norm, Pold);						
		} 
		
		/*
		 * With the point on the tangential plane and the origin of all
		 * rays we obtain a new direction of our <code>node</code>.
		 */
		Pnew.sub(rayOrigin);		
		
		// DONE!
		return Pnew;
	}
	
	/**
	 * This method is invoked after the intersection test. With the information
	 * of distance from the <code>originNode</code> to intersection points
	 * and the kind of intersection, it is now possible to calculate the new 
	 * direction of the <code>originNode</code>.
	 * To obtain a free direction with no object intersection all free direction
	 * will summed in one vector and all un-free directions will be substrate
	 * from the sum.
	 * Maybe after obtaining the new free direction a tangential direction to
	 * a the surface of a certain object is calculated.
	 * May it happen that this method is invoked recursively. This is the case, 
	 * when the result of intersection calculation now free direction found.
	 * 	
	 * @param depth		Recursive depth. Not deeper then one.
	 */
	private void getNewDirection(int depth)
	{
		// Here all free direction were summed.
		freeVec.set(0,0,0);
		// Here all un-free direction were summed.
		unfreeVec.set(0,0,0);
		
		Line oldLine;
		
		// To obtain what has to happen later. 
		allLinesFree				= true;
		allLinesUnFree				= true;
		allLinesFriendlyFree		= true;
		
		oldLine 					= rays.get(0);
		Vector3d tempVec;
		
		// Depends on the kind of intersection may some vector additions..
		for(int i = 1; i< hitList.length; i++)
		{			
			// ... but at first we take ray number #i.
			tempVec 				= rays.get(i).direction;

			switch(hitList[i])
			{ 
				case NO_HIT :
					// NO_HIT means a free direction. So we can add this direction to freeVec.
					allLinesUnFree 	= false;
					freeVec.add(tempVec);
					break;
				case UNFRIENDLY_HIT:
					// UNFRIENDLY_HIT means a un-free direction. So we can add this direction to unfreeVec.
					// Before we do that, we have to scale this direction and ensure, that the distance isn't
					// inifity.
					allLinesFree 	= false;
					tempVec.scale(hitDistance[i] < Double.POSITIVE_INFINITY ? 1/hitDistance[i] : 0 );
					unfreeVec.add(tempVec);
					break;
				case FRIENDLY_HIT:
					// Just mark that there is friendly hit.
					allLinesFriendlyFree = false;
					break;
			}
		}
				
		// Here the free direction will computed.
		if(allLinesUnFree && depth == 0)
		{
			// Iff there was no free direction, try again to found a free direction.
			
			hitRatio = 0;					
			
			// Re-shoot all rays with more open angle...
			shootRays(rays.get(0), rangeWidth*20, rangeHeight*20);
			
			// ... and calculate one more time.
			getNewDirection(++depth);
		
		} else if(!allLinesFree) {
			
			// Iff at least one ray hit something weight the free direction...			
			freeVec.scale(10/(hitRatio*(rangeWidth*rangeHeight)));
			
			// ... and substrate all un-free direction.
			freeVec.sub(unfreeVec);				
					
			// Then normalize the direction...
			freeVec.normalize();
					
			// ... and replace the current direction of the originNode.
			direction.set(freeVec);					
		}
			
		// After obtaining and setting a free direction, may be the direction has to be
		// tangential to a point on surface of a certain object.
		if(depth == 0 && minDistRay > -1) 
		{			
			// Iff there is no ray which hit a friendly object, try it agein with rays in
			// every direction.
			if(allLinesFriendlyFree)
			{
				setListToZero(rays);
				setListToZero(friendlyRays);
				setListToZero(normals);
				
				minParameter 	= 0;				
				prepareScene 	= true;
				
				// Rebuild the octree.
				prepareScene();
				
				// Don't forget to exclude the volume of the originNode.
				is.volume 		= originVolume;
				
				// This shoot rays in all directions.
				shootInAllDirection(oldLine);
			}
			
			// Getting and setting the new direction iff a friendly ray is a favor.
			if(minDistRay > -1)			
				direction 		=  getTangentDirection(minDistRay);
			
		}
	}
	
	/**
	 * Shoots the rays into scene. The layout of all rays defined a pyramid with a round bottom.
	 * The which position and orientation depends on the given <code>ray</code>. The size of the
	 * pyramid is defined by <code>width</code> and <code>height</code>.
	 * 
	 * @param ray 		This ray is the basic for the alignment of the rays
	 * @param width 	Horizontal size of the pyramid.
	 * @param height 	Vertical size of the pyramid.	 * 
	 */
	private void shootRays(Line ray, float width, float height)
	{
		final Vector3d a 				= new Vector3d();
		final Vector3d b 				= new Vector3d();
		
		Vector3d originDirection 		= new Vector3d(ray.direction);
		
		if(direction.x != 0)
		{
			a.x = -direction.y;
			a.y = direction.x;
			a.z = 0;
			
		} else {
			
			a.x = 0;
			a.y = -direction.z;
			a.z = direction.y;
		}
		
		b.cross(originDirection, a);
		
		a.normalize();
		b.normalize();
		
		Line newLine;
		
		for(int i=1; i<=RayCount; i++)
		{			
			newLine 			= rays.get(i);			
			
			originDirection.scaleAdd(random(-width, width), a, direction);
			originDirection.scaleAdd(random(-height, height), b, originDirection);	
			
			originDirection.normalize();
			
			newLine.origin.set(rayOrigin);
			newLine.direction.set(originDirection);			
			
			newLine.start 		= ray.start;
			newLine.end 		= ray.end;
							
			traceRay(i);						
		}
	}
	
	/**
	 * This method shoot rays in all direction. Independence on <code>rangeWidth</code> or
	 * <code>rangeHeight.</code>
	 * 
	 * @param ray We need for computation origin, start and end parameter.
	 */
	private void shootInAllDirection(Line ray)
	{
		Vector3d randomDirection = new Vector3d();
		
		Line newLine;
		
		for(int i=1; i<=RayCount; i++)
		{			
			newLine 			= rays.get(i);
			
			randomDirection.x 	= Math.sin(random(0, 360));
			randomDirection.y 	= Math.sin(random(0, 360));
			randomDirection.z 	= Math.sin(random(0, 360));
						
			randomDirection.normalize();
						
			newLine.origin.set(rayOrigin);
			newLine.direction.set(randomDirection);	
			
			newLine.start 		= ray.start;
			newLine.end 		= ray.end;			
				
			traceRay(i);						
		}
	}
	
	/**
	 * This class stores some {@link de.grogra.vecmath.geom.Line} in some lists. 
	 * Here we can set all to zero.
	 * 
	 * @param list
	 */
	private void setListToZero(ArrayList<Line> list)
	{
		Line line;
		for(int i = 0; i < list.size(); i++)
		{
			line 				= list.get(i);
			
			line.origin.set(0,0,0);			
			line.direction.set(0,0,0);
			
			line.start 			= 0;
			line.end 			= 0;
		}
	}
	
	/** 
	 * Check if a certain volume id belongs to one favor node in <code>favorNodes</code>
	 * 
	 * @param volumeID	Id of a volume which has to be checked.
	 * @return			True iff volumeID belongs to a favor node.
	 */
	private boolean belongsToObject(long volumeID)
	{	
		if(favorVolumeID.size() > 0)
		{
			for(int i =0 ; i < favorVolumeID.size; i++)
				return volumeID == favorVolumeID.get(i);
		}
		return false;
	}
	
	
	private boolean isFavorNode(long nodeID)
	{	
		if(favorNodes.size() > 0)
		{
			for(int i =0 ; i < favorNodes.size(); i++)
				return nodeID == favorNodes.get(i).getId();
		}
		return false;
	}		
	
	private de.grogra.imp3d.objects.Line drawLine(int index)
	{
		if(index >= 0 && index <= RayCount)	
		{
			// Get the next ray.
			Line ray = rays.get(index);
			
			// Obtain where the intersection on the line lies, otherwise its the
			// given end of a ray.
			double end = hitDistance[index] > 0 && 
							hitDistance[index] < Double.POSITIVE_INFINITY &&
							hitDistance[index] > 0.001 ? 
									hitDistance[index] : 
										ray.end;  
			ray.direction.normalize();
			
			// To visualize the line a real line-object has to be create.
			de.grogra.imp3d.objects.Line tempLine = new de.grogra.imp3d.objects.Line (
							(float) ray.origin.x, (float) ray.origin.y, (float) ray.origin.z, 
							(float) (ray.direction.x * end), 
							(float) (ray.direction.y * end), 
							(float) (ray.direction.z * end));			
					
			// Find out, which type of line has to be visualized.
			if(index != 0)
			{
				RGBColor rgba;
			
				switch(hitList[index])
				{
					case UNFRIENDLY_HIT:
						rgba = new RGBColor(1, 0.4f, 0);
						rgba.scale(((float) hitDistance[index]) / ((float) ray.end));
						break;
					case FRIENDLY_HIT:
						rgba = RGBColor.GREEN;
						break;
					default:
						rgba = RGBColor.YELLOW;
						break;
				}
				// If the line intersect something, choose the color red otherwise yellow.
				tempLine.setColor(rgba);
			}
			else
				// The first line is the original direction of the object. That's why it has to
				// be colored green.
				tempLine.setColor(RGBColor.GREEN);
					
			return tempLine;
		}
		else
			return null;
	}
	
	/**
	 * Visualized a single (invisible) ray as a visible line.
	 * 
	 * @param index Id of the ray which has to be visualized
	 * @return The {@link de.grogra.imp3d.objects.Line} of the ray.
	 */
	private void showLines()
	{
		Transaction xa = originNode.getGraph ().getActiveTransaction ();
		
		for(int i=1; i<=RayCount; i++)
		{
		//	if(favorRay == i) {
				
				// Draw and put into the scene those rays which was used for intersection computation.
				RGGRoot.getRoot(originNode.getGraph()).addEdgeBitsTo(drawLine(i), Graph.BRANCH_EDGE, xa);	
				
				if(hitList[i] == FRIENDLY_HIT )
				{
					Line tempRay = friendlyRays.get(i);
					
					// To visualize the line tangential line a real line-object has to be create.
					de.grogra.imp3d.objects.Line tempLine = new de.grogra.imp3d.objects.Line (
							(float) tempRay.origin.x, (float) tempRay.origin.y, (float) tempRay.origin.z, 
							(float) (tempRay.direction.x * tempRay.end), 
							(float) (tempRay.direction.y * tempRay.end), 
							(float) (tempRay.direction.z * tempRay.end));		
					
					tempLine.setColor( RGBColor.MAGENTA);
					
					RGGRoot.getRoot(originNode.getGraph()).addEdgeBitsTo(tempLine, Graph.BRANCH_EDGE, xa);
					
					tempRay = normals.get(i);
					
					// To visualize the line a real line-object has to be create.
					tempLine = new de.grogra.imp3d.objects.Line (
							(float) tempRay.origin.x, (float) tempRay.origin.y, (float) tempRay.origin.z, 
							(float) (tempRay.direction.x * tempRay.end * disToNodeSurface), 
							(float) (tempRay.direction.y * tempRay.end * disToNodeSurface ), 
							(float) (tempRay.direction.z * tempRay.end * disToNodeSurface ));		
					
					tempLine.setColor(RGBColor.ORANGE);
					
					RGGRoot.getRoot(originNode.getGraph()).addEdgeBitsTo(tempLine, Graph.BRANCH_EDGE, xa);
				}
		//	}				
		}
		
		// Create the line which visualize the new direction.
		de.grogra.imp3d.objects.Line tempLine = new de.grogra.imp3d.objects.Line (
				(float) rayOrigin.x, (float) rayOrigin.y, (float) rayOrigin.z, 
				(float) (direction.x * predictedLength), 
				(float) (direction.y * predictedLength), 
				(float) (direction.z * predictedLength));		
		
		// Set the color of line to white...
		tempLine.setColor(RGBColor.WHITE);
		
		// .. and append it to the root of the graph.
		RGGRoot.getRoot(originNode.getGraph()).addEdgeBitsTo(tempLine, Graph.BRANCH_EDGE, xa);
	}	
	
	private float random(float min, float max)
	{
		return Operators.getRandomGenerator ().nextFloat () * (max - min) + min;
	}
	
	public Object get (String key, Object defaultValue)
	{
		// return options for scene construction and/or concrete light model
		return defaultValue;
	}

	public void volumeCreated (Object object, boolean asNode, Volume volume)
	{
		// The project graph of the current workbench is used as graph. Its
		// edges have no attributes, so they cannot have an associated volume.
		assert asNode;
		
		long id = ((Node) object).getId ();

		Type interfaces[];
		boolean isInstance = false;
		
		if (grouping == 0)
		{
			currentGroupIndex = nextGroupIndex++;
			nodeToGroup.put (id, currentGroupIndex);
			
			// Obtain list of interfaces which is implemented by class of this object.
			interfaces = object.getClass().getGenericInterfaces();
			
			// Obtain whether the object implements the interface which marks a favor node
			if(interfaces.length > 0)
			{
				for(int i = 0 ; i< interfaces.length; i++)
				{
					if(interfaces[i].equals(favorNodeType))
					{
						isInstance = true;
						break;
					}				
				}
			}
			
			if(isFavorNode(id) || isInstance)
			{
				//favorNodeID = id;
				favorVolumeID.add(volume.getId ());
			}
			
			if(originNode != null && originNode.getId() == id)
			{
				originVolume = volume;
				originVolumeID = volume.getId ();
			}
			
		// TODO	We take every MeshVolume as friendly object: When we import
		//		an object a MeshVolume will constructed. So we wouldn't 
		// 		distinguish every MeshVolume instance.
		} else if(grouping == 1 && volume instanceof MeshVolume)
		{
			favorVolumeID.add(volume.getId ());
		}
		
		groupToVolumeId.set (currentGroupIndex, volume.getId ());
	}

	public void beginGroup (Object object, boolean asNode)
	{
		assert asNode;

		if (grouping == 0)
		{
			currentGroupIndex = nodeToGroup.get (((Node) object).getId ());
			if (currentGroupIndex == 0)
			{
				currentGroupIndex = nextGroupIndex++;
				nodeToGroup.put (((Node) object).getId (), currentGroupIndex);
			}
		}
		grouping++;
	}
	
	public void endGroup ()
	{
		if (--grouping < 0)
		{
			throw new IllegalStateException ();
		}
	}
}
