/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.gui.form;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.handler.CellKeyboardHandler;
import com.jeta.forms.gui.handler.CellMouseHandler;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.StateRequest;

/**
 * This class is used to contain a JETABean in a cell on a form. It maintains
 * information about its cell such as row/column location as well as other cell
 * constraints.
 * 
 * A GridComponent also defines methods (using memento pattern) for serializing
 * itself to and from a persistent store. We don't use the standard Java
 * serialization because the Sun does not guarantee compatibility with future
 * versions of Java, and we need more fine-grained control of the serialization
 * process.
 * 
 * A GridComponent maintains a reference to keyboard and mouse handlers needed
 * during design mode. Different GridComonent types will have different types of
 * handlers.
 * 
 * @author Jeff Tassin
 */
abstract public class GridComponent extends JPanel {
	/**
	 * This is a wrapper around the actual JavaBean. It can also contain custom
	 * properties and design info about that bean.
	 */
	private JETABean m_jetabean;

	/**
	 * Flag that indicates if this component is currently selected in the
	 * designer.
	 */
	private boolean m_selected;

	/**
	 * Mouse and keyboard handlers used only by the designer.
	 */
	private CellMouseHandler m_mousehandler;
	private CellKeyboardHandler m_keyboardhandler;

	/** The parent that contains this component */
	private GridView m_gridview;

	/**
	 * The cell constraints for this component.
	 */
	private ComponentConstraints m_cc;

	/**
	 * A list of GridCellListener objects. These objects get notified when this
	 * component changes.
	 */
	private LinkedList m_listeners = new LinkedList();

	/**
	 * Flag that indicates if we are running in design mode or not. We use this
	 * to set the minimum size
	 */
	private boolean m_design_mode;

	/**
	 * Used when in design mode to prevent the bean from becoming too small too
	 * select with mouse.
	 */
	private static final int MIN_WIDTH = 16;
	private static final int MIN_HEIGHT = 16;

	private Dimension m_min_size = new Dimension(MIN_WIDTH, MIN_HEIGHT);

	/**
	 * Empty cell dimension
	 */
	static final int EMPTY_CELL_WIDTH = 10;
	static final int EMPTY_CELL_HEIGHT = 10;

	/**
	 * Creates an uninitialized <code>GridComponent</code> instance.
	 */
	public GridComponent() {
		m_design_mode = FormUtils.isDesignMode();
		setOpaque(false);
	}

	/**
	 * Creates a <code>GridComponent</code> instance with the specified
	 * GridView parent.
	 * 
	 * @param parentView
	 *            the GridView that contains this component.
	 */
	public GridComponent(GridView parentView) {
		m_design_mode = FormUtils.isDesignMode();
		setOpaque(false);
	}

	/**
	 * Creates a <code>GridComponent</code> instance with the specified
	 * GridView parent and JETABean.
	 * 
	 * @param jbean
	 *            the underyling JETABean. This object contains the Java Bean.
	 * @param parent
	 *            the GridView that contains this component.
	 */
	public GridComponent(JETABean jbean, GridView parent) {
		m_jetabean = jbean;
		m_gridview = parent;
		m_design_mode = FormUtils.isDesignMode();
		setOpaque(false);
	}

	/**
	 * Adds a listener that is interested in GridCellEvents from this component.
	 * This is only needed in design mode.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addListener(GridCellListener listener) {
		if (!m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	/**
	 * Notifies all GridCellListeners with the specified event. This is only
	 * used in design mode.
	 * 
	 * @param evt
	 *            the event to send to all registered listeners
	 */
	public void fireGridCellEvent(GridCellEvent evt) {
		Iterator iter = m_listeners.iterator();
		while (iter.hasNext()) {
			GridCellListener listener = (GridCellListener) iter.next();
			listener.cellChanged(evt);
		}
	}

	/**
	 * Returns the underlying JETABean. The JETABean is a container for the
	 * actual Java Bean.
	 * 
	 * @return the JETABean component.
	 */
	public JETABean getBean() {
		return m_jetabean;
	}

