package de.grogra.mtg;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3d;

public class MTGBranchElement {

	private	double length;
	private	double topdia;
	private	double botdia;
	private	double alpha;
	private	double beta;
	private	double gamma;
	private	int  nodeIndex;
	private	Vector3d _dirp;
	private	Vector3d _dirs;
	private	Vector3d _origin;
	
	private Point3d[] topSurface;
	private Point3d[] botSurface;
	private int numOfSurfacePoints;
	
	private int order;
	
	public void setOrder(int order)
	{
		this.order = order;
	}
	
	public int getOrder()
	{
		return order;
	}
	
	public int getNumOfSurfacePoints()
	{
		return numOfSurfacePoints;
	}
	
	public Point3d[] getTopSurface(MTGBranchElement nextElement)
	{
		if((topSurface==null)||(botSurface==null))
		{
			topSurface = new Point3d[numOfSurfacePoints];
			botSurface = new Point3d[numOfSurfacePoints];
			computeSurfaces(nextElement);
		}
		
		return topSurface;
	}
	
	public Point3d[] getBotSurface(MTGBranchElement nextElement)
	{
		if((topSurface==null)||(botSurface==null))
		{
			topSurface = new Point3d[numOfSurfacePoints];
			botSurface = new Point3d[numOfSurfacePoints];
			computeSurfaces(nextElement);
		}
		
		return botSurface;
	}
	
	private void computeSurfaces(MTGBranchElement nextElement)
	{	
		Point3d pt = new Point3d((double)this._origin.x,
				(double)this._origin.y,
				(double)this._origin.z);

		Point3d vec;
		Point3d pt2;
		if(nextElement==null)
		{
			vec = new Point3d(this._dirp);
			vec.scale(this.length);
			pt2 = new Point3d(pt.x+vec.x,
					pt.y+vec.y,
					pt.z+vec.z);
		}
		else
		{
			pt2 = new Point3d(nextElement.getOrigin());
			vec = new Point3d();
			vec.sub(pt2,pt);
		}

		//step 1 translate
		Matrix4d transToOrigin = new Matrix4d();
		transToOrigin.setIdentity();
		transToOrigin.setTranslation(new Vector3d(-pt.x,-pt.y,-pt.z));

		Point3d pt_t = new Point3d();
		Point3d pt2_t = new Point3d();
		transToOrigin.transform(pt,pt_t);
		transToOrigin.transform(pt2,pt2_t);


		//step 2 rotation to yz plane
		vec.sub(pt2_t, pt_t);
		double rotateToYZPlaneAngle=0;

		if((vec.x==0)&&(vec.y>=0))
			rotateToYZPlaneAngle=0;
		else if((vec.x==0)&&(vec.y<0))
			rotateToYZPlaneAngle=Math.PI;
		else if((vec.x>0)&&(vec.y>0))
			rotateToYZPlaneAngle=Math.atan(vec.x/vec.y);
		else if((vec.x>0)&&(vec.y<0))
			rotateToYZPlaneAngle=Math.atan(Math.abs(vec.y)/vec.x)+(Math.PI/2.0);
		else if((vec.x<0)&&(vec.y<0))
			rotateToYZPlaneAngle=Math.atan(Math.abs(vec.x)/Math.abs(vec.y)) + Math.PI;
		else if((vec.x<0)&&(vec.y>0))
			rotateToYZPlaneAngle=Math.atan(vec.y/Math.abs(vec.x)) + (Math.PI/2.0*3.0);
		else if((vec.y==0)&&(vec.x>0))
			rotateToYZPlaneAngle=(Math.PI/2.0);
		else if((vec.y==0)&&(vec.x<0))
			rotateToYZPlaneAngle=(Math.PI/2.0*3.0);

		Matrix4d rotateToYZPlane = new Matrix4d();
		rotateToYZPlane.setIdentity();
		rotateToYZPlane.rotZ(rotateToYZPlaneAngle);

		Point3d pt_r1 = new Point3d();
		Point3d pt2_r1 = new Point3d();
		rotateToYZPlane.transform(pt_t,pt_r1);
		rotateToYZPlane.transform(pt2_t,pt2_r1);


		//step 3 rotation to y axis
		vec.sub(pt2_r1, pt_r1);
		double rotateToYAxisAngle=0;

		if((vec.z==0)&&(vec.y>=0))
			rotateToYAxisAngle=0;
		else if((vec.z==0)&&(vec.y<0))
			rotateToYAxisAngle=Math.PI;
		else if((vec.z>0)&&(vec.y>0))
			rotateToYAxisAngle=Math.atan(vec.y/vec.z) + (Math.PI/2.0*3.0);
		else if((vec.z>0)&&(vec.y<0))
			rotateToYAxisAngle=Math.atan(vec.z/Math.abs(vec.y))+(Math.PI);
		else if((vec.z<0)&&(vec.y<0))
			rotateToYAxisAngle=Math.atan(Math.abs(vec.y)/Math.abs(vec.z)) + (Math.PI/2.0);
		else if((vec.z<0)&&(vec.y>0))
			rotateToYAxisAngle=Math.atan(Math.abs(vec.z)/vec.y);
		else if((vec.y==0)&&(vec.z>0))
			rotateToYAxisAngle=(Math.PI/2.0*3.0);
		else if((vec.y==0)&&(vec.z<0))
			rotateToYAxisAngle=(Math.PI/2.0);

		Matrix4d rotateToYAxis = new Matrix4d();
		rotateToYAxis.setIdentity();
		rotateToYAxis.rotX(rotateToYAxisAngle);

		Point3d pt_r2 = new Point3d();
		Point3d pt2_r2 = new Point3d();
		rotateToYAxis.transform(pt_r1,pt_r2);
		rotateToYAxis.transform(pt2_r1,pt2_r2);

		//step 4 transform pt and pt2 to align with y-axis
		Point3d pt_a = new Point3d(pt_r2);
		Point3d pt2_a = new Point3d(pt2_r2);

		//generate surface points
		double angleIncrement = (Math.PI*2.0/numOfSurfacePoints);
		for(int i=0; i<numOfSurfacePoints; i++)
		{
			double x = Math.cos((double)(i+1)*angleIncrement)*botdia;
			double z = Math.sin((double)(i+1)*angleIncrement)*botdia;
			botSurface[i] = new Point3d((double)x,0,(double)z);
		}
		for(int i=0; i<numOfSurfacePoints; i++)
		{
			double x = Math.cos((double)(i+1)*angleIncrement)*topdia;
			double z = Math.sin((double)(i+1)*angleIncrement)*topdia;
			topSurface[i] = new Point3d((double)x,pt2_a.y,(double)z);
		}

		//inverse transformations
		rotateToYAxis.invert();
		rotateToYZPlane.invert();
		transToOrigin.invert();

		//inverse transform surface points
		for(int i=0; i<botSurface.length; ++i)
		{
			rotateToYAxis.transform(botSurface[i]);
			rotateToYZPlane.transform(botSurface[i]);
			transToOrigin.transform(botSurface[i]);
		}
		for(int i=0; i<topSurface.length; ++i)
		{
			rotateToYAxis.transform(topSurface[i]);
			rotateToYZPlane.transform(topSurface[i]);
			transToOrigin.transform(topSurface[i]);
		}
		
		//if(nextElement!=null)
		//	nextElement.setBotDia(topdia);
	}
	
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getTopDia() {
		return topdia;
	}

