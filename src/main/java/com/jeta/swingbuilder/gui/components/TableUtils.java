/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.gui.components;

import java.awt.Container;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This class contains utility methods for doing common tasks with JTables
 * 
 * @author Jeff Tassin
 */
public class TableUtils {

	/**
	 * Clears the column header icons for a given table
	 */
	public static void clearColumnHeaders(JTable table) {
		if (table == null)
			return;

		TableColumnModel columnmodel = table.getColumnModel();
		// reset all other column sort modes to NONE at this point
		int count = columnmodel.getColumnCount();

		for (int index = 0; index < count; index++) {
			TableColumn tc = columnmodel.getColumn(index);
			Object robj = tc.getHeaderRenderer();
			if (robj instanceof SortedColumnHeaderRenderer)
				((SortedColumnHeaderRenderer) robj).setSortMode(SortMode.NONE);
		}
		JTableHeader th = table.getTableHeader();
		th.repaint();
	}

	/**
	 * Converts a table row the the corresponding model index. This is needed
	 * when the table is sorted. If the table is not sorted, the table index
	 * will equal the model index
	 * 
	 * @param table
	 *            the table whose index to convert
	 * @param index
	 *            the table index to convert
	 * @return the corresponding model index
	 */
	public static int convertTableToModelIndex(JTable table, int index) {
		if (index >= 0 && (table.getModel() instanceof TableSorter)) {
			TableSorter sorter = (TableSorter) table.getModel();
			return sorter.getModelRow(index);
		}
		else
			return index;
	}

	/**
	 * Creates a panel that contains a table that can be sorted can be sorted.
	 * The table is scrollable and has automatically resized subsequent columns
	 * 
	 * @param model
	 *            the data model for the table. Don't send in a TableSorter
	 *            here.
	 * @param sortable
	 *            if true, then we will return a table that has sortable
	 *            columns. This will wrap the tablemodel with a TableSorter.
	 * @return a container that contains the table.
	 */
	public static JTable createBasicTablePanel(TableModel model, boolean sortable) {
		JTable table = null;
		if (sortable) {
			TableSorter sorter = new TableSorter(model);
			table = new JTable(sorter);
			initializeTableSorter(sorter, table);
		}
		else {
			table = new JTable(model);
		}

		table.setCellSelectionEnabled(true);
		return table;
	}

	/**
	 * Intializes a table and a table sorter
	 */
	public static void initializeTableSorter(TableSorter sorter, JTable table) {
		sorter.addMouseListenerToHeaderInTable(table);

		// set the renderer for each column
		TableColumnModel columnmodel = table.getColumnModel();
		int count = columnmodel.getColumnCount();
		for (int index = 0; index < count; index++) {
			TableColumn column = columnmodel.getColumn(index);
			column.setHeaderRenderer(new SortedColumnHeaderRenderer());
		}
	}

	/**
	 * * Creates row header for table with row number (starting with 1)
	 * displayed
	 */
	public static void removeRowHeader(JTable table) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setRowHeader(null);
			}
		}
	}

	/** Creates row header for table with row number (starting with 1) displayed */
	public static TableRowHeader setRowHeader(JTable table) {
		return setRowHeader(table, -1);
	}

	/**
	 * Creats a row header for the given table. The row number is displayed to
	 * the left of the table ( starting with row 1).
	 * 
	 * @param table
	 *            the table to create the row header for
	 * @param headerWidth
	 *            the number of characters to size the header
	 */
	public static TableRowHeader setRowHeader(JTable table, int headerWidth) {
		boolean isok = false;

		TableRowHeader result = null;
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				result = new TableRowHeader(table);
				scrollPane.setRowHeaderView(result);
				isok = true;
			}
		}
		assert (isok);
		return result;
	}

	/**
	 * Stops any editing for a given cell on a table.
	 */
	public static void stopEditing(JTable table) {
		if (table == null) {
			assert (false);
			return;
		}

		if (table.isEditing()) {
			int row = table.getEditingColumn();
			int col = table.getEditingRow();
			if (row >= 0 && col >= 0) {
				javax.swing.CellEditor editor = table.getCellEditor(row, col);
				editor.stopCellEditing();
			}
		}
	}
}
