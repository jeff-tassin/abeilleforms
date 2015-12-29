/* 
 * Copyright 2004 JETA Software, Inc. All rights reserved.
 * JETA SOFTWARE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jeta.swingbuilder.gui.components.list;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;

import com.jeta.forms.store.properties.ListItemProperty;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.JETATableModel;

/**
 * Table model for a list of items.
 * 
 * @author Jeff Tassin
 */
public class ItemsModel extends JETATableModel {

	public static final int ICON_COLUMN = 0;
	public static final int LABEL_COLUMN = 1;

	/**
	 * ctor
	 * 
	 * @param a
	 *            collection of ListItemProperty objects
	 */
	public ItemsModel(Collection items) {
		setColumnNames(new String[] { I18N.getLocalizedMessage("Icon"), I18N.getLocalizedMessage("Label") });
		setColumnTypes(new Class[] { Icon.class, String.class });
		if (items != null) {
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				if (obj instanceof ListItemProperty) {
					addRow((ListItemProperty) obj);
				}
				else if (obj != null) {
					addRow(new ListItemProperty(obj.toString(), null));
				}
			}
		}
	}

	public Object getValueAt(int row, int col) {
		ListItemProperty lip = (ListItemProperty) getRow(row);
		if (lip != null) {
			if (col == ICON_COLUMN) {
				return lip.icon();
			}
			else if (col == LABEL_COLUMN) {
				return lip.getLabel();
			}
			else
				return null;
		}
		else
			return null;
	}
}
