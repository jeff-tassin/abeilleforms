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

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.support.PropertyMap;

/**
 * This class is used to store properties for a Java Bean. This class stores
 * both standard and custom properties.
 * 
 * @author Jeff Tassin
 */
public class PropertiesMemento extends AbstractJETAPersistable {
	static final long serialVersionUID = -3299575217906477613L;

	public static final int VERSION = 2;

	/**
	 * The Component class associated with this memento. This is the class name
	 * of the Java bean. For example: javax.swing.JButton
	 */
	private String m_class_name;

	/**
	 * A map of property names to their value: m_standard_props<String,Object>
	 * The key is the name of the property. The value is the property value.
	 * This value must be serializable or it will not be stored.
	 */
	private PropertyMap m_props = new PropertyMap();

	/**
	 * Creates an uninitialized <code>PropertiesMemento</code> instance.
	 */
	public PropertiesMemento() {

	}

	/**
	 * Creates a <code>PropertiesMemento</code> instance with the given Java
	 * bean class name.
	 * 
	 * @param className
	 *            the name of the Java bean class associated with this memento.
	 */
	public PropertiesMemento(String className) {
		m_class_name = className;
	}

	/**
	 * Returns the class name of the Java Bean component associated with this
	 * memento.
	 * 
	 * @return the Java bean class name associated with this memento.
	 */
	public String getBeanClassName() {
		return m_class_name;
	}

	/**
	 * Adds a property value to this memento.
	 * 
	 * @param name
	 *            the name of the property to add to this memento.
	 * @param value
	 *            the property value.
	 */
	public void addProperty(String name, Serializable value) {
		m_props.put(name, value);
	}

	/**
	 * Return true if this memento contains a value for the given property name.
	 * 
	 * @param propName
	 *            the name of the property
	 * @return true if this object contains a value for the property with the
	 *         given name.
	 */
	public boolean containsProperty(String propName) {
		return m_props.containsKey(propName);
	}

	/**
	 * Return the value associated with the standard property <i>name</i>. If
	 * the <i>name</i> property is not contained by this memento, null is
	 * returned.
	 * 
	 * @return the value of the name property.
	 */
	public String getComponentName() {
		Object obj = getPropertyValue("name");
		if (obj != null)
			return obj.toString();
		else
			return null;
	}

	/**
	 * Return the names of the properties contained by this memento.
	 */
	public Collection getPropertyNames() {
		return m_props.keySet();
	}

	/**
	 * Returns the value for the given property
	 * 
	 * @param propName
	 *            the name of the property
	 * @return the value for the property. Null if the property is not contained
	 *         by this memento.
	 */
	public Object getPropertyValue(String propName) {
		return m_props.get(propName);
	}

	/**
	 * Prints this object to the console
	 */
	public void print() {
		System.out.println("................ properties memento: " + getBeanClassName() + "   ..............");
		java.util.Iterator iter = m_props.keySet().iterator();
		while (iter.hasNext()) {
			String pname = (String) iter.next();
			System.out.println("          " + pname + " = " + m_props.get(pname));
		}
	}

	/**
	 * Sets the class name of the Java Bean associated with this memento.
	 * 
	 * @param cname
	 *            the class name of the Java Bean to set.
	 */
	public void setBeanClassName(String cname) {
		m_class_name = cname;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_class_name = (String) in.readObject("classname");
		if (version <= 1) {
			Object obj = in.readObject("properties");
			if (obj instanceof HashMap)
				m_props = new PropertyMap((HashMap) obj);
			else
				m_props = (PropertyMap) obj;

			if (m_props == null)
				m_props = new PropertyMap();

			HashMap custom_props = (HashMap) in.readObject("customproperties");
			if (custom_props != null) {
				Iterator iter = custom_props.keySet().iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					m_props.put(key, custom_props.get(key));
				}
			}
		}
		else {
			Object obj = in.readObject("properties");
			if (obj instanceof HashMap)
				m_props = new PropertyMap((HashMap) obj);
			else
				m_props = (PropertyMap) obj;
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("classname", m_class_name);
		out.writeObject("properties", m_props);
	}

}
