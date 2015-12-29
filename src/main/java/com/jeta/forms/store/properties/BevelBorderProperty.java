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

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>BevelBorderProperty</code> stores the attributes for a Swing bevel
 * border.
 * 
 * @author Jeff Tassin
 */
public class BevelBorderProperty extends BorderProperty {
	static final long serialVersionUID = -9171261099090722557L;

	public static final int VERSION = 2;

	/**
	 * The type of bevel: BevelBorder.RAISED, LOWERED
	 */
	private int m_type;

	/**
	 * The colors for the border.
	 */
	private ColorProperty m_highlightOuter = new ColorProperty(ColorProperty.DEFAULT_COLOR);

	private ColorProperty m_highlightInner = new ColorProperty(ColorProperty.DEFAULT_COLOR);

	private ColorProperty m_shadowOuter = new ColorProperty(ColorProperty.DEFAULT_COLOR);

	private ColorProperty m_shadowInner = new ColorProperty(ColorProperty.DEFAULT_COLOR);

	/**
	 * Creates a raised <code>BevelBorderProperty</code> instance.
	 */
	public BevelBorderProperty() {
		setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	/**
	 * Creates a <code>BevelBorderProperty</code> instance with the specified
	 * bevel type.
	 * 
	 * @param bevelType
	 *            the type of bevel for the border. Either BevelBorder.RAISED or
	 *            BevelBorder.LOWERED.
	 */
	public BevelBorderProperty(int bevelType) {
		setBorder(new BevelBorder(bevelType));
	}

	/**
	 * BorderProperty.createBorder specialization. Creates a BevelBorder
	 * instance based on the values in this BorderProperty instance.
	 * 
	 * @return a BevelBorder instance based on the values in this BorderProperty
	 *         instance.
	 */
	public Border createBorder(Component comp) {
		Color h_outer = null;
		Color h_inner = null;
		Color s_outer = null;
		Color s_inner = null;

		ColorProperty cprop = getHighlightOuterColorProperty();
		if (cprop != null && !cprop.getColorKey().equals(ColorProperty.DEFAULT_COLOR) && cprop.getColor() != null) {
			h_outer = new ColorProxy(cprop);
		}

		cprop = getHighlightInnerColorProperty();
		if (cprop != null && !cprop.getColorKey().equals(ColorProperty.DEFAULT_COLOR) && cprop.getColor() != null) {
			h_inner = new ColorProxy(cprop);
		}

		cprop = getShadowOuterColorProperty();
		if (cprop != null && !cprop.getColorKey().equals(ColorProperty.DEFAULT_COLOR) && cprop.getColor() != null) {
			s_outer = new ColorProxy(cprop);
		}

		cprop = getShadowInnerColorProperty();
		if (cprop != null && !cprop.getColorKey().equals(ColorProperty.DEFAULT_COLOR) && cprop.getColor() != null) {
			s_inner = new ColorProxy(cprop);
		}

		Border b = new BevelBorder(getBevelType(), h_outer, h_inner, s_outer, s_inner);
		return createTitle(b);
	}

	/**
	 * Returns the bevel type for this border. Either BevelBorder.RAISED or
	 * BevelBorder.LOWERED.
	 * 
	 * @return the bevel type for this border.
	 */
	public int getBevelType() {
		return m_type;
	}

	/**
	 * Returns the highlight outer color for the border.
	 * 
	 * @return the highlight outer color for the border.
	 */
	public Color getHighlightOuterColor() {
		return m_highlightOuter.getColor();
	}

	/**
	 * Returns the highlight inner color for the border.
	 * 
	 * @return the highlight inner color for the border.
	 */
	public Color getHighlightInnerColor() {
		return m_highlightInner.getColor();
	}

	/**
	 * Returns the shadow inner color for the border.
	 * 
	 * @return the shadow inner color for the border.
	 */
	public Color getShadowInnerColor() {
		return m_shadowInner.getColor();
	}

	/**
	 * Returns the shadow outer color for the border.
	 * 
	 * @return the shadow outer color for the border.
	 */
	public Color getShadowOuterColor() {
		return m_shadowOuter.getColor();
	}

	/**
	 * Returns the highlight outer ColorProperty for the border.
	 * 
	 * @return the highlight outer ColorProperty for the border.
	 */
	public ColorProperty getHighlightOuterColorProperty() {
		return m_highlightOuter;
	}

	/**
	 * Returns the highlight inner ColorProperty for the border.
	 * 
	 * @return the highlight inner ColorProperty for the border.
	 */
	public ColorProperty getHighlightInnerColorProperty() {
		return m_highlightInner;
	}

	/**
	 * Returns the shadow inner ColorProperty for the border.
	 * 
	 * @return the shadow inner ColorProperty for the border.
	 */
	public ColorProperty getShadowInnerColorProperty() {
		return m_shadowInner;
	}

	/**
	 * Returns the shadow outer ColorProperty for the border.
	 * 
	 * @return the shadow outer ColorProperty for the border.
	 */
	public ColorProperty getShadowOuterColorProperty() {
		return m_shadowOuter;
	}

	/**
	 * Sets the attributes of this property to those of the given bevel border
	 * instance.
	 * 
	 * @param bb
	 *            the bevel border instance
	 */
	private void setBorder(BevelBorder bb) {
		m_type = bb.getBevelType();
	}

	/**
	 * Sets the bevel border type.
	 * 
	 * @param type
	 *            the type of border: BevelBorder.RAISED or BevelBorder.LOWERED.
	 */
	public void setBevelType(int type) {
		m_type = type;
	}

	/**
	 * Sets the highlight outer ColorProperty for the border.
	 * 
	 * @param c
	 *            the highlight outer ColorProperty for the border.
	 */
	public void setHighlightOuterColorProperty(ColorProperty c) {
		m_highlightOuter = c;
	}

	/**
	 * Sets the highlight inner ColorProperty for the border.
	 * 
	 * @param c
	 *            the highlight inner ColorProperty for the border.
	 */
	public void setHighlightInnerColorProperty(ColorProperty c) {
		m_highlightInner = c;
	}

	/**
	 * Sets the shadow inner ColorProperty for the border.
	 * 
	 * @param c
	 *            the shadow inner ColorProperty for the border.
	 */
	public void setShadowInnerColorProperty(ColorProperty c) {
		m_shadowInner = c;
	}

	/**
	 * Sets the shadow outer ColorProperty for the border.
	 * 
	 * @param c
	 *            the shadow outer ColorProperty for the border.
	 */
	public void setShadowOuterColorProperty(ColorProperty c) {
		m_shadowOuter = c;
	}

	/**
	 * Sets this border property to that of another bevel border property.
	 * 
	 * @param prop
	 *            the value to set. This should be a BevelBorderPropery
	 *            instance.
	 */
	public void setValue(Object prop) {
		super.setValue(prop);
		if (prop instanceof BevelBorderProperty) {
			BevelBorderProperty bp = (BevelBorderProperty) prop;
			m_type = bp.m_type;
			m_highlightOuter.setValue(bp.m_highlightOuter);
			m_highlightInner.setValue(bp.m_highlightInner);
			m_shadowOuter.setValue(bp.m_shadowOuter);
			m_shadowInner.setValue(bp.m_shadowInner);
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
			m_highlightOuter.setConstantColor((Color) in.readObject("highlightouter"));
			m_highlightInner.setConstantColor((Color) in.readObject("highlightinner"));
			m_shadowOuter.setConstantColor((Color) in.readObject("shadowouter"));
			m_shadowInner.setConstantColor((Color) in.readObject("shadowinner"));
		}
		else {
			m_highlightOuter = (ColorProperty) in.readObject("highlightouter");
			m_highlightInner = (ColorProperty) in.readObject("ightlightinner");
			m_shadowOuter = (ColorProperty) in.readObject("shadowouter");
			m_shadowInner = (ColorProperty) in.readObject("shadowinner");
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(BorderProperty.class));
		out.writeVersion(VERSION);
		out.writeInt("type", m_type);
		out.writeObject("highlightouter", m_highlightOuter);
		out.writeObject("highlightinner", m_highlightInner);
		out.writeObject("shadowouter", m_shadowOuter);
		out.writeObject("shadowinner", m_shadowInner);
	}

	public String toString() {
		return "BEVEL";
	}
}
