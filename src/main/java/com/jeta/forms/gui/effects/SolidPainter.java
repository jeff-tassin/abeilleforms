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

package com.jeta.forms.gui.effects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.jeta.forms.store.properties.effects.SolidProperty;

/**
 * This class is an implementation of a painter that renders a solid color on a
 * part of a canvas or component. Keep in mind that the color is constant only
 * for the current look and feel. If the look and feel changes, the color may
 * change depending settings specified by the user in the designer.
 * 
 * @author Jeff Tassin
 */
public class SolidPainter implements Painter {
	/**
	 * The property that defines the solid settings.
	 */
	private SolidProperty m_solid_prop;

	/**
	 * Creates a <code>SolidPainter</code> instance with no color attributes.
	 */
	public SolidPainter() {

	}

	/**
	 * Creates a <code>SolidPainter</code> instance with the specified color
	 * attributes.
	 */
	public SolidPainter(SolidProperty prop) {
		m_solid_prop = prop;
	}

	/**
	 * Painter Implementation. Fills a rectangle with a solid color.
	 * 
	 * @param g
	 *            the graphics context
	 * @param rect
	 *            the rectangle that defines the region to paint. Note, that
	 *            this is different than the clipping rectangle.
	 */
	public void paint(Component c, Graphics g, Rectangle rect) {
		if (rect == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		Color old_color = g2.getColor();

		g.setColor(m_solid_prop.getColor());
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g2.setColor(old_color);
	}

	/**
	 * Sets the color attributes for this painter
	 * 
	 * @param prop
	 *            the color properties to set for this painter.
	 */
	public void setSolidProperty(SolidProperty prop) {
		m_solid_prop = prop;
	}
}
