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
import com.jeta.forms.gui.effects.RadialGradientPainter;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.open.i18n.I18N;

/**
 * Property for storing settings for a radial gradient effect. See:
 * {@link com.jeta.forms.gui.effects.RadialGradientPainter}
 * 
 * @author Jeff Tassin
 */
public class RadialGradientProperty extends JETAProperty implements PaintSupport {
	static final long serialVersionUID = 1554397018383485167L;

	public static final int VERSION = 1;

	public static final int TOP_LEFT = 0;
	public static final int TOP_CENTER = 1;
	public static final int TOP_RIGHT = 2;

	public static final int CENTER = 3;

	public static final int BOTTOM_LEFT = 4;
	public static final int BOTTOM_CENTER = 5;
	public static final int BOTTOM_RIGHT = 6;

	public static final int LEFT_CENTER = 7;
	public static final int RIGHT_CENTER = 8;

	/**
	 * The start color is at the center of the radial.
	 */
	private ColorProperty m_start_color = new ColorProperty(Color.black);

	/**
	 * The end color for the gradient.
	 */
	private ColorProperty m_end_color = new ColorProperty(Color.white);

	/**
	 * The location of the center of the gradient.
	 */
	private int m_position = TOP_LEFT;

	/**
	 * Controls the rate of change from one color to the next
	 */
	private int m_magnitude = 100;

	/**
	 * A cached painter object
	 */
	private transient RadialGradientPainter m_painter;

	/**
	 * Creates an uninitialized <code>RadialGradientProperty</code> instance.
	 */
	public RadialGradientProperty() {

	}

	/**
	 * Creates an uninitialized <code>RadialGradientProperty</code> instance.
	 * 
	 * @param startColor
	 *            the start color for the gradient
	 */
	public RadialGradientProperty(ColorProperty startColor, ColorProperty endColor, int position, int magnitude) {
		m_start_color = startColor;
		m_end_color = endColor;
		m_position = position;
		m_magnitude = magnitude;
	}

	/**
	 * PaintSupport implementation. Creates a painter that renders a radial
	 * gradient using the attributes defined in this object.
	 */
	public Painter createPainter() {
		if (m_painter == null)
			m_painter = new RadialGradientPainter(this);

		return m_painter;
	}

	/**
	 * Returns the color at the center of the radial.
	 * 
	 * @return the start color
	 */
	public ColorProperty getStartColor() {
		return m_start_color;
	}

	/**
	 * Returns the center of the radial gradient. Valid values: TOP_LEFT,
	 * TOP_CENTER, TOP_RIGHT, CENTER, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
	 * LEFT_CENTER, RIGHT_CENTER
	 */
	public int getPosition() {
		return m_position;
	}

	/**
	 * Returns the end color.
	 * 
	 * @return the end color
	 */
	public ColorProperty getEndColor() {
		return m_end_color;
	}

	/**
	 * Returns the rate of change from one color to the next. 100 is the default
	 * value.
	 */
	public int getMagnitude() {
		return m_magnitude;
	}

	/**
	 * Sets the rate of change from one color to the next. 100 is the default
	 * value.
	 * 
	 * @param mag
	 *            the magnitude
	 */
	public void setMagnitude(int mag) {
		m_magnitude = mag;
		m_painter = null;
	}

	/**
	 * Sets the position of the radial center.
	 * 
	 * @param position
	 *            the location of the center of the gradient. Valid values:
	 *            TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER, BOTTOM_LEFT,
	 *            BOTTOM_CENTER, BOTTOM_RIGHT, LEFT_CENTER, RIGHT_CENTER
	 */
	public void setPosition(int position) {
		m_position = position;
		m_painter = null;
	}

	/**
	 * Sets the color at the center of the radial.
	 * 
	 * @param c
	 *            the start color
	 */
	public void setStartColor(ColorProperty c) {
		m_start_color = c;
		m_painter = null;
	}

	/**
	 * Sets the end color.
	 * 
	 * @param c
	 *            the end color
	 */
	public void setEndColor(ColorProperty c) {
		m_end_color = c;
		m_painter = null;
	}

	/**
	 * Sets this property to that of another property.
	 */
	public void setValue(Object prop) {
		if (prop instanceof RadialGradientProperty) {
			RadialGradientProperty gp = (RadialGradientProperty) prop;
			if (m_start_color == null)
				m_start_color = new ColorProperty();
			if (m_end_color == null)
				m_end_color = new ColorProperty();

			m_start_color.setValue(gp.m_start_color);
			m_end_color.setValue(gp.m_end_color);
			m_position = gp.m_position;
			m_magnitude = gp.m_magnitude;
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
		RadialGradientPainter painter = (RadialGradientPainter) createPainter();
		if (jbean != null) {
			java.awt.Component comp = jbean.getDelegate();
			if (comp instanceof Paintable) {
				((Paintable) comp).setBackgroundPainter(painter);
			}
			else {
				assert (false);
			}
		}

	}

	/**
	 * @return a string representation of this proprety
	 */
	public String toString() {
		return I18N.getLocalizedMessage("Radial Gradient");
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_start_color = (ColorProperty) in.readObject("startcolor");
		m_end_color = (ColorProperty) in.readObject("endcolor");
		m_position = in.readInt("position");
		m_magnitude = in.readInt("magnitude");
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("startcolor", m_start_color);
		out.writeObject("endcolor", m_end_color);
		out.writeInt("position", m_position);
		out.writeInt("magnitude", m_magnitude);
	}

}
