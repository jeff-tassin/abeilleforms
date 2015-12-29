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

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jeta.open.gui.framework.JETAController;

/**
 * 
 * @author Jeff Tassin
 */
public class LinePropertiesController extends JETAController {
	private LinePropertiesView m_view;

	/**
	 * ctor
	 */
	public LinePropertiesController(LinePropertiesView view) {
		super(view);
		m_view = view;
		JSpinner spinner = m_view.getSpinner(LinePropertiesNames.ID_LINE_THICKNESS_FIELD);
		spinner.addChangeListener(new SpinnerChangeListener());
	}

	/**
	 * Handler for changing line thickness
	 */
	public class SpinnerChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSpinner spinner = m_view.getSpinner(LinePropertiesNames.ID_LINE_THICKNESS_FIELD);
			Integer tval = (Integer) spinner.getValue();
			m_view.setThickness(tval.intValue());
		}
	}

}
