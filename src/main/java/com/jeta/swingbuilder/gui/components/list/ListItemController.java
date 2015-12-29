/* 
 * Copyright 2004 JETA Software, Inc. All rights reserved.
 * JETA SOFTWARE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jeta.swingbuilder.gui.components.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.store.properties.IconProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.swingbuilder.gui.images.ImageUtils;

public class ListItemController extends JETAController {
	private ListItemView m_view;

	public ListItemController(ListItemView view) {
		super(view);
		m_view = view;

		assignAction(ListItemNames.ID_ICON_BTN, new SetIconAction());
	}

	public class SetIconAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String path = null;
			IconProperty iprop = m_view.getIconProperty();
			if (ImageUtils.chooseImageFile(m_view, iprop)) {
				m_view.setIconProperty(iprop);
			}
		}
	}
}
