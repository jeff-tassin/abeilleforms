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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.colorchooser.JETAColorChooser;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;

public class ColorEditor extends JETAPropertyEditor {
	private Color m_last_color;
	private String m_rgb;
	private ValuePainter m_value_painter = new ValuePainter();

	public ColorEditor() {

	}

	public String getJavaInitializationString() {
		return "new RGB()";
	}

	public boolean isPaintable() {
		return true;
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog(Component comp) {
		Color new_color = JETAColorChooser.invokeColorChooser(comp, (Color) getValue());
		if (new_color != null) {
			setValue(new_color);
		}
	}

	/**
	 * 
	 */
	public boolean supportsCustomEditor() {
		return true;
	}

	public void paintValue(Graphics g, Rectangle rect) {
		Color c = (Color) getValue();
		if (c != null) {
			int box_height = 12;
			int box_width = 12;
			int y = (rect.height - box_height) / 2;
			int x = 5;

			assert (rect != null);
			g.setColor((Color) c);
			g.fillRect(x, y, box_width, box_height);

			g.setColor(Color.white);
			g.drawRect(x + 1, y + 1, box_width - 2, box_height - 2);

			g.setColor(Color.black);
			g.drawRect(x, y, box_width, box_height);

			if (!c.equals(m_last_color)) {
				m_last_color = c;
				m_rgb = I18N.format("RGB_3", new Integer(c.getRed()), new Integer(c.getGreen()), new Integer(c.getBlue()));
			}
			m_value_painter.drawString(g, rect, box_width + 10, m_rgb);

		}
	}

	private String getHexString(int ival) {
		String result = Integer.toHexString(ival).toUpperCase();
		if (result.length() == 1)
			return "0" + result;
		else
			return result;
	}

}
