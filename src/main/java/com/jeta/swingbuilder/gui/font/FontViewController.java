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

package com.jeta.swingbuilder.gui.font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jeta.open.gui.framework.JETAController;

/**
 * Event handler for the FontView
 * 
 * @author Jeff Tassin
 */
public class FontViewController extends JETAController {
	private FontView m_view;

	/**
	 * ctor
	 */
	public FontViewController(FontView view) {
		super(view);
		m_view = view;
		assignListener(FontViewNames.ID_FAMILY_LIST, new FontListListener(FontViewNames.ID_FAMILY_LIST, FontViewNames.ID_FAMILY_FIELD));
		assignListener(FontViewNames.ID_STYLE_LIST, new FontListListener(FontViewNames.ID_STYLE_LIST, FontViewNames.ID_STYLE_FIELD));
		assignListener(FontViewNames.ID_SIZE_LIST, new FontListListener(FontViewNames.ID_SIZE_LIST, FontViewNames.ID_SIZE_FIELD));
		assignAction(FontViewNames.ID_SIZE_FIELD, new FontSizeListener());
	}

	public class FontSizeListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.setPointSize(m_view.getPointSize());
			m_view.setFontValue(m_view.createFont());
		}
	}

	/**
	 * A list selection listener for one of the JList components on the
	 * FontView. The caller passes in the name for the list we wish to handle
	 * events for.
	 */
	public class FontListListener implements ListSelectionListener {
		/**
		 * The name of the list
		 */
		private String m_list_name;

		/**
		 * The corresponding text field
		 */
		private String m_txt_field_name;

		/**
		 * ctor
		 */
		public FontListListener(String listName, String fieldName) {
			m_list_name = listName;
			m_txt_field_name = fieldName;
		}

		/**
		 * ListSelectionListener implementation
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				m_view.setText(m_txt_field_name, (String) m_view.getSelectedItem(m_list_name));
				m_view.setFontValue(m_view.createFont());
			}
		}
	}
}
