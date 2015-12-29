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

package com.jeta.swingbuilder.gui.undo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class FormEditsRenderer extends DefaultTableCellRenderer {
	/**
	 * @directed
	 */
	private int m_next_add_index;

	private Font m_font;

	private Color m_selbg;
	private Color m_selfg;
	private Color m_bg;
	private Color m_fg;
	private Color m_idx_bg = new Color(249, 199, 124);

	/**
	 * ctor
	 */
	public FormEditsRenderer(int idx) {
		m_next_add_index = idx;

		m_selbg = UIManager.getColor("Table.selectionBackground");
		m_selfg = UIManager.getColor("Table.selectionForeground");
		m_fg = UIManager.getColor("Table.foreground");
		m_bg = UIManager.getColor("Table.background");
	}

	/**
	 * TableCellRenderer implementation
	 */
	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
		Component result = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, col);
		if (row == m_next_add_index)
			result.setBackground(m_idx_bg);
		else
			result.setBackground(m_bg);

		return result;
	}

}
