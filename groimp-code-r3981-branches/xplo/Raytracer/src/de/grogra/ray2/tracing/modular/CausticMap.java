package de.grogra.ray2.tracing.modular;

import javax.vecmath.Color4f;

public class CausticMap {

	public CausticElement[][] causticMap;
	private CausticElement[] map;
	
	public CausticMap(int width, int height) {

		causticMap = new CausticElement[height][width];
		for(int i=0; i< height; i++){
			for(int j=0; j<width; j++){
				causticMap[i][j] = new CausticElement();
			}
		}
		
	}
	
	public CausticMap(int size)
	{
		map = new CausticElement[size];
		
		for(int i = 0; i < size; i++)
		{
			map[i] = new CausticElement();
		} //for
	}
	
	public void saveColor(Color4f causCol, int pos)
	{
		CausticElement target = map[pos];
		
		if(causCol == null || pos == 0)
		{
			target.nonCausticCounter++;
		}
		else
		{
			if(target.color == null)
			{
				target.color = causCol;
			}
			else
			{
				target.color.add(causCol);
			}
			
			target.causticCounter++;
		} //if
		
	} //saveColor
	
	
	public void saveColor(Color4f causCol, int x,int y){
		CausticElement target = causticMap[y][x];
		
		if(causCol==null){
			target.nonCausticCounter++;
		}else{
			if(target.color==null) target.color = causCol;
			else target.color.add(causCol);
			target.causticCounter++;
			
		}
		
	}
	
	public CausticElement getCausticElement(int pos)
	{
		return map[pos];
	}
	
	
	public class CausticElement{
		public Color4f color = null;
		public int causticCounter = 0;
		public int nonCausticCounter = 0;
	}

}
