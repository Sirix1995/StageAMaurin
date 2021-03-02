
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

package de.grogra.xl.vmx;

import de.grogra.reflect.*;

/**
 * Base class for abrupt completions. An abrupt completion is one
 * of the following:
 * <ul>
 * <li>
 *   A {@link Break} with a label.
 * <li>
 *   A {@link Return} with a value.
 * <li>
 *   A {@link Throw} with a throwable as its cause.
 * </ul>
 * Abrupt completions are <code>RuntimeException</code>s that have to be
 * caught at suitable locations.
 * <p> 
 * The class {@link Nonlocal} wraps an abrupt completion that has to be
 * transferred (non-locally) to a statically containing routine invocation
 * before Java's catching mechanism comes into play.
 * 
 * @author Ole Kniemeyer
 */
public abstract class AbruptCompletion extends RuntimeException
{
	final VMXState vmx;


	AbruptCompletion (VMXState vmx)
	{
		this.vmx = vmx;
	}

	
	public abstract int getLabel ();
	
	
	public abstract void dispose ();


	/**
	 * An abrupt completion due to a <code>return</code>. The returned value
	 * is wrapped.
	 * 
	 * @author Ole Kniemeyer
	 */
	public static final class Return extends AbruptCompletion
	{
		public static final int LABEL = -1;

		int etype;

		int ival;
		long lval;
		float fval;
		double dval;
		Object aval;


		Return (VMXState vmx)
		{
			super (vmx);
		}
		
		
		@Override
		public int getLabel ()
		{
			return LABEL;
		}


		/**
		 * This method can be used to recycle this instance. If this
		 * instance is not needed any more, this method can be invoked
		 * in order to inform the VMXState that it may re-use this instance.
		 */
		@Override
		public void dispose ()
		{
			aval = null;
			vmx.dispose (this);
		}


		/**
		 * Returns the {@link TypeId} of the wrapped value.
		 * 
		 * @return the {@link TypeId} of the wrapped value
		 */
		public int getTypeId ()
		{
			return etype;
		}

/*!!

#foreach ($type in ["int", "long", "float", "double", "Object", "void"])
$pp.setType($type)


		/**
#if ($pp.int)
		 * Returns the wrapped value as an <code>int</code>. This may
		 * only be invoked if the wrapped value is of type
		 * <code>boolean</code>, <code>byte</code>, <code>short</code>,
		 * <code>char</code>, or <code>int</code>. 
#else
		 * Returns the wrapped <code>$type</code> value. This may
		 * only be invoked if the wrapped value is of type <code>$type</code>.
#end
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
#if (!$pp.void)
		 * 
		 * @return the wrapped value
#end
		 $C
		public $type
#if ($pp.void)
			vget ()
#else
			${pp.prefix}get ()
#end
		{
#if ($pp.int)
			if (((1 << etype) & TypeId.I_VALUE) == 0)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected I_VALUE");
			}
#else
			if (etype != TypeId.${pp.TYPE})
			{
				throw new IllegalStateException
					("type = " + etype + ", expected ${pp.TYPE}");
			}
#end
#if ($pp.Object)
			Object o = aval;
			dispose ();
			return o;
#elseif ($pp.void)
			dispose ();
#else
			dispose ();
			return ${pp.prefix}val;
#end
		}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
// generated
// generated
		/**
		 * Returns the wrapped value as an <code>int</code>. This may
		 * only be invoked if the wrapped value is of type
		 * <code>boolean</code>, <code>byte</code>, <code>short</code>,
		 * <code>char</code>, or <code>int</code>. 
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
		 * 
		 * @return the wrapped value
		 */
		public int
			iget ()
		{
			if (((1 << etype) & TypeId.I_VALUE) == 0)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected I_VALUE");
			}
			dispose ();
			return ival;
		}
// generated
// generated
// generated
// generated
		/**
		 * Returns the wrapped <code>long</code> value. This may
		 * only be invoked if the wrapped value is of type <code>long</code>.
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
		 * 
		 * @return the wrapped value
		 */
		public long
			lget ()
		{
			if (etype != TypeId.LONG)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected LONG");
			}
			dispose ();
			return lval;
		}
// generated
// generated
// generated
// generated
		/**
		 * Returns the wrapped <code>float</code> value. This may
		 * only be invoked if the wrapped value is of type <code>float</code>.
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
		 * 
		 * @return the wrapped value
		 */
		public float
			fget ()
		{
			if (etype != TypeId.FLOAT)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected FLOAT");
			}
			dispose ();
			return fval;
		}
