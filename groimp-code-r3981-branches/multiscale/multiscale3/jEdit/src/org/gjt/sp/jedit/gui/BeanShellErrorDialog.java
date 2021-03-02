/*
 * BeanShellErrorDialog.java - BeanShell execution error dialog box
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

/*
 * Modifications for GroIMP-integration made by Ole Kniemeyer 2005/08/17
 * Copyright (C) 2005 Lehrstuhl Grafische Systeme, BTU Cottbus
 */

package org.gjt.sp.jedit.gui;

//{{{ Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.gjt.sp.jedit.*;
//}}}

/**
 * A dialog box showing a stack trace. Perhaps badly named, since any error, not
 * just a BeanShell error can be shown.
 * @author Slava Pestov
 * @version $Id: BeanShellErrorDialog.java,v 1.7 2004/04/05 03:11:47 spestov Exp $
 */
public class BeanShellErrorDialog extends TextAreaDialog
{
	// 2005/08/17: modified by Ole Kniemeyer
	public BeanShellErrorDialog(View view, Throwable t)
	{
		super(view.getFrame(),"beanshell-error",t);
	}
	// end of modification
}
