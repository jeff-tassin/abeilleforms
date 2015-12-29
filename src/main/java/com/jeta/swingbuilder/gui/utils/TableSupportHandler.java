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

package com.jeta.swingbuilder.gui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;

import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.JETATableModel;

public class TableSupportHandler extends JETAController {
	private String m_table_name;
	private JETAPanel m_view;

	public TableSupportHandler(JETAPanel view, String tableName, String delId, String moveUp, String moveDown) {
		super(view);
		m_view = view;
		m_table_name = tableName;
		assignAction(delId, new DeleteItemAction());
		assignAction(moveDown, new MoveDownAction());
		assignAction(moveUp, new MoveUpAction());
	}

	public class DeleteItemAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JTable table = m_view.getTable(m_table_name);
			JETATableModel items_model = (JETATableModel) table.getModel();
			int row = table.getSelectedRow();
			if (row >= 0) {
				items_model.removeRow(row);
				row--;
				if (row < 0)
					row = 0;
				if (items_model.getRowCount() > row) {
					table.setRowSelectionInterval(row, row);
				}
			}
			table.repaint();
			m_view.repaint();
		}
	}

	public class MoveUpAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JTable table = m_view.getTable(m_table_name);
			JETATableModel tmodel = (JETATableModel) table.getModel();
			int row = table.getSelectedRow();
			if (row > 0 && table.getRowCount() > 1) {
				tmodel.moveRow(row, row, row - 1);
				table.setRowSelectionInterval(row - 1, row - 1);
			}
			table.repaint();
			m_view.repaint();
		}
	}

	public class MoveDownAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JTable table = m_view.getTable(m_table_name);
			JETATableModel tmodel = (JETATableModel) table.getModel();
			int row = table.getSelectedRow();
			if (row >= 0 && (row + 1) < table.getRowCount()) {
				tmodel.moveRow(row, row, row + 1);
				table.setRowSelectionInterval(row + 1, row + 1);
			}
			table.repaint();
			m_view.repaint();
		}
	}
}
