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

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * This property is used to represent a button group assignment for a given
 * radio button. Button groups on a form are identified by a group name. This
 * property also provides the logic for creating and adding radio buttons to the
 * groups at at runtime.
 * 
 * @author Jeff Tassin
 */
public class ButtonGroupProperty extends JETAProperty {
	static final long serialVersionUID = 7814800168073274313L;

	/**
	 * The current version number of this class
	 */
	public static final int VERSION = 1;

	/**
	 * The name of this group. We use group names to identify which group a
	 * button belongs to in a given form.
	 */
	private String m_groupname;

	/**
	 * The name of this property
	 */
	public static final String PROPERTY_ID = "buttonGroup";

	/**
	 * Creates a <code>ButtonGroupProperty</code> set to group "1"
	 */
	public ButtonGroupProperty() {
		super(PROPERTY_ID);
		setGroupName("1");
	}

	/**
	 * Creates a <code>ButtonGroupProperty</code> with the specified group
	 * name.
	 * 
	 * @param groupName
	 *            the name of the group to set.
	 */
	public ButtonGroupProperty(String groupName) {
		super(PROPERTY_ID);
		setGroupName(groupName);
	}

	/**
	 * Returns the name of the button group.
	 * 
	 * @return the name of this group
	 */
	public String getGroupName() {
		return m_groupname;
	}

	/**
	 * Sets the name of this group
	 * 
	 * @param groupName
	 *            the name of the group
	 */
	public void setGroupName(String groupName) {
		m_groupname = groupName;
	}

	/**
	 * Sets this property to that of another ButtonGroupProperty.
	 * 
	 * @param prop
	 *            a ButtonGroupProperty instance.
	 */
	public void setValue(Object prop) {
		if (prop instanceof ButtonGroupProperty) {
			ButtonGroupProperty bgp = (ButtonGroupProperty) prop;
			setGroupName(bgp.getGroupName());
		}
		else {
			assert (false);
		}
	}

	/**
	 * PostInitialize is called once after all components in a form have been
	 * instantiated at runtime (not design time). This call allows properties to
	 * do last minute initializations to components that require the FormPanel
	 * instance which contains them. In this case we need to override because we
	 * use FormPanel to store the ButtonGroup instances. Adds the underlying
	 * JavaBean to the named button group in the panel.
	 * 
	 * @param panel
	 *            the panel that contains the radio buttons associated with this
	 *            button group.
	 * @param bean
	 *            the jetabean associated with this property
	 */
	public void postInitialize(FormPanel panel, JETABean bean) {
		String groupname = getGroupName();
		if (groupname != null && groupname.length() > 0) {
			FormUtils.safeAssert(bean != null);
			if (bean != null) {
				String groupkey = groupname;
				/**
				 * here we need to handle the case where a bean that has this
				 * property is contained in a container such as JTabbedPane. In
				 * this case, any radio buttons in a tab view should be isolated
				 * from buttons in other views with respect to the button group.
				 * Since we use the FormPanel to hold the button groups, we need
				 * to distigush between the same group names but on different
				 * tabs. The hashCode is used for this
				 */
				Container toplevelform = FormComponent.getTopLevelForm(bean);
				if (toplevelform != null) {
					groupkey = String.valueOf(toplevelform.hashCode()) + "." + groupname;
				}
				ButtonGroup group = (ButtonGroup) panel.get(groupkey);
				if (group == null) {
					group = new ButtonGroup();
					panel.put(groupkey, group);
				}
				Component javabean = bean.getDelegate();
				if (javabean instanceof AbstractButton) {
					AbstractButton btn = (AbstractButton) javabean;
					group.add(btn);
				}
			}
		}
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
		m_groupname = (String) in.readObject("groupname");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("groupname", m_groupname);
	}

}
