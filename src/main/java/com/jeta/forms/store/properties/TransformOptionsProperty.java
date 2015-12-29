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

package com.jeta.forms.store.properties;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * This class is used to manage a set if integer options for a bean property -
 * for example, the options to set the horizontal alignment for the text in a
 * JLabel. In the designer we don't want to display integer values to the user,
 * instead we provide table of String descriptions that map to an associated
 * integer value. The user is shown the strings in a combo. When the user makes
 * a selection, the corresponding integer value is set in the bean.
 * 
 * @author Jeff Tassin
 */
public class TransformOptionsProperty extends JETAProperty implements TransformProperty {
	static final long serialVersionUID = -1652327155513571580L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 3;

	/**
	 * The set of options available to the user m_options<String,Integer> where
	 * the String is the human readable name for the property value Integer is
	 * the actual property value.
	 */
	private transient HashMap m_options = new HashMap();

	/**
	 * The default property value
	 */
	private int m_default_value;

	/**
	 * The getter method for the property
	 */
	private Method m_getter;

	/**
	 * The name of the getter method
	 */
	private String m_getter_name;

	/**
	 * The setter method for the property
	 */
	private Method m_setter;

	/**
	 * The name of the setter method
	 */
	private String m_setter_name;

	/**
	 * The bean this property is bound to.
	 */
	private transient JETABean m_bean;

	/**
	 * Creates an uninitialized <code>TransformOptionsProperty</code>
	 */
	public TransformOptionsProperty() {

	}

	/**
	 * Creates a <code>TransformOptionsProperty</code> with the specified
	 * property name and methods.
	 * 
	 * @param propName
	 *            the name of the property associated with this transform.
	 * @param getMethod
	 *            the name of the bean method to get the property value
	 * @param setMethdo
	 *            the name of the bean method to set the property value
	 * @param options
	 *            an Nx2 array of options for this transform. The first column
	 *            is the integer value for the property. The second column is a
	 *            human readable name that will be displayed in the designer
	 *            properties table.
	 */
	public TransformOptionsProperty(String propName, String getMethod, String setMethod, Object[][] options) {
		super(propName);
		m_getter_name = getMethod;
		m_setter_name = setMethod;
		for (int index = 0; index < options.length; index++) {
			String option = (String) options[index][0];
			Integer value = (Integer) options[index][1];
			if (index == 0)
				m_default_value = value.intValue();

			m_options.put(option, value);
		}
	}

	/**
	 * Returns true if this property equals the given object.
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		/** @JMT fix. this is a hack. */
		if (obj instanceof Integer) {
			return (((Integer) obj).intValue() == getPropertyValue());
		}
		else if (obj instanceof TransformOptionsProperty) {
			TransformOptionsProperty prop = (TransformOptionsProperty) obj;
			return (prop.getPropertyValue() == getPropertyValue());
		}
		else
			return false;
	}

	/**
	 * Returns the JETABean associated with this property. The JETABean contains
	 * the underlying Java bean.
	 * 
	 * @return the bean this property is bound to.
	 */
	public JETABean getBean() {
		return m_bean;
	}

	/**
	 * Returns the human readable string for the current property value of the
	 * Java bean associated with this object.
	 * 
	 * @return the selected value
	 */
	public Object getCurrentItem() {
		int pval = getPropertyValue();
		Iterator iter = m_options.keySet().iterator();
		while (iter.hasNext()) {
			String option = (String) iter.next();
			Integer ival = (Integer) m_options.get(option);
			if (ival.intValue() == pval)
				return option;
		}
		return null;
	}

	/**
	 * Return a collection of human readable options (Strings) for the
	 * associated property.
	 * 
	 * @return the set of options available to the user
	 */
	public Collection getOptions() {
		return m_options.keySet();
	}

	/**
	 * Returns the current property value of the Java bean associated with this
	 * property
	 * 
	 * @return the current property value.
	 */
	public int getPropertyValue() {
		try {
			Integer ival = (Integer) m_getter.invoke(getBean().getDelegate(), new Object[0]);
			return ival.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return m_default_value;
		}
	}

	/**
	 * Returns the setter method for this property
	 * 
	 * @return the setter method for the property
	 */
	public Method getWriteMethod() {
		return m_setter;
	}

	/**
	 * Returns true if this object is not serialiable.
	 * 
	 * @return true if this object is not serializable
	 */
	public boolean isTransient() {
		return true;
	}

	/**
	 * Sets the bean this property is bound to. This must be called right after
	 * initalization or de-serialization because the bean cannot get/set the
	 * properties on the bean without a reference to it.
	 */
	public void setBean(JETABean bean) {
		try {
			m_bean = bean;
			if (bean != null) {
				Class c = bean.getDelegate().getClass();
				m_getter = c.getMethod(m_getter_name, new Class[0]);
				m_setter = c.getMethod(m_setter_name, new Class[] { int.class });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets this property to the specified value. If required, the value is
	 * first transformed to an integer. The associated Java bean is updated.
	 */
	public void setValue(Object option) {
		try {
			Integer ival = null;
			if (option instanceof String) {
				String sval = (String) option;
				ival = (Integer) m_options.get(sval);
				if (ival == null) {
					/**
					 * try to convert to an integer. this can happen when
					 * storing forms to XML. The option value will be restored
					 * as a String.
					 */
					try {
						ival = Integer.valueOf((String) option);
					} catch (Exception e) {
						// ignore here
					}
				}
			}
			else if (option instanceof Integer) {
				ival = (Integer) option;
			}
			else if (option instanceof TransformOptionsProperty) {
				ival = new Integer(((TransformOptionsProperty) option).getPropertyValue());
			}

			if (ival != null) {
				m_setter.invoke(getBean().getDelegate(), new Object[] { ival });
			}
		} catch (Exception e) {
			if (option != null) {
				System.out.println("TransformOptionsProperty.setValue failed: " + option.getClass());
			}
			e.printStackTrace();
		}
	}

	/**
	 * Return the number of options in this property.
	 * 
	 * @return the number of options in this property
	 */
	public int size() {
		return m_options.size();
	}

	/**
	 * Updates the given bean with the latest value of this property.
	 */
	public void updateBean(JETABean jbean) {
		setBean(jbean);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
	}

	public String toString() {
		return "transform value: " + getPropertyValue();
	}
}
