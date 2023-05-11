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
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.bean.BeanDeserializer;
import com.jeta.forms.store.bean.BeanSerializer;
import com.jeta.forms.store.bean.BeanSerializerFactory;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.CellConstraintsMemento;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.FocusPolicyMemento;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.registry.JETARegistry;

/**
 * A <code>FormComponent</code> is a type of GridComponent that contains a
 * nested form in a GridView. A FormComponent represents a top-level form or
 * nested form whereas StandardComponents represent Swing components (Java
 * beans).
 * 
 * Forms come in two types: Embedded and Linked. An embedded form is a nested
 * form that is fully encapsulated and stored in the parent. A linked form
 * refers to a form file on disk, but it can exist as a child in another form.
 * 
 * All GridComponents have a single JETABean has a child. The FormComponent has
 * a JETABean child which is a wrapper around a GridView bean.
 * 
 * Containment Hierarchy:
 * 
 * <pre>
 *   FormComponent
 *       |
 *        -- JETABean -maintains the properties for the form.
 *             |
 *              -- GridView -a GridView has N child components that occupy the cells in the view
 *                    |                           
 *                    //-- GridView.FormContainer ---------------------&gt; FormLayout
 *                            |                         (layoutmgr)
 *                             -- GridComponent1
 *                            |
 *                             -- GridComponent2
 *                            |
 *                             -- GridComponentN
 *                                 -each GridComponent is either a StandardComponent(Swing component)
 *                                  or a nested form (FormComponent)
 * </pre>
 * 
 * @author Jeff Tassin
 */
public class FormComponent extends GridComponent {
	/**
	 * A unique id for this form. If the form is linked, then this value will be
	 * the absolute file path.
	 */
	private String m_id;

	/**
	 * Flag that indicates if this form is embedded.
	 */
	private boolean m_embedded = false;

	/**
	 * The path for this form on the local file system. Note that we don't store
	 * the full path in the FormMemento (only the relative package).
	 */
	private String m_abspath = null;

	/**
	 * This is a flag used to indicate is this is the top-most form when saving
	 * this form. This reason for the flag is if this form is linked,we only
	 * store the path to the linked form and not the contents. However, a
	 * top-level form can be linked, and in this case we need to store
	 * everything.
	 */
	private boolean m_top_level_form = false;

	/**
	 * The focus policy for this form. This applies to top-level forms only.
	 * 
	 * @deprecated The designer is no longer supporting focus.
	 */
	@Deprecated
	private FocusPolicyMemento m_focus_policy;

	/**
	 * Creates a <code>FormComponent</code> instance.
	 */
	protected FormComponent() {

	}

	/**
	 * Creates a <code>FormComponent</code> instance with the specified id,
	 * child bean, and parent view.
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
	protected FormComponent(String id, JETABean jbean, GridView parentView, boolean embedded) throws FormException {
		super(jbean, parentView);
		m_id = id;
		FormUtils.safeAssert(jbean.getDelegate() instanceof GridView);
		setBean(getBean());
		m_embedded = embedded;
	}

	/**
	 * Creates a form component.
	 */
	public static FormComponent create() {
		FormComponentFactory factory = (FormComponentFactory) JETARegistry.lookup(FormComponentFactory.COMPONENT_ID);
		if (factory == null) {
			FormUtils.safeAssert(!FormUtils.isDesignMode());
			return new FormComponent();
		}
		else {
			return factory.createFormComponent();
		}
	}

	/**
	 * Returns the absolute path to this form. If the form is embedded, this
	 * value will be null.
	 * 
	 * @return the absolute path to this form if the form is a linked form.
	 */
	public String getAbsolutePath() {
		return m_abspath;
	}

	/**
	 * Returns the total width in pixels of the cells occupied by this
	 * component.
	 * 
	 * @return the total width in pixels of the cells occupied by this component
	 */
	public int getCellWidth() {
		if (getParentView() == null)
			return getWidth();
		else
			return super.getCellWidth();
	}

	/**
	 * Returns the total height in pixels of the cells occupied by this
	 * component.
	 * 
	 * @return the total height in pixels of the cells occupied by this
	 *         component
	 */
	public int getCellHeight() {
		if (getParentView() == null)
			return getHeight();
		else
			return super.getCellHeight();
	}

