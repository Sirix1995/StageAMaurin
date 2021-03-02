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

package de.grogra.pf.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.statistics.HistogramBin;

import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;
import de.grogra.pf.io.ObjectSourceImpl;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Showable;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.registry.PanelFactory;
import de.grogra.util.MimeType;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>Dataset</code> contains a set of {@link de.grogra.pf.data.Datacell}s
 * which are arranged in a tabular scheme, i.e., in rows and columns.
 *
 * @author Ole Kniemeyer
 */
public final class Dataset implements Shareable, java.io.Serializable, Showable
{
	public static final MimeType MIME_TYPE = MimeType.valueOf (Dataset.class);

	private static final long serialVersionUID = 4519569933099691755L;

	private transient DatasetGroup group;
	private transient ArrayList<TableModelListener> tmListeners;
	private transient SharedObjectProvider soProvider;
	private transient ObjectList refs = null;

	private String title;
	private String categoryLabel;
	private String valueLabel;
	private final ArrayList<ArrayList<Datacell>> rows = new ArrayList<ArrayList<Datacell>> ();
	final ArrayList<Comparable> rowKeys = new ArrayList<Comparable> ();
	final ArrayList<Comparable> columnKeys = new ArrayList<Comparable> ();
	final ArrayList<ArrayList<HistogramBin>> bins = new ArrayList<ArrayList<HistogramBin>> ();
	private DomainOrder order;
	boolean seriesInRows = false;

	private static final Datacell NULL_CELL = new Datacell ((Dataset) null);

	private transient DatasetAdapter adapter;
	private transient HistogramAdapter histogram;
	private transient StatisticsAdapter statistics;
	private transient TableModel tableAdapter;

	private final class Table implements TableModel
	{
		Dataset getDataset ()
		{
			return Dataset.this;
		}

		public void addTableModelListener (TableModelListener l)
		{
			synchronized (Dataset.this)
			{
				if (tmListeners == null)
				{
					tmListeners = new ArrayList<TableModelListener> ();
				}
				tmListeners.add (l);
			}
		}

		public void removeTableModelListener (TableModelListener l)
		{
			synchronized (Dataset.this)
			{
				if (tmListeners != null)
				{
					tmListeners.add (l);
				}
			}
		}

		public int getColumnCount ()
		{
			return Dataset.this.getColumnCount () + 1;
		}

		public int getRowCount ()
		{
			return Dataset.this.getRowCount ();
		}

		public boolean isCellEditable (int rowIndex, int columnIndex)
		{
			return columnIndex > 0;
		}

		public Class getColumnClass (int columnIndex)
		{
			return (columnIndex == 0) ? Object.class : Datacell.class;
		}

		public Object getValueAt (int rowIndex, int columnIndex)
		{
			return (columnIndex == 0) ? getRowKey (rowIndex)
					: (Object) getCellOrDefault (rowIndex, columnIndex - 1);
		}

		public void setValueAt (Object value, int rowIndex, int columnIndex)
		{
			if (value instanceof Datacell)
			{
				getCell (rowIndex, columnIndex - 1).set ((Datacell) value);
			}
		}

		public String getColumnName (int columnIndex)
		{
			return (columnIndex == 0) ? "#" : String
				.valueOf (getColumnKey (columnIndex - 1));
		}
	}

	/**
	 * Returns a view of this dataset as an instance of
	 * {@link DatasetAdapter}. Note that the meanings of rows
	 * and columns are exchanged.
	 * 
	 * @return dataset as a {@link DatasetAdapter}
	 */
	public synchronized DatasetAdapter asDatasetAdapter ()
	{
		if (adapter == null)
		{
			adapter = new DatasetAdapter (this);
		}
		return adapter;
	}

	/**
	 * Returns a histogram of this dataset.
	 * 
	 * @return dataset as a {@link DatasetAdapter}
	 */
	public synchronized HistogramAdapter toHistogram ()
	{
		if (histogram == null)
		{
			histogram = new HistogramAdapter (this);
		}
		return histogram;
	}

