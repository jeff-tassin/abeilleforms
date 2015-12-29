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

package com.jeta.forms.gui.beans;

import java.util.Collection;
import java.util.HashMap;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.properties.JETAProperty;

/**
 * Used for managing a set of JETAProperties. A BeanProperties is only used
 * during instantiation/initialization of a Java Bean (or more specifically its
 * JETABean container.) It is passed to a BeanFactory during the instantiation
 * of the Java Bean. This allows specialized factories to create JETABeans with
 * customized properties.
 * 
 * @author Jeff Tassin
 */
public class BeanProperties {
	/**
	 * The BeanInfo associated with the Java Bean. This defines the property
	 * descriptors for the bean.
	 */
	private DynamicBeanInfo m_beaninfo;

	/**
	 * A map of default property values for the bean that are not declared by
	 * the bean itself. BeanFactories can provide their own default properties.
	 * m_props<String,JETAProperty> where: String is the property name.
	 * JETAProperty is the property value.
	 */
	private HashMap m_props = new HashMap();

	/**
	 * Creates a <code>BeanProperties</code> instance with the specified
	 * DynamicBeanInfo object.
	 * 
	 * @param beaninfo
	 *            the beaninfo (PropertyDescriptors) associated with these
	 *            properties
	 */
	public BeanProperties(DynamicBeanInfo beaninfo) {
		m_beaninfo = beaninfo;
		FormUtils.safeAssert(beaninfo != null);
	}

	/**
	 * Returns the BeanInfo associated with these properties.
	 * 
	 * @return the underyling BeanInfo
	 */
	public DynamicBeanInfo getBeanInfo() {
		return m_beaninfo;
	}

	/**
	 * Returns a collection of JETAProperty objects that define customized
	 * properties to be associated with a Java Bean.
	 * 
	 * @return a collection of JETAProperty values.
	 */
	public Collection getPropertyValues() {
		return m_props.values();
	}

	/**
	 * Removes the property with the given name.
	 * 
	 * @param propName
	 *            the name of the property to remove.
	 */
	public void removeProperty(String propName) {
		m_props.remove(propName);
		m_beaninfo.removePropertyDescriptor(propName);
	}

	/**
	 * Registers a custom property to be associated with a Java Bean.
	 * 
	 * @param prop
	 *            the custom property to add.
	 */
	public void register(JETAProperty prop) {
		if (prop != null) {
			m_props.put(prop.getName(), prop);
			m_beaninfo.register(new DynamicPropertyDescriptor(prop.getName(), prop.getClass(), prop.isPreferred(), prop.isTransient()));
		}
	}

	/**
	 * Sets the preferred flag for the property with the given name.
	 * 
	 * @param propName
	 *            the name of the property to set.
	 * @param pref
	 *            true if the property should be set to preferred.
	 */
	public void setPreferred(String propName, boolean pref) {
		JETAPropertyDescriptor jpd = m_beaninfo.getPropertyDescriptor(propName);
		if (jpd != null)
			jpd.setPreferred(pref);
	}

}
