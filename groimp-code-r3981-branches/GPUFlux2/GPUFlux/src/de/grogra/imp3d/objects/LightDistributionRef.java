/*
 * Copyright (C) 2013 GroIMP Developer Team
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


package de.grogra.imp3d.objects;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3f;

import de.grogra.pf.registry.ItemReference;
import de.grogra.pf.registry.Registry;

public class LightDistributionRef extends ItemReference<LightDistributionIF> implements LightDistributionIF
{
	//enh:sco
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends ItemReference.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LightDistributionRef representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ItemReference.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new LightDistributionRef ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (LightDistributionRef.class);
		$TYPE.validate ();
	}

//enh:end

	
	LightDistributionRef ()
	{
		super (null);
	}

	
	public LightDistributionRef (String name)
	{
		super (name);
	}

	public synchronized LightDistributionIF resolve ()
	{
//		objectResolved = false;
//		Object oo = resolveObject ("/objects/lights", Registry.current ());
		LightDistributionIF ld = (objectResolved ? object : resolveObject ("/objects/3d/lights/lights", Registry.current ()));
		return ld;
	}


	@Override
	public Object manageableWriteReplace ()
	{
		return resolve ();
	}

	

	@Override
	public int getWidth() {
		return resolve ().getWidth();
	}


	@Override
	public int getHeight() {
		return resolve ().getHeight();
	}


	@Override
	public double[][] getDistribution() {
		return resolve ().getDistribution();
	}


	@Override
	public double[] getLinearCDF() {
		return resolve ().getLinearCDF();
	}


	@Override
	public void setDistribution(double[][] lipdf) {
		resolve ().setDistribution(lipdf);
	}


	@Override
	public void setDistributionEx(double[][] lipdf) {
		resolve ().setDistributionEx(lipdf);
	}


	@Override
	public double getPower() {
		return resolve ().getPower();
	}


	@Override
	public void setPower(double power) {
		resolve ().setPower(power);
	}


	@Override
	public double getDensityAt(Vector3f direction) {
		return resolve ().getDensityAt(direction);
	}


	@Override
	public double map2direction(Vector3f outDirection, Tuple2d inPoint) {
		return resolve ().map2direction(outDirection, inPoint);
	}


}

