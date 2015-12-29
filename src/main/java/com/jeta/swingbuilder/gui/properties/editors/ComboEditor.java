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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;

public class ComboEditor extends JETAPropertyEditor {
	private JPanel m_panel;
	private JComboBox m_cbox = new JComboBox();

	public ComboEditor() {
		m_panel = new JPanel(new BorderLayout());
		m_panel.add(m_cbox, BorderLayout.CENTER);
		m_cbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setValue(m_cbox.getSelectedItem());
			}
		});
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

	public Object getValue() {
		return m_cbox.getSelectedItem();
	}

	/**
	 * 
	 */
	public void setValue(Object value) {
		super.setValue(value);
		if (value instanceof TransformOptionsProperty) {
			TransformOptionsProperty p = (TransformOptionsProperty) value;
			m_cbox.removeAllItems();
			Collection c = p.getOptions();
			if (c != null) {
				Iterator iter = c.iterator();
				while (iter.hasNext()) {
					m_cbox.addItem(iter.next());
				}
			}
			m_cbox.setSelectedItem(p.getCurrentItem());
		}
		else {
			// assert( false );
		}
	}

}
