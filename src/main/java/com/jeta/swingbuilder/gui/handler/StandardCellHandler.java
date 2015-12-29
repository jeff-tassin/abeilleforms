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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.commands.AddComponentCommand;
import com.jeta.swingbuilder.gui.commands.CommandUtils;
import com.jeta.swingbuilder.gui.commands.EditTextPropertyCommand;
import com.jeta.swingbuilder.gui.commands.MoveComponentCommand;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;

/**
 * Mouse event handler for a standard grid cell. This is for a cell that
 * contains any component except a child form (i.e. GridView)
 * 
 * @author Jeff Tassin
 */
public class StandardCellHandler extends AbstractMouseHandler {
	/** @directed */
	private GridComponent m_cell;
	private ComponentSource m_compsrc;

	/** ctor */
	public StandardCellHandler(GridComponent comp, ComponentSource compsrc) {
		super(comp);
		m_cell = comp;
		m_compsrc = compsrc;
		assert (m_cell != null);
		assert (compsrc != null);
	}

	/**
	 * @return true if the cell associated with this handler contains the mouse
	 *         point for the given event.
	 */
	private boolean containsMouse(MouseEvent e) {
		Point local_pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), m_cell.getParent());
		if (local_pt.x >= m_cell.getCellX() && local_pt.x < (m_cell.getCellX() + m_cell.getCellWidth()) && local_pt.y >= m_cell.getCellY()
				&& local_pt.y < (m_cell.getCellY() + m_cell.getCellHeight())) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return the component under the given mouse. This can be different that
	 *         the component associated with this handler if another component
	 *         spans multiple cells and overlaps the cell occupied by this
	 *         component.
	 */
	private GridComponent getComponent(MouseEvent e) {
		if (containsMouse(e)) {
			GridView view = getView();
			GridComponent gc = view.getOverlappingComponent(m_cell.getColumn(), m_cell.getRow());
			if (gc == null)
				return m_cell;
			else
				return gc;
		}
		return null;
	}

	/** @return the grid view associated with this handler */
	public GridView getView() {
		return m_cell.getParentView();
	}

	/**
	 * MouseMotionListener implementation. Note that the source will always be
	 * the top level window. So, we need to convert the mouse point to the
	 * coordinates of our associated overlay window before any other operation.
	 */
	public void mouseMoved(MouseEvent e) {
		if (!m_compsrc.isSelectionTool() || isDragging()) {
			GridComponent gc = getComponent(e);
			if (gc != null) {
				/**
				 * If the component is not the component associated with this
				 * handler, let's just forward the call to that component. The
				 * reason is the component might span multiple cells and it
				 * might be a child form.
				 */
				if (gc != m_cell) {
					gc.getMouseHandler().mouseMoved(e);
				}
				else {
					gc.setSelected(true);
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (containsMouse(e)) {
			FormEditor editor = FormEditor.getEditor(getView());
			GridComponent gc = getComponent(e);
			if (gc != null && editor != null) {
				/**
				 * If the component is not the component associated with this
				 * handler, let's just forward the call to that component. The
				 * reason is the component might span multiple cells or it might
				 * be a child form.
				 */
				if (gc != m_cell) {
					gc.getMouseHandler().mousePressed(e);
				}
				else {
					if (m_compsrc.isSelectionTool()) {
						gc.setSelected(true);
						Component comp = gc.getBeanDelegate();
						if (comp != null && e.getClickCount() == 1) {
							setDragSource(this);
						}
						if (e.getClickCount() > 1) {
							if ((e.getModifiers() & java.awt.event.InputEvent.CTRL_MASK) != 0) {
								editComponentName(gc);
							}
							else {
								tryEditDefaultProperty(gc);
								gc.fireGridCellEvent(new GridCellEvent(GridCellEvent.EDIT_COMPONENT, gc));
							}
						}
					}
					else {
						try {
							ComponentFactory factory = m_compsrc.getComponentFactory();
							if (factory != null) {
								final GridComponent comp = factory.createComponent("", getView());
								if (comp != null) {
									/**
									 * Special case when adding a linked form we
									 * need to check if the target editor does
									 * not already contain that linked form
									 */
									if (comp instanceof FormComponent) {
										FormComponent fc = (FormComponent) comp;
										if (fc.isLinked()) {
											if (FormManagerDesignUtils.containsForm(editor.getFormComponent(), fc.getId())) {
												String msg = I18N.format("Only one instance of a linked form allowed per view.", fc.getId());
												String title = I18N.getLocalizedMessage("Error");
												JOptionPane.showMessageDialog(editor, msg, title, JOptionPane.ERROR_MESSAGE);
												m_compsrc.setSelectionTool();
												return;
											}
										}
									}

									AddComponentCommand cmd = new AddComponentCommand(getView().getParentForm(), comp, gc.getConstraints());
									CommandUtils.invoke(cmd, editor);

									if (!e.isControlDown())
										m_compsrc.setSelectionTool();

									GridView view = getView();
									if (view != null)
										view.deselectAll();

									javax.swing.SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											comp.setSelected(true);
										}
									});
								}
							}
						} catch (Exception e1) {
							m_compsrc.setSelectionTool();
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (containsMouse(e) && isDragging()) {
			FormEditor editor = FormEditor.getEditor(getView());
			GridComponent gc = getComponent(e);
			if (gc != null && editor != null) {
				/**
				 * If the component is not the component associated with this
				 * handler, let's just forward the call to that component. The
				 * reason is the component might span multiple cells or it might
				 * be a child form.
				 */
				if (gc != m_cell) {
					if (getDragSource() != this) {
						gc.getMouseHandler().mouseReleased(e);
					}
				}
				else {
					if (getDragSource() != this) {
						GridComponent destComp = m_cell;
						FormComponent destForm = getView().getParentForm();
						GridComponent srcComp = getDragSource().getGridComponent();
						FormComponent srcForm = FormComponent.getParentForm(srcComp);
						MoveComponentCommand cmd = new MoveComponentCommand(destForm, destComp, srcForm, srcComp, m_compsrc);
						CommandUtils.invoke(cmd, editor);
						m_compsrc.setSelectionTool();
						srcComp.setSelected(true);
						/**
						 * we need to do this in case the user tried to drag a
						 * top-level form from inside a JTabbedPane which is not
						 * allowed
						 */
						destComp.setSelected(false);
					}
				}
			}
		}
	}

	/** MouseMotionListener implemenation */
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		// System.out.println( "StandMouseDragged..." );
	}

	private void editComponentName(GridComponent gc) {
		Component comp = gc.getBeanDelegate();
		if (comp != null) {
			String oldname = comp.getName();
			String newvalue = javax.swing.JOptionPane.showInputDialog(comp, I18N.getLocalizedMessage("Set Name"), oldname);
			if (newvalue != null) {
				comp.setName(newvalue);
				gc.fireGridCellEvent(new GridCellEvent(GridCellEvent.EDIT_COMPONENT, gc, GridCellEvent.COMPONENT_NAME_CHANGED));
			}
		}

	}

	/**
	 * Checks if the component has a getText and setText property. If so,
	 * lauches a JOptionPane to allow the user to change the property in place.
	 */
	private void tryEditDefaultProperty(GridComponent gc) {
		EditTextPropertyCommand.tryEditTextProperty(FormEditor.getEditor(gc), FormComponent.getParentForm(gc), gc);
	}

}
