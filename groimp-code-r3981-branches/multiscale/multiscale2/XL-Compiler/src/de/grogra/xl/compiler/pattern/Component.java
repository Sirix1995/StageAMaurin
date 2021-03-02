
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

import de.grogra.util.*;
import de.grogra.xl.compiler.*;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.util.ObjectList;

public abstract class Component
{
	public static final I18NBundle I18N = Compiler.I18N;

	final ObjectList<PatternData> dependsOn = new ObjectList<PatternData> ();
	final ProblemReporter problems;
	
	
	public Component (ProblemReporter problems)
	{
		this.problems = problems;
	}

	
	public void addDependency (PatternData dep)
	{
		dependsOn.addIfNotContained (dep);
	}

}
