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

import java.awt.BorderLayout;
import java.awt.Insets;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.ComponentConstraints;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.forms.gui.form.ReadOnlyConstraints;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.IntegerDocument;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Displays the cell constraints for a cell
 * 
 * @author Jeff Tassin
 */
public class CellConstraintsView extends JETAPanel implements GridViewListener {
	/**
	 * The panel that contains the form.
	 */
	private FormPanel m_view;

	/**
	 * The component that we are currently displaying constraints for.
	 */
	private GridComponent m_current_comp;

	public CellConstraintsView() {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/main/cellConstraints.frm");
		add(m_view, BorderLayout.CENTER);

		/** require all text fields to allow only numbers */
		getTextField(CellConstraintsNames.ID_COLUMN_SPAN).setDocument(new IntegerDocument(false));
		getTextField(CellConstraintsNames.ID_ROW_SPAN).setDocument(new IntegerDocument(false));
		getTextField(CellConstraintsNames.ID_INSETS_TOP).setDocument(new IntegerDocument(true));
		getTextField(CellConstraintsNames.ID_INSETS_LEFT).setDocument(new IntegerDocument(true));
		getTextField(CellConstraintsNames.ID_INSETS_BOTTOM).setDocument(new IntegerDocument(true));
		getTextField(CellConstraintsNames.ID_INSETS_RIGHT).setDocument(new IntegerDocument(true));

		setController(new CellConstraintsController(this));
	}

	/**
	 * @return the column of the current component
	 */
	public int getColumn() {
		return m_current_comp.getColumn();
	}

	/**
	 * @return the column span entered in the view
	 */
	public int getColumnSpan() {
		return getInteger(CellConstraintsNames.ID_COLUMN_SPAN, 1);
	}

	/**
	 * @return the component constraints for this view
	 */
	public ComponentConstraints getComponentConstraints() {
		return new ReadOnlyConstraints(getColumn(), getRow(), getColumnSpan(), getRowSpan(), getHorizontalAlignment(), getVerticalAlignment(), getCellInsets());
	}

	/**
	 * @return the currently selected grid component
	 */
	public GridComponent getGridComponent() {
		return m_current_comp;
	}

	/**
	 * @return the component's horizontal alignment.
	 */
	public CellConstraints.Alignment getHorizontalAlignment() {
		return FormUtils.toAlignment((String) getSelectedItem(CellConstraintsNames.ID_HORIZONTAL_ALIGNMENT));
	}

	/**
	 * @return the insets displayed in the view
	 */
	public Insets getCellInsets() {
		return new Insets(getInteger(CellConstraintsNames.ID_INSETS_TOP, 0), getInteger(CellConstraintsNames.ID_INSETS_LEFT, 0), getInteger(
				CellConstraintsNames.ID_INSETS_BOTTOM, 0), getInteger(CellConstraintsNames.ID_INSETS_RIGHT, 0));
	}

	/**
	 * @return the row of the current component
	 */
	public int getRow() {
		return m_current_comp.getRow();
	}

	/**
	 * @return the row span entered in the view
	 */
	public int getRowSpan() {
		return getInteger(CellConstraintsNames.ID_ROW_SPAN, 1);
	}

	/**
	 * @return the component's vertical alignment.
	 */
	public CellConstraints.Alignment getVerticalAlignment() {
		return FormUtils.toAlignment((String) getSelectedItem(CellConstraintsNames.ID_VERTICAL_ALIGNMENT));
	}

	/**
	 * GridViewListener implementation
	 */
	public void gridChanged(GridViewEvent evt) {
		GridComponent comp = evt.getComponent();
		update(comp);
	}

	/**
	 * Updates the panel using the constraint info from the currently selected
	 * cell in the given editor
	 */
	public void update(GridComponent gc) {
		m_current_comp = gc;
		JETAController controller = getController();
		try {
			controller.enableEvents(false);
			if (gc == null) {
				setEnabled(false);
			}
			else {
				setEnabled(true);
				int row = gc.getRow();
				int col = gc.getColumn();
				ComponentConstraints cc = gc.getConstraints();

				setText(CellConstraintsNames.ID_COLUMN_FIELD, String.valueOf(cc.getColumn()));
				setText(CellConstraintsNames.ID_ROW_FIELD, String.valueOf(cc.getRow()));
				setText(CellConstraintsNames.ID_COLUMN_SPAN, String.valueOf(cc.getColumnSpan()));
				setText(CellConstraintsNames.ID_ROW_SPAN, String.valueOf(cc.getRowSpan()));

				Insets insets = cc.getInsets();
				setText(CellConstraintsNames.ID_INSETS_TOP, String.valueOf(insets.top));
				setText(CellConstraintsNames.ID_INSETS_LEFT, String.valueOf(insets.left));
				setText(CellConstraintsNames.ID_INSETS_BOTTOM, String.valueOf(insets.bottom));
				setText(CellConstraintsNames.ID_INSETS_RIGHT, String.valueOf(insets.right));

				setSelectedItem(CellConstraintsNames.ID_HORIZONTAL_ALIGNMENT, cc.getHorizontalAlignment().toString().toUpperCase());
				setSelectedItem(CellConstraintsNames.ID_VERTICAL_ALIGNMENT, cc.getVerticalAlignment().toString().toUpperCase());

				GridView parentview = gc.getParentView();
				Object pp = parentview.getPaintProperty(col, row);
				setText(CellConstraintsNames.ID_FILL_LABEL, pp == null ? "No Fill" : pp.toString());
			}
		} finally {
			controller.enableEvents(true);
		}
	}
}
