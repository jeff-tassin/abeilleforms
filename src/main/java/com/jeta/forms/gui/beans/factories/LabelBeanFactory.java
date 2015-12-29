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

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.jeta.forms.components.label.JETALabel;
import com.jeta.forms.gui.beans.BeanProperties;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;
import com.jeta.forms.store.properties.TransformOptionsProperty;
import com.jeta.forms.store.properties.effects.PaintProperty;
import com.jeta.forms.support.UserProperties;
import com.jeta.open.registry.JETARegistry;

/**
 * Factory for instantiating a JLabel bean.
 * 
 * @author Jeff Tassin
 */
public class LabelBeanFactory extends JComponentBeanFactory {
	/**
	 * User property for default horizontal alignment.
	 */
	public static final String ID_DEFAULT_HORIZONTAL_ALIGNMENT = "label.h.align";

	/**
	 * ctor
	 */
	public LabelBeanFactory() {
		super(JETALabel.class);
	}

	/**
	 * Override to set custom properties for your factory
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
		htprop.setPreferred(false);
		vtprop.setPreferred(false);

		props.register(hprop);
		props.register(htprop);
		props.register(vprop);
		props.register(vtprop);
		props.register(new PaintProperty());

		/** make font preferred since it is used often in labels */
		props.setPreferred("font", true);

	}

	public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException {
		JETABean jbean = super.createBean(compName, instantiateBean, setDefaults);
		Component comp = jbean.getDelegate();
		if (comp instanceof JLabel) {
			JLabel label = (JLabel) comp;
			String bname = getShortBeanClassName();
			if ("JETALabel".equals(bname))
				bname = "JLabel";

			if (setDefaults)
				label.setText(bname);

			try {
				UserProperties props = (UserProperties) JETARegistry.lookup(UserProperties.COMPONENT_ID);
				if (props != null) {
					String default_halign = props.getProperty(ID_DEFAULT_HORIZONTAL_ALIGNMENT, null);
					if (default_halign != null && setDefaults) {
						label.setHorizontalAlignment(getHorizontalAlignment(default_halign));
					}
				}
			} catch (Exception e) {
				// ignore here
			}
		}
		return jbean;
	}

	/**
	 * @return the JLabel horizontal alignment string from the given integer
	 *         value
	 */
	public static String getHorizontalAlignmentString(int halign) {
		if (halign == SwingConstants.LEFT)
			return "LEFT";
		else if (halign == SwingConstants.CENTER)
			return "CENTER";
		else if (halign == SwingConstants.RIGHT)
			return "RIGHT";
		else if (halign == SwingConstants.TRAILING)
			return "TRAILING";
		else if (halign == SwingConstants.LEADING)
			return "LEADING";
		else
			return "LEFT";
	}

	/**
	 * @return the JLabel horizontal alignment string from the given integer
	 *         value
	 */
	public static int getHorizontalAlignment(String halign) {
		if ("LEFT".equalsIgnoreCase(halign))
			return SwingConstants.LEFT;
		else if ("CENTER".equalsIgnoreCase(halign))
			return SwingConstants.CENTER;
		else if ("RIGHT".equalsIgnoreCase(halign))
			return SwingConstants.RIGHT;
		else if ("TRAILING".equalsIgnoreCase(halign))
			return SwingConstants.TRAILING;
		else if ("LEADING".equalsIgnoreCase(halign))
			return SwingConstants.LEADING;
		else
			return SwingConstants.LEFT;
	}

}
