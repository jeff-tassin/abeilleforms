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

package com.jeta.swingbuilder.gui.border;

import javax.swing.BorderFactory;

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.EmptyBorderProperty;
import com.jeta.swingbuilder.gui.components.IntegerDocument;

/**
 * View that is used to create and edit bevel borders
 * 
 * @author Jeff Tassin
 */
public class EmptyBorderView extends AbstractBorderView {

	/**
	 * ctor
	 */
	public EmptyBorderView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public EmptyBorderView(EmptyBorderProperty bp) {
		super("com/jeta/swingbuilder/gui/border/emptyBorder.frm");
		if (bp == null) {
			setBorderProperty(new EmptyBorderProperty());
		}
		else {
			setBorderProperty(bp);
		}
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getTextField(EmptyBorderNames.ID_EMPTY_BORDER_TOP).setDocument(new IntegerDocument(false));
		getTextField(EmptyBorderNames.ID_EMPTY_BORDER_LEFT).setDocument(new IntegerDocument(false));
		getTextField(EmptyBorderNames.ID_EMPTY_BORDER_BOTTOM).setDocument(new IntegerDocument(false));
		getTextField(EmptyBorderNames.ID_EMPTY_BORDER_RIGHT).setDocument(new IntegerDocument(false));
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public BorderProperty createBorderProperty() {
		EmptyBorderProperty bp = new EmptyBorderProperty(getInteger(EmptyBorderNames.ID_EMPTY_BORDER_TOP), getInteger(EmptyBorderNames.ID_EMPTY_BORDER_LEFT),
				getInteger(EmptyBorderNames.ID_EMPTY_BORDER_BOTTOM), getInteger(EmptyBorderNames.ID_EMPTY_BORDER_RIGHT));
		setTitle(bp);
		return bp;
	}

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public String getDescription() {
		return "Empty Border";
	}

	public int getInteger(String compId) {
		try {
			return Integer.parseInt(getView().getText(compId));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		super.setBorderProperty(border);
		if (border instanceof EmptyBorderProperty) {
			EmptyBorderProperty bb = (EmptyBorderProperty) border;
			getView().setText(EmptyBorderNames.ID_EMPTY_BORDER_TOP, String.valueOf(bb.getTop()));
			getView().setText(EmptyBorderNames.ID_EMPTY_BORDER_LEFT, String.valueOf(bb.getLeft()));
			getView().setText(EmptyBorderNames.ID_EMPTY_BORDER_BOTTOM, String.valueOf(bb.getBottom()));
			getView().setText(EmptyBorderNames.ID_EMPTY_BORDER_RIGHT, String.valueOf(bb.getRight()));
		}
		else {
			assert (false);
		}
	}

}
