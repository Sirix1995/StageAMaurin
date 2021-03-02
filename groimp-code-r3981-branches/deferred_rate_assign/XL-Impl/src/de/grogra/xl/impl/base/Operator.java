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

package de.grogra.xl.impl.base;

import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.lang.ObjectToObjectGenerator;

/**
 * 
 * @author Ole Kniemeyer
 *
 * @param <N> node type
 */
public interface Operator<N> extends ObjectToObjectGenerator<N, NodeEdgePair<N>>
{
	int ONLY_CT_EDGES_MATCH = RuntimeModel.SPECIAL_MASK;

	int match (N node, Operator<N> op, NodeEdgePair<N> opResult);

	void evaluate (ObjectConsumer<? super NodeEdgePair<N>> cons, NodeEdgePair<N> x);
}
