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

package com.jeta.swingbuilder.gui.undo;

import javax.swing.JFrame;

import com.jeta.swingbuilder.gui.formmgr.EditorManager;

/**
 * The frame used to contain the form when we are testing
 * 
 * @author Jeff Tassin
 */
public class UndoManagerFrame extends JFrame {
	private UndoManagerView m_view;

	/**
	 * ctor
	 */
	public UndoManagerFrame(EditorManager editormgr) {
		super("Form Manager");
		m_view = new UndoManagerView(editormgr);
		getContentPane().add(m_view);
	}

	public void reload() {
		m_view.reload();
	}

}
