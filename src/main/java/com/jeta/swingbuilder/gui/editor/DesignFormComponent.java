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

package com.jeta.swingbuilder.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.EmptyComponentFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridCellEvent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridOverlay;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.GridViewEvent;
import com.jeta.forms.gui.form.GridViewListener;
import com.jeta.forms.gui.form.ReadOnlyConstraints;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.registry.JETARegistry;
import com.jeta.open.resources.AppResourceLoader;

/**
 */
public class DesignFormComponent extends FormComponent implements GridViewListener {
	/**
	 * The panel at the top of this component that contains the expand/hide and
	 * edit buttons for this form. These buttons are only visible in design
	 * mode.
	 */
	private JPanel m_btnpanel;
	private AbstractButton m_expand_btn;
	private AbstractButton m_edit_btn;
	private AbstractButton m_grid_btn;

	/**
	 * Creates a <code>DesignFormComponent</code> instance.
	 */
	public DesignFormComponent() {

	}

	/**
	 * Creates a <code>DesignFormComponent</code> instance with the specified
	 * id, child bean, and parent view.
	 * 
	 * @param id
	 *            the unique id for this form.
	 * @param jbean
	 *            the underlying GridView
	 * @param parentView
	 *            the parent for this form.
	 * @param embedded
	 *            flag that indicates if this form is embedded
	 */
	public DesignFormComponent(String id, JETABean jbean, GridView parentView, boolean embedded) throws FormException {
		super(id, jbean, parentView, embedded);
	}

	/**
	 * Creates the control buttons at the top of the form. The control buttons
	 * are only visible in the designer.
	 * 
	 * @return the panel that contains the control buttons for the form.
	 */
	private JPanel createControlsPanel() {
		// expand/hide buttons for child form when in design mode.
		m_btnpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		m_btnpanel.setOpaque(false);
		m_btnpanel.setFocusable(false);

		JButton btn = new JButton(AppResourceLoader.getImage("forms/form_control_collapse.gif"));
		Dimension d = new Dimension(12, 20);
		btn.setPreferredSize(d);
		btn.setMinimumSize(d);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusable(false);

		m_expand_btn = btn;
		m_btnpanel.add(btn);

		m_btnpanel.add(javax.swing.Box.createHorizontalStrut(2));
		btn = new JButton(AppResourceLoader.getImage("forms/form_control_edit.gif"));
		btn.setPreferredSize(d);
		btn.setMinimumSize(d);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusable(false);

		m_edit_btn = btn;
		m_btnpanel.add(btn);

		m_btnpanel.add(javax.swing.Box.createHorizontalStrut(2));
		btn = new JButton(AppResourceLoader.getImage("forms/form_control_grid.gif"));
		btn.setPreferredSize(d);
		btn.setMinimumSize(d);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setFocusable(false);

		btn.setContentAreaFilled(false);
		m_grid_btn = btn;
		m_btnpanel.add(btn);

		return m_btnpanel;
	}

	/**
	 * The panel at the top of this component that contains the expand/hide and
	 * edit buttons for this form. These buttons are only visible during design
	 * mode.
	 * 
	 * @return the panel that contains the control buttons for the form.
	 */
	public JPanel getButtonPanel() {
		return m_btnpanel;
	}

	/**
	 * Returns the component that renders the grid lines for the GridView
	 * associated with this form.
	 * 
	 * @return the GridOverlay component associated with the GridView that is
	 *         contained within this component.
	 */
	public GridOverlay getChildOverlay() {
		GridView gv = getChildView();
		return gv.getOverlay();
	}

	/**
	 * Returns the control button used to open a nested form in the editor. This
	 * button is only visible in design mode.
	 * 
	 * @return the button used to open a nested form in the editor
	 */
	public AbstractButton getEditButton() {
		return m_edit_btn;
	}

	/**
	 * Returns the control button used to expand/hide a nested form in the
	 * editor. This button is only visible in design mode.
	 * 
	 * @return the button used to expand/hide a nested form
	 */
	public AbstractButton getExpandButton() {
		return m_expand_btn;
	}

	/**
	 * Returns the control button used to show/hide the grid lines of a form in
	 * the editor. This button is only visible in design mode.
	 * 
	 * @return the button used to show/hide the grid lines
	 */
	public AbstractButton getGridButton() {
		return m_grid_btn;
	}

	/**
	 * We keep this event around so we don't have to instantiate every time a
	 * cell is selected.
	 */
	private GridCellEvent m_cell_changed = new GridCellEvent(GridCellEvent.CELL_CHANGED, this);

	/**
	 * GridViewListener implementation. This method is called when events are
	 * fired from the child GridView contained by this FormComponent. All events
	 * are forwarded up the listener chain.
	 * 
	 * @param evt
	 *            the event fired by the GridView contained by this form.
	 */
	public void gridChanged(GridViewEvent evt) {
		if (evt.getComponentEvent() != null)
			fireGridCellEvent(evt.getComponentEvent());
		else {
			fireGridCellEvent(m_cell_changed);
		}
	}

