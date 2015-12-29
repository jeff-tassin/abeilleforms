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

package com.jeta.swingbuilder.gui.dimension;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.IntegerDocument;

public class DimensionView extends JETAPanel {
	private FormPanel m_view;

	public DimensionView() {
		this(null);
	}

	public DimensionView(Dimension dim) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/dimension/dimension.jfrm");
		add(m_view, BorderLayout.CENTER);

		m_view.getTextField(DimensionNames.ID_WIDTH_FIELD).setDocument(new IntegerDocument(false));
		m_view.getTextField(DimensionNames.ID_HEIGHT_FIELD).setDocument(new IntegerDocument(false));

		if (dim != null) {
			m_view.setText(DimensionNames.ID_WIDTH_FIELD, String.valueOf(dim.width));
			m_view.setText(DimensionNames.ID_HEIGHT_FIELD, String.valueOf(dim.height));
		}

	}

	public Dimension getDimension() {
		Dimension d = new Dimension(m_view.getInteger(DimensionNames.ID_WIDTH_FIELD, 20), m_view.getInteger(DimensionNames.ID_HEIGHT_FIELD, 20));
		return d;
	}
}
