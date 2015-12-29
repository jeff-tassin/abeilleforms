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

package com.jeta.swingbuilder.gui.editor;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jeta.forms.gui.form.GridView;

/**
 * Represents a row or column header that can be clicked or dragged to allow the
 * user to change the row/column specs on a form layout.
 * 
 * @author Jeff Tassin
 */
public class MarginCell extends JPanel {
	/**
	 * The one based index (either row or column index)
	 */
	private int m_index;

	/**
	 * The orientation of the cell (horizontal/vertical )
	 */
	private Orientation m_orientation;

	/**
	 * @directed
	 */
	private GridView m_gridview;

	/**
	 * ctor for a cell in a margin
	 * 
	 * @param orientation
	 *            the orientation of the cell (vertical/horizontal)
	 * @param index
	 *            the 1-based index of the cell
	 * @param view
	 *            the grid view associated with this cell
	 */
	public MarginCell(Orientation orientation, int index, GridView view) {
		m_index = index;
		m_orientation = orientation;
		m_gridview = view;
		setLayout(new BorderLayout());
		JButton btn = new JButton("");
		btn.setFocusPainted(false);
		add(btn, BorderLayout.CENTER);
	}

}
