
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

package de.grogra.graph.impl;

import de.grogra.persistence.*;

final class GraphXAApplier extends TransactionApplier
	implements GraphTransaction.Consumer
{

	public void addEdgeBits (Node source, Node target, int mask)
	{
		if (Transaction.isNotApplying (transaction))
		{
			throw new IllegalStateException
				("GraphThreadState.addEdges may only be invoked "
				 + "while applying a transaction");
		}
		source.getOrCreateEdgeTo (target).addEdgeBits (mask, transaction);
	}


	public void removeEdgeBits (Node source, Node target, int mask)
	{
		if (Transaction.isNotApplying (transaction))
		{
			throw new IllegalStateException
				("GraphThreadState.removeEdges may only be invoked "
				 + "while applying a transaction");
		}
		source.removeEdgeBitsTo (target, mask, transaction);
	}

}
