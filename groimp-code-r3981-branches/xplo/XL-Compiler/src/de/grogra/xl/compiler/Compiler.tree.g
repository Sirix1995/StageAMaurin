header
{

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

import java.util.List;
import java.lang.reflect.Array;

import de.grogra.util.*;
import de.grogra.xl.expr.*;
import de.grogra.xl.query.*;
import de.grogra.xl.util.*;
import de.grogra.xl.lang.*;
import de.grogra.reflect.*;
import de.grogra.reflect.Method;
import de.grogra.reflect.Field;
import de.grogra.grammar.ASTWithToken;
import de.grogra.grammar.RecognitionExceptionList;
import de.grogra.xl.compiler.scope.*;
import de.grogra.xl.compiler.pattern.*;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.Package;
import de.grogra.xl.modules.Instantiator;
import de.grogra.xl.property.CompiletimeModel.Property;
}


class Compiler extends TreeParser;


options
{
	importVocab = Tokenizer;
	defaultErrorHandler = false;
	classHeaderPrefix = "/*";
	classHeaderSuffix = "*/ public class Compiler extends CompilerBase";
}

{
	private boolean termListContainsEmpty;
	private Member declaredVariable;

	private Block shellBlock = null;
	private BlockScope shellBlockScope = null;

	private CompiletimeModel currentQueryModel;

	@Override
	protected CompilationUnitScope compile
		(ClassPath classPath, AST tree, String source,
		 Scope defaultImports, Annotation[] annotations, CClass shell)
		throws RecognitionException
	{
		return compilationUnit (tree, classPath, defaultImports, annotations, source, shell);
	}

	void clearTemporaries ()
	{
		super.clearTemporaries ();
		declaredVariable = null;
	}

}


compilationUnit[ClassPath cpath, Scope imports, Annotation[] annotations, String source, CClass shell]
	returns [CompilationUnitScope s]
	{
		this.shell = shell;
		s = null;
	}
	:	#(COMPILATION_UNIT
			(#(PACKAGE pid:name))?
			{
				if (run == TYPE_AND_FIELD_DECLARATION)
				{
					if (pid != null)
					{
						currentPackage = cpath.getPackage (qualifiedName (pid), true);
					}
					else
					{
						currentPackage = cpath.getPackage ("", true);
					}
					s = new CompilationUnitScope (imports, source, this);
					s.getRoot ().insert (currentPackage);
					s.setAnnotations (annotations);
					units.put (#compilationUnit, s);
				}
				else
				{
					s = units.get (#compilationUnit);
					currentPackage = s.getPackage ();
				}
				currentCompilationUnitScope = s;
			}
			(importDecl[s])*
			(	#(m:MODULE classDecl[s, TOP_LEVEL_MODULE_MODIFIERS, MOD_MODULE, m])
			|	#(c:CLASS classDecl[s, Type.TOP_LEVEL_CLASS_MODIFIERS | MOD_STATIC_MEMBER_CLASSES, 0, c])
			|	#(i:INTERFACE classDecl[s, Type.TOP_LEVEL_INTERFACE_MODIFIERS, Member.ABSTRACT | Member.INTERFACE, i])
			)*
		)
	;


importDecl[final CompilationUnitScope scope]
	{
		final Package curPkg = currentPackage;
	}
	:   #(IMPORT_ON_DEMAND iod:name)
		{
			if (run == TYPE_AND_FIELD_DECLARATION)
			{
				final AST a = iod;
				toResolveBeforeMethodDeclaration.add (0, new Resolvable ()
				{
					public void resolve ()
					{
						currentPackage = curPkg;
						Member m = resolver.resolveCanonicalTypeOrPackageName (a, curPkg);
						if (m != null)
						{
							if (m instanceof Type)
							{
								scope.insert (new TypeImportOnDemand (null, (Type) m));
							}
							else
							{
								scope.insert (new PackageImportOnDemand (null, (Package) m));
							}
						}
					}
				});
			}
		}
	|   #(STATIC_IMPORT_ON_DEMAND siodType:name)
		{
			if (run == TYPE_AND_FIELD_DECLARATION)
			{
				final AST a = siodType;
				toResolveBeforeMethodDeclaration.add (0, new Resolvable ()
				{
					public void resolve ()
					{
						currentPackage = curPkg;
						Type t = resolver.resolveCanonicalTypeName (a, curPkg);
						if (Reflection.isInvalid (t))
						{
							return;
						}
						scope.insert (new StaticImportOnDemand (null, t));
					}
				});
			}
		}
	|   #(SINGLE_TYPE_IMPORT sti:name)
		{
			if (run == TYPE_AND_FIELD_DECLARATION)
			{
				final AST a = sti;
				toResolveBeforeMethodDeclaration.add (0, new Resolvable ()
				{
					public void resolve ()
					{
						currentPackage = curPkg;
						Type t = resolver.resolveCanonicalTypeName (a, curPkg);
						if (Reflection.isInvalid (t))
						{
							return;
						}
						if (checkTopLevelType (simpleNameAST (a), t, scope))
						{
							scope.insert (new SingleTypeImport (null, t));
						}
					}
				});
			}
		}
	|   #(SINGLE_STATIC_IMPORT ssiType:name member:IDENT)
		{
			if (run == TYPE_AND_FIELD_DECLARATION)
			{
				final AST a = ssiType;
				final AST mb = member;
				toResolveBeforeMethodDeclaration.add (0, new Resolvable ()
				{
					public void resolve ()
					{
						currentPackage = curPkg;
						Type t = resolver.resolveCanonicalTypeName (a, curPkg);
						if (Reflection.isInvalid (t))
						{
							return;
						}
						Member m = resolver.resolveStaticMember (t, mb, curPkg);
						if ((m != null) && (!(m instanceof Type)
											|| checkTopLevelType (mb, t, scope)))
						{
							scope.insert (new SingleStaticImport (null, t, mb.getText ()));
						}
					}
				});
			}
		}
	;


classDecl[Scope scope, long allowedMods, long implMods, AST root]
	{
		TypeScope s = null;
		long m, pm;
		Type[] extType = Type.TYPE_0, implType = Type.TYPE_0;
		ClassInfo info = null;
		boolean iface = root.getType () == INTERFACE;
		boolean module = root.getType () == MODULE;
		boolean isShell = (shell != null) && (scope instanceof CompilationUnitScope);
		TypeScope old = currentTypeScope;
		int inheritedCount = 0;
		Expression inst;
		Expression init = null;
		Type t;

		long[] mods = null;
		Type[] types = Type.TYPE_0;
		AST[] pids = null;
		List<Annotation> fieldAnnot = null;
		boolean[] inherited = null;
		AST[] methods = null;

		Expression[] list = null;
		MethodScope modCtorScope = null;
		ProduceScope prodScope = null;
		int i = 0;
		
		if (run > TYPE_AND_FIELD_DECLARATION)
		{
			info = (ClassInfo) currentCompilationUnitScope.properties.get (root);
			s = info.scope;
			modCtorScope = info.moduleConstructorScope;
		}
	}
	:	m=modifiers[allowedMods, implMods, Member.FINAL,
					(s != null) ? s.getDeclaredType ().getDeclaredAnnotations () : null,
					scope]
		id:IDENT
		(#(EXTENDS extType=ext:classList[scope]))?
		(#(IMPLEMENTS implType=impl:classList[scope]))?
		(#(params:PARAMETERS
			{
				assert module;
				pids = new AST[params.getNumberOfChildren ()];
				mods = new long[pids.length];
				types = new Type[pids.length];
				inherited = new boolean[pids.length];
				methods = new AST[pids.length];
				i = 0;
			}
			(   #(pd:PARAMETER_DEF
					(   {
							if ((info != null) && (info.moduleFields[i] != null))
							{
								fieldAnnot = ((XField) info.moduleFields[i]).getDeclaredAnnotations ();
								if (fieldAnnot.isEmpty ())
								{
									fieldAnnot.add (new AnnotationImpl (de.grogra.annotation.Editable.class));
								}
							}
						}
						pm=modifiers[Member.FINAL, 0, 0, fieldAnnot, scope] t=typeSpec[scope]
						{types[i] = t; mods[i] = pm;}
					|   SUPER {inherited[i] = true; inheritedCount++;}
					)
					pid:IDENT
					(method:IDENT {methods[i] = method;})?
				)
				{pids[i++] = pid;}
			)*
		))?
		{
			if (run == TYPE_AND_FIELD_DECLARATION)
			{
				if (!isShell)
				{
					checkMember (scope, m, root);
				}
				s = createAndDeclareType (scope, m, id,
										  ext, extType, impl, implType);
				info = new ClassInfo ();
				info.node = id;
				info.scope = s;
				if (module)
				{
					info.moduleFields = new Field[types.length];
					CClass mod = s.getDeclaredType ();
					for (i = 0; i < types.length; i++)
					{
						if (!inherited[i] && (methods[i] == null))
						{
							info.moduleFields[i] = mod.declareField (pids[i].getText (), Member.PUBLIC, types[i]);
							s.setASTOfDeclaration (info.moduleFields[i], params);
						}
					}
					AST pred = new ASTWithToken ();
					pred.setText ("Pattern");
					info.predicateScope = createAndDeclareType
						(s, Member.PUBLIC | Member.STATIC | Member.FINAL, pred,
						 id, new Type[] {ClassAdapter.wrap (de.grogra.xl.query.AttributeListPattern.class)},
						 null, Type.TYPE_0);
				}
				currentCompilationUnitScope.properties.put (root, info);
			} else if (run == COMPILATION)
			{
				checkForUnimplementedMethod(id, s);
			}
			currentTypeScope = s;
			if (module && (run == METHOD_DECLARATION))
			{
				initModuleSuperclass (s);
				modCtorScope = s.createMethodScope (Member.PUBLIC | MOD_CONSTRUCTOR);
				for (i = 0; i < types.length; i++)
				{
					Type st = s.getDeclaredType ().getSupertype ();
					if (inherited[i])
					{
						if (Reflection.isInvalid (st))
						{
							types[i] = Type.INVALID;
						}
						else
						{
							Field f = resolver.resolveField
								(modCtorScope, s.getDeclaredType (),
								 pids[i], Members.INSTANCE_ONLY | Members.SUPER);
							info.moduleFields[i] = f;
							types[i] = (f != null) ? f.getType () : Type.INVALID;
						}
					}
					else if ((methods[i] == null) && problems.isWarning (ProblemReporter.WARN_ON_HIDDEN_FIELDS))
					{
						Field f = Reflection.findFieldInClasses (st, pids[i].getText ());
						if (f != null)
						{
							problems.addSemanticWarning (I18N.msg (ProblemReporter.DECLARATION_HIDES_FIELD, pids[i].getText(), f.getDeclaringType ().getName ()), pids[i]);
						}
					}
					declareParameter (modCtorScope, pids[i], mods[i], types[i]);
				}
				info.moduleConstructorScope = modCtorScope;
				modCtorScope.createAndDeclareMethod ("<init>", Type.VOID);
			}
			if (!(iface || isShell))
			{
				process (s, false);
			}
			if (module)
			{
				process (info.predicateScope, false);
			}
		}

		(   {run == COMPILATION}? list=args:arglist[modCtorScope]
		|	ARGLIST
		|
			{
				if (module && (run == COMPILATION))
				{
					list = new Expression[inheritedCount];
					inheritedCount = 0;
					for (i = 0; i < types.length; i++)
					{
						if (inherited[i])
						{
							list[inheritedCount++]
								= modCtorScope.getParameter (i).createGet ();
						}
					}
				}
			}
		)

		(   {run == COMPILATION}? init=initBlock:slist[modCtorScope]
		|	SLIST
		|
		)

		{
			if (module)
			{
				if (run == METHOD_DECLARATION)
				{
					createModuleMethods (info, types);
				}
				else if (run == COMPILATION)
				{
					modCtorScope.addExpression
						(new SetThis ()
						 .add (compileSuperConstructorInvocation
							   (modCtorScope.createThis (), null, list, modCtorScope, (args != null) ? args : params)));
					compileModuleMethods (info, types, inherited, methods);
					modCtorScope.addExpression (init);
					modCtorScope.addExpression (new InvokeSpecial (s.instanceInit.getMethod ()).add (modCtorScope.createThis ()));
				}
			}
		}

		(classMember[s, iface])*
		(#(instPos:INSTANTIATOR
			(	{run == COMPILATION}?
				{
					Expression e = info.instState.createGet ();
					e = compileMethodInvocation (e, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, instPos);
					prodScope = createProduceScope (info.instMethodScope, info.instMethodScope.getBlock (), e, instPos);
				}
				inst=slist[prodScope]
				{
					prodScope.addExpression (inst);
					Expression e = prodScope.createExpression (prodScope, instPos);
					e = compileMethodInvocation (e, PRODUCER_END, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, prodScope, instPos);
					prodScope.addExpression (e);
					inst = prodScope.getBlock ();
					MethodScope is = info.instMethodScope;
					is.addExpression (inst);
					MethodScope iis = info.instIfaceMethodScope;
				compileInstantiate:
					if (iis != null)
					{
						Type ist = s.getInstantiationProducerType ();
						Expression[] a = new Expression[types.length + 1];
						a[0] = castingConversion (info.instIfaceState.createGet (),
										 ist, scope, id);
						for (i = 0; i < types.length; i++)
						{
							if (info.moduleFields[i] != null)
							{
								a[i + 1] = new GetField (info.moduleFields[i]).add (iis.createThis ());
							}
							else if (info.parameterGetters[i] != null)
							{
								a[i + 1] = compileMethodInvocation
									(info.parameterGetters[i], iis.createThis (),
									 ArgumentTransformations.NO_IMPLICIT_ARGS, Members.Applicability.DEFAULT,
									 Expression.EXPR_0, iis, methods[i]);
							}
							else
							{
								break compileInstantiate;
							}
						}
						iis.addExpression
							(compileMethodInvocation
							 (iis.createThis (), "instantiate",
							  ArgumentTransformations.NO_IMPLICIT_ARGS, a, 0,
							  iis, id));
					}
				}
			|	sl:SLIST
				{
					assert module;
					if (run == TYPE_AND_FIELD_DECLARATION)
					{
						s.setInstantiatorModule ();
						s.getDeclaredType ().addInterface (ClassAdapter.wrap (Instantiator.class));
					}
					else if (run == METHOD_DECLARATION)
					{
						Type ist = s.getInstantiationProducerType ();
						if (ist == null)
						{
							problems.addSemanticError
								(I18N.msg (ProblemReporter.NO_INSTANTIATION_PRODUCER_TYPE), sl);
							ist = Type.INVALID;
						}
						MethodScope is = s.createMethodScope (Member.PUBLIC);
						info.instState = is.declareParameter ("state.", 0, ist);
						for (i = 0; i < types.length; i++)
						{
							declareParameter (is, pids[i], 0, types[i]);
						}
						info.instMethodScope = is;
						is.createAndDeclareMethod ("instantiate", Type.VOID);
						if ((ist != Type.INVALID)
							&& ((types.length > 0) || !Reflection.equal (ist, Type.OBJECT)))
						{
							MethodScope iis = s.createMethodScope (Member.PUBLIC);
							info.instIfaceState = iis.declareParameter ("state", 0, Type.OBJECT);
							info.instIfaceMethodScope = iis;
							iis.createAndDeclareMethod ("instantiate", Type.VOID);
						}
					}
				}
			)
		))?
		{
			if (run == METHOD_DECLARATION)
			{
				if (!(iface || isShell))
				{
					createDefaultConstructor
						(info,
						 Reflection.getDeclaredMethod (s.getDeclaredType (), "<init>") == null);
				}
			}
			else if (run == COMPILATION)
			{
				if (!(iface || isShell))
				{
					MethodScope dcs = info.defaultConstructorScope;
					if (dcs != null)
					{
						if (dcs.enclosingInstance != null)
						{
							dcs.addExpression
								(new AssignField (s.enclosingInstance,
													  Assignment.SIMPLE)
								 .add (dcs.createThis ())
								 .add (dcs.enclosingInstance.createGet ()));
						}
						dcs.addExpression
							(new SetThis ()
							 .add (compileSuperConstructorInvocation
								   (dcs.createThis (), null, Expression.EXPR_0,
							    	dcs, id)));
						dcs.addExpression (new InvokeSpecial (s.instanceInit.getMethod ()).add (dcs.createThis ()));
					}
				}
			}
			if (!(iface || isShell))
			{
				process (s, true);
			}
			if (module)
			{
				process (info.predicateScope, true);
			}
			if (run == COMPILATION)
			{
				finish (s, id);
				if (module)
				{
					finish (info.predicateScope, id);
				}
			}
			currentTypeScope = old;
		}
	;


extendsClause[Scope scope] returns [Type[] types]
	{
		types = null;
		int i = 0;
		Type t;
	}
	:	#(EXTENDS {types = new Type[#extendsClause.getNumberOfChildren ()];}
			(t=classType[scope] { types[i++] = t; } )*
		)
	|   {types = Type.TYPE_0;}
	;


