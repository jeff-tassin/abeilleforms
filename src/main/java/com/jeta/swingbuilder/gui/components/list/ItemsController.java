/* 
 * Copyright 2004 JETA Software, Inc. All rights reserved.
 * JETA SOFTWARE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jeta.swingbuilder.gui.components.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JTable;

import com.jeta.forms.store.properties.ListItemProperty;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.utils.TableSupportHandler;

public class ItemsController extends TableSupportHandler {
	private ItemsView m_view;

	/** @link dependency */

	/* # ItemsViewNames lnkItemsViewNames; */

	public ItemsController(ItemsView view) {
		super(view, ItemsViewNames.ID_ITEMS_TABLE, ItemsViewNames.ID_DELETE_ITEM, ItemsViewNames.ID_MOVE_UP, ItemsViewNames.ID_MOVE_DOWN);
		m_view = view;
		assignAction(ItemsViewNames.ID_ADD_ITEM, new AddItemAction());
		assignAction(ItemsViewNames.ID_EDIT_ITEM, new EditItemAction());
	}

	public class AddItemAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			JTable table = m_view.getTable(ItemsViewNames.ID_ITEMS_TABLE);
			ItemsModel tmodel = (ItemsModel) table.getModel();
			ListItemView view = new ListItemView();
			JETADialog dlg = JETAToolbox.invokeDialog(view, m_view, I18N.getLocalizedMessage("Item Properties"), (JComponent) view
					.getComponentByName(ListItemNames.ID_ITEM_LABEL));
			if (dlg.isOk()) {
				ListItemProperty lip = new ListItemProperty(view.getLabel(), view.getIconProperty());
				tmodel.addRow(lip);
			}
		}
	}

	public class EditItemAction implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			ListItemProperty oldprop = m_view.getSelectedProperty();
			if (oldprop != null) {
				JTable table = m_view.getTable(ItemsViewNames.ID_ITEMS_TABLE);
				ItemsModel items_model = (ItemsModel) table.getModel();
				ListItemView view = new ListItemView(oldprop.getLabel(), oldprop.getIconProperty());
				JETADialog dlg = JETAToolbox.invokeDialog(view, m_view, I18N.getLocalizedMessage("Item Properties"), (JComponent) view
						.getComponentByName(ListItemNames.ID_ITEM_LABEL));
				if (dlg.isOk()) {
					ListItemProperty lip = new ListItemProperty(view.getLabel(), view.getIconProperty());
					items_model.set(items_model.indexOf(oldprop), lip);
				}
			}
		}
	}
}
