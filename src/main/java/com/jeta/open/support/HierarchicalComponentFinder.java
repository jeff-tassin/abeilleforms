/*
 * Copyright (C) 2008 Alexander Klein
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

import javax.swing.JMenu;

/**
 * Alternative Component finder implementation finding components hierarchically in the following way:
 * FormName.Subform1.Subform2.<...>.Component
 * 
 * @author Alexander Klein
 */
public class HierarchicalComponentFinder implements ComponentFinder, ContainerListener {
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
	public HierarchicalComponentFinder(Container parent) {
		m_container_ref = new WeakReference(parent);
	}

	/**
	 * Recursively searches all Components owned by this container. If the Component has a name, we store it in the
	 * m_components hash table
	 * 
	 * @param container
	 *            the container to search
	 */
	protected void buildNames(Container container) {
		if (container != null) {
			if (container instanceof JMenu) {
				buildNames(((JMenu) container).getPopupMenu());
			} else {
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
		} else {
			assert (false);
		}
	}

	/**
	 * A component was added to the container
	 */
	public void componentAdded(ContainerEvent e) {
		/**
		 * ignore table cell renderers because the JTable creates/destroys these objects quite often
		 */
		Object child = e.getChild();
		if (child instanceof javax.swing.table.TableCellRenderer || child instanceof javax.swing.ListCellRenderer) {
			return;
		}

		if (child instanceof Container) {
			buildNames((Container) child);
		} else if (child instanceof Component) {
			registerComponent((Component) child);
		}
	}

	/**
	 * A component was remove from the container
	 */
	public void componentRemoved(ContainerEvent e) {
		/**
		 * ignore table cell renderers because the JTable creates/destroys these objects quite often
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
	 * This method looks at all components owned by a container. It will recursively search into child containers as
	 * well. @param componentName the name of the component to search for @return the named component
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
	 * Recursively searches an associated parent container for all components with the given name. An empty collection
	 * is returned if no components are found with the given name.
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
		Container parent = comp.getParent();
		while ((parent != null) && (!m_container_ref.equals(parent))) {
			String pName = parent.getName();
			if ((pName != null) && (pName.length() > 0))
				name = pName + "." + name;
			parent = parent.getParent();
		}
		if (name != null && name.length() > 0) {
			m_components.put(name, new WeakReference(comp));
		}
	}

	/**
	 * Tells the implementation that any cached components should be flushed and reloaded because the parent container
	 * might have changed.
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
	 * Tells the implementation that any cached components should be flushed and reloaded because the parent container
	 * might have changed.
	 */
	public void reset() {
		m_components = null;
	}

	/**
	 * Recursively searches an associated parent container for all components that are named. An empty collection is
	 * returned if no names components exist.
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
