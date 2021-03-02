package de.grogra.xl.ode;

import de.grogra.xl.property.RuntimeModel;
import de.grogra.xl.property.RuntimeModel.Property;

/**
 * A RateAssignemnt stores class and property of a rate assignment
 * (operator symbol <code>:'=</code>).
 * For a rate assignment of the form <b>a[n] :'= 1</b> the
 * class of <b>a</b> will be stored in the field <code>cls</code>
 * and the property named <b>n</b> will be stored in the
 * field named <code>property</code>.
 * Note that <code>cls</code> may be the class where the property
 * was declared in, but also a subclass. 
 * 
 * @author Reinhard Hemmerling
 * 
 */
public class RateAssignment {
	public RuntimeModel.Property property;
	public Class cls;

	public static RateAssignment create(RuntimeModel.Property p, Class c)
	{
		return new RateAssignment(p, c);
	}
	
	private RateAssignment(Property property, Class cls) {
		this.property = property;
		this.cls = cls;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cls == null) ? 0 : cls.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RateAssignment other = (RateAssignment) obj;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.equals(other.cls))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	public String toString()
	{
		return "RateAssignment[" + property + "," + cls + "]";
	}
}
