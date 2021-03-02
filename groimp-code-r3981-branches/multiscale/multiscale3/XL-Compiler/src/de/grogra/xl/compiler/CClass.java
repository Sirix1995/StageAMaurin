
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

package de.grogra.xl.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import antlr.collections.AST;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XField;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.query.CompiletimeModel;
import de.grogra.xl.query.Query;
import de.grogra.xl.query.RuntimeModel;
import de.grogra.xl.query.RuntimeModelFactory;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.vmx.AbruptCompletion;
import de.grogra.xl.vmx.RoutineBase;
import de.grogra.xl.vmx.VMXState;

/**
 * CClass is used by the compiler to build up the class
 * information during compilation.
 */
public class CClass extends XClass implements Resolvable
{
	Expression rateAssignmentInitializer;

	private int nextAccessorId;
	private final HashSet accessors = new HashSet ();
	private final HashMap accessMethods = new HashMap ();

	
	private CClass routineClass;
	private ObjectList routines;
	
	
	private AST extNode;
	private AST implNode;
	private Compiler compiler;
	private Type superTypeToCheck;
	private Type[] implementedTypes = null;
	

	CClass (String simpleName, String binaryName, int modifiers,
			CClass declaringClass, Type superTypeToCheck, AST extNode,
			Type[] implementedTypes, AST implNode, CompilerBase compiler)
	{
		super (simpleName, binaryName, modifiers, declaringClass, false);
		this.superTypeToCheck = superTypeToCheck;
		this.implementedTypes = implementedTypes;
		this.compiler = (Compiler) compiler;
		this.extNode = extNode;
		this.implNode = implNode;
	}


	public CClass (String simpleName, String binaryName, int modifiers,
				   CClass declaringClass, boolean writable)
	{
		super (simpleName, binaryName, modifiers, declaringClass, writable);
	}

	
	static final Type ROUTINE_BASE = ClassAdapter.wrap (RoutineBase.class);
	static final Type RETURN_TYPE = ClassAdapter.wrap (AbruptCompletion.Return.class);
	static final Type MODEL = ClassAdapter.wrap (RuntimeModel.class);
	static final Type MODEL_FACTORY = ClassAdapter.wrap (RuntimeModelFactory.class);
	static final Type PMODEL = ClassAdapter.wrap (de.grogra.xl.property.RuntimeModel.class);
	static final Type PMODEL_FACTORY = ClassAdapter.wrap (de.grogra.xl.property.RuntimeModelFactory.class);
	static final Type QUERY = ClassAdapter.wrap (Query.class);
	
	private Expression createBody ()
	{
		return new Expression ()
		{
			@Override
			protected void writeImpl (BytecodeWriter writer, boolean discard)
			{
				writeMethod ((XMethod) getAxisParent (), writer);
			}
		};
	}


	private CClass getRoutineClass ()
	{
		CClass t = (CClass) Reflection.getTopLevelType (this);
		if (t != this)
		{
			return t.getRoutineClass ();
		}
		if (routineClass == null)
		{
			String n = Reflection.getSyntheticName
				(Reflection.getDeclaredTypes (this), "Routine");
			routineClass = new CClass
				(n, getBinaryName () + '$' + n,
				 STATIC | FINAL | SYNTHETIC, this, false);
			routineClass.initSupertype (ROUTINE_BASE);
			declareType (routineClass);
			new XMethod ("<init>", 0, routineClass, new Type[] {Type.INT, Type.INT, Type.INT}, Type.VOID, routineClass.createBody ());
			new XMethod ("<clinit>", STATIC, routineClass, Type.TYPE_0, Type.VOID, routineClass.createBody ());
			new XMethod ("execute", PUBLIC, routineClass, new Type[] {CompilerBase.VMX_TYPE}, RETURN_TYPE, routineClass.createBody ());
			routineClass.routines = new ObjectList ();
		}
		return routineClass;
	}

	
	private HashMap<String,CClass> modelClasses;

