
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

package de.grogra.xl.compiler.pattern;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import antlr.collections.AST;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.IntersectionType;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.util.I18NBundle;
import de.grogra.util.Utils;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.ProblemReporter;
import de.grogra.xl.compiler.XMethod;
import de.grogra.xl.compiler.scope.BlockScope;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.compiler.scope.TypeScope;
import de.grogra.xl.expr.Block;
import de.grogra.xl.expr.Constant;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.expr.GetLocal;
import de.grogra.xl.expr.InvokeVirtual;
import de.grogra.xl.expr.LocalAccess;
import de.grogra.xl.expr.ObjectConst;
import de.grogra.xl.expr.Return;
import de.grogra.xl.query.BuiltInPattern;
import de.grogra.xl.query.CompiletimeModel;
import de.grogra.xl.query.CompoundPattern;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.EdgePattern;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.Pattern;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.query.TypePattern;
import de.grogra.xl.query.WrappedTypePattern;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.vmx.ExpressionPattern;

public class PatternBuilder
{
	public static boolean DEBUG = false;

	private static final I18NBundle I18N = Compiler.I18N;

	private static final Class TARGET_TYPE_MARK = Place.Mapping.class;
	private static final Type QS_TYPE = ClassAdapter.wrap (QueryState.class);


	protected final CompiletimeModel model;
	protected final PatternBuilder enclosing;
	protected final BlockScope scope;
	protected final Compiler compiler;
	protected final ProblemReporter problems;
	protected final Type nodeType;

	private final Local queryState;


	private int context;
	
	private int nextEdgeType = EdgePattern.SUCCESSOR_EDGE;
	private boolean includeJoinInContext = true;

	private boolean beginning = true, ending = false;
	private PatternData lastPred;
	private Type lastNodeType;
	
	private boolean allowOpenEnds;
	

	final ObjectList predicates = new ObjectList ();
	private final ObjectList predicateStack = new ObjectList ();
	private int textualPosition = 0;

	private final HashMap placesForLocals;
	private final ObjectList allPlaces;
	
	private final ObjectList variables = new ObjectList ();
	
	private final ObjectList compositeChildren = new ObjectList ();
	private final ObjectList<TraversalData> traversals = new ObjectList<TraversalData> ();

	private ObjectConst targetTypeExpr = new ObjectConst (null, Type.CLASS);

	private final int[] nextId;
	
	static final int NULL_ARGUMENT = 0;
	static final int CLOSED_ARGUMENT = 1;
	static final int OPEN_ARGUMENT = 2;


	public PatternBuilder (CompiletimeModel model, PatternBuilder parent,
							 BlockScope scope, AST pos)
	{
		this.model = model;
		this.enclosing = parent;
		this.scope = scope;
		this.compiler = TypeScope.get (scope).getCompiler ();
		this.problems = compiler.problems;
		this.nodeType = model.getNodeType ();
		this.placesForLocals = (parent != null) ? parent.placesForLocals : new HashMap ();
		this.allPlaces = (parent != null) ? parent.allPlaces : new ObjectList ();
		this.nextId = (parent != null) ? parent.nextId : new int[3];
		queryState = (parent != null) ? parent.queryState
			: scope.declareLocal ("qs.", Member.FINAL | Member.SYNTHETIC,
								  ClassAdapter.wrap (QueryState.class), pos);
		lastNodeType = nodeType;
	}
	
	
	public Local getQueryState ()
	{
		return queryState;
	}

	int nextPlaceId ()
	{
		return nextId[0]++;
	}


	int nextPatternId ()
	{
		return nextId[1]++;
	}


	int nextArgumentId ()
	{
		return nextId[2]++;
	}

	
	public boolean enclosesOrEquals (PatternBuilder c)
	{
		while (c != null)
		{
			if (c == this)
			{
				return true;
			}
			c = c.enclosing;
		}
		return false;
	}

	
	public void allowOpenEnds ()
	{
		allowOpenEnds = true;
	}

	
	public CompiletimeModel getModel ()
	{
		return model;
	}


	public BlockScope getScope ()
	{
		return scope;
	}

	
	private ArgumentDescription[] getArgumentDescriptions (ObjectList vars, int offset)
	{
		ArgumentDescription[] a = new ArgumentDescription[vars.size () + offset];
		for (int i = 0; i < vars.size; i++)
		{
			Local l = (Local) vars.get (i);
			a[offset + i] = ((l.getModifiersEx () & Compiler.MOD_IMPLICIT_ARGUMENT) != 0)
				? null : new ArgumentDescription (null, l);
		}
		return a;
	}


	public Local declareAuxVariable (Type type)
	{
		Local l = declareVariable ("term.", type, null);
		l.getMethodScope ().removeLocal (l);
		return l;
	}


