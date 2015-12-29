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

package com.jeta.forms.store.memento;

import java.io.IOException;

import com.jeta.forms.store.AbstractJETAPersistable;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * This class is used to wrap a Form when it is stored to a file. When you
 * deserialize a form file (.jfrm), this is the object you get from the
 * deserialization. We wrap all forms in a FormPackage in case we need to add
 * some information to the file at a later date that is outside the scope of the
 * FormMemento. Only one FormPackage object is used per file. [FormPackage]
 * [forms version] [FormMemento] ----- the main form [FormMemento] ---- a nested
 * form [BeanMemento] --- a java bean [BeanMemento] --- a java bean
 * [BeanMemento] ---- a java bean [BeanMemento] ---- a java bean [...]
 * 
 * @author Jeff Tassin
 */
public class FormPackage extends AbstractJETAPersistable {
	static final long serialVersionUID = -1091037320216196430L;

	/**
	 * The version of this class.
	 */
	public static final int VERSION = 2;

	/**
	 * The form memento we are packaging.
	 */
	private FormMemento m_form_memento;

	/**
	 * The version of the current forms runtime
	 */
	private static FormsVersion2 FORMS_RUNTIME_VERSION = new FormsVersion2();

	/**
	 * Creates a <code>FormPackage</code> instance.
	 */
	public FormPackage() {

	}

	/**
	 * Creates a <code>FormPackage</code> instance with the specified
	 * FormMemento object.
	 */
	public FormPackage(FormMemento fm) {
		m_form_memento = fm;
	}

	/**
	 * Returns the FormMemento associated with this object.
	 * 
	 * @return the form mememento
	 */
	public FormMemento getMemento() {
		return m_form_memento;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		int class_version = in.readVersion();
		if (class_version >= 2) {
			Object obj = in.readObject("fileversion");
			if (obj instanceof FormsVersion2) {
				FormsVersion2 file_version = (FormsVersion2) obj;
				if (FORMS_RUNTIME_VERSION.compareTo(file_version) < 0) {
					System.err.println("Encountered a form file with a version greater than that supported by this runtime.  File version: " + file_version
							+ "  Current runtime version: " + VERSION);
				}
			}
		}
		m_form_memento = (FormMemento) in.readObject("form");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		out.writeVersion(VERSION);
		out.writeObject("fileversion", FORMS_RUNTIME_VERSION);
		out.writeObject("form", m_form_memento);
	}

}
