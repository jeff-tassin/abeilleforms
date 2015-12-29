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
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.border.Border;

import com.jeta.forms.components.border.BorderManager;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>DefaultBorderProperty</code> specifies the default border that is
 * assigned to a component by the current look and feel. The designer allows a
 * user to specify the default border in combination with custom borders for
 * most components.
 * 
 * @author Jeff Tassin
 */
public class DefaultBorderProperty extends BorderProperty {
	static final long serialVersionUID = 1494794388961888787L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 1;

	/**
	 * Creates a <code>DefaultBorderProperty</code> instance.
	 */
	public DefaultBorderProperty() {

	}

	/**
	 * BorderProperty implementation. Creates a default border for the Java bean
	 * associated with this property. The call is merely forwarded to the
	 * BorderManager which is responsible for getting the default border for the
	 * current look and feel.
	 * 
	 * @return a newly created border instance.
	 */
	public Border createBorder(Component comp) {
		if (comp instanceof JComponent) {
			return BorderManager.getDefaultBorder((JComponent) comp);
		}
		else {
			return null;
		}
	}

	/**
	 * Object equals implementation.
	 */
	public boolean equals(Object object) {
		if (object instanceof DefaultBorderProperty) {
			DefaultBorderProperty bp = (DefaultBorderProperty) object;
			return (super.equals(object));
		}
		else {
			return false;
		}
	}

	/**
	 * Sets this property to that of another property. No op for this property.
	 */
	public void setValue(Object prop) {

	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
	}

	public String toString() {
		return "DEFAULT";
	}
}