	public Local declareWrapper (Local wrapped, Type type, AST pos)
	{
		assert wrapped.isVariable (this);
		Local w = declareVariable
			(compiler.verifyNotDeclared ('$' + wrapped.getName (), scope, pos), type, pos);
		wrapped.wrapper = w;
		w.wrapped = wrapped;
		return w;
	}


	public Local declareVariable (AST name, Type type)
	{
		return declareVariable (name.getText (), type, name);
	}


	private Local declareVariable (String name, Type type, AST pos)
	{
		if (type.getTypeId () == TypeId.OBJECT)
		{
			IntersectionType i = new IntersectionType
				(compiler.getPackage ().getName ());
			i.intersect (type);
			type = i;
		}
		Local local = scope.declareLocal (compiler.verifyNotDeclared (name, scope, pos),
										  Member.FINAL, type, pos);
		local.setVariable (this);
		variables.add (local);
		return local;
	}


	Place createPlace (Local label)
	{
		if ((label != null) && (label.wrapped != null))
		{
			label = label.wrapped;
		}
		Place p = new Place (label, this);
		allPlaces.add (p);
		return p;
	}


	Place getPlace (Expression e)
	{
		if (e instanceof GetLocal)
		{
			Local l = ((GetLocal) e).getLocal (0);
			if (l.isVariable (this))
			{
				return getPlace (l);
			}
		}
		return null;
	}


	Place getPlace (Local label)
	{
		if ((enclosing != null) && label.isVariable (enclosing))
		{
			return enclosing.getPlace (label);
		}
		assert label.isVariable (this);
		if (label.wrapped != null)
		{
			label = label.wrapped;
		}
		Place p = (Place) placesForLocals.get (label);
		if (p == null)
		{
			p = createPlace (label);
			placesForLocals.put (label, p);
		}
		return p.resolve ();
	}

	
	Place getPlace (PatternData pred, int index)
	{
		Place p = pred.getPlaces ()[index];
		if (p == null)
		{
			p = createPlace (null);
			p.map (pred, index, pred.pos);
		}
		return p;
	}


	public Local[] getDeclaredVariables ()
	{
		ObjectList list = new ObjectList ();
		for (int i = 0; i < variables.size (); i++)
		{
			if (((((Local) variables.get (i)).getModifiers ())
				 & Member.SYNTHETIC) == 0)
			{
				list.add (variables.get (i));
			}
		}
		return (Local[]) list.toArray (new Local[list.size ()]);
	}

	
	private void completeTraversal (TraversalData tp)
	{
		tp.child.computeDependenciesFromLocalAccess (predicates);
		for (int j = 0; j < tp.child.dependsOn.size (); j++)
		{
			tp.addDependency (tp.child.dependsOn.get (j));
		}
		ObjectList<Argument> outerTerms = new ObjectList<Argument> ();
		tp.child.getArguments (outerTerms);
		for (int i = outerTerms.size () - 1; i >= 0; i--)
		{
			Argument t = outerTerms.get (i);
			if (((t.local.getModifiers () & Member.SYNTHETIC) != 0)
				|| !t.local.isVariable (this))
			{
				outerTerms.remove (i);
			}
		}
		tp.complete (outerTerms);
		tp.addLocalAccess (tp.child);
	}


