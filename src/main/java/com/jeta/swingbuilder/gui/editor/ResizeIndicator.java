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

package com.jeta.swingbuilder.gui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridView;

public class ResizeIndicator {

	/**
	 * The font that we use to render the size
	 */
	private Font m_font;

	private Color m_background = new Color(255, 255, 128);
	private Rectangle m_rect = new Rectangle();

	/**
	 * The top level view where the indicator will be rendered.
	 */
	private GridView m_view;

	/**
	 * The current position of the indicator relative to the top-level grid
	 * overlay
	 */
	private int m_position;

	/**
	 * The actual size of the component and units
	 */
	private String m_size;

	/**
	 * The orientation of the indicator (HORIZONTAL or VERTICAL)
	 */
	private Orientation m_orientation;

	/**
	 * The view offset
	 */
	private int m_view_offset;

	public ResizeIndicator(GridView view, Orientation orientation) {
		m_view = view;
		m_orientation = orientation;
		m_font = javax.swing.UIManager.getFont("Table.font");
	}

	/**
	 * Renders the indicator
	 */
	public void paint(Graphics g) {
		if (m_size != null) {
			Color oldcolor = g.getColor();
			Font oldfont = g.getFont();
			g.setFont(m_font);

			FontMetrics fm = g.getFontMetrics();

			if (Orientation.VERTICAL.equals(m_orientation)) {
				g.drawLine(m_position, 0, m_position, m_view.getHeight());

				int view_height = m_view.getHeight();
				int line_height = fm.getHeight();
				int y = view_height - (view_height - line_height) / 2 - fm.getDescent() + m_view_offset;
				if (y > (70 + m_view_offset))
					y = 70 + m_view_offset;

				int string_width = fm.stringWidth(m_size);
				int x = m_position - string_width / 2;

				g.setColor(m_background);

				m_rect.setBounds(x - 5, y - fm.getAscent() - 2, string_width + 10, fm.getHeight() + 4);
				g.fillRect(m_rect.x, m_rect.y, m_rect.width, m_rect.height);

				g.setColor(Color.black);
				g.drawRect(m_rect.x, m_rect.y, m_rect.width, m_rect.height);

				g.drawString(m_size, x, y);
			}
			else {
				g.drawLine(0, m_position, m_view.getWidth(), m_position);

				int line_height = fm.getHeight();
				int string_width = fm.stringWidth(m_size);
				int x = 70 + m_view_offset;

				g.setColor(m_background);

				m_rect.setBounds(x - 5, m_position - fm.getHeight() / 2 - 2, string_width + 10, fm.getHeight() + 4);
				g.fillRect(m_rect.x, m_rect.y, m_rect.width, m_rect.height);

				g.setColor(Color.black);
				g.drawRect(m_rect.x, m_rect.y, m_rect.width, m_rect.height);

				g.drawString(m_size, x, m_position + fm.getAscent() / 2);
			}

			g.setColor(oldcolor);
			g.setFont(oldfont);
		}
	}

	public void setPosition(int pos) {
		m_position = pos;
	}

	/**
	 * Sets the value for the numeric size indicator.
	 * 
	 * @param size
	 *            the value for the size
	 * @param units
	 *            the units (MM, CM, IN, DLU etc. )
	 * @param viewOfffset
	 *            the current position of the viewport (for rendering correctly
	 *            while scrolling)
	 */
	public void setSize(double size, String units, int viewOffset) {
		StringBuffer sbuff = new StringBuffer();
		if (FormUtils.isIntegralUnits(units)) {
			sbuff.append(Math.round(size));
		}
		else {
			java.text.DecimalFormat dformat = new java.text.DecimalFormat("#.0");
			sbuff.append(dformat.format(size));
		}
		sbuff.append(' ');
		sbuff.append(units);
		m_size = sbuff.toString();
		m_view_offset = viewOffset;
	}
}
