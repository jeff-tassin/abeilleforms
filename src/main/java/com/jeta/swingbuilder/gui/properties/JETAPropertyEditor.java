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

package com.jeta.swingbuilder.gui.properties;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

/**
 * @author Jeff Tassin
 */
public abstract class JETAPropertyEditor extends PropertyEditorSupport {
	/**
	 * Invokes a dialog used to update the property
	 * 
	 * @param view
	 *            the component that invokes the dialog. This is mainly used to
	 *            set the parent of the dialog.
	 */
	public void invokePropertyDialog(Component view) {
		// no op
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return false;
	}

	/**
	 * Determines whether this class renders itself using the
	 * paintValue(Graphics g, Rectangle rect) method. Generally, editors that
	 * are not JComponents are paintable.
	 */
	public boolean isPaintable() {
		return false;
	}

	public String getJavaInitializationString() {
		return "";
	}

}
