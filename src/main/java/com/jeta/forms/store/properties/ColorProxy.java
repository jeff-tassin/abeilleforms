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
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * A <code>ColorProxy</code> is a Color that can change based on the current
 * look and feel. This proxy has a <code>ColorProperty</code> delegate that it
 * uses as a basis for the color. ColorProxys are important for effects that
 * need to change color based on the L&F. For example, in some gradients it is
 * desirable to fade out to the current background color. This gives a
 * transparent effect. However, the background color changes based on the L&F,
 * so we need to employ ColorProxys to change color to match the background in
 * these cases.
 * 
 * @author Jeff Tassin
 */
public class ColorProxy extends Color {
	private ColorProperty m_color_prop;

	/**
	 * Creates an uninitialized <code>ColorProxy</code> object.
	 */
	public ColorProxy() {
		super(255, 255, 255);

	}

	/**
	 * Creates a <code>ColorProxy</code> object with the specified
	 * ColorProperty delegate.
	 * 
	 * @param cprop
	 *            a ColorProperty that this object uses to get the current
	 *            color.
	 */
	public ColorProxy(ColorProperty cprop) {
		super(255, 255, 255);
		m_color_prop = new ColorProperty();
		m_color_prop.setValue(cprop);
	}

	/**
	 * Returns the ColorProperty delegate associated with this object.
	 */
	public ColorProperty getColorProperty() {
		return m_color_prop;
	}

	/**
	 * Sets the ColorProperty delegate associated with this object.
	 */
	public void setColorProperty(ColorProperty prop) {
		m_color_prop = prop;
	}

	/**
	 * Returns the underlying Color of the ColorProperty delegate.
	 */
	public Color getColor() {
		Color c = m_color_prop.getColor();
		if (c == null)
			c = Color.white;
		return c;
	}

	/**
	 * java.awt.Color override. Forward the call to the delegate.
	 */
	public int getRed() {
		return getColor().getRed();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public int getGreen() {
		return getColor().getGreen();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public int getBlue() {
		return getColor().getBlue();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public int getAlpha() {
		return getColor().getAlpha();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public int getRGB() {
		return getColor().getRGB();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public Color brighter() {
		return getColor().brighter();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public Color darker() {
		return getColor().darker();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public int hashCode() {
		return getColor().hashCode();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Color) {
			return obj.equals(getColor());
		}
		else if (obj instanceof ColorProxy) {
			return ((ColorProxy) obj).getColor().equals(getColor());
		}
		else {
			return false;
		}
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public String toString() {
		return getColor().toString();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public float[] getRGBComponents(float[] compArray) {
		return getColor().getRGBComponents(compArray);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public float[] getRGBColorComponents(float[] compArray) {
		return getColor().getRGBColorComponents(compArray);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public float[] getComponents(float[] compArray) {
		return getColor().getComponents(compArray);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public float[] getColorComponents(float[] compArray) {
		return getColor().getColorComponents(compArray);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public float[] getComponents(ColorSpace cspace, float[] compArray) {
		return getColor().getComponents(cspace, compArray);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public float[] getColorComponents(ColorSpace cspace, float[] compArray) {
		return getColor().getColorComponents(cspace, compArray);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public ColorSpace getColorSpace() {
		return getColor().getColorSpace();
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public synchronized PaintContext createContext(ColorModel cm, Rectangle r, Rectangle2D r2d, AffineTransform xform, RenderingHints hints) {
		return getColor().createContext(cm, r, r2d, xform, hints);
	}

	/**
	 * java.awt.Color. Forward the call to the delegate.
	 */
	public int getTransparency() {
		return getColor().getTransparency();
	}

}
