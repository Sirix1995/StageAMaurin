package de.grogra.gpuflux.scene.filter;

import java.io.Serializable;

/**
 * @author      Dietger van Antwerpen <dietger@xs4all.nl>
 * @version     1.0                                       
 * @since       2011.0824                                 
 *
 * The NoneFilter class filters in all objects
 */
public class AllFilter extends ObjectFilter{

	//enh:sco de.grogra.persistence.SCOType
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AllFilter representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AllFilter ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AllFilter.class);
		$TYPE.validate ();
	}

//enh:end
	
	public boolean filter(Object obj) {
		return true;
	}

	public void enter(Object obj) {
	}

	public void leave(Object obj) {
	}

}