	private void complete (CompositeData cp)
	{
		for (int i = 0; i < compositeChildren.size (); i++)
		{
			CompositeData p = (CompositeData) compositeChildren.get (i);
			p.builder.complete (p);
		}

		for (int i = 0; i < traversals.size (); i++)
		{
			completeTraversal (traversals.get (i));
		}
		
		ObjectList terms = new ObjectList ();
		final int n = predicates.size ();
		for (int i = 0; i < n; i++)
		{
			PatternData pi = (PatternData) predicates.get (i);
			pi.index = i;
			pi.getArguments (terms);
		}
		Pattern[] p = new Pattern[n];
		boolean[] pc = new boolean[n];
		short[][] m = new short[n][];
		short[][] d = new short[n][];
		for (int i = 0; i < n; i++)
		{
			m[i] = ((PatternData) predicates.get (i)).completeArguments (terms);
		}
		for (int i = 0; i < n; i++)
		{
			PatternData pi = (PatternData) predicates.get (i);
			pi.computeDependenciesFromLocalAccess (predicates);
			pi.computeAllFollowers ();
			p[i] = pi.getPattern ();
			pc[i] = pi.context;
		}
		for (int i = 0; i < n; i++)
		{
			((PatternData) predicates.get (i)).convertFollowersToDependencies ();
		}
		Type[] t = new Type[terms.size ()];
		boolean[] node = new boolean[terms.size ()];
		boolean[] ctx = new boolean[terms.size ()];
		Argument[] mapping = new Argument[terms.size ()];
		short[][] folding = new short[terms.size ()][];
		for (int i = terms.size () - 1; i >= 0; i--)
		{
			Argument a = (Argument) terms.get (i);
			node[i] = a.node;
			if (a.node)
			{
				ObjectList<Place> foldings = new ObjectList<Place> ();
				if (!a.place.foldings.isEmpty ())
				{
					for (int j = a.place.foldings.size () - 1; j >= 0; j--)
					{
						Local id = a.place.foldings.get (j);
						if (id.isVariable (this))
						{
							Place pl = getPlace (id);
							if (enclosesOrEquals (pl.getBuilder ()))
							{
								foldings.add (pl);
							}
						}
					}
				}
				if (!a.place.placeFoldings.isEmpty ())
				{
					for (int j = a.place.placeFoldings.size () - 1; j >= 0; j--)
					{
						Place pl = a.place.placeFoldings.get (j);
						if (enclosesOrEquals (pl.getBuilder ()))
						{
							foldings.add (pl);
						}
					}
				}
				if (!foldings.isEmpty ())
				{
					short[] f = new short[foldings.size ()];
					folding[i] = f;
					for (int j = 0; j < f.length; j++)
					{
						f[j] = (short) terms.indexOf (foldings.get (j).getNodeArgument ());
					}
				}
			}
			ctx[i] = a.context;
			Type x = a.getType ();
			t[i] = (x instanceof IntersectionType)
				? ((IntersectionType) x).simplify () : x;
			mapping[i] = a;
		}
		for (int i = 0; i < n; i++)
		{
			PatternData pi = (PatternData) predicates.get (i);
			if (pi.dependsOn.isEmpty ())
			{
				d[i] = Utils.SHORT_0;
			}
			else
			{
				d[i] = new short[pi.dependsOn.size];
				for (int j = d[i].length - 1; j >= 0; j--)
				{
					d[i][j] = (short) pi.dependsOn.get (j).index;
				}
			}
			if (pi.targetTypeExpr != null)
			{
				Type tt = t[m[i][0]];
				if ((tt instanceof IntersectionType)
					&& (tt.getDeclaredInterfaceCount () == 1)
					&& Reflection.equal (model.getNodeType (), tt.getSupertype ()))
				{
					tt = tt.getDeclaredInterface (0);
				}
				pi.targetTypeExpr.value = tt;
			}
		}
		cp.complete
			(new CompoundPattern
			 (t, node, ctx, p, pc, m, d, folding,
			  (cp.getInPlace (false) == null) ? -1
			  : terms.indexOf (cp.getInPlace (false).getNodeArgument ()),
			  (cp.getOutPlace (false) == null) ? -1
			  : terms.indexOf (cp.getOutPlace (false).getNodeArgument ()),
			  cp.getMatchDirection ().getCode (), cp.getCompositeType (),
			  cp.isOptional (), cp.getContinueLabel ()),
			 mapping);
	}


