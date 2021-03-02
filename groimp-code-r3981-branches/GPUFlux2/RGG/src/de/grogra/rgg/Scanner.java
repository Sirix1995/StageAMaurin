package de.grogra.rgg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;

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
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongToIntHashMap;
import java.util.Random;


/**
 * Instances of <code>Scanner</code> enables to scan a structure
 * using rays. This code is largely inspired from
 * <code>AvoidIntersection</code>.<br><br>
 *
 */
public class Scanner implements Options, VolumeListener
{
	
	// 3 vectors defining an
	// orthonormal basis
	
	/**
	 * First vector of the
	 * orthonormal basis
	 */
	private Vector3d x0 ;
	
	/**
	 * Second vector of the
	 * orthonormal basis
	 */
	private Vector3d y0 ;
	
	/**
	 * Third vector of the
	 * orthonormal basis
	 */
	private Vector3d z0 ;
	
	// theta and phi are the
	// standard angles used in
	// the spherical coordinate
	// system, with respect to
	// the basis (x0,y0,z0) :
	//
	// for a point M, P being the
	// orthogonal projection of M
	// on the plane (x0,y0) :
	// 
	// theta = (x0;OP) in [0,2*Pi] (longitude)
	// phi = (z0;OM) in [0,Pi] (colatitude)
	
	/**
	 * Angular opening for theta (around
	 * theta = 0) :
	 * must be 0 < thetaRange < 2*PI
	 * 
	 * if thetaRange < 0, only the direction
	 * theta = 0 will be used
	 * 
	 * if thetaRange > 2*PI, thetaRange = 2*PI
	 * will be used instead
	 */
	private double thetaRange ;
	
	/**
	 * Angular opening for phi (around
	 * phi = PI/2) :
	 * must be 0 < phiRange < PI
	 * 
	 * if phiRange < 0, only the direction
	 * phi = PI/2 will be used
	 * 
	 * if phiRange > PI, phiRange = PI
	 * will be used instead
	 */
	private double phiRange ;
	
	/**
	 * A ray will be shot
	 * every thetaStep, for theta
	 * in [-thetaRange/2,thetaRange/2]
	 */
	private double thetaStep ;
	
	/**
	 * A ray will be shot
	 * every thetaStep, for theta
	 * in [-thetaRange/2,thetaRange/2]
	 */
	private double phiStep ;
	
	/**
	 * P is the matrix allowing to change 
	 * a vector's coordinates from the 
	 * base (x0,y0,z0) to the original basis
	 * v_OB = P * v_(x0,y0,z0)
	 * 
	 *     (x0.x y0.x z0.x) ^(- 1)
	 * P = (x0.y y0.y z0.y)
	 *     (x0.z y0.z z0.z)
	 */
	private Matrix3d P ;
	
	/**
	 * Probability of drawing one ray
	 */
	private double p ;
	
	/**
	 * Boolean value indicating
	 * whether the value given
	 * for the parameters are
	 * correct
	 */
	private boolean allCorrect ;
	
	/**
	 * Boolean value indicating
	 * whether the arrays containing
	 * the rays-related informations
	 * have been initialized
	 */
	private boolean raysInit ;
	
	/**
	 * Boolean value indicating
	 * whether the rayCount value
	 * have been initialized
	 */
	private boolean rayCountInit ;
	
	/**
	 * Lenght of the rays
	 */
	private double rayLength ;
	
	/**
	 * Number of rays  that will be shot
	 */
	private int RayCount ;
	
	/**
	 * Contains the coordinates of the
	 * scanned points (ie the coordinate
	 * of the point of intersection of
	 * rays that intersect something)
	 */
	private ArrayList<Point3d> scannedPoints ;
	
	/**
	 * This is the position of the current node and of all rays.
	 */
	private Point3d origin;
	
	
	/**
	 * All rays are stored in this ArrayList. 
	 */
	private ArrayList<Line> rays;
	
	
	/**
	 * parameters for the noise
	 * implementation
	 */
	// theta
	private boolean thetaNoise ;
	private int thetaNoiseType ;
	private boolean thetaNoiseAdapt ;
	private double thetaNoiseParam1 ;
	private double thetaNoiseParam2  ;
	
	// phi
	private boolean phiNoise ;
	private int phiNoiseType ;
	private boolean phiNoiseAdapt ;
	private double phiNoiseParam1 ;
	private double phiNoiseParam2  ;
	
	// distance
	private boolean distanceNoise ;
	private int distanceNoiseType ;
	private boolean distanceNoiseAdapt ;
	private double distanceNoiseParam1 ;
	private double distanceNoiseParam2  ;
	
	// random number generator
	private Random generator ; 
	
	/** 
	 * In this array are stored, whether a ray is
	 * intersect something.
	 */
	private int[] hitList;
	private double[] hitDistance;
	
	private boolean prepareScene = true;
	public static final int NO_HIT = 0;
	public static final int HIT = 1 ;
	
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
		
