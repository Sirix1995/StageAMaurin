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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import antlr.collections.AST;
import de.grogra.grammar.ASTWithToken;
import de.grogra.grammar.RecognitionException;
import de.grogra.grammar.RecognitionExceptionList;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.AnnotationImpl;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.FieldDecorator;
import de.grogra.reflect.IntersectionType;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Signature;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeDecorator;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.XField;
import de.grogra.util.I18NBundle;
import de.grogra.util.Int2IntMap;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.vfs.FileSystem;
import de.grogra.xl.compiler.pattern.ArgumentDescription;
import de.grogra.xl.compiler.pattern.PatternBuilder;
import de.grogra.xl.compiler.pattern.PatternWrapper;
import de.grogra.xl.compiler.scope.BlockScope;
import de.grogra.xl.compiler.scope.ClassPath;
import de.grogra.xl.compiler.scope.CompilationUnitScope;
import de.grogra.xl.compiler.scope.InstanceScope;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.Members;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.compiler.scope.Package;
import de.grogra.xl.compiler.scope.ProduceScope;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.compiler.scope.SingleStaticImport;
import de.grogra.xl.compiler.scope.StaticImportOnDemand;
import de.grogra.xl.compiler.scope.TypeScope;
import de.grogra.xl.compiler.scope.Members.Applicability;
import de.grogra.xl.expr.ArrayGenerator;
import de.grogra.xl.expr.ArrayInit;
import de.grogra.xl.expr.AssignField;
import de.grogra.xl.expr.AssignLocal;
import de.grogra.xl.expr.Assignment;
import de.grogra.xl.expr.Block;
import de.grogra.xl.expr.BooleanConst;
import de.grogra.xl.expr.Break;
import de.grogra.xl.expr.ByteConst;
import de.grogra.xl.expr.Cast;
import de.grogra.xl.expr.CharConst;
import de.grogra.xl.expr.CheckNonNull;
import de.grogra.xl.expr.ClassConst;
import de.grogra.xl.expr.Constant;
import de.grogra.xl.expr.DisposeDescriptor;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.expr.ExpressionFactory;
import de.grogra.xl.expr.ExpressionList;
import de.grogra.xl.expr.FilterGuard;
import de.grogra.xl.expr.Generator;
import de.grogra.xl.expr.GetDescriptor;
import de.grogra.xl.expr.GetField;
import de.grogra.xl.expr.GetLocal;
import de.grogra.xl.expr.GetProperty;
import de.grogra.xl.expr.GetPropertyInstance;
import de.grogra.xl.expr.If;
import de.grogra.xl.expr.IllegalCastException;
import de.grogra.xl.expr.IllegalOperandTypeException;
import de.grogra.xl.expr.IntConst;
import de.grogra.xl.expr.Invoke;
import de.grogra.xl.expr.InvokeSpecial;
import de.grogra.xl.expr.InvokeStatic;
import de.grogra.xl.expr.InvokeVirtual;
import de.grogra.xl.expr.LocalValue;
import de.grogra.xl.expr.New;
import de.grogra.xl.expr.NoBytecode;
import de.grogra.xl.expr.NonlocalGenerator;
import de.grogra.xl.expr.ObjectConst;
import de.grogra.xl.expr.Pop;
import de.grogra.xl.expr.PopIntArray;
import de.grogra.xl.expr.PushInts;
import de.grogra.xl.expr.Return;
import de.grogra.xl.expr.SetThis;
import de.grogra.xl.expr.ShortConst;
import de.grogra.xl.expr.Super;
import de.grogra.xl.expr.SwapBytecode;
import de.grogra.xl.expr.Switch;
import de.grogra.xl.expr.Throw;
import de.grogra.xl.expr.TypeConst;
import de.grogra.xl.expr.Variable;
import de.grogra.xl.expr.Yield;
import de.grogra.xl.lang.Aggregate;
import de.grogra.xl.lang.ConversionConstructor;
import de.grogra.xl.lang.ConversionType;
import de.grogra.xl.ode.RateAssignment;
import de.grogra.xl.property.CompiletimeModel.Property;
import de.grogra.xl.query.CompiletimeModel;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.UserDefinedPattern;
import de.grogra.xl.util.IntHashMap;
import de.grogra.xl.util.LongHashMap;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.vmx.RoutineDescriptor;
import de.grogra.xl.vmx.VMXState;

/**
 * This helper class is the superclass of {@link de.grogra.xl.compiler.Compiler}.
 * Its members would normally be declared in <code>Compiler</code>. However,
 * since the latter class is generated by ANTLR from a grammar file, it is
 * more convenient to extract these members to a helper class which is defined
 * by this conventional Java source file.
 *
 * @author Ole Kniemeyer
 */
