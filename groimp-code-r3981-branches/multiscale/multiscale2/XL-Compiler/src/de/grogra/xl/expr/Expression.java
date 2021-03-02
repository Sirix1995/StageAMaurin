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

package de.grogra.xl.expr;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import antlr.collections.AST;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.persistence.Transaction;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.ClassLoaderAdapter;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.TypeLoader;
import de.grogra.util.I18NBundle;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.vmx.AbruptCompletion;
import de.grogra.xl.vmx.VMXState;

/**
 * <code>Expression</code> is the base class for the internal representation
 * of compiled expression trees. These are used by the XL compiler as
 * an intermediate step in the creation of byte-code. However, they are
 * evaluatable on their own (though this is not as performant as byte-code).
 * Expression trees are used for both expressions and statements.
 * 
 * @author Ole Kniemeyer
 */
public class Expression extends Node implements TypeId, ValueObserver,
		Cloneable, ExpressionFactory
{
	public static final I18NBundle I18N = I18NBundle
		.getInstance (Expression.class);

	public static final Expression[] EXPR_0 = new Expression[0];

	static final Type VMX_TYPE = ClassAdapter.wrap (VMXState.class);
	static final Type ABRUPT_COMPLETION_TYPE = ClassAdapter
		.wrap (AbruptCompletion.class);
	static final Type RETURN_TYPE = ClassAdapter
		.wrap (AbruptCompletion.Return.class);
	static final Type THROW_TYPE = ClassAdapter
		.wrap (AbruptCompletion.Throw.class);
	static final Type NONLOCAL_TYPE = ClassAdapter
		.wrap (AbruptCompletion.Nonlocal.class);

	public int etype;

	private static final int LINKED = 1 << Node.USED_BITS;
	public static final int VALID = 1 << Node.USED_BITS + 1;
	public static final int USED_BITS = Node.USED_BITS + 2;

	public long lval;
	//enh:field

	public double dval;
	//enh:field

	public Object aval;
	//enh:field

	// public boolean valid
	//enh:field type=bits(VALID)

	private Type exprType;

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field lval$FIELD;
	public static final NType.Field dval$FIELD;
	public static final NType.Field aval$FIELD;
	public static final NType.Field valid$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Expression.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setLong (Object o, long value)
		{
			switch (id)
			{
				case 0:
					((Expression) o).lval = (long) value;
					return;
			}
			super.setLong (o, value);
		}

		@Override
		public long getLong (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Expression) o).lval;
			}
			return super.getLong (o);
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 1:
					((Expression) o).dval = (double) value;
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 1:
					return ((Expression) o).dval;
			}
			return super.getDouble (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 2:
					((Expression) o).aval = (Object) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 2:
					return ((Expression) o).aval;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Expression ());
		$TYPE.addManagedField (lval$FIELD = new _Field ("lval", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.LONG, null, 0));
		$TYPE.addManagedField (dval$FIELD = new _Field ("dval", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 1));
		$TYPE.addManagedField (aval$FIELD = new _Field ("aval", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Object.class), null, 2));
		$TYPE.addManagedField (valid$FIELD = new NType.BitField ($TYPE, "valid", NType.BitField.PUBLIC  | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, VALID));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new Expression ();
	}

