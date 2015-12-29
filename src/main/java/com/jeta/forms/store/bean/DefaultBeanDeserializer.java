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

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jeta.forms.beanmgr.BeanManager;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.beans.JETAPropertyDescriptor;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.PropertiesMemento;
import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.forms.store.properties.FontProperty;
import com.jeta.open.registry.JETARegistry;

/**
 * An implementation of BeanDeserializer that defines how a bean is instantiated
 * and intialized from a PropertiesMemento instance. See
 * {@link com.jeta.forms.store.memento.PropertiesMemento}
 * 
 * @author Jeff Tassin
 */
public class DefaultBeanDeserializer implements BeanDeserializer {
	/**
	 * The properties that are used as a basis for creating and initializing a
	 * JETABean.
	 */
	private PropertiesMemento m_memento;

	/**
	 * Creates a <code>DefaultBeanDeserializer</code> instance with the
	 * specified PropertiesMemento object.
	 * 
	 * @param memento
	 *            the properties memento that will be used to create and
	 *            initialize java beans.
	 */
	public DefaultBeanDeserializer(PropertiesMemento memento) {
		m_memento = memento;
	}

	/**
	 * Returns the PropertiesMemento object that is used to create and
	 * initialize java bean instances.
	 * 
	 * @return the properties memento associated with this deserializer.
	 */
	public PropertiesMemento getProperties() {
		return m_memento;
	}

	/**
	 * Creates an unitialized Java Bean component using the properties memento
	 * associated with this deserializer.
	 * 
	 * @return a default Java Bean component.
	 */
	public Component createBean() throws FormException {
		try {
			Class bean_class = null;

			try {
				/**
				 * first, try the bean manager. This is for Java beans that were
				 * imported in the designer
				 */
				BeanManager bmgr = (BeanManager) JETARegistry.lookup(BeanManager.COMPONENT_ID);
				if (bmgr != null) {
					bean_class = bmgr.getBeanClass(m_memento.getBeanClassName());
				}
			} catch (Exception e) {
				FormsLogger.severe(e);
			}

			if (bean_class == null) {
				bean_class = Class.forName(m_memento.getBeanClassName());
			}

			Component comp = (Component) bean_class.newInstance();
			return comp;
		} catch (Exception e) {
			FormsLogger.severe(e);

			if (e instanceof FormException)
				throw (FormException) e;
			throw new FormException(e);
		}
	}

	/**
	 * Sets the property values of a java bean using the PropertiesMemento
	 * contained by this deserializer. Both standard and custom property values
	 * are set.
	 * 
	 * @param jbean
	 *            the container for the Java bean that will be intialize
	 */
	public void initializeBean(JETABean jbean) throws FormException {
		if (jbean == null)
			return;

		try {
			Component comp = jbean.getDelegate();
			/**
			 * Special handling for JList because we need to set the list model
			 * to DefaultListModel this is for the custom ItemsProperty. It
			 * might be better to provide specialized JList and JTable
			 * deserializers in the future.
			 */
			if (comp instanceof javax.swing.JList) {
				((javax.swing.JList) comp).setModel(new javax.swing.DefaultListModel());
			}
			else if (comp instanceof JTable) {
				JTable table = (JTable) comp;
				Object model = table.getModel();
				/**
				 * Add a few default columns and rows to give the table a little
				 * more identity on the form. Do this only if this is not a
				 * specialized type of JTable where the table model might be set
				 * in the derived class.
				 */
				if ((model instanceof DefaultTableModel)) {
					DefaultTableModel tmodel = (DefaultTableModel) model;
					if (tmodel.getColumnCount() == 0) {
						tmodel.addColumn("A");
						tmodel.addColumn("B");
						tmodel.addRow(new Object[] { "", "" });
						tmodel.addRow(new Object[] { "", "" });
					}
				}
			}

			if (comp != null) {
				Collection jeta_pds = jbean.getPropertyDescriptors();
				Iterator iter = jeta_pds.iterator();
				while (iter.hasNext()) {
					JETAPropertyDescriptor jpd = (JETAPropertyDescriptor) iter.next();
					try {
						if (m_memento.containsProperty(jpd.getName())) {
							Object prop_value = m_memento.getPropertyValue(jpd.getName());
							// System.out.println( "default bean deserializer
							// prop
							// descriptor: " + jpd.getName() + " mementovalue: "
							// +
							// prop_value + " comp: " + comp.getClass() );

							if (prop_value instanceof FontProperty) {
								/**
								 * we need to do this because de-serialized
								 * fonts from Linux(and probabaly Windows) have
								 * problems on OS X
								 */
								prop_value = ((FontProperty) prop_value).getFont();
							}
							else if (prop_value instanceof ColorHolder) {
								/**
								 * Some Look and Feels use specialized Color
								 * objects that are invalid if the L&F is not
								 * present in the classpath. We don't form files
								 * to be dependent on any look and feel, so we
								 * need to store color holders instead of Color
								 * objects
								 */
								prop_value = ((ColorHolder) prop_value).getColor();
							}

							/**
							 * We allow properties to be stored as strings in
							 * the memento. So check if it can be converted to
							 * the appropriate type (only support Primitive
							 * conversions for now )
							 */
							jpd.setPropertyValue(jbean, convertValue(jpd.getPropertyType(), prop_value));
						}
					} catch (Exception e) {
						FormsLogger.debug(e);
					}
				}

				/**
				 * Always store the component name. I've encountered some Java
				 * Beans that don't define the 'name' property in the BeanInfo
				 * class. Since this architecture depends on the component name,
				 * we need to store it here regardless of whether it is declared
				 * in the bean info
				 */
				try {
					if (m_memento.containsProperty("name"))
						comp.setName((String) m_memento.getPropertyValue("name"));
				} catch (Exception e) {
					FormsLogger.severe(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			if (e instanceof FormException)
				throw (FormException) e;
			throw new FormException(e);
		}
	}

	/**
	 * Converts a stored property value to the correct type in the bean. This is
	 * mainly to support storing Java primitives as String objects (when storing
	 * to XML).
	 */
	private Object convertValue(Class c, Object prop_value) {

		String propclass = prop_value == null ? "null" : prop_value.getClass().getName();
		if (prop_value instanceof String) {
			String sval = (String) prop_value;
			if (c == Boolean.class || c == boolean.class)
				return Boolean.valueOf(sval);
			else if (c == Byte.class || c == byte.class)
				return Byte.valueOf(sval);
			else if (c == Character.class || c == char.class)
				return new Character(sval.length() == 0 ? '\0' : sval.charAt(0));
			else if (c == Short.class || c == short.class)
				return Short.valueOf(sval);
			else if (c == Integer.class || c == int.class)
				return Integer.valueOf(sval);
			else if (c == Long.class || c == long.class)
				return Long.valueOf(sval);
			else if (c == Float.class || c == float.class)
				return Float.valueOf(sval);
			else if (c == Double.class || c == double.class)
				return Double.valueOf(sval);
		}
		return prop_value;
	}

}
