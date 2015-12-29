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

package com.jeta.swingbuilder.gui.effects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jeta.open.gui.framework.JETAController;

/**
 * Controller for GradientView class.
 * 
 * @author Jeff Tassin
 */
public class GradientViewController extends JETAController {
	private GradientView m_view;

	public GradientViewController(GradientView view) {
		super(view);
		m_view = view;

		assignAction(GradientNames.ID_START_COLOR_SELECTOR, new GradientChangedAction());
		assignAction(GradientNames.ID_END_COLOR_SELECTOR, new GradientChangedAction());
		assignAction(GradientNames.ID_DIRECTION_COMBO, new GradientChangedAction());
		assignListener(GradientNames.ID_MAGNITUDE_SPINNER, new MagnitudeListener());
	}

	public class GradientChangedAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			m_view.updatePreview();
		}
	}

	public class MagnitudeListener implements ChangeListener {
		public void stateChanged(ChangeEvent evt) {
			m_view.updatePreview();
		}
	}

}
