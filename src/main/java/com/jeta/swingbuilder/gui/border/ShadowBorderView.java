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

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.ColorProperty;
import com.jeta.forms.store.properties.ShadowBorderProperty;
import com.jeta.open.i18n.I18N;

/**
 * A view for creating and editing shadow border.s
 * 
 * @author Jeff Tassin
 */
public class ShadowBorderView extends AbstractBorderView {

	public ShadowBorderView() {
		super("com/jeta/swingbuilder/gui/border/shadowBorder.frm");

		JSpinner sp = getSpinner(ShadowBorderNames.ID_THICKNESS_SPINNER);
		sp.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		sp.setValue(new Integer(1));
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public BorderProperty createBorderProperty() {
		return new ShadowBorderProperty(getType(), getThickness(), getStartColor(), getEndColor(), getSymmetric());
	}

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public String getDescription() {
		return I18N.getLocalizedMessage("Shadow Border");
	}

	public ColorProperty getStartColor() {
		return getColorProperty(ShadowBorderNames.ID_START_COLOR);
	}

	public ColorProperty getEndColor() {
		return getColorProperty(ShadowBorderNames.ID_END_COLOR);
	}

	public boolean getSymmetric() {
		return getBoolean(ShadowBorderNames.ID_SYMMETRIC);
	}

	/**
	 * @return the type of border selected (ShadowBorderProperty.SOLID or
	 *         GRADIENT)
	 */
	public int getType() {
		return isSelected(ShadowBorderNames.ID_SOLID_RADIO) ? ShadowBorderProperty.SOLID : ShadowBorderProperty.GRADIENT;
	}

	/**
	 * @return the thickness of the border
	 */
	public int getThickness() {
		SpinnerNumberModel model = (SpinnerNumberModel) getSpinner(ShadowBorderNames.ID_THICKNESS_SPINNER).getModel();
		Integer value = (Integer) model.getValue();
		return value.intValue();
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		if (border instanceof ShadowBorderProperty) {
			ShadowBorderProperty sp = (ShadowBorderProperty) border;
			setColorProperty(ShadowBorderNames.ID_START_COLOR, sp.getStartColor());
			setColorProperty(ShadowBorderNames.ID_END_COLOR, sp.getEndColor());
			SpinnerNumberModel model = (SpinnerNumberModel) getSpinner(ShadowBorderNames.ID_THICKNESS_SPINNER).getModel();
			model.setValue(new Integer(sp.getThickness()));
			setSelected(ShadowBorderNames.ID_SOLID_RADIO, (sp.getType() == ShadowBorderProperty.SOLID));
			setSelected(ShadowBorderNames.ID_GRADIENT_RADIO, (sp.getType() == ShadowBorderProperty.GRADIENT));
			setSelected(ShadowBorderNames.ID_SYMMETRIC, sp.isSymmetric());
		}
		else {
			assert (false);
		}
	}

}
