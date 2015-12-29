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

package com.jeta.forms.gui.formmgr;

import java.awt.Container;
import java.io.File;
import java.util.Collection;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;

/**
 * This interface defines a service for managing all opened forms in the
 * designer.
 * 
 * @author Jeff Tassin
 */
public interface FormManager {

	public static final String COMPONENT_ID = "jeta.forms.formmanager";

	/**
	 * Activates and shows the form in a top level editor in the application
	 * workspace. Additionally, any nested forms are synchronized with the
	 * latest changes in the new view. This is important because we can have
	 * multiple views of the same form.
	 */
	void activateForm(String formId);

	/**
	 * Closes the form in the editor.
	 */
	void closeForm(String formId);

	/**
	 * DesActivates the forms in the given editor. Additionally, any nested
	 * forms are synchronized with the latest changes in the new view. This is
	 * important because we can have multiple views of the same form.
	 */
	void deactivateForms(Container editor);

	/**
	 * @return the current editor
	 */
	Container getCurrentEditor();

	/**
	 * @return a collection of Form Ids that are current opened in the manager.
	 */
	Collection getForms();

	/**
	 * @return the form that has current formId. Null is returned if the form is
	 *         not in cache.
	 */
	FormComponent getForm(String formId);

	/**
	 * Installs the mouse and keyboard handlers for the given component. Note
	 * that we don't do this in the GridComponent itself because handlers are
	 * only installed in design mode.
	 */
	void installHandlers(ComponentSource compsrc, Container comp);

	/**
	 * Opens a form from a absolute file path.
	 */
	FormComponent openLinkedForm(String path) throws FormException;

	/**
	 * Opens a form from a absolute file path.
	 */
	FormComponent openLinkedForm(File f) throws FormException;

	/**
	 * Opens an embedded form.
	 */
	void openEmbeddedForm(FormComponent comp);

	/**
	 * @return true if the given form is opened in a top level editor.
	 */
	boolean isOpened(String formId);

	/**
	 * Removes a form from the cache.
	 */
	void removeForm(String oldId);

	/**
	 * Registers a form with this FormManager. This is mainly used for embedded
	 * forms.
	 */
	void registerForm(FormComponent fc);

	/**
	 * Only shows the form in a top level editor. No synchronization is made
	 * with any other views.
	 */
	void showForm(String formId);
}
