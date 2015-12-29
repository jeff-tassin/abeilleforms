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

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.components.IntegerDocument;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Panel that allows a user to select properties for a Font. This includes the
 * Font family, style (bold, italic), and point size.
 * 
 * @author Jeff Tassin
 */
public class FontView extends JETAPanel {
	/**
	 * The actual view
	 */
	private FormPanel m_view;

	/**
	 * The font preview component
	 */
	private FontSampleComponent m_font_comp;

	public static final String STYLE_BOLD_ITALIC = "Bold Italic";
	public static final String STYLE_BOLD = "Bold";
	public static final String STYLE_ITALIC = "Italic";
	public static final String STYLE_NORMAL = "Regular";

	/**
	 * ctor
	 */
	public FontView(Font f) {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/font/fontSelector.frm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		m_view.getTextField(FontViewNames.ID_SIZE_FIELD).setDocument(new IntegerDocument());

		CellConstraints cc = new CellConstraints();
		m_font_comp = new FontSampleComponent(f);

		m_view.getFormAccessor(FontViewNames.ID_SAMPLE_VIEW).addBean(m_font_comp, cc.xy(1, 1));

		initialize(f);
		setController(new FontViewController(this));
	}

	/**
	 * @return a Font object based on the settings in the view
	 */
	public Font createFont() {
		return new Font(getFamilyName(), getStyleValue(), getPointSize());
	}

	public String getFamilyName() {
		return m_view.getText(FontViewNames.ID_FAMILY_FIELD);
	}

	public int getStyleValue() {
		String style = m_view.getText(FontViewNames.ID_STYLE_FIELD);
		if (STYLE_BOLD_ITALIC.equals(style))
			return (Font.BOLD | Font.ITALIC);
		else if (STYLE_BOLD.equals(style))
			return Font.BOLD;
		else if (STYLE_ITALIC.equals(style))
			return Font.ITALIC;
		else
			return Font.PLAIN;
	}

	public int getPointSize() {
		try {
			int point_size = Integer.parseInt(m_view.getText(FontViewNames.ID_SIZE_FIELD));
			if (point_size < 1)
				point_size = 10;
			return point_size;
		} catch (Exception e) {
			e.printStackTrace();
			return 10;
		}
	}

	/**
	 * Initializes the view with the current font set
	 */
	private void initialize(Font f) {
		String[] fonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		JList list = m_view.getList(FontViewNames.ID_FAMILY_LIST);
		DefaultListModel lmodel = new DefaultListModel();
		list.setModel(lmodel);

		for (int index = 0; index < fonts.length; index++) {
			lmodel.addElement(fonts[index]);
		}

		if (f != null) {
			setFontValue(f);
		}
	}

	/**
	 * Sets the family name in the view
	 */
	private void setFamilyName(String name) {
		m_view.setText(FontViewNames.ID_FAMILY_FIELD, name);
		m_view.getList(FontViewNames.ID_FAMILY_LIST).setSelectedValue(name, true);
	}

	/**
	 * Sets the point size in the view
	 */
	public void setPointSize(int size) {
		m_view.getList(FontViewNames.ID_SIZE_LIST).setSelectedValue(String.valueOf(size), true);
		m_view.setText(FontViewNames.ID_SIZE_FIELD, String.valueOf(size));
	}

	/**
	 * Sets the style in the view
	 */
	public void setStyle(String style) {
		m_view.setText(FontViewNames.ID_STYLE_FIELD, style);
		m_view.getList(FontViewNames.ID_STYLE_LIST).setSelectedValue(style, true);
	}

	/**
	 * Sets the font to be displayed in this view
	 */
	public void setFontValue(Font f) {
		setFamilyName(f.getName());
		if (f.isBold() && f.isItalic()) {
			setStyle(STYLE_BOLD_ITALIC);
		}
		else if (f.isBold()) {
			setStyle(STYLE_BOLD);
		}
		else if (f.isItalic()) {
			setStyle(STYLE_ITALIC);
		}
		else {
			setStyle(STYLE_NORMAL);
		}
		setPointSize(f.getSize());
		m_font_comp.setFont(f);
	}

}
