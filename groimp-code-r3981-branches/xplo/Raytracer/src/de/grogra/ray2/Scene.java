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

package de.grogra.ray2;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;

import de.grogra.ray.physics.Interior;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.vecmath.geom.BoundingBox;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.OctreeUnion;
import de.grogra.vecmath.geom.Volume;

/**
 * A <code>Scene</code> represents the complete geometry, shading,
 * and lighting information which is needed for a raytracer. In addition,
 * it defines the sensors within the scene for a radiation model.
 * <p>
 * Note that
 * the methods are not thread-safe: For each additional thread,
 * its own instance of <code>Scene</code> has to be created from the
 * original instance via the method {@link #dup()}. 
 * 
 * @author Ole Kniemeyer
 */
public interface Scene
{
	/**
	 * Returns a clone of this <code>Scene</code>.
	 * All constant variables which are related to the structure
	 * of the scene are copied shallowly, state variables
	 * are newly created without copying.
	 * 
	 * @return clone of this scene
	 */
	Scene dup ();

	/**
	 * Computes intersections between the boundary surface of the objects
	 * of the scene and the specified <code>line</code>. For the precise
	 * behaviour and the meaning of the parameters, see
	 * {@link Volume#computeIntersections}.
	 *
	 * @param line a line
	 * @param which one of {@link Intersection#ALL},
	 * {@link Intersection#CLOSEST}, {@link Intersection#ANY}, this
	 * determines which intersections have to be added to <code>list</code>
	 * @param list the intersections are added to this list
	 * @param excludeStart intersection at start point which shall be excluded, or <code>null</code>
	 * @param excludeEnd intersection at end point which shall be excluded, or <code>null</code>
	 * @return <code>true</code> iff the beginning of the line lies
	 * within the volume (i.e., if the line starts within the volume or
	 * enters the volume at the starting point); however note that the returned
	 * value is valid only if <code>which == Intersection.ALL</code>
	 * 
	 * @see Volume#computeIntersections
	 */
	boolean computeIntersections (Line line, int which, IntersectionList list,
			Intersection excludeStart, Intersection excludeEnd);

	/**
	 * Returns a bounding box which contains all finite geometric
	 * objects of the scene. The returned box must not be modified.
	 * 
	 * @return bounding box of finite geometry
	 */
	BoundingBox getBoundingBox ();

	/**
	 * Returns the shader which is associated with volume <code>v</code>.
	 * 
	 * @param v a volume
	 * @return corresponding shader
	 */
	Shader getShader (Volume v);

	/**
	 * Returns the interior which is associated with volume <code>v</code>.
	 * 
	 * @param v a volume
	 * @return corresponding interior
	 */
	Interior getInterior (Volume v);

	/**
	 * Returns the index in {@link #getLights()} of the light which is
	 * associated with volume <code>v</code>, or <code>-1</code>
	 * if no such light exists.
	 * 
	 * @param v a volume
	 * @return index of corresponding light in {@link #getLights()}
	 */
	int getLight (Volume v);

	/**
	 * Returns the index in {@link #getSensors()} of the sensor which is
	 * associated with volume <code>v</code>, or <code>-1</code>
	 * if no such sensor exists.
	 * 
	 * @param v a volume
	 * @return index of corresponding sensor in {@link #getSensors()}
	 */
	int getSensor (Volume v);

	/**
	 * Transforms a point in global world coordinates to local object
	 * coordinates of the volume <code>v</code>.
	 * 
	 * @param v volume which defines the local object coordinates
	 * @param global input point in global world coordinates
	 * @param localOut output point in local object coordinates
	 */
	void transform (Volume v, Tuple3d global, Tuple3d localOut);

	/**
	 * Returns an array of all lights in the scene. The returned array
	 * must not be modified. The corresponding coordinate transformations
	 * are obtained by {@link #getLightTransformation}.
	 * 
	 * @return array of all lights
	 */
	Light[] getLights ();

	/**
	 * Returns the affine light transformation from
	 * local light coordinates to global world coordinates
	 * for the light having index <code>light</code> in
	 * {@link #getLights()}.
	 * 
	 * @param light index of light in {@link #getLights()}
	 * @return light transformation for <code>light</code>
	 * 
	 * @see #getLights()
	 */
	Matrix4d getLightTransformation (int light);

	/**
	 * Returns the affine light transformation from
	 * global world coordinates to local light coordinates 
	 * for the light having index <code>light</code> in
	 * {@link #getLights()}.
	 * 
	 * @param light index of light in {@link #getLights()}
	 * @return inverse light transformation for <code>light</code>
	 * 
	 * @see #getLights()
	 */
	Matrix4d getInverseLightTransformation (int light);

	/**
	 * Returns an array of all sensors in the scene. The returned array
	 * must not be modified. The corresponding coordinate transformations
	 * are obtained by {@link #getSensorTransformation}.
	 * 
	 * @return array of all sensors
	 */
	Sensor[] getSensors ();

	/**
	 * Returns the affine sensor transformation from
	 * local sensor coordinates to global world coordinates
	 * for the sensor having index <code>sensor</code> in
	 * {@link #getSensors()}.
	 * 
	 * @param sensor index of sensor in {@link #getSensors()}
	 * @return sensor transformation for <code>sensor</code>
	 * 
	 * @see #getSensors()
	 */
	Matrix4d getSensorTransformation (int sensor);

	/**
	 * Returns the affine sensor transformation from
	 * global world coordinates to local sensor coordinates 
	 * for the sensor having index <code>sensor</code> in
	 * {@link #getSensors()}.
	 * 
	 * @param sensor index of sensor in {@link #getSensors()}
	 * @return inverse sensor transformation for <code>sensor</code>
	 * 
	 * @see #getSensors()
	 */
	Matrix4d getInverseSensorTransformation (int sensor);

	/**
	 * Returns a modification stamp for the underlying scene graph.
	 * Each modification increments the value, so that the test
	 * whether some modification occured can be simply performed
	 * on values of the stamp.
	 * 
	 * @return a stamp for the whole graph
	 */
	int getStamp ();

	/** 
	 * Returns a unique name for the current scene, so that the test
	 * whether two scenes are identical can be performed.
	 * 
	 * @returna unique name for the current scene.
	 */
	String getUniqueName();
	
	/**
	 * Returns an object identifying the underlying scene graph.
	 * 
	 * @return scene graph
	 */
	Object getGraph ();

	/**
	 * This factory method creates a new spectrum which shall be used for
	 * light computations within the context of this scene.
	 * 
	 * @return new spectrum instance for use in computations 
	 */
	Spectrum createSpectrum ();

	/**
	 * Appends some statistics information about the scene to
	 * <code>stats</code>.
	 * 
	 * @param stats buffer for statistics information
	 */
	void appendStatistics (StringBuffer stats);
	
	OctreeUnion getOctree();
	

}
