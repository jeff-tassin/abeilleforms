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
import java.awt.Container;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.JETABean;

/**
 * A FormContainerComponent is a standard Swing component that can have embedded
 * forms as children. An example of type type of component is a JTabbedPane. A
 * JTabbedPane is a StandardComponent in our architecture; however, it is the
 * only GridComponent besides a FormComponent that can have embedded forms as
 * children. We have this specialized class for the sole purpose of propagating
 * gridcomponent events up the forms hierarchy when in design mode. JSplitPanes
 * are not currrently supported, but they would also be an example of a
 * FormContainerComponent.
 * 
 * @author Jeff Tassin
 */
public class FormContainerComponent extends StandardComponent implements GridViewListener {

	/**
	 * Creates an uninitialized <code>FormContainerComponent</code> instance.
	 */
	public FormContainerComponent() {

	}

	/**
	 * Creates a <code>FormContainerComponent</code> instance with the
	 * specified parent view.
	 * 
	 * @param parentView
	 *            the view that contains this component
	 */
	public FormContainerComponent(GridView parentView) {
		super(parentView);
	}

	/**
	 * Creates a <code>FormContainerComponent</code> instance with the
	 * specified jetabean and parent view.
	 * 
	 * @param jbean
	 *            a JETABean that contains a Swing component that can contain an
	 *            embedded form. Currently, we are limited to JTabbedPane (or in
	 *            the future a JSplitPane).
	 * @param parentView
	 *            the view that contains this component
	 */
	public FormContainerComponent(JETABean jbean, GridView parentView) {
		super(jbean, parentView);
	}

	/**
	 * If this component is selected, then it is returned. Otherise, this call
	 * is forwarded to any nested forms contained by this component.
	 * 
	 * @returns the first selected component it finds in the component hierarhcy
	 *          of this container.
	 */
	public GridComponent getSelectedComponent() {
		if (isSelected())
			return this;

		Component comp = getBeanDelegate();
		if (comp instanceof Container)
			return getSelectedComponent((Container) comp);

		return null;
	}

	/**
	 * Returns the first selected GridComponent found in the specified component
	 * hierarchy.
	 * 
	 * @returns the first selected component it finds in the component hierarhcy
	 *          of this container.
	 */
	public GridComponent getSelectedComponent(Container cc) {
		if (cc == null)
			return null;

		for (int index = 0; index < cc.getComponentCount(); index++) {
			Component comp = cc.getComponent(index);
			if (comp instanceof FormComponent) {
				FormComponent fc = (FormComponent) comp;
				GridComponent gc = fc.getSelectedComponent();
				if (gc != null)
					return gc;
			}
			else if (comp instanceof FormContainerComponent) {
				FormContainerComponent fc = (FormContainerComponent) comp;
				GridComponent gc = fc.getSelectedComponent();
				if (gc != null)
					return gc;
			}
			else if (comp instanceof GridComponent) {
				GridComponent gc = (GridComponent) comp;
				if (gc.isSelected())
					return gc;
			}
		}
		return null;
	}

	/**
	 * GridViewListener implementation. Forwards any GridView events from any
	 * nested forms to any GridCellListeners on this component.
	 */
	public void gridChanged(GridViewEvent evt) {
		if (evt.getId() == GridViewEvent.EDIT_COMPONENT) {
			if (evt.getComponentEvent() != null)
				fireGridCellEvent(evt.getComponentEvent());

		}
		else if (evt.getId() == GridViewEvent.CELL_SELECTED) {
			if (evt.getComponentEvent() != null)
				fireGridCellEvent(evt.getComponentEvent());
		}
		else {
			if (evt.getComponentEvent() != null)
				fireGridCellEvent(evt.getComponentEvent());
		}
	}

	/**
	 * PostInitialize is called once after all components in a form have been
	 * re-instantiated at runtime (not design time). This gives each property
	 * and component a chance to do some last minute initializations that might
	 * depend on the top level parent. FormComponent simply forwards the call to
	 * any children.
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
	 * depend on the top level parent. FormComponent simply forwards the call to
	 * any children.
	 */
	public void postInitialize(FormPanel panel) {
		super.postInitialize(panel);
		if (this.getBeanDelegate() instanceof Container) {
			_postInitialize(panel, (Container) this.getBeanDelegate());
		}
	}

}
