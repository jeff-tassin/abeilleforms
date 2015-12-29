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
import java.awt.event.KeyListener;

import com.jeta.forms.gui.handler.CellKeyboardHandler;

public class GridKeyboardListener implements KeyListener {
	public void keyPressed(KeyEvent event) {
		if (m_delegate != null)
			m_delegate.keyPressed(event);
	}

	public void keyReleased(KeyEvent event) {
		if (m_delegate != null)
			m_delegate.keyReleased(event);

	}

	public void keyTyped(KeyEvent event) {
		if (m_delegate != null)
			m_delegate.keyTyped(event);
	}

	public GridKeyboardListener(CellKeyboardHandler delegate) {
		assert (delegate != null);
		m_delegate = delegate;
	}

	/**
	 * @directed
	 */
	private CellKeyboardHandler m_delegate;

	/** @link dependency */
	/* # com.jeta.forms.gui.form.GridOverlay lnkGridOverlay; */
}
