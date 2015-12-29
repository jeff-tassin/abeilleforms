/*
 * Copyright (C) 2005 Jeff Tassin
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

package com.jeta.swingbuilder.support;

import java.awt.Container;

import com.jeta.open.support.DefaultComponentFinder;
import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * A component finder used for design time. In this case we don't want component
 * listeners added to anything owned by the FormEditor. The reason is because
 * the XMLEncoder/XMLDecoder will store the container listeners added by the
 * design components that are not needed when in runtime.
 * 
 * @author Jeff Tassin
 */
public class DesignTimeComponentFinder extends DefaultComponentFinder {

	/**
	 * ctor
	 */
	public DesignTimeComponentFinder(Container parent) {
		super(parent);
	}

	/**
	 * Recursively searches all Components owned by this container. If the
	 * Component has a name, we store it in the m_components hash table
	 * 
	 * @param container
	 *            the container to search
	 */
	protected void buildNames(Container container) {
		if (!(container instanceof FormEditor)) {
			super.buildNames(container);
		}
	}
}
