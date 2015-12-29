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

import javax.swing.BorderFactory;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Displays the column and row settings.
 * 
 * @author Jeff Tassin
 */
public class GridControlsView extends JETAPanel {
	private FormPanel m_col_panel;
	private FormPanel m_row_panel;

	public GridControlsView() {
		FormLayout layout = new FormLayout("center:pref:grow", "pref,2dlu,pref");
		CellConstraints cc = new CellConstraints();

		setLayout(layout);

		m_col_panel = new FormPanel("com/jeta/swingbuilder/gui/main/columnSpec.frm");
		m_row_panel = new FormPanel("com/jeta/swingbuilder/gui/main/rowSpec.frm");

		add(m_col_panel, cc.xy(1, 1));
		add(m_row_panel, cc.xy(1, 3));

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
}
