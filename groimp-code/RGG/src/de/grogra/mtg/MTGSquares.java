package de.grogra.mtg;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

public class MTGSquares {

	private double deltaPlant;
	private ArrayList<MTGVoxel> voxels;

	public MTGSquares(double deltaPlant)
	{
		this.deltaPlant = deltaPlant;
		voxels = new ArrayList<MTGVoxel>();
	}

	public Vector3d getMin(int voxelIndex)
	{
		if(voxels.size()-1 >= voxelIndex)
		{
			return voxels.get(voxelIndex).getMin();
		}
		else
			return null;
	}

	public Vector3d getMax(int voxelIndex)
	{
		if(voxels.size()-1 >= voxelIndex)
		{
			return voxels.get(voxelIndex).getMax();
		}
		else
			return null;
	}

	public void addVoxel(MTGVoxel voxel)
	{
		boolean exists=false;
		for(int i=0; i<voxels.size(); ++i)
		{
			if(voxels.get(i).equals(voxel))
				exists=true;
		}
		if(!exists)
			voxels.add(voxel);
	}

	public void computePositions(int n)
	{
		double ddy=0;
		double ddx=deltaPlant;

		for(int index=0; index<voxels.size();++index)
		{
			if(index%n!=0)
			{
				Vector3d min=voxels.get(index).getMin();
				Vector3d max=voxels.get(index-1).getMax();

				double y1=(double)min.y;
				double y2=(double)max.y;

				if (deltaPlant>0)
				{
					voxels.get(index).setDeltay(y2+deltaPlant-y1);
				}
				else
				{
					voxels.get(index).setDeltay(-deltaPlant+ddy); // In this case _deltaPlant is negative.
					ddy-=-deltaPlant;
				}
			}
			else
			{
				ddy=0;
				ddx-=deltaPlant;
			}

			if (index>=n)
			{
				// Translation X

				Vector3d min=voxels.get(index).getMin();
				Vector3d max=voxels.get(index-n).getMax();

				double x1=(double) min.x;
				double x2=(double) max.x;

				if (deltaPlant>0)
				{
					voxels.get(index).setDeltax(x2+deltaPlant-x1);
				}
				else
				{
					voxels.get(index).setDeltax(ddx);
				}
			}

			voxels.get(index).translate();
		}
	}
}