classList[Scope scope] returns [Type[] types]
	{
		int i = 0;
		for (AST a = _t; a != null; a = a.getNextSibling ())
		{
			i++;
		}
		types = new Type[i];
		i = 0;
		Type t;
	}
	:	(t=classType[scope] {types[i++] = t;})*
	;


anonymousClass[BlockScope scope, Type type, AST typeAST, Expression qualifier,
			   Expression[] args, AST root] returns [Expression e]
	{
		if (type == null)
		{
			type = Type.INVALID;
		}
		e = null;
		boolean iface = (qualifier == null) && Reflection.isInterface (type);
		TypeScope old = currentTypeScope;
		TypeScope s = createAndDeclareType
			(scope, scope.isStatic () ? Member.STATIC | Member.FINAL : Member.FINAL,
			 null, typeAST, new Type[] {iface ? getDefaultSuperclass () : type},
			 iface ? typeAST : null,
			 iface ? new Type[] {type} : Type.TYPE_0);
		currentTypeScope = s;
		MethodScope cs = s.createMethodScope (MOD_CONSTRUCTOR);
		CClass t = s.getDeclaredType ();
		Expression q = (qualifier == null) ? null
			: cs.declareParameter ("this$1", Member.FINAL, qualifier.getType ())
				.createGet ();
		Expression[] exprs;
		if (args != null)
		{
			exprs = new Expression[args.length];
			for (int i = 0; i < args.length; i++)
			{
				exprs[i] = new Expression (args[i].getType ());
			}
		}
		else
		{
			exprs = null;
		}
		Expression sc = compileSuperConstructorInvocation
		   (cs.createThis (), q, exprs, cs, typeAST);
		if ((exprs != null) && (exprs.length > 0))
		{
			exprs[0].removeAll (null);
		}
		setLocalRun (TYPE_AND_FIELD_DECLARATION);
	}
	:	#(c:CLASS anonymousClassImpl[s])
	{
		AST a = c.getFirstChild ();
		process (s, false);
		process (s, true);
		setLocalRun (METHOD_DECLARATION);
		anonymousClassImpl (a, s);
		if (args != null)
		{
			if (sc instanceof Invoke)
			{
				Method m = ((Invoke) sc).getOriginalMethod ();
				int ofs = m.getParameterCount () - args.length;
				for (int i = 0; i < args.length; i++)
				{
					sc.add (cs.declareParameter ("arg" + i + '.', Member.FINAL, m.getParameterType (ofs + i))
							.createGet ());
				}
			}
			else
			{
				for (int i = 0; i < args.length; i++)
				{
					cs.declareParameter ("arg" + i + '.', Member.FINAL, args[i].getType ());
				}
			}
		}
		cs.createAndDeclareMethod ("<init>", Type.VOID);
		process (s, false);
		process (s, true);
		setLocalRun (COMPILATION);
		process (s, false);
		anonymousClassImpl (a, s);
		if (Reflection.isInvalid (type) || (args == null))
		{
			e = new Expression (t);
		}
		else
		{
			if (cs.enclosingInstance != null)
			{
				cs.addExpression
					(new AssignField (s.enclosingInstance,
										  Assignment.SIMPLE)
					 .add (cs.createThis ())
					 .add (cs.enclosingInstance.createGet ()));
			}
			cs.addExpression (new SetThis ().add (sc));
			cs.addExpression (new InvokeSpecial (s.instanceInit.getMethod ()).add (cs.createThis ()));
			process (s, true);
			finish (s, typeAST);
			if (qualifier != null)
			{
				System.arraycopy (args, 0, args = new Expression[args.length + 1], 1,
								  args.length - 1);
				args[0] = qualifier;
			}
			e = compileConstructorInvocation
				(new New (t), null, args, scope, typeAST, false);
		}
		currentTypeScope = old;
	}
	;


anonymousClassImpl[TypeScope scope]
	:	(classMember[scope, false])*
	;


classMember[TypeScope scope, boolean iface]
	{
		Expression e = null;
	}
	:	#(c:CLASS classDecl[scope, Type.MEMBER_CLASS_MODIFIERS | MOD_STATIC_MEMBER_CLASSES,
							iface ? Member.PUBLIC | Member.STATIC
							: ((scope.getModifiersEx () & MOD_STATIC_MEMBER_CLASSES) != 0) ? Member.STATIC : 0, c])
	|	#(m:MODULE classDecl[scope, MEMBER_MODULE_MODIFIERS,
							 iface ? Member.PUBLIC | Member.STATIC | MOD_MODULE : Member.STATIC | MOD_MODULE, m])
	|	#(i:INTERFACE classDecl[scope, Type.MEMBER_INTERFACE_MODIFIERS,
								iface ? Member.PUBLIC | Member.STATIC | Member.ABSTRACT | Member.INTERFACE
								: Member.STATIC | Member.ABSTRACT | Member.INTERFACE, i])
	|	e=variableDecl[scope]
		{
			(Reflection.isStatic (declaredVariable) ? scope.staticInit : scope.instanceInit)
				.addExpression (e);
		}
	|	methodDecl[scope, iface]
	|	constructorDecl[scope]
	|	instanceInit[scope]
	|	staticInit[scope]
	;


parameterList[TypeScope ts, MethodScope ms, AST name]
	{
		long m;
		Type t;
		
		// check if the method is static
//		boolean isStatic = ms.isStatic ();
		
		// true if all parameters are primitive
//		boolean isPrimitive = isStatic;
		
		// count the number of parameters
		int paramCount = 0;
		ObjectList<Annotation> annots = null;
	}
	:	(#(PARAMETER_DEF
			{
				if (run == METHOD_DECLARATION)
				{
					annots = new ObjectList<Annotation> (0);
				}
				else if (run == COMPILATION)
				{
					annots = ms.getParameter (paramCount).getDeclaredAnnotations ();
				}
			}
			m=modifiers[Member.FINAL, 0, 0, annots, ts]
			t=typeSpec[ts] id:IDENT)
			{
				if (run == METHOD_DECLARATION)
				{
					declareParameter (ms, id, m, t).setAnnotations (annots);
//					if (!Reflection.isPrimitive (t))
//					{
//						isPrimitive = false;
//					}
				}
				paramCount++;
			}
		)*
		{
			// check if this function is unary or binary
//			boolean isBinary = true;

			// operator overloading is only possible if at least
			// one operand is not of a primitive type
//			if ((run == METHOD_DECLARATION)
//				&& isOperatorFunction (name.getText ())
//				&& (
					// non-static function have an extra parameter
					// check if paramCount plus the optional parameter
					// is two for binary and one for unary functions
//					((paramCount + (isStatic?0:1)) != (isBinary?2:1))
//					|| isPrimitive
//				)
//			)
//			{
//				problems.addSemanticError
//					(I18N.msg (ProblemReporter.ILLEGAL_OPERATOR_OVERLOAD), name);
//			}
		}
	;


throwsList[MethodScope ms]
	{
		Type t;
	}
	:	(	t=c:classType[ms]
			{
				if (run == METHOD_DECLARATION)
				{
					if (!Reflection.isInvalid (t))
					{
						ms.declareException (t);
					}
				}
				else if (run == COMPILATION)
				{
					if (!Reflection.isSuperclassOrSame (Throwable.class, t))
					{
						problems.addSemanticError
							(I18N.msg (ProblemReporter.UNEXPECTED_TYPE, t, "java.lang.Throwable"), c);
					}
				}
			}
		)*
	;


methodDecl[TypeScope scope, boolean iface]
	{
		AST root = _t;
		Expression st = null;
		long mods;
		Type retType;
		MethodScope s = (MethodScope) currentCompilationUnitScope.properties.get (root);
	}
	:	#(METHOD mods=modifiers[iface ? Method.INTERFACE_MODIFIERS | MOD_ITERATING
								: Method.MODIFIERS | MOD_ITERATING,
								iface ? Member.PUBLIC | Member.ABSTRACT : 0,
								ABSTRACT_METHOD_INCOMPATIBLE,
								(run == COMPILATION) ? s.getMethod ().getDeclaredAnnotations () : null, scope]
			retType=typeSpec[scope]
			id:IDENT
			{
				if (run == METHOD_DECLARATION)
				{
					checkMember (scope, mods, id);
					if (((mods & Member.ABSTRACT) != 0)
						&& ((scope.getModifiersEx () & Member.ABSTRACT) == 0))
					{
						problems.addSemanticError (I18N,
							ProblemReporter.ABSTRACT_IN_CONCRETE, id);
					}
					s = scope.createMethodScope (mods);
					if ((mods & MOD_ITERATING) != 0)
					{
						s.consumer = s.declareParameter
							("consumer.", Member.FINAL,
							 XMethod.getConsumerType (retType.getTypeId ()));
						if (Reflection.isPrimitive (retType)
							|| "evaluateObject".equals (id.getText ()))
						{
							retType = Type.VOID;
						}
					}
					currentCompilationUnitScope.properties.put (root, s);
				}
			}
			#(PARAMETERS parameterList[scope, s, id])
			#(THROWS throwsList[s])
			(	SEMI
			|	{run == COMPILATION}? st=slist[s]
			|	SLIST
			)
			{
				if (run == METHOD_DECLARATION)
				{
					XMethod m = s.createAndDeclareMethod (id.getText (), retType);
					String nrd = m.noRetDescriptor;
					CClass c = scope.getDeclaredType ();
					for (int i = c.getDeclaredMethodCount () - 1; i >= 0; i--)
					{
						if ((c.getDeclaredMethod (i) != m)
							&& c.getDeclaredMethod (i).getDescriptor ().startsWith (nrd))
						{
							problems.addSemanticError
								(I18N.msg (ProblemReporter.DUPLICATE_METHOD, id.getText ()), id);
							break;
						}
					}
				}
				if (run == COMPILATION)
				{
					problems.checkMethodDeclaration (id, s.getMethod ());
					s.addExpression (st);
				}
			}
		)
	;


constructorDecl[TypeScope scope]
	{
		AST root = _t;
		Expression st = null;
		long mods;
		MethodScope s = (MethodScope) currentCompilationUnitScope.properties.get (root);
	}
	:	#(CONSTRUCTOR mods=modifiers[Method.CONSTRUCTOR_MODIFIERS, 0, 0, (run == COMPILATION) ? s.getMethod ().getDeclaredAnnotations () : null, scope]
			id:IDENT
			{
				if (run == METHOD_DECLARATION)
				{
					if (!id.getText ().equals (scope.getDeclaredType ().getSimpleName ()))
					{
						problems.addSemanticError
							(I18N.msg (ProblemReporter.MISMATCHED_CONSTRUCTOR_NAME, id.getText (),
									   scope.getDeclaredType ().getSimpleName ()),
							 id);
					}
					s = scope.createMethodScope (mods | MOD_CONSTRUCTOR);
					currentCompilationUnitScope.properties.put (root, s);
				}
			}
			#(PARAMETERS parameterList[scope, s, id])
			#(THROWS throwsList[s])
			(	{run == COMPILATION}? st=slist[s]
			|	SLIST
			)
			{
				if (run == METHOD_DECLARATION)
				{
					CClass c = scope.getDeclaredType ();
					XMethod m = s.createAndDeclareMethod ("<init>", Type.VOID);
					for (int i = c.getDeclaredMethodCount () - 1; i >= 0; i--)
					{
						if ((c.getDeclaredMethod (i) != m)
							&& c.getDeclaredMethod (i).getDescriptor ().equals
								(m.getDescriptor ()))
						{
							problems.addSemanticError
								(I18N, ProblemReporter.DUPLICATE_CONSTRUCTOR, id);
							break;
						}
					}
				}
				else if (run == COMPILATION)
				{
					s.addExpression (st);
				}
			}
		)
	;