	/**
	 * Returns the left location of this component's cell in the parent
	 * coordinates.
	 * 
	 * @return the left location of this component's cell in the parent
	 *         coordinates.
	 */
	public int getCellX() {
		if (getParentView() == null)
			return getX();
		else
			return super.getCellX();
	}

	/**
	 * Returns the top location of this component's cell in the parent
	 * coordinates.
	 * 
	 * @return the top location of this component's cell in the parent
	 *         coordinates.
	 */
	public int getCellY() {
		if (getParentView() == null)
			return getY();
		else
			return super.getCellY();
	}

	/**
	 * Returns the GridView that is associated with this form.
	 * 
	 * @return the child view associated with this component
	 */
	public GridView getChildView() {
		JETABean bean = getBean();
		if (bean != null) {
			return (GridView) bean.getDelegate();
		}
		return null;
	}

	/**
	 * Returns the number of columns in this form.
	 * 
	 * @return the number of columns in this form
	 */
	public int getColumnCount() {
		GridView view = getChildView();
		return (view == null ? 0 : view.getColumnCount());
	}

	/**
	 * Returns the filename of this form. If the form is embedded, this value is
	 * null.
	 * 
	 * @return the filename portion of the path to this form (only if this form
	 *         is linked)
	 */
	public String getFileName() {
		if (isLinked()) {
			String path = getAbsolutePath();
			if (path != null) {
				int pos = path.lastIndexOf('/');
				if (pos < 0)
					pos = path.lastIndexOf('\\');

				if (pos >= 0) {
					return path.substring(pos + 1, path.length());
				}
				else {
					return path;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the focus policy assigned to this form.
	 * 
	 * @return the focus policy for this form (not currently used)
	 * @deprecated the designer no longer supports focus policy.
	 */
	@Deprecated
	public FocusPolicyMemento getFocusPolicy() {
		return m_focus_policy;
	}

	/**
	 * Returns the child grid component that occupies the given row and column
	 * in this form.
	 * 
	 * @return the GridComponent at the specificed column and row
	 */
	public GridComponent getGridComponent(int col, int row) {
		GridView view = getChildView();
		return (view == null ? null : view.getGridComponent(col, row));
	}

	/**
	 * Traverses the container hiearchy for the given form starting from its
	 * parent and traversing until it encounters a FormComponent instance.
	 * 
	 * @return the formcomponent that is the closest ancestor of the given form.
	 *         Null is returned if this the topmost parent form.
	 */
	public static FormComponent getParentForm(Component comp) {
		if (comp == null)
			return null;

		comp = comp.getParent();
		while (comp != null) {
			if (comp instanceof FormComponent)
				return (FormComponent) comp;

			comp = comp.getParent();
		}
		return null;
	}

	/**
	 * Traverses the container hiearchy for this form starting from its parent
	 * and traversing until it encounters a FormComponent instance.
	 * 
	 * @return the formcomponent that is the closest ancestor of this form. Null
	 *         is returned if this the topmost parent form.
	 */
	public FormComponent getParentForm() {
		return getParentForm(this);
	}

	/**
	 * Returns the selected child component in this form. The is the component
	 * that is selected by the user in design mode.
	 * 
	 * @returns the first selected component it finds in the component hierarhcy
	 *          of this container. Null is returned if no component is selected.
	 */
	public GridComponent getSelectedComponent() {
		if (isSelected())
			return this;
		else
			return getChildView().getSelectedComponent();
	}

	/**
	 * Always call this method instead of getState when saving a top level form.
	 * Saves this form's state to a memento.
	 * 
	 * @return the form state as a memento.
	 */
	public FormMemento getExternalState(StateRequest si) throws FormException {
		try {
			JETARegistry.rebind(StateRequest.COMPONENT_ID, si);
			setTopLevelForm(true);
			return (FormMemento) getState(si);
		} finally {
			setTopLevelForm(false);
			JETARegistry.rebind(StateRequest.COMPONENT_ID, null);
		}
	}

	/**
	 * Returns the number of rows in this form.
	 * 
	 * @return the number of rows in this form
	 */
	public int getRowCount() {
		GridView view = getChildView();
		return (view == null ? 0 : view.getRowCount());
	}

	/**
	 * Saves this form's state as a memento object.
	 * 
	 * @param si
	 *            a state request that has some control over how the form state
	 *            is stored. For example, in some cases we want to store full
	 *            copy of a linked form in the memento as oposed to the link
	 *            reference.
	 * @return the state of this object as a mememento
	 */
	public ComponentMemento getState(StateRequest si) throws FormException {
		FormMemento state = new FormMemento();
		GridView view = getChildView();

		state.setId(getId());
		state.setComponentClass(FormComponent.class.getName());
		state.setFocusPolicy(m_focus_policy);

		if (getParentView() != null) {
			state.setCellConstraints(getConstraints().createCellConstraints());
		}

		if (isLinked() && si.isShallowCopy()) {
			state.setRelativePath(getRelativePath());
			if (!isTopLevelForm()) {
				return state;
			}
		}

		state.setRowGroups(view.getRowGroups());
		state.setColumnGroups(view.getColumnGroups());
		state.setRowSpecs(view.getRowSpecs());
		state.setColumnSpecs(view.getColumnSpecs());
		state.setCellPainters(view.getCellPainters());
		Iterator iter = view.gridIterator();
		while (iter.hasNext()) {
			GridComponent gc = (GridComponent) iter.next();
			if (gc.getBean() != null) {
				ComponentMemento ccm = gc.getState(si);
				state.addComponent(ccm);
			}
		}

		/** store the view properties */
		BeanSerializerFactory fac = (BeanSerializerFactory) JETARegistry.lookup(BeanSerializerFactory.COMPONENT_ID);
		BeanSerializer bs = fac.createSerializer();
		JETABean jbean = getBean();
		FormUtils.safeAssert(jbean.getDelegate() == view);
		PropertiesMemento pm = bs.writeBean(jbean);
		state.setPropertiesMemento(pm);
		return state;
	}

	/**
	 * Returns a unique id for this form. If the form is embedded, the id is
	 * based on this object's hashCode. If the form is linked, the id is the
	 * absolute path to the form.
	 * 
	 * @return the unique id of this form.
	 */
	public String getId() {
		if (isEmbedded()) {
			if (m_id == null)
				m_id = "embedded." + String.valueOf(hashCode());

			return m_id;
		}
		else {
			if (m_abspath == null) {
				if (m_id == null)
					m_id = "linked." + String.valueOf(hashCode());

				return m_id;
			}
			else
				return m_abspath;
		}
	}

	/**
	 * Returns the relative path to this form. The path is determined by the
	 * source paths defined in the project settings. If this form is embedded,
	 * null is returned.
	 * 
	 * @return the relative path.
	 */
	public String getRelativePath() {
		ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
		return pmgr.getRelativePath(m_abspath);
	}

	/**
	 * Traverses the container hierarchy for the given component and returns the
	 * first parent that is a top-level FormComponent. Most components only have
	 * one top level parent. However, if a component is in a JTabbedPane, it
	 * will have two top-level parents.
	 * 
	 * @param comp
	 *            the comp that determines where the traversal will start.
	 * @return the first FormComponent ancestor found in the container
	 *         heirarchy.
	 */
	public static FormComponent getTopLevelForm(Component comp) {
		if (comp == null)
			return null;

		while (comp != null && !(comp instanceof java.awt.Window)) {
			if (comp instanceof FormComponent && ((FormComponent) comp).isTopLevelForm())
				return (FormComponent) comp;
			comp = comp.getParent();
		}
		return null;
	}

	/**
	 * Returns true if this form is an embedded form. An embedded form is stored
	 * within the parent form.
	 * 
	 * @return true if this form is an embedded form.
	 */
	public boolean isEmbedded() {
		return m_embedded;
	}

	/**
	 * Returns true if this form is linked. A linked form is stored in its own
	 * form file on disk.
	 * 
	 * @return true if this form is a linked form.
	 */
	public boolean isLinked() {
		return !isEmbedded();
	}

	/**
	 * Returns an interator that iterates over the grid components in a child
	 * view.
	 * 
	 * @return an iterator that iterates over the grid components (GridComponent
	 *         objects) in the child view
	 */
	public Iterator gridIterator() {
		return getChildView().gridIterator();
	}

	/**
	 * Return true if this form is the top-most form in the container hierarchy.
	 * 
	 * @return the flag used to indicate is this is the top-most form when
	 *         saving this form.
	 */
	public boolean isTopLevelForm() {
		return m_top_level_form;
	}

	/**
	 * Creates and initializes a FormComponent from the given form memento.
	 */
	protected FormComponent openLinkedForm(FormMemento fm) throws FormException {
		FormComponent fc = com.jeta.forms.gui.formmgr.FormManagerUtils.openPackagedForm(fm.getRelativePath());
		return fc;
	}

	/**
	 * PostInitialize is called once after all components in a FormPanel have
	 * been instantiated at runtime (not design time). This gives each property
	 * and component a chance to do some last minute initializations that might
	 * depend on the top level parent. FormComponent simply forwards the call to
	 * any children.
	 * 
	 * @param panel
	 *            the top-level form container
	 * @param cc
	 *            a container whose child components are to be
	 *            (post)intitialized.
	 */
	public void _postInitialize(FormPanel panel, Container cc) {
		if (cc == null)
			return;

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof GridComponent)
				((GridComponent) comp).postInitialize(panel);
			else if (comp instanceof Container)
				_postInitialize(panel, (Container) comp);
		}
	}

	/**
	 * PostInitialize is called once after all components in a form have been
	 * re-instantiated at runtime (not design time). This gives each property
	 * and component a chance to do some last minute initializations that might
	 * depend on the top level parent. An example of this is button groups which
	 * are global to a form. FormComponent simply forwards the call to any
	 * children.
	 * 
	 * @param panel
	 *            the top-level form container
	 */
	public void postInitialize(FormPanel panel) {
		_postInitialize(panel, this);
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
		GridView view = getChildView();
		for (int col = 1; col <= view.getColumnCount(); col++) {
			for (int row = 1; row <= view.getRowCount(); row++) {
				GridComponent gc = view.getGridComponent(col, row);
				if (gc == null) {
					gc = new StandardComponent(null, view);
					gc.setPreferredSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
					gc.setMinimumSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
					view.addComponent(gc, new ReadOnlyConstraints(col, row));
					break;
				}
			}
		}

		for (int row = 2; row <= view.getRowCount(); row++) {
			for (int col = 1; col <= view.getColumnCount(); col++) {
				GridComponent gc = view.getGridComponent(col, row);
				if (gc == null) {
					gc = new StandardComponent(null, view);
					gc.setPreferredSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
					gc.setMinimumSize(new Dimension(GridComponent.EMPTY_CELL_WIDTH, GridComponent.EMPTY_CELL_HEIGHT));
					view.addComponent(gc, new ReadOnlyConstraints(col, row));
					break;
				}
			}
		}

	}

	/**
	 * Print for debugging
	 */
	public void print() {
		FormsLogger.debug("FormComponent  name = " + getName() + "   uid = " + getId() + "  path = " + getAbsolutePath() + "  hash: " + hashCode()
				+ "   parentView: " + getParentView());
	}

	/**
	 * Override revalidate so we can forward the call to the underlying GridView
	 * as well.
	 */
	public void revalidate() {
		GridView view = getChildView();
		if (view != null)
			view.revalidate();
		super.revalidate();
	}

	/**
	 * Override GridComponent implementation so we can add the child to this
	 * container for the design view.
	 */
	protected void setBean(JETABean jbean) {
		super.setBean(jbean);

		FormUtils.safeAssert(jbean.getDelegate() instanceof GridView);
		setLayout(new BorderLayout());

		FormUtils.safeAssert(getComponentCount() == 0);

		/** remove any existing components */
		removeAll();

		add(jbean, BorderLayout.CENTER);
	}

	/**
	 * Sets the absolute path for this form.
	 */
	public void setAbsolutePath(String path) {
		m_abspath = path;
		if (path != null)
			m_embedded = false;
	}

	public void setControlButtonsVisible(boolean bVisible) {
		// no op
	}

	/**
	 * Sets the focus policy for this form
	 * 
	 * @deprecated The designer no longer supports focus.
	 */
	@Deprecated
	public void setFocusPolicy(FocusPolicyMemento fm) {
		m_focus_policy = fm;
	}

	/**
	 * Override setSelected so we can deselect everything in the child view when
	 * being dselected.
	 */
	public void setSelected(boolean bsel) {
		super.setSelected(bsel);
		if (!bsel) {
			GridView gv = (GridView) getChildView();
			gv.deselectAll();
		}
	}

	/**
	 * Resets this component from a previously saved state.
	 * 
	 * @param memento
	 */
	public void setState(ComponentMemento memento) throws FormException {
		FormMemento state = (FormMemento) memento;

		if (state.getRelativePath() == null)
			m_embedded = true;

		m_focus_policy = state.getFocusPolicy();

		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		if (FormUtils.isDesignMode()) {
			FormUtils.safeAssert(fmgr != null);
		}

		JETABean viewbean = JETABeanFactory.createBean("com.jeta.forms.gui.form.GridView", "gridview", true, true);
		GridView view = (GridView) viewbean.getDelegate();
		view.initialize(state.getColumnSpecs(), state.getRowSpecs());
		view.setCellPainters(state.getCellPainters());
		setBean(viewbean);
		view.setRowGroups(state.getRowGroups());
		view.setColumnGroups(state.getColumnGroups());

		/** set the view properties */
		PropertiesMemento pm = state.getPropertiesMemento();
		if (pm != null) {
			BeanSerializerFactory fac = (BeanSerializerFactory) JETARegistry.lookup(BeanSerializerFactory.COMPONENT_ID);
			BeanDeserializer bds = fac.createDeserializer(pm);
			bds.initializeBean(viewbean);
		}
		else {
			/** encountered a deprecated form state. */
			try {
				HashMap props = state.getProperties();
				DynamicBeanInfo beaninfo = viewbean.getBeanInfo();
				Collection jpds = beaninfo.getPropertyDescriptors();
				Iterator iter = jpds.iterator();
				while (iter.hasNext()) {
					JETAPropertyDescriptor jpd = (JETAPropertyDescriptor) iter.next();
					Object value = props.get(jpd.getName());
					if (value != null) {
						jpd.setPropertyValue(viewbean, value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Iterator iter = state.iterator();
		while (iter.hasNext()) {
			ComponentMemento cm = (ComponentMemento) iter.next();
			try {
				if (cm instanceof FormMemento) {
					FormMemento fm = (FormMemento) cm;
					/** form is linked if the path is valid */
					if (fm.getRelativePath() != null) {
						try {
							FormComponent fc = openLinkedForm(fm);
							/**
							 * @todo check if the linked form has already been
							 *       opened in this view. need to handle the
							 *       case where the user wants to embed two or
							 *       more of the same linked forms in a single
							 *       view. This is an exceedinly rare case
							 */
							view.addComponent(fc, fm.getCellConstraintsMemento().createCellConstraints());
						} catch (Exception e) {
							javax.swing.JLabel elabel = new javax.swing.JLabel("Error: " + fm.getRelativePath());
							elabel.setForeground(java.awt.Color.red);
							JETABean bean = new JETABean(elabel);
							StandardComponent gc = new StandardComponent(bean, view);
							view.addComponent(gc, fm.getCellConstraintsMemento().createCellConstraints());

							/**
							 * Unable to add form.
							 */
							FormsLogger.severe(e);
						}
						continue;
					}
				}

				CellConstraintsMemento ccm = cm.getCellConstraintsMemento();

				/**
				 * If we are here, then the child component is either a standard
				 * Java Bean or an embedded form.
				 */
				if (StandardComponent.class.getName().equals(cm.getComponentClass()) && cm instanceof BeanMemento) {
					BeanMemento bm = (BeanMemento) cm;
					if (bm.getBeanClass() == null) {
						// ignore empty components here.
						continue;
					}
				}

				GridComponent gc = null;
				Class gc_class = Class.forName(cm.getComponentClass());
				if (FormComponent.class.isAssignableFrom(gc_class))
					gc = FormComponent.create();
				else
					gc = (GridComponent) gc_class.newInstance();

				gc.setState(cm);
				if (ccm == null) {
					/** this should never happen */
					System.out.println("FormComponent.setState cellconstraints memento is null: ");
					gc.print();
					FormUtils.safeAssert(false);
				}
				else {
					view.addComponent(gc, ccm.createCellConstraints());
				}
			} catch (Exception e) {
				FormsLogger.severe(e);
			}
		}

		// now traverse the grid cache and add an empty components where needed
		postSetState(memento);

		view.refreshView();
	}

	/**
	 * Sets the flag used to indicate is this is the top-most form when saving
	 * this form.
	 * 
	 * @param topLevel
	 *            set to true to make this a top-level form.
	 */
	public void setTopLevelForm(boolean topLevel) {
		m_top_level_form = topLevel;
	}

}
