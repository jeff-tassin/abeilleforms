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

package com.jeta.swingbuilder.gui.components;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JTabbedPane;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.FormContainerComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.open.registry.JETARegistry;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class DefaultContainedFormFactory implements ContainedFormFactory {
	/**
	 * ctor
	 */
	public DefaultContainedFormFactory() {

	}

	/**
	 * Creates a form that is meant to be contained in a Swing container. This
	 * is form forms that can be edited in-place in the designer.
	 */
	public FormComponent createContainedForm(Class swingClass, FormMemento fm) throws FormException {
		FormUtils.safeAssert(swingClass == JTabbedPane.class);

		ComponentSource compsrc = (ComponentSource) JETARegistry.lookup(ComponentSource.COMPONENT_ID);
		FormUtils.safeAssert(compsrc != null);

		EmbeddedFormComponentFactory embedded_fac = new EmbeddedFormComponentFactory(compsrc);
		FormComponent form = null;

		if (fm == null) {
			form = embedded_fac.create(compsrc, "", null, 3, 3, true);
			GridView.fillCells(form.getChildView(), compsrc);
		}
		else {
			form = FormComponent.create();
			form.setState(fm);
		}
		return form;

	}

	/**
	 * This method creates a top-level parent form that is used to contain a
	 * form that we can edit. This is used in two cases. In the first, we use a
	 * top level form in the FormEditor. In the second, we use the top-level
	 * form in contained forms (e.g. a form that is contained in a JTabbedPane
	 * tab ).
	 * 
	 * @param parent
	 *            the object that will contain the top-level parent
	 * @param compsrc
	 *            the component source
	 * @param form
	 *            the form that will be contained by the top-level parent
	 */
	public FormComponent createTopParent(Container container, ComponentSource compsrc, FormComponent form) throws FormException {
		EmbeddedFormComponentFactory factory = new EmbeddedFormComponentFactory(compsrc);
		FormComponent parent = (FormComponent) factory.create(compsrc, "formeditor.top.parent", null, 1, 1, true);
		parent.setTopLevelForm(true);
		parent.setControlButtonsVisible(false);
		form.setControlButtonsVisible(true);
		GridView view = parent.getChildView();
		view.setGridVisible(false);
		view.setRowSpec(1, new RowSpec("fill:pref:grow"));
		view.setColumnSpec(1, new ColumnSpec("fill:pref:grow"));
		CellConstraints cc = new CellConstraints();
		view.addComponent(form, cc.xy(1, 1));
		parent.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));

		/**
		 * Now get the FormContainerComponent instance and add it as a listener
		 * to the newly created form. This is needed so that GridComponent
		 * events such as cell_selected and cell_changed are properly propagated
		 * up the form hierarchy.
		 */
		FormContainerComponent fcc = getFormContainerComponent(container);
		if (fcc != null) {
			assert (fcc.getBeanDelegate() instanceof javax.swing.JTabbedPane);
			view.addListener(fcc);
		}

		if ((container instanceof javax.swing.JTabbedPane) && FormUtils.isDesignMode()) {
			if (fcc == null) {
				System.err.println("DefaultContainedFormFactory encountered invalid container: " + container);
			}
			FormUtils.safeAssert(fcc != null);
		}

		return parent;
	}

	/**
	 * @return the FormContainerComponent that is an ancestor of the given
	 *         parent.
	 */
	private FormContainerComponent getFormContainerComponent(Component parent) {
		while (parent != null && !(parent instanceof java.awt.Window)) {
			if (parent instanceof FormContainerComponent)
				return (FormContainerComponent) parent;

			parent = parent.getParent();
		}
		return null;
	}

}
