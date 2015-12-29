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

package com.jeta.forms.components.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.jeta.forms.gui.effects.ImagePainter;
import com.jeta.forms.store.properties.ImageProperty;

/**
 * A component that displays an image. The image can be aligned horizontally:
 * LEFT, CENTER, and RIGHT and vertically: TOP, CENTER, BOTTOM.
 * 
 * This component is used instead if JLabel because it has fewer properties and
 * is more intuitive when used in the designer.
 * 
 * @author Jeff Tassin
 */
public class ImageComponent extends JComponent {

	/**
	 * The horizontal alignment LEFT, CENTER, RIGHT
	 */
	private int m_halign = ImageProperty.LEFT;

	/**
	 * The vertical alignment TOP, CENTER, BOTTOM
	 */
	private int m_valign = ImageProperty.TOP;

	/**
	 * The image
	 */
	private Icon m_icon;

	/**
	 * The preferred size
	 */
	private Dimension m_pref_size = new Dimension(16, 16);

	/**
	 * The object that actually renderes the image.
	 */
	private ImagePainter m_painter;

	/**
	 * A rectangle used for holding the paint bounds so we don't have to
	 * re-instantiate with every paint.
	 */
	private Rectangle m_painter_rect;

	/**
	 * ctor
	 */
	public ImageComponent() {
	}

	/**
	 * @return the horizontal alignment for this image (LEFT,CENTER,RIGHT)
	 */
	public int getHorizontalAlignment() {
		return m_halign;
	}

	/**
	 * @return the preferred size
	 */
	public Dimension getPreferredSize() {
		Insets insets = getInsets();
		m_pref_size.setSize(insets.left + insets.right + getIconWidth(), insets.top + insets.bottom + getIconHeight());
		return m_pref_size;
	}

	/**
	 * @return the minimum size
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * @return the vertical alignment for this image (TOP,CENTER,BOTTOM)
	 */
	public int getVerticalAlignment() {
		return m_valign;
	}

	/**
	 * @return the icon for this image
	 */
	public Icon getIcon() {
		return m_icon;
	}

	/**
	 * @return the height of the icon
	 */
	public int getIconHeight() {
		if (m_icon == null) {
			return 16;
		}
		else
			return m_icon.getIconHeight();
	}

	/**
	 * @return the width of the icon
	 */
	public int getIconWidth() {
		if (m_icon == null) {
			return 16;
		}
		else
			return m_icon.getIconWidth();
	}

	/**
	 * Sets the horizontal alignment for this image (LEFT,CENTER,RIGHT)
	 */
	public void setHorizontalAlignment(int align) {
		m_halign = align;
		m_painter = null;
	}

	/**
	 * Sets the icon for this iamge
	 */
	public void setIcon(Icon icon) {

		m_icon = icon;
		if (icon != null) {
			m_pref_size.setSize(Math.max(16, icon.getIconWidth()), Math.max(16, icon.getIconHeight()));
		}
		m_painter = null;
		revalidate();
	}

	/**
	 * Sets the vertical alignment for this image (TOP,CENTER,BOTTOM)
	 */
	public void setVerticalAlignment(int align) {
		m_valign = align;
		m_painter = null;
	}

	/**
	 * Renders the image on the graphics context
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Insets insets = getInsets();
		int x = insets.left;
		int y = insets.top;
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;

		if (m_painter == null)
			m_painter = new ImagePainter(m_icon, getHorizontalAlignment(), getVerticalAlignment());

		if (m_painter_rect == null)
			m_painter_rect = new Rectangle();

		m_painter_rect.setBounds(x, y, width, height);

		if (isOpaque()) {
			Color bg = getBackground();
			if (bg != null) {
				Rectangle clip_rect = g.getClipBounds();
				if (m_painter_rect.intersects(clip_rect)) {
					Rectangle rect = m_painter_rect.intersection(clip_rect);
					g.setClip(rect.x, rect.y, rect.width, rect.height);
					Color old_color = g.getColor();
					g.setColor(bg);
					g.fillRect(rect.x, rect.y, rect.width, rect.height);
					g.setColor(old_color);
				}
				g.setClip(clip_rect.x, clip_rect.y, clip_rect.width, clip_rect.height);
			}
		}
		m_painter.paint(this, g, m_painter_rect);
	}
}
