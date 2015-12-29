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

package com.jeta.swingbuilder.gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyEditor;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import com.jeta.forms.gui.common.FormUtils;

public class PropertyValueRenderer extends JComponent implements TableCellRenderer {
	/**
	 * @directed
	 */
	private PropertyTableModel m_model;

	private PropertyEditor m_editor;

	/**
	 * A cache of editors for each row
	 */
	private PropertyEditorCache m_cache;

	/**
	 * Used for storing bounds of component for painting
	 */
	private Rectangle m_bounds = new Rectangle();

	/**
	 * The current object value
	 */
	private Object m_value;

	private int m_row;

	/**
	 * The font for this component
	 */
	private Font m_font;

	private Color m_selbg;
	private Color m_selfg;
	private Color m_bg;
	private Color m_fg;

	/**
	 * ctor
	 */
	public PropertyValueRenderer(PropertyTableModel tmodel) {
		m_model = tmodel;
		m_cache = new PropertyEditorCache(tmodel);

	}

	/**
	 * TableCellRenderer implementation
	 */
	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {

		Component result = this;
		m_value = obj;

		try {
			m_editor = m_cache.getPropertyEditor(row);
			if (m_editor != null) {
				m_editor.setValue(obj);
				if (m_editor instanceof JETAPropertyEditor) {
					JETAPropertyEditor jpe = (JETAPropertyEditor) m_editor;
					if (!jpe.isPaintable()) {
						result = jpe.getCustomEditor();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * For renderering JComponent
	 */
	public void paintComponent(Graphics g) {
		try {
			if (m_font == null)
				cacheProperties();

			if (m_editor != null && m_editor.isPaintable()) {
				m_bounds.setBounds(0, 0, getWidth(), getHeight());
				m_editor.paintValue(g, m_bounds);
			}
			else {
				// if we are here, there is no editor available for the given
				// type

				int height = getHeight();
				g.setColor(m_bg);

				g.fillRect(0, 0, getWidth(), height);

				Color fg = getForeground();
				if (fg != null)
					g.setColor(fg);

				g.setFont(m_font);
				FontMetrics fm = getFontMetrics(m_font);
				assert (fm != null);
				int line_height = fm.getHeight();
				int y = height - (height - line_height) / 2 - fm.getDescent();
				if (m_value == null) {
					g.drawString("null", 0, y);
				}
				else {
					g.drawString(m_value.toString(), 0, y);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateUI() {
		super.updateUI();
		m_cache.updateUI();
		if (m_editor != null) {
			java.awt.Component comp = m_editor.getCustomEditor();
			FormUtils.updateLookAndFeel(comp);
		}

		m_font = null;
	}

	private void cacheProperties() {
		m_font = UIManager.getFont("Table.font");
		m_selbg = UIManager.getColor("Table.selectionBackground");
		m_selfg = UIManager.getColor("Table.selectionForeground");
		m_fg = UIManager.getColor("Table.foreground");
		m_bg = UIManager.getColor("TextField.background");
	}

}
