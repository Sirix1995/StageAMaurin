
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

package de.grogra.grogra;

import java.io.IOException;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.grammar.LexicalException;
import de.grogra.grammar.RecognitionException;
import de.grogra.grammar.SemanticException;
import de.grogra.grammar.Token;
import de.grogra.grammar.Tokenizer;
import de.grogra.grammar.UnexpectedTokenException;
import de.grogra.graph.Graph;
import de.grogra.math.TMatrix4d;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;
import de.grogra.turtle.F;
import de.grogra.util.IOWrapException;
import de.grogra.xl.util.IntHashMap;
import de.grogra.xl.util.IntList;

public class DTGFilter extends FilterBase implements ObjectSource
{
	public static final MetaDataKey<Float> SCALE = new MetaDataKey<Float> ("scale");

	private static final class DTGTokenizer extends Tokenizer
	{
		static final int N = Token.MIN_UNUSED;
		static final int S = Token.MIN_UNUSED + 1;
		static final int E = Token.MIN_UNUSED + 2;
	
	
		DTGTokenizer ()
		{
			super (FLOAT_IS_DEFAULT | MINUS_IS_SIGN | EVALUATE_NUMBERS);
			addToken (N, "n");
			addToken (S, "S");
			addToken (E, "e");
		}
	
	
		void getTuple (Tuple3f tuple) throws IOException, LexicalException
		{
			tuple.set (getFloat (), getFloat (), getFloat ());
		}
		
	
		void consumeFloats (int n) throws IOException, LexicalException
		{
			for (int i = 0; i < n; i++)
			{
				getFloat ();
			}
		}

		@Override
		protected boolean isWhitespace (char c)
		{
			return (c < 32) || super.isWhitespace (c);
		}

		@Override
		protected boolean isIdentifierStart (char c)
		{
			return !isWhitespace (c) && super.isIdentifierStart (c);
		}

		@Override
		protected boolean isIdentifierPart (char c)
		{
			return !isWhitespace (c) && super.isIdentifierPart (c);
		}
	}
	
	
	private static final class ShootInfo extends Point3f
	{
		F shoot;
		int order;
		final Matrix3f xf = new Matrix3f ();
		final Vector3f tip = new Vector3f ();
	}


	public DTGFilter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.NODE);
	}


	public Object getObject () throws IOException
	{
		Token t;
		DTGTokenizer tokenizer = new DTGTokenizer ();
		int id, parentId, i;

		// association shoot id -> ShootInfo instance
		IntHashMap shoots = new IntHashMap (100);

		// contains pairs (id, parentId). edges have to be created from parentId-shoot to id-shoot 
		IntList links = new IntList ();

		float factor = getMetaData (SCALE, 0.001f);

		tokenizer.setSource (((ReaderSource) source).getReader (),
							 source.getSystemId ());
		try
		{
			tokenizer.consume (DTGTokenizer.N);
			Vector3f v = new Vector3f ();
			while ((t = tokenizer.getToken ()).getType () == DTGTokenizer.S)
			{
				ShootInfo info = new ShootInfo ();

				// read shoot data in DTG format into info
				id = tokenizer.getInt ();
				parentId = tokenizer.getInt ();
				tokenizer.getFloat ();
				DTGShoot shoot = new DTGShoot ();
				info.shoot = shoot;
				shoot.diameter = factor * tokenizer.getFloat ();
				tokenizer.getFloat (); // top diameter
				shoot.parameter = tokenizer.getFloat ();
				shoot.internodeCount = tokenizer.getInt ();
				shoot.color = tokenizer.getInt ();
				info.order = tokenizer.getInt ();
				tokenizer.getInt (); // scale
				shoot.generativeDistance = tokenizer.getInt ();
				shoot.relPosition = tokenizer.getFloat ();
				tokenizer.consumeFloats (6);
				tokenizer.getTuple (info); // base
				info.scale (factor);
				tokenizer.getTuple (info.tip); // tip
				info.tip.scale (factor);
				v.sub (info.tip, info);
				shoot.length = v.length ();
				tokenizer.getTuple (v); // head
				v.normalize ();
				info.xf.setColumn (2, v);
				tokenizer.getTuple (v); // left
				v.normalize ();
				info.xf.setColumn (0, v);
				tokenizer.getTuple (v); // up
				v.normalize ();
				info.xf.setColumn (1, v);

				// store shoot data and link from parent shoot to this shoot
				shoots.put (id, info);
				links.push (id).push (parentId);
			}
			if (t.getType () != DTGTokenizer.E)
			{
				throw new UnexpectedTokenException (t.getText (), "e").set (tokenizer);
			}
			ShootInfo root = null;
			Matrix3f m =  new Matrix3f ();
			Matrix4d m4d = new Matrix4d ();
			m4d.setIdentity ();
			
			while (!links.isEmpty ())
			{
				ShootInfo parent = (ShootInfo) shoots.get (links.pop (), null);
				ShootInfo child = (ShootInfo) shoots.get (links.pop (), null);
				
				// compute local transformation matrix between parent and child
				if (parent == null)
				{
					if (root != null)
					{
						throw new SemanticException ("Invalid parent")
							.set (tokenizer);
					}
					root = child;
					m.setIdentity ();
				}
				else
				{
					m.invert (parent.xf);
					child.sub (parent.tip);
				}
				m.transform (child);
				m.mul (m, child.xf);
				m4d.setRotationScale (m);
				m4d.m03 = child.x;
				m4d.m13 = child.y;
				m4d.m23 = child.z;
				
				// create transformation
				child.shoot.setTransform (new TMatrix4d (m4d));
				if (parent != null)
				{
					// create edge
					parent.shoot.addEdgeBitsTo
						(child.shoot,
						 (child.order == parent.order) ? Graph.SUCCESSOR_EDGE
						  : Graph.BRANCH_EDGE, null);
				}
			}
			if (root == null)
			{
				throw new SemanticException ("No root")
					.set (tokenizer);
			}
			return root.shoot;
		}
		catch (RecognitionException e)
		{
			throw new IOWrapException (e);
		}
	}

}
