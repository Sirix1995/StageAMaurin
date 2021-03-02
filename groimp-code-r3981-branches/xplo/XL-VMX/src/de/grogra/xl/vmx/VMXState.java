
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

import java.util.EmptyStackException;

import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.TypeId;
import de.grogra.xl.query.BytecodeSerialization;
import de.grogra.xl.query.Frame;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.query.Utils;
import de.grogra.xl.query.Variable;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>VMXState</code> is associated with a single
 * {@link java.lang.Thread}. It provides a stack (consisting of frames) and some other features to be used
 * exclusively within the context of this thread.
 * <p>
 * A <code>VMXState</code> can be seen as an extension of the per-thread state
 * that a Java virtual machine maintains. This per-thread state contains
 * a stack. However, this stack can only be accessed by instructions
 * for the Java virtual machine, not by method invocations, and it
 * is a purely local stack without the possibility to access stack frames
 * of other method invocations than the current one.
 * <p>
 * Some features of the XL programming language, e.g., invocations
 * of generator methods, require nested routines and a stack with
 * support for access of stack frame of statically containing routine
 * invocations. The stack
 * of a <code>VMXState</code> provides such support; implementations of
 * these features of the XL programming language can make use of the
 * stack of the current thread's <code>VMXState</code>.
 * <p>
 * A <code>VMXState</code> has a stack pointer
 * {@link #sp}, a "Java frame pointer" {@link #jfp}, and
 * a frame for the current routine invocation {@link #getFrame(Authorization)}.
 * Each frame has a {@link VMXFrame#staticLink} to the frame
 * of statically containing routine invocation.
 * The stack frame of the statically containing routine invocation
 * is the stack frame belonging to the nearest invocation of the
 * containing (i.e., the textually enclosing) routine of the current routine.
 * As for the Java virtual machine, values of the types <code>long</code>
 * and <code>double</code> occupy two consecutive elements of the stack,
 * values of the other types occupy a single element.
 * 
 * @author Ole Kniemeyer
 */
public final class VMXState
{
	public static final int[] INT_0 = new int[0];

	private static final ThreadLocal STATE = new ThreadLocal ();

	private final Thread thread;

	static final class VMXFrame implements Frame
	{
		final VMXState vmx;		//reference to call stack
		int fp;					//frame pointer
		int size;				//frame size
		int jfp; 				//java frame pointer - starting point of java variables
		int jsize;				//java frame size - number of pointers used by java frame
		int minSp;				//first unused pointer in this frame
		int spSave;				//pointer for restoration after returning from sub-routine
		VMXFrame staticLink;	//reference to immediate caller frame
		VMXFrame parent;		//reference to static enclosing caller
		Authorization auth;		//authorization

		VMXFrame (VMXState vmx)
		{
			this.vmx = vmx;
		}
	}

	/**
	 * This class represents a local variable. Local variables exist
	 * with respect to the Java frame pointer {@link VMXState#jfp} 
	 * or with respect to the normal frame pointer of the current
	 * ({@link VMXState#getFrame(Authorization)}) or
	 * of statically containing routine invocations
	 * ({@link VMXFrame#staticLink}).
	 * 
	 * @author Ole Kniemeyer
	 */
	public final static class Local implements BytecodeSerialization.Serializable, Variable
	{
		private static final Object UNSET = new Object () {};

		/**
		 * This value is used for {@link #nesting} to indicate a
		 * local variable in the Java frame.
		 */
		public static final int JAVA = -1;


		/**
		 * The local is on the stack frame of the <code>nesting</code>-th
		 * statically containing routine invocation. {@link #JAVA} is used
		 * to indicate a local variable on the Java frame.
		 */
		final int nesting;


		/**
		 * The index of the local variable within its stack frame.
		 */
		final int index;


		public Local (int nesting, int index)
		{
			this.nesting = nesting;
			this.index = index;
			if (index < 0)
			{
				throw new IllegalArgumentException ("index = " + index);
			}
		}


		@Override
		public boolean equals (Object o)
		{
			return (o instanceof Local) && (((Local) o).index == index)
				&& (((Local) o).nesting == nesting);
		}


		@Override
		public int hashCode ()
		{
			return index + nesting * 31;
		}
		
		
		public int getIndex ()
		{
			return index;
		}
		
		
		public int getNesting ()
		{
			return nesting;
		}
		
		
		public boolean isJavaLocal ()
		{
			return nesting == JAVA;
		}


		@Override
		public String toString ()
		{
			switch (nesting)
			{
				case JAVA:
					return "JLocal[" + index + ']';
				default:
					return "Local[" + nesting + ':' + index + ']';
			}
		}


		public void write (BytecodeSerialization out) throws java.io.IOException
		{
			out.beginMethod (Utils.getConstructor (this));
			out.visitInt (nesting);
			out.visitInt (index);
			out.endMethod ();
		}

		public void unset (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			vmx.aseta (vmx.getPointer ((VMXFrame) frame, this), UNSET);
		}

		public void nullset (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.lseta (p, 0);
			vmx.dseta (p, Double.NaN);
			vmx.aseta (p, null);
		}

		public boolean isSet (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return vmx.ageta (vmx.getPointer ((VMXFrame) frame, this)) != UNSET;
		}

		public boolean isNull (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			return (vmx.ageta (p) == null) && (vmx.igeta (p) == 0);
		}

/*!!

#foreach ($type in $vmtypes)
$pp.setType($type)

		public $type ${pp.prefix}get (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return ($type) vmx.${pp.prefix}geta (vmx.getPointer ((VMXFrame) frame, this));
		}

		public void ${pp.prefix}set (Frame frame, $type value)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.${pp.prefix}seta (p, value);
#if ($pp.Object)
			vmx.iseta (p, 1);
#else
			vmx.aseta (p, this);
#end
		}

#end

!!*/
//!! #* Start of generated code
// generated
// generated
// generated
		public int iget (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return (int) vmx.igeta (vmx.getPointer ((VMXFrame) frame, this));
		}
// generated
		public void iset (Frame frame, int value)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.iseta (p, value);
			vmx.aseta (p, this);
		}
// generated
// generated
// generated
		public long lget (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return (long) vmx.lgeta (vmx.getPointer ((VMXFrame) frame, this));
		}
// generated
		public void lset (Frame frame, long value)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.lseta (p, value);
			vmx.aseta (p, this);
		}
// generated
// generated
// generated
		public float fget (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return (float) vmx.fgeta (vmx.getPointer ((VMXFrame) frame, this));
		}
// generated
		public void fset (Frame frame, float value)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.fseta (p, value);
			vmx.aseta (p, this);
		}
// generated
// generated
// generated
		public double dget (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return (double) vmx.dgeta (vmx.getPointer ((VMXFrame) frame, this));
		}
// generated
		public void dset (Frame frame, double value)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.dseta (p, value);
			vmx.aseta (p, this);
		}
// generated
// generated
// generated
		public Object aget (Frame frame)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			return (Object) vmx.ageta (vmx.getPointer ((VMXFrame) frame, this));
		}
