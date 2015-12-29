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

import java.awt.Component;
import java.beans.PropertyEditor;

import com.jeta.forms.gui.common.FormUtils;

public class PropertyEditorCache {
	/**
	 * @directed
	 */
	private PropertyTableModel m_model;

	/**
	 * The bean we are currently rendering properties for
	 */
	private Object m_bean;

	/**
	 * The property editors for each row in the table
	 */
	private PropertyEditor[] m_editors;

	/**
	 * ctor
	 */
	public PropertyEditorCache(PropertyTableModel model) {
		m_model = model;
	}

	public PropertyEditor getPropertyEditor(int row) {
		if (m_bean != m_model.getBean() || m_editors == null || m_editors.length != m_model.getRowCount()) {
			m_bean = m_model.getBean();
			m_editors = new PropertyEditor[m_model.getRowCount()];
		}

		PropertyEditor editor = m_editors[row];
		if (editor == null) {
			editor = m_model.getPropertyEditor(row);
			if (editor != null) {
				try {
					m_editors[row] = (PropertyEditor) editor.getClass().newInstance(); // we
																						// need
																						// to
																						// create
																						// a
																						// copy
																						// here
					editor = m_editors[row];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return editor;
	}

	public void updateUI() {
		if (m_editors != null) {
			for (int index = 0; index < m_editors.length; index++) {
				PropertyEditor editor = m_editors[index];
				if (editor != null) {
					Component comp = editor.getCustomEditor();
					FormUtils.updateLookAndFeel(comp);
				}
			}
		}

	}
}
