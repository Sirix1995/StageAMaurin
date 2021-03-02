package de.grogra.rgg;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MSObservable extends Observable implements Serializable, Observer {

	private static final long serialVersionUID = 7464202729209768585L;
	
	//Map from observable to argument and method name.
	//(In order to invoke a specified method, given an observable and argument) 
	private HashMap<Observable, HashMap<Object, String> > observableMethodMap;
	
	public MSObservable()
	{
		super();
		observableMethodMap = new HashMap<Observable, HashMap<Object, String> >();
	}
	
	/**
	 * Register the method (by its name) to invoke given the notification from an 
	 * observable and an argument object.
	 * @param o
	 * @param arg
	 * @param methodName
	 */
	public void observe(MSObservable o, Object arg, String methodName)
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
	public void observe(MSObservable o, int arg, String methodName)
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
	public void update(Observable o, Object arg) {
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
			Method method = this.getClass().getMethod(methodName);
			//invoke the mehod
			method.invoke(this);
		} catch (NoSuchMethodException e) {
			//Library.println("Method \"" + methodName + "\" not found in class \"" + this.getClass() + "\"");
			return;
		} catch (SecurityException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		} catch (IllegalArgumentException e) {
			return;
		} catch (InvocationTargetException e) {
			return;
		} catch (Exception e){
			return;
		}
	}
	
	/**
	 * Empty implementation of writeObject method in Serializable interface.
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
	}
	
	/**
	 * Empty implementation of readObject method in Serializable interface.
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
	}

	/*
	private void readObjectNoData() throws ObjectStreamException
	{
	}
	*/
}