	private CClass getModelClass (String model, Type type, Type factoryType)
	{
		CClass t = (CClass) Reflection.getTopLevelType (this);
		if (t != this)
		{
			return t.getModelClass (model, type, factoryType);
		}
		if (modelClasses == null)
		{
			modelClasses = new HashMap<String,CClass> ();
		}
		CClass c = modelClasses.get (model);
		if (c == null)
		{
			String n = Reflection.getSyntheticName
				(Reflection.getDeclaredTypes (this), "Model");
			c = new CClass
				(n, getBinaryName () + '$' + n,
				 STATIC | FINAL | SYNTHETIC, this, false);
			c.initSupertype (Type.OBJECT);
			c.fieldForModel = c.declareAuxField ("MODEL", STATIC | FINAL, type);
			c.model = model;
			c.factoryType = factoryType;
			modelClasses.put (model, c);
			declareType (c);
			new XMethod ("<clinit>", STATIC, c, Type.TYPE_0, Type.VOID, c.createBody ());
		}
		return c;
	}

	private XField fieldForModel;
	private String model;
	private Type factoryType;
	private HashMap<String,XField> properties;
	
	public XField getFieldForModel (CompiletimeModel model)
	{
		return getModelClass (model.getRuntimeName (), MODEL, MODEL_FACTORY).fieldForModel;
	}
	
	
	public XField getFieldForQuery (CompiletimeModel model, Query q)
	{
		return getModelClass (model.getRuntimeName (), MODEL, MODEL_FACTORY).getFieldForQuery (q);
	}

	
	public XField getFieldForPropertyModel (de.grogra.xl.property.CompiletimeModel model)
	{
		return getModelClass (model.getRuntimeName (), PMODEL, PMODEL_FACTORY).fieldForModel;
	}
	
	
	public XField getFieldForProperty (de.grogra.xl.property.CompiletimeModel.Property prop)
	{
		return getModelClass (prop.getModel ().getRuntimeName (), PMODEL, PMODEL_FACTORY)
			.getFieldForPropertyImpl (prop);
	}

	
	private XField getFieldForPropertyImpl (de.grogra.xl.property.CompiletimeModel.Property prop)
	{
		if (properties == null)
		{
			properties = new HashMap<String,XField> ();
		}
		XField f = properties.get (prop.getRuntimeName ());
		if (f == null)
		{
			f = declareAuxField ("PROPERTY", STATIC | FINAL, prop.getRuntimeType ());
			properties.put (prop.getRuntimeName (), f);
		}
		return f;
	}

	
	private HashMap<String, Query> queries;

	private static final Type[] LOADER = {ClassAdapter.wrap (ClassLoader.class)};

	private XField getFieldForQuery (Query q)
	{
		if (queries == null)
		{
			queries = new HashMap<String,Query> ();
		}
		XField f = declareAuxField ("QUERY", STATIC | FINAL, QUERY);
		new XMethod (f.getSimpleName (), STATIC, this, LOADER, QUERY, createBody ());
		queries.put (f.getSimpleName (), q);
		return f;
	}


	public CClass getCallbackClass ()
	{
		return Reflection.isInterface (this) ? getRoutineClass () : this;
	}

	
	XField addRoutine (XMethod routine)
	{
		CClass c = getRoutineClass (); 
		c.routines.add (routine);
		return c.declareAuxField
			(routine.getSimpleName (),
			 STATIC | FINAL | (routine.getModifiers () & ACCESS_MODIFIERS), c);
	}

	
	private void writeModelMethod (BytecodeWriter out)
	{
		out.visitMethodInsn (factoryType, "getInstance");
		out.visitaconst (model);
		out.visitaconst (this);
		out.visitMethodInsn (Type.CLASS, "getClassLoader");
		out.visitInsn (Opcodes.DUP);
		out.visitVarInsn (Opcodes.ASTORE, 0);
		out.visitMethodInsn (factoryType, "modelForName");
		out.visitInsn (Opcodes.DUP);
		out.visitVarInsn (Opcodes.ASTORE, 1);
		out.visitFieldInsn (Opcodes.PUTSTATIC, fieldForModel, null);
		if (properties != null)
		{
			for (Entry<String,XField> e : properties.entrySet ())
			{
				out.visitVarInsn (Opcodes.ALOAD, 1);
				out.visitaconst (e.getKey ());
				out.visitVarInsn (Opcodes.ALOAD, 0);
				out.visitMethodInsn
					(Reflection.findMethodWithPrefixInTypes
					 (PMODEL, "mpropertyForName;(Ljava/lang/String;Lj", false, true));
				out.visitCheckCast (e.getValue ().getType ());
				out.visitFieldInsn (Opcodes.PUTSTATIC, e.getValue (), null);
			}
		}
		if (queries != null)
		{
			for (String s : queries.keySet ())
			{
				Field f = getDeclaredField (s);
				out.visitVarInsn (Opcodes.ALOAD, 0);
				out.visitMethodInsn (Reflection.getDeclaredMethod (this, f.getSimpleName ()));
				out.visitFieldInsn (Opcodes.PUTSTATIC, f, null);
			}
		}
	}


