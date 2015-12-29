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

package com.jeta.swingbuilder.gui.main;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.support.AbeilleForms;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.i18n.I18N;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * The about box view for the application.
 * 
 * @author Jeff Tassin
 */
public class AboutView extends JETAPanel {

	private FormPanel m_view;

	public AboutView() {
		setLayout(new BorderLayout());
		m_view = new FormPanel("com/jeta/swingbuilder/gui/main/aboutView.jfrm");
		add(m_view, BorderLayout.CENTER);
		createCreditsPanel();

		String licensee = I18N.getLocalizedMessage("Unlicensed");
		String license = I18N.getLocalizedMessage("Evaluation");
		String serialno = I18N.getLocalizedMessage("None");

		String version = null;
		if (FormDesignerUtils.isDebug()) {
			version = AbeilleForms.getVersion();
		}
		else {
			version = I18N.format("forms_version_2", AbeilleForms.getVersion(), String.valueOf(AbeilleForms.BUILD_NUMBER));
		}

		m_view.setText("version.label", "Version " + com.jeta.forms.support.AbeilleForms.getVersionEx());
	}

	public void createCreditsPanel() {
		JEditorPane editor = (JEditorPane) m_view.getComponentByName(AboutViewNames.ID_CREDITS);
		editor.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
		try {
			java.net.URL url = AboutView.class.getClassLoader().getResource("com/jeta/swingbuilder/resources/help/credits.htm");
			editor.setPage(url);
			editor.setEditable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
