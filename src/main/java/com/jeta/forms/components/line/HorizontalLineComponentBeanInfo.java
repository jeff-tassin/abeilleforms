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

package com.jeta.forms.components.line;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

/**
 * BeanInfo for a HorizontalLineComponent.
 * 
 * @author Jeff Tassin
 */
public class HorizontalLineComponentBeanInfo implements BeanInfo {
	private PropertyDescriptor[] m_props = null;

	/**
	 * ctor
	 */
	public HorizontalLineComponentBeanInfo() {
		try {
			PropertyDescriptor linedef = new PropertyDescriptor("lineDefinition", HorizontalLineComponent.class, "getLineDefinition", "setLineDefinition");
			PropertyDescriptor namedef = new PropertyDescriptor("name", HorizontalLineComponent.class, "getName", "setName");
			PropertyDescriptor posdef = new PropertyDescriptor("position", HorizontalLineComponent.class, "getPosition", "setPosition");

			linedef.setPreferred(true);
			posdef.setPreferred(true);
			m_props = new PropertyDescriptor[] { linedef, namedef, posdef };
		} catch (Exception e) {
			m_props = new PropertyDescriptor[0];
			e.printStackTrace();
		}
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return new BeanInfo[0];
	}

	public BeanDescriptor getBeanDescriptor() {
		return new BeanDescriptor(HorizontalLineComponent.class);
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
