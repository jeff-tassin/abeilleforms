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

import javax.swing.Icon;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * This property represents a single item in a JComboBox or JList. Its primary
 * purpose is to allow a user to specify an icon and text for an item in a list
 * in the forms designer.
 * 
 * @author Jeff Tassin
 */
public class ListItemProperty extends JETAProperty {
	static final long serialVersionUID = -6728088256939060742L;

	/**
	 * The current version number of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The item label
	 */
	private String m_label;

	/**
	 * The item icon.
	 */
	private IconProperty m_icon_property;

	/**
	 * The name of this property
	 */
	public static final String PROPERTY_ID = "listitem";

	/**
	 * Creates an unitialized <code>ListItemProperty</code> instance.
	 */
	public ListItemProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates a <code>ListItemProperty</code> instance with the specified
	 * label and no icon.
	 * 
	 * @param label
	 *            the label for this list item.
	 */
	public ListItemProperty(String label) {
		this(label, null);
	}

	/**
	 * Creates a <code>ListItemProperty</code> instance with the specified
	 * label and icon
	 * 
	 * @param label
	 *            the label for this list item.
	 * @param icon
	 *            the icon for this list item.
	 */
	public ListItemProperty(String label, IconProperty iprop) {
		super(PROPERTY_ID);
		m_label = label;
		setIconProperty(iprop);
	}

	/**
	 * Returns true if this property equals the given object.
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof ListItemProperty) {
			ListItemProperty prop = (ListItemProperty) obj;
			if (m_label == null)
				return false;
			else
				return m_label.equals(prop.m_label);
		}
		else
			return false;
	}

	/**
	 * Returns the label for this list item
	 */
	public String getLabel() {
		return m_label;
	}

	/**
	 * Returns the icon for this list item. Can be null.
	 */
	public IconProperty getIconProperty() {
		return m_icon_property;
	}

	/**
	 * Return the underlying icon that is specified by the IconProperty. This
	 * value can be null if no icon is specified.
	 * 
	 * @return the underlying icon.
	 */
	public Icon icon() {
		return m_icon_property;
	}

	/**
	 * Sets the icon property for this list item.
	 */
	public void setIconProperty(IconProperty iprop) {
		if (m_icon_property == null)
			m_icon_property = new IconProperty();
		m_icon_property.setValue(iprop);
	}

	/**
	 * Sets this property to that of another ListItemProperty or String object.
	 * 
	 * @param prop
	 *            a ListItemProperty instance. This can also be a String.
	 */
	public void setValue(Object prop) {
		if (prop instanceof ListItemProperty) {
			ListItemProperty lip = (ListItemProperty) prop;
			m_label = lip.m_label;

			if (m_icon_property == null)
				m_icon_property = new IconProperty();
			m_icon_property.setValue(lip.m_icon_property);
		}
		else if (prop instanceof String) {
			m_icon_property.setValue(null);
			m_label = (String) prop;
		}
	}

	/**
	 * Sets the label for this list item.
	 */
	public void setLabel(String label) {
		m_label = label;
	}

	/**
	 * Updates the bean with the current value of this property
	 */
	public void updateBean(JETABean jbean) {

	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());

		int version = in.readVersion();
		m_label = (String) in.readObject("label");
		m_icon_property = (IconProperty) in.readObject("icon");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("label", m_label);
		out.writeObject("icon", m_icon_property);
	}

	public String toString() {
		return m_label;
	}
}