variableDecl[Scope scope] returns [Expression e]
	{
		declaredVariable = null;
		e = null;
		Type t;
		Local l = null;
		BlockScope s = null;
		long allowedMods, implicitMods = 0, m;
		boolean local = scope instanceof BlockScope;
		AST var = _t;
		XField f = (XField) currentCompilationUnitScope.properties.get (var);
		if (local)
		{
			allowedMods = Member.FINAL;
		}
		else
		{
			if (Reflection.isInterface (scope.getDeclaredType ()))
			{
				allowedMods = implicitMods = Field.INTERFACE_MODIFIERS
					| MOD_CONST;
			}
			else
			{
				allowedMods = Field.MODIFIERS | MOD_CONST;
			}
		}
	}
	:	#(VARIABLE_DEF m=modifiers[allowedMods, implicitMods, 0, (f != null) ? f.getDeclaredAnnotations () : null, scope]
			t=typeSpec[scope] id:IDENT
			{
				if (local)
				{
					s = (BlockScope) scope;
					l = declareLocal (s, id, (int) m, t);
				}
				else
				{
					MethodScope ms = ((m & Member.STATIC) == 0) ? ((TypeScope) scope).instanceInit
						: ((TypeScope) scope).staticInit;
					s = ms;
					ms.ast = var;
					if (run == TYPE_AND_FIELD_DECLARATION)
					{
						CClass c = ((TypeScope) scope).getDeclaredType ();
						if (c.getDeclaredField (id.getText ()) != null)
						{
							problems.addSemanticError
								(I18N.msg (ProblemReporter.DUPLICATE_FIELD, id.getText ()), id);
						}
						f = c.declareField (id.getText (), (int) m, t);
						((TypeScope) scope).setASTOfDeclaration (f, var);
						currentCompilationUnitScope.properties.put (var, f);
					}
				}
			}
			({l != null}? #(ASSIGN e=v:initializer[s, t])
			|{!local}? #(ASSIGN e=v2:fieldInitializer[(MethodScope) s, f])
				{
					v = v2;
				}
			|	ASSIGN
			|
				{
					if ((run == COMPILATION) && (l != null) && (shell != null) && ((m & Member.FINAL) == 0))
					{
						v = var;
						e = Expression.createConst (t, null);
					}
				}
			)
		)
		{
			declaredVariable = local ? (Member) l : f;
			if (t != null)
			{
				if ((run == COMPILATION) && (e != null))
				{
					e = assignmentConversion (e, t, scope, v);
				}
				if (local)
				{
					if (e != null)
					{
						if (((m & Member.FINAL) != 0) && (e instanceof Constant))
						{
							l.setConstant (e.evaluateAsObject (null));
							e = null;
						}
						else
						{
							e = compileAssignment
								(l.createGet (), e, id, v, scope);
						}
					}
				}
				else
				{
					if (run == COMPILATION)
					{
						if (e != null)  
						{
							e = compileAssignment
							   (compileFieldExpression
								(f, ((m & Member.STATIC) != 0) ? null
								 : s.createThis (), s, id), e, id, v, scope);
						}
						if ((f.getModifiers () & Member.CONSTANT) == 0)
						{
							checkMember (scope, m, var);
						}
					}
				}
			}
		}
	;


typeSpec[Scope scope] returns [Type t]
	{ t = null; }
	:	#(ARRAY_DECLARATOR t=typeSpec[scope])
		{
			final Type t2 = t;
			t = new TypeDecorator (null)
			{
				private Type arrayType;
				private String descr;

				@Override				
				public int getModifiers ()
				{
					return Member.PUBLIC | Member.FINAL | Member.ARRAY;
				}

				@Override				
				public Member getDecoratedMember ()
				{
					if (arrayType == null)
					{
						arrayType = t2.getArrayType ();
					}
					return arrayType;
				}

				@Override				
				public String getBinaryName ()
				{
					return getDescriptor ();
				}
	
				@Override				
				public synchronized String getDescriptor ()
				{
					if (descr == null)
					{
						descr = '[' + t2.getDescriptor ();
					}
					return descr;
				}
			};
		}
	|	t=classType[scope]
	|	t=builtInType
	;


classType[Scope scope] returns [Type t]
	{
		t = null;
	}
	:	id:name
		{
			if (scope != null)
			{
				if (run == TYPE_AND_FIELD_DECLARATION)
				{
					t = new UnresolvedType (this, id, scope);
					currentCompilationUnitScope.properties.put (id, t);
					toResolveBeforeMethodDeclaration.add (t);
				}
				else
				{
					t = (Type) currentCompilationUnitScope.properties.get (id);
					if (t == null)
					{
						t = resolver.resolveTypeName (id, scope);
						if (problems.isAddEnabled () || !Reflection.isInvalid (t))
						{
							currentCompilationUnitScope.properties.put (id, t);
						}
					}
				}
			}
		}
	|	DECLARING_TYPE
		{
			t = TypeScope.get (scope).getDeclaredType ();
		}
	;


builtInType returns [Type t]
	{ t = null; }
	:	VOID_ { t = Type.VOID; }
	|	BOOLEAN_ { t = Type.BOOLEAN; }
	|	BYTE_ { t = Type.BYTE; }
	|	SHORT_ { t = Type.SHORT; }
	|	CHAR_ { t = Type.CHAR; }
	|	INT_ { t = Type.INT; }
	|	LONG_ { t = Type.LONG; }
	|	FLOAT_ { t = Type.FLOAT; }
	|	DOUBLE_ { t = Type.DOUBLE; }
	;


instanceInit[TypeScope scope]
	{
		Expression e;
		scope.instanceInit.ast = _t;
	}
	:	#(INSTANCE_INIT
			({run == COMPILATION}? e=slist[scope.instanceInit]
				{scope.instanceInit.addExpression (e);}
			|	SLIST
			)
		)
	;


staticInit[TypeScope scope]
	{
		Expression e;
		scope.staticInit.ast = _t;
	}
	:	#(si:STATIC_INIT
			({run == COMPILATION}? e=slist[scope.staticInit]
				{
					scope.staticInit.addExpression (e);
					checkMember (scope, Member.STATIC, si);
				}
			|	SLIST
			)
		)
	;

graph[BlockScope scope] returns [Expression e]
	{
		e = null;
		AST root = _t;
	}
	:	EMPTY
		{
			currentQueryModel = scope.getQueryModel (this, root);
			if (currentQueryModel == null)
			{
				problems.addSemanticError (I18N, ProblemReporter.NO_QUERY_MODEL, root);
				currentQueryModel = InvalidQueryModel.INSTANCE;
			}
		}
	|	e=referenceExpr[scope, Graph.class]
		{
			currentQueryModel = InvalidQueryModel.INSTANCE;
		findModel:
			if ((e != null) && (e.getClass () != Expression.class))
			{
				for (Type t = e.getType (); t != null; t = t.getSupertype ())
				{
					Annotation<HasModel> a = getAnnotation (t, HasModel.class, true);
					if (a != null)
					{
						currentQueryModel = createModel (CompiletimeModel.class, (Type<?>) a.value ("value"), root);
						if (currentQueryModel == null)
						{
							currentQueryModel = InvalidQueryModel.INSTANCE;
						}
						break findModel;
					}
				}
				problems.addSemanticError (I18N.msg (ProblemReporter.NO_QUERY_MODEL_IN_GRAPH, e.getType ()), root);
			}
		}
	;

query[BlockScope scope, ExpressionFactory graph, CompiletimeModel model, boolean forProduction, boolean context] returns [Expression e]
	{
		BlockScope s;
		if (scope.useNewScopeForQueries ())
		{
			s = new BlockScope (scope, null);
		}
		else
		{
			s = scope;
		}
		s.setUseNewScopeForQueries (true);
		e = null;
		PatternBuilder pb = null;
		AST pos = _t;
		Expression qs;
		if (graph != null)
		{
			qs = graph.createExpression (scope, pos);
		}
		else
		{
			qs = new InvokeStatic (Reflection.getDeclaredMethod (ClassAdapter.wrap (Query.class), "currentGraph"))
					.add (new ModelExpression (model));
		}
		qs = compileMethodInvocation
			(qs, "createQueryState", ArgumentTransformations.NO_IMPLICIT_ARGS,
			 Expression.EXPR_0, 0, scope, pos);
		qs = new Cast (model.getQueryStateType ()).add (qs);
	}
	:	(	#(QUERY
				(	EMPTY
				|	qs=withBlock[scope, qs, pos]
				)
				pb=compositePattern[s, null, model, false, context]
			)
		|	EMPTY
			{
				pb = new PatternBuilder (model, null, s, pos);
				pb.allowOpenEnds ();
			}
		)
	{
		GetQuery get = new GetQuery (pb, forProduction);
		Type qt = forProduction ? pb.getModel ().getProducerType ()
			: (get.getPattern ().getOutParameter () < 0) ? Type.VOID
			: get.getPattern ().getParameterType (get.getPattern ().getOutParameter ());
		Invoke i = new InvokeVirtual
			(Reflection.getDeclaredMethod
			 (get.getType (),
			  "find" + Reflection.getTypeSuffix (qt.getTypeId ()) + "Matches"));
		i.setGenerator ();
		i.setType (qt);
		i.add (get);
		i.add (new GetVMXFrame ());
		i.add (qs);
		e = i;
		if (s != scope)
		{
			e = s.setBlock (e);
		}
		else
		{
			scope.setUseNewScopeForQueries (false);
		}
	}
	;


nestedCompositePattern[PatternBuilder pb]
	returns [PatternBuilder child]
	{
		BlockScope childScope = new BlockScope (pb.getScope (), null);
		childScope.setUseNewScopeForQueries (true);
	}
	:	child=cp:compositePattern[childScope, pb, pb.getModel (), true, false]
	;


compositePattern[BlockScope scope, PatternBuilder parent, CompiletimeModel model, boolean allowOpen, boolean context]
	returns [PatternBuilder builder]
	{
		AST root = _t;
		builder = new PatternBuilder (model, parent, scope, root);
		if (allowOpen)
		{
			builder.allowOpenEnds ();
		}
		if (context)
		{
			builder.beginContext (root);
		}
		Type t;
	}
	:	#(COMPOUND_PATTERN
			(	#(VARIABLE_DEF
					modifiers[Member.FINAL, Member.FINAL, 0, null, scope]
					t=typeSpec[scope]
					id:IDENT
				)
				{builder.declareVariable (id, t);}
			)*
			(predicate[builder, null])*
		)
	{
		if (context)
		{
			builder.endContext (root);
		}
	}
	;


predicate[PatternBuilder pb, AST label]
	{
		AST pos = _t;
		Type type;
		Expression e;
		Expression wrapped = null;
		BlockScope scope = pb.getScope ();
		Expression[] args;
		Object o;
		PatternBuilder child;
		TraversalData td;
	}
	:	#(LABEL
			lb:IDENT
			predicate[pb, lb]
		)
	|	#(PATTERN_WITH_BLOCK
			predicate[pb, label]
			e=block:slist[scope]
			{if (e != null) pb.addBlock (e, block);}
		)
	|	traversal[pb, label]
	|	#(MINIMAL td=traversal[pb, null] child=cp:nestedCompositePattern[pb])
		{
			CompositeData cd = pb.addComposite (label, child, EdgeDirection.FORWARD, false, cp);
			if (td != null)
			{
				cd.setBreak (td);
			}
		}
	|	#(LATE_MATCH child=nestedCompositePattern[pb])
		{
			pb.addComposite (label, child, EdgeDirection.FORWARD, false, pos).setBreak ();
		}
	|	#(SINGLE_MATCH child=nestedCompositePattern[pb])
		{
			pb.addComposite (label, child, EdgeDirection.FORWARD, false, pos).setBreak ();
		}
	|	#(OPTIONAL_MATCH child=nestedCompositePattern[pb])
		{
			pb.addComposite (label, child, EdgeDirection.FORWARD, true, pos);
		}
	|	#(SINGLE_OPTIONAL_MATCH child=nestedCompositePattern[pb])
		{
			pb.addComposite (label, child, EdgeDirection.FORWARD, true, pos);
		}
	|	ANY
			{pb.addAny (label, pos);}
	|	#(FOLDING
			predicate[pb, label]
			(	fid:IDENT
				{
					pb.addFolding (fid);
				}
			)+
		)
	|	SEPARATE
			{pb.addSeparation (pos);}
	|	LT
			{pb.addStandardEdge (label, EdgeDirection.BACKWARD, EdgePattern.SUCCESSOR_EDGE, pos);}
	|	GT
			{pb.addStandardEdge (label, EdgeDirection.FORWARD, EdgePattern.SUCCESSOR_EDGE, pos);}
	|	LINE
			{pb.addStandardEdge (label, EdgeDirection.UNDIRECTED, EdgePattern.SUCCESSOR_EDGE, pos);}
	|	LEFT_RIGHT_ARROW
			{pb.addStandardEdge (label, EdgeDirection.BOTH, EdgePattern.SUCCESSOR_EDGE, pos);}
	|	PLUS_LEFT_ARROW
			{pb.addStandardEdge (label, EdgeDirection.BACKWARD, EdgePattern.BRANCH_EDGE, pos);}
	|	PLUS_ARROW
			{pb.addStandardEdge (label, EdgeDirection.FORWARD, EdgePattern.BRANCH_EDGE, pos);}
	|	PLUS_LINE
			{pb.addStandardEdge (label, EdgeDirection.UNDIRECTED, EdgePattern.BRANCH_EDGE, pos);}
	|	PLUS_LEFT_RIGHT_ARROW
			{pb.addStandardEdge (label, EdgeDirection.BOTH, EdgePattern.BRANCH_EDGE, pos);}
	|	SLASH_LEFT_ARROW
			{pb.addStandardEdge (label, EdgeDirection.BACKWARD, EdgePattern.REFINEMENT_EDGE, pos);}
	|	SLASH_ARROW
			{pb.addStandardEdge (label, EdgeDirection.FORWARD, EdgePattern.REFINEMENT_EDGE, pos);}
	|	SLASH_LINE
			{pb.addStandardEdge (label, EdgeDirection.UNDIRECTED, EdgePattern.REFINEMENT_EDGE, pos);}
	|	SLASH_LEFT_RIGHT_ARROW
			{pb.addStandardEdge (label, EdgeDirection.BOTH, EdgePattern.REFINEMENT_EDGE, pos);}
	|	#(TYPE_PATTERN type=typeSpec[scope]
			{pb.addType (label, type, pos);}
		)
	|	#(WRAPPED_TYPE_PATTERN type=typeSpec[scope] wrapped=term[pb]
			{
				pb.addWrappedType (label, type, wrapped, pos);
			}
		)
	|	#(NAME_PATTERN
			id:name
			{
				Local l;
				if ((label == null) && (id.getType () == IDENT)
					&& ((l = scope.findLocal (id.getText (), true)) != null))
				{
					if (l.isVariable (pb))
					{
						pb.addVariableReference (l, pos);
					}
					else
					{
						if (l.wrapper != null)
						{
							l = l.wrapper;
						}
						addExpression (pb, l.createExpression (scope, id), label, pos);
					}
				}
				else if ((o = resolver.resolveExpressionOrPatternOrMethodOrTypeName (id, scope))
						 instanceof Method)
				{
					addMethodPattern (pb, label, id, Expression.EXPR_0, pos);
				}
				else if (o instanceof Expression)
				{
					addExpression (pb, (Expression) o, label, pos);
				}
				else if (o instanceof Type)
				{
					pb.addType (label, (Type) o, pos);
				}
				else if (o instanceof PatternWrapper)
				{
					compilePattern (id, Expression.EXPR_0, pos, pb, label);
				}
				else
				{
					pb.addAny (label, pos);
				}
			}
		)
	|	#(TREE
			{pb.beginTree (pos);}
			(predicate[pb, null])*
			{pb.endTree (pos);}
		)
	|	#(CONTEXT
			{pb.beginContext (pos);}
			(predicate[pb, null])*
			{pb.endContext (pos);}
		)
	|	#(EXPR e=expr[scope]
			{
				addExpression (pb, e, label, pos);
			}
		)
	|	ROOT
		{
			pb.addExpression
				(label, new Root (pb.getModel ().getNodeType (), pb.getQueryState ()),
				 pos);
		}
	|	#(METHOD_PATTERN
			#(METHOD_CALL #(DOT e=expr[scope] mcn:IDENT) args=arglist[scope])
			{
				if (e != null)
				{
					addMethodPattern (pb, label, e, mcn.getText (), args, pos);
				}
			}
		)
	|	#(APPLICATION_CONDITION e=cond:expr[scope]
			{
				pb.addCondition (label,
								 wideningConversion (e, Type.BOOLEAN, scope, cond),
								 pos);
			}
		)
	|	#(PARAMETERIZED_PATTERN predid:name args=tl:termList[pb]
			{
				int flags = Members.PREDICATE;
				int qflags = Members.TOP_LEVEL_PACKAGE | Members.TYPE;
				if (!termListContainsEmpty)
				{
					flags |= Members.METHOD;
					qflags |= Members.VARIABLE;
				}
				if (tl.getNumberOfChildren () == 1)
				{
					flags |= Members.TYPE;
				}
				Object m = resolver.resolveName (predid, flags, qflags, scope);
				if (m instanceof Method)
				{
					addMethodPattern (pb, label, predid, resolveOpenArguments (args, scope), pos);
				}				
				else if ((m instanceof Type) && (args != null))
				{
					if (args.length != 1)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.SINGLE_ARGTERM_FOR_TYPE_EXPECTED, pos);
					}
					else
					{
						pb.addWrappedType (label, (Type) m, args[0], pos);
					}
				}
				else if ((m instanceof PatternWrapper) && (args != null))
				{
					compilePattern (predid, args, pos, pb, label);
				}
			}
		)
	|	#(SUB edgeRest[pb, label, pos])
	|	#(LEFT_ARROW edgeRest[pb, label, pos])
	|	#(ARROW edgeRest[pb, label, pos])
	|	#(X_LEFT_RIGHT_ARROW edgeRest[pb, label, pos])
	;


