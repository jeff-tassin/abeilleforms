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

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;

/**
 * The main purpose of this class is to display an icon in the header cell that
 * shows how the column is currently sorted. There can be one of three modes for
 * this: ASCENDING - show up arrow DESCENDING - show down arrow NONE - no image
 * 
 * @author Jeff Tassin
 */

class SortedColumnHeaderRenderer extends JLabel implements TableCellRenderer {
	static final ImageIcon m_upimage;
	static final ImageIcon m_downimage;
	static final ImageIcon m_emptyheaderimage;

	private SortMode m_sortmode; // the current sorting for a given column

	static {
		m_upimage = FormDesignerUtils.loadImage(Icons.UP_16);
		m_downimage = FormDesignerUtils.loadImage(Icons.DOWN_16);
		m_emptyheaderimage = FormDesignerUtils.loadImage("emptytableheader16.gif");

	}

	public SortedColumnHeaderRenderer() {
		super();
		this.setHorizontalAlignment(JLabel.CENTER);
	}

	/**
	 * Gets the sort mode for this renderer. Each column has a separate
	 * renderer. When the user sorts a given column, we put an icon in the
	 * header that shows whether the column is ascending, descending, or natural
	 * ordering.
	 * 
	 * @return mode the sort mode to set. We get the sort mode from the SortMode
	 *         class: ASCENDING, DESCENDING, or NONE
	 */
	public SortMode getSortMode() {
		return m_sortmode;
	}

	/**
	 * TableCellRenderer implementation
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// Try to set default fore- and background colors
		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				setForeground(header.getForeground());
				setBackground(header.getBackground());
				setFont(header.getFont());
				AbstractTableModel model = (AbstractTableModel) table.getModel();

				column = table.convertColumnIndexToModel(column);
				setText(model.getColumnName(column));
				// set normal border
				setBorder(javax.swing.UIManager.getBorder("TableHeader.cellBorder"));

				if (m_sortmode == SortMode.ASCENDING)
					setIcon(m_upimage);
				else if (m_sortmode == SortMode.DESCENDING)
					setIcon(m_downimage);
				else
					// if NONE
					setIcon(m_emptyheaderimage);
			}
		}
		return this;
	}

	/**
	 * Sets the sort mode for this renderer. Each column has a separate
	 * renderer. When the user sorts a given column, we put an icon in the
	 * header that shows whether the column is ascending, descending, or natural
	 * ordering.
	 * 
	 * @param mode
	 *            the sort mode to set. We get the sort mode from the SortMode
	 *            class: ASCENDING, DESCENDING, or NONE
	 */
	public void setSortMode(SortMode mode) {
		m_sortmode = mode;
	}
}
