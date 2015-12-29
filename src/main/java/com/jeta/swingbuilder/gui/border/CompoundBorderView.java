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

package com.jeta.swingbuilder.gui.border;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.border.Border;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.open.gui.framework.JETAPanel;

/**
 * View that is used to create and edit borders
 * 
 * @author Jeff Tassin
 */
public class CompoundBorderView extends JETAPanel {

	/**
	 * The actual view
	 */
	private FormPanel m_view;

	/**
	 * The list model that handles the borders
	 */
	private DefaultListModel m_borders_model;

	/**
	 * ctor
	 */
	public CompoundBorderView(CompoundBorderProperty border) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/border/compoundBorder.frm");
		add(m_view, BorderLayout.CENTER);

		setController(new CompoundBorderController(this));

		m_borders_model = new DefaultListModel();
		m_view.getList(CompoundBorderNames.ID_BORDER_LIST).setModel(m_borders_model);

		Iterator iter = border.iterator();
		while (iter.hasNext()) {
			BorderProperty bp = (BorderProperty) iter.next();
			m_borders_model.addElement(bp);
		}
		updateBorderView();
	}

	/**
	 * Adds a border to the list
	 */
	public void addBorder(BorderProperty bp) {
		m_borders_model.add(0, bp);
		updateBorderView();
	}

	/**
	 * Creates a Swing border
	 */
	private Border createBorder() {
		return createBorderProperty().createBorder(null);
	}

	/**
	 * Creates a BorderProperty based on this view
	 */
	public BorderProperty createBorderProperty() {
		CompoundBorderProperty prop = new CompoundBorderProperty();
		for (int index = 0; index < m_borders_model.size(); index++) {
			BorderProperty bp = (BorderProperty) m_borders_model.elementAt(index);
			prop.addBorder(bp);
		}
		return prop;
	}

	/**
	 * Ensure the selected border is visible in the list.
	 */
	void ensureIndexIsVisible() {
		JList list = m_view.getList(CompoundBorderNames.ID_BORDER_LIST);
		int index = list.getSelectedIndex();
		if (index >= 0)
			list.ensureIndexIsVisible(index);
	}

	/**
	 * @return the selected border in the border list.
	 */
	public BorderProperty getSelectedBorder() {
		return (BorderProperty) m_view.getList(CompoundBorderNames.ID_BORDER_LIST).getSelectedValue();
	}

	/**
	 * Modifies a given border and sets it to a new border
	 */
	public void setBorder(BorderProperty new_border, BorderProperty old_border) {
		int index = m_borders_model.indexOf(old_border);
		if (index >= 0) {
			m_borders_model.set(index, new_border);
		}
		updateBorderView();
	}

	/**
	 * Creates a swing border based on the properties in this view and displays
	 * it.
	 */
	public void updateBorderView() {
		Border b = createBorder();
		m_view.getLabel(CompoundBorderNames.ID_BORDER_PREVIEW).setBorder(b);
	}
}
