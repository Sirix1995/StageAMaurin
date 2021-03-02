
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.xl.query;

/**
 * A <code>Producer</code> is used within right hand sides of XL rules
 * in order to construct
 * the replacement for the match of the left hand side. 
 * 
 * @author Ole Kniemeyer
 */
public interface Producer
{
	/**
	 * Indicates the XL rule arrow <code>==&gt;</code>.
	 */
	int SIMPLE_ARROW = 0;

	/**
	 * Indicates the XL rule arrow <code>==&gt;&gt;</code>.
	 */
	int DOUBLE_ARROW = 1;

	/**
	 * Indicates the XL rule arrow <code>::&gt;</code>.
	 */
	int EXECUTION_ARROW = 2;

	/**
	 * This method is invoked by the XL run-time system in order to notify the
	 * producer about the beginning of a right-hand side (i.e., a match for the left-hand side
	 * has been found, and the right-hand side is executed).
	 * 
	 * @param arrow the type of rule arrow, one of {@link #SIMPLE_ARROW},
	 * {@link #DOUBLE_ARROW}, {@link #EXECUTION_ARROW}
	 * 
	 * @return <code>true</code> if the right-hand side shall be executed,
	 * <code>false</code> if its execution shall be skipped
	 */
	boolean producer$beginExecution (int arrow);

	/**
	 * This method is invoked by the XL run-time system in order to notify the
	 * producer about the end of the execution of a right-hand side.
	 * 
	 * @param executed return value of invocation of
	 * {@link #producer$beginExecution}
	 */
	void producer$endExecution (boolean executed);

	void producer$visitEdge (EdgePattern pattern);

	/**
	 * Returns the graph for which this producer constructs the
	 * right-hand side structur.
	 * 
	 * @return the graph on which this producer operates
	 */
	Graph producer$getGraph ();
}
