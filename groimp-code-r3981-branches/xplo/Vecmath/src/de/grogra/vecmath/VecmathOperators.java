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

package de.grogra.vecmath;

import javax.vecmath.*;

import de.grogra.vecmath.geom.Volume;

public final class VecmathOperators
{

	private VecmathOperators ()
	{
	}


	/**
	 * This operator method is an alias for
	 * <code>(set != null) && set.contains(point, false)</code>.
	 * 
	 * @param point a point
	 * @param set a volume
	 * @return <code>true</code> iff <code>point</code> is contained in <code>set</code>
	 * 
	 * @see Volume#contains(Tuple3d, boolean)
	 */
	public static boolean operator$in (Tuple3d point, Volume set)
	{
		return (set != null) && set.contains (point, false);
	}

/*!!
#foreach ($dim in [2..4])

  #foreach ($prec in ["float", "double"])
  
	#set ($dest = "$dim$prec.charAt(0)")

	public static Vector$dest operator$mul (Tuple$dest a, $prec b)
	{
		Vector$dest r = new Vector$dest ();
		r.scale (b, a);
		return r;
	}


	public static Point$dest operator$mul (Point$dest a, $prec b)
	{
		Point$dest r = new Point$dest ();
		r.scale (b, a);
		return r;
	}


	public static Vector$dest operator$div (Tuple$dest a, $prec b)
	{
		Vector$dest r = new Vector$dest ();
		r.scale (1 / b, a);
		return r;
	}


	public static Point$dest operator$div (Point$dest a, $prec b)
	{
		Point$dest r = new Point$dest ();
		r.scale (1 / b, a);
		return r;
	}


	public static Vector$dest operator$mul ($prec b, Tuple$dest a)
	{
		Vector$dest r = new Vector$dest ();
		r.scale (b, a);
		return r;
	}


	public static Point$dest operator$mul ($prec b, Point$dest a)
	{
		Point$dest r = new Point$dest ();
		r.scale (b, a);
		return r;
	}


	public static Vector$dest operator$neg (Tuple$dest a)
	{
		Vector$dest r = new Vector$dest ();
		r.negate (a);
		return r;
	}


	public static Point$dest operator$neg (Point$dest a)
	{
		Point$dest r = new Point$dest ();
		r.negate (a);
		return r;
	}


	public static Vector$dest operator$mulAssign (Vector$dest a, $prec b)
	{
		a.scale (b);
		return a;
	}


	public static Point$dest operator$mulAssign (Point$dest a, $prec b)
	{
		a.scale (b);
		return a;
	}

  #end

  #foreach ($op in ["+", "-"])
  
  	#if ($op == "+")
  	  #set ($name = "operator$add")
  	#else
  	  #set ($name = "operator$sub")
  	#end

	#set ($constr = "a.x $op b.x, a.y $op b.y")
	#if ($dim > 2)
	  #set ($constr = "$constr, a.z $op b.z")
	#end
	#if ($dim > 3)
	  #set ($constr = "$constr, a.w $op b.w")
	#end
	 

	#foreach ($prec in ["fff", "dfd", "ddf", "ddd"])

		#set ($dest = "$dim$prec.charAt(0)")
		#set ($first = "$dim$prec.charAt(1)")
		#set ($second = "$dim$prec.charAt(2)")



	public static Point$dest $name (Point$first a, Tuple$second b)
	{
		return new Point$dest ($constr);
	}


	public static Point$dest $name (Tuple$first a, Point$second b)
	{
		return new Point$dest ($constr);
	}

#if ($op == "-")
	#set ($res = "Vector$dest")
#else
	#set ($res = "Point$dest")
#end

	public static $res $name (Point$first a, Point$second b)
	{
		return new $res ($constr);
	}


	public static Vector$dest $name (Vector$first a, Vector$second b)
	{
		return new Vector$dest ($constr);
	}

#if ($dest == $first)

	public static Point$dest ${name}Assign (Point$first a, Tuple$second b)
	{
		a.set ($constr);
		return a;
	}


	public static Vector$dest ${name}Assign (Vector$first a, Vector$second b)
	{
		a.set ($constr);
		return a;
	}

#end

	#end

  #end

#end
!!*/
//!! #* Start of generated code
// generated
    
	
	public static Vector2f operator$mul (Tuple2f a, float b)
	{
		Vector2f r = new Vector2f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point2f operator$mul (Point2f a, float b)
	{
		Point2f r = new Point2f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector2f operator$div (Tuple2f a, float b)
	{
		Vector2f r = new Vector2f ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Point2f operator$div (Point2f a, float b)
	{
		Point2f r = new Point2f ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Vector2f operator$mul (float b, Tuple2f a)
	{
		Vector2f r = new Vector2f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point2f operator$mul (float b, Point2f a)
	{
		Point2f r = new Point2f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector2f operator$neg (Tuple2f a)
	{
		Vector2f r = new Vector2f ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Point2f operator$neg (Point2f a)
	{
		Point2f r = new Point2f ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Vector2f operator$mulAssign (Vector2f a, float b)
	{
		a.scale (b);
		return a;
	}
// generated
// generated
	public static Point2f operator$mulAssign (Point2f a, float b)
	{
		a.scale (b);
		return a;
	}
// generated
    
	
	public static Vector2d operator$mul (Tuple2d a, double b)
	{
		Vector2d r = new Vector2d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point2d operator$mul (Point2d a, double b)
	{
		Point2d r = new Point2d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector2d operator$div (Tuple2d a, double b)
	{
		Vector2d r = new Vector2d ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Point2d operator$div (Point2d a, double b)
	{
		Point2d r = new Point2d ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Vector2d operator$mul (double b, Tuple2d a)
	{
		Vector2d r = new Vector2d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point2d operator$mul (double b, Point2d a)
	{
		Point2d r = new Point2d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector2d operator$neg (Tuple2d a)
	{
		Vector2d r = new Vector2d ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Point2d operator$neg (Point2d a)
	{
		Point2d r = new Point2d ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Vector2d operator$mulAssign (Vector2d a, double b)
	{
		a.scale (b);
		return a;
	}
// generated
// generated
	public static Point2d operator$mulAssign (Point2d a, double b)
	{
		a.scale (b);
		return a;
	}
// generated
  
    
  	  	
				 
// generated
	
		
// generated
// generated
	public static Point2f operator$add (Point2f a, Tuple2f b)
	{
		return new Point2f (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2f operator$add (Tuple2f a, Point2f b)
	{
		return new Point2f (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2f operator$add (Point2f a, Point2f b)
	{
		return new Point2f (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Vector2f operator$add (Vector2f a, Vector2f b)
	{
		return new Vector2f (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2f operator$addAssign (Point2f a, Tuple2f b)
	{
		a.set (a.x + b.x, a.y + b.y);
		return a;
	}
// generated
// generated
	public static Vector2f operator$addAssign (Vector2f a, Vector2f b)
	{
		a.set (a.x + b.x, a.y + b.y);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point2d operator$add (Point2f a, Tuple2d b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$add (Tuple2f a, Point2d b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$add (Point2f a, Point2d b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Vector2d operator$add (Vector2f a, Vector2d b)
	{
		return new Vector2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	
		
// generated
// generated
	public static Point2d operator$add (Point2d a, Tuple2f b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$add (Tuple2d a, Point2f b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$add (Point2d a, Point2f b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Vector2d operator$add (Vector2d a, Vector2f b)
	{
		return new Vector2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$addAssign (Point2d a, Tuple2f b)
	{
		a.set (a.x + b.x, a.y + b.y);
		return a;
	}
// generated
// generated
	public static Vector2d operator$addAssign (Vector2d a, Vector2f b)
	{
		a.set (a.x + b.x, a.y + b.y);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point2d operator$add (Point2d a, Tuple2d b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$add (Tuple2d a, Point2d b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$add (Point2d a, Point2d b)
	{
		return new Point2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Vector2d operator$add (Vector2d a, Vector2d b)
	{
		return new Vector2d (a.x + b.x, a.y + b.y);
	}
// generated
// generated
	public static Point2d operator$addAssign (Point2d a, Tuple2d b)
	{
		a.set (a.x + b.x, a.y + b.y);
		return a;
	}
// generated
// generated
	public static Vector2d operator$addAssign (Vector2d a, Vector2d b)
	{
		a.set (a.x + b.x, a.y + b.y);
		return a;
	}
// generated
// generated
	
    
  	  	
				 
// generated
	
		
// generated
// generated
	public static Point2f operator$sub (Point2f a, Tuple2f b)
	{
		return new Point2f (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2f operator$sub (Tuple2f a, Point2f b)
	{
		return new Point2f (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2f operator$sub (Point2f a, Point2f b)
	{
		return new Vector2f (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2f operator$sub (Vector2f a, Vector2f b)
	{
		return new Vector2f (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2f operator$subAssign (Point2f a, Tuple2f b)
	{
		a.set (a.x - b.x, a.y - b.y);
		return a;
	}
// generated
// generated
	public static Vector2f operator$subAssign (Vector2f a, Vector2f b)
	{
		a.set (a.x - b.x, a.y - b.y);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point2d operator$sub (Point2f a, Tuple2d b)
	{
		return new Point2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2d operator$sub (Tuple2f a, Point2d b)
	{
		return new Point2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2d operator$sub (Point2f a, Point2d b)
	{
		return new Vector2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2d operator$sub (Vector2f a, Vector2d b)
	{
		return new Vector2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	
		
// generated
// generated
	public static Point2d operator$sub (Point2d a, Tuple2f b)
	{
		return new Point2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2d operator$sub (Tuple2d a, Point2f b)
	{
		return new Point2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2d operator$sub (Point2d a, Point2f b)
	{
		return new Vector2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2d operator$sub (Vector2d a, Vector2f b)
	{
		return new Vector2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2d operator$subAssign (Point2d a, Tuple2f b)
	{
		a.set (a.x - b.x, a.y - b.y);
		return a;
	}
// generated
// generated
	public static Vector2d operator$subAssign (Vector2d a, Vector2f b)
	{
		a.set (a.x - b.x, a.y - b.y);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point2d operator$sub (Point2d a, Tuple2d b)
	{
		return new Point2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2d operator$sub (Tuple2d a, Point2d b)
	{
		return new Point2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2d operator$sub (Point2d a, Point2d b)
	{
		return new Vector2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Vector2d operator$sub (Vector2d a, Vector2d b)
	{
		return new Vector2d (a.x - b.x, a.y - b.y);
	}
// generated
// generated
	public static Point2d operator$subAssign (Point2d a, Tuple2d b)
	{
		a.set (a.x - b.x, a.y - b.y);
		return a;
	}
// generated
// generated
	public static Vector2d operator$subAssign (Vector2d a, Vector2d b)
	{
		a.set (a.x - b.x, a.y - b.y);
		return a;
	}
// generated
// generated
	
  
// generated
    
	
	public static Vector3f operator$mul (Tuple3f a, float b)
	{
		Vector3f r = new Vector3f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point3f operator$mul (Point3f a, float b)
	{
		Point3f r = new Point3f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector3f operator$div (Tuple3f a, float b)
	{
		Vector3f r = new Vector3f ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Point3f operator$div (Point3f a, float b)
	{
		Point3f r = new Point3f ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Vector3f operator$mul (float b, Tuple3f a)
	{
		Vector3f r = new Vector3f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point3f operator$mul (float b, Point3f a)
	{
		Point3f r = new Point3f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector3f operator$neg (Tuple3f a)
	{
		Vector3f r = new Vector3f ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Point3f operator$neg (Point3f a)
	{
		Point3f r = new Point3f ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Vector3f operator$mulAssign (Vector3f a, float b)
	{
		a.scale (b);
		return a;
	}
// generated
// generated
	public static Point3f operator$mulAssign (Point3f a, float b)
	{
		a.scale (b);
		return a;
	}
// generated
    
	
	public static Vector3d operator$mul (Tuple3d a, double b)
	{
		Vector3d r = new Vector3d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point3d operator$mul (Point3d a, double b)
	{
		Point3d r = new Point3d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector3d operator$div (Tuple3d a, double b)
	{
		Vector3d r = new Vector3d ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Point3d operator$div (Point3d a, double b)
	{
		Point3d r = new Point3d ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Vector3d operator$mul (double b, Tuple3d a)
	{
		Vector3d r = new Vector3d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point3d operator$mul (double b, Point3d a)
	{
		Point3d r = new Point3d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector3d operator$neg (Tuple3d a)
	{
		Vector3d r = new Vector3d ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Point3d operator$neg (Point3d a)
	{
		Point3d r = new Point3d ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Vector3d operator$mulAssign (Vector3d a, double b)
	{
		a.scale (b);
		return a;
	}
// generated
// generated
	public static Point3d operator$mulAssign (Point3d a, double b)
	{
		a.scale (b);
		return a;
	}
// generated
  
    
  	  	
					 
// generated
	
		
// generated
// generated
	public static Point3f operator$add (Point3f a, Tuple3f b)
	{
		return new Point3f (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3f operator$add (Tuple3f a, Point3f b)
	{
		return new Point3f (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3f operator$add (Point3f a, Point3f b)
	{
		return new Point3f (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Vector3f operator$add (Vector3f a, Vector3f b)
	{
		return new Vector3f (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3f operator$addAssign (Point3f a, Tuple3f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z);
		return a;
	}
// generated
// generated
	public static Vector3f operator$addAssign (Vector3f a, Vector3f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point3d operator$add (Point3f a, Tuple3d b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$add (Tuple3f a, Point3d b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$add (Point3f a, Point3d b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Vector3d operator$add (Vector3f a, Vector3d b)
	{
		return new Vector3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	
		
// generated
// generated
	public static Point3d operator$add (Point3d a, Tuple3f b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$add (Tuple3d a, Point3f b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$add (Point3d a, Point3f b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Vector3d operator$add (Vector3d a, Vector3f b)
	{
		return new Vector3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$addAssign (Point3d a, Tuple3f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z);
		return a;
	}
// generated
// generated
	public static Vector3d operator$addAssign (Vector3d a, Vector3f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point3d operator$add (Point3d a, Tuple3d b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$add (Tuple3d a, Point3d b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$add (Point3d a, Point3d b)
	{
		return new Point3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Vector3d operator$add (Vector3d a, Vector3d b)
	{
		return new Vector3d (a.x + b.x, a.y + b.y, a.z + b.z);
	}
// generated
// generated
	public static Point3d operator$addAssign (Point3d a, Tuple3d b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z);
		return a;
	}
// generated
// generated
	public static Vector3d operator$addAssign (Vector3d a, Vector3d b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z);
		return a;
	}
// generated
// generated
	
    
  	  	
					 
// generated
	
		
// generated
// generated
	public static Point3f operator$sub (Point3f a, Tuple3f b)
	{
		return new Point3f (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3f operator$sub (Tuple3f a, Point3f b)
	{
		return new Point3f (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3f operator$sub (Point3f a, Point3f b)
	{
		return new Vector3f (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3f operator$sub (Vector3f a, Vector3f b)
	{
		return new Vector3f (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3f operator$subAssign (Point3f a, Tuple3f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z);
		return a;
	}
// generated
// generated
	public static Vector3f operator$subAssign (Vector3f a, Vector3f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point3d operator$sub (Point3f a, Tuple3d b)
	{
		return new Point3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3d operator$sub (Tuple3f a, Point3d b)
	{
		return new Point3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3d operator$sub (Point3f a, Point3d b)
	{
		return new Vector3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3d operator$sub (Vector3f a, Vector3d b)
	{
		return new Vector3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	
		
// generated
// generated
	public static Point3d operator$sub (Point3d a, Tuple3f b)
	{
		return new Point3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3d operator$sub (Tuple3d a, Point3f b)
	{
		return new Point3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3d operator$sub (Point3d a, Point3f b)
	{
		return new Vector3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3d operator$sub (Vector3d a, Vector3f b)
	{
		return new Vector3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3d operator$subAssign (Point3d a, Tuple3f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z);
		return a;
	}
// generated
// generated
	public static Vector3d operator$subAssign (Vector3d a, Vector3f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point3d operator$sub (Point3d a, Tuple3d b)
	{
		return new Point3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3d operator$sub (Tuple3d a, Point3d b)
	{
		return new Point3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3d operator$sub (Point3d a, Point3d b)
	{
		return new Vector3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Vector3d operator$sub (Vector3d a, Vector3d b)
	{
		return new Vector3d (a.x - b.x, a.y - b.y, a.z - b.z);
	}
// generated
// generated
	public static Point3d operator$subAssign (Point3d a, Tuple3d b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z);
		return a;
	}
// generated
// generated
	public static Vector3d operator$subAssign (Vector3d a, Vector3d b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z);
		return a;
	}
// generated
// generated
	
  
// generated
    
	
	public static Vector4f operator$mul (Tuple4f a, float b)
	{
		Vector4f r = new Vector4f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point4f operator$mul (Point4f a, float b)
	{
		Point4f r = new Point4f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector4f operator$div (Tuple4f a, float b)
	{
		Vector4f r = new Vector4f ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Point4f operator$div (Point4f a, float b)
	{
		Point4f r = new Point4f ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Vector4f operator$mul (float b, Tuple4f a)
	{
		Vector4f r = new Vector4f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point4f operator$mul (float b, Point4f a)
	{
		Point4f r = new Point4f ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector4f operator$neg (Tuple4f a)
	{
		Vector4f r = new Vector4f ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Point4f operator$neg (Point4f a)
	{
		Point4f r = new Point4f ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Vector4f operator$mulAssign (Vector4f a, float b)
	{
		a.scale (b);
		return a;
	}
// generated
// generated
	public static Point4f operator$mulAssign (Point4f a, float b)
	{
		a.scale (b);
		return a;
	}
// generated
    
	
	public static Vector4d operator$mul (Tuple4d a, double b)
	{
		Vector4d r = new Vector4d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point4d operator$mul (Point4d a, double b)
	{
		Point4d r = new Point4d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector4d operator$div (Tuple4d a, double b)
	{
		Vector4d r = new Vector4d ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Point4d operator$div (Point4d a, double b)
	{
		Point4d r = new Point4d ();
		r.scale (1 / b, a);
		return r;
	}
// generated
// generated
	public static Vector4d operator$mul (double b, Tuple4d a)
	{
		Vector4d r = new Vector4d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Point4d operator$mul (double b, Point4d a)
	{
		Point4d r = new Point4d ();
		r.scale (b, a);
		return r;
	}
// generated
// generated
	public static Vector4d operator$neg (Tuple4d a)
	{
		Vector4d r = new Vector4d ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Point4d operator$neg (Point4d a)
	{
		Point4d r = new Point4d ();
		r.negate (a);
		return r;
	}
// generated
// generated
	public static Vector4d operator$mulAssign (Vector4d a, double b)
	{
		a.scale (b);
		return a;
	}
// generated
// generated
	public static Point4d operator$mulAssign (Point4d a, double b)
	{
		a.scale (b);
		return a;
	}
// generated
  
    
  	  	
						 
// generated
	
		
// generated
// generated
	public static Point4f operator$add (Point4f a, Tuple4f b)
	{
		return new Point4f (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4f operator$add (Tuple4f a, Point4f b)
	{
		return new Point4f (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4f operator$add (Point4f a, Point4f b)
	{
		return new Point4f (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Vector4f operator$add (Vector4f a, Vector4f b)
	{
		return new Vector4f (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4f operator$addAssign (Point4f a, Tuple4f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
		return a;
	}
// generated
// generated
	public static Vector4f operator$addAssign (Vector4f a, Vector4f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point4d operator$add (Point4f a, Tuple4d b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$add (Tuple4f a, Point4d b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$add (Point4f a, Point4d b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Vector4d operator$add (Vector4f a, Vector4d b)
	{
		return new Vector4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	
		
// generated
// generated
	public static Point4d operator$add (Point4d a, Tuple4f b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$add (Tuple4d a, Point4f b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$add (Point4d a, Point4f b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Vector4d operator$add (Vector4d a, Vector4f b)
	{
		return new Vector4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$addAssign (Point4d a, Tuple4f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
		return a;
	}
// generated
// generated
	public static Vector4d operator$addAssign (Vector4d a, Vector4f b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point4d operator$add (Point4d a, Tuple4d b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$add (Tuple4d a, Point4d b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$add (Point4d a, Point4d b)
	{
		return new Point4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Vector4d operator$add (Vector4d a, Vector4d b)
	{
		return new Vector4d (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
// generated
// generated
	public static Point4d operator$addAssign (Point4d a, Tuple4d b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
		return a;
	}
// generated
// generated
	public static Vector4d operator$addAssign (Vector4d a, Vector4d b)
	{
		a.set (a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
		return a;
	}
// generated
// generated
	
    
  	  	
						 
// generated
	
		
// generated
// generated
	public static Point4f operator$sub (Point4f a, Tuple4f b)
	{
		return new Point4f (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4f operator$sub (Tuple4f a, Point4f b)
	{
		return new Point4f (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4f operator$sub (Point4f a, Point4f b)
	{
		return new Vector4f (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4f operator$sub (Vector4f a, Vector4f b)
	{
		return new Vector4f (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4f operator$subAssign (Point4f a, Tuple4f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
		return a;
	}
// generated
// generated
	public static Vector4f operator$subAssign (Vector4f a, Vector4f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point4d operator$sub (Point4f a, Tuple4d b)
	{
		return new Point4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4d operator$sub (Tuple4f a, Point4d b)
	{
		return new Point4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4d operator$sub (Point4f a, Point4d b)
	{
		return new Vector4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4d operator$sub (Vector4f a, Vector4d b)
	{
		return new Vector4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	
		
// generated
// generated
	public static Point4d operator$sub (Point4d a, Tuple4f b)
	{
		return new Point4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4d operator$sub (Tuple4d a, Point4f b)
	{
		return new Point4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4d operator$sub (Point4d a, Point4f b)
	{
		return new Vector4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4d operator$sub (Vector4d a, Vector4f b)
	{
		return new Vector4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4d operator$subAssign (Point4d a, Tuple4f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
		return a;
	}
// generated
// generated
	public static Vector4d operator$subAssign (Vector4d a, Vector4f b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
		return a;
	}
// generated
// generated
	
		
// generated
// generated
	public static Point4d operator$sub (Point4d a, Tuple4d b)
	{
		return new Point4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4d operator$sub (Tuple4d a, Point4d b)
	{
		return new Point4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4d operator$sub (Point4d a, Point4d b)
	{
		return new Vector4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Vector4d operator$sub (Vector4d a, Vector4d b)
	{
		return new Vector4d (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}
// generated
// generated
	public static Point4d operator$subAssign (Point4d a, Tuple4d b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
		return a;
	}
// generated
// generated
	public static Vector4d operator$subAssign (Vector4d a, Vector4d b)
	{
		a.set (a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
		return a;
	}
// generated
// generated
	
  
//!! *# End of generated code

}