	public CompositeData createCompositePattern ()
	{
		assert enclosing == null;
		CompositeData cp = createCompositePattern (false, EdgeDirection.UNDIRECTED, null, textualPosition++, -1);
		for (int i = 0; i < allPlaces.size (); i++)
		{
			Place p = (Place) allPlaces.get (i);
			if (p != p.resolve ())
			{
				allPlaces.remove (i--);
			}
			else
			{
				p.mapToArguments ();
			}
		}
		complete (cp);
		if (DEBUG)
		{
			System.err.println (this);
			System.err.println ("Places:");
			ObjectList terms = new ObjectList ();
			for (int i = 0; i < allPlaces.size (); i++)
			{
				Place p = (Place) allPlaces.get (i);
				System.err.println ("  " + p);
				p.getArguments (terms);
				for (int k = 0; k < terms.size (); k++)
				{
					System.err.println ("    " + terms.get (k));
					for (int j = 0; j < p.mappings.size (); j++)
					{
						Place.Mapping m = p.mappings.get (j);
						if (m.getArgument () == terms.get (k))
						{
							System.err.println
								("      -> " + m.pred + ", Index " + m.index);
						}
					}
				}
				terms.clear ();
			}
			dumpPatterns (cp, "");
		}
		return cp;
	}
	
	
	private void dumpPatterns (CompositeData cp, String indent)
	{
		System.err.print (indent);
		System.err.println ("Pattern components of " + cp);
		for (int i = 0; i < predicates.size (); i++)
		{
			PatternData pd = (PatternData) predicates.get (i);
			System.err.println (indent + "  " + pd);
			for (int j = 0; j < pd.getPlaces ().length; j++)
			{
				Place p = pd.getPlaces ()[j];
				Place.Mapping m = p.getMapping (pd, j);
				System.err.println (indent + "    " + j + " -> " + p
									+ ", " + m.getArgument ());
			}
			if (pd.builder != this)
			{
				pd.builder.dumpPatterns ((CompositeData) pd, indent + "      ");
			}
			if (pd instanceof TraversalData)
			{
				CompositeData c = ((TraversalData) pd).child;
				c.builder.dumpPatterns (c, indent + "      ");
			}
			System.err.println (indent + "    Depends on " + pd.dependsOn);
		}
	}

	
	private CompositeData createCompositePattern
		(boolean forTraversal, EdgeDirection direction, AST pos,
		 int textualPosition, int level)
	{
		addSeparation (pos);
		PatternData first = null, last = null;
		int n = predicates.size ();
		for (int i = 0; i < n; i++)
		{
			PatternData pi = (PatternData) predicates.get (i);
			if (pi.context)
			{
				context++;
			}
			pi.createSubPatterns ();
			if (pi.context)
			{
				context--;
			}
			if (!pi.context && (pi.level == 0))
			{
				if ((first == null) && pi.hasInPlace ())
				{
					first = pi;
				}
				if (pi.hasOutPlace ())
				{
					last = pi;
				}
			}
		}
		int inKind = (first != null) ? first.isInClosed () ? CLOSED_ARGUMENT : OPEN_ARGUMENT
			: NULL_ARGUMENT;
		int outKind = (last != null) ? last.isOutClosed () ? CLOSED_ARGUMENT : OPEN_ARGUMENT
			: NULL_ARGUMENT;
		if (forTraversal)
		{
			if ((first == null) || (last == null))
			{
				problems.addSemanticError
					(I18N, ProblemReporter.NOT_TRAVERSABLE, pos);
				return null;
			}
			if (first.getInPlace (false) == last.getOutPlace (false))
			{
				problems.addSemanticError
					(I18N, ProblemReporter.CLOSED_NOT_TRAVERSABLE, pos);
				return null;
			}
			inKind = OPEN_ARGUMENT;
			outKind = OPEN_ARGUMENT;
		}
		CompositeData p = new CompositeData
			((first != null) ? first.getInPlace (false) : null, inKind,
			 (last != null) ? last.getOutPlace (false) : null, outKind,
			 direction, pos, textualPosition, level, this);
		p.addLocalAccess (predicates);
		return p;
	}


	public final SimplePatternData addNodePattern
		(AST label, BuiltInPattern pred, ArgumentDescription[] args, AST pos)
	{
		return add
			(new SimplePatternData (label, pred, args, 0, 0, CLOSED_ARGUMENT,
									  CLOSED_ARGUMENT, pos, textualPosition++, predicateStack.size (), this), true);
	}
	

	public final SimplePatternData addNodePattern
		(AST label, PatternWrapper pred, ArgumentDescription[] args, AST pos)
	{
		return add
			(new SimplePatternData (label, pred, args, pred.getInParameter (), pred.getOutParameter (), CLOSED_ARGUMENT,
									  CLOSED_ARGUMENT, pos, textualPosition++, predicateStack.size (), this), true);
	}
	
	
	public final SimplePatternData addRelationPattern
		(AST label, BuiltInPattern pred, ArgumentDescription[] args, boolean swapInOut, AST pos)
	{
		return add
			(new SimplePatternData (label, pred, args, swapInOut ? 1 : 0, swapInOut ? 0 : 1, OPEN_ARGUMENT,
									  OPEN_ARGUMENT, pos, textualPosition++, predicateStack.size (), this), true);
	}
	
	
	public final SimplePatternData addRelationPattern
		(AST label, PatternWrapper pred, ArgumentDescription[] args, boolean swapInOut, AST pos)
	{
		return add
			(new SimplePatternData (label, pred, args,
									  swapInOut ? pred.getOutParameter () : pred.getInParameter (),
									  swapInOut ? pred.getInParameter () : pred.getOutParameter (), OPEN_ARGUMENT,
									  OPEN_ARGUMENT, pos, textualPosition++, predicateStack.size (), this), true);
	}
	
	
	public final SimplePatternData addPattern
		(AST label, Pattern pred, ArgumentDescription[] args, AST pos)
	{
		return addPattern (label, pred, args, pos, true);
	}

	protected final SimplePatternData addPattern
		(AST label, Pattern pred, ArgumentDescription[] args, AST pos, boolean join)
	{
		return add
			((pred != null)
			 ? new SimplePatternData (label, pred, args, -1, -1, NULL_ARGUMENT, NULL_ARGUMENT,
										pos, textualPosition++,
										predicateStack.size (), this)
			 : null, join);
	}

	
	public Type getLastNodeType ()
	{
		return lastNodeType;
	}


