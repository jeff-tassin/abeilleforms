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

package com.jeta.swingbuilder.gui.properties;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.jeta.forms.gui.beans.JETAPropertyDescriptor;

/**
 * 
 */
public class PropertyColumnModel extends DefaultTableColumnModel {
	private final static String COL_LABEL_PROP = "Property";
	private final static String COL_LABEL_DESC = "Description";
	private final static String COL_LABEL_VALUE = "Value";

	private static final int minColWidth = 150;

	/**
	 * PropertyColumnModel
	 */
	public PropertyColumnModel() {
		// Configure the columns and add them to the model
		TableColumn column;

		// Property
		column = new TableColumn(0);
		column.setHeaderValue(COL_LABEL_PROP);
		column.setPreferredWidth(minColWidth);
		column.setCellRenderer(new PropertyNameRenderer());
		addColumn(column);

		// Value
		column = new TableColumn(1);
		column.setHeaderValue(COL_LABEL_VALUE);
		column.setPreferredWidth(minColWidth * 2);
		addColumn(column);
	}

	/**
	 * Renders the name of the property. Sets the short description of the
	 * property as the tooltip text.
	 */
	class PropertyNameRenderer extends DefaultTableCellRenderer {
		/**
		 * Get UI for current editor, including custom editor button if
		 * applicable.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			PropertyTableModel model = (PropertyTableModel) table.getModel();
			JETAPropertyDescriptor desc = model.getPropertyDescriptor(row);

			setToolTipText(desc.getShortDescription());
			setBackground(UIManager.getColor("control"));

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
