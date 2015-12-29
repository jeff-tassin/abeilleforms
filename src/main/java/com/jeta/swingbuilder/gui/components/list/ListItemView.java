/* 
 * Copyright 2004 JETA Software, Inc. All rights reserved.
 * JETA SOFTWARE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jeta.swingbuilder.gui.components.list;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.IconProperty;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * View for editing list item properties.
 * 
 * @author Jeff Tassin
 */
public class ListItemView extends JETAPanel {
	private FormPanel m_view;

	private IconProperty m_icon_prop = new IconProperty();

	/**
	 * ctor
	 */
	public ListItemView() {
		this(null, null);
	}

	/**
	 * ctor
	 */
	public ListItemView(String label, IconProperty iconprop) {
		m_view = new FormPanel("com/jeta/swingbuilder/gui/components/list/listItemView.jfrm");
		setLayout(new BorderLayout());
		add(m_view, BorderLayout.CENTER);

		m_view.getButton(ListItemNames.ID_ICON_BTN).setPreferredSize(new Dimension(24, 16));
		setValues(label, iconprop);
		setController(new ListItemController(this));
	}

	public String getLabel() {
		return m_view.getText(ListItemNames.ID_ITEM_LABEL);
	}

	public IconProperty getIconProperty() {
		String icon_path = FormDesignerUtils.fastTrim(m_view.getText(ListItemNames.ID_ICON_PATH));
		if (icon_path.length() == 0)
			icon_path = null;

		m_icon_prop.setRelativePath(icon_path);
		return m_icon_prop;
	}

	void setIconProperty(IconProperty iprop) {
		m_icon_prop.setValue(iprop);

		String path = (iprop == null) ? "" : iprop.getRelativePath();

		m_view.setText(ListItemNames.ID_ICON_PATH, path);
	}

	/**
	 * Initializes the view based on the given item property
	 */
	public void setValues(String label, IconProperty icon_prop) {
		if (label != null)
			m_view.setText(ListItemNames.ID_ITEM_LABEL, label);

		setIconProperty(icon_prop);
	}
}
