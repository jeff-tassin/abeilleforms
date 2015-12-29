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

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridOverlay;
import com.jeta.forms.gui.handler.CellMouseHandler;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.dnd.DesignerDragSource;
import com.jeta.swingbuilder.gui.editor.DesignGridOverlay;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;

public class GridMouseListener implements MouseListener, MouseMotionListener, DesignerDragSource {
	/* # GridOverlay lnkGridOverlay; */

	/** @directed */
	private CellMouseHandler m_delegate;

	/**
	 * The top level grid overlay
	 */
	private DesignGridOverlay m_topoverlay;

	private boolean m_drag_mouse = false;
	private boolean m_dragging = false;

	public GridMouseListener(GridOverlay topOverlay, CellMouseHandler delegate) {
		assert (delegate != null);

		m_delegate = delegate;
		m_topoverlay = (DesignGridOverlay) topOverlay;
	}

	/**
	 * DesignerDragSource implementation
	 */
	public void cancelDrag() {
		if (m_dragging) {
			m_topoverlay.setCursor(Cursor.getDefaultCursor());

			GridComponent dragsrc = null;
			CellMouseHandler draghandler = AbstractMouseHandler.getDragSource();
			if (draghandler != null)
				dragsrc = draghandler.getGridComponent();

			AbstractMouseHandler.setDragSource(null);
			FormManagerDesignUtils.deselectAll(m_topoverlay.getForm());
			if (dragsrc != null) {
				dragsrc.setSelected(true);
			}
			m_dragging = false;
		}
	}

	public boolean isDragging() {
		return m_drag_mouse;
	}

	/** MouseListener implementation */
	public void mouseClicked(MouseEvent e) {
		if (m_delegate != null)
			m_delegate.mouseClicked(e);

		m_topoverlay.requestFocus();
	}

	public void mouseEntered(MouseEvent e) {
		if (m_delegate != null)
			m_delegate.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		if (m_delegate != null)
			m_delegate.mouseExited(e);
	}

	public void mousePressed(MouseEvent e) {
		JETARegistry.rebind(DesignerDragSource.COMPONENT_ID, this);
		if (m_delegate != null)
			m_delegate.mousePressed(e);

		m_topoverlay.requestFocus();
		m_dragging = true;
		m_drag_mouse = false;
	}

	public void mouseReleased(MouseEvent e) {
		if (m_dragging) {
			m_dragging = false;
			m_drag_mouse = false;

			m_topoverlay.setCursor(Cursor.getDefaultCursor());
			if (m_delegate != null)
				m_delegate.mouseReleased(e);

			GridComponent dragsrc = null;
			CellMouseHandler draghandler = AbstractMouseHandler.getDragSource();
			if (draghandler != null)
				dragsrc = draghandler.getGridComponent();

			AbstractMouseHandler.setDragSource(null);

			if (dragsrc != null && dragsrc.isSelected()) {
				dragsrc.fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_SELECTED, dragsrc));
			}
		}
	}

	/** MouseMotionListener implemenation */
	public void mouseDragged(MouseEvent e) {
		if (m_dragging) {
			m_drag_mouse = true;
			m_topoverlay.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			if (m_delegate != null)
				m_delegate.mouseDragged(e);
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (m_delegate != null)
			m_delegate.mouseMoved(e);
	}

}