	private void writeRoutineMethod (XMethod method, BytecodeWriter out)
	{
		if (method.getName ().equals ("<init>"))
		{
			out.visitLoad (0, TypeId.OBJECT);
			out.visitLoad (1, TypeId.INT);
			out.visiticonst (0);
			out.visitLoad (2, TypeId.INT);
			out.visiticonst (0);
			out.visitLoad (3, TypeId.INT);
			out.visitMethodInsn (ROUTINE_BASE, "<init>");
			out.visitInsn (Opcodes.RETURN);
		}
		else if (method.getName ().equals ("<clinit>"))
		{
			for (int i = 0; i < routines.size (); i++)
			{
				XMethod m = (XMethod) routines.get (i);
				out.visitTypeInsn (Opcodes.NEW, this);
				out.visitInsn (Opcodes.DUP);
				out.visiticonst (i);
				out.visiticonst (m.getParameterSize ());
				out.visiticonst (m.getFrameSize ());
				out.visitMethodInsn (this, "<init>");
				out.visitFieldInsn (Opcodes.PUTSTATIC, m.getRoutineField (), null);
			}
			out.visitInsn (Opcodes.RETURN);
		}
		else
		{
			Label defLabel = new Label ();
			Label[] lbls = new Label[routines.size ()];
			for (int i = 0; i < routines.size (); i++)
			{
				lbls[i] = new Label ();
			}
			out.visitLoad (0, TypeId.OBJECT);
			out.visitFieldInsn
				(Opcodes.GETFIELD,
				 Reflection.getDeclaredField (ROUTINE_BASE, "id"), null);
			out.visitTableSwitchInsn (0, routines.size () - 1, defLabel, lbls);
			for (int i = 0; i < routines.size (); i++)
			{
				out.visitLabel (lbls[i]);
				XMethod m = (XMethod) routines.get (i);
				out.visitLoad (1, TypeId.OBJECT);
				if (m.getReturnType ().getTypeId () != TypeId.VOID)
				{
					out.visitInsn (Opcodes.DUP);
				}
				out.visitMethodInsn (m);
				if (m.getReturnType ().getTypeId () != TypeId.VOID)
				{
					out.visitMethodInsn
						(CompilerBase.VMX_TYPE, Reflection.getJVMPrefix (m.getReturnType ()) + "return");
				}
				else
				{
					out.visitInsn (Opcodes.ACONST_NULL);
				}
				out.visitInsn (Opcodes.ARETURN);
			}
			out.visitLabel (defLabel);
			out.visitTypeInsn (Opcodes.NEW, Compiler.ASSERTION_ERROR);
			out.visitInsn (Opcodes.DUP);
			out.visitMethodInsn (Compiler.ASSERTION_INIT);
			out.visitInsn (Opcodes.ATHROW);
		}
	}


	void writeMethod (XMethod method, BytecodeWriter out)
	{
		if (method.getReturnType () == QUERY)
		{
			try
			{
				Serialization s = new Serialization (this, out, 0, 1);
				((Query) queries.get (method.getSimpleName ())).write (s);
				s.flush ();
			}
			catch (java.io.IOException e)
			{
				throw new AssertionError (e);
			}
			out.visitReturn (TypeId.OBJECT);
		}
		else if (fieldForModel != null)
		{
			writeModelMethod (out);
		}
		else if (method.getDeclaringType ().getSupertype () == ROUTINE_BASE)
		{
			writeRoutineMethod (method, out);
		}
		else
		{
			throw new AssertionError ();
		}
	}


