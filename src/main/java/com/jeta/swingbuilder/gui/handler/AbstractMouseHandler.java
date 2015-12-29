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

import java.awt.event.MouseEvent;

import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.handler.CellMouseHandler;

/**
 * AbstractMouse event handler for a standard grid cell.
 * 
 * @author Jeff Tassin
 */
public abstract class AbstractMouseHandler implements CellMouseHandler {
	/**
	 * The grid component.
	 */
	private GridComponent m_comp;

	/**
	 * Set to non-null if we are currently dragging the component associated
	 * with this handler
	 */
	private static CellMouseHandler m_drag_source;

	/**
	 * ctor
	 */
	public AbstractMouseHandler(GridComponent gc) {
		m_comp = gc;
	}

	/**
	 * @return true if we are currently dragging a component.
	 */
	public static boolean isDragging() {
		return (m_drag_source != null);
	}

	public static CellMouseHandler getDragSource() {
		return m_drag_source;
	}

	/**
	 * @return the grid component associated with this handler.
	 */
	public GridComponent getGridComponent() {
		return m_comp;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {

	}

	public static void setDragSource(CellMouseHandler handler) {
		m_drag_source = handler;
	}
}
