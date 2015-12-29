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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.store.properties.BevelBorderProperty;
import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.ColorProperty;

/**
 * View that is used to create and edit bevel borders
 * 
 * @author Jeff Tassin
 */
public class BevelBorderView extends AbstractBorderView {

	/**
	 * ctor
	 */
	public BevelBorderView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public BevelBorderView(BevelBorderProperty bp) {
		super("com/jeta/swingbuilder/gui/border/bevelBorder.frm");
		if (bp == null) {
			setBorderProperty(new BevelBorderProperty());
		}
		else {
			setBorderProperty(bp);
		}
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		addCustomColor(BevelBorderNames.ID_HIGHLIGHT_INNER_COLOR, ColorProperty.DEFAULT_COLOR);
		addCustomColor(BevelBorderNames.ID_HIGHLIGHT_OUTER_COLOR, ColorProperty.DEFAULT_COLOR);
		addCustomColor(BevelBorderNames.ID_SHADOW_OUTER_COLOR, ColorProperty.DEFAULT_COLOR);
		addCustomColor(BevelBorderNames.ID_SHADOW_INNER_COLOR, ColorProperty.DEFAULT_COLOR);
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public BorderProperty createBorderProperty() {
		BevelBorderProperty bp = new BevelBorderProperty(getType());
		bp.setHighlightInnerColorProperty(getColorProperty(BevelBorderNames.ID_HIGHLIGHT_INNER_COLOR));
		bp.setHighlightOuterColorProperty(getColorProperty(BevelBorderNames.ID_HIGHLIGHT_OUTER_COLOR));
		bp.setShadowOuterColorProperty(getColorProperty(BevelBorderNames.ID_SHADOW_OUTER_COLOR));
		bp.setShadowInnerColorProperty(getColorProperty(BevelBorderNames.ID_SHADOW_INNER_COLOR));
		setTitle(bp);
		return bp;
	}

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public String getDescription() {
		return "Bevel Border";
	}

	/**
	 * @return the preferred size for this view
	 */
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}

	/**
	 * @return the type of border (RAISED or LOWERED)
	 */
	public int getType() {
		if (getView().getBoolean(BevelBorderNames.ID_BEVEL_RAISED_RADIO)) {
			return BevelBorder.RAISED;
		}
		else {
			return BevelBorder.LOWERED;
		}
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		super.setBorderProperty(border);
		if (border instanceof BevelBorderProperty) {
			BevelBorderProperty bb = (BevelBorderProperty) border;
			if (bb.getBevelType() == BevelBorder.RAISED) {
				getView().getRadioButton(BevelBorderNames.ID_BEVEL_RAISED_RADIO).setSelected(true);
			}
			else {
				getView().getRadioButton(BevelBorderNames.ID_BEVEL_LOWERED_RADIO).setSelected(true);
			}

			setColorProperty(BevelBorderNames.ID_HIGHLIGHT_INNER_COLOR, bb.getHighlightInnerColorProperty());
			setColorProperty(BevelBorderNames.ID_HIGHLIGHT_OUTER_COLOR, bb.getHighlightOuterColorProperty());
			setColorProperty(BevelBorderNames.ID_SHADOW_OUTER_COLOR, bb.getShadowOuterColorProperty());
			setColorProperty(BevelBorderNames.ID_SHADOW_INNER_COLOR, bb.getShadowInnerColorProperty());
		}
		else {
			assert (false);
		}
	}

	public void setColor(String compId, Color c) {
		JETAColorWell inkwell = (JETAColorWell) getView().getComponentByName(compId);
		inkwell.setColor(c);
	}

}