	public void setTopDia(double topdia) {
		this.topdia = topdia;
	}

	public double getBotDia() {
		return botdia;
	}

	public void setBotDia(double botdia) {
		this.botdia = botdia;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public Vector3d getDirp() {
		return _dirp;
	}

	public void setDirp(Vector3d _dirp) {
		if(this._dirp==null)
			this._dirp = new Vector3d();
		this._dirp.set(_dirp);
	}
	
	public Vector3d getDirs() {
		return _dirs;
	}

	public void setDirs(Vector3d _dirs) {
		if(this._dirs==null)
			this._dirs = new Vector3d();
		this._dirs.set(_dirs);
	}
	
	public Vector3d getDirt() { 
		Vector3d _dirt = new Vector3d();
		_dirt.cross(_dirp,_dirs); 
		_dirt.normalize();
		return _dirt;
	}

	public Vector3d getOrigin() {
		return _origin;
	}

	public void setOrigin(Vector3d _origin) {
		if(this._origin==null)
			this._origin = new Vector3d();
		this._origin.set(_origin);
	}

	

	public MTGBranchElement(int _nodeIndex)
	{
		nodeIndex=_nodeIndex;
		length=0;
		topdia=-1;
		botdia=-1;
		alpha=0;
		beta=0;
		gamma=0;
		numOfSurfacePoints=16;
		order=-1;
	}
	
	public int getNodeIndex()
	{
		return nodeIndex;
	}
	
	public double getBeta()
	{
		return beta;
	}
	
	public void setBeta(double beta)
	{
		this.beta = beta;
	}
}
