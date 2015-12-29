/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.gui.font;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JComponent;

/**
 * Displays the current font
 * 
 * @author Jeff Tassin
 */
public class FontSampleComponent extends JComponent {
	private static final String TEXT_SAMPLE = "AaBbYyZz 012345...";
	/**
	 * The current font
	 */
	private Font m_font;

	/**
	 * The preferred size
	 */
	private Dimension m_pref_size;

	/**
	 * ctor
	 */
	public FontSampleComponent(Font f) {
		m_font = f;
		if (f == null) {
			m_font = javax.swing.UIManager.getFont("Table.font");
		}
		setOpaque(true);
		setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
	}

	/**
	 * @return the preferred size for this component
	 */
	public Dimension getPreferredSize() {
		if (m_pref_size == null) {
			m_pref_size = new Dimension(50, 20);
		}
		return m_pref_size;
	}

	/**
	 * Paints the line
	 */
	public void paintComponent(Graphics g) {
		if (m_font == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		Font old_font = g2.getFont();
		Color old_color = g2.getColor();

		Insets insets = getInsets();
		int height = getHeight() - insets.top - insets.bottom;
		int width = getWidth() - insets.left - insets.right;

		g2.setColor(Color.white);
		g2.fillRect(insets.left, insets.top, width, height);

		g2.setColor(Color.black);
		g2.setFont(m_font);

		FontMetrics fm = g.getFontMetrics();

		int line_height = fm.getHeight();
		int y = height - (height - line_height) / 2 - fm.getDescent();
		if (y < 0)
			y = line_height;

		int str_width = fm.stringWidth(TEXT_SAMPLE);
		int x = (width - str_width) / 2;
		if (x < 0)
			x = 0;

		g.drawString(TEXT_SAMPLE, x, y);

		g2.setColor(old_color);
		g2.setFont(old_font);
	}

	/**
	 * Sets the current font
	 */
	public void setFont(Font f) {
		m_font = f;
		revalidate();
		repaint();
	}
}
