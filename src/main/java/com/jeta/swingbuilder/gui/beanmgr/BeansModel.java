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

package com.jeta.swingbuilder.gui.beanmgr;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.store.ImportedBeanInfo;

/**
 * This class is the table model for the registered Java beans in the builder.
 * 
 * @author Jeff Tassin
 */
public class BeansModel extends AbstractTableModel {
	/** an array of registered beans */
	private ArrayList m_data;

	/** an array of column names for the table */
	private String[] m_colnames;

	/** an array of column types for the table */
	private Class[] m_coltypes;

	/** column definitions */
	public static final int ICON_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int SCROLLABLE_COLUMN = 2;

	/**
	 * ctor.
	 */
	public BeansModel() {
		super();

		m_data = new ArrayList();

		String[] values = { I18N.getLocalizedMessage("Icon"), I18N.getLocalizedMessage("Class Name"), I18N.getLocalizedMessage("Scrollable") };

		m_colnames = values;

		Class[] types = { Icon.class, String.class, Boolean.class };
		m_coltypes = types;
		reload();
	}

	/**
	 * Adds the given trigger object to the table
	 */
	public void addRow(ImportedBeanInfo cbi) {
		if (m_data == null)
			m_data = new ArrayList();

		m_data.add(cbi);
		fireTableRowsInserted(m_data.size() - 1, m_data.size() - 1);
	}

	/**
	 * @return the number of columns in this model
	 */
	public int getColumnCount() {
		return m_colnames.length;
	}

	/**
	 * @return the number of rows objects in this model
	 */
	public int getRowCount() {
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
	public ImportedBeanInfo getRow(int row) {
		if (row >= 0 && row < m_data.size())
			return (ImportedBeanInfo) m_data.get(row);
		else
			return null;
	}

	/**
	 * @return the column value at the given row
	 */
	public Object getValueAt(int row, int column) {
		/** "Icon", "ClassName" */
		ImportedBeanInfo cbi = getRow(row);
		if (column == ICON_COLUMN) {
			return cbi.getIcon();
		}
		else if (column == NAME_COLUMN) {
			return cbi.getBeanName();
		}
		else if (column == SCROLLABLE_COLUMN) {
			return Boolean.valueOf(cbi.isScrollable());
		}
		else
			return "";
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex == SCROLLABLE_COLUMN);
	}

	/**
	 * Reload the model
	 */
	public void reload() {
		removeAll();
	}

	/**
	 * Removes a beaninfor object from the table
	 */
	public void removeRow(int row) {
		if (m_data != null && row >= 0 && row < m_data.size()) {
			m_data.remove(row);
			fireTableDataChanged();
		}
	}

	/**
	 * Remove all data items from this model
	 */
	public void removeAll() {
		m_data.clear();
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == SCROLLABLE_COLUMN) {
			if (aValue instanceof Boolean) {
				Boolean enabled = (Boolean) aValue;
				ImportedBeanInfo binfo = getRow(rowIndex);
				if (binfo != null) {
					binfo.setScrollable(enabled.booleanValue());
					fireTableDataChanged();
				}
			}
		}
	}
}