traversal[PatternBuilder pb, AST label] returns [TraversalData td]
	{
		Expression min = null, max = null;
		BlockScope scope = pb.getScope ();
		PatternBuilder child;
		AST pos = _t;
		td = null;
		boolean addFolding = true;
	}
	:
	#(TRAVERSAL
		child=nestedCompositePattern[pb]
		(	QUESTION {min = new LongConst (0); max = new LongConst (1);}
		|	MUL {min = new LongConst (0); max = new LongConst (-1);}
		|	ADD {min = new LongConst (1); max = new LongConst (-1); addFolding = false;}
		|	#(RANGE_EXACTLY min=longExpr[scope])
		|	#(RANGE_MIN min=longExpr[scope]) {max = new LongConst (-1);}
		|	#(RANGE min=longExpr[scope] max=longExpr[scope])
		)
	)
	{
		td = pb.addTraversal (label, child, EdgeDirection.FORWARD, min, max, addFolding, pos);
	}
	;


edgeRest[final PatternBuilder pb, AST label, AST edge]
	{
		AST rest = _t;
		Expression e = null;
		BlockScope scope = pb.getScope ();
		Expression[] args;
		EdgeDirection dir = edgeDirection (edge);
		
		class LastNode implements ExpressionFactory
		{
			public Type getType ()
			{
				return pb.getLastNodeType ();
			}
				
			public Expression createExpression (Scope s, AST a)
			{
				return PatternBuilder.createArgument (getType ());
			}
		}
	}
	:	ANY
		{
			pb.addStandardEdge (label, dir, EdgePattern.ANY_EDGE, edge);
		}
	|	(name) => id:name
		{
			if (dir != EdgeDirection.BACKWARD)
			{
				InstanceScope is = new InstanceScope (scope);
				is.setInstance (new LastNode ());
				scope = is;
			}
			Object m = resolver.resolveExpressionOrPatternOrMethodOrTypeName (id, scope);
			if (m instanceof Method)
			{
				compileMethodEdgePattern
					(id, Expression.EXPR_0, edge, pb, scope, label, dir,
					 (dir != EdgeDirection.BACKWARD) && (resolver.getInitialScope () == scope));
			}
			else if (m instanceof Expression)
			{
				pb.addEdge (label, dir, (Expression) m, id);
			}
			else if (m instanceof PatternWrapper)
			{
				compileEdgePattern
					(id, Expression.EXPR_0, edge, pb, scope, label, dir);
			}
			else if (m instanceof Type)
			{
				pb.addConstantEdge (label, dir, (Type) m, null, id);
			}
		}
	|	(METHOD_CALL) =>
		#(METHOD_CALL
			(	(name) => predid:name args=tl:termList[pb]
				{
					int flags = Members.PREDICATE;
					int qflags = Members.TOP_LEVEL_PACKAGE | Members.TYPE;
					if (!termListContainsEmpty)
					{
						flags |= Members.METHOD;
						qflags |= Members.VARIABLE;
						if (dir != EdgeDirection.BACKWARD)
						{
							InstanceScope is = new InstanceScope (scope);
							is.setInstance (new LastNode ());
							scope = is;
						}
					}
					if (tl.getNumberOfChildren () == 1)
					{
						flags |= Members.TYPE;
					}
					Object m = resolver.resolveName (predid, flags, qflags, scope);
					if (m instanceof Method)
					{
						compileMethodEdgePattern
							(predid, resolveOpenArguments (args, scope), edge, pb, scope, label, dir,
							 (dir != EdgeDirection.BACKWARD) && (resolver.getInitialScope () == scope));
					}				
					else if ((m instanceof Type) && (args != null))
					{
						if (args.length != 1)
						{
							problems.addSemanticError
								(I18N, ProblemReporter.SINGLE_ARGTERM_FOR_TYPE_EXPECTED, rest);
						}
						else
						{
							// TODOpb.addWrappedType (label, (Type) m, args[0], pos);
						}
					}
					else if ((m instanceof PatternWrapper) && (args != null))
					{
						compileEdgePattern (predid, args, edge, pb, scope, label, dir);
					}
				}
			|	#(DOT e=expr[scope] mcn:IDENT)
				args=arglist[scope]
				{
					compileMethodEdgePattern (e, mcn.getText (), args, edge, pb, label, dir);
				}
			)
		)
	|	e=expr[scope] {if (e != null) pb.addEdge (label, dir, e, rest);}
	;


term[PatternBuilder pb] returns [Expression e]
	{
		e = null;
	}
	:	(IDENT) => id:IDENT
		{
			Local loc = pb.getScope ().findLocal (id.getText (), true);
			if (loc != null)
			{
				e = loc.createExpression (pb.getScope (), id);
			}
			else
			{
				e = new OpenArgument ();
				setAST (e, id);
			}
		}		
	|	EMPTY {e = new OpenArgument ();}
	|	e=expr[pb.getScope ()] {if (e == null) e = new OpenArgument ();}
	;


termList[PatternBuilder pb] returns [Expression[] list]
	{
		Expression e = null;
		list = new Expression[#termList.getNumberOfChildren ()];
		int i = 0;
		termListContainsEmpty = false;
	}
	:	#(ARGLIST
			(e=term[pb]
				{
					if ((e instanceof OpenArgument) && (getAST (e) == null))
					{
						termListContainsEmpty = true;
					}
					if (list != null)
					{
						if (e == null)
						{
							list = null;
						}
						else
						{
							list[i++] = e;
						}
					}
				}
			)*
		)
	;


rule[BlockScope scope, ExpressionFactory graph, CompiletimeModel model] returns [Expression e]
	{
		BlockScope s = new BlockScope (scope);
		ProduceScope w = null;
		e = null;
		Block loop = null;
	}
	:   #(r:RULE
			(	str:DOUBLE_ARROW_RULE
			|	stmt:EXEC_RULE
			|
			)
			e=q:query[s, graph, model, true, stmt != null]
			{
				loop = Block.createSequentialBlock ();
				w = createProduceScope (s, loop, e, q);
				w.setBlock (new Production ((Local) w.getInstance (),
											(str != null) ? Producer.DOUBLE_ARROW :
											(stmt != null) ? Producer.EXECUTION_ARROW :
											Producer.SIMPLE_ARROW));
				e = w.createExpression (w, r);
				e = compileMethodInvocation (e, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, r);
				w.addExpression (setProducer (w, e, r));
				scope.getMethodScope ().enterBreakTarget (null);
			}
			e=st:stat[w, null]
		)
		{
			w.addExpression (e);
			e = w.createExpression (scope, r);
			w.addExpression (compileMethodInvocation (e, PRODUCER_END, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, w, st));
			Block breakBlock = new Block ();
			breakBlock.add (w.getBlock ());
			scope.getMethodScope ().leave (breakBlock);
			loop.add (breakBlock);
			s.addExpression (loop);
			e = s.getBlock ();
		}
	;


produce[BlockScope scope] returns [Expression e]
	{
		e = null;
		Expression producer = null;
		ProduceScope slistScope = null;
	}
	:   #(g:PRODUCE
			(	EMPTY
				{
					if (scope.getProduceScope () == null)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.NO_PRODUCTION_CONTEXT, g);
						producer = null;
					}
					else
					{
						producer = scope.getProduceScope ().createExpression (scope, g);
					}
				}
			|	producer=s:referenceExpr[scope, null]
			)
			{
				if (producer != null)
				{
					producer = compileMethodInvocation (producer, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, (s != null) ? s : g);
				}
				e = new Block ();
				slistScope = createProduceScope (scope, e, producer, g);
				e.add (slistScope.getBlock ());
				slistScope.setScopeForLocals (scope);
			}
			slistInScope[slistScope]
			{
				producer = slistScope.createExpression (scope, g);
				e.add (compileMethodInvocation (producer, PRODUCER_END, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, (s != null) ? s : g));
			}
		)
	;


node[BlockScope scope, final AST pos, final Expression producerIn] returns [Expression e]
	{
		e = null;
		Expression edge = null;
		Expression n = null;
		Local ref;
		final BlockScope exprScope = new BlockScope (scope, null);

		class ProdFactory implements ExpressionFactory
		{
			Expression producer = producerIn;
			Local producerLocal;

			public Type getType ()
			{
				return producer.getType ();
			}

			public Expression createExpression (Scope s, AST p)
			{
				if (producerLocal == null)
				{
					producerLocal = exprScope.declareLocal ("prod.", Member.FINAL, producer.getType (), pos);
					producer = producerLocal.createSet (exprScope, p).add (producer);
				}
				return producerLocal.createExpression (s, p);
			}
		}

		ProdFactory prodFactory = new ProdFactory ();
		scope.getProduceScope ().setInstance (prodFactory);
	}
	:
	(	(name) => n=nodeExpr[exprScope, prodFactory, pos]
	|	(METHOD_CALL) => n=nodeExpr[exprScope, prodFactory, pos]
	|	(WITH) =>
		#(w:WITH
			(	(name) => n=nodeExpr[exprScope, prodFactory, pos]
			|	(METHOD_CALL) => n=nodeExpr[exprScope, prodFactory, pos]
			|	n=referenceExpr[exprScope, null]
			)
			n=withBlock[exprScope, n, w]
		)
	|	n=expr[exprScope]
	|	ROOT
		{
			n = compileMethodInvocation (prodFactory.createExpression (scope, pos), PRODUCER_GET_ROOT, ArgumentTransformations.NO_IMPLICIT_ARGS,
										 Expression.EXPR_0, 0, scope, pos);
		}
	|	#(UNARY_PREFIX n=unaryExpr[exprScope, true])
	)
	(options{warnWhenFollowAmbig=false;}: label:IDENT)?
	{
		if (n != null)
		{
			if (Reflection.equal (n.getType (), Type.VOID))
			{
				n = Block.createSequentialBlock ().add (n);
				if (label != null)
				{
					problems.addSemanticError
						(I18N, ProblemReporter.LABEL_FOR_VOID, pos);
				}
			}
			else if (label != null)
			{
				n = declareLocal (scope, label, Member.FINAL, n.getType ())
					.createSet ().add (n);
			}
		}
	}
	#(edgeOp:.
		{
			produceArgs.setType (edgeOp, (n != null) ? n : new Expression (Type.INVALID), prodFactory);
		}
		(edge=edgeExpr[scope])?
	)
	{
		if (produceArgs.nodeUsed)
		{
			e = new ExpressionList (edge.getType ()).add (prodFactory.producer).add (edge);
			setAST (e, pos);
		}
		else if ((n != null) && Reflection.equal (n.getType (), Type.VOID) && (edgeOp.getType () == EMPTY))
		{
			e = new ExpressionList (prodFactory.producer.getType (), true).add (prodFactory.producer).add (n);
			setAST (e, pos);
		}
		else if (n != null)
		{
			Expression[] ops;
			if (edge != null)
			{
				ops = new Expression[] {n, edge};
			}
			else
			{
				ops = new Expression[] {n};
			}
			String opName;
			switch (edgeOp.getType ())
			{
				case EMPTY:
					opName = OPERATOR_NAME_SPACE;
					break;
				case INC:
					opName = OPERATOR_NAME_INC;
					break;
				case DEC:
					opName = OPERATOR_NAME_DEC;
					break;
				case ADD:
					opName = OPERATOR_NAME_POS;
					break;
				default:
					opName = binaryOperators.get (edgeOp.getType ());
					opName.getClass ();
					break;
			}
			e = compileMethodInvocation (prodFactory.producer, opName, ArgumentTransformations.NO_IMPLICIT_ARGS,
										 ops, 0, scope, (edgeOp.getType () == EMPTY) ? pos : edgeOp);
		}
		if (e == null)
		{
			e = prodFactory.producer;
		}
	}
	;


edgeExpr[BlockScope scope] returns [Expression e]
	{
		AST pos = _t;
		e = null;
		Expression[] args;
	}
	:	(name) => edgeId:name
		{
			e = compileProduceName (edgeId, scope, pos);
		}
	|	(METHOD_CALL) => #(METHOD_CALL
			(	(name) => mcid:name
			|	#(DOT e=expr[scope] mcn:IDENT)
			)
			args=arglist[scope]
		)
		{
			e = compileProduceInvocation (mcid, e, mcn, args, scope, pos);
		}
	|	(WITH) =>
		#(w:WITH
			e=edgeExpr[scope]
			e=withBlock[scope, e, w]
		)
	|	e=expr[scope]
	;

nodeExpr[BlockScope scope, ExpressionFactory producer, AST pos] returns [Expression n]
	{
		n = null;
		Expression e = null;
		Expression[] args;
		Local ref;
		produceArgs.setType (null, null, producer);
	}
	:	id:name
		{
			if ((id.getType () == IDENT)
				&& ((ref = scope.findLocal (id.getText (), true)) != null))
			{
				if (ref.wrapper != null)
				{
					ref = ref.wrapper;
				}
				n = ref.createExpression (scope, id);
			}
			else
			{
				n = compileProduceName (id, scope, pos);
			}
		}
	|	#(METHOD_CALL
			(	(name) => mcid:name
			|	#(DOT e=expr[scope] mcn:IDENT)
			)
			args=arglist[scope]
		)
		{
			n = compileProduceInvocation (mcid, e, mcn, args, scope, pos);
		}
	;


