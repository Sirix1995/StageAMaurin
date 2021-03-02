package de.grogra.animation.interpolation.linear;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import de.grogra.persistence.*;
import de.grogra.animation.interpolation.Interpolation;
import de.grogra.math.TMatrix4d;
import de.grogra.math.TVector3d;
import de.grogra.math.UniformScale;

public class LinearInterpolation extends ShareableBase implements Interpolation {

	//enh:sco SCOType
	
	// private HashMap<Integer, ValueEntry> timeMap;
	private HashMap<Integer,ValueEntry> timeMap;
	// enh:field
	
	/**
	 * Only used to determine a suitable key.
	 * May not be consistent with real keys.
	 */
	// private TreeSet<Integer> sortedKeys;
	private TreeSet<Integer> sortedKeys;
	// enh:field
		
	private InterpolationRule ir;
	// enh:field
	
	public LinearInterpolation() {
		timeMap = new HashMap<Integer,ValueEntry>();
		sortedKeys = new TreeSet<Integer>();
	}
	
	public void setInterpolationType(de.grogra.reflect.Type<?> type) {
		if (type.equals(Type.BOOLEAN))
			ir = new BooleanInterpolationRule();
		else if (type.equals(Type.BYTE))
			ir = new ByteInterpolationRule();
		else if (type.equals(Type.SHORT))
			ir = new ShortInterpolationRule();
		else if (type.equals(Type.CHAR))
			ir = new CharacterInterpolationRule();
		else if (type.equals(Type.INT))
			ir = new IntegerInterpolationRule();
		else if (type.equals(Type.LONG))
			ir = new LongInterpolationRule();
		else if (type.equals(Type.FLOAT))
			ir = new FloatInterpolationRule();
		else if (type.equals(Type.DOUBLE))
			ir = new DoubleInterpolationRule();
		else
			ir = new ObjectInterpolationRule();
	}
	
	public boolean putValue(int time, Object value) {
		// add/overwrite value for property
		ValueEntry entry;
		// TODO: convert TVector3d or UniformScale or TMatrix4d to ComponentTransform
//		if (value instanceof Transform3D) {
//			value = convertToTMatrix4d(value);
//		}
		if (timeMap.containsKey(time)) {
			entry = timeMap.get(time);
			entry.setValue(value);
			return true;
		}
		entry = new ValueEntry();
		entry.setValue(value);
		timeMap.put(time, entry);
		return false;
	}
	
	public void changeValue(int oldTime, int newTime, Object value) {
		ValueEntry entry = timeMap.get(oldTime);
		if (entry != null) {
			entry.setValue(value);
	
			timeMap.remove(oldTime);
			timeMap.put(newTime, entry);
		}
	}
	
	public synchronized Object getValue(double time) {
		Object result = null;
		int key = (int) time;
		
		if (timeMap.containsKey(time)) {
			// we have an entry for the node+property+time
			ValueEntry entry = timeMap.get(key);
			result = entry.getValue();
		}
		else {
			// no entry for the time
			// search for entries at other time values

			Integer iLower = null;
			Integer iHigher = null;

			sortedKeys.clear();
			sortedKeys.addAll(timeMap.keySet());
			Iterator<Integer> it = sortedKeys.iterator();

			Integer previous = null;

			while (it.hasNext()) {
				Integer i = it.next();
				iLower = i;
				if (i > key) {
					iHigher = i;
					iLower = previous;
					break;
				} // if
				previous = i;
			} // while

			if ((iLower != null) && (iHigher != null)) {
				// interpolate
				result = getInterpolatedValue(time,
						iLower, timeMap.get(iLower.intValue()),
						iHigher, timeMap.get(iHigher.intValue()));
			}
			else if (iLower != null) {
				// use lower value
				ValueEntry entry = timeMap.get(iLower.intValue());
				result = entry.getValue();
			}
			else if (iHigher != null) {
				// use higher value
				ValueEntry entry = timeMap.get(iHigher.intValue());
				result = entry.getValue();
			}
		}
		
		return result;
	}
	
	private Object getInterpolatedValue(double currentTime,
			int time1, ValueEntry entry1, int time2, ValueEntry entry2) {
		
		Object result = null;
		
		Object value1 = entry1.getValue();
		Object value2 = entry2.getValue();
		
		double ratio = (currentTime - (double) time1)
				/ ((double) time2 - (double) time1);
		
		result = ir.getInterpolatedValue(value1, value2, ratio);

		return result;
	}
	
	public void getTimes(Set<Integer> times) {
		times.addAll(timeMap.keySet());
	}
	
	@Override
	public String toString() {
		return timeMap.toString();
	}
	
	private Object convertToTMatrix4d(Object value) {
		Object result = value;
		if (value instanceof TVector3d) {
			result = new TMatrix4d((TVector3d) value);
		}
		else if (value instanceof UniformScale) {
			result = new TMatrix4d();
			((TMatrix4d) result).setScale(((UniformScale) value).getScale());
		}
		return result;
	}
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field timeMap$FIELD;
	public static final Type.Field sortedKeys$FIELD;
	public static final Type.Field ir$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LinearInterpolation representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((LinearInterpolation) o).timeMap = (HashMap<Integer,ValueEntry>) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((LinearInterpolation) o).sortedKeys = (TreeSet<Integer>) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((LinearInterpolation) o).ir = (InterpolationRule) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((LinearInterpolation) o).timeMap;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((LinearInterpolation) o).sortedKeys;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((LinearInterpolation) o).ir;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new LinearInterpolation ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (LinearInterpolation.class);
		timeMap$FIELD = Type._addManagedField ($TYPE, "timeMap", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (HashMap.class), null, Type.SUPER_FIELD_COUNT + 0);
		sortedKeys$FIELD = Type._addManagedField ($TYPE, "sortedKeys", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (TreeSet.class), null, Type.SUPER_FIELD_COUNT + 1);
		ir$FIELD = Type._addManagedField ($TYPE, "ir", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (InterpolationRule.class), null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

//enh:end
	
}
