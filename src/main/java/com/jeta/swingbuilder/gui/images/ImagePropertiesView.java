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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.logger.FormsLogger;
import com.jeta.forms.project.ProjectManager;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * View for editing the properties of an image in a form.
 * 
 * @author Jeff Tassin
 */
public class ImagePropertiesView extends JETAPanel {
	/**
	 * The form view
	 */
	private FormPanel m_view;

	private ImageIcon m_image;

	/**
	 * ctor
	 */
	public ImagePropertiesView(String formName, IconProperty iProp) {
		m_view = new FormPanel(formName);
		setLayout(new BorderLayout());
		add(m_view, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		m_view.getButton(ImagePropertiesNames.ID_FILE_BUTTON).setPreferredSize(new Dimension(24, 10));
		setIconProperty(iProp);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return m_view.getText(ImagePropertiesNames.ID_DESCRIPTION_FIELD);
	}

	/**
	 * @return the image for this view
	 */
	public ImageIcon getImage() {
		return m_image;
	}

	public IconProperty getIconProperty() {

		String relpath = FormDesignerUtils.fastTrim(getRelativePath());
		if (relpath.length() == 0)
			return null;

		IconProperty iprop = new IconProperty();
		iprop.setDescription(getDescription());
		iprop.setRelativePath(getRelativePath());
		return iprop;
	}

	String getRelativePath() {
		return m_view.getText(ImagePropertiesNames.ID_FILE_FIELD);
	}

	public FormPanel getView() {
		return m_view;
	}

	void setDescription(String desc) {
		m_view.setText(ImagePropertiesNames.ID_DESCRIPTION_FIELD, desc);
	}

	/**
	 * Sets the image for this view
	 */
	public void setImage(ImageIcon image) {
		m_image = image;
	}

	/**
	 * Initializes this view with the settings of the given icon property
	 */
	public void setIconProperty(IconProperty iProp) {
		if (iProp == null) {

		} else {
			setDescription(iProp.getDescription());
			setRelativePath(iProp.getRelativePath());
			ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
			setImage(pmgr.loadImage(iProp.getRelativePath()));
		}
	}

	void setRelativePath(String path) {
		m_view.setText(ImagePropertiesNames.ID_FILE_FIELD, path);
	}

}