	/**
	 * Returns a statistics view of this dataset.
	 * 
	 * @return dataset as a {@link StatisticsAdapter}
	 */
	public synchronized StatisticsAdapter toStatistics ()
	{
		if (statistics == null)
		{
			statistics = new StatisticsAdapter (this);
		}
		return statistics;
	}

	/**
	 * Returns a view of this dataset as an instance of
	 * {@link TableModel}.
	 * 
	 * @return dataset as a {@link TableModel}
	 */
	public synchronized TableModel asTableModel ()
	{
		if (tableAdapter == null)
		{
			tableAdapter = new Table ();
		}
		return tableAdapter;
	}

	void fireDatasetChanged (Object source)
	{
		ArrayList l;
		int n;
		DatasetAdapter ad = asDatasetAdapter ();
		HistogramAdapter h = toHistogram ();
		StatisticsAdapter s = toStatistics ();
		synchronized (this)
		{
			l = (ad.listeners != null) ? new ArrayList (ad.listeners)
					: new ArrayList ();
			if (h.listeners != null)
			{
				l.addAll (h.listeners);
			}
			if (s.listeners != null)
			{
				l.addAll (s.listeners);
			}
			n = l.size ();
			if (tmListeners != null)
			{
				l.addAll (tmListeners);
			}
		}
		if (n > 0)
		{
			DatasetChangeEvent e = new DatasetChangeEvent (source, ad);
			for (int i = 0; i < n; i++)
			{
				try
				{
					((DatasetChangeListener) l.get (i)).datasetChanged (e);
				}
				catch (RuntimeException ex)
				{
					ex.printStackTrace ();
				}
			}
		}
		if (l.size () > n)
		{
			TableModelEvent e = new TableModelEvent (asTableModel ());
			for (int i = n; i < l.size (); i++)
			{
				((TableModelListener) l.get (i)).tableChanged (e);
			}
		}
	}

	public void initProvider (SharedObjectProvider provider)
	{
		this.soProvider = provider;
	}

	public SharedObjectProvider getProvider ()
	{
		return soProvider;
	}

	public synchronized void addReference (SharedObjectReference ref)
	{
		if (refs == null)
		{
			refs = new ObjectList (4, false);
		}
		refs.add (ref);
	}

	public synchronized void removeReference (SharedObjectReference ref)
	{
		if (refs != null)
		{
			refs.remove (ref);
		}
	}

	public synchronized void appendReferencesTo (java.util.List out)
	{
		if (refs != null)
		{
			out.addAll (refs);
		}
	}

	public DatasetGroup getGroup ()
	{
		return group;
	}

	public void setGroup (DatasetGroup group)
	{
		this.group = group;
	}

	public Dataset setSeriesInRows (boolean value)
	{
		if (value != seriesInRows)
		{
			seriesInRows = value;
			fireDatasetChanged (this);
		}
		return this;
	}

	public boolean hasSeriesInRows ()
	{
		return seriesInRows;
	}

	/**
	 * Clears this dataset. This removes all rows, columns, and their keys.
	 * 
	 * @return this dataset
	 */
	public Dataset clear ()
	{
		rows.clear ();
		columnKeys.clear ();
		rowKeys.clear ();
		return this;
	}

	private ArrayList<Datacell> getRow0 (int row)
	{
		boolean c = false;
		while (rows.size () <= row)
		{
			rows.add (new ArrayList ());
			c = true;
		}
		while (rowKeys.size () <= row)
		{
			rowKeys.add (getDefaultRowKey (rowKeys.size ()));
			c = true;
		}
		if (c)
		{
			fireDatasetChanged (this);
		}
		return rows.get (row);
	}

	/**
	 * Returns the datacell at <code>row</code> and <code>column</code>.
	 * The rows and columns are extended if necessary.
	 * 
	 * @param row row index of cell
	 * @param col column index of cell
	 * @return datacell at specified location
	 */
	public Datacell getCell (int row, int col)
	{
		ArrayList<Datacell> columns = getRow0 (row);
		boolean c = false;
		while (columns.size () <= col)
		{
			columns.add (new Datacell (this));
			c = true;
		}
		while (columnKeys.size () <= col)
		{
			columnKeys.add (getDefaultColumnKey (columnKeys.size ()));
			c = true;
		}
		if (c)
		{
			fireDatasetChanged (this);
		}
		return columns.get (col);
	}