// generated
		public void aset (Frame frame, Object value)
		{
			VMXState vmx = ((VMXFrame) frame).vmx;
			int p = vmx.getPointer ((VMXFrame) frame, this);
			vmx.aseta (p, value);
			vmx.iseta (p, 1);
		}
// generated
// generated
//!! *# End of generated code

	}


	private final int id;
	private static int nextId = 0;


	private static final int INITIAL_SIZE = 64;

	/**
	 * istack is used for <code>boolean</code> and integral values.
	 * <code>long</code>-values are stored in two consecutive components.
	 */
	private int[] istack = new int[INITIAL_SIZE];

	/**
	 * dstack is used for floating point values (<code>float</code>
	 * and <code>double</code>).
	 */
	private double[] dstack = new double[INITIAL_SIZE];
	
	/**
	 * astack is used for objects. 
	 */
	private Object[] astack = new Object[INITIAL_SIZE];

	/**
	 * This is the stack pointer. It points to the first unused
	 * stack element (for <code>istack</code>, <code>dstack</code>,
	 * as well as for <code>astack</code>). 
	 */
	private int sp = 0;

	private int minSp = 0;

	/**
	 * This is the Java frame pointer.
	 */
	private int jfp = Integer.MIN_VALUE;

	private int jsize = 0;

	/**
	 * This is the current frame pointer. It is the same as
	 * <code>staticLinkStack[ssp+1]</code>.
	 */
	private int fp = Integer.MIN_VALUE;

	private int fsize = 0;

	/**
	 * This is the static-link stack. A stack element is composed
	 * of two consecutive <code>int</code>-values:
	 * <code>staticLinkStack[i]</code> represents the index into
	 * <code>staticLinkStack</code> which addresses the element
	 * corresponding to the stack frame of the statically containing
	 * routine invocation, 
	 * <code>staticLinkStack[i+1]</code> represents the value of the
	 * frame pointer for element <code>i</code>.
	 */
//	private int[] staticLinkStack = new int[64];

	private ObjectList<VMXFrame> framePool = new ObjectList<VMXFrame> ();

	/**
	 * This is the stack pointer for the static-link stack.
	 * It points to the last valid stack element. Thus,
	 * <code>staticLinkStack[ssp+1]</code> equals <code>fp</code>,
	 * the frame pointer of the current stack frame.
	 */
