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

package com.jeta.forms.components.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.LineBorder;

/**
 * Extension of a line border that supports painting specific sides of the
 * border (i.e. top, left, bottom, or right ).
 * 
 * @author Jeff Tassin
 */
public class JETALineBorder extends LineBorder {
	private boolean m_top = true;
	private boolean m_left = true;
	private boolean m_bottom = true;
	private boolean m_right = true;

	public JETALineBorder() {
		super(Color.black);
	}

	/**
	 * ctor
	 */
	public JETALineBorder(Color c, int thickness, boolean curved, boolean top, boolean left, boolean bottom, boolean right) {
		super(c, thickness, curved);
		m_top = top;
		m_left = left;
		m_bottom = bottom;
		m_right = right;
	}

	/**
	 * Returns the insets of the border.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c) {
		if (getRoundedCorners())
			return super.getBorderInsets(c);

		int thickness = getThickness();
		return new Insets((m_top ? thickness : 0), (m_left ? thickness : 0), (m_bottom ? thickness : 0), (m_right ? thickness : 0));
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @param insets
	 *            the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		if (getRoundedCorners())
			return super.getBorderInsets(c, insets);

		insets.left = (m_left ? thickness : 0);
		insets.top = (m_top ? thickness : 0);
		insets.right = (m_right ? thickness : 0);
		insets.bottom = (m_bottom ? thickness : 0);
		return insets;
	}

	/**
	 * Renders the border on the graphics context.
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if (getRoundedCorners() || (m_top && m_bottom && m_right && m_left)) {
			super.paintBorder(c, g, x, y, width, height);
		}
		else {

			Color oldColor = g.getColor();
			int i;

			g.setColor(lineColor);
			for (i = 0; i < thickness; i++) {
				int top = y + i;
				int left = x + i;
				int right = left + width - i - i - 1;
				int bottom = top + height - i - i - 1;

				if (m_top) {
					g.drawLine(x, top, x + width - 1, top);
				}

				if (m_left) {
					g.drawLine(left, y, left, y + height - 1);
				}

				if (m_bottom) {
					g.drawLine(x, bottom, x + width - 1, bottom);
				}

				if (m_right) {
					g.drawLine(right, y, right, y + height - 1);
				}
			}
			g.setColor(oldColor);
		}
	}
}
