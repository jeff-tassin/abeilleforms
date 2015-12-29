/* 
 * Copyright 2004 JETA Software, Inc. All rights reserved.
 * JETA SOFTWARE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jeta.swingbuilder.gui.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * This class implements a text Document that only takes digits and a '-' sign
 * 
 * @author Jeff Tassin
 */
public class IntegerDocument extends PlainDocument {
	private boolean m_allow_signed = true;

	public IntegerDocument() {
		this(true);
	}

	public IntegerDocument(boolean bsigned) {
		m_allow_signed = bsigned;
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (str == null) {
			return;
		}

		str = FormDesignerUtils.fastTrim(str);
		for (int index = 0; index < str.length(); index++) {
			char c = str.charAt(index);
			if (!Character.isDigit(c)) {
				if (c == '-' && m_allow_signed)
					continue;

				// Toolkit toolkit = Toolkit.getDefaultToolkit() ;
				// toolkit.beep();
				return;
			}
		}
		super.insertString(offs, str, a);
	}
}
