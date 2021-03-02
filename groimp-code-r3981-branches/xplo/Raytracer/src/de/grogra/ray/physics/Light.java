
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

package de.grogra.ray.physics;


/**
 * A <code>Light</code> instance represents a light source.
 * The interface specializes the common methods of the <code>Emitter</code>
 * interface for light sources.
 * 
 * @author Ole Kniemeyer
 */
public interface Light extends Emitter
{
	/**
	 * This return value for {@link #getLightType()} indicates that
	 * this light should actually be ignored.
	 */
	int NO_LIGHT = 0;

	/**
	 * This return value for {@link #getLightType()} indicates a
	 * point light, i.e., a light source whose rays emanate from a
	 * single point.
	 * This includes the usual point lights
	 * which radiate equally in all directions, but also more general lights
	 * with a direction-dependent radiant intensity, e.g., spot lights.
	 */
	int POINT = 1;

	/**
	 * This return value for {@link #getLightType()} indicates an
	 * area light. 
	 */
	int AREA = 2;

	/**
	 * This return value for {@link #getLightType()} indicates a
	 * directional light.
	 */
	int DIRECTIONAL = 3;

	/**
	 * This return value for {@link #getLightType()} indicates a
	 * sky light.
	 */
	int SKY = 4;

	/**
	 * This return value for {@link #getLightType()} indicates an
	 * ambient light. Ambient lights have no physically plausible counterpart
	 * in the real world and, thus, should not be considered in physically
	 * based rendering at all.
	 */
	int AMBIENT = 5;

	/**
	 * Determines the type of light source which is represented by this
	 * light.
	 * 
	 * @return one of {@link #NO_LIGHT}, {@link #POINT}, {@link #AREA},
	 * {@link #DIRECTIONAL}, {@link #SKY}, {@link #AMBIENT}. 
	 */
	int getLightType ();

	/**
	 * Determines whether the light source casts shadows or not.
	 * @return <code>true</code> iff the light source does not cast shadows 
	 */
	boolean isShadowless ();

	/**
	 * Determines whether the light source shall be ignored when a shot ray
	 * happens to hit the geometry of the light source.
	 * @return <code>true</code> iff the light source shall be ignored 
	 */
	boolean isIgnoredWhenHit ();

	/**
	 * Computes the total power of this light source which is emitted to the
	 * region defined by <code>env.bounds</code>. Note that the computed
	 * value is not necessarily exact: It should be used just as a hint, e.g.,
	 * when one of a set of lights has to be chosen randomly on the basis of
	 * their relative power.
	 * 
	 * @param env environment which defines the bounds of the scene
	 * @return total power emitted to the region <code>env.bounds</env>
	 */
	double getTotalPower (Environment env);

}
