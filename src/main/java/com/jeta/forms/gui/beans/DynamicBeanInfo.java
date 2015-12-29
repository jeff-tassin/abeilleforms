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

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.properties.JETAProperty;

/**
 * A <code>DynamicBeanInfo</code> is a BeanInfo for a Java Bean that also has
 * the ability to dynamically add and remove PropertyDescriptors for that bean.
 * 
 * @author Jeff Tassin
 */
public class DynamicBeanInfo {
	/**
	 * The actual BeanInfo for the Java Bean.
	 */
	private BeanInfo m_delegate;

	/**
	 * An ordered set of JETAPropertyDescriptor objects m_props<String,PropertyDescriptor>
	 * where String is the property name.
	 */
	private TreeMap m_props = new TreeMap();

	/**
	 * Creates a <code>DynamicBeanInfo</code> instance with the specified
	 * BeanInfo delegate.
	 * 
	 * @param delegate
	 *            the actual BeanInfo for the associated Java Bean class.
	 */
	public DynamicBeanInfo(BeanInfo delegate) throws IntrospectionException, IllegalAccessException {
		this(delegate, null);
	}

	/**
	 * Creates a <code>DynamicBeanInfo</code> instance with the specified
	 * BeanInfo delegate and adds a set of custom properties for the associated
	 * Java Bean class.
	 * 
	 * @param delegate
	 *            the default BeanInfo for the bean.
	 * @param customProps
	 *            a collection of JETAProperty objects that are in addition to
	 *            the default properties for the bean.
	 */
	public DynamicBeanInfo(BeanInfo delegate, Collection customProps) throws IntrospectionException, IllegalAccessException {
		m_delegate = delegate;

		PropertyDescriptor[] pds = delegate.getPropertyDescriptors();
		for (int index = 0; index < pds.length; index++) {
			StandardPropertyDescriptor std_pds = new StandardPropertyDescriptor(pds[index]);
			m_props.put(std_pds.getName(), std_pds);
		}

		if (customProps != null) {
			Iterator iter = customProps.iterator();
			while (iter.hasNext()) {
				JETAProperty prop = (JETAProperty) iter.next();
				FormUtils.safeAssert(prop.getName() != null);
				m_props.put(prop.getName(), new DynamicPropertyDescriptor(prop.getName(), prop.getClass(), prop.isPreferred(), prop.isTransient()));
			}
		}
		/** this is not used in the designer */
		removePropertyDescriptor("debugGraphicsOptions");
	}

	/**
	 * BeanInfo implementation. Just forward the call to the delegate.
	 */
	public int getDefaultPropertyIndex() {
		return m_delegate.getDefaultPropertyIndex();
	}

	/**
	 * BeanInfo implementation. Just forward the call to the delegate.
	 */
	public Image getIcon(int i) {
		return m_delegate.getIcon(i);
	}

	/**
	 * BeanInfo implementation. Just forward the call to the delegate.
	 */
	public BeanDescriptor getBeanDescriptor() {
		return m_delegate.getBeanDescriptor();
	}

	/**
	 * BeanInfo implementation. Just forward the call to the delegate.
	 */
	public BeanInfo[] getAdditionalBeanInfo() {
		return m_delegate.getAdditionalBeanInfo();
	}

	/**
	 * BeanInfo implementation. Just forward the call to the delegate.
	 */
	public MethodDescriptor[] getMethodDescriptors() {
		return m_delegate.getMethodDescriptors();
	}

	/**
	 * Return the property descriptor with the given name. Null is returned if
	 * the descriptor is not found.
	 * 
	 * @return a the property descriptor with the given name.
	 */
	public JETAPropertyDescriptor getPropertyDescriptor(String propName) {
		return (JETAPropertyDescriptor) m_props.get(propName);
	}

	/**
	 * Returns the property descriptors for the Java Bean class associated with
	 * this bean information.
	 * 
	 * @return a collection of JETAPropertyDescriptor objects
	 */
	public Collection getPropertyDescriptors() {
		return m_props.values();
	}

	/**
	 * Registers a property descriptor for this info object. This allows us to
	 * add properties dynamically.
	 * 
	 * @param prop
	 *            the property descriptor to register with this bean
	 *            information.
	 */
	void register(JETAPropertyDescriptor prop) {
		if (prop != null) {
			m_props.put(prop.getName(), prop);
		}
	}

	/**
	 * Removes a property descriptor from this info object. This allows us to
	 * remove properties that are either deprecated or irrelevent for the
	 * designer.
	 * 
	 * @param propName
	 *            the name of the property descriptor to remove.
	 */
	void removePropertyDescriptor(String propName) {
		m_props.remove(propName);
	}
}
