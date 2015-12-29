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

package com.jeta.forms.gui.form;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Defines the BeanInfo for a GridView. When the user selects a form in the
 * designer, the properties pane shows several properties for that form. Forms
 * use GridViews as their underlying Java bean component. So, the designer is
 * really updating properties for a GridView object. The allows the user to set
 * standard properties such as border and background color for forms. A custom
 * BeanInfo is provided instead of the standard JPanel BeanInfo because we don't
 * want nor need all of the JPanel properties.
 * 
 * @author Jeff Tassin
 */
public class GridViewBeanInfo implements BeanInfo {
	private PropertyDescriptor[] m_props = new PropertyDescriptor[0];

	/**
	 * ctor
	 */
	public GridViewBeanInfo() {
		try {
			ArrayList props = new ArrayList();

			/** we don't need every property from JPanel, just name and opaque */
			BeanInfo info = Introspector.getBeanInfo(JPanel.class);
			PropertyDescriptor[] pds = info.getPropertyDescriptors();
			for (int index = 0; index < pds.length; index++) {
				PropertyDescriptor pd = pds[index];
				if ("name".equals(pd.getName()))
					props.add(pd);

				if ("opaque".equals(pd.getName())) {
					props.add(pd);
				}
			}

			m_props = (PropertyDescriptor[]) props.toArray(new PropertyDescriptor[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return new BeanInfo[0];
	}

	public BeanDescriptor getBeanDescriptor() {
		return null;
	}

	public int getDefaultEventIndex() {
		return 0;
	}

	public int getDefaultPropertyIndex() {
		return 0;
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return new EventSetDescriptor[0];
	}

	public Image getIcon(int i) {
		return null;
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return new MethodDescriptor[0];
	}

	/**
	 * @return a collection of JETAPropertyDescriptor objects
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		return m_props;
	}

}
