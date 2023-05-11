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
import java.util.Collection;
import java.util.Iterator;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.jml.JMLObjectOutput;

/**
 * A <code>JETAProperty</code> is the base class for all <i>custom</i>
 * properties for a Java bean. A custom property does more than simply hold a
 * value. They allow a JETABean to add additional properties to a Java bean that
 * are not defined in the BeanInfo. JETAProperties can also have custom logic
 * for setting the value on a bean as well as versionable serialization. For
 * example, some Swing components such as JTree and JList are generally
 * contained in a JScrollPane. So a <i>scrollable</i> custom property was
 * created that allows the user to simply check or uncheck the scrollable
 * behavior for these components in the designer. This is much easier than
 * having to create a JScrollPane instance on a form and manually adding a child
 * component. The scrollable property has the logic necessary to create the
 * JScrollPane at runtime and add the underlying Java bean.
 * 
 * The following methods are invoked when a JETAProperty value is set on a bean:
 * 1. setValue this updates this property with a new value if necessary. The
 * property has the option of updating the bean here as well. 2. updateBean If
 * needed, updates the associated bean with current property values. This is
 * needed during initialization. Not all properties need to implement this
 * method. See: {@link com.jeta.forms.gui.beans.JETABean}
 * 
 * @author Jeff Tassin
 */
public abstract class JETAProperty extends AbstractJETAPersistable {
	static final long serialVersionUID = -7709719636185198546L;

	/**
	 * The current version number of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The name for this property
	 */
	private String m_name;

	/**
	 * The type for this property. This type should match the type of the value.
	 * 
	 * @deprecated no longer required.
	 */
	@Deprecated
	private Class m_type;

	/**
	 * Flag that indicates if this property is preferred.
	 */
	private transient boolean m_preferred = true;

	/**
	 * Creates an unitialized <code>JETAProperty</code> instance.
	 */
	public JETAProperty() {
		this(null);
	}

	/**
	 * Creates a <code>JETAProperty</code> with specified name
	 * 
	 * @param name
	 *            the name for this property
	 */
	public JETAProperty(String name) {
		m_name = name;
	}

	/**
	 * Object equals implementation.
	 */
	public boolean equals(Object object) {
		if (object instanceof JETAProperty) {
			JETAProperty jp = (JETAProperty) object;
			return (super.equals(object) && isEqual(m_name, jp.m_name) && isEqual(m_type, jp.m_type) && m_preferred == jp.m_preferred);
		}
		else {
			return false;
		}
	}

	/**
	 * Returns the name of this property.
	 * 
	 * @return the name of this property
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Returns true if both collections have equal size and each object in the
	 * collection equals the corresponding object in the same position in the
	 * other collection. If both collections are null, true is returned.
	 */
	protected static boolean isCollectionsEqual(Collection c1, Collection c2) {
		if (c1 != null) {
			if (c2 == null || c1.size() != c2.size())
				return false;

			Iterator i1 = c1.iterator();
			Iterator i2 = c2.iterator();
			while (i1.hasNext()) {
				if (!(i1.next().equals(i2.next())))
					return false;
			}
			return true;
		}
		else {
			return (c2 == null);
		}
	}

	/**
	 * Tests if two objects are equal. If both are null, true is returned.
	 */
	protected static boolean isEqual(Object o1, Object o2) {
		if (o1 instanceof Collection && o2 instanceof Collection)
			return isCollectionsEqual((Collection) o1, (Collection) o2);
		if (o1 != null) {
			return o1.equals(o2);
		}
		else {
			return (o2 == null);
		}
	}

	/**
	 * Returns the flag that indicates if this property is preferred. A
	 * preferred property is shown in the Basic tab in the properties panel in
	 * the designer.
	 * 
	 * @return the flag that indicates if this property is preferred
	 */
	public boolean isPreferred() {
		return m_preferred;
	}

	/**
	 * Returns true if this property is not serializable.
	 * 
	 * @return true if this object is not serializable
	 */
	public boolean isTransient() {
		return false;
	}

	/**
	 * PostInitialize is called once after all components in a form have been
	 * instantiated at runtime (not design time). This gives each property a
	 * chance to do some last minute initializations that might depend on the
	 * top level parent. An example of this is button groups for radio buttons.
	 * Button groups are global to the top-level parent and not to the nested
	 * form that might contain the radio button.
	 * 
	 * @param panel
	 *            the top level parent that contains the form.
	 * @param jbean
	 *            the JETABean associated with this property
	 */
	public void postInitialize(FormPanel panel, JETABean jbean) {
		// no op here. Most properties don't need this call.
	}

	/**
	 * Sets the flag that indicates if this property is preferred. A preferred
	 * property is shown in the Basic tab in the properties panel in the
	 * designer.
	 * 
	 * @param pref
	 *            the flag that indicates if this property is preferred
	 */
	public void setPreferred(boolean pref) {
		m_preferred = pref;
	}

	/**
	 * Sets this property to that of another property.
	 */
	public abstract void setValue(Object obj);

	/**
	 * Updates the specified bean with the current values of this property.
	 * 
	 * @param jbean
	 *            the jetabean to update.
	 */
	public abstract void updateBean(JETABean jbean);

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_name = in.readString("name");
		/** type is deprecated */
		Class type = (Class) in.readObject("type");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		/** this is a hack to prevent too much noise when writing to XML */
		if (!(out instanceof JMLObjectOutput))
			out.writeVersion(VERSION);

		out.writeObject("name", m_name);

		/** this is a hack to prevent too much noise when writing to XML */
		if (!(out instanceof JMLObjectOutput))
			out.writeObject("type", m_type);
	}

}
