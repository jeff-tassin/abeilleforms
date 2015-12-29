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
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.font.FontView;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.resources.Icons;

/**
 * Editor for handling font properties
 * 
 * @author Jeff Tassin
 */
public class FontEditor extends JETAPropertyEditor {
	private ValuePainter m_painter = new ValuePainter();

	private static ImageIcon[] m_font_icon = { FormDesignerUtils.loadImage(Icons.FONT_16) };
	private ImageIcon[] m_styles = new ImageIcon[2];

	private static ImageIcon m_bold_icon;
	private static ImageIcon m_no_bold_icon;
	private static ImageIcon m_italic_icon;
	private static ImageIcon m_no_italic_icon;

	static {
		m_bold_icon = FormDesignerUtils.loadImage("forms/font_bold16.gif");
		m_no_bold_icon = FormDesignerUtils.loadImage("forms/font_no_bold16.gif");
		m_italic_icon = FormDesignerUtils.loadImage("forms/font_italic16.gif");
		m_no_italic_icon = FormDesignerUtils.loadImage("forms/font_no_italic16.gif");
	}

	/**
	 * ctor
	 */
	public FontEditor() {
		m_painter.setPreImages(m_font_icon);
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog(Component comp) {
		FontView view = new FontView((Font) getValue());
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, comp, true);
		dlg.setPrimaryPanel(view);
		dlg.setTitle(I18N.getLocalizedMessage("Font"));
		dlg.pack();
		dlg.showCenter();
		if (dlg.isOk()) {
			setValue(view.createFont());
		}
	}

	/**
	 * This editor can be rendered in-place
	 */
	public boolean isPaintable() {
		return true;
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	public void paintValue(Graphics g, Rectangle rect) {
		assert (rect != null);
		m_painter.paintValue(g, rect);
	}

	/**
	 * Override setValue so we can nullify the descriptor for the font
	 */
	public void setValue(Object value) {
		super.setValue(value);
		Font font = (Font) value;

		if (font == null) {
			m_painter.setValue(I18N.format("font_description_2", "null", ""));
		}
		else {
			m_painter.setValue(I18N.format("font_description_2", font.getFamily(), new Integer(font.getSize())));
			if (font.isBold())
				m_styles[0] = m_bold_icon;
			else
				m_styles[0] = m_no_bold_icon;

			if (font.isItalic())
				m_styles[1] = m_italic_icon;
			else
				m_styles[1] = m_no_italic_icon;

			m_painter.setPostImages(m_styles);
		}

	}

}
