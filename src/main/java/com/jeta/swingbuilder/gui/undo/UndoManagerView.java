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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.TableUtils;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.EditorManager;

/**
 * This class displays the list of undoable edits in the system.
 * 
 * @author Jeff Tassin
 */
public class UndoManagerView extends JETAPanel {
	private JTabbedPane m_form_edits_tab = new JTabbedPane();

	private EditorManager m_editor_mgr;

	/**
	 * ctor
	 */
	public UndoManagerView(EditorManager emgr) {
		m_editor_mgr = emgr;
		setLayout(new BorderLayout());

		add(createButtonPanel(), BorderLayout.NORTH);
		add(m_form_edits_tab, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

		reload();
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
	private JComponent createFormEditsTable(FormEditor editor) {
		EditorUndoManager undomgr = (EditorUndoManager) editor.getUndoManager();

		FormEditsModel model = new FormEditsModel(undomgr);
		JTable table = TableUtils.createBasicTablePanel(model, false);

		TableColumnModel cmodel = table.getColumnModel();
		cmodel.getColumn(FormEditsModel.EDIT_NAME_COLUMN).setCellRenderer(new FormEditsRenderer(undomgr.getIndexOfNextAdd()));

		cmodel.getColumn(FormEditsModel.EDIT_NAME_COLUMN).setPreferredWidth(200);
		cmodel.getColumn(FormEditsModel.CAN_UNDO_COLUMN).setPreferredWidth(32);
		cmodel.getColumn(FormEditsModel.CAN_REDO_COLUMN).setPreferredWidth(32);

		return new JScrollPane(table);
	}

	private JPanel createFormEditsView(FormEditor editor) {
		EditorUndoManager undomgr = (EditorUndoManager) editor.getUndoManager();
		JPanel panel = new JPanel(new BorderLayout(4, 4));

		JPanel toppanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toppanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		toppanel.add(new JLabel("indexOfNextAdd: " + undomgr.getIndexOfNextAdd()));

		panel.add(toppanel, BorderLayout.NORTH);
		panel.add(createFormEditsTable(editor));
		return panel;
	}

	public void reload() {
		m_form_edits_tab.removeAll();
		Collection editors = m_editor_mgr.getEditors();
		Iterator iter = editors.iterator();
		while (iter.hasNext()) {
			FormEditor editor = (FormEditor) iter.next();
			JScrollPane scroll = new JScrollPane(createFormEditsView(editor));

			String title = "";
			if (editor.isLinked()) {
				String filename = editor.getForm().getFileName();
				if (filename == null)
					filename = "New Form";
				title = filename;
			}
			else {
				title = editor.getForm().getName();
			}
			m_form_edits_tab.addTab(title, scroll);
		}
	}

}