//enh:end

	public Expression ()
	{
		super ();
		etype = -1;
		exprType = null;
	}

	public Expression (Type type)
	{
		this ();
		if (type != null)
		{
			setType (type);
		}
	}

	public Type getType ()
	{
		return exprType;
	}

	public Expression createExpression (Scope scope, AST ast)
	{
		return this;
	}

	@Override
	public Object clone ()
	{
		try
		{
			return super.clone ();
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError ();
		}
	}

	public boolean hasType (Type type)
	{
		return ((type.getModifiers () & Member.INTERFACE) != 0) ? Reflection
			.isSupertypeOrSame (type, getType ()) : Reflection
			.isSuperclassOrSame (type, getType ());
	}

	public boolean hasType (Class type)
	{
		return type.isInterface () ? Reflection.isSupertypeOrSame (type,
			getType ()) : Reflection.isSuperclassOrSame (type, getType ());
	}

	public boolean isPrimitiveOrStringConstant ()
	{
		return (this instanceof Constant)
			&& Reflection.isPrimitiveOrString (getType ());
	}

	public Expression setType (Type type)
	{
		checkSetType (type);
		etype = type.getTypeId ();
		exprType = type;
		return this;
	}

	protected void checkSetType (Type type)
	{
		if ((etype >= 0) && (etype != type.getTypeId ()))
		{
			throw new AssertionError ();
		}
	}

	public boolean isRequired (int index)
	{
		return true;
	}

	public boolean discards (int index)
	{
		return !isRequired (index);
	}

	public boolean needsEmptyOperandStackForFinally ()
	{
		return false;
	}

	public boolean allowsIteration (int index)
	{
		return true;
	}

	public TypeLoader getTypeLoader ()
	{
		Node n = getAxisParent ();
		return (n instanceof Expression) ? ((Expression) n).getTypeLoader ()
				: new ClassLoaderAdapter (getClass ().getClassLoader ());
	}

	public void link (boolean checkTypes)
	{
	}

	public final void linkGraph (boolean checkTypes)
	{
		if ((bits & LINKED) != 0)
		{
			return;
		}
		link (checkTypes);
		bits |= LINKED;
		for (Expression e = getFirstExpression (); e != null; e = e
			.getNextExpression ())
		{
			e.linkGraph (checkTypes);
		}
	}

	public final void unlinkTree ()
	{
		if ((bits & LINKED) == 0)
		{
			return;
		}
		bits &= ~LINKED;
		for (Expression e = getFirstExpression (); e != null; e = e
			.getNextExpression ())
		{
			e.unlinkTree ();
		}
	}

	public int getSupportedTypes (int arg)
	{
		return 0;
	}

	protected void fireValueChanged (Transaction t)
	{
		int i = 0;
		Node n = this, s = null;
		Edge e;
		do
		{
			for (e = n.getFirstEdge (); e != null; e = e.getNext (n))
			{
				if ((s = e.getSource ()) != n)
				{
					int edges = e.getEdgeBits ();
					if ((edges & Graph.SUCCESSOR_EDGE) != 0)
					{
						i++;
						n = s;
						break;
					}
					else if ((edges & Graph.BRANCH_EDGE) != 0)
					{
						if (s instanceof ValueObserver)
						{
							((ValueObserver) s).valueChanged (this, i, t);
						}
						return;
					}
				}
			}
		}
		while (e != null);
	}

	public void valueChanged (Expression expr, int index, Transaction t)
	{
		boolean valid = true;
		try
		{
			switch (etype)
			{
/*!!
#foreach ($type in $types)
				$pp.setType($type)

				#if ($pp.fnumeric)
					#set ($field = "dval$FIELD")
					#set ($t = "Double")
				#elseif ($pp.prefix != "a")
					#set ($field = "lval$FIELD")
					#set ($t = "Long")
				#else
					#set ($field = "aval$FIELD")
					#set ($t = "Object")
				#end
				case $pp.TYPE:
					${field}.set$t
						(this, null,
						 evaluate${pp.Type}Impl (VMXState.current ())
						 $pp.type2vm, t);
					break;
#end
!!*/
//!! #* Start of generated code
				
// generated
												case BOOLEAN:
					lval$FIELD.setLong
						(this, null,
						 evaluateBooleanImpl (VMXState.current ())
						  ? 1 : 0, t);
					break;
				
// generated
												case BYTE:
					lval$FIELD.setLong
						(this, null,
						 evaluateByteImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case SHORT:
					lval$FIELD.setLong
						(this, null,
						 evaluateShortImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case CHAR:
					lval$FIELD.setLong
						(this, null,
						 evaluateCharImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case INT:
					lval$FIELD.setLong
						(this, null,
						 evaluateIntImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case LONG:
					lval$FIELD.setLong
						(this, null,
						 evaluateLongImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case FLOAT:
					dval$FIELD.setDouble
						(this, null,
						 evaluateFloatImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case DOUBLE:
					dval$FIELD.setDouble
						(this, null,
						 evaluateDoubleImpl (VMXState.current ())
						 , t);
					break;
				
// generated
												case OBJECT:
					aval$FIELD.setObject
						(this, null,
						 evaluateObjectImpl (VMXState.current ())
						 , t);
					break;
//!! *# End of generated code
			}
		}
		catch (UndefinedInputException e)
		{
			valid = false;
		}
		if (valid != ((bits & VALID) != 0))
		{
			valid$FIELD.setBoolean (this, null, valid, t);
		}
		fireValueChanged (t);
	}

	public AbruptCompletion.Return evaluateRet (VMXState t)
	{
		switch (etype)
		{
/*!!
#foreach ($type in $types)
			$pp.setType($type)
			case $pp.TYPE:
				return t.${pp.prefix}return (evaluate$pp.Type (t) $pp.type2vm);
#end
!!*/
//!! #* Start of generated code
			
			case BOOLEAN:
				return t.ireturn (evaluateBoolean (t)  ? 1 : 0);
			
			case BYTE:
				return t.ireturn (evaluateByte (t) );
			
			case SHORT:
				return t.ireturn (evaluateShort (t) );
			
			case CHAR:
				return t.ireturn (evaluateChar (t) );
			
			case INT:
				return t.ireturn (evaluateInt (t) );
			
			case LONG:
				return t.lreturn (evaluateLong (t) );
			
			case FLOAT:
				return t.freturn (evaluateFloat (t) );
			
			case DOUBLE:
				return t.dreturn (evaluateDouble (t) );
			
			case OBJECT:
				return t.areturn (evaluateObject (t) );
//!! *# End of generated code
			case VOID:
				evaluateVoidImpl (t);
				return t.vreturn ();
			default:
				throw new AssertionError ();
		}
	}

	public final void evaluateAsVoid (VMXState t)
	{
		switch (etype)
		{
/*!!
#foreach ($type in $types)
			$pp.setType($type)
			case $pp.TYPE:
				evaluate$pp.Type (t);
				return;
#end
!!*/
//!! #* Start of generated code
			
			case BOOLEAN:
				evaluateBoolean (t);
				return;
			
			case BYTE:
				evaluateByte (t);
				return;
			
			case SHORT:
				evaluateShort (t);
				return;
			
			case CHAR:
				evaluateChar (t);
				return;
			
			case INT:
				evaluateInt (t);
				return;
			
			case LONG:
				evaluateLong (t);
				return;
			
			case FLOAT:
				evaluateFloat (t);
				return;
			
			case DOUBLE:
				evaluateDouble (t);
				return;
			
			case OBJECT:
				evaluateObject (t);
				return;
//!! *# End of generated code
			case VOID:
				evaluateVoidImpl (t);
				return;
		}
		throw new AssertionError ();
	}

	public final Object evaluateAsObject (VMXState t)
	{
		switch (etype)
		{
/*!!
#foreach ($type in $types)
			$pp.setType($type)
			case $pp.TYPE:
				return $pp.wrap("evaluate$pp.Type (t)");
#end
!!*/
//!! #* Start of generated code
			
			case BOOLEAN:
				return ((evaluateBoolean (t)) ? Boolean.TRUE : Boolean.FALSE);
			
			case BYTE:
				return Byte.valueOf (evaluateByte (t));
			
			case SHORT:
				return Short.valueOf (evaluateShort (t));
			
			case CHAR:
				return Character.valueOf (evaluateChar (t));
			
			case INT:
				return Integer.valueOf (evaluateInt (t));
			
			case LONG:
				return Long.valueOf (evaluateLong (t));
			
			case FLOAT:
				return Float.valueOf (evaluateFloat (t));
			
			case DOUBLE:
				return Double.valueOf (evaluateDouble (t));
			
			case OBJECT:
				return (evaluateObject (t));
//!! *# End of generated code
			case VOID:
				evaluateVoidImpl (t);
				return null;
		}
		throw new AssertionError ();
	}

/*!!
#foreach ($type in $types_void)
	$pp.setType($type)

#if (!$pp.void)

	public final $pp.jtype evaluate$pp.Type ()
	{
		linkGraph (false);
		return evaluate$pp.Type (VMXState.current ());
	}

	public final $pp.jtype evaluate$pp.Type (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
	#if ($pp.boolean)
				lval != 0;
	#elseif ($pp.fnumeric)
				($type) dval;
	#elseif ($pp.prefix == "a")
				aval;
	#else
				($type) lval;
	#end
		}
	#if ($pp.boolean)
		return evaluateBooleanImpl (t);
	#elseif ($pp.Object)
		return ((etype == OBJECT) || (etype < 0)) ? evaluateObjectImpl (t)
			: evaluateAsObject (t);
	#else
		switch (etype)
		{
		#foreach ($type2 in $numeric_char)
		#if ($type2 != $type)
			$pp.setType($type2)
			case $pp.TYPE:
				return ($type) evaluate${pp.Type}Impl (t);
		#end
		#end
		$pp.setType($type)
			default:
				return evaluate${pp.Type}Impl (t);
		}
	#end
	}

#end


	protected $pp.jtype evaluate${pp.Type}Impl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
#end
!!*/
//!! #* Start of generated code
	
// generated
// generated
	public final boolean evaluateBoolean ()
	{
		linkGraph (false);
		return evaluateBoolean (VMXState.current ());
	}
// generated
	public final boolean evaluateBoolean (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					lval != 0;
			}
			return evaluateBooleanImpl (t);
		}
// generated
// generated
// generated
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final byte evaluateByte ()
	{
		linkGraph (false);
		return evaluateByte (VMXState.current ());
	}
// generated
	public final byte evaluateByte (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(byte) lval;
			}
			switch (etype)
		{
											
			case SHORT:
				return (byte) evaluateShortImpl (t);
									
			case CHAR:
				return (byte) evaluateCharImpl (t);
									
			case INT:
				return (byte) evaluateIntImpl (t);
									
			case LONG:
				return (byte) evaluateLongImpl (t);
									
			case FLOAT:
				return (byte) evaluateFloatImpl (t);
									
			case DOUBLE:
				return (byte) evaluateDoubleImpl (t);
						
			default:
				return evaluateByteImpl (t);
		}
		}
// generated
// generated
// generated
	protected byte evaluateByteImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final short evaluateShort ()
	{
		linkGraph (false);
		return evaluateShort (VMXState.current ());
	}
// generated
	public final short evaluateShort (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(short) lval;
			}
			switch (etype)
		{
							
			case BYTE:
				return (short) evaluateByteImpl (t);
													
			case CHAR:
				return (short) evaluateCharImpl (t);
									
			case INT:
				return (short) evaluateIntImpl (t);
									
			case LONG:
				return (short) evaluateLongImpl (t);
									
			case FLOAT:
				return (short) evaluateFloatImpl (t);
									
			case DOUBLE:
				return (short) evaluateDoubleImpl (t);
						
			default:
				return evaluateShortImpl (t);
		}
		}
// generated
// generated
// generated
	protected short evaluateShortImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final char evaluateChar ()
	{
		linkGraph (false);
		return evaluateChar (VMXState.current ());
	}
// generated
	public final char evaluateChar (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(char) lval;
			}
			switch (etype)
		{
							
			case BYTE:
				return (char) evaluateByteImpl (t);
									
			case SHORT:
				return (char) evaluateShortImpl (t);
													
			case INT:
				return (char) evaluateIntImpl (t);
									
			case LONG:
				return (char) evaluateLongImpl (t);
									
			case FLOAT:
				return (char) evaluateFloatImpl (t);
									
			case DOUBLE:
				return (char) evaluateDoubleImpl (t);
						
			default:
				return evaluateCharImpl (t);
		}
		}
// generated
// generated
// generated
	protected char evaluateCharImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final int evaluateInt ()
	{
		linkGraph (false);
		return evaluateInt (VMXState.current ());
	}
// generated
	public final int evaluateInt (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(int) lval;
			}
			switch (etype)
		{
							
			case BYTE:
				return (int) evaluateByteImpl (t);
									
			case SHORT:
				return (int) evaluateShortImpl (t);
									
			case CHAR:
				return (int) evaluateCharImpl (t);
													
			case LONG:
				return (int) evaluateLongImpl (t);
									
			case FLOAT:
				return (int) evaluateFloatImpl (t);
									
			case DOUBLE:
				return (int) evaluateDoubleImpl (t);
						
			default:
				return evaluateIntImpl (t);
		}
		}
// generated
// generated
// generated
	protected int evaluateIntImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final long evaluateLong ()
	{
		linkGraph (false);
		return evaluateLong (VMXState.current ());
	}
// generated
	public final long evaluateLong (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(long) lval;
			}
			switch (etype)
		{
							
			case BYTE:
				return (long) evaluateByteImpl (t);
									
			case SHORT:
				return (long) evaluateShortImpl (t);
									
			case CHAR:
				return (long) evaluateCharImpl (t);
									
			case INT:
				return (long) evaluateIntImpl (t);
													
			case FLOAT:
				return (long) evaluateFloatImpl (t);
									
			case DOUBLE:
				return (long) evaluateDoubleImpl (t);
						
			default:
				return evaluateLongImpl (t);
		}
		}
