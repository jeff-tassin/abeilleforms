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

package com.jeta.forms.store.properties;

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.components.ContainedFormFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.memento.FormMemento;
import com.jeta.forms.store.memento.StateRequest;
import com.jeta.open.registry.JETARegistry;

/**
 * A <code>TabProperty</code> defines a single tab in a JTabbedPane. Included
 * in this description is the tab title, icon, and the content of the tab -
 * which is a form. This class is used by <code>TabbedPaneProperties</code> to
 * intialize a JTabbedPane during runtime.
 * 
 * @author Jeff Tassin
 */
public class TabProperty extends JETAProperty {
	static final long serialVersionUID = 2375434406561274626L;

	/**
	 * The current version number of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The tab title
	 */
	private String m_title;

	/**
	 * The icon for the tab.
	 */
	private IconProperty m_icon_property;

	/**
	 * A cached value for the form content for this tab
	 */
	private transient FormComponent m_form;

	/**
	 * The form state. This gets serialized to the persistent store and is used
	 * to create and initialize the cached form <code>m_form</code> value.
	 */
	private FormMemento m_memento;

	/**
	 * The name of this property
	 */
	public static final String PROPERTY_ID = "tab";

	/**
	 * Creates an unitialized <code>TabProperty</code> instance
	 */
	public TabProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates a <code>TabProperty</code> instance with the given title.
	 */
	public TabProperty(String title) {
		super(PROPERTY_ID);
		m_title = title;
	}

	/**
	 * Returns the form component contained by this tab property.
	 * 
	 * @return the form component contained by this tab property.
	 */
	public FormComponent getForm() throws FormException {
		if (m_form == null) {
			ContainedFormFactory factory = (ContainedFormFactory) JETARegistry.lookup(ContainedFormFactory.COMPONENT_ID);
			FormUtils.safeAssert(factory != null);
			m_form = factory.createContainedForm(JTabbedPane.class, m_memento);
		}
		return m_form;
	}

	/**
	 * Returns a memento object that completely defines the state of the form
	 * contained by this tab.
	 * 
	 * @return the memento for the form contained by this tab.
	 */
	public FormMemento getFormMemento() throws FormException {
		if (m_form != null)
			return m_form.getExternalState(StateRequest.DEEP_COPY);
		else
			return m_memento;
	}

	/**
	 * Returns the title of this tab.
	 * 
	 * @return the title of this tab.
	 */
	public String getTitle() {
		return m_title;
	}

	/**
	 * Returns the icon property for this tab. This property defines the icon
	 * used by this tab.
	 * 
	 * @return the icon property for this tab.
	 */
	public IconProperty getIconProperty() {
		return m_icon_property;
	}

	/**
	 * Return the underlying icon that is specified by the IconProperty. This
	 * value can be null if no icon is specified.
	 * 
	 * @return the underlying icon.
	 */
	public Icon icon() {
		return m_icon_property;
	}

	/**
	 * Sets the icon property for this tab.
	 * 
	 * @param iprop
	 *            the icon property.
	 */
	public void setIconProperty(IconProperty iprop) {
		if (m_icon_property == null)
			m_icon_property = new IconProperty();
		m_icon_property.setValue(iprop);
	}

	/**
	 * Sets this property to that of another TabProperty
	 * 
	 * @param prop
	 *            a TabProperty instance.
	 */
	public void setValue(Object prop) {
		if (prop instanceof TabProperty) {
			TabProperty tp = (TabProperty) prop;
			m_title = tp.m_title;
			m_form = tp.m_form;
			m_memento = tp.m_memento;

			if (m_icon_property == null)
				m_icon_property = new IconProperty();
			m_icon_property.setValue(tp.m_icon_property);
		}
	}

	/**
	 * Sets the title for this tab.
	 * 
	 * @param title
	 *            the tab title
	 */
	public void setTitle(String title) {
		m_title = title;
	}

	/**
	 * Updates the bean with the current value of this property
	 */
	public void updateBean(JETABean jbean) {
		// no op
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_title = (String) in.readObject("title");
		m_icon_property = (IconProperty) in.readObject("icon");
		m_memento = (FormMemento) in.readObject("form");
		m_form = null;
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("title", m_title);
		out.writeObject("icon", m_icon_property);
		/**
		 * This is a hack to obtain the state request which is set by the
		 * caller. There is no way to obtain this value other than getting it
		 * from the registry. The caller should have set this value. This is
		 * needed during preview when in design mode; otherwise we won't get the
		 * most current state for linked forms if they are opened in the
		 * designer
		 */
		StateRequest state_req = StateRequest.SHALLOW_COPY;
		Object obj = com.jeta.open.registry.JETARegistry.lookup(StateRequest.COMPONENT_ID);
		if (obj instanceof StateRequest)
			state_req = (StateRequest) obj;

		try {
			getForm();
			if (m_form != null) {
				out.writeObject("form", m_form.getExternalState(state_req));
			}
			else {
				out.writeObject("form", null);
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
			out.writeObject("form", null);
		}
	}

}
