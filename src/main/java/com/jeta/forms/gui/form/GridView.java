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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormSpecAdapter;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.EmptyComponentFactory;
import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.FormGroupSet;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.forms.store.support.Matrix;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The <code>GridViewBase</code> is responsible for containing the parent
 * container that contains the components on the form as well as rendering the
 * grid lines during design mode as well as any background and fill effects for
 * grid cells. The GridViewBase employs a JLayeredPane to separate the various
 * elements of the view.
 * 
 * <pre>
 * Containment Hierarchy:
 * 
 *  GridViewBase(this)
 *      |
 *       -- JLayeredPane (m_layered_pane)
 *              |
 *               -- GridViewBase.FormContainer (m_form &lt;-- FormLayout )
 *              |        |
 *              |         -- GridComponent1
 *              |        |
 *              |         -- GridComponent2
 *              |        |
 *              |         -- GridComponentN
 *              |
 *               -- CellPainter (m_cell_painter)
 *              |
 *               -- BackgroundPainter ( m_background_painter - bottom layer )
 * </pre>
 * 
 * @author Jeff Tassin
 */
public class GridView extends JETAPanel implements Paintable, FormAccessor, GridCellListener {
	/**
	 * The JGoodies FormLayout for this view.
	 */
	private FormLayout m_formlayout;

	/**
	 * The container that contains the GridComponents. This is also the
	 * container whose layout manager is m_formlayout.
	 */
	private FormContainer m_form;

	/**
	 * The layered pane for this view [grid overlay] top [ form ] [ cell painter ]
	 * [background effects] bottom
	 */
	private JLayeredPane m_layered_pane;

	/**
	 * Paints the background effects for a form (i.e. texture, gradient, solid
	 * fill, image )
	 */
	private BackgroundPainter m_background_painter;

	/**
	 * Maintains a cache of row/column assignments for the GridComponents in
	 * this view. This allows us to quickly lookup a component based on the
	 * row/column position. Otherwise, we would need to do a linear lookup by
	 * iterating over all components in the view. This would be too inefficient
	 * when repainting.
	 */
	private CellAssignmentCache m_assignment_cache = new CellAssignmentCache(this);

	/**
	 * The object that renders the grid lines.
	 */
	private GridOverlay m_overlay;

	/**
	 * A cached LayoutInfo object from the FormLayout. This is needed because
	 * the FormLayout returns a copy. This makes painting very slow, so we need
	 * to cache it here.
	 */
	private FormLayout.LayoutInfo m_layoutinfo = null;

	/**
	 * Paints effects for individual cells in the grid.
	 */
	private CellPainter m_cell_painter;

	/**
	 * A matrix of PaintProperty objects that define the fill effects for
	 * individual cells in the grid. The matrix cells correspond to the cells in
	 * this view's grid. If a PaintProperty is found in the matrix at a given
	 * column/row, then it is used to fill the corresponding cell in this view.
	 */
	private Matrix m_cell_painters = new Matrix(0, 0);

	/**
	 * Defines the the column groups for this form See:
	 * {@link com.jgoodies.forms.layout.FormLayout#.setColumnGroups}
	 */
	private FormGroupSet m_col_groups = new FormGroupSet();

	/**
	 * Defines the row groups for this form. See:
	 * {@link com.jgoodies.forms.layout.FormLayout#.setRowGroups}
	 */
	private FormGroupSet m_row_groups = new FormGroupSet();

	/**
	 * A timestamp of the last time a nested form in this container was
	 * modified. This is primarily used to implement the fail-fast FormIterator
	 */
	private long m_nested_mod_stamp = -1;

	/**
	 * Flag that indicates if GridViewEvents should be fired.
	 */
	private boolean m_events_enabled = false;

	/**
	 * A list of GridViewListener objects that are interested in GridViewEvents
	 * from this object.
	 */
	private LinkedList m_listeners = new LinkedList();

	/**
	 * The various layers in this view
	 */
	public static final Integer BACKGROUND_PAINTER_LAYER = new Integer(0);
	public static final Integer CELL_PAINTER_LAYER = new Integer(1);
	public static final Integer FORM_LAYER = new Integer(2);
	public static final Integer OVERLAY_LAYER = new Integer(9);
	public static final Integer FOCUS_LAYER = new Integer(10);

	/**
	 * Creates a <code>GridView</code> with no rows and columns
	 */
	public GridView() {
		setOpaque(true);
	}

	/**
	 * Creates a <code>GridView</code> with the specified column and row
	 * specifications. The view is initialized which also creates the columns
	 * and rows.
	 * 
	 * @param colspecs
	 *            the encoded column specs (comma delimited) for the form layout
	 * @param rowspecs
	 *            the encoded row specs (comma delimited) for the form layout
	 */
	public GridView(String colspecs, String rowspecs) {
		initialize(colspecs, rowspecs);
	}

	/**
	 * Creates a <code>GridView</code> with the specified columns and rows.
	 * The default column and row specifications are applied.
	 * 
	 * @param cols
	 *            the number of columns to create
	 * @param row
	 *            the number of rows to create.
	 */
	public GridView(int cols, int rows) {
		initialize(cols, rows);
	}

