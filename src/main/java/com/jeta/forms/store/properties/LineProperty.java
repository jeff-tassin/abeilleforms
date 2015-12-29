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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.IOException;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A <code>LineProperty</code> is used to define the attributes for a single
 * line in a compound line component. See:
 * {@link com.jeta.forms.store.CompoundLineProperty}
 * 
 * @author Jeff Tassin
 */
public class LineProperty extends JETAProperty {
	static final long serialVersionUID = -6081805367391562785L;

	/**
	 * The version for this class.
	 */
	public static final int VERSION = 2;

	/**
	 * The line color
	 */
	private ColorProperty m_color_prop = new ColorProperty();

	/**
	 * The thickness of the line
	 */
	private int m_thickness = 1;

	/**
	 * The stroke definition. Currently not used.
	 */
	private float[] m_dash;

	/**
	 * For rendering.
	 */
	private Stroke m_stroke;

	public static final String PROPERTY_ID = "lineStyle";

	/**
	 * Creates a <code>LineProperty</code> object with a thickness of 1 pixel
	 * and a black color.
	 */
	public LineProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Returns the line color.
	 * 
	 * @return the line color
	 */
	public Color getColor() {
		return m_color_prop.getColor();
	}

	/**
	 * Returns the color property that defines the line color.
	 * 
	 * @return the color property
	 */
	public ColorProperty getColorProperty() {
		return m_color_prop;
	}

	/**
	 * Returns a Stroke object that is used to render the line in a graphics
	 * context.
	 * 
	 * @param maxThickness
	 *            defines the maximum thickness for the stroke. If the line
	 *            thickness for this property is greater than the maxThickness,
	 *            then the maxThickness is used.
	 * @return the stroke object.
	 */
	public Stroke getStroke(int maxThickness) {
		int thickness = Math.min(maxThickness, getThickness());
		if (m_dash != null && m_dash.length > 1) {
			return new BasicStroke((float) thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, m_dash, 0.0f);
		}
		else {
			return new BasicStroke((float) thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		}
	}

	/**
	 * Returns the Stroke object used to render the line.
	 * 
	 * @return a stroke defined by this property
	 */
	public Stroke getStroke() {
		if (m_stroke == null)
			m_stroke = getStroke(getThickness());
		return m_stroke;
	}

	/**
	 * Returns the line thickness in pixels.
	 * 
	 * @return the line thickness
	 */
	public int getThickness() {
		return m_thickness;
	}

	/**
	 * Returns the stroke style. Currently not used.
	 * 
	 * @return the stroke style
	 */
	public float[] getDash() {
		return m_dash;
	}

	/**
	 * Sets the color property to constant using the specified color
	 * 
	 * @param c
	 *            the color to apply to this line.
	 */
	public void setConstantColor(Color c) {
		m_color_prop.setConstantColor(c);
	}

	/**
	 * Sets the line color.
	 * 
	 * @param cprop
	 *            a color property.
	 */
	public void setColorProperty(ColorProperty cprop) {
		m_color_prop.setValue(cprop);
	}

	/**
	 * Sets the line thickness
	 * 
	 * @param t
	 *            the line thickness
	 */
	public void setThickness(int t) {
		m_thickness = t;
		m_stroke = null;
	}

	/**
	 * Sets the stroke style. Currently not used.
	 */
	public void setDash(float[] dash) {
		m_dash = dash;
		m_stroke = null;
	}

	/**
	 * Sets this property to that of another LineProperty
	 * 
	 * @param prop
	 *            a LineProperty instance.
	 */
	public void setValue(Object prop) {
		if (prop instanceof LineProperty) {
			LineProperty lp = (LineProperty) prop;
			m_color_prop.setValue(lp.m_color_prop);
			m_thickness = lp.m_thickness;
			m_dash = lp.m_dash;
		}
	}

	/**
	 * Prints this component state to the console
	 */
	public void print() {
		System.out.println("   line component:  thickness: " + getThickness() + "   color: " + getColor());
	}

	/**
	 * JETAProperty implementation. No op for this property.
	 */
	public void updateBean(JETABean jbean) {

	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());

		int version = in.readVersion();

		if (version == 1) {
			Color color = (Color) in.readObject("color");
			m_color_prop = new ColorProperty(color);
		}
		else {
			m_color_prop = (ColorProperty) in.readObject("color");
		}
		m_thickness = in.readInt("thickness");
		m_dash = (float[]) in.readObject("dash");

	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("color", m_color_prop);
		out.writeInt("thickness", m_thickness);
		out.writeObject("dash", m_dash);
	}

}
