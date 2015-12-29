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

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import com.jeta.open.support.EmptyCollection;

/**
 * This class is the table model for the common tables.
 * 
 * @author Jeff Tassin
 */
public class JETATableModel extends AbstractTableModel {
	/** an array of Trigger object */
	private ArrayList m_data;

	/** an array of column names for the table */
	private String[] m_colnames;

	/** an array of column types for the table */
	private Class[] m_coltypes;

	/**
	 * ctor.
	 * 
	 * @param connection
	 *            the underlying database connection
	 * @param tableId
	 *            the id of the table we are displaying the indices for
	 */
	public JETATableModel() {

	}

	/**
	 * Adds the given trigger object to the table
	 */
	public void addRow(Object obj) {
		if (m_data == null)
			m_data = new ArrayList();

		m_data.add(obj);
		fireTableRowsInserted(m_data.size() - 1, m_data.size() - 1);
	}

	/**
	 * @return true if the model contains the given object
	 */
	public boolean contains(Object obj) {
		if (m_data == null)
			return false;
		else
			return m_data.contains(obj);
	}

	/**
	 * @return the number of columns in this model
	 */
	public int getColumnCount() {
		return m_colnames.length;
	}

	/**
	 * @return the collection of objects in this model
	 */
	public Collection getData() {
		if (m_data == null)
			return EmptyCollection.getInstance();
		else
			return m_data;
	}

	/**
	 * @return the number of rows objects in this model
	 */
	public int getRowCount() {
		if (m_data == null)
			return 0;
		else
			return m_data.size();
	}

	/**
	 * @return the name of a column at a given index
	 */
	public String getColumnName(int column) {
		return m_colnames[column];
	}

	/**
	 * @return the type of column at a given index
	 */
	public Class getColumnClass(int column) {
		return m_coltypes[column];
	}

	/**
	 * @return the object at the given row in the model
	 */
	public Object getRow(int row) {
		if (m_data == null)
			return null;
		else {
			if (row >= 0 && row < m_data.size())
				return m_data.get(row);
			else
				return null;
		}
	}

	/**
	 * @return the Object at the given row column. Note: you generally override
	 *         this method
	 */
	public Object getValueAt(int row, int col) {
		Object obj = getRow(row);
		if (obj instanceof Object[]) {
			Object[] array = (Object[]) obj;
			return array[col];
		}
		else {
			return obj;
		}
	}

	/**
	 * @return the index of the given object if it is contained in this model.
	 *         -1 is returned if this model does not contain the object.
	 */
	public int indexOf(Object obj) {
		return m_data.indexOf(obj);
	}

	/**
	 * Inserts the object at the given row in the model.
	 */
	public void insertRow(Object obj, int row) {
		if (row < 0)
			row = 0;

		if (row >= m_data.size())
			m_data.add(obj);
		else
			m_data.add(row, obj);
	}

	private static int gcd(int i, int j) {
		return (j == 0) ? i : gcd(j, i % j);
	}

	public void moveRow(int start, int end, int to) throws IndexOutOfBoundsException {
		int shift = to - start;
		int first, last;
		if (shift < 0) {
			first = to;
			last = end;
		}
		else {
			first = start;
			last = to + end - start;
		}

		int a = first;
		int b = last + 1;

		int size = b - a;
		int r = size - shift;
		int g = gcd(size, r);
		for (int i = 0; i < g; i++) {
			to = i;
			Object tmp = m_data.get(a + to);
			for (int from = (to + r) % size; from != i; from = (to + r) % size) {
				m_data.set(a + to, m_data.get(a + from));
				to = from;
			}
			m_data.set(a + to, tmp);
		}
		fireTableRowsUpdated(first, last);
	}

	/**
	 * Remove all data items from this model
	 */
	public void removeAll() {
		if (m_data != null) {
			m_data.clear();
			fireTableDataChanged();
		}
	}

	/**
	 * Removes the object at the given row
	 */
	public Object removeRow(int row) {
		Object obj = getRow(row);
		m_data.remove(obj);
		return obj;
	}

	/**
	 * Removes the object at the given row
	 */
	public boolean remove(Object obj) {
		int index = m_data.indexOf(obj);
		if (index >= 0) {
			m_data.remove(index);
			fireTableRowsDeleted(index, index);
			return true;
		}
		else {
			return false;
		}
	}

	public Object set(int index, Object element) throws IndexOutOfBoundsException {
		Object result = m_data.set(index, element);
		fireTableRowsUpdated(index, index);
		return result;
	}

	/**
	 * Sets the names for the columns in this model
	 */
	public void setColumnNames(String[] names) {
		m_colnames = names;
	}

	/**
	 * Sets the types for the columns in this model
	 */
	public void setColumnTypes(Class[] types) {
		m_coltypes = types;
	}

}
