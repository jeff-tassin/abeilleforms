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

package com.jeta.swingbuilder.gui.properties.editors;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class ValuePainter {
	/**
	 * The text to paint
	 */
	private String m_value;

	/**
	 * The font to use
	 */
	private Font m_font;

	private ImageIcon[] m_preimages;
	private ImageIcon[] m_postimages;

	/**
	 * The component associated with this painter.
	 */
	private Component m_comp;

	/**
	 * ctor
	 * 
	 * @param comp
	 *            the component associated with this painter.
	 */
	public ValuePainter() {
		this(null);
	}

	/**
	 * ctor
	 */
	public ValuePainter(String value) {
		m_value = value;
		m_font = javax.swing.UIManager.getFont("Table.font");
	}

	/**
	 * Sets the images rendered to the left of the value
	 */
	public void setPreImages(ImageIcon[] images) {
		m_preimages = images;
	}

	/**
	 * Sets the images rendered to the left of the value
	 */
	public void setPreImages(ImageIcon image) {
		ImageIcon[] images = new ImageIcon[] { image };
		setPreImages(images);
	}

	/**
	 * Sets the images rendered to the right of the value
	 */
	public void setPostImages(ImageIcon[] images) {
		m_postimages = images;
	}

	/**
	 * Sets the images rendered to the right of the value
	 */
	public void setPostImages(ImageIcon image) {
		ImageIcon[] images = new ImageIcon[] { image };
		setPostImages(images);
	}

	/**
	 * Method that renders the text on the given graphics context
	 * 
	 * @return the string width in pixels
	 */
	public int drawString(Graphics g, Rectangle rect, int x, String value) {
		g.setFont(m_font);
		FontMetrics fm = g.getFontMetrics();

		int line_height = fm.getHeight();
		int y = rect.height - (rect.height - line_height) / 2 - fm.getDescent();

		if (value == null) {
			g.drawString("null", x, y);
			return fm.stringWidth("null");
		}
		else {
			g.drawString(value, x, y);
			return fm.stringWidth(value);
		}

	}

	/**
	 * Method that renders the text on the given graphics context
	 */
	public void paintValue(Graphics g, Rectangle rect) {
		assert (rect != null);

		int x = 0;
		if (m_preimages != null) {
			x += 4;
			for (int index = 0; index < m_preimages.length; index++) {
				ImageIcon icon = m_preimages[index];
				int y = (rect.height - icon.getIconHeight()) / 2;
				if (y < 0)
					y = 0;

				icon.paintIcon(null, g, x, y);
				x += icon.getIconWidth();
			}
		}

		x += 5;
		x += drawString(g, rect, x, m_value);

		if (m_postimages != null) {
			x += 5;
			for (int index = 0; index < m_postimages.length; index++) {
				ImageIcon icon = m_postimages[index];
				int y = (rect.height - icon.getIconHeight()) / 2;
				if (y < 0)
					y = 0;

				icon.paintIcon(null, g, x, y);
				x += icon.getIconWidth();
			}
		}
	}

	/**
	 * Override setValue so we can nullify the descriptor for the font
	 */
	public void setValue(Object value) {
		if (value instanceof String) {
			m_value = (String) value;
		}
		else if (value == null) {
			m_value = null;
		}
		else {
			m_value = value.toString();
		}
	}
}
