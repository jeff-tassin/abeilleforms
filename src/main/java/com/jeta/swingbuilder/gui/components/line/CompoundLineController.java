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

package com.jeta.swingbuilder.gui.components.line;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.jeta.forms.store.properties.LineProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;

/**
 * 
 * @author Jeff Tassin
 */
public class CompoundLineController extends JETAController {
	private CompoundLineView m_view;

	/**
	 * ctor
	 */
	public CompoundLineController(CompoundLineView view) {
		super(view);
		m_view = view;
		assignAction(CompoundLineNames.ID_NEW_LINE, new NewLineAction());
		assignAction(CompoundLineNames.ID_EDIT_LINE, new EditLineAction());
		assignAction(CompoundLineNames.ID_DELETE_LINE, new DeleteLineAction());
		assignAction(CompoundLineNames.ID_MOVE_UP, new MoveUpAction());
		assignAction(CompoundLineNames.ID_MOVE_DOWN, new MoveDownAction());
	}

	/**
	 * Edits the given line property
	 */
	private void editLine(LineProperty lp) {
		LinePropertiesView view = new LinePropertiesView(lp);
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_view, true);
		dlg.setPrimaryPanel(view);
		dlg.setSize(dlg.getPreferredSize());
		if (lp == null)
			dlg.setTitle(I18N.getLocalizedMessage("New Line"));
		else
			dlg.setTitle(I18N.getLocalizedMessage("Edit Line"));

		dlg.showCenter();
		if (dlg.isOk()) {
			if (lp == null) {
				m_view.addLine(view.getLineProperty());
			}
			else {
				m_view.setLine(view.getLineProperty(), lp);
			}
		}
	}

	public class DeleteLineAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.deleteSelectedLine();
		}
	}

	public class EditLineAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			LineProperty lp = m_view.getSelectedLine();
			if (lp != null) {
				editLine(lp);
			}
		}
	}

	/**
	 * Moves the current line up
	 */
	public class MoveUpAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JList list = m_view.getList(CompoundLineNames.ID_LINES_LIST);
			DefaultListModel model = (DefaultListModel) list.getModel();
			int index = list.getSelectedIndex();
			if (index > 0 && model.size() > 1) {
				Object mv_obj = model.getElementAt(index);
				Object next_obj = model.getElementAt(index - 1);
				model.setElementAt(mv_obj, index - 1);
				model.setElementAt(next_obj, index);
				list.setSelectedIndex(index - 1);
			}
			m_view.ensureIndexIsVisible();
		}
	}

	/**
	 * Moves the current line down
	 */
	public class MoveDownAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JList list = m_view.getList(CompoundLineNames.ID_LINES_LIST);
			DefaultListModel model = (DefaultListModel) list.getModel();
			int index = list.getSelectedIndex();
			if ((index + 1) < model.size()) {
				Object mv_obj = model.getElementAt(index);
				Object next_obj = model.getElementAt(index + 1);
				model.setElementAt(mv_obj, index + 1);
				model.setElementAt(next_obj, index);
				list.setSelectedIndex(index + 1);
			}
			m_view.ensureIndexIsVisible();
		}
	}

	/**
	 * Creates a new line
	 */
	public class NewLineAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			editLine(null);
		}
	}

}