fieldInitializer[MethodScope scope, XField field] returns [Expression e]
	{
		AST root = _t;
		e = null;
		FieldInitializer init = (run == COMPILATION)
			? (FieldInitializer) currentCompilationUnitScope.properties.get (root)
			: null;
		if (init != null)
		{
			e = init.expr;
		}
	}
	:	{init != null}? .
	|	{(run == TYPE_AND_FIELD_DECLARATION) && Reflection.isFinal (field)}?
		(constantExprPattern) => i:.
		{
			init = new FieldInitializer ();
			init.options = options;
			init.field = field;
			init.ast = i;
			init.scope = scope;
			currentCompilationUnitScope.properties.put (root, init);
			initializersToCompile.put (field, init);
		}
	|	{run == COMPILATION}? e=initializer[scope, field.getType ()]
	|	.
	;


constantExprPattern
	:	literal
	|	#(TYPECAST (name | builtInType) constantExprPattern)
	|	#(TYPECHECK (name | builtInType) constantExprPattern)
	|	#(COM constantExprPattern)
	|	#(NOT constantExprPattern)
	|	#(NEG constantExprPattern)
	|	#(POS constantExprPattern)
	|	#(EXPR constantExprPattern)
	|	#(DIV constantExprPattern constantExprPattern)
	|	#(REM constantExprPattern constantExprPattern)
	|	#(MUL constantExprPattern constantExprPattern)
	|	#(POW constantExprPattern constantExprPattern)
	|	#(ADD constantExprPattern constantExprPattern)
	|	#(SUB constantExprPattern constantExprPattern)
	|	#(SHL constantExprPattern constantExprPattern)
	|	#(SHR constantExprPattern constantExprPattern)
	|	#(USHR constantExprPattern constantExprPattern)
	|	#(LT constantExprPattern constantExprPattern)
	|	#(GT constantExprPattern constantExprPattern)
	|	#(LE constantExprPattern constantExprPattern)
	|	#(GE constantExprPattern constantExprPattern)
	|	#(CMP constantExprPattern constantExprPattern)
	|	#(NOT_EQUALS constantExprPattern constantExprPattern)
	|	#(EQUALS constantExprPattern constantExprPattern)
	|	#(OR constantExprPattern constantExprPattern)
	|	#(XOR constantExprPattern constantExprPattern)
	|	#(AND constantExprPattern constantExprPattern)
	|	#(COR constantExprPattern constantExprPattern)
	|	#(CAND constantExprPattern constantExprPattern)
	|	#(QUESTION constantExprPattern constantExprPattern constantExprPattern)
	|	name
	;


initializer[BlockScope scope, Type type] returns [Expression e]
	{
		e = null;
	}
	:	(	e=expr[scope]
		|	e=arrayInitializer[scope, type]
		)
	{
		if (e == null)
		{
			e = new Expression (type);
		}
	}
	;


arrayInitializer[BlockScope scope, Type type] returns [Expression e]
	{
		e = null;
		Expression f;
		Object array = null;
	}
	:	#(a:ARRAY_INIT
			{
				if (type != null)
				{
					if (Reflection.isArray (type))
					{
						e = new ArrayInit (type);
						setAST (e, a);
						type = type.getComponentType ();
					}
					else
					{
						problems.addSemanticError
							(I18N.msg (ProblemReporter.ARRAYINIT_FOR_NONARRAY, type), a);
						type = null;
					}
				}
			}
			(   f=init:initializer[scope, type]
				{
					if (type != null)
					{
						e.add (assignmentConversion (f, type, scope, init));
					}
				}
			)*
		 )
	;


slistInScope[BlockScope scope]
	{
		Expression e;
	}
	:	#(SLIST (e=stat[scope, null] {scope.addExpression (e);})*)
	;


slist[BlockScope scope] returns [Expression e]
	{
		e = null;
		BlockScope s = new BlockScope (scope);
	}
	:	slistInScope[s]
		{
			e = s.getBlock ();
		}
	;

ruleBlock[BlockScope scope] returns [Expression e]
	{
		e = null;
		BlockScope s = new BlockScope (scope);
		Expression g = null;
		Local graph = null;
		CompiletimeModel ct = null;
	}
	:	#(rb:RULE_BLOCK
			g=graph[scope]
			{
				ct = currentQueryModel;
				if (g != null)
				{
					graph = scope.declareLocal ("graph.", Member.FINAL, g.getType (), rb);
					s.addExpression (graph.createSet ().add (g));
				}
			}
			(	e=stat[s, null] {s.addExpression (e);}
			|	e=rule[s, graph, ct] {s.addExpression (e);}
			)*
		)
		{
			e = s.getBlock ();
		}
	;

statBlock[BlockScope scope] returns [Block b]
	{
		b = null;
		Expression e = null;
		BlockScope s = new BlockScope (scope);
		AST root = _t;
		pushProducer (scope, root);
	}
	:	(	(SLIST) => slistInScope[s]
		|	e=stat[s, null]
			{
				s.addExpression (e);
			}
		)
	{
		b = (Block) s.getBlock ();
		b.add (popAndJoinProducer (s, root));
	}
	;


elist[BlockScope scope] returns [Expression e]
	{ e = null; BlockScope s = new BlockScope (scope); boolean ok = true; }
	:
	#(el:ELIST
		(	e=variableDecl[s] {s.addExpression (e);}
		|	e=expr[s]
			{
				if (e == null)
				{
					ok = false;
				}
				else
				{
					s.addExpression (e);
				}
			}
		)*
	)
	{
		e = null;
		if (ok)
		{
			e = s.getBlock ();
			int c = e.getExpressionCount ();
			if (c > 0)
			{
				Expression last = e.getExpression (c - 1);
				e = s.setBlock (new ExpressionList (last.getType ()));
			}
		}
		setAST (e, el);
	}
	;


withBlock[BlockScope scope, Expression i, AST block] returns [Expression e]
	{
		e = null;
		InstanceScope with = null;
		Local local = null;
	}
	:	{
			if (i != null)
			{
				if (i.etype == TypeId.OBJECT)
				{
					with = new InstanceScope (scope);
					with.setInstance
						(local = with.declareLocal
						 ((scope.findLocal ("$", false) == null) ? "$" : "$.",
						  Member.FINAL, i.getType (), block));
					with.addExpression (local.createSet ().add (i));
				}
				else
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.NO_REFERENCE_TYPE, i.getType ()),
						 block.getFirstChild ());
				}
			}
		}
		e=expr[(with != null) ? with : scope]
		{
			if (with != null)
			{
				with.addExpression (e);
				with.addExpression (local.createGet ());
				e = with.setBlock (new ExpressionList (local.getType ()));
			}
			else
			{
				e = i;
			}
		}
	;


stat[BlockScope scope, String label] returns [Expression e]
	{
		e = null;
		Expression e1 = null, e2 = null;
		Expression[] list = null;
		long m;
		AST root = _t;
		int labelId = 0;
		Type t;
		BlockScope scope2 = null;
	}
	:
	(	e=expr[new BlockScope (scope, null)]
		{
			e = Block.createSequentialBlock ().add (e);
		}
	|	e=slist[scope]
	|	e=ruleBlock[scope]
	|	#(SHELL_BLOCK
			{
				shellBlock = new Block ();
				e = shellBlock;
				shellBlockScope = scope;
			}
			(	e1=sh:stat[scope, null]
				{
					if ((e1 instanceof Block) && (e1.getExpressionCount () == 0))
					{
						e1 = null;
					}
					if (e1 != null)
					{
						e.add (e1);
						e2 = e1;
					}
				}
			)*
			{
				shellBlock = null;
				shellBlockScope = null;
				if ((e2 instanceof Block)
					&& (e2.getExpressionCount () == 1)
					&& ((e1 = e2.getExpression (0)).etype != TypeId.VOID))
				{
					e2.setBranch (null);
					e2.add (compileMethodInvocation
						(new ASTWithToken (IDENT, "println"),
						 ArgumentTransformations.NO_IMPLICIT_ARGS,
						 new Expression[] {e1}, 0, scope, sh));
				}
				for (Local loc : scope.getLocals ())
				{
					if ((loc.getModifiers () & Member.SYNTHETIC) == 0)
					{
						t = Reflection.getBinaryType (loc.getType ());
						Field f = Reflection.getDeclaredField
							(shell, Reflection.getFieldDescriptor (loc.getName (), t));
						if (f == null)
						{
							f = shell.declareField (loc.getName (), Member.PUBLIC | Member.STATIC, t);
						}
						else
						{
							shell.makeVisible ((XField) f);
						}
						e.add (compileAssignment
							   (new GetField (f), loc.createGet (),
							    root, root, scope));
						
					}
				}
			}
		)
	|	e=produce[scope]
	|	e=variableDecl[scope]
	|	#(c:CLASS
			{
				setLocalRun (TYPE_AND_FIELD_DECLARATION);
			}
			classDecl[scope, Type.LOCAL_CLASS_MODIFIERS, scope.isStatic () ? Member.STATIC : 0, c]
			{
				AST cd = c.getFirstChild ();
				setLocalRun (METHOD_DECLARATION);
				classDecl (cd, scope, Type.LOCAL_CLASS_MODIFIERS, 0, c);
				setLocalRun (COMPILATION);
				classDecl (cd, scope, Type.LOCAL_CLASS_MODIFIERS, 0, c);
			}
		)
	|	#(CONSTRUCTOR
			(	alt:THIS
			|	SUPER
			|	#(QUALIFIED_SUPER e2=referenceExpr[scope, null])
			)
			list=arglist[scope]
		)
		{
			if (list != null)
			{
				MethodScope ms = scope.getMethodScope ();
				e2 = (alt == null)
					? compileSuperConstructorInvocation
						(scope.createThis (), e2, list, scope, root)
					: compileConstructorInvocation
						(scope.createThis (), null, list, scope, root, true);
				e = new Block ();
				if (alt == null)
				{
					if (ms.enclosingInstance != null)
					{
						e.add (new AssignField (TypeScope.get (ms).enclosingInstance,
													Assignment.SIMPLE)
							   .add (scope.createThis ())
							   .add (ms.enclosingInstance.createGet ()));
					}
				}
				e.add (new SetThis ().add (e2));
				if (alt == null)
				{
					e.add (new InvokeSpecial (TypeScope.get (ms).instanceInit.getMethod ())
						   .add (ms.createThis ()));
				}
			}
		}
	|   #(IF e=booleanExpr[scope] e1=statBlock[scope] (e2=statBlock[scope])? )
		{
			e = new If ().add (e).add (e1).add (e2);
		}
	|   #(RETURN (e1=ex1:expr[scope])?)
		{
		compileRet:
			{
				MethodScope ms = MethodScope.get (scope);
				if (ms.isInitializer ())
				{
					problems.addSemanticError
						(I18N, ProblemReporter.RETURN_OUTSIDE_METHOD, root);
					break compileRet;
				}
				XMethod method = ms.getMethod ();
				t = method.getReturnType ();
				e = new Return (ms, t);
				if ((method.getModifiersEx () & MOD_ITERATING) != 0)
				{
					if (ex1 != null)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.ITERATING_NONVOID_RETURN,
							 root);
					}
					break compileRet;
				}
				if (t.getTypeId () == TypeId.VOID)
				{
					if (ex1 != null)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.NONVOID_RETURN, root);
					}
				}
				else
				{
					if (ex1 == null)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.VOID_RETURN, root);
					}
					if (e1 != null)
					{
						e.add (returnConversion (e1, t, scope, ex1));
					}
				}
			}
		}
	|   #(YIELD (e1=y:expr[scope2 = new BlockScope (scope, null)])?)
		{
		compileYield:
			{
				MethodScope ms = MethodScope.get (scope);
				XMethod method = ms.getMethod ();
				if ((method.getModifiersEx () & MOD_ITERATING) == 0)
				{
					problems.addSemanticError (I18N, ProblemReporter.NONITERATING_YIELD,
											   root);
					break compileYield;
				}
				e = new Yield ().add (ms.consumer.createGet ());
				t = XMethod.getGeneratorType (method);
				e2 = null;
				if (t.getTypeId () == TypeId.VOID)
				{
					if (e1 != null)
					{
						if (e1.etype != TypeId.VOID)
						{
							problems.addSemanticError (I18N, ProblemReporter.NONVOID_YIELD,
													   root);
						}
						else
						{
							e2 = e1;
						}
					}
				}
				else
				{
					if (y == null)
					{
						problems.addSemanticError (I18N, ProblemReporter.VOID_YIELD,
												   root);
					}
					if (e1 != null)
					{
						e.add (returnConversion (e1, t, scope, y));
					}
				}
				e = Block.createSequentialBlock ().add (e2).add (e);
			}
		}
	|	#(THROW e=referenceExpr[scope, Throwable.class])
		{
			e = new Throw ().add (e);
		}
	|	#(SYNCHRONIZED_ e=referenceExpr[scope, null] e2=statBlock[scope])
		{
			Local lock = scope.declareLocal ("lock.", Member.FINAL, Type.OBJECT, root);
			e = new Synchronized (lock).add (e).add (e2);
		}
	|	#(ASSERT e=booleanExpr[scope] (e2=expr[scope])?)
		{
			e = new Assert (TypeScope.getNonlocal (scope).getAssertionsDisabledField ())
				.add (e).add (e2);
		}
	|	#(LABELED_STATEMENT lid:IDENT
			{
				if (scope.getMethodScope ().enterLabel (lid.getText ()))
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.DUPLICATE_LABEL, lid.getText ()), lid);
				}
				pushProducer (scope, root);
			}
			e=stat[scope, lid.getText ()]
			{
				if (!(e instanceof BreakTarget))
				{
					e = new Block ().add (e);
				}
				e2 = popAndJoinProducer (scope, root);
				if (e2 != null)
				{
					assert e instanceof Block;
					e.add (e2);
				}
				scope.getMethodScope ().leave ((BreakTarget) e);
				if (label != null)
				{
					e = new Block ().add (e);
				}
			}
		)
	|   #(br:BREAK labelId=labelRef[scope, false, br])
		{
			e = new Break (labelId);
		}
	|   #(cont:CONTINUE labelId=labelRef[scope, true, cont])
		{
			e = new Break (labelId);
		}
	|	#(TRY e=statBlock[scope]
			{
				e = new TryCatch ().add (e);
			}
			(#(CATCH m=modifiers[Member.FINAL, 0, 0, null, scope] t=ec:classType[scope] ex:IDENT
				{
					if (!Reflection.isInvalid (t)
						&& !Reflection.isSuperclassOrSame (Throwable.class, t))
					{
						problems.addSemanticError
							(I18N.msg (ProblemReporter.UNEXPECTED_TYPE, t, "java.lang.Throwable"), ec);
					}
					scope2 = new BlockScope (scope);
					e2 = new Catch (declareLocal (scope2, ex, (int) m, t));
				}
				slistInScope[scope2]
				{
					e.add (e2.add (scope2.getBlock ()));
				}
			))+
		)
	|	#(FINALLY e1=statBlock[scope] e2=statBlock[scope])
		{
			e2 = new Finally
				(scope.declareLocal ("ex.", Member.FINAL, Type.OBJECT, root),
				 scope.declareLocal ("addr.", Member.FINAL, Type.OBJECT, root))
				.add (e2);
			e = new TryFinally ().add (e1).add (e2);
		}
	|	e=loop[scope, label]
	|	e=switchStatement[scope, label]
	|	#(NODES
			{
				e = scope.getProduceScope ().createExpression (scope, root);
			}
			(#(node:NODE e=node[scope, node, e]))*
			{
				e = setProducer (scope, e, root);
			}
		)
	|	#(TREE
			{
				e = new Block ();
				Local prod = pushProducer (scope, root);
				e1 = prod.createExpression (scope, root);
				e1 = compileMethodInvocation (e1, PRODUCER_PUSH, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root);
				e1 = compileMethodInvocation (e1, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root);
				e.add (setProducer (scope, e1, root));
			}
			(e1=stat[scope, null] {e.add (e1);})*
			{
				e2 = scope.getProduceScope ().createExpression (scope, root);
				e.add (compileMethodInvocation (e2, PRODUCER_END, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root));
				e2 = scope.getProduceScope ().createExpression (scope, root);
				e1 = popProducer (scope, root).createExpression (scope, root);
				if (e2 != null)
				{
					e1 = compileMethodInvocation (e1, PRODUCER_POP, ArgumentTransformations.NO_IMPLICIT_ARGS, new Expression[] {e2}, 0, scope, root);
					e.add (setProducer (scope, e1, root));
				}
			}
		)
	|	SEPARATE
		{
			e = new Block ();
			e2 = scope.getProduceScope ().createExpression (scope, root);
			e2 = compileMethodInvocation (e2, PRODUCER_SEPARATE, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root);
			e.add (setProducer (scope, e2, root));
		}
	)
	{
		setAST (e, root);
	}
	;


