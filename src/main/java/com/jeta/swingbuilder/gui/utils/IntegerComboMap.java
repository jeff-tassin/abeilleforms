/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.gui.utils;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComboBox;

/**
 * Very often we have JComboBoxes that are loaded with String values but which
 * map to integers. So, we use this helper class to make using combo boxes in
 * these situations a little easier.
 * 
 * @author Jeff Tassin
 */
public class IntegerComboMap {
	/**
	 * The actual map
	 */
	private LinkedList m_map = new LinkedList();

	/**
	 * Assigns a combo box item to an integer value
	 */
	public void map(int key, String comboItem) {
		Iterator iter = m_map.iterator();
		while (iter.hasNext()) {
			KeyValuePair kv = (KeyValuePair) iter.next();
			if (kv.key == key)
				iter.remove();
		}
		m_map.add(new KeyValuePair(key, comboItem));
	}

	/**
	 * @return the selected map value. -1 is returned if no item is selected in
	 *         the box or no match is found in this map for the selected item in
	 *         the combo.
	 */
	public int getSelectedValue(JComboBox cbox) {
		if (cbox == null)
			return -1;

		Object value = cbox.getSelectedItem();
		if (value == null)
			return -1;

		Iterator iter = m_map.iterator();
		while (iter.hasNext()) {
			KeyValuePair kv = (KeyValuePair) iter.next();
			if (value.equals(kv.value))
				return kv.key;
		}
		return -1;
	}

	/**
	 * Sets the selected item in the combo box using the key.
	 */
	public void setSelectedItem(JComboBox cbox, int key) {
		if (cbox == null)
			return;

		Iterator iter = m_map.iterator();
		while (iter.hasNext()) {
			KeyValuePair kv = (KeyValuePair) iter.next();
			if (key == kv.key) {
				cbox.setSelectedItem(kv.value);
				return;
			}
		}
	}

	private static class KeyValuePair {
		Object value;
		int key;

		KeyValuePair(int key, Object value) {
			this.key = key;
			this.value = value;
		}
	}
}
