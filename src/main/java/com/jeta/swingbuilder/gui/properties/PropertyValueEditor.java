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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.jeta.swingbuilder.gui.properties.editors.IconEditor;
import com.jeta.swingbuilder.gui.properties.editors.StringEditor;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;

/**
 * This class is the cell editor for the properties in the Property sheet for a
 * bean. It delegates the editing to the registered editor for the type of
 * property in the current row.
 * 
 * @author Jeff Tassin
 */
public class PropertyValueEditor extends AbstractCellEditor implements TableCellEditor, PropertyChangeListener {
	/**
	 * @directed
	 */
	private PropertyTableModel m_model;

	private PropertyValueRenderer m_renderer;
	private JButton m_editbtn;
	private JButton m_delete_btn;

	/**
	 * The bean we are editing
	 */
	private Object m_bean;

	/**
	 * A cache of editors for each row
	 */
	private PropertyEditorCache m_cache;

	private JPanel m_content = new JPanel(new BorderLayout());
	private PropertyEditor m_current_editor;
	private JTable m_table;

	public PropertyValueEditor(PropertyTableModel tmodel) {
		m_model = tmodel;
		m_renderer = new PropertyValueRenderer(tmodel);
		m_cache = new PropertyEditorCache(tmodel);
		m_content.setOpaque(false);

		Dimension d = new Dimension(32, 24);
		m_editbtn = new JButton(FormDesignerUtils.loadImage("common/ellipsis16.gif"));
		m_editbtn.setSize(d);
		m_editbtn.setMaximumSize(d);
		m_editbtn.setPreferredSize(d);
		m_editbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_current_editor instanceof JETAPropertyEditor) {
					JETAPropertyEditor jpe = (JETAPropertyEditor) m_current_editor;
					jpe.invokePropertyDialog(m_editbtn);
				}

			}
		});

		d = new Dimension(24, 24);
		m_delete_btn = new JButton(FormDesignerUtils.loadImage(Icons.DELETE2_16));
		m_delete_btn.setSize(d);
		m_delete_btn.setMaximumSize(d);
		m_delete_btn.setPreferredSize(d);
		m_delete_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_current_editor instanceof JETAPropertyEditor) {
					JETAPropertyEditor jpe = (JETAPropertyEditor) m_current_editor;
					jpe.setValue(null);
				}
			}
		});

	}

	/**
	 * Recursively disables all components in a given heirarchy.
	 */
	private void disableComponent(Component comp) {
		if (comp != null)
			comp = null;
		if (comp instanceof Container) {
			Container cc = (Container) comp;
			for (int index = 0; index < cc.getComponentCount(); index++) {
				disableComponent(cc.getComponent(index));
			}
		}
	}

	/**
	 * TableCellEditor implementation
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		m_content.removeAll();
		PropertyEditor editor = m_cache.getPropertyEditor(row);
		if (editor != null) {
			/**
			 * add this class to the editor as a property change listener. First
			 * remove it to prevent it being added multiple times. When the
			 * property value is changed by an editor, we will get the update
			 * and forward the property to the bean
			 */
			editor.removePropertyChangeListener(this);
			editor.addPropertyChangeListener(this);

			m_table = table;
			m_current_editor = editor;
			try {
				editor.setValue(value);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (editor instanceof JETAPropertyEditor) {
				JETAPropertyEditor jpe = (JETAPropertyEditor) editor;
				if (jpe.supportsInlineEditing()) {
					Component custom_editor = jpe.getCustomEditor();
					if (jpe instanceof StringEditor) {
						m_content.removeAll();
						JPanel btnpanel = new JPanel(new BorderLayout());
						btnpanel.add(m_editbtn, BorderLayout.WEST);
						m_content.add(btnpanel, BorderLayout.EAST);
						JPanel panel = new JPanel();
						panel.setOpaque(false);
						panel.setLayout(new BorderLayout());
						panel.add(custom_editor);
						m_content.add(panel, BorderLayout.CENTER);
						return m_content;
					}
					else {
						return custom_editor;
					}
				}
			}

			if (editor.isPaintable() || (editor instanceof JETAPropertyEditor)) {
				if (editor.supportsCustomEditor()) {
					m_content.removeAll();
					JPanel btnpanel = new JPanel(new BorderLayout());
					btnpanel.add(m_editbtn, BorderLayout.WEST);
					m_content.add(btnpanel, BorderLayout.EAST);
					if (editor instanceof IconEditor) {
						btnpanel.add(m_delete_btn, BorderLayout.EAST);
					}
					JPanel panel = new JPanel();
					panel.setOpaque(false);
					panel.setLayout(new BorderLayout());
					panel.add(m_renderer.getTableCellRendererComponent(table, value, isSelected, false, row, column));
					m_content.add(panel, BorderLayout.CENTER);
					return m_content;
				}
				else {
					assert (false);
				}
			}

			if (editor.supportsCustomEditor()) {
				return editor.getCustomEditor();
			}
		}
		return m_content;
	}

	/**
	 * Get cellEditorValue for current editor
	 */
	public Object getCellEditorValue() {
		if (m_current_editor != null) {
			return m_current_editor.getValue();
		}
		else
			return null;
	}

	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	/**
	 * @return true if the cell editor has a value different than is in the
	 *         current table cell.
	 */
	private boolean isNewValue() {
		int row = m_table.getEditingRow();
		int col = m_table.getEditingColumn();
		if (row >= 0 && col >= 0) {
			Object new_val = getCellEditorValue();
			Object old_val = m_table.getValueAt(row, col);
			if (old_val != null && new_val != null) {
				if (old_val == new_val || old_val.equals(new_val)) {
					// System.out.println( "PropertyValueEditor.object not
					// changed." );
					return false;
				}
			}
			else if (old_val == null && new_val == null) {
				// System.out.println( "PropertyValueEditor.object not changed."
				// );
				return false;
			}
		}
		else {
			return false;
		}
		return true;
	}

	/**
	 * PropertyChangeListener implementation. We get property events from each
	 * editor as the user makes changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			if (m_current_editor != null)
				m_current_editor.removePropertyChangeListener(this);

			if (isNewValue()) {
				assert (m_table != null);
				m_table.setValueAt(getCellEditorValue(), m_table.getEditingRow(), m_table.getEditingColumn());
				m_renderer.getTableCellRendererComponent(m_table, getCellEditorValue(), true, true, m_table.getEditingRow(), m_table.getEditingColumn());

				/**
				 * Set the value in the current editor to the table value
				 * because the bean may have made a copy of the property.
				 */
				m_current_editor.setValue(m_table.getValueAt(m_table.getEditingRow(), m_table.getEditingColumn()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (m_current_editor != null)
				m_current_editor.addPropertyChangeListener(this);
		}
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	public boolean stopCellEditing() {
		if (m_table != null) {
			try {
				if (isNewValue()) {
					m_table.setValueAt(getCellEditorValue(), m_table.getEditingRow(), m_table.getEditingColumn());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**
			 * we need to do this because for some reason the JTable does not
			 * properly update the cell when finished editing.
			 */
			boolean result = super.stopCellEditing();
			m_table.removeEditor();
			return result;
		}
		else {
			return true;
		}
	}

	public void updateUI() {
		if (m_renderer != null)
			m_renderer.updateUI();

		if (m_editbtn != null)
			m_editbtn.updateUI();

		if (m_cache != null)
			m_cache.updateUI();

		if (m_delete_btn != null)
			m_delete_btn.updateUI();
	}
}
