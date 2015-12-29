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

package com.jeta.swingbuilder.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;

/**
 * The frame used to contain the form when we are testing
 * 
 * @author Jeff Tassin
 */
public class RunFrame extends JFrame {
	private FormPanel m_form;

	private JScrollPane m_scroll;

	/**
	 * ctor
	 */
	public RunFrame(FormPanel panel) {
		super("Form Preview");
		m_form = panel;
		m_scroll = new JScrollPane(panel);
		getContentPane().add(m_scroll);

		if (FormDesignerUtils.isTest()) {
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);

			JMenu menu = new JMenu("Test");
			menu.add(createMenuItem("Iterator", "std.iterator"));
			menu.add(createMenuItem("Nested Iterator", "nested.iterator"));
			menu.add(createMenuItem("Remove Named", "remove.named"));
			menu.add(createMenuItem("Iterator Remove Named", "iterator.remove.named"));
			menu.add(createMenuItem("Nested Iterator Remove Named", "iterator.remove.nested"));
			menu.add(createMenuItem("Remove All", "iterator.remove.all"));
			menu.add(createMenuItem("Nested Remove All", "iterator.remove.all.nested"));
			menuBar.add(menu);
		}
	}

	public void setForm(FormPanel panel) {
		if (m_scroll != null)
			getContentPane().remove(m_scroll);

		m_form = panel;
		m_scroll = new JScrollPane(panel);
		getContentPane().add(m_scroll);

		if (getExtendedState() != java.awt.Frame.NORMAL) {
			setExtendedState(java.awt.Frame.NORMAL);
		}

		repaint();
	}

	private JMenuItem createMenuItem(String menuName, String cmd) {
		JMenuItem item = new JMenuItem(menuName);
		item.setActionCommand(cmd);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				runUnitTest(evt.getActionCommand());
			}
		});
		return item;
	}

	private void runUnitTest(String actionCmd) {
		// com.jeta.swingbuilder.test.JETATestFactory.runTest(
		// "test.jeta.swingbuilder.gui.main.APIValidator", m_form, actionCmd );
	}

}