// generated
// generated
// generated
	protected long evaluateLongImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final float evaluateFloat ()
	{
		linkGraph (false);
		return evaluateFloat (VMXState.current ());
	}
// generated
	public final float evaluateFloat (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(float) dval;
			}
			switch (etype)
		{
							
			case BYTE:
				return (float) evaluateByteImpl (t);
									
			case SHORT:
				return (float) evaluateShortImpl (t);
									
			case CHAR:
				return (float) evaluateCharImpl (t);
									
			case INT:
				return (float) evaluateIntImpl (t);
									
			case LONG:
				return (float) evaluateLongImpl (t);
													
			case DOUBLE:
				return (float) evaluateDoubleImpl (t);
						
			default:
				return evaluateFloatImpl (t);
		}
		}
// generated
// generated
// generated
	protected float evaluateFloatImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final double evaluateDouble ()
	{
		linkGraph (false);
		return evaluateDouble (VMXState.current ());
	}
// generated
	public final double evaluateDouble (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					(double) dval;
			}
			switch (etype)
		{
							
			case BYTE:
				return (double) evaluateByteImpl (t);
									
			case SHORT:
				return (double) evaluateShortImpl (t);
									
			case CHAR:
				return (double) evaluateCharImpl (t);
									
			case INT:
				return (double) evaluateIntImpl (t);
									
			case LONG:
				return (double) evaluateLongImpl (t);
									
			case FLOAT:
				return (double) evaluateFloatImpl (t);
										
			default:
				return evaluateDoubleImpl (t);
		}
		}
