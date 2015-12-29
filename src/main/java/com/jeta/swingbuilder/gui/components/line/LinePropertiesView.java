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

package com.jeta.swingbuilder.gui.components.line;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jeta.forms.store.properties.LineProperty;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.panel.SwingBuilderPanel;

/**
 * View that is used to create and edit a single line
 * 
 * @author Jeff Tassin
 */
public class LinePropertiesView extends JETAPanel {
	/**
	 * The actual view
	 */
	private SwingBuilderPanel m_view;

	/**
	 * The current line property
	 */
	private LineProperty m_prop = new LineProperty();

	/**
	 * ctor
	 */
	public LinePropertiesView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public LinePropertiesView(LineProperty lp) {
		if (lp != null)
			m_prop.setValue(lp);

		Color new_color = m_prop.getColor();

		setLayout(new BorderLayout());
		m_view = new SwingBuilderPanel("com/jeta/swingbuilder/gui/components/line/lineProperties.frm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JSpinner sp = m_view.getSpinner(LinePropertiesNames.ID_LINE_THICKNESS_FIELD);
		sp.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		sp.setValue(new Integer(m_prop.getThickness()));

		m_view.setColorProperty(LinePropertiesNames.ID_COLOR_SELECTOR, m_prop.getColorProperty());
		setController(new LinePropertiesController(this));
	}

	public int getThickness() {
		JSpinner sp = m_view.getSpinner(LinePropertiesNames.ID_LINE_THICKNESS_FIELD);
		Integer ival = (Integer) sp.getValue();
		return ival.intValue();
	}

	/**
	 * Sets the current line thickness
	 */
	public void setThickness(int thk) {
		m_prop.setThickness(thk);
	}

	public LineProperty getLineProperty() {
		m_prop.setColorProperty(m_view.getColorProperty(LinePropertiesNames.ID_COLOR_SELECTOR));
		m_prop.setThickness(getThickness());
		return m_prop;
	}

	/**
	 * Updates the line component in the preview pane with the latest properties
	 */
	public void updatePreview() {

	}

}
