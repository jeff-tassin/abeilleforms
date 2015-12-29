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

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.store.properties.TransformOptionsProperty;

/**
 * Factory for instantiating a JETABean that contains an AbstractButton and its
 * associated properties.
 * 
 * @author Jeff Tassin
 */
public class AbstractButtonFactory extends JComponentBeanFactory {
	/**
	 * Creates an <code>AbstractButtonFactory</code> instance.
	 */
	public AbstractButtonFactory(Class btnclass) {
		super(btnclass);
	}

	/**
	 * Defines the custom properties and default values for those properties for
	 * an AbstractButton.
	 * 
	 * @param props
	 *            used to register any custom properties.
	 */
	public void defineProperties(BeanProperties props) {
		super.defineProperties(props);

		TransformOptionsProperty hprop = new TransformOptionsProperty("horizontalAlignment", "getHorizontalAlignment", "setHorizontalAlignment",
				new Object[][] { { "LEFT", new Integer(SwingConstants.LEFT) }, { "CENTER", new Integer(SwingConstants.CENTER) },
						{ "RIGHT", new Integer(SwingConstants.RIGHT) }, { "LEADING", new Integer(SwingConstants.LEADING) },
						{ "TRAILING", new Integer(SwingConstants.TRAILING) } });

		TransformOptionsProperty htprop = new TransformOptionsProperty("horizontalTextPosition", "getHorizontalTextPosition", "setHorizontalTextPosition",
				new Object[][] { { "LEFT", new Integer(SwingConstants.LEFT) }, { "CENTER", new Integer(SwingConstants.CENTER) },
						{ "RIGHT", new Integer(SwingConstants.RIGHT) }, { "LEADING", new Integer(SwingConstants.LEADING) },
						{ "TRAILING", new Integer(SwingConstants.TRAILING) } });

		TransformOptionsProperty vprop = new TransformOptionsProperty("verticalAlignment", "getVerticalAlignment", "setVerticalAlignment",
				new Object[][] { { "TOP", new Integer(SwingConstants.TOP) }, { "CENTER", new Integer(SwingConstants.CENTER) },
						{ "BOTTOM", new Integer(SwingConstants.BOTTOM) } });

		TransformOptionsProperty vtprop = new TransformOptionsProperty("verticalTextPosition", "getVerticalTextPosition", "setVerticalTextPosition",
				new Object[][] { { "TOP", new Integer(SwingConstants.TOP) }, { "CENTER", new Integer(SwingConstants.CENTER) },
						{ "BOTTOM", new Integer(SwingConstants.BOTTOM) } });

		hprop.setPreferred(false);
		htprop.setPreferred(false);
		vprop.setPreferred(false);
		vtprop.setPreferred(false);
		props.register(hprop);
		props.register(htprop);
		props.register(vprop);
		props.register(vtprop);

		/**
		 * getLabel/setLabel is deprecated in AbstactButton, so we don't need it
		 * in the designer
		 */
		props.removeProperty("label");
	}

	/**
	 * BeanFactory implementation. Override from JComponentBeanFactory because
	 * we want to set the text property of the button when it is created.
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
	public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException {
		JETABean jbean = super.createBean(compName, instantiateBean, setDefaults);
		Component comp = jbean.getDelegate();
		if (comp instanceof AbstractButton && setDefaults) {
			AbstractButton btn = (AbstractButton) comp;
			btn.setText(getShortBeanClassName());
		}
		return jbean;
	}
}