	<T extends PatternData> T add (T p, boolean join)
	{
		if (p != null)
		{
			predicates.add (p);
			p.mapLabeledArgs ();
			p.context = context > 0;
			if (p.label != null)
			{
				if (p.getLabelArgument () < 0)
				{
					problems.addSemanticError
						(I18N, ProblemReporter.PATTERN_NOT_LABELABLE, p.label);
				}
				else
				{
					getPlace (declareVariable (p.label, p.getPattern ().getParameterType (p.getLabelArgument ()))).map (p, p.getLabelArgument (), p.label);
				}
			}
		}

		if (join)
		{
			if (p != null)
			{
				lastNodeType = p.getLastNodeType ();
			}
			if ((p != null) && (lastPred != null))
			{
				lastPred.next = p;
				p.prev = lastPred;
			}
			Place in;
			if ((lastPred != null) && ((in = lastPred.getOutPlace (false)) != null))
			{
				Place out;
				if ((p != null) && ((out = p.getInPlace (true)) != null))
				{
					in.setNode (!lastPred.context && lastPred.isOutClosed (), lastPred.context, p.pos);
					out.setNode (!p.context && p.isInClosed (), p.context, p.pos);
					if (lastPred.isOutClosed () && p.isInClosed ())
					{
						boolean ctx = includeJoinInContext && (lastPred.context || p.context);
						if (ctx)
						{
							beginContext (p.pos);
						}
						join (nextEdgeType, in, out, p.pos);
						if (ctx)
						{
							endContext (p.pos);
						}
					}
					else
					{
						in.merge (out, p.pos);
					}
				}
				else if (in.label == null)
				{
					if (lastPred.isOutClosed () || (ending && allowOpenEnds))
					{
						in.setNode (!lastPred.context, lastPred.context, lastPred.pos);
					}
					else
					{
						problems.addSemanticError
							(I18N, ProblemReporter.DANGLING_OUT_TERM, lastPred.pos);
					}
				}
			}
			else if ((p != null) && p.hasInPlace ())
			{
				Place pl = p.getInPlace (true);
				if (p.isInClosed () || (beginning && allowOpenEnds))
				{
					pl.setNode (!p.context, p.context, p.pos);
				}
				else
				{
					problems.addSemanticError
						(I18N, ProblemReporter.DANGLING_IN_TERM, p.pos);
				}
			}
			lastPred = p;
			nextEdgeType = EdgePattern.SUCCESSOR_EDGE;
			includeJoinInContext = true;
		}
		beginning = false;
		if (p != null)
		{
			p.mapUnlabeledArgs ();
		}
		return p;
	}


	public void addVariableReference (Local term, AST pos)
	{
		addNodePattern (null, new TypePattern (term.getType ()),
						  new ArgumentDescription[] {new ArgumentDescription (pos, term)}, pos);
	}


	public TraversalData addTraversal
		(AST label, PatternBuilder child, EdgeDirection direction,
		 Expression min, Expression max, boolean addFolding, AST pos)
	{
		CompositeData p = child.createCompositePattern (true, direction, pos, textualPosition++, -1);
		if (p == null)
		{
			return null;
		}
		compositeChildren.add (p);

		TraversalData t = new TraversalData
			(p, p.inKind, p.outKind, min, max, pos, textualPosition++,
			 predicateStack.size (), this, addFolding);
		traversals.add (t);
		return add (t, true);
	}


	public CompositeData addComposite
		(AST label, PatternBuilder child, EdgeDirection direction, boolean optional, AST pos)
	{
		CompositeData p = child.createCompositePattern
			(false, direction, pos, textualPosition++, predicateStack.size ());
		if (optional)
		{
			p.setOptional ();
		}
		p.atomic = true;
		compositeChildren.add (p);
		scope.receiveLocals (child.scope, null);
		while (!child.variables.isEmpty ())
		{
			Local t = (Local) child.variables.pop ();
			if ((t.getModifiers () & Member.SYNTHETIC) == 0)
			{
				t.setVariable (this);
				variables.add (t);
			}
		}
		return add (p, true);
	}


	public void addAny (AST label, AST pos)
	{
		addType (label, nodeType, pos);
	}

	
	public void addSeparation (AST pos)
	{
		ending = true;
		addPattern (null, null, null, pos);
		ending = false;
	}


	protected void join (int edgeType, Place in, Place out, AST pos)
	{
		addPattern
			(null, new EdgePattern (nodeType,
									  model.getEdgeType (),
									  model.getStandardEdgeFor (edgeType), EdgeDirection.FORWARD_INT),
			 new ArgumentDescription[] {new ArgumentDescription (pos, in), new ArgumentDescription (pos, out), null}, pos, false);
	}


