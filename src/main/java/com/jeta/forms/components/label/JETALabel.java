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

package com.jeta.forms.components.label;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import com.jeta.forms.gui.effects.Paintable;
import com.jeta.forms.gui.effects.Painter;

/**
 * A standard JLabel that also supports anti-aliased fonts as well as background
 * effects such as a texture or gradient.
 * 
 * @author Jeff Tassin
 */
public class JETALabel extends JLabel implements Paintable {
	/**
	 * Responsible for rendering the fill background pattern.
	 */
	private Painter m_painter;

	/**
	 * Used for rendering the painter.
	 */
	private Rectangle m_bounds;

	/**
	 * Flag that indicates if the font in this label is anti-aliased.
	 */
	private boolean m_antialiased = false;

	/**
	 * Default <code>JETALabel</code> constructor.
	 */
	public JETALabel() {
	}

	/**
	 * Creates a new JETALabel with the specified text.
	 * 
	 * @param txt
	 *            the text to assign to the label.
	 */
	public JETALabel(String txt) {
		super(txt);
	}

	/**
	 * Returns if this the text in this label is rendered using an anti-aliased
	 * effect.
	 * 
	 * @return true if this text in this label is anti-aliased.
	 */
	public boolean isAntiAliased() {
		return m_antialiased;
	}

	/**
	 * Override paint to provide background fill.
	 * 
	 * @param g
	 *            the <code>Graphics</code> context in which to paint
	 */
	public void paint(Graphics g) {
		if (m_painter != null) {
			if (m_bounds == null)
				m_bounds = new Rectangle();

			m_bounds.setBounds(0, 0, getWidth(), getHeight());
			m_painter.paint(this, g, m_bounds);
		}
		super.paint(g);
	}

	/**
	 * Override paintComponent to provide anti-aliased rendering of the label
	 * font.
	 * 
	 * @param g
	 *            the <code>Graphics</code> context in which to paint
	 */
	public void paintComponent(Graphics g) {
		if (isAntiAliased()) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		super.paintComponent(g);
	}

	/**
	 * Sets the flag for this label that determines if the text is rendered
	 * using an anti-aliased effect.
	 * 
	 * @param aa
	 *            set to true if this text in this label should be anti-aliased.
	 */
	public void setAntiAliased(boolean aa) {
		m_antialiased = aa;
	}

	/**
	 * Sets the painter responsible for any background fill effect.
	 * 
	 * @param p
	 *            the painter to set.
	 */
	public void setBackgroundPainter(Painter p) {
		m_painter = p;
		if (p != null)
			setOpaque(false);
	}
}
