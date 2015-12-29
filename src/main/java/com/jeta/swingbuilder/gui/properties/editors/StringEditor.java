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
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.text.TextPropertyView;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;

public class StringEditor extends JETAPropertyEditor {
	private JPanel m_panel;
	private EditorTextArea m_field = new EditorTextArea();
	private boolean m_field_is_null = true;

	public StringEditor() {
		m_panel = new JPanel();
		m_panel.setLayout(new BorderLayout());

		m_field.setRows(1);
		m_field.setLineWrap(false);
		m_field.setWrapStyleWord(false);
		m_field.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 0));

		m_panel.add(m_field, BorderLayout.CENTER);

		m_panel.setBackground(UIManager.getColor("Table.background"));

		/**
		 * We need to trap document changes so that we can differentiate between
		 * a NULL string and a zero length string.
		 */
		m_field.setDocument(new PlainDocument() {
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				super.insertString(offs, str, a);
				if (str != null && m_field_is_null) {
					m_field_is_null = false;
				}
			}
		});
	}

	public Component getCustomEditor() {
		return m_panel;
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog(Component comp) {
		TextPropertyView view = new TextPropertyView((String) getValue());
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, comp, true);
		dlg.setPrimaryPanel(view);
		dlg.setTitle(I18N.getLocalizedMessage("Set Text Property"));
		dlg.setInitialFocusComponent((javax.swing.JComponent) view.getComponentByName(TextPropertyView.ID_TEXT_AREA));
		dlg.setSize(dlg.getPreferredSize());
		dlg.showCenter();
		if (dlg.isOk()) {
			m_field.setText(view.getText());
			setValue(view.getText());
		}
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return true;
	}

	/**
	 * 
	 */
	public boolean supportsCustomEditor() {
		return true;
	}

	public void setValue(Object value) {
		super.setValue(value);
		if (value != null) {
			m_field.setText(value.toString());
		}
	}

	public Object getValue() {
		Object value = super.getValue();
		/**
		 * Need to differentiate between NULL strings and zero length strings
		 */
		if (value == null && m_field.getText().length() == 0 && m_field_is_null) {
			return null;
		}
		return m_field.getText();
	}

	/**
	 * Specialization of JTextArea that we need for this component
	 * 
	 * @author Jeff Tassin
	 */
	public class EditorTextArea extends JTextArea {
		/**
		 * Intercept commands to set field to null
		 */
		public void processKeyEvent(KeyEvent evt) {
			if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
				setValue(getText());
			}
			else
				super.processKeyEvent(evt);
		}
	}

}
