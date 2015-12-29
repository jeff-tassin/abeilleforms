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

package com.jeta.swingbuilder.gui.main;

import java.awt.Container;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.AbstractFormManager;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;

/**
 * The main form manager for the application.
 * 
 * @author Jeff Tassin
 */
public class MainFormManager extends AbstractFormManager {
	/**
	 * @undirected
	 */
	private MainFrame m_frame;

	/**
	 * ctor
	 */
	public MainFormManager(MainFrame frame) {
		super(frame, frame);
		m_frame = frame;
	}

	/**
	 * Closes the form in the editor.
	 */
	public void closeForm(String formId) {
		FormComponent form = getForm(formId);
		Collection child_forms = null;
		if (form != null) {
			child_forms = FormManagerDesignUtils.getNestedForms(this, form);
		}

		if (m_frame != null)
			m_frame.removeForm(formId);
		FormManagerDesignUtils.clearUnreferencedForms();
	}

	/**
	 * @return the form that is currently active in the editor
	 */
	public Container getCurrentEditor() {
		if (m_frame != null)
			return m_frame.getCurrentEditor();
		else
			return null;
	}

	/**
	 * @return true if the given form is opened in a top level editor.
	 */
	public boolean isOpened(String formId) {
		if (m_frame == null)
			return false;
		else
			return (m_frame.getForm(formId) != null);
	}

	/**
	 * @return true if the given formId is opened in any editor either as a top
	 *         level form or as a nest
	 */
	public boolean isReferenced(String formId) {
		/**
		 * check if any opened editors have the given form opened as a nested
		 * child. If so, then simply return. Otherwise, we can safely remove the
		 * form from the cache
		 */
		Collection editors = m_frame == null ? Collections.EMPTY_LIST : m_frame.getEditors();
		Iterator iter = editors.iterator();
		while (iter.hasNext()) {
			FormEditor editor = (FormEditor) iter.next();
			FormComponent fc = editor.getForm();
			if (FormManagerDesignUtils.containsForm(fc, formId))
				return true;
		}

		return false;
	}

	/**
	 * Only shows the form in the editor. No synchronization is made with any
	 * other views.
	 */
	public void showForm(String formId) {
		FormComponent fc = getForm(formId);
		if (fc != null) {
			if (m_frame != null)
				m_frame.showForm(fc);
		}
		else {
			System.out.println("MainFormManager.showForm failed: " + formId);
			assert (false);
		}
	}
}
