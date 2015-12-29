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

package com.jeta.swingbuilder.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jeta.open.gui.framework.JETAPanel;
import com.jeta.swingbuilder.gui.project.UserPreferencesNames;
import com.jeta.swingbuilder.gui.utils.FormDesignerUtils;
import com.jeta.swingbuilder.interfaces.userprops.TSUserPropertiesUtils;

/**
 * This class is used to display a exception. It provides a text area to display the exception information
 * 
 * @author Jeff Tassin
 */
public class TSErrorPanel extends JETAPanel {
	private JLabel m_error_label;

	/**
	 * ctor
	 */
	public TSErrorPanel() {
	}

	/**
	 * @return the preferred size for this panel
	 */
	public Dimension getPreferredSize() {
		return FormDesignerUtils.getWindowDimension(this, 210, 160);
	}

	/**
	 * Initializes the panel with the given data.
	 * 
	 * @param msg
	 *            a message to include with the exception msg
	 * @param e
	 *            the exception that was thrown. The error message of this exception will be displayed.
	 */
	public void initialize(String msg, Throwable e) {
		StringBuffer msgbuff = new StringBuffer();

		if (msg != null) {
			msgbuff.append(msg);
			msgbuff.append("\n");
		}

		String errormsg = e.getLocalizedMessage();
		if (errormsg == null || errormsg.length() == 0)
			errormsg = e.getMessage();
		if (errormsg != null) {
			int pos = errormsg.indexOf("Stack Trace");
			if (pos >= 0)
				errormsg = errormsg.substring(0, pos);
		}		

		if (msgbuff.length() > 0)
			msgbuff.append('\n');

		msgbuff.append(e.getClass().getName());
		msgbuff.append(": ");
		msgbuff.append(errormsg);
		
		if (TSUserPropertiesUtils.getBoolean(UserPreferencesNames.ID_SHOW_ERROR_STACK, false)) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			sw.getBuffer();
			msgbuff.append("StackTrace:\n");
			msgbuff.append(sw);
		}		
		
		initialize(msgbuff.toString());
	}

	/**
	 * Initializes the panel with the given data.
	 * 
	 * @param msg
	 *            a message to display
	 */
	public void initialize(String msg) {
		setLayout(new BorderLayout());

		JTextArea msgcomp = new JTextArea();
		msgcomp.setEditable(false);
		msgcomp.setLineWrap(true);
		msgcomp.setWrapStyleWord(true);
		msgcomp.setText(msg);
		JScrollPane msgpane = new JScrollPane(msgcomp);

		add(msgpane, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		try {
			msgcomp.setCaretPosition(0);
		} catch (Exception e) {

		}
	}

	/**
	 * Shows an error message 'title' with an error icon at top of panel
	 */
	public void showErrorIcon(String msg) {
		if (m_error_label == null) {
			m_error_label = new JLabel(msg);
			m_error_label.setIcon(javax.swing.UIManager.getIcon("OptionPane.errorIcon"));
			m_error_label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
			add(m_error_label, BorderLayout.NORTH);
		} else {
			m_error_label.setText(msg);
		}
	}

}
