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

package com.jeta.swingbuilder.gui.components.tabpane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.store.properties.TabProperty;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.list.ListItemView;
import com.jeta.swingbuilder.gui.utils.TableSupportHandler;

public class TabDesignerController extends TableSupportHandler {
	private TabDesignerView m_view;

	/** @link dependency */

	/* # TabDesignerNames lnkTabDesignerNames; */

	public TabDesignerController(TabDesignerView view) {
		super(view, TabDesignerNames.ID_TABS_TABLE, TabDesignerNames.ID_DELETE_TAB, TabDesignerNames.ID_MOVE_UP, TabDesignerNames.ID_MOVE_DOWN);
		m_view = view;
		assignAction(TabDesignerNames.ID_ADD_TAB, new AddTabAction());
		assignAction(TabDesignerNames.ID_EDIT_TAB, new EditTabAction());
	}

	public class AddTabAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			ListItemView view = new ListItemView();
			JETADialog dlg = JETAToolbox.invokeDialog(view, m_view, I18N.getLocalizedMessage("Tab Properties"));
			if (dlg.isOk()) {
				TabProperty tp = new TabProperty();
				tp.setTitle(view.getLabel());
				tp.setIconProperty(view.getIconProperty());
				m_view.addTabProperty(tp);
			}
		}
	}

	public class EditTabAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TabProperty tp = m_view.getSelectedTabProperty();
			if (tp != null) {
				ListItemView view = new ListItemView(tp.getTitle(), tp.getIconProperty());
				JETADialog dlg = JETAToolbox.invokeDialog(view, m_view, I18N.getLocalizedMessage("Tab Properties"));
				if (dlg.isOk()) {
					TabProperty newtp = new TabProperty();
					newtp.setValue(tp);
					newtp.setTitle(view.getLabel());
					newtp.setIconProperty(view.getIconProperty());
					m_view.setTabProperty(newtp, tp);
				}
			}
		}
	}
}