	Datacell getCellOrDefault (int row, int col)
	{
		if (row < rows.size ())
		{
			ArrayList<Datacell> r = rows.get (row);
			if (col < r.size ())
			{
				return r.get (col);
			}
		}
		return NULL_CELL;
	}

	Datacell getCell0 (int row, int col)
	{
		return seriesInRows ? getCellOrDefault (col, row) : getCellOrDefault (row, col);
	}

	public boolean hasCell (int row, int col)
	{
		if (row >= rows.size ())
		{
			return false;
		}
		ArrayList<Datacell> r = rows.get (row);
		if (col >= r.size ())
		{
			return false;
		}
		return !r.get (col).isNull ();
	}

	boolean hasCell0 (int row, int col)
	{
		return seriesInRows ? hasCell (col, row) : hasCell (row, col);
	}
	
	public DomainOrder getDomainOrder ()
	{
		return order;
	}

	public void setDomainOrder (DomainOrder order)
	{
		this.order = order;
		fireDatasetChanged (this);
	}

	public String getTitle ()
	{
		return title;
	}

	public Dataset setTitle (String s)
	{
		title = s;
		return this;
	}

	public String getCategoryLabel ()
	{
		return categoryLabel;
	}

	public Dataset setCategoryLabel (String s)
	{
		categoryLabel = s;
		return this;
	}

	public String getValueLabel ()
	{
		return valueLabel;
	}

	public Dataset setValueLabel (String s)
	{
		valueLabel = s;
		return this;
	}

	/**
	 * Returns the number of rows of this dataset.
	 * 
	 * @return number of rows
	 */
	public int getRowCount ()
	{
		return rows.size ();
	}

	public int getRowCount (int column)
	{
		for (int i = rows.size (); i > 0; i--)
		{
			ArrayList<Datacell> r = rows.get (i - 1);
			if ((column < r.size ())
				&& !r.get (column).isNull ())
			{
				return i;
			}
		}
		return 0;
	}

	/**
	 * Returns the number of columns of this dataset.
	 * 
	 * @return number of columns
	 * @see #getColumnCount(int)
	 */
	public int getColumnCount ()
	{
		return rows.isEmpty () ? 0 : columnKeys.size ();
	}

	/**
	 * Returns the number of columns which are defined in <code>row</code>.
	 * The returned value does not exceed {@link #getColumnCount()}.
	 * 
	 * @param row row index
	 * @return number of columns in <code>row</code>
	 */
	public int getColumnCount (int row)
	{
		return getRow0 (row).size ();
	}

	/**
	 * Returns <code>row</code> as a dataseries.
	 * 
	 * @param row row index
	 * @return specified row represented as a dataseries
	 */
	public Dataseries getRow (int row)
	{
		return new Dataseries (this, false, row);
	}

	/**
	 * Returns <code>column</code> as a dataseries.
	 * 
	 * @param column column index
	 * @return specified column represented as a dataseries
	 */
	public Dataseries getColumn (int column)
	{
		return new Dataseries (this, true, column);
	}

	/**
	 * Adds a new row to this dataset and returns it as a dataseries.
	 * 
	 * @return added row as a dataseries
	 */
	public Dataseries addRow ()
	{
		return addRow (getDefaultRowKey (getRowCount ()));
	}

	/**
	 * Adds a new column to this dataset and returns it as a dataseries.
	 * 
	 * @return added column as a dataseries
	 */
	public Dataseries addColumn ()
	{
		return addColumn (getDefaultColumnKey (getColumnCount ()));
	}

	/**
	 * Adds a new row to this dataset and returns it as a dataseries.
	 * 
	 * @param key row key to use
	 * @return added row as a dataseries
	 */
	public Dataseries addRow (Comparable key)
	{
		setRowKey (getRowCount (), key);
		return getRow (getRowCount ());
	}

	/**
	 * Adds a new column to this dataset and returns it as a dataseries.
	 * 
	 * @param key column key to use
	 * @return added column as a dataseries
	 */
	public Dataseries addColumn (Comparable key)
	{
		setColumnKey (getColumnCount (), key);
		return getColumn (getColumnCount ());
	}

