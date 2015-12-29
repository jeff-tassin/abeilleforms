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
import java.io.IOException;

import com.jeta.forms.colormgr.ColorManager;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.registry.JETARegistry;

/**
 * A <code>ColorProperty</code> object defines a 'dynamic' color value for
 * some components in this architecture. A dynamic color is one that depends on
 * the current look and feel. For example, the background for a JButton has a
 * different color for the Windows Look and Feel than it does for the Metal Look
 * and Feel. This is important for effects such as gradients that need to fade
 * out to the current background (which can change if the look and feel
 * changes). The designer allows the user to select a color based on its
 * resource name. This class uses the resource name to get the actual color from
 * the ColorManager.
 * 
 * @see com.jeta.forms.colormgr.ColorManager#getColor(String,java.awt.Color)
 * @author Jeff Tassin
 */
public class ColorProperty extends JETAProperty {
	static final long serialVersionUID = -4907629297184715948L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 2;

	/**
	 * Color key name for a constant color
	 */
	public static final String CONSTANT_COLOR = "constant";

	/**
	 * This is for those components/borders that define their colors based on
	 * parent components or any associated components (in the case of borders).
	 */
	public static final String DEFAULT_COLOR = "default";

	/**
	 * The color to use if for a constant color
	 */
	private Color m_constant_color = Color.black;

	/**
	 * The resource name for the color
	 */
	private String m_color_key = CONSTANT_COLOR;

	/**
	 * The brightness determines if the given color should be made brighter or
	 * darker depending on the selected color. +1 Color.brighter -1
	 * Color.darker.
	 */
	private int m_brightness = 0;

	private float m_brightness_factor = 0.7f;

	/**
	 * The default brightness factor
	 */
	public static final float DEFAULT_FACTOR = 0.7f;
	public static final int DEFAULT_BRIGHTNESS = 0;

	/**
	 * The property name
	 */
	public static final String PROPERTY_ID = "dyncolor";

	/**
	 * Creates a <code>ColorProperty</code> instance that specifies a
	 * constant, black color.
	 */
	public ColorProperty() {
		super(PROPERTY_ID);
	}

	/**
	 * Creates a constant <code>ColorProperty</code> instance with the
	 * specified color
	 * 
	 * @param c
	 *            the color to set.
	 */
	public ColorProperty(Color c) {
		super(PROPERTY_ID);

		m_constant_color = c;
		m_color_key = CONSTANT_COLOR;
	}

	/**
	 * Creates a dynamic <code>ColorProperty</code> instance with the
	 * specified color resource name as defined by the UIManager.
	 * 
	 * @param colorKey
	 *            the name of the color resource.
	 * @see javax.swing.UIManager#getColor(String)
	 */
	public ColorProperty(String colorKey) {
		super(PROPERTY_ID);
		m_color_key = colorKey;
		assert (!CONSTANT_COLOR.equals(colorKey));
	}

	/**
	 * Creates a brigher color based on the given factor. Based on the logic in
	 * the java.awt.Color class
	 * 
	 * @param c
	 *            the color to brighten
	 * @param factor
	 *            the brightness factor
	 * @see java.awt.Color#brighter
	 */
	private Color brighter(Color c, float factor) {
		if (factor < 0.01)
			factor = 0.01f;
		else if (factor > 1.0)
			factor = 1.0f;

		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		int i = (int) (1.0 / (1.0 - factor));
		if (red == 0 && green == 0 && blue == 0) {
			return new Color(i, i, i);
		}

		if (red > 0 && red < i)
			red = i;

		if (green > 0 && green < i)
			green = i;

		if (blue > 0 && blue < i)
			blue = i;

		return new Color(Math.min((int) (red / factor), 255), Math.min((int) (green / factor), 255), Math.min((int) (blue / factor), 255));
	}

	/**
	 * Creates a darker color given a color and a factor.
	 * 
	 * @param c
	 *            the color to darken
	 * @param factor
	 *            the brightness factor
	 * @see java.awt.Color#darker
	 */
	public Color darker(Color c, float factor) {
		return new Color(Math.max((int) (c.getRed() * factor), 0), Math.max((int) (c.getGreen() * factor), 0), Math.max((int) (c.getBlue() * factor), 0));
	}

