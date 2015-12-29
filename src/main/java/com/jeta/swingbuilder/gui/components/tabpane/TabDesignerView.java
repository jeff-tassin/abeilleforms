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

package com.jeta.swingbuilder.gui.components.tabpane;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.TabProperty;
import com.jeta.forms.store.properties.TabbedPaneProperties;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * View for editing tab properties.
 * 
 * @author Jeff Tassin
 */
public class TabDesignerView extends JETAPanel {
	private FormPanel m_view;

	private TabsModel m_tabs_model;

	public TabDesignerView(TabbedPaneProperties props) {
		m_view = new FormPanel("com/jeta/swingbuilder/gui/components/tabpane/tabDesigner.jfrm");
		setLayout(new BorderLayout());
		add(m_view, BorderLayout.CENTER);
		setController(new TabDesignerController(this));

		m_tabs_model = new TabsModel(props);
		JTable table = m_view.getTable(TabDesignerNames.ID_TABS_TABLE);
		table.setModel(m_tabs_model);

		int col_width = 60;
		TableColumnModel cmodel = table.getColumnModel();
		cmodel.getColumn(TabsModel.ICON_COLUMN).setPreferredWidth(col_width);
		cmodel.getColumn(TabsModel.TITLE_COLUMN).setPreferredWidth(col_width * 5);
	}

	/**
	 * Adds a tab to the model.
	 */
	public void addTabProperty(TabProperty tp) {
		m_tabs_model.addRow(tp);
	}

	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 250, 150);
	}

	/**
	 * @return the currently selected tab property. Null is returned if no item
	 *         is selected.
	 */
	public TabProperty getSelectedTabProperty() {
		int row = m_view.getTable(TabDesignerNames.ID_TABS_TABLE).getSelectedRow();
		if (row >= 0) {
			return (TabProperty) m_tabs_model.getRow(row);
		}
		else {
			return null;
		}
	}

	public TabbedPaneProperties getTabbedPaneProperties() {
		TabbedPaneProperties tprop = new TabbedPaneProperties();
		for (int row = 0; row < m_tabs_model.getRowCount(); row++) {
			tprop.addTab((TabProperty) m_tabs_model.getRow(row));
		}
		return tprop;
	}

	/**
	 * Modifies an existing tab property with a new property
	 */
	public void setTabProperty(TabProperty newProp, TabProperty oldProp) {
		m_tabs_model.setTabProperty(newProp, oldProp);
	}

}
