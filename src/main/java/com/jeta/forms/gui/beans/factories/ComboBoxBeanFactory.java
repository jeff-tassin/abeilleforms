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

package com.jeta.forms.gui.beans.factories;

import javax.swing.JComboBox;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.store.properties.ItemsProperty;

/**
 * Factory for instantiating a JETABean that contains an JComboBox and its
 * associated properties.
 * 
 * @author Jeff Tassin
 */
public class ComboBoxBeanFactory extends JComponentBeanFactory {
	/**
	 * Creates a <code>ComboBoxBeanFactory</code> instance.
	 */
	public ComboBoxBeanFactory() {
		super(JComboBox.class);
	}

	/**
	 * Defines the custom properties and default values for those properties for
	 * a JComboBox. In this case, we define an items property used to hold the
	 * items in the combo box drop down list.
	 * 
	 * @param props
	 *            used to register any custom properties.
	 * @see com.jeta.forms.store.properties.ItemsProperty
	 */
	public void defineProperties(BeanProperties props) {
		super.defineProperties(props);
		props.register(new ItemsProperty());
		props.removeProperty("selectedIndex");
	}
}