	public void addStandardEdge (AST label, EdgeDirection direction, int edgeType, AST pos)
	{
		addConstantEdge (label, direction, model.getEdgeType (),
						 model.getStandardEdgeFor (edgeType), pos);
	}

	private static int getForwardCode (EdgeDirection dir)
	{
		return (dir == EdgeDirection.BACKWARD) ? EdgeDirection.FORWARD_INT : dir.getCode ();
	}

	public SimplePatternData addConstantEdge (AST label, EdgeDirection direction,
								  Type edgeClass, Serializable edge, AST pos)
	{
		
		return addRelationPattern
			(label, new EdgePattern (nodeType, edgeClass, edge, getForwardCode (direction)),
			 null, direction == EdgeDirection.BACKWARD, pos);
	}


	public void addEdge (AST label, EdgeDirection direction, Expression term, AST pos)
	{
		Type et = getModel ().getEdgeTypeFor (term.getType ());
		term = compiler.methodInvocationConversion (term, et, scope, pos);
		if ((term instanceof Constant)
			&& (Reflection.isPrimitive (term.getType ())
				|| term.hasType (Serializable.class)))
		{
			addConstantEdge
				(label, direction, et,
				 (Serializable) term.evaluateAsObject (null), pos);
		}
		else
		{
			addRelationPattern
				(label, new EdgePattern (nodeType, et, getForwardCode (direction)),
				 new ArgumentDescription[] {null, null, new ArgumentDescription (pos, term)},
				 direction == EdgeDirection.BACKWARD, pos);
		}
	}

	public void addEdgePattern (AST label, PatternWrapper edge, ArgumentDescription[] args, EdgeDirection direction, AST pos)
	{
		if (!edge.isFirstInOut ())
		{
			throw new IllegalArgumentException ();
		}
		SimplePatternData data = addConstantEdge (label, direction, edge.getParameterType (0), null, pos);
		args[0] = new ArgumentDescription (pos, data.getPlaces ()[2]);
		addPattern (null, edge, args, pos, false);
	}

	public void addType (AST label, Type type, AST pos)
	{
		addNodePattern (label, new TypePattern (type), new ArgumentDescription[1], pos);
	}


	public void addWrappedType (AST label, Type type, Expression wrapped, AST pos)
	{
		addNodePattern
			(label,
			 new WrappedTypePattern (compiler.getWrapperTypeFor (type, model, pos), type),
			 new ArgumentDescription[] {null, new ArgumentDescription (pos, wrapped)}, pos);
	}


	public void addCondition (AST label, Expression expr, AST pos)
	{
		beginContext (pos);
		ObjectList termLocals = new ObjectList ();
		HashSet used = new HashSet (), assigned = new HashSet ();
		MethodScope r = declareRoutine (expr, termLocals, used, assigned);
		ExpressionPattern p = createCondition (r, expr, termLocals, -1, -1);
		PatternData d = addPattern (label, p, getArgumentDescriptions (termLocals, 0), pos);
		d.addRoutine (r);
		d.setLocalAccess (used, assigned);
		endContext (pos);
	}


	public void addGuard (AST label, Expression guard, AST pos)
	{
		guard = compiler.assignmentConversion (guard, Type.BOOLEAN, scope, pos);
		ObjectList termLocals = new ObjectList ();
		HashSet used = new HashSet (), assigned = new HashSet ();
		MethodScope r = declareRoutine (guard, termLocals, used, assigned);
		ExpressionPattern p = createCondition (r, guard, termLocals, 0, 0);
		PatternData d = addNodePattern (label, p, getArgumentDescriptions (termLocals, 0), pos);
		d.addRoutine (r);
		d.setLocalAccess (used, assigned);
	}


	public static final Expression createArgument (Type type)
	{
		return Local.DUMMY.createGet ().setType (type);
	}


