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

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * This class listens for mouse events on the column headers of a sorted table.
 * When the user clicks a column, this class invokes the appropriate sort
 * routine for that column.
 * 
 * @author Jeff Tassin
 */
class TableSorterHeaderMouseAdapter extends MouseAdapter {
	private WeakReference m_sorterref;
	private WeakReference m_tableref;

	/**
	 * ctor
	 */
	public TableSorterHeaderMouseAdapter(TableSorter sorter, JTable table) {
		m_sorterref = new WeakReference(sorter);
		m_tableref = new WeakReference(table);
	}

	/**
	 * @return the underlying table sorter. This will be null if the table has
	 *         been gc'ed
	 */
	TableSorter getSorter() {
		return (TableSorter) m_sorterref.get();
	}

	/**
	 * @return the underlying table. This will be null if the table has been
	 *         gc'ed
	 */
	JTable getTable() {
		return (JTable) m_tableref.get();
	}

	/**
	 * MouseAdapter event
	 */
	public void mouseClicked(MouseEvent e) {
		JTable table = getTable();
		TableSorter sorter = getSorter();

		// sometimes the controller can be null here even if not garbage
		// collected
		if (sorter != null && table != null) {
			TableColumnModel columnmodel = table.getColumnModel();
			int viewcolumn = columnmodel.getColumnIndexAtX(e.getX());
			if (viewcolumn < 0)
				return;

			int modelcolumn = columnmodel.getColumn(viewcolumn).getModelIndex();

			TableColumn tablecol = columnmodel.getColumn(viewcolumn);
			SortedColumnHeaderRenderer renderer = null;
			Object obj = tablecol.getHeaderRenderer();
			if (obj instanceof SortedColumnHeaderRenderer)
				renderer = (SortedColumnHeaderRenderer) obj;

			if (e.getClickCount() == 1 && modelcolumn != -1) {
				SortMode mode = SortMode.NONE;
				int shiftpressed = e.getModifiers() & InputEvent.SHIFT_MASK;
				int ctrlpressed = e.getModifiers() & InputEvent.CTRL_MASK;

				if (shiftpressed != 0 && ctrlpressed != 0)
					mode = SortMode.NONE; // user explicitly chose natural
											// sort
				else if (shiftpressed != 0)
					mode = SortMode.DESCENDING; // user explicitly chose
												// descending sort
				else if (ctrlpressed != 0)
					mode = SortMode.ASCENDING; // user explicitly chose
												// ascending sort
				else {
					// then toggle through the sort states
					if (renderer != null) {
						mode = renderer.getSortMode();
						if (mode == SortMode.NONE)
							mode = SortMode.ASCENDING;
						else if (mode == SortMode.ASCENDING)
							mode = SortMode.DESCENDING;
						else if (mode == SortMode.DESCENDING)
							mode = SortMode.NONE;
						else
							mode = SortMode.ASCENDING;
					}
					else
						mode = SortMode.ASCENDING;
				}
				sorter.sortByColumn(modelcolumn, mode);

				if (renderer != null) {
					TableUtils.clearColumnHeaders(table);
					renderer.setSortMode(mode);
				}
			}
		}
	}
}
