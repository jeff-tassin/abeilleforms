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

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;

import com.jeta.forms.store.properties.TabProperty;
import com.jeta.forms.store.properties.TabbedPaneProperties;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.JETATableModel;

/**
 * This class is the table model for designing tabs in a JTabbedPane
 * 
 * @author Jeff Tassin
 */
public class TabsModel extends JETATableModel {

	/** column definitions */
	static final int ICON_COLUMN = 0;
	static final int TITLE_COLUMN = 1;

	/**
	 * ctor.
	 */
	public TabsModel(TabbedPaneProperties props) {

		String[] values = { I18N.getLocalizedMessage("Icon"), I18N.getLocalizedMessage("Title") };

		setColumnNames(values);

		Class[] types = { Icon.class, String.class };
		setColumnTypes(types);
		reload(props);
	}

	/**
	 * Adds the given object to the model
	 */
	/*
	 * public void addRow( TabProperty tp ) { if ( m_data == null ) m_data = new
	 * ArrayList();
	 * 
	 * m_data.add( tp ); fireTableRowsInserted( m_data.size()-1, m_data.size()-1 ); }
	 */

	/**
	 * @return the column value at the given row
	 */
	public Object getValueAt(int row, int column) {
		/** "Icon", "Title", "Content", "Scrollable" */
		TabProperty tp = (TabProperty) getRow(row);
		if (column == ICON_COLUMN) {
			return tp.icon();
		}
		else if (column == TITLE_COLUMN) {
			return tp.getTitle();
		}
		else
			return "";
	}

	/**
	 * Reload the model
	 */
	public void reload(TabbedPaneProperties props) {
		removeAll();
		if (props != null) {
			Collection tabs = props.getTabs();
			Iterator iter = tabs.iterator();
			while (iter.hasNext()) {
				TabProperty tp = (TabProperty) iter.next();
				addRow(tp);
			}
		}
	}

	/**
	 * Modifies an existing tab property with a new property
	 */
	public void setTabProperty(TabProperty newProp, TabProperty oldProp) {
		int pos = indexOf(oldProp);
		if (pos >= 0) {
			set(pos, newProp);
		}
	}

	/**
	 * Sets the tab property at the given index.
	 */
	public void setTabProperty(int index, TabProperty prop) {
		set(index, prop);
	}
}
