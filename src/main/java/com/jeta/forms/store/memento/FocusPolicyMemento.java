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
import java.util.Collection;
import java.util.LinkedList;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.support.EmptyCollection;

/**
 * Stores the focus policy for a form.
 * 
 * @deprecated Focus is no longer supported by the designer.
 * 
 * @author Jeff Tassin
 */
public class FocusPolicyMemento extends AbstractJETAPersistable {
	static final long serialVersionUID = -6821089968630851097L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 1;

	private LinkedList m_focus_policy;

	/**
	 * ctor
	 */
	public FocusPolicyMemento() {

	}

	/**
	 * @return a collection of focus keys (FocusKey objects) that describe the
	 *         focus order for a given form
	 */
	public Collection getFocusPolicyKeys() {
		if (m_focus_policy == null)
			return EmptyCollection.getInstance();
		else
			return m_focus_policy;
	}

	/**
	 * Sets the focus order for a given form.
	 * 
	 * @param focusKeys
	 *            a collection of FocusKey objects
	 */
	public void addFocusKey(FocusKey fKey) {
		if (m_focus_policy == null)
			m_focus_policy = new LinkedList();

		m_focus_policy.add(fKey);
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int version = in.readVersion();
		m_focus_policy = (LinkedList) in.readObject("focuspolicy");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("focuspolicy", m_focus_policy);
	}

}
