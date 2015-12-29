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
import javax.swing.border.EtchedBorder;

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.EtchedBorderProperty;

/**
 * View that is used to create and edit bevel borders
 * 
 * @author Jeff Tassin
 */
public class EtchedBorderView extends AbstractBorderView {

	/**
	 * ctor
	 */
	public EtchedBorderView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public EtchedBorderView(EtchedBorderProperty bp) {
		super("com/jeta/swingbuilder/gui/border/etchedBorder.frm");
		if (bp == null) {
			setBorderProperty(new EtchedBorderProperty());
		}
		else {
			setBorderProperty(bp);
		}
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		addCustomColor(EtchedBorderNames.ID_HIGHLIGHT_COLOR_SELECTOR, ColorProperty.DEFAULT_COLOR);
		addCustomColor(EtchedBorderNames.ID_SHADOW_COLOR_SELECTOR, ColorProperty.DEFAULT_COLOR);
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public BorderProperty createBorderProperty() {
		EtchedBorderProperty bp = new EtchedBorderProperty(getType());
		bp.setHighlightColorProperty(getColorProperty(EtchedBorderNames.ID_HIGHLIGHT_COLOR_SELECTOR));
		bp.setShadowColorProperty(getColorProperty(EtchedBorderNames.ID_SHADOW_COLOR_SELECTOR));
		setTitle(bp);
		return bp;
	}

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public String getDescription() {
		return "Etched Border";
	}

	/**
	 * @return the type of border (RAISED or LOWERED)
	 */
	public int getType() {
		if (getView().getBoolean(EtchedBorderNames.ID_ETCHED_RAISED_RADIO)) {
			return EtchedBorder.RAISED;
		}
		else {
			return EtchedBorder.LOWERED;
		}
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		super.setBorderProperty(border);
		if (border instanceof EtchedBorderProperty) {
			EtchedBorderProperty bb = (EtchedBorderProperty) border;
			if (bb.getEtchType() == EtchedBorder.RAISED) {
				getView().getRadioButton(EtchedBorderNames.ID_ETCHED_RAISED_RADIO).setSelected(true);
			}
			else {
				getView().getRadioButton(EtchedBorderNames.ID_ETCHED_LOWERED_RADIO).setSelected(true);
			}

			setColorProperty(EtchedBorderNames.ID_HIGHLIGHT_COLOR_SELECTOR, bb.getHighlightColorProperty());
			setColorProperty(EtchedBorderNames.ID_SHADOW_COLOR_SELECTOR, bb.getShadowColorProperty());
		}
		else {
			assert (false);
		}
	}

}
