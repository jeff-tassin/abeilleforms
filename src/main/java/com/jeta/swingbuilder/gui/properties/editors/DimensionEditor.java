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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.dimension.DimensionView;
import com.jeta.swingbuilder.gui.properties.JETAPropertyEditor;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Editor for handling Dimension types.
 * 
 * @author Jeff Tassin
 */
public class DimensionEditor extends JETAPropertyEditor {
	private JPanel m_panel;
	private JTextField m_width = new JTextField(4);
	private JTextField m_height = new JTextField(4);
	private boolean m_is_editable = false;

	public DimensionEditor() {
		m_panel = new JPanel();
		FormLayout layout = new FormLayout("pref,5px,pref,10px,pref,5px,pref,pref:grow", "pref");
		m_panel.setLayout(layout);
		CellConstraints cc = new CellConstraints();

		m_panel.add(new JLabel(I18N.getLocalizedMessage("width")), cc.xy(1, 1));
		m_panel.add(m_width, cc.xy(3, 1));
		m_panel.add(new JLabel(I18N.getLocalizedMessage("height")), cc.xy(5, 1));
		m_panel.add(m_height, cc.xy(7, 1));
		m_panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		m_width.setEnabled(false);
		m_height.setEnabled(false);
	}

	public Component getCustomEditor() {
		return m_panel;
	}

	public String getJavaInitializationString() {
		return "";
	}

	/**
	 * Invokes a dialog used to update the property
	 */
	public void invokePropertyDialog() {
		DimensionView view = new DimensionView((Dimension) getValue());
		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, null, true);
		dlg.setPrimaryPanel(view);
		dlg.setSize(dlg.getPreferredSize());
		dlg.setTitle(I18N.getLocalizedMessage("Set Preferred Size"));
		dlg.showCenter();
		if (dlg.isOk()) {
			setValue(view.getDimension());
		}
	}

	/**
	 * @return true if this editor supports custom editing inline in the
	 *         property table. Property types such as the Java primitives and
	 *         Strings support inline editing.
	 */
	public boolean supportsInlineEditing() {
		return true;
	}

	public boolean supportsCustomEditor() {
		return false;
	}

	public void setValue(Object value) {
		if (value instanceof Dimension) {
			super.setValue(value);
			m_is_editable = false;
			Dimension d = (Dimension) value;
			m_width.setText(String.valueOf(d.width));
			m_height.setText(String.valueOf(d.height));
		}
		/*
		 * else if ( value instanceof JETADimensionProperty ) { m_is_editable =
		 * true; JETADimensionProperty dp = (JETADimensionProperty)value;
		 * Dimension d = dp.getDimension(); super.setValue(d); m_width.setText(
		 * String.valueOf( d.width) ); m_height.setText(
		 * String.valueOf(d.height) ); }
		 */
	}

	public Object getValue() {
		return new Dimension(Integer.parseInt(m_width.getText()), Integer.parseInt(m_height.getText()));
	}

}
