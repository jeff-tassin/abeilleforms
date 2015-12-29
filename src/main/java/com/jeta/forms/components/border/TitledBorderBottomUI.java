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
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

/**
 * The UI for a component that simulates the bottom of a TitledBorder. This
 * component is useful when flattening layouts. It allows a titled border to be
 * used to separate components, while allowing those components to stay in the
 * same row or column of the form. This allows components to have the same x,y,
 * width, or height, yet have differnt title borders. This is not possible when
 * using standard TitledBorder.
 * 
 * @author Jeff Tassin
 */
public class TitledBorderBottomUI extends ComponentUI {
	/** the preferred width and height */
	private static final int PREF_WIDTH = 24;
	private static final int PREF_HEIGHT = 6;

	/**
	 * Calculates the preferred size for a widget
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
		if (c instanceof TitledBorderBottom && border != null) {
			/**
			 * render the border at a negative x-offset and y-offset (4 pixels)
			 * so that the top of the rectangle is clipped by the component
			 */
			border.paintBorder(c, g, -4, -4, c.getWidth() + 8, c.getHeight() + 4);
		}
	}

}
