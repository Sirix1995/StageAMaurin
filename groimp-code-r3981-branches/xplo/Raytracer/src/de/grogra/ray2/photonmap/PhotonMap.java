package de.grogra.ray2.photonmap;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;

import de.grogra.vecmath.geom.Intersection;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

/**
 * This class provides a photon map. 
 * The photon map uses a kd-tree data structure for fast access.
 * @author Ralf Kopsch
 */
public class PhotonMap {
	private static double epsilon = 0.005d;
	private KDTree photonMap;
	private double photonArea;
	private int entryCount = 0;
	
	
	/**
	 * Creates a new Photon Map.
	 * @param photonArea the scanning area.
	 */
	public PhotonMap(double photonArea) {
		this.photonMap = new KDTree(3);
		this.photonArea = photonArea;
	}
	
	/**
	 * Inserts a new Photon into this map.
	 * @param col The Photon color.
	 * @param pos The Photon position.
	 * @param dir The Photon impact direction. 
	 */
	public void insertPhoton(Color3f col, Point3d pos, Vector3f dir){
		this.entryCount++;
		double posa[] = {pos.x, pos.y, pos.z};
//		Vector3f direction = new Vector3f(dir);
//		dir.negate();
		Photon newPhoton = new Photon(new Point3d(col), new Vector3f(dir));
		try {
			photonMap.insert(posa, newPhoton);
		} catch (KeySizeException e) {
			e.printStackTrace();
		} catch (KeyDuplicateException e) {
			try {
				Photon temp = (Photon)photonMap.search(posa);
				photonMap.delete(posa);
				temp.col.add(new Point3d(col));
				photonMap.insert(posa, temp);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Calculates the color for the given point.
	 * @param point the impact point.
	 * @param normal the impact normal vector. 
	 * @param color output - the calculated color
	 */
	public void sumPhotons(Point3d point, Vector3f normal, Tuple3d color) {
		Object[] photons;
		Photon photon = null;
		Point3d tempColor = new Point3d();
		photons = getNearestPhotons(point);
		for(int i = 0; i < photons.length; i++) {
			photon = (Photon) photons[i];
			float factor = photon.dir.dot(normal);
			if (factor > 0) {
//				tempColor.add(photon.col);
				tempColor.set(tempColor.x + photon.col.x * factor, tempColor.y + photon.col.y * factor, tempColor.z + photon.col.z * factor );
			}
		}
		/*
		// calculate distance between point and the last photon  
		double distance = 0;
		if(n != null){
			distance = n.pos.distanceSquared(point);
		}
		tempColor.scale(1 / (distance * Math2.M_PI));
		*/
		tempColor.scale(1d / (this.photonArea * this.photonArea));
		color.add(tempColor);
	}
	
	/**
	 * Returns an array of Photons near the given point.
	 * @param point impact point.
	 * @return Returns an array of Photons near the given point.
	 */
	private Object[] getNearestPhotons(Point3d point) {
		Object[] photons;
		double limit = this.photonArea;
		double[] low = {point.x - limit, point.y - limit, point.z - limit};
		double[] up  = {point.x + limit, point.y + limit, point.z + limit};
		try {
//			double[] posArray = {point.x, point.y, point.z};			
//			photons = photonMap.nearest(posArray, photonRange);
			photons = photonMap.range(low, up);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (KeySizeException e) {
			e.printStackTrace();
			return null;
		}
		return photons;
	}
	
	/**
	 * This is only a debug function. It direct visualise the photon map.
	 * @param desc the intersection description
	 * @param color output - the calculated color
	 * @return transparent value
	 */
	public float traceRay(Intersection desc, Tuple3d color) {
		Point3d p = desc.getPoint();
		try {
			Object[] photones = this.photonMap.range(new double[] {p.x - epsilon, p.y - epsilon, p.z - epsilon}, 
					  new double[] {p.x + epsilon, p.y + epsilon, p.z + epsilon});
			if ((photones != null) && photones.length > 0) {
				color.set(0,0,0);
				for (Object photon: photones) {
					color.add(((Photon) photon).col);
				}
				color.scale(2000);
			} else {
				color.set(0,0,0);
			}
		} catch (KeySizeException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Returns the number of photon map entries.
	 * @return Returns the number of photon map entries.
	 */
	public int getEntryCount() {
		return this.entryCount;
	}
	
	/**
	 * Private Photon class. It describes a node in the kd-tree.
	 * @author Ralf
	 */
	private class Photon {
		public Point3d  col;
		public Vector3f dir;

		public Photon(Point3d col, Vector3f dir) {
			this.col = col;
			this.dir = dir;
		}
	}
}