	public static final Expression createTargetTypeArgument ()
	{
		return new ObjectConst (TARGET_TYPE_MARK, Type.CLASS);
	}

	
	private void findVariables (Expression e, ObjectList termLocals,
							HashSet used, HashSet assigned, HashMap paramForTerm,
							MethodScope ms)
	{
		if (e instanceof LocalAccess)
		{
			LocalAccess a = (LocalAccess) e;
			for (int i = a.getLocalCount () - 1; i >= 0; i--)
			{
				Local l = a.getLocal (i);
				if (l == Local.DUMMY)
				{
					assert a instanceof GetLocal;
					Local x = ms.declareParameter
						("termparam.", Member.FINAL | Compiler.MOD_IMPLICIT_ARGUMENT, e.getType ());
					termLocals.add (x);
					a.setLocal (i, x);
				}
				else if (l.isVariable (this))
				{
					Local x = (Local) paramForTerm.get (l);
					if (x == null)
					{
						x = ms.declareParameter
							(l.getSimpleName () + '.', Member.FINAL, l.getType ());
						paramForTerm.put (l, x);
						termLocals.add (l);
						used.add (l);
					}
					a.setLocal (i, x);
				}
				else
				{
					 if ((a.getAccessType (i) & LocalAccess.USES_LOCAL) != 0)
					 {
					 	used.add (l);
					 }
					 if ((a.getAccessType (i) & LocalAccess.ASSIGNS_LOCAL) != 0)
					 {
					 	assigned.add (l);
					 }
				}
			}
		}
		else if (e instanceof ObjectConst)
		{
			ObjectConst c = (ObjectConst) e;
			if (c.value == TARGET_TYPE_MARK)
			{
				assert targetTypeExpr == null;
				targetTypeExpr = c;
			}
		}
		for (e = e.getFirstExpression (); e != null; e = e.getNextExpression ())
		{
			findVariables (e, termLocals, used, assigned, paramForTerm, ms);
		}
	}


	protected MethodScope declareRoutine (Expression expr, ObjectList termLocals,
										  HashSet used, HashSet assigned)
	{
		MethodScope ms = new MethodScope (scope.getMethodScope ());
		findVariables (expr, termLocals, used, assigned, new HashMap (), ms);
		return ms;
	}

	
	private static int index (int index, ObjectList vars)
	{
		return (index >= 0) ? index : vars.size () + index;
	}


	public void addRelation (AST label, Expression expr,
							 boolean swapInOut, AST pos)
	{
		ObjectList termLocals = new ObjectList ();
		HashSet used = new HashSet (), assigned = new HashSet ();
		MethodScope r = declareRoutine (expr, termLocals, used, assigned);
		ExpressionPattern p = createCondition
			(r, expr, termLocals, index (0, termLocals), index (1, termLocals));
		PatternData d = addRelationPattern (label, p, getArgumentDescriptions (termLocals, 0), swapInOut, pos);
		d.addRoutine (r);
		d.setLocalAccess (used, assigned);
	}


	public void addBlock (Expression block, AST pos)
	{
		beginContext (pos);
		ObjectList termLocals = new ObjectList ();
		HashSet used = new HashSet (), assigned = new HashSet ();
		MethodScope r = declareRoutine (block, termLocals, used, assigned);
		ExpressionPattern p = createBlock (r, block, termLocals);
		PatternData pred = new SimplePatternData
			(null, p, getArgumentDescriptions (termLocals, 0), -1, -1, NULL_ARGUMENT, NULL_ARGUMENT, pos, textualPosition++,
			 -1, this);
		pred.addRoutine (r);
		pred.setLocalAccess (used, assigned);
		lastPred.addFollower (pred);
		add (pred, false);
		endContext (pos);
	}


	public void addPathExpression (AST label, Expression expr, boolean swapInOut, AST pos)
	{
		ObjectList termLocals = new ObjectList ();
		HashSet used = new HashSet (), assigned = new HashSet ();
		targetTypeExpr = null;
		MethodScope r = declareRoutine (expr, termLocals, used, assigned);
		ExpressionPattern p = createExpression (r, expr, termLocals);
		PatternData pr = addRelationPattern (label, p, getArgumentDescriptions (termLocals, 1), !swapInOut, pos);
		if (targetTypeExpr != null)
		{
			pr.targetTypeExpr = targetTypeExpr;
			targetTypeExpr = null;
		}
		pr.addRoutine (r);
		pr.setLocalAccess (used, assigned);
	}

	
	public void addExpression (AST label, Expression expr, AST pos)
	{
		addExpression (label, expr, pos, false, null, 0);
	}

	
	SimplePatternData addExpression (AST label, Expression expr, AST pos,
									   boolean synthetic, ArgumentDescription term,
									   int textualPosition)
	{
		ObjectList termLocals = new ObjectList ();
		HashSet used = new HashSet (), assigned = new HashSet ();
		MethodScope r = declareRoutine (expr, termLocals, used, assigned);
		ExpressionPattern p = createExpression (r, expr, termLocals);
		ArgumentDescription[] a = getArgumentDescriptions (termLocals, 1);
		SimplePatternData pred;
		if (synthetic)
		{
			a[0] = term;
			pred = new SimplePatternData
				(null, p, a, -1, -1, NULL_ARGUMENT, NULL_ARGUMENT, pos, textualPosition,
				 -1, this);
			add (pred, false);
		}
		else
		{
			pred = addNodePattern (label, p, a, pos);
		}
		pred.addRoutine (r);
		pred.setLocalAccess (used, assigned);
		return pred;
	}

	
	public void addFolding (AST id)
	{
		Local node = scope.findLocal (id.getText (), true);
		if (node == null)
		{
			problems.addSemanticError
				(I18N.msg (ProblemReporter.NO_MEMBER_IN_SCOPE,
						   I18N.msg (ProblemReporter.VARIABLE),
						   id.getText ()), id);
			return;
		}
		if (!node.isVariable (this))
		{
			problems.addSemanticError
				(I18N.msg (ProblemReporter.QUERY_VARIABLE_EXPECTED,
						   id.getText ()), id);
			return;
		}
		if (node.getPatternBuilder () != this)
		{
			problems.addSemanticError
				(I18N.msg (ProblemReporter.QUERY_VARIABLE_AT_HIGHER_LEVEL,
						   id.getText ()), id);
			return;
		}
		if (lastPred == null)
		{
			return;
		}
		lastPred.getInPlace (true).foldings.add (node);
	}

