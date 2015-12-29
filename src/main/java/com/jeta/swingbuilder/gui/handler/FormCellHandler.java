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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridOverlay;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.gui.handler.CellMouseHandler;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.editor.DesignFormComponent;
import com.jeta.swingbuilder.gui.editor.DesignGridOverlay;
import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * MouseHandler for a Form in design mode.
 * 
 * @author Jeff Tassin
 */
public class FormCellHandler extends AbstractMouseHandler {
	/** @directed */
	private DesignFormComponent m_comp;

	/**
	 * The component source
	 */
	private ComponentSource m_compsrc;

	public FormCellHandler(FormComponent comp, ComponentSource compsrc) {
		super(comp);
		assert (comp != null);
		assert (compsrc != null);
		m_comp = (DesignFormComponent) comp;
		m_compsrc = compsrc;
	}

	/**
	 * @return the grid component that contains the given mouse ponit.
	 */
	private GridComponent getComponent(MouseEvent e) {
		DesignGridOverlay overlay = (DesignGridOverlay) m_comp.getChildOverlay();
		Point local_pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), (Component) overlay);
		GridComponent cell = overlay.getCell(local_pt);
		if (cell == null) {
			DesignFormComponent parent = (DesignFormComponent) getParentForm(m_comp);
			if (parent != null) {
				DesignGridOverlay parentoverlay = (DesignGridOverlay) parent.getChildOverlay();
				local_pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), (Component) parentoverlay);

				int y = local_pt.y;
				int x = local_pt.x;

				if (x >= m_comp.getCellX() && x <= m_comp.getCellX() + m_comp.getCellWidth() && y >= m_comp.getCellY()
						&& y <= m_comp.getCellY() + m_comp.getCellHeight()) {
					cell = m_comp;
				}
			}
		}
		return cell;
	}

	/**
	 * @return the parent form of the given child form.
	 */
	public FormComponent getParentForm(FormComponent comp) {
		Component parent = comp.getParent();
		while (parent != null) {
			if (parent instanceof FormComponent)
				return (FormComponent) parent;
			else if (parent instanceof FormEditor)
				return null;
			else if (parent instanceof javax.swing.JFrame)
				return null;

			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * @return the view that is contained within this component.
	 */
	private GridView getView() {
		return (GridView) m_comp.getBean().getDelegate();
	}

	/**
	 * Determines if the mouse is pressed over a control button. If so, handles
	 * the event accordingly.
	 */
	private boolean handleControlButton(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			java.awt.Container btnpanel = m_comp.getButtonPanel();
			JComponent btn = m_comp.getExpandButton();
			Point local_pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), btnpanel);

			Rectangle rect = btn.getBounds();
			if (rect.contains(local_pt)) {
				m_comp.setGridViewVisible(!m_comp.isGridViewVisible());
				return true;
			}

			btn = m_comp.getEditButton();
			rect = btn.getBounds();
			if (rect.contains(local_pt)) {
				FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
				fmgr.showForm(m_comp.getId());
				return true;
			}

			btn = m_comp.getGridButton();
			rect = btn.getBounds();
			if (rect.contains(local_pt)) {
				GridView view = m_comp.getChildView();
				boolean bvis = !view.isGridVisible();
				if (bvis) {
					m_comp.setGridViewVisible(bvis);
				}
				else {
					view.setGridVisible(bvis);
				}
				return true;
			}
		}

		return false;
	}

	public void mousePressed(MouseEvent e) {
		GridView view = getView();
		if (view != null)
			view.deselectAll();

		GridComponent cell = getComponent(e);
		if (cell == m_comp) {
			if (!handleControlButton(e)) {
				GridView childview = m_comp.getChildView();
				childview.deselectAll();

				if (m_compsrc.isSelectionTool()) {
					m_comp.setSelected(true);
					m_comp.repaint();
					if (e.getClickCount() > 1) {
						m_comp.fireGridCellEvent(new GridCellEvent(GridCellEvent.EDIT_COMPONENT, m_comp));
					}
					else if (e.getClickCount() == 1) {
						setDragSource(this);
					}
				}
			}
		}
		else if (cell != null) {
			CellMouseHandler handler = cell.getMouseHandler();
			assert (handler != null);
			if (handler != null) {
				handler.mousePressed(e);
			}
			m_comp.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isDragging()) {
			GridComponent cell = getComponent(e);
			if (cell == m_comp) {
				if (getDragSource() != this) {
					// mouse released on this entire form, so we replace the
					// form.

				}
			}
			else if (cell != null) {
				if (getDragSource() != this) {
					// don't allow drops on children of this form if we are the
					// drag source
					CellMouseHandler handler = cell.getMouseHandler();
					if (handler != null) {
						handler.mouseReleased(e);
					}
					m_comp.repaint();
				}
			}
		}
	}

	/** MouseMotionListener implemenation */
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	/**
	 * MouseMotionListener implementation. Note that the source will always be
	 * the top level window. So, we need to convert the mouse point to the
	 * coordinates of our associated overlay window before any other operation.
	 */
	public void mouseMoved(MouseEvent e) {
		if (!m_compsrc.isSelectionTool() || isDragging()) {
			GridView view = getView();
			if (view != null)
				view.deselectAll();
		}

		GridOverlay overlay = m_comp.getChildOverlay();
		GridComponent cell = getComponent(e);
		if (cell != null) {
			CellMouseHandler handler = cell.getMouseHandler();
			if (handler == null) {
				// System.out.println( "found null handler at row: " +
				// cell.getRow() + " col: " + cell.getColumn() + " for: " +
				// cell.getComponent() );
				assert (false);
			}
			else {
				if (getDragSource() != this) {
					if (handler != this) {
						handler.mouseMoved(e);
					}
				}
			}
		}
	}

}
