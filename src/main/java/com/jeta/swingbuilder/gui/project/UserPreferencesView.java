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

package com.jeta.swingbuilder.gui.project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.beans.factories.LabelBeanFactory;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.project.ProjectManager;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.components.FloatDocument;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * Displays the view for editing the current project settings
 * 
 * @author Jeff Tassin
 */
public class UserPreferencesView extends JETAPanel {
	/**
	 * The projectSettings.jfrm form
	 */
	private FormPanel m_view;

	/**
	 * ctor
	 */
	public UserPreferencesView() {
		initialize();
	}

	/**
	 * Initializes the view
	 */
	public void initialize() {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/project/envPreferences2.jfrm");
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		getTextField(UserPreferencesNames.ID_COL_STD_SEP_SIZE).setDocument(new FloatDocument(false));
		getTextField(UserPreferencesNames.ID_COL_LARGE_SEP_SIZE).setDocument(new FloatDocument(false));
		getTextField(UserPreferencesNames.ID_ROW_STD_SEP_SIZE).setDocument(new FloatDocument(false));
		getTextField(UserPreferencesNames.ID_ROW_LARGE_SEP_SIZE).setDocument(new FloatDocument(false));
		getButton(UserPreferencesNames.ID_CLEAR_CACHE).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ProjectManager pmgr = (ProjectManager) JETARegistry.lookup(ProjectManager.COMPONENT_ID);
				if (pmgr != null)
					pmgr.clearResourceCache();
			}
		});
		loadModel();
	}

	/**
	 * Loads the view with the current model settings
	 */
	private void loadModel() {
		setSeparatorSize(UserPreferencesNames.ID_COL_STD_SEP_SIZE, UserPreferencesNames.ID_COL_STD_SEP_UNITS);
		setSeparatorSize(UserPreferencesNames.ID_COL_LARGE_SEP_SIZE, UserPreferencesNames.ID_COL_LARGE_SEP_UNITS);
		setSeparatorSize(UserPreferencesNames.ID_ROW_STD_SEP_SIZE, UserPreferencesNames.ID_ROW_STD_SEP_UNITS);
		setSeparatorSize(UserPreferencesNames.ID_ROW_LARGE_SEP_SIZE, UserPreferencesNames.ID_ROW_LARGE_SEP_UNITS);

		setSelected(UserPreferencesNames.ID_CACHE_IMAGES, TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_CACHE_IMAGES, true));
		setSelected(UserPreferencesNames.ID_OPEN_LAST_PROJECT, TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_OPEN_LAST_PROJECT, true));
		setSelected(UserPreferencesNames.ID_STORE_AS_XML, TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_STORE_AS_XML, false));

		setSelected(UserPreferencesNames.ID_SHOW_RESIZE_HANDLES, TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_SHOW_RESIZE_HANDLES, true));
		setSelected(UserPreferencesNames.ID_SHOW_ERROR_STACK, TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_SHOW_ERROR_STACK, false));

		javax.swing.JLabel label = new javax.swing.JLabel();
		String halign = TSUserPropertiesUtils.getString(LabelBeanFactory.ID_DEFAULT_HORIZONTAL_ALIGNMENT, LabelBeanFactory.getHorizontalAlignmentString(label
				.getHorizontalAlignment()));

		setSelectedItem(UserPreferencesNames.ID_LABEL_H_ALIGN, halign);
		setSelectedItem(UserPreferencesNames.ID_DEFAULT_RESIZE_UNITS, TSUserPropertiesUtils.getString(UserPreferencesNames.ID_DEFAULT_RESIZE_UNITS, "PX"));
	}

	/**
	 * Saves the separator settings to the model.
	 */
	private void saveSeparatorSize(String sz_field, String units_field) {
		TSUserPropertiesUtils.setString(units_field, (String) getSelectedItem(units_field));
		TSUserPropertiesUtils.setString(sz_field, FormDesignerUtils.fastTrim(getText(sz_field)));
	}

	private void setSeparatorSize(String sz_field, String units_field) {
		String units = TSUserPropertiesUtils.getString(units_field, "DLU");
		String defsize = FormUtils.getReasonableSize(units);
		if ("DLU".equalsIgnoreCase(units)) {
			if (UserPreferencesNames.ID_COL_STD_SEP_SIZE.equals(sz_field))
				defsize = "4";
			else if (UserPreferencesNames.ID_COL_LARGE_SEP_SIZE.equals(sz_field))
				defsize = "8";
			else if (UserPreferencesNames.ID_ROW_STD_SEP_SIZE.equals(sz_field))
				defsize = "2";
			else if (UserPreferencesNames.ID_ROW_LARGE_SEP_SIZE.equals(sz_field))
				defsize = "4";

		}
		String sz = TSUserPropertiesUtils.getString(sz_field, defsize);

		setSelectedItem(units_field, units);
		setText(sz_field, sz);
	}

	/**
	 * Saves the settings in the view to the model
	 */
	public void saveToModel() {
		saveSeparatorSize(UserPreferencesNames.ID_COL_STD_SEP_SIZE, UserPreferencesNames.ID_COL_STD_SEP_UNITS);
		saveSeparatorSize(UserPreferencesNames.ID_COL_LARGE_SEP_SIZE, UserPreferencesNames.ID_COL_LARGE_SEP_UNITS);
		saveSeparatorSize(UserPreferencesNames.ID_ROW_STD_SEP_SIZE, UserPreferencesNames.ID_ROW_STD_SEP_UNITS);
		saveSeparatorSize(UserPreferencesNames.ID_ROW_LARGE_SEP_SIZE, UserPreferencesNames.ID_ROW_LARGE_SEP_UNITS);
		TSUserPropertiesUtils.setBoolean(UserPreferencesNames.ID_CACHE_IMAGES, isSelected(UserPreferencesNames.ID_CACHE_IMAGES));
		TSUserPropertiesUtils.setBoolean(UserPreferencesNames.ID_OPEN_LAST_PROJECT, isSelected(UserPreferencesNames.ID_OPEN_LAST_PROJECT));
		TSUserPropertiesUtils.setBoolean(UserPreferencesNames.ID_STORE_AS_XML, isSelected(UserPreferencesNames.ID_STORE_AS_XML));
		TSUserPropertiesUtils.setBoolean(UserPreferencesNames.ID_SHOW_RESIZE_HANDLES, isSelected(UserPreferencesNames.ID_SHOW_RESIZE_HANDLES));
		TSUserPropertiesUtils.setBoolean(UserPreferencesNames.ID_SHOW_ERROR_STACK, isSelected(UserPreferencesNames.ID_SHOW_ERROR_STACK));

		TSUserPropertiesUtils.setString(LabelBeanFactory.ID_DEFAULT_HORIZONTAL_ALIGNMENT, (String) m_view
				.getSelectedItem(UserPreferencesNames.ID_LABEL_H_ALIGN));
		TSUserPropertiesUtils.setString(UserPreferencesNames.ID_DEFAULT_RESIZE_UNITS, (String) getSelectedItem(UserPreferencesNames.ID_DEFAULT_RESIZE_UNITS));

	}
}
