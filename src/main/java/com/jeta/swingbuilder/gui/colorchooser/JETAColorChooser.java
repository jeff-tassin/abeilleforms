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

package com.jeta.swingbuilder.gui.colorchooser;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.jeta.open.gui.framework.JETADialog;
import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.open.gui.utils.JETAToolbox;
import com.jeta.open.i18n.I18N;

public class JETAColorChooser extends JETAPanel {
	/**
	 * The current look and feel
	 */
	private static LookAndFeel m_look_and_feel;

	private JColorChooser m_cc;
	private RecentSwatchPanel m_recent_panel;

	public JETAColorChooser() {
		m_look_and_feel = UIManager.getLookAndFeel();

		setLayout(new BorderLayout());
		m_cc = new JColorChooser();
		m_recent_panel = new RecentSwatchPanel();
		m_recent_panel.setController(new RecentSwatchController(m_recent_panel, this));
		add(m_cc, BorderLayout.CENTER);

		JPanel swatch_panel = new JPanel();
		swatch_panel.add(m_recent_panel);
		swatch_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(I18N.getLocalizedMessage("Recent Selections")));
		add(swatch_panel, BorderLayout.SOUTH);
	}

	public Color getColor() {
		return m_cc.getColor();
	}

	/**
	 * Invokes a ColorChooser dialog.
	 */
	public static Color invokeColorChooser(java.awt.Component parentComp, Color selectedColor) {
		JETAColorChooser color_chooser = new JETAColorChooser();

		if (selectedColor != null) {
			color_chooser.setColor(selectedColor);
		}

		JETADialog dlg = (JETADialog) JETAToolbox.createDialog(JETADialog.class, parentComp, true);
		dlg.setPrimaryPanel(color_chooser);
		dlg.setTitle(I18N.getLocalizedMessage("Color Chooser"));
		dlg.setSize(dlg.getPreferredSize());
		dlg.showCenter();
		if (dlg.isOk()) {
			Color c = color_chooser.getColor();
			color_chooser.m_recent_panel.setMostRecentColor(c);
			color_chooser.m_recent_panel.saveSettings();
			return c;
		}
		else
			return null;
	}

	private static boolean isLookAndFeelChanged() {
		LookAndFeel lf = UIManager.getLookAndFeel();
		if (m_look_and_feel != lf) {
			m_look_and_feel = lf;
			return true;
		}
		return false;
	}

	public void setColor(Color c) {
		m_cc.setColor(c);
	}

}
