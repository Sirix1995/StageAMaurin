
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
 * A <code>Variable</code> represents a query variable of a query.
 * When a query is evaluated, a single {@link Frame} is allocated which
 * holds the current values of all query variables,
 * and values are bound to the variable within the frame.
 * <p>
 * Initially, a query variable is not bound to a value. This is indicated by
 * {@link #isSet} returning <code>false</code>. Values are bound via the
 * setter-methods. This includes the method {@link #nullset} which sets the
 * variable to a null-value, a feature used by optional patterns without match.
 * A binding is removed by {@link #unset}. 
 * 
 * @author Ole Kniemeyer
 */
public interface Variable
{
	void unset (Frame frame);

	void nullset (Frame frame);

	boolean isSet (Frame frame);

	boolean isNull (Frame frame);

/*!!

#foreach ($type in $vmtypes)
$pp.setType($type)

	$type ${pp.prefix}get (Frame frame);

	void ${pp.prefix}set (Frame frame, $type value);

#end

!!*/
//!! #* Start of generated code
// generated
// generated
// generated
	int iget (Frame frame);
// generated
	void iset (Frame frame, int value);
// generated
// generated
// generated
	long lget (Frame frame);
// generated
	void lset (Frame frame, long value);
// generated
// generated
// generated
	float fget (Frame frame);
// generated
	void fset (Frame frame, float value);
// generated
// generated
// generated
	double dget (Frame frame);
// generated
	void dset (Frame frame, double value);
// generated
// generated
// generated
	Object aget (Frame frame);
// generated
	void aset (Frame frame, Object value);
// generated
// generated
//!! *# End of generated code

}