public abstract class CompilerBase extends antlr.TreeParser implements
		CompilerTokenTypes, Members.Resolution
{
	/**
	 * Resource bundle for messages. 
	 */
	public static final I18NBundle I18N = ProblemReporter.I18N;

	/**
	 * Constant for {@link #run} indicating the first run. In this run,
	 * type and field declarations are processed.
	 */
	public static final int TYPE_AND_FIELD_DECLARATION = 0;

	/**
	 * Constant for {@link #run} indicating the second run. In this run,
	 * method and constructor declarations are processed.
	 */
	public static final int METHOD_DECLARATION = 1;

	/**
	 * Constant for {@link #run} indicating the third, final run. In this run,
	 * the actual expressions and statements are compiled.
	 */
	public static final int COMPILATION = 2;

	/**
	 * Current run of the compiler.
	 * 
	 * @see #getRun()
	 */
	int run;

	/**
	 * Sets the current run of the compiler. Should not be invoked by
	 * user code.
	 * 
	 * @param run current run
	 * @see #run
	 */
	void setRun (int run)
	{
		if (run == TYPE_AND_FIELD_DECLARATION)
		{
			members.reset ();
			resolver.reset ();
			units.clear ();
			initializersToCompile.clear ();
			argList = null;
		}
		setLocalRun (run);
	}

	/**
	 * Sets the current local run of the compiler. Should not be invoked by
	 * user code. This method is invoked when local classes are compiled: Each
	 * such declaration induces a sequence of three local runs for that
	 * declaration. 
	 * 
	 * @param run current run
	 * @see #run
	 */
	void setLocalRun (int run)
	{
		this.run = run;
		switch (run)
		{
			case METHOD_DECLARATION:
				resolve (toResolveBeforeMethodDeclaration);
				break;
			case COMPILATION:
				resolve (toResolveBeforeCompilation);
				compileInitializers ();
				initializersToCompile.clear ();
				break;
		}
	}

	/**
	 * Returns the current run of the compiler.
	 * The compiler works in three runs. The return value indicates the current run.
	 * 
	 * @return current run
	 * @see #TYPE_AND_FIELD_DECLARATION
	 * @see #METHOD_DECLARATION
	 * @see #COMPILATION
	 */
	public int getRun ()
	{
		return run;
	}

	public Object compile (ClassPath classPath,
			Scope defaultImports, de.grogra.xl.parser.Parser parser,
			CClass shell) throws java.io.IOException, RecognitionException
	{
		parser.parse ();
		Object o;
		try
		{
			setRun (TYPE_AND_FIELD_DECLARATION);
			compile (classPath, parser.getAST (), parser.getFilename (),
				defaultImports, null, shell);
			setRun (METHOD_DECLARATION);
			compile (classPath, parser.getAST (), parser.getFilename (),
				defaultImports, null, shell);
			setRun (COMPILATION);
			o = compile (classPath, parser.getAST (), parser.getFilename (),
				defaultImports, null, shell);
		}
		catch (antlr.RecognitionException e)
		{
			if (e instanceof RecognitionException)
			{
				throw (RecognitionException) e;
			}
			throw new WrapException (e);
		}
		problems.addAll (parser.getExceptionList ());
		problems.check ();
		return o;
	}

	protected abstract CompilationUnitScope compile (ClassPath classPath, AST tree,
			String source, Scope defaultImports, Annotation[] annotations, CClass shell)
			throws antlr.RecognitionException;

	public CompilationUnitScope[] compile (CompilationUnit[] units, CClass shell,
										   BytecodeWriter writer,
										   FileSystem fs, Object dir,
										   boolean keepExpressions)
		throws IOException
	{
		CompilationUnitScope[] result = new CompilationUnitScope[units.length];
		try
		{
			for (int r = TYPE_AND_FIELD_DECLARATION; r <= COMPILATION; r++)
			{
				setRun (r);
				for (int i = 0; i < units.length; i++)
				{
					options = units[i].options;
					result[i] = compile (units[i].classPath, units[i].tree,
						units[i].source,
						units[i].defaultImports, units[i].annotations, shell);
					clearTemporaries ();
					if (r == COMPILATION)
					{
						if ((writer != null) && !problems.containsErrors ())
						{
							writer.write (result[i], fs, dir);
						}
						if (!keepExpressions)
						{
							result[i].dispose ();
						}
					}
				}
				if (out != null)
				{
					Utils.printTime ("Run " + r + " completed.");
				}
			}
		}
		catch (antlr.RecognitionException e)
		{
			throw new WrapException (e);
		}
		return result;
	}

	
	void clearTemporaries ()
	{
		argList = null;
		constrImplicit.enclosing = null;
		currentCompilationUnitScope = null;
		currentPackage = null;
		currentTypeScope = null;
		implCvScope = null;
		implCvSrc = null;
		invokable = null;
		members.reset ();
		resolver.reset ();
		resolutionArgs = null;
	}

	/**
	 * Returns true if the specified version number designates a version
	 * of the JVM that is compatible with the target version currently
	 * set. Valid values for the version number are the constants
	 * in org.objectweb.asm.Opcodes.V*
	 * For instance a call supportsVersion(Opcodes.V1_4) with Opcodes.V1_4
	 * currently set as target would return true. The same call with
	 * Opcodes.V1_3 set as target would return false.
	 * @param ver
	 * @return
	 */
	public boolean supportsVersion (int ver)
	{
		return options.supportsVersion (ver);
	}

	/**
	 * Package of current compilation unit.
	 */
	Package currentPackage;

	/**
	 * Returns the package of the current compilation unit.
	 * 
	 * @return current package
	 */
	public Package getPackage ()
	{
		return currentPackage;
	}

	/**
	 * Stores the current CompilationUnitScope, if one was set.
	 * This field is set during parsing, so see Compiler.tree.g.
	 */
	public CompilationUnitScope currentCompilationUnitScope;

	/**
	 * Return the current value of the <code>CompilationUnitScope</code>.
	 * @return current <code>CompilationUnitScope</code>
	 */
	public CompilationUnitScope getCompilationUnitScope ()
	{
		return currentCompilationUnitScope;
	}

	/**
	 * Stores the current <code>TypeScope</code>, if one was set.
	 * This field is set during parsing, so see Compiler.tree.g.
	 */
	TypeScope currentTypeScope;

	/**
	 * Return the current value of the <code>TypeScope</code>.
	 * @return current <code>TypeScope</code>
	 */
	public TypeScope getTypeScope ()
	{
		return currentTypeScope;
	}

	public CompilerOptions options = new CompilerOptions ();

	public static final int EXPR_FINAL = 1;
	public static final int EXPR_THIS = 2;

	static final int ABSTRACT_METHOD_INCOMPATIBLE = Member.PRIVATE
		| Member.STATIC | Member.FINAL | Member.NATIVE | Member.STRICT
		| Member.SYNCHRONIZED;

	public static final long MOD_ITERATING = 1L << 32;
	public static final long MOD_PARAMETER = MOD_ITERATING << 1;
	public static final long MOD_ROUTINE = MOD_PARAMETER << 1;
	public static final long MOD_CONSTRUCTOR = MOD_ROUTINE << 1;
	public static final long MOD_THIS_PARAMETER = MOD_CONSTRUCTOR << 1;
	public static final long MOD_INITIALIZER = MOD_THIS_PARAMETER << 1;
	public static final long MOD_CONST = MOD_INITIALIZER << 1;
	public static final long MOD_INSTANTIATOR = MOD_CONST << 1;
	public static final long MOD_MODULE = MOD_INSTANTIATOR << 1;
	public static final long MOD_IMPLICIT_ARGUMENT = MOD_MODULE << 1;
	public static final long MOD_STATIC_MEMBER_CLASSES = MOD_IMPLICIT_ARGUMENT << 1;

	public static final long ROUTINE_MODIFIERS = Member.STATIC | Member.FINAL
		| Member.SYNTHETIC | MOD_ROUTINE;

	static final int TOP_LEVEL_MODULE_MODIFIERS = Member.PUBLIC | Member.FINAL | Member.ABSTRACT | Member.STRICT;
	static final int MEMBER_MODULE_MODIFIERS = TOP_LEVEL_MODULE_MODIFIERS | Member.ACCESS_MODIFIERS | Member.STATIC;

	private static final LongHashMap extModifiers;

	static
	{
		extModifiers = new LongHashMap ();
		extModifiers.put (MOD_ITERATING, "iterating");
		extModifiers.put (MOD_PARAMETER, "parameter");
		extModifiers.put (MOD_ROUTINE, "routine");
		extModifiers.put (MOD_CONST, "const");
		extModifiers.put (MOD_INSTANTIATOR, "instantiator");
		extModifiers.put (MOD_MODULE, "module");
	}

	static final ArgumentTransformations METHOD_ARGS = new ArgumentTransformations (
		new Type[][] {ArgumentTransformations.NO_IMPLICIT,
				ArgumentTransformations.GENERATOR,
				ArgumentTransformations.AGGREGATE,
				ArgumentTransformations.FILTER}, null);

	public static final String modifiersToString (long mods)
	{
		StringBuffer b = new StringBuffer (Reflection
			.modifiersToString ((int) mods));
		for (long mask = 1L << 32; mask != 0; mask <<= 1)
		{
			if ((mods & mask) != 0)
			{
				Object s = extModifiers.get (mask, null);
				if (s != null)
				{
					if (b.length () > 0)
					{
						b.insert (0, ' ');
					}
					b.insert (0, s);
				}
			}
		}
		return b.toString ();
	}

	static Type getClasspathClassFor (Type type)
	{
		if (type == null)
		{
			return null;
		}
		type = TypeDecorator.undecorate (Reflection.getBinaryType (type));
		return ((type instanceof ClassAdapter) || (type instanceof ASMType)) ? type
				: null;
	}

	boolean isClassReady (Type type)
	{
		return (run == COMPILATION) || (getClasspathClassFor (type) != null);
	}

	void checkClassReady (Type type)
	{
		if (!Reflection.isInvalid (type) && !isClassReady (type))
		{
			throw ClassNotReadyException.INSTANCE;
		}
	}

	static class ExprInfo
	{
		AST ast;
		BlockScope scope;

		ExprInfo dup ()
		{
			ExprInfo i = new ExprInfo ();
			i.ast = ast;
			i.scope = scope;
			return i;
		}
	}

	public static void copyInfo (Expression src, Expression dest)
	{
		if ((src != dest) && (src.aval != null))
		{
			dest.aval = (src.aval instanceof ExprInfo) ? ((ExprInfo) src.aval)
				.dup () : src.aval;
		}
	}

	private static ExprInfo setInfo (Expression e)
	{
		if (e.aval instanceof ExprInfo)
		{
			return (ExprInfo) e.aval;
		}
		else
		{
			ExprInfo i = new ExprInfo ();
			if (e.aval instanceof BlockScope)
			{
				i.scope = (BlockScope) e.aval;
			}
			else
			{
				i.ast = (AST) e.aval;
			}
			e.aval = i;
			return i;
		}
	}

	public static void setBlockScope (Expression e, BlockScope s)
	{
		if ((e.aval == null) || (e.aval instanceof BlockScope))
		{
			e.aval = s;
		}
		else
		{
			setInfo (e).scope = s;
		}
	}

	public static BlockScope getBlockScope (Expression e)
	{
		while (true)
		{
			if (e.aval instanceof BlockScope)
			{
				return (BlockScope) e.aval;
			}
			else if ((e.aval instanceof ExprInfo)
				&& (((ExprInfo) e.aval).scope != null))
			{
				return ((ExprInfo) e.aval).scope;
			}
			e = (Expression) e.getAxisParent ();
		}
	}

	public static Expression setAST (Expression e, AST ast)
	{
		if (e == null)
		{
			return null;
		}
		if ((e.aval == null) || (e.aval instanceof AST))
		{
			e.aval = ast;
		}
		else
		{
			setInfo (e).ast = ast;
		}
		return e;
	}

	public static AST getAST (Expression e)
	{
		while (true)
		{
			if (e == null)
			{
				return null;
			}
			if (e.aval instanceof AST)
			{
				return (AST) e.aval;
			}
			else if ((e.aval instanceof ExprInfo)
				&& (((ExprInfo) e.aval).ast != null))
			{
				return ((ExprInfo) e.aval).ast;
			}
			e = (Expression) e.getAxisParent ();
		}
	}

	CClass shell;
	Resolver resolver = new Resolver ((Compiler) this);

	HashMap<Field,FieldInitializer> initializersToCompile = new HashMap<Field,FieldInitializer> ();
	
	HashMap<AST,CompilationUnitScope> units = new HashMap<AST, CompilationUnitScope> ();

	ObjectList toResolveBeforeMethodDeclaration = new ObjectList ();
	ObjectList toResolveBeforeCompilation = new ObjectList ();

	HashSet<String> accessedShellFields = new HashSet<String> ();

	Type legalSupertype;

	PrintWriter out;

	CompilerBase ()
	{
		super ();
	}

	public void setLegalSupertype (Type lst)
	{
		legalSupertype = lst;
	}

	public void setVerbose (PrintWriter out)
	{
		this.out = out;
	}

	void fieldAccessed (Field field)
	{
		if ((shell != null) && (field.getDeclaringType () == shell))
		{
			accessedShellFields.add (field.getDescriptor ());
		}
	}

	public ProblemReporter problems = new ProblemReporter (-1L, 0);

	public static final long WARN_ON_IMPLICIT_MODIFIER = de.grogra.grammar.RecognitionException.MIN_UNUSED;

	private static void resolve (ObjectList list)
	{
		for (int i = 0; i < list.size (); i++)
		{
			((Resolvable) list.get (i)).resolve ();
		}
		list.clear ();
	}

	static String qualifiedName (AST id)
	{
		StringBuffer b = new StringBuffer ();
		while (id.getType () == DOT)
		{
			id = id.getFirstChild ();
			b.insert (0, id.getNextSibling ().getText ()).insert (0, '.');
		}
		return b.insert (0, id.getText ()).toString ();
	}

	void process (TypeScope scope, boolean post)
	{
		if (run == TYPE_AND_FIELD_DECLARATION)
		{
			return;
		}
		Extension e = scope.getExtension ();
		if (e != null)
		{
			if (post)
			{
				e.postprocess (scope, run);
			}
			else
			{
				e.preprocess (scope, run);
			}
		}
	}

	static AST simpleNameAST (AST id)
	{
		return (id.getType () == DOT) ? id.getFirstChild ().getNextSibling ()
				: id;
	}

	Local declareLocal (BlockScope s, AST name, int modifiers, Type type)
	{
		return declareLocal (s, name.getText (), name, modifiers, type);
	}

	Local declareLocal (BlockScope s, String name, AST pos, int modifiers,
			Type type)
	{
		return s.declareLocal (verifyNotDeclared (name, s, pos), modifiers,
			type, pos);
	}

	public String verifyNotDeclared (AST name, BlockScope s)
	{
		return verifyNotDeclared (name.getText (), s, name);
	}

	public String verifyNotDeclared (String name, BlockScope s, AST pos)
	{
		if (s.findLocal (name, false) != null)
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.DUPLICATE_LOCAL, name), pos);
			return s.getUniqueName (name);
		}
		return name;
	}

	public void intersect (IntersectionType intersection, Type newType, AST pos)
	{
		if (!Reflection.isCastableFrom (newType, intersection))
		{
			if (Reflection.isInvalid (newType))
			{
				return;
			}
			problems.addSemanticError (I18N.msg (
				ProblemReporter.EMPTY_TYPE_INTERSECTION, intersection
					.getName (), newType.getName ()), pos);
			return;
		}
		intersection.intersect (newType);
	}

	void addExpression (PatternBuilder pb, Expression e, AST label, AST pos)
	{
		if (e == null)
		{
			return;
		}
		pb.addExpression (label, e, pos);
	}

	void addMethodPattern (PatternBuilder pb, AST label, AST id,
			Expression[] args, AST pos)
	{
		methodPatternTermType = null;
		Expression e = compileMethodInvocation (id, methodPatternArgs, args,
			0, pb.getScope (), pos);
		if (methodPatternTermType == null)
		{
			addExpression (pb, e, label, pos);
		}
		else if (e != null)
		{
			pb.addGuard (label, e, pos);
		}
	}

	void addMethodPattern (PatternBuilder pb, AST label, Expression expr,
			String name, Expression[] args, AST pos)
	{
		methodPatternTermType = null;
		Expression e = compileMethodInvocation (expr, name,
			methodPatternArgs, args, 0, pb.getScope (), pos);
		if (methodPatternTermType == null)
		{
			addExpression (pb, e, label, pos);
		}
		else if (e != null)
		{
			pb.addGuard (label, e, pos);
		}
	}

	public Type getWrapperTypeFor (Type type, CompiletimeModel model, AST pos)
	{
		Type w = model.getWrapperTypeFor (type);
		if (w == null)
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.CANNOT_WRAP_TYPE, type.getName ()), pos);
			return Type.INVALID;
		}
		return w;
	}

	static final class ClassInfo
	{
		AST node;

		TypeScope scope;
		MethodScope defaultConstructorScope;
		MethodScope moduleConstructorScope;
		MethodScope instMethodScope;
		Local instState;
		MethodScope instIfaceMethodScope;
		Local instIfaceState;

		TypeScope predicateScope;
		MethodScope predicateConstructorScope;
		MethodScope signatureScope;
		Field[] moduleFields;
		Method[] parameterGetters;
		MethodScope[] getterMethods;
		Switch[] switches;
		Local[] objects;
	}

	private Type methodPatternTermType;

	private final ArgumentTransformations methodPatternArgs = new ArgumentTransformations (
		new Type[][] {ArgumentTransformations.NO_IMPLICIT,
				ArgumentTransformations.GENERATOR,
				ArgumentTransformations.AGGREGATE,
				ArgumentTransformations.FILTER,
				ArgumentTransformations.FIRST_TERM}, null)
	{

		@Override
		int getApplicableOption (Signature m, Applicability out,
				Expression[] args, Scope scope)
		{
			int opt = super.getApplicableOption (m, out, args, scope);
			if (opt < 0)
			{
				return -1;
			}
			if ((types[opt] == ArgumentTransformations.FIRST_TERM)
				&& (((Method) m).getReturnType ().getTypeId () != TypeId.BOOLEAN))
			{
				return -1;
			}
			return opt;
		}

		@Override
		Expression createImplicitArgument (int option, int index, Type type, Scope scope, AST pos)
		{
			if ((types[option] == ArgumentTransformations.FIRST_TERM)
				&& (index == 0))
			{
				methodPatternTermType = type;
				return PatternBuilder.createArgument (type);
			}
			else
			{
				return super.createImplicitArgument (option, index, type, scope, pos);
			}
		}
	};

	final ProduceImplicit produceArgs = new ProduceImplicit ();

	static final class ProduceImplicit extends ArgumentTransformations
	{
		private static final Type EDGE_DIR = ClassAdapter.wrap (EdgeDirection.class);

		boolean nodeUsed;

		private AST edge;
		private Expression node;
		private ExpressionFactory implExpr;
		private Type[][] origTypes;

		private static final Type[][][] typesArray = {
				{Type.TYPE_0, {Type.INVALID}, AGGREGATE, {AGGREGATE_TYPE, Type.INVALID}},
				{Type.TYPE_0, {Type.INVALID}, {EDGE_DIR}, {EDGE_DIR, Type.INVALID},
				 {null}, {null, Type.INVALID}, {null, EDGE_DIR}, {null, EDGE_DIR, Type.INVALID}}};

		ProduceImplicit ()
		{
			super (null, null);
		}

		void setType (AST edge, Expression node, ExpressionFactory implExpr)
		{
			origTypes = typesArray[(edge != null) ? 1 : 0];
			types = origTypes.clone ();
			this.implExpr = implExpr;
			this.edge = edge;
			this.node = node;
			nodeUsed = false;
			for (int i = types.length - 1; i >= 0; i--)
			{
				Type[] a = types[i].clone ();
				types[i] = a;
				for (int j = 0; j < a.length; j++)
				{
					Type t = a[j];
					if (t == null)
					{
						a[j] = node.getType ();
					}
					else if ((t != AGGREGATE_TYPE) && (t != EDGE_DIR))
					{
						a[j] = implExpr.getType ();
					}
				}
			}
		}

		@Override
		boolean matchesImplicit (Type param, int option, int implIndex,
				Type implType)
		{
			return Reflection.isSupertypeOrSame (param, implType);
		}

		@Override
		Expression createImplicitArgument (int option, int index, Type type, Scope scope, AST pos)
		{
			Type t = origTypes[option][index];
			if (t == EDGE_DIR)
			{
				return new GetField (Reflection.getDeclaredField (EDGE_DIR, edgeDirection (edge).toString ()));
			}
			else if (t == null)
			{
				nodeUsed = true;
				return node;
			}
			else if (t == AGGREGATE_TYPE)
			{
				return super.createImplicitArgument (option, index, type, scope, pos);
			}				 
			else
			{
				return implExpr.createExpression (scope, pos);
			}
		}
	}

	Expression compileProduceName (AST name, BlockScope scope, AST pos)
	{
		Object o = resolver.resolveExpressionOrMethodOrTypeName (name, scope);
		if (o instanceof Method)
		{
			return compileMethodInvocation
				(name, produceArgs, Expression.EXPR_0, 0, scope, pos);
		}
		else if (o instanceof Expression)
		{
			return (Expression) o;
		}
		else if ((o instanceof Type) && !Reflection.isInvalid ((Type) o))
		{
			return compileConstructorInvocation
				(new New ((Type) o), null, Expression.EXPR_0, scope, pos, false);
		}
		else
		{
			return null;
		}
	}

	Expression compileProduceInvocation (AST qname, Expression expr, AST sname, Expression[] args, BlockScope scope, AST pos)
	{
		if (qname != null)
		{
			Member m = resolver.resolveMethodOrTypeName (qname, scope);
			if (m instanceof Method)
			{
				return compileMethodInvocation
					(qname, produceArgs, args, 0, scope, pos);
			}
			else if (m instanceof Type)
			{
				return compileConstructorInvocation
					(new New ((Type) m), null, args, scope, pos, false);
			}
		}
		else if (expr != null)
		{
			return compileMethodInvocation
				(expr, sname.getText (), produceArgs, args, 0, scope, pos);
		}
		return null;
	}

	public Expression castingConversion (Expression expr, Type type, Scope scope, AST pos)
	{
		return castingConversion (expr, type, scope, pos, ProblemReporter.ILLEGAL_CAST, false);
	}


	private Expression wideningConversion (Expression expr, Type type, Scope scope, AST pos,
			String msg)
	{
		if (expr == null)
		{
			return new Expression (type);
		}
		if (Reflection.equal (expr.getType (), type))
		{
			return expr;
		}
		if (Reflection.isWideningConversion (expr.getType (), type))
		{
			return expr.cast (type);
		}
		if (msg == null)
		{
			return null;
		}
		problems.addSemanticError (I18N.msg (msg, expr.getType (), type), pos);
		return new Expression (type);
	}

	public Expression wideningConversion (Expression expr, Type type, Scope scope, AST pos)
	{
		return wideningConversion (expr, type, scope, pos,
			ProblemReporter.UNEXPECTED_TYPE);
	}

	public Expression methodInvocationConversion (Expression expr, Type type,
			Scope scope, AST pos)
	{
		if (expr == null)
		{
			return new Expression (type);
		}
		if (expr instanceof OpenArgument)
		{
			return expr.setType (type);
		}
		if ((expr.etype == TypeId.OBJECT)
			&& (type.getTypeId () == TypeId.OBJECT)
			&& Reflection.isAssignableFrom (type, expr.getType ()))
		{
			return expr;
		}

		return implicitConversion (expr, type, true, scope, pos, ProblemReporter.UNEXPECTED_TYPE, false);
	}

	Expression assignmentConversion (Expression expr, Type type, Scope scope, AST pos,
			String msg)
	{
		if (checkInvalid (expr, type))
		{
			return new Expression (type);
		}
		checkConstant: if (expr instanceof Constant)
		{
			int v;
			switch (expr.etype)
			{
				case TypeId.BYTE:
					v = expr.evaluateByte (null);
					break;
				case TypeId.SHORT:
					v = expr.evaluateShort (null);
					break;
				case TypeId.CHAR:
					v = expr.evaluateChar (null);
					break;
				case TypeId.INT:
					v = expr.evaluateInt (null);
					break;
				default:
					break checkConstant;
			}
			switch ((Reflection.isPrimitive (type) ? type : Reflection
				.getUnwrappedType (type)).getTypeId ())
			{
				case TypeId.BYTE:
					if ((Byte.MIN_VALUE <= v) && (v <= Byte.MAX_VALUE))
					{
						expr = new ByteConst ((byte) v);
					}
					break;
				case TypeId.SHORT:
					if ((Short.MIN_VALUE <= v) && (v <= Short.MAX_VALUE))
					{
						expr = new ShortConst ((short) v);
					}
					break;
				case TypeId.CHAR:
					if ((Character.MIN_VALUE <= v)
						&& (v <= Character.MAX_VALUE))
					{
						expr = new CharConst ((char) v);
					}
					break;
			}
		}
		return implicitConversion (expr, type, true, scope, pos, msg, false);
	}

	public Expression assignmentConversion (Expression expr, Type type, Scope scope, AST pos)
	{
		return assignmentConversion (expr, type, scope, pos,
			ProblemReporter.ILLEGAL_ASSIGNMENT_CONVERSION);
	}

	public Expression returnConversion (Expression expr, Type type, Scope scope, AST pos)
	{
		return assignmentConversion (expr, type, scope, pos,
			ProblemReporter.ILLEGAL_RETURN_CONVERSION);
	}

	private Expression boxingExpression (Type wrapperType)
	{
		Expression is;
		
		Type wrapped = Reflection.getUnwrappedType (wrapperType);

		// check if target VM is 1.5 compatible
		// then java.lang.Integer contains a static method valueOf(int)
		if (supportsVersion (Opcodes.V1_5))
		{
			// find a method valueOf to convert from int to Integer
			// this method is available for 
			is = new InvokeStatic (Reflection.getDeclaredMethod (
				wrapperType, "mvalueOf;("
					+ wrapped.getDescriptor () + ")"
					+ wrapperType.getDescriptor ()));
		}
		// otherwise use old-style way of creating an instance
		else
		{
			// call ctor of java.lang.Integer
			is = new InvokeSpecial (Reflection.getDeclaredMethod (
				wrapperType, "m<init>;("
					+ wrapped.getDescriptor () + ")V"));
			is.add (new New (wrapperType));
		}
		return is;
	}

	public Type typeForType (Type type)
	{
		try
		{
			return ClassPath.get (currentCompilationUnitScope).typeForType (type);
		}
		catch (ClassNotFoundException e)
		{
			return type;
		}
	}

	public <T extends java.lang.annotation.Annotation> Annotation<T> getAnnotation (Member member, Class<T> cls, boolean inherited)
	{
	replace:
		if (member instanceof ClassAdapter)
		{
			member = typeForType ((Type) member);
		}
		else if (member.getDeclaringType () instanceof ClassAdapter)
		{
			Type d = typeForType (member.getDeclaringType ());
			String descr = member.getDescriptor ();
			Member m;
			switch (descr.charAt (0))
			{
				case 'f':
					m = Reflection.getDeclaredField (d, descr);
					break;
				case 'm':
					m = Reflection.getDeclaredMethod (d, descr);
					break;
				default:
					break replace;
			}
			if (m != null)
			{
				member = m;
			}
		}
		while (member != null)
		{
			Annotation<T> a = Reflection.getDeclaredAnnotation (member, cls);
			if ((a != null) || !inherited || Reflection.isInterface ((Type) member))
			{
				return a;
			}
			member = ((Type) member).getSupertype ();
		}
		return null;
	}

	static final int CV_TYPE_NONE = 0;
	static final int CV_TYPE_USER_DEFINED = 1;
	static final int CV_TYPE_D2F = 2;
	static final int CV_TYPE_STANDARD = 3;

	private int implCvType;
	private Scope implCvScope;
	private Expression implCvSrc;
	private Type implCvDestType;
	private ObjectList<Method> implCvMethods = new ObjectList<Method> ();

	private static Type getConversionType (Method m, boolean target)
	{
		if (target)
		{
			return Reflection.isCtor (m) ? m.getDeclaringType () : m.getReturnType ();
		}
		else
		{
			return (m.getParameterCount () == 1) ? m.getParameterType (0) : m.getDeclaringType ();
		}
	}

	private void checkCvCandidate (Method m)
	{
		Type s = getConversionType (m, false);
		if ((standardImplicitConversion (implCvSrc, s, false, implCvScope, null, null, true) != null)
			&& (standardImplicitConversion (new Expression (getConversionType (m, true)), implCvDestType, false, implCvScope, null, null, true) != null)
			// check if the declaring type of the method is accessible
			// from the current context
			&& Reflection.isAccessible (m.getDeclaringType (), null, implCvScope.getDeclaredType ())
			// check if the method is accessible from the current context
			&& Reflection.isAccessible (m, s, implCvScope.getDeclaredType ()))
		{
			for (int i = implCvMethods.size () - 1; i >= 0; i--)
			{
				Method m2 = implCvMethods.get (i);
				int t = lessThanIfEqualSig (m, m2);
				if (t > 0)
				{
					return;
				}
				else if ((t < 0) && (lessThanIfEqualSig (m2, m) > 0))
				{
					implCvMethods.remove (i);
				}
			}
			implCvMethods.add (m);
		}
	}

	private Type findMostSpecificType (ObjectList<Method> list, boolean target)
	{
		Type x = getConversionType (list.get (0), target);
		for (int i = list.size () - 1; i >= 1; i--)
		{
			Type y = getConversionType (list.get (i), target);
			if (standardImplicitConversion (new Expression (target ? x : y), target ? y : x, false, implCvScope, null, null, true) != null)
			{
				x = y;
			}
		}
		for (int i = list.size () - 1; i >= 0; i--)
		{
			Type y = getConversionType (list.get (i), target);
			if (standardImplicitConversion (new Expression (target ? y : x), target ? x : y, false, implCvScope, null, null, true) == null)
			{
				return null;
			}
		}
		return x;
	}

	/**
	 * Perform autoconversion of the expression to target type.
	 * Autoconversion includes widening conversions, boxing and unboxing,
	 * and allows to transform an object of type A into
	 * an object of type B by means of conversion functions. Every
	 * function declared as 'static B valueOf(A)', 'static B toB(A)', 'B A.toB()'
	 * and constructors of the form 'B(A)' are considered as
	 * conversion functions.
	 * If more than one conversion from type A to type B using those
	 * conversion functions is possible, the conversion is
	 * ambiguous and results in an error.
	 *  
	 * @param expr source expression
	 * @param type target type
	 * @param boxOnly only check for autoboxing/unboxing, no further autoconversions
	 * @param scope current scope
	 * @param pos location to use for error reports
	 * @param msg message key to use for error reports
	 * 
	 * @return auto-converted expression, or <code>null</code> if no such
	 * conversion is possible and <code>msg</code> is <code>null</code>
	 */
	public Expression implicitConversion (Expression expr, Type type, boolean allowD2F, Scope scope, AST pos, String msg, boolean test)
	{
		Expression std = standardImplicitConversion (expr, type, allowD2F, scope, pos, null, test);
		if (std != null)
		{
			return std;
		}

		implCvScope = scope;
		implCvSrc = expr;
		implCvDestType = type;
		implCvMethods.clear ();

		for (int i = type.getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			Method m = type.getDeclaredMethod (i);
			if (m.getParameterCount () == 1)
			{
				if (Reflection.isStatic (m))
				{
					if (m.getSimpleName ().equals ("valueOf")
						&& scope.isEnabledConversion (ConversionType.VALUE_OF))
					{
						checkCvCandidate (m);
					}
				}
				else if (Reflection.isCtor (m) && !Reflection.isInner (type)
						 && !Reflection.isAbstract (type))
				{
					if (scope.isEnabledConversion (ConversionType.CONSTRUCTOR)
						|| (scope.isEnabledConversion (ConversionType.CONVERSION_CONSTRUCTOR)
							&& (getAnnotation (m, ConversionConstructor.class, false) != null)))
					{
						checkCvCandidate (m);
					}
				}
			}
		}

		addConversionMethods (expr.getType ());
		
		if (scope.isEnabledConversion (ConversionType.TO_TYPE_IN_SCOPE))
		{
			String methodName = getToTypeMethodName (type);
			for (Scope currentScope = scope; currentScope != null; currentScope = currentScope.getEnclosingScope ())
			{
				Type importedType;
				// check if this scope is of type SingleStatic import
				if (currentScope instanceof SingleStaticImport)
				{
					// obtain imported type and members
					SingleStaticImport ssi = (SingleStaticImport) currentScope;
					if (!methodName.equals (ssi.getImportedMembers ()))
					{
						continue;
					}
					importedType = ssi.getImportedType ();
				}
				else if (currentScope instanceof StaticImportOnDemand)
				{
					// obtain imported type and members
					importedType = ((StaticImportOnDemand) currentScope).getImportedType ();
				}
				else if (currentScope instanceof ProduceScope)
				{
					importedType = ((ProduceScope) currentScope).getType ();
				}
				else
				{
					continue;
				}
				while (importedType != null)
				{
					// check every imported function of the type
					for (int i = 0; i < importedType.getDeclaredMethodCount (); i++)
					{
						// obtain the i-th method declared by the imported type
						Method m = importedType.getDeclaredMethod (i);

						// if a conversion function was found
						// and its name matches the imported name, insert it into the
						// conversion graph
						if (m.getSimpleName ().equals (methodName)
							&& Reflection.isStatic (m)
							&& (m.getParameterCount () == 1))
						{
							checkCvCandidate (m);
						}
					}
					importedType = importedType.getSupertype ();
				}
			}
		}
		
		if (implCvMethods.size () > 1)
		{
			Type sx = findMostSpecificType (implCvMethods, false);
			Type tx = findMostSpecificType (implCvMethods, true);
		findMethod:
			if ((sx != null) && (tx != null))
			{
				Method cv = null; 
				for (int i = implCvMethods.size () - 1; i >= 0; i--)
				{
					Method m = implCvMethods.get (i);
					if (Reflection.equal (getConversionType (m, false), sx)
						&& Reflection.equal (getConversionType (m, true), tx))
					{
						if (cv != null)
						{
							break findMethod;
						}
						cv = m;
					}
				}
				if (cv != null)
				{
					implCvMethods.clear ();
					implCvMethods.add (cv);
				}
			}
		}

		if (implCvMethods.size () == 1)
		{
			implCvType = CV_TYPE_USER_DEFINED;
			if (test)
			{
				return OK;
			}
			Method cv = implCvMethods.get (0); 
			expr = standardImplicitConversion (expr, getConversionType (cv, false), true, scope, pos, msg, false);
			if (Reflection.isCtor (cv))
			{
				expr = compileConstructorInvocation (cv, new Expression[] {expr}, scope, pos);
			}
			else if (Reflection.isStatic (cv))
			{
				expr = compileMethodInvocation (cv, null,
					ArgumentTransformations.NO_IMPLICIT_ARGS,
					Applicability.DEFAULT, new Expression[] {expr},
					scope, pos);
			}
			else
			{
				expr = compileMethodInvocation (cv, expr,
					ArgumentTransformations.NO_IMPLICIT_ARGS,
					Applicability.DEFAULT, new Expression[0], scope,
					pos);
			}
			expr = standardImplicitConversion (expr, type, true, scope, pos, msg, false);
			implCvType = CV_TYPE_USER_DEFINED;
			return expr;
		}

		implCvType = CV_TYPE_NONE;
		
		if (msg == null)
		{
			return null;
		}

		if (implCvMethods.isEmpty ())
		{
			problems.addSemanticError (I18N.msg (msg, expr.getType (), type), pos);
		}
		else
		{
			String[] m = new String[implCvMethods.size ()];
			for (int i = 0; i < m.length; i++)
			{
				m[i] = Reflection.getDescription (implCvMethods.get (i));
			}
			problems.addSemanticError (I18N.msg (ProblemReporter.AMBIGUOUS_CONVERSIONS, ProblemReporter.enumerate (m)), pos);
		}
		return new Expression (type);
	}

	private static String getToTypeMethodName (Type dest)
	{
		if (Reflection.isPrimitive (dest))
		{
			return dest.getSimpleName () + "Value";
		}
		else if (Reflection.isArray (dest))
		{
			StringBuilder b = new StringBuilder ();
			while (Reflection.isArray (dest))
			{
				b.append ("Array");
				dest = dest.getComponentType ();
			}
			b.insert (0, dest.getSimpleName ());
			if (Reflection.isPrimitive (dest))
			{
				b.setCharAt (0, Character.toUpperCase (b.charAt (0)));
			}
			return b.insert (0, "to").toString ();
		}
		else
		{
			return "to" + dest.getSimpleName ();
		}
	}

	private void addConversionMethods (Type src)
	{
		while (src != null)
		{
			for (int i = src.getDeclaredMethodCount () - 1; i >= 0; i--)
			{
				Method m = src.getDeclaredMethod (i);
				if ((m.getParameterCount () == 0) && !Reflection.isStatic (m))
				{
					String s = m.getSimpleName ();
					Type t = m.getReturnType ();
					if ((Reflection.isArray (t) && s.startsWith ("to") && s.endsWith ("Array"))
						|| s.equals (getToTypeMethodName (t)))
					{
						checkCvCandidate (m);
					}
				}
			}
			for (int i = src.getDeclaredInterfaceCount () - 1; i >= 0; i--)
			{
				addConversionMethods (src.getDeclaredInterface (i));
			}
			src = src.getSupertype ();
		}
	}

	private static boolean checkInvalid (Expression e, Type t)
	{
		return (e == null) || Reflection.isInvalid (t) || Reflection.isInvalid (e.getType ());
	}

	
	private static final Expression OK = new Expression (Type.INVALID);

	public Expression castingConversion (Expression expr, Type type, Scope scope, AST pos, String msg, boolean test)
	{
		if (checkInvalid (expr, type))
		{
			return new Expression (type);
		}
		if (Reflection.equal (expr.getType (), type))
		{
			return expr;
		}

		// check for conversions:
		// - widening primitive conversion
		// - narrowing primitve conversion
		// - widening reference conversion
		// - narrowing reference conversion
		int t = type.getTypeId ();
		boolean ok = false;
		switch (t)
		{
			case TypeId.OBJECT:
				ok = (expr.etype == TypeId.OBJECT)
					&& Reflection.isCastableFrom (type, expr.getType ());
				break;
			case TypeId.BOOLEAN:
				break;
			case TypeId.VOID:
				ok = true;
				break;
			default:
				ok = (((1 << expr.etype) & TypeId.NUMERIC_MASK) != 0)
					&& (((1 << t) & TypeId.NUMERIC_MASK) != 0);
				break;
		}
		Expression result = null;
		if (ok)
		{
			if (test)
			{
				return OK;
			}
			result = new Cast (type).add (expr);
		}
		else
		{
			// was none of the above conversions
			// try to perform boxing/unboxing conversions
			
			if (t == TypeId.OBJECT)
			{
				if (Reflection.isPrimitive (expr.getType ())
					&& Reflection.equal (Reflection.getWrapperClass (expr.etype), type))
				{
					if (test)
					{
						return OK;
					}
					result = boxingExpression (type).add (expr);
				}
			}
			else if (Reflection.equal (Reflection.getUnwrappedType (expr.getType ()), type))
			{
				if (test)
				{
					return OK;
				}
				result = expr.unboxingConversion ();
			}
			if (result == null)
			{
				result = implicitConversion (expr, type, false, scope, pos, msg, test);
			}
		}
		result = result.toConst ();
		Compiler.copyInfo (expr, result);
		return result;
	}

	static int stdCvType (Type src, Type dest, boolean allowD2F, Scope scope)
	{
		if (Reflection.isWideningConversion (src, dest))
		{
			return CV_TYPE_STANDARD;
		}
		if (allowD2F && (src.getTypeId () == TypeId.DOUBLE)
			&& (dest.getTypeId () == TypeId.FLOAT)
			&& scope.isD2FImplicit ())
		{
			return CV_TYPE_D2F;
		}
		return CV_TYPE_NONE;
	}

	/**
	 * Perform autoconversion of the expression to target type.
	 * Autoconversion includes widening conversions, boxing and unboxing,
	 * and allows to transform an object of type A into
	 * an object of type B by means of conversion functions. Every
	 * function declared as 'static B valueOf(A)', 'static B toB(A)', 'B A.toB()'
	 * and constructors of the form 'B(A)' are considered as
	 * conversion functions.
	 * If more than one conversion from type A to type B using those
	 * conversion functions is possible, the conversion is
	 * ambiguous and results in an error.
	 *  
	 * @param expr source expression
	 * @param type target type
	 * @param boxOnly only check for autoboxing/unboxing, no further autoconversions
	 * @param scope current scope
	 * @param pos location to use for error reports
	 * @param msg message key to use for error reports
	 * 
	 * @return auto-converted expression, or <code>null</code> if no such
	 * conversion is possible and <code>msg</code> is <code>null</code>
	 */
	public Expression standardImplicitConversion (Expression expr, Type type, boolean allowD2F, Scope scope, AST pos, String msg, boolean test)
	{
		implCvType = CV_TYPE_NONE;
		if (checkInvalid (expr, type))
		{
			implCvType = CV_TYPE_STANDARD;
			return new Expression (type);
		}
		if (Reflection.equal (expr.getType (), type))
		{
			implCvType = CV_TYPE_STANDARD;
			return expr;
		}
		int ct = stdCvType (expr.getType (), type, allowD2F, scope);
		if (ct != CV_TYPE_NONE)
		{
			implCvType = ct;
			if (test)
			{
				return OK;
			}
			return expr.cast (type);
		}

		// check if autoboxing is possible
		// this is when the expression type is a primitive one
		if (Reflection.isPrimitive (expr.getType ()))
		{
			// obtain the wrapped type for the expression type
			Type wrappedExpr = ClassAdapter.wrap (Reflection
				.getWrapperClass (expr.getType ().getTypeId ()));

			// autoboxing of actual followed by widening conversion
			// yields param ?
			if (Reflection.isWideningConversion (wrappedExpr, type))
			{
				implCvType = CV_TYPE_STANDARD;
				if (test)
				{
					return OK;
				}
				Expression is = boxingExpression (wrappedExpr);
				is.add (expr);
				return is.cast (type);
			}
		}
		else
		{
			Type w = Reflection.getUnwrappedType (expr.getType ());
			if (w != Type.INVALID)
			{
				ct = stdCvType (w, type, allowD2F, scope);
				if (ct != CV_TYPE_NONE)
				{
					implCvType = ct;
					if (test)
					{
						return OK;
					}
					return expr.unboxingConversion ().cast (type);
				}
			}
		}
		if (msg == null)
		{
			return null;
		}

		// still no conversion found ? throw a semantic error
		problems.addSemanticError (I18N.msg (msg,
			expr.getType (), type), pos);
		return new Expression (type);
	}

	/* *
	 * Return an array of all methods of the input array that are accessible
	 * in the specified scope. If the input array of methods was null, then
	 * null is returned.
	 * @param methods
	 * @return 
	 * /
	Method[] filterAccessibleMethods (Method[] methods, Type instance,
			Scope scope)
	{
		Method[] result = null;
		if (methods != null)
		{
			// initialise a temporary array to collect the result
			Method[] accessibleMethods = new Method[methods.length];

			// index counts the number of accessible methods
			int index = 0;

			// iterate through all methods of the input array
			for (int j = 0; j < methods.length; j++)
			{
				// obtain the next method
				Method m = methods[j];

				// check if the declaring type of the method is accessible
				// from the current context
				if (Reflection.isAccessible (InheritedMethod
					.getQualifyingType (m), null, scope.getDeclaredType ()))
				{
					// check if the method is accessible from the current context
					if (Reflection.isAccessible (m, instance, scope
						.getDeclaredType ()))
					{
						// copy the method to the result array
						accessibleMethods[index++] = m;
					}
				}
			}

			// create an array for the result whose size matches the
			// number of accessible methods found
			result = new Method[index];

			// copy the accessible methods
			System.arraycopy (accessibleMethods, 0, result, 0, index);
		}
		return result;
	}

	/**
	 * Check all methods of the given type for conversion functions.
	 * For each conversion function found an entry in the conversion
	 * graph is made.
	 * @param cg
	 * @param type
	 * /
	void addConversionNodes (ConversionGraph cg, Type type)
	{
		// loop over type and its supertypes
		while (type != null)
		{
			// check every function of the type
			for (int i = 0; i < type.getDeclaredMethodCount (); i++)
			{
				// obtain the i-th method declared by the source type
				Method m = type.getDeclaredMethod (i);

				// if a conversion function was found, insert it into the
				// conversion graph
				switch (getAutoConversionType (m))
				{
					case AUTO_CTOR:
						// ctor-conversion
						cg.addConversion (m.getParameterType (0), m
							.getDeclaringType (), m);
						break;
					case AUTO_STATIC_VALUE_OF:
					case AUTO_STATIC_TO_X:
						// valueOf-conversion or static toX-conversion
						cg.addConversion (m.getParameterType (0), m
							.getReturnType (), m);
						break;
					case AUTO_TO_X:
						cg.addConversion (m.getDeclaringType (), m
							.getReturnType (), m);
						break;
				}
			}

			// add conversion nodes for interfaces (may contain toX-methods)
			for (int i = 0; i < type.getDeclaredInterfaceCount (); i++)
			{
				addConversionNodes (cg, type.getDeclaredInterface (i));
			}

			type = type.getSupertype ();
		}
	}

	/**
	 * Add nodes that represent conversions provided by imported
	 * types and functions.
	 * Statically imported functions are of the type 'static B valueOf(A)'
	 * and 'static B toB(A)'.
	 * @param cg
	 * @param scope
	 * /
	void addConversionNodesFromScope (ConversionGraph cg, Scope scope)
	{
		// iterate through all enclosing scopes
		Scope currentScope = scope;
		while (currentScope != null)
		{
			// check if this scope is of type SingleStatic import
			if (currentScope instanceof SingleStaticImport)
			{
				// obtain imported type and members
				SingleStaticImport ssi = (SingleStaticImport) currentScope;
				Type importedType = ssi.getImportedType ();
				String importedMembers = ssi.getImportedMembers ();

				// loop over importedType and its supertypes
				while (importedType != null)
				{
					// check every imported function of the type
					for (int i = 0; i < importedType.getDeclaredMethodCount (); i++)
					{
						// obtain the i-th method declared by the imported type
						Method m = importedType.getDeclaredMethod (i);

						// obtain the simple name of the function
						String simpleName = m.getSimpleName ();

						// if a conversion function was found
						// and its name matches the imported name, insert it into the
						// conversion graph
						if (simpleName != null
							&& simpleName.equals (importedMembers)
							&& Reflection.isStatic (m))
						{
							switch (getAutoConversionType (m))
							{
								case AUTO_STATIC_VALUE_OF:
								case AUTO_STATIC_TO_X:
									cg.addConversion (m.getParameterType (0), m
										.getReturnType (), m);
									break;
							}
						}
					}
					importedType = importedType.getSupertype ();
				}

			}
			// check if this scope is of type SingleStatic import
			else if (currentScope instanceof StaticImportOnDemand)
			{
				// obtain imported type and members
				StaticImportOnDemand siod = (StaticImportOnDemand) currentScope;
				Type importedType = siod.getImportedType ();

				// loop over importedType and its supertypes
				while (importedType != null)
				{
					// check every imported function of the type
					for (int i = 0; i < importedType.getDeclaredMethodCount (); i++)
					{
						// obtain the i-th method declared by the imported type
						Method m = importedType.getDeclaredMethod (i);

						if (Reflection.isStatic (m))
						{
							switch (getAutoConversionType (m))
							{
								case AUTO_STATIC_VALUE_OF:
								case AUTO_STATIC_TO_X:
									cg.addConversion (m.getParameterType (0), m
										.getReturnType (), m);
									break;
							}
						}
					}
					importedType = importedType.getSupertype ();
				}
			}

			// next scope is enclosing scope
			currentScope = currentScope.getEnclosingScope ();
		}
	}


	static final int AUTO_NONE = -1;
	static final int AUTO_STATIC_VALUE_OF = 0;
	static final int AUTO_STATIC_TO_X = 1;
	static final int AUTO_TO_X = 2;
	static final int AUTO_CTOR = 3;

	/*
	 * Returns the type of autoconversion which is implemented
	 * by the specified method, one of
	 * {@link #AUTO_NONE}, {@link #AUTO_STATIC_VALUE_OF},
	 * {@link #AUTO_STATIC_TO_X}, {@link #AUTO_TO_X}, or
	 * {@link #AUTO_CTOR}.
	 * 
	 * @param m a method
	 * @return type of autoconversion
	 * /
	static int getAutoConversionType (Method m, Scope scope)
	{
		if (Reflection.isStatic (m))
		{
			if (m.getParameterCount () == 1)
			{
				if (m.getSimpleName ().equals ("valueOf"))
				{
					
				}
				else if (m)
			&& (m.getParameterCount () == 1))
			{
				result = AUTO_STATIC_VALUE_OF;
			}

			// check for conversion functions of the form 'static B toB(A)'
			else if (options.allowConversionWithStaticToX
				&& Reflection.isStatic (m)
				&& m.getSimpleName ().equals (
					"to" + m.getReturnType ().getSimpleName ())
				&& (m.getParameterCount () == 1))
			{
				result = AUTO_STATIC_TO_X;
			}

			// check for conversion functions of the form 'B A.toB()'
			else if (options.allowConversionWithToX
				&& !Reflection.isStatic (m)
				&& m.getSimpleName ().equals (
					"to" + m.getReturnType ().getSimpleName ())
				&& (m.getParameterCount () == 0))
			{
				result = AUTO_TO_X;
			}

			// check for conversion functions of the form 'B(A)'
			else if (options.allowConversionWithCtor && Reflection.isCtor (m)
				&& (m.getParameterCount () == 1)
				&& !Reflection.isAbstract (m.getDeclaringType ())
				&& !Reflection.isInner (m.getDeclaringType ()))
			{
				result = AUTO_CTOR;
			}
		}

		return result;
	}* /

	/**
	 * Query the conversion graph if a conversion from type source
	 * to type target is possible using autoconversions and return
	 * the method which implements autoconversion.
	 * 
	 * @param sourceType source type of expression
	 * @param targetType desired target type
	 * @param pos
	 * @return method implementing the conversion, or <code>null</code>
	 * /
	Method getAutoConversionMethod (Type sourceType, Type targetType, AST pos, Scope scope)
	{
		Method result = null;

		//
		// insert all possible conversion functions into the conversion graph
		//

		// make sure that neither source nor target is void
		if (!Reflection.equal (sourceType, Type.VOID)
			&& !Reflection.equal (targetType, Type.VOID))
		{
			// add possible conversions to the graph if not already done so
			addConversionNodes (conversionGraph, sourceType);
			addConversionNodes (conversionGraph, targetType);

			ConversionGraph cucg = currentCompilationUnitScope
				.getConversionGraph ();
			if (cucg == null)
			{
				cucg = new ConversionGraph ();
				// add possible conversions from compilation unit scope
				addConversionNodesFromScope (cucg, currentCompilationUnitScope);
				currentCompilationUnitScope.initConversionGraph (cucg);
			}

			ObjectList conversions = new ObjectList ();

			// search the global conversion graph for matching conversions
			findAllAutoConversions (conversions, conversionGraph, sourceType,
				targetType);

			// search the conversion graph of compilation unit for matching conversions
			findAllAutoConversions (conversions, cucg, sourceType, targetType);

			// remove all methods that are not accessible from the current context
			Method[] accessibleConversions = filterAccessibleMethods (
				(Method[]) conversions
					.toArray (new Method[conversions.size ()]), sourceType,
				currentTypeScope);

			for (int i = accessibleConversions.length - 1; i >= 0; i--)
			{
				Method m = accessibleConversions[i];
				if (m != null)
				{
					Type mt = (m.getParameterCount () == 0) ? m.getDeclaringType () : m.getParameterType (0);
					for (int j = accessibleConversions.length - 1; j >= 0; j--)
					{
						Method n = accessibleConversions[j];
						if ((i != j) && (n != null))
						{
							Type nt = (n.getParameterCount () == 0) ? n.getDeclaringType () : n.getParameterType (0);
							if (isWideningConversion (mt, nt, scope))
							{
								accessibleConversions[j] = null;
							}
						}
					}
				}
			}
			
			// check if there is exactly one conversion
			for (int i = accessibleConversions.length - 1; i >= 0; i--)
			{
				Method m = accessibleConversions[i];
				if (m != null)
				{
					if (result != null)
					{
						// more than one conversion
						return null;
					}
					else
					{
						result = m;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Search the conversion graph for a possible conversion from
	 * the source type to the target type.
	 * @param source
	 * @param target
	 * @return an array of all possible conversion methods or null if none was found
	 * /
	void findAllAutoConversions (ObjectList convs, ConversionGraph cg,
			Type source, Type target)
	{
		// loop over source and its supertypes
		while (source != null)
		{
			// get node for the source type
			ConversionNode sourceNode = cg.getNode (source);

			// get node for the target type
			ConversionNode targetNode = cg.getNode (target);

			// check that source and target nodes exist
			if (sourceNode != null && targetNode != null)
			{
				// check all conversions for this node to see if there is
				// exactly one conversion to the target type
				ConversionEdge[] edges = sourceNode.findEdges (targetNode);

				// check if conversions were found
				if (edges != null && edges.length > 0)
				{
					// extract methods from edges
					for (int i = 0; i < edges.length; i++)
					{
						convs.addIfNotContained (edges[i].getMethod ());
					}
				}
			}

			// find conversions for interfaces implemented by source
			for (int i = 0; i < source.getDeclaredInterfaceCount (); i++)
			{
				findAllAutoConversions (convs, cg, source
					.getDeclaredInterface (i), target);
			}

			source = source.getSupertype ();
		}
	}*/

	void checkIfCanonical (AST id, Type t)
	{
		if (!t.getName ().equals (qualifiedName (id)))
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.NAME_NOT_CANONICAL, qualifiedName (id), t), id);
		}
	}

	void checkMember (Scope scope, long mods, AST pos)
	{
		if ((mods & Member.STATIC) != 0)
		{
			TypeScope ts = TypeScope.get (scope);
			if ((ts != null) && !(ts.isStatic () && ts.isNonlocal ()))
			{
				problems.addSemanticError (I18N,
					ProblemReporter.STATIC_MEMBER_IN_INNER, pos);
			}
		}
	}

	boolean checkTopLevelType (AST id, Type type, Scope scope)
	{
		Type t = (Type) resolver.resolveIfDeclared (id, Members.TYPE
			| Members.EXCLUDE_TYPES_IN_PACKAGES, scope);
		if ((t != null)
			&& (t != type)
			&& (!Reflection.equal (t, type) || ((t instanceof CClass) && (type instanceof CClass))))
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.TOP_LEVEL_TYPE_CONFLICT, id.getText ()), id);
			return false;
		}
		return true;
	}

	// helper set to search for unimplemented methods
	private final Set<Type> implTypesToCheck = new HashSet<Type>();
	
	// add all interfaces implemented by type t to the set s
	private void recursivelyAddAllImplementedInterfaces(Set<Type> s, Type t)
	{
		for (int i = 0; i < t.getDeclaredInterfaceCount(); i++)
		{
			Type declaredInterface = t.getDeclaredInterface(i);
			implTypesToCheck.add(declaredInterface);
			recursivelyAddAllImplementedInterfaces(s, declaredInterface);
		}
	}
	
	/**
	 * For a class implementing certain interfaces, check if all the methods declared
	 * by those interfaces are really implemented in this class. If not and the class
	 * is not abstract, report a compilation error.
	 */
	void checkForUnimplementedMethod(AST id, TypeScope s)
	{
		// get declaring class
		CClass c = s.getDeclaredType();
		
		// abstract classes and interfaces are skipped
		if (!Reflection.isAbstract(c) && !Reflection.isInterface(c))
		{
			implTypesToCheck.clear();
			// consider all interfaces implemented by c and its superclasses
			// stop walking up the hierarchy if a non-abstract class is reached
			// (including Type.OBJECT), since this implements all methods by definition
			recursivelyAddAllImplementedInterfaces(implTypesToCheck, c);
			Type x = c.getSupertype();
			while (x != null)
			{
				implTypesToCheck.add(x);
				recursivelyAddAllImplementedInterfaces(implTypesToCheck, x);
				x = x.getSupertype();
			}
			
			// check all interfaces
			for (Type t : implTypesToCheck)
			{
				// check if all abstract methods of class or interface t had been implemented
				for (int j = 0; j < t.getDeclaredMethodCount(); j++)
				{
					Method m = t.getDeclaredMethod(j);
					if (!Reflection.isAbstract(m))
					{
						// skip non-abstract methods
						continue;
					}
					m = Reflection.findMethodWithPrefixInTypes(c, m.getDescriptor(), false, false);
					if ((m == null) || Reflection.isAbstract(m))
					{
						problems.addSemanticError(I18N.msg(
								ProblemReporter.UNIMPLEMENTED_METHOD, id
										.getText(), t.getName() + "."
										+ m.getSimpleName()), id);
					}
				}
			}
			implTypesToCheck.clear();
		}
	}
	
	static EdgeDirection edgeDirection (AST edge)
	{
		switch (edge.getType ())
		{
			case SUB:
				return EdgeDirection.UNDIRECTED;
			case LEFT_ARROW:
				return EdgeDirection.BACKWARD;
			case ARROW:
				return EdgeDirection.FORWARD;
			case X_LEFT_RIGHT_ARROW:
				return EdgeDirection.BOTH;
			default:
				throw new AssertionError ();
		}
	}

	ProduceScope createProduceScope (BlockScope scope, Expression block,
			Expression producer, AST pos)
	{
		Type producerType = (producer != null) ? producer.getType () : Type.INVALID;
		Local s = scope.declareLocal ("state.", 0, producerType, pos);
		ProduceScope cs = new ProduceScope (scope, s);
		block.add (s.createSet ().add (producer));
		return cs;
	}

	void initModuleSuperclass (TypeScope scope)
	{
		if (!scope.getDeclaredType ().hasSupertypeBeenSet ())
		{
			scope.getDeclaredType ().initSupertype (
				scope.getDefaultModuleSuperclass ());
		}
	}

	public <T> T createModel (Class<T> type, Type<?> model, AST pos)
	{
		if (model == null)
		{
			return null;
		}
		if (Reflection.isAbstract (model))
		{
			problems.addSemanticError (I18N.msg (ProblemReporter.CONCRETE_CLASS_EXPECTED, model.getName ()), pos);
			return null;
		}
		if (!Reflection.isSupertypeOrSame (type, model))
		{
			problems.addSemanticError (I18N.msg (ProblemReporter.INCOMPATIBLE_TYPES, model.getName (), type.getName ()), pos);
			return null;
		}
		if (!Reflection.isPublic (model))
		{
			problems.addSemanticError (I18N.msg (ProblemReporter.PUBLIC_CLASS_EXPECTED, model.getName ()), pos);
			return null;
		}
		Method ctor = Reflection.getDefaultConstructor (model);
		if (ctor == null)
		{
			problems.addSemanticError (I18N.msg (ProblemReporter.NO_DEFAULT_CONSTRUCTOR, model.getName ()), pos);
			return null;
		}
		if (!Reflection.isPublic (ctor))
		{
			problems.addSemanticError (I18N.msg (ProblemReporter.DEFAULT_CONSTRUCTOR_NOT_PUBLIC, model.getName ()), pos);
			return null;
		}
		try
		{
			return type.cast (ctor.invoke (null, null));
		}
		catch (Exception e)
		{
			Throwable t = (e instanceof InvocationTargetException) ? e.getCause () : e;
			problems.addSemanticError (I18N.msg (ProblemReporter.INSTANTIATION_EXCEPTION, model.getName (), t.getClass ().getName (), t.getMessage ()), pos);
			return null;
		}
	}

	Type getDefaultSuperclass ()
	{
		return Type.OBJECT;
	}

	TypeScope createAndDeclareType (Scope scope, long mods, AST id,
			final AST ext, Type[] extendedTypes, AST impl,
			Type[] implementedTypes)
	{
		Type superType;
		boolean checkSuperType = false;
		if ((mods & Member.INTERFACE) != 0)
		{
			superType = Type.OBJECT;
			implementedTypes = extendedTypes;
			impl = ext;
		}
		else if (extendedTypes.length == 0)
		{
			if ((mods & MOD_MODULE) != 0)
			{
				superType = null;
			}
			else
			{
				superType = getDefaultSuperclass ();
			}
		}
		else
		{
			superType = extendedTypes[0];
			if (!Reflection.isInvalid (superType))
			{
				checkSuperType = true;
			}
		}
		Type toCheck = checkSuperType ? superType : null;
		CClass t;
		if (scope instanceof CompilationUnitScope)
		{
			CompilationUnitScope cs = (CompilationUnitScope) scope;
			if (shell != null)
			{
				t = shell;
			}
			else
			{
				t = new CClass (id.getText (), cs.getPackage ()
					.getCanonicalName (id.getText ()), (int) mods, null,
					toCheck, ext, implementedTypes, impl, this);
				checkTopLevelType (id, t, scope);
			}
			cs.declareType (t);
		}
		else if (scope instanceof TypeScope)
		{
			TypeScope e = (TypeScope) scope;
			CClass declaring = e.getDeclaredType ();
			for (int i = declaring.getDeclaredTypeCount () - 1; i >= 0; i--)
			{
				if (declaring.getDeclaredType (i).getSimpleName ().equals (
					id.getText ()))
				{
					problems.addSemanticError (I18N.msg (
						ProblemReporter.DUPLICATE_TYPE, id.getText ()), id);
					break;
				}
			}
			t = new CClass (id.getText (), declaring.getBinaryName () + '$'
				+ id.getText (), (int) mods, declaring, toCheck, ext,
				implementedTypes, impl, this);
			declaring.declareType (t);
		}
		else
		{
			BlockScope b = (BlockScope) scope;
			TypeScope e = TypeScope.get (scope);
			CClass declaring = e.getDeclaredType ();
			String name;
			if (id != null)
			{
				name = id.getText ();
				if (b.findClass (name) != null)
				{
					problems.addSemanticError (I18N.msg (
						ProblemReporter.DUPLICATE_TYPE, name), id);
				}
			}
			else
			{
				name = "";
			}
			t = new CClass (name, declaring.getBinaryName () + '$'
				+ e.nextLocalClassId () + name,
				(int) mods | Member.LOCAL_CLASS, declaring, toCheck, ext,
				implementedTypes, impl, this);
			CompilationUnitScope.get (e).declareLocalClass (t);
		}
		if ((superType != null) && (shell != t))
		{
			t.initSupertype (superType);
		}
		toResolveBeforeMethodDeclaration.add (t);
		if (t != shell)
		{
			t.initTypeLoader (ClassPath.get (scope));
		}
		TypeScope ret = new TypeScope (scope, t, mods, (Compiler) this, id);
		if ((t.getModifiers () & Member.LOCAL_CLASS) != 0)
		{
			((BlockScope) scope).declareLocalClass (ret);
		}
		return ret;
	}

	void createDefaultConstructor (ClassInfo info, boolean force)
	{
		Type t = info.scope.getDeclaredType ();
		Extension e;
		if (force
			|| ((info.defaultConstructorScope == null)
				&& (Reflection.getDefaultConstructor (t) == null)
				&& ((e = info.scope.getExtension ()) != null)
				&& e.forcesDefaultConstructorFor (t)))
		{
			info.defaultConstructorScope = info.scope.createMethodScope ((t
				.getModifiers () & Member.ACCESS_MODIFIERS)
				| MOD_CONSTRUCTOR);
			info.defaultConstructorScope.createAndDeclareMethod ("<init>",
				Type.VOID);
		}
	}

	Local declareParameter (MethodScope s, AST id, long mod, Type t)
	{
		String name = id.getText ();
		if (s.findLocal (name, false) != null)
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.DUPLICATE_PARAMETER, name), id);
			name = s.getUniqueName (name);
		}
		return s.declareParameter (name, mod, t);
	}

	/*
	 Object newInstance (Type type, AST pos)
	 {
	 if (problems.containsErrors ())
	 {
	 return null;
	 }
	 if ((type.getModifiers () & Member.ABSTRACT) != 0)
	 {
	 problems.addSemanticError
	 (I18N.msg (ProblemReporter.CONCRETE_CLASS_EXPECTED, type.getName ()), pos);
	 return null;
	 }
	 if ((type.getModifiers () & Member.PUBLIC) == 0)
	 {
	 problems.addSemanticError
	 (I18N.msg (ProblemReporter.PUBLIC_CLASS_EXPECTED, type.getName ()), pos);
	 return null;
	 }
	 Method c = Reflection.getDefaultConstructor (type);
	 if (c == null)
	 {
	 problems.addSemanticError
	 (I18N.msg (ProblemReporter.NO_DEFAULT_CONSTRUCTOR, type.getName ()), pos);
	 return null;
	 }
	 if ((c.getModifiers () & Member.PUBLIC) == 0)
	 {
	 problems.addSemanticError
	 (I18N.msg (ProblemReporter.DEFAULT_CONSTRUCTOR_NOT_PUBLIC, type.getName ()), pos);
	 return null;
	 }
	 try
	 {
	 return c.invoke (null, null);
	 }
	 catch (IllegalAccessException e)
	 {
	 throw new AssertionError (e.getMessage ());
	 }
	 catch (java.lang.reflect.InvocationTargetException e)
	 {
	 Throwable t = e.getCause ();
	 problems.addSemanticError
	 (I18N.msg (ProblemReporter.INSTANTIATION_EXCEPTION, type.getName (),
	 t.getClass ().getName (), t.getMessage ()), pos);
	 return null;
	 }
	 }
	 */

	private final Members members = new Members ((Compiler) this);

	private Expression argList;
	private Type[] implicitOption;
	private Method invokable;


	private Expression compileBuiltInOperator (Expression op, Expression[] operands, Scope scope)
	{
		try
		{
			// at first, try to compile the standard operator of XL
			return ((operands.length == 1) ? op.compile (scope,
				operands[0]) : op.compile (scope, operands[0], operands[1])).toConst ();
		}
		catch (RuntimeException e)
		{
			// possibly op.compile prepended casts etc., remove them from exprs
			operands[0].removeFromChain ();
			if (operands.length == 2)
			{
				operands[1].removeFromChain ();
			}
			return null;
		}
	}

	static final int OP_INC = 1;
	static final int OP_POST = 2;
	static final int OP_ISCOPE = 4;

	boolean ambiguousOperatorOverload;

	Expression compileOperator (Expression op, String operatorName,
			Expression[] operands, int flags,
			Scope scope, AST opAST)
	{
		ambiguousOperatorOverload = false;
		// test operator overloading before traditional XL operator?
		boolean overloadBeforeTraditional = false;

		if (OPERATOR_NAME_EQUALS.equals (operatorName)
			|| OPERATOR_NAME_NOT_EQUALS.equals (operatorName)
			|| OPERATOR_NAME_IN.equals (operatorName))
		{
			// operator overloading precedes XL operators only if
			// operator is == or != and if at least one of the operands
			// has a reference type
			for (int i = 0; i < operands.length; i++)
			{
				if (operands[i].etype == TypeId.OBJECT)
				{
					overloadBeforeTraditional = true;
					break;
				}
			}
		}

		if ((op != null) && !overloadBeforeTraditional)
		{
			// we have an op which defines the standard operator of XL 
			Expression e = compileBuiltInOperator (op, operands, scope);
			if (e != null)
			{
				return e;
			}
		}

		if (operatorName != null)
		{
			if ((flags & (OP_INC | OP_POST)) == OP_INC)
			{
				operands = new Expression[] {operands[0]};
			}

			// remember results and error messages
			Expression e;
			RecognitionExceptionList errs;

			// check if there is a matching operator with two parameters
			// for both operands
			try
			{
				problems.disableAdd ();
				e = compileMethodInvocation (new ASTWithToken (IDENT,
					operatorName), ArgumentTransformations.NO_IMPLICIT_ARGS,
					operands,
					((flags & OP_ISCOPE) != 0)
					? Members.INCLUDE_FIRST_INSTANCE_SCOPE | Members.OPERATOR_METHODS
					: Members.EXCLUDE_INSTANCE_SCOPES | Members.OPERATOR_METHODS, scope, opAST);
			}
			finally
			{
				errs = problems.enableAdd ();
			}
			if (errs.containsErrors ())
			{
				if (members.haveApplicable ())
				{
					problems.addAll (errs);
					return null;
				}
			}
			else if (e != null)
			{
				return e;
			}
		}

		if ((op != null) && overloadBeforeTraditional)
		{
			// we have an op which defines the standard operator of XL 
			Expression e = compileBuiltInOperator (op, operands, scope);
			if (e != null)
			{
				return e;
			}
		}

		String sym;
		switch (opAST.getType ())
		{
			case INDEX_OP:
				sym = "[]";
				break;
			case INVOKE_OP:
				sym = "()";
				break;
			case QUOTE:
				sym = "``";
				break;
			default:
				sym = opAST.getText ();
				break;
		}

		if ((operands.length == 1) || ((flags & OP_INC) != 0))
		{
			problems.addSemanticError (
				I18N.msg (ProblemReporter.ILLEGAL_UNOP_TYPE, sym, operands[0]
					.getType ()), opAST);
		}
		else
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.ILLEGAL_BINOP_TYPE, sym,
				operands[0].getType (), operands[1].getType ()), opAST);
		}
		return null;
	}

	Expression compileCompoundAssignment (Expression op, String operatorName,
			Expression expr1, Expression expr2, int flags,
			Scope scope, AST lhs, AST opAST)
	{
		if (expr1 == null)
		{
			return null;
		}
		if (expr2 == null)
		{
			return new Expression (expr1.getType ());
		}

		try
		{
			// at first, try to compile the standard operator of Java
			Expression e = op.compile (scope, new Pop (expr1.getType ()), expr2).cast (expr1.getType ());

			// now if this succeeded, we have a standard compound assignment
			if (!(expr1 instanceof Variable))
			{
				problems.addSemanticError (I18N,
					((flags & OP_INC) != 0) ? ProblemReporter.INC_VARIABLE
							: ProblemReporter.LHS_VARIABLE, lhs);
			}
			else if (expr2 != null)
			{
				return ((Variable) expr1)
					.toAssignment (
						((flags & OP_POST) != 0) ? Assignment.POSTFIX_COMPOUND
								: Assignment.COMPOUND).add (e);
			}
			// in case of errors, return a dummy expression of expr1's type
			return new Expression (expr1.getType ());
		}
		catch (IllegalOperandTypeException e)
		{
			// possibly op.compile prepended casts etc., remove them from expr2
			expr2.removeFromChain ();
		}
		catch (IllegalCastException e)
		{
			// possibly op.compile prepended casts etc., remove them from expr2
			expr2.removeFromChain ();
		}

		// delegate to normal handling of binary operators
		Expression e = compileOperator (null, operatorName, new Expression[] {
				expr1, expr2}, flags, scope, opAST);

		return (e != null) ? e
		// return a dummy expression of expr1's type
				: new Expression (expr1.getType ());
	}

	Expression compileAssignment (Expression expr1, Expression expr2, AST lhs,
			AST rhs, Scope scope)
	{
		if (expr1 == null)
		{
			return null;
		}
		if (!(expr1 instanceof Variable))
		{
			problems.addSemanticError (I18N, ProblemReporter.LHS_VARIABLE, lhs);
			return new Expression (expr1.getType ());
		}
		if (expr2 == null)
		{
			return new Expression (expr1.getType ());
		}
		expr2 = assignmentConversion (expr2, expr1.getType (), scope, rhs);
		return ((Variable) expr1).toAssignment (Assignment.SIMPLE).add (expr2);
	}

	Expression compileDeferredAssignment (String operatorName,
			Expression lhs, Expression rhs, BlockScope scope, AST lhsAST, AST opAST)
	{
		if ((lhs == null) || (rhs == null))
		{
			return null;
		}

		Expression lhsOrig = lhs;

		Local w;
		if ((lhs instanceof GetLocal)
			&& ((w = ((GetLocal) lhs).getLocal (0).wrapper) != null))
		{
			Property p = w.wrapped.getPatternBuilder ().getModel ().getWrapProperty (w.getType ());
			if (p != null)
			{
				p = p.getTypeCastProperty (lhs.getType ());
				lhs = new GetProperty (p).add (w.createExpression (scope, lhsAST));
			}
		}
		else if (lhs instanceof GetField)
		{
			Field f = ((GetField) lhs).getField ();
			if (!Reflection.isStatic (f))
			{
				Expression i = lhs.getFirstExpression ();
				de.grogra.xl.property.CompiletimeModel cm = scope.getPropertyModel (i.getType (), (Compiler) this, lhsAST);
				if (cm != null)
				{
					Property p = cm.getDirectProperty (i.getType (), f.getSimpleName ());
					if (p != null)
					{
						lhs.removeAll (null);
						lhs = new GetProperty (p).add (i);
					}
				}
				
			}
		}

		if (lhs instanceof GetProperty)
		{
			Property p = ((GetProperty) lhs).getProperty ();
			if (!Reflection.isPrimitive (p.getType ()))
			{
				rhs = assignmentConversion (rhs, p.getType (), scope, opAST);
			}
			int indexCount = lhs.getExpressionCount () - 1;
			Expression i = lhs.getFirstExpression ();
			Expression instance = new ExpressionList (i.getType (), true);
			Expression indices = (indexCount > 0) ? new PopIntArray (indexCount) : new GetField (INT_0);
			problems.disableAdd ();
			Expression def = compileOperator (null, operatorName,
				new Expression[] {new GetPropertyInstance (p), instance, indices, rhs},
				0, scope, opAST);
			RecognitionExceptionList errs = problems.enableAdd ();
			if (def != null)
			{
				problems.addAll (errs);
				instance.receiveChildren (lhs);
				if (indexCount > 0)
				{
					Expression ind = i.getNextExpression ();
					i.setNextSibling (null);
					instance.add (new PushInts ().add (ind));
					Expression lca = indices.getLeastCommonAncestor (rhs);
					if (lca != null)
					{
						Expression rhsArg = lca.getLastExpression ();
						Expression indicesArg = (Expression) rhsArg.getPredecessor ();
						assert rhsArg.getLeastCommonAncestor (rhs) == rhsArg;
						assert indicesArg.getLeastCommonAncestor (indices) == indicesArg;
						
						new NoBytecode (indicesArg.getType ()).substitute (indicesArg).add (indicesArg);
						new SwapBytecode (rhsArg.getType (), indicesArg).substitute (rhsArg).add (rhsArg);
					}
				}
				
				// create a table of all (type, property) pairs for operator$defRateAssign
				// and store them in a special class "$ODEHelper"
				if (OPERATOR_NAME_DEFERRED_RATE_ASSIGN.equals(operatorName)) {
					// find out scope of topmost type
					TypeScope s = TypeScope.getNonlocal(scope);
					while (TypeScope.get(s.getEnclosingScope()) != null)
					{
						s = TypeScope.get(s.getEnclosingScope());
					}
					// and retrieve the type for that scope
					CClass outer = s.getDeclaredType();
					// helper will be used to store the rate table
					CClass helper = null;
					String helperName = "$ODEHelper";
					// find the helper class if it exists
					for (int j = outer.getDeclaredTypeCount () - 1; j >= 0; j--)
					{
						if (outer.getDeclaredType (j).getSimpleName ().equals (helperName))
						{
							helper = (CClass) outer.getDeclaredType (j);
							break;
						}
					}
					// if helper did not exist, then create one
					if (helper == null)
					{
						// create helper type
						helper = new CClass(helperName, outer.getBinaryName() + "$" + helperName,
								Member.PUBLIC | Member.STATIC | Member.FINAL | Member.SYNTHETIC, outer, false);
						// let helper derive from object
						helper.initSupertype (Type.OBJECT);
						// make helper inner class of outer
						outer.declareType(helper);
						// declare field in $ODEHelper as: 
						//     public static final RateAssignment[] TABLE;
						Field f = helper.declareField("TABLE", Member.PUBLIC | Member.STATIC | Member.FINAL, 
								ClassAdapter.wrap(RateAssignment[].class));
						// create static initializer for the field:
						//     static { TABLE = new RateAssignment[] { ... }; }
						helper.rateAssignmentInitializer = new ArrayInit(f.getType());
						new XMethod ("<clinit>", Member.STATIC, helper, Type.TYPE_0, Type.VOID, 
								new AssignField(f, Assignment.SIMPLE).add(helper.rateAssignmentInitializer));
					}
					
					// get type of the object the property belongs to
					Type type = i.getType();

					// prepare static initializer as expression tree
					// instances of RateAssignment will be created by factory method "create"
					//     create(RuntimeModel.Property, Class)
					InvokeStatic is = new InvokeStatic(Reflection.findMethodInClasses(
							ClassAdapter.wrap(RateAssignment.class), "create"));
					is.complete(null);
					// first child returns property
					GetPropertyInstance gpi = new GetPropertyInstance(p);
					gpi.complete(outer);
					// second child returns class
					helper.rateAssignmentInitializer.add(is.add(gpi).add(new ObjectConst(type, Type.CLASS)));
				}
				
				return def;
			}
			if (ambiguousOperatorOverload)
			{
				problems.addAll (errs);
				return null;
			}
		}
		return compileOperator (null, operatorName,
			new Expression[] {lhsOrig, rhs}, 0, scope, opAST);
	}

	static final Type[] GENERATOR_TYPES;

	static
	{
		GENERATOR_TYPES = new Type[TypeId.TYPE_COUNT];
		for (int i = 0; i < TypeId.TYPE_COUNT; i++)
		{
			try
			{
				GENERATOR_TYPES[i] = ClassAdapter.wrap (Class.forName ("de.grogra.xl.lang.VoidTo" + Reflection.getTypeSuffix (i) + "Generator"));
			}
			catch (ClassNotFoundException e)
			{
				throw new NoClassDefFoundError (e.getMessage ());
			}
		}
	}

	Expression compileArrayGenerator (Expression e, BlockScope scope, AST pos)
	{
		Type t = e.getType ();
		if (Reflection.isArray (t))
		{
			return new ArrayGenerator
				(t.getComponentType (),
				 scope.declareLocal ("array.", Member.FINAL, t, pos),
				 scope.declareLocal ("counter.", 0, Type.INT, pos))
				.add (e);
		}
		else
		{
			int typeId = -1;
			for (int i = 0; i < TypeId.TYPE_COUNT; i++)
			{
				if (Reflection.isSupertypeOrSame (GENERATOR_TYPES[i], t))
				{
					if (typeId >= 0)
					{
						typeId = -1;
						break;
					}
					else
					{
						typeId = i;
					}
				}
			}
			if (typeId < 0)
			{
				problems.addSemanticError (I18N.msg (ProblemReporter.NO_ARRAY_TYPE, t), pos);
				return null;
			}
			InvokeVirtual i = new InvokeVirtual (Reflection.findMethodWithPrefixInTypes (GENERATOR_TYPES[typeId], "mevaluate", false, true));
			i.add (e);
			i.setGenerator ();
			setAST (i, pos);
			return i;
		}
	}

	Expression compileAggregateBooleanOr (Expression e, BlockScope scope,
			AST pos)
	{
		if (e == null)
		{
			return new Expression (Type.BOOLEAN);
		}
		ExpressionList list = new ExpressionList (Type.BOOLEAN);
		BlockScope bs = new BlockScope (scope, list);
		Local l = bs.declareLocal ("or.", 0, Type.BOOLEAN, pos);
		list.add (l.createSet ().add (
			new BooleanConst (false)));
		MethodScope ms = scope.getMethodScope ();
		ms.enterBreakTarget (null);
		Block b = Block.createSequentialBlock ();
		b.add (l.createSet ().add (e));
		b.add (new If ().add (l.createGet ()).add (
			new Break (ms.getTargetId (null, false))));
		ms.leave (b);
		list.add (b);
		list.add (l.createGet ());
		bs.receiveLocals (scope, pos);
		return list;
	}

	/**
	 * Indicates that methods are to be resolved currently.
	 * @see #resolution
	 */
	private static final int METHOD_RES = 0;

	/**
	 * Indicates that node predicates are to be resolved currently.
	 * @see #resolution
	 */
	private static final int PREDICATE_RES = 1;

	/**
	 * Indicates that edge predicates are to be resolved currently.
	 * @see #resolution
	 */
	private static final int EDGE_PREDICATE_RES = 2;

	/**
	 * Indicates that undirected edge predicates are to be resolved currently.
	 * @see #resolution
	 */
	private static final int UNDIR_EDGE_PREDICATE_RES = 3;

	/**
	 * Determines the current kind of resolution, one of
	 * {@link #METHOD_RES}, {@link #PREDICATE_RES},
	 * {@link #EDGE_PREDICATE_RES}, {@link #UNDIR_EDGE_PREDICATE_RES}.
	 */
	private int resolution;

	private ArgumentTransformations resolutionImplicit;
	private Expression[] resolutionArgs;

	/**
	 * Indicates that only widening conversions are allowed for
	 * applicable method invocations.
	 * @see #resolutionPhase
	 */
	static final int APPLICABLE_BY_WIDENING_CONVERSION_PHASE = 0;

	/**
	 * Indicates that method invocation conversions are allowed for
	 * applicable method invocations.
	 * @see #resolutionPhase
	 */
	static final int APPLICABLE_BY_METHOD_INVOCATION_CONVERSION_PHASE = 1;

	/**
	 * Indicates that enhanced method invocation conversions are allowed for
	 * applicable method invocations.
	 * @see #resolutionPhase
	 */
	static final int APPLICABLE_BY_ENHANCED_METHOD_INVOCATION_CONVERSION_PHASE = 2;

	/**
	 * Indicates that enhanced method invocation conversions
	 * and variable arity method invocations are allowed for
	 * applicable method invocations.
	 * @see #resolutionPhase
	 */
	static final int APPLICABLE_BY_VARIABLE_ARITY_PHASE = 3;

	/**
	 * Contains the current phase of resolution, one of
	 * {@link #APPLICABLE_BY_WIDENING_CONVERSION_PHASE},
	 * {@link #APPLICABLE_BY_METHOD_INVOCATION_CONVERSION_PHASE},
	 * {@link #APPLICABLE_BY_ENHANCED_METHOD_INVOCATION_CONVERSION_PHASE},
	 * {@link #APPLICABLE_BY_VARIABLE_ARITY_PHASE}.
	 * This is only needed when applicable methods are to be resolved. 
	 */
	int resolutionPhase = APPLICABLE_BY_WIDENING_CONVERSION_PHASE;

	public boolean allowsAmbiguousMembers (Member first)
	{
		return false;
	}

	public boolean isApplicable (Member m, Applicability appl, Scope scope)
	{
		ArgumentTransformations impl;
		if (resolution == PREDICATE_RES)
		{
			impl = ((PatternWrapper) m).isFirstInOut () ? ArgumentTransformations.PREDICATE_ARGS
					: ArgumentTransformations.NO_IMPLICIT_ARGS;
		}
		else
		{
			impl = resolutionImplicit;
		}
		return impl.getApplicableOption ((Signature) m, appl, resolutionArgs, scope) >= 0;
	}

	public Type[] getArgumentTypes ()
	{
		Type[] t = new Type[resolutionArgs.length];
		for (int i = 0; i < t.length; i++)
		{
			t[i] = resolutionArgs[i].getType ();
			if (t[i] == null)
			{
				t[i] = Type.INVALID;
			}
		}
		return t;
	}

	private static int lessThanIfEqualSig (Method m1, Method m2)
	{
		if (m1 instanceof ShiftedMethod)
		{
			m1 = ((ShiftedMethod) m1).getMethod ();
		}
		if (m2 instanceof ShiftedMethod)
		{
			m2 = ((ShiftedMethod) m2).getMethod ();
		}
		if (!Reflection.equal (m1, m2))
		{
			return 0;
		}
		if (Reflection.equal (m1.getReturnType (), m2.getReturnType ()))
		{
			return !Reflection.isAbstract (m2)
				|| Reflection.isSuperclassOrSame (m1.getDeclaringType (), m2.getDeclaringType ())
				|| (!Reflection.isSuperclassOrSame (m2.getDeclaringType (), m1.getDeclaringType ()) && Reflection.isAbstract (m1))
				? 1 : -1;
		}
		else
		{
			return Reflection.isSupertypeOrSame (m1.getReturnType (), m2.getReturnType ()) ? 1 : -1;
		}
	}

	public boolean isLessThan (Member m1, Applicability a1, Member m2,
			Applicability a2, Scope scope)
	{
		if ((a1.scope != a2.scope) && (a1.scope != null) && (a2.scope != null))
		{
			return false;
		}
		if (a1.varArity != a2.varArity)
		{
			return false;
		}
		if (a1.implicitCount != a2.implicitCount)
		{
			return a1.implicitCount < a2.implicitCount;
		}
		Signature s1 = (Signature) m1, s2 = (Signature) m2;
		if ((a1.matchSet == a2.matchSet) && (s1 instanceof Method))
		{
			int t = lessThanIfEqualSig ((Method) m1, (Method) m2);
			if (t != 0)
			{
				return t > 0;
			}
		}
		return isMoreSpecific (s2, a2, s1, a1, scope)
			&& !isMoreSpecific (s1, a1, s2, a2, scope);
	}

	private boolean isMoreSpecific (Signature s1, Applicability a1,
			Signature s2, Applicability a2, Scope scope)
	{
		assert a1.varArity
			|| (s1.getParameterCount () == s2.getParameterCount ());
		for (int i = Math
			.max (s1.getParameterCount (), s2.getParameterCount ()) - 1; i >= 0; i--)
		{
			if (i == 1)
			{
				if (a1.array2Generator && !a2.array2Generator)
				{
					continue;
				}
				if (a2.array2Generator && !a1.array2Generator)
				{
					return false;
				}
			}
			Type arg = a1.actualArguments.get (i);
			if (!Reflection.equal (arg, a2.actualArguments.get (i)))
			{
				arg = null;
			}
			Type p1 = Reflection.getParameterType (s1, i, a1.varArity);
			Type p2 = Reflection.getParameterType (s2, i, a2.varArity);
			if (arg != null)
			{
				implicitConversion (new Expression (arg), p1, true, scope, null, null, true);
				int ct1 = implCvType;
				implicitConversion (new Expression (arg), p2, true, scope, null, null, true);
				int ct2 = implCvType;
				if (ct2 > ct1)
				{
					return false;
				}
				if (ct2 < ct1)
				{
					continue;
				}
			}
			if (!Reflection.isWideningConversion (p1, p2))
//				&& (implicitConversion (new Expression (p1), p2, false, scope, null, null, true) == null))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines the most specific method for the possible
	 * argument transformations <code>implicit</code> and the
	 * explicit arguments <code>args</code>. The context to search for
	 * must have been set previously in {@link #members}.
	 * 
	 * @param implicit set of alternative argument transformations
	 * @param args explicit arguments
	 * @return most specific method
	 */
	private Method findMostSpecificMethod (ArgumentTransformations implicit,
			Expression[] args) throws RecognitionException
	{
		resolution = METHOD_RES;
		resolutionImplicit = implicit;
		resolutionArgs = args;
		RecognitionException ex = null;
		for (resolutionPhase = APPLICABLE_BY_WIDENING_CONVERSION_PHASE; resolutionPhase <= APPLICABLE_BY_VARIABLE_ARITY_PHASE; resolutionPhase++)
		{
			try
			{
				return (Method) members.resolve (this);
			}
			catch (RecognitionException e)
			{
				ex = e;
			}
		}
		throw ex;
	}

	Expression getArgumentList ()
	{
		return argList;
	}

	Type[] getImplicitOption ()
	{
		return implicitOption;
	}

	Method getInvokable ()
	{
		return invokable;
	}

	static final Type FIELD_TYPE = ClassAdapter.wrap (Field.class);

	static final Type AGGREGATE_TYPE = ClassAdapter.wrap (Aggregate.class);
	static final Type DESCRIPTOR_TYPE = ClassAdapter
		.wrap (RoutineDescriptor.class);

	static final Type ANNOTATION_ARRAY_TYPE = ClassAdapter.wrap (Annotation[].class);

	static final Type FILTER_TYPE = ClassAdapter
		.wrap (de.grogra.xl.lang.Filter.class);

	static final Type ASSERTION_ERROR = ClassAdapter
		.wrap (AssertionError.class);

	static final Type VMX_TYPE = ClassAdapter.wrap (VMXState.class);

	static final Method ASSERTION_INIT = Reflection
		.findMethodWithPrefixInTypes (ASSERTION_ERROR, "m<init>;()V", false,
			true);
	
	static final Field INT_0 = Reflection.findFieldInClasses (VMX_TYPE, "INT_0");

	private Expression compileInvocation (Method method, Invoke e,
			ArgumentTransformations implicit, Applicability appl,
			Expression[] args, Scope scope, AST pos)
	{
		int option = appl.transformationAlternative;
		assert option >= 0;
		invokable = method;
		implicitOption = implicit.types[option];
		assert appl.varArity
			|| (implicitOption.length + args.length == method
				.getParameterCount ()) : method + " " + implicitOption.length + " " + args.length;
		argList = e.getFirstExpression ();
		boolean iter = implicit.isGenerator (option), aggr = implicit
			.isAggregate (option), filt = implicit.isFilter (option);
		if (iter)
		{
			e.setGenerator ();
		}
		if ((aggr || filt) && appl.array2Generator)
		{
			args[0] = new ArrayGenerator (args[0].getType ()
				.getComponentType (), ((BlockScope) scope).declareLocal (
				"array.", Member.FINAL, args[0].getType (), pos),
				((BlockScope) scope)
					.declareLocal ("counter.", 0, Type.INT, pos)).add (args[0]);
		}
		Expression ret = e;
		if (aggr || filt)
		{
			Type type = method.getReturnType ();
			boolean v;
			if ((v = (type.getTypeId () == TypeId.VOID))
				|| Reflection.equal (java.lang.reflect.Array.class, type))
			{
				args[0] = methodInvocationConversion (args[0], method
					.getParameterType (1), scope, Compiler.getAST (args[0]));
				type = args[0].getType ();
				if (!v)
				{
					type = type.getArrayType ();
				}
			}
			ret = new ExpressionList (type);
			Expression expr;
			if (aggr)
			{
				BlockScope bs = new BlockScope (scope, ret);
				Invoke finalize = (Invoke) e.clone ();
				if (!Reflection.isStatic (method))
				{
					Expression f = e.getFirstExpression ();
					e.setBranch (null);
					Local instance = bs.declareLocal ("instance.",
						Member.FINAL, f.getType (), pos);
					ret.add (instance.createSet ().add (f));
					e.add (instance.createGet ());
					finalize.add (instance.createGet ());
				}
				Local aggregate = bs.declareLocal ("aggr.", Member.FINAL,
					AGGREGATE_TYPE, pos);
				ret.add (aggregate.createSet ().add (
					new InvokeStatic (Reflection.findMethodInClasses (
						AGGREGATE_TYPE, "allocate"))
						.add (new ClassConst (type))));
				e.add (aggregate.createGet ());
				finalize.add (aggregate.createGet ());
				for (int i = 1; i < method.getParameterCount (); i++)
				{
					finalize.add (Expression.createConst (method
						.getParameterType (i), null));
				}
				MethodScope ms = bs.getMethodScope ();
				ms.enterBreakTarget (null);
				Block outer = new Block ();
				Block b = Block.createSequentialBlock ();
				b.add (e).add (
					new If ().add (
						new InvokeVirtual (Reflection.findMethodInClasses (
							AGGREGATE_TYPE, "isFinished")).add (aggregate
							.createGet ())).add (
						new Break (ms.getTargetId (null, false))));
				outer.add (b);
				outer.add (new InvokeVirtual (Reflection.getDeclaredMethod (
					AGGREGATE_TYPE, "setFinished")).add (aggregate
					.createGet ()));
				outer.add (finalize);
				ms.leave (outer);
				ret.add (outer);
				expr = new InvokeStatic (Reflection.getDeclaredMethod (
					AGGREGATE_TYPE, Reflection.getJVMPrefix (type) + "val"))
					.add (aggregate.createGet ());
				bs.receiveLocals ((BlockScope) scope, pos);
			}
			else
			{
				BlockScope bs = (BlockScope) scope;
				Local filter = bs.declareLocal ("filter.", Member.FINAL,
					FILTER_TYPE, pos);
				ret.add (filter.createSet ().add (
					new InvokeStatic (Reflection.getDeclaredMethod (
						FILTER_TYPE, "allocate")).add (new ClassConst (type))));
				e.add (filter.createGet ());
				expr = new FilterGuard (type, filter).add (e);
			}
			if ((expr.getType ().getTypeId () != type.getTypeId ())
				|| ((type.getTypeId () == TypeId.OBJECT) && !Reflection.equal (
					Type.OBJECT, type)))
			{
				expr = expr.cast (type);
			}
			ret.add (expr);
		}
		args = implicit.complete (method, option, args, appl.varArity, scope, pos);
		int n = appl.varArity ? method.getParameterCount () - 1 : args.length;
		assert n >= 0 : method;
		for (int i = 0; i < n; i++)
		{
			if ((i > 0) || !(iter || aggr || filt))
			{
				args[i] = methodInvocationConversion (args[i], method
					.getParameterType (i), scope, Compiler.getAST (args[i]));
				if ((i == 0) && Reflection.isCtor (method))
				{
					Type t = method.getDeclaringType ();
					if ((t.getDeclaringType () != null)
						&& !Reflection.isStatic (t))
					{
						args[i] = new CheckNonNull (args[i].getType ()).add (args[i]);
					}
				}
				e.add (args[i]);
			}
		}
		if (appl.varArity)
		{
			Type varArgsType = method.getParameterType (n);
			Expression array = new ArrayInit (varArgsType);
			varArgsType = varArgsType.getComponentType ();
			for (int i = n; i < args.length; i++)
			{
				array.add (args[i] = methodInvocationConversion (args[i],
					varArgsType, scope, Compiler.getAST (args[i])));
			}
			e.add (array);
		}
		argList = (argList == null) ? e.getFirstExpression () : argList
			.getNextExpression ();
		CClass amc = getClassForAccessMethod (method, scope, e
			.getFirstExpression ());
		if (amc != null)
		{
			e.useAccessMethod (amc);
		}
		setAST (ret, pos);
		return ret;
	}

	private static CClass getClassForAccessMethod (Member m, Scope scope,
			Expression instance)
	{
		if (Reflection.isPrivate (m))
		{
			if (Reflection.equal (m.getDeclaringType (), scope
				.getDeclaredType ()))
			{
				return null;
			}
			else
			{
				return (CClass) m.getDeclaringType ();
			}
		}
		else if (Reflection.isProtected (m))
		{
			TypeScope ts = TypeScope.get (scope);
			scope = ts;
			if (m.getDeclaringType ().getPackage ().equals (
				ts.getDeclaredType ().getPackage ()))
			{
				return null;
			}
			TypeScope dest = null;
			while (ts != null)
			{
				if (Reflection.isSupertypeOrSame (m.getDeclaringType (), ts
					.getDeclaredType ())
					&& ((instance == null) || Reflection.isStatic (m) || Reflection
						.isSupertypeOrSame (ts.getDeclaredType (), instance
							.getType ())))
				{
					dest = ts;
					if (dest == scope)
					{
						return null;
					}
				}
				ts = TypeScope.get (ts.getEnclosingScope ());
			}
			return dest.getDeclaredType ();
		}
		else
		{
			return null;
		}
	}

	Expression compileMethodInvocation (Type type, ExpressionFactory expr,
			String name, ArgumentTransformations implicit, Expression[] args,
			int flags, Scope scope, AST pos)
	{
		if (Reflection.isInvalid (type) || (args == null))
		{
			return null;
		}
		try
		{
			members.resetName (name, pos);
			members.addMatches (scope, type, flags
				| ((expr instanceof Super) ? Members.METHOD | Members.SUPER
					| Members.EXCLUDE_INTERFACES : Members.METHOD));
			return compileMethodInvocation (findMostSpecificMethod (implicit,
				args), expr, implicit, members.getApplicability (), args,
				scope, pos);
		}
		catch (RecognitionException e)
		{
			problems.add (e);
			return null;
		}
	}

	Expression compileMethodInvocation (Method m, ExpressionFactory factory,
			ArgumentTransformations implicit, Applicability appl,
			Expression[] args, Scope scope, AST pos)
	{
		Invoke invoke;
		Expression expr;
		if (Reflection.isStatic (m))
		{
			if ((factory instanceof Expression)
				&& !((Expression) factory).evaluatesWithoutSideeffect ())
			{
				expr = (Expression) factory;
			}
			else
			{
				expr = null;
			}
			invoke = new InvokeStatic (m);
		}
		else
		{
			if (factory != null)
			{
				expr = factory.createExpression (scope, pos);
			}
			else
			{
				problems.addSemanticError (I18N.msg (
					ProblemReporter.NONSTATIC_IN_STATIC_CONTEXT, Members
						.getMemberTypeDescription (m), Reflection
						.getDescription (m)), pos);
				expr = new Expression (m.getDeclaringType ());
			}
			if ((expr instanceof Super) || Reflection.isPrivate (m))
			{
				invoke = new InvokeSpecial (m);
				if (Reflection.isAbstract (m))
				{
					problems.addSemanticError (I18N.msg (
						ProblemReporter.ABSTRACT_METHOD_INVOCATION, Reflection
							.getDescription (m)), pos);
				}
			}
			else
			{
				invoke = new InvokeVirtual (m);
			}
			invoke.add (expr);
			expr = null;
		}
		Expression e = compileInvocation (m, invoke, implicit, appl, args,
			scope, pos);
		return (expr == null) ? e : new ExpressionList (e.getType ())
			.add (expr).add (e);
	}

	/**
	 * Compiles a method invocation expression whose name (a simple or qualified name)
	 * is described by <code>tree</code>. 
	 * 
	 * @param tree the method name
	 * @param ax the possible argument transformations for the invocation
	 * @param args explicit arguments of the invocation expression
	 * @param flags flags to pass to {@link Scope#findMembers(String, int, Members)}
	 * @param scope the scope in which the invocation occurs
	 * @param pos the source code location to use for messages
	 * @return the compiled method invocation expression
	 */
	Expression compileMethodInvocation (AST tree, ArgumentTransformations ax,
			Expression[] args, int flags, Scope scope, AST pos)
	{
		if (args == null)
		{
			return null;
		}
		switch (tree.getType ())
		{
			case IDENT:
				String name = tree.getText ();
				members.resetName (name, pos);
				flags |= Members.METHOD;
				if ((flags & Members.OPERATOR_METHODS) != 0)
				{
					members.addMatches (scope, args[0].getType (), flags | Members.SHIFT_METHODS);
					for (int i = 0; i < args.length; i++)
					{
						members.addMatches (scope, args[i].getType (), flags);
					}
				}
				members.addMatches (scope, flags);
				Method m;
				Applicability appl;
				try
				{
					m = findMostSpecificMethod (ax, args);
					appl = members.getApplicability ();
				}
				catch (RecognitionException e)
				{
					problems.add (e);
					return null;
				}
				Expression expr;
				if (m instanceof ShiftedMethod)
				{
					m = ((ShiftedMethod) m).getMethod ();
					expr = args[0];
					System.arraycopy (args, 1, args = new Expression[args.length - 1], 0, args.length);
				}
				else
				{
					expr = compileInstance (m, members.getScopeForResult (), scope, pos);
				}
				return compileMethodInvocation (m, expr, ax, appl, args, scope, pos);
			case DOT:
				tree = tree.getFirstChild ();
				Object n = resolver.resolveExpressionOrTypeName (tree, scope);
				if (n == null)
				{
					return null;
				}
				tree = tree.getNextSibling ();
				assert tree.getType () == IDENT;
				if (n instanceof Expression)
				{
					return compileMethodInvocation ((Expression) n, tree
						.getText (), ax, args, flags, scope, pos);
				}
				else
				{
					return compileMethodInvocation ((Type) n, null, tree
						.getText (), ax, args, flags, scope, pos);
				}
			default:
				throw new AssertionError ();
		}
	}

	Expression compileMethodInvocation (ExpressionFactory expr, String name,
			ArgumentTransformations implicit, Expression[] args, int flags,
			Scope scope, AST pos)
	{
		if ((expr == null) || (args == null))
		{
			return null;
		}
		return compileMethodInvocation (expr.getType (), expr, name, implicit,
			args, flags, scope, pos);
	}

	private static class ConstructorImplicit extends ArgumentTransformations
	{
		private Expression enclosing;

		ConstructorImplicit ()
		{
			super (new Type[1][1], null);
		}

		void setEnclosing (Expression e)
		{
			types[0][0] = e.getType ();
			this.enclosing = e;
		}

		@Override
		Expression createImplicitArgument (int option, int index, Type type, Scope scope, AST pos)
		{
			return enclosing;
		}

		@Override
		boolean matchesImplicit (Type param, int option, int implIndex,
				Type implType)
		{
			return Reflection.isSuperclassOrSame (param, implType);
		}
	}

	private final ConstructorImplicit constrImplicit = new ConstructorImplicit ();

	private ArgumentTransformations getImplArgsForConstructor (Type type,
			Expression enclosing, Scope scope, AST pos,
			String qualifiedStaticMsg)
	{
		if ((type.getDeclaringType () != null) && !Reflection.isStatic (type))
		{
			if (enclosing == null)
			{
				enclosing = compileInstance (type, null, scope, pos);
			}
			else if (!enclosing.hasType (type.getDeclaringType ()))
			{
				problems.addSemanticError (I18N.msg (
					ProblemReporter.UNEXPECTED_TYPE, enclosing.getType ()
						.getName (), type.getDeclaringType ().getName ()), pos
					.getFirstChild ());
				return null;
			}
			constrImplicit.setEnclosing (enclosing);
			return constrImplicit;
		}
		if (enclosing != null)
		{
			problems.addSemanticError (I18N.msg (qualifiedStaticMsg, type
				.getName ()), pos);
			return null;
		}
		return ArgumentTransformations.NO_IMPLICIT_ARGS;
	}

	private void compileLocalClassConstructor (Scope scope, Invoke e)
	{
		Type type = e.getType ();
		if ((type.getModifiers () & Member.LOCAL_CLASS) != 0)
		{
			BlockScope bs = (BlockScope) scope;
			TypeScope localClassScope = bs.getTypeScope (type);
			MethodScope ms = MethodScope.get (bs);
			boolean later = localClassScope.encloses (bs);
			if (!later)
			{
				later = ms.isConstructor ();
				ObjectList<Local> locals = localClassScope.getEnclosingLocals ();
				for (int i = 0; i < locals.size (); i++)
				{
					Expression loc = locals.get (i).createExpression (bs, null);
					if (!later)
					{
						e.add (loc);
					}
				}
			}
			if (later)
			{
				setBlockScope (e, bs);
				TypeScope.getNonlocal (localClassScope)
					.addIncompleteConstructorInvocation (e);
			}
		}
	}

	Expression compileConstructorInvocation (Method constructor,
			Expression[] args, Scope scope, AST pos)
	{
		Type type = constructor.getDeclaringType ();
		assert !Reflection.isAbstract (type);
		if (args == null)
		{
			return new Expression (type);
		}
		ArgumentTransformations impl = getImplArgsForConstructor (type, null,
			scope, pos, ProblemReporter.QUALIFIED_NEW_OF_STATIC);
		if (impl == null)
		{
			return new Expression (type);
		}
		Invoke e = new InvokeSpecial (constructor);
		e.add (new New (type));
		Applicability appl = new Applicability ();
		impl.getApplicableOption (constructor, appl, args, scope);
		e = (Invoke) compileInvocation (constructor, e, impl, appl, args,
			scope, pos);
		compileLocalClassConstructor (scope, e);
		return e;
	}

	Expression compileConstructorInvocation (Expression instance,
			Expression enclosing, Expression[] args, Scope scope, AST pos,
			boolean alternative)
	{
		Type type = instance.getType ();
		if (Reflection.isInvalid (type))
		{
			return null;
		}
		if (args == null)
		{
			return new Expression (type);
		}
		if (!alternative)
		{
			if (Reflection.isAbstract (type))
			{
				problems.addSemanticError (I18N.msg (
					ProblemReporter.ABSTRACT_INSTANTIATION, type.getName ()),
					pos);
			}
		}
		try
		{
			ArgumentTransformations impl = getImplArgsForConstructor (type,
				enclosing, scope, pos, ProblemReporter.QUALIFIED_NEW_OF_STATIC);
			if (impl == null)
			{
				return new Expression (type);
			}
			members.resetName ("<init>", pos);
			members.addMatches (scope, type, Members.CONSTRUCTOR
				| Members.DECLARED_ONLY);
			Method c = findMostSpecificMethod (impl, args);
			Applicability appl = members.getApplicability ();
			Invoke e = new InvokeSpecial (c);
			e.add (instance);
			e = (Invoke) compileInvocation (c, e, impl, appl, args, scope, pos);
			compileLocalClassConstructor (scope, e);
			return e;
		}
		catch (RecognitionException e)
		{
			problems.add (e);
			return new Expression (type);
		}
	}

	Expression compileSuperConstructorInvocation (Expression instance,
			Expression enclosing, Expression[] args, BlockScope scope, AST pos)
	{
		Type type = scope.getDeclaredType ();
		if ((args == null) || Reflection.isInvalid (type) || Reflection.isInvalid (type.getSupertype ()))
		{
			return null;
		}
		try
		{
			ArgumentTransformations impl = getImplArgsForConstructor (type
				.getSupertype (), enclosing, scope, pos,
				ProblemReporter.QUALIFIED_SUPER_OF_STATIC);
			if (impl == null)
			{
				return null;
			}
			members.resetName ("<init>", pos);
			members.addMatches (scope, type, Members.CONSTRUCTOR
				| Members.SUPER | Members.DECLARED_ONLY);
			Method c = findMostSpecificMethod (impl, args);
			Applicability appl = members.getApplicability ();
			Invoke e = new InvokeSpecial (c);
			e.add (instance);
			e = (Invoke) compileInvocation (c, e, impl, appl, args, scope, pos);
			compileLocalClassConstructor (scope, e);
			return e;
		}
		catch (RecognitionException e)
		{
			problems.add (e);
			return null;
		}
	}

	public Expression compileFieldExpression (Field field, Expression expr,
			Scope scope, AST pos)
	{
		if ((expr == null) && !Reflection.isStatic (field))
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.NONSTATIC_IN_STATIC_CONTEXT, Members
					.getMemberTypeDescription (field), Reflection
					.getDescription (field)), pos);
			expr = new Expression (field.getDeclaringType ());
		}
		if ((expr != null)
			&& ((field.getModifiers () & (Member.STATIC | Member.CONSTANT)) != 0)
			&& expr.evaluatesWithoutSideeffect ())
		{
			expr = null;
		}
		Expression e;
		if ((field.getModifiers () & Member.CONSTANT) != 0)
		{
			try
			{
				e = Expression.createConst (field.getType (), Reflection.get (
					null, field));
			}
			catch (IllegalAccessException iae)
			{
				throw new AssertionError (iae.getMessage ());
			}
			if (expr != null)
			{
				e = new ExpressionList (field.getType ()).add (expr).add (e);
			}
			return e;
		}
		e = new GetField (field).add (expr);
		fieldAccessed (field);
		CClass amc = getClassForAccessMethod (field, scope, e
			.getFirstExpression ());
		if (amc != null)
		{
			((GetField) e).useAccessMethod (amc);
		}
		return e;
	}


	Expression compileInstance (Type enclosingType, BlockScope s, AST pos,
			boolean forSuper)
	{
		Expression e;
		MethodScope ms = s.getMethodScope ();
		if (ms.isStatic ())
		{
			problems.addSemanticError (I18N,
				forSuper ? ProblemReporter.SUPER_IN_STATIC
						: ProblemReporter.THIS_IN_STATIC, pos);
			e = new Expression ((enclosingType == null) ? ms.getDeclaredType ()
					: enclosingType);
		}
		else if ((enclosingType != null) && Reflection.isInvalid (enclosingType))
		{
			return new Expression (Type.INVALID);
		}
		else
		{
			e = ms.createThis ();
			TypeScope c = TypeScope.get (ms);
			if ((enclosingType != null)
				&& !Reflection.equal (c.getDeclaredType (), enclosingType))
			{
				if (ms.enclosingInstance != null)
				{
					e = ms.enclosingInstance.createGet ();
					c = TypeScope.get (c.getEnclosingScope ());
				}
				while (!Reflection.equal (c.getDeclaredType (), enclosingType))
				{
					if (c.isStatic ())
					{
						problems.addSemanticError (I18N.msg (
							ProblemReporter.NO_ENCLOSING_INSTANCE,
							enclosingType.getName ()), pos);
						e = new Expression (enclosingType);
						break;
					}
					e = compileFieldExpression (c.enclosingInstance, e, s, pos);
					c = TypeScope.get (c.getEnclosingScope ());
				}
			}
		}
		return forSuper ? new Super (e.getType ()).add (e) : e;
	}

	public Expression compileInstance (Member m, Scope declaring, Scope s, AST pos)
	{
		if (Reflection.isStatic (m))
		{
			return null;
		}
		if (declaring instanceof InstanceScope)
		{
			return ((InstanceScope) declaring).createExpression (s, pos);
		}
		Type declType = m.getDeclaringType ();
		MethodScope ms;
		if (!(s instanceof BlockScope)
			|| (ms = ((BlockScope) s).getMethodScope ()).isStatic ())
		{
			problems.addSemanticError (I18N.msg (
				ProblemReporter.NONSTATIC_IN_STATIC_CONTEXT, Members
					.getMemberTypeDescription (m), m.getSimpleName ()), pos);
			return new Expression (declType);
		}
		Expression e = ms.createThis ();
		TypeScope c = TypeScope.get (ms);
		if ((declaring != null) ? (c == declaring) : Reflection
			.isSuperclassOrSame (declType, c.getDeclaredType ()))
		{
			return e;
		}
		if (ms.enclosingInstance != null)
		{
			e = ms.enclosingInstance.createGet ();
			c = TypeScope.get (c.getEnclosingScope ());
		}
		while ((declaring != null) ? (c != declaring) : !Reflection
			.isSuperclassOrSame (declType, c.getDeclaredType ()))
		{
			if (c.isStatic ())
			{
				problems.addSemanticError (I18N.msg (
					ProblemReporter.NO_ENCLOSING_INSTANCE_FOR_MEMBER, Members
						.getMemberTypeDescription (m), m.getSimpleName (),
					declType.getName ()), pos);
				return new Expression (declType);
			}
			e = compileFieldExpression (c.enclosingInstance, e, s, pos);
			c = TypeScope.get (c.getEnclosingScope ());
		}
		return e;
	}

	private void setMembers (AST ident, Scope scope, int flags)
	{
		if (ident.getType () == DOT)
		{
			ident = ident.getFirstChild ();
			Object n = resolver.resolveExpressionOrTypeName (ident, scope);
			ident = ident.getNextSibling ();
			members.resetName (ident);
			if (n instanceof Expression)
			{
				members.addMatches (scope, ((Expression) n).getType (), flags);
			}
			else
			{
				members.addMatches (scope, (Type) n, flags);
			}
		}
		else
		{
			members.resetName (ident);
			members.addMatches (scope, flags);
		}
	}

	void compilePattern (AST tree, Expression[] args, AST pos,
			PatternBuilder pb, AST label)
	{
		resolution = PREDICATE_RES;
		setMembers (tree, pb.getScope (), Members.PREDICATE);
		resolutionArgs = args;
		try
		{
			PatternWrapper p = (PatternWrapper) members.resolve (this);
			if (p == null)
			{
				return;
			}
			int s = (members.getApplicability ().transformationAlternative > 0) ? 1
					: 0;
			if (s > 0)
			{
				Expression[] a = new Expression[args.length + 1];
				System.arraycopy (args, 0, a, 1, args.length);
				a[0] = null;
				args = a;
			}
			for (int i = s; i < args.length; i++)
			{
				if (args[i].etype != TypeId.OBJECT)
				{
					args[i] = methodInvocationConversion (args[i], p
						.getParameterType (i), pb.getScope (), Compiler.getAST (args[i]));
				}
			}
			if (s > 0)
			{
				pb.addNodePattern (label, p, ArgumentDescription
					.create (args), pos);
			}
			else
			{
				pb.addPattern (label, p, ArgumentDescription.create (args), pos);
			}
		}
		catch (RecognitionException e)
		{
			problems.add (e);
		}
	}

	private static final class EPImplicit extends ArgumentTransformations
	{
		private static final Type[] EDGE_PRED_ARGS = new Type[2];
		private static final Type[] EDGE_PRED_DIR_ARGS = {null, null,
				Type.BOOLEAN};

		private final boolean undirected;

		EPImplicit (boolean undirected)
		{
			super (undirected ? new Type[][] {FIRST_TERM, EDGE_PRED_DIR_ARGS}
					: new Type[][] {FIRST_TERM, EDGE_PRED_ARGS, EDGE_PRED_DIR_ARGS});
			this.undirected = undirected;
		}

		boolean object;

		@Override
		int getApplicableOption (Signature m, Applicability applOut,
				Expression[] args, Scope scope)
		{
			PatternWrapper p = (PatternWrapper) m;
			int in = p.getInParameter (), out = p.getOutParameter ();
			if ((in < 0) || (out < 0))
			{
				return -1;
			}
			object = in == out;
			if (object && !p.isFirstInOut ())
			{
				return -1;
			}
			for (int i = 0; i < indices.length; i++)
			{
				int[] a = indices[i];
				a[0] = in;
				if (a.length >= 2)
				{
					a[1] = out;
					if (a.length == 3)
					{
						a[2] = out + 1;
					}
				}
			}
			return super.getApplicableOption (m, applOut, args, scope);
		}
		
		@Override
		boolean isApplicable (Signature m, Applicability out,
				int alternative, Expression[] args, Scope scope)
		{
			return (!object || (alternative == 0))
				&& super.isApplicable (m, out, alternative, args, scope);
		}

		@Override
		Expression createImplicitArgument (int option, int index, Type type, Scope scope, AST pos)
		{
			return (index == 2) ? new BooleanConst (undirected) : null;
		}
	}

	private final EPImplicit directedEPImplicit = new EPImplicit (false);
	private final EPImplicit undirectedEPImplicit = new EPImplicit (true);

	void compileEdgePattern (AST tree, Expression[] args, AST pos,
			PatternBuilder pb, BlockScope scope, AST label, EdgeDirection dir)
	{
		resolution = (dir == EdgeDirection.UNDIRECTED) ? UNDIR_EDGE_PREDICATE_RES
				: EDGE_PREDICATE_RES;
		setMembers (tree, scope, Members.PREDICATE);
		EPImplicit impl = (dir == EdgeDirection.UNDIRECTED) ? undirectedEPImplicit : directedEPImplicit;
		resolutionImplicit = impl;
		resolutionArgs = args;
		try
		{
			PatternWrapper p = (PatternWrapper) members.resolve (this);
			if (p == null)
			{
				return;
			}
			args = resolutionImplicit.complete (p,
				members.getApplicability ().transformationAlternative, args,
				false, scope, pos);
			for (int i = 0; i < args.length; i++)
			{
				if ((args[i] != null) && (args[i].etype != TypeId.OBJECT))
				{
					args[i] = methodInvocationConversion (args[i], p
						.getParameterType (i), pb.getScope (), Compiler.getAST (args[i]));
				}
			}
			if (impl.object)
			{
				pb.addEdgePattern (label, p, ArgumentDescription.create (args), dir, pos);
			}
			else
			{
				pb.addRelationPattern (label, p, ArgumentDescription.create (args),
					dir == EdgeDirection.BACKWARD, pos);
			}
		}
		catch (RecognitionException e)
		{
			problems.add (e);
		}
	}

	private static final class MEPImplicit extends ArgumentTransformations
	{
		private static final Type[] THIS_PATH_ARGS = {};
		private static final Type[] THIS_GPATH_ARGS = {null};
		private static final Type[] THIS_PATH_DIR_ARGS = {Type.BOOLEAN};
		private static final Type[] THIS_GPATH_DIR_ARGS = {null, Type.BOOLEAN};
		private static final Type[] THIS_PATH_T_ARGS = {Type.CLASS};
		private static final Type[] THIS_GPATH_T_ARGS = {null, Type.CLASS};
		private static final Type[] THIS_PATH_DIR_T_ARGS = {Type.BOOLEAN,
				Type.CLASS};
		private static final Type[] THIS_GPATH_DIR_T_ARGS = {null,
				Type.BOOLEAN, Type.CLASS};

		private static final Type[] PATH_ARGS = {null};
		private static final Type[] GPATH_ARGS = {null, null};
		private static final Type[] PATH_DIR_ARGS = {null, Type.BOOLEAN};
		private static final Type[] GPATH_DIR_ARGS = {null, null, Type.BOOLEAN};
		private static final Type[] PATH_T_ARGS = {null, Type.CLASS};
		private static final Type[] GPATH_T_ARGS = {null, null, Type.CLASS};
		private static final Type[] PATH_DIR_T_ARGS = {null, Type.BOOLEAN,
				Type.CLASS};
		private static final Type[] GPATH_DIR_T_ARGS = {null, null,
				Type.BOOLEAN, Type.CLASS};

		private static final Type[] THIS_REL_ARGS = {null};
		private static final Type[] THIS_REL_DIR_ARGS = {null, Type.BOOLEAN};

		private static final Type[] REL_ARGS = {null, null};
		private static final Type[] REL_DIR_ARGS = {null, null, Type.BOOLEAN};

		private static final Type[][] THIS_PATH = {THIS_PATH_ARGS,
				THIS_GPATH_ARGS, THIS_PATH_DIR_ARGS, THIS_GPATH_DIR_ARGS,
				THIS_PATH_T_ARGS, THIS_GPATH_T_ARGS, THIS_PATH_DIR_T_ARGS,
				THIS_GPATH_DIR_T_ARGS};

		private static final Type[][] PATH = {PATH_ARGS, GPATH_ARGS,
				PATH_DIR_ARGS, GPATH_DIR_ARGS, PATH_T_ARGS, GPATH_T_ARGS,
				PATH_DIR_T_ARGS, GPATH_DIR_T_ARGS};

		private static final Type[][] THIS_REL = {THIS_REL_ARGS, THIS_REL_DIR_ARGS};

		private static final Type[][] REL = {REL_ARGS, REL_DIR_ARGS};

		boolean undirected;
		boolean useThis;

		boolean relation;
		boolean path;

		MEPImplicit ()
		{
			super (null, null);
		}

		@Override
		boolean isGenerator (int option)
		{
			return !relation && ((option & 1) != 0);
		}

		@Override
		int getApplicableOption (Signature m, Applicability out,
				Expression[] args, Scope scope)
		{
			switch (XMethod.getGeneratorOrReturnType ((Method) m).getTypeId ())
			{
				case TypeId.VOID:
					return -1;
				case TypeId.BOOLEAN:
					types = useThis ? THIS_REL : REL;
					relation = true;
					path = false;
					break;
				default:
					types = useThis ? THIS_PATH : PATH;
					relation = false;
					path = true;
					break;
			}
			return super.getApplicableOption (m, out, args, scope);
		}

		@Override
		Expression createImplicitArgument (int option, int index, Type type, Scope scope, AST pos)
		{
			if (types[option][index] == Type.BOOLEAN)
			{
				return new BooleanConst (undirected);
			}
			else if (types[option][index] == Type.CLASS)
			{
				return PatternBuilder.createTargetTypeArgument ();
			}
			else if ((index == 0) && isGenerator (option))
			{
				return null;
			}
			else
			{
				return PatternBuilder.createArgument (type);
			}
		}
	}

	private final MEPImplicit mepImplicit = new MEPImplicit ();

	void compileMethodEdgePattern (AST tree, Expression[] args, AST pos,
			PatternBuilder pb, BlockScope scope, AST label, EdgeDirection dir,
			boolean useThis)
	{
		mepImplicit.undirected = dir == EdgeDirection.UNDIRECTED;
		mepImplicit.useThis = useThis;
		Expression e = compileMethodInvocation (tree, mepImplicit, args, 0,
			scope, pos);
		if (e != null)
		{
			if (mepImplicit.relation)
			{
				pb.addRelation (label, e, dir == EdgeDirection.BACKWARD, pos);
			}
			else if (mepImplicit.path)
			{
				pb.addPathExpression (label, e,
					dir == EdgeDirection.BACKWARD, pos);
			}
			else
			{
				pb.addEdge (label, dir, e, tree);
			}
		}
	}

	void compileMethodEdgePattern (Expression expr, String name,
			Expression[] args, AST pos, PatternBuilder pb, AST label, EdgeDirection dir)
	{
		mepImplicit.undirected = dir == EdgeDirection.UNDIRECTED;
		mepImplicit.useThis = false;
		Expression e = compileMethodInvocation (expr, name, mepImplicit, args,
			0, pb.getScope (), pos);
		if (e != null)
		{
			if (mepImplicit.relation)
			{
				pb.addRelation (label, e, dir == EdgeDirection.BACKWARD, pos);
			}
			else if (mepImplicit.path)
			{
				pb.addPathExpression (label, e,
					dir == EdgeDirection.BACKWARD, pos);
			}
			else
			{
				pb.addEdge (label, dir, e, pos);
			}
		}
	}

	Expression[] resolveOpenArguments (Expression[] args, Scope scope)
	{
		if (args != null)
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i] instanceof OpenArgument)
				{
					args[i] = resolver.resolveExpressionName (getAST (args[i]), scope);
					if (args[i] == null)
					{
						return null;
					}
				}
			}
		}
		return args;
	}

	Local pushProducer (BlockScope scope, AST pos)
	{
		ProduceScope ps = ProduceScope.get (scope);
		if (ps == null)
		{
			return null;
		}
		ps.push ();
		return (Local) ps.getInstance ();
	}


	Local popProducer (BlockScope scope, AST pos)
	{
		ProduceScope ps = ProduceScope.get (scope);
		if (ps == null)
		{
			return null;
		}
		ps.pop ();
		return (Local) ps.getInstance ();
	}


	Expression popAndJoinProducer (BlockScope scope, AST pos)
	{
		ProduceScope ps = ProduceScope.get (scope);
		if (ps == null)
		{
			return null;
		}
		ExpressionFactory old = ps.getInstance ();
		ps.pop ();
		Local i = (Local) ps.getInstance ();
		if (old == i)
		{
			return null;
		}
		Expression e = i.createSet (scope, pos);
		e.add (assignmentConversion (old.createExpression (scope, pos), i.getType (), scope, pos));
		return e;
	}

	Expression setProducer (BlockScope scope, Expression prod, AST pos)
	{
		if (prod == null)
		{
			return null;
		}
		if (prod.getType ().getTypeId () == TypeId.VOID)
		{
			problems.addSemanticError (I18N, ProblemReporter.VOID_PRODUCER, pos);
			return new Expression (scope.getProduceScope ().getInstance ().getType ());
		}
		Local p = scope.declareLocal ("prod.", Member.FINAL, prod.getType (), pos);
		scope.getProduceScope ().setInstance (p);
		return p.createSet (scope, pos).add (prod);
	}

	private final ObjectList stack = new ObjectList (20, false);

	void compileInitializers ()
	{
		try
		{
			for (FieldInitializer f : initializersToCompile.values ())
			{
				compile (f);
			}
		}
		catch (antlr.RecognitionException e)
		{
			throw new WrapException (e);
		}
	}

	private void compile (FieldInitializer init)
			throws antlr.RecognitionException
	{
		if (!init.compiled)
		{
			CompilerOptions opt = options;
			TypeScope ts = currentTypeScope;
			CompilationUnitScope cs = currentCompilationUnitScope;
			Package p = currentPackage;
			options = init.options;
			init.compiled = true;
			
			currentTypeScope = TypeScope.get (init.scope);
			currentCompilationUnitScope = CompilationUnitScope.get (currentTypeScope);
			currentPackage = currentCompilationUnitScope.getPackage ();
			init.expr = compileConstants (((Compiler) this).initializer (
				init.ast, init.scope, init.field.getType ()));
			if (init.expr.isPrimitiveOrStringConstant ()
				&& Reflection.isPrimitiveOrString (init.field.getType ()))
			{
				Expression e = assignmentConversion (init.expr,
					init.field.getType (), init.scope, init.ast);
				if (e.isPrimitiveOrStringConstant ())
				{
					init.field.setConstant (e.evaluateAsObject (null));
					init.expr = null;
				}
				else
				{
					init.expr = e;
				}
			}
			options = opt;
			currentTypeScope = ts;
			currentCompilationUnitScope = cs;
			currentPackage = p;
		}
	}

	private Expression compileConstants (Expression e)
			throws antlr.RecognitionException
	{
		if (e instanceof GetField)
		{
			Field f = FieldDecorator.undecorate (((GetField) e).getField ());
			if ((f instanceof XField)
				&& ((f.getModifiers () & Member.CONSTANT) == 0))
			{
				FieldInitializer init = initializersToCompile.get (f);
				if (init != null)
				{
					compile (init);
				}
			}
			if ((f.getModifiers () & Member.CONSTANT) != 0)
			{
				try
				{
					return Expression.createConst (f.getType (), Reflection
						.get (null, f));
				}
				catch (IllegalAccessException ex)
				{
					throw (antlr.RecognitionException) new antlr.RecognitionException ()
						.initCause (ex);
				}
			}
		}
		Expression next = e.getFirstExpression ();
		if (next == null)
		{
			return e;
		}
		boolean constant = true;
		while (next != null)
		{
			Expression f = next;
			next = next.getNextExpression ();
			Expression c = compileConstants (f);
			if (c != f)
			{
				c.substitute (f);
			}
			if (!c.isPrimitiveOrStringConstant ())
			{
				constant = false;
			}
		}
		return constant ? e.toConst () : e;
	}

	private Block compileIterations (Expression e, Block sblock)
	{
		if ((e instanceof Block) && ((Block) e).isSequentialBlock ())
		{
			sblock = (Block) e;
		}
		Expression c = e.getFirstExpression ();
		Block breakTarget = sblock;
		while (c != null)
		{
			Expression n = c.getNextExpression ();
			sblock = compileIterations (c, sblock);
			c = n;
		}
		if (!(e instanceof Generator)
			|| (((Generator) e).getGeneratorType () == Generator.NONE))
		{
			return sblock;
		}
		MethodScope ms = MethodScope.get (e);

		if ((e instanceof Invoke) && (e.getAxisParent () instanceof Yield)
			&& (e.etype == XMethod.getGeneratorType (ms.getMethod ()).getTypeId ()))
		{
			Yield y = (Yield) e.getAxisParent ();
			e.removeFromChain ();
			((Invoke) e).receiveConsumer (ms.consumer.createGet ());
			e.substitute (y);
			ms.createVMXFrame ();
			return sblock;
		}

		boolean nonlocal = ((Generator) e).getGeneratorType () == Generator.NONLOCAL;

		MethodScope scope = nonlocal ? new MethodScope (ms) : null;

		Block innerBlock = Block.createSequentialBlock ();
		sblock = innerBlock;
		Expression iterationStart = e, parent = (Expression) e.getAxisParent ();
		boolean required = parent.isRequired (e.getIndex ());
		Local assignedLocal = (parent instanceof AssignLocal) ? ((AssignLocal) parent)
			.getLocal (0)
				: null;
		stack.clear ();
		while (true)
		{
			parent = (Expression) iterationStart.getAxisParent ();
			if ((parent instanceof Block)
				&& ((Block) parent).isSequentialBlock ())
			{
				assert parent.getBranch () == iterationStart;
				break;
			}
			int i = iterationStart.getIndex ();
			if (!parent.allowsIteration (i))
			{
				problems.addSemanticError (I18N,
					ProblemReporter.NO_ITERATION_TARGET, Compiler.getAST (e));
				return sblock;
			}
			c = iterationStart;
			while (--i >= 0)
			{
				c = (Expression) c.getPredecessor ();
				if ((c instanceof Constant) || (c instanceof New))
				{
					continue;
				}
				if (c instanceof GetField)
				{
					GetField g = (GetField) c;
					if ((g.getExpressionCount () == 0)
						&& ((g.getField ().getModifiers () & (Member.STATIC | Member.FINAL))
							== (Member.STATIC | Member.FINAL)))
					{
						continue;
					}
				}
				if ((((int) c.lval & Compiler.EXPR_FINAL) == 0) || (nonlocal && !(c instanceof LocalValue)))
				{
					stack.push (Boolean.valueOf (parent.isRequired (i)));
					stack.push (c);
				}
			}
			iterationStart = parent;
		}

		c = parent.getFirstExpression ();
		parent.setBranch (null);
		innerBlock.add (c);

		while (stack.size > 0)
		{
			c = (Expression) stack.pop ();
			if (stack.pop () == Boolean.TRUE)
			{
				assert c.getType ().getTypeId () != TypeId.VOID;
				Local l = ms.declareLocal ("param.", Member.FINAL,
					c.getType (), null);
				Expression n = l.createGet (), a = l.createSet ();
				n.substitute (c);
				a.setBranch (c);
				parent.add (a);
			}
			else
			{
				c.removeFromChain ();
				parent.add (c);
			}
		}
		Expression iteratedBlock = new Block (), toReplace = e, iteratorReplacement;

		if (e.etype == TypeId.VOID)
		{
			iteratorReplacement = new Block ();
		}
		else if (nonlocal)
		{
			iteratorReplacement = scope.declareParameter ("value.",
				Member.FINAL, e.getType ()).createGet ();
		}
		else if (!required)
		{
			iteratorReplacement = new Block ();
			iteratedBlock.add (new Pop (e.getType ()));
		}
		else
		{
			Local l;
			if (assignedLocal == null)
			{
				l = ms.declareLocal ("val.", Member.FINAL, e.getType (), null);
			}
			else
			{
				l = assignedLocal;
				toReplace = (Expression) e.getAxisParent ();
				new Expression ().substitute (e);
			}
			iteratedBlock.add (l.createSet ().add (
				new Pop (e.getType ())));
			iteratorReplacement = l.createGet ();
		}
		iteratorReplacement.substitute (toReplace);
		iteratedBlock.add (innerBlock);

		if (nonlocal)
		{
			Local rd = ms.declareLocal ("rd.", Member.FINAL, DESCRIPTOR_TYPE,
				null);
			XMethod method = scope.createAndDeclareMethod (ms.getMethod ().getName (), Type.VOID);
			parent.add (
				rd.createSet ().add (
					new GetDescriptor (method, -1))).add (e).add (
				new DisposeDescriptor ().add (rd.createGet ()));
			((NonlocalGenerator) e).receiveRoutine (TypeScope.get (ms), rd
				.createGet ());
			e.add (method);
			scope.addExpression (iteratedBlock);
		}
		else
		{
			parent.add (e);
			e.add (iteratedBlock);
		}
		if (!breakTarget.isInitialized ())
		{
			ms.enterBreakTarget (null);
			ms.getTargetId (null, false);
			ms.leave (breakTarget);
		}
		((Generator) e).setBreakTarget (breakTarget);
		return sblock;
	}

	public void finish (TypeScope scope, AST pos)
	{
		ObjectList cs = scope.getConstructorScopes ();
		ObjectList<Local> args = scope.getEnclosingLocals ();
		for (int i = 0; i < cs.size (); i++)
		{
			MethodScope ms = (MethodScope) cs.get (i);
			for (int j = 0; j < args.size (); j++)
			{
				Local l = args.get (j);
				ms.prependExpression (new AssignField (scope
					.getFieldForEnclosingLocal (l), Assignment.SIMPLE).add (
					ms.createThis ()).add (
					ms.getParameterForEnclosingLocal (l).createGet ()));
			}
		}

		ObjectList constrs = scope.getIncompleteConstructorInvocations ();
		for (int i = 0; i < constrs.size (); i++)
		{
			Invoke e = (Invoke) constrs.get (i);
			BlockScope bs = getBlockScope (e);
			TypeScope localClassScope = bs.getTypeScope (e.getType ());
			args = localClassScope.getEnclosingLocals ();
			for (int j = 0; j < args.size (); j++)
			{
				e.add (args.get (j).createExpression (bs, null));
			}
		}

		ObjectList mss = scope.getAllContainedMethodScopes ();
		for (int i = 0; i < mss.size (); i++)
		{
			MethodScope ms = (MethodScope) mss.get (i);
			if (ms.isConstructor ())
			{
				ms.getMethod ().updateParameters ();
			}
		}
		for (int i = 0; i < mss.size (); i++)
		{
			compileMethod ((MethodScope) mss.get (i));
		}
	}

	void compileMethod (MethodScope ms)
	{
		if (!problems.containsErrors ())
		{
			//			ms.getMethod ().dumpTree ();
			compileIterations (ms.getBlock (), null);
			//			ms.getMethod ().dumpTree ();
			ms.complete ();
			//			ms.dumpLocals ();
		}
	}

	void createModuleMethods (ClassInfo info, Type[] types)
	{
		MethodScope pcs = info.predicateScope.createMethodScope (Member.PUBLIC
			| MOD_CONSTRUCTOR);
		info.predicateConstructorScope = pcs;
		MethodScope sig = info.predicateScope.createMethodScope (Member.STATIC);
		info.signatureScope = sig;
		Local node = sig.declareParameter ("node", 0, info.scope.getDeclaredType ());
		node.getDeclaredAnnotations ()
			.push (new AnnotationImpl (UserDefinedPattern.In.class), new AnnotationImpl (UserDefinedPattern.Out.class));
		info.getterMethods = new MethodScope[TypeId.TYPE_COUNT];
		info.switches = new Switch[TypeId.TYPE_COUNT];
		info.objects = new Local[TypeId.TYPE_COUNT];
		for (int i = 0; i < types.length; i++)
		{
			Type t = types[i];
			int tid = t.getTypeId ();
			sig.declareParameter ("f" + i, 0, t);
			if (info.getterMethods[tid] == null)
			{
				MethodScope g = info.predicateScope
					.createMethodScope (Member.PROTECTED);
				info.getterMethods[tid] = g;
				info.objects[tid] = g
					.declareParameter ("o", 0, Type.OBJECT);
				Local index = g.declareParameter ("i", 0, Type.INT);
				g.createAndDeclareMethod ("get"
					+ Reflection.getTypeSuffix (tid), Reflection
					.getType (tid));
				info.switches[tid] = new Switch ();
				g.addExpression (info.switches[tid].add (index
					.createGet ()));
				g.addExpression (new Throw ().add (new InvokeSpecial (
					ASSERTION_INIT).add (new New (ASSERTION_ERROR))));
			}
		}
		pcs.createAndDeclareMethod ("<init>", Type.VOID);
		sig.createAndDeclareMethod ("signature", Type.VOID);
		createDefaultConstructor (info, false);
	}

	void compileModuleMethods (ClassInfo info, Type[] types,
			boolean[] inherited, AST[] methods)
	{
//		Expression fieldInit = new ArrayInit (FIELD_TYPE.getArrayType (),
//			types.length);
		Int2IntMap[] labelToId = new Int2IntMap[TypeId.TYPE_COUNT];
		info.parameterGetters = new Method[types.length];
		for (int i = 0; i < types.length; i++)
		{
			if (info.moduleFields[i] != null)
			{
				types[i] = info.moduleFields[i].getType ();
				if (!inherited[i])
				{
					info.moduleConstructorScope.addExpression (new AssignField (
						info.moduleFields[i], Assignment.SIMPLE).add (
						info.moduleConstructorScope.createThis ()).add (
						info.moduleConstructorScope.getParameter (i)
							.createGet ()));
				}
			}
//			fieldInit.add (new ObjectConst (info.moduleFields[i], true,
//				FIELD_TYPE));

			Type t = types[i];
			if (t == null)
			{
				continue;
			}
			int tid = t.getTypeId ();
			if (labelToId[tid] == null)
			{
				labelToId[tid] = new Int2IntMap ();
			}
			Expression e;
			if (info.moduleFields[i] != null)
			{
				e = compileFieldExpression (info.moduleFields[i],
					info.objects[tid].createGet ().cast (info.scope.getDeclaredType ()),
					info.getterMethods[tid], info.node);
			}
			else if (methods[i] != null)
			{
				Method m = Reflection.findMethodWithPrefixInTypes
					(info.scope.getDeclaredType (), 'm' + methods[i].getText () + ";()", true, false);
				if (m == null)
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.NO_MEMBER_IN_TYPE,
								   I18N.msg (ProblemReporter.METHOD),
								   methods[i].getText (),
								   info.scope.getDeclaredType ().getName ()), methods[i]);
					continue;
				}
				if (Reflection.isStatic (m))
				{
					problems.addSemanticError
						(I18N.msg (ProblemReporter.INSTANCE_MEMBERS_EXPECTED,
								   I18N.msg (ProblemReporter.METHOD),
								   Reflection.getDescription (m), 1), methods[i]);
					continue;
				}
				info.parameterGetters[i] = m;
				e = compileMethodInvocation (m,
					info.objects[tid].createGet ().cast (info.scope.getDeclaredType ()),
					ArgumentTransformations.NO_IMPLICIT_ARGS, Applicability.DEFAULT,
					Expression.EXPR_0, info.getterMethods[tid], methods[i]);
				e = returnConversion (e, t, info.getterMethods[tid], methods[i]);
			}
			else
			{
				continue;
			}
			labelToId[tid]
				.put (i, info.switches[tid].getExpressionCount ());
			info.switches[tid].add (new Return (info.getterMethods[tid],
				null).add (e));
		}
		for (int i = 0; i < TypeId.TYPE_COUNT; i++)
		{
			if (labelToId[i] != null)
			{
				info.switches[i].initialize (labelToId[i], -1);
			}
		}

		MethodScope pcs = info.predicateConstructorScope;

		pcs.addExpression (new SetThis ()
			.add (compileSuperConstructorInvocation (pcs.createThis (), null,
				new Expression[] {
						new TypeConst (info.predicateScope.getDeclaredType (),
							true),
						new IntConst (types.length)}, pcs, info.node)));
	}

	
	public static final String PRODUCER_GET_ROOT = "producer$getRoot";
	public static final String PRODUCER_BEGIN = "producer$begin";
	public static final String PRODUCER_END = "producer$end";
	public static final String PRODUCER_PUSH = "producer$push";
	public static final String PRODUCER_POP = "producer$pop";
	public static final String PRODUCER_SEPARATE = "producer$separate";
	//multiscale begin
	public static final String PRODUCER_CLIQUE_BEGIN = "producer$cliqueBegin";
	public static final String PRODUCER_CLIQUE_END = "producer$cliqueEnd";
	//multiscale end
	// contains all overloadable operators
	// allows for fast check if an identifier is an operator
	static final IntHashMap<String> unaryOperators = new IntHashMap<String> ();
	static final IntHashMap<String> binaryOperators = new IntHashMap<String> ();

	public static final String OPERATOR_NAME_NEG = "operator$neg";
	public static final String OPERATOR_NAME_POS = "operator$pos";
	public static final String OPERATOR_NAME_NOT = "operator$not";
	public static final String OPERATOR_NAME_COM = "operator$com";
	public static final String OPERATOR_NAME_INC = "operator$inc";
	public static final String OPERATOR_NAME_DEC = "operator$dec";
	public static final String OPERATOR_NAME_POST_INC = "operator$postInc";
	public static final String OPERATOR_NAME_POST_DEC = "operator$postDec";

	public static final String OPERATOR_NAME_ADD = "operator$add";
	public static final String OPERATOR_NAME_SUB = "operator$sub";
	public static final String OPERATOR_NAME_MUL = "operator$mul";
	public static final String OPERATOR_NAME_DIV = "operator$div";
	public static final String OPERATOR_NAME_REM = "operator$rem";
	public static final String OPERATOR_NAME_POW = "operator$pow";
	public static final String OPERATOR_NAME_COR = "operator$cor";
	public static final String OPERATOR_NAME_CAND = "operator$cand";
	public static final String OPERATOR_NAME_ADD_ASSIGN = "operator$addAssign";
	public static final String OPERATOR_NAME_SUB_ASSIGN = "operator$subAssign";
	public static final String OPERATOR_NAME_MUL_ASSIGN = "operator$mulAssign";
	public static final String OPERATOR_NAME_DIV_ASSIGN = "operator$divAssign";
	public static final String OPERATOR_NAME_REM_ASSIGN = "operator$remAssign";
	public static final String OPERATOR_NAME_POW_ASSIGN = "operator$powAssign";
	public static final String OPERATOR_NAME_SHL = "operator$shl";
	public static final String OPERATOR_NAME_SHR = "operator$shr";
	public static final String OPERATOR_NAME_USHR = "operator$ushr";
	public static final String OPERATOR_NAME_SHL_ASSIGN = "operator$shlAssign";
	public static final String OPERATOR_NAME_SHR_ASSIGN = "operator$shrAssign";
	public static final String OPERATOR_NAME_USHR_ASSIGN = "operator$ushrAssign";
	public static final String OPERATOR_NAME_XOR = "operator$xor";
	public static final String OPERATOR_NAME_OR = "operator$or";
	public static final String OPERATOR_NAME_AND = "operator$and";
	public static final String OPERATOR_NAME_XOR_ASSIGN = "operator$xorAssign";
	public static final String OPERATOR_NAME_OR_ASSIGN = "operator$orAssign";
	public static final String OPERATOR_NAME_AND_ASSIGN = "operator$andAssign";
	public static final String OPERATOR_NAME_EQUALS = "operator$eq";
	public static final String OPERATOR_NAME_NOT_EQUALS = "operator$neq";
	public static final String OPERATOR_NAME_LT = "operator$lt";
	public static final String OPERATOR_NAME_LE = "operator$le";
	public static final String OPERATOR_NAME_GT = "operator$gt";
	public static final String OPERATOR_NAME_GE = "operator$ge";
	public static final String OPERATOR_NAME_CMP = "operator$cmp";

	public static final String OPERATOR_NAME_RANGE = "operator$range";
	public static final String OPERATOR_NAME_IN = "operator$in";
	public static final String OPERATOR_NAME_GUARD = "operator$guard";

	public static final String OPERATOR_NAME_ARROW = "operator$arrow";
	public static final String OPERATOR_NAME_LEFT_ARROW = "operator$leftArrow";
	public static final String OPERATOR_NAME_LONG_ARROW = "operator$longArrow";
	public static final String OPERATOR_NAME_LONG_LEFT_ARROW = "operator$longLeftArrow";
	public static final String OPERATOR_NAME_LONG_LEFT_RIGHT_ARROW = "operator$longLeftRightArrow";

	public static final String OPERATOR_NAME_PLUS_LEFT_ARROW = "operator$plusLeftArrow";
	public static final String OPERATOR_NAME_PLUS_ARROW = "operator$plusArrow";
	public static final String OPERATOR_NAME_PLUS_LEFT_RIGHT_ARROW = "operator$plusLeftRightArrow";
	public static final String OPERATOR_NAME_PLUS_LINE = "operator$plusLine";

	public static final String OPERATOR_NAME_SLASH_LEFT_ARROW = "operator$slashLeftArrow";
	public static final String OPERATOR_NAME_SLASH_ARROW = "operator$slashArrow";
	public static final String OPERATOR_NAME_SLASH_LEFT_RIGHT_ARROW = "operator$slashLeftRightArrow";
	public static final String OPERATOR_NAME_SLASH_LINE = "operator$slashLine";

	public static final String OPERATOR_NAME_LEFT_RIGHT_ARROW = "operator$leftRightArrow";
	public static final String OPERATOR_NAME_X_LEFT_RIGHT_ARROW = "operator$xLeftRightArrow";
	public static final String OPERATOR_NAME_LINE = "operator$line";

	public static final String OPERATOR_NAME_INDEX = "operator$index";

	public static final String OPERATOR_NAME_INVOKE = "operator$invoke";

	public static final String OPERATOR_NAME_QUOTE = "operator$quote";

	public static final String OPERATOR_NAME_SPACE = "operator$space";

	public static final String OPERATOR_NAME_DEFERRED_ASSIGN = "operator$defAssign";
	public static final String OPERATOR_NAME_DEFERRED_RATE_ASSIGN = "operator$defRateAssign";
	public static final String OPERATOR_NAME_DEFERRED_POW = "operator$defPowAssign";
	public static final String OPERATOR_NAME_DEFERRED_MUL = "operator$defMulAssign";
	public static final String OPERATOR_NAME_DEFERRED_DIV = "operator$defDivAssign";
	public static final String OPERATOR_NAME_DEFERRED_REM = "operator$defRemAssign";
	public static final String OPERATOR_NAME_DEFERRED_ADD = "operator$defAddAssign";
	public static final String OPERATOR_NAME_DEFERRED_SUB = "operator$defSubAssign";
	public static final String OPERATOR_NAME_DEFERRED_SHL = "operator$defShlAssign";
	public static final String OPERATOR_NAME_DEFERRED_SHR = "operator$defShrAssign";
	public static final String OPERATOR_NAME_DEFERRED_USHR = "operator$defUshrAssign";
	public static final String OPERATOR_NAME_DEFERRED_AND = "operator$defAndAssign";
	public static final String OPERATOR_NAME_DEFERRED_XOR = "operator$defXorAssign";
	public static final String OPERATOR_NAME_DEFERRED_OR = "operator$defOrAssign";

	static
	{
		// add unary operators
		unaryOperators.put (ADD, OPERATOR_NAME_POS);
		unaryOperators.put (SUB, OPERATOR_NAME_NEG);
		unaryOperators.put (NOT, OPERATOR_NAME_NOT);
		unaryOperators.put (COM, OPERATOR_NAME_COM);
		unaryOperators.put (INC, OPERATOR_NAME_INC);
		unaryOperators.put (DEC, OPERATOR_NAME_DEC);
		unaryOperators.put (QUOTE, OPERATOR_NAME_QUOTE);
		unaryOperators.put (INVOKE_OP, OPERATOR_NAME_INVOKE);

		// add binary operators
		binaryOperators.put (ADD, OPERATOR_NAME_ADD);
		binaryOperators.put (SUB, OPERATOR_NAME_SUB);
		binaryOperators.put (MUL, OPERATOR_NAME_MUL);
		binaryOperators.put (DIV, OPERATOR_NAME_DIV);
		binaryOperators.put (REM, OPERATOR_NAME_REM);
		binaryOperators.put (POW, OPERATOR_NAME_POW);
		binaryOperators.put (COR, OPERATOR_NAME_COR);
		binaryOperators.put (CAND, OPERATOR_NAME_CAND);
		binaryOperators.put (ADD_ASSIGN, OPERATOR_NAME_ADD_ASSIGN);
		binaryOperators.put (SUB_ASSIGN, OPERATOR_NAME_SUB_ASSIGN);
		binaryOperators.put (MUL_ASSIGN, OPERATOR_NAME_MUL_ASSIGN);
		binaryOperators.put (DIV_ASSIGN, OPERATOR_NAME_DIV_ASSIGN);
		binaryOperators.put (REM_ASSIGN, OPERATOR_NAME_REM_ASSIGN);
		binaryOperators.put (POW_ASSIGN, OPERATOR_NAME_POW_ASSIGN);
		binaryOperators.put (SHL, OPERATOR_NAME_SHL);
		binaryOperators.put (SHR, OPERATOR_NAME_SHR);
		binaryOperators.put (USHR, OPERATOR_NAME_USHR);
		binaryOperators.put (SHL_ASSIGN, OPERATOR_NAME_SHL_ASSIGN);
		binaryOperators.put (SHR_ASSIGN, OPERATOR_NAME_SHR_ASSIGN);
		binaryOperators.put (USHR_ASSIGN, OPERATOR_NAME_USHR_ASSIGN);
		binaryOperators.put (XOR, OPERATOR_NAME_XOR);
		binaryOperators.put (OR, OPERATOR_NAME_OR);
		binaryOperators.put (AND, OPERATOR_NAME_AND);
		binaryOperators.put (XOR_ASSIGN, OPERATOR_NAME_XOR_ASSIGN);
		binaryOperators.put (OR_ASSIGN, OPERATOR_NAME_OR_ASSIGN);
		binaryOperators.put (AND_ASSIGN, OPERATOR_NAME_AND_ASSIGN);
		binaryOperators.put (EQUALS, OPERATOR_NAME_EQUALS);
		binaryOperators.put (NOT_EQUALS, OPERATOR_NAME_NOT_EQUALS);
		binaryOperators.put (LT, OPERATOR_NAME_LT);
		binaryOperators.put (LE, OPERATOR_NAME_LE);
		binaryOperators.put (GT, OPERATOR_NAME_GT);
		binaryOperators.put (GE, OPERATOR_NAME_GE);

		binaryOperators.put (RANGE, OPERATOR_NAME_RANGE);
		binaryOperators.put (IN, OPERATOR_NAME_IN);
		binaryOperators.put (GUARD, OPERATOR_NAME_GUARD);

		binaryOperators.put (ARROW, OPERATOR_NAME_ARROW);
		binaryOperators.put (LEFT_ARROW, OPERATOR_NAME_LEFT_ARROW);
		binaryOperators.put (LONG_ARROW, OPERATOR_NAME_LONG_ARROW);
		binaryOperators.put (LONG_LEFT_ARROW, OPERATOR_NAME_LONG_LEFT_ARROW);
		binaryOperators.put (LONG_LEFT_RIGHT_ARROW, OPERATOR_NAME_LONG_LEFT_RIGHT_ARROW);

		binaryOperators.put (PLUS_LEFT_ARROW, OPERATOR_NAME_PLUS_LEFT_ARROW);
		binaryOperators.put (PLUS_ARROW, OPERATOR_NAME_PLUS_ARROW);
		binaryOperators.put (PLUS_LEFT_RIGHT_ARROW, OPERATOR_NAME_PLUS_LEFT_RIGHT_ARROW);
		binaryOperators.put (PLUS_LINE, OPERATOR_NAME_PLUS_LINE);

		binaryOperators.put (SLASH_LEFT_ARROW, OPERATOR_NAME_SLASH_LEFT_ARROW);
		binaryOperators.put (SLASH_ARROW, OPERATOR_NAME_SLASH_ARROW);
		binaryOperators.put (SLASH_LEFT_RIGHT_ARROW, OPERATOR_NAME_SLASH_LEFT_RIGHT_ARROW);
		binaryOperators.put (SLASH_LINE, OPERATOR_NAME_SLASH_LINE);

		binaryOperators.put (LINE, OPERATOR_NAME_LINE);
		binaryOperators.put (LEFT_RIGHT_ARROW, OPERATOR_NAME_LEFT_RIGHT_ARROW);
		binaryOperators.put (X_LEFT_RIGHT_ARROW, OPERATOR_NAME_X_LEFT_RIGHT_ARROW);
		
		binaryOperators.put (INDEX_OP, OPERATOR_NAME_INDEX);
	
		binaryOperators.put (INVOKE_OP, OPERATOR_NAME_INVOKE);

		binaryOperators.put (DEFERRED_ASSIGN, OPERATOR_NAME_DEFERRED_ASSIGN);
		binaryOperators.put (DEFERRED_RATE_ASSIGN, OPERATOR_NAME_DEFERRED_RATE_ASSIGN);
		binaryOperators.put (DEFERRED_POW, OPERATOR_NAME_DEFERRED_POW);
		binaryOperators.put (DEFERRED_MUL, OPERATOR_NAME_DEFERRED_MUL);
		binaryOperators.put (DEFERRED_DIV, OPERATOR_NAME_DEFERRED_DIV);
		binaryOperators.put (DEFERRED_REM, OPERATOR_NAME_DEFERRED_REM);
		binaryOperators.put (DEFERRED_ADD, OPERATOR_NAME_DEFERRED_ADD);
		binaryOperators.put (DEFERRED_SUB, OPERATOR_NAME_DEFERRED_SUB);
		binaryOperators.put (DEFERRED_SHL, OPERATOR_NAME_DEFERRED_SHL);
		binaryOperators.put (DEFERRED_SHR, OPERATOR_NAME_DEFERRED_SHR);
		binaryOperators.put (DEFERRED_USHR, OPERATOR_NAME_DEFERRED_USHR);
		binaryOperators.put (DEFERRED_AND, OPERATOR_NAME_DEFERRED_AND);
		binaryOperators.put (DEFERRED_XOR, OPERATOR_NAME_DEFERRED_XOR);
		binaryOperators.put (DEFERRED_OR, OPERATOR_NAME_DEFERRED_OR);
	}

	/**
	 * Map an operator symbol to an operator name. The flag isUnary determines
	 * which mapping is applied (i.e. consider the symbol '-', which may be
	 * mapped differently in this case).
	 */
	static String getOperatorName (int operatorSymbol, boolean isUnary)
	{
		String result;
		if (isUnary)
		{
			result = unaryOperators.get (operatorSymbol);
		}
		else
		// isBinary
		{
			result = binaryOperators.get (operatorSymbol);
		}
		return result;
	}

	static boolean isUnaryOperator (int op)
	{
		return unaryOperators.containsKey (op);
	}

	static boolean isBinaryOperator (int op)
	{
		return binaryOperators.containsKey (op);
	}

	public static void checkOperatorFunction (AST params, AST mods, AST id,
			int symbol, RecognitionExceptionList exceptionList)
	{
		int paramCount = params.getNumberOfChildren ();
		boolean isStatic = false;

		// check if the method has a modifier 'static'
		for (AST ast = mods.getFirstChild (); ast != null; ast = ast
			.getNextSibling ())
		{
			if (ast.getType () == STATIC_)
			{
				isStatic = true;
				break;
			}
		}

		if (!isStatic)
		{
			paramCount++;
		}

		boolean error = true;

		if (paramCount == 1)
		{
			if (isUnaryOperator (symbol))
			{
				String operatorName = getOperatorName (symbol, true);
				id.setText (operatorName);
				error = false;
			}
		}
		else if (paramCount == 2)
		{
			if ((INC == symbol) || (DEC == symbol))
			{
				// this is a postfix increment/decrement operator method
				// having a dummy parameter. Check that it has type int.
				AST dummyParam = params.getFirstChild ();
				if (isStatic)
				{
					dummyParam = dummyParam.getNextSibling ();
				}
				// now dummy is the PARAMETER_DEF-node of the dummy parameter.

				// Move dummy to the node which defines the type
				dummyParam = dummyParam.getFirstChild ().getNextSibling ();

				if (dummyParam.getType () != INT_)
				{
					exceptionList.addSemanticError (I18N,
						ProblemReporter.INT_EXPECTED_FOR_DUMMY, dummyParam);
				}
				id.setText ((INC == symbol) ? OPERATOR_NAME_POST_INC
						: OPERATOR_NAME_POST_DEC);
				error = false;
			}
			else if (isBinaryOperator (symbol))
			{
				String operatorName = getOperatorName (symbol, false);
				id.setText (operatorName);
				error = false;
			}
		}
		else if (paramCount > 2)
		{
			if (INDEX_OP == symbol)
			{
				id.setText (OPERATOR_NAME_INDEX);
				error = false;
			}
			else if (INVOKE_OP == symbol)
			{
				id.setText (OPERATOR_NAME_INVOKE);
				error = false;
			}
		}

		if (error)
		{
			exceptionList.addSemanticError (I18N,
				ProblemReporter.WRONG_OPERAND_NUMBER_FOR_OP, id);
		}
	}

	/**
	 * Returns true if a can be converted to b via boxing.
	 * @param a
	 * @param b
	 * @return
	 */
	//	boolean isBoxingConversion(Type a, Type b)
	//	{
	//		boolean result = false;
	//		return result;
	//	}
}
