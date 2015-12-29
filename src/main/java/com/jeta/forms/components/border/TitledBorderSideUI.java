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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

/**
 * The UI for a component that simulates the left or right side of a
 * TitledBorder. This component is useful when flattening layouts. It allows a
 * titled border to be used to separate components, while allowing those
 * components to stay in the same row or column of the form. This allows
 * components to have the same x,y, width, or height, yet have differnt title
 * borders. This is not possible when using standard TitledBorder.
 * 
 * @author Jeff Tassin
 */
public class TitledBorderSideUI extends ComponentUI {
	/** the preferred width and height for this component */
	private static final int PREF_WIDTH = 6;
	private static final int PREF_HEIGHT = 24;

	/**
	 * Returns the component's preferred size for the current look and feel.
	 * 
	 * @param c
	 *            the component whose preferred size is being queried
	 * @return the preferred size
	 */
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(PREF_WIDTH, PREF_HEIGHT);
	}

	/**
	 * Renders the border using the current look and feel.
	 * 
	 * @param g
	 *            the <code>Graphics</code> context in which to paint
	 * @param c
	 *            the component being painted;
	 */
	public void paint(Graphics g, JComponent c) {
		Border border = UIManager.getBorder("TitledBorder.border");
		if (c instanceof TitledBorderSide && border != null) {
			TitledBorderSide tb = (TitledBorderSide) c;

			Font font = tb.getFont();
			FontMetrics fm = c.getFontMetrics(font);

			int border_y = fm.getAscent() / 2 + TitledBorderLabelUI.VERTICAL_PADDING;
			int border_height = c.getHeight() - border_y;

			if (tb.getOrientation() == TitledBorderSide.LEFT) {
				/**
				 * render the border at an x-offset so that the right side is
				 * clipped by the component
				 */
				int border_x = Math.max(0, c.getWidth() - PREF_WIDTH / 2 - 2);
				border.paintBorder(c, g, border_x, border_y, c.getWidth(), border_height);
			}
			else {
				/**
				 * render the border at a negative x-offset so that the left
				 * side is clipped by the component
				 */
				border.paintBorder(c, g, -PREF_WIDTH, border_y, c.getWidth() + 1, border_height);
			}
		}
	}

}