	/**
	 * Object equals implementation.
	 */
	public boolean equals(Object object) {
		if (object instanceof ColorProperty) {
			ColorProperty cp = (ColorProperty) object;
			return (super.equals(object) && isEqual(m_constant_color, cp.m_constant_color) && isEqual(m_color_key, cp.m_color_key)
					&& m_brightness == cp.m_brightness && m_brightness_factor == cp.m_brightness_factor);
		}
		else {
			return false;
		}
	}

	/**
	 * Returns the color specified by this property. If the this property
	 * represents a constant color, then the constant color is returned.
	 * Otherwise, the color is retrieved from the color manager. Currently, the
	 * color manager merely forwards the call to the UIManager.
	 * 
	 * @return the color specified by this property.
	 */
	public Color getColor() {
		Color result = Color.white;
		if (isConstant()) {
			result = m_constant_color;
		}
		else {
			ColorManager cmgr = (ColorManager) JETARegistry.lookup(ColorManager.COMPONENT_ID);
			if (cmgr != null)
				result = cmgr.getColor(m_color_key, null);
		}
		if (result == null)
			result = Color.white;

		result = modifyBrightness(result);
		return result;
	}

	/**
	 * Returns the brightness value for this color
	 * 
	 * @return the brightness value for this color
	 */
	public int getBrightness() {
		return m_brightness;
	}

	/**
	 * Returns the brightness factor for this color
	 * 
	 * @return the brightness factor for this color
	 */
	public float getBrightnessFactor() {
		return m_brightness_factor;
	}

	/**
	 * @return the resource name for the color. This is the name passed to the
	 *         ColorManager.
	 */
	public String getColorKey() {
		return m_color_key;
	}

	public Color getConstantColor() {
		return m_constant_color;
	}

	/**
	 * Modifies the color based on the given brightess values
	 */
	private Color modifyBrightness(Color result) {
		for (int count = 0; count < Math.abs(m_brightness); count++) {
			if (m_brightness > 0) {
				result = brighter(result, m_brightness_factor);
			}
			else if (m_brightness < 0) {
				result = darker(result, m_brightness_factor);
			}
		}
		return result;
	}

	public void setBrightness(int ival) {
		m_brightness = ival;
	}

	public void setBrightnessFactor(float fval) {
		m_brightness_factor = fval;
	}

	public void setColorKey(String color_key) {
		m_color_key = color_key;
	}

	/**
	 * @return true if the property represents a constant color
	 */
	public boolean isConstant() {
		return "constant".equalsIgnoreCase(m_color_key);
	}

	public void setConstantColor(Color c) {
		m_constant_color = c;
		m_color_key = CONSTANT_COLOR;
	}

	/**
	 * Sets this property to that of another property.
	 */
	public void setValue(Object prop) {
		if (prop instanceof ColorProperty) {
			ColorProperty cprop = (ColorProperty) prop;
			m_color_key = cprop.m_color_key;
			m_constant_color = cprop.m_constant_color;
			m_brightness = cprop.m_brightness;
			m_brightness_factor = cprop.m_brightness_factor;
		}
		else if (prop != null) {
			assert (false);
		}
	}

	/**
	 * Updates the bean
	 */
	public void updateBean(JETABean jbean) {
		// no op
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read(in.getSuperClassInput());
		int version = in.readVersion();
		m_color_key = (String) in.readObject("colorkey");
		Object color = in.readObject("constantcolor");
		if (color instanceof Color)
			m_constant_color = (Color) color;
		else if (color instanceof ColorHolder)
			m_constant_color = ((ColorHolder) color).getColor();

		if (version > 1) {
			m_brightness = in.readInt("brightness", DEFAULT_BRIGHTNESS);
			m_brightness_factor = in.readFloat("brightnessfactor", DEFAULT_FACTOR);
		}
	}

	/**
	 * JETAPersistable Implementation
	 */
	public void write(JETAObjectOutput out) throws IOException {
		super.write(out.getSuperClassOutput(JETAProperty.class));
		out.writeVersion(VERSION);
		out.writeObject("colorkey", m_color_key);
		if (isConstant()) {
			out.writeObject("constantcolor", new ColorHolder(m_constant_color));
		}
		else {
			out.writeObject("constantcolor", null);
		}
		out.writeInt("brightness", m_brightness, DEFAULT_BRIGHTNESS);
		out.writeFloat("brightnessfactor", m_brightness_factor, DEFAULT_FACTOR);
	}

}
