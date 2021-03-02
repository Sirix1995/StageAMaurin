package de.grogra.ray2.radiosity;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;

import de.grogra.ray2.tracing.PixelwiseRenderer;

/**
 * This is the main radiosity calculation class.
 * @author Ralf Kopsch
 */
public class RadiosityAlgorithm {
	private float iterTreshold = 0;
	private PixelwiseRenderer renderer;
	private int steps = 0;
	
	/**
	 * Constructor.
	 * @param renderer the PixelwiseRenderer.
	 */
	public RadiosityAlgorithm(PixelwiseRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Calculates the scene.
	 * @param globalGroupList the list of patch groups. 
	 * @param subDivThresh the subdivision threshold.
	 * @param maxSubDivDepth the recursion depth.
	 * @param threadCount the thread count.
	 */
	public void calculateScene(Vector<PatchGroup> globalGroupList, float subDivThresh, int maxSubDivDepth, int threadCount) {
		boolean restart = true;
		Vector<PatchGroup> subdivGroups = new Vector<PatchGroup>(globalGroupList);
		
		while (restart && !isStopped() && (steps < maxSubDivDepth)) {
//			StopWatch all = new StopWatch();
			this.renderer.setMessage("Calculating Radiosity. Iteration = " + (steps + 1), -1);
//			System.out.println("PatchGroupCount GlobalGroups: " + globalGroupList.size() + " subDivGroups: " + subdivGroups.size());
			projectAllPatches(subdivGroups, threadCount);
			computeAllPatchColors(subdivGroups);
			if (steps + 1 < maxSubDivDepth) {
				computeChangedPatches(globalGroupList, subdivGroups, subDivThresh);
				if (subdivGroups.isEmpty()) {
					restart = false;
				}				
			}
			steps++;
//			all.stop("radiosity");
		}
	}

	private void computeChangedPatches(Vector<PatchGroup> globalGroupList, Vector<PatchGroup> subDivList, float subDivThresh) {
		List<SubPatch> deprecatedPatches = new LinkedList<SubPatch>(); 
		subDivList.clear();
		
		// check if subdivide is needed
		for( int g = 0; g < globalGroupList.size(); g++) {
			PatchGroup pg = globalGroupList.get(g);
			if (pg.subdivide(globalGroupList, deprecatedPatches, subDivList, subDivThresh)) {
				g--;
			}
		}
		for (PatchGroup pg: globalGroupList) {
			if (pg.ffMapContains(deprecatedPatches)) {
				pg.clearRadiosity();
				subDivList.add(pg);
			}
		}
	}
	
	private void projectAllPatches(Vector<PatchGroup> groups, int threadCount) {
//		StopWatch project = new StopWatch(); 
		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			threads[i] = new Thread(new ProjectThread(groups, i, threadCount));
			threads[i].start();
		}
		for (Thread t: threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		project.stop("Project");
	}
	
	private void computeAllPatchColors(Vector<PatchGroup> groups) {
//		StopWatch color = new StopWatch();
		for (int c = 0; c < 3; c++) {
			float lastMaxDiff = this.iterTreshold + 1;
			while (lastMaxDiff > this.iterTreshold) {
				lastMaxDiff = 0;
				lastMaxDiff = computeRadiosity(groups, lastMaxDiff, c);
			}
		}
//		color.stop("color calculation");
	}
	
	private float computeRadiosity(Vector<PatchGroup> groups, float lastMaxDiff, int c) {
		int numGroups = groups.size();
		
		for (int g = 0; ((g < numGroups) && !isStopped()); g++) {
			PatchGroup thisPatchGroup = groups.get(g);
			float reflectance = getComponent(thisPatchGroup.getReflectance(), c);
			for(int p = 0; p < 4; p++) {	// for each patch in group

				Map<SubPatch, FormFactor> patchFormFactors = thisPatchGroup.getFormFactors(p);
				float componentSum = 0;
				for (SubPatch otherPatch: patchFormFactors.keySet()) {
					Color3f otherPatchRad = otherPatch.getRadiosity();
					// only this color component
					float otherComponent = getComponent(otherPatchRad, c);
					
					// form factor of this patch times radiosity of other patch
					componentSum += patchFormFactors.get(otherPatch).getValue() * otherComponent;
				}
				Color3f patchRad = thisPatchGroup.getRadiosity(p);
				Color3f patchEmittance = thisPatchGroup.getEmittance(p);

				float oldValue = getComponent(patchRad, c);
				setComponent(patchRad, c, getComponent(patchEmittance, c) + reflectance * componentSum);

				// check this change against lastMaxDiff
				float diff = getComponent(patchRad, c) - oldValue;
				if( diff > lastMaxDiff ) lastMaxDiff = diff;
				else if( -diff > lastMaxDiff) lastMaxDiff = -diff;
			}
		}
		return lastMaxDiff;	
	}
	
	
	
	private class ProjectThread implements Runnable {
		private Vector<PatchGroup> groups;
		private int start;
		private int step;
		
		public ProjectThread(Vector<PatchGroup> groups, int start, int step) {
			this.groups = groups;
			this.start = start;
			this.step = step;
		}

		public void run() {
			int pos = start;
			while ((pos < this.groups.size()) && !isStopped()) {
				this.groups.get(pos).project(this.groups);
				pos += step;
			}
		}
	}
	
    /**
     * Returns the value with the index i.
     * @param pos The index
     * @return Returns the value with the index i.
     */
	public float getComponent(Tuple3f t, int pos) {
		float ret = 0;
		switch (pos) {
		case 0:
			ret = t.x;
			break;
		case 1:
			ret = t.y;
			break;
		case 2:
			ret = t.z;
			break;
		default:
			System.err.println("ERROR in Tuple3f no Component " + pos + " found");
			break;
		}
		return ret;
	}
	
	/**
	 * Sets the value with the index i to a new Value.
	 * @param pos The index.
	 * @param newVal The new value to set.
	 */
	public void setComponent(Tuple3f t, int pos, float newVal) {
		switch (pos) {
		case 0:
			t.x = newVal;
			break;
		case 1:
			t.y = newVal;
			break;
		case 2:
			t.z = newVal;
			break;
		default:
			System.err.println("ERROR in Tuple3f cannot set Component " + pos);
			break;
		}
	}
	
	/**
	 * Returns the current calculation step.
	 * @return Returns the current calculation step.
	 */
	public int getSteps() {
		return this.steps;
	}
	
	private boolean isStopped() {
		return this.renderer.isStopped();
	}
}

