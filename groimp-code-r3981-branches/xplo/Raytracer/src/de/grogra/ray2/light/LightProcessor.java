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

package de.grogra.ray2.light;

import java.util.ArrayList;
import java.util.Random;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.vecmath.geom.Intersection;

/**
 * Interface that encapsulates the specific light calculation. It has 
 * to compute all light rays that directly illuminate a specific
 * intersection point. 
 * 
 * @author Michaal Tauer
 * @author Ole Kniemeyer
 */
public interface LightProcessor
{
	/**
	 * Returns a clone of this <code>LightProcessor</code>.
	 * All constant variables are copied shallowly, state variables
	 * are newly created and copied where necessary.
	 * 
	 * @param scene duplicate of scene 
	 * @return clone of this light processor
	 */
	LightProcessor dup (Scene scene);

	/**
	 * Initializes the light processor for use with the given
	 * <code>scene</code>.
	 * 
	 * @param scene the scene in which light rays are to be computed
	 * @param raytracerType type of raytracer
	 * @param random pseudorandom generator 
	 * ({@link Environment#STANDARD_RAY_TRACER} or
	 * {@link Environment#PATH_TRACER})
	 */
	void initialize (Scene scene, int raytracerType, Random random);

	/**
	 * Adds all light rays that directly illuminate the specified
	 * intersection point <code>is</code> to the list <code>rays</code>.
	 * <code>frontFace</code> indicates whether the front face
	 * (the side where the normal vector points to)
	 * or the back face of the surface at the intersection point is to be
	 * illuminated. 
	 * <p>
	 * The <code>cache</code> may be used freely by implementations
	 * of this method in order to store some caching information.
	 * Invokers of this method should provide the same cache for
	 * similar situations. E.g., a raytracer should provide an own cache
	 * for every node of the recursive raytracing tree.
	 * 
	 * @param frontFace illuminate front face or back face?
	 * @param is the intersection point
	 * @param rays all determined rays are added to this list
	 * @param cache the cache may be used freely by implementations
	 * @param random pseudorandom generator 
	 */
	void getLightRays (boolean frontFace, Intersection is, RayList rays,
			ArrayList cache, Random random);

	/**
	 * Appends some statistics information about the light computations to
	 * <code>stats</code>. This method will be invoked after the whole
	 * rendering process has completed.
	 * 
	 * @param stats buffer for statistics information
	 */
	void appendStatistics (StringBuffer stats);
}
