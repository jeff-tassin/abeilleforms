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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.handler.CellMouseHandler;

/**
 * Mouse event handler for a standard grid cell that contains a JTabbedPane.
 * 
 * @author Jeff Tassin
 */
public class TabPaneCellHandler extends StandardCellHandler {
	/** @directed */
	private JTabbedPane m_tabpane;

	/** ctor */
	public TabPaneCellHandler(GridComponent comp, ComponentSource compsrc) {
		super(comp, compsrc);
		m_tabpane = (JTabbedPane) comp.getBeanDelegate();
	}

	/**
	 * If the mouse event is over a form contained by the JTabPane
	 */
	private FormComponent getForm(MouseEvent e) {
		Component comp = m_tabpane.getSelectedComponent();
		if (comp instanceof FormComponent) {
			FormComponent fc = (FormComponent) comp;
			Point local_pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), fc.getParent());
			if (local_pt.x >= fc.getX() && local_pt.x < (fc.getX() + fc.getWidth()) && local_pt.y >= fc.getY() && local_pt.y < (fc.getY() + fc.getHeight())) {
				return fc;
			}
		}
		return null;
	}

	/**
	 * MouseMotionListener implementation. Note that the source will always be
	 * the top level window. So, we need to convert the mouse point to the
	 * coordinates of our associated overlay window before any other operation.
	 */
	public void mouseMoved(MouseEvent e) {
		FormComponent form = getForm(e);
		if (form != null) {
			CellMouseHandler handler = form.getMouseHandler();
			if (handler != null)
				handler.mouseMoved(e);
		}
		else {
			super.mouseMoved(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		FormComponent form = getForm(e);
		if (form != null) {
			CellMouseHandler handler = form.getMouseHandler();
			if (handler != null)
				handler.mousePressed(e);
		}
		else {
			isTabClicked(e);
			super.mousePressed(e);
		}
	}

	private boolean isTabClicked(MouseEvent e) {
		Point local_pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), m_tabpane);
		for (int index = 0; index < m_tabpane.getTabCount(); index++) {
			java.awt.Rectangle rect = m_tabpane.getBoundsAt(index);
			if (rect.contains(local_pt)) {
				m_tabpane.setSelectedIndex(index);
				return true;
			}
		}
		return false;
	}

	public void mouseReleased(MouseEvent e) {
		FormComponent form = getForm(e);
		if (form != null) {
			CellMouseHandler handler = form.getMouseHandler();
			if (handler != null)
				handler.mouseReleased(e);
		}
		else {
			super.mouseReleased(e);
		}
	}

	/** MouseMotionListener implemenation */
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

}
