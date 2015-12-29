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

package com.jeta.open.gui.framework;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Method;
import java.util.EventListener;

import javax.swing.AbstractButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

/**
 * This class recursively searches a container for child components. It searches
 * for all registered listeners on those components that are part of the
 * com.jeta package and removes those listeners. This is used to assist in
 * garbage collection. Hopefully, it ensures that a frame or container does not
 * linger because of registered listeners that are never removed.
 * 
 * @author Jeff Tassin
 */
public class JETAComponentCleanser {
	Object[] m_params = new Object[1];
	Class[] m_types = new Class[1];

	/**
	 * Recursively searches all Components owned by this container. If the
	 * Component has a name, we store it in the m_components hash table
	 * 
	 * @param container
	 *            the container to search
	 */
	public void cleanse(Container container) {
		int count = container.getComponentCount();
		for (int index = 0; index < count; index++) {
			Component comp = container.getComponent(index);

			if (comp instanceof Container)
				cleanse((Container) comp);

			removeJETAListeners(comp, ActionListener.class, "removeActionListener");
			removeJETAListeners(comp, ContainerListener.class, "removeContainerListener");
			removeJETAListeners(comp, ChangeListener.class, "removeChangeListener");
			removeJETAListeners(comp, ItemListener.class, "removeItemListener");
			removeJETAListeners(comp, ComponentListener.class, "removeComponentListener");
			removeJETAListeners(comp, FocusListener.class, "removeFocusListener");
			removeJETAListeners(comp, HierarchyBoundsListener.class, "removeHierarchyBoundsListener");
			removeJETAListeners(comp, HierarchyListener.class, "removeHierarchyListener");
			removeJETAListeners(comp, InputMethodListener.class, "removeInputMethodListener");
			removeJETAListeners(comp, KeyListener.class, "removeKeyListener");
			removeJETAListeners(comp, MouseListener.class, "removeMouseListener");
			removeJETAListeners(comp, MouseMotionListener.class, "removeMouseMotionListener");
			removeJETAListeners(comp, MouseWheelListener.class, "removeMouseWheelListener");
		}
	}

	public void removeJETAListeners(Component comp, Class listenerClass, String methodName) {
		m_types[0] = listenerClass;
		Method method = null;

		EventListener[] listeners = comp.getListeners(listenerClass);
		for (int index = 0; index < listeners.length; index++) {
			EventListener listener = listeners[index];
			String lclass = listener.getClass().getName();
			if (lclass.indexOf("com.jeta") >= 0) {
				if (method == null) {
					try {
						if (listenerClass == ActionListener.class || listenerClass == ChangeListener.class || listenerClass == ItemListener.class) {
							if (comp instanceof AbstractButton || comp instanceof JTextField) {
								method = comp.getClass().getMethod(methodName, m_types);
							}
						}
						else {
							method = comp.getClass().getMethod(methodName, m_types);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (method != null) {
					try {
						m_params[0] = listener;
						method.invoke(comp, m_params);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
