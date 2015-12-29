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

package com.jeta.swingbuilder.gui.main;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSpinner;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.UIDirector;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.commands.ChangeGroupCommand;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.EditColumnSpecCommand;
import com.jeta.swingbuilder.gui.commands.EditRowSpecCommand;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The controller for this panel
 */
public class SpecViewController extends JETAController {
	/**
	 * The view we are handling events for.
	 */
	private SpecView m_view;

	/**
	 * The form manager.
	 */
	private FormManager m_formmgr;

	/**
	 * A map of component names to ActionListeners for components in the view.
	 * m_listeners<String,ActionListener>
	 */
	private HashMap m_listeners = new HashMap();

	/**
	 * ctor
	 */
	public SpecViewController(SpecView view) {
		super(view);
		m_view = view;
		m_formmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		assignAction(SpecViewNames.ID_ALIGNMENT_COMBO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_CONST_SIZE_AMT_FIELD, new ConstantSizeListener());
		assignAction(SpecViewNames.ID_CONST_SIZE_UNITS_COMBO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_COMP_SIZE_COMBO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_BOUNDED_MIN_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_BOUNDED_MAX_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_RESIZE_NONE_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_RESIZE_GROW_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_RESIZE_GROW_WEIGHT, new WeightAction());

		assignAction(SpecViewNames.ID_CONSTANT_SIZE_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_COMPONENT_SIZE_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_BOUNDED_SIZE_RADIO, new SpecChangeListener());
		assignAction(SpecViewNames.ID_GROUP_APPLY_BTN, new GroupChangedAction());

		view.setUIDirector(new SpecViewUIDirector(m_view));
	}

	/**
	 * Gets the latest settings from the view and updates the Form.
	 */
	private void updateForm() {
		String newspec = FormUtils.toEncodedString(m_view);
		FormEditor editor = (FormEditor) m_formmgr.getCurrentEditor();
		if (editor != null) {
			GridComponent gc = editor.getSelectedComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				int row = gc.getRow();
				int col = gc.getColumn();
				if (m_view.isRowView()) {
					RowSpec oldspec = view.getRowSpec(row);
					EditRowSpecCommand cmd = new EditRowSpecCommand(view.getParentForm(), row, new RowSpec(newspec), oldspec);
					CommandUtils.invoke(cmd, editor);
				}
				else {
					ColumnSpec oldspec = view.getColumnSpec(col);
					EditColumnSpecCommand cmd = new EditColumnSpecCommand(view.getParentForm(), col, new ColumnSpec(newspec), oldspec);
					CommandUtils.invoke(cmd, editor);
				}
			}
		}
	}

	/**
	 * Changes the group in the current view.
	 */
	public class GroupChangedAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = m_view.getCurrentComponent();
			if (gc != null) {
				GridView view = gc.getParentView();
				if (view != null) {
					int current_group = m_view.getGroupId(gc);
					JSpinner sp = m_view.getSpinner(SpecViewNames.ID_GROUP_NUMBER_SPINNER);
					int new_group = ((Integer) sp.getValue()).intValue();
					if (new_group != current_group) {
						int index = m_view.isRowView() ? gc.getRow() : gc.getColumn();
						ChangeGroupCommand cmd = new ChangeGroupCommand(view.getParentForm(), new_group, current_group, index, m_view.isRowView());
						CommandUtils.invoke(cmd, FormEditor.getEditor(view));
					}
				}
			}
		}
	}

	/**
	 * Listener for most controls on SpecView
	 */
	public class SpecChangeListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			updateForm();
		}
	}

	private void constantSizeChanged() {
		String result = m_view.getTextField(SpecViewNames.ID_CONST_SIZE_AMT_FIELD).getText();
		try {
			double sz = Double.parseDouble(result);
			updateForm();
		} catch (Exception e) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			toolkit.beep();
			System.out.println("SpecViewController.constantSize exception");
		}
	}

	/**
	 * Listener for constant size amount field on SpecView
	 */
	public class ConstantSizeListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			constantSizeChanged();
		}
	}

	/**
	 * Listener for weight field on SpecView
	 */
	public class WeightAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String result = m_view.getText(SpecViewNames.ID_RESIZE_GROW_WEIGHT);
			try {
				double sz = Double.parseDouble(result);
				if (sz > 1.0) {
					m_view.setText(SpecViewNames.ID_RESIZE_GROW_WEIGHT, "1.0");
				}
				updateForm();
			} catch (Exception e) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				toolkit.beep();
				System.out.println("SpecViewController.weight exception");
			}
		}
	}

	/**
	 * The UIDirector for the SpecView
	 */
	public static class SpecViewUIDirector implements UIDirector {
		/**
		 * The view we are handling events for.
		 */
		private SpecView m_view;

		/**
		 * ctor
		 */
		public SpecViewUIDirector(SpecView view) {
			m_view = view;
		}

		/**
		 * Updates the components in the view
		 */
		public void updateComponents(java.util.EventObject evt) {
			if (m_view.isConstantSize()) {
				m_view.enableComponent(SpecViewNames.ID_CONST_SIZE_AMT_FIELD, true);
				m_view.enableComponent(SpecViewNames.ID_CONST_SIZE_UNITS_COMBO, true);
				m_view.enableComponent(SpecViewNames.ID_COMP_SIZE_COMBO, false);
				m_view.enableComponent(SpecViewNames.ID_BOUNDED_MIN_RADIO, false);
				m_view.enableComponent(SpecViewNames.ID_BOUNDED_MAX_RADIO, false);
			}
			else if (m_view.isComponentSize()) {
				m_view.enableComponent(SpecViewNames.ID_CONST_SIZE_AMT_FIELD, false);
				m_view.enableComponent(SpecViewNames.ID_CONST_SIZE_UNITS_COMBO, false);
				m_view.enableComponent(SpecViewNames.ID_COMP_SIZE_COMBO, true);
				m_view.enableComponent(SpecViewNames.ID_BOUNDED_MIN_RADIO, false);
				m_view.enableComponent(SpecViewNames.ID_BOUNDED_MAX_RADIO, false);

			}
			else if (m_view.isBoundedSize()) {
				m_view.enableComponent(SpecViewNames.ID_CONST_SIZE_AMT_FIELD, true);
				m_view.enableComponent(SpecViewNames.ID_CONST_SIZE_UNITS_COMBO, true);
				m_view.enableComponent(SpecViewNames.ID_COMP_SIZE_COMBO, true);
				m_view.enableComponent(SpecViewNames.ID_BOUNDED_MIN_RADIO, true);
				m_view.enableComponent(SpecViewNames.ID_BOUNDED_MAX_RADIO, true);
			}
			m_view.enableComponent(SpecViewNames.ID_RESIZE_GROW_WEIGHT, m_view.isResizeGrow());
		}
	}
}
