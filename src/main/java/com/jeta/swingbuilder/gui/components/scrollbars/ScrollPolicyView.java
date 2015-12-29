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

package com.jeta.swingbuilder.gui.components.scrollbars;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.ScrollBarsProperty;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.utils.IntegerComboMap;

/**
 * Displays the scroll policy for a given component.
 * 
 * @author Jeff Tassin
 */
public class ScrollPolicyView extends JETAPanel {
	/**
	 * The projectSettings.jfrm form
	 */
	private FormPanel m_view;

	private ScrollBarsProperty m_prop = new ScrollBarsProperty();

	private static IntegerComboMap m_vmap = new IntegerComboMap();
	private static IntegerComboMap m_hmap = new IntegerComboMap();

	static {
		m_vmap.map(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, "AS_NEEDED");
		m_vmap.map(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, "ALWAYS");
		m_vmap.map(JScrollPane.VERTICAL_SCROLLBAR_NEVER, "NEVER");

		m_hmap.map(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED, "AS_NEEDED");
		m_hmap.map(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS, "ALWAYS");
		m_hmap.map(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, "NEVER");
	}

	/**
	 * ctor
	 */
	public ScrollPolicyView(ScrollBarsProperty prop) {
		m_prop.setValue(prop);
		initialize(prop);
		setController(new ScrollPolicyController(this));
	}

	/**
	 * @return the scroll property defined by the current view
	 */
	public ScrollBarsProperty getScrollBarsProperty() {
		m_prop.setHorizontalScrollBarPolicy(m_hmap.getSelectedValue(getComboBox(ScrollPolicyNames.ID_HORIZONTAL_POLICY)));
		m_prop.setVerticalScrollBarPolicy(m_vmap.getSelectedValue(getComboBox(ScrollPolicyNames.ID_VERTICAL_POLICY)));
		m_prop.setScrollName(m_view.getText(ScrollPolicyNames.ID_NAME_FIELD));
		return m_prop;
	}

	/**
	 * Initializes the view
	 */
	public void initialize(ScrollBarsProperty prop) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/components/scrollbars/scrollBars.jfrm");
		add(m_view, BorderLayout.CENTER);

		m_view.setText(ScrollPolicyNames.ID_NAME_FIELD, prop.getScrollName());
		m_hmap.setSelectedItem(getComboBox(ScrollPolicyNames.ID_HORIZONTAL_POLICY), prop.getHorizontalScrollBarPolicy());
		m_vmap.setSelectedItem(getComboBox(ScrollPolicyNames.ID_VERTICAL_POLICY), prop.getVerticalScrollBarPolicy());

		String border_txt = "DEFAULT";

		CompoundBorderProperty border = prop.getBorderProperty();
		if (border != null)
			border_txt = border.toString();
		m_view.setText(ScrollPolicyNames.ID_BORDER_LABEL, border_txt);
	}
}
