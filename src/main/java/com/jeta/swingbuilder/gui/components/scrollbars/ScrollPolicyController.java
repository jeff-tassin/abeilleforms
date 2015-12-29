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

package com.jeta.swingbuilder.gui.components.scrollbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.store.properties.BorderProperty;
import com.jeta.forms.store.properties.CompoundBorderProperty;
import com.jeta.forms.store.properties.ScrollBarsProperty;
import com.jeta.open.gui.framework.JETAController;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.border.CompoundBorderView;

public class ScrollPolicyController extends JETAController {
	private ScrollPolicyView m_view;

	public ScrollPolicyController(ScrollPolicyView view) {
		super(view);
		m_view = view;
		assignAction(ScrollPolicyNames.ID_BORDER_BTN, new BorderAction());
	}

	public class BorderAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			ScrollBarsProperty sprop = m_view.getScrollBarsProperty();
			BorderProperty border_prop = sprop.getBorderProperty();
			if (!(border_prop instanceof CompoundBorderProperty)) {
				if (border_prop == null)
					border_prop = new CompoundBorderProperty();
				else {
					CompoundBorderProperty cprop = new CompoundBorderProperty();
					cprop.setValue(border_prop);
					border_prop = cprop;
				}
			}

			CompoundBorderView view = new CompoundBorderView((CompoundBorderProperty) border_prop);
			JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, m_view, true);
			dlg.setPrimaryPanel(view);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setTitle(I18N.getLocalizedMessage("Edit Border"));
			dlg.showCenter();
			if (dlg.isOk()) {
				CompoundBorderProperty cborder = (CompoundBorderProperty) view.createBorderProperty();
				sprop.setBorderProperty(cborder);
				m_view.setText(ScrollPolicyNames.ID_BORDER_LABEL, cborder.toString());
			}
		}
	}
}
