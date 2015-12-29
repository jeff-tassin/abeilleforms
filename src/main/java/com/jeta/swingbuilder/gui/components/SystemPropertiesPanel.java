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

package com.jeta.swingbuilder.gui.components;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JComponent;

import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;

/**
 * This class displays Java System properties in a table.
 * 
 * @author Jeff Tassin
 */
public class SystemPropertiesPanel extends JETAPanel {
	/** the model for holding the Java system properties */
	private JETATableModel m_model;

	/**
	 * ctor
	 */
	public SystemPropertiesPanel() {
		initialize();
	}

	/**
	 * Creates the JTable that displays the JDBC driver properties
	 * 
	 * @returns the table component
	 */
	private JComponent createInfoTable() {
		m_model = new JETATableModel();

		String[] names = new String[2];
		names[0] = I18N.getLocalizedMessage("Property");
		names[1] = I18N.getLocalizedMessage("Value");
		m_model.setColumnNames(names);

		Class[] coltypes = new Class[2];
		coltypes[0] = String.class;
		coltypes[1] = String.class;
		m_model.setColumnTypes(coltypes);

		return new javax.swing.JScrollPane(TableUtils.createBasicTablePanel(m_model, true));
	}

	/**
	 * Creates the components on this panel
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		add(createInfoTable(), BorderLayout.CENTER);

		// load the data
		try {
			Properties props = System.getProperties();
			Enumeration names = props.propertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				String value = props.getProperty(name);
				Object[] row = new Object[2];
				row[0] = name;
				row[1] = value;
				m_model.addRow(row);
			}
		} catch (Exception e) {

		}
	}

}
