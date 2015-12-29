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

package com.jeta.swingbuilder.gui.formmgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.open.i18n.I18N;
import com.jeta.open.registry.JETARegistry;

/**
 * This class is the table model for the FormManagerView. It gets the list of
 * forms from the FormManager and stores them in this model. This is mainly used
 * to help debugging.
 * 
 * @author Jeff Tassin
 */
public class FormsModel extends AbstractTableModel {
	/** an array of Trigger object */
	private ArrayList m_data;

	/** an array of column names for the table */
	private String[] m_colnames;

	/** an array of column types for the table */
	private Class[] m_coltypes;

	/** column definitions */
	static final int EMBEDDED_COLUMN = 0;
	static final int ID_COLUMN = 1;
	static final int NAME_COLUMN = 2;
	static final int PARENT_COLUMN = 3;

	/**
	 * ctor.
	 */
	public FormsModel() {
		super();

		m_data = new ArrayList();

		String[] values = { I18N.getLocalizedMessage("Embedded"), I18N.getLocalizedMessage("Form Id"), I18N.getLocalizedMessage("Name"),
				I18N.getLocalizedMessage("Parent Form Id") };

		m_colnames = values;

		Class[] types = { Boolean.class, String.class, String.class, String.class };
		m_coltypes = types;
		reload();
	}

	/**
	 * Adds the given trigger object to the table
	 */
	public void addRow(FormComponent fc) {
		if (m_data == null)
			m_data = new ArrayList();

		m_data.add(fc);
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
	public FormComponent getRow(int row) {
		if (row >= 0 && row < m_data.size())
			return (FormComponent) m_data.get(row);
		else
			return null;
	}

	/**
	 * @return the column value at the given row
	 */
	public Object getValueAt(int row, int column) {
		/** "Embedded", "Form Id", "Name", "Parent Id" */
		FormComponent fc = getRow(row);
		if (column == EMBEDDED_COLUMN) {
			return Boolean.valueOf(fc.isEmbedded());
		}
		else if (column == ID_COLUMN) {
			return fc.getId();
		}
		else if (column == NAME_COLUMN) {
			return fc.getChildView().getName();
		}
		else if (column == PARENT_COLUMN) {
			FormComponent parent = fc.getParentForm();
			if (parent == null)
				return "null";
			else
				return parent.getId();
		}
		else
			return "";
	}

	/**
	 * Reload the model
	 */
	public void reload() {
		removeAll();
		FormManager fm = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		Collection form_ids = fm.getForms();
		Iterator iter = form_ids.iterator();
		while (iter.hasNext()) {
			String form_id = (String) iter.next();
			addRow(fm.getForm(form_id));
		}
	}

	/**
	 * Remove all data items from this model
	 */
	public void removeAll() {
		m_data.clear();
	}

}
