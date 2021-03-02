// $ANTLR 2.7.7 (20111006): "Compiler.tree.g" -> "Compiler.java"$


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

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


/* class Compiler extends antlr.TreeParser       implements CompilerTokenTypes
, */ public class Compiler extends CompilerBase {

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

public Compiler() {
	tokenNames = _tokenNames;
}

	public final CompilationUnitScope  compilationUnit(AST _t,
		ClassPath cpath, Scope imports, Annotation[] annotations, String source, CClass shell
	) throws RecognitionException {
		CompilationUnitScope s;
		
		AST compilationUnit_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST pid = null;
		AST m = null;
		AST c = null;
		AST i = null;
		
				this.shell = shell;
				s = null;
			
		
		AST __t2 = _t;
		AST tmp1_AST_in = (AST)_t;
		match(_t,COMPILATION_UNIT);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case PACKAGE:
		{
			AST __t4 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,PACKAGE);
			_t = _t.getFirstChild();
			pid = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			_t = __t4;
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		case MODULE:
		case CLASS:
		case INTERFACE:
		case IMPORT_ON_DEMAND:
		case STATIC_IMPORT_ON_DEMAND:
		case SINGLE_TYPE_IMPORT:
		case SINGLE_STATIC_IMPORT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
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
								units.put (compilationUnit_AST_in, s);
							}
							else
							{
								s = units.get (compilationUnit_AST_in);
								currentPackage = s.getPackage ();
							}
							currentCompilationUnitScope = s;
						
		}
		{
		_loop6:
		do {
			if (_t==null) _t=ASTNULL;
			if (((_t.getType() >= IMPORT_ON_DEMAND && _t.getType() <= SINGLE_STATIC_IMPORT))) {
				importDecl(_t,s);
				_t = _retTree;
			}
			else {
				break _loop6;
			}
			
		} while (true);
		}
		{
		_loop11:
		do {
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case MODULE:
			{
				AST __t8 = _t;
				m = _t==ASTNULL ? null :(AST)_t;
				match(_t,MODULE);
				_t = _t.getFirstChild();
				classDecl(_t,s, TOP_LEVEL_MODULE_MODIFIERS, MOD_MODULE, m);
				_t = _retTree;
				_t = __t8;
				_t = _t.getNextSibling();
				break;
			}
			case CLASS:
			{
				AST __t9 = _t;
				c = _t==ASTNULL ? null :(AST)_t;
				match(_t,CLASS);
				_t = _t.getFirstChild();
				classDecl(_t,s, Type.TOP_LEVEL_CLASS_MODIFIERS | MOD_STATIC_MEMBER_CLASSES, 0, c);
				_t = _retTree;
				_t = __t9;
				_t = _t.getNextSibling();
				break;
			}
			case INTERFACE:
			{
				AST __t10 = _t;
				i = _t==ASTNULL ? null :(AST)_t;
				match(_t,INTERFACE);
				_t = _t.getFirstChild();
				classDecl(_t,s, Type.TOP_LEVEL_INTERFACE_MODIFIERS, Member.ABSTRACT | Member.INTERFACE, i);
				_t = _retTree;
				_t = __t10;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				break _loop11;
			}
			}
		} while (true);
		}
		_t = __t2;
		_t = _t.getNextSibling();
		_retTree = _t;
		return s;
	}
	
	public final void name(AST _t) throws RecognitionException {
		
		AST name_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IDENT:
		{
			AST tmp3_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			break;
		}
		case DOT:
		{
			AST __t427 = _t;
			AST tmp4_AST_in = (AST)_t;
			match(_t,DOT);
			_t = _t.getFirstChild();
			name(_t);
			_t = _retTree;
			AST tmp5_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t427;
			_t = _t.getNextSibling();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
	}
	
	public final void importDecl(AST _t,
		final CompilationUnitScope scope
	) throws RecognitionException {
		
		AST importDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST iod = null;
		AST siodType = null;
		AST sti = null;
		AST ssiType = null;
		AST member = null;
		
				final Package curPkg = currentPackage;
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IMPORT_ON_DEMAND:
		{
			AST __t13 = _t;
			AST tmp6_AST_in = (AST)_t;
			match(_t,IMPORT_ON_DEMAND);
			_t = _t.getFirstChild();
			iod = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			_t = __t13;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case STATIC_IMPORT_ON_DEMAND:
		{
			AST __t14 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,STATIC_IMPORT_ON_DEMAND);
			_t = _t.getFirstChild();
			siodType = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			_t = __t14;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case SINGLE_TYPE_IMPORT:
		{
			AST __t15 = _t;
			AST tmp8_AST_in = (AST)_t;
			match(_t,SINGLE_TYPE_IMPORT);
			_t = _t.getFirstChild();
			sti = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			_t = __t15;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case SINGLE_STATIC_IMPORT:
		{
			AST __t16 = _t;
			AST tmp9_AST_in = (AST)_t;
			match(_t,SINGLE_STATIC_IMPORT);
			_t = _t.getFirstChild();
			ssiType = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			member = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t16;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
	}
	
	public final void classDecl(AST _t,
		Scope scope, long allowedMods, long implMods, AST root
	) throws RecognitionException {
		
		AST classDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		AST ext = null;
		AST impl = null;
		AST params = null;
		AST pd = null;
		AST pid = null;
		AST method = null;
		AST args = null;
		AST initBlock = null;
		AST instPos = null;
		AST sl = null;
		
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
			
		
		m=modifiers(_t,allowedMods, implMods, Member.FINAL,
					(s != null) ? s.getDeclaredType ().getDeclaredAnnotations () : null,
					scope);
		_t = _retTree;
		id = (AST)_t;
		match(_t,IDENT);
		_t = _t.getNextSibling();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case EXTENDS:
		{
			AST __t19 = _t;
			AST tmp10_AST_in = (AST)_t;
			match(_t,EXTENDS);
			_t = _t.getFirstChild();
			ext = _t==ASTNULL ? null : (AST)_t;
			extType=classList(_t,scope);
			_t = _retTree;
			_t = __t19;
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		case MODULE:
		case CLASS:
		case INTERFACE:
		case IMPLEMENTS:
		case PARAMETERS:
		case ARGLIST:
		case SLIST:
		case INSTANTIATOR:
		case METHOD:
		case CONSTRUCTOR:
		case VARIABLE_DEF:
		case INSTANCE_INIT:
		case STATIC_INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IMPLEMENTS:
		{
			AST __t21 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,IMPLEMENTS);
			_t = _t.getFirstChild();
			impl = _t==ASTNULL ? null : (AST)_t;
			implType=classList(_t,scope);
			_t = _retTree;
			_t = __t21;
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		case MODULE:
		case CLASS:
		case INTERFACE:
		case PARAMETERS:
		case ARGLIST:
		case SLIST:
		case INSTANTIATOR:
		case METHOD:
		case CONSTRUCTOR:
		case VARIABLE_DEF:
		case INSTANCE_INIT:
		case STATIC_INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case PARAMETERS:
		{
			AST __t23 = _t;
			params = _t==ASTNULL ? null :(AST)_t;
			match(_t,PARAMETERS);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
								assert module;
								pids = new AST[params.getNumberOfChildren ()];
								mods = new long[pids.length];
								types = new Type[pids.length];
								inherited = new boolean[pids.length];
								methods = new AST[pids.length];
								i = 0;
							
			}
			{
			_loop28:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PARAMETER_DEF)) {
					AST __t25 = _t;
					pd = _t==ASTNULL ? null :(AST)_t;
					match(_t,PARAMETER_DEF);
					_t = _t.getFirstChild();
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case MODIFIERS:
					{
						if ( inputState.guessing==0 ) {
							
														if ((info != null) && (info.moduleFields[i] != null))
														{
															fieldAnnot = ((XField) info.moduleFields[i]).getDeclaredAnnotations ();
															if (fieldAnnot.isEmpty ())
															{
																fieldAnnot.add (new AnnotationImpl (de.grogra.annotation.Editable.class));
															}
														}
													
						}
						pm=modifiers(_t,Member.FINAL, 0, 0, fieldAnnot, scope);
						_t = _retTree;
						t=typeSpec(_t,scope);
						_t = _retTree;
						if ( inputState.guessing==0 ) {
							types[i] = t; mods[i] = pm;
						}
						break;
					}
					case SUPER:
					{
						AST tmp12_AST_in = (AST)_t;
						match(_t,SUPER);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							inherited[i] = true; inheritedCount++;
						}
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					pid = (AST)_t;
					match(_t,IDENT);
					_t = _t.getNextSibling();
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case IDENT:
					{
						method = (AST)_t;
						match(_t,IDENT);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							methods[i] = method;
						}
						break;
					}
					case 3:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					_t = __t25;
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						pids[i++] = pid;
					}
				}
				else {
					break _loop28;
				}
				
			} while (true);
			}
			_t = __t23;
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		case MODULE:
		case CLASS:
		case INTERFACE:
		case ARGLIST:
		case SLIST:
		case INSTANTIATOR:
		case METHOD:
		case CONSTRUCTOR:
		case VARIABLE_DEF:
		case INSTANCE_INIT:
		case STATIC_INIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
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
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==ARGLIST))&&(run == COMPILATION)) {
			args = _t==ASTNULL ? null : (AST)_t;
			list=arglist(_t,modCtorScope);
			_t = _retTree;
		}
		else if ((_t.getType()==ARGLIST)) {
			AST tmp13_AST_in = (AST)_t;
			match(_t,ARGLIST);
			_t = _t.getNextSibling();
		}
		else if ((_tokenSet_0.member(_t.getType()))) {
			if ( inputState.guessing==0 ) {
				
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
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==SLIST))&&(run == COMPILATION)) {
			initBlock = _t==ASTNULL ? null : (AST)_t;
			init=slist(_t,modCtorScope);
			_t = _retTree;
		}
		else if ((_t.getType()==SLIST)) {
			AST tmp14_AST_in = (AST)_t;
			match(_t,SLIST);
			_t = _t.getNextSibling();
		}
		else if ((_tokenSet_1.member(_t.getType()))) {
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		if ( inputState.guessing==0 ) {
			
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
		{
		_loop32:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_2.member(_t.getType()))) {
				classMember(_t,s, iface);
				_t = _retTree;
			}
			else {
				break _loop32;
			}
			
		} while (true);
		}
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case INSTANTIATOR:
		{
			AST __t34 = _t;
			instPos = _t==ASTNULL ? null :(AST)_t;
			match(_t,INSTANTIATOR);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==SLIST))&&(run == COMPILATION)) {
				if ( inputState.guessing==0 ) {
					
										Expression e = info.instState.createGet ();
										e = compileMethodInvocation (e, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, instPos);
										prodScope = createProduceScope (info.instMethodScope, info.instMethodScope.getBlock (), e, instPos);
									
				}
				inst=slist(_t,prodScope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else if ((_t.getType()==SLIST)) {
				sl = (AST)_t;
				match(_t,SLIST);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				throw new NoViableAltException(_t);
			}
			
			}
			_t = __t34;
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
	}
	
	public final long  modifiers(AST _t,
		long allowed, long implicit, long incompatibleWithAbstract,
		  List<Annotation> annots, Scope scope
	) throws RecognitionException {
		long ms;
		
		AST modifiers_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST mods = null;
		AST mod = null;
		AST an = null;
		
				long m;
				ms = 0;
				boolean check = currentCompilationUnitScope.properties.put (_t, this) == null;
				AnnotationInfo info = null;
			
		
		AST __t444 = _t;
		mods = _t==ASTNULL ? null :(AST)_t;
		match(_t,MODIFIERS);
		_t = _t.getFirstChild();
		{
		_loop446:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_3.member(_t.getType()))) {
				mod = _t==ASTNULL ? null : (AST)_t;
				m=modifier(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else if (((_t.getType()==ANNOTATION))&&((run >= METHOD_DECLARATION) && (annots != null))) {
				an = _t==ASTNULL ? null : (AST)_t;
				info=annotation(_t,scope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else if ((_t.getType()==ANNOTATION)) {
				AST tmp15_AST_in = (AST)_t;
				match(_t,ANNOTATION);
				_t = _t.getNextSibling();
			}
			else {
				break _loop446;
			}
			
		} while (true);
		}
		_t = __t444;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return ms;
	}
	
	public final Type[]  classList(AST _t,
		Scope scope
	) throws RecognitionException {
		Type[] types;
		
		AST classList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				int i = 0;
				for (AST a = _t; a != null; a = a.getNextSibling ())
				{
					i++;
				}
				types = new Type[i];
				i = 0;
				Type t;
			
		
		{
		_loop42:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==IDENT||_t.getType()==DECLARING_TYPE||_t.getType()==DOT)) {
				t=classType(_t,scope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					types[i++] = t;
				}
			}
			else {
				break _loop42;
			}
			
		} while (true);
		}
		_retTree = _t;
		return types;
	}
	
	public final Type  typeSpec(AST _t,
		Scope scope
	) throws RecognitionException {
		Type t;
		
		AST typeSpec_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		t = null;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ARRAY_DECLARATOR:
		{
			AST __t75 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,ARRAY_DECLARATOR);
			_t = _t.getFirstChild();
			t=typeSpec(_t,scope);
			_t = _retTree;
			_t = __t75;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case IDENT:
		case DECLARING_TYPE:
		case DOT:
		{
			t=classType(_t,scope);
			_t = _retTree;
			break;
		}
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		{
			t=builtInType(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return t;
	}
	
	public final Expression[]  arglist(AST _t,
		Scope scope
	) throws RecognitionException {
		Expression[] list;
		
		AST arglist_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		AST __t307 = _t;
		AST tmp17_AST_in = (AST)_t;
		match(_t,ARGLIST);
		_t = _t.getFirstChild();
		list=exprlist(_t,scope);
		_t = _retTree;
		_t = __t307;
		_t = _t.getNextSibling();
		_retTree = _t;
		return list;
	}
	
	public final Expression  slist(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST slist_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
				BlockScope s = new BlockScope (scope);
			
		
		slistInScope(_t,s);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						e = s.getBlock ();
					
		}
		_retTree = _t;
		return e;
	}
	
	public final void classMember(AST _t,
		TypeScope scope, boolean iface
	) throws RecognitionException {
		
		AST classMember_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		AST m = null;
		AST i = null;
		
				Expression e = null;
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case CLASS:
		{
			AST __t49 = _t;
			c = _t==ASTNULL ? null :(AST)_t;
			match(_t,CLASS);
			_t = _t.getFirstChild();
			classDecl(_t,scope, Type.MEMBER_CLASS_MODIFIERS | MOD_STATIC_MEMBER_CLASSES,
							iface ? Member.PUBLIC | Member.STATIC
							: ((scope.getModifiersEx () & MOD_STATIC_MEMBER_CLASSES) != 0) ? Member.STATIC : 0, c);
			_t = _retTree;
			_t = __t49;
			_t = _t.getNextSibling();
			break;
		}
		case MODULE:
		{
			AST __t50 = _t;
			m = _t==ASTNULL ? null :(AST)_t;
			match(_t,MODULE);
			_t = _t.getFirstChild();
			classDecl(_t,scope, MEMBER_MODULE_MODIFIERS,
							 iface ? Member.PUBLIC | Member.STATIC | MOD_MODULE : Member.STATIC | MOD_MODULE, m);
			_t = _retTree;
			_t = __t50;
			_t = _t.getNextSibling();
			break;
		}
		case INTERFACE:
		{
			AST __t51 = _t;
			i = _t==ASTNULL ? null :(AST)_t;
			match(_t,INTERFACE);
			_t = _t.getFirstChild();
			classDecl(_t,scope, Type.MEMBER_INTERFACE_MODIFIERS,
								iface ? Member.PUBLIC | Member.STATIC | Member.ABSTRACT | Member.INTERFACE
								: Member.STATIC | Member.ABSTRACT | Member.INTERFACE, i);
			_t = _retTree;
			_t = __t51;
			_t = _t.getNextSibling();
			break;
		}
		case VARIABLE_DEF:
		{
			e=variableDecl(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
							(Reflection.isStatic (declaredVariable) ? scope.staticInit : scope.instanceInit)
								.addExpression (e);
						
			}
			break;
		}
		case METHOD:
		{
			methodDecl(_t,scope, iface);
			_t = _retTree;
			break;
		}
		case CONSTRUCTOR:
		{
			constructorDecl(_t,scope);
			_t = _retTree;
			break;
		}
		case INSTANCE_INIT:
		{
			instanceInit(_t,scope);
			_t = _retTree;
			break;
		}
		case STATIC_INIT:
		{
			staticInit(_t,scope);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
	}
	
	public final Type[]  extendsClause(AST _t,
		Scope scope
	) throws RecognitionException {
		Type[] types;
		
		AST extendsClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				types = null;
				int i = 0;
				Type t;
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case EXTENDS:
		{
			AST __t37 = _t;
			AST tmp18_AST_in = (AST)_t;
			match(_t,EXTENDS);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				types = new Type[extendsClause_AST_in.getNumberOfChildren ()];
			}
			{
			_loop39:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==IDENT||_t.getType()==DECLARING_TYPE||_t.getType()==DOT)) {
					t=classType(_t,scope);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						types[i++] = t;
					}
				}
				else {
					break _loop39;
				}
				
			} while (true);
			}
			_t = __t37;
			_t = _t.getNextSibling();
			break;
		}
		case 3:
		{
			if ( inputState.guessing==0 ) {
				types = Type.TYPE_0;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return types;
	}
	
	public final Type  classType(AST _t,
		Scope scope
	) throws RecognitionException {
		Type t;
		
		AST classType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
				t = null;
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IDENT:
		case DOT:
		{
			id = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case DECLARING_TYPE:
		{
			AST tmp19_AST_in = (AST)_t;
			match(_t,DECLARING_TYPE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							t = TypeScope.get (scope).getDeclaredType ();
						
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return t;
	}
	
	public final Expression  anonymousClass(AST _t,
		BlockScope scope, Type type, AST typeAST, Expression qualifier,
			   Expression[] args, AST root
	) throws RecognitionException {
		Expression e;
		
		AST anonymousClass_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		
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
			
		
		AST __t44 = _t;
		c = _t==ASTNULL ? null :(AST)_t;
		match(_t,CLASS);
		_t = _t.getFirstChild();
		anonymousClassImpl(_t,s);
		_t = _retTree;
		_t = __t44;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final void anonymousClassImpl(AST _t,
		TypeScope scope
	) throws RecognitionException {
		
		AST anonymousClassImpl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		{
		_loop47:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_2.member(_t.getType()))) {
				classMember(_t,scope, false);
				_t = _retTree;
			}
			else {
				break _loop47;
			}
			
		} while (true);
		}
		_retTree = _t;
	}
	
	public final Expression  variableDecl(AST _t,
		Scope scope
	) throws RecognitionException {
		Expression e;
		
		AST variableDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		AST v = null;
		AST v2 = null;
		
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
			
		
		AST __t70 = _t;
		AST tmp20_AST_in = (AST)_t;
		match(_t,VARIABLE_DEF);
		_t = _t.getFirstChild();
		m=modifiers(_t,allowedMods, implicitMods, 0, (f != null) ? f.getDeclaredAnnotations () : null, scope);
		_t = _retTree;
		t=typeSpec(_t,scope);
		_t = _retTree;
		id = (AST)_t;
		match(_t,IDENT);
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==ASSIGN))&&(l != null)) {
			AST __t72 = _t;
			AST tmp21_AST_in = (AST)_t;
			match(_t,ASSIGN);
			_t = _t.getFirstChild();
			v = _t==ASTNULL ? null : (AST)_t;
			e=initializer(_t,s, t);
			_t = _retTree;
			_t = __t72;
			_t = _t.getNextSibling();
		}
		else if (((_t.getType()==ASSIGN))&&(!local)) {
			AST __t73 = _t;
			AST tmp22_AST_in = (AST)_t;
			match(_t,ASSIGN);
			_t = _t.getFirstChild();
			v2 = _t==ASTNULL ? null : (AST)_t;
			e=fieldInitializer(_t,(MethodScope) s, f);
			_t = _retTree;
			_t = __t73;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
									v = v2;
								
			}
		}
		else if ((_t.getType()==ASSIGN)) {
			AST tmp23_AST_in = (AST)_t;
			match(_t,ASSIGN);
			_t = _t.getNextSibling();
		}
		else if ((_t.getType()==3)) {
			if ( inputState.guessing==0 ) {
				
									if ((run == COMPILATION) && (l != null) && (shell != null) && ((m & Member.FINAL) == 0))
									{
										v = var;
										e = Expression.createConst (t, null);
									}
								
			}
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		_t = __t70;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final void methodDecl(AST _t,
		TypeScope scope, boolean iface
	) throws RecognitionException {
		
		AST methodDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
				AST root = _t;
				Expression st = null;
				long mods;
				Type retType;
				MethodScope s = (MethodScope) currentCompilationUnitScope.properties.get (root);
			
		
		AST __t60 = _t;
		AST tmp24_AST_in = (AST)_t;
		match(_t,METHOD);
		_t = _t.getFirstChild();
		mods=modifiers(_t,iface ? Method.INTERFACE_MODIFIERS | MOD_ITERATING
								: Method.MODIFIERS | MOD_ITERATING,
								iface ? Member.PUBLIC | Member.ABSTRACT : 0,
								ABSTRACT_METHOD_INCOMPATIBLE,
								(run == COMPILATION) ? s.getMethod ().getDeclaredAnnotations () : null, scope);
		_t = _retTree;
		retType=typeSpec(_t,scope);
		_t = _retTree;
		id = (AST)_t;
		match(_t,IDENT);
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		AST __t61 = _t;
		AST tmp25_AST_in = (AST)_t;
		match(_t,PARAMETERS);
		_t = _t.getFirstChild();
		parameterList(_t,scope, s, id);
		_t = _retTree;
		_t = __t61;
		_t = _t.getNextSibling();
		AST __t62 = _t;
		AST tmp26_AST_in = (AST)_t;
		match(_t,THROWS);
		_t = _t.getFirstChild();
		throwsList(_t,s);
		_t = _retTree;
		_t = __t62;
		_t = _t.getNextSibling();
		{
		if (_t==null) _t=ASTNULL;
		if ((_t.getType()==SEMI)) {
			AST tmp27_AST_in = (AST)_t;
			match(_t,SEMI);
			_t = _t.getNextSibling();
		}
		else if (((_t.getType()==SLIST))&&(run == COMPILATION)) {
			st=slist(_t,s);
			_t = _retTree;
		}
		else if ((_t.getType()==SLIST)) {
			AST tmp28_AST_in = (AST)_t;
			match(_t,SLIST);
			_t = _t.getNextSibling();
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		if ( inputState.guessing==0 ) {
			
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
		_t = __t60;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void constructorDecl(AST _t,
		TypeScope scope
	) throws RecognitionException {
		
		AST constructorDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
				AST root = _t;
				Expression st = null;
				long mods;
				MethodScope s = (MethodScope) currentCompilationUnitScope.properties.get (root);
			
		
		AST __t65 = _t;
		AST tmp29_AST_in = (AST)_t;
		match(_t,CONSTRUCTOR);
		_t = _t.getFirstChild();
		mods=modifiers(_t,Method.CONSTRUCTOR_MODIFIERS, 0, 0, (run == COMPILATION) ? s.getMethod ().getDeclaredAnnotations () : null, scope);
		_t = _retTree;
		id = (AST)_t;
		match(_t,IDENT);
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		AST __t66 = _t;
		AST tmp30_AST_in = (AST)_t;
		match(_t,PARAMETERS);
		_t = _t.getFirstChild();
		parameterList(_t,scope, s, id);
		_t = _retTree;
		_t = __t66;
		_t = _t.getNextSibling();
		AST __t67 = _t;
		AST tmp31_AST_in = (AST)_t;
		match(_t,THROWS);
		_t = _t.getFirstChild();
		throwsList(_t,s);
		_t = _retTree;
		_t = __t67;
		_t = _t.getNextSibling();
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==SLIST))&&(run == COMPILATION)) {
			st=slist(_t,s);
			_t = _retTree;
		}
		else if ((_t.getType()==SLIST)) {
			AST tmp32_AST_in = (AST)_t;
			match(_t,SLIST);
			_t = _t.getNextSibling();
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		if ( inputState.guessing==0 ) {
			
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
		_t = __t65;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void instanceInit(AST _t,
		TypeScope scope
	) throws RecognitionException {
		
		AST instanceInit_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				Expression e;
				scope.instanceInit.ast = _t;
			
		
		AST __t79 = _t;
		AST tmp33_AST_in = (AST)_t;
		match(_t,INSTANCE_INIT);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==SLIST))&&(run == COMPILATION)) {
			e=slist(_t,scope.instanceInit);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				scope.instanceInit.addExpression (e);
			}
		}
		else if ((_t.getType()==SLIST)) {
			AST tmp34_AST_in = (AST)_t;
			match(_t,SLIST);
			_t = _t.getNextSibling();
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		_t = __t79;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void staticInit(AST _t,
		TypeScope scope
	) throws RecognitionException {
		
		AST staticInit_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST si = null;
		
				Expression e;
				scope.staticInit.ast = _t;
			
		
		AST __t82 = _t;
		si = _t==ASTNULL ? null :(AST)_t;
		match(_t,STATIC_INIT);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==SLIST))&&(run == COMPILATION)) {
			e=slist(_t,scope.staticInit);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
									scope.staticInit.addExpression (e);
									checkMember (scope, Member.STATIC, si);
								
			}
		}
		else if ((_t.getType()==SLIST)) {
			AST tmp35_AST_in = (AST)_t;
			match(_t,SLIST);
			_t = _t.getNextSibling();
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		_t = __t82;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final void parameterList(AST _t,
		TypeScope ts, MethodScope ms, AST name
	) throws RecognitionException {
		
		AST parameterList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
				long m;
				Type t;
				
				// check if the method is static
		//		boolean isStatic = ms.isStatic ();
				
				// true if all parameters are primitive
		//		boolean isPrimitive = isStatic;
				
				// count the number of parameters
				int paramCount = 0;
				ObjectList<Annotation> annots = null;
			
		
		{
		_loop55:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==PARAMETER_DEF)) {
				AST __t54 = _t;
				AST tmp36_AST_in = (AST)_t;
				match(_t,PARAMETER_DEF);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					
									if (run == METHOD_DECLARATION)
									{
										annots = new ObjectList<Annotation> (0);
									}
									else if (run == COMPILATION)
									{
										annots = ms.getParameter (paramCount).getDeclaredAnnotations ();
									}
								
				}
				m=modifiers(_t,Member.FINAL, 0, 0, annots, ts);
				_t = _retTree;
				t=typeSpec(_t,ts);
				_t = _retTree;
				id = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				_t = __t54;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				break _loop55;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
	}
	
	public final void throwsList(AST _t,
		MethodScope ms
	) throws RecognitionException {
		
		AST throwsList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		
				Type t;
			
		
		{
		_loop58:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==IDENT||_t.getType()==DECLARING_TYPE||_t.getType()==DOT)) {
				c = _t==ASTNULL ? null : (AST)_t;
				t=classType(_t,ms);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				break _loop58;
			}
			
		} while (true);
		}
		_retTree = _t;
	}
	
	public final Expression  initializer(AST _t,
		BlockScope scope, Type type
	) throws RecognitionException {
		Expression e;
		
		AST initializer_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case ASSIGN:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case EXPR:
		case METHOD_CALL:
		case DOT:
		case SUB:
		case LEFT_ARROW:
		case ARROW:
		case QUESTION:
		case MUL:
		case ADD:
		case RANGE:
		case WITH:
		case TYPECAST:
		case TYPECHECK:
		case COM:
		case NOT:
		case NEG:
		case POS:
		case DIV:
		case REM:
		case POW:
		case SHL:
		case SHR:
		case USHR:
		case LE:
		case GE:
		case CMP:
		case NOT_EQUALS:
		case EQUALS:
		case OR:
		case XOR:
		case AND:
		case COR:
		case CAND:
		case ELIST:
		case THIS:
		case NULL_LITERAL:
		case INVALID_EXPR:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case INSTANCEOF:
		case CLASS_LITERAL:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case INC:
		case DEC:
		case POST_INC:
		case POST_DEC:
		case IN:
		case GUARD:
		case ARRAY_ITERATOR:
		case QUERY_EXPR:
		case INVOKE_OP:
		case QUALIFIED_NEW:
		case INDEX_OP:
		case NEW:
		{
			e=expr(_t,scope);
			_t = _retTree;
			break;
		}
		case ARRAY_INIT:
		{
			e=arrayInitializer(_t,scope, type);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
					if (e == null)
					{
						e = new Expression (type);
					}
				
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  fieldInitializer(AST _t,
		MethodScope scope, XField field
	) throws RecognitionException {
		Expression e;
		
		AST fieldInitializer_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		
				AST root = _t;
				e = null;
				FieldInitializer init = (run == COMPILATION)
					? (FieldInitializer) currentCompilationUnitScope.properties.get (root)
					: null;
				if (init != null)
				{
					e = init.expr;
				}
			
		
		if (_t==null) _t=ASTNULL;
		if ((((_t.getType() >= BOOLEAN_LITERAL && _t.getType() <= NORMAL)))&&(init != null)) {
			AST tmp37_AST_in = (AST)_t;
			if ( _t==null ) throw new MismatchedTokenException();
			_t = _t.getNextSibling();
		}
		else {
			boolean synPredMatched195 = false;
			if (_t==null) _t=ASTNULL;
			if (((((_t.getType() >= BOOLEAN_LITERAL && _t.getType() <= NORMAL)))&&((run == TYPE_AND_FIELD_DECLARATION) && Reflection.isFinal (field)))) {
				AST __t195 = _t;
				synPredMatched195 = true;
				inputState.guessing++;
				try {
					{
					constantExprPattern(_t);
					_t = _retTree;
					}
				}
				catch (RecognitionException pe) {
					synPredMatched195 = false;
				}
				_t = __t195;
inputState.guessing--;
			}
			if ( synPredMatched195 ) {
				i = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					
								init = new FieldInitializer ();
								init.options = options;
								init.field = field;
								init.ast = i;
								init.scope = scope;
								currentCompilationUnitScope.properties.put (root, init);
								initializersToCompile.put (field, init);
							
				}
			}
			else if (((_tokenSet_4.member(_t.getType())))&&(run == COMPILATION)) {
				e=initializer(_t,scope, field.getType ());
				_t = _retTree;
			}
			else if (((_t.getType() >= BOOLEAN_LITERAL && _t.getType() <= NORMAL))) {
				AST tmp38_AST_in = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
			}
			else {
				throw new NoViableAltException(_t);
			}
			}
			_retTree = _t;
			return e;
		}
		
	public final Type  builtInType(AST _t) throws RecognitionException {
		Type t;
		
		AST builtInType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		t = null;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case VOID_:
		{
			AST tmp39_AST_in = (AST)_t;
			match(_t,VOID_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.VOID;
			}
			break;
		}
		case BOOLEAN_:
		{
			AST tmp40_AST_in = (AST)_t;
			match(_t,BOOLEAN_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.BOOLEAN;
			}
			break;
		}
		case BYTE_:
		{
			AST tmp41_AST_in = (AST)_t;
			match(_t,BYTE_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.BYTE;
			}
			break;
		}
		case SHORT_:
		{
			AST tmp42_AST_in = (AST)_t;
			match(_t,SHORT_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.SHORT;
			}
			break;
		}
		case CHAR_:
		{
			AST tmp43_AST_in = (AST)_t;
			match(_t,CHAR_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.CHAR;
			}
			break;
		}
		case INT_:
		{
			AST tmp44_AST_in = (AST)_t;
			match(_t,INT_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.INT;
			}
			break;
		}
		case LONG_:
		{
			AST tmp45_AST_in = (AST)_t;
			match(_t,LONG_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.LONG;
			}
			break;
		}
		case FLOAT_:
		{
			AST tmp46_AST_in = (AST)_t;
			match(_t,FLOAT_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.FLOAT;
			}
			break;
		}
		case DOUBLE_:
		{
			AST tmp47_AST_in = (AST)_t;
			match(_t,DOUBLE_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				t = Type.DOUBLE;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return t;
	}
	
	public final Expression  graph(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST graph_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
				AST root = _t;
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case EMPTY:
		{
			AST tmp48_AST_in = (AST)_t;
			match(_t,EMPTY);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							currentQueryModel = scope.getQueryModel (this, root);
							if (currentQueryModel == null)
							{
								problems.addSemanticError (I18N, ProblemReporter.NO_QUERY_MODEL, root);
								currentQueryModel = InvalidQueryModel.INSTANCE;
							}
						
			}
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case ASSIGN:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case EXPR:
		case METHOD_CALL:
		case DOT:
		case SUB:
		case LEFT_ARROW:
		case ARROW:
		case QUESTION:
		case MUL:
		case ADD:
		case RANGE:
		case WITH:
		case TYPECAST:
		case TYPECHECK:
		case COM:
		case NOT:
		case NEG:
		case POS:
		case DIV:
		case REM:
		case POW:
		case SHL:
		case SHR:
		case USHR:
		case LE:
		case GE:
		case CMP:
		case NOT_EQUALS:
		case EQUALS:
		case OR:
		case XOR:
		case AND:
		case COR:
		case CAND:
		case ELIST:
		case THIS:
		case NULL_LITERAL:
		case INVALID_EXPR:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case INSTANCEOF:
		case CLASS_LITERAL:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case INC:
		case DEC:
		case POST_INC:
		case POST_DEC:
		case IN:
		case GUARD:
		case ARRAY_ITERATOR:
		case QUERY_EXPR:
		case INVOKE_OP:
		case QUALIFIED_NEW:
		case INDEX_OP:
		case NEW:
		{
			e=referenceExpr(_t,scope, Graph.class);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  referenceExpr(AST _t,
		Scope scope, Class cls
	) throws RecognitionException {
		Expression e;
		
		AST referenceExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST ex = null;
		e = null;
		
		ex = _t==ASTNULL ? null : (AST)_t;
		e=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  query(AST _t,
		BlockScope scope, ExpressionFactory graph, CompiletimeModel model, boolean forProduction, boolean context
	) throws RecognitionException {
		Expression e;
		
		AST query_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
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
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case QUERY:
		{
			AST __t87 = _t;
			AST tmp49_AST_in = (AST)_t;
			match(_t,QUERY);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EMPTY:
			{
				AST tmp50_AST_in = (AST)_t;
				match(_t,EMPTY);
				_t = _t.getNextSibling();
				break;
			}
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case ASSIGN:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case EXPR:
			case METHOD_CALL:
			case DOT:
			case SUB:
			case LEFT_ARROW:
			case ARROW:
			case QUESTION:
			case MUL:
			case ADD:
			case RANGE:
			case WITH:
			case TYPECAST:
			case TYPECHECK:
			case COM:
			case NOT:
			case NEG:
			case POS:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case NOT_EQUALS:
			case EQUALS:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case ELIST:
			case THIS:
			case NULL_LITERAL:
			case INVALID_EXPR:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case INSTANCEOF:
			case CLASS_LITERAL:
			case QUOTE:
			case ADD_ASSIGN:
			case SUB_ASSIGN:
			case MUL_ASSIGN:
			case DIV_ASSIGN:
			case REM_ASSIGN:
			case POW_ASSIGN:
			case SHR_ASSIGN:
			case USHR_ASSIGN:
			case SHL_ASSIGN:
			case AND_ASSIGN:
			case XOR_ASSIGN:
			case OR_ASSIGN:
			case DEFERRED_ASSIGN:
			case DEFERRED_RATE_ASSIGN:
			case DEFERRED_ADD:
			case DEFERRED_SUB:
			case DEFERRED_MUL:
			case DEFERRED_DIV:
			case DEFERRED_REM:
			case DEFERRED_POW:
			case DEFERRED_OR:
			case DEFERRED_AND:
			case DEFERRED_XOR:
			case DEFERRED_SHL:
			case DEFERRED_SHR:
			case DEFERRED_USHR:
			case INC:
			case DEC:
			case POST_INC:
			case POST_DEC:
			case IN:
			case GUARD:
			case ARRAY_ITERATOR:
			case QUERY_EXPR:
			case INVOKE_OP:
			case QUALIFIED_NEW:
			case INDEX_OP:
			case NEW:
			{
				qs=withBlock(_t,scope, qs, pos);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			pb=compositePattern(_t,s, null, model, false, context);
			_t = _retTree;
			_t = __t87;
			_t = _t.getNextSibling();
			break;
		}
		case EMPTY:
		{
			AST tmp51_AST_in = (AST)_t;
			match(_t,EMPTY);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
								pb = new PatternBuilder (model, null, s, pos);
								pb.allowOpenEnds ();
							
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  withBlock(AST _t,
		BlockScope scope, Expression i, AST block
	) throws RecognitionException {
		Expression e;
		
		AST withBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
				InstanceScope with = null;
				Local local = null;
			
		
		if ( inputState.guessing==0 ) {
			
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
		e=expr(_t,(with != null) ? with : scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final PatternBuilder  compositePattern(AST _t,
		BlockScope scope, PatternBuilder parent, CompiletimeModel model, boolean allowOpen, boolean context
	) throws RecognitionException {
		PatternBuilder builder;
		
		AST compositePattern_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
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
			
		
		AST __t91 = _t;
		AST tmp52_AST_in = (AST)_t;
		match(_t,COMPOUND_PATTERN);
		_t = _t.getFirstChild();
		{
		_loop94:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==VARIABLE_DEF)) {
				AST __t93 = _t;
				AST tmp53_AST_in = (AST)_t;
				match(_t,VARIABLE_DEF);
				_t = _t.getFirstChild();
				modifiers(_t,Member.FINAL, Member.FINAL, 0, null, scope);
				_t = _retTree;
				t=typeSpec(_t,scope);
				_t = _retTree;
				id = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				_t = __t93;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					builder.declareVariable (id, t);
				}
			}
			else {
				break _loop94;
			}
			
		} while (true);
		}
		{
		_loop96:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_5.member(_t.getType()))) {
				predicate(_t,builder, null);
				_t = _retTree;
			}
			else {
				break _loop96;
			}
			
		} while (true);
		}
		_t = __t91;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
					if (context)
					{
						builder.endContext (root);
					}
				
		}
		_retTree = _t;
		return builder;
	}
	
	public final PatternBuilder  nestedCompositePattern(AST _t,
		PatternBuilder pb
	) throws RecognitionException {
		PatternBuilder child;
		
		AST nestedCompositePattern_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST cp = null;
		
				BlockScope childScope = new BlockScope (pb.getScope (), null);
				childScope.setUseNewScopeForQueries (true);
			
		
		cp = _t==ASTNULL ? null : (AST)_t;
		child=compositePattern(_t,childScope, pb, pb.getModel (), true, false);
		_t = _retTree;
		_retTree = _t;
		return child;
	}
	
	public final void predicate(AST _t,
		PatternBuilder pb, AST label
	) throws RecognitionException {
		
		AST predicate_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST lb = null;
		AST block = null;
		AST cp = null;
		AST fid = null;
		AST id = null;
		AST mcn = null;
		AST cond = null;
		AST predid = null;
		AST tl = null;
		
				AST pos = _t;
				Type type;
				Expression e;
				Expression wrapped = null;
				BlockScope scope = pb.getScope ();
				Expression[] args;
				Object o;
				PatternBuilder child;
				TraversalData td;
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case LABEL:
		{
			AST __t98 = _t;
			AST tmp54_AST_in = (AST)_t;
			match(_t,LABEL);
			_t = _t.getFirstChild();
			lb = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			predicate(_t,pb, lb);
			_t = _retTree;
			_t = __t98;
			_t = _t.getNextSibling();
			break;
		}
		case PATTERN_WITH_BLOCK:
		{
			AST __t99 = _t;
			AST tmp55_AST_in = (AST)_t;
			match(_t,PATTERN_WITH_BLOCK);
			_t = _t.getFirstChild();
			predicate(_t,pb, label);
			_t = _retTree;
			block = _t==ASTNULL ? null : (AST)_t;
			e=slist(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				if (e != null) pb.addBlock (e, block);
			}
			_t = __t99;
			_t = _t.getNextSibling();
			break;
		}
		case TRAVERSAL:
		{
			traversal(_t,pb, label);
			_t = _retTree;
			break;
		}
		case MINIMAL:
		{
			AST __t100 = _t;
			AST tmp56_AST_in = (AST)_t;
			match(_t,MINIMAL);
			_t = _t.getFirstChild();
			td=traversal(_t,pb, null);
			_t = _retTree;
			cp = _t==ASTNULL ? null : (AST)_t;
			child=nestedCompositePattern(_t,pb);
			_t = _retTree;
			_t = __t100;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							CompositeData cd = pb.addComposite (label, child, EdgeDirection.FORWARD, false, cp);
							if (td != null)
							{
								cd.setBreak (td);
							}
						
			}
			break;
		}
		case LATE_MATCH:
		{
			AST __t101 = _t;
			AST tmp57_AST_in = (AST)_t;
			match(_t,LATE_MATCH);
			_t = _t.getFirstChild();
			child=nestedCompositePattern(_t,pb);
			_t = _retTree;
			_t = __t101;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							pb.addComposite (label, child, EdgeDirection.FORWARD, false, pos).setBreak ();
						
			}
			break;
		}
		case SINGLE_MATCH:
		{
			AST __t102 = _t;
			AST tmp58_AST_in = (AST)_t;
			match(_t,SINGLE_MATCH);
			_t = _t.getFirstChild();
			child=nestedCompositePattern(_t,pb);
			_t = _retTree;
			_t = __t102;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							pb.addComposite (label, child, EdgeDirection.FORWARD, false, pos).setBreak ();
						
			}
			break;
		}
		case OPTIONAL_MATCH:
		{
			AST __t103 = _t;
			AST tmp59_AST_in = (AST)_t;
			match(_t,OPTIONAL_MATCH);
			_t = _t.getFirstChild();
			child=nestedCompositePattern(_t,pb);
			_t = _retTree;
			_t = __t103;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							pb.addComposite (label, child, EdgeDirection.FORWARD, true, pos);
						
			}
			break;
		}
		case SINGLE_OPTIONAL_MATCH:
		{
			AST __t104 = _t;
			AST tmp60_AST_in = (AST)_t;
			match(_t,SINGLE_OPTIONAL_MATCH);
			_t = _t.getFirstChild();
			child=nestedCompositePattern(_t,pb);
			_t = _retTree;
			_t = __t104;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							pb.addComposite (label, child, EdgeDirection.FORWARD, true, pos);
						
			}
			break;
		}
		case ANY:
		{
			AST tmp61_AST_in = (AST)_t;
			match(_t,ANY);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addAny (label, pos);
			}
			break;
		}
		case FOLDING:
		{
			AST __t105 = _t;
			AST tmp62_AST_in = (AST)_t;
			match(_t,FOLDING);
			_t = _t.getFirstChild();
			predicate(_t,pb, label);
			_t = _retTree;
			{
			int _cnt107=0;
			_loop107:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==IDENT)) {
					fid = (AST)_t;
					match(_t,IDENT);
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						
											pb.addFolding (fid);
										
					}
				}
				else {
					if ( _cnt107>=1 ) { break _loop107; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt107++;
			} while (true);
			}
			_t = __t105;
			_t = _t.getNextSibling();
			break;
		}
		case SEPARATE:
		{
			AST tmp63_AST_in = (AST)_t;
			match(_t,SEPARATE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addSeparation (pos);
			}
			break;
		}
		case LT:
		{
			AST tmp64_AST_in = (AST)_t;
			match(_t,LT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.BACKWARD, EdgePattern.SUCCESSOR_EDGE, pos);
			}
			break;
		}
		case GT:
		{
			AST tmp65_AST_in = (AST)_t;
			match(_t,GT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.FORWARD, EdgePattern.SUCCESSOR_EDGE, pos);
			}
			break;
		}
		case LINE:
		{
			AST tmp66_AST_in = (AST)_t;
			match(_t,LINE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.UNDIRECTED, EdgePattern.SUCCESSOR_EDGE, pos);
			}
			break;
		}
		case LEFT_RIGHT_ARROW:
		{
			AST tmp67_AST_in = (AST)_t;
			match(_t,LEFT_RIGHT_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.BOTH, EdgePattern.SUCCESSOR_EDGE, pos);
			}
			break;
		}
		case PLUS_LEFT_ARROW:
		{
			AST tmp68_AST_in = (AST)_t;
			match(_t,PLUS_LEFT_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.BACKWARD, EdgePattern.BRANCH_EDGE, pos);
			}
			break;
		}
		case PLUS_ARROW:
		{
			AST tmp69_AST_in = (AST)_t;
			match(_t,PLUS_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.FORWARD, EdgePattern.BRANCH_EDGE, pos);
			}
			break;
		}
		case PLUS_LINE:
		{
			AST tmp70_AST_in = (AST)_t;
			match(_t,PLUS_LINE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.UNDIRECTED, EdgePattern.BRANCH_EDGE, pos);
			}
			break;
		}
		case PLUS_LEFT_RIGHT_ARROW:
		{
			AST tmp71_AST_in = (AST)_t;
			match(_t,PLUS_LEFT_RIGHT_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.BOTH, EdgePattern.BRANCH_EDGE, pos);
			}
			break;
		}
		case SLASH_LEFT_ARROW:
		{
			AST tmp72_AST_in = (AST)_t;
			match(_t,SLASH_LEFT_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.BACKWARD, EdgePattern.REFINEMENT_EDGE, pos);
			}
			break;
		}
		case SLASH_ARROW:
		{
			AST tmp73_AST_in = (AST)_t;
			match(_t,SLASH_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.FORWARD, EdgePattern.REFINEMENT_EDGE, pos);
			}
			break;
		}
		case SLASH_LINE:
		{
			AST tmp74_AST_in = (AST)_t;
			match(_t,SLASH_LINE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.UNDIRECTED, EdgePattern.REFINEMENT_EDGE, pos);
			}
			break;
		}
		case SLASH_LEFT_RIGHT_ARROW:
		{
			AST tmp75_AST_in = (AST)_t;
			match(_t,SLASH_LEFT_RIGHT_ARROW);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				pb.addStandardEdge (label, EdgeDirection.BOTH, EdgePattern.REFINEMENT_EDGE, pos);
			}
			break;
		}
		case TYPE_PATTERN:
		{
			AST __t108 = _t;
			AST tmp76_AST_in = (AST)_t;
			match(_t,TYPE_PATTERN);
			_t = _t.getFirstChild();
			type=typeSpec(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				pb.addType (label, type, pos);
			}
			_t = __t108;
			_t = _t.getNextSibling();
			break;
		}
		case WRAPPED_TYPE_PATTERN:
		{
			AST __t109 = _t;
			AST tmp77_AST_in = (AST)_t;
			match(_t,WRAPPED_TYPE_PATTERN);
			_t = _t.getFirstChild();
			type=typeSpec(_t,scope);
			_t = _retTree;
			wrapped=term(_t,pb);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								pb.addWrappedType (label, type, wrapped, pos);
							
			}
			_t = __t109;
			_t = _t.getNextSibling();
			break;
		}
		case NAME_PATTERN:
		{
			AST __t110 = _t;
			AST tmp78_AST_in = (AST)_t;
			match(_t,NAME_PATTERN);
			_t = _t.getFirstChild();
			id = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
			_t = __t110;
			_t = _t.getNextSibling();
			break;
		}
		case TREE:
		{
			AST __t111 = _t;
			AST tmp79_AST_in = (AST)_t;
			match(_t,TREE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				pb.beginTree (pos);
			}
			{
			_loop113:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_5.member(_t.getType()))) {
					predicate(_t,pb, null);
					_t = _retTree;
				}
				else {
					break _loop113;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				pb.endTree (pos);
			}
			_t = __t111;
			_t = _t.getNextSibling();
			break;
		}
		case CONTEXT:
		{
			AST __t114 = _t;
			AST tmp80_AST_in = (AST)_t;
			match(_t,CONTEXT);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				pb.beginContext (pos);
			}
			{
			_loop116:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_5.member(_t.getType()))) {
					predicate(_t,pb, null);
					_t = _retTree;
				}
				else {
					break _loop116;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				pb.endContext (pos);
			}
			_t = __t114;
			_t = _t.getNextSibling();
			break;
		}
		case EXPR:
		{
			AST __t117 = _t;
			AST tmp81_AST_in = (AST)_t;
			match(_t,EXPR);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								addExpression (pb, e, label, pos);
							
			}
			_t = __t117;
			_t = _t.getNextSibling();
			break;
		}
		case ROOT:
		{
			AST tmp82_AST_in = (AST)_t;
			match(_t,ROOT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							pb.addExpression
								(label, new Root (pb.getModel ().getNodeType (), pb.getQueryState ()),
								 pos);
						
			}
			break;
		}
		case METHOD_PATTERN:
		{
			AST __t118 = _t;
			AST tmp83_AST_in = (AST)_t;
			match(_t,METHOD_PATTERN);
			_t = _t.getFirstChild();
			AST __t119 = _t;
			AST tmp84_AST_in = (AST)_t;
			match(_t,METHOD_CALL);
			_t = _t.getFirstChild();
			AST __t120 = _t;
			AST tmp85_AST_in = (AST)_t;
			match(_t,DOT);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			mcn = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t120;
			_t = _t.getNextSibling();
			args=arglist(_t,scope);
			_t = _retTree;
			_t = __t119;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
								if (e != null)
								{
									addMethodPattern (pb, label, e, mcn.getText (), args, pos);
								}
							
			}
			_t = __t118;
			_t = _t.getNextSibling();
			break;
		}
		case APPLICATION_CONDITION:
		{
			AST __t121 = _t;
			AST tmp86_AST_in = (AST)_t;
			match(_t,APPLICATION_CONDITION);
			_t = _t.getFirstChild();
			cond = _t==ASTNULL ? null : (AST)_t;
			e=expr(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								pb.addCondition (label,
												 wideningConversion (e, Type.BOOLEAN, scope, cond),
												 pos);
							
			}
			_t = __t121;
			_t = _t.getNextSibling();
			break;
		}
		case PARAMETERIZED_PATTERN:
		{
			AST __t122 = _t;
			AST tmp87_AST_in = (AST)_t;
			match(_t,PARAMETERIZED_PATTERN);
			_t = _t.getFirstChild();
			predid = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			tl = _t==ASTNULL ? null : (AST)_t;
			args=termList(_t,pb);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
			_t = __t122;
			_t = _t.getNextSibling();
			break;
		}
		case SUB:
		{
			AST __t123 = _t;
			AST tmp88_AST_in = (AST)_t;
			match(_t,SUB);
			_t = _t.getFirstChild();
			edgeRest(_t,pb, label, pos);
			_t = _retTree;
			_t = __t123;
			_t = _t.getNextSibling();
			break;
		}
		case LEFT_ARROW:
		{
			AST __t124 = _t;
			AST tmp89_AST_in = (AST)_t;
			match(_t,LEFT_ARROW);
			_t = _t.getFirstChild();
			edgeRest(_t,pb, label, pos);
			_t = _retTree;
			_t = __t124;
			_t = _t.getNextSibling();
			break;
		}
		case ARROW:
		{
			AST __t125 = _t;
			AST tmp90_AST_in = (AST)_t;
			match(_t,ARROW);
			_t = _t.getFirstChild();
			edgeRest(_t,pb, label, pos);
			_t = _retTree;
			_t = __t125;
			_t = _t.getNextSibling();
			break;
		}
		case X_LEFT_RIGHT_ARROW:
		{
			AST __t126 = _t;
			AST tmp91_AST_in = (AST)_t;
			match(_t,X_LEFT_RIGHT_ARROW);
			_t = _t.getFirstChild();
			edgeRest(_t,pb, label, pos);
			_t = _retTree;
			_t = __t126;
			_t = _t.getNextSibling();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
	}
	
	public final TraversalData  traversal(AST _t,
		PatternBuilder pb, AST label
	) throws RecognitionException {
		TraversalData td;
		
		AST traversal_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				Expression min = null, max = null;
				BlockScope scope = pb.getScope ();
				PatternBuilder child;
				AST pos = _t;
				td = null;
				boolean addFolding = true;
			
		
		AST __t128 = _t;
		AST tmp92_AST_in = (AST)_t;
		match(_t,TRAVERSAL);
		_t = _t.getFirstChild();
		child=nestedCompositePattern(_t,pb);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case QUESTION:
		{
			AST tmp93_AST_in = (AST)_t;
			match(_t,QUESTION);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				min = new LongConst (0); max = new LongConst (1);
			}
			break;
		}
		case MUL:
		{
			AST tmp94_AST_in = (AST)_t;
			match(_t,MUL);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				min = new LongConst (0); max = new LongConst (-1);
			}
			break;
		}
		case ADD:
		{
			AST tmp95_AST_in = (AST)_t;
			match(_t,ADD);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				min = new LongConst (1); max = new LongConst (-1); addFolding = false;
			}
			break;
		}
		case RANGE_EXACTLY:
		{
			AST __t130 = _t;
			AST tmp96_AST_in = (AST)_t;
			match(_t,RANGE_EXACTLY);
			_t = _t.getFirstChild();
			min=longExpr(_t,scope);
			_t = _retTree;
			_t = __t130;
			_t = _t.getNextSibling();
			break;
		}
		case RANGE_MIN:
		{
			AST __t131 = _t;
			AST tmp97_AST_in = (AST)_t;
			match(_t,RANGE_MIN);
			_t = _t.getFirstChild();
			min=longExpr(_t,scope);
			_t = _retTree;
			_t = __t131;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				max = new LongConst (-1);
			}
			break;
		}
		case RANGE:
		{
			AST __t132 = _t;
			AST tmp98_AST_in = (AST)_t;
			match(_t,RANGE);
			_t = _t.getFirstChild();
			min=longExpr(_t,scope);
			_t = _retTree;
			max=longExpr(_t,scope);
			_t = _retTree;
			_t = __t132;
			_t = _t.getNextSibling();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t128;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
					td = pb.addTraversal (label, child, EdgeDirection.FORWARD, min, max, addFolding, pos);
				
		}
		_retTree = _t;
		return td;
	}
	
	public final Expression  term(AST _t,
		PatternBuilder pb
	) throws RecognitionException {
		Expression e;
		
		AST term_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
				e = null;
			
		
		boolean synPredMatched145 = false;
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==IDENT))) {
			AST __t145 = _t;
			synPredMatched145 = true;
			inputState.guessing++;
			try {
				{
				AST tmp99_AST_in = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched145 = false;
			}
			_t = __t145;
inputState.guessing--;
		}
		if ( synPredMatched145 ) {
			id = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
		}
		else if ((_t.getType()==EMPTY)) {
			AST tmp100_AST_in = (AST)_t;
			match(_t,EMPTY);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				e = new OpenArgument ();
			}
		}
		else if ((_tokenSet_6.member(_t.getType()))) {
			e=expr(_t,pb.getScope ());
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				if (e == null) e = new OpenArgument ();
			}
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		_retTree = _t;
		return e;
	}
	
	public final Expression  expr(AST _t,
		Scope scope
	) throws RecognitionException {
		Expression e;
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST tex = null;
		AST iex = null;
		AST tce = null;
		AST tch = null;
		AST id = null;
		AST i = null;
		
				e = null; Type t = null; Expression e2 = null, e3;
				AST root = _t;
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case SUPER:
		case ASSIGN:
		case METHOD_CALL:
		case WITH:
		case ELIST:
		case THIS:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case INC:
		case DEC:
		case POST_INC:
		case POST_DEC:
		case IN:
		case GUARD:
		case ARRAY_ITERATOR:
		case QUERY_EXPR:
		case INVOKE_OP:
		case QUALIFIED_NEW:
		case INDEX_OP:
		case NEW:
		{
			e=blockExpr(_t,scope);
			_t = _retTree;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		{
			e=literal(_t);
			_t = _retTree;
			break;
		}
		case NULL_LITERAL:
		{
			AST tmp101_AST_in = (AST)_t;
			match(_t,NULL_LITERAL);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				e = new ObjectConst (null, Type.NULL);
			}
			break;
		}
		case INVALID_EXPR:
		{
			AST tmp102_AST_in = (AST)_t;
			match(_t,INVALID_EXPR);
			_t = _t.getNextSibling();
			break;
		}
		case EXPR:
		{
			AST __t313 = _t;
			AST tmp103_AST_in = (AST)_t;
			match(_t,EXPR);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t313;
			_t = _t.getNextSibling();
			break;
		}
		case RANGE:
		{
			AST __t314 = _t;
			AST tmp104_AST_in = (AST)_t;
			match(_t,RANGE);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, e2 = new Range (), OPERATOR_NAME_RANGE, root);
			_t = _retTree;
			_t = __t314;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							if ((e == e2) && (e.etype >= 0))
							{
								((Range) e).setLocals
									(((BlockScope) scope).declareLocal ("counter.", 0, e.getType (), root),
									 ((BlockScope) scope).declareLocal ("max.", Member.FINAL, e.getType (), root));
							}
						
			}
			break;
		}
		case COR:
		{
			AST __t315 = _t;
			AST tmp105_AST_in = (AST)_t;
			match(_t,COR);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new ConditionalOr (), OPERATOR_NAME_COR, root);
			_t = _retTree;
			_t = __t315;
			_t = _t.getNextSibling();
			break;
		}
		case CAND:
		{
			AST __t316 = _t;
			AST tmp106_AST_in = (AST)_t;
			match(_t,CAND);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new ConditionalAnd (), OPERATOR_NAME_CAND, root);
			_t = _retTree;
			_t = __t316;
			_t = _t.getNextSibling();
			break;
		}
		case OR:
		{
			AST __t317 = _t;
			AST tmp107_AST_in = (AST)_t;
			match(_t,OR);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Or (), OPERATOR_NAME_OR, root);
			_t = _retTree;
			_t = __t317;
			_t = _t.getNextSibling();
			break;
		}
		case XOR:
		{
			AST __t318 = _t;
			AST tmp108_AST_in = (AST)_t;
			match(_t,XOR);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Xor (), OPERATOR_NAME_XOR, root);
			_t = _retTree;
			_t = __t318;
			_t = _t.getNextSibling();
			break;
		}
		case AND:
		{
			AST __t319 = _t;
			AST tmp109_AST_in = (AST)_t;
			match(_t,AND);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new And (), OPERATOR_NAME_AND, root);
			_t = _retTree;
			_t = __t319;
			_t = _t.getNextSibling();
			break;
		}
		case NOT_EQUALS:
		{
			AST __t320 = _t;
			AST tmp110_AST_in = (AST)_t;
			match(_t,NOT_EQUALS);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new NotEquals (), OPERATOR_NAME_NOT_EQUALS, root);
			_t = _retTree;
			_t = __t320;
			_t = _t.getNextSibling();
			break;
		}
		case EQUALS:
		{
			AST __t321 = _t;
			AST tmp111_AST_in = (AST)_t;
			match(_t,EQUALS);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Equals (), OPERATOR_NAME_EQUALS, root);
			_t = _retTree;
			_t = __t321;
			_t = _t.getNextSibling();
			break;
		}
		case LT:
		{
			AST __t322 = _t;
			AST tmp112_AST_in = (AST)_t;
			match(_t,LT);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new LT (), OPERATOR_NAME_LT, root);
			_t = _retTree;
			_t = __t322;
			_t = _t.getNextSibling();
			break;
		}
		case GT:
		{
			AST __t323 = _t;
			AST tmp113_AST_in = (AST)_t;
			match(_t,GT);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new GT (), OPERATOR_NAME_GT, root);
			_t = _retTree;
			_t = __t323;
			_t = _t.getNextSibling();
			break;
		}
		case LE:
		{
			AST __t324 = _t;
			AST tmp114_AST_in = (AST)_t;
			match(_t,LE);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new LE (), OPERATOR_NAME_LE, root);
			_t = _retTree;
			_t = __t324;
			_t = _t.getNextSibling();
			break;
		}
		case GE:
		{
			AST __t325 = _t;
			AST tmp115_AST_in = (AST)_t;
			match(_t,GE);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new GE (), OPERATOR_NAME_GE, root);
			_t = _retTree;
			_t = __t325;
			_t = _t.getNextSibling();
			break;
		}
		case CMP:
		{
			AST __t326 = _t;
			AST tmp116_AST_in = (AST)_t;
			match(_t,CMP);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Compare(), OPERATOR_NAME_CMP, root);
			_t = _retTree;
			_t = __t326;
			_t = _t.getNextSibling();
			break;
		}
		case SHL:
		{
			AST __t327 = _t;
			AST tmp117_AST_in = (AST)_t;
			match(_t,SHL);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Shl (), OPERATOR_NAME_SHL, root);
			_t = _retTree;
			_t = __t327;
			_t = _t.getNextSibling();
			break;
		}
		case SHR:
		{
			AST __t328 = _t;
			AST tmp118_AST_in = (AST)_t;
			match(_t,SHR);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Shr (), OPERATOR_NAME_SHR, root);
			_t = _retTree;
			_t = __t328;
			_t = _t.getNextSibling();
			break;
		}
		case USHR:
		{
			AST __t329 = _t;
			AST tmp119_AST_in = (AST)_t;
			match(_t,USHR);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Ushr (), OPERATOR_NAME_USHR, root);
			_t = _retTree;
			_t = __t329;
			_t = _t.getNextSibling();
			break;
		}
		case ADD:
		{
			AST __t330 = _t;
			AST tmp120_AST_in = (AST)_t;
			match(_t,ADD);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Add (), OPERATOR_NAME_ADD, root);
			_t = _retTree;
			_t = __t330;
			_t = _t.getNextSibling();
			break;
		}
		case SUB:
		{
			AST __t331 = _t;
			AST tmp121_AST_in = (AST)_t;
			match(_t,SUB);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Sub (), OPERATOR_NAME_SUB, root);
			_t = _retTree;
			_t = __t331;
			_t = _t.getNextSibling();
			break;
		}
		case DIV:
		{
			AST __t332 = _t;
			AST tmp122_AST_in = (AST)_t;
			match(_t,DIV);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Div (), OPERATOR_NAME_DIV, root);
			_t = _retTree;
			_t = __t332;
			_t = _t.getNextSibling();
			break;
		}
		case REM:
		{
			AST __t333 = _t;
			AST tmp123_AST_in = (AST)_t;
			match(_t,REM);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Rem (), OPERATOR_NAME_REM, root);
			_t = _retTree;
			_t = __t333;
			_t = _t.getNextSibling();
			break;
		}
		case MUL:
		{
			AST __t334 = _t;
			AST tmp124_AST_in = (AST)_t;
			match(_t,MUL);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Mul (), OPERATOR_NAME_MUL, root);
			_t = _retTree;
			_t = __t334;
			_t = _t.getNextSibling();
			break;
		}
		case POW:
		{
			AST __t335 = _t;
			AST tmp125_AST_in = (AST)_t;
			match(_t,POW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Power (), OPERATOR_NAME_POW, root);
			_t = _retTree;
			_t = __t335;
			_t = _t.getNextSibling();
			break;
		}
		case LONG_LEFT_ARROW:
		{
			AST __t336 = _t;
			AST tmp126_AST_in = (AST)_t;
			match(_t,LONG_LEFT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_LONG_LEFT_ARROW, root);
			_t = _retTree;
			_t = __t336;
			_t = _t.getNextSibling();
			break;
		}
		case LONG_ARROW:
		{
			AST __t337 = _t;
			AST tmp127_AST_in = (AST)_t;
			match(_t,LONG_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_LONG_ARROW, root);
			_t = _retTree;
			_t = __t337;
			_t = _t.getNextSibling();
			break;
		}
		case LONG_LEFT_RIGHT_ARROW:
		{
			AST __t338 = _t;
			AST tmp128_AST_in = (AST)_t;
			match(_t,LONG_LEFT_RIGHT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_LONG_LEFT_RIGHT_ARROW, root);
			_t = _retTree;
			_t = __t338;
			_t = _t.getNextSibling();
			break;
		}
		case LEFT_ARROW:
		{
			AST __t339 = _t;
			AST tmp129_AST_in = (AST)_t;
			match(_t,LEFT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_LEFT_ARROW, root);
			_t = _retTree;
			_t = __t339;
			_t = _t.getNextSibling();
			break;
		}
		case ARROW:
		{
			AST __t340 = _t;
			AST tmp130_AST_in = (AST)_t;
			match(_t,ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_ARROW, root);
			_t = _retTree;
			_t = __t340;
			_t = _t.getNextSibling();
			break;
		}
		case LINE:
		{
			AST __t341 = _t;
			AST tmp131_AST_in = (AST)_t;
			match(_t,LINE);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_LINE, root);
			_t = _retTree;
			_t = __t341;
			_t = _t.getNextSibling();
			break;
		}
		case LEFT_RIGHT_ARROW:
		{
			AST __t342 = _t;
			AST tmp132_AST_in = (AST)_t;
			match(_t,LEFT_RIGHT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_LEFT_RIGHT_ARROW, root);
			_t = _retTree;
			_t = __t342;
			_t = _t.getNextSibling();
			break;
		}
		case PLUS_LEFT_ARROW:
		{
			AST __t343 = _t;
			AST tmp133_AST_in = (AST)_t;
			match(_t,PLUS_LEFT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_PLUS_LEFT_ARROW, root);
			_t = _retTree;
			_t = __t343;
			_t = _t.getNextSibling();
			break;
		}
		case PLUS_ARROW:
		{
			AST __t344 = _t;
			AST tmp134_AST_in = (AST)_t;
			match(_t,PLUS_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_PLUS_ARROW, root);
			_t = _retTree;
			_t = __t344;
			_t = _t.getNextSibling();
			break;
		}
		case PLUS_LEFT_RIGHT_ARROW:
		{
			AST __t345 = _t;
			AST tmp135_AST_in = (AST)_t;
			match(_t,PLUS_LEFT_RIGHT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_PLUS_LEFT_RIGHT_ARROW, root);
			_t = _retTree;
			_t = __t345;
			_t = _t.getNextSibling();
			break;
		}
		case PLUS_LINE:
		{
			AST __t346 = _t;
			AST tmp136_AST_in = (AST)_t;
			match(_t,PLUS_LINE);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_PLUS_LINE, root);
			_t = _retTree;
			_t = __t346;
			_t = _t.getNextSibling();
			break;
		}
		case SLASH_LEFT_ARROW:
		{
			AST __t347 = _t;
			AST tmp137_AST_in = (AST)_t;
			match(_t,SLASH_LEFT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_SLASH_LEFT_ARROW, root);
			_t = _retTree;
			_t = __t347;
			_t = _t.getNextSibling();
			break;
		}
		case SLASH_ARROW:
		{
			AST __t348 = _t;
			AST tmp138_AST_in = (AST)_t;
			match(_t,SLASH_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_SLASH_ARROW, root);
			_t = _retTree;
			_t = __t348;
			_t = _t.getNextSibling();
			break;
		}
		case SLASH_LEFT_RIGHT_ARROW:
		{
			AST __t349 = _t;
			AST tmp139_AST_in = (AST)_t;
			match(_t,SLASH_LEFT_RIGHT_ARROW);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_SLASH_LEFT_RIGHT_ARROW, root);
			_t = _retTree;
			_t = __t349;
			_t = _t.getNextSibling();
			break;
		}
		case SLASH_LINE:
		{
			AST __t350 = _t;
			AST tmp140_AST_in = (AST)_t;
			match(_t,SLASH_LINE);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, null, OPERATOR_NAME_SLASH_LINE, root);
			_t = _retTree;
			_t = __t350;
			_t = _t.getNextSibling();
			break;
		}
		case QUESTION:
		{
			AST __t351 = _t;
			AST tmp141_AST_in = (AST)_t;
			match(_t,QUESTION);
			_t = _t.getFirstChild();
			e=booleanExpr(_t,scope);
			_t = _retTree;
			tex = _t==ASTNULL ? null : (AST)_t;
			e2=expr(_t,scope);
			_t = _retTree;
			e3=expr(_t,scope);
			_t = _retTree;
			_t = __t351;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case INSTANCEOF:
		{
			AST __t352 = _t;
			AST tmp142_AST_in = (AST)_t;
			match(_t,INSTANCEOF);
			_t = _t.getFirstChild();
			iex = _t==ASTNULL ? null : (AST)_t;
			e2=referenceExpr(_t,scope, null);
			_t = _retTree;
			t=typeSpec(_t,scope);
			_t = _retTree;
			_t = __t352;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case TYPECAST:
		{
			AST __t353 = _t;
			AST tmp143_AST_in = (AST)_t;
			match(_t,TYPECAST);
			_t = _t.getFirstChild();
			t=typeSpec(_t,scope);
			_t = _retTree;
			tce = _t==ASTNULL ? null : (AST)_t;
			e2=expr(_t,scope);
			_t = _retTree;
			_t = __t353;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case TYPECHECK:
		{
			AST __t354 = _t;
			AST tmp144_AST_in = (AST)_t;
			match(_t,TYPECHECK);
			_t = _t.getFirstChild();
			t=typeSpec(_t,scope);
			_t = _retTree;
			tch = _t==ASTNULL ? null : (AST)_t;
			e2=expr(_t,scope);
			_t = _retTree;
			_t = __t354;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							checkClassReady (t);
							e = returnConversion (e2, t, scope, root);
						
			}
			break;
		}
		case CLASS_LITERAL:
		{
			AST __t355 = _t;
			AST tmp145_AST_in = (AST)_t;
			match(_t,CLASS_LITERAL);
			_t = _t.getFirstChild();
			t=typeSpec(_t,scope);
			_t = _retTree;
			_t = __t355;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							checkClassReady (t);
							e = new ClassConst (t);
						
			}
			break;
		}
		case COM:
		case NOT:
		case NEG:
		case POS:
		case QUOTE:
		{
			e=unaryExpr(_t,scope, false);
			_t = _retTree;
			break;
		}
		default:
			boolean synPredMatched357 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==IDENT||_t.getType()==DOT))) {
				AST __t357 = _t;
				synPredMatched357 = true;
				inputState.guessing++;
				try {
					{
					name(_t);
					_t = _retTree;
					}
				}
				catch (RecognitionException pe) {
					synPredMatched357 = false;
				}
				_t = __t357;
inputState.guessing--;
			}
			if ( synPredMatched357 ) {
				id = _t==ASTNULL ? null : (AST)_t;
				name(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
								e = resolver.resolveExpressionName (id, scope);
							
				}
			}
			else if ((_t.getType()==DOT)) {
				AST __t358 = _t;
				AST tmp146_AST_in = (AST)_t;
				match(_t,DOT);
				_t = _t.getFirstChild();
				e2=expr(_t,scope);
				_t = _retTree;
				i = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				_t = __t358;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					
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
			}
		else {
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
					setAST (e, root);
				
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression[]  termList(AST _t,
		PatternBuilder pb
	) throws RecognitionException {
		Expression[] list;
		
		AST termList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				Expression e = null;
				list = new Expression[termList_AST_in.getNumberOfChildren ()];
				int i = 0;
				termListContainsEmpty = false;
			
		
		AST __t147 = _t;
		AST tmp147_AST_in = (AST)_t;
		match(_t,ARGLIST);
		_t = _t.getFirstChild();
		{
		_loop149:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_7.member(_t.getType()))) {
				e=term(_t,pb);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				break _loop149;
			}
			
		} while (true);
		}
		_t = __t147;
		_t = _t.getNextSibling();
		_retTree = _t;
		return list;
	}
	
	public final void edgeRest(AST _t,
		final PatternBuilder pb, AST label, AST edge
	) throws RecognitionException {
		
		AST edgeRest_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		AST predid = null;
		AST tl = null;
		AST mcn = null;
		
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
			
		
		if (_t==null) _t=ASTNULL;
		if ((_t.getType()==ANY)) {
			AST tmp148_AST_in = (AST)_t;
			match(_t,ANY);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							pb.addStandardEdge (label, dir, EdgePattern.ANY_EDGE, edge);
						
			}
		}
		else {
			boolean synPredMatched135 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==IDENT||_t.getType()==DOT))) {
				AST __t135 = _t;
				synPredMatched135 = true;
				inputState.guessing++;
				try {
					{
					name(_t);
					_t = _retTree;
					}
				}
				catch (RecognitionException pe) {
					synPredMatched135 = false;
				}
				_t = __t135;
inputState.guessing--;
			}
			if ( synPredMatched135 ) {
				id = _t==ASTNULL ? null : (AST)_t;
				name(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				boolean synPredMatched137 = false;
				if (_t==null) _t=ASTNULL;
				if (((_t.getType()==METHOD_CALL))) {
					AST __t137 = _t;
					synPredMatched137 = true;
					inputState.guessing++;
					try {
						{
						AST tmp149_AST_in = (AST)_t;
						match(_t,METHOD_CALL);
						_t = _t.getNextSibling();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched137 = false;
					}
					_t = __t137;
inputState.guessing--;
				}
				if ( synPredMatched137 ) {
					AST __t138 = _t;
					AST tmp150_AST_in = (AST)_t;
					match(_t,METHOD_CALL);
					_t = _t.getFirstChild();
					{
					boolean synPredMatched141 = false;
					if (_t==null) _t=ASTNULL;
					if (((_t.getType()==IDENT||_t.getType()==DOT))) {
						AST __t141 = _t;
						synPredMatched141 = true;
						inputState.guessing++;
						try {
							{
							name(_t);
							_t = _retTree;
							}
						}
						catch (RecognitionException pe) {
							synPredMatched141 = false;
						}
						_t = __t141;
inputState.guessing--;
					}
					if ( synPredMatched141 ) {
						predid = _t==ASTNULL ? null : (AST)_t;
						name(_t);
						_t = _retTree;
						tl = _t==ASTNULL ? null : (AST)_t;
						args=termList(_t,pb);
						_t = _retTree;
						if ( inputState.guessing==0 ) {
							
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
					}
					else if ((_t.getType()==DOT)) {
						AST __t142 = _t;
						AST tmp151_AST_in = (AST)_t;
						match(_t,DOT);
						_t = _t.getFirstChild();
						e=expr(_t,scope);
						_t = _retTree;
						mcn = (AST)_t;
						match(_t,IDENT);
						_t = _t.getNextSibling();
						_t = __t142;
						_t = _t.getNextSibling();
						args=arglist(_t,scope);
						_t = _retTree;
						if ( inputState.guessing==0 ) {
							
												compileMethodEdgePattern (e, mcn.getText (), args, edge, pb, label, dir);
											
						}
					}
					else {
						throw new NoViableAltException(_t);
					}
					
					}
					_t = __t138;
					_t = _t.getNextSibling();
				}
				else if ((_tokenSet_6.member(_t.getType()))) {
					e=expr(_t,scope);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						if (e != null) pb.addEdge (label, dir, e, rest);
					}
				}
				else {
					throw new NoViableAltException(_t);
				}
				}}
				_retTree = _t;
			}
			
	public final Expression  longExpr(AST _t,
		Scope scope
	) throws RecognitionException {
		Expression e;
		
		AST longExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST ex = null;
		e = null;
		
		ex = _t==ASTNULL ? null : (AST)_t;
		e=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						e = assignmentConversion (e, Type.LONG, scope, ex);
					
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  rule(AST _t,
		BlockScope scope, ExpressionFactory graph, CompiletimeModel model
	) throws RecognitionException {
		Expression e;
		
		AST rule_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST r = null;
		AST str = null;
		AST stmt = null;
		AST q = null;
		AST st = null;
		
				BlockScope s = new BlockScope (scope);
				ProduceScope w = null;
				e = null;
				Block loop = null;
			
		
		AST __t151 = _t;
		r = _t==ASTNULL ? null :(AST)_t;
		match(_t,RULE);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case DOUBLE_ARROW_RULE:
		{
			str = (AST)_t;
			match(_t,DOUBLE_ARROW_RULE);
			_t = _t.getNextSibling();
			break;
		}
		case EXEC_RULE:
		{
			stmt = (AST)_t;
			match(_t,EXEC_RULE);
			_t = _t.getNextSibling();
			break;
		}
		case EMPTY:
		case QUERY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		q = _t==ASTNULL ? null : (AST)_t;
		e=query(_t,s, graph, model, true, stmt != null);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
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
		st = _t==ASTNULL ? null : (AST)_t;
		e=stat(_t,w, null);
		_t = _retTree;
		_t = __t151;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  stat(AST _t,
		BlockScope scope, String label
	) throws RecognitionException {
		Expression e;
		
		AST stat_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST sh = null;
		AST c = null;
		AST alt = null;
		AST ex1 = null;
		AST y = null;
		AST lid = null;
		AST br = null;
		AST cont = null;
		AST ec = null;
		AST ex = null;
		AST node = null;
		
				e = null;
				Expression e1 = null, e2 = null;
				Expression[] list = null;
				long m;
				AST root = _t;
				int labelId = 0;
				Type t;
				BlockScope scope2 = null;
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case ASSIGN:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case EXPR:
		case METHOD_CALL:
		case DOT:
		case SUB:
		case LEFT_ARROW:
		case ARROW:
		case QUESTION:
		case MUL:
		case ADD:
		case RANGE:
		case WITH:
		case TYPECAST:
		case TYPECHECK:
		case COM:
		case NOT:
		case NEG:
		case POS:
		case DIV:
		case REM:
		case POW:
		case SHL:
		case SHR:
		case USHR:
		case LE:
		case GE:
		case CMP:
		case NOT_EQUALS:
		case EQUALS:
		case OR:
		case XOR:
		case AND:
		case COR:
		case CAND:
		case ELIST:
		case THIS:
		case NULL_LITERAL:
		case INVALID_EXPR:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case INSTANCEOF:
		case CLASS_LITERAL:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case INC:
		case DEC:
		case POST_INC:
		case POST_DEC:
		case IN:
		case GUARD:
		case ARRAY_ITERATOR:
		case QUERY_EXPR:
		case INVOKE_OP:
		case QUALIFIED_NEW:
		case INDEX_OP:
		case NEW:
		{
			e=expr(_t,new BlockScope (scope, null));
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
							e = Block.createSequentialBlock ().add (e);
						
			}
			break;
		}
		case SLIST:
		{
			e=slist(_t,scope);
			_t = _retTree;
			break;
		}
		case RULE_BLOCK:
		{
			e=ruleBlock(_t,scope);
			_t = _retTree;
			break;
		}
		case SHELL_BLOCK:
		{
			AST __t254 = _t;
			AST tmp152_AST_in = (AST)_t;
			match(_t,SHELL_BLOCK);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
								shellBlock = new Block ();
								e = shellBlock;
								shellBlockScope = scope;
							
			}
			{
			_loop256:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_8.member(_t.getType()))) {
					sh = _t==ASTNULL ? null : (AST)_t;
					e1=stat(_t,scope, null);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						
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
				}
				else {
					break _loop256;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				
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
			_t = __t254;
			_t = _t.getNextSibling();
			break;
		}
		case PRODUCE:
		{
			e=produce(_t,scope);
			_t = _retTree;
			break;
		}
		case VARIABLE_DEF:
		{
			e=variableDecl(_t,scope);
			_t = _retTree;
			break;
		}
		case CLASS:
		{
			AST __t257 = _t;
			c = _t==ASTNULL ? null :(AST)_t;
			match(_t,CLASS);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
								setLocalRun (TYPE_AND_FIELD_DECLARATION);
							
			}
			classDecl(_t,scope, Type.LOCAL_CLASS_MODIFIERS, scope.isStatic () ? Member.STATIC : 0, c);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								AST cd = c.getFirstChild ();
								setLocalRun (METHOD_DECLARATION);
								classDecl (cd, scope, Type.LOCAL_CLASS_MODIFIERS, 0, c);
								setLocalRun (COMPILATION);
								classDecl (cd, scope, Type.LOCAL_CLASS_MODIFIERS, 0, c);
							
			}
			_t = __t257;
			_t = _t.getNextSibling();
			break;
		}
		case CONSTRUCTOR:
		{
			AST __t258 = _t;
			AST tmp153_AST_in = (AST)_t;
			match(_t,CONSTRUCTOR);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case THIS:
			{
				alt = (AST)_t;
				match(_t,THIS);
				_t = _t.getNextSibling();
				break;
			}
			case SUPER:
			{
				AST tmp154_AST_in = (AST)_t;
				match(_t,SUPER);
				_t = _t.getNextSibling();
				break;
			}
			case QUALIFIED_SUPER:
			{
				AST __t260 = _t;
				AST tmp155_AST_in = (AST)_t;
				match(_t,QUALIFIED_SUPER);
				_t = _t.getFirstChild();
				e2=referenceExpr(_t,scope, null);
				_t = _retTree;
				_t = __t260;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			list=arglist(_t,scope);
			_t = _retTree;
			_t = __t258;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case IF:
		{
			AST __t261 = _t;
			AST tmp156_AST_in = (AST)_t;
			match(_t,IF);
			_t = _t.getFirstChild();
			e=booleanExpr(_t,scope);
			_t = _retTree;
			e1=statBlock(_t,scope);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_8.member(_t.getType()))) {
				e2=statBlock(_t,scope);
				_t = _retTree;
			}
			else if ((_t.getType()==3)) {
			}
			else {
				throw new NoViableAltException(_t);
			}
			
			}
			_t = __t261;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new If ().add (e).add (e1).add (e2);
						
			}
			break;
		}
		case RETURN:
		{
			AST __t263 = _t;
			AST tmp157_AST_in = (AST)_t;
			match(_t,RETURN);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case ASSIGN:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case EXPR:
			case METHOD_CALL:
			case DOT:
			case SUB:
			case LEFT_ARROW:
			case ARROW:
			case QUESTION:
			case MUL:
			case ADD:
			case RANGE:
			case WITH:
			case TYPECAST:
			case TYPECHECK:
			case COM:
			case NOT:
			case NEG:
			case POS:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case NOT_EQUALS:
			case EQUALS:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case ELIST:
			case THIS:
			case NULL_LITERAL:
			case INVALID_EXPR:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case INSTANCEOF:
			case CLASS_LITERAL:
			case QUOTE:
			case ADD_ASSIGN:
			case SUB_ASSIGN:
			case MUL_ASSIGN:
			case DIV_ASSIGN:
			case REM_ASSIGN:
			case POW_ASSIGN:
			case SHR_ASSIGN:
			case USHR_ASSIGN:
			case SHL_ASSIGN:
			case AND_ASSIGN:
			case XOR_ASSIGN:
			case OR_ASSIGN:
			case DEFERRED_ASSIGN:
			case DEFERRED_RATE_ASSIGN:
			case DEFERRED_ADD:
			case DEFERRED_SUB:
			case DEFERRED_MUL:
			case DEFERRED_DIV:
			case DEFERRED_REM:
			case DEFERRED_POW:
			case DEFERRED_OR:
			case DEFERRED_AND:
			case DEFERRED_XOR:
			case DEFERRED_SHL:
			case DEFERRED_SHR:
			case DEFERRED_USHR:
			case INC:
			case DEC:
			case POST_INC:
			case POST_DEC:
			case IN:
			case GUARD:
			case ARRAY_ITERATOR:
			case QUERY_EXPR:
			case INVOKE_OP:
			case QUALIFIED_NEW:
			case INDEX_OP:
			case NEW:
			{
				ex1 = _t==ASTNULL ? null : (AST)_t;
				e1=expr(_t,scope);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t263;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case YIELD:
		{
			AST __t265 = _t;
			AST tmp158_AST_in = (AST)_t;
			match(_t,YIELD);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case ASSIGN:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case EXPR:
			case METHOD_CALL:
			case DOT:
			case SUB:
			case LEFT_ARROW:
			case ARROW:
			case QUESTION:
			case MUL:
			case ADD:
			case RANGE:
			case WITH:
			case TYPECAST:
			case TYPECHECK:
			case COM:
			case NOT:
			case NEG:
			case POS:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case NOT_EQUALS:
			case EQUALS:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case ELIST:
			case THIS:
			case NULL_LITERAL:
			case INVALID_EXPR:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case INSTANCEOF:
			case CLASS_LITERAL:
			case QUOTE:
			case ADD_ASSIGN:
			case SUB_ASSIGN:
			case MUL_ASSIGN:
			case DIV_ASSIGN:
			case REM_ASSIGN:
			case POW_ASSIGN:
			case SHR_ASSIGN:
			case USHR_ASSIGN:
			case SHL_ASSIGN:
			case AND_ASSIGN:
			case XOR_ASSIGN:
			case OR_ASSIGN:
			case DEFERRED_ASSIGN:
			case DEFERRED_RATE_ASSIGN:
			case DEFERRED_ADD:
			case DEFERRED_SUB:
			case DEFERRED_MUL:
			case DEFERRED_DIV:
			case DEFERRED_REM:
			case DEFERRED_POW:
			case DEFERRED_OR:
			case DEFERRED_AND:
			case DEFERRED_XOR:
			case DEFERRED_SHL:
			case DEFERRED_SHR:
			case DEFERRED_USHR:
			case INC:
			case DEC:
			case POST_INC:
			case POST_DEC:
			case IN:
			case GUARD:
			case ARRAY_ITERATOR:
			case QUERY_EXPR:
			case INVOKE_OP:
			case QUALIFIED_NEW:
			case INDEX_OP:
			case NEW:
			{
				y = _t==ASTNULL ? null : (AST)_t;
				e1=expr(_t,scope2 = new BlockScope (scope, null));
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t265;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case THROW:
		{
			AST __t267 = _t;
			AST tmp159_AST_in = (AST)_t;
			match(_t,THROW);
			_t = _t.getFirstChild();
			e=referenceExpr(_t,scope, Throwable.class);
			_t = _retTree;
			_t = __t267;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new Throw ().add (e);
						
			}
			break;
		}
		case SYNCHRONIZED_:
		{
			AST __t268 = _t;
			AST tmp160_AST_in = (AST)_t;
			match(_t,SYNCHRONIZED_);
			_t = _t.getFirstChild();
			e=referenceExpr(_t,scope, null);
			_t = _retTree;
			e2=statBlock(_t,scope);
			_t = _retTree;
			_t = __t268;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							Local lock = scope.declareLocal ("lock.", Member.FINAL, Type.OBJECT, root);
							e = new Synchronized (lock).add (e).add (e2);
						
			}
			break;
		}
		case ASSERT:
		{
			AST __t269 = _t;
			AST tmp161_AST_in = (AST)_t;
			match(_t,ASSERT);
			_t = _t.getFirstChild();
			e=booleanExpr(_t,scope);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case ASSIGN:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case EXPR:
			case METHOD_CALL:
			case DOT:
			case SUB:
			case LEFT_ARROW:
			case ARROW:
			case QUESTION:
			case MUL:
			case ADD:
			case RANGE:
			case WITH:
			case TYPECAST:
			case TYPECHECK:
			case COM:
			case NOT:
			case NEG:
			case POS:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case NOT_EQUALS:
			case EQUALS:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case ELIST:
			case THIS:
			case NULL_LITERAL:
			case INVALID_EXPR:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case INSTANCEOF:
			case CLASS_LITERAL:
			case QUOTE:
			case ADD_ASSIGN:
			case SUB_ASSIGN:
			case MUL_ASSIGN:
			case DIV_ASSIGN:
			case REM_ASSIGN:
			case POW_ASSIGN:
			case SHR_ASSIGN:
			case USHR_ASSIGN:
			case SHL_ASSIGN:
			case AND_ASSIGN:
			case XOR_ASSIGN:
			case OR_ASSIGN:
			case DEFERRED_ASSIGN:
			case DEFERRED_RATE_ASSIGN:
			case DEFERRED_ADD:
			case DEFERRED_SUB:
			case DEFERRED_MUL:
			case DEFERRED_DIV:
			case DEFERRED_REM:
			case DEFERRED_POW:
			case DEFERRED_OR:
			case DEFERRED_AND:
			case DEFERRED_XOR:
			case DEFERRED_SHL:
			case DEFERRED_SHR:
			case DEFERRED_USHR:
			case INC:
			case DEC:
			case POST_INC:
			case POST_DEC:
			case IN:
			case GUARD:
			case ARRAY_ITERATOR:
			case QUERY_EXPR:
			case INVOKE_OP:
			case QUALIFIED_NEW:
			case INDEX_OP:
			case NEW:
			{
				e2=expr(_t,scope);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t269;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new Assert (TypeScope.getNonlocal (scope).getAssertionsDisabledField ())
								.add (e).add (e2);
						
			}
			break;
		}
		case LABELED_STATEMENT:
		{
			AST __t271 = _t;
			AST tmp162_AST_in = (AST)_t;
			match(_t,LABELED_STATEMENT);
			_t = _t.getFirstChild();
			lid = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
								if (scope.getMethodScope ().enterLabel (lid.getText ()))
								{
									problems.addSemanticError
										(I18N.msg (ProblemReporter.DUPLICATE_LABEL, lid.getText ()), lid);
								}
								pushProducer (scope, root);
							
			}
			e=stat(_t,scope, lid.getText ());
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
			_t = __t271;
			_t = _t.getNextSibling();
			break;
		}
		case BREAK:
		{
			AST __t272 = _t;
			br = _t==ASTNULL ? null :(AST)_t;
			match(_t,BREAK);
			_t = _t.getFirstChild();
			labelId=labelRef(_t,scope, false, br);
			_t = _retTree;
			_t = __t272;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new Break (labelId);
						
			}
			break;
		}
		case CONTINUE:
		{
			AST __t273 = _t;
			cont = _t==ASTNULL ? null :(AST)_t;
			match(_t,CONTINUE);
			_t = _t.getFirstChild();
			labelId=labelRef(_t,scope, true, cont);
			_t = _retTree;
			_t = __t273;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new Break (labelId);
						
			}
			break;
		}
		case TRY:
		{
			AST __t274 = _t;
			AST tmp163_AST_in = (AST)_t;
			match(_t,TRY);
			_t = _t.getFirstChild();
			e=statBlock(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								e = new TryCatch ().add (e);
							
			}
			{
			int _cnt277=0;
			_loop277:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CATCH)) {
					AST __t276 = _t;
					AST tmp164_AST_in = (AST)_t;
					match(_t,CATCH);
					_t = _t.getFirstChild();
					m=modifiers(_t,Member.FINAL, 0, 0, null, scope);
					_t = _retTree;
					ec = _t==ASTNULL ? null : (AST)_t;
					t=classType(_t,scope);
					_t = _retTree;
					ex = (AST)_t;
					match(_t,IDENT);
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						
											if (!Reflection.isInvalid (t)
												&& !Reflection.isSuperclassOrSame (Throwable.class, t))
											{
												problems.addSemanticError
													(I18N.msg (ProblemReporter.UNEXPECTED_TYPE, t, "java.lang.Throwable"), ec);
											}
											scope2 = new BlockScope (scope);
											e2 = new Catch (declareLocal (scope2, ex, (int) m, t));
										
					}
					slistInScope(_t,scope2);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						
											e.add (e2.add (scope2.getBlock ()));
										
					}
					_t = __t276;
					_t = _t.getNextSibling();
				}
				else {
					if ( _cnt277>=1 ) { break _loop277; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt277++;
			} while (true);
			}
			_t = __t274;
			_t = _t.getNextSibling();
			break;
		}
		case FINALLY:
		{
			AST __t278 = _t;
			AST tmp165_AST_in = (AST)_t;
			match(_t,FINALLY);
			_t = _t.getFirstChild();
			e1=statBlock(_t,scope);
			_t = _retTree;
			e2=statBlock(_t,scope);
			_t = _retTree;
			_t = __t278;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e2 = new Finally
								(scope.declareLocal ("ex.", Member.FINAL, Type.OBJECT, root),
								 scope.declareLocal ("addr.", Member.FINAL, Type.OBJECT, root))
								.add (e2);
							e = new TryFinally ().add (e1).add (e2);
						
			}
			break;
		}
		case FOR:
		case ENHANCED_FOR:
		case WHILE:
		case DO:
		{
			e=loop(_t,scope, label);
			_t = _retTree;
			break;
		}
		case SWITCH:
		{
			e=switchStatement(_t,scope, label);
			_t = _retTree;
			break;
		}
		case NODES:
		{
			AST __t279 = _t;
			AST tmp166_AST_in = (AST)_t;
			match(_t,NODES);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
								e = scope.getProduceScope ().createExpression (scope, root);
							
			}
			{
			_loop282:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==NODE)) {
					AST __t281 = _t;
					node = _t==ASTNULL ? null :(AST)_t;
					match(_t,NODE);
					_t = _t.getFirstChild();
					e=node(_t,scope, node, e);
					_t = _retTree;
					_t = __t281;
					_t = _t.getNextSibling();
				}
				else {
					break _loop282;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				
								e = setProducer (scope, e, root);
							
			}
			_t = __t279;
			_t = _t.getNextSibling();
			break;
		}
		case TREE:
		{
			AST __t283 = _t;
			AST tmp167_AST_in = (AST)_t;
			match(_t,TREE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
								e = new Block ();
								Local prod = pushProducer (scope, root);
								e1 = prod.createExpression (scope, root);
								e1 = compileMethodInvocation (e1, PRODUCER_PUSH, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root);
								e1 = compileMethodInvocation (e1, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root);
								e.add (setProducer (scope, e1, root));
							
			}
			{
			_loop285:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_8.member(_t.getType()))) {
					e1=stat(_t,scope, null);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						e.add (e1);
					}
				}
				else {
					break _loop285;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				
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
			_t = __t283;
			_t = _t.getNextSibling();
			break;
		}
		case SEPARATE:
		{
			AST tmp168_AST_in = (AST)_t;
			match(_t,SEPARATE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new Block ();
							e2 = scope.getProduceScope ().createExpression (scope, root);
							e2 = compileMethodInvocation (e2, PRODUCER_SEPARATE, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, root);
							e.add (setProducer (scope, e2, root));
						
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
					setAST (e, root);
				
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  produce(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST produce_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST g = null;
		AST s = null;
		
				e = null;
				Expression producer = null;
				ProduceScope slistScope = null;
			
		
		AST __t154 = _t;
		g = _t==ASTNULL ? null :(AST)_t;
		match(_t,PRODUCE);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case EMPTY:
		{
			AST tmp169_AST_in = (AST)_t;
			match(_t,EMPTY);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case ASSIGN:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case EXPR:
		case METHOD_CALL:
		case DOT:
		case SUB:
		case LEFT_ARROW:
		case ARROW:
		case QUESTION:
		case MUL:
		case ADD:
		case RANGE:
		case WITH:
		case TYPECAST:
		case TYPECHECK:
		case COM:
		case NOT:
		case NEG:
		case POS:
		case DIV:
		case REM:
		case POW:
		case SHL:
		case SHR:
		case USHR:
		case LE:
		case GE:
		case CMP:
		case NOT_EQUALS:
		case EQUALS:
		case OR:
		case XOR:
		case AND:
		case COR:
		case CAND:
		case ELIST:
		case THIS:
		case NULL_LITERAL:
		case INVALID_EXPR:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case INSTANCEOF:
		case CLASS_LITERAL:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case INC:
		case DEC:
		case POST_INC:
		case POST_DEC:
		case IN:
		case GUARD:
		case ARRAY_ITERATOR:
		case QUERY_EXPR:
		case INVOKE_OP:
		case QUALIFIED_NEW:
		case INDEX_OP:
		case NEW:
		{
			s = _t==ASTNULL ? null : (AST)_t;
			producer=referenceExpr(_t,scope, null);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
							if (producer != null)
							{
								producer = compileMethodInvocation (producer, PRODUCER_BEGIN, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, (s != null) ? s : g);
							}
							e = new Block ();
							slistScope = createProduceScope (scope, e, producer, g);
							e.add (slistScope.getBlock ());
							slistScope.setScopeForLocals (scope);
						
		}
		slistInScope(_t,slistScope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
							producer = slistScope.createExpression (scope, g);
							e.add (compileMethodInvocation (producer, PRODUCER_END, ArgumentTransformations.NO_IMPLICIT_ARGS, Expression.EXPR_0, 0, scope, (s != null) ? s : g));
						
		}
		_t = __t154;
		_t = _t.getNextSibling();
		_retTree = _t;
		return e;
	}
	
	public final void slistInScope(AST _t,
		BlockScope scope
	) throws RecognitionException {
		
		AST slistInScope_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				Expression e;
			
		
		AST __t235 = _t;
		AST tmp170_AST_in = (AST)_t;
		match(_t,SLIST);
		_t = _t.getFirstChild();
		{
		_loop237:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_8.member(_t.getType()))) {
				e=stat(_t,scope, null);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					scope.addExpression (e);
				}
			}
			else {
				break _loop237;
			}
			
		} while (true);
		}
		_t = __t235;
		_t = _t.getNextSibling();
		_retTree = _t;
	}
	
	public final Expression  node(AST _t,
		BlockScope scope, final AST pos, final Expression producerIn
	) throws RecognitionException {
		Expression e;
		
		AST node_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST w = null;
		AST label = null;
		AST edgeOp = null;
		
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
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ROOT:
		{
			AST tmp171_AST_in = (AST)_t;
			match(_t,ROOT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							n = compileMethodInvocation (prodFactory.createExpression (scope, pos), PRODUCER_GET_ROOT, ArgumentTransformations.NO_IMPLICIT_ARGS,
														 Expression.EXPR_0, 0, scope, pos);
						
			}
			break;
		}
		case UNARY_PREFIX:
		{
			AST __t170 = _t;
			AST tmp172_AST_in = (AST)_t;
			match(_t,UNARY_PREFIX);
			_t = _t.getFirstChild();
			n=unaryExpr(_t,exprScope, true);
			_t = _retTree;
			_t = __t170;
			_t = _t.getNextSibling();
			break;
		}
		default:
			boolean synPredMatched159 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==IDENT||_t.getType()==METHOD_CALL||_t.getType()==DOT))) {
				AST __t159 = _t;
				synPredMatched159 = true;
				inputState.guessing++;
				try {
					{
					name(_t);
					_t = _retTree;
					}
				}
				catch (RecognitionException pe) {
					synPredMatched159 = false;
				}
				_t = __t159;
inputState.guessing--;
			}
			if ( synPredMatched159 ) {
				n=nodeExpr(_t,exprScope, prodFactory, pos);
				_t = _retTree;
			}
			else {
				boolean synPredMatched161 = false;
				if (_t==null) _t=ASTNULL;
				if (((_t.getType()==IDENT||_t.getType()==METHOD_CALL||_t.getType()==DOT))) {
					AST __t161 = _t;
					synPredMatched161 = true;
					inputState.guessing++;
					try {
						{
						AST tmp173_AST_in = (AST)_t;
						match(_t,METHOD_CALL);
						_t = _t.getNextSibling();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched161 = false;
					}
					_t = __t161;
inputState.guessing--;
				}
				if ( synPredMatched161 ) {
					n=nodeExpr(_t,exprScope, prodFactory, pos);
					_t = _retTree;
				}
				else {
					boolean synPredMatched163 = false;
					if (_t==null) _t=ASTNULL;
					if (((_t.getType()==WITH))) {
						AST __t163 = _t;
						synPredMatched163 = true;
						inputState.guessing++;
						try {
							{
							AST tmp174_AST_in = (AST)_t;
							match(_t,WITH);
							_t = _t.getNextSibling();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched163 = false;
						}
						_t = __t163;
inputState.guessing--;
					}
					if ( synPredMatched163 ) {
						AST __t164 = _t;
						w = _t==ASTNULL ? null :(AST)_t;
						match(_t,WITH);
						_t = _t.getFirstChild();
						{
						boolean synPredMatched167 = false;
						if (_t==null) _t=ASTNULL;
						if (((_t.getType()==IDENT||_t.getType()==METHOD_CALL||_t.getType()==DOT))) {
							AST __t167 = _t;
							synPredMatched167 = true;
							inputState.guessing++;
							try {
								{
								name(_t);
								_t = _retTree;
								}
							}
							catch (RecognitionException pe) {
								synPredMatched167 = false;
							}
							_t = __t167;
inputState.guessing--;
						}
						if ( synPredMatched167 ) {
							n=nodeExpr(_t,exprScope, prodFactory, pos);
							_t = _retTree;
						}
						else {
							boolean synPredMatched169 = false;
							if (_t==null) _t=ASTNULL;
							if (((_t.getType()==IDENT||_t.getType()==METHOD_CALL||_t.getType()==DOT))) {
								AST __t169 = _t;
								synPredMatched169 = true;
								inputState.guessing++;
								try {
									{
									AST tmp175_AST_in = (AST)_t;
									match(_t,METHOD_CALL);
									_t = _t.getNextSibling();
									}
								}
								catch (RecognitionException pe) {
									synPredMatched169 = false;
								}
								_t = __t169;
inputState.guessing--;
							}
							if ( synPredMatched169 ) {
								n=nodeExpr(_t,exprScope, prodFactory, pos);
								_t = _retTree;
							}
							else if ((_tokenSet_6.member(_t.getType()))) {
								n=referenceExpr(_t,exprScope, null);
								_t = _retTree;
							}
							else {
								throw new NoViableAltException(_t);
							}
							}
							}
							n=withBlock(_t,exprScope, n, w);
							_t = _retTree;
							_t = __t164;
							_t = _t.getNextSibling();
						}
						else if ((_tokenSet_6.member(_t.getType()))) {
							n=expr(_t,exprScope);
							_t = _retTree;
						}
					else {
						throw new NoViableAltException(_t);
					}
					}}}
					}
					{
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==IDENT)) {
						label = (AST)_t;
						match(_t,IDENT);
						_t = _t.getNextSibling();
					}
					else if (((_t.getType() >= BOOLEAN_LITERAL && _t.getType() <= NORMAL))) {
					}
					else {
						throw new NoViableAltException(_t);
					}
					
					}
					if ( inputState.guessing==0 ) {
						
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
					AST __t172 = _t;
					edgeOp = _t==ASTNULL ? null :(AST)_t;
					if ( _t==null ) throw new MismatchedTokenException();
					_t = _t.getFirstChild();
					if ( inputState.guessing==0 ) {
						
									produceArgs.setType (edgeOp, (n != null) ? n : new Expression (Type.INVALID), prodFactory);
								
					}
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case BOOLEAN_LITERAL:
					case INT_LITERAL:
					case LONG_LITERAL:
					case FLOAT_LITERAL:
					case DOUBLE_LITERAL:
					case CHAR_LITERAL:
					case STRING_LITERAL:
					case IDENT:
					case SUPER:
					case ASSIGN:
					case LT:
					case GT:
					case LINE:
					case LEFT_RIGHT_ARROW:
					case PLUS_LEFT_ARROW:
					case PLUS_ARROW:
					case PLUS_LINE:
					case PLUS_LEFT_RIGHT_ARROW:
					case SLASH_LEFT_ARROW:
					case SLASH_ARROW:
					case SLASH_LINE:
					case SLASH_LEFT_RIGHT_ARROW:
					case EXPR:
					case METHOD_CALL:
					case DOT:
					case SUB:
					case LEFT_ARROW:
					case ARROW:
					case QUESTION:
					case MUL:
					case ADD:
					case RANGE:
					case WITH:
					case TYPECAST:
					case TYPECHECK:
					case COM:
					case NOT:
					case NEG:
					case POS:
					case DIV:
					case REM:
					case POW:
					case SHL:
					case SHR:
					case USHR:
					case LE:
					case GE:
					case CMP:
					case NOT_EQUALS:
					case EQUALS:
					case OR:
					case XOR:
					case AND:
					case COR:
					case CAND:
					case ELIST:
					case THIS:
					case NULL_LITERAL:
					case INVALID_EXPR:
					case LONG_LEFT_ARROW:
					case LONG_ARROW:
					case LONG_LEFT_RIGHT_ARROW:
					case INSTANCEOF:
					case CLASS_LITERAL:
					case QUOTE:
					case ADD_ASSIGN:
					case SUB_ASSIGN:
					case MUL_ASSIGN:
					case DIV_ASSIGN:
					case REM_ASSIGN:
					case POW_ASSIGN:
					case SHR_ASSIGN:
					case USHR_ASSIGN:
					case SHL_ASSIGN:
					case AND_ASSIGN:
					case XOR_ASSIGN:
					case OR_ASSIGN:
					case DEFERRED_ASSIGN:
					case DEFERRED_RATE_ASSIGN:
					case DEFERRED_ADD:
					case DEFERRED_SUB:
					case DEFERRED_MUL:
					case DEFERRED_DIV:
					case DEFERRED_REM:
					case DEFERRED_POW:
					case DEFERRED_OR:
					case DEFERRED_AND:
					case DEFERRED_XOR:
					case DEFERRED_SHL:
					case DEFERRED_SHR:
					case DEFERRED_USHR:
					case INC:
					case DEC:
					case POST_INC:
					case POST_DEC:
					case IN:
					case GUARD:
					case ARRAY_ITERATOR:
					case QUERY_EXPR:
					case INVOKE_OP:
					case QUALIFIED_NEW:
					case INDEX_OP:
					case NEW:
					{
						edge=edgeExpr(_t,scope);
						_t = _retTree;
						break;
					}
					case 3:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					_t = __t172;
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						
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
					_retTree = _t;
					return e;
				}
				
	public final Expression  nodeExpr(AST _t,
		BlockScope scope, ExpressionFactory producer, AST pos
	) throws RecognitionException {
		Expression n;
		
		AST nodeExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		AST mcid = null;
		AST mcn = null;
		
				n = null;
				Expression e = null;
				Expression[] args;
				Local ref;
				produceArgs.setType (null, null, producer);
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IDENT:
		case DOT:
		{
			id = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case METHOD_CALL:
		{
			AST __t188 = _t;
			AST tmp176_AST_in = (AST)_t;
			match(_t,METHOD_CALL);
			_t = _t.getFirstChild();
			{
			boolean synPredMatched191 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==IDENT||_t.getType()==DOT))) {
				AST __t191 = _t;
				synPredMatched191 = true;
				inputState.guessing++;
				try {
					{
					name(_t);
					_t = _retTree;
					}
				}
				catch (RecognitionException pe) {
					synPredMatched191 = false;
				}
				_t = __t191;
inputState.guessing--;
			}
			if ( synPredMatched191 ) {
				mcid = _t==ASTNULL ? null : (AST)_t;
				name(_t);
				_t = _retTree;
			}
			else if ((_t.getType()==DOT)) {
				AST __t192 = _t;
				AST tmp177_AST_in = (AST)_t;
				match(_t,DOT);
				_t = _t.getFirstChild();
				e=expr(_t,scope);
				_t = _retTree;
				mcn = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				_t = __t192;
				_t = _t.getNextSibling();
			}
			else {
				throw new NoViableAltException(_t);
			}
			
			}
			args=arglist(_t,scope);
			_t = _retTree;
			_t = __t188;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							n = compileProduceInvocation (mcid, e, mcn, args, scope, pos);
						
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return n;
	}
	
	public final Expression  unaryExpr(AST _t,
		Scope scope, boolean includeInstanceScope
	) throws RecognitionException {
		Expression e;
		
		AST unaryExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
				Expression op = null;
				String opName = null;
				AST root = _t;
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case COM:
		{
			AST __t361 = _t;
			AST tmp178_AST_in = (AST)_t;
			match(_t,COM);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t361;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				op = new Complement (); opName = OPERATOR_NAME_COM;
			}
			break;
		}
		case NOT:
		{
			AST __t362 = _t;
			AST tmp179_AST_in = (AST)_t;
			match(_t,NOT);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t362;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				op = new Not (); opName = OPERATOR_NAME_NOT;
			}
			break;
		}
		case NEG:
		{
			AST __t363 = _t;
			AST tmp180_AST_in = (AST)_t;
			match(_t,NEG);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t363;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				op = new Neg (); opName = OPERATOR_NAME_NEG;
			}
			break;
		}
		case POS:
		{
			AST __t364 = _t;
			AST tmp181_AST_in = (AST)_t;
			match(_t,POS);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t364;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				op = new Pos (); opName = OPERATOR_NAME_POS;
			}
			break;
		}
		case QUOTE:
		{
			AST __t365 = _t;
			AST tmp182_AST_in = (AST)_t;
			match(_t,QUOTE);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t365;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				op = new Id (); opName = OPERATOR_NAME_QUOTE;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  edgeExpr(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST edgeExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST edgeId = null;
		AST mcid = null;
		AST mcn = null;
		AST w = null;
		
				AST pos = _t;
				e = null;
				Expression[] args;
			
		
		boolean synPredMatched176 = false;
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==IDENT||_t.getType()==DOT))) {
			AST __t176 = _t;
			synPredMatched176 = true;
			inputState.guessing++;
			try {
				{
				name(_t);
				_t = _retTree;
				}
			}
			catch (RecognitionException pe) {
				synPredMatched176 = false;
			}
			_t = __t176;
inputState.guessing--;
		}
		if ( synPredMatched176 ) {
			edgeId = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
							e = compileProduceName (edgeId, scope, pos);
						
			}
		}
		else {
			boolean synPredMatched178 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==METHOD_CALL))) {
				AST __t178 = _t;
				synPredMatched178 = true;
				inputState.guessing++;
				try {
					{
					AST tmp183_AST_in = (AST)_t;
					match(_t,METHOD_CALL);
					_t = _t.getNextSibling();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched178 = false;
				}
				_t = __t178;
inputState.guessing--;
			}
			if ( synPredMatched178 ) {
				AST __t179 = _t;
				AST tmp184_AST_in = (AST)_t;
				match(_t,METHOD_CALL);
				_t = _t.getFirstChild();
				{
				boolean synPredMatched182 = false;
				if (_t==null) _t=ASTNULL;
				if (((_t.getType()==IDENT||_t.getType()==DOT))) {
					AST __t182 = _t;
					synPredMatched182 = true;
					inputState.guessing++;
					try {
						{
						name(_t);
						_t = _retTree;
						}
					}
					catch (RecognitionException pe) {
						synPredMatched182 = false;
					}
					_t = __t182;
inputState.guessing--;
				}
				if ( synPredMatched182 ) {
					mcid = _t==ASTNULL ? null : (AST)_t;
					name(_t);
					_t = _retTree;
				}
				else if ((_t.getType()==DOT)) {
					AST __t183 = _t;
					AST tmp185_AST_in = (AST)_t;
					match(_t,DOT);
					_t = _t.getFirstChild();
					e=expr(_t,scope);
					_t = _retTree;
					mcn = (AST)_t;
					match(_t,IDENT);
					_t = _t.getNextSibling();
					_t = __t183;
					_t = _t.getNextSibling();
				}
				else {
					throw new NoViableAltException(_t);
				}
				
				}
				args=arglist(_t,scope);
				_t = _retTree;
				_t = __t179;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					
								e = compileProduceInvocation (mcid, e, mcn, args, scope, pos);
							
				}
			}
			else {
				boolean synPredMatched185 = false;
				if (_t==null) _t=ASTNULL;
				if (((_t.getType()==WITH))) {
					AST __t185 = _t;
					synPredMatched185 = true;
					inputState.guessing++;
					try {
						{
						AST tmp186_AST_in = (AST)_t;
						match(_t,WITH);
						_t = _t.getNextSibling();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched185 = false;
					}
					_t = __t185;
inputState.guessing--;
				}
				if ( synPredMatched185 ) {
					AST __t186 = _t;
					w = _t==ASTNULL ? null :(AST)_t;
					match(_t,WITH);
					_t = _t.getFirstChild();
					e=edgeExpr(_t,scope);
					_t = _retTree;
					e=withBlock(_t,scope, e, w);
					_t = _retTree;
					_t = __t186;
					_t = _t.getNextSibling();
				}
				else if ((_tokenSet_6.member(_t.getType()))) {
					e=expr(_t,scope);
					_t = _retTree;
				}
				else {
					throw new NoViableAltException(_t);
				}
				}}
				_retTree = _t;
				return e;
			}
			
	public final void constantExprPattern(AST _t) throws RecognitionException {
		
		AST constantExprPattern_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		{
			literal(_t);
			_t = _retTree;
			break;
		}
		case TYPECAST:
		{
			AST __t197 = _t;
			AST tmp187_AST_in = (AST)_t;
			match(_t,TYPECAST);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IDENT:
			case DOT:
			{
				name(_t);
				_t = _retTree;
				break;
			}
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			{
				builtInType(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t197;
			_t = _t.getNextSibling();
			break;
		}
		case TYPECHECK:
		{
			AST __t199 = _t;
			AST tmp188_AST_in = (AST)_t;
			match(_t,TYPECHECK);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IDENT:
			case DOT:
			{
				name(_t);
				_t = _retTree;
				break;
			}
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			{
				builtInType(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t199;
			_t = _t.getNextSibling();
			break;
		}
		case COM:
		{
			AST __t201 = _t;
			AST tmp189_AST_in = (AST)_t;
			match(_t,COM);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t201;
			_t = _t.getNextSibling();
			break;
		}
		case NOT:
		{
			AST __t202 = _t;
			AST tmp190_AST_in = (AST)_t;
			match(_t,NOT);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t202;
			_t = _t.getNextSibling();
			break;
		}
		case NEG:
		{
			AST __t203 = _t;
			AST tmp191_AST_in = (AST)_t;
			match(_t,NEG);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t203;
			_t = _t.getNextSibling();
			break;
		}
		case POS:
		{
			AST __t204 = _t;
			AST tmp192_AST_in = (AST)_t;
			match(_t,POS);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t204;
			_t = _t.getNextSibling();
			break;
		}
		case EXPR:
		{
			AST __t205 = _t;
			AST tmp193_AST_in = (AST)_t;
			match(_t,EXPR);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t205;
			_t = _t.getNextSibling();
			break;
		}
		case DIV:
		{
			AST __t206 = _t;
			AST tmp194_AST_in = (AST)_t;
			match(_t,DIV);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t206;
			_t = _t.getNextSibling();
			break;
		}
		case REM:
		{
			AST __t207 = _t;
			AST tmp195_AST_in = (AST)_t;
			match(_t,REM);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t207;
			_t = _t.getNextSibling();
			break;
		}
		case MUL:
		{
			AST __t208 = _t;
			AST tmp196_AST_in = (AST)_t;
			match(_t,MUL);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t208;
			_t = _t.getNextSibling();
			break;
		}
		case POW:
		{
			AST __t209 = _t;
			AST tmp197_AST_in = (AST)_t;
			match(_t,POW);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t209;
			_t = _t.getNextSibling();
			break;
		}
		case ADD:
		{
			AST __t210 = _t;
			AST tmp198_AST_in = (AST)_t;
			match(_t,ADD);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t210;
			_t = _t.getNextSibling();
			break;
		}
		case SUB:
		{
			AST __t211 = _t;
			AST tmp199_AST_in = (AST)_t;
			match(_t,SUB);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t211;
			_t = _t.getNextSibling();
			break;
		}
		case SHL:
		{
			AST __t212 = _t;
			AST tmp200_AST_in = (AST)_t;
			match(_t,SHL);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t212;
			_t = _t.getNextSibling();
			break;
		}
		case SHR:
		{
			AST __t213 = _t;
			AST tmp201_AST_in = (AST)_t;
			match(_t,SHR);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t213;
			_t = _t.getNextSibling();
			break;
		}
		case USHR:
		{
			AST __t214 = _t;
			AST tmp202_AST_in = (AST)_t;
			match(_t,USHR);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t214;
			_t = _t.getNextSibling();
			break;
		}
		case LT:
		{
			AST __t215 = _t;
			AST tmp203_AST_in = (AST)_t;
			match(_t,LT);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t215;
			_t = _t.getNextSibling();
			break;
		}
		case GT:
		{
			AST __t216 = _t;
			AST tmp204_AST_in = (AST)_t;
			match(_t,GT);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t216;
			_t = _t.getNextSibling();
			break;
		}
		case LE:
		{
			AST __t217 = _t;
			AST tmp205_AST_in = (AST)_t;
			match(_t,LE);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t217;
			_t = _t.getNextSibling();
			break;
		}
		case GE:
		{
			AST __t218 = _t;
			AST tmp206_AST_in = (AST)_t;
			match(_t,GE);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t218;
			_t = _t.getNextSibling();
			break;
		}
		case CMP:
		{
			AST __t219 = _t;
			AST tmp207_AST_in = (AST)_t;
			match(_t,CMP);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t219;
			_t = _t.getNextSibling();
			break;
		}
		case NOT_EQUALS:
		{
			AST __t220 = _t;
			AST tmp208_AST_in = (AST)_t;
			match(_t,NOT_EQUALS);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t220;
			_t = _t.getNextSibling();
			break;
		}
		case EQUALS:
		{
			AST __t221 = _t;
			AST tmp209_AST_in = (AST)_t;
			match(_t,EQUALS);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t221;
			_t = _t.getNextSibling();
			break;
		}
		case OR:
		{
			AST __t222 = _t;
			AST tmp210_AST_in = (AST)_t;
			match(_t,OR);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t222;
			_t = _t.getNextSibling();
			break;
		}
		case XOR:
		{
			AST __t223 = _t;
			AST tmp211_AST_in = (AST)_t;
			match(_t,XOR);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t223;
			_t = _t.getNextSibling();
			break;
		}
		case AND:
		{
			AST __t224 = _t;
			AST tmp212_AST_in = (AST)_t;
			match(_t,AND);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t224;
			_t = _t.getNextSibling();
			break;
		}
		case COR:
		{
			AST __t225 = _t;
			AST tmp213_AST_in = (AST)_t;
			match(_t,COR);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t225;
			_t = _t.getNextSibling();
			break;
		}
		case CAND:
		{
			AST __t226 = _t;
			AST tmp214_AST_in = (AST)_t;
			match(_t,CAND);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t226;
			_t = _t.getNextSibling();
			break;
		}
		case QUESTION:
		{
			AST __t227 = _t;
			AST tmp215_AST_in = (AST)_t;
			match(_t,QUESTION);
			_t = _t.getFirstChild();
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			constantExprPattern(_t);
			_t = _retTree;
			_t = __t227;
			_t = _t.getNextSibling();
			break;
		}
		case IDENT:
		case DOT:
		{
			name(_t);
			_t = _retTree;
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
	}
	
	public final Expression  literal(AST _t) throws RecognitionException {
		Expression e;
		
		AST literal_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
				ASTWithToken ast = (ASTWithToken) _t;
				String text = ast.getText ();
			
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case BOOLEAN_LITERAL:
		{
			AST tmp216_AST_in = (AST)_t;
			match(_t,BOOLEAN_LITERAL);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				e = new BooleanConst (text.equals ("true"));
			}
			break;
		}
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		{
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INT_LITERAL:
			{
				AST tmp217_AST_in = (AST)_t;
				match(_t,INT_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case LONG_LITERAL:
			{
				AST tmp218_AST_in = (AST)_t;
				match(_t,LONG_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case FLOAT_LITERAL:
			{
				AST tmp219_AST_in = (AST)_t;
				match(_t,FLOAT_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case DOUBLE_LITERAL:
			{
				AST tmp220_AST_in = (AST)_t;
				match(_t,DOUBLE_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
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
			break;
		}
		case CHAR_LITERAL:
		{
			AST tmp221_AST_in = (AST)_t;
			match(_t,CHAR_LITERAL);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				e = new CharConst (text.charAt (0));
			}
			break;
		}
		case STRING_LITERAL:
		{
			AST tmp222_AST_in = (AST)_t;
			match(_t,STRING_LITERAL);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				e = new ObjectConst (text.intern (), Type.STRING);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  arrayInitializer(AST _t,
		BlockScope scope, Type type
	) throws RecognitionException {
		Expression e;
		
		AST arrayInitializer_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		AST init = null;
		
				e = null;
				Expression f;
				Object array = null;
			
		
		AST __t231 = _t;
		a = _t==ASTNULL ? null :(AST)_t;
		match(_t,ARRAY_INIT);
		_t = _t.getFirstChild();
		if ( inputState.guessing==0 ) {
			
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
		{
		_loop233:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_4.member(_t.getType()))) {
				init = _t==ASTNULL ? null : (AST)_t;
				f=initializer(_t,scope, type);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
										if (type != null)
										{
											e.add (assignmentConversion (f, type, scope, init));
										}
									
				}
			}
			else {
				break _loop233;
			}
			
		} while (true);
		}
		_t = __t231;
		_t = _t.getNextSibling();
		_retTree = _t;
		return e;
	}
	
	public final Expression  ruleBlock(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST ruleBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST rb = null;
		
				e = null;
				BlockScope s = new BlockScope (scope);
				Expression g = null;
				Local graph = null;
				CompiletimeModel ct = null;
			
		
		AST __t240 = _t;
		rb = _t==ASTNULL ? null :(AST)_t;
		match(_t,RULE_BLOCK);
		_t = _t.getFirstChild();
		g=graph(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
							ct = currentQueryModel;
							if (g != null)
							{
								graph = scope.declareLocal ("graph.", Member.FINAL, g.getType (), rb);
								s.addExpression (graph.createSet ().add (g));
							}
						
		}
		{
		_loop242:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_8.member(_t.getType()))) {
				e=stat(_t,s, null);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					s.addExpression (e);
				}
			}
			else if ((_t.getType()==RULE)) {
				e=rule(_t,s, graph, ct);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					s.addExpression (e);
				}
			}
			else {
				break _loop242;
			}
			
		} while (true);
		}
		_t = __t240;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
						e = s.getBlock ();
					
		}
		_retTree = _t;
		return e;
	}
	
	public final Block  statBlock(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Block b;
		
		AST statBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				b = null;
				Expression e = null;
				BlockScope s = new BlockScope (scope);
				AST root = _t;
				pushProducer (scope, root);
			
		
		{
		boolean synPredMatched246 = false;
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==SLIST))) {
			AST __t246 = _t;
			synPredMatched246 = true;
			inputState.guessing++;
			try {
				{
				AST tmp223_AST_in = (AST)_t;
				match(_t,SLIST);
				_t = _t.getNextSibling();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched246 = false;
			}
			_t = __t246;
inputState.guessing--;
		}
		if ( synPredMatched246 ) {
			slistInScope(_t,s);
			_t = _retTree;
		}
		else if ((_tokenSet_8.member(_t.getType()))) {
			e=stat(_t,s, null);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								s.addExpression (e);
							
			}
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		if ( inputState.guessing==0 ) {
			
					b = (Block) s.getBlock ();
					b.add (popAndJoinProducer (s, root));
				
		}
		_retTree = _t;
		return b;
	}
	
	public final Expression  elist(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST elist_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST el = null;
		e = null; BlockScope s = new BlockScope (scope); boolean ok = true;
		
		AST __t248 = _t;
		el = _t==ASTNULL ? null :(AST)_t;
		match(_t,ELIST);
		_t = _t.getFirstChild();
		{
		_loop250:
		do {
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VARIABLE_DEF:
			{
				e=variableDecl(_t,s);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					s.addExpression (e);
				}
				break;
			}
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case ASSIGN:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case EXPR:
			case METHOD_CALL:
			case DOT:
			case SUB:
			case LEFT_ARROW:
			case ARROW:
			case QUESTION:
			case MUL:
			case ADD:
			case RANGE:
			case WITH:
			case TYPECAST:
			case TYPECHECK:
			case COM:
			case NOT:
			case NEG:
			case POS:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case NOT_EQUALS:
			case EQUALS:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case ELIST:
			case THIS:
			case NULL_LITERAL:
			case INVALID_EXPR:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case INSTANCEOF:
			case CLASS_LITERAL:
			case QUOTE:
			case ADD_ASSIGN:
			case SUB_ASSIGN:
			case MUL_ASSIGN:
			case DIV_ASSIGN:
			case REM_ASSIGN:
			case POW_ASSIGN:
			case SHR_ASSIGN:
			case USHR_ASSIGN:
			case SHL_ASSIGN:
			case AND_ASSIGN:
			case XOR_ASSIGN:
			case OR_ASSIGN:
			case DEFERRED_ASSIGN:
			case DEFERRED_RATE_ASSIGN:
			case DEFERRED_ADD:
			case DEFERRED_SUB:
			case DEFERRED_MUL:
			case DEFERRED_DIV:
			case DEFERRED_REM:
			case DEFERRED_POW:
			case DEFERRED_OR:
			case DEFERRED_AND:
			case DEFERRED_XOR:
			case DEFERRED_SHL:
			case DEFERRED_SHR:
			case DEFERRED_USHR:
			case INC:
			case DEC:
			case POST_INC:
			case POST_DEC:
			case IN:
			case GUARD:
			case ARRAY_ITERATOR:
			case QUERY_EXPR:
			case INVOKE_OP:
			case QUALIFIED_NEW:
			case INDEX_OP:
			case NEW:
			{
				e=expr(_t,s);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
									if (e == null)
									{
										ok = false;
									}
									else
									{
										s.addExpression (e);
									}
								
				}
				break;
			}
			default:
			{
				break _loop250;
			}
			}
		} while (true);
		}
		_t = __t248;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  booleanExpr(AST _t,
		Scope scope
	) throws RecognitionException {
		Expression e;
		
		AST booleanExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST ex = null;
		e = null;
		
		ex = _t==ASTNULL ? null : (AST)_t;
		e=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						e = assignmentConversion (e, Type.BOOLEAN, scope, ex);
					
		}
		_retTree = _t;
		return e;
	}
	
	public final int  labelRef(AST _t,
		BlockScope scope, boolean forContinue, AST pos
	) throws RecognitionException {
		int id;
		
		AST labelRef_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		
				id = -1;
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case IDENT:
		{
			i = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
								id = scope.getMethodScope ().getTargetId (i.getText (), forContinue);
								if (id < 0)
								{
									problems.addSemanticError
										(I18N.msg ((id < -1) ? ProblemReporter.NONLOOP_CONTINUE_TARGET
												   : ProblemReporter.NO_LABEL_IN_SCOPE, i.getText ()), i);
								}
							
			}
			break;
		}
		case 3:
		{
			if ( inputState.guessing==0 ) {
				
								id = scope.getMethodScope ().getTargetId (null, forContinue);
								if (id < 0)
								{
									problems.addSemanticError
										(I18N, forContinue ? ProblemReporter.NO_CONTINUE_TARGET:
										 ProblemReporter.NO_BREAK_TARGET, pos);
								}
							
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_retTree = _t;
		return id;
	}
	
	public final Expression  loop(AST _t,
		BlockScope scope, String label
	) throws RecognitionException {
		Expression e;
		
		AST loop_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST enh = null;
		AST it = null;
		
				e = null;
				Expression e1 = null, e2 = null;
				BlockScope forScope = null;
				scope.getMethodScope ().enterBreakTarget (label);
				Local local = null;
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case FOR:
		{
			AST __t290 = _t;
			AST tmp224_AST_in = (AST)_t;
			match(_t,FOR);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				forScope = new BlockScope (scope);
			}
			slistInScope(_t,forScope);
			_t = _retTree;
			e=booleanExpr(_t,forScope);
			_t = _retTree;
			e1=statBlock(_t,forScope);
			_t = _retTree;
			e2=loopBlock(_t,forScope, label);
			_t = _retTree;
			_t = __t290;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							forScope.addExpression (new For ().add (e).add (e2).add (e1));
							e = forScope.getBlock ();
						
			}
			break;
		}
		case ENHANCED_FOR:
		{
			AST __t291 = _t;
			enh = _t==ASTNULL ? null :(AST)_t;
			match(_t,ENHANCED_FOR);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				forScope = new BlockScope (scope, true);
			}
			it = _t==ASTNULL ? null : (AST)_t;
			e1=expr(_t,forScope);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VARIABLE_DEF:
			{
				e2=variableDecl(_t,forScope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
										if (e2 != null)
										{
											assert shell != null;
										}
										local = (Local) declaredVariable;
									
				}
				break;
			}
			case VOID_:
			{
				AST tmp225_AST_in = (AST)_t;
				match(_t,VOID_);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
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
			e2=loopBlock(_t,forScope, label);
			_t = _retTree;
			_t = __t291;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							forScope.addExpression (e2);
							e = forScope.getBlock ();
						
			}
			break;
		}
		case WHILE:
		{
			AST __t293 = _t;
			AST tmp226_AST_in = (AST)_t;
			match(_t,WHILE);
			_t = _t.getFirstChild();
			e=booleanExpr(_t,scope);
			_t = _retTree;
			e1=loopBlock(_t,scope, label);
			_t = _retTree;
			_t = __t293;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new While ().add (e).add (e1);
						
			}
			break;
		}
		case DO:
		{
			AST __t294 = _t;
			AST tmp227_AST_in = (AST)_t;
			match(_t,DO);
			_t = _t.getFirstChild();
			e=loopBlock(_t,scope, label);
			_t = _retTree;
			e1=booleanExpr(_t,scope);
			_t = _retTree;
			_t = __t294;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = new Do ().add (e).add (e1);
						
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
					scope.getMethodScope ().leave ((BreakTarget) e);
				
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  switchStatement(AST _t,
		BlockScope enclosing, String label
	) throws RecognitionException {
		Expression e;
		
		AST switchStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST sw = null;
		AST swg = null;
		AST sl = null;
		AST def = null;
		
				e = null;
				Switch s = new Switch ();
				BlockScope scope = new BlockScope (enclosing, s);
				Type labelType = null;
				int defaultIndex = -1;
				Int2IntMap switchLabels = new Int2IntMap ();
				scope.getMethodScope ().enterBreakTarget (label);
				int classCount = 0;
			
		
		AST __t297 = _t;
		AST tmp228_AST_in = (AST)_t;
		match(_t,SWITCH);
		_t = _t.getFirstChild();
		sw = _t==ASTNULL ? null : (AST)_t;
		e=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
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
		{
		_loop305:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_t.getType()==SWITCH_GROUP)) {
				AST __t299 = _t;
				swg = _t==ASTNULL ? null :(AST)_t;
				match(_t,SWITCH_GROUP);
				_t = _t.getFirstChild();
				{
				int _cnt302=0;
				_loop302:
				do {
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case CASE:
					{
						AST __t301 = _t;
						AST tmp229_AST_in = (AST)_t;
						match(_t,CASE);
						_t = _t.getFirstChild();
						sl = _t==ASTNULL ? null : (AST)_t;
						e=expr(_t,scope);
						_t = _retTree;
						_t = __t301;
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							
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
						break;
					}
					case DEFAULT:
					{
						def = (AST)_t;
						match(_t,DEFAULT);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							
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
						break;
					}
					default:
					{
						if ( _cnt302>=1 ) { break _loop302; } else {throw new NoViableAltException(_t);}
					}
					}
					_cnt302++;
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					
										classCount = scope.getDeclaredClassCount ();
										pushProducer (scope, swg);
									
				}
				{
				_loop304:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_8.member(_t.getType()))) {
						e=stat(_t,scope, null);
						_t = _retTree;
						if ( inputState.guessing==0 ) {
							s.add (e);
						}
					}
					else {
						break _loop304;
					}
					
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					
										s.add (popAndJoinProducer (scope, swg));
										scope.setDeclaredClassCount (classCount);
									
				}
				_t = __t299;
				_t = _t.getNextSibling();
			}
			else {
				break _loop305;
			}
			
		} while (true);
		}
		_t = __t297;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
					s.initialize (switchLabels, defaultIndex);
					scope.getMethodScope ().leave (s);
					e = scope.getBlock ();
				
		}
		_retTree = _t;
		return e;
	}
	
	public final Block  loopBlock(AST _t,
		BlockScope scope, String label
	) throws RecognitionException {
		Block e;
		
		AST loopBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				e = null;
				scope.getMethodScope ().enterContinueTarget (label);
			
		
		e=statBlock(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
					scope.getMethodScope ().leave (e);
				
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression[]  exprlist(AST _t,
		Scope scope
	) throws RecognitionException {
		Expression[] list;
		
		AST exprlist_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				int i = 0;
				for (AST a = exprlist_AST_in; a != null; a = a.getNextSibling ())
				{
					i++;
				}
				list = new Expression[i];
				i = 0;
				Expression e;
			
		
		{
		_loop310:
		do {
			if (_t==null) _t=ASTNULL;
			if ((_tokenSet_6.member(_t.getType()))) {
				e=expr(_t,scope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				break _loop310;
			}
			
		} while (true);
		}
		_retTree = _t;
		return list;
	}
	
	public final Expression  blockExpr(AST _t,
		Scope s
	) throws RecognitionException {
		Expression e;
		
		AST blockExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST w = null;
		AST q = null;
		AST qnid = null;
		
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
			
		
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ASSIGN:
		{
			AST __t368 = _t;
			AST tmp230_AST_in = (AST)_t;
			match(_t,ASSIGN);
			_t = _t.getFirstChild();
			e=assignOp(_t,scope, root);
			_t = _retTree;
			_t = __t368;
			_t = _t.getNextSibling();
			break;
		}
		case ADD_ASSIGN:
		{
			AST __t369 = _t;
			AST tmp231_AST_in = (AST)_t;
			match(_t,ADD_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Add (), OPERATOR_NAME_ADD_ASSIGN, root);
			_t = _retTree;
			_t = __t369;
			_t = _t.getNextSibling();
			break;
		}
		case SUB_ASSIGN:
		{
			AST __t370 = _t;
			AST tmp232_AST_in = (AST)_t;
			match(_t,SUB_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Sub (), OPERATOR_NAME_SUB_ASSIGN, root);
			_t = _retTree;
			_t = __t370;
			_t = _t.getNextSibling();
			break;
		}
		case MUL_ASSIGN:
		{
			AST __t371 = _t;
			AST tmp233_AST_in = (AST)_t;
			match(_t,MUL_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Mul (), OPERATOR_NAME_MUL_ASSIGN, root);
			_t = _retTree;
			_t = __t371;
			_t = _t.getNextSibling();
			break;
		}
		case DIV_ASSIGN:
		{
			AST __t372 = _t;
			AST tmp234_AST_in = (AST)_t;
			match(_t,DIV_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Div (), OPERATOR_NAME_DIV_ASSIGN, root);
			_t = _retTree;
			_t = __t372;
			_t = _t.getNextSibling();
			break;
		}
		case REM_ASSIGN:
		{
			AST __t373 = _t;
			AST tmp235_AST_in = (AST)_t;
			match(_t,REM_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Rem (), OPERATOR_NAME_REM_ASSIGN, root);
			_t = _retTree;
			_t = __t373;
			_t = _t.getNextSibling();
			break;
		}
		case POW_ASSIGN:
		{
			AST __t374 = _t;
			AST tmp236_AST_in = (AST)_t;
			match(_t,POW_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Power (), OPERATOR_NAME_POW_ASSIGN, root);
			_t = _retTree;
			_t = __t374;
			_t = _t.getNextSibling();
			break;
		}
		case SHR_ASSIGN:
		{
			AST __t375 = _t;
			AST tmp237_AST_in = (AST)_t;
			match(_t,SHR_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Shr (), OPERATOR_NAME_SHR_ASSIGN, root);
			_t = _retTree;
			_t = __t375;
			_t = _t.getNextSibling();
			break;
		}
		case USHR_ASSIGN:
		{
			AST __t376 = _t;
			AST tmp238_AST_in = (AST)_t;
			match(_t,USHR_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Ushr (), OPERATOR_NAME_USHR_ASSIGN, root);
			_t = _retTree;
			_t = __t376;
			_t = _t.getNextSibling();
			break;
		}
		case SHL_ASSIGN:
		{
			AST __t377 = _t;
			AST tmp239_AST_in = (AST)_t;
			match(_t,SHL_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Shl (), OPERATOR_NAME_SHL_ASSIGN, root);
			_t = _retTree;
			_t = __t377;
			_t = _t.getNextSibling();
			break;
		}
		case AND_ASSIGN:
		{
			AST __t378 = _t;
			AST tmp240_AST_in = (AST)_t;
			match(_t,AND_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new And (), OPERATOR_NAME_AND_ASSIGN, root);
			_t = _retTree;
			_t = __t378;
			_t = _t.getNextSibling();
			break;
		}
		case XOR_ASSIGN:
		{
			AST __t379 = _t;
			AST tmp241_AST_in = (AST)_t;
			match(_t,XOR_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Xor (), OPERATOR_NAME_XOR_ASSIGN, root);
			_t = _retTree;
			_t = __t379;
			_t = _t.getNextSibling();
			break;
		}
		case OR_ASSIGN:
		{
			AST __t380 = _t;
			AST tmp242_AST_in = (AST)_t;
			match(_t,OR_ASSIGN);
			_t = _t.getFirstChild();
			e=compoundAssignOp(_t,scope, new Or (), OPERATOR_NAME_OR_ASSIGN, root);
			_t = _retTree;
			_t = __t380;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_ASSIGN:
		{
			AST __t381 = _t;
			AST tmp243_AST_in = (AST)_t;
			match(_t,DEFERRED_ASSIGN);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_ASSIGN, root);
			_t = _retTree;
			_t = __t381;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_RATE_ASSIGN:
		{
			AST __t382 = _t;
			AST tmp244_AST_in = (AST)_t;
			match(_t,DEFERRED_RATE_ASSIGN);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_RATE_ASSIGN, root);
			_t = _retTree;
			_t = __t382;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_ADD:
		{
			AST __t383 = _t;
			AST tmp245_AST_in = (AST)_t;
			match(_t,DEFERRED_ADD);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_ADD, root);
			_t = _retTree;
			_t = __t383;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_SUB:
		{
			AST __t384 = _t;
			AST tmp246_AST_in = (AST)_t;
			match(_t,DEFERRED_SUB);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_SUB, root);
			_t = _retTree;
			_t = __t384;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_MUL:
		{
			AST __t385 = _t;
			AST tmp247_AST_in = (AST)_t;
			match(_t,DEFERRED_MUL);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_MUL, root);
			_t = _retTree;
			_t = __t385;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_DIV:
		{
			AST __t386 = _t;
			AST tmp248_AST_in = (AST)_t;
			match(_t,DEFERRED_DIV);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_DIV, root);
			_t = _retTree;
			_t = __t386;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_REM:
		{
			AST __t387 = _t;
			AST tmp249_AST_in = (AST)_t;
			match(_t,DEFERRED_REM);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_REM, root);
			_t = _retTree;
			_t = __t387;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_POW:
		{
			AST __t388 = _t;
			AST tmp250_AST_in = (AST)_t;
			match(_t,DEFERRED_POW);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_POW, root);
			_t = _retTree;
			_t = __t388;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_OR:
		{
			AST __t389 = _t;
			AST tmp251_AST_in = (AST)_t;
			match(_t,DEFERRED_OR);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_OR, root);
			_t = _retTree;
			_t = __t389;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_AND:
		{
			AST __t390 = _t;
			AST tmp252_AST_in = (AST)_t;
			match(_t,DEFERRED_AND);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_AND, root);
			_t = _retTree;
			_t = __t390;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_XOR:
		{
			AST __t391 = _t;
			AST tmp253_AST_in = (AST)_t;
			match(_t,DEFERRED_XOR);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_XOR, root);
			_t = _retTree;
			_t = __t391;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_SHL:
		{
			AST __t392 = _t;
			AST tmp254_AST_in = (AST)_t;
			match(_t,DEFERRED_SHL);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_SHL, root);
			_t = _retTree;
			_t = __t392;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_SHR:
		{
			AST __t393 = _t;
			AST tmp255_AST_in = (AST)_t;
			match(_t,DEFERRED_SHR);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_SHR, root);
			_t = _retTree;
			_t = __t393;
			_t = _t.getNextSibling();
			break;
		}
		case DEFERRED_USHR:
		{
			AST __t394 = _t;
			AST tmp256_AST_in = (AST)_t;
			match(_t,DEFERRED_USHR);
			_t = _t.getFirstChild();
			e=deferredOp(_t,scope, OPERATOR_NAME_DEFERRED_USHR, root);
			_t = _retTree;
			_t = __t394;
			_t = _t.getNextSibling();
			break;
		}
		case INC:
		{
			AST __t395 = _t;
			AST tmp257_AST_in = (AST)_t;
			match(_t,INC);
			_t = _t.getFirstChild();
			e=incOp(_t,scope, false, (byte) 1, OPERATOR_NAME_INC, root);
			_t = _retTree;
			_t = __t395;
			_t = _t.getNextSibling();
			break;
		}
		case DEC:
		{
			AST __t396 = _t;
			AST tmp258_AST_in = (AST)_t;
			match(_t,DEC);
			_t = _t.getFirstChild();
			e=incOp(_t,scope, false, (byte) -1, OPERATOR_NAME_DEC, root);
			_t = _retTree;
			_t = __t396;
			_t = _t.getNextSibling();
			break;
		}
		case POST_INC:
		{
			AST __t397 = _t;
			AST tmp259_AST_in = (AST)_t;
			match(_t,POST_INC);
			_t = _t.getFirstChild();
			e=incOp(_t,scope, true, (byte) 1, OPERATOR_NAME_POST_INC, root);
			_t = _retTree;
			_t = __t397;
			_t = _t.getNextSibling();
			break;
		}
		case POST_DEC:
		{
			AST __t398 = _t;
			AST tmp260_AST_in = (AST)_t;
			match(_t,POST_DEC);
			_t = _t.getFirstChild();
			e=incOp(_t,scope, true, (byte) -1, OPERATOR_NAME_POST_DEC, root);
			_t = _retTree;
			_t = __t398;
			_t = _t.getNextSibling();
			break;
		}
		case IN:
		{
			AST __t399 = _t;
			AST tmp261_AST_in = (AST)_t;
			match(_t,IN);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, e2 = new Equals (), OPERATOR_NAME_IN, root);
			_t = _retTree;
			_t = __t399;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							if (e == e2)
							{
								e = compileAggregateBooleanOr (e, scope, root);
							}
						
			}
			break;
		}
		case GUARD:
		{
			AST __t400 = _t;
			AST tmp262_AST_in = (AST)_t;
			match(_t,GUARD);
			_t = _t.getFirstChild();
			e=binaryOp(_t,scope, new Guard (), OPERATOR_NAME_GUARD, root);
			_t = _retTree;
			_t = __t400;
			_t = _t.getNextSibling();
			break;
		}
		case ARRAY_ITERATOR:
		{
			AST __t401 = _t;
			AST tmp263_AST_in = (AST)_t;
			match(_t,ARRAY_ITERATOR);
			_t = _t.getFirstChild();
			e=expr(_t,scope);
			_t = _retTree;
			_t = __t401;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							if (e != null)
							{
								e = compileArrayGenerator (e, scope, root);
							}
						
			}
			break;
		}
		case WITH:
		{
			AST __t402 = _t;
			w = _t==ASTNULL ? null :(AST)_t;
			match(_t,WITH);
			_t = _t.getFirstChild();
			e=referenceExpr(_t,scope, null);
			_t = _retTree;
			e=withBlock(_t,scope, e, w);
			_t = _retTree;
			_t = __t402;
			_t = _t.getNextSibling();
			break;
		}
		case METHOD_CALL:
		{
			e=methodInvocation(_t,scope, METHOD_ARGS);
			_t = _retTree;
			break;
		}
		case QUERY_EXPR:
		{
			AST __t403 = _t;
			q = _t==ASTNULL ? null :(AST)_t;
			match(_t,QUERY_EXPR);
			_t = _t.getFirstChild();
			e=graph(_t,scope);
			_t = _retTree;
			e=query(_t,scope, e, currentQueryModel, false, false);
			_t = _retTree;
			_t = __t403;
			_t = _t.getNextSibling();
			break;
		}
		case ELIST:
		{
			e=elist(_t,scope);
			_t = _retTree;
			break;
		}
		case INDEX_OP:
		{
			e=arrayIndex(_t,scope);
			_t = _retTree;
			break;
		}
		case NEW:
		{
			e=newExpression(_t,scope);
			_t = _retTree;
			break;
		}
		case INVOKE_OP:
		{
			AST __t404 = _t;
			AST tmp264_AST_in = (AST)_t;
			match(_t,INVOKE_OP);
			_t = _t.getFirstChild();
			list=exprlist(_t,scope);
			_t = _retTree;
			_t = __t404;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							if (list != null)
							{
								e = compileOperator (null, OPERATOR_NAME_INVOKE, list, 0, scope, root);
							}
						
			}
			break;
		}
		case QUALIFIED_NEW:
		{
			AST __t405 = _t;
			AST tmp265_AST_in = (AST)_t;
			match(_t,QUALIFIED_NEW);
			_t = _t.getFirstChild();
			e=referenceExpr(_t,scope, null);
			_t = _retTree;
			qnid = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			list=arglist(_t,scope);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
								if (e != null)
								{
									t = resolver.resolveTypeName (e.getType (), qnid, scope);
								}
							
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CLASS:
			{
				e=anonymousClass(_t,scope, t, qnid, e, list, root);
				_t = _retTree;
				break;
			}
			case 3:
			{
				if ( inputState.guessing==0 ) {
					
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
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t405;
			_t = _t.getNextSibling();
			break;
		}
		case THIS:
		{
			AST __t407 = _t;
			AST tmp266_AST_in = (AST)_t;
			match(_t,THIS);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IDENT:
			case DECLARING_TYPE:
			case DOT:
			{
				t=classType(_t,scope);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t407;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = compileInstance (t, scope, root, false);
						
			}
			break;
		}
		case SUPER:
		{
			AST __t409 = _t;
			AST tmp267_AST_in = (AST)_t;
			match(_t,SUPER);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IDENT:
			case DECLARING_TYPE:
			case DOT:
			{
				t=classType(_t,scope);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t409;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
							e = compileInstance (t, scope, root, true);
						
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  binaryOp(AST _t,
		Scope scope, Expression op, String opName, AST root
	) throws RecognitionException {
		Expression e;
		
		AST binaryOp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		e = null; Expression expr1, expr2;
		
		expr1=expr(_t,scope);
		_t = _retTree;
		expr2=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						if ((expr1 != null) && (expr2 != null))
						{
							e = compileOperator (op, opName, new Expression[] {expr1, expr2}, 0, scope, root);
						}
						if ((e == null) && (op != null) && (op.etype >= 0))
						{
							e = new Expression (op.getType ());
						}
					
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  assignOp(AST _t,
		BlockScope scope, AST pos
	) throws RecognitionException {
		Expression e;
		
		AST assignOp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		
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
			
		
		{
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==IDENT))&&(implicit)) {
			id = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
		}
		else if ((_tokenSet_6.member(_t.getType()))) {
			expr1=expr(_t,scope);
			_t = _retTree;
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		expr2=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  compoundAssignOp(AST _t,
		BlockScope scope, Expression op, String operatorName, AST root
	) throws RecognitionException {
		Expression e;
		
		AST compoundAssignOp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST ex = null;
		e = null; Expression expr1, expr2;
		
		ex = _t==ASTNULL ? null : (AST)_t;
		expr1=expr(_t,scope);
		_t = _retTree;
		expr2=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						e = compileCompoundAssignment (op, operatorName, expr1, expr2, 0, scope, ex, root);
					
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  deferredOp(AST _t,
		BlockScope scope, String op, AST pos
	) throws RecognitionException {
		Expression e;
		
		AST deferredOp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST ex = null;
		e = null; Expression rhs;
		
		ex = _t==ASTNULL ? null : (AST)_t;
		e=expr(_t,scope);
		_t = _retTree;
		rhs=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						e = compileDeferredAssignment (op, e, rhs, scope, ex, pos);
					
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  incOp(AST _t,
		BlockScope scope, boolean postfix, byte inc, String operatorName, AST root
	) throws RecognitionException {
		Expression e;
		
		AST incOp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST ex = null;
		
				e = null;
			
		
		ex = _t==ASTNULL ? null : (AST)_t;
		e=expr(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
						e = compileCompoundAssignment
							(new Add (), operatorName, e,
							 Expression.createConst (Type.INT, Integer.valueOf (inc)),
							 postfix ? OP_POST | OP_INC : OP_INC, scope, ex, root);
					
		}
		_retTree = _t;
		return e;
	}
	
	public final Expression  methodInvocation(AST _t,
		BlockScope scope, ArgumentTransformations impl
	) throws RecognitionException {
		Expression e;
		
		AST methodInvocation_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST mc = null;
		AST id = null;
		AST name = null;
		
				e = null;
				Expression e2 = null;
				Expression[] list;
			
		
		AST __t412 = _t;
		mc = _t==ASTNULL ? null :(AST)_t;
		match(_t,METHOD_CALL);
		_t = _t.getFirstChild();
		{
		boolean synPredMatched415 = false;
		if (_t==null) _t=ASTNULL;
		if (((_t.getType()==IDENT||_t.getType()==DOT))) {
			AST __t415 = _t;
			synPredMatched415 = true;
			inputState.guessing++;
			try {
				{
				name(_t);
				_t = _retTree;
				}
			}
			catch (RecognitionException pe) {
				synPredMatched415 = false;
			}
			_t = __t415;
inputState.guessing--;
		}
		if ( synPredMatched415 ) {
			id = _t==ASTNULL ? null : (AST)_t;
			name(_t);
			_t = _retTree;
		}
		else if ((_t.getType()==DOT)) {
			AST __t416 = _t;
			AST tmp268_AST_in = (AST)_t;
			match(_t,DOT);
			_t = _t.getFirstChild();
			e2=expr(_t,scope);
			_t = _retTree;
			name = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t416;
			_t = _t.getNextSibling();
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		list=arglist(_t,scope);
		_t = _retTree;
		_t = __t412;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  arrayIndex(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST arrayIndex_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST iop = null;
		AST aex = null;
		AST id = null;
		AST iex = null;
		
				e = null;
				Expression array = null;
				Object index = null;
				int ic = _t.getNumberOfChildren () - 1;
				Expression[] list = null;
			
		
		AST __t429 = _t;
		iop = _t==ASTNULL ? null :(AST)_t;
		match(_t,INDEX_OP);
		_t = _t.getFirstChild();
		{
		if (_t==null) _t=ASTNULL;
		if (((_tokenSet_9.member(_t.getType())))&&(ic > 1)) {
			list=exprlist(_t,scope);
			_t = _retTree;
		}
		else if ((_tokenSet_6.member(_t.getType()))) {
			aex = _t==ASTNULL ? null : (AST)_t;
			array=expr(_t,scope);
			_t = _retTree;
			{
			boolean synPredMatched433 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==IDENT))) {
				AST __t433 = _t;
				synPredMatched433 = true;
				inputState.guessing++;
				try {
					{
					AST tmp269_AST_in = (AST)_t;
					match(_t,IDENT);
					_t = _t.getNextSibling();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched433 = false;
				}
				_t = __t433;
inputState.guessing--;
			}
			if ( synPredMatched433 ) {
				id = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
			}
			else if ((_tokenSet_6.member(_t.getType()))) {
				iex = _t==ASTNULL ? null : (AST)_t;
				index=expr(_t,scope);
				_t = _retTree;
			}
			else {
				throw new NoViableAltException(_t);
			}
			
			}
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		_t = __t429;
		_t = _t.getNextSibling();
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
		return e;
	}
	
	public final Expression  newExpression(AST _t,
		BlockScope scope
	) throws RecognitionException {
		Expression e;
		
		AST newExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST ts = null;
		AST dim = null;
		e = null; Type t; Expression[] args; Expression e2 = null;
		
		AST __t435 = _t;
		n = _t==ASTNULL ? null :(AST)_t;
		match(_t,NEW);
		_t = _t.getFirstChild();
		ts = _t==ASTNULL ? null : (AST)_t;
		t=typeSpec(_t,scope);
		_t = _retTree;
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case ARGLIST:
		{
			args=arglist(_t,scope);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CLASS:
			{
				e=anonymousClass(_t,scope, t, ts, null, args, n);
				_t = _retTree;
				break;
			}
			case 3:
			{
				if ( inputState.guessing==0 ) {
					
											e = compileConstructorInvocation
												(new New (t), null, args, scope, n, false);
										
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			break;
		}
		case ARRAY_INIT:
		{
			e=arrayInitializer(_t,scope, t);
			_t = _retTree;
			break;
		}
		case DIMLIST:
		{
			AST __t438 = _t;
			AST tmp270_AST_in = (AST)_t;
			match(_t,DIMLIST);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				e = new CreateArray (t);
			}
			{
			_loop440:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_6.member(_t.getType()))) {
					dim = _t==ASTNULL ? null : (AST)_t;
					e2=expr(_t,scope);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						
													if (e2 != null)
													{
														e2 = assignmentConversion (e2, Type.INT, scope, dim);
														if (e != null)
														{
															e.add (e2);
														}
													}
												
					}
				}
				else {
					break _loop440;
				}
				
			} while (true);
			}
			_t = __t438;
			_t = _t.getNextSibling();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		_t = __t435;
		_t = _t.getNextSibling();
		_retTree = _t;
		return e;
	}
	
	public final long  modifier(AST _t) throws RecognitionException {
		long m;
		
		AST modifier_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		m = 0;
		
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case PRIVATE_:
		{
			AST tmp271_AST_in = (AST)_t;
			match(_t,PRIVATE_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.PRIVATE;
			}
			break;
		}
		case PUBLIC_:
		{
			AST tmp272_AST_in = (AST)_t;
			match(_t,PUBLIC_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.PUBLIC;
			}
			break;
		}
		case PROTECTED_:
		{
			AST tmp273_AST_in = (AST)_t;
			match(_t,PROTECTED_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.PROTECTED;
			}
			break;
		}
		case STATIC_:
		{
			AST tmp274_AST_in = (AST)_t;
			match(_t,STATIC_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.STATIC;
			}
			break;
		}
		case TRANSIENT_:
		{
			AST tmp275_AST_in = (AST)_t;
			match(_t,TRANSIENT_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.TRANSIENT;
			}
			break;
		}
		case FINAL_:
		{
			AST tmp276_AST_in = (AST)_t;
			match(_t,FINAL_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.FINAL;
			}
			break;
		}
		case ABSTRACT_:
		{
			AST tmp277_AST_in = (AST)_t;
			match(_t,ABSTRACT_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.ABSTRACT;
			}
			break;
		}
		case NATIVE_:
		{
			AST tmp278_AST_in = (AST)_t;
			match(_t,NATIVE_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.NATIVE;
			}
			break;
		}
		case SYNCHRONIZED_:
		{
			AST tmp279_AST_in = (AST)_t;
			match(_t,SYNCHRONIZED_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.SYNCHRONIZED;
			}
			break;
		}
		case VOLATILE_:
		{
			AST tmp280_AST_in = (AST)_t;
			match(_t,VOLATILE_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.VOLATILE;
			}
			break;
		}
		case STRICT_:
		{
			AST tmp281_AST_in = (AST)_t;
			match(_t,STRICT_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = Member.STRICT;
			}
			break;
		}
		case ITERATING_:
		{
			AST tmp282_AST_in = (AST)_t;
			match(_t,ITERATING_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = MOD_ITERATING;
			}
			break;
		}
		case CONST_:
		{
			AST tmp283_AST_in = (AST)_t;
			match(_t,CONST_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m = MOD_CONST;
			}
			break;
		}
		case VARARGS_:
		{
			AST tmp284_AST_in = (AST)_t;
			match(_t,VARARGS_);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m |= Member.VARARGS;
			}
			break;
		}
		case STATIC_MEMBER_CLASSES:
		{
			AST tmp285_AST_in = (AST)_t;
			match(_t,STATIC_MEMBER_CLASSES);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				m |= MOD_STATIC_MEMBER_CLASSES;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		_retTree = _t;
		return m;
	}
	
	public final AnnotationInfo  annotation(AST _t,
		Scope scope
	) throws RecognitionException {
		AnnotationInfo info;
		
		AST annotation_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		AST cls = null;
		AST se = null;
		AST id = null;
		
				Expression[] list;
				ASTWithToken value = null;
				int ec = problems.getErrorCount ();
				info = null;
				Type atype;
			
		
		AST __t449 = _t;
		a = _t==ASTNULL ? null :(AST)_t;
		match(_t,ANNOTATION);
		_t = _t.getFirstChild();
		cls = _t==ASTNULL ? null : (AST)_t;
		atype=classType(_t,scope);
		_t = _retTree;
		if ( inputState.guessing==0 ) {
			
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
		{
		if (_t==null) _t=ASTNULL;
		switch ( _t.getType()) {
		case MARKER:
		{
			AST tmp286_AST_in = (AST)_t;
			match(_t,MARKER);
			_t = _t.getNextSibling();
			break;
		}
		case SINGLE_ELEMENT:
		{
			AST __t451 = _t;
			se = _t==ASTNULL ? null :(AST)_t;
			match(_t,SINGLE_ELEMENT);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
										value = new ASTWithToken ();
										value.initialize (se);
										value.setText ("value");
									
			}
			elementValuePair(_t,info, value);
			_t = _retTree;
			_t = __t451;
			_t = _t.getNextSibling();
			break;
		}
		case NORMAL:
		{
			AST __t452 = _t;
			AST tmp287_AST_in = (AST)_t;
			match(_t,NORMAL);
			_t = _t.getFirstChild();
			{
			_loop455:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ASSIGN)) {
					AST __t454 = _t;
					AST tmp288_AST_in = (AST)_t;
					match(_t,ASSIGN);
					_t = _t.getFirstChild();
					id = (AST)_t;
					match(_t,IDENT);
					_t = _t.getNextSibling();
					elementValuePair(_t,info, id);
					_t = _retTree;
					_t = __t454;
					_t = _t.getNextSibling();
				}
				else {
					break _loop455;
				}
				
			} while (true);
			}
			_t = __t452;
			_t = _t.getNextSibling();
			break;
		}
		default:
		{
			throw new NoViableAltException(_t);
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
							if (info.run == run)
							{
								info.finish (problems.getErrorCount () > ec);
							}
						
		}
		_t = __t449;
		_t = _t.getNextSibling();
		_retTree = _t;
		return info;
	}
	
	public final void elementValuePair(AST _t,
		AnnotationInfo info, AST element
	) throws RecognitionException {
		
		AST elementValuePair_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				Type elementType = null;
				AST pos = _t;
				Object value = null;
				Type ct = null;
				ObjectList<Object> values = null;
			
		
		if ( inputState.guessing==0 ) {
			
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
		{
		if (_t==null) _t=ASTNULL;
		if ((((_t.getType() >= BOOLEAN_LITERAL && _t.getType() <= NORMAL)))&&(elementType == null)) {
			AST tmp289_AST_in = (AST)_t;
			if ( _t==null ) throw new MismatchedTokenException();
			_t = _t.getNextSibling();
		}
		else if ((_t.getType()==ARRAY_INIT)) {
			AST __t458 = _t;
			AST tmp290_AST_in = (AST)_t;
			match(_t,ARRAY_INIT);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				
								if (ct == null)
								{
									problems.addSemanticError
										(I18N.msg (ProblemReporter.ARRAYINIT_FOR_NONARRAY, elementType), pos);
								}
								values = new ObjectList<Object> ();
							
			}
			{
			_loop460:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_10.member(_t.getType()))) {
					value=elementValue(_t,ct, info);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						
											if (value != null)
											{
												values.add (value);
											}
										
					}
				}
				else {
					break _loop460;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				
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
			_t = __t458;
			_t = _t.getNextSibling();
		}
		else if ((_tokenSet_10.member(_t.getType()))) {
			value=elementValue(_t,(ct != null) ? ct : elementType, info);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				
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
		}
		else {
			throw new NoViableAltException(_t);
		}
		
		}
		if ( inputState.guessing==0 ) {
			
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
		_retTree = _t;
	}
	
	public final Object  elementValue(AST _t,
		Type expected, AnnotationInfo info
	) throws RecognitionException {
		Object value;
		
		AST elementValue_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
				AST pos = _t;
				value = null;
				Expression e;
				AnnotationInfo a = null;
			
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			if (((_tokenSet_10.member(_t.getType())))&&(run != info.run)) {
				AST tmp291_AST_in = (AST)_t;
				matchNot(_t,ARRAY_INIT);
				_t = _t.getNextSibling();
			}
			else if ((_t.getType()==ANNOTATION)) {
				a=annotation(_t,info.scope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else if ((_tokenSet_6.member(_t.getType()))) {
				e=expr(_t,info.scope);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					
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
			}
			else {
				throw new NoViableAltException(_t);
			}
			
		}
		catch (ClassNotReadyException ex) {
			if (inputState.guessing==0) {
				
						assert run == METHOD_DECLARATION;
						info.deferToCompilation ();
						_t = pos.getNextSibling ();
					
			} else {
				throw ex;
			}
		}
		_retTree = _t;
		return value;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"BOOLEAN_LITERAL",
		"INT_LITERAL",
		"LONG_LITERAL",
		"FLOAT_LITERAL",
		"DOUBLE_LITERAL",
		"CHAR_LITERAL",
		"STRING_LITERAL",
		"<identifier>",
		"COMPILATION_UNIT",
		"PACKAGE",
		"MODULE",
		"CLASS",
		"INTERFACE",
		"IMPORT_ON_DEMAND",
		"STATIC_IMPORT_ON_DEMAND",
		"SINGLE_TYPE_IMPORT",
		"SINGLE_STATIC_IMPORT",
		"EXTENDS",
		"IMPLEMENTS",
		"PARAMETERS",
		"PARAMETER_DEF",
		"SUPER",
		"ARGLIST",
		"SLIST",
		"INSTANTIATOR",
		"METHOD",
		"THROWS",
		"SEMI",
		"CONSTRUCTOR",
		"VARIABLE_DEF",
		"ASSIGN",
		"ARRAY_DECLARATOR",
		"DECLARING_TYPE",
		"VOID_",
		"BOOLEAN_",
		"BYTE_",
		"SHORT_",
		"CHAR_",
		"INT_",
		"LONG_",
		"FLOAT_",
		"DOUBLE_",
		"INSTANCE_INIT",
		"STATIC_INIT",
		"EMPTY",
		"QUERY",
		"COMPOUND_PATTERN",
		"LABEL",
		"PATTERN_WITH_BLOCK",
		"MINIMAL",
		"LATE_MATCH",
		"SINGLE_MATCH",
		"OPTIONAL_MATCH",
		"SINGLE_OPTIONAL_MATCH",
		"ANY",
		"FOLDING",
		"SEPARATE",
		"LT",
		"GT",
		"LINE",
		"LEFT_RIGHT_ARROW",
		"PLUS_LEFT_ARROW",
		"PLUS_ARROW",
		"PLUS_LINE",
		"PLUS_LEFT_RIGHT_ARROW",
		"SLASH_LEFT_ARROW",
		"SLASH_ARROW",
		"SLASH_LINE",
		"SLASH_LEFT_RIGHT_ARROW",
		"TYPE_PATTERN",
		"WRAPPED_TYPE_PATTERN",
		"NAME_PATTERN",
		"TREE",
		"CONTEXT",
		"EXPR",
		"ROOT",
		"METHOD_PATTERN",
		"METHOD_CALL",
		"DOT",
		"APPLICATION_CONDITION",
		"PARAMETERIZED_PATTERN",
		"SUB",
		"LEFT_ARROW",
		"ARROW",
		"X_LEFT_RIGHT_ARROW",
		"TRAVERSAL",
		"QUESTION",
		"MUL",
		"ADD",
		"RANGE_EXACTLY",
		"RANGE_MIN",
		"RANGE",
		"RULE",
		"DOUBLE_ARROW_RULE",
		"EXEC_RULE",
		"PRODUCE",
		"WITH",
		"UNARY_PREFIX",
		"TYPECAST",
		"TYPECHECK",
		"COM",
		"NOT",
		"NEG",
		"POS",
		"DIV",
		"REM",
		"POW",
		"SHL",
		"SHR",
		"USHR",
		"LE",
		"GE",
		"CMP",
		"NOT_EQUALS",
		"EQUALS",
		"OR",
		"XOR",
		"AND",
		"COR",
		"CAND",
		"ARRAY_INIT",
		"RULE_BLOCK",
		"ELIST",
		"SHELL_BLOCK",
		"THIS",
		"QUALIFIED_SUPER",
		"IF",
		"RETURN",
		"YIELD",
		"THROW",
		"SYNCHRONIZED_",
		"ASSERT",
		"LABELED_STATEMENT",
		"BREAK",
		"CONTINUE",
		"TRY",
		"CATCH",
		"FINALLY",
		"NODES",
		"NODE",
		"FOR",
		"ENHANCED_FOR",
		"WHILE",
		"DO",
		"SWITCH",
		"SWITCH_GROUP",
		"CASE",
		"DEFAULT",
		"NULL_LITERAL",
		"INVALID_EXPR",
		"LONG_LEFT_ARROW",
		"LONG_ARROW",
		"LONG_LEFT_RIGHT_ARROW",
		"INSTANCEOF",
		"CLASS_LITERAL",
		"QUOTE",
		"ADD_ASSIGN",
		"SUB_ASSIGN",
		"MUL_ASSIGN",
		"DIV_ASSIGN",
		"REM_ASSIGN",
		"POW_ASSIGN",
		"SHR_ASSIGN",
		"USHR_ASSIGN",
		"SHL_ASSIGN",
		"AND_ASSIGN",
		"XOR_ASSIGN",
		"OR_ASSIGN",
		"DEFERRED_ASSIGN",
		"DEFERRED_RATE_ASSIGN",
		"DEFERRED_ADD",
		"DEFERRED_SUB",
		"DEFERRED_MUL",
		"DEFERRED_DIV",
		"DEFERRED_REM",
		"DEFERRED_POW",
		"DEFERRED_OR",
		"DEFERRED_AND",
		"DEFERRED_XOR",
		"DEFERRED_SHL",
		"DEFERRED_SHR",
		"DEFERRED_USHR",
		"INC",
		"DEC",
		"POST_INC",
		"POST_DEC",
		"IN",
		"GUARD",
		"ARRAY_ITERATOR",
		"QUERY_EXPR",
		"INVOKE_OP",
		"QUALIFIED_NEW",
		"INDEX_OP",
		"NEW",
		"DIMLIST",
		"MODIFIERS",
		"ANNOTATION",
		"PRIVATE_",
		"PUBLIC_",
		"PROTECTED_",
		"STATIC_",
		"TRANSIENT_",
		"FINAL_",
		"ABSTRACT_",
		"NATIVE_",
		"VOLATILE_",
		"STRICT_",
		"ITERATING_",
		"CONST_",
		"VARARGS_",
		"STATIC_MEMBER_CLASSES",
		"MARKER",
		"SINGLE_ELEMENT",
		"NORMAL"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 211120057073672L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 211119922855944L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 211119654420480L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[8];
		data[2]=64L;
		data[3]=8388096L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[8];
		data[0]=-2305842992000266256L;
		data[1]=6917528824114987519L;
		data[2]=-16777215L;
		data[3]=63L;
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { -2251799813685248L, 66715647L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=-2305842992000266256L;
		data[1]=5764607319508140543L;
		data[2]=-16777215L;
		data[3]=63L;
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=-2305561517023555600L;
		data[1]=5764607319508140543L;
		data[2]=-16777215L;
		data[3]=63L;
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[8];
		data[0]=-1152921474374266896L;
		data[1]=-1152921673773198849L;
		data[2]=-14716931L;
		data[3]=63L;
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = new long[8];
		data[0]=-2305842992000266248L;
		data[1]=5764607319508140543L;
		data[2]=-16777215L;
		data[3]=63L;
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = new long[8];
		data[0]=-16L;
		data[1]=-1152921504606846977L;
		data[2]=-1L;
		data[3]=67108863L;
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	}
	