labelRef[BlockScope scope, boolean forContinue, AST pos] returns [int id]
	{
		id = -1;
	}
	:	(	i:IDENT
			{
				id = scope.getMethodScope ().getTargetId (i.getText (), forContinue);
				if (id < 0)
				{
					problems.addSemanticError
						(I18N.msg ((id < -1) ? ProblemReporter.NONLOOP_CONTINUE_TARGET
								   : ProblemReporter.NO_LABEL_IN_SCOPE, i.getText ()), i);
				}
			}
		|	{
				id = scope.getMethodScope ().getTargetId (null, forContinue);
				if (id < 0)
				{
					problems.addSemanticError
						(I18N, forContinue ? ProblemReporter.NO_CONTINUE_TARGET:
						 ProblemReporter.NO_BREAK_TARGET, pos);
				}
			}
		)
	;


loop[BlockScope scope, String label] returns [Expression e]
	{
		e = null;
		Expression e1 = null, e2 = null;
		BlockScope forScope = null;
		scope.getMethodScope ().enterBreakTarget (label);
		Local local = null;
	}
	:
	(	#(FOR { forScope = new BlockScope (scope); }
			slistInScope[forScope] e=booleanExpr[forScope]
			e1=statBlock[forScope]
			e2=loopBlock[forScope, label]
		)
		{
			forScope.addExpression (new For ().add (e).add (e2).add (e1));
			e = forScope.getBlock ();
		}
	|	#(enh:ENHANCED_FOR { forScope = new BlockScope (scope, true); }
			e1=it:expr[forScope]
			(	e2=variableDecl[forScope]
				{
					if (e2 != null)
					{
						assert shell != null;
					}
					local = (Local) declaredVariable;
				}
			|	VOID_
			)
			{
				if (e1 != null)
				{
					final int DISPOSABLE_ITERATOR = 0, ITERABLE = 1, ARRAY = 2, SEQ = 3;
					int iter;
					Type potential;
					if (e1.hasType (DisposableIterator.class))
					{
						iter = DISPOSABLE_ITERATOR;
						Method m = Reflection.findMethodWithPrefixInTypes
							(e1.getType (), "mvalue;()", true, true);
						potential = (m != null) ? m.getReturnType () : Type.VOID;
					}
					else if (Reflection.findMethodWithPrefixInTypes (e1.getType (), "miterator;()Ljava/util/Iterator;", true, true) != null)
					{
						iter = ITERABLE;
						potential = Type.OBJECT;
						if ((local != null)
							&& !Reflection.isPrimitive (local.getType ()))
						{
							potential = local.getType ();
						}
					}
					else if (Reflection.isArray (e1.getType ()))
					{
						iter = ARRAY;
						potential = e1.getType ().getComponentType ();
					}
					else
					{
						iter = SEQ;
						potential = e1.getType ();
					}
					if ((iter != SEQ) && (local != null)
						&& (standardImplicitConversion (new Expression (potential),
														local.getType (), true, scope,
														null, null, true) == null))
					{
						iter = SEQ;
						potential = e1.getType ();
					}
					switch (iter)
					{
						case DISPOSABLE_ITERATOR:
							Local iterator = forScope.declareLocal
								("iterator.", Member.FINAL, e1.getType (), enh);
							Expression value = (potential.getTypeId () != TypeId.VOID)
								? compileMethodInvocation
									(iterator.createGet (), "value", ArgumentTransformations.NO_IMPLICIT_ARGS,
									 Expression.EXPR_0, 0, scope, it)
								: null;
							e1 = new FinishIteratorGenerator (potential, iterator)
								.add (e1).add (value);
							break;
						case ITERABLE:
							e1 = compileMethodInvocation
								(e1, "iterator", ArgumentTransformations.NO_IMPLICIT_ARGS,
								 Expression.EXPR_0, 0, scope, it);
							e1 = new IterableGenerator
								(potential,
								 forScope.declareLocal
									("iterator.", Member.FINAL, e1.getType (), enh))
								.add (e1);
							break;
						case ARRAY:
							e1 = new ArrayGenerator
								(potential,
								 forScope.declareLocal
									("array.", Member.FINAL, e1.getType (), enh),
								 forScope.declareLocal
									("counter.", 0, Type.INT, enh)
								).add (e1);
							break;
					}
					if (local != null)
					{
						e1 = compileAssignment
							(local.createGet (), e1, it, it, scope);
					}
					forScope.addExpression (e1);
				}
			}
			e2=loopBlock[forScope, label]
		)
		{
			forScope.addExpression (e2);
			e = forScope.getBlock ();
		}
	|	#(WHILE e=booleanExpr[scope] e1=loopBlock[scope, label])
		{
			e = new While ().add (e).add (e1);
		}
	|	#(DO e=loopBlock[scope, label] e1=booleanExpr[scope])
		{
			e = new Do ().add (e).add (e1);
		}
	)
	{
		scope.getMethodScope ().leave ((BreakTarget) e);
	}
	;


loopBlock[BlockScope scope, String label] returns [Block e]
	{
		e = null;
		scope.getMethodScope ().enterContinueTarget (label);
	}
	:	e=statBlock[scope]
	{
		scope.getMethodScope ().leave (e);
	}
	;


switchStatement[BlockScope enclosing, String label] returns [Expression e]
	{
		e = null;
		Switch s = new Switch ();
		BlockScope scope = new BlockScope (enclosing, s);
		Type labelType = null;
		int defaultIndex = -1;
		Int2IntMap switchLabels = new Int2IntMap ();
		scope.getMethodScope ().enterBreakTarget (label);
		int classCount = 0;
	}
	:	#(SWITCH e=sw:expr[scope]
			{
				labelType = Type.INT;
				if (e != null)
				{
					if (!Reflection.isPrimitive (e.getType ()))
					{
						Type t = Reflection.getUnwrappedType (e.getType ());
						if (!Reflection.isInvalid (t))
						{
							e = assignmentConversion (e, t, scope, sw);
						}
					}
					if (((1 << e.etype) & TypeId.INT_ASSIGNABLE) != 0)
					{
						labelType = e.getType ();
						e = wideningConversion (e, Type.INT, scope, sw);
						s.add (e);
					}
					else
					{
						problems.addSemanticError
							(I18N.msg (ProblemReporter.ILLEGAL_SWITCH_TYPE, e.getType ().getName ()), sw);
					}
				}
			}
			(#(swg:SWITCH_GROUP
				(	#(CASE e=sl:expr[scope])
					{
						if (e != null)
						{
							int ec = problems.getErrorCount ();
							e = assignmentConversion (e, labelType, scope, sl, ProblemReporter.ILLEGAL_LABEL_TYPE);
							if (e instanceof Constant)
							{
								int i = (e.etype == TypeId.CHAR)
									? e.evaluateChar (null)
									: ((Number) e.evaluateAsObject (null)).intValue ();
								if (switchLabels.findIndex (i) < 0)
								{
									switchLabels.put (i, s.getExpressionCount ());
								}
								else
								{
									problems.addSemanticError
										(I18N, ProblemReporter.DUPLICATE_SWITCH_LABEL, sl);
								}
							}
							else if (ec == problems.getErrorCount ())
							{
								problems.addSemanticError
									(I18N, ProblemReporter.LABEL_NOT_CONSTANT, sl);
							}
						}
					}
				|	def:DEFAULT
					{
						if (defaultIndex >= 0)
						{
							problems.addSemanticError
								(I18N, ProblemReporter.DUPLICATE_DEFAULT_LABEL, def);
						}
						else
						{
							defaultIndex = s.getExpressionCount ();
						}
					}
				)+
				{
					classCount = scope.getDeclaredClassCount ();
					pushProducer (scope, swg);
				}
				(e=stat[scope, null] {s.add (e);})*
				{
					s.add (popAndJoinProducer (scope, swg));
					scope.setDeclaredClassCount (classCount);
				}
			))*
		)
	{
		s.initialize (switchLabels, defaultIndex);
		scope.getMethodScope ().leave (s);
		e = scope.getBlock ();
	}
	;


arglist[Scope scope] returns [Expression[] list]
	:	#(ARGLIST list=exprlist[scope])
	;


exprlist[Scope scope] returns [Expression[] list]
	{
		int i = 0;
		for (AST a = #exprlist; a != null; a = a.getNextSibling ())
		{
			i++;
		}
		list = new Expression[i];
		i = 0;
		Expression e;
	}
	:	(	e=expr[scope]
			{
				if (list != null)
				{
					if (e == null)
					{
						list = null;
					}
					else
					{
						list[i++] = e;
					}
				}
			}
		)*
	;


expr[Scope scope] returns [Expression e]
	{
		e = null; Type t = null; Expression e2 = null, e3;
		AST root = _t;
	}
	:
	(	e=blockExpr[scope]
	|	e=literal
	|	NULL_LITERAL {e = new ObjectConst (null, Type.NULL);}
	|   INVALID_EXPR
	|   #(EXPR e=expr[scope])
	|   #(RANGE e=binaryOp[scope, e2 = new Range (), OPERATOR_NAME_RANGE, root])
		{
			if ((e == e2) && (e.etype >= 0))
			{
				((Range) e).setLocals
					(((BlockScope) scope).declareLocal ("counter.", 0, e.getType (), root),
					 ((BlockScope) scope).declareLocal ("max.", Member.FINAL, e.getType (), root));
			}
		}
	|	#(COR e=binaryOp[scope, new ConditionalOr (), OPERATOR_NAME_COR, root])
	|	#(CAND e=binaryOp[scope, new ConditionalAnd (), OPERATOR_NAME_CAND, root])
	|	#(OR e=binaryOp[scope, new Or (), OPERATOR_NAME_OR, root])
	|	#(XOR e=binaryOp[scope, new Xor (), OPERATOR_NAME_XOR, root])
	|	#(AND e=binaryOp[scope, new And (), OPERATOR_NAME_AND, root])
	|	#(NOT_EQUALS e=binaryOp[scope, new NotEquals (), OPERATOR_NAME_NOT_EQUALS, root])
	|	#(EQUALS e=binaryOp[scope, new Equals (), OPERATOR_NAME_EQUALS, root])
	|	#(LT e=binaryOp[scope, new LT (), OPERATOR_NAME_LT, root])
	|	#(GT e=binaryOp[scope, new GT (), OPERATOR_NAME_GT, root])
	|	#(LE e=binaryOp[scope, new LE (), OPERATOR_NAME_LE, root])
	|	#(GE e=binaryOp[scope, new GE (), OPERATOR_NAME_GE, root])
	|	#(CMP e=binaryOp[scope, new Compare(), OPERATOR_NAME_CMP, root])
	|	#(SHL e=binaryOp[scope, new Shl (), OPERATOR_NAME_SHL, root])
	|	#(SHR e=binaryOp[scope, new Shr (), OPERATOR_NAME_SHR, root])
	|	#(USHR e=binaryOp[scope, new Ushr (), OPERATOR_NAME_USHR, root])
	|	#(ADD e=binaryOp[scope, new Add (), OPERATOR_NAME_ADD, root])
	|	#(SUB e=binaryOp[scope, new Sub (), OPERATOR_NAME_SUB, root])
	|	#(DIV e=binaryOp[scope, new Div (), OPERATOR_NAME_DIV, root])
	|	#(REM e=binaryOp[scope, new Rem (), OPERATOR_NAME_REM, root])
	|	#(MUL e=binaryOp[scope, new Mul (), OPERATOR_NAME_MUL, root])
	|	#(POW e=binaryOp[scope, new Power (), OPERATOR_NAME_POW, root])
	|	#(LONG_LEFT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_LONG_LEFT_ARROW, root])
	|	#(LONG_ARROW e=binaryOp[scope, null, OPERATOR_NAME_LONG_ARROW, root])
	|	#(LONG_LEFT_RIGHT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_LONG_LEFT_RIGHT_ARROW, root])
	|	#(LEFT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_LEFT_ARROW, root])
	|	#(ARROW e=binaryOp[scope, null, OPERATOR_NAME_ARROW, root])
	|	#(LINE e=binaryOp[scope, null, OPERATOR_NAME_LINE, root])
	|	#(LEFT_RIGHT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_LEFT_RIGHT_ARROW, root])
	|	#(PLUS_LEFT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_PLUS_LEFT_ARROW, root])
	|	#(PLUS_ARROW e=binaryOp[scope, null, OPERATOR_NAME_PLUS_ARROW, root])
	|	#(PLUS_LEFT_RIGHT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_PLUS_LEFT_RIGHT_ARROW, root])
	|	#(PLUS_LINE e=binaryOp[scope, null, OPERATOR_NAME_PLUS_LINE, root])
	|	#(SLASH_LEFT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_SLASH_LEFT_ARROW, root])
	|	#(SLASH_ARROW e=binaryOp[scope, null, OPERATOR_NAME_SLASH_ARROW, root])
	|	#(SLASH_LEFT_RIGHT_ARROW e=binaryOp[scope, null, OPERATOR_NAME_SLASH_LEFT_RIGHT_ARROW, root])
	|	#(SLASH_LINE e=binaryOp[scope, null, OPERATOR_NAME_SLASH_LINE, root])
	|	#(QUESTION e=booleanExpr[scope] e2=tex:expr[scope] e3=expr[scope])
		{
			if (e2 == null)
			{
				e = e3;
			}
			else if (e3 == null)
			{
				e = e2;
			}
			else
			{
				try
				{
					e = new Conditional ().compile (scope, e, e2, e3).toConst ();
				}
				catch (IllegalOperandTypeException ex)
				{
					ASTWithToken n = new ASTWithToken ();
					n.setFirstChild (tex);
					problems.addSemanticError
						(I18N.msg (ProblemReporter.INCOMPATIBLE_TYPES, e2.getType (), e3.getType ()), n);
					e = null;
				}
			}
		}
	|	#(INSTANCEOF e2=iex:referenceExpr[scope, null] t=typeSpec[scope])
		{
			checkClassReady (t);
			if (t.getTypeId () != TypeId.OBJECT)
			{
				problems.addSemanticError
					(I18N.msg (ProblemReporter.NO_REFERENCE_TYPE, t), iex);
				t = null;
			}
			if ((e2 != null) && (t != null))
			{
				if (Reflection.isCastableFrom (t, e2.getType ()))
				{
					e = new InstanceOf (t).add (e2);
				}
				else
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.NEVER_INSTANCE, e2.getType (), t), root);
				}
			}
			if (e == null)
			{
				e = new Expression (Type.BOOLEAN);
			}
		}
	|	#(TYPECAST t=typeSpec[scope] e2=tce:expr[scope])
		{
			checkClassReady (t);
			if (e2 != null)
			{
				if ((e2.etype == TypeId.OBJECT)
					&& (e2 instanceof GetProperty)
					&& Reflection.isCastableFrom (t, e2.getType ()))
				{
					Property s = ((GetProperty) e2).getProperty ();
					s = s.getTypeCastProperty (t);
					((GetProperty) e2).setProperty (s);
					e = e2;
				}
				else
				{
					e = castingConversion (e2, t, scope, tce);
				}
			}
			if (e == null)
			{
				e = new Expression (t);
			}
		}
	|	#(TYPECHECK t=typeSpec[scope] e2=tch:expr[scope])
		{
			checkClassReady (t);
			e = returnConversion (e2, t, scope, root);
		}
	|	#(CLASS_LITERAL t=typeSpec[scope])
		{
			checkClassReady (t);
			e = new ClassConst (t);
		}
	|	e=unaryExpr[scope, false]
	|	(name) => id:name
		{
			e = resolver.resolveExpressionName (id, scope);
		}
	|  #(DOT e2=expr[scope] i:IDENT)
		{
			if (e2 != null)
			{
				t = e2.getType ();
				Field f = resolver.resolveField
					(scope, t, i, (e2 instanceof Super) ? Members.SUPER : 0);
				if (f != null)
				{
					e = compileFieldExpression (f, e2, scope, i);
				}
			}	
		}
	)
	{
		setAST (e, root);
	}
	;