	/**
	 * Returns the component that is directly contained by the JETABean.
	 * Normally, this would be the Java bean. However, in a few cases, the
	 * JETABean does not directly contain the Java Bean. Scrollpanes are an
	 * example of this.
	 * 
	 * @return the JETABean child component.
	 */
	Component getBeanChildComponent() {
		if (m_jetabean != null) {
			return m_jetabean.getBeanChildComponent();
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the actual Java Bean that is on the form.
	 * 
	 * @return the Java Bean that is contained by the JETABean.
	 */
	public Component getBeanDelegate() {
		if (m_jetabean != null) {
			return m_jetabean.getDelegate();
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the component constraints for this component. Component
	 * constraints include the column, row, column span, and row span
	 * assignments. They also include any alignment and inset values for this
	 * component.
	 * 
	 * @return the component constraints associated with this component.
	 */
	public ComponentConstraints getConstraints() {
		if (m_cc == null)
			m_cc = new GridComponentConstraints(this);

		return m_cc;
	}

	/**
	 * @return the minimum size for this component. When in design mode, we set
	 *         the min size to something like 4x4 to prevent the grid cells in
	 *         the GridView from being too small.
	 */
	public Dimension getMinimumSize() {
		if (m_design_mode) {
			return m_min_size;
		}
		else {
			return super.getMinimumSize();
		}
	}

	/**
	 * Returns the preferred size for this component. When in design mode, we
	 * set the preferred size to some non-zero value to prevent the grid cells
	 * in the GridView from being too small.
	 * 
	 * @return the preferred size for this component.
	 */
	public Dimension getPreferredSize() {
		if (m_design_mode) {
			Dimension d = super.getPreferredSize();
			if (d == null || d.width < MIN_WIDTH || d.height < MIN_HEIGHT) {
				if (d == null)
					d = m_min_size;

				m_min_size.width = Math.max(d.width, MIN_WIDTH);
				m_min_size.height = Math.max(d.height, MIN_HEIGHT);
				return m_min_size;
			}
			else
				return d;
		}
		else {
			Dimension d = super.getPreferredSize();
			return d;
		}
	}

	/**
	 * Returns the name property of the underlying Java bean.
	 * 
	 * @return the name of the bean component
	 */
	public String getBeanName() {
		if (m_jetabean == null)
			return null;
		else
			return m_jetabean.getName();
	}

	/**
	 * Returns the GridView that contains this component. This is not the same
	 * as this component's immediate parent.
	 * 
	 * @return the view that contains this component
	 */
	public GridView getParentView() {
		java.awt.Component parent = getParent();
		if (parent instanceof GridView)
			return (GridView) parent;

		if (parent != null) {
			parent = parent.getParent();
			if (parent instanceof GridView)
				return (GridView) parent;

			if (parent != null) {
				parent = parent.getParent();
				if (parent instanceof GridView)
					return (GridView) parent;
			}
		}
		return null;
	}

	/**
	 * Returns the row that contains this component.
	 */
	public int getRow() {
		if (getParentView() == null)
			return 1;
		else
			return getConstraints().getRow();
	}

	/**
	 * Returns the number of rows occupied by this component. This is normally 1
	 * unless the row span was changed in the designer.
	 */
	public int getRowSpan() {
		if (getParentView() == null)
			return 1;
		else
			return getConstraints().getRowSpan();
	}

	/**
	 * Returns the number of columns occupied by this component. This is
	 * normally 1 unless the column span was changed in the designer.
	 */
	public int getColumnSpan() {
		if (getParentView() == null)
			return 1;
		else
			return getConstraints().getColumnSpan();
	}

	/**
	 * Returns the column that contains this component.
	 */
	public int getColumn() {
		if (getParentView() == null)
			return 1;
		else
			return getConstraints().getColumn();
	}

	/**
	 * Returns the mouse handler for this component. This is only set and used
	 * by the designer.
	 */
	public CellMouseHandler getMouseHandler() {
		return m_mousehandler;
	}

	/**
	 * Returns the total width in pixels of the cells occupied by this component
	 */
	public int getCellWidth() {
		int colspan = getColumnSpan();
		int col = getColumn();

		int width = getParentView().getColumnWidth(col);
		for (int index = 1; index < colspan; index++)
			width += getParentView().getColumnWidth(col + index);
		return width;
	}

	/**
	 * Returns the total height in pixels of the cells occupied by this
	 * component
	 */
	public int getCellHeight() {
		int rowspan = getRowSpan();
		int row = getRow();

		int height = getParentView().getRowHeight(row);
		for (int index = 1; index < rowspan; index++)
			height += getParentView().getRowHeight(row + index);
		return height;
	}

	/**
	 * Returns the left origin (in parent coordinates) of the fist cell occupied
	 * by this component.
	 * 
	 * @return the left location of this cell in the parent coordinates.
	 */
	public int getCellX() {
		return getParentView().getColumnOrgX(getColumn());
	}

	/**
	 * Returns the top origin (in parent coordinates) of the fist cell occupied
	 * by this component.
	 * 
	 * @return the top location of this cell in the parent coordinates.
	 */
	public int getCellY() {
		return getParentView().getRowOrgY(getRow());
	}

	/**
	 * Returns the id of this component. This is only valid for form components.
	 * 
	 * @return the id of this component.
	 */
	public String getId() {
		return getBeanName();
	}

	/**
	 * Returns true if this component is currently selected. This is used only
	 * in design mode.
	 */
	public boolean isSelected() {
		return m_selected;
	}

	/**
	 * Removes the previously registered grid cell listener.
	 */
	public void removeListener(GridCellListener listener) {
		m_listeners.remove(listener);
	}

	/** Sets the mouse handler for this component */
	public void setMouseHandler(CellMouseHandler handler) {
		m_mousehandler = handler;
	}

	/**
	 * This should never be called. Overridden here so we can assert when
	 * debugging.
	 */
	public void setName(String name) {
		FormUtils.safeAssert(false);
	}

	/**
	 * Sets the JETABean associated with this component.
	 */
	protected void setBean(JETABean jbean) {
		m_jetabean = jbean;
	}

	/**
	 * Sets the selected flag for this component.
	 */
	public void setSelected(boolean sel) {
		boolean old_sel = m_selected;
		m_selected = sel;
		if (sel != old_sel) {
			if (getParentView() == null) {

			}
			else {
				getParentView().getOverlay().repaint(this);
			}
		}
		if (sel) {
			fireGridCellEvent(new GridCellEvent(GridCellEvent.CELL_SELECTED, this));
		}
	}

	/**
	 * Returns the keyboard handler associated with this component. Handlers are
	 * set only during design mode.
	 */
	public CellKeyboardHandler getKeyboardHandler() {
		return m_keyboardhandler;
	}

	/**
	 * Sets the keyboard handler associated with this component. Handlers are
	 * set only during design mode.
	 */
	public void setKeyboardHandler(CellKeyboardHandler handler) {
		m_keyboardhandler = handler;
	}

	/**
	 * Sets the GridView that contains this component. This is not the same as
	 * the immediate parent of this component.
	 * 
	 * @return the view that contains this component
	 */
	public void setParentView(GridView view) {
		FormUtils.safeAssert(getParentView() == null);
		m_gridview = view;
	}

	/**
	 * PostInitialize is called once after all components in a form have been
	 * re-instantiated and the state has been set at runtime (not design time).
	 * This gives each property and component a chance to do some last minute
	 * initializations that might depend on the top level parent.
	 */
	public void postInitialize(FormPanel panel) {
		// no op
	}

	/**
	 * Returns the internal state of this component as a memento. This includes
	 * the cell constraints as well as the properties for the underlying Java
	 * bean.
	 * 
	 * @param si
	 *            a request object that controls how much information should be
	 *            stored in the memento.
	 * @returns the state of this component which can be persisted.
	 */
	public abstract ComponentMemento getState(StateRequest si) throws FormException;

	/**
	 * Sets the state of this component from a previously stored state
	 * 
	 * @param memento
	 *            the state information to set.
	 */
	public abstract void setState(ComponentMemento memento) throws FormException;

	/**
	 * Returns true if this component contains a java bean. In design mode empty
	 * cells on the form will not contain a bean but will contain a valid
	 * GridComponent
	 * 
	 * @return true if this component contains java bean
	 */
	public boolean hasBean() {
		if (m_jetabean != null) {
			return (m_jetabean.getDelegate() != null);
		}
		else {
			return false;
		}
	}

	/**
	 * Returns true if this component has a nonzero width and height. This can
	 * occur in some cases in the designer. If we have non visible component, we
	 * show an icon so the user has some indication that it exists.
	 * 
	 * @return true if this component is large enough to be visible
	 */
	public boolean isShowing() {
		if (m_jetabean == null) {
			return true;
		}
		else {
			Component delegate = m_jetabean.getDelegate();
			if (delegate == null) {
				return true;
			}
			else {
				Dimension d = delegate.getSize();
				if (d == null || d.width == 0 || d.height == 0) {
					return false;
				}
				else {
					return true;
				}
			}
		}
	}

	/**
	 * Print for debugging
	 */
	public abstract void print();

}
