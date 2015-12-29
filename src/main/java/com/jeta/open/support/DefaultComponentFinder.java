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
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JMenu;

/**
 * Interface used to define a service for locating child components within a
 * container that have a given name. Specialized implementations of this
 * interface are responsible for managing the parent container assocation.
 * 
 * @author Jeff Tassin
 */
public class DefaultComponentFinder implements ComponentFinder, ContainerListener {
	/**
	 * A map of component name to Component m_components<String,WeakReference(Component)>
	 */
	private HashMap m_components;

	/**
	 * The parent container that we search.
	 */
	private WeakReference m_container_ref;

	/**
	 * ctor
	 */
	public DefaultComponentFinder(Container parent) {
		m_container_ref = new WeakReference(parent);
	}

	/**
	 * Recursively searches all Components owned by this container. If the
	 * Component has a name, we store it in the m_components hash table
	 * 
	 * @param container
	 *            the container to search
	 */
	protected void buildNames(Container container) {
		if (container != null) {
			if (container instanceof JMenu) {
				buildNames(((JMenu) container).getPopupMenu());
			}
			else {
				registerComponent(container);

				container.removeContainerListener(this);
				container.addContainerListener(this);

				int count = container.getComponentCount();

				for (int index = 0; index < count; index++) {
					Component comp = container.getComponent(index);

					if (comp instanceof Container)
						buildNames((Container) comp);
					else {
						registerComponent(comp);
					}
				}
			}
		}
		else {
			assert (false);
		}
	}

	/**
	 * A component was added to the container
	 */
	public void componentAdded(ContainerEvent e) {
		/**
		 * ignore table cell renderers because the JTable creates/destroys these
		 * objects quite often
		 */
		Object child = e.getChild();
		if (child instanceof javax.swing.table.TableCellRenderer || child instanceof javax.swing.ListCellRenderer) {
			return;
		}

		if (child instanceof Container) {
			buildNames((Container) child);
		}
		else if (child instanceof Component) {
			registerComponent((Component) child);
		}
	}

	/**
	 * A component was remove from the container
	 */
	public void componentRemoved(ContainerEvent e) {
		/**
		 * ignore table cell renderers because the JTable creates/destroys these
		 * objects quite often
		 */
		Object child = e.getChild();
		if (child instanceof javax.swing.table.TableCellRenderer || child instanceof javax.swing.ListCellRenderer) {
			return;
		}

		// System.out.println( "DefaultComponentFinder.componentRemoved..." );
		if (child instanceof Component)
			unregisterComponent((Component) child);
	}

	/**
	 * Enables/Disables the menu/toolbar button associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bEnable
	 *            true/false to enable/disable
	 */
	public void enableComponent(String commandId, boolean bEnable) {
		Component comp = getComponentByName(commandId);
		if (comp != null) {
			comp.setEnabled(bEnable);
		}
	}

	/*
	 * This method looks at all components owned by a container. It will
	 * recursively search into child containers as well. @param componentName
	 * the name of the component to search for @return the named component
	 */
	public Component getComponentByName(String componentName) {
		if (m_components == null) {
			m_components = new HashMap();
			buildNames((Container) m_container_ref.get());
		}

		WeakReference wref = (WeakReference) m_components.get(componentName);
		if (wref != null)
			return (Component) wref.get();
		else
			return null;
	}

	/**
	 * Recursively searches an associated parent container for all components
	 * with the given name. An empty collection is returned if no components are
	 * found with the given name.
	 */
	public Collection getComponentsByName(String compName) {
		Component comp = getComponentByName(compName);
		if (comp == null)
			return EmptyCollection.getInstance();
		else {
			LinkedList list = new LinkedList();
			list.add(comp);
			return list;
		}
	}

	/**
	 * Returns the container associated with this component finder.
	 * 
	 * @return the container associated with this component finder.
	 */
	public Container getContainer() {
		return (Container) m_container_ref.get();
	}

	private void registerComponent(Component comp) {
		if (comp == null)
			return;

		String name = comp.getName();
		if (name != null && name.length() > 0) {
			m_components.put(name, new WeakReference(comp));
		}
	}

	/**
	 * Tells the implementation that any cached components should be flushed and
	 * reloaded because the parent container might have changed.
	 */
	public void unregisterComponent(Component c) {
		if (c instanceof Container)
			((Container) c).removeContainerListener(this);

		Iterator iter = m_components.values().iterator();
		while (iter.hasNext()) {
			WeakReference wref = (WeakReference) iter.next();
			if (wref == null || wref.get() == c)
				iter.remove();
		}
	}

	/**
	 * Shows/Hides the menu/toolbar button associated with the commandid
	 * 
	 * @param commandId
	 *            the id of the command whose button to enable/disable
	 * @param bVisible
	 *            show/hide the component/disable
	 */
	public void setVisible(String commandId, boolean bVisible) {
		Component comp = getComponentByName(commandId);
		if (comp != null) {
			comp.setVisible(bVisible);
		}
	}

	/**
	 * Tells the implementation that any cached components should be flushed and
	 * reloaded because the parent container might have changed.
	 */
	public void reset() {
		m_components = null;
	}
	
	/**
	 * Recursively searches an associated parent container for all components
	 * that are named. An empty collection is returned if no names components
	 * exist.
	 * 
	 * @return a collection of all named Component objects.
	 */
	public Collection getAllNamedComponents() {
		if (m_components == null) {
			m_components = new HashMap();
			buildNames((Container) m_container_ref.get());
		}

		LinkedList components = new LinkedList();
		
		Iterator names = m_components.keySet().iterator();
		while (names.hasNext()) {
			String name = (String) names.next();
			WeakReference wref = (WeakReference) m_components.get(name);
			if (wref != null)
				components.addLast((Component) wref.get());
		}

		return components;
	}
}
