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

package com.jeta.forms.gui.form;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.jeta.forms.gui.effects.Painter;
import com.jeta.forms.logger.FormsLogger;

/**
 * This component renders a background effect for an entire form. When the user
 * sets the fill property in the designer for a form, this is the object that
 * will ultimately render the fill effect. The caller specifies the type of
 * painter such as solid, gradient, image, or texture. The background effect is
 * specified by a Painter object. See:
 * {@link com.jeta.forms.gui.effects.Painter}
 * 
 * @author Jeff Tassin
 */
public class BackgroundPainter extends JPanel {
	/**
	 * A painter for rendering a background effect. This can be null for no
	 * effect.
	 */
	private Painter m_background_painter;

	/**
	 * A rectangle used for specifing the paint area. We use this object so we
	 * don't have to re-instantiate everytime a paint operation is required.
	 */
	private Rectangle m_painter_rect;

	/**
	 * The preferred size for this component. Just set to 10x10 pixels so we
	 * don't have a 0x0 size.
	 */
	private Dimension m_pref_size = new Dimension(10, 10);

	/**
	 * Creates a <code>BackgroundPainter</code> instance with a null painter.
	 */
	public BackgroundPainter() {
		setOpaque(false);
	}

	/**
	 * Returns the preferred size for this component.
	 * 
	 * @return the preferred size for this component.
	 */
	public Dimension getPreferredSize() {
		return m_pref_size;
	}

	/**
	 * Override paintComponent so we can paint the fill effect
	 * 
	 * @param g
	 *            the graphics context.
	 */
	public void paintComponent(Graphics g) {
		try {
			if (m_background_painter != null) {
				if (m_painter_rect == null)
					m_painter_rect = new Rectangle();

				m_painter_rect.setBounds(0, 0, getWidth(), getHeight());
				m_background_painter.paint(this, g, m_painter_rect);
			}
		} catch (Exception e) {
			FormsLogger.severe(e);
		}
	}

	/**
	 * Sets an object used to render an effect on this components background
	 * 
	 * @param p
	 *            the painter for this background
	 */
	public void setBackgroundPainter(Painter p) {
		m_background_painter = p;
		m_painter_rect = null;
		repaint();
	}

	/**
	 * Override updateUI so we can force a repaint.
	 */
	public void updateUI() {
		super.updateUI();
		if (m_background_painter != null) {
			repaint();
		}
	}
}
