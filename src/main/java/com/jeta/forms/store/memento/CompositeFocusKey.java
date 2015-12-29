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

import java.awt.Component;
import java.awt.Container;
import java.io.Externalizable;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A list of focus keys relative to each other.
 * 
 * @deprecated Focus is no longer supported by the designer.
 * 
 * @author Jeff Tassin
 */
public class CompositeFocusKey extends AbstractJETAPersistable implements FocusKey, Cloneable, Externalizable {
	static final long serialVersionUID = -8573867529435047276L;

	public static final int VERSION = 1;

	private LinkedList m_focus_keys = new LinkedList();

	/**
	 * ctor
	 * 
	 * @param path
	 *            the path to this key
	 */
	public CompositeFocusKey() {

	}

	/**
	 * Adds a focus key to this composite
	 */
	public void add(FocusKey fkey) {
		m_focus_keys.add(fkey);
	}

	/**
	 * Cloneable implementation
	 */
	public Object clone() {
		CompositeFocusKey result = new CompositeFocusKey();
		result.m_focus_keys.addAll(m_focus_keys);
		return result;
	}

	/**
	 * FocusKey implementation. Iterates over the keys in this composite and
	 * recursively locates a component based on each element
	 */
	public Component getComponent(Container c) {
		Component result = null;

		if (c == null)
			return null;

		Container parent = c;
		Iterator iter = m_focus_keys.iterator();
		while (iter.hasNext()) {
			FocusKey fkey = (FocusKey) iter.next();
			Component comp = fkey.getComponent(parent);
			if (iter.hasNext()) {
				if (comp instanceof Container) {
					parent = (Container) comp;
				}
				else {
					return null;
				}
			}
			else {
				result = comp;
			}
		}
		return result;
	}

	/**
	 * For debugging
	 */
	public void print() {
		Iterator iter = m_focus_keys.iterator();
		while (iter.hasNext()) {
			FocusKey fkey = (FocusKey) iter.next();
			fkey.print();
			if (iter.hasNext())
				System.out.print(", ");

		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_focus_keys = (LinkedList) in.readObject("focuskeys");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("focuskeys", m_focus_keys);
	}

}
