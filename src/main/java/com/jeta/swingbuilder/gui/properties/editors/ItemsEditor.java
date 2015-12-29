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

package com.jeta.swingbuilder.gui.properties.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jeta.forms.store.properties.ItemsProperty;
import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.components.list.ItemsView;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;

/**
 * Editor for handling items for a combo box
 * 
 * @author Jeff Tassin
 */
public class ItemsEditor extends JETAPropertyEditor {

	/**
	 * The label used to display the items
	 */
	private JLabel m_items_label;

	/**
	 * The panel that contains the items label
	 */
	private JPanel m_panel;

	/**
	 * ctor
	 */
	public ItemsEditor() {
		m_items_label = new JLabel();

		Font font = javax.swing.UIManager.getFont("Table.font");
		m_items_label.setFont(font);
		FontMetrics fm = m_items_label.getFontMetrics(font);

		/**
		 * We use a text field to get the preferred size of this renderer
		 * instead of relying on the JLabel.
		 */
		final javax.swing.JTextField tf = new javax.swing.JTextField("foo");
		tf.setFont(font);
		m_panel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
			public Dimension getPreferredSize() {
				return tf.getPreferredSize();
			}
		};

		m_panel.add(m_items_label);
		m_panel.setOpaque(false);
	}

	/**
	 * @return the editor component
	 */
	public Component getCustomEditor() {
		return m_panel;
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog(Component comp) {
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, comp, true);
		Object value = getValue();

		Collection items = null;
		if (value instanceof ItemsProperty) {
			items = ((ItemsProperty) value).getItems();
		}

		ItemsView view = new ItemsView(items);
		dlg.setTitle(I18N.getLocalizedMessage("Items"));
		dlg.setPrimaryPanel(view);
		dlg.setSize(dlg.getPreferredSize());
		dlg.showCenter();
		if (dlg.isOk()) {
			setValue(new ItemsProperty(view.getItems()));
		}
	}

	/**
	 * @return true since we have a custom editor dialog for this type
	 */
	public boolean supportsCustomEditor() {
		return true;
	}

	/**
	 * Override setValue so we can nullify the descriptor for the font
	 */
	public void setValue(Object value) {
		super.setValue(value);

		if (value instanceof ItemsProperty) {
			ItemsProperty iprop = (ItemsProperty) value;

			Collection c = iprop.getItems();

			if (c != null && c.size() > 0) {
				StringBuffer buff = new StringBuffer();
				Iterator iter = c.iterator();
				if (iter.hasNext()) {
					buff.append(iter.next());
				}
				if (iter.hasNext()) {
					buff.append("...");
				}

				m_items_label.setText(buff.toString());
			}
			else {
				m_items_label.setText("");
			}

		}
		else {
			if (value != null) {
				m_items_label.setText(value.toString());
			}
			else {
				m_items_label.setText("");
			}
		}
	}

}
