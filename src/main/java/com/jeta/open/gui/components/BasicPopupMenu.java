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

package com.jeta.open.gui.components;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.jeta.open.i18n.I18N;

/**
 * PopupMenu with cut/copy/paste items.
 * 
 * @author Jeff Tassin
 */
public class BasicPopupMenu extends JPopupMenu {
	/**
	 * Creates a menu item for this popup
	 * 
	 * @param itemText
	 *            the text to show for the menu item
	 * @param actionCmd
	 *            the name of the action that is fired when the menu item is
	 *            selected
	 * @param keyStroke
	 *            the keyboard accelerator
	 */
	public static JMenuItem createMenuItem(String itemText, String actionCmd, KeyStroke keyStroke) {
		JMenuItem item = new JMenuItem(itemText);
		item.setActionCommand(actionCmd);
		item.setName(actionCmd);
		if (keyStroke != null)
			item.setAccelerator(keyStroke);
		return item;
	}

	/**
	 * ctor
	 */
	public BasicPopupMenu() {
		add(createMenuItem(I18N.getLocalizedMessage("Cut"), JETAComponentNames.ID_CUT, null));
		add(createMenuItem(I18N.getLocalizedMessage("Copy"), JETAComponentNames.ID_COPY, null));
		add(createMenuItem(I18N.getLocalizedMessage("Paste"), JETAComponentNames.ID_PASTE, null));
	}
}
