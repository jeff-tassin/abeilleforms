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

package com.jeta.swingbuilder.gui.export;

import java.awt.BorderLayout;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * This view allows the user to configure how all the component names for a
 * given panel will be exported.
 * 
 * @author Jeff Tassin
 */
public class ExportNamesView extends JETAPanel {
	private FormPanel m_view;

	public static final String ID_PREFIX_FIELD = "export.names.prefix";
	public static final String ID_DECORATOR_FIELD = "export.names.decorator";
	public static final String ID_POSTFIX_FIELD = "export.names.postfix";
	public static final String ID_INCLUDE_LINKED_FORMS = "include.linked.forms";
	public static final String ID_INCLUDE_EMBEDDED_FORMS = "include.embedded.forms"; // javax.swing.JCheckBox
	public static final String ID_INCLUDE_LABELS = "include.labels"; // javax.swing.JCheckBox

	/**
	 * ctor
	 */
	public ExportNamesView() {
		m_view = new FormPanel("com/jeta/swingbuilder/gui/export/exportNames.frm");
		setLayout(new BorderLayout());
		add(m_view, BorderLayout.CENTER);
		setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		m_view.setText(ID_PREFIX_FIELD, TSUserPropertiesUtils.getString(ID_PREFIX_FIELD, "// Generation Start"));
		m_view.setText(ID_DECORATOR_FIELD, TSUserPropertiesUtils.getString(ID_DECORATOR_FIELD,
		"public static final String ID_$identifier = \"$name\";  //$type"));
		m_view.setText(ID_POSTFIX_FIELD, TSUserPropertiesUtils.getString(ID_POSTFIX_FIELD, "// Generation End"));
	}

	/**
	 * @return the decorator for value
	 */
	public String getDecorator() {
		return m_view.getText(ID_DECORATOR_FIELD);
	}
	
	/**
	 * @return the prefix for value
	 */
	public String getPrefix() {
		return m_view.getText(ID_PREFIX_FIELD);
	}
	
	/**
	 * @return the postfix for value
	 */
	public String getPostfix() {
		return m_view.getText(ID_POSTFIX_FIELD);
	}

	/**
	 * @return true if linked forms should be included.
	 */
	public boolean isIncludeLinkedForms() {
		return m_view.getBoolean(ID_INCLUDE_LINKED_FORMS);
	}

	/**
	 * @return true if embedded forms should be included.
	 */
	public boolean isIncludeEmbeddedForms() {
		return m_view.getBoolean(ID_INCLUDE_EMBEDDED_FORMS);
	}

	/**
	 * @return true if lables forms should be included.
	 */
	public boolean isIncludeLabels() {
		return m_view.getBoolean(ID_INCLUDE_LABELS);
	}

	/**
	 * Saves the view settings
	 */
	public void saveToModel() {
		TSUserPropertiesUtils.setString(ID_PREFIX_FIELD, m_view.getText(ID_PREFIX_FIELD));
		TSUserPropertiesUtils.setString(ID_DECORATOR_FIELD, m_view.getText(ID_DECORATOR_FIELD));
		TSUserPropertiesUtils.setString(ID_POSTFIX_FIELD, m_view.getText(ID_POSTFIX_FIELD));
	}
}
