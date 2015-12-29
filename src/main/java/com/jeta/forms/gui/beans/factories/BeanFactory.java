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

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;

/**
 * Defines an interface for creating a JETABean, it's associated Java Bean, and
 * any custom properties. The BeanFactories are responsible for instantiating
 * all Swing components and defining custom properties and default values for
 * each type of Swing component.
 * 
 * @author Jeff Tassin
 */
public interface BeanFactory {
	/**
	 * Instantiates a JETABean and assigns any custom properties needed by the
	 * application.
	 * 
	 * @param compName
	 *            the name to assign to this component by calling
	 *            Component.setName
	 * @param instantiateBean
	 *            set to true if the underlying Java Bean should be instantiated
	 *            as well. During deserialization we don't want to do this
	 *            because the BeanDeserializer will create the JavaBean for us.
	 * @param setDefaults
	 *            sets default properties for the bean. If false, no properties
	 *            will be set (e.g. the text for a JButton)
	 * @return the newly instantiated JETABean
	 */
	public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException;

}
