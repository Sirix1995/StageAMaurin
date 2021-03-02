/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.imp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Context;
import de.grogra.util.I18NBundle;

public class SlotOverview {

	private final Context ctx;
	private I18NBundle thisI18NBundle;
	private final GraphManager graph;

	private final JPanel panel = new JPanel ();
	
	private JCheckBox idCheckBox;
	
	public SlotOverview (Context ctx, GraphManager graph) {
		this.ctx = ctx;
		this.graph = graph;
	}

	public JScrollPane getPanel () {
		updateUI ();
		return new JScrollPane(panel);
	}

	public void updateUI () {
		Registry registry = ctx.getWorkbench ().getRegistry ();
		thisI18NBundle = registry.getPluginDescriptor ("de.grogra.imp").getI18NBundle ();

		panel.removeAll ();
		panel.setLayout (new BorderLayout());

		panel.setBorder (BorderFactory.createEmptyBorder (2, 2, 2, 2));

		SortedMap<String, Set<String>> inSet = registry.getInputSlotMap ();
		SortedMap<String, Set<String>> outSet = registry.getOutputSlotMap ();
		Set<String> keySet = new TreeSet<String> ();
		keySet.addAll (inSet.keySet ());
		keySet.addAll (outSet.keySet ());
		Object data[][] = new Object[keySet.size ()][3];

		int ii = 0;
		String tmp ="";
		Node n = null;
		for (Iterator<String> i = keySet.iterator (); i.hasNext ();) {
			tmp = i.next ();
			if((n=graph.getNodeForName (graph.getRootComponentGraph (), tmp)) == null) continue;
			data[ii][0] = tmp;
			data[ii][1] = inSet.get (data[ii][0]) != null ?
				" "+inSet.get (data[ii][0]).toString ().replace("[", "").replace("]", "").replace(",", "\n") : " -";
			data[ii][2] = outSet.get (data[ii][0]) != null ?
				" "+outSet.get (data[ii][0]).toString ().replace("[", "").replace("]", "").replace(",", "\n") : " -";
			// add ID's
			data[ii][0] = ((String)data[ii][0]) + (idCheckBox.isSelected ()? "("+n.getId ()+")":"");
			ii++;
		}
		Object columnTitles[] = {
				thisI18NBundle.getString ("slotOverview.component.Name"),
				thisI18NBundle.getString ("slotOverview.inputslot.Name"),
				thisI18NBundle.getString ("slotOverview.outputslot.Name")};
		JTable table = new JTable (data, columnTitles);
		table.setEnabled(false);
		TableColumnModel cmodel = table.getColumnModel ();
		cmodel.getColumn (0).setCellRenderer (new TextAreaRenderer (new Color(0,0,0)));
		cmodel.getColumn (1).setCellRenderer (new TextAreaRenderer (new Color(75,210,75)));
		cmodel.getColumn (2).setCellRenderer (new TextAreaRenderer (new Color(210,75,75)));

		panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		panel.add (table, BorderLayout.CENTER);
		panel.add (Box.createHorizontalStrut (2), BorderLayout.EAST);
		panel.add (Box.createHorizontalStrut (2), BorderLayout.WEST);
		panel.add (Box.createVerticalStrut (2), BorderLayout.NORTH);
		panel.add (Box.createVerticalStrut (2), BorderLayout.SOUTH);
		panel.validate ();
		panel.repaint ();
	}

	private class TextAreaRenderer extends JTextArea implements TableCellRenderer {
		private static final long serialVersionUID = 2347648761L;
		
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer ();
		/** map from table to map of rows to map of column heights */
		private final Map cellSizes = new HashMap ();

		private final Color color;
		
		public TextAreaRenderer (Color color) {
			setLineWrap (true);
			setWrapStyleWord (true);
			this.color = color;
		}

		@Override
		public Component getTableCellRendererComponent (JTable table, Object obj, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// set the colours, etc. using the standard for that platform
			adaptee.getTableCellRendererComponent (table, obj, isSelected,hasFocus, row, column);
			setForeground (color);
			setBackground (adaptee.getBackground ());
			setBorder (adaptee.getBorder ());
			setFont (adaptee.getFont ());
			setText (adaptee.getText ());

			TableColumnModel columnModel = table.getColumnModel ();
			setSize (columnModel.getColumn (column).getWidth (), 1000);
			int height_wanted = (int) getPreferredSize ().getHeight ()+2;
			addSize (table, row, column, height_wanted);
			height_wanted = findTotalMaximumRowSize (table, row);
			if (height_wanted != table.getRowHeight (row)) {
				table.setRowHeight (row, height_wanted);
			}
			return this;
		}

		private void addSize (JTable table, int row, int column, int height)
		{
			Map rows = (Map) cellSizes.get (table);
			if (rows == null) {
				cellSizes.put (table, rows = new HashMap ());
			}
			Map rowheights = (Map) rows.get (new Integer (row));
			if (rowheights == null) {
				rows.put (new Integer (row), rowheights = new HashMap ());
			}
			rowheights.put (new Integer (column), new Integer (height));
		}

		/**
		 * Look through all columns and get the renderer.  If it is
		 * also a TextAreaRenderer, we look at the maximum height in
		 * its hash table for this row.
		 */
		private int findTotalMaximumRowSize (JTable table, int row)
		{
			int maximum_height = 0;
			Enumeration columns = table.getColumnModel ().getColumns ();
			while (columns.hasMoreElements ())
			{
				TableColumn tc = (TableColumn) columns.nextElement ();
				TableCellRenderer cellRenderer = tc.getCellRenderer ();
				if (cellRenderer instanceof TextAreaRenderer) {
					TextAreaRenderer tar = (TextAreaRenderer) cellRenderer;
					maximum_height = Math.max (maximum_height, tar.findMaximumRowSize (table, row));
				}
			}
			return maximum_height;
		}

		private int findMaximumRowSize (JTable table, int row)
		{
			Map rows = (Map) cellSizes.get (table);
			if (rows == null) return 0;
			Map rowheights = (Map) rows.get (new Integer (row));
			if (rowheights == null) return 0;
			int maximum_height = 0;
			for (Iterator it = rowheights.entrySet ().iterator (); it.hasNext ();) {
				Map.Entry entry = (Map.Entry) it.next ();
				maximum_height = Math.max (maximum_height, ((Integer) entry.getValue ()).intValue ());
			}
			return maximum_height;
		}
	}

	public void setIdCheckBox (JCheckBox idCheckBox) {
		this.idCheckBox = idCheckBox;
	}
}
