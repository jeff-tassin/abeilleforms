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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.logger.FormsLogger;

/**
 * A <code>StandardPropertyDescriptor</code> is basically just a proxy for a
 * standard Java Bean PropertyDescriptor.
 * 
 * @author Jeff Tassin
 */
public class StandardPropertyDescriptor implements JETAPropertyDescriptor {
	/**
	 * The underlying PropertyDescriptor for a Java Bean property.
	 */
	private PropertyDescriptor m_delegate;

	/**
	 * A simple value array so we don't have to re-instantiate every time we
	 * call setPropertyValue.
	 */
	private Object[] m_set_value = new Object[1];

	/**
	 * Creates a <code>StandardPropertyDescriptor</code> instance with the
	 * specified PropertyDescriptor delegate.
	 */
	public StandardPropertyDescriptor(PropertyDescriptor delegate) throws IntrospectionException, IllegalAccessException {
		m_delegate = delegate;
	}

	/**
	 * Equals implementation. Just forward to the delegate
	 */
	public boolean equals(Object obj) {
		return m_delegate.equals(obj);
	}

	/**
	 * Return the display name for the property.
	 */
	public String getDisplayName() {
		return m_delegate.getDisplayName();
	}

	/**
	 * Return the name of the property.
	 */
	public String getName() {
		return m_delegate.getName();
	}

	/**
	 * Returns the value of the associated property in the given bean.
	 * 
	 * @return the value for property
	 */
	public Object getPropertyValue(JETABean bean) throws FormException {
		Method getter = m_delegate.getReadMethod();
		try {
			Object value = null;
			if (getter != null) {
				Class[] paramTypes = getter.getParameterTypes();
				Object[] args = new Object[paramTypes.length];

				assert (bean != null);
				assert (bean.getDelegate() != null);
				value = getter.invoke(bean.getDelegate(), args);
			}
			return value;
		} catch (Exception e) {
			System.out.println("StandardPropertyDescriptor.failed for property: " + m_delegate.getName() + "   declaringClass: " + getter.getDeclaringClass()
					+ "   " + bean.getDelegate());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a PropertyEditor class that has been registered for the property.
	 * Null is returned if no property editor class has been registered.
	 */
	public Class getPropertyEditorClass() {
		return m_delegate.getPropertyEditorClass();
	}

	/**
	 * Returns the class object for the property.
	 */
	public Class getPropertyType() {
		return m_delegate.getPropertyType();
	}

	/**
	 * Gets the short description for the property.
	 */
	public String getShortDescription() {
		return m_delegate.getShortDescription();
	}

	/**
	 * Returns a method object used to set the property value. Null is returned
	 * if the property is not writable.
	 */
	public Method getWriteMethod() {
		return m_delegate.getWriteMethod();
	}

	/**
	 * The "hidden" flag is used to identify features that are intended only for
	 * tool use, and which should not be exposed to humans.
	 */
	public boolean isHidden() {
		return m_delegate.isHidden();
	}

	/**
	 * The "preferred" flag is used to identify features that are particularly
	 * important for presenting to humans
	 */
	public boolean isPreferred() {
		return m_delegate.isPreferred();
	}

	/**
	 * Returns true if the underlying property should not be stored.
	 */
	public boolean isTransient() {
		/** all standard properties are not transient */
		return false;
	}

	/**
	 * Returns true if the property is writable.
	 */
	public boolean isWritable() {
		return (m_delegate.getWriteMethod() != null);
	}

	/**
	 * Sets this property to preferred.
	 */
	public void setPreferred(boolean bpref) {
		m_delegate.setPreferred(bpref);
	}

	/**
	 * Sets the value for this property on the given bean.
	 * 
	 * @param bean
	 *            the JETABean that contains the property to set
	 * @param value
	 *            the value of the property
	 */
	public void setPropertyValue(JETABean bean, Object value) throws FormException {
		try {
			Method setter = m_delegate.getWriteMethod();
			if (setter != null) {
				m_set_value[0] = value;
				setter.invoke(bean.getDelegate(), m_set_value);
			}
		} catch (Exception e) {
			FormsLogger.debug("failed to set property: " + getName());
			FormsLogger.debug(e);
		}
	}

}
