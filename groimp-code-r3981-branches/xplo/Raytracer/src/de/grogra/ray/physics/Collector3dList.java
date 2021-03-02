package de.grogra.ray.physics;

import javax.vecmath.Tuple3d;

/**
 * This class distinguish 
 * @author adge-k
 *
 */
public class Collector3dList extends Collector3d 
{
	private Collector3d primary 	= new Collector3d();
	private Collector3d secondary 	= new Collector3d();
	
	public Collector3dList()
	{
		
	}
	
	public Collector3dList(double x, double y, double z)
	{
		super ( x, y, z );
		this.add((Spectrum) this );
	}
	
	public Collector3dList(Spectrum spectrum)
	{		
		this.add((Spectrum) spectrum);
	}
	
	
	public void addToStatistic(Tuple3d rayOrigin, Spectrum3d spectrum, double scaleFactor, boolean isPrimary)
	{
		if(isPrimary)
			primary.addToStatistic(rayOrigin, spectrum, scaleFactor, isPrimary);
		else
			secondary.addToStatistic(rayOrigin, spectrum, scaleFactor, isPrimary);
	}
	
	public void add(Spectrum collector)
	{		
		super.add((Collector) collector);
		
		if(collector instanceof Collector3dList)
		{		
			primary.setAsCollector();
			secondary.setAsCollector();
			
			primary.add((Collector) ((Collector3dList) collector).getPrimary());
			secondary.add((Collector) ((Collector3dList) collector).getSecondary());
		}
	}
	
	public Collector3d getPrimary()
	{
		primary.setAsCollector();
		return primary.clone();
	}
	
	public Collector3d getSecondary()
	{
		secondary.setAsCollector();
		return secondary.clone();
	}
	
	public void setZero()
	{
		super.setZero();
		primary.setZero();
		secondary.setZero();
	}
	
	public Collector3dList newInstance ()
	{
		return new Collector3dList();
	}
	
	public Collector3dList clone()
	{
		Collector3dList colList = new Collector3dList();
		
		colList.x				= super.x;		
		colList.y				= super.y;	
		colList.z				= super.z;	
		
		primary.setAsCollector();
		secondary.setAsCollector();
		
		//colList.primary 		= this.primary.clone();
				
		colList.primary.x						= this.primary.x;
		colList.primary.y						= this.primary.y;
		colList.primary.z						= this.primary.z;		
		
		colList.primary.rayCount 				= this.primary.rayCount;
		
		colList.primary.squareSumDirectionX 	= this.primary.squareSumDirectionX;
		colList.primary.squareSumDirectionY 	= this.primary.squareSumDirectionY;
		colList.primary.squareSumDirectionZ 	= this.primary.squareSumDirectionZ;
		
		colList.primary.sumDirectionX 			= this.primary.sumDirectionX;
		colList.primary.sumDirectionY			= this.primary.sumDirectionY;
		colList.primary.sumDirectionZ 			= this.primary.sumDirectionZ;
		
		colList.primary.squareSumSpectrumX 		= this.primary.squareSumSpectrumX;
		colList.primary.squareSumSpectrumY 		= this.primary.squareSumSpectrumY;
		colList.primary.squareSumSpectrumZ 		= this.primary.squareSumSpectrumZ;
		
		colList.primary.sumSpectrumX 			= this.primary.sumSpectrumX;
		colList.primary.sumSpectrumY 			= this.primary.sumSpectrumY;
		colList.primary.sumSpectrumZ 			= this.primary.sumSpectrumZ;

		//colList.secondary 					= this.secondary.clone();
		
		colList.secondary.x						= this.secondary.x;
		colList.secondary.y						= this.secondary.y;
		colList.secondary.z						= this.secondary.z;		
		
		colList.secondary.rayCount 				= this.secondary.rayCount;
		
		colList.secondary.squareSumDirectionX 	= this.secondary.squareSumDirectionX;
		colList.secondary.squareSumDirectionY 	= this.secondary.squareSumDirectionY;
		colList.secondary.squareSumDirectionZ 	= this.secondary.squareSumDirectionZ;
		
		colList.secondary.sumDirectionX 		= this.secondary.sumDirectionX;
		colList.secondary.sumDirectionY			= this.secondary.sumDirectionY;
		colList.secondary.sumDirectionZ 		= this.secondary.sumDirectionZ;
		
		colList.secondary.squareSumSpectrumX 	= this.secondary.squareSumSpectrumX;
		colList.secondary.squareSumSpectrumY 	= this.secondary.squareSumSpectrumY;
		colList.secondary.squareSumSpectrumZ 	= this.secondary.squareSumSpectrumZ;
		
		colList.secondary.sumSpectrumX 			= this.secondary.sumSpectrumX;
		colList.secondary.sumSpectrumY 			= this.secondary.sumSpectrumY;
		colList.secondary.sumSpectrumZ 			= this.secondary.sumSpectrumZ;
		
		
		return colList;
	}
	
}
