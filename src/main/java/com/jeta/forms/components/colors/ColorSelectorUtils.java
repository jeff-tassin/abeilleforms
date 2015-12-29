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

package com.jeta.forms.components.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

/**
 * Helper class that uses reflection to access a JETAColorWell component.
 * 
 * @author Jeff Tassin
 */
class ColorSelectorUtils {
	/**
	 * color well class.
	 */
	private static Class m_cwc;

	/**
	 * Returns the method object associated with the given method name.
	 */
	private static Method getMethod(String methodName, Class[] params) throws ClassNotFoundException, NoSuchMethodException {
		if (m_cwc == null)
			m_cwc = Class.forName("com.jeta.forms.components.colors.JETAColorWell");

		return m_cwc.getMethod(methodName, params);
	}

	/**
	 * Invokes the addActionListener method.
	 */
	public static void addActionListener(Component colorWell, ActionListener listener) {
		try {
			Class[] params = new Class[] { ActionListener.class };
			Method m = getMethod("addActionListener", params);
			Object[] values = new Object[] { listener };
			m.invoke(colorWell, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Invokes the getColor method.
	 */
	public static Color getColor(Component colorWell) {
		try {
			Class[] params = new Class[0];
			Method m = getMethod("getColor", params);
			Object[] values = new Object[0];
			return (Color) m.invoke(colorWell, values);
		} catch (Exception e) {
			e.printStackTrace();
			/** not much else we can do here */
			return Color.black;
		}
	}

	/**
	 * Invokes the setColor method.
	 */
	public static void setColor(Component colorWell, Color color) {
		try {
			Class[] params = new Class[] { Color.class };
			Method m = getMethod("setColor", params);
			Object[] values = new Object[] { color };
			m.invoke(colorWell, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
