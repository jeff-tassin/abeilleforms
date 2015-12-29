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
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * Renderer for the row header for a given table
 * 
 * @author Jeff Tassin
 */
public class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	private JTable m_table;
	private Border m_selectedborder;
	private Border m_normalborder;
	private Font m_selectedfont;
	private Font m_normalfont;

	RowHeaderRenderer(JTable table) {
		this.m_table = table;
		/** this needs to be updated if the LaF changes */
		m_normalborder = UIManager.getBorder("TableHeader.cellBorder");
		m_selectedborder = BorderFactory.createRaisedBevelBorder();
		final JTableHeader header = m_table.getTableHeader();
		m_normalfont = header.getFont();
		m_selectedfont = m_normalfont.deriveFont(m_normalfont.getStyle() | Font.BOLD);
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setOpaque(true);
		setHorizontalAlignment(CENTER);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (list.getSelectionModel().isSelectedIndex(index)) {
			setFont(m_selectedfont);
			setBorder(m_selectedborder);
		}
		else {
			setFont(m_normalfont);
			setBorder(m_normalborder);
		}
		String label = String.valueOf(index + 1);
		setText(label);
		return this;
	}
}