	public Dataseries operator$shl (Number v)
	{
		return addRow ().set (0, v);
	}

	public Dataseries operator$shl (double[] v)
	{
		return addRow ().operator$shl (v);
	}

	/**
	 * Returns the key for the row with index <code>row</code>.
	 * If such a key has not been set explicitly by
	 * {@link #setRowKey(int, Comparable)}, an instance of
	 * {@link Integer} is returned, its value being <code>row + 1</code>. 
	 * 
	 * @param row row index
	 * @return key for row
	 */
	public Comparable getRowKey (int row)
	{
		return (row >= rowKeys.size ()) ? null : (Comparable) rowKeys.get (row);
	}

	/**
	 * Returns the key for the column with index <code>column</code>.
	 * If such a key has not been set explicitly by
	 * {@link #setColumnKey(int, Comparable)}, an instance of
	 * {@link Character} is returned, its value being <code>column + 'A'</code>. 
	 * 
	 * @param column column index
	 * @return key for column
	 */
	public Comparable getColumnKey (int column)
	{
		return (column >= columnKeys.size ()) ? null : (Comparable) columnKeys
			.get (column);
	}

	/**
	 * Sets the key for row with index <code>row</code> to <code>key</code>
	 * 
	 * @param row row index
	 * @param key key for row
	 * @return this dataset
	 * @see #getRowKey(int)
	 */
	public Dataset setRowKey (int row, Comparable key)
	{
		while (rowKeys.size () <= row)
		{
			rowKeys.add (getDefaultRowKey (rowKeys.size ()));
		}
		rowKeys.set (row, key);
		fireDatasetChanged (this);
		return this;
	}

	/**
	 * Sets the key for column with index <code>column</code> to <code>key</code>
	 * 
	 * @param col column index
	 * @param key key for column
	 * @return this dataset
	 * @see #getColumnKey(int)
	 */
	public Dataset setColumnKey (int col, Comparable key)
	{
		while (columnKeys.size () <= col)
		{
			columnKeys.add (getDefaultColumnKey (columnKeys.size ()));
		}
		columnKeys.set (col, key);
		fireDatasetChanged (this);
		return this;
	}

	ArrayList<HistogramBin> getBins (int col)
	{
		while (bins.size () <= col)
		{
			bins.add (new ArrayList<HistogramBin> ());
		}
		return bins.get (col);
	}

	public Dataset setHistogramBins (int col, double min, double max, int count)
	{
		ArrayList<HistogramBin> b = getBins (col);
		b.clear ();
		double f = (max - min) / count;
		for (int i = 0; i < count; i++)
		{
			b.add (new HistogramBin (min + i * f, min + (i + 1) * f));
		}
		fireDatasetChanged (this);
		return this;
	}

	public Dataset setHistogramBins (int col, List<HistogramBin> bins)
	{
		ArrayList<HistogramBin> b = getBins (col);
		b.clear ();
		b.addAll (bins);
		fireDatasetChanged (this);
		return this;
	}

	public void show (Context ctx)
	{
		showInPanel (PanelFactory.getAndShowPanel (ctx, "/ui/panels/table",
			null));
	}

	public static void export (Item item, Object info, Context context)
	{
		Panel p = context.getPanel ();
		if (p == null)
		{
			return;
		}
		TableModel t = UIToolkit.get (p).getTable (p.getContent ());
		if (t instanceof Table)
		{
			Dataset ds = ((Table) t).getDataset ();
			context.getWorkbench ().export (
				new ObjectSourceImpl (ds, ds.getTitle (), MIME_TYPE, context
					.getWorkbench ().getRegistry ().getRootRegistry (), null));
		}
	}

	public void showInPanel (Panel panel)
	{
		panel.setContent (UIToolkit.get (panel).createTable (asTableModel (),
			panel));
	}

	private Comparable getDefaultColumnKey (int column)
	{
		return new Character ((char) (column + 'A'));
	}

	private Comparable getDefaultRowKey (int column)
	{
		return Integer.valueOf (column + 1);
	}

}
