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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.LineBorderProperty;

/**
 * View that is used to create and edit bevel borders
 * 
 * @author Jeff Tassin
 */
public class LineBorderView extends AbstractBorderView {

	/**
	 * ctor
	 */
	public LineBorderView() {
		this(null);
	}

	/**
	 * ctor
	 */
	public LineBorderView(LineBorderProperty bp) {
		super("com/jeta/swingbuilder/gui/border/lineBorder.frm");
		if (bp == null) {
			setBorderProperty(new LineBorderProperty());
		}
		else {
			setBorderProperty(bp);
		}
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JSpinner sp = (JSpinner) getView().getComponentByName(LineBorderNames.ID_LINE_THICKNESS);
		sp.setModel(new SpinnerNumberModel(1, 1, 100, 1));
	}

	/**
	 * Creates a border property based on the view inputs
	 */
	public BorderProperty createBorderProperty() {
		LineBorderProperty bp = new LineBorderProperty();
		bp.setLineThickness(getLineThickness());
		bp.setLineColorProperty(getColorProperty(LineBorderNames.ID_LINE_COLOR_SELECTOR));
		bp.setCurved(getView().getBoolean(LineBorderNames.ID_ROUNDED_BORDER));
		bp.setTopPainted(getBoolean(BorderNames.ID_BORDER_TOP));
		bp.setLeftPainted(getBoolean(BorderNames.ID_BORDER_LEFT));
		bp.setBottomPainted(getBoolean(BorderNames.ID_BORDER_BOTTOM));
		bp.setRightPainted(getBoolean(BorderNames.ID_BORDER_RIGHT));
		setTitle(bp);
		return bp;
	}

	/**
	 * @return a description for this view. Typically used for a title in a
	 *         dialog
	 */
	public String getDescription() {
		return "Line Border";
	}

	/**
	 * @return the line thickness
	 */
	public int getLineThickness() {
		JSpinner sp = (JSpinner) getView().getComponentByName(LineBorderNames.ID_LINE_THICKNESS);
		Integer ival = (Integer) sp.getValue();
		return ival.intValue();
	}

	/**
	 * Sets the line thickness in the view
	 */
	public void setLineThickness(int thickness) {
		JSpinner sp = (JSpinner) getView().getComponentByName(LineBorderNames.ID_LINE_THICKNESS);
		sp.setValue(new Integer(thickness));
	}

	/**
	 * Updates this view based on the given border settings
	 */
	public void setBorderProperty(BorderProperty border) {
		super.setBorderProperty(border);
		if (border instanceof LineBorderProperty) {
			LineBorderProperty bb = (LineBorderProperty) border;
			setColorProperty(LineBorderNames.ID_LINE_COLOR_SELECTOR, bb.getLineColorProperty());
			setLineThickness(bb.getLineThickness());
			getView().setSelected(LineBorderNames.ID_ROUNDED_BORDER, bb.isCurved());
			getView().setSelected(LineBorderNames.ID_SQUARE_BORDER, !bb.isCurved());

			setSelected(BorderNames.ID_BORDER_TOP, border.isTopPainted());
			setSelected(BorderNames.ID_BORDER_LEFT, border.isLeftPainted());
			setSelected(BorderNames.ID_BORDER_RIGHT, border.isRightPainted());
			setSelected(BorderNames.ID_BORDER_BOTTOM, border.isBottomPainted());
		}
		else {
			assert (false);
		}
	}
}
