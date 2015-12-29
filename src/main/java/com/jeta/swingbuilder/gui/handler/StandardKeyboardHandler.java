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

package com.jeta.swingbuilder.gui.handler;

import java.awt.event.KeyEvent;

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.forms.gui.handler.CellKeyboardHandler;

public class StandardKeyboardHandler implements CellKeyboardHandler {
	public StandardKeyboardHandler(StandardComponent comp, ComponentSource compsrc) {
		m_comp = comp;
	}

	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
			assert (m_comp.getParentView() != null);
			if (m_comp.isSelected() && m_comp.getParentView() != null) {
				// m_comp.getView().replaceComponent( m_comp );
			}
		}
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyTyped(KeyEvent evt) {
	}

	/**
	 * @directed
	 */
	private StandardComponent m_comp;
}
