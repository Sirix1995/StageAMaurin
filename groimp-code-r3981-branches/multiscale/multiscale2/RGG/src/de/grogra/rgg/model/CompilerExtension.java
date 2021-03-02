
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

package de.grogra.rgg.model;

import java.util.HashMap;

import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.persistence.ManageableType;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.XField;
import de.grogra.util.Int2IntMap;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.Extension;
import de.grogra.xl.compiler.XMethod;
import de.grogra.xl.compiler.scope.ClassPath;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.compiler.scope.TypeScope;
import de.grogra.xl.expr.AssignField;
import de.grogra.xl.expr.Assignment;
import de.grogra.xl.expr.Block;
import de.grogra.xl.expr.BooleanConst;
import de.grogra.xl.expr.Cast;
import de.grogra.xl.expr.ClassConst;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.expr.GetField;
import de.grogra.xl.expr.IntConst;
import de.grogra.xl.expr.InvokeSpecial;
import de.grogra.xl.expr.InvokeVirtual;
import de.grogra.xl.expr.New;
import de.grogra.xl.expr.ObjectConst;
import de.grogra.xl.expr.Return;
import de.grogra.xl.expr.SetThis;
import de.grogra.xl.expr.Switch;
import de.grogra.xl.expr.Variable;

public class CompilerExtension implements Extension
{
	static final Type<ManageableType> MTYPE = ClassAdapter.wrap (ManageableType.class);
	static final Type<NType> NTYPE = ClassAdapter.wrap (NType.class);
	static final Type<NType.Field> FIELD = ClassAdapter.wrap (NType.Field.class);


	public boolean forcesDefaultConstructorFor (Type type)
	{
		return Reflection.isSuperclassOrSame (Node.$TYPE, type);
	}

	
	private static Method getSupermethod (MethodScope ms)
	{
		return Reflection.findMethodWithPrefixInTypes
			(ms.getDeclaredType ().getSupertype (), ms.getMethod ().getDescriptor (),
			 false, false);
	}


	private HashMap<TypeScope,MethodScope> methods = new HashMap<TypeScope,MethodScope> ();
	private HashMap<TypeScope,Block> inits = new HashMap<TypeScope,Block> ();

