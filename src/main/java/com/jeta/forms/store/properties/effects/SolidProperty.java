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

package com.jeta.forms.store.properties.effects;

import java.awt.Color;
import java.io.IOException;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.gui.effects.SolidPainter;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.open.i18n.I18N;

/**
 * Property for storing settings for a solid color fill effect. See
 * {@link com.jeta.forms.gui.effects.SolidPainter}
 * 
 * @author Jeff Tassin
 */
public class SolidProperty extends JETAProperty implements PaintSupport {
	static final long serialVersionUID = -846608124012768052L;

	/**
	 * The version for this class
	 */
	public static final int VERSION = 2;

	/**
	 * The color for the solid.
	 */
	private ColorProperty m_color_prop = new ColorProperty();

	/**
	 * A cached painter object
	 */
	private transient SolidPainter m_painter;

	/**
	 * Creates an uninitialized <code>SolidProperty</code> instance
	 */
	public SolidProperty() {

	}

	/**
	 * PaintSupport implementation. Creates a painter that renders a solid
	 * background.
	 */
	public Painter createPainter() {
		if (m_painter == null)
			m_painter = new SolidPainter(this);

		return m_painter;
	}

	/**
	 * Returns the fill color.
	 * 
	 * @return the fill color
	 */
	public Color getColor() {
		return m_color_prop.getColor();
	}

	/**
	 * Returns the fill color property
	 * 
	 * @return the fill color property
	 */
	public ColorProperty getColorProperty() {
		return m_color_prop;
	}

	/**
	 * Sets the fill color property.
	 * 
	 * @param c
	 *            the fill color property
	 */
	public void setColorProperty(ColorProperty c) {
		m_color_prop.setValue(c);
	}

	/**
	 * Sets this property to that of SolidProperty
	 * 
	 * @param prop
	 *            a SolidProperty instance
	 */
	public void setValue(Object prop) {
		if (prop instanceof SolidProperty) {
			SolidProperty sp = (SolidProperty) prop;
			m_color_prop.setValue(sp.m_color_prop);
			m_painter = null;
		}
		else {
			assert (false);
		}
	}

	/**
	 * Updates the bean. Gets the underlying Java bean component associated with
	 * this property and if that component implements the Paintable interface,
	 * sets the background painter.
	 */
	public void updateBean(JETABean jbean) {
		SolidPainter painter = (SolidPainter) createPainter();
		if (jbean != null) {
			java.awt.Component comp = jbean.getDelegate();
			if (comp instanceof Paintable) {
				((Paintable) comp).setBackgroundPainter(painter);
			}
		}
	}

	/**
	 * @return a string representation of this proprety
	 */
	public String toString() {
		return I18N.getLocalizedMessage("Solid");
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		if (version == 1) {
			Color c = (Color) in.readObject("color");
			m_color_prop.setColorKey(ColorProperty.CONSTANT_COLOR);
			m_color_prop.setConstantColor(c);
		}
		else {
			m_color_prop = (ColorProperty) in.readObject("color");
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("color", m_color_prop);

	}

}
