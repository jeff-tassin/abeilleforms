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
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.jeta.open.i18n.I18N;

/**
 * A component that simulates the top part of a TitledBorder. This component is
 * useful when flattening layouts. It allows a titled border to be used to
 * separate components, while allowing those components to stay in the same row
 * or column of the form. This allows components to have the same x,y, width, or
 * height, yet have differnt title borders. This is not possible when using
 * standard TitledBorder.
 * 
 * @author Jeff Tassin
 */
public class TitledBorderLabel extends JComponent {
	/**
	 * The text for the label
	 */
	private String m_title = I18N.getLocalizedMessage("Title");

	/**
	 * The font for the label
	 */
	private Font m_font;

	/**
	 * The text color
	 */
	private Color m_title_color;

	/**
	 * The justification for the title (see TitledBorder justification)
	 */
	private int m_justification = TitledBorder.DEFAULT_JUSTIFICATION;

	/**
	 * ctor
	 */
	public TitledBorderLabel() {
		updateUI();
	}

	/**
	 * Returns the title text.
	 */
	public String getText() {
		return m_title;
	}

	/**
	 * Returns the title text color.
	 */
	public Color getTitleColor() {
		Color c = m_title_color;
		if (c == null)
			c = UIManager.getColor("TitledBorder.titleColor");
		return c;
	}

	/**
	 * Returns the justfication of the title (see TitledBorder justification)
	 */
	public int getTitleJustification() {
		return m_justification;
	}

	/**
	 * Returns the title font
	 */
	public Font getFont() {
		Font f = m_font;
		if (f == null)
			f = UIManager.getFont("TitledBorder.font");

		return f;
	}

	/**
	 * Resets the UI property to a value from the current look and feel.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		setUI(new TitledBorderLabelUI());
	}

	/**
	 * Sets the title text
	 */
	public void setText(String title) {
		m_title = title;
	}

	/**
	 * Sets the title color.
	 */
	public void setTitleColor(Color c) {
		m_title_color = c;
	}

	/**
	 * Sets the title font.
	 */
	public void setFont(Font font) {
		m_font = font;
	}

	/**
	 * Sets the title justification (see TitledBorder justfication).
	 */
	public void setTitleJustification(int justification) {
		m_justification = justification;
	}

}
