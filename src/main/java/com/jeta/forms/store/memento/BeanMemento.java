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

package com.jeta.forms.store.memento;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>BeanMemento</code> is used to store the state for a JETABean object
 * and its properties. This includes the standard properties for the underlying
 * Java bean as well as any custom properties.
 * 
 * @author Jeff Tassin
 */
public class BeanMemento extends ComponentMemento {
	static final long serialVersionUID = -92122133275350884L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 2;

	/**
	 * Currently, this is "com.jeta.forms.gui.beans.JETABean". It may change in
	 * the future.
	 */
	private String m_jetabean_class;

	/**
	 * The class name of the Swing-based Java bean whose state is represented
	 * here
	 */
	private String m_bean_class;

	/**
	 * The bean properties(values). This includes standard as well as custom
	 * properties.
	 */
	private PropertiesMemento m_properties;

	/**
	 * The bean state in XML form
	 * 
	 * @deprecated only supported in version 1 of this class. Replaced by
	 *             <code>m_properties</code>
	 */
	@Deprecated
	private byte[] m_bean_xml;

	/**
	 * A collection of JETAProperty objects. These are custom properties
	 * associated with the bean.
	 * 
	 * @deprecated only supported in version 1 of this class. Replaced by
	 *             <code>m_properties</code>
	 */
	@Deprecated
	private Collection m_custom_properties;

	/**
	 * Return true if this memento contains a value for the given property name.
	 * 
	 * @param propName
	 *            the name of the property
	 * @return true if this object contains a value for the property with the
	 *         given name.
	 */
	public boolean containsProperty(String propName) {
		if (m_properties != null)
			return m_properties.containsProperty(propName);
		else
			return false;
	}

	/**
	 * Returns a collection of custom properties associated.
	 * 
	 * @return a collection of JETAProperty objects. These are custom properties
	 *         associated with the bean.
	 * @deprecated only suupported in version 1 of this class.
	 */
	@Deprecated
	public Collection getCustomProperties() {
		return m_custom_properties;
	}

	/**
	 * Currently returns "com.jeta.forms.gui.beans.JETABean"
	 * 
	 * @return the JETABean wrapper class
	 */
	public String getJETABeanClass() {
		return m_jetabean_class;
	}

	/**
	 * The class name of the Swing-based Java bean whose state is represented
	 * here
	 */
	public String getBeanClass() {
		return m_bean_class;
	}

	/**
	 * @deprecated Use <code>getProperties</code> instead.
	 */
	@Deprecated
	public byte[] getBeanXML() {
		return m_bean_xml;
	}

	/**
	 * Returns the bean properties that are stored in this memento.
	 * 
	 * @return the bean properties.
	 */
	public PropertiesMemento getProperties() {
		return m_properties;
	}

	/**
	 * Method used only for testing
	 */
	public void print() {
		System.out.println("     BeanMemento state.................. ");
		System.out.println("           jetabean: " + m_jetabean_class);
		System.out.println("           bean_class: " + m_bean_class);
	}

	/**
	 * Sets the custom properties (i.e.JETAProperty objects) for this bean.
	 * 
	 * @deprecated
	 * @param props
	 *            a collection of JETAProperty objects
	 */
	@Deprecated
	public void setCustomProperties(Collection props) {
		m_custom_properties = props;
	}

	/**
	 * Sets the class name of the Swing-based Java bean whose state is
	 * represented here
	 */
	public void setBeanClass(String beanClass) {
		m_bean_class = beanClass;
	}

	/**
	 * @return the JETABean wrapper class
	 */
	public void setJETABeanClass(String className) {
		m_jetabean_class = className;
	}

	/**
	 * @deprecated Use <code>setProperties</code> instead.
	 */
	@Deprecated
	public void setBeanXML(byte[] xml) {
		m_bean_xml = xml;
	}

	/**
	 * Sets the bean properties.
	 */
	public void setProperties(PropertiesMemento props) {
		m_properties = props;
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_jetabean_class = in.readString("jetabeanclass");
		m_bean_class = in.readString("beanclass");

		if (version == 1) {
			m_bean_xml = (byte[]) in.readObject("bean_xml");
			m_custom_properties = (Collection) in.readObject("custom_properties");
			m_properties = null;
		}
		else {
			Object props = in.readObject("beanproperties");
			if (props instanceof byte[]) {
				/** deprecated support */
				ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) props);
				ObjectInputStream ois = new ObjectInputStream(bis);
				m_properties = (PropertiesMemento) ois.readObject();
			}
			else if (props instanceof PropertiesMemento) {
				m_properties = (PropertiesMemento) props;
			}
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(ComponentMemento.class));
		out.writeVersion(VERSION);
		out.writeObject("jetabeanclass", m_jetabean_class);
		out.writeObject("beanclass", m_bean_class);
		out.writeObject("beanproperties", m_properties);
	}

}
