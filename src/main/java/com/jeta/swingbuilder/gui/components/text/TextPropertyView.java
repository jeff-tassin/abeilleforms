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

package com.jeta.swingbuilder.gui.components.text;

import java.awt.Dimension;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

public class TextPropertyView extends FormPanel {
	public static final String ID_TEXT_AREA = "text.area"; // javax.swing.JTextArea

	public TextPropertyView() {
		this("");
	}

	public TextPropertyView(String txt) {
		super("com/jeta/swingbuilder/gui/components/text/textProperty.jfrm");
		setText(txt);
	}

	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 175, 75);
	}

	public void setText(String txt) {
		if (txt == null)
			txt = "";
		setText(ID_TEXT_AREA, txt);
		try {
			getTextComponent(ID_TEXT_AREA).setCaretPosition(0);
		} catch (Exception e) {

		}
	}

	public String getText() {
		return getText(ID_TEXT_AREA);
	}
}