	public void preprocess (TypeScope scope, int run)
	{
		if (run != Compiler.COMPILATION)
		{
			return;
		}
		CClass type = scope.getDeclaredType ();
		if ((scope.getModifiersEx () & (Compiler.MOD_MODULE | Compiler.MOD_INSTANTIATOR))
			== (Compiler.MOD_MODULE | Compiler.MOD_INSTANTIATOR))
		{
			MethodScope ms = methods.get (scope);
			ms.addExpression
				(new Return (ms, ms.getMethod ().getReturnType ())
				 .add (new GetField
				 	   (Reflection.getDeclaredField (ClassAdapter.wrap (Instantiation.class),
				 		"INSTANTIATOR"))));
		}
		type.setPublic ();
		if (!Reflection.isSuperclassOrSame (Node.$TYPE, type)
			|| Reflection.isInner (type))
		{
			return;
		}
		
		XField typeField = type.declareField
			("$TYPE", Member.PUBLIC | Member.FINAL | Member.STATIC | Member.SYNTHETIC,
			 NTYPE);
		
		Method defConstr = Reflection.getDefaultConstructor (type);
		assert defConstr != null : type;
		
		((XMethod) defConstr).setPublic ();

		Expression newNType;
		if (Reflection.isAbstract (type))
		{
			newNType = new InvokeSpecial (Reflection.getDeclaredMethod (NTYPE, "m<init>;(Ljava/lang/Class;)V"))
				.add (new New (NTYPE))
				.add (new ObjectConst (type, Type.CLASS));
		}
		else
		{
			newNType = new InvokeSpecial (Reflection.getDeclaredMethod (NTYPE, "m<init>;(Lde/grogra/graph/impl/Node;)V"))
				.add (new New (NTYPE))
				.add (new InvokeSpecial (defConstr).add (new New (type)));
		}
		Block staticInit = new Block ();
		inits.put (scope, staticInit);
		staticInit.add (new AssignField (typeField, Assignment.SIMPLE).add (newNType));

		if (!Reflection.isAbstract (type))
		{
			MethodScope ms = scope.createMethodScope (Member.PROTECTED);
			ms.createAndDeclareMethod ("newInstance", Node.$TYPE);
			ms.addExpression (new Return (ms, null).add (new InvokeSpecial (defConstr).add (new New (type))));
			
			ms = scope.createMethodScope (Member.PROTECTED);
			ms.createAndDeclareMethod ("getNTypeImpl", NTYPE);
			ms.addExpression (new Return (ms, null).add (new GetField (typeField)));
		}
		
		CClass fieldClass = null;
		TypeScope fcs = null;
		XMethod constr = null;
		XField idField = null;

		MethodScope[] getterMethods = new MethodScope[TypeId.TYPE_COUNT];
		MethodScope[] setterMethods = new MethodScope[TypeId.TYPE_COUNT];
		Switch[] getterSwitches = new Switch[TypeId.TYPE_COUNT];
		Switch[] setterSwitches = new Switch[TypeId.TYPE_COUNT];
		Local[] getterObjects = new Local[TypeId.TYPE_COUNT];
		Local[] setterObjects = new Local[TypeId.TYPE_COUNT];
		Local[] setterValues = new Local[TypeId.TYPE_COUNT];
		Int2IntMap[] getterLabels = new Int2IntMap[TypeId.TYPE_COUNT];
		Int2IntMap[] setterLabels = new Int2IntMap[TypeId.TYPE_COUNT];

		for (int i = 0; i < type.getDeclaredFieldCount (); i++)
		{
			XField f = (XField) type.getDeclaredField (i);
			int m = f.getModifiers ();
			if ((m & (Member.FINAL | Member.STATIC | Member.TRANSIENT)) == 0)
			{
				if (fieldClass == null)
				{
					fieldClass = new CClass
						("_Field", type.getBinaryName () + "$_Field",
						 Member.PRIVATE | Member.FINAL | Member.STATIC | Member.SYNTHETIC,
						 type, false);
					fieldClass.initSupertype (FIELD);
					type.declareType (fieldClass);
					fieldClass.initTypeLoader (ClassPath.get (scope));
					idField = fieldClass.declareField ("id", Member.PRIVATE | Member.FINAL, Type.INT);
					fcs = new TypeScope (scope, fieldClass, 0, scope.getCompiler (), scope.getAST ());
					MethodScope cs = fcs.createMethodScope (Compiler.MOD_CONSTRUCTOR);
					Expression e = new InvokeSpecial
						(Reflection.getDeclaredMethod
						 (FIELD, "m<init>;(" + NTYPE.getDescriptor ()
						  + Type.STRING.getDescriptor () + "I"
						  + Type.CLASS.getDescriptor ()
						  + Type.CLASS.getDescriptor () + "Z)V"))
						.add (cs.createThis ())
						.add (new GetField (typeField))
						.add (cs.declareParameter ("name", 0, Type.STRING).createGet ())
						.add (cs.declareParameter ("mods", 0, Type.INT).createGet ())
						.add (cs.declareParameter ("type", 0, Type.CLASS).createGet ())
						.add (cs.declareParameter ("ctype", 0, Type.CLASS).createGet ())
						.add (new BooleanConst (true));
					cs.addExpression (new SetThis ().add (e));
					cs.addExpression (new AssignField (idField, Assignment.SIMPLE)
									  .add (cs.createThis ())
									  .add (cs.declareParameter ("id", 0, Type.INT).createGet ()));
					constr = cs.createAndDeclareMethod ("<init>", Type.VOID);
				}
				Type ct = f.getType ();
				while (Reflection.isArray (ct))
				{
					ct = ct.getComponentType ();
				}
				m |= Reflection.isSuperclassOrSame (Node.$TYPE, ct)
					? NType.Field.FCO : NType.Field.SCO;
				if (ct == f.getType ())
				{
					ct = null;
				}
				Expression e = new InvokeSpecial (constr)
					.add (new New (fieldClass))
					.add (new ObjectConst (f.getName (), Type.STRING))
					.add (new IntConst (m))
					.add (new ClassConst (f.getType ()))
					.add ((ct == null) ? (Expression) new ObjectConst (null, Type.CLASS) : new ClassConst (ct))
					.add (new IntConst (i));
				staticInit.add
					(new InvokeVirtual (Reflection.getDeclaredMethod (MTYPE, "addManagedField"))
					 .add (new GetField (typeField))
					 .add (e));
				
				int tid = f.getType ().getTypeId ();
				if (getterMethods[tid] == null)
				{
					MethodScope ms = fcs.createMethodScope (Member.PUBLIC);
					getterMethods[tid] = ms;
					getterObjects[tid] = ms.declareParameter ("o", 0, Type.OBJECT);
					ms.createAndDeclareMethod ("get" + Reflection.getTypeSuffix (tid),
											   Reflection.getType (tid));
					getterSwitches[tid] = new Switch ();
					getterLabels[tid] = new Int2IntMap ();
					ms.addExpression (getterSwitches[tid].add (new GetField (idField).add (ms.createThis ())));
					ms.addExpression (new Return (ms, null)
									  .add (new InvokeSpecial (getSupermethod (ms))
											.add (ms.createThis ())
											.add (getterObjects[tid].createGet ())));

					ms = fcs.createMethodScope (Member.PUBLIC);
					setterMethods[tid] = ms;
					setterObjects[tid] = ms.declareParameter ("o", 0, Type.OBJECT);
					setterValues[tid] = ms.declareParameter ("v", 0, Reflection.getType (tid));
					ms.createAndDeclareMethod ("set" + Reflection.getTypeSuffix (tid)
											   + ((tid == TypeId.OBJECT) ? "Impl" : ""),
											   Type.VOID);
					setterSwitches[tid] = new Switch ();
					setterLabels[tid] = new Int2IntMap ();
					ms.addExpression (setterSwitches[tid].add (new GetField (idField).add (ms.createThis ())));
					ms.addExpression (new InvokeSpecial (getSupermethod (ms))
									  .add (ms.createThis ())
									  .add (setterObjects[tid].createGet ())
									  .add (setterValues[tid].createGet ()));
				}
				e = scope.getCompiler ().compileFieldExpression
					(f, new Cast (type).add (getterObjects[tid].createGet ()),
					 getterMethods[tid], scope.getAST ());
				getterLabels[tid].put (i, getterSwitches[tid].getExpressionCount ());
				getterSwitches[tid].add (new Return (getterMethods[tid], null).add (e));

				e = scope.getCompiler ().compileFieldExpression
					(f, new Cast (type).add (setterObjects[tid].createGet ()),
					 setterMethods[tid], scope.getAST ());
				setterLabels[tid].put (i, setterSwitches[tid].getExpressionCount ());
				setterSwitches[tid]
					.add (((Variable) e).toAssignment (Assignment.SIMPLE)
						  .add (new Cast (f.getType ()).add (setterValues[tid].createGet ())))
					.add (new Return (setterMethods[tid], null));
			}
		}

		if (fieldClass != null)
		{
			for (int i = 0; i < TypeId.TYPE_COUNT; i++)
			{
				if (getterLabels[i] != null)
				{
					getterSwitches[i].initialize (getterLabels[i], -1);
					setterSwitches[i].initialize (setterLabels[i], -1);
				}
			}

			scope.getCompiler ().finish (fcs, null);
		}
		staticInit.add
			(new InvokeVirtual (Reflection.getDeclaredMethod (MTYPE, "validate"))
			 .add (new GetField (typeField)));
	}

	public void postprocess (TypeScope scope, int run)
	{
		switch (run)
		{
			case Compiler.COMPILATION:
				scope.staticInit.addExpression (inits.get (scope));
				break;
			case Compiler.METHOD_DECLARATION:
				if ((scope.getModifiersEx () & (Compiler.MOD_MODULE | Compiler.MOD_INSTANTIATOR))
					 == (Compiler.MOD_MODULE | Compiler.MOD_INSTANTIATOR))
				{
					MethodScope ms = scope.createMethodScope (Member.PUBLIC);
					ms.createAndDeclareMethod ("getInstantiator", ClassAdapter.wrap (de.grogra.graph.Instantiator.class));
					methods.put (scope, ms);
				}
				break;
		}
	}

}
