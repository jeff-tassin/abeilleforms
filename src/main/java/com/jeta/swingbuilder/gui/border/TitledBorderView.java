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

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.TitledBorderProperty;

/**
 * View that is used to create and edit bevel borders
 * 
 * @author Jeff Tassin
 */
public class TitledBorderView extends AbstractBorderView {

	/**
	 * ctor
	 */
	public TitledBorderView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public TitledBorderView(TitledBorderProperty bp) {
		super("com/jeta/swingbuilder/gui/border/titledBorder.frm");
		if (bp == null) {
			setBorderProperty(new TitledBorderProperty());
		}
		else {
			setBorderProperty(bp);
		}
		getView().setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public BorderProperty createBorderProperty() {
		TitledBorderProperty tp = new TitledBorderProperty();
		setTitle(tp);
		return tp;
	}

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public String getDescription() {
		return "Titled Border";
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		super.setBorderProperty(border);
	}
}
