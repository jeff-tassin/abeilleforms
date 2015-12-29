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
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>CompoundBorderProperty</code> is a composite of BorderProperty
 * instances. It is used to define a compound border.
 * 
 * @author Jeff Tassin
 */
public class CompoundBorderProperty extends BorderProperty {
	static final long serialVersionUID = -8513525079430361312L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 1;

	/**
	 * A list of borders that make up this compound border. The order is from
	 * outer most ---> inner most.
	 */
	private LinkedList m_borders = new LinkedList();

	/**
	 * Creates an uninitialized <code>CompoundBorderProperty</code> instance.
	 */
	public CompoundBorderProperty() {

	}

	/**
	 * Creates a <code>CompoundBorderProperty</code> instance with the
	 * specified border.
	 */
	public CompoundBorderProperty(BorderProperty prop) {
		addBorder(prop);
	}

	/**
	 * Adds a border to the end of the border list.
	 */
	public void addBorder(BorderProperty bp) {
		if (bp != null) {
			m_borders.addLast(bp);
		}
	}

	/**
	 * Creates a Swing Border composed of all the borders specified in this
	 * object.
	 */
	public Border createBorder(Component comp) {
		if (m_borders.size() == 0) {
			return null;
		}
		else if (m_borders.size() == 1) {
			BorderProperty bp = (BorderProperty) m_borders.getFirst();
			Border b = bp.createBorder(comp);
			return b;
		}
		else {
			Border last_border = null;
			Iterator iter = m_borders.iterator();
			while (iter.hasNext()) {
				BorderProperty bp = (BorderProperty) iter.next();
				Border b = bp.createBorder(comp);
				if (last_border == null) {
					last_border = b;
				}
				else {
					last_border = BorderFactory.createCompoundBorder(last_border, b);
				}
			}
			return last_border;
		}
	}

	/**
	 * Object equals implementation.
	 */
	public boolean equals(Object object) {
		if (object instanceof CompoundBorderProperty) {
			CompoundBorderProperty bp = (CompoundBorderProperty) object;
			boolean bresult = super.equals(object);
			return (bresult && isEqual(m_borders, bp.m_borders));
		}
		return false;
	}

	/**
	 * Returns the border at the given index.
	 */
	public BorderProperty getBorder(int index) throws IndexOutOfBoundsException {
		return (BorderProperty) m_borders.get(index);
	}

	/**
	 * @return an iterator to the borders (BorderProperty objects)in this
	 *         compound border
	 */
	public Iterator iterator() {
		return m_borders.iterator();
	}

	/**
	 * Return the number of borders defined by this compound border.
	 */
	public int size() {
		return m_borders.size();
	}

	/**
	 * Sets this property to that of another property.
	 */
	public void setValue(Object prop) {
		if (prop instanceof CompoundBorderProperty) {
			CompoundBorderProperty cb = (CompoundBorderProperty) prop;
			m_borders.clear();
			m_borders.addAll(cb.m_borders);
		}
	}

	/**
	 * Sets the border on the bean
	 */
	public void updateBean(JETABean jbean) {
		if (m_borders != null)
			super.updateBean(jbean);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_borders = (LinkedList) in.readObject("borders");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("borders", m_borders);
	}

	public String toString() {
		if (m_borders.size() == 0) {
			return "NO BORDER";
		}
		else if (m_borders.size() == 1) {
			BorderProperty bp = (BorderProperty) m_borders.getFirst();
			return bp.toString();
		}
		else {
			return "COMPOUND";
		}
	}

	public int getSize() {
		return m_borders.size();
	}
}
