package de.grogra.numeric;

import de.grogra.xl.lang.VoidToDouble;

/**
 * This class represents a monitor function.
 * Associated is an event handler, that is called
 * when the monitor triggers. It returns a flag
 * indicating if integration should stop after
 * the event handler was called.
 * 
 * @author Reinhard Hemmerling
 *
 */
interface MonitorEntry {
	/**
	 * Evaluates current state to obtain a single value.
	 * When the value changes sign during course of integration
	 * the root of the function will be found and an event will 
	 * be triggered.
	 * @param t time
	 * @param y state
	 * @return when zero an event triggers
	 */
	double g(double t, double[] y);
	/**
	 * Handle an event that was triggered.
	 * Returns a flag indicating if integration should proceed
	 * or stop after the event was handled.
	 * @param t time
	 * @param y state
	 * @return true if integration should stop
	 */
	boolean handleEvent(double t, double[] y);
}