	/**
	 * Create an instance of <code>Scanner</code>
	 * 
	 */
	public Scanner()
	{
		this ( new Vector3d(1,0,0), new Vector3d(0,1,0), new Vector3d(0,0,1), -0.1f, -0.1f, -0.1f, -0.1f, 10f ) ;
	}
	
		
	/**
	 * Create an instance of <code>Scanner</code>
	 * 
	 * @param RayCount Number of rays.
	 */
	public Scanner( Vector3d x, Vector3d y, Vector3d z, double thetaRangeParam, double phiRangeParam, double thetaStepParam, double phiStepParam, double rayLenghtParam )
	{
		// setting the basis
		setBasis(x, y, z) ;
		//
		// setting the angular parameters
		setRange( thetaRangeParam, phiRangeParam ) ;
		setSteps( thetaRangeParam, phiRangeParam ) ;
		//
		// setting the length of the rays
		setRayLength( rayLenghtParam ) ;
		//
		//computing the number of rays
		// that will be shot
		computeRayCount() ;
		// initialization of the arrays
		// containing the rays and
		// intersection informations
		initializeArrays() ;
		//
		// noise parameters
		generator = new Random() ;
		setThetaNoise(false) ;
		setThetaNoiseType(0, false, 0, 0) ;
		setPhiNoise(false) ;
		setPhiNoiseType(0, false, 0, 0) ;
		setDistanceNoise(false) ;
		setDistanceNoiseType(0, false, 0, 0) ;
		//		
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
		//
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
	 * Sets the probability of drawing one ray.
	 * @param pParam
	 */
	public void setpDrawRay(double pParam)
	{
		p = pParam;
	}
	
	
	/**
	 * compute RayCount
	 */
	private void computeRayCount()
	{
		RayCount = 0 ;
		
		if ( thetaStep > 0 && thetaRange > 0 )
		{
			int line = (int) Math.floor( thetaRange / thetaStep ) + 1 ;
			
			if ( phiStep > 0 && phiRange > 0 )
			{
				int col = (int) Math.floor( phiRange / phiStep ) + 1 ;
				RayCount = line * col ; 
			}
			else
			{
				RayCount = line ;
			}
		}
		else if ( phiStep > 0 && phiRange > 0 )
		{
			RayCount = (int) Math.floor( phiRange / phiStep ) + 1 ;
		}
		else
		{
			RayCount = 1 ;
		}
		
		rayCountInit = true ;
		//
		// rays informations nedd
		// to be recomputed
		raysInit = false ;
		//
	}
	
	
	/**
	 * Initialize the ArrayLists rays and
	 * scannedPoints, and the arrays 
	 * hitList and hitDistance
	 */
	private void initializeArrays()
	{
		// Initializion of  the ray-ArrayList
		rays 			= new ArrayList<Line>(RayCount);
		//
		// Initializion of the hitList/Disctance
		hitList 		= new int[RayCount];
		hitDistance		= new double[RayCount];
		//
		// Initialization of the scannedPoints-ArrayList
		scannedPoints = new ArrayList<Point3d>(RayCount) ;
		//
		// Create rayCount empty rays
		for(int i = 1 ; i <= RayCount ; i++)
		{
			rays.add(new Line());
		}
		//
		// all variable have
		// been initialized
		raysInit = true ;
		//
	}
	
	
	/**
	 * Set the angular parameters of the solid angle that will be
	 * scanned.
	 * 
	 * NB : if thetaRangeParam > 2*PI then
	 * thetaRangeParam = 2*PI will be used instead
	 * 
	 * if phiRangeParam > PI then
	 * phiRangeParam = PI will be used instead
	 * 
	 * @param thetaRange	angular opening in theta
	 * @param phiRange		angular opening in phi
	 */
	public void setRange(double thetaRangeParam, double phiRangeParam)
	{
		thetaRange = thetaRangeParam ;
		if ( thetaRange > 2 * Math.PI )
		{
			thetaRange = 2 * Math.PI ;
		}
		
		phiRange = phiRangeParam ;
		if (phiRange > Math.PI )
		{
			thetaRange = Math.PI ;
		}
		
		// modification of the range
		// may change RayCount
		rayCountInit = false ;
	}

	
	/**
	 * Set the steps for scanning the solid angle.
	 * 
	 * @param thetaStepParam	step for the theta parameter
	 * @param phiStepParam		step for the phi parameter
	 */
	public void setSteps(double thetaStepParam, double phiStepParam)
	{
		thetaStep = thetaStepParam ;
		phiStep = phiStepParam ;
		
		// modification of the range
		// may change RayCount
		rayCountInit = false ;
	}
	
	
	/**
	 * Set the length of the rays that will be shot.
	 * 
	 * @param rayLengthParam
	 */
	public void setRayLength(double rayLengthParam)
	{
		rayLength = rayLengthParam ;
		if ( rayLength < 0 )
		{
			allCorrect = false ;
			System.err.println("rayLength < 0 ! (" + rayLength + ")" );
		}
	}
	
	/**
	 * Writes an ArrayList<Poin3d> to a file
	 * according to a 'x y z' format
	 * 
	 * @param data
	 * @param fileName
	 * @throws IOException
	 */
	public void writeDataToFile(ArrayList<Point3d> data, String fileName) throws IOException
	{
		 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		 for (int i = 0 ; i < data.size()  ; i ++ )
		 {
			 out.println(data.get(i).x + "\t" + data.get(i).y + "\t" + data.get(i).z) ;
		 }
		 out.close() ;
	}
	
	/**
	 * Moves the origin of the scanner on a sphere,
	 * with a step "phiStep" along the colatitude
	 * and a step thetaStep along the longitude.
	 * Rays are shot for each point on the sphere,
	 * according to the angular parameters set
	 * (see setRange and setSteps) and the local
	 * coordinate system (-uR,-uPhi,uTheta)
	 * [see spherical coordinate system] 
	 * 
	 * @param center
	 * @param radius
	 * @param phiStep
	 * @param thetaStep
	 * @param xParam
	 * @param yParam
	 * @param zParam
	 * @return
	 */
	public ArrayList<Point3d> scanSphere (Point3d center, double radius, double phiStep, double thetaStep, Vector3d xParam, Vector3d yParam, Vector3d zParam)
	{
		ArrayList<Point3d> data = new ArrayList<Point3d>() ;
		if (radius <= 0)
		{
			System.err.println("The radius must be > 0 !");
			return data ;
		}
		if (phiStep <= 0)
		{
			System.err.println("The phi-angular step must be > 0 !");
			return data ;
		}
		if( !checkBasis(xParam, yParam, zParam) )
		{
			return data ;
		}
		
		Vector3d x = new Vector3d(xParam) ;
		Vector3d y = new Vector3d(yParam) ;
		Vector3d z = new Vector3d(zParam) ;
		x.normalize();
		y.normalize() ;
		z.normalize() ;
		
		final int N = (int) Math.floor( Math.PI / phiStep ) ;
		
		for (int i =0 ; i <= N  ; i ++ )
		{
			double phi = i * phiStep ;
			data.addAll( scanCircleSphere(center, phi, radius, x, y, z, thetaStep) ) ;
		}
				
		return data ;
	}
	
	/**
	 * Implements the scanning
	 * along a circle -- considered
	 * as a slice of a sphere
	 * 
	 * @param sphereCenter
	 * @param phi
	 * @param radiusSphere
	 * @param x
	 * @param y
	 * @param z
	 * @param thetaStep
	 * @return
	 */
	private ArrayList<Point3d> scanCircleSphere(Point3d sphereCenter, double phi, double radiusSphere, Vector3d x, Vector3d y, Vector3d z, double thetaStep)
	{
		ArrayList<Point3d> data = new ArrayList<Point3d>() ;
		
		double sinPhi = Math.sin(phi) ;
		double cosPhi = Math.cos(phi) ;

		final int N = ( Math.sin(phi) == 0.0 || thetaStep <= 0 )? 0 : (int) Math.floor( 2* Math.PI / thetaStep ) ;
		
		for (int i = 0 ; i <= N ; i ++ )
		{
			double theta = i * thetaStep ;
			
			double cosTheta = Math.cos(theta) ;
			double sinTheta = Math.sin(theta) ;
			
			Vector3d uR = new Vector3d();
			uR.scale(cosTheta*sinPhi, x) ;
			uR.scaleAdd(sinTheta*sinPhi, y, uR) ;
			uR.scaleAdd(cosPhi, z, uR) ;
			
			Vector3d muPhi = new Vector3d();
			muPhi.scale(- cosTheta * cosPhi, x) ;
			muPhi.scaleAdd(- sinTheta * cosPhi , y, muPhi) ;
			muPhi.scaleAdd(sinPhi, z, muPhi) ;
			
			Vector3d uTheta = new Vector3d();
			uTheta.scale(- sinTheta, x) ;
			uTheta.scaleAdd(cosTheta, y, uTheta) ;
			
			Point3d originPoint = new Point3d() ;
			originPoint.scaleAdd(radiusSphere, uR, sphereCenter) ;

			
			uR.negate() ;
			setBasisNoCheck(uR, muPhi, uTheta) ;
			
			scan(originPoint) ;
			data.addAll(scannedPoints) ;
		}

		return data ;
	}
	
	/**
	 * Move the origin of the scanner
	 * along the segment starting
	 * at startingPoint, of direction
	 * directionParam and of length
	 * (nbSteps-1) * stepLength ;
	 * 
	 * rays are shot according to
	 * the angular parameters
	 * specified (see setRange / 
	 * setSteps) and the basis
	 * set (see setBasis)
	 * 
	 * @param startingPoint
	 * @param directionParam
	 * @param stepLength
	 * @param nbSteps
	 * @return
	 */
	public ArrayList<Point3d> scanSegment (Point3d startingPoint, Vector3d directionParam, float stepLength, int nbSteps)
	{
		ArrayList<Point3d> data = new ArrayList<Point3d>() ;
		
		if (stepLength <= 0 )
		{
			System.err.println("The lenght of the steps must be > 0 !");
			return data ;
		}
		if (nbSteps <= 0)
		{
			System.err.println("The number of steps must be > 0 !");
			return data ;
		}
		if ( directionParam.length() <= 0 )
		{
			System.err.println("The direction vector is incorrect !");
			return data ;
		}
		
		Vector3d direction = new Vector3d(directionParam) ;
		direction.normalize() ;
		
		for ( int i = 0 ; i < nbSteps ; i++ )
		{
			Point3d currentLocation = new Point3d() ;
			currentLocation.scaleAdd(i*stepLength, direction, startingPoint) ;
			//
			// scanning ...
			scan(currentLocation) ;
			//
			data.addAll( scannedPoints ) ;
		}
		
		return data ;
	}
	
	/**
	 * Does the same as scanCircle(Point3d, double, Vector3d, Vector3d, double)
	 * but the origin of the angle (zeroAngleVector) is arbitrary chosen.
	 * 
	 * @param center
	 * @param radius
	 * @param normal
	 * @param angleStep
	 * @return
	 */
	public ArrayList<Point3d> scanCircle ( Point3d center, double radius, Vector3d normal,
											double angleStep )
			{
				Vector3d zeroAngleVector = new Vector3d() ;
				
				if ( Math.abs(normal.x) > 0.0001 )
				{
					zeroAngleVector.x = normal.y ;
					zeroAngleVector.y = - normal.x ;
					zeroAngleVector.z = 0 ;
				}
				else
				{
					zeroAngleVector.x = 0 ;
					zeroAngleVector.y = - normal.z ;
					zeroAngleVector.z = normal.y ;
				}
				
				return scanCircle( center, radius, normal,
						 zeroAngleVector,  angleStep ) ;
			}
	
	/**
	 * Moves the origin of the scanner
	 * along the circle of center center,
	 * radius radius, and contained in a
	 * plane orthogonal to the vector normal ;
	 * 
	 * zeroAngleVector specifies must be orthogonal
	 * to normal, and specifies an origin for the angles ;
	 * 
	 * for each angular step angleStep, rays are
	 * shot according to the angular parameters
	 * specified (see setRange / setSteps) and
	 * the local basis (-uR,-uTheta,normal)
	 * [see cylindrical coordinate system]
	 * 
	 * @param center
	 * @param radius
	 * @param normalParam
	 * @param zeroAngleVectorParam
	 * @param angleStep
	 * @return
	 */
	public ArrayList<Point3d> scanCircle( Point3d center, double radius, Vector3d normalParam,
							Vector3d zeroAngleVectorParam, double angleStep )
	{
		ArrayList<Point3d> data = new ArrayList<Point3d>() ;
		
		if (radius <= 0 )
		{
			System.err.println("The radius must be > 0 !");
			return 	data ;
		}
		if (angleStep <= 0)
		{
			System.err.println("The angular step must be > 0 !");
			return data ;
		}
		if ( normalParam.length() < 0.0001 )
		{
			System.err.println("The normal vector must not be null !");
			return data ;
		}
		if ( zeroAngleVectorParam.length() < 0.0001 )
		{
			System.err.println("The angle reference vector must not be null !");
			return data ;
		}
		if ( Math.abs( zeroAngleVectorParam.dot(normalParam) ) > 0.0001  )
		{
			System.err.println("The given vectors are not orthogonal !");
			return data ;
		}
		
		final int N = (int) Math.floor(2*Math.PI/angleStep) ;
		
		Vector3d normal = new Vector3d(normalParam) ;
		Vector3d zeroAngleVector = new Vector3d(zeroAngleVectorParam) ;
		
		normal.normalize() ;
		zeroAngleVector.normalize() ;
		
		final Vector3d y = new Vector3d() ;
		y.cross(normal, zeroAngleVector) ;
		
		double theta ;
		
		for ( int i = 0 ; i <= N ; i++ )
		{
			Point3d currentLocation = new Point3d( center ) ;
			
			Vector3d uR = new Vector3d() ;
			Vector3d uTheta = new Vector3d() ;
			
			theta = i * angleStep ;
			double cosTheta = Math.cos(theta) ;
			double sinTheta = Math.sin(theta) ;
			
			// uR = cos(theta) . zeroAngleVector + sin(theta) . y ;
			uR.scale( cosTheta, zeroAngleVector ) ;
			uR.scaleAdd( sinTheta, y, uR ) ;
			// uTheta = - sin(theta) . zeroAngleVector + cos(theta) . y ;
			uTheta.scale( -sinTheta, zeroAngleVector ) ;
			uTheta.scaleAdd( cosTheta, y, uTheta ) ;
			//
			// point : center + R * uR
			currentLocation.scaleAdd(radius, uR, center) ;
			//
			// basis (-uR,-uTheta,normal) for the scanning
			uR.negate() ;
			uTheta.negate() ;
			setBasisNoCheck(uR, uTheta, normal) ;
			//
			// scanning ...
			scan(currentLocation) ;
			//
			data.addAll( scannedPoints ) ;
		}
		
		return data ;
	}
	
	/**
	 * scanning along a circle
	 * for internal purpose (scanCylinder)
	 * 
	 * works as scanCircle
	 * 
	 * @param center
	 * @param radius
	 * @param x
	 * @param y
	 * @param z
	 * @param angleStep
	 * @return
	 */
	private ArrayList<Point3d> scanCircleNoCheck( Point3d center, double radius, Vector3d x,Vector3d y,
													Vector3d z, double angleStep )
	{
		ArrayList<Point3d> data = new ArrayList<Point3d>() ;

		final int N = (int) Math.floor(2*Math.PI/angleStep) ;

		double theta ;

		for ( int i = 0 ; i <= N ; i++ )
		{
			Point3d currentLocation = new Point3d( center ) ;

			Vector3d uR = new Vector3d() ;
			Vector3d uTheta = new Vector3d() ;

			theta = i * angleStep ;
			double cosTheta = Math.cos(theta) ;
			double sinTheta = Math.sin(theta) ;

			// uR = cos(theta) . x + sin(theta) . y ;
			uR.scale( cosTheta, x ) ;
			uR.scaleAdd( sinTheta, y, uR ) ;
			// uTheta = - sin(theta) . x + cos(theta) . y ;
			uTheta.scale( -sinTheta, x ) ;
			uTheta.scaleAdd( cosTheta, y, uTheta ) ;
			//
			// point : center + R * uR
			currentLocation.scaleAdd(radius, uR, center) ;
			//
			// basis (-uR,-uTheta,normal) for the scanning
			uR.negate() ;
			uTheta.negate() ;
			setBasisNoCheck(uR, uTheta, z) ;
			//
			// scanning ...
			scan(currentLocation) ;
			//
			data.addAll( scannedPoints ) ;
		}

		return data ;
	}
	
	/**
	 * Moves the origin of the scanner
	 * on the cylinder which axis starts
	 * at startingPoint, has axisParam as
	 * direction, and of length (nbSteps-1)*
	 * lengthAxisStep, and of radius radius ;
	 * 
	 * for each step along the axis, the origin
	 * of the scanner revolves around the axis
	 * with an angular step angularStep ;
	 * 
	 * zeroAngleVector specifies must be orthogonal
	 * to axisParam, and specifies an origin for the angles ;
	 * 
	 * rays are shot at each step according to
	 * the angular parameters
	 * specified (see setRange / setSteps) and
	 * the local basis (-uR,-uTheta,normal)
	 * [see cylindrical coordinate system]
	 * 
	 * @param startingPoint
	 * @param axisParam
	 * @param radius
	 * @param angularStep
	 * @param lengthAxisStep
	 * @param nbSteps
	 * @param zeroAngleVectorParam
	 * @return
	 */
	public ArrayList<Point3d> scanCylinder( Point3d startingPoint, Vector3d axisParam, double radius, double angularStep, double lengthAxisStep, int nbSteps, 
											Vector3d zeroAngleVectorParam )
	{
		ArrayList<Point3d> data = new ArrayList<Point3d>() ;
		
		if (angularStep <= 0)
		{
			System.err.println("The angular step must be > 0 ! ");
			return data ;
		}
		if (lengthAxisStep <= 0)
		{
			System.err.println("The lenght of the steps along the axis must be > 0 ! ");
			return data ;
		}
		if (nbSteps <= 0)
		{
			System.err.println("The number of steps along the axis must be > 0 ! ");
			return data ;
		}if ( axisParam.length() < 0.0001 )
		{
			System.err.println("The axis vector must not be null !");
			return data ;
		}
		if ( zeroAngleVectorParam.length() < 0.0001 )
		{
			System.err.println("The angle reference vector must not be null !");
			return data ;
		}
		if ( Math.abs( zeroAngleVectorParam.dot(axisParam) ) > 0.0001  )
		{
			System.err.println("The given vectors are not orthogonal !");
			return data ;
		}
		
		Vector3d axis = new Vector3d(axisParam) ;
		Vector3d zeroAngleVector = new Vector3d(zeroAngleVectorParam) ;
		
		axis.normalize() ;
		zeroAngleVector.normalize() ;
		
		final Vector3d y = new Vector3d() ;
		y.cross(axis, zeroAngleVector) ;
		
		for ( int i = 0 ; i < nbSteps ; i++ )
		{
			Point3d currentLocation = new Point3d() ;
			currentLocation.scaleAdd(i*lengthAxisStep, axis, startingPoint) ;
			//
			// scanning ...
			data.addAll( scanCircleNoCheck(currentLocation, radius, zeroAngleVector, y, axis, angularStep) ) ;
			//
		}
		
		return data ;
	}
	
	/**
	 * Works as scanCylinder
	 * (Point3d, Vector3d, double, double, double, int, Vector3d)
	 * but an origin for the angles is arbitrary chosen) 
	 *  
	 * @param startingPoint
	 * @param axisParam
	 * @param radius
	 * @param angularStep
	 * @param lengthAxisStep
	 * @param nbSteps
	 * @return
	 */
	public ArrayList<Point3d> scanCylinder ( Point3d startingPoint, Vector3d axisParam, double radius, double angularStep, double lengthAxisStep, int nbSteps )
	{
		Vector3d zeroAngleVector = new Vector3d() ;

		if ( Math.abs(axisParam.x) > 0.0001 )
		{
			zeroAngleVector.x = axisParam.y ;
			zeroAngleVector.y = - axisParam.x ;
			zeroAngleVector.z = 0 ;
		}
		else
		{
			zeroAngleVector.x = 0 ;
			zeroAngleVector.y = - axisParam.z ;
			zeroAngleVector.z = axisParam.y ;
		}

		return scanCylinder(startingPoint,axisParam,radius,angularStep,lengthAxisStep,nbSteps,zeroAngleVector ) ;
	}
	
	/**
	 * checks that (x,y,z)
	 * forms an orthogonal basis
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean checkBasis (Vector3d x, Vector3d y, Vector3d z)
	{
		// checking that (x,y,z) form
		// a direct-orthogonal basis
		if ( Math.abs(x.dot(y)) > 0.0001 )
		{
			System.err.println("Vectors incorrect : x.y = " + x.dot(y));
			return false ;
		}
		else if ( Math.abs(x.dot(z)) > 0.0001 )
		{
			System.err.println("Vectors incorrect : x.z = " + y.dot(y));
			return false ;
		}
		else if ( Math.abs(z.dot(y)) > 0.0001 )
		{
			System.err.println("Vectors incorrect : z.y = " + z.dot(y));
			return false ;
		}
		Vector3d xCrossy = new Vector3d() ;
		xCrossy.cross(x, y) ;
		if ( xCrossy.dot(z) < 0 )
		{
			System.err.println("Vectors incorrect : x (cross) y = " + xCrossy + " ; and z = " + z);
			return false ;
		}
		//
		return true ;
	}
	
	/**
	 * set the (x,y,z) basis which will
	 * serve as reference for the 
	 * spherical coordinates system ;
	 * 
	 * (x,y,z) must form an orthogonal
	 * basis ; the vectors are then
	 * normalized to form an
	 * orthonormal basis
	 * 
	 * also compute the P matrix, and
	 * change the basisCorrect value
	 * accordingly
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setBasis(Vector3d xParam, Vector3d yParam, Vector3d zParam)
	{
		// checking that (x,y,z) form
		// a direct-orthogonal basis
		allCorrect = checkBasis(xParam,yParam,zParam) ;
		//
		
		Vector3d x = new Vector3d(xParam) ;
		Vector3d y = new Vector3d(yParam) ;
		Vector3d z = new Vector3d(zParam) ;
		
		// OK, normalising
		if (allCorrect)
		{
			x.normalize() ;
			y.normalize() ;
			z.normalize() ;
		}
		//
		
		// setting the basis
		x0 = x ;
		y0 = y ;
		z0 = z ;
		//
		
		// computing P
		if (allCorrect)
		{
			P = new Matrix3d(x0.x, y0.x, z0.x, x0.y, y0.y, z0.y, x0.z, y0.z, z0.z) ;
		}
		//
	}
	
	/**
	 * sets the basis (x,y,z)
	 * as reference basis
	 * and compute the P matrix
	 * @param x
	 * @param y
	 * @param z
	 */
	private void setBasisNoCheck(Vector3d x, Vector3d y, Vector3d z)
	{
		allCorrect = true ;
		// setting the basis
		x0 = x ;
		y0 = y ;
		z0 = z ;
		//
		
		P = new Matrix3d(x0.x, y0.x, z0.x, x0.y, y0.y, z0.y, x0.z, y0.z, z0.z) ;
	}
	
	
	/**
	 * returns the number of scanned points
	 * (ie length of scannedPoints)
	 * 
	 */
	public int getScannedPointsNb()
	{
		return scannedPoints.size() ;
	}
	
	
	/**
	 * Return an ArrayList<Point3d> containing
	 * the scannedPoints.
	 * 
	 */
	public ArrayList<Point3d> getScannedPoints()
	{
		return scannedPoints ;
	}
	
	
	/**
	 * This method prepare the whole scene, may for a new 
	 * intersection-computation. This is necessary, when something
	 * in the scene has changed.
	 * The method {@link look(Null node, double size, double distance)}
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
			
		}

		// Setting all stored information of rays to zero. We don't need these information anymore, but
		// need the allocated memory for the next calculations.
		setListToZero(rays);
				
		// Another reseting.
		ilist 	= new IntersectionList();
		is 		= new Intersection(new IntersectionList());
	}
	
		
	/**
	 * This method triggers the scanning process.
	 * 
	 * NB : each time this method is called, the list of scan point
	 * is re-initialized. Make sure to save your data !
	 * 
	 * @param originPoint 	Scan from this point
	 * @param showLines		To visualize what is going on in front of <code>node</code>, set this
	 * 						parameter true.
	 * @return				Iff every ray has an unfriendly intersection with objects, this method
	 * 						return false otherwise true.
	 */
	public void scan(Point3d originPoint)
	{				
		if ( !allCorrect )
		{
			System.err.println("Error in the parameters ! No scan will be performed !");
			return ;
		}
		if ( !rayCountInit )
		{
			computeRayCount() ;
			initializeArrays() ;
		}
		else if ( !raysInit )
		{
			// angular ranges and/or steps
			// have change since the initialization
			// of RayCount and the rays array
			// they need to be recomputed
			initializeArrays() ;
		}
		
		// Constructing the octree with the <code>SceneVisitor</code> an initializing
		// or resetting some attributes.
		prepareScene();	
		
		origin = originPoint ;
	
		// Shoot all rays from rayOrigin into the near environment of 
		// node and look for intersection.
		if (thetaNoise || phiNoise || distanceNoise )
		{
			shootRaysNoisy();
		}
		else
		{
			shootRays() ;
		}
		
		// Visualize shot rays with a probability p
		showLines() ;
		
		// thus free rays will
		// be available for the next
		// call
		raysInit = false ;
	}
	
	
	/**
	 * This method trace every constructed line (<code>rays</code>) and decide
	 * if there is an intersection.
	 * 
	 * @param index Which ray in <code>rays</code> is to be checked.
	 */
	private void traceRay(int index)
	{		
		int ilistSize = ilist.size ;	
		
		// Every ray has initially no intersection
		hitList[index] = NO_HIT ;
		hitDistance[index] = 0 ;
		
		ilist.clear() ;
		Line ray = rays.get(index) ;
		
		// Ray of current index is sent into the scene
		scene.computeIntersections (ray, Intersection.CLOSEST, ilist, is, null);		
		
		if (ilist.size > ilistSize)
		{			
			// There should be just one intersection	
			Intersection intersection = ilist.elements[0] ;
						
			// Check if no sky object was hit (parameter < INF)
			if ( intersection.parameter < Double.POSITIVE_INFINITY )
			{
				// The intersection of this ray is valid.				
				// Mark this ray as a hit.
				hitList[index] = HIT;
				// Store the distance of rayOrigin to intersection point
				hitDistance[index] = intersection.parameter ;
			} 
			
			ilist.setSize (ilistSize);			
		} 
	}
	
	
	/**
	 * Shoots rays into scene, and compute the
	 * coordinates of the intersection points.
	 * These points are stored in scannedPoints.
	 */
	private void shootRays()
	{
		final int hor ;
		if ( thetaRange > 0 && thetaStep > 0 )
		{
			hor = (int) Math.floor( thetaRange / thetaStep ) ;
		}
		else
		{
			hor = 0 ;
		}
		
		final int ver ;
		if ( phiRange > 0 && phiStep > 0 )
		{
			ver = (int) Math.floor( phiRange / phiStep ) ;
		}
		else
		{
			ver = 0 ;
		}
		
		if ( hor > 0 )
		{
			if (ver > 0 )
			{
				// SHOOT RAY THETA / PHI
				double theta ;
				double phi ;
				int rayNb = 0 ;
				for (int h = 0 ; h <= hor ; h ++ )
				{
					theta = - thetaRange / 2 + thetaStep * h ;
					for (int v = 0 ; v <= ver ; v ++ )
					{
						phi = Math.PI/2 - phiRange / 2 + phiStep * v ;
						shootRay(theta, phi, rayNb) ;
						rayNb ++ ;
					}
				}
			}
			else
			{
				//SHOOT RAY THETA / PHI = PI/2
				double theta ;
				int rayNb = 0 ;
				for (int h = 0 ; h <= hor ; h ++ )
				{
					theta = - thetaRange / 2 + thetaStep * h ;
					shootRayTheta(theta,rayNb) ;
					rayNb ++ ;
				}
			}
		}
		else
		{
			if (ver > 0 )
			{
				// SHOOT RAY THETA = 0 / PHI
				double phi ;
				int rayNb = 0 ;
				
				for (int v = 0 ; v <= ver ; v ++ )
				{
					phi =  Math.PI/2 - phiRange / 2 + phiStep * v ;
					shootRayPhi(phi,rayNb) ;
					rayNb ++ ;
				}
			}
			else
			{
				// SHOOT RAY THETA = 0 / PHI = PI/2
				Vector3d ray = x0 ;

				Line newLine ;

				newLine = rays.get(0);			
				newLine.origin.set(origin);
				newLine.direction.set(ray);			

				newLine.start = 0.0001 ;
				newLine.end = rayLength ;

				traceRay(0);
				if ( hitList[0] == HIT )
				{
					Point3d newScannedPoint = new Point3d();
					ray.scale(hitDistance[0]) ;
					newScannedPoint.add(origin, ray) ;
					scannedPoints.add(newScannedPoint) ;
				}
			}
		}
	}
	
	
	/**
	 * Shoots rays into scene, and compute the
	 * coordinates of the intersection points.
	 * These points are stored in scannedPoints.
	 */
	private void shootRaysNoisy()
	{
		final int hor ;
		if ( thetaRange > 0 && thetaStep > 0 )
		{
			hor = (int) Math.floor( thetaRange / thetaStep ) ;
		}
		else
		{
			hor = 0 ;
		}
		
		final int ver ;
		if ( phiRange > 0 && phiStep > 0 )
		{
			ver = (int) Math.floor( phiRange / phiStep ) ;
		}
		else
		{
			ver = 0 ;
		}
		
		if ( hor > 0 )
		{
			if (ver > 0 )
			{
				// SHOOT RAY THETA / PHI
				double theta ;
				double phi ;
				int rayNb = 0 ;
				for (int h = 0 ; h <= hor ; h ++ )
				{
					theta = - thetaRange / 2 + thetaStep * h ;
					for (int v = 0 ; v <= ver ; v ++ )
					{
						phi = Math.PI/2 - phiRange / 2 + phiStep * v ;
						shootRayNoisy(theta, phi, rayNb) ;
						rayNb ++ ;
					}
				}
			}
			else
			{
				//SHOOT RAY THETA / PHI = PI/2
				double theta ;
				int rayNb = 0 ;
				for (int h = 0 ; h <= hor ; h ++ )
				{
					theta = - thetaRange / 2 + thetaStep * h ;
					shootRayThetaNoisy(theta,rayNb) ;
					rayNb ++ ;
				}
			}
		}
		else
		{
			if (ver > 0 )
			{
				// SHOOT RAY THETA = 0 / PHI
				double phi ;
				int rayNb = 0 ;
				
				for (int v = 0 ; v <= ver ; v ++ )
				{
					phi =  Math.PI/2 - phiRange / 2 + phiStep * v ;
					shootRayPhiNoisy(phi,rayNb) ;
					rayNb ++ ;
				}
			}
			else
			{
				// SHOOT RAY THETA = 0 / PHI = PI/2
				Vector3d ray = x0 ;

				Line newLine ;

				newLine = rays.get(0);			
				newLine.origin.set(origin);
				newLine.direction.set(ray);			

				newLine.start = 0.0001 ;
				newLine.end = rayLength ;

				traceRay(0);
				if ( hitList[0] == HIT )
				{
					if (thetaNoise || phiNoise)
					{
						double thetaNoisy = (thetaNoise)? makeNoisyValue(0, thetaNoiseType, thetaNoiseParam1, thetaNoiseParam2, thetaNoiseAdapt) : 0 ;
						double phiNoisy = (phiNoise)? makeNoisyValue(Math.PI / 2, phiNoiseType, phiNoiseParam1, phiNoiseParam2, phiNoiseAdapt) : Math.PI / 2 ;

						// ray coordinates
						Vector3d rayOldBaseNoisy = new Vector3d() ;
						rayOldBaseNoisy.x = Math.cos(thetaNoisy) * Math.sin(phiNoisy) ;
						rayOldBaseNoisy.y = Math.sin(thetaNoisy) * Math.sin(phiNoisy) ;
						rayOldBaseNoisy.z = Math.cos(phiNoisy) ;

						double[] rayNewBaseNoisy = new double[3] ;

						for (int i = 0 ; i < 3 ; i ++)
						{
							Vector3d row = new Vector3d() ;
							P.getRow(i, row);

							rayNewBaseNoisy[i] = row.dot(rayOldBaseNoisy) ;
						}
						//  adding phi / theta noise
						ray = new Vector3d(rayNewBaseNoisy) ;
					}
					// distance noise
					double distance = ( distanceNoise )?
							makeNoisyValue(hitDistance[0], distanceNoiseType, distanceNoiseParam1, distanceNoiseParam2, distanceNoiseAdapt)
							: hitDistance[0] ;
					//
					Point3d newScannedPoint = new Point3d();
					ray.scale(distance) ;
					newScannedPoint.add(origin, ray) ;
					scannedPoints.add(newScannedPoint) ;
				}
			}
		}
	}
	
	
	/**
	 * sets thetaNoise on / off
	 * @param param
	 */
	public void setThetaNoise(boolean param)
	{
		thetaNoise = param ;
	}
	
	
	/**
	 * sets the parameters for the
	 * theta noise
	 * 
	 * @param type 0 for uniform noise, 1 for gaussian noise
	 * @param adapt false for a fixed noise, true for a noise dependent on the value
	 * @param param1
	 * @param param2
	 */
	public void setThetaNoiseType( int type, boolean adapt, double param1, double param2 )
	{
		thetaNoiseType = type ;
		thetaNoiseAdapt = adapt ;
		thetaNoiseParam1 = param1 ;
		thetaNoiseParam2 = param2 ;
	}
	
	
	/**
	 * set phiNoise on / off
	 * @param param
	 */
	public void setPhiNoise(boolean param)
	{
		phiNoise = param ;
	}
	
	
	/**
	 * sets the parameters for the
	 * phi noise
	 * 
	 * @param type 0 for uniform noise, 1 for gaussian noise
	 * @param adapt false for a fixed noise, true for a noise dependent on the value
	 * @param param1
	 * @param param2
	 */
	public void setPhiNoiseType( int type, boolean adapt, double param1, double param2 )
	{
		phiNoiseType = type ;
		phiNoiseAdapt = adapt ;
		phiNoiseParam1 = param1 ;
		phiNoiseParam2 = param2 ;
	}
	
	
	/**
	 * sets distanceNoise on / off
	 * @param param
	 */
	public void setDistanceNoise(boolean param)
	{
		distanceNoise = param ;
	}
	
	
	/**
	 * sets the parameters for the
	 * distance noise
	 * 
	 * @param type 0 for uniform noise, 1 for gaussian noise
	 * @param adapt false for a fixed noise, true for a noise dependent on the value
	 * @param param1
	 * @param param2
	 */
	public void setDistanceNoiseType( int type, boolean adapt, double param1, double param2 )
	{
		distanceNoiseType = type ;
		distanceNoiseAdapt = adapt ;
		distanceNoiseParam1 = param1 ;
		distanceNoiseParam2 = param2 ;
	}
	
	
	/**
	 * generate a noisy value of
	 * value depending on the
	 * given parameters :
	 * 
	 * noisyValue = value + noise
	 * 
	 * noiseType == 0 :
	 * uniform noise
	 * (param2 > param1)
	 *      
	 *      adapt == true :
	 *      noise between 
	 *      param1 * value and
	 *      param2 * value
	 * 	
	 *      adapt == false
	 *      noise between
	 *      param1 and param2
	 * 
	 * 
	 * noiseType == 1 :
	 * gaussian noise
	 *      
	 *      adapt == true
	 *      gaussian noise
	 *      mean = param1 * value
	 *      std = param2 * value
	 *      
	 *      adapt == false
	 *      gaussian noise
	 *      mean = param1
	 *      std = param2
	 * 
	 * @param value
	 * @param noiseType
	 * @param param1
	 * @param param2
	 * @param adapt
	 * @return
	 */
	private double makeNoisyValue(double value, int noiseType, double param1, double param2, boolean adapt)
	{
		// uniform
		if (noiseType == 0)
		{
			double r = generator.nextDouble() ;
			
			// adaptative
			if (adapt)
			{
				return (   value * ( 1 + ( param2 - param1) * r + param1 )   ) ;
			}
			else
			{
				return (   value + (param2 - param1) * r + param1 ) ;
			}
		}
		// gaussian
		else if (noiseType == 1)
		{
			double r = generator.nextGaussian() ;
			
			// adaptative
			if (adapt)
			{
				return ( value * ( 1+ param1 + param2 * r )  ) ;
			}
			else
			{
				return ( value + param1 + param2 * r ) ;
			}
		}
		else
		{
			return value ;
		}
	}
	
	
	/**
	 * Shoot one ray of spherical parameters
	 * theta and phi, and of length rayLength
	 * with some noise
	 * If there is an intersection, a new point
	 * is added in scannedPoints.
	 * 
	 * @param theta
	 * @param phi
	 * @param rayNb
	 */
	private void shootRayNoisy (double theta, double phi, int rayNb)
	{
		// ray coordinates
		Vector3d rayOldBase = new Vector3d();
		rayOldBase.x = Math.cos(theta) * Math.sin(phi) ;
		rayOldBase.y = Math.sin(theta) * Math.sin(phi) ;
		rayOldBase.z = Math.cos(phi) ;
		
		double[] rayNewBase = new double[3] ;
		
		for (int i = 0 ; i < 3 ; i ++)
		{
			Vector3d row = new Vector3d() ;
			P.getRow(i, row);
			
			rayNewBase[i] = row.dot(rayOldBase) ;
		}
		
		Vector3d ray = new Vector3d(rayNewBase) ;
		//

		Line newLine ;
		
		newLine = rays.get(rayNb);			
		newLine.origin.set(origin);
		newLine.direction.set(ray);			

		newLine.start = 0.0001 ;
		newLine.end = rayLength ;

		traceRay(rayNb);

		if ( hitList[rayNb] == HIT )
		{
			Vector3d rayOldBaseNoisy = new Vector3d( rayOldBase );

			if (thetaNoise || phiNoise)
			{
				double thetaNoisy = (thetaNoise)? makeNoisyValue(0, thetaNoiseType, thetaNoiseParam1, thetaNoiseParam2, thetaNoiseAdapt) : theta ;
				double phiNoisy = (phiNoise)? makeNoisyValue(Math.PI / 2, phiNoiseType, phiNoiseParam1, phiNoiseParam2, phiNoiseAdapt) : phi ;

				// ray coordinates
				rayOldBaseNoisy.x = Math.cos(thetaNoisy) * Math.sin(phiNoisy) ;
				rayOldBaseNoisy.y = Math.sin(thetaNoisy) * Math.sin(phiNoisy) ;
				rayOldBaseNoisy.z = Math.cos(phiNoisy) ;

				double[] rayNewBaseNoisy = new double[3] ;

				for (int i = 0 ; i < 3 ; i ++)
				{
					Vector3d row = new Vector3d() ;
					P.getRow(i, row);

					rayNewBaseNoisy[i] = row.dot(rayOldBaseNoisy) ;
				}
				//  adding phi / theta noise
				ray = new Vector3d(rayNewBaseNoisy) ;
			}
			// distance noise
			double distance = ( distanceNoise )?
					makeNoisyValue(hitDistance[rayNb], distanceNoiseType, distanceNoiseParam1, distanceNoiseParam2, distanceNoiseAdapt)
					: hitDistance[rayNb] ;
			//
			Point3d newScannedPoint = new Point3d();
			ray.scale(distance) ;
			newScannedPoint.add(origin, ray) ;
			scannedPoints.add(newScannedPoint) ;
		}
	}
	
	
	/**
	 * Works as {@link #shootRayNoisy(double,double,int)},
	 * with phi = PI/2 being assumed.
	 * 
	 * @param theta
	 * @param rayNb
	 */
	private void shootRayThetaNoisy (double theta, int rayNb)
	{
		// ray coordinates
		Vector3d rayOldBase = new Vector3d();
		rayOldBase.x = Math.cos(theta) ;
		rayOldBase.y = Math.sin(theta) ;
		rayOldBase.z = 0 ;
		
		double[] rayNewBase = new double[3] ;
		
		for (int i = 0 ; i < 3 ; i ++)
		{
			Vector3d row = new Vector3d() ;
			P.getRow(i, row);
			
			rayNewBase[i] = row.dot(rayOldBase) ;
		}
		
		Vector3d ray = new Vector3d(rayNewBase) ;
		//

		Line newLine ;
		
		newLine = rays.get(rayNb);			
		newLine.origin.set(origin);
		newLine.direction.set(ray);			

		newLine.start = 0.0001 ;
		newLine.end = rayLength ;

		traceRay(rayNb);

		if ( hitList[rayNb] == HIT )
		{
			Vector3d rayOldBaseNoisy = new Vector3d( rayOldBase );

			if (thetaNoise || phiNoise)
			{
				double thetaNoisy = (thetaNoise)? makeNoisyValue(0, thetaNoiseType, thetaNoiseParam1, thetaNoiseParam2, thetaNoiseAdapt) : theta ;
				double phiNoisy = (phiNoise)? makeNoisyValue(Math.PI / 2, phiNoiseType, phiNoiseParam1, phiNoiseParam2, phiNoiseAdapt) : Math.PI / 2 ;

				// ray coordinates
				rayOldBaseNoisy.x = Math.cos(thetaNoisy) * Math.sin(phiNoisy) ;
				rayOldBaseNoisy.y = Math.sin(thetaNoisy) * Math.sin(phiNoisy) ;
				rayOldBaseNoisy.z = Math.cos(phiNoisy) ;

				double[] rayNewBaseNoisy = new double[3] ;

				for (int i = 0 ; i < 3 ; i ++)
				{
					Vector3d row = new Vector3d() ;
					P.getRow(i, row);

					rayNewBaseNoisy[i] = row.dot(rayOldBaseNoisy) ;
				}
				//  adding phi / theta noise
				ray = new Vector3d(rayNewBaseNoisy) ;
			}
			// distance noise
			double distance = ( distanceNoise )?
					makeNoisyValue(hitDistance[rayNb], distanceNoiseType, distanceNoiseParam1, distanceNoiseParam2, distanceNoiseAdapt)
					: hitDistance[rayNb] ;
			//
			Point3d newScannedPoint = new Point3d();
			ray.scale(distance) ;
			newScannedPoint.add(origin, ray) ;
			scannedPoints.add(newScannedPoint) ;
		}
	}
	
	
	/**
	 * Works as {@link #shootRay(double,double,int)},
	 * with theta = 0 being assumed.
	 * 
	 * @param theta
	 * @param rayNb
	 */
	private void shootRayPhiNoisy ( double phi, int rayNb )
	{
		// ray coordinates
		Vector3d rayOldBase = new Vector3d();
		rayOldBase.x = Math.sin(phi) ;
		rayOldBase.y = 0 ;
		rayOldBase.z = Math.cos(phi) ;
		
		double[] rayNewBase = new double[3] ;
		
		for (int i = 0 ; i < 3 ; i ++)
		{
			Vector3d row = new Vector3d() ;
			P.getRow(i, row);
			
			rayNewBase[i] = row.dot(rayOldBase) ;
		}
		
		Vector3d ray = new Vector3d(rayNewBase) ;
		//

		Line newLine ;
		
		newLine = rays.get(rayNb);			
		newLine.origin.set(origin);
		newLine.direction.set(ray);			

		newLine.start = 0.0001 ;
		newLine.end = rayLength ;

		traceRay(rayNb);

		if ( hitList[rayNb] == HIT )
		{
			Vector3d rayOldBaseNoisy = new Vector3d( rayOldBase );

			if (thetaNoise || phiNoise)
			{
				double thetaNoisy = (thetaNoise)? makeNoisyValue(0, thetaNoiseType, thetaNoiseParam1, thetaNoiseParam2, thetaNoiseAdapt) : 0 ;
				double phiNoisy = (phiNoise)? makeNoisyValue(Math.PI / 2, phiNoiseType, phiNoiseParam1, phiNoiseParam2, phiNoiseAdapt) : phi ;

				// ray coordinates
				rayOldBaseNoisy.x = Math.cos(thetaNoisy) * Math.sin(phiNoisy) ;
				rayOldBaseNoisy.y = Math.sin(thetaNoisy) * Math.sin(phiNoisy) ;
				rayOldBaseNoisy.z = Math.cos(phiNoisy) ;

				double[] rayNewBaseNoisy = new double[3] ;

				for (int i = 0 ; i < 3 ; i ++)
				{
					Vector3d row = new Vector3d() ;
					P.getRow(i, row);

					rayNewBaseNoisy[i] = row.dot(rayOldBaseNoisy) ;
				}
				//  adding phi / theta noise
				ray = new Vector3d(rayNewBaseNoisy) ;
			}
			// distance noise
			double distance = ( distanceNoise )?
					makeNoisyValue(hitDistance[rayNb], distanceNoiseType, distanceNoiseParam1, distanceNoiseParam2, distanceNoiseAdapt)
					: hitDistance[rayNb] ;
			//
			Point3d newScannedPoint = new Point3d();
			ray.scale(distance) ;
			newScannedPoint.add(origin, ray) ;
			scannedPoints.add(newScannedPoint) ;
		}
	}
	
	
	/**
	 * Shoot one ray of spherical parameters
	 * theta and phi, and of length rayLength.
	 * If there is an intersection, a new point
	 * is added in scannedPoints.
	 * 
	 * @param theta
	 * @param phi
	 * @param rayNb
	 */
	private void shootRay (double theta, double phi, int rayNb)
	{
		// ray coordinates
		Vector3d rayOldBase = new Vector3d();
		rayOldBase.x = Math.cos(theta) * Math.sin(phi) ;
		rayOldBase.y = Math.sin(theta) * Math.sin(phi) ;
		rayOldBase.z = Math.cos(phi) ;
			
		double[] rayNewBase = new double[3] ;
		
		for (int i = 0 ; i < 3 ; i ++)
		{
			Vector3d row = new Vector3d() ;
			P.getRow(i, row);
			
			rayNewBase[i] = row.dot(rayOldBase) ;
		}
		
		Vector3d ray = new Vector3d(rayNewBase) ;
		//

		Line newLine ;
		
		newLine = rays.get(rayNb);			
		newLine.origin.set(origin);
		newLine.direction.set(ray);			

		newLine.start = 0.0001 ;
		newLine.end = rayLength ;

		traceRay(rayNb);
		
		if ( hitList[rayNb] == HIT )
		{
			Point3d newScannedPoint = new Point3d();
			ray.scale(hitDistance[rayNb]) ;
			newScannedPoint.add(origin, ray) ;
			
			scannedPoints.add(newScannedPoint) ;
		}
	}
	
	
	/**
	 * Works as {@link #shootRay(double,double,int)},
	 * with theta = 0 being assumed.
	 * 
	 * @param theta
	 * @param rayNb
	 */
	private void shootRayPhi ( double phi, int rayNb )
	{
		// ray coordinates
		Vector3d rayOldBase = new Vector3d();
		rayOldBase.x = Math.sin(phi) ;
		rayOldBase.y = 0 ;
		rayOldBase.z = Math.cos(phi) ;
			
		double[] rayNewBase = new double[3] ;
		
		for (int i = 0 ; i < 3 ; i ++)
		{
			Vector3d row = new Vector3d() ;
			P.getRow(i, row);
			
			rayNewBase[i] = row.dot(rayOldBase) ;
		}
		
		Vector3d ray = new Vector3d(rayNewBase) ;
		//

		Line newLine ;

		newLine = rays.get(rayNb);			
		newLine.origin.set(origin);
		newLine.direction.set(ray);			

		newLine.start = 0.0001 ;
		newLine.end = rayLength ;

		traceRay(rayNb);
		
		if ( hitList[rayNb] == HIT )
		{
			Point3d newScannedPoint = new Point3d();
			ray.scale(hitDistance[rayNb]) ;
			newScannedPoint.add(origin, ray) ;
			
			scannedPoints.add(newScannedPoint) ;
		}
	}
	
	
	/**
	 * Works as {@link #shootRay(double,double,int)},
	 * with phi = PI/2 being assumed.
	 * 
	 * @param theta
	 * @param rayNb
	 */
	private void shootRayTheta ( double theta, int rayNb )
	{
		// ray coordinates
		Vector3d rayOldBase = new Vector3d();
		rayOldBase.x = Math.cos(theta) ;
		rayOldBase.y = Math.sin(theta) ;
		rayOldBase.z = 0 ;
			
		double[] rayNewBase = new double[3] ;
		
		for (int i = 0 ; i < 3 ; i ++)
		{
			Vector3d row = new Vector3d() ;
			P.getRow(i, row);
			
			rayNewBase[i] = row.dot(rayOldBase) ;
		}
		
		Vector3d ray = new Vector3d(rayNewBase) ;
		//

		Line newLine ;

		newLine = rays.get(rayNb);			
		newLine.origin.set(origin);
		newLine.direction.set(ray);			

		newLine.start = 0.0001 ;
		newLine.end = rayLength ;

		traceRay(rayNb);
		
		if ( hitList[rayNb] == HIT )
		{
			Point3d newScannedPoint = new Point3d();
			ray.scale(hitDistance[rayNb]) ;
			newScannedPoint.add(origin, ray) ;
			
			scannedPoints.add(newScannedPoint) ;
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
	 * Visualize a single (invisible) ray as a visible line.
	 * 
	 * @param index Id of the ray which has to be visualized
	 * @return The {@link de.grogra.imp3d.objects.Line} of the ray.
	 */
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
			RGBColor rgba;

			switch(hitList[index])
			{
			case HIT:
				rgba = new RGBColor(1, 0.4f, 0);
				rgba.scale(((float) hitDistance[index]) / ((float) ray.end));
				break;
			default :
				rgba = RGBColor.YELLOW;
				break;
			}
			// If the line intersect something, choose the color red otherwise yellow.
			tempLine.setColor(rgba);

			return tempLine;
		}
		else
			return null;
	}

	
	/**
	 * Draws the rays with different
	 * colors, depending on if they
	 * intersect an object, and with
	 * a probability p.
	 */
	private void showLines()
	{
		Transaction xa = Library.graph().getActiveTransaction () ;
		
		if ( p < 1.0 && p > 0.0)
		{
			for( int i = 0 ; i < RayCount ; i ++ )
			{
				if (Math.random() < p)
				{
					// Draw and put into the scene the rays which were used for scanning
					RGGRoot.getRoot(Library.graph()).addEdgeBitsTo(drawLine(i), Graph.BRANCH_EDGE, xa) ;
				}
			}
		}
		else if ( p > 1.0)
		{
			for( int i = 0 ; i < RayCount ; i ++ )
			{
				// Draw and put into the scene the rays which were used for scanning
				RGGRoot.getRoot(Library.graph()).addEdgeBitsTo(drawLine(i), Graph.BRANCH_EDGE, xa) ;
			}
		}
	}
	
	
	/**
	 * return options for scene construction and/or concrete light model
	 */
	public Object get (String key, Object defaultValue)
	{
		return defaultValue;
	}

	
	public void volumeCreated (Object object, boolean asNode, Volume volume)
	{
		// The project graph of the current workbench is used as graph. Its
		// edges have no attributes, so they cannot have an associated volume.
		assert asNode;
		
		long id = ((Node) object).getId ();

		if (grouping == 0)
		{
			currentGroupIndex = nextGroupIndex++;
			nodeToGroup.put (id, currentGroupIndex);			
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
