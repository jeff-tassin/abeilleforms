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

package com.jeta.swingbuilder.gui.properties.editors;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;

public class UnknownEditor extends JETAPropertyEditor {
	private JPanel m_panel;
	private JTextField m_field = new JTextField();

	public UnknownEditor() {
		m_panel = new JPanel();
		m_panel.setLayout(new BorderLayout());
		m_field.setEnabled(false);
		m_panel.add(m_field, BorderLayout.CENTER);
		m_panel.setBackground(javax.swing.UIManager.getColor("Table.background"));
		m_field.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 3, 2));
	}

	public Component getCustomEditor() {
		return m_panel;
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return true;
	}

	public void setValue(Object value) {
		super.setValue(value);
		if (value != null) {
			m_field.setText(value.toString());
		}
		else {
			m_field.setText("");
		}
	}

}
