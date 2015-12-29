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

package com.jeta.swingbuilder.gui.formmgr;

import java.util.Collection;

import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * An interface for an object that manages FormEditor objects.
 * 
 * @author Jeff Tassin
 */
public interface EditorManager {
	public static final String COMPONENT_ID = "editor.manager";

	/**
	 * @return a collection of FormEditor objects that are currently opened
	 */
	public Collection getEditors();

	/**
	 * The current editor.
	 */
	public FormEditor getCurrentEditor();

	/**
	 * Called when a form or forms have changed. This tells the EditorManager to
	 * update any modified indicators on the GUI if a form is
	 * modified/unmodified.
	 */
	public void updateModifiedStatus();

}
