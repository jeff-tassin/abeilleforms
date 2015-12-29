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

package com.jeta.forms.gui.components;

import java.awt.Container;

import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.store.memento.FormMemento;

/**
 * A factory for creating a form that is contained within a Swing container
 * other than another FormComponent. For example, when a form is contained
 * within a JTabbedPane or a JSplitPane, we use this factory to instantiate the
 * form.
 * 
 * @author Jeff Tassin
 */
public interface ContainedFormFactory {
	public static final String COMPONENT_ID = "contained.form.factory";

	/**
	 * Creates a form that is meant to be contained in a Swing container. This
	 * is form forms that can be edited in-place in the designer. This method
	 * actually creates two forms. The actual form that we can edit, and a top
	 * level parent that contains the form but is not directly editable.
	 * 
	 * @return the top level parent form.
	 */
	FormComponent createContainedForm(Class swingClass, FormMemento fm) throws FormException;

	/**
	 * This method creates a top-level parent form that is used to contain a
	 * form that we can edit. This is used in two cases. In the first, we use a
	 * top level form in the FormEditor. In the second, we use the top-level
	 * form in contained forms (e.g. a form that is contained in a JTabbedPane
	 * tab ).
	 * 
	 * @param parent
	 *            the object that will contain the top-level parent
	 * @param compsrc
	 *            the component source
	 * @param form
	 *            the form that will be contained by the top-level parent
	 */
	FormComponent createTopParent(Container parent, ComponentSource compsrc, FormComponent form) throws FormException;

}