unaryExpr[Scope scope, boolean includeInstanceScope] returns [Expression e]
	{
		e = null;
		Expression op = null;
		String opName = null;
		AST root = _t;
	}
	:
	(	#(COM e=expr[scope]) {op = new Complement (); opName = OPERATOR_NAME_COM;}
	|	#(NOT e=expr[scope]) {op = new Not (); opName = OPERATOR_NAME_NOT;}
	|	#(NEG e=expr[scope]) {op = new Neg (); opName = OPERATOR_NAME_NEG;}
	|	#(POS e=expr[scope]) {op = new Pos (); opName = OPERATOR_NAME_POS;}
	|   #(QUOTE e=expr[scope]) {op = new Id (); opName = OPERATOR_NAME_QUOTE;}
	)
	{
		if (e != null)
		{
			e = compileOperator (op, opName, new Expression[] {e}, includeInstanceScope ? OP_ISCOPE : 0, scope, root);
		}
		if ((e == null) && (op != null) && (op.etype >= 0))
		{
			e = new Expression (op.getType ());
		}
		setAST (e, root);
	}
	;


blockExpr[Scope s] returns [Expression e]
	{
		AST root = _t;
		if (!(s instanceof BlockScope))
		{
			problems.addSemanticError (I18N, ProblemReporter.EXPR_NEEDS_BLOCK, root);
			_retTree = root.getNextSibling ();
			return null;
		}
		BlockScope scope = (BlockScope) s;
		e = null; Type t = null; Expression e2 = null, e3;
		Expression[] list;
	}
	:
	(	#(ASSIGN e=assignOp[scope, root])
	|	#(ADD_ASSIGN e=compoundAssignOp[scope, new Add (), OPERATOR_NAME_ADD_ASSIGN, root])
	|	#(SUB_ASSIGN e=compoundAssignOp[scope, new Sub (), OPERATOR_NAME_SUB_ASSIGN, root])
	|	#(MUL_ASSIGN e=compoundAssignOp[scope, new Mul (), OPERATOR_NAME_MUL_ASSIGN, root])
	|	#(DIV_ASSIGN e=compoundAssignOp[scope, new Div (), OPERATOR_NAME_DIV_ASSIGN, root])
	|	#(REM_ASSIGN e=compoundAssignOp[scope, new Rem (), OPERATOR_NAME_REM_ASSIGN, root])
	|	#(POW_ASSIGN e=compoundAssignOp[scope, new Power (), OPERATOR_NAME_POW_ASSIGN, root])
	|	#(SHR_ASSIGN e=compoundAssignOp[scope, new Shr (), OPERATOR_NAME_SHR_ASSIGN, root])
	|	#(USHR_ASSIGN e=compoundAssignOp[scope, new Ushr (), OPERATOR_NAME_USHR_ASSIGN, root])
	|	#(SHL_ASSIGN e=compoundAssignOp[scope, new Shl (), OPERATOR_NAME_SHL_ASSIGN, root])
	|	#(AND_ASSIGN e=compoundAssignOp[scope, new And (), OPERATOR_NAME_AND_ASSIGN, root])
	|	#(XOR_ASSIGN e=compoundAssignOp[scope, new Xor (), OPERATOR_NAME_XOR_ASSIGN, root])
	|	#(OR_ASSIGN e=compoundAssignOp[scope, new Or (), OPERATOR_NAME_OR_ASSIGN, root])
	|	#(DEFERRED_ASSIGN e=deferredOp[scope, OPERATOR_NAME_DEFERRED_ASSIGN, root])
	|	#(DEFERRED_RATE_ASSIGN e=deferredOp[scope, OPERATOR_NAME_DEFERRED_RATE_ASSIGN, root])
	|	#(DEFERRED_ADD e=deferredOp[scope, OPERATOR_NAME_DEFERRED_ADD, root])
	|	#(DEFERRED_SUB e=deferredOp[scope, OPERATOR_NAME_DEFERRED_SUB, root])
	|	#(DEFERRED_MUL e=deferredOp[scope, OPERATOR_NAME_DEFERRED_MUL, root])
	|	#(DEFERRED_DIV e=deferredOp[scope, OPERATOR_NAME_DEFERRED_DIV, root])
	|	#(DEFERRED_REM e=deferredOp[scope, OPERATOR_NAME_DEFERRED_REM, root])
	|	#(DEFERRED_POW e=deferredOp[scope, OPERATOR_NAME_DEFERRED_POW, root])
	|	#(DEFERRED_OR e=deferredOp[scope, OPERATOR_NAME_DEFERRED_OR, root])
	|	#(DEFERRED_AND e=deferredOp[scope, OPERATOR_NAME_DEFERRED_AND, root])
	|	#(DEFERRED_XOR e=deferredOp[scope, OPERATOR_NAME_DEFERRED_XOR, root])
	|	#(DEFERRED_SHL e=deferredOp[scope, OPERATOR_NAME_DEFERRED_SHL, root])
	|	#(DEFERRED_SHR e=deferredOp[scope, OPERATOR_NAME_DEFERRED_SHR, root])
	|	#(DEFERRED_USHR e=deferredOp[scope, OPERATOR_NAME_DEFERRED_USHR, root])
	|	#(INC e=incOp[scope, false, (byte) 1, OPERATOR_NAME_INC, root])
	|	#(DEC e=incOp[scope, false, (byte) -1, OPERATOR_NAME_DEC, root])
	|	#(POST_INC e=incOp[scope, true, (byte) 1, OPERATOR_NAME_POST_INC, root])
	|	#(POST_DEC e=incOp[scope, true, (byte) -1, OPERATOR_NAME_POST_DEC, root])
	|	#(IN e=binaryOp[scope, e2 = new Equals (), OPERATOR_NAME_IN, root])
		{
			if (e == e2)
			{
				e = compileAggregateBooleanOr (e, scope, root);
			}
		}
	|	#(GUARD e=binaryOp[scope, new Guard (), OPERATOR_NAME_GUARD, root])
	|	#(ARRAY_ITERATOR e=expr[scope])
		{
			if (e != null)
			{
				e = compileArrayGenerator (e, scope, root);
			}
		}
	|	#(w:WITH e=referenceExpr[scope, null] e=withBlock[scope, e, w])
	|	e=methodInvocation[scope, METHOD_ARGS]
	|	#(q:QUERY_EXPR
			e=graph[scope]
			e=query[scope, e, currentQueryModel, false, false]
		)
	|   e=elist[scope]
	|	e=arrayIndex[scope]
	|	e=newExpression[scope]
	|	#(INVOKE_OP list=exprlist[scope])
		{
			if (list != null)
			{
				e = compileOperator (null, OPERATOR_NAME_INVOKE, list, 0, scope, root);
			}
		}
	
	|	#(QUALIFIED_NEW e=referenceExpr[scope, null] qnid:IDENT list=arglist[scope]
			{
				if (e != null)
				{
					t = resolver.resolveTypeName (e.getType (), qnid, scope);
				}
			}
			(	e=anonymousClass[scope, t, qnid, e, list, root]
			|	{
					if (t != null)
					{
						if (list == null)
						{
							e = new Expression (t);
						}
						else
						{
							e = compileConstructorInvocation
								(new New (t), e, list, scope, root, false);
						}
					}
				}
			)
		)
	|	#(THIS (t=classType[scope])?)
		{
			e = compileInstance (t, scope, root, false);
		}
	|	#(SUPER (t=classType[scope])?)
		{
			e = compileInstance (t, scope, root, true);
		}
	)
	;


methodInvocation[BlockScope scope, ArgumentTransformations impl] returns [Expression e]
	{
		e = null;
		Expression e2 = null;
		Expression[] list;
	}
	:	#(mc:METHOD_CALL
			(   (name) => id:name
			|   #(DOT e2=expr[scope] name:IDENT)
			)
			list=arglist[scope])
	{
		if (list != null)
		{
			if (id != null)
			{
				e = compileMethodInvocation (id, impl, list, 0, scope, mc);
			}
			else if (e2 != null)
			{
				e = compileMethodInvocation
					(e2, name.getText (), impl, list, 0, scope, mc);
			}
			setAST (e, mc);
		}
	}
	;


booleanExpr[Scope scope] returns [Expression e]
	{ e = null; }
	:	e=ex:expr[scope]
		{
			e = assignmentConversion (e, Type.BOOLEAN, scope, ex);
		}
	;


longExpr[Scope scope] returns [Expression e]
	{ e = null; }
	:	e=ex:expr[scope]
		{
			e = assignmentConversion (e, Type.LONG, scope, ex);
		}
	;


referenceExpr[Scope scope, Class cls] returns [Expression e]
	{ e = null; }
	:	e=ex:expr[scope]
		{
			if (e != null)
			{
				if (e.etype != TypeId.OBJECT)
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.NO_REFERENCE_TYPE, e.getType ()), ex);
					e = null;
				}
				else if (Reflection.isInvalid (e.getType ()))
				{
					e = null;
				}
				else if ((cls != null) && !e.hasType (cls))
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.UNEXPECTED_TYPE, e.getType (), cls.getName ()), ex);
					e = null;
				}
			}
			if ((e == null) && (cls != null))
			{
				e = new Expression (ClassAdapter.wrap (cls));
			}
		}
	;


binaryOp[Scope scope, Expression op, String opName, AST root] returns [Expression e]
	{ e = null; Expression expr1, expr2; }
	:	expr1=expr[scope] expr2=expr[scope]
		{
			if ((expr1 != null) && (expr2 != null))
			{
				e = compileOperator (op, opName, new Expression[] {expr1, expr2}, 0, scope, root);
			}
			if ((e == null) && (op != null) && (op.etype >= 0))
			{
				e = new Expression (op.getType ());
			}
		}
	;


incOp[BlockScope scope, boolean postfix, byte inc, String operatorName, AST root]
	returns [Expression e]
	{
		e = null;
	}
	:	e=ex:expr[scope]
		{
			e = compileCompoundAssignment
				(new Add (), operatorName, e,
				 Expression.createConst (Type.INT, Integer.valueOf (inc)),
				 postfix ? OP_POST | OP_INC : OP_INC, scope, ex, root);
		}
	;


deferredOp[BlockScope scope, String op, AST pos] returns [Expression e]
	{ e = null; Expression rhs; }
	:	e=ex:expr[scope] rhs=expr[scope]
		{
			e = compileDeferredAssignment (op, e, rhs, scope, ex, pos);
		}
	;


assignOp[BlockScope scope, AST pos] returns [Expression e]
	{
		e = null; Expression expr1 = null, expr2;
		boolean implicit;
		Field shField = null;
		if (_t.getType () == IDENT)
		{
			if (shell != null)
			{
				Object v = resolver.resolveIfDeclared
					(_t, Members.VARIABLE | Resolver.RETURN_VAR, scope);
				implicit = (v == null)
					|| ((v instanceof Field)
						&& ((shField = (Field) v).getDeclaringType () == shell));
			}
			else
			{
				implicit = _t.getText ().equals ("$")
					&& (resolver.resolveIfDeclared (_t, Members.VARIABLE | Resolver.RETURN_VAR, scope) == null);
			}
		}
		else
		{
			implicit = false;
		}
	}
	:	(   {implicit}? id:IDENT
		|   expr1=expr[scope]
		)
		expr2=expr[scope]
		{
			if (implicit && (expr2 != null))
			{
				if (expr2.etype == TypeId.VOID)
				{
					problems.addSemanticError
						(I18N, ProblemReporter.IMPLICIT_VOID_VARIABLE, pos);
					return null;
				}
				Type t = expr2.getType ();
				if ((shField != null) && !accessedShellFields.contains (shField.getDescriptor ()))
				{
					if (Reflection.equal (t, Type.NULL)
						&& (shField.getType ().getTypeId () == TypeId.OBJECT))
					{
						t = shField.getType ();
					}
					if (!Reflection.isWideningConversion (t, shField.getType ()))
					{
						shField = null;
					}
				}
				if (Reflection.equal (t, Type.NULL))
				{
					t = Type.OBJECT;
				}
				if (shField != null)
				{
					expr1 = compileFieldExpression (shField, null, scope, id);
				}
				else
				{
					BlockScope s = scope;
					boolean sb = s.getEnclosingScope () == shellBlockScope;
					if (sb)
					{
						s = shellBlockScope;
					}
					Local loc = declareLocal (s, id, 0, t);
					expr1 = loc.createGet ();
					if (sb)
					{
						shellBlock.insertBranchNode (0, loc.createGet ().toAssignment (Assignment.SIMPLE).add (Expression.createConst (t, null)));
 					}
				}
			}
			pos = pos.getFirstChild ();
			e = compileAssignment
				(expr1, expr2, pos, pos.getNextSibling (), scope);
		}
	;