	public void beginTree (AST pos)
	{
		if (lastPred == null)
		{
			problems.addSemanticError (I18N, ProblemReporter.MISSING_PARENT, pos);
			return;
		}
		if (!lastPred.isOutClosed ())
		{
			problems.addSemanticError (I18N, ProblemReporter.OUT_TERM_OF_PARENT_NOT_CLOSED, pos);
		}
		predicateStack.push (lastPred);
		nextEdgeType = EdgePattern.BRANCH_EDGE;
		includeJoinInContext = false;
	}

	
	public void endTree (AST pos)
	{
		addSeparation (pos);
		if (!predicateStack.isEmpty ())
		{
			lastPred = (PatternData) predicateStack.pop ();
		}
	}

	
	public void beginContext (AST pos)
	{
		context++;
	}

	
	public void endContext (AST pos)
	{
		if (context == 0)
		{
			throw new IllegalStateException ();
		}
		else
		{
			context--;
		}
	}


	static ExpressionPattern createExpression
		(MethodScope routine, Expression expr, ObjectList terms)
	{
		Type[] t = new Type[terms.size + 1];
		t[0] = expr.getType ();
		for (int i = terms.size; i > 0; i--)
		{
			t[i] = ((Local) terms.get (i - 1)).getType ();
		}
		Local qs = routine.declareParameter ("qs.", Member.FINAL, QS_TYPE),
			tp = routine.declareParameter ("tp.", Member.FINAL, Type.INT),
			cons = routine.declareParameter ("cons.", Member.FINAL, ClassAdapter.wrap (MatchConsumer.class)),
			arg = routine.declareParameter ("arg.", Member.FINAL, Type.INT);
		XMethod m = routine.createAndDeclareMethod ("expr", Type.VOID);
		Block b = Block.createSequentialBlock ();
		routine.addExpression (b);
		b.add (new InvokeVirtual (Reflection.findMethodWithPrefixInTypes
								  (QS_TYPE,
								   "m" + Reflection.getJVMPrefix (expr.getType ())
								   + "match;", false, true))
			   .add (qs.createGet ())
			   .add (tp.createGet ())
			   .add (expr)
			   .add (cons.createGet ())
			   .add (arg.createGet ()));
		return new ExpressionPattern (t, -1, -1, ExpressionPattern.EXPRESSION, m);
	}
	

	static ExpressionPattern createCondition
		(MethodScope routine, Expression expr, ObjectList terms, int in, int out)
	{
		return create (routine, expr, terms, ExpressionPattern.CONDITION, in, out);
	}


	static ExpressionPattern createBlock
		(MethodScope routine, Expression expr, ObjectList terms)
	{
		return create (routine, expr, terms, ExpressionPattern.BLOCK, -1, -1);
	}


	private static ExpressionPattern create
		(MethodScope routine, Expression expr, ObjectList terms, int type,
		 int in, int out)
	{
		Type[] t = new Type[terms.size];
		for (int i = terms.size - 1; i >= 0; i--)
		{
			t[i] = ((Local) terms.get (i)).getType ();
		}
		XMethod m = routine.createAndDeclareMethod
			((type == ExpressionPattern.CONDITION) ? "condition" : (type == ExpressionPattern.BLOCK) ? "block"
			 : "unknown",
			 (type == ExpressionPattern.CONDITION) ? Type.BOOLEAN : Type.VOID);
		if (type == ExpressionPattern.CONDITION)
		{
			routine.addExpression (new Return (routine, Type.BOOLEAN).add (expr));
		}
		else
		{
			routine.addExpression (expr);
		}
		return new ExpressionPattern (t, in, out, type, m);
	}


}
