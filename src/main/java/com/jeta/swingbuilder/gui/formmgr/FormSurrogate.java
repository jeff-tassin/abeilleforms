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

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.formmgr.FormManager;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.memento.ComponentMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.registry.JETARegistry;

/**
 * This class acts as a place-holder for a FormComponent. This is needed because
 * we can have multiple views opened for the same FormComponent. For example, in
 * one view, a FormComponent could be part of a nest. In another view, this
 * component could be in a top level view.
 * 
 * @author Jeff Tassin
 */
public class FormSurrogate extends GridComponent {
	/**
	 * @directed
	 */
	private FormComponent m_form;

	/**
	 * ctor
	 */
	public FormSurrogate(FormComponent form) {
		m_form = form;
	}

	/**
	 * @return the Form associated with this surrogate.
	 */
	public String getId() {
		return m_form.getId();
	}

	/**
	 * @return the form that this surrogate represents.
	 */
	public FormComponent getForm() {
		return m_form;
	}

	/**
	 * Print for debugging
	 */
	public void print() {
		FormsLogger.debug("FormSurrogate  formid: " + getId());
	}

	/**
	 * Sets the state of this component from a previously stored state
	 */
	public void setState(ComponentMemento memento) throws FormException {
		assert (false);
	}

	public JETABean getBean() {
		return m_form.getBean();
	}

	/**
	 * @returns the state of this component which can be persisted.
	 */
	public ComponentMemento getState(StateRequest sr) throws FormException {
		FormManager fmgr = (FormManager) JETARegistry.lookup(FormManager.COMPONENT_ID);
		FormComponent fc = fmgr.getForm(getId());
		if (FormUtils.isDebug()) {
			if (fc != m_form) {
				System.out.println("FormSurrogate.getState failed.  id: " + getId() + "   form manager result: " + fc);
				assert (false);
			}
		}
		System.out.println("  formsurrogate.getState: " + fc.getChildView().getName());
		return m_form.getState(sr);
	}
}
