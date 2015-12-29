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

package com.jeta.swingbuilder.gui.main;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.TSComponentNames;

public class FormEditorPopupMenu extends JPopupMenu {
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
	public FormEditorPopupMenu() {
		add(createMenuItem(I18N.getLocalizedMessage("Cut"), TSComponentNames.ID_CUT, null));
		add(createMenuItem(I18N.getLocalizedMessage("Copy"), TSComponentNames.ID_COPY, null));
		add(createMenuItem(I18N.getLocalizedMessage("Paste"), TSComponentNames.ID_PASTE, null));
		add(createMenuItem(I18N.getLocalizedMessage("Paste Special"), FormEditorNames.ID_PASTE_SPECIAL, null));

		addSeparator();
		add(createMenuItem(I18N.getLocalizedMessage("Insert Column"), FormEditorNames.ID_INSERT_COLUMN_LEFT, null));
		add(createMenuItem(I18N.getLocalizedMessage("Delete Column"), FormEditorNames.ID_DELETE_COLUMN, null));

		addSeparator();

		add(createMenuItem(I18N.getLocalizedMessage("Insert Row"), FormEditorNames.ID_INSERT_ROW_ABOVE, null));
		add(createMenuItem(I18N.getLocalizedMessage("Delete Row"), FormEditorNames.ID_DELETE_ROW, null));

	}

}
