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

import java.awt.Font;

import javax.swing.JComponent;

/**
 * A component that simulates the left or right side of a TitledBorder. This
 * component is useful when flattening layouts. It allows a titled border to be
 * used to separate components, while allowing those components to stay in the
 * same row or column of the form. This allows components to have the same x,y,
 * width, or height, yet have differnt title borders. This is not possible when
 * using standard TitledBorder.
 * 
 * @author Jeff Tassin
 */
public class TitledBorderSide extends JComponent {
	/**
	 * The font for the title bar. This is only needed if you change the default
	 * font in the TitledBorderLabel. The font is needed to calculate the top
	 * part of this component so that it is properly aligned with the
	 * TitledBorderLabel.
	 */
	private Font m_font;

	/**
	 * LEFT or RIGHT
	 */
	private int m_orientation;

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	/**
	 * ctor
	 */
	public TitledBorderSide() {
		updateUI();
	}

	/**
	 * Returns the font for this component. If the font has not been set, the
	 * default TitledBorder.font for the current look and feel is returned.
	 */
	public Font getFont() {
		Font f = m_font;
		if (f == null)
			f = javax.swing.UIManager.getFont("TitledBorder.font");

		return f;
	}

	/**
	 * Returns the orienation for this component: LEFT or RIGHT
	 */
	public int getOrientation() {
		return m_orientation;
	}

	/**
	 * Sets the font for this component. If the font has not been set, the
	 * default TitledBorder.font for the current look and feel is returned.
	 */
	public void setFont(Font font) {
		m_font = font;
	}

	/**
	 * Sets the orienation for this component: LEFT or RIGHT
	 * 
	 * @param orientation
	 *            the orientation (LEFT or RIGHT).
	 */
	public void setOrientation(int orientation) {
		if (orientation != LEFT && orientation != RIGHT)
			orientation = LEFT;

		m_orientation = orientation;
	}

	/**
	 * Resets the UI property to a value from the current look and feel.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		setUI(new TitledBorderSideUI());
	}

}
