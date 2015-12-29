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

package com.jeta.swingbuilder.gui.images;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import com.jeta.forms.store.properties.IconProperty;

/**
 * View for editing the properties of an image in a form. This is basically an
 * image properties view with a preview window.
 * 
 * @author Jeff Tassin
 */
public class ImageAssignmentView extends ImagePropertiesView {
	/**
	 * ctor
	 */
	public ImageAssignmentView(IconProperty iProp) {
		super("com/jeta/swingbuilder/gui/images/imageAssignment.frm", iProp);
		getView().getLabel(ImagePropertiesNames.ID_IMAGE_PREVIEW_LABEL).setText("");
		getView().getPanel(ImagePropertiesNames.ID_IMAGE_PREVIEW_PANEL).setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		setController(new ImagePropertiesController(this));

	}

	/**
	 * Sets the image for this view
	 */
	public void setImage(ImageIcon image) {
		super.setImage(image);
		getView().getLabel(ImagePropertiesNames.ID_IMAGE_PREVIEW_LABEL).setIcon(image);
	}
}
