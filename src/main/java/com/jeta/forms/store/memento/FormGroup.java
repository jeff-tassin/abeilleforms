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
import java.util.ArrayList;
import java.util.Iterator;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Defines a set of rows or columns that belong to the same group in a form. A
 * <code>FormGroup</code> is used to store a single group of rows or columns.
 * 
 * @author Jeff Tassin
 */
public class FormGroup extends AbstractJETAPersistable {
	static final long serialVersionUID = -9169090936449529487L;

	/**
	 * The version of this class.
	 */
	public static final int VERSION = 1;

	/**
	 * An array of Integer objects that define the row or columns in a group on
	 * a form
	 */
	private ArrayList m_indexes = new ArrayList();

	/**
	 * Creates an unitialized <code>FormGroup</code> instance.
	 */
	public FormGroup() {
	}

	/**
	 * Add a row or column to this group.
	 * 
	 * @param index
	 *            the 1-based row or column to add
	 */
	void assign(int index) {
		if (!contains(index)) {
			m_indexes.add(new Integer(index));
		}
	}

	/**
	 * Returns true if this group contains the specified row or column.
	 * 
	 * @return true if this group contains the 1-based row or column
	 */
	public boolean contains(int index) {
		Iterator iter = m_indexes.iterator();
		while (iter.hasNext()) {
			Integer ival = (Integer) iter.next();
			if (ival.intValue() == index)
				return true;
		}
		return false;
	}

	/**
	 * Returns the group as an array of row or column indexes.
	 * 
	 * @return the group as an array of row or column indexes.
	 */
	public int[] toArray() {
		int[] result = new int[m_indexes.size()];
		int count = 0;
		Iterator iter = m_indexes.iterator();
		while (iter.hasNext()) {
			Integer ival = (Integer) iter.next();
			result[count] = ival.intValue();
			count++;
		}
		return result;
	}

	/**
	 * Remove a row or column from this group
	 * 
	 * @param index
	 *            the 1-based row or column to remove
	 */
	void remove(int index) {
		Iterator iter = m_indexes.iterator();
		while (iter.hasNext()) {
			Integer ival = (Integer) iter.next();
			if (ival.intValue() == index)
				iter.remove();
		}
	}

	/**
	 * Returns the number of rows or columns in this group.
	 * 
	 * @return the size of this group
	 */
	public int size() {
		return m_indexes.size();
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_indexes = (ArrayList) in.readObject("indexes");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("indexes", m_indexes);
	}

}
