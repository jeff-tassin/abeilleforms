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

import java.awt.datatransfer.Transferable;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.swingbuilder.gui.dnd.FormObjectFlavor;

/**
 * Displays paste special GUI
 * 
 * @author Jeff Tassin
 */
public class PasteSpecialView extends FormPanel {
	private Transferable m_transferable;

	public PasteSpecialView(Transferable t) {
		super("com/jeta/swingbuilder/gui/main/pasteSpecial.jfrm");
		m_transferable = t;
		initialize(t);
	}

	public void initialize(Transferable transferable) {
		try {
			enableComponent(PasteSpecialNames.ID_LINKED_AS_EMBEDDED, false);
			enableComponent(PasteSpecialNames.ID_COMPONENT, false);
			enableComponent(PasteSpecialNames.ID_CELL_BACKGROUND, false);

			if (transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM_SET)) {
				enableComponent(PasteSpecialNames.ID_LINKED_AS_EMBEDDED, transferable.isDataFlavorSupported(FormObjectFlavor.LINKED_FORM_SET));
			}
			else {
				enableComponent(PasteSpecialNames.ID_COMPONENT, transferable.isDataFlavorSupported(FormObjectFlavor.COMPONENT_MEMENTO));
			}
			enableComponent(PasteSpecialNames.ID_CELL_BACKGROUND, transferable.isDataFlavorSupported(FormObjectFlavor.CELL_BACKGROUND));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
