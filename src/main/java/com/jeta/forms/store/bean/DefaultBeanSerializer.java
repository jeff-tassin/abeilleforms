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

package com.jeta.forms.store.bean;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.LookAndFeel;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETABeanFactory;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.forms.store.properties.FontProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.forms.store.properties.TransformOptionsProperty;

/**
 * This interface defines how a bean is instantiated and intialized from a
 * stored state (i.e. a properties memento). See
 * {@link com.jeta.forms.store.memento.PropertiesMemento}
 * 
 * @author Jeff Tassin
 */
public class DefaultBeanSerializer implements BeanSerializer {
	/**
	 * A map of default beans classes to a instance of each class.
	 * m_default_beans<Class,Component>
	 */
	private static HashMap m_default_beans = new HashMap();

	/**
	 * The current look and feel. We clear the default bean map when the look
	 * and feel changes.
	 */
	private static LookAndFeel m_look_and_feel = null;

	/**
	 * A set of supported property types (Class objects) m_supported_properties<Class>
	 */
	private static HashSet m_supported_properties = new HashSet();

	static {
		/**
		 * load the supported properties map with the types of properties that
		 * can be stored (these are in addition to JETAProperty types)
		 */
		m_supported_properties.add(java.awt.Color.class);
		m_supported_properties.add(java.awt.Font.class);
		m_supported_properties.add(String.class);
		m_supported_properties.add(java.util.Calendar.class);
		m_supported_properties.add(java.util.Date.class);
		/** primitive types */
		m_supported_properties.add(Boolean.class);
		m_supported_properties.add(Byte.class);
		m_supported_properties.add(Character.class);
		m_supported_properties.add(Short.class);
		m_supported_properties.add(Integer.class);
		m_supported_properties.add(Long.class);
		m_supported_properties.add(Float.class);
		m_supported_properties.add(Double.class);
	}

	/**
	 * Return true if the given two values are different.
	 */
	private boolean areDifferent(Object def_value, Object prop_value) {
		boolean bresult = true;
		if (def_value != null) {
			if (prop_value != null) {
				bresult = !(def_value == prop_value || def_value.equals(prop_value) || prop_value.equals(def_value));
			}
		}
		else {
			if (prop_value == null)
				bresult = false;
			else {
				bresult = !(prop_value == def_value || prop_value.equals(def_value));
			}
		}
		return bresult;
	}

	/**
	 * Returns a Java Bean instance that has only default properties. We use
	 * this to test which properties to save when serializing a JETABean. Null
	 * is returned if the component cannot be instantiated.
	 */
	private JETABean getDefaultBean(Class compClass) {
		if (isLookAndFeelChanged()) {
			m_default_beans.clear();
		}

		JETABean jbean = (JETABean) m_default_beans.get(compClass);
		if (jbean == null) {
			try {
				jbean = JETABeanFactory.createBean(compClass.getName(), null, true, false);
				if (jbean == null) {
					Component comp = (Component) compClass.newInstance();
					jbean = new JETABean(comp);
				}
				m_default_beans.put(compClass, jbean);
			} catch (Exception e) {
				FormsLogger.severe(e);
			}
		}
		return jbean;
	}

	/**
	 * Check if the look and feel has changed since the last time we called this
	 * method.
	 */
	private static boolean isLookAndFeelChanged() {
		LookAndFeel lf = javax.swing.UIManager.getLookAndFeel();
		if (m_look_and_feel != lf) {
			m_look_and_feel = lf;
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the given value can be serialized. We don't allow storing
	 * some properties even though they implement Serializable. For example, we
	 * don't store properties that are instances of Containers and/or Components
	 * because those objects don't guarantee Serialiable compatibility with
	 * future versions of Java.
	 * 
	 * @param value
	 *            the value to check for serializability
	 * @return true if the given property value can be serialized.
	 */
	protected boolean isPropertySerializable(Object value) {
		if (value == null)
			return true;

		Class c = value.getClass();
		if (c.isPrimitive())
			return true;

		if (value instanceof JETAProperty) {
			JETAProperty jp = (JETAProperty) value;
			if (jp instanceof TransformOptionsProperty)
				return true;

			return !jp.isTransient();
		}

		if (m_supported_properties.contains(c)) {
			return true;
		}

		if (value instanceof java.awt.Color || value instanceof java.awt.Font || value instanceof String || value instanceof java.util.Calendar
				|| value instanceof java.util.Date)
			return true;

		return false;
	}

	/**
	 * Writes the bean state to a PropertiesMemento
	 */
	public PropertiesMemento writeBean(JETABean jbean) throws FormException {
		try {
			/** for invoking the read methods. Read methods don't take parameters */
			Object[] read_params = new Object[0];
			PropertiesMemento ppm = new PropertiesMemento();

			Component comp = jbean.getDelegate();
			if (comp != null) {
				ppm.setBeanClassName(comp.getClass().getName());
				JETABean default_bean = getDefaultBean(comp.getClass());
				if (default_bean != null) {
					/**
					 * Iterate over all properties in the bean and store those
					 * properties that are read/write and are different from the
					 * default properties for a bean.
					 */
					Collection prop_descriptors = jbean.getPropertyDescriptors();
					Iterator iter = prop_descriptors.iterator();
					while (iter.hasNext()) {
						JETAPropertyDescriptor jpd = (JETAPropertyDescriptor) iter.next();
						try {
							Object def_value = jpd.getPropertyValue(default_bean);
							Object prop_value = jpd.getPropertyValue(jbean);
							/**
							 * only store a property if its value is different
							 * than the default value for the bean
							 */
							if (areDifferent(def_value, prop_value)) {
								if (isPropertySerializable(prop_value) && (prop_value instanceof Serializable)) {
									if (prop_value instanceof Font) {
										/**
										 * need to do this because serialized
										 * fonts have problems on OS X
										 */
										ppm.addProperty(jpd.getName(), new FontProperty((Font) prop_value));
									}
									else if (prop_value instanceof Color) {
										/**
										 * Some Look and Feels use specialized
										 * Color objects that are invalid if the
										 * L&F is not present in the classpath.
										 * We don't form files to be dependent
										 * on any look and feel, so we need to
										 * store color holders instead of Color
										 * objects
										 */
										ppm.addProperty(jpd.getName(), new ColorHolder((Color) prop_value));
									}
									else if (prop_value instanceof TransformOptionsProperty) {
										/**
										 * special handling for transforms.
										 * store the actual value instead
										 */
										ppm.addProperty(jpd.getName(), new Integer(((TransformOptionsProperty) prop_value).getPropertyValue()));
									}
									else {
										ppm.addProperty(jpd.getName(), (Serializable) prop_value);
									}
								}
							}
						} catch (Exception e) {
							FormsLogger.severe(e);
						}
					}

					/**
					 * Always store the component name. I've encountered some
					 * Java Beans that don't define the 'name' property in the
					 * BeanInfo class. Since this architecture depends on the
					 * component name, we need to store it here regardless of
					 * whether it is declared in the bean info
					 */
					try {
						ppm.addProperty("name", comp.getName());
					} catch (Exception e) {
						FormsLogger.severe(e);
					}
				}
			}
			return ppm;
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;

			throw new FormException(e);
		}
	}

	/**
	 * Writes the bean state to the memento.
	 */
	public void writeBean(OutputStream ostream, JETABean jbean) throws FormException {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(ostream);
			oos.writeObject(writeBean(jbean));
		} catch (Exception e) {
			if (e instanceof FormException)
				throw (FormException) e;

			throw new FormException(e);
		}
	}
}
