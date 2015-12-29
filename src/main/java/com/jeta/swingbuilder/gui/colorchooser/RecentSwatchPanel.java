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

package com.jeta.swingbuilder.gui.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.UIManager;

import com.jeta.forms.store.properties.ColorHolder;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.store.RecentColorsModel;

/**
 * This panel is used to display recent colors selected in the JETAColorChooser.
 * It is needed because the JColorChooser has a curiously poor design.
 * 
 * @author Jeff Tassin
 */
public class RecentSwatchPanel extends JETAPanel {
	/**
	 * Used to store the recent colors between application invocations.
	 */
	private RecentColorsModel m_colors;

	/**
	 * The size in pixels of each swatch
	 */
	private Dimension m_swatch_size;

	private static final int SWATCH_COLUMNS = 30;
	private static final int SWATCH_ROWS = 2;

	/**
	 * A small space between each swatch.
	 */
	protected Dimension m_gap;

	/**
	 * The default color for a swatch if one has not been defined.
	 */
	private ColorHolder m_default_color;

	public RecentSwatchPanel() {
		Color default_color = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor");
		if (default_color == null)
			default_color = UIManager.getColor("control");
		if (default_color == null)
			default_color = Color.gray;

		m_default_color = new ColorHolder(default_color);
		m_colors = RecentColorsModel.createInstance(SWATCH_ROWS * SWATCH_COLUMNS);

		m_swatch_size = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize");
		if (m_swatch_size == null)
			m_swatch_size = new Dimension(10, 10);

		m_gap = new Dimension(1, 1);

		setToolTipText("");
		setRequestFocusEnabled(false);
		setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
	}

	public boolean isFocusTraversable() {
		return false;
	}

	public void paintComponent(Graphics g) {
		Insets insets = getInsets();
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int row = 0; row < SWATCH_ROWS; row++) {
			for (int column = 0; column < SWATCH_COLUMNS; column++) {
				g.setColor(getColorForCell(column, row).getColor());
				int x = insets.left + column * (m_swatch_size.width + m_gap.width);
				int y = insets.top + row * (m_swatch_size.height + m_gap.height);
				g.fillRect(x, y, m_swatch_size.width, m_swatch_size.height);
				g.setColor(Color.black);
				g.drawLine(x + m_swatch_size.width - 1, y, x + m_swatch_size.width - 1, y + m_swatch_size.height - 1);
				g.drawLine(x, y + m_swatch_size.height - 1, x + m_swatch_size.width - 1, y + m_swatch_size.height - 1);
			}
		}

		g.setColor(Color.white);
		int width = SWATCH_COLUMNS * (m_swatch_size.width + m_gap.width) - 2;
		int height = SWATCH_ROWS * (m_swatch_size.height + m_gap.height) - 2;
		g.drawLine(insets.left, insets.top, insets.left + width, insets.top);
		g.drawLine(insets.left, insets.top, insets.left, insets.top + height);
		g.drawLine(insets.left, insets.top + height, insets.left + width, insets.top + height);
		g.drawLine(insets.left + width, insets.top, insets.left + width, insets.top + height);
	}

	public Dimension getPreferredSize() {
		Insets insets = getInsets();
		int width = SWATCH_COLUMNS * (m_swatch_size.width + m_gap.width) - 1 + insets.left + insets.right;
		int height = SWATCH_ROWS * (m_swatch_size.height + m_gap.height) - 1 + insets.top + insets.bottom;
		return new Dimension(width, height);
	}

	public String getToolTipText(MouseEvent e) {
		ColorHolder color = getColorForLocation(e.getX(), e.getY());
		return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
	}

	public ColorHolder getColorForLocation(int x, int y) {
		int column = x / (m_swatch_size.width + m_gap.width);
		int row = y / (m_swatch_size.height + m_gap.height);
		return getColorForCell(column, row);
	}

	private ColorHolder getColorForCell(int column, int row) {
		return m_colors.getColor((row * SWATCH_COLUMNS) + column, m_default_color);
	}

	public void setMostRecentColor(Color c) {
		m_colors.setRecentColor(new ColorHolder(c));
		repaint();
	}

	/**
	 * Saves the recent color settings to the application store
	 */
	public void saveSettings() {
		RecentColorsModel.save(m_colors);
	}
}
