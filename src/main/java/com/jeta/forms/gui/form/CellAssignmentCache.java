/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.gui.form;

import java.util.Iterator;

import com.jeta.forms.gui.common.FormUtils;

/**
 * This class maintains an array of GridComponents for a given view. It is used
 * to quickly find a component at a given cell row/column position.
 * Additionally, it can find out any components that overlap a given row/column
 * position of those componenents have rowspan/columnspans greater than 1. We
 * need this class because the current FormLayout does not fully provide this
 * information, and the information it does provide is a copy. This makes it
 * terribly inefficient to use when repainting, so we need this cache mainly for
 * performance.
 * 
 * @author Jeff Tassin
 */
public class CellAssignmentCache {
	/**
	 * The view which we are caching cell assignments
	 */
	private GridView m_view;

	/**
	 * An array of row/col assignment objects.
	 */
	private CellAssignment[][] m_grid;

	/**
	 * Creates a <code>CellAssignmentCache</code> that is associated with the
	 * specified view.
	 * 
	 * @param view
	 *            the GridView that this cache maintains information for.
	 */
	public CellAssignmentCache(GridView view) {
		m_view = view;
	}

	/**
	 * Adds the specified component to this cache.
	 * 
	 * @param gc
	 *            the component to add to the cache.
	 */
	void addComponent(GridComponent gc) {
		checkGrid();

		if (gc == null) {
			FormUtils.safeAssert(false);
			return;
		}

		CellAssignment ca = m_grid[gc.getRow() - 1][gc.getColumn() - 1];
		if (ca == null) {
			ca = new CellAssignment();
			ca.m_comp = gc;
			m_grid[gc.getRow() - 1][gc.getColumn() - 1] = ca;
		}
		else {
			ca.m_comp = gc;
		}

		int rowstart = gc.getRow();
		int colstart = gc.getColumn();
		int rowspan = gc.getRowSpan();
		int colspan = gc.getColumnSpan();
		if (rowspan > 1 || colspan > 1) {
			for (int row = rowstart; row <= (rowstart + rowspan - 1); row++) {
				for (int col = colstart; col <= (colstart + colspan - 1); col++) {
					if (row != rowstart || col != colstart) {
						CellAssignment ova = m_grid[row - 1][col - 1];
						if (ova == null) {
							ova = new CellAssignment();
							m_grid[row - 1][col - 1] = ova;
						}
						ova.m_overlap = gc;
					}
				}
			}
		}
	}

	/**
	 * Checks that the grid size matches the view settings.
	 */
	private void checkGrid() {
		int columncount = m_view.getColumnCount();
		int rowcount = m_view.getRowCount();
		if (m_grid == null || m_grid[0].length != columncount || m_grid.length != rowcount) {
			resync();
		}
	}

	/**
	 * Returns the GridComponent object that is located at the give column and
	 * row.
	 * 
	 * @return the component that occupies the cell at the given row/column.
	 *         Null is returned if the specified cell does not have a
	 *         GridComponent.
	 */
	public GridComponent getGridComponent(int col, int row) {
		checkGrid();
		try {
			CellAssignment ca = m_grid[row - 1][col - 1];
			if (ca == null)
				return null;
			return ca.m_comp;
		} catch (Exception e) {
			resync();
			return null;
		}
	}

	/**
	 * Returns the component, if any, that overlaps the given cell.
	 * 
	 * @param col
	 *            the column for the specified cell
	 * @param row
	 *            the row for the specified cell.
	 * @return the component that overlaps the cell at the given column and row.
	 *         If no component overlaps, then null is returned.
	 */
	public GridComponent getOverlappingComponent(int col, int row) {
		checkGrid();

		CellAssignment ca = m_grid[row - 1][col - 1];
		FormUtils.safeAssert(ca != null);
		FormUtils.safeAssert(ca.m_comp.getRow() == row);
		FormUtils.safeAssert(ca.m_comp.getColumn() == col);
		if (ca != null) {
			return ca.m_overlap;
		}
		return null;
	}

	/**
	 * Marks the cache as invalid.
	 */
	void sync() {
		m_grid = null;
	}

	/**
	 * Updates the cell assignments array if anything on the view or the view
	 * itself has changed.
	 */
	private void resync() {
		int columncount = m_view.getColumnCount();
		int rowcount = m_view.getRowCount();
		if (m_grid == null || m_grid[0].length != columncount || m_grid.length != rowcount) {
			m_grid = new CellAssignment[rowcount][columncount];
		}
		else {
			for (int row = 0; row < rowcount; row++) {
				for (int col = 0; col < columncount; col++) {
					m_grid[row][col] = null;
				}
			}
		}

		Iterator iter = m_view.gridIterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (!(obj instanceof GridComponent)) {
				continue;
			}
			GridComponent gc = (GridComponent) obj;
			if (gc.getColumn() > columncount || gc.getRow() > rowcount) {
				FormUtils.safeAssert(false);
			}
			addComponent(gc);
		}
	}

	/**
	 * This class is used to hold a GridComponet and is used to associate a
	 * component that might overlap a component at a given cell.
	 */
	private static class CellAssignment {
		/**
		 * A grid component at a given column,row in the grid.
		 */
		GridComponent m_comp;

		/**
		 * A compnent that overlaps the above grid component ( m_comp ). This is
		 * null if m_comp does not have an overlapping component.
		 */
		GridComponent m_overlap;
	}
}
