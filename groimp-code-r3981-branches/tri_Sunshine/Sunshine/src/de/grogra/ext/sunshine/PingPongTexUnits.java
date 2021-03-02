package de.grogra.ext.sunshine;

import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;

public class PingPongTexUnits {
	
	String name 		= "";
	IntList texIDs		= new IntList();
	LongList timeStamps = new LongList();
	
	int oldestID;
	int newestID;
	
	public PingPongTexUnits(String name)
	{
		this.name 	= name;
	}
	
	public void putTexID(int id)
	{
		texIDs.add(id);
		timeStamps.add(-1L);
	}
	
	public void swap()
	{
		long oldestStamp = -1;
		long newestStamp = -1;
		
		long tmpStamp;
		
		for(int i = 0; i < texIDs.size(); i++)
		{
			tmpStamp = timeStamps.get(i);
			
			if(oldestStamp < tmpStamp || oldestStamp < 0)
			{
				oldestStamp = tmpStamp;
				oldestID 	= texIDs.get(i);
			}
			
			if(newestStamp > tmpStamp || newestStamp < 0)
			{
				newestStamp = tmpStamp;
				newestID 	= texIDs.get(i);
			}
		}
		
		timeStamps.set(oldestID, System.currentTimeMillis());
	}
	
	public int getNextInputTex()
	{
		return newestID;
	}
	
	public int getNextOutputTex()
	{
		return oldestID;
	}
	
	
}