//	private int fsp = 0;

	private Authorization currentAuthorization = null;
	private VMXFrame currentFrame;

	/**
	 * The header of a linked list of <code>RoutineDescriptor</code>
	 * instances which are available for re-use.
	 */
	RoutineDescriptor descriptorPool = null;


	private VMXState ()
	{
		synchronized (STATE)
		{
			id = nextId++;
		}
		thread = Thread.currentThread ();
		pushFrame (null);
	}

	private static void checkIndex (int index, int size)
	{
		if ((index < 0) || (index >= size))
		{
			throw new IndexOutOfBoundsException ();
		}
	}
	
	static void check (Authorization a, Authorization b)
	{
		if (a != b)
		{
			throw new SecurityException ("Authorization " + a + " does not match " + b);
		}
	}

	void check (Authorization auth)
	{
		if (auth != currentAuthorization)
		{
			throw new SecurityException ("Authorization " + auth + " does not match " + currentAuthorization);
		}
	}

	private void pushFrame (VMXFrame staticLink)
	{
		VMXFrame f = framePool.isEmpty () ? new VMXFrame (this) : framePool.pop ();
		f.fp = fp;
		f.size = fsize;
		f.jfp = jfp;
		f.jsize = jsize;
		f.auth = currentAuthorization;
		f.staticLink = staticLink;
		f.parent = currentFrame;
		f.minSp = minSp;
		currentFrame = f;
	}
	
	private void popFrame ()
	{
		VMXFrame f = currentFrame.parent;
		currentFrame.parent = null;
		framePool.push (currentFrame);
		loadFrame (f);
	}
	
	private void loadFrame (VMXFrame f)
	{
		currentFrame = f;
		fp = f.fp;
		fsize = f.size;
		jfp = f.jfp;
		jsize = f.jsize;
		minSp = f.minSp;
		currentAuthorization = f.auth;
	}

	boolean isCurrentFrame (Frame f)
	{
		return f == currentFrame;
	}

	public void enter (int frameSize, Authorization auth)
	{
		currentFrame.spSave = sp;
		currentAuthorization = auth;
		fp = sp;
		fsize = frameSize;
		jfp = sp;
		jsize = 0;
		setSp (fp + frameSize);
		minSp = sp;
		pushFrame (currentFrame);
	}


	public void leave (Authorization auth)
	{
		check (auth);
		popFrame ();
		setSp (currentFrame.spSave);
	}

	/**
	 * Returns a unique id for this VMXState. The id is unique within
	 * the scope of the current Java virtual machine.
	 * 
	 * @return a unique id
	 */
	public int getId ()
	{
		return id;
	}


	/**
	 * Returns the VMXState for the current thread.
	 * 
	 * @return the current VMXState
	 */
	public static VMXState current ()
	{
		VMXState t = (VMXState) STATE.get ();
		if (t == null)
		{
			t = new VMXState ();
			STATE.set (t);
		}
		return t;
	}


	/**
	 * Sets <code>sp</code> to <code>s</code>. The stack arrays are
	 * enlarged if necessary.
	 * 
	 * @param s the new stack pointer
	 */
	void setSp (int s)
	{
		int l;
/*!!

#foreach ($type in ["int", "double", "Object"])
$pp.setType($type)

		if (s > (l = ${pp.prefix}stack.length))
		{
			${pp.Type}List.arraycopy
				(${pp.prefix}stack, 0,
				 ${pp.prefix}stack = new $type[s * 3 >> 1], 0, l);
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
// generated
		if (s > (l = istack.length))
		{
			IntList.arraycopy
				(istack, 0,
				 istack = new int[s * 3 >> 1], 0, l);
		}
// generated
// generated
		if (s > (l = dstack.length))
		{
			DoubleList.arraycopy
				(dstack, 0,
				 dstack = new double[s * 3 >> 1], 0, l);
		}
// generated
// generated
		if (s > (l = astack.length))
		{
			ObjectList.arraycopy
				(astack, 0,
				 astack = new Object[s * 3 >> 1], 0, l);
		}
//!! *# End of generated code
		if (s < sp)
		{
			ObjectList.clear (astack, s, sp - s);
		}
		l = astack.length;
		if (l > sp)
		{
			l = sp;
		}
		while (l > s)
		{
			astack[--l] = null;
		}
		sp = s;
	}

	
	/**
	 * Returns the current stack pointer for the static-link stack.
	 * 
	 * @return current static-link stack pointer
	 */
	public Frame getFrame (Authorization auth)
	{
		check (auth);
		return currentFrame;
	}


	/**
	 * Creates a new stack frame and invokes the given <code>routine</code>.
	 * This method proceeds as follows:
	 * <ol>
	 * <li>
	 *   It is assumed that <code>routine.getParameterSize()</code>
	 *   stack elements have been pushed on the stack. These are used
	 *   as the parameters for the routine invocation.
	 * <li>
	 *   If <code>routine.hasJavaParameters()</code> returns
	 *   <code>true</code>, the Java frame pointer is set to point
	 *   to the stack element of the first parameter. The frame pointer
	 *   is set to point behind the Java frame (which has a total size
	 *   of <code>routine.getJavaFrameSize()</code>). The stack pointer
	 *   is set to point behind the frame (which has a total size
	 *   of <code>routine.getFrameSize()</code>). Otherwise, if
	 *   <code>routine.hasJavaParameters()</code> returns <code>false</code>,
	 *   the pointers are modified correspondingly, with the roles of the
	 *   frame and the Java frame exchanged.
	 * <li>
	 *   A new element is pushed on the static-link stack.
	 *   <code>nesting</code> is used in this step. Its value has to be
	 *   <ul>
	 *   <li>
	 *     0 if the invoking routine and the invoked <code>routine</code>
	 *     are statically contained in the same routine (i.e., their
	 *     immediately enclosing routine is the same),
	 *   <li>
	 *     <em>n</em> if the invoked <code>routine</code> is the <em>n</em>-th
	 *     statically containing routine of the invoking routine,
	 *   <li>
	 *     -1 otherwise.
	 *   </ul>
	 * <li>
	 *   The method <code>routine.execute</code> is invoked.
	 * <li>
	 *   All modifications to the stack pointers are undone, the routine
	 *   parameters are popped from the stack. This step happens even in the
	 *   case of an exception. 
	 * </ol>
	 *
	 * @param routine the routine to be invoked
	 * @param nesting the nesting of the invoking routine within the invoked routine
	 * @return the returned value of <code>routine.execute</code>
	 */
	public AbruptCompletion.Return invoke (Routine routine, int nesting, Authorization auth)
	{
		int s = sp - routine.getParameterSize ();
		currentAuthorization = auth;
		fsize = routine.getFrameSize ();
		jsize = routine.getJavaFrameSize ();
		if (routine.hasJavaParameters ())
		{
			jfp = s;
			fp = s + jsize;
			setSp (fp + fsize);
		}
		else
		{
			fp = s;
			jfp = s + fsize;
			setSp (jfp + jsize);
		}
		minSp = sp;
		VMXFrame f = currentFrame;
		while (--nesting >= -1)
		{
			f = currentFrame.staticLink;
		}
		pushFrame (f);
		try
		{
			return routine.execute (this);
		}
		finally
		{
			popFrame ();
			setSp (s);
		}
	}


	AbruptCompletion.Return invoke (RoutineDescriptor callback)
	{
		Routine routine = callback.routine;
		int s = sp - routine.getParameterSize ();
		currentAuthorization = callback.auth;
		fsize = routine.getFrameSize ();
		jsize = routine.getJavaFrameSize ();
		if (routine.hasJavaParameters ())
		{
			jfp = s;
			fp = s + jsize;
			setSp (fp + fsize);
		}
		else
		{
			fp = s;
			jfp = s + fsize;
			setSp (jfp + jsize);
		}
		minSp = sp;
		pushFrame (callback.staticLink);
		try
		{
			return routine.execute (this);
		}
		catch (AbruptCompletion.Throw e)
		{
			throw newNonlocal (callback.staticLink, e);
		}
		catch (Exception e)
		{
			throw newNonlocal (callback.staticLink, newThrow (e));
		}
		finally
		{
			popFrame ();
			setSp (s);
		}
	}


	/* *
	 * Returns the frame pointer. This method returns the frame pointer
	 * of the stack frame of the <code>nesting</code>-th statically
	 * containing routine invocation.
	 * 
	 * @return frame pointer
	 $ C
	public int getFp (int nesting)
	{
		if (nesting == 0)
		{
			return fp;
		}
		else
		{
			int f;
			#GET_FP("")
			return f;
		}
	}*/

/*!!

#macro (GET_FP $vmx $index)
		if (nesting == 0)
		{
			${vmx}check (auth);
			checkIndex ($index, ${vmx}fsize);
			f = ${vmx}fp + $index;
		}
		else
		{
			VMXFrame v = ${vmx}currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex ($index, v.size);
			f = v.fp + $index;
		}
#end

#foreach ($type in $vmtypes)
$pp.setType($type)

#if ($pp.float)
	#set ($stack = "dstack")
	#set ($stacktype = "double")
	#set ($cast = "(float)")
#else
	#set ($stack = "${pp.prefix}stack")
	#set ($stacktype = $type)
	#set ($cast = "")
#end

	/**
	 * Sets the stack element at absolute address <code>index</code> to
	 * <code>value</code>.
	 $C
	void ${pp.prefix}seta (int index, $type value)
	{
#if ($pp.long)
		istack[index] = (int) (value >> 32);
		istack[index + 1] = (int) value;
#else
		$stack[index] = value;
#end
	}


	/**
	 * Returns the value of the stack element at
	 * absolute address <code>index</code>.
	 * 
	 * @return stack element at <code>index</code>
	 $C
	$type ${pp.prefix}geta (int index)
	{
#if ($pp.long)
		return ((long) istack[index] << 32)
			+ (istack[index + 1] & 0xffffffffL);
#else
		return $cast $stack[index];
#end
	}


	/**
	 * Sets the stack element <code>index</code> of the
	 * current Java frame to <code>value</code>.
	 $C
	public void ${pp.prefix}setj (int index, $type value, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
#if ($pp.long)
		istack[index += jfp] = (int) (value >> 32);
		istack[index + 1] = (int) value;
#else
		$stack[jfp + index] = value;
#end
	}


	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the current Java frame.
	 * 
	 * @return stack element at <code>index</code>
	 $C
	public $type ${pp.prefix}getj (int index, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
#if ($pp.long)
		return ((long) istack[index += jfp] << 32)
			+ (istack[index + 1] & 0xffffffffL);
#else
		return $cast $stack[jfp + index];
#end
	}


	/**
	 * Sets the stack element <code>index</code> of the
	 * <code>nesting</code>-th statically containing frame to <code>value</code>.
	 $C
	public static void ${pp.prefix}set ($type value, VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
		#GET_FP("vmx." "index")
#if ($pp.long)
		vmx.istack[f] = (int) (value >> 32);
		vmx.istack[f + 1] = (int) value;
#else
		vmx.$stack[f] = value;
#end
	}


	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the <code>nesting</code>-th statically containing frame.
	 * 
	 * @return stack element at <code>index</code>
	 $C
	public static $type ${pp.prefix}get (VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
		#GET_FP("vmx." "index")
#if ($pp.long)
		return ((long) vmx.istack[f] << 32)
			+ (vmx.istack[f + 1] & 0xffffffffL);
#else
		return $cast vmx.$stack[f];
#end
	}


	/**
	 * Sets the stack element corresponding to <code>local</code>
	 * to <code>value</code>.
	 $C
	public void ${pp.prefix}set (Local local, $type value, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
				#GET_FP("" "local.index")
				break;
		}
#if ($pp.long)
		istack[f] = (int) (value >> 32);
		istack[f + 1] = (int) value;
#else
		$stack[f] = value;
#end
	}


	/**
	 * Returns the value of the stack element corresponding
	 * to <code>local</code>.
	 * 
	 * @return stack element for <code>local</code>
	 $C
	public $type ${pp.prefix}get (Local local, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
				#GET_FP("" "local.index")
				break;
		}
#if ($pp.long)
		return ((long) istack[f] << 32) + (istack[f + 1] & 0xffffffffL);
#else
		return $cast $stack[f];
#end
	}


	/**
	 * Pushs the given <code>value</code> on top of the stack.
	 $C
	public void ${pp.prefix}push ($type value)
	{
#if ($pp.long)
		setSp (sp + 2);
		istack[sp - 2] = (int) (value >> 32);
		istack[sp - 1] = (int) value;
#elseif ($pp.double)
		setSp (sp + 2);
		$stack[sp - 2] = value;
#else
		setSp (sp + 1);
		$stack[sp - 1] = value;
#end
	}


	/**
	 * Pops the topmost value from the stack.
	 * 
	 * @return the popped stack element
	 $C
	public $type ${pp.prefix}pop ()
	{
#if ($pp.long)
		int p = (sp -= 2);
		if (p < minSp)
		{
			throw new EmptyStackException ();
		}
		return ((long) istack[p] << 32) + (istack[p + 1] & 0xffffffffL);
#elseif ($pp.Object)
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		Object a = astack[--sp];
		astack[sp] = null;
		return a;
#elseif ($pp.double)
		if (sp - 1 <= minSp)
		{
			throw new EmptyStackException ();
		}
		return $stack[sp -= 2];
#else
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return $cast $stack[--sp];
#end
	}


	/**
	 * Returns the topmost value from the stack without popping if
	 * off the stack.
	 * 
	 * @return the topmost stack element
	 $C
	public $type ${pp.prefix}peek ()
	{
#if ($pp.long)
		if (sp - 1 <= minSp)
		{
			throw new EmptyStackException ();
		}
		return ((long) istack[sp - 2] << 32)
			+ (istack[sp - 1] & 0xffffffffL);
#elseif ($pp.double)
		if (sp - 1 <= minSp)
		{
			throw new EmptyStackException ();
		}
		return $stack[sp - 2];
#else
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return $cast $stack[sp - 1];
#end
	}


	/**
	 * Returns the <code>index</code>-th value from the top of the stack,
	 * i.e., the value with absolute address <code>sp - index</code>.
	 * 
	 * @return the <code>index</code>-th value from the top of the stack
	 $C
	public $type ${pp.prefix}peek (int index)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException ();
		}
		index = sp - index;
		if (index < minSp)
		{
			throw new EmptyStackException ();
		}
#if ($pp.long)
		return ((long) istack[index] << 32)
			+ (istack[index + 1] & 0xffffffffL);
#else
		return $cast $stack[index];
#end
	}
#end

!!*/
//!! #* Start of generated code
// generated
// generated
// generated
// generated
// generated
	/**
	 * Sets the stack element at absolute address <code>index</code> to
	 * <code>value</code>.
	 */
	void iseta (int index, int value)
	{
		istack[index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element at
	 * absolute address <code>index</code>.
	 * 
	 * @return stack element at <code>index</code>
	 */
	int igeta (int index)
	{
		return  istack[index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * current Java frame to <code>value</code>.
	 */
	public void isetj (int index, int value, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		istack[jfp + index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the current Java frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public int igetj (int index, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		return  istack[jfp + index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * <code>nesting</code>-th statically containing frame to <code>value</code>.
	 */
	public static void iset (int value, VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		vmx.istack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the <code>nesting</code>-th statically containing frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public static int iget (VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		return  vmx.istack[f];
	}
// generated
// generated
	/**
	 * Sets the stack element corresponding to <code>local</code>
	 * to <code>value</code>.
	 */
	public void iset (Local local, int value, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		istack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element corresponding
	 * to <code>local</code>.
	 * 
	 * @return stack element for <code>local</code>
	 */
	public int iget (Local local, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		return  istack[f];
	}
// generated
// generated
	/**
	 * Pushs the given <code>value</code> on top of the stack.
	 */
	public void ipush (int value)
	{
		setSp (sp + 1);
		istack[sp - 1] = value;
	}
// generated
// generated
	/**
	 * Pops the topmost value from the stack.
	 * 
	 * @return the popped stack element
	 */
	public int ipop ()
	{
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return  istack[--sp];
	}
// generated
// generated
	/**
	 * Returns the topmost value from the stack without popping if
	 * off the stack.
	 * 
	 * @return the topmost stack element
	 */
	public int ipeek ()
	{
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return  istack[sp - 1];
	}
// generated
// generated
	/**
	 * Returns the <code>index</code>-th value from the top of the stack,
	 * i.e., the value with absolute address <code>sp - index</code>.
	 * 
	 * @return the <code>index</code>-th value from the top of the stack
	 */
	public int ipeek (int index)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException ();
		}
		index = sp - index;
		if (index < minSp)
		{
			throw new EmptyStackException ();
		}
		return  istack[index];
	}
// generated
// generated
// generated
	/**
	 * Sets the stack element at absolute address <code>index</code> to
	 * <code>value</code>.
	 */
	void lseta (int index, long value)
	{
		istack[index] = (int) (value >> 32);
		istack[index + 1] = (int) value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element at
	 * absolute address <code>index</code>.
	 * 
	 * @return stack element at <code>index</code>
	 */
	long lgeta (int index)
	{
		return ((long) istack[index] << 32)
			+ (istack[index + 1] & 0xffffffffL);
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * current Java frame to <code>value</code>.
	 */
	public void lsetj (int index, long value, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		istack[index += jfp] = (int) (value >> 32);
		istack[index + 1] = (int) value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the current Java frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public long lgetj (int index, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		return ((long) istack[index += jfp] << 32)
			+ (istack[index + 1] & 0xffffffffL);
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * <code>nesting</code>-th statically containing frame to <code>value</code>.
	 */
	public static void lset (long value, VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		vmx.istack[f] = (int) (value >> 32);
		vmx.istack[f + 1] = (int) value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the <code>nesting</code>-th statically containing frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public static long lget (VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		return ((long) vmx.istack[f] << 32)
			+ (vmx.istack[f + 1] & 0xffffffffL);
	}
// generated
// generated
	/**
	 * Sets the stack element corresponding to <code>local</code>
	 * to <code>value</code>.
	 */
	public void lset (Local local, long value, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		istack[f] = (int) (value >> 32);
		istack[f + 1] = (int) value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element corresponding
	 * to <code>local</code>.
	 * 
	 * @return stack element for <code>local</code>
	 */
	public long lget (Local local, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		return ((long) istack[f] << 32) + (istack[f + 1] & 0xffffffffL);
	}
// generated
// generated
	/**
	 * Pushs the given <code>value</code> on top of the stack.
	 */
	public void lpush (long value)
	{
		setSp (sp + 2);
		istack[sp - 2] = (int) (value >> 32);
		istack[sp - 1] = (int) value;
	}
// generated
// generated
	/**
	 * Pops the topmost value from the stack.
	 * 
	 * @return the popped stack element
	 */
	public long lpop ()
	{
		int p = (sp -= 2);
		if (p < minSp)
		{
			throw new EmptyStackException ();
		}
		return ((long) istack[p] << 32) + (istack[p + 1] & 0xffffffffL);
	}
// generated
// generated
	/**
	 * Returns the topmost value from the stack without popping if
	 * off the stack.
	 * 
	 * @return the topmost stack element
	 */
	public long lpeek ()
	{
		if (sp - 1 <= minSp)
		{
			throw new EmptyStackException ();
		}
		return ((long) istack[sp - 2] << 32)
			+ (istack[sp - 1] & 0xffffffffL);
	}
// generated
// generated
	/**
	 * Returns the <code>index</code>-th value from the top of the stack,
	 * i.e., the value with absolute address <code>sp - index</code>.
	 * 
	 * @return the <code>index</code>-th value from the top of the stack
	 */
	public long lpeek (int index)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException ();
		}
		index = sp - index;
		if (index < minSp)
		{
			throw new EmptyStackException ();
		}
		return ((long) istack[index] << 32)
			+ (istack[index + 1] & 0xffffffffL);
	}
// generated
// generated
// generated
	/**
	 * Sets the stack element at absolute address <code>index</code> to
	 * <code>value</code>.
	 */
	void fseta (int index, float value)
	{
		dstack[index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element at
	 * absolute address <code>index</code>.
	 * 
	 * @return stack element at <code>index</code>
	 */
	float fgeta (int index)
	{
		return (float) dstack[index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * current Java frame to <code>value</code>.
	 */
	public void fsetj (int index, float value, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		dstack[jfp + index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the current Java frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public float fgetj (int index, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		return (float) dstack[jfp + index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * <code>nesting</code>-th statically containing frame to <code>value</code>.
	 */
	public static void fset (float value, VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		vmx.dstack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the <code>nesting</code>-th statically containing frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public static float fget (VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		return (float) vmx.dstack[f];
	}
// generated
// generated
	/**
	 * Sets the stack element corresponding to <code>local</code>
	 * to <code>value</code>.
	 */
	public void fset (Local local, float value, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		dstack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element corresponding
	 * to <code>local</code>.
	 * 
	 * @return stack element for <code>local</code>
	 */
	public float fget (Local local, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		return (float) dstack[f];
	}
// generated
// generated
	/**
	 * Pushs the given <code>value</code> on top of the stack.
	 */
	public void fpush (float value)
	{
		setSp (sp + 1);
		dstack[sp - 1] = value;
	}
// generated
// generated
	/**
	 * Pops the topmost value from the stack.
	 * 
	 * @return the popped stack element
	 */
	public float fpop ()
	{
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return (float) dstack[--sp];
	}
// generated
// generated
	/**
	 * Returns the topmost value from the stack without popping if
	 * off the stack.
	 * 
	 * @return the topmost stack element
	 */
	public float fpeek ()
	{
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return (float) dstack[sp - 1];
	}
// generated
// generated
	/**
	 * Returns the <code>index</code>-th value from the top of the stack,
	 * i.e., the value with absolute address <code>sp - index</code>.
	 * 
	 * @return the <code>index</code>-th value from the top of the stack
	 */
	public float fpeek (int index)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException ();
		}
		index = sp - index;
		if (index < minSp)
		{
			throw new EmptyStackException ();
		}
		return (float) dstack[index];
	}
// generated
// generated
// generated
	/**
	 * Sets the stack element at absolute address <code>index</code> to
	 * <code>value</code>.
	 */
	void dseta (int index, double value)
	{
		dstack[index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element at
	 * absolute address <code>index</code>.
	 * 
	 * @return stack element at <code>index</code>
	 */
	double dgeta (int index)
	{
		return  dstack[index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * current Java frame to <code>value</code>.
	 */
	public void dsetj (int index, double value, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		dstack[jfp + index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the current Java frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public double dgetj (int index, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		return  dstack[jfp + index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * <code>nesting</code>-th statically containing frame to <code>value</code>.
	 */
	public static void dset (double value, VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		vmx.dstack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the <code>nesting</code>-th statically containing frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public static double dget (VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		return  vmx.dstack[f];
	}
// generated
// generated
	/**
	 * Sets the stack element corresponding to <code>local</code>
	 * to <code>value</code>.
	 */
	public void dset (Local local, double value, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		dstack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element corresponding
	 * to <code>local</code>.
	 * 
	 * @return stack element for <code>local</code>
	 */
	public double dget (Local local, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		return  dstack[f];
	}
// generated
// generated
	/**
	 * Pushs the given <code>value</code> on top of the stack.
	 */
	public void dpush (double value)
	{
		setSp (sp + 2);
		dstack[sp - 2] = value;
	}
// generated
// generated
	/**
	 * Pops the topmost value from the stack.
	 * 
	 * @return the popped stack element
	 */
	public double dpop ()
	{
		if (sp - 1 <= minSp)
		{
			throw new EmptyStackException ();
		}
		return dstack[sp -= 2];
	}
// generated
// generated
	/**
	 * Returns the topmost value from the stack without popping if
	 * off the stack.
	 * 
	 * @return the topmost stack element
	 */
	public double dpeek ()
	{
		if (sp - 1 <= minSp)
		{
			throw new EmptyStackException ();
		}
		return dstack[sp - 2];
	}
// generated
// generated
	/**
	 * Returns the <code>index</code>-th value from the top of the stack,
	 * i.e., the value with absolute address <code>sp - index</code>.
	 * 
	 * @return the <code>index</code>-th value from the top of the stack
	 */
	public double dpeek (int index)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException ();
		}
		index = sp - index;
		if (index < minSp)
		{
			throw new EmptyStackException ();
		}
		return  dstack[index];
	}
// generated
// generated
// generated
	/**
	 * Sets the stack element at absolute address <code>index</code> to
	 * <code>value</code>.
	 */
	void aseta (int index, Object value)
	{
		astack[index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element at
	 * absolute address <code>index</code>.
	 * 
	 * @return stack element at <code>index</code>
	 */
	Object ageta (int index)
	{
		return  astack[index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * current Java frame to <code>value</code>.
	 */
	public void asetj (int index, Object value, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		astack[jfp + index] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the current Java frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public Object agetj (int index, Authorization auth)
	{
		check (auth);
		checkIndex (index, jsize);
		return  astack[jfp + index];
	}
// generated
// generated
	/**
	 * Sets the stack element <code>index</code> of the
	 * <code>nesting</code>-th statically containing frame to <code>value</code>.
	 */
	public static void aset (Object value, VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		vmx.astack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element <code>index</code>
	 * of the <code>nesting</code>-th statically containing frame.
	 * 
	 * @return stack element at <code>index</code>
	 */
	public static Object aget (VMXState vmx, int nesting, int index, Authorization auth)
	{
		int f;
				if (nesting == 0)
		{
			vmx.check (auth);
			checkIndex (index, vmx.fsize);
			f = vmx.fp + index;
		}
		else
		{
			VMXFrame v = vmx.currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (index, v.size);
			f = v.fp + index;
		}
		return  vmx.astack[f];
	}
// generated
// generated
	/**
	 * Sets the stack element corresponding to <code>local</code>
	 * to <code>value</code>.
	 */
	public void aset (Local local, Object value, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		astack[f] = value;
	}
// generated
// generated
	/**
	 * Returns the value of the stack element corresponding
	 * to <code>local</code>.
	 * 
	 * @return stack element for <code>local</code>
	 */
	public Object aget (Local local, Authorization auth)
	{
		int f;
		switch (local.nesting)
		{
			case Local.JAVA:
				check (auth);
				checkIndex (local.index, jsize);
				f = jfp + local.index;
				break;
			case 0:
				check (auth);
				checkIndex (local.index, fsize);
				f = fp + local.index;
				break;
			default:
				int nesting = local.nesting;
						if (nesting == 0)
		{
			check (auth);
			checkIndex (local.index, fsize);
			f = fp + local.index;
		}
		else
		{
			VMXFrame v = currentFrame;
			while (--nesting >= 0)
			{
				v = v.staticLink;
			}
			check (auth, v.auth);
			checkIndex (local.index, v.size);
			f = v.fp + local.index;
		}
				break;
		}
		return  astack[f];
	}
// generated
// generated
	/**
	 * Pushs the given <code>value</code> on top of the stack.
	 */
	public void apush (Object value)
	{
		setSp (sp + 1);
		astack[sp - 1] = value;
	}
// generated
// generated
	/**
	 * Pops the topmost value from the stack.
	 * 
	 * @return the popped stack element
	 */
	public Object apop ()
	{
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		Object a = astack[--sp];
		astack[sp] = null;
		return a;
	}
// generated
// generated
	/**
	 * Returns the topmost value from the stack without popping if
	 * off the stack.
	 * 
	 * @return the topmost stack element
	 */
	public Object apeek ()
	{
		if (sp <= minSp)
		{
			throw new EmptyStackException ();
		}
		return  astack[sp - 1];
	}
// generated
// generated
	/**
	 * Returns the <code>index</code>-th value from the top of the stack,
	 * i.e., the value with absolute address <code>sp - index</code>.
	 * 
	 * @return the <code>index</code>-th value from the top of the stack
	 */
	public Object apeek (int index)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException ();
		}
		index = sp - index;
		if (index < minSp)
		{
			throw new EmptyStackException ();
		}
		return  astack[index];
	}
// generated
//!! *# End of generated code

	
	/**
	 * Pops and discards <code>size</code> elements from the stack.
	 * 
	 * @param size the number of elements to pop
	 */
	public void pop (int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException ();
		}
		if (sp - size < minSp)
		{
			throw new EmptyStackException ();
		}
		setSp (sp - size);
	}


	/**
	 * Computes the absolute address of the given local variable in
	 * the stack.
	 * 
	 * @param local a local variable
	 * @return absolute address in stack
	 */
	int getPointer (VMXFrame frame, Local local)
	{
		if (local.nesting == Local.JAVA)
		{
			checkIndex (local.index, jsize);
			return frame.jfp + local.index;
		}
		else
		{
			int n = local.nesting;
			while (--n >= 0)
			{
				frame = frame.staticLink;
			}
			checkIndex (local.index, frame.size);
			return frame.fp + local.index;
		}
	}

	public final class MatchConsumerInFrame implements MatchConsumer
	{
		MatchConsumer orig;
		VMXFrame frame;
		QueryState qs;
		int arg;

		MatchConsumerInFrame ()
		{
		}

		public void matchFound (QueryState qs, int arg)
		{
			this.qs = qs;
			this.arg = arg;
			VMXFrame c = currentFrame;
			loadFrame (frame);
			int s = sp;
			minSp = s;
			try
			{
				orig.matchFound (qs, arg);
			}
			finally
			{
				setSp (s);
				loadFrame (c);
			}
			this.qs = null;
		}

		public void dispose ()
		{
			orig = null;
			qs = null;
			frame = null;
			consumerPool.push (this);
		}
	}

	final ObjectList<MatchConsumerInFrame> consumerPool = new ObjectList<MatchConsumerInFrame> ();

	public MatchConsumerInFrame invokeInFrame (MatchConsumer consumer, Frame frame)
	{
		MatchConsumerInFrame c = consumerPool.isEmpty () ? new MatchConsumerInFrame () : consumerPool.pop ();
		c.orig = consumer;
		c.frame = (VMXFrame) frame;
		return c;
	}

	/* *
	 * Executes <code>e</code> in a given stack frame. Firstly,
	 * the Java frame pointer is set to <code>jfp</code>,
	 * a copy of the element at <code>staticLink</code>
	 * is pushed on the static-link stack, and the frame pointer
	 * is set to the frame pointer of this element. Secondly, the
	 * method <code>e.execute</code> is invoked. Then, even in
	 * the case of an exception, all modifications to pointers are
	 * undone. Finally, the returned value of the execution is returned.
	 * <p>
	 * The effect is that <code>e</code> is executed in the same frame
	 * context as the routine with static link <code>staticLink</code>
	 * and Java frame pointer <code>jfp</code>.
	 * 
	 * @param e the executable to be executed
	 * @param staticLink the static-link stack pointer of the frame
	 * @param jfp the Java frame pointer 
	 * @return the returned value of the execution
	 * /
	AbruptCompletion.Return executeInFrame (MatchConsumerInFrame e, VMXFrame frame)
	{
		VMXFrame f = currentFrame;
		loadFrame (frame);
		try
		{
			return e.execute (this);
		}
		finally
		{
			loadFrame (f);
		}
	}*/

/*!!
#foreach ($a in ["Return", "Break", "Throw", "Nonlocal"])

	private final ObjectList pool$a = new ObjectList ();

	/**
	 * Returns an instance of {@link AbruptCompletion.$a}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 * 
#if ($a == "Throw")
	 * @param cause the cause
#elseif ($a == "Break")
	 * @param label the break label
#elseif ($a == "Nonlocal")
	 * @param frame the frame of the target routine
	 * @param reason the reason of the nonlocal abrupt completion
#end
	 * @return an instance of {@link AbruptCompletion.$a}
	 $C
#if ($a != "Nonlocal")
	public
#end
	AbruptCompletion.$a new$a (
#if ($a == "Throw")
		Throwable cause
#elseif ($a == "Break")
		int label
#elseif ($a == "Nonlocal")
		VMXFrame frame, AbruptCompletion reason
#end
	)
	{
		AbruptCompletion.$a ac = pool${a}.isEmpty ()
			? new AbruptCompletion.$a (this)
			: (AbruptCompletion.$a) pool${a}.pop ();
#if ($a == "Throw")
		cause.getClass ();
		ac.cause = cause;
#elseif ($a == "Break")
		ac.label = label;
#elseif ($a == "Nonlocal")
		ac.frame = frame;
		ac.reason = reason;
#end
		return ac;
	}


	void dispose (AbruptCompletion.$a ac)
	{
		pool${a}.push (ac);
	}

#end

#foreach ($type in $vmtypes)
$pp.setType($type)

	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use. The value of the
	 * instance is set to the given <code>value</code>.
	 *
	 * @param value the returned value
	 * @return an instance of {@link AbruptCompletion.Return} with the given <code>value</code>
	 $C
	public AbruptCompletion.Return ${pp.prefix}return (
		$type value)
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.$pp.TYPE;
		ret.${pp.prefix}val = value;
		return ret;
	}

#end

!!*/
//!! #* Start of generated code
// generated
	private final ObjectList poolReturn = new ObjectList ();
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 * 
	 * @return an instance of {@link AbruptCompletion.Return}
	 */
	public
	AbruptCompletion.Return newReturn (
	)
	{
		AbruptCompletion.Return ac = poolReturn.isEmpty ()
			? new AbruptCompletion.Return (this)
			: (AbruptCompletion.Return) poolReturn.pop ();
		return ac;
	}
// generated
// generated
	void dispose (AbruptCompletion.Return ac)
	{
		poolReturn.push (ac);
	}
// generated
// generated
	private final ObjectList poolBreak = new ObjectList ();
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Break}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 * 
	 * @param label the break label
	 * @return an instance of {@link AbruptCompletion.Break}
	 */
	public
	AbruptCompletion.Break newBreak (
		int label
	)
	{
		AbruptCompletion.Break ac = poolBreak.isEmpty ()
			? new AbruptCompletion.Break (this)
			: (AbruptCompletion.Break) poolBreak.pop ();
		ac.label = label;
		return ac;
	}
// generated
// generated
	void dispose (AbruptCompletion.Break ac)
	{
		poolBreak.push (ac);
	}
// generated
// generated
	private final ObjectList poolThrow = new ObjectList ();
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Throw}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 * 
	 * @param cause the cause
	 * @return an instance of {@link AbruptCompletion.Throw}
	 */
	public
	AbruptCompletion.Throw newThrow (
		Throwable cause
	)
	{
		AbruptCompletion.Throw ac = poolThrow.isEmpty ()
			? new AbruptCompletion.Throw (this)
			: (AbruptCompletion.Throw) poolThrow.pop ();
		cause.getClass ();
		ac.cause = cause;
		return ac;
	}
// generated
// generated
	void dispose (AbruptCompletion.Throw ac)
	{
		poolThrow.push (ac);
	}
// generated
// generated
	private final ObjectList poolNonlocal = new ObjectList ();
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Nonlocal}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 * 
	 * @param frame the frame of the target routine
	 * @param reason the reason of the nonlocal abrupt completion
	 * @return an instance of {@link AbruptCompletion.Nonlocal}
	 */
	AbruptCompletion.Nonlocal newNonlocal (
		VMXFrame frame, AbruptCompletion reason
	)
	{
		AbruptCompletion.Nonlocal ac = poolNonlocal.isEmpty ()
			? new AbruptCompletion.Nonlocal (this)
			: (AbruptCompletion.Nonlocal) poolNonlocal.pop ();
		ac.frame = frame;
		ac.reason = reason;
		return ac;
	}
// generated
// generated
	void dispose (AbruptCompletion.Nonlocal ac)
	{
		poolNonlocal.push (ac);
	}
// generated
// generated
// generated
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use. The value of the
	 * instance is set to the given <code>value</code>.
	 *
	 * @param value the returned value
	 * @return an instance of {@link AbruptCompletion.Return} with the given <code>value</code>
	 */
	public AbruptCompletion.Return ireturn (
		int value)
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.INT;
		ret.ival = value;
		return ret;
	}
// generated
// generated
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use. The value of the
	 * instance is set to the given <code>value</code>.
	 *
	 * @param value the returned value
	 * @return an instance of {@link AbruptCompletion.Return} with the given <code>value</code>
	 */
	public AbruptCompletion.Return lreturn (
		long value)
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.LONG;
		ret.lval = value;
		return ret;
	}
// generated
// generated
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use. The value of the
	 * instance is set to the given <code>value</code>.
	 *
	 * @param value the returned value
	 * @return an instance of {@link AbruptCompletion.Return} with the given <code>value</code>
	 */
	public AbruptCompletion.Return freturn (
		float value)
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.FLOAT;
		ret.fval = value;
		return ret;
	}
// generated
// generated
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use. The value of the
	 * instance is set to the given <code>value</code>.
	 *
	 * @param value the returned value
	 * @return an instance of {@link AbruptCompletion.Return} with the given <code>value</code>
	 */
	public AbruptCompletion.Return dreturn (
		double value)
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.DOUBLE;
		ret.dval = value;
		return ret;
	}
// generated
// generated
// generated
	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use. The value of the
	 * instance is set to the given <code>value</code>.
	 *
	 * @param value the returned value
	 * @return an instance of {@link AbruptCompletion.Return} with the given <code>value</code>
	 */
	public AbruptCompletion.Return areturn (
		Object value)
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.OBJECT;
		ret.aval = value;
		return ret;
	}
// generated
// generated
//!! *# End of generated code

	/**
	 * Returns an instance of {@link AbruptCompletion.Nonlocal}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 * 
	 * @param nesting the nesting of the target routine within the invoked routine
	 * @param reason the reason of the nonlocal abrupt completion
	 * @return an instance of {@link AbruptCompletion.Nonlocal}
	 */
	public AbruptCompletion.Nonlocal newNonlocal (int nesting, AbruptCompletion reason, Authorization auth)
	{
		check (auth);
		VMXFrame f = currentFrame;
		while (--nesting >= 0)
		{
			f = f.staticLink;
		}
		return newNonlocal (f, reason);
	}

	/**
	 * Returns an instance of {@link AbruptCompletion.Return}. This instance can be used
	 * until its <code>dispose</code>-method is invoked, which marks
	 * the instance as available for later re-use.
	 *
	 * @return an instance of {@link AbruptCompletion.Return} with a <code>void</code> return value
	 */
	public AbruptCompletion.Return vreturn ()
	{
		AbruptCompletion.Return ret = newReturn ();
		ret.etype = TypeId.VOID;
		return ret;
	}


	private final ObjectList objectArrays = new ObjectList ();

	private Object[] getObjectArray (int length)
	{
		if (length >= objectArrays.size)
		{
			objectArrays.setSize (length + 1);
		}
		Object[] a = (Object[]) objectArrays.get (length);
		if (a == null)
		{
			a = new Object[length];
			objectArrays.set (length, a);
		}
		return a;
	}


	private final ObjectList<int[]> intArrays = new ObjectList<int[]> ();

	/**
	 * Pops <code>length</code> values of type <code>int</code> from the
	 * stack and returns them as an array. The first popped element is placed
	 * in the last component of the array and so on. The returned array
	 * can only be used safely until the next invocation of this method
	 * on the same <code>VMXState</code>.
	 * 
	 * @param length the number of values to be popped
	 * @return an array containing the <code>length</code> popped values
	 */
	public int[] popIntArray (int length)
	{
		int[] a = peekIntArray (length);
		sp -= length;
		return a;
	}

	public int[] peekIntArray (int length)
	{
		int[] a = intArrays.get (length);
		if (a == null)
		{
			a = new int[length];
			intArrays.set (length, a);
		}
		int base = sp - length;
		if (base < minSp)
		{
			throw new EmptyStackException ();
		}
		while (--length >= 0)
		{
			a[length] = istack[base + length];
		}
		return a;
	}


	/**
	 * Invokes the given <code>method</code> using parameters popped
	 * from the stack. The returned value is wrapped in an instance of
	 * {@link AbruptCompletion.Return}. Checked exceptions are wrapped
	 * in an instance of {@link AbruptCompletion.Throw} which is then thrown.
	 * 
	 * @param method the method to invoke
	 * @return the method's return value
	 */
	public AbruptCompletion.Return invoke (Method method)
	{
		int n = method.getParameterCount (), i = n;
		Object[] a = getObjectArray (i);
		while (i > 0)
		{
			i--;
			switch (method.getParameterType (i).getTypeId ())
			{
/*!!
#foreach ($type in $types)
$pp.setType($type)
				case TypeId.$pp.TYPE:
				{
					a[i] = $pp.wrap("($type) (${pp.prefix}pop () $pp.vm2type)");
					break;
				}
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
				{
					a[i] = (((boolean) (ipop ()  != 0)) ? Boolean.TRUE : Boolean.FALSE);
					break;
				}
// generated
				case TypeId.BYTE:
				{
					a[i] = Byte.valueOf ((byte) (ipop () ));
					break;
				}
// generated
				case TypeId.SHORT:
				{
					a[i] = Short.valueOf ((short) (ipop () ));
					break;
				}
// generated
				case TypeId.CHAR:
				{
					a[i] = Character.valueOf ((char) (ipop () ));
					break;
				}
// generated
				case TypeId.INT:
				{
					a[i] = Integer.valueOf ((int) (ipop () ));
					break;
				}
// generated
				case TypeId.LONG:
				{
					a[i] = Long.valueOf ((long) (lpop () ));
					break;
				}
// generated
				case TypeId.FLOAT:
				{
					a[i] = Float.valueOf ((float) (fpop () ));
					break;
				}
// generated
				case TypeId.DOUBLE:
				{
					a[i] = Double.valueOf ((double) (dpop () ));
					break;
				}
// generated
				case TypeId.OBJECT:
				{
					a[i] = ((Object) (apop () ));
					break;
				}
//!! *# End of generated code
			}
		}
		Object v;
		try
		{
			v = method.invoke (((method.getModifiers () & Member.STATIC) == 0)
							   ? apop () : null, a);
		}
		catch (IllegalAccessException e)
		{
			throw newThrow (e);
		}
		catch (java.lang.reflect.InvocationTargetException e)
		{
			Throwable t = e.getCause ();
			if (t instanceof RuntimeException)
			{
				throw (RuntimeException) t;
			}
			else if (t instanceof Error)
			{
				throw (Error) t;
			}
			else
			{
				throw newThrow (t);
			}
		}
		finally
		{
			for (i = n - 1; i >= 0; i--)
			{
				a[i] = null;
			}
		}
		if ("<init>".equals (method.getName ()))
		{
			return areturn (v);
		}
		switch (i = method.getReturnType ().getTypeId ())
		{
			case TypeId.VOID:
				return null;
			case TypeId.BOOLEAN:
				return ireturn (((Boolean) v).booleanValue () ? 1 : 0);
			case TypeId.CHAR:
				return ireturn (((Character) v).charValue ());
			case TypeId.BYTE:
			case TypeId.SHORT:
			case TypeId.INT:
				return ireturn (((Number) v).intValue ());
			case TypeId.LONG:
				return lreturn (((Number) v).longValue ());
			case TypeId.FLOAT:
				return freturn (((Number) v).floatValue ());
			case TypeId.DOUBLE:
				return dreturn (((Number) v).doubleValue ());
			default:
				return areturn (v);
		}
	}

	
	/**
	 * Returns a descriptor for a routine invocation. This method
	 * returns a descriptor for the later invocation of the given
	 * <code>routine</code>. The parameters have to be set as in
	 * {@link #invoke(Routine, int, Authorization)}, however, the <code>routine</code>
	 * is not invoked immediately, but later on by methods declared
	 * in {@link RoutineDescriptor}. This later invocation happens in
	 * an equal frame context as for the immediate invocation by
	 * {@link #invoke(Routine, int, Authorization)}.
	 * 
	 * @param routine the routine to be invoked later on
	 * @param nesting the nesting of the invoking routine within the invoked routine
	 * @return a descriptor for the specified routine incarnation
	 */
	public RoutineDescriptor createDescriptor (Routine routine, int nesting, Authorization auth)
	{
		RoutineDescriptor d = descriptorPool;
		if (d != null)
		{
			descriptorPool = d.next;
			d.next = null;
		}
		else
		{
			d = new RoutineDescriptor (this);
		}
		d.routine = routine;
		VMXFrame f = currentFrame;
		while (--nesting >= -1)
		{
			f = currentFrame.staticLink;
		}
		d.auth = auth;
		d.staticLink = f;
		return d;
	}

}
