package de.lmu.ifi.dbs.elki;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.spatial.Polygon;
import de.lmu.ifi.dbs.elki.math.geometry.AlphaShape;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

public class Test {

	public static void main(String[] args) {
		List<Vector> data = new ArrayList<Vector>();
		
		/*
		data.add(new Vector(43,17));
		data.add(new Vector(25,79));
		data.add(new Vector(14,33));
		data.add(new Vector(37,72));
		data.add(new Vector(51,66));
		data.add(new Vector(37,58));
		data.add(new Vector(42,44));
		data.add(new Vector(17,35));
		data.add(new Vector(23,14));
		data.add(new Vector(32,34));
		data.add(new Vector(45,64));
		*/
		
		data.add(new Vector(0,0));
		data.add(new Vector(0.378560463, 0));
		data.add(new Vector(0.565387787, 0.248899149));
		data.add(new Vector(0.694823114, -0.295869846));
		data.add(new Vector(-0.449999988, 0));
		data.add(new Vector(-0.667572081, 0.33940791));
		data.add(new Vector(-0.831309308, -0.403458854));
		
		for(int i =-25; i<25; i++) {
			double alpha = (i/10f);
			AlphaShape as = new AlphaShape(data, alpha);
			List<Polygon> res = as.compute();
			if(res.size()!=0) {
				System.out.println("r= "+res + "  "+alpha+"  "+res.size());
			}
		}
		System.out.println("fertisch");		
		System.exit(0);
	}

	
}