	/**
	 * Returns the flag that indicates if the grid view associated with this
	 * form is visible. This is different than showing the grid lines. This call
	 * determines if the underlying components in the view in addition to the
	 * grid lines are visible.
	 */
	public boolean isGridViewVisible() {
		GridView view = getChildView();
		DesignGridOverlay overlay = (DesignGridOverlay) view.getOverlay();
		return !overlay.isOpaque();
	}

	/**
	 * Creates and initializes a FormComponent from the given form memento.
	 */
	protected FormComponent openLinkedForm(FormMemento fm) throws FormException {
		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);

		/**
		 * If we are in design mode and a child form is encountered that is
		 * linked, then we open the form using the formmanager. This is because
		 * another view might have the linked form opened already. This can
		 * never happen if the form is embedded.
		 */
		if (FormUtils.isDesignMode()) {
			/**
			 * Here we must assume that any other views to the same form *must*
			 * be deactivated by the FormManager.
			 */
			FormComponent fc = fmgr.getForm(fm.getId());
			if (fc == null) {

				ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
				String abspath = pmgr.getAbsolutePath(fm.getRelativePath());
				fc = fmgr.openLinkedForm(abspath);
			}
			return fc;
		}
		else {
			FormUtils.safeAssert(false);
			return super.openLinkedForm(fm);
		}
	}

	/**
	 * Shows/hides the control buttons at the top of this component
	 */
	public void setControlButtonsVisible(boolean bvis) {
		if (m_btnpanel != null) {
			remove(m_btnpanel);
			if (bvis) {
				add(m_btnpanel, BorderLayout.NORTH);
			}
		}
	}

	/**
	 * Shows or hides the grid view associated with this form. This is different
	 * than showing the grid lines. This call also shows/hides the underlying
	 * components in the view in addition to the grid lines.
	 */
	public void setGridViewVisible(boolean bvis) {
		GridView view = getChildView();
		DesignGridOverlay overlay = (DesignGridOverlay) view.getOverlay();
		overlay.setOpaque(!bvis);
		overlay.setGridVisible(bvis);
		if (bvis) {
			m_expand_btn.setIcon(AppResourceLoader.getImage("forms/form_control_collapse.gif"));
		}
		else {
			m_expand_btn.setIcon(AppResourceLoader.getImage("forms/form_control_expand.gif"));
		}
	}

	/**
	 * Override GridComponent implementation so we can add the child to this
	 * container for the design view.
	 */
	protected void setBean(JETABean jbean) {
		super.setBean(jbean);

		FormUtils.safeAssert(jbean.getDelegate() instanceof GridView);
		((GridView) jbean.getDelegate()).addListener(this);

		if (FormUtils.isDesignMode()) {
			add(javax.swing.Box.createVerticalStrut(2), BorderLayout.SOUTH);
			if (m_btnpanel == null)
				add(createControlsPanel(), BorderLayout.NORTH);

			setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}
		else {
			assert (false);
		}
	}

	/**
	 * Performs any final initialization of this form component after it's state
	 * has been restored. The main operation is to add empty components where
	 * needed. When in design mode, every cell in the grid has a GridComponent.
	 * The reason is because we need to enforce a minimum size for the cell when
	 * the user sets the row/col size to preferred. If not, the cell size would
	 * be zero if nothing were there. However, in run mode, we don't want to
	 * create an empty component for every single cell. For example, a 20x20
	 * grid would require 400 components. To prevent this, we only add 1 empty
	 * component per row and column. This allows the runtime form to look
	 * approximately like the design time forms with rows/cols that have no
	 * components. We use the grid_cache to keep track of which columns and rows
	 * have had empty components added.
	 */
	protected void postSetState(ComponentMemento cm) {
		try {
			GridView view = getChildView();
			ComponentSource compsrc = (ComponentSource) JETARegistry.lookup(ComponentSource.COMPONENT_ID);
			EmptyComponentFactory factory = new EmptyComponentFactory(compsrc);
			for (int col = 1; col <= view.getColumnCount(); col++) {
				for (int row = 1; row <= view.getRowCount(); row++) {
					GridComponent gc = view.getGridComponent(col, row);
					if (gc == null) {
						gc = (StandardComponent) factory.createComponent("empty", view);
						view.addComponent(gc, new ReadOnlyConstraints(col, row));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets this component from a previously saved state.
	 * 
	 * @param memento
	 */
	public void setState(ComponentMemento memento) throws FormException {
		super.setState(memento);
		// **** now make sure that any empty cells are properly filled
		getChildView().enableEvents(true);
	}
}
