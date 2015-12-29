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

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jeta.open.rules.JETARule;
import com.jeta.open.rules.RuleResult;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This panel shows two text fields that allow a user to specify the number of
 * rows and columns for a new form view.
 * 
 * @author Jeff Tassin
 */
public class GridSizePanel extends JETAPanel implements JETARule {

	public static final String ID_ROWS_FIELD = "rows.field";
	public static final String ID_COLS_FIELD = "cols.field";

	/**
	 * ctor
	 */
	public GridSizePanel() {
		FormLayout layout = new FormLayout("12px,pref,4dlu,pref,32px", "12px,pref,2dlu,pref,24px");
		setLayout(layout);
		CellConstraints cc = new CellConstraints();

		JTextField tf = new JTextField(5);
		tf.setDocument(new IntegerDocument(false));
		tf.setName(ID_COLS_FIELD);
		add(new JLabel(I18N.getLocalizedMessage("Columns")), cc.xy(2, 2));
		add(tf, cc.xy(4, 2));

		tf = new JTextField(5);
		tf.setDocument(new IntegerDocument(false));
		tf.setName(ID_ROWS_FIELD);
		add(new JLabel(I18N.getLocalizedMessage("Rows")), cc.xy(2, 4));
		add(tf, cc.xy(4, 4));
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * JETARule implementation
	 * 
	 * @param params
	 *            an array one element: the GridSizePanel we are validating. It
	 *            must be the same as the current this pointer.
	 */
	public RuleResult check(Object[] params) {
		assert (this == params[0]);
		GridSizePanel panel = this;
		if (getColumns() <= 0) {
			return new RuleResult(I18N.getLocalizedMessage("A form must have one or more columns."));
		}
		if (getRows() <= 0) {
			return new RuleResult(I18N.getLocalizedMessage("A form must have one or more rows."));
		}
		return RuleResult.SUCCESS;
	}

	/**
	 * @return the number of columns entered by the user
	 */
	public int getColumns() {
		return getInteger(ID_COLS_FIELD, 0);
	}

	/**
	 * @return the number of rows entered by the user
	 */
	public int getRows() {
		return getInteger(ID_ROWS_FIELD, 0);
	}
}