compoundAssignOp[BlockScope scope, Expression op, String operatorName, AST root] returns [Expression e]
	{ e = null; Expression expr1, expr2; }
	:	expr1=ex:expr[scope] expr2=expr[scope]
		{
			e = compileCompoundAssignment (op, operatorName, expr1, expr2, 0, scope, ex, root);
		}
	;


name
	:	IDENT
	|	#(DOT name IDENT)
	;


arrayIndex[BlockScope scope] returns [Expression e]
	{
		e = null;
		Expression array = null;
		Object index = null;
		int ic = _t.getNumberOfChildren () - 1;
		Expression[] list = null;
	}
	:	#(iop:INDEX_OP
			(	{ic > 1}? list=exprlist[scope]
			|	array=aex:expr[scope]
				(	(IDENT) => id:IDENT
				|	index=iex:expr[scope]
				)
			)
		)
	{
	compile:
		{
			if (array != null)
			{
				Property s;
				if (id != null)
				{
					if (array instanceof GetProperty)
					{
						s = ((GetProperty) array).getProperty ();
						if ((s = s.getSubProperty (id.getText ())) != null)
						{
							((GetProperty) array).setProperty (s);
							e = array;
							break compile;
						}
					}
					Local w;
					if ((array instanceof GetLocal)
						&& ((w = ((GetLocal) array).getLocal (0).wrapper) != null))
					{
						s = w.wrapped.getPatternBuilder ().getModel ().getWrapProperty (w.getType ());
						if (s != null)
						{
							s = s.getTypeCastProperty (array.getType ());
							if ((s = s.getSubProperty (id.getText ())) != null)
							{
								e = new GetProperty (s).add (w.createExpression (scope, iop));
								break compile;
							}
						}
					}
					de.grogra.xl.property.CompiletimeModel cm = scope.getPropertyModel (array.getType (), this, iop);
					if (cm != null)
					{
						s = cm.getDirectProperty (array.getType (), id.getText ());
						if (s != null)
						{
							e = new GetProperty (s).add (array);
							break compile;
						}
					}
					iex = id;
					index = resolver.resolveExpressionName (iex, scope);
				}

				Type t = array.getType ();
				if (Reflection.isArray (t))
				{
					e = assignmentConversion ((Expression) index, Type.INT, scope, iex);
					if (array instanceof GetProperty)
					{
						s = ((GetProperty) array).getProperty ();
						s = s.getComponentProperty ();
						if (s != null)
						{
							((GetProperty) array).setProperty (s);
							array.add (e);
							e = array;
							break compile;
						}
					}
					t = t.getComponentType ();
					e = new GetArrayComponent (t).add (array).add (e);
					break compile;
				}
				if (index != null)
				{
					list = new Expression[] {array, (Expression) index};
				}
			}
			if (list != null)
			{
				e = compileOperator (null, OPERATOR_NAME_INDEX, list, 0, scope, iop);
			}
			else
			{
				e = null;
			}
		}
	}
	;


newExpression[BlockScope scope] returns [Expression e]
	{ e = null; Type t; Expression[] args; Expression e2 = null; }
	:	#(n:NEW t=ts:typeSpec[scope]
			(   args=arglist[scope]
				(	e=anonymousClass[scope, t, ts, null, args, n]
				|	{
						e = compileConstructorInvocation
							(new New (t), null, args, scope, n, false);
					}
				)
			|   e=arrayInitializer[scope, t]
			|   #(DIMLIST
					{e = new CreateArray (t);}
					(e2=dim:expr[scope]
						{
							if (e2 != null)
							{
								e2 = assignmentConversion (e2, Type.INT, scope, dim);
								if (e != null)
								{
									e.add (e2);
								}
							}
						}
					)*
				)
			)
		)
	;


literal returns [Expression e]
	{
		e = null;
		ASTWithToken ast = (ASTWithToken) _t;
		String text = ast.getText ();
	}
	:	BOOLEAN_LITERAL
		{e = new BooleanConst (text.equals ("true"));}
	|	(INT_LITERAL | LONG_LITERAL | FLOAT_LITERAL | DOUBLE_LITERAL)
		{
			de.grogra.grammar.NumberLiteral t
				= (de.grogra.grammar.NumberLiteral) ast.token;
			switch (ast.getType ())
			{
				case INT_LITERAL:
					try
					{
						e = new IntConst ((t == null) ? Integer.parseInt (text)
										  : t.intValue ());
					}
					catch (NumberFormatException n)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.ILLEGAL_INT, ast);
						e = new Expression (Type.INT);
					}
					break;
				case LONG_LITERAL:
					try
					{
						e = new LongConst ((t == null) ? Long.parseLong (text)
										   : t.longValue ());
					}
					catch (NumberFormatException n)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.ILLEGAL_LONG, ast);
						e = new Expression (Type.LONG);
					}
					break;
				case FLOAT_LITERAL:
					try
					{
						e = new FloatConst ((t == null) ? Float.parseFloat (text)
											: t.floatValue ());
					}
					catch (NumberFormatException n)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.ILLEGAL_FLOAT, ast);
						e = new Expression (Type.FLOAT);
					}
					break;
				case DOUBLE_LITERAL:
					try
					{
						e = new DoubleConst ((t == null) ? Double.parseDouble (text)
											 : t.doubleValue ());
					}
					catch (NumberFormatException n)
					{
						problems.addSemanticError
							(I18N, ProblemReporter.ILLEGAL_DOUBLE, ast);
						e = new Expression (Type.DOUBLE);
					}
					break;
			}
		}
	|	CHAR_LITERAL
		{e = new CharConst (text.charAt (0));}
	|	STRING_LITERAL
		{e = new ObjectConst (text.intern (), Type.STRING);}
	;


modifiers[long allowed, long implicit, long incompatibleWithAbstract,
		  List<Annotation> annots, Scope scope]
	returns [long ms]
	{
		long m;
		ms = 0;
		boolean check = currentCompilationUnitScope.properties.put (_t, this) == null;
		AnnotationInfo info = null;
	}
	:	#(mods:MODIFIERS
			(   m=mod:modifier
				{
					if ((m & allowed) == 0)
					{
						if (check)
						{
							problems.addSemanticError
								(I18N.msg (ProblemReporter.ILLEGAL_MODIFIER,
										   modifiersToString (m)),
								 mod);
						}
					}
					else if ((m & ms) != 0)
					{
						if (check)
						{
							problems.addSemanticError
								(I18N.msg (ProblemReporter.DUPLICATE_MODIFIER,
										   modifiersToString (m)),
								 mod);
						}
					}
					else if (((m & Member.ACCESS_MODIFIERS) != 0)
							 && ((ms & Member.ACCESS_MODIFIERS) != 0))
					{
						if (check)
						{
							problems.addSemanticError
								(I18N, ProblemReporter.DUPLICATE_ACCESS_MODIFIER, mod);
						}
					}
					else
					{
						if (check && ((m & implicit) != 0)
							&& problems.isWarning (WARN_ON_IMPLICIT_MODIFIER))
						{
							problems.addSemanticWarning
								(I18N.msg (ProblemReporter.REDUNDANT_MODIFIER,
										   modifiersToString (m)),
								 mod);
						}
						ms |= m;
					}
				}
			|	{(run >= METHOD_DECLARATION) && (annots != null)}?
				info=an:annotation[scope]
				{
				checkAnnotation:
					if ((info != null) && (info.run == run))
					{
						for (int i = annots.size () - 1; i >= 0; i--)
						{
							if (Reflection.equal (info.annotationType (),
												  annots.get (i).annotationType ()))
							{
								problems.addSemanticError
									(I18N.msg (ProblemReporter.DUPLICATE_ANNOTATION,
											   Reflection.getDescription (info.annotationType ())), an);
								break checkAnnotation;
							}
						}
						annots.add (info);
					}
				}
			|	ANNOTATION
			)*
		)
		{
			if ((ms & Member.ABSTRACT) != 0)
			{
				if (check)
				{
					for (int i = 1; i != 0; i <<= 1)
					{
						if ((ms & incompatibleWithAbstract & i) != 0)
						{
							problems.addSemanticError
								(I18N.msg
								 (ProblemReporter.INCOMPATIBLE_MODIFIERS,
								  "abstract", modifiersToString (i)),
								 mods);
						}
					}
				}
				ms &= ~incompatibleWithAbstract;
			}
			if ((allowed & ms & (Member.FINAL | Member.VOLATILE))
				== (Member.FINAL | Member.VOLATILE))
			{
				if (check)
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.INCOMPATIBLE_MODIFIERS,
								   "final", "volatile"), mods);
				}
				ms &= ~Member.VOLATILE;
			}
			ms |= implicit;
			if ((ms & MOD_CONST) != 0)
			{
				ms |= Member.STATIC | Member.FINAL;
			}
		}
	;


modifier returns [long m]
	{ m = 0; }
	:	PRIVATE_ { m = Member.PRIVATE; }
	|	PUBLIC_ { m = Member.PUBLIC; }
	|	PROTECTED_ { m = Member.PROTECTED; }
	|	STATIC_ { m = Member.STATIC; }
	|	TRANSIENT_ { m = Member.TRANSIENT; }
	|	FINAL_ { m = Member.FINAL; }
	|	ABSTRACT_ { m = Member.ABSTRACT; }
	|	NATIVE_ { m = Member.NATIVE; }
	|	SYNCHRONIZED_ { m = Member.SYNCHRONIZED; }
	|	VOLATILE_ { m = Member.VOLATILE; }
	|	STRICT_ { m = Member.STRICT; }
	|	ITERATING_ { m = MOD_ITERATING; }
	|	CONST_ { m = MOD_CONST; }
	|	VARARGS_ { m |= Member.VARARGS; }
	|	STATIC_MEMBER_CLASSES {m |= MOD_STATIC_MEMBER_CLASSES; }
	;


annotation[Scope scope] returns [AnnotationInfo info]
	{
		Expression[] list;
		ASTWithToken value = null;
		int ec = problems.getErrorCount ();
		info = null;
		Type atype;
	}
	:	#(a:ANNOTATION atype=cls:classType[scope]
			{
				info = (AnnotationInfo) currentCompilationUnitScope.properties.get (a);
				if (info == null)
				{
					info = new AnnotationInfo (this, atype, scope, a);
					currentCompilationUnitScope.properties.put (a, info);
				}
				if (!isClassReady (atype))
				{
					info.run = COMPILATION;
				}
				else
				{
					info.init (cls);
				}
			}
			(	MARKER
			|	#(se:SINGLE_ELEMENT
					{
						value = new ASTWithToken ();
						value.initialize (se);
						value.setText ("value");
					}
					elementValuePair[info, value]
				)
			|	#(NORMAL
					(#(ASSIGN id:IDENT elementValuePair[info, id]))*
				)
			)
			{
				if (info.run == run)
				{
					info.finish (problems.getErrorCount () > ec);
				}
			}
		)
	;

	
elementValuePair[AnnotationInfo info, AST element]
	{
		Type elementType = null;
		AST pos = _t;
		Object value = null;
		Type ct = null;
		ObjectList<Object> values = null;
	}
	:
	{
		problems.disableAdd ();
		elementType = (info.run == run) ? info.addElement (element) : null;
		if (elementType != null)
		{
			ct = elementType.getComponentType ();
			if (ct != null)
			{
				if (Reflection.equal (Type.CLASS, ct))
				{
					elementType = Type.TYPE.getArrayType ();
				}
				else if (Reflection.equal (java.lang.annotation.Annotation.class, ct))
				{
					elementType = ANNOTATION_ARRAY_TYPE;
				}
				else if (!Reflection.isPrimitiveOrString (ct))
				{
					elementType = Type.STRING.getArrayType ();
				}
			}
		}
	}
	(	{elementType == null}? .
	|	#(ARRAY_INIT
			{
				if (ct == null)
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.ARRAYINIT_FOR_NONARRAY, elementType), pos);
				}
				values = new ObjectList<Object> ();
			}
			(	value=elementValue[ct, info]
				{
					if (value != null)
					{
						values.add (value);
					}
				}
			)*
			{
				if ((ct != null) && (info.run == run))
				{
					value = elementType.createArray (values.size ());
					for (int i = 0; i < values.size (); i++)
					{
						Array.set (value, i, values.get (i));
					}
					info.setValue (element.getText (), value);
				}
			}
		)
	|	value=elementValue[(ct != null) ? ct : elementType, info]
		{
			if ((info.run == run) && (value != null))
			{
				if (ct != null)
				{
					Object array = elementType.createArray (1);
					Array.set (array, 0, value);
					value = array;
				}
				info.setValue (element.getText (), value);
			}
		}
	)
	{
		RecognitionExceptionList errs = problems.enableAdd ();
		if (errs.containsErrors ())
		{
			if (info.run != COMPILATION)
			{
				info.deferToCompilation ();
			}
			else if (run == COMPILATION)
			{
				problems.addAll (errs);
			}
		}
	}
	;


elementValue[Type expected, AnnotationInfo info] returns [Object value]
	{
		AST pos = _t;
		value = null;
		Expression e;
		AnnotationInfo a = null;
	}
	:	{run != info.run}? ~ARRAY_INIT
	|	a=annotation[info.scope]
		{
			if ((expected != null) && (a != null))
			{
				if (Reflection.equal (a.annotationType (), expected))
				{
					value = a;
				}
				else
				{
					problems.addSemanticError (I18N.msg (ProblemReporter.UNEXPECTED_TYPE, a.annotationType ().getName (), expected.getName ()), pos);
				}
			}
		}
	|	e=expr[info.scope]
		{
			if ((expected != null) && (e != null))
			{
				int ec = problems.getErrorCount ();
				e = standardImplicitConversion (e, expected, true, info.scope, pos, ProblemReporter.ILLEGAL_ASSIGNMENT_CONVERSION, false);
				if (problems.getErrorCount () == ec)
				{
					if (e.isPrimitiveOrStringConstant ())
					{
						value = e.evaluateAsObject (null);
					}
					else if ((e instanceof ClassConst)
							 && Reflection.equal (expected, Type.CLASS))
					{
						value = ((ClassConst) e).value;
					}
					if (value == null)
					{
						problems.addSemanticError (I18N, ProblemReporter.ANNOTATION_CONSTANT_EXPECTED, pos);
					}
				}
			}
		}
	;
	exception
	catch [ClassNotReadyException ex]
	{
		assert run == METHOD_DECLARATION;
		info.deferToCompilation ();
		_t = pos.getNextSibling ();
	}
