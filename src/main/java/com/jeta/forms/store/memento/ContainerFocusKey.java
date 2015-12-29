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

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * Locates a component in a container
 * 
 * @deprecated Focus is no longer supported by the designer.
 * 
 * @author Jeff Tassin
 */
public class ContainerFocusKey extends AbstractJETAPersistable implements FocusKey, Externalizable {
	static final long serialVersionUID = 2805759792148388234L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The index within the parent that the component can be found
	 */
	private int m_index;

	/**
	 * This is only used for testing
	 */
	private transient Component m_component;

	/**
	 * Default ctor for serialization
	 */
	public ContainerFocusKey() {

	}

	/**
	 * ctor
	 * 
	 * @param path
	 *            the path to this key
	 */
	public ContainerFocusKey(int index, Component comp) {
		m_index = index;
		m_component = comp;
	}

	public Component getComponent(Container c) {
		if (c == null)
			return null;

		Component comp = c.getComponent(m_index);
		return comp;
	}

	/**
	 * For debugging
	 */
	public void print() {
		System.out.print("container(");
		System.out.print(m_index);
		System.out.print(")");
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_index = in.readInt("index");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeInt("index", m_index);
	}

}
