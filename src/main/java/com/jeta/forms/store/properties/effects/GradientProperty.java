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
import com.jeta.forms.gui.effects.LinearGradientPainter;
import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.JETAProperty;
import com.jeta.open.i18n.I18N;

/**
 * A <code>GradientProperty</code> stores the attributes for a
 * LinearGradientPainter. See:
 * {@link com.jeta.forms.gui.effects.LinearGradientPainter}
 * 
 * @author Jeff Tassin
 */
public class GradientProperty extends JETAProperty implements PaintSupport {
	static final long serialVersionUID = 8426558667506208881L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 3;

	/**
	 * Gradient direction constants.
	 */
	public static final int TOP_BOTTOM = 0;

	public static final int BOTTOM_TOP = 1;

	public static final int LEFT_RIGHT = 2;

	public static final int RIGHT_LEFT = 3;

	public static final int UP_RIGHT = 4;

	public static final int UP_LEFT = 5;

	public static final int DOWN_RIGHT = 6;

	public static final int DOWN_LEFT = 7;

	/**
	 * Start and end colors
	 */
	private ColorProperty m_start_color = new ColorProperty(Color.black);

	private ColorProperty m_end_color = new ColorProperty(Color.white);

	/**
	 * Magnitude controls the rate of change from start to end color. A value of
	 * 1.0 means equal rates of change for both colors.
	 */
	private float m_magnitude = 1.0f;

	private int m_direction = TOP_BOTTOM;

	/**
	 * A cached painter
	 */
	private transient LinearGradientPainter m_painter;

	/**
	 * Creates an unitialized <code>GradientProperty</code> instance
	 */
	public GradientProperty() {

	}

	/**
	 * Creates a <code>GradientProperty</code> instance with the specified
	 * colors and direction
	 * 
	 * @param start
	 *            the start color
	 * @param end
	 *            the end color
	 * @param direction
	 *            the direction of the gradient.
	 */
	public GradientProperty(ColorProperty start, ColorProperty end, int direction) {
		m_start_color = start;
		m_end_color = end;
		m_direction = direction;
	}

	/**
	 * PaintSupport implementation. Creates a painter that renders a linear
	 * gradient using the attributes defined by this object.
	 */
	public Painter createPainter() {
		if (m_painter == null)
			m_painter = new LinearGradientPainter(this);

		return m_painter;
	}

	/**
	 * Returns the direction of the gradient: valid values are: TOP_BOTTOM,
	 * BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT, UP_RIGHT, UP_LEFT, DOWN_RIGHT,
	 * DOWN_LEFT
	 */
	public int getDirection() {
		return m_direction;
	}

	/**
	 * Returns the end color for the gradient
	 * 
	 * @return the end color
	 */
	public ColorProperty getEndColor() {
		return m_end_color;
	}

	/**
	 * Returns the value that controls the rate of change from one color to the
	 * next. The default value is 1.0f
	 */
	public float getMagnitude() {
		return m_magnitude;
	}

	/**
	 * Returns the start color for the gradient
	 * 
	 * @return the start color
	 */
	public ColorProperty getStartColor() {
		return m_start_color;
	}

	/**
	 * Sets the direction of the gradient: valid values are: TOP_BOTTOM,
	 * BOTTOM_TOP, LEFT_RIGHT, RIGHT_LEFT, UP_RIGHT, UP_LEFT, DOWN_RIGHT,
	 * DOWN_LEFT
	 * 
	 * @param direction
	 *            the direction
	 */
	public void setDirection(int direction) {
		m_direction = direction;
		m_painter = null;
	}

	/**
	 * Sets the start color for the gradient
	 * 
	 * @param c
	 *            the start color
	 */
	public void setStartColor(ColorProperty c) {
		m_start_color = c;
		m_painter = null;
	}

	/**
	 * Sets the end color for the gradient
	 * 
	 * @param c
	 *            the end color
	 */
	public void setEndColor(ColorProperty c) {
		m_end_color = c;
		m_painter = null;
	}

	/**
	 * Sets the value that controls the rate of change from the start color to
	 * the end color. The default value is 1.0f
	 * 
	 * @param mag
	 *            the magnitude.
	 */
	public void setMagnitude(float mag) {
		m_magnitude = mag;
		m_painter = null;
	}

	/**
	 * Sets this property to that of another GradientProperty
	 * 
	 * @param prop
	 *            a GradientProperty instance
	 */
	public void setValue(Object prop) {
		if (prop instanceof GradientProperty) {
			GradientProperty gp = (GradientProperty) prop;
			if (m_start_color == null)
				m_start_color = new ColorProperty();
			if (m_end_color == null)
				m_end_color = new ColorProperty();
			m_start_color.setValue(gp.m_start_color);
			m_end_color.setValue(gp.m_end_color);
			m_magnitude = gp.m_magnitude;
			m_direction = gp.m_direction;
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
		LinearGradientPainter painter = (LinearGradientPainter) createPainter();
		if (jbean != null) {
			java.awt.Component comp = jbean.getDelegate();
			if (comp instanceof Paintable) {
				((Paintable) comp).setBackgroundPainter(painter);
			}
			else {
				if (comp != null) {
					System.out.println("Fill property set on non paintable. " + comp.getClass());
				}
			}
		}

	}

	/**
	 * @return a string representation of this proprety
	 */
	public String toString() {
		return I18N.getLocalizedMessage("Linear Gradient");
	}

	/**
	 * Externalizable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_start_color = (ColorProperty) in.readObject("startcolor");
		m_end_color = (ColorProperty) in.readObject("endcolor");
		m_magnitude = in.readFloat("magnitude");
		m_direction = in.readInt("direction");
		if (version == 1) {
			/** deprecated */
			boolean radial = in.readBoolean("radial");
		}
	}

	/**
	 * Externalizable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {

		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("startcolor", m_start_color);
		out.writeObject("endcolor", m_end_color);
		out.writeFloat("magnitude", m_magnitude);
		out.writeInt("direction", m_direction);
	}

}
