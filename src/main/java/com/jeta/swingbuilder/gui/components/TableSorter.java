package com.jeta.swingbuilder.gui.components;

import java.util.Date;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel)
 * and itself implements TableModel. TableSorter does not store or copy the data
 * in the TableModel, instead it maintains an array of integers which it keeps
 * the same size as the number of rows in its model. When the model changes it
 * notifies the sorter that something has changed eg. "rowsAdded" so that its
 * internal array of integers can be reallocated. As requests are made of the
 * sorter (like getValueAt(row, col) it redirects them to its model via the
 * mapping array. That way the TableSorter appears to hold another copy of the
 * table with the rows in a different order. The sorting algorthm used is stable
 * which means that it does not move around rows when its comparison function
 * returns 0 to denote that they are equivalent.
 * 
 * @version 1.5 12/17/97
 * @author Philip Milne
 */
public class TableSorter extends TableMap {
	int m_indexes[];
	private int m_sortingColumn;

	// the current sort mode
	private SortMode m_sortmode;

	public TableSorter() {
		m_indexes = new int[0]; // for consistency
		m_sortmode = SortMode.NONE;
	}

	public TableSorter(TableModel model) {
		this();
		setModel(model);
	}

	public void checkModel() {
		if (m_indexes.length != model.getRowCount()) {
			if (FormDesignerUtils.isDebug()) {
				System.err.println("Sorter not informed of a change in model:  indexes.length = " + m_indexes.length + "  model.rowCount = "
						+ model.getRowCount());
			}
		}
	}

	public int compareRowsByColumn(int row1, int row2, int column) {
		Class type = model.getColumnClass(column);
		TableModel data = model;

		// Check for nulls.

		Object o1 = data.getValueAt(row1, column);
		Object o2 = data.getValueAt(row2, column);

		// If both values are null, return 0.
		if (o1 == null && o2 == null) {
			return 0;
		}
		else if (o1 == null) {
			// Define null less than everything.
			return -1;
		}
		else if (o2 == null) {
			return 1;
		}

		/*
		 * We copy all returned values from the getValue call in case an
		 * optimised model is reusing one object to return many values. The
		 * Number subclasses in the JDK are immutable and so will not be used in
		 * this way but other subclasses of Number might want to do this to save
		 * space and avoid unnecessary heap allocation.
		 */

		if (type.getSuperclass() == java.lang.Number.class) {
			Number n1 = (Number) data.getValueAt(row1, column);
			double d1 = n1.doubleValue();
			Number n2 = (Number) data.getValueAt(row2, column);
			double d2 = n2.doubleValue();

			if (d1 < d2) {
				return -1;
			}
			else if (d1 > d2) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else if (type == java.util.Date.class) {
			Date d1 = (Date) data.getValueAt(row1, column);
			long n1 = d1.getTime();
			Date d2 = (Date) data.getValueAt(row2, column);
			long n2 = d2.getTime();

			if (n1 < n2) {
				return -1;
			}
			else if (n1 > n2) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else if (type == String.class) {
			String s1 = (String) data.getValueAt(row1, column);
			String s2 = (String) data.getValueAt(row2, column);
			int result = s1.compareToIgnoreCase(s2);

			if (result < 0) {
				return -1;
			}
			else if (result > 0) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else if (type == Boolean.class) {
			Boolean bool1 = (Boolean) data.getValueAt(row1, column);
			boolean b1 = bool1.booleanValue();
			Boolean bool2 = (Boolean) data.getValueAt(row2, column);
			boolean b2 = bool2.booleanValue();

			if (b1 == b2) {
				return 0;
			}
			else if (b1) { // Define false < true
				return 1;
			}
			else {
				return -1;
			}
		}
		else {
			Object v1 = data.getValueAt(row1, column);
			String s1 = v1.toString();
			Object v2 = data.getValueAt(row2, column);
			String s2 = v2.toString();
			int result = s1.compareTo(s2);

			if (result < 0) {
				return -1;
			}
			else if (result > 0) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}

	public int compare(int row1, int row2) {
		int result = compareRowsByColumn(row1, row2, m_sortingColumn);
		if (result != 0) {
			return (m_sortmode == SortMode.ASCENDING) ? result : -result;
		}
		return 0;
	}

	/**
	 * @return the actual model row for the given index
	 */
	public int getModelRow(int index) {
		return m_indexes[index];
	}

	/**
	 * @return the indexes for this sorter
	 */
	int[] getIndexes() {
		return m_indexes;
	}

	// The mapping only affects the contents of the data rows.
	// Pass all requests to these rows through the mapping array: "m_indexes".
	public Object getValueAt(int aRow, int aColumn) {
		checkModel();
		return model.getValueAt(m_indexes[aRow], aColumn);
	}

	/**
	 * Handles data inserted into the model. We update all indexes affected by
	 * the insert (but we don't resort).
	 * 
	 * @param firstRow
	 *            the first row of the range of rows that were inserted
	 * @param lastRow
	 *            the last row of the range of rows that were inserted
	 */
	void handleDataDeleted(int firstRow, int lastRow) {
		int oldsize = m_indexes.length;
		int delta = lastRow - firstRow + 1;
		int newsize = oldsize - delta;

		// create the new set of indexes, and copy the data over
		int[] newindexes = new int[newsize];
		System.arraycopy(m_indexes, 0, newindexes, 0, firstRow);

		// we need to shift all affected indexes up as well as correct the index
		// values
		int newindex = firstRow;
		for (int index = lastRow + 1; index < oldsize; index++) {
			newindexes[newindex] = m_indexes[index];
			if (newindexes[newindex] >= firstRow)
				newindexes[newindex] -= delta;

			newindex++;
		}
		m_indexes = newindexes;
	}

	/**
	 * Handles data inserted into the model. We update all indexes affected by
	 * the insert (but we don't resort).
	 * 
	 * @param firstRow
	 *            the first row of the range of rows that were inserted
	 * @param lastRow
	 *            the last row of the range of rows that were inserted
	 */
	void handleDataInserted(int firstRow, int lastRow) {
		int oldsize = m_indexes.length;
		int delta = lastRow - firstRow + 1;
		int newsize = oldsize + delta;

		// create the new set of indexes, and copy the data over
		int[] newindexes = new int[newsize];
		if (oldsize > 0) {
			System.arraycopy(m_indexes, 0, newindexes, 0, oldsize);

			// we need to shift all affected indexes down as well as correct the
			// index values
			for (int index = (newsize - 1); index >= (newsize - delta); index--) {
				newindexes[index] = newindexes[index - delta];
				if (newindexes[index] >= firstRow)
					newindexes[index] += delta;
			}
		}

		for (int index = firstRow; index <= lastRow; index++) {
			newindexes[index] = index; // set to unity for newly added rows
		}

		m_indexes = newindexes;
	}

	public void reallocateIndexes() {
		int rowCount = model.getRowCount();

		// Set up a new array of indexes with the right number of elements
		// for the new data model.
		m_indexes = new int[rowCount];

		// Initialise with the identity mapping.
		for (int row = 0; row < rowCount; row++) {
			m_indexes[row] = row;
		}
	}

	void setIndexes(int[] indexes) {
		m_indexes = indexes;
		super.tableChanged(new TableModelEvent(this));
	}

	public void setModel(TableModel model) {
		super.setModel(model);
		if (model == null)
			return;

		reallocateIndexes();

		// let's get table changed events for the model, so that we can update
		// our indexes according. For example, if the user adds a row to the end
		// of the model, we need to make sure our indexes are updated to include
		// the newly added rows (we don't resort though)
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				// okay, let's handle the various cases
				if (e.getType() == TableModelEvent.INSERT) {
					// data was added to model, so let's update only those
					// indexes
					// that were affected
					int firstrow = e.getFirstRow();
					int lastrow = e.getLastRow();
					handleDataInserted(firstrow, lastrow);
				}
				else if (e.getType() == TableModelEvent.DELETE) {
					// data was removed from the model, so let's update only
					// those indexes
					// that were affected
					int firstrow = e.getFirstRow();
					int lastrow = e.getLastRow();
					handleDataDeleted(firstrow, lastrow);
				}
				else {
					// System.out.println( "TableSorter got unknown event:
					// sortmode = " + m_sortmode + " col = " + m_sortingColumn
					// );
					// assume that the entire table has changed
					reallocateIndexes();
					if (m_sortmode != SortMode.NONE)
						sort(this);
				}
				// we don't worry about updates, the user must manually resort
				// if
				// he wants the data in sorted order
				fireTableChanged(e);
			}
		});

	}

	public void sort(Object sender) {
		checkModel();
		// n2sort();
		// qsort(0, indexes.length-1);
		shuttlesort((int[]) m_indexes.clone(), m_indexes, 0, m_indexes.length);
		// System.out.println("Compares: "+compares);
	}

	public void n2sort() {
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = i + 1; j < getRowCount(); j++) {
				if (compare(m_indexes[i], m_indexes[j]) == -1) {
					swap(i, j);
				}
			}
		}
	}

	// This is a home-grown implementation which we have not had time
	// to research - it may perform poorly in some circumstances. It
	// requires twice the space of an in-place algorithm and makes
	// NlogN assigments shuttling the values between the two
	// arrays. The number of compares appears to vary between N-1 and
	// NlogN depending on the initial order but the main reason for
	// using it here is that, unlike qsort, it is stable.
	public void shuttlesort(int from[], int to[], int low, int high) {

		if (high - low < 2) {
			return;
		}
		int middle = (low + high) / 2;
		shuttlesort(to, from, low, middle);
		shuttlesort(to, from, middle, high);

		int p = low;
		int q = middle;

		/*
		 * This is an optional short-cut; at each recursive call, check to see
		 * if the elements in this subset are already ordered. If so, no further
		 * comparisons are needed; the sub-array can just be copied. The array
		 * must be copied rather than assigned otherwise sister calls in the
		 * recursion might get out of sinc. When the number of elements is three
		 * they are partitioned so that the first set, [low, mid), has one
		 * element and and the second, [mid, high), has two. We skip the
		 * optimisation when the number of elements is three or less as the
		 * first compare in the normal merge will produce the same sequence of
		 * steps. This optimisation seems to be worthwhile for partially ordered
		 * lists but some analysis is needed to find out how the performance
		 * drops to Nlog(N) as the initial order diminishes - it may drop very
		 * quickly.
		 */

		if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
			for (int i = low; i < high; i++) {
				to[i] = from[i];
			}
			return;
		}

		// A normal merge.

		for (int i = low; i < high; i++) {
			if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
				to[i] = from[p++];
			}
			else {
				to[i] = from[q++];
			}
		}
	}

	public void swap(int i, int j) {
		int tmp = m_indexes[i];
		m_indexes[i] = m_indexes[j];
		m_indexes[j] = tmp;
	}

	public void setValueAt(Object aValue, int aRow, int aColumn) {
		checkModel();
		model.setValueAt(aValue, m_indexes[aRow], aColumn);
	}

	public void sortByColumn(int column) {
		sortByColumn(column, SortMode.ASCENDING);
	}

	public void sortByColumn(int column, SortMode mode) {
		m_sortingColumn = column;
		m_sortmode = mode;
		if (mode == SortMode.ASCENDING) {
			sort(this);
			super.tableChanged(new TableModelEvent(this));
		}
		else if (mode == SortMode.DESCENDING) {
			sort(this);
			super.tableChanged(new TableModelEvent(this));
		}
		else {
			// no sort
			reallocateIndexes();
			super.tableChanged(new TableModelEvent(this));
		}
	}

	// There is no-where else to put this.
	// Add a mouse listener to the Table to trigger a table sort
	// when a column heading is clicked in the JTable.
	public void addMouseListenerToHeaderInTable(JTable table) {
		table.setColumnSelectionAllowed(false);
		JTableHeader th = table.getTableHeader();
		th.addMouseListener(new TableSorterHeaderMouseAdapter(this, table));
	}
}
