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

import javax.swing.JCheckBox;

import com.jeta.forms.components.colors.ColorSelector;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.ListItemProperty;
import com.jeta.swingbuilder.gui.components.panel.SwingBuilderPanel;

/**
 * Base view that is used to create and edit border properties
 * 
 * @author Jeff Tassin
 */
public abstract class AbstractBorderView extends SwingBuilderPanel {

	/**
	 * ctor
	 */
	public AbstractBorderView(String formPath) {
		super(formPath);
	}

	public FormPanel getView() {
		return this;
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public abstract BorderProperty createBorderProperty();

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public abstract String getDescription();

	/**
	 * Adds a custom 'default' color to the colorselectivew with the given name.
	 * This is needed because the BevelBorder can defines its colors based on
	 * the component associated with the border.
	 */
	public void addCustomColor(String controlName, String key) {
		ColorSelector cv = (ColorSelector) getComponentByName(controlName);
		if (cv != null) {
			cv.prependColor(key, null);
			cv.setSelectedColor(key);
		}
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		JCheckBox cbox = getCheckBox(BorderNames.ID_TITLE_INCLUDE);
		if (cbox != null)
			cbox.setSelected(border.isIncludeTitle());

		setText(BorderNames.ID_TITLE_TEXT_FIELD, border.getTitle());

		getComboBox(BorderNames.ID_TITLE_POSITION_COMBO).setSelectedItem(new ListItemProperty(BorderProperty.toPositionString(border.getPosition())));
		getComboBox(BorderNames.ID_TITLE_JUSTIFICATION_COMBO).setSelectedItem(
				new ListItemProperty(BorderProperty.toJustificationString(border.getJustification())));

		setColorProperty(BorderNames.ID_TITLE_COLOR_SELECTOR, border.getTextColorProperty());
	}

	/**
	 * Sets the BorderProperty title settings based on the form inputs
	 */
	protected void setTitle(BorderProperty border) {
		border.setIncludeTitle(getBoolean(BorderNames.ID_TITLE_INCLUDE));
		border.setTitle(getText(BorderNames.ID_TITLE_TEXT_FIELD));

		Object sel_item = getComboBox(BorderNames.ID_TITLE_JUSTIFICATION_COMBO).getSelectedItem();
		if (sel_item != null)
			sel_item = sel_item.toString();

		border.setJustification(BorderProperty.fromJustificationString((String) sel_item));

		sel_item = getComboBox(BorderNames.ID_TITLE_POSITION_COMBO).getSelectedItem();
		if (sel_item != null)
			sel_item = sel_item.toString();

		border.setPosition(BorderProperty.fromPositionString((String) sel_item));

		border.setTextColorProperty(getColorProperty(BorderNames.ID_TITLE_COLOR_SELECTOR));
	}

}
