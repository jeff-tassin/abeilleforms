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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.SetCellBackgroundCommand;
import com.jeta.swingbuilder.gui.commands.SetConstraintsCommand;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.effects.PaintAssignmentView;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Controller for CellConstraintsView
 * 
 * @author Jeff Tassin
 */
public class CellConstraintsController extends JETAController {
	/**
	 * The view we are handling events for
	 */
	private CellConstraintsView m_view;

	/**
	 * ctor
	 */
	public CellConstraintsController(CellConstraintsView view) {
		super(view);
		m_view = view;
		assignAction(CellConstraintsNames.ID_COLUMN_SPAN, new SpanAction());
		assignAction(CellConstraintsNames.ID_ROW_SPAN, new SpanAction());
		assignAction(CellConstraintsNames.ID_INSETS_TOP, new ConstraintsAction());
		assignAction(CellConstraintsNames.ID_INSETS_LEFT, new ConstraintsAction());
		assignAction(CellConstraintsNames.ID_INSETS_BOTTOM, new ConstraintsAction());
		assignAction(CellConstraintsNames.ID_INSETS_RIGHT, new ConstraintsAction());
		assignAction(CellConstraintsNames.ID_FILL_BUTTON, new FillAction());
		assignAction(CellConstraintsNames.ID_HORIZONTAL_ALIGNMENT, new ConstraintsAction());
		assignAction(CellConstraintsNames.ID_VERTICAL_ALIGNMENT, new ConstraintsAction());
	}

	public class ConstraintsAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = m_view.getGridComponent();
			if (gc != null) {
				GridView parentview = gc.getParentView();
				SetConstraintsCommand cmd = new SetConstraintsCommand(parentview.getParentForm(), gc, m_view.getComponentConstraints());
				CommandUtils.invoke(cmd, FormEditor.getEditor(gc));
				m_view.update(gc);
			}
		}
	}

	/**
	 * Sets the fill property for the currently selected cell in the view.
	 */
	public class FillAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = m_view.getGridComponent();
			if (gc != null) {
				GridView parentview = gc.getParentView();
				int row = gc.getRow();
				int col = gc.getColumn();

				PaintAssignmentView view = new PaintAssignmentView(parentview.getPaintProperty(col, row));
				JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_view, true);
				dlg.setPrimaryPanel(view);
				dlg.setSize(dlg.getPreferredSize());
				dlg.setTitle(I18N.getLocalizedMessage("Edit Fill"));
				dlg.showCenter();
				if (dlg.isOk()) {
					PaintProperty pp = view.getPaintProperty();
					PaintProperty oldpaint = parentview.getPaintProperty(col, row);
					SetCellBackgroundCommand cmd = new SetCellBackgroundCommand(parentview.getParentForm(), col, row, pp, oldpaint);
					CommandUtils.invoke(cmd, FormEditor.getEditor(parentview));
				}
			}
		}
	}

	public class SpanAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			GridComponent gc = m_view.getGridComponent();
			if (gc != null) {
				FormDesignerUtils.setSpan(gc, m_view.getColumnSpan(), m_view.getRowSpan());
			}
		}
	}

}
