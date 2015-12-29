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

package com.jeta.swingbuilder.gui.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * Document that only allows floating point values for a text field
 * 
 * @author Jeff Tassin
 */
public class FloatDocument extends PlainDocument {
	private boolean m_signed = true;

	public FloatDocument() {

	}

	public FloatDocument(boolean signed) {
		m_signed = signed;
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (str == null) {
			return;
		}

		str = FormDesignerUtils.fastTrim(str);
		StringBuffer sbuff = new StringBuffer();
		for (int index = 0; index < str.length(); index++) {
			char c = str.charAt(index);
			if (Character.isDigit(c) || c == '.' || c == ' ') {
				if (m_signed || c != '-') {
					sbuff.append(c);
					continue;
				}
			}
			return;
		}

		if (sbuff.length() > 0) {
			super.insertString(offs, sbuff.toString(), a);
		}

	}
}
