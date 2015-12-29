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

package com.jeta.swingbuilder.gui.formmgr;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.TableUtils;

/**
 * This class displays the list of forms in the form manager.
 * 
 * @author Jeff Tassin
 */
public class FormManagerView extends JETAPanel {
	/** the table that displays the forms */
	private JTable m_table;
	/** the scroll pane for the table */
	private JScrollPane m_scrollpane;
	/* model for the triggers */
	private FormsModel m_model;
	private FormsTree m_forms_tree;

	/**
	 * ctor
	 */
	public FormManagerView() {
		setLayout(new BorderLayout());
		m_model = new FormsModel();
		add(createButtonPanel(), BorderLayout.NORTH);

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Table", createTable());
		tab.addTab("Tree", createTree());
		add(tab, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btn = new JButton("Reload");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				reload();
			}
		});
		panel.add(btn);
		return panel;
	}

	/**
	 * Creates the JTable that displays the table triggers
	 * 
	 * @returns the table component
	 */
	private JComponent createTable() {
		JTable table = TableUtils.createBasicTablePanel(m_model, false);
		return new JScrollPane(table);
	}

	private JComponent createTree() {
		m_forms_tree = new FormsTree();
		JScrollPane scroll = new JScrollPane(m_forms_tree);
		return scroll;
	}

	/**
	 * @return the underlying data model
	 */
	public FormsModel getTableModel() {
		return m_model;
	}

	public void reload() {
		m_model.reload();
		m_forms_tree.reload();
	}

}