	int getAccessMethodDescriptor (StringBuffer sb, Member member)
	{
		boolean constructor = "<init>".equals (member.getName ());
		int additionalArgCount = 0;
		if (!(Reflection.isStatic (member) || constructor))
		{
			sb.insert (sb.indexOf ("(") + 1, getDescriptor ());
		}

		while (true)
		{
			String s = sb.toString ();
		checkUniqueness:
			if (accessors.add (s))
			{
				for (int i = getDeclaredMethodCount () - 1; i >= 0; i--)
				{
					if (getDeclaredMethod (i).getDescriptor ().startsWith (s))
					{
						break checkUniqueness;
					}
				}
				return additionalArgCount;
			}
			if (constructor)
			{
				sb.insert (sb.lastIndexOf (")"), "Ljava/lang/Math;");
				additionalArgCount++;
			}
			else
			{
				int i = sb.indexOf (";");
				sb.insert (i, nextAccessorId++).insert (i, '$');
			}
		}
	}

	
	public java.util.Iterator getAccessMethods ()
	{
		for (int i = getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			if (((XMethod) getDeclaredMethod (i)).accessMethod != null)
			{
				accessMethods.put (Integer.valueOf (i), ((XMethod) getDeclaredMethod (i)).accessMethod);
			}
		}
		return accessMethods.values ().iterator ();
	}

	
	public AccessMethod getAccessMethodFor (Member m, boolean setter)
	{
		AccessMethod am;
		if ((m.getDeclaringType () == this) && (m instanceof XMethod))
		{
			am = ((XMethod) m).accessMethod;
			if (am == null)
			{
				am = new AccessMethod (this, m, setter);
				((XMethod) m).accessMethod = am;
			}
		}
		else
		{
			StringBuffer sb = new StringBuffer (m.getDeclaringType ().getBinaryName ());
			sb.append (';').append (m.getDescriptor ());
			if (setter)
			{
				sb.append ('.');
			}
			String key = sb.toString ();
			am = (AccessMethod) accessMethods.get (key);
			if (am == null)
			{
				am = new AccessMethod (this, m, setter);
				accessMethods.put (key, am);
			}
		}
		return am;
	}


	private boolean resolvingInterfaceCount;

	@Override
	public int getDeclaredInterfaceCount ()
	{
		if (implementedTypes != null)
		{
			resolveInterfaceCount (new Resolver (compiler));
		}
		return super.getDeclaredInterfaceCount ();
	}


	public void resolve ()
	{
		if (implementedTypes != null)
		{
			resolveInterfaceCount (compiler.resolver);
		}
		if (!Reflection.isInvalid (superTypeToCheck))
		{
			if (compiler.getRun () < Compiler.COMPILATION)
			{
				compiler.toResolveBeforeCompilation.add (this);
				return;
			}
			if (Reflection.isInterface (superTypeToCheck))
			{
				compiler.problems.addSemanticError (Compiler.I18N.msg (
						ProblemReporter.CLASS_EXTENDS_INTERFACE,
						superTypeToCheck.getName ()), extNode);
			}
			else if ((compiler.legalSupertype != null)
					&& !Reflection
							.isSupertype (compiler.legalSupertype, superTypeToCheck))
			{
				compiler.problems.addSemanticError (Compiler.I18N.msg (
						ProblemReporter.ILLEGAL_SUPERCLASS, superTypeToCheck
								.getName ()), extNode);
			}
			else if (Reflection.isFinal (superTypeToCheck))
			{
				compiler.problems.addSemanticError (Compiler.I18N.msg (
						ProblemReporter.CLASS_EXTENDS_FINAL, superTypeToCheck
								.getName ()), extNode);
			}
			superTypeToCheck = null;
		}
	}


	private void resolveInterfaceCount (Resolver r)
	{
		if (resolvingInterfaceCount)
		{
			throw new Error ("Circularity");
		}
		resolvingInterfaceCount = true;
		if (implementedTypes != null)
		{
			AST c = implNode;
			for (int i = 0; i < implementedTypes.length; i++)
			{
				Type s = implementedTypes[i];
				if (!Reflection.isInvalid (s))
				{
					if (Reflection.isInterface (s))
					{
						addInterface (s);
					}
					else
					{
						compiler.problems.addSemanticError (Compiler.I18N.msg (
								ProblemReporter.NO_INTERFACE_TYPE, s
										.getName ()), c);
					}
				}
				c = c.getNextSibling ();
			}
		}
		implementedTypes = null;
	}

	public void dispose ()
	{
		accessMethods.clear ();
		accessors.clear ();
		properties = null;
		queries = null;
		routines = null;
		for (int i = getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			((XMethod) getDeclaredMethod (i)).dispose ();
		}
		for (int i = getDeclaredTypeCount () - 1; i >= 0; i--)
		{
			((CClass) getDeclaredType (i)).dispose ();
		}
	}

}
