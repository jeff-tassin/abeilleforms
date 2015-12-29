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

package com.jeta.swingbuilder.gui.colorchooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.open.gui.framework.JETAController;

public class RecentSwatchController extends JETAController {
	private JETAColorChooser m_color_chooser;

	private RecentSwatchPanel m_recent_panel;

	public RecentSwatchController(RecentSwatchPanel panel, JETAColorChooser cc) {
		super(cc);
		m_recent_panel = panel;
		m_color_chooser = cc;
		m_recent_panel.addMouseListener(new RecentSwatchListener());
	}

	class RecentSwatchListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			ColorHolder ch = m_recent_panel.getColorForLocation(e.getX(), e.getY());
			m_color_chooser.setColor(ch.getColor());
		}
	}
}
