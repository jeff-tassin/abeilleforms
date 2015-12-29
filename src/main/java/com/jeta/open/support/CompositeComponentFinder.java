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

package com.jeta.open.support;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A component finder that is composed of multipe component finders. This is
 * very useful for Frames that contain multiple containers with components that
 * have the same command. For example, you might have a menu item, toolbar
 * button, and popup menu item that have the same cut, copy, and paste commands.
 * These commands should route to the same action listener in the controller. To
 * handle this case, use a CompositeComponentFinder which is composed of a
 * finder for the menu, toolbar, and popup. You can then define your Frame as a
 * JETAContainer and delegate the most of the JETAContainer methods to this
 * finder.
 * 
 * @author Jeff Tassin
 */
public class CompositeComponentFinder implements ComponentFinder {
	/**
	 * A list of ComponentFinder objects.
	 */
	private LinkedList m_finders = new LinkedList();

	/**
	 * Constructor
	 */
	public CompositeComponentFinder() {

	}

	/**
	 * Constructor
	 */
	public CompositeComponentFinder(ComponentFinder finder) {
		add(finder);
	}

	/**
	 * Adds a finder to this composite.
	 */
	public void add(ComponentFinder finder) {
		m_finders.add(finder);
	}

	/**
	 * Enables/Disables the component associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bEnable
	 *            true/false to enable/disable
	 */
	public void enableComponent(String commandId, boolean bEnable) {
		Iterator iter = m_finders.iterator();
		while (iter.hasNext()) {
			ComponentFinder finder = (ComponentFinder) iter.next();
			Component comp = finder.getComponentByName(commandId);
			if (comp != null) {
				comp.setEnabled(bEnable);
			}
		}
	}

	/**
	 * Since we have multiple finders, the only approach we can take is to
	 * return the first component found.
	 */
	public Component getComponentByName(String compName) {
		Iterator iter = m_finders.iterator();
		while (iter.hasNext()) {
			ComponentFinder finder = (ComponentFinder) iter.next();
			Component comp = finder.getComponentByName(compName);
			if (comp != null)
				return comp;
		}
		return null;
	}

	/**
	 * Recursively searches an associated parent container for all components
	 * with the given name. An empty collection is returned if no components are
	 * found with the given name.
	 */
	public Collection getComponentsByName(String compName) {
		LinkedList list = new LinkedList();
		Iterator iter = m_finders.iterator();
		while (iter.hasNext()) {
			ComponentFinder finder = (ComponentFinder) iter.next();
			list.addAll(finder.getComponentsByName(compName));
		}
		return list;
	}

	/**
	 * Return a collection of ComponentFinder instances that are contained by
	 * this composite.
	 * 
	 * @return the collection of ComponentFinder instances owned by this
	 *         composite.
	 */
	public Collection getFinders() {
		return m_finders;
	}

	/**
	 * Tells the implementation that any cached components should be flushed and
	 * reloaded because the parent container might have changed.
	 */
	public void reset() {
		Iterator iter = m_finders.iterator();
		while (iter.hasNext()) {
			ComponentFinder finder = (ComponentFinder) iter.next();
			finder.reset();
		}
	}

	/**
	 * Shows/Hides the component associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bVisible
	 *            show/hide the component/disable
	 */
	public void setVisible(String commandId, boolean bVisible) {
		Iterator iter = m_finders.iterator();
		while (iter.hasNext()) {
			ComponentFinder finder = (ComponentFinder) iter.next();
			Component comp = finder.getComponentByName(commandId);
			if (comp != null)
				comp.setVisible(bVisible);
		}
	}

	/**
	 * Recursively searches an associated parent container for all components
	 * that are named. An empty collection is returned if no names components
	 * exist.
	 * 
	 * @return a collection of all named Component objects.
	 */
	public Collection getAllNamedComponents() {
		LinkedList list = new LinkedList();
		Iterator iter = m_finders.iterator();
		while (iter.hasNext()) {
			ComponentFinder finder = (ComponentFinder) iter.next();
			list.addAll(finder.getAllNamedComponents());
		}
		return list;
	}
}
