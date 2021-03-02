/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 * 
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * --------------------------
 * LibraryReferencePanel.java
 * --------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LibraryPanel.java,v 1.3 2005/10/18 13:19:13 mungady Exp $
 *
 * Changes
 * -------
 * 28-Feb-2002 : Version 1 (DG);
 * 08-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * A panel containing a table that lists the libraries used in a project.
 * <P>
 * Used in the AboutFrame class.
 *
 * @author David Gilbert
 */
public class LibraryPanel extends JPanel {

    /** The table. */
    private JTable table;

    /** The data. */
    private TableModel model;

    /**
     * Constructs a LibraryPanel.
     *
     * @param libraries  a list of libraries (represented by Library objects).
     */
    public LibraryPanel(final List libraries) {

        setLayout(new BorderLayout());
        this.model = new LibraryTableModel(libraries);
        this.table = new JTable(this.model);
        add(new JScrollPane(this.table));

    }

}
