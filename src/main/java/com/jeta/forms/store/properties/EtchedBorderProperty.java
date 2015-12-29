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
import javax.swing.border.EtchedBorder;

import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A set of attributes that define an <code>EtchedBorder</code>. See:
 * {@link javax.swing.EtchedBorder}
 * 
 * @author Jeff Tassin
 */
public class EtchedBorderProperty extends BorderProperty {
	static final long serialVersionUID = -8742021440405641715L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 2;

	/**
	 * The type of etch: EtchedBorder.RAISED, LOWERED
	 */
	private int m_type;

	/**
	 * The highlight color for the border.
	 */
	private ColorProperty m_highlightColor = new ColorProperty(ColorProperty.DEFAULT_COLOR);

	/**
	 * The shadow color for the border.
	 */
	private ColorProperty m_shadowColor = new ColorProperty(ColorProperty.DEFAULT_COLOR);

	/**
	 * Creates a raised <code>EtchedBorderProperty</code> with default colors.
	 */
	public EtchedBorderProperty() {
		setBorder(new EtchedBorder(EtchedBorder.RAISED));
	}

	/**
	 * Creates an <code>EtchedBorderProperty</code> with the specified etch
	 * type.
	 * 
	 * @param etchType
	 *            the etch type for the border. Either: EtchedBorder.RAISED or
	 *            LOWERED.
	 */
	public EtchedBorderProperty(int etchType) {
		setBorder(new EtchedBorder(etchType));
	}

	/**
	 * BorderProperty implementation. Creates an <code>EtchedBorder</code>
	 * instance with the attributes specified by this property.
	 * 
	 * @return an EtchedBorder instance.
	 */
	public Border createBorder(Component comp) {

		Color h = null;
		Color s = null;

		ColorProperty cprop = getHighlightColorProperty();
		if (!cprop.getColorKey().equals(ColorProperty.DEFAULT_COLOR) && cprop.getColor() != null) {
			/**
			 * a color proxy is a Color that can change depending on the current
			 * look and feel
			 */
			h = new ColorProxy(cprop);
		}

		cprop = getShadowColorProperty();
		if (!cprop.getColorKey().equals(ColorProperty.DEFAULT_COLOR) && cprop.getColor() != null) {
			s = new ColorProxy(cprop);
		}

		Border b = new EtchedBorder(getEtchType(), h, s);
		return createTitle(b);
	}

	/**
	 * Returns the type of etched border: EtchedBorder.RAISED, LOWERED
	 * 
	 * @return the etch type
	 */
	public int getEtchType() {
		return m_type;
	}

	/**
	 * Returns the border highlight color
	 */
	public Color getHighlightColor() {
		return m_highlightColor.getColor();
	}

	/**
	 * Returns the border shadow color
	 */
	public Color getShadowColor() {
		return m_shadowColor.getColor();
	}

	/**
	 * Returns the border highlight color property
	 */
	public ColorProperty getHighlightColorProperty() {
		return m_highlightColor;
	}

	/**
	 * Returns the border shadow color property
	 */
	public ColorProperty getShadowColorProperty() {
		return m_shadowColor;
	}

	/**
	 * Sets the attributes of this property to that of the specified
	 * EtchedBorder.
	 * 
	 * @param bb
	 *            the etched border whose attributes to get.
	 */
	public void setBorder(EtchedBorder bb) {
		m_type = bb.getEtchType();
	}

	/**
	 * Sets the type of etched border: EtchedBorder.RAISED, LOWERED
	 * 
	 * @param type
	 *            the etch type
	 */
	public void setEtchType(int type) {
		m_type = type;
	}

	/**
	 * Sets the border highlight color property
	 */
	public void setHighlightColorProperty(ColorProperty c) {
		m_highlightColor = c;
	}

	/**
	 * Sets the border shadown color property
	 */
	public void setShadowColorProperty(ColorProperty c) {
		m_shadowColor = c;
	}

	/**
	 * Sets this property to that of another EtchedBorderProperty.
	 * 
	 * @param prop
	 *            an EtchedBorderProperty instance.
	 */
	public void setValue(Object prop) {
		super.setValue(prop);
		if (prop instanceof EtchedBorderProperty) {
			EtchedBorderProperty bp = (EtchedBorderProperty) prop;
			m_type = bp.m_type;
			m_highlightColor = bp.m_highlightColor;
			m_shadowColor = bp.m_shadowColor;
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
		if (version == 1) {
			m_highlightColor.setConstantColor((Color) in.readObject("highlightclor"));
			m_shadowColor.setConstantColor((Color) in.readObject("shadowcolor"));
		}
		else {
			m_highlightColor.setValue((ColorProperty) in.readObject("highlightcolor"));
			m_shadowColor.setValue((ColorProperty) in.readObject("shadowcolor"));
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("type", m_type);
		out.writeObject("highlightcolor", m_highlightColor);
		out.writeObject("shadowcolor", m_shadowColor);
	}

	public String toString() {
		return "ETCHED";
	}
}