// generated
// generated
// generated
// generated
		/**
		 * Returns the wrapped <code>double</code> value. This may
		 * only be invoked if the wrapped value is of type <code>double</code>.
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
		 * 
		 * @return the wrapped value
		 */
		public double
			dget ()
		{
			if (etype != TypeId.DOUBLE)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected DOUBLE");
			}
			dispose ();
			return dval;
		}
// generated
// generated
// generated
// generated
		/**
		 * Returns the wrapped <code>Object</code> value. This may
		 * only be invoked if the wrapped value is of type <code>Object</code>.
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
		 * 
		 * @return the wrapped value
		 */
		public Object
			aget ()
		{
			if (etype != TypeId.OBJECT)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected OBJECT");
			}
			Object o = aval;
			dispose ();
			return o;
		}
// generated
// generated
// generated
// generated
		/**
		 * Returns the wrapped <code>void</code> value. This may
		 * only be invoked if the wrapped value is of type <code>void</code>.
		 * In addition, this instance is disposed by invocation of
		 * {@link #dispose()}.
		 */
		public void
			vget ()
		{
			if (etype != TypeId.VOID)
			{
				throw new IllegalStateException
					("type = " + etype + ", expected VOID");
			}
			dispose ();
		}
// generated
//!! *# End of generated code

	}


	/**
	 * An abrupt completion due to a <code>break</code> or
	 * <code>continue</code>.
	 * 
	 * @author Ole Kniemeyer
	 */
	public static final class Break extends AbruptCompletion
	{
		int label;


		Break (VMXState vmx)
		{
			super (vmx);
		}


		/**
		 * This method can be used to recycle this instance. If this
		 * instance is not needed any more, this method can be invoked
		 * in order to inform the VMXState that it may re-use this instance.
		 */
		@Override
		public void dispose ()
		{
			vmx.dispose (this);
		}


		@Override
		public int getLabel ()
		{
			return label;
		}

	}


	/**
	 * An abrupt completion due to a thrown <code>Throwable</code>.
	 * 
	 * @author Ole Kniemeyer
	 */
	public static final class Throw extends AbruptCompletion
	{
		public static final int LABEL = -2;

		Throwable cause;


		Throw (VMXState vmx)
		{
			super (vmx);
			initCause (null);
		}

		
		@Override
		public int getLabel ()
		{
			return LABEL;
		}


		/**
		 * This method can be used to recycle this instance. If this
		 * instance is not needed any more, this method can be invoked
		 * in order to inform the VMXState that it may re-use this instance.
		 */
		@Override
		public void dispose ()
		{
			cause = null;
			vmx.dispose (this);
		}


		/**
		 * Returns the throwable that caused this abrupt completion.
		 * 
		 * @return the throwable that caused this abrupt completion
		 */
		@Override
		public Throwable getCause ()
		{
			return cause;
		}


		/**
		 * Returns the throwable that caused this abrupt completion.
		 * In addition, this instance
		 * is disposed by invocation of {@link #dispose()}.
		 * 
		 * @return the throwable that caused this abrupt completion
		 */
		public Throwable getCauseAndDispose ()
		{
			Throwable c = cause;
			cause = null;
			vmx.dispose (this);
			return c;
		}
	}


	/**
	 * This class wraps an abrupt completion that has to be
	 * transferred (non-locally) to a statically containing routine invocation
	 * before Java's catching mechanism comes into play. It extends
	 * <code>Error</code> so that it is not catched and discarded by
	 * well-behaved Java programs (which would lead to improper behaviour
	 * of the non-local transfer mechanism).
	 * 
	 * @author Ole Kniemeyer
	 */
	public static final class Nonlocal extends Error
	{
		final VMXState vmx;

		VMXState.VMXFrame frame;

		AbruptCompletion reason;


		Nonlocal (VMXState vmx)
		{
			this.vmx = vmx;
		}


		/**
		 * Returns the wrapped abrupt completion if the current frame of the
		 * <code>VMXState</code> corresponds to the frame that has been specified
		 * in {@link VMXState#newNonlocal(int, AbruptCompletion, Authorization)}.
		 * In this case, this instance is disposed and may be re-used
		 * by the <code>VMXState</code>.
		 * Otherwise, this instance is re-thrown in order to continue
		 * the non-local transfer of the reason.
		 * 
		 * @return the wrapped abrupt completion
		 */
		public AbruptCompletion getReason (Authorization auth)
		{
			if (vmx.isCurrentFrame (frame))
			{
				vmx.check (auth);
				AbruptCompletion r = reason;
				reason = null;
				vmx.dispose (this);
				return r;
			}
			else
			{
				throw this;
			}
		}

	}

}