	/**
	 * Adds the component at the given row and column. If a component already
	 * occupies cells specified by the constraints, it is not overwritten, and
	 * the call will still succeed.
	 * 
	 * @param gc
	 *            the grid component to add to this view.
	 * @param cc
	 *            the cell constraints to apply to the component.
	 */
	public void addComponent(GridComponent gc, CellConstraints cc) {
		gc.addListener(this);
		m_form.add(gc, cc);
		m_assignment_cache.addComponent(gc);

		m_form.revalidate();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.CELL_CHANGED));
		unitTest();
	}

	/**
	 * Adds the component at the given row and column. If a component already
	 * occupies cells specified by the constraints, it is not overwritten, and
	 * the call will still succeed.
	 * 
	 * @param gc
	 *            the grid component to add to this view
	 * @param cc
	 *            the component constraints to apply to the component.
	 */
	public void addComponent(GridComponent gc, ComponentConstraints cc) {
		addComponent(gc, cc.createCellConstraints());
	}

	/**
	 * Adds a listener that wants to receive GridViewEvents from this view
	 * 
	 * @param listener
	 *            the listener to add to this view.
	 */
	public void addListener(GridViewListener listener) {
		if (listener != null && !m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	/**
	 * cache the last component that was selected. This is needed for
	 * performance.
	 */
	private GridComponent m_last_comp;

	/**
	 * GridCellListener implementation. Here we are getting events from
	 * individual GridComponents owned by this view. Most events are simply
	 * forwarded up the chain as GridViewEvents.
	 */
	public void cellChanged(GridCellEvent evt) {
		if (evt.getId() == GridCellEvent.EDIT_COMPONENT) {
			fireGridEvent(new GridViewEvent(this, GridViewEvent.EDIT_COMPONENT, evt));
		}
		else if (evt.getId() == GridCellEvent.CELL_SELECTED) {
			fireGridEvent(new GridViewEvent(this, GridViewEvent.CELL_SELECTED, evt));
			m_last_comp = (GridComponent) evt.getSource();
		}
		else {
			fireGridEvent(new GridViewEvent(this, GridViewEvent.CELL_CHANGED, evt));
		}
	}

	/**
	 * Deselects all grid components in the view
	 */
	public void deselectAll() {
		if (m_last_comp != null)
			m_last_comp.setSelected(false);
	}

	/**
	 * Override so we can explicitly tell the form to layout as well.
	 */
	public void doLayout() {
		super.doLayout();
		m_form.doLayout();
	}

	/**
	 * Sets the flag that indicates if events should be fired.
	 * 
	 * @param enable
	 *            set to true if GridViewEvents should be fired when this view
	 *            is changed.
	 */
	public void enableEvents(boolean enable) {
		m_events_enabled = enable;
	}

	/**
	 * Sends a GridViewEvent to all registered listeners. A GridViewEvent is
	 * fired when the view changes or one of its child GridComponents changes.
	 * This is only needed by the designer.
	 * 
	 * @param evt
	 *            the event to post to all registered listeners.
	 */
	public void fireGridEvent(GridViewEvent evt) {
		if (isEventsEnabled()) {
			if (evt.getId() != GridViewEvent.EDIT_COMPONENT && evt.getId() != GridViewEvent.CELL_SELECTED) {
				refreshView();
			}

			m_layoutinfo = null;
			Iterator iter = m_listeners.iterator();
			while (iter.hasNext()) {
				try {
					GridViewListener listener = (GridViewListener) iter.next();
					listener.gridChanged(evt);
				} catch (Exception e) {
					FormsLogger.debug(e);
				}
			}
		}
	}

	/**
	 * Helper method that creates empty components. During design mode, a grid
	 * is filled with empty components to provide a minimum space for each cell.
	 * Without these components, the grid cells could have a zero width or
	 * height when the size is set to <i>Preferred</i> for either the column
	 * and/or row.
	 * 
	 * @param view
	 *            the view to be initialized
	 * @param compsrc
	 *            the component source that is used to help create empty
	 *            component objects.
	 */
	public static void fillCells(GridView view, ComponentSource compsrc) {
		try {
			for (int col = 1; col <= view.getColumnCount(); col++) {
				for (int row = 1; row <= view.getRowCount(); row++) {
					EmptyComponentFactory factory = new EmptyComponentFactory(compsrc);
					StandardComponent gc = (StandardComponent) factory.createComponent("empty", view);
					view.addComponent(gc, new ReadOnlyConstraints(col, row));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the cell bounds at the given column and row.
	 * 
	 * @param col
	 *            the 1-based column
	 * @param row
	 *            the 1-based row
	 * @return the cell bounds at the given row/column.
	 */
	public Rectangle getCellBounds(int col, int row) {
		try {
			int org_x = getColumnOrgX(col);
			int org_y = getRowOrgY(row);
			int width = getColumnWidth(col);
			int height = getRowHeight(col);
			return new Rectangle(org_x, org_y, width, height);
		} catch (Exception e) {
			System.out.println("GridView.getCellBounds exception:  col: " + col + "  row: " + row);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a Matrix object that contains the cell painter properties
	 * (PaintProperty) for cells that have fill effects.
	 * 
	 * @return A matrix of cell painters
	 */
	public Matrix getCellPainters() {
		FormUtils.safeAssert(m_cell_painters.getRowCount() == getRowCount());
		FormUtils.safeAssert(m_cell_painters.getColumnCount() == getColumnCount());
		return m_cell_painters;
	}

	/**
	 * Returns the number of columns in this view.
	 * 
	 * @return the number of columns in this view
	 */
	public int getColumnCount() {
		return m_formlayout.getColumnCount();
	}

	/**
	 * Returns the left location in pixels of the given column.
	 * 
	 * @param col
	 *            the 1-based column
	 * @return the column x-origin for the given column.
	 */
	public int getColumnOrgX(int col) {
		col--;
		FormLayout.LayoutInfo linfo = getLayoutInfo();
		return linfo.columnOrigins[col];
	}

	/**
	 * Returns a FormGroupSet that specifies the column groups for this view.
	 * 
	 * @return the column groups for this view
	 */
	public FormGroupSet getColumnGroups() {
		return m_col_groups;
	}

	/**
	 * Return a encoded string of all column specs for this view. Each column
	 * spec is separated by a comma.
	 * 
	 * @return the encoded ColumnSpecs for all rows in the view
	 */
	public String getColumnSpecs() {
		StringBuffer sbuff = new StringBuffer();
		for (int col = 1; col <= getColumnCount(); col++) {
			ColumnSpec cs = getColumnSpec(col);
			if (col > 1)
				sbuff.append(",");

			String colspec = cs.toShortString();
			sbuff.append(FormSpecAdapter.fixup(colspec));
		}

		return sbuff.toString();
	}

	/**
	 * Returns the column width in pixels of the given column.
	 * 
	 * @param col
	 *            the 1-based column
	 * @return the width for the given column.
	 */
	public int getColumnWidth(int col) {
		col--;
		FormLayout.LayoutInfo linfo = getLayoutInfo();
		return linfo.columnOrigins[col + 1] - linfo.columnOrigins[col];
	}

	/**
	 * Returns the ColumnSpec associated with the given column.
	 * 
	 * @param col
	 *            the 1-based column
	 * @return the ColumnSpec for the given column
	 */
	public ColumnSpec getColumnSpec(int col) {
		return m_formlayout.getColumnSpec(col);
	}

	/**
	 * Returns the FormAccessor associated with this view. A FormAccessor is
	 * used to add/remove/iterate components on a form.
	 */
	public FormAccessor getFormAccessor() {
		return this;
	}

	/**
	 * Do not call this method. It is used only for testing. Call iterator() if
	 * you need to list the components in the view.
	 * 
	 * @deprecated
	 */
	public Container getFormContainer() {
		return m_form;
	}

	/**
	 * Returns the name assigned to the form component associated with this
	 * accessor.
	 * 
	 * @return the name assigned to the form component associated with this
	 *         accessor.
	 */
	public String getFormName() {
		return getName();
	}

	/**
	 * Returns the timestamp of the last time a child component was added or
	 * removed from the form associated with this view. This is primarily used
	 * to implement the fail-fast FormIterator
	 * 
	 * @return the timestamp of the last time this container was modified.
	 */
	long getModificationStamp() {
		return m_form.getModificationStamp();
	}

	/**
	 * Returns the timestamp of the last time a child component was added or
	 * removed from a nested form associated with this view. This is primarily
	 * used to implement the fail-fast FormIterator
	 * 
	 * @return the timestamp of the last time a nested form in this view was
	 *         modified.
	 */
	long getNestedModificationStamp() {
		return m_nested_mod_stamp;
	}

	/**
	 * Returns the number of rows in this view.
	 * 
	 * @return the number of rows in this view
	 */
	public int getRowCount() {
		return m_formlayout.getRowCount();
	}

	/**
	 * Return the layout manager (FormLayout) for the form.
	 * 
	 * @return the underlying form layout object.
	 */
	public FormLayout getFormLayout() {
		return m_formlayout;
	}

	/**
	 * Return the total width of the form as calculated by the FormLayout
	 * manager.
	 * 
	 * @return the width of the form as determined by the FormLayout manager
	 */
	public int getFormWidth() {
		FormLayout.LayoutInfo linfo = getLayoutInfo();
		return linfo.getWidth();
	}

	/**
	 * Return the total height of the form as calculated by the FormLayout
	 * manager.
	 * 
	 * @return the height of the form as determined by the FormLayout manager
	 */
	public int getFormHeight() {
		FormLayout.LayoutInfo linfo = getLayoutInfo();
		return linfo.getHeight();
	}

	/**
	 * Returns the GridComponent that occupies the cell at the given row/column.
	 * Null is returned if a component does not occupy the cell.
	 * 
	 * @return the grid component that occupies the cell at the given row/column
	 */
	public GridComponent getGridComponent(int col, int row) {
		return m_assignment_cache.getGridComponent(col, row);
	}

	/**
	 * Returns the total number of GridComponent objects contained by this form.
	 * 
	 * @return the number of grid components in this view
	 */
	public int getGridComponentCount() {
		return m_form.getComponentCount();
	}

	/**
	 * Returns the GridComponent at the given index in the parent container.
	 * 
	 * @param index
	 *            the 0-based index in the parent container.
	 * @return the grid component at the given index.
	 */
	public GridComponent getGridComponent(int index) {
		Object obj = m_form.getComponent(index);
		if (obj instanceof GridComponent)
			return (GridComponent) obj;
		else
			return null;
	}

	/**
	 * Return the FormLayout.LayoutInfo.
	 * 
	 * @return the FormLayout.LayoutInfo. See:
	 *         {@link com.jgoodies.forms.layout.FormLayout.LayoutInfo }
	 */
	FormLayout.LayoutInfo getLayoutInfo() {
		/**
		 * We need to cache this object because the FormLayout makes a copy
		 * everytime getLayoutInfo is called. This slows down the system when
		 * repainting or dragging the mouse.
		 */
		if (m_layoutinfo == null)
			m_layoutinfo = m_formlayout.getLayoutInfo(m_form);

		return m_layoutinfo;
	}

	/**
	 * Return the fill property for the cell at the given column and row.
	 * 
	 * @param col
	 *            the 1-based column
	 * @param row
	 *            the 1-based row
	 * @return the painter at the given column and row. Null is returned if no
	 *         painter exists for that cell.
	 */
	public PaintProperty getPaintProperty(int col, int row) {
		Object obj = m_cell_painters.getValue(row - 1, col - 1);
		if (obj instanceof PaintProperty) {
			PaintProperty pp = (PaintProperty) obj;
			if (pp != null)
				return pp;
			else
				return null;
		}
		else {
			if (obj != null) {
				System.out.println("GridView.getPaintProperty failed  for col: " + col + " row: " + row + "  prop: " + obj.getClass());
			}
			return null;
		}
	}

	/**
	 * Returns the FormComponent that contains this GridView. Since the parent
	 * of this view is not a FormComponent, the component heirarhcy must be
	 * traversed until the first FormComponent instance is found.
	 * 
	 * @return the FormComponent that contains this view.
	 */
	public FormComponent getParentForm() {
		/**
		 * the first parent should be a JETABean. The grandparent is actually
		 * the FormComponent.
		 */
		Component cc = getParent();
		while (cc != null && !(cc instanceof java.awt.Window)) {
			if (cc instanceof FormComponent) {
				FormComponent fc = (FormComponent) cc;
				FormUtils.safeAssert(fc.getBean().getDelegate() == this);
				return fc;
			}
			cc = cc.getParent();
		}
		FormUtils.safeAssert(false);
		return null;
	}

	/**
	 * Returns the row height in pixels of the given row.
	 * 
	 * @param row
	 *            the 1-based row
	 * @return the row height for the given row.
	 */
	public int getRowHeight(int row) {
		row--;
		FormLayout.LayoutInfo linfo = getLayoutInfo();
		return linfo.rowOrigins[row + 1] - linfo.rowOrigins[row];
	}

	/**
	 * Returns the row origin in pixels of the given row.
	 * 
	 * @param row
	 *            the 1-based row
	 * @return the row y-origin for the given row.
	 */
	public int getRowOrgY(int row) {
		row--;
		FormLayout.LayoutInfo linfo = getLayoutInfo();
		return linfo.rowOrigins[row];
	}

	/**
	 * Returns the GridComponent that overlaps the cell at the given column and
	 * row.
	 * 
	 * @return the component that overlaps the cell at the given column and row.
	 *         If no component overlaps, then null is returned.
	 */
	public GridComponent getOverlappingComponent(int col, int row) {
		return m_assignment_cache.getOverlappingComponent(col, row);
	}

	/**
	 * Returns the GridOverlay associated with this view. The GridOverlay is
	 * responsible for rendering the grid lines when in design mode.
	 * 
	 * @return the GridOverlay associated with this view.
	 */
	public GridOverlay getOverlay() {
		return m_overlay;
	}

	/**
	 * Returns a FormGroupSet that specifies the row groups for this view.
	 * 
	 * @return the row groups for this view
	 */
	public FormGroupSet getRowGroups() {
		return m_row_groups;
	}

	/**
	 * Returns the row spec for the specified row.
	 * 
	 * @param row
	 *            the 1-based row
	 * @return the RowSpec for the given row.
	 */
	public RowSpec getRowSpec(int row) {
		FormUtils.safeAssert(row >= 1 && row <= getRowCount());
		return m_formlayout.getRowSpec(row);
	}

	/**
	 * Return a encoded string of all row specs for this view. Each orow spec is
	 * separated by a comma.
	 * 
	 * @return the encoded RowSpecs for all rows in the view
	 */
	public String getRowSpecs() {
		StringBuffer sbuff = new StringBuffer();
		for (int row = 1; row <= getRowCount(); row++) {
			RowSpec rs = getRowSpec(row);
			if (row > 1)
				sbuff.append(",");

			String rowspec = rs.toString();
			sbuff.append(FormSpecAdapter.fixup(rowspec));
		}
		return sbuff.toString();
	}

	/**
	 * Returns the first selected GridComponent found in the component hierarhcy
	 * of this container. Components in nested forms are checked as well.
	 * 
	 * @returns the first selected component it finds in the component hierarhcy
	 *          of this container.
	 */
	public GridComponent getSelectedComponent() {
		Iterator iter = gridIterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc instanceof FormComponent) {
				FormComponent cc = (FormComponent) gc;
				gc = cc.getSelectedComponent();
				if (gc != null)
					return gc;
			}
			else if (gc instanceof FormContainerComponent) {
				FormContainerComponent cc = (FormContainerComponent) gc;
				gc = cc.getSelectedComponent();
				if (gc != null)
					return gc;
			}
			else {
				if (gc.isSelected())
					return gc;
			}
		}
		return null;
	}

	/**
	 * Call this only once to initialize to the default settings for a given
	 * number of rows and columns
	 * 
	 * @param cols
	 *            the number of columns in the view
	 * @param rows
	 *            the number of rows in the view
	 */
	public void initialize(int cols, int rows) {
		enableEvents(FormUtils.isDesignMode());
		StringBuffer colspec = new StringBuffer();
		for (int col = 1; col <= cols; col++) {
			if (col > 1)
				colspec.append(",");
			colspec.append("f:d:n");

		}
		StringBuffer rowspec = new StringBuffer();
		for (int row = 1; row <= rows; row++) {
			if (row > 1)
				rowspec.append(",");
			rowspec.append("c:d:n");
		}
		initialize(colspec.toString(), rowspec.toString());
		m_cell_painters = new Matrix(getRowCount(), getColumnCount());
	}

	/**
	 * Initializes the view. This should be called only once.
	 * 
	 * @param colspecs
	 *            the encoded column specs for the form layout
	 * @param rowspecs
	 *            the encoded row specs for the form layout
	 */
	void initialize(String colspecs, String rowspecs) {
		/**
		 * This should not be needed since initialize should only be called
		 * once. Just in case.
		 */
		removeAll();

		/**
		 * We need to handle the case where the FormLayout stores the encoded
		 * string in a different form than it takes in the constructor. For
		 * example, the FormLayout stores dlu sizes as dluX or dluY. However,
		 * the FormSpec constructor cannot handle this form.
		 */
		colspecs = FormSpecAdapter.fixupSpecs(colspecs);
		rowspecs = FormSpecAdapter.fixupSpecs(rowspecs);

		FormUtils.safeAssert(m_formlayout == null);
		setOpaque(true);

		m_formlayout = new FormLayout(colspecs, rowspecs);
		m_form = new FormContainer();
		m_form.setOpaque(false);
		m_form.setLayout(m_formlayout);

		setLayout(new BorderLayout());

		m_layered_pane = new JLayeredPane();
		m_layered_pane.setOpaque(false);
		m_layered_pane.setLayout(new JETALayerLayout());

		m_background_painter = new BackgroundPainter();
		m_cell_painter = new CellPainter(this);

		m_layered_pane.add(m_background_painter, BACKGROUND_PAINTER_LAYER);
		m_layered_pane.add(m_cell_painter, CELL_PAINTER_LAYER);
		m_layered_pane.add(m_form, FORM_LAYER);
		super.add(m_layered_pane, BorderLayout.CENTER);

		if (FormUtils.isDesignMode()) {
			GridOverlayFactory factory = (GridOverlayFactory) JETARegistry.lookup(GridOverlayFactory.COMPONENT_ID);
			if (factory != null) {
				m_overlay = factory.createOverlay(this);
				((JComponent) getFormContainer()).setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 1, 1));
				m_layered_pane.add((JComponent) m_overlay, OVERLAY_LAYER);
			}
			else {
				FormUtils.safeAssert(false);
			}
		}
	}

	/**
	 * Inserts a new column into the view at the given column index.
	 * 
	 * @param col
	 *            the 1-based column index where the new column will be
	 *            inserted.
	 * @param colspec
	 *            the specification for the new column.
	 * @param factory
	 *            a factory used to create components to insert into the empty
	 *            cells of the newly created column. This is generally an
	 *            EmptyComponentFactory.
	 */
	public void insertColumn(int col, ColumnSpec colspec, ComponentFactory factory) throws FormException {
		int old_col_count = getColumnCount();
		/**
		 * first instantiate all components in case an exception is thrown we
		 * don't have an empty column
		 */
		LinkedList comps = new LinkedList();
		for (int row = 1; row <= getRowCount(); row++) {
			GridComponent gc = factory.createComponent("test", this);
			comps.add(gc);
		}

		CellConstraints cc = new CellConstraints();
		FormLayout layout = getFormLayout();

		if (col > getColumnCount()) {
			col = getColumnCount() + 1;
			layout.appendColumn(colspec);
		}
		else
			layout.insertColumn(col, colspec);

		if (FormUtils.isDebug()) {
			Iterator iter = gridIterator();
			while (iter.hasNext()) {
				GridComponent curr_gc = (GridComponent) iter.next();
				if (curr_gc.getColumn() == col) {
					System.out.println("GridView.insertRow failed at: col: " + col + " row: " + curr_gc.getRow() + "  gc: " + curr_gc);
				}
			}
		}

		Iterator iter = comps.iterator();
		int row = 1;
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			addComponent(gc, cc.xy(col, row));
			row++;
		}

		m_cell_painters.insertColumn(col - 1);

		FormUtils.safeAssert(getColumnCount() == (old_col_count + 1));
		FormUtils.safeAssert(getGridComponentCount() == getColumnCount() * getRowCount());

		updateColumnGroups();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.COLUMN_ADDED));
	}

	/**
	 * Inserts a new row into the view at the given column index.
	 * 
	 * @param row
	 *            the 1-based row index where the new row will be inserted.
	 * @param rowspec
	 *            the specification for the new row.
	 * @param factory
	 *            a factory used to create components to insert into the empty
	 *            cells of the newly created row. This is generally an
	 *            EmptyComponentFactory.
	 */
	public void insertRow(int row, RowSpec rowspec, ComponentFactory factory) throws FormException {
		/**
		 * first instantiate all components in case an exception is thrown we
		 * don't have an empty row
		 */
		int old_row_count = getRowCount();
		LinkedList comps = new LinkedList();
		for (int col = 1; col <= getColumnCount(); col++) {
			GridComponent gc = factory.createComponent("", this);
			comps.add(gc);
		}

		CellConstraints cc = new CellConstraints();
		FormLayout layout = getFormLayout();
		if (row > getRowCount()) {
			row = getRowCount() + 1;
			layout.appendRow(rowspec);
		}
		else
			layout.insertRow(row, rowspec);

		if (FormUtils.isDebug()) {
			Iterator iter = gridIterator();
			while (iter.hasNext()) {
				GridComponent curr_gc = (GridComponent) iter.next();
				if (curr_gc.getRow() == row) {
					System.out.println("GridView.insertRow failed at: col: " + curr_gc.getColumn() + " row: " + row + "  gc: " + curr_gc);
				}
			}
		}

		Iterator iter = comps.iterator();
		int col = 1;
		while (iter.hasNext()) {
			// @todo provide a better naming strategy
			GridComponent gc = (GridComponent) iter.next();
			addComponent(gc, cc.xy(col, row));
			col++;
		}

		m_cell_painters.insertRow(row - 1);

		FormUtils.safeAssert(getRowCount() == (old_row_count + 1));
		FormUtils.safeAssert(getGridComponentCount() == getColumnCount() * getRowCount());
		updateRowGroups();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.ROW_ADDED));
	}

	/**
	 * Return true if GridViewEvents will be fired from the view.
	 * 
	 * @return the flag that indicates if events should be fired.
	 */
	public boolean isEventsEnabled() {
		return m_events_enabled;
	}

	/**
	 * Return true if the grid lines are visible. This is only needed by the
	 * designer.
	 * 
	 * @return the grid overlay as visible/invisible
	 */
	public boolean isGridVisible() {
		return m_overlay.isGridVisible();
	}

	/**
	 * Returns an interator that iterates over the grid components
	 * (GridComponent objects) in this view.
	 * 
	 * @return an interator that iterates over the grid components in this view.
	 */
	public Iterator gridIterator() {
		return new ComponentIterator();
	}

	/**
	 * Creates a collection of all child grid components (GridComponent objects)
	 * in this view. This method only returns GridComponent objects.
	 * 
	 * @return a collection of GridComponents contained by this view.
	 */
	public Collection listComponents() {
		LinkedList list = new LinkedList();
		Iterator iter = gridIterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			FormUtils.safeAssert(obj instanceof GridComponent);
			list.add(obj);
		}
		return list;
	}

	/**
	 * Updates any parameters that need to be updated if the view changed such
	 * as adding/deleting/editing a component, row, or column.
	 */
	public void refreshView() {
		syncCellAssignments();
		revalidate();
		repaint();
	}

	/**
	 * Removes the given column from the layout/view. Note that all components
	 * in the column are removed as well.
	 * 
	 * @param column
	 *            the 1-based index of the column to remove.
	 */
	public void removeColumn(int column) {
		if (column < 1 || column > getColumnCount()) {
			FormUtils.safeAssert(false);
			return;
		}
		m_cell_painters.removeColumn(column - 1);

		LinkedList comps = new LinkedList();
		Iterator iter = gridIterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc.getColumn() == column) {
				comps.add(gc);
			}
		}

		iter = comps.iterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			m_form.remove(gc);
		}
		m_formlayout.removeColumn(column);
		updateColumnGroups();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.COLUMN_DELETED));
	}

	/**
	 * Removes the given listener for the set of listeners that want events from
	 * this view
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(GridViewListener listener) {
		if (listener != null)
			m_listeners.remove(listener);
	}

	/**
	 * Removes the specified row from the layout/view. Note that all components
	 * in the row are removed as well.
	 * 
	 * @param row
	 *            the 1-based row to remove from the view.
	 */
	public void removeRow(int row) {

		if (row < 1 || row > getRowCount()) {
			FormUtils.safeAssert(false);
			return;
		}

		m_cell_painters.removeRow(row - 1);

		LinkedList comps = new LinkedList();
		Iterator iter = gridIterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc.getRow() == row) {
				comps.add(gc);
			}
		}

		iter = comps.iterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			m_form.remove(gc);
		}
		m_formlayout.removeRow(row);
		updateRowGroups();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.ROW_DELETED));
	}

	/**
	 * Replaces a component in the view. Note that components cannot be deleted
	 * in the design view. They must be replaced with 'Empty' components if you
	 * want to simulate an empty cell.
	 */
	public void replaceComponent(GridComponent newComp, GridComponent oldComp) {
		CellConstraints cc = oldComp.getConstraints().createCellConstraints();
		setComponent(newComp, cc);
		fireGridEvent(new GridViewEvent(this, GridViewEvent.CELL_CHANGED));
	}

	/**
	 * Override so we can explicitly tell the child form to revalidate as well.
	 */
	public void revalidate() {
		if (m_form != null) {
			m_form.revalidate();
		}
		super.revalidate();
	}

	/**
	 * Sets an object used to render the fill effect for the entire form
	 * associated with this view.
	 * 
	 * @param p
	 *            the painter to set for this view.
	 */
	public void setBackgroundPainter(Painter p) {
		if (m_background_painter != null)
			m_background_painter.setBackgroundPainter(p);
	}

	/**
	 * Override setBounds so we can reset the layoutinfo.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		m_layoutinfo = null;
	}

	/**
	 * Sets the cell painters associated with this view
	 * 
	 * @param painters
	 *            a matrix of cell painters (PaintProperty objects)
	 */
	public void setCellPainters(Matrix painters) {
		if (painters == null) {
			m_cell_painters = new Matrix(getRowCount(), getColumnCount());
		}
		else {
			m_cell_painters = painters;
			if ((painters.getRowCount() != getRowCount()) || (painters.getColumnCount()) != (getColumnCount()))
				m_cell_painters = new Matrix(getRowCount(), getColumnCount());
		}
	}

	/**
	 * Updates the columnspecrowspec for the given column
	 * 
	 * @param colspec
	 *            the new columnspec
	 * @param col
	 *            the column
	 */
	public void setColumnSpec(int col, ColumnSpec colspec) {
		FormLayout layout = getFormLayout();
		ColumnSpec newspec = new ColumnSpec(colspec.getDefaultAlignment(), colspec.getSize(), colspec.getResizeWeight());
		layout.setColumnSpec(col, newspec);
		m_form.revalidate();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.COLUMN_SPEC_CHANGED));
	}

	/**
	 * Sets the component at the given row and column. Removes any existing
	 * components that occupy cells which will contain the new component.
	 */
	public void setComponent(GridComponent gc, CellConstraints cc) {

		GridComponent oldcomp = getGridComponent(cc.gridX, cc.gridY);
		if (oldcomp != null) {
			oldcomp.removeListener(this);
			m_form.remove(oldcomp);
		}
		gc.addListener(this);
		m_form.add(gc, cc);
		m_form.revalidate();
		syncCellAssignments();

		fireGridEvent(new GridViewEvent(this, GridViewEvent.CELL_CHANGED));
		unitTest();
	}

	/**
	 * Sets the component at the given row and column. Removes any existing
	 * components that occupy cells which will contain the new component. The
	 * component constraints determines the row and column.
	 * 
	 * @param gc
	 *            the grid component
	 * @param cc
	 *            the component constraints to set.
	 */
	public void setComponent(GridComponent gc, ComponentConstraints cc) {
		setComponent(gc, cc.createCellConstraints());
	}

	/**
	 * Sets the constraints for the given component. Note that you should not
	 * change the column and row assignments using this method.
	 * 
	 * @param gc
	 *            the grid component
	 * @param cc
	 *            the cell constraints to set.
	 */
	public void setConstraints(GridComponent gc, CellConstraints cc) {
		m_formlayout.setConstraints(gc, cc);
		syncCellAssignments();
		m_form.revalidate();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.CELL_CHANGED, gc));
	}

	/**
	 * Sets the constraints for the given component. Note that you should not
	 * change the column and row assignments using this method.
	 * 
	 * @param gc
	 *            the grid component
	 * @param cc
	 *            the component constraints to set.
	 */
	public void setConstraints(GridComponent gc, ComponentConstraints cc) {
		setConstraints(gc, cc.createCellConstraints());
	}

	/**
	 * Sets the grid overlay as visible/invisible. This is only needed during
	 * design mode.
	 * 
	 * @param bvis
	 *            set to true to show the grid. set to false to hide the grid.
	 */
	public void setGridVisible(boolean bvis) {
		m_overlay.setGridVisible(bvis);
	}

	/**
	 * Sets the paint property for the given cell. This is the property the is
	 * responsible for the fill effect for the cell.
	 * 
	 * @param col
	 *            the 1-based column
	 * @param row
	 *            the 1-based row
	 */
	public void setPaintProperty(int col, int row, PaintProperty pp) {
		m_cell_painters.setValue(row - 1, col - 1, pp);
		repaint();
	}

	/**
	 * Sets the column groups for this form. This call is ignored if the group
	 * contains columns that are invalid.
	 * 
	 * @param groups
	 *            the column groups to set for this form.
	 */
	public void setColumnGroups(FormGroupSet groups) {
		if (groups != null) {
			int[][] grp_idxs = groups.toArray();
			for (int index = 0; index < grp_idxs.length; index++) {
				int[] col_grp = grp_idxs[index];
				for (int colidx = 0; colidx < col_grp.length; colidx++) {
					int col = col_grp[colidx];
					if (col < 1 || col > getColumnCount()) {
						FormUtils.safeAssert(false);
						return;
					}
				}
			}
			m_formlayout.setColumnGroups(grp_idxs);
			m_col_groups = groups;
			fireGridEvent(new GridViewEvent(this, GridViewEvent.COLUMN_GROUPS_CHANGED));
		}
	}

	/**
	 * Sets the row groups for this form. This call is ignored if the group
	 * contains rows that are invalid.
	 * 
	 * @param groups
	 *            the row groups for this form.
	 */
	public void setRowGroups(FormGroupSet groups) {
		if (groups != null) {
			int[][] grp_idxs = groups.toArray();
			for (int index = 0; index < grp_idxs.length; index++) {
				int[] row_grp = grp_idxs[index];
				for (int rowidx = 0; rowidx < row_grp.length; rowidx++) {
					int row = row_grp[rowidx];
					if (row < 1 || row > getRowCount()) {
						FormUtils.safeAssert(false);
						return;
					}
				}
			}

			m_formlayout.setRowGroups(grp_idxs);
			m_row_groups = groups;
			fireGridEvent(new GridViewEvent(this, GridViewEvent.ROW_GROUPS_CHANGED));
		}
	}

	/**
	 * Updates the rowspec for the given row
	 * 
	 * @param rowspec
	 *            the new rowpsec
	 * @param row
	 *            the row
	 */
	public void setRowSpec(int row, RowSpec rowspec) {
		FormLayout layout = getFormLayout();

		RowSpec newspec = new RowSpec(rowspec.getDefaultAlignment(), rowspec.getSize(), rowspec.getResizeWeight());
		layout.setRowSpec(row, newspec);
		m_form.revalidate();
		fireGridEvent(new GridViewEvent(this, GridViewEvent.ROW_SPEC_CHANGED));
	}

	/**
	 * Tells the CellAssignmentCache to update itself
	 */
	private void syncCellAssignments() {
		m_assignment_cache.sync();
	}

	/**
	 * FormAccessor implementation. Adds a bean to this container using the
	 * given constraints.
	 * 
	 * @param comp
	 *            the bean to add to the form.
	 * @param cc
	 *            the constraints for the bean. This must be a valid
	 *            CellConstraints instance.
	 */
	public void addBean(Component comp, CellConstraints cc) {
		if (comp == null) {
			System.err.println("addBean: comp cannot be null");
			return;
		}

		if (cc == null) {
			System.err.println("addBean: cc cannot be null");
			return;
		}

		m_form.add(comp, cc);
		refreshView();
	}

	/**
	 * Return the cell constraints associated with the given component. The call
	 * is merely forwarded to the FormLayout instance.
	 * 
	 * @return the constraints associated with the given component.
	 * @throws NullPointerException
	 *             if component is null or has not been added to the Container
	 */
	public CellConstraints getConstraints(Component comp) {
		return getFormLayout().getConstraints(comp);
	}

	/**
	 * Return the actual container that has the given layout. This method should
	 * rarely be called. It is only provided for very limited cases.
	 * 
	 * @return the container associated with the FormLayout
	 */
	public Container getContainer() {
		return getFormContainer();
	}

	/**
	 * Defaults to beanIterator(false). See {@link #beanIterator(boolean)}
	 * 
	 * @return an iterator to all the Java Beans in this container. Beans in
	 *         nested containers are not included.
	 */
	public Iterator beanIterator() {
		return new FormIterator(this, false);
	}

	/**
	 * Returns an iterator to all Java Beans in this container. This iterator
	 * also supports iterating over nested containers if the nested flag is set
	 * to true.
	 * 
	 * @param nested
	 *            if true, all components in nested forms will be returned.
	 * @return an iterator to all the Java Beans in this container. Beans in
	 *         nested containers are included if nested is true.
	 */
	public Iterator beanIterator(boolean nested) {
		return new FormIterator(this, nested);
	}

	/**
	 * Removes a bean from the container associated with this accessor.
	 * 
	 * @param comp
	 *            the component to remove.
	 * @return the component that was removed. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component removeBean(Component comp) {
		if (comp == null) {
			System.err.println("removeBean: comp cannot be null");
			return null;
		}

		return replaceBean(comp, null);
	}

	/**
	 * FormAccessor implementation. Removes a bean with the given name from this
	 * container.
	 * 
	 * @param compName
	 *            the name of the Java Bean to remove.
	 * @return the component that was removed. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component removeBean(String compName) {
		if (compName == null) {
			System.err.println("removeBean: compName cannot be null");
			return null;
		}

		for (int index = 0; index < m_form.getComponentCount(); index++) {
			Component comp = m_form.getComponent(index);
			if (compName.equals(comp.getName())) {
				m_form.remove(comp);
				refreshView();
				return comp;
			}
			else if (comp instanceof GridComponent) {
				GridComponent gc = (GridComponent) comp;
				Component bean = gc.getBeanChildComponent();
				if (bean != null && compName.equals(bean.getName())) {
					m_form.remove(comp);
					refreshView();
					return comp;
				}
				else if (bean instanceof JScrollPane) {
					JScrollPane scroll = (JScrollPane) bean;
					JViewport viewport = scroll.getViewport();
					if (viewport != null) {
						bean = viewport.getView();
						if (bean != null && compName.equals(bean.getName())) {
							m_form.remove(scroll);
							refreshView();
							return scroll;
						}
					}
				}

			}
			else if (comp instanceof JScrollPane) {
				JScrollPane scroll = (JScrollPane) comp;
				JViewport viewport = scroll.getViewport();
				if (viewport != null) {
					Component bean = viewport.getView();
					if (bean != null && compName.equals(bean.getName())) {
						m_form.remove(comp);
						refreshView();
						return comp;
					}
				}
			}
		}
		return null;
	}

	/**
	 * FormAccessor implementation. Replaces an existing bean with a new bean.
	 * If the existing bean is not found, the request is ignored.
	 * 
	 * @param oldComp
	 *            the component to replace
	 * @param newComponent
	 *            the component to add.
	 * @return the component that was replaced. If this method fails for any
	 *         reason then null is returned.
	 */
	public Component replaceBean(Component oldComp, Component newComponent) {
		if (oldComp == null) {
			System.err.println("replaceBean: oldComponent cannot be null");
			return null;
		}

		Component comp = null;
		CellConstraints cc = null;

		for (int index = 0; index < m_form.getComponentCount(); index++) {
			comp = m_form.getComponent(index);
			cc = m_formlayout.getConstraints(comp);

			if (oldComp == comp) {
				break;
			}
			else if (comp instanceof GridComponent) {
				GridComponent gc = (GridComponent) comp;
				Component bean = gc.getBeanChildComponent();
				if (bean instanceof JScrollPane) {
					JScrollPane scroll = (JScrollPane) bean;
					JViewport viewport = scroll.getViewport();
					if (viewport != null) {
						Component vbean = viewport.getView();
						if (vbean == oldComp) {
							comp = scroll;
							break;
						}
					}
				}

				if (bean == oldComp) {
					break;
				}

			}
			else if (comp instanceof JScrollPane) {
				JScrollPane scroll = (JScrollPane) comp;
				JViewport viewport = scroll.getViewport();
				if (viewport != null) {
					Component bean = viewport.getView();
					if (bean == oldComp) {
						break;
					}
				}
			}
			comp = null;
		}

		if (comp != null) {
			m_form.remove(comp);
			if (newComponent != null) {
				m_form.add(newComponent, cc);
			}
			refreshView();
			return comp;
		}

		System.err.println("replaceBean failed.  Unable to find oldComp: " + oldComp.getClass() + "  name: " + oldComp.getName());
		return null;
	}

	/**
	 * FormAccessor implementation. Locates an existing bean with the given name
	 * and replaces it with a new bean. If the existing bean cannot be found,
	 * the request is ignored.
	 * 
	 * @param compName
	 *            the name of the component to replace
	 * @param newComponent
	 *            the component to add.
	 * @return the component that was replaced. If this method fails for any
	 *         reason null is returned.
	 */
	public Component replaceBean(String compName, Component newComponent) {
		if (compName == null) {
			System.err.println("replaceBean failed.  compName cannot be null. ");
			return null;
		}

		Iterator iter = beanIterator(false);
		while (iter.hasNext()) {
			Component comp = (Component) iter.next();
			if (compName.equals(comp.getName())) {
				return replaceBean(comp, newComponent);
			}
		}
		System.err.println("replaceBean failed.  Unable to find compName: " + compName);
		return null;
	}

	/**
	 * Runs unit test routines on this View. Internally used method.
	 */
	private void unitTest() {
		// JETATestFactory.runTest(
		// "test.jeta.forms.gui.form.GridViewValidator", this );
	}

	/**
	 * Updates the local column groups reference so that it is consistent with
	 * the form layout. The column groups are stored in a FormGroupSet variable.
	 * This variable is updated to correspond to the column groups in the
	 * FormLayout.
	 */
	public void updateColumnGroups() {
		try {
			FormGroupSet gset = new FormGroupSet();
			int[][] groups = m_formlayout.getColumnGroups();
			for (int gindex = 0; gindex < groups.length; gindex++) {
				int[] cols = groups[gindex];
				if (cols != null) {
					Integer ckey = new Integer(gindex + 1);
					for (int cindex = 0; cindex < cols.length; cindex++) {
						gset.assignToGroup(ckey, cols[cindex]);
					}
				}
			}
			m_col_groups = gset;
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

	/**
	 * Updates the local row groups reference so that it is consistent with the
	 * form layout. The row groups are stored in a FormGroupSet variable. This
	 * variable is updated to correspond to the row groups in the FormLayout.
	 */
	public void updateRowGroups() {
		try {
			FormGroupSet gset = new FormGroupSet();
			int[][] groups = m_formlayout.getRowGroups();
			for (int gindex = 0; gindex < groups.length; gindex++) {
				int[] rows = groups[gindex];
				if (rows != null) {
					Integer rkey = new Integer(gindex + 1);
					for (int rindex = 0; rindex < rows.length; rindex++) {
						gset.assignToGroup(rkey, rows[rindex]);
					}
				}
			}
			m_row_groups = gset;
		} catch (Exception e) {
			FormsLogger.debug(e);
		}
	}

	/**
	 * Traverses the container hierarhcy of a given container and notifies all
	 * containers that are GridView instances that a nested form has changed.
	 * 
	 * @param c
	 *            the container whose parent containment hiearchy to traverse
	 * @param nestedModStamp
	 *            a time stamp indicating that a child component has changed.
	 *            This is needed to support fail-fast component iterators.
	 */
	private void updateParentModicationStamps(Container c, long nestedModStamp) {
		while (c != null) {
			if (c instanceof GridView) {
				GridView gv = (GridView) c;
				gv.m_nested_mod_stamp = nestedModStamp;
			}
			c = c.getParent();
			if (c instanceof java.awt.Window || c instanceof javax.swing.JInternalFrame)
				break;
		}
	}

	/**
	 * Used for iterating over the GridComponents in this view. This iterator
	 * accesses all child components of the form container. Only components that
	 * are GridComponent instances are returned by the iterator.
	 */
	private class ComponentIterator implements Iterator {
		private int m_pos = -1;

		public boolean hasNext() {
			int pos = m_pos + 1;
			while (pos < m_form.getComponentCount()) {
				Object obj = m_form.getComponent(pos);
				if (obj instanceof GridComponent) {
					m_pos = pos - 1;
					return true;
				}
				pos++;
			}
			return false;
		}

		public Object next() {
			m_pos++;
			while (m_pos < m_form.getComponentCount()) {
				Object obj = m_form.getComponent(m_pos);
				if (obj instanceof GridComponent)
					return obj;

			}
			throw new java.util.NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * This is the container that has the actual FormLayout. This class
	 * overrides JPanel mainly to intercept add/remove calls so the timestamp
	 * can be modified. We keep track of the timestamp to support fail-fast
	 * FormIterators.
	 */
	private class FormContainer extends JPanel {
		/**
		 * A timestamp of the last time this container was modified. This is
		 * primarily used to implement the fail-fast FormIterator
		 */
		private long m_mod_stamp;

		protected void addImpl(Component comp, Object constraints, int index) {
			super.addImpl(comp, constraints, index);
			m_mod_stamp = System.currentTimeMillis();
			updateParentModicationStamps(GridView.this.getParent(), m_mod_stamp);
		}

		public void remove(int index) {
			super.remove(index);
			m_mod_stamp = System.currentTimeMillis();
			updateParentModicationStamps(GridView.this.getParent(), m_mod_stamp);
		}

		public void removeAll() {
			super.removeAll();
			m_mod_stamp = System.currentTimeMillis();
			updateParentModicationStamps(GridView.this.getParent(), m_mod_stamp);
		}

		/**
		 * @return the timestamp of the last time this container was modified.
		 *         This is primarily used to implement the fail-fast
		 *         FormIterator
		 */
		long getModificationStamp() {
			return m_mod_stamp;
		}
	}

}
