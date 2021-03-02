/*
 * Copyright (C) 2016 GroIMP Developer Team
 *
 * Department Ecoinformatics, Biometrics and Forest Growth,
 * University of Göttingen, Germany
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */ 

package de.grogra.lignum.stlLignum;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.sky.FirmamentWithMask;
import de.grogra.lignum.stlVoxelspace.VoxelMovement;
import de.grogra.lignum.stlVoxelspace.VoxelSpace;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class EvaluateRadiationSelfVoxel implements Mathsym {

	private VoxelSpace voxel_space;
	private double needle_area;
	private double forest_k;
	private double tree_height;
	private double crownbase_height;
	private double density;
	private Point3d stem_loc;
	private ParametricCurve K;

	private FirmamentWithMask firmament = null;
	private int number_of_sectors = 0;

	/**
	 * 
	 * @param firmament
	 */
	public EvaluateRadiationSelfVoxel(FirmamentWithMask firmament) {
		this.firmament = firmament;
		number_of_sectors = firmament.numberOfRegions();
	}

	/**
	 * 
	 * @param vs
	 * @param Af
	 * @param for_k
	 * @param tree_h
	 * @param Hcb
	 * @param dens
	 * @param x0
	 * @param k_func
	 */
	public void setTreeParameters(VoxelSpace vs, double Af, double for_k,
			double tree_h, double Hcb, double dens, Point3d x0,
			ParametricCurve k_func) {
		voxel_space = vs;
		needle_area = Af;
		forest_k = for_k;
		tree_height = tree_h;
		crownbase_height = Hcb;
		density = dens;
		stem_loc = new Point3d(x0);
		K = new ParametricCurve(k_func);
	}

	public CfTreeSegment eval(CfTreeSegment tc) {

		tc.setLGAQin(0.0);
		tc.setLGAQabs(0.0);
		// Original Comment:
		// Radiation conditions are not evaluated if the segment has no
		// foliage (in practice there would be division by 0 in computing
		// absorbed radiation)

		if (tc.getLGAWf() < R_EPSILON) {
			return tc;
		}

		Vector<Double> radiation_direction = new Vector<Double>(3);
		radiation_direction.add(0, 0d);
		radiation_direction.add(1, 0d);
		radiation_direction.add(2, 0d);

		Point3d middle = new Point3d(tc.getMidPoint()); // TODO: Like this or as
														// pointer ?
		double par_a = 0.0, par_b = 0.0; // dummy arguments here
		AccumulateOpticalThickness AOT = new AccumulateOpticalThickness(
				voxel_space.getXSideLength(), par_a, par_b);
		Vector<Double> s = new Vector<Double>(number_of_sectors);
		for (int i = 0; i < number_of_sectors; i++) {
			s.add(i, 0d);
		}
		// For forest effect in radiation
		double z = middle.z;
		double dist = sqrt(pow(middle.x - stem_loc.x, 2.0)
				+ pow(middle.y - stem_loc.y, 2.0));

		for (int i = 0; i < number_of_sectors; i++) {
			double Iop = firmament.diffuseForestRegionRadiationSum(i, z, dist,
					needle_area, forest_k, tree_height, crownbase_height, radiation_direction, density);

			double transmission_voxel = 1.0;
			Vector<VoxelMovement> vm = new Vector<VoxelMovement>();
			PositionVector dir = new PositionVector(radiation_direction);
			ParametricCurve K = new ParametricCurve();
			voxel_space.getRoute(vm, middle, dir, K, false);
			double optical_thickness = 0;
			for (int j = 0; j < vm.size(); j++) {
				optical_thickness = AOT.eval(optical_thickness, vm.get(j));
			}

			// Original Comment:
			// Vahenna target_segmentin vaikutus pois

			double k;
			if (vm.get(0).n_segs_real > 0.0)
				k = max(0.0, -0.014 + 1.056 * vm.get(0).STAR_mean);
			else
				k = 0.0;
			optical_thickness -= k * tc.getLGAAf() * vm.get(0).l
					/ voxel_space.getBoxVolume();

			if (optical_thickness < 0.0)
				optical_thickness = 0.0;

			if (optical_thickness < 20.0) {
				transmission_voxel = exp(-optical_thickness);
			} else {
				transmission_voxel = 0.0;
			}

			Iop *= transmission_voxel;
			s.set(i, Iop);
		} // End of no_sectors ...

		double Q_in = 0;
		for (int j = 0; j < s.size(); j++) {
			Q_in = Q_in + s.get(j);
		}

		// Original Comment:
		// s contains now incoming radiation from each sector. Evaluate how
		// much segment absorbs from incoming radiation.
		double Lk, inclination, Rfk, Ack, extinction, sfk, Ask, Wfk;
		Lk = Rfk = Ack = extinction = sfk = Ask = Wfk = 0.0;
		Lk = tc.getLGAL(); // length is > 0.0, otherwise we would not bee here
		Rfk = tc.getLGARf(); // Radius to foliage limit
		Wfk = tc.getLGAWf(); // Foliage mass
		sfk = tc.getLGAsf(); // Foliage m2/kg from segment!!!

		for (int i = 0; i < number_of_sectors; i++) {
			firmament.diffuseRegionRadiationSum(i, radiation_direction);
			double a_dot_b = tc.getDirection().dot(
					new PositionVector(radiation_direction));
			inclination = PI_DIV_2 - acos(abs(a_dot_b));

			Ack = 2.0 * Lk * Rfk * cos(inclination) + PI * pow(Rfk, 2.0)
					* sin(inclination);
			extinction = K.eval(inclination);

			if (Ack == 0.0) {
				// cout <<
				// "ERROR EvaluateRadiationForCfTreeSegment: Ack == 0 (division by 0)"
				// //TODO: Error catch
				// << endl;
				Ack = 0.000001;
			}

			// implement I(k)p = Ip*Ask, Note Ack must be greater than 0 (it
			// should if there is any foliage)
			Ask = (1.0 - exp(-extinction * ((sfk * Wfk) / Ack))) * Ack;
			s.set(i, s.get(i) * Ask);
		}
		double Q_abs = 0;
		for (int j = 0; j < s.size(); j++) {
			Q_abs = Q_abs + s.get(j);
		}

		tc.setLGAQabs(Q_abs);
		tc.setLGAQin(Q_in);

		return tc;
	}

}
