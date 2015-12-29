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
import java.util.HashMap;
import java.util.Iterator;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Defines a set of FormGroup objects that define the column or row groups for a
 * given form. For a given form, you should have two FormGroupSet objects: one
 * for the row groups and one for the column groups.
 * 
 * @author Jeff Tassin
 */
public class FormGroupSet extends AbstractJETAPersistable {
	static final long serialVersionUID = -7806433967830846242L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 1;

	/**
	 * An map of FormGroup keys to FormGroup objects m_groups<Integer,FormGroup>
	 * The Integer key is an arbitrary integer that defines the group.
	 */
	private HashMap m_groups = new HashMap();

	/**
	 * Creates an unitialized <code>FormGroupSet</code> instance.
	 */
	public FormGroupSet() {
	}

	/**
	 * Assigns a row /column to a group with the given key. If no group exists,
	 * then a new group is created.
	 * 
	 * @param groupKey
	 *            the key number for the group. This is the group index.
	 * @param index
	 *            the 1-based row or column.
	 */
	public void assignToGroup(Integer groupKey, int index) {
		if (groupKey == null)
			return;

		removeAssignment(index);
		if (groupKey.intValue() == 0)
			return;

		FormGroup group = (FormGroup) m_groups.get(groupKey);
		if (group == null) {
			group = new FormGroup();
			m_groups.put(groupKey, group);
		}
		group.assign(index);
	}

	/**
	 * Returns the group that the given column or row is assigned to.
	 * 
	 * @param index
	 *            the 1-based row or column
	 * @return the group that the given column/row is assigned to. Null is
	 *         returned if no group is found.
	 */
	public FormGroup getGroup(int index) {
		Iterator iter = m_groups.values().iterator();
		while (iter.hasNext()) {
			FormGroup group = (FormGroup) iter.next();
			if (group.contains(index))
				return group;
		}
		return null;
	}

	/**
	 * Returns the group id that the given row or column is assigned to.
	 * 
	 * @param index
	 *            the 1-based row or column.
	 * @return the group that the given column/row is assigned to. Null is
	 *         returned if no group is found.
	 */
	public Integer getGroupId(int index) {
		Iterator iter = m_groups.keySet().iterator();
		while (iter.hasNext()) {
			Integer key = (Integer) iter.next();
			FormGroup group = (FormGroup) m_groups.get(key);
			if (group != null && group.contains(index))
				return key;
		}
		return null;
	}

	/**
	 * Removes a row or column from the group it is assigned to.
	 * 
	 * @param index
	 *            the 1-based row or column
	 */
	public void removeAssignment(int index) {
		Iterator iter = m_groups.values().iterator();
		while (iter.hasNext()) {
			FormGroup group = (FormGroup) iter.next();
			group.remove(index);
			if (group.size() == 0)
				iter.remove();
		}
	}

	/**
	 * Returns this group definition as an array that is suitable for the
	 * FormLayout.
	 * 
	 * @return this group definition as an array that is suitable for the
	 *         FormLayout. See:
	 *         {@link com.jgoodies.forms.layout.FormLayout.setColumnGroups} See:
	 *         {@link com.jgoodies.forms.layout.FormLayout.setRowGroups}
	 */
	public int[][] toArray() {
		int[][] result = new int[m_groups.size()][];
		int count = 0;
		Iterator iter = m_groups.values().iterator();
		while (iter.hasNext()) {
			FormGroup group = (FormGroup) iter.next();
			int[] grp_array = group.toArray();
			assert (grp_array.length > 0);
			result[count] = grp_array;
			count++;
		}
		return result;
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_groups = (HashMap) in.readObject("groups");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("groups", m_groups);
	}

}
