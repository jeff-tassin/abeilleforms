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

import javax.swing.border.Border;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.memento.BeanMemento;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.StateRequest;

/**
 * This is a GridComponent wrapper around a Swing component.
 * 
 * @author Jeff Tassin
 */
public class StandardComponent extends GridComponent {
	/**
	 * A border used to add some padding between the component and the grid
	 * lines during design mode.
	 */
	private static Border m_design_border = javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2);

	/**
	 * Creates an unitialized <code>StandardComponent</code> instance.
	 */
	public StandardComponent() {
		if (FormUtils.isDesignMode())
			setBorder(m_design_border);
	}

	/**
	 * Creates a <code>StandardComponent</code> instance with the specified
	 * GridView.
	 * 
	 * @param parentView
	 *            the view that contains this component
	 */
	public StandardComponent(GridView parentView) {
		super(parentView);
		if (FormUtils.isDesignMode())
			setBorder(m_design_border);
	}

	/**
	 * Creates a <code>StandardComponent</code> instance with the specified
	 * JETABean and GridView.
	 * 
	 * @param jbean
	 *            the JETABean object that contains the Swing component.
	 * @param parentView
	 *            the GridView that contains this component.
	 */
	public StandardComponent(JETABean jbean, GridView parentView) {
		super(jbean, parentView);
		setBean(jbean);
		if (FormUtils.isDesignMode())
			setBorder(m_design_border);

		/** ----- for debugging purposes ----------- */
		// if ( jbean == null )
		// setBorder( javax.swing.BorderFactory.createLineBorder(
		// java.awt.Color.red ) );
	}

	/**
	 * Saves this component's state as a memento. Included are the properties of
	 * the underlying Swing component.
	 * 
	 * @return the state of this component as a memento
	 */
	public ComponentMemento getState(StateRequest sr) throws FormException {
		BeanMemento bm = new BeanMemento();
		bm.setCellConstraints(getConstraints().createCellConstraints());
		bm.setComponentClass(getClass().getName());
		JETABean jbean = getBean();
		if (jbean != null) {
			jbean.getState(bm, sr);
		}
		return bm;
	}

	/**
	 * Print for debugging
	 */
	public void print() {
		JETABean jbean = getBean();
		if (jbean == null) {
			System.out.println("StandardComponent  name = " + getName() + "   hash: " + hashCode() + "   bean:  null");
		}
		else {
			System.out.println("StandardComponent  name = " + getName() + "   hash: " + hashCode() + "   bean: " + jbean.getDelegate());
		}
	}

	/**
	 * PostInitialize is called once after all components in a form have been
	 * re-instantiated and the state has been set at runtime (not design time).
	 * This gives each property and component a chance to do some last minute
	 * initializations that might depend on the top level parent.
	 */
	public void postInitialize(FormPanel panel) {
		JETABean jbean = getBean();
		if (jbean != null) {
			jbean.postInitialize(panel);
		}
	}

	/**
	 * Sets the JETABean associated with this component. The JETABean contains
	 * the Java Bean that is displayed on the form.
	 * 
	 * @param jetabean
	 *            the JETABean instance.
	 */
	protected void setBean(JETABean jetabean) {
		FormUtils.safeAssert(getComponentCount() == 0);
		super.setBean(jetabean);
		removeAll();
		setLayout(new BorderLayout());
		if (jetabean != null) {
			add(jetabean, BorderLayout.CENTER);
		}
	}

	/**
	 * Sets the state of this component to a previously saved state. This
	 * includes adding the underlying Java bean (i.e. Swing component) to this
	 * component.
	 * 
	 * @param memento
	 *            the state of a StandardComponent
	 */
	public void setState(ComponentMemento state) throws FormException {
		try {
			BeanMemento bm = (BeanMemento) state;

			/**
			 * this is required when running outside the designer. it is needed
			 * to support custom swing components that are scrollable
			 */
			if (FormUtils.isRuntime()) {
				JETABeanFactory.tryRegisterCustomFactory(bm.getBeanClass(), true);
			}

			JETABean jbean = JETABeanFactory.createBean(bm.getBeanClass(), null, false, true);
			if (jbean == null) {
				jbean = new JETABean();
			}

			if (jbean != null) {
				setBean(jbean);
				jbean.setState(bm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
