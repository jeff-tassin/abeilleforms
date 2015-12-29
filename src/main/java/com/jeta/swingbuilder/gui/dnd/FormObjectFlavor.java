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

package com.jeta.swingbuilder.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collection;

public class FormObjectFlavor {
	/** A GridComponent state */
	public static final DataFlavor COMPONENT_MEMENTO = new DataFlavor(com.jeta.forms.store.memento.ComponentMemento.class, "componentmemento");
	public static final DataFlavor LINKED_FORM_SET = new DataFlavor(Collection.class, "linkedforms");
	public static final DataFlavor LINKED_FORM = new DataFlavor(String.class, "linked.form.id");
	public static final DataFlavor CELL_BACKGROUND = new DataFlavor(com.jeta.forms.store.properties.effects.PaintProperty.class, "cell.background");

	public static boolean isDesignerFlavorSupported(Transferable t) {
		try {
			return (t.isDataFlavorSupported(COMPONENT_MEMENTO) || t.isDataFlavorSupported(LINKED_FORM_SET) || t.isDataFlavorSupported(LINKED_FORM) || t
					.isDataFlavorSupported(CELL_BACKGROUND));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
