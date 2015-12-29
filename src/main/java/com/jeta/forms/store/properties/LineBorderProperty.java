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

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;

import javax.swing.border.Border;

import com.jeta.forms.components.border.JETALineBorder;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>LineBorderProperty</code> is used to store the attributes for a
 * JETALineBorder. This is a type of LineBorder with the added capability of
 * drawing only specified sides of the border. See:
 * {@link com.jeta.forms.components.border.JETALineBorder}
 * 
 * @author Jeff Tassin
 */
public class LineBorderProperty extends BorderProperty {
	static final long serialVersionUID = -2679973957505795245L;

	/**
	 * The version for this class
	 */
	private static final int VERSION = 3;

	/**
	 * The thickness in pixels of the border
	 */
	private int m_line_thickness = 1;

	/**
	 * The color of the border
	 */
	private ColorProperty m_line_color = new ColorProperty(Color.black);

	/**
	 * Set to true for a curved border. False for a square border
	 */
	private boolean m_curved = false;

	/**
	 * Creates an uninitialized <code>LineBorderProperty</code> instance.
	 */
	public LineBorderProperty() {
	}

	/**
	 * Creates an <code>JETALineBorder</code> instance with the attributes of
	 * this property.
	 * 
	 * @return a newly created JETALineBorer instance that can be set on any
	 *         Swing component.
	 */
	public Border createBorder(Component comp) {
		Border b = new JETALineBorder(new ColorProxy(m_line_color), getLineThickness(), isCurved(), isTopPainted(), isLeftPainted(), isBottomPainted(),
				isRightPainted());
		return createTitle(b);
	}

	/**
	 * Returns the thickness in pixels of the border.
	 * 
	 * @return the thickness of the border
	 */
	public int getLineThickness() {
		return m_line_thickness;
	}

	/**
	 * Returns the border color.
	 * 
	 * @return the border line color
	 */
	public Color getLineColor() {
		return m_line_color.getColor();
	}

	/**
	 * Returns the border color property.
	 * 
	 * @return the border line color property
	 */
	public ColorProperty getLineColorProperty() {
		return m_line_color;
	}

	/**
	 * Returns true if the corners of this border are rounded.
	 * 
	 * @return true if this border is curved
	 */
	public boolean isCurved() {
		return m_curved;
	}

	/**
	 * Sets the curved attribute for this border
	 * 
	 * @param bcurve
	 *            if true, sets rounded corners for this border.
	 */
	public void setCurved(boolean bcurve) {
		m_curved = bcurve;
	}

	/**
	 * Set the thickness of the border
	 */
	public void setLineThickness(int thickness) {
		m_line_thickness = thickness;
	}

	/**
	 * Set the border line color property
	 */
	public void setLineColorProperty(ColorProperty c) {
		m_line_color = c;
	}

	/**
	 * Sets this property to that of another LineBorderProperty.
	 * 
	 * @param prop
	 *            a LineBorderProperty instance
	 */
	public void setValue(Object prop) {
		super.setValue(prop);
		if (prop instanceof LineBorderProperty) {
			LineBorderProperty bp = (LineBorderProperty) prop;
			m_line_color.setValue(bp.m_line_color);
			m_line_thickness = bp.m_line_thickness;
			m_curved = bp.m_curved;
		}
		else {
			assert (false);
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_line_thickness = in.readInt("thickness");
		Object color = in.readObject("color");

		if (version > 1) {
			m_curved = in.readBoolean("curved");
		}

		if (color instanceof Color) {
			m_line_color = new ColorProperty((Color) color);
		}
		else {
			m_line_color = (ColorProperty) color;
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("thickness", m_line_thickness);
		out.writeObject("color", m_line_color);
		out.writeBoolean("curved", m_curved);
	}

	public String toString() {
		return "LINE";
	}
}
