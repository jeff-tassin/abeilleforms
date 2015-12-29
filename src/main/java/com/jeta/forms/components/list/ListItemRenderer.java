/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeta.forms.components.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.jeta.forms.store.properties.ListItemProperty;

/**
 * Responsible for rendering ListItemProperty objects. This renderer is needed
 * to support icons for list and combo box items that are specified in the
 * designer. The runtime will automatically create ListItemProperties for these
 * items.
 * 
 * @author Jeff Tassin
 */
public class ListItemRenderer extends JLabel implements ListCellRenderer {
	/**
	 * The foreground and background colors for a list cell. These can change if
	 * the look and feel changes.
	 */
	private Color m_sel_bg;
	private Color m_sel_fg;
	private Color m_bg;
	private Color m_fg;

	/**
	 * Keep a reference to the current look and feel. Check each time we render
	 * in case the look and feel has changed.
	 */
	private LookAndFeel m_laf;

	public ListItemRenderer() {
		/**
		 * This is needed for some look and feels or the background color won't
		 * show.
		 */
		setOpaque(true);
	}

	/**
	 * ListCellRenderer implementation.
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		LookAndFeel laf = UIManager.getLookAndFeel();
		if (laf != m_laf) {
			m_laf = laf;
			updateColorCache();
		}

		if (isSelected) {
			setBackground(m_sel_bg);
			setForeground(m_sel_fg);
		}
		else {
			setBackground(m_bg);
			setForeground(m_fg);
		}

		if (value instanceof ListItemProperty) {
			ListItemProperty prop = (ListItemProperty) value;

			setIcon(prop.icon());
			String txt = prop.getLabel();
			if (txt == null)
				txt = "";
			setText(txt);
		}
		else {
			if (value == null)
				value = "";
			setText(value.toString());
		}
		return this;
	}

	/**
	 * Caches the foreground/background colors for a list box for the current
	 * look and feel.
	 */
	private void updateColorCache() {
		m_sel_fg = UIManager.getColor("List.selectionForeground");
		m_sel_bg = UIManager.getColor("List.selectionBackground");
		m_bg = UIManager.getColor("List.background");
		m_fg = UIManager.getColor("List.foreground");
		setOpaque(true);
	}
}