// generated
// generated
// generated
	protected double evaluateDoubleImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
	public final Object evaluateObject ()
	{
		linkGraph (false);
		return evaluateObject (VMXState.current ());
	}
// generated
	public final Object evaluateObject (VMXState t)
	{
		if ((bits & VALID) != 0)
		{
			return
					aval;
			}
			return ((etype == OBJECT) || (etype < 0)) ? evaluateObjectImpl (t)
			: evaluateAsObject (t);
		}
// generated
// generated
// generated
	protected Object evaluateObjectImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
	
// generated
// generated
// generated
	protected void evaluateVoidImpl (VMXState t)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}
//!! *# End of generated code

	public Expression toConst ()
	{
		return this;
	}

	protected Expression toConstImpl ()
	{
		linkGraph (true);
		Type t = getType ();
		if ((etype == TypeId.OBJECT) && !Reflection.equal (t, Type.NULL)
			&& !Reflection.equal (t, Type.STRING))
		{
			return this;
		}
		try
		{
			Expression c = createConst (t, evaluateAsObject (null));
			Compiler.copyInfo (this, c);
			return c;
		}
		catch (RuntimeException e)
		{
			return this;
		}
	}

	public final void push (VMXState t)
	{
		switch (etype)
		{
/*!!
#foreach ($type in $types)
			$pp.setType($type)
			case $pp.TYPE:
			 	t.${pp.prefix}push (evaluate$pp.Type (t) $pp.type2vm);
				return;
#end
!!*/
//!! #* Start of generated code
			
			case BOOLEAN:
			 	t.ipush (evaluateBoolean (t)  ? 1 : 0);
				return;
			
			case BYTE:
			 	t.ipush (evaluateByte (t) );
				return;
			
			case SHORT:
			 	t.ipush (evaluateShort (t) );
				return;
			
			case CHAR:
			 	t.ipush (evaluateChar (t) );
				return;
			
			case INT:
			 	t.ipush (evaluateInt (t) );
				return;
			
			case LONG:
			 	t.lpush (evaluateLong (t) );
				return;
			
			case FLOAT:
			 	t.fpush (evaluateFloat (t) );
				return;
			
			case DOUBLE:
			 	t.dpush (evaluateDouble (t) );
				return;
			
			case OBJECT:
			 	t.apush (evaluateObject (t) );
				return;
//!! *# End of generated code
			case VOID:
				evaluateVoidImpl (t);
				return;
		}
		throw new AssertionError ();
	}

	public Expression add (Expression expr)
	{
		if (expr != null)
		{
			appendBranchNode (expr);
		}
		return this;
	}

	public Expression setNextSibling (Expression expr)
	{
		setSuccessor ((Node) expr);
		return this;
	}

	public Expression receiveChildren (Expression source)
	{
		Node c = source.getBranch ();
		source.setBranch (null);
		setBranch (c);
		return this;
	}

	public Expression substitute (Expression prev)
	{
		Node c = prev.getPredecessor ();
		if (c == null)
		{
			c = prev.getAxisParent ();
			c.setBranch (this);
		}
		else
		{
			c.setSuccessor (this);
		}
		c = prev.getSuccessor ();
		if (c != null)
		{
			prev.setNextSibling (null);
			setSuccessor (c);
		}
		return this;
	}

	public Expression getFirstExpression ()
	{
		return (Expression) getBranch ();
	}

	public Expression getNextExpression ()
	{
		return (Expression) getSuccessor ();
	}

	public Expression getLastExpression ()
	{
		Expression e = getFirstExpression ();
		for (Expression f = e; f != null; f = f.getNextExpression ())
		{
			e = f;
		}
		return e;
	}

	public int getExpressionCount ()
	{
		return getBranchLength ();
	}

	public Expression getExpression (int index)
	{
		for (Expression e = getFirstExpression (); e != null; e = e
			.getNextExpression ())
		{
			if (index-- == 0)
			{
				return e;
			}
		}
		return null;
	}

	public Expression getLeastCommonAncestor (Expression other)
	{
		for (Expression e = this; e != null; e = (Expression) e.getAxisParent ())
		{
			for (Expression f = other; f != null; f = (Expression) f.getAxisParent ())
			{
				if (e == f)
				{
					return e;
				}
			}
		}
		return null;
	}


	/**
	 * Determines the type id (see {@link TypeId}) of the binarily promoted type
	 * of the operands <code>expr1</code> and <code>expr2</code>
	 * (binary promotion is defined by the Java Language
	 * Specification, it includes unboxing).
	 * 
	 * @param expr1 first operand
	 * @param expr2 second operand
	 * @return type id of promoted type
	 */
	public int getPromotedType (Expression expr1, Expression expr2)
	{
		// obtain type information of both expressions
		int etype1 = expr1.etype;
		int etype2 = expr2.etype;

		// check for unboxing
		if (etype1 == OBJECT)
		{
			Type t1 = Reflection.getUnwrappedType (expr1.getType ());
			if (t1 != Type.INVALID)
			{
				// perform unboxing for type 1
				etype1 = t1.getTypeId ();
			}
		}
		if (etype2 == OBJECT)
		{
			Type t2 = Reflection.getUnwrappedType (expr2.getType ());
			if (t2 != Type.INVALID)
			{
				// perform unboxing for type 2
				etype2 = t2.getTypeId ();
			}
		}

		// get the union of the type masks of both operands
		int t = (1 << etype1) | (1 << etype2);
		if ((t & VOID_MASK) != 0)
		{
			// at least one operand is void
			return -1;
		}
		if ((t & BOOLEAN_MASK) != 0)
		{
			// at least one operand is boolean,
			// then promotion is valid only if both operands are boolean
			return (t == BOOLEAN_MASK) ? BOOLEAN : -1;
		}
		if ((t & OBJECT_MASK) != 0)
		{
			// at least one operand is of reference type,
			// then promotion is valid only if both operands are of reference type
			return (t == OBJECT_MASK) ? OBJECT : -1;
		}
		if ((t & ~NUMERIC_MASK) != 0)
		{
			// at least one operand is non-numeric. The only valid cases
			// of non-numeric promotion were handled above, so reaching
			// this point means that a promotion is not defined
			return -1;
		}

		// at this points both operands are numeric

		if ((t & DOUBLE_MASK) != 0)
		{
			// at least one operand is double, then this is the promoted type
			return DOUBLE;
		}
		if ((t & FLOAT_MASK) != 0)
		{
			// at least one operand is float, then this is the promoted type
			return FLOAT;
		}
		if ((t & LONG_MASK) != 0)
		{
			// at least one operand is long, then this is the promoted type
			return LONG;
		}

		// both operands are int or convertible to int by widening conversion,
		// the promoted type is int
		return INT;
	}

	/**
	 * Converts this expression by unary promotion to
	 * one of the types whose mask (see {@link TypeId})
	 * is present in <code>supportedTypes</code>. The most specific
	 * type is chosen, i.e. the type
	 * which is a subtype of all other possible, supported types.
	 * @param scope TODO
	 * @param supportedTypes union of allowed type masks for the promoted expression
	 * 
	 * @return promoted expression, its type being one of the supported types
	 * @throws IllegalOperandTypeException if promotion cannot be performed
	 */
	public Expression promote (Scope scope, int supportedTypes)
	{
		// check for unboxing
		if (etype == OBJECT)
		{
			Type t1 = Reflection.getUnwrappedType (getType ());
			if (t1 != Type.INVALID)
			{
				// perform unboxing 
				Expression result = unboxingConversion ();
				return result.promote (scope, supportedTypes);
			}
		}
		else
		{
			int t = etype;
	
			// this loop starts at type id t and advances as long as it is
			// a widening conversion from t to i. This automatically starts
			// with more specific types and moves towards lesser specific
			// types. The case CHAR has to be handled specially, because
			// BYTE < SHORT < CHAR < INT, but there is no widening conversion
			// from byte or short to char
			for (int i = t; (i == CHAR) || Reflection.isWideningConversion (t, i); i++)
			{
				// consider char as promoted type i only if char is the original
				// type t too
				if (((i != CHAR) || (t == CHAR))
				// and check if the type mask of i is present in supportedTypes
					&& (((1 << i) & supportedTypes) != 0))
				{
					// in this case, i is the most specific supported type for t
					return implicitConversion (scope, Reflection.getType (i));
				}
			}
		}

		// no matching type for t was found in supportedTypes
		throw new IllegalOperandTypeException (I18N.msg (
			"expr.illegal-promotion", getType ()));
	}

	public Expression compile (Scope scope, Expression e)
	{
		throw new AssertionError ();
	}

	public Expression compile (Scope scope, Expression e1, Expression e2)
	{
		throw new AssertionError ();
	}

	public Expression implicitConversion (Scope scope, Type type)
	{
		// check for identity conversion
		if (Reflection.equal (getType (), type))
		{
			return this;
		}
		Expression e = scope.getCompiler ().standardImplicitConversion (this, type, true, scope, null, null, false);
		if (e == null)
		{
			throw new IllegalCastException (getType (), type);
		}
		return e.toConst ();
	}

	/**
	 * If this expression is a boxed value, return its unboxed expression.
	 * Otherwise return this expression itself.
	 * @return
	 */
	public Expression unboxingConversion ()
	{
		Type t = Reflection.getUnwrappedType (getType ());
		if (t != Type.INVALID)
		{
			return new InvokeVirtual (Reflection
				.getDeclaredMethod (getType (), t.getBinaryName ()
					+ "Value")).add (this);
		}
		return this;
	}

	public Expression cast (Type type)
	{
		return Reflection.equal (type, getType ()) ? this : new Cast (type).add (this).toConst ();
	}

	public final void checkExpressionCount (int count)
	{
		if (count != getExpressionCount ())
		{
			throw new ValidationException (I18N.msg ("expr.wrong-expr-count",
				getClass ().getName (), count, getExpressionCount ()));
		}
	}

	public final Expression getExpression (int index, int typeId, boolean checkTypes)
	{
		Expression e = getExpression (index);
		if (checkTypes && (e.etype != typeId))
		{
			throw new ValidationException (I18N.msg ("expr.wrong-expr-type",
				getClass ().getName (), index, Reflection
					.getTypeName (typeId), e.getType ().getName ()));
		}
		return e;
	}

	public final Expression getExpression (int index, Class cls)
	{
		Expression e = getExpression (index);
		if (!cls.isInstance (e))
		{
			throw new ValidationException (I18N.msg ("expr.wrong-expr-class",
				getClass ().getName (), index, cls.getName (), e
					.getClass ().getName ()));
		}
		return e;
	}

	public final Expression getObjectExpression (int index, Class type, boolean checkTypes)
	{
		return getObjectExpression (index, ClassAdapter.wrap (type), checkTypes);
	}

	public final Expression getObjectExpression (int index, Type type, boolean checkTypes)
	{
		Expression e = getExpression (index, OBJECT, checkTypes);
		if (checkTypes && !Reflection.isAssignableFrom (type, e.getType ()))
		{
			throw new ValidationException (I18N.msg ("expr.wrong-expr-type",
				getClass ().getName (), index, type.getName (), e
					.getType ().getName ()));
		}
		return e;
	}

	public static final Expression createConst (Type type, Object value)
	{
		switch (type.getTypeId ())
		{
/*!!
#foreach ($type in $primitives)
			$pp.setType($type)
			case TypeId.$pp.TYPE:
				return new ${pp.Type}Const
					((value == null) ? $pp.null : $pp.unwrap("value"));
#end
!!*/
//!! #* Start of generated code
			
			case TypeId.BOOLEAN:
				return new BooleanConst
					((value == null) ? false : (((Boolean) (value)).booleanValue ()));
			
			case TypeId.BYTE:
				return new ByteConst
					((value == null) ? ((byte) 0) : (((Number) (value)).byteValue ()));
			
			case TypeId.SHORT:
				return new ShortConst
					((value == null) ? ((short) 0) : (((Number) (value)).shortValue ()));
			
			case TypeId.CHAR:
				return new CharConst
					((value == null) ? ((char) 0) : (((Character) (value)).charValue ()));
			
			case TypeId.INT:
				return new IntConst
					((value == null) ? ((int) 0) : (((Number) (value)).intValue ()));
			
			case TypeId.LONG:
				return new LongConst
					((value == null) ? ((long) 0) : (((Number) (value)).longValue ()));
			
			case TypeId.FLOAT:
				return new FloatConst
					((value == null) ? ((float) 0) : (((Number) (value)).floatValue ()));
			
			case TypeId.DOUBLE:
				return new DoubleConst
					((value == null) ? ((double) 0) : (((Number) (value)).doubleValue ()));
//!! *# End of generated code
			case TypeId.OBJECT:
				return new ObjectConst (value, type);
			default:
				throw new AssertionError ();
		}
	}

	public static final Expression createMinConst (int typeId)
	{
		switch (typeId)
		{
/*!!
#foreach ($type in $numeric_char)
			$pp.setType($type)
			case $pp.TYPE:
			#if ($pp.fnumeric)
				return new ${pp.Type}Const (${pp.wrapper}.NEGATIVE_INFINITY);
			#else
				return new ${pp.Type}Const (${pp.wrapper}.MIN_VALUE);
			#end
#end
!!*/
//!! #* Start of generated code
			
			case BYTE:
							return new ByteConst (Byte.MIN_VALUE);
						
			case SHORT:
							return new ShortConst (Short.MIN_VALUE);
						
			case CHAR:
							return new CharConst (Character.MIN_VALUE);
						
			case INT:
							return new IntConst (Integer.MIN_VALUE);
						
			case LONG:
							return new LongConst (Long.MIN_VALUE);
						
			case FLOAT:
							return new FloatConst (Float.NEGATIVE_INFINITY);
						
			case DOUBLE:
							return new DoubleConst (Double.NEGATIVE_INFINITY);
//!! *# End of generated code
			default:
				throw new AssertionError (typeId);
		}
	}

	public static final Expression createMaxConst (int typeId)
	{
		switch (typeId)
		{
/*!!
#foreach ($type in $numeric_char)
			$pp.setType($type)
			case $pp.TYPE:
			#if ($pp.fnumeric)
				return new ${pp.Type}Const (${pp.wrapper}.POSITIVE_INFINITY);
			#else
				return new ${pp.Type}Const (${pp.wrapper}.MAX_VALUE);
			#end
#end
!!*/
//!! #* Start of generated code
			
			case BYTE:
							return new ByteConst (Byte.MAX_VALUE);
						
			case SHORT:
							return new ShortConst (Short.MAX_VALUE);
						
			case CHAR:
							return new CharConst (Character.MAX_VALUE);
						
			case INT:
							return new IntConst (Integer.MAX_VALUE);
						
			case LONG:
							return new LongConst (Long.MAX_VALUE);
						
			case FLOAT:
							return new FloatConst (Float.POSITIVE_INFINITY);
						
			case DOUBLE:
							return new DoubleConst (Double.POSITIVE_INFINITY);
//!! *# End of generated code
			default:
				throw new AssertionError (typeId);
		}
	}

	public boolean evaluatesWithoutSideeffect ()
	{
		return (this instanceof LocalValue) || (this instanceof Constant);
	}

	@Override
	public String toString ()
	{
		String s = getClass ().getName ();
		return s.substring (s.lastIndexOf ('.') + 1) + '[' + paramString ()
			+ "]@" + Integer.toHexString (hashCode ());
	}

	@Override
	protected String paramString ()
	{
		return (getType () != null) ? getType ().getBinaryName () : "";
	}

	static String getDescriptorNoBS (int typeId)
	{
		return ((typeId == BYTE) || (typeId == SHORT)) ? "I" : Reflection
			.getType (typeId).getDescriptor ();
	}

	public static int opcode (int typeId, int[] opcodes)
	{
		int i;
		switch (typeId)
		{
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case CHAR:
			case INT:
				i = 0;
				break;
			case LONG:
				i = 1;
				break;
			case FLOAT:
				i = 2;
				break;
			case DOUBLE:
				i = 3;
				break;
			case OBJECT:
				i = 4;
				break;
			default:
				throw new IllegalArgumentException ();
		}
		i = opcodes[i];
		if (i < 0)
		{
			throw new IllegalArgumentException ();
		}
		return i;
	}

	public int opcode (int[] opcodes)
	{
		return opcode (etype, opcodes);
	}

	public void writeChildren (BytecodeWriter writer)
	{
		int i = 0;
		for (Expression e = getFirstExpression (); e != null; e = e
			.getNextExpression ())
		{
			e.write (writer, discards (i++));
		}
	}

	public final void write (BytecodeWriter writer, boolean discard)
	{
		AST node = Compiler.getAST (this);
		if (node != null)
		{
			writer.visitLineNumber (node.getLine ());
		}
		writeImpl (writer, discard);
	}

	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writeChildren (writer);
		writeOperator (writer);
		if (discard)
		{
			writer.visitPop (etype);
		}
	}

	public boolean isConditional ()
	{
		return false;
	}

	public int writeConditional (BytecodeWriter writer, Label falseLabel,
			Label trueLabel)
	{
		assert etype == BOOLEAN;
		write (writer, false);
		if (trueLabel == null)
		{
			if (falseLabel != null)
			{
				writer.visitJumpInsn (Opcodes.IFEQ, falseLabel);
			}
			else
			{
				writer.visitInsn (Opcodes.POP);
			}
		}
		else
		{
			writer.visitJumpInsn (Opcodes.IFNE, trueLabel);
			if (falseLabel != null)
			{
				writer.visitJumpInsn (Opcodes.GOTO, falseLabel);
			}
		}
		return 0;
	}

	protected void writeConditional (BytecodeWriter writer, boolean discard)
	{
		if (discard)
		{
			writeConditional (writer, null, null);
		}
		else
		{
			Label f = new Label ();
			Label end = new Label ();

			int c = writeConditional (writer, f, null);
			if (c != 0)
			{
				writer.visitLabel (f);
				writer.visiticonst ((c > 0) ? 1 : 0);
			}
			else
			{
				writer.visiticonst (1);
				writer.visitJumpInsn (Opcodes.GOTO, end);

				writer.visitLabel (f);
				writer.visiticonst (0);

				writer.visitLabel (end);
			}
		}
	}

	public void writeFinally (BytecodeWriter writer, int label,
			ControlTransfer cause)
	{
		((Expression) getAxisParent ()).writeFinally (writer, label, cause);
	}

	protected void writeOperator (BytecodeWriter writer)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}

}
