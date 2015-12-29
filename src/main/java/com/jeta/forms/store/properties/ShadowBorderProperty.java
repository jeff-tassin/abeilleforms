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

import javax.swing.border.Border;

import com.jeta.forms.components.border.ShadowBorder;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>ShadowBorderProperty</code> defines the attributes needed to define
 * a shadow border for a component. The Swing library does not define a shadow
 * border, so one is defined in the forms code. See:
 * {@link com.jeta.forms.components.border.ShadowBorder}
 * 
 * @author Jeff Tassin
 */
public class ShadowBorderProperty extends BorderProperty {
	static final long serialVersionUID = -1317321607073605569L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 2;

	public static final int SOLID = 0;

	public static final int GRADIENT = 1;

	/**
	 * The type of border SOLID or GRADIENT
	 */
	private int m_type = SOLID;

	/**
	 * The start color for a gradient or the background color for a solid
	 */
	private ColorProperty m_start_color = new ColorProperty();

	/**
	 * The end color for a gradient. Ignored if a solid color
	 */
	private ColorProperty m_end_color = new ColorProperty();

	/**
	 * The thickness for the border
	 */
	private int m_thickness = 1;

	/**
	 * Flag that indicates if the border takes up the same space on the top/left
	 * as the bottom/right. If set to false, the border has zero padding on the
	 * top and left sides.
	 */
	private boolean m_symmetric = true;

	/**
	 * Creates an unitialized <code>ShadowBorderProperty</code>
	 */
	public ShadowBorderProperty() {
	}

	/**
	 * Creates a <code>ShadowBorderProperty</code> with the specified type,
	 * thickness and colors.
	 * 
	 * @param type
	 *            the type of shadow: SOLID or GRADIENT
	 * @param thickness
	 *            the thickness of the border
	 * @param startColor
	 *            the start color
	 * @param endColor
	 *            the end color
	 * @param symmetric
	 *            flag that indicates if the border is symmetric.
	 */
	public ShadowBorderProperty(int type, int thickness, ColorProperty startColor, ColorProperty endColor, boolean symmetric) {
		m_type = type;
		m_thickness = thickness;
		m_start_color = startColor;
		m_end_color = endColor;
		m_symmetric = symmetric;
	}

	/**
	 * BorderProperty implementation. Creates a ShadowBorder instance with the
	 * attributes of this object.
	 * 
	 * @return a border instance that can be applied to any Swing component.
	 */
	public Border createBorder(Component comp) {
		Border b = new ShadowBorder(getType(), getThickness(), getStartColor(), getEndColor(), isSymmetric());
		return b;
	}

	/**
	 * Returns the type of shadow: SOLID or GRADIENT
	 * 
	 * @return the type of shadow.
	 */
	public int getType() {
		return m_type;
	}

	/**
	 * Returns the start color property for the border
	 * 
	 * @return the start color property
	 */
	public ColorProperty getStartColor() {
		return m_start_color;
	}

	/**
	 * Returns the end color property for the border
	 * 
	 * @return the end color property
	 */
	public ColorProperty getEndColor() {
		return m_end_color;
	}

	/**
	 * Returns the thickness of the border.
	 * 
	 * @return the border thickness.
	 */
	public int getThickness() {
		return m_thickness;
	}

	/**
	 * Returns the flag that indicates if the border takes up the same space on
	 * the top/left as the bottom/right. If false, the border has zero padding
	 * on the top and left sides.
	 * 
	 * @return the symmetric flag for this border.
	 */
	public boolean isSymmetric() {
		return m_symmetric;
	}

	/**
	 * Sets the type of shadow for this border. Either SOLID or GRADIENT.
	 * 
	 * @param type
	 *            the type of shadow
	 */
	public void setType(int type) {
		m_type = type;
	}

	/**
	 * Sets the start color for the border.
	 * 
	 * @param c
	 *            the start color for the border
	 */
	public void setStartColor(ColorProperty c) {
		m_start_color = c;
	}

	/**
	 * Sets the end color for the border. This only has an effect for GRADIENT
	 * shadows.
	 * 
	 * @param c
	 *            the end color for the border.
	 */
	public void setEndColor(ColorProperty c) {
		m_end_color = c;
	}

	/**
	 * Sets the flag that controls the border symmetry.
	 * 
	 * @param symmetric
	 *            if true the border will have equal padding on the top/bottom
	 *            and left/right
	 */
	public void setSymmetric(boolean symmetric) {
		m_symmetric = symmetric;
	}

	/**
	 * Sets the border thickness
	 * 
	 * @param t
	 *            the border thickness
	 */
	public void setThickness(int t) {
		m_thickness = t;
	}

	/**
	 * Sets this property to that of another ShadowBorderProperty
	 * 
	 * @param prop
	 *            a ShadowBorderProperty instance.
	 */
	public void setValue(Object prop) {
		super.setValue(prop);
		if (prop instanceof ShadowBorderProperty) {
			ShadowBorderProperty bp = (ShadowBorderProperty) prop;
			m_type = bp.m_type;
			if (m_start_color == null)
				m_start_color = new ColorProperty();
			if (m_end_color == null)
				m_end_color = new ColorProperty();

			m_start_color.setValue(bp.m_start_color);
			m_end_color.setValue(bp.m_end_color);
			m_thickness = bp.m_thickness;
			m_symmetric = bp.m_symmetric;
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
		m_type = in.readInt("type");
		m_start_color = (ColorProperty) in.readObject("startcolor");
		m_end_color = (ColorProperty) in.readObject("endcolor");
		m_thickness = in.readInt("thickness");
		if (version > 1)
			m_symmetric = in.readBoolean("symmetric");
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("type", m_type);
		out.writeObject("startcolor", m_start_color);
		out.writeObject("endcolor", m_end_color);
		out.writeInt("thickness", m_thickness);
		out.writeBoolean("symmetric", m_symmetric);
	}

	public String toString() {
		return "SHADOW";
	}
}
