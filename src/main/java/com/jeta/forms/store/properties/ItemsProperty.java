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

import java.awt.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;

import com.jeta.forms.components.list.ListItemRenderer;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * This property is used to represent a Collection of objects for those beans
 * that support showing a group. For example: JComboBox and JList.
 * 
 * @author Jeff Tassin
 */
public class ItemsProperty extends JETAProperty {
	static final long serialVersionUID = -382961445445803877L;

	/**
	 * The current version number of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The Collection of items in this property that will be added to the JList
	 * or JComboBox. This collection will normally contain String objects or
	 * more likely, ListItemProperty objects.
	 */
	private Collection m_items;

	/**
	 * The name of this property
	 */
	public static final String PROPERTY_ID = "items";

	/**
	 * Creates an unitialized <code>ItemsProperty</code> instance.
	 */
	public ItemsProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates an <code>ItemsProperty</code> instance with the specified list
	 * items.
	 * 
	 * @param items
	 *            a collection of objects used to populate the Java bean.
	 */
	public ItemsProperty(Collection items) {
		super(PROPERTY_ID);
		setItems(items);
	}

	/**
	 * Returns the collection of objects used to populate the Java bean
	 * associated with this property. This collection will contain either
	 * Strings or ListItemProperty objects.
	 * 
	 * @return the items in this property
	 */
	public Collection getItems() {
		return m_items;
	}

	/**
	 * Sets the items of this property.
	 * 
	 * @param items
	 *            the items for this property. This collection will contain
	 *            either Strings or ListItemProperty objects.
	 */
	private void setItems(Collection items) {
		if (items == null)
			m_items = null;
		else
			m_items = new java.util.LinkedList(items);
	}

	/**
	 * Sets the values of this property to that of another ItemsProperty.
	 * 
	 * @param prop
	 *            an ItemsProperty object.
	 */
	public void setValue(Object prop) {
		if (prop instanceof ItemsProperty) {
			ItemsProperty ip = (ItemsProperty) prop;
			setItems(ip.getItems());
		}
		else {
			assert (false);
		}
	}

	/**
	 * Updates the bean with the current items of this property. If the
	 * associated Java bean is a JComboBox or JList, the items of this property
	 * are added to the bean. Additionally, a ListItemRenderer is set as the
	 * renderer for the bean. This allows any icons associated with the list
	 * items to be correctly rendered.
	 */
	public void updateBean(JETABean jbean) {
		Component comp = null;
		if (jbean != null)
			comp = jbean.getDelegate();

		if (comp instanceof JComboBox) {
			JComboBox jbox = (JComboBox) comp;
			jbox.setRenderer(new ListItemRenderer());
			jbox.removeAllItems();
			if (m_items != null) {
				Iterator iter = m_items.iterator();
				while (iter.hasNext()) {
					jbox.addItem(iter.next());
				}
			}
		}
		else if (comp instanceof JList) {
			JList list = (JList) comp;
			Object lm = list.getModel();
			if (lm instanceof DefaultListModel) {
				list.setCellRenderer(new ListItemRenderer());
				DefaultListModel lmodel = (DefaultListModel) lm;
				lmodel.removeAllElements();
				if (m_items != null) {
					Iterator iter = m_items.iterator();
					while (iter.hasNext()) {
						lmodel.addElement(iter.next());
					}
				}
			}
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_items = (Collection) in.readObject("items");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("items", m_items);
	}

	public String toString() {
		if (m_items == null)
			return "Items Property:  null " + hashCode();
		else
			return "Items Property:  size: " + m_items.size() + "   " + hashCode();
	}
}
