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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;

public class BooleanEditor extends JETAPropertyEditor {
	private JPanel m_panel;
	private JCheckBox m_cbox = new JCheckBox();

	public BooleanEditor() {
		m_panel = new JPanel() {
			public void updateUI() {
				super.updateUI();
				if (m_panel != null) {
					m_panel.setBackground(javax.swing.UIManager.getColor("TextField.background"));
					m_cbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 2));
					m_cbox.setOpaque(false);
				}
			}
		};
		m_panel.setLayout(new BoxLayout(m_panel, BoxLayout.Y_AXIS));
		m_panel.add(m_cbox);
		m_panel.setOpaque(true);
		m_panel.setBackground(javax.swing.UIManager.getColor("TextField.background"));
		m_cbox.setOpaque(false);
		m_cbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 4, 2));

		m_cbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					setValue(Boolean.TRUE);
				}
				else {
					setValue(Boolean.FALSE);

				}
			}
		});

	}

	public Component getCustomEditor() {
		return m_panel;
	}

	/**
	 * 
	 */
	public boolean isPaintable() {
		return false;
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return true;
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	public void setValue(Object value) {
		super.setValue(value);
		if (value != null) {
			try {
				m_cbox.setText(value.toString());
				if (m_cbox.isSelected() != ((Boolean) value).booleanValue()) {
					// Don't call setSelected unless the state actually changes
					// to avoid a loop.
					m_cbox.setSelected(((Boolean) value).booleanValue());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public Object getValue() {
		return Boolean.valueOf(m_cbox.isSelected());
	}

}
