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

package com.jeta.forms.gui.beans.factories;

import java.awt.Component;

import javax.swing.JScrollPane;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.gui.beans.DynamicBeanInfo;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.ScrollBarsProperty;
import com.jeta.forms.store.properties.effects.PaintProperty;

/**
 * Factory for instantiating a nested child form. All forms are defined by
 * FormComponent objects. FormComponent objects are parents for GridViews which
 * is where the properties for the form are defined. See
 * {@link com.jeta.forms.gui.form.GridView}.
 * 
 * @author Jeff Tassin
 */
public class GridViewBeanFactory implements BeanFactory {

	/**
	 * BeanFactory implementation. Creates a JETABean and if specified, its
	 * GridView component.
	 * 
	 * @param compName
	 *            the name to assign to this component by calling
	 *            Component.setName
	 * @param instantiateBean
	 *            set to true if the underlying Java Bean should be instantiated
	 *            as well. During deserialization we don't want to do this
	 *            because the BeanDeserializer will create the JavaBean for us.
	 * @param setDefaults
	 *            sets default properties for the bean. If false, no properties
	 *            will be set (e.g. the text for a JButton)
	 * @return the newly instantiated JETABean
	 */
	public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException {
		Component comp = null;
		if (instantiateBean) {
			comp = new GridView();
			comp.setName(compName);
		}

		DynamicBeanInfo beaninfo = JComponentBeanFactory.createBeanInfo(GridView.class);
		/** now define the properties for a form */
		BeanProperties default_props = new BeanProperties(beaninfo);
		defineProperties(default_props);
		JETABean bean = new JETABean(comp, default_props);
		return bean;
	}

	/**
	 * Defines the custom properties for a GridView.
	 * 
	 * @param props
	 *            used to register any custom properties.
	 */
	public void defineProperties(BeanProperties props) {
		props.register(new CompoundBorderProperty());
		props.register(new PaintProperty());
		props.register(new ScrollBarsProperty(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	}

}
