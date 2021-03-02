package de.grogra.numeric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.rgg.Library;
import de.grogra.xl.ode.RateAssignment;

public abstract class BasicODE implements ODE {
	public final void getRate(double[] out, double t, double[] state) {
		GraphManager graph = Library.graph();

		// prepare rate array
		Arrays.fill(out, 0);
		graph.rate = out;

		// TODO
		
		// call the actual rate function
		getRate();
	}

	public abstract void getRate();
	
	public static List<RateAssignment> getRateAssignments(Registry r)
	{
		List<RateAssignment> list = new ArrayList<RateAssignment>();
		Item dir = r.getItem("/classes");
		for (Item c = (Item) dir.getBranch(); c != null; c = (Item) c.getSuccessor())
		{
			if (c.getName().endsWith("$ODEHelper"))
			{
				try {
					RateAssignment[] ra = (RateAssignment[]) Reflection.get(
							null, Reflection.getDeclaredField((Type) ((TypeItem) c).getObject(), "TABLE"));
					list.addAll(Arrays.asList(ra));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
