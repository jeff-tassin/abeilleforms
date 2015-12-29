/* 
 * Copyright 2004 JETA Software, Inc. All rights reserved.
 * JETA SOFTWARE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jeta.swingbuilder.gui.components.list;

import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.ListItemProperty;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

public class ItemsView extends FormPanel {
	/**
	 * The underlying table model for the items.
	 */
	private ItemsModel m_model;

	public ItemsView(Collection items) {
		super("com/jeta/swingbuilder/gui/components/list/itemsView.jfrm");
		m_model = new ItemsModel(items);
		JTable table = getTable(ItemsViewNames.ID_ITEMS_TABLE);
		table.setModel(m_model);
		setController(new ItemsController(this));

		int col_width = 60;
		TableColumnModel cmodel = table.getColumnModel();
		cmodel.getColumn(ItemsModel.ICON_COLUMN).setPreferredWidth(col_width);
		cmodel.getColumn(ItemsModel.LABEL_COLUMN).setPreferredWidth(col_width * 5);
	}

	public ListItemProperty getSelectedProperty() {
		JTable table = getTable(ItemsViewNames.ID_ITEMS_TABLE);
		int row = table.getSelectedRow();
		return (ListItemProperty) m_model.getRow(row);
	}

	/**
	 * @return a collection of ListItemProperty objects
	 */
	public Collection getItems() {
		LinkedList list = new LinkedList();
		for (int index = 0; index < m_model.getRowCount(); index++) {
			list.add(m_model.getRow(index));
		}
		return list;
	}

	/**
	 * @return the preferred size for this view
	 */
	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 245, 180);
	}
}
