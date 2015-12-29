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

package com.jeta.swingbuilder.gui.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyEditor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.jeta.forms.gui.beans.JETABean;

/**
 * This pane displays the properties for a Java Bean
 * 
 * @author Jeff Tassin
 */
public class PropertyPane extends JPanel implements TableModelListener {
	private JETABean m_bean; // Current Bean.

	private JTable m_table;
	private PropertyColumnModel m_columnModel;
	private PropertyTableModel m_tablemodel;
	private PropertyValueEditor m_editor;
	private PropertyValueRenderer m_renderer;

	private static final int ROW_HEIGHT = 20;

	/**
	 * Constructor
	 * 
	 * @param basic
	 *            set to true if you only want to show preferred properties
	 */
	public PropertyPane(boolean basic) {
		super(new BorderLayout());

		m_tablemodel = new PropertyTableModel();
		if (basic)
			m_tablemodel.setFilter(PropertyTableModel.VIEW_PREFERRED);

		m_tablemodel.addTableModelListener(this);

		m_columnModel = new PropertyColumnModel();
		m_table = new JTable(m_tablemodel, m_columnModel);
		m_table.setShowGrid(true);
		m_table.setRowHeight(ROW_HEIGHT);
		m_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		m_renderer = new PropertyValueRenderer(m_tablemodel);
		m_editor = new PropertyValueEditor(m_tablemodel);
		m_columnModel.getColumn(PropertyTableModel.COL_VALUE).setCellRenderer(m_renderer);
		m_columnModel.getColumn(PropertyTableModel.COL_VALUE).setCellEditor(m_editor);

		add(new JScrollPane(m_table), BorderLayout.CENTER);
	}

	public JETABean getBean() {
		return m_bean;
	}

	PropertyTableModel getTableModel() {
		return m_tablemodel;
	}

	/**
	 * Sets the state of the up and down buttons based on the contents of the
	 * stack.
	 */
	private void setButtonState() {
	}

	/**
	 * Sets the PropertyPane to show the properties of the named bean.
	 */
	protected void setBean(JETABean bean) {
		if (m_table.isEditing()) {
			m_editor.stopCellEditing();
		}
		m_bean = bean;
		m_tablemodel.setBean(bean);
	}

	/**
	 * Cancels any editing in the property table
	 */
	public void cancelEditing() {
		try {
			if (m_table.isEditing()) {
				m_editor.cancelCellEditing();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops any editing in the property table
	 */
	public void stopEditing() {
		try {
			if (m_table.isEditing()) {
				m_editor.stopCellEditing();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TableModelListener Implementation
	 */
	public void tableChanged(TableModelEvent evt) {
		m_table.setRowHeight(ROW_HEIGHT);

		for (int i = 0; i < m_table.getRowCount(); i++) {
			PropertyEditor editor = m_tablemodel.getPropertyEditor(i);
			if (editor != null) {
				Component comp = editor.getCustomEditor();
				if (comp != null) {
					Dimension prefsize = comp.getPreferredSize();
					if (prefsize.height != m_table.getRowHeight(i)) {
						m_table.setRowHeight(i, prefsize.height);
					}
				}
			}
		}
	}

	public void updateUI() {
		super.updateUI();

		if (m_table != null) {
			m_table.updateUI();
			m_table.getTableHeader().updateUI();
		}

		if (m_renderer != null)
			m_renderer.updateUI();

		if (m_editor != null)
			m_editor.updateUI();
	}

	public class DeletePropertyAction extends javax.swing.AbstractAction {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
			System.out.println("PropertyPane.delete key hit on table.. ");
		}
	}
}
