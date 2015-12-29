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

import java.lang.reflect.Method;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.store.properties.JETAProperty;

/**
 * A <code>DynamicPropertyDescriptor</code> is similar to a standard Java
 * Beans PropertyDescriptor. However, these descriptors can be dynamically added
 * and removed. For example, some Swing components such as JTables and JLists
 * are almost always contained within a JScrollPane. So, instead of working with
 * a JScrollPane, we add a scrollable property to the Swing components instead.
 * This scrollable property is an example of a dynamic property since it is not
 * defined by the Java Bean. Dynamic properties are defined by a
 * <code>DynamicPropertyDescriptor</code> and are assigned to a bean using
 * <code>BeanFactory</code> instances.
 * 
 * @author Jeff Tassin
 */
public class DynamicPropertyDescriptor implements JETAPropertyDescriptor {
	/** The name of the property associated with this descriptor */
	private String m_name;

	/** The type of the property value associated with this descriptor */
	private Class m_type;

	/**
	 * Flag that indicates if the property associated with this descriptor is
	 * preferred or not.
	 */
	private boolean m_preferred;

	/**
	 * Flag that indicates if this property associated with this descriptor can
	 * be stored. Set to true if the property should not be stored.
	 */
	private boolean m_transient = false;

	/**
	 * Creates a <code>DynamicPropertyDescriptor</code> instance with the
	 * specified name, type and preferred/transient flags.
	 * 
	 * @param name
	 *            the programmatic name for the property
	 * @param type
	 *            the type for the property
	 * @param pref
	 *            set to true if the property is a preferred property.
	 * @param istransient
	 *            set to true if the property value is NOT persistent.
	 */
	public DynamicPropertyDescriptor(String name, Class type, boolean pref, boolean istransient) {
		m_name = name;
		m_type = type;
		m_preferred = pref;
		m_transient = istransient;
	}

	/** @return the value for this property */
	public Object getPropertyValue(JETABean bean) throws FormException {
		return bean.getCustomProperty(getName());
	}

	/** @return the name of the property associated with this descriptor */
	public String getName() {
		return m_name;
	}

	/**
	 * Return the display name for the property.
	 */
	public String getDisplayName() {
		return getName();
	}

	/**
	 * Gets the short description for the property.
	 */
	public String getShortDescription() {
		return getName();
	}

	/**
	 * Gets a PropertyEditor class that has been registered for the property.
	 * Null is returned if no property editor class has been registered. Not
	 * currently used for any dynamic properties.
	 */
	public Class getPropertyEditorClass() {
		return null;
	}

	/** @return the type of the property value associated with this descriptor */
	public Class getPropertyType() {
		return m_type;
	}

	/**
	 * Dynamic properties do not use write methods. Instead, they must implement
	 * updateBean() method of JETAProperty. Always returns null.
	 */
	public Method getWriteMethod() {
		return null;
	}

	/**
	 * The "preferred" flag is used to identify features that are particularly
	 * important for presenting to humans
	 */
	public boolean isPreferred() {
		return m_preferred;
	}

	/**
	 * The "hidden" flag is used to identify features that are intended only for
	 * tool use, and which should not be exposed to humans.
	 */
	public boolean isHidden() {
		return false;
	}

	/**
	 * Returns true if the underlying property should not be stored.
	 */
	public boolean isTransient() {
		return m_transient;
	}

	/**
	 * Returns true if the property is writable. Dynamic properties are always
	 * writable (currently).
	 */
	public boolean isWritable() {
		return true;
	}

	/**
	 * Sets the value for the property associated with this descriptor. This is
	 * potentially a two step process. First, the property value itself is
	 * updated. This may or may not have an effect on the bean - it depends on
	 * the type of property. Second, we tell the property to update the bean.
	 * Some properties require a two step process because of the nature of the
	 * property. For example, a scrollable property has a value that defines
	 * when scroll bars should be displayed. Additionally, we need to tell the
	 * scroll property to update the bean after its value has been set. This
	 * action instantiates the JScrollPane instance and substitutes it for the
	 * actual java bean on the form.
	 * 
	 * @param bean
	 *            the JETABean object whose property we are setting.
	 * @param value
	 *            the value of the property to set.
	 */
	public void setPropertyValue(JETABean bean, Object value) throws FormException {
		JETAProperty old_prop = bean.getCustomProperty(getName());
		FormUtils.safeAssert(old_prop != null);
		if (old_prop != null) {
			if (value instanceof JETAProperty && value.getClass() == old_prop.getClass()) {
				JETAProperty prop = (JETAProperty) value;
				if (!getName().equals(prop.getName())) {
					/** This should never happen */
					System.out.println("DynamicPropertyDescriptor.setPropertyValue  getName(): " + getName() + "  prop.getName(): " + prop.getName());
					FormUtils.safeAssert(false);
				}

				try {
					if (FormUtils.isDesignMode()) {
						/**
						 * We make a copy of the property mainly to support
						 * undo/redo when setting properties in the designer.
						 */
						prop = (JETAProperty) value.getClass().newInstance();
						prop.setValue(value);
						bean.setCustomProperty(getName(), prop);
						prop.updateBean(bean);
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			old_prop.setValue(value);
			old_prop.updateBean(bean);
		}
	}

	/**
	 * Sets this property to preferred.
	 */
	public void setPreferred(boolean bpref) {
		m_preferred = bpref;
	}

}
