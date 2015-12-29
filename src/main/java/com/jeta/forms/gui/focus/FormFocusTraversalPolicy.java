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

package com.jeta.forms.gui.focus;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The focus traversal policy for our form.
 * 
 * This class is currently not being used since focus is not supported in the
 * designer.
 * 
 * @author Jeff Tassin
 */
public class FormFocusTraversalPolicy extends FocusTraversalPolicy {
	/**
	 * The list of components in focus order
	 */
	private ArrayList m_focus_list = new ArrayList();

	/**
	 * A map of components to focus_list index m_comp_map<Component,Integer>
	 */
	private HashMap m_comp_map = new HashMap();

	/**
	 * ctor
	 * 
	 * @param components
	 *            a collection of components (java.awt.Component objects) in the
	 *            correct focus order.
	 */
	public FormFocusTraversalPolicy(Collection components) {
		rebuildPolicy(components);
	}

	/**
	 * Rebuilds the focus policy based on the given collection of components.
	 */
	public void rebuildPolicy(Collection components) {
		m_focus_list.clear();
		m_comp_map.clear();
		m_focus_list.addAll(components);
		for (int index = 0; index < m_focus_list.size(); index++) {
			Component comp = (Component) m_focus_list.get(index);
			m_comp_map.put(comp, new Integer(index));
		}
	}

	/**
	 * FocusTraversalPolicy implementation
	 */
	public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
		Integer idx = (Integer) m_comp_map.get(aComponent);
		if (idx == null)
			return getFirstComponent(focusCycleRoot);
		else {
			int ival = idx.intValue();
			ival++;
			if (ival >= m_focus_list.size())
				ival = 0;
			return (Component) m_focus_list.get(ival);
		}
	}

	/**
	 * FocusTraversalPolicy implementation
	 */
	public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
		Integer idx = (Integer) m_comp_map.get(aComponent);
		if (idx == null)
			return getFirstComponent(focusCycleRoot);
		else {
			int ival = idx.intValue();
			ival--;
			if (ival < 0)
				ival = 0;
			return (Component) m_focus_list.get(ival);
		}
	}

	/**
	 * FocusTraversalPolicy implementation
	 */
	public Component getDefaultComponent(Container focusCycleRoot) {
		return getFirstComponent(focusCycleRoot);
	}

	/**
	 * FocusTraversalPolicy implementation
	 */
	public Component getFirstComponent(Container focusCycleRoot) {
		if (m_focus_list.size() > 0)
			return (Component) m_focus_list.get(0);
		else
			return null;
	}

	/**
	 * FocusTraversalPolicy implementation
	 */
	public Component getInitialComponent(Window window) {
		return getFirstComponent(window);
	}

	/**
	 * FocusTraversalPolicy implementation
	 */
	public Component getLastComponent(Container focusCycleRoot) {
		if (m_focus_list.size() > 0)
			return (Component) m_focus_list.get(m_focus_list.size() - 1);
		else
			return null;
	}
}
