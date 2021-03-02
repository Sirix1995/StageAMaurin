package de.grogra.graph.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import de.grogra.graph.Attributes;
import de.grogra.graph.impl.Node.NType;

/**
 * This class represents a scale in a structure-of-scales. 
 * 
 * It contains a visibility flag (representing visibility of a scale) 
 * and a sub class to provide the functionality of observation. Here,
 * observation refers to the observer design pattern.
 * 
 * This class is part of the extension of XL for multiscale modelling.
 * 
 * @since 18-02-2013
 * @author yong
 *
 */
public class Scale extends Node{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5502721738158946354L;
	
	public static final NType $TYPE;
	public static final NType.Field visible$FIELD;
	public static final NType.Field rgg$FIELD;
	
	private boolean visible;
	private ScaleObserver scaleObserver;
	public Object rgg;
	
	private static void initType ()
	{
		$TYPE.addDependency (Attributes.RGG, Attributes.RGG);
	}
	
	static
	{
		$TYPE = new NType (Scale.class);
		$TYPE.addManagedField (visible$FIELD = new _Field ("visible", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.addManagedField (rgg$FIELD = new _Field ("rgg", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 1));
		$TYPE.declareFieldAttribute (visible$FIELD, Attributes.VISIBLE);
		$TYPE.declareFieldAttribute (rgg$FIELD, Attributes.RGG);
		initType();
		$TYPE.validate ();
	}
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Scale.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((Scale) o).visible = value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Scale) o).visible;
			}
			return super.getBoolean (o);
		}
		
		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 1:
					((Scale) o).rgg = value;
					return;
			}
			super.setObjectImpl (o, value);
		}
		
		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 1:
					return ((Scale) o).rgg;
			}
			return super.getObject (o);
		}
		
	}
	
	private class ScaleObserver extends java.util.Observable implements Observer
	{
		//reference to parent scale node
		private Scale scale;
		
		//Map from observable to argument and method name.
		//(In order to invoke a specified method, given an observable and argument) 
		private HashMap<Observable, HashMap<Object, String> > observableMethodMap;
		
		/**
		 * Constructor
		 * @param scale
		 */
		public ScaleObserver(Scale scale)
		{
			this.scale = scale;
			this.observableMethodMap = new HashMap<Observable, HashMap<Object, String> >();
		}
		
		/**
		 * Register the method (by its name) to invoke given the notification from an 
		 * observable and an argument object.
		 * @param o
		 * @param arg
		 * @param methodName
		 */
		public void observe(ScaleObserver o, Object arg, String methodName)
		{
			HashMap<Object, String> argMethodMap = observableMethodMap.get(o);

			//lazy addition to hash map
			if(argMethodMap == null)
			{
				argMethodMap = new HashMap<Object,String>();
				observableMethodMap.put(o, argMethodMap);
			}

			argMethodMap.put(arg, methodName);

			o.addObserver(this);
		}

		/**
		 * Register the method (by its name) to invoke given the notification from an 
		 * observable and an argument object.
		 * @param o
		 * @param arg
		 * @param methodName
		 */
		public void observe(ScaleObserver o, int arg, String methodName)
		{
			Integer i = new Integer(arg);

			observe(o, i, methodName);
		}

		/**
		 * Notify observers of this observable that a changed occurred with given argument.
		 * @param arg
		 */
		public void notify(Object arg)
		{
			this.setChanged();
			this.notifyObservers(arg);
		}

		/**
		 * For primitive int arguments
		 * @param arg
		 */
		public void notify(int arg)
		{
			Integer i = new Integer(arg);
			notify(i);
		}

		/**
		 * Method invoked when the observable object observed by this object invokes notify
		 */
		@Override
		public void update(java.util.Observable o, Object arg) {
			HashMap<Object, String> argMethodMap = observableMethodMap.get(o);

			//if no argument & method-name pair for given observable, return.
			if(argMethodMap == null)
				return;

			String methodName = argMethodMap.get(arg);

			//if no method name for given argument, return.
			if(methodName == null)
				return;

			//method name found
			try {
				//get reference to method
				java.lang.reflect.Method method = scale.getClass().getDeclaredMethod(methodName);
				method.setAccessible(true);
				//invoke the method
				method.invoke(scale);
			} catch (NoSuchMethodException e) {
				updateRGG(methodName);
			} catch (SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
				return;
			}
		}
		
		/**
		 * If method "update" could not successfully find the method name in this scale class,
		 * this method is invoked to search for the method in the RGG class.
		 */
		public void updateRGG(String methodName)
		{
			if(scale.rgg==null)
				return;
			
			try {
				//get reference to method
				java.lang.reflect.Method method = scale.rgg.getClass().getDeclaredMethod(methodName);
				method.setAccessible(true);
				//invoke the method
				method.invoke(scale.rgg);
			} catch (NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
				return;
			}
		}
		
		public void changed()
		{
			this.setChanged();
		}
	}
	
	public Scale()
	{
		super();
		visible = true;
		scaleObserver = new ScaleObserver(this);
		this.rgg = null;
	}
	
	public Scale(Object rgg)
	{
		super();
		visible = true;
		scaleObserver = new ScaleObserver(this);
		this.rgg = rgg;
	}

	/**
	 * Accessor method for visibility flag of this scale
	 * @return visible flag
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Setter method for setting the visibility flag of this scale
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Getter method for getting scaleObserver
	 * @return scaleObserver
	 */
	public ScaleObserver getScaleObserver()
	{
		return scaleObserver;
	}
	
	public void addObserver(Scale s)
	{
		this.scaleObserver.addObserver(s.scaleObserver);
	}
	
	public void addObserver(Observer o)
	{
		this.scaleObserver.addObserver(o);
	}
	
	public void observe(Scale s, int arg, String methodName)
	{
		this.scaleObserver.observe(s.getScaleObserver(), arg, methodName);
	}
	
	public void observe(Scale s, Object arg, String methodName)
	{
		this.scaleObserver.observe(s.getScaleObserver(), arg, methodName);
	}
	
	public void notify(Object arg)
	{
		this.scaleObserver.changed();
		this.scaleObserver.notifyObservers(arg);
	}

	public void notify(int arg)
	{
		Integer i = new Integer(arg);
		notify(i);
	}
}
