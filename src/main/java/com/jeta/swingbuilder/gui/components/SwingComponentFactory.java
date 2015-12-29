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
import java.lang.reflect.Constructor;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.StandardComponentFactory;
import com.jeta.forms.gui.form.FormContainerComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.open.registry.JETARegistry;

/**
 * A factory for creating swing components
 */
public class SwingComponentFactory extends StandardComponentFactory {

	/**
	 * The class name of the component to create
	 */
	private String m_comp_class;

	/**
	 * Parameter types for the constructor of the component class.
	 */
	private Class[] m_params = new Class[0];

	/**
	 * Object values for the constructor of the component class
	 */
	private Object[] m_args = new Object[0];

	/**
	 * ctor
	 */
	public SwingComponentFactory(ComponentSource compSrc, String compClass) {
		super(compSrc);
		m_comp_class = compClass;
	}

	/**
	 * ctor
	 */
	public SwingComponentFactory(ComponentSource compSrc, String compClass, Class[] params, Object[] args) {
		super(compSrc);
		m_comp_class = compClass;
		m_params = params;
		m_args = args;
	}

	public GridComponent createComponent(String compName, GridView view) throws FormException {
		try {
			JETABean jetabean = JETABeanFactory.createBean(getComponentClass(), compName, true, true);
			if (jetabean == null) {
				BeanManager bm = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
				Class c = bm.getBeanClass(getComponentClass());
				Constructor ctor = c.getConstructor(m_params);
				Component comp = (Component) ctor.newInstance(m_args);
				comp.setName(compName);
				return super.createComponent(comp, view);
			}
			else {
				if (jetabean.getDelegate() instanceof javax.swing.JTabbedPane) {
					FormContainerComponent gc = new FormContainerComponent(jetabean, view);
					installHandlers(gc);
					return gc;
				}
				else {
					StandardComponent gc = new StandardComponent(jetabean, view);
					installHandlers(gc);
					return gc;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FormException(e);
		}
	}

	public String getComponentClass() {
		return m_comp_class;
	}
}
